package com.alibaba.myandroiddemo.camera;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.Camera;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * create by 高 (｡◕‿◕｡) 磊
 * 2020/10/24
 * desc :
 */
public class CameraHelper implements Camera.PreviewCallback {
    Camera camera;
    Camera.Parameters parameters;
    private WeakReference<Activity> weakReference;
    private AutoFitSurfaceView surfaceView;
    private CameraCallBack listener;
    private Point currentSize = new Point();
    private boolean isTakeCamera = false;

    public CameraHelper(Activity activity, AutoFitSurfaceView surfaceView, CameraCallBack listener) {
        weakReference = new WeakReference<>(activity);
        this.surfaceView = surfaceView;
        this.listener = listener;
    }

    public void openCamera() {
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        parameters = camera.getParameters();
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        currentSize.x = supportedPreviewSizes.get(0).width;
        currentSize.y = supportedPreviewSizes.get(0).height;
        parameters.setPreviewSize(supportedPreviewSizes.get(0).width, supportedPreviewSizes.get(0).height);
        weakReference.get().runOnUiThread(() -> surfaceView.setAspectRadio(supportedPreviewSizes.get(0).width, supportedPreviewSizes.get(0).height));
        parameters.setPreviewFormat(ImageFormat.NV21);
        try {
            camera.setPreviewDisplay(surfaceView.getHolder());
        } catch (IOException e) {
            e.printStackTrace();
        }
        camera.setDisplayOrientation(90);
        camera.setParameters(parameters);
        camera.setPreviewCallback(this);
        camera.startPreview();
    }


    public void release() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
    }

    public void setCanTakePhoto(boolean isTakeCamera) {
        this.isTakeCamera = isTakeCamera;
    }


    public void stopCamera() {
        if (camera != null) {
            camera.stopPreview();
        }
    }

    public void reStartCamera() {
        if (camera != null) {
            camera.startPreview();
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //因为只需要width 和 height 可以用point 来代替size 看起来方便些
        listener.previewCallBack(data, isTakeCamera, currentSize);
    }
}
