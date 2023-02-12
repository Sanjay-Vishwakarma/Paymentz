<%@ page import="com.directi.pg.Functions,com.directi.pg.Logger" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.merchantmonitoring.MonitoringParameterMappingVO" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.vo.merchantmonitoring.MonitoringParameterVO" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.manager.vo.merchantmonitoring.enums.MonitoringUnit" %>
<%@ page import="com.manager.vo.merchantmonitoring.enums.MonitoringSubKeyword" %>
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
    var checkboxes = document.getElementsByName("mappingid_" + i);
    var total_boxes = checkboxes.length;
    flag = false;

    for(i=0; i<total_boxes; i++ )
    {
      if(checkboxes[i].checked)
      {
        flag= true;
        break;
      }
    }
    if(!flag)
    {
      alert("select at least one rule");
      return false;
    }
    if (confirm("Do you really want to update all selected rule."))
    {
      document.getElementById("details"+i).submit();
    }
  }
  function doChangesForAlertActivation(data, mappingId)
  {
    if(data.checked)
    {
      document.getElementById('alertActiovationValue_' + mappingId).value = "Y";
    }
    else
    {
      document.getElementById('alertActiovationValue_' + mappingId).value = "N";
    }
    document.getElementById('alertActiovation_' + mappingId).value = document.getElementById('alertActiovationValue_' + mappingId).value;
  }
  function doChangesForSuspensionActivation(data, mappingId)
  {
    if (data.checked)
    {
      document.getElementById('suspensionActivationValue_' + mappingId).value = "Y";
    }
    else
    {
      document.getElementById('suspensionActivationValue_' + mappingId).value = "N";
    }
    document.getElementById('suspensionActivation_' + mappingId).value = document.getElementById('suspensionActivationValue_' + mappingId).value;
  }
  function doChangesForAlertToAdmin(data, mappingId)
  {
    if (data.checked)
    {
      document.getElementById('isAlertToAdminValue_' + mappingId).value = "Y";
    }
    else
    {
      document.getElementById('isAlertToAdminValue_' + mappingId).value = "N";
    }
    document.getElementById('isAlertToAdmin_' + mappingId).value = document.getElementById('isAlertToAdminValue_' + mappingId).value;
  }
  function doChangesForAlertToPSP(data, mappingId)
  {
    if (data.checked)
    {
      document.getElementById('isAlertToPartnerValue_' + mappingId).value = "Y";
    }
    else
    {
      document.getElementById('isAlertToPartnerValue_' + mappingId).value = "N";
    }
    document.getElementById('isAlertToPartner_' + mappingId).value = document.getElementById('isAlertToPartnerValue_' + mappingId).value;
  }
  function doChangesForAlertToMerchant(data, mappingId)
  {
    if (data.checked)
    {
      document.getElementById('isAlertToMerchantValue_' + mappingId).value = "Y";
    }
    else
    {
      document.getElementById('isAlertToMerchantValue_' + mappingId).value = "N";
    }
    document.getElementById('isAlertToMerchant_' + mappingId).value = document.getElementById('isAlertToMerchantValue_' + mappingId).value;
  }
  function doChangesForAlertToAgent(data, mappingId)
  {
    if (data.checked)
    {
      document.getElementById('isAlertToAgentValue_' + mappingId).value = "Y";
    }
    else
    {
      document.getElementById('isAlertToAgentValue_' + mappingId).value = "N";
    }
    document.getElementById('isAlertToAgent_' + mappingId).value = document.getElementById('isAlertToAgentValue_' + mappingId).value;
  }
  function ToggleAll(checkbox, terminalid)
  {
    flag = checkbox.checked;
    var checkboxes = document.getElementsByName("mappingid_" + terminalid);
    var total_boxes = checkboxes.length;

    for (i = 0; i < total_boxes; i++)
    {
      checkboxes[i].checked = flag;
    }
  }
</script>
<body class="bodybackground">
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading">
        Merchant Monitoring Settings
      </div>
      <% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        if (com.directi.pg.Admin.isLoggedIn(session))
        {
      %>
      <form action="/icici/servlet/MappingMaster?ctoken=<%=ctoken%>" method="get" name="F1"
            onsubmit="">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <br>
        <table align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
          <tr>
            <td>
              <%
                String errormsg1 = (String) request.getAttribute("error");
                //String updateMsg = (String) request.getAttribute("updatemsg");
                if (errormsg1 != null)
                {
                  out.println("<center><font class=\"textb\"><b>" + errormsg1 + "<br></b></font></center>");
                }
                /*if(updateMsg!=null && "".equals(updateMsg))
                {
                  out.println("<center><font class=\"textb\"><b>" + updateMsg + "<br></b></font></center>");
                }*/
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
    Map<String, List<MonitoringParameterMappingVO>> stringListMap=(Map)request.getAttribute("stringListMap");
    TerminalManager terminalManager = new TerminalManager();

    if (stringListMap!=null && stringListMap.size() > 0)
    {
      Set set=stringListMap.keySet();
      Iterator iterator=set.iterator();
      while (iterator.hasNext())
      {
        String terminalId=(String)iterator.next();
        TerminalVO terminalVO = terminalManager.getTerminalByTerminalId(terminalId);
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
        String currency = gatewayAccount.getCurrency();
        String bank = gatewayAccount.getGateway();
        String cardType = terminalVO.getCardType();

        List<MonitoringParameterMappingVO> monitoringParameterMappingVOs=stringListMap.get(terminalId);
  %>
  <form name="update" id="details_<%=terminalId%>" action="/icici/servlet/UpdateMonitoringParameterMapping?ctoken=<%=ctoken%>"
        method=post>
    <input type="hidden" value="<%=ctoken%>" name="ctoken">
    <input type=hidden value="<%=terminalVO.getAccountId()%>" name="accountid">
    <input type=hidden value="<%=terminalVO.getTerminalId()%>" name="terminalid">
    <input type=hidden value="<%=terminalVO.getMemberId()%>" name="memberid">
    <input type=hidden value="UPDATE" name="action">
    <table border="0" width="100%" class="table table-striped table-bordered table-green dataTable"
           style="margin-bottom: 0px">
      <thead>
      <tr>
        <td valign="middle" align="center" class="th0" colspan='11'><font size=2><%=bank%> - <%=currency%> - <%=cardType%></font></td>
        <td valign="middle" align="center" class="th0" style="border-left-style: hidden"><%=terminalVO.toString()%>
        </td>
      </tr>
      </thead>
      <thead>
      <tr>
        <td class="textb" valign="middle" align="center"><input type="checkbox"
                                                                onClick="ToggleAll(this,<%=terminalId%>);"
                                                                name="alltrans"></td>
        <td class="textb" valign="middle" align="center"><b>Monitoring ID</b></td>
        <td class="textb" valign="middle" align="center"><b>Risk Parameter</b></td>
        <td class="textb" valign="middle" align="center"><b>Alert Activation</b></td>
        <td class="textb" valign="middle" align="center"><b>Alert Threshold</b></td>
        <td class="textb" valign="middle" align="center"><b>Suspension Activation</b></td>
        <td class="textb" valign="middle" align="center"><b>Suspension Threshold</b></td>
        <td class="textb" valign="middle" align="center"><b>Alert To Admin</b></td>
        <td class="textb" valign="middle" align="center"><b>Alert To PSP</b></td>
        <td class="textb" valign="middle" align="center"><b>Alert To Merchant</b></td>
        <td class="textb" valign="middle" align="center"><b>Alert To Agent</b></td>
        <td class="textb" valign="middle" align="center"><b>Frequency</b></td>
      </tr>
      </thead>
      <%
        for (MonitoringParameterMappingVO monitoringParameterMappingVO : monitoringParameterMappingVOs)
        {
          MonitoringParameterVO monitoringParameterVO = monitoringParameterMappingVO.getMonitoringParameterVO();
          String alertActivation = "";
          String suspensionActivation = "";
          String isAlertToAdmin = "";
          String isAlertToPSP = "";
          String isAlertToMerchant = "";
          String isAlertToAgent = "";

          String monitoringUnit = "";
          String alertThreshold = "0";
          String suspensionThreshold = "0";
          if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()))
          {
            alertThreshold = Functions.round(monitoringParameterMappingVO.getAlertThreshold(), 2);
            suspensionThreshold = Functions.round(monitoringParameterMappingVO.getSuspensionThreshold(), 2);
            if (Double.parseDouble(alertThreshold) <= 0)
            {
              alertThreshold = "";
            }
            if (Double.parseDouble(suspensionThreshold) <= 0)
            {
              suspensionThreshold = "";
            }
            monitoringUnit = "%";
          }
          else
          {
            alertThreshold = Functions.round(monitoringParameterMappingVO.getAlertThreshold(), 0);
            suspensionThreshold = Functions.round(monitoringParameterMappingVO.getSuspensionThreshold(), 0);
            if (Integer.parseInt(alertThreshold) <= 0)
            {
              alertThreshold = "";
            }
            if (Integer.parseInt(suspensionThreshold) <= 0)
            {
              suspensionThreshold = "";
            }

            if (MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword()))
            {
              monitoringUnit = "Days";
            }
            else
            {
              monitoringUnit = "Count";
            }
          }

          if ("Y".equals(monitoringParameterMappingVO.getAlertActivation()))
          {
            alertActivation = "checked";
          }
          if ("Y".equals(monitoringParameterMappingVO.getSuspensionActivation()))
          {
            suspensionActivation = "checked";
          }
          if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAdmin()))
          {
            isAlertToAdmin = "checked";
          }
          if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartner()))
          {
            isAlertToPSP = "checked";
          }
          if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchant()))
          {
            isAlertToMerchant = "checked";
          }
          if ("Y".equals(monitoringParameterMappingVO.getIsAlertToAgent()))
          {
            isAlertToAgent = "checked";
          }
      %>
      <tr>
        <td align="center" class="textb">
        <input type="checkbox" name="mappingid_<%=terminalId%>" value="<%=monitoringParameterMappingVO.getMappingId()%>"></td>
        <input type="hidden" name="monitoringParameterId_<%=monitoringParameterMappingVO.getMappingId()%>" value="<%=monitoringParameterVO.getMonitoringParameterId()%>"></td>
        <input type="hidden" name="monitoringUnit_<%=monitoringParameterMappingVO.getMappingId()%>" value="<%=monitoringUnit%>">
        <td align="center" class="textb"><%=monitoringParameterVO.getMonitoringParameterId()%></td>
        <td align="center" class="textb"><%=monitoringParameterVO.getMonitoringParameterName()%></td>
        <td align="center"><input type="checkbox" id="alertActiovationValue_<%=monitoringParameterMappingVO.getMappingId()%>" value="<%=monitoringParameterMappingVO.getAlertActivation()%>"
                                  valign="middle" <%=alertActivation%>
                                  onclick="doChangesForAlertActivation(this,<%=monitoringParameterMappingVO.getMappingId()%>)">
          <input type="hidden" name="alertActiovation_<%=monitoringParameterMappingVO.getMappingId()%>" id="alertActiovation_<%=monitoringParameterMappingVO.getMappingId()%>" value="<%=monitoringParameterMappingVO.getAlertActivation()%>">
        </td>

        <td align="center" class="textb"><input type=text class="txtboxsmall" size=10
                                              name="alertThreshold_<%=monitoringParameterMappingVO.getMappingId()%>"
                                              value="<%=alertThreshold%>"><%=monitoringUnit%>
        </td>

        <td align="center"><input type="checkbox"
                                  id="suspensionActivationValue_<%=monitoringParameterMappingVO.getMappingId()%>"
                                  value="<%=monitoringParameterMappingVO.getSuspensionActivation()%>"
                                  valign="middle" <%=suspensionActivation%>
                                  onclick="doChangesForSuspensionActivation(this,<%=monitoringParameterMappingVO.getMappingId()%>)">

          <input type="hidden" name="suspensionActivation_<%=monitoringParameterMappingVO.getMappingId()%>"
                 id="suspensionActivation_<%=monitoringParameterMappingVO.getMappingId()%>"
                 value="<%=monitoringParameterMappingVO.getSuspensionActivation()%>">
        </td>

        <td align="center" class="textb"><input type=text class="txtboxsmall" size=10
                                              name="suspensionThreshold_<%=monitoringParameterMappingVO.getMappingId()%>"
                                              value="<%=suspensionThreshold%>"><%=monitoringUnit%>
        </td>

        <td align="center"><input type="checkbox"
                                  id="isAlertToAdminValue_<%=monitoringParameterMappingVO.getMappingId()%>"
                                  value="<%=monitoringParameterMappingVO.getIsAlertToAdmin()%>"
                                  valign="middle" <%=isAlertToAdmin%>
                                  onclick="doChangesForAlertToAdmin(this,<%=monitoringParameterMappingVO.getMappingId()%>)">

          <input type="hidden" name="isAlertToAdmin_<%=monitoringParameterMappingVO.getMappingId()%>"
                 id="isAlertToAdmin_<%=monitoringParameterMappingVO.getMappingId()%>"
                 value="<%=monitoringParameterMappingVO.getIsAlertToAdmin()%>">
        </td>

        <td align="center"><input type="checkbox"
                                  id="isAlertToPartnerValue_<%=monitoringParameterMappingVO.getMappingId()%>"
                                  value="<%=monitoringParameterMappingVO.getIsAlertToPartner()%>"
                                  valign="middle" <%=isAlertToPSP%>
                                  onclick="doChangesForAlertToPSP(this,<%=monitoringParameterMappingVO.getMappingId()%>)">

          <input type="hidden" name="isAlertToPartner_<%=monitoringParameterMappingVO.getMappingId()%>"
                 id="isAlertToPartner_<%=monitoringParameterMappingVO.getMappingId()%>"
                 value="<%=monitoringParameterMappingVO.getIsAlertToPartner()%>">
        </td>

        <td align="center"><input type="checkbox"
                                  id="isAlertToMerchantValue_<%=monitoringParameterMappingVO.getMappingId()%>"
                                  value="<%=monitoringParameterMappingVO.getIsAlertToMerchant()%>"
                                  valign="middle" <%=isAlertToMerchant%>
                                  onclick="doChangesForAlertToMerchant(this,<%=monitoringParameterMappingVO.getMappingId()%>)">

          <input type="hidden" name="isAlertToMerchant_<%=monitoringParameterMappingVO.getMappingId()%>"
                 id="isAlertToMerchant_<%=monitoringParameterMappingVO.getMappingId()%>"
                 value="<%=monitoringParameterMappingVO.getIsAlertToMerchant()%>">
        </td>

        <td align="center"><input type="checkbox" name="isAlertToAgent"
                                  id="isAlertToAgentValue_<%=monitoringParameterMappingVO.getMappingId()%>"
                                  value="<%=monitoringParameterMappingVO.getIsAlertToAgent()%>"
                                  valign="middle" <%=isAlertToAgent%>
                                  onclick="doChangesForAlertToAgent(this,<%=monitoringParameterMappingVO.getMappingId()%>)">

          <input type="hidden" name="isAlertToAgent_<%=monitoringParameterMappingVO.getMappingId()%>"
                 id="isAlertToAgent_<%=monitoringParameterMappingVO.getMappingId()%>"
                 value="<%=monitoringParameterMappingVO.getIsAlertToAgent()%>">
        </td>

        <td align="center" class="textb"><%=monitoringParameterVO.getMonitoingFrequency()%>
        </td>
      </tr>
      <%
        }
      %>
      <tr>
        <td colspan="12" align="center"><input id="submit" type="Submit" value="Save Changes" name="submit"
                                               class="buttonform"
                                               onclick="return confirmsubmit2(<%=terminalId%>)"></td>
      </tr>
    </table>
  </form>
  <%
      }
    }
    /*if (errormsg2 != null && ""!=errormsg2)
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
    }*/
    else if (request.getAttribute("updatemsg")!=null)
    {
      out.println("<table align=\"center\"><tr><td><font class=\"textb\"><b>");
      out.println((String)request.getAttribute("updatemsg"));
      out.println("</b></font>");
      out.println("</td></tr></table>");
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