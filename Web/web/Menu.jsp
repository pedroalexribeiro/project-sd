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

            <p>
            <a href="add-dropbox">Add Dropbox</a>
            </p>>
            <a href="list-files-dropbox">List Files in Dropbox</a>
            <p>
            </p>
            <p>
            <a href="add-artist">Add Artist</a>
            </p>
            <p>
            <a href="add-album">Add Album</a>
            </p>
            <p>
            <a href="add-music">Add Music</a>
            </p>

            <s:if test="hasActionErrors()">
                <div class="errors">
                    <s:actionerror />
                </div>
            </s:if>
            <s:form action="search" method=x"get">
                <s:textfield name="str" label="Search"/>
                <s:submit />
            </s:form>
            <a href="logout">Logout</a>
    </body>
</html>
