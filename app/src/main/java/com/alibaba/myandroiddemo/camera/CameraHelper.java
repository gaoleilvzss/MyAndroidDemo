package com.alibaba.myandroiddemo.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.view.ActionMode;
import android.view.SurfaceView;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * create by 高 (｡◕‿◕｡) 磊
 * 2020/10/24
 * desc :
 */
public class CameraHelper {
    Camera camera;
    Camera.Parameters parameters;
    private WeakReference<Activity> weakReference;
    private AutoFitSurfaceView surfaceView;
    private Camera.PreviewCallback previewCallback;

    public CameraHelper(Activity activity, AutoFitSurfaceView surfaceView, Camera.PreviewCallback previewCallback) {
        weakReference = new WeakReference<>(activity);
        this.surfaceView = surfaceView;
        this.previewCallback = previewCallback;
    }

    public void openCamera() {
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        parameters = camera.getParameters();
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
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
        camera.setPreviewCallback(previewCallback);
        camera.startPreview();
    }
}
