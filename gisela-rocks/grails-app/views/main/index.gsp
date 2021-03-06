<%--
  Created by IntelliJ IDEA.
  User: thomas
  Date: 25.10.14
  Time: 05:09
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Gisela, die Wanderkrabbe</title>

    <script src="https://maps.googleapis.com/maps/api/js?v=3.exp"></script>
    <script>
        function initialize() {
            var mapOptions = {
                zoom: 2,
                center: new google.maps.LatLng(22.854889, 10.259410)
            }
            var map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);

            <g:each in="${previous}">
                <g:if test="${it.coordinates}">
                new google.maps.Marker({
                    position: new google.maps.LatLng(${it.coordinates.latitude}, ${it.coordinates.longitude}),
                    map: map,
                    title: "${it.location}"
                });
                </g:if>
            </g:each>

            <g:if test="${current}">
            new google.maps.Marker({
                position: new google.maps.LatLng(${current.coordinates.latitude}, ${current.coordinates.longitude}),
                map: map,
                title: "${current.location}",
                icon: "http://gmapsmarkergenerator.eu01.aws.af.cm/getmarker?scale=1&color=00ff00"
            });
            </g:if>
        }

        google.maps.event.addDomListener(window, 'load', initialize);

    </script>

</head>


<body>

<div class="jumbotron">
    <h1>Gisela, die Wanderkrabbe</h1>
    <g:if test="${current}">
        <p>
            Momentan @${current.location} (bis ${current.end.getDayOfMonth()}.${current.end.getMonthOfYear()}.).
        </p>
    </g:if>
    <g:else>
        <p>
            Momentan @zu Hause.
        </p>
    </g:else>
</div>

<div class="container-fluid" style="background-color: #f1b935">

    <div class="container">
        <div class="row">

            <div class="col-xs-12 col-md-6">
                <h2>Bisherige Reisen</h2>
                Gisela reist gern. Und deshalb auch oft. Sie ist insgesamt schon <b>${(int) statistics.totalDistance}km</b> gewandert!
                <ul>
                    <g:each in="${previous}">
                        <g:render template="travel" model="${it}"/>
                    </g:each>
                </ul>
            </div>

            <div class="col-xs-12 col-md-6">
                <h2>Kommende Reisen</h2>
                <g:if test="${upcoming}">
                    <ul>
                        <g:each in="${upcoming}">
                            <g:render template="travel" model="${it}"/>
                        </g:each>
                    </ul>
                </g:if>
                <g:else>
                    Gisela will sich erst einmal ausruhen und hat deswegen noch keine konkreten Pläne für die Zukunft.
                </g:else>
            </div>
        </div>

    </div>

</div>

<div class="container-fluid" style="background-color: #000000; color:#ffffff">
    <div class="row">
        <div id="map-canvas"></div>
    </div>
</div>

<div class="container-fluid" style="background-color: #f1b935">

    <div class="container">
        <div class="row">
            <div class="col-xs-12">
                <h2>Kontakt</h2>

                <p>
                    Gisela ist natürlich auf <a href="https://www.facebook.com/giselakrabbe "
                                                target="_blank">Facebook</a>.
                </p>

                Sie freut sich sehr auf Deine Urlaubskarte. Schreibe ihr!<br/>
                <dd class="text-indent-4"><b>Gisela die Wanderkrabbe</b></dd>
                <dd class="text-indent-4">Postfach 10 13 29</dd>
                <dd class="text-indent-4">45013 Essen</dd>
            </dd>

            </p>
                <p>
                    Giselas Kalendar findest Du <a
                        href="https://www.google.com/calendar/embed?src=5hpviugc2v1c9vkoudqj1po9jo%40group.calendar.google.com&ctz=Europe%2FBerlin"
                        target="_blank">hier</a>.
                </p>
            </div>
        </div>
    </div>
</div>

<div class="container-fluid" style="background-color: #000000; color:#ffffff">

    <div class="container">
        <div class="row">
            <div class="col-xs-12">
                www.gisela.rocks is brought to you by justKile. To be found on <a
                    href="https://github.com/just-kile/gisela-rocks"
                    _target="_blank">github</a>.
            </div>
        </div>
    </div>
</div>

</body>
</html>
