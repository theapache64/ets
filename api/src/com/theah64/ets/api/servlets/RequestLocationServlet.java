package com.theah64.ets.api.servlets;

import com.theah64.ets.api.database.tables.BaseTable;
import com.theah64.ets.api.database.tables.Employees;
import com.theah64.ets.api.utils.APIResponse;
import com.theah64.ets.api.utils.FCMUtils;
import com.theah64.ets.api.utils.Request;
import org.json.JSONArray;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;

/**
 * Created by theapache64 on 19/11/16,3:21 PM.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/request_location"})
public class RequestLocationServlet extends AdvancedBaseServlet {

    private static final String KEY_EMP_CODES = "emp_codes";

    @Override
    protected boolean isSecureServlet() {
        return false;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{KEY_EMP_CODES};
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, BaseTable.InsertFailedException, JSONException, BaseTable.UpdateFailedException {
        final String empCodes = getStringParameter(KEY_EMP_CODES);
        final JSONArray jaEmpCodes = new JSONArray(empCodes);
        if (jaEmpCodes.length() > 0) {
            final JSONArray jaFcmIds = Employees.getInstance().getFCMIds(jaEmpCodes);
            if (jaFcmIds != null) {
                //Alright
                final boolean isSent = FCMUtils.sendLocationRequest(jaFcmIds);

                if (isSent) {
                    final String status = String.format("emp_codes received : %d, request sent to: %d", jaEmpCodes.length(), jaFcmIds.length());
                    getWriter().write(new APIResponse("Location request sent", "status", status).getResponse());
                } else {
                    throw new Request.RequestException("Failed to send location request");
                }

            } else {
                throw new Request.RequestException("Couldn't find fcm_id from the given emp_codes: " + empCodes);
            }
        } else {
            throw new Request.RequestException("Can't proceed without at least one emp_code");
        }
    }
}
