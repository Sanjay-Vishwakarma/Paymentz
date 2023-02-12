<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.merchantmonitoring.MonitoringParameterVO" %>
<%@ page import="com.manager.vo.merchantmonitoring.enums.*" %>
<%--
  Created by IntelliJ IDEA.
  User: Supriya
  Date: 1/19/15
  Time: 7:32 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="index.jsp"%>
<%!
  Functions functions = new Functions();
%>
<html>
<head>
  <style>
    .multiselect {
      width: 100px;
    }
    .selectBox {
      position: relative;
    }
    .selectBox select {
      width: 100%;
      font-weight: bold;
    }
    .overSelect {
      position: absolute;
      left: 0; right: 0; top: 0; bottom: 0;
    }
    .checkboxes {
      display: none;
      border: 1px #dadada solid;
    }
    .checkboxes label {
      display: block;
    }
    .checkboxes label:hover {
      background-color: #1e90ff;
    }
  </style>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title></title>
  <script language="javascript">
    function doChangesForAlertActivation(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultAlertActivation').value = "Y";
      }
      else
      {
        document.getElementById('defaultAlertActivation').value = "N";
      }
      document.getElementById('defaultAlertActivationValue').value = document.getElementById('defaultAlertActivation').value;
    }
    function doChangesForSuspensionActivation(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultSuspensionActivation').value = "Y";
      }
      else
      {
        document.getElementById('defaultSuspensionActivation').value = "N";
      }
      document.getElementById('defaultSuspensionActivationValue').value = document.getElementById('defaultSuspensionActivation').value;
    }
    function doChangesForIsAlertToAdmin(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToAdminValue').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToAdminValue').value = "N";
      }
      document.getElementById('defaultIsAlertToAdminValue').value = document.getElementById('defaultIsAlertToAdminValue').value;
    }
    function doChangesForIsAlertToAdminSales(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToAdminSales').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToAdminSales').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToAdminSalesValue').value = document.getElementById('defaultIsAlertToAdminSales').value;
    }
    function doChangesForIsAlertToAdminRF(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToAdminRF').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToAdminRF').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToAdminRFValue').value = document.getElementById('defaultIsAlertToAdminRF').value;
    }
    function doChangesForIsAlertToAdminCB(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToAdminCB').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToAdminCB').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToAdminCBValue').value = document.getElementById('defaultIsAlertToAdminCB').value;
    }
    function doChangesForIsAlertToAdminFraud(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToAdminFraud').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToAdminFraud').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToAdminFraudValue').value = document.getElementById('defaultIsAlertToAdminFraud').value;
    }
    function doChangesForIsAlertToAdminTech(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToAdminTech').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToAdminTech').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToAdminTechValue').value = document.getElementById('defaultIsAlertToAdminTech').value;
    }
    function doChangesForIsAlertToMerchant(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToMerchantValue').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToMerchantValue').value = "N";
      }
      document.getElementById('defaultIsAlertToMerchantValue').value = document.getElementById('defaultIsAlertToMerchantValue').value;
    }
    function doChangesForIsAlertToMerchantSales(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToMerchantSales').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToMerchantSales').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToMerchantSalesValue').value = document.getElementById('defaultIsAlertToMerchantSales').value;
    }
    function doChangesForIsAlertToMerchantRF(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToMerchantRF').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToMerchantRF').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToMerchantRFValue').value = document.getElementById('defaultIsAlertToMerchantRF').value;
    }
    function doChangesForIsAlertToMerchantCB(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToMerchantCB').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToMerchantCB').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToMerchantCBValue').value = document.getElementById('defaultIsAlertToMerchantCB').value;
    }
    function doChangesForIsAlertToMerchantFraud(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToMerchantFraud').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToMerchantFraud').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToMerchantFraudValue').value = document.getElementById('defaultIsAlertToMerchantFraud').value;
    }
    function doChangesForIsAlertToMerchantTech(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToMerchantTech').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToMerchantTech').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToMerchantTechValue').value = document.getElementById('defaultIsAlertToMerchantTech').value;
    }
    function doChangesForIsAlertToPartner(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToPartnerValue').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToPartnerValue').value = "N";
      }
      document.getElementById('defaultIsAlertToPartnerValue').value = document.getElementById('defaultIsAlertToPartnerValue').value;
    }
    function doChangesForIsAlertToPartnerSales(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToPartnerSales').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToPartnerSales').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToPartnerSalesValue').value = document.getElementById('defaultIsAlertToPartnerSales').value;
    }
    function doChangesForIsAlertToPartnerRF(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToPartnerRF').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToPartnerRF').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToPartnerRFValue').value = document.getElementById('defaultIsAlertToPartnerRF').value;
    }
    function doChangesForIsAlertToPartnerCB(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToPartnerCB').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToPartnerCB').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToPartnerCBValue').value = document.getElementById('defaultIsAlertToPartnerCB').value;
    }
    function doChangesForIsAlertToPartnerFraud(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToPartnerFraud').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToPartnerFraud').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToPartnerFraudValue').value = document.getElementById('defaultIsAlertToPartnerFraud').value;
    }
    function doChangesForIsAlertToPartnerTech(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToPartnerTech').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToPartnerTech').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToPartnerTechValue').value = document.getElementById('defaultIsAlertToPartnerTech').value;
    }
    function doChangesForIsAlertToAgent(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToAgentValue').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToAgentValue').value = "N";
      }
      document.getElementById('defaultIsAlertToAgentValue').value = document.getElementById('defaultIsAlertToAgentValue').value;
    }
    function doChangesForIsAlertToAgentSales(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToAgentSales').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToAgentSales').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToAgentSalesValue').value = document.getElementById('defaultIsAlertToAgentSales').value;
    }
    function doChangesForIsAlertToAgentRF(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToAgentRF').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToAgentRF').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToAgentRFValue').value = document.getElementById('defaultIsAlertToAgentRF').value;
    }
    function doChangesForIsAlertToAgentCB(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToAgentCB').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToAgentCB').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToAgentCBValue').value = document.getElementById('defaultIsAlertToAgentCB').value;
    }
    function doChangesForIsAlertToAgentFraud(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToAgentFraud').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToAgentFraud').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToAgentFraudValue').value = document.getElementById('defaultIsAlertToAgentFraud').value;
    }
    function doChangesForIsAlertToAgentTech(data)
    {
      if(data.checked)
      {
        document.getElementById('defaultIsAlertToAgentTech').value = "Y";
      }
      else
      {
        document.getElementById('defaultIsAlertToAgentTech').value = "N";
      }
      var value=document.getElementById('defaultIsAlertToAgentTechValue').value = document.getElementById('defaultIsAlertToAgentTech').value;
    }
  </script>
  <script>
    var expanded = false;
    function showCheckboxesAdmin()
    {
      var checkboxes = document.getElementById('checkboxesadmin');
      if (!expanded) {
        checkboxes.style.display = "block";
        expanded = true;
      } else {
        checkboxes.style.display = "none";
        expanded = false;
      }
    }
    function showCheckboxesMerchant()
    {
      var checkboxes = document.getElementById('checkboxesmerchant');
      if (!expanded) {
        checkboxes.style.display = "block";
        expanded = true;
      } else {
        checkboxes.style.display = "none";
        expanded = false;
      }
    }
    function showCheckboxesPartner()
    {
      var checkboxes = document.getElementById('checkboxespartner');
      if (!expanded)
      {
        checkboxes.style.display = "block";
        expanded = true;
      }
      else
      {
        checkboxes.style.display = "none";
        expanded = false;
      }
    }
    function showCheckboxesAgent()
    {
      var checkboxes = document.getElementById('checkboxesagent');
      if (!expanded)
      {
        checkboxes.style.display = "block";
        expanded = true;
      }
      else
      {
        checkboxes.style.display = "none";
        expanded = false;
      }
    }
    function doChangeFrequency(data)
    {
      var value=confirm("Do you really want to change selected frequency?")
      if(value)
      {
        document.getElementById('monitoingFrequencyActualValue').value=data.value;
      }
      else
      {
        data.value=document.getElementById('monitoingFrequencyActualValue').value;
      }
    }
    function showCheckboxesFrequency()
    {
      var checkbox = document.getElementById("checkboxefrequency")
      if(!expanded)
      {
        checkbox.style.display = "block";
        expanded = true;
      }
      else
      {
        checkbox.style.display = "none";
        expanded = false;
      }
    }
    function doChangeForDailyExecution(data)
    {
      if(data.checked)
      {
        document.getElementById('isdailyexecution').value = "Y";
      }
      else
      {
        document.getElementById('isdailyexecution').value = "N";
      }
      var value=document.getElementById('isdailyexecutionvalue').value = document.getElementById('isdailyexecution').value;
    }
    function doChangeForWeeklyExecution(data)
    {
      if(data.checked)
      {
        document.getElementById('isweeklyexecution').value = "Y";
      }
      else
      {
        document.getElementById('isweeklyexecution').value = "N";
      }
      var value=document.getElementById('isweeklyexecutionvalue').value = document.getElementById('isweeklyexecution').value;
    }
    function doChangeForMonthlyExecution(data)
    {
      if(data.checked)
      {
        document.getElementById('ismonthlyexecution').value = "Y";
      }
      else
      {
        document.getElementById('ismonthlyexecution').value = "N";
      }
      var value=document.getElementById('ismonthlyexecutionvalue').value = document.getElementById('ismonthlyexecution').value;
    }
    function dailyEnableDisableTextBox(data)
    {
      if(data.checked)
      {
        document.getElementsByName("defaultAlertThreshold")[0].disabled = false;
        document.getElementsByName("defaultSuspensionThreshold")[0].disabled = false;
      }
      else
      {
        document.getElementsByName("defaultAlertThreshold")[0].disabled = true;
        document.getElementsByName("defaultSuspensionThreshold")[0].disabled = true;
      }
    }
    function weeklyEnablesDisableTextBox(data)
    {
      if (data.checked)
      {
        document.getElementsByName("weeklyAlertThreshold")[0].disabled = false;
        document.getElementsByName("weeklySuspensionThreshold")[0].disabled = false;
      }
      else
      {
        document.getElementsByName("weeklyAlertThreshold")[0].disabled = true;
        document.getElementsByName("weeklySuspensionThreshold")[0].disabled = true;
      }
    }
    function monthlyEnableDisableTextBox(data)
    {
      if (data.checked)
      {
        document.getElementsByName("monthlyAlertThreshold")[0].disabled = false;
        document.getElementsByName("monthlySuspensionThreshold")[0].disabled = false;
      }
      else
      {
        document.getElementsByName("monthlyAlertThreshold")[0].disabled = true;
        document.getElementsByName("monthlySuspensionThreshold")[0].disabled = true;
      }
    }
  </script>
</head>
<body>
  <%
  MonitoringParameterVO monitoringParameterVO=(MonitoringParameterVO)request.getAttribute("monitoringParameterVO");
  //System.out.println("monitoringParameterVO===="+monitoringParameterVO);
  if(monitoringParameterVO!=null)
  {
     String alertThreshold = "0";
     String suspensionThreshold = "0";
     String weeklyAlertThreshold = "0";
     String weeklySuspensionThreshold = "0";
     String monthlyAlertThreshold = "0";
     String monthlySuspensionThreshold = "0";
   if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()) || MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword()))
   {
            alertThreshold = Functions.round(monitoringParameterVO.getDefaultAlertThreshold(), 2);
            suspensionThreshold = Functions.round(monitoringParameterVO.getDefaultSuspensionThreshold(), 2);
            weeklyAlertThreshold = Functions.round(monitoringParameterVO.getWeeklyAlertThreshold(), 2);
            weeklySuspensionThreshold = Functions.round(monitoringParameterVO.getWeeklySuspensionThreshold(), 2);
            monthlyAlertThreshold = Functions.round(monitoringParameterVO.getMonthlyAlertThreshold(), 2);
            monthlySuspensionThreshold = Functions.round(monitoringParameterVO.getMonthlySuspensionThreshold(), 2);
           /* if (Double.parseDouble(alertThreshold) <= 0)
            {
              alertThreshold = "";
            }
            if (Double.parseDouble(suspensionThreshold) <= 0)
            {
              suspensionThreshold = "";
            }
            if(Double.parseDouble(weeklyAlertThreshold) <= 0)
            {
              weeklyAlertThreshold = "";
            }
            if(Double.parseDouble(weeklySuspensionThreshold) <= 0)
            {
              weeklySuspensionThreshold = "";
            }
            if(Double.parseDouble(monthlyAlertThreshold) <= 0)
            {
              monthlyAlertThreshold = "";
            }
            if(Double.parseDouble(monthlySuspensionThreshold) <= 0)
            {
              monthlySuspensionThreshold = "";
            }*/
          }
          else
          {
            alertThreshold = Functions.round(monitoringParameterVO.getDefaultAlertThreshold(), 0);
            suspensionThreshold = Functions.round(monitoringParameterVO.getDefaultSuspensionThreshold(), 0);
            weeklyAlertThreshold = Functions.round(monitoringParameterVO.getWeeklyAlertThreshold(), 0);
            weeklySuspensionThreshold = Functions.round(monitoringParameterVO.getWeeklySuspensionThreshold(), 0);
            monthlyAlertThreshold = Functions.round(monitoringParameterVO.getMonthlyAlertThreshold(), 0);
            monthlySuspensionThreshold = Functions.round(monitoringParameterVO.getMonthlySuspensionThreshold(), 0);

            /*if (Integer.parseInt(alertThreshold) <= 0)
            {
              alertThreshold = "";
            }
            if (Integer.parseInt(suspensionThreshold) <= 0)
            {
              suspensionThreshold = "";
            }
            if(Integer.parseInt(weeklyAlertThreshold) <= 0)
            {
              weeklyAlertThreshold = "";
            }
            if(Integer.parseInt(weeklySuspensionThreshold) <= 0)
            {
              weeklySuspensionThreshold = "";
            }
            if(Integer.parseInt(monthlyAlertThreshold) <= 0)
            {
              monthlyAlertThreshold = "";
            }
            if(Integer.parseInt(monthlySuspensionThreshold) <= 0)
            {
              monthlySuspensionThreshold = "";
            }*/
          }
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Update Risk Rule Master
        <div style="float: right;">
          <form action="/icici/monitoringParameterMaster.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Charge Master" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Risk Rule Master
            </button>
          </form>
        </div>
      </div>
      <form action="/icici/servlet/UpdateMonitoringParameterMaster?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <input type="hidden" value="<%=monitoringParameterVO.getMonitoringParameterId()%>" name="monitoingParaId">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <%
            if(request.getParameter("MES")!=null)
            {
              String mes=request.getParameter("MES");
              if(mes.equals("ERR"))
              {
                String error=(String)request.getAttribute("error");
                //System.out.println("Error===="+error);
                out.println("<center><font class=\"textb\">" +error + "</font></center>");
              }
            }
          %>
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr><td colspan="5">&nbsp;</td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb">Risk Rule Name*</td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb" colspan="2">
                    <input maxlength="255" type="text" name="monitoingParaName" class="txtbox"
                           value="<%=monitoringParameterVO.getMonitoringParameterName()%>">
                    <input maxlength="50" type="hidden" name="orgMonitoingParaName" class="txtbox" value="<%=monitoringParameterVO.getMonitoringParameterName()%>">
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Risk Rule Tech Name*</td>
                  <td class="textb">:</td>
                  <td width="50%" class="textb" colspan="2">
                    <input maxlength="255" type="text" name="monitoingParaTechName" class="txtbox"
                           value="<%=monitoringParameterVO.getMonitoingParaTechName()%>">
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Monitoring Unit*</td>
                  <td class="textb">:</td>
                  <td colspan="2">
                    <select name="monitoringUnit" class="txtbox">
                      <option value=""></option>
                      <%
                        String unit = monitoringParameterVO.getMonitoringUnit();
                        for (MonitoringUnit monitoringUnit: MonitoringUnit.values()){
                          String tx1="";
                          if(monitoringUnit.name().equals(unit))
                          {
                            tx1="selected";
                          }
                      %>
                      <option value=<%=monitoringUnit.name()%> <%=tx1%>><%=monitoringUnit.name()%></option>
                      <%
                        }
                      %>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Monitoring Category*</td>
                  <td class="textb">:</td>
                  <td colspan="2">
                    <select name="monitoingCategory" class="txtbox">
                      <option value=""></option>
                      <%
                        String category=monitoringParameterVO.getMonitoingCategory();
                        for (MonitoringCategory monitoringCategory: MonitoringCategory.values()){
                          String tx1="";
                          if(monitoringCategory.name().equals(category))
                          {
                            tx1="selected";
                          }
                      %>
                      <option value=<%=monitoringCategory.name()%> <%=tx1%>><%=monitoringCategory.name()%></option>
                      <%
                        }
                      %>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Monitoring Deviation*</td>
                  <td class="textb">:</td>
                  <td colspan="2">
                    <select name="monitoingDeviation" class="txtbox">
                      <option value=""></option>
                      <%
                        String deviation=monitoringParameterVO.getMonitoingDeviation();
                        for (MonitoringDeviation monitoringDeviation: MonitoringDeviation.values()){
                          String tx1="";
                          if(monitoringDeviation.name().equals(deviation))
                          {
                            tx1="selected";
                          }
                      %>
                      <option value=<%=monitoringDeviation.name()%> <%=tx1%>><%=monitoringDeviation.name()%></option>
                      <%
                        }
                      %>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Monitoring Keyword*</td>
                  <td class="textb">:</td>
                  <td colspan="2">
                    <select name="monitoingKeyword" class="txtbox">
                      <option value=""></option>
                      <%
                        String keyword=monitoringParameterVO.getMonitoingKeyword();
                        for (MonitoringKeyword monitoringKeyword: MonitoringKeyword.values()){
                          String tx1="";
                          if(monitoringKeyword.name().equals(keyword))
                          {
                            tx1="selected";
                          }
                      %>
                      <option value=<%=monitoringKeyword.name()%> <%=tx1%>><%=monitoringKeyword.name()%></option>
                      <%
                        }
                      %>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Monitoring Sub Keyword*</td>
                  <td class="textb">:</td>
                  <td colspan="2">
                    <select name="monitoingSubKeyword" class="txtbox">
                      <option value=""></option>
                      <%
                        String subKeyword=monitoringParameterVO.getMonitoingSubKeyword();
                        for (MonitoringSubKeyword monitoringSubKeyword: MonitoringSubKeyword.values()){
                          String tx1="";
                          if(monitoringSubKeyword.name().equals(subKeyword))
                          {
                            tx1="selected";
                          }
                      %>
                      <option value=<%=monitoringSubKeyword.name()%> <%=tx1%>><%=monitoringSubKeyword.name()%></option>
                      <%
                        }
                      %>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Monitoring Alert Category*</td>
                  <td class="textb">:</td>
                  <td colspan="2">
                    <select name="monitoingAlertCategory" class="txtbox">
                      <option value=""></option>
                      <%
                        String alertType=monitoringParameterVO.getMonitoingAlertCategory();
                        for (MonitoringAlertType monitoringAlertType: MonitoringAlertType.values()){
                          String tx1="";
                          if(monitoringAlertType.name().equals(alertType))
                          {
                            tx1="selected";
                          }
                      %>
                      <option value=<%=monitoringAlertType.name()%> <%=tx1%>><%=monitoringAlertType.name()%></option>
                      <%
                        }
                      %>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Monitoring Channel*</td>
                  <td class="textb">:</td>
                  <td colspan="2">
                    <select name="monitoingOnChannel" class="txtbox">
                      <option value=""></option>
                      <%
                        String channelLevel=monitoringParameterVO.getMonitoingOnChannel();
                        for (MonitoringChannelLevel monitoringChannelLevel: MonitoringChannelLevel.values()){
                          String tx1="";
                          if(monitoringChannelLevel.name().equals(channelLevel))
                          {
                            tx1="selected";
                          }
                      %>
                      <option value=<%=monitoringChannelLevel.name()%> <%=tx1%>><%=monitoringChannelLevel.name()%></option>
                      <%
                        }
                      %>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Display Chart Type*</td>
                  <td class="textb">:</td>
                  <td colspan="2">
                    <select name="displayChartType" class="txtbox">
                      <%
                        String displayChartType = monitoringParameterVO.getDisplayChartType();
                        if("LineChart".equals(displayChartType))
                        {
                      %>
                      <option value="LineChart">LineChart</option>
                      <option value="BarChart">BarChart</option>
                      <option value="DoughnutChart">DoughnutChart</option>
                      <option value="ProgressBarChart">ProgressBarChart</option>
                      <%
                        }
                        else if("BarChart".equals(displayChartType))
                        {
                      %>
                      <option value="BarChart">BarChart</option>
                      <option value="LineChart">LineChart</option>
                      <option value="DoughnutChart">DoughnutChart</option>
                      <option value="ProgressBarChart">ProgressBarChart</option>
                      <%
                      }
                      else if("DoughnutChart".equals(displayChartType))
                      {
                      %>
                      <option value="DoughnutChart">DoughnutChart</option>
                      <option value="LineChart">LineChart</option>
                      <option value="BarChart">BarChart</option>
                      <option value="ProgressBarChart">ProgressBarChart</option>
                      <%
                      }
                      else if("ProgressBarChart".equals(displayChartType))
                      {
                      %>
                      <option value="ProgressBarChart">ProgressBarChart</option>
                      <option value="LineChart">LineChart</option>
                      <option value="BarChart">BarChart</option>
                      <option value="DoughnutChart">DoughnutChart</option>
                      <%
                        }
                        else
                        {
                      %>
                      <option value=""></option>
                      <option value="LineChart">LineChart</option>
                      <option value="BarChart">BarChart</option>
                      <option value="DoughnutChart">DoughnutChart</option>
                      <option value="ProgressBarChart">ProgressBarChart</option>\
                      <%
                        }
                      %>
                    </select>
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Monitoring Frequency*</td>
                  <td class="textb">
                    <%
                      String dailyThresholdVisibility="disabled";
                      String weeklyThresholdVisibility="disabled";
                      String monthlyThresholdVisibility="disabled";

                      String extDailyExecution="";
                      if("Y".equals(monitoringParameterVO.getIsDailyExecution()))
                      {
                        extDailyExecution="checked";
                        dailyThresholdVisibility="";
                      }
                      String extWeeklyExecution="";
                      if("Y".equals(monitoringParameterVO.getIsWeeklyExecution()))
                      {
                        extWeeklyExecution="checked";
                        weeklyThresholdVisibility="";
                      }
                      String extMonthlyExecution="";
                      if("Y".equals(monitoringParameterVO.getIsMonthlyExecution()))
                      {
                        extMonthlyExecution="checked";
                        monthlyThresholdVisibility="";
                      }
                    %>
                    :</td>
                  <td colspan="2">
                    <div class="multiselect" style="width: 140px" align="center">
                      <div class="selectbox" onclick="showCheckboxesFrequency()">
                        <select class="txtbox">
                          <option>Frequency</option>
                        </select>
                        <div class="overSelect"></div>
                      </div>
                      <div id="checkboxefrequency" align="left" class="checkboxes" style="width: 140px">
                        <label for="isdailyexecution">
                          <input type="checkbox" id="isdailyexecution" value="<%=monitoringParameterVO.getIsDailyExecution()%>>" valign="middle" <%=extDailyExecution%> onclick="doChangeForDailyExecution(this)" onchange="dailyEnableDisableTextBox(this)" />Daily
                          <input type="hidden" id="isdailyexecutionvalue" name="isdailyexecutionvalue" value="<%=monitoringParameterVO.getIsDailyExecution()%>">
                        </label>
                        <label for="isweeklyexecution">
                          <input type="checkbox" id="isweeklyexecution" value="<%=monitoringParameterVO.getIsWeeklyExecution()%>>" valign="middle" <%=extWeeklyExecution%> onclick="doChangeForWeeklyExecution(this)" onchange="weeklyEnablesDisableTextBox(this)" />Weekly
                          <input type="hidden" id="isweeklyexecutionvalue" name="isweeklyexecutionvalue" value="<%=monitoringParameterVO.getIsWeeklyExecution()%>">
                        </label>
                        <label for="ismonthlyexecution">
                          <input type="checkbox" id="ismonthlyexecution" value="<%=monitoringParameterVO.getIsMonthlyExecution()%>>" valign="middle" <%=extMonthlyExecution%> onclick="doChangeForMonthlyExecution(this)" onchange="monthlyEnableDisableTextBox(this)" />Monthly
                          <input type="hidden" id="ismonthlyexecutionvalue" name="ismonthlyexecutionvalue" value="<%=monitoringParameterVO.getIsMonthlyExecution()%>">
                        </label>
                      </div>
                    </div>
                  </td>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <td  colspan="5" align="center" class="textb" style="margin-right: 50%"><h5><b><u>Default Configuration</u></b></h5></td>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Daily Alert Threshold[Contains whole number Or whole number
                    with decimal points]*</td>
                  <td class="textb">:</td>
                  <td width="50%" class="textb" colspan="2">
                    <input maxlength="50" type="text" name="defaultAlertThreshold" class="txtbox" value="<%=alertThreshold%>" <%=dailyThresholdVisibility%>>
                    <input maxlength="50" type="hidden" name="hiddenDefaultAlertThreshold" class="txtbox" value="<%=alertThreshold%>">
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Daily Suspension Threshold[Contains whole number Or whole
                    number with decimal points]*</td>
                  <td class="textb">:</td>
                  <td width="50%" class="textb" colspan="2">
                    <input maxlength="50" type="text" name="defaultSuspensionThreshold" class="txtbox" value="<%=suspensionThreshold%>" <%=dailyThresholdVisibility%>>
                    <input maxlength="50" type="hidden" name="hiddenDefaultSuspensionThreshold" class="txtbox" value="<%=suspensionThreshold%>">
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Weekly Alert Threshold[Contains whole number Or whole number
                    with decimal points]*</td>
                  <td class="textb">:</td>
                  <td width="50%" class="textb" colspan="2">
                    <input maxlength="50" type="text" name="weeklyAlertThreshold" class="txtbox" value="<%=weeklyAlertThreshold%>" <%=weeklyThresholdVisibility%>>
                    <input maxlength="50" type="hidden" name="hiddenWeeklyAlertThreshold" class="txtbox" value="<%=weeklyAlertThreshold%>">
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Weekly Suspension Threshold[Contains whole number Or whole number
                    with decimal points]*</td>
                  <td class="textb">:</td>
                  <td width="50%" class="textb" colspan="2">
                    <input maxlength="50" type="text" name="weeklySuspensionThreshold" class="txtbox" value="<%=weeklySuspensionThreshold%>" <%=weeklyThresholdVisibility%>>
                    <input maxlength="50" type="hidden" name="hiddenWeeklySuspensionThreshold" class="txtbox" value="<%=weeklySuspensionThreshold%>">
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Monthly Alert Threshold[Contains whole number Or whole number
                    with decimal points]*</td>
                  <td class="textb">:</td>
                  <td width="50%" class="textb" colspan="2">
                    <input maxlength="50" type="text" name="monthlyAlertThreshold" class="txtbox" value="<%=monthlyAlertThreshold%>" <%=monthlyThresholdVisibility%>>
                    <input maxlength="50" type="hidden" name="hiddenMonthlyAlertThreshold" class="txtbox" value="<%=monthlyAlertThreshold%>">
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td class="textb">Monthly Suspension Threshold[Contains whole number Or whole number
                    with decimal points]*</td>
                  <td class="textb">:</td>
                  <td width="50%" class="textb" colspan="2">
                    <input maxlength="50" type="text" name="monthlySuspensionThreshold" class="txtbox" value="<%=monthlySuspensionThreshold%>" <%=monthlyThresholdVisibility%>>
                    <input maxlength="50" type="hidden" name="hiddenMonthlySuspensionThreshold" class="txtbox" value="<%=monthlySuspensionThreshold%>">
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb">Alert Message</td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb" colspan="2">
                    <textarea cols="21" rows="2" name="defaultAlertMessage" style="width: 140px"><%=monitoringParameterVO.getDefaultAlertMsg()%></textarea>
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Is Alert To Admin</td>
                  <td width="5%" class="textb">
                    <%
                      String ext="";
                      if("Y".equals(monitoringParameterVO.getDefaultIsAlertToAdmin()))
                      {
                        ext="checked";
                      }
                      String extAdminSales="";
                      if("Y".equals(monitoringParameterVO.getDefaultIsAlertToAdminSales()))
                      {
                        extAdminSales="checked";
                      }
                      String extAdminRF="";
                      if("Y".equals(monitoringParameterVO.getDefaultIsAlertToAdminRF()))
                      {
                        extAdminRF="checked";
                      }
                      String extAdminCB="";
                      if("Y".equals(monitoringParameterVO.getDefaultIsAlertToAdminCB()))
                      {
                        extAdminCB="checked";
                      }
                      String extAdminFraud="";
                      if("Y".equals(monitoringParameterVO.getDefaultIsAlertToAdminFraud()))
                      {
                        extAdminFraud="checked";
                      }
                      String extAdminTech="";
                      if("Y".equals(monitoringParameterVO.getDefaultIsAlertToAdminTech()))
                      {
                        extAdminTech="checked";
                      }
                    %>
                    :</td>
                  <td class="tr0">
                    <input type="checkbox" name="defaultIsAlertToAdmin" value="<%=monitoringParameterVO.getDefaultIsAlertToAdmin()%>" style="margin-top:8px" <%=ext%> onclick="doChangesForIsAlertToAdmin(this)">
                    <input type="hidden" name="defaultIsAlertToAdminValue" id="defaultIsAlertToAdminValue" value="<%=monitoringParameterVO.getDefaultIsAlertToAdmin()%>">
                  </td>
                  <td>
                    <div class="multiselect" align="center">
                      <div class="selectBox" onclick="showCheckboxesAdmin()">
                        <select  class="textb" >
                          <option>Teams</option>
                        </select>
                        <div class="overSelect"></div>
                      </div>
                      <div id="checkboxesadmin" align="left" class="checkboxes" >
                        <label for="defaultIsAlertToAdminSales">
                          <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToAdminSales()%>" id="defaultIsAlertToAdminSales" valign="middle" <%=extAdminSales%> onclick="doChangesForIsAlertToAdminSales(this)" />Sales
                          <input type="hidden" name="defaultIsAlertToAdminSalesValue" id="defaultIsAlertToAdminSalesValue" value="<%=monitoringParameterVO.getDefaultIsAlertToAdminSales()%>">
                        </label>
                        <label for="defaultIsAlertToAdminRF">
                          <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToAdminRF()%>" id="defaultIsAlertToAdminRF" valign="middle" <%=extAdminRF%> onclick="doChangesForIsAlertToAdminRF(this)" />RF
                          <input type="hidden" name="defaultIsAlertToAdminRFValue" id="defaultIsAlertToAdminRFValue" value="<%=monitoringParameterVO.getDefaultIsAlertToAdminRF()%>">
                        </label>
                        <label for="defaultIsAlertToAdminCB">
                          <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToAdminCB()%>" id="defaultIsAlertToAdminCB" valign="middle" <%=extAdminCB%> onclick="doChangesForIsAlertToAdminCB(this)" />CB
                          <input type="hidden" name="defaultIsAlertToAdminCBValue" id="defaultIsAlertToAdminCBValue" value="<%=monitoringParameterVO.getDefaultIsAlertToAdminCB()%>">
                        </label>
                        <label for="defaultIsAlertToAdminFraud">
                          <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToAdminFraud()%>" id="defaultIsAlertToAdminFraud" valign="middle" <%=extAdminFraud%> onclick="doChangesForIsAlertToAdminFraud(this)" />Fraud
                          <input type="hidden" name="defaultIsAlertToAdminFraudValue" id="defaultIsAlertToAdminFraudValue" value="<%=monitoringParameterVO.getDefaultIsAlertToAdminFraud()%>">
                        </label>
                        <label for="defaultIsAlertToAdminTech">
                          <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToAdminFraud()%>" id="defaultIsAlertToAdminTech" valign="middle" <%=extAdminTech%> onclick="doChangesForIsAlertToAdminTech(this)" />Tech
                          <input type="hidden" name="defaultIsAlertToAdminTechValue" id="defaultIsAlertToAdminTechValue" value="<%=monitoringParameterVO.getDefaultIsAlertToAdminTech()%>">
                        </label>
                      </div>
                    </div>
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <td width="4%" class="textb">&nbsp;</td>
                <td width="8%" class="textb">Is Alert To Merchant</td>
                <td valign="middle" class="tr0" width="6%">
                  <%
                    String ext1="";
                    if("Y".equals(monitoringParameterVO.getDefaultIsAlertToMerchant()))
                    {
                      ext1="checked";
                    }
                    String ext1MerchantSales="";
                    if("Y".equals(monitoringParameterVO.getDefaultIsAlertToMerchantSales()))
                    {
                      ext1MerchantSales="checked";
                    }
                    String ext1MerchantRF="";
                    if("Y".equals(monitoringParameterVO.getDefaultIsAlertToMerchantRF()))
                    {
                      ext1MerchantRF="checked";
                    }
                    String ext1MerchantCB="";
                    if("Y".equals(monitoringParameterVO.getDefaultIsAlertToMerchantCB()))
                    {
                      ext1MerchantCB="checked";
                    }
                    String ext1MerchantFraud="";
                    if("Y".equals(monitoringParameterVO.getDefaultIsAlertToMerchantFraud()))
                    {
                      ext1MerchantFraud="checked";
                    }
                    String ext1MerchantTech="";
                    if("Y".equals(monitoringParameterVO.getDefaultIsAlertToMerchantTech()))
                    {
                      ext1MerchantTech="checked";
                    }
                  %>
                  :
                </td>
                <td class="tr0">
                  <input type="checkbox" name="defaultIsAlertToMerchant" value="<%=monitoringParameterVO.getDefaultIsAlertToMerchant()%>" style="margin-top:8px" <%=ext1%> onclick="doChangesForIsAlertToMerchant(this)">
                  <input type="hidden" name="defaultIsAlertToMerchantValue" id="defaultIsAlertToMerchantValue" value="<%=monitoringParameterVO.getDefaultIsAlertToMerchant()%>">
                </td>
                <td>
                  <div class="multiselect" align="center">
                    <div class="selectBox" onclick="showCheckboxesMerchant()">
                      <select  class="textb" >
                        <option>Teams</option>
                      </select>
                      <div class="overSelect"></div>
                    </div>
                    <div id="checkboxesmerchant" align="left" class="checkboxes" >
                      <label for="defaultIsAlertToMerchantSales">
                        <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToMerchantSales()%>" id="defaultIsAlertToMerchantSales" valign="middle" <%=ext1MerchantSales%> onclick="doChangesForIsAlertToMerchantSales(this)" />Sales
                        <input type="hidden" name="defaultIsAlertToMerchantSalesValue" id="defaultIsAlertToMerchantSalesValue" value="<%=monitoringParameterVO.getDefaultIsAlertToMerchantSales()%>">
                      </label>
                      <label for="defaultIsAlertToMerchantRF">
                        <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToMerchantRF()%>" id="defaultIsAlertToMerchantRF" valign="middle" <%=ext1MerchantRF%> onclick="doChangesForIsAlertToMerchantRF(this)" />RF
                        <input type="hidden" name="defaultIsAlertToMerchantRFValue" id="defaultIsAlertToMerchantRFValue" value="<%=monitoringParameterVO.getDefaultIsAlertToMerchantRF()%>">
                      </label>
                      <label for="defaultIsAlertToMerchantCB">
                        <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToMerchantCB()%>" id="defaultIsAlertToMerchantCB" valign="middle" <%=ext1MerchantCB%> onclick="doChangesForIsAlertToMerchantCB(this)" />CB
                        <input type="hidden" name="defaultIsAlertToMerchantCBValue" id="defaultIsAlertToMerchantCBValue" value="<%=monitoringParameterVO.getDefaultIsAlertToMerchantCB()%>">
                      </label>
                      <label for="defaultIsAlertToMerchantFraud">
                        <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToMerchantFraud()%>" id="defaultIsAlertToMerchantFraud" valign="middle" <%=ext1MerchantFraud%> onclick="doChangesForIsAlertToMerchantFraud(this)" />Fraud
                        <input type="hidden" name="defaultIsAlertToMerchantFraudValue" id="defaultIsAlertToMerchantFraudValue" value="<%=monitoringParameterVO.getDefaultIsAlertToMerchantFraud()%>">
                      </label>
                      <label for="defaultIsAlertToMerchantTech">
                        <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToMerchantTech()%>" id="defaultIsAlertToMerchantTech" valign="middle" <%=ext1MerchantTech%> onclick="doChangesForIsAlertToMerchantTech(this)" />Tech
                        <input type="hidden" name="defaultIsAlertToMerchantTechValue" id="defaultIsAlertToMerchantTechValue" value="<%=monitoringParameterVO.getDefaultIsAlertToMerchantTech()%>">
                      </label>
                    </div>
                  </div>
                </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Is Alert To Partner</td>
                  <td width="5%" class="textb">
                    <%
                      String ext2="";
                      if("Y".equals(monitoringParameterVO.getDefaultIsAlertToPartner()))
                      {
                        ext2="checked";
                      }
                      String ext2PartnerSales="";
                      if("Y".equals(monitoringParameterVO.getDefaultIsAlertToPartnerSales()))
                      {
                        ext2PartnerSales="checked";
                      }
                      String ext2PartnerRF="";
                      if("Y".equals(monitoringParameterVO.getDefaultIsAlertToPartnerRF()))
                      {
                        ext2PartnerRF="checked";
                      }
                      String ext2PartnerCB="";
                      if("Y".equals(monitoringParameterVO.getDefaultIsAlertToPartnerCB()))
                      {
                        ext2PartnerCB="checked";
                      }
                      String ext2PartnerFraud="";
                      if("Y".equals(monitoringParameterVO.getDefaultIsAlertToPartnerFraud()))
                      {
                        ext2PartnerFraud="checked";
                      }
                      String ext2PartnerTech="";
                      if("Y".equals(monitoringParameterVO.getDefaultIsAlertToPartnerTech()))
                      {
                        ext2PartnerTech="checked";
                      }
                    %>
                    :
                  </td>
                  <td class="tr0">
                    <input type="checkbox" name="defaultIsAlertToPartner" value="<%=monitoringParameterVO.getDefaultIsAlertToPartner()%>" style="margin-top:8px" <%=ext2%> onclick="doChangesForIsAlertToPartner(this)">
                    <input type="hidden" name="defaultIsAlertToPartnerValue" id="defaultIsAlertToPartnerValue" value="<%=monitoringParameterVO.getDefaultIsAlertToPartner()%>">
                  </td>
                  <td>
                    <div class="multiselect" align="center">
                      <div class="selectBox" onclick="showCheckboxesPartner()">
                        <select  class="textb" >
                          <option>Teams</option>
                        </select>
                        <div class="overSelect"></div>
                      </div>
                      <div id="checkboxespartner" align="left" class="checkboxes" >
                        <label for="defaultIsAlertToPartnerSales">
                          <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToPartnerSales()%>" id="defaultIsAlertToPartnerSales" valign="middle" <%=ext2PartnerSales%> onclick="doChangesForIsAlertToPartnerSales(this)" />Sales
                          <input type="hidden" name="defaultIsAlertToPartnerSalesValue" id="defaultIsAlertToPartnerSalesValue" value="<%=monitoringParameterVO.getDefaultIsAlertToPartnerSales()%>">
                        </label>
                        <label for="defaultIsAlertToPartnerRF">
                          <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToPartnerRF()%>" id="defaultIsAlertToPartnerRF" valign="middle" <%=ext2PartnerRF%> onclick="doChangesForIsAlertToPartnerRF(this)" />RF
                          <input type="hidden" name="defaultIsAlertToPartnerRFValue" id="defaultIsAlertToPartnerRFValue" value="<%=monitoringParameterVO.getDefaultIsAlertToPartnerRF()%>">
                        </label>
                        <label for="defaultIsAlertToPartnerCB">
                          <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToPartnerCB()%>" id="defaultIsAlertToPartnerCB" valign="middle" <%=ext2PartnerCB%> onclick="doChangesForIsAlertToPartnerCB(this)" />CB
                          <input type="hidden" name="defaultIsAlertToPartnerCBValue" id="defaultIsAlertToPartnerCBValue" value="<%=monitoringParameterVO.getDefaultIsAlertToPartnerCB()%>">
                        </label>
                        <label for="defaultIsAlertToPartnerFraud">
                          <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToPartnerFraud()%>" id="defaultIsAlertToPartnerFraud" valign="middle" <%=ext2PartnerFraud%> onclick="doChangesForIsAlertToPartnerFraud(this)" />Fraud
                          <input type="hidden" name="defaultIsAlertToPartnerFraudValue" id="defaultIsAlertToPartnerFraudValue" value="<%=monitoringParameterVO.getDefaultIsAlertToPartnerFraud()%>">
                        </label>
                        <label for="defaultIsAlertToPartnerTech">
                          <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToPartnerTech()%>" id="defaultIsAlertToPartnerTech" valign="middle" <%=ext2PartnerTech%> onclick="doChangesForIsAlertToPartnerTech(this)" />Tech
                          <input type="hidden" name="defaultIsAlertToPartnerTechValue" id="defaultIsAlertToPartnerTechValue" value="<%=monitoringParameterVO.getDefaultIsAlertToPartnerTech()%>">
                        </label>
                      </div>
                    </div>
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Is Alert To Agent</td>
                  <td width="5%" class="textb">
                    <%
                      String ext3="";
                      if("Y".equals(monitoringParameterVO.getDefaultIsAlertToAgent()))
                      {
                        ext3="checked";
                      }
                      String ext3AgentSales="";
                      if("Y".equals(monitoringParameterVO.getDefaultIsAlertToAgentSales()))
                      {
                        ext3AgentSales="checked";
                      }
                      String ext3AgentRF="";
                      if("Y".equals(monitoringParameterVO.getDefaultIsAlertToAgentRF()))
                      {
                        ext3AgentRF="checked";
                      }
                      String ext3AgentCB="";
                      if("Y".equals(monitoringParameterVO.getDefaultIsAlertToAgentCB()))
                      {
                        ext3AgentCB="checked";
                      }
                      String ext3AgentFraud="";
                      if("Y".equals(monitoringParameterVO.getDefaultIsAlertToAgentFraud()))
                      {
                        ext3AgentFraud="checked";
                      }
                      String ext3AgentTech="";
                      if("Y".equals(monitoringParameterVO.getDefaultIsAlertToAgentTech()))
                      {
                        ext3AgentTech="checked";
                      }
                    %>
                    :
                  </td>
                  <td class="tr0">
                    <input type="checkbox" name="defaultIsAlertToAgent" value="<%=monitoringParameterVO.getDefaultIsAlertToAgent()%>" style="margin-top:8px" <%=ext3%> onclick="doChangesForIsAlertToAgent(this)">
                    <input type="hidden" name="defaultIsAlertToAgentValue" id="defaultIsAlertToAgentValue" value="<%=monitoringParameterVO.getDefaultIsAlertToAgent()%>">
                  </td>
                  <td>
                    <div class="multiselect" align="center">
                      <div class="selectBox" onclick="showCheckboxesAgent()">
                        <select  class="textb" >
                          <option>Teams</option>
                        </select>
                        <div class="overSelect"></div>
                      </div>
                      <div id="checkboxesagent" align="left" class="checkboxes" >
                        <label for="defaultIsAlertToAgentSales">
                          <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToAgentSales()%>" id="defaultIsAlertToAgentSales" valign="middle" <%=ext3AgentSales%> onclick="doChangesForIsAlertToAgentSales(this)" />Sales
                          <input type="hidden" name="defaultIsAlertToAgentSalesValue" id="defaultIsAlertToAgentSalesValue" value="<%=monitoringParameterVO.getDefaultIsAlertToAgentSales()%>">
                        </label>
                        <label for="defaultIsAlertToAgentRF">
                          <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToAgentRF()%>" id="defaultIsAlertToAgentRF" valign="middle" <%=ext3AgentRF%> onclick="doChangesForIsAlertToAgentRF(this)" />RF
                          <input type="hidden" name="defaultIsAlertToAgentRFValue" id="defaultIsAlertToAgentRFValue" value="<%=monitoringParameterVO.getDefaultIsAlertToAgentRF()%>">
                        </label>
                        <label for="defaultIsAlertToAgentCB">
                          <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToAgentCB()%>" id="defaultIsAlertToAgentCB" valign="middle" <%=ext3AgentCB%> onclick="doChangesForIsAlertToAgentCB(this)" />CB
                          <input type="hidden" name="defaultIsAlertToAgentCBValue" id="defaultIsAlertToAgentCBValue" value="<%=monitoringParameterVO.getDefaultIsAlertToAgentCB()%>">
                        </label>
                        <label for="defaultIsAlertToAgentFraud">
                          <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToAgentFraud()%>" id="defaultIsAlertToAgentFraud" valign="middle" <%=ext3AgentFraud%> onclick="doChangesForIsAlertToAgentFraud(this)" />Fraud
                          <input type="hidden" name="defaultIsAlertToAgentFraudValue" id="defaultIsAlertToAgentFraudValue" value="<%=monitoringParameterVO.getDefaultIsAlertToAgentFraud()%>">
                        </label>
                        <label for="defaultIsAlertToAgentTech">
                          <input type="checkbox" value="<%=monitoringParameterVO.getDefaultIsAlertToAgentTech()%>" id="defaultIsAlertToAgentTech" valign="middle" <%=ext3AgentTech%> onclick="doChangesForIsAlertToAgentTech(this)" />Tech
                          <input type="hidden" name="defaultIsAlertToAgentTechValue" id="defaultIsAlertToAgentTechValue" value="<%=monitoringParameterVO.getDefaultIsAlertToAgentTech()%>">
                        </label>
                      </div>
                    </div>
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <td width="4%" class="textb">&nbsp;</td>
                <td width="8%" class="textb">Alert Activation</td>
                <td width="5%" class="textb">:</td>
                <td valign="middle" class="tr0" width="6%" colspan="2">
                  <%
                    String ext4="";
                    if("Y".equals(monitoringParameterVO.getDefaultAlertActivation()))
                    {
                      ext4="checked";
                    }
                  %>
                  <input type="checkbox" name="defaultAlertActivation" id="defaultAlertActivation" value="<%=monitoringParameterVO.getDefaultAlertActivation()%>" valign="middle" <%=ext4%> onclick="doChangesForAlertActivation(this)">
                  <input type="hidden" name="defaultAlertActivationValue" id="defaultAlertActivationValue" value="<%=monitoringParameterVO.getDefaultAlertActivation()%>">
                </td>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <td width="4%" class="textb">&nbsp;</td>
                <td width="8%" class="textb">Suspension Activation</td>
                <td width="5%" class="textb">:</td>
                <td valign="middle" class="tr0" width="6%" colspan="2">
                  <%
                    String ext5="";
                    if("Y".equals(monitoringParameterVO.getDefaultSuspensionActivation()))
                    {
                      ext5="checked";
                    }
                  %>
                  <input type="checkbox" name="defaultSuspensionActivation" id="defaultSuspensionActivation" value="<%=monitoringParameterVO.getDefaultSuspensionActivation()%>" valign="middle" <%=ext5%> onclick="doChangesForSuspensionActivation(this)">
                  <input type="hidden" name="defaultSuspensionActivationValue" id="defaultSuspensionActivationValue" value="<%=monitoringParameterVO.getDefaultSuspensionActivation()%>">
                </td>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2">
                    <button type="submit" class="buttonform" name="update" value="<%=monitoringParameterVO.getMonitoringParameterId()%>">
                      <i class="fa fa-sign-in"></i>
                      &nbsp;&nbsp;Update
                    </button>
                  </td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                </tbody>
              </table>
            </td>
          </tr>
          </tbody>
        </table>
      </form>
    </div>
  </div>
</div>
  <%
      }
      else
      {
        out.println("<br><br><br>");
        out.println(Functions.NewShowConfirmation("Sorry","No Records Found"));
      }
    %>
