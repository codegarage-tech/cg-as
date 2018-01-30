package com.rc.abovesound.model;

import java.util.ArrayList;

/**
 * Md. Rashadul Alam
 */
public class ResponseUserData extends ResponseBase {

    private String status = "";
    private String msg = "";
    private ArrayList<UserData> user_details = new ArrayList<UserData>();

    public ResponseUserData() {
    }

    public ResponseUserData(String status, String msg, ArrayList<UserData> user_details) {
        this.status = status;
        this.msg = msg;
        this.user_details = user_details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public ArrayList<UserData> getUser_details() {
        return user_details;
    }

    public void setUser_details(ArrayList<UserData> user_details) {
        this.user_details = user_details;
    }
}
