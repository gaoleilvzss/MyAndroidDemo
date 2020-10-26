package com.alibaba.myandroiddemo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.myandroiddemo.camera.CameraActivity;
import com.alibaba.myandroiddemo.camera2.Camera2Activity;
import com.alibaba.myandroiddemo.camera2.Camera2Helper;
import com.alibaba.myandroiddemo.utils.Constant;
import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.RequestCallback;

import java.io.File;
import java.util.List;

/**
 * create by 高 (｡◕‿◕｡) 磊
 * 2020/10/24
 * desc :
 */
public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PermissionX.init(this).permissions(Manifest.permission.CAMERA
                , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request(new RequestCallback() {
                    @Override
                    public void onResult(boolean allGranted, List<String> grantedList, List<String> deniedList) {
                        createDir();
                        startActivity(new Intent(SplashActivity.this, CameraActivity.class));
                    }
                });
    }

    private void createDir() {
        File file = new File(Constant.BYTES_SAVE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}
