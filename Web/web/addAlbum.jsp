<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: pedro
  Date: 10-12-2018
  Time: 23:01
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<s:form action="add_album" method="post">
    <s:textfield name="title" label="title"/><br>
    <s:textfield name="release_date" label="realease_date"/><br>
    <s:textfield name="description" label="description"/><br>
    <s:textfield name="artistName" label="artist name"/><br>
    <s:submit />
</s:form>
</body>
</html>
