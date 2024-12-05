package edu.sabanciuniv.howudoin.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String name;
    private String lastName;
    private String email;
    private String password;

    private List<String> friends = new ArrayList<>();

    @Field("friendRequests")
    private List<FriendRequest> friendRequests = new ArrayList<>();

    @Field("groups")
    private List<String> groups = new ArrayList<>();

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public List<FriendRequest> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(List<FriendRequest> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    // Utility methods for `groups`
    public void addGroup(String groupId) {
        if (!this.groups.contains(groupId)) {
            this.groups.add(groupId);
        }
    }

    public void removeGroup(String groupId) {
        this.groups.remove(groupId);
    }

    // Utility methods for `friendRequests`
    public void addFriendRequest(FriendRequest request) {
        this.friendRequests.add(request);
    }

    public void removeFriendRequest(String friendId) {
        this.friendRequests.removeIf(request -> request.getFriendId().equals(friendId));
    }

    public FriendRequest findFriendRequestById(String friendId) {
        return this.friendRequests.stream()
                .filter(request -> request.getFriendId().equals(friendId))
                .findFirst()
                .orElse(null);
    }
}
