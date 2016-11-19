package com.theah64.ets.services.firebase;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.theah64.ets.asyncs.FCMSynchronizer;
import com.theah64.ets.model.Employee;
import com.theah64.ets.utils.APIRequestGateway;
import com.theah64.ets.utils.PrefUtils;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class InstanceIdService extends FirebaseInstanceIdService {

    private static final String X = InstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {

        final String newFcmId = FirebaseInstanceId.getInstance().getToken();
        Log.i(X, "Firebase token refreshed : " + newFcmId);

        final SharedPreferences.Editor prefEditor = PrefUtils.getInstance(this).getEditor();
        prefEditor.putString(Employee.KEY_FCM_ID, newFcmId);
        prefEditor.putBoolean(PrefUtils.KEY_IS_FCM_SYNCED, false);
        prefEditor.commit();

        new APIRequestGateway(this, new APIRequestGateway.APIRequestGatewayCallback() {
            @Override
            public void onReadyToRequest(String apiKey) {
                new FCMSynchronizer(InstanceIdService.this, apiKey).execute();
            }

            @Override
            public void onFailed(String reason) {

            }
        });
    }
}
