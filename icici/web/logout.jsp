<%@ page import="com.directi.pg.TransactionEntry,
                 org.owasp.esapi.ESAPI" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%

    session.invalidate();
    ESAPI.authenticator().logout();
%>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title>Admin Logout</title>
    <link rel="stylesheet" type="text/css" href="/icici/css/new/styles123.css">
    <script src='/icici/css/umd/popper.js'></script>
    <script src='/icici/css_new/bootstrap.min.js'></script>

    <link href="/icici/css_new/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">
</head>

<body >

<form action="/icici/admin/login.jsp" method=post>
    <br><br><br><br>
    <table class=search border="0" cellpadding="2" cellspacing="0" width="50%" align=center valign="center">
        <tr>
            <td align="center">
                <img src="/icici/images/paylogo_icon.png">
            </td>

        </tr>
    </table>
    <br><br>
    <table class=search border="1" bgcolor="#ffffff" cellpadding="2" cellspacing="0" width="600"
           bordercolorlight="#000000" bordercolordark="#FFFFFF" align=center valign="center">
        <tr>
            <td height="40px;" class="textb" align="left" style="background-color: #2c3e50;color: #ffffff;">&nbsp;&nbsp;Administration Module</td>
        </tr>
        <tr><td>
            <table class=search border="0" bgcolor="#ffffff" cellpadding="2" cellspacing="0" width="600"
                   bordercolorlight="#000000" bordercolordark="#FFFFFF" align=center valign="center">
                <tr><td>&nbsp;</td></tr>
                <tr align="center">
                    <td  class="textb" >&nbsp;&nbsp;Thank you for visiting Administration Module</td>
                </tr>
                <tr><td>&nbsp;</td></tr>
                <tr align="center">
                    <td  class="textb">&nbsp;&nbsp;Click here <form action="/icici/admin/login.jsp" method="post"><input type="submit" value="LOGIN" name="submit" style="background:#34495e;color:#ffffff;border:0" > </form> to go back to the
                        Admin login page </td>
                </tr>
                <tr><td>&nbsp;</td></tr>
            </table>
        </td></tr>

    </table>



</form>
</body>
</html>


