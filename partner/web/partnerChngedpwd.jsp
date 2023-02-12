<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 10/18/13
  Time: 12:20 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<html>
<head>
    <title></title>
</head>
<body>
<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    if (partner.isLoggedInPartner(session))
    {
%>
<p class="title"> Partner Change Password</p>
<input type="hidden" value="<%=ctoken%>" name="ctoken">
<p>
<table border="0" width="750" align="center">
    <tr><td>&nbsp;</td></tr>
    <tr>
        <td class="subtitle" align="center">Password Changed Successful</td>
    </tr>

    <tr><td>&nbsp;</td></tr>
</table>


<br>

</p>
<%
    }
    else
    {
        response.sendRedirect("/partner/logout.jsp");
        return;
    }
%>
<p>&nbsp;&nbsp; </p>
</body>
</html>