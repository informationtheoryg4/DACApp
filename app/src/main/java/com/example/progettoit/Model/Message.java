package com.example.progettoit.Model;

public class Message {

    private String sender, receiver, senderUserName;
    private String msg;
    private int length;
    private boolean deflate;
    private int channelCoding;

    public Message(String sender, String receiver, String message, String senderUserName, int length, boolean deflate, int channelCoding) {
        this.sender = sender;
        this.receiver = receiver;
        this.msg = message;
        this.senderUserName = senderUserName;
        this.length=length;
        this.deflate = deflate;
        this.channelCoding= channelCoding;
    }

    public Message() {
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getSenderUserName() {
        return senderUserName;
    }

    public void setSenderUserName(String senderUserName) {
        this.senderUserName = senderUserName;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return msg;
    }

    public void setMessage(String message) {
        this.msg = message;
    }


    public boolean isDeflate() {
        return deflate;
    }

    public void setDeflate(boolean deflate) {
        this.deflate = deflate;
    }

    public int getChannelCoding() {
        return channelCoding;
    }

    public void setChannelCoding(int channelCoding) {
        this.channelCoding = channelCoding;
    }
}
