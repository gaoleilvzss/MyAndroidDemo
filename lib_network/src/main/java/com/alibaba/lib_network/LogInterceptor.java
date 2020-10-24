package com.alibaba.lib_network;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * create by 高 (｡◕‿◕｡) 磊
 * 2020/10/24
 * desc :
 */
public class LogInterceptor implements Interceptor {
    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request request = chain.request();
        Log.e("LogInterceptor", "request:" + request);
        Log.e("LogInterceptor", "System.nanoTime():" + System.nanoTime());
        Response response = chain.proceed(request);
        Log.e("LogInterceptor", "request:" + request);
        Log.e("LogInterceptor", "System.nanoTime():" + System.nanoTime());
        return response;
    }
}
