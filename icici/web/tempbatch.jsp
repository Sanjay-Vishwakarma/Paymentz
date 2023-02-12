<%@ page import="java.util.*,com.directi.pg.*"%>
<%@ include file="functions.jsp"%>	
<html>
<head>
<link rel="stylesheet" type="text/css" href="/icici/styyle.css" />
</head>
<body>

<br><br><br>
<%

	Hashtable hash=(Hashtable)request.getAttribute("batchdetails");
	Hashtable temphash=null;
	//int pageno=1;
	//int records=30;
	//out.println(hash);
	int hashsize=hash.size();
	String currentblock=request.getParameter("currentblock");
	if(currentblock==null)
	currentblock="1";

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
<form action="/icici/servlet/TempConfirmBatch" method=post>
<font class="info">Total Records :<%=totalrecords%></font>
<table align=center>
<tr>
	<td valign="middle" align="center" bgcolor="#008BBA">&nbsp;</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Sr no</td>
	<td valign="middle" align="center" bgcolor="#008BBA">ICICI Transid</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Authid</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Captureid</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Transid</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Description</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Amount</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Batch</td>
	<td valign="middle" align="center" bgcolor="#008BBA">TimeStamp</td>
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
		String podbatch=(String)temphash.get("podbatch");
		
			out.println("<tr>");
			out.println("<td "+style+"><input type=checkbox name=listicicitransid value="+temphash.get("icicitransid")+"></td>");
			out.println("<td "+style+">&nbsp;"+srno+ "</td>");
			out.println("<td "+style+">&nbsp;" +temphash.get("icicitransid")+"</a></td>");
			out.println("<td "+style+">&nbsp;" +temphash.get("authid")+"</a></td>");
			out.println("<td "+style+">&nbsp;" +temphash.get("captureid")+"</a></td>");
			out.println("<td "+style+">&nbsp;"+(String)temphash.get("transid")+"</td>");
			out.println("<td "+style+">&nbsp;"+(String)temphash.get("description")+"</td>");
			out.println("<td "+style+">&nbsp;"+(String)temphash.get("amount")+"</td>");
			out.println("<td "+style+">&nbsp;"+ (String)temphash.get("podbatch")+ "</td>");
			out.println("<td "+style+">&nbsp;"+ (String)temphash.get("day") + " - " + (String)temphash.get("month")+ " - " + (String)temphash.get("year")+"</td>");
			//out.println("<td "+style+">&nbsp;<a href=/icici/servlet/TempConfirmBatch?icicitransid=" + temphash.get("icicitransid") +">Settle</a></td>");
			out.println("</tr>");
	}
%>
</table>
<center><INPUT TYPE="submit" value="Settle All Checked"></center>
</form>


<table align=center valign=top><tr>
<td align=center>
<jsp:include page="page.jsp" flush="true" >
	<jsp:param name="numrecords" value="<%=totalrecords%>" />
 	<jsp:param name="numrows" value="<%=pagerecords%>"/>
	<jsp:param name="pageno" value="<%=pageno%>"/>
	<jsp:param name="str" value=""/>
	<jsp:param name="page" value="TempBatch"/>
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
	out.println(ShowConfirmation("Sorry","No records found."));
}
%>


</body>
</html>