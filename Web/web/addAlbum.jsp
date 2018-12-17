<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<s:if test="hasActionErrors()">
    <div class="errors">
        <s:actionerror />
    </div>
</s:if>
<s:form action="add_album" method="post">
    <s:textfield name="title" label="title"/><br>
    <s:textfield name="releaseDate" label="release Date"/><small>yyyy-mm-dd</small><br>
    <s:textfield name="description" label="description"/><br>
    <s:textfield name="artist" label="artist Name"/><br>
    <s:submit />
</s:form>
</body>
</html>
