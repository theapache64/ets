package com.theah64.ets.api.servlets;

import com.theah64.ets.api.database.tables.Employees;

import javax.servlet.annotation.WebServlet;

/**
 * Created by theapache64 on 18/11/16,1:33 AM.
 * route: /get_api_key
 * -------------------
 * on_req: company_code,name, imei, fcm_id
 * on_resp: api_key
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/get_api_key"})
public class GetAPIKeyServlet extends AdvancedBaseServlet {

    private static final String KEY_COMPANY_CODE = "company_code";

    @Override
    protected boolean isSecureServlet() {
        return false;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{KEY_COMPANY_CODE, Employees.COLUMN_NAME, Employees.COLUMN_IMEI, Employees.COLUMN_FCM_ID};
    }

    @Override
    protected void doAdvancedPost() throws Exception {
        //TODO: Do employee sign up here.
    }
}
