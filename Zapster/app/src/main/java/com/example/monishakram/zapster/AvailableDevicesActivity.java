package com.example.monishakram.zapster;

import android.content.Intent;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

public class AvailableDevicesActivity extends AppCompatActivity {
    private String TAG = "DevicesActivity";
    private MyWifiManager myWifiManager;
    ListView listViewNetworkList;
    ArrayList<String> arrayList;
    ArrayAdapter<String> arrayAdapter;
    boolean loop;
    Intent intent;
    String userName;
    ConnectionStatusTask connectionStatusTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_devices);

        //get the LoginName from previous activity
        userName = getIntent().getStringExtra("LoginName");
        Log.i(TAG, "UserName received from previous activity = " + userName);
        if(userName.length() == 0)
            userName = "No Name";

        //Initialize WifiManager
        myWifiManager = new MyWifiManager(this);

        //SetUpListView
        listViewNetworkList = (ListView)findViewById(R.id.listViewDeviceList);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter<>(this, R.layout.listviewtext, arrayList);
        listViewNetworkList.setAdapter(arrayAdapter);

        loop = false;

        //SetUpButtonExit
        (findViewById(R.id.buttonExit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //SetUpButtonRescan
        findViewById(R.id.buttonRescan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myWifiManager.enableWifi(true);
                myWifiManager.scanDevice();
            }
        });

        //SetUpItemClickListener for connecting to device
        ((ListView) findViewById(R.id.listViewDeviceList)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myWifiManager.connectToDevice(position);
            }
        });

        //DisplayName
        ((TextView)findViewById(R.id.textViewDeviceName)).setText(userName);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG,"OnStart Called");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG,"OnRestart Called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG,"OnResume Called");
        myWifiManager.enableWifi(true);
        myWifiManager.enableReceiver(true);
        myWifiManager.setAdapter(arrayAdapter, arrayList);
        myWifiManager.scanDevice();
        loop = true;
        connectionStatusTask = new ConnectionStatusTask();
        connectionStatusTask.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myWifiManager.enableReceiver(false);
        Log.i(TAG,"OnPause Called");
        loop = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"OnStop Called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"OnDestroy Called");
    }


    private class ConnectionStatusTask extends AsyncTask{

        @Override
        protected Void doInBackground(Object[] params) {
            while(loop){
                try{
                    Thread.sleep(1000);
                    if(myWifiManager.isConnected())
                        break;
                    Log.i(TAG, "Connection Status = " +myWifiManager.isConnected());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
          //  super.onPostExecute(o);
            if(myWifiManager.isConnected()){
                WifiP2pInfo info = myWifiManager.getWifiP2pInfo();
                Log.i(TAG, "Group Owner status = " +info.isGroupOwner);
                Log.i(TAG," Owner Address" +Arrays.toString(info.groupOwnerAddress.getAddress()));
                intent = new Intent(getApplicationContext(), ChatActivity.class);
                intent.putExtra("DeviceName", userName);
                ChatActivity.wifiP2pInfo = info;
                startActivity(intent);
            }
        }
    }
}
