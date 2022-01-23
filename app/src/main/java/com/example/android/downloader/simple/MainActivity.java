package com.example.android.downloader.simple;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.android.downloader.simple.activity.DescargaManagerActivity;
import com.example.android.downloader.simple.activity.ListActivity;
import com.example.android.downloader.simple.activity.SimpleActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void downloadAFile(View view) {
        startActivity(new Intent(this, SimpleActivity.class));
    }

    public void useInList(View view) {
        startActivity(new Intent(this, ListActivity.class));
    }

    public void downloadManager(View view) {
        startActivity(new Intent(this, DescargaManagerActivity.class));
    }
}