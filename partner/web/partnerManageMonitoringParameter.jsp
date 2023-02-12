<%@ page import="com.directi.pg.Database" %>
<%@ include file="top.jsp"%>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Vishal
  Date: 2/27/2017
  Time: 5:33 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","Risk Rule Mapping");
  Logger logger=new Logger("manageChargeMaster");
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
    String memberid = nullToStr(request.getParameter("memberId"));
    String terminalid = nullToStr(request.getParameter("terminalId"));
    String partnerid = (String) session.getAttribute("merchantid");
    String risk_rule = nullToStr(request.getParameter("monitoringParameterId"));
    String pid="";
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    String Config =" ";
    if(Roles.contains("superpartner")){
      pid=nullToStr(request.getParameter("pid"));
    }else{
      Config = "disabled";
      pid = String.valueOf(session.getAttribute("merchantid"));
    }
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String partnerManageMonitoringParameter_Single_Risk_Rule = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_Single_Risk_Rule")) ? rb1.getString("partnerManageMonitoringParameter_Single_Risk_Rule") : "Single Risk Rule Mapping";
    String partnerManageMonitoringParameter_PartnerID = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_PartnerID")) ? rb1.getString("partnerManageMonitoringParameter_PartnerID") : "Partner ID";
    String partnerManageMonitoringParameter_MerchantID = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_MerchantID")) ? rb1.getString("partnerManageMonitoringParameter_MerchantID") : "Merchant ID*";
    String partnerManageMonitoringParameter_TerminalID = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_TerminalID")) ? rb1.getString("partnerManageMonitoringParameter_TerminalID") : "Terminal ID*";
    String partnerManageMonitoringParameter_Risk_Rule_Name = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_Risk_Rule_Name")) ? rb1.getString("partnerManageMonitoringParameter_Risk_Rule_Name") : "Risk Rule Name* :";
    String partnerManageMonitoringParameter_Risk_Rule_Name1 = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_Risk_Rule_Name1")) ? rb1.getString("partnerManageMonitoringParameter_Risk_Rule_Name1") : "Risk Rule Name";
    String partnerManageMonitoringParameter_Monitoring_Frequency = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_Monitoring_Frequency")) ? rb1.getString("partnerManageMonitoringParameter_Monitoring_Frequency") : "Monitoring Frequency* :";
    String partnerManageMonitoringParameter_Frequency = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_Frequency")) ? rb1.getString("partnerManageMonitoringParameter_Frequency") : "Frequency";
    String partnerManageMonitoringParameter_Daily_alert = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_Daily_alert")) ? rb1.getString("partnerManageMonitoringParameter_Daily_alert") : "Daily Alert Threshold [Contains whole number Or whole number with decimal points]";
    String partnerManageMonitoringParameter_suspension = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_suspension")) ? rb1.getString("partnerManageMonitoringParameter_suspension") : "Daily Suspension Threshold [Contains whole number Or whole number with decimal points]";
    String partnerManageMonitoringParameter_weekly_alert = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_weekly_alert")) ? rb1.getString("partnerManageMonitoringParameter_weekly_alert") : "Weekly Alert Threshold [Contains whole number Or whole number with decimal points]";
    String partnerManageMonitoringParameter_weekly_alert11 = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_weekly_alert11")) ? rb1.getString("partnerManageMonitoringParameter_weekly_alert11") : "Weekly Suspension Threshold [Contains whole number Or whole number with decimal points]";
    String partnerManageMonitoringParameter_monthly_alert = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_monthly_alert")) ? rb1.getString("partnerManageMonitoringParameter_monthly_alert") : "Monthly Alert Threshold [Contains whole number Or whole number with decimal points]";
    String partnerManageMonitoringParameter_monthly_suspension = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_monthly_suspension")) ? rb1.getString("partnerManageMonitoringParameter_monthly_suspension") : "Monthly Suspension Threshold [Contains whole number Or whole number with decimal points]";
    String partnerManageMonitoringParameter_Is_Alert_Merchant = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_Is_Alert_Merchant")) ? rb1.getString("partnerManageMonitoringParameter_Is_Alert_Merchant") : "Is Alert To Merchant :";
    String partnerManageMonitoringParameter_Teams = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_Teams")) ? rb1.getString("partnerManageMonitoringParameter_Teams") : "Teams";
    String partnerManageMonitoringParameter_Sales = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_Sales")) ? rb1.getString("partnerManageMonitoringParameter_Sales") : "Sales";
    String partnerManageMonitoringParameter_RF = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_RF")) ? rb1.getString("partnerManageMonitoringParameter_RF") : "RF";
    String partnerManageMonitoringParameter_CB = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_CB")) ? rb1.getString("partnerManageMonitoringParameter_CB") : "CB";
    String partnerManageMonitoringParameter_Fraud = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_Fraud")) ? rb1.getString("partnerManageMonitoringParameter_Fraud") : "Fraud";
    String partnerManageMonitoringParameter_Tech = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_Tech")) ? rb1.getString("partnerManageMonitoringParameter_Tech") : "Tech";
    String partnerManageMonitoringParameter_Is_Alert_Partner = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_Is_Alert_Partner")) ? rb1.getString("partnerManageMonitoringParameter_Is_Alert_Partner") : "Is Alert To Partner :";
    String partnerManageMonitoringParameter_Alert_Activation = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_Alert_Activation")) ? rb1.getString("partnerManageMonitoringParameter_Alert_Activation") : "Alert Activation :";
    String partnerManageMonitoringParameter_Suspension_Activation = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_Suspension_Activation")) ? rb1.getString("partnerManageMonitoringParameter_Suspension_Activation") : "Suspension Activation :";
    String partnerManageMonitoringParameter_Button = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_Button")) ? rb1.getString("partnerManageMonitoringParameter_Button") : "Button";
    String partnerManageMonitoringParameter_Save = StringUtils.isNotEmpty(rb1.getString("partnerManageMonitoringParameter_Save")) ? rb1.getString("partnerManageMonitoringParameter_Save") : "Save";

%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <%--  <script type="text/javascript" src="/partner/javascript/jquery-1.7.1.js?ver=1"></script>--%>

  <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

  <script language="javascript">
    function showCheckboxesFrequency()
    {
      var checkboxes = document.getElementById("checkboxsfrequency");
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
        document.getElementsByName("alertThreshold")[0].disabled = false;
        document.getElementsByName("suspensionThreshold")[0].disabled = false;
      }
      else
      {
        document.getElementsByName("alertThreshold")[0].disabled = true;
        document.getElementsByName("suspensionThreshold")[0].disabled = true;
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
  <%-- <script type="text/javascript">
     function selectTerminals(ctoken)
     {
       document.f1.action="/partner/partnerManageMonitoringParameter.jsp?ctoken="+ctoken;
       document.f1.submit();
     }
   </script>--%>
  <style>
    /*.multiselect {
      width: 100px;
    }*/
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

    @media (min-width: 768px){
      .form-horizontal .control-label {
        text-align: left!important;
      }
    }

  </style>
  <title><%=company%> | Single Risk Rule Mapping</title>
</head>
<body>
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
    if (!expanded) {
      checkboxes.style.display = "block";
      expanded = true;
    } else {
      checkboxes.style.display = "none";
      expanded = false;
    }
  }
  function showCheckboxesAgent()
  {
    var checkboxes = document.getElementById('checkboxesagent');
    if (!expanded) {
      checkboxes.style.display = "block";
      expanded = true;
    } else {
      checkboxes.style.display = "none";
      expanded = false;
    }
  }
  function showCheckboxesFrequency()
  {
    var checkboxes = document.getElementById("checkboxsfrequency");
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

  /*function mandatory()
  {
    var riskrule = document.getElementById("checkboxsfrequency");
    var memberId = document.getElementById("memberId");
    var terminalId = document.getElementById("terminalId");
    var isdailyexecution = document.getElementById("isdailyexecution");
    var isweeklyexecution = document.getElementById("isweeklyexecution");
    var ismonthlyexecution = document.getElementById("ismonthlyexecution");
    var error = "";

    if (memberId == "") {
      error += "Please select Risk Rule Name \n";
    }

    if (terminalId == "") {
      error += "Please select Risk Rule Name \n";
    }

    if (riskrule == "") {
      error += "Please select Risk Rule Name \n";
    }

    if (isdailyexecution == "" && isweeklyexecution == "" && ismonthlyexecution == "") {
      error += "Please select at least one frequency\n";
    }

    if (error != "") {
      window.alert(error);
    } else {
      document.getElementById("form1").submit();
    }


  }*/

</script>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">


      <br><br><br>
      <div class="row">
        <div class="col-md-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=company%> <%=partnerManageMonitoringParameter_Single_Risk_Rule%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <%
              Functions functions = new Functions();
              String error=(String ) request.getAttribute("error");
              if(functions.isValueNull(error)){
                //out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+msg+"</h5>");
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
              }
              if (request.getAttribute("message") != null)
              {

                out.println(Functions.NewShowConfirmation1("Result", (String) request.getAttribute("message")));
              }

            %>
            <br>
            <form action="/partner/net/PartnerMonitoringParameterMapping?ctoken=<%=ctoken%>" method="post" name="f1" id="f1" class="form-horizontal">
              <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
              <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
              <input type="hidden" value="true" name="isSubmitted">
              <div class="widget-content padding">

                <%--<div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label">Merchant ID* :</label>
                  <div class="col-md-4">
                    <select name="memberId" id="memberId" class="form-control" onchange="selectTerminals('<%=ctoken%>')" style="font-weight: bold;">
                      <option value="">Select Merchant ID</option>
                      <%
                        Connection conn=null;
                        String query=null;
                        PreparedStatement pstmt=null;
                        ResultSet rs=null;
                        try
                        {
                          conn= Database.getConnection();
                          query = "select memberid, company_name from members where partnerId = '"+partnerid+"' ORDER BY memberid ASC";
                          pstmt = conn.prepareStatement( query );
                          rs = pstmt.executeQuery();

                          while (rs.next())
                          {
                            String selection="";
                            if(rs.getString("memberid").equals(memberId))
                            {
                              selection="selected";
                            }
                      %>
                      <option value="<%=rs.getInt("memberid")%>" <%=selection%>><%=rs.getInt("memberid")+"-"+rs.getString("company_name")%></option>;
                      <%
                          }
                        }
                        catch (SystemError se)
                        {
                          logger.error("Exception:::::"+se);
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>--%>

                  <div class="form-group">
                    <div class="col-md-2"></div>
                    <label class="col-md-4 control-label"><%=partnerManageMonitoringParameter_PartnerID%><br>
                    </label>
                    <div class="col-md-4 ui-widget">
                      <%--<input class="form-control" type="Text" maxlength="100"  maxlength = 100 value="" name="memberid" size="35">--%>
                      <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                    </div>
                    <div class="col-md-6"></div>
                  </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnerManageMonitoringParameter_MerchantID%><br>
                  </label>
                  <div class="col-md-4 ui-widget">
                    <%--<input class="form-control" type="Text" maxlength="100"  maxlength = 100 value="" name="memberid" size="35">--%>
                    <input name="memberId" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <%--   <div class="form-group">
                     <div class="col-md-2"></div>
                     <label class="col-md-4 control-label">Terminal ID* :</label>
                     <div class="col-md-4">
                       <select name="terminalId" class="form-control" style="font-weight: bold;">
                         <option value="">Select Terminal ID</option>
                         <%
                           try
                           {
                             conn=Database.getConnection();
                             query = "SELECT terminalid,paymodeid,cardtypeid,memberid FROM member_account_mapping WHERE memberid='"+memberId+"'";
                             pstmt = conn.prepareStatement( query );
                             rs = pstmt.executeQuery();
                             while (rs.next()){

                               String selection="";
                               if(rs.getString("terminalid").equals(terminalId))
                               {
                                 selection="selected";
                               }
                               %>
                                <option value="<%=rs.getInt("terminalid")%>" <%=selection%>><%=rs.getString("memberid")+"-"+rs.getString("terminalid")+"-"+GatewayAccountService.getPaymentMode(rs.getString("paymodeid"))+"-"+GatewayAccountService.getCardType(rs.getString("cardtypeid"))%></option>;
                               <%
                             }
                           }
                           catch (SystemError se)
                           {
                             logger.debug("SystemError::::"+se);
                           }
                           finally
                           {
                             Database.closeConnection(conn);
                           }
                         %>
                       </select>
                     </div>
                     <div class="col-md-6"></div>
                   </div>
   --%>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnerManageMonitoringParameter_TerminalID%><br>
                  </label>
                  <div class="col-md-4 ui-widget">
                    <%--<input class="form-control" type="Text" maxlength="100"  maxlength = 100 value="" name="terminalid" size="35">--%>
                    <input name="terminalId" id="terminalALL" value="<%=terminalid%>" class="form-control" autocomplete="on">
                  </div>
                  <div class="col-md-6"></div>
                </div>


                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnerManageMonitoringParameter_Risk_Rule_Name%></label>
                  <div class="col-md-4">
                    <select name="monitoringParameterId" class="form-control" style="font-weight: bold;" >
                      <option value=""><%=partnerManageMonitoringParameter_Risk_Rule_Name1%></option>
                      <%
                        Connection conn=null;
                        try
                        {
                          String query=null;
                          PreparedStatement pstmt=null;
                          ResultSet rs=null;
                          conn=Database.getConnection();
                          query = "select * from  monitoring_parameter_master";
                          pstmt = conn.prepareStatement( query );
                          rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            out.println("<option value=\""+rs.getString("monitoing_para_id")+"\" "+(risk_rule.equals(rs.getString("monitoing_para_id"))?"Selected":"")+">"+rs.getString("monitoing_para_id")+"- "+rs.getString("monitoing_para_name")+"</option>");
                          }
                        }
                        catch(Exception e)
                        {
                          logger.debug("SystemError::::"+e);
                        }
                        finally
                        {
                          Database.closeConnection(conn);
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnerManageMonitoringParameter_Monitoring_Frequency%></label>
                  <div class="col-md-4">
                    <div class="multiselect" align="center">
                      <div class="selectBox" onclick="showCheckboxesFrequency()">
                        <select class="form-control">
                          <option><%=partnerManageMonitoringParameter_Frequency%></option>
                        </select>
                        <div class="overSelect"></div>
                      </div>
                      <div id="checkboxsfrequency" align="left" class="checkboxes" style="padding-left: 15px; padding-top: 5px;">
                        <label for="isdailyexecution">
                          <input type="checkbox" value="Y" name="isdailyexecution" id="isdailyexecution" onchange="dailyEnableDisableTextBox(this)">Daily
                        </label>
                        <label for="isweeklyexecution">
                          <input type="checkbox" value="Y" name="isweeklyexecution" id="isweeklyexecution" onchange="weeklyEnableDisableTextBox(this)">Weekly
                        </label>
                        <label for="ismonthlyexecution">
                          <input type="checkbox" value="Y" name="ismonthlyexecution" id="ismonthlyexecution" onchange="monthlyEnableDisableTextBox(this)">Monthly
                        </label>
                      </div>
                    </div>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnerManageMonitoringParameter_Daily_alert%></label>
                  <div class="col-md-4">
                    <input class="form-control" maxlength="25" type="text" name="alertThreshold" id="alertThreshold" disabled="disabled">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnerManageMonitoringParameter_suspension%></label>
                  <div class="col-md-4">
                    <input class="form-control" maxlength="25" type="text" name="suspensionThreshold" id="suspensionThreshold" disabled="disabled">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnerManageMonitoringParameter_weekly_alert%></label>
                  <div class="col-md-4">
                    <input class="form-control" maxlength="25" type="text" name="weeklyAlertThreshold" id="weeklyAlertThreshold" disabled="disabled">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnerManageMonitoringParameter_weekly_alert11%></label>
                  <div class="col-md-4">
                    <input class="form-control" maxlength="25" type="text" name="weeklySuspensionThreshold" id="weeklySuspensionThreshold" disabled="disabled">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnerManageMonitoringParameter_monthly_alert%>]</label>
                  <div class="col-md-4">
                    <input class="form-control" maxlength="25" type="text" name="monthlyAlertThreshold" id="monthlyAlertThreshold" disabled="disabled">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnerManageMonitoringParameter_monthly_suspension%></label>
                  <div class="col-md-4">
                    <input class="form-control" maxlength="25" type="text" name="monthlySuspensionThreshold" id="monthlySuspensionThreshold" disabled="disabled">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <%--<div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label">Is Alert To Admin :</label>
                  <div class="col-md-1">
                    <input type="checkbox" style="width: 15px;" class="form-control" name="isAlertToAdmin" value="Y">
                  </div>
                  <div class="col-md-3">
                    <div class="multiselect" align="center">
                      <div class="selectBox" onclick="showCheckboxesAdmin()" >
                        <select  class="form-control" >
                          <option>Teams</option>
                        </select>
                        <div class="overSelect"></div>
                      </div>
                      <div id="checkboxesadmin" align="left" class="checkboxes" style="padding-left: 15px; padding-top: 5px;">
                        <label for="isAlertToAdminSales">
                          <input type="checkbox" value="Y" name="isAlertToAdminSales" id="isAlertToAdminSales"/>Sales
                        </label>
                        <label for="isAlertToAdminRF">
                          <input type="checkbox" value="Y" name="isAlertToAdminRF" id="isAlertToAdminRF"/>RF
                        </label>
                        <label for="isAlertToAdminCB">
                          <input type="checkbox" value="Y" name="isAlertToAdminCB" id="isAlertToAdminCB"/>CB
                        </label>
                        <label for="isAlertToAdminFraud">
                          <input type="checkbox" value="Y" name="isAlertToAdminFraud" id="isAlertToAdminFraud"/>Fraud
                        </label>
                        <label for="isAlertToAdminTech">
                          <input type="checkbox" value="Y" name="isAlertToAdminTech" id="isAlertToAdminTech"/>Tech
                        </label>
                      </div>
                    </div>
                  </div>
                  <div class="col-md-6"></div>
                </div>--%>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnerManageMonitoringParameter_Is_Alert_Merchant%></label>
                  <div class="col-md-1">
                    <input type="checkbox" style="width: 15px;" class="form-control" name="isAlertToMerchant" value="Y">
                  </div>
                  <div class="col-md-3">
                    <div class="multiselect" align="center">
                      <div class="selectBox" onclick="showCheckboxesMerchant()">
                        <select  class="form-control" >
                          <option><%=partnerManageMonitoringParameter_Teams%></option>
                        </select>
                        <div class="overSelect"></div>
                      </div>
                      <div id="checkboxesmerchant" align="left" class="checkboxes" style="padding-left: 15px; padding-top: 5px;">
                        <label for="isAlertToMerchantSales">
                          <input type="checkbox" value="Y" name="isAlertToMerchantSales" id="isAlertToMerchantSales"/><%=partnerManageMonitoringParameter_Sales%>
                        </label>
                        <label for="isAlertToMerchantRF">
                          <input type="checkbox" value="Y" name="isAlertToMerchantRF" id="isAlertToMerchantRF"/><%=partnerManageMonitoringParameter_RF%>
                        </label>
                        <label for="isAlertToMerchantCB">
                          <input type="checkbox" value="Y" name="isAlertToMerchantCB" id="isAlertToMerchantCB"/><%=partnerManageMonitoringParameter_CB%>
                        </label>
                        <label for="isAlertToMerchantFraud">
                          <input type="checkbox" value="Y" name="isAlertToMerchantFraud" id="isAlertToMerchantFraud"/><%=partnerManageMonitoringParameter_Fraud%>
                        </label>
                        <label for="isAlertToMerchantTech">
                          <input type="checkbox" value="Y" name="isAlertToMerchantTech" id="isAlertToMerchantTech"/><%=partnerManageMonitoringParameter_Tech%>
                        </label>
                      </div>
                    </div>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnerManageMonitoringParameter_Is_Alert_Partner%></label>
                  <div class="col-md-1">
                    <input type="checkbox" style="width: 15px;" class="form-control" name="isAlertToPartner" value="Y">
                  </div>
                  <div class="col-md-3">
                    <div class="multiselect" align="center">
                      <div class="selectBox" onclick="showCheckboxesPartner()">
                        <select  class="form-control" >
                          <option><%=partnerManageMonitoringParameter_Teams%></option>
                        </select>
                        <div class="overSelect"></div>
                      </div>
                      <div id="checkboxespartner" align="left" class="checkboxes" style="padding-left: 15px; padding-top: 5px;">
                        <label for="isAlertToPartnerSales">
                          <input type="checkbox" value="Y" name="isAlertToPartnerSales" id="isAlertToPartnerSales"/><%=partnerManageMonitoringParameter_Sales%>
                        </label>
                        <label for="isAlertToPartnerRF">
                          <input type="checkbox" value="Y" name="isAlertToPartnerRF" id="isAlertToPartnerRF"/><%=partnerManageMonitoringParameter_RF%>
                        </label>
                        <label for="isAlertToPartnerCB">
                          <input type="checkbox" value="Y" name="isAlertToPartnerCB" id="isAlertToPartnerCB"/><%=partnerManageMonitoringParameter_CB%>
                        </label>
                        <label for="isAlertToPartnerFraud">
                          <input type="checkbox" value="Y" name="isAlertToPartnerFraud" id="isAlertToPartnerFraud"/><%=partnerManageMonitoringParameter_Fraud%>
                        </label>
                        <label for="isAlertToPartnerTech">
                          <input type="checkbox" value="Y" name="isAlertToPartnerTech" id="isAlertToPartnerTech"/><%=partnerManageMonitoringParameter_Tech%>
                        </label>
                      </div>
                    </div>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <%--<div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label">Is Alert To Agent :</label>
                  <div class="col-md-1">
                    <input type="checkbox" style="width: 15px;" class="form-control" name="isAlertToAgent" value="Y">
                  </div>
                  <div class="col-md-3">
                    <div class="multiselect" align="center">
                      <div class="selectBox" onclick="showCheckboxesAgent()">
                        <select  class="form-control" >
                          <option>Teams</option>
                        </select>
                        <div class="overSelect"></div>
                      </div>
                      <div id="checkboxesagent" align="left" class="checkboxes" style="padding-left: 15px; padding-top: 5px;">
                        <label for="isAlertToAgentSales">
                          <input type="checkbox" value="Y" name="isAlertToAgentSales" id="isAlertToAgentSales"/>Sales
                        </label>
                        <label for="isAlertToAgentRF">
                          <input type="checkbox" value="Y" name="isAlertToAgentRF" id="isAlertToAgentRF"/>RF
                        </label>
                        <label for="isAlertToAgentCB">
                          <input type="checkbox" value="Y" name="isAlertToAgentCB" id="isAlertToAgentCB"/>CB
                        </label>
                        <label for="isAlertToAgentFraud">
                          <input type="checkbox" value="Y" name="isAlertToAgentFraud" id="isAlertToAgentFraud"/>Fraud
                        </label>
                        <label for="isAlertToAgentTech">
                          <input type="checkbox" value="Y" name="isAlertToAgentTech" id="isAlertToAgentTech"/>Tech
                        </label>
                      </div>
                    </div>
                  </div>
                  <div class="col-md-6"></div>
                </div>--%>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnerManageMonitoringParameter_Alert_Activation%></label>
                  <div class="col-md-1">
                    <input type="checkbox" style="width: 15px;" class="form-control" name="alertActivation" value="Y">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnerManageMonitoringParameter_Suspension_Activation%></label>
                  <div class="col-md-1">
                    <input type="checkbox" style="width: 15px;" class="form-control" name="suspensionActivation" value="Y">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-3"></div>
                  <label class="col-md-3 control-label" style="visibility: hidden;"><%=partnerManageMonitoringParameter_Button%></label>
                  <div class="col-md-4">
                    <input type="hidden" value="1" name="step">
                    <button type="submit" class="btn btn-default" id="save" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=partnerManageMonitoringParameter_Save%></button>
                  </div>
                  <div class="col-md-6"></div>
                </div>

              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

  </div>
</div>
<%
    /*  if (request.getAttribute("message") != null)
      {

            out.println(Functions.NewShowConfirmation1("Result", (String) request.getAttribute("message")));
          }*/
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