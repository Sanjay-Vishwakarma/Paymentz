<%String company = (String)session.getAttribute("company");%>
<%@ page import="com.directi.pg.TransactionEntry" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%
    TransactionEntry transactionentry = (TransactionEntry) session.getAttribute("transactionentry");
    if (transactionentry != null)
    {
        transactionentry.closeConnection();
    }
    session.invalidate();
    ESAPI.authenticator().logout();
%>
<html>
<head>
    <title>Virtual Logout</title>
    <link rel="stylesheet" type="text/css" href="/merchant/style.css"/>
</head>

<body style="margin: 0px" text=#000000 vlink=#000000 alink=#000000 link=#000000 bgcolor=#ffffff leftmargin=0 topmargin=0
      marginwidth="0" marginheight="0">

<form action="index.jsp" method=post>
    <br><br>

    <br>
    <table class=search border="0" cellpadding="2" cellspacing="0" width="50%" align=center valign="center">
        <tr>
            <td align="center">
                <img src="/merchant/images/<%=session.getAttribute("logo")%>" border="0">
            </td>
        </tr>
    </table>
    <br><br>
    <table class=search border="0" bgcolor="#CCE0FF" cellpadding="2" cellspacing="0" width="600"
           bordercolorlight="#000000" bordercolordark="#FFFFFF" align=center valign="center">
        <tr>
            <td bgcolor="#007ACC" class="label" align="left">&nbsp;&nbsp;Virtual Merchant</td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp;Thankyou for visiting <%=company%>'s Merchant Virtual Module</td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp;Click <a href="/merchant/virtualLogin.jsp" class="link">here</a> to go back to the
                Merchant login page </td>
        </tr>
        <tr><td>&nbsp;</td></tr>


    </table>
</form>
</body>
</html>


