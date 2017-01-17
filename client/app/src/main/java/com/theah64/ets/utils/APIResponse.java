package com.theah64.ets.utils;

import android.support.annotation.StringRes;

import org.acra.ACRA;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shifar on 23/7/16.
 */
public class APIResponse {

    public static final String KEY_MESSAGE = "message";
    public static final String KEY_ERROR = "error";
    public static final String KEY_ERROR_CODE = "error_code";
    public static final String KEY_DATA = "data";
    private final String message;
    private final JSONObject joMain;

    public APIResponse(final String stringResp) throws APIException, JSONException {

        try {
            joMain = new JSONObject(stringResp);
            this.message = joMain.getString(KEY_MESSAGE);

            if (joMain.getBoolean(KEY_ERROR)) {
                final int errorCode = joMain.getInt(KEY_ERROR_CODE);
                throw new APIException(errorCode, message);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            ACRA.getErrorReporter().handleException(e);
            throw e;
        }

    }

    JSONObject getJSONObjectData() throws JSONException {
        return joMain.getJSONObject(KEY_DATA);
    }


    public String getMessage() {
        return this.message;
    }

    public static class APIException extends Exception {

        private final int pleasantMsg;

        APIException(final int errorCode, String msg) {
            super(msg);
            pleasantMsg = getPleasantMessage(errorCode);
        }

        private static
        @StringRes
        int getPleasantMessage(int errorCode) {

            switch (errorCode) {

                default:
                    return -1;
            }

        }

        @StringRes
        public int getPleasantMsg() {
            return pleasantMsg;
        }
    }

}
