package com.reversecoder.mh.model;

import java.util.ArrayList;

/**
 * Md. Rashadul Alam
 */
public class Country extends ResponseBase {

    private String id = "";
    private String name = "";
    private ArrayList<TimeZone> timezone;

    public Country(String id, String name, ArrayList<TimeZone> timezone) {
        this.id = id;
        this.name = name;
        this.timezone = timezone;
    }

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

    public ArrayList<TimeZone> getTimezone() {
        return timezone;
    }

    public void setTimezone(ArrayList<TimeZone> timezone) {
        this.timezone = timezone;
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", timezone=" + timezone +
                '}';
    }
}
