package com.theah64.ets.api.servlets;

import com.theah64.ets.api.database.tables.Companies;
import com.theah64.ets.api.database.tables.Employees;
import com.theah64.ets.api.models.Employee;
import com.theah64.ets.api.utils.Request;

import javax.servlet.annotation.WebServlet;

/**
 * Created by theapache64 on 18/11/16,1:33 AM.if
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

        final String companyCode = getStringParameter(KEY_COMPANY_CODE);
        final boolean isCompanyExists = Companies.getInstance().isExist(Companies.COLUMN_CODE, companyCode, Companies.COLUMN_IS_ACTIVE, Companies.TRUE);

        if (isCompanyExists) {

            final String imei = getStringParameter(Employees.COLUMN_IMEI);
            final boolean isEmpExists = Employees.getInstance().isExist(Employees.COLUMN_IMEI, imei, Employees.COLUMN_IS_ACTIVE, Employees.TRUE);
            if(isEmpExists){

            }


        } else {
            throw new Request.RequestException("Company doesn't exist : " + companyCode);
        }

    }
}
