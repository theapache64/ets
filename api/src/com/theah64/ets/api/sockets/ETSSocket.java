package com.theah64.ets.api.sockets;

import com.theah64.ets.api.database.tables.Companies;
import com.theah64.ets.api.database.tables.Employees;
import com.theah64.ets.api.models.Company;
import com.theah64.ets.api.servlets.AdvancedBaseServlet;
import com.theah64.ets.api.utils.APIResponse;
import org.json.JSONException;
import org.json.JSONObject;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;

/**
 * Created by theapache64 on 17/1/17.
 */
@ServerEndpoint(AdvancedBaseServlet.VERSION_CODE + "/ets_socket/{code}")
public class ETSSocket {

    private static final Map<String, Set<Session>> activeSessions = Collections.synchronizedMap(new HashMap<>());

    @OnOpen
    public void onOpen(@PathParam("code") String companyCode, Session session) throws IOException {
        System.out.println("Socket opened");

        //Getting company id from company code
        final Company company = Companies.getInstance().get(Companies.COLUMN_CODE, companyCode);
        if (company != null && company.isActive()) {
            //company exists
            activeSessions.computeIfAbsent(company.getId(), k -> new HashSet<>());
            activeSessions.get(company.getId()).add(session);
        } else {
            System.out.println("Invalid request");
            session.close();
        }
    }

    @OnMessage
    public void onMessage(Session session, String data) throws IOException {
        System.out.println("onMessage : " + data);

        try {
            final JSONObject joResp = new JSONObject(data);
            if (!joResp.getBoolean(APIResponse.KEY_ERROR)) {

                //no error
                final JSONObject joData = joResp.getJSONObject(APIResponse.KEY_DATA);
                final String companyId = joData.getString(Employees.COLUMN_COMPANY_ID);

                //Looping through all sessions
                synchronized (activeSessions) {
                    for (final Session client : activeSessions.get(companyId)) {
                        if (!client.equals(session)) {
                            client.getBasicRemote().sendText(data);
                        }
                    }
                }

            } else {
                throw new JSONException(joResp.getString(APIResponse.KEY_MESSAGE));
            }

        } catch (JSONException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    @OnError
    public void onError(Throwable e) {
        e.printStackTrace();
        System.out.println("ERROR:" + e.getMessage());
    }

    @OnClose
    public void onClose(Session session) {
        System.out.println("Closing socket " + session);

        //Looping through every company to find the session and then remove it
        final List<String> companyCode = session.getRequestParameterMap().get(Companies.COLUMN_CODE);
        System.out.println("CompanyCode: " + companyCode);

        if (companyCode != null) {
            final String cmpCode = companyCode.get(0);
            System.out.println("companyCode: " + cmpCode);
            final Company company = Companies.getInstance().get(Companies.COLUMN_CODE, cmpCode);
            if (company != null) {
                System.out.println("company: " + company);
                activeSessions.get(company.getId()).remove(session);
                return;
            }
        }

        throw new IllegalArgumentException("Failed to close session");
    }

}
