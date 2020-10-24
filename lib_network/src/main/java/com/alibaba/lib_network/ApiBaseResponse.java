package com.alibaba.lib_network;

/**
 * create by 高 (｡◕‿◕｡) 磊
 * 2020/10/24
 * desc :
 */
public class ApiBaseResponse<T> {
    public int errorCode;
    public String errorMsg;
    public T data;
}
