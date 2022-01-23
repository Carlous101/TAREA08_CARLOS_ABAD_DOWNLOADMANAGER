package com.example.android.downloader.core;

import com.example.android.downloader.domain.DownloadInfo;
import com.example.android.downloader.exception.DownloadException;



public interface DownloadResponse {

    void onStatusChanged(DownloadInfo downloadInfo);

    void handleException(DownloadInfo downloadInfo, DownloadException exception);
}
