<%@ page language="java" contentType="text/html; charset = ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <title>Register</title>
</head>

<body>
<h1>Register</h1>
<s:form action="register" method="post">
    <s:textfield name="username" label="Username"/><br>
    <s:textfield name="password"  label="Password"/><br>
    <s:textfield name="email"  label="Email"/><br>
    <s:textfield name="name" label="Name"/><br>
    <s:submit/>
</s:form>
</body>
</html>