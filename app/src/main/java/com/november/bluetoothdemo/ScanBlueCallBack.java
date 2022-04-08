package com.november.bluetoothdemo;

import android.bluetooth.BluetoothDevice;

/**
 * author November
 * time 2022/3/10 17:07
 * desc 蓝牙扫描结果接口
 */
public interface ScanBlueCallBack {

    /**
     * 开始扫描
     */
    void onScanStarted();

    /**
     * 结束扫描
     */
    void onScanFinished();

    /**
     * 发现设备
     *
     * @param bluetoothDevice
     */
    void onScanning(BluetoothDevice bluetoothDevice);
}