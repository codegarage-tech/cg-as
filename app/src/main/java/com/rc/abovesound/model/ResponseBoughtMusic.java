package com.rc.abovesound.model;

import java.util.ArrayList;

/**
 * Md. Rashadul Alam
 */
public class ResponseBoughtMusic extends ResponseBase {

    private String status = "";
    private ArrayList<Music> data = new ArrayList<Music>();

    public ResponseBoughtMusic() {
    }

    public ResponseBoughtMusic(String status, ArrayList<Music> data) {
        this.status = status;
        this.data = data;
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

    @Override
    public String toString() {
        return "{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
