package com.theah64.ets.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.theah64.ets.services.StickyService;

public class BootReceiver extends BroadcastReceiver {
    private static final String X = BootReceiver.class.getSimpleName();

    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(X, "Boot recieved from system");
        context.startService(new Intent(context.getApplicationContext(), StickyService.class));
    }
}
