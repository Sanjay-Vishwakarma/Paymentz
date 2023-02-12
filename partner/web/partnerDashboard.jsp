<%--
  Created by IntelliJ IDEA.
  User: Rajeev Singh
  Date: 28/4/18
  Time: 12:35 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page errorPage=""
         import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="java.util.*" %>
<%
    Functions functions = new Functions();
%>

<%
    //String totalSalesAmount = (String)request.getAttribute("totalSalesAmount");
    String[] hashtablesale = (String[])request.getAttribute("hashtablesale");
    String salesamount="";
    String salescount="";
    if (hashtablesale!= null)
    {
        salesamount= hashtablesale[0];
        salescount= hashtablesale[1];
    }
    String totalSettledAmount = (String) request.getAttribute("totalSettledAmount");
    String totalRefundAmount = (String) request.getAttribute("totalRefundAmount");
    String totalChargebackAmount = (String) request.getAttribute("totalChargebackAmount");
    //String totalDeclinedAmount = (String) request.getAttribute("totalDeclinedAmount");

    /*String[] hashtabledeclined= (String[]) request.getAttribute("hashtabledeclined");
    String declinedamount="";
    String declinedcount="";
    if(hashtabledeclined!= null)
    {
        declinedamount= hashtabledeclined[0];
        declinedcount= hashtabledeclined[1];
    }*/

    HashMap<String,String> totalAmount= (HashMap<String, String>) request.getAttribute("totalAmount");
    HashMap<String,String> datefilter= new LinkedHashMap<>();
    datefilter.put("last_seven_days_key","Last seven days");
    datefilter.put("today_key","Today");
    datefilter.put("current_month_key","Current month");
    datefilter.put("last_month_key","Last month");
    datefilter.put("last_six_months_key","Last six months");

    String partner_datefilter= "";
    if (request.getParameter("datefilter")!= null)
    {
        partner_datefilter= request.getParameter("datefilter");
    }
    else
    {
        partner_datefilter= "";
    }
    String status= (String) request.getAttribute("status");

    String salesPerCurrencyDonutChartJsonStr = (String) request.getAttribute("salesPerCurrencyDonutChartJsonStr");
    String statusChartDonutChartJsonStr = (String) request.getAttribute("statusChartDonutChartJsonStr");

    String salesBarChartJsonStr =(String)request.getAttribute("salesBarChartJsonStr");
    String company =(String)session.getAttribute("partnername");

    String salesBarChartLabelJsonStr =(String)request.getAttribute("salesBarChartLabelJsonStr");
    int anglelabel=0;
    String datelabel="";
    if((String) request.getAttribute("datelabel")!= null)
    {
        datelabel= (String) request.getAttribute("datelabel");
    }
    if(datelabel.equalsIgnoreCase("Current month") || datelabel.equalsIgnoreCase("Last month"))
    {
        anglelabel=60;
    }

    String currency1 = (Functions.checkStringNull(request.getParameter("currency")) == null) ? "" : request.getParameter("currency");
    String payMode1 = Functions.checkStringNull(request.getParameter("payMode"))==null?"":request.getParameter("payMode");
    String payBrand1 = Functions.checkStringNull(request.getParameter("payBrand"))==null?"":request.getParameter("payBrand");
     String partnerid1 = Functions.checkStringNull(request.getParameter("partnerid"))==null?"":request.getParameter("partnerid");

    List<String> list =(List)request.getAttribute("list");

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

    String cur= "";

%>
<%@ include file="top.jsp" %>

<%
    String pid ="";
    //String partnerid =nullToStr(request.getParameter("partnerid"));
    java.util.TreeMap<String,String> partneriddetails =null;
    partneriddetails=partner.getPartnerDetailsForUI(String.valueOf(session.getAttribute("merchantid")));
    java.util.TreeMap<String,String> partneriddetails1 =null;
    partneriddetails1= partner.getPartnerDetailsForPartnerUI(String.valueOf(session.getAttribute("merchantid")));
    String Config =null;
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    if(Roles.contains("superpartner")){
        pid=Functions.checkStringNull(request.getParameter("pid"))==null?"":request.getParameter("pid");
    }else{
        pid = String.valueOf(session.getAttribute("merchantid"));
        Config = "disabled";
    }



    session.setAttribute("submit", "Partner Dashboard");
    session.getAttribute("colorPallet");
    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    StringBuffer colors= new StringBuffer();
    colors= (StringBuffer)request.getAttribute("colorFromCurrencyProperties");


    String partnerDashboard_TOTAL = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_TOTAL")) ? rb1.getString("partnerDashboard_TOTAL") : "TOTAL";
    String partnerDashboard_SALES = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_SALES")) ? rb1.getString("partnerDashboard_SALES") : "SALES";
    String partnerDashboard_TOTAL1 = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_TOTAL1")) ? rb1.getString("partnerDashboard_TOTAL1") : "TOTAL";
    String partnerDashboard_SETTLED = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_SETTLED")) ? rb1.getString("partnerDashboard_SETTLED") : "SETTLED";
    String partnerDashboard_TOTAL2 = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_TOTAL2")) ? rb1.getString("partnerDashboard_TOTAL2") : "TOTAL";
    String partnerDashboard_REFUND = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_REFUND")) ? rb1.getString("partnerDashboard_REFUND") : "REFUND";
    String partnerDashboard_TOTAL3 = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_TOTAL3")) ? rb1.getString("partnerDashboard_TOTAL3") : "TOTAL";
    String partnerDashboard_CHARGEBACK = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_CHARGEBACK")) ? rb1.getString("partnerDashboard_CHARGEBACK") : "CHARGEBACK";
    String partnerDashboard_TOTAL4 = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_TOTAL4")) ? rb1.getString("partnerDashboard_TOTAL4") : "TOTAL";
    String partnerDashboard_DECLINED = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_DECLINED")) ? rb1.getString("partnerDashboard_DECLINED") : "DECLINED";
    String partnerDashboard_All_Currency = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_All_Currency")) ? rb1.getString("partnerDashboard_All_Currency") : "All Currency";
    String partnerDashboard_Payment_Mode = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_Payment_Mode")) ? rb1.getString("partnerDashboard_Payment_Mode") : "Payment Mode";
    String partnerDashboard_Payment_Brand = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_Payment_Brand")) ? rb1.getString("partnerDashboard_Payment_Brand") : "Payment Brand";
    String partnerDashboard_Sales = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_Sales")) ? rb1.getString("partnerDashboard_Sales") : "Sales";
    String partnerDashboard_Chart = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_Chart")) ? rb1.getString("partnerDashboard_Chart") : "Chart";
    String partnerDashboard_Sales_Per_Currency = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_Sales_Per_Currency")) ? rb1.getString("partnerDashboard_Sales_Per_Currency") : "Sales Per Currency";
    String partnerDashboard_Chart1 = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_Chart1")) ? rb1.getString("partnerDashboard_Chart1") : "Chart";
    String partnerDashboard_Status = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_Status")) ? rb1.getString("partnerDashboard_Status") : "Status";
    String partnerDashboard_Chart2 = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_Chart2")) ? rb1.getString("partnerDashboard_Chart2") : "Chart";
    String partnerDashboard_Progress = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_Progress")) ? rb1.getString("partnerDashboard_Progress") : "Progress";
    String partnerDashboard_Status1 = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_Status1")) ? rb1.getString("partnerDashboard_Status1") : "Status";
    String partnerDashboard_AuthStarted = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_AuthStarted")) ? rb1.getString("partnerDashboard_AuthStarted") : "AuthStarted";
    String partnerDashboard_AuthSuccessful = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_AuthSuccessful")) ? rb1.getString("partnerDashboard_AuthSuccessful") : "AuthSuccessful";
    String partnerDashboard_CaptureSuccess = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_CaptureSuccess")) ? rb1.getString("partnerDashboard_CaptureSuccess") : "CaptureSuccess";
    String partnerDashboard_Chargeback1 = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_Chargeback1")) ? rb1.getString("partnerDashboard_Chargeback1") : "Chargeback";
    String partnerDashboard_MarkedForReversal = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_MarkedForReversal")) ? rb1.getString("partnerDashboard_MarkedForReversal") : "MarkedForReversal";
    String partnerDashboard_Reversed = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_Reversed")) ? rb1.getString("partnerDashboard_Reversed") : "Reversed";
    String partnerDashboard_Settled = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_Settled")) ? rb1.getString("partnerDashboard_Settled") : "Settled";
    String partnerDashboard_Failed = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_Failed")) ? rb1.getString("partnerDashboard_Failed") : "Failed";
    String partnerDashboard_AuthFailed = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_AuthFailed")) ? rb1.getString("partnerDashboard_AuthFailed") : "AuthFailed";
    String partnerDashboard_PartialRefund = StringUtils.isNotEmpty(rb1.getString("partnerDashboard_PartialRefund")) ? rb1.getString("partnerDashboard_PartialRefund") : "PartialRefund";


%>
<html lang="en">
<head>

    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>

    <title><%=company%> Partner Dashboard</title>

    <style type="text/css">
        svg:not(:root) {
            overflow: inherit!important;
        }

        .lightblue-1 .royalblue-4{
            width: 104%;
        }
    </style>

</head>


<!-- Base Css Files -->
<script src="/partner/NewCss/libs/jquery-animate-numbers/jquery.animateNumbers.js"></script>
<link href="/partner/NewCss/libs/ios7-switch/ios7-switch.css" rel="stylesheet" />
<link href="/partner/NewCss/libs/pace/pace.css" rel="stylesheet" />
<link href="/partner/NewCss/css/style.css" rel="stylesheet" type="text/css" />
<link href="/partner/NewCss/css/style-responsive.css" rel="stylesheet" />
<script src="/partner/NewCss/libs/morrischart/morris.min.js"></script>
<script src="/partner/NewCss/morrisJS/raphael-min.js"></script>
<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>


<body>


<style type="text/css">
    .morris-hover {
        position:absolute;
        z-index:1000;
    }

    .morris-hover.morris-default-style {     border-radius:10px;
        padding:6px;
        color:#666;
        background:rgba(255, 255, 255, 0.8);
        border:solid 2px rgba(230, 230, 230, 0.8);
        font-family:sans-serif;
        font-size:12px;
        text-align:center;
        z-index: inherit;
    }

    .morris-hover.morris-default-style .morris-hover-row-label {
        font-weight:bold;
        margin:0.25em 0;
    }

    .morris-hover.morris-default-style .morris-hover-point {
        white-space:nowrap;
        margin:0.1em 0;
    }

    svg { width: 100%; }

    #currencyid{
        position: absolute;
        right: 0px;
        padding: 5px
    }

    @media(max-width: 620px){
        #currencyid{
            position: initial;
        }
    }
    @media (min-width: 1200px) {
        .col-lg-3 {
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

<%--<%
    String fromdate = null;
    String todate = null;

    Date date = new Date();
    SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

    String Date = originalFormat.format(date);
    date.setDate(1);

    String fromDate = originalFormat.format(date);

    fromdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDate : request.getParameter("fromdate");
    todate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");
%>--%>


<script>

    <%
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
                payoutsuccessful= Integer.parseInt(getProgressBarFromGetValidStatusChartForPartner.get("payoutsuccessful"));
            }
            if (functions.isValueNull((getProgressBarFromGetValidStatusChartForPartner.get("payoutfailed"))))
            {
                payoutFailed= Integer.parseInt(getProgressBarFromGetValidStatusChartForPartner.get("payoutfailed"));
            }
            if (functions.isValueNull((getProgressBarFromGetValidStatusChartForPartner.get("payoutstarted"))))
            {
               payoutStarted= Integer.parseInt(getProgressBarFromGetValidStatusChartForPartner.get("payoutstarted"));
            }
            int totalCount = (authStarted + authSuccessful + captureSuccess + chargeBack + markedForReversal + reversed + settled + failed + authFailed + partialRefund + payoutsuccessful + payoutFailed+ payoutStarted);

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
            String countPayoutSuccess= "";
            String countPayoutFailed= "";
            String countPayoutStarted= "";

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
            if (functions.isValueNull(String.valueOf((double)payoutsuccessful)) && payoutsuccessful!= 0 && functions.isValueNull(String.valueOf((double) totalCount)) && totalCount!= 0)
            {
                double payoutsuccess1= ((double) payoutsuccessful / (double) totalCount) * 100;
                countPayoutSuccess= functions.convert2Decimal(String.valueOf(payoutsuccess1));
            }
            if (functions.isValueNull(String.valueOf((double)payoutFailed)) && payoutFailed!= 0 && functions.isValueNull(String.valueOf((double) totalCount)) && totalCount!= 0)
            {
                double payoutFailed1= ((double) payoutFailed / (double) totalCount) * 100;
                countPayoutFailed= functions.convert2Decimal(String.valueOf(payoutFailed1));
            }
            if (functions.isValueNull(String.valueOf((double)payoutStarted)) && payoutStarted!=0 && functions.isValueNull(String.valueOf((double)totalCount)) && totalCount!=0)
            {
                double payoutStarted1= ((double) payoutStarted / (double) totalCount) * 100;
                countPayoutStarted= functions.convert2Decimal(String.valueOf(payoutStarted1));
            }

    %>

    window.onload = function()
    {
        donutChart1();
        donutChart2();
        barChart1();
    };
</script>

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
                        '#4A2CC9',
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
    function barChart1()
    {
        window.barChart1 = Morris.Bar(
                {
                    element: 'bar-example3',
                    <%=salesBarChartJsonStr%>
                    xkey: 'x',
                    gridTextSize: 15,
                    xLabelAngle:<%=anglelabel%> ,
                    ykeys:  [<%=salesBarChartLabelJsonStr%>],
                    labels: [<%=salesBarChartLabelJsonStr%>],
                    stacked: true,
                    resize: true,
                    responsive: true,
                    colors: [<%=colors.toString()%>],
                    barColors: [<%=colors.toString()%>]
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
            document.myformname.action="/partner/net/PartnerDashboard?&ctoken="+ctoken;
        }
        else
        {
            document.myformname.action="/partner/net/PartnerDashboard?currency="+currency+"&ctoken="+ctoken+"&payBrand="+payBrand+"&payMode="+payMode;
        }
        document.getElementById("myformname").submit();
    }

</script>
<div class="content-page">
    <div class="content">
        <div class="col-sm-10 portlets ui-sortable">
            <div class="widget" style="margin-left: -12px; width: 123%">
                <form name="myformname" id="myformname" action="" method="POST">
                    <div class="form-group col-md-2">
                        <label>&nbsp;</label>
                        <select id="datefilter" name="datefilter" onchange="myAllFilterFunctions(this)" class="btn btn-default2 btn-xs dropdown-toggle form-control">
                            <%
                                for (String datefilterkeyset: datefilter.keySet())
                                {
                                    String datestr= datefilter.get(datefilterkeyset);
                                    if (datestr.equalsIgnoreCase(partner_datefilter))
                                    {
                            %>
                            <option value="<%=datestr%>" SELECTED><%=datestr%></option>
                            <%
                            }else{
                            %>
                            <option value="<%=datestr%>"><%=datestr%></option>
                            <%
                                    }}
                            %>
                        </select>
                    </div>
                    <div class="form-group col-md-2">
                        <label>&nbsp;</label>
                        <select id="pid" name="pid" onchange="myAllFilterFunctions(this)" class="btn btn-default2 btn-xs dropdown-toggle form-control"<%=Config%>>
                            <%--<option  value=""> Partner id </option>--%>
                            &lt;&ndash;
                            <%
                                String Selected = "";
                                if (Roles.contains("superpartner"))
                                {
                                    for(String pid1 : partneriddetails.keySet())
                                    {
                                        if(pid1.toString().equals(pid))
                                        {
                                            Selected="selected";
                                        }
                                        else
                                        {
                                            Selected="";
                                        }


                            %>
                            <option value="<%=pid1%>" <%=Selected%>><%=partneriddetails.get(pid1)%></option>
                            <%
                                }
                            }
                            else
                            {
                                for(String pid2 : partneriddetails1.keySet())
                                {
                                    if(pid2.toString().equals(pid))
                                    {
                                        Selected="selected";
                                    }
                                    else
                                    {
                                        Selected="";
                                    }

                            %>
                            <option value="<%=pid2%>" <%=Selected%>><%=partneriddetails1.get(pid2)%></option>
                            <%
                                    }
                                }
                            %>

                        </select>
                    </div>

                    <%--<div class="form-group col-md-2">--%>
                    <%--<label>&nbsp;</label>--%>
                    <%--<select id="currency" name="currency" onchange="myAllFilterFunctions(this)" class="btn btn-default2 btn-xs dropdown-toggle form-control">--%>
                    <%--&lt;%&ndash;<option  value=""> All Currency </option>&ndash;%&gt;--%>
                    <%--&lt;&ndash;--%>
                    <%--<%--%>
                    <%--for (String currency :list)--%>
                    <%--{--%>
                    <%--String isSelected="";--%>
                    <%--if(currency.equalsIgnoreCase(currency1)){--%>
                    <%--isSelected="selected";--%>
                    <%--}--%>

                    <%--/* if (functions.isValueNull(currency)){*/--%>

                    <%--%>--%>
                    <%--<option  value="<%=currency%>" <%=isSelected%>> <%=currency.toUpperCase()%> </option>--%>
                    <%--<%--%>
                    <%--//  }--%>

                    <%--}--%>
                    <%--%>--%>
                    <%--</select>--%>
                    <%--</div>--%>


                    <div class="form-group col-md-2">
                        <label>&nbsp;</label>
                        <select id="currency" name="currency" onchange="myAllFilterFunctions(this)" class="btn btn-default2 btn-xs dropdown-toggle form-control">
                            <%-- <option  value=""> All Currency </option>--%>
                            &lt;&ndash;
                            <%
                                for (String currency :list)
                                {
                                    String isSelected="";
                                    if(currency.equalsIgnoreCase(currency1)){
                                        isSelected="selected";
                                    }

                                    if (functions.isValueNull(currency)){

                            %>
                            <option  value="<%=currency%>" <%=isSelected%>> <%=currency.toUpperCase()%> </option>
                            <%
                                    }

                                }
                            %>
                        </select>
                    </div>


                    <div class="form-group col-md-2" >
                        <label>&nbsp;</label>
                        <select id="payMode" name="payMode" onchange="myAllFilterFunctions(this)" class="btn btn-default2 btn-xs dropdown-toggle form-control">
                            <option  value=""> <%=partnerDashboard_Payment_Mode%> </option>
                            &lt;%&ndash;
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
                        <%-- <%
                             }
                         %>--%>
                    </div>

                    <div class="form-group col-md-2">
                        <label>&nbsp;</label>
                        <select id="payBrand" name="payBrand" onchange="myAllFilterFunctions(this)" class="btn btn-default2 btn-xs dropdown-toggle form-control">
                            <option  value=""> <%=partnerDashboard_Payment_Brand%> </option>
                            &lt;&ndash;
                            <%
                                if (payBrandList.size() > 0)
                                {
                                    Set statusSet = payBrandList.keySet();
                                    Iterator iterator=statusSet.iterator();
                                    String selected = "";
                                    String key = "";
                                    String value = "";

                                    while (iterator.hasNext())
                                    {
                                        key = (String)iterator.next();
                                        value = (String) payBrandList.get(key);

                                        if (key.equals(payBrand1))
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
                <div class="widget lightblue-1 animated fadeInDown">
                    <div class="widget-content padding">
                        <div class="widget-icon">
                            <img src="/merchant/images/Sales.png">
                        </div>

                        <div class="text-box">
                            <p class="maindata"><%=partnerDashboard_TOTAL%> <b><%=partnerDashboard_SALES%></b></p>
                            <%
                                if (functions.isValueNull(currency1))
                                {
                                    if (functions.isValueNull(currency1.substring(0).toUpperCase()))
                                    {
                                        cur=currency1.substring(0).toUpperCase();
                                    }
                            %>

                            <h2><img src="/partner/images/<%=cur%>.png">
                                <%
                                    }
                                    else if (!list.isEmpty() && functions.isValueNull(list.get(0)))
                                    {
                                %>
                                <h2><img src="/partner/images/<%=list.get(0)%>.png">
                                <%
                                    }
                                %>
                                    <span class="animate-number" style="color: white;" data-value=<%=salesamount%> data-duration="3000"></span><br>
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
                <div class="widget green-1 animated fadeInDown" style="background-color:#68c39f !important ">
                    <div class="widget-content padding">
                        <div class="widget-icon">
                            <img src="/merchant/images/Settled.png">
                        </div>

                        <div class="text-box">
                            <p class="maindata"><%=partnerDashboard_TOTAL1%> <b><%=partnerDashboard_SETTLED%></b></p>
                            <%
                                String settledval= totalAmount.get("Settled");
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

                            <h2><img src="/partner/images/<%=cur%>.png">
                                <%
                                    }
                                    else if (!list.isEmpty() && functions.isValueNull(list.get(0)))
                                    {
                                %>
                                <h2><img src="/partner/images/<%=list.get(0)%>.png">
                                <%
                                    }
                                %>
                                    <span class="animate-number" style="color: white;" data-value=<%=settledAmount%> data-duration="3000">0</span><br>
                                <span class="animate-number" style="font-size: 20px;">Count = </span>
                                <span class="animate-number" data-value="<%=settledCount%>" data-duration="3000"></span>
                            </h2>
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
                    <div class="widget-content padding">
                        <div class="widget-icon">

                            <img src="/merchant/images/refund_resized1.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata"><%=partnerDashboard_TOTAL2%> <b><%=partnerDashboard_REFUND%></b></p>
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

                                <h2><img src="/partner/images/<%=cur%>.png ">
                                <%
                                    }
                                    else if (!list.isEmpty() && functions.isValueNull(list.get(0)))
                                    {
                                %>
                                    <h2><img src="/partner/images/<%=list.get(0)%>.png ">
                                <%
                                    }
                                %>
                                        <span class="animate-number" style="color: white;" data-value=<%=refundAmount%> data-duration="3000">0</span><br>
                                <span class="animate-number" style="color: white; font-size: 20px;">Count = </span>
                                <span class="animate-number" style="color: white;" data-value="<%=refundCount%>" data-duration="3000"></span>
                            </h2>

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

                <div class="widget darkblue-2 animated fadeInDown">
                    <div class="widget-content padding">
                        <div class="widget-icon">
                            <img src="/merchant/images/Chargeback1.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata"><%=partnerDashboard_TOTAL3%> <b><%=partnerDashboard_CHARGEBACK%></b></p>
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

                            <h2> <img src="/partner/images/<%=cur%>.png">
                                <%
                                    }
                                    else if (!list.isEmpty() && functions.isValueNull(list.get(0)))
                                    {
                                %>
                                <h2> <img src="/partner/images/<%=list.get(0)%>.png">
                                <%
                                    }
                                %>
                                    <span class="animate-number" style="color: white;" data-value=<%=chargebackAmount%> data-duration="3000">0</span><br>
                                <span class="animate-number" style="font-size: 20px;">Count = </span>
                                <span class="animate-number" data-value="<%=chargebackCount%>" data-duration="3000"></span>
                            </h2>

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

            <div class="col-lg-3 col-md-6">
                <div class="widget darkblue-2 animated fadeInDown" style="background-color:#1e8b92 !important">
                    <div class="widget-content padding">
                        <div class="widget-icon">
                            <img src="/merchant/images/declined.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata"><%=partnerDashboard_TOTAL4%> <b><%=partnerDashboard_DECLINED%> </b></p>
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

                            <h2><img src="/partner/images/<%=cur%>.png">
                                <%
                                    }
                                    else if (!list.isEmpty() && functions.isValueNull(list.get(0)))
                                    {
                                %>
                                <h2><img src="/partner/images/<%=list.get(0)%>.png">
                                <%
                                    }
                                %>
                                    <span class="animate-number" style="color: white;" data-value=<%=declinedAmount%> data-duration="3000">0</span>
                                <br>
                                <span class="animate-number" style="font-size: 20px;">Count = </span>
                                <span class="animate-number" data-value="<%=declinedCount%>" data-duration="3000"></span>
                            </h2>
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
            <div class="col-lg-3 col-md-6">
                <div class="widget darkblue-2 animated fadeInDown" style="background-color:#1e8b92 !important">
                    <div class="widget-content padding">
                        <div class="widget-icon">
                            <img src="/merchant/images/declined.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata">TOTAL <b>CAPTURE </b></p>
                            <%
                                String capture= totalAmount.get("capturesuccess");
                                String captureAmount = "";
                                String captureCount ="";
                                if (functions.isValueNull(capture))
                                {
                                    String[] AmountandCountCapture = capture.split("_");
                                    captureAmount = AmountandCountCapture[0];
                                    captureCount = AmountandCountCapture[1];
                                }
                                else
                                {
                                    captureAmount="0.00";
                                    captureCount="0";
                                }
                                if ( functions.isValueNull(currency1))
                                {
                                    if (functions.isValueNull(currency1.substring(0).toUpperCase()))
                                    {
                                        cur=currency1.substring(0).toUpperCase();
                                    }
                            %>

                            <h2><img src="/partner/images/<%=cur%>.png">
                                <%
                                    }
                                    else if (!list.isEmpty() && functions.isValueNull(list.get(0)))
                                    {
                                %>
                                <h2><img src="/partner/images/<%=list.get(0)%>.png">
                                <%
                                    }
                                %>
                                    <span class="animate-number" style="color: white;" data-value=<%=captureAmount%> data-duration="3000">0</span>
                                <br>
                                <span class="animate-number" style="font-size: 20px;">Count = </span>
                                <span class="animate-number" data-value="<%=captureCount%>" data-duration="3000"></span>
                            </h2>
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
            <div class="col-lg-3 col-md-6">
                <div class="widget royalblue-4 animated fadeInDown" style="background-color:royalblue">
                    <div class="widget-content padding">
                        <div class="widget-icon">
                            <img src="/merchant/images/declined.png">
                        </div>
                        <div class="text-box">
                            <p class="maindata">TOTAL <b>PAYOUT SUCCESS </b></p>
                            <%
                                String payoutsuccess= totalAmount.get("payoutsuccessful");
                                String payoutsuccessAmount = "";
                                String payoutsuccessCount ="";
                                if (functions.isValueNull(payoutsuccess))
                                {
                                    String[] AmountandCountPayout = payoutsuccess.split("_");
                                    payoutsuccessAmount = AmountandCountPayout[0];
                                    payoutsuccessCount = AmountandCountPayout[1];
                                }
                                else
                                {
                                    payoutsuccessAmount="0.00";
                                    payoutsuccessCount="0";
                                }
                                if (functions.isValueNull(currency1))
                                {
                                    if (functions.isValueNull(currency1.substring(0).toUpperCase()))
                                    {
                                        cur=currency1.substring(0).toUpperCase();
                                    }
                            %>

                            <h2><img src="/partner/images/<%=cur%>.png">
                                <%
                                    }
                                    else if (!list.isEmpty() && functions.isValueNull(list.get(0)))
                                    {
                                %>
                                <h2><img src="/partner/images/<%=list.get(0)%>.png">
                                <%
                                    }
                                %>
                                    <span class="animate-number" style="color: white;" data-value=<%=payoutsuccessAmount%> data-duration="3000">0</span>
                                <br>
                                <span class="animate-number" style="color: white; font-size: 20px;">Count = </span>
                                <span class="animate-number" style="color: white;" data-value="<%=payoutsuccessCount%>" data-duration="3000"></span>
                            </h2>
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
            <div class="col-lg-3 col-md-6">
                <div class="widget green-1 animated fadeInDown" style="background-color:#68c39f !important">
                    <div class="widget-content padding">
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
                                    String[] AmountandCountPayoutFail = payoutfailed.split("_");
                                    payoutfailedAmount = AmountandCountPayoutFail[0];
                                    payoutfailedCount = AmountandCountPayoutFail[1];
                                }
                                else
                                {
                                    payoutfailedAmount="0.00";
                                    payoutfailedCount="0";
                                }
                                if (functions.isValueNull(currency1))
                                {
                                    if (functions.isValueNull(currency1.substring(0).toUpperCase()))
                                    {
                                        cur=currency1.substring(0).toUpperCase();
                                    }
                            %>

                            <h2><img src="/partner/images/<%=cur%>.png">
                                <%
                                    }
                                    else if (!list.isEmpty() && functions.isValueNull(list.get(0)))
                                    {
                                %>
                                <h2><img src="/partner/images/<%=list.get(0)%>.png">
                                <%
                                    }
                                %>
                                    <span class="animate-number" style="color: white;" data-value=<%=payoutfailedAmount%> data-duration="3000">0</span>
                                <br>
                                <span class="animate-number" style="color: white; font-size: 20px;">Count = </span>
                                <span class="animate-number" style="color: white;" data-value="<%=payoutfailedCount%>" data-duration="3000"></span>
                            </h2>
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
            <%-- <div class="col-sm-12 portlets ui-sortable">
                 <div class="widget">
                     <form name="myformname" id="myformname" action="" method="POST">

                         <div class="form-group col-md-3">
                             <label>&nbsp;</label>
                             <select id="pid" name="pid" onchange="myAllFilterFunctions(this)" class="btn btn-default2 btn-xs dropdown-toggle form-control"<%=Config%>>
                                 &lt;%&ndash;<option  value=""> Partner id </option>&ndash;%&gt;
                                 &lt;&ndash;
                                 <%
                                     String Selected = "";
                                     if (Roles.contains("superpartner"))
                                     {
                                         for(String pid1 : partneriddetails.keySet())
                                         {
                                             if(pid1.toString().equals(pid))
                                             {
                                                 Selected="selected";
                                             }
                                             else
                                             {
                                                 Selected="";
                                             }


                                 %>
                                 <option value="<%=pid1%>" <%=Selected%>><%=partneriddetails.get(pid1)%></option>
                                 <%
                                          }
                                     }
                                     else
                                          {
                                              for(String pid2 : partneriddetails1.keySet())
                                              {
                                                  if(pid2.toString().equals(pid))
                                                  {
                                                      Selected="selected";
                                                  }
                                                  else
                                                  {
                                                      Selected="";
                                                  }

                                 %>
                                     <option value="<%=pid2%>" <%=Selected%>><%=partneriddetails1.get(pid2)%></option>
                                     <%
                                              }
                                         }
                                     %>

                                 </select>
                             </div>

                         <div class="form-group col-md-3">
                             <label>&nbsp;</label>
                             <select id="currency" name="currency" onchange="myAllFilterFunctions(this)" class="btn btn-default2 btn-xs dropdown-toggle form-control">
                                 <option  value=""> All Currency </option>
                                 &lt;&ndash;
                                 <%
                                     for (String currency :list)
                                     {
                                         String isSelected="";
                                         if(currency.equalsIgnoreCase(currency1)){
                                             isSelected="selected";
                                         }

                                         if (functions.isValueNull(currency)){

                                 %>
                                 <option  value="<%=currency%>" <%=isSelected%>> <%=currency.toUpperCase()%> </option>
                                 <%
                                         }

                                     }
                                 %>
                             </select>
                         </div>

                         <div class="form-group col-md-3" >
                             <label>&nbsp;</label>
                             <select id="payMode" name="payMode" onchange="myAllFilterFunctions(this)" class="btn btn-default2 btn-xs dropdown-toggle form-control">
                                 <option  value=""> <%=partnerDashboard_Payment_Mode%> </option>
                                 &lt;%&ndash;
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
                            &lt;%&ndash; <%
                                 }
                             %>&ndash;%&gt;
                         </div>

                         <div class="form-group col-md-3">
                             <label>&nbsp;</label>
                             <select id="payBrand" name="payBrand" onchange="myAllFilterFunctions(this)" class="btn btn-default2 btn-xs dropdown-toggle form-control">
                                 <option  value=""> <%=partnerDashboard_Payment_Brand%> </option>
                                 &lt;&ndash;
                                 <%
                                     if (payBrandList.size() > 0)
                                     {
                                         Set statusSet = payBrandList.keySet();
                                         Iterator iterator=statusSet.iterator();
                                         String selected = "";
                                         String key = "";
                                         String value = "";

                                         while (iterator.hasNext())
                                         {
                                             key = (String)iterator.next();
                                             value = (String) payBrandList.get(key);

                                             if (key.equals(payBrand1))
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
                             &lt;%&ndash;<%
                                 }
                             %>&ndash;%&gt;
                         </div>
                     </form>
                 </div>
             </div>
 --%>
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
                            <h2><strong><%=partnerDashboard_Sales%></strong> <%=partnerDashboard_Chart%></h2>
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
                            <h2><strong>Payout per currency &nbsp;</strong>Chart</h2>
                            <%
                                }
                                else
                                {
                            %>
                            <h2><strong><%=partnerDashboard_Sales_Per_Currency%> &nbsp;</strong><%=partnerDashboard_Chart1%></h2>
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
            <%--</div>--%>
            <%--<div class="row">--%>
                <div class="col-md-4 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><%=partnerDashboard_Status%></strong>&nbsp;<%=partnerDashboard_Chart2%></h2>
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
                            <h2><i class="icon-chart-pie-1"></i> <strong><%=partnerDashboard_Progress%></strong> <%=partnerDashboard_Status1%></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a class="hidden" id="dropdownMenu1" data-toggle="dropdown">
                                    <i class="fa fa-cogs"></i>
                                </a>
                                <ul class="dropdown-menu pull-right" role="menu" aria-labelledby="dropdownMenu1">
                                    <li><a href="#">Action</a></li>
                                    <li><a href="#">Another action</a></li>
                                    <li><a href="#">Something else here</a></li>
                                    <li class="divider"></li>
                                    <li><a href="#">Separated link</a></li>
                                </ul>
                                <a href="#" class="widget-popout hidden tt" title="Pop Out/In"><i class="icon-publish"></i></a>
                                <a href="#" class="widget-maximize hidden"><i class="icon-resize-full-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content">
                            <div id="website-statistic2" class="statistic-chart">
                                <div class="col-sm-12 stacked">
                                    <div class="col-sm-12 status-data">

                                        <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=partnerDashboard_AuthStarted%></span><br>
                                        <div id="bar1" class="barfiller">
                                            <div class="tipWrap">
                                                <span class="tip"><%=partnerDashboard_AuthStarted%></span>
                                            </div>
                                            <span class="fill" data-percentage=<%=countAuthStarted%>></span>
                                        </div>

                                        <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=partnerDashboard_AuthSuccessful%></span><br>
                                        <div id="bar2" class="barfiller">
                                            <div class="tipWrap">
                                                <span class="tip"><%=partnerDashboard_AuthSuccessful%></span>
                                            </div>
                                            <span class="fill" data-percentage=<%=countAuthSuccessful%>></span>
                                        </div>

                                        <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=partnerDashboard_CaptureSuccess%></span><br>
                                        <div id="bar3" class="barfiller">
                                            <div class="tipWrap">
                                                <span class="tip"><%=partnerDashboard_CaptureSuccess%></span>
                                            </div>
                                            <span class="fill" data-percentage=<%=countCaptureSuccess%>></span>
                                        </div>

                                        <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=partnerDashboard_Chargeback1%></span><br>
                                        <div id="bar4" class="barfiller">
                                            <div class="tipWrap">
                                                <span class="tip"><%=partnerDashboard_Chargeback1%></span>
                                            </div>
                                            <span class="fill" data-percentage=<%=countChargeBack%>></span>
                                        </div>

                                        <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=partnerDashboard_MarkedForReversal%></span><br>
                                        <div id="bar5" class="barfiller">
                                            <div class="tipWrap">
                                                <span class="tip"><%=partnerDashboard_MarkedForReversal%></span>
                                            </div>
                                            <span class="fill" data-percentage=<%=countMarkedForReversal%>></span>
                                        </div>

                                        <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=partnerDashboard_Reversed%></span><br>
                                        <div id="bar6" class="barfiller">
                                            <div class="tipWrap">
                                                <span class="tip"><%=partnerDashboard_Reversed%></span>
                                            </div>
                                            <span class="fill" data-percentage=<%=countReversed%>></span>
                                        </div>

                                        <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=partnerDashboard_Settled%></span><br>
                                        <div id="bar7" class="barfiller">
                                            <div class="tipWrap">
                                                <span class="tip"><%=partnerDashboard_Settled%></span>
                                            </div>
                                            <span class="fill" data-percentage=<%=countSettled%>></span>
                                        </div>

                                        <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=partnerDashboard_Failed%></span><br>
                                        <div id="bar8" class="barfiller">
                                            <div class="tipWrap">
                                                <span class="tip"><%=partnerDashboard_Failed%></span>
                                            </div>
                                            <span class="fill" data-percentage=<%=countFailed%>></span>
                                        </div>

                                        <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=partnerDashboard_AuthFailed%></span><br>
                                        <div id="bar9" class="barfiller">
                                            <div class="tipWrap">
                                                <span class="tip"><%=partnerDashboard_AuthFailed%></span>
                                            </div>
                                            <span class="fill" data-percentage=<%=countAuthFailed%>></span>
                                        </div>

                                        <span class="pull-right" style="color: #ffffff;font-weight: bold"><%=partnerDashboard_PartialRefund%></span><br>
                                        <div id="bar10" class="barfiller">
                                            <div class="tipWrap">
                                                <span class="tip"><%=partnerDashboard_PartialRefund%></span>
                                            </div>
                                            <span class="fill" data-percentage=<%=countPartialRefund%>></span>
                                        </div>
                                        <span class="pull-right" style="color: #ffffff;font-weight: bold">PayoutSuccessful</span><br>
                                        <div id="bar11" class="barfiller">
                                            <div class="tipWrap">
                                                <span class="tip">PayoutSuccessful</span>
                                            </div>
                                            <span class="fill" data-percentage=<%=countPayoutSuccess%>></span>
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
<%!
    public static String nullToStr(String str)
    {
        if(str == null)
            return "";
        return str;
    }
%>

</html>

<script src="/partner/NewCss/js/jquery.barfiller.js" type="text/javascript"></script>
<link href="/partner/NewCss/css/style_BarFiller.css" rel="stylesheet" />

<script type="text/javascript">

    $(document).ready(function(){

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
        $('#bar11').barfiller({barColor: '#b93a71',duration: 3000});
        $('#bar12').barfiller({barColor: '#4a525f',duration: 3000});
        $('#bar13').barfiller({barColor: '#54ce28',duration: 3000});

    });

</script>