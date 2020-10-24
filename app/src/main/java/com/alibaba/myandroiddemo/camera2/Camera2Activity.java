package com.alibaba.myandroiddemo.camera2;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.myandroiddemo.R;
import com.alibaba.myandroiddemo.camera.AutoFitSurfaceView;

/**
 * create by 高 (｡◕‿◕｡) 磊
 * 2020/10/24
 * desc : camera2 照相api
 */
public class Camera2Activity extends AppCompatActivity implements SurfaceHolder.Callback {
    AutoFitSurfaceView surfaceView;
    Camera2Helper camera2Helper;
    HandlerThread handlerThread = new HandlerThread("camera2Thread");
    Handler handler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_layout);
        surfaceView = findViewById(R.id.sfv);
        camera2Helper = new Camera2Helper();
        surfaceView.getHolder().addCallback(this);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        handler.post(()->camera2Helper.openCamera());
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }
}
