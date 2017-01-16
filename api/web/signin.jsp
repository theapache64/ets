<%@ page import="com.theah64.ets.api.database.tables.Companies" %>
<%@ page import="com.theah64.ets.api.models.Company" %><%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 12/9/16
  Time: 4:08 PM=
  To change this template use FileNode | Settings | FileNode Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute(Companies.COLUMN_ID) != null) {
        response.sendRedirect("index.jsp");
        return;
    }
%>
<html>
<head>
    <title>Comapany SignIn</title>
    <%@include file="common_headers.jsp" %>
</head>
<body>
<div class="container">

    <div class="row">
        <h1 class="text-center">Sign in</h1>
    </div>

    <div class="row">

        <div class="col-md-4  content-centered">

            <%--Form--%>
            <form action="signin.jsp" method="POST" role="form">

                <div class="form-group">
                    <label for="iUsername">Username : </label>
                    <input value="<%=Connection.isDebugMode() ? "etsadmin" : ""%>" name="username" type="text"
                           id="iUsername" class="form-control"
                           placeholder="Username"/>
                </div>


                <div class="form-group">
                    <label for="iPassword">Password : </label>
                    <input value="<%=Connection.isDebugMode() ? "1234" : ""%>" name="password" type="password" id="iPassword" class="form-control"
                           placeholder="Password"/>
                </div>

                <div class="row">

                    <div class="col-md-8">
                        <%

                            final boolean isFormSubmitted = request.getParameter("isFormSubmitted") != null;

                            if (isFormSubmitted) {

                                final String username = request.getParameter(Companies.COLUMN_USERNAME);
                                final String password = request.getParameter("password");

                                final Company company = Companies.getInstance().get(Companies.COLUMN_USERNAME, username, Companies.COLUMN_PASSWORD, password);

                                if (company != null && company.isActive()) {
                                    session.setAttribute(Companies.COLUMN_ID, company.getId());
                                    response.sendRedirect("index.jsp");
                                } else {
                        %>
                        <div class="text-danger pull-left">
                            <%=company == null ? "Invalid credentials!!" : "BLOCKED COMPANY"%>
                        </div>

                        <%
                                }
                            }
                        %>
                    </div>


                    <div class="col-md-4">
                        <input value="Sign in" name="isFormSubmitted" type="submit" class="btn btn-primary pull-right"/>
                    </div>


                </div>


            </form>


        </div>
    </div>
</div>
</body>
</html>
