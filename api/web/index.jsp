<%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 17/11/16
  Time: 10:25 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="signin_check.jsp" %>
<html>
<head>
    <title><%=company.getName() + " - Control PANEL"%>
    </title>
    <%@include file="common_headers.jsp" %>
</head>
<body>

<%@include file="navbar.jsp" %>

<div class="container-fluid">

    <%--Main--%>
    <div class="row">
        <%--Employee list--%>
        <div class="col-md-2">

            <div class="employee">
                <p class="employee_name"><strong>John</strong></p>
                <p class="employee_status">Last seen: 2 minutes ago</p>
                <button type="button" class="btn btn-default"><span class="glyphicon glyphicon-map-marker"></span>
                </button>
            </div>

        </div>

        <%--Map view--%>
        <div class="col-md-10">
        </div>
    </div>
</div>
</body>
</html>
