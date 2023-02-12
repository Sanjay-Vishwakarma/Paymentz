<%--
  Created by IntelliJ IDEA.
  User: Sanjay
  Date: 1/4/2022
  Time: 6:49 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.manager.vo.fraudruleconfVOs.RuleMasterVO" %>
<%@ page import="com.payment.exceptionHandler.errorcode.ErrorCodeUtils" %>
<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName" %>
<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.directi.pg.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="com.directi.pg.Base64" %>
<%@ include file="Top.jsp" %>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  private static Logger log = new Logger("payoutTransactionDetails.jsp");
%>
<%
  String uId = "";
  if(session.getAttribute("role").equals("submerchant"))
  {
    uId = (String) session.getAttribute("userid");
  }
  else
  {
    uId = (String) session.getAttribute("merchantid");
  }

  String  company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
  String bank = (String) session.getAttribute("bank");
  Hashtable transactionDetails = (Hashtable) request.getAttribute("transactionsDetails");
  Hashtable hashpayout = (Hashtable)request.getAttribute("hashpayout");
  Hashtable childhash=(Hashtable) request.getAttribute("childhash");
  String userTerminals = Functions.checkStringNull(request.getParameter("terminalbuffer"))==null?"":request.getParameter("terminalbuffer");

  TransactionEntry transactionentry = (TransactionEntry) session.getAttribute("transactionentry");
  SortedMap statushash = transactionentry.getSortedMap();
  String desc = Functions.checkStringNull(request.getParameter("desc"));
  String status = Functions.checkStringNull(request.getParameter("status"));
  String firstName=Functions.checkStringNull(request.getParameter("firstname"));
  String lastName=Functions.checkStringNull(request.getParameter("lastname"));
  String emailAddress=Functions.checkStringNull(request.getParameter("emailaddr"));
  String terminalid =Functions.checkStringNull(request.getParameter("terminalid"))==null?"":request.getParameter("terminalid");
  String paymentId =Functions.checkStringNull(request.getParameter("paymentid"));
  String trackingid =Functions.checkStringNull(request.getParameter("trackingid"))==null?"":request.getParameter("trackingid");
  String toid = Functions.checkStringNull(request.getParameter("toid"));
  Functions functions = new Functions();
  byte[] transactionReceiptImg=null;
  Calendar rightNow = Calendar.getInstance();
  String str = "";
  if (desc != null) str = str + "&desc=" + desc;
  if (bank != null) str = str + "&bank=" + bank;
  if (paymentId != null) str = str + "&paymentid=" + paymentId;
  if(firstName!=null)str = str + "&firstname=" + firstName;
  else
    firstName="";
  if(lastName!=null)str = str + "&lastname=" + lastName;
  else
    lastName="";
  if(emailAddress!=null)str = str + "&emailaddr=" + emailAddress;
  else
    emailAddress="";

  if(terminalid!=null)str =str + "&terminalid=" + terminalid;
  else
    terminalid="";
  if(userTerminals!=null)str =str + "&terminalbuffer=" + userTerminals;
  else
    userTerminals="";

  boolean archive = Boolean.valueOf(request.getParameter("archive")).booleanValue();
  str = str + "&archive=" + archive;
  String archivalString = "Archived";
  String currentString = "Current";
  String selectedArchived = "", selectedCurrent = "";
  if (archive)
  {
    selectedArchived = "selected";
    selectedCurrent = "";
  }
  else
  {
    selectedArchived = "";
    selectedCurrent = "selected";
  }
  int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

  StringBuffer terminalBuffer = new StringBuffer();
  ResourceBundle rb1 = null;
  ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.inquiryCronGateway");
  String language_property1 = (String)session.getAttribute("language_property");
  String transactionGateway="";
  rb1 = LoadProperties.getProperty(language_property1);
  String transactionDetails_Go_Back = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Go_Back"))?rb1.getString("transactionDetails_Go_Back"): "Go Back";
  String transactionDetails_Transaction_Status_Flag = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Transaction_Status_Flag"))?rb1.getString("transactionDetails_Transaction_Status_Flag"): "Transaction Status Flag";
  String transactionDetails_isSuccessful = StringUtils.isNotEmpty(rb1.getString("transactionDetails_isSuccessful"))?rb1.getString("transactionDetails_isSuccessful"): "isSuccessful";
  String transactionDetails_isSettled = StringUtils.isNotEmpty(rb1.getString("transactionDetails_isSettled"))?rb1.getString("transactionDetails_isSettled"): "isSettled";
  String transactionDetails_isRefund = StringUtils.isNotEmpty(rb1.getString("transactionDetails_isRefund"))?rb1.getString("transactionDetails_isRefund"): "isRefund ";
  String transactionDetails_isChargeback = StringUtils.isNotEmpty(rb1.getString("transactionDetails_isChargeback"))?rb1.getString("transactionDetails_isChargeback"): "isChargeback";
  String transactionDetails_isFraud = StringUtils.isNotEmpty(rb1.getString("transactionDetails_isFraud"))?rb1.getString("transactionDetails_isFraud"): "isFraud";
  String transactionDetails_isRollingReserveKept = StringUtils.isNotEmpty(rb1.getString("transactionDetails_isRollingReserveKept"))?rb1.getString("transactionDetails_isRollingReserveKept"): "isRollingReserveKept";
  String transactionDetails_isRollingReserve = StringUtils.isNotEmpty(rb1.getString("transactionDetails_isRollingReserve"))?rb1.getString("transactionDetails_isRollingReserve"): "isRollingReserveReleased";
  String transactionDetails_Action_History = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Action_History"))?rb1.getString("transactionDetails_Action_History"): "Action History";
  String transactionDetails_Action = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Action"))?rb1.getString("transactionDetails_Action"): "Action";
  String transactionDetails_Amount = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Amount"))?rb1.getString("transactionDetails_Amount"): "Amount";
  String transactionDetails_Currency = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Currency"))?rb1.getString("transactionDetails_Currency"): "Currency";
  String transactionDetails_Error_Code = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Error_Code"))?rb1.getString("transactionDetails_Error_Code"): "Error Code";
  String transactionDetails_Error_Description = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Error_Description"))?rb1.getString("transactionDetails_Error_Description"): "Error Description";
  String transactionDetails_Response = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Response"))?rb1.getString("transactionDetails_Response"): "Response TransactionID";
  String transactionDetails_ARN = StringUtils.isNotEmpty(rb1.getString("transactionDetails_ARN"))?rb1.getString("transactionDetails_ARN"): "ARN";
  String transactionDetails_Remark = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Remark"))?rb1.getString("transactionDetails_Remark"): "Remark";
  String transactionDetails_Timestamp = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Timestamp"))?rb1.getString("transactionDetails_Timestamp"): "Timestamp";
  String transactionDetails_BillingDescriptor = StringUtils.isNotEmpty(rb1.getString("transactionDetails_BillingDescriptor"))?rb1.getString("transactionDetails_BillingDescriptor"): "BillingDescriptor";
  String transactionDetails_Response_Code = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Response_Code"))?rb1.getString("transactionDetails_Response_Code"): "Response Code";
  String transactionDetails_Response1 = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Response1"))?rb1.getString("transactionDetails_Response1"): "Response Description";
  String transactionDetails_Verification = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Verification"))?rb1.getString("transactionDetails_Verification"): "Verification Code";
  String transactionDetails_Action_Executor = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Action_Executor"))?rb1.getString("transactionDetails_Action_Executor"): "Action Executor";
  String transactionDetails_TMPL = StringUtils.isNotEmpty(rb1.getString("transactionDetails_TMPL"))?rb1.getString("transactionDetails_TMPL"): "TMPL_Amount";
  String transactionDetails_TMPL1 = StringUtils.isNotEmpty(rb1.getString("transactionDetails_TMPL1"))?rb1.getString("transactionDetails_TMPL1"): "TMPL_Currency";
  String transactionDetails_Wallet_Amount = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Wallet_Amount"))?rb1.getString("transactionDetails_Wallet_Amount"): "Wallet_Amount";
  String transactionDetails_Wallet_Currency = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Wallet_Currency"))?rb1.getString("transactionDetails_Wallet_Currency"): "Wallet_Currency";
  String transactionDetails_Order_Details = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Order_Details"))?rb1.getString("transactionDetails_Order_Details"): "Order Details";
  String transactionDetails_Order_ID = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Order_ID"))?rb1.getString("transactionDetails_Order_ID"): "Order ID";
  String transactionDetails_Order1 = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Order1"))?rb1.getString("transactionDetails_Order1"): "Order Description";
  String transactionDetails_Transaction_Amount = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Transaction_Amount"))?rb1.getString("transactionDetails_Transaction_Amount"): "Transaction Amount";
  String transactionDetails_Template_Amount = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Template_Amount"))?rb1.getString("transactionDetails_Template_Amount"): "Template Amount";
  String transactionDetails_Notification_URL = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Notification_URL"))?rb1.getString("transactionDetails_Notification_URL"): "Notification URL";
  String transactionDetails_Payment_Mode = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Payment_Mode"))?rb1.getString("transactionDetails_Payment_Mode"): "Payment Mode";
  String transactionDetails_Payment_Brand = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Payment_Brand"))?rb1.getString("transactionDetails_Payment_Brand"): "Payment Brand";
  String transactionDetails_Terminal_ID = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Terminal_ID"))?rb1.getString("transactionDetails_Terminal_ID"): "Terminal ID";
  String transactionDetails_Transaction_Details = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Transaction_Details"))?rb1.getString("transactionDetails_Transaction_Details"): "Transaction Details";
  String transactionDetails_Date = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Date"))?rb1.getString("transactionDetails_Date"): "Date of Transaction";
  String transactionDetails_Last_Upadted_Date = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Last_Upadted_Date"))?rb1.getString("transactionDetails_Last_Upadted_Date"): "Last Upadted Date";
  String transactionDetails_Tracking_ID = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Tracking_ID"))?rb1.getString("transactionDetails_Tracking_ID"): "Tracking ID";
  String transactionDetails_Transaction_Status = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Transaction_Status"))?rb1.getString("transactionDetails_Transaction_Status"): "Transaction Status";
  String transactionDetails_Transaction_Remark = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Transaction_Remark"))?rb1.getString("transactionDetails_Transaction_Remark"): "Transaction Remark";
  String transactionDetails_Capture_Amount = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Capture_Amount"))?rb1.getString("transactionDetails_Capture_Amount"): "Capture Amount";
  String transactionDetails_Refund_Amount = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Refund_Amount"))?rb1.getString("transactionDetails_Refund_Amount"): "Refund Amount";
  String transactionDetails_Chargeback_Amount = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Chargeback_Amount"))?rb1.getString("transactionDetails_Chargeback_Amount"): "Chargeback Amount";
  String transactionDetails_Payout_Amount = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Payout_Amount"))?rb1.getString("transactionDetails_Payout_Amount"): "Payout Amount";
  String transactionDetails_ECI = StringUtils.isNotEmpty(rb1.getString("transactionDetails_ECI"))?rb1.getString("transactionDetails_ECI"): "ECI";
  String transactionDetails_WalletId = StringUtils.isNotEmpty(rb1.getString("transactionDetails_WalletId"))?rb1.getString("transactionDetails_WalletId"): "WalletId";
  String transactionDetails_Sorry = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Sorry"))?rb1.getString("transactionDetails_Sorry"): "Sorry";
  String transactionDetails_no = StringUtils.isNotEmpty(rb1.getString("transactionDetails_no"))?rb1.getString("transactionDetails_no"): "No records found for given search criteria.";
  String transactionDetails_Vendor_Order = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Vendor_Order"))?rb1.getString("transactionDetails_Vendor_Order"): "Vendor Order Details";
  String transactionDetails_Order_ID1 = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Order_ID1"))?rb1.getString("transactionDetails_Order_ID1"): "Order ID";
  String transactionDetails_Order2 = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Order2"))?rb1.getString("transactionDetails_Order2"): "Order Description";
  String transactionDetails_Transaction_Amount1 = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Transaction_Amount1"))?rb1.getString("transactionDetails_Transaction_Amount1"): "Transaction Amount";
  String transactionDetails_Template_Amount1 = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Template_Amount1"))?rb1.getString("transactionDetails_Template_Amount1"): "Template Amount";
  String transactionDetails_NotificationURL = StringUtils.isNotEmpty(rb1.getString("transactionDetails_NotificationURL"))?rb1.getString("transactionDetails_NotificationURL"): "Notification URL";
  String transactionDetails_PaymentMode = StringUtils.isNotEmpty(rb1.getString("transactionDetails_PaymentMode"))?rb1.getString("transactionDetails_PaymentMode"): "Payment Mode";
  String transactionDetails_PaymentBrand = StringUtils.isNotEmpty(rb1.getString("transactionDetails_PaymentBrand"))?rb1.getString("transactionDetails_PaymentBrand"): "Payment Brand";
  String transactionDetails_Vendor = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Vendor"))?rb1.getString("transactionDetails_Vendor"): "Vendor Transaction Details";
  String transactionDetails_Date_of_Transaction = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Date_of_Transaction"))?rb1.getString("transactionDetails_Date_of_Transaction"): "Date of Transaction";
  String transactionDetails_Last = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Last"))?rb1.getString("transactionDetails_Last"): "Last Upadted Date";
  String transactionDetails_TrackingID = StringUtils.isNotEmpty(rb1.getString("transactionDetails_TrackingID"))?rb1.getString("transactionDetails_TrackingID"): "Tracking ID";
  String transactionDetails_Transaction_Status1 = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Transaction_Status1"))?rb1.getString("transactionDetails_Transaction_Status1"): "Transaction Status";
  String transactionDetails_Transaction_Remark1 = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Transaction_Remark1"))?rb1.getString("transactionDetails_Transaction_Remark1"): "Transaction Remark";
  String transactionDetails_Capture_Amount1 = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Capture_Amount1"))?rb1.getString("transactionDetails_Capture_Amount1"): "Capture Amount";
  String transactionDetails_Refund_Amount1 = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Refund_Amount1"))?rb1.getString("transactionDetails_Refund_Amount1"): "Refund Amount";
  String transactionDetails_Chargeback = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Chargeback"))?rb1.getString("transactionDetails_Chargeback"): "Chargeback Amount";
  String transactionDetails_Payout_Amount1 = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Payout_Amount1"))?rb1.getString("transactionDetails_Payout_Amount1"): "Payout Amount";
  String transactionDetails_ECI1 = StringUtils.isNotEmpty(rb1.getString("transactionDetails_ECI1"))?rb1.getString("transactionDetails_ECI1"): "ECI";
  String transactionDetails_WalletId1 = StringUtils.isNotEmpty(rb1.getString("transactionDetails_WalletId1"))?rb1.getString("transactionDetails_WalletId1"): "WalletId";
  String transactionDetails_Customer = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Customer"))?rb1.getString("transactionDetails_Customer"): "Customer's  Details";
  String transactionDetails_Emailaddress = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Emailaddress"))?rb1.getString("transactionDetails_Emailaddress"): "Emailaddress";
  String transactionDetails_Ipaddress = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Ipaddress"))?rb1.getString("transactionDetails_Ipaddress"): "Ipaddress";
  String transactionDetails_Phone_Number = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Phone_Number"))?rb1.getString("transactionDetails_Phone_Number"): "Phone Number";
  String transactionDetails_City = StringUtils.isNotEmpty(rb1.getString("transactionDetails_City"))?rb1.getString("transactionDetails_City"): "City";
  String transactionDetails_Street = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Street"))?rb1.getString("transactionDetails_Street"): "Street";
  String transactionDetails_State = StringUtils.isNotEmpty(rb1.getString("transactionDetails_State"))?rb1.getString("transactionDetails_State"): "State";
  String transactionDetails_Country = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Country"))?rb1.getString("transactionDetails_Country"): "Country";
  String transactionDetails_post = StringUtils.isNotEmpty(rb1.getString("transactionDetails_post"))?rb1.getString("transactionDetails_post"): "Post/Zip Code";
  String transactionDetails_Card_Details = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Card_Details"))?rb1.getString("transactionDetails_Card_Details"): "Card Details";
  String transactionDetails_Cardholder = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Cardholder"))?rb1.getString("transactionDetails_Cardholder"): "Cardholder's Name";
  String transactionDetails_FirstSix = StringUtils.isNotEmpty(rb1.getString("transactionDetails_FirstSix"))?rb1.getString("transactionDetails_FirstSix"): "First Six";
  String transactionDetails_LastFour = StringUtils.isNotEmpty(rb1.getString("transactionDetails_LastFour"))?rb1.getString("transactionDetails_LastFour"): "Last Four";
  String transactionDetails_BinDetails = StringUtils.isNotEmpty(rb1.getString("transactionDetails_BinDetails"))?rb1.getString("transactionDetails_BinDetails"): "Bin Details";
  String transactionDetails_BinBrand = StringUtils.isNotEmpty(rb1.getString("transactionDetails_BinBrand"))?rb1.getString("transactionDetails_BinBrand"): "Bin Brand";
  String transactionDetails_Country1 = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Country1"))?rb1.getString("transactionDetails_Country1"): "Country";
  String transactionDetails_IssuingBank = StringUtils.isNotEmpty(rb1.getString("transactionDetails_IssuingBank"))?rb1.getString("transactionDetails_IssuingBank"): "Issuing Bank";
  String transactionDetails_Bin_Card_Type = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Bin_Card_Type"))?rb1.getString("transactionDetails_Bin_Card_Type"): "Bin Card Type";
  String transactionDetails_Bin_Card = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Bin_Card"))?rb1.getString("transactionDetails_Bin_Card"): "Bin Card Category";
  String transactionDetails_Fraud_Rules = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Fraud_Rules"))?rb1.getString("transactionDetails_Fraud_Rules"): "Fraud Rules Triggered";
  String transactionDetails_Fraud_Rule = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Fraud_Rule"))?rb1.getString("transactionDetails_Fraud_Rule"): "Fraud Rule Name";
  String transactionDetails_Status = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Status"))?rb1.getString("transactionDetails_Status"): "Status";
  String transactionDetails_Fraud_Rule_Score = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Fraud_Rule_Score"))?rb1.getString("transactionDetails_Fraud_Rule_Score"): "Fraud Rule Score";
  String transactionDetails_Transaction_Receipt = StringUtils.isNotEmpty(rb1.getString("transactionDetails_Transaction_Receipt"))?rb1.getString("transactionDetails_Transaction_Receipt"): "Transaction Receipt";
  String transactionDetails_TerminalID = StringUtils.isNotEmpty(rb1.getString("transactionDetails_TerminalID"))?rb1.getString("transactionDetails_TerminalID"): "Terminal ID";

%>
<%
  String fromdate = null;
  String todate = null;

  Date date = new Date();
  SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

  String Date = originalFormat.format(date);
  date.setDate(1);
  String fromDate = originalFormat.format(date);

  fromdate = Functions.checkStringNull(request.getParameter("fdate")) == null ? fromDate : request.getParameter("fdate");
  todate = Functions.checkStringNull(request.getParameter("tdate")) == null ? Date : request.getParameter("tdate");
%>

<html>
<head>
  <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
  <title><%=company%> <%=archive ? archivalString : currentString%> Transaction Details</title>

  <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script type="text/javascript">

    $('#sandbox-container input').datepicker({
    });
  </script>
  <script>
    $(function() {
      $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
    });
  </script>

  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
</head>

<body class="pace-done widescreen fixed-left-void">
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="pull-right">
        <div class="btn-group">
          <%
            String form = "";
            if(request.getAttribute("buttonValue").equals("transaction"))
            {
              form = "/merchant/servlet/PayoutTransactionList?ctoken=";
            }
            if(request.getAttribute("buttonValue").equals("invoice"))
            {
              form = "/merchant/servlet/Invoice?ctoken=";
            }
          %>
          <form action="<%=form%><%=ctoken%>" method="post" name="form">
            <%
              Enumeration<String> stringEnumeration=request.getParameterNames();
              while(stringEnumeration.hasMoreElements())
              {
                String name=stringEnumeration.nextElement();
                if ("trackingid".equals(name))
                {
                  out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                }
                else
                {
                  out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                }
              }
            %>
            <button type="submit" name="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-arrow-circle-left" aria-hidden="true"></i>&nbsp;&nbsp;<%=transactionDetails_Go_Back%></button>
          </form>

        </div>
      </div>
      <br><br><br>

      <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">


        <%
                int records = 0;
                Hashtable actionHistory = (Hashtable) request.getAttribute("actionHistory");

                ActionEntry entry = new ActionEntry();
                Hashtable statusmap = entry.getStatusHash();
                Hashtable temphash = null;

                str = str + "&terminalbuffer=" + terminalBuffer;
                str = str + "&ctoken=" + ctoken;
                str = str + "&terminalid=" + terminalid;
                str = str + "&paymentid=" + paymentId;

                try
                {
                    records=Integer.parseInt((String)actionHistory.get("records"));
                }
                catch(Exception ex)
                {

                }
                String style = "class=textb";

                if(records>0)
                {
            %>
      <div class="widget">
        <%
          Hashtable innerHash = (Hashtable) transactionDetails.get("1");// Since we'll get details of one transaction only
          if (innerHash != null)
          {
        %>
        <div class="widget-header">
          <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=transactionDetails_Transaction_Status_Flag%></strong></h2>
        </div>
        <div class="widget-content padding" style="overflow-x: auto;">

          <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
            <thead>
            <tr style="color: white;">
              <th style="text-align: center"><%=transactionDetails_isSuccessful%></th>
              <th style="text-align: center"><%=transactionDetails_isSettled%></th>
              <th style="text-align: center"><%=transactionDetails_isRefund%></th>
              <th style="text-align: center"><%=transactionDetails_isChargeback%></th>
              <th style="text-align: center"><%=transactionDetails_isFraud%></th>
              <th style="text-align: center"><%=transactionDetails_isRollingReserveKept%></th>
              <th style="text-align: center"><%=transactionDetails_isRollingReserve%></th>


            </tr>
            </thead>
            <%
                String isSuccessful = (String) innerHash.get("isSuccessful");
                String isSettled = (String) innerHash.get("isSettled");
                String isRefund = (String) innerHash.get("isRefund");
                String isChargeback = (String) innerHash.get("isChargeback");
                String isFraud = (String) innerHash.get("isFraud");
                String isRollingReserveKept = (String) innerHash.get("isRollingReserveKept");
                String isRollingReserveReleased = (String) innerHash.get("isRollingReserveReleased");
                transactionGateway = (String) innerHash.get("fromtype");

                if (!functions.isValueNull(isSuccessful))
                  isSuccessful = "-";
                if (!functions.isValueNull(isSettled))
                  isSettled = "-";
                if (!functions.isValueNull(isRefund))
                  isRefund = "-";
                if (!functions.isValueNull(isChargeback))
                  isChargeback = "-";
                if (!functions.isValueNull(isFraud))
                  isFraud = "-";
                if (!functions.isValueNull(isRollingReserveKept))
                  isRollingReserveKept = "-";
                if (!functions.isValueNull(isRollingReserveReleased))
                  isRollingReserveReleased = "-";

                out.println("<tr " + style + ">");
                out.println("<td valgn=\"middle\" data-label=\"isSuccessful\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(isSuccessful) + "</td>");
                out.println("<td valgn=\"middle\" data-label=\"isSettled\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(isSettled) + "</td>");
                out.println("<td valgn=\"middle\" data-label=\"isRefund\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(isRefund) + "</td>");
                out.println("<td valgn=\"middle\" data-label=\"isChargeback\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(isChargeback) + "</td>");
                out.println("<td valgn=\"middle\" data-label=\"isFraud\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(isFraud) + "</td>");
                out.println("<td valgn=\"middle\" data-label=\"isRollingReserveKept\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(isRollingReserveKept) + "</td>");
                out.println("<td valgn=\"middle\" data-label=\"isRollingReserveReleased\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(isRollingReserveReleased) + "</td>");
                out.println("</tr>");
              }
            %>
          </table>
        </div>
      </div>
      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                  <div class="widget">

                    <div class="widget-header transparent">
                      <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=transactionDetails_Action_History%></strong></h2>
                      <div class="additional-btn">
                        <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                        <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                        <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                      </div>
                    </div>

                    <div class="widget-content padding"  style="overflow-y: auto;">
                      <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                        <thead >
                        <tr style="color: white;">
                          <th style="text-align: center"><%=transactionDetails_Action%></th>
                          <th style="text-align: center"><%=transactionDetails_Amount%></th>
                          <th style="text-align: center"><%=transactionDetails_Currency%></th>
                          <th style="text-align: center"><%=transactionDetails_Error_Code%></th>
                          <th style="text-align: center"><%=transactionDetails_Error_Description%></th>
                          <th style="text-align: center"><%=transactionDetails_Response%></th>
                          <th style="text-align: center"><%=transactionDetails_ARN%></th>
                          <th style="text-align: center"><%=transactionDetails_Remark%></th>
                          <th style="text-align: center"><%=transactionDetails_Timestamp%></th>
                          <th style="text-align: center"><%=transactionDetails_BillingDescriptor%></th>
                          <th style="text-align: center"><%=transactionDetails_Response_Code%></th>
                          <th style="text-align: center"><%=transactionDetails_Response1%></th>
                          <th style="text-align: center"><%=transactionDetails_Verification%></th>
                          <th style="text-align: center"><%=transactionDetails_Action_Executor%></th>
                          <th style="text-align: center"><%=transactionDetails_TMPL%></th>
                          <th style="text-align: center"><%=transactionDetails_TMPL1%></th>
                          <th style="text-align: center"><%=transactionDetails_Wallet_Amount%></th>
                          <th style="text-align: center"><%=transactionDetails_Wallet_Currency%></th>
                        </tr>
                        </thead>
                        <tbody>
                        <%

                          ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
                          ErrorCodeVO errorCodeVO = null;
                          for (int pos = 1; pos <= records; pos++)
                          {
                            String id = Integer.toString(pos);
                            style = "class=\"tr" + (pos + 1) % 2 + "\"";
                            temphash = (Hashtable) actionHistory.get(id);
                            String date1 = (String)temphash.get("timestamp");
                            String action = (String)temphash.get("action");
                            if (functions.isValueNull((String)temphash.get("action")))
                            {
                              action=(String)temphash.get("action");
                            }
                            else
                            {
                              action="";
                            }
                            String remark = (String)temphash.get("remark");
                            String responseInfo=(String) temphash.get("responsehashinfo");
                            String arn=(String) temphash.get("arn");
                            if (!functions.isValueNull(arn))
                              arn = "-";

                            if (!functions.isValueNull(responseInfo))
                              responseInfo = "-";

                            if (!functions.isValueNull(remark))
                              remark = "-";
                            String billingDescriptor = "";
                            if (functions.isValueNull((String) temphash.get("responsedescriptor")))
                              billingDescriptor = (String) temphash.get("responsedescriptor");
                            String actionexename=(String)temphash.get("actionexecutorname");
                            String amount=(String)temphash.get("amount");
                            String templateamount="";
                            String currency="";
                            String templatecurrency="";
                            String responseDescription="";
                            String walletAmount="";
                            String walletCurrency="";

                            if(functions.isValueNull((String)temphash.get("amount")))
                            {
                              amount=(String)temphash.get("amount");
                            }
                            if(functions.isValueNull((String)temphash.get("templateamount")))
                            {
                              templateamount=(String)temphash.get("templateamount");

                            }
                            if(functions.isValueNull((String)temphash.get("currency")))
                            {
                              currency=(String)temphash.get("currency");
                            }
                            else
                            {
                              currency="-";
                            }
                            if(functions.isValueNull((String)temphash.get("actionexecutorname")))
                            {
                              String role="";
                              String username="";
                              if(actionexename.contains("Admin"))
                              {
                                String splitvalue[]=actionexename.split("-");
                                role=splitvalue[0];
                                username=splitvalue[1];
                                actionexename=role;
                              }
                              else
                              {
                                actionexename=(String)temphash.get("actionexecutorname");
                              }
                            }
                            else
                            {
                              actionexename="-";
                            }

                            if(functions.isValueNull((String)temphash.get("templatecurrency")))
                            {
                              templatecurrency=(String)temphash.get("templatecurrency");
                            }
                            else
                            {
                              templatecurrency="-";
                            }

                            if(functions.isValueNull((String)temphash.get("walletAmount"))){
                              walletAmount=(String)temphash.get("walletAmount");
                            }else {
                              walletAmount="-";
                            }

                            if(functions.isValueNull((String)temphash.get("walletCurrency"))){
                              walletCurrency=(String)temphash.get("walletCurrency");
                            }else {
                              walletCurrency="-";
                            }

                            if(functions.isValueNull((String)temphash.get("responsedescription")))
                            {
                              responseDescription=(String)temphash.get("responsedescription");
                            }
                            else
                            {
                              responseDescription="";
                            }

                            String errorName = (String) temphash.get("errorName");
                            String errorCode = "-";
                            String errorDescription = "-";
                            if (functions.isValueNull((String) temphash.get("errorName")))
                            {
                              errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.valueOf(errorName));
                              errorCode = errorCodeVO.getApiCode();
                              errorDescription = errorCodeVO.getApiDescription();
                            }

                            String responceTransId="";
                            if(functions.isValueNull((String) temphash.get("responsetransactionid")))
                            {
                              responceTransId=(String)temphash.get("responsetransactionid");
                            }
                            else
                            {
                              responceTransId="";
                            }
                            String responseCode="";
                            if(functions.isValueNull((String) temphash.get("responsecode")))
                            {
                              responseCode=(String)temphash.get("responsecode");
                            }
                            else
                            {
                              responseCode="";
                            }
                            if ("JPY".equalsIgnoreCase(currency))
                            {
                              amount = functions.printNumber(Locale.JAPAN, amount);
                            }
                            else if ("EUR".equalsIgnoreCase(currency))
                            {
                              amount = functions.printNumber(Locale.FRANCE, amount);
                            }
                            else if ("GBP".equalsIgnoreCase(currency))
                            {
                              amount = functions.printNumber(Locale.UK, amount);
                            }
                            else if ("USD".equalsIgnoreCase(currency))
                            {
                              amount = functions.printNumber(Locale.US, amount);
                            }

                            if ("JPY".equalsIgnoreCase(templatecurrency))
                            {
                              templateamount = functions.printNumber(Locale.JAPAN, templateamount);
                            }
                            else if ("EUR".equalsIgnoreCase(templatecurrency))
                            {
                              templateamount = functions.printNumber(Locale.FRANCE, templateamount);
                            }
                            else if ("GBP".equalsIgnoreCase(templatecurrency))
                            {
                              templateamount = functions.printNumber(Locale.UK, templateamount);
                            }
                            else if ("USD".equalsIgnoreCase(templatecurrency))
                            {
                              templateamount = functions.printNumber(Locale.US, templateamount);
                            }
                            if (temphash.get("transactionReceiptImg") != null)
                            {
                              transactionReceiptImg = (byte[]) temphash.get("transactionReceiptImg");
                            }
                            out.println("<td data-label=\"Action\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(action) + "</td>");
                            out.println("<td data-label=\"Amount\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(amount) + "</td>");
                            out.println("<td data-label=\"Currency\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(currency) + "</td>");
                            out.println("<td data-label=\"Action\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(errorCode) + "</td>");
                            out.println("<td data-label=\"Action\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(errorDescription) + "</td>");
                            out.println("<td data-label=\"Action\" style=\"text-align: center\">-</td>");
                            out.println("<td data-label=\"Arn\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(arn) + "</td>");
                            out.println("<td data-label=\"Remark\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(remark) + "</td>");
                            out.println("<td data-label=\"Timestamp\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(date1) +  "</td>");
                            out.println("<td data-label=\"BillingDescriptor\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(billingDescriptor) +  "</td>");
                            out.println("<td data-label=\"Response Code\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(responseCode) +  "</td>");
                            out.println("<td data-label=\"Response Description\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(responseDescription) +  "</td>");
                              out.println("<td data-label=\"Verification Code\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(responseInfo) +  "</td>");

                            out.println("<td data-label=\"Action Executor\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(actionexename) +  "</td>");
                            out.println("<td data-label=\"Templateamount\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(templateamount) + "</td>");
                            out.println("<td data-label=\"Templatecurrency\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(templatecurrency) + "</td>");
                            out.println("<td data-label=\"walletAmount\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(walletAmount) + "</td>");
                            out.println("<td data-label=\"walletCurrency\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(walletCurrency) + "</td>");
                            out.println("</tr>");
                          }
                        %>
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>
              </div>
              <%
                }
              %>

              <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                  <div class="widget">

                    <div class="widget-header transparent">
                      <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=transactionDetails_Order_Details%></strong></h2>
                      <div class="additional-btn">
                        <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                        <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                        <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                      </div>
                    </div>
                    <%
                      Hashtable innerHash = (Hashtable) transactionDetails.get("1");// Since we'll get details of one transaction only
                      if(innerHash!=null)
                      {
                    %>

                    <div class="widget-content padding" style="overflow-y: auto;">
                      <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">

                        <thead>

                        <tr style="color: white;">
                          <th style="text-align: center"><%=transactionDetails_Order_ID%></th>
                          <th style="text-align: center"><%=transactionDetails_Order1%></th>
                          <th style="text-align: center"><%=transactionDetails_Transaction_Amount%></th>
                          <th style="text-align: center"><%=transactionDetails_Template_Amount%></th>
                          <th style="text-align: center"><%=transactionDetails_Notification_URL%></th>
                          <th style="text-align: center"><%=transactionDetails_Payment_Mode%></th>
                          <th style="text-align: center"><%=transactionDetails_Payment_Brand%></th>
                          <th style="text-align: center"><%=transactionDetails_Terminal_ID%></th>

                        </tr>
                        </thead>
                        <tbody>
                        <%
                          String orderId = "";
                          if (functions.isValueNull((String)innerHash.get("description")))
                          {
                            orderId=(String)innerHash.get("description");
                          }
                          else
                          {
                            orderId="";
                          }

                          String orderDescription = "";
                          if (functions.isValueNull((String)innerHash.get("orderdescription")))
                          {
                            orderDescription=(String) innerHash.get("orderdescription");
                          }
                          else
                          {
                            orderDescription = "";
                          }

                          String transactionAmout = (String) innerHash.get("amount");

                          String templateAmount="";
                          if(functions.isValueNull((String) innerHash.get("templateAmount")))
                          {
                            templateAmount=(String) innerHash.get("templateAmount");
                          }
                          else
                          {
                            templateAmount = "";
                          }

                          String tmplcurrency="";
                          if(functions.isValueNull((String) innerHash.get("templatecurrency")))
                          {
                            tmplcurrency=(String) innerHash.get("templatecurrency");
                          }
                          else
                          {
                            tmplcurrency = "";
                          }

                          String currency="";
                          if(functions.isValueNull((String) innerHash.get("currency")))
                          {
                            currency=(String) innerHash.get("currency");
                          }
                          else
                          {
                            currency = "";
                          }

                          String paymentMode="";
                          if(functions.isValueNull((String) innerHash.get("paymodeid")))
                          {
                            paymentMode=(String) innerHash.get("paymodeid");
                          }
                          else
                          {
                            paymentMode = "-";
                          }

                          String paymentBrand="";
                          if(functions.isValueNull((String) innerHash.get("cardtype")))
                          {
                            paymentBrand=(String) innerHash.get("cardtype");
                          }
                          else
                          {
                            paymentBrand = "-";
                          }

                          String notificationUrl="";

                          if(functions.isValueNull((String) innerHash.get("NotificationUrl"))){
                            notificationUrl=(String) innerHash.get("NotificationUrl");
                          }else {
                            notificationUrl = "-";
                          }
                          String terminalId="";

                          if(functions.isValueNull((String) innerHash.get("terminalid"))){
                            terminalId=(String) innerHash.get("terminalid");
                          }else {
                            terminalId = "-";
                          }

                          if ("JPY".equalsIgnoreCase(tmplcurrency))
                          {
                            templateAmount = functions.printNumber(Locale.US, templateAmount);
                          }
                          else if ("EUR".equalsIgnoreCase(tmplcurrency))
                          {
                            templateAmount = functions.printNumber(Locale.US, templateAmount);
                          }
                          else if ("GBP".equalsIgnoreCase(tmplcurrency))
                          {
                            templateAmount = functions.printNumber(Locale.US, templateAmount);
                          }
                          else if ("USD".equalsIgnoreCase(tmplcurrency))
                          {
                            templateAmount = functions.printNumber(Locale.US, templateAmount);
                          }

                          if ("JPY".equalsIgnoreCase(currency))
                          {
                            transactionAmout = functions.printNumber(Locale.JAPAN, transactionAmout);
                          }
                          else if ("EUR".equalsIgnoreCase(currency))
                          {
                            transactionAmout = functions.printNumber(Locale.FRANCE, transactionAmout);
                          }
                          else if ("GBP".equalsIgnoreCase(currency))
                          {
                            transactionAmout = functions.printNumber(Locale.UK, transactionAmout);
                          }
                          else if ("USD".equalsIgnoreCase(currency))
                          {
                            transactionAmout = functions.printNumber(Locale.US, transactionAmout);
                          }

                          //out.println("<tr " + style + ">");
                          out.println("<td data-label=\"Order Id\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(orderId) +  "</td>");
                          out.println("<td data-label=\"Order Description \" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(orderDescription) +  "</td>");
                          out.println("<td data-label=\"Transaction Amount\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(currency+" "+transactionAmout) + "</td>");
                          out.println("<td data-label=\"Template Amount\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(tmplcurrency+" "+templateAmount) +  "</td>");
                          out.println("<td data-label=\"Notification Url\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(notificationUrl) +  "</td>");
                          out.println("<td data-label=\"Payment Mode\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(paymentMode) +  "</td>");
                          out.println("<td data-label=\"Payment Brand\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(paymentBrand) +  "</td>");
                          out.println("<td data-label=\"Terminal ID\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(terminalId) +  "</td>");
                          out.println("</tr>");

                        %>
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>
              </div>

              <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                  <div class="widget">

                    <div class="widget-header transparent">
                      <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=transactionDetails_Transaction_Details%></strong></h2>
                      <div class="additional-btn">
                        <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                        <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                        <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                      </div>
                    </div>
                    <div class="widget-content padding" style="overflow-y: auto;">
                      <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">

                        <thead>

                        <tr style="color: white;">
                          <th style="text-align: center"><%=transactionDetails_Date%></th>
                          <th style="text-align: center"><%=transactionDetails_Last_Upadted_Date%></th>
                          <th style="text-align: center"><%=transactionDetails_Tracking_ID%></th>
                          <th style="text-align: center"><%=transactionDetails_Transaction_Status%></th>
                          <th style="text-align: center"><%=transactionDetails_Transaction_Remark%></th>
                          <th style="text-align: center"><%=transactionDetails_Capture_Amount%></th>
                          <th style="text-align: center"><%=transactionDetails_Refund_Amount%></th>
                          <th style="text-align: center"><%=transactionDetails_Chargeback_Amount%></th>
                          <th style="text-align: center"><%=transactionDetails_Payout_Amount%></th>
                          <th style="text-align: center"><%=transactionDetails_ECI%></th>
                          <%
                            String Emi="";
                            if(functions.isValueNull((String) innerHash.get("Emi"))){

                              Emi=(String) innerHash.get("Emi");
                          %>
                          <th style="text-align: center">Installment</th>
                          <%
                            }
                          %>

                          <%
                            String walletId="";
                            if(functions.isValueNull((String) innerHash.get("walletId"))){

                              walletId=(String) innerHash.get("walletId");
                          %>
                          <th style="text-align: center"><%=transactionDetails_WalletId%></th>
                          <%
                            }
                          %>
                        </tr>
                        </thead>
                        <tbody>
                        <%
                          String trackingId = (String) innerHash.get("icicitransid");
                          String refundamount = "";
                          if (functions.isValueNull((String)innerHash.get("refundamount")))
                          {
                            refundamount=(String) innerHash.get("refundamount");
                          }
                          else
                          {
                            refundamount="";
                          }
                          String captureamount = "";
                          if (functions.isValueNull((String)innerHash.get("captureamount")))
                          {
                            captureamount=(String) innerHash.get("captureamount");
                          }
                          else
                          {
                            captureamount="";
                          }
                          String payoutamount = "";
                          if (functions.isValueNull((String)innerHash.get("payoutamount")))
                          {
                            payoutamount=(String) innerHash.get("payoutamount");
                          }
                          else
                          {
                            payoutamount="";
                          }
                          String chargebackAmount = "";
                          if (functions.isValueNull((String)innerHash.get("chargebackamount")))
                          {
                            chargebackAmount =(String) innerHash.get("chargebackamount");
                          }
                          String date1 = ((String) innerHash.get("date"));
                          String timestamp = "";
                          if (functions.isValueNull((String)innerHash.get("timestamp")))
                          {
                            timestamp=(String) innerHash.get("timestamp");
                          }
                          else
                          {
                            timestamp="";
                          }
                          String transStatus = (String) innerHash.get("status");
                          String transStatusDetail = "Not Available";
                          String Eci="";

                          if(functions.isValueNull((String) innerHash.get("ECI"))){
                            Eci=(String) innerHash.get("ECI");
                          }else {
                            Eci = "-";
                          }

                          if(Functions.checkStringNull((String) innerHash.get("authqsiresponsecode"))!=null)
                          {
                            transStatusDetail = (String) innerHash.get("authqsiresponsecode");
                          }
                          if ("JPY".equalsIgnoreCase(currency))
                          {
                            captureamount = functions.printNumber(Locale.US, captureamount);
                            refundamount = functions.printNumber(Locale.US, refundamount);
                            chargebackAmount = functions.printNumber(Locale.US, chargebackAmount);
                            payoutamount = functions.printNumber(Locale.US, payoutamount);
                          }
                          else if ("EUR".equalsIgnoreCase(currency))
                          {
                            captureamount = functions.printNumber(Locale.US, captureamount);
                            refundamount = functions.printNumber(Locale.US, refundamount);
                            chargebackAmount = functions.printNumber(Locale.US, chargebackAmount);
                            payoutamount = functions.printNumber(Locale.US, payoutamount);
                          }
                          else if ("GBP".equalsIgnoreCase(currency))
                          {
                            captureamount = functions.printNumber(Locale.US, captureamount);
                            refundamount = functions.printNumber(Locale.US, refundamount);
                            chargebackAmount = functions.printNumber(Locale.US, chargebackAmount);
                            payoutamount = functions.printNumber(Locale.US, payoutamount);;
                          }
                          else if ("USD".equalsIgnoreCase(currency))
                          {
                            captureamount = functions.printNumber(Locale.US, captureamount);
                            refundamount = functions.printNumber(Locale.US, refundamount);
                            chargebackAmount = functions.printNumber(Locale.US, chargebackAmount);
                            payoutamount = functions.printNumber(Locale.US, payoutamount);
                          }
                          out.println("<td data-label=\"Date of Transaction\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(date1) +  "</td>");
                          out.println("<td data-label=\"Last Updated Date \" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(timestamp) +  "</td>");
                          out.println("<td data-label=\"Tracking Id\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(trackingId) + "</td>");
                          out.println("<td data-label=\"Transaction Status\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(transStatus) +  "</td>");
                          out.println("<td data-label=\"Transaction Remark\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(transStatusDetail) +  "</td>");
                          out.println("<td data-label=\"Capture Amount\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(captureamount) +  "</td>");
                          out.println("<td data-label=\"Refund Amount\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(refundamount) +  "</td>");
                          out.println("<td data-label=\"Chargeback Amount\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(chargebackAmount) +  "</td>");
                          out.println("<td data-label=\"Payout Amount\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(payoutamount) +  "</td>");
                          out.println("<td data-label=\"ECI\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(Eci) +  "</td>");
                          if(functions.isValueNull(Emi)){
                            out.println("<td data-label=\"Installment\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(Emi) +  "</td>");
                          }
                          if(functions.isValueNull(walletId)){
                            out.println("<td data-label=\"Installment\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(walletId) +  "</td>");
                          }
                          out.println("</tr>");

                        %>
                        </tbody>
                      </table>
                      <%
                        Enumeration gatewayName=RB.getKeys();
                        String str11="";

                        while(gatewayName.hasMoreElements()){
                          String key = (String) gatewayName.nextElement();
                          str11=RB.getString(key);
                          System.out.println("key--------->"+key);
                        }
                        System.out.println("transactionGateway--------->"+transactionGateway);
                        System.out.println("transactionGateway key--------->"+RB.containsKey(transactionGateway));
                        System.out.println("transStatus--------->"+transStatus);
                        if(RB.containsKey(transactionGateway)&&("payoutfailed".equalsIgnoreCase(transStatus)||"payoutstarted".equalsIgnoreCase(transStatus)||"authstarted".equalsIgnoreCase(transStatus)||("authfailed".equalsIgnoreCase(transStatus)))){
                      %>
                      <br>
                      <div align="center">
                        <form action="/merchant/servlet/TransactionDetailsInquiryServlet?ctoken=<%=ctoken%>" method="post" >
                          <input type="hidden" name="trackingid" value="<%=trackingid%>">
                          <input type="hidden" name="status" value="<%=transStatus%>">
                          <input type="hidden" name="toid" value="<%=toid%>">
                          <input type="hidden" name="gateway" value="<%=transactionGateway%>">

                          <button type="submit" class="btn btn-default">
                            InquiryStatus
                          </button>
                        </form>
                      </div>
                      <%}%>
                    </div>
                    <%
                      }
                      else
                      {
                        System.out.println("transactionDetails_no--->"+transactionDetails_no);
                        out.println(Functions.NewShowConfirmation1(transactionDetails_Sorry, transactionDetails_no));
                      }
                    %>
                  </div>
                </div>
              </div>
              <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                  <div class="widget">
                    <%
                      String transStatus1 = (String) innerHash.get("status");
                      if (transStatus1.equalsIgnoreCase("payoutfailed") || transStatus1.equalsIgnoreCase("payoutstarted") || transStatus1.equalsIgnoreCase("payoutsuccessful"))
                      {
                        String value1="",value2="", value3="";
                        Hashtable innerhashpay= (Hashtable) hashpayout.get("1");
                        if (innerhashpay != null  && innerhashpay.size()>0)
                        {
                          if (innerhashpay.get("fullname")!= null)
                          {
                            value1 = (String) innerhashpay.get("fullname");
                          }
                          if (innerhashpay.get("bankaccount")!= null)
                          {
                            value2 = (String) innerhashpay.get("bankaccount");
                          }
                          if (innerhashpay.get("ifsc")!= null)
                          {
                            value3 = (String) innerhashpay.get("ifsc");
                          }
                        }
                        else
                        {
                          value1= "-";
                          value2= "-";
                          value3= "-";
                        }

                    %>
                    <div class="widget-header transparent">
                      <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Customer Bank Details</strong></h2>
                      <div class="additional-btn">
                        <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                        <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                        <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                      </div>
                    </div>
                    <div class="widget-content padding" style="overflow-y: auto;">
                      <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">

                        <thead>
                        <tr style="color: white;">
                          <th style="text-align: center">Beneficiary name</th>
                          <th style="text-align: center">Beneficiary Account Number</th>
                          <th style="text-align: center">IFSC Code</th>
                        </tr>
                        </thead>
                        <tbody>
                        <%
                          out.println("<td data-label=\"Bank name\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(value1) +  "</td>");
                          out.println("<td data-label=\"Bank Account \" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(value2) +  "</td>");
                          out.println("<td data-label=\"Bank Ifsc\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(value3) + "</td>");
                        %>
                        </tbody>
                      </table>
                    </div>
                    <%
                      }
                    %>
                  </div>
                </div>
              </div>
<%--              <%if(childhash != null){
                Hashtable childInnerHash = (Hashtable) childhash.get("1");// Since we'll get details of one transaction only
                if(childInnerHash!=null)
                {
              %>
              <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                  <div class="widget">

                    <div class="widget-header transparent">
                      <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=transactionDetails_Vendor_Order%></strong></h2>
                      <div class="additional-btn">
                        <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                        <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                        <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                      </div>
                    </div>

                    <div class="widget-content padding" style="overflow-y: auto;">
                      <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">

                        <thead>

                        <tr style="color: white;">
                          <th style="text-align: center"><%=transactionDetails_Order_ID1%></th>
                          <th style="text-align: center"><%=transactionDetails_Order2%></th>
                          <th style="text-align: center"><%=transactionDetails_Transaction_Amount1%></th>
                          <th style="text-align: center"><%=transactionDetails_Template_Amount1%></th>
                          <th style="text-align: center"><%=transactionDetails_NotificationURL%></th>
                          <th style="text-align: center"><%=transactionDetails_PaymentMode%></th>
                          <th style="text-align: center"><%=transactionDetails_PaymentBrand%></th>
                          <th style="text-align: center"><%=transactionDetails_TerminalID%></th>

                        </tr>
                        </thead>
                        <tbody>
                        <%
                          for(int i=1;i<=childhash.size();i++)
                          {
                            childInnerHash = (Hashtable) childhash.get(""+i);// Since we'll get details of one transaction only
                            if(childInnerHash!=null)
                            {
                              String orderId = "";
                              if (functions.isValueNull((String) childInnerHash.get("description")))
                              {
                                orderId = (String) childInnerHash.get("description");
                              }
                              else
                              {
                                orderId = "";
                              }

                              String orderDescription = "";
                              if (functions.isValueNull((String) childInnerHash.get("orderdescription")))
                              {
                                orderDescription = (String) childInnerHash.get("orderdescription");
                              }
                              else
                              {
                                orderDescription = "";
                              }

                              String transactionAmout = (String) childInnerHash.get("amount");

                              String templateAmount = "";
                              if (functions.isValueNull((String) childInnerHash.get("templateAmount")))
                              {
                                templateAmount = (String) childInnerHash.get("templateAmount");
                              }
                              else
                              {
                                templateAmount = "";
                              }

                              String tmplcurrency = "";
                              if (functions.isValueNull((String) childInnerHash.get("templatecurrency")))
                              {
                                tmplcurrency = (String) childInnerHash.get("templatecurrency");
                              }
                              else
                              {
                                tmplcurrency = "";
                              }

                              String currency = "";
                              if (functions.isValueNull((String) childInnerHash.get("currency")))
                              {
                                currency = (String) childInnerHash.get("currency");
                              }
                              else
                              {
                                currency = "";
                              }

                              String paymentMode = "";
                              if (functions.isValueNull((String) childInnerHash.get("paymodeid")))
                              {
                                paymentMode = (String) childInnerHash.get("paymodeid");
                              }
                              else
                              {
                                paymentMode = "-";
                              }

                              String paymentBrand = "";
                              if (functions.isValueNull((String) childInnerHash.get("cardtype")))
                              {
                                paymentBrand = (String) childInnerHash.get("cardtype");
                              }
                              else
                              {
                                paymentBrand = "-";
                              }

                              String notificationUrl = "";

                              if (functions.isValueNull((String) childInnerHash.get("NotificationUrl")))
                              {
                                notificationUrl = (String) childInnerHash.get("NotificationUrl");
                              }
                              else
                              {
                                notificationUrl = "-";
                              }
                              String terminalId = "";

                              if (functions.isValueNull((String) childInnerHash.get("terminalid")))
                              {
                                terminalId = (String) childInnerHash.get("terminalid");
                              }
                              else
                              {
                                terminalId = "-";
                              }

                              if ("JPY".equalsIgnoreCase(tmplcurrency))
                              {
                                templateAmount = functions.printNumber(Locale.US, templateAmount);
                              }
                              else if ("EUR".equalsIgnoreCase(tmplcurrency))
                              {
                                templateAmount = functions.printNumber(Locale.US, templateAmount);
                              }
                              else if ("GBP".equalsIgnoreCase(tmplcurrency))
                              {
                                templateAmount = functions.printNumber(Locale.US, templateAmount);
                              }
                              else if ("USD".equalsIgnoreCase(tmplcurrency))
                              {
                                templateAmount = functions.printNumber(Locale.US, templateAmount);
                              }

                              if ("JPY".equalsIgnoreCase(currency))
                              {
                                transactionAmout = functions.printNumber(Locale.JAPAN, transactionAmout);
                              }
                              else if ("EUR".equalsIgnoreCase(currency))
                              {
                                transactionAmout = functions.printNumber(Locale.FRANCE, transactionAmout);
                              }
                              else if ("GBP".equalsIgnoreCase(currency))
                              {
                                transactionAmout = functions.printNumber(Locale.UK, transactionAmout);
                              }
                              else if ("USD".equalsIgnoreCase(currency))
                              {
                                transactionAmout = functions.printNumber(Locale.US, transactionAmout);
                              }
                              out.println("<tr " + style + ">");
                              out.println("<td data-label=\"Order Id\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(orderId) + "</td>");
                              out.println("<td data-label=\"Order Description \" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(orderDescription) + "</td>");
                              out.println("<td data-label=\"Transaction Amount\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(currency + " " + transactionAmout) + "</td>");
                              out.println("<td data-label=\"Template Amount\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(tmplcurrency + " " + templateAmount) + "</td>");
                              out.println("<td data-label=\"Notification Url\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(notificationUrl) + "</td>");
                              out.println("<td data-label=\"Payment Mode\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(paymentMode) + "</td>");
                              out.println("<td data-label=\"Payment Brand\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(paymentBrand) + "</td>");
                              out.println("<td data-label=\"Terminal ID\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(terminalId) + "</td>");
                              out.println("</tr>");
                            }
                          }
                        %>
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>
              </div>
              <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                  <div class="widget">

                    <div class="widget-header transparent">
                      <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=transactionDetails_Vendor%></strong></h2>
                      <div class="additional-btn">
                        <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                        <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                        <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                      </div>
                    </div>
                    <div class="widget-content padding" style="overflow-y: auto;">
                      <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">

                        <thead>

                        <tr style="color: white;">
                          <th style="text-align: center"><%=transactionDetails_Date_of_Transaction%></th>
                          <th style="text-align: center"><%=transactionDetails_Last%></th>
                          <th style="text-align: center"><%=transactionDetails_TrackingID%> </th>
                          <th style="text-align: center"><%=transactionDetails_Transaction_Status1%></th>
                          <th style="text-align: center"><%=transactionDetails_Transaction_Remark1%></th>
                          <th style="text-align: center"><%=transactionDetails_Capture_Amount1%></th>
                          <th style="text-align: center"><%=transactionDetails_Refund_Amount1%></th>
                          <th style="text-align: center"><%=transactionDetails_Chargeback%></th>
                          <th style="text-align: center"><%=transactionDetails_Payout_Amount1%></th>
                          <th style="text-align: center"><%=transactionDetails_ECI1%></th>
                          <%
                            String Emi="";
                            if(functions.isValueNull((String) childInnerHash.get("Emi"))){

                              Emi=(String) childInnerHash.get("Emi");
                          %>
                          <th style="text-align: center">Installment</th>
                          <%
                            }
                          %>

                          <%
                            String walletId="";
                            if(functions.isValueNull((String) childInnerHash.get("walletId"))){

                              walletId=(String) childInnerHash.get("walletId");
                          %>
                          <th style="text-align: center"><%=transactionDetails_WalletId1%></th>
                          <%
                            }
                          %>
                        </tr>
                        </thead>
                        <tbody>
                        <%
                          for(int i=1;i<=childhash.size();i++)
                          {
                            childInnerHash=(Hashtable)childhash.get(""+i);
                            String trackingId = (String) childInnerHash.get("icicitransid");
                            String currency = "";
                            String refundamount = "";
                            if (functions.isValueNull((String) childInnerHash.get("currency")))
                            {
                              currency = (String) childInnerHash.get("currency");
                            }
                            if (functions.isValueNull((String) childInnerHash.get("refundamount")))
                            {
                              refundamount = (String) childInnerHash.get("refundamount");
                            }
                            else
                            {
                              refundamount = "";
                            }
                            String captureamount = "";
                            if (functions.isValueNull((String) childInnerHash.get("captureamount")))
                            {
                              captureamount = (String) childInnerHash.get("captureamount");
                            }
                            else
                            {
                              captureamount = "";
                            }
                            String payoutamount = "";
                            if (functions.isValueNull((String) childInnerHash.get("payoutamount")))
                            {
                              payoutamount = (String) childInnerHash.get("payoutamount");
                            }
                            else
                            {
                              payoutamount = "";
                            }
                            String chargebackAmount = "";
                            if (functions.isValueNull((String) childInnerHash.get("chargebackamount")))
                            {
                              chargebackAmount = (String) childInnerHash.get("chargebackamount");
                            }
                            String date1 = ((String) childInnerHash.get("date"));
                            String timestamp = "";
                            if (functions.isValueNull((String) childInnerHash.get("timestamp")))
                            {
                              timestamp = (String) childInnerHash.get("timestamp");
                            }
                            else
                            {
                              timestamp = "";
                            }
                            String transStatus = (String) statushash.get((String) childInnerHash.get("status"));
                            String transStatusDetail = "Not Available";
                            String Eci = "";

                            if (functions.isValueNull((String) childInnerHash.get("ECI")))
                            {
                              Eci = (String) childInnerHash.get("ECI");
                            }
                            else
                            {
                              Eci = "-";
                            }

                            if (Functions.checkStringNull((String) childInnerHash.get("authqsiresponsecode")) != null)
                            {
                              transStatusDetail = (String) childInnerHash.get("authqsiresponsecode");
                            }
                            if ("JPY".equalsIgnoreCase(currency))
                            {
                              captureamount = functions.printNumber(Locale.US, captureamount);
                              refundamount = functions.printNumber(Locale.US, refundamount);
                              chargebackAmount = functions.printNumber(Locale.US, chargebackAmount);
                              payoutamount = functions.printNumber(Locale.US, payoutamount);
                            }
                            else if ("EUR".equalsIgnoreCase(currency))
                            {
                              captureamount = functions.printNumber(Locale.US, captureamount);
                              refundamount = functions.printNumber(Locale.US, refundamount);
                              chargebackAmount = functions.printNumber(Locale.US, chargebackAmount);
                              payoutamount = functions.printNumber(Locale.US, payoutamount);
                            }
                            else if ("GBP".equalsIgnoreCase(currency))
                            {
                              captureamount = functions.printNumber(Locale.US, captureamount);
                              refundamount = functions.printNumber(Locale.US, refundamount);
                              chargebackAmount = functions.printNumber(Locale.US, chargebackAmount);
                              payoutamount = functions.printNumber(Locale.US, payoutamount);
                              ;
                            }
                            else if ("USD".equalsIgnoreCase(currency))
                            {
                              captureamount = functions.printNumber(Locale.US, captureamount);
                              refundamount = functions.printNumber(Locale.US, refundamount);
                              chargebackAmount = functions.printNumber(Locale.US, chargebackAmount);
                              payoutamount = functions.printNumber(Locale.US, payoutamount);
                            }
                            out.println("<td data-label=\"Date of Transaction\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(date1) + "</td>");
                            out.println("<td data-label=\"Last Updated Date \" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(timestamp) + "</td>");
                            out.println("<td data-label=\"Tracking Id\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(trackingId) + "</td>");
                            out.println("<td data-label=\"Transaction Status\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(transStatus) + "</td>");
                            out.println("<td data-label=\"Transaction Remark\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(transStatusDetail) + "</td>");
                            out.println("<td data-label=\"Capture Amount\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(captureamount) + "</td>");
                            out.println("<td data-label=\"Refund Amount\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(refundamount) + "</td>");
                            out.println("<td data-label=\"Chargeback Amount\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(chargebackAmount) + "</td>");
                            out.println("<td data-label=\"Payout Amount\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(payoutamount) + "</td>");
                            out.println("<td data-label=\"ECI\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(Eci) + "</td>");
                            if (functions.isValueNull(Emi))
                            {
                              out.println("<td data-label=\"Installment\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(Emi) + "</td>");
                            }
                            if (functions.isValueNull(walletId))
                            {
                              out.println("<td data-label=\"Installment\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(walletId) + "</td>");
                            }
                            out.println("</tr>");
                          }
                        %>
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>
              </div>
              <%}
              }--%>
              <%
                String email = (String) innerHash.get("email");
                String ipaddress = (String) innerHash.get("ipaddress");
                String customerIPCountry="";
                String telnocc = (String) innerHash.get("telnocc");
                String telno = (String) innerHash.get("telno");
                String city = (String) innerHash.get("city");
                String street = (String) innerHash.get("street");
                String state = (String) innerHash.get("state");
                String country = (String) innerHash.get("country");
                String zip = (String) innerHash.get("zip");
                String customerId=(String) innerHash.get("customerId");
                if (functions.isValueNull(email) || functions.isValueNull(telnocc) || functions.isValueNull(telno)
                        || functions.isValueNull(city) || functions.isValueNull(street) || functions.isValueNull(state)
                        || functions.isValueNull(country) || functions.isValueNull(zip) ||functions.isValueNull(customerId))
                {

              %>
              <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                  <div class="widget">

                    <div class="widget-header transparent">
                      <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=transactionDetails_Customer%></strong></h2>
                      <div class="additional-btn">
                        <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                        <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                        <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                      </div>
                    </div>

                    <div class="widget-content padding" style="overflow-y: auto;">
                      <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                        <thead >
                        <tr style="color: white;">

                          <th style="text-align: center"><%=transactionDetails_Emailaddress%> </th>
                          <th style="text-align: center"><%=transactionDetails_Ipaddress%></th>
                          <th style="text-align: center">IP Country</th>
                          <th style="text-align: center"><%=transactionDetails_Phone_Number%></th>
                          <th style="text-align: center"><%=transactionDetails_City%></th>
                          <th style="text-align: center"><%=transactionDetails_Street%></th>
                          <th style="text-align: center"><%=transactionDetails_State%></th>
                          <th style="text-align: center"><%=transactionDetails_Country%></th>
                          <th style="text-align: center"><%=transactionDetails_post%></th>
                          <th style="text-align: center">Customer Id</th>
                        </tr>
                        </thead>
                        <tbody>
                        <%

                          email = (String) innerHash.get("email");
                          if (email == null) email = "-";
                                                    /*  ipaddress = (String) innerHash.get("ipaddress");*/
                          if (ipaddress == null) ipaddress = "-";
                          telnocc = (String) innerHash.get("telnocc");
                          if (telnocc == null) telnocc = "-";
                          telno = (String) innerHash.get("telno");
                          if (telno == null) telno = "-";
                          city = (String) innerHash.get("city");
                          if (city == null) city = "-";
                          street = (String) innerHash.get("street");
                          if (street == null) street = "-";
                          state = (String) innerHash.get("state");
                          if (state == null) state = "-";
                          country = (String) innerHash.get("country");
                          if (country == null) country = "-";
                          zip = (String) innerHash.get("zip");
                          if (zip == null) zip = "-";
                          customerIPCountry = functions.getIPCountryLong(ipaddress);
                          if(!functions.isValueNull(customerIPCountry))
                            customerIPCountry="-";
                          customerId = (String) innerHash.get("customerId");
                          if (customerId == null) customerId = "-";



                          out.println("<td data-label=\"Emailaddress\" style=\"text-align: center\">&nbsp;" + functions.getEmailMasking(email) +  "</td>");
                          out.println("<td data-label=\"ipAddress\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(ipaddress) +  "</td>");
                          out.println("<td data-label=\"customerIPCountry\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(customerIPCountry) +  "</td>");
                          out.println("<td data-label=\"Tel No\" style=\"text-align: center\">&nbsp;" + functions.getPhoneNumMasking(telno) +  "</td>");
                          out.println("<td data-label=\"City\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(city) +  "</td>");
                          out.println("<td data-label=\"Street\" style=\"text-align: center\">&nbsp;" + street +  "</td>");
                          out.println("<td data-label=\"State\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(state) +  "</td>");
                          out.println("<td data-label=\"Country\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(country) +  "</td>");
                          out.println("<td data-label=\"Pin Code\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(zip) +  "</td>");
                          out.println("<td data-label=\"customerId\" style=\"text-align: center\">&nbsp;" + ESAPI.encoder().encodeForHTML(customerId) +  "</td>");
                          out.println("</tr>");
                        %>
                        </tbody>
                      </table>
                    </div>
                  </div>
                </div>
              </div>
              <%}%>
            </div>
            <input type="hidden" value="<%=terminalBuffer.toString()%>" name="terminalbuffer">
          </div>
        </div>
      </div>
      <input type="hidden" value="<%=terminalBuffer.toString()%>" name="terminalbuffer">
</body>
</html>