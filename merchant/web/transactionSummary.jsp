<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.enums.StatusChartsType" %>
<%@ page import="com.manager.vo.InputDateVO" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.TransactionReportVO" %>
<%@ page import="com.manager.vo.TransactionVO" %>
<%@ page import="com.payment.exceptionHandler.PZExceptionHandler" %>
<%@ page import="com.payment.exceptionHandler.operations.PZOperations" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 8/16/14
  Time: 5:29 PM
  To change this template use File | Settings | File Templates.
--%>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="Top.jsp" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("company"));
    session.setAttribute("submit", "Transaction Summary");

    session.getAttribute("colorPallet");

    String bincountrysuccessful = null;
    String bincountryfailed = null;
    String ipcountrysuccessful = null;
    String ipcountryfailed = null;


%>

<%--<script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
<script src="/merchant/NewCss/js/jquery-ui.min.js"></script>--%>
<script src="/merchant/javascript/autocomplete_merchant_terminalid.js"></script>
<script>
    $(function ()
    {
        $(".datepicker").datepicker({startDate: '-1y'});
    });
</script>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title><%=company%> Merchant Account Details > Transaction Summary</title>
    <link href="/merchant/NewCss/css/style-responsive.css" rel="stylesheet"/>
    <script src="/merchant/NewCss/libs/morrischart/morris.min.js"></script>
    <%--<script src="/merchant/NewCss/morrisJS/morris-0.4.1.min.js"></script>--%>
    <script src="/merchant/NewCss/morrisJS/raphael-min.js"></script>

    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">


    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>

    <script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>

    <script src="/merchant/datepicker/datetimepicker/moment-with-locales.js"></script>
    <%--<link href="/merchant/datepicker/datetimepicker/bootstrap-datetimepicker.css" rel="stylesheet">--%>
    <script src="/merchant/datepicker/datetimepicker/bootstrap-datetimepicker.min.js"></script>
    <script src="/merchant/javascript/jspdf.js"></script>
    <script src="/merchant/javascript/html2canvas.js"></script>
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
    <style>
        #refund
        {
            /* height:342px;*/
        }

        #chargeback
        {
            /*height:342px;*/
        }

        .hide_label
        {
            color: transparent;
            user-select: none;
        }

        #From_div, #To_div
        {
            width: 138px;
        }

        @media (max-width: 991px)
        {
            .hide_label
            {
                display: none;
            }

            #From_dt, #To_dt
            {
                width: 100%;
            }

            #From_div, #To_div
            {
                width: 100%;
            }
        }

        .table-condensed a.btn
        {
            padding: 0;
        }

        .table-condensed
        .separator
        {
            padding-left: 0;
            padding-right: 0;
        }

    </style>

    <style type="text/css">
        .morris-hover
        {
            position:absolute;
            z-index:1000;
        }
        .morris-hover.morris-default-style
        {
            border-radius:10px;
            padding:6px;
            color:#666;
            background:rgba(255, 255, 255, 0.8);
            border:solid 2px rgba(230, 230, 230, 0.8);
            font-family:sans-serif;
            font-size:12px;
            text-align:center;
            z-index: inherit;
        }
        .morris-hover.morris-default-style
        .morris-hover-row-label
        {
            font-weight:bold;
            margin:0.25em 0;
        }
        .morris-hover.morris-default-style
        .morris-hover-point
        {
            white-space:nowrap;
            margin:0.1em 0;
        }
        svg
        {
            width: 100%;
        }
        #currencyid
        {
            position: absolute;
            right: 0px;
            padding: 5px
        }
        @media(max-width: 620px)
        {
            #currencyid
            {
                position: initial;
            }
        }
        @media (min-width: 1200px)
        {
            .col-lg-3
            {
                width: 20%;
            }
        }
        @media(min-width:1200px)
        {
            .widget-icon img
            {
                width: 50px;
                margin-top: -12px;
            }
            .text-box h2 img
            {
                margin-top: 4px;
                width: 1.5vw;
            }
            .animate-number
            {
                font-size: 1.5vw;
            }
        }
        @media(min-width:992px) and (max-width: 1199px)
        {
            .widget-icon img
            {
                width: 50px; margin-top: -12px;
            }
            .text-box h2 img
            {
                margin-top: -1px;
            }
            .animate-number
            {
                font-size: 2.5vw;
            }
        }
        @media(min-width:768px) and (max-width:991px)
        {
            .widget-icon img
            {
                width: 50px;
                margin-top: -12px;
            }
            .text-box h2 img
            {
                margin-top: 3px; width: 2.5vw;
            }
            .animate-number
            {
                font-size: 2.5vw;
            }
        }
        @media(min-width:480px) and (max-width:767px)
        {
            .widget-icon img
            {
                width: 50px; margin-top: -12px;
            }
            .text-box h2 img
            {
                margin-top: 4px; width: 3.5vw;
            }
            .animate-number
            {
                font-size: 3vw;
            }
        }
        @media(max-width:479px)
        {
            .widget-icon img
            {
                width: 50px; margin-top: -12px;
            }
            .text-box h2 img
            {
                margin-top: 7px; width: 4.5vw;
            }
            .animate-number{font-size: 4.5vw;}
        }
    </style>
</head>


<script type="text/javascript">

    $('#sandbox-container input').datepicker({});

    $(function ()
    {
        $('#datetimepicker12').datetimepicker({
            format: 'HH:mm:ss',
            useCurrent: true,
        });
    });

    $(function ()
    {
        $('#datetimepicker13').datetimepicker({
            format: 'HH:mm:ss',
            useCurrent: true,
        });
    });

</script>
<body>
<%!
    private static Logger logger = new Logger("transactionSummary.jsp");
    private static TerminalManager terminalManager = new TerminalManager();
%>
<%
    Functions functions = new Functions();
    try
    {
        String uId = "";
        if (session.getAttribute("role").equals("submerchant"))
        {
            uId = (String) session.getAttribute("userid");
        }
        else
        {
            uId = (String) session.getAttribute("merchantid");
        }

        //List<TerminalVO> terminalVO = terminalManager.getTerminalsByMerchantId(session.getAttribute("merchantid").toString());
        String fromdate = null;
        String todate = null;
        bincountrysuccessful = Functions.checkStringNull((String) request.getAttribute("bincountrysuccessful"));
        bincountryfailed = Functions.checkStringNull((String) request.getAttribute("bincountryfailed"));
        ipcountrysuccessful = Functions.checkStringNull((String) request.getAttribute("ipcountrysuccessful"));
        ipcountryfailed = Functions.checkStringNull((String) request.getAttribute("ipcountryfailed"));
        String startTime = Functions.checkStringNull(request.getParameter("starttime"));
        String endTime = Functions.checkStringNull(request.getParameter("endtime"));
        if (startTime == null) startTime = "00:00:00";
        if (endTime == null) endTime = "23:59:59";
        String terminalid = Functions.checkStringNull(request.getParameter("terminalid")) == null ? "" : request.getParameter("terminalid");
        String currency = Functions.checkStringNull(request.getParameter("currency")) == null ? "" : request.getParameter("currency");
        String role = String.valueOf(session.getAttribute("role"));

        Date date = new Date();
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

        String Date = originalFormat.format(date);
        date.setDate(1);
        String fromDate = originalFormat.format(date);

        fromdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDate : request.getParameter("fromdate");
        todate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");

        /*if (startTime != null) str = str + "&starttime=" + startTime;
        if (endTime != null) str = str + "&endtime=" + endTime;   */

        ResourceBundle rb1 = null;
        String language_property1 = (String)session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);

        String transactionSummary_Transaction_Summary = !functions.isEmptyOrNull(rb1.getString("transactionSummary_Transaction_Summary"))?rb1.getString("transactionSummary_Transaction_Summary"): "Transaction Summary";
        String transactionSummary_from_date = !functions.isEmptyOrNull(rb1.getString("transactionSummary_from_date"))?rb1.getString("transactionSummary_from_date"): "From";
        String transactionSummary_start_time = !functions.isEmptyOrNull(rb1.getString("transactionSummary_start_time"))?rb1.getString("transactionSummary_start_time"): "From";
        String transactionSummary_to_date = !functions.isEmptyOrNull(rb1.getString("transactionSummary_to_date"))?rb1.getString("transactionSummary_to_date"): "To";
        String transactionSummary_end_time = !functions.isEmptyOrNull(rb1.getString("transactionSummary_end_time"))?rb1.getString("transactionSummary_end_time"): "To";
        String transactionSummary_currency = !functions.isEmptyOrNull(rb1.getString("transactionSummary_currency"))?rb1.getString("transactionSummary_currency"): "Currency";
        String transactionSummary_terminal_id1 = !functions.isEmptyOrNull(rb1.getString("transactionSummary_terminal_id1"))?rb1.getString("transactionSummary_terminal_id1"): "Terminal ID*";
        String transactionSummary_Search1 = !functions.isEmptyOrNull(rb1.getString("transactionSummary_Search1"))?rb1.getString("transactionSummary_Search1"): "Search";
        String transactionSummary_sorry = !functions.isEmptyOrNull(rb1.getString("transactionSummary_sorry"))?rb1.getString("transactionSummary_sorry"): "Sorry";
        String transactionSummary_status = !functions.isEmptyOrNull(rb1.getString("transactionSummary_status"))?rb1.getString("transactionSummary_status"): "Status records are not found.";
        String transactionSummary_filter = !functions.isEmptyOrNull(rb1.getString("transactionSummary_filter"))?rb1.getString("transactionSummary_filter"): "Filter";
        String transactionSummary_no_record = !functions.isEmptyOrNull(rb1.getString("transactionSummary_no_record"))?rb1.getString("transactionSummary_no_record"): "No records found for given search criteria.";
        String terminal_id = !functions.isEmptyOrNull(rb1.getString("Invalid_terminalid"))?rb1.getString("Invalid_terminalid"): "Invalid terminalid.";
%>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=transactionSummary_Transaction_Summary%></strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <form name="form" method="post"
                                      action="/merchant/servlet/TransactionSummary?ctoken=<%=ctoken%>" <%--class="form-horizontal"--%>>
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                                    <input type="hidden" value="<%=uId%>" name="merchantid" id="merchantid">
                                    <input type="hidden" value="<%=role%>" name="role" id="role">

                                    <%
                                        String terminalCurrency = "";
                                        StringBuffer terminalBuffer = new StringBuffer();
                                        if (request.getParameter("MES") != null)
                                        {
                                            String mes = request.getParameter("MES");
                                            if (mes.equals("ERR"))
                                            {
                                                if (request.getAttribute("errorM") != null)
                                                {
                                                    ValidationErrorList errorM = (ValidationErrorList) request.getAttribute("errorM");
                                                    for (Object errorList : errorM.errors())
                                                    {
                                                        ValidationException ve = (ValidationException) errorList;
                                                        String a = "";
                                                        if(ve.getMessage().contains("Invalid Terminal ID"))
                                                        {
                                                            a = terminal_id;
                                                        }
                                                        else
                                                        {
                                                            a = ve.getMessage();
                                                        }
//                                                        out.println("<center><li class=\"alert alert-danger alert-dismissable\"><b>" + ve.getMessage() + "</b></li></center>");
                                                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + a + "</h5>");
                                                    }
                                                }

                                                if (request.getAttribute("errorO") != null)
                                                {
                                                    ValidationErrorList errorO = (ValidationErrorList) request.getAttribute("errorO");
                                                    for (Object errorList : errorO.errors())
                                                    {
                                                        ValidationException ve = (ValidationException) errorList;
                                                        //out.println("<center><li class=\"alert alert-danger alert-dismissable\"><b>" + ve.getMessage() + "</b></li></center>");
                                                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + ve.getMessage() + "</h5>");
                                                    }
                                                }

                                                if (request.getAttribute("catchError") != null)
                                                {
                                                    //out.println("<center><li class=\"alert alert-danger alert-dismissable\"><b>" + request.getAttribute("catchError") + "</b></li></center>");
                                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + request.getAttribute("catchError") + "</h5>");
                                                }
                                            }
                                        }
                                        if (request.getAttribute("InvalidError") != null)
                                        {
                                            //out.println("<center><li class=\"alert alert-danger alert-dismissable\"><b>" + request.getAttribute("catchError") + "</b></li></center>");
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + request.getAttribute("InvalidError") + "</h5>");
                                        }
                                    %>

                                    <div class="form-group col-sm-12 col-md-3 col-lg-2">
                                        <label><%=transactionSummary_from_date%></label>
                                        <input type="text" name="fromdate" id="From_dt" class="datepicker form-control"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromdate)%>"
                                               readonly="readonly"
                                               style="cursor: auto;background-color: #ffffff;opacity: 1;">
                                    </div>

                                    <%--<div id="From_div" class="form-group col-sm-12 col-md-3 col-lg-2">
                                        <label class="hide_label">From</label>
                                        <input type="text" size="6" class="form-control" placeholder="HH:MM:SS" name="starttime" maxlength="8" value="<%=startTime%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                    </div>--%>

                                    <div id="From_div" class="form-group col-sm-12 col-md-3 col-lg-2">
                                        <div class="">
                                            <label class="hide_label"><%=transactionSummary_start_time%></label>

                                            <div class='input-group date'>
                                                <input type='text' id='datetimepicker12' class="form-control"
                                                       placeholder="HH:MM:SS" name="starttime" maxlength="8"
                                                       value="<%=startTime%>"
                                                       style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                                <%--<div id="datetimepicker12"></div>--%>
                                            </div>
                                        </div>
                                    </div>


                                    <div class="form-group col-sm-12 col-md-3 col-lg-2">
                                        <label><%=transactionSummary_to_date%></label>
                                        <input type="text" name="todate" id="To_dt" class="datepicker form-control"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(todate)%>"
                                               readonly="readonly"
                                               style="cursor: auto;background-color: #ffffff;opacity: 1;">

                                    </div>

                                    <%-- <div id="To_div" class="form-group form-group col-sm-12 col-md-3 col-lg-2">
                                         <label class="hide_label">To</label>
                                         <input type="text" size="6" class="form-control" placeholder="HH:MM:SS" name="endtime" maxlength="8" value="<%=endTime%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                     </div>--%>

                                    <div class="form-group col-sm-12 col-md-3 col-lg-2" id="From_div">
                                        <div class="">
                                            <label class="hide_label"><%=transactionSummary_end_time%></label>

                                            <div class='input-group date'>
                                                <input type='text' id='datetimepicker13' class="form-control"
                                                       placeholder="HH:MM:SS" name="endtime" maxlength="8"
                                                       value="<%=endTime%>"
                                                       style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                            </div>
                                        </div>
                                    </div>

                                    <div class="form-group col-md-4">
                                        <label for="curr"><%=transactionSummary_currency%></label>
                                        <input name="currency" id="curr" class="form-control" value="<%=currency%>"
                                               style="width:267px;" &ndash;%&gt; autocomplete="on">
                                    </div>

                                    <div class="form-group col-md-6 ui-widget">
                                        <label for="tid"><%=transactionSummary_terminal_id1%></label>
                                        <input name="terminalid" id="tid" class="form-control" value="<%=terminalid%>"
                                               style="width:267px;" autocomplete="on">
                                    </div>


                                    <%-- <div class="form-group col-md-3">
                                         <label >Terminal ID</label>
                                         <select name="terminalid" class="form-control">
                                             <option value="all">All</option>
                                             <%
                                                 terminalBuffer.append("(");
                                                 TerminalManager terminalManager=new TerminalManager();
                                                 List<TerminalVO> terminalVOList=terminalManager.getMemberandUserTerminalList(uId, String.valueOf(session.getAttribute("role")));
                                                 if(terminalVOList.size()>0)
                                                 {
                                             %>
                                             <option value="">Select Terminal ID</option>
                                             <%
                                                 for (TerminalVO terminalVO:terminalVOList)
                                                 {
                                                     String str1 = "";
                                                     String active = "";
                                                     if (terminalVO.getIsActive().equalsIgnoreCase("Y"))
                                                     {
                                                         active = "Active";
                                                     }
                                                     else
                                                     {
                                                         active = "InActive";
                                                     }
                                                     if(terminalid.equals(terminalVO.getTerminalId()))
                                                     {
                                                         terminalCurrency = terminalVO.getCurrency();
                                                         str1= "selected";
                                                     }
                                                     else
                                                         str1   = "";
                                                     if(terminalBuffer.length()!=0 && terminalBuffer.length()!=1)
                                                     {
                                                         terminalBuffer.append(",");
                                                     }
                                                     terminalBuffer.append(terminalVO.getTerminalId());
                                             %>
                                             <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO.getTerminalId())%>" <%=str1%>> <%=ESAPI.encoder().encodeForHTML(terminalVO.getTerminalId()+"-"+terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+terminalVO.getCurrency()+"-"+active)%> </option>
                                             <%
                                                 }
                                                 terminalBuffer.append(")");
                                             }
                                             else
                                             {
                                             %>
                                             <option value="NoTerminals">No Terminals Allocated</option>
                                             <%
                                                 }
                                             %>
                                         </select>

                                     </div>--%>

                                    <div class="form-group col-sm-12 col-md-4 col-lg-2">
                                        <label>&nbsp;</label>
                                        <button type="submit" class="btn btn-default" style="display: -webkit-box;"><i
                                                class="fa fa-save"></i>&nbsp;&nbsp;<%=transactionSummary_Search1%>
                                        </button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <%--<div class="row">

                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Summary Data</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content">--%>
            <br>
            <%--<div class="table-responsive">--%>
            <input type="hidden" name="terminalid" value="<%=terminalid%>">
            <input type="hidden" value="<%=terminalBuffer.toString()%>" name="terminalbuffer">
            <input type="hidden" name="currency" value="<%=terminalCurrency%>">
            <%--<div class="table-responsive datatable">--%>
            <%
                System.out.println("trminal id jsp ::::"+terminalid);
                TransactionReportVO transactionReportVO = (TransactionReportVO) request.getAttribute("transactionReportVO");
                if (transactionReportVO != null)
                {
                    HashMap<String, TransactionVO> transactionVOHashMap = transactionReportVO.getTransactionVOHashMap();
                    if (transactionVOHashMap.size() > 0)
                    {
                        double totalAmount = transactionReportVO.getTotalAmount();
                        long totalCount = transactionReportVO.getTotalCount();
                        TerminalVO TerminalVO = transactionReportVO.getTerminalVO();
                        InputDateVO inputDateVO = transactionReportVO.getInputDateVO();
                        Map<String, StringBuilder> statusViseChartContent = transactionReportVO.getChartContent();
                        String terminalId = Functions.checkStringNull(TerminalVO.getTerminalId()) == null ? "_" : "_" + TerminalVO.getTerminalId() + "_";
                        String salesPath = TerminalVO.getMemberId() + terminalId + StatusChartsType.SALES.toString() + "_" + inputDateVO.getsMinTransactionDate().replaceAll("[-:\\s]", "") + "_" + inputDateVO.getsMaxTransactionDate().replaceAll("[-:\\s]", "") + "_DATA.xml";
                        String refundPath = TerminalVO.getMemberId() + terminalId + StatusChartsType.REFUND.toString() + "_" + inputDateVO.getsMinTransactionDate().replaceAll("[-:\\s]", "") + "_" + inputDateVO.getsMaxTransactionDate().replaceAll("[-:\\s]", "") + "_DATA.xml";
                        String chargeBackPath = TerminalVO.getMemberId() + terminalId + StatusChartsType.CHARGEBACK.toString() + "_" + inputDateVO.getsMinTransactionDate().replaceAll("[-:\\s]", "") + "_" + inputDateVO.getsMaxTransactionDate().replaceAll("[-:\\s]", "") + "_DATA.xml";
                        logger.debug(" salesPath::" + salesPath);
                        logger.debug(" refundPath::" + refundPath);
                        logger.debug(" chargeBackPath::" + chargeBackPath);
                        System.out.println("dssds"+transactionReportVO.getTerminalVO().getTerminalId());
            %>
            <script>

                <%
                            String salesBarChartData = statusViseChartContent.containsKey(StatusChartsType.SALES.toString()) ? (statusViseChartContent.get(StatusChartsType.SALES.toString())).toString().replaceAll("", "") : "";
                            String refundBarChartData = statusViseChartContent.containsKey(StatusChartsType.REFUND.toString()) ? (statusViseChartContent.get(StatusChartsType.REFUND.toString())).toString().replaceAll("", "") : "";
                            String chargebackBarChartData = statusViseChartContent.containsKey(StatusChartsType.CHARGEBACK.toString()) ? (statusViseChartContent.get(StatusChartsType.CHARGEBACK.toString())).toString().replaceAll("", "") : "";
                %>

                window.onload = function ()
                {
                    salesChart();
                    refundChart();
                    chargebackChart();
                    donutChart1();
                    donutChart2();
                    donutChart3();
                    donutChart4();
                };

                $(document).ready(function ()
                {
                    $(window).resize(function ()
                    {
                        window.salesChart.redraw();
                        window.refundChart.redraw();
                        window.chargebackChart.redraw();
                        window.donutChart1.redraw();
                        window.donutChart2.redraw();
                        window.donutChart3.redraw();
                        window.donutChart4.redraw();
                    });
                });
                function salesChart()
                {
                    <%if (!salesBarChartData.equals("") && functions.isValueNull(TerminalVO.getTerminalId()))
                        {%>
                    window.salesChart = Morris.Bar(<%=salesBarChartData%>);
                    <%
                        }
                        else if(!salesBarChartData.equals("") && !functions.isValueNull(TerminalVO.getTerminalId()))
                        {%>
                    window.salesChart = Morris.Bar(
                            {
                                element: 'bar-example',
                                "data" : [ {
                                    "month" :"<h6><strong> Please Select Terminal ID To Display Chart</strong></h6>",
                                    "amount" : 0.0
                                } ],

                                xkey: 'amount',
                                ykeys:  "monthyear",
                                labels: "month",

                            });
                    document.getElementById('bar-example').innerHTML = "<h6><strong> Please Select Terminal ID To Display Chart</strong></h6>";
                    <%
                        }
                        else
                        {%>
                    window.salesChart = Morris.Bar(
                            {
                                element: 'bar-example',
                                "data" : [ {
                                    "month" : "<strong><h6>No data to display</strong></h6>",
                                    "amount" : 0.0
                                } ],

                                xkey: 'amount',
                                ykeys:  "monthyear",
                                labels: "month",

                            });
                    document.getElementById('bar-example').innerHTML = "<strong><h6>No data to display</strong></h6>";
                    <%
                        }
                            %>
                }

                function refundChart()
                {

                    <%if (!refundBarChartData.equals("") && functions.isValueNull(TerminalVO.getTerminalId()))
                        {%>
                    window.refundChart = Morris.Bar(<%=refundBarChartData%>);
                    <%
                        }
                         else if(!refundBarChartData.equals("") && !functions.isValueNull(TerminalVO.getTerminalId()))
                        {%>
                    window.refundChart = Morris.Bar(
                            {
                                element: 'refund',
                                "data" : [ {
                                    "month" : "<h6><strong> Please Select Terminal ID To Display Chart</strong></h6>",
                                    "amount" : 0.0
                                } ],

                                xkey: 'amount',
                                ykeys:  "monthyear",
                                labels: "month",

                            });
                    document.getElementById('refund').innerHTML = "<h6><strong> Please Select Terminal ID To Display Chart</strong></h6>";
                    <%
                        }
                        else
                        {%>
                    window.refundChart = Morris.Bar(
                            {
                                element: 'refund',
                                "data" : [ {
                                    "month" : "<strong><h6>No data to display</strong></h6>",
                                    "amount" : 0.0
                                } ],

                                xkey: 'amount',
                                ykeys:  "monthyear",
                                labels: "month",

                            });

                    document.getElementById('refund').innerHTML = "<strong><h6>No data to display</strong></h6>";
                    <%
                        }
                            %>
                }

                function chargebackChart()
                {
                    <%if (!chargebackBarChartData.equals("") && functions.isValueNull(TerminalVO.getTerminalId()))
                        {%>
                    window.chargebackChart = Morris.Bar(<%=chargebackBarChartData%>);
                    <%
                        }
                         else if(!chargebackBarChartData.equals("") && !functions.isValueNull(TerminalVO.getTerminalId()))
                        {%>
                    window.chargebackChart = Morris.Bar(
                            {
                                element: 'chargeback',
                                "data" : [ {
                                    "month" : "<h6><strong> Please Select Terminal ID To Display Chart</strong></h6>",
                                    "amount" : 0.0
                                } ],

                                xkey: 'amount',
                                ykeys:  "monthyear",
                                labels: "month",

                            });
                    document.getElementById('chargeback').innerHTML = "<h6><strong> Please Select Terminal ID To Display Chart</strong></h6>";
                    <%
                        }
                        else
                        {%>
                    window.chargebackChart = Morris.Bar(
                            {
                                element: 'chargeback',
                                "data" : [ {
                                    "month" : "<strong><h6>No data to display</strong></h6>",
                                    "amount" : 0.0
                                } ],

                                xkey: 'amount',
                                ykeys:  "monthyear",
                                labels: "month",

                            });
                    document.getElementById('chargeback').innerHTML = "<strong><h6>No data to display</strong></h6>";
                    <%
                        }
                            %>
                }

            </script>
            <div id="ALLDATA">
                <div class="row" <%--style="margin-left:2%;margin-right: 5%"--%>>

                    <div class="col-md-6 portlets ui-sortable">
                        <div class="widget">
                            <div class="widget-header transparent">
                                <h2><strong>Sales</strong> Chart</h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <div class="widget-content padding">
                                <div id="bar-example">

                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-6 portlets ui-sortable">
                        <div class="widget">
                            <div class="widget-header transparent">
                                <h2><strong>Refund</strong> Chart</h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <div class="widget-content padding">
                                <div id="refund">
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6 portlets ui-sortable">
                        <div class="widget">
                            <div class="widget-header transparent">
                                <h2><strong>Chargeback</strong> Chart</h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <div class="widget-content padding">
                                <div id="chargeback">

                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-6 portlets ui-sortable">
                        <div class="widget">
                            <%--<div class="ibox float-e-margins">
                                <font class="textb" style="margin-left:4%"><b>Member Transaction Report</b></font>
                            </div>--%>
                            <%--<div class="form foreground bodypanelfont_color panelbody_color">
                                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="/*background-color: #ffffff;*/ color:#34495e" ><i class="fa fa-th-large"></i>&nbsp;&nbsp;Member Transaction Report</h2>
                                <hr class="hrform">
                            </div>--%>

                            <div class="widget-header transparent" style="margin-bottom: inherit;">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Merchant Transaction Report</strong>
                                </h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <div class="ibox-content">
                                <div class="flot-chart">
                                    <table id="myTable" class="table table-striped table-bordered"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <thead>
                                        <tr>
                                            <td class="mtransreport" colspan="1" align="center" style="color: #ffffff;">
                                                Merchant Id: <%=TerminalVO.getMemberId()%>
                                            </td>
                                            <td class="mtransreport" colspan="2" align="center" style="color: #ffffff;">
                                                FromDate: <%=inputDateVO.getsMinTransactionDate()%>
                                                &nbsp;&nbsp;ToDate: <%=inputDateVO.getsMaxTransactionDate()%>
                                            </td>
                                        </tr>
                                        </thead>
                                        <thead>
                                        <tr style="color: white;">
                                            <th style="text-align: center;color: #ffffff;">Status</th>
                                            <th style="text-align: center;color: #ffffff;">Transaction</th>
                                            <th style="text-align: center;color: #ffffff;">Total Amount</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <%
                                            for (Map.Entry<String, TransactionVO> transactionVOPair : transactionVOHashMap.entrySet())
                                            {
                                                TransactionVO transactionVO = transactionVOPair.getValue();

                                        %>
                                        <tr>
                                            <td data-label="Status"
                                                style="text-align: center"><%=transactionVO.getStatus()%>
                                            </td>
                                            <td data-label="Transaction"
                                                style="text-align: center"><%=transactionVO.getCount()%>
                                            </td>
                                            <td data-label="Total Amount"
                                                style="text-align: center">
                                                <%=Functions.convert2Decimal(transactionVO.getCaptureAmount())%>
                                            </td>
                                        </tr>
                                        <% //tempcount = tempcount+Integer.parseInt(count);

                                        }
                                       /* out.println("<tr>");
                                        out.println("<td colspan=\"3\" align=\"center\" style=\"font-weight: bold;\">TOTAL Transaction : " + totalCount + " </td>");
                                        out.println("</tr >");
                                        out.println("<tr>");
                                        out.println("<td colspan=\"3\" align=\"center\" style=\"font-weight: bold;\">GRAND Total Amount : " + Functions.convert2Decimal(totalAmount) + " </td>");
                                        out.println("</tr>");*/

                                        %>
                                        </tbody>
                                    </table>
                                    <br>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-md-6 portlets ui-sortable">
                        <div class="widget">
                            <div class="widget-header transparent">
                                <h2><strong>Bin Country Successful</strong> Chart</h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <div class="widget-content padding">
                                <div id="bar-example1" style="font-size: 10px" >

                                </div>
                            </div>
                        </div>
                    </div>

                    <%--<div class="row">--%>
                    <div class="col-md-6 portlets ui-sortable">
                        <div class="widget">
                            <div class="widget-header transparent">
                                <h2><strong>Bin Country Failed</strong> Chart</h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <div class="widget-content padding">
                                <div id="bar-example2" style="font-size: 10px" >

                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-6 portlets ui-sortable">
                        <div class="widget">
                            <div class="widget-header transparent">
                                <h2><strong>Ip Country Successful </strong> Chart</h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <div class="widget-content padding">
                                <div id="bar-example3" style="font-size: 10px" >

                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="col-md-6 portlets ui-sortable">
                        <div class="widget">
                            <div class="widget-header transparent">
                                <h2><strong>Ip Country Failed </strong> Chart</h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <div class="widget-content padding">
                                <div id="bar-example4" style="font-size: 10px" >

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div style="left: 40%; position: relative;" >
                <button type="button" id="download"  class="btn btn-default" style="font-size: 18px;" onclick="getPDF()"> Export To Pdf</button>
            </div>


            <% }
            else
            {
                out.println(Functions.NewShowConfirmation1(transactionSummary_sorry, transactionSummary_status));
            }
            }
            else
            {
                out.println(Functions.NewShowConfirmation1(transactionSummary_filter, transactionSummary_no_record));
            }
            }
           /* catch(PZDBViolationException dbe)
            {
                logger.error("Db exception::",dbe);
                out.println("<br>");
                out.println("<br>");
                out.println("<br>");
                out.println("<div class=\"table-responsive datatable\" style=\"margin-top:100px\">");
                out.println(Functions.NewShowConfirmation1("Sorry","Kindly check Transaction Summary after some time."));
                out.println("</div>");
                PZExceptionHandler.handleDBCVEException(dbe, session.getAttribute("merchantid").toString(), PZOperations.TRANSACTION_SUMMARY);
            }*/
            catch (Exception e)
            {
                logger.error("Generic exception in transaction summary", e);
                out.println("<br>");
                out.println("<br>");
                out.println("<br>");
                out.println("<div class=\"table-responsive datatable\" style=\"margin-top:100px\">");
                out.println(Functions.NewShowConfirmation1("Sorry", "Kindly check Transaction Summary after some time."));
                out.println("</div>");
                PZExceptionHandler.raiseAndHandleGenericViolationException("transactionSummary.jsp", "doService()", null, "Merchant", "Generic exception while getting Transaction Summary", null, e.getMessage(), e.getCause(), session.getAttribute("merchantid").toString(), PZOperations.TRANSACTION_SUMMARY);
            }
            %>
        </div>
    </div>
</div>
</div>
</div>
<script>
    function donutChart1()
    {
        window.donutChart1 = Morris.Donut
        (
                {
                    element: 'bar-example1',
                    <%=bincountrysuccessful%>
                    resize: true,
                    responsive: true,
                    colors: [
                        '#68c39f',
                        '#edce8c',
                        '#b4c0c0',
                        '#e8a451',
                        '#e8e85a',
                        '#4a525f',
                        '#99e6ff',
                        '#b399ff',
                        '#6fc51b',
                        '#ff99e6',
                        '#ff9999',
                        '#3b76c7',
                        '#d689c0',
                        '#b93a71',
                        '#990000',
                        '#54ce28',
                        '#68c39f',
                        '#edce8c',
                        '#4A2CC9',
                        '#C92C5C',
                    ],
                    formatter: function (x)
                    {
                        return x
                    }
                }).on('click', function (i, row)
                {
                    console.log(i, row);
                });
    }

    function donutChart2()
    {
        //console.log("inside pie chart")
        window.donutChart2 = Morris.Donut
        (
                {
                    element: 'bar-example2',
                    <%=bincountryfailed%>
                    resize: true,
                    responsive: true,
                    colors: [
                        '#68c39f',
                        '#edce8c',
                        '#b4c0c0',
                        '#e8a451',
                        '#e8e85a',
                        '#4a525f',
                        '#99e6ff',
                        '#b399ff',
                        '#6fc51b',
                        '#ff99e6',
                        '#ff9999',
                        '#3b76c7',
                        '#d689c0',
                        '#b93a71',
                        '#990000',
                        '#54ce28',
                        '#68c39f',
                        '#edce8c',
                        '#4A2CC9',
                        '#C92C5C',
                    ],
                    formatter: function (x)
                    {
                        return x
                    }
                }).on('click', function (i, row)
                {
                    console.log(i, row);
                });
    }

    function donutChart3()
    {
        console.log("inside pie chart")
        window.donutChart3 = Morris.Donut
        (
                {
                    element: 'bar-example3',
                    <%=ipcountrysuccessful%>
                    resize: true,
                    responsive: true,
                    colors: [
                        '#68c39f',
                        '#edce8c',
                        '#b4c0c0',
                        '#e8a451',
                        '#e8e85a',
                        '#4a525f',
                        '#99e6ff',
                        '#b399ff',
                        '#6fc51b',
                        '#ff99e6',
                        '#ff9999',
                        '#3b76c7',
                        '#d689c0',
                        '#b93a71',
                        '#990000',
                        '#54ce28',
                        '#68c39f',
                        '#edce8c',
                        '#4A2CC9',
                        '#C92C5C',
                    ],
                    formatter: function (x)
                    {
                        return x
                    }
                }).on('click', function (i, row)
                {
                    console.log(i, row);
                });
    }

    function donutChart4()
    {
        console.log("inside pie chart")
        window.donutChart4 = Morris.Donut
        (
                {
                    element: 'bar-example4',
                    <%=ipcountryfailed%>
                    resize: true,
                    responsive: true,
                    colors: [
                        '#68c39f',
                        '#edce8c',
                        '#b4c0c0',
                        '#e8a451',
                        '#e8e85a',
                        '#4a525f',
                        '#99e6ff',
                        '#b399ff',
                        '#6fc51b',
                        '#ff99e6',
                        '#ff9999',
                        '#3b76c7',
                        '#d689c0',
                        '#b93a71',
                        '#990000',
                        '#54ce28',
                        '#68c39f',
                        '#edce8c',
                        '#4A2CC9',
                        '#C92C5C',
                    ],
                    formatter: function (x)
                    {
                        return x
                    }
                }).on('click', function (i, row)
                {
                    console.log(i, row);
                });
    }
    function getPDF()
    {
        var HTML_Width = $("#ALLDATA").width();
        var HTML_Height = $("#ALLDATA").height();
        var top_left_margin = 40;
        var PDF_Width = HTML_Width+(top_left_margin*2);
        var PDF_Height = (PDF_Width*1.5)+(top_left_margin*2);
        var canvas_image_width = HTML_Width;
        var canvas_image_height = HTML_Height;
        var totalPDFPages = Math.ceil(HTML_Height/PDF_Height)-1;

        html2canvas($("#ALLDATA")[0],{allowTaint:true}).then(function(canvas) {
            canvas.getContext('2d');

            console.log(canvas.height+"  "+canvas.width);

            var imgData = canvas.toDataURL("image/jpeg", 5.0);
            var pdf = new jsPDF('p', 'pt',  [PDF_Width, PDF_Height]);

            pdf.addImage(imgData, 'JPG', top_left_margin, top_left_margin,canvas_image_width,canvas_image_height);


            for (var i = 1; i <= totalPDFPages; i++) {
                pdf.addPage(PDF_Width, PDF_Height);
                pdf.addImage(imgData, 'JPG', top_left_margin, -(PDF_Height*i)+(top_left_margin*4),canvas_image_width,canvas_image_height);
            }

            pdf.save("TransactionSummary.pdf");
        });
    }

</script>
</body>
</html>