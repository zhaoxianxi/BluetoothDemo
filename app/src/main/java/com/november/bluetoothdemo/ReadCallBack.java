package com.november.bluetoothdemo;

/**
 * author November
 * time 2022/3/11 16:10
 * desc 读取数据接口
 */
public interface ReadCallBack {

    /** 开始读取 */
    void onStarted();

    /**
     * 读取结果
     *
     * @param isSuccess
     * @param hint
     */
    void onFinished(boolean isSuccess, String hint);

}