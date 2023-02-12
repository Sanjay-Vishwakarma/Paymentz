<%@ page import="com.directi.pg.Functions,com.directi.pg.Logger" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.merchantmonitoring.MerchantTerminalThresholdVO" %>
<%@ page import="com.manager.vo.merchantmonitoring.TerminalThresholdsVO" %>
<%!
    private static Logger log = new Logger("merchantTerminalThreshold.jsp");
%>
<html>
<head>
</head>
<title> Merchant Threshold </title>
<script type="text/javascript">
    function confirmsubmit2(i)
    {
        document.getElementById("details"+i).submit();
    }
</script>

<body class="bodybackground">
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Merchant Monitoring Master
                <%--<div style="float: right;">
                    <form action="/icici/addNewRiskParameter.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Add New Partner Logo" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add Monitoring Parameter
                        </button>
                    </form>
                </div>--%>
                <div style="float: right;">
                    <form action="/icici/manageMonitoringParameter.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Add New Partner Logo" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Parameter Mapping
                        </button>
                    </form>
                </div>
                <div style="float: right;">
                    <form action="/icici/monitoringParameterMaster.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Add New Partner Logo" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Parameter Mster
                        </button>
                    </form>
                </div>
                <div style="float: right;">
                    <form action="/icici/mappingMaster.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Add New Partner Logo" name="submit">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Mapping Master
                        </button>
                    </form>
                </div>

            </div>
            <% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                if (com.directi.pg.Admin.isLoggedIn(session))
                {
            %>
            <form action="/icici/servlet/MerchantTerminalThresholdList?ctoken=<%=ctoken%>" method="get" name="F1"
                  onsubmit="">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <br>
                <table align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
                    <tr>
                        <td>
                            <%
                                String errormsg1 = (String) request.getAttribute("error");
                                if (errormsg1 != null)
                                {
                                    out.println("<center><font class=\"textb\"><b>" + errormsg1 + "<br></b></font></center>");
                                }
                            %>
                            <%
                                String errormsg = (String) request.getAttribute("cbmessage");
                                if (errormsg == null)
                                    errormsg = "";
                                out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" ><b>");
                                out.println(errormsg);
                                out.println("</b></font></td></tr></table>");
                            %>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb">Member Id</td>
                                    <td width="0%" class="textb"></td>
                                    <td width="22%" class="textb">
                                        <input name="memberid" size="10" class="txtbox">
                                    </td>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="50%" class="textb">
                                        <button type="submit" class="buttonform" style="margin-left:40px; ">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </form>
        </div>
    </div>
</div>
<div class="reporttable" style="margin-bottom: 9px;">
    <%
        String errormsg2 = (String) request.getAttribute("error1");
        if (errormsg2 != null)
        {
            out.println("<table align=\"center\"><tr><td><font class=\"textb\"><b>");
            out.println(errormsg2);
            out.println("</b></font>");
            out.println("</td></tr></table>");
        }
        if (request.getAttribute("success1")!=null)
        {
            out.println("<table align=\"center\"><tr><td><font class=\"textb\"><b>");
            out.println((String)request.getAttribute("success1"));
            out.println("</b></font>");
            out.println("</td></tr></table>");
        }

        List<MerchantTerminalThresholdVO> merchantTerminalThresholdVOs = (List) request.getAttribute("merchantTerminalThresholdVOs");
        if (merchantTerminalThresholdVOs!=null && merchantTerminalThresholdVOs.size() > 0)
        {
            for (MerchantTerminalThresholdVO merchantTerminalThresholdVO : merchantTerminalThresholdVOs)
            {
                TerminalVO terminalVO = merchantTerminalThresholdVO.getTerminalVO();
                TerminalThresholdsVO terminalThresholdsVO = merchantTerminalThresholdVO.getTerminalThresholdsVO();
    %>
    <form name="update" id="details<%=terminalVO.getTerminalId()%>" action="/icici/servlet/UpdateTerminalThreshold?ctoken=<%=ctoken%>" method=post>
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <input type=hidden value="<%=terminalVO.getAccountId()%>" name="accountid">
        <input type=hidden value="<%=terminalVO.getTerminalId()%>" name="terminalid">
        <input type=hidden value="<%=merchantTerminalThresholdVO.getMemberId()%>" name="memberid">
        <input type=hidden value="UPDATE" name="action">
        <table border="0" width="100%" class="table table-striped table-bordered table-green dataTable"
               style="margin-bottom: 0px">
            <thead>
            <tr>
                <td valign="middle" align="center" class="th0" colspan='5'><font size=2>Merchant Monitoring(Triggers & Alert)</font></td>
                <td valign="middle" align="center" class="th0" style="border-left-style: hidden"><%=terminalVO.toString()%></td>
            </tr>
            </thead>
            <thead>
            <tr>
                <td class="textb" valign="middle" align="center">Daily Approval Ratio(Count)</td>
                <td class="textb" valign="middle" align="center">Weekly Approval Ratio(Count)</td>
                <td class="textb" valign="middle" align="center">Monthly Approval Ratio(Count)</td>
                <td class="textb" valign="middle" align="center">First Submission Threshold(Day's)</td>
                <td class="textb" valign="middle" align="center">Inactive Period Threshold(Day's)</td>
                <td class="textb" valign="middle" align="center">Manual Capture Threshold(Day's)</td>
            </tr>
            </thead>
            <tr>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="daily_approval_ratio"  value="<%=Functions.round(terminalThresholdsVO.getDailyApprovalRatio(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="weekly_approval_ratio" value="<%=Functions.round(terminalThresholdsVO.getWeeklyApprovalRatio(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="monthly_approval_ratio" value="<%=Functions.round(terminalThresholdsVO.getMonthlyApprovalRatio(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="first_submission_threshold"  value="<%=terminalThresholdsVO.getFirstSubmissionThreshold()%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="inactive_period_threshold" value="<%=terminalThresholdsVO.getInactivePeriodThreshold()%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="manual_capture_threshold"  value="<%=terminalThresholdsVO.getManualCaptureAlertThreshold()%>"></td>
            </tr>
            <thead>
            <tr>
                <td class="textb" valign="middle" align="center">Daily CB Ratio(Count)</td>
                <td class="textb" valign="middle" align="center">Weekly CB Ratio(Count)</td>
                <td class="textb" valign="middle" align="center">Monthly CB Ratio(Count)</td>
                <td class="textb" valign="middle" align="center">Daily CB Ratio(Amt)</td>
                <td class="textb" valign="middle" align="center">Weekly CB Ratio(Amt)</td>
                <td class="textb" valign="middle" align="center">Monthly CB Ratio(Amt)</td>
            </tr>
            </thead>
            <tr>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="daily_cb_ratio"  value="<%=Functions.round(terminalThresholdsVO.getDailyCBRatio(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="weekly_cb_ratio" value="<%=Functions.round(terminalThresholdsVO.getWeeklyCBRatio(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="monthly_cb_ratio" value="<%=Functions.round(terminalThresholdsVO.getMonthlyCBRatio(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="daily_cb_ratio_amount" value="<%=Functions.round(terminalThresholdsVO.getDailyCBRatioAmount(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="weekly_cb_ratio_amount" value="<%=Functions.round(terminalThresholdsVO.getWeeklyCBRatioAmount(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="monthly_cb_ratio_amount" value="<%=Functions.round(terminalThresholdsVO.getMonthlyCBRatioAmount(),2)%>"></td>
            </tr>
            <tr>
                <td class="textb" valign="middle" align="center">Daily RF Ratio(Count)</td>
                <td class="textb" valign="middle" align="center">Weekly RF Ratio(Count)</td>
                <td class="textb" valign="middle" align="center">Monthly RF Ratio(Count)</td>
                <td class="textb" valign="middle" align="center">Daily RF Ratio(Amt)</td>
                <td class="textb" valign="middle" align="center">Weekly RF Ratio(Amt)</td>
                <td class="textb" valign="middle" align="center">Monthly RF Ratio(Amt)</td>
            </tr>
            </thead>
            <tr>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="daily_rf_ratio" value="<%=Functions.round(terminalThresholdsVO.getDailyRFRatio(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="weekly_rf_ratio" value="<%=Functions.round(terminalThresholdsVO.getWeeklyRFRatio(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="monthly_rf_ratio" value="<%=Functions.round(terminalThresholdsVO.getMonthlyRFRatio(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="daily_rf_ratio_amount" value="<%=Functions.round(terminalThresholdsVO.getDailyRFAmountRatio(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="weekly_rf_ratio_amount" value="<%=Functions.round(terminalThresholdsVO.getWeeklyRFAmountRatio(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="monthly_rf_ratio_amount" value="<%=Functions.round(terminalThresholdsVO.getMonthlyRFAmountRatio(),2)%>"></td>
            </tr>
            <tr>
                <td class="textb" valign="middle" align="center">Daily Avg Ticket Amt</td>
                <td class="textb" valign="middle" align="center">Weekly Avg Ticket Amt</td>
                <td class="textb" valign="middle" align="center">Monthly Avg Ticket Amt</td>
                <td class="textb" valign="middle" align="center">Daily Vs Quarterly Avg Ticket Threshold(%)</td>
                <td class="textb" valign="middle" align="center">Alert CB Count(Monthly)</td>
                <td class="textb" valign="middle" align="center">Suspend CB Count(Monthly)</td>
            </tr>
            <tr>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="daily_avgticket_threshold" value="<%=Functions.round(terminalThresholdsVO.getDailyAvgTicketThreshold(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="weekly_avgticket_threshold" value="<%=Functions.round(terminalThresholdsVO.getWeeklyAvgTicketThreshold(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="monthly_avgticket_threshold" value="<%=Functions.round(terminalThresholdsVO.getMonthlyAvgTicketThreshold(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="daily_vs_quarterly_avgticket_threshold"  value="<%=Functions.round(terminalThresholdsVO.getDailyVsQuarterlyAvgTicketThreshold(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="alert_cbcount_threshold"  value="<%=terminalThresholdsVO.getAlertCBCountThreshold()%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="suspend_cbcount_threshold"  value="<%=terminalThresholdsVO.getSuspendCBCountThreshold()%>"></td>
            </tr>
            <tr>
                <td class="textb" valign="middle" align="center">Prior Month RF V Current Month Sales(%)</td>
                <td class="textb" valign="middle" align="center">Same Card Same Amount Threshold(Count)</td>
                <td class="textb" valign="middle" align="center">Resume Processing Alert</td>
                <td class="textb" valign="middle" align="center">Daily Avg Ticket Percentage Threshold</td>
                <td class="textb" valign="middle" align="center">Daily CB Suspension Ratio(Count)</td>
                <td class="textb" valign="middle" align="center">Daily CB Suspension(Amt)</td>
            </tr>
            <tr>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="priormonth_rf_vs_currentmonth_sales_threshold"  value="<%=Functions.round(terminalThresholdsVO.getPriorMonthRFVsCurrentMonthSales(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="samecard_cardamount_threshold"  value="<%=terminalThresholdsVO.getSameCardSameAmountThreshold()%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="resume_processing_alert"  value="<%=terminalThresholdsVO.getResumeProcessingAlert()%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="daily_avgticket_percentage_threshold"  value="<%=Functions.round(terminalThresholdsVO.getDailyAvgTicketPercentageThreshold(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="daily_cb_ratio_suspension"  value="<%=Functions.round(terminalThresholdsVO.getDailyCBRatioSuspension(),2)%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="daily_cb_amount_ratio_suspension"  value="<%=Functions.round(terminalThresholdsVO.getDailyCBAmountRatioSuspension(),2)%>"></td>
            </tr>
            <tr>
                <td class="textb" valign="middle" align="center">SameCard SameAmount Consequence Threshold</td>
                <td class="textb" valign="middle" align="center">SameCard Consequently Threshold</td>
            </tr>>
            <tr>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="samecard_sameamount_consequence_threshold"  value="<%=terminalThresholdsVO.getSameCardSameAmountConsequenceThreshold()%>"></td>
                <td align="center"><input type=text class="txtboxsmall" size=10 name="samecard_consequently_threshold"  value="<%=terminalThresholdsVO.getSameCardConsequentlyThreshold()%>"></td>
            </tr>
            <tr>
                <td colspan="6" align="center"><input id="submit" type="Submit" value="submit" name="submit" class="buttonform" onclick="return confirmsubmit2(<%=terminalVO.getTerminalId()%>)"></td>
            </tr>
        </table>
    </form>
    <%
        }
    %>
    <%
        }
        else
        {
            out.println(Functions.NewShowConfirmation("Sorry","No Records Found."));
        }
    %>
</div>
<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</body>
</html>