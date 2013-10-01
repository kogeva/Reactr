package com.example.reactr.reactr.models;

import java.util.ArrayList;

public class MessageEntity {

    private int id;
    private int from_user;
    private int to_user;
    private String text;
    private String photo;
    private String reactionPhoto;
    private String createdAt;
    private Boolean fromMe;
    private Boolean isRead;
    private String username;
    private String toUsername;

    public MessageEntity() {
    }

    public MessageEntity(
            int id,
            int from_user,
            int to_user,
            String text,
            String photo,
            String reactionPhoto,
            String createdAt,
            Boolean fromMe,
            Boolean isRead,
            String username,
            String toUsername
    ) {
        this.id = id;
        this.from_user = from_user;
        this.to_user = to_user;
        this.text = text;
        this.photo = photo;
        this.reactionPhoto = reactionPhoto;
        this.createdAt = createdAt;
        this.fromMe = fromMe;
        this.isRead = isRead;
        this.username = username;
        this.toUsername = toUsername;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFrom_user() {
        return from_user;
    }

    public void setFrom_user(int from_user) {
        this.from_user = from_user;
    }

    public int getTo_user() {
        return to_user;
    }

    public void setTo_user(int to_user) {
        this.to_user = to_user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getReactionPhoto() {
        return reactionPhoto;
    }

    public void setReactionPhoto(String reactionPhoto) {
        this.reactionPhoto = reactionPhoto;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getFromMe() {
        return fromMe;
    }

    public void setFromMe(Boolean fromMe) {
        this.fromMe = fromMe;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToUsername() {
        return toUsername;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }

    public void setUsernameWithFriends(ArrayList<FriendEntity> friends)
    {
        for (int i = 0; i < friends.size(); i++)
        {
            if(!fromMe)
            {
                if(from_user == friends.get(i).getId())
                    username = friends.get(i).getUsername();
            }
        }
    }
}
