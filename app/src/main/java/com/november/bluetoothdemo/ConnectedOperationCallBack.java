package com.november.bluetoothdemo;

/**
 * author November
 * time 2022/3/28 10:20
 * desc 连接操作回调
 */
public interface ConnectedOperationCallBack {

    /**
     * 读取成功
     *
     * @param content 内容
     */
    default void onReadSuccess(String content) {
    }

    /**
     * 读取失败
     */
    default void onReadFile() {
    }

    /**
     * 写入成功
     */
    default void onWriteSuccess() {
    }

    /**
     * 写入失败
     */
    default void onWriteFile() {
    }

}