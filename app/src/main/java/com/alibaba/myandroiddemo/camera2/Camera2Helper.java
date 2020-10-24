package com.alibaba.myandroiddemo.camera2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.alibaba.myandroiddemo.camera.AutoFitSurfaceView;

import java.lang.ref.WeakReference;
import java.util.Arrays;

/**
 * create by 高 (｡◕‿◕｡) 磊
 * 2020/10/24
 * desc :
 */
public class Camera2Helper {
    CameraManager cameraManager;
    CameraDevice cameraDevice;
    CameraCharacteristics cameraCharacteristics;
    ImageReader imageReader;
    CameraCaptureSession cameraCaptureSession;
    WeakReference<Activity> weakReference;
    private Handler handler;
    SmartSize SIZE_1080P = new SmartSize(1920, 1080);

    public Camera2Helper(Activity activity, Handler handler) {
        weakReference = new WeakReference<>(activity);
        this.handler = handler;
        try {
            cameraManager = (CameraManager) weakReference.get().getSystemService(Context.CAMERA_SERVICE);
            cameraCharacteristics = cameraManager.getCameraCharacteristics("1");
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }


    public void openCamera(AutoFitSurfaceView surfaceView) {
        Size previewOutputSize = getPreviewOutputSize(
                surfaceView.getDisplay(), cameraCharacteristics, SurfaceHolder.class, null);
        surfaceView.setAspectRadio(previewOutputSize.getWidth(), previewOutputSize.getHeight());
        initializeCamera();
    }

    private void initializeCamera() {
       openCamera2(cameraManager, "1", handler);
    }

    @SuppressLint("MissingPermission")
    private void openCamera2(CameraManager cameraManager, String cameraId, Handler handler) {
        try {
            cameraManager.openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    weakReference.get().finish();
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {

                }
            }, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private <T> Size getPreviewOutputSize(Display display, CameraCharacteristics cameraCharacteristics, Class<T> targetClass, Integer format) {
        SmartSize screenSize = getDisplaySmartSize(display);
        boolean hdScreen = screenSize.longSize >= SIZE_1080P.longSize || screenSize.shortSize >= SIZE_1080P.shortSize;
        SmartSize maxSize;
        if (hdScreen) maxSize = SIZE_1080P;
        else maxSize = screenSize;
        StreamConfigurationMap config = cameraCharacteristics.get(
                CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Size[] allSizes;
        if (format == null) {
            allSizes = config.getOutputSizes(targetClass);
        } else
            allSizes = config.getOutputSizes(format);
        SmartSize x = new SmartSize(0, 0);
        for (Size allSize : allSizes) {
            if (allSize.getWidth() * allSize.getWidth() > x.longSize * x.shortSize) {
                x.shortSize = allSize.getHeight();
                x.longSize = allSize.getWidth();
            }
        }
        if (x.longSize < maxSize.longSize && x.shortSize < maxSize.shortSize) {
            return maxSize.getSize();
        } else {
            return x.getSize();
        }
    }

    private SmartSize getDisplaySmartSize(Display display) {
        Point outPoint = new Point();
        display.getRealSize(outPoint);
        return new SmartSize(outPoint.x, outPoint.y);
    }
}
