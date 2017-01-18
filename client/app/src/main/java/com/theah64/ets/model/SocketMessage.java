package com.theah64.ets.model;

import android.content.Context;

import com.theah64.ets.utils.APIResponse;
import com.theah64.ets.utils.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

/**
 * Created by theapache64 on 17/1/17.
 */

public class SocketMessage {

    private static final String KEY_COMPANY_ID = "company_id";
    public static final String KEY_EMPLOYEE_ID = "employee_id";
    private static final String KEY_TYPE = "type";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LON = "lon";
    private static final String KEY_DEVICE_TIME = "device_time";

    public static final String TYPE_LOCATION = "location";
    private static final String TYPE_MESSAGE = "message";
    private static final String TYPE_SEARCHING_FOR_SATELLITE = "satellite";

    private final JSONObject joSocketMessage;

    public SocketMessage(String message, boolean isError, String empId, String type, String lat, String lon) throws JSONException, IOException {

        joSocketMessage = new JSONObject();
        joSocketMessage.put(APIResponse.KEY_MESSAGE, message);
        joSocketMessage.put(APIResponse.KEY_ERROR, isError);

        final JSONObject joData = new JSONObject();
        joData.put(KEY_COMPANY_ID, App.getCompanyId());

        if (!isError) {

            joData.put(KEY_EMPLOYEE_ID, empId);
            joData.put(KEY_TYPE, type);

            if (type.equals(TYPE_LOCATION)) {
                joData.put(KEY_LAT, lat);
                joData.put(KEY_LON, lon);
                joData.put(KEY_DEVICE_TIME, DateFormat.getDateTimeInstance().format(new Date()));
            }
        }

        joSocketMessage.put(APIResponse.KEY_DATA, joData);

    }

    public SocketMessage(final String message, final String empId) throws IOException, JSONException {
        this(message, false, empId, TYPE_MESSAGE, null, null);
    }

    public SocketMessage(final String message, boolean isError, final String empId) throws IOException, JSONException {
        this(message, isError, empId, TYPE_MESSAGE, null, null);
    }

    @Override
    public String toString() {
        return joSocketMessage.toString();
    }
}
