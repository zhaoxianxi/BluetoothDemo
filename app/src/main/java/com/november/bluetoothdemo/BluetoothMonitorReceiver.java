package com.november.bluetoothdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * author November
 * time 2022/3/28 9:47
 * desc 蓝牙状态监测广播
 */
public class BluetoothMonitorReceiver extends BroadcastReceiver {

    private final String TAG = BluetoothMonitorReceiver.class.getSimpleName();

    private BluetoothMonitorCallBack callBack;

    public BluetoothMonitorReceiver(BluetoothMonitorCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (null != action) {
            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                    switch (blueState) {
                        case BluetoothAdapter.STATE_TURNING_ON:
                            Log.e(TAG, "蓝牙正在打开！");
                            callBack.onOpening();
                            break;
                        case BluetoothAdapter.STATE_ON:
                            Log.e(TAG, "蓝牙已打开！");
                            callBack.onOpened();
                            break;
                        case BluetoothAdapter.STATE_TURNING_OFF:
                            Log.e(TAG, "蓝牙正在关闭！");
                            callBack.onClosing();
                            break;
                        case BluetoothAdapter.STATE_OFF:
                            Log.e(TAG, "蓝牙已关闭！");
                            callBack.onClosed();
                            break;
                    }
                    break;
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    Log.e(TAG, "蓝牙设备已连接！");
                    callBack.onConnected();
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    Log.e(TAG, "蓝牙设备已断开！");
                    callBack.onDisconnected();
                    break;
            }
        }
    }
}