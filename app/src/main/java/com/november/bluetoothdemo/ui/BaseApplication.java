package com.november.bluetoothdemo.ui;

import android.app.Application;
import android.bluetooth.BluetoothSocket;

/**
 * author November
 * time 2022/4/8 9:51
 * desc Application
 */
public class BaseApplication extends Application {

    private BluetoothSocket mBluetoothSocket;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public BluetoothSocket getBluetoothSocket() {
        return mBluetoothSocket;
    }

    public void setBluetoothSocket(BluetoothSocket bluetoothSocket) {
        this.mBluetoothSocket = bluetoothSocket;
    }
}