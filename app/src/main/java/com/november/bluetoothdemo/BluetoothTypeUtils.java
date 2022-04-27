package com.november.bluetoothdemo;

import android.bluetooth.BluetoothClass;

/**
 * author November
 * time 2022/4/25 14:44
 * desc 蓝牙类型工具类
 */
public class BluetoothTypeUtils {

    /**
     * 根据蓝牙设备类型返回相应的设备图标
     *
     * @param bluetoothClass
     * @return
     */
    public static int getDeviceType(BluetoothClass bluetoothClass) {
        if (null == bluetoothClass) {
            return R.mipmap.icon_bluetooth;
        }
        switch (bluetoothClass.getMajorDeviceClass()) {
            case BluetoothClass.Device.Major.PHONE:
                return R.mipmap.icon_phone;
            case BluetoothClass.Device.Major.COMPUTER:
                return R.mipmap.icon_computer;
            case BluetoothClass.Device.Major.PERIPHERAL:
                return R.mipmap.icon_printer;
            case BluetoothClass.Device.Major.AUDIO_VIDEO:
                return R.mipmap.icon_earphone;
            default:
                return R.mipmap.icon_bluetooth;
        }
    }
}