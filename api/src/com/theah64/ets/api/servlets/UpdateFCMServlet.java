package com.theah64.ets.api.servlets;

import com.theah64.ets.api.database.tables.BaseTable;
import com.theah64.ets.api.database.tables.Employees;
import com.theah64.ets.api.models.Employee;
import com.theah64.ets.api.utils.APIResponse;
import com.theah64.ets.api.utils.Request;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;

/**
 * Created by theapache64 on 19/11/16,3:11 PM.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/update_fcm"})
public class UpdateFCMServlet extends AdvancedBaseServlet {

    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{Employees.COLUMN_FCM_ID};
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, BaseTable.InsertFailedException, JSONException, BaseTable.UpdateFailedException {
        final String empCode = getHeaderSecurity().getEmployeeId();
        final String fcmId = getStringParameter(Employees.COLUMN_FCM_ID);
        Employees.getInstance().update(Employees.COLUMN_ID, empCode, Employees.COLUMN_FCM_ID, fcmId);
        getWriter().write(new APIResponse("FCM id updated", null).getResponse());
    }
}
