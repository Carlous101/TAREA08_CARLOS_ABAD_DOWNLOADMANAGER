package com.example.android.downloader.simple.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.android.common.adapter.CustomFragmentPagerAdapter;
import com.example.android.downloader.simple.fragment.DownloadedFragment;
import com.example.android.downloader.simple.fragment.DownloadingFragment;

public class DownloadManagerAdapter extends CustomFragmentPagerAdapter<String> {


    public DownloadManagerAdapter(FragmentManager fm, Context context) {
        super(fm, context);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return DownloadingFragment.newInstance();
        } else {
            return DownloadedFragment.newInstance();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return getData(position);
    }
}
