package com.november.bluetoothdemo;

/**
 * author November
 * time 2022/3/29 16:05
 * desc 蓝牙状态检测接口回调
 */
public interface BluetoothMonitorCallBack {

    /** 蓝牙正在打开 */
    default void onOpening() {
    }

    /** 蓝牙已打开 */
    default void onOpened() {
    }

    /** 蓝牙正在关闭 */
    default void onClosing() {
    }

    /** 蓝牙已关闭 */
    default void onClosed() {
    }

    /** 设备已连接 */
    void onConnected();

    /** 设备已断开 */
    void onDisconnected();

}