package com.theah64.ets.api.database.tables;

import com.theah64.ets.api.models.Employee;

/**
 * Created by theapache64 on 18/11/16,1:31 AM.
 */
public class Employees extends BaseTable<Employee> {

    private static final Employees instance = new Employees();
    public static final String COLUMN_API_KEY = "api_key";
    public static final String COLUMN_IMEI = "imei";
    public static final String COLUMN_FCM_ID = "fcm_id";
    public static final String COLUMN_DEVICE_HASH = "device_hash";

    private Employees() {
        super("employee");
    }

    public static Employees getInstance() {
        return instance;
    }

}
