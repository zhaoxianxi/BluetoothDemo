package com.november.bluetoothdemo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MyDeviceActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvWeight;
    private Button btnStartConnected;
    private Button btnAddressConnected;
    private Button btnGetWeight;
    private Button btnPeeling;
    private Button btnZero;
    private Button btnIsConnect;
    private Button btnDisconnect;

    private BluetoothMonitorReceiver mBluetoothMonitorReceiver = null;
    private BluetoothUtils mBluetoothUtils;
    private ConnectedThread mThread;
    private BluetoothDevice mDevice;

    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 10001) {
                String weight = (String) msg.obj;
                tvWeight.setText(weight.trim());
            }else if (msg.what==10002){
                mDevice= (BluetoothDevice) msg.obj;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_device);
        mDevice = getIntent().getParcelableExtra("device");
        initView();
    }

    private void initView() {
        tvWeight = (TextView) findViewById(R.id.tv_weight);
        btnStartConnected = (Button) findViewById(R.id.btn_start_connected);
        btnAddressConnected = (Button) findViewById(R.id.btn_address_connected);
        btnGetWeight = (Button) findViewById(R.id.btn_get_weight);
        btnPeeling = (Button) findViewById(R.id.btn_peeling);
        btnZero = (Button) findViewById(R.id.btn_zero);
        btnIsConnect = (Button) findViewById(R.id.btn_is_connect);
        btnDisconnect = (Button) findViewById(R.id.btn_disconnect);

        btnStartConnected.setOnClickListener(this);
        btnAddressConnected.setOnClickListener(this);
        btnGetWeight.setOnClickListener(this);
        btnPeeling.setOnClickListener(this);
        btnZero.setOnClickListener(this);
        btnIsConnect.setOnClickListener(this);
        btnDisconnect.setOnClickListener(this);

        mBluetoothUtils = new BluetoothUtils();
        mBluetoothMonitorReceiver = new BluetoothMonitorReceiver(new BluetoothMonitorCallBack() {

            @Override
            public void onConnected() {

            }

            @Override
            public void onDisconnected() {
                Toast.makeText(MyDeviceActivity.this, "设备已断开连接！", Toast.LENGTH_SHORT).show();
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        // 监视蓝牙关闭和打开的状态
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        // 监视蓝牙设备与APP连接的状态
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        registerReceiver(this.mBluetoothMonitorReceiver, intentFilter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_connected:
                startConnected();
                break;
            case R.id.btn_address_connected:
                mBluetoothUtils.connectMAC("DC:0D:30:12:E7:C4", new ConnectBlueCallBack() {
                    @Override
                    public void onStartConnect() {
                        Toast.makeText(MyDeviceActivity.this, "开始连接！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onConnectSuccess(BluetoothDevice device, BluetoothSocket socket) {
                        Message message = new Message();
                        message.what = 10002;
                        message.obj = device;
                        mHandler.sendMessage(message);

                        initThread(socket);
                        Toast.makeText(MyDeviceActivity.this, "连接成功！", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onConnectFail(BluetoothDevice device, String hint) {
                        Message message = new Message();
                        message.what = 10002;
                        message.obj = device;
                        mHandler.sendMessage(message);

                        Toast.makeText(MyDeviceActivity.this, "连接失败！", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.btn_get_weight:
                if (null != mThread) {
                    mThread.start();
                }
                break;
            case R.id.btn_peeling:
                if (null != mThread) {
                    mThread.write("T".getBytes());
                }
                break;
            case R.id.btn_zero:
                if (null != mThread) {
                    mThread.write("Z".getBytes());
                }
                break;
            case R.id.btn_is_connect:
                if (mBluetoothUtils.isConnectBlue(mDevice)) {
                    Toast.makeText(this, "已连接！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "未连接！", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_disconnect:
                if (null != mThread) {
                    mThread.cancel();
                }
                mBluetoothUtils.cancelConnect(mDevice);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBluetoothMonitorReceiver);
    }

    /**
     * 开始连接
     */
    private void startConnected() {
        mBluetoothUtils.connect(mDevice, new ConnectBlueCallBack() {
            @Override
            public void onStartConnect() {
                Toast.makeText(MyDeviceActivity.this, "开始连接！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConnectSuccess(BluetoothDevice device, BluetoothSocket socket) {
                initThread(socket);
                Toast.makeText(MyDeviceActivity.this, "连接成功！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onConnectFail(BluetoothDevice device, String hint) {
                Toast.makeText(MyDeviceActivity.this, "连接失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 初始化连接传输线程
     *
     * @param socket
     */
    private void initThread(BluetoothSocket socket) {
        if (null == socket) {
            return;
        }
        mThread = new ConnectedThread(socket, new ConnectedOperationCallBack() {
            @Override
            public void onReadSuccess(String content) {
                Message message = new Message();
                message.what = 10001;
                message.obj = content;
                mHandler.sendMessage(message);
            }

            @Override
            public void onReadFile() {
                Toast.makeText(MyDeviceActivity.this, "读取失败！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWriteSuccess() {
                Toast.makeText(MyDeviceActivity.this, "写入成功！", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onWriteFile() {
                Toast.makeText(MyDeviceActivity.this, "写入失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }
}