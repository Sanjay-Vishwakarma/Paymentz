<%@ page import="com.directi.pg.Database" %>
<%@ include file="top.jsp" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.SystemError" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
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
    <title><%=company%> | Risk Rule Graph</title>
    <style type="text/css">
        svg:not(:root) {
            overflow: inherit !important;
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

        .no-data-text{
            text-align: center;
            margin: 15px;
            font-weight: bold;
            font-size: 16px;
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
<!-- Base Css Files -->
<%--<script src="/partner/NewCss/libs/jquery-animate-numbers/jquery.animateNumbers.js"></script>
<link href="/partner/NewCss/libs/nifty-modal/css/component.css" rel="stylesheet"/>
<link href="/partner/NewCss/libs/magnific-popup/magnific-popup.css" rel="stylesheet"/>
<link href="/partner/NewCss/libs/ios7-switch/ios7-switch.css" rel="stylesheet"/>
<link href="/partner/NewCss/libs/pace/pace.css" rel="stylesheet"/>
<link href="/partner/NewCss/libs/prettify/github.css" rel="stylesheet"/>
<link href="/partner/NewCss/css/style.css" rel="stylesheet" type="text/css"/>
<link href="/partner/NewCss/css/style.css" rel="stylesheet" type="text/css"/>
<!-- Extra CSS Libraries End -->
<link href="/partner/NewCss/css/style-responsive.css" rel="stylesheet"/>
<script src="/partner/NewCss/libs/morrischart/morris.min.js"></script>
<script src="/partner/NewCss/chartJS/Chart.bundle.js"></script>
<script src="/partner/NewCss/morrisJS/morris-0.4.1.min.js"></script>
<script src="/partner/NewCss/morrisJS/raphael-min.js"></script>
<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>--%>

<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
<link href="/partner/NewCss/css/style-responsive.css" rel="stylesheet" />
<script src="/partner/NewCss/libs/morrischart/morris.min.js"></script>
<script src="/partner/NewCss/chartJS/Chart.bundle.js"></script>
<%--<script src="/partner/NewCss/morrisJS/morris-0.4.1.min.js"></script>--%>
<script src="/partner/NewCss/morrisJS/raphael-min.js"></script>
<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>

<script type="text/javascript">
    function selectTerminals(data, ctoken)
    {
        document.f1.action = "/partner/partnerRiskRuleGraph.jsp?ctoken=" + ctoken;
        document.f1.submit();
    }
</script>
<%
    String dailyJsonStr = (String) request.getAttribute("dailyJsonStr");
    String weeklyJsonStr = (String) request.getAttribute("weeklyJsonStr");
    String weeklyNewJsonStr = (String) request.getAttribute("weeklyNewJsonStr");
    String monthlyJsonStr = (String) request.getAttribute("monthlyJsonStr");

    String morrisMonthlyLabel = (String) request.getAttribute("morrisMonthlyLabel");
    String morrisWeeklyNewLabel = (String) request.getAttribute("morrisWeeklyNewLabel");
    String morrisWeeklyLabel = (String) request.getAttribute("morrisWeeklyLabel");
    String morrisDailyLabel = (String) request.getAttribute("morrisDailyLabel");

    String dailyTableData = (String) request.getAttribute("dailyTableData");
    String weeklyTableData = (String) request.getAttribute("weeklyTableData");
    String weeklyNewTableData = (String) request.getAttribute("weeklyNewTableData");
    String monthlyTableData = (String) request.getAttribute("monthlyTableData");

    String displayRuleName = (String) request.getAttribute("displayRuleName");

    System.out.println("DailyJsonStr:::::"+dailyJsonStr);
    System.out.println("WeeklyJsonStr:::::"+weeklyJsonStr);
    System.out.println("MonthlyJsonStr:::::"+monthlyJsonStr);


    /*System.out.println("DailyTableData:::::"+dailyTableData);
    System.out.println("WeeklyTableData:::::"+weeklyTableData);
    System.out.println("MonthlyTableData:::::"+monthlyTableData);*/
%>
<body>
<%
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    Logger logger = new Logger("manageChargeMaster");
    if (partner.isLoggedInPartner(session))
    {
        Functions functions = new Functions();
        String memberId = request.getParameter("memberid");
        String terminalId = request.getParameter("terminalid");
        String ruleid = request.getParameter("ruleid");

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
        if(document.getElementById('bar-example'))
        {
            Morris.Bar({
                element: 'bar-example',
                <%=dailyJsonStr%>
                xkey: 'x',
                ykeys: [<%=morrisDailyLabel%>],
                labels: [<%=morrisDailyLabel%>],
                stacked: true,
                resize: true
            });
        }
    }
    function lineChart1()
    {
        if(document.getElementById('bar-example1'))
        {
            Morris.Bar({
                element: 'bar-example1',
                <%=weeklyJsonStr%>
                xkey: 'x',
                ykeys: [<%=morrisWeeklyLabel%>],
                labels: [<%=morrisWeeklyLabel%>],
                stacked: true,
                resize: true
            });
        }
    }
    function lineChart2()
    {
        if(document.getElementById('bar-example2'))
        {
            Morris.Bar({
                element: 'bar-example2',
                <%=weeklyNewJsonStr%>
                xkey: 'x',
                ykeys: [<%=morrisWeeklyNewLabel%>],
                labels: [<%=morrisWeeklyNewLabel%>],
                stacked: true,
                resize: true
            });
        }
    }
    function lineChart3()
    {
        if(document.getElementById('bar-example3'))
        {
            Morris.Bar({
                element: 'bar-example3',
                <%=monthlyJsonStr%>
                xkey: 'x',
                ykeys: [<%=morrisMonthlyLabel%>],
                labels: [<%=morrisMonthlyLabel%>],
                stacked: true,
                resize: true
            });
        }
    }
</script>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="row">
            <div class="col-sm-12 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> Risk Rule Graph</strong></h2>

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
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                                <%
                                    String str = "ctoken=" + ctoken;
                                    if (request.getParameter("memberid") != null)
                                        str = str + "&memberid=" + request.getParameter("memberid");
                                    if (request.getAttribute("error") != null)
                                    {
                                        String message = (String) request.getAttribute("error");
                                        if (message != null)
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
                                    <label>Merchant ID</label>
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
                                    </select>
                                </div>
                                <div class="form-group col-md-4 has-feedback">
                                    <label>Terminal ID</label>
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
                                    </select>
                                </div>
                                <div class="form-group col-md-4 has-feedback">
                                    <label>Risk Rule Name</label>
                                    <select name="ruleid" class="form-control">
                                        <option value="" selected></option>
                                        <%
                                            try
                                            {
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
                                    </select>
                                </div>
                                <div class="form-group col-md-4 has-feedback">
                                    <label>Frequency</label> <br>
                                    <input type="checkbox" value="hoursly" name="ishourslyexecution" id="ishourslyexecution" <%=isHourlyChecked%> style="zoom:1.3" > <b>Hourly</b> &nbsp;
                                    <input type="checkbox" value="daily" name="isdailyexecution" id="isdailyexecution" <%=isDailyChecked%> style="zoom:1.3" > <b>Daily</b> &nbsp;
                                    <input type="checkbox" value="weekly" name="isweeklyexecution" id="isweeklyexecution" <%=isWeeklyChecked%> style="zoom:1.3" > <b>Weekly</b> &nbsp;
                                    <input type="checkbox" value="monthly" name="ismonthlyexecution" id="ismonthlyexecution" <%=isMonthlyChecked%> style="zoom:1.3" > <b>Monthly</b> &nbsp;
                                </div>
                                <div class="form-group col-md-4">
                                    <label style="color: transparent;">Path</label>
                                    <button type="submit" class="btn btn-default" style="display:block;">
                                        <i class="fa fa-clock-o"></i>
                                        &nbsp;&nbsp;Search
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
            <div class="row" style="margin: 0">
                <div class="col-sm-6 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Threshold Details</strong></h2>

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
                            <h2><strong><%=displayRuleName%> [today]</strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">
                            <%
                                if(functions.isValueNull(dailyTableData)) {
                            %>
                            <div id="bar-example"></div>
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
            </div>
            <%
                }
                if ("daily".equals(dailyFrequency) && functions.isValueNull(weeklyJsonStr))
                {
            %>
            <div class="row" style="margin: 0">
                <div class="col-sm-6 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Threshold Details</strong></h2>

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
                            <h2><strong><%=displayRuleName%> [in current week]</strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">
                            <%
                                if(functions.isValueNull(weeklyTableData)) {
                            %>
                            <div id="bar-example1"></div>
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
            </div>
            <%
                }
                if ("weekly".equals(weeklyFrequency) && functions.isValueNull(weeklyNewJsonStr))
                {
            %>
            <div class="row" style="margin: 0">
                <div class="col-sm-6 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Threshold Details</strong></h2>

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
                            <h2><strong><%=displayRuleName%> [in current month]</strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">
                            <%
                                if(functions.isValueNull(weeklyNewTableData)) {
                            %>
                            <div id="bar-example2"></div>
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
            </div>
            <%
                }
                if ("monthly".equals(monthlyFrequency) && functions.isValueNull(monthlyJsonStr))
                {
            %>
            <div class="row" style="margin: 0">
                <div class="col-sm-6 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Threshold Details</strong></h2>

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
                            <h2><strong><%=displayRuleName%> [in last six month]</strong></h2>

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
            <%
                }
            %>
        </div>
    </div>
</div>
<%
    if (!functions.isValueNull(dailyJsonStr) && !functions.isValueNull(weeklyJsonStr) && !functions.isValueNull(weeklyNewJsonStr) && !functions.isValueNull(monthlyJsonStr))
    {
        out.println("<div class=\"content\">");
        out.println("<div class=\"page-heading\">");
        out.println("<div class=\"row\">");
        out.println("<div class=\"col-sm-12 portlets ui-sortable\">");
        out.println("<div class=\"widget\">");
        out.println("<div class=\"widget-header transparent\">\n" +
                "                                <h2><strong><i class=\"fa fa-th-large\"></i>&nbsp;&nbsp;Report Table</strong></h2>\n" +
                "                                <div class=\"additional-btn\">\n" +
                "                                    <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
                "                                    <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
                "                                    <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
                "                                </div>\n" +
                "                            </div>");
        out.println("<div class=\"widget-content padding\">");
        out.println(Functions.NewShowConfirmation1("Sorry", "No Records Found."));
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
</body>
</html>