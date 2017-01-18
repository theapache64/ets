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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * Created by theapache64 on 17/1/17.
 */

public class WebSocketHelper {

    private static final String X = WebSocketHelper.class.getSimpleName();
    private static WebSocketHelper instance;
    private static final String ETS_SOCKET_URL_FORMAT = App.IS_DEBUG_MODE ? "ws://192.168.43.234:8080/v1/ets_socket/%s" : "ws://employeetrackingsystem.xyz:8080/ets/v1/ets_socket/%s";

    private final WebSocketClient webSocketClient;
    private Context context;
    private static final Queue<SocketMessage> pendingMessages = new LinkedList<>();


    private WebSocketHelper(final Context context) throws IOException, JSONException {
        this.context = context;

        final String etsSocketUrl = String.format(ETS_SOCKET_URL_FORMAT, App.getCompanyCode());
        Log.d(X, "SocketUrl: " + etsSocketUrl);

        try {
            webSocketClient = new WebSocketClient(new URI(etsSocketUrl), new Draft_17()) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    Log.i(X, "Socket opened");

                    Log.d(X, "Sending pending messages...");
                    while (webSocketClient.getConnection().isOpen() && pendingMessages.iterator().hasNext()) {
                        final SocketMessage socketMessage = pendingMessages.poll();
                        Log.d(X, "Sending: " + socketMessage);
                        WebSocketHelper.this.send(socketMessage);
                    }

                }

                @Override
                public void onMessage(String message) {
                    Log.i(X, "Message: " + message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    Log.e(X, code + " Socket closed: " + reason + ", REMOTE: " + remote);
                    Log.d(X, "Trying to reopen the socket...");

                    if (NetworkUtils.hasNetwork(context)) {
                        try {
                            Log.d(X, "Establishing new socket connection...");
                            instance = new WebSocketHelper(context);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(X, "Network unavailable");
                    }
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

    public static WebSocketHelper getInstance(final Context context) throws URISyntaxException, IOException, JSONException {
        if (instance == null) {
            instance = new WebSocketHelper(context.getApplicationContext());
        }
        return instance;
    }

    private WebSocketClient getWebSocketClient() {
        if (webSocketClient.getConnection().isOpen()) {
            return webSocketClient;
        }

        if (webSocketClient.getConnection().isClosed()) {
            //Reopening
            try {
                instance = new WebSocketHelper(context);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

        }

        Log.e(X, "Socket ");
        return null;
    }

    public void send(SocketMessage socketMessage) {
        final WebSocketClient client = getWebSocketClient();
        if (client != null) {
            Log.d(X, "Sending : " + socketMessage);
            client.send(socketMessage.toString());
        } else {
            Log.e(X, "Socket not opened yet : Failed message -> " + socketMessage);
            pendingMessages.add(socketMessage);
        }
    }


}
