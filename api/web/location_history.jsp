<%@ page import="com.theah64.ets.api.database.tables.Employees" %>
<%@ page import="com.theah64.ets.api.database.tables.LocationHistories" %>
<%@ page import="com.theah64.ets.api.models.Employee" %>
<%@ page import="com.theah64.ets.api.models.Location" %>
<%@ page import="java.util.List" %><%--
  Created by IntelliJ IDEA.
  User: theapache64
  Date: 19/1/17
  Time: 12:35 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="signin_check.jsp" %>
<html>
<head>
    <title>
        <%
            final String empId = request.getParameter("emp_id");
            final Employee employee = Employees.getInstance().get(Employees.COLUMN_ID, empId);

            if (employee == null) {
                response.sendError(HttpServletResponse.SC_NO_CONTENT);
                return;
            }

        %>

        <%=employee.getName()%> - Location History</title>
    <%@include file="common_headers.jsp" %>
    <script>
        $(document).ready(function () {

        });
    </script>
</head>
<body>
<%@include file="navbar.jsp" %>
<div class="container-fluid">
    <div class="row">
        <div id="map" class="col-md-12" style="width: 100%;height: 89%    ;">
        </div>
    </div>
</div>

<script>

    const TYPE_LOCATION_FIRST = 1;
    const TYPE_LOCATION_LAST = 2;
    const TYPE_LOCATION_NORMAL = 3;

    function initMap() {

        var mapCanvas = document.getElementById("map");
        var mapOptions = {
            zoom: 10
        };

        map = new google.maps.Map(mapCanvas, mapOptions);

        //loading locations
        var locations = [];
        <%
            final List<Location> locations = LocationHistories.getInstance().getAll(LocationHistories.COLUMN_EMPLOYEE_ID, request.getParameter("emp_id"));
            if(locations!=null){
                for(int i=0;i<locations.size();i++){
                    final Location location = locations.get(i);
                    %>
        var gLatLon = new google.maps.LatLng(<%=location.getLat()+","+location.getLon()%>);
        locations.push(gLatLon);


        addInfoWindow(gLatLon, "<%=location.getDeviceTime()%>", getLocationIcon(<%=i+","+locations.size()%>), <%=i==0 ? "true" : "false"%>);

        function getLocationIcon(current, total) {
            if (current == 0) {
                return "assets/map_current.png";
            } else if (current == (total - 1)) {
                return "assets/map_first.png";
            } else {
                return null;
            }
        }

        <%
    }

}else{
                response.sendRedirect("error.jsp?title=No history&message=No history found for the selected employee");
}
%>

        function addInfoWindow(gLatLon, deviceTime, icon, isFirstLocation) {


            var marker = new google.maps.Marker({
                position: gLatLon,
                icon: icon,
                animation: google.maps.Animation.DROP
            });


            marker.setMap(map);

            var infowindow = new google.maps.InfoWindow({
                content: deviceTime
            });

            google.maps.event.addListener(marker, 'click', function () {
                infowindow.open(map, marker);
            });

            if (isFirstLocation) {
                infowindow.open(map, marker);
            }
        }

        //moving map to last known location
        map.panTo(locations[0]);
        map.setZoom(12);


        var poly = new google.maps.Polyline({
            path: locations,
            strokeColor: '#000000',
            strokeOpacity: 1.0,
            strokeWeight: 2
        });

        poly.setMap(map);
    }


</script>
<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyCwmri1d59R8EH5zyXAw-BXRcEMFUtKjA4&callback=initMap"></script>
</body>

</html>
