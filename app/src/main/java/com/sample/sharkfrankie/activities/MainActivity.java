package com.sample.sharkfrankie.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sample.sharkfrankie.R;
import com.sample.sharkfrankie.utils.ConnectDevice;

import java.util.UUID;

import io.github.controlwear.virtual.joystick.android.JoystickView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BluetoothGatt mGatt;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattCharacteristic characteristic;
    
    private static final int REQUEST_ENABLE_BT = 1;
    private static final UUID SERVICE_UUID = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    private static final UUID CHAR_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");

    private byte[] bytes = new byte[3];
    private byte middleMotor = 0, rightMotor = 0, leftMotor = 0;

    ConnectDevice connectDevice = new ConnectDevice();

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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

        //joystick control
        JoystickView joystick = (JoystickView) findViewById(R.id.joystickView);
        joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {

                characteristic = mGatt.getService(SERVICE_UUID).getCharacteristic(CHAR_UUID);

                if ((angle > 0 & angle <= 15) || angle > 345) {
                    rightMotor = prepareData("01", "1", "1111");
                    leftMotor = prepareData("10", "1", "1111");
                } else if (angle > 15 & angle <= 45) {
                    rightMotor = prepareData("01", "1", "1111");
                    leftMotor = prepareData("10", "1", "1010");
                } else if (angle > 45 & angle <= 75) {
                    rightMotor = prepareData("01", "1", "1111");
                    leftMotor = prepareData("10", "1", "0101");
                } else if (angle > 75 & angle <= 105) {
                    rightMotor = prepareData("01", "1", "1111");
                    leftMotor = prepareData("10", "0", "1111");
                } else if (angle > 105 & angle <= 135) {
                    rightMotor = prepareData("01", "0", "1111");
                    leftMotor = prepareData("10", "0", "0101");
                } else if (angle > 135 & angle <= 165) {
                    rightMotor = prepareData("01", "0", "1111");
                    leftMotor = prepareData("10", "0", "1010");
                } else if (angle > 165 & angle <= 195) {
                    rightMotor = prepareData("01", "0", "1111");
                    leftMotor = prepareData("10", "0", "1111");
                } else if (angle > 195 & angle <= 225) {
                    rightMotor = prepareData("01", "0", "1010");
                    leftMotor = prepareData("10", "0", "1111");
                } else if (angle > 225 & angle <= 255) {
                    rightMotor = prepareData("01", "0", "0101");
                    leftMotor = prepareData("10", "0", "1111");
                } else if (angle > 255 & angle <= 285) {
                    rightMotor = prepareData("01", "0", "1111");
                    leftMotor = prepareData("10", "1", "1111");
                } else if (angle > 285 & angle <= 315) {
                    rightMotor = prepareData("01", "1", "0101");
                    leftMotor = prepareData("10", "1", "1111");
                } else if (angle > 315 & angle <= 345) {
                    rightMotor = prepareData("01", "1", "1010");
                    leftMotor = prepareData("10", "1", "1111");
                } else {
                    rightMotor = prepareData("01", "1", "0000");
                    leftMotor = prepareData("10", "1", "0000");
                    middleMotor = prepareData("00", "1", "0000");
                }

                bytes[0] = middleMotor;
                bytes[1] = rightMotor;
                bytes[2] = leftMotor;
                sendData(bytes);
            }
        }, 500);
    }

    public void actionBtnUp(View view) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                characteristic = mGatt.getService(SERVICE_UUID).getCharacteristic(CHAR_UUID);

                middleMotor = prepareData("00", "1", "1111");

                bytes[0] = middleMotor;
                sendData(bytes);
            }
        }, 500);
    }

    public void actionBtnDown(View view) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                characteristic = mGatt.getService(SERVICE_UUID).getCharacteristic(CHAR_UUID);

                middleMotor = prepareData("00", "0", "1111");

                bytes[0] = middleMotor;
                sendData(bytes);
            }
        }, 500);
    }

    public void actionBtnStop(View view) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                characteristic = mGatt.getService(SERVICE_UUID).getCharacteristic(CHAR_UUID);

                middleMotor = prepareData("00", "0", "0000");
                rightMotor = prepareData("01", "0", "0000");
                leftMotor = prepareData("10", "0", "0000");

                bytes[0] = middleMotor;
                bytes[1] = rightMotor;
                bytes[2] = leftMotor;
                sendData(bytes);
            }
        }, 500);
    }

    public byte prepareData(String motor, String direction, String speed) {
        String data = "";
        String crc = "1";
        data += crc;
        data += motor;
        data += direction;
        data += speed;

        byte b = (byte) Integer.parseInt(data, 2);

        return b;
    }

    private void sendData(byte[] b) {
        if(mGatt != null) {
            characteristic = mGatt.getService(SERVICE_UUID).getCharacteristic(CHAR_UUID);
            characteristic.setValue(b);
            mGatt.writeCharacteristic(characteristic);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_connect) {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("E0:E5:CF:23:C4:E1");
            connectToDevice(device);

            if(mGatt == null) {
                Toast.makeText(MainActivity.this, "Device is not connected", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Device is connected", Toast.LENGTH_LONG).show();
            }


        } else if (id == R.id.nav_disconnect) {
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice("E0:E5:CF:23:C4:E2");
            disconnectFromDevice(device);

            if(mGatt == null) {
                Toast.makeText(MainActivity.this, "Device is not connected", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Device is connected", Toast.LENGTH_LONG).show();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    public void connectToDevice(BluetoothDevice device) {
        if(mGatt == null) {
            mGatt = device.connectGatt(this, false, gattCallback);
        }
    }

    public void disconnectFromDevice(BluetoothDevice device) {
        mGatt = null;
    }
}
