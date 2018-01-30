package com.rc.abovesound.model;

import java.util.ArrayList;

/**
 * Md. Rashadul Alam
 */
public class MusicCategoryData extends ResponseBase {

    private ArrayList<MusicCategory> data;

    public MusicCategoryData(ArrayList<MusicCategory> data) {
        this.data = data;
    }

    public ArrayList<MusicCategory> getMusicCategory() {
        return data;
    }

    private void setMusicCategory(ArrayList<MusicCategory> data) {
        this.data = data;
    }

//    public ArrayList<MusicCategory> getMusicCategory() {
//        ArrayList<MusicCategory> mCategory = new ArrayList<MusicCategory>();
//        for (int i = 0; i < data.size(); i++) {
//            mCategory.add(new MusicCategory(data.get(i).getId(), data.get(i).getName()));
//        }
//        if (!isItemExist(mCategory, "Choose music category")) {
//            mCategory.add(0, new MusicCategory("42343434343", "Choose music category"));
//        }
//        return mCategory;
//    }

    private boolean isItemExist(ArrayList<MusicCategory> data, String itemName) {
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
