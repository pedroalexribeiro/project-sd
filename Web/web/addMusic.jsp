<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: pedro
  Date: 09-12-2018
  Time: 1:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Add Music</title>
</head>
<body>
<s:form action="add_music" method="post">
    <s:textfield name="name" label="name"/><br>
    <s:textfield name="genre" label="genre"/><br>
    <s:textfield name="length" label="length"/><br>
    <s:textfield name="lyrics" label="lyrics"/><br>
    <s:textfield name="albumName" label="albumName"/><br>
    <s:submit />
</s:form>
</body>
</html>
