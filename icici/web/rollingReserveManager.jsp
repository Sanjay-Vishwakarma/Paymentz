<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%--
  Created by IntelliJ IDEA.
  User: WAHEED
  Date: 5/29/14
  Time: 3:05 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="index.jsp" %>
<html>
<head>
    <title></title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<br>
<center><h2>Rolling Reserve Manager</h2></center>
<br>
<center>
    <table border="0">
        <tr>
            <td><form action="/icici/rollingreservefile.jsp?ctoken=<%=ctoken%>" method="POST"><input type="submit" value="Release Rolling Reserve" name="submit" class="buttonadmin"></form>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
            <td></td><td></td><td></td><td></td><td></td><td></td>
            <td><form action="/icici/generateRollingReserveFile.jsp?ctoken=<%=ctoken%>" method="POST"><input type="submit" value="Generate Rolling Reserve Release File" name="submit" class="buttonadmin"></form>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>

        </tr>
    </table>
</center>


<%


    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>

</body>
</html>