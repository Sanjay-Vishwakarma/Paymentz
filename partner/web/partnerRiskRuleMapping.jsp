<%@ include file="top.jsp"%>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.merchantmonitoring.MonitoringParameterMappingVO" %>
<%@ page import="com.manager.vo.merchantmonitoring.MonitoringParameterVO" %>
<%@ page import="com.manager.vo.merchantmonitoring.enums.MonitoringKeyword" %>
<%@ page import="com.manager.vo.merchantmonitoring.enums.MonitoringSubKeyword" %>
<%@ page import="com.manager.vo.merchantmonitoring.enums.MonitoringUnit" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Vishal
  Date: 2/27/2017
  Time: 4:04 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","Risk Rule Mapping");
  String partnerid = (String) session.getAttribute("merchantid");
  Functions functions = new Functions();
  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String partnerRiskRuleMapping_Risk_Rule_Mapping = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Risk_Rule_Mapping")) ? rb1.getString("partnerRiskRuleMapping_Risk_Rule_Mapping") : "Risk Rule Mapping";
  String partnerRiskRuleMapping_PartnerID = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_PartnerID")) ? rb1.getString("partnerRiskRuleMapping_PartnerID") : "Partner ID";
  String partnerRiskRuleMapping_MerchantID = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_MerchantID")) ? rb1.getString("partnerRiskRuleMapping_MerchantID") : "Merchant ID*";
  String partnerRiskRuleMapping_TerminalID = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_TerminalID")) ? rb1.getString("partnerRiskRuleMapping_TerminalID") : "Terminal ID*";
  String partnerRiskRuleMapping_Path = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Path")) ? rb1.getString("partnerRiskRuleMapping_Path") : "Path";
  String partnerRiskRuleMapping_Search = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Search")) ? rb1.getString("partnerRiskRuleMapping_Search") : "Search";
  String partnerRiskRuleMapping_Report_Table = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Report_Table")) ? rb1.getString("partnerRiskRuleMapping_Report_Table") : "Report Table";
  String partnerRiskRuleMapping_Merchant_ID = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Merchant_ID")) ? rb1.getString("partnerRiskRuleMapping_Merchant_ID") : "Merchant ID :";
  String partnerRiskRuleMapping_Terminal_Id = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Terminal_Id")) ? rb1.getString("partnerRiskRuleMapping_Terminal_Id") : "Terminal Id :";
  String partnerRiskRuleMapping_Check = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Check")) ? rb1.getString("partnerRiskRuleMapping_Check") : "Check";
  String partnerRiskRuleMapping_Risk_Rule_Detail = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Risk_Rule_Detail")) ? rb1.getString("partnerRiskRuleMapping_Risk_Rule_Detail") : "Risk Rule Detail";
  String partnerRiskRuleMapping_Frequency = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Frequency")) ? rb1.getString("partnerRiskRuleMapping_Frequency") : "Frequency";
  String partnerRiskRuleMapping_AlertSetting = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_AlertSetting")) ? rb1.getString("partnerRiskRuleMapping_AlertSetting") : "Alert Setting";
  String partnerRiskRuleMapping_Suspension_Setting = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Suspension_Setting")) ? rb1.getString("partnerRiskRuleMapping_Suspension_Setting") : "Suspension Setting";
  String partnerRiskRuleMapping_Email_Notification = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Email_Notification")) ? rb1.getString("partnerRiskRuleMapping_Email_Notification") : "E-mail Notification";
  String partnerRiskRuleMapping_Risk_ID = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Risk_ID")) ? rb1.getString("partnerRiskRuleMapping_Risk_ID") : "Risk ID";
  String partnerRiskRuleMapping_Risk_Name = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Risk_Name")) ? rb1.getString("partnerRiskRuleMapping_Risk_Name") : "Risk Name";
  String partnerRiskRuleMapping_Threshold = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Threshold")) ? rb1.getString("partnerRiskRuleMapping_Threshold") : "Threshold";
  String partnerRiskRuleMapping_Merchant = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Merchant")) ? rb1.getString("partnerRiskRuleMapping_Merchant") : "Merchant";
  String partnerRiskRuleMapping_Partner = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Partner")) ? rb1.getString("partnerRiskRuleMapping_Partner") : "Partner";
  String partnerRiskRuleMapping_Daily = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Daily")) ? rb1.getString("partnerRiskRuleMapping_Daily") : "Daily";
  String partnerRiskRuleMapping_Weekly = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Weekly")) ? rb1.getString("partnerRiskRuleMapping_Weekly") : "Weekly";
  String partnerRiskRuleMapping_Monthly = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Monthly")) ? rb1.getString("partnerRiskRuleMapping_Monthly") : "Monthly";
  String partnerRiskRuleMapping_Teams = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Teams")) ? rb1.getString("partnerRiskRuleMapping_Teams") : "Teams";
  String partnerRiskRuleMapping_Sales = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Sales")) ? rb1.getString("partnerRiskRuleMapping_Sales") : "Sales";
  String partnerRiskRuleMapping_RF = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_RF")) ? rb1.getString("partnerRiskRuleMapping_RF") : "RF";
  String partnerRiskRuleMapping_CB = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_CB")) ? rb1.getString("partnerRiskRuleMapping_CB") : "CB";
  String partnerRiskRuleMapping_Fraud = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Fraud")) ? rb1.getString("partnerRiskRuleMapping_Fraud") : "Fraud";
  String partnerRiskRuleMapping_Tech = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Tech")) ? rb1.getString("partnerRiskRuleMapping_Tech") : "Tech";
  String partnerRiskRuleMapping_Sorry = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_Sorry")) ? rb1.getString("partnerRiskRuleMapping_Sorry") : "Sorry";
  String partnerRiskRuleMapping_No_Records = StringUtils.isNotEmpty(rb1.getString("partnerRiskRuleMapping_No_Records")) ? rb1.getString("partnerRiskRuleMapping_No_Records") : "No Records Found.";



%>
<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">
  <title><%=company%> Merchant Monitoring> Risk Rule Mapping</title>
  <style type="text/css">
    @media(max-width: 991px) {
      .additional-btn {
        float: left;
        margin-left: 30px;
        margin-top: 10px;
        position: inherit!important;
      }
    }
  </style>
  <style>
    .multiselect {
      width: 115px;
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
    .input-group-addon, .input-group-btn{width: inherit!important;}
    .input-group > .form-control{width:inherit;}
    @media (max-width: 640px){
      td > b {display: none;}
      .icheckbox_square-aero{border: 1px solid #749096;}
      .input-group > .form-control{width:100%;}
    }
  </style>
  <style type="text/css">
    #ui-id-2
    {
      overflow: auto;
      max-height: 350px;
    }
  </style>
  <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <script type='text/javascript' src='/partner/javascript/merchantmonitroingconfig.js'></script>
  <script type="">
    $(document).ready(function() {
      removejscssfile("icheck.min.js", "js") //remove all occurences of "somescript.js" on page
    });
    function removejscssfile(filename, filetype){
      var targetelement=(filetype=="js")? "script" : (filetype=="css")? "link" : "none" //determine element type to create nodelist from
      var targetattr=(filetype=="js")? "src" : (filetype=="css")? "href" : "none" //determine corresponding attribute to test for
      var allsuspects=document.getElementsByTagName(targetelement)
      for (var i=allsuspects.length; i>=0; i--){ //search backwards within nodelist for matching elements to remove
        if (allsuspects[i] && allsuspects[i].getAttribute(targetattr)!=null && allsuspects[i].getAttribute(targetattr).indexOf(filename)!=-1)
          allsuspects[i].parentNode.removeChild(allsuspects[i]) //remove element by calling parentNode.removeChild()
      }
    }
    function check(checkbox)
    {
      if (checkbox.checked)
      {
        // Iterate each checkbox
        $('.togglecheck').each(function ()
        {
          this.checked = true;
        });
      }
      else
      {
        $('.togglecheck').each(function ()
        {
          this.checked = false;
        });
      }
    }
  </script>
</head>
<title> Merchant Threshold </title>
<body>
<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  Logger logger=new Logger("manageChargeMaster");
  if (partner.isLoggedInPartner(session))
  {
    String memberId = nullToStr(request.getParameter("memberid"));
    String terminalid = nullToStr(request.getParameter("terminalid"));
    String pid="";
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    String Config =" ";
    if(Roles.contains("superpartner")){
      pid=nullToStr(request.getParameter("pid"));
    }else{
      Config = "disabled";
      pid = String.valueOf(session.getAttribute("merchantid"));
    }
%>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/partnerManageMonitoringParameter.jsp?ctoken=<%=ctoken%>" method="POST">
            <button class="btn-xs" type="submit" value="Risk Rule Mapping" name="submit" name="B1" style="background: transparent;border: 0;">
              <img style="height: 45px;width: 200px;" src="/partner/images/SingleRiskRuleMapping.png">
            </button>
          </form>
        </div>
      </div>
      <br>
      <br>
      <br>
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> <%=partnerRiskRuleMapping_Risk_Rule_Mapping%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <div id="horizontal-form">
                <form action="/partner/net/PartnerRiskRuleList?ctoken=<%=ctoken%>" method="post" name="f1">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                  <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                  <%

                    String str="ctoken=" + ctoken;
                    if (request.getParameter("memberid") != null) str = str + "&memberid=" + request.getParameter("memberid");
                    int pageno = PartnerFunctions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
                    int pagerecords = PartnerFunctions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
                    if(request.getAttribute("error")!=null)
                    {
                      String message = (String) request.getAttribute("error");
                      if(message != null)
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                    }
                    if(request.getAttribute("success")!=null)
                    {
                      String success = (String) request.getAttribute("success");
                      if(success != null)
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + success + "</h5>");
                    }
                  %>
                  <div class="form-group col-md-4 has-feedback">

                    <div class="ui-widget">
                      <label for="pid"><%=partnerRiskRuleMapping_PartnerID%></label>
                      <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                    </div>
                    </div>

                    <div class="form-group col-md-4 has-feedback">

                    <div class="ui-widget">
                      <label for="member"><%=partnerRiskRuleMapping_MerchantID%></label>
                      <input name="memberid" id="member" value="<%=memberId%>" class="form-control" autocomplete="on">
                    </div>

                  <%--<label >Merchant ID*</label>
                    <select name="memberid" class="form-control" onchange="selectTerminals(this,'<%=ctoken%>')">
                      <option value="" selected>Select Merchant ID</option>
                      <%
                        Connection conn=null;
                        PreparedStatement pstmt = null;
                        ResultSet rs = null;
                        try
                        {
                          conn= Database.getConnection();
                          String query = "select memberid, company_name from members where partnerId = '"+partnerid+"' ORDER BY memberid ASC";
                          pstmt = conn.prepareStatement(query);
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
                    </select>--%>
                  </div>
                  <div class="form-group col-md-4 has-feedback">

                    <div class="ui-widget">
                      <label ><%=partnerRiskRuleMapping_TerminalID%></label>
                      <input name="terminalid" id="terminalALL" value="<%=terminalid%>" class="form-control" autocomplete="on">
                    </div>
                  </div>
                   <%-- <label>Terminal ID*</label>
                    <select name="terminalid" class="form-control">
                      <option value="" selected>Select Terminal ID</option>
                      <%
                        try
                        {
                          conn = Database.getConnection();
                          String query = "SELECT mam.terminalid,mam.paymodeid,mam.cardtypeid,mam.memberid,mam.isActive,mam.accountid,gtype.currency FROM member_account_mapping AS mam JOIN gateway_accounts AS gaccount ON mam.accountid=gaccount.accountid JOIN gateway_type AS gtype ON gaccount.pgtypeid=gtype.pgtypeid WHERE mam.memberid='"+memberId+"'";
                          pstmt = conn.prepareStatement(query);
                          rs = pstmt.executeQuery();
                          while (rs.next())
                          {
                            String selection="";
                            if(rs.getString("terminalid").equals(terminalid))
                            {
                              selection="selected";
                            }
                            String active="";
                            if (rs.getString("isActive").equalsIgnoreCase("Y"))
                            {
                              active = "Active";
                            }
                            else
                            {
                              active = "InActive";
                            }
                      %>
                      <option value="<%=rs.getInt("terminalid")%>" <%=selection%>><%=rs.getString("terminalid") + "-" + GatewayAccountService.getPaymentMode(rs.getString("paymodeid")) + "-" + GatewayAccountService.getCardType(rs.getString("cardtypeid")) + "-" + rs.getString("currency") + "-" +active%></option>;
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
                  <div class="form-group col-md-4">
                    <label style="color: transparent;"><%=partnerRiskRuleMapping_Path%></label>
                    <button type="submit" class="btn btn-default" style="display:block;">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;<%=partnerRiskRuleMapping_Search%>
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
      <%
        Map<String, List<MonitoringParameterMappingVO>> stringListMap = (Map) request.getAttribute("stringListMap");
        TerminalManager terminalManager = new TerminalManager();
        if (stringListMap != null && stringListMap.size() > 0)
        {
          Set set = stringListMap.keySet();
          Iterator iterator = set.iterator();
          while (iterator.hasNext())
          {
            String terminalIdString = (String) iterator.next();
            String[] terminalIdStringArr=terminalIdString.split(":");
            String terminalId=terminalIdStringArr[0];
            String settings=terminalIdStringArr[1];

            TerminalVO terminalVO = terminalManager.getActiveInActiveTerminalInfo(terminalId);
            GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
            String currency = gatewayAccount.getCurrency();
            String bank = gatewayAccount.getGateway();
            String cardType = terminalVO.getCardType();
            String isActive = terminalVO.getIsActive();
            String status = "";
            if ("Y".equals(isActive)){
              status = "Active";
            }
            else{
              status = "Inactive";
            }
            String toggleVisibility="";
            String toggleSelection="";

            if("default".equals(settings))
            {
              toggleVisibility="disabled";
              toggleSelection="checked";
            }
            List<MonitoringParameterMappingVO> monitoringParameterMappingVOs = stringListMap.get(terminalIdString);
      %>
      <form name="update" id="details_<%=terminalId%>" action="/partner/net/PartnerRiskRuleMapping?ctoken=<%=ctoken%>"
            method=post>
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <input type=hidden value="<%=terminalVO.getAccountId()%>" name="accountid">
        <input type=hidden value="<%=terminalVO.getTerminalId()%>" name="terminalid">
        <input type=hidden value="<%=terminalVO.getMemberId()%>" name="memberid">
        <input type=hidden value="UPDATE" name="action">
        <div class="row reporttable">
          <div class="col-md-12">
            <div class="widget">
              <div class="widget-header">
                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerRiskRuleMapping_Report_Table%></strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>
              <div class="widget-content padding" style="overflow-y: auto;">
                <table id="myTable" class="display table table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                  <thead style="display: table-header-group!important;">
                  <tr style="background-color: #7eccad !important; color: #ffffff; font-weight: bold;">
                    <td valign="middle" align="center" class="th0" colspan="8" style="/* border-right: 1px solid #ffffff;*/"><font size=2> <%=partnerRiskRuleMapping_Merchant_ID%> <%=memberId%> <%--<%=bank%> - <%=currency%> - <%=cardType%>--%></font></td>
                    <td valign="middle" align="center" class="th0" colspan="4" style="border-left-style: hidden;border-right: 1px solid #ffffff;">
                      <font size=2><%=partnerRiskRuleMapping_Terminal_Id%> <%=terminalVO.toString()%> - <%=currency%>  <%=status%></font>
                    </td>
                  </tr>
                  </thead>
                  <thead>
                  <tr>
                    <td  width="30%" class="textb" valign="middle" align="center" rowspan="1" style="background-color: #7eccad; color: #ffffff; "><b><%=partnerRiskRuleMapping_Check%></b></td>
                    <td  width="30%" class="textb" valign="middle" align="center" colspan="2" style="background-color: #7eccad; color: #ffffff; "><b><%=partnerRiskRuleMapping_Risk_Rule_Detail%></b></td>
                    <td width="2%" class="textb" valign="middle" align="center" rowspan="1" style="background-color: #7eccad; color: #ffffff; "><b><%=partnerRiskRuleMapping_Frequency%></b></td>
                    <td  width="17%" class="textb" valign="middle" align="center" colspan="2" style="background-color: #7eccad; color: #ffffff; "><b><%=partnerRiskRuleMapping_AlertSetting%></b></td>
                    <td  width="17%"  class="textb" valign="middle" align="center" colspan="2" style="background-color: #7eccad; color: #ffffff; "><b><%=partnerRiskRuleMapping_Suspension_Setting%></b></td>
                    <td  width="17%" class="textb" valign="middle" align="center" colspan="4" style="background-color: #7eccad; color: #ffffff; "><b><%=partnerRiskRuleMapping_Email_Notification%></b></td>
                  </tr>
                  <tr>
                    <td class="textb" valign="middle" align="center" rowspan="2" style="background-color: #c0c7c7;color: #ffffff;">
                      <input type="checkbox" id="alltrans" onclick="check(this)" name="alltrans" <%=toggleSelection%>></td>
                    <%--<td class="textb" valign="middle" align="center" style="background-color: #c0c7c7;color: #ffffff;"><b>&nbsp;</b></td>--%>
                    <td class="textb" valign="middle" align="center" style="background-color: #c0c7c7;color: #ffffff;" rowspan="2"><b><%=partnerRiskRuleMapping_Risk_ID%></b></td>
                    <td class="textb" valign="middle" align="center" style="background-color: #c0c7c7;color: #ffffff;" rowspan="2"><b><%=partnerRiskRuleMapping_Risk_Name%></b></td>
                    <td class="textb" valign="middle" align="center" style="background-color: #c0c7c7;color: #ffffff;"><b>&nbsp;</b></td>
                    <td class="textb" valign="middle" align="center" style="background-color: #c0c7c7;color: #ffffff;" rowspan="3" colspan="2"><b><%=partnerRiskRuleMapping_Threshold%><br>(DL,WL,ML)</b></td>
                    <td class="textb" valign="middle" align="center" style="background-color: #c0c7c7;color: #ffffff;" rowspan="3" colspan="2"<%-- style="border-right: 1px solid #dddddd;"--%>><b>Threshold<br>(DL,WL,ML)</b></td>
                    <td class="textb" valign="middle" align="center" style="background-color: #c0c7c7;color: #ffffff;" colspan="2"><b><%=partnerRiskRuleMapping_Merchant%></b></td>
                    <td class="textb" valign="middle" align="center" style="background-color: #c0c7c7;color: #ffffff;" colspan="2"><b><%=partnerRiskRuleMapping_Partner%></b></td>
                  </tr>
                  </thead>
                  <%
                    for (MonitoringParameterMappingVO monitoringParameterMappingVO : monitoringParameterMappingVOs){
                      MonitoringParameterVO monitoringParameterVO = monitoringParameterMappingVO.getMonitoringParameterVO();
                      String alertActivation = "";
                      String suspensionActivation = "";
                      String isAlertToMerchant = "";
                      String isAlertToMerchantSales = "";
                      String isAlertToMerchantRF = "";
                      String isAlertToMerchantCB = "";
                      String isAlertToMerchantFraud = "";
                      String isAlertToMerchantTech = "";
                      String isAlertToPSP = "";
                      String isAlertToPSPSales = "";
                      String isAlertToPSPRF = "";
                      String isAlertToPSPCB = "";
                      String isAlertToPSPFraud = "";
                      String isAlertToPSPTech = "";
                      String monitoringUnit = "";
                      String alertThreshold = "0";
                      String weeklyThreshold = "0";
                      String monthlyThreshold = "0";
                      String suspensionThreshold = "0";
                      String weeklySuspensionThreshold = "0";
                      String monthlySuspensionThreshold = "0";
                      String ruleVisibility="";
                      String isdailyexecution="";
                      String isweeklyexecution="";
                      String ismonthlyexecution="";
                      String dailyThresholdVisibility="disabled";
                      String weeklyThresholdVisibility="disabled";
                      String monthlyThresholdVisibility="disabled";
                      if (MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit()) || MonitoringKeyword.HighAmountOrder.toString().equals(monitoringParameterVO.getMonitoingKeyword())){
                        alertThreshold = Functions.round(monitoringParameterMappingVO.getAlertThreshold(), 2);
                        weeklyThreshold = Functions.round(monitoringParameterMappingVO.getWeeklyAlertThreshold(), 2);
                        monthlyThreshold = Functions.round(monitoringParameterMappingVO.getMonthlyAlertThreshold(), 2);
                        suspensionThreshold = Functions.round(monitoringParameterMappingVO.getSuspensionThreshold(), 2);
                        weeklySuspensionThreshold = Functions.round(monitoringParameterMappingVO.getWeeklySuspensionThreshold(), 2);
                        monthlySuspensionThreshold = Functions.round(monitoringParameterMappingVO.getMonthlySuspensionThreshold(), 2);
                        if(MonitoringUnit.Percentage.toString().equals(monitoringParameterVO.getMonitoringUnit())){
                          monitoringUnit = "%";
                        }
                        else{
                          monitoringUnit = currency;
                        }
                      }
                      else{
                        alertThreshold = Functions.round(monitoringParameterMappingVO.getAlertThreshold(), 0);
                        weeklyThreshold = Functions.round(monitoringParameterMappingVO.getWeeklyAlertThreshold(), 0);
                        monthlyThreshold = Functions.round(monitoringParameterMappingVO.getMonthlyAlertThreshold(), 0);
                        suspensionThreshold = Functions.round(monitoringParameterMappingVO.getSuspensionThreshold(), 0);
                        weeklySuspensionThreshold = Functions.round(monitoringParameterMappingVO.getWeeklySuspensionThreshold(), 0);
                        monthlySuspensionThreshold = Functions.round(monitoringParameterMappingVO.getMonthlySuspensionThreshold(), 0);
                        if (MonitoringSubKeyword.Day.toString().equals(monitoringParameterVO.getMonitoingSubKeyword())){
                          monitoringUnit = "Day";
                        }
                        else if(MonitoringSubKeyword.Count.toString().equals(monitoringParameterVO.getMonitoingSubKeyword())){
                          monitoringUnit = "Cnt";
                        }
                      }
                      if ("Y".equals(monitoringParameterMappingVO.getAlertActivation())){
                        alertActivation = "checked";
                      }
                      if ("Y".equals(monitoringParameterMappingVO.getSuspensionActivation())){
                        suspensionActivation = "checked";
                      }
                      if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchant())){
                        isAlertToMerchant = "checked";
                      }
                      if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantSales())){
                        isAlertToMerchantSales = "checked";
                      }
                      if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantRF())){
                        isAlertToMerchantRF = "checked";
                      }
                      if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantCB())){
                        isAlertToMerchantCB = "checked";
                      }
                      if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantFraud())){
                        isAlertToMerchantFraud = "checked";
                      }
                      if ("Y".equals(monitoringParameterMappingVO.getIsAlertToMerchantTech())){
                        isAlertToMerchantTech = "checked";
                      }
                      if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartner())){
                        isAlertToPSP = "checked";
                      }
                      if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerSales())){
                        isAlertToPSPSales = "checked";
                      }
                      if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerRF())){
                        isAlertToPSPRF = "checked";
                      }
                      if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerCB())){
                        isAlertToPSPCB = "checked";
                      }
                      if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerFraud())){
                        isAlertToPSPFraud = "checked";
                      }
                      if ("Y".equals(monitoringParameterMappingVO.getIsAlertToPartnerTech())){
                        isAlertToPSPTech = "checked";
                      }
                      if("disabled".equals(toggleVisibility)){
                        ruleVisibility="checked";
                      }
                      if("Y".equals(monitoringParameterMappingVO.getIsDailyExecution())){
                        isdailyexecution = "checked";
                        dailyThresholdVisibility="";
                      }
                      if("Y".equals(monitoringParameterMappingVO.getIsWeeklyExecution())){
                        isweeklyexecution = "checked";
                        weeklyThresholdVisibility="";
                      }
                      if("Y".equals(monitoringParameterMappingVO.getIsMonthlyExecution())){
                        ismonthlyexecution = "checked";
                        monthlyThresholdVisibility="";
                      }
                  %>
                  <tr>
                    <td align="center" class="textb" data-label="Check">
                      <input type="checkbox" class = "togglecheck" name="mappingid_<%=terminalId%>" value="<%=monitoringParameterVO.getMonitoringParameterId()%>" <%=ruleVisibility%>>
                      <input type="hidden" name="<%=terminalId%>_monitoringParameterId_<%=monitoringParameterVO.getMonitoringParameterId()%>" value="<%=monitoringParameterVO.getMonitoringParameterId()%>">
                      <input type="hidden" name="<%=terminalId%>_monitoringUnit_<%=monitoringParameterVO.getMonitoringParameterId()%>" value="<%=monitoringUnit%>">
                      <input type="hidden" name="<%=terminalId%>_alertMessage_<%=monitoringParameterVO.getMonitoringParameterId()%>" value="<%=monitoringParameterMappingVO.getAlertMessage()%>">
                      <input type="hidden" name="<%=terminalId%>_isAlertToAdmin_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             id="<%=terminalId%>_isAlertToAdmin_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monitoringParameterMappingVO.getIsAlertToAdmin()%>">
                      <input type="hidden" name="<%=terminalId%>_isAlertToAdminSales_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             id="<%=terminalId%>_isAlertToAdminSales_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monitoringParameterMappingVO.getIsAlertToAdminSales()%>">
                      <input type="hidden" name="<%=terminalId%>_isAlertToAdminRF_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             id="<%=terminalId%>_isAlertToAdminRF_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monitoringParameterMappingVO.getIsAlertToAdminRF()%>">
                      <input type="hidden" name="<%=terminalId%>_isAlertToAdminCB_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             id="<%=terminalId%>_isAlertToAdminCB_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monitoringParameterMappingVO.getIsAlertToAdminCB()%>">
                      <input type="hidden" name="<%=terminalId%>_isAlertToAdminFraud_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             id="<%=terminalId%>_isAlertToAdminFraud_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monitoringParameterMappingVO.getIsAlertToAdminFraud()%>">
                      <input type="hidden" name="<%=terminalId%>_isAlertToAdminTech_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             id="<%=terminalId%>_isAlertToAdminTech_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monitoringParameterMappingVO.getIsAlertToAdminTech()%>">
                      <input type="hidden" name="<%=terminalId%>_isAlertToAgent_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             id="<%=terminalId%>_isAlertToAgent_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monitoringParameterMappingVO.getIsAlertToAgent()%>">
                      <input type="hidden" name="<%=terminalId%>_isAlertToAgentSales_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             id="<%=terminalId%>_isAlertToAgentSales_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monitoringParameterMappingVO.getIsAlertToAgentSales()%>">
                      <input type="hidden" name="<%=terminalId%>_isAlertToAgentRF_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             id="<%=terminalId%>_isAlertToAgentRF_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monitoringParameterMappingVO.getIsAlertToAgentRF()%>">
                      <input type="hidden" name="<%=terminalId%>_isAlertToAgentCB_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             id="<%=terminalId%>_isAlertToAgentCB_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monitoringParameterMappingVO.getIsAlertToAgentCB()%>">
                      <input type="hidden" name="<%=terminalId%>_isAlertToAgentFraud_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             id="<%=terminalId%>_isAlertToAgentFraud_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monitoringParameterMappingVO.getIsAlertToAgentFraud()%>">
                      <input type="hidden" name="<%=terminalId%>_isAlertToAgentTech_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             id="<%=terminalId%>_isAlertToAgentTech_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monitoringParameterMappingVO.getIsAlertToAgentTech()%>">
                    </td>
                    <td align="center" data-label="Risk Rule Detail - Risk ID" class="textb"><button type="submit" class="goto"
                                                                                                     onclick="window.open('PartnerRiskRuleDetails?ruleid=<%=monitoringParameterVO.getMonitoringParameterId()%>&ctoken=<%=user.getCSRFToken()%>', 'newwindow', 'width=500, height=350'); return false;"> <%=monitoringParameterVO.getMonitoringParameterId()%></button></td>
                    <td align="center" data-label="Risk Rule Detail - Risk Name" class="textb"><%=monitoringParameterVO.getMonitoringParameterName()%></td>
                    <td align="center" class="textb" data-label="Frequency">
                      <div class="multiselect" align="center">
                        <div class="selectBox" onclick="showCheckboxesFrequency(<%=terminalId%>,<%=monitoringParameterVO.getMonitoringParameterId()%>)">
                          <select  class="textb form-control" onchange="doChangeFrequency(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">
                            <option><%=partnerRiskRuleMapping_Frequency%></option>
                          </select>
                          <div class="overSelect"></div>
                        </div>
                        <div id="<%=terminalId%>_checkboxesfrequency_<%=monitoringParameterVO.getMonitoringParameterId()%>" align="left" class="checkboxes" style="padding-left: 15px; padding-top: 5px; background-color: #ffffff;">
                          <label for="<%=terminalId%>_isdailyexecutionvalue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                            <input type="checkbox" align="left"
                                   id="<%=terminalId%>_isdailyexecutionvalue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsDailyExecution()%>"
                                   valign="middle" <%=isdailyexecution%>
                                   onclick="doChangesForDailyExecution(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)"
                                   name="<%=terminalId%>_isdailyexecution_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   onchange="dailyEnableDisableTextBox(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">
                            <input type="hidden" name="<%=terminalId%>_isdailyexecution_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   id="<%=terminalId%>_isdailyexecution_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsDailyExecution()%>">
                            <%=partnerRiskRuleMapping_Daily%>
                          </label>
                          <label for="<%=terminalId%>_isweeklyexecutionvalue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                            <input type="checkbox" align="left"
                                   id="<%=terminalId%>_isweeklyexecutionvalue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsWeeklyExecution()%>"
                                   valign="middle" <%=isweeklyexecution%>
                                   onclick="doChangesForWeeklyExecution(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)"
                                   name="<%=terminalId%>_isweeklyexecution_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   onchange="weeklyEnableDisableTextBox(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">
                            <input type="hidden" name="<%=terminalId%>_isweeklyexecution_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   id="<%=terminalId%>_isweeklyexecution_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsWeeklyExecution()%>">
                           <%=partnerRiskRuleMapping_Weekly%>
                          </label>
                          <label for="<%=terminalId%>_ismonthlyexecutionvalue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                            <input type="checkbox" align="left"
                                   id="<%=terminalId%>_ismonthlyexecutionvalue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsMonthlyExecution()%>"
                                   valign="middle" <%=ismonthlyexecution%>
                                   onclick="doChangesForMonthlyExecution(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)"
                                   name="<%=terminalId%>_ismonthlyexecution_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   onchange="monthlyEnableDisableTextBox(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">
                            <input type="hidden" name="<%=terminalId%>_ismonthlyexecution_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   id="<%=terminalId%>_ismonthlyexecution_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsMonthlyExecution()%>">
                           <%=partnerRiskRuleMapping_Monthly%>
                          </label>
                        </div>
                      </div>
                    </td>
                    <td align="center" class="textb" data-label="Alert Setting - Threshold (DL,WL,ML)">
                      <input type="checkbox"
                             id="<%=terminalId%>_alertActiovationValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monitoringParameterMappingVO.getAlertActivation()%>"
                             valign="middle" <%=alertActivation%>
                             onclick="doChangesForAlertActivation(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">
                      <input type="hidden" name="<%=terminalId%>_alertActiovation_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             id="<%=terminalId%>_alertActiovation_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monitoringParameterMappingVO.getAlertActivation()%>">
                    </td>
                    <td align="center" class="textb" data-label="<%=monitoringUnit%>">
                      <div class="input-group">
                        <input type=text class="form-control" size=5 name="<%=terminalId%>_alertThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>" value="<%=alertThreshold%>" <%=dailyThresholdVisibility%>>
                        <span class="input-group-addon" style="font-weight: 800;"><%=monitoringUnit%></span>
                      </div>
                      <input type=hidden class="form-control" size=5
                             name="<%=terminalId%>_hiddenAlertThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=alertThreshold%>">
                      <div class="input-group">
                        <input type=text class="form-control" size=5
                               name="<%=terminalId%>_weeklyAlertThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                               value="<%=weeklyThreshold%>" <%=weeklyThresholdVisibility%>>
                        <span class="input-group-addon" style="font-weight: 800;"><%=monitoringUnit%></span>
                      </div>
                      <input type=hidden class="form-control" size=5
                             name="<%=terminalId%>_hiddenWeeklyAlertThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=weeklyThreshold%>">
                      <div class="input-group">
                        <input type=text class="form-control" size=5
                               name="<%=terminalId%>_monthlyAlertThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                               value="<%=monthlyThreshold%>" <%=monthlyThresholdVisibility%>>
                        <span class="input-group-addon" style="font-weight: 800;"><%=monitoringUnit%></span>
                      </div>
                      <input type=hidden class="form-control" size=5
                             name="<%=terminalId%>_hiddenMonthlyAlertThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monthlyThreshold%>">
                      <%--<b><%=monitoringUnit%></b>--%>
                    </td>
                    <td align="center" class="textb" data-label="Suspension Setting - Threshold (DL,WL,ML)"><input type="checkbox"
                                                                                                                   id="<%=terminalId%>_suspensionActivationValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                                                                                                   value="<%=monitoringParameterMappingVO.getSuspensionActivation()%>"
                                                                                                                   valign="middle" <%=suspensionActivation%>
                                                                                                                   onclick="doChangesForSuspensionActivation(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">
                      <input type="hidden"
                             name="<%=terminalId%>_suspensionActivation_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             id="<%=terminalId%>_suspensionActivation_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monitoringParameterMappingVO.getSuspensionActivation()%>">
                    </td>
                    <td align="center" class="textb" data-label="<%=monitoringUnit%>">
                      <div class="input-group">
                        <input type=text class="form-control" size=5
                               name="<%=terminalId%>_suspensionThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                               value="<%=suspensionThreshold%>" <%=dailyThresholdVisibility%>>
                        <span class="input-group-addon" style="font-weight: 800;"><%=monitoringUnit%></span>
                      </div>
                      <input type=hidden class="form-control" size=5
                             name="<%=terminalId%>_hiddenSuspensionThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=suspensionThreshold%>" >
                      <div class="input-group">
                        <input type=text class="form-control" size=5
                               name="<%=terminalId%>_weeklySuspensionThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                               value="<%=weeklySuspensionThreshold%>" <%=weeklyThresholdVisibility%>>
                        <span class="input-group-addon" style="font-weight: 800;"><%=monitoringUnit%></span>
                      </div>
                      <input type=hidden class="form-control" size=5
                             name="<%=terminalId%>_hiddenWeeklySuspensionThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=weeklySuspensionThreshold%>">
                      <div class="input-group">
                        <input type=text class="form-control" size=5
                               name="<%=terminalId%>_monthlySuspensionThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                               value="<%=monthlySuspensionThreshold%>" <%=monthlyThresholdVisibility%>>
                        <span class="input-group-addon" style="font-weight: 800;"><%=monitoringUnit%></span>
                      </div>
                      <input type=hidden class="form-control" size=5
                             name="<%=terminalId%>_hiddenMonthlySuspensionThreshold_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monthlySuspensionThreshold%>">
                      <%--<b><%=monitoringUnit%></b>--%>
                    </td>
                    <td align="center" class="textb" data-label="E-mail Notification - Merchant - Check">
                      <input type="checkbox"
                             id="<%=terminalId%>_isAlertToMerchantValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monitoringParameterMappingVO.getIsAlertToMerchant()%>"
                             valign="middle" <%=isAlertToMerchant%>
                             onclick="doChangesForAlertToMerchant(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">
                      <input type="hidden" name="<%=terminalId%>_isAlertToMerchant_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             id="<%=terminalId%>_isAlertToMerchant_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monitoringParameterMappingVO.getIsAlertToMerchant()%>">
                    </td>
                    <td align="center" class="textb" data-label="E-mail Notification - Merchant">
                      <div class="multiselect" align="center">
                        <div class="selectBox" onclick="showCheckboxesMerchant(<%=terminalId%>,<%=monitoringParameterVO.getMonitoringParameterId()%>)">
                          <select  class="textb form-control" >
                            <option><%=partnerRiskRuleMapping_Teams%></option>
                          </select>
                          <div class="overSelect"></div>
                        </div>
                        <div id="<%=terminalId%>_checkboxesmerchant_<%=monitoringParameterVO.getMonitoringParameterId()%>" align="left" class="checkboxes"  style="padding-left: 15px; padding-top: 5px; background-color: #ffffff;">
                          <label for="<%=terminalId%>_isAlertToMerchantSalesValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                            <input type="checkbox" align="left"
                                   id="<%=terminalId%>_isAlertToMerchantSalesValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToMerchantSales()%>"
                                   valign="middle" <%=isAlertToMerchantSales%>
                                   onclick="doChangesForAlertToMerchantSales(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">
                            <input type="hidden" name="<%=terminalId%>_isAlertToMerchantSales_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   id="<%=terminalId%>_isAlertToMerchantSales_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToMerchantSales()%>">
                            <%=partnerRiskRuleMapping_Sales%>
                          </label>
                          <label for="<%=terminalId%>_isAlertToMerchantRFValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                            <input type="checkbox" align="left"
                                   id="<%=terminalId%>_isAlertToMerchantRFValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToMerchantRF()%>"
                                   valign="middle" <%=isAlertToMerchantRF%>
                                   onclick="doChangesForAlertToMerchantRF(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">
                            <input type="hidden" name="<%=terminalId%>_isAlertToMerchantRF_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   id="<%=terminalId%>_isAlertToMerchantRF_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToMerchantRF()%>">
                            <%=partnerRiskRuleMapping_RF%>
                          </label>
                          <label for="<%=terminalId%>_isAlertToMerchantCBValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                            <input type="checkbox" align="left"
                                   id="<%=terminalId%>_isAlertToMerchantCBValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToMerchantCB()%>"
                                   valign="middle" <%=isAlertToMerchantCB%>
                                   onclick="doChangesForAlertToMerchantCB(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">
                            <input type="hidden" name="<%=terminalId%>_isAlertToMerchantCB_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   id="<%=terminalId%>_isAlertToMerchantCB_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToMerchantCB()%>">
                            <%=partnerRiskRuleMapping_CB%>
                          </label>
                          <label for="<%=terminalId%>_isAlertToMerchantFraudValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                            <input type="checkbox" align="left"
                                   id="<%=terminalId%>_isAlertToMerchantFraudValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToMerchantFraud()%>"
                                   valign="middle" <%=isAlertToMerchantFraud%>
                                   onclick="doChangesForAlertToMerchantFraud(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">
                            <input type="hidden" name="<%=terminalId%>_isAlertToMerchantFraud_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   id="<%=terminalId%>_isAlertToMerchantFraud_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToMerchantFraud()%>">
                           <%=partnerRiskRuleMapping_Fraud%>
                          </label>
                          <label for="<%=terminalId%>_isAlertToMerchantTechValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                            <input type="checkbox" align="left"
                                   id="<%=terminalId%>_isAlertToMerchantTechValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToMerchantTech()%>"
                                   valign="middle" <%=isAlertToMerchantTech%>
                                   onclick="doChangesForAlertToMerchantTech(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">
                            <input type="hidden" name="<%=terminalId%>_isAlertToMerchantTech_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   id="<%=terminalId%>_isAlertToMerchantTech_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToMerchantTech()%>">
                           <%=partnerRiskRuleMapping_Tech%>
                          </label>
                        </div>
                      </div>
                    </td>
                    <td align="center" class="textb" data-label="E-mail Notification - Partner - Check"><input type="checkbox"
                                                                                                               id="<%=terminalId%>_isAlertToPartnerValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                                                                                               value="<%=monitoringParameterMappingVO.getIsAlertToPartner()%>"
                                                                                                               valign="middle" <%=isAlertToPSP%>
                                                                                                               onclick="doChangesForAlertToPSP(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">
                      <input type="hidden" name="<%=terminalId%>_isAlertToPartner_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             id="<%=terminalId%>_isAlertToPartner_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                             value="<%=monitoringParameterMappingVO.getIsAlertToPartner()%>">
                    </td>
                    <td align="center" class="textb" data-label="E-mail Notification - Partner">
                      <div class="multiselect" align="center">
                        <div class="selectBox" onclick="showCheckboxesPSP(<%=terminalId%>,<%=monitoringParameterVO.getMonitoringParameterId()%>)">
                          <select  class="textb form-control"  >
                            <option><%=partnerRiskRuleMapping_Teams%></option>
                          </select>
                          <div class="overSelect"></div>
                        </div>
                        <div id="<%=terminalId%>_checkboxespsp_<%=monitoringParameterVO.getMonitoringParameterId()%>" align="left" class="checkboxes"  style="padding-left: 15px; padding-top: 5px; background-color: #ffffff;">

                          <label for="<%=terminalId%>_isAlertToPartnerSalesValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                            <input type="checkbox" align="left"
                                   id="<%=terminalId%>_isAlertToPartnerSalesValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToPartnerSales()%>"
                                   valign="middle" <%=isAlertToPSPSales%>
                                   onclick="doChangesForAlertToPartnerSales(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                            <input type="hidden" name="<%=terminalId%>_isAlertToPartnerSales_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   id="<%=terminalId%>_isAlertToPartnerSales_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToPartnerSales()%>">
                           <%=partnerRiskRuleMapping_Sales%>
                          </label>

                          <label for="<%=terminalId%>_isAlertToPartnerRFValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                            <input type="checkbox" align="left"
                                   id="<%=terminalId%>_isAlertToPartnerRFValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToPartnerRF()%>"
                                   valign="middle" <%=isAlertToPSPRF%>
                                   onclick="doChangesForAlertToPartnerRF(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                            <input type="hidden" name="<%=terminalId%>_isAlertToPartnerRF_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   id="<%=terminalId%>_isAlertToPartnerRF_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToPartnerRF()%>">
                           <%=partnerRiskRuleMapping_RF%>
                          </label>

                          <label for="<%=terminalId%>_isAlertToPartnerCBValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                            <input type="checkbox" align="left"
                                   id="<%=terminalId%>_isAlertToPartnerCBValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToPartnerCB()%>"
                                   valign="middle" <%=isAlertToPSPCB%>
                                   onclick="doChangesForAlertToPartnerCB(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                            <input type="hidden" name="<%=terminalId%>_isAlertToPartnerCB_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   id="<%=terminalId%>_isAlertToPartnerCB_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToPartnerCB()%>">
                            <%=partnerRiskRuleMapping_CB%>
                          </label>

                          <label for="<%=terminalId%>_isAlertToPartnerFraudValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                            <input type="checkbox" align="left"
                                   id="<%=terminalId%>_isAlertToPartnerFraudValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToPartnerFraud()%>"
                                   valign="middle" <%=isAlertToPSPFraud%>
                                   onclick="doChangesForAlertToPartnerFraud(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                            <input type="hidden" name="<%=terminalId%>_isAlertToPartnerFraud_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   id="<%=terminalId%>_isAlertToPartnerFraud_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToPartnerFraud()%>">
                            <%=partnerRiskRuleMapping_Fraud%>
                          </label>

                          <label for="<%=terminalId%>_isAlertToPartnerTechValue_<%=monitoringParameterVO.getMonitoringParameterId()%>">
                            <input type="checkbox" align="left"
                                   id="<%=terminalId%>_isAlertToPartnerTechValue_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToPartnerTech()%>"
                                   valign="middle" <%=isAlertToPSPTech%>
                                   onclick="doChangesForAlertToPartnerTech(this,<%=monitoringParameterVO.getMonitoringParameterId()%>,<%=terminalId%>)">

                            <input type="hidden" name="<%=terminalId%>_isAlertToPartnerTech_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   id="<%=terminalId%>_isAlertToPartnerTech_<%=monitoringParameterVO.getMonitoringParameterId()%>"
                                   value="<%=monitoringParameterMappingVO.getIsAlertToPartnerTech()%>">
                            <%=partnerRiskRuleMapping_Tech%>
                          </label>
                        </div>
                      </div>
                    </td>
                  </tr>
                  <%
                    }
                  %>
                  <tr>
                    <td colspan="12" align="center"><input id="submit" type="Submit" value="Save Changes" name="submit"
                                                           class="buttonform btn btn-default"
                                                           onclick="return confirmsubmit2(<%=terminalId%>)"></td>
                  </tr>
                </table>

              </div>
            </div>
          </div>
        </div>
      </form>
      <%
          }
        }
        else if (functions.isValueNull((String)request.getAttribute("updatemsg")))
        {
          out.println("<table align=\"center\"><tr><td><font class=\"textb\"><b>");
          out.println("<div class=\"content\">");
          out.println("<div class=\"page-heading\">");
          out.println("<div class=\"row\">");
          out.println("<div class=\"col-sm-12 portlets ui-sortable\">");
          out.println("<div class=\"widget\">");
          out.println("<div class=\"widget-header transparent\">\n" +
                  "                                <h2><strong><i class=\"fa fa-th-large\"></i>&nbsp;&nbsp;"+partnerRiskRuleMapping_Report_Table+"</strong></h2>\n" +
                  "                                <div class=\"additional-btn\">\n" +
                  "                                    <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
                  "                                    <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
                  "                                    <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
                  "                                </div>\n" +
                  "                            </div>");
          out.println("<div class=\"widget-content padding\">");
          out.println(Functions.NewShowConfirmation1(partnerRiskRuleMapping_Sorry, (String) request.getAttribute("updatemsg")));
          out.println("</div>");
          out.println("</div>");
          out.println("</div>");
          out.println("</div>");
          out.println("</div>");
          out.println("</div>");
          out.println("</b></font>");
          out.println("</td></tr></table>");
        }
        else
        {

          out.println("<div class=\"content\">");
          out.println("<div class=\"page-heading\">");
          out.println("<div class=\"row\">");
          out.println("<div class=\"col-sm-12 portlets ui-sortable\">");
          out.println("<div class=\"widget\">");
          out.println("<div class=\"widget-header transparent\">\n" +
                  "                                <h2><strong><i class=\"fa fa-th-large\"></i>&nbsp;&nbsp;"+partnerRiskRuleMapping_Report_Table+"</strong></h2>\n" +
                  "                                <div class=\"additional-btn\">\n" +
                  "                                    <a href=\"#\" class=\"hidden reload\"><i class=\"icon-ccw-1\"></i></a>\n" +
                  "                                    <a href=\"#\" class=\"widget-toggle\"><i class=\"icon-down-open-2\"></i></a>\n" +
                  "                                    <a href=\"#\" class=\"widget-close\"><i class=\"icon-cancel-3\"></i></a>\n" +
                  "                                </div>\n" +
                  "                            </div>");
          out.println("<div class=\"widget-content padding\">");
          out.println(Functions.NewShowConfirmation1(partnerRiskRuleMapping_Sorry, partnerRiskRuleMapping_No_Records));
          out.println("</div>");
          out.println("</div>");
          out.println("</div>");
          out.println("</div>");
          out.println("</div>");
          out.println("</div>");
        }
      %>
    </div>
    <%
      }
      else
      {
        response.sendRedirect("/partner/logout.jsp");
        return;
      }
    %>
  </div>
</div>
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