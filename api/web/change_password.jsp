<%--
  Created by IntelliJ IDEA.
  User: theapache44
  Date: 19/1/17
  Time: 1:39 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="signin_check.jsp" %>
<html>
<head>
    <title>Change Password</title>
    <%@include file="common_headers.jsp" %>
</head>
<body>
<%@include file="navbar.jsp" %>

<div class="container">
    <div class="row">
        <div class="col-md-4 content-centered">

            <%--Change pasword--%>
            <form data-toggle="validator" action="change_password.jsp" method="POST" role="form">
                <br>
                <h4>Change password</h4>
                <br>

                <div class="form-group">
                    <label for="iPassword">Old password : </label>
                    <input name="old_password" type="password" data-minlength="4" id="iPassword"
                           class="form-control"
                           placeholder="Current password" required/>

                    <div class="help-block with-errors">Current password</div>
                </div>

                <div class="form-group">
                    <label for="iNewPassword">New password : </label>
                    <input name="password" type="password" data-minlength="4" id="iNewPassword"
                           class="form-control"
                           placeholder="Password" required/>

                    <div class="help-block with-errors">Minimum 4 characters</div>
                </div>

                <div class="form-group">
                    <label for="iNewCPassword">Confirm : </label>
                    <input name="cpassword" type="password" id="iNewCPassword" class="form-control"
                           placeholder="Confirm password" data-match="#iNewPassword"
                           data-match-error="Password doesn't match" required/>

                    <div class="help-block with-errors">&nbsp;</div>

                    <%
                        try {
                            final boolean isPasswordChangeFormSubmitted = request.getParameter("isPasswordChangeFormSubmitted") != null;
                            if (isPasswordChangeFormSubmitted) {

                                final String oldPassword = request.getParameter("old_password");
                                final String newPassword = request.getParameter(Companies.COLUMN_PASSWORD);
                                final String confirmNewPassword = request.getParameter(Companies.KEY_C_PASSWORD);

                                System.out.println(oldPassword);
                                System.out.println(newPassword);
                                System.out.println(confirmNewPassword);

                                if (oldPassword != null && newPassword != null && confirmNewPassword != null) {

                                    if (company.getPassword().equals(oldPassword)) {

                                        if (!newPassword.equals(company.getPassword())) {

                                            final Companies companies = Companies.getInstance();
                                            companies.update(Companies.COLUMN_ID, company.getId(), Companies.COLUMN_PASSWORD, newPassword);
                    %>
                    <p class="text text-success change-settings-result">Password changed!
                    </p>
                    <%

                                    } else {
                                        throw new Exception("Same as old!");
                                    }


                                } else {
                                    throw new Exception("Old password is wrong!");
                                }

                            } else {
                                throw new Exception("Wrong request!");
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    %>
                    <span class="text text-danger"><%=e.getMessage()%>
                </span>
                    <%
                        }
                    %>
                </div>


                <input name="isPasswordChangeFormSubmitted" style="margin: 0  0px 10px 0" value="Change password"
                       type="submit" class="btn btn-danger pull-right"/>
            </form>
        </div>
    </div>
</div>
</body>
</html>
