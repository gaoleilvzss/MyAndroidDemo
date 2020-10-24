package com.alibaba.myandroiddemo.camera;

import android.graphics.Point;

/**
 * create by 高 (｡◕‿◕｡) 磊
 * 2020/10/24
 * desc :
 */
public interface CameraCallBack {
    void previewCallBack(byte[] bytes, boolean isCanTake, Point currentSize);
}
