package com.reversecoder.mh.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FilterData {
    private List<Music> mList = new ArrayList<>();

    public FilterData(List<Music> mList) {
        this.mList = mList;
    }

    public List<Music> getAllMusics() {
        return mList;
    }

    public void setList(List<Music> mList) {
        this.mList = mList;
    }

    public List<String> getUniqueCategoryKeys() {
        List<String> categories = new ArrayList<>();
        for (Music music : mList) {
            if (!categories.contains(music.getMusic_category())) {
                categories.add(music.getMusic_category());
            }
        }
        Collections.sort(categories);
        return categories;
    }

    public List<String> getUniqueStateKeys() {
        List<String> states = new ArrayList<>();
        for (Music music : mList) {
            if (!states.contains(music.getState_name())) {
                states.add(music.getState_name());
            }
        }
        Collections.sort(states);
        return states;
    }

    public List<Music> getCategoryFilteredMusics(List<String> categories, List<Music> mList) {
        List<Music> tempList = new ArrayList<>();
        for (Music music : mList) {
            for (String category : categories) {
                if (music.getMusic_category().equalsIgnoreCase(category)) {
                    tempList.add(music);
                }
            }
        }
        return tempList;
    }

    public List<Music> getStateFilteredMusics(List<String> states, List<Music> mList) {
        List<Music> tempList = new ArrayList<>();
        for (Music music : mList) {
            for (String state : states) {
                if (music.getState_name().equalsIgnoreCase(state)) {
                    tempList.add(music);
                }
            }
        }
        return tempList;
    }
}
