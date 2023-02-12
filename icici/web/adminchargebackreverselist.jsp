<%@ page import="java.util.*,
                 com.directi.pg.Merchants,
                 com.directi.pg.Admin,
                 org.owasp.esapi.User"%>
<%@ include file="functions.jsp"%>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp"%>
<html>
<head>

<script language="javascript">

function confirmation()
{

	if(confirm("Do u really want to reverse this chargeback"))
	{
		return true;
	}
	return false;
}
</script>
</head>
<body>
<h1 align="center">Reverse Chargeback</h1>
<br><br><br>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    if (Admin.isLoggedIn(session))
    {
    %>
<%

        String cbreversemessage = (String)request.getAttribute("cbreversemessage");
        if (cbreversemessage != null && !cbreversemessage.equals("null"))
        {

        out.println("<table align=\"center\"  ><tr><td valign=\"middle\"><font class=\"text\" >");

        out.println(cbreversemessage);

        out.println("</font></td></tr></table>");
        }


       Hashtable hash=(Hashtable)request.getAttribute("transdetails");
       /* if(hash==null)
        {

             RequestDispatcher rd = request.getRequestDispatcher("/icici/servlet/AdminChargebackReverseList?ctoken="+ctoken);
             rd.forward(request, response);
             return;
        }*/
	Hashtable temphash=null;
	//int pageno=1;
	//int records=30;
	//out.println(hash);
	int hashsize=hash.size();

	int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
	int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

	int records=0;
	int totalrecords=0;

	String currentblock=request.getParameter("currentblock");

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

<table align=center>
<input type="hidden" value="<%=ctoken%>" name="ctoken">
<tr>
	<td valign="middle" align="center" bgcolor="#008BBA">Sr no</td>
	<td valign="middle" align="center" bgcolor="#008BBA">ICICI Transid</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Captureid(FT)</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Company Name</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Description</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Amount</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Status</td>
	<td valign="middle" align="center" bgcolor="#008BBA" colspan=2>Action</td>
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

/*		String tempcurrentstatus=(String)temphash.get("currentstatus");
		String tempprovstatus=(String)temphash.get("provstatus");
		String productname=(String)temphash.get("productkey");
		String path=productname+"/";
*/
			out.println("<tr>");
			out.println("<td "+style+">&nbsp;"+srno+ "</td>");
			out.println("<td "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("icicitransid"))+"</a></td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("captureid"))+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("company_name"))+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("description"))+"</td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("amount"))+"</td>");
			//out.println("<td "+style+">&nbsp;<input type=text name=" +(String)temphash.get("icicitransid")+"></td>");
			out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("status"))+"</td>");
			String status=(String)temphash.get("status");

			out.println("<td "+style+">");

			if(status.equals("chargeback"))
			{
			//out.println("&nbsp;<a href=AdminDoReverseTransaction?icicitransid="+ temphash.get("icicitransid") +" onclick='return confirm(\"Do you really want to reverse this transaction.\")' >Reverse</a>");
			out.println("<form action=AdminDoReverseChargeback?ctoken="+ctoken+"><input type =hidden  name=icicitransid value="+ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("icicitransid")) +"><input type =hidden  name=ctoken value="+ctoken +"><input type=submit value=submit onclick='return confirmation();' ></form>");
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
	<jsp:param name="page" value="AdminChargebackReverseList"/>
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
<form action="/icici/servlet/AdminChargebackReverseList?ctoken=<%=ctoken%>" method="post" name="F1" >
 <%

        String errormsg = (String)request.getAttribute("cbmessage");
        if (errormsg == null)
        errormsg = "";

        out.println("<table align=\"center\"  ><tr><td valign=\"middle\"><font class=\"text\" >");

        out.println(errormsg);

        out.println("</font></td></tr></table>");



  %>
	<table class="search" align=center border="1" cellpadding="2" cellspacing="0" width="60%" bordercolorlight="#000000" bordercolordark="#FFFFFF">
		<tr>
		<td width="692" bgcolor="#2379A5" colspan="3"><font color="#FFFFFF" size="1" face="Verdana, Arial"><b>Search</b></font></td>
		</tr>
		<tr>
			<td class=search width="20%" align="right">Tracking Id</td>
			<td><input name="STrakingid" maxlength="20"  size="10"></td>
		</tr>
        <tr>
			<td class=search width="20%" align="right">FT</td>
			<td><input name="SCaptureid" maxlength="30"  size="10"></td>
		</tr>
		<tr>
			<td class=search width="20%" align="right">Description</td>
			<td><input name="SDescription" maxlength="100"  size="10"></td>
		</tr>
		<!--
		<tr>
			<td class=search width="20%" align="right">Credicard Number</td>
			<td><input name="SCc" size="10"></td>
		</tr>
		-->
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