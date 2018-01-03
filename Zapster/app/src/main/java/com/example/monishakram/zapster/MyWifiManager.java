package com.example.monishakram.zapster;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;


class MyWifiManager implements Serializable{
    private Activity activity;
    private WifiManager wifiManager;
    private WifiP2pManager wifiP2pManager;
    IntentFilter intentFilter;
    Channel channel;
    String TAG = "MyWifiManager";
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> netWorkList;
    MyBroadCastReceiver myBroadCastReceiver;


    MyWifiManager(Activity activity){

        this.activity = activity;
        wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiP2pManager = (WifiP2pManager) activity.getApplicationContext().getSystemService(Context.WIFI_P2P_SERVICE);
        channel = wifiP2pManager.initialize(activity, activity.getMainLooper(), null);
        myBroadCastReceiver = new MyBroadCastReceiver(wifiP2pManager, channel, activity);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);


    }

    void setDeviceName(final String deviceName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Log.i(TAG, "In function setDeviceName");
        Class[] methodParams = new Class[3];
        methodParams[0] = WifiP2pManager.Channel.class;
        methodParams[1] = String.class;
        methodParams[2] = WifiP2pManager.ActionListener.class;

        Object[] argList = new Object[3];
        argList[0] = channel;
        argList[1] = deviceName;
        argList[2] = new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "DeviceName changed to " +deviceName);
            }

            @Override
            public void onFailure(int reason) {
                Log.i(TAG, "Unable to change device name");
            }
        };

        Method setDeviceName = wifiP2pManager.getClass().getMethod("setDeviceName", methodParams);
        setDeviceName.setAccessible(true);
        setDeviceName.invoke(wifiP2pManager, argList);
    }

    void scanDevice(){
        wifiP2pManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

                Log.i(TAG, "Searching for new Devices");
                Toast.makeText(activity, "Scanning for Devices", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                Log.i(TAG, "Searching failed");
                Toast.makeText(activity, "Retry", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void connectToDevice(int i){
        WifiP2pDevice device;
        WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
        device = myBroadCastReceiver.getDevice(i);
        wifiP2pConfig.deviceAddress = device.deviceAddress;
        switch (device.status){
            case WifiP2pDevice.AVAILABLE:
                Log.i("Status", "Device Status = Available");break;
            case WifiP2pDevice.UNAVAILABLE:
                Log.i("Status", "Device Status = Unavailable");break;
            case WifiP2pDevice.CONNECTED:
                Log.i("Status", "Device Status = Connected");break;
        }
        wifiP2pManager.connect(channel, wifiP2pConfig, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(int reason) {

            }
        });
    }

    void enableReceiver(boolean status){
        if(status)
            activity.registerReceiver(myBroadCastReceiver, intentFilter);
        else
            activity.unregisterReceiver(myBroadCastReceiver);
    }

    void setAdapter(ArrayAdapter<String> arrayAdapter, ArrayList<String> netWorkList){
        this.arrayAdapter = arrayAdapter;
        this.netWorkList = netWorkList;
        myBroadCastReceiver.setAdapter(this.arrayAdapter, this.netWorkList);
    }

    boolean isConnected(){
        return myBroadCastReceiver.isConnected();
    }

    void enableWifi(boolean status){
        wifiManager.setWifiEnabled(status);
    }

    WifiP2pInfo getWifiP2pInfo(){
        return myBroadCastReceiver.wifiP2pInfo;
    }

}
