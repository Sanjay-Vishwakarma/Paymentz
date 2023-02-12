<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<html>
<head>
    <title>Merchant Administration </title>
    <link rel="stylesheet" type="text/css" href="/merchant/style.css"/>
</head>

<body style="margin: 0px" text=#000000 vlink=#000000 alink=#000000 link=#000000 bgcolor=#ffffff leftmargin=0 topmargin=0
      marginwidth="0" marginheight="0">

<table class=search border="0" cellpadding="2" cellspacing="0" width="50%" align=center valign="center">
    <tr>
        <td align="center">
            <img src="/merchant/images/logo.gif" border="0">
        </td>
    </tr>
</table>
<br><br>
<table class=search border="0" bgcolor="#F1EDE0" cellpadding="2" cellspacing="0" width="600" bordercolorlight="#000000"
       bordercolordark="#FFFFFF" align=center valign="center">
    <tr>
        <td bgcolor="#9A1305" class="label" align="left">&nbsp;&nbsp;Finance Administration Module</td>
    </tr>
    <tr align="center">
        <td class="text">
            <%
                String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
                out.println("Your Login or Password was incorrect.");
                out.println("</p><p ALIGN=\"CENTER\"><a href=\"/newfinance/index.jsp\"><b>Please Try Again.</b></a></p>");
                out.println("<p ALIGN=\"CENTER\">If you have forgotten the password please contact support ");
                out.println("user on" + supportUrl);

            %>
        </td>
    </tr>
    <tr><td>&nbsp;</td></tr>
</table>


</body>
</html>
