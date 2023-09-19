<%-- 
    Document   : index
    Created on : Sep 16, 2023, 5:50:46 PM
    Author     : kdill
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%--@taglib prefix="f" uri="http://java.sun.com/jsf/core"--%>
<%--@taglib prefix="h" uri="http://java.sun.com/jsf/html"--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>JSP Page</title>
    </head>
    <body>
        <h1><h:outputText value="Hello World!"/></h1>
        <a href="api">Servlet Button</a>

        <h2>Maybe this is different</h2>
        <form action="api" method="GET">
            <input type="text" name="uid">
            <input type="submit" value="login">
        </form>
    </body>
</html>
