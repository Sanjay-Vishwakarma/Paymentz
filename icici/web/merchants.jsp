<%@ page import="java.util.*,com.directi.pg.Functions,com.directi.pg.Merchants,com.directi.pg.TransactionEntry,java.math.*,java.net.*,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException"%>
<%@ page import="com.manager.dao.MerchantDAO" %>
<%@ include file="index.jsp"%>
<%  String memberid=nullToStr(request.getParameter("memberid"));
    String company_name=nullToStr(request.getParameter("company_name"));
    String sitename=nullToStr(request.getParameter("sitename"));
    String activation=nullToStr(request.getParameter("activation"));
    String icici=nullToStr(request.getParameter("icici"));
    String reserves=nullToStr(request.getParameter("reserves"));
    String chargeper=nullToStr(request.getParameter("chargeper"));
    String contact_emails=nullToStr(request.getParameter("contact_emails"));
    String partnerName=nullToStr(request.getParameter("partnerName"));
    String contact_persons=nullToStr(request.getParameter("contact_persons"));
    String login=nullToStr(request.getParameter("login"));
    String mailresult=nullToStr((String) request.getAttribute("mailresult"));

        String fdate=null;
        String tdate=null;
        String fmonth=null;
        String tmonth=null;
        String fyear=null;
        String tyear=null;
    String perfactMatch= "Yes";
           perfactMatch =nullToStr((String)request.getAttribute("perfectmatch"));
    try
    {
            fdate = ESAPI.validator().getValidInput("fdate",(String)request.getAttribute("fdate"),"Days",2,true);
            tdate = ESAPI.validator().getValidInput("tdate",(String)request.getAttribute("tdate"),"Days",2,true);
            fmonth = ESAPI.validator().getValidInput("fmonth",(String)request.getAttribute("fmonth"),"Months",2,true);
            tmonth = ESAPI.validator().getValidInput("tmonth",(String)request.getAttribute("tmonth"),"Months",2,true);
            fyear = ESAPI.validator().getValidInput("fyear",(String)request.getAttribute("fyear"),"Years",4,true);
            tyear = ESAPI.validator().getValidInput("tyear",(String)request.getAttribute("tyear"),"Years",4,true);
    }
    catch(ValidationException e)
    {

    }String str="";
    String perfectMatch =Functions.checkStringNull(request.getParameter("perfectmatch"));
    String ignoredates=Functions.checkStringNull(request.getParameter("ignoredates"));
	Calendar rightNow = Calendar.getInstance();
	String currentyear= ""+rightNow.get(rightNow.YEAR);

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
	if(memberid!=null)str=str+"&memberid="+memberid;
	if(company_name!=null)str=str+"&company_name="+company_name;
	if(sitename!=null)str=str+"&sitename="+sitename;
	if(partnerName!=null)str=str+"&partnerName="+partnerName;
	if(activation!=null)str=str+"&activation="+activation;
	if(icici!=null)str=str+"&icici="+icici;
    if(ignoredates!=null)str=str+"&ignoredates="+ignoredates;
    if(perfactMatch!=null)str=str+"&perfectmatch="+perfactMatch;
    if(contact_emails!=null)str=str+"&contact_emails="+contact_emails;
    if(contact_persons!=null)str=str+"&contact_persons="+contact_persons;
    if(login!=null)str=str+"&login="+login;
    str=str+"&ctoken="+ctoken;

    int pageno =1;
    int pagerecords=30;
    try
    {
        pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno",request.getParameter("SPageno"),"Numbers",3,true), 1);
        pagerecords = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord",request.getParameter("SRecords"),"Numbers",3,true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

    }
    catch(ValidationException e)
    {
        pageno = 1;
        pagerecords = 30;
    }
    str = str + "&SRecords=" + pagerecords;
%>
<html>
<head>
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>

    <script type="text/javascript">
        function getExcelFile()
        {
            if(document.getElementById("containrecord"))
            {
                document.exportform.submit();
            }
        }
    </script>
<title>Merchant Management > Merchant Master</title>
</head>
<body class="bodybackground">

<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
    %>
<%
  try{
%>


<input type="hidden" value="<%=ctoken%>" name="ctoken">
<div class="row">
<div class="col-lg-12">
<div class="panel panel-default">
<div class="panel-heading">
    Merchant Master

    <div style="float: right;">
        <form action="/icici/merchantLogo.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" class="addnewmember" value="Upload Merchant Logo" name="submit">
                <i class="fa fa-sign-in"></i>
                &nbsp;&nbsp;Upload Merchant Logo
            </button>
        </form>
    </div>

    <div style="float: right;">
        <form action="/icici/membersignup.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" class="addnewmember" value="Add New Member" name="submit">
                <i class="fa fa-sign-in"></i>
                &nbsp;&nbsp;Add New Member
            </button>
        </form>
    </div>

    <div style="float: right;">
        <form action="/icici/addMerchantTemplateColors.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" class="addnewmember" value="Add Template Colors" name="submit">
                <i class="fa fa-sign-in"></i> &nbsp;&nbsp;Add Template Colors
            </button>
        </form>
    </div>

    <div style="float: right;">
        <form action="/icici/resetpassword.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" class="addnewmember" value="" name="submit">
                <i class="fa fa-sign-in"></i> &nbsp;&nbsp;Reset Password
            </button>
        </form>
    </div>
</div>

<%
    String errormsg = (String) request.getAttribute("error");
    if(errormsg!=null)
    {
        out.println("<br><font class=\"textb\"><b>");
        errormsg = errormsg.replace("&lt;BR&gt;","<BR>");
        out.println(errormsg);
        out.println("</b></font>");
        out.println("</td></tr></table>");
    }
%>

    <%
        MerchantDAO merchantDAO = new MerchantDAO();
        LinkedHashMap<Integer,String> memberMap = merchantDAO.listAllMember();
    %>
    <form name="form" method = "post" action="MerchantDetails?ctoken=<%=ctoken%>">
<input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

<table  align="center" width="98%" cellpadding="2" cellspacing="2" style="margin-left:1.4%;margin-right: 2.4% ">

<tr>
<td>

<table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


<tr><td colspan="4">&nbsp;</td></tr>
<tr>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >From</td>
    <td width="3%" class="textb"></td>
    <td width="12%" class="textb">
        <select size="1" name="fdate" class="textb">
            <%
                if (fdate != null)
                    out.println(Functions.dayoptions(1, 31, fdate));
                else
                    out.println(Functions.printoptions(1, 31));
            %>
        </select>
        <select size="1" name="fmonth" class="textb">
            <%
                if (fmonth != null)
                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                else
                    out.println(Functions.printoptions(1, 12));
            %>
        </select>
        <select size="1" name="fyear" class="textb">
            <%
                if (fyear != null)
                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), fyear));
                else
                    out.println(Functions.printoptions(2005, 2013));
            %>
        </select>
    </td>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >To</td>
    <td width="3%" class="textb"></td>
    <td width="12%" class="textb">
        <select size="1" name="tdate" class="textb">

            <%
                if (tdate != null)
                    out.println(Functions.dayoptions(1, 31, tdate));
                else
                    out.println(Functions.printoptions(1, 31));
            %>
        </select>

        <select size="1" name="tmonth" class="textb">

            <%
                if (tmonth != null)
                    out.println(Functions.newmonthoptions(1, 12, tmonth));
                else
                    out.println(Functions.printoptions(1, 12));
            %>
        </select>

        <select size="1" name="tyear" class="textb" style="width:54px;">

            <%
                if (tyear != null)
                    out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                else
                    out.println(Functions.printoptions(2005, 2013));
            %>
        </select>
    </td>
        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb">Company Name</td>
        <td width="3%" class="textb">&nbsp;</td>
        <td width="12%" class="textb">
            <input type=text name="company_name" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(company_name)%>" class="txtbox">
        </td>
    </td>
</tr>
<tr>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>
</tr>
<tr>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >Transaction Status</td>
    <td width="3%" class="textb"></td>
    <td width="10%" class="textb">
        <select size="1" name="activation" class="txtbox">
            <option value="" <%=activation.equals("")?"selected":""%>>All</option>
            <option value="N" <%=activation.equals("N")?"selected":""%>>Inactive</option>
            <option value="Y" <%=activation.equals("Y")?"selected":""%>>Active</option>
            <option value="T" <%=activation.equals("T")?"selected":""%>>Test</option>
        </select>
    </td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb">Site Name</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <input type=text name="sitename" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(sitename)%>"  class="txtbox" size="20">
    </td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="12%" class="textb" >Is Merchant Interface Access</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <select size="1" name="icici" class="txtbox">
            <option value="" <%=icici.equals("")?"selected":""%>>All</option>
            <option value="N" <%=icici.equals("N")?"selected":""%>>Inactive</option>
            <option value="Y" <%=icici.equals("Y")?"selected":""%>>Active</option>
        </select>
    </td>
</tr>
<tr>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>

</tr>

<tr>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" colspan="2" for="mid">Merchant ID</td>
    <td width="12%" class="textb">
        <input name="memberid" id="mid" value="<%=memberid%>" class="txtbox" autocomplete="on">
        <%--<input type=text maxlength="10"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>" name="memberid" size="5"  class="txtbox">--%>
       <%-- <select size="1" name="memberid" class="txtbox">
            <option value="" selected>--Select Member ID--</option>
            <%
                for (Integer mId : memberMap.keySet())
                {
                    String companyname = memberMap.get(mId);
                    String isSelected = "";
                    if (String.valueOf(mId).equalsIgnoreCase(memberid))
                    {
                        isSelected = "selected";
                    }
            %>
            <option value="<%=mId%>" <%=isSelected%>><%=mId+"-"+companyname%></option>
            <%
                }
            %>
        </select>--%>
    </td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >Rows/pages</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <input type="text" maxlength="3"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(new Integer(pagerecords).toString())%>" name="SRecords" size="2" class="txtbox"/>

    </td>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >
        <%
        if(request.getParameter("ignoredates")!=null)
        {
        //System.out.println("ignore value"+request.getParameter("ignoredates"));
        %>
        <input type=checkbox name="ignoredates" value="yes" checked>
        <%
            }
            else
            {
                //System.out.println("ignore value"+request.getParameter("ignoredates"));
        %>
        <input type=checkbox name="ignoredates">
        <%

            }
        %>
        &nbsp;&nbsp;Ignore Dates</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="15%" class="textb">
        <%--<input type=checkbox name="perfectmatch" value="<%=ESAPI.encoder().encodeForHTMLAttribute(perfectMatch)%>" class="textb">--%>
            <select name="perfectmatch">
                <option value="No">NO</option>
                <option value="Yes" selected="">Yes</option>
            </select>&nbsp;&nbsp;Show Perfect Match
    </td>

</tr>
<tr>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">&nbsp;</td>

</tr>
<tr>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >Contact&nbsp;Person&nbsp;Email</td>
    <td width="3%" class="textb"></td>
    <td width="12%" class="textb">
        <input  type="text" name="contact_emails" class="txtbox">
    </td>

    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >Username</td>
    <td width="3%" class="textb"></td>
    <td width="12%" class="textb">
        <%--<input  type="text" name="username" class="txtbox" >--%>
        <input type=text name="login" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(login)%>"  class="txtbox" size="20">
    </td>
    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >Partner Name</td>
    <td width="3%" class="textb"></td>
    <td width="12%" class="textb">
        <%--<input  type="text" name="username" class="txtbox" >--%>
        <input type=text name="partnerName" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(partnerName)%>"  class="txtbox" size="20">
    </td>
</tr>
    <tr>
        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb" >&nbsp;</td>
        <td width="3%" class="textb">&nbsp;</td>
        <td width="12%" class="textb">&nbsp;</td>

        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb" >&nbsp;</td>
        <td width="3%" class="textb">&nbsp;</td>
        <td width="12%" class="textb">&nbsp;</td>

        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb" >&nbsp;</td>
        <td width="3%" class="textb">&nbsp;</td>
        <td width="12%" class="textb">&nbsp;</td>

    </tr>
    <tr>
        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb" >&nbsp;</td>
        <td width="3%" class="textb">&nbsp;</td>
        <td width="12%" class="textb">&nbsp;</td>

        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb" >&nbsp;</td>
        <td width="3%" class="textb">&nbsp;</td>
        <td width="12%" class="textb">&nbsp;</td>


    <td width="4%" class="textb">&nbsp;</td>
    <td width="8%" class="textb" >&nbsp;</td>
    <td width="3%" class="textb">&nbsp;</td>
    <td width="12%" class="textb">
        <button type="submit" class="buttonform" value="Submit">
            <i class="fa fa-clock-o"></i>
            &nbsp;&nbsp;Submit
        </button>
    </td>
    </tr>
<tr>
    <td width="4%" class="textb">&nbsp;</td>


</tr>
</table>
</td>
</tr>
</table>
    </form>
</div>
</div>
</div>


<div class="reporttable">

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
    if(mailresult.equals("success"))
    {
        out.println(Functions.NewShowConfirmation("Successfull","Merchant Registration Verification Mail sent<br>Member Id : "+memberid+""));

    }else
    if(mailresult.equals("fail"))
    {
        out.println(Functions.NewShowConfirmation("Sorry","Failed to send mail"));
    }
    else{
    if(records>0){
%>
<div id="containrecord"></div>
<table>

    <tr>
        <td>
            <form name="exportform" method="post" action="ExportMemberDetails?ctoken=<%=ctoken%>" >
                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>" name="toid">
                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" name="fdate">
                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" name="tdate">
                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fmonth)%>" name="fmonth">
                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tmonth)%>" name="tmonth">
                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fyear)%>" name="fyear">
                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tyear)%>" name="tyear">
                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(ignoredates)%>" name="ignoredates">
                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(company_name)%>" name="company_name">
                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(sitename)%>" name="sitename">
                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(activation)%>" name="activation">
                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(icici)%>" name="icici">
                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(partnerName)%>" name="partnername">
                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(login)%>" name="login">
                <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_emails)%>" name="contact_emails">
                <%--<a onclick="getExcelFile();" href="#"><p  class="textb">Export to Excel</p></a>--%>
<%--
                <button type="submit" class="button3" style="width:70%;margin-left:6% ;margin-top:0px"><b>Export to excel</b>&nbsp;&nbsp;&nbsp;<img width="20%" height="100%" border="0" src="/merchant/images/excel.png"></button>
--%>
                <button type="submit" class="button3" style="width:155px;margin-left:2px;margin-top:0px"><b>Export to excel</b>&nbsp;&nbsp;&nbsp;<img width="20%" height="100%" border="0" src="/merchant/images/excel.png"></button>
            </form>
        </td>
    </tr>

</table>
<table border="1" cellpadding="5" cellspacing="0" bordercolor="#ffffff" align="center" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
	  <td width="3%" class="th0">Activation Date</td>
	  <td width="3%" class="th0">Merchant Id</td>
	  <td width="3%" class="th0">Partner Name</td>
      <td width="3%" class="th0">Username</td>
      <td width="3%" class="th0">Company Name</td>
      <td width="3%" class="th0">Site Name</td>
      <td width="3%" class="th0">Transaction Status</td>
      <td width="5%" class="th0">is Merchant Interface Access</td>
      <td width="3%" class="th0">Contact Person Name</td>
        <td width="5%" class="th0">Contact Email</td>
        <td width="3%" class="th0" colspan="3">Action</td>
    </tr>
    </thead>
<%
    Functions functions = new Functions();
    for(int pos=1;pos<=records;pos++)
	{
        String id=Integer.toString(pos);
        style="class=\"tr"+pos%2+"\"";

        temphash=(Hashtable)hash.get(id);
        partnerName=(String)temphash.get("partnerName");
        String cname=(String)temphash.get("company_name");
        String sname=(String)temphash.get("sitename");
        String mid=(String)temphash.get("memberid");
        String activation_date="";
        if (functions.isValueNull((String)temphash.get("activation_date")))
        {
            activation_date = functions.convertDtstampToDateTime((String)temphash.get("activation_date"));
        }
        else
        {
            activation_date = "-";
        }
        String sta=(String)temphash.get("activation");
        String tsta= (String)temphash.get("icici");
        String username=(String)temphash.get("login");
        String emails=(String)temphash.get("contact_emails");
        out.println("<tr "+ style +">");
        //out.println("<td align=\"center\"><form action=\"MerchantDetails?ctoken="+ctoken+"\" method=\"post\"><input type=\"hidden\" name=\"singlememberid\" value=\""+mid+"\"><input type=\"submit\" class=\"goto\" name=\"submit\" value=\""+mid+"\"></form></td>");
        out.println("<td align=\"center\">"+ activation_date +"</td>");
        out.println("<td align=\"center\">"+ mid +"</td>");
        out.println("<td align=\"center\">"+ partnerName +"</td>");
        out.println("<td align=\"center\">"+ username +"</td>");
        out.println("<td align=\"center\">"+ cname +"</td>");
        out.println("<td align=\"center\">"+ sname +"</td>");

        out.println("<td align=\"center\">"+ getStatus(sta) +"</td>");
        out.println("<td align=\"center\">"+ getStatus(tsta) +"</td>");
        out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("contact_persons"))+"<input type=\"hidden\" name=\"contact_persons\" value=\""+temphash.get("contact_persons")+"\"></td>");
        out.println("<td align=\"center\">"+ emails +"</td>");
        out.println("<td align=\"center\""+style+"><form action=\"/icici/servlet/UpdateMemberDetails?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"memberid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+"\"><input type=\"submit\" name=\"submit\" value=\"View\" class=\"gotoauto\" ><input type=\"hidden\" name=\"action\" value=\"View\"></form></td>");
        out.println("<td align=\"center\""+style+"><form action=\"/icici/servlet/UpdateMemberDetails?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"memberid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+"\"><input type=\"submit\" name=\"submit\" value=\"Edit\" class=\"gotoauto\"><input type=\"hidden\" name=\"action\" value=\"modify\"></form></td>");

        /*verify button form*/
        out.println("<td align=\"center\""+style+"><form action=\"/icici/servlet/VerifyMerchantRegistration?ctoken="+ctoken+"\" method=\"POST\">");
        out.println("<input type=\"hidden\" name=\"memberid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+"\">");
        out.println("<input type=\"hidden\" name=\"login\" value=\""+username+"\">");
        out.println("<input type=\"hidden\" name=\"contact_persons\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("contact_persons"))+"\">");
        out.println("<input type=\"hidden\" name=\"emailtoken\" value=\""+emails+"\">");
        out.println("<input type=\"submit\" name=\"submit\" value=\"Verify\" class=\"gotoauto\">");
        out.println("</form></td>");

        out.println("</tr>");
    }
%>
    </tr>
    <thead>
        <tr>
            <td  align="center" class="th0">Total Records: <%=totalrecords%></td>
            <td  align="center" class="th0">Page No <%=pageno%></td>
            <td align="center" class="th0"></td>
            <td align="center" class="th0"></td>
            <td align="center" class="th0"></td>
            <td align="center"class="th0"></td>
            <td align="center"class="th0"></td>
            <td align="center"class="th0"></td>
            <td align="center"class="th0"></td>
            <td align="center"class="th0"></td>
            <td align="center"class="th0"></td>
            <td align="center"class="th0"></td>
            <td align="center"class="th0"></td>
        </tr>
    </thead>
 </table>

<table align=center valign=top><tr>
<td align=center>
<jsp:include page="page.jsp" flush="true" >
	<jsp:param name="numrecords" value="<%=totalrecords%>" />
 	<jsp:param name="numrows" value="<%=pagerecords%>"/>
	<jsp:param name="pageno" value="<%=pageno%>"/>
	<jsp:param name="str" value="<%=(str)%>"/>
	<jsp:param name="page" value="MerchantDetails"/>
	<jsp:param name="currentblock" value="<%=currentblock%>"/>
	<jsp:param name="orderby" value=""/>
</jsp:include>
</td>
</table>
<%
    }else
    {
        int fmonthnum = Integer.parseInt(fmonth)+1;
        int tmonthnum = Integer.parseInt(tmonth)+1;
        out.println(Functions.NewShowConfirmation("Sorry","No records found. Try again with different criteria.<br><br>Date :<br>From " + fdate + "/" + fmonthnum + "/" + fyear + "<br>To " + tdate + "/" + tmonthnum + "/" + tyear));
    }
        }
  }catch(NumberFormatException nex){

  }
%>
    </div>



<%
    }
    else
{
    response.sendRedirect("/icici/logout.jsp");
    return;
}
%>
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