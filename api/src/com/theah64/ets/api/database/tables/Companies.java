package com.theah64.ets.api.database.tables;

import com.theah64.ets.api.models.Company;

/**
 * Created by theapache64 on 18/11/16.
 */
public class Companies extends BaseTable<Company> {

    private static final Companies instance = new Companies();
    public static final String COLUMN_CODE = "code";

    private Companies() {
        super("companies");
    }

    public static Companies getInstance() {
        return instance;
    }
}
