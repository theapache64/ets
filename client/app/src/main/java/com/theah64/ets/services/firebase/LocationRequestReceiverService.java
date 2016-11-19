package com.theah64.ets.services.firebase;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.theah64.ets.services.LocationReporterService;

import java.util.Map;

public class LocationRequestReceiverService extends FirebaseMessagingService {

    private static final String X = LocationRequestReceiverService.class.getSimpleName();
    private static final String KEY_TYPE = "type";
    private static final String TYPE_LOCATION_REQUEST = "location_request";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(X, "FCM sayssss: " + remoteMessage);
        Map<String, String> payload = remoteMessage.getData();
        Log.i(X, "FCM says : " + payload);

        if (!payload.isEmpty()) {

            final String type = payload.get(KEY_TYPE);

            if (type.equals(TYPE_LOCATION_REQUEST)) {
                startService(new Intent(this, LocationReporterService.class));
            } else {
                //TODO: Manage anything else here.
            }
        }
    }

}
