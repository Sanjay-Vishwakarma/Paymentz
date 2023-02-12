<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 11/8/12
  Time: 3:09 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.manager.PayoutManager" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.ChargeVO" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="com.manager.ChargeManager" %>
<%@ page import="com.manager.utils.CommonFunctionUtil" %>
<%@ page import="java.lang.reflect.Array" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.vo.AgentCommissionVO" %>
<%@ page import="com.payment.exceptionHandler.PZDBViolationException" %>
<%@ page import="com.manager.dao.AgentDAO" %>

<html>
<head>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">

  <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
  <script src="/icici/css/jquery-ui.min.js"></script>
  <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
  <script src="/icici/javascript/autocomplete.js"></script>


  <script type="text/javascript" language="JavaScript" src="/icici/javascript/memberid_filter.js"></script>
  <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script type="text/javascript" language="JavaScript" src="/icici/javascript/accountid.js"></script>
  <script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-accountid.js"></script>
  <script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-account-member.js"></script>
  <script language="javascript">
    function isint(form)
    {
      if (isNaN(form.numrows.value))
        return false;
      else
        return true;
    }
    function handleTeminalChange()
    {
      var els = document.querySelectorAll('.hideTerminal');
      if(document.getElementById('copyCharge').checked) {
        document.getElementById("commissionId").disabled = true;
        document.getElementById("tid3").disabled = true;
        els.forEach(function(el) {
          el.classList.remove("hide");
        });
      }else{
        document.getElementById("commissionId").disabled = false;
        document.getElementById("tid3").disabled = false;
        els.forEach(function(el) {
          el.classList.add("hide");
        });
      }
    }
    function checked()
    {
      if(document.getElementById('copyCharge').checked) {
        if(!document.getElementById("tid4").value){
          alert("Please provide the terminal Id")
          return false;
        }
        if(!document.getElementById("tid5").value){
          alert("Please provide the terminal Id")
          return false;
        }
      }else{
        if(!document.getElementById("tid3").value){
          alert("Please provide the terminal Id")
          return false;
        }
        var checked = 0;
        var chks = document.getElementsByName("commissionId");
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

    $('#sandbox-container input').datepicker({
    });

    $(function() {
      $( ".datepicker" ).datepicker({dateFormat: 'yy-mm-dd',endDate:'+10y'});
    });

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
  <title> Add Merchant Charges</title>
  <style type="text/css">

    .multiselect {
      width: 100%;

    }

    .selectBox {
      position: relative;
    }

    .selectBox select {
      width: 60%;
      font-weight: normal;
      height: 60%;

    }

    .overSelect {
      position: absolute;
      left: 0;
      right: 0;
      top: 0;
      bottom: 0;
    }

    .checkboxes-option {
      display: none;
      border: 1px #dadada solid;

      position: absolute;
      width: 30%;
      background-color: #ffffff;
      z-index: 1;
      height: 100px;
      overflow-x: auto;
    }

    .checkboxes-option label {
      display: block;
    }

    .checkboxes-option label:hover {
      background-color: #1e90ff;
    }


    input[type="checkbox"]{
      width: 18px; /*Desired width*/
      height: 18px; /*Desired height*/
    }
  </style>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    TerminalManager terminalManager = new TerminalManager();
    TreeMap<String, TerminalVO> memberMap = new TreeMap();
    Functions functions = new Functions();

    String terminalid = nullToStr(request.getParameter("terminalid"));
    String memberid = nullToStr(request.getParameter("memberid"));
    String fromterminal = nullToStr(request.getParameter("fromTerminal"));
    String toterminal = nullToStr(request.getParameter("toTerminal"));
    String checked = request.getParameter("copyCharge");
    String agentid=nullToStr(request.getParameter("agentid"));


    StringBuilder sb = new StringBuilder();
    String EOL="<BR>";

    if (functions.isValueNull(request.getParameter("memberid")))
    {
      if (!ESAPI.validator().isValidInput("memberid", request.getParameter("memberid"), "Numbers", 20, false) || "0".equals(request.getParameter("memberid")))
      {

        sb.append("Invalid Member Id." +EOL);
      }
    }
    if (functions.isValueNull(request.getParameter("terminalid")))
    {
      if (!ESAPI.validator().isValidInput("terminalid", request.getParameter("terminalid"), "Numbers", 5, false) || "0".equals(request.getParameter("terminalid")))
      {
        sb.append("Invalid Terminal Id." +EOL);
      }
    }

    if ( functions.isValueNull(request.getParameter("agentid")) && !ESAPI.validator().isValidInput("agentid", request.getParameter("agentid"), "Numbers", 10, false) || "0".equals(request.getParameter("agentid")))
    {
      sb.append( "Invalid Agent Id." );
    }
    List<AgentCommissionVO> chargeNameList = PayoutManager.loadchargenameAgent();
    List<TerminalVO> terminalList = null;
    try
    {
      terminalList = terminalManager.getAllMappedTerminals();
    }
    catch (PZDBViolationException e)
    {
      e.printStackTrace();
    }
    String role="Admin";
    String username=(String)session.getAttribute("username");
    for(TerminalVO terminalVO : terminalList)
    {
      String memberKey = terminalVO.getMemberId()+"-"+terminalVO.getAccountId()+"-"+terminalVO.getGateway().toUpperCase() + "-" + terminalVO.getCurrency() + "-" + terminalVO.getGateway_id();
      memberMap.put(memberKey,terminalVO);
    }
%>
<div class="row">
  <div class="col-lg-12">
    <div class="panel panel-default">
      <div class="panel-heading" >
        Agent Commission
        <div style="float: right;">
          <form action="/icici/listAgentCommission.jsp?ctoken=<%=ctoken%>" method="POST">
            <button type="submit" value="Agent Commission Master" name="submit" class="addnewmember" style="width: 250px;">
              <i class="fa fa-sign-in"></i>
              &nbsp;&nbsp;Agent Commission Master
            </button>
          </form>
        </div>
      </div>
      <%
        String errormsg2 = (String) request.getAttribute("sberror");
        String status = (String) request.getAttribute("message");
        String chargeids1 = (String) request.getAttribute("commissionids1");
        if (functions.isValueNull(errormsg2))
        {
          out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg2 + "</h5>");
        }
        if (functions.isValueNull(status))
        {
          out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + status + "</h5>");
        }
        if (sb.length()>0){
          out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + sb.toString() + "</h5>");
        }
      %>
      <form id="myForm" action="/icici/manageAgentCommission.jsp?ctoken=<%=ctoken%>" method="post" name="form" onsubmit="return checked()" >
        <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
        <input type="hidden" value="<%=(String)session.getAttribute("merchantid")%>" name="partnerId">

        <table border="0" align="center" width="65%" cellpadding="2" cellspacing="2">
          <tbody>
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                <tbody>
                <tr>
                  <td colspan="4">&nbsp;</td>
                <tr>
                  <td style="padding: 2px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 2px" width="30%" class="textb"><b>Agent Id*</b></td>
                  <td style="padding: 2px" width="5%" class="textb">:</td>
                  <td style="padding: 2px" width="50%">
                    <input name="agentid" id="agnt" value="<%=ESAPI.encoder().encodeForHTMLAttribute(agentid)%>" class="form-control" autocomplete="on" required/>
                  </td>
                </tr>
                <tr>
                  <td colspan="4">&nbsp;</td>
                <tr>
                <tr>
                  <td style="padding: 2px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 2px" width="30%" class="textb"><b>Member Id*</b></td>
                  <td style="padding: 2px" width="5%" class="textb">:</td>
                  <td style="padding: 2px" width="50%">
                    <input name="memberid" id="memberid1" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>" class="form-control" autocomplete="on" required/>
                  </td>
                </tr>
                <tr>
                  <td colspan="4">&nbsp;</td>
                <tr>
                  <td style="padding: 3px"  width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px"  width="30%" class="textb"><b>Terminal Id*</b></td>
                  <td style="padding: 2px" width="5%" class="textb">:</td>
                  <td style="padding: 2px" width="50%">
                    <input name="terminalid" id="tid3" value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalid)%>" class="form-control" autocomplete="on" <%--required--%>/>
                  </td>
                </tr>

                <tr>
                  <td colspan="4">&nbsp;</td>
                <tr>
                  <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px" width="30%" class="textb"><b>Commission Name*</b></td>
                  <td style="padding: 3px" width="5%" class="textb">:</td>
                  <td style="padding: 3px" width="80%">
                    <div select size="1" class="selectBox" onclick="showCheckboxes('commissionId','checkboxes')" name="commissionname">
                      <select class="form-control" id="commissionId" name="commissionid">
                        <option value="" default>Select Commission Name</option>
                      </select>
                      <div class="overSelect"></div>
                    </div>
                    <div id="checkboxes" class="checkboxes-option">
                      <div id="chkbox" class="checkboxes">
                        <%
                          for(AgentCommissionVO commissionVO : chargeNameList)
                          {
                        %>
                        <input type="checkbox" name="commissionId" value="<%=commissionVO.getChargeId()%>-<%=commissionVO.getChargeName()%>"><%=commissionVO.getChargeName()%><br>
                        <%
                          }
                        %>
                      </div>
                    </div>
                  </td>
                </tr>
                <tr>
                  <td colspan="4">&nbsp;</td>
                </tr>
                <tr>
                  <td style="padding: 3px"  width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px"  width="30%" class="textb">
                    <%
                      String check="";
                      if(functions.isValueNull(checked)){
                        check="checked";
                      }
                    %>
                    <input type="checkbox" name="copyCharge" id="copyCharge" <%=check%> onchange="handleTeminalChange()"><b> Copy Terminal Commission</b>
                  </td>
                </tr>

                <tr>
                  <td colspan="4">&nbsp;</td>
                </tr>
                <tr class="hideTerminal hide">
                  <td style="padding: 3px"  width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px"  width="30%" class="textb"><b>From Terminal*</b></td>
                  <td style="padding: 2px" width="5%" class="textb">:</td>
                  <td style="padding: 2px" width="50%">
                    <input name="fromTerminal" id="tid4" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromterminal)%>" class="form-control" autocomplete="on" />
                  </td>
                </tr>

                <tr>
                  <td colspan="4">&nbsp;</td>
                </tr>
                <tr class="hideTerminal hide">
                  <td style="padding: 3px"  width="2%" class="textb">&nbsp;</td>
                  <td style="padding: 3px"  width="30%" class="textb"><b>To Terminal*</b></td>
                  <td style="padding: 2px" width="5%" class="textb">:</td>
                  <td style="padding: 2px" width="50%">
                    <input name="toTerminal" id="tid5" value="<%=ESAPI.encoder().encodeForHTMLAttribute(toterminal)%>" class="form-control" autocomplete="on" />
                  </td>
                </tr>


                <tr>
                  <td colspan="4">&nbsp;</td>
                <tr>
                  <center>
                    <td style="padding: 3px" width="2%" class="textb">&nbsp;</td>
                    <td style="padding: 3px" width="43%" class="textb"></td>
                    <td style="padding: 3px" width="5%" class="textb"></td>
                    <td style="padding: 3px" width="50%" class="textb">
                      <button type="submit" class="btn btn-default" id="submit" value="OK" <%--onclick="checked()"--%>>
                        <i class="fa fa-sign-in"></i>
                        &nbsp;&nbsp;OK
                      </button>
                    </td></center>
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
<script>
  (function () {
    console.log("inside function---",document.getElementById('copyCharge'));
    var els = document.querySelectorAll('.hideTerminal');
    if(document.getElementById('copyCharge').checked) {
      document.getElementById("commissionId").disabled = true;
      document.getElementById("tid3").disabled = true;
      els.forEach(function(el) {
        el.classList.remove("hide");
      });
    }else{
      document.getElementById("commissionId").disabled = false;
      document.getElementById("tid3").disabled = false;
      els.forEach(function(el) {
        el.classList.add("hide");
      });
    }
  }());
</script>
<%
  if (sb.length()<=0)
  {
    String commissionIds[] = null;
    if (functions.isValueNull(chargeids1))
    {
      commissionIds = chargeids1.split("\\|");
    }
    else
    {
      commissionIds = request.getParameterValues("commissionId");
    }

    String memberId = request.getParameter("memberid");
    String agentId = request.getParameter("agentid");
    String terminalId = request.getParameter("terminalid");
    String fromTerminal = request.getParameter("fromTerminal");
    String ToTerminal = request.getParameter("toTerminal");
    String copy = request.getParameter("copyCharge");
    if (!functions.isValueNull(terminalId) && functions.isValueNull(ToTerminal))
    {
      terminalId = ToTerminal;
    }
    List<AgentCommissionVO> commissionVOList = null;
    ChargeManager chargeManager = new ChargeManager();
    if (commissionIds == null && functions.isValueNull(fromTerminal) && functions.isValueNull(toterminal))
    {
      commissionVOList = chargeManager.getListOfTerminalChargesAgent(memberId, agentid, fromTerminal);

    }
    if (functions.isValueNull(fromTerminal) && functions.isValueNull(toterminal))
    {
      if (!ESAPI.validator().isValidInput("terminalid", terminalId, "Numbers", 5, false) || "0".equals(terminalId))
      {
        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "Invalid To Terminal Id." + "</h5>");
        return;
      }

      if (!ESAPI.validator().isValidInput("terminalid", fromTerminal, "Numbers", 5, false) || "0".equals(fromTerminal))
      {
        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "Invalid From Terminal Id." + "</h5>");
        return;
      }
      if (fromTerminal.equals(toterminal))
      {
        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "From terminal and To terminal can not be the same" + "</h5>");
        return;
      }
    }
    if (functions.isValueNull(fromterminal))
    {
      if (commissionVOList.size() == 0)
      {
        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "Commission Not Found.Please select at least one Commission." + "</h5>");
        return;
      }
    }

    TerminalVO terminalVO = terminalManager.getTerminalByTerminalId(terminalId);

    GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(terminalVO.getAccountId());
    String accountId = terminalVO.getAccountId();
    String payModeId = terminalVO.getPaymodeId();
    String cardTypeId = terminalVO.getCardTypeId();

    boolean terminalVO1 = terminalManager.isValidTerminal(memberId, accountId, terminalId, payModeId, cardTypeId);

    AgentDAO agentDAO=new AgentDAO();

    if (functions.isValueNull(memberId) && functions.isValueNull(terminalId) && functions.isValueNull(agentId))
    {
      if(terminalVO==null)
      {
        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "Invalid member terminal configuration" + "</h5>");
      }
      else
      {
        boolean ismembermappedwithAgent=agentDAO.isCheckMemberMappedWithAgent(memberId,agentId);
          if (ismembermappedwithAgent)
          {
            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "Invalid member agent mapping" + "</h5>");
          }
          else if (terminalVO1)
          {
            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + "Invalid member terminal configuration" + "</h5>");
          }
          else
          {
          StringBuffer stringBuffer = new StringBuffer();
          if ((functions.isValueNull(String.valueOf(commissionIds))&& commissionIds!=null) || (functions.isValueNull(String.valueOf(commissionVOList)) && commissionVOList.size()>0))
          {
%>
<div class="reporttable">
  <form name="AddNewCharges" action="/icici/servlet/ManageAgentCommission?ctoken=<%=ctoken%>" method="post">
    <input type="hidden" value="<%=memberId%>" name="memberid" id="memberid">
    <input type="hidden" value="<%=agentId%>" name="agentid" id="agentid">
    <input type="hidden" value="<%=terminalId%>" name="terminalid" id="terminalid">
    <input type="hidden" value="<%=fromTerminal%>" name="fromTerminal" id="fromTerminal">
    <input type="hidden" value="<%=toterminal%>" name="toTerminal" id="toTerminal">
    <input type="hidden" value="<%=copy%>" name="copyCharge">
    <table align="center" width="80%" class="table table-striped table-bordered table-hover table-green dataTable">
      <thead>
      <tr>
        <td align="center" class="th0">Commission Name</td>
        <td align="center" class="th0">Commission Value*</td>
        <td align="center" class="th0">Start Date*</td>
        <td align="center" class="th0">End Date*</td>
        <td align="center" class="th0">Sequence Number</td>
        <td align="center" class="th0">Action</td>
      </tr>
      </thead>
      <%
        if(commissionIds!=null)
        {
          int i=0;
          for(String commissionId:commissionIds)
          {
            String cid=commissionId.split("-")[0];
            String cname=commissionId.split("-")[1];

            stringBuffer.append(commissionId);
            if(i!=commissionIds.length)
            {
              stringBuffer.append("|");
            }
            i=i+1;
            String agentCommission="";
            String sequenceNumber="";
            String fromDate="";
            String toDate="";

            String chargeUnit="";

            if(functions.isValueNull(request.getParameter("agentCommission_"+cid)))
              agentCommission=request.getParameter("agentCommission_"+cid);
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
          <input type="hidden" name="commissionId_<%=cid%>" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cid)%>"></td>
        <td><div class="input-group"><input class="form-control"  maxlength="20" type="text" name="agentCommission_<%=cid%>"  value="<%=agentCommission%>" size="5" placeholder="0.00">
          <span class="input-group-addon" style="font-weight: 500;"><%=chargeUnit%></span></div>
        </td>
        <td><input type="text" size="10" readonly="readonly" name="fromdate_<%=cid%>" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromDate)%>" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;"></td>
        <td><input type="text" size="10" readonly="readonly" class="datepicker form-control" name="todate_<%=cid%>" value="<%=ESAPI.encoder().encodeForHTMLAttribute(toDate)%>" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;"></td>
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
      else if(commissionVOList!=null && commissionVOList.size()>0)
      {
        int i=0;
        for(AgentCommissionVO commissionVO:commissionVOList)
        {
          stringBuffer.append(commissionVO.getChargeId()+"-"+commissionVO.getChargeName());
          if(i!=commissionVOList.size())
          {
            stringBuffer.append("|");
          }
          i=i+1;

          String agentCommission="";
          String sequenceNumber="";
          String fromDate="";
          String toDate="";
          String[] fromdate=null;
          String[] todate=null;
          CommonFunctionUtil commonFunctionUtil=new CommonFunctionUtil();

          String currency = gatewayAccount.getCurrency();

          String chargeUnit="";

          if(functions.isValueNull(commissionVO.getChargeValue()))
            agentCommission= commissionVO.getChargeValue();
          if(functions.isValueNull(commissionVO.getSequenceNo()))
            sequenceNumber =commissionVO.getSequenceNo();
          if(functions.isValueNull(commissionVO.getStartDate())){
            fromDate = commissionVO.getStartDate();
            fromdate= commonFunctionUtil.convertTimestampToDateTimePicker(fromDate);
          }

          if(functions.isValueNull(commissionVO.getEndDate())){
            toDate = commissionVO.getEndDate();
            todate= commonFunctionUtil.convertTimestampToDateTimePicker(toDate);
          }
      %>
      <tbody>
      <tr id="hideRow_<%=commissionVO.getChargeId()%>">
        <td><%=commissionVO.getChargeName()%>
          <input type="hidden" name="commissionId_<%=commissionVO.getChargeId()%>"
                 value="<%=ESAPI.encoder().encodeForHTMLAttribute(commissionVO.getChargeId())%>"></td>
        <td><div class="input-group"><input class="form-control"  maxlength="20" type="text" name="agentCommission_<%=commissionVO.getChargeId()%>"  value="<%=agentCommission%>" size="5" placeholder="0.00">
          <span class="input-group-addon" style="font-weight: 500;"><%=chargeUnit%></span></div>
        </td>
        <td><input type="text" size="10" readonly="readonly" name="fromdate_<%=commissionVO.getChargeId()%>" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromdate[0])%>" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;"></td>
        <td><input type="text" size="10" readonly="readonly" class="datepicker form-control" name="todate_<%=commissionVO.getChargeId()%>" value="<%=ESAPI.encoder().encodeForHTMLAttribute(todate[0])%>" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;"></td>
        <td>
          <select size="1" class="form-control" name="sequenceNumber_<%=commissionVO.getChargeId()%>">
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
        <td><button type="button" class="btn btn-default" name="delete" value="delete" style="display: -webkit-box;" onclick="deleteRow('hideRow_<%=commissionVO.getChargeId()%>','<%=commissionVO.getChargeId()%>')">
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
        <input type="hidden" name="commissionids" value="<%=stringBuffer.toString()%>">
        <button type="submit" class="btn btn-default" name="save" value="Save" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;Save</button>
      </center>
    </div>
  </form>
</div>
<%
            }
          }
        }
      }
    }
  }
  else
  {
    response.sendRedirect("/icici/logout.jsp");
    return;
  }
%>
<script>
  console.log("outside function---",document.getElementById('copyCharge'));
  (function () {
    console.log("inside function---",document.getElementById('copyCharge'));
    var els = document.querySelectorAll('.hideTerminal');
    if(document.getElementById('copyCharge').checked) {
      document.getElementById("commissionId").disabled = true;
      document.getElementById("tid3").disabled = true;
      els.forEach(function(el) {
        el.classList.remove("hide");
      });
    }else{
      document.getElementById("commissionId").disabled = false;
      document.getElementById("tid3").disabled = false;
      els.forEach(function(el) {
        el.classList.add("hide");
      });
    }
  }());
  $(document).mouseup(function(e)
  {
    var container = $("#checkboxes");
    if(!$(e.target).closest('#commissionId').length > 0) {
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
