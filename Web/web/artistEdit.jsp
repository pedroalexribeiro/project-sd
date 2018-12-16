<%@ page language="java" contentType="text/html; charset = ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
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
    <title>Artist Edit</title>
</head>

<body>
<h1>Artist Edit</h1>
<s:if test="hasActionErrors()">
    <div class="errors">
        <s:actionerror />
    </div>
</s:if>
<s:form action="artistEdit" method="post">
    <s:textfield name="name" label="name" value="%{#session.searchArtist.name}"></s:textfield>
    <s:textfield name="details" label="details" value="%{#session.searchArtist.details}"></s:textfield>
    <s:submit />
</s:form>


<a href="displayArtist.jsp">Back</a>

</body>
</html>