<%@ page import="com.theah64.ets.api.database.tables.Companies" %>
<%@ page import="com.theah64.ets.api.models.Company" %><%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 30/8/16
  Time: 10:32 PM
  To change this template use FileNode | Settings | FileNode Templates.
--%>
<%

    final Object companyId = session.getAttribute(Companies.COLUMN_ID);
    final Company company;
    if (companyId == null) {
        response.sendRedirect("signin.jsp");
        return;
    } else {
        company = Companies.getInstance().get(Companies.COLUMN_ID, companyId.toString());
        if (company != null) {
            request.setAttribute(Company.KEY, company);
        } else {
            //Expired
            session.invalidate();
            response.sendRedirect("signin.jsp");
            return;
        }
    }
%>
