<%@ page import="com.theah64.ets.api.database.tables.Companies" %><%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 12/9/16
  Time: 4:08 PM
  To change this template use FileNode | Settings | FileNode Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    if (session.getAttribute(Companies.COLUMN_ID) != null) {
        response.sendRedirect("/control_panel");
        return;
    }
%>
<html>
<head>
    <title>Client Sign in</title>
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
            <form action="/signin" method="POST" role="form">

                <div class="form-group">
                    <label for="iUsername">Username : </label>
                    <input value="testuser" name="username" type="text" id="iUsername" class="form-control"
                           placeholder="Username"/>
                </div>


                <div class="form-group">
                    <label for="iPassword">Password : </label>
                    <input value="testpass" name="password" type="password" id="iPassword" class="form-control"
                           placeholder="Password"/>
                </div>

                <div class="row">

                    <div class="col-md-8">
                        <%

                            final boolean isFormSubmitted = request.getParameter("isFormSubmitted") != null;

                            if (isFormSubmitted) {

                                final String username = request.getParameter(Clients.COLUMN_USERNAME);
                                final String password = request.getParameter("password");

                                final Client theClient = Clients.getInstance().get(Clients.COLUMN_USERNAME, username, Clients.COLUMN_PASS_HASH, DarKnight.getEncrypted(password));

                                if (theClient != null) {
                                    session.setAttribute(Clients.COLUMN_ID, theClient.getId());
                                    response.sendRedirect("/client/panel");
                                } else {
                        %>
                        <div class="text-danger pull-left">Invalid credentials!!</div>

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

    <div class="row">
        <div class="col-md-12 text-center">
            Don't have an account, <a href="/client/signup">Sign up</a>
        </div>
    </div>
</div>
</body>
</html>
