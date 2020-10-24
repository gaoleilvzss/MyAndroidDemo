package com.alibaba.myandroiddemo.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

/**
 * create by 高 (｡◕‿◕｡) 磊
 * 2020/10/24
 * desc :
 */
public class AutoFitSurfaceView extends SurfaceView {
    float aspectRadio = 0f;

    public void setAspectRadio(int width, int height) {
        if (width < 0 || height < 0) return;
        aspectRadio = (float) width / height;
        getHolder().setFixedSize(width, height);
        requestLayout();
    }

    public AutoFitSurfaceView(Context context) {
        super(context);
    }

    public AutoFitSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoFitSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (aspectRadio == 0f) {
            setMeasuredDimension(width, height);
        } else {
            int newWidth;
            int newHeight;
            float actualRatio;
            if (width > height) actualRatio = aspectRadio;
            else actualRatio = 1f / aspectRadio;
            if (width < height * actualRatio) {
                newHeight = height;
                newWidth = (int) (height * actualRatio);
            } else {
                newWidth = width;
                newHeight = (int) (width / actualRatio);
            }
            setMeasuredDimension(newWidth, newHeight);
        }


    }
}
