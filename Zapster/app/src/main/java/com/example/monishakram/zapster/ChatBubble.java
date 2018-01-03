package com.example.monishakram.zapster;

/**
 * Created by Monish Akram on 11/7/2017.
 */

//Chat Bubble for Setting ListView
class ChatBubble{
    String content;
    boolean myMessage;
    public ChatBubble(String content, boolean myMessage) {
        this.content = content;
        this.myMessage = myMessage;
    }

    public String getContent() {
        return content;
    }
}
