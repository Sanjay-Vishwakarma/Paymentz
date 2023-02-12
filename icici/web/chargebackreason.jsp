<%@ page import="com.directi.pg.core.GatewayAccount,
                 org.owasp.esapi.ESAPI,
                 com.directi.pg.Merchants" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
<html>
<head>
</head>

<body>
<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    Merchants merchants = new Merchants();
    if (merchants.isLoggedIn(session))
    {
    %>
<br><br><br>

<%  /*String errormsg=(String) request.getAttribute("error");
    if(errormsg!="")
    {
        out.println(errormsg);
    }*/
    Hashtable hash = (Hashtable) request.getAttribute("chargebackdetails");
    String postUrl, label;
    String action = (String) request.getAttribute("action");
    String rr_status = (String) request.getAttribute("rr_status");
    String note = "";
    Hashtable transactionDetailsHash = (Hashtable) hash.get("1");
    String accountId = (String) transactionDetailsHash.get("accountid");
    GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
    String currency = account.getCurrency();

    if ("chargeback".equals(action))
    {
        postUrl = "/icici/servlet/DoChargebackTransaction?ctoken="+ctoken;
        label = "Chargeback Request";
    }
    else
    {
        postUrl = "/icici/servlet/RetrivalRequestServlet?ctoken="+ctoken;
        label = "Send Retrival Request";
    }


%>
<table border=1 align=center width=50%>
    <TR>
        <TD colspan=2 valign="middle" align="center" bgcolor="#008BBA">Transaction Details</TD>
    </TR>
    <tr>
        <td width=100>Tracking Id</td><td><%=ESAPI.encoder().encodeForHTML((String) transactionDetailsHash.get("icicitransid"))%></td>
    </tr>
    <tr>
        <td>Transid</td><td><%=ESAPI.encoder().encodeForHTML((String) transactionDetailsHash.get("transid"))%></td>
    </tr>
    <tr>
        <td>Description</td><td><%=ESAPI.encoder().encodeForHTML((String) transactionDetailsHash.get("description"))%></td>
    </tr>
    <tr>
        <td>Amount</td><td><%=currency + " " + ESAPI.encoder().encodeForHTML((String) transactionDetailsHash.get("amount"))%></td>
    </tr>
    <tr>
        <td>Status</td><td><%=ESAPI.encoder().encodeForHTML((String) transactionDetailsHash.get("status"))%></td>
    </tr>
    <tr>
        <td>Company </td><td><%=ESAPI.encoder().encodeForHTML((String) transactionDetailsHash.get("company_name"))%></td>
    </tr>
</table>
<FORM METHOD=POST ACTION="<%=postUrl%>">
    <input type=hidden name="icicitransid" value=<%=ESAPI.encoder().encodeForHTMLAttribute((String)transactionDetailsHash.get("icicitransid"))%>>
    <input type="hidden" name="action" value=<%=action%>>
    <table border=1 align=center class=search>


        <TR>
            <TD colspan=2 valign="middle" align="center" bgcolor="#008BBA"><%=label%></TD>
        </TR>

        <%
            if ("chargeback".equals(action))
            {
                note = "(Required only if Retrival Request wasn't sent)";
        %>
        <TR>
            <TD>Chargeback Amount</TD>
            <TD><input name="cbamount"> Do partial chargeback <input type="checkbox" name="partial"></TD>
        </TR>

        <TR>
            <TD>CB Ref Number </TD>
            <TD><input name="cbrefnumber"></TD>
        </TR>

        <TR>
            <TD>CB Reason </TD>
            <TD><input name="cbreason" maxlength="100"  type="text" maxlength="255"></TD>
        </TR>

        <%
            }
        %>

        <TR>
            <TD>Last Date for Information <br><%=note%></TD>
            <TD><input maxlength="30"  name="date"></TD>
        </TR>

        <TR>
            <TD colspan=2 valign="middle" align="center" bgcolor="#008BBA"><input type=submit value="<%=label%>"></TD>
        </TR>
    </TABLE>
</FORM>
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