package com.theah64.ets.utils;

import android.content.Context;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by theapache64 on 17/1/17.
 */

public class WebSocketHelper {

    private static final String X = WebSocketHelper.class.getSimpleName();
    private static WebSocketHelper instance;
    private static final String ETS_SOCKET_URL_FORMAT = App.IS_DEBUG_MODE ? "ws://192.168.43.234:8080/v1/ets_socket/%s" : "ws://employeetrackingsystem.xyz:8080/ets/v1/ets_socket/%s";

    private final WebSocketClient webSocketClient;

    private WebSocketHelper(String companyCode) throws URISyntaxException {

        final String etsSocketUrl = String.format(ETS_SOCKET_URL_FORMAT, companyCode);

        webSocketClient = new WebSocketClient(new URI(etsSocketUrl)) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                Log.i(X, "ETS Socket opened");
            }

            @Override
            public void onMessage(String message) {
                Log.i(X, "Message: " + message);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.e(X, code + " Socket closed: " + reason + ", REMOTE: " + remote);
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();
                Log.e(X, "ERROR: " + ex.getMessage());
            }
        };

        webSocketClient.connect();
    }

    public static WebSocketHelper getInstance(final String companyCode) throws URISyntaxException {
        if (instance == null) {
            instance = new WebSocketHelper(companyCode);
        }
        return instance;
    }

    public WebSocketClient getWebSocketClient() {
        return webSocketClient;
    }
}
