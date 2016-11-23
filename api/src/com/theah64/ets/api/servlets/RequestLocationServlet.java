package com.theah64.ets.api.servlets;

import com.theah64.ets.api.database.tables.BaseTable;
import com.theah64.ets.api.database.tables.Companies;
import com.theah64.ets.api.database.tables.Employees;
import com.theah64.ets.api.models.Company;
import com.theah64.ets.api.models.Employee;
import com.theah64.ets.api.utils.APIResponse;
import com.theah64.ets.api.utils.FCMUtils;
import com.theah64.ets.api.utils.Request;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{Companies.COLUMN_ID};
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, BaseTable.InsertFailedException, JSONException, BaseTable.UpdateFailedException {

        final String empCodes = getStringParameter(KEY_EMP_CODES);

        final Employees empTable = Employees.getInstance();

        //if emp codes are empty ? flash_push : specific push
        final List<Employee> employees = empCodes == null ? empTable.getAllFireableEmployees(getStringParameter(Companies.COLUMN_ID)) : empTable.get(Employees.COLUMN_CODE, new JSONArray(empCodes));

        if (employees != null) {

            final JSONArray jaFcmIds = new JSONArray();

            for (final Employee employee : employees) {
                jaFcmIds.put(employee.getFcmId());
            }

            //Alright
            final JSONObject joFcmResp = FCMUtils.sendLocationRequest(jaFcmIds);

            if (joFcmResp != null) {

                final boolean isEverythingOk = joFcmResp.getInt("failure") == 0;
                final JSONObject joFcmResult = new JSONObject();
                final JSONArray jaFailedEmps = new JSONArray();

                if (!isEverythingOk) {

                    //Looping through result
                    final JSONArray jaFcmResults = joFcmResp.getJSONArray("results");

                    if (jaFcmResults.length() == jaFcmIds.length()) {

                        for (int i = 0; i < jaFcmResults.length(); i++) {
                            final JSONObject joEmpFcmResult = jaFcmResults.getJSONObject(i);
                            if (joEmpFcmResult.has("error")) {

                                final Employee failedEmp = employees.get(i);
                                final JSONObject joFailedEmp = new JSONObject();

                                joFailedEmp.put(Employees.COLUMN_NAME, failedEmp.getName());
                                joFailedEmp.put(Employees.COLUMN_CODE, failedEmp.getEmpCode());

                                jaFailedEmps.put(joFailedEmp);
                            }
                        }


                    } else {
                        throw new Request.RequestException("fcm id count doesn't match with collected and requested");
                    }

                }

                joFcmResult.put("failed_emps", jaFailedEmps);
                joFcmResult.put("fcm_resp", joFcmResp);
                getWriter().write(new APIResponse("Location request sent to " + (employees.size() - jaFailedEmps.length()) + " employee(s)", joFcmResult).getResponse());

            } else {
                throw new Request.RequestException("Failed to send location request");
            }

        } else {
            throw new Request.RequestException("No employee found in the given company or with the emp_codes");
        }

    }
}
