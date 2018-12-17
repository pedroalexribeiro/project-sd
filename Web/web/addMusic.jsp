<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Add Music</title>
</head>
<body>
<s:if test="hasActionErrors()">
    <div class="errors">
        <s:actionerror />
    </div>
</s:if>
<s:form action="add_music" method="post">
    <s:textfield name="name" label="name"/><br>
    <s:textfield name="type" label="type"/><br>
    <s:textfield name="length" label="length"/><br>
    <s:textfield name="lyrics" label="lyrics"/><br>
    <s:textfield name="albumName" label="albumName"/><br>
    <s:submit />
</s:form>
</body>
</html>
