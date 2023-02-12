<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page language="java" %>

<%
    String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
%>
<html>
<head>
    <title>Finance Administration Login</title>
    <link rel="stylesheet" type="text/css" href="/merchant/style.css"/>
</head>

<body style="margin: 0px" text=#000000 vlink=#000000 alink=#000000 link=#000000 bgcolor=#ffffff leftmargin=0 topmargin=0
      marginwidth="0" marginheight="0">


<form action="/newfinance/servlet/FinanceLogin" method=post>
    <br><br>


    <br>
    <table class=search border="0" cellpadding="2" cellspacing="0" width="50%" align=center valign="center">
        <tr>
            <td align="center">
                <img src="/merchant/images/logo.gif" border="0">
            </td>
        </tr>
    </table>
    <br><br>
    <%
        String action = request.getParameter("action");
        if (action != null)
        {
    %>
    <table width="100%">
        <tr>
            <td class="text" align="center">
                <%
                    if (action.equals("F"))
                    {
                        out.println("*Your Login or Password was incorrect.");
                    }
                    else
                    {
                        out.println("*Your Login has been Disabled.<br><b>Please Contact Support " + supportUrl + "</b>");
                    }
                %>
            </td></tr></table>
    <br>
    <%
        }
    %>

    <table class=search border="1" bgcolor="#F1EDE0" cellpadding="2" cellspacing="0" width="50%"
           bordercolorlight="#000000" bordercolordark="#FFFFFF" align=center valign="center">
        <tr>
            <td bgcolor="#9A1305" colspan="2" class="label">Finance Login</td>
        </tr>
        <tr>
            <td class="textb">Username: </td>
            <td><input name="username" type="Text" value=""></td>
        </tr>
        <tr>
            <td class="textb">Password: </td>
            <td><input name="password" type="Password" value=""></td>
        </tr>
        <tr><td colspan="2" align="center"><INPUT type=submit value=Login class="submit"></td></tr>
    </table>
</form>
</body>
</html>


