<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp"%>
<%@ include file="functions.jsp"%>
<%@ page
        import="com.directi.pg.Functions,com.directi.pg.TransactionEntry,com.logicboxes.util.ApplicationProperties,java.util.Calendar,java.util.Enumeration,
                 org.owasp.esapi.errors.ValidationException" %>
<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 4/17/14
  Time: 2:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
<%--

--%>
    <title></title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<%  String memberid=nullToStr(request.getParameter("memberid"));
    String company_name=nullToStr(request.getParameter("company_name"));
    String sitename=nullToStr(request.getParameter("sitename"));
    String activation=nullToStr(request.getParameter("activation"));
    String icici=nullToStr(request.getParameter("icici"));
    String reserves=nullToStr(request.getParameter("reserves"));
    String chargeper=nullToStr(request.getParameter("chargeper"));
    String contact_emails=nullToStr(request.getParameter("contact_emails"));

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
    if(activation!=null)str=str+"&activation="+activation;
    if(icici!=null)str=str+"&icici="+icici;
    if(ignoredates!=null)str=str+"&ignoredates="+ignoredates;
    if(perfactMatch!=null)str=str+"&perfectmatch="+perfactMatch;
    if(contact_emails!=null)str=str+"&contact_emails="+contact_emails;
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
    <%--<div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default" >
                <div class="panel-heading" >
                         Merchant Master
                        &lt;%&ndash;<div style="float: right;">
                            <form action="/icici/membersignup.jsp?ctoken=<%=ctoken%>" method="POST">

                                <button type="submit" class="addnewmember" value="Add New Member" name="submit">
                                    <i class="fa fa-sign-in"></i>
                                    &nbsp;&nbsp;Add New Member
                                </button>
                            </form>
                        </div>&ndash;%&gt;
                </div>--%>
                <%--<form action="/icici/servlet/MemberDetailList?ctoken=<%=ctoken%>" method="post" name="forms" >
                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                        <%
                            String str="ctoken=" + ctoken;
                            if (request.getParameter("memberid") != null) str = str + "&memberid=" + request.getParameter("memberid");
                            int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                            int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);


                        %>
                    <%
                        if(request.getAttribute("error")!=null)
                        {
                        String message = (String) request.getAttribute("error");
                        if(message != null)
                            out.println("<center><font class=\"textb\">"+message+"</font></center><br/><br/>");
                        }

                    %>
                    <table  align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:-2%;margin-right: 2.5% ">

                        <tr>
                            <td>

                                <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                                    <tr><td colspan="4">&nbsp;</td></tr>
                                    <tr>
                                        <td width="0%" class="textb">&nbsp;</td>
                                        <td width="11%" class="textb" >Merchant Id</td>
                                        <td width="3%" class="textb"></td>
                                        <td width="12%" class="textb">
                                            <input  type="text" name="memberid"  class="txtbox">

                                        </td>
                                        <td width="4%" class="textb">&nbsp;</td>
                                        <td width="11%" class="textb" >Username</td>
                                        <td width="3%" class="textb"></td>
                                        <td width="12%" class="textb">
                                            <input  type="text" name="username" class="txtbox" >

                                        </td>
                                        <td width="4%" class="textb">&nbsp;</td>
                                        <td width="15%" class="textb" >Contact&nbsp;Person&nbsp;Name</td>
                                        <td width="3%" class="textb"></td>
                                        <td width="10%" class="textb">
                                            <input  type="text" name="contact_persons" class="txtbox" >

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
                                        <td width="12%" class="textb" >Contact&nbsp;Person&nbsp;Email</td>
                                        <td width="3%" class="textb"></td>
                                        <td width="12%" class="textb">
                                            <input  type="text" name="contact_emails" class="txtbox">
                                        </td>

                                        <td width="4%" class="textb">&nbsp;</td>
                                        <td width="8%" class="textb">Company Name For</td>
                                        <td width="3%" class="textb">&nbsp;</td>
                                        <td width="12%" class="textb">
                                            <input  type="text" name="company_name" class="txtbox" >
                                        </td>

                                        <td width="4%" class="textb">&nbsp;</td>
                                        <td width="8%" class="textb"></td>
                                        <td width="3%" class="textb">&nbsp;</td>
                                        <td width="12%" class="textb">
                                            <button type="submit" class="buttonform" >
                                                <i class="fa fa-clock-o"></i>
                                                &nbsp;&nbsp;Search
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
                </form>--%>

                <form name="form" method = "post" action="MerchantDetails?ctoken=<%=ctoken%>">


                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                    <div class="row">
                        <div class="col-lg-12">
                            <div class="panel panel-default">
                                <div class="panel-heading">
                                    Merchant Master

                                </div>

                                <%--<%
                                    String errormsg = (String) request.getAttribute("error");
                                    if(errormsg!=null)
                                    {
                                        out.println("<br><font class=\"textb\"><b>");
                                        errormsg = errormsg.replace("&lt;BR&gt;","<BR>");
                                        //out.println(errormsg);
                                        out.println("</b></font>");
                                        out.println("</td></tr></table>");
                                    }

                                %>--%>
                                <%
                                    String str1="ctoken=" + ctoken;
                                    if (request.getParameter("memberid") != null) str1 = str1 + "&memberid=" + request.getParameter("memberid");
                                    int pageno1=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                                    int pagerecords1=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);


                                %>
                                <%
                                    if(request.getAttribute("error")!=null)
                                    {
                                        String message = (String) request.getAttribute("error");
                                        if(message != null)
                                            out.println("<center><font class=\"textb\">"+message+"</font></center><br/><br/>");
                                    }

                                %>
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
                                                    <td width="12%" class="textb" >is Merchant Interface Access</td>
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
                                                    <td width="8%" class="textb" colspan="2">Merchant ID</td>
                                                    <td width="12%" class="textb">
                                                        <input type=text maxlength="10"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>" name="memberid" size="5"  class="txtbox">
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
                                                        <input  type="text" name="username" class="txtbox" >
                                                    </td>

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

                            </div>
                        </div>
                    </div>
                </form>

            </div>
        </div>
    </div>
<div class="reporttable">
    <br><br>
<%  Hashtable hash = (Hashtable)request.getAttribute("transdetails");

    Hashtable temphash=null;
    int records=0;
    int totalrecords=0;

    String errormsg=(String)request.getAttribute("error");
    if(errormsg!=null)
    {
        out.println("<center><class=\"textb\" "+errormsg+"</center>");
    }

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
    if(hash!=null)
    {
        hash = (Hashtable)request.getAttribute("transdetails");
    }
    if(records>0)
    {
%>
<table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
        <tr>
            <td valign="middle" align="center"  class="th0">Sr no</td>
            <td valign="middle" align="center" class="th0">Member Id</td>
            <td valign="middle" align="center" class="th0">Member Username</td>
            <td valign="middle" align="center" class="th0">Contact Person Name</td>
            <td valign="middle" align="center" class="th0">Company Name</td>
            <td valign="middle" align="center" class="th0">Contact Email</td>
            <td valign="middle" align="center" class="th0" colspan = "2">Action</td>
        </tr>
    </thead>
    <%
        String style="class=td1";
        String ext="light";

        for(int pos=1;pos<=records;pos++)
        {
            String id=Integer.toString(pos);

            int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);

            if(pos%2==0)
            {
                style="class=tr0";
                ext="dark";
            }
            else
            {
                style="class=tr1";
                ext="light";
            }

            temphash=(Hashtable)hash.get(id);
            out.println("<tr>");
            out.println("<td align=\"center\" "+style+">&nbsp;"+srno+ "</td>");
            out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+"<input type=\"hidden\" name=\"memberid\"value=\""+temphash.get("memberid")+"\"></td>");
            out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("login"))+"<input type=\"hidden\" name=\"login\" value=\""+temphash.get("login")+"\"></td>");
            out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("contact_persons"))+"<input type=\"hidden\" name=\"contact_persons\" value=\""+temphash.get("contact_persons")+"\"></td>");
            out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("company_name"))+"<input type=\"hidden\" name=\"company_name\" value=\""+temphash.get("company_name")+"\"></td>");
            out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("contact_emails"))+"<input type=\"hidden\" name=\"contact_emails\" value=\""+temphash.get("contact_emails")+"\"></td>");
            out.println("<td align=\"center\""+style+"><form action=\"/icici/servlet/UpdateMemberDetails?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"memberid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+"\"><input type=\"submit\" name=\"submit\" value=\"View\" class=\"gotoauto\" ><input type=\"hidden\" name=\"action\" value=\"View\"></form></td>");
            out.println("<td align=\"center\""+style+"><form action=\"/icici/servlet/UpdateMemberDetails?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"memberid\" value=\""+ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+"\"><input type=\"submit\" name=\"submit\" value=\"Edit\" class=\"gotoauto\"><input type=\"hidden\" name=\"action\" value=\"modify\"></form></td>");
            out.println("</tr>");
        }
    %>
</table>

<table align=center valign=top>
    <tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true">
            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
            <jsp:param name="numrows" value="<%=pagerecords%>"/>
            <jsp:param name="pageno" value="<%=pageno%>"/>
            <jsp:param name="str" value="<%=str%>"/>
            <jsp:param name="page" value="MemberDetailList"/>
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
        out.println(Functions.NewShowConfirmation("Sorry","No records found."));
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