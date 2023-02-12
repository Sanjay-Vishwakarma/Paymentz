<%@ page language="java" %>


<%
    session.setAttribute("valid", "true");
%>
<html>
<head>
    <title>Merchant Administration Login</title>
    <link rel="stylesheet" type="text/css" href="/merchant/style.css"/>
</head>

<body style="margin: 0px" text=#000000 vlink=#000000 alink=#000000 link=#000000 bgcolor=#ffffff leftmargin=0 topmargin=0
      marginwidth="0" marginheight="0" onLoad='document.forms[0].username.focus();'>
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

<form action="/merchant/servlet/Login" method=post>
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
                        out.println("*Your Login has been Disabled.<br><b>Please Contact Support </b>");
                    }
                %>
            </td></tr></table>
    <br>
    <%
        }
    %>
    <table bgcolor="#E4D6C9" align="center" width="50%" cellpadding="1" cellspacing="1">
        <tr>
            <td>
                <table border="1" bgcolor="#F1EDE0" cellpadding="2" cellspacing="0" width="100%"
                       bordercolorlight="#E4D6C9" bordercolordark="#E4D6C9" align=center valign="center">
                    <tr>
                        <td bgcolor="#9A1305" colspan="2" class="label">Merchant Login</td>
                    </tr>
                    <tr>
                        <td class="textb">Username: </td>
                        <td><input name="username" type="Text" value="" class="textBoxes"></td>
                    </tr>
                    <tr>
                        <td class="textb">Password: </td>
                        <td><input name="password" type="Password" value="" class="textBoxes"></td>
                    </tr>
                    <tr><td colspan="2" align="center"><INPUT type=submit value=Login class="submit"></td></tr>
                    <tr><td align="center">
                        <a href="/merchant/forgotpwd.jsp" class="link">Forgot Password?</a>
                    </td>
                        <td align="center">
                            <a href="/merchant/signup.jsp" class="link">New User?</a>
                        </td>
                    </tr>
                </table>
            </td></tr></table>
</form>
</body>
</html>


