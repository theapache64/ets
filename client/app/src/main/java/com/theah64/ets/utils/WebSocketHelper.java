package com.theah64.ets.utils;

import android.content.Context;
import android.util.Log;

import com.theah64.ets.model.SocketMessage;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;

import java.io.IOException;
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

    private WebSocketHelper() throws IOException, JSONException {

        final String etsSocketUrl = String.format(ETS_SOCKET_URL_FORMAT, App.getCompanyCode());
        Log.d(X, "SocketUrl: " + etsSocketUrl);

        try {
            webSocketClient = new WebSocketClient(new URI(etsSocketUrl), new Draft_17()) {
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
        } catch (URISyntaxException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Failed to initialize the socket");
        }


    }

    public static WebSocketHelper getInstance() throws URISyntaxException, IOException, JSONException {
        if (instance == null) {
            instance = new WebSocketHelper();
        }
        return instance;
    }

    private WebSocketClient getWebSocketClient() {
        if (webSocketClient.getConnection().isOpen()) {
            Log.i(X, "Open socket available");
            return webSocketClient;
        }

        Log.e(X, "SOCKET NOT OPENED");
        return null;
    }

    public void send(SocketMessage socketMessage) {
        final WebSocketClient client = getWebSocketClient();
        if (client != null) {
            client.send(socketMessage.toString());
        }
    }
}
