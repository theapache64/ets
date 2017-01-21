<%@ page import="com.theah64.ets.api.database.tables.LocationHistories" %>
<%@ page import="com.theah64.ets.api.models.Employee" %>
<%@ page import="com.theah64.ets.api.database.tables.Employees" %><%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 21/1/17
  Time: 12:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="signin_check.jsp" %>
<%
    LocationHistories.getInstance().delete(request.getParameter("emp_id"), company.getId());
    response.sendRedirect("employees.jsp");
%>
