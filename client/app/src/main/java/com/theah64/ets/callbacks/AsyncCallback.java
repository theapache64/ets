package com.theah64.ets.callbacks;

public interface AsyncCallback {
    void onAsyncStarted();
    void onAsyncFailed(final String reason);
    void onAsyncMessage(final String message);
    void onAsyncSuccess();
}
