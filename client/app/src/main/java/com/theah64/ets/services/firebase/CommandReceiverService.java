package com.theah64.ets.services.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class CommandReceiverService extends FirebaseMessagingService {

    private static final String X = CommandReceiverService.class.getSimpleName();
    private static final String KEY_TYPE = "type";
    private static final String TYPE_LOCATION_REQUEST = "location_request";
    private static final String KEY_DATA = "data";
    private static final String KEY_TYPE_DATA = "type_data";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Map<String, String> payload = remoteMessage.getData();
        Log.i(X, "FCM says : " + payload);

        if (!payload.isEmpty()) {

            final String type = payload.get(KEY_TYPE);

            if (type.equals(TYPE_LOCATION_REQUEST)) {
                //TODO: location request received.
            } else {
                //TODO: Manage anything else here.
            }
        }
    }

}
