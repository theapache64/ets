<%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 21/1/17
  Time: 2:28 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title><%=request.getParameter("title")%>
    </title>
    <%@include file="common_headers.jsp" %>
</head>
<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-12" style="text-align: center;">
            <h2><%=request.getParameter("title")%>
            </h2>
            <p><%=request.getParameter("message")%>
            </p>
        </div>
    </div>
</div>
</body>
</html>
