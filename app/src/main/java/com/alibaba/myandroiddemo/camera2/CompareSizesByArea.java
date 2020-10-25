package com.alibaba.myandroiddemo.camera2;

import android.util.Size;

import java.util.Comparator;

/**
 * create by 高 (｡◕‿◕｡) 磊
 * 2020/10/25
 * desc :
 */
public class CompareSizesByArea implements Comparator<Size> {

    @Override
    public int compare(Size size1, Size size2) {
        return java.lang.Long.signum(size1.getWidth() * size1.getHeight() - size2.getWidth() * size2.getHeight());
    }
}
