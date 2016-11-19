package com.theah64.ets.api.database.tables;

import com.theah64.ets.api.database.Connection;
import com.theah64.ets.api.models.Location;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by theapache64 on 19/11/16,2:58 PM.
 */
public class LocationHistories extends BaseTable<Location> {

    private static final LocationHistories instance = new LocationHistories();
    public static final String COLUMN_LATITUDE = "lat";
    public static final String COLUMN_LONGITUDE = "lon";

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
        final String query = "INSERT INTO location_histories (employee_id, lat, lon) VALUES (?,?,?);";
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, location.getEmpId());
            ps.setString(2, location.getLat());
            ps.setString(3, location.getLon());
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
}

