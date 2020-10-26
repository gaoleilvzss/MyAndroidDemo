package com.alibaba.myandroiddemo.camera;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.myandroiddemo.R;
import com.alibaba.myandroiddemo.utils.Constant;
import com.alibaba.myandroiddemo.utils.ExecutorUtils;
import com.alibaba.myandroiddemo.utils.FileUtils;
import com.alibaba.myandroiddemo.utils.ImageUtils;
import com.alibaba.myandroiddemo.utils.WriteTask;

import java.io.File;
import java.util.Arrays;

public class CameraActivity extends AppCompatActivity implements SurfaceHolder.Callback, CameraCallBack {
    private static final String TAG = "camera1";
    HandlerThread handlerThread = new HandlerThread("thread");
    Handler handler;
    AutoFitSurfaceView surfaceView;
    CameraHelper helper;
    ImageButton imageButton;
    ExecutorUtils executorUtils = new ExecutorUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_layout);
        surfaceView = findViewById(R.id.sfv);
        imageButton = findViewById(R.id.capture_image);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        surfaceView.getHolder().addCallback(this);
        helper = new CameraHelper(this, surfaceView, this);
        imageButton.setOnClickListener(v -> helper.setCanTakePhoto(true));
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        handler.post(() -> helper.openCamera());
    }


    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (helper != null) {
            helper.release();
        }
    }

    int a = 0;

    @Override
    public void previewCallBack(byte[] bytes, boolean isCanTake, Point currentSize) {
//        if (isCanTake) {
//            //进行拍照
//            helper.stopCamera();
//            byte[] data = ImageUtils.rotateYUVDegree270AndMirror(bytes, currentSize.x, currentSize.y);
//            String s = ImageUtils.yuv2BitmapAndSave(data, currentSize.y, currentSize.x);
//            helper.setCanTakePhoto(false);
//            runOnUiThread(() -> Toast.makeText(this, "保存路径" + s, Toast.LENGTH_SHORT).show());
//        } else {
        //可以进行帧处理等操作
        if (a == 0) {
            a++;
            byte_a = ImageUtils.rotateYUVDegree270AndMirror(bytes, currentSize.x, currentSize.y);
            executorUtils.putTask(new WriteTask(executorUtils, byte_a, Constant.BYTES_SAVE_PATH + File.separator + "mit_1.txt"));
            byte_b = FileUtils.getInstance().readFileToByteArray(Constant.BYTES_SAVE_PATH + File.separator + "mit_1.txt");
            Toast.makeText(this, Arrays.equals(byte_a, byte_b) ? "相同" : "不同", Toast.LENGTH_SHORT).show();
        }

//        }
    }

    byte[] byte_a;
    byte[] byte_b;
}