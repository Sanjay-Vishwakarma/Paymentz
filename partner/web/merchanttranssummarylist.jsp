<%--<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>--%>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.vo.ChartVolumeVO" %>
<%@ page import="net.partner.TransReport" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Calendar" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 3/4/14
  Time: 6:18 PM
  To change this template use File | Settings | File Templates.
  asdasdasd
--%>

<%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>--%>
<%--<script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
<script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Functions functions = new Functions();
    Logger logger = new Logger("merchanttranssummarylist");

    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
    session.setAttribute("submit","merchanttranssummarylist");
    PartnerFunctions partnerFunctions = new PartnerFunctions();
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    String partnerid = session.getAttribute("partnerId").toString();



    /*LinkedHashMap memberidDetails=partnerFunctions.getPartnerMembersDetail((String) session.getAttribute("merchantid"));*/
    String memberid=nullToStr((String)request.getAttribute("toid"));
    String pid=nullToStr(request.getParameter("pid"));
    String Config="";
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    if(Roles.contains("superpartner")){

    }else{
        pid = String.valueOf(session.getAttribute("merchantid"));
        Config = "disabled";
    }
    String str="";
    String fdate=null;
    String tdate=null;


    try
    {
        fdate = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"Days",10,true);
        tdate = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"Days",10,true);

    }
    catch(ValidationException e)
    {

    }

    Date date = new Date();
    SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

    String Date = originalFormat.format(date);
    date.setDate(1);
    String fromDates = originalFormat.format(date);

    fdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDates : request.getParameter("fromdate");
    tdate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");
    String startTime = Functions.checkStringNull(request.getParameter("starttime"));
    String endTime = Functions.checkStringNull(request.getParameter("endtime"));
    if (startTime == null) startTime = "00:00:00";
    if (endTime == null) endTime = "23:59:59";
    Calendar rightNow = Calendar.getInstance();
    if (fdate == null) fdate = "" + 1;
    if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);


    String charttoken = Functions.getFormattedDate("yyMMddHHmmss");

    //PerCurrency
    //List<String> currencyList = partnerFunctions.perCurrency(partnerName);

    String currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");
    String accountid=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
    String bincountrysuccessful = (String) request.getAttribute("bincountrysuccessful");
    String bincountryfailed = (String) request.getAttribute("bincountryfailed");
    String ipcountrysuccessful = (String) request.getAttribute("ipcountrysuccessful");
    String ipcountryfailed = (String) request.getAttribute("ipcountryfailed");
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String merchanttranssummarylist_Merchant_Volume = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_Merchant_Volume")) ? rb1.getString("merchanttranssummarylist_Merchant_Volume") : "Merchant Volume";
    String merchanttranssummarylist_From = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_From")) ? rb1.getString("merchanttranssummarylist_From") : "From";
    String merchanttranssummarylist_To = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_To")) ? rb1.getString("merchanttranssummarylist_To") : "To";
    String merchanttranssummarylist_Partner_ID = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_Partner_ID")) ? rb1.getString("merchanttranssummarylist_Partner_ID") : "Partner ID";
    String merchanttranssummarylist_Merchant_ID = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_Merchant_ID")) ? rb1.getString("merchanttranssummarylist_Merchant_ID") : "Merchant ID*";
    String merchanttranssummarylist_Account_ID = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_Account_ID")) ? rb1.getString("merchanttranssummarylist_Account_ID") : "Account ID";
    String merchanttranssummarylist_Currency = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_Currency")) ? rb1.getString("merchanttranssummarylist_Currency") : "Currency";
    String merchanttranssummarylist_search = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_search")) ? rb1.getString("merchanttranssummarylist_search") : "Search";
    String merchanttranssummarylist_Sales = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_Sales")) ? rb1.getString("merchanttranssummarylist_Sales") : "Sales";
    String merchanttranssummarylist_chart = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_chart")) ? rb1.getString("merchanttranssummarylist_chart") : "Chart";
    String merchanttranssummarylist_Refund = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_Refund")) ? rb1.getString("merchanttranssummarylist_Refund") : "Refund";
    String merchanttranssummarylist_Chargeback = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_Chargeback")) ? rb1.getString("merchanttranssummarylist_Chargeback") : "Chargeback";
    String merchanttranssummarylist_Merchant_Transaction = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_Merchant_Transaction")) ? rb1.getString("merchanttranssummarylist_Merchant_Transaction") : "Merchant Transaction Report";
    String merchanttranssummarylist_Merchantid = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_Merchantid")) ? rb1.getString("merchanttranssummarylist_Merchantid") : "Merchant Id:";
    String merchanttranssummarylist_FromDate = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_FromDate")) ? rb1.getString("merchanttranssummarylist_FromDate") : "FromDate:";
    String merchanttranssummarylist_Status = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_Status")) ? rb1.getString("merchanttranssummarylist_Status") : "Status";
    String merchanttranssummarylist_Transaction = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_Transaction")) ? rb1.getString("merchanttranssummarylist_Transaction") : "Transaction";
    String merchanttranssummarylist_Total_Amount = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_Total_Amount")) ? rb1.getString("merchanttranssummarylist_Total_Amount") : "Total Amount";
    String merchanttranssummarylist_merchant_percentage = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_merchant_percentage")) ? rb1.getString("merchanttranssummarylist_merchant_percentage") : "Merchant Transaction Percentage Report";
    String merchanttranssummarylist_Count_Percentage = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_Count_Percentage")) ? rb1.getString("merchanttranssummarylist_Count_Percentage") : "Count Percentage";
    String merchanttranssummarylist_amount_Percentage = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_amount_Percentage")) ? rb1.getString("merchanttranssummarylist_amount_Percentage") : "Amount Percentage";
    String merchanttranssummarylist_bin_country = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_bin_country")) ? rb1.getString("merchanttranssummarylist_bin_country") : "Bin Country Successful";
    String merchanttranssummarylist_bin_failed = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_bin_failed")) ? rb1.getString("merchanttranssummarylist_bin_failed") : "Bin Country Failed";
    String merchanttranssummarylist_ip_country = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_ip_country")) ? rb1.getString("merchanttranssummarylist_ip_country") : "Ip Country Successful";
    String merchanttranssummarylist_ip_failed = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_ip_failed")) ? rb1.getString("merchanttranssummarylist_ip_failed") : "Ip Country Failed";
    String merchanttranssummarylist_Sorry = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_Sorry")) ? rb1.getString("merchanttranssummarylist_Sorry") : "Sorry";
    String merchanttranssummarylist_Status1 = StringUtils.isNotEmpty(rb1.getString("merchanttranssummarylist_Status1")) ? rb1.getString("merchanttranssummarylist_Status1") : "Status records is not found.";



%>
<style type="text/css">
    .table#myTable > thead > tr > th {font-weight: inherit;text-align: center;}

    .hide_label{
        color: transparent;
        user-select: none;
    }
    .table-condensed a.btn{
        padding: 0;
    }

    .table-condensed .separator{
        padding-left: 0;
        padding-right: 0;
    }

</style>
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

<%--<script>

    $(function() {

        $( ".datepicker" ).datepicker({startDate:'-1y'});
    });
</script>--%>
<script type="text/javascript">

    $('#sandbox-container input').datepicker({
    });
</script>
<script>
    $(function() {
        $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
    });
</script>
<script>
    $(function() {
        $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
    });

    $(function() {
        $('#datetimepicker12').datetimepicker({
            format: 'HH:mm:ss',
            useCurrent: true
        });
    });

    $(function() {
        $('#datetimepicker13').datetimepicker({
            format: 'HH:mm:ss',
            useCurrent: true
        });
    });
</script>
<html>
<head>
    <title><%=company%> Merchant Management> Merchant Volume</title>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <script src="/partner/NewCss/libs/morrischart/morris.min.js"></script>
    <%--<script src="/partner/NewCss/morrisJS/morris-0.4.1.min.js"></script>--%>
    <script src="/partner/NewCss/morrisJS/raphael-min.js"></script>
    <link href="/partner/NewCss/css/style-responsive.css" rel="stylesheet" />
    <%-- <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
     <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>--%>

    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script src="/merchant/datepicker/datetimepicker/moment-with-locales.js"></script>
    <script src="/merchant/datepicker/datetimepicker/bootstrap-datetimepicker.min.js"></script>

    <%--    <script type="text/javascript">

            $('#sandbox-container input').datepicker({
            });
        </script>--%>


    <style type="text/css">
        .morris-hover.morris-default-style {
            border-radius: 10px;
            padding: 6px;
            color: #666;
            background: rgba(255, 255, 255, 0.8);
            border: solid 2px rgba(230, 230, 230, 0.8);
            font-family: sans-serif;
            font-size: 12px;
            text-align: center;
            z-index: inherit;
        }
    </style>
    <style type="text/css">
        #ui-id-2
        {
            overflow: auto;
            max-height: 350px;
        }
    </style>
    <script>

        $(document).ready(function(){

            var w = $(window).width();

            //alert(w);

            if(w > 990){
                //alert("It's greater than 990px");
                $("body").removeClass("smallscreen").addClass("widescreen");
                $("#wrapper").removeClass("enlarged");
            }
            else{
                //alert("It's less than 990px");
                $("body").removeClass("widescreen").addClass("smallscreen");
                $("#wrapper").addClass("enlarged");
                $(".left ul").removeAttr("style");
            }
        });

    </script>

</head>
<body>

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> <%=merchanttranssummarylist_Merchant_Volume%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <form action="/partner/net/MerchantTransSummaryList?ctoken=<%=ctoken%>" method="post" name="forms" >

                                    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                                    <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                                    <input type="hidden" value="<%=company%>" name="companyname" id="companyname">
                                        <%
                                        String currencyError = (String) request.getAttribute("currencyError");
                                        if(currencyError!=null)
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + currencyError + "</h5>");
                                        }
                                        else
                                        {
                                            currencyError="";
                                        }
                                        String errormsg = (String) request.getAttribute("catchError");
                                        if(errormsg!=null)
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
                                        }
                                        else
                                        {
                                            errormsg="";
                                        }
                                    %>

                                    <%--<div class="form-group col-md-3">
                                        <label>From</label>
                                        &lt;%&ndash;<input type="text" name="fromdate" class="datepicker form-control" value="<%=fromdate%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">&ndash;%&gt;
                                        <select size="1" name="fdate" class="form-control" value="<%=request.getParameter("fdate")==null?"":request.getParameter("fdate")%>" >
                                            <%
                                                if (fdate != null)
                                                    out.println(Functions.dayoptions(1, 31, fdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>
                                        <select size="1" name="fmonth" class="form-control" value="<%=request.getParameter("fmonth")==null?"":request.getParameter("fmonth")%>" >
                                            <%
                                                if (fmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>
                                        <select size="1" name="fyear" class="form-control" value="<%=request.getParameter("fyear")==null?"":request.getParameter("fyear")%>" >
                                            <%
                                                if (fyear != null)
                                                    out.println(Functions.yearoptions(2005, 2020, fyear));
                                                else
                                                    out.println(Functions.printoptions(2005, 2020));
                                            %>
                                        </select>
                                        &lt;%&ndash;<input type="text" size="16" name="fdate" class="datepicker form-control" value="<%=fdate%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">&ndash;%&gt;

                                    </div>

                                    <div class="form-group col-md-3">
                                        <label>To</label>
                                        &lt;%&ndash;<input type="text" name="todate" class="datepicker form-control" value="<%=todate%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">&ndash;%&gt;
                                        <select size="1" name="tdate" class="form-control">
                                            <%
                                                if (tdate != null)
                                                    out.println(Functions.dayoptions(1, 31, tdate));
                                                else
                                                    out.println(Functions.printoptions(1, 31));
                                            %>
                                        </select>

                                        <select size="1" name="tmonth" class="form-control">
                                            <%
                                                if (tmonth != null)
                                                    out.println(Functions.newmonthoptions(1, 12, tmonth));
                                                else
                                                    out.println(Functions.printoptions(1, 12));
                                            %>
                                        </select>

                                        <select size="1" name="tyear" class="form-control">
                                            <%
                                                if (tyear != null)
                                                    out.println(Functions.yearoptions(2005, 2020, tyear));
                                                else
                                                    out.println(Functions.printoptions(2005, 2020));
                                            %>
                                        </select>
                                        &lt;%&ndash;<input type="text" size="16" name="tdate" class="datepicker form-control" value="<%=tdate%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">&ndash;%&gt;

                                    </div>--%>
                                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-4">

                                        <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                                            <label><%=merchanttranssummarylist_From%></label>
                                            <input type="text" name="fromdate" id="From_dt" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">
                                        </div>


                                        <div id="From_div" class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4" style="padding: 0;/*width: inherit;*/">
                                            <div class="form-group">
                                                <label class="hide_label"><%=merchanttranssummarylist_From%></label>
                                                <div class='input-group date' >
                                                    <input type='text' id='datetimepicker12' class="form-control" placeholder="HH:MM:SS" name="starttime" maxlength="8" value="<%=startTime%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                                    <%--<div id="datetimepicker12"></div>--%>
                                                </div>
                                            </div>
                                        </div>
                                    </div>



                                    <%--<div class="form-group col-md-3">
                                        <label>To</label>
                                        <input type="text" name="todate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">

                                    </div>--%>
                                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-4">

                                        <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                                            <label><%=merchanttranssummarylist_To%></label>
                                            <input type="text" name="todate" id="To_dt" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">

                                        </div>


                                        <div id="From_div" class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4" style="padding: 0;/*width: inherit;*/">
                                            <div class="form-group">
                                                <label class="hide_label"><%=merchanttranssummarylist_To%></label>
                                                <div class='input-group date' >
                                                    <input type='text' id='datetimepicker13' class="form-control" placeholder="HH:MM:SS" name="endtime" maxlength="8" value="<%=endTime%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                                                </div>
                                            </div>
                                        </div>

                                    </div>

                                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-2 has-feedback">
                                        <label for="pid"><%=merchanttranssummarylist_Partner_ID%></label>
                                        <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                                        <input type="hidden" name="pid"  value="<%=pid%>">
                                    </div>

                                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-2 has-feedback">
                                        <label for="member"><%=merchanttranssummarylist_Merchant_ID%></label>
                                        <input name="toid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                                    </div>

                                    <%--<label>Merchant ID</label>

                                        <select size="1" name="toid" class="form-control">
                                            <option value="all">ALL</option>
                                            <%
                                                String isSelected="";
                                                for(Object mid : memberidDetails.keySet())
                                                {
                                                    if(mid.toString().equals(memberid))
                                                    {
                                                        isSelected="selected";
                                                    }
                                                    else
                                                    {
                                                        isSelected="";
                                                    }
                                            %>
                                            <option value="<%=mid%>" <%=ESAPI.encoder().encodeForHTMLAttribute(isSelected)%>><%=mid+"-"+memberidDetails.get(mid)%></option>
                                            <%
                                                }
                                            %>
                                        </select>--%>
                            </div>

                            <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-2 has-feedback">
                                <label for="member"><%=merchanttranssummarylist_Account_ID%></label>
                                <input name="accountid" id="account_id" value="<%=accountid%>" class="form-control" autocomplete="on">
                            </div>

                            <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-2 has-feedback">
                                <label for="curr"><%=merchanttranssummarylist_Currency%></label>
                                <input name="currency" id="curr" value="<%=currency%>" class="form-control" autocomplete="on">
                            </div>
                            <%--  <label>Currency*</label>

                              <select size="1" name="currency" class="form-control">
                                  <option value="">Select Currency</option>
                                  <%
                                      if(currencyList.size()>0)
                                      {
                                          Iterator currencyIterator = currencyList.iterator();
                                          String singleCurrency = "";
                                          while (currencyIterator.hasNext())
                                          {
                                              singleCurrency = (String) currencyIterator.next();
                                              String select = "";

                                              if(currency.equalsIgnoreCase(singleCurrency))
                                                  select = "selected";
                                  %>
                                  <option value="<%=singleCurrency%>" <%=select%>><%=singleCurrency%></option>
                                  <%
                                          }
                                      }
                                  %>

                              </select>--%>
                            <%--  </div>

                              <div class="form-group col-md-9">
                              </div>
--%>
                            <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-2 has-feedback">
                                <label>&nbsp;</label>
                                <button type="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=merchanttranssummarylist_search%></button>

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        </form>
        <br>
        <div class="row reporttable">


            <%

                HashMap status_data=(HashMap)request.getAttribute("status_report");
                HashMap status_reportpercentage=(HashMap)request.getAttribute("status_reportpercentage");
                HashMap SuccessCount= (HashMap) request.getAttribute("totalcounthash");
                HashMap SuccessCount1= (HashMap) request.getAttribute("totalcounthash1");

                String style="class=\"tr00\"";
                int records = 0;
                int totalrecords = 0;

                int records1 = 0;
                int totalrecords1 = 0;

                HashMap temphash=new HashMap();
                HashMap temphash1=new HashMap();
                String fdtstamp=(String)request.getAttribute("fdtstamp");
                String tdtstamp=(String)request.getAttribute("tdtstamp");

                try
                {
                    records = Integer.parseInt((String) status_data.get("records"));
                    totalrecords = Integer.parseInt((String) status_data.get("totalrecords"));

                    records1 = Integer.parseInt((String) status_reportpercentage.get("records"));
                    totalrecords1 = Integer.parseInt((String) status_reportpercentage.get("totalrecords"));
                }
                catch (Exception ex)
                {
                }
                if (records > 0 && status_data!=null)
                {
            %>
            <%
                if("".equals(memberid)){
            %>
            <tr><td align="center" colspan="2">
                <font color="red" size="3"><b>Merchant mandatory input.</b></font>
            </td>   </tr>
            <%
            }
            else {


                TransReport transReport = new TransReport();
                ChartVolumeVO chartVolumeVO = transReport.prepareAllChartsData(memberid,partnerid, fdtstamp, tdtstamp, charttoken,currency,accountid);


            %>
            <br>


            <script>

                window.onload = function()
                {
                    salesChart();

                    refundChart();

                    chargebackChart();
                    donutChart1();
                    donutChart2();
                    donutChart3();
                    donutChart4();
                };

                $(document).ready(function() {

                    $(window).resize(function() {

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
                    <%if(functions.isValueNull(chartVolumeVO.getSalesChart()))

                    {
                    %>
                    window.salesChart = Morris.Bar(<%=chartVolumeVO.getSalesChart()%>);
                    <%
                        }
                        else
                        {
                            out.println("No data to display");
                        }
                    %>
                }

                function refundChart()
                {
                    <%if(functions.isValueNull(chartVolumeVO.getRefundChart())){%>
                    window.refundChart = Morris.Bar(<%=chartVolumeVO.getRefundChart()%>);
                    <%
                        }
                        else
                        {
                            out.println("No data to display");
                        }
                    %>
                }

                function chargebackChart()
                {
                    <%if(functions.isValueNull(chartVolumeVO.getChargebackChart())){%>
                    window.chargebackChart = Morris.Bar(<%=chartVolumeVO.getChargebackChart()%>);
                    <%
                        }
                        else
                        {
                        out.println("No data to display");
                        }
                    %>
                }

            </script>

            <div class="col-md-6 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong><%=merchanttranssummarylist_Sales%></strong> <%=merchanttranssummarylist_chart%></h2>
                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding">
                        <div id="salesChart" >

                        </div>
                    </div>
                </div>
            </div>

            <div class="col-md-6 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong><%=merchanttranssummarylist_Refund%></strong> <%=merchanttranssummarylist_chart%></h2>
                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding">
                        <div id="refundChart">

                        </div>
                    </div>
                </div>
            </div>

            <div class="col-md-6 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong><%=merchanttranssummarylist_Chargeback%></strong> <%=merchanttranssummarylist_chart%></h2>
                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding">
                        <div id="chargebackChart" >

                        </div>
                    </div>
                </div>
            </div>
            <%
                }
            %>

            <div class="col-sm-6 portlets ui-sortable">

                <div class="widget">

                    <div class="widget-header transparent">
                        <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=merchanttranssummarylist_Merchant_Transaction%></strong></h2>
                    </div>
                    <div class="ibox-content">
                        <div class="flot-chart">
                            <table class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>
                                <tr>
                                    <td  class="mtransreport" colspan="1" align="center"><%=merchanttranssummarylist_Merchantid%> <%=memberid%></td>
                                    <td  class="mtransreport" colspan="2" align="center"><%=merchanttranssummarylist_FromDate%> <%=fdate%>&nbsp;&nbsp;ToDate: <%=tdate%></td>
                                </tr>
                                </thead>
                                <thead>
                                <tr style="background-color: #7eccad !important;color: white;">
                                    <th style="text-align: center"><%=merchanttranssummarylist_Status%></th>
                                    <th style="text-align: center"><%=merchanttranssummarylist_Transaction%></th>
                                    <th style="text-align: center"><%=merchanttranssummarylist_Total_Amount%></th>
                                </tr>
                                </thead>
                                <tbody>
                                <%  int tempcount=0;
                                    String tempamt=null;
                                    for (int pos = 1; pos < records; pos++)
                                    {
                                        String id = Integer.toString(pos);

                                        temphash = (HashMap) status_data.get(id);

                                        String status=(String)temphash.get("status");
                                        String count=(String)temphash.get("count");
                                        String amount=(String)temphash.get("amount");

                                        PartnerDAO partnerDAO = new PartnerDAO();

                                        status = partnerDAO.getFormattedStatus(status);

                                %>
                                <tr>
                                    <td data-label="Status" style="text-align: center"><%=status%> </td>
                                    <td data-label="Transaction" style="text-align: center"><%=count%> </td>
                                    <td data-label="Total Amount" style="text-align: center"><%=amount%> </td>
                                </tr>
                                <%      //tempcount = tempcount+Integer.parseInt(count);

                                }
                                    out.println("<tr "+style+">");
                                    out.println("<td colspan=\"3\" align=\"center\" style=\"background-color: #7eccad !important;color: white;\">TOTAL Transaction:<font color=\"#FFFFF\"> "+totalrecords+"</font> </td>");
                                    out.println("</tr >");
                                    out.println("<tr "+style+">");
                                    out.println("<td colspan=\"3\" align=\"center\" style=\"background-color: #7eccad !important;color: white;\">GRAND Total Amount:<font color=\"#FFFFF\"> "+status_data.get("grandtotal")+"</font> </td>");
                                    out.println("</tr >");
                                    out.println("<tr "+style+">");
                                    out.println("<td colspan=\"3\" align=\"center\" style=\"background-color: #7eccad !important;color: white;\">Total Successful Count:<font color=\"#FFFFF\"> "+SuccessCount.get("count")+"</font> </td>");
                                    out.println("</tr >");
                                    out.println("<tr "+style+">");
                                    out.println("<td colspan=\"3\" align=\"center\" style=\"background-color: #7eccad !important;color: white;\">Total Successful Amount:<font color=\"#FFFFF\"> "+SuccessCount.get("amount")+"</font> </td>");
                                    out.println("</tr >");

                                %>
                                </tbody>

                            </table>

                        </div>
                    </div>
                </div>
            </div>

        </div>

        <div class="row">
        <div class="col-sm-12 portlets ui-sortable">

            <div class="widget">

                <div class="widget-header transparent">
                    <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=merchanttranssummarylist_merchant_percentage%></strong></h2>
                </div>
                <div class="ibox-content">
                    <div class="flot-chart">
                        <table class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                            <thead>
                            <tr>
                                <td  class="mtransreport" colspan="2" align="center"><%=merchanttranssummarylist_Merchantid%> <%=memberid%></td>
                                <td  class="mtransreport" colspan="3" align="center"><%=merchanttranssummarylist_FromDate%> <%=fdate%>&nbsp;&nbsp;ToDate: <%=tdate%></td>
                            </tr>
                            </thead>
                            <thead>
                            <tr style="background-color: #7eccad !important;color: white;">
                                <th style="text-align: center"><%=merchanttranssummarylist_Status%></th>
                                <th style="text-align: center"><%=merchanttranssummarylist_Transaction%></th>
                                <th style="text-align: center"><%=merchanttranssummarylist_Count_Percentage%></th>
                                <th style="text-align: center"><%=merchanttranssummarylist_Total_Amount%></th>
                                <th style="text-align: center"><%=merchanttranssummarylist_amount_Percentage%></th>
                            </tr>
                            </thead>
                            <tbody>
                            <%  int tempcount1=0;
                                String tempamt1=null;
                                for (int pos = 1; pos < records1; pos++)
                                {
                                    String id = Integer.toString(pos);
                                    temphash1 = (HashMap) status_reportpercentage.get(id);
                                    String status=(String)temphash1.get("status");
                                    String count=(String)temphash1.get("count");
                                    Float total_record = Float.parseFloat((String) status_reportpercentage.get("totalrecords"));
                                     Float countpercentage = (Float.parseFloat(functions.isValueNull(count)? count :"0")/total_record) * 100;
                                     String amount=(String)temphash1.get("amount");
                                    float grand_total =0;
                                    float amountpercentage =0;
                                    try
                                    {
                                        String total = (String) status_reportpercentage.get("grandtotal");
                                        grand_total = Float.parseFloat(total);
                                         amountpercentage = (Float.parseFloat(functions.isValueNull(amount)? amount :"0.00")/grand_total) * 100;
                                    }
                                    catch (NumberFormatException e){

                                        logger.error("NumberFormatException---" + e);
                                    }


                                    PartnerDAO partnerDAO = new PartnerDAO();

                                    status = partnerDAO.getFormattedStatus(status);

                            %>
                            <tr>
                                <td data-label="Status" style="text-align: center"><%=status%> </td>
                                <td data-label="Transaction" style="text-align: center"><%=count%> </td>
                                <td data-label="countpercentage" style="text-align: center"><%=countpercentage%> </td>
                                <td data-label="Total Amount" style="text-align: center"><%=amount%> </td>
                                <td data-label="amountpercentage" style="text-align: center"><%=amountpercentage%> </td>
                            </tr>
                            <%      //tempcount = tempcount+Integer.parseInt(count);

                            }
                                out.println("<tr "+style+">");
                                out.println("<td colspan=\"5\" align=\"center\" style=\"background-color: #7eccad !important;color: white;\">TOTAL Transaction:<font color=\"#FFFFF\"> "+totalrecords1+"</font> </td>");
                                out.println("</tr >");
                                out.println("<tr "+style+">");
                                out.println("<td colspan=\"5\" align=\"center\" style=\"background-color: #7eccad !important;color: white;\">GRAND Total Amount:<font color=\"#FFFFF\"> "+status_reportpercentage.get("grandtotal")+"</font> </td>");
                                out.println("</tr >");
                                out.println("<tr "+style+">");
                                out.println("<td colspan=\"5\" align=\"center\" style=\"background-color: #7eccad !important;color: white;\">Total Successful Count:<font color=\"#FFFFF\"> "+SuccessCount1.get("count")+"</font> </td>");
                                out.println("</tr >");
                                out.println("<tr "+style+">");
                                out.println("<td colspan=\"5\" align=\"center\" style=\"background-color: #7eccad !important;color: white;\">Total Successful Amount:<font color=\"#FFFFF\"> "+SuccessCount1.get("amount")+"</font> </td>");
                                out.println("</tr >");



                            %>
                            </tbody>

                        </table>

                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

        <%--<div class="row">--%>
            <div class="col-md-6 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong><%=merchanttranssummarylist_bin_country%></strong> <%=merchanttranssummarylist_chart%></h2>
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

            <div class="col-md-6 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong><%=merchanttranssummarylist_bin_failed%> </strong> <%=merchanttranssummarylist_chart%></h2>
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
                        <h2><strong><%=merchanttranssummarylist_ip_country%> </strong> <%=merchanttranssummarylist_chart%></h2>
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
                        <h2><strong><%=merchanttranssummarylist_ip_failed%> </strong><%=merchanttranssummarylist_chart%></h2>
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

        <%     }
        else
        {
            out.println(Functions.NewShowConfirmation1(merchanttranssummarylist_Sorry,merchanttranssummarylist_Status1));
        }


        %>

    </div>
</div>
</div>

<script>
    function donutChart1()
    {
        console.log("inside pie chart")
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
        console.log("inside pie chart")
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
</script>

<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
</body>
</html>
<%!
    public static String nullToStr(String str)
    {
        if(str == null)
            return "";
        return str;
    }
%>
