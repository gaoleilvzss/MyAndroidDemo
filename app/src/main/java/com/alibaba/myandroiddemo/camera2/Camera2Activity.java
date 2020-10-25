package com.alibaba.myandroiddemo.camera2;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageButton;

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
public class Camera2Activity extends AppCompatActivity {
    AutoFitSurfaceView surfaceView;
    Camera2Helper camera2Helper;
    ImageButton imageButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_layout);
        surfaceView = findViewById(R.id.sfv);
        imageButton = findViewById(R.id.capture_image);
        camera2Helper = new Camera2Helper(this, surfaceView);
        imageButton.setOnClickListener(v -> camera2Helper.takePic());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera2Helper.releaseCamera();
        camera2Helper.releaseThread();
    }
}
