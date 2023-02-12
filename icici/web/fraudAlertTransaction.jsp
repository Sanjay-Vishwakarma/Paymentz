<%@ page import="com.directi.pg.Functions,com.directi.pg.Logger,com.directi.pg.core.GatewayAccount,com.directi.pg.core.GatewayAccountService,
                com.directi.pg.core.GatewayType,com.directi.pg.core.GatewayTypeService,org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.*" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%!
  private static Logger log=new Logger("transactions.jsp");
%>
<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
<script src="/icici/javascript/cardtype-issuing-bank.js"></script>
<%
  String accountid = request.getParameter("accountid")==null?"":request.getParameter("accountid");
  String memberid = request.getParameter("toid")==null?"":request.getParameter("toid");
  String terminalId = request.getParameter("terminalId")==null?"":request.getParameter("terminalId");
  String authCode = Functions.checkStringNull(request.getParameter("authCode"));
  String amt = Functions.checkStringNull(request.getParameter("amount"));
  String rrn = Functions.checkStringNull(request.getParameter("rrn"));
  String trackingid = Functions.checkStringNull(request.getParameter("STrackingid"));
  String refundamount = Functions.checkStringNull(request.getParameter("refundamount"));
  String mid = Functions.checkStringNull(request.getParameter("mid"));
  String isRefund = Functions.checkStringNull(request.getParameter("isRefund"));
  String transactionType = Functions.checkStringNull(request.getParameter("transactionType"));
  String paymentid = Functions.checkStringNull(request.getParameter("paymentid"))==null?"":request.getParameter("paymentid");
  String firstfourofccnum = Functions.checkStringNull(request.getParameter("firstfourofccnum"));
  String lastfourofccnum = Functions.checkStringNull(request.getParameter("lastfourofccnum"));
  String startTime = Functions.checkStringNull(request.getParameter("starttime"));
  String endTime = Functions.checkStringNull(request.getParameter("endtime"));

  String str = "";
  if (authCode == null) authCode = "";
  if (amt == null) amt = "";
  if (rrn == null) rrn = "";
  if (trackingid == null) trackingid = "";
  if (memberid == null) memberid = "";
  if (refundamount == null) refundamount = "";
  if (mid == null) mid = "";
  if (firstfourofccnum == null)firstfourofccnum = "";
  if (lastfourofccnum == null)lastfourofccnum = "";
  if (startTime == null) startTime = "00:00:00";
  if (endTime == null) endTime = "23:59:59";

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
    tmonth =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
    fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
    tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);
  }
  catch (ValidationException e)
  {
    log.error("Date Format Exception while select");
  }

  Calendar rightNow = Calendar.getInstance();
  String currentyear = "" + rightNow.get(Calendar.YEAR);
  if (fdate == null) fdate = "" + 1;
  if (tdate == null) tdate = "" + rightNow.get(Calendar.DATE);
  if (fmonth == null) fmonth = "" + rightNow.get(Calendar.MONTH);
  if (tmonth == null) tmonth = "" + rightNow.get(Calendar.MONTH);
  if (fyear == null) fyear = "" + rightNow.get(Calendar.YEAR);
  if (tyear == null) tyear = "" + rightNow.get(Calendar.YEAR);

  if (fdate != null) str = str + "fdate=" + fdate;
  if (tdate != null) str = str + "&tdate=" + tdate;
  if (fmonth != null) str = str + "&fmonth=" + fmonth;
  if (tmonth != null) str = str + "&tmonth=" + tmonth;
  if (fyear != null) str = str + "&fyear=" + fyear;
  if (tyear != null) str = str + "&tyear=" + tyear;
  if (startTime != null) str = str + "&starttime=" + startTime;
  if (endTime != null) str = str + "&endtime=" + endTime;
  if (authCode != null) str = str + "&authCode=" + authCode;
  if (amt != null) str = str + "&amount=" + amt;
  if (trackingid != null) str = str + "&STrackingid=" + trackingid;
  if (paymentid != null) str = str + "&paymentid=" + paymentid;
  if (accountid != null) str = str + "&accountid=" + accountid;
  if (terminalId != null) str = str + "&terminalId=" + terminalId;
  if (transactionType != null) str = str + "&transactionType=" + transactionType;
  if (rrn != null) str = str + "&rrn=" + rrn;
  if (memberid != null) str = str + "&toid=" + memberid;
  if (firstfourofccnum != null) str = str + "&firstfourofccnum=" + firstfourofccnum;
  if (lastfourofccnum != null) str = str + "&lastfourofccnum=" + lastfourofccnum;
  if (mid !=null) str = str + "&mid=" + mid;
  if (refundamount !=null) str = str + "&refundamount=" + refundamount;
  if (isRefund != null) str = str + "&isRefund=" + isRefund;

  int pageno = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
  int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 30);
%>
<html>
<head>
  <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
  <title> Transaction Management> Fraud Alert Transactions</title>
  <script language="javascript">
    function isint(form)
    {
      if (isNaN(form.numrows.value))
        return false;
      else
        return true;
    }
    function validateccnum()
    {

      var firstfourofccnum = document.form.firstfourofccnum.value;
      var lastfourofccnum= document.form.lastfourofccnum.value;
      if(firstfourofccnum.length==0 && lastfourofccnum.length==0 )
        return true;
      if(firstfourofccnum.length<4)
      {
        alert("Enter first four of ccnum");
        return false;
      }

      if( lastfourofccnum.length<4)
      {
        alert("Enter last four of ccnum");
        return false;
      }

    }

    function getExcelFile()
    {
      if(document.getElementById("containrecord"))
      {
        document.exportform.submit();
      }
    }
  </script>
  <script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-account-member.js"></script>
</head>
<body>
<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    try
    {
%>
<form name="form" method="post" action="/icici/servlet/FraudAlertTransactionList?ctoken=<%=ctoken%>" onsubmit="return validateccnum()">
  <input type="hidden" value="<%=ctoken%>" name="ctoken">
  <div class="row" >
    <div class="col-lg-12" style="position:static;">
      <div class="panel panel-default">
        <div class="panel-heading">
          <p>Fraud Alert Transactions Details</p>
        </div>
        <br>
        <%
        String error=(String ) request.getAttribute("errormessage");
        if(error !=null)
        {
        out.println("<center><p class=\"textb\">"+error+"</center>");
        }
        %>
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

                  <td width="5%" class="textb" >Tracking Id</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb">
                    <input type="text" maxlength="500" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>"
                           name="STrackingid" size="10" class="txtbox">
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
                  <td width="4%" class="textb">Payment ID</td>
                  <td width="3%" class="textb" > </td>
                  <td width="12%" class="textb">
                    <input type=text name="paymentid" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(paymentid)%>" class="txtbox" size="50">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Account Id</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="accountid" id="accountid1" value="<%=accountid%>" class="txtbox" autocomplete="on">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Member Id</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="toid" id="memberid1" value="<%=memberid%>" class="txtbox" autocomplete="on">
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
                  <td width="8%" class="textb">Terminal Id</td>
                  <td width="3%" class="textb"></td>
                  <td width="12%" class="textb">
                    <input name="terminalid" id="terminalId" value="<%=terminalId%>" class="txtbox" autocomplete="on">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" colspan="2">Card Number</td>
                  <td width="7%" class="textb">
                    <input type=text  name="firstfourofccnum" maxlength="6" size="5"  class="txtbox" style="width:60px" value="<%=firstfourofccnum%>">
                    <input type=text name="lastfourofccnum" maxlength="4" size="4" class="txtbox" style="width:60px" value="<%=lastfourofccnum%>">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="5%" class="textb" >Amount</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb">
                    <input type=text name="amount" maxlength="10"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(amt)%>" class="txtbox" size="10">
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
                  <td width="4%" class="textb" >Refund Amount</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="12%" class="textb">
                    <input type=text name="refundamount" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundamount)%>" class="txtbox" size="20">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="4%" class="textb">MID</td>
                  <td width="3%" class="textb" > </td>
                  <td width="12%" class="textb">
                    <input type=text name="mid" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(mid)%>" class="txtbox" size="50">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb" >Authorization Code</td>
                  <td width="3%" class="textb">&nbsp;</td>
                  <td width="7%" class="textb">
                    <input type=text name="authCode" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(authCode)%>" class="txtbox" size="10">
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
                  <td width="5%" class="textb">RRN</td>
                  <td width="3%" class="textb" > </td>
                  <td width="7%" class="textb">
                    <input type=text name="rrn" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(rrn)%>" class="txtbox" size="20">
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Is Refund</td>
                  <td width="3%" class="textb"></td>
                  <td width="7%" class="textb">
                    <select size="1" name="isRefund" class="form-control">
                      <%
                        if("Y".equals(isRefund))
                        {%>
                      <option value="Y" SELECTED>Yes</option>
                      <option value="N">No</option>
                      <%}
                      else
                      {%>
                      <option value="N" SELECTED>No</option>
                      <option value="Y">Yes</option>
                      <%}
                      %>
                    </select>
                  </td>

                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Transaction Type</td>
                  <td width="3%" class="textb"></td>
                  <td width="7%" class="textb">
                    <select size="1" name="transactionType" class="form-control">

                      <%
                        if("REFUND".equals(transactionType))
                        {%>
                      <option value="REFUND" SELECTED>REFUND</option>
                      <option value="SALE">SALE</option>
                      <%}
                      else
                      {%>
                      <option value="SALE" SELECTED>SALE</option>
                      <option value="REFUND">REFUND</option>
                      <%}
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
                  <td width="12%" class="textb"></td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </div>
    </div>
  </div>
</form>
<div class="reporttable">
  <%
    Hashtable hash = (Hashtable) request.getAttribute("transactionsdetails");
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
  <form name="exportform" method="post" action="ExportFraudAlertTransactions?ctoken=<%=ctoken%>" >
    <%
      if (!request.getParameter("toid").equalsIgnoreCase("0"))
      {
    %>
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>" name="toid">
    <%
      }
    %>
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(authCode)%>" name="authCode">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(amt)%>" name="amt">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(mid)%>" name="mid">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(rrn)%>" name="rrn">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>" name="trackingId">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(paymentid)%>" name="paymentid">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalId)%>" name="terminalId">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstfourofccnum)%>" name="firstSix">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastfourofccnum)%>" name="lastFour">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(mid)%>" name="mid">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" name="fdate">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" name="tdate">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fmonth)%>" name="fmonth">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tmonth)%>" name="tmonth">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fyear)%>" name="fyear">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tyear)%>" name="tyear">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundamount)%>" name="refundamount">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(startTime)%>" name="starttime">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(endTime)%>" name="endtime">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(accountid)%>" name="accountid">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(paymentid)%>" name="paymentid">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(isRefund)%>" name="isRefund">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(transactionType)%>" name="transactionType">
    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("accountid"))%>" name="accountid">
    <button type="submit" class="button3" style="width:15%;margin-left:85% ;margin-top:0px"><b>Export to excel</b>&nbsp;&nbsp;&nbsp;<img width="20%" height="100%" border="0" src="/merchant/images/excel.png"></button>
  </form>
  <br>
  <div id="containrecord"></div>
  <table border="1" cellpadding="5" cellspacing="0" width="750" align="center" class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
      <td width="7%" class="th0">ID</td>
      <td width="7%" class="th0">Transaction Date</td>
      <td width="7%" class="th0">TIMESTAMP</td>
      <td width="4%" class="th1">Tracking ID</td>
      <td width="4%" class="th1">Payment ID</td>
      <td width="7%" class="th0">Account ID</td>
      <td width="7%" class="th0">Member ID</td>
      <td width="7%" class="th0">Terminal ID</td>
      <td width="7%" class="th1">Amount</td>
      <td width="7%" class="th0">Refund Amount</td>
      <td width="7%" class="th0">Transaction Type</td>
      <td width="7%" class="th0">Card Number</td>
      <td width="7%" class="th0">MID</td>
      <td width="7%" class="th0">Authorization Code</td>
      <td width="7%" class="th0">RRN</td>
      <td width="7%" class="th0">Is Refunded</td>
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
        String transId = (String) temphash.get("id");
        String transactionTime = (String) temphash.get("transactionDate");
        String TIMESTAMP = (String) temphash.get("TIMESTAMP");
        String icicitransid = (String) temphash.get("trackingId");
        String paymentId = (String) temphash.get("paymentid");
        String currency = (String) temphash.get("currency");
        String accountId = (String) temphash.get("accountId");
        String memberId = (String) temphash.get("toid");
        String terminalid = (String) temphash.get("terminalId");
        String IsRefund = (String)temphash.get("isRefunded");
        String amount = currency + " " + temphash.get("amount");
        refundamount = currency + " " + temphash.get("refund_amount");
        String transactiontype=(String)temphash.get("transactionType");
        String cardnumber=(String)temphash.get("personalAccountNumber");
        String MID=(String)temphash.get("merchant_id");
        String autherizationCode=(String)temphash.get("authorization_code");
        String RRN=(String)temphash.get("rrn");
        if(!functions.isValueNull(accountId))
          accountId = "";

        if(!functions.isValueNull(memberId))
          memberId = "";

        if(!functions.isValueNull(terminalid))
          terminalid = "";

        if(!functions.isValueNull(paymentId))
          paymentId = "";

        if(!functions.isValueNull((String) temphash.get("refund_amount")))
          refundamount = "";

        if(!functions.isValueNull(transactiontype))
          transactiontype = "";
        if(!functions.isValueNull(cardnumber))
          cardnumber = "";
        if(!functions.isValueNull(MID))
          MID = "";
        if(!functions.isValueNull(autherizationCode))
          autherizationCode = "";
        if(!functions.isValueNull(RRN))
          RRN = "";


        out.println("<tr " + style + ">");
        /*out.println("<td align=center>" +
                "<form action=\"FraudAlertTransactionList?ctoken="+ctoken+"\" method=\"post\">" +
                "<input type=\"hidden\" name=\"action\" value=\"FraudAlertTransactionList\">" +
                "<input type=\"hidden\" name=\"STrackingid\" value=\""+icicitransid+"\">" +
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
                "<input type=\"hidden\" name=\"isRefund\" value=\""+ isRefund +"\">" +
                "<input type=\"submit\" class=\"goto\" name=\"submit\" value=\""+transId+"\">" +
                "</form>" +
                "</td>");*/
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(transId) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(transactionTime) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(TIMESTAMP) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(icicitransid) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(paymentId) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(accountId) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(memberId) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(terminalid) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(amount) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(refundamount) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(transactiontype) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(cardnumber) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(MID) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(autherizationCode) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(RRN) + "</td>");
        out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(IsRefund) + "</td>");
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
        <jsp:param name="page" value="FraudAlertTransactionList"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
    </td>
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