package com.alibaba.myandroiddemo.utils;

/**
 * create by 高 (｡◕‿◕｡) 磊
 * 2020/10/26
 * desc :
 */
public abstract class Task implements Runnable {

    public Task() {
    }

    @Override
    public final void run() {
        execute();
    }

    protected abstract void execute();
}
