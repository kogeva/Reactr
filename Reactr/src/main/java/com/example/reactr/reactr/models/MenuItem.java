package com.example.reactr.reactr.models;

public class MenuItem {
    private int index;
    private String name;
    private String countNewMessage;

    public static final int SEND_PHOTO = 0;
    public static final int MAILBOX = 1;
    public static final int FRIENDS = 2;
    public static final int SETTINGS = 3;

    public MenuItem(String name, String countNewMessage, int index) {
        this.name = name;
        this.countNewMessage = countNewMessage;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getCountNewMessage() {
        return countNewMessage;
    }

    public void setCountNewMessage(String countNewMessage) {
        this.countNewMessage = countNewMessage;
    }
}
