package com.theah64.ets.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.theah64.ets.services.StickyService;

public class RestartServiceReceiver extends BroadcastReceiver {
    private static final String X = RestartServiceReceiver.class.getSimpleName();

    public RestartServiceReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(X, "Restarting sticky service");
        context.startService(new Intent(context.getApplicationContext(), StickyService.class));
    }
}
