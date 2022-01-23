package com.example.android.downloader.simple.adapter;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.common.adapter.BaseRecyclerViewAdapter;
import com.example.android.downloader.DownloadService;
import com.example.android.downloader.callback.DownloadManager;
import com.example.android.downloader.domain.DownloadInfo;
import com.example.android.downloader.domain.DownloadInfo.Builder;
import com.example.android.downloader.simple.R;
import com.example.android.downloader.simple.callback.MyDownloadListener;
import com.example.android.downloader.simple.db.DBController;
import com.example.android.downloader.simple.domain.MyBusinessInfLocal;
import com.example.android.downloader.simple.domain.MyBusinessInfo;
import com.example.android.downloader.simple.util.FileUtil;

import java.io.File;
import java.lang.ref.SoftReference;
import java.sql.SQLException;

import static com.example.android.downloader.domain.DownloadInfo.STATUS_COMPLETED;
import static com.example.android.downloader.domain.DownloadInfo.STATUS_REMOVED;
import static com.example.android.downloader.domain.DownloadInfo.STATUS_WAIT;

import androidx.recyclerview.widget.RecyclerView;

public class DownloadListAdapter extends
        BaseRecyclerViewAdapter<MyBusinessInfo, DownloadListAdapter.ViewHolder> {

    private static final String TAG = "DownloadListAdapter";
    private final Context context;
    private final DownloadManager downloadManager;
    private DBController dbController;

    public DownloadListAdapter(Context context) {
        super(context);
        this.context = context;
        downloadManager = DownloadService.getDownloadManager(context.getApplicationContext());
        try {
            dbController = DBController.getInstance(context.getApplicationContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(
                R.layout.item_descarga_inform, parent, false));
    }

    @Override
    public void onBindViewHolder(DownloadListAdapter.ViewHolder holder, int position) {
        holder.bindData(getData(position), position, context);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(holder.getLayoutPosition());
            }
        });
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView iv_icon;
        private final TextView tv_size;
        private final TextView tv_status;
        private final ProgressBar pb;
        private final TextView tv_name;
        private final Button bt_action;
        private DownloadInfo downloadInfo;

        public ViewHolder(View view) {
            super(view);
            itemView.setClickable(true);
            iv_icon = view.findViewById(R.id.iv_icon);
            tv_size = view.findViewById(R.id.tv_size);
            tv_status = view.findViewById(R.id.tv_status);
            pb = view.findViewById(R.id.pb);
            tv_name = view.findViewById(R.id.tv_name);
            bt_action = view.findViewById(R.id.bt_action);
        }

        @SuppressWarnings("unchecked")
        public void bindData(final MyBusinessInfo data, int position, final Context context) {
            Glide.with(context).load(data.getIcon()).into(iv_icon);
            tv_name.setText(data.getName());

            // Get download task status
            downloadInfo = downloadManager.getDownloadById(data.getUrl());

            // Set a download listener
            if (downloadInfo != null) {
                downloadInfo
                        .setDownloadListener(new MyDownloadListener(new SoftReference(ViewHolder.this)) {
                            //  Call interval about one second
                            @Override
                            public void onRefresh() {
                                if (getUserTag() != null && getUserTag().get() != null) {
                                    ViewHolder viewHolder = (ViewHolder) getUserTag().get();
                                    viewHolder.refresh();
                                }
                            }
                        });

            }

            refresh();

            //      Download button
            bt_action.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (downloadInfo != null) {

                        switch (downloadInfo.getStatus()) {
                            case DownloadInfo.STATUS_NONE:
                            case DownloadInfo.STATUS_PAUSED:
                            case DownloadInfo.STATUS_ERROR:

                                //resume downloadInfo
                                downloadManager.resume(downloadInfo);
                                break;

                            case DownloadInfo.STATUS_DOWNLOADING:
                            case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
                            case STATUS_WAIT:
                                //pause downloadInfo
                                downloadManager.pause(downloadInfo);
                                break;
                            case DownloadInfo.STATUS_COMPLETED:
                                downloadManager.remove(downloadInfo);
                                break;
                        }
                    } else {
                        //            Create new download task
                        File d = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "d");
                        if (!d.exists()) {
                            d.mkdirs();
                        }
                        String path = d.getAbsolutePath().concat("/").concat(data.getName());
                        downloadInfo = new Builder().setUrl(data.getUrl())
                                .setPath(path)
                                .build();
                        downloadInfo
                                .setDownloadListener(new MyDownloadListener(new SoftReference(ViewHolder.this)) {

                                    @Override
                                    public void onRefresh() {
                                        notifyDownloadStatus();

                                        if (getUserTag() != null && getUserTag().get() != null) {
                                            ViewHolder viewHolder = (ViewHolder) getUserTag().get();
                                            viewHolder.refresh();
                                        }
                                    }
                                });
                        downloadManager.download(downloadInfo);

                        //save extra info to my database.
                        MyBusinessInfLocal myBusinessInfLocal = new MyBusinessInfLocal(
                                data.getUrl(), data.getName(), data.getIcon(), data.getUrl());
                        try {
                            dbController.createOrUpdateMyDownloadInfo(myBusinessInfLocal);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        }

        private void notifyDownloadStatus() {
            if (downloadInfo.getStatus() == STATUS_REMOVED) {
                try {
                    dbController.deleteMyDownloadInfo(downloadInfo.getUri());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        }

        private void refresh() {
            if (downloadInfo == null) {
                defaultStatusUI();
            } else {
                switch (downloadInfo.getStatus()) {
                    case DownloadInfo.STATUS_NONE:
                        bt_action.setText("Descargar");
                        tv_status.setText("sin descargar");
                        break;
                    case DownloadInfo.STATUS_PAUSED:
                    case DownloadInfo.STATUS_ERROR:
                        bt_action.setText("continuar");
                        tv_status.setText("en espera");
                        try {
                            pb.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        tv_size.setText(FileUtil.formatFileSize(downloadInfo.getProgress()) + "/" + FileUtil
                                .formatFileSize(downloadInfo.getSize()));
                        break;

                    case DownloadInfo.STATUS_DOWNLOADING:
                    case DownloadInfo.STATUS_PREPARE_DOWNLOAD:
                        bt_action.setText("pausar");
                        try {
                            pb.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        tv_size.setText(FileUtil.formatFileSize(downloadInfo.getProgress()) + "/" + FileUtil
                                .formatFileSize(downloadInfo.getSize()));
                        tv_status.setText("descargando");
                        break;
                    case STATUS_COMPLETED:
                        bt_action.setText("eliminar");
                        try {
                            pb.setProgress((int) (downloadInfo.getProgress() * 100.0 / downloadInfo.getSize()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        tv_size.setText(FileUtil.formatFileSize(downloadInfo.getProgress()) + "/" + FileUtil
                                .formatFileSize(downloadInfo.getSize()));
                        tv_status.setText("descarga completa");
                        break;
                    case STATUS_REMOVED:
                        defaultStatusUI();
                        break;
                    case STATUS_WAIT:
                        tv_size.setText("");
                        pb.setProgress(0);
                        bt_action.setText("pausar");
                        tv_status.setText("en espera");
                        break;
                }

            }
        }

        private void defaultStatusUI() {
            tv_size.setText("");
            pb.setProgress(0);
            bt_action.setText("Descargar");
            tv_status.setText("sin descargar");

        }
    }
}
