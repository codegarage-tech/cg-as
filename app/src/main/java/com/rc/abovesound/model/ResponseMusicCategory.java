package com.rc.abovesound.model;

import java.util.ArrayList;

/**
 * Md. Rashadul Alam
 */
public class ResponseMusicCategory extends ResponseBase {

    private String status = "";
    private ArrayList<MusicCategory> data;

    public ResponseMusicCategory(String status, ArrayList<MusicCategory> data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<MusicCategory> getData() {
        return data;
    }

    public void setData(ArrayList<MusicCategory> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
