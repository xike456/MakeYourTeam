package com.smile.makeyourteam.Models;

/**
 * Created by mpnguyen on 10/11/2016.
 */

public class Message {
    public String userName;
    public long timestamp;
    public String messages;

    public String senderId;
    public String receiveId;
    public String photoUrl;
    public String nameUserReceive;

    public String messageImage;
    public String fileName = "";
    public String fileUrl = "";
    public long fileSize = -1;
    public boolean isNotify = false;

    public Message(){
    }

    public Message(String userName, long timestamp, String messages, String senderId, String receiveId, String photoUrl, String nameUserReceive, String messageImage){
        this.messages = messages;
        this.receiveId = receiveId;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.messages = messages;
        this.userName = userName;
        this.photoUrl = photoUrl;
        this.nameUserReceive = nameUserReceive;
        this.messageImage = messageImage;
    }

    public Message(String userName, long timestamp, String messages, String senderId,
                   String receiveId, String photoUrl, String nameUserReceive, String messageImage,
                   String fileName, String fileUrl, long fileSize){
        this.messages = messages;
        this.receiveId = receiveId;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.messages = messages;
        this.userName = userName;
        this.photoUrl = photoUrl;
        this.nameUserReceive = nameUserReceive;
        this.messageImage = messageImage;

        this.fileName = fileName;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
    }
}
