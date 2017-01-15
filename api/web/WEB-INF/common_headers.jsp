<%@ page import="com.theah64.xrob.api.database.Connection" %>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">

<%
    if (Connection.isDebugMode()) {
%>
<%--OFFLINE RESOURCES--%>
<link rel="stylesheet" href="/styles/bootstrap.min.css">
<script src="/js/jquery-2.2.4.min.js"></script>
<script src="/js/bootstrap.min.js"></script>
<script src="/js/validator.min.js"></script>
<%
} else {
%>
<%--ONLINE RESOURCES--%>
<link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.4/jquery.min.js"></script>
<script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/1000hz-bootstrap-validator/0.11.5/validator.min.js"></script>
<%
    }
%>

<link rel="stylesheet" href="/styles/style.css">

