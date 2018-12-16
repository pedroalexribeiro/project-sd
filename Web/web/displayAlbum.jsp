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
    <title>Display Album</title>
</head>
<body>
    <h>Album</h><br>
    <p>Title: ${session.searchAlbum.title}</p>
    <p>Release Date: ${session.searchAlbum.releaseDate}</p>
    <p>Description: ${session.searchAlbum.description}</p>
    <p>Artist: ${session.searchAlbum.artistName}</p>
    <p>Average Rating: ${session.searchAlbum.avgRating}</p>

    <c:choose>
    <c:when test = "${session.searchReviews.size() > 0 || session.searchMusics.size() > 0}">
        <c:if test="${session.searchMusics.size() > 0}">
            <h3>Musics:</h3>
            <c:forEach items="${session.searchMusics}" var="music" begin="0">
                <div>
                    <p>Title:  ${music.name}</p>
                    <p>Genre:  ${music.type}</p>
                    <p>Length: ${music.length}</p>
                    <p>Lyrics: ${music.lyrics}</p>
                </div>
                <br><br>
            </c:forEach>
        </c:if>

        <c:if test="${session.searchReviews.size() > 0}">
            <h3>Reviews:</h3>
            <c:forEach items="${session.searchReviews}" var="review" begin="0">
                <div>
                    <p>User:   ${review.username}"</p>
                    <p>Text:   ${review.text}</p>
                    <p>Rating: ${review.rating}</p>
                    <p>Date:   ${review.date}</p>
                </div>
                <br><br>
            </c:forEach>
        </c:if>
    </c:when>
    <c:otherwise>
        <p>No Reviews/Musics Results</p>
    </c:otherwise>
</c:choose>

    <s:url action = "chooseReview" var="urlTagReview">
    </s:url>
    <s:a href="%{urlTagReview}">
        Review
    </s:a>


    <c:if test="${session.user.editor}">

        <s:url action = "deleteAlbum" var="urlTagDelete">
        </s:url>
        <s:a href="%{urlTagDelete}">
            Delete
        </s:a>
    </c:if>

    <c:if test="${session.user.editor}">
        <a href="albumEdit.jsp">Edit</a>
    </c:if>

    <a href="Menu.jsp">Back</a>

</body>
</html>
