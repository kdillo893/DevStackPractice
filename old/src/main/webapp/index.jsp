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
        <script>
            function requestUser(data) {
                let url = "api/users/";
                url += data.id.value;

                fetch(url)
                        .then(data => {
                            return data.json();
                        })
                        .then(response => {
                            console.log(response);
                        })
                        .catch(error => {
                            console.log(error);
                        });

                return;
            }
        </script>
        <link rel="stylesheet" href="./css/index.css"/>
    </head>
    <body>
        <h1>Random Landing Page</h1>
        <a href="api">API Root</a>

        <p>I highly recommend using the next-js page instead of this... I don't plan on touching this</p>

        <form onsubmit="requestUser(this);
                return false;">
            <input type="text" name="id">
            <input type="submit">
        </form>


        <%--maybe I can do a "pull in from servlet" thing here? --%>


    </body>
</html>
