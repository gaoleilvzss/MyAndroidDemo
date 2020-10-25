package com.alibaba.myandroiddemo.camera2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.alibaba.myandroiddemo.camera.AutoFitSurfaceView;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * create by 高 (｡◕‿◕｡) 磊
 * 2020/10/24
 * desc :
 */
public class Camera2Helper implements SurfaceHolder.Callback {
    public static int PREVIEW_WIDTH = 720;       //预览的宽度
    public static int PREVIEW_HEIGHT = 1280;    //预览的高度
    public static int SAVE_WIDTH = 720;   //保存图片的宽度
    public static int SAVE_HEIGHT = 1280;  //保存图片的高度

    private CameraManager mCameraManager;
    private ImageReader mImageReader;
    private CameraDevice mCameraDevice;
    private CameraCaptureSession mCameraCaptureSession;

    private String mCameraId = "0";
    private CameraCharacteristics mCameraCharacteristics;

    private int mCameraSensorOrientation = 0;        //摄像头方向
    private int mCameraFacing = CameraCharacteristics.LENS_FACING_BACK;        //默认使用后置摄像头
    private int mDisplayRotation; //手机方向

    private boolean canTakePic = true;                 //是否可以拍照
    private boolean canExchangeCamera = false;           //是否可以切换摄像头

    private Handler mCameraHandler;
    private HandlerThread handlerThread = new HandlerThread("CameraThread");

    private Size mPreviewSize = new Size(PREVIEW_WIDTH, PREVIEW_HEIGHT); //预览大小
    private Size mSavePicSize = new Size(SAVE_WIDTH, SAVE_HEIGHT);      //保存图片大小

    private WeakReference<Activity> weakReference;
    private AutoFitSurfaceView autoFitSurfaceView;
    private final String TAG = "Camera2Helper";

    public Camera2Helper(Activity activity, AutoFitSurfaceView autoFitSurfaceView) {
        weakReference = new WeakReference<>(activity);
        this.autoFitSurfaceView = autoFitSurfaceView;
        handlerThread.start();
        mCameraHandler = new Handler(handlerThread.getLooper());
        mDisplayRotation = weakReference.get().getWindowManager().getDefaultDisplay().getRotation();
        autoFitSurfaceView.getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        initCameraInfo();
    }

    private void initCameraInfo() {
        mCameraManager = (CameraManager) weakReference.get().getSystemService(Context.CAMERA_SERVICE);
        String[] cameraIdList;
        try {
            cameraIdList = mCameraManager.getCameraIdList();
            if (cameraIdList.length == 0) {
                return;
            }

            for (String id : cameraIdList) {
                CameraCharacteristics cameraCharacteristics = mCameraManager.getCameraCharacteristics(id);
                int facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing == mCameraFacing) {
                    mCameraId = id;
                    mCameraCharacteristics = cameraCharacteristics;
                }
            }
            int supportLevel = mCameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
            if (supportLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY) {
                return;
            }
            //获取摄像头方向
            mCameraSensorOrientation = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            //获取StreamConfigurationMap，它是管理摄像头支持的所有输出格式和尺寸
            StreamConfigurationMap configurationMap = mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

            Size[] savePicSize = configurationMap.getOutputSizes(ImageFormat.JPEG);//保存照片尺寸
            Size[] previewSize = configurationMap.getOutputSizes(SurfaceView.class); //预览尺寸

            boolean exchange = exchangeWidthAndHeight(mDisplayRotation, mCameraSensorOrientation);
            mSavePicSize = getBestSize(exchange ? mSavePicSize.getHeight() : mSavePicSize.getWidth(),
                    exchange ? mSavePicSize.getWidth() : mSavePicSize.getHeight(),
                    exchange ? mSavePicSize.getHeight() : mSavePicSize.getWidth(),
                    exchange ? mSavePicSize.getWidth() : mSavePicSize.getHeight(),
                    savePicSize
            );

            mPreviewSize = getBestSize(
                    exchange ? mPreviewSize.getHeight() : mPreviewSize.getWidth(),
                    exchange ? mPreviewSize.getWidth() : mPreviewSize.getHeight(),
                    exchange ? autoFitSurfaceView.getHeight() : autoFitSurfaceView.getWidth(),
                    exchange ? autoFitSurfaceView.getWidth() : autoFitSurfaceView.getHeight(),
                    previewSize);

            autoFitSurfaceView.setAspectRadio(mPreviewSize.getWidth(), mPreviewSize.getHeight());
            Log.d(TAG, "预览最优尺寸 : " + mPreviewSize.getWidth() + "*" + mPreviewSize.getHeight() + ",scale = " + mPreviewSize.getWidth() / mPreviewSize.getHeight());
            Log.d(TAG, "保存图片最优尺寸: " + mSavePicSize.getWidth() + "*" + mSavePicSize.getHeight() + ",scale=" + mSavePicSize.getWidth() / mSavePicSize.getHeight());


            mImageReader = ImageReader.newInstance(mSavePicSize.getWidth(), mSavePicSize.getHeight(), ImageFormat.JPEG, 1);
            mImageReader.setOnImageAvailableListener(onImageAvailableListener, mCameraHandler);
            openCamera();


        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(weakReference.get(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "openCamera: 没有权限");
            return;
        }
        try {
            mCameraManager.openCamera(mCameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    mCameraDevice = camera;
                    createCaptureSession(camera);
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {

                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {

                }
            }, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void createCaptureSession(CameraDevice cameraDevice) {
        try {
            CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            Surface surface = autoFitSurfaceView.getHolder().getSurface();
            captureRequestBuilder.addTarget(surface);
            captureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);      // 闪光灯
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE); // 自动对焦

            // 为相机预览，创建一个CameraCaptureSession对象
            cameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()), new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(@NonNull CameraCaptureSession session) {
                    mCameraCaptureSession = session;
                    try {
                        session.setRepeatingRequest(captureRequestBuilder.build(), mCaptureCallBack, mCameraHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                }
            }, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拍照
     */
    public void takePic() {
        if (mCameraDevice == null || !canTakePic) return;
        try {
            CaptureRequest.Builder captureRequest = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureRequest.addTarget(mImageReader.getSurface());
            captureRequest.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);// 自动对焦
            captureRequest.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);     // 闪光灯
            captureRequest.set(CaptureRequest.JPEG_ORIENTATION, mCameraSensorOrientation);     //根据摄像头方向对保存的照片进行旋转，使其为"自然方向"
            mCameraCaptureSession.capture(captureRequest.build(), null, mCameraHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 切换摄像头
     */
    public void exchangeCamera() {
        if (mCameraDevice == null || !canExchangeCamera) return;

        if (mCameraFacing == CameraCharacteristics.LENS_FACING_FRONT)
            mCameraFacing = CameraCharacteristics.LENS_FACING_BACK;
        else
            mCameraFacing = CameraCharacteristics.LENS_FACING_FRONT;

        mPreviewSize = new Size(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        releaseCamera();
        initCameraInfo();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        releaseCamera();
    }

    public  void releaseCamera() {
        mCameraCaptureSession.close();
        mCameraCaptureSession = null;

        mCameraDevice.close();
        mCameraDevice = null;

        mImageReader.close();
        mImageReader = null;

        canExchangeCamera = false;
    }

    public void releaseThread() {
        handlerThread.quitSafely();
    }

    private boolean exchangeWidthAndHeight(int displayRotation, int sensorOrientation) {
        boolean exchange = false;
        if (displayRotation == Surface.ROTATION_0 || displayRotation == Surface.ROTATION_180) {
            if (sensorOrientation == 90 || sensorOrientation == 270) {
                exchange = true;
            }
        } else if (displayRotation == Surface.ROTATION_90 || displayRotation == Surface.ROTATION_270) {
            if (sensorOrientation == 0 || sensorOrientation == 180) {
                exchange = true;
            }
        } else {
            Log.d(TAG, "exchangeWidthAndHeight: display rotation is invalid");
        }

        Log.d(TAG, "屏幕方向" + displayRotation);
        Log.d(TAG, "相机方向" + sensorOrientation);
        return exchange;
    }

    private Size getBestSize(int targetWidth, int targetHeight, int maxWidth, int maxHeight, Size[] sizeList) {
        List<Size> bigEnough = new ArrayList<>();
        List<Size> notBigEnough = new ArrayList<>();
        for (Size size : sizeList) {
            if (size.getWidth() <= maxWidth && size.getHeight() <= maxHeight
                    && size.getWidth() == size.getHeight() * targetWidth / targetHeight) {
                if (size.getWidth() >= targetWidth && size.getHeight() >= targetHeight)
                    bigEnough.add(size);
                else
                    notBigEnough.add(size);
            }
            Log.d(TAG, "系统支持的尺寸: " + size.getWidth() + "*" + size.getHeight() + ",scale = " + size.getWidth() / size.getHeight());
        }

        Log.d(TAG, "最大尺寸:" + maxWidth + "*" + maxHeight + ", scale :" + maxWidth / maxHeight);
        Log.d(TAG, "目标尺寸:" + targetWidth + "*" + targetHeight + ",scale:" + targetWidth / targetHeight);

        Size needSize;
        if (bigEnough.size() > 0) {
            needSize = Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            needSize = Collections.min(notBigEnough, new CompareSizesByArea());
        } else {
            needSize = sizeList[0];
        }
        return needSize;
    }


    private final ImageReader.OnImageAvailableListener onImageAvailableListener = new ImageReader.OnImageAvailableListener() {
        @Override
        public void onImageAvailable(ImageReader reader) {
            Image image = reader.acquireNextImage();
            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
            byte[] data = new byte[buffer.remaining()];
            buffer.get(data);
            image.close();
        }
    };

    private CameraCaptureSession.CaptureCallback mCaptureCallBack = new CameraCaptureSession.CaptureCallback() {
        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
            super.onCaptureCompleted(session, request, result);
            canExchangeCamera = true;
            canTakePic = true;
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
        }
    };
}
