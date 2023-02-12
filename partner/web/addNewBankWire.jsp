<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.vo.ActionVO" %>
<%@ page import="com.manager.vo.BankWireManagerVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.ResourceBundle" %>
<%--
  Created by IntelliJ IDEA.
  User: Mahima
  Date: 31-01-2020
  Time: 15:43
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp" %>
<html>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
%>
<head>
  <title><%=company%>Bank WireManger  </title>
  <script type="text/javascript" src="/partner/javascript/autocomplete.js"></script>
  <link href="/partner/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/partner/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
  <script src="/partner/datepicker/datetimepicker/moment-with-locales.js"></script>
  <script src="/partner/datepicker/datetimepicker/bootstrap-datetimepicker.min.js"></script>
  <script type="text/javascript" src="/partner/javascript/autocomplete.js"></script>
  <script>
    $(function () {
      $(".datepicker").datepicker({dateFormat: "yy-mm-dd"});
    });

    $(function () {
      $('#datetimepicker12').datetimepicker({
        format: 'HH:mm:ss',
        useCurrent: true
      });
    });

    $(function () {
      $('#datetimepicker13').datetimepicker({
        format: 'HH:mm:ss',
        useCurrent: true
      });
    });
  </script>
</head>
<body>

<%
  Functions functions = new Functions();
  session.setAttribute("submit","bankWire");
  String str="";
  int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
  int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), 10);
  str = str + "&ctoken=" + ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  str = str + "&SRecords=" + pagerecords;

  String bankWireId=functions.isValueNull(request.getParameter("bankWireId"))?request.getParameter("bankWireId"):"";
  String accountId=functions.isValueNull(request.getParameter("accountid"))?request.getParameter("accountid"):"";
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String addNewBankWire_Bank_WireManger_List = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Bank_WireManger_List")) ? rb1.getString("addNewBankWire_Bank_WireManger_List") : "Bank WireManger List";
  String addNewBankWire_Bank_WireID = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Bank_WireID")) ? rb1.getString("addNewBankWire_Bank_WireID") : "Bank Wire ID";
  String addNewBankWire_AccountId = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_AccountId")) ? rb1.getString("addNewBankWire_AccountId") : "Account Id";
  String addNewBankWire_Search = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Search")) ? rb1.getString("addNewBankWire_Search") : "Search";
  String addNewBankWire_Bank_Wire_Manager = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Bank_Wire_Manager")) ? rb1.getString("addNewBankWire_Bank_Wire_Manager") : "Bank Wire Manager";
  String addNewBankWire_Bank_WireId = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Bank_WireId")) ? rb1.getString("addNewBankWire_Bank_WireId") : "Bank Wire Id";
  String addNewBankWire_Settled_Date = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Settled_Date")) ? rb1.getString("addNewBankWire_Settled_Date") : "Settled Date*";
  String addNewBankWire_Account_Id = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Account_Id")) ? rb1.getString("addNewBankWire_Account_Id") : "Account Id*";
  String addNewBankWire_Bank_Start_Date = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Bank_Start_Date")) ? rb1.getString("addNewBankWire_Bank_Start_Date") : "Bank Start Date*";
  String addNewBankWire_Bank_End_Date = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Bank_End_Date")) ? rb1.getString("addNewBankWire_Bank_End_Date") : "Bank End Date*";
  String addNewBankWire_Processing_Amount = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Processing_Amount")) ? rb1.getString("addNewBankWire_Processing_Amount") : "Processing Amount*";
  String addNewBankWire_Gross_Amount = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Gross_Amount")) ? rb1.getString("addNewBankWire_Gross_Amount") : "Gross Amount*";
  String addNewBankWire_Net_Final_Amount = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Net_Final_Amount")) ? rb1.getString("addNewBankWire_Net_Final_Amount") : "Net Final Amount";
  String addNewBankWire_Unpaid_Amount = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Unpaid_Amount")) ? rb1.getString("addNewBankWire_Unpaid_Amount") : "Unpaid Amount";
  String addNewBankWire_isrollingreservereleasewire = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_isrollingreservereleasewire")) ? rb1.getString("addNewBankWire_isrollingreservereleasewire") : "isrollingreservereleasewire";
  String addNewBankWire_Rolling_Reserve_DateUpto = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Rolling_Reserve_DateUpto")) ? rb1.getString("addNewBankWire_Rolling_Reserve_DateUpto") : "Rolling Reserve DateUpto";
  String addNewBankWire_Declined_Covered_DateUpto = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Declined_Covered_DateUpto")) ? rb1.getString("addNewBankWire_Declined_Covered_DateUpto") : "Declined Covered DateUpto";
  String addNewBankWire_Chargeback_Covered_DateUpto = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Chargeback_Covered_DateUpto")) ? rb1.getString("addNewBankWire_Chargeback_Covered_DateUpto") : "Chargeback Covered DateUpto";
  String addNewBankWire_Reversed_Covered_DateUpto = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Reversed_Covered_DateUpto")) ? rb1.getString("addNewBankWire_Reversed_Covered_DateUpto") : "Reversed Covered DateUpto";
  String addNewBankWire_Is_SettlementCronExecuted = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Is_SettlementCronExecuted")) ? rb1.getString("addNewBankWire_Is_SettlementCronExecuted") : "Is SettlementCronExecuted";
  String addNewBankWire_Yes = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Yes")) ? rb1.getString("addNewBankWire_Yes") : "Yes";
  String addNewBankWire_No = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_No")) ? rb1.getString("addNewBankWire_No") : "No";
  String addNewBankWire_Is_PayoutCronExecuted = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Is_PayoutCronExecuted")) ? rb1.getString("addNewBankWire_Is_PayoutCronExecuted") : "Is PayoutCronExecuted";
  String addNewBankWire_Is_PartnerCommCronExecuted = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Is_PartnerCommCronExecuted")) ? rb1.getString("addNewBankWire_Is_PartnerCommCronExecuted") : "Is PartnerCommCronExecuted";
  String addNewBankWire_Is_AgentCommCronExecuted = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Is_AgentCommCronExecuted")) ? rb1.getString("addNewBankWire_Is_AgentCommCronExecuted") : "Is AgentCommCronExecuted";
  String addNewBankWire_Is_Paid = StringUtils.isNotEmpty(rb1.getString("addNewBankWire_Is_Paid")) ? rb1.getString("addNewBankWire_Is_Paid") : "Is Paid";

%>

<div class="content-page">
  <div class="content">
    <div class="page-heading">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-credit-card iconstyle"></i>&nbsp;&nbsp; <%=addNewBankWire_Bank_WireManger_List%>
                </strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
              </div>
            </div>

            <div class="widget-content padding">
              <div id="horizontal-form">
                <form action="/partner/net/BankWireManager?ctoken=<%=ctoken%>" method="post" name="forms">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                  <div class="form-group col-md-4 has-feedback">
                    <label ><%=addNewBankWire_Bank_WireID%></label>
                    <input  type="text" name="bankWireId" class="form-control" value="<%= bankWireId%>">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label ><%=addNewBankWire_AccountId%></label>
                    <input  type="text" name="accountid" class="form-control" value="<%= accountId%>">
                  </div>

                  <div class="form-group col-md-6">
                    <label style="color: transparent;">&nbsp;</label>
                    <button type="submit" class="btn btn-default" style="display:block;">
                      <i class="far fa-clock"></i>
                      &nbsp;<%=addNewBankWire_Search%>
                    </button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="row">
        <div class="col-md-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=addNewBankWire_Bank_Wire_Manager%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
              </div>
            </div>
            <br>
            <%
              BankWireManagerVO singleBankWireManagerVO= (BankWireManagerVO) request.getAttribute("singleBankWireManagerVO");
              BankWireManagerVO bankWireManagerVO=(BankWireManagerVO) request.getAttribute("bankWireManagerVO");
              if(request.getParameter("UPDATE")!=null)
              {
                String update=request.getParameter("UPDATE");
                if(update.equals("Success"))
                {
                  out.println("<center><font class=\"textb\">Bank wire manager");
                  if( ((ActionVO) request.getAttribute("actionVO")).isAdd())
                  {
                    out.println("Added");
                  }
                  else
                  {
                    out.println("Updated");
                  }
                  out.println("</font></center>");
                }
              }
              if(singleBankWireManagerVO!=null || ("Add").equals(request.getParameter("name")))
              {
                String bankwiremanagerId = "";
                String settleddate = "";
                String pgtypeId = "";
                String accountid = "";
                String mid = "";
                String bank_start_date = "";
                String bank_end_date = "";
                String server_start_date = "";
                String server_end_date = "";
                String processing_amount = "";
                String grossAmount = "";
                String netfinal_amount = "";
                String unpaid_amount = "";
                String currency = "";
                String isrollingreservereleasewire = "";
                String rollingreservereleasedateupto = "";
                String declinedcoveredupto = "";
                String chargebackcoveredupto = "";
                String reversedCoveredUpto = "";
                String banksettlement_report_file = "";
                String banksettlement_transaction_file = "";
                String isSettlementCronExceuted = "";
                String isPayoutCronExcuted = "";
                String ispaid = "";
                String bank_start_time = "";
                String bank_end_time = "";
                String server_start_time = "";
                String server_end_time = "";
                String settled_time = "";
                String rollingreservecovered_time = "";
                String declinecovered_time = "";
                String reversecovered_time = "";
                String chargebackcovered_time = "";
                String ispartnerpommcronexecuted = "";
                String isagentcommcronexecuted = "";

                String declinedcoveredStartdate = "";
                String chargebackcoveredStartdate = "";
                String reversedCoveredUptoStartdate = "";
                String rollingReleaseStartdate="";

                String declinecovered_time_start = "";
                String reversecovered_time_start = "";
                String chargebackcovered_time_start = "";
                String rollingRelease_time_start="";
                accountid = Functions.checkStringNull(request.getParameter("accountid")) == null ? "" : request.getParameter("accountid");
                bank_start_date = Functions.checkStringNull(request.getParameter("expected_startDate")) == null ? "" : request.getParameter("expected_startDate");
                bank_start_time = Functions.checkStringNull(request.getParameter("expected_startTime")) == null ? "" : request.getParameter("expected_startTime");
                bank_end_date = Functions.checkStringNull(request.getParameter("expected_endDate")) == null ? "" : request.getParameter("expected_endDate");
                bank_end_time = Functions.checkStringNull(request.getParameter("expected_endTime")) == null ? "" : request.getParameter("expected_endTime");
                processing_amount = Functions.checkStringNull(request.getParameter("processingamount")) == null ? "" : request.getParameter("processingamount");
                grossAmount = Functions.checkStringNull(request.getParameter("grossamount")) == null ? "" : request.getParameter("grossamount");
                netfinal_amount = Functions.checkStringNull(request.getParameter("netfinalamout")) == null ? "" : request.getParameter("netfinalamout");
                unpaid_amount = Functions.checkStringNull(request.getParameter("unpaidamount")) == null ? "" : request.getParameter("unpaidamount");
                isrollingreservereleasewire = Functions.checkStringNull(request.getParameter("isrollingreservereleasewire")) == null ? "" : request.getParameter("isrollingreservereleasewire");
                rollingReleaseStartdate = Functions.checkStringNull(request.getParameter("rollingreserveStartdate")) == null ? "" : request.getParameter("rollingreserveStartdate");
                rollingRelease_time_start = Functions.checkStringNull(request.getParameter("rollingReleaseTimeStart")) == null ? "" : request.getParameter("rollingReleaseTimeStart");
                rollingreservereleasedateupto = Functions.checkStringNull(request.getParameter("rollingreservedateupto")) == null ? "" : request.getParameter("rollingreservedateupto");
                rollingreservecovered_time = Functions.checkStringNull(request.getParameter("rollingRelease_Time")) == null ? "" : request.getParameter("rollingRelease_Time");
                declinedcoveredStartdate = Functions.checkStringNull(request.getParameter("declinedcoveredStartdate")) == null ? "" : request.getParameter("declinedcoveredStartdate");
                declinecovered_time_start = Functions.checkStringNull(request.getParameter("declinedcoveredtimeStart")) == null ? "" : request.getParameter("declinedcoveredtimeStart");
                declinedcoveredupto = Functions.checkStringNull(request.getParameter("declinedcoveredupto")) == null ? "" : request.getParameter("declinedcoveredupto");
                declinecovered_time = Functions.checkStringNull(request.getParameter("declinedcoveredtime")) == null ? "" : request.getParameter("declinedcoveredtime");
                chargebackcoveredStartdate = Functions.checkStringNull(request.getParameter("chargebackcovereduptoStartdate")) == null ? "" : request.getParameter("chargebackcovereduptoStartdate");
                chargebackcovered_time_start = Functions.checkStringNull(request.getParameter("chargebackcoveredtimeStart")) == null ? "" : request.getParameter("chargebackcoveredtimeStart");
                chargebackcoveredupto = Functions.checkStringNull(request.getParameter("chargebackcoveredupto")) == null ? "" : request.getParameter("chargebackcoveredupto");
                chargebackcovered_time = Functions.checkStringNull(request.getParameter("chargebackcoveredtime")) == null ? "" : request.getParameter("chargebackcoveredtime");
                reversedCoveredUptoStartdate = Functions.checkStringNull(request.getParameter("reversedcovereduptoStartdate")) == null ? "" : request.getParameter("reversedcovereduptoStartdate");
                reversecovered_time_start = Functions.checkStringNull(request.getParameter("reversedcoveredtimeStart")) == null ? "" : request.getParameter("reversedcoveredtimeStart");
                reversedCoveredUpto = Functions.checkStringNull(request.getParameter("reversedcoveredupto")) == null ? "" : request.getParameter("reversedcoveredupto");
                reversecovered_time = Functions.checkStringNull(request.getParameter("reversedcoveredtime")) == null ? "" : request.getParameter("reversedcoveredtime");
                banksettlement_report_file = Functions.checkStringNull(request.getParameter("settlement_report_file")) == null ? "" : request.getParameter("settlement_report_file");
                banksettlement_transaction_file = Functions.checkStringNull(request.getParameter("settlement_transaction_file")) == null ? "" : request.getParameter("settlement_transaction_file");
                if(bankWireManagerVO!=null)
                {
                  if(functions.isValueNull( bankWireManagerVO.getDeclinedcoveredStartdate()))
                    declinedcoveredStartdate = bankWireManagerVO.getDeclinedcoveredStartdate();
                  if(functions.isValueNull( bankWireManagerVO.getChargebackcoveredStartdate()))
                    chargebackcoveredStartdate = bankWireManagerVO.getChargebackcoveredStartdate();
                  if(functions.isValueNull( bankWireManagerVO.getReversedCoveredStartdate()))
                    reversedCoveredUptoStartdate = bankWireManagerVO.getReversedCoveredStartdate();
                  if(functions.isValueNull( bankWireManagerVO.getRollingreservereleaseStartdate()))
                    rollingReleaseStartdate=bankWireManagerVO.getRollingreservereleaseStartdate();

                  if(functions.isValueNull( bankWireManagerVO.getDeclinedcoveredtimeStarttime()))
                    declinecovered_time_start = bankWireManagerVO.getDeclinedcoveredtimeStarttime();
                  if(functions.isValueNull( bankWireManagerVO.getChargebackcoveredtimeStarttime()))
                    chargebackcovered_time_start = bankWireManagerVO.getChargebackcoveredtimeStarttime();
                  if(functions.isValueNull( bankWireManagerVO.getReversedcoveredtimeStarttime()))
                    reversecovered_time_start = bankWireManagerVO.getReversedcoveredtimeStarttime();
                  if(functions.isValueNull( bankWireManagerVO.getRollingreservereleaseStarttime()))
                    rollingRelease_time_start=bankWireManagerVO.getRollingreservereleaseStarttime();
                }
                request.setAttribute("bankWireManagerVO", bankWireManagerVO);
                session.setAttribute("bankWireManagerVO", bankWireManagerVO);

                ActionVO actionVO = null;
                if (("Add").equals(request.getParameter("name")))
                {
                  actionVO = new ActionVO();
                  actionVO.setAdd();
                }
                if (!("Add").equals(request.getParameter("name")))
                {
                  actionVO = (ActionVO) request.getAttribute("actionVO");
                  bankwiremanagerId = singleBankWireManagerVO.getBankwiremanagerId();
                  settleddate = singleBankWireManagerVO.getSettleddate();
                  //pgtypeId = singleBankWireManagerVO.getPgtypeId();
                  accountid = singleBankWireManagerVO.getAccountId();
                  mid = singleBankWireManagerVO.getMid();
                  bank_start_date = singleBankWireManagerVO.getBank_start_date();
                  bank_end_date = singleBankWireManagerVO.getBank_end_date();
                  server_start_date = singleBankWireManagerVO.getServer_start_date();
                  server_end_date = singleBankWireManagerVO.getServer_end_date();
                  processing_amount = singleBankWireManagerVO.getProcessing_amount();
                  grossAmount = singleBankWireManagerVO.getGrossAmount();
                  netfinal_amount = singleBankWireManagerVO.getNetfinal_amount();
                  unpaid_amount = singleBankWireManagerVO.getUnpaid_amount();
                  currency = singleBankWireManagerVO.getCurrency();
                  isrollingreservereleasewire = singleBankWireManagerVO.getIsrollingreservereleasewire();
                  rollingreservereleasedateupto = singleBankWireManagerVO.getRollingreservereleasedateupto();
                  declinedcoveredupto = singleBankWireManagerVO.getDeclinedcoveredupto();
                  chargebackcoveredupto = singleBankWireManagerVO.getChargebackcoveredupto();
                  reversedCoveredUpto = singleBankWireManagerVO.getReversedCoveredUpto();
                  banksettlement_report_file = singleBankWireManagerVO.getBanksettlement_report_file();
                  banksettlement_transaction_file = singleBankWireManagerVO.getBanksettlement_transaction_file();
                  isSettlementCronExceuted = singleBankWireManagerVO.getIsSettlementCronExceuted();
                  isPayoutCronExcuted = singleBankWireManagerVO.getIsPayoutCronExcuted();
                  ispaid = singleBankWireManagerVO.getIspaid();
                  bank_start_time = singleBankWireManagerVO.getBank_start_timestamp();
                  bank_end_time = singleBankWireManagerVO.getBank_end_timestamp();
                  server_start_time = singleBankWireManagerVO.getServer_start_timestamp();
                  server_end_time = singleBankWireManagerVO.getServer_end_timestamp();
                  settled_time = singleBankWireManagerVO.getSettled_timestamp();
                  rollingreservecovered_time = singleBankWireManagerVO.getRollingreservetime();
                  declinecovered_time = singleBankWireManagerVO.getDeclinedcoveredtime();
                  reversecovered_time = singleBankWireManagerVO.getReversedcoveredtime();
                  chargebackcovered_time = singleBankWireManagerVO.getChargebackcoveredtime();
                  ispartnerpommcronexecuted = singleBankWireManagerVO.getIsPartnerCommCronExecuted();
                  isagentcommcronexecuted = singleBankWireManagerVO.getIsAgentCommCronExecuted();

                  if(functions.isValueNull(singleBankWireManagerVO.getDeclinedcoveredStartdate())){
                    declinedcoveredStartdate = singleBankWireManagerVO.getDeclinedcoveredStartdate();
                  }
                  else{
                    declinedcoveredStartdate="";
                  }

                  if(functions.isValueNull(singleBankWireManagerVO.getReversedCoveredStartdate())){
                    reversedCoveredUptoStartdate = singleBankWireManagerVO.getReversedCoveredStartdate();
                  }
                  else{
                    reversedCoveredUptoStartdate="";
                  }

                  if(functions.isValueNull(singleBankWireManagerVO.getChargebackcoveredStartdate())){
                    chargebackcoveredStartdate = singleBankWireManagerVO.getChargebackcoveredStartdate();
                  }
                  else{
                    chargebackcoveredStartdate="";
                  }

                  if(functions.isValueNull(singleBankWireManagerVO.getRollingreservereleaseStartdate())){
                    rollingReleaseStartdate=singleBankWireManagerVO.getRollingreservereleaseStartdate();
                  }
                  else{
                    rollingReleaseStartdate="";
                  }

                  if(functions.isValueNull(singleBankWireManagerVO.getDeclinedcoveredtimeStarttime())){
                    declinecovered_time_start=singleBankWireManagerVO.getDeclinedcoveredtimeStarttime();
                  }
                  else{
                    declinecovered_time_start="";
                  }
                  if(functions.isValueNull(singleBankWireManagerVO.getChargebackcoveredtimeStarttime())){
                    chargebackcovered_time_start=singleBankWireManagerVO.getChargebackcoveredtimeStarttime();
                  }
                  else{
                    chargebackcovered_time_start="";
                  }
                  if(functions.isValueNull(singleBankWireManagerVO.getReversedcoveredtimeStarttime())){
                    reversecovered_time_start=singleBankWireManagerVO.getReversedcoveredtimeStarttime();
                  }
                  else{
                    reversecovered_time_start="";
                  }
                  if(functions.isValueNull(singleBankWireManagerVO.getRollingreservereleaseStarttime())){
                    rollingRelease_time_start=singleBankWireManagerVO.getRollingreservereleaseStarttime();
                  }
                  else{
                    rollingRelease_time_start="";
                  }

                  session.setAttribute("singleBankWireManagerVO", singleBankWireManagerVO);
                  request.setAttribute("singleBankWireManagerVO", singleBankWireManagerVO);
                }
            %>

            <form action="/partner/net/AddNewBankWire?ctoken=<%=ctoken%>" method="post" name="form1" class="form-horizontal">
              <input  name="ctoken" type="hidden" value="<%=ctoken%>" id="ctoken">
              <div class="widget-content padding">
                <%
                  ValidationErrorList error= (ValidationErrorList) request.getAttribute("error");
                  if(error!=null) {
                    for (Object errorList : error.errors()) {
                      ValidationException ve = (ValidationException) errorList;
                      out.println("<center><font class=\"textb\">" + ve.getMessage() + "</font></center>");
                    }
                  }
                %>
                <%
                  if(!actionVO.isAdd())
                  {
                %>
                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=addNewBankWire_Bank_WireId%></label>
                  <div class="col-md-4">
                    <input type="hidden" name="bankwiremangerid" value="<%=bankwiremanagerId%>">
                    <input class="form-control"  type="Text" name="accountid" id="bankwiremangerid1" value="<%=bankwiremanagerId%>" disabled>
                  </div>
                  <div class="col-md-4"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=addNewBankWire_Settled_Date%></label>
                  <div class="col-md-2">
                    <input class="datepicker" style="width:174px;" readonly name="settlementdate" value="<%=settleddate%>" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                  </div>
                  <div class="col-md-2">
                    <input class="form-control" name="settlementtime" value="<%=settled_time%>" placeholder="HH:MM:SS" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                  </div>
                  <div class="col-md-4"></div>
                </div>
                <%
                  }
                %>

                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=addNewBankWire_Account_Id%></label>
                  <div class="col-md-4">
                    <input name="accountid" class="form-control" style="width: 200px;" value="<%=accountid%>" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                      <%--<%
                        BankDAO bankDAO=new BankDAO();
                        List<TerminalVO> accountList=bankDAO.getTerminalVODetails();
                        for(TerminalVO terminalVO:accountList)
                        {
                          out.println("<option value=\""+terminalVO.getAccountId()+"\">"+terminalVO.getAccountId()+" - "+terminalVO.getMemberId()+"-"+terminalVO.getPartnerId()+"</option>");
                        }
                      %>--%>
                    </select>
                  </div>
                  <div class="col-md-4"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=addNewBankWire_Bank_Start_Date%></label>
                  <div class="col-md-2">
                    <input class="datepicker" style="width:174px;" readonly name="expected_startDate" value="<%=bank_start_date%>" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                  </div>
                  <div class="col-md-2">
                    <input class="form-control" name="expected_startTime" value="<%=bank_start_time%>" placeholder="HH:MM:SS" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                  </div>
                  <div class="col-md-4"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=addNewBankWire_Bank_End_Date%></label>
                  <div class="col-md-2">
                    <input class="datepicker" style="width:174px;" readonly name="expected_endDate" value="<%=bank_end_date%>" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                  </div>
                  <div class="col-md-2">
                    <input class="form-control" name="expected_endTime" value="<%=bank_end_time%>" placeholder="HH:MM:SS" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                  </div>
                  <div class="col-md-4"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=addNewBankWire_Processing_Amount%></label>
                  <div class="col-md-4">
                    <input type="Text" class="form-control" name="processingamount" value="<%=processing_amount%>" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                  </div>
                  <div class="col-md-4"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=addNewBankWire_Gross_Amount%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" name="grossamount"  value="<%=grossAmount%>" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                  </div>
                  <div class="col-md-4"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=addNewBankWire_Net_Final_Amount%></label>
                  <div class="col-md-4">
                    <input class="form-control"  type="Text" name="netfinalamout"  value="<%=netfinal_amount%>" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                  </div>
                  <div class="col-md-4"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=addNewBankWire_Unpaid_Amount%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" name="unpaidamount" value="<%=unpaid_amount%>" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                  </div>
                  <div class="col-md-4"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=addNewBankWire_isrollingreservereleasewire%></label>
                  <div class="col-md-4">
                    <select id="status" name="isrollingreservereleasewire" size="1" class="form-control" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                      <option value="Y" <%=isrollingreservereleasewire.equals("Y")?"selected":""%>>Yes</option>
                      <option value="N" <%=isrollingreservereleasewire.equals("N")?"selected":""%>>No</option>
                    </select>
                  </div>
                  <div class="col-md-4"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=addNewBankWire_Rolling_Reserve_DateUpto%></label>
                  <div class="col-md-2">
                    <input class="datepicker" style="width:174px;" readonly name="rollingreservedateupto" value="<%=rollingreservereleasedateupto%>"<%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                  </div>
                  <div class="col-md-2">
                    <input class="bootstrap-datetimepicker-widget" name="rollingRelease_Time" value="<%=rollingreservecovered_time%>" placeholder="HH:MM:SS" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                  </div>
                  <div class="col-md-4"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=addNewBankWire_Declined_Covered_DateUpto%></label>
                  <div class="col-md-2">
                    <input class="datepicker" style="width:174px;" readonly name="declinedcoveredupto" value="<%=declinedcoveredupto%>" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                  </div>
                  <div class="col-md-2">
                    <input class="bootstrap-datetimepicker-widget" name="declinedcoveredtime" value="<%=declinecovered_time%>" placeholder="HH:MM:SS" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                  </div>
                  <div class="col-md-4"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=addNewBankWire_Chargeback_Covered_DateUpto%></label>
                  <div class="col-md-2">
                    <input class="datepicker" style="width:174px;" readonly name="chargebackcoveredupto" value="<%=chargebackcoveredupto%>" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                  </div>
                  <div class="col-md-2">
                    <input class="bootstrap-datetimepicker-widget" name="chargebackcoveredtime" value="<%=chargebackcovered_time%>" placeholder="HH:MM:SS" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                  </div>
                  <div class="col-md-4"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=addNewBankWire_Reversed_Covered_DateUpto%></label>
                  <div class="col-md-2">
                    <input class="datepicker" style="width:174px;" readonly name="reversedcoveredupto" value="<%=reversedCoveredUpto%>" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                  </div>
                  <div class="col-md-2">
                    <input class="bootstrap-datetimepicker-widget" name="reversedcoveredtime" value="<%=reversecovered_time%>" placeholder="HH:MM:SS" <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
                  </div>
                  <div class="col-md-4"></div>
                </div>

                <%
                  if(!actionVO.isAdd())
                  {
                %>
                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=addNewBankWire_Is_SettlementCronExecuted%></label>
                  <div class="col-md-4">
                    <select id="status" name="issettlementcron" size="1" class="form-control">
                      <option value="Y" <%=isSettlementCronExceuted.equals("Y")?"selected":""%>><%=addNewBankWire_Yes%></option>
                      <option value="N" <%=isSettlementCronExceuted.equals("N")?"selected":""%>><%=addNewBankWire_No%></option>
                    </select>
                  </div>
                  <div class="col-md-4"></div>
                </div>
                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=addNewBankWire_Is_PayoutCronExecuted%></label>
                  <div class="col-md-4">
                    <select id="status" name="ispayoutcron" size="1" class="form-control">
                      <option value="Y" <%=isPayoutCronExcuted.equals("Y")?"selected":""%>><%=addNewBankWire_Yes%></option>
                      <option value="N" <%=isPayoutCronExcuted.equals("N")?"selected":""%>><%=addNewBankWire_No%></option>
                    </select>
                  </div>
                  <div class="col-md-4"></div>
                </div>
                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=addNewBankWire_Is_PartnerCommCronExecuted%></label>
                  <div class="col-md-4">
                    <select id="status" name="ispartnercommcronexecuted" size="1" class="form-control">
                      <option value="Y" <%=ispartnerpommcronexecuted.equals("Y")?"selected":""%>><%=addNewBankWire_Yes%></option>
                      <option value="N" <%=ispartnerpommcronexecuted.equals("N")?"selected":""%>><%=addNewBankWire_No%></option>
                    </select>
                  </div>
                  <div class="col-md-4"></div>
                </div>
                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=addNewBankWire_Is_AgentCommCronExecuted%></label>
                  <div class="col-md-4">
                    <select id="status" name="isagentcommcronexecuted" size="1" class="form-control">
                      <option value="Y" <%=isagentcommcronexecuted.equals("Y")?"selected":""%>><%=addNewBankWire_Yes%></option>
                      <option value="N" <%=isagentcommcronexecuted.equals("N")?"selected":""%>><%=addNewBankWire_No%></option>
                    </select>
                  </div>
                  <div class="col-md-4"></div>
                </div>
                <%
                  }
                %>

                <div class="form-group">
                  <div class="col-md-1"></div>
                  <label class="col-md-3 control-label"><%=addNewBankWire_Is_Paid%></label>
                  <div class="col-md-4">
                    <select id="status" name="isPaid" size="1" class="form-control">
                      <option value="Y" <%=ispaid.equals("Y")?"selected":""%>><%=addNewBankWire_Yes%></option>
                      <option value="N" <%=ispaid.equals("N")?"selected":""%>><%=addNewBankWire_No%></option>
                    </select>
                  </div>
                  <div class="col-md-4"></div>
                </div>

                <%
                  if(actionVO.isEdit())
                  {
                %>
                <div class="widget-content padding">
                  <div id="horizontal-form">
                    <div class="form-group col-md-12 has-feedback">
                      <center>
                        <label >&nbsp;</label>
                        <button type="submit" class="btn btn-default" id="submit" style="display: -webkit-box;" name="action" id="submit_button" value="<%=singleBankWireManagerVO.getBankwiremanagerId()%>_Update">
                          <input type="hidden" name="submit" value="Update">
                          <i class="fa fa-save"></i>&nbsp;&nbsp;Update
                        </button>
                      </center>
                    </div>
                  </div>
                </div>
                <%
                }
                else if(actionVO.isAdd()){
                %>

                <div class="widget-content padding">
                  <div id="horizontal-form">
                    <div class="form-group col-md-12 has-feedback">
                      <center>
                        <label >&nbsp;</label>
                        <button type="submit" class="btn btn-default" id="submit" style="display: -webkit-box;" name="action" id="submit_button" value="1_Add">
                          <input type="hidden" name="name" value="Add">
                          <i class="fa fa-save"></i>&nbsp;&nbsp;Save
                        </button>
                      </center>
                    </div>
                  </div>
                </div>
                <%
                  }
                %>
              </div>
            </form>
            <%
              }
            %>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>