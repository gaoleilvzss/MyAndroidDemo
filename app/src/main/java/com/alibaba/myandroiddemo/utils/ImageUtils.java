package com.alibaba.myandroiddemo.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * create by 高 (｡◕‿◕｡) 磊
 * 2020/10/24
 * desc : 对bytes 或者 bitmap进行处理
 */
public class ImageUtils {
    /**
     * 前置摄像头有旋转90的和镜面反转的问题。对byte数据进行处理
     *
     * @param data
     * @param imageWidth
     * @param imageHeight
     * @return
     */
    public static byte[] rotateYUVDegree270AndMirror(byte[] data, int imageWidth, int imageHeight) {
        try {
            byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
            int i = 0;
            int maxY = 0;
            for (int x = imageWidth - 1; x >= 0; x--) {
                maxY = imageWidth * (imageHeight - 1) + x * 2;
                for (int y = 0; y < imageHeight; y++) {
                    yuv[i] = data[maxY - (y * imageWidth + x)];
                    i++;
                }
            }
            int uvSize = imageWidth * imageHeight;
            i = uvSize;
            int maxUV = 0;
            for (int x = imageWidth - 1; x > 0; x = x - 2) {
                maxUV = imageWidth * (imageHeight / 2 - 1) + x * 2 + uvSize;
                for (int y = 0; y < imageHeight / 2; y++) {
                    yuv[i] = data[maxUV - 2 - (y * imageWidth + x - 1)];
                    i++;
                    yuv[i] = data[maxUV - (y * imageWidth + x)];
                    i++;
                }
            }
            return yuv;
        } catch (OutOfMemoryError e) {
            return data;
        }

    }

    public static String yuv2BitmapAndSave(byte[] data, int w, int h) {
        YuvImage image = new YuvImage(data, ImageFormat.NV21, w, h, null);
        File file = null;
        if (image != null) {
            try {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compressToJpeg(new Rect(0, 0, w, h), 100, stream);
                Bitmap bmp = BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.size());
                file = saveBitMap(bmp, "camera_" + System.currentTimeMillis());
                stream.close();
                return file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static File saveBitMap(Bitmap bitmap, String fileName) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            savePath = Constant.PHOTO_PATH;
            File saveFilePath = new File(savePath);
            if (!saveFilePath.exists()) {
                saveFilePath.mkdirs();
            }
        } else {
            return null;
        }
        try {
            filePic = new File(savePath, fileName + ".jpeg");
            if (filePic.exists()) {
                filePic.delete();
            }
            filePic.createNewFile();
            FileOutputStream fos = new FileOutputStream(filePic);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            bitmap.recycle();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return filePic;
    }

    /**
     * 将bitmap 转化为NV21的数据
     *
     * @param src
     * @param width
     * @param height
     * @return
     */
    public static byte[] bitmapToNv21(Bitmap src, int width, int height) {
        if (src != null && src.getWidth() >= width && src.getHeight() >= height) {
            int[] argb = new int[width * height];
            src.getPixels(argb, 0, width, 0, 0, width, height);
            return argbToNv21(argb, width, height);
        } else {
            return null;
        }
    }

    /**
     * ARGB数据转化为NV21数据
     *
     * @param argb   argb数据
     * @param width  宽度
     * @param height 高度
     * @return nv21数据
     */
    private static byte[] argbToNv21(int[] argb, int width, int height) {
        int frameSize = width * height;
        int yIndex = 0;
        int uvIndex = frameSize;
        int index = 0;
        byte[] nv21 = new byte[width * height * 3 / 2];
        for (int j = 0; j < height; ++j) {
            for (int i = 0; i < width; ++i) {
                int R = (argb[index] & 0xFF0000) >> 16;
                int G = (argb[index] & 0x00FF00) >> 8;
                int B = argb[index] & 0x0000FF;
                int Y = (66 * R + 129 * G + 25 * B + 128 >> 8) + 16;
                int U = (-38 * R - 74 * G + 112 * B + 128 >> 8) + 128;
                int V = (112 * R - 94 * G - 18 * B + 128 >> 8) + 128;
                nv21[yIndex++] = (byte) (Y < 0 ? 0 : (Y > 255 ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0 && uvIndex < nv21.length - 2) {
                    nv21[uvIndex++] = (byte) (V < 0 ? 0 : (V > 255 ? 255 : V));
                    nv21[uvIndex++] = (byte) (U < 0 ? 0 : (U > 255 ? 255 : U));
                }

                ++index;
            }
        }
        return nv21;
    }


}
