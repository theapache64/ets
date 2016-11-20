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

    private Employees() {
        super("employees");
    }

    public static Employees getInstance() {
        return instance;
    }

    @Override
    public void add(Employee newEmp) throws InsertFailedException {
        boolean isFailed = false;
        final String query = "INSERT INTO employees (company_id, name, imei,device_hash, fcm_id, api_key,code) VALUES (?,?,?,?,?,?,?); ";

        final java.sql.Connection con = Connection.getConnection();

        try {

            final PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, newEmp.getCompanyId());
            ps.setString(2, newEmp.getName());
            ps.setString(3, newEmp.getImei());
            ps.setString(4, newEmp.getDeviceHash());
            ps.setString(5, newEmp.getFcmId());
            ps.setString(6, newEmp.getApiKey());
            ps.setString(7, newEmp.getEmpCode());

            isFailed = ps.executeUpdate() != 1;
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
            isFailed = true;
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

    @Override
    public Employee get(String column1, String value1, String column2, String value2) {
        Employee emp = null;
        final String query = String.format("SELECT id, fcm_id, api_key FROM employees WHERE %s = ? AND %s = ?", column1, column2);
        final java.sql.Connection con = Connection.getConnection();
        try {
            final PreparedStatement ps = con.prepareStatement(query);

            ps.setString(1, value1);
            ps.setString(2, value2);

            final ResultSet rs = ps.executeQuery();

            if (rs.first()) {
                final String id = rs.getString(COLUMN_ID);
                final String fcmId = rs.getString(COLUMN_FCM_ID);
                final String apiKey = rs.getString(COLUMN_API_KEY);
                emp = new Employee(id, null, null, null, fcmId, apiKey, null, null);
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

        final StringBuilder queryBuilder = new StringBuilder(String.format("SELECT name, code, fcm_id FROM employees WHERE %s IN (", whereInColumn));

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

                    employeeList.add(new Employee(null, name, null, null, fcmId, null, null, empCode));

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

    public List<Employee> getAllFireableEmployees(String companyCode) {

        List<Employee> employeeList = null;

        final String query = "SELECT e.name,e.code, e.fcm_id FROM employees e INNER JOIN companies c ON e.company_id = c.id WHERE c.code = ? AND !ISNULL(e.fcm_id);";
        final java.sql.Connection con = Connection.getConnection();

        try {
            final PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, companyCode);

            final ResultSet rs = ps.executeQuery();

            if (rs.first()) {
                employeeList = new ArrayList<>();
                do {

                    final String name = rs.getString(COLUMN_NAME);
                    final String empCode = rs.getString(COLUMN_CODE);
                    final String fcmId = rs.getString(COLUMN_FCM_ID);

                    employeeList.add(new Employee(null, name, null, null, fcmId, null, null, empCode));

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
