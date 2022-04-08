package com.november.bluetoothdemo;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.permissionx.guolindev.PermissionX;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnIsSupport;
    private Button btnOpenAsync;
    private Button btnOpenSync;
    private Button btnIsEnable;
    private Button btnSearch;
    private ImageView ivRefresh;
    private RecyclerView rvList;

    /** 蓝牙工具类 */
    private BluetoothUtils mBluetoothUtils;
    /** 扫描蓝牙设备广播 */
    private ScanBlueReceiver mScanBlueReceiver;
    /** 蓝牙配对广播 */
    private PinBlueReceiver mPinBlueReceiver;
    /** 属性动画 */
    private ObjectAnimator mAnimator;

    private BaseQuickAdapter mAdapter;
    private List<BluetoothDevice> mList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {

        btnIsSupport = (Button) findViewById(R.id.btn_is_support);
        btnOpenAsync = (Button) findViewById(R.id.btn_open_asyn);
        btnOpenSync = (Button) findViewById(R.id.btn_open_sync);
        btnIsEnable = (Button) findViewById(R.id.btn_is_enable);
        btnSearch = (Button) findViewById(R.id.btn_search);
        ivRefresh = (ImageView) findViewById(R.id.iv_refresh);
        rvList = (RecyclerView) findViewById(R.id.rv_list);
        btnIsSupport.setOnClickListener(this);
        btnOpenAsync.setOnClickListener(this);
        btnOpenSync.setOnClickListener(this);
        btnIsEnable.setOnClickListener(this);
        btnSearch.setOnClickListener(this);

        mAnimator = rotation(ivRefresh);
        mBluetoothUtils = new BluetoothUtils();

        mScanBlueReceiver = new ScanBlueReceiver(new ScanBlueCallBack() {
            @Override
            public void onScanStarted() {
                Toast.makeText(MainActivity.this, "开始扫描！", Toast.LENGTH_SHORT).show();
                mAnimator.start();
                if (mList.size() > 0) {
                    mList.clear();
                }
            }

            @Override
            public void onScanFinished() {
                mAnimator.pause();
            }

            @Override
            public void onScanning(BluetoothDevice bluetoothDevice) {
                Log.e("BluetoothDevice", bluetoothDevice.getName() + "---" + bluetoothDevice.getAddress());
                if (TextUtils.isEmpty(bluetoothDevice.getName()) || TextUtils.isEmpty(bluetoothDevice.getAddress())) {
                    return;
                }
                for (BluetoothDevice device : mList) {
                    if (device.getAddress().equals(bluetoothDevice.getAddress())) {
                        return;
                    }
                }
                Log.e("DeviceType",bluetoothDevice.getName()+"---"+bluetoothDevice.getBluetoothClass().getDeviceClass());
                mList.add(bluetoothDevice);
                mAdapter.notifyDataSetChanged();
            }
        });
        mPinBlueReceiver = new PinBlueReceiver(new PinBlueCallBack() {
            @Override
            public void onBondRequest() {
                Toast.makeText(MainActivity.this, "开始配对！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBondFail(BluetoothDevice device) {
                Toast.makeText(MainActivity.this, "取消配对！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBonding(BluetoothDevice device) {
                Toast.makeText(MainActivity.this, "配对中...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBondSuccess(BluetoothDevice device) {
                Toast.makeText(MainActivity.this, "配对成功！", Toast.LENGTH_SHORT).show();
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mScanBlueReceiver, intentFilter);

        IntentFilter pinFilter = new IntentFilter();
        pinFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        pinFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mPinBlueReceiver, pinFilter);

        mAdapter = new BaseQuickAdapter<BluetoothDevice, BaseViewHolder>(R.layout.item_list, mList) {
            @Override
            protected void convert(@NotNull BaseViewHolder holder, BluetoothDevice item) {
                holder.setText(R.id.tv_name, TextUtils.isEmpty(item.getName()) ? "未知设备" : item.getName());
                holder.setText(R.id.tv_mac, item.getAddress());
                holder.setText(R.id.tv_status, item.getBondState() == BluetoothDevice.BOND_BONDED ? "已配对" : "未配对");
            }
        };
        rvList.setLayoutManager(new LinearLayoutManager(this));
        rvList.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                BluetoothDevice device = (BluetoothDevice) adapter.getItem(position);
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Intent intent = new Intent(MainActivity.this, MyDeviceActivity.class);
                    intent.putExtra("device", device);
                    startActivity(intent);
                } else if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    mBluetoothUtils.pin(device);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_is_support:
                if (mBluetoothUtils.isSupportBlue()) {
                    Toast.makeText(this, "当前设备支持蓝牙！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "当前设备不支持蓝牙！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_open_asyn:
                mBluetoothUtils.openBlueAsync();
                break;
            case R.id.btn_open_sync:
                mBluetoothUtils.openBlueSync(this, 10001);
                break;
            case R.id.btn_is_enable:
                if (mBluetoothUtils.isBlueEnable()) {
                    Toast.makeText(this, "蓝牙已开启！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "蓝牙未开启！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_search:
                checkPermission();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mScanBlueReceiver);
        unregisterReceiver(mPinBlueReceiver);
    }

    /**
     * 检查权限
     */
    private void checkPermission() {
        PermissionX.init(this).permissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION).request((allGranted, grantedList, deniedList) -> {
            if (allGranted) {
                if (!mBluetoothUtils.scanBlue()) {
                    Toast.makeText(this, "搜索异常！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10001 && resultCode == RESULT_OK) {
            if (mBluetoothUtils.isBlueEnable()) {
                Toast.makeText(this, "蓝牙已开启！", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "开启蓝牙失败！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 旋转动画
     *
     * @param view
     * @return
     */
    private static ObjectAnimator rotation(View view) {
        ObjectAnimator mAnim = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        mAnim.setDuration(1 * 1000);
        mAnim.setRepeatMode(ValueAnimator.RESTART);
        mAnim.setRepeatCount(ValueAnimator.INFINITE);
        mAnim.setInterpolator(new LinearInterpolator());
        return mAnim;
    }
}