package com.theah64.ets.asyncs;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by theapache64 on 12/9/16.
 */
abstract class BaseJSONPostNetworkAsyncTask<RESULT> extends AsyncTask<String, Void, RESULT> {

    private final Context context;
    private final String apiKey;

    BaseJSONPostNetworkAsyncTask(Context context, String apiKey) {
        this.context = context;
        this.apiKey = apiKey;
    }

    public Context getContext() {
        return context;
    }

    public String getApiKey() {
        return apiKey;
    }
}
