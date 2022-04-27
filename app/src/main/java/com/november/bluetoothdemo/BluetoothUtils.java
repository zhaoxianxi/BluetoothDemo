package com.november.bluetoothdemo;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * author November
 * time 2022/3/10 16:04
 * desc 蓝牙工具类
 */
public class BluetoothUtils {

    private final String TAG = BluetoothUtils.class.getSimpleName();

    public static final UUID MY_UUID_SECURE =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private final BluetoothAdapter mBluetoothAdapter;

    public BluetoothUtils() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * 判断该设备是否支持蓝牙
     *
     * @return
     */
    public boolean isSupportBlue() {
        return mBluetoothAdapter != null;
    }

    /**
     * 蓝牙是否开启
     *
     * @return
     */
    public boolean isBlueEnable() {
        return isSupportBlue() && mBluetoothAdapter.isEnabled();
    }

    /**
     * 自动打开蓝牙（异步：蓝牙不会立刻就处于开启状态）
     * 这个方法打开蓝牙不会弹出提示
     */
    public void openBlueAsync() {
        if (isSupportBlue()) {
            mBluetoothAdapter.enable();
        }
    }

    /**
     * 自动打开蓝牙（同步）
     * 这个方法打开蓝牙会弹出提示
     * 需要在onActivityResult 方法中判断resultCode == RESULT_OK  true为成功
     */
    public void openBlueSync(Activity activity, int requestCode) {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 获取已经配对的蓝牙对象
     *
     * @return
     */
    public List<BluetoothDevice> getBondedDevices() {
        if (!isBlueEnable()) {
            return null;
        }
        return new ArrayList<>(mBluetoothAdapter.getBondedDevices());
    }

    /**
     * 扫描方法
     * 通过接收广播获取到扫描结果
     *
     * @return
     */
    public boolean scanBlue() {
        if (!isBlueEnable()) {
            Log.e(TAG, "Bluetooth not enable！");
            return false;
        }

        //当前是否在扫描，如果是就取消当前的扫描，重新扫描
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        //这个方法是异步操作，一般耗时12秒
        return mBluetoothAdapter.startDiscovery();
    }

    /**
     * 取消扫描蓝牙设备
     *
     * @return
     */
    public boolean cancelScanBlue() {
        if (isBlueEnable()) {
            return mBluetoothAdapter.cancelDiscovery();
        }
        return true;
    }

    /**
     * 配对（配对成功与失败通过广播返回）
     *
     * @param device
     */
    public void pin(BluetoothDevice device) {
        if (device == null) {
            Log.e(TAG, "bond device null！");
            return;
        }
        if (!isBlueEnable()) {
            Log.e(TAG, "Bluetooth not enable！");
            return;
        }
        //配对前先将扫描关闭
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        //判断设备是否配对，没有配对再配，配对了就不需做操作了
        if (device.getBondState() == BluetoothDevice.BOND_NONE) {
            Log.d(TAG, "attempts to bond:" + device.getName());
            try {
                Method createBondMethod = device.getClass().getMethod("createBond");
                Boolean returnValue = (Boolean) createBondMethod.invoke(device);
                returnValue.booleanValue();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "attempts to bond fail！");
            }
        }
    }

    /**
     * 取消配对（取消配对成功与失败通过广播返回 也就是配对失败）
     *
     * @param device
     */
    public void cancelPinBlue(BluetoothDevice device) {
        if (device == null) {
            Log.e(TAG, "cancel bond device null！");
            return;
        }
        if (!isBlueEnable()) {
            Log.e(TAG, "Bluetooth not enable！");
            return;
        }
        //判断设备是否配对，没有配对就不操作
        if (device.getBondState() != BluetoothDevice.BOND_NONE) {
            Log.e(TAG, "attempts to cancel bond:" + device.getName());
            try {
                Method removeBondMethod = device.getClass().getMethod("removeBond");
                Boolean returnValue = (Boolean) removeBondMethod.invoke(device);
                returnValue.booleanValue();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "attempts to cancel bond fail！");
            }
        }
    }

    /**
     * 连接（在配对之后调用）
     *
     * @param device
     * @param callBack
     */
    public void connect(BluetoothDevice device, ConnectBlueCallBack callBack) {
        if (device == null) {
            Log.e(TAG, "bond device null！");
            return;
        }
        if (!isBlueEnable()) {
            Log.e(TAG, "Bluetooth not enable！");
            return;
        }
        //连接之前把扫描关闭
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }
        new ConnectBlueTask(callBack).execute(device);
    }

    /**
     * 蓝牙是否连接
     *
     * @return
     */
    public boolean isConnectBlue(BluetoothSocket socket) {
        return socket != null && socket.isConnected();
    }

    /**
     * 断开连接
     *
     * @param socket
     * @return
     */
    public boolean cancelConnect(BluetoothSocket socket) {
        if (socket != null && socket.isConnected()) {
            try {
                socket.getInputStream().close();
                socket.getOutputStream().close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    /**
     * 输入mac地址进行自动连接
     * 前提是系统保存了该地址的对象
     *
     * @param address
     * @param callBack
     */
    public void connectMAC(String address, ConnectBlueCallBack callBack) {
        if (!isBlueEnable()) {
            return;
        }
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        connect(device, callBack);
    }

    /**
     * 根据MAC地址获取蓝牙对象
     *
     * @param address
     */
    public BluetoothDevice getBluetoothDevice(String address) {
        if (TextUtils.isEmpty(address)) {
            return null;
        }
        return mBluetoothAdapter.getRemoteDevice(address);
    }
}