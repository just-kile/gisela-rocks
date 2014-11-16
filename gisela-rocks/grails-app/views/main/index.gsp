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
</head>

<body>

<div class="jumbotron">
    <h1>Gisela, die Wanderkrabbe</h1>
    <g:if test="${current.isTraveling}">
        <p>
            Momentan @${current.location} (bis ${current.until.getDayOfMonth()}.${current.until.getMonthOfYear()}.).
        </p>
    </g:if>
    <g:else>
        <p>
            Momentan @zu Hause.
        </p>
    </g:else>
</div>

<div class="container">

    <div class="row">
        <div class="col-xs-6">
            <h2>Bisherige Reisen</h2>
            Gisela reist gern. Und deshalb auch oft.
            <ul>
                <g:each in="${previous}">
                    <li><b>${it.location}</b> (${it.start.getDayOfMonth()}.${it.start.getMonthOfYear()}.${it.start.getYear()} bis ${it.end.getDayOfMonth()}.${it.end.getMonthOfYear()}.${it.end.getYear()})
                    </li>
                </g:each>
            </ul>
        </div>

        <div class="col-xs-6">
            <h2>Kommende Reisen</h2>
            <g:if test="${upcoming}">
                <g:each in="${upcoming}">
                    <li><b>${it.location}</b> (${it.start.getDayOfMonth()}.${it.start.getMonthOfYear()}.${it.start.getYear()} bis ${it.end.getDayOfMonth()}.${it.end.getMonthOfYear()}.${it.end.getYear()})
                    </li>
                </g:each>

            </g:if>
            <g:else>
                Gisela will sich erst einmal ausruhen und hat deswegen noch keine konkreten Pläne für die Zukunft.
            </g:else>
        </div>
    </div>

    <div class="row">
        <p>
            Giselas Kalendar findest Du <a
                href="https://www.google.com/calendar/embed?src=5hpviugc2v1c9vkoudqj1po9jo%40group.calendar.google.com&ctz=Europe%2FBerlin"
                target="_blank">hier</a>.
        </p>
    </div>

</div>

<div style="background: #DDDDDD; width=100%; padding-left: 2em">
    www.gisela.rocks is brought to you by justKile. To be found on <a href="https://github.com/just-kile/gisela-rocks" _target="_blank">github</a>.
</div>

<p>

</p>
</body>
</html>
