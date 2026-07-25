package com.example.chatapp;

public class User {
    public String firstName, lastName, phoneInput, emailInput, pwd, gender;
    private String uid;

    public User() {

    }

    public User(String firstName, String lastName, String phoneInput, String emailInput, String pwd, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneInput = phoneInput;
        this.emailInput = emailInput;
        this.pwd = pwd;
        this.gender = gender;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public String getName() {
        return firstName + " " + lastName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return emailInput;
    }
}