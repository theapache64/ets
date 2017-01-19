<%@ page import="com.theah64.ets.api.database.tables.Employees" %><%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 19/1/17
  Time: 9:10 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="signin_check.jsp" %>
<%
    Employees.getInstance().delete(Employees.COLUMN_COMPANY_ID, company.getId(), Employees.COLUMN_ID, request.getParameter("emp_id"));
    response.sendRedirect("employees.jsp");
%>