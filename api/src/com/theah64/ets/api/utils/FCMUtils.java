package com.theah64.ets.api.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

/**
 * Created by theapache64 on 14/9/16,6:07 PM.
 */
public class FCMUtils {

    private static final String FCM_SEND_URL = "https://fcm.googleapis.com/fcm/send";


    public static final String KEY_TYPE = "type";
    public static final String TYPE_LOCATION_REQUEST = "location_request";
    public static final String KEY_DATA = "data";
    public static final String KEY_TO = "to";
    private static final String FCM_NOTIFICATION_KEY = "AIzaSyCq_V-Hu0qn4jZhdWosj3j5cRxjTc22R6s";
    private static final String KEY_REG_IDS = "registration_ids";

    public static boolean sendLocationRequest(final JSONArray jaFcmIds) {

        final JSONObject joFcm = new JSONObject();
        try {

            joFcm.put(jaFcmIds.length() == 1 ? FCMUtils.KEY_TO : FCMUtils.KEY_REG_IDS, jaFcmIds.length() == 1 ? jaFcmIds.get(0) : jaFcmIds);

            final JSONObject joData = new JSONObject();
            joData.put(FCMUtils.KEY_TYPE, FCMUtils.TYPE_LOCATION_REQUEST);
            joFcm.put(FCMUtils.KEY_DATA, joData);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return sendPayload(joFcm.toString());
    }

    private static boolean sendPayload(String payload) {


        try {
            final URL url = new URL(FCM_SEND_URL);
            final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.addRequestProperty("Authorization", "key=" + FCM_NOTIFICATION_KEY);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");

            OutputStream os = urlConnection.getOutputStream();
            os.write(payload.getBytes());
            os.flush();
            os.close();

            final BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            final StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line).append("\n");
            }
            
            br.close();
            final JSONObject joResp = new JSONObject(response.toString());
            final boolean isSent = joResp.getInt("failure") == 0;
            if (!isSent) {
                throw new IllegalArgumentException("FCM failed to send command : " + response);
            }
            return true;
        } catch (java.io.IOException e) {
            e.printStackTrace();
            return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }

    }


}
