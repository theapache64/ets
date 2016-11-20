package com.theah64.ets.api.servlets;

import com.theah64.ets.api.database.tables.BaseTable;
import com.theah64.ets.api.database.tables.Companies;
import com.theah64.ets.api.database.tables.Employees;
import com.theah64.ets.api.models.Employee;
import com.theah64.ets.api.utils.APIResponse;
import com.theah64.ets.api.utils.RandomString;
import com.theah64.ets.api.utils.Request;
import org.json.JSONException;

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
    private static final int API_KEY_LENGTH = 10;
    private static final int EMP_CODE_LENGTH = 10;

    @Override
    protected boolean isSecureServlet() {
        return false;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{KEY_COMPANY_CODE, Employees.COLUMN_NAME, Employees.COLUMN_DEVICE_HASH, Employees.COLUMN_IMEI};
    }


    @Override
    protected void doAdvancedPost() throws Request.RequestException, BaseTable.InsertFailedException, JSONException, BaseTable.UpdateFailedException {

        System.out.println("----------------------");
        System.out.println("New api request received....");

        final String companyCode = getStringParameter(KEY_COMPANY_CODE);
        final String companyId = Companies.getInstance().get(Companies.COLUMN_CODE, companyCode, Companies.COLUMN_ID, true);

        if (companyId != null) {

            final String deviceHash = getStringParameter(Employees.COLUMN_DEVICE_HASH);
            final String fcmId = getStringParameter(Employees.COLUMN_FCM_ID);

            final Employees empTable = Employees.getInstance();
            Employee emp = empTable.get(Employees.COLUMN_DEVICE_HASH, deviceHash, Employees.COLUMN_IS_ACTIVE, Employees.TRUE);

            if (emp != null && fcmId != null && !emp.getFcmId().equals(fcmId)) {
                //EMP exist.

                //Updating fcm id
                empTable.update(Employees.COLUMN_ID, emp.getId(), Employees.COLUMN_FCM_ID, fcmId);

            } else {
                //EMP doesn't exist. so create new one.
                final String name = getStringParameter(Employees.COLUMN_NAME);
                final String imei = getStringParameter(Employees.COLUMN_IMEI);

                final String apiKey = RandomString.getNewApiKey(API_KEY_LENGTH);
                final String empCode = RandomString.getRandomString(EMP_CODE_LENGTH);

                emp = new Employee(null, name, imei, deviceHash, fcmId, apiKey, companyId, empCode);
                empTable.add(emp);
            }

            System.out.println("Employee: " + emp);

            //Finally showing api key
            getWriter().write(new APIResponse("Verified employee", Employees.COLUMN_API_KEY, emp.getApiKey()).getResponse());

        } else {
            throw new Request.RequestException("Company doesn't exist : " + companyCode);
        }

    }
}
