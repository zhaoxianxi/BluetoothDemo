package com.november.bluetoothdemo;

/**
 * author November
 * time 2022/3/11 16:38
 * desc 写入数据接口
 */
public interface WriteCallBack {

    /** 开始写入 */
    void onStarted();

    /**
     * 写入结果
     *
     * @param isSuccess
     * @param hint
     */
    void onFinished(boolean isSuccess, String hint);

}