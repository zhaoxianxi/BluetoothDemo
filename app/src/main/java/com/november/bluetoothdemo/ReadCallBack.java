package com.november.bluetoothdemo;

/**
 * author November
 * time 2022/3/11 16:10
 * desc 读取数据接口
 */
public interface ReadCallBack {

    /** 开始读取 */
    default void onStarted() {
    }

    /**
     * 读取结果
     *
     * @param isSuccess
     * @param content
     */
    void onFinished(boolean isSuccess, String content);

}