<%@ page import="com.directi.pg.Merchants,com.logicboxes.util.ApplicationProperties" %>
<%
    String supportUrl = ApplicationProperties.getProperty("COMPANY_SUPPORT_URL");
    String phpurl = (String) application.getAttribute("merchantpath");
    Merchants merchants = new Merchants();
    if (!merchants.isLoggedIn(session))
    {
%>
<html>
<head>
    <title>Merchant Administration Logout</title>
    <link rel="stylesheet" type="text/css" href="/merchant/style.css"/>
</head>

<body style="margin: 0px" text=#000000 vlink=#000000 alink=#000000 link=#000000 bgcolor=#ffffff leftmargin=0 topmargin=0
      marginwidth="0" marginheight="0">

<form action="index.jsp" method=post>
    <br><br>

    <br>
    <table class=search border="0" cellpadding="0" cellspacing="0" width="50%" align=center valign="center">
        <tr>
            <td align="center">
                <img src="/merchant/images/logo.gif" border="0">
            </td>
        </tr>
    </table>
    <br><br>
    <table class=search border="0" bgcolor="#F1EDE0" cellpadding="2" cellspacing="0" width="600"
           bordercolorlight="#000000" bordercolordark="#FFFFFF" align=center valign="center">
        <tr>
            <td bgcolor="#9A1305" class="label" align="left">&nbsp;&nbsp;Merchant Administration Module</td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp;Your session has expired </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp;Click <a href="index.jsp" class="link">here</a> to go to the Merchant login
                page </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
    </table>
</form>
</body>
</html>
<%
        return;
    }
%>
<link rel="stylesheet" type="text/css" href="/merchant/style.css"/>
<table align=center border="0" cellspacing="0" cellpadding="2" bordercolor="#FFFFFF" color="#FFFFFF" width="100%">
    <tr><td>
        <table align="center" border="0" cellspacing="2" cellpadding="0" color="#000000" width="100%">
            <tr>
                <td colspan="7">
                    <img src="<%=phpurl%>/images/logo.gif" border="0">
                </td>
            </tr>
            <tr bgcolor="#000000">
                <td width="14%" height="30" valign="middle" align="center"><a href="<%=phpurl%>/servlet/Accounts"
                                                                              class="menufont">Accounts</a></td>
                <td width="14%" height="30" valign="middle" align="center"><a href="<%=phpurl%>/withdrawal.jsp"
                                                                              class="menufont">Withdraw</a></td>
                <td width="14%" height="30" valign="middle" align="center"><a href="<%=phpurl%>/settings.jsp"
                                                                              class="menufont">Settings</a>
    </td>
        <td width="14%" height="30" valign="middle" align="center"><a href="<%=phpurl%>/servlet/Transactions"
                                                                      class="menufont">Transactions</a></td>
        <td width="14%" height="30" valign="middle" align="center"><a href="<%=phpurl%>/servlet/Pod" class="menufont">POD</a>
        </td>
        <td width="14%" height="30" valign="middle" align="center"><a href="<%=phpurl%>/servlet/ReverseList"
                                                                      class="menufont">Reversal</a></td>
        <td width="14%" height="30" valign="middle" align="center"><a href="<%=phpurl%>/Logout.jsp" class="menufont">Logout</a>
        </td>
    </tr>
    <tr>
        <td align="left" colspan="4" class="merchant">
            <%
                if (merchants.getCompany((String) session.getAttribute("merchantid")) != null)
                    out.println(merchants.getCompany((String) session.getAttribute("merchantid")));
            %>
        </td>
        <td align="right" colspan="3" class="merchant">
            Member Id: <%=(String) session.getAttribute("merchantid")%>
        </td>
    </tr>
    <tr><td colspan="6">&nbsp;</td><td colspan="1" align="right" valing="middle" class="merchant"><a
            href="<%=supportUrl%>tc/servlet/KBServlet/cat7.html" target="_new"><font size="1">Need
        Support</font><img border="0" src="/merchant/images/help.gif"></a></td></tr>
</table>
</TD></TR></TABLE>
