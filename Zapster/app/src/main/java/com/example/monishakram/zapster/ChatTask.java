package com.example.monishakram.zapster;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static android.content.ContentValues.TAG;

class ChatTask extends AsyncTask<Object, ChatBubble, Object> {
    private boolean chatLoop;
    private String message;
    private BufferedReader inComingStream;
    private PrintWriter outGoingStream;
    private ArrayAdapter<ChatBubble> arrayAdapter;
    private List<ChatBubble> arrayList;
    private TextView display;
    private boolean nameSet;

    ChatTask(String message, BufferedReader inComingStream, PrintWriter outGoingStream, List<ChatBubble> arrayList, ArrayAdapter<ChatBubble> arrayAdapter, TextView display){
        chatLoop = true;
        this.message = message;
        this.inComingStream = inComingStream;
        this.outGoingStream = outGoingStream;
        this.arrayList = arrayList;
        this.arrayAdapter = arrayAdapter;
        this.display = display;
        nameSet = false;
    }
    void sendMessage(String message){
        this.message = message;
    }
    @Override
    protected Object doInBackground(Object[] params) {
        Thread readerThread, writerThread;
        readerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String name = "";
                    while(name.length() < 0)
                        name = inComingStream.readLine();
                    publishProgress(new ChatBubble(name, false));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while(chatLoop){
                    try {
                        Thread.sleep(500);
                        String message = inComingStream.readLine();
                        Log.i(TAG, "Message Received" +message);
                        publishProgress(new ChatBubble(message, false));
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        writerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                message = "";
                try{
                    while(chatLoop){
                        Thread.sleep(500);
                        if(message.length()> 0){
                            outGoingStream.println(message);
                            publishProgress(new ChatBubble(message, true));
                            message = "";
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        readerThread.start();
        writerThread.start();
        return null;
    }
    @Override
    protected void onProgressUpdate(ChatBubble... values) {
        super.onProgressUpdate(values);
        if(!nameSet) {
            display.setText(values[0].getContent());
            nameSet = true;
            return;
        }
        arrayList.add(values[0]);
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
    }

    void setChatLoop(boolean value){
        chatLoop = value;
    }

}