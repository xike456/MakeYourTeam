package com.smile.makeyourteam.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.smile.makeyourteam.Adapters.ChatAdapter;
import com.smile.makeyourteam.Models.Message;
import com.smile.makeyourteam.R;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    List<Message> messageList = new ArrayList<Message>();
    ChatAdapter chatAdapter = null;
    ListView lvChat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Message message = new Message();
        message.userName = "Vu";
        message.timestamp = 1478702193;
        message.messages = "Hello";
        Message message2 = new Message();
        message2.userName = "Phu";
        message2.timestamp = 1478702193;
        message2.messages = "Chao Vu";
        messageList.add(message);
        messageList.add(message2);


        lvChat = (ListView) findViewById(R.id.listMessages);

        chatAdapter = new ChatAdapter(this, R.layout.chat_item, messageList);
        lvChat.setAdapter(chatAdapter);
        lvChat.setSelection(lvChat.getAdapter().getCount()-1);
    }
}
