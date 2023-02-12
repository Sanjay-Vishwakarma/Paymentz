<%@ page import="com.directi.pg.Functions"%>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.vo.merchantmonitoring.enums.*" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Vishal
  Date: 11/5/16
  Time: 9:10 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
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
    function showCheckboxesFrequency()
    {
      var checkboxes = document.getElementById("checkboxesfrequency");
      if(!expanded)
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
    function weeklyEnableDisableTextBox(data)
    {
      if(data.checked)
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
      if(data.checked)
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
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title></title>
</head>
<body>
<%!
  Logger logger=new Logger("manageChargeMaster");
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default" >
      <div class="panel-heading" >
        Risk Rule Master
        <div style="float: right;">
          <form action="/icici/monitoringParameterMaster.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Charge Master" name="submit" class="addnewmember">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Risk Rule Master
            </button>
          </form>
        </div>
      </div>
      <br>
      <form action="/icici/servlet/AddNewMonitoringParameter?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <input type="hidden" value="true" name="isSubmitted">
        <table align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
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
                    <input maxlength="255" type="text" name="monitoingParaName" class="txtbox" value="">
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
                  <td width="43%" class="textb">Risk Rule Description*</td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb" colspan="2">
                    <input maxlength="255" type="text" name="monitoingParaTechName" class="txtbox" value="">
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
                        for (MonitoringUnit monitoringUnit: MonitoringUnit.values()){
                          out.println("<option value=\""+monitoringUnit.name()+"\">"+monitoringUnit.name()+"</option>");
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
                        for (MonitoringCategory monitoringCategory: MonitoringCategory.values()){
                          out.println("<option value=\""+monitoringCategory.name()+"\">"+monitoringCategory.name()+"</option>");
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
                        for (MonitoringDeviation monitoringDeviation: MonitoringDeviation.values()){
                          out.println("<option value=\""+monitoringDeviation.name()+"\">"+monitoringDeviation.name()+"</option>");
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
                        for (MonitoringKeyword monitoringKeyword: MonitoringKeyword.values()){
                          out.println("<option value=\""+monitoringKeyword.name()+"\">"+monitoringKeyword.name()+"</option>");
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
                        for (MonitoringSubKeyword monitoringSubKeyword: MonitoringSubKeyword.values()){
                          out.println("<option value=\""+monitoringSubKeyword.name()+"\">"+monitoringSubKeyword.name()+"</option>");
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
                  <td class="textb">Monitoring Rule Group*</td>
                  <td class="textb">:</td>
                  <td colspan="2">
                    <select name="monitoingAlertCategory" class="txtbox">
                      <option value=""></option>
                      <%
                        for (MonitoringAlertType monitoringAlertType: MonitoringAlertType.values())
                        {
                          out.println("<option value=\""+monitoringAlertType.name()+"\">"+monitoringAlertType.name()+"</option>");
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
                  <td class="textb">Terminal/Merchant Maturity*</td>
                  <td class="textb">:</td>
                  <td colspan="2">
                    <select name="monitoingOnChannel" class="txtbox">
                      <option value=""></option>
                      <%
                        for (MonitoringChannelLevel monitoringChannelLevel: MonitoringChannelLevel.values())
                        {
                          String displayText="";
                          if(MonitoringChannelLevel.New.name().equals(monitoringChannelLevel.name()))
                          {
                            displayText="(<90 days)";
                          }
                          else if(MonitoringChannelLevel.Old.name().equals(monitoringChannelLevel.name()))
                          {
                            displayText="(>90 days)";
                          }
                          out.println("<option value=\""+monitoringChannelLevel.name()+"\">"+monitoringChannelLevel.name()+" "+displayText+"</option>");
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
                      <option value=""></option>
                      <option value="lineChart">LineChart</option>
                      <option value="barChart">BarChart</option>
                      <option value="doughnutChart">DoughnutChart</option>
                      <option value="progressBarChart">ProgressBarChart</option>
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
                  <td class="textb">:</td>
                  <td colspan="2">
                    <div class="multiselect" style="width: 140px" align="center">
                      <div class="selectbox" onclick="showCheckboxesFrequency()">
                        <select class="txtbox">
                          <option>Frequency</option>
                        </select>
                        <div class="overSelect"></div>
                      </div>
                      <div id="checkboxesfrequency" align="left" class="checkboxes" style="width: 140px">
                        <label for="isdailyexecution">
                          <input type="checkbox" value="Y" id="isdailyexecution" name="isdailyexecution" onchange="dailyEnableDisableTextBox(this)" />Daily
                        </label>
                        <label for="isweeklyexecution">
                          <input type="checkbox" value="Y" id="isweeklyexecution" name="isweeklyexecution" onchange="weeklyEnableDisableTextBox(this)" />Weekly
                        </label>
                        <label for="ismonthlyexecution">
                          <input type="checkbox" value="Y" id="ismonthlyexecution" name="ismonthlyexecution" onchange="monthlyEnableDisableTextBox(this)" />Monthly
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
                <td  colspan="5" align="center" class="textb" style="margin-right: 50%"><h5><b><u>Default Configuration</u></b></h5></td>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb">Daily Alert Threshold[Contains whole number Or whole number
                    with decimal points]
                  </td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb" colspan="2">
                    <input maxlength="20" type="text" name="defaultAlertThreshold" class="txtbox" value="" disabled="disabled">
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
                  <td width="43%" class="textb">Daily Suspension Threshold[Contains whole number Or whole
                    number with decimal points]
                  </td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb" colspan="2">
                    <input maxlength="20" type="text" name="defaultSuspensionThreshold" class="txtbox" value="" disabled="disabled">
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
                  <td width="43%" class="textb">Weekly Alert Threshold[Contains whole number Or whole
                    number with decimal points]
                  </td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb" colspan="2">
                    <input maxlength="20" type="text" name="weeklyAlertThreshold" class="txtbox" value="" disabled="disabled">
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
                  <td width="43%" class="textb">Weekly Suspension Threshold[Contains whole number Or whole
                    number with decimal points]
                  </td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb" colspan="2">
                    <input maxlength="20" type="text" name="weeklySuspensionThreshold" class="txtbox" value="" disabled="disabled">
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
                  <td width="43%" class="textb">Monthly Alert <Threshold></Threshold>[Contains whole number Or whole
                    number with decimal points]
                  </td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb" colspan="2">
                    <input maxlength="20" type="text" name="monthlyAlertThreshold" class="txtbox" value="" disabled="disabled">
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
                  <td width="43%" class="textb">Monthly Suspension Threshold[Contains whole number Or whole
                    number with decimal points]
                  </td>
                  <td width="5%" class="textb">:</td>
                  <td width="50%" class="textb" colspan="2">
                    <input maxlength="20" type="text" name="monthlySuspensionThreshold" class="txtbox" value="" disabled="disabled">
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
                  <td width="40%" class="textb" colspan="2">
                    <textarea cols="21" rows="2" name="defaultAlertMessage" style="width: 140px"></textarea>
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
                  <td width="5%" class="textb">:</td>
                  <td class="tr0">
                    <input type="checkbox" name="defaultIsAlertToAdmin" value="Y" style="margin-top:8px">
                  </td>
                  <td>
                    <div class="multiselect" align="center">
                      <div class="selectBox" onclick="showCheckboxesAdmin()">
                        <select class="txtbox">
                          <option>Teams</option>
                        </select>
                        <div class="overSelect"></div>
                      </div>
                      <div id="checkboxesadmin" align="left" class="checkboxes" >
                        <label for="defaultIsAlertToAdminSales">
                          <input type="checkbox" value="Y" name="defaultIsAlertToAdminSales" id="defaultIsAlertToAdminSales"/>Sales
                        </label>
                        <label for="defaultIsAlertToAdminRF">
                          <input type="checkbox" value="Y" name="defaultIsAlertToAdminRF" id="defaultIsAlertToAdminRF"/>RF
                        </label>
                        <label for="defaultIsAlertToAdminCB">
                          <input type="checkbox" value="Y" name="defaultIsAlertToAdminCB" id="defaultIsAlertToAdminCB"/>CB
                        </label>
                        <label for="defaultIsAlertToAdminFraud">
                          <input type="checkbox" value="Y" name="defaultIsAlertToAdminFraud" id="defaultIsAlertToAdminFraud"/>Fraud
                        </label>
                        <label for="defaultIsAlertToAdminTech">
                          <input type="checkbox" value="Y" name="defaultIsAlertToAdminTech" id="defaultIsAlertToAdminTech"/>Tech
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
                  <td width="8%" class="textb">Is Alert To Merchant</td>
                  <td width="5%" class="textb">:</td>
                  <td class="tr0">
                    <input type="checkbox" name="defaultIsAlertToMerchant" value="Y" style="margin-top:8px">
                  </td>
                  <td>
                    <div class="multiselect" align="center">
                      <div class="selectBox" onclick="showCheckboxesMerchant()">
                        <select  class="txtbox" >
                          <option>Teams</option>
                        </select>
                        <div class="overSelect"></div>
                      </div>
                      <div id="checkboxesmerchant" align="left" class="checkboxes" >
                        <label for="defaultIsAlertToMerchantSales">
                          <input type="checkbox" value="Y" name="defaultIsAlertToMerchantSales" id="defaultIsAlertToMerchantSales"/>Sales
                        </label>
                        <label for="defaultIsAlertToMerchantRF">
                          <input type="checkbox" value="Y" name="defaultIsAlertToMerchantRF" id="defaultIsAlertToMerchantRF"/>RF
                        </label>
                        <label for="defaultIsAlertToMerchantCB">
                          <input type="checkbox" value="Y" name="defaultIsAlertToMerchantCB" id="defaultIsAlertToMerchantCB"/>CB
                        </label>
                        <label for="defaultIsAlertToMerchantFraud">
                          <input type="checkbox" value="Y" name="defaultIsAlertToMerchantFraud" id="defaultIsAlertToMerchantFraud"/>Fraud
                        </label>
                        <label for="defaultIsAlertToMerchantTech">
                          <input type="checkbox" value="Y" name="defaultIsAlertToMerchantTech" id="defaultIsAlertToMerchantTech"/>Tech
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
                  <td width="5%" class="textb">:</td>
                  <td class="tr0">
                    <input type="checkbox" name="defaultIsAlertToPartner" value="Y" style="margin-top:8px">
                  </td>
                  <td>
                    <div class="multiselect" align="center">
                      <div class="selectBox" onclick="showCheckboxesPartner()">
                        <select  class="txtbox" >
                          <option>Teams</option>
                        </select>
                        <div class="overSelect"></div>
                      </div>
                      <div id="checkboxespartner" align="left" class="checkboxes" >
                        <label for="defaultIsAlertToPartnerSales">
                          <input type="checkbox" value="Y" name="defaultIsAlertToPartnerSales" id="defaultIsAlertToPartnerSales"/>Sales
                        </label>
                        <label for="defaultIsAlertPartnerRF">
                          <input type="checkbox" value="Y" name="defaultIsAlertPartnerRF" id="defaultIsAlertPartnerRF"/>RF
                        </label>
                        <label for="defaultIsAlertToPartnerCB">
                          <input type="checkbox" value="Y" name="defaultIsAlertToPartnerCB" id="defaultIsAlertToPartnerCB"/>CB
                        </label>
                        <label for="defaultIsAlertToPartnerFraud">
                          <input type="checkbox" value="Y" name="defaultIsAlertToPartnerFraud" id="defaultIsAlertToPartnerFraud"/>Fraud
                        </label>
                        <label for="defaultIsAlertToPartnerTech">
                          <input type="checkbox" value="Y" name="defaultIsAlertToPartnerTech" id="defaultIsAlertToPartnerTech"/>Tech
                        </label>
                      </div>
                    </div>
                  </td>
                </tr>
                </td>
                <tr>
                  <td width="2%" class="textb">&nbsp;</td>
                  <td width="43%" class="textb"></td>
                  <td width="5%" class="textb"></td>
                  <td width="50%" class="textb" colspan="2"></td>
                </tr>
                <tr>
                  <td width="4%" class="textb">&nbsp;</td>
                  <td width="8%" class="textb">Is Alert To Agent</td>
                  <td width="5%" class="textb">:</td>
                  <td class="tr0">
                    <input type="checkbox" name="defaultIsAlertToAgent" value="Y" style="margin-top:8px">
                  </td>
                  <td>
                    <div class="multiselect" align="center">
                      <div class="selectBox" onclick="showCheckboxesAgent()">
                        <select  class="txtbox" >
                          <option>Teams</option>
                        </select>
                        <div class="overSelect"></div>
                      </div>
                      <div id="checkboxesagent" align="left" class="checkboxes" >
                        <label for="defaultIsAlertToAgentSales">
                          <input type="checkbox" value="Y" name="defaultIsAlertToAgentSales" id="defaultIsAlertToAgentSales"/>Sales
                        </label>
                        <label for="defaultIsAlertToAgentRF">
                          <input type="checkbox" value="Y" name="defaultIsAlertToAgentRF" id="defaultIsAlertToAgentRF"/>RF
                        </label>
                        <label for="defaultIsAlertToAgentCB">
                          <input type="checkbox" value="Y" name="defaultIsAlertToAgentCB" id="defaultIsAlertToAgentCB"/>CB
                        </label>
                        <label for="defaultIsAlertToAgentFraud">
                          <input type="checkbox" value="Y" name="defaultIsAlertToAgentFraud" id="defaultIsAlertToAgentFraud"/>Fraud
                        </label>
                        <label for="defaultIsAlertToAgentTech">
                          <input type="checkbox" value="Y" name="defaultIsAlertToAgentTech" id="defaultIsAlertToAgentTech"/>Tech
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
                  <input type="checkbox" name="defaultAlertActivation" value="Y" valign="middle">
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
                  <input type="checkbox" name="defaultSuspensionActivation" value="Y" valign="middle">
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
                    <button type="submit" class="buttonform" value="Save">
                      <i class="fa fa-sign-in"></i>
                      &nbsp;&nbsp;Add
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
    if (request.getAttribute("message") != null)
    {
      out.println("<div class=\"reporttable\">");
      out.println(Functions.NewShowConfirmation("Result",(String)request.getAttribute("message")));
      out.println("</div>");
    }
  }
  else
  {
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
%>
</div>
</body>
</html>