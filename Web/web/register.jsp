<%@ page language="java" contentType="text/html; charset = ISO-8859-1"
         pageEncoding="ISO-8859-1" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO" crossorigin="anonymous">
    <title>Register</title>
</head>

<body>

<div class="container">
    <div class="row">
        <div class="col">
            <div class="row" align="center">
                <div class="col">
                    <h1>Register</h1>
                </div>
            </div>
            <div class="row" align="center">
                <div class="col">
                    <s:form action="register" method="post">
                        <s:textfield name="username" label="Username"/><br>
                        <s:textfield name="password"  label="Password"/><br>
                        <s:textfield name="email"  label="Email"/><br>
                        <s:textfield name="name" label="Name"/><br>
                        <s:submit/>
                    </s:form>
                </div>
            </div>
            <div class="row" align="center">
                <div class="col">
                    <a href="index.jsp">Login</a>
                </div>
            </div>
        </div>
    </div>
</div>




</body>
</html>