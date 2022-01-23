package com.example.android.downloader.simple.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.common.fragment.BaseFragment;
import com.example.android.downloader.DownloadService;
import com.example.android.downloader.callback.DownloadManager;
import com.example.android.downloader.simple.R;
import com.example.android.downloader.simple.adapter.DownloadAdapter;
import com.example.android.downloader.simple.event.DownloadStatusChanged;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class DownloadedFragment extends BaseFragment {

    private RecyclerView rv;
    private DownloadAdapter downloadAdapter;
    private DownloadManager downloadManager;

    public static DownloadedFragment newInstance() {

        Bundle args = new Bundle();

        DownloadedFragment fragment = new DownloadedFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected View getLayoutView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_descargado, null);
    }

    @Override
    protected void initView() {
        super.initView();
        rv = (RecyclerView) getView().findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(getActivity()));


    }

    @Override
    protected void initData() {
        super.initData();
        EventBus.getDefault().register(this);

        downloadManager = DownloadService
                .getDownloadManager(getActivity().getApplicationContext());

        downloadAdapter = new DownloadAdapter(getActivity());
        rv.setAdapter(downloadAdapter);

        //downloading info

        fetchData();
    }

    private void fetchData() {
        downloadAdapter.setData(downloadManager.findAllDownloaded());
    }

    @Subscribe
    public void onEventMainThread(DownloadStatusChanged event) {
        fetchData();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
