package com.example.monishakram.zapster;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;

class MyBroadCastReceiver extends BroadcastReceiver{
    WifiP2pManager.Channel channel;
    WifiP2pManager wifiP2pManager;
    Activity activity;
    String TAG = "MyBroadCastReceiver";
    ArrayList<WifiP2pDevice> deviceList;
    ArrayAdapter<String> arrayAdapter;
    ArrayList<String> networkList;
    WifiP2pInfo wifiP2pInfo;
    boolean connectionStatus;

    public MyBroadCastReceiver(WifiP2pManager wifiP2pManager, WifiP2pManager.Channel channel, Activity activity) {
        this.channel = channel;
        this.activity = activity;
        this.wifiP2pManager = wifiP2pManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)){
            WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
                @Override
                public void onPeersAvailable(WifiP2pDeviceList peers) {
                    deviceList = new ArrayList<>(peers.getDeviceList());
                    Log.i(TAG, deviceList.size() +" Devices Discovered");
                    updateNetwork(deviceList);
                }
            };
            wifiP2pManager.requestPeers(channel, peerListListener);
        }

        if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)){
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if(networkInfo == null)
                connectionStatus = false;
            else
                connectionStatus = networkInfo.isConnected();
            if(connectionStatus){
                wifiP2pManager.requestConnectionInfo(channel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {
                        wifiP2pInfo = info;
                    }
                });
            }
        }
    }

    void setAdapter(ArrayAdapter<String> arrayAdapter, ArrayList<String> networkList){
        this.arrayAdapter = arrayAdapter;
        this.networkList = networkList;
    }

    void updateNetwork(ArrayList<WifiP2pDevice> deviceList){
        if(networkList == null)
            return;
        while(!networkList.isEmpty())
            networkList.remove(networkList.size() -1);
        for (WifiP2pDevice device : deviceList)
            networkList.add(device.deviceName);
        arrayAdapter.notifyDataSetChanged();
    }

    WifiP2pDevice getDevice(int i){
        return deviceList.get(i);
    }

    boolean isConnected(){
        return connectionStatus;
    }

    WifiP2pInfo getWifiP2pInfo(){
        return wifiP2pInfo;
    }


}
