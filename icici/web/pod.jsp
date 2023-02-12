<%@ page import="java.util.*"%>
<%@ include file="functions.jsp"%>	
<html>
<head>
<link rel="stylesheet" type="text/css" href="/icici/styyle.css" />

<script language="javascript">
function cancelTrans(icicitransid)
{
	//alert(icicitransid)
	if(confirm("Do u really want to cancel this transaction.\n\n Tracking : " +icicitransid))
	{
		var e=eval("document.f1.mybutton"+icicitransid)
		e.disabled=true;
		//document.f1.mybutton.disabled=true;
		document.location.href="/icici/servlet/CancelTransaction?icicitransid="+icicitransid;
	}
	
}
</script>

</head>
<body>
<%@ include file="top.jsp"%>
<br><br><br>
<center>
<%
	Hashtable hash=(Hashtable)request.getAttribute("poddetails");
	Hashtable temphash=null;
	//int pageno=1;
	//int records=30;
	//out.println(hash);
	int hashsize=hash.size();

	int pageno=convertStringtoInt(request.getParameter("SPageno"),1);
	int pagerecords=convertStringtoInt(request.getParameter("SRecords"),5);
	
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

String message=(String)request.getAttribute("message");

if(message!=null)
{
	out.println(message);
}
if(records>0)
{
%>
<br>
<form name=f1 action="/icici/servlet/PodSubmit" method=post>
<font class="info">* Please do not put pod as N/A or NA</font>
</center>
<table align=center>
<tr>
	<td valign="middle" align="center" bgcolor="#008BBA">Sr no</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Tracking id</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Description</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Amount</td>
	<td valign="middle" align="center" bgcolor="#008BBA">Pod</td>
	<td valign="middle" align="center" bgcolor="#008BBA">&nbsp;</td>
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
			//out.println("<td "+style+">&nbsp;"+(String)temphash.get("transid")+"</td>");
			out.println("<td "+style+">&nbsp;"+(String)temphash.get("description")+"</td>");
			out.println("<td "+style+">&nbsp;"+(String)temphash.get("amount")+"</td>");
			out.println("<td "+style+">&nbsp;<input type=text name=" +(String)temphash.get("icicitransid")+"></td>");

			out.println("<td "+style+">&nbsp;<input type=button name=mybutton"+ (String)temphash.get("icicitransid") +" value=\"Cancel\" onClick=cancelTrans('" +(String)temphash.get("icicitransid")+"')></td>");
			out.println("</tr>");
	}
%>
<tr>
<td valign="middle" align="center" bgcolor="#008BBA" colspan=7><input type=submit value="Capture"></td>
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
	<jsp:param name="page" value="Pod"/>
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

</body>
</html>