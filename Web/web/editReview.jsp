<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

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
    <title>Review Edit</title>
</head>

<body>
<h1>Review Edit</h1>
<c:if test="${session.searchReview == null}">
    <jsp:forward page="/createReview.jsp" />
</c:if>
Only 1 review per Album
<s:if test="hasActionErrors()">
    <div class="errors">
        <s:actionerror />
    </div>
</s:if>
<s:form action="reviewEdit" method="post">
    <s:textfield name="text" label="text" value="%{#session.searchReview.text}"></s:textfield>
    <s:textfield name="rating" type="number" label="rating" value="%{#session.searchReview.rating}"></s:textfield>
    <s:submit />
</s:form>

    <a href="displayAlbum.jsp">Back</a>
</body>
</html>