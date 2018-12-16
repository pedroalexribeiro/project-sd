<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%--
  Created by IntelliJ IDEA.
  User: pedro
  Date: 15-12-2018
  Time: 15:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
    <c:forEach items="${files}" var="file">
        <c:out value="${file.name}" />
        <s:form action="associate_file" method="post">
            <s:hidden name="username" value="%{#attr.user.username}"></s:hidden>
            <s:hidden name="dropbox_id" value="%{#attr.file.id}"></s:hidden>
            <s:hidden name="music_id" value="%{#attr.musicId}"></s:hidden>
            <s:submit type="button" value="Associate File" />
        </s:form>
        <br>
    </c:forEach>
</body>
</html>
