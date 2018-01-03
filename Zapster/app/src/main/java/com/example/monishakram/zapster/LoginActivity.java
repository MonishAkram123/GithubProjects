package com.example.monishakram.zapster;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.lang.reflect.InvocationTargetException;

public class LoginActivity extends AppCompatActivity {
    String TAG = "LoginActivity";
    String textLoginName;
    Intent intent;
    MyWifiManager myWifiManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i(TAG,"OnCreate Called");
        myWifiManager = new MyWifiManager(this);
        //Setting Intent for next activity
        intent = new Intent(this, AvailableDevicesActivity.class);
        (findViewById(R.id.buttonLogin)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textLoginName = ((EditText)findViewById(R.id.editTextLoginName)).getText().toString();
                if(textLoginName.length() > 0){
                    try {
                        myWifiManager.setDeviceName(textLoginName);
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                intent.putExtra("LoginName", textLoginName);
                startActivity(intent);
            }
        });
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"OnPause Called");
        myWifiManager.enableWifi(true);
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
}
