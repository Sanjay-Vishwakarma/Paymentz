<%--
  Created by IntelliJ IDEA.
  User: Sanjay
  Date: 11/30/2021
  Time: 1:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page
        import="com.directi.pg.Functions,com.directi.pg.LoadProperties,com.directi.pg.TransactionEntry,com.directi.pg.core.GatewayAccountService" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ include file="top.jsp" %>
<%
//    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
    session.setAttribute("submit", "MerchantPayoutTransactions");
%>

<%
//    System.out.println("inside merchantPayoutTransactions.jsp");
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    HashMap<String, String> statusHash = new LinkedHashMap();
    statusHash.put("payoutfailed", "Payout Failed");
    statusHash.put("payoutstarted", "Payout Started");
    statusHash.put("payoutsuccessful", "Payout Successful");


    if (partner.isLoggedInPartner(session))
    {
//        System.out.println("inside merchantPayoutTransactions.jsp if condition");

        TransactionEntry transactionentry = new TransactionEntry();
        SortedMap sortedMap = transactionentry.getSortedMap();
        String pid = nullToStr(request.getParameter("pid"));
        String Config = null;
        String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));


        if (Roles.contains("superpartner"))
        {

        }
        else
        {

            pid = String.valueOf(session.getAttribute("merchantid"));
            Config = "disabled";
        }

        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String partnerTransactions_Sorry = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_Sorry")) ? rb1.getString("partnerTransactions_Sorry") : "Sorry";
        String partnerTransactions_No_records_found = StringUtils.isNotEmpty(rb1.getString("partnerTransactions_No_records_found")) ? rb1.getString("partnerTransactions_No_records_found") : "No records found.";

        String remark = request.getParameter("remark");
        String partnerid = session.getAttribute("partnerId").toString();
        String str = "";
        String trackingid = Functions.checkStringNull(request.getParameter("trackingid")) == null ? "" : request.getParameter("trackingid");
        String emailAddress = Functions.checkStringNull(request.getParameter("emailaddr")) == null ? "" : request.getParameter("emailaddr");
        String accountId = Functions.checkStringNull(request.getParameter("accountid")) == null ? "" : request.getParameter("accountid");
        String bankaccount = Functions.checkStringNull(request.getParameter("bankaccount")) == null ? "" : request.getParameter("bankaccount");
        String status = Functions.checkStringNull(request.getParameter("status")) == null ? "" : request.getParameter("status");
        String amount = Functions.checkStringNull(request.getParameter("amount")) == null ? "" : request.getParameter("amount");
        String description = Functions.checkStringNull(request.getParameter("desc")) == null ? "" : request.getParameter("desc");
        String memberid = Functions.checkStringNull(request.getParameter("memberid")) == null ? "" : request.getParameter("memberid");
        String dateType     = Functions.checkStringNull(request.getParameter("datetype"));


        String fdate = null;
        String tdate = null;
        Date date = new Date();
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

        String Date = originalFormat.format(date);
        date.setDate(1);
        String fromDate = originalFormat.format(date);

        fdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDate : request.getParameter("fromdate");
        tdate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");
        String startTime = Functions.checkStringNull(request.getParameter("starttime"));
        String endTime = Functions.checkStringNull(request.getParameter("endtime"));


        String partnerTransactions_page_no = "Page Number";
        String partnerTransactions_of = "of";
        String partnerTransactions_total_no_of_records = "Total number of records";

        if (startTime == null) startTime = "00:00:00";
        if (endTime == null) endTime = "23:59:59";
        Calendar rightNow = Calendar.getInstance();
        if (fdate == null) fdate = "" + 1;
        if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

        String currentyear = "" + rightNow.get(rightNow.YEAR);

        if (fdate != null) str = str + "&fromdate=" + fdate;
        if (tdate != null) str = str + "&todate=" + tdate;
        if (startTime != null) str = str + "&starttime=" + startTime;
        if (endTime != null) str = str + "&endtime=" + endTime;
        if (description != null) str = str + "&description=" + description;
        if (memberid != null) str = str + "&memberid=" + memberid;
        if (trackingid != null) str = str + "&trackingid=" + trackingid;
        if (accountId != null) str = str + "&accountid=" + accountId;

        if (emailAddress != null) str = str + "&emailaddr=" + emailAddress;
        if (pid != null) str = str + "&pid=" + pid;
        if (status != null) str = str + "&status=" + status;
        if (bankaccount != null) str = str + "&bankaccount=" + bankaccount;
        if (amount != null) str = str + "&amount=" + amount;
        if (dateType != null) str = str + "&datetype=" + dateType;

        int pageno = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
        int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 30);
        str = str + "&SRecords=" + pagerecords;

%>

<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">

    <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>--%>
    <%--<script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
    <script type="text/javascript" language="JavaScript"
            src="/partner/javascript/autocomplete_partner_memberid.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>

    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script src="/merchant/datepicker/datetimepicker/moment-with-locales.js"></script>
    <script src="/merchant/datepicker/datetimepicker/bootstrap-datetimepicker.min.js"></script>
    <title>Merchant Payout Transactions</title>

    <style>
        .table#myTable > thead > tr > th {
            font-weight: inherit;
            text-align: center;
        }

        .hide_label {
            color: transparent;
            user-select: none;
        }

        .table-condensed a.btn {
            padding: 0;
        }

        .table-condensed .separator {
            padding-left: 0;
            padding-right: 0;
        }
        #ui-id-2
        {
            overflow: auto;
            max-height: 350px;
        }
    </style>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
    </script>
    <script type="text/javascript">

        $('#sandbox-container input').datepicker({});
    </script>
    <script>
        $(function ()
        {
            $(".datepicker").datepicker({dateFormat: "yy-mm-dd"});
        });
    </script>
    <script>
        $(function ()
        {
            $(".datepicker").datepicker({dateFormat: "yy-mm-dd"});
        });

        $(function ()
        {
            $('#datetimepicker12').datetimepicker({
                format: 'HH:mm:ss',
                useCurrent: true
            });
        });

        $(function ()
        {
            $('#datetimepicker13').datetimepicker({
                format: 'HH:mm:ss',
                useCurrent: true
            });
        });
    </script>

</head>
<body>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <form name="form" method="post" action="/partner/net/PayoutTransactionList?ctoken=<%=ctoken%>">
                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp; Merchant Payout Transactions </strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                            <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                            <input type="hidden" value="" name="partnername" id="partnername">
                            <%
                                Functions functions = new Functions();
                                String error = (String) request.getAttribute("error");
                                if (functions.isValueNull(error))
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                                }
                            %>
                            <div class="widget-content padding">
                                <div id="horizontal-form">

                                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-4">

                                        <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                                            <label>From</label>
                                            <input type="text" name="fromdate" id="From_dt"
                                                   class="datepicker form-control"
                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>"
                                                   readonly="readonly"
                                                   style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">
                                        </div>

                                        <div id="From_div" class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4"
                                             style="padding: 0;/*width: inherit;*/">
                                            <div class="form-group">
                                                <label class="hide_label">From</label>

                                                <div class='input-group date'>
                                                    <input type='text' id='datetimepicker12' class="form-control"
                                                           placeholder="HH:MM:SS" name="starttime" maxlength="8"
                                                           value="<%=startTime%>"
                                                           style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-4">

                                        <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                                            <label>To</label>
                                            <input type="text" name="todate" id="To_dt" class="datepicker form-control"
                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>"
                                                   readonly="readonly"
                                                   style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">
                                        </div>

                                        <div id="From_div" class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4"
                                             style="padding: 0;/*width: inherit;*/">
                                            <div class="form-group">
                                                <label class="hide_label">To</label>

                                                <div class='input-group date'>
                                                    <input type='text' id='datetimepicker13' class="form-control"
                                                           placeholder="HH:MM:SS" name="endtime" maxlength="8"
                                                           value="<%=endTime%>"
                                                           style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                                </div>
                                            </div>
                                        </div>

                                    </div>

                                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-2 has-feedback">
                                        <label>Partner Id</label>
                                        <input type=text name="pid" id="pid" value="<%=pid%>" class="form-control"
                                               autocomplete="on" <%=Config%> >
                                        <input type=hidden name="pid" value="<%=pid%>">
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-2 has-feedback">
                                        <label for="member">Merchant Id</label>
                                        <input name="memberid" id="member" value="<%=memberid%>" class="form-control"
                                               autocomplete="on">
                                    </div>

                                    <div class="form-group col-xs-12 col-md-12 has-feedback"
                                         style="margin:0; padding: 0;"></div>
                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label>Tracking id</label>
                                        <input type=text name="trackingid" class="form-control" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>">
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label>Description</label>
                                        <input type=text name="desc" maxlength="100" class="form-control"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(description)%>">
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label>Email Id</label>
                                        <input type="text" class="form-control"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailAddress)%>"
                                               name="emailaddr">
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label>Status</label>
                                        <select size="1" name="status" class="form-control">
                                            <option value="">All</option>
                                            <%
                                                Set setStatus   = statusHash.keySet();
                                                Iterator it         = setStatus.iterator();
                                                String selected3    = "";
                                                String key3         = "";
                                                String value3       = "";
                                                while (it.hasNext())
                                                {
                                                    key3    = (String) it.next();
                                                    value3  = statusHash.get(key3);

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
                                        <label>Account Id</label>
                                        <input name="accountid" id="accountid"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(accountId)%>"
                                               class="form-control"
                                               autocomplete="on">
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label>Bank Account Number</label>
                                        <input name="bankaccount" id="bankaccount"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(bankaccount)%>"
                                               class="form-control"
                                               autocomplete="on">
                                    </div>
                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label>Amount</label>
                                        <input name="amount" id="amount"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(amount)%>"
                                               class="form-control"
                                               autocomplete="on">
                                    </div>
                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label >Date Type</label>
                                        <select size="1" name="datetype" class="form-control">
                                            <%
                                                if("TIMESTAMP".equals(dateType))
                                                {%>
                                            <option value="DTSTAMP">DTSTAMP</option>
                                            <option value="TIMESTAMP" SELECTED>TIMESTAMP</option>
                                            <%}
                                            else
                                            {%>
                                            <option value="DTSTAMP" SELECTED>DTSTAMP</option>
                                            <option value="TIMESTAMP">TIMESTAMP</option>
                                            <%}
                                            %>
                                        </select>
                                    </div>
                                    <div class="form-group col-xs-12 col-sm-12 col-md-3 has-feedback">
                                        <label >Row/Pages</label>
                                        <input type="text" maxlength="4"  size="15" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(new Integer(pagerecords).toString())%>" name="SRecords" size="2" class="textBoxes" />
                                    </div>
                                    <div class="form-group col-xs-3 col-sm-3 col-md-3 has-feedback">
                                        <label>&nbsp;</label>
                                        <button type="submit" name="B1" class="btn btn-default"
                                                style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;Search
                                        </button>
                                    </div>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form>

            <%--Start Report Data--%>

            <div class="row reporttable">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Report Table</strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">
                            <%
                                String errormsg1 = (String) request.getAttribute("message");
                                if (errormsg1 == null)
                                {
                                    errormsg1 = "";
                                }
                                else
                                {
                                    out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\">");
                                    out.println(errormsg1);
                                    out.println("</font></td></tr></table>");
                                }
                            %>
                            <%
                                HashMap hash = (HashMap) request.getAttribute("payouttransaction");
//                                System.out.println("hashhhhhh---"+hash);
                                HashMap temphash = null, temphash1 = null;
                                List<String> trackingList = new ArrayList<String>();

                                str = str + "&trackingid=" + trackingid;
                                str = str + "&memberid=" + memberid;
                                int records = 0, trackingRecords = 0;
                                int totalrecords = 0;
                                int currentblock = 1;
                                try
                                {
//                                    System.out.println("inside try block jsp");
                                    records = Integer.parseInt((String) hash.get("records"));
//                                    trackingRecords = Integer.parseInt((String) trackinghash.get("records"));
                                    totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
                                    currentblock = Integer.parseInt((String) hash.get("currentblock"));

                                }
                                catch (Exception ex)
                                {
                                }

                                if (trackingRecords > 0)
                                {
                                    for (int pos = 1; pos <= trackingRecords; pos++)
                                    {
                                        String id = Integer.toString(pos);

                                        String icicitransid = (String) temphash1.get("transid");
                                        trackingList.add(icicitransid);

                                    }
                                }

                                String style = "class=tr0";
                                String ext = "light";
                                String style1 = "class=\"textb\"";
                                if (records > 0)
                                {
                            %>
                            <%--export to excel start--%>
                            <div class="pull-right">
                                <div class="btn-group">
                                    <form name="exportform" method="post" action="/partner/net/ExportPayoutTransactionByPartner?ctoken=<%=ctoken%>" >
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" name="fromdate">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" name="todate">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(startTime)%>" name="starttime">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(endTime)%>" name="endtime">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(status)%>" name="status">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(description)%>" name="desc">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>" name="memberid">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(bankaccount)%>" name="bankaccount">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(amount)%>" name="amount">
                                        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(String.valueOf(trackingid))%>" name="trackingid">
                                        <input type="hidden" value="<%=accountId%>" name="accountid">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <input type="hidden" value="<%=Roles%>" name="role">
                                        <input type="hidden" value="<%=dateType%>" name="datetype">

                                        <button class="btn-xs" type="submit" style="background: white;border: 0;">
                                            <img style="height: 40px;" src="/partner/images/exporttransaction.png">
                                        </button>
                                    </form>
                                </div>
                            </div>
                            <%--export to excel end--%>
                            <div class="widget-content padding" style="overflow-x: auto;">
                                <table align=center width="50%"
                                       class="display table table table-striped table-bordered table-hover dataTable"
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
                                                requestParameter.append("<input type='hidden' name='" + name + "' value=\"" + request.getParameter(name) + "\"/>");
                                        }
                                        requestParameter.append("<input type='hidden' name='SPageno' value='" + pageno + "'/>");
                                        requestParameter.append("<input type='hidden' name='SRecords' value='" + pagerecords + "'/>");

                                        for (int pos = 1; pos <= records; pos++)
                                        {
                                            String id = Integer.toString(pos);

                                            int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

                                            style = "class=\"tr" + (pos + 1) % 2 + "\"";

                                            temphash = (HashMap) hash.get(id);


                                            String date2        = Functions.convertDtstampToDateTime((String) temphash.get("dtstamp"));

                                            String description1  = (String)temphash.get("description");
                                            String icicitransid     =(String)temphash.get("transid");
                                            String name     = (String)temphash.get("totype");
                                            if (name == null) name = "-";
                                            String amount1       = (String) temphash.get("amount");
                                            String tempstatus       = (String) temphash.get("status");
                                            String accountid        = (String) temphash.get("accountid");
                                            String currency        = (String) temphash.get("currency");
                                            String toid                         = (String) temphash.get("toid");
                                            PartnerFunctions partnerfunctions   = new PartnerFunctions();
                                            String partner_id                   = partnerfunctions.getPartnerId(toid);
                                            ctoken                              = request.getParameter("ctoken");
                                            String tempterminalid   = (String) temphash.get("terminalid");
                                            String remark1          = (String) temphash.get("remark");
                                            String customerEmail    = (String) temphash.get("emailaddr");
                                            if (customerEmail == null) customerEmail = "-";

                                            String bankaccount1  = (String) temphash.get("bankaccount");
                                            if(bankaccount1==null) bankaccount1 ="-";
                                            String print_email      = " ";

                                            if(Roles.contains("superpartner")){
                                                print_email = customerEmail;
                                            }else{

                                                print_email = functions.getEmailMasking(customerEmail);
                                            }
                                            if (tempterminalid == null) tempterminalid = "-";

                                            ctoken      = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                                            out.println("<tr " + style + ">");
                                            out.println("<td valign=\"middle\" data-label=\"Date\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(date2) + "</td>");
                                            out.println("<td valign=\"middle\" data-label=\"Tracking ID\" align=\"center\"" + style + "><form action=\"MerchantPayoutTransactionDetails?ctoken=\"+ctoken+\"method=\"post\" ><input type=\"hidden\" name=\"memberid\" value=\"" + toid + "\"><input type=\"hidden\" name=\"trackingid\" value=\"" + icicitransid + "\"><input type=\"hidden\" name=\"ctoken\" value=\"" + ctoken + "\"><input type=\"submit\" class=\"btn btn-default\" name=\"action\" value=\"" + icicitransid + "\">");
                                            out.println("<td valign=\"middle\" data-label=\"Merchant ID\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(toid) + "</td>");
                                            out.println(requestParameter.toString());
                                            out.print("</form></td>");
                                            out.println("<td valign=\"middle\" data-label=\"Description\" style=\"word-break:break-all\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(description1) + "</td>");
                                            out.println("<td valign=\"middle\" data-label=\"Partner Name\" align=\"center\"" + style + ">&nbsp;" +name + "</td>");
                                            if (!functions.isValueNull(print_email)) {
                                                out.println("<td data-label=\"Customer Email\" align=\"center\"" + style + ">" +"-"+ "</td>");}
                                            else{
                                                out.println("<td valign=\"middle\" data-label=\"Customer Email\" align=\"center\"" + style + ">" + print_email + "</td>");
                                            }
                                            out.println("<td valign=\"middle\" data-label=\"Account Id\" align=\"center\"" + style + ">"+ESAPI.encoder().encodeForHTML(accountid)+"</td>");
                                            out.println("<td valign=\"middle\" data-label=\"Auth Amt\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(amount1) + "</td>");
                                            out.println("<td valign=\"middle\" data-label=\"Currency\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(currency) + "</td>");
                                            if (!functions.isValueNull(bankaccount1)) {
                                                out.println("<td data-label=\"Bankaccount\" align=\"center\"" + style + ">" +"-"+ "</td>");}
                                            else{
                                                out.println("<td valign=\"middle\" data-label=\"Bankaccount\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(bankaccount1) + "</td>");
                                            }
                                            out.println("<td valign=\"middle\" data-label=\"Status\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(tempstatus) + "</td>");

                                            if (!functions.isValueNull(remark1)) {
                                                out.println("<td data-label=\"Remark\" align=\"center\"" + style + ">" +"-"+ "</td>");}
                                            else{
                                                out.println("<td valign=\"middle\" data-label=\"Remark\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(remark1) + "</td>");
                                            }
                                            out.println("<td valign=\"middle\" data-label=\"Terminal Id\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(tempterminalid) + "</td>");

                                            out.println("</tr>");
                                        }
                                    %>
                                </table>
                            </div>
                        </div>
                        <% int TotalPageNo;
                            if (totalrecords % pagerecords != 0)
                            {
                                TotalPageNo = totalrecords / pagerecords + 1;
                            }
                            else
                            {
                                TotalPageNo = totalrecords / pagerecords;
                            }
                        %>
                        <div id="showingid">
                            <strong><%=partnerTransactions_page_no%> <%=pageno%> <%=partnerTransactions_of%> <%=TotalPageNo%>
                            </strong></div>
                        &nbsp;&nbsp;
                        <div id="showingid"><strong><%=partnerTransactions_total_no_of_records%>   <%=totalrecords%>
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
                                out.println(Functions.NewShowConfirmation1(partnerTransactions_Sorry, partnerTransactions_No_records_found));
                            }
                        %>
                        <%!
                            public static String nullToStr(String str)
                            {
                                if (str == null)
                                    return "";
                                return str;
                            }

                        %>
                    </div>
                </div>
            </div>
            <%
                }
                else
                {
                    response.sendRedirect("/partner/logout.jsp");
                    return;
                }
            %>

        </div>
    </div>
</div>
<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
</body>
</html>
