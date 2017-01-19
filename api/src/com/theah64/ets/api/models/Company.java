package com.theah64.ets.api.models;

/**
 * Created by theapache64 on 18/11/16.
 */
public class Company {
    public static final String KEY = "company";
    public static String KEY_COMPANY_CODE = "company_code";
    private final String id, name, code,password;
    private final boolean isActive;

    public Company(String id, String name, String code, String password, boolean isActive) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.password = password;
        this.isActive = isActive;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
