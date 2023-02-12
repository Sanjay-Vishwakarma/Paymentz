<%@ page import="com.directi.pg.Functions" %>
<%@ include file="top.jsp"%>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.PayoutManager" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.ChargeVO" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.manager.ChargeManager" %>
<%@ page import="com.manager.utils.CommonFunctionUtil" %>
<%@ page import="com.manager.dao.TerminalDAO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
  Created by IntelliJ IDEA.
  User: SurajT.
  Date: 11/3/2017
  Time: 2:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","partnerMerchantCharges");

  String terminalid = nullToStr(request.getParameter("terminalid"));
  String memberid = nullToStr(request.getParameter("memberid"));
  String pid = nullToStr(request.getParameter("pid"));
  String fromterminal = nullToStr(request.getParameter("fromTerminal"));
  String toterminal = nullToStr(request.getParameter("toTerminal"));
  String copy=request.getParameter("copyCharge");
  String Config="";
  String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
  if(Roles.contains("superpartner")){

  }else{
    pid = String.valueOf(session.getAttribute("merchantid"));
    Config = "disabled";
  }
  Functions functions = new Functions();

%>
<html>
<head>
  <title><%=company%> | Merchant Charges</title>
  <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

  <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

  <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>

  <script language="javascript">
    function isint(form)
    {
      if (isNaN(form.numrows.value))
        return false;
      else
        return true;
    }
  </script>
  <script type="text/javascript">
    $('#sandbox-container input').datepicker({
    });
  </script>
  <script>
    $(function() {
      $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd",endDate:'+10y'});
    });
  </script>

  <style type="text/css">
    #main{background-color: #ffffff}
    :target:before {
      content: "";
      display: block;
      height: 50px;
      margin: -50px 0 0;
    }
    .table > thead > tr > th {font-weight: inherit;}
    :target:before {
      content: "";
      display: block;
      height: 90px;
      margin: -50px 0 0;
    }
    footer{border-top:none;margin-top: 0;padding: 0;}
    /********************Table Responsive Start**************************/
    @media (max-width: 640px){
      table {border: 0;}
      table thead { display: none;}
      tr:nth-child(odd), tr:nth-child(even) {background: #ffffff;}
      table td {
        display: block;
        border-bottom: none;
        padding-left: 0;
        padding-right: 0;
      }
      table td:before {
        content: attr(data-label);
        float: left;
        width: 100%;
        font-weight: bold;
      }
    }
    table {
      width: 100%;
      max-width: 100%;
      border-collapse: collapse;
      margin-bottom: 20px;
      display: table;
      border-collapse: separate;
      border-color: grey;
    }
    thead {
      display: table-header-group;
      vertical-align: middle;
      border-color: inherit;
    }
    tr:nth-child(odd) {background: #F9F9F9;}
    tr {
      display: table-row;
      vertical-align: inherit;
      border-color: inherit;
    }
    th {padding-right: 1em;text-align: left;font-weight: bold;}
    td, th {display: table-cell;vertical-align: inherit;}
    tbody {
      display: table-row-group;
      vertical-align: middle;
      border-color: inherit;
    }
    td {
      padding-top: 6px;
      padding-bottom: 6px;
      padding-left: 10px;
      padding-right: 10px;
      vertical-align: top;
      border-bottom: none;
    }


    .multiselect {
      width: 100%;

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
      left: 0;
      right: 0;
      top: 0;
      bottom: 0;
    }

    #checkboxes {
      display: none;
      border: 1px #dadada solid;

      position: absolute;
      width: 92%;
      background-color: #ffffff;
      z-index: 1;
      height: 150px;
      overflow-x: auto;
    }

    #checkboxes label {
      display: block;
    }

    #checkboxes label:hover {
      background-color: #1e90ff;
    }


    input[type="checkbox"]{
      width: 18px; /*Desired width*/
      height: 18px; /*Desired height*/
    }
    .table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td{border-top: 1px solid #ddd;}
    /********************Table Responsive Ends**************************/
    @media (min-width: 768px){
      .form-horizontal .control-label {
        text-align: left!important;
      }
    }
  </style>
  <script>
    var expanded = false;
    function showCheckboxes(selectId,checkboxDivId) {
      if(!document.getElementById(selectId).disabled)
      {
        var checkboxes = document.getElementById(checkboxDivId);
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
    }
  </script>
  <script type="text/javascript">

    function handleTeminalChange()
    {
      var els = document.querySelectorAll('.hideTerminal');
      if(document.getElementById('copyCharge').checked) {
        if(document.getElementById('member').value ==""){
          alert("Please provide the Member Id");
          document.getElementById('copyCharge').checked = false;
          return false;
        }else
        {
          document.getElementById("chargeId").disabled = true;
          document.getElementById("terminalALL").disabled = true;
          els.forEach(function (el)
          {
            el.classList.remove("hide");
          });
        }
      }else{
        document.getElementById("chargeId").disabled = false;
        document.getElementById("terminalALL").disabled = false;
        els.forEach(function(el) {
          el.classList.add("hide");
        });
      }
    }
    function checked()
    {
      if(document.getElementById('copyCharge').checked) {
        if(!document.getElementById("terminal1").value){
          alert("Please provide the terminal Id")
          return false;
        }
        if(!document.getElementById("terminal").value){
          alert("Please provide the terminal Id")
          return false;
        }
      }else{
        if(!document.getElementById("terminalALL").value){
          alert("Please provide the terminal Id")
          return false;
        }
        var checked = 0;
        var chks = document.getElementsByName("chargeId");
        for (var i = 0; i < chks.length; i++) {
          if (chks[i].checked) {
            checked++;
          }
        }

        if (checked<= 0) {
          alert("Please select Charge Details");
          return false;
        }
      }
    }

    function deleteRow(rowId,id)
    {
      var element=document.getElementById(rowId);
      element.parentNode.removeChild(element);
      document.getElementById("deleteId").value += id+",";
    }
  </script>
</head>
<body>
<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if(partner.isLoggedInPartner(session))
  {
    String partnerid = session.getAttribute("partnerId").toString();
    TerminalManager terminalManager =new TerminalManager();
    List<ChargeVO> chargesVOList = PayoutManager.loadchargename();
    StringBuilder sb = new StringBuilder();
    String EOL="<BR>";

    if (functions.isValueNull(request.getParameter("pid")))
    {
      if (!ESAPI.validator().isValidInput("pid", request.getParameter("pid"), "Numbers", 20, false) || "0".equals(request.getParameter("pid")))
      {
        sb.append("Invalid Partner Id."+EOL );
      }
    }
    if (functions.isValueNull(request.getParameter("memberid")))
    {
      if (!ESAPI.validator().isValidInput("memberid", request.getParameter("memberid"), "Numbers", 20, false) || "0".equals(request.getParameter("memberid")))
      {
        sb.append("Invalid Member Id."+EOL);
      }
    }
    if (functions.isValueNull(request.getParameter("terminalid")))
    {
      if (!ESAPI.validator().isValidInput("terminalid", request.getParameter("terminalid"), "Numbers", 5, false) || "0".equals(request.getParameter("terminalid")))
      {
        sb.append("Invalid Terminal Id." );
      }
    }
    String checked = request.getParameter("copyCharge");
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String manageMemberAccountCharges_Merchant_Charges = StringUtils.isNotEmpty(rb1.getString("manageMemberAccountCharges_Merchant_Charges")) ? rb1.getString("manageMemberAccountCharges_Merchant_Charges") : "Merchant Charges";
    String manageMemberAccountCharges_PartnerID = StringUtils.isNotEmpty(rb1.getString("manageMemberAccountCharges_PartnerID")) ? rb1.getString("manageMemberAccountCharges_PartnerID") : "Partner ID";
    String manageMemberAccountCharges_MerchantID = StringUtils.isNotEmpty(rb1.getString("manageMemberAccountCharges_MerchantID")) ? rb1.getString("manageMemberAccountCharges_MerchantID") : "Merchant ID*";
    String manageMemberAccountCharges_TerminalID = StringUtils.isNotEmpty(rb1.getString("manageMemberAccountCharges_TerminalID")) ? rb1.getString("manageMemberAccountCharges_TerminalID") : "Terminal ID*";
    String manageMemberAccountCharges_Charge_Name = StringUtils.isNotEmpty(rb1.getString("manageMemberAccountCharges_Charge_Name")) ? rb1.getString("manageMemberAccountCharges_Charge_Name") : "Charge Name*";
    String manageMemberAccountCharges_OK = StringUtils.isNotEmpty(rb1.getString("manageMemberAccountCharges_OK")) ? rb1.getString("manageMemberAccountCharges_OK") : "OK";
    String manageMemberAccountCharges_Dynamic_Input = StringUtils.isNotEmpty(rb1.getString("manageMemberAccountCharges_Dynamic_Input")) ? rb1.getString("manageMemberAccountCharges_Dynamic_Input") : "Dynamic Input*";
    String manageMemberAccountCharges_Start_Date = StringUtils.isNotEmpty(rb1.getString("manageMemberAccountCharges_Start_Date")) ? rb1.getString("manageMemberAccountCharges_Start_Date") : "Start Date*";
    String manageMemberAccountCharges_end_Date = StringUtils.isNotEmpty(rb1.getString("manageMemberAccountCharges_end_Date")) ? rb1.getString("manageMemberAccountCharges_end_Date") : "End Date*";
    String manageMemberAccountCharges_Merchant_Rate = StringUtils.isNotEmpty(rb1.getString("manageMemberAccountCharges_Merchant_Rate")) ? rb1.getString("manageMemberAccountCharges_Merchant_Rate") : "Merchant Rate*";
    String manageMemberAccountCharges_Agent_Commission = StringUtils.isNotEmpty(rb1.getString("manageMemberAccountCharges_Agent_Commission")) ? rb1.getString("manageMemberAccountCharges_Agent_Commission") : "Agent Commission*";
    String manageMemberAccountCharges_partner_Commission = StringUtils.isNotEmpty(rb1.getString("manageMemberAccountCharges_partner_Commission")) ? rb1.getString("manageMemberAccountCharges_partner_Commission") : "Partner Commission*";
    String manageMemberAccountCharges_SequenceNumber = StringUtils.isNotEmpty(rb1.getString("manageMemberAccountCharges_SequenceNumber")) ? rb1.getString("manageMemberAccountCharges_SequenceNumber") : "Sequence Number";
    String manageMemberAccountCharges_Save = StringUtils.isNotEmpty(rb1.getString("manageMemberAccountCharges_Save")) ? rb1.getString("manageMemberAccountCharges_Save") : "Save";


%>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/partnerMerchantCharges.jsp?ctoken=<%=ctoken%>" method="post" name="form">
            <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/partner/images/goBack.png">
            </button>
          </form>
        </div>
      </div>
      <br><br><br>
      <div class="row">
        <div class="col-md-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=company%> <%=manageMemberAccountCharges_Merchant_Charges%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <br>
            <div class="widget-content padding">
              <div id="horizontal-form">
                <%
                  String errormsg2 = (String) request.getAttribute("sberror");
                  String success = (String) request.getAttribute("message");
                  String success1 = (String) request.getAttribute("success1");
                  String chargeids1 = (String) request.getAttribute("chargeids1");
                  if (functions.isValueNull(errormsg2))
                  {
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg2 + "</h5>");
                  }
                  if (functions.isValueNull(success))
                  {
                    out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + success + "</h5>");
                  }
                  if (functions.isValueNull(success1))
                  {
                    out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + success1 + "</h5>");
                  }
                  if (sb.length()>0)
                  {
                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + sb.toString() + "</h5>");
                  }
                %>
                <form action="/partner/manageMemberAccountCharges.jsp?ctoken=<%=ctoken%>" method="post" name="form1" id="form1" class="form-horizontal" onsubmit="return checked()">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                  <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                  <input type="hidden" value="<%=(String)session.getAttribute("merchantid")%>" name="partnerId">
                  <div class="widget-content padding">
                    <div class="form-group">
                      <div class="col-md-2"></div>
                      <label for="pid" class="col-md-4 control-label"><%=manageMemberAccountCharges_PartnerID%><br>
                      </label>
                      <div class="col-md-4 ui-widget">
                        <input name="pid" id="pid" style="border: 1px solid #b2b2b2;" value="<%=ESAPI.encoder().encodeForHTMLAttribute(pid)%>" class="form-control" autocomplete="on" <%=Config%>/>
                        <input name="pid" type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(pid)%>"/>
                      </div>
                      <div class="col-md-6"></div>
                    </div>
                    <div class="form-group">
                      <div class="col-md-2"></div>
                      <label for="member" class="col-md-4 control-label"><%=manageMemberAccountCharges_MerchantID%><br>
                      </label>
                      <div class="col-md-4 ui-widget">
                        <input name="memberid" id="member" style="border: 1px solid #b2b2b2;" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>" class="form-control" autocomplete="on" required/>
                      </div>
                      <div class="col-md-6"></div>
                    </div>
                    <div class="form-group">
                      <div class="col-md-2"></div>
                      <label for="terminalALL" class="col-md-4 control-label"><%=manageMemberAccountCharges_TerminalID%><br>
                      </label>
                      <div class="col-md-4 ui-widget">
                        <input name="terminalid" id="terminalALL" style="border: 1px solid #b2b2b2;" value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalid)%>" class="form-control" autocomplete="on" required/>
                      </div>
                      <div class="col-md-6"></div>
                    </div>
                    <div class="form-group">
                      <div class="col-md-2"></div>
                      <label class="col-md-4 control-label"><%=manageMemberAccountCharges_Charge_Name%><br></label>
                      <div class="col-md-4">
                        <div class="selectBox" onclick="showCheckboxes('chargeId','checkboxes')" name="chargename">
                          <select class="form-control" id="chargeId" name="chargeId" style="border: 1px solid #b2b2b2;font-weight:bold ">
                            <option value="" default>Select Charge Name</option>
                          </select>
                          <div class="overSelect"></div>
                        </div>
                        <div id="checkboxes" class="checkboxes-option">
                          <div id="chkbox" align="left" class="checkboxes"  style="padding-left: 15px; padding-top: 5px ">
                            <%
                              for(ChargeVO chargeVO : chargesVOList)
                              {
                            %>
                            <input type="checkbox" name="chargeId" align="left" value="<%=chargeVO.getChargeid()%>-<%=chargeVO.getChargename()%>-<%=chargeVO.getValuetype()%>"><%=chargeVO.getChargename() + "-" + chargeVO.getValuetype()%><br>
                            <%
                              }
                            %>
                          </div>
                        </div>
                      </div>
                      <div class="col-md-6"></div>
                    </div>

                    <div class="form-group">
                      <div class="col-md-2"></div>
                      <div class="col-md-4 ui-widget">
                        <%
                          String check="";
                          if(functions.isValueNull(checked)){
                            check="checked";
                          }
                        %>
                        <input type="checkbox" name="copyCharge" id="copyCharge" <%=check%> onchange="handleTeminalChange()"><b> Copy Terminal Charge</b>
                      </div>

                      <div class="col-md-6"></div>
                    </div>

                    <div class="hideTerminal hide">
                      <div class="form-group">
                      <div class="col-md-2"></div>
                      <label for="terminalALL" class="col-md-4 control-label">From Terminal*<br>
                      </label>
                      <div class="col-md-4 ui-widget">
                        <input name="fromTerminal" id="terminal1" style="border: 1px solid #b2b2b2;" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromterminal)%>" class="form-control" />
                      </div>
                      <div class="col-md-6"></div>
                    </div>
                      </div>

                    <div class="hideTerminal hide">
                      <div class="form-group">
                        <div class="col-md-2"></div>
                        <label for="terminalALL" class="col-md-4 control-label">To Terminal*<br>
                        </label>
                        <div class="col-md-4 ui-widget">
                          <input name="toTerminal" id="terminal" style="border: 1px solid #b2b2b2;" value="<%=ESAPI.encoder().encodeForHTMLAttribute(toterminal)%>" class="form-control"  />
                        </div>
                        <div class="col-md-6"></div>
                        </div>
                    </div>

                    <div class="form-group col-md-12 has-feedback">
                      <center>
                        <label >&nbsp;</label>
                        <input type="hidden" value="1" name="step">
                        <button type="submit" class="btn btn-default" id="submit"  value="OK" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;Process</button>
                      </center>
                    </div>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
      <br>

      <script>
        (function () {
          console.log("inside function---",document.getElementById('copyCharge'));
          var els = document.querySelectorAll('.hideTerminal');
          if(document.getElementById('copyCharge').checked) {
            document.getElementById("chargeId").disabled = true;
            document.getElementById("terminalALL").disabled = true;
            els.forEach(function(el) {
              el.classList.remove("hide");
            });
          }else{
            document.getElementById("chargeId").disabled = false;
            document.getElementById("terminalALL").disabled = false;
            els.forEach(function(el) {
              el.classList.add("hide");
            });
          }
        }());
      </script>

      <%
        if (sb.length()<=0)
        {
          String chargeIds[]=null;
          if(functions.isValueNull(chargeids1))
          {
            chargeIds=chargeids1.split("\\|");
          }
          else
          {
            chargeIds=request.getParameterValues("chargeId");
          }
          PartnerFunctions partnerfunctions = new PartnerFunctions();
          String memberId=request.getParameter("memberid");
          String terminalId=request.getParameter("terminalid");
         /* String partner_id="";
          if(functions.isValueNull(pid)){
            partner_id = pid;
          }
          else{
            partner_id = partnerid;
          }*/

          if(!functions.isValueNull(terminalId) && functions.isValueNull(toterminal)){
            terminalId=toterminal;
          }
          List<ChargeVO> chargeVOList=null;
          ChargeManager chargeManager=new ChargeManager();
          TerminalDAO terminalDAO = new TerminalDAO();
          if(chargeIds==null && functions.isValueNull(fromterminal) && functions.isValueNull(toterminal)){
            chargeVOList=chargeManager.getListOfTerminalCharges(memberId, fromterminal);
          }
          if (functions.isValueNull(fromterminal) && functions.isValueNull(toterminal))
          {
            if (!ESAPI.validator().isValidInput("terminalid", terminalId, "Numbers", 5, false) || "0".equals(terminalId))
            {
              out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "Invalid To Terminal Id." + "</h5>");
              return;
            }

            if (!ESAPI.validator().isValidInput("terminalid", fromterminal, "Numbers", 5, false) || "0".equals(fromterminal))
            {
              out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "Invalid From Terminal Id." + "</h5>");
              return;
            }
            if (functions.isValueNull(fromterminal) && !terminalDAO.isMemberMappedWithTerminal(memberId,fromterminal))
            {
              out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "Invalid Member From Terminal configuration." + "</h5>");
              return;
            }
            if (functions.isValueNull(fromterminal) && !terminalDAO.isMemberMappedWithTerminal(memberId,toterminal))
            {
              out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "Invalid Member To Terminal configuration." + "</h5>");
              return;
            }
            if (fromterminal.equals(toterminal))
            {
              out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "From terminal and To terminal can not be the same." + "</h5>");
              return;
            }
            if (!functions.isValueNull(toterminal) && functions.isValueNull(fromterminal))
            {
              out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "To terminal can not be empty." + "</h5>");
              return;
            }
          }
          if (functions.isValueNull(fromterminal))
          {
            if (chargeVOList.size()==0)
            {
              out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "No Charges Found on From Terminal." + "</h5>");
              return;
            }
          }

          if (functions.isValueNull(memberId) && functions.isValueNull(terminalId))
          {
            TerminalVO terminalVO = terminalManager.getTerminalByTerminalId(terminalId);
            if (functions.isValueNull(pid) &&!partner.isPartnerMemberMapped(memberId, pid))
            {

              out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "Invalid partner member configuration." + "</h5>");
            }
            else if (!functions.isValueNull(pid) && !partner.isPartnerSuperpartnerMembersMapped(memberId, String.valueOf(session.getAttribute("merchantid"))))
            {
              out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "Invalid partner member configuration." + "</h5>");
            }else if(terminalVO==null)
            {
              out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "Invalid member terminal configuration" + "</h5>");
            }
            else
            {
              GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
              String accountId= terminalVO.getAccountId();
              String payModeId= terminalVO.getPaymodeId();
              String cardTypeId=terminalVO.getCardTypeId();

              boolean terminalVO1 = terminalManager.isValidTerminal(memberId,accountId,terminalId,payModeId,cardTypeId);
              if(terminalVO1)
              {
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "Invalid member terminal configuration" + "</h5>");
              }
              else
              {
                StringBuffer stringBuffer = new StringBuffer();
                if((functions.isValueNull(String.valueOf(chargeIds))&& chargeIds!=null) || (functions.isValueNull(String.valueOf(chargeVOList)) && chargeVOList.size()>0))
                {
      %>
      <form name="AddNewCharges" action="/partner/net/ManageMemberAccountCharges?ctoken=<%=ctoken%>" method="post">
        <input type="hidden" value="<%=memberId%>" name="memberid" id="memberid">
        <input type="hidden" value="<%=terminalId%>" name="terminalid" id="terminalid">
        <input type="hidden" value="<%=pid%>" name="pid" id="pid">
        <input type="hidden" value="<%=fromterminal%>" name="fromTerminal" id="fromTerminal">
        <input type="hidden" value="<%=toterminal%>" name="toTerminal" id="toTerminal">
        <input type="hidden" value="<%=copy%>" name="copyCharge">
        <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
          <thead>
          <tr>
            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=manageMemberAccountCharges_Charge_Name%></b></td>
            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=manageMemberAccountCharges_Dynamic_Input%></b></td>
            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=manageMemberAccountCharges_Start_Date%></b></td>
            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=manageMemberAccountCharges_end_Date%></b></td>
            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=manageMemberAccountCharges_Merchant_Rate%></b></td>
            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=manageMemberAccountCharges_Agent_Commission%></b></td>
            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=manageMemberAccountCharges_partner_Commission%></b></td>
            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=manageMemberAccountCharges_SequenceNumber%></b></td>
            <td colspan="2" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
          </tr>
          </thead>
          <%
            if(chargeIds!=null)
            {
              int i=1;
              for(String chargeId:chargeIds)
              {
                String splitvalue[] =chargeId.split("-");
                System.out.println("splitvalue==="+splitvalue.length);
                if (splitvalue.length >= 3)
                {
                  String cid = splitvalue[0];
                  String cname = splitvalue[1];
                  String cvaluetype = splitvalue[2];

                stringBuffer.append(chargeId);
                if(i!=chargeIds.length)
                {
                  stringBuffer.append("|");
                }
                i=1+1;

                String merchantRate="";
                String agentCommission="";
                String partnerCommission="";
                String isinputrequired="";
                String sequenceNumber="";
                String fromDate="";
                String toDate = "";

                String currency = gatewayAccount.getCurrency();

                String chargeUnit="";
                if("Percentage".equals(cvaluetype)){
                  chargeUnit = "%";
                }
                else if ("FlatRate".equals(cvaluetype)){
                  chargeUnit = currency;
                }

                if(functions.isValueNull(request.getParameter("merchantRate_" + cid)))
                  merchantRate = request.getParameter("merchantRate_" + cid);
                if(functions.isValueNull(request.getParameter("agentCommission_"+ cid)))
                  agentCommission = request.getParameter("agentCommission_"+ cid);
                if(functions.isValueNull(request.getParameter("partnerCommission_"+ cid)))
                  partnerCommission = request.getParameter("partnerCommission_"+ cid);
                if(functions.isValueNull(request.getParameter("isinputrequired_"+ cid)))
                  isinputrequired = request.getParameter("isinputrequired_"+ cid);
                if(functions.isValueNull(request.getParameter("sequenceNumber_"+ cid)))
                  sequenceNumber = request.getParameter("sequenceNumber_"+ cid);
                if(functions.isValueNull(request.getParameter("fromdate_"+cid)))
                  fromDate = request.getParameter("fromdate_"+cid);
                if(functions.isValueNull(request.getParameter("todate_"+cid)))
                  toDate = request.getParameter("todate_"+cid);
          %>
          <tbody>
          <tr id="hideRow_<%=cid%>">
            <td><%=cname%>
              <input type="hidden" name="chargeId_<%=cid%>" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cid)%>"></td>
            <td>
              <select name="isinputrequired_<%=cid%>" class="form-control" >
                <%
                  if ("Y".equalsIgnoreCase(isinputrequired))
                  {
                %>
                <option value="N">N</option>
                <option value="Y" selected>Y</option>
                <%
                }
                else
                {
                %>
                <option value="N">N</option>
                <option value="Y">Y</option>
                <%
                  }
                %>
              </select>
            </td>
            <td>
              <input type="text" size="10" name="fromdate_<%=cid%>" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute (fromDate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
            </td>
            <td><input type="text" size="10" name="todate_<%=cid%>" class="datepicker form-control" value="<%= ESAPI.encoder().encodeForHTMLAttribute (toDate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
            </td>
            <td><div class="input-group"><input class="form-control" type="Text" maxlength="20" value="<%=merchantRate%>" name="merchantRate_<%=cid%>" size="5" placeholder="0.00">
              <span class="input-group-addon" style="font-weight: 500;"><%=chargeUnit%></span></div>
            </td>
            <td><div class="input-group"><input class="form-control" type="Text" maxlength="20" value="<%=agentCommission%>" name="agentCommission_<%=cid%>" size="5" placeholder="0.00">
              <span class="input-group-addon" style="font-weight: 500;"><%=chargeUnit%></span></div>
            </td>
            <td class="input-group"><input class="form-control" type="Text" maxlength="20" value="<%=partnerCommission%>" name="partnerCommission_<%=cid%>" size="5" placeholder="0.00">
              <span class="input-group-addon" style="font-weight: 500;"><%=chargeUnit%></span>
            </td>
            <td>
              <select size="1" class="form-control" name="sequenceNumber_<%=cid%>">
                <option value=""></option>
                <%
                  for (int j = 1; j <= 50; j++)
                  {
                    String isSelected = "";
                    if (String.valueOf(j).equals(sequenceNumber))
                    {
                      isSelected = "selected";
                    }
                %>
                <option value="<%=j%>" <%=isSelected%>><%=j%>
                    <%
                    }
                  %>
              </select>
            </td>
            <td><button type="button" class="btn btn-default" name="delete" value="delete" style="display: -webkit-box;" onclick="deleteRow('hideRow_<%=cid%>','<%=cid%>')">
              <i class="fas fa-trash-alt"></i>&nbsp;&nbsp;Delete</button></td>
          </tr>
          </tbody>
          <%
              }
            }
            }
            else if (chargeVOList!=null && chargeVOList.size()>0)
            {
              int i=1;
              for(ChargeVO chargeVO:chargeVOList)
              {

                stringBuffer.append(chargeVO.getChargeid()+"-"+chargeVO.getChargename()+"-"+chargeVO.getValuetype());
                if(i!=chargeVOList.size())
                {
                  stringBuffer.append("|");
                }
                i=1+1;

                String merchantRate="";
                String agentCommission="";
                String partnerCommission="";
                String isinputrequired="";
                String sequenceNumber="";
                String fromdate="";
                String todate="";
                String[] fromDate=null;
                String[] toDate = null;
                CommonFunctionUtil commonFunctionUtil=new CommonFunctionUtil();
                String currency = gatewayAccount.getCurrency();

                String chargeUnit="";
                if("Percentage".equals(chargeVO.getValuetype())){
                  chargeUnit = "%";
                }
                else if ("FlatRate".equals(chargeVO.getValuetype())){
                  chargeUnit = currency;
                }

                if(functions.isValueNull(chargeVO.getChargevalue()))
                  merchantRate=chargeVO.getChargevalue();
                if(functions.isValueNull(chargeVO.getAgentChargeValue()))
                  agentCommission=chargeVO.getAgentChargeValue();
                if(functions.isValueNull(chargeVO.getPartnerChargeValue()))
                  partnerCommission = chargeVO.getPartnerChargeValue();
                if(functions.isValueNull(chargeVO.getIsInputRequired()))
                  isinputrequired = chargeVO.getIsInputRequired();
                if(functions.isValueNull(chargeVO.getSequencenum()))
                  sequenceNumber = chargeVO.getSequencenum();
                if(functions.isValueNull(chargeVO.getStartdate()))
                {
                  fromdate = chargeVO.getStartdate();
                  fromDate = commonFunctionUtil.convertTimestampToDateTimePicker(fromdate);
                }
                if(functions.isValueNull(chargeVO.getEnddate()))
                {
                  todate = chargeVO.getEnddate();
                  toDate = commonFunctionUtil.convertTimestampToDateTimePicker(todate);
                }

          %>
          <tbody>
          <tr id="hideRow_<%=chargeVO.getChargeid()%>">
            <td><%=chargeVO.getChargename()%>
              <input type="hidden" name="chargeId_<%=chargeVO.getChargeid()%>" value="<%=ESAPI.encoder().encodeForHTMLAttribute(chargeVO.getChargeid())%>"></td>
            <td>
              <select name="isinputrequired_<%=chargeVO.getChargeid()%>" class="form-control" >
                <%
                  if ("Y".equalsIgnoreCase(isinputrequired))
                  {
                %>
                <option value="N">N</option>
                <option value="Y" selected>Y</option>
                <%
                }
                else
                {
                %>
                <option value="N">N</option>
                <option value="Y">Y</option>
                <%
                  }
                %>
              </select>
            </td>
            <td>
              <input type="text" size="10" name="fromdate_<%=chargeVO.getChargeid()%>" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute (fromDate[0])%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
            </td>
            <td><input type="text" size="10" name="todate_<%=chargeVO.getChargeid()%>" class="datepicker form-control" value="<%= ESAPI.encoder().encodeForHTMLAttribute (toDate[0])%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
            </td>
            <td><div class="input-group"><input class="form-control" type="Text" maxlength="20" value="<%=merchantRate%>" name="merchantRate_<%=chargeVO.getChargeid()%>" size="5" placeholder="0.00">
              <span class="input-group-addon" style="font-weight: 500;"><%=chargeUnit%></span></div>
            </td>
            <td><div class="input-group"><input class="form-control" type="Text" maxlength="20" value="<%=agentCommission%>" name="agentCommission_<%=chargeVO.getChargeid()%>" size="5" placeholder="0.00">
              <span class="input-group-addon" style="font-weight: 500;"><%=chargeUnit%></span></div>
            </td>
            <td class="input-group"><input class="form-control" type="Text" maxlength="20" value="<%=partnerCommission%>" name="partnerCommission_<%=chargeVO.getChargeid()%>" size="5" placeholder="0.00">
              <span class="input-group-addon" style="font-weight: 500;"><%=chargeUnit%></span>
            </td>
            <td>
              <select size="1" class="form-control" name="sequenceNumber_<%=chargeVO.getChargeid()%>">
                <option value=""></option>
                <%
                  for (int j = 1; j <= 50; j++)
                  {
                    String isSelected = "";
                    if (String.valueOf(j).equals(sequenceNumber))
                    {
                      isSelected = "selected";
                    }
                %>
                <option value="<%=j%>" <%=isSelected%>><%=j%>
                    <%
                    }
                  %>
              </select>
            </td>
            <td><button type="button" class="btn btn-default" name="delete" value="delete" style="display: -webkit-box;" onclick="deleteRow('hideRow_<%=chargeVO.getChargeid()%>','<%=chargeVO.getChargeid()%>')">
              <i class="fas fa-trash-alt"></i>&nbsp;&nbsp;Delete</button></td>
          </tr>
          </tbody>
          <%
            }
            }
            else {
              out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "No charges Found On From Terminal" + "</h5>");
            }
          %>

          <input type="hidden" name="deletedId" id="deleteId" value="">
        </table>
        <div class="form-group col-md-12 has-feedback">
          <center>
            <label >&nbsp;</label>
            <input type="hidden" value="1" name="step">
            <input type="hidden" name="chargeids" value="<%=stringBuffer.toString()%>">
            <button type="submit" class="btn btn-default" name="save" value="Save" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=manageMemberAccountCharges_Save%></button>
          </center>
        </div>
      </form>
      <%
                }
              }
            }
          }
        }
      %>
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

  <script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
  <script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
  <link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
  <link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
</div>
<script>
  console.log("outside function---",document.getElementById('copyCharge'));
  (function () {
    console.log("inside function---",document.getElementById('copyCharge'));
    var els = document.querySelectorAll('.hideTerminal');
    if(document.getElementById('copyCharge').checked) {
      document.getElementById("chargeId").disabled = true;
      document.getElementById("terminalALL").disabled = true;
      els.forEach(function(el) {
        el.classList.remove("hide");
      });
    }else{
      document.getElementById("chargeId").disabled = false;
      document.getElementById("terminalALL").disabled = false;
      els.forEach(function(el) {
        el.classList.add("hide");
      });
    }
  }());
  $(document).mouseup(function(e)
  {
    var container = $("#checkboxes");
    if(!$(e.target).closest('#chargeId').length > 0) {
      if (!container.is(e.target) && container.has(e.target).length === 0) {
        container.hide();
      }
    }
    else{
      container.show();
    }
  });

  $(document).mouseup(function(e)
  {
    var container = $("#checkboxesTerm");
    if(!$(e.target).closest('#terId').length > 0) {
      if (!container.is(e.target) && container.has(e.target).length === 0) {
        container.hide();
      }
    }
    else{
      container.show();
    }
  });
</script>
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