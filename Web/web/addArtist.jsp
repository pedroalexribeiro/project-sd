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
<s:form action="add_artist" method="post">
    <s:textfield name="name" label="name"/><br>
    <s:textfield name="details" label="details"/><br>
    <s:submit />
</s:form>

<a href="Menu.jsp">Back</a>
</body>
</html>
