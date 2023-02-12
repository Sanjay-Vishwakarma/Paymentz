<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.payment.fraudAPI.APITester" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 6/3/14
  Time: 3:27 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="index.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=UTF-8" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <link rel="stylesheet" type="text/css" href="/icici/styyle.css" >

    <link rel="stylesheet" href="/icici/olddatepicker1/jquery-ui.css">
    <script src="/icici/olddatepicker1/jquery-1.9.1.js"></script>
    <script src="/icici/olddatepicker1/jquery-ui.js"></script>


    <link rel="stylesheet" href="/resources/demos/style.css">

    <title></title>
</head>
<body>
<%!
    Logger logger=new Logger("test");
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<center><h2>UTF-8 Test</h2></center>
<form action="/icici/servlet/UTF8TestServlet?ctoken=<%=ctoken%>" method="post" name="forms">
    <input type="hidden" value="<%=ctoken%>" name="ctoken">

    <table class="search" align=center border="1" cellpadding="2" cellspacing="0" width="80%" bordercolorlight="#000000" bordercolordark="#FFFFFF">
        <tr>
            <td>Enter The UTF Inputs:</td>
            <td><input type="text" value="" name="name" required></td>
        </tr>
        <tr>
            <td  align="center" colspan="2">
                <input type="submit" value="submit" >
            </td>

        </tr>
        <TR>
            <td colspan="2">

                <%
                    if(request.getAttribute("list")!=null)
                    {
                        out.println(request.getAttribute("list"));
                    }

                %>
            </td>

        </TR>
    </table>
    <br/>
</form>
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