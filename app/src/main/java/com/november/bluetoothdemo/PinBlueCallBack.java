package com.november.bluetoothdemo;

import android.bluetooth.BluetoothDevice;

/**
 * author November
 * time 2022/3/11 10:59
 * desc 广播接收接口
 */
public interface PinBlueCallBack {

    /**
     * 配对请求
     */
    void onBondRequest();

    /**
     * 取消配对
     *
     * @param device
     */
    void onBondFail(BluetoothDevice device);

    /**
     * 配对中
     *
     * @param device
     */
    void onBonding(BluetoothDevice device);

    /**
     * 配对成功
     *
     * @param device
     */
    void onBondSuccess(BluetoothDevice device);
}