<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.vo.merchantmonitoring.MonitoringAlertDetailVO" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Jitendra
  Date: 09/02/2018
  Time: 2:00 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
   // session.setAttribute("submit","partnerMonitoringRuleLog");
    session.setAttribute("submit","Monitoring Rule Logs");
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if(partner.isLoggedInPartner(session))
    {
%>
<html>
<head>

    <title><%=company%>Monitoring Rule Log</title>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <meta name="viewport" content="width=device-width, initial-scale=0, maximum-scale=0, user-scalable=no">
    <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
</head>
<body>
<div class="content-page">
    <div class="content">
        <div class="pull-right">
            <div class="btn-group">
                <form action="/partner/net/PartnerMonitoringRuleLog?ctoken=<%=ctoken%>" method="post" name="form1">
                    <input type="hidden" name="fromdate" value=<%=request.getParameter("fromdate")%>>
                    <input type="hidden" name="todate" value=<%=request.getParameter("todate")%>>
                    <input type="hidden" name="memberid" value=<%=request.getParameter("memberid")%>>
                    <input type="hidden" name="ruleid" value="<%=request.getParameter("ruleid")%>">
                    <input type="hidden" name="terminalid" value=<%=request.getParameter("terminalid")%>>
                        <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
                        <img style="height: 35px;" src="/partner/images/goBack.png">
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>
<%
    MonitoringAlertDetailVO monitoringAlertDetailVO= (MonitoringAlertDetailVO) request.getAttribute("details");
    Map<String,List<MonitoringAlertDetailVO>> stringListMap= (Map<String,List<MonitoringAlertDetailVO>>) request.getAttribute("stringListMap");
    String style = "class=tr0";

    //System.out.println("from date:::::"+request.getParameter("fromdate"));
    //System.out.println("to date:::::"+request.getParameter("todate"));
    //System.out.println("memberid:::::"+request.getParameter("memberid"));
    //System.out.println("terminal id:::::"+request.getParameter("terminalid"));
    //System.out.println("rule id:::::"+request.getParameter("ruleid"));
    //System.out.println("alert id:::::"+request.getParameter("alertid"));
    //System.out.println("action:::::"+request.getParameter("action"));
%>
<%--Start Report Data--%>
<div class="content-page">
    <div class="content">
        <div class="widget">
            <div class="widget-header">
                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Report Table</strong></h2>
                <div class="additional-btn">
                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
            </div>
            <div class="widget-content padding" style="overflow-x: auto;">
                <table id="AlertDetails" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                    <thead>
                    <tr>
                        <td  colspan="5" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Monitoring Alert Detail</b>
                            <form action="/partner/net/PartnerMerchantResendLog?ctoken=<%=ctoken%>" method="post">
                                <input type="hidden" name="action" value="sendmail"><input type="hidden" name="alertid" value=<%=monitoringAlertDetailVO.getAlertId()%>> <input type="submit" name="submit" style="float: right;" value="NotifyAgain" class="button btn btn-default">
                            </form>
                        </td>
                    </tr>
                    </thead>
                    <tr>
                        <td colspan="3" valign="middle" align="left" >AlertId:</td>
                        <td colspan="2" valign="middle" align="left"> <%=monitoringAlertDetailVO.getAlertId()%></td>
                    </tr>
                    <tr>
                        <td colspan="3" valign="middle" align="left" >Alert Type:</td>
                        <td colspan="2" valign="middle" align="left" > <%=monitoringAlertDetailVO.getAlertType()%></td>
                    </tr>
                    <tr>
                        <td colspan="3" valign="middle" align="left" >Alert Team:</td>
                        <td colspan="2" valign="middle" align="left"> <%=monitoringAlertDetailVO.getAlertTeam()%></td>
                    </tr>
                    <tr>
                        <td colspan="3" valign="middle" align="left" >MemberId:</td>
                        <td colspan="2" valign="middle" align="left"> <%=monitoringAlertDetailVO.getMemberId()%></td>
                    </tr>
                    <tr>
                        <td colspan="3" valign="middle" align="left" >Alert Date:</td>
                        <td colspan="2" valign="middle" align="left"><%=monitoringAlertDetailVO.getAlertDate()%></td>
                    </tr>
                </table>
                <table id="RuleDetails" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                    <%
                        Set terminalSet=stringListMap.keySet();
                        Iterator iterator=terminalSet.iterator();
                        while (iterator.hasNext()){
                            String terminalId=(String)iterator.next();
                            List<MonitoringAlertDetailVO> monitoringAlertDetailVOsList=stringListMap.get(terminalId);
                            if(monitoringAlertDetailVOsList.size()>0){
                    %>
                    <thead>
                    <tr>
                        <td colspan="7" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 5px;padding-bottom: 5px;"><b>TerminalId-<%=terminalId%></b>
                            <form action="/partner/net/PartnerMerchantResendLog?ctoken=<%=ctoken%>" method="post">
                                <input type="hidden" name="action" value="suspendaccount"><input type="hidden" name="alertid" value=<%=monitoringAlertDetailVO.getAlertId()%>><input type="hidden" name="terminalid" value=<%=terminalId%>> <input type="submit" name="submit" style="float: right;" value="Suspend" <%--onclick="return confirmsubmit1(<%=terminalId%>)"--%> class="button btn btn-default">
                            </form>
                        </td>
                    </tr>
                    <tr>
                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Id</b></td>
                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>RuleName</b></td>
                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>ActualRatio</b></td>
                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>AlertThreshold</b></td>
                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>SuspensionThreshold</b></td>
                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>RuleMessage</b></td>
                    </tr>
                    </thead>
                    <%
                                int i=1;
                                for (MonitoringAlertDetailVO monitoringAlertDetailVO3:monitoringAlertDetailVOsList){
                                    out.println("<tr " + style + ">");
                                    out.println("<td valign=\"middle\" data-label=\"MemberId\" align=\"center\"" + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(String.valueOf(i)) + "</td>");
                                    out.println("<td valign=\"middle\" data-label=\"MemberId\" align=\"center\"" + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(monitoringAlertDetailVO3.getMonitoringAlertName()) + "</td>");
                                    out.println("<td valign=\"middle\" data-label=\"MemberId\" align=\"center\"" + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(String.valueOf(monitoringAlertDetailVO3.getActualratio())) + "</td>");
                                    out.println("<td valign=\"middle\" data-label=\"MemberId\" align=\"center\"" + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(String.valueOf(monitoringAlertDetailVO3.getAlertThreshold())) + "</td>");
                                    out.println("<td valign=\"middle\" data-label=\"MemberId\" align=\"center\"" + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(String.valueOf(monitoringAlertDetailVO3.getSuspensionThreshold())) + "</td>");
                                    out.println("<td valign=\"middle\" data-label=\"MemberId\" align=\"center\"" + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(String.valueOf(monitoringAlertDetailVO3.getAlertMsg())) + "</td>");
                                    out.println("</tr>");
                                    i=i+1;
                                }
                            }
                        }
                    %>
                </table>
            </div>
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
</body>
</html>