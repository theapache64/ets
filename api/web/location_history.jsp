<%@ page import="com.theah64.ets.api.database.tables.LocationHistories" %>
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
    <title>Location History</title>
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
    function initMap() {

        var dubai = new google.maps.LatLng(25.276987, 55.296249);
        var mapCanvas = document.getElementById("map");
        var mapOptions = {
            center: dubai,
            zoom: 10
        };

        map = new google.maps.Map(mapCanvas, mapOptions);

        //loading locations
        var locations = [];
        <%
            final List<Location> locations = LocationHistories.getInstance().getAll(LocationHistories.COLUMN_EMPLOYEE_ID, request.getParameter("emp_id"));
            if(locations!=null){
                for(final Location location : locations){
                    %>
        var gLatLon = new google.maps.LatLng(<%=location.getLat()+","+location.getLon()%>);
        locations.push(gLatLon);

        var marker<%=location.getId()%> = new google.maps.Marker({
            position: gLatLon,
            animation: google.maps.Animation.DROP
        });

        marker<%=location.getId()%>.setMap(map);

        google.maps.event.addListener(marker<%=location.getId()%>, 'click', function() {

            var infowindow<%=location.getId()%> = new google.maps.InfoWindow({
                content:"<%=location.getDeviceTime()%>"
            });

            infowindow<%=location.getId()%>.open(map,marker<%=location.getId()%>);
        });

        //Creating point

        <%
    }
}
%>

        //moving map to last known location
        map.panTo(locations[0]);

        var poly = new google.maps.Polyline({
            path : locations,
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
