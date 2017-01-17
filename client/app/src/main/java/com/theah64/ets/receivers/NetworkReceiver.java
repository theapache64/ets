package com.theah64.ets.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.theah64.ets.asyncs.FCMSynchronizer;
import com.theah64.ets.model.Employee;
import com.theah64.ets.utils.APIRequestGateway;
import com.theah64.ets.utils.NetworkUtils;
import com.theah64.ets.utils.PrefUtils;


public class NetworkReceiver extends BroadcastReceiver {

    private static final String X = NetworkReceiver.class.getSimpleName();

    public NetworkReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                doNormalWork(context);
            } else {
                Log.d(X, "Permission not yet granted");
            }

        } else {
            doNormalWork(context);
        }


    }

    private static void doNormalWork(final Context context) {

        if (NetworkUtils.hasNetwork(context)) {

            if (!PrefUtils.getInstance(context).getBoolean(Employee.KEY_IS_FCM_SYNCED)) {

                new APIRequestGateway(context, new APIRequestGateway.APIRequestGatewayCallback() {

                    @Override
                    public void onReadyToRequest(String apiKey,final String id) {
                        new FCMSynchronizer(context, apiKey).execute();
                    }

                    @Override
                    public void onFailed(String reason) {
                        Log.e(X, "Reason: " + reason);
                    }
                });

            }
        }
    }
}
