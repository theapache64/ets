package com.theah64.ets.services.firebase;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.theah64.ets.model.SocketMessage;
import com.theah64.ets.services.LocationReporterService;
import com.theah64.ets.utils.APIRequestGateway;
import com.theah64.ets.utils.App;
import com.theah64.ets.utils.WebSocketHelper;

import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;
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
                new APIRequestGateway(this, new APIRequestGateway.APIRequestGatewayCallback() {
                    @Override
                    public void onReadyToRequest(String apiKey, String id) {
                        final Intent locReqIntent = new Intent(LocationRequestReceiverService.this, LocationReporterService.class);
                        locReqIntent.putExtra(APIRequestGateway.KEY_API_KEY, apiKey);
                        locReqIntent.putExtra(SocketMessage.KEY_EMPLOYEE_ID, id);

                        startService(locReqIntent);
                    }

                    @Override
                    public void onFailed(String reason) {

                    }
                });

            } else {
                //TODO: Manage [anything-else] here.
            }
        }
    }

}
