package com.november.bluetoothdemo;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;

/**
 * author November
 * time 2022/3/11 16:35
 * desc 写入
 */
public class WriteTask extends AsyncTask<String, Integer, String> {

    private static final String TAG = WriteTask.class.getSimpleName();

    private WriteCallBack callBack;

    private BluetoothSocket socket;

    public WriteTask(WriteCallBack callBack, BluetoothSocket socket) {
        this.callBack = callBack;
        this.socket = socket;
    }

    @Override
    protected String doInBackground(String... strings) {
        String string = strings[0];
        OutputStream outputStream = null;
        try {
            outputStream = socket.getOutputStream();
            outputStream.write(string.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return "发送失败";
        } finally {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "发送成功";
    }

    @Override
    protected void onPreExecute() {
        Log.e(TAG, "开始写入数据！");
        if (callBack != null) {
            callBack.onStarted();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        Log.e(TAG, "完成写入数据！");
        if (callBack != null) {
            if ("发送成功".equals(s)) {
                callBack.onFinished(true, s);
            } else {
                callBack.onFinished(false, s);
            }
        }
    }
}