package com.theah64.ets.api.servlets;

import com.theah64.ets.api.database.tables.BaseTable;
import com.theah64.ets.api.database.tables.LocationHistories;
import com.theah64.ets.api.models.Location;
import com.theah64.ets.api.utils.APIResponse;
import com.theah64.ets.api.utils.Request;
import org.json.JSONException;

import javax.servlet.annotation.WebServlet;

/**
 * Created by theapache64 on 19/11/16,2:58 PM.
 */
@WebServlet(urlPatterns = {AdvancedBaseServlet.VERSION_CODE + "/report_location"})
public class ReportLocationServlet extends AdvancedBaseServlet {

    @Override
    protected boolean isSecureServlet() {
        return true;
    }

    @Override
    protected String[] getRequiredParameters() {
        return new String[]{LocationHistories.COLUMN_LATITUDE, LocationHistories.COLUMN_LONGITUDE, LocationHistories.COLUMN_DEVICE_TIME};
    }

    @Override
    protected void doAdvancedPost() throws Request.RequestException, BaseTable.InsertFailedException, JSONException {

        final String empId = getHeaderSecurity().getEmployeeId();

        final String lat = getStringParameter(LocationHistories.COLUMN_LATITUDE);
        final String lon = getStringParameter(LocationHistories.COLUMN_LONGITUDE);
        final String deviceTime = getStringParameter(LocationHistories.COLUMN_DEVICE_TIME);

        LocationHistories.getInstance().add(new Location(empId, lat, lon, deviceTime));

        //success
        getWriter().write(new APIResponse("Location reported", null).getResponse());
    }
}
