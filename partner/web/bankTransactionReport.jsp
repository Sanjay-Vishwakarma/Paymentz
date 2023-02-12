<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="java.util.*" %>
<%@ page import="com.payment.safexpay.SafexPayPaymentGateway" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%--
  Created by IntelliJ IDEA.
  User: Diksha
  Date: 24-Aug-20
  Time: 4:54 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="top.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%
    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
    session.setAttribute("submit", "bankTransactionReport");
%>
<%
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
%>
<html>
<head>
    <title><%=company%> Settlement Management> Bank Transaction Report</title>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">

    <script type="text/javascript" language="JavaScript"
            src="/partner/javascript/autocomplete_partner_memberid.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script src="/merchant/datepicker/datetimepicker/moment-with-locales.js"></script>
    <script src="/merchant/datepicker/datetimepicker/bootstrap-datetimepicker.min.js"></script>

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

    <script>

        $(document).ready(function ()
        {

            var w = $(window).width();

            //alert(w);

            if (w > 990)
            {
                //alert("It's greater than 990px");
                $("body").removeClass("smallscreen").addClass("widescreen");
                $("#wrapper").removeClass("enlarged");
            }
            else
            {
                //alert("It's less than 990px");
                $("body").removeClass("widescreen").addClass("smallscreen");
                $("#wrapper").addClass("enlarged");
                $(".left ul").removeAttr("style");
            }
        });

    </script>
    <style type="text/css">
        #ui-id-2
        {
            overflow: auto;
            max-height: 350px;
        }
    </style>

</head>
<body>
<% ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (partner.isLoggedInPartner(session))
    {
        String gateway = Functions.checkStringNull(request.getParameter("gateway"));
        String style = "class=\"tr0\"";
        String accountid = Functions.checkStringNull(request.getParameter("accountid"));
        String merchantid = Functions.checkStringNull(request.getParameter("merchantid"));
        String partnerid = String.valueOf(session.getAttribute("partnerId"));
        String pid = null;
        String Config = null;
        String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("partnerId")));
        if (Roles.contains("superpartner") || Roles.contains("childsuperpartner"))
        {
            pid = Functions.checkStringNull(request.getParameter("pid")) == null ? "" : request.getParameter("pid");
        }
        else
        {
            pid = String.valueOf(session.getAttribute("merchantid"));
            Config = "disabled";
        }


        String str = "";
        Date date = new Date();
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

        String Date = originalFormat.format(date);
        date.setDate(1);
        String fromDate = originalFormat.format(date);

        String startDate = request.getParameter("fromdate") == null ? fromDate : request.getParameter("fromdate");
        String startTime = request.getParameter("starttime") == null ? "00:00:00" : request.getParameter("starttime");
        String endDate = request.getParameter("todate") == null ? Date : request.getParameter("todate");
        String endTime = request.getParameter("endtime") == null ? "23:59:59" : request.getParameter("endtime");

        Calendar rightNow = Calendar.getInstance();
        if (startDate == null) startDate = "" + 1;
        if (endDate == null) endDate = "" + rightNow.get(rightNow.DATE);


        if (startDate != null) str = str + "&fromdate=" + startDate;
        if (endDate != null) str = str + "&todate=" + endDate;

        String currency = "";
        Functions functions = new Functions();
        if (functions.isValueNull(request.getParameter("gateway")))
        {
            String aGatewaySet[] = request.getParameter("gateway").split("-");
            if (aGatewaySet.length == 3)
            {
                currency = aGatewaySet[1];
            }
        }

        Hashtable innerhash = new Hashtable();
        Hashtable innerhash2 = new Hashtable();
        String memberid = Functions.checkStringNull(request.getParameter("memberid")) == null ? "" : request.getParameter("memberid");
        String pgtypeid = Functions.checkStringNull(request.getParameter("gateway")) == null ? "" : request.getParameter("gateway");
        String terminalId = Functions.checkStringNull(request.getParameter("terminalid") == null ? "" : request.getParameter("terminalid"));
        String gatewayName = request.getAttribute("gatewayName") == null ? "" : (String) request.getAttribute("gatewayName");
        String dayLight = request.getParameter("daylightsaving");

        String InquirySupported = request.getAttribute("InquirySupported") == null ? "" : (String) request.getAttribute("InquirySupported");


        TerminalManager terminalManager = new TerminalManager();
        List<TerminalVO> terminalList = terminalManager.getAllMappedTerminals();

        Hashtable dataHash = (Hashtable) request.getAttribute("bankdetails");
        Hashtable payoutHash = (Hashtable) request.getAttribute("payoutdetails");
        Hashtable refundHash = (Hashtable) request.getAttribute("bankrefunddetails");
        String refundtotalstr = (String) request.getAttribute("refundtotal");
        String ptotalstr = (String) request.getAttribute("payouttotal");
        String totalstr = (String) request.getAttribute("total");
        int refundtotal = 0;
        int total = 0;
        int payoutTotal=0;
        if (functions.isValueNull(refundtotalstr))
        {
            refundtotal = Integer.parseInt(refundtotalstr);
        }

        if (functions.isValueNull(totalstr))
        {
            total = Integer.parseInt(totalstr);
        }
        if (functions.isValueNull(ptotalstr))
        {
            payoutTotal = Integer.parseInt(ptotalstr);
        }

        String errorMsg = (String) request.getAttribute("errormsg");
        String error = (String) request.getAttribute("error");

        String timeDifference = (String) request.getAttribute("timediff");
        if (timeDifference == null || timeDifference.equalsIgnoreCase("null"))
        {
            timeDifference = "00:00:00";
        }
        if (gateway == null)
        {
            gateway = "";
        }
        if (accountid == null)
        {
            accountid = "";
        }
        if (merchantid == null)
        {
            merchantid = "";
        }
        if (terminalId == null)
        {
            terminalId = "";
        }

        TreeMap<String, TerminalVO> memberMap = new TreeMap<String, TerminalVO>();
        TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();
        TreeMap<String, GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();
        for (TerminalVO terminalVO : terminalList)
        {
            String memberKey = terminalVO.getMemberId() + "-" + terminalVO.getAccountId() + "-" + terminalVO.getGateway().toUpperCase() + "-" + terminalVO.getCurrency() + "-" + terminalVO.getGateway_id();
            memberMap.put(memberKey, terminalVO);
        }
%>

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <form name="form" method="post" action="/partner/net/BankTransactionReport?ctoken=<%=ctoken%>">
                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> Bank Transaction
                                    Report</strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                            <input type="hidden" value="<%=company%>" name="partnername" id="partnername">
                            <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                            <%
                                if (functions.isValueNull(errorMsg))
                                {
                                    out.println("<p><h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errorMsg + "</h5></p>");
                                }
                                if (functions.isValueNull(error))
                                {
                                    out.println("<p><h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5></p>");
                                }
                            %>

                            <div class="widget-content padding">
                                <div id="horizontal-form">

                                    <div class="form-group col-md-4" style="display: inline-block;">
                                        <label>Start Date</label>
                                        <input type="text" name="fromdate" id="fromdate" class="datepicker form-control"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(startDate)%>"
                                               readonly="readonly"
                                               style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Start
                                            Time(HH:MM:SS)</label>
                                        <input type='text' id='datetimepicker12' class="form-control"
                                               placeholder="HH:MM:SS" name="starttime" maxlength="8"
                                               value="<%=startTime%>"
                                               style="border: 1px solid #b2b2b2;font-weight:bold">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Day Light
                                            Saving</label>
                                        <select name='daylightsaving' class="form-control"
                                                style="border: 1px solid #b2b2b2;font-weight:bold">
                                            <%
                                                if ("Y".equalsIgnoreCase(dayLight))
                                                {
                                            %>
                                            <option value="N">No</option>
                                            <option value="Y" selected>Yes</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N">No</option>
                                            <option value="Y">Yes</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="form-group col-md-4" style="display: inline-block;">
                                        <label>End Date</label>
                                        <input type="text" name="todate" id="todate" class="datepicker form-control"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(endDate)%>"
                                               readonly="readonly"
                                               style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">End
                                            Time(HH:MM:SS)</label>
                                        <input type='text' id='datetimepicker13' class="form-control"
                                               placeholder="HH:MM:SS" name="endtime" maxlength="8"
                                               value="<%=request.getParameter("endtime")==null?"23:59:59":request.getParameter("endtime")%>"
                                               style="border: 1px solid #b2b2b2;font-weight:bold">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <%
                                            String time = (gateway.equals("") || gateway.equals("null")) ? "00:00:00" : timeDifference;
                                        %>
                                        <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Time
                                            Diffrence</label>
                                        <input type=text class="form-control"
                                               style="border: 1px solid #b2b2b2;font-weight:bold" disabled
                                               value="<%=time%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label>Partner ID*</label>
                                        <input type=text name="pid" id="pid" maxlength="100" class="form-control"
                                               value="<%=pid%>" autocomplete="on" <%=Config%>>
                                        <input type="hidden" name="pid" value="<%=pid%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label>Gateway*</label>
                                        <input type=text name="gateway" id="bank_name" maxlength="20" class="form-control"
                                               size="15" value="<%=pgtypeid%>" autocomplete="on">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label for="member">Member ID</label>
                                        <input type=text name="memberid" maxlength="100" class="form-control"
                                               id="member" value="<%=memberid%>" autocomplete="on">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label>Account ID</label>
                                        <input type=text name="accountid" maxlength="100" id="acc_id"
                                               class="form-control"
                                               value="<%=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid")%>"
                                               autocomplete="on">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label>Terminal ID</label>
                                        <input type="text" class="form-control" value="<%=terminalId%>" id="terminal"
                                               name="terminalid" autocomplete="on">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label>&nbsp;</label>
                                        <button type="submit" name="button" value="search" class="btn btn-default"
                                                style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;Search
                                        </button>
                                    </div>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form>

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
                                String status = (String) request.getAttribute("status");

                                if (status == null)
                                {
                                    status = "0";
                            %>
                            <%
                                if (dataHash != null && innerhash != null)
                                {
                                    if (total != 0 && refundtotal != 0)
                                    {

                            %>
                            <div class="pull-right">
                                <div class="btn-group">
                                    <form name="exportform" method="post"
                                          action="/partner/net/ExportBankTransactions?ctoken=<%=ctoken%>">
                                        <input type="hidden" value="<%=startDate%>" name="fromdate">
                                        <input type="hidden" value="<%=endDate%>" name="todate">
                                        <input type="hidden" value="<%=startTime%>" name="startTime">
                                        <input type="hidden" value="<%=endTime%>" name="endTime">
                                        <input type="hidden" value="<%=gateway%>" name="gateway">
                                        <input type="hidden" value="<%=pid%>" name="pid">
                                        <input type="hidden" value="<%=accountid%>" name="accountId">
                                        <input type="hidden" value="<%=memberid%>" name="memberId">
                                        <input type="hidden" value="<%=terminalId%>" name="terminalId">
                                        <button class="btn-xs" type="submit" style="background: white;border: 0;">
                                            <img style="height: 40px;" src="/merchant/images/excel.png">
                                        </button>
                                    </form>
                                </div>
                            </div>


                            <table border="0" cellpadding="3" cellspacing="3" bordercolor="#2379A5" width="50%"
                                   align="center"
                                   class="table table-striped table-bordered table-hover table-green dataTable"
                                   id="table1">
                                <tr>
                                    <td valign="middle" align="center" colspan="8"
                                        style="height: 30px; background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <center><b>Bank Transaction Report(Capture)</b></center>
                                    </td>
                                </tr>
                                <tr style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                    <td valign="middle" align="center" style="text-align: center">Status</td>
                                    <td valign="middle" align="center" style="text-align: center">No. Of Transaction
                                    </td>
                                    <td valign="middle" align="center" style="text-align: center">Amount</td>
                                    <td valign="middle" align="center" style="text-align: center">Capture Amount</td>
                                    <td valign="middle" align="center" style="text-align: center">Percentage(%)</td>
                                    <td valign="middle" align="center" style="text-align: center">Action</td>
                                </tr>
                                <%
                                    for (int pos = 1; pos <= dataHash.size(); pos++)
                                    {
                                        innerhash = (Hashtable) dataHash.get(pos + "");
                                        if (pos % 2 == 0)
                                        {
                                            style = "class=tr0";

                                        }
                                        else
                                        {
                                            style = "class=tr1";

                                        }
                                        String status2 = (String) innerhash.get("STATUS");
                                        out.println("<tr  " + style + ">");
                                        out.println("<td align=\"center\" >" + innerhash.get("STATUS") + " </td>");
                                        out.println("<td align=\"center\" >" + innerhash.get("COUNT(*)") + " </td>");
                                        out.println("<td align=\"center\" >" + innerhash.get("amount") + " </td>");
                                        out.println("<td align=\"center\" >" + innerhash.get("captureamount") + " </td>");
                                        out.println("<td align=\"center\" >" + Functions.round(Double.parseDouble((String) innerhash.get("COUNT(*)")) / total * 100, 2) + "%</td>");
                                        if (status2.equalsIgnoreCase("authstarted") && InquirySupported.equals("supported"))
                                        {
                                            out.println("<td align=\"center\" " + style + ">" +
                                                    "<form name =\"updateSettled\" action=\"/partner/net/BankTransactionInquiry?ctoken=" + ctoken + "\" method=\"post\">" +
                                                    "<input type=\"hidden\" name=\"fromdate\" value=\"" + startDate + "\">" +
                                                    "<input type=\"hidden\" name=\"starttime\" value=\"" + startTime + "\">" +
                                                    "<input type=\"hidden\" name=\"todate\" value=\"" + endDate + "\">" +
                                                    "<input type=\"hidden\" name=\"endtime\" value=\"" + endTime + "\">" +
                                                    "<input type=\"hidden\" name=\"gateway\" value=\"" + pgtypeid + "\">" +
                                                    "<input type=\"hidden\" name=\"memberid\" value=\"" + memberid + "\">" +
                                                    "<input type=\"hidden\" name=\"accountid\" value=\"" + accountid + "\">" +
                                                    "<input type=\"hidden\" name=\"terminalid\" value=\"" + terminalId + "\">" +
                                                    "<input type=\"hidden\" name=\"partnerName\" value=\"" + pid + "\">" +
                                                    "<input type=\"hidden\" name=\"currency\" value=\"" + currency + "\">" +
                                                    "<input type=\"hidden\" name=\"status\" value=\"" + status2 + "\">" +
                                                    "<input type=\"hidden\" name=\"daylight\" value=\"" + dayLight + "\">" +
                                                    "<input type=\"hidden\" name=\"ActivityAction\" value=\"AuthStarted Inquiry Cron\">" +
                                                    "<input type=\"submit\" class=\"goto\" value=\"AuthStarted Inquiry Cron\"></form></td>");
                                        }
                                        else if (status2.equalsIgnoreCase("authfailed") && SafexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                                        {
                                            out.println("<td align=\"center\" " + style + ">" +
                                                    "<form name =\"updateSettled\" action=\"/partner/net/BankTransactionInquiry?ctoken=" + ctoken + "\" method=\"post\">" +
                                                    "<input type=\"hidden\" name=\"fromdate\" value=\"" + startDate + "\">" +
                                                    "<input type=\"hidden\" name=\"starttime\" value=\"" + startTime + "\">" +
                                                    "<input type=\"hidden\" name=\"todate\" value=\"" + endDate + "\">" +
                                                    "<input type=\"hidden\" name=\"endtime\" value=\"" + endTime + "\">" +
                                                    "<input type=\"hidden\" name=\"gateway\" value=\"" + pgtypeid + "\">" +
                                                    "<input type=\"hidden\" name=\"memberid\" value=\"" + memberid + "\">" +
                                                    "<input type=\"hidden\" name=\"accountid\" value=\"" + accountid + "\">" +
                                                    "<input type=\"hidden\" name=\"terminalid\" value=\"" + terminalId + "\">" +
                                                    "<input type=\"hidden\" name=\"partnerName\" value=\"" + pid + "\">" +
                                                    "<input type=\"hidden\" name=\"currency\" value=\"" + currency + "\">" +
                                                    "<input type=\"hidden\" name=\"status\" value=\"" + status2 + "\">" +
                                                    "<input type=\"hidden\" name=\"daylight\" value=\"" + dayLight + "\">" +
                                                    "<input type=\"hidden\" name=\"ActivityAction\" value=\"AuthFailed Inquiry Cron\">" +
                                                    "<input type=\"submit\" class=\"goto\" value=\"AuthFailed Inquiry Cron\"></form></td>");

                                        }
                                        else
                                        {
                                            out.println("<td>&nbsp;</td>");
                                        }
                                        out.println("</tr>");
                                    }
                                %>
                            </table>

                            <table border="0" cellpadding="3" cellspacing="3" bordercolor="#2379A5" width="50%"
                                   align="center"
                                   class="table table-striped table-bordered table-hover table-green dataTable"
                                   id="table1">
                                <tr>
                                    <td valign="middle" align="center" colspan="8"
                                        style="height: 30px; background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <center><b>Bank Transaction Report(Payout)</b></center>
                                    </td>
                                </tr>
                                <tr style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                    <td valign="middle" align="center" style="text-align: center">Status</td>
                                    <td valign="middle" align="center" style="text-align: center">No. Of Transaction</td>
                                    <td valign="middle" align="center" style="text-align: center">Amount</td>
                                    <td valign="middle" align="center" style="text-align: center">Payout Amount</td>
                                    <td valign="middle" align="center" style="text-align: center">Percentage(%)</td>
                                </tr>
                                <%
                                    for (int pos = 1; pos <= payoutHash.size(); pos++)
                                    {
                                        innerhash = (Hashtable) payoutHash.get(pos + "");
                                        if (pos % 2 == 0)
                                        {
                                            style = "class=tr0";

                                        }
                                        else
                                        {
                                            style = "class=tr1";

                                        }
                                        out.println("<tr  " + style + ">");
                                        out.println("<td align=\"center\" >" + innerhash.get("STATUS") + " </td>");
                                        out.println("<td align=\"center\" >" + innerhash.get("COUNT(*)") + " </td>");
                                        out.println("<td align=\"center\" >" + innerhash.get("amount") + " </td>");
                                        out.println("<td align=\"center\" >" + innerhash.get("payoutamount") + " </td>");
                                        out.println("<td align=\"center\" >" + Functions.round(Double.parseDouble((String) innerhash.get("COUNT(*)")) / payoutTotal * 100, 2) + "%</td>");
                                        out.println("</tr>");
                                    }
                                %>
                            </table>

                            <table border="0" cellpadding="3" cellspacing="3" bordercolor="#2379A5" width="50%"
                                   align="center"
                                   class="table table-striped table-bordered table-hover table-green dataTable"
                                   id="table2">
                                <tr>
                                    <td valign="middle" align="center" colspan="8"
                                        style="height: 30px; background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <center><b>Bank Transaction Report(Refund & Chargeback)</b></center>
                                    </td>
                                </tr>
                                <tr <%=style%>
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                    <td valign="middle" align="center" style="text-align: center">Status</td>
                                    <td valign="middle" align="center" style="text-align: center">No. Of Transaction
                                    </td>
                                    <td valign="middle" align="center" style="text-align: center">Amount</td>
                                    <td valign="middle" align="center" style="text-align: center">Refund Amount</td>
                                    <td valign="middle" align="center" style="text-align: center">Chargeback Amount</td>
                                    <td valign="middle" align="center" style="text-align: center">Percentage(%)</td>

                                </tr>

                                <%
                                    for (int pos1 = 1; pos1 <= refundHash.size(); pos1++)
                                    {
                                        innerhash2 = (Hashtable) refundHash.get(pos1 + "");
                                        if (pos1 % 2 == 0)
                                        {
                                            style = "class=tr0";

                                        }
                                        else
                                        {
                                            style = "class=tr1";

                                        }
                                        out.println("<tr  " + style + ">");
                                        out.println("<td align=\"center\" >" + innerhash2.get("STATUS") + " </td>");
                                        out.println("<td align=\"center\" >" + innerhash2.get("COUNT(*)") + " </td>");
                                        out.println("<td align=\"center\" >" + innerhash2.get("amount") + " </td>");
                                        out.println("<td align=\"center\" >" + innerhash2.get("refundamount") + " </td>");
                                        out.println("<td align=\"center\" >" + innerhash2.get("chargebackamount") + " </td>");
                                        out.println("<td align=\"center\" >" + Functions.round(Double.parseDouble((String) innerhash2.get("COUNT(*)")) / refundtotal * 100, 2) + "%</td>");
                                        out.println("</tr>");
                                    }
                                %>
                            </table>

                            <%
                            }
                            else if (total != 0 && refundtotal == 0)
                            {
                            %>
                            <div class="pull-right">
                                <div class="btn-group">
                                    <form name="exportform" method="post"
                                          action="/partner/net/ExportBankTransactions?ctoken=<%=ctoken%>">
                                        <input type="hidden" value="<%=startDate%>" name="fromdate">
                                        <input type="hidden" value="<%=endDate%>" name="todate">
                                        <input type="hidden" value="<%=startTime%>" name="startTime">
                                        <input type="hidden" value="<%=endTime%>" name="endTime">
                                        <input type="hidden" value="<%=gateway%>" name="gateway">
                                        <input type="hidden" value="<%=pid%>" name="pid">
                                        <input type="hidden" value="<%=accountid%>" name="accountId">
                                        <input type="hidden" value="<%=memberid%>" name="memberId">
                                        <input type="hidden" value="<%=terminalId%>" name="terminalId">
                                        <button class="btn-xs" type="submit" style="background: white;border: 0;">
                                            <img style="height: 40px;" src="/merchant/images/excel.png">
                                        </button>
                                    </form>
                                </div>
                            </div>


                            <table border="0" cellpadding="3" cellspacing="3" bordercolor="#2379A5" width="50%"
                                   align="center"
                                   class="table table-striped table-bordered table-hover table-green dataTable"
                                   id="table1">

                                <tr>
                                    <td valign="middle" align="center" colspan="8"
                                        style="height: 30px; background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <center><b>Bank Transaction Report(Capture)</b></center>
                                    </td>
                                </tr>
                                <tr style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                    <td valign="middle" align="center" style="text-align: center">Status</td>
                                    <td valign="middle" align="center" style="text-align: center">No. Of Transaction
                                    </td>
                                    <td valign="middle" align="center" style="text-align: center">Amount</td>
                                    <td valign="middle" align="center" style="text-align: center">Capture Amount</td>
                                    <td valign="middle" align="center" style="text-align: center">Percentage(%)</td>
                                    <td valign="middle" align="center" style="text-align: center">Action</td>
                                </tr>
                                <%
                                    for (int pos = 1; pos <= dataHash.size(); pos++)
                                    {
                                        innerhash = (Hashtable) dataHash.get(pos + "");
                                        if (pos % 2 == 0)
                                        {
                                            style = "class=tr0";

                                        }
                                        else
                                        {
                                            style = "class=tr1";

                                        }
                                        String status2 = (String) innerhash.get("STATUS");
                                        out.println("<tr  " + style + ">");
                                        out.println("<td align=\"center\" >" + innerhash.get("STATUS") + " </td>");
                                        out.println("<td align=\"center\" >" + innerhash.get("COUNT(*)") + " </td>");
                                        out.println("<td align=\"center\" >" + innerhash.get("amount") + " </td>");
                                        out.println("<td align=\"center\" >" + innerhash.get("captureamount") + " </td>");
                                        out.println("<td align=\"center\" >" + Functions.round(Double.parseDouble((String) innerhash.get("COUNT(*)")) / total * 100, 2) + "%</td>");
                                        if (status2.equalsIgnoreCase("authstarted") && InquirySupported.equals("supported"))
                                        {
                                            out.println("<td align=\"center\" " + style + ">" +
                                                    "<form name =\"updateSettled\" action=\"/partner/net/BankTransactionInquiry?ctoken=" + ctoken + "\" method=\"post\">" +
                                                    "<input type=\"hidden\" name=\"fromdate\" value=\"" + startDate + "\">" +
                                                    "<input type=\"hidden\" name=\"starttime\" value=\"" + startTime + "\">" +
                                                    "<input type=\"hidden\" name=\"todate\" value=\"" + endDate + "\">" +
                                                    "<input type=\"hidden\" name=\"endtime\" value=\"" + endTime + "\">" +
                                                    "<input type=\"hidden\" name=\"gateway\" value=\"" + pgtypeid + "\">" +
                                                    "<input type=\"hidden\" name=\"memberid\" value=\"" + memberid + "\">" +
                                                    "<input type=\"hidden\" name=\"accountid\" value=\"" + accountid + "\">" +
                                                    "<input type=\"hidden\" name=\"terminalid\" value=\"" + terminalId + "\">" +
                                                    "<input type=\"hidden\" name=\"partnerName\" value=\"" + pid + "\">" +
                                                    "<input type=\"hidden\" name=\"currency\" value=\"" + currency + "\">" +
                                                    "<input type=\"hidden\" name=\"status\" value=\"" + status2 + "\">" +
                                                    "<input type=\"hidden\" name=\"ActivityAction\" value=\"AuthStarted Inquiry Cron\">" +
                                                    "<input type=\"submit\" class=\"goto\" value=\"AuthStarted Inquiry Cron\"></form></td>");
                                        }
                                        else if (status2.equalsIgnoreCase("authfailed") && SafexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName))
                                        {
                                            out.println("<td align=\"center\" " + style + ">" +
                                                    "<form name =\"updateSettled\" action=\"/partner/net/BankTransactionInquiry?ctoken=" + ctoken + "\" method=\"post\">" +
                                                    "<input type=\"hidden\" name=\"fromdate\" value=\"" + startDate + "\">" +
                                                    "<input type=\"hidden\" name=\"starttime\" value=\"" + startTime + "\">" +
                                                    "<input type=\"hidden\" name=\"todate\" value=\"" + endDate + "\">" +
                                                    "<input type=\"hidden\" name=\"endtime\" value=\"" + endTime + "\">" +
                                                    "<input type=\"hidden\" name=\"gateway\" value=\"" + pgtypeid + "\">" +
                                                    "<input type=\"hidden\" name=\"memberid\" value=\"" + memberid + "\">" +
                                                    "<input type=\"hidden\" name=\"accountid\" value=\"" + accountid + "\">" +
                                                    "<input type=\"hidden\" name=\"terminalid\" value=\"" + terminalId + "\">" +
                                                    "<input type=\"hidden\" name=\"partnerName\" value=\"" + pid + "\">" +
                                                    "<input type=\"hidden\" name=\"currency\" value=\"" + currency + "\">" +
                                                    "<input type=\"hidden\" name=\"status\" value=\"" + status2 + "\">" +
                                                    "<input type=\"hidden\" name=\"ActivityAction\" value=\"AuthFailed Inquiry Cron\">" +
                                                    "<input type=\"submit\" class=\"goto\" value=\"AuthFailed Inquiry Cron\"></form></td>");
                                        }
                                        else
                                        {
                                            out.println("<td>&nbsp;</td>");
                                        }
                                        out.println("</tr>");
                                    }
                                %>
                            </table>
                            <%
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation1("Sorry", "No records found"));
                                }
                            %>

                        </div>
                        <%
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation1("Sorry", "No records found"));
                                }
                            }
                            else
                            {
                                out.println(Functions.NewShowConfirmation1("Result", status));
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

</body>
</html>
