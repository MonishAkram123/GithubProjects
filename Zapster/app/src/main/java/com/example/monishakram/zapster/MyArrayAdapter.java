package com.example.monishakram.zapster;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


class MyArrayAdapter extends ArrayAdapter<ChatBubble> {
    private Context context;
  //  private List<ChatBubble> messages;
    MyArrayAdapter(@NonNull Context context, @LayoutRes int resource, List<ChatBubble> objects) {
        super(context, resource, objects);
        this.context = context;
    //    this.messages = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        int layoutResource;
        ChatBubble chatBubble = getItem(position);
        getItemViewType(position);
        if(chatBubble == null)
            Log.i("ArrayAdapter", "ChatBubble is null");
        if(chatBubble.myMessage)
            layoutResource = R.layout.layout_outgoing_message;
        else
            layoutResource = R.layout.layout_incoming_message;

        if(convertView != null)
            holder = (ViewHolder) convertView.getTag();
        else {
            convertView = inflater.inflate(layoutResource, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }
        holder.message.setText(chatBubble.getContent());
        return convertView;
    }
    @Override
    public int getViewTypeCount() {
        // return the total number of view types. this value should never change
        // at runtime. Value 2 is returned because of left and right views.
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        // return a value between 0 and (getViewTypeCount - 1)
        return getItem(position).myMessage?0:1;
    }
    private class ViewHolder{
        private TextView message;
        ViewHolder(View v){
            message =  v.findViewById(R.id.txt_message);
        }
    }
}

