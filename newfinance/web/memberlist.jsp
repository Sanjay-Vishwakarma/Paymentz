<%@ page import="java.util.*,java.math.*,java.net.*"%>
<%@ include file="functions.jsp"%>	
<html>

<head>
<link rel="stylesheet" type="text/css" href="/icici/styyle.css" />
</head>
<html>
	<title>Members</title>
	<body>
<h2> Merchant List</h2> <center>
<br><br>
<form action="MerchantList" method="get" name="F1" >
	<table class="search" align=center border="1" cellpadding="2" cellspacing="0" width="60%" bordercolorlight="#000000" bordercolordark="#FFFFFF">
		<tr>
		<td width="692" bgcolor="#2379A5" colspan="3"><font color="#FFFFFF" size="1" face="Verdana, Arial"><b>Search</b></font></td>
		</tr>
		<tr>
			<td class=search width="20%" align="right">Member Id</td>
			<td><input name="memberid" size="10"></td>
		</tr>
		<tr>
			<td class=search width="20%" align="right">Company Name</td>
			<td><input name="company" size="10"></td>
		</tr>
		<tr>
			<td class=search colspan=2 align=center>
			<input type="submit" value="Search"></td>
		</tr>
	</table>
</form>

<%
	Hashtable hash=(Hashtable)request.getAttribute("memberdetails");
//	Hashtable uhash=(Hashtable)request.getAttribute("uhash");
//	Hashtable fhash=(Hashtable)request.getAttribute("fhash");

	Hashtable temphash=null;
	//int pageno=1;
	//int records=30;
	//out.println(hash);
	int hashsize=hash.size();

	int pageno=convertStringtoInt(request.getParameter("SPageno"),1);
	int pagerecords=convertStringtoInt(request.getParameter("SRecords"),100);
	
	int records=0;
	int totalrecords=0;
	String str=null;

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
	//str="year="+URLEncoder.encode((String)hash.get("year"))+"&month="+URLEncoder.encode((String)hash.get("month"));
%>
<font class="info">Total records <%=totalrecords%></font>
<form action="/icici/servlet/SetReserves" method=post>
<table align=center>
<tr>
	<td valign="middle" align="center" bgcolor="#008BBA">Sr.</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Merchant Id</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Company Name</td>
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

			out.println("<tr><td "+style+" >&nbsp;"+srno+ "</td>");
			out.println("<td "+style+" >&nbsp;<font color=red>"+temphash.get("memberid")+"</font></td>");
			out.println("<td "+style+" >&nbsp;<a href=\"/newfinance/servlet/FinanceAccounts?merchantid="+ temphash.get("memberid") +"\">"+(String)temphash.get("company_name")+"</a></td>");
			out.println("</tr>");
	}
%>

</table>

</form>

<%
	int currentblock=1;
	try
	{
		currentblock=Integer.parseInt(request.getParameter("currentblock"));
	}
	catch(Exception ex)
	{
		currentblock=1;
	}

%>

<table align=center valign=top><tr>
<td align=center>
<jsp:include page="page.jsp" flush="true" >
	<jsp:param name="numrecords" value="<%=totalrecords%>" />
 	<jsp:param name="numrows" value="<%=pagerecords%>"/>
	<jsp:param name="pageno" value="<%=pageno%>"/>
	<jsp:param name="str" value="<%=str%>"/>
	<jsp:param name="page" value="MerchantList"/>
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
	out.println(ShowConfirmation("Sorry","No records found.<br><br>Either the Member is Not activated"));
}
%>
</center>
</body>
</html>