<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="com.manager.vo.payoutVOs.*" %>
<%@ page import="com.manager.vo.*" %>
<%@ page import="com.manager.enums.Charge_unit" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.payment.exceptionHandler.PZDBViolationException" %>
<%@ page import="com.payment.exceptionHandler.PZExceptionHandler" %>
<%@ page import="com.payment.exceptionHandler.operations.PZOperations" %>
<%@ page import="com.manager.enums.Charge_keyword" %>
<%@ page import="org.eclipse.persistence.internal.queries.ReportItem" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 7/28/14
  Time: 2:06 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="ietest.jsp"%>
<%@ include file="Top.jsp" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","Account Summary");
%>
<html>
<head>
    <title><%=company%> Merchant Account Details > Account Summary</title>
    <script src="/merchant/javascript/html2pdf.js"></script>
</head>
<body>

<%--<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>--%>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<script type="text/javascript">
    $('#sandbox-container input').datepicker({

    });
</script>
<script>
    $(function() {
        $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
        /* $(".datepicker").datepicker({}).datepicker("setValue",new Date);*/
    });
</script>

<%!
    private static Logger logger = new Logger("accountSummary.jsp");
    Functions function = new Functions();
    TerminalManager terminalManager = new TerminalManager();
%>
<%
    String uId = "";
    if(session.getAttribute("role").equals("submerchant"))
    {
        uId = (String) session.getAttribute("userid");
    }
    else
    {
        uId = (String) session.getAttribute("merchantid");
    }

    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);

    Functions functions= new Functions();
    String accountSummary_Account_Summary = !functions.isEmptyOrNull(rb1.getString("accountSummary_Account_Summary"))?rb1.getString("accountSummary_Account_Summary"): "Account Summary";
    String accountSummary_terminalid = !functions.isEmptyOrNull(rb1.getString("accountSummary_terminalid"))?rb1.getString("accountSummary_terminalid"): "Terminal ID*";
    String accountSummary_Submit = !functions.isEmptyOrNull(rb1.getString("accountSummary_Submit"))?rb1.getString("accountSummary_Submit"): "Terminal ID*";
    String accountSummary_Report_Table = !functions.isEmptyOrNull(rb1.getString("accountSummary_Report_Table"))?rb1.getString("accountSummary_Report_Table"): "Report Table";
    String accountSummary_Merchant_Details = !functions.isEmptyOrNull(rb1.getString("accountSummary_Merchant_Details"))?rb1.getString("accountSummary_Merchant_Details"): "Merchant Details";
    String accountSummary_Merchant_ID = !functions.isEmptyOrNull(rb1.getString("accountSummary_Merchant_ID"))?rb1.getString("accountSummary_Merchant_ID"): "Merchant ID:";
    String accountSummary_Terminal_ID = !functions.isEmptyOrNull(rb1.getString("accountSummary_Terminal_ID"))?rb1.getString("accountSummary_Terminal_ID"): "Terminal ID:";
    String accountSummary_select_terminal_id = !functions.isEmptyOrNull(rb1.getString("accountSummary_select_terminal_id"))?rb1.getString("accountSummary_select_terminal_id"): "Select Terminal ID";
    String accountSummary_terminal_allocated = !functions.isEmptyOrNull(rb1.getString("accountSummary_terminal_allocated"))?rb1.getString("accountSummary_terminal_allocated"): "No Terminals Allocated";
    String accountSummary_Company_Name = !functions.isEmptyOrNull(rb1.getString("accountSummary_Company_Name"))?rb1.getString("accountSummary_Company_Name"): "Company Name:";
    String accountSummary_Contact_Person = !functions.isEmptyOrNull(rb1.getString("accountSummary_Contact_Person"))?rb1.getString("accountSummary_Contact_Person"): "Contact Person:";
    String accountSummary_Payment_Mode = !functions.isEmptyOrNull(rb1.getString("accountSummary_Payment_Mode"))?rb1.getString("accountSummary_Payment_Mode"): "Payment Mode:";
    String accountSummary_Card_Type = !functions.isEmptyOrNull(rb1.getString("accountSummary_Card_Type"))?rb1.getString("accountSummary_Card_Type"): "Card Type:";
    String accountSummary_Currency = !functions.isEmptyOrNull(rb1.getString("accountSummary_Currency"))?rb1.getString("accountSummary_Currency"): "Currency:";
    String accountSummary_Statement_Period = !functions.isEmptyOrNull(rb1.getString("accountSummary_Statement_Period"))?rb1.getString("accountSummary_Statement_Period"): "Statement Period:";
    String accountSummary_Statement_From = !functions.isEmptyOrNull(rb1.getString("accountSummary_Statement_From"))?rb1.getString("accountSummary_Statement_From"): "From:";
    String accountSummary_Statement_To = !functions.isEmptyOrNull(rb1.getString("accountSummary_Statement_To"))?rb1.getString("accountSummary_Statement_To"): "To:";
    String accountSummary_Declined_Period1 = !functions.isEmptyOrNull(rb1.getString("accountSummary_Declined_Period1"))?rb1.getString("accountSummary_Declined_Period1"): "Declined Period:";
    String accountSummary_Declined_From = !functions.isEmptyOrNull(rb1.getString("accountSummary_Declined_From"))?rb1.getString("accountSummary_Declined_From"): "From:";
    String accountSummary_Declined_To = !functions.isEmptyOrNull(rb1.getString("accountSummary_Declined_To"))?rb1.getString("accountSummary_Declined_To"): "To:";
    String accountSummary_Reversal_Period1 = !functions.isEmptyOrNull(rb1.getString("accountSummary_Reversal_Period1"))?rb1.getString("accountSummary_Reversal_Period1"): "Reversal Period:";
    String accountSummary__Reversal_From = !functions.isEmptyOrNull(rb1.getString("accountSummary__Reversal_From"))?rb1.getString("accountSummary__Reversal_From"): "From::";
    String accountSummary_Reversal_To = !functions.isEmptyOrNull(rb1.getString("accountSummary_Reversal_To"))?rb1.getString("accountSummary_Reversal_To"): "To:";
    String accountSummary_ChargeBack_Period = !functions.isEmptyOrNull(rb1.getString("accountSummary_ChargeBack_Period"))?rb1.getString("accountSummary_ChargeBack_Period"): "ChargeBack Period:";
    String accountSummary_ChargeBack_Period_From = !functions.isEmptyOrNull(rb1.getString("accountSummary_ChargeBack_Period_From"))?rb1.getString("accountSummary_ChargeBack_Period_From"): "From:";
    String accountSummary_ChargeBack_Period_To = !functions.isEmptyOrNull(rb1.getString("accountSummary_ChargeBack_Period_To"))?rb1.getString("accountSummary_ChargeBack_Period_To"): "To:";
    String accountSummary_Rolling_Reverse_Date = !functions.isEmptyOrNull(rb1.getString("accountSummary_Rolling_Reverse_Date"))?rb1.getString("accountSummary_Rolling_Reverse_Date"): "Rolling Reverse Date :";
    String accountSummary_Rolling_Reverse_Start_Date = !functions.isEmptyOrNull(rb1.getString("accountSummary_Rolling_Reverse_Start_Date"))?rb1.getString("accountSummary_Rolling_Reverse_Start_Date"): "Start Date :";
    String accountSummary_Rolling_Reverse_End_Date = !functions.isEmptyOrNull(rb1.getString("accountSummary_Rolling_Reverse_End_Date"))?rb1.getString("accountSummary_Rolling_Reverse_End_Date"): "End Date :";
    String accountSummary_Charges_Details = !functions.isEmptyOrNull(rb1.getString("accountSummary_Charges_Details"))?rb1.getString("accountSummary_Charges_Details"): " Charges Details :";
    String accountSummary_FEES = !functions.isEmptyOrNull(rb1.getString("accountSummary_FEES"))?rb1.getString("accountSummary_FEES"): "FEES:";
    String accountSummary_Rates_Fees = !functions.isEmptyOrNull(rb1.getString("accountSummary_Rates_Fees"))?rb1.getString("accountSummary_Rates_Fees"): "Rates_Fees";
    String accountSummary_Counter = !functions.isEmptyOrNull(rb1.getString("accountSummary_Counter"))?rb1.getString("accountSummary_Counter"): "Counter";
    String accountSummary_Amount = !functions.isEmptyOrNull(rb1.getString("accountSummary_Amount"))?rb1.getString("accountSummary_Amount"): "Amount";
    String accountSummary_Total = !functions.isEmptyOrNull(rb1.getString("accountSummary_Total"))?rb1.getString("accountSummary_Total"): "Total";
    String accountSummary_Total1 = !functions.isEmptyOrNull(rb1.getString("accountSummary_Total1"))?rb1.getString("accountSummary_Total1"): "Total";
    String accountSummary_Generated_Reserve_Details = !functions.isEmptyOrNull(rb1.getString("accountSummary_Generated_Reserve_Details"))?rb1.getString("accountSummary_Generated_Reserve_Details"): "Generated Reserve Details";
    String accountSummary_Generated_Reserve_Details_Total = !functions.isEmptyOrNull(rb1.getString("accountSummary_Generated_Reserve_Details_Total"))?rb1.getString("accountSummary_Generated_Reserve_Details_Total"): " Total";
    String accountSummary_Generated_Refunded_Details_Total = !functions.isEmptyOrNull(rb1.getString("accountSummary_Generated_Refunded_Details_Total"))?rb1.getString("accountSummary_Generated_Refunded_Details_Total"): " Refunded Reserve Details";
    String accountSummary_Reserve_Refunded_Amount = !functions.isEmptyOrNull(rb1.getString("accountSummary_Reserve_Refunded_Amount"))?rb1.getString("accountSummary_Reserve_Refunded_Amount"): "Total";
    String accountSummary_Summary1 = !functions.isEmptyOrNull(rb1.getString("accountSummary_Summary1"))?rb1.getString("accountSummary_Summary1"): "Summary";
    String accountSummary_Total_Processing_Amount = !functions.isEmptyOrNull(rb1.getString("accountSummary_Total_Processing_Amount"))?rb1.getString("accountSummary_Total_Processing_Amount"): "Total Processing Amount";
    String accountSummary_Total_Fees = !functions.isEmptyOrNull(rb1.getString("accountSummary_Total_Fees"))?rb1.getString("accountSummary_Total_Fees"): "Total Fees";
    String accountSummary_Total_Chargeback_Reversal = !functions.isEmptyOrNull(rb1.getString("accountSummary_Total_Chargeback_Reversal"))?rb1.getString("accountSummary_Total_Chargeback_Reversal"): "Total Chargeback/Reversal";
    String accountSummary_Generated_Reserve = !functions.isEmptyOrNull(rb1.getString("accountSummary_Generated_Reserve"))?rb1.getString("accountSummary_Generated_Reserve"): "Generated Reserve";
    String accountSummary_Gross_Amount1 = !functions.isEmptyOrNull(rb1.getString("accountSummary_Gross_Amount1"))?rb1.getString("accountSummary_Gross_Amount1"): "Gross Amount";
    String accountSummary_Paid_Amount = !functions.isEmptyOrNull(rb1.getString("accountSummary_Paid_Amount"))?rb1.getString("accountSummary_Paid_Amount"): " Paid Amount";
    String accountSummary_Previous_Balance_Amount = !functions.isEmptyOrNull(rb1.getString("accountSummary_Previous_Balance_Amount"))?rb1.getString("accountSummary_Previous_Balance_Amount"): " Previous Balance Amount";
    String accountSummary_Refunded_Rolling_Reserve = !functions.isEmptyOrNull(rb1.getString("accountSummary_Refunded_Rolling_Reserve"))?rb1.getString("accountSummary_Refunded_Rolling_Reserve"): " Refunded Rolling Reserve";
    String accountSummary_Total_Amount_Funded = !functions.isEmptyOrNull(rb1.getString("accountSummary_Total_Amount_Funded"))?rb1.getString("accountSummary_Total_Amount_Funded"): "Total Amount Funded";
    String accountSummary_Note = !functions.isEmptyOrNull(rb1.getString("accountSummary_Note"))?rb1.getString("accountSummary_Note"): "Note";
    String accountSummary_View = !functions.isEmptyOrNull(rb1.getString("accountSummary_View"))?rb1.getString("accountSummary_View"): "View";
    String accountSummary_sorry = !functions.isEmptyOrNull(rb1.getString("accountSummary_sorry"))?rb1.getString("accountSummary_sorry"): "Sorry";
    String accountSummary_records = !functions.isEmptyOrNull(rb1.getString("accountSummary_records"))?rb1.getString("accountSummary_records"): "No records found for given search criteria.";



    try
    {
        //List<TerminalVO> terminalVO = terminalManager.getTerminalsByMerchantId(session.getAttribute("merchantid").toString());
        String fromdate = null;
        String todate = null;
        String terminalid = "";
        if(request.getAttribute("terminalid")!=null)
        {
            terminalid = request.getAttribute("terminalid").toString();
        }

        //date functionality if neccessary
        Date date = new Date();
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

        String Date = originalFormat.format(date);

        fromdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? Date : request.getParameter("fromdate");
        todate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");
%>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=accountSummary_Account_Summary%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <%--<div class="form foreground bodypanelfont_color panelbody_color">
                            <h2 class="col-md-12 background panelheading_color headpanelfont_color"><i class="fa fa-th-large"></i>&nbsp;&nbsp;Merchant Account Summary</h2>
                            <hr class="hrform">
                        </div>--%>
                        <%
                            String terminalCurrency ="";
                            StringBuffer terminalBuffer = new StringBuffer();
                            if (request.getParameter("MES") != null)
                            {
                                String mes = request.getParameter("MES");
                                if (mes.equals("ERR"))
                                {
                                    if(request.getAttribute("error")!=null)
                                    {
                                        ValidationErrorList error = (ValidationErrorList) request.getAttribute("error");
                                        for (Object errorList : error.errors())
                                        {
                                            ValidationException ve = (ValidationException) errorList;

                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + ve.getMessage() + "</h5>");
                                        }
                                    }
                                    if(request.getAttribute("catchError")!=null)
                                    {
                                        //out.println("<center><li class=\"alert alert-danger alert-dismissable\"><b>" + request.getAttribute("catchError") + "</b></li></center>");
                                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + request.getAttribute("catchError") + "</h5>");
                                    }
                                }
                            }
                        %>
                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <form  name="form" method="post" action="/merchant/servlet/AccountSummary?ctoken=<%=ctoken%>" class="form-horizontal">
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">

                                    <div class="form-group col-md-8">
                                        <label class="col-sm-3 control-label"><%=accountSummary_terminalid%></label>
                                        <div class="col-sm-4">
                                            <select size="1" name="terminalid" class="form-control">
                                                <%--<option value="all">All</option>--%>
                                                <%
                                                    terminalBuffer.append("(");
                                                    TerminalManager terminalManager=new TerminalManager();
                                                    List<TerminalVO> terminalVOList=terminalManager.getMemberandUserTerminalList(uId, String.valueOf(session.getAttribute("role")));
                                                    if(terminalVOList.size()>0)
                                                    {
                                                %>
                                                <option value=""><%=accountSummary_select_terminal_id%></option>
                                                <%
                                                    for (TerminalVO terminalVO:terminalVOList)
                                                    {
                                                        String str1 = "";
                                                        String active = "";
                                                        if (terminalVO.getIsActive().equalsIgnoreCase("Y"))
                                                        {
                                                            active = "Active";
                                                        }
                                                        else
                                                        {
                                                            active = "InActive";
                                                        }
                                                        if(terminalid.equals(terminalVO.getTerminalId()))
                                                        {
                                                            terminalCurrency = terminalVO.getCurrency();
                                                            str1= "selected";
                                                            terminalid = terminalVO.getTerminalId();
                                                        }
                                                        if(terminalBuffer.length()!=0 && terminalBuffer.length()!=1)
                                                        {
                                                            terminalBuffer.append(",");
                                                        }
                                                        terminalBuffer.append(terminalVO.getTerminalId());
                                                %>
                                                <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO.getTerminalId())%>" <%=str1%>> <%=ESAPI.encoder().encodeForHTML(terminalVO.getTerminalId()+"-"+terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+terminalVO.getCurrency()+"-"+active)%> </option>
                                                <%
                                                    }
                                                    terminalBuffer.append(")");
                                                }
                                                else
                                                {
                                                %>
                                                <option value="NoTerminals"><%=accountSummary_terminal_allocated%></option>
                                                <%
                                                    }
                                                %>
                                            </select>
                                        </div>
                                    </div>
                                    <%--<div class="form-group col-md-3 has-feedback">&nbsp;</div>--%>
                                    <div class="form-group col-md-3">
                                        <div class="col-sm-offset-2 col-sm-3">
                                            <button type="submit" class="btn btn-default"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=accountSummary_Submit%></button>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=accountSummary_Report_Table%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">
                            <input type="hidden" value="<%=terminalBuffer.toString()%>" name="terminalbuffer">
                            <input type="hidden" name="currency" value=<%=terminalCurrency%>>
                            <input type="hidden" name="terminalid" value=<%=terminalid%>>

                            <%
                                AccountVO accountVO = (AccountVO)request.getAttribute("AccountVO");
                                if(accountVO!=null)
                                {
                                    //member Details
                                    MerchantDetailsVO merchantDetailsVO1 = accountVO.getMerchantDetailsVO();
                                    //chargesMap
                                    HashMap<String,ChargeDetailsVO> chargeMap = accountVO.getChargesMap();
                                    //ChargeFee Total
                                    double chargeFeeTotal=accountVO.getTotalFees();
                                    //ReserveGeneratedMap
                                    HashMap<String,ReserveGeneratedVO> reserveGeneratedHashMap = accountVO.getReserveGeneratedMap();
                                    //ReserveGenerated (Total)
                                    double totalOfReserveGeneratedAmount=accountVO.getTotalOfReserveGeneratedAmount();
                                    //ReserveRefunded
                                    HashMap<String,ReserveRefundVO> reserveRefundHashMap=accountVO.getReserveRefundMap();
                                    //ReserveRefunded (Total)
                                    double totalOfReserveRefundedAmount=accountVO.getTotalOfReserveRefundAmount();
                                    // TotalProcessingCharges
                                    double totalProcessingAmount=accountVO.getTotalProcessingAmount();
                                    //totalChargeBack & Reversal Amount
                                    double totalReverseAndChargebackAmount=accountVO.getTotalReverseAndChargebackAmount();
                                    //Total of BalanceAmount
                                    double totalBalanceAmount=accountVO.getTotalBalanceAmount();
                                    //wireCharges
                                    WireChargeVO wireChargeVO=accountVO.getFinalWireVO();
                                    //GrossTypeCharges
                                    GrossChargeVO grossChargeVO= accountVO.getGrossTypeCharges();
                                    //WireAmountVo
                                    WireAmountVO wireAmountVO= accountVO.getWireAmountVO();
                                    //totalAmountRefunded
                                    double totalFundedToBank=accountVO.getTotalFundedToBank();
                                    //terminalVo
                                    TerminalVO TerminalVO=accountVO.getTerminalVO();
                                    //Currency Type
                                    String Currency=accountVO.getCurrency();
                                    //InputDateVO
                                    InputDateVO inputDateVO= accountVO.getInputDateVO();
                                    //calculatedReserveRefund
                                    CalculatedReserveRefundVO calculatedReserveRefundVO = accountVO.getCalculatedReserveRefund();
                                    //RollingReserveDate
                                    RollingReserveDateVO rollingReserveDateVO= accountVO.getRollingReserveDateVO();

                                    TransactionSummaryVO transactionSummaryVO = accountVO.getTransactionSummaryVO();

                            %>
                            <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>
                                    <tr>
                                        <td  colspan="5" valign="middle" align="center" style="color: white;"><b><%=accountSummary_Merchant_Details%></b></td>
                                    </tr>
                                </thead>

                                <tr>
                                    <td colspan="3" valign="middle" align="left" ><%=accountSummary_Merchant_ID%></td>
                                    <td colspan="2" valign="middle" align="left" ><%=TerminalVO.getMemberId()%></td>
                                </tr>
                                <tr>
                                    <td colspan="3" valign="middle" align="left" ><%=accountSummary_Terminal_ID%></td>
                                    <td colspan="2" valign="middle" align="left" ><%=TerminalVO.getTerminalId()%></td>
                                </tr>
                                <tr>
                                    <td colspan="3" valign="middle" align="left" ><%=accountSummary_Company_Name%></td>
                                    <td colspan="2" valign="middle" align="left" ><%=merchantDetailsVO1.getCompany_name()%></td>
                                </tr>
                                <tr>
                                    <td  colspan="3" valign="middle" align="left" ><%=accountSummary_Contact_Person%></td>
                                    <td colspan="2" valign="middle" align="left" ><%=merchantDetailsVO1.getContact_persons()%></td>
                                </tr>
                                <tr>
                                    <td colspan="3" valign="middle" align="left" ><%=accountSummary_Payment_Mode%></td>
                                    <td colspan="2" valign="middle" align="left" ><%=TerminalVO.getPaymentTypeName()%></td>
                                </tr>
                                <tr>
                                    <td colspan="3" valign="middle" align="left" ><%=accountSummary_Card_Type%></td>
                                    <td colspan="2" valign="middle" align="left" ><%=TerminalVO.getCardType()%></td>
                                </tr>
                                <tr>
                                    <td colspan="3" valign="middle" align="left" ><%=accountSummary_Currency%></td>
                                    <td colspan="2" valign="middle" align="left" ><%=Currency%></td>
                                </tr>

                                <tr>
                                    <td colspan="2" rowspan="2" valign="middle" align="left" ><%=accountSummary_Statement_Period%></td>
                                    <td colspan=""  valign="middle" align="left" ><%=accountSummary_Statement_From%></td>
                                    <td colspan="2" valign="middle" align="left" > <%=transactionSummaryVO.getMinOfAllStatusDate()%></td>
                                </tr>
                                <tr>
                                    <td colspan=""  valign="middle" align="left" ><%=accountSummary_Statement_To%></td>
                                    <td colspan="2" valign="middle" align="left" ><%=transactionSummaryVO.getMaxOfAllStatusDate()%></td>
                                </tr>
                                <tr>
                                    <td colspan="2" rowspan="2" valign="middle" align="left" ><%=accountSummary_Declined_Period1%></td>
                                    <td colspan=""  valign="middle" align="left" ><%=accountSummary_Declined_From%></td>
                                    <td colspan="2" valign="middle" align="left" > <%=function.isValueNull(transactionSummaryVO.getMinAuthFailedDate())?transactionSummaryVO.getMinAuthFailedDate():"N.A"%></td>
                                </tr>
                                <tr>
                                    <td colspan=""  valign="middle" align="left" ><%=accountSummary_Declined_To%></td>
                                    <td colspan="2" valign="middle" align="left" ><%=function.isValueNull(transactionSummaryVO.getMaxAuthFailedDate())?transactionSummaryVO.getMaxAuthFailedDate():"N.A"%></td>
                                </tr>
                                <tr>
                                    <td colspan="2" rowspan="2" valign="middle" align="left" ><%=accountSummary_Reversal_Period1%></td>
                                    <td colspan=""  valign="middle" align="left" ><%=accountSummary__Reversal_From%></td>
                                    <td colspan="2" valign="middle" align="left" > <%=function.isValueNull(transactionSummaryVO.getMinReversedDate())?transactionSummaryVO.getMinReversedDate():"N.A"%></td>
                                </tr>
                                <tr>
                                    <td colspan=""  valign="middle" align="left" ><%=accountSummary_Reversal_To%></td>
                                    <td colspan="2" valign="middle" align="left" ><%=function.isValueNull(transactionSummaryVO.getMaxReversedDate())?transactionSummaryVO.getMaxReversedDate():"N.A"%></td>
                                </tr>
                                <tr>
                                    <td colspan="2" rowspan="2" valign="middle" align="left" ><%=accountSummary_ChargeBack_Period%></td>
                                    <td colspan=""  valign="middle" align="left" ><%=accountSummary_ChargeBack_Period_From%></td>
                                    <td colspan="2" valign="middle" align="left" > <%=function.isValueNull(transactionSummaryVO.getMinChargeBackDate())?transactionSummaryVO.getMinChargeBackDate():"N.A"%></td>
                                </tr>
                                <tr>
                                    <td colspan=""  valign="middle" align="left" ><%=accountSummary_ChargeBack_Period_To%></td>
                                    <td colspan="2" valign="middle" align="left" ><%=function.isValueNull(transactionSummaryVO.getMaxChargeBackDate())?transactionSummaryVO.getMaxChargeBackDate():"N.A"%></td>
                                </tr>
                                <%
                                    if(function.isValueNull(rollingReserveDateVO.getRollingReserveStartDate()) || function.isValueNull(rollingReserveDateVO.getRollingReserveEndDate()))
                                    {
                                %>
                                <tr>
                                    <td colspan="2" rowspan="2" valign="middle" align="left" ><%=accountSummary_Rolling_Reverse_Date%></td>
                                    <td colspan=""  valign="middle" align="left" ><%=accountSummary_Rolling_Reverse_Start_Date%></td>
                                    <td colspan="2" valign="middle" align="left" ><%=function.isValueNull(rollingReserveDateVO.getRollingReserveStartDate())?rollingReserveDateVO.getRollingReserveStartDate():transactionSummaryVO.getMinOfAllStatusDate()%></td>
                                </tr>
                                <tr>
                                    <td colspan=""  valign="middle" align="left" ><%=accountSummary_Rolling_Reverse_End_Date%></td>
                                    <td colspan="2" valign="middle" align="left" ><%=function.isValueNull(rollingReserveDateVO.getRollingReserveEndDate())?rollingReserveDateVO.getRollingReserveEndDate():transactionSummaryVO.getMaxOfAllStatusDate()%></td>
                                </tr>
                                <%
                                    }
                                %>
                                <thead>
                                    <tr><td  colspan="5" valign="middle" align="center" style="color: white;" ><b><%=accountSummary_Charges_Details%></b></td>
                                    </tr>
                                </thead>

                                <tr>
                                    <td valign="middle" align="center" ><%=accountSummary_FEES%></td>
                                    <td valign="middle" align="center" ><%=accountSummary_Rates_Fees%></td>
                                    <td valign="middle" align="center" ><%=accountSummary_Counter%></td>
                                    <td valign="middle" align="center" ><%=accountSummary_Amount%></td>
                                    <td valign="middle" align="center" ><%=accountSummary_Total%></td>
                                </tr>

                                <%
                                    for(Map.Entry<String, ChargeDetailsVO> chargeDetailsPairs : chargeMap.entrySet())
                                    {
                                        ChargeDetailsVO chargeDetailsVO=chargeDetailsPairs.getValue();
                                %>
                                <tr>
                                    <td valign="middle" align="center" data-label="FEES" ><%=chargeDetailsVO.getChargeName()%></td>
                                    <td valign="middle" align="center" data-label="Rates/Fees" ><%=chargeDetailsVO.getChargeValue()%></td>
                                    <td valign="middle" align="center" data-label="Counter" ><%=chargeDetailsVO.getCount()%></td>
                                    <%
                                        if(Charge_unit.Percentage.name().equals(chargeDetailsVO.getValueType()) || chargeDetailsVO.getChargeName().contains(Charge_keyword.Chargeback.name()) || chargeDetailsVO.getChargeName().contains("Reversal")|| chargeDetailsVO.getChargeName().contains(Charge_keyword.Payout.name()))
                                        {
                                    %>
                                    <td valign="middle" align="center" data-label="Amount" ><%=chargeDetailsVO.getAmount()%></td>
                                    <%
                                    }
                                    else
                                    {
                                    %>
                                    <td valign="middle" align="center" >-</td>
                                    <%
                                        }
                                    %>
                                    <td valign="middle" align="center" data-label="Total" ><%=chargeDetailsVO.getTotal()%></td>
                                </tr>

                                <%
                                    }
                                %>
                                <tr>
                                    <td  colspan="4" valign="middle" align="right"  ><%=accountSummary_Total1%></td>
                                    <td valign="middle" align="center" ><b><%=chargeFeeTotal%></b></td>
                                </tr>

                                <thead>
                                <tr>
                                    <td valign="middle" align="center" style="color: white;"  colspan="5"><b><%=accountSummary_Generated_Reserve_Details%></b></td>
                                </tr>
                                </thead>

                                <%
                                    for(Map.Entry<String,ReserveGeneratedVO> reserveGeneratedPair : reserveGeneratedHashMap.entrySet())
                                    {
                                        ReserveGeneratedVO reserveGeneratedVO=reserveGeneratedPair.getValue();
                                %>
                                <tr>
                                    <td valign="middle" align="left" ><%=reserveGeneratedVO.getChargeName()%></td>
                                    <td valign="middle" align="right" ><%=reserveGeneratedVO.getChargeValue()%></td>
                                    <td valign="middle" align="right" ><%=reserveGeneratedVO.getCount()%></td>
                                    <td valign="middle" align="right" ><%=reserveGeneratedVO.getAmount()%></td>
                                    <td valign="middle" align="center" ><%=reserveGeneratedVO.getTotal()%></td>
                                </tr>
                                <%
                                    }
                                %>
                                <tr>
                                    <%--<td valign="middle" align="left" bgcolor=""></td>
                                 <td valign="middle" align="left" bgcolor=""></td>
                                 <td valign="middle" align="left" bgcolor=""></td>--%>
                                    <td  colspan="4" valign="middle" align="right" ><%=accountSummary_Generated_Reserve_Details_Total%></td>
                                    <td valign="middle" align="center" ><b><%=totalOfReserveGeneratedAmount%></b></td>
                                </tr>
                                <thead>
                                <tr>
                                    <td valign="middle" align="center" style="color: white;"  colspan="5"><b><%=accountSummary_Generated_Refunded_Details_Total%></b></td>
                                </tr>
                                </thead>

                                <%
                                    for(Map.Entry<String,ReserveRefundVO> reserveRefundPair : reserveRefundHashMap.entrySet())
                                    {
                                        ReserveRefundVO reserveRefundVO= reserveRefundPair.getValue();
                                %>
                                <tr>
                                    <td valign="middle" align="left" ><%=reserveRefundVO.getChargeName()%></td>
                                    <%
                                        if (function.isValueNull(reserveRefundVO.getChargeValue()))
                                        {
                                    %>
                                    <td valign="middle" align="right" ><%=reserveRefundVO.getChargeValue()%></td>
                                    <%
                                    }
                                    else {
                                    %>

                                    <td valign="middle" align="right" >-</td>
                                    <%
                                        }
                                    %>

                                    <td valign="middle" align="right" ><%=reserveRefundVO.getCount()%></td>
                                    <td valign="middle" align="right" ><%=reserveRefundVO.getAmount()%></td>
                                    <td valign="middle" align="center" ><%=reserveRefundVO.getTotal()%></td>
                                </tr>
                                <%
                                    }
                                    if(calculatedReserveRefundVO!=null)
                                    {
                                %>
                                <tr>
                                    <td valign="middle" align="left" ><%=calculatedReserveRefundVO.getChargeName()%></td>
                                    <td valign="middle" align="right" ><%=calculatedReserveRefundVO.getChargeValue()%></td>
                                    <td valign="middle" align="right" ><%=calculatedReserveRefundVO.getCount()%></td>
                                    <td valign="middle" align="right" ><%=calculatedReserveRefundVO.getAmount()%></td>
                                    <td valign="middle" align="center" ><%=calculatedReserveRefundVO.getTotal()%></td>
                                </tr>
                                <%
                                    }
                                %>
                                <tr>
                                    <td  colspan="4" valign="middle" align="right" ><%=accountSummary_Reserve_Refunded_Amount%></td>
                                    <td valign="middle" align="center" ><b><%=totalOfReserveRefundedAmount%></b></td>
                                </tr>
                                <thead>
                                <tr>
                                    <td colspan="5" valign="middle" align="center" style="color: white;" ><b><%=accountSummary_Summary1%></b></td>

                                </tr>
                                </thead>
                                <tr>
                                    <td colspan="4" valign="middle" align="left" ><%=accountSummary_Total_Processing_Amount%>
                                    <td valign="middle" align="center" ><%=Functions.convert2Decimal(totalProcessingAmount)%></td>
                                </tr>
                                <tr>
                                    <td colspan="4" valign="middle" align="left" ><%=accountSummary_Total_Fees%></td>
                                    <td valign="middle" align="center" ><%if(chargeFeeTotal>0){out.print("-"+chargeFeeTotal);}else{out.print(chargeFeeTotal);}%></td>
                                </tr>
                                <tr>
                                    <td colspan="4" valign="middle" align="left" ><%=accountSummary_Total_Chargeback_Reversal%></td>
                                    <td valign="middle" align="center" ><%if(totalReverseAndChargebackAmount>0){out.print("-"+Functions.convert2Decimal(totalReverseAndChargebackAmount));}else{out.print(Functions.convert2Decimal(totalReverseAndChargebackAmount));}%></td>
                                </tr>
                                <tr>
                                    <td colspan="4" valign="middle" align="left" ><%=accountSummary_Generated_Reserve%></td>
                                    <td valign="middle" align="center" ><%if(totalOfReserveGeneratedAmount>0){out.print("-"+totalOfReserveGeneratedAmount);}else{out.print(totalOfReserveGeneratedAmount);}%></td>
                                </tr>
                                <thead>
                                <tr>
                                    <td colspan="4" valign="middle" align="left" style="color: white;"><b><%=accountSummary_Gross_Amount1%></b></td>
                                    <td valign="middle" align="center" style="color: white;"><b><%=totalBalanceAmount%></b></td>
                                </tr>
                                </thead>
                                <%
                                    if(wireChargeVO!=null)
                                    {%>
                                <tr>
                                    <td valign="middle" align="left" ><%=wireChargeVO.getChargeName()%></td>
                                    <td valign="middle" align="right" ><%=wireChargeVO.getChargeValue()%></td>
                                    <td valign="middle" align="right" ><%=wireChargeVO.getCount()%></td>
                                    <td valign="middle" align="right" >-</td>
                                    <td valign="middle" align="center" ><%if(wireChargeVO.getTotal()>0){out.print("-"+wireChargeVO.getTotal());}else{out.print(wireChargeVO.getTotal());}%></td>
                                </tr>
                                <%}
                                %>

                                <%
                                    if(grossChargeVO!=null)
                                    {
                                %>
                                </tr>
                                <td valign="middle" align="left" ><%=grossChargeVO.getChargeName()%></td>
                                <td valign="middle" align="right" ><%=grossChargeVO.getChargeValue()%></td>
                                <td valign="middle" align="right" ><%=grossChargeVO.getCount()%></td>
                                <td valign="middle" align="right" ><%=grossChargeVO.getAmount()%></td>
                                <td valign="middle" align="center" ><%if(grossChargeVO.getTotal()>0){out.print("-"+grossChargeVO.getTotal());}else{out.print(grossChargeVO.getTotal());}%></td>
                                </tr>
                                <%
                                    }
                                %>
                                <tr>
                                    <td colspan="4" valign="middle" align="left" ><%=accountSummary_Paid_Amount%></td>
                                    <td valign="middle" align="center" ><%if(wireAmountVO.getPaidAmount()>0){out.print("-"+wireAmountVO.getPaidAmount());}else{out.print(wireAmountVO.getPaidAmount());}%></td>
                                </tr>
                                <tr>
                                    <td colspan="4" valign="middle" align="left" ><%=accountSummary_Previous_Balance_Amount%></td>
                                    <td valign="middle" align="center" ><%=wireAmountVO.getWireBalanceAmount()%></td>
                                </tr>
                                <tr>
                                    <td colspan="4" valign="middle" align="left" ><%=accountSummary_Refunded_Rolling_Reserve%></td>
                                    <td valign="middle" align="center" ><%=totalOfReserveRefundedAmount%></td>
                                </tr>
                                <thead>
                                <tr>
                                    <td colspan="4" valign="middle" align="left" style="color: white;"><b><%=accountSummary_Total_Amount_Funded%></b></td>
                                    <td valign="middle" align="center" style="color: white;"><b><%=totalFundedToBank%></b></td>
                                </tr>
                                </thead>

                            </table>
                            <div style="position: relative;left: 42%;">
                                <button type="button" id="download"  class="btn btn-default" style="display: -webkit-box; font-size: 16px "><i
                                        class="fa fa-files-o" > Export To Pdf</i>&nbsp;
                                </button>
                            </div>
                        </div>

                        <%

                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation1(accountSummary_sorry, accountSummary_records));

                                }
                            }
                            catch(PZDBViolationException dbe)
                            {
                                logger.error("Db exception::",dbe);
                                out.println(Functions.NewShowConfirmation1("Sorry", "Kindly check Account Summary after some time."));
                                PZExceptionHandler.handleDBCVEException(dbe, session.getAttribute("merchantid").toString(), PZOperations.Account_summary);
                            }
                            catch(Exception e)
                            {
                                logger.error("Generic exception in transaction summary",e);
                                out.println(Functions.NewShowConfirmation1("Sorry", "Kindly check Account Summary after some time."));
                                PZExceptionHandler.raiseAndHandleGenericViolationException("accountSummary.jsp","doService()",null,"Merchant","Generic exception while getting Account Summary",null,e.getMessage(),e.getCause(),session.getAttribute("merchantid").toString(),PZOperations.Account_summary);
                            }
                        %>
                    </div>

                </div>

            </div>

        </div>

    </div>

</div>

<script>
    window.onload = function () {
        document.getElementById("download")
                .addEventListener("click", () => {
            const myTable = this.document.getElementById("myTable");
        console.log(myTable);
        console.log(window);
        var opt = {
            margin: 1,
            filename: 'AccountSummary.pdf',
            image: { type: 'jpeg', quality: 0.98 },
            html2canvas: { scale: 3 },
            jsPDF: { unit: 'in', format: 'ledger', orientation: 'portrait' }
        };
        html2pdf().from(myTable).set(opt).save();
    })
    }
</script>
</body>
</html>