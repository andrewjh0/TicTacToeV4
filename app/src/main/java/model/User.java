package model;

import java.util.Objects;

public class User {
    // Class Attributes
    private String username;
    private String password;
    private String displayName;
    private boolean online;

    // Default Constructor
    public User() {
    }

    // Constructor with all attributes
    public User(String username, String password, String displayName, boolean online) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.online = online;
    }

    // Getters and Setters for all attributes
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    @Override
    public boolean equals(Object obj) {
        try {
            User other = (User) obj;
            return this.username.equals(other.getUsername());
        } catch (ClassCastException e) {
            return false;
        }
    }
}

