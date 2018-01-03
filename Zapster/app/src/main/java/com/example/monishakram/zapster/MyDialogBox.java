package com.example.monishakram.zapster;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiManager;
import android.support.v7.app.AlertDialog;

/**
 * Created by Monish Akram on 9/1/2017.
 */

class MyDialogBox{
    private AlertDialog.Builder builder;
    private Activity activity;
    public MyDialogBox(Activity activity){
        this.activity = activity;
    }
    public void showExitDialog(String title, String message){
        builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.finish();
            }
        });
        builder.create().show();
    }
    public void showEnableWifi(String title, String message){
        builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        builder.setCancelable(false);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(enableWifi())
                    showExitDialog("Cannot Enable Wifi", "There is something wrong with your wifi");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.finish();
            }
        });
        builder.create().show();
    }

    private boolean enableWifi() {
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.setWifiEnabled(true);
    }
}
