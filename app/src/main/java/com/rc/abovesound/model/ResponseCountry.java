package com.rc.abovesound.model;

import java.util.ArrayList;

/**
 * Md. Rashadul Alam
 */
public class ResponseCountry extends ResponseBase {

    private String status = "";
    private ArrayList<Country> data;

    public ResponseCountry(String status, ArrayList<Country> data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<Country> getData() {
        return data;
    }

    public ArrayList<TimeZone> getTimezone(String countryName) {
        ArrayList<TimeZone> mTimeZone = new ArrayList<TimeZone>();
        if (isCountryExist(data, countryName)) {
            Country country = getCountry(data, countryName);
            mTimeZone = country.getTimezone();
            TimeZone defaultTimeZone = new TimeZone("42343434343", "Choose your timezone", new ArrayList<City>() {{
                add(new City("45433545345", "Choose your city"));
            }});
            if (!isTimeZoneExist(mTimeZone, defaultTimeZone.getName())) {
                mTimeZone.add(0, defaultTimeZone);
            }
        }
        return mTimeZone;
    }

    public TimeZone getTimezone(String countryName, String cityName) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getName().equalsIgnoreCase(countryName)) {
                ArrayList<TimeZone> timeZones = data.get(i).getTimezone();
                for (int j = 0; j < timeZones.size(); j++) {
                    ArrayList<City> cities = timeZones.get(j).getCity();
                    for (int k = 0; k < cities.size(); k++) {
                        if (cities.get(k).getName().equalsIgnoreCase(cityName)) {
                            TimeZone timeZone = timeZones.get(j);
                            return timeZone;
                        }
                    }
                }
            }
        }
        return null;
    }

    public TimeZone getAnyTimezone(String timeZoneName) {
        for (int i = 0; i < data.size(); i++) {
            ArrayList<TimeZone> timeZones = data.get(i).getTimezone();
            for (int j = 0; j < timeZones.size(); j++) {
                if (timeZones.get(j).getName().equalsIgnoreCase(timeZoneName)) {
                    return timeZones.get(j);
                }
            }
        }
        return null;
    }

    public ArrayList<City> getCity(String countryName, String timeZone) {
        ArrayList<City> mCity = new ArrayList<City>();
        if (isCountryExist(data, countryName)) {
            Country country = getCountry(data, countryName);
            if (isTimeZoneExist(country.getTimezone(), timeZone)) {
                TimeZone mTimeZone = getTimeZone(country.getTimezone(), timeZone);
                mCity = mTimeZone.getCity();
                City defaultCity = new City("45433545345", "Choose your city");
                if (!isCityExist(mCity, defaultCity)) {
                    mCity.add(0, defaultCity);
                }
            }
        }
        return mCity;
    }

    public void setData(ArrayList<Country> data) {
        this.data = data;
    }

    private boolean isCountryExist(ArrayList<Country> data, String countryName) {
        boolean isExist = false;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getName().equalsIgnoreCase(countryName)) {
                isExist = true;
                return isExist;
            }
        }
        return isExist;
    }

    private TimeZone getTimeZone(ArrayList<TimeZone> data, String timeZoneName) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getName().equalsIgnoreCase(timeZoneName)) {
                return data.get(i);
            }
        }
        return null;
    }

    private boolean isTimeZoneExist(ArrayList<TimeZone> data, String timeZoneName) {
        boolean isExist = false;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getName().equalsIgnoreCase(timeZoneName)) {
                isExist = true;
                return isExist;
            }
        }
        return isExist;
    }

    private Country getCountry(ArrayList<Country> data, String countryName) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getName().equalsIgnoreCase(countryName)) {
                return data.get(i);
            }
        }
        return null;
    }

    private boolean isCityExist(ArrayList<City> data, City city) {
        boolean isExist = false;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getName().equalsIgnoreCase(city.getName())) {
                isExist = true;
                return isExist;
            }
        }
        return isExist;
    }

    private City getCity(ArrayList<City> data, String cityName) {
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getName().equalsIgnoreCase(cityName)) {
                return data.get(i);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
