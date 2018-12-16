<%@ taglib prefix="s" uri="/struts-tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <title>Menu</title>
    </head>
    <body>
        <h>Search</h><br>
        <c:choose>
            <c:when test = "${session.searchArtists.size() > 0 || session.searchAlbums.size() > 0  || session.searchMusics.size() > 0}">
                <c:if test="${session.searchArtists.size() > 0}">
                    <h3>Artists:</h3>
                    <c:forEach items="${session.searchArtists}" var="artist" begin="0">
                        <a><c:out value = "${artist}"/></a>
                        <br><br>
                    </c:forEach>
                </c:if>


                <c:if test="${session.searchAlbums.size() > 0}">
                    <h3>Albums:</h3>
                    <c:forEach items="${session.searchAlbums}" var="album" begin="0">
                        <a><c:out value = "${album}"/></a>
                        <br><br>
                    </c:forEach>
                </c:if>

                <c:if test="${session.searchMusics.size() > 0}">
                    <h3>Musics:</h3>
                    <c:forEach items="${session.searchMusics}" var="music" begin="0">
                        <a><c:out value = "${music}"/></a>
                        <br><br>
                    </c:forEach>
                </c:if>
            </c:when>
            <c:otherwise>
                <p>No Search Results</p>
            </c:otherwise>
        </c:choose>
    </body>
</html>
