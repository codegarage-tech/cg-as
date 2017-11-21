package com.reversecoder.mh.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by krupenghetiya on 28/06/17.
 */

public class FilterData {
    private List<Music> mList = new ArrayList<>();

    public FilterData(List<Music> mList) {
        this.mList = mList;
    }

    public List<Music> getAllMovies() {
        return mList;
    }

    public void setmList(List<Music> mList) {
        this.mList = mList;
    }

    public List<Music> getGenreFilteredMovies(List<String> genre, List<Music> mList) {
        List<Music> tempList = new ArrayList<>();
        for (Music movie : mList) {
            for (String g : genre) {
                if (movie.getGenre().equalsIgnoreCase(g)) {
                    tempList.add(movie);
                }
            }

        }
        return tempList;
    }

    public List<Music> getYearFilteredMovies(List<String> yearstr, List<Music> mList) {
        List<SingleMovie> tempList = new ArrayList<>();
        for (SingleMovie movie : mList) {
            for (String y : yearstr) {
                if (movie.getYear() == Integer.parseInt(y)) {
                    tempList.add(movie);
                }
            }
        }
        return tempList;
    }

    public List<Music> getQualityFilteredMovies(List<String> quality, List<Music> mList) {
        List<Music> tempList = new ArrayList<>();
        for (Music movie : mList) {
            for (String q : quality) {
                if (movie.getQuality().equalsIgnoreCase(q)) {
                    tempList.add(movie);
                }
            }
        }
        return tempList;
    }

    public List<Music> getRatingFilteredMovies(List<String> rating, List<Music> mList) {
        List<Music> tempList = new ArrayList<>();
        for (Music movie : mList) {
            for (String r : rating) {
                if (movie.getRating() >= Float.parseFloat(r.replace(">",""))) {
                    tempList.add(movie);
                }
            }
        }
        return tempList;
    }

    public List<String> getUniqueGenreKeys() {
        List<String> genres = new ArrayList<>();
        for (Music movie : mList) {
            if (!genres.contains(movie.getGenre())) {
                genres.add(movie.getGenre());
            }
        }
        Collections.sort(genres);
        return genres;
    }

    public List<String> getUniqueYearKeys() {
        List<String> years = new ArrayList<>();
        for (Music movie : mList) {
            if (!years.contains(movie.getYear() + "")) {
                years.add(movie.getYear() + "");
            }
        }
        Collections.sort(years);
        return years;
    }

    public List<String> getUniqueQualityKeys() {
        List<String> qualities = new ArrayList<>();
        for (Music movie : mList) {
            if (!qualities.contains(movie.getQuality())) {
                qualities.add(movie.getQuality());
            }
        }
        Collections.sort(qualities);
        return qualities;
    }

    public List<String> getUniqueRatingKeys() {
        List<String> ratings = new ArrayList<>();
        for (Music movie : mList) {
            int rating = (int) Math.floor(movie.getRating());
            String rate = "> " + rating;
            if (!ratings.contains(rate)) {
                ratings.add(rate);
            }
        }
        Collections.sort(ratings);
        return ratings;
    }

}
