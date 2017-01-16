package com.theah64.ets.services;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.theah64.ets.utils.APIRequestBuilder;
import com.theah64.ets.utils.APIRequestGateway;
import com.theah64.ets.utils.APIResponse;
import com.theah64.ets.utils.NetworkUtils;
import com.theah64.ets.utils.OkHttpUtils;
import com.theah64.ets.utils.WebSocketHelper;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class LocationReporterService extends Service implements android.location.LocationListener {

    private static final String X = LocationReporterService.class.getSimpleName();
    private WebSocketClient webSocketClient;

    public LocationReporterService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(X, "Location reporter started...");

        Log.d(X, "Google api client connected");
        

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                doNormalWork();
            } else {
                Log.d(X, "Permission not yet granted");
            }

        } else {
            doNormalWork();
        }

        return START_STICKY;
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
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null);
    }


    @Override
    public void onLocationChanged(final Location location) {

        Log.i(X, "Location retrieved: " + location);
        Log.i(X, String.format("Lat:%s Lon:%s", location.getLatitude(), location.getLongitude()));

        if (NetworkUtils.hasNetwork(this)) {

            new APIRequestGateway(this, new APIRequestGateway.APIRequestGatewayCallback() {
                @Override
                public void onReadyToRequest(String apiKey) {

                    final Request locRepReq = new APIRequestBuilder("/report_location", apiKey)
                            .addParam("lat", String.valueOf(location.getLatitude()))
                            .addParam("lon", String.valueOf(location.getLongitude()))
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
                            } catch (APIResponse.APIException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }

                @Override
                public void onFailed(String reason) {

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
    }

    @Override
    public void onProviderDisabled(String s) {
        Log.e(X, "GPS disabled");
    }

}
