package com.clbooster.app.backend.service.profile;

public class User {
    private int pin;
    private String identityEmail;
    private String username;
    private String password;
    private String firstName;
    private String lastName;

    // registration
    public User(String identityEmail, String username, String password, String firstName, String lastName) {
        this.identityEmail = identityEmail;
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // login
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and setters methods for identity

    public int getPin() { return pin; }
    public String getIdentityEmail() { return identityEmail; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }


    public void setPin(int pin) { this.pin = pin; }
    public void setIdentityEmail(String identityEmail) { this.identityEmail = identityEmail; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}