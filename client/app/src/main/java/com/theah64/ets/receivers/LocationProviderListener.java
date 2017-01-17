package com.theah64.ets.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.theah64.ets.model.SocketMessage;
import com.theah64.ets.services.LocationReporterService;
import com.theah64.ets.services.firebase.LocationRequestReceiverService;
import com.theah64.ets.utils.APIRequestGateway;
import com.theah64.ets.utils.NetworkUtils;
import com.theah64.ets.utils.PermissionUtils;
import com.theah64.ets.utils.WebSocketHelper;

public class LocationProviderListener extends BroadcastReceiver implements PermissionUtils.Callback {

    private static final String X = LocationProviderListener.class.getSimpleName();
    private Context context;

    public LocationProviderListener() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d(X, "Location provider changed : " + intent);
        this.context = context;
        new PermissionUtils(context, this, null).begin();
    }

    @Override
    public void onAllPermissionGranted() {

        Log.d(X, "GPS Enabled");
        new APIRequestGateway(context, new APIRequestGateway.APIRequestGatewayCallback() {
            @Override
            public void onReadyToRequest(String apiKey, String id) {

                final Intent locReqIntent = new Intent(context, LocationReporterService.class);
                locReqIntent.putExtra(APIRequestGateway.KEY_API_KEY, apiKey);
                locReqIntent.putExtra(SocketMessage.KEY_EMPLOYEE_ID, id);

                context.startService(locReqIntent);
            }

            @Override
            public void onFailed(String reason) {

            }
        });


    }

    @Override
    public void onPermissionDenial() {
        Log.e(X, "Permissions are not accepted");
    }
}
