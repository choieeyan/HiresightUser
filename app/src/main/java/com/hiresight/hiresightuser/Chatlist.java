package com.hiresight.hiresightuser;

public class Chatlist {
    private String recentMessage, timeStamp, clientID;

    public Chatlist() {
    }

    public Chatlist(String recentMessage, String timeStamp, String clientID) {
        this.recentMessage = recentMessage;
        this.timeStamp = timeStamp;
        this.clientID = clientID;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getRecentMessage() {
        return recentMessage;
    }

    public void setRecentMessage(String recentMessage) {
        this.recentMessage = recentMessage;
    }
}
