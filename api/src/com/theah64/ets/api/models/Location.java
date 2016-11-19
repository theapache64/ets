package com.theah64.ets.api.models;

/**
 * Created by theapache64 on 19/11/16,2:58 PM.
 */
public class Location {
    private final String empId, lat, lon;

    public Location(String empId, String lat, String lon) {
        this.empId = empId;
        this.lat = lat;
        this.lon = lon;
    }

    public String getEmpId() {
        return empId;
    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }
}
