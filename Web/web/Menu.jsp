<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>
        <title>Menu</title>
    </head>
    <body>
        <h>Menu</h>
            <p>Username: ${session.user.username}</p>
            <p>Password: ${session.user.password}</p>

            <s:if test="hasActionErrors()">
                <div class="errors">
                    <s:actionerror />
                </div>
            </s:if>
            <s:form action="search" method="get">
                <s:textfield name="str" label="Search"/>
                <s:submit />
            </s:form>
            <a href="logout">Logout</a>
    </body>
</html>
