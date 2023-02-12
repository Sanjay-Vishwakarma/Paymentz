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
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.Iterator" %>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 3/30/2017
  Time: 3:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
    session.setAttribute("submit", "Risk Rule Graph");
    String partnerid = (String) session.getAttribute("merchantid");
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    Logger logger = new Logger("manageChargeMaster");
    if (partner.isLoggedInPartner(session))
    {
%>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title><%=company%> | Risk Rule Mapping</title>
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
<title> Merchant Threshold </title>
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
<script src="/partner/NewCss/js/jquery.barfiller.js" type="text/javascript"></script>
<link href="/partner/NewCss/css/style_BarFiller.css" rel="stylesheet"/>
<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>--%>

<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
<link href="/partner/NewCss/css/style-responsive.css" rel="stylesheet" />
<script src="/partner/NewCss/libs/morrischart/morris.min.js"></script>
<script src="/partner/NewCss/chartJS/Chart.bundle.js"></script>
<script src="/partner/NewCss/morrisJS/morris-0.4.1.min.js"></script>
<script src="/partner/NewCss/morrisJS/raphael-min.js"></script>
<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>

<script src="/merchant/NewCss/js/jquery.barfiller.js" type="text/javascript"></script>
<link href="/merchant/NewCss/css/style_BarFiller.css" rel="stylesheet" />

<script type="text/javascript">
    function selectTerminals(data, ctoken)
    {
        document.f1.action = "/partner/partnerRiskRuleDonutGraph.jsp?ctoken=" + ctoken;
        document.f1.submit();
    }
</script>
<body>
<%
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
                                    <label>Member ID</label>
                                    <select name="memberid" class="form-control"
                                            onchange="selectTerminals(this,'<%=ctoken%>')">
                                        <option value="" selected>Select Member ID</option>
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
                String displayRuleName = (String) request.getAttribute("displayRuleName");
                Hashtable hashtable = (Hashtable) request.getAttribute("hashtable");
                Hashtable hashtable1 = (Hashtable) request.getAttribute("hashtable1");
                if (hashtable != null && hashtable1 != null)
                {
                    Iterator iterator = hashtable.keySet().iterator();
                    String unit = "T";
                    int alertThreshold = (int) request.getAttribute("alertThresholdOrange");
                    int suspensionThreshold = (int) request.getAttribute("suspensionThresholdRed");
                    int i = 0;
                    while (iterator.hasNext())
                    {
                        i = i + 1;
                        String status = (String) iterator.next();
                        int actualInactiveDays = Integer.parseInt((String) hashtable.get(status));

                        String dailyTableData = (String) hashtable1.get(status);
                        String message = "[" + displayRuleName + "]" + "<br>" + status + " transactions";
                        int actualInactiveGreen = 0;
                        int alertThresholdOrange = 0;
                        int suspensionThresholdRed = 0;
                        String color = "";
                        if (actualInactiveDays > alertThreshold && actualInactiveDays > suspensionThreshold)
                        {
                            actualInactiveGreen = alertThreshold;
                            alertThresholdOrange = (suspensionThreshold - actualInactiveGreen);
                            suspensionThresholdRed = actualInactiveDays - (actualInactiveGreen + alertThresholdOrange);
                        }
                        else if (actualInactiveDays > alertThreshold)
                        {
                            actualInactiveGreen = alertThreshold;
                            alertThresholdOrange = actualInactiveDays - actualInactiveGreen;
                        }
                        else
                        {
                            actualInactiveGreen = actualInactiveDays;
                        }

                        int actualInactive = actualInactiveGreen + alertThresholdOrange + suspensionThresholdRed;
                        String actualInactiveFill = actualInactive + "";
                        if (actualInactive > 100)
                        {
                            actualInactiveFill = "90";
                        }
                        if (actualInactiveDays > 0 && actualInactiveDays <= alertThreshold)
                        {
                            color = "#68c39f";
                        }
                        if (actualInactiveDays > alertThreshold && actualInactiveDays <= suspensionThreshold)
                        {
                            color = "#edce8c";
                        }
                        if (actualInactiveDays > suspensionThreshold)
                        {
                            color = "#900";
                        }
            %>
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
                        <%=dailyTableData%>
                    </div>
                </div>
            </div>
            <div class="col-sm-6 portlets ui-sortable">
                <div class="widget">
                    <div class="widget-header transparent">
                        <h2><strong><%=status%> Transactions</strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding" style="height:170px;">
                        <div id="bar_<%=status%>" class="barfiller">
                            <div class="tipWrap">
                                <span class="tip"></span>
                            </div>
                                <span class="fill" data-percentage="<%=actualInactiveFill%>"
                                      data-value="<%=actualInactiveDays%>" data-unit="<%=unit%>"></span>
                        </div>
                    </div>
                </div>
            </div>
            <script type="text/javascript">
                $(document).ready(function ()
                {
                    $('#bar_<%=status%>').barfiller({barColor: '<%=color%>', duration: 3000});
                });
            </script>
            <%
                }
            %>
        </div>
    </div>
</div>
<%
        }
        else
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
    }
    else
    {
        response.sendRedirect("/partner/logout.jsp");
        return;
    }
%>

</body>
</html>
