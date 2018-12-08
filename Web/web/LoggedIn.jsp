<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
         pageEncoding="ISO-8859-1"%>

<html>
<head>
    <title>Hello World</title>
</head>

<body>
    <p>Username: ${session.username}</p>
    <p>Password: ${session.password}</p>
</body>
</html>
