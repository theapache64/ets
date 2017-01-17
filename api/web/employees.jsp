<%@ page import="com.theah64.ets.api.database.tables.Employees" %>
<%@ page import="com.theah64.ets.api.models.Employee" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 16/1/17
  Time: 1:29 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="signin_check.jsp" %>
<html>
<head>
    <title>Employees - <%=company.getName()%>
    </title>
    <%@include file="common_headers.jsp" %>
</head>
<body>
<%@include file="navbar.jsp" %>
<div class="container">
    <div class="row">
        <div class="col-md-12">
            <table class="table table-striped table-bordered table-hover">

                <thead>
                <tr>
                    <th>
                        Name
                    </th>
                    <th>
                        IMEI
                    </th>
                    <th>

                    </th>
                </tr>
                </thead>

                <tbody>
                <tr>
                        <%
                        final List<Employee> employees = Employees.getInstance().getAllFireableEmployees(company.getId(), true);
                        if (employees != null) {
                            //Looping through each employee
                            for (final Employee employee : employees) {
                    %>
                <tr>
                    <td><%=employee.getName()%>
                    </td>
                    <td>
                        <%=employee.getImei()%>
                    </td>
                    <td>
                        <a type="button" class="btn btn-default"><span class="glyphicon glyphicon-map-marker"></span>
                        </a>
                        <a type="button" class="btn btn-default"><span class="glyphicon glyphicon-pencil"></span>

                        </a>
                        <a type="button" class="btn btn-default"><span class="glyphicon glyphicon-remove"></span>

                        </a>
                    </td>
                </tr>
                <%
                        }
                    }
                %>
                </tbody>

            </table>
        </div>
    </div>
</div>
</body>
</html>
