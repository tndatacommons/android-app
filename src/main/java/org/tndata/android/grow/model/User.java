package org.tndata.android.grow.model;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = 4582633283983173348L;
    private int id = -1;
    private String first_name = "";
    private String last_name = "";
    private String full_name = "";
    private String email = "";
    private int userprofile_id = -1;
    private String date_joined = "";
    private String token = "";
    private String password = "";
    private String error = "";

    public User() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return first_name;
    }

    public void setFirstName(String first_name) {
        this.first_name = first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public void setLastName(String last_name) {
        this.last_name = last_name;
    }

    public String getFullName() {
        return full_name;
    }

    public void setFullName(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getUserprofileId() {
        return userprofile_id;
    }

    public void setUserprofileId(int userprofile_id) {
        this.userprofile_id = userprofile_id;
    }

    public String getDateJoined() {
        return date_joined;
    }

    public void setDateJoined(String date_joined) {
        this.date_joined = date_joined;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
