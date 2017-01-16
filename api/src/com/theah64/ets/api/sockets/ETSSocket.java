package com.theah64.ets.api.sockets;

import com.theah64.ets.api.servlets.AdvancedBaseServlet;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * Created by theapache64 on 17/1/17.
 */
@ServerEndpoint(AdvancedBaseServlet.VERSION_CODE + "/ets_socket")
public class ETSSocket {

    @OnOpen
    public void onOpen(Session session) {
        System.out.println("Socket opened");
    }

    @OnMessage
    public void onMessage(Session session, String data) {
        System.out.println("onMessage : " + data);
    }

    @OnError
    public void onError(Throwable e) {
        e.printStackTrace();
        System.out.println("ERROR:" + e.getMessage());
    }

    @OnClose
    public void onClose() {
        System.out.println("Socket closed");
    }

}
