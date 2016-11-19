package com.theah64.ets.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.theah64.ets.utils.APIRequestBuilder;
import com.theah64.ets.utils.APIRequestGateway;
import com.theah64.ets.utils.APIResponse;
import com.theah64.ets.utils.NetworkUtils;
import com.theah64.ets.utils.OkHttpUtils;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class LocationReporterService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String X = LocationReporterService.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;

    public LocationReporterService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(X, "Location reporter started...");

        //buildGoogleApiClient();
        mGoogleApiClient = new
                GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();


        mGoogleApiClient.connect();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

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

    }

    @SuppressWarnings("MissingPermission")
    private void doNormalWork() {

        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);

            Log.d(X, "Location requested");

        } else {
            Log.e(X, "Location provider not enabled");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(X, "Connection suspended " + i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(X, "Connection failed");
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


        //removeLocationUpdates();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, LocationReporterService.this);
    }


}
