package com.november.bluetoothdemo;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * author November
 * time 2022/3/11 13:44
 * desc 连接线程接口回调
 */
public interface ConnectBlueCallBack {

    /** 开始连接 */
    default void onStartConnect() {
    }

    /**
     * 连接成功
     *
     * @param device
     * @param socket
     */
    void onConnectSuccess(BluetoothDevice device, BluetoothSocket socket);

    /**
     * 连接失败
     *
     * @param device
     * @param hint
     */
    void onConnectFail(BluetoothDevice device, String hint);

}