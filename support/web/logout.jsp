<%String company = ApplicationProperties.getProperty("COMPANY");%>

<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="com.directi.pg.CustomerSupport" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.sql.Timestamp" %>
<%@ page import="org.owasp.esapi.User" %>
<%
    Logger  log=new Logger("logger1");
    session.invalidate();
    ESAPI.authenticator().logout();
    CustomerSupport.sessionout_updateLogout();
%>
<html>
<head>
    <title>support Administration Logout</title>
    <link rel="stylesheet" type="text/css" href="/support/css/styles123.css"/>
</head>

<body >

<form action="/support/login.jsp" method=post>
    <br><br>

    <br>
    <table class=search border="0" cellpadding="2" cellspacing="0" width="50%" align=center valign="center">
        <tr>
            <td align="center">
                <img src="/support/images/pay1.png" border="0">
            </td>

        </tr>
    </table>
    <br><br>
    <table class=search  bgcolor="#ffffff" cellpadding="2" cellspacing="0" width="600"
           bordercolorlight="#000000" bordercolordark="#FFFFFF" align=center valign="center" style="border:1px solid #2c3e50 " >
        <tr>
            <td height="40px;" class="textb" align="left" style="background-color: #2c3e50;color: #ffffff;">&nbsp;&nbsp;support Administration Module</td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td>
        <tr align="center">
            <td  class="textb" >&nbsp;&nbsp;Thank you for visiting Customer Support Executive Module</td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr align="center">
            <td  class="textb">&nbsp;&nbsp;Click here <form action="/support/login.jsp" method="post"><input type="hidden"  value="here" name="partnerid"><input type="submit" value="LOGIN" name="submit" style="background:#34495e;color:#ffffff;border:0" > </form> to go back to the
                support login page </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td>


    </table>
    <table class=search border="0" cellpadding="2" cellspacing="0" width="600" align=center valign="center">
        <tr>
            <td align="center">

                <a target="_blank" href="http://sisainfosec.com/site/certificate/13835412899812182686"><IMG border=0 height=120 src="./images/Certificatelogo.png" width=210></a>


            </td>
        </tr>

    </table>
</form>
</body>
</html>


