<%--
  Created by IntelliJ IDEA.
  User: Rajeev Singh
  Date: 9/10/18
  Time: 12:35 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page errorPage="error.jsp"%>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.*" %>
<%@ include file="Top.jsp" %>

<%!
    Logger log = new Logger("dashBoard.jsp");
%>
<%
    Functions functions = new Functions();
    String company =(String)session.getAttribute("company");
    // Edited Ganesh ///
    session.setAttribute("submit","DashBoard");

%>
<html lang="en">
<head>

    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <title><%=company%> Merchant Dashboard</title>
    <style type="text/css">
        svg:not(:root) {
            overflow: inherit!important;
        }

        .lightblue-1 .royalblue-4{
            width: 104%;
        }

        .widget .text-box .maindata {
            font-size: 10px !important;
        }

        .widget .text-box .maindata b {
            font-size: 11px !important;
        }
    </style>

    <script src="/merchant/NewCss/libs/jquery-animate-numbers/jquery.animateNumbers.js"></script>
    <link href="/merchant/NewCss/libs/ios7-switch/ios7-switch.css" rel="stylesheet" />
    <link href="/merchant/NewCss/libs/pace/pace.css" rel="stylesheet" />
    <link href="/merchant/NewCss/css/style.css" rel="stylesheet" type="text/css" />
    <link href="/merchant/NewCss/css/style-responsive.css" rel="stylesheet" />
    <script src="/merchant/NewCss/libs/morrischart/morris.min.js"></script>
    <script src="/merchant/NewCss/morrisJS/raphael-min.js"></script>
    <script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script src="/merchant/NewCss/js/jquery.barfiller.js" type="text/javascript"></script>
    <link href="/merchant/NewCss/css/style_BarFiller.css" rel="stylesheet" />

</head>
<body>

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
    .morris-hover.morris-default-style .morris-hover-row-label
    {
        font-weight:bold;
        margin:0.25em 0;
    }
    .morris-hover.morris-default-style .morris-hover-point
    {
        white-space:nowrap;
        margin:0.1em 0;
    }
    svg { width: 100%; }
    #currencyid{
        position: absolute;
        right: 0px;
        padding: 5px
    }
    @media(max-width: 620px)
    {
        #currencyid{
            position: initial;
        }
    }
    @media (min-width: 1200px)
    {
        .col-lg-3
        {
            /*width: 18.6%;*/
            width: 20%;
        }
    }

    @media(min-width:1200px){
        .widget-icon img{width: 50px; margin-top: -12px;}
        .text-box h2 img{ margin-top: 4px; width: 1.5vw;}
        .animate-number{font-size: 1.5vw;}
    }
    @media(min-width:992px) and (max-width: 1199px) {
        .widget-icon img{width: 50px; margin-top: -12px;}
        .text-box h2 img{margin-top: -1px;}
        .animate-number{font-size: 2.5vw;}
    }
    @media(min-width:768px) and (max-width:991px){
        .widget-icon img{width: 50px; margin-top: -12px;}
        .text-box h2 img{ margin-top: 3px; width: 2.5vw;}
        .animate-number{font-size: 2.5vw;}
    }
    @media(min-width:480px) and (max-width:767px){
        .widget-icon img{width: 50px; margin-top: -12px;}
        .text-box h2 img{ margin-top: 4px; width: 3.5vw;}
        .animate-number{font-size: 3vw;}
    }
    @media(max-width:479px){
        .widget-icon img{width: 50px; margin-top: -12px;}
        .text-box h2 img{margin-top: 7px; width: 4.5vw;}
        .animate-number{font-size: 4.5vw;}
    }
</style>
<script>
    window.onload = function()
    {
        donutChart1();
        donutChart2();
        barChart1();
    };

    $(document).ready(function()
    {
        $('#bar1').barfiller({ barColor: '#68c39f', duration: 3000 });
        $('#bar2').barfiller({ barColor: '#900', duration: 3000 });
        $('#bar3').barfiller({ barColor: '#b93a71',duration: 3000 });
        $('#bar4').barfiller({ barColor: '#Abb7b7', duration: 3000 });
        $('#bar5').barfiller({ barColor: '#ff999', duration: 3000 });
        $('#bar6').barfiller({ barColor: '#4a525f', duration: 3000 });
        $('#bar7').barfiller({ barColor: '#54ce28',duration: 3000 });
        $('#bar8').barfiller({ barColor: '#C92C5C', duration: 3000 });
        $('#bar9').barfiller({ barColor: '#4A2CC9', duration: 3000 });
        $('#bar10').barfiller({ barColor: '#99e6ff', duration: 3000 });
        $('#bar11').barfiller({ barColor: '#99e6ff', duration: 3000 });
        $('#bar12').barfiller({ barColor: '#C92C5C', duration: 3000 });
        $('#bar13').barfiller({ barColor: '#68c39f', duration: 3000 });
    });
</script>


<%
    HashMap<String,String> totalAmount= (HashMap<String, String>) request.getAttribute("totalAmount");

    String[] hashtablesale = (String[])request.getAttribute("hashtablesale");
    String salesamount="";
    String salescount="";
    if (hashtablesale!= null)
    {
        salesamount= hashtablesale[0];
        salescount= hashtablesale[1];
    }

  /*  String[] hashtablewithdraw = (String[]) request.getAttribute("hashtablewithdraw");
    String withdrawamt="";
    String withdrawcount="";
    if (hashtablewithdraw!= null)
    {
        withdrawamt= hashtablewithdraw[0];
        withdrawcount= hashtablewithdraw[1];
    }*/

    /*String[] hashtabledeclined = (String[]) request.getAttribute("hashtabledeclined");
    String declinedamt= "";
    String declinedcount="";
    if (hashtabledeclined!= null)
    {
        declinedamt=hashtabledeclined[0];
        declinedcount=hashtabledeclined[1];
    }*/

    String salesPerCurrencyDonutChartJsonStr = (String) request.getAttribute("salesPerCurrencyDonutChartJsonStr");
    String statusChartDonutChartJsonStr = (String) request.getAttribute("statusChartDonutChartJsonStr");
    String salesBarChartJsonStr ="";
    if (request.getAttribute("salesBarChartJsonStr")!= null)
    {
         salesBarChartJsonStr =(String)request.getAttribute("salesBarChartJsonStr");
    }

    String salesBarChartLabelJsonStr =(String)request.getAttribute("salesBarChartLabelJsonStr");

    int anglevalue=0;
    String datelabel="";
    if(request.getAttribute("datelabel")!= null)
    {
         datelabel= (String) request.getAttribute("datelabel");
    }
    if(datelabel.equalsIgnoreCase("Current month") || datelabel.equalsIgnoreCase("Last month"))
    {
        anglevalue=60;
    }

    HashMap<String, String> datefilter = new LinkedHashMap();
    datefilter.put("last_seven_days_key","Last seven days");
    datefilter.put("today_key","Today");
    datefilter.put("current_month_key","Current month");
    datefilter.put("last_month_key","Last month");
    datefilter.put("last_six_months_key","Last six months");

    String   dateModestr = "";
    if(request.getParameter("datefilter") != null){
        dateModestr  = request.getParameter("datefilter");
    }else{
        dateModestr  = "";
    }

    String status= (String) request.getAttribute("status");
    String currency1 = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");
    String payMode1 = Functions.checkStringNull(request.getParameter("payMode"))==null?"":request.getParameter("payMode");
    String payBrand1 = Functions.checkStringNull(request.getParameter("payBrand"))==null?"":request.getParameter("payBrand");
    String status1 = Functions.checkStringNull(request.getParameter("status"))==null?"":request.getParameter("status");

    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String dashBoard_total = !functions.isEmptyOrNull(rb1.getString("dashBoard_total"))?rb1.getString("dashBoard_total"): "TOTAL";
    String dashBoard_sales = !functions.isEmptyOrNull(rb1.getString("dashBoard_sales"))?rb1.getString("dashBoard_sales"): "SALES";
    String dashBoard_TOTAL_SETTLED = !functions.isEmptyOrNull(rb1.getString("dashBoard_TOTAL_SETTLED"))?rb1.getString("dashBoard_TOTAL_SETTLED"): "TOTAL";
    String dashBoard_SETTLED = !functions.isEmptyOrNull(rb1.getString("dashBoard_SETTLED"))?rb1.getString("dashBoard_SETTLED"): "SETTLED";
    String dashBoard_total_refund = !functions.isEmptyOrNull(rb1.getString("dashBoard_total_refund"))?rb1.getString("dashBoard_total_refund"): "TOTAL";
    String dashBoard_refund = !functions.isEmptyOrNull(rb1.getString("dashBoard_refund"))?rb1.getString("dashBoard_refund"): " REFUND";
    String dashBoard_total_chargeback = !functions.isEmptyOrNull(rb1.getString("dashBoard_total_chargeback"))?rb1.getString("dashBoard_total_chargeback"): "TOTAL";
    String dashBoard_chargeback = !functions.isEmptyOrNull(rb1.getString("dashBoard_chargeback"))?rb1.getString("dashBoard_chargeback"): "CHARGEBACK";
    String dashBoard_total_declined = !functions.isEmptyOrNull(rb1.getString("dashBoard_total_declined"))?rb1.getString("dashBoard_total_declined"): "TOTAL";
    String dashBoard_declined = !functions.isEmptyOrNull(rb1.getString("dashBoard_declined"))?rb1.getString("dashBoard_declined"): "DECLINED";
    String dashBoard_all_currency = !functions.isEmptyOrNull(rb1.getString("dashBoard_all_currency"))?rb1.getString("dashBoard_all_currency"): "All Currency";
    String dashBoard_Payment_Mode = !functions.isEmptyOrNull(rb1.getString("dashBoard_Payment_Mode"))?rb1.getString("dashBoard_Payment_Mode"): "Payment Mode";
    String dashBoard_Payment_Brand = !functions.isEmptyOrNull(rb1.getString("dashBoard_Payment_Brand"))?rb1.getString("dashBoard_Payment_Brand"): "Payment Brand";
    String dashBoard_Sales = !functions.isEmptyOrNull(rb1.getString("dashBoard_Sales"))?rb1.getString("dashBoard_Sales"): "Sales";
    String dashBoard_chart = !functions.isEmptyOrNull(rb1.getString("dashBoard_chart"))?rb1.getString("dashBoard_chart"): "Chart";
    String dashBoard_sales_per_currency = !functions.isEmptyOrNull(rb1.getString("dashBoard_sales_per_currency"))?rb1.getString("dashBoard_sales_per_currency"): "Sales Per Currency";
    String dashBoard_chart1 = !functions.isEmptyOrNull(rb1.getString("dashBoard_chart1"))?rb1.getString("dashBoard_chart1"): "Chart";
    String dashBoard_Status = !functions.isEmptyOrNull(rb1.getString("dashBoard_Status"))?rb1.getString("dashBoard_Status"): "Status";
    String dashBoard_Chart = !functions.isEmptyOrNull(rb1.getString("dashBoard_Chart"))?rb1.getString("dashBoard_Chart"): "Chart";
    String dashBoard_Progress = !functions.isEmptyOrNull(rb1.getString("dashBoard_Progress"))?rb1.getString("dashBoard_Progress"): "Progress";
    String dashBoard_Status1 = !functions.isEmptyOrNull(rb1.getString("dashBoard_Status1"))?rb1.getString("dashBoard_Status1"): "Status";
    String dashBoard_AuthStarted = !functions.isEmptyOrNull(rb1.getString("dashBoard_AuthStarted"))?rb1.getString("dashBoard_AuthStarted"): "AuthStarted";
    String dashBoard_AuthSuccessful = !functions.isEmptyOrNull(rb1.getString("dashBoard_AuthSuccessful"))?rb1.getString("dashBoard_AuthSuccessful"): "AuthSuccessful";
    String dashBoard_CaptureSuccess = !functions.isEmptyOrNull(rb1.getString("dashBoard_CaptureSuccess"))?rb1.getString("dashBoard_CaptureSuccess"): "CaptureSuccess";
    String dashBoard_Chargeback = !functions.isEmptyOrNull(rb1.getString("dashBoard_Chargeback"))?rb1.getString("dashBoard_Chargeback"): "Chargeback";
    String dashBoard_MarkedForReversal1 = !functions.isEmptyOrNull(rb1.getString("dashBoard_MarkedForReversal1"))?rb1.getString("dashBoard_MarkedForReversal1"): "MarkedForReversal";
    String dashBoard_Reversed = !functions.isEmptyOrNull(rb1.getString("dashBoard_Reversed"))?rb1.getString("dashBoard_Reversed"): "Reversed";
    String dashBoard_Settled = !functions.isEmptyOrNull(rb1.getString("dashBoard_Settled"))?rb1.getString("dashBoard_Settled"): "Settled";
    String dashBoard_Failed = !functions.isEmptyOrNull(rb1.getString("dashBoard_Failed"))?rb1.getString("dashBoard_Failed"): "Failed";
    String dashBoard_AuthFailed = !functions.isEmptyOrNull(rb1.getString("dashBoard_AuthFailed"))?rb1.getString("dashBoard_AuthFailed"): "AuthFailed";
    String dashBoard_PartialRefund = !functions.isEmptyOrNull(rb1.getString("dashBoard_PartialRefund"))?rb1.getString("dashBoard_PartialRefund"): "PartialRefund";
    String dashBoard_WITHDRAW_AMOUNT = !functions.isEmptyOrNull(rb1.getString("dashBoard_WITHDRAW_AMOUNT"))?rb1.getString("dashBoard_WITHDRAW_AMOUNT"): "LAST SETTLEMENT AMOUNT";

    StringBuffer colors= new StringBuffer();

    StringBuffer currencyWithColor = new StringBuffer();
    List<String> list =(List)request.getAttribute("list");
    List<String> listDb =(List)request.getAttribute("listDb");
    String cur="";
    colors = (StringBuffer)request.getAttribute("colorFromCurrencyProperties");

    HashMap<String,String> payBrandList =(HashMap)request.getAttribute("payBrandList");
    HashMap<String,String> payModeList =(HashMap)request.getAttribute("payModeList");
    HashMap<String,String> statusList= new LinkedHashMap<>();
    statusList.put("sales","sales");
    statusList.put("payout","payout");

    String   statusModeStr = "";
    if(request.getParameter("status") != null){
        statusModeStr  =  request.getParameter("status");
    }else{
        statusModeStr  = "";
    }

    if (functions.isValueNull(String.valueOf(request.getAttribute("hashMapValidStatusChart"))))
    {
        HashMap<String, String> getProgressBarFromGetValidStatusChartForPartner = (HashMap<String, String>) request.getAttribute("hashMapValidStatusChart");

        int authFailed = 0;
        int captureSuccess = 0;
        int settled = 0;
        int failed = 0;
        int markedForReversal = 0;
        int chargeBack = 0;
        int authStarted = 0;
        int authSuccessful = 0;
        int reversed = 0;
        int partialRefund = 0;
        int payoutsuccessful= 0;
        int payoutFailed= 0;
        int payoutStarted= 0;

        if (functions.isValueNull((getProgressBarFromGetValidStatusChartForPartner.get("authfailed"))))
        {
            authFailed = Integer.parseInt(getProgressBarFromGetValidStatusChartForPartner.get("authfailed"));
        }
        if (functions.isValueNull((getProgressBarFromGetValidStatusChartForPartner.get("capturesuccess"))))
        {
            captureSuccess = Integer.parseInt(getProgressBarFromGetValidStatusChartForPartner.get("capturesuccess"));
        }
        if (functions.isValueNull((getProgressBarFromGetValidStatusChartForPartner.get("settled"))))
        {
            settled = Integer.parseInt(getProgressBarFromGetValidStatusChartForPartner.get("settled"));
        }
        if (functions.isValueNull((getProgressBarFromGetValidStatusChartForPartner.get("failed"))))
        {
            failed = Integer.parseInt(getProgressBarFromGetValidStatusChartForPartner.get("failed"));
        }
        if (functions.isValueNull((getProgressBarFromGetValidStatusChartForPartner.get("markedforreversal"))))
        {
            markedForReversal = Integer.parseInt(getProgressBarFromGetValidStatusChartForPartner.get("markedforreversal"));
        }

        if (functions.isValueNull((getProgressBarFromGetValidStatusChartForPartner.get("chargeback"))))
        {
            chargeBack = Integer.parseInt(getProgressBarFromGetValidStatusChartForPartner.get("chargeback"));
        }
        if (functions.isValueNull((getProgressBarFromGetValidStatusChartForPartner.get("authstarted"))))
        {
            authStarted = Integer.parseInt(getProgressBarFromGetValidStatusChartForPartner.get("authstarted"));
        }
        if (functions.isValueNull((getProgressBarFromGetValidStatusChartForPartner.get("authsuccessful"))))
        {
            authSuccessful = Integer.parseInt(getProgressBarFromGetValidStatusChartForPartner.get("authsuccessful"));
        }
        if (functions.isValueNull((getProgressBarFromGetValidStatusChartForPartner.get("reversed"))))
        {
            reversed = Integer.parseInt(getProgressBarFromGetValidStatusChartForPartner.get("reversed"));
        }
        if (functions.isValueNull((getProgressBarFromGetValidStatusChartForPartner.get("partialrefund"))))
        {
            partialRefund = Integer.parseInt(getProgressBarFromGetValidStatusChartForPartner.get("partialrefund"));
        }
        if (functions.isValueNull((getProgressBarFromGetValidStatusChartForPartner.get("payoutsuccessful"))))
        {
            payoutsuccessful = Integer.parseInt(getProgressBarFromGetValidStatusChartForPartner.get("payoutsuccessful"));
        }
        if (functions.isValueNull((getProgressBarFromGetValidStatusChartForPartner.get("payoutfailed"))))
        {
            payoutFailed= Integer.parseInt(getProgressBarFromGetValidStatusChartForPartner.get("payoutfailed"));
        }
        if (functions.isValueNull((getProgressBarFromGetValidStatusChartForPartner.get("payoutstarted"))))
        {
            payoutStarted= Integer.parseInt(getProgressBarFromGetValidStatusChartForPartner.get("payoutstarted"));
        }
        int totalCount = (authStarted + authSuccessful + captureSuccess + chargeBack + markedForReversal + reversed + settled + failed + authFailed + partialRefund + payoutsuccessful +payoutFailed +payoutStarted);

        String countAuthStarted = "";
        String countAuthSuccessful = "";
        String countCaptureSuccess = "";
        String countChargeBack = "";
        String countMarkedForReversal = "";
        String countReversed = "";
        String countSettled = "";
        String countFailed = "";
        String countAuthFailed = "";
        String countPartialRefund = "";
        String countPayoutSuccessful = "";
        String countPayoutFailed = "";
        String countPayoutStarted = "";

        if (functions.isValueNull(String.valueOf((double) authStarted)) && authStarted != 0 && functions.isValueNull(String.valueOf((double) totalCount)) && totalCount != 0 )
        {
            double authStarted1 = ((double) authStarted / (double) totalCount) * 100;
            if (functions.isValueNull(String.valueOf(authStarted1)))
            {
                countAuthStarted = functions.convert2Decimal(String.valueOf(authStarted1));
            }
        }

        if (functions.isValueNull(String.valueOf((double) authSuccessful)) && authSuccessful != 0 && functions.isValueNull(String.valueOf((double) totalCount)) && totalCount != 0)
        {
            double authSuccessful1 = ((double) authSuccessful / (double) totalCount) * 100;
            if (functions.isValueNull(String.valueOf(authSuccessful1)))
            {
                countAuthSuccessful = functions.convert2Decimal(String.valueOf(authSuccessful1));
            }
        }

        if (functions.isValueNull(String.valueOf((double) captureSuccess)) && captureSuccess !=0 && functions.isValueNull(String.valueOf((double) totalCount)) && totalCount != 0)
        {
            double captureSuccess1 = ((double) captureSuccess / (double) totalCount) * 100;
            if (functions.isValueNull(String.valueOf(captureSuccess1)))
            {
                countCaptureSuccess = functions.convert2Decimal(String.valueOf(captureSuccess1));
            }
        }

        if (functions.isValueNull(String.valueOf((double) chargeBack)) && chargeBack != 0 && functions.isValueNull(String.valueOf((double) totalCount)) && totalCount != 0)
        {
            double chargeBack1 = ((double) chargeBack / (double) totalCount) * 100;
            if (functions.isValueNull(String.valueOf(chargeBack1)))
            {
                countChargeBack = functions.convert2Decimal(String.valueOf(chargeBack1));
            }
        }

        if (functions.isValueNull(String.valueOf((double) markedForReversal)) && markedForReversal != 0 && functions.isValueNull(String.valueOf((double) totalCount)) && totalCount != 0)
        {
            double markedForReversal1 = ((double) markedForReversal / (double) totalCount) * 100;
            if (functions.isValueNull(String.valueOf(markedForReversal1)))
            {
                countMarkedForReversal = functions.convert2Decimal(String.valueOf(markedForReversal1));
            }
        }

        if (functions.isValueNull(String.valueOf((double) reversed)) && reversed != 0 && functions.isValueNull(String.valueOf((double) totalCount)) && totalCount != 0)
        {
            double reversed1 = ((double) reversed / (double) totalCount) * 100;
            if (functions.isValueNull(String.valueOf(reversed1)))
            {
                countReversed = functions.convert2Decimal(String.valueOf(reversed1));
            }
        }

        if (functions.isValueNull(String.valueOf((double) settled)) && settled != 0 && functions.isValueNull(String.valueOf((double) totalCount)) && totalCount != 0)
        {
            double settled1 = ((double) settled / (double) totalCount) * 100;
            if (functions.isValueNull(String.valueOf(settled1)))
            {
                countSettled = functions.convert2Decimal(String.valueOf(settled1));
            }
        }

        if (functions.isValueNull(String.valueOf((double) failed)) && failed != 0 && functions.isValueNull(String.valueOf((double) totalCount)) && totalCount != 0)
        {
            double failed1 = ((double) failed / (double) totalCount) * 100;
            countFailed = functions.convert2Decimal(String.valueOf(failed1));
        }

        if (functions.isValueNull(String.valueOf((double) authFailed))&& authFailed != 0 && functions.isValueNull(String.valueOf((double) totalCount)) && totalCount != 0)
        {
            double authFailed1 = ((double) authFailed / (double) totalCount) * 100;
            countAuthFailed = functions.convert2Decimal(String.valueOf(authFailed1));
        }

        if (functions.isValueNull(String.valueOf((double) partialRefund))&& partialRefund != 0 && functions.isValueNull(String.valueOf((double) totalCount)) && totalCount != 0)
        {
            double partialRefund1 = ((double) partialRefund / (double) totalCount) * 100;
            countPartialRefund = functions.convert2Decimal(String.valueOf(partialRefund1));
        }
        if (functions.isValueNull(String.valueOf((double) payoutsuccessful))&& payoutsuccessful != 0 && functions.isValueNull(String.valueOf((double)totalCount)) && totalCount!=0)
        {
            double payoutSuccessful1= ((double) payoutsuccessful / (double) totalCount)* 100;
            countPayoutSuccessful = functions.convertDecimalToInt(String.valueOf(payoutSuccessful1));
        }

        if (functions.isValueNull(String.valueOf((double) payoutFailed)) && payoutFailed!= 0 && functions.isValueNull(String.valueOf((double) totalCount)) && totalCount!=0)
        {
            double payoutFailed1= ((double) payoutFailed / (double) totalCount) * 100;
            countPayoutFailed= functions.convert2Decimal(String.valueOf(payoutFailed1));
        }
        if (functions.isValueNull(String.valueOf((double) payoutStarted)) && payoutStarted!= 0 && functions.isValueNull(String.valueOf((double)totalCount)) && totalCount!=0)
        {
            double payoutStarted1= ((double) payoutStarted / (double)totalCount) *100;
            countPayoutStarted= functions.convert2Decimal(String.valueOf(payoutStarted1));
        }
%>

<script>
    $(document).ready(function()
    {
        $(window).resize(function()
        {
            window.donutChart1.redraw();
            window.donutChart2.redraw();
            window.barChart1.redraw();
        });
    });

    function donutChart1()
    {
        window.donutChart1 = Morris.Donut
        (
                {
                    element: 'bar-example1',
                    <%=salesPerCurrencyDonutChartJsonStr%>
                    resize: true,
                    responsive: true,
                    colors: [<%=colors.toString()%>],
                    formatter: function (x)
                    {
                        return x
                    }
                }).on('click', function (i, row)
                {
                    console.log(i, row);
                });
    }

    function barChart1()
    {
        window.barChart1 = Morris.Bar(
                {
                    element: 'bar-example3',
                    <%=salesBarChartJsonStr%>
                    xkey: 'x',
                    gridTextSize: 15,
                    xLabelAngle:<%=anglevalue%> ,
                    ykeys:  [<%=salesBarChartLabelJsonStr%>],
                    labels: [<%=salesBarChartLabelJsonStr%>],
                    stacked: true,
                    resize: true,
                    responsive: true,
                    color:[<%=colors.toString()%>],
                    barColors: [<%=colors.toString()%>]
                });
    }

    function donutChart2()
    {
        window.donutChart2 = Morris.Donut(
                {
                    element: 'bar-example2',
                    <%=statusChartDonutChartJsonStr%>
                    resize: true,
                    responsive: true,
                    colors: [
                        '#68c39f',
                        '#900',
                        '#b93a71',
                        '#edce8c',
                        '#Abb7b7',
                        '#99e6ff',
                        '#4a525f',
                        '#54ce28',
                        '#C92C5C',
                        '#4A2CC9',
                        '#99e6ff',
                        '#b93a71'
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

    function myAllFilterFunctions(data)
    {
        var currency=document.getElementById("currency").value;
        var payBrand=document.getElementById("payBrand").value;
        var payMode=document.getElementById("payMode").value;
        var ctoken=document.getElementById("ctoken").value;
        var datefilter= document.getElementById("datefilter").value;
        var status= document.getElementById("status").value;

        if(data.length === 0)
        {
            document.myformname.action="/merchant/servlet/DashBoard?ctoken="+ctoken;
        }
        else
        {
            document.myformname.action="/merchant/servlet/DashBoard?ctoken="+ctoken;
        }
            document.getElementById("myformname").submit();
    }
</script>

<div class="content-page">
    <div class="content">
        <div class="col-sm-12 portlets ui-sortable">
            <div class="widget" style="margin-left: -12px; width: 123%">
                <form name="myformname" id="myformname" action="" method="POST">
                    <div class="form-group col-md-2">
                        <label>&nbsp;</label>
                        <select id="datefilter" name="datefilter" class="btn btn-default2 btn-xs dropdown-toggle form-control" onchange="myAllFilterFunctions(this)">
                            <%
                                for(String datefilterkey : datefilter.keySet()){

                                    String datestr = datefilter.get(datefilterkey);
                                    if(datestr.equalsIgnoreCase(dateModestr)){

                            %>
                            <option value="<%=datestr%>" SELECTED  ><%=datestr%></option>
                            <% }else{ %>
                            <option value="<%=datestr%>" ><%=datestr%></option>
                            <% }}
                            %>
                        </select>
                    </div>

                    <div class="form-group col-md-2">
                        <label>&nbsp;</label>
                        <select id="currency" name="currency" onchange="myAllFilterFunctions(this)" class="btn btn-default2 btn-xs dropdown-toggle form-control">
                            <option  value=""> <%=dashBoard_all_currency%> </option>
                            <%
                                for (String currency :listDb)
                                {
                                    String isSelected="";
                                    if(currency.equalsIgnoreCase(currency1)){
                                        isSelected="selected";
                                    }
                            %>
                            <option  value="<%=currency%>" <%=isSelected%>> <%=currency.toUpperCase()%> </option>
                            <%
                                }
                            %>
                        </select>
                    </div>

                    <div class="form-group col-md-2" >
                        <label>&nbsp;</label>
                        <select id="payMode" name="payMode" onchange="myAllFilterFunctions(this)" class="btn btn-default2 btn-xs dropdown-toggle form-control">
                            <option  value=""> <%=dashBoard_Payment_Mode%> </option>
                            <%
                                if (payModeList.size() > 0)
                                {
                                    Set statusSet = payModeList.keySet();
                                    Iterator iterator=statusSet.iterator();
                                    String selected = "";
                                    String key = "";
                                    String value = "";

                                    while (iterator.hasNext())
                                    {
                                        key = (String)iterator.next();
                                        value = (String) payModeList.get(key);
                                        if (key.equals(payMode1))
                                            selected = "selected";
                                        else
                                            selected = "";
                            %>
                            <option value="<%=key%>" <%=selected%>><%=value.toUpperCase()%></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                        <%--<%
                            }
                        %>--%>
                    </div>

                    <div class="form-group col-md-2">
                        <label>&nbsp;</label>
                        <select id="payBrand" name="payBrand" onchange="myAllFilterFunctions(this)" class="btn btn-default2 btn-xs dropdown-toggle form-control">
                            <option  value=""> <%=dashBoard_Payment_Brand%></option>
                            <%
                                if (payBrandList.size() > 0)
                                {
                                    Set statusSet = payBrandList.keySet();
                                    Iterator iterator=statusSet.iterator();
                                    String selectedBrand = "";
                                    String keyBrand = "";
                                    String valueBrand = "";

                                    while (iterator.hasNext())
                                    {

                                        keyBrand = (String)iterator.next();
                                        valueBrand = (String) payBrandList.get(keyBrand);

                                        if (keyBrand.equals(payBrand1))
                                            selectedBrand = "selected";
                                        else
                                            selectedBrand = "";
                            %>
                            <option value="<%=keyBrand%>" <%=selectedBrand%>><%=valueBrand.toUpperCase()%></option>
                            <%
                                    }
                                }
                            %>
                        </select>
                        <%--<%
                            }
                        %>--%>
                    </div>
                    <div class="form-group col-md-2">
                        <label>&nbsp;</label>
                        <select id="status" name="status" class="btn btn-default2 btn-xs dropdown-toggle form-control" onchange="myAllFilterFunctions(this)">
                            <%
                                for(String statuskey : statusList.keySet()){

                                    String statusStr = statusList.get(statuskey);
                                    if(statusStr.equalsIgnoreCase(statusModeStr)){
                            %>
                            <option value="<%=statusStr%>" SELECTED  ><%=statusStr%></option>
                            <% }else{ %>
                            <option value="<%=statusStr%>" ><%=statusStr%></option>
                            <% }}
                            %>
                           <%-- <option value="">Select Status</option>--%>
                            <%--<option value="sales">Sales</option>
                            <option value="payout">Payout</option>--%>
                          <%--  <%
                                if (statusList.size() > 0)
                                {
                                    Set statusSet = statusList.keySet();
                                    Iterator iterator=statusSet.iterator();
                                    String selectedStatus = "";
                                    String keyStatus = "";
                                    String valueStatus = "";

                                    while (iterator.hasNext())
                                    {

                                        keyStatus = (String)iterator.next();
                                        valueStatus = (String) statusList.get(keyStatus);

                                        if (keyStatus.equals(status1))
                                            selectedStatus = "selected";
                                        else
                                            selectedStatus = "";
                            %>
                            <option value="<%=keyStatus%>" <%=selectedStatus%>><%=valueStatus%></option>
                            <%
                                    }
                                }
                            %>--%>
                        </select>
                    </div>
                </form>
            </div>
        </div>
        <div class="row top-summary">
            <div class="col-lg-3 col-md-5">
                <div class="widget green-1 animated fadeInDown">
                    <div class="widget-content padding" style="padding: 5px !important;">
                        <div class="widget-icon">
                            <img src="/merchant/images/Sales.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata"><%=dashBoard_total%> <b><%=dashBoard_sales%></b></p>
                            <%
                                if (functions.isValueNull(currency1))
                                {
                                    if (functions.isValueNull(currency1.substring(0).toUpperCase()))
                                    {
                                        cur=currency1.substring(0).toUpperCase();
                                    }
                            %>
<%--
                            <h2><img src="/merchant/images/<%=currency1.substring(0).toUpperCase()%>.png">
--%>
                            <h2><img src="/merchant/images/<%=cur%>.png">
                                <%
                                    }
                                    else if (!listDb.isEmpty() && functions.isValueNull(listDb.get(0)))
                                    {
                                %>
                                <h2><img src="/merchant/images/<%=listDb.get(0)%>.png">
                                <%
                                    }
                                %>
                                    <span class="animate-number" data-value="<%=salesamount%>" data-duration="3000"></span><br>
                                    <span class="animate-number" style="font-size: 20px;">Count = </span>
                                    <span class="animate-number" data-value="<%=salescount%>" data-duration="3000"></span></h2>
                            <div class="clearfix"></div>
                        </div>
                    </div>
                    <div class="widget-footer">
                        <div class="row">
                            <div class="col-sm-12">
                            </div>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                </div>
            </div>

            <div class="col-lg-3 col-md-5">
                <div class="widget lightblue-1 animated fadeInDown">
                    <div class="widget-content padding" style="padding: 5px !important;">
                        <div class="widget-icon">
                            <img src="/merchant/images/Settled.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata"><%=dashBoard_TOTAL_SETTLED%>  <b><%=dashBoard_SETTLED%></b></p>
                            <%
                                String settledval= totalAmount.get("settled");
                                String settledAmount = "";
                                String settledCount ="";
                                if (functions.isValueNull(settledval))
                                {
                                    String[] AmountandCountSettled = settledval.split("_");
                                    settledAmount = AmountandCountSettled[0];
                                    settledCount = AmountandCountSettled[1];
                                }
                                else
                                {
                                    settledAmount="0.00";
                                    settledCount="0";
                                }
                                if (functions.isValueNull(currency1))
                                {
                                    if (functions.isValueNull(currency1.substring(0).toUpperCase()))
                                    {
                                        cur=currency1.substring(0).toUpperCase();
                                    }
                            %>
                            <h2><img src="/merchant/images/<%=cur%>.png">
                                <%
                                    }
                                    else if (!listDb.isEmpty() && functions.isValueNull(listDb.get(0)))
                                    {
                                %>
                                <h2><img src="/merchant/images/<%=listDb.get(0)%>.png">
                                <%
                                    }
                                %>
                                    <span class="animate-number" data-value="<%=settledAmount%>" data-duration="3000"></span><br>
                                <span class="animate-number" style="font-size: 20px;">Count = </span>
                                <span class="animate-number" data-value="<%=settledCount%>" data-duration="3000"></span></h2>
                            <div class="clearfix"></div>
                        </div>
                    </div>
                    <div class="widget-footer">
                        <div class="row">
                            <div class="col-sm-12">
                            </div>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                </div>
            </div>

           <%-- <div class="col-lg-3 col-md-5">
                <div class="widget lightblue-1 animated fadeInDown" style="width: 104%; background-color: #91d8d8;">
                    <div class="widget-content padding" style="padding: 5px !important;">
                        <div class="widget-icon">
                            <img src="/merchant/images/Settled.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata"><b><%=dashBoard_WITHDRAW_AMOUNT%> </b></p>
                            <%
                                if ( functions.isValueNull(listDb.get(0)) || functions.isValueNull(currency1))
                                {
                                    String cur= "";
                                    if (functions.isValueNull(currency1.substring(0).toUpperCase()))
                                    {
                                        cur=currency1.substring(0).toUpperCase();
                                    }
                                    else
                                    {
                                        cur= listDb.get(0);
                                    }
                                    System.out.println("currency value from jsp+++ "+listDb.get(0));
                            %>
                            <h2><img src="/merchant/images/<%=cur%>.png">
                                <%
                                    }
                                %>
                                <span class="animate-number" data-value="<%=withdrawamt%>" data-duration="3000"></span><br>
                                <span class="animate-number" style="font-size: 20px;">Count = </span>
                                <span class="animate-number" data-value="<%=withdrawcount%>" data-duration="3000"> </span></h2>
                            <div class="clearfix"></div>
                        </div>
                    </div>
                    <div class="widget-footer">
                        <div class="row">
                            <div class="col-sm-12">
                            </div>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                </div>
            </div>--%>

            <div class="col-lg-3 col-md-5">
                <div class="widget darkblue-2 animated fadeInDown">
                    <div class="widget-content padding" style="padding: 5px !important;">
                        <div class="widget-icon">
                            <img src="/merchant/images/refund_resized1.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata"><%=dashBoard_total_refund%> <b><%=dashBoard_refund%></b></p>
                            <%
                                String refundvalue= totalAmount.get("reversed");
                                String refundAmount = "";
                                String refundCount ="";
                                if (functions.isValueNull(refundvalue))
                                {
                                     String[] AmountandCountRefund = refundvalue.split("_");
                                     refundAmount = AmountandCountRefund[0];
                                     refundCount = AmountandCountRefund[1];
                                }
                                else
                                {
                                    refundAmount="0.00";
                                    refundCount="0";
                                }
                                if (functions.isValueNull(currency1))
                                {
                                    if (functions.isValueNull(currency1.substring(0).toUpperCase()))
                                    {
                                        cur=currency1.substring(0).toUpperCase();
                                    }
                            %>
                            <h2><img src="/merchant/images/<%=cur%>.png">
                                <%
                                    }
                                    else if (!listDb.isEmpty() && functions.isValueNull(listDb.get(0)))
                                    {
                                %>
                                <h2><img src="/merchant/images/<%=listDb.get(0)%>.png">
                                <%
                                    }
                                %>
                                    <span class="animate-number" data-value=<%=refundAmount%> data-duration="3000"></span><br>
                                <span class="animate-number" style="font-size: 20px;">Count = </span>
                                <span class="animate-number" data-value=<%=refundCount%> data-duration="3000"></span></h2>
                            <div class="clearfix"></div>
                        </div>
                    </div>
                    <div class="widget-footer">
                        <div class="row">
                            <div class="col-sm-12">
                            </div>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                </div>
            </div>

            <div class="col-lg-3 col-md-5">
                <div class="widget orange-4 animated fadeInDown">
                    <div class="widget-content padding" style="padding: 5px !important;">
                        <div class="widget-icon">
                            <img src="/merchant/images/Chargeback1.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata"><%=dashBoard_total_chargeback%> <b><%=dashBoard_chargeback%></b></p>
                            <%
                                String chargeback= totalAmount.get("chargeback");
                                String chargebackAmount = "";
                                String chargebackCount ="";
                                if (functions.isValueNull(chargeback))
                                {
                                    String[] AmountandCountCharge = chargeback.split("_");
                                    chargebackAmount = AmountandCountCharge[0];
                                    chargebackCount = AmountandCountCharge[1];
                                }
                                else
                                {
                                    chargebackAmount="0.00";
                                    chargebackCount="0";
                                }
                                if ( functions.isValueNull(currency1))
                                {
                                    if (functions.isValueNull(currency1.substring(0).toUpperCase()))
                                    {
                                        cur=currency1.substring(0).toUpperCase();
                                    }
                            %>
                            <h2><img src="/merchant/images/<%=cur%>.png">
                                <%
                                    }
                                    else if (!listDb.isEmpty() && functions.isValueNull(listDb.get(0)))
                                    {
                                %>
                                <h2><img src="/merchant/images/<%=listDb.get(0)%>.png">
                                <%
                                    }
                                %>
                                    <span class="animate-number"  style="color: white;" data-value="<%=chargebackAmount%>" data-duration="3000"></span><br>
                                <span class="animate-number"  style="color: white; font-size: 20px;">Count = </span>
                                <span class="animate-number"  style="color: white;" data-value="<%=chargebackCount%>" data-duration="3000"></span></h2>
                            <div class="clearfix"></div>
                        </div>
                    </div>
                    <div class="widget-footer">
                        <div class="row">
                            <div class="col-sm-12">
                            </div>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                </div>
            </div>

            <div class="col-lg-3 col-md-5">
                <div class="widget orange-4 animated fadeInDown" style="background-color:#1e8b92 !important ">
                    <div class="widget-content padding" style="padding: 5px !important;">
                        <div class="widget-icon">
                            <img src="/merchant/images/declined.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata"><%=dashBoard_total_declined%> <b><%=dashBoard_declined%> </b></p>
                            <%
                                String declined= totalAmount.get("declined");
                                String declinedAmount = "";
                                String declinedCount ="";
                                if (functions.isValueNull(declined))
                                {
                                    String[] AmountandCountDeclined = declined.split("_");
                                    declinedAmount = AmountandCountDeclined[0];
                                    declinedCount = AmountandCountDeclined[1];
                                }
                                else
                                {
                                    declinedAmount="0.00";
                                    declinedCount="0";
                                }
                                if (functions.isValueNull(currency1))
                                {
                                    if (functions.isValueNull(currency1.substring(0).toUpperCase()))
                                    {
                                        cur=currency1.substring(0).toUpperCase();
                                    }
                            %>
                            <h2><img src="/merchant/images/<%=cur%>.png">
                                <%
                                    }
                                    else if (!listDb.isEmpty() && functions.isValueNull(listDb.get(0)))
                                    {
                                %>
                                <h2><img src="/merchant/images/<%=listDb.get(0)%>.png">
                                <%
                                    }
                                %>
                                    <span class="animate-number" style="color: white;" data-value="<%=declinedAmount%>" data-duration="3000"></span><br>
                                <span class="animate-number" style="color: white; font-size: 20px;">Count = </span>
                                <span class="animate-number" style="color: white;" data-value="<%=declinedCount%>" data-duration="3000"></span></h2>
                            <div class="clearfix"></div>
                        </div>
                    </div>
                    <div class="widget-footer">
                        <div class="row">
                            <div class="col-sm-12">
                            </div>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                </div>
            </div>

            <div class="col-lg-3 col-md-5">
                <div class="widget orange-4 animated fadeInDown">
                    <div class="widget-content padding" style="padding: 5px !important;">
                        <div class="widget-icon">
                            <img src="/merchant/images/declined.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata">TOTAL <b>CAPTURE</b></p>
                            <%
                                String capturesuccess= totalAmount.get("capturesuccess");
                                String captureAmount = "";
                                String captureCount ="";
                                if (functions.isValueNull(capturesuccess))
                                {
                                    String[] AmountandCountCap = capturesuccess.split("_");
                                     captureAmount = AmountandCountCap[0];
                                     captureCount = AmountandCountCap[1];
                                }
                                else
                                {
                                    captureAmount="0.00";
                                    captureCount="0";
                                }
                                if (functions.isValueNull(currency1))
                                {
                                    if (functions.isValueNull(currency1.substring(0).toUpperCase()))
                                    {
                                        cur=currency1.substring(0).toUpperCase();
                                    }
                            %>
                            <h2><img src="/merchant/images/<%=cur%>.png">
                                <%
                                    }
                                    else if (!listDb.isEmpty() && functions.isValueNull(listDb.get(0)))
                                    {
                                %>
                                <h2><img src="/merchant/images/<%=listDb.get(0)%>.png">
                                <%
                                    }
                                %>
                                    <span class="animate-number"  style="color: white;" data-value="<%=captureAmount%>" data-duration="3000"></span><br>
                                <span class="animate-number"  style="color: white; font-size: 20px;">Count = </span>
                                <span class="animate-number"  style="color: white;" data-value="<%=captureCount%>" data-duration="3000"></span></h2>
                            <div class="clearfix"></div>
                        </div>
                    </div>
                    <div class="widget-footer">
                        <div class="row">
                            <div class="col-sm-12">
                            </div>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                </div>
            </div>

            <div class="col-lg-3 col-md-5">
                <div class="widget royalblue-4 animated fadeInDown" style="background-color:royalblue ">
                    <div class="widget-content padding" style="padding: 5px !important;">
                        <div class="widget-icon">
                            <img src="/merchant/images/declined.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata">TOTAL <b>PAYOUT SUCCESS </b></p>
                            <%
                                String payoutsuccess= totalAmount.get("payoutsuccessful");
                                String payoutSuccessAmount = "";
                                String payoutSuccessCount ="";
                                if (functions.isValueNull(payoutsuccess))
                                {
                                    String[] AmountandCountPayoutSuc = payoutsuccess.split("_");
                                     payoutSuccessAmount = AmountandCountPayoutSuc[0];
                                     payoutSuccessCount = AmountandCountPayoutSuc[1];
                                }
                                else
                                {
                                    payoutSuccessAmount="0.00";
                                    payoutSuccessCount= "0";
                                }
                                if (functions.isValueNull(currency1))
                                {
                                    if (functions.isValueNull(currency1.substring(0).toUpperCase()))
                                    {
                                        cur=currency1.substring(0).toUpperCase();
                                    }
                            %>
                            <h2><img src="/merchant/images/<%=cur%>.png">
                                <%
                                    }
                                    else if (!listDb.isEmpty() && functions.isValueNull(listDb.get(0)))
                                    {
                                %>
                                <h2><img src="/merchant/images/<%=listDb.get(0)%>.png">
                                <%
                                    }
                                %>
                                    <span class="animate-number" style="color: white;" data-value="<%=payoutSuccessAmount%>" data-duration="3000"></span><br>
                                <span class="animate-number" style="color: white; font-size: 20px;">Count = </span>
                                <span class="animate-number" style="color: white;" data-value="<%=payoutSuccessCount%>" data-duration="3000"></span></h2>
                            <div class="clearfix"></div>
                        </div>
                    </div>
                    <div class="widget-footer">
                        <div class="row">
                            <div class="col-sm-12">
                            </div>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                </div>
            </div>

            <div class="col-lg-3 col-md-5">
                <div class="widget orange-4 animated fadeInDown" style="background-color:#1e8b92 !important ">
                    <div class="widget-content padding" style="padding: 5px !important;">
                        <div class="widget-icon">
                            <img src="/merchant/images/declined.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata">TOTAL <b>PAYOUT FAILED </b></p>
                            <%
                                String payoutfailed= totalAmount.get("payoutfailed");
                                String payoutfailedAmount = "";
                                String payoutfailedCount ="";
                                if (functions.isValueNull(payoutfailed))
                                {
                                    String[] AmountandCountPayoutfailed = payoutfailed.split("_");
                                    payoutfailedAmount = AmountandCountPayoutfailed[0];
                                    payoutfailedCount = AmountandCountPayoutfailed[1];
                                }
                                else
                                {
                                    payoutfailedAmount="0.00";
                                    payoutfailedCount= "0";
                                }
                                if (functions.isValueNull(currency1))
                                {
                                    if (functions.isValueNull(currency1.substring(0).toUpperCase()))
                                    {
                                        cur=currency1.substring(0).toUpperCase();
                                    }
                            %>
                            <h2><img src="/merchant/images/<%=cur%>.png">
                                <%
                                    }
                                    else if (!listDb.isEmpty() && functions.isValueNull(listDb.get(0)))
                                    {
                                %>
                                <h2><img src="/merchant/images/<%=listDb.get(0)%>.png">
                                <%
                                    }
                                %>
                                    <span class="animate-number" style="color: white;" data-value="<%=payoutfailedAmount%>" data-duration="3000"></span><br>
                                <span class="animate-number" style="color: white; font-size: 20px;">Count = </span>
                                <span class="animate-number" style="color: white;" data-value="<%=payoutfailedCount%>" data-duration="3000"></span></h2>
                            <div class="clearfix"></div>
                        </div>
                    </div>
                    <div class="widget-footer">
                        <div class="row">
                            <div class="col-sm-12">
                            </div>
                        </div>
                        <div class="clearfix"></div>
                    </div>
                </div>
            </div>
        </div>


        <div class="row">
            <div class="col-lg-12 portlets ui-sortable">
                <div class="widget">
                    <input type="hidden" name="ctoken" id="ctoken" value="<%=ctoken%>">
                    <div class="widget-header transparent">
                        <%
                            if (functions.isValueNull(status) && "payout".equalsIgnoreCase(status))
                            {
                        %>
                        <h2><strong>Payout&nbsp;</strong>Chart</h2>
                        <%
                            }
                            else
                            {
                        %>
                        <h2><strong><%=dashBoard_Sales%>&nbsp;</strong><%=dashBoard_chart%></h2>
                        <%
                            }
                        %>

                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>

                    <div class="widget-content padding">
                        <div id="bar-example3">
                        </div>
                    </div>
                    </form>
                </div>
            </div>

            <div class="col-lg-4 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <%
                            if (functions.isValueNull(status) && "payout".equalsIgnoreCase(status))
                            {
                        %>
                        <h2><strong>Payout per currency</strong> <%=dashBoard_chart1%></h2>
                        <%
                            }
                            else
                            {
                        %>
                        <h2><strong><%=dashBoard_sales_per_currency%></strong> <%=dashBoard_chart1%></h2>
                        <%
                            }
                        %>
                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding">
                        <div id="bar-example1" style="font-size: 10px">
                        </div>
                    </div>
                </div>
            </div>


            <div class="col-md-4 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong><%=dashBoard_Status%></strong> <%=dashBoard_Chart%></h2>
                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding">
                        <div id="bar-example2" style="font-size: 10px" ></div>
                    </div>
                </div>
            </div>
            <div class="col-md-4 portlets ui-sortable">
                <div class="widget darkblue-3">
                    <div class="widget-header transparent">
                        <h2><i class="icon-chart-pie-1"></i> <strong><%=dashBoard_Progress%></strong> <%=dashBoard_Status1%></h2>
                    </div>
                    <div class="widget-content">
                        <div id="website-statistic2" class="statistic-chart">
                            <div class="col-sm-12 stacked">
                                <div class="col-sm-12 status-data">

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=dashBoard_AuthStarted%></span><br>
                                    <div id="bar1" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip"><%=dashBoard_AuthStarted%></span>
                                        </div>
                                        <span class="fill" data-percentage=<%=countAuthStarted%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=dashBoard_AuthSuccessful%></span><br>
                                    <div id="bar2" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip"><%=dashBoard_AuthSuccessful%></span>
                                        </div>
                                        <span class="fill" data-percentage=<%=countAuthSuccessful%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=dashBoard_CaptureSuccess%></span><br>
                                    <div id="bar3" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip"><%=dashBoard_CaptureSuccess%></span>
                                        </div>
                                        <span class="fill" data-percentage=<%=countCaptureSuccess%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=dashBoard_Chargeback%></span><br>
                                    <div id="bar4" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip"><%=dashBoard_Chargeback%></span>
                                        </div>
                                        <span class="fill" data-percentage=<%=countChargeBack%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=dashBoard_MarkedForReversal1%></span><br>
                                    <div id="bar5" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip"><%=dashBoard_MarkedForReversal1%></span>
                                        </div>
                                        <span class="fill" data-percentage=<%=countMarkedForReversal%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=dashBoard_Reversed%></span><br>
                                    <div id="bar6" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip"><%=dashBoard_Reversed%></span>
                                        </div>
                                        <span class="fill" data-percentage=<%=countReversed%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=dashBoard_Settled%></span><br>
                                    <div id="bar7" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip"><%=dashBoard_Settled%></span>
                                        </div>
                                        <span class="fill" data-percentage=<%=countSettled%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=dashBoard_Failed%></span><br>
                                    <div id="bar8" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip"><%=dashBoard_Failed%></span>
                                        </div>
                                        <span class="fill" data-percentage=<%=countFailed%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=dashBoard_AuthFailed%></span><br>
                                    <div id="bar9" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip"><%=dashBoard_AuthFailed%></span>
                                        </div>
                                        <span class="fill" data-percentage=<%=countAuthFailed%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=dashBoard_PartialRefund%></span><br>
                                    <div id="bar10" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip"><%=dashBoard_PartialRefund%></span>
                                        </div>
                                        <span class="fill" data-percentage=<%=countPartialRefund%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold">PayoutSuccessful</span><br>
                                    <div id="bar11" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip">PayoutSuccessful</span>
                                        </div>
                                        <span class="fill" data-percentage=<%=countPayoutSuccessful%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold">PayoutFailed</span><br>
                                    <div id="bar12" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip">PayoutFailed</span>
                                        </div>
                                        <span class="fill" data-percentage=<%=countPayoutFailed%>></span>
                                    </div>

                                    <span class="pull-right" style="color: #ffffff;font-weight: bold">PayoutStarted</span><br>
                                    <div id="bar13" class="barfiller">
                                        <div class="tipWrap">
                                            <span class="tip">PayoutStarted</span>
                                        </div>
                                        <span class="fill" data-percentage=<%=countPayoutStarted%>></span>
                                    </div>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
    </div>
    </div>
</div>
</div>

<%
    }
%>
</body>
</html>

