package com.reversecoder.mh.model;

import java.util.ArrayList;

/**
 * Md. Rashadul Alam
 */
public class MusicCategoryData extends ResponseBase {

    private ArrayList<MusicCategory> data;

    public MusicCategoryData(ArrayList<MusicCategory> data) {
        this.data = data;
    }

    private ArrayList<MusicCategory> getData() {
        return data;
    }

    private void setData(ArrayList<MusicCategory> data) {
        this.data = data;
    }

    public ArrayList<SpinnerItem> getMusicCategory() {
        ArrayList<SpinnerItem> mCategory = new ArrayList<SpinnerItem>();
        for (int i = 0; i < data.size(); i++) {
            mCategory.add(new SpinnerItem(data.get(i).getId(), data.get(i).getName()));
        }
        if (!isItemExist(mCategory, "Choose music category")) {
            mCategory.add(0, new SpinnerItem("42343434343", "Choose music category"));
        }
        return mCategory;
    }

    private boolean isItemExist(ArrayList<SpinnerItem> data, String itemName) {
        boolean isExist = false;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getName().equalsIgnoreCase(itemName)) {
                isExist = true;
                return isExist;
            }
        }
        return isExist;
    }

    @Override
    public String toString() {
        return "{" +
                "data=" + data +
                '}';
    }
}
