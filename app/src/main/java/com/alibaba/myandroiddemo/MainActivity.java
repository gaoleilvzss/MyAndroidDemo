package com.alibaba.myandroiddemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.alibaba.myandroiddemo.camera.AutoFitSurfaceView;
import com.alibaba.myandroiddemo.camera.CameraHelper;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final String TAG = "camera1";
    HandlerThread handlerThread = new HandlerThread("thread");
    Handler handler;
    AutoFitSurfaceView surfaceView;
    CameraHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_layout);
        surfaceView = findViewById(R.id.sfv);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        surfaceView.getHolder().addCallback(this);
        helper = new CameraHelper(this, surfaceView, this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        handler.post(()->helper.openCamera());
    }


    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.d(TAG, "onPreviewFrame: " + data.length);
    }
}