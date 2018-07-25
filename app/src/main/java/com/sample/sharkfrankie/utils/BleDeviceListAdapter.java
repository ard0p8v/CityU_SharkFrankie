package com.sample.sharkfrankie.utils;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sample.sharkfrankie.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class BleDeviceListAdapter extends BaseAdapter {

    private ArrayList<BluetoothDevice> deviceArrayList;
    private Context context;
    private LayoutInflater mInflater;

    public BleDeviceListAdapter(Context context, ArrayList<BluetoothDevice> devices) {
        this.context = context;
        this.deviceArrayList = devices;
        mInflater = LayoutInflater.from(this.context);

    }
    public void clear() {
        deviceArrayList.clear();
    }


    @Override
    public int getCount() {
        return deviceArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;

        if(convertView == null){
            convertView = mInflater.inflate(R.layout.activity_scan_devices, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        BluetoothDevice currentListData = (BluetoothDevice) getItem(position);

        if(currentListData.getName() == null) {
            mViewHolder.tvname.setText("No name");
        } else {
            mViewHolder.tvname.setText(currentListData.getName());
        }

        mViewHolder.tvmac.setText(currentListData.getAddress());

        return convertView;
    }

    private class MyViewHolder {
        TextView tvname, tvmac;

        public MyViewHolder(View item) {
            tvname = (TextView) item.findViewById(R.id.bleDeviceInfo);
            tvmac = (TextView) item.findViewById(R.id.macAddress);
        }
    }
}
