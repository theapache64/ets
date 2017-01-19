package com.theah64.ets.api.database.tables;

import com.theah64.ets.api.database.Connection;
import com.theah64.ets.api.models.Employee;
import org.json.JSONArray;
import org.json.JSONException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by theapache64 on 18/11/16,1:31 AM.
 * id          INT         NOT NULL AUTO_INCREMENT,
 * company_id  INT         NOT NULL,
 * name        VARCHAR(20) NOT NULL,
 * imei        VARCHAR(20) NOT NULL,
 * device_hash TEXT        NOT NULL,
 * fcm_id      TEXT        NOT NULL,
 * api_key     VARCHAR(20) NOT NULL,
 * is_active   TINYINT(4)  NOT NULL DEFAULT 1,
 * created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
 * PRIMARY KEY (id),
 * UNIQUE KEY (api_key),
 * FOREIGN KEY (company_id) REFERENCES companies (id)
 * ON UPDATE CASCADE
 * ON DELETE CASCADE
 */
public class Employees extends BaseTable<Employee> {

    private static final Employees instance = new Employees();
    public static final String COLUMN_API_KEY = "api_key";
    public static final String COLUMN_IMEI = "imei";
    public static final String COLUMN_FCM_ID = "fcm_id";
    public static final String COLUMN_DEVICE_HASH = "device_hash";
    public static final String COLUMN_CODE = "code";
    public static final String COLUMN_COMPANY_ID = "company_id";

    private Employees() {
        super("employees");
    }

    public static Employees getInstance() {
        return instance;
    }

    @Override
    public void add(Employee newEmp) throws InsertFailedException {

    }

    @Override
    public String addv3(Employee newEmp) throws InsertFailedException {
        String empId = null;
        final String query = "INSERT INTO employees (company_id, name, imei,device_hash, fcm_id, api_key,code) VALUES (?,?,?,?,?,?,?); ";

        final java.sql.Connection con = Connection.getConnection();

        try {

            final PreparedStatement ps = con.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setString(1, newEmp.getCompanyId());
            ps.setString(2, newEmp.getName());
            ps.setString(3, newEmp.getImei());
            ps.setString(4, newEmp.getDeviceHash());
            ps.setString(5, newEmp.getFcmId());
            ps.setString(6, newEmp.getApiKey());
            ps.setString(7, newEmp.getEmpCode());
            ps.executeUpdate();

            final ResultSet rs = ps.getGeneratedKeys();
            if (rs.first()) {
                empId = rs.getString(1);
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

        if (empId == null) {
            throw new InsertFailedException("Failed to add new employee");
        }
        return empId;
    }

    @Override
    public Employee get(String column1, String value1) {
        Employee emp = null;
        final String query = String.format("SELECT id,name, fcm_id, api_key,is_active FROM employees WHERE %s = ?", column1);
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, value1);

            final ResultSet rs = ps.executeQuery();

            if (rs.first()) {
                final String id = rs.getString(COLUMN_ID);
                final String name = rs.getString(COLUMN_NAME);
                final String fcmId = rs.getString(COLUMN_FCM_ID);
                final String apiKey = rs.getString(COLUMN_API_KEY);
                final boolean isActive = rs.getBoolean(COLUMN_IS_ACTIVE);

                emp = new Employee(id, name, null, null, fcmId, apiKey, null, null, null, isActive);
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
        return emp;
    }

    public List<Employee> get(String whereInColumn, JSONArray whereInValues) throws JSONException {

        List<Employee> employeeList = null;

        final StringBuilder queryBuilder = new StringBuilder(String.format("SELECT name, code, fcm_id,is_active FROM employees WHERE %s IN (", whereInColumn));

        for (int i = 0; i < whereInValues.length(); i++) {
            queryBuilder.append("'").append(whereInValues.getString(i)).append("'");

            if (i < (whereInValues.length() - 1)) {
                queryBuilder.append(",");
            } else {
                queryBuilder.append(");");
            }
        }

        final java.sql.Connection con = Connection.getConnection();
        try {
            final Statement stmt = con.createStatement();
            final ResultSet rs = stmt.executeQuery(queryBuilder.toString());

            if (rs.first()) {
                employeeList = new ArrayList<>();
                do {

                    final String name = rs.getString(COLUMN_NAME);
                    final String empCode = rs.getString(COLUMN_CODE);
                    final String fcmId = rs.getString(COLUMN_FCM_ID);
                    final boolean isActive = rs.getBoolean(COLUMN_IS_ACTIVE);

                    employeeList.add(new Employee(null, name, null, null, fcmId, null, null, empCode, null, isActive));

                } while (rs.next());
            }

            rs.close();
            stmt.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        return employeeList;
    }

    public List<Employee> getAllFireableEmployees(String companyId, boolean isLastKnownLocationNeeded) {

        List<Employee> employeeList = null;

        final String query = "SELECT e.id,e.name,e.imei,e.code, e.fcm_id,e.is_active FROM employees e WHERE e.company_id = ? AND !ISNULL(e.fcm_id) ORDER BY id DESC;";
        final java.sql.Connection con = Connection.getConnection();

        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, companyId);

            final ResultSet rs = ps.executeQuery();

            if (rs.first()) {
                employeeList = new ArrayList<>();
                final LocationHistories locationHistories = LocationHistories.getInstance();
                do {

                    final String id = rs.getString(COLUMN_ID);
                    final String imei = rs.getString(COLUMN_IMEI);
                    final String name = rs.getString(COLUMN_NAME);
                    final String empCode = rs.getString(COLUMN_CODE);
                    final String fcmId = rs.getString(COLUMN_FCM_ID);
                    final boolean isActive = rs.getBoolean(COLUMN_IS_ACTIVE);

                    employeeList.add(new Employee(
                            id, name, imei, null,
                            fcmId, null, null, empCode, isLastKnownLocationNeeded ? locationHistories.getLastKnownLocation(id) : null, isActive));

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

        return employeeList;
    }

}
