<%@ page import="com.theah64.ets.api.models.Company" %><%--Navbar--%>
<nav class="navbar navbar-default">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="index.jsp">
                <%=((Company) request.getAttribute(Company.KEY)).getName()%>
            </a>
        </div>

        <ul class="nav navbar-nav navbar-right">
            <li><a href="index.jsp"><span class="glyphicon glyphicon-home"></span> Home</a></li>
            <li><a href="employees.jsp"><span class="glyphicon glyphicon-user"></span> Employees</a></li>
            <li><a href="#"><span class="glyphicon glyphicon-cog"></span> Change password</a></li>
            <li><a href="signout.jsp"><span class="glyphicon glyphicon-log-out"></span> SignOut</a></li>
        </ul>
    </div>
</nav>