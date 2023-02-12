<%--
  Created by IntelliJ IDEA.
  User: Sanjay
  Date: 12/27/2021
  Time: 5:55 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page errorPage="error.jsp" import="com.directi.pg.Functions,
                                       com.directi.pg.TransactionEntry,
                                       com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.*" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.enums.TemplatePreference" %>
<%@ page import="com.directi.pg.*" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="com.directi.pg.Logger" %>

<script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
<script src="/merchant/NewCss/js/jquery-ui.min.js"></script>
<script src="/merchant/javascript/autocomplete_merchant_terminalid.js"></script>

<%@ include file="Top.jsp" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("company"));
    session.setAttribute("submit", "PayoutTransactionList");
%>

<%
    HashMap<String, String> statusHash = new LinkedHashMap<>();
    statusHash.put("payoutfailed", "Payout Failed");
    statusHash.put("payoutstarted", "Payout Started");
    statusHash.put("payoutsuccessful", "Payout Successful");

    Logger log = new Logger("payoutTransaction.jsp");
    ActionEntry entry = new ActionEntry();
    String uId = "";
    if (session.getAttribute("role").equals("submerchant"))
    {
        uId = (String) session.getAttribute("userid");
    }
    else
    {
        uId = (String) session.getAttribute("merchantid");
    }

    String Config = "disabled";
    TransactionEntry transactionEntry = (TransactionEntry) session.getAttribute("transactionentry");
    SortedMap sortedMap = transactionEntry.getSortedMap();

    String status = Functions.checkStringNull(request.getParameter("status"));
    String dateType = Functions.checkStringNull(request.getParameter("datetype"));
    String desc = Functions.checkStringNull(request.getParameter("desc")) == null ? "" : request.getParameter("desc");
    String trackingid = Functions.checkStringNull(request.getParameter("trackingid")) == null ? "" : request.getParameter("trackingid");
    String bankaccount = Functions.checkStringNull(request.getParameter("bankaccount")) == null ? "" : request.getParameter("bankaccount");
    String amount = Functions.checkStringNull(request.getParameter("amount")) == null ? "" : request.getParameter("amount");
    String emailAddress = Functions.checkStringNull(request.getParameter("emailaddr")) == null ? "" : request.getParameter("emailaddr");
    String accountid = Functions.checkStringNull(request.getParameter("accountid")) == null ? "" : request.getParameter("accountid");

    ResourceBundle rb1                  = null;
    String language_property1           = (String)session.getAttribute("language_property");
    rb1                                 = LoadProperties.getProperty(language_property1);
    String transactions_sorry               = StringUtils.isNotEmpty(rb1.getString("transactions_sorry"))?rb1.getString("transactions_sorry"): "Sorry";
    String transactions_no_records          = StringUtils.isNotEmpty(rb1.getString("transactions_no_records"))?rb1.getString("transactions_no_records"): "No records found for given search criteria.";
    String transactions_filter              = StringUtils.isNotEmpty(rb1.getString("transactions_filter"))?rb1.getString("transactions_filter"): "Filter";
    String transactions_search1             = StringUtils.isNotEmpty(rb1.getString("transactions_search1"))?rb1.getString("transactions_search1"): "Please provide the appropriate data for the search of Transaction";
    String transactions_page_no             = StringUtils.isNotEmpty(rb1.getString("transactions_page_no"))?rb1.getString("transactions_page_no"):"Page number";
    String transactions_total_no_of_records = StringUtils.isNotEmpty(rb1.getString("transactions_total_no_of_records"))?rb1.getString("transactions_total_no_of_records"):"Total number of records";
    String transactions_Export_To_Excel     = StringUtils.isNotEmpty(rb1.getString("transactions_Export_To_Excel"))?rb1.getString("transactions_Export_To_Excel"): "Export To Excel";
    String transactions_Report_Table        = StringUtils.isNotEmpty(rb1.getString("transactions_Report_Table"))?rb1.getString("transactions_Report_Table"): "Report Table";
    String transactions_Payout_Transactions = StringUtils.isNotEmpty(rb1.getString("transactions_Payout_Transactions"))?rb1.getString("transactions_Payout_Transactions"): "Payout Transactions";
    String transactions_From                = StringUtils.isNotEmpty(rb1.getString("transactions_From"))?rb1.getString("transactions_From"): "From";
    String transactions_To                  = StringUtils.isNotEmpty(rb1.getString("transactions_To"))?rb1.getString("transactions_To"): "To";
    String transactions_Account_Id          = StringUtils.isNotEmpty(rb1.getString("transactions_Account_Id"))?rb1.getString("transactions_Account_Id"): "Account Id";
    String transactions_Merchant_Id         = StringUtils.isNotEmpty(rb1.getString("transactions_Merchant_Id"))?rb1.getString("transactions_Merchant_Id"): "Merchant Id";
    String transactions_Tracking_id        = StringUtils.isNotEmpty(rb1.getString("transactions_Tracking_id"))?rb1.getString("transactions_Tracking_id"): "Tracking id";
    String transactions_Description        = StringUtils.isNotEmpty(rb1.getString("transactions_Description"))?rb1.getString("transactions_Description"): "Description";
    String transactions_Email_Id        = StringUtils.isNotEmpty(rb1.getString("transactions_Email_Id"))?rb1.getString("transactions_Email_Id"): "Email Id";
    String transactions_Bank_Account_Number        = StringUtils.isNotEmpty(rb1.getString("transactions_Bank_Account_Number"))?rb1.getString("transactions_Bank_Account_Number"): "Bank Account Number";
    String transactions_Amount1       = StringUtils.isNotEmpty(rb1.getString("transactions_Amount1"))?rb1.getString("transactions_Amount1"): "Amount";
    String transactions_Status1     = StringUtils.isNotEmpty(rb1.getString("transactions_Status1"))?rb1.getString("transactions_Status1"): "Status";
    String transactions_Date_Type   = StringUtils.isNotEmpty(rb1.getString("transactions_Date_Type"))?rb1.getString("transactions_Date_Type"): "Date Type";
    String transactions_Row_Pages   = StringUtils.isNotEmpty(rb1.getString("transactions_Row_Pages"))?rb1.getString("transactions_Row_Pages"): "Row/Pages";
    String transactions_Search2  = StringUtils.isNotEmpty(rb1.getString("transactions_Search2"))?rb1.getString("transactions_Search2"): "Search";


    String fromdate = null;
    String todate = null;
    Date date = new Date();
    SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

    String Date = originalFormat.format(date);
    date.setDate(1);
    String fromDate = originalFormat.format(date);

    fromdate = Functions.checkStringNull(request.getParameter("fdate")) == null ? fromDate : request.getParameter("fdate");
    todate = Functions.checkStringNull(request.getParameter("tdate")) == null ? Date : request.getParameter("tdate");
    String startTime = Functions.checkStringNull(request.getParameter("starttime"));
    String endTime = Functions.checkStringNull(request.getParameter("endtime"));

    if (startTime == null) startTime = "00:00:00";
    if (endTime == null) endTime = "23:59:59";
    if (dateType == null) dateType = "";

    Calendar rightNow = Calendar.getInstance();
    String str = "";
    if (fromdate == null) fromdate = "" + 1;
    if (todate == null) todate = "" + rightNow.get(Calendar.DATE);
    String currentyear = "" + rightNow.get(Calendar.YEAR);

    if (fromdate != null) str = str + "fdate=" + fromdate;
    if (todate != null) str = str + "&tdate=" + todate;
    if (desc != null) str = str + "&desc=" + desc;
    if (dateType != null) str = str + "&datetype=" + dateType;
    if (startTime != null) str = str + "&starttime=" + startTime;
    if (endTime != null) str = str + "&endtime=" + endTime;

    if (emailAddress != null) str = str + "&emailaddr=" + emailAddress;
    else
        emailAddress = "";

    if (desc != null) str = str + "&desc=" + desc;
    else
        desc = "";

    if (accountid != null) str = str + "&accountid=" + accountid;
    else
        accountid = "";
    if (bankaccount != null) str = str + "&bankaccount=" + bankaccount;
    else
        bankaccount = "";

    if (status != null) str = str + "&status=" + status;
    else
        status = "";
    if (amount != null) str = str + "&amount=" + amount;
    else
        amount = "";
    if (trackingid != null) str = str + "&trackingid=" + trackingid;
    else
        trackingid = "";

    str = str + "&ctoken=" + ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    str = str + "&status=" + request.getParameter("status");
    int pageno = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
    int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

    str = str + "&SRecords=" + pagerecords;


%>

<input type="hidden" id="pageno" name="pageno" value=" ">
<link rel="stylesheet" href=" /merchant/transactionCSS/css/transactions.css">
<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
    <script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>

    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>

    <script src="/merchant/datepicker/datetimepicker/moment-with-locales.js"></script>
    <script src="/merchant/datepicker/datetimepicker/bootstrap-datetimepicker.min.js"></script>

    <title> Payout Transactions</title>

    <script src="/merchant/transactionCSS/js/transactions.js"></script>
</head>
<body>


<div class="content-page">
    <div class="content">
        <%--Page heading start--%>


        <div class="page-heading">
            <div class="row">

                <div class="col-sm-12 portlets ui-sortable ">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=transactions_Payout_Transactions%></strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <form name="form" method="post"
                              action="/merchant/servlet/PayoutTransactionList?ctoken=<%=ctoken%>">
                            <input type="hidden" name="ctoken" value="<%=ctoken%>" id="ctoken">
                            <input type="hidden" name="merchantid" vlaue="<%=uId%>" id="merchantid">
                            <%
                                StringBuffer terminalBuffer = new StringBuffer();
                                if ("ERR".equals(request.getParameter("MES")))
                                {
                                    ValidationErrorList error = (ValidationErrorList) request.getAttribute("validationErrorList");
                                    for (ValidationException errorList : error.errors())
                                    {
                                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errorList.getMessage() + "</h5>");
                                    }
                                }

                               /* if (request.getAttribute("error") != null)
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + request.getAttribute("error") + "</h5>");

                                }*/
                            %>
                            <div class="widget-content padding">
                                <div id="horizontal-form">
                                    <div class="form-group col-xs-12 col-sm-6 col-md-3 col-lg-3">
                                        <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                                            <label><%=transactions_From%></label>
                                            <input type="text" name="fdate" id="From_dt" class="datepicker form-control"
                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromdate)%>"
                                                   readonly="readonly"
                                                   style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">
                                        </div>

                                        <div <%--id="From_div"--%>
                                                class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4"
                                                style="padding: 0;/*width: inherit;*/">
                                            <div class="form-group">
                                                <label class="hide_label">From</label>

                                                <div class='input-group date'>
                                                    <input type='text' id='datetimepicker12' class="form-control"
                                                           placeholder="HH:MM:SS" name="starttime" maxlength="8"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute(startTime)%>"
                                                           style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-6 col-md-3 col-lg-3">

                                        <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                                            <label><%=transactions_To%></label>
                                            <input type="text" name="tdate" id="To_dt" class="datepicker form-control"
                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(todate)%>"
                                                   readonly="readonly"
                                                   style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">
                                        </div>

                                        <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4"
                                             style="padding: 0;/*width: inherit;*/">
                                            <div class="form-group">
                                                <label class="hide_label">To</label>

                                                <div class='input-group date'>
                                                    <input type='text' id='datetimepicker13' class="form-control"
                                                           placeholder="HH:MM:SS" name="endtime" maxlength="8"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute(endTime)%>"
                                                           style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=transactions_Account_Id%></label>
                                        <input type=text name="accountid" id="accountid"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(accountid)%>"
                                               class="form-control"
                                               autocomplete="on">
                                        <input type=hidden name="accountid" value="">
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label for="member"><%=transactions_Merchant_Id%></label>
                                        <input name="memberid" id="member" value="<%=uId%>" class="form-control"
                                            <%=Config%>>
                                    </div>

                                    <div class="form-group col-xs-12 col-md-12 has-feedback"
                                         style="margin:0; padding: 0;"></div>
                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=transactions_Tracking_id%></label>
                                        <input type=text name="trackingid" class="form-control" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>">
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=transactions_Description%></label>
                                        <input type=text name="desc" maxlength="100" class="form-control"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>">
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=transactions_Email_Id%></label>
                                        <input type="text" class="form-control"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailAddress)%>"
                                               name="emailaddr">
                                    </div>
                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=transactions_Bank_Account_Number%></label>
                                        <input name="bankaccount" id="bankaccount"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(bankaccount)%>"
                                               class="form-control"
                                               autocomplete="on">
                                    </div>
                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=transactions_Amount1%></label>
                                        <input name="amount" id="amount"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(amount)%>"
                                               class="form-control"
                                               autocomplete="on">
                                    </div>
                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=transactions_Status1%></label>
                                        <select size="1" name="status" class="form-control">
                                            <option value="0">All</option>
                                            <%
                                                Set setStatus = statusHash.keySet();
                                                Iterator it = setStatus.iterator();
                                                String selected3 = "";
                                                String key3 = "";
                                                String value3 = "";
                                                while (it.hasNext())
                                                {
                                                    key3 = (String) it.next();
                                                    value3 = statusHash.get(key3);

                                                    if (key3.equals(status))
                                                        selected3 = "selected";
                                                    else
                                                        selected3 = "";

                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key3)%>" <%=selected3%>><%=ESAPI.encoder().encodeForHTML(value3)%>
                                            </option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=transactions_Date_Type%></label>
                                        <select size="1" name="datetype" class="form-control">
                                            <%
                                                if ("TIMESTAMP".equals(dateType))
                                                {%>
                                            <option value="DTSTAMP">DTSTAMP</option>
                                            <option value="TIMESTAMP" SELECTED>TIMESTAMP</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="DTSTAMP" SELECTED>DTSTAMP</option>
                                            <option value="TIMESTAMP">TIMESTAMP</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label><%=transactions_Row_Pages%></label>
                                        <input type="text" maxlength="4" size="15" class="form-control"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(new Integer(pagerecords).toString())%>"
                                               name="SRecords" size="2" class="textBoxes"/>
                                    </div>

                                    <div class="form-group col-xs-3 col-sm-3 col-md-3 has-feedback">
                                        <label>&nbsp;</label>
                                        <button type="submit" name="B1" class="btn btn-default"
                                                style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=transactions_Search2%>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <%-- Report Table Start Here --%>

            <div class="row">
                <div class="col-md-12">

                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=transactions_Report_Table%></strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding" style=overflow-y:auto;>

                            <%

                                if (request.getAttribute("payoutTransaction") != null)
                                {
                                    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

                                    Hashtable hash = (Hashtable) request.getAttribute("payoutTransaction");
                                    log.debug("payoutTransaction jsp==="+hash);
//                                    System.out.println("payoutTransaction jsp==="+hash);
                                    List<String> trackingList = new ArrayList<String>();
                                    HashMap trackinghash = null;

                                    Hashtable temphash = null;
                                    HashMap temphash1 = null;
                                    str = str + "&terminalbuffer=" + terminalBuffer;
                                    str = str + "&ctoken=" + ctoken;
                                    int records = 0;
                                    int totalrecords = 0, trackingRecords = 0;
                                    int currentblock = 1;


                                    try
                                    {
                                        records = Integer.parseInt((String) hash.get("records"));
                                        totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
                                        currentblock = Integer.parseInt(request.getParameter("currentblock"));
                                    }
                                    catch (Exception ex)
                                    {

                                    }
                                    String style = "class=tr0";

                                    if (records > 0)
                                    {

                            %>
                            <form name="exportform" method="post" action="ExportPayoutTransactions?ctoken=<%=ctoken%>">
                                <div class="pull-right" >
                                    <div class="btn-group" style="margin-left:6px">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>"
                                               name="desc">
                                        <input type="hidden"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>"
                                               name="trackingid">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(status)%>"
                                               name="status">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(amount)%>"
                                               name="amount">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(accountid)%>"
                                               name="accountid">
                                        <input type="hidden"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromdate)%>"
                                               name="fdate">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(todate)%>"
                                               name="tdate">
                                        <input type="hidden"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(startTime)%>"
                                               name="starttime">
                                        <input type="hidden"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(endTime)%>"
                                               name="endtime">
                                        <input type="hidden"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(dateType)%>"
                                               name="datetype">
                                        <input type="hidden" value="<%=emailAddress%>" name="emailaddr">
                                        <%--<input type="hidden" value="<%=terminalBuffer%>" name="terminalbuffer">--%>
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(bankaccount)%>"
                                               name="bankaccount">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <button type="submit" name="Excel" id="Excel" onclick="myExcel()" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-file-excel-o"></i>&nbsp;&nbsp<%=transactions_Export_To_Excel%></button>
                                    </div>
                                </div>
                            </form>


                            <div class="widget-content padding" id="table_div" style="overflow-x: auto;">

                                <table id="myTable" class="display table table table-striped table-bordered"
                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    <tr>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Transaction Date</b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Tracking Id</b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Member Id</b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Description</b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Partner Name</b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Customer Email</b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Account Id</b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Amount</b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Currency</b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Bank Account Number</b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Status</b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Remark</b></td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <b>Terminal Id</b></td>

                                    </tr>
                                    </thead>
                                    <tbody>
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                    <%
                                        StringBuffer requestParameter = new StringBuffer();
                                        Enumeration<String> stringEnumeration = request.getParameterNames();
                                        while (stringEnumeration.hasMoreElements())
                                        {
                                            String name = stringEnumeration.nextElement();
                                            if ("SPageno".equals(name) || "SRecords".equals(name))
                                            {

                                            }
                                            else
                                            {
                                                requestParameter.append("<input type='hidden' name='" + name + "' value=\"" + request.getParameter(name) + "\"/>");
                                            }

                                        }
                                        requestParameter.append("<input type='hidden' name='SPageno' name='SPageno' value='" + pageno + "'/>");
                                        requestParameter.append("<input type='hidden' name='SRecords' value='" + pagerecords + "'/>");

                                        for (int pos = 1; pos <= records; pos++)
                                        {
                                            String id = Integer.toString(pos);
                                            int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

                                            style = "class=\"tr" + (pos + 1) % 2 + "\"";
                                            temphash = (Hashtable) hash.get(id);

                                            // Functions functions     = new Functions();
                                            String date1 = Functions.convertDtstampToDateTime((String) temphash.get("dtstamp"));
                                            String description1 = (String) temphash.get("description");
                                            String icicitransid = (String) temphash.get("transid");
                                            String name = (String) temphash.get("totype");
                                            ctoken = request.getParameter("ctoken");
                                            if (name == null) name = "-";
                                            String amount1 = (String) temphash.get("amount");
                                            String tempstatus = (String)temphash.get("status");
                                            String accountid1 = (String) temphash.get("accountid");
                                            if(accountid1==null) accountid1="-";
                                            String tempterminalid = (String) temphash.get("terminalid");
                                            String customerEmail = (String) temphash.get("emailaddr");
                                            String bankaccount1 = (String) temphash.get("bankaccount");
                                            String currency = (String) temphash.get("currency");
                                            if(bankaccount1 == null) bankaccount1="-";
                                            String partner_id = (String) temphash.get("partnerid");
                                            String toid = (String) temphash.get("toid");
                                            String remark1 = (String) temphash.get("remark");
                                            if(remark1==null) remark1="-";
                                            if (tempterminalid == null) tempterminalid = "-";

                                            out.println("<tr " + style + ">");
                                            out.println("<td data-label=\"Date\" class=\"Transactions_Date\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(date1) + "</td>");
                                            out.println("<td data-label=\"Tracking ID\" class=\"Transactions_TrackingID\" align=\"center\"" + style + "><form action=\"PayoutTransactionDetails?ctoken=" + ctoken + "\" method=\"post\" ><input type=\"hidden\" name=\"accountid\" value=\"" + accountid + "\"><input type=\"hidden\" name=\"trackingid\" value=\"" + icicitransid + "\"><input type=\"hidden\" name=\"ctoken\" value=\"" + ctoken + "\"><input type=\"hidden\" name=\"buttonValue\" value=\"transaction\"><input type=\"hidden\" name=\"terminalbuffer\" value=\"" + terminalBuffer.toString() + "\"><input type=\"submit\" class=\"btn btn-default\" name=\"submit\"  value=\"" + icicitransid + "\">");
                                            out.println(requestParameter.toString());
                                            out.print("</form></td>");
                                            out.println("<td valign=\"middle\" data-label=\"Merchant ID\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(toid) + "</td>");
                                            out.println("<td valign=\"middle\" data-label=\"Description\" style=\"word-break:break-all\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(description1) + "</td>");
                                            out.println("<td valign=\"middle\" data-label=\"Partner Name\" align=\"center\"" + style + ">&nbsp;" +name + "</td>");
                                            out.println("<td valign=\"middle\" data-label=\"Customer Email\" align=\"center\"" + style + ">" + customerEmail + "</td>");
                                            out.println("<td valign=\"middle\" data-label=\"Account Id\" align=\"center\"" + style + ">"+ESAPI.encoder().encodeForHTML(accountid1)+"</td>");
                                            out.println("<td valign=\"middle\" data-label=\"Auth Amt\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(amount1) + "</td>");
                                            out.println("<td valign=\"middle\" data-label=\"Currency\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(currency) + "</td>");
                                            out.println("<td valign=\"middle\" data-label=\"Bankaccount\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(bankaccount1) + "</td>");
                                            out.println("<td valign=\"middle\" data-label=\"Status\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(tempstatus) + "</td>");
                                            out.println("<td valign=\"middle\" data-label=\"Remark\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(remark1) + "</td>");
                                            out.println("<td valign=\"middle\" data-label=\"Terminal Id\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(tempterminalid) + "</td>");

                                            out.println("</tr>");
                                        }
                                    %>
                                    </tbody>
                                </table>
                            </div>

                            <%
                                int TotalPageNo;
                                if (totalrecords % pagerecords != 0)
                                {
                                    TotalPageNo = totalrecords / pagerecords + 1;
                                }
                                else
                                {
                                    TotalPageNo = totalrecords / pagerecords;
                                }
                            %>

                            <div id="showingid"><strong><%=transactions_page_no%> <%=pageno%> of <%=TotalPageNo%>
                            </strong></div>
                            &nbsp;&nbsp;
                            <div id="showingid"><strong><%=transactions_total_no_of_records%>   <%=totalrecords%>
                            </strong></div>


                            <jsp:include page="page.jsp" flush="true">
                                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                                <jsp:param name="pageno" value="<%=pageno%>"/>
                                <jsp:param name="str" value="<%=str%>"/>
                                <jsp:param name="page" value="PayoutTransactionList"/>
                                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                                <jsp:param name="orderby" value=""/>
                            </jsp:include>

                            <%
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation1(transactions_sorry, transactions_no_records));
                                }

                            %>

                        </div>
                    </div>
                </div>
            </div>
            <%
                }
                else
                {
                    out.println(Functions.NewShowConfirmation1(transactions_filter,transactions_search1));
                }
            %>

        </div>
    </div>
</div>

</body>
</html>
