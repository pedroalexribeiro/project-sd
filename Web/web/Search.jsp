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
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
        <title>Search</title>
    </head>
    <body>

    <div class="container">
        <div class="row">
            <div class="col-8">
                <h>Search</h>
            </div>
            <div class="col">
                <a href="Menu.jsp">Back</a>
            </div>
        </div>
        <div class="row" align="center">
            <c:choose>
                <c:when test = "${session.searchArtists.size() > 0 || session.searchAlbums.size() > 0  || session.searchMusics.size() > 0 || session.searchUsers.size() > 0}">
                    <div class="col">
                        <c:if test="${session.searchUsers.size() > 0}">
                            <h3>Users:</h3>
                            <c:forEach items="${session.searchUsers}" var="user" begin="0">
                                <s:url action = "displayUser" var="urlTagUser">
                                    <s:param name="username">${user.username}</s:param>
                                    <s:param name="email">${user.email}</s:param>
                                    <s:param name="name">${user.name}</s:param>
                                    <s:param name="personalInfo">${user.personalInfo}</s:param>
                                    <s:param name="editor">${user.editor}</s:param>
                                    <s:param name="dropbox_id">${user.dropbox_id}</s:param>
                                </s:url>
                                <div>
                                    <s:a href="%{urlTagUser}">
                                        ${user.username}
                                    </s:a>
                                </div>
                                <br><br>
                            </c:forEach>
                        </c:if>
                    </div>
                    <div class="col">
                    <c:if test="${session.searchArtists.size() > 0}">
                        <h3>Artists:</h3>
                        <c:forEach items="${session.searchArtists}" var="artist" begin="0">
                            <s:url action = "displayArtist" var="urlTagArtist">
                                <s:param name="name">${artist.name}</s:param>
                                <s:param name="details">${artist.details}</s:param>
                                <s:param name="id">${artist.id}</s:param>
                            </s:url>
                            <div>
                                <s:a href="%{urlTagArtist}">
                                    ${artist.name}
                                </s:a>
                                <p>Details: ${artist.details}</p>
                            </div>
                            <br><br>
                        </c:forEach>
                    </c:if>
                    </div>
                    <div class="col">
                    <c:if test="${session.searchAlbums.size() > 0}">
                        <h3>Albums:</h3>
                        <c:forEach items="${session.searchAlbums}" var="album" begin="0">
                            <s:url action = "displayAlbum" var="urlTagAlbum">
                                <s:param name="title">${album.title}</s:param>
                                <s:param name="releaseDate">${album.releaseDate}</s:param>
                                <s:param name="description">${album.description}</s:param>
                                <s:param name="id">${album.id}</s:param>
                                <s:param name="artist">${album.artist}</s:param>
                            </s:url>
                            <div>
                                <s:a href="%{urlTagAlbum}">
                                    ${album.title}
                                </s:a>
                                <p>Release Date: ${album.releaseDate}</p>
                                <p>Description:  ${album.description}</p>
                            </div>
                            <br><br>
                        </c:forEach>
                    </c:if>
                    </div>
                    <div class="col">
                    <c:if test="${session.searchMusics.size() > 0}">
                        <h3>Musics:</h3>
                        <c:forEach items="${session.searchMusics}" var="music" begin="0">
                            <s:url action = "displayMusic" var="urlTagMusic">
                                <s:param name="name">${music.name}</s:param>
                                <s:param name="type">${music.type}</s:param>
                                <s:param name="length">${music.length}</s:param>
                                <s:param name="lyrics">${music.lyrics}</s:param>
                                <s:param name="id">${music.id}</s:param>
                                <s:param name="album_id">${music.album_id}</s:param>
                            </s:url>
                            <div>
                                <s:a href="%{urlTagMusic}">
                                    ${music.name}
                                </s:a>
                                <p>Genre:  ${music.type}</p>
                                <p>Length: ${music.length}</p>
                                <p>Lyrics: ${music.lyrics}</p>
                            </div>
                            <br><br>
                        </c:forEach>
                    </c:if>
                    </div>
                </c:when>
                <c:otherwise>
                    <p>No Search Results</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
    </body>
</html>
