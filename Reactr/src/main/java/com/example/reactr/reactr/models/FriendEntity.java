package com.example.reactr.reactr.models;

/**
 * Created by vova on 30.08.13.
 */
public class FriendEntity {
    public int id;
    public String username;
    public long phone;
    public boolean privacyMessage;
    public boolean confirmed;
    public String nameInContacts;

    public FriendEntity() {
    }

    public FriendEntity(int id, String username, long phone, boolean privacyMessage, boolean confirmed) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.privacyMessage = privacyMessage;
        this.confirmed = confirmed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public boolean isPrivacyMessage() {
        return privacyMessage;
    }

    public void setPrivacyMessage(boolean privacyMessage) {
        this.privacyMessage = privacyMessage;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getNameInContacts() {

        return nameInContacts;
    }

    public void setNameInContacts(String nameInContacts) {
        this.nameInContacts = nameInContacts;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FriendEntity that = (FriendEntity) o;

        if (phone != that.phone) return false;
        if (nameInContacts != null ? !nameInContacts.equals(that.nameInContacts) : that.nameInContacts != null)
            return false;
        if (username != null ? !username.equals(that.username) : that.username != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (int) (phone ^ (phone >>> 32));
        result = 31 * result + (nameInContacts != null ? nameInContacts.hashCode() : 0);
        return result;
    }
}
