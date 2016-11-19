package com.theah64.ets.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.theah64.ets.services.LocationReporterService;

public class LocationProviderListener extends BroadcastReceiver {

    private static final String X = LocationProviderListener.class.getSimpleName();

    public LocationProviderListener() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(X, "Location provider changed : " + intent);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        final boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        System.out.println("GPS Enabled: " + isGPSEnabled);

        if (isGPSEnabled) {
            Log.d(X, "GPS Enabled");
            context.startService(new Intent(context, LocationReporterService.class));
        } else {
            Log.e(X, "GPS disabled");
        }
    }
}
