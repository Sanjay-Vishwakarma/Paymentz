<%@ page import="java.util.*" %>
<%@ page import="sun.font.Script" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="ietest.jsp" %>
<%@ include file="index.jsp"%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
<script src="/icici/javascript/cardtype-issuing-bank.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>

<%!
  private static Logger log=new Logger("payoutTransactions.jsp");
%>

<%
  String memberid = (String)session.getAttribute("merchantid");
  HashMap<String, String> statusFlagHash  = new LinkedHashMap();
  HashMap<String, String> statusHash      = new LinkedHashMap();

  statusHash.put("payoutfailed", "Payout Failed");
  statusHash.put("payoutstarted", "Payout Started");
  statusHash.put("payoutsuccessful", "Payout Successful");
  //statusHash.put("payoutcancelstarted", "Payout Cancel Started");
  //statusHash.put("payoutcancelsuccessful", "Payout Cancel Successful");
  //statusHash.put("payoutcancelfailed", "Payout Cancel Failed");
  //String currency =  (String)session.getAttribute("currency");
  Hashtable hiddenvariables = (Hashtable)request.getAttribute("hiddenvariables");

  String str              = "";

  String fdate    = null;
  String tdate    = null;
  String fmonth   = null;
  String tmonth   = null;
  String fyear    = null;
  String tyear    = null;



  try
  {
    fdate   = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"Days",2,true);
    tdate   = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"Days",2,true);
    fmonth  = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
    tmonth  =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
    fyear   = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
    tyear   = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);
  }
  catch (ValidationException e)
  {
    log.error("Date Format Exception while select");
  }

  String flaghash         = Functions.checkStringNull(request.getParameter("statusflag"));
  String status           = Functions.checkStringNull(request.getParameter("status"));
  String startTime        = Functions.checkStringNull(request.getParameter("starttime"));
  String endTime          = Functions.checkStringNull(request.getParameter("endtime"));
  String toid     = request.getParameter("toid")==null?"":request.getParameter("toid");
  String emailaddr    = Functions.checkStringNull(request.getParameter("emailaddr"));
  String trackingid   = Functions.checkStringNull(request.getParameter("STrackingid"));
  String desc         = Functions.checkStringNull(request.getParameter("desc"));
  String amt          = Functions.checkStringNull(request.getParameter("amount"));
  String accountid    = request.getParameter("accountid")==null?"":request.getParameter("accountid");
  String partnerid     = request.getParameter("partnerid")==null?"":request.getParameter("partnerid");
  String bankacc      = Functions.checkStringNull(request.getParameter("bankaccount"));

  if (desc == null) desc = "";
  if (amt == null) amt = "";
  if (emailaddr == null) emailaddr = "";
  if (trackingid == null) trackingid = "";
  if (toid == null) toid = "";
  if (bankacc == null) bankacc = "";

  Calendar rightNow = Calendar.getInstance();
  String currentyear = "" + rightNow.get(Calendar.YEAR);
  if (fdate == null) fdate = "" + 1;
  if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);
  if (fmonth == null) fmonth = "" + rightNow.get(Calendar.MONTH);
  if (tmonth == null) tmonth = "" + rightNow.get(Calendar.MONTH);
  if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
  if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);
  if (startTime == null) startTime = "00:00:00";
  if (endTime == null) endTime = "23:59:59";

  if (fdate != null) str = str + "fdate=" + fdate;
  if (tdate != null) str = str + "&tdate=" + tdate;
  if (fmonth != null) str = str + "&fmonth=" + fmonth;
  if (tmonth != null) str = str + "&tmonth=" + tmonth;
  if (fyear != null) str = str + "&fyear=" + fyear;
  if (tyear != null) str = str + "&tyear=" + tyear;
  if (startTime != null) str = str + "&starttime=" + startTime;
  if (endTime != null) str = str + "&endtime=" + endTime;

%>


<html>
<head>
  <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
  <title>Payout Transaction List</title>

</head>


<body>
<%
  System.out.println("inside payoutTransactions.jsp--->");
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    try
    {

      int pageno = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
      int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 30);
%>
<form name="form" method="post" action="/icici/servlet/PayoutTransactionDetails?ctoken=<%=ctoken%>" onsubmit="return validateccnum()">
  <input type="hidden" value="<%=ctoken%>" name="ctoken">
  <div class="row" >
    <div class="col-lg-12" style="position:static;">
      <div class="panel panel-default">
        <div class="panel-heading">
          <p>Payout Transactions Details</p>
        </div>
        <br>
        <div style="width: 100%;overflow: auto;">
          <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">
            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
            <tr>
              <td>
                <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                  <tr><td colspan="12">&nbsp;</td></tr>
                  <tr>
                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >From</td>
                    <td width="3%" class="textb"></td>
                    <td width="7%" class="textb">
                      <select size="1" name="fdate" value="" >
                        <%
                          if (fdate != null)
                            out.println(Functions.dayoptions(1, 31, fdate));
                          else
                            out.println(Functions.printoptions(1, 31));
                        %>
                      </select>
                      <select size="1" name="fmonth"  value="" >
                        <%
                          if (fmonth != null)
                            out.println(Functions.newmonthoptions(1, 12, fmonth));
                          else
                            out.println(Functions.printoptions(1, 12));
                        %>
                      </select>
                      <select size="1" name="fyear" value="" >
                        <%
                          if (fyear != null)
                            out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), fyear));
                          else
                            out.println(Functions.printoptions(2005, 2020));
                        %>
                      </select>
                    </td>
                    <td width="10%" class="textb">
                      <input type="text" size="6" placeholder="HH:MM:SS" name="starttime" maxlength="8" value="<%=startTime%>"/>
                    </td>
                    <td width="5%" class="textb">To</td>
                    <td width="3%" class="textb"></td>
                    <td width="7%" class="textb">
                      <select size="1" name="tdate" >
                        <%
                          if (tdate != null)
                            out.println(Functions.dayoptions(1, 31, tdate));
                          else
                            out.println(Functions.printoptions(1, 31));
                        %>
                      </select>

                      <select size="1" name="tmonth" >
                        <%
                          if (tmonth != null)
                            out.println(Functions.newmonthoptions(1, 12, tmonth));
                          else
                            out.println(Functions.printoptions(1, 12));
                        %>
                      </select>

                      <select size="1" name="tyear" >
                        <%
                          if (tyear != null)
                            out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                          else
                            out.println(Functions.printoptions(2005, 2020));
                        %>
                      </select>
                    </td>
                    <td width="10%" class="textb">
                      <input type="text" size="6" placeholder="HH:MM:SS" name="endtime" maxlength="8" value="<%=endTime%>"/>
                    </td>
                    <td width="4%" class="textb" >Status</td>
                    <td width="3%" class="textb"></td>
                    <td width="10%" class="textb">
                      <select size="1" name="status" class="txtbox">
                        <option value="">All</option>
                        <%
                          Set statusSet = statusHash.keySet();
                          Iterator iterator=statusSet.iterator();
                          String selected = "";
                          String key = "";
                          String value = "";

                          while (iterator.hasNext())
                          {
                            key = (String)iterator.next();
                            value = (String) statusHash.get(key);

                            if (key.equals(status))
                              selected = "selected";
                            else
                              selected = "";
                        %>
                        <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key)%>" <%=selected%>><%=ESAPI.encoder().encodeForHTML(value)%></option>
                        <%

                          }
                        %>
                      </select>
                    </td>
                  </tr>
                  <tr>
                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >&nbsp;</td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="7%" class="textb">&nbsp;</td>

                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >&nbsp;</td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="7%" class="textb">&nbsp;</td>

                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >&nbsp;</td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="12%" class="textb">&nbsp;</td>

                  </tr>
                  <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="4%" class="textb">Merchant ID</td>
                  <td width="3%" class="textb" > </td>
                  <td width="12%" class="textb">
                    <input type=text name="toid" id="memberid1" maxlength="50"  value="<%=toid%>" class="txtbox" size="50" autocomplete="on">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Email ID</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="email" id="email" value="<%=emailaddr%>" class="txtbox" >
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Tracking ID</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="trackingid" id="trackingid" value="<%=trackingid%>" class="txtbox" >
                  </td>
                  </tr>
                  <tr>
                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >&nbsp;</td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="7%" class="textb">&nbsp;</td>

                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >&nbsp;</td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="7%" class="textb">&nbsp;</td>

                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >&nbsp;</td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="12%" class="textb">&nbsp;</td>

                  </tr>
                  <tr>

                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb">Description</td>
                    <td width="3%" class="textb"></td>
                    <td width="12%" class="textb">
                      <input name="description" id="description" value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>" class="txtbox" >
                    </td>

                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="5%" class="textb" >Account ID</td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="7%" class="textb">
                      <input type=text name="accountid" maxlength="10"  id="accountid1" value="<%=accountid%>" class="txtbox" size="10" autocomplete="on">
                    </td>

                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="5%" class="textb" >Partner ID</td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="7%" class="textb">
                      <input type=text name="partnerid" maxlength="10"  id="pid1" value="<%=partnerid%>" class="txtbox" size="10" autocomplete="on">
                    </td>
                  </tr>
                  <tr>
                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >&nbsp;</td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="7%" class="textb">&nbsp;</td>

                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >&nbsp;</td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="7%" class="textb">&nbsp;</td>

                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >&nbsp;</td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="12%" class="textb">&nbsp;</td>

                  </tr>
                  <tr>

                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb">Amount</td>
                    <td width="3%" class="textb"></td>
                    <td width="12%" class="textb">
                      <input name="amount" id="amount" value="<%=ESAPI.encoder().encodeForHTMLAttribute(amt)%>" class="txtbox" >
                    </td>


                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="5%" class="textb" >Bank Account Number</td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="7%" class="textb">
                      <input type=text name="bankaccount" maxlength="10"  value="<%= ESAPI.encoder().encodeForHTMLAttribute(bankacc) %>" class="txtbox" size="10">
                    </td>


                  </tr>
                  <tr>
                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >&nbsp;</td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="7%" class="textb">&nbsp;</td>

                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >&nbsp;</td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="7%" class="textb">&nbsp;</td>

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
                    <td width="10%" class="textb" >
                      <input type="checkbox" value="yes" name="perfectmatch"><font color="#1D7F2C">&nbsp;&nbsp;<b>Show Perfect Match</b></font>
                    </td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="12%" class="textb">
                      <button type="submit" class="buttonform">
                        Search
                      </button>
                    </td>


                  </tr>


                  <tr>
                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >&nbsp;</td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="7%" class="textb">&nbsp;</td>

                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >&nbsp;</td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="7%" class="textb">&nbsp;</td>

                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >&nbsp;</td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="12%" class="textb">&nbsp;</td>

                  </tr>
                </table>
              </td>
            </tr>
          </table>
        </div>
      </div>
    </div>
  </div>
</form>
<div class="reporttable">
  <%
    Hashtable hash = (Hashtable) request.getAttribute("transactionsdetails");
    System.out.println("transactionsdetails hash==="+hash);
    Hashtable temphash = null;
    if (hash != null && hash.size() > 0)
    {
      int records = 0;
      int totalrecords = 0;
      int currentblock = 1;

      try
      {
        records = Integer.parseInt((String) hash.get("records"));
        totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
        System.out.println(totalrecords);
        if(request.getParameter("currentblock")!=null)
        {
          currentblock = Integer.parseInt(request.getParameter("currentblock"));
        }
      }
      catch (NumberFormatException ex)
      {
        log.error("Records & TotalRecords is found null",ex);
      }
      //str = str + "SRecords=" + pagerecords;
      str = str + "&ctoken=" + ctoken;
      String style = "class=td0";
      String ext = "light";
      if (records > 0)
      {

  %>
  <br/>
  <div class="pull-right">
    <form name="exportform" method="post" action="ExportPayoutTransactions?ctoken=<%=ctoken%>" >
      <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>" name="desc">
      <%
        if (!request.getParameter("toid").equalsIgnoreCase("0"))
        {
      %>
      <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(toid)%>" name="toid">
      <%
        }
      %>
      <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(amt)%>" name="amt">
      <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailaddr)%>" name="emailaddr">
      <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>" name="trackingid">
      <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(status)%>" name="status">
      <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" name="fdate">
      <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" name="tdate">
      <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fmonth)%>" name="fmonth">
      <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tmonth)%>" name="tmonth">
      <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fyear)%>" name="fyear">
      <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tyear)%>" name="tyear">
      <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(startTime)%>" name="starttime">
      <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(endTime)%>" name="endtime">
      <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(accountid)%>" name="accountid">
      <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(flaghash)%>" name="statusflag">
      <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(partnerid)%>" name="partnerid">


      <%
        if (request.getParameter("pgtypeid")!=null)
        {
      %>
      <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("pgtypeid"))%>" name="gateway">
      <%

        }
//            if (!request.getParameter("accountid").equalsIgnoreCase("0"))
//            {
      %>
      <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("accountid"))%>" name="accountid">
      <%--<%
          }
      %>--%>
      <button type="submit" class="button3" style="background: transparent; border-radius: 25px;"><b>Export to excel</b>&nbsp;&nbsp;&nbsp;<img width="20%" height="100%" border="0" src="/merchant/images/excel.png"></button>
    </form>
  </div>
  <br>
  <div id="containrecord"></div>
  <table border="1" cellpadding="5" cellspacing="0" width="750" align="center" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
      <td width="7%" class="th0">Transaction Date</td>
      <td width="4%" class="th1">Tracking ID</td>
      <td width="4%" class="th1">Partner Name</td>
      <td width="7%" class="th0">Description</td>
      <td width="7%" class="th0">Account ID</td>
      <td width="7%" class="th0">Member ID</td>
      <td width="7%" class="th1">Amount</td>
      <td width="7%" class="th0">Email ID</td>
      <td width="7%" class="th0">Terminal ID</td>
      <td width="7%" class="th0">Bank Account Number</td>
      <td width="7%" class="th0">Bank Account Name</td>
      <td width="7%" class="th0">Bank IFSC</td>
      <td width="7%" class="th0">Status</td>
      <td width="7%" class="th0">Remark</td>
    </tr>
    </thead>
    <%
      Functions functions=new Functions();
      for (int pos = 1; pos <= records; pos++)
      {
        String id = Integer.toString(pos);

        int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

        style = "class=\"tr" + pos % 2 + "\"";

        temphash = (Hashtable) hash.get(id);
        String transactionTime = (String) temphash.get("transactionDate");
        String date             = (String) temphash.get("dtstamp");
        String icicitransid = (String) temphash.get("trackingid");
        String currency = (String) temphash.get("currency");
        String accountId = (String) temphash.get("accountid");
        String memberId = (String) temphash.get("toid");
        String terminalid = (String) temphash.get("terminalId");
        String amount = currency + " " + temphash.get("amount");
        String remark=(String)temphash.get("remark");
        String status1=(String)temphash.get("status");
        String email=(String)temphash.get("emailaddr");
        String partnerName=(String)temphash.get("partnerName");
        String description=(String)temphash.get("description");
        String fullname=(String)temphash.get("fullname");
        String bankaccount=(String)temphash.get("bankaccount");
        String ifsc=(String)temphash.get("ifsc");
        String totype=(String)temphash.get("totype");

        if(!functions.isValueNull(accountId))
          accountId = "";

        if(!functions.isValueNull(memberId))
          memberId = "";

        if(!functions.isValueNull(terminalid))
          terminalid = "";

        if(!functions.isValueNull(remark))
          remark = "";

        if(!functions.isValueNull(status1))
          status1 = "";

        if(!functions.isValueNull(terminalid))
          terminalid = "";

        if(!functions.isValueNull(email))
          email = "";

        if(!functions.isValueNull(partnerName))
          partnerName = "";

        if(!functions.isValueNull(description))
          description = "";



        out.println("<tr " + style + ">");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(date) + "</td>");
        out.println("<td align=center>" +
                "<form action=\"PayoutTransactionDetails?ctoken="+ctoken+"\" method=\"post\">" +
                "<input type=\"hidden\" name=\"action\" value=\"TransactionDetails\">" +
                "<input type=\"hidden\" name=\"STrackingid\" value=\""+icicitransid+"\">" +
                "<input type=\"hidden\" name=\"partnername\" value=\""+totype+"\">" +
                "<input type=\"hidden\" name=\"ctoken\" value=\""+ctoken+"\">" +
                "<input type=\"hidden\" name=\"accountid\" value=\""+accountId+"\">" +
                "<input type=\"hidden\" name=\"fdate\" value=\""+fdate+"\">" +
                "<input type=\"hidden\" name=\"tdate\" value=\""+tdate+"\">" +
                "<input type=\"hidden\" name=\"fmonth\" value=\""+fmonth+"\">" +
                "<input type=\"hidden\" name=\"tmonth\" value=\""+tmonth+"\">" +
                "<input type=\"hidden\" name=\"fyear\" value=\""+fyear+"\">" +
                "<input type=\"hidden\" name=\"tyear\" value=\""+tyear+"\">" +
                "<input type=\"hidden\" name=\"starttime\" value=\""+startTime+"\">" +
                "<input type=\"hidden\" name=\"endtime\" value=\""+endTime+"\">" +
                "<input type=\"submit\" class=\"goto\" name=\"submit\" value=\""+icicitransid+"\">" +
                "</form>" +
                "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(totype) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(description) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(accountId) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(memberId) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(amount)+"-"+(currency) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(email) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(terminalid) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(bankaccount) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(fullname) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(ifsc) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(status1) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(remark) + "</td>");
        out.println("</tr>");
      }
    %>
    <thead>
    <tr>
      <td class="th0" align="center" class=textb>Page No:- <%=pageno%></td>
      <td class="th0"align="center" class=textb>Total Records: <%=totalrecords%></td>
      <td class="th0">&nbsp;</td>
      <td class="th0">&nbsp;</td>
      <td class="th0">&nbsp;</td>
      <td class="th0">&nbsp;</td>
      <td class="th0">&nbsp;</td>
      <td class="th0">&nbsp;</td>
      <td class="th0">&nbsp;</td>
      <td class="th0">&nbsp;</td>
      <td class="th0">&nbsp;</td>
      <td class="th0">&nbsp;</td>
      <td class="th0">&nbsp;</td>
      <td class="th0">&nbsp;</td>
      <td class="th0">&nbsp;</td>
    </tr>
    </thead>
  </table>
  <table align=center valign=top><tr>
    <td align=center>
      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
        <jsp:param name="numrows" value="<%=pagerecords%>"/>
        <jsp:param name="pageno" value="<%=pageno%>"/>
        <jsp:param name="str" value="<%=str%>"/>
        <jsp:param name="page" value="PayoutTransactionDetails"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
    </td>
  </table>
    </table>
  <%
        }
        else
        {
          out.println(Functions.NewShowConfirmation("Sorry", "No records found.<br><br>Date :<br>From " + fdate + "/" + (Integer.parseInt(fmonth) + 1) + "/" + fyear + "<br>To " + tdate + "/" + (Integer.parseInt(tmonth) + 1) + "/" + tyear));
        }
      }
      else
      {
        out.println(Functions.NewShowConfirmation("Sorry", "No records found"));
      }
    }
    catch (Exception e)
    {
      log.error("Exception while getting the transactions",e);
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
</html>
