<%@ page import="java.net.InetAddress" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2/17/14
  Time: 1:05 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<%
    out.println("get Remot ADDR :" + request.getRemoteAddr() + "<br>");
    out.println("get Remot CONTEXT :"+request.getContextPath()+"<br>");
    out.println("get Remot URI :"+request.getRequestURI()+"<br>");
    out.println("get Remot USER :"+request.getRemoteUser()+"<br>");
    out.println("get Remot HOST :"+request.getRemoteHost()+"<br>");
    out.println("get Remot SCHEME :"+request.getScheme()+"<br>");
    out.println("get Remot Server :"+request.getServerName()+"<br>");
    out.println("get remote IP :"+request.getHeader("X-Forwarded-For")+"<br>");
    out.println("name   "+InetAddress.getByName("200.124.146.132").getHostName());
%>
</body>
</html>