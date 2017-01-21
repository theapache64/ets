<%@ page import="com.theah64.ets.api.database.tables.Employees" %>
<%@ page import="com.theah64.ets.api.models.Employee" %>
<%@ page import="com.theah64.ets.api.models.Location" %>
<%@ page import="java.util.List" %>
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

    <script>

        var map;
        var markers = [];
        var locations = [];
        var isDeviceResponded = [];

        $(document).ready(function () {

            $("div.employee button").click(function (e) {

                //Sending location request
                var empId = $(this).parent().attr("id");
                console.log("empId: "+ empId);
                var empCode = $(this).data("emp-code");

                //Changing status
                var pEmpStatus = $(this).siblings(".employee_status");
                $(pEmpStatus).text("Requesting for new location...");

                $.ajax({
                    url: '<%=Connection.isDebugMode()? "/v1/request_location" : "/ets/v1/request_location" %>',
                    type: 'GET',
                    data: {
                        id: <%=company.getId()%>,
                        emp_codes: "[" + empCode + "]"
                    },
                    success: function (data, status) {
                        if (!data.error) {
                            isDeviceResponded[empId] = false;
                            $(pEmpStatus).text("Location request sent");

                            setTimeout(function () {
                                console.log("isDeviceResponded: " + isDeviceResponded[empId]);
                                if (!isDeviceResponded[empId]) {
                                    $(pEmpStatus).text("Device OFFLINE");
                                }
                            }, 3000);

                        } else {
                            $(pEmpStatus).text(data.message);
                        }
                    },
                    error: function (data) {
                        console.log(data);
                        $(pEmpStatus).text("ERROR: " + data.status + ", Please check your connection");
                    }

                });


                e.stopPropagation();
            });


            $("div.employee").click(function () {

                var lat = $(this).data("lat");
                var lon = $(this).data("lon");

                moveMapTo(lat, lon);
            });

            function moveMapTo(lat, lon) {
                var gLatLon = new google.maps.LatLng(lat, lon);
                map.panTo(gLatLon);
            }

            var url = "<%=Connection.isDebugMode()
            ? "ws://localhost:8080/v1/ets_socket/" + company.getCode()
            : "ws://employeetrackingsystem.xyz:8080/ets/v1/ets_socket/"+ company.getCode()%>";


            //Building websocket
            var webSocket = new WebSocket(url);

            log("Opening socket...");

            //onOpen
            webSocket.onopen = function (evnt) {
                log("Socket opened");
            };

            //onMessage
            webSocket.onmessage = function (evnt) {


//              {"error":false,"data":{"company_id":"1","employee_id":"1",message:"Hello"}}
                var joResp = JSON.parse(event.data);

                console.log(joResp);
                var message = joResp.message;

                var joData = joResp.data;
                var employeeId = joData.employee_id;
                isDeviceResponded[employeeId] = true;

                var empDivId = "div#" + employeeId;

                if (joData.type == 'location') {


                    if (markers[employeeId]) {
                        markers[employeeId].setMap(null);
                    }

                    var name = $(empDivId).data("name");
                    lat = joData.lat;
                    lon = joData.lon;

                    gLatLon = new google.maps.LatLng(lat, lon);
                    marker = new google.maps.Marker({position: gLatLon, animation: google.maps.Animation.BOUNCE});
                    marker.setMap(map);

                    var infoWindow = new google.maps.InfoWindow(
                        {
                            content: name + " - ( " + joData.device_time + " )"
                        }
                    );

                    //Showing info window
                    infoWindow.open(map, marker);

                    //Click on zoom
                    google.maps.event.addListener(marker, 'click', function () {
                        map.setZoom(18);
                        map.setCenter(marker.getPosition());
                    });

                    //Setting new values
                    $(empDivId).data("lat", lat);
                    $(empDivId).data("lon", lon);

                    markers[employeeId] = marker;

                    //Adding location
                    var poly = locations[employeeId].getPath();
                    poly.push(gLatLon);
                }


                $(empDivId).find("p.employee_status").text(message);
            };

            webSocket.onclose = function (evnt) {
                log("Socket closed");
            };

            webSocket.onerror = function (evnt) {
                log("ERROR: " + evnt.data);
            };

            function log(message) {
                $("p#ws_status").text(message);
            }

            $("a.openGMaps").click(function () {
                var emp = $(this).parent();
                var lat = $(emp).data("lat");
                var lon = $(emp).data("lon");
                var url = "http://maps.google.com/maps?z=12&t=m&q=loc:" + lat + "+ " + lon;
                window.open(url, '_blank');
            });

        });
    </script>

</head>
<body>

<%@include file="navbar.jsp" %>

<div class="container-fluid">


    <div class="row">
        <div class="col-md-10">
            <!--Test row-->
            <p id="ws_status"></p>
        </div>
    </div>


    <div class="row">

        <!--Employees-->
        <div id="employees" class="col-md-3" style="height: 89%;overflow: scroll;">

            <%
                final List<Employee> employeeList = Employees.getInstance().getAllFireableEmployees(company.getId(), true);
                if (employeeList != null) {
                    for (final Employee employee : employeeList) {
                        final Location loc = employee.getLastKnownLocation();
            %>

            <div id="<%=employee.getId()%>" class="employee" data-name="<%=employee.getName()%>"
                 data-lat="<%=loc!=null ? loc.getLat() : ""%>"
                 data-lon="<%=loc!=null ? loc.getLon() : ""%>"
                 data-last-seen="<%=loc!=null ? loc.getDeviceTime() : ""%>">
                <p class="employee_name"><strong><%=employee.getName()%>
                </strong></p>
                <p class="employee_status">
                    <%=loc != null ? "last seen " + loc.getDeviceTime() : "NEVER SEEN"%>
                </p>
                <%=loc != null ? "<a class=\"openGMaps\" target=\"_blank\">(view)</a>" : ""%>
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
        <div class="col-md-9" style="padding: 0px;">
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
        };

        map = new google.maps.Map(mapCanvas, mapOptions);

        //Pinning last location of employees
        var employees = $("div#employees").children();
        console.log("Found " + employees.length + " employee(s)");

        for (var i = 0; i < employees.length; i++) {

            var name = $(employees[i]).data("name");
            var lat = $(employees[i]).data("lat");
            var lon = $(employees[i]).data("lon");

            var gLatLon;
            if (lat && lon) {

                gLatLon = new google.maps.LatLng(lat, lon);
                var marker = new google.maps.Marker({
                    position: gLatLon,
                    animation: google.maps.Animation.DROP
                });

                marker.setMap(map);

                var lastSeen = $(employees[i]).data("last-seen");

                //Setting marker

                //Building info window with employee name
                var infoWindow = new google.maps.InfoWindow(
                    {
                        content: name + " - (" + lastSeen + ")"
                    }
                );

                //Showing info window
                infoWindow.open(map, marker);

                setMarketListener(marker);
            }

            //Saving marker
            var empId = $(employees[i]).attr("id");
            markers[empId] = marker;

            //Drawing path
            var poly = new google.maps.Polyline({
                strokeColor: '#000000',
                strokeOpacity: 1.0,
                strokeWeight: 3
            });

            poly.setMap(map);
            locations[empId] = poly;

            if (gLatLon) {
                poly.getPath().push(gLatLon);
            }

        }


        function setMarketListener(marker) {

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
