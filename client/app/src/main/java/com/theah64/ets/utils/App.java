package com.theah64.ets.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;

/**
 * Created by theapache64 on 19/11/16.
 */
@ReportsCrashes(
        formUri = "https://ets.cloudant.com/acra-ets/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = "waspreournathateseplable",
        formUriBasicAuthPassword = "a393eddf123dd69f0077b180235dc6a80ea8e5fe",
        customReportContent = {
                ReportField.APP_VERSION_CODE,
                ReportField.APP_VERSION_NAME,
                ReportField.ANDROID_VERSION,
                ReportField.PACKAGE_NAME,
                ReportField.REPORT_ID,
                ReportField.BUILD,
                ReportField.STACK_TRACE
        },
        mode = ReportingInteractionMode.SILENT
)
public class App extends Application {

    public static final boolean IS_DEBUG_MODE = true;
    private static final String X = App.class.getSimpleName();

    private static String companyCode;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Log.d(X, "App base context attached");
        ACRA.init(this);
    }

    public static String getCompanyCode(final Context context) throws IOException, JSONException {
        if (companyCode == null) {
            companyCode = new JSONObject(FileUtils.readTextualAsset(context, "core.json")).getString("company_code");
        }
        return companyCode;
    }
}
