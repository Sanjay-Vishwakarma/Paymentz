<%@ page import="org.owasp.esapi.ESAPI" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 12/2/13
  Time: 1:54 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<html>
<head>
    <title></title>
</head>

<body class="bodybackground">
<br><br><br><br><br><br><br><br>
<div class="qwipitab" style="margin-top: 0px">
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<p align="center" class="textb"><b>New Partner Signup Form</b></p>


<table  width="100%" >

    <tr><td>&nbsp;</td></tr>
    <tr>
        <td class="textb" align="center" >Please enter a new username</td>
    </tr>

</table>
<%
    String errormsg                 = ESAPI.encoder().encodeForHTML((String) request.getAttribute("error"));
    String sendEmailNotification    = (String) request.getParameter("sendEmailNotification");

    if (errormsg != null)
    {       out.println("<table align=\"center\"  ><tr><td>");
        out.println("<br><font class=\"textb\">");
        out.println(errormsg);
        out.println("</font>");
        out.println("</td></tr></table>");
    }
    else
    {
        out.println("<table align=\"center\"  ><tr><td>");
        out.println("<br><font class=\"textb\">");
        out.println("The username ("+ESAPI.encoder().encodeForHTML((String) request.getAttribute("username"))+") specified by you already exists for partner.");
        out.println("</font>");
        out.println("</td></tr></table>");
    }
%>
    <br>
<table  align="center" width="30%" cellpadding="2" cellspacing="2">

    <form action="/icici/servlet/NewPartner?ctoken=<%=ctoken%>" method="post" name="form1">

        <tr>
            <td>

                <table border="0" cellpadding="5" cellspacing="0" style="width:100%" bgcolor="#ecf0f1" align="center">

                    <tr class="texthead" >
                        <td width="2%">&nbsp;</td>
                        <td colspan="3" >
                            Partner Information (New Login)
                        </td>

                    </tr>
                    <tr><td colspan="4">&nbsp;</td></tr>
                    <tr>
                        <td width="2%" class="textb">&nbsp;</td>
                        <td width="43%" class="textb" valign="top">New Login *</td>
                        <td width="5%" class="textb" valign="top">:</td>
                        <td width="50%" valign="top">
                            <input type="text" maxlength="100"  value="" class="txtbox" name="username">
                            <input type="hidden" value="<%=sendEmailNotification%>"  name="sendEmailNotification">
                        </td>
                    </tr>
                    <tr><td colspan="4">&nbsp;</td></tr>
                    <tr>
                        <td colspan="4" align="right">
                            <input type="hidden" value="3" name="step">
                            <button type="submit" name="submit" class="buttonform">
                                <i class="fa fa-clock-o" class="iconbuttonform"></i>
                                &nbsp;&nbsp;Save
                            </button>
                        </td>
                    </tr>
                    <tr><td colspan="4">&nbsp;</td></tr>
                </table>

            </td>
        </tr>
    </form>
</table>
</div>
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