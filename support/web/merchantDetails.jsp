<%@ page import="java.util.*,com.directi.pg.Functions,com.directi.pg.Merchants,com.directi.pg.TransactionEntry,java.math.*,java.net.*"%>
<%@ page import="org.owasp.esapi.ESAPI" %>
<style>
.td0{
	color:#7D0000;
	background:#F1EDE0;
	text-align:left;
	font-family:verdana,arial;
	font-size:12px;
	FONT-WEIGHT: bold;
}
.td1{
	color:#7D0000;
	background:#E4D6C9;
	text-align:left;
	font-family:verdana,arial;
	font-size: 12px;
	FONT-WEIGHT: bold
}
</style>
<%  String memberid=nullToStr(request.getParameter("memberid"));

    String fdate=null,tdate=null,fmonth=null,tmonth=null,fyear=null,tyear=null;
	Calendar rightNow = Calendar.getInstance();

	fdate=""+1;
	tdate=""+rightNow.get(rightNow.DATE);
	fmonth=""+rightNow.get(rightNow.MONTH);
	tmonth=""+rightNow.get(rightNow.MONTH);
	fyear="2000";//+rightNow.get(rightNow.YEAR);
	tyear=""+rightNow.get(rightNow.YEAR);
%>
<html>
<head>
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<title>Merchants</title>
<link rel="stylesheet" type="text/css" href="/merchant/style.css" />
</head>
<body>
<p align="center" class="title">Merchant's Details</p>
<%try{
 %>
<form name="form" method = "post" action="MerchantDetails">
  <table border="0" cellpadding="4" cellspacing="0" align="center">
	 <tr class="label">
      <td valign="middle" bgcolor="#6B7A8A" colspan="2"><nobr>
      <input type=checkbox name="ignoredates" value="yes">Ignore Dates&nbsp;&nbsp;
	  From Date :<select size="1" name="fdate" class="textBoxes">
<%      if(fdate!=null)
		    out.println(Functions.dayoptions(1,31,fdate));
        else
		    out.println(Functions.printoptions(1,31));
%>
        </select>
		<select size="1" name="fmonth" class="textBoxes">
<%      if(fmonth!=null)
            out.println(Functions.newmonthoptions(1,12,fmonth));
        else
            out.println(Functions.printoptions(1,12));
%>
        </select>
		<select size="1" name="fyear" class="textBoxes">
<%      if(fyear!=null)
            out.println(Functions.yearoptions(2000,2008,fyear));
        else
            out.println(Functions.printoptions(2000,2008));
%>
        </select>&nbsp;
		To Date : 
        <select size="1" name="tdate" class="textBoxes">
<%      if(tdate!=null)
            out.println(Functions.dayoptions(1,31,tdate));
        else
            out.println(Functions.printoptions(1,31));
%>
        </select>
		<select size="1" name="tmonth" class="textBoxes">
<%      if(tmonth!=null)
            out.println(Functions.newmonthoptions(1,12,tmonth));
        else
            out.println(Functions.printoptions(1,12));
%>
        </select>
		<select size="1" name="tyear" class="textBoxes">
<%      if(tyear!=null)
            out.println(Functions.yearoptions(2000,2008,tyear));
        else
            out.println(Functions.printoptions(2000,2008));
%>
        </select>
		</td>
		<td valign="middle" bgcolor="#6B7A8A" class="label"><nobr>Merchant ID:
        <input type="text" value="" maxlength="10"  name="memberid" size="5" class="textBoxes">
		</td>
    </tr>
    <tr class="label">
        <td valign="middle" bgcolor="#6B7A8A" class="label"><nobr>Company Name:
          <input type=text name="company_name" maxlength="50"  value="" class="textBoxes" size="20">
        </td>
        <td valign="middle" bgcolor="#6B7A8A" class="label"><nobr>Site Name:
            <input type=text name="sitename" value="" maxlength="50"  class="textBoxes" size="20">
        </td>
        <td valign="middle" bgcolor="#6B7A8A" class="label"><nobr>
          <input type=checkbox name="perfectmatch" value="yes">Show Perfect Match
        </td>
    </tr>
    <tr class="label">
        <td valign="middle" bgcolor="#6B7A8A" class="label"><nobr>Status:
            <select size="1" name="activation" class="textBoxes">
                <option value="" >All</option>
                <option value="N">Inactive</option>
                <option value="Y">Active</option>
                <option value="T">Test</option>
            </select>
	    </td>
        <td valign="middle" bgcolor="#6B7A8A" class="label"><nobr>Transaction Status:
            <select size="1" name="icici" class="textBoxes">
                <option value="" >All</option>
                <option value="N">Inactive</option>
                <option value="Y">Active</option>
            </select>
	    </td>
        <td valign="middle" bgcolor="#6B7A8A" class="label">&nbsp;
        </td>
    </tr>
    <tr class="label" >
        <td valign="middle" bgcolor="#6B7A8A" class="label">Charge Percentage:
            <input type=text name="chargeper" maxlength="10"  value="" class="textBoxes" size="4">
        </td>
        <td valign="middle" bgcolor="#6B7A8A" class="label">Reserves:
          <input type=text name="reserves" maxlength="50"  value="" class="textBoxes" size="10" >
        </td>
        <td valign="middle" align="center" bgcolor="#6B7A8A" class="label">
	 	  <input type="submit" value="Submit" class="button">
	  </td>
    </tr>
	</table>
</form>
	<br>
<table border="1" cellpadding="5" cellspacing="0" bordercolor="#ffffff" align="center">
<%  Hashtable hash=(Hashtable)request.getAttribute("merchantdetails");
	Hashtable innerhash=null;
	//out.println(hash);
 if(hash!=null){
	String style="class=tr0";
    String value="";
    innerhash=(Hashtable)hash.get(1+"");
    int pos=0;
    value=(String)innerhash.get("memberid");
%>
    <tr <%=style%>>
          <td class="td1" colspan="2"><U>Merchant Details:</U></td>
    </tr>
	<tr <%=style%>>
	  <td class="td0">Member ID: </td>
      <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
<%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("company_name"));
%>
    <tr <%=style%>>
        <td class="td0">Company Name: </td>
        <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
<%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("activation"));
%>
    <tr <%=style%>>
        <td class="td0">Merchant Activation Status: </td>
        <td class="td1"><%=getStatus(ESAPI.encoder().encodeForHTML(value))%></td>
    </tr>
<%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("icici"));
%>
    <tr <%=style%>>
        <td class="td0">Transaction Activation Status: </td>
        <td class="td1"><%=getStatus(ESAPI.encoder().encodeForHTML(value))%></td>
    </tr>
<%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("contact_persons"));
%>
    <tr <%=style%>>
        <td class="td0">Contact person: </td>
        <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
<%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("contact_emails"));
%>
    <tr <%=style%>>
        <td class="td0">Merchant's Email: </td>
        <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
<%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("notifyemail"));
%>
    <tr <%=style%>>
        <td class="td0">Notify Email: </td>
        <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
<%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("sitename"));
%>
    <tr <%=style%>>
        <td class="td0">Site URL: </td>
        <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
<%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("brandname"));
%>
    <tr <%=style%>>
        <td class="td0">Brand Name: </td>
        <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
<%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("telno"));
%>
    <tr <%=style%>>
        <td class="td0">Telephone Number: </td>
        <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
<%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("faxno"));
%>
    <tr <%=style%>>
        <td class="td0">Fax Number: </td>
        <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
<%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("address"));
%>
    <tr <%=style%>>
        <td class="td0">Address: </td>
        <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
<%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("city"));
%>
    <tr <%=style%>>
        <td class="td0">City: </td>
        <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
<%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("state"));
%>
    <tr <%=style%>>
        <td class="td0">State: </td>
        <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
<%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("zip"));
%>
    <tr <%=style%>>
        <td class="td0">Zip: </td>
        <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
<%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("country"));
%>
    <tr <%=style%>>
        <td class="td0">Country: </td>
        <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
<%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("actDate"));
%>
    <tr <%=style%>>
        <td class="td0">Activation Date: </td>
        <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
<%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)innerhash.get("modDate"));
%>
    <tr <%=style%>>
        <td class="td0">Last Modified Date: </td>
        <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
<%   pos++;
     style="class=\"tr"+pos%2+"\"";
     value=nullToStr((String)hash.get("balance"));
%>
    <tr <%=style%>>
        <td class="td0">Current Balance: </td>
        <td class="td1"><font color="red"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
<%  }else{
	    out.println(Functions.ShowConfirmation("Sorry","No data found for Member Id :"+ESAPI.encoder().encodeForHTML(memberid)));
    }
  }catch(NumberFormatException nex){
    System.out.print("inside error"+nex);
  }
%>
</table>
<p>&nbsp;&nbsp; </p>
</body>
<%!
    public static String nullToStr(String str)
    {
        if(str == null)
            return "";
        return str;
    }
    public static String getStatus(String str)
    {
        if(str.equals("Y"))
            return "Active";
        else if(str.equals("N"))
            return "Inactive";
        else if(str.equals("T"))
            return "Test";
        
        return str;
    }    
%>
</html>
