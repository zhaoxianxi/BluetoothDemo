package com.november.bluetoothdemo;

import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;

/**
 * author November
 * time 2022/3/11 16:07
 * desc 读取线程
 */
public class ReadTask extends AsyncTask<String, Integer, String> {

    private static final String TAG = ReadTask.class.getSimpleName();

    private ReadCallBack callBack;

    private BluetoothSocket socket;

    public ReadTask(ReadCallBack callBack, BluetoothSocket socket) {
        this.callBack = callBack;
        this.socket = socket;
    }

    @Override
    protected String doInBackground(String... strings) {
        BufferedInputStream inputStream = null;
        try {
            StringBuilder stringBuffer = new StringBuilder();
            inputStream = new BufferedInputStream(socket.getInputStream());

            int length = 0;
            byte[] buf = new byte[1024];
            while ((length = inputStream.read()) != -1) {
                stringBuffer.append(new String(buf, 0, length));
            }
            return stringBuffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "读取失败！";
    }

    @Override
    protected void onPreExecute() {
        Log.e(TAG, "开始读取数据！");
        if (callBack != null) {
            callBack.onStarted();
        }
    }

    @Override
    protected void onPostExecute(String s) {
        Log.e(TAG, "完成读取数据！");
        if ("读取失败！".equals(s)) {
            callBack.onFinished(false, s);
        } else {
            callBack.onFinished(true, s);
        }
    }
}