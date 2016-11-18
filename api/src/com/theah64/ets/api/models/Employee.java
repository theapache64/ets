package com.theah64.ets.api.models;

/**
 * Created by theapache64 on 18/11/16,1:32 AM.
 * id          INT         NOT NULL AUTO_INCREMENT,
 * company_id  INT         NOT NULL,
 * name        VARCHAR(20) NOT NULL,
 * imei        VARCHAR(20) NOT NULL,
 * device_hash TEXT        NOT NULL,
 * fcm_id      TEXT        NOT NULL,
 * api_key     VARCHAR(20) NOT NULL,
 * is_active   TINYINT(4)  NOT NULL DEFAULT 1,
 * created_at  TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
 */
public class Employee {
    private final String id, name, imei, deviceHash, fcmId, apiKey, companyId;

    public Employee(String id, String name, String imei, String deviceHash, String fcmId, String apiKey, String companyId) {
        this.id = id;
        this.name = name;
        this.imei = imei;
        this.deviceHash = deviceHash;
        this.fcmId = fcmId;
        this.apiKey = apiKey;
        this.companyId = companyId;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImei() {
        return imei;
    }

    public String getDeviceHash() {
        return deviceHash;
    }

    public String getFcmId() {
        return fcmId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getCompanyId() {
        return companyId;
    }
}
