<%--
  Created by IntelliJ IDEA.
  User: sanjeev
  Date: 4/6/14
  Time: 1:37 PM
--%>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>

    <meta name="layout" content="linksharing"> </meta>
    <title>Login</title>

    <link rel="stylesheet" href="${resource(dir: 'css', file: 'main.css')}" type="text/css">
    <link rel="stylesheet" href="${resource(dir: 'css' ,file: 'formlogin.css')}" type="text/css">



</head>

<body>
<g:form controller="newUser" action="resetPassword">
    My LinkSharing Email Id:<g:textField name="emailid" required=""></g:textField>
    <g:submitButton name="submit" value="Reset My Password"></g:submitButton>
</g:form>
</body>
</html>