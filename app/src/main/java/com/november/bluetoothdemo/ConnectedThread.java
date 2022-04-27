package com.november.bluetoothdemo;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * author November
 * time 2022/3/28 10:06
 * desc 连接线程操作输入与输出
 */
public class ConnectedThread extends Thread {

    private final BluetoothSocket mBluetoothSocket;
    private final InputStream mInputStream;
    private final OutputStream mOutputStream;
    private ConnectedOperationCallBack mOperationCallBack;
    private boolean isRead = true;

    public ConnectedThread(BluetoothSocket bluetoothSocket, ConnectedOperationCallBack callBack) {
        this.mBluetoothSocket = bluetoothSocket;
        this.mOperationCallBack = callBack;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            tmpIn = mBluetoothSocket.getInputStream();
            tmpOut = mBluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mInputStream = tmpIn;
        mOutputStream = tmpOut;
    }

    public void run() {
        byte[] buffer = new byte[1024];
        int bytes = 0;

        //监听输入流以备获取数据
        while (isRead) {
            try {
                bytes = mInputStream.read(buffer);
                if (bytes != -1) {
                    String string = new String(buffer, 0, bytes, "utf-8");
                    if (null != mOperationCallBack) {
                        mOperationCallBack.onReadSuccess(string.substring(0, 7));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                if (null != mOperationCallBack) {
                    mOperationCallBack.onReadFile();
                }
            }
            try {
                //线程睡眠20ms以避免过于频繁工作  50ms->20ms 2017.12.2
                //导致UI处理发回的数据不及时而阻塞
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 写入
     *
     * @param buffer
     */
    public void write(byte[] buffer) {
        try {
            mOutputStream.write(buffer);
            if (null != mOperationCallBack) {
                mOperationCallBack.onWriteSuccess();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (null != mOperationCallBack) {
                mOperationCallBack.onWriteFile();
            }
        }
    }

    /**
     * 取消连接
     */
    public void cancel() {
        isRead = false;
        try {
            mInputStream.close();
            mOutputStream.close();
            mBluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}