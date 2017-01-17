package com.theah64.ets.services;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.theah64.ets.model.SocketMessage;
import com.theah64.ets.utils.APIRequestBuilder;
import com.theah64.ets.utils.APIRequestGateway;
import com.theah64.ets.utils.APIResponse;
import com.theah64.ets.utils.NetworkUtils;
import com.theah64.ets.utils.OkHttpUtils;
import com.theah64.ets.utils.PermissionUtils;
import com.theah64.ets.utils.WebSocketHelper;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;

import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class LocationReporterService extends Service implements LocationListener, PermissionUtils.Callback {

    private static final String X = LocationReporterService.class.getSimpleName();
    private String apiKey, empId;

    public LocationReporterService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(X, "Location reporter started...");
        Log.d(X, "Google api client connected");

        apiKey = intent.getStringExtra(APIRequestGateway.KEY_API_KEY);
        empId = intent.getStringExtra(SocketMessage.KEY_EMPLOYEE_ID);

        sendSocketMessage("Location request received", false);
        new PermissionUtils(this, this, null).begin();
        return START_STICKY;
    }

    private void sendSocketMessage(String text, boolean isError) {
        try {
            final SocketMessage message = new SocketMessage(text, isError, empId);
            WebSocketHelper.getInstance().send(message);
        } catch (URISyntaxException | IOException | JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @SuppressWarnings("MissingPermission")
    private void doNormalWork() {
        Log.d(X, "Requesting location");

        final LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            sendSocketMessage("Requesting GPS location", false);
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
        } else {
            sendSocketMessage("GPS not enabled", false);
        }
    }


    @Override
    public void onLocationChanged(final Location location) {

        Log.i(X, "Location retrieved: " + location);
        final String latitude = String.valueOf(location.getLatitude());
        final String longitude = String.valueOf(location.getLongitude());

        Log.i(X, String.format("Lat:%s Lon:%s", latitude, longitude));

        if (NetworkUtils.hasNetwork(this)) {

            try {
                final SocketMessage socketMessage = new SocketMessage(
                        "Location collected", false, empId, SocketMessage.TYPE_LOCATION, latitude, longitude);
                WebSocketHelper.getInstance().send(socketMessage);
            } catch (JSONException | IOException | URISyntaxException e) {
                e.printStackTrace();
            }

            final Request locRepReq = new APIRequestBuilder("/report_location", apiKey)
                    .addParam("lat", latitude)
                    .addParam("lon", longitude)
                    .addParam("device_time", DateFormat.getDateTimeInstance().format(new Date()))
                    .build();

            OkHttpUtils.getInstance().getClient().newCall(locRepReq).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        new APIResponse(OkHttpUtils.logAndGetStringBody(response));
                        sendSocketMessage("Location saved", false);
                    } catch (APIResponse.APIException | JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        } else {
            Log.e(X, "No network");
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        Log.d(X, "GPS enabled");
        sendSocketMessage("GPS enabled", false);
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.e(X, "GPS disabled");
        sendSocketMessage("GPS disabled", true);
    }

    @Override
    public void onAllPermissionGranted() {
        doNormalWork();
    }

    @Override
    public void onPermissionDenial() {
        sendSocketMessage("Permissions are not accepted", true);
    }
}
