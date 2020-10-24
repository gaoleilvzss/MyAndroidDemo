package com.alibaba.module_pagging.utils;

import com.alibaba.lib_network.ApiNetWork;

/**
 * create by 高 (｡◕‿◕｡) 磊
 * 2020/10/24
 * desc :
 */
public class PagingNetApi extends ApiNetWork {
    private volatile static PagingNetApi instance;

    public static PagingNetApi getInstance() {
        if (instance == null) {
            synchronized (PagingNetApi.class) {
                if (instance == null) {
                    instance = new PagingNetApi();
                }
            }
        }
        return instance;
    }

    public PagingNetApi() {
        super();
    }

    @Override
    protected String getBaseUrl() {
        return "https://www.wanandroid.com/";
    }

    public <T> T create(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }


    


}
