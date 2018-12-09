<%@ page language="java" contentType="text/html; charset = ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <title>Login</title>
</head>

<body>
<h1>Login</h1>
    <s:if test="hasActionErrors()">
       <div class="errors">
            <s:actionerror />
        </div>
    </s:if>
    <s:if test="hasActionMessages()">
        <div class="messages">
            <s:actionmessage />
        </div>
    </s:if>
        <s:form action="login" method="post">
        <s:textfield name="username" label="Username"/><br>
        <s:password name="password" label="Password"/><br>
        <s:submit />
    </s:form>
</body>
</html>