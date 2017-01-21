package com.theah64.ets.api.database.tables;

import com.theah64.ets.api.database.Connection;
import com.theah64.ets.api.models.Location;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by theapache64 on 19/11/16,2:58 PM.
 */
public class LocationHistories extends BaseTable<Location> {

    private static final LocationHistories instance = new LocationHistories();
    public static final String COLUMN_LATITUDE = "lat";
    public static final String COLUMN_LONGITUDE = "lon";
    public static final String COLUMN_DEVICE_TIME = "device_time";
    public static final String COLUMN_EMPLOYEE_ID = "employee_id";

    private LocationHistories() {
        super("location_histories");
    }

    public static LocationHistories getInstance() {
        return instance;
    }

    @Override
    public void add(Location location) throws InsertFailedException {

        System.out.println("-------------------");
        System.out.println("New location : " + location);

        boolean isFailed = false;
        final String query = "INSERT INTO location_histories (employee_id, lat, lon,device_time) VALUES (?,?,?,?);";
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, location.getEmpId());
            ps.setString(2, location.getLat());
            ps.setString(3, location.getLon());
            ps.setString(4, location.getDeviceTime());

            isFailed = ps.executeUpdate() != 1;
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (isFailed) {
            throw new InsertFailedException("Failed to add new employee");
        }
    }


    public Location getLastKnownLocation(String employeeId) {
        Location location = null;
        final String query = "SELECT lat,lon,device_time FROM location_histories WHERE employee_id = ? ORDER BY id DESC LIMIT 1";
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, employeeId);

            final ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                final String lat = rs.getString(COLUMN_LATITUDE);
                final String lon = rs.getString(COLUMN_LONGITUDE);
                final String deviceTime = rs.getString(COLUMN_DEVICE_TIME);

                location = new Location(null, null, lat, lon, deviceTime);
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return location;
    }

    @Override
    public List<Location> getAll(String whereColumn, String whereColumnValue) {
        List<Location> locations = null;
        final String query = String.format("SELECT id,lat,lon,device_time FROM location_histories WHERE %s = ? ORDER BY id DESC;", whereColumn);
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, whereColumnValue);

            final ResultSet rs = ps.executeQuery();

            if (rs.first()) {
                locations = new ArrayList<>();

                do {
                    final String id = rs.getString(COLUMN_ID);
                    final String lat = rs.getString(COLUMN_LATITUDE);
                    final String lon = rs.getString(COLUMN_LONGITUDE);
                    final String deviceTime = rs.getString(COLUMN_DEVICE_TIME);

                    locations.add(new Location(id, null, lat, lon, deviceTime));
                } while (rs.next());
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return locations;
    }

    public boolean delete(String employeeId, String companyId) {
        boolean isDeleted = false;
        final String query = "DELETE lh.* FROM location_histories lh INNER JOIN companies c ON c.id = ? WHERE lh.employee_id = ?";
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, companyId);
            ps.setString(2, employeeId);
            isDeleted = ps.executeUpdate() > 0;
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return isDeleted;
    }
}

