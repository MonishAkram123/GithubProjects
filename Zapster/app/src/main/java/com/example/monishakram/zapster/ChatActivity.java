package com.example.monishakram.zapster;

import android.net.wifi.p2p.WifiP2pInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    static WifiP2pInfo wifiP2pInfo = new WifiP2pInfo();
    MyWifiManager myWifiManager;
    String TAG = "ChatActivity";
    ListView chatMessages;
    ImageButton sendButton;
    Socket clientSocket;
    PrintWriter outGoingStream;
    BufferedReader inComingStream;
    ServerSocket serverSocket;
    ClientSocketTask clientSocketTask;
    ServerSocketTask serverSocketTask;
    ArrayAdapter<ChatBubble> arrayAdapter;
    List<ChatBubble> chatBubbleList;
    TextView displayName;
    ChatTask chatTask;
    int portNumber = 8888;
    boolean loop;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        myWifiManager = new MyWifiManager(this);


        //Setting the Send Button
        sendButton = (ImageButton) findViewById(R.id.imageButtonSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editText = (EditText) findViewById(R.id.TextToSend);
                String message = editText.getText().toString();
                chatTask.sendMessage(message);
                editText.setText("");
            }
        });

        //Set Socket
        if(wifiP2pInfo.isGroupOwner){
            connectAsServer();
        }
        else
            connectAsClient();


        //Setting the ArrayAdapter and ListView;
        chatBubbleList = new ArrayList<>();
        arrayAdapter = new MyArrayAdapter(this, R.layout.layout_outgoing_message, chatBubbleList);
        chatMessages = (ListView) findViewById(R.id.listViewChatMessages);
        chatMessages.setAdapter(arrayAdapter);


        displayName = (TextView) findViewById(R.id.textViewDeviceName);

    }

    private void connectAsServer() {
        serverSocketTask = new ServerSocketTask();
        serverSocketTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void connectAsClient() {
        clientSocketTask = new ClientSocketTask();
        clientSocketTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
        myWifiManager.enableReceiver(true);
        if(chatTask != null)
            chatTask.setChatLoop(true);
        loop = true;
        ConnectionStatusThread connectionStatusThread = new ConnectionStatusThread();
        connectionStatusThread.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //SocketManagement

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,"OnPause Called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG,"OnStop Called");
        myWifiManager.enableReceiver(false);
        if(chatTask != null)
            chatTask.setChatLoop(false);
        loop = false;
        myWifiManager.enableWifi(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"OnDestroy Called");
        try {
            if(serverSocket != null && !serverSocket.isClosed())
                serverSocket.close();
            if(clientSocket != null && clientSocket.isConnected())
                clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private class ConnectionStatusThread extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            while(loop){
                try{
                    Thread.sleep(100);
                    Log.i(TAG, "Connection Status = " +myWifiManager.isConnected());
                    if(!myWifiManager.isConnected())
                        break;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
       //     super.onPostExecute(o);
            if(chatTask != null)
                chatTask.setChatLoop(false);
            if(chatTask != null && chatTask.isCancelled())
                chatTask.cancel(true);

            if(!myWifiManager.isConnected())
                (new MyDialogBox(ChatActivity.this)).showExitDialog("Connection Lost", "You Are Disconnected");
            //finish();

        }
    }
    private class ClientSocketTask extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                Log.i(TAG,"In ClientSocketTask");
                Log.i(TAG, "Waiting for Connection to server");
                clientSocket = new Socket(ChatActivity.wifiP2pInfo.groupOwnerAddress, portNumber);
                if(clientSocket.isConnected()) {
                    Log.i(TAG, "Connected to Server");
                }
                else
                    return null;
                inComingStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outGoingStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
           // super.onPostExecute(o);
            String name = getIntent().getStringExtra("DeviceName");
            chatTask = new ChatTask("", inComingStream, outGoingStream, chatBubbleList, arrayAdapter, displayName);
            chatTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            chatTask.sendMessage(name);
        }
    }
    private class ServerSocketTask extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] params) {
            Log.i(TAG,"In ServerSocketTask");
            try {
                serverSocket = new ServerSocket(portNumber);
                Log.i(TAG, "Waiting for Connection Request from client");
                clientSocket = new Socket();
                clientSocket = serverSocket.accept();
                if(clientSocket.isConnected())
                    Log.i(TAG, "Connected to Client");
                else
                    Log.i(TAG, "Error! in Connection");
                inComingStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                outGoingStream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            //super.onPostExecute(o);
            String name = getIntent().getStringExtra("DeviceName");
            chatTask = new ChatTask("", inComingStream, outGoingStream, chatBubbleList, arrayAdapter, displayName);
            chatTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            chatTask.sendMessage(name);
        }
    }
}
