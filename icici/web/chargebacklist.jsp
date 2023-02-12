<%@ page import="com.directi.pg.core.GatewayAccount,
                 com.directi.pg.Merchants" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ include file="functions.jsp" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp" %>
<html>
<head>
</head>

<body>
<center>
<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
    %>
<br><br><br>

<h1>Admin Transaction Search Interface.</h1>

<%

        String errormsg = (String)request.getAttribute("cbmessage");
        if (errormsg == null)
        errormsg = "";

        out.println("<table align=\"center\"  ><tr><td valign=\"middle\"><font class=\"text\" >");

        out.println(errormsg);

        out.println("</font></td></tr></table>");


  %>

<%
    Hashtable hash = (Hashtable) request.getAttribute("transdetails");

    if (hash != null)
    {
        String str="str";
        Hashtable temphash = null;

        int pageno = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
        int pagerecords = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
        str = str + "SRecords=" + pagerecords;
     str = str + "&ctoken="+ctoken;
        int records = 0;
        int totalrecords = 0;

        try
        {
            records = Integer.parseInt((String) hash.get("records"));
            totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
        }
        catch (Exception ex)
        {
        }

        if (records > 0)
        {
%>

<font color=blue>chargeback of ICICI transaction.</font>
<br><br><br>
<font class="info">Total records <%=totalrecords%></font>
<table align=center>
    <tr>
        <td valign="middle" align="center" bgcolor="#008BBA">Sr no</td>
        <td valign="middle" align="center" bgcolor="#008BBA">Tracking ID</td>
        <td valign="middle" align="center" bgcolor="#008BBA">Transid</td>
        <td valign="middle" align="center" bgcolor="#008BBA">Authid</td>
        <td valign="middle" align="center" bgcolor="#008BBA">Captureid</td>
        <td valign="middle" align="center" bgcolor="#008BBA">AuthCode</td>
        <td valign="middle" align="center" bgcolor="#008BBA">CaptureRRN</td>
        <td valign="middle" align="center" bgcolor="#008BBA">Description</td>
        <td valign="middle" align="center" bgcolor="#008BBA">Capture Amount</td>
        <td valign="middle" align="center" bgcolor="#008BBA">Status</td>
        <td valign="middle" align="center" bgcolor="#008BBA" colspan=3>Action</td>
    </tr>
    <%
        String style = "class=td1";
        String ext = "light";

        for (int pos = 1; pos <= records; pos++)
        {
            String id = Integer.toString(pos);

            int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

            if (pos % 2 == 0)
            {
                style = "class=td2";
                ext = "dark";
            }
            else
            {
                style = "class=td1";
                ext = "light";
            }

            temphash = (Hashtable) hash.get(id);

            String tempcurrentstatus = (String) temphash.get("currentstatus");
            String tempprovstatus = (String) temphash.get("provstatus");
            String productname =(String) temphash.get("productkey");
            String path = productname + "/";

            out.println("<tr>");
            out.println("<td " + style + ">&nbsp;" + srno + "</td>");
            out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String)temphash.get("icicitransid")) + "</a></td>");
            out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("transid")) + "</td>");
            out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("authid")) + "</td>");
            out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("captureid")) + "</td>");
            out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("authcode")) + "</td>");
            out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("capturereceiptno")) + "</td>");
            out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("description")) + "</td>");
            String accountid = ESAPI.encoder().encodeForHTML((String) temphash.get("accountid"));
            String currency = null;
            if (accountid != null)
            {
                GatewayAccount account = GatewayAccountService.getGatewayAccount(accountid);
                currency = account.getCurrency();
            }
            out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(currency) + "  " + ESAPI.encoder().encodeForHTML((String) temphash.get("amount")) + "</td>");
            out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("status")) + "</td>");
            String status = (String) temphash.get("status");


            if (status.equals("settled"))
            {
                out.println("<td " + style + ">");
                out.println("&nbsp;&nbsp;<a href=GetChargebackDetails?action=sendrequest&icicitransid=" + ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("icicitransid")) + "&ctoken="+ctoken+" onclick='return confirm(\"Do you really want to send Rerival Request mail for this transaction.\")' >Send Retrival Request Mail</a>");
                out.println("</td>");
                out.println("<td " + style + ">");
                out.println("&nbsp;&nbsp;<a href=RetrivalRequestServlet?action=docreceived&icicitransid=" + ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("icicitransid")) + "&ctoken="+ctoken+" onclick='return confirm(\"Are u Sure.\")' >Documents Received</a>");
                out.println("</td>");
                out.println("<td " + style + ">");
                out.println("&nbsp;&nbsp;<a href=GetChargebackDetails?action=chargeback&icicitransid=" + ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("icicitransid")) + "&ctoken="+ctoken+" onclick='return confirm(\"Do you really want to Chargeback this transaction.\")' >ChargeBack</a>");
                out.println("</td>");
            }
            else
            {
                out.println("<td " + style + ">");
                out.println("&nbsp;&nbsp;");
                out.println("</td>");
                out.println("<td " + style + ">");
                out.println("&nbsp;&nbsp;");
                out.println("</td>");
                out.println("<td " + style + ">");
                out.println("&nbsp;&nbsp;");
                out.println("</td>");
            }
            out.println("</tr>");
        }
    %>
</table>

<%
    int currentblock = 1;
    try
    {
        currentblock = Integer.parseInt(request.getParameter("currentblock"));
    }
    catch (Exception ex)
    {
        currentblock = 1;
    }

%>

<table align=center valign=top><tr>
    <td align=center>
        <jsp:include page="page.jsp" flush="true">
            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
            <jsp:param name="numrows" value="<%=pagerecords%>"/>
            <jsp:param name="pageno" value="<%=pageno%>"/>
            <jsp:param name="str" value="<%=ESAPI.encoder().encodeForURL(str)%>"/>
            <jsp:param name="page" value="ChargebackList"/>
            <jsp:param name="currentblock" value="<%=currentblock%>"/>
            <jsp:param name="orderby" value=""/>
        </jsp:include>
    </td>
</tr>
</table>
<%
        }
        else
        {
            out.println(ShowConfirmation("Sorry", "No records found."));
        }
    }
%>
<form action="/icici/servlet/ChargebackList?ctoken=<%=ctoken%>" method="post" name="F1">
<%  String errormsg1 = ESAPI.encoder().encodeForHTML((String) request.getAttribute("error"));
    if (errormsg1 != null)
    {
        out.println("<table align=\"center\"  ><tr><td><font class=\"text\" ></font>");
        out.println("<br><font face=\"arial\" color=\"red\" size=\"2\">");
        errormsg1 = errormsg1.replace("&lt;BR&gt;","<BR>");
        out.println(errormsg1);
        out.println("</font>");
        out.println("</td></tr></table>");
    }
    String errormsg2 = ESAPI.encoder().encodeForHTML((String) request.getAttribute("error1"));
    if (errormsg2 != null)
    {
        out.println("<table align=\"center\"  ><tr><td><font class=\"text\" ></font>");
        out.println("<br><font face=\"arial\" color=\"red\" size=\"2\">");
        out.println(errormsg2);
        out.println("</font>");
        out.println("</td></tr></table>");
    }
%>
    <table class="search" align=center border="1" cellpadding="2" cellspacing="0" width="60%" bordercolorlight="#000000"
           bordercolordark="#FFFFFF">
        <tr>
            <td width="692" bgcolor="#2379A5" colspan="3"><font color="#FFFFFF" size="1" face="Verdana, Arial">
                <b>Search</b></font></td>
        </tr>
        <tr>
            <td class=search width="20%" align="right">Auth Id(comma seperated)</td>
            <td><input name="authid" maxlength="200"  size="10"></td>
        </tr>
        <tr>
            <td class=search width="20%" align="right">Capture Id(comma seperated)</td>
            <td><input name="captureid" maxlength="200"  size="10"></td>
        </tr>

        <tr>
            <td class=search width="20%" align="right">AuthCode(comma seperated)</td>
            <td><input name="authcode" maxlength="200"  size="10"></td>
        </tr>

        <tr>
            <td class=search width="20%" align="right">Reference Number(comma seperated)</td>
            <td><input name="referenceno" maxlength="200"  size="10"></td>
        </tr>

        <tr>
            <td class=search colspan=2 align=center>
                <input type="submit" value="Search"></td>
        </tr>
    </table>
</form>
</center>
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