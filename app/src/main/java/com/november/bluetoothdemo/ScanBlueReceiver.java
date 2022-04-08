package com.november.bluetoothdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * author November
 * time 2022/3/10 17:04
 * desc 扫描广播接收类
 */
public class ScanBlueReceiver extends BroadcastReceiver {

    private static final String TAG = ScanBlueReceiver.class.getSimpleName();

    private ScanBlueCallBack callBack;

    public ScanBlueReceiver(ScanBlueCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "action:" + action);
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        switch (action) {
            case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                Log.d(TAG, "开始扫描...");
                callBack.onScanStarted();
                break;
            case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                Log.d(TAG, "结束扫描...");
                callBack.onScanFinished();
                break;
            case BluetoothDevice.ACTION_FOUND:
                Log.d(TAG, "发现设备...");
                callBack.onScanning(device);
                break;
        }
    }
}