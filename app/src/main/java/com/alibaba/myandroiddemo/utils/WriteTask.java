package com.alibaba.myandroiddemo.utils;

/**
 * create by 高 (｡◕‿◕｡) 磊
 * 2020/10/26
 * desc :
 */
public class WriteTask extends Task{
    ExecutorUtils executorUtils;
    private byte[] bytes;
    private String fileName;

    public WriteTask(ExecutorUtils executorUtils,byte[] bytes,String fileName) {
        this.executorUtils = executorUtils;
        this.bytes = bytes;
        this.fileName = fileName;
    }
    @Override
    protected void execute() {
        try {
            FileUtils.getInstance().writeFile(fileName,bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executorUtils.finished(this);
        }
    }
}
