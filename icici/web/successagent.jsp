<%@ page import="org.owasp.esapi.ESAPI" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2/18/14
  Time: 9:45 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<html>
<head>
    <title></title>
</head>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<body class="bodybackground">
<br><br><br><br><br><br><br><br>
<div class="qwipitab" style="margin-top: 0px">
    <p align="center" class="textb">New Agent Signup Form</p>

    <table  align="center" width="60%" cellpadding="2" cellspacing="2">

        <tr>
            <td>

                <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">

                    <tr bgcolor="#34495e">
                        <td width="2%" class="textb">&nbsp;</td>
                        <td class="label">
                            Message
                        </td>

                    </tr>
                    <tr bgcolor="#ecf0f1"><td colspan="4">&nbsp;</td></tr>
                    <tr bgcolor="#ecf0f1" align="center">
                        <td width="2%" class="textb">&nbsp;</td>
                        <td class="textb" valign="top">
                            Thank you, <b><%=ESAPI.encoder().encodeForHTML((String) request.getAttribute("username"))%></b> for registering with
                            Online Payment Gateway Service.
                        </td>
                    </tr>
                    <tr bgcolor="#ecf0f1"><td colspan="4">&nbsp;</td></tr>
                </table>
            </td>
        </tr>
    </table>
</div>
</body>
<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }

%>
</html>