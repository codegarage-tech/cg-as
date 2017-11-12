package com.reversecoder.mh.model;

import java.util.ArrayList;

/**
 * Md. Rashadul Alam
 */
public class TimeZone extends ResponseBase {

    private String id = "";
    private String name = "";
    private ArrayList<City> city;

    public TimeZone(String id, String name, ArrayList<City> city) {
        this.id = id;
        this.name = name;
        this.city = city;
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

    public ArrayList<City> getCity() {
        return city;
    }

    public void setCity(ArrayList<City> city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", city=" + city +
                '}';
    }
}
