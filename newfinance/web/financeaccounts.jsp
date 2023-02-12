<%@ page import="java.util.*,com.directi.pg.Functions,com.directi.pg.Merchants,java.math.*,java.net.*"%>
<html>
<head>
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<title>Merchant Accounts</title>
<script language = "javascript">
function isint(form)
{
	if(isNaN(form.numrows.value))
		return false;
	else
	    return true;
}
</script>
<link rel="stylesheet" type="text/css" href="/merchant/style.css" />
</head>

<body>
<a href="/newfinance/servlet/MerchantList">Home</a>
<p align="center" class="title">Merchant Account</p>

<%
	String merchantid=request.getParameter("merchantid");
	String desc=Functions.checkStringNull(request.getParameter("desc"));
	String str="";
    Merchants merchants = new Merchants();
	String fdate=Functions.checkStringNull(request.getParameter("fdate"));
	String tdate=Functions.checkStringNull(request.getParameter("tdate"));
	String fmonth=Functions.checkStringNull(request.getParameter("fmonth"));
	String tmonth=Functions.checkStringNull(request.getParameter("tmonth"));
	String fyear=Functions.checkStringNull(request.getParameter("fyear"));
	String tyear=Functions.checkStringNull(request.getParameter("tyear"));

	Calendar rightNow = Calendar.getInstance();

	//rightNow.setTime(new Date());

	if(fdate==null)fdate=""+1;
	if(tdate==null)tdate=""+rightNow.get(rightNow.DATE);

	if(fmonth==null)fmonth=""+rightNow.get(rightNow.MONTH);
	if(tmonth==null)tmonth=""+rightNow.get(rightNow.MONTH);

	if(fyear==null)fyear=""+rightNow.get(rightNow.YEAR);
	if(tyear==null)tyear=""+rightNow.get(rightNow.YEAR);

	if(fdate!=null)str=str+"fdate="+fdate;
	if(tdate!=null)str=str+"&tdate="+tdate;
	if(fmonth!=null)str=str+"&fmonth="+fmonth;
	if(tmonth!=null)str=str+"&tmonth="+tmonth;
	if(fyear!=null)str=str+"&fyear="+fyear;
	if(tyear!=null)str=str+"&tyear="+tyear;
	if(desc!=null)str=str+"&desc="+desc;

	str=str+"&merchantid="+merchantid;



	int pageno=Functions.convertStringtoInt(request.getParameter("SPageno"),1);
	int pagerecords=Functions.convertStringtoInt(request.getParameter("SRecords"),30);

	str=str+"&SRecords="+pagerecords;


%>
<form name="form" method = "post" action="FinanceAccounts?<%=request.getQueryString()%>">
  <table border="0" cellpadding="4" cellspacing="0" width="750" align="center">
    <tr>
      <td colspan="8" valign="middle" align="left" bgcolor="#9A1305" class="label">From:
        <select size="1" name="fdate" class="textBoxes">

		<%
			if(fdate!=null)
			out.println(Functions.dayoptions(1,31,fdate));
			else
			out.println(Functions.printoptions(1,31));
		%>
        </select>

		<select size="1" name="fmonth" class="textBoxes">

        <%
			if(fmonth!=null)
			out.println(Functions.newmonthoptions(1,12,fmonth));
			else
			out.println(Functions.printoptions(1,12));
		%>
        </select>

		<select size="1" name="fyear" class="textBoxes">

        <%
			if(fyear!=null)
			out.println(Functions.yearoptions(2000,2008,fyear));
			else
			out.println(Functions.printoptions(2000,2008));
		%>
        </select>
		</td>
		<td colspan="8" valign="middle" align="center" bgcolor="#9A1305" class="label">
		To:
        <select size="1" name="tdate" class="textBoxes">

		<%
			if(tdate!=null)
			out.println(Functions.dayoptions(1,31,tdate));
			else
			out.println(Functions.printoptions(1,31));
		%>
        </select>

		<select size="1" name="tmonth" class="textBoxes">

        <%
			if(tmonth!=null)
			out.println(Functions.newmonthoptions(1,12,tmonth));
			else
			out.println(Functions.printoptions(1,12));
		%>
        </select>

		<select size="1" name="tyear" class="textBoxes">

        <%
			if(tyear!=null)
			out.println(Functions.yearoptions(2000,2008,tyear));
			else
			out.println(Functions.printoptions(2000,2008));
		%>
        </select>
		</td>
		<td colspan="8" valign="middle" align="center" bgcolor="#9A1305" class="label">
		Description:
	  <input type="text" name="desc" class="textBoxes" size="13">
	  </td>
      <td colspan="8" valign="middle" align="center" bgcolor="#9A1305" class="label">
	  Rows / Page:
	  <input type="text" value="<%=pagerecords%>" name="SRecords" size="2" class="textBoxes">
	  <input type="submit" value="Submit" name="B1" class="button">
	  </td>
    </tr>
	</table>
	<br>
	<table border="1" cellpadding="5" cellspacing="0" width="750" bordercolor="#ffffff" align="center">
    <tr class="merchant">
		<td colspan="6" align="left">
		Closing Balance: INR <%=request.getAttribute("balance")%>
		</td>
	</tr>
	<tr>
	  <td width="14%" class="th0">Date</td>
      <td width="15%" class="th1">Transaction ID</td>
     	 <td width="41%" class="th0">Description</td>
      <td width="10%" class="th1">Debit</td>
      <td width="10%" class="th0">Credit</td>
      <td width="10%" class="th1">Balance</td>
    </tr>
<%
	BigDecimal balance=new BigDecimal((String)request.getAttribute("cfbalance"));
	BigDecimal debitAmt=new BigDecimal("0");
	BigDecimal creditAmt=new BigDecimal("0");

%>
	<tr class="tr0">
	  <td align="center"><%=Functions.convertDtstamptoDate((String)request.getAttribute("fdtstamp"))%></td>
      <td align="center">&nbsp;</td>
      <td align="left">Balance Carry Forwarded</td>
      <td align="right">
	  <%
		if(balance.signum()==-1)
		{
			debitAmt=debitAmt.add(balance);
			debitAmt=debitAmt.abs();
			out.println(debitAmt);
		}
		else
		{
			out.println("&nbsp;");
		}
	  %>
	  </td>
      <td align="right">
	  <%
		if(balance.signum()!=-1)
		{
			out.println(balance);
			creditAmt=creditAmt.add(balance);
		}
		else
		{
			out.println("&nbsp;");
		}
	  %>
	  </td>

	  <%
		if(desc==null)
		{
			if(balance.signum()==-1)
			{
				out.println("<td align=\"right\" class=\"negbal\">"+balance+"</td>");
			}
		  else
			{
				out.println("<td align=\"right\">"+balance+"</td>");
			}
		}
		else
		{
	  %>
	  <td align="right">&nbsp;</td>
	  <%
		}
	  %>
    </tr>
	<%
	Hashtable hash=(Hashtable)request.getAttribute("Accountsdetails");
	Hashtable temphash=null;
	//out.println(hash);

	int records=0;
	int totalrecords=0;
	int currentblock=1;

	try
	{
		records=Integer.parseInt((String)hash.get("records"));
		totalrecords=Integer.parseInt((String)hash.get("totalrecords"));
		currentblock=Integer.parseInt(request.getParameter("currentblock"));
	}
	catch(Exception ex)
	{
	}

	String style="class=td0";
	String ext="light";

if(records>0)
{

	for(int pos=1;pos<=records;pos++)
	{
		String id=Integer.toString(pos);

		//int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

		style="class=\"tr"+pos%2+"\"";

		temphash=(Hashtable)hash.get(id);

		String date=Functions.convertDtstamptoDate((String)temphash.get("dtstamp"));
		String description=(String)temphash.get("description");

		String debit="";
		String credit="";

		String toid=(String)temphash.get("toid");
		String totype = (String)temphash.get("totype");
		String fromid=(String)temphash.get("fromid");
		String fromtype = (String)temphash.get("fromtype");

		BigDecimal amount = new BigDecimal((String)temphash.get("amount"));

		if (toid.equals(merchantid))
		{
			description="TRF from " + merchants.getCompany(fromid) + " for " + description;

			credit=amount.toString();
			creditAmt=creditAmt.add(amount);
			balance = balance.add(amount);
		}
		if(fromid.equals(merchantid))
		{
			if (totype.equals("withdrawal"))
				description="Withdrawal (" + description + ")";
			else
				description="TRF to " + merchants.getCompany(toid) + " for " + description;

			debit=amount.toString();
			debitAmt=debitAmt.add(amount);
			balance = balance.subtract(amount);
		}
			//out.println("check this:" + toid+":"+ totype +":" + fromid + ":" + fromtype);
			out.println("<tr "+ style +">");
				  out.println("<td >"+ date +"</td>");
				  out.println("<td >"+ temphash.get("transid") +"</td>");
				  out.println("<td align=\"left\">"+ description +"</td>");
				  out.println("<td align=\"right\">"+ debit +"</td>");
				  out.println("<td align=\"right\">"+ credit +"</td>");
				  if (balance.signum()==-1)
		  			  out.println("<td align=\"right\" class=\"negbal\">"+ balance +"</td>");
				 else
				 	  out.println("<td align=\"right\" >"+ balance +"</td>");
			out.println("</tr>");

	}
%>
	<tr class="total" >
	  <td align="center">Total</td>
      <td align="center">&nbsp;</td>
      <td align="left">&nbsp;</td>
      <td align="right">
	  <%=debitAmt%>
	  </td>
      <td align="right">
		<%=creditAmt%>
	  </td>
	  <%
		if(desc==null)
		{
			if(balance.signum()==-1)
			{
				out.println("<td align=\"right\" class=\"negbal\">"+balance+"</td>");
			}
		  else
			{
				out.println("<td align=\"right\">"+balance+"</td>");
			}
		}
		else
		{
	  %>
	  <td align="right">&nbsp;</td>
	  <%
		}
	  %>
    </tr>

	</tr><tr>
	<td colspan=3 align="left" class="textb">Total Records : <%=totalrecords%></td>
	<td colspan=3 align="right" class="textb">Page No : <%=pageno%>  </td></tr>
  </table>
</form>

<%//=request.getQueryString()%>
<table align=center valign=top><tr>
<td align=center>
<jsp:include page="page.jsp" flush="true" >
	<jsp:param name="numrecords" value="<%=totalrecords%>" />
 	<jsp:param name="numrows" value="<%=pagerecords%>"/>
	<jsp:param name="pageno" value="<%=pageno%>"/>
	<jsp:param name="str" value="<%=str%>"/>
	<jsp:param name="page" value="FinanceAccounts"/>
	<jsp:param name="currentblock" value="<%=currentblock%>"/>
	<jsp:param name="orderby" value=""/>
</jsp:include>
</td>
</table>
<%
}
else
{
	out.println(Functions.ShowConfirmation("Sorry","No records found.<br><br>Date :<br>From "+fdate+ "/"+ (Integer.parseInt(fmonth) +1)+ "/"+fyear+"<br>To "+tdate+ "/" + (Integer.parseInt(tmonth) +1) + "/"+tyear));
}
%>
</body>
</html>