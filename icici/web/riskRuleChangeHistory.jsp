<%@ page import="com.manager.vo.merchantmonitoring.MonitoringRuleLogVO" %>
<%@ page import="com.manager.vo.merchantmonitoring.MonitoringParameterMappingVO" %>
<%@ page import="com.directi.pg.Functions" %>
<%--
  Created by IntelliJ IDEA.
  User: Sandip
  Date: 30/7/16
  Time: 9:17 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Risk Rule Change History</title>
</head>
<body>
<%!
  public String checkData(Double isValidAmount)
  {
    if(isValidAmount!=null && isValidAmount>0)
    {
      return String.valueOf(isValidAmount);
    }
    else
    {
      return "-";
    }
  }
%>
<%
  MonitoringRuleLogVO monitoringRuleLogVO=(MonitoringRuleLogVO)request.getAttribute("monitoringRuleLogVO");
  MonitoringRuleLogVO monitoringRuleLogVO1=(MonitoringRuleLogVO)request.getAttribute("monitoringRuleLogVO1");
  MonitoringParameterMappingVO monitoringParameterMappingVONew=monitoringRuleLogVO.getMonitoringParameterMappingVO();
  MonitoringParameterMappingVO monitoringParameterMappingVOOld=monitoringRuleLogVO1.getMonitoringParameterMappingVO();

  //System.out.println("monitoringParameterMappingVONew======="+monitoringParameterMappingVONew);
  //System.out.println("monitoringParameterMappingVOOld======="+monitoringParameterMappingVOOld);
  if(monitoringRuleLogVO==null)
  {
%>
<h1> Risk Rule Change History</h1>
<center> <font color=red size=2> Sorry ,Data Not Available!</font></center>
<%
}
else
{
%>
<h1 align=center>Risk Rule Change History</h1>
<table align=center border="1" bordercolorlight="#000000" width="70%">
  <tr>
    <td colspan=6 bgcolor="#CC9966" align=center><b>Frequency</b></td>
  </tr>
  <tr bgcolor="#CCCCCC">
    <th>Value</th>
    <th>Daily</th>
    <th>Weekly</th>
    <th colspan="6">Monthly</th>
  </tr>
  <tr bgcolor="CCCCCC">
    <td align="center"><%="Old"%></td>
    <td align="center"><%=monitoringParameterMappingVOOld.getIsDailyExecution()%></td>
    <td align="center"><%=monitoringParameterMappingVOOld.getIsWeeklyExecution()%></td>
    <td align="center" colspan="6"><%=monitoringParameterMappingVOOld.getIsMonthlyExecution()%></td>
  </tr>
  <tr bgcolor="CCCCCC">
      <td align="center"><%="New"%></td>
      <td align="center"><%=monitoringParameterMappingVONew.getIsDailyExecution()%></td>
      <td align="center"><%=monitoringParameterMappingVONew.getIsWeeklyExecution()%></td>
      <td align="center" colspan="6"><%=monitoringParameterMappingVONew.getIsMonthlyExecution()%></td>
  </tr>
  </table>
<td>&nbsp;</td>
<table align=center border="1" bordercolorlight="#000000" width="70%">
  <tr>
      <td colspan=6 bgcolor="#CC9966" align=center><b>Alert Settings</b></td>
  </tr>
  <tr bgcolor="#CCCCCC">
      <th>Value</th>
      <th>Activation</th>
      <th>Daily Threshold</th>
      <th >Weekly Threshold</th>
      <th colspan="6">Monthly Threshold</th>
  </tr>
    <tr bgcolor="#CCCCCC">
    <td align="center"><%="Old"%></td>
    <td align="center"><%=monitoringParameterMappingVONew.getAlertActivation()%></td>
      <td align="center"><%=checkData(monitoringParameterMappingVOOld.getAlertThreshold())%></td>
      <td align="center"><%=checkData(monitoringParameterMappingVOOld.getWeeklyAlertThreshold())%></td>
      <td align="center" colspan="6"><%=checkData(monitoringParameterMappingVOOld.getMonthlyAlertThreshold())%></td>
    </tr>
    <tr bgcolor="#CCCCCC">
      <td align="center"><%="New"%></td>
      <td align="center"><%=monitoringParameterMappingVONew.getAlertActivation()%></td>
      <td align="center"><%=checkData(monitoringParameterMappingVONew.getAlertThreshold())%></td>
      <td align="center"><%=checkData(monitoringParameterMappingVONew.getWeeklyAlertThreshold())%></td>
      <td align="center" colspan="6"><%=checkData(monitoringParameterMappingVONew.getMonthlyAlertThreshold())%></td>
    </tr>
  </table>
  <td>&nbsp;</td>
  <table align=center border="1" bordercolorlight="#000000" width="70%">
    <tr>
      <td colspan=6 bgcolor="#CC9966" align=center><b>Suspension Settings</b></td>
    </tr>
    <tr bgcolor="#CCCCCC">
      <th>Value</th>
      <th>Activation</th>
      <th>Daily Threshold</th>
      <th>Weekly Threshold</th>
      <th colspan="6">Monthly Threshold</th>
    </tr>
    <tr bgcolor="#CCCCCC">
      <td align="center"><%="Old"%></td>
      <td align="center"><%=monitoringParameterMappingVOOld.getSuspensionActivation()%></td>
      <td align="center"><%=checkData(monitoringParameterMappingVOOld.getSuspensionThreshold())%></td>
      <td align="center"><%=checkData(monitoringParameterMappingVOOld.getWeeklySuspensionThreshold())%></td>
      <td align="center" colspan="6"><%=checkData(monitoringParameterMappingVOOld.getMonthlySuspensionThreshold())%></td>
    </tr>
    <tr bgcolor="#CCCCCC">
      <td align="center"><%="New"%></td>
      <td align="center"><%=monitoringParameterMappingVONew.getSuspensionActivation()%></td>
      <td align="center"><%=checkData(monitoringParameterMappingVONew.getSuspensionThreshold())%></td>
      <td align="center"><%=checkData(monitoringParameterMappingVONew.getWeeklySuspensionThreshold())%></td>
      <td align="center" colspan="6"><%=checkData(monitoringParameterMappingVONew.getMonthlySuspensionThreshold())%></td>
    </tr>
    </table>
    <td>&nbsp;</td>
    <table align=center border="1" bordercolorlight="#000000" width="70%">
    <tr>
      <td colspan=6 bgcolor="#CC9966" align=center><b>Email Configuration</b></td>
    </tr>
    <tr bgcolor="#CCCCCC">
      <th>Team</th>
      <th>Value</th>
      <th>Admin</th>
      <th>PSP</th>
      <th>Merchant</th>
      <th>Agent</th>
    </tr>
    <tr bgcolor="#CCCCCC">
      <th rowspan="2">Sales</th>
      <th>Old</th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToAdminSales()%></th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToPartnerSales()%></th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToMerchantSales()%></th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToAgentSales()%></th>
    </tr>
    <tr bgcolor="#CCCCCC">
      <th>New</th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToAdminSales()%></th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToPartnerSales()%></th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToMerchantSales()%></th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToAgentSales()%></th>
    </tr>

    <tr bgcolor="#CCCCCC">
      <th rowspan="2">CB</th>
      <th>Old</th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToAdminCB()%></th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToPartnerCB()%></th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToMerchantCB()%></th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToAgentCB()%></th>
    </tr>
    <tr bgcolor="#CCCCCC">
      <th>New</th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToAdminCB()%></th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToPartnerCB()%></th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToMerchantCB()%></th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToAgentCB()%></th>
    </tr>

    <tr bgcolor="#CCCCCC">
      <th rowspan="2">RF</th>
      <th>Old</th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToAdminRF()%></th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToPartnerRF()%></th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToMerchantRF()%></th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToAgentRF()%></th>
    </tr>
    <tr bgcolor="#CCCCCC">
      <th>New</th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToAdminRF()%></th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToPartnerRF()%></th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToMerchantRF()%></th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToAgentRF()%></th>
    </tr>

    <tr bgcolor="#CCCCCC">
      <th rowspan="2">Fraud</th>
      <th>Old</th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToAdminFraud()%></th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToPartnerFraud()%></th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToMerchantFraud()%></th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToAgentFraud()%></th>
    </tr>
    <tr bgcolor="#CCCCCC">
      <th>New</th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToAdminFraud()%></th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToPartnerFraud()%></th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToMerchantFraud()%></th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToAgentFraud()%></th>
    </tr>

    <tr bgcolor="#CCCCCC">
      <th rowspan="2">Tech</th>
      <th>Old</th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToAdminTech()%></th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToPartnerTech()%></th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToMerchantTech()%></th>
      <th><%=monitoringParameterMappingVOOld.getIsAlertToAgentTech()%></th>
    </tr>
    <tr bgcolor="#CCCCCC">
      <th>New</th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToAdminTech()%></th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToPartnerTech()%></th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToMerchantTech()%></th>
      <th><%=monitoringParameterMappingVONew.getIsAlertToAgentTech()%></th>
    </tr>
</table>
<%
 }
%>
</body>
</html>