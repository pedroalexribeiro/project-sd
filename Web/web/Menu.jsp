<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<script type="text/javascript">
    var websocket = null;

    window.onload = function() { // URI = ws://10.16.0.165:8080/WebSocket/ws
        connect('ws://' + window.location.host + '/ws');
        document.getElementById("chat").focus();
    };
    function connect(host) { // connect to the host websocket
        if ('WebSocket' in window)
            websocket = new WebSocket(host);
        else if ('MozWebSocket' in window)
            websocket = new MozWebSocket(host);
        else {
            return;
        }
        websocket.onerror = onError;
        websocket.onclose = onClose;
        websocket.onopen  = onOpen;
        websocket.onmessage = onMessage;
    }

    function onOpen(){
        var user = "${session.user.username}";
        websocket.send(user);
    }
    function onClose(){

    }
    function onError(){

    }
    function onMessage(message) { // print the received message
        alert(message.data);
        if(document.getElementById("Editor") != null){
            document.getElementById("Editor").innerText = "Editor";
        }
    }
</script>
    <head>
        <title>Menu</title>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
    </head>
    <body>
        <div class="container">
            <div class="row row-header">
                <div class="col-8   ">
                    <h2>Menu</h2>
            <p>
            <a href="add-dropbox">Add Dropbox</a>
            </p>>
            <a href="list-files-dropbox">List Files in Dropbox</a>
            <p>
            </p>
            <p>
            <a href="add-artist">Add Artist</a>
            </p>
            <p>
            <a href="add-album">Add Album</a>
            </p>
            <p>
            <a href="add-music">Add Music</a>
            </p>
                </div>
                <div class="col">
                    <div class="row">
                        <div class="col">
                            Username: ${session.user.username}
                        </div>
                        <div class="col">
                            <a href="logout">Logout</a>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col my-auto">
                    <div class="row" align="center">
                        <div class="col">
                            <s:if test="hasActionErrors()">
                                <div class="errors">
                                    <s:actionerror />
                                </div>
                            </s:if>
                            <s:form action="search" method="get">
                                <s:textfield name="str" label="Search"/>
                                <s:submit />
                            </s:form>
                        </div>
                    </div>
                </div>
                <div class="col my-auto">
                    <div class="row" align="center">
                        <div class="col">
                            <p>Notifications</p>
                        </div>
                    </div>
                    <div class="row" align="center">
                        <div class="col">
                            <c:if test="${session.offlineNotifications.size() > 0}">
                                <c:forEach items="${session.offlineNotifications}" var="notification" begin="0">
                                    <div>
                                        <p>Text: ${notification.text}</p>
                                    </div>
                                    <br><br>
                                </c:forEach>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
