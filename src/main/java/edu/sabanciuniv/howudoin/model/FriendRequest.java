package edu.sabanciuniv.howudoin.model;

import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;


public class FriendRequest implements Serializable {
    private String friendId;
    private String status; // Pending, Accepted, Rejected


    public FriendRequest(String friendId, String status) {
        this.friendId = friendId;
        this.status = status;
    }


    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Check if the request is pending
    public boolean isPending() {
        return "Pending".equalsIgnoreCase(this.status);
    }

    // Accept the friend request
    public void accept() {
        this.status = "Accepted";
    }

    // Reject the friend request
    public void reject() {
        this.status = "Rejected";
    }

    // Utility method to check if the request matches a specific friend ID
    public boolean matchesFriendId(String friendId) {
        return this.friendId.equals(friendId);
    }
    @Override
    public String toString() {
        return "FriendRequest{" +
                "friendId='" + friendId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
