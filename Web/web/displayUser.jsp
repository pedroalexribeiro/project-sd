<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<script type="text/javascript">
    var websocket = null;

    window.onload = function() { // URI = ws://10.16.0.165:8080/WebSocket/ws
        connect('ws://' + window.location.host + '/Web/ws');
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
        <title>Display User</title>
    </head>
    <body>
        <h>User</h><br>
        <p>Username:              ${session.searchUser.username}</p>
        <p>Email:                 ${session.searchUser.email}</p>
        <p>Name:                  ${session.searchUser.name}</p>
        <p>Personal Information:  ${session.searchUser.personalInfo}</p>
        <p id = "Editor">
            Editor:
            <c:choose>
                <c:when test="${session.searchUser.editor}">
                    Editor
                </c:when>
                <c:otherwise>
                    Non-Editor
                </c:otherwise>
            </c:choose>
        </p>


        <p><s:if test="hasActionMessages()">
            <div class="messages">
                <s:actionmessage />
            </div>
        </s:if>
        </p>

        <c:if test="${session.user.editor && !session.searchUser.editor}">
            <s:url action = "makeEditor" var="urlTagEditor">
                <s:param name="user">${session.searchUser.username}</s:param>
            </s:url>
            <s:a href="%{urlTagEditor}">
                Make Editor
            </s:a>
        </c:if>

        <s:form action="show_own_files" method="post">
            <s:hidden name="other_username" value="%{#attr.username}"></s:hidden>
            <s:hidden name="other_drop_id" value="%{#attr.dropbox_id}"></s:hidden>
            <s:submit type="button" value="Share a file" />
        </s:form>

        <a href="Menu.jsp">Back</a>
    </body>
</html>
