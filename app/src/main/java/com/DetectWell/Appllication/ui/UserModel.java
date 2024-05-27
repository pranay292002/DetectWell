package com.DetectWell.Appllication.ui;

public class UserModel {
    private String Username, email;

    public UserModel() {
    }

    public UserModel(String username, String email) {
        Username = username;
        this.email = email;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
