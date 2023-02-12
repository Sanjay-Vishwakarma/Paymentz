<%@ page import="java.util.*,com.directi.pg.Functions,com.directi.pg.Merchants,com.directi.pg.TransactionEntry,java.math.*,java.net.*,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException"%>
<%  String memberid=nullToStr(request.getParameter("memberid"));
    String company_name=nullToStr(request.getParameter("company_name"));
    String sitename=nullToStr(request.getParameter("sitename"));
    String activation=nullToStr(request.getParameter("activation"));
    String icici=nullToStr(request.getParameter("icici"));
    String reserves=nullToStr(request.getParameter("reserves"));
    String chargeper=nullToStr(request.getParameter("chargeper"));
        String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;
    try
    {
            fdate = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"Days",2,true);
            tdate = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"Days",2,true);
            fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
            tmonth = ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
            fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
            tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);
    }
    catch(ValidationException e)
    {

    }
    String ignoredates=Functions.checkStringNull(request.getParameter("ignoredates"));
	Calendar rightNow = Calendar.getInstance();
	String str="";
	//rightNow.setTime(new Date());
	if(fdate==null)fdate=""+1;
	if(tdate==null)tdate=""+rightNow.get(rightNow.DATE);
	if(fmonth==null)fmonth=""+rightNow.get(rightNow.MONTH);
	if(tmonth==null)tmonth=""+rightNow.get(rightNow.MONTH);
	if(fyear==null)fyear="2000";//+rightNow.get(rightNow.YEAR);
	if(tyear==null)tyear=""+rightNow.get(rightNow.YEAR);

	if(fdate!=null)str=str+"&fdate="+fdate;
	if(tdate!=null)str=str+"&tdate="+tdate;
	if(fmonth!=null)str=str+"&fmonth="+fmonth;
	if(tmonth!=null)str=str+"&tmonth="+tmonth;
	if(fyear!=null)str=str+"&fyear="+fyear;
	if(tyear!=null)str=str+"&tyear="+tyear;
	if(memberid!=null)str=str+"&memberid="+memberid;
	if(company_name!=null)str=str+"&company_name="+company_name;
	if(sitename!=null)str=str+"&sitename="+sitename;
	if(activation!=null)str=str+"&activation="+activation;
	if(icici!=null)str=str+"&icici="+icici;
	if(reserves!=null)str=str+"&reserves="+reserves;
	if(chargeper!=null)str=str+"&chargeper="+chargeper;
    if(ignoredates!=null)str=str+"&ignoredates="+ignoredates;
    /*System.out.println(ignoredates);*/


	int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
	int pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),30);
%>
<html>
<head>
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<title>Merchants</title>
<link rel="stylesheet" type="text/css" href="/merchant/style.css" />
</head>
<body>
<p align="center" class="title">Merchants' Details</p>
<%
  try{
%>
<form name="form" method = "post" action="MerchantDetails">
  <table border="0" cellpadding="4" cellspacing="0" align="center">
	 <tr class="label">
      <td valign="middle" bgcolor="#9A1305" colspan="2"><nobr>
      <%
      if(request.getParameter("ignoredates")!=null)
      {
          /*System.out.println("ignore value"+request.getParameter("ignoredates"));*/
      %>
      <input type=checkbox name="ignoredates" value="yes" checked>
      <%
      }
       else
          {
         /* System.out.println("ignore value"+request.getParameter("ignoredates"));*/
          %>
          <input type=checkbox name="ignoredates">
          <%

          }
      %>
Ignore Dates&nbsp;&nbsp;
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
            out.println(Functions.yearoptions(2000,2012,fyear));
        else
            out.println(Functions.printoptions(2000,2012));
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
            out.println(Functions.yearoptions(2000,2012,tyear));
        else
            out.println(Functions.printoptions(2000,2012));
%>
        </select>
		</td>
        <%
      String errormsg = (String) request.getAttribute("error");
      if(errormsg!=null)
      {
          out.println("<br><font face=\"arial\" color=\"red\" size=\"2\">");
            errormsg = errormsg.replace("&lt;BR&gt;","<BR>");
            out.println(errormsg);
            out.println("</font>");
            out.println("</td></tr></table>");
      }
        %>
		<td valign="middle" bgcolor="#9A1305" class="label"><nobr>Merchant ID:
        <input type="text" maxlength="10"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>" name="memberid" size="5" class="textBoxes">
		</td>
    </tr>
    <tr class="label">
        <td valign="middle" bgcolor="#9A1305" class="label"><nobr>Company Name:
          <input type=text name="company_name" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(company_name)%>" class="textBoxes" size="20">
        </td>
        <td valign="middle" bgcolor="#9A1305" class="label"><nobr>Site Name:
            <input type=text name="sitename" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(sitename)%>" class="textBoxes" size="20">
        </td>
        <td valign="middle" bgcolor="#9A1305" class="label"><nobr>
          <input type=checkbox name="perfectmatch" value="yes">Show Perfect Match
        </td>
    </tr>
    <tr class="label">
        <td valign="middle" bgcolor="#9A1305" class="label"><nobr>Status:
            <select size="1" name="activation" class="textBoxes">
                <option value="" <%=activation.equals("")?"selected":""%>>All</option>
                <option value="N" <%=activation.equals("N")?"selected":""%>>Inactive</option>
                <option value="Y" <%=activation.equals("Y")?"selected":""%>>Active</option>
                <option value="T" <%=activation.equals("T")?"selected":""%>>Test</option>
            </select>
	    </td>
        <td valign="middle" bgcolor="#9A1305" class="label"><nobr>Transaction Status:
            <select size="1" name="icici" class="textBoxes">
                <option value="" <%=icici.equals("")?"selected":""%>>All</option>
                <option value="N" <%=icici.equals("N")?"selected":""%>>Inactive</option>
                <option value="Y" <%=icici.equals("Y")?"selected":""%>>Active</option>
            </select>
	    </td>
        <td valign="middle" bgcolor="#9A1305" class="label">&nbsp;
        </td>
    </tr>
    <tr class="label" >
        <td valign="middle" bgcolor="#9A1305" class="label">Charge Percentage:
            <input type=text name="chargeper" maxlength="10"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(chargeper)%>" class="textBoxes" size="4">
        </td>
        <td valign="middle" bgcolor="#9A1305" class="label">Reserves:
          <input type=text name="reserves" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(reserves)%>" class="textBoxes" size="10" >
        </td>
        <td valign="middle" align="center" bgcolor="#9A1305" class="label">
	 	  <input type="submit" value="Submit" class="button">
	  </td>
    </tr>
	</table>
</form>    
<%	Hashtable hash=(Hashtable)request.getAttribute("merchantsdetails");
	Hashtable temphash=null;
	//out.println(hash);
	int records=0;
	int totalrecords=0;
	int currentblock=1;

	try{
		records=Integer.parseInt((String)hash.get("records"));
		totalrecords=Integer.parseInt((String)hash.get("totalrecords"));
		currentblock=Integer.parseInt(request.getParameter("currentblock"));
	}catch(Exception ex){

	}

	String style="class=td0";
    if(records>0){
%>
<table border="1" cellpadding="5" cellspacing="0" bordercolor="#ffffff" align="center">
	<tr>
	  <td width="5%" class="th0">Merchant Id</td>
      <td width="20%" class="th1">Company Name</td>
      <td width="17%" class="th0">Site Name</td>
      <td width="5%" class="th1">Status</td>
      <td width="5%" class="th0">Transaction Status</td>
      <td width="10%" class="th1">Reserves</td>
      <td width="10%" class="th0">Charge %</td>
    </tr>
<%
    for(int pos=1;pos<=records;pos++)
	{
        String id=Integer.toString(pos);
        style="class=\"tr"+pos%2+"\"";
        
        temphash=(Hashtable)hash.get(id);
        String cname=(String)temphash.get("company_name");
        String sname=(String)temphash.get("sitename");
        String mid=(String)temphash.get("memberid");
        String sta=(String)temphash.get("activation");
        String tsta= (String)temphash.get("icici");
        String res = (String)temphash.get("reserves");
        String chper = (String)temphash.get("chargeper");
        
        out.println("<tr "+ style +">");
        out.println("<td ><a href=\"MerchantDetails?memberid="+ESAPI.encoder().encodeForHTMLAttribute(mid)+"\">"+ mid +"</a></td>");
        out.println("<td align=\"left\">"+ cname +"</td>");
        out.println("<td align=\"left\">"+ sname +"</td>");
        out.println("<td align=\"left\">"+ getStatus(sta) +"</td>");
        out.println("<td align=\"left\">"+ getStatus(tsta) +"</td>");
        out.println("<td align=\"right\">"+ res +"</td>");
        out.println("<td align=\"right\">"+ chper +"</td>");
        out.println("</tr>");
    }
%>
	</tr><tr><td colspan=3 align="left" class=textb>Total Records: <%=totalrecords%></td><td colspan=3 align="right" class=textb>Page No <%=pageno%></td></tr>
 </table>

<table align=center valign=top><tr>
<td align=center>
<jsp:include page="page.jsp" flush="true" >
	<jsp:param name="numrecords" value="<%=totalrecords%>" />
 	<jsp:param name="numrows" value="<%=pagerecords%>"/>
	<jsp:param name="pageno" value="<%=pageno%>"/>
	<jsp:param name="str" value="<%=ESAPI.encoder().encodeForURL(str)%>"/>
	<jsp:param name="page" value="MerchantDetails"/>
	<jsp:param name="currentblock" value="<%=currentblock%>"/>
	<jsp:param name="orderby" value=""/>
</jsp:include>
</td>
</table>
<%
    }else{
	    out.println(Functions.ShowConfirmation("Sorry","No records found. Try again with different criteria."));
    }
      
  }catch(NumberFormatException nex){
    System.out.print("inside error"+nex);
  }
%>
  </table>
</form>
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
