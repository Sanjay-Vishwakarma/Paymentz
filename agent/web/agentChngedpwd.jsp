<%@ page import="com.directi.pg.Functions" %>
<%--
  Created by IntelliJ IDEA.
  User: saurabh
  Date: 2/24/14
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

    if (agent.isLoggedInAgent(session))
    {
%>
<br><br><br><br><br><br><br>


<input type="hidden" value="<%=ctoken%>" name="ctoken">
<p>

            <%
                out.println(Functions.NewShowConfirmation1("Agent Change Password", "Password Changed Successfully<br><br>"));
            %>

<br>

</p>
<%
    }
    else
    {
        response.sendRedirect("/agent/logout.jsp");
        return;
    }
%>
<p>&nbsp;&nbsp; </p>
</body>
</html>