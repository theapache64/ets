package com.theah64.ets.api.models;

/**
 * Created by theapache64 on 19/11/16,2:58 PM.
 */
public class Location {

    private final String empId, lat, lon, deviceTime;

    public Location(String empId, String lat, String lon, String deviceTime) {
        this.empId = empId;
        this.lat = lat;
        this.lon = lon;
        this.deviceTime = deviceTime;
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

    public String getDeviceTime() {
        return deviceTime;
    }

    @Override
    public String toString() {
        return "Location{" +
                "empId='" + empId + '\'' +
                ", lat='" + lat + '\'' +
                ", lon='" + lon + '\'' +
                '}';
    }
}
