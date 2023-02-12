<%--
  Created by IntelliJ IDEA.
  User: Dhiresh
  Date: 19/2/13
  Time: 2:55 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>test e wallet</title>
</head>
<body>
<form name="form" action="/icici/servlet/MyMonedero" method="Post">
    
    <input type=submit value=submit>
</form>

<font color=red size=5>

    <%=request.getParameter("req1")%> <BR>
    <%=request.getParameter("req2")%> <BR>
    <%=request.getParameter("req3")%> <BR>
    <%=request.getParameter("req4")%> <BR>
    <%=request.getParameter("req5")%> <BR>

</font>
</body>
</html>