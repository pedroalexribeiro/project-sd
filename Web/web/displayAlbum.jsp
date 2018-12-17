<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<script src="https://code.jquery.com/jquery-3.3.1.min.js"
        integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8="
        crossorigin="anonymous"></script>
<script type="text/javascript">
    var websocket = null;

    window.onload = function() { // URI = ws://10.16.0.165:8080/WebSocket/ws
        connect('ws://' + window.location.host + '/ws');
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
        if(message.data === "review"){
            console.log("asdasdasd");
            $.ajax ({
                url: '<s:url action="callAction"/>',
                type: 'POST',
                dataType: 'text',
                success: function (data) {
                    var str = "";
                    var f_split = data.split(";;");
                    var average_rating = f_split[1].split("||");
                    document.getElementById("avg_rate").innerHTML = average_rating[1];
                    console.log(f_split[0]
                    );
                    var info_reviews = f_split[0].split("||");
                    var reviews = info_reviews.split("**");
                    var ind;
                    for (var i=0; i<reviews[0].length;i++){
                        ind = reviews[i].split(";");
                        for (var j=0; j<ind.length;j++){
                            var atrib = ind[j].split("|");
                            str += "<p>" + atrib[0] + atrib[1] + "</p>";
                        }
                        str += "<br><br>";
                    }
                    document.getElementById("reviews").innerHTML = str;
                    console.log(data);
                }
            });
        }else{
            alert(message.data);
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
    <p id="avg_rate">Average Rating: ${session.searchAlbum.avgRating}</p>

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
            <div id="reviews">
                <c:forEach items="${session.searchReviews}" var="review" begin="0">
                    <p>User:   ${review.username}"</p>
                    <p>Text:   ${review.text}</p>
                    <p>Rating: ${review.rating}</p>
                    <p>Date:   ${review.date}</p>
                <br><br>
                </c:forEach>
            </div>
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
