package com.november.bluetoothdemo;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * author November
 * time 2022/3/11 10:52
 * desc 配对广播接收类
 */
public class PinBlueReceiver extends BroadcastReceiver {

    /** 此处为要连接设备的初始秘钥，一般为1234或0000 */
    private String pin = "1234";

    private static final String TAG = PinBlueReceiver.class.getSimpleName();

    private PinBlueCallBack callBack;

    public PinBlueReceiver(PinBlueCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.e(TAG, "action:" + action);
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
            try {
                callBack.onBondRequest();
                //1.确认配对
                Method setPairingConfirmation = device.getClass().getDeclaredMethod("setPairingConfirmation", boolean.class);
                setPairingConfirmation.invoke(device, true);
                //2.终止有序广播
                abortBroadcast();//如果没有将广播终止，则会出现一个一闪而过的配对框。
                //3.调----用setPin方法进行配对...
                Method removeBondMethod = device.getClass().getDeclaredMethod("setPin", new Class[]{byte[].class});
                Boolean returnValue = (Boolean) removeBondMethod.invoke(device, new Object[]{pin.getBytes()});
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
            switch (device.getBondState()) {
                case BluetoothDevice.BOND_BONDING:
                    Log.d(TAG, "配对中！");
                    callBack.onBonding(device);
                    break;
                case BluetoothDevice.BOND_BONDED:
                    Log.d(TAG, "配对成功！");
                    callBack.onBondSuccess(device);
                    break;
                default:
                    Log.d(TAG, "配对失败！");
                    callBack.onBondFail(device);
                    break;
            }
        }
    }
}