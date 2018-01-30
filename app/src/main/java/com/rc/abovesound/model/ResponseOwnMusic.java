package com.rc.abovesound.model;

import java.util.ArrayList;

/**
 * Md. Rashadul Alam
 */
public class ResponseOwnMusic extends ResponseBase {

    private String status = "";
    private ArrayList<Music> data = new ArrayList<Music>();
    private ArrayList<UserData> profile = new ArrayList<UserData>();

    public ResponseOwnMusic() {
    }

    public ResponseOwnMusic(String status, ArrayList<Music> data, ArrayList<UserData> profile) {
        this.status = status;
        this.data = data;
        this.profile = profile;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Music> getData() {
        return data;
    }

    public void setData(ArrayList<Music> data) {
        this.data = data;
    }

    public ArrayList<UserData> getProfile() {
        return profile;
    }

    public void setProfile(ArrayList<UserData> profile) {
        this.profile = profile;
    }

    @Override
    public String toString() {
        return "{" +
                "status='" + status + '\'' +
                ", data=" + data +
                ", profile=" + profile +
                '}';
    }
}
