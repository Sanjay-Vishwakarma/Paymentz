<%@ page import="com.directi.pg.*" %>
<%@ page import="com.directi.pg.Base64" %>
<%@ page import="com.manager.vo.fraudruleconfVOs.RuleMasterVO" %>
<%@ page import="com.payment.exceptionHandler.errorcode.ErrorCodeUtils" %>
<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName" %>
<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="top.jsp" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 10/15/13
  Time: 2:17 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
    String trackingid = Functions.checkStringNull(request.getParameter("trackingid"))==null?"":request.getParameter("trackingid");
    session.setAttribute("submit", "partnerTransactions");
    String fdate=null;
    String tdate=null;
    Date date = new Date();
    SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

    String Date = originalFormat.format(date);
    date.setDate(1);
    String fromDate = originalFormat.format(date);

    fdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDate : request.getParameter("fromdate");
    tdate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String partnerTransactionDetails_Transaction_Status = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Transaction_Status")) ? rb1.getString("partnerTransactionDetails_Transaction_Status") : "Transaction Status Flag";
    String partnerTransactionDetails_isSuccessful = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_isSuccessful")) ? rb1.getString("partnerTransactionDetails_isSuccessful") : "isSuccessful";
    String partnerTransactionDetails_isSettled = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_isSettled")) ? rb1.getString("partnerTransactionDetails_isSettled") : "isSettled";
    String partnerTransactionDetails_isRefund = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_isRefund")) ? rb1.getString("partnerTransactionDetails_isRefund") : "isRefund";
    String partnerTransactionDetails_isChargeback = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_isChargeback")) ? rb1.getString("partnerTransactionDetails_isChargeback") : "isChargeback";
    String partnerTransactionDetails_isFraud = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_isFraud")) ? rb1.getString("partnerTransactionDetails_isFraud") : "isFraud";
    String partnerTransactionDetails_isRollingReserveKept = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_isRollingReserveKept")) ? rb1.getString("partnerTransactionDetails_isRollingReserveKept") : "isRollingReserveKept";
    String partnerTransactionDetails_isRollingReserveReleased = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_isRollingReserveReleased")) ? rb1.getString("partnerTransactionDetails_isRollingReserveReleased") : "isRollingReserveReleased";
    String partnerTransactionDetails_Action_History = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Action_History")) ? rb1.getString("partnerTransactionDetails_Action_History") : "Action History";
    String partnerTransactionDetails_Srno = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Srno")) ? rb1.getString("partnerTransactionDetails_Srno") : "Sr no";
    String partnerTransactionDetails_Action = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Action")) ? rb1.getString("partnerTransactionDetails_Action") : "Action";
    String partnerTransactionDetails_Amount = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Amount")) ? rb1.getString("partnerTransactionDetails_Amount") : "Amount";
    String partnerTransactionDetails_Currency = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Currency")) ? rb1.getString("partnerTransactionDetails_Currency") : "Currency";
    String partnerTransactionDetails_ErrorCode = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_ErrorCode")) ? rb1.getString("partnerTransactionDetails_ErrorCode") : "Error Code";
    String partnerTransactionDetails_Error_Description = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Error_Description")) ? rb1.getString("partnerTransactionDetails_Error_Description") : "Error Description";
    String partnerTransactionDetails_Response_Transactionid = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Response_Transactionid")) ? rb1.getString("partnerTransactionDetails_Response_Transactionid") : "Response Transactionid";
    String partnerTransactionDetails_ARN = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_ARN")) ? rb1.getString("partnerTransactionDetails_ARN") : "ARN";
    String partnerTransactionDetails_Remark = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Remark")) ? rb1.getString("partnerTransactionDetails_Remark") : "Remark";
    String partnerTransactionDetails_Timestamp = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Timestamp")) ? rb1.getString("partnerTransactionDetails_Timestamp") : "Timestamp";
    String partnerTransactionDetails_Customer_IpAddress = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Customer_IpAddress")) ? rb1.getString("partnerTransactionDetails_Customer_IpAddress") : "Customer IpAddress";
    String partnerTransactionDetails_Billing_Descriptor = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Billing_Descriptor")) ? rb1.getString("partnerTransactionDetails_Billing_Descriptor") : "Billing Descriptor";
    String partnerTransactionDetails_Response_Code = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Response_Code")) ? rb1.getString("partnerTransactionDetails_Response_Code") : "Response Code";
    String partnerTransactionDetails_Response_Description = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Response_Description")) ? rb1.getString("partnerTransactionDetails_Response_Description") : "Response Description";
    String partnerTransactionDetails_Action_Executor = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Action_Executor")) ? rb1.getString("partnerTransactionDetails_Action_Executor") : "Action Executor";
    String partnerTransactionDetails_TMPL_Amount = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_TMPL_Amount")) ? rb1.getString("partnerTransactionDetails_TMPL_Amount") : "TMPL_Amount";
    String partnerTransactionDetails_TMPL_Currency = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_TMPL_Currency")) ? rb1.getString("partnerTransactionDetails_TMPL_Currency") : "TMPL_Currency";
    String partnerTransactionDetails_Wallet_Amount = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Wallet_Amount")) ? rb1.getString("partnerTransactionDetails_Wallet_Amount") : "Wallet_Amount";
    String partnerTransactionDetails_Wallet_Currency = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Wallet_Currency")) ? rb1.getString("partnerTransactionDetails_Wallet_Currency") : "Wallet_Currency";
    String partnerTransactionDetails_Order_Details = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Order_Details")) ? rb1.getString("partnerTransactionDetails_Order_Details") : "Order Details";
    String partnerTransactionDetails_Order_ID = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Order_ID")) ? rb1.getString("partnerTransactionDetails_Order_ID") : "Order ID";
    String partnerTransactionDetails_Order_Description = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Order_Description")) ? rb1.getString("partnerTransactionDetails_Order_Description") : "Order Description";
    String partnerTransactionDetails_Transaction_Amount = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Transaction_Amount")) ? rb1.getString("partnerTransactionDetails_Transaction_Amount") : "Transaction Amount";
    String partnerTransactionDetails_Template_Amount = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Template_Amount")) ? rb1.getString("partnerTransactionDetails_Template_Amount") : "Template Amount";
    String partnerTransactionDetails_Notification_URL = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Notification_URL")) ? rb1.getString("partnerTransactionDetails_Notification_URL") : "Notification URL";
    String partnerTransactionDetails_Payment_Mode = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Payment_Mode")) ? rb1.getString("partnerTransactionDetails_Payment_Mode") : "Payment Mode";
    String partnerTransactionDetails_Payment_Brand = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Payment_Brand")) ? rb1.getString("partnerTransactionDetails_Payment_Brand") : "Payment Brand";
    String partnerTransactionDetails_Terminal_ID = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Terminal_ID")) ? rb1.getString("partnerTransactionDetails_Terminal_ID") : "Terminal ID";
    String partnerTransactionDetails_Transaction_Details = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Transaction_Details")) ? rb1.getString("partnerTransactionDetails_Transaction_Details") : "Transaction Details";
    String partnerTransactionDetails_Date_Transaction = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Date_Transaction")) ? rb1.getString("partnerTransactionDetails_Date_Transaction") : "Date of Transaction";
    String partnerTransactionDetails_Last_Updated = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Last_Updated")) ? rb1.getString("partnerTransactionDetails_Last_Updated") : "Last Updated Date";
    String partnerTransactionDetails_TrackingID = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_TrackingID")) ? rb1.getString("partnerTransactionDetails_TrackingID") : "Tracking ID";
    String partnerTransactionDetails_Transaction_Status1 = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Transaction_Status1")) ? rb1.getString("partnerTransactionDetails_Transaction_Status1") : "Transaction Status";
    String partnerTransactionDetails_Transaction_Remark = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Transaction_Remark")) ? rb1.getString("partnerTransactionDetails_Transaction_Remark") : "Transaction Remark";
    String partnerTransactionDetails_Capture_Amount = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Capture_Amount")) ? rb1.getString("partnerTransactionDetails_Capture_Amount") : "Capture Amount";
    String partnerTransactionDetails_Refund_Amount = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Refund_Amount")) ? rb1.getString("partnerTransactionDetails_Refund_Amount") : "Refund Amount";
    String partnerTransactionDetails_Chargeback_Amount = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Chargeback_Amount")) ? rb1.getString("partnerTransactionDetails_Chargeback_Amount") : "Chargeback Amount";
    String partnerTransactionDetails_Payout_Amount = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Payout_Amount")) ? rb1.getString("partnerTransactionDetails_Payout_Amount") : "Payout Amount";
    String partnerTransactionDetails_Bank_ID = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Bank_ID")) ? rb1.getString("partnerTransactionDetails_Bank_ID") : "Bank ID";
    String partnerTransactionDetails_ECI = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_ECI")) ? rb1.getString("partnerTransactionDetails_ECI") : "ECI";
    String partnerTransactionDetails_Installment = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Installment")) ? rb1.getString("partnerTransactionDetails_Installment") : "Installment";
    String partnerTransactionDetails_WalletId = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_WalletId")) ? rb1.getString("partnerTransactionDetails_WalletId") : "WalletId";
    String partnerTransactionDetails_Fraud_Score = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Fraud_Score")) ? rb1.getString("partnerTransactionDetails_Fraud_Score") : "Fraud Score";
    String partnerTransactionDetails_Fraud_Remark = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Fraud_Remark")) ? rb1.getString("partnerTransactionDetails_Fraud_Remark") : "Fraud Remark";
    String partnerTransactionDetails_Customer_Details = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Customer_Details")) ? rb1.getString("partnerTransactionDetails_Customer_Details") : "Customer's Details";
    String partnerTransactionDetails_Emailaddress = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Emailaddress")) ? rb1.getString("partnerTransactionDetails_Emailaddress") : "Emailaddress";
    String partnerTransactionDetails_Ipaddress = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Ipaddress")) ? rb1.getString("partnerTransactionDetails_Ipaddress") : "Ipaddress";
    String partnerTransactionDetails_IP_Country = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_IP_Country")) ? rb1.getString("partnerTransactionDetails_IP_Country") : "IP Country";
    String partnerTransactionDetails_Tel_No = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Tel_No")) ? rb1.getString("partnerTransactionDetails_Tel_No") : "Tel No";
    String partnerTransactionDetails_City = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_City")) ? rb1.getString("partnerTransactionDetails_City") : "City";
    String partnerTransactionDetails_Street = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Street")) ? rb1.getString("partnerTransactionDetails_Street") : "Street";
    String partnerTransactionDetails_State = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_State")) ? rb1.getString("partnerTransactionDetails_State") : "State";
    String partnerTransactionDetails_Country = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Country")) ? rb1.getString("partnerTransactionDetails_Country") : "Country";
    String partnerTransactionDetails_post = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_post")) ? rb1.getString("partnerTransactionDetails_post") : "Post/Zip Code";
    String partnerTransactionDetails_CustomerId = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_CustomerId")) ? rb1.getString("partnerTransactionDetails_CustomerId") : "Customer Id";
    String partnerTransactionDetails_Card_Details = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Card_Details")) ? rb1.getString("partnerTransactionDetails_Card_Details") : "Card Details";
    String partnerTransactionDetails_Cardname = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Cardname")) ? rb1.getString("partnerTransactionDetails_Cardname") : "Cardholder's Name";
    String partnerTransactionDetails_First_Six = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_First_Six")) ? rb1.getString("partnerTransactionDetails_First_Six") : "First Six";
    String partnerTransactionDetails_Last_Four = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Last_Four")) ? rb1.getString("partnerTransactionDetails_Last_Four") : "Last Four";
    String partnerTransactionDetails_merchant_Details = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_merchant_Details")) ? rb1.getString("partnerTransactionDetails_merchant_Details") : "Merchant Details";
    String partnerTransactionDetails_MemberID = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_MemberID")) ? rb1.getString("partnerTransactionDetails_MemberID") : "Member ID";
    String partnerTransactionDetails_MerchantName = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_MerchantName")) ? rb1.getString("partnerTransactionDetails_MerchantName") : "Merchant Name";
    String partnerTransactionDetails_Merchant_Email = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Merchant_Email")) ? rb1.getString("partnerTransactionDetails_Merchant_Email") : "Merchant Email";
    String partnerTransactionDetails_Redirect_URL = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Redirect_URL")) ? rb1.getString("partnerTransactionDetails_Redirect_URL") : "Redirect URL";
    String partnerTransactionDetails_Merchant_IP = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Merchant_IP")) ? rb1.getString("partnerTransactionDetails_Merchant_IP") : "Merchant IP";
    String partnerTransactionDetails_Merchant_IP_country = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Merchant_IP_country")) ? rb1.getString("partnerTransactionDetails_Merchant_IP_country") : "Merchant IP Country";
    String partnerTransactionDetails_Fraud_Rule = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Fraud_Rule")) ? rb1.getString("partnerTransactionDetails_Fraud_Rule") : "Fraud Rules Triggered";
    String partnerTransactionDetails_Fraud_Rule_name = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Fraud_Rule_name")) ? rb1.getString("partnerTransactionDetails_Fraud_Rule_name") : "Fraud Rule Name";
    String partnerTransactionDetails_Status = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_Status")) ? rb1.getString("partnerTransactionDetails_Status") : "Status";
    String partnerTransactionDetails_FraudScore = StringUtils.isNotEmpty(rb1.getString("partnerTransactionDetails_FraudScore")) ? rb1.getString("partnerTransactionDetails_FraudScore") : "Fraud Rule Score";
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
%>
<%
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (partner.isLoggedInPartner(session))
    {
        TransactionEntry transactionentry = new TransactionEntry();
        SortedMap statushash = transactionentry.getSortedMap();
        Functions functions = new Functions();
        byte[] transactionReceiptImg=null;
%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
</head>
<body>
<form name="form" method="post" action="PartnerTransaction?ctoken=<%=ctoken%>">
    <div class="content-page">
        <div class="content">
            <!-- Page Heading Start -->
            <div class="page-heading">
                <div class="pull-right">
                    <div class="btn-group">
                        <form action="partner/net/PartnerTransaction?ctoken=<%=ctoken%>" method="post" name="form">
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
                            <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
                                <img style="height: 35px;" src="/partner/images/goBack.png">
                            </button>
                        </form>
                    </div>
                </div>
                <br><br><br>
                <%String message="";
                    if(request.getAttribute("message")!=null)
                        message=(String)request.getAttribute("message");
                if(functions.isValueNull(message)){
                %>
                <div class="page-heading">
                    <div>
                        <div class="widget-header">
                            <h5 class="bg-infoorange" style="text-align: center;"><i class="fa fa-exclamation-triangle"></i>&nbsp;&nbsp;<%=message%></h5>
                        </div>
                    </div>
                    <%}%>
                <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                <%

                    int records = 0;

                    String cardtypeId = (String) request.getAttribute("cardtypeid");
                    Hashtable actionHistory = (Hashtable) request.getAttribute("actionHistory");
                    ActionEntry entry = new ActionEntry();
                    Hashtable statusmap = entry.getStatusHash();
                    Hashtable temphash = null;

                    try
                    {
                        records = Integer.parseInt((String) actionHistory.get("records"));

                    }
                    catch (Exception ex)
                    {

                    }

                    String style = "class=td0";
                    String ext = "light";

                    if (records > 0)
                    {
                %>

                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <%
                                HashMap transactionDetails = (HashMap) request.getAttribute("transactionsDetails");
                                HashMap innerHash = (HashMap) transactionDetails.get("1");// Since we'll get details of one transaction only
                                if (innerHash != null)
                                {
                            %>
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerTransactionDetails_Transaction_Status%></strong></h2>
                            </div>
                            <div class="widget-content padding" style="overflow-x: auto;">

                                <table align=center width="50%"
                                       class="display table table table-striped table-bordered table-hover dataTable"
                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_isSuccessful%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_isSettled%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_isRefund%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_isChargeback%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_isFraud%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_isRollingReserveKept%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_isRollingReserveReleased%></b></td>
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
                    </div>
                </div>
                <%

                %>

                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerTransactionDetails_Action_History%></strong></h2>
                                <div class="pull-right">
                                    <div class="btn-group">
                                        <form action="/partner/net/ExportActionHistoryByPartner?ctoken=<%=ctoken%>" method="post" >
                                            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(String.valueOf(trackingid))%>" name="trackingid">
                                            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" name="fromdate">
                                            <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" name="todate">
                                           <%-- <button class="btn-xs" type="submit" style="background: transparent;border: 0;">
                                                <img style="height: 40px;" src="/merchant/images/excel.png">
                                            </button>--%>
                                        </form>
                                    </div>
                                </div>
                            </div>
                            <div class="widget-content padding" style="overflow-x: auto;">

                                <table align=center width="50%"
                                       class="display table table table-striped table-bordered table-hover dataTable"
                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Srno%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Action%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Amount%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Currency%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_ErrorCode%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Error_Description%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Response_Transactionid%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_ARN%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Remark%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Timestamp%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Customer_IpAddress%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Billing_Descriptor%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Response_Code%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Response_Description%></b></td>
                                        <%
                                            if ("22".equals(cardtypeId))
                                            {
                                        %>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Verification Code</b></td>
                                        <%
                                            }
                                        %>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Action_Executor%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_TMPL_Amount%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_TMPL_Currency%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Wallet_Amount%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Wallet_Currency%></b></td>


                                    </tr>
                                    </thead>
                                    <%

                                        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
                                        ErrorCodeVO errorCodeVO = new ErrorCodeVO();
                                        int size=actionHistory.size();
                                        int pos = 1;
                                        for(int i=size-2;i>0;i--)
                                        {
                                            System.out.println("key::::" + i);
                                            style = "class=\"tr" + pos % 2 + "\"";
                                        /*for (int pos = 1; pos <= records; pos++)
                                        {*/
                                            String id = Integer.toString(pos);
                                            style = "class=\"tr" + (pos + 1) % 2 + "\"";

                                            temphash = (Hashtable) actionHistory.get(id);

                                            String date1 = (String) temphash.get("timestamp");
                                            String action = "";
                                            if (functions.isValueNull((String) temphash.get("action")))
                                            {
                                                action = (String) temphash.get("action");
                                            }
                                            else
                                            {
                                                action = "-";
                                            }
                                            String remark = (String) temphash.get("remark");
                                            String amount = (String) temphash.get("amount");
                                            String templateamount = (String) temphash.get("templateamount");
                                            String currency = (String) temphash.get("currency");
                                            String templatecurrency = (String) temphash.get("templatecurrency");
                                            String responseInfo = (String) temphash.get("responsehashinfo");
                                            String arn = (String) temphash.get("arn");
                                            String responseTransactionid = (String) temphash.get("responsetransactionid");
                                            String walletAmount=(String)temphash.get("walletAmount");
                                            String walletCurrency=(String)temphash.get("walletCurrency");
                                            String ipAddress=(String)temphash.get("ipAddress");
                                            if (!functions.isValueNull(responseTransactionid))
                                                responseTransactionid = "-";
                                            if (!functions.isValueNull(arn))
                                                arn = "-";
                                            if (!functions.isValueNull(remark))
                                                remark = "-";
                                            if (!functions.isValueNull(amount))
                                                amount = "-";
                                            if (!functions.isValueNull(templateamount))
                                                templateamount = "-";
                                            if (!functions.isValueNull(currency))
                                                currency = "-";
                                            if (!functions.isValueNull(walletAmount))
                                                walletAmount = "-";
                                            if (!functions.isValueNull(walletCurrency))
                                                walletCurrency = "-";
                                            if (!functions.isValueNull(templatecurrency))
                                                templatecurrency = "-";
                                            if (!functions.isValueNull(responseInfo))
                                                responseInfo = "-";
                                            if (!functions.isValueNull(ipAddress))
                                                ipAddress = "-";

                                            String responseCode = (String) temphash.get("responsecode");
                                            if (!functions.isValueNull(responseCode))
                                                responseCode = "-";
                                            String billingDescriptor = (String) temphash.get("responsedescriptor");
                                            if (!functions.isValueNull(billingDescriptor)) billingDescriptor = "-";
                                            String responseDescriptor = (String) temphash.get("responsedescription");
                                            if (!functions.isValueNull(responseDescriptor)) responseDescriptor = "-";
                                            String actionexename = (String) temphash.get("actionexecutorname");
                                            if (!functions.isValueNull(actionexename))
                                                actionexename = "-";
                                            if (temphash.get("transactionReceiptImg") != null)
                                            {
                                                transactionReceiptImg = (byte[]) temphash.get("transactionReceiptImg");
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
                                            out.println("<tr " + style + ">");
                                            out.println("<td valgn=\"middle\" data-label=\"Sr no\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(String.valueOf(pos)) + "</td>");
                                            out.println("<td valgn=\"middle\" data-label=\"Action\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(action) + "</td>");
                                            out.println("<td valgn=\"middle\" data-label=\"Amount\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(amount) + "</td>");
                                            out.println("<td valgn=\"middle\" data-label=\"Currency\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(currency) + "</td>");
                                            out.println("<td valgn=\"middle\" data-label=\"Error Code\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(errorCode) + "</td>");
                                            out.println("<td valgn=\"middle\" data-label=\"Error Description\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(errorDescription) + "</td>");
                                            out.println("<td valgn=\"middle\" data-label=\"Response Transactionid\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(responseTransactionid) + "</td>");
                                            out.println("<td valgn=\"middle\" data-label=\"Arn\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(arn) + "</td>");
                                            out.println("<td valgn=\"middle\" data-label=\"Remark\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(remark) + "</td>");
                                            out.println("<td valign=\"middle\" data-label=\"Timestamp\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(date1) + "</td>");
                                            out.println("<td valign=\"middle\" data-label=\"ipAddress\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(ipAddress) + "</td>");


                                            if (billingDescriptor == null || billingDescriptor.equals(""))

                                            {
                                                out.println("<td valign=\"middle\" data-label=\"Billing Descriptor\" align=\"center\"" + style + ">" + "-" + "</td>");
                                            }
                                            else
                                            {
                                                out.println("<td valign=\"middle\" data-label=\"Billing Descriptor\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(billingDescriptor) + "</td>");
                                            }
                                            out.println("<td valign=\"middle\" data-label=\"responseCode\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(responseCode) + "</td>");
                                            out.println("<td valign=\"middle\" data-label=\"Response Description\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(responseDescriptor) + "</td>");

                                            if ("22".equals(cardtypeId))
                                            {
                                                out.println("<td valign=\"middle\" data-label=\"Verification Code\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(responseInfo) + "</td>");
                                            }
                                            out.println("<td valign=\"middle\" data-label=\"Action Executor\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(actionexename) + "</td>");
                                            out.println("<td valgn=\"middle\" data-label=\"Templateamount\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(templateamount) + "</td>");
                                            out.println("<td valgn=\"middle\" data-label=\"Templatecurrency\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(templatecurrency) + "</td>");
                                            out.println("<td valgn=\"middle\" data-label=\"Wallet Amount\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(walletAmount) + "</td>");
                                            out.println("<td valgn=\"middle\" data-label=\"Wallet Currency\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(walletCurrency) + "</td>");

                                            out.println("</tr>");
                                            pos++;
                                        }
                                    %>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                <%
                    }

                %>

                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <%
                                HashMap transactionDetails = (HashMap) request.getAttribute("transactionsDetails");
                                HashMap innerHash = (HashMap) transactionDetails.get("1");// Since we'll get details of one transaction only
                                if (innerHash != null)
                                {
                            %>
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerTransactionDetails_Order_Details%></strong></h2>
                            </div>
                            <%
                                String orderId = "";
                                if (functions.isValueNull((String) innerHash.get("description")))
                                {
                                    orderId = (String) innerHash.get("description");
                                }
                                else
                                {
                                    orderId = "-";
                                }
                                String orderDescription = "";
                                if (functions.isValueNull((String) innerHash.get("orderdescription")))
                                {
                                    orderDescription = (String) innerHash.get("orderdescription");
                                }
                                else
                                {
                                    orderDescription = "-";
                                }

                                String transactionAmout = "";
                                if (functions.isValueNull((String) innerHash.get("amount")))
                                {
                                    transactionAmout = (String) innerHash.get("amount");
                                }
                                else
                                {
                                    transactionAmout = "-";
                                }
                                String currency1 = "";
                                if (functions.isValueNull((String) innerHash.get("currency")))
                                {
                                    currency1 = (String) innerHash.get("currency");
                                }
                                else
                                {
                                    currency1 = "";
                                }
                                String templateCurrency = "";
                                if (functions.isValueNull((String) innerHash.get("templatecurrency")))
                                {
                                    templateCurrency = (String) innerHash.get("templatecurrency");
                                }
                                else
                                {
                                    templateCurrency = "";
                                }

                                String templateAmount = "";
                                if (functions.isValueNull((String) innerHash.get("templateamount")))
                                {
                                    templateAmount = (String) innerHash.get("templateamount");
                                }
                                else
                                {
                                    templateAmount = "";
                                }

                                String paymentMode = "";
                                if (functions.isValueNull((String) innerHash.get("paymodeid")))
                                {
                                    paymentMode = (String) innerHash.get("paymodeid");
                                }
                                else
                                {
                                    paymentMode = "-";
                                }

                                String paymentBrand = "";
                                if (functions.isValueNull((String) innerHash.get("cardtype")))
                                {
                                    paymentBrand = (String) innerHash.get("cardtype");
                                }
                                else
                                {
                                    paymentBrand = "-";
                                }

                                String notificationUrl = "";
                                if (functions.isValueNull((String) innerHash.get("notificationUrl")))
                                {
                                    notificationUrl = (String) innerHash.get("notificationUrl");
                                }
                                else
                                {
                                    notificationUrl = "-";
                                }
                                String terminalId = "";

                                if (functions.isValueNull((String) innerHash.get("terminalid")))
                                {
                                    terminalId = (String) innerHash.get("terminalid");
                                }
                                else
                                {
                                    terminalId = "-";
                                }
                                if ("JPY".equalsIgnoreCase(currency1))
                                {
                                    transactionAmout = functions.printNumber(Locale.JAPAN, transactionAmout);
                                }
                                else if ("EUR".equalsIgnoreCase(currency1))
                                {
                                    transactionAmout = functions.printNumber(Locale.FRANCE, transactionAmout);
                                }
                                else if ("GBP".equalsIgnoreCase(currency1))
                                {
                                    transactionAmout = functions.printNumber(Locale.UK, transactionAmout);
                                }
                                else if ("USD".equalsIgnoreCase(currency1))
                                {
                                    transactionAmout = functions.printNumber(Locale.US, transactionAmout);
                                }

                                if ("JPY".equalsIgnoreCase(templateCurrency))
                                {
                                    templateAmount = functions.printNumber(Locale.JAPAN, templateAmount);
                                }
                                else if ("EUR".equalsIgnoreCase(templateCurrency))
                                {
                                    templateAmount = functions.printNumber(Locale.FRANCE, templateAmount);
                                }
                                else if ("GBP".equalsIgnoreCase(templateCurrency))
                                {
                                    templateAmount = functions.printNumber(Locale.UK, templateAmount);
                                }
                                else if ("USD".equalsIgnoreCase(templateCurrency))
                                {
                                    templateAmount = functions.printNumber(Locale.US, templateAmount);
                                }
                                String trackingId = (String) innerHash.get("icicitransid");
                                String transStatus = (String) innerHash.get("status");
                                String memberid = (String) innerHash.get("memberid");
                            %>
                            <div class="widget-content padding" style="overflow-x: auto;">

                                <table align=center width="50%"
                                       class="display table table table-striped table-bordered table-hover dataTable"
                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Order_ID%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Order_Description%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Transaction_Amount%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Template_Amount%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Notification_URL%></b></td>
                                        <%--<td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Redirect Url</b></td>--%>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Payment_Mode%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Payment_Brand%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Terminal_ID%></b></td>

                                    </tr>
                                    </thead>


                                    <tr>
                                        <td class="tr" align="center" data-label="Order ID"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(orderId)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Order Descriptiont"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(orderDescription)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Transaction Amount"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(currency1 + " " + transactionAmout)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Template Amount"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(templateCurrency + " " + templateAmount)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Notification Url"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(notificationUrl)%>
                                        </td>
                                        <%--<td class="tr" align="center" data-label="Redirect Url" valign="middle"><%=ESAPI.encoder().encodeForHTML(redirectUlr)%></td>--%>
                                        <td class="tr" align="center" data-label="Payment Mode"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(paymentMode)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Payment Brand"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(paymentBrand)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Terminal Id"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(terminalId)%>
                                        </td>

                                    </tr>
                                </table>
                                <div class="widget-content padding" style="overflow-x: auto;" align="center">
                                    <%if(notificationUrl!="" && notificationUrl!="-" && !"authstarted".equalsIgnoreCase(transStatus) && !"begun".equalsIgnoreCase(transStatus) && !"payoutstarted".equalsIgnoreCase(transStatus)){%>
                                        <form action="/partner/net/SendNotification?ctoken=<%=ctoken%>" method="post">
                                            <input type="hidden" name="trackingid" value="<%=trackingid%>">
                                            <input type="hidden" name="memberid" value="<%=memberid%>">
                                            <input type="hidden" name="cardpresent" value="">
                                            <button type="submit" class="btn btn-default">
                                                Resend Notification
                                            </button>
                                        </form>
                                    <%}%>
                                </div>

                            </div>
                        </div>
                    </div>
                </div>

                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerTransactionDetails_Transaction_Details%></strong></h2>
                            </div>
                            <%
                                trackingId = (String) innerHash.get("icicitransid");
                                String currency = (String) innerHash.get("currency");
                                String date2 = (String) innerHash.get("date");
                                String timestamp = "";
                                if (functions.isValueNull(((String) innerHash.get("timestamp"))))
                                {
                                    timestamp = ((String) innerHash.get("timestamp"));
                                }
                                else
                                {
                                    timestamp = "-";
                                }
                                String refundamount = "";
                                if (functions.isValueNull(((String) innerHash.get("refundamount"))))
                                {
                                    refundamount = ((String) innerHash.get("refundamount"));
                                }
                                else
                                {
                                    refundamount = "-";
                                }
                                String captureamount = "";
                                if (functions.isValueNull(((String) innerHash.get("captureamount"))))
                                {
                                    captureamount = ((String) innerHash.get("captureamount"));
                                }
                                else
                                {
                                    captureamount = "-";
                                }
                                String payoutamount = "";
                                if (functions.isValueNull(((String) innerHash.get("payoutamount"))))
                                {
                                    payoutamount = ((String) innerHash.get("payoutamount"));
                                }
                                else
                                {
                                    payoutamount = "-";
                                }
                                String chargebackAmount = "";
                                if (functions.isValueNull(((String) innerHash.get("chargebackamount"))))
                                {
                                    chargebackAmount = ((String) innerHash.get("chargebackamount"));
                                }
                                else
                                {
                                    chargebackAmount = "-";
                                }
                                String Eci = "";
                                if (functions.isValueNull((String) innerHash.get("ECI")))
                                {
                                    Eci = (String) innerHash.get("ECI");
                                }
                                else
                                {
                                    Eci = "-";
                                }

                                String Emi = "";
                                if (functions.isValueNull((String) innerHash.get("Emi")))
                                {
                                    Emi = (String) innerHash.get("Emi");
                                }
                                String walletId = "";
                                if (functions.isValueNull((String) innerHash.get("walletId")))
                                {
                                    walletId = (String) innerHash.get("walletId");
                                }
                                String paymentid = "";
                                if (functions.isValueNull((String) innerHash.get("paymentid")))
                                {
                                    paymentid = (String) innerHash.get("paymentid");
                                }
                                else
                                {
                                    paymentid = "-";
                                }
                                transStatus = (String) statushash.get(innerHash.get("status"));
                                String transStatusDetail = "Not Available";
                                if (Functions.checkStringNull((String) innerHash.get("authqsiresponsecode")) != null)
                                {
                                    transStatusDetail = (String) innerHash.get("authqsiresponsecode");
                                }

                                String fraudscore = (String) transactionDetails.get("fraudscore");
                                String fraudremark = (String) transactionDetails.get("fraudremark");
                                if ("JPY".equalsIgnoreCase(currency))
                                {
                                    captureamount = functions.printNumber(Locale.JAPAN, captureamount);
                                    refundamount = functions.printNumber(Locale.JAPAN, refundamount);
                                    chargebackAmount = functions.printNumber(Locale.JAPAN, chargebackAmount);
                                    payoutamount = functions.printNumber(Locale.JAPAN, payoutamount);
                                }
                                else if ("EUR".equalsIgnoreCase(currency))
                                {
                                    captureamount = functions.printNumber(Locale.FRANCE, captureamount);
                                    refundamount = functions.printNumber(Locale.FRANCE, refundamount);
                                    chargebackAmount = functions.printNumber(Locale.FRANCE, chargebackAmount);
                                    payoutamount = functions.printNumber(Locale.FRANCE, payoutamount);
                                }
                                else if ("GBP".equalsIgnoreCase(currency))
                                {
                                    captureamount = functions.printNumber(Locale.UK, captureamount);
                                    refundamount = functions.printNumber(Locale.UK, refundamount);
                                    chargebackAmount = functions.printNumber(Locale.UK, chargebackAmount);
                                    payoutamount = functions.printNumber(Locale.UK, payoutamount);
                                }
                                else if ("USD".equalsIgnoreCase(currency))
                                {
                                    captureamount = functions.printNumber(Locale.US, captureamount);
                                    refundamount = functions.printNumber(Locale.US, refundamount);
                                    chargebackAmount = functions.printNumber(Locale.US, chargebackAmount);
                                    payoutamount = functions.printNumber(Locale.US, payoutamount);
                                }
                            %>
                            <div class="widget-content padding" style="overflow-x: auto;">

                                <table align=center width="50%"
                                       class="display table table table-striped table-bordered table-hover dataTable"
                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Date_Transaction%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Last_Updated%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_TrackingID%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Transaction_Status1%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Transaction_Remark%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Capture_Amount%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Refund_Amount%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Chargeback_Amount%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Payout_Amount%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Bank_ID%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_ECI%></b></td>
                                        <%
                                            if (functions.isValueNull(Emi))
                                            {
                                        %>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Installment%></b></td>
                                        <%
                                            }
                                        %>
                                        <%
                                            if (functions.isValueNull(walletId))
                                            {
                                        %>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_WalletId%></b></td>
                                        <%
                                            }
                                        %>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Fraud_Score%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Fraud_Remark%></b></td>
                                        <%--<td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Notification Url</b></td>--%>

                                    </tr>
                                    </thead>


                                    <tr>
                                        <td class="tr" align="center" data-label="Date of Transaction"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(date2)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Last Updated Date"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(timestamp)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Tracking ID"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(trackingId)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Transaction Status"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(transStatus)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Transaction Remark"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(transStatusDetail)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Capture Amount"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(captureamount)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Refund Amount"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(refundamount)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Chargeback Amount"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(chargebackAmount)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Payout Amount"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(payoutamount)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Bank ID"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(paymentid)%>
                                        </td>
                                        <td class="tr" align="center" data-label="ECI"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(Eci)%>
                                        </td>
                                        <%
                                            if (functions.isValueNull(Emi))
                                            {
                                        %>
                                        <td class="tr" align="center" data-label="Installment"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(Emi)%>
                                        </td>
                                        <%
                                            }
                                        %>
                                        <%
                                            if (functions.isValueNull(walletId))
                                            {
                                        %>
                                        <td class="tr" align="center" data-label="WalletId"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(walletId)%>
                                        </td>
                                        <%
                                            }
                                        %>
                                        <td class="tr" align="center" data-label="Fraud Score"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(fraudscore)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Fraud Remark"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(fraudremark)%>
                                        </td>
                                    </tr>

                                </table>

                            </div>
                        </div>
                    </div>
                </div>
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
                    String customerId = (String) innerHash.get("customerId");
                    if (functions.isValueNull(email) || functions.isValueNull(telnocc) || functions.isValueNull(telno)
                            || functions.isValueNull(city) || functions.isValueNull(street) || functions.isValueNull(state)
                            || functions.isValueNull(country) || functions.isValueNull(zip) || functions.isValueNull(customerId))
                    {

                %>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">

                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerTransactionDetails_Customer_Details%></strong></h2>
                            </div>
                            <%

                                email = (String) innerHash.get("email");
                                if (email == null) email = "-";
                                ipaddress = (String) innerHash.get("ipaddress");
                                if (ipaddress == null) {
                                    ipaddress = "-";
                                }else
                                {
                                    customerIPCountry = functions.getIPCountryLong(ipaddress);
                                    if(!functions.isValueNull(customerIPCountry))
                                        customerIPCountry="-";
                                }
                                telnocc = (String) innerHash.get("telnocc");
                                if (telnocc == null) telnocc = "-";
                                telno = (String) innerHash.get("telno");
                                if (telno == null) telno = "-";
                                city = (String) innerHash.get("city");
                                if (city == null) city = "-";
                                street = (String) innerHash.get("street");
                                if (street == null) street = "-";
                                zip = (String) innerHash.get("zip");
                                if (zip == null) zip = "-";
                                state = (String) innerHash.get("state");
                                if (state == null) state = "-";
                                country = (String) innerHash.get("country");
                                if (country == null) country = "-";
                                customerId = (String) innerHash.get("customerId");
                                if (customerId == null) customerId = "-";
                                String print_email = " ";
                                String phone_no=" ";
                                String print_vpaAddress="";
                                if(Roles.contains("superpartner")){
                                    print_email =email;
                                    phone_no =telno;
                                    print_vpaAddress=customerId;

                                }else{

                                    print_email=functions.getEmailMasking(email);
                                    phone_no=functions.getPhoneNumMasking(telno);
                                    print_vpaAddress=functions.maskVpaAddress(customerId);
                                }

                            %>
                            <div class="widget-content padding" style="overflow-x: auto;">

                                <table align=center width="50%"
                                       class="display table table table-striped table-bordered table-hover dataTable"
                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr>

                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Emailaddress%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Ipaddress%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_IP_Country%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Tel_No%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_City%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Street%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_State%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Country%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_post%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_CustomerId%></b></td>
                                    </tr>
                                    </thead>
                                    <tr>

                                        <td class="tr" align="center" data-label="Customer's Email Address"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(print_email)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Customer's IP Address"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(ipaddress)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Customer's IP Country"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(customerIPCountry)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Tel No"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(phone_no)%>
                                        </td>
                                        <td class="tr" align="center" data-label="City"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(city)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Street"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(street)%>
                                        </td>
                                        <td class="tr" align="center" data-label="State"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(state)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Country"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(country)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Pin Code"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(zip)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Pin Code"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(print_vpaAddress)%>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                <%
                    }

                    String cardNum = (String) innerHash.get("ccnum");
                    String name = (String) innerHash.get("name");
                    String bin_card_type = (String) innerHash.get("bin_card_type");
                    String bin_card_category = (String) innerHash.get("bin_card_category");
                    //String CardExpired= (String)innerHash.get("cardExpired");
                    String CardExpired= "";
                    if (functions.isValueNull(cardNum) || functions.isValueNull(name) || functions.isValueNull(bin_card_type) || functions.isValueNull(bin_card_category) || functions.isValueNull(CardExpired))
                    {
                %>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerTransactionDetails_Card_Details%></strong></h2>
                            </div>
                            <%
                                //cardNum = (String) innerHash.get("ccnum");
                                name = (String) innerHash.get("name");
                                if (name == null) name = "-";

                                String firstSix = "";
                                String lastFour = "";
                                bin_card_type = (String) innerHash.get("bin_card_type");
                                bin_card_category = (String) innerHash.get("bin_card_category");
                                /*if (functions.isValueNull(cardNum))
                                {
                                 */   /*firstSix = cardNum.substring(0, 6);
                                    lastFour = cardNum.substring((cardNum.length() - 4), cardNum.length());*/
                                    //using from bin details
                                    firstSix = (String) innerHash.get("first_six");
                                    lastFour = (String) innerHash.get("last_four");
                                if(!functions.isValueNull(firstSix))
                                {
                                    firstSix = "-";

                                }
                                if(!functions.isValueNull(lastFour)){
                                    lastFour = "-";
                                }
                               // CardExpired= (String) innerHash.get("cardExpired");
                                CardExpired= "";
                                if (CardExpired== null) CardExpired = "-";
                            %>
                            <div class="widget-content padding" style="overflow-x: auto;">

                                <table align=center width="50%"
                                       class="display table table table-striped table-bordered table-hover dataTable"
                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Cardname%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_First_Six%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Last_Four%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Card Expired</b></td>
                                    </tr>
                                    </thead>


                                    <tr>
                                        <td class="tr" align="center" data-label="Cardholder's Name"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(name)%>
                                        </td>
                                        <td class="tr" align="center" data-label="First Six"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(firstSix)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Last Four"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(lastFour)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Card Expired"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(CardExpired)%>
                                        </td>
                                    </tr>
                                </table>

                            </div>
                        </div>
                    </div>
                </div>
                <%
                    }
                    String bin_brand = (String) innerHash.get("bin_brand");
                    String card_country = (String) innerHash.get("country_name");
                    String cardIssuingBank = (String) innerHash.get("issuing_bank");
                    String bin_usage_type = (String) innerHash.get("bin_usage_type");
                    String country_name = (String) innerHash.get("country_name");
                    if (functions.isValueNull(bin_brand) || functions.isValueNull(card_country) || functions.isValueNull(cardIssuingBank))
                    {
                %>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">

                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Bin Details</strong></h2>
                            </div>
                            <%
                                bin_brand = (String) innerHash.get("bin_brand");
                                if (bin_brand == null) bin_brand = "-";
                                card_country = (String) innerHash.get("country_name");
                                if (card_country == null) card_country = "-";
                                cardIssuingBank = (String) innerHash.get("issuing_bank");
                                if (cardIssuingBank == null) cardIssuingBank = "-";

                                if (!functions.isValueNull(bin_brand))
                                    bin_brand = "-";

                                if (!functions.isValueNull(card_country))
                                    card_country = "-";

                                if (!functions.isValueNull(cardIssuingBank))
                                    cardIssuingBank = "-";

                                if (!functions.isValueNull(bin_card_type))
                                    bin_card_type = "-";

                                if (!functions.isValueNull(bin_card_category))
                                    bin_card_category = "-";
                                if (!functions.isValueNull(bin_usage_type))
                                    bin_usage_type = "-";
                                if (!functions.isValueNull(country_name))
                                    country_name = "-";

                            %>
                            <div class="widget-content padding" style="overflow-x: auto;">

                                <table align=center width="50%"
                                       class="display table table table-striped table-bordered table-hover dataTable"
                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Bin Brand</b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Country</b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Issuing Bank</b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Bin Card Type</b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Bin Card Category</b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Bin Usages Type</b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>ISO Country</b></td>
                                    </tr>
                                    </thead>


                                    <tr>
                                        <td class="tr" align="center" data-label="Bin Brand"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(bin_brand)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Country"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(card_country)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Issuing Bank"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(cardIssuingBank)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Bin Card Type"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(bin_card_type)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Bin Card Category"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(bin_card_category)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Bin Usages Type"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(bin_usage_type)%>
                                        </td>
                                        <td class="tr" align="center" data-label="ISO Country"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(country_name)%>
                                        </td>
                                    </tr>

                                </table>

                            </div>
                        </div>
                    </div>
                </div>
                <%
                    }
                    memberid = (String) innerHash.get("memberid");
                    String mercchantName = (String) innerHash.get("contact_persons");
                    String merchantIP = (String) innerHash.get("ipaddress");
                    String merchantEmail = (String) innerHash.get("contact_emails");

                    if (functions.isValueNull(memberid) || functions.isValueNull(mercchantName))
                    {
                %>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">

                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerTransactionDetails_merchant_Details%></strong></h2>
                            </div>
                            <%
                                memberid = (String) innerHash.get("memberid");
                                if (memberid == null) memberid = "-";
                                mercchantName = (String) innerHash.get("contact_persons");
                                if (mercchantName == null) mercchantName = "-";
                                String merchantIPCountry="";
                                merchantIP = (String) innerHash.get("merchantIp");
                                if (merchantIP == null){ merchantIP = "-";}
                                else {
                                    try
                                    {
                                        merchantIPCountry = functions.getIPCountryLong(merchantIP);
                                        if (!functions.isValueNull(merchantIPCountry))
                                            merchantIPCountry = "-";
                                    }
                                    catch(Exception e){
                                        merchantIPCountry = "-";
                                    }
                                }
                                String redirectUlr = "";
                                if (functions.isValueNull((String) innerHash.get("redirecturl")))
                                {
                                    redirectUlr = (String) innerHash.get("redirecturl");
                                }
                                else
                                {
                                    redirectUlr = "-";
                                }
                                if (merchantEmail == null) merchantEmail = "-";
                            %>
                            <div class="widget-content padding" style="overflow-x: auto;">

                                <table align=center width="50%"
                                       class="display table table table-striped table-bordered table-hover dataTable"
                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_MemberID%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_MerchantName%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Merchant_Email%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Redirect_URL%></b></td>

                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Merchant_IP%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Merchant_IP_country%></b></td>

                                    </tr>
                                    </thead>

                                    <tr>
                                        <td class="tr" align="center" data-label="Member ID"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(memberid)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Merchant Name"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(mercchantName)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Customer's Email Address"
                                            valign="middle"><%=functions.getEmailMasking(merchantEmail)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Redirect Url"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(redirectUlr)%>
                                        </td>

                                        <td class="tr" align="center" data-label="Merchant IP"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(merchantIP)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Merchant IP"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(merchantIPCountry)%>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                <%
                        }
                    }
                 /*   else
                    {
                        out.println(Functions.NewShowConfirmation1("Sorry", "No records found. Invalid TrackingID<br><br>"));
                    }*/

                %>

                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">

                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerTransactionDetails_Fraud_Rule%></strong></h2>
                            </div>

                            <div class="widget-content padding" style="overflow-x: auto;">

                                <table align=center width="50%"
                                       class="display table table table-striped table-bordered table-hover dataTable"
                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Fraud_Rule_name%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_Status%></b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b><%=partnerTransactionDetails_FraudScore%></b></td>
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
                                    <tr>
                                        <td class="tr" align="center" data-label="Fraud Rule Name"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(ruleMasterVO.getRuleName())%>
                                        </td>
                                        <td class="tr" align="center" data-label="Status"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(defaultStatus)%>
                                        </td>
                                        <td class="tr" align="center" data-label="Fraud Rule Score"
                                            valign="middle"><%=ESAPI.encoder().encodeForHTML(ruleMasterVO.getDefaultScore())%>
                                        </td>
                                    </tr>
                                    <%
                                        }
                                    }
                                    else
                                    {
                                    %>
                                    <tr>
                                        <td colspan="3" class="tr1" align="center">No rules triggered for this
                                            transaction
                                        </td>
                                    </tr>
                                    <%
                                        }
                                    %>

                                </table>

                            </div>
                        </div>
                    </div>
                </div>
                <%if(transactionReceiptImg != null){%>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">

                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Transaction Receipt</strong></h2>
                            </div>

                            <div class="widget-content padding" style="overflow-x: auto;">
                                <table align=center width="50%"
                                       class="display table table table-striped table-bordered table-hover dataTable"
                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <tr>
                                        <td colspan="3" class="tr1" align="center">
                                        <%
                                            String base64Value= Base64.encode(transactionReceiptImg);
                                        %>
                                        <img alt="img" src="data:image/jpeg;base64,<%=base64Value%>" style="height: 30%"/>
                                        </td>
                                    </tr>
                                    </table>
                            </div>
                        </div>
                    </div>
                </div>
                <%}%>
            </div>
        </div>
    </div>
</form>
</body>
<%!
    public static String nullToStr(String str)
    {
        if (str == null)
            return "";
        return str;
    }

    public static String getStatus(String str)
    {
        if (str.equals("Y"))
            return "Active";
        else if (str.equals("N"))
            return "Inactive";
        else if (str.equals("T"))
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