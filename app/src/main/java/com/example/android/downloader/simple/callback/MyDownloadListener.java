package com.example.android.downloader.simple.callback;

import com.example.android.downloader.callback.AbsDownloadListener;
import com.example.android.downloader.exception.DownloadException;

import java.lang.ref.SoftReference;

public abstract class MyDownloadListener extends AbsDownloadListener {

    public MyDownloadListener() {
        super();
    }

    public MyDownloadListener(SoftReference<Object> userTag) {
        super(userTag);
    }

    @Override
    public void onStart() {
        onRefresh();
    }

    public abstract void onRefresh();

    @Override
    public void onWaited() {
        onRefresh();
    }

    @Override
    public void onDownloading(long progress, long size) {
        onRefresh();
    }

    @Override
    public void onRemoved() {
        onRefresh();
    }

    @Override
    public void onDownloadSuccess() {
        onRefresh();
    }

    @Override
    public void onDownloadFailed(DownloadException e) {
        onRefresh();
    }

    @Override
    public void onPaused() {
        onRefresh();
    }
}
