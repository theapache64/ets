package com.theah64.ets.activities;


import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.theah64.ets.R;
import com.theah64.ets.activities.base.PermissionActivity;
import com.theah64.ets.asyncs.FCMSynchronizer;
import com.theah64.ets.callbacks.AsyncCallback;
import com.theah64.ets.model.Employee;
import com.theah64.ets.model.SocketMessage;
import com.theah64.ets.utils.APIRequestGateway;
import com.theah64.ets.utils.App;
import com.theah64.ets.utils.NetworkUtils;
import com.theah64.ets.utils.PrefUtils;
import com.theah64.ets.utils.WebSocketHelper;

import org.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;


public class MainActivity extends PermissionActivity implements AsyncCallback {

    private TextView tvLog;
    private ProgressBar pbLoading;
    private Button bActivate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLog = (TextView) findViewById(R.id.tvLog);
        pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
        bActivate = (Button) findViewById(R.id.bActivate);

        bActivate.setVisibility(View.GONE);
        tvLog.setText(R.string.Waiting_for_permissions);

        // activate
        bActivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doNormalWork();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FCMSynchronizer.setCallback(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FCMSynchronizer.setCallback(null);
    }

    private void doNormalWork() {


        if (NetworkUtils.hasNetwork(this)) {

            new APIRequestGateway(
                    this,
                    new APIRequestGateway.APIRequestGatewayCallback() {
                        @Override
                        public void onReadyToRequest(String apiKey, final String id) {

                            onAsyncMessage(getString(R.string.Ready_to_Request));

                            if (!PrefUtils.getInstance(MainActivity.this).getBoolean(Employee.KEY_IS_FCM_SYNCED)) {

                                onAsyncMessage(getString(R.string.Syncing_FCM));

                                new FCMSynchronizer(MainActivity.this, apiKey)
                                        .execute();
                            } else {
                                onAsyncSuccess();
                            }

                            try {
                                WebSocketHelper.getInstance(MainActivity.this).send(new SocketMessage("ETS Client initialized", id));
                            } catch (URISyntaxException | IOException | JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onFailed(String reason) {
                            onAsyncFailed(reason);
                        }
                    }
            );

        } else {
            Toast.makeText(this, R.string.Network_error, Toast.LENGTH_SHORT).show();
            onAsyncMessage(getString(R.string.Network_error));
        }

    }

    @Override
    public void onAllPermissionGranted() {
        tvLog.setText(R.string.Press_below_button_to_start_activation);

        pbLoading.setVisibility(View.GONE);
        bActivate.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPermissionDenial() {

        Toast.makeText(
                this,
                R.string.All_permissions_must_be_accepted,
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }

    @Override
    public void onAsyncStarted() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pbLoading.setVisibility(View.VISIBLE);
                bActivate.setVisibility(View.GONE);
                tvLog.setText(R.string.Activating);
            }
        });
    }

    @Override
    public void onAsyncFailed(final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pbLoading.setVisibility(View.GONE);
                tvLog.setText(reason);
                bActivate.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onAsyncMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvLog.setText(message);
            }
        });
    }

    @Override
    public void onAsyncSuccess() {

        tvLog.setText(R.string.Finished);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                confirmClose();
                pbLoading.setVisibility(View.GONE);
                bActivate.setVisibility(View.VISIBLE);
            }
        });
    }

    public void confirmClose() {

        final AlertDialog confirmDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.Finished)
                .setMessage(R.string.Device_activation_finished)
                .setCancelable(false)
                .setPositiveButton(R.string.CLOSE_APP, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        hideAppAndClose();
                    }
                }).create();

        confirmDialog.show();
    }

    private void hideAppAndClose() {
        //Hiding app icon
        if (!App.IS_DEBUG_MODE) {
            PackageManager p = getPackageManager();
            ComponentName componentName = new ComponentName(this, MainActivity.class);
            p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        }


        finish();
    }
}
