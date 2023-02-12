<%@ page import="com.directi.pg.Functions" %>
<%@ include file="top.jsp" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 3/15/2017
  Time: 12:39 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
    session.setAttribute("submit", "Risk Rule Graph");
    String partnerid = (String) session.getAttribute("merchantid");
%>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title><%=company%> Merchant Monitoring> Risk Rule Graph</title>

    <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
    <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

    <style type="text/css">
        svg:not(:root) {
            overflow: inherit !important;
        }
    </style>
    <style type="text/css">
        #ui-id-1
        {
            overflow: auto;
            max-height: 350px;
        }
    </style>
    <style type="text/css">
        .morris-hover {
            position: absolute;
            z-index: 1000;
        }

        .morris-hover.morris-default-style {
            border-radius: 10px;
            padding: 6px;
            color: #666;
            background: rgba(255, 255, 255, 0.8);
            border: solid 2px rgba(230, 230, 230, 0.8);
            font-family: sans-serif;
            font-size: 12px;
            text-align: center;
        }

        .morris-hover.morris-default-style .morris-hover-row-label {
            font-weight: bold;
            margin: 0.25em 0;
        }

        .morris-hover.morris-default-style .morris-hover-point {
            white-space: nowrap;
            margin: 0.1em 0;
        }

        svg {
            width: 100%;
        }

        #currencyid {
            position: absolute;
            right: 0px;
            padding: 5px
        }

        @media (max-width: 620px) {
            #currencyid {
                position: initial;
            }
        }

        @media (min-width: 1200px) {
            .widget-icon img {
                width: 50px;
                margin-top: -12px;
            }

            .text-box h2 img {
                margin-top: 4px;
                width: 1.5vw;
            }

            .animate-number {
                font-size: 1.5vw;
            }
        }

        @media (min-width: 992px) and (max-width: 1199px) {
            .widget-icon img {
                width: 50px;
                margin-top: -12px;
            }

            .text-box h2 img {
                margin-top: -1px;
            }

            .animate-number {
                font-size: 2.5vw;
            }
        }

        @media (min-width: 768px) and (max-width: 991px) {
            .widget-icon img {
                width: 50px;
                margin-top: -12px;
            }

            .text-box h2 img {
                margin-top: 3px;
                width: 2.5vw;
            }

            .animate-number {
                font-size: 2.5vw;
            }
        }

        @media (min-width: 480px) and (max-width: 767px) {
            .widget-icon img {
                width: 50px;
                margin-top: -12px;
            }

            .text-box h2 img {
                margin-top: 4px;
                width: 3.5vw;
            }

            .animate-number {
                font-size: 3vw;
            }
        }

        @media (max-width: 479px) {
            .widget-icon img {
                width: 50px;
                margin-top: -12px;
            }

            .text-box h2 img {
                margin-top: 7px;
                width: 4.5vw;
            }

            .animate-number {
                font-size: 4.5vw;
            }
        }

    </style>
</head>
<title><%=company%> | Risk Rule Graph </title>
<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
<link href="/partner/NewCss/css/style-responsive.css" rel="stylesheet" />
<script src="/partner/NewCss/libs/morrischart/morris.min.js"></script>
<script src="/partner/NewCss/chartJS/Chart.bundle.js"></script>
<%--<script src="/partner/NewCss/morrisJS/morris-0.4.1.min.js"></script>--%>
<script src="/partner/NewCss/morrisJS/raphael-min.js"></script>
<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>

<%--<script type="text/javascript">
    function selectTerminals(data, ctoken)
    {
        document.f1.action = "/partner/partnerRiskRuleGraph.jsp?ctoken=" + ctoken;
        document.f1.submit();
    }
</script>--%>
<%
    String dailyJsonStr = (String) request.getAttribute("dailyJsonStr");
    String weeklyJsonStr = (String) request.getAttribute("weeklyJsonStr");
    String weeklyNewJsonStr = (String) request.getAttribute("weeklyNewJsonStr");
    String monthlyJsonStr = (String) request.getAttribute("monthlyJsonStr");

    String dailyTableData = (String) request.getAttribute("dailyTableData");
    String weeklyTableData = (String) request.getAttribute("weeklyTableData");
    String weeklyNewTableData = (String) request.getAttribute("weeklyNewTableData");
    String monthlyTableData = (String) request.getAttribute("monthlyTableData");

    String displayRuleName = (String) request.getAttribute("displayRuleName");
    String lables = (String) request.getAttribute("lables");
%>
<body>
<%
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String partnerRiskRuleGraph_Risk_Rule_Graph = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleGraph_Risk_Rule_Graph")) ? rb1.getString("partnerRiskRuleGraph_Risk_Rule_Graph") : " Risk Rule Graph";
    String partnerRiskRuleGraph_MerchantID = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleGraph_MerchantID")) ? rb1.getString("partnerRiskRuleGraph_MerchantID") : "Merchant ID*";
    String partnerRiskRuleGraph_TerminalID = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleGraph_TerminalID")) ? rb1.getString("partnerRiskRuleGraph_TerminalID") : "Terminal ID*";
    String partnerRiskRuleGraph_Risk_RuleID = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleGraph_Risk_RuleID")) ? rb1.getString("partnerRiskRuleGraph_Risk_RuleID") : "Risk Rule ID*";
    String partnerRiskRuleGraph_Frequency = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleGraph_Frequency")) ? rb1.getString("partnerRiskRuleGraph_Frequency") : "Frequency";
    String partnerRiskRuleGraph_Hourly = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleGraph_Hourly")) ? rb1.getString("partnerRiskRuleGraph_Hourly") : "Hourly";
    String partnerRiskRuleGraph_Daily = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleGraph_Daily")) ? rb1.getString("partnerRiskRuleGraph_Daily") : "Daily";
    String partnerRiskRuleGraph_Weekly = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleGraph_Weekly")) ? rb1.getString("partnerRiskRuleGraph_Weekly") : "Weekly";
    String partnerRiskRuleGraph_Monthly = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleGraph_Monthly")) ? rb1.getString("partnerRiskRuleGraph_Monthly") : "Monthly";
    String partnerRiskRuleGraph_Path = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleGraph_Path")) ? rb1.getString("partnerRiskRuleGraph_Path") : "Path";
    String partnerRiskRuleGraph_Search = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleGraph_Search")) ? rb1.getString("partnerRiskRuleGraph_Search") : "Search";
    String partnerRiskRuleGraph_Threshold_Details = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleGraph_Threshold_Details")) ? rb1.getString("partnerRiskRuleGraph_Threshold_Details") : "Threshold Details";
    String partnerRiskRuleGraph_total = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleGraph_total")) ? rb1.getString("partnerRiskRuleGraph_total") : "[today]";
    String partnerRiskRuleGraph_no_data = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleGraph_no_data")) ? rb1.getString("partnerRiskRuleGraph_no_data") : "NO DATA";
    String partnerRiskRuleGraph_current_week = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleGraph_current_week")) ? rb1.getString("partnerRiskRuleGraph_current_week") : "[in current week]";
    String partnerRiskRuleGraph_last_six = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleGraph_last_six")) ? rb1.getString("partnerRiskRuleGraph_last_six") : "[in last six month]";
    String partnerRiskRuleGraph_Report_Table = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleGraph_Report_Table")) ? rb1.getString("partnerRiskRuleGraph_Report_Table") : "Report Table";
    String partnerRiskRuleGraph_Sorry = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleGraph_Sorry")) ? rb1.getString("partnerRiskRuleGraph_Sorry") : "Sorry";
    String partnerRiskRuleGraph_no = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleGraph_no")) ? rb1.getString("partnerRiskRuleGraph_no") : "No Records Found.";

    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    Logger logger = new Logger("manageChargeMaster");
    if (partner.isLoggedInPartner(session))
    {
        Functions functions = new Functions();
        String memberId = nullToStr(request.getParameter("memberid"));
        String terminalId = nullToStr(request.getParameter("terminalid"));
        String ruleid = nullToStr(request.getParameter("ruleid"));

        String hourlyFrequency = request.getParameter("ishourslyexecution");
        String dailyFrequency = request.getParameter("isdailyexecution");
        String weeklyFrequency = request.getParameter("isweeklyexecution");
        String monthlyFrequency = request.getParameter("ismonthlyexecution");

        if (!functions.isValueNull(memberId))
        {
            memberId = "";
        }
        if (!functions.isValueNull(terminalId))
        {
            terminalId = "";
        }

        String isHourlyChecked="";
        String isDailyChecked="";
        String isWeeklyChecked="";
        String isMonthlyChecked="";

        if(!functions.isValueNull(hourlyFrequency) && !functions.isValueNull(dailyFrequency) && !functions.isValueNull(weeklyFrequency) && !functions.isValueNull(monthlyFrequency))
        {
            isHourlyChecked = "checked";
            isDailyChecked = "checked";
            isWeeklyChecked = "checked";
            isMonthlyChecked = "checked";
        }
        else
        {
            if (functions.isValueNull(hourlyFrequency))
            {
                isHourlyChecked = "checked";
            }
            if (functions.isValueNull(dailyFrequency))
            {
                isDailyChecked = "checked";
            }
            if (functions.isValueNull(weeklyFrequency))
            {
                isWeeklyChecked = "checked";
            }
            if (functions.isValueNull(monthlyFrequency))
            {
                isMonthlyChecked = "checked";
            }
        }
%>
<script>
    window.onload = function ()
    {
        <%
           if(functions.isValueNull(dailyJsonStr))
        {%>
        lineChart();
        <%}
        if(functions.isValueNull(weeklyJsonStr))
        {%>
        lineChart1();
        <%}
        if(functions.isValueNull(weeklyNewJsonStr))
        {%>
        lineChart2();
        <%}
        if(functions.isValueNull(monthlyJsonStr))
        {%>
        lineChart3();
        <%}
      %>
    };
    function lineChart()
    {
        var hour = JSON.parse('<%=dailyJsonStr%>');
        window.line = Morris.Line({
            element: 'bar-example',
            data: hour.data,
            xkey: 'date',
            ykeys: [<%=lables%>],
            labels: [<%=lables%>],
            lineColors: ['#A4ADD3', '#fc8710', '#FF6541'],
            xLabels: "hour",
            resize: true
        });
    }
    function lineChart1()
    {
        var day = JSON.parse('<%=weeklyJsonStr%>');
        Morris.Line({
            element: 'bar-example1',
            data: day.data,
            xkey: 'date',
            ykeys: [<%=lables%>],
            labels: [<%=lables%>],
            lineColors: ['#A4ADD3', '#fc8710', '#FF6541'],
            xLabels: "day",
            resize: true
        });
    }
    function lineChart2()
    {
        var weeks = JSON.parse('<%=weeklyNewJsonStr%>');
        window.line = Morris.Line({
            element: 'bar-example2',
            data: weeks.data,
            xkey: 'date',
            ykeys: [<%=lables%>],
            labels: [<%=lables%>],
            lineColors: ['#A4ADD3', '#fc8710', '#FF6541'],
            xLabels: "week",
            resize: true
        });
    }
    function lineChart3()
    {
        var month = JSON.parse('<%=monthlyJsonStr%>');
        window.line = Morris.Line({
            element: 'bar-example3',
            data: month.data,
            xkey: 'date',
            ykeys: [<%=lables%>],
            labels: [<%=lables%>],
            lineColors: ['#A4ADD3', '#fc8710', '#FF6541'],
            xLabels: "month",
            resize: true
        });
    }
</script>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="row">
            <div class="col-sm-12 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%><%=partnerRiskRuleGraph_Risk_Rule_Graph%></strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding">
                        <div id="">
                            <form action="/partner/net/PartnerMerchantMonitoringGraphController?ctoken=<%=ctoken%>"
                                  method="post" name="f1">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                                <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                                <%
                                    String str = "ctoken=" + ctoken;
                                    if (request.getParameter("memberid") != null)
                                        str = str + "&memberid=" + request.getParameter("memberid");
                                    if (request.getAttribute("error") != null)
                                    {
                                        String message = (String) request.getAttribute("error");
                                        if (functions.isValueNull(message))
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                                    }
                                    if (request.getAttribute("success") != null)
                                    {
                                        String success = (String) request.getAttribute("success");
                                        if (success != null)
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + success + "</h5>");
                                    }
                                %>
                                <div class="form-group col-md-4 has-feedback">

                                    <div class="ui-widget">
                                        <label for="mid"><%=partnerRiskRuleGraph_MerchantID%></label>
                                        <input name="memberid" id="mid" value="<%=memberId%>" class="form-control" autocomplete="on">
                                    </div>
                                    <%-- <label>Merchant ID</label>
                                     <select name="memberid" class="form-control"
                                             onchange="selectTerminals(this,'<%=ctoken%>')">
                                         <option value="" selected>Select Merchant ID</option>
                                         <%
                                             Connection conn = null;
                                             PreparedStatement pstmt = null;
                                             ResultSet rs = null;
                                             try
                                             {
                                                 conn = Database.getConnection();
                                                 String query = "select memberid, company_name from members where activation='Y' AND partnerId = '" + partnerid + "' ORDER BY memberid ASC";
                                                 pstmt = conn.prepareStatement(query);
                                                 rs = pstmt.executeQuery();
                                                 while (rs.next())
                                                 {
                                                     String selection = "";
                                                     if (rs.getString("memberid").equals(memberId))
                                                     {
                                                         selection = "selected";
                                                     }
                                         %>
                                         <option value="<%=rs.getInt("memberid")%>" <%=selection%>><%=rs.getInt("memberid") + "-" + rs.getString("company_name")%>
                                         </option>
                                         ;
                                         <%
                                                 }
                                             }
                                             catch (SystemError se)
                                             {
                                                 logger.error("Exception:::::" + se);
                                             }
                                             finally
                                             {
                                                 Database.closeConnection(conn);
                                             }
                                         %>
                                     </select>--%>
                                </div>
                                <div class="form-group col-md-4 has-feedback">
                                    <div class="ui-widget">
                                        <label for="tidAll"><%=partnerRiskRuleGraph_TerminalID%></label>
                                        <input name="terminalid" id="tidAll" value="<%=terminalId%>" class="form-control" autocomplete="on">
                                    </div>
                                    <%--<label>Terminal ID</label>
                                    <select name="terminalid" class="form-control"
                                            onchange="selectTerminals(this,'<%=ctoken%>')">
                                        <option value="" selected>Select Terminal ID</option>
                                        <%
                                            try
                                            {
                                                conn = Database.getConnection();
                                                String query = "SELECT terminalid,paymodeid,cardtypeid,memberid FROM member_account_mapping where memberid='" + memberId + "'";
                                                pstmt = conn.prepareStatement(query);
                                                rs = pstmt.executeQuery();
                                                while (rs.next())
                                                {
                                                    String selection = "";
                                                    if (rs.getString("terminalid").equals(terminalId))
                                                    {
                                                        selection = "selected";
                                                    }
                                        %>
                                        <option value="<%=rs.getInt("terminalid")%>" <%=selection%>><%=rs.getString("memberid") + "-" + rs.getString("terminalid") + "-" + GatewayAccountService.getPaymentMode(rs.getString("paymodeid")) + "-" + GatewayAccountService.getCardType(rs.getString("cardtypeid"))%>
                                        </option>
                                        ;
                                        <%
                                                }
                                            }
                                            catch (SystemError se)
                                            {
                                                logger.error("Exception:::::" + se);
                                            }
                                            finally
                                            {
                                                Database.closeConnection(conn);
                                            }
                                        %>
                                    </select>--%>
                                </div>
                                <div class="form-group col-md-4 has-feedback">
                                    <div class="ui-widget">
                                        <label for="rn"><%=partnerRiskRuleGraph_Risk_RuleID%></label>
                                        <input name="ruleid" id="rn" value="<%=ruleid%>" class="form-control" autocomplete="on">
                                    </div>
                                    <%--<label>Risk Rule Name</label>
                                    <select name="ruleid" class="form-control">
                                        <option value="" selected></option>
                                        <%
                                            Connection conn = null;
                                            try
                                            {
                                                PreparedStatement pstmt = null;
                                                ResultSet rs = null;
                                                conn = Database.getRDBConnection();
                                                String query = "SELECT mampm.monitoing_para_id,mpm.monitoing_para_name,terminalid FROM member_account_monitoringpara_mapping AS mampm JOIN monitoring_parameter_master AS mpm ON mampm.monitoing_para_id=mpm.monitoing_para_id WHERE memberid='" + request.getParameter("memberid") + "' AND terminalid='" + request.getParameter("terminalid") + "'";
                                                pstmt = conn.prepareStatement(query);
                                                rs = pstmt.executeQuery();
                                                while (rs.next())
                                                {
                                                    String selection = "";
                                                    if (rs.getString("monitoing_para_id").equals(ruleid))
                                                    {
                                                        selection = "selected";
                                                    }
                                        %>
                                        <option value="<%=rs.getString("monitoing_para_id")%>" <%=selection%>><%=rs.getString("monitoing_para_name") + " - " + rs.getString("monitoing_para_id")%>
                                        </option>
                                        ;
                                        <%
                                                }
                                            }
                                            catch (Exception e)
                                            {
                                            }
                                            finally
                                            {
                                                Database.closeConnection(conn);
                                            }
                                        %>
                                    </select>--%>
                                </div>
                                <div class="form-group col-md-4 has-feedback">
                                    <label><%=partnerRiskRuleGraph_Frequency%></label> <br>
                                    <input type="checkbox" value="hoursly" name="ishourslyexecution" id="ishourslyexecution" <%=isHourlyChecked%> style="zoom:1.3" > <b><%=partnerRiskRuleGraph_Hourly%></b> &nbsp;
                                    <input type="checkbox" value="daily" name="isdailyexecution" id="isdailyexecution" <%=isDailyChecked%> style="zoom:1.3" > <b><%=partnerRiskRuleGraph_Daily%></b> &nbsp;
                                    <input type="checkbox" value="weekly" name="isweeklyexecution" id="isweeklyexecution" <%=isWeeklyChecked%> style="zoom:1.3" > <b><%=partnerRiskRuleGraph_Weekly%></b> &nbsp;
                                    <input type="checkbox" value="monthly" name="ismonthlyexecution" id="ismonthlyexecution" <%=isMonthlyChecked%> style="zoom:1.3" > <b><%=partnerRiskRuleGraph_Monthly%></b> &nbsp;
                                </div>
                                <div class="form-group col-md-4">
                                    <label style="color: transparent;"><%=partnerRiskRuleGraph_Path%></label>
                                    <button type="submit" class="btn btn-default" style="display:block;">
                                        <i class="fa fa-clock-o"></i>
                                        &nbsp;&nbsp;<%=partnerRiskRuleGraph_Search%>
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
            <%
                if ("hoursly".equals(hourlyFrequency) && functions.isValueNull(dailyJsonStr))
                {
            %>
            <div class="col-sm-6 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=partnerRiskRuleGraph_Threshold_Details%></strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding" style="overflow-x:auto;">
                        <%
                            if(functions.isValueNull(dailyTableData)) {
                        %>
                        <%=dailyTableData%>
                        <%
                        }else{
                        %>
                        <div class="no-data-text"> NO DATA</div>
                        <%
                            }
                        %>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong><%=displayRuleName%> <%=partnerRiskRuleGraph_total%></strong></h2>

                        <div class="additional-btn">
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
            <%
                }
                if ("daily".equals(dailyFrequency) && functions.isValueNull(weeklyJsonStr))
                {
            %>
            <div class="col-sm-6 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=partnerRiskRuleGraph_Threshold_Details%></strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding" style="overflow-x:auto;">
                        <%
                            if(functions.isValueNull(weeklyTableData)) {
                        %>
                        <%=weeklyTableData%>
                        <%
                        }else{
                        %>
                        <div class="no-data-text"> <%=partnerRiskRuleGraph_no_data%></div>
                        <%
                            }
                        %>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong><%=displayRuleName%> <%=partnerRiskRuleGraph_current_week%></strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding">
                        <div id="bar-example1">
                        </div>
                    </div>
                </div>
            </div>
            <%
                }
                if ("weekly".equals(weeklyFrequency) && functions.isValueNull(weeklyNewJsonStr))
                {
            %>
            <div class="col-sm-6 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=partnerRiskRuleGraph_Threshold_Details%></strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding" style="overflow-x:auto;">
                        <%
                            if(functions.isValueNull(weeklyNewTableData)) {
                        %>
                        <%=weeklyNewTableData%>
                        <%
                        }else{
                        %>
                        <div class="no-data-text"> <%=partnerRiskRuleGraph_no_data%></div>
                        <%
                            }
                        %>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong><%=displayRuleName%> <%=partnerRiskRuleGraph_current_week%></strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding">
                        <div id="bar-example2">
                        </div>
                    </div>
                </div>
            </div>
            <%
                }
                if ("monthly".equals(monthlyFrequency) && functions.isValueNull(monthlyJsonStr))
                {
            %>
            <div class="col-sm-6 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=partnerRiskRuleGraph_Threshold_Details%></strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding" style="overflow-x:auto;">
                        <%
                            if(functions.isValueNull(monthlyTableData)) {
                        %>
                        <%=monthlyTableData%>
                        <%
                        }else{
                        %>
                        <div class="no-data-text"> <%=partnerRiskRuleGraph_no_data%></div>
                        <%
                            }
                        %>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong><%=displayRuleName%> <%=partnerRiskRuleGraph_last_six%></strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding">
                        <div id="bar-example3">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<%
    }
    if (!functions.isValueNull(dailyJsonStr) && !functions.isValueNull(weeklyJsonStr) && !functions.isValueNull(weeklyNewJsonStr) && !functions.isValueNull(monthlyJsonStr))
    {
        out.println("<div class=\"content\">");
        out.println("<div class=\"page-heading\">");
        out.println("<div class=\"row\">");
        out.println("<div class=\"col-sm-12 portlets ui-sortable\">");
        out.println("<div class=\"widget\">");
        out.println("<div class=\"widget-header transparent\">\n" +
                "                                <h2><strong><i class=\"fa fa-th-large\"></i>&nbsp;&nbsp;"+partnerRiskRuleGraph_Report_Table+"</strong></h2>\n" +
                "                                <div class=\"additional-btn\">\n" +
                "                                    <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
                "                                    <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
                "                                    <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
                "                                </div>\n" +
                "                            </div>");
        out.println("<div class=\"widget-content padding\">");
        out.println(Functions.NewShowConfirmation1(partnerRiskRuleGraph_Sorry,partnerRiskRuleGraph_no));
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
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