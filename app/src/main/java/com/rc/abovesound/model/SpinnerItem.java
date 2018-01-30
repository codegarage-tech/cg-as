package com.rc.abovesound.model;

/**
 * Md. Rashadul Alam
 */
public class SpinnerItem {

    private String id = "";
    private String name = "";

    public SpinnerItem(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public SpinnerItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
