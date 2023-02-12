<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="java.util.*" %>
<%@ page import="com.payment.exceptionHandler.PZDBViolationException" %>
<%@ page import="com.payment.exceptionHandler.PZExceptionHandler" %>
<%@ page import="com.payment.exceptionHandler.operations.PZOperations" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.manager.vo.memeberConfigVOS.MemberAccountMappingVO" %>
<%@ page import="com.manager.vo.memeberConfigVOS.MerchantConfigCombinationVO" %>
<%@ page import="com.manager.vo.ChargeVO" %>
<%@ page import="com.manager.enums.Charge_unit" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%--
  Created by IntelliJ IDEA.
  User: Niket
  Date: 7/28/14
  Time: 2:06 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="ietest.jsp" %>
<%@ include file="Top.jsp" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","Merchant Config Details");
%>
<html>
<head>
    <title><%=company%> Merchant Settings > Merchant Config Details</title>
</head>
<body class="pace-done widescreen fixed-left-void bodybackground">
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<script type="text/javascript">
    $('#sandbox-container input').datepicker({
    });
</script>
<script>
    $(function() {
        $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
    });
</script>
<%!
    private static Logger logger = new Logger("merchantConfigDetails.jsp");
    private TerminalManager terminalManager = new TerminalManager();
%>
<%
    Functions functions = new Functions();
    try
    {
        List<TerminalVO> terminalVO = terminalManager.getTerminalsByMerchantId(session.getAttribute("merchantid").toString());
        String fromdate = null;
        String todate = null;
        String terminalid = null;
        terminalid =Functions.checkStringNull(request.getParameter("terminalid"))==null?"":request.getParameter("terminalid");

        ResourceBundle rb1 = null;
        String language_property1 = (String)session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String merchantConfigDetails_merchant_details = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_merchant_details"))?rb1.getString("merchantConfigDetails_merchant_details"): "Merchant Configuration Details";
        String merchantConfigDetails_select_terminalid = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_select_terminalid"))?rb1.getString("merchantConfigDetails_select_terminalid"): "Select Terminal ID";
        String merchantConfigDetails_search = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_search"))?rb1.getString("merchantConfigDetails_search"): "Search";
        String merchantConfigDetails_terminalid1 = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_terminalid1"))?rb1.getString("merchantConfigDetails_terminalid1"): "Terminal ID*";
        String merchantConfigDetails_profile_details = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_profile_details"))?rb1.getString("merchantConfigDetails_profile_details"): "Profile Details";
        String merchantConfigDetails_merchant_id = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_merchant_id"))?rb1.getString("merchantConfigDetails_merchant_id"): "Merchant ID";
        String merchantConfigDetails_terminal_id = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_terminal_id"))?rb1.getString("merchantConfigDetails_terminal_id"): "Terminal Id";
        String merchantConfigDetails_company_name = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_company_name"))?rb1.getString("merchantConfigDetails_company_name"): "Company Name";
        String merchantConfigDetails_person_name = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_person_name"))?rb1.getString("merchantConfigDetails_person_name"): "Person Name";
        String merchantConfigDetails_email_address = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_email_address"))?rb1.getString("merchantConfigDetails_email_address"): "Email Address";
        String merchantConfigDetails_site_url = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_site_url"))?rb1.getString("merchantConfigDetails_site_url"): "Site URL";
        String merchantConfigDetails_phone = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_phone"))?rb1.getString("merchantConfigDetails_phone"): "Phone Number";
        String merchantConfigDetails_country = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_country"))?rb1.getString("merchantConfigDetails_country"): "Country";
        String merchantConfigDetails_whitelist = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_whitelist"))?rb1.getString("merchantConfigDetails_whitelist"): "Whitelist/Blacklist Configuration";
        String merchantConfigDetails_bin_routing = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_bin_routing"))?rb1.getString("merchantConfigDetails_bin_routing"): "Bin Routing/Whitelisting";
        String merchantConfigDetails_whitelist1 = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_whitelist1"))?rb1.getString("merchantConfigDetails_whitelist1"): "Whitelist IP";
        String merchantConfigDetails_check_global = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_check_global"))?rb1.getString("merchantConfigDetails_check_global"): "Check Global Blacklist";
        String merchantConfigDetails_fraud = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_fraud"))?rb1.getString("merchantConfigDetails_fraud"): "Fraud Configuration";
        String merchantConfigDetails_online = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_online"))?rb1.getString("merchantConfigDetails_online"): "Online Fraud Check";
        String merchantConfigDetails_internal = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_internal"))?rb1.getString("merchantConfigDetails_internal"): "Internal Fraud Check";
        String merchantConfigDetails_refund = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_refund"))?rb1.getString("merchantConfigDetails_refund"): "RefundRefund Configuration";
        String merchantConfigDetails_allowed = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_allowed"))?rb1.getString("merchantConfigDetails_allowed"): "Allowed";
        String merchantConfigDetails_multiple = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_multiple"))?rb1.getString("merchantConfigDetails_multiple"): "Multiple Refund Allowed";
        String merchantConfigDetails_partial = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_partial"))?rb1.getString("merchantConfigDetails_partial"): "merchantConfigDetails_partial";
        String merchantConfigDetails_daily = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_daily"))?rb1.getString("merchantConfigDetails_daily"): "Daily Refund Limit";
        String merchantConfigDetails_allowed1 = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_allowed1"))?rb1.getString("merchantConfigDetails_allowed1"): "Refund Allowed Days";
        String merchantConfigDetails_terminal_level = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_terminal_level"))?rb1.getString("merchantConfigDetails_terminal_level"): "Terminal Level Configuration";
        String merchantConfigDetails_is_active = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_is_active"))?rb1.getString("merchantConfigDetails_is_active"): "IsActive";
        String merchantConfigDetails_priority = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_priority"))?rb1.getString("merchantConfigDetails_priority"): "Priority";
        String merchantConfigDetails_is_test = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_is_test"))?rb1.getString("merchantConfigDetails_is_test"): "IsTest";
        String merchantConfigDetails_tokenization = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_tokenization"))?rb1.getString("merchantConfigDetails_tokenization"): "IsTokenizationActive";
        String merchantConfigDetails_address_details = !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_address_details"))?rb1.getString("merchantConfigDetails_address_details"): "Address Details";
        String merchantConfigDetails_address_validation= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_address_validation"))?rb1.getString("merchantConfigDetails_address_validation"): "Address Validation";
        String merchantConfigDetails_risk_rule= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_risk_rule"))?rb1.getString("merchantConfigDetails_risk_rule"): "Risk Rule Activation";
        String merchantConfigDetails_is_bin= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_is_bin"))?rb1.getString("merchantConfigDetails_is_bin"): "Is Bin Routing/Whitelisting";
        String merchantConfigDetails_card_whitelisted= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_card_whitelisted"))?rb1.getString("merchantConfigDetails_card_whitelisted"): "Is Card Whitelisted";
        String merchantConfigDetails_is_email= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_is_email"))?rb1.getString("merchantConfigDetails_is_email"): "Is Email Whitelisted";
        String merchantConfigDetails_emi_support= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_emi_support"))?rb1.getString("merchantConfigDetails_emi_support"): "EMI Support";
        String merchantConfigDetails_terminal_limits= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_terminal_limits"))?rb1.getString("merchantConfigDetails_terminal_limits"): "Terminal Limits";
        String merchantConfigDetails_daily_list= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_daily_list"))?rb1.getString("merchantConfigDetails_daily_list"): "Daily Amount Limit";
        String merchantConfigDetails_weekly_amount= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_weekly_amount"))?rb1.getString("merchantConfigDetails_weekly_amount"): "Weekly Amount Limit";
        String merchantConfigDetails_monthly_amounty= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_monthly_amounty"))?rb1.getString("merchantConfigDetails_monthly_amounty"): "Monthly Amount Limit";
        String merchantConfigDetails_daily_card= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_daily_card"))?rb1.getString("merchantConfigDetails_daily_card"): "Daily Card Limit";
        String merchantConfigDetails_weekly= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_weekly"))?rb1.getString("merchantConfigDetails_weekly"): "Weekly Card Limit";
        String merchantConfigDetails_monthly= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_monthly"))?rb1.getString("merchantConfigDetails_monthly"): "Monthly Card Limit";
        String merchantConfigDetails_daily1= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_daily1"))?rb1.getString("merchantConfigDetails_daily1"): "Daily Card Amount Limit";
        String merchantConfigDetails_weekly1= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_weekly1"))?rb1.getString("merchantConfigDetails_weekly1"): "Weekly Card Amount Limit";
        String merchantConfigDetails_monthly1= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_monthly1"))?rb1.getString("merchantConfigDetails_monthly1"): "Monthly Card Amount Limit";
        String merchantConfigDetails_daily_avg= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_daily_avg"))?rb1.getString("merchantConfigDetails_daily_avg"): "Daily Avg Ticket";
        String merchantConfigDetails_weekly_avg= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_weekly_avg"))?rb1.getString("merchantConfigDetails_weekly_avg"): "Weekly Avg Ticket";
        String merchantConfigDetails_monthly_ticket= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_monthly_ticket"))?rb1.getString("merchantConfigDetails_monthly_ticket"): "Monthly Avg Ticket";
        String merchantConfigDetails_min_transaction= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_min_transaction"))?rb1.getString("merchantConfigDetails_min_transaction"): "Min Transaction Amount";
        String merchantConfigDetails_max_transaction= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_max_transaction"))?rb1.getString("merchantConfigDetails_max_transaction"): "Max Transaction Amount";
        String merchantConfigDetails_terminal_charges= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_terminal_charges"))?rb1.getString("merchantConfigDetails_terminal_charges"): "Terminal Charges Details";
        String merchantConfigDetails_charges1= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_charges1"))?rb1.getString("merchantConfigDetails_charges1"): "Charges";
        String merchantConfigDetails_rates= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_rates"))?rb1.getString("merchantConfigDetails_rates"): "Rates/Fees";
        String merchantConfigDetails_sorry= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_sorry"))?rb1.getString("merchantConfigDetails_sorry"): "Sorry";
        String merchantConfigDetails_no_records= !functions.isEmptyOrNull(rb1.getString("merchantConfigDetails_no_records"))?rb1.getString("merchantConfigDetails_no_records"): "No records found for given search criteria.";

%>

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=merchantConfigDetails_merchant_details%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <br>
                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <form  name="form" method="post" action="/merchant/servlet/MerchantConfigDetails?ctoken=<%=ctoken%>">
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                    <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                                    <%
                                        String error=(String) request.getAttribute("error");
                                        if( error!= null)
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                                        }
                                    %>
                                    <div class="form-group">
                                        <label class="col-sm-2 control-label"><%=merchantConfigDetails_terminalid1%></label>
                                        <div class="col-sm-3">
                                            <select size="1" name="terminalid" class="form-control">
                                                <option value="" selected><%=merchantConfigDetails_select_terminalid%></option>
                                                <%
                                                    for(TerminalVO terminalVO1 : terminalVO)
                                                    {
                                                        String active = "";
                                                        String selected = "";
                                                        if (terminalid.equals(terminalVO1.getTerminalId())){
                                                            selected="selected";
                                                        }

                                                        if (terminalVO1.getIsActive().equalsIgnoreCase("Y")){
                                                            active = "Active";
                                                        }
                                                        else{
                                                            active = "InActive";
                                                        }
                                                %>
                                                <option value="<%=terminalVO1.getTerminalId()%>" <%=selected%>>
                                                    <%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO1.toString())%>-<%=terminalVO1.getCurrency()%>-<%=active%>
                                                </option>
                                                <%
                                                    }
                                                %>
                                            </select>
                                        </div>
                                        <div class="form-group col-md-3 has-feedback">&nbsp;</div>
                                        <div class="form-group col-md-3">
                                            <button type="submit" name="B1" class="btn btn-default" style="display: -webkit-box;" onclick="doSubmit();"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=merchantConfigDetails_search%></button>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <%
                if(request.getAttribute("merchantConfigCombinationVO")!=null)
                {
                    MerchantConfigCombinationVO merchantConfigCombinationVO= (MerchantConfigCombinationVO) request.getAttribute("merchantConfigCombinationVO");
                    MerchantDetailsVO merchantDetailsVOnew = merchantConfigCombinationVO .getMerchantDetailsVO();
                    MemberAccountMappingVO memberAccountMappingVO= merchantConfigCombinationVO.getMemberAccountMappingVO();
                    List<ChargeVO> chargeVOList = merchantConfigCombinationVO.getChargeVOs();
            %>
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=merchantConfigDetails_profile_details%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-y: auto;">
                            <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead >
                                <tr style="color: white;">
                                    <th style="text-align: center">&nbsp;<%=merchantConfigDetails_merchant_id%></th>
                                    <th style="text-align: center">&nbsp;<%=merchantConfigDetails_terminal_id%></th>
                                    <th style="text-align: center">&nbsp;<%=merchantConfigDetails_company_name%></th>
                                    <th style="text-align: center">&nbsp;<%=merchantConfigDetails_person_name%></th>
                                    <th style="text-align: center">&nbsp;<%=merchantConfigDetails_email_address%></th>
                                    <th style="text-align: center">&nbsp;<%=merchantConfigDetails_site_url%></th>
                                    <th style="text-align: center">&nbsp;<%=merchantConfigDetails_phone%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_country%></th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr >
                                    <td data-label="Merchant ID" style="text-align: center"><%=merchantDetailsVOnew.getMemberId()%></td>
                                    <td data-label="Terminal Id" style="text-align: center"><%=memberAccountMappingVO.getTerminalid()%></td>
                                    <td data-label="Company Name" style="text-align: center"><%=merchantDetailsVOnew.getCompany_name()%></td>
                                    <td data-label="Person Name" style="text-align: center"><%=merchantDetailsVOnew.getContact_persons()%></td>
                                    <td data-label="Email Address" style="text-align: center"><%=functions.getEmailMasking(merchantDetailsVOnew.getContact_emails())%></td>
                                    <td data-label="Site URL" style="text-align: center"><%=merchantDetailsVOnew.getSiteName()%></td>
                                    <td data-label="Phone Number" style="text-align: center"><%=functions.getPhoneNumMasking(merchantDetailsVOnew.getTelNo())%></td>
                                    <td data-label="Country" style="text-align: center"><%=merchantDetailsVOnew.getCountry()%></td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=merchantConfigDetails_whitelist%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-y: auto;">
                            <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead >
                                <tr style="color: white;">
                                    <th style="text-align: center"><%=merchantConfigDetails_bin_routing%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_whitelist1%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_check_global%></th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr >
                                    <td data-label="Bin Rounting" style="text-align: center"><%=merchantDetailsVOnew.getBinRouting()%></td>
                                    <td data-label="Blacklist Country by Ip" style="text-align: center"><%=merchantDetailsVOnew.getIsIpWhiteListed()%></td>
                                    <td data-label="Blacklist Country by Bin" style="text-align: center"><%=merchantDetailsVOnew.getIsBlacklistTransaction()%></td>
                                </tr>
                                </tbody>
                                </thead>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=merchantConfigDetails_fraud%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-y: auto;">
                            <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead >
                                <tr style="color: white;">
                                    <%--<th style="text-align: center">Blacklist Country By IP</th>
                                    <th style="text-align: center">Blacklist Country By Bin</th>
                                    <th style="text-align: center">Bin IP Mismatch</th>--%>
                                    <th style="text-align: center"><%=merchantConfigDetails_online%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_internal%></th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr >
                                    <%--<td data-label="Blacklist Country by Ip" style="text-align: center"><%=merchantDetailsVO.getBlacklistCountryIp()%></td>
                                    <td data-label="Blacklist Country by Bin" style="text-align: center"><%=merchantDetailsVO.getBlacklistCountryBin()%></td>
                                    <td data-label="Bin IP Mismatch" style="text-align: center"><%=merchantDetailsVO.getBlacklistCountryBinIp()%></td>--%>
                                    <td data-label="Online Fraud Check" style="text-align: center"><%=merchantDetailsVOnew.getOnlineFraudCheck()%></td>
                                    <td data-label="Online Fraud Check" style="text-align: center"><%=merchantDetailsVOnew.getInternalFraudCheck()%></td>
                                </tr>
                                </tbody>
                                </thead>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=merchantConfigDetails_refund%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-y: auto;">
                            <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead >
                                <tr style="color: white;">
                                    <th style="text-align: center"><%=merchantConfigDetails_allowed%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_multiple%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_partial%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_daily%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_allowed1%></th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr >
                                    <td data-label="Refund Allowed" style="text-align: center"><%=merchantDetailsVOnew.getIsrefund()%></td>
                                    <td data-label="Multiple Refund Allowed" style="text-align: center"><%=merchantDetailsVOnew.getMultipleRefund()%></td>
                                    <td data-label="Partial Refund Allowed" style="text-align: center"><%=merchantDetailsVOnew.getPartialRefund()%></td>
                                    <td data-label="Daily Refund Limit" style="text-align: center"><%=merchantDetailsVOnew.getRefunddailylimit()%></td>
                                    <td data-label="Refund Allowed Days" style="text-align: center"><%=merchantDetailsVOnew.getRefundAllowedDays()%></td>
                                </tr>
                                </tbody>
                                </thead>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=merchantConfigDetails_terminal_level%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-y: auto;">
                            <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead >
                                <tr style="color: white;">
                                    <th style="text-align: center"><%=merchantConfigDetails_is_active%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_priority%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_is_test%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_tokenization%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_address_details%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_address_validation%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_risk_rule%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_is_bin%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_card_whitelisted%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_is_email%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_emi_support%></th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr >
                                    <td data-label="Is Active" style="text-align: center"><%=memberAccountMappingVO.getActive()%></td>
                                    <td data-label="Priority" style="text-align: center"><%=memberAccountMappingVO.getPriority()%></td>
                                    <td data-label="Is Test" style="text-align: center"><%=memberAccountMappingVO.getTest()%></td>
                                    <td data-label="Is Tokenization Active" style="text-align: center"><%=memberAccountMappingVO.getIsTokenizationAllowed()%></td>
                                    <td data-label="Address Details" style="text-align: center"><%=memberAccountMappingVO.getAddressDetails()%></td>
                                    <td data-label="Address Validation" style="text-align: center"><%=memberAccountMappingVO.getAddressValidation()%></td>
                                    <td data-label="Risk Rule Activation" style="text-align: center"><%=memberAccountMappingVO.getRiskRuleActivation()%></td>
                                    <td data-label="Bin Routing" style="text-align: center"><%=memberAccountMappingVO.getBinRouting()%></td>
                                    <td data-label="Is Card Whitelisted" style="text-align: center"><%=memberAccountMappingVO.getIsCardWhitelisted()%></td>
                                    <td data-label="Is Email Whitelisted" style="text-align: center"><%=memberAccountMappingVO.getIsEmailWhitelisted()%></td>
                                    <td data-label="EMI Support" style="text-align: center"><%=memberAccountMappingVO.getEmiSupport()%></td>
                                </tr>
                                </tbody>
                                </thead>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=merchantConfigDetails_terminal_limits%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-y: auto;">
                            <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>
                                <tr style="color: white;">
                                    <th style="text-align: center"><%=merchantConfigDetails_daily_list%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_weekly_amount%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_monthly_amounty%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_daily_card%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_weekly%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_monthly%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_daily1%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_weekly1%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_monthly1%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_daily_avg%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_weekly_avg%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_monthly_ticket%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_min_transaction%></th>
                                    <th style="text-align: center"><%=merchantConfigDetails_max_transaction%></th>
                                </tr>
                                </thead>
                                <tbody>
                                <tr >
                                    <td data-label="Daily Amount Limit" style="text-align: center"><%=memberAccountMappingVO.getDaily_amount_limit()%></td>
                                    <td data-label="Daily Amount Limit" style="text-align: center"><%=memberAccountMappingVO.getWeekly_amount_limit()%></td>
                                    <td data-label="Monthly Amount Limit" style="text-align: center"><%=memberAccountMappingVO.getMonthly_amount_limit()%></td>
                                    <td data-label="Daily Card Limit" style="text-align: center"><%=memberAccountMappingVO.getDaily_card_limit()%></td>
                                    <td data-label="Weekly Card Limit" style="text-align: center"><%=memberAccountMappingVO.getWeekly_card_limit()%></td>
                                    <td data-label="Monthly Card Limit" style="text-align: center"><%=memberAccountMappingVO.getMonthly_card_limit()%></td>
                                    <td data-label="Daily Card Amount Limit" style="text-align: center"><%=memberAccountMappingVO.getDaily_card_amount_limit()%></td>
                                    <td data-label="Weekly Card Amount Limit" style="text-align: center"><%=memberAccountMappingVO.getWeekly_card_amount_limit()%></td>
                                    <td data-label="Monthly Card Amount Limit" style="text-align: center"><%=memberAccountMappingVO.getMonthly_card_amount_limit()%></td>
                                    <td data-label="Monthly Card Amount Limit" style="text-align: center"><%=memberAccountMappingVO.getDaily_avg_ticket()%></td>
                                    <td data-label="Monthly Card Amount Limit" style="text-align: center"><%=memberAccountMappingVO.getWeekly_avg_ticket()%></td>
                                    <td data-label="Monthly Card Amount Limit" style="text-align: center"><%=memberAccountMappingVO.getMonthly_avg_ticket()%></td>
                                    <td data-label="Monthly Card Amount Limit" style="text-align: center"><%=memberAccountMappingVO.getMin_transaction_amount()%></td>
                                    <td data-label="Monthly Card Amount Limit" style="text-align: center"><%=memberAccountMappingVO.getMax_transaction_amount()%></td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=merchantConfigDetails_terminal_charges%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">
                            <%
                                if(chargeVOList.size()>0)
                                {
                            %>
                            <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead style="color: white;">
                                <tr>
                                    <td style="text-align: center"><%=merchantConfigDetails_charges1%></td>
                                    <td style="text-align: center"><%=merchantConfigDetails_rates%></td>
                                </tr>
                                </thead>
                                <tbody>
                                <%
                                    for(ChargeVO chargeVO : chargeVOList)
                                    {
                                %>
                                <tr>
                                    <td style="text-align: center" data-label="Charges"><%=chargeVO.getChargename()%></td>
                                    <td data-label="Rates/Fees" style="text-align: center"><%=(Charge_unit.Percentage.name().equals(chargeVO.getValuetype()))?chargeVO.getChargevalue()+"%":chargeVO.getChargevalue()%></td>
                                </tr>
                                </tbody>
                                    <%
                                        }
                                        out.println("</table>");
                                    }
                                    else
                                    {
                                        out.println(Functions.NewShowConfirmation1("Sorry", "No charges has been mapped"));
                                    }
                                %>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <%
        }
        else
        {
            out.println(Functions.NewShowConfirmation1(merchantConfigDetails_sorry, merchantConfigDetails_no_records));
        }
    %>
    <%
        }
        catch(PZDBViolationException dbe)
        {
            logger.error("Db exception::",dbe);
            out.println("<br>");
            out.println("<br>");
            out.println("<br>");
            out.println("<div class=\"table-responsive datatable\" style=\"margin-top:100px\">");
            out.println(Functions.NewShowConfirmation1("Sorry", "Kindly check Merchant Config details after some time."));
            out.println("</div>");
            PZExceptionHandler.handleDBCVEException(dbe, session.getAttribute("merchantid").toString(), PZOperations.MEMBER_CONFIG);
        }
        catch(Exception e)
        {
            logger.error("Generic exception in transaction summary",e);
            out.println("<br>");
            out.println("<br>");
            out.println("<br>");
            out.println("<div class=\"table-responsive datatable\" style=\"margin-top:100px\">");
            out.println(Functions.NewShowConfirmation1("Sorry", "Kindly check Merchant Config details after some time."));
            out.println("</div>");
            PZExceptionHandler.raiseAndHandleGenericViolationException("merchantConfigDetails.jsp","doService()",null,"Merchant","Generic exception while getting Member Config details",null,e.getMessage(),e.getCause(),session.getAttribute("merchantid").toString(),PZOperations.MEMBER_CONFIG);
        }
    %>
</div>
</div>
</div>
</body>
</html>