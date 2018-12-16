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
            if(document.getElementById("Editor") != null && message.data.contains("Editor")){
                document.getElementById("Editor").innerText = "Editor";
            }
            if(document.getElementById("Edit") != null){
                document.getElementById("Editor").innerText = "Editor";
            }
        }
    </script>
    <head>
        <title>Display Music</title>
    </head>
    <body>
        <h>Music</h><br>
        <p>title:   ${session.searchMusic.name}</p>
        <p>Genre:   ${session.searchMusic.type}</p>
        <p> Length: ${session.searchMusic.length}</p>
        <p>Lyrics:  ${session.searchMusic.lyrics}</p>


    <c:if test="${session.user.editor}">

        <s:url action = "deleteMusic" var="urlTagDelete">
        </s:url>
        <s:a href="%{urlTagDelete}">
            Delete
        </s:a>
    </c:if>

        <c:if test="${session.user.editor}">
            <a href="musicEdit.jsp">Edit</a>
        </c:if>

        <a href="Menu.jsp">Back</a>
    </body>
</html>
