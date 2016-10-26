package com.smile.makeyourteam.models;

/**
 * Created by NgoChiHai on 10/27/16.
 */

public class Message {
    public  String senderId;
    public  String message;

    public Message(){

    }

    public Message(String senderId, String message){
        this.senderId = senderId;
        this.message = message;
    }
}
