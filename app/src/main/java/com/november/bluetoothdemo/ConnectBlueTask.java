package com.november.bluetoothdemo;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

/**
 * author November
 * time 2022/3/11 11:34
 * desc 连接线程
 */
public class ConnectBlueTask extends AsyncTask<BluetoothDevice, Integer, BluetoothSocket> {

    private static final String TAG = ConnectBlueTask.class.getSimpleName();

    private BluetoothDevice bluetoothDevice;

    private final ConnectBlueCallBack callBack;

    public ConnectBlueTask(ConnectBlueCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected BluetoothSocket doInBackground(BluetoothDevice... bluetoothDevices) {
        bluetoothDevice = bluetoothDevices[0];
        BluetoothSocket socket = null;
        try {
            Log.e(TAG, "开始连接！");
            socket = bluetoothDevice.createRfcommSocketToServiceRecord(BluetoothUtils.MY_UUID_SECURE);
            if (socket != null && !socket.isConnected()) {
                socket.connect();
            }
        } catch (IOException e) {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return socket;
    }

    @Override
    protected void onPreExecute() {
        Log.e(TAG, "开始连接！");
        if (callBack != null) {
            callBack.onStartConnect();
        }
    }

    @Override
    protected void onPostExecute(BluetoothSocket socket) {
        if (socket != null && socket.isConnected()) {
            Log.e(TAG, "连接成功！");
            if (callBack != null) {
                callBack.onConnectSuccess(bluetoothDevice, socket);
            }
        } else {
            Log.e(TAG, "连接失败！");
            if (callBack != null) {
                callBack.onConnectFail(bluetoothDevice, "连接失败");
            }
        }
    }
}