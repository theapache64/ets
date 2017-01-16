<%@ page import="com.theah64.ets.api.models.Employee" %>
<%@ page import="java.util.List" %>
<%@ page import="com.theah64.ets.api.database.tables.Employees" %>
<%@ page import="com.theah64.ets.api.models.Location" %>
<%@ page import="com.theah64.ets.api.database.tables.LocationHistories" %><%--
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

    <script>

        var map = null;

        $(document).ready(function () {

            //onRequest location button
            $("div.employee button").click(function (e) {

                //Sending location request
                var empCode = $(this).data("emp-code");

                //Changing status
                var pEmpStatus = $(this).siblings(".employee_status");
                $(pEmpStatus).text("Requesting for new location...");

                $.get('/v1/request_location', {
                    id:<%=company.getId()%>,
                    emp_codes: "[" + empCode + "]"
                }, function (data, status) {
                    if (!data.error) {
                        $(pEmpStatus).text("Location request sent");
                    } else {
                        $(pEmpStatus).text(data.message);
                    }
                });

                e.stopPropagation();
            });


            //onShow current employee location
            $("div.employee").click(function () {
                var lat = $(this).data("lat");
                var lon = $(this).data("lon");

                var gLatLon = new google.maps.LatLng(lat, lon);
                map.panTo(gLatLon);
            });

            //building socket

        });
    </script>

</head>
<body>

<%@include file="navbar.jsp" %>

<div class="container-fluid">


    <div class="row">

        <!--Employees-->
        <div id="employees" class="col-md-3" style="height: 89%;overflow: scroll;">

            <%
                final List<Employee> employeeList = Employees.getInstance().getAllFireableEmployees(company.getId(), true);
                if (employeeList != null) {
                    for (final Employee employee : employeeList) {
                        final Location loc = employee.getLastKnownLocation();
            %>

            <div class="employee" data-name="<%=employee.getName()%>"
                 data-lat="<%=loc!=null ? loc.getLat() : ""%>"
                 data-lon="<%=loc!=null ? loc.getLon() : ""%>"
                 data-last-seen="<%=loc!=null ? loc.getDeviceTime() : ""%>">
                <p class="employee_name"><strong><%=employee.getName()%>
                </strong></p>
                <p class="employee_status"><%=loc != null ? "last seen " + loc.getDeviceTime() : "NEVER SEEN"%>
                </p>
                <button data-emp-code="<%=employee.getEmpCode()%>" type="button" class="btn"><span
                        class="glyphicon glyphicon-refresh"></span>
                </button>
            </div>

            <%

                    }
                }
            %>

        </div>

        <!--Map-->
        <div class="col-md-9">
            <div id="map" style="width: 100%;height: 89%    ;">

            </div>
        </div>
    </div>
</div>
<script>

    //Initializing map
    function initMap() {

        var dubai = new google.maps.LatLng(25.276987, 55.296249);
        var mapCanvas = document.getElementById("map");
        var mapOptions = {
            center: dubai,
            zoom: 10
        }

        map = new google.maps.Map(mapCanvas, mapOptions);

        //Pinning last location of employees
        var employees = $("div#employees").children();
        console.log("Found " + employees.length + " employee(s)");

        for (var i = 0; i < employees.length; i++) {

            var name = $(employees[i]).data("name");
            var lat = $(employees[i]).data("lat");
            var lon = $(employees[i]).data("lon");

            if (!lat || !lon) {
                console.log("no location found for " + name);
                continue;
            }

            var lastSeen = $(employees[i]).data("last-seen");

            //Setting marker
            var gLatLon = new google.maps.LatLng(lat, lon);
            var marker = new google.maps.Marker({position: gLatLon});

            marker.setMap(map);

            //Building info window with employee name
            var infoWindow = new google.maps.InfoWindow(
                {
                    content: name + " - (" + lastSeen + ")"
                }
            );

            //Showing info window
            infoWindow.open(map, marker);

            //Click on zoom
            google.maps.event.addListener(marker, 'click', function () {
                map.setZoom(18);
                map.setCenter(marker.getPosition());
            });

        }
    }

</script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCwmri1d59R8EH5zyXAw-BXRcEMFUtKjA4&callback=initMap"></script>
</body>
</html>
