package com.theah64.ets.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.theah64.ets.model.Employee;

import org.acra.ACRA;
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

    private static String getDeviceName() {
        final String manufacturer = Build.MANUFACTURER;
        final String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return model.toUpperCase();
        } else {
            return manufacturer.toUpperCase() + " " + model;
        }
    }



    public interface APIRequestGatewayCallback {
        void onReadyToRequest(final String apiKey);

        void onFailed(final String reason);
    }

    private final Context context;
    @NonNull
    private final APIRequestGatewayCallback callback;

    public APIRequestGateway(Context context, @NonNull APIRequestGatewayCallback callback) {
        this.context = context;
        this.callback = callback;
        execute();
    }


    private void register(final Context context) throws IOException, JSONException {

        final ProfileUtils profileUtils = ProfileUtils.getInstance(context);

        final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

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

        final String finalFcmId = fcmId;

        //Attaching them with the request
        final Request inRequest = new APIRequestBuilder("/get_api_key")
                .addParam("company_code", App.getCompanyCode(context))
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
                callback.onFailed(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {

                    final APIResponse inResp = new APIResponse(OkHttpUtils.logAndGetStringBody(response));
                    final String apiKey = inResp.getJSONObjectData().getString(KEY_API_KEY);


                    //Saving in preference
                    final SharedPreferences.Editor editor = prefUtils.getEditor();
                    editor.putString(KEY_API_KEY, apiKey);
                    editor.putBoolean(Employee.KEY_IS_FCM_SYNCED, true);

                    editor.commit();

                    callback.onReadyToRequest(apiKey);

                } catch (JSONException | APIResponse.APIException e) {
                    e.printStackTrace();

                    callback.onFailed(e.getMessage());

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

                callback.onReadyToRequest(apiKey);

            } else {

                Log.i(X, "Registering victim...");

                //Register victim here
                try {
                    register(context);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    Log.e(X, "Failed to signup employee");
                    ACRA.getErrorReporter().handleException(e);
                }
            }

        } else {
            callback.onFailed("No network!");
            Log.e(X, "Doesn't have APIKEY and no network!");

        }
    }
}
