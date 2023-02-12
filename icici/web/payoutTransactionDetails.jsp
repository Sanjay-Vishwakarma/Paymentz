<%--
  Created by IntelliJ IDEA.
  User: Diksha
  Date: 01-Dec-21
  Time: 5:22 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.directi.pg.core.GatewayAccount,
                 com.directi.pg.core.GatewayAccountService,
                 com.directi.pg.core.GatewayType,
                 com.directi.pg.core.GatewayTypeService,
                 com.payment.exceptionHandler.errorcode.ErrorCodeUtils" %>
<%@ include file="index.jsp" %>
<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName" %>
<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.*" %>
<%@ page import="com.directi.pg.Base64" %>
<%@ page import="com.manager.TransactionManager" %>
<%@ page import="com.payment.safexpay.SafexPayPaymentGateway" %>
<%@ page import="com.payment.Rubixpay.RubixpayPaymentGateway" %>
<%@ page import="com.payment.PZTransactionStatus" %>
<%@ page import="com.payment.verve.VervePaymentGateway" %>
<%@ page import="com.payment.bhartiPay.BhartiPayPaymentGateway" %>
<%@ page import="com.manager.vo.fraudruleconfVOs.RuleMasterVO" %>
<%@ page import="com.payment.qikpay.QikpayPaymentGateway" %>
<%@ page import="com.payment.LetzPay.LetzPayPaymentGateway" %>
<%@ page import="com.payment.asiancheckout.AsianCheckoutPaymentGateway" %>
<%@ page import="com.payment.easypaymentz.EasyPaymentzPaymentGateway" %>
<%@ page import="com.payment.imoneypay.IMoneyPayPaymentGateway" %>
<%@ page import="com.payment.payu.PayUPaymentGateway" %>
<%@ page import="com.payment.payg.PayGPaymentGateway" %>
<%@ page import="com.payment.apexpay.ApexPayPaymentGateway" %>
<%@ page import="com.payment.qikpayv2.QikPayV2PaymentGateway" %>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/cardtype-issuing-bank.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
<style type="text/css">
  #maintitle {
    text-align: center;
    background: #7eccad;
    color: #fff;
    font-size: 14px;
  }

  @media (min-width: 640px) {
    #saveid {
      position: absolute;
      background: #F9F9F9 !important;
    }

    #savetable {
      padding-bottom: 25px;
    }

    table.table {
      margin-bottom: 6px !important;
    }

    table#savetable td:before {
      font-size: inherit;
    }
  }

  table#savetable td:before {
    font-size: 13px;
    font-family: Open Sans;
  }

  table.table {
    margin-bottom: 0px !important;
  }

  #saveid input {
    font-size: 16px;
    padding-right: 30px;
    padding-left: 30px;
  }

  .multiselect {
    width: 100%;

  }

  .selectBox {
    position: relative;
  }

  .selectBox select {
    width: 100%;
    font-weight: bold;
  }

  .overSelect {
    position: absolute;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
  }

  #checkboxes {
    display: none;
    border: 1px #dadada solid;
    width: 80%;
    background-color: #ffffff;
    z-index: 1;
    height: 82px;
    overflow-x: auto;
  }

  #checkboxes label {
    display: block;
  }

  #checkboxes label:hover {
    background-color: #1e90ff;
  }

  #checkboxes_1 {
    display: none;
    border: 1px #dadada solid;
    position: absolute;
    width: 100%;
    background-color: #ffffff;
    z-index: 1;
    height: 130px;
    overflow-x: auto;
  }

  #checkboxes_1 label {
    display: block;
  }

  #checkboxes_1 label:hover {
    background-color: #1e90ff;
  }

  #checkboxes_2 {
    display: none;
    border: 1px #dadada solid;
    position: absolute;
    width: 17%;
    background-color: #ffffff;
    z-index: 1;
    height: 110px;
    overflow-x: auto;
  }

  #checkboxes_2 label {
    display: block;
  }

  #checkboxes_2 label:hover {
    background-color: #1e90ff;
  }


</style>
<%!
  private static Logger log = new Logger("payoutTransactionDetails.jsp");
%>
<%

  Functions functions = new Functions();
  HashMap<String, String> statusFlagHash = new LinkedHashMap();
  HashMap<String, String> statusHash = new LinkedHashMap();
  HashMap<String, String> transactionModeHM = new LinkedHashMap();
  transactionModeHM.put("3Dv1","3Dv1");
  transactionModeHM.put("3Dv2","3Dv2");
  transactionModeHM.put("Non-3D","Non-3D");
  String currency = "";
  String templatecurrency = "";
  String accountId = "";
  statusFlagHash.put("isChargeback", "Chargeback");
  statusFlagHash.put("isFraud", "Fraud");
  statusFlagHash.put("isRefund", "Refund");
  statusFlagHash.put("isRollingReserveKept", "RollingReserveKept");
  statusFlagHash.put("isRollingReserveReleased", "RollingReserveReleased");
  statusFlagHash.put("isSettled", "Settled");
  statusFlagHash.put("isSuccessful", "Successful");
  statusHash.put("authstarted", "Auth Started");
  statusHash.put("authsuccessful", "Auth Successful");
  statusHash.put("authfailed", "Auth Failed");
  statusHash.put("authcancelled", "Authorisation Cancelled");
  statusHash.put("begun", "Begun Processing");
  statusHash.put("cancelstarted", "Cancel Initiated");
  statusHash.put("cancelled", "Cancelled Transaction");
  statusHash.put("capturestarted", "Capture Started");
  statusHash.put("capturesuccess", "Capture Successful");
  statusHash.put("capturefailed", "Capture Failed");
  statusHash.put("chargeback", "Chargeback");
  statusHash.put("chargebackreversed", "Chargeback Reversed");
  statusHash.put("partialrefund", "Partial Refund");
  statusHash.put("payoutfailed", "Payout Failed");
  statusHash.put("payoutstarted", "Payout Started");
  statusHash.put("payoutsuccessful", "Payout Successful");
  statusHash.put("podsent", "POD Sent ");
  statusHash.put("proofrequired", "Proof Required");
  statusHash.put("reversed", "Reversed");
  statusHash.put("markedforreversal", "Reversal Request Sent");
  statusHash.put("settled", "Settled");
  statusHash.put("failed", "Validation Failed");
  statusHash.put("authstarted_3D", "Authstarted 3D");

  String gateway = Functions.checkStringNull(request.getParameter("gateway"));
  String accountid = Functions.checkStringNull(request.getParameter("accountid")) == null ? "" : request.getParameter("accountid");
  String memberid = Functions.checkStringNull(request.getParameter("toid")) == null ? "" : request.getParameter("toid");
  String pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid")) == null ? "" : request.getParameter("pgtypeid");
  String partnerid = request.getParameter("partnerid")==null?"":request.getParameter("partnerid");
  String desc = Functions.checkStringNull(request.getParameter("desc"));
  String amt = Functions.checkStringNull(request.getParameter("amount"));
  String emailaddr = Functions.checkStringNull(request.getParameter("emailaddr"));
  String orderdesc = Functions.checkStringNull(request.getParameter("orderdesc"));
  String name = Functions.checkStringNull(request.getParameter("name"));
  String trackingid = Functions.checkStringNull(request.getParameter("STrackingid"));
  String toid = Functions.checkStringNull(request.getParameter("toid"));
  String refundamount = Functions.checkStringNull(request.getParameter("refundamount"));
  String remark = Functions.checkStringNull(request.getParameter("remark"));
  String cardtype = Functions.checkStringNull(request.getParameter("cardtype"));
  String issuing_bank = Functions.checkStringNull(request.getParameter("issuing_bank"));
  String paymentid    = Functions.checkStringNull(request.getParameter("paymentid"))==null?"":request.getParameter("paymentid");
  String dateType = Functions.checkStringNull(request.getParameter("datetype"));
  String firstfourofccnum = Functions.checkStringNull(request.getParameter("firstfourofccnum"));
  String lastfourofccnum = Functions.checkStringNull(request.getParameter("lastfourofccnum"));
  String startTime = Functions.checkStringNull(request.getParameter("starttime"));
  String endTime = Functions.checkStringNull(request.getParameter("endtime"));
  String flaghash = Functions.checkStringNull(request.getParameter("statusflag"));
  String status = Functions.checkStringNull(request.getParameter("status"));
  String cardpresent = Functions.checkStringNull(request.getParameter("cardpresent"));
  String arn = request.getParameter("ARN");
  String rrn = request.getParameter("RRN");
  String auth = request.getParameter("AUTHORIZATION_CODE");
  String telno   = Functions.checkStringNull(request.getParameter("telno")) == null ? "": request.getParameter("telno");
  String telnocc= Functions.checkStringNull(request.getParameter("telnocc")) == null ? "": request.getParameter("telnocc");
  String statusType = Functions.checkStringNull(request.getParameter("statusType"));
  String customerid = Functions.checkStringNull(request.getParameter("customerid"))== null? "" : request.getParameter("customerid");
  String transactionMode = (String) request.getAttribute("transactionMode");
  String bankacc      = Functions.checkStringNull(request.getParameter("bankaccount"));
  if(!functions.isValueNull(request.getParameter("ARN")))
  {
    arn = "";
  }
  if(!functions.isValueNull(request.getParameter("RRN")))
  {
    rrn = "";
  }
  if(!functions.isValueNull(request.getParameter("AUTHORIZATION_CODE")))
  {
    auth = "";
  }

  boolean archive = Boolean.valueOf(request.getParameter("archive")).booleanValue();
  String transactionStatus="";
  String transactionGateway="";

  String str = "";
  String selectedArchived = "", selectedCurrent = "";
  String archivalString = "Archived";
  String currentString = "Current";

  if (gateway == null) gateway = "";
  if (desc == null) desc = "";
  if (amt == null) amt = "";
  if (emailaddr == null) emailaddr = "";
  if (orderdesc == null) orderdesc = "";
  if (name == null) name = "";
  if (trackingid == null) trackingid = "";
  if (toid == null) toid = "";
  if (refundamount == null) refundamount = "";
  if (remark == null) remark = "";
  if (cardtype == null) cardtype = "";
  if (issuing_bank == null) issuing_bank = "";
  if (dateType == null) dateType = "";
  if (firstfourofccnum == null) firstfourofccnum = "";
  if (lastfourofccnum == null) lastfourofccnum = "";
  if (telnocc == null)telnocc= "";
  if (telno == null) telno= "";
  if (partnerid == null)partnerid = "";
  if (bankacc == null) bankacc ="";
  if (startTime == null) startTime = "00:00:00";
  if (endTime == null) endTime = "23:59:59";
  if (flaghash == null)
  {
    flaghash = "";
  }
  if (status == null)
  {
    status = "";
  }

  if (archive)
  {
    selectedCurrent = "";
  }
  else
  {
    selectedCurrent = "selected";
  }

  String fdate = null;
  String tdate = null;
  String fmonth = null;
  String tmonth = null;
  String fyear = null;
  String tyear = null;

  try
  {
    fdate = ESAPI.validator().getValidInput("fdate", request.getParameter("fdate"), "Days", 2, true);
    tdate = ESAPI.validator().getValidInput("tdate", request.getParameter("tdate"), "Days", 2, true);
    fmonth = ESAPI.validator().getValidInput("fmonth", request.getParameter("fmonth"), "Months", 2, true);
    tmonth = ESAPI.validator().getValidInput("tmonth", request.getParameter("tmonth"), "Months", 2, true);
    fyear = ESAPI.validator().getValidInput("fyear", request.getParameter("fyear"), "Years", 4, true);
    tyear = ESAPI.validator().getValidInput("tyear", request.getParameter("tyear"), "Years", 4, true);
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
  if (desc != null) str = str + "&desc=" + desc;
  if (amt != null) str = str + "&amount=" + amt;
  if (emailaddr != null) str = str + "&emailaddr=" + emailaddr;
  if (name != null) str = str + "&name=" + name;
  if (trackingid != null) str = str + "&STrackingid=" + trackingid;
  if (orderdesc != null) str = str + "&orderdesc=" + orderdesc;
  if (toid != null) str = str + "&toid=" + toid;
  if (status != null) str = str + "&status=" + status;
  if (firstfourofccnum != null) str = str + "&firstfourofccnum=" + firstfourofccnum;
  if (lastfourofccnum != null) str = str + "&lastfourofccnum=" + lastfourofccnum;
  if (gateway != null) str = str + "&gateway=" + gateway;
  if (remark != null) str = str + "&remark=" + remark;
  if (cardtype != null) str = str + "&cardtype=" + cardtype;
  if (issuing_bank != null) str = str + "&issuing_bank=" + issuing_bank;
  if (flaghash != null) str = str + "&statusflag=" + flaghash;
  if (pgtypeid != null) str = str + "&pgtypeid=" + pgtypeid;
  if (accountid != null) str = str + "&accountid=" + accountid;
  if (dateType != null) str = str + "&datetype=" + accountid;
  if (arn != null) str = str + "&ARN=" + arn;
  if (rrn != null) str = str + "&RRN=" + rrn;
  if (auth != null) str = str + "&AUTHORIZATION_CODE=" + auth;
  if (cardpresent != null) str = str + "&cardpresent=" + cardpresent;
  if (customerid != null) str = str + "&customerid=" + customerid;
  if (transactionMode != null) str = str + "&transactionMode=" + transactionMode;
  if (telno != null) str = str+ "&telno=" + telno;
  if (telnocc != null) str= str+ "&telnocc=" +telnocc;
  if (partnerid != null) str = str + "&partnerid=" + partnerid;
  if (bankacc != null) str = str + "&bankacc=" + bankacc;


  firstfourofccnum = null;
  lastfourofccnum = null;

  str = str + "&archive=" + archive;

  int pageno = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
  int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 30);

  TreeMap<String, GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();
  TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();

%>
<html>
<head>
  <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
  <title>Transactions List</title>
  <script language="javascript">
    var expanded2 = false;
    function showaccountcheckbox(event)
    {
      event.stopPropagation();
      var checkboxes = document.getElementById("checkboxes_2");
      if (!expanded2) {
        checkboxes.style.display = "block";
        expanded2 = true;
      } else {
        checkboxes.style.display = "none";
        expanded2 = false;
      }
    }
    if (expanded2)
    {
      var checkboxes = document.getElementById("checkboxes_2");
      checkboxes.style.display = "none";
      expanded2 = false;
    }


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
      var lastfourofccnum = document.form.lastfourofccnum.value;
      if (firstfourofccnum.length == 0 && lastfourofccnum.length == 0)
        return true;
      if (firstfourofccnum.length < 4)
      {
        alert("Enter first four of ccnum");
        return false;
      }

      if (lastfourofccnum.length < 4)
      {
        alert("Enter last four of ccnum");
        return false;
      }

    }

    function getExcelFile()
    {
      if (document.getElementById("containrecord"))
      {
        document.exportform.submit();
      }
    }
  </script>
  <script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-account-member.js"></script>
</head>
<body>
<%
  ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    try
    {
%>
<form name="form" method="post" action="/icici/servlet/PayoutTransactionDetails?ctoken=<%=ctoken%>"
      onsubmit="return validateccnum()">

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
                        <input name="email" id="email" value="<%=emailaddr%>" class="txtbox" autocomplete="on">
                      </td>

                      <td width="4%" class="textb">&nbsp;</td>
                      <td width="8%" class="textb">Tracking ID</td>
                      <td width="3%" class="textb"></td>
                      <td width="12%" class="textb">
                        <input name="trackingid" id="trackingid" value="<%=trackingid%>" class="txtbox" autocomplete="on">
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
<%
  Hashtable hash = (Hashtable) request.getAttribute("transactionsdetails");
  System.out.println("hash==========>"+ hash);
  Hashtable innerhash = null;
  String CP ="";
  if (hash != null && hash.size() > 0)
  {
    CP = hash.get("CP")!=null?(String)hash.get("CP"):"";
    Set<String> actionEntrySet = hash.keySet();

    String style = "class=tr0";

    innerhash = (Hashtable) hash.get(1 + "");
    int pos = 0;
    value = (String) innerhash.get("Tracking ID");
    log.debug("Inher Hash==========="+value);
    log.debug("Inher Hash==========="+innerhash.get("Tracking ID"));
%>
<div class="reporttable">
  <table border="1" cellpadding="5" cellspacing="0" bordercolor="#ffffff" align="center"
         class="table table-striped table-bordered table-hover table-green dataTable">
    <thead>
    <tr>
      <td width="5%" class="th0" colspan="7">Transaction Status Flag</td>
    </tr>
    <tr>
      <td width="5%" class="th0">isSuccessful</td>
      <td width="3%" class="th0">isSettled</td>
      <td width="3%" class="th0">isRefund</td>
      <td width="3%" class="th0">isChargeback</td>
      <td width="5%" class="th0">isFraud</td>
      <td width="5%" class="th0">isRollingReserveKept</td>
      <td width="5%" class="th0">isRollingReserveReleased</td>
    </tr>
    </thead>
    <tr>
      <% if (CP.equals("CP")){
        //System.out.println("inside if"+CP);
      %>
      <td align="center" <%=style%>> Y </td>
      <td align="center" <%=style%>> N </td>
      <td align="center" <%=style%>> N </td>
      <td align="center" <%=style%>> N </td>
      <td align="center" <%=style%>> N </td>
      <td align="center" <%=style%>> N </td>
      <td align="center" <%=style%>> Y </td>
      <% }else{
        //System.out.println("inside else"+CP);
      %>
      <td align="center" <%=style%>><%=getValue((String) innerhash.get("isSuccessful"))%>
      </td>
      <td align="center" <%=style%>><%=getValue((String) innerhash.get("isSettled"))%>
      </td>
      <td align="center" <%=style%>><%=getValue((String) innerhash.get("isRefund"))%>
      </td>
      <td align="center" <%=style%>><%=getValue((String) innerhash.get("isChargeback"))%>
      </td>
      <td align="center" <%=style%>><%=getValue((String) innerhash.get("isFraud"))%>
      </td>
      <td align="center" <%=style%>><%=getValue((String) innerhash.get("isRollingReserveKept"))%>
      </td>
      <td align="center" <%=style%>><%=getValue((String) innerhash.get("isRollingReserveReleased"))%>
      </td>
      <%}%>
    </tr>
  </table>
</div>
<div style="margin-left: -3px;margin-right: -3px;">
    <% String message=(String) request.getAttribute("message");%>
  <div class="col-sm-12" style="font-size:15px;">
    <%=message%>
  </div>
  <div class="col-lg-2 col-sm-6" style="width: 25%;margin-left: 245px;margin-top:20px">
    <table border="1" cellpadding="5" cellspacing="0" style="" align="center"
           class="table table-striped table-bordered table-green dataTable">
      <tr <%=style%>  >
        <td class="th0" colspan="3" align="center">Transaction Details</td>
      </tr>
      <tr <%=style%>  >
        <td class="td0" width="10%">Tracking ID:</td>
        <td class="td0" width="10%" colspan="2"><%=value%>
        </td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Description");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">Description:</td>
        <td class="td0" colspan="2"><%=value%>
        </td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Order Description");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">Order Description:</td>
        <td class="td0" colspan="2"><%=value%>
        </td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("paymentid");
        if (value == null || value.equals("null") || value.equals(""))
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">Payment ID:</td>
        <td class="td0" colspan="2"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>
      <% pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        templatecurrency = (String) innerhash.get("templatecurrency");
        if (templatecurrency == null)
          templatecurrency = "";

        accountId = (String) innerhash.get("accountid");
                /*if (accountId != null)
                {
                    GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                    currency = account.getCurrency();
                }*/
        value = (String) innerhash.get("Template Amount");
        if (value == null)
          value = "-";
        else
          value = templatecurrency + " " + value;

      %>
      <tr <%=style%> >
        <td class="td0">Template Amount:</td>
        <td class="td0" colspan="2"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>


      <% pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        currency = (String) innerhash.get("currency");

        accountId = (String) innerhash.get("accountid");
                /*if (accountId != null)
                {
                    GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                    currency = account.getCurrency();
                }*/
        value = (String) innerhash.get("Transaction Amount");
        if (value == null)
          value = "-";
        else
          value = currency + " " + value;

      %>
      <tr <%=style%> >
        <td class="td0">Transaction Amount:</td>
        <td class="td0" colspan="2"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Captured Amount");
        if (value == null)
          value = "-";
        else
          value = currency + " " + value;


      %>
      <tr <%=style%> >
        <td class="td0">Captured Amount:</td>
        <td class="td0" colspan="2"><%=value%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Refund Amount");
        if (value == null)
          value = "-";
        else
          value = currency + " " + value;


      %>
      <tr <%=style%> >
        <td class="td0">Refund Amount:</td>
        <td class="td0" colspan="2"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Payout Amount");
        if (value == null)
          value = "-";
        else
          value = currency + " " + value;


      %>
      <tr <%=style%> >
        <td class="td0">Payout Amount:</td>
        <td class="td0" colspan="2"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Chargeback Amount");
        if (value == null)
          value = "-";
        else
          value = currency + " " + value;


      %>
      <tr <%=style%> >
        <td class="td0">Chargeback Amount:</td>
        <td class="td0" colspan="2"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Date of transaction");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">Date of transaction:</td>
        <td class="td0" colspan="2"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Last update");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">Last update:</td>
        <td class="td0" colspan="2"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Status");
        transactionStatus=value;
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">Status:</td>
        <td class="td0" colspan="2"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Remark");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">Status Detail:</td>
        <td class="td0" colspan="2"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Auth Response Description");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">Auth Response Description:</td>
        <td class="td0" colspan="2"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("accountid")+"-"+innerhash.get("fromtype");
        transactionGateway= (String) innerhash.get("fromtype");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">Account Id:</td>
        <td class="td0" colspan="2"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        String newterminal="";
        String terminalid =(String)innerhash.get("terminalid");
        TransactionManager transactionManager=new TransactionManager();
        if(functions.isValueNull(terminalid)){
          newterminal=terminalid;
        }
        else{
          String accountId1 = (String) innerhash.get("accountid");
          String memberId=(String)innerhash.get("memberid");
          newterminal=transactionManager.getTerminalId(accountId1,memberId);
        }
        if (newterminal == null)
          newterminal = "-";
      %>
      <tr <%=style%> >
        <td class="td0">Terminal Id:</td>
        <td class="td0" colspan="2"><%=ESAPI.encoder().encodeForHTML(newterminal)%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("arn");
        if (value == null)
          value = "-";


      %>

      <tr <%=style%> >
        <td class="td0">ARN:</td>
        <td class="td0" colspan="2"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>


      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("rrn");
        if (value == null)
          value = "-";


      %>

      <tr <%=style%> >
        <td class="td0">RRN:</td>
        <td class="td0" colspan="2"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("authorization_code");
        if (value == null)
          value = "-";


      %>

      <tr <%=style%> >
        <td class="td0">AuthCode:</td>
        <td class="td0" colspan="2"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("chargebackinfo");
        if (value == null)
          value = "-";
      %>

      <tr <%=style%> >
        <td class="td0">Chargeback Info:</td>
        <td class="td0" colspan="2"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("notificationUrl");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">Notification Url:</td>
        <td class="td0" style="width: 30%;">
          <%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
        <%if(value!="" && value!="-" && !PZTransactionStatus.AUTH_STARTED.toString().equalsIgnoreCase(transactionStatus) && !PZTransactionStatus.BEGUN.toString().equalsIgnoreCase(transactionStatus) && !PZTransactionStatus.PAYOUT_STARTED.toString().equalsIgnoreCase(transactionStatus)){%>
        <td class="td0" align="center" style="width:3%;">
          <form action="/icici/servlet/SendNotification?ctoken=<%=ctoken%>" method="post">
            <input type="hidden" name="STrackingid" value="<%=trackingid%>">
            <input type="hidden" name="accountid" value="<%=accountid%>">
            <input type="hidden" name="action" value="TransactionDetails">
            <input type="hidden" name="cardpresent" value="">
            <button type="submit" class="buttonform" style="font-size:11px;width:60px;">
              Resend
            </button>
          </form>
        </td>
        <%}%>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("machineid");
        if (value == null)
          value = "-";
      %>

      <tr <%=style%> >
        <td class="td0">Machine Id:</td>
        <td class="td0" colspan="2"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%

        if (functions.isValueNull((String) innerhash.get("emi")))
        {
          pos++;
          style = "class=\"tr" + pos % 2 + "\"";
          value = (String) innerhash.get("emi");
          if (value == null)
            value = "-";

      %>
      <tr <%=style%> >
        <td class="td0">Installment:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>
      <%
        }
      %>


    </table>
  </div>
  <div class="col-lg-2 col-sm-6" style="width: 27%;margin-top:20px">
    <table border="1" cellpadding="5" cellspacing="0" align="center"
           class="table table-striped table-bordered table-green dataTable">
      <tr <%=style%>  >
        <td class="th0" align="center" colspan="2">Customer's Details</td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Cardholder_Name");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0" width="10%">Cardholder's Name:</td>
        <td class="td0" width="10%"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("ccnumber");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">Card Number:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("ECI");

        if (value == null || value.equalsIgnoreCase("null"))
          value = "-";
      %>
      <tr <%=style%> >
        <td class="td0">ECI:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("subcard_type");

        if (value == null)
          value = "-";
      %>

      <tr <%=style%> >
        <td class="td0">Subcard Type:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>


      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("CustomerId");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">CustomerId:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%
        pos++;
        style="class=\"tr"+pos % 2 + "\"";
        value=(String) innerhash.get("walletId");
        if(!functions.isValueNull(value))
          value="-";
      %>
      <tr <%=style%> >
        <td class="td0">WalletId:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Customer's Emailaddress");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">Customer's Emailaddress:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("City");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">City:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Street");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">Street:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("State");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">State:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Country");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">Country:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("HistoryIpAddress");
        String customerIpCountry="-";
        if (value == null)
        {
          value = "-";
        }else
        {
          customerIpCountry= functions.getIPCountryLong(value);
          if (!functions.isValueNull(customerIpCountry))
            customerIpCountry = "-";
        }
      %>
      <tr <%=style%> >
        <td class="td0">Customer IP Address:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>
      <tr <%=style%> >
        <td class="td0">Customer IP Country:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(customerIpCountry)%>
        </td>
      </tr>
      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        String telephoneCode      = (String) innerhash.get("telephoneCode");
        String customerPhoneNo  = (String) innerhash.get("customerPhoneNo");
        if (functions.isEmptyOrNull(telephoneCode)){
          telephoneCode = "";
        }else{
          telephoneCode = telephoneCode + "-";
        }
        if (functions.isEmptyOrNull(customerPhoneNo)){
          customerPhoneNo = "";
          telephoneCode     = "";
        }
        value = telephoneCode + customerPhoneNo;
        if (functions.isEmptyOrNull(value))
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">Customer Phone Number:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

    </table>
    <br><br>
  </div>
  <div class="col-lg-2 col-sm-6" style="width: 27%;margin-top:20px">
    <table border="1" cellpadding="5" cellspacing="0" align="center"
           class="table table-striped table-bordered table-green dataTable">
      <tr <%=style%>  >
        <td class="th0" align="center" colspan="2">Merchant's Details</td>
      </tr>

      <tr <%=style%> >
        <td class="td0" width="7%">Member ID:</td>
        <td class="td0" width="5%"><%=(String) innerhash.get("memberid")%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Name of Merchant");
        if (value == null)
          value = "-";

      %>

      <tr <%=style%> >
        <td class="td0">Name of Merchant:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%>
          <%--<td class="td0" ><%=(String) innerhash.get("Name of Merchant")%>--%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Contact person");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">Contact person:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Merchant's Emailaddress");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">Merchant's Emailaddress:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>


      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Site URL");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">Site URL:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>


      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Merchant's telephone Number");
        if (value == null)
          value = "-";


      %>
      <tr <%=style%> >
        <td class="td0">Merchant's Telephone Number:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("ipaddress");
        String merchantIpCountry="-";
        if (value == null)
        {
          value = "-";
        }else
        {
          merchantIpCountry= functions.getIPCountryLong(value);
          if (!functions.isValueNull(merchantIpCountry))
            merchantIpCountry = "-";
        }


      %>
      <tr <%=style%> >
        <td class="td0">Merchant IP Address:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(value)%>
        </td>
      </tr>
      <tr <%=style%> >
        <td class="td0">Merchant IP Country:</td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(merchantIpCountry)%>
        </td>
      </tr>

    </table>
  </div>
    <%
    Hashtable hash_pay          = (Hashtable) request.getAttribute("transpayoutdetails");
    Hashtable innerhash_pay     = null;

         if (hash_pay != null && hash_pay.size() > 0)
        {
            innerhash_pay   = (Hashtable) hash_pay.get(1 + "");

        }

        if(transactionStatus.equalsIgnoreCase("payoutfailed") || transactionStatus.equalsIgnoreCase("payoutstarted") || transactionStatus.equalsIgnoreCase("payoutsuccessful"))
        {
%>

  <div class="col-lg-2 col-sm-6" style="width: 27%;margin-top:20px">
    <table border="1" cellpadding="5" cellspacing="0" align="center"
           class="table table-striped table-bordered table-green dataTable">


      <tr <%=style%>  >
        <td class="th0" align="center" colspan="2">Customer's Bank Details</td>
      </tr>
      <%
        if(innerhash_pay != null){

          value= (String)innerhash_pay.get("fullname");
        }else{
          value = "-";
        }

                    /*if (value == null)
                        value = "-";*/
      %>
      <tr <%=style%> >
        <td class="td0" width="7%">Beneficiary name</td>
        <td class="td0" width="5%"><%=ESAPI.encoder().encodeForHTMLAttribute(value)%></td>
      </tr>

      <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        if(innerhash_pay != null){
          value= (String)innerhash_pay.get("bankaccount");
        }else{
          value = "-";
        }

                    /*if (value == null)
                        value = "-";*/
      %>
      <tr <%=style%>>
        <td class="td0" width="7%">Beneficiary Account Number</td>
        <td class="td0" width="5%"><%=ESAPI.encoder().encodeForHTMLAttribute(value)%></td>
      </tr>
      <%
        pos++;
        style   = "class=\"tr" + pos % 2 + "\"";
        if(innerhash_pay != null){
          value= (String)innerhash_pay.get("ifsc");
        }else{
          value = "-";
        }

                    /*if (value == null)
                        value = "-";*/
      %>
      <tr <%=style%>>
        <td class="td0" width="7%">Bank IFSC code:</td>
        <td class="td0" width="5%"><%=ESAPI.encoder().encodeForHTMLAttribute(value)%></td>
      </tr>
      <%
        pos++;
        style= "class=\"tr" + pos % 2 + "\"";
        if (innerhash_pay!= null && ((String)innerhash_pay.get("transferType")!= null))
        {
          value= (String)innerhash_pay.get("transferType");
        }
        else
        {
          value="-";
        }
      %>
      <tr <%=style%>>
        <td class="td0" width="7%">Bank Transfer Type:</td>
        <td class="td0" width="5%"><%=ESAPI.encoder().encodeForHTMLAttribute(value)%></td>
      </tr>
    </table>
  </div>
    <%
    }

%>
  <div class="reporttable">
    <div style="overflow-x:scroll;overflow-y: hidden;width: 100%;">
      <table border="1" cellpadding="5" cellspacing="0" bordercolor="#ffffff" align="center"
             class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
          <td width="7%" class="th0" colspan="22">Transaction Action History</td>
        </tr>
        <tr>
          <td width="3%" class="th0">Sr no</td>
          <td width="3%" class="th0">Action</td>
          <td width="3%" class="th0">BillingDescriptor</td>
          <td width="3%" class="th0">Remark</td>
          <td width="5%" class="th0">Timestamp</td>
          <td width="3%" class="th0">Customer&nbsp;<br>IpAddress</td>
          <td width="5%" class="th0">Amount</td>
          <td width="5%" class="th0">Currency</td>
          <td width="3%" class="th0">Response&nbsp;<br>Transactionid</td>
          <td width="5%" class="th0">ActionEx&nbsp;Id</td>
          <td width="5%" class="th0">ActionEx&nbsp;Name</td>
          <td width="3%" class="th0">Bank&nbsp;Response&nbsp;Code</td>
          <td width="3%" class="th0">Response<br>Description</td>
          <td width="3%" class="th0">Response<br>Time</td>
          <td width="3%" class="th0">ARN</td>
          <%
            if (innerhash.get("CardType").equals("22"))
            {//needed for neteller
          %>
          <td width="3%" class="th0">Verification&nbsp;Code</td>
          <%
            }
          %>
          <td width="3%" class="th0">PZ Error&nbsp;Code</td>
          <td width="3%" class="th0">PZ Error&nbsp;Description</td>
          <td width="3%" class="th0">TMPL<br>Amount</td>
          <td width="3%" class="th0" style="padding: 2px;">TMPL<br>Currency</td>
          <td width="3%" class="th0">Wallet<br>Amount</td>
          <td width="3%" class="th0">Wallet<br>Currency</td>

        </tr>
        </thead>
        <%
          log.debug("outerHash:::::::::" + hash);
          pos = 1;

          ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
          ErrorCodeVO errorCodeVO = null;
          byte[] transactionReceiptImg=null;
          int size=hash.size();
          for(int i=size;i>0;i--)/*{}
                for (String key1 : actionEntrySet)*/
          {
            log.debug("key::::" + i);
            style = "class=\"tr" + pos % 2 + "\"";
            innerhash = (Hashtable) hash.get(String.valueOf(i));
            log.debug("innerHash::::::::" + innerhash);
            String errorName = (String) innerhash.get("Error Name");
            String errorCode = "-";
            String errorDescription = "-";
            if (functions.isValueNull((String) innerhash.get("Error Name")))
            {
              errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.valueOf(errorName));
              errorCode = errorCodeVO.getApiCode();
              errorDescription = errorCodeVO.getApiDescription();
            }
            String HistoryResponseTransId = "";
            if (functions.isValueNull((String) innerhash.get("HistoryResponseTransId")))
            {
              HistoryResponseTransId = (String) innerhash.get("HistoryResponseTransId");
            }
            else
            {
              HistoryResponseTransId = "";
            }
            if (innerhash.get("transactionReceiptImg") != null)
            {
              transactionReceiptImg = (byte[]) innerhash.get("transactionReceiptImg");
            }
        %>
        <tr <%=style%> >
          <td class="td0"><%=pos%>
          </td>
          <td class="td0"><%=innerhash.containsKey("HistoryAction") ? innerhash.get("HistoryAction") : ""%>
          </td>
          <td class="td0"><%=innerhash.containsKey("Historyresponsedescriptor") ? innerhash.get("Historyresponsedescriptor") : ""%>
          </td>
          <td class="td0"><%=innerhash.containsKey("HistoryRemark") ? innerhash.get("HistoryRemark") : ""%>
          </td>
          <td class="td0"><%=innerhash.containsKey("HistoryTimeStamp") ? innerhash.get("HistoryTimeStamp") : ""%>
          </td>
          <td class="td0"><%=innerhash.containsKey("HistoryIpAddress") ? innerhash.get("HistoryIpAddress") : ""%>
          </td>
          <td class="td0"><%=innerhash.containsKey("HistoryAmount") ? innerhash.get("HistoryAmount") : ""%>
          </td>
          <td class="td0"><%=innerhash.containsKey("HistoryCurrency") ? innerhash.get("HistoryCurrency") : ""%>
          </td>
          <td class="td0"><%=HistoryResponseTransId%>
          </td>
          <td class="td0"><%=innerhash.containsKey("HistoryExecutorId") ? innerhash.get("HistoryExecutorId") : ""%>
          </td>
          <td class="td0"><%=innerhash.containsKey("HistoryExecutorName") ? innerhash.get("HistoryExecutorName") : ""%>
          </td>
          <td class="td0"><%=innerhash.containsKey("HistoryResponseCode") ? innerhash.get("HistoryResponseCode") : ""%>
          </td>
          <td class="td0"><%=innerhash.containsKey("HistoryResponseDesc") ? innerhash.get("HistoryResponseDesc") : ""%>
          </td>
          <td class="td0"><%=innerhash.containsKey("ResponseTime") ? innerhash.get("ResponseTime") : ""%>
          </td>
          <td class="td0"><%=innerhash.containsKey("arn") ? innerhash.get("arn") : ""%>
          </td>
          <%
            if (innerhash.get("CardType").equals("22"))
            {//needed for neteller
          %>
          <td class="td0"><%=innerhash.containsKey("responsehashinfo") ? innerhash.get("responsehashinfo") : ""%>
          </td>
          <%
            }
          %>
          <td class="td0"><%=errorCode%>
          </td>
          <td class="td0"><%=errorDescription%>
          </td>
          <td class="td0"><%=innerhash.containsKey("Historytemplateamount") ? innerhash.get("Historytemplateamount") : ""%>
          </td>
          <td class="td0"><%=innerhash.containsKey("Historytemplatecurrency") ? innerhash.get("Historytemplatecurrency") : ""%>
          </td>
          <td class="td0"><%=innerhash.containsKey("walletAmount") ? innerhash.get("walletAmount") : ""%>
          </td>
          <td class="td0"><%=innerhash.containsKey("walletCurrency") ? innerhash.get("walletCurrency") : ""%>
          </td>

        </tr>
        <%
            pos++;
          }
        %>
      </table>
    </div>

    <% if ((QikPayV2PaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(transactionGateway) || ApexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(transactionGateway) || PayGPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(transactionGateway) || PayUPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(transactionGateway) || IMoneyPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(transactionGateway) ||  AsianCheckoutPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(transactionGateway) || EasyPaymentzPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(transactionGateway)  || QikpayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(transactionGateway)||BhartiPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(transactionGateway)||VervePaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(transactionGateway)||SafexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(transactionGateway)|| RubixpayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(transactionGateway) || LetzPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(transactionGateway))
            &&("payoutstarted".equalsIgnoreCase(transactionStatus)||"payoutfailed".equalsIgnoreCase(transactionStatus)||"authstarted".equalsIgnoreCase(transactionStatus)||("authfailed".equalsIgnoreCase(transactionStatus)))){
    %>
    </br>
    <div align="center">
      <form action="/icici/servlet/TransactionDetailsInquiryServlet?ctoken=<%=ctoken%>" method="post" >
        <input type="hidden" name="trackingid" value="<%=trackingid%>">
        <input type="hidden" name="status" value="<%=transactionStatus%>">
        <input type="hidden" name="toid" value="<%=toid%>">
        <input type="hidden" name="gateway" value="<%=transactionGateway%>">

        <button type="submit" class="buttonform">
          InquiryStatus
        </button>
      </form>
    </div><%}%>
  </div>



  <div class="reporttable">
    <table border="1" cellpadding="5" cellspacing="0" bordercolor="#ffffff" align="center"
           class="table table-striped table-bordered table-hover table-green dataTable">
      <thead>
      <tr>
        <td width="7%" class="th0" colspan="8">Bin Details</td>
      </tr>
      <tr>
        <%--<td width="3%" class="th0">Sr no</td>--%>
        <td width="10%" class="th0">Issuing Bank Name</td>
        <td width="10%" class="th0">Bin Card Category</td>
        <td width="10%" class="th0">Bin Card Type</td>
        <td width="5%" class="th0">Bin Brand</td>
        <td width="5%" class="th0">Bin Trans Type</td>
        <td width="10%" class="th0">Bin Usage Type</td>
        <td width="5%" class="th0">ISO Country</td>
      </tr>
      </thead>
      <%
        pos = 1;
        for (String key1 : actionEntrySet)
        {
          style = "class=\"tr" + pos % 2 + "\"";
          innerhash = (Hashtable) hash.get(key1);
          if (key1.equals("1"))
          {

      %>
      <tr <%=style%> align="center">
        <%--<td class="td0"><%=pos%>--%>
        </td>
        <td class="td0"><%=innerhash.containsKey("issuing_bank") ? innerhash.get("issuing_bank") : ""%>
        </td>
        <td class="td0"><%=innerhash.containsKey("bin_card_category") ? innerhash.get("bin_card_category") : ""%>
        </td>
        <td class="td0"><%=innerhash.containsKey("bin_card_type") ? innerhash.get("bin_card_type") : ""%>
        </td>
        <td class="td0"><%=innerhash.containsKey("bin_brand") ? innerhash.get("bin_brand") : ""%>
        </td>
        <td class="td0"><%=innerhash.containsKey("bin_trans_type") ? innerhash.get("bin_trans_type") : ""%>
        </td>
        <td class="td0"><%=innerhash.containsKey("bin_usage_type") ? innerhash.get("bin_usage_type") : ""%>
        </td>
        <td class="td0"><%=innerhash.containsKey("country_name") ? innerhash.get("country_name") : ""%>
        </td>
      </tr>
      <%
            pos++;
          }
        }
      %>
    </table>
  </div>
  <div class="reporttable">
    <table border="1" cellpadding="5" cellspacing="0" bordercolor="#ffffff" align="center"
           class="table table-striped table-bordered table-hover table-green dataTable">
      <thead>
      <tr>
        <td width="7%" class="th0" colspan="8">Fraud Rules Triggered</td>
      </tr>
      <tr>
        <%--<td width="3%" class="th0">Sr no</td>--%>
        <td width="10%" class="th0">Fraud Rule Name</td>
        <td width="10%" class="th0">Status</td>
        <td width="10%" class="th0">Fraud Rule Score</td>
      </tr>
      </thead>
      <%
        List<RuleMasterVO> ruleMasterVOList = (List<RuleMasterVO>) request.getAttribute("transactionDetailsFraud");
        if (ruleMasterVOList != null && ruleMasterVOList.size() > 0)
        {
          for (RuleMasterVO ruleMasterVO : ruleMasterVOList)
          {
            String defaultStatus = "";
            if (functions.isValueNull(ruleMasterVO.getDefaultStatus()))
            {
              defaultStatus = ruleMasterVO.getDefaultStatus();
            }
            if (defaultStatus.equals("0"))
            {
              defaultStatus = "Pass";
            }
            else if (defaultStatus.equals("1"))
            {
              defaultStatus = "Fail";
            }
            else
            {
              defaultStatus = "-";
            }
      %>
      <tr <%=style%> align="center">
        <%--<td class="td0"><%=pos%>--%>
        </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(ruleMasterVO.getRuleName())%>
        </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(defaultStatus)%>
        </td>
        <td class="td0"><%=ESAPI.encoder().encodeForHTML(ruleMasterVO.getDefaultScore())%>
        </td>
      </tr>
      <%
        }
      }
      else
      {
      %>
      <tr>
        <td colspan="3" class="td0" align="center">No rules triggered for this
          transaction
        </td>
      </tr>

      <%
        }
      %>
    </table>
  </div>
    <%if(transactionReceiptImg != null){%>
  <div class="reporttable">
    <table border="1" cellpadding="5" cellspacing="0" bordercolor="#ffffff" align="center"
           class="table table-striped table-bordered table-hover table-green dataTable" style="height: 100%;">
      <thead>
      <tr>
        <td width="7%" class="th0" colspan="8">Transaction Receipt</td>
      </tr>
      </thead>
      <tbody style="height:100%;">
      <tr <%=style%> align="center" style="height:100%;">
        <td  colspan="8">
          <div style="height:100%;">
            <%
              String base64Value= Base64.encode(transactionReceiptImg);
            %>
            <img alt="img" src="data:image/jpeg;base64,<%=base64Value%>" style="height: 100%"/>
          </div>
        </td>
      </tr>
      </tbody>
    </table>
  </div>
    <%
                }
            }
            else
            {
                out.println("<div class=\"reporttable\">");
                out.println(Functions.NewShowConfirmation("Sorry", "No records found for Tracking Id :" + trackingid));
                out.println("</div>");
            }

        }
        catch (Exception e)
        {
            log.error("Exception while getting transaction details", e);

        }
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</body>
</html>
<%!
  public String getValue(String data)
  {
    String data1 = "-";
    Functions functions = new Functions();
    if (functions.isValueNull(data))
    {
      data1 = data;
    }
    return data1;
  }
%>`