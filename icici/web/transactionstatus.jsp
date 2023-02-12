<%@ page import="java.util.*"%>
<%@ include file="functions.jsp"%>	
<html>
<head>
<link rel="stylesheet" type="text/css" href="/icici/styyle.css" />
</head>
<body>
<%//@ include file="top.jsp"%>

<br><br><br>

<marquee> Reversal / Charge Back</marquee>

<%
	Hashtable hash=(Hashtable)request.getAttribute("transdetails");
	Hashtable temphash=null;
	//int pageno=1;
	//int records=30;
	//out.println(hash);
	int hashsize=hash.size();

	int pageno=convertStringtoInt(request.getParameter("SPageno"),1);
	int pagerecords=convertStringtoInt(request.getParameter("SRecords"),15);
	
	int records=0;
	int totalrecords=0;

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
<form action="/icici/servlet/TransactionStatus" method=post>
<font class="info">Total records <%=totalrecords%></font>
<table align=center>
<tr>
	<td valign="middle" align="center" bgcolor="#008BBA">Sr no</td>
	<td valign="middle" align="center" bgcolor="#008BBA">ICICI Transid</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Transid</td>
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
		
		String tempcurrentstatus=(String)temphash.get("currentstatus");
		String tempprovstatus=(String)temphash.get("provstatus");
		String productname=(String)temphash.get("productkey");
		String path=productname+"/";
		
			out.println("<tr>");
			out.println("<td "+style+">&nbsp;"+srno+ "</td>");
			out.println("<td "+style+">&nbsp;" +temphash.get("icicitransid")+"</a></td>");
			out.println("<td "+style+">&nbsp;"+(String)temphash.get("transid")+"</td>");
			out.println("<td "+style+">&nbsp;"+(String)temphash.get("company_name")+"</td>");
			out.println("<td "+style+">&nbsp;"+(String)temphash.get("description")+"</td>");
			out.println("<td "+style+">&nbsp;"+(String)temphash.get("amount")+"</td>");
			//out.println("<td "+style+">&nbsp;<input type=text name=" +(String)temphash.get("icicitransid")+"></td>");
			out.println("<td "+style+">&nbsp;"+(String)temphash.get("status")+"</td>");
			String status=(String)temphash.get("status");

			out.println("<td "+style+">");

			if(status.equals("settled") || status.equals("podsent") || status.equals("capturesuccess"))
			out.println("&nbsp;<a href=DoReverse>Reverse</a>");
			else
			out.println("&nbsp;");

			out.println("</td>");
			out.println("<td "+style+">");
			

			if(status.equals("settled") || status.equals("podsent") || status.equals("capturesuccess"))
			out.println("&nbsp;<a href=DoChargeBack>Charge back</a>");
			else
			out.println("&nbsp;");

			out.println("</td>");
			out.println("</tr>");
	}
%>
<tr>
<td valign="middle" align="center" bgcolor="#008BBA" colspan=6><input type=submit value="Capture"></td>
</tr>
</table>
</form>

<table align=center valign=top><tr>
<td align=center>
<jsp:include page="page.jsp" flush="true" >
	<jsp:param name="numrecords" value="<%=totalrecords%>" />
 	<jsp:param name="numrows" value="<%=pagerecords%>"/>
	<jsp:param name="pageno" value="<%=pageno%>"/>
	<jsp:param name="str" value=""/>
	<jsp:param name="page" value="TransactionStatus"/>
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
<form action="/icici/servlet/TransactionStatus" method="post" name="F1" >
	<table class="search" align=center border="1" cellpadding="2" cellspacing="0" width="90%" bordercolorlight="#000000" bordercolordark="#FFFFFF">
		<tr>
		<td width="692" bgcolor="#2379A5" colspan="3"><font color="#FFFFFF" size="1" face="Verdana, Arial"><b>Search</b></font></td>
		</tr>
		<tr>
			<td class=search width="20%" align="right">Traking Id</td>
			<td><input name="STrakingid" size="10"></td>
		</tr>
		<tr>
			<td class=search width="20%" align="right">Description</td>
			<td><input name="SDescription" size="10"></td>
		</tr>
		<tr>
			<td class=search width="20%" align="right">Credicard Number</td>
			<td><input name="SCc" size="10"></td>
		</tr>
		<tr>
			<td class=search colspan=2>
			<input type="submit" value="Search"></td>
		</tr>
	</table>
</form>
</body>
</html>