<%@ page import="java.util.Enumeration" %>
<%--
  Created by IntelliJ IDEA.
  User: Vivek
  Date: 2/22/2021
  Time: 2:49 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
  <script>function closeTab(){window.close();}</script>
</head>
<body onload="closeTab()">
<%Enumeration enumeration=request.getParameterNames();
while (enumeration.hasMoreElements())
{
  String key= (String) enumeration.nextElement();
  System.out.println(key+" - "+request.getParameter(key));
}
%>
</body>
</html>
