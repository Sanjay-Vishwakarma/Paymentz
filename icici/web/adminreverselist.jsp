<%@ page import="java.util.*,
                 com.directi.pg.Merchants,
                 com.directi.pg.Admin"%>
<%@ include file="functions.jsp"%>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ include file="index.jsp"%>
<html>
<head>

</head>
<body>
<h1 align="center">Reverse List</h1>
<br><br><br>
<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    if (Admin.isLoggedIn(session))
    {
    %>

<%
	Hashtable hash=(Hashtable)request.getAttribute("transdetails");
	Hashtable temphash=null;
	//int pageno=1;
	//int records=30;
	//out.println(hash);
	int hashsize=hash.size();

    Hashtable gatewayHash = GatewayTypeService.getGatewayTypes();
    String gateway = Functions.checkStringNull(request.getParameter("gateway"));
    if (gateway == null)
        gateway = "";

	int pageno=convertStringtoInt(request.getParameter("SPageno"),1);
	int pagerecords=convertStringtoInt(request.getParameter("SRecords"),15);

	int records=0;
	int totalrecords=0;

	String currentblock=ESAPI.encoder().encodeForHTML(request.getParameter("currentblock"));
    String errormsg = ESAPI.encoder().encodeForHTML((String) request.getAttribute("error"));
    String errormsg1 = ESAPI.encoder().encodeForHTML((String) request.getAttribute("error1"));
	if(currentblock==null)
	currentblock="1";



	try
	{
		records=Integer.parseInt((String)hash.get("records"));
		totalrecords=Integer.parseInt((String)hash.get("totalrecords"));
	}
	catch(Exception ex)
	{
	}

if(records>0)
{
%>

<font class="info">Total records <%=totalrecords%></font>
<table align=center>
<tr>
	<td valign="middle" align="center" bgcolor="#008BBA">Sr no</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Merchant Txn. No.</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Company Name</td>
	<td valign="middle" align="center" bgcolor="#008BBA">ICICI merchantid</td>
    <td valign="middle" align="center" bgcolor="#008BBA">Captured Amount</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Refund Amount</td>
	<td valign="middle" align="center" bgcolor="#008BBA" colspan=3>
        <table align=center border=1 width="100%">
        <tr>
            <td valign="middle" align="center" bgcolor="#008BBA" colspan=3>Action</td>
        </tr>
        <tr>
            <td valign="middle" align="center" bgcolor="#008BBA">Txn. Ref. No.</td>
            <td valign="middle" align="center" bgcolor="#008BBA">Refund Code</td>
            <td valign="middle" align="center" bgcolor="#008BBA">Refund RRN</td>
        </tr>
    </table>


    </td>
</tr>
<%
	String style="class=td1";
	String ext="light";

	for(int pos=1;pos<=records;pos++)
	{
		String id=Integer.toString(pos);

		int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

			if(pos%2==0)
			{
				style="class=td2";
				ext="dark";
			}
			else
			{
				style="class=td1";
				ext="light";
			}

		temphash=(Hashtable)hash.get(id);

		String tempcurrentstatus=(String)temphash.get("currentstatus");
		String tempprovstatus=(String)temphash.get("provstatus");
		String productname=(String)temphash.get("productkey");
		String path=productname+"/";

			out.println("<tr>");



			out.println("<td "+style+">&nbsp;"+srno+ "</td>");
        if (errormsg1 != null)
    {
        out.println("<table align=\"center\"  ><tr><td><font class=\"text\" ></font>");
        out.println("<br><font face=\"arial\" color=\"red\" size=\"2\">");
        errormsg1 = errormsg1.replace("&lt;BR&gt;","<BR>");
        out.println(errormsg1);
        out.println("<font>");
        out.println("</td></tr></table>");
    }
			out.println("<td "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("icicitransid"))+"</a></td>");
			//out.println("<td "+style+">&nbsp;"+(String)temphash.get("transid")+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("company_name"))+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("icicimerchantid"))+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("captureamount"))+"</td>");
			out.println("<td "+style+">&nbsp;<font color=red>"+ESAPI.encoder().encodeForHTML((String)temphash.get("refundamount"))+"</font></td>");
			//out.println("<td "+style+">&nbsp;<input type=text name=" +(String)temphash.get("icicitransid")+"></td>");
			//out.println("<td "+style+">&nbsp;"+(String)temphash.get("status")+"</td>");
			String status=(String)temphash.get("status");

			out.println("<td "+style+">");

			if(status.equals("markedforreversal"))
			{
			//out.println("&nbsp;<a href=AdminDoReverseTransaction?icicitransid="+ temphash.get("icicitransid") +" onclick='return confirm(\"Do you really want to reverse this transaction.\")' >Reverse</a>");
			out.println("<form action=AdminDoReverseTransaction?ctoken="+ctoken+"><input name=refundid size=5><input name=refundcode size=5><input name=refundreceiptno size=5><input type=hidden  name=icicitransid value="+ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("icicitransid")) +"><input type=hidden  name=accountid value="+ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("accountid"))+"<input type=hidden  name=ctoken value="+ctoken+" ><input type=submit value=Refund></form>");
			}
			else
			out.println("&nbsp;");

			out.println("</td>");
			out.println("</tr>");
	}
%>
</table>
</form>

<table align=center valign=top><tr>
<td align=center>
<jsp:include page="page.jsp" flush="true" >
	<jsp:param name="numrecords" value="<%=totalrecords%>" />
 	<jsp:param name="numrows" value="<%=pagerecords%>"/>
	<jsp:param name="pageno" value="<%=pageno%>"/>
	<jsp:param name="str" value="<%=ESAPI.encoder().encodeForURL(request.getQueryString())%>"/>
	<jsp:param name="page" value="AdminReverseList"/>
	<jsp:param name="currentblock" value="1"/>
	<jsp:param name="orderby" value=""/>
</jsp:include>
</td>
</tr>
</table>
<%
}
else
{
	out.println(ShowConfirmation("Sorry","No records found."));
}


%>

<form action="/icici/servlet/AdminReverseList?ctoken=<%=ctoken%>" method="post" name="F1" >
<%
    if (errormsg != null)
    {
        out.println("<table align=\"center\"  ><tr><td><font class=\"text\" ></font>");
        out.println("<br><font align=\"center\" face=\"arial\" color=\"red\" size=\"2\">");
        out.println(errormsg);
        out.println("</font>");
        out.println("</td></tr></table>");
    }
%>
	<table class="search" align=center border="1" cellpadding="2" cellspacing="0" width="60%" bordercolorlight="#000000" bordercolordark="#FFFFFF">
		<tr>
		<td width="692" bgcolor="#2379A5" colspan="3"><font color="#FFFFFF" size="1" face="Verdana, Arial"><b>Search</b></font></td>
		</tr>
		<tr>
			<td class=search width="20%" align="right">Tracking Id</td>
			<td><input name="STrakingid" maxlength="10"  size="10"></td>
            <td>&nbsp;</td>
		</tr>
		<tr>
			<td class=search width="20%" align="right">Description</td>
			<td><input name="SDescription" maxlength="100"  size="10"></td>
            <td valign="middle" align="left" >
                Gateway:
                <select size="1" name="gateway" class="textBoxes">
                    <option value="">All</option>
                    <%
                        Enumeration enu2 = gatewayHash.keys();
                        String selected2 = "";
                        String key2 = "";
                        String value2 = "";


                        while (enu2.hasMoreElements())
                        {
                            key2 = (String) enu2.nextElement();
                            value2 = (String) gatewayHash.get(key2);

                            if (key2.equals(gateway))
                                selected2 = "selected";
                            else
                                selected2 = "";

                    %>
                    <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key2)%>" <%=selected2%>><%=ESAPI.encoder().encodeForHTML(value2)%></option>
                    <%
                        }
                    %>
                </select>
            </td>
		</tr>

		<tr>
            </tr>
		<tr>
			<td class=search colspan=2 align=center>
			<input type="submit" value="Search"></td>
		</tr>
	</table>
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