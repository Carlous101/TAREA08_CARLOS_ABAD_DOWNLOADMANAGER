package com.example.android.downloader.simple.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.common.activity.BaseActivity;
import com.example.android.common.adapter.BaseRecyclerViewAdapter.OnItemClickListener;
import com.example.android.downloader.simple.R;
import com.example.android.downloader.simple.adapter.DownloadListAdapter;
import com.example.android.downloader.simple.domain.MyBusinessInfo;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends BaseActivity implements OnItemClickListener {

    private static final int REQUEST_DOWNLOAD_DETAIL_PAGE = 100;

    private RecyclerView rv;
    private DownloadListAdapter downloadListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);


    }

    @Override
    public void initListener() {
        downloadListAdapter.setOnItemClickListener(this);
    }

    @Override
    public void initData() {
        downloadListAdapter = new DownloadListAdapter(this);

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(downloadListAdapter);

        downloadListAdapter.setData(getDownloadListData());
    }

    private List<MyBusinessInfo> getDownloadListData() {
        ArrayList<MyBusinessInfo> myBusinessInfos = new ArrayList<>();
        myBusinessInfos.add(new MyBusinessInfo("Introducción MD y AA",
                "https://i.ibb.co/Vt9q0tS/1.jpg",
                "http://eventos.citius.usc.es/bigdata/workshops/hadoop-taller.pdf"));

        myBusinessInfos.add(new MyBusinessInfo("CARLOS ABAD - Trabajo Independiente",
                "https://i.ibb.co/gPSq3bT/4.png",
                "https://www.uma.es/media/tinyimages/file/android_ed2.pdf"));

        myBusinessInfos.add(new MyBusinessInfo("Presentación - AWS",
                "https://i.ibb.co/gPSq3bT/4.png",
                "https://d1.awsstatic.com/legal/awsserviceterms/AWS_Service_Terms_2021-07-09_Spanish.pdf"));

        myBusinessInfos.add(new MyBusinessInfo("SGA - Presentación - Gestión de aula - Semana 6 Detalles",
                "https://i.ibb.co/GMvNTVJ/2.jpg",
                "https://drive.google.com/file/d/1zmFZy88YC2RJ41gkzMRii0uMm6miEfAe/view?usp=sharing"));

        return myBusinessInfos;
    }

    @Override
    public void initView() {
        rv = findViewById(R.id.rv);
    }

    @Override
    public void onItemClick(int position) {
        MyBusinessInfo data = downloadListAdapter.getData(position);
        Intent intent = new Intent(this, DescargaDetailActivity.class);
        intent.putExtra(DescargaDetailActivity.DATA, data);
        startActivityForResult(intent, REQUEST_DOWNLOAD_DETAIL_PAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        downloadListAdapter.notifyDataSetChanged();
    }
}
