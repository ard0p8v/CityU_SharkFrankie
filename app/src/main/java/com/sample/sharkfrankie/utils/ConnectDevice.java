package com.sample.sharkfrankie.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.sample.sharkfrankie.R;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConnectDevice extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    private static final UUID SERVICE_UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    private static final UUID CHAR_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    private BluetoothGatt mGatt;

    Button btnScanDevices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_devices);

        btnScanDevices = findViewById(R.id.btnScanDevices);

        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE Not Supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        btnScanDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("E0:E5:CF:23:C4:E1");
                connectToDevice(device);
                if(mGatt == null) {
                    Toast.makeText(ConnectDevice.this, "Device is not connected", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ConnectDevice.this, "Device is connected", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status:" + status);

            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }
        }
    };

    public void sendCommand() {
        if(mGatt != null) {
            BluetoothGattService service = mGatt.getService(SERVICE_UUID);
            BluetoothGattCharacteristic characteristic = service.getCharacteristic(CHAR_UUID);

            characteristic.setValue("f".getBytes());
            mGatt.writeCharacteristic(characteristic);
        }
    }

    public void connectToDevice(BluetoothDevice device) {
        if(mGatt == null) {
            mGatt = device.connectGatt(this, false, gattCallback);
        }
    }



}
