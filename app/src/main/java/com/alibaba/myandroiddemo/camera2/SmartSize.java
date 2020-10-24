package com.alibaba.myandroiddemo.camera2;

import android.util.Size;

/**
 * create by 高 (｡◕‿◕｡) 磊
 * 2020/10/24
 * desc :
 */
public class SmartSize {
    public int longSize;
    public int shortSize;

    public SmartSize(int width, int height) {
        Size size = new Size(width, height);
        longSize = Math.max(size.getWidth(), size.getHeight());
        shortSize = Math.min(size.getWidth(), size.getHeight());
    }

    @Override
    public String toString() {
        return "SmartSize{" +
                longSize + "*" + shortSize +
                '}';
    }
    public Size getSize(){
        return new Size(shortSize,longSize);
    }

}
