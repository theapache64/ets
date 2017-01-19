package com.theah64.ets.api.database.tables;

import com.sun.istack.internal.Nullable;
import com.theah64.ets.api.database.Connection;
import com.theah64.ets.api.models.Company;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by theapache64 on 18/11/16.
 */
public class Companies extends BaseTable<Company> {

    private static final Companies instance = new Companies();
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String KEY_C_PASSWORD = "cpassword";

    private Companies() {
        super("companies");
    }

    public static Companies getInstance() {
        return instance;
    }

    @Override
    public Company get(String column, String value) {
        return get(column, value, null, null);
    }

    @Override
    public Company get(String column1, String value1, @Nullable String column2, @Nullable String value2) {
        Company company = null;
        final String query;

        final boolean isSingleModeQuery = column2 == null || value2 == null;

        if (isSingleModeQuery) {
            query = String.format("SELECT id,password,name,code,is_active FROM companies WHERE %s = ?;", column1);
        } else {
            query = String.format("SELECT id,password,name,code,is_active FROM companies WHERE %s = ? AND %s = ?;", column1, column2);
        }

        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);

            if (isSingleModeQuery) {
                ps.setString(1, value1);
            } else {
                ps.setString(1, value1);
                ps.setString(2, value2);
            }

            final ResultSet rs = ps.executeQuery();

            if (rs.first()) {
                final String id = rs.getString(COLUMN_ID);
                final String password = rs.getString(COLUMN_PASSWORD);
                final String name = rs.getString(COLUMN_NAME);
                final String code = rs.getString(COLUMN_CODE);
                final boolean isActive = rs.getBoolean(COLUMN_IS_ACTIVE);

                company = new Company(id, name, code, password, isActive);
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
        return company;
    }
}
