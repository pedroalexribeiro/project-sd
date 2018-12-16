<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
            <br>
            <a href="add-dropbox">Add Dropbox</a>
            <br>
            <a href="list-files-dropbox">List Files in Dropbox</a>
        <br>
        <br>
        <a href="add-artist">Add Artist</a>
        <br>
        <a href="add-album">Add Album</a>
        <br>
        <a href="add-music">Add Music</a>
    </body>
</html>
