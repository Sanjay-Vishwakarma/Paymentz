<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Partner" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="com.directi.pg.Logger" %>


<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 10/1/13
  Time: 2:14 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));%>
<html>
<head>
    <title><%=company%> Member Details </title>
</head>
<body>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    if (partner.isLoggedInPartner(session))
    {
    Hashtable memberidDetails=partner.getPartnerMemberDetails((String)session.getAttribute("merchantid"));
    String memberid=nullToStr(request.getParameter("memberid"));
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

    }String str="";
    String ignoredates=Functions.checkStringNull(request.getParameter("ignoredates"));
    Calendar rightNow = Calendar.getInstance();
    String currentyear= ""+rightNow.get(rightNow.YEAR);

    rightNow.setTime(new Date());
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
    if(ignoredates!=null)str=str+"&ignoredates="+ignoredates;
    str=str+"&ctoken="+ctoken;

    int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),30);
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                <%=company%> Member Details

            </div>
            <form name="form" method = "post" action="/partner/net/MerchantDetails?ctoken=<%=ctoken%>">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">

                <table  align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:-2%;margin-right: 2.5% ">

                    <tr>
                        <td>

                            <table border="0" cellpadding="5" cellspacing="0" width="90%"  align="center">


                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="0%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >From</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="18%" class="textb">
                                        <select size="1" name="fdate">
                                            <%      if(fdate!=null)
                                                out.println(Functions.dayoptions(1,31,fdate));
                                            else
                                                out.println(Functions.printoptions(1,31));
                                            %>
                                        </select>
                                        <select size="1" name="fmonth">
                                            <%      if(fmonth!=null)
                                                out.println(Functions.newmonthoptions(1,12,fmonth));
                                            else
                                                out.println(Functions.printoptions(1,12));
                                            %>
                                        </select>
                                        <select size="1" name="fyear">
                                            <%      if(fyear!=null)
                                                out.println(Functions.yearoptions(2005,Integer.parseInt(currentyear),fyear));
                                            else
                                                out.println(Functions.printoptions(2005,Integer.parseInt(currentyear)));
                                            %>
                                        </select>

                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >To</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="22%" class="textb">
                                        <select size="1" name="tdate">
                                            <%      if(tdate!=null)
                                                out.println(Functions.dayoptions(1,31,tdate));
                                            else
                                                out.println(Functions.printoptions(1,31));
                                            %>
                                        </select>
                                        <select size="1" name="tmonth">
                                            <%      if(tmonth!=null)
                                                out.println(Functions.newmonthoptions(1,12,tmonth));
                                            else
                                                out.println(Functions.printoptions(1,12));
                                            %>
                                        </select>
                                        <select size="1" name="tyear">
                                            <%      if(tyear!=null)
                                                out.println(Functions.yearoptions(2005,Integer.parseInt(currentyear),tyear));
                                            else
                                                out.println(Functions.printoptions(2005,Integer.parseInt(currentyear)));
                                            %>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="15%" class="textb" ></td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <input type=checkbox name="ignoredates" value="yes" <%="yes".equals(request.getParameter("ignoredates"))?"checked":""%>>&nbsp;&nbsp;Ignore Dates

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
                                    <td width="12%" class="textb" > Memberid ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select size="1" name="memberid" class="txtbox">
                                            <option value="all">All</option>
                                            <%
                                                Enumeration enu3 = memberidDetails.keys();
                                                String selected3 = "";
                                                String key3 = "";
                                                String value3 = "";
                                                while (enu3.hasMoreElements())
                                                {
                                                    key3 = (String) enu3.nextElement();
                                                    value3 = (String) memberidDetails.get(key3);
                                                    if (value3.equals(memberid))
                                                        selected3 = "selected";
                                                    else
                                                        selected3 = "";
                                            %>
                                            <option value="<%=value3%>" <%=selected3%>><%=value3%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
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
            </form>

        </div>
    </div>
</div>
<div class="reporttable">
<%  Hashtable hash = (Hashtable)request.getAttribute("transdetails");

    Hashtable temphash=null;
    int records=0;
    int totalrecords=0;
    String error=(String ) request.getAttribute("error");
    if(error !=null)
    {
        out.println(error);
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
                <td valign="middle" align="center" class="th0">Sr no</td>
                <td valign="middle" align="center" class="th0">MemberID</td>
                <td valign="middle" align="center" class="th0">Company Name</td>
                <td valign="middle" align="center" class="th0">Contact Persons</td>
                <td valign="middle" align="center" class="th0">Contact Emails</td>
                <td valign="middle" align="center" class="th0">Country</td>
            </tr>
        </thead>
        <%
            String style="class=td1";
            String ext="light";

            for(int pos=1;pos<=records;pos++)
            {
                String id=Integer.toString(pos);
                int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);
                style = "class=\"tr" + (pos + 1) % 2 + "\"";
                temphash=(Hashtable)hash.get(id);
                out.println("<tr>");
                out.println("<td align=\"center\""+style+">&nbsp;"+srno+ "</td>");
                out.println("<td align=\"center\""+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("memberid"))+"<input type=\"hidden\" name=\"trackingid\" value=\""+temphash.get("trackingid")+"\"></td>");
                out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("company_name"))+"<input type=\"hidden\" name=\"accountid\" value=\""+temphash.get("accountid")+"\"></td>");
                out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("contact_persons"))+"<input type=\"hidden\" name=\"toid\" value=\""+temphash.get("toid")+"\"></td>");
                out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("contact_emails"))+"<input type=\"hidden\" name=\"description\" value=\""+temphash.get("description")+"\"></td>");
                out.println("<td align=\"center\""+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("country"))+"</td>");
                out.println("</tr>");
            }
        %>

    </table>
    <table align=center valign=top><tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="MerchantDetails"/>
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
        out.println(Functions.NewShowConfirmation1("Sorry","No records found."));
    }
%>

</body>
</div>
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
<%
    }
    else
    {
        response.sendRedirect("/partner/logout.jsp");
        return;
    }
%>
</html>