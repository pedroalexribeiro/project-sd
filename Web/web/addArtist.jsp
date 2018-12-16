<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: pedro
  Date: 10-12-2018
  Time: 22:22
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<s:form action="add_artist" method="post">
    <s:textfield name="name" label="name"/><br>
    <s:textfield name="details" label="details"/><br>
    <s:checkbox name="solo" fieldValue="false" label="Is the artist solo, if so check the box."/>
    <s:submit />
</s:form>
</body>
</html>
