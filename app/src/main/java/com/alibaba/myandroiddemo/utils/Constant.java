package com.alibaba.myandroiddemo.utils;

import android.os.Environment;

import java.io.File;

/**
 * create by 高 (｡◕‿◕｡) 磊
 * 2020/10/24
 * desc :
 */
public class Constant {
    public static final String BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

    //图片存放路径
    public static final String PHOTO_PATH = BASE_PATH + File.separator + "cameraPicture";

    //bytes文件存放路径
    public static final String BYTES_SAVE_PATH = BASE_PATH + File.separator + "bytes";

}
