<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<script type="text/javascript">
    var websocket = null;

    window.onload = function() { // URI = ws://10.16.0.165:8080/WebSocket/ws
        connect('ws://' + window.location.host + '/Web/ws');
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
        <title>Display Artist</title>
    </head>
    <body>
        <h>Artist</h><br>
        <p>${session.searchArtist.name}</p>
        <p>${session.searchArtist.details}</p>



        <c:choose>
            <c:when test = "${session.searchAlbums.size() > 0 || session.searchMusics.size() > 0}">
                <c:if test="${session.searchAlbums.size() > 0}">
                    <c:forEach items="${session.searchAlbums}" var="album" begin="0">
                        <div>
                            <h3>Album:</h3>
                            <p>Title: ${album.title}</p>
                            <br><p><b>Musics:</b></p>
                            <c:forEach items="${session.searchMusics}" var="music" begin="0">
                                <c:if test="${album.id == music.album_id}">
                                    <p>title:   ${music.name}</p>
                                </c:if>
                            </c:forEach>
                        </div>
                        <br><br>
                    </c:forEach>
                </c:if>
            </c:when>
            <c:otherwise>
                <p>No Albums/Musics Results</p>
            </c:otherwise>
        </c:choose>


    <c:if test="${session.user.editor}">
        <s:url action = "deleteArtist" var="urlTagDelete">
        </s:url>
        <s:a href="%{urlTagDelete}">
            Delete
        </s:a>
    </c:if>

        <c:if test="${session.user.editor}">
            <a href="artistEdit.jsp">Edit</a>
        </c:if>

        <a href="Menu.jsp">Back</a>
    </body>
</html>
