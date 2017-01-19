<%@ page import="com.theah64.ets.api.database.tables.BaseTable" %>
<%@ page import="com.theah64.ets.api.database.tables.Employees" %>
<%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 20/1/17
  Time: 12:19 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="signin_check.jsp" %>
<%
    try {
        Employees.getInstance().update(Employees.COLUMN_COMPANY_ID, company.getId(), Employees.COLUMN_ID, request.getParameter("emp_id"), Employees.COLUMN_NAME, request.getParameter("name"));
    } catch (BaseTable.UpdateFailedException e) {
        e.printStackTrace();
    }

    response.sendRedirect("employees.jsp");
%>
