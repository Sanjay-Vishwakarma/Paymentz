<%@ page import="com.directi.pg.Functions" %>
<%@ include file="functions.jsp" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="top.jsp" %>

<%!
    private static Logger log = new Logger("partnerpreference");
%>
<%
     String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit", "partnerpreference");
%>


<% ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    String partnerid = request.getParameter("partnerid") == null ? "" : request.getParameter("partnerid");
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String partnerpreference_Partner_Default_Configuration = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Partner_Default_Configuration")) ? rb1.getString("partnerpreference_Partner_Default_Configuration") : "Partner Default Configuration";
    String partnerpreference_Partner_ID = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Partner_ID")) ? rb1.getString("partnerpreference_Partner_ID") : "Partner ID";
    String partnerpreference_Search = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Search")) ? rb1.getString("partnerpreference_Search") : "Search";
    String partnerpreference_Report_Table = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Report_Table")) ? rb1.getString("partnerpreference_Report_Table") : "Report Table";
    String partnerpreference_SrNo = StringUtils.isNotEmpty(rb1.getString("partnerpreference_SrNo")) ? rb1.getString("partnerpreference_SrNo") : "Sr. No.";
    String partnerpreference_PartnerId = StringUtils.isNotEmpty(rb1.getString("partnerpreference_PartnerId")) ? rb1.getString("partnerpreference_PartnerId") : "Partner Id";
    String partnerpreference_Partner_Name = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Partner_Name")) ? rb1.getString("partnerpreference_Partner_Name") : "Partner Name";
    String partnerpreference_Site_Url = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Site_Url")) ? rb1.getString("partnerpreference_Site_Url") : "Site Url";
    String partnerpreference_Member_Limits = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Member_Limits")) ? rb1.getString("partnerpreference_Member_Limits") : "Member Limits";
    String partnerpreference_Daily_Amount_Limit = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Daily_Amount_Limit")) ? rb1.getString("partnerpreference_Daily_Amount_Limit") : "Daily Amount Limit";
    String partnerpreference_Monthly_Amount_Limit = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Monthly_Amount_Limit")) ? rb1.getString("partnerpreference_Monthly_Amount_Limit") : "Monthly Amount Limit";
    String partnerpreference_Weekly_Amount_Limit = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Weekly_Amount_Limit")) ? rb1.getString("partnerpreference_Weekly_Amount_Limit") : "Weekly Amount Limit";
    String partnerpreference_Daily_Card_Limit = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Daily_Card_Limit")) ? rb1.getString("partnerpreference_Daily_Card_Limit") : "Daily Card Limit";
    String partnerpreference_Weekly_Card_Limit = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Weekly_Card_Limit")) ? rb1.getString("partnerpreference_Weekly_Card_Limit") : "Weekly Card Limit";
    String partnerpreference_Monthly_Card_Limit = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Monthly_Card_Limit")) ? rb1.getString("partnerpreference_Monthly_Card_Limit") : "Monthly Card Limit";
    String partnerpreference_Daily_Card_Amount_Limit = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Daily_Card_Amount_Limit")) ? rb1.getString("partnerpreference_Daily_Card_Amount_Limit") : "Daily Card Amount Limit";
    String partnerpreference_Weekly_Card_Amount_Limit = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Weekly_Card_Amount_Limit")) ? rb1.getString("partnerpreference_Weekly_Card_Amount_Limit") : "Weekly Card Amount Limit";
    String partnerpreference_Monthly_Card_Amount_Limit = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Monthly_Card_Amount_Limit")) ? rb1.getString("partnerpreference_Monthly_Card_Amount_Limit") : "Monthly Card Amount Limit";
    String partnerpreference_Card_Limit_Check = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Card_Limit_Check")) ? rb1.getString("partnerpreference_Card_Limit_Check") : "Card Limit Check";
    String partnerpreference_Card_Amount_Limit_Check = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Card_Amount_Limit_Check")) ? rb1.getString("partnerpreference_Card_Amount_Limit_Check") : "Card Amount Limit Check";
    String partnerpreference_Amount_Limit_Check = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Amount_Limit_Check")) ? rb1.getString("partnerpreference_Amount_Limit_Check") : "Amount Limit Check";
    String partnerpreference_1 = StringUtils.isNotEmpty(rb1.getString("partnerpreference_1")) ? rb1.getString("partnerpreference_1") : "1";
    String partnerpreference_0 = StringUtils.isNotEmpty(rb1.getString("partnerpreference_0")) ? rb1.getString("partnerpreference_0") : "0";
    String partnerpreference_Card_Velocity_Check = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Card_Velocity_Check")) ? rb1.getString("partnerpreference_Card_Velocity_Check") : "Card Velocity Check";
    String partnerpreference_Limit_Routing = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Limit_Routing")) ? rb1.getString("partnerpreference_Limit_Routing") : "Limit Routing";
    String partnerpreference_General_Configuration = StringUtils.isNotEmpty(rb1.getString("partnerpreference_General_Configuration")) ? rb1.getString("partnerpreference_General_Configuration") : "General Configuration";
    String partnerpreference_Is_Activation = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Is_Activation")) ? rb1.getString("partnerpreference_Is_Activation") : "Is Activation";
    String partnerpreference_HasPaid = StringUtils.isNotEmpty(rb1.getString("partnerpreference_HasPaid")) ? rb1.getString("partnerpreference_HasPaid") : "HasPaid";
    String partnerpreference_Is_MerchantInterface = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Is_MerchantInterface")) ? rb1.getString("partnerpreference_Is_MerchantInterface") : "Is MerchantInterface";
    String partnerpreference_Is_ExcessCaptureAllowed = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Is_ExcessCaptureAllowed")) ? rb1.getString("partnerpreference_Is_ExcessCaptureAllowed") : "Is ExcessCaptureAllowed";
    String partnerpreference_Is_FlightMode = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Is_FlightMode")) ? rb1.getString("partnerpreference_Is_FlightMode") : "Is FlightMode";
    String partnerpreference_Blacklist_Transactions = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Blacklist_Transactions")) ? rb1.getString("partnerpreference_Blacklist_Transactions") : "Blacklist Transactions";
    String partnerpreference_Y = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Y")) ? rb1.getString("partnerpreference_Y") : "Y";
    String partnerpreference_N = StringUtils.isNotEmpty(rb1.getString("partnerpreference_N")) ? rb1.getString("partnerpreference_N") : "N";
    String partnerpreference_T = StringUtils.isNotEmpty(rb1.getString("partnerpreference_T")) ? rb1.getString("partnerpreference_T") : "T";
    String partnerpreference_Transaction_Configuration = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Transaction_Configuration")) ? rb1.getString("partnerpreference_Transaction_Configuration") : "Transaction Configuration";
    String partnerpreference_Instant_Capture = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Instant_Capture")) ? rb1.getString("partnerpreference_Instant_Capture") : "Instant Capture";
    String partnerpreference_Auto_Redirect = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Auto_Redirect")) ? rb1.getString("partnerpreference_Auto_Redirect") : "Auto Redirect";
    String partnerpreference_VBV = StringUtils.isNotEmpty(rb1.getString("partnerpreference_VBV")) ? rb1.getString("partnerpreference_VBV") : "VBV";
    String partnerpreference_MasterCardSupported = StringUtils.isNotEmpty(rb1.getString("partnerpreference_MasterCardSupported")) ? rb1.getString("partnerpreference_MasterCardSupported") : "MasterCardSupported";
    String partnerpreference_Auto_Select_Terminal = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Auto_Select_Terminal")) ? rb1.getString("partnerpreference_Auto_Select_Terminal") : "Auto Select Terminal";
    String partnerpreference_Is_POD_Required = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Is_POD_Required")) ? rb1.getString("partnerpreference_Is_POD_Required") : "Is POD Required";
    String partnerpreference_Is_RestrictedTicket = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Is_RestrictedTicket")) ? rb1.getString("partnerpreference_Is_RestrictedTicket") : "Is RestrictedTicket";
    String partnerpreference_Bin_Routing = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Bin_Routing")) ? rb1.getString("partnerpreference_Bin_Routing") : "Bin Routing";
    String partnerpreference_Chargeback_Allowed = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Chargeback_Allowed")) ? rb1.getString("partnerpreference_Chargeback_Allowed") : "Chargeback Allowed Day's";
    String partnerpreference_Is_Email = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Is_Email")) ? rb1.getString("partnerpreference_Is_Email") : "Is Email Limit Enabled";
    String partnerpreference_Bin_Service = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Bin_Service")) ? rb1.getString("partnerpreference_Bin_Service") : "Bin Service";
    String partnerpreference_Exp_Date_Offset = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Exp_Date_Offset")) ? rb1.getString("partnerpreference_Exp_Date_Offset") : "Exp Date Offset";
    String partnerpreference_Support_Section = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Support_Section")) ? rb1.getString("partnerpreference_Support_Section") : "Support Section";
    String partnerpreference_Card_Whitelist_Level = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Card_Whitelist_Level")) ? rb1.getString("partnerpreference_Card_Whitelist_Level") : "Card Whitelist Level";
    String partnerpreference_Multi_Currency_Support = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Multi_Currency_Support")) ? rb1.getString("partnerpreference_Multi_Currency_Support") : "Multi Currency Support";
    String partnerpreference_IP_Validation_Required = StringUtils.isNotEmpty(rb1.getString("partnerpreference_IP_Validation_Required")) ? rb1.getString("partnerpreference_IP_Validation_Required") : "IP Validation Required";
    String partnerpreference_Personal_Info_Display = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Personal_Info_Display")) ? rb1.getString("partnerpreference_Personal_Info_Display") : "Personal Info Display";
    String partnerpreference_Personal_Info_Validation = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Personal_Info_Validation")) ? rb1.getString("partnerpreference_Personal_Info_Validation") : "Personal Info Validation";
    String partnerpreference_Rest_Checkout_Page = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Rest_Checkout_Page")) ? rb1.getString("partnerpreference_Rest_Checkout_Page") : "Rest Checkout Page";
    String partnerpreference_EMI_Support = StringUtils.isNotEmpty(rb1.getString("partnerpreference_EMI_Support")) ? rb1.getString("partnerpreference_EMI_Support") : "EMI Support";
    String partnerpreference_Merchant_Order = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Merchant_Order")) ? rb1.getString("partnerpreference_Merchant_Order") : "Merchant Order Details";
    String partnerpreference_Market_Place = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Market_Place")) ? rb1.getString("partnerpreference_Market_Place") : "Market Place";
    String partnerpreference_Is_Cvv_Store = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Is_Cvv_Store")) ? rb1.getString("partnerpreference_Is_Cvv_Store") : "Is Cvv Store";
    String partnerpreference_BackOffice_Access_Management = StringUtils.isNotEmpty(rb1.getString("partnerpreference_BackOffice_Access_Management")) ? rb1.getString("partnerpreference_BackOffice_Access_Management") : "BackOffice Access Management";
    String partnerpreference_DashBoard = StringUtils.isNotEmpty(rb1.getString("partnerpreference_DashBoard")) ? rb1.getString("partnerpreference_DashBoard") : "DashBoard";
    String partnerpreference_Account_Details = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Account_Details")) ? rb1.getString("partnerpreference_Account_Details") : "Account Details";
    String partnerpreference_Settings = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Settings")) ? rb1.getString("partnerpreference_Settings") : "Settings";
    String partnerpreference_Transaction_Management = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Transaction_Management")) ? rb1.getString("partnerpreference_Transaction_Management") : "Transaction Management";
    String partnerpreference_Invoicing = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Invoicing")) ? rb1.getString("partnerpreference_Invoicing") : "Invoicing";
    String partnerpreference_Rejected_Transaction = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Rejected_Transaction")) ? rb1.getString("partnerpreference_Rejected_Transaction") : "Rejected Transaction";
    String partnerpreference_Virtual_Terminal = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Virtual_Terminal")) ? rb1.getString("partnerpreference_Virtual_Terminal") : "Virtual Terminal";
    String partnerpreference_Merchant_Management = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Merchant_Management")) ? rb1.getString("partnerpreference_Merchant_Management") : "Merchant Management";
    String partnerpreference_Application_Manager = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Application_Manager")) ? rb1.getString("partnerpreference_Application_Manager") : "Application Manager";
    String partnerpreference_Recurring = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Recurring")) ? rb1.getString("partnerpreference_Recurring") : "Recurring";
    String partnerpreference_Token_Management = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Token_Management")) ? rb1.getString("partnerpreference_Token_Management") : "Token Management";
    String partnerpreference_Virtual_Checkout = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Virtual_Checkout")) ? rb1.getString("partnerpreference_Virtual_Checkout") : "Virtual Checkout";
    String partnerpreference_Account_Details1 = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Account_Details1")) ? rb1.getString("partnerpreference_Account_Details1") : "Account Details";
    String partnerpreference_Account_Summary = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Account_Summary")) ? rb1.getString("partnerpreference_Account_Summary") : "Account Summary";
    String partnerpreference_Charges_Summary = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Charges_Summary")) ? rb1.getString("partnerpreference_Charges_Summary") : "Charges Summary";
    String partnerpreference_Transaction_Summary = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Transaction_Summary")) ? rb1.getString("partnerpreference_Transaction_Summary") : "Transaction Summary";
    String partnerpreference_Reports = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Reports")) ? rb1.getString("partnerpreference_Reports") : "Reports";
    String partnerpreference_Settings1 = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Settings1")) ? rb1.getString("partnerpreference_Settings1") : "Settings";
    String partnerpreference_Merchant_Profile = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Merchant_Profile")) ? rb1.getString("partnerpreference_Merchant_Profile") : "Merchant Profile";
    String partnerpreference_Organisation_Profile = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Organisation_Profile")) ? rb1.getString("partnerpreference_Organisation_Profile") : "Organisation Profile";
    String partnerpreference_Generate_Key = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Generate_Key")) ? rb1.getString("partnerpreference_Generate_Key") : "Generate Key";
    String partnerpreference_Merchant_Configuration = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Merchant_Configuration")) ? rb1.getString("partnerpreference_Merchant_Configuration") : "Merchant Configuration";
    String partnerpreference_Fraud_Rule_Configuration = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Fraud_Rule_Configuration")) ? rb1.getString("partnerpreference_Fraud_Rule_Configuration") : "Fraud Rule Configuration";
    String partnerpreference_Whitelist_Details = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Whitelist_Details")) ? rb1.getString("partnerpreference_Whitelist_Details") : "Whitelist Details";
    String partnerpreference_Block_Details = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Block_Details")) ? rb1.getString("partnerpreference_Block_Details") : "Block Details";
    String partnerpreference_Transactions = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Transactions")) ? rb1.getString("partnerpreference_Transactions") : "Transactions";
    String partnerpreference_Capture = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Capture")) ? rb1.getString("partnerpreference_Capture") : "Capture";
    String partnerpreference_Reversal = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Reversal")) ? rb1.getString("partnerpreference_Reversal") : "Reversal";
    String partnerpreference_Payout = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Payout")) ? rb1.getString("partnerpreference_Payout") : "Payout";
    String partnerpreference_Invocing = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Invocing")) ? rb1.getString("partnerpreference_Invocing") : "Invocing";
    String partnerpreference_Generate_Invoice = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Generate_Invoice")) ? rb1.getString("partnerpreference_Generate_Invoice") : "Generate Invoice";
    String partnerpreference_Invoice_History = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Invoice_History")) ? rb1.getString("partnerpreference_Invoice_History") : "Invoice History";
    String partnerpreference_Invoice_Configuration = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Invoice_Configuration")) ? rb1.getString("partnerpreference_Invoice_Configuration") : "Invoice Configuration";
    String partnerpreference_Registration_History = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Registration_History")) ? rb1.getString("partnerpreference_Registration_History") : "Registration History";
    String partnerpreference_Register_Card = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Register_Card")) ? rb1.getString("partnerpreference_Register_Card") : "Register Card";
    String partnerpreference_User_Management = StringUtils.isNotEmpty(rb1.getString("partnerpreference_User_Management")) ? rb1.getString("partnerpreference_User_Management") : "User Management";
    String partnerpreference_Template_Configuration = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Template_Configuration")) ? rb1.getString("partnerpreference_Template_Configuration") : "Template Configuration";
    String partnerpreference_IS_Pharma = StringUtils.isNotEmpty(rb1.getString("partnerpreference_IS_Pharma")) ? rb1.getString("partnerpreference_IS_Pharma") : "IS Pharma(Y/N)";
    String partnerpreference_Template = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Template")) ? rb1.getString("partnerpreference_Template") : "Template";
    String partnerpreference_PCI_Logo = StringUtils.isNotEmpty(rb1.getString("partnerpreference_PCI_Logo")) ? rb1.getString("partnerpreference_PCI_Logo") : "PCI Logo";
    String partnerpreference_Partner_Logo = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Partner_Logo")) ? rb1.getString("partnerpreference_Partner_Logo") : "Partner Logo";
    String partnerpreference_Visa_Secure_Logo = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Visa_Secure_Logo")) ? rb1.getString("partnerpreference_Visa_Secure_Logo") : "Visa Secure Logo";
    String partnerpreference_MC_Secure_Logo = StringUtils.isNotEmpty(rb1.getString("partnerpreference_MC_Secure_Logo")) ? rb1.getString("partnerpreference_MC_Secure_Logo") : "MC Secure Logo";
    String partnerpreference_Consent = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Consent")) ? rb1.getString("partnerpreference_Consent") : "Consent";
    String partnerpreference_Security_Logo = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Security_Logo")) ? rb1.getString("partnerpreference_Security_Logo") : "Is Security Logo";
    String partnerpreference_Checkout_Timer = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Checkout_Timer")) ? rb1.getString("partnerpreference_Checkout_Timer") : "Checkout Timer";
    String partnerpreference_Checkout_Timer_Time = StringUtils.isNotEmpty(rb1.getString("partnerpreference_Checkout_Timer_Time")) ? rb1.getString("partnerpreference_Checkout_Timer_Time") : "Checkout Timer Time";
    String partnerpreference_isUniqueOrderIdRequired = StringUtils.isNotEmpty(rb1.getString("partnerpreference_isUniqueOrderIdRequired")) ? rb1.getString("partnerpreference_isUniqueOrderIdRequired") : "Is Unique OrderId Required";
    String isMerchantLogoBO= Functions.checkStringNull(request.getParameter("isMerchantLogoBO"));

    String str=null;
    if (isMerchantLogoBO != null) str = str + "&isMerchantLogoBO=" + isMerchantLogoBO;
%>
<html>
<head>
    <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
    <script>

        $(document).ready(function ()
        {

            var w = $(window).width();

            //alert(w);

            if (w > 990)
            {
                //alert("It's greater than 990px");
                $("body").removeClass("smallscreen").addClass("widescreen");
                $("#wrapper").removeClass("enlarged");
            }
            else
            {
                //alert("It's less than 990px");
                $("body").removeClass("widescreen").addClass("smallscreen");
                $("#wrapper").addClass("enlarged");
                $(".left ul").removeAttr("style");
            }
        });

        function change(dropdown,input){
            var val = dropdown.options[dropdown.selectedIndex].value;
            if(val.trim()==='N'){

                document.getElementById(input).disabled = true;

            }else{
                document.getElementById(input).disabled = false;
            }
        }
    </script>
</head>
<title><%=company%> Partner> Partner Default Configuration </title>
<script>
    var lablevalues = new Array();
    function ChangeFunction(Value , lable){
        console.log("Value" + Value + "lable" + lable);
        var finalvalue=lable+"="+Value;
        console.log("finalvalue" + finalvalue );
        lablevalues.push(finalvalue);
        console.log(lablevalues);
        document.getElementById("onchangedvalue").value = lablevalues;
    }
</script>

<body class="bodybackground">
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=partnerpreference_Partner_Default_Configuration%>
                            </strong></h2>

                            <div class="additional-btn">N
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <form action="/partner/net/PartnerDefaultDetails?ctoken=<%=ctoken%>" method="post"
                                      name="forms">
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                                    <input type="hidden" value="<%=String.valueOf(session.getAttribute("partnerId"))%>"
                                           name="superAdminId" id="partnerid">
                                    <%
                                        Functions functions = new Functions();
                                        if (functions.isValueNull((String)request.getAttribute("error")))
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + (String)request.getAttribute("error") + "</h5>");
                                        }
                                        String success = (String) request.getAttribute("cbmessage");
                                        System.out.println("success" + success);
                                        if (functions.isValueNull(success))
                                        {
                                            out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-check-circle\" style=\" font-size:initial !important;\" ></i>&nbsp;&nbsp;" + success + "</h5>");
                                        }
                                        Hashtable temphash = null;
                                    %>

                                    <div class="form-group col-md-4">
                                        <label class="col-sm-3 control-label"><%=partnerpreference_Partner_ID%></label>

                                        <div class="col-sm-8">
                                            <input name="partnerid" id="PID" value="<%=partnerid%>" class="form-control"
                                                   autocomplete="on">
                                        </div>
                                    </div>

                                    <div class="form-group col-md-8">
                                        <div class="col-sm-offset-2 col-sm-3">
                                            <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                                                &nbsp;&nbsp;<%=partnerpreference_Search%>
                                            </button>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <form action="/partner/net/SetReservesDefault?ctoken=<%=ctoken%>" method=post>
                <input type="hidden" value="" name="onchangedvalue" id="onchangedvalue">   <%--***do not remove the field*****--%>

                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerpreference_Report_Table%></strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <%
                                Hashtable hash = (Hashtable) request.getAttribute("memberdetails");
                                int pageno = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
                                int pagerecords = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
                                int records = 0;
                                int totalrecords = 0;
                                if ((hash != null && hash.size() > 0))
                                {
                                    try
                                    {
                                        records = Integer.parseInt((String) hash.get("records"));
                                        totalrecords = Integer.parseInt((String) hash.get("totalrecords"));

                                    }
                                    catch (Exception ex)
                                    {
                                        log.error("Records & TotalRecords is found null", ex);
                                    }
                                }
                                if (records > 0)
                                {
                            %>
                            <div class="widget-content padding" style="overflow-x: auto;">
                                <table align=center width="50%"
                                       class="display table table table-striped table-bordered table-hover dataTable"
                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <%
                                        String style = "td1";
                                        for (int pos = 1; pos <= records; pos++)
                                        {
                                            String id = Integer.toString(pos);
                                            int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);
                                            if (pos % 2 == 0)
                                                style = "tr0";
                                            else
                                                style = "tr0";
                                            temphash = (Hashtable) hash.get(id);
                                            String partnerId = (String) temphash.get("partnerid");
                                            String partnerName = (String) temphash.get("partnerName");
                                            String siteUrl = (String) temphash.get("siteurl");
                                            String isReadOnly = "";
                                    %>
                                    <thead>
                                    <tr>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <%=partnerpreference_SrNo%>
                                        </td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <%=partnerpreference_PartnerId%>
                                        </td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <%=partnerpreference_Partner_Name%>
                                        </td>
                                        <td valign="middle" align="center"
                                            style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                            <%=partnerpreference_Site_Url%>
                                        </td>
                                    </tr>
                                    </thead>
                                    <tr>
                                        <td valign="middle" data-label="Sr No" align="center"
                                            class="<%=style%>"><%=srno%>
                                        </td>
                                        <td valign="middle" data-label="Sr No" align="center"
                                            class="<%=style%>"><%=partnerId%><input
                                                type=hidden name="partnerid"
                                                value="<%=ESAPI.encoder().encodeForHTMLAttribute(partnerId)%>">
                                        </td>
                                        <td valign="middle" data-label="Sr No" align="center"
                                            class="<%=style%>"><%=partnerName%><input
                                                type="hidden" name="partnerName"
                                                value="<%=ESAPI.encoder().encodeForHTMLAttribute(partnerName)%>">
                                        </td>
                                        <td valign="middle" data-label="Sr No" align="center"
                                            class="<%=style%>"><%=siteUrl%><input
                                                type="hidden" name="siteurl"
                                                value="<%=ESAPI.encoder().encodeForHTMLAttribute(siteUrl)%>">
                                        </td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerpreference_Member_Limits%></strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <thead>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Daily_Amount_Limit%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Monthly_Amount_Limit%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Weekly_Amount_Limit%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Daily_Card_Limit%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Weekly_Card_Limit%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Monthly_Card_Limit%>
                                            </td>
                                        </tr>
                                        </thead>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff" name='daily_amount_limit'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Daily Amount Limit')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff" name='monthly_amount_limit'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Monthly Amount Limit')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff" name='weekly_amount_limit'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Weekly Amount Limit')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff" name='daily_card_limit'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_card_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Daily Card Limit')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff" name='weekly_card_limit'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_card_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Weekly Card Limit')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff" name='monthly_card_limit'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_card_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Monthly Card Limit')">
                                            </td>
                                        </tr>
                                        <thead>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Daily_Card_Amount_Limit%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Weekly_Card_Amount_Limit%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Monthly_Card_Amount_Limit%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Card_Limit_Check%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Card_Amount_Limit_Check%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Amount_Limit_Check%>
                                            </td>
                                        </tr>
                                        </thead>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                          name='daily_card_amount_limit'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_card_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Daily Card Amount Limit')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                          name='weekly_card_amount_limit'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_card_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Weekly Card Amount Limit')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                          name='monthly_card_amount_limit'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_card_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Monthly Card Amount Limit')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='card_check_limit' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Card Limit Check')">
                                                <%
                                                    if (temphash.get("card_check_limit").equals("0"))
                                                    {
                                                %>
                                                <option value="<%=temphash.get("card_check_limit")%>"
                                                        selected="selected"><%=temphash.get("card_check_limit")%>
                                                </option>
                                                <option value="1">1</option>
                                                <% }
                                                else
                                                {%>
                                                <option value="0">0</option>
                                                <option value="<%=temphash.get("card_check_limit")%>"
                                                        selected="selected"><%=temphash.get("card_check_limit")%>
                                                </option>
                                                <%}%></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='card_transaction_limit' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Card Amount Limit Check')">
                                                <%
                                                    if (temphash.get("card_transaction_limit").equals("0"))
                                                    {
                                                %>
                                                <option value="<%=temphash.get("card_transaction_limit")%>"
                                                        selected="selected"><%=temphash.get("card_transaction_limit")%>
                                                </option>
                                                <option value="1"><%=partnerpreference_1%></option>
                                                <% }
                                                else
                                                {%>
                                                <option value="0"><%=partnerpreference_0%></option>
                                                <option value="<%=temphash.get("card_transaction_limit")%>"
                                                        selected="selected"><%=temphash.get("card_transaction_limit")%>
                                                </option>
                                                <%}%></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='check_limit' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Amount Limit Check')">
                                                <%
                                                    if (temphash.get("check_limit").equals("0"))
                                                    {
                                                %>
                                                <option value="<%=temphash.get("check_limit")%>"
                                                        selected="selected"><%=temphash.get("check_limit")%>
                                                </option>
                                                <option value="1"><%=partnerpreference_1%></option>
                                                <% }
                                                else
                                                {%>
                                                <option value="0"><%=partnerpreference_0%>/option>
                                                <option value="<%=temphash.get("check_limit")%>"
                                                        selected="selected"><%=temphash.get("check_limit")%>
                                                </option>
                                                <%}%></select>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Card_Velocity_Check%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Limit_Routing%>
                                            </td>

                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='card_velocity_check' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Card Velocity Check')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("card_velocity_check")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='limitRouting' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Limit Routing')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("limitRouting")))); %></select>
                                            </td>

                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Transaction Limits</strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                VPA Address Limit Check
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                VPA Address Daily Count
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                VPA Address Monthly Count
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                VPA Address Amount Limit Check
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                VPA Address Daily Amount Limit
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                VPA Address Monthly Amount Limit
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='vpaAddressLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'VPA Address Limit Check')">
                                                <%
                                                    out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressLimitCheck"))));
                                                %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                          name='vpaAddressDailyCount'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressDailyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,' VPA Address Daily Count')">

                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                          name='vpaAddressMonthlyCount'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressMonthlyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,' VPA Address Monthly Count')">

                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='vpaAddressAmountLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'VPA Address Amount Limit Check')" >
                                                <%
                                                    out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressAmountLimitCheck"))));
                                                %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                          name='vpaAddressDailyAmountLimit'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressDailyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'VPA Address Daily Amount Limit')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                          name='vpaAddressMonthlyAmountLimit'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressMonthlyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'VPA Address Monthly Amount Limit')">
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Ip Count Limit Check
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Ip Daily Count
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Ip Monthly Count
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Ip Amount Limit Check
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Ip Daily Amount Limit
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Ip Monthly Amount Limit
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff"
                                                                           name='customerIpLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Ip Count Limit Check')">
                                                <%
                                                    out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpLimitCheck"))));
                                                %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                          name='customerIpDailyCount'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpDailyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Ip Daily Count')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                          name='customerIpMonthlyCount'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpMonthlyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Ip Monthly Count')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                    class="<%=style%>">
                                                    <select class="form-control" style="background: #ffffff"
                                                                                name='customerIpAmountLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Ip Amount Limit Check')">
                                                    <%
                                                        out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpAmountLimitCheck"))));
                                                    %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                    class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                        name='customerIpDailyAmountLimit'
                                                                        value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpDailyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Ip Daily Amount Limit')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                    class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                        name='customerIpMonthlyAmountLimit'
                                                                        value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpMonthlyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Ip Monthly Amount Limit')">
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Name Count Limit Check
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Name Daily Count
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Name Monthly Count
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Name Amount Limit Check
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Name Daily Amount Limit
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Name Monthly Amount Limit
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff"
                                                        name='customerNameLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Name Count Limit Check')">
                                                    <%
                                                        out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameLimitCheck"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                    class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                        name='customerNameDailyCount'
                                                                        value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameDailyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Name Daily Count')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                    class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                        name='customerNameMonthlyCount'
                                                                        value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameMonthlyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Name Monthly Count')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                    class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff"
                                                        name='customerNameAmountLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Name Amount Limit Check')">
                                                    <%
                                                        out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameAmountLimitCheck"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                    class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                              name='customerNameDailyAmountLimit'
                                                                              value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameDailyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Name Daily Amount Limit')">

                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                    class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                              name='customerNameMonthlyAmountLimit'
                                                                              value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameMonthlyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Name Monthly Amount Limit')">

                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Email Count Limit Check
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Email Daily Count
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Email Monthly Count
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Email Amount Limit Check
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Email Daily Amount Limit
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Email Monthly Amount Limit
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                    class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff"
                                                        name='customerEmailLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Email Count Limit Check')">
                                                    <%
                                                        out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailLimitCheck"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center" class="<%=style%>">
                                                <input type=text class="form-control" style="background: #ffffff"
                                                        name='customerEmailDailyCount' <%=isReadOnly%> value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailDailyCount"))%>" onchange="ChangeFunction(this.value,'Customer Email Daily Count')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center" class="<%=style%>">
                                                <input type=text class="form-control" style="background: #ffffff"
                                                        name='customerEmailMonthlyCount' <%=isReadOnly%> value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailMonthlyCount"))%>" onchange="ChangeFunction(this.value,'Customer Email Monthly Count')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center" class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff"
                                                        name='customerEmailAmountLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Email Amount Limit Check')">
                                                    <%
                                                        out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailAmountLimitCheck"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center" class="<%=style%>">
                                                <input type=text class="form-control" style="background: #ffffff"
                                                        name='customerEmailDailyAmountLimit' <%=isReadOnly%> value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailDailyAmountLimit"))%>" onchange="ChangeFunction(this.value,'Customer Email Daily Amount Limit')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center" class="<%=style%>">
                                                <input type=text class="form-control" style="background: #ffffff"
                                                        name='customerEmailMonthlyAmountLimit' <%=isReadOnly%> value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailMonthlyAmountLimit"))%>" onchange="ChangeFunction(this.value,'Customer Email Monthly Amount Limit')">
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Phone Count Limit Check
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Phone Daily Count
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Phone Monthly Count
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Phone Amount Limit Check
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Phone Daily Amount Limit
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Phone Monthly Amount Limit
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center" class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff"
                                                        name='customerPhoneLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Phone Count Limit Check')">
                                                    <%
                                                        out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneLimitCheck"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center" class="<%=style%>">
                                                <input type=text class="form-control" style="background: #ffffff"
                                                       name='customerPhoneDailyCount' <%=isReadOnly%> value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneDailyCount"))%>" onchange="ChangeFunction(this.value,'Customer Phone Daily Count')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center" class="<%=style%>">
                                                <input type=text class="form-control" style="background: #ffffff"
                                                       name='customerPhoneMonthlyCount' <%=isReadOnly%> value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneMonthlyCount"))%>" onchange="ChangeFunction(this.value,'Customer Phone Monthly Count')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center" class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff"
                                                        name='customerPhoneAmountLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Phone Amount Limit Check')">
                                                    <%
                                                        out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneAmountLimitCheck"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center" class="<%=style%>">
                                                <input type=text class="form-control" style="background: #ffffff"
                                                        name='customerPhoneDailyAmountLimit' <%=isReadOnly%> value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneDailyAmountLimit"))%>" onchange="ChangeFunction(this.value,'Customer Phone Daily Amount Limit')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center" class="<%=style%>">
                                                <input type=text class="form-control" style="background: #ffffff"
                                                        name='customerPhoneMonthlyAmountLimit' <%=isReadOnly%> value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneMonthlyAmountLimit"))%>" onchange="ChangeFunction(this.value,'Customer Phone Monthly Amount Limit')">
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Payout Transactions</strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Payout Bank AccountNo Limit Check
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Bank AccountNo Daily Count
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Bank AccountNo Monthly Count
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Payout Bank AccountNo Amount Limit Check
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Bank AccountNo Daily Amount Limit
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Bank AccountNo Monthly Amount Limit
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='payoutBankAccountNoLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Payout Bank AccountNo Limit Check')" >
                                                <%
                                                    out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("payoutBankAccountNoLimitCheck"))));
                                                %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                          name='bankAccountNoDailyCount'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoDailyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Bank AccountNo Daily Count')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                          name='bankAccountNoMonthlyCount'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoMonthlyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Bank AccountNo Monthly Count')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='payoutBankAccountNoAmountLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Payout Bank AccountNo Amount Limit Check')" >
                                                <%
                                                    out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("payoutBankAccountNoAmountLimitCheck"))));
                                                %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                          name='bankAccountNoDailyAmountLimit'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoDailyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Bank AccountNo Daily Amount Limit')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"
                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                          name='bankAccountNoMonthlyAmountLimit'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoMonthlyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Bank AccountNo Monthly Amount Limit')">
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerpreference_General_Configuration%></strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Is_Activation%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_HasPaid%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Is_MerchantInterface%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Merchant Login with Otp
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Is_ExcessCaptureAllowed%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Is_FlightMode%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Blacklist_Transactions%>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='activation' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Activation')">
                                                <%
                                                    if (temphash.get("activation").equals("T"))
                                                    {
                                                %>
                                                <option value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("activation"))%>"
                                                        selected="selected"><%=ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("activation"))%>
                                                </option>
                                                <option value="Y"><%=partnerpreference_Y%></option>
                                                <option value="N"><%=partnerpreference_N%></option>
                                                <%
                                                }
                                                else if (temphash.get("activation").equals("N"))
                                                {
                                                %>
                                                <option value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("activation"))%>"
                                                        selected="selected"><%=ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("activation"))%>
                                                </option>
                                                <option value="T"><%=partnerpreference_T%></option>
                                                <option value="Y"><%=partnerpreference_Y%></option>
                                                <%
                                                }
                                                else
                                                {
                                                %>
                                                <option value="T"><%=partnerpreference_T%></option>
                                                <option value="N"><%=partnerpreference_N%></option>
                                                <option value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("activation"))%>"
                                                        selected="selected"><%=ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("activation"))%>
                                                </option>
                                                <% }%>
                                            </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='haspaid' <%=isReadOnly%> onchange="ChangeFunction(this.value,'HasPaid')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("haspaid")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='merchant_interface_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is MerchantInterface')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchant_interface_access")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='merchant_verify_otp' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Merchant Login with Otp')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchant_verify_otp")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='is_excesscapture_allowed' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is ExcessCaptureAllowed')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_excesscapture_allowed")))); %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='flight_mode' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is FlightMode')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("flight_mode")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='blacklist_transaction' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Blacklist Transactions')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("blacklist_transaction")))); %></select>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerpreference_Transaction_Configuration%></strong>
                                </h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Instant_Capture%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Auto_Redirect%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_VBV%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_MasterCardSupported%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Auto_Select_Terminal%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Is_POD_Required%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Is_RestrictedTicket%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Bin_Routing%>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='isservice' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Instant Capture')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isservice")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='auto_redirect' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Auto Redirect')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("auto_redirect")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='vbv' <%=isReadOnly%> onchange="ChangeFunction(this.value,'VBV')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("vbv")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='mastercard_supported' <%=isReadOnly%> onchange="ChangeFunction(this.value,'MasterCardSupported')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("mastercard_supported")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='auto_select_terminal' onchange="ChangeFunction(this.value,'Auto Select Terminal')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("auto_select_terminal")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='is_pod_required' onchange="ChangeFunction(this.value,'Is POD Required')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_pod_required")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='is_restricted_ticket' onchange="ChangeFunction(this.value,'Is RestrictedTicket')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_restricted_ticket")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='binRouting' onchange="ChangeFunction(this.value,'Bin Routing')">
                                                <%
                                                    out.println(Functions.comboval9(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("binRouting")))); %></select>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Chargeback_Allowed%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Is_Email%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Bin_Service%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Exp_Date_Offset%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Support_Section%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Support Number Needed
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Card_Whitelist_Level%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Multi_Currency_Support%>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                          name='chargeback_allowed_days'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("chargeback_allowed_days"))%>" onchange="ChangeFunction(this.value,'Chargeback Allowed Day')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='email_limit_enabled' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Email Limit Enabled')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("email_limit_enabled")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='bin_service' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Bin Service')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("bin_service")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff" name='exp_date_offset'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("exp_date_offset"))%>" onchange="ChangeFunction(this.value,'Exp Date Offset')">
                                            </td>

                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='support_section' <%=isReadOnly%> onchange="change(this,'supportNoNeeded')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("support_section")))); %></select>
                                            </td>

                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='supportNoNeeded' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Support Number Needed')" id="supportNoNeeded"  <%if(temphash.get("support_section").equals("N")){  %>disabled<%}%>>
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("supportNoNeeded")))); %></select>
                                                <input type=hidden size=10  name='supportNoNeeded' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("supportNoNeeded"))%>" >
                                            </td>

                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='card_whitelist_level' onchange="ChangeFunction(this.value,'Card Whitelist Level')">
                                                    <%
                                                        out.println(Functions.combovalForCardWhitelistLevel(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("card_whitelist_level")))); %>
                                                </select>
                                            </td>

                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='multi_Currency_support' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Multi Currency Support')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("multi_Currency_support")))); %></select>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_IP_Validation_Required%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Personal_Info_Display%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Personal_Info_Validation%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Rest_Checkout_Page%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_EMI_Support%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Merchant_Order%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Market_Place%>
                                            </td>
                                            <%--<td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Is_Cvv_Store%>
                                            </td>--%>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_isUniqueOrderIdRequired%></td>
                                        </tr>

                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='ip_validation_required' onchange="ChangeFunction(this.value,'IP Validation Required')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("ip_validation_required")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='personal_info_display' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Personal Info Display')">
                                                <%
                                                    out.println(Functions.comboPersonalInfo(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("personal_info_display")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='personal_info_validation' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Personal Info Validation')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("personal_info_validation")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='hosted_payment_page' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Rest Checkout Page')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("hosted_payment_page")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='emiSupport' <%=isReadOnly%> onchange="ChangeFunction(this.value,'EMI Support')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("emiSupport")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='merchant_order_details' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Merchant Order Details')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchant_order_details")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='marketplace' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Market Place')">
                                                <%
                                                    out.println(Functions.comboval4(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("marketplace")))); %></select>
                                            </td>
                                            <%--<td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='isCvvStore' <%=isReadOnly%>>
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isCvvStore")))); %></select>
                                            </td>--%>
                                            <td valign="middle" data-label=<%=partnerpreference_isUniqueOrderIdRequired%> align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='isUniqueOrderIdRequired' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Unique OrderId Required')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isUniqueOrderIdRequired")))); %></select>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is OTPRequired
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is CardStorageRequired
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Is OTPRequired" align="center"
                                                  class="<%=style%>"><select class="form-control" style="background: #ffffff" name="isOTPRequired" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is OTPRequired')">
                                                  <%
                                                      out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isOTPRequired"))));%></select>
                                            </td>
                                            <td valign="middle" data-label="Is CardStorageRequired" align="center"
                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name="isCardStorageRequired" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is CardStorageRequired')" >
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isCardStorageRequired"))));
                                                %></select>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerpreference_BackOffice_Access_Management%></strong>
                                </h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_DashBoard%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Account_Details%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Settings%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Transaction_Management%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Invoicing%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Rejected_Transaction%>
                                            </td>
                                            <%-- <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> EMI Configuration</td>--%>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='dashboard_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'DashBoard')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("dashboard_access")))); %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='accounting_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Account Details')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("accounting_access")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='setting_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Settings')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("setting_access")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='transactions_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Transaction Management')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("transactions_access")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='invoicing_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Invoicing')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("invoicing_access")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='rejected_transaction' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Rejected Transaction')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("rejected_transaction"))));
                                                    %>
                                                </select>
                                            </td>
                                            <%--<td valign="middle" data-label="Sr No" align="center"  class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                    name='emi_configuration' <%=isReadOnly%>>
                                              <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("emi_configuration")))); %></select>
                                            </td>--%>
                                        </tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Virtual_Terminal%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Merchant_Management%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Application_Manager%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Recurring%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Token_Management%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Virtual_Checkout%>
                                            </td>
                                            <%--<td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> &nbsp;</td>--%>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='virtualterminal_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Virtual Terminal')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("virtualterminal_access")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='merchantmgt_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Merchant Management')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchantmgt_access")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='is_appmanager_activate' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Application Manager')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_appmanager_activate")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='is_recurring' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Recurring')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_recurring"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='is_card_registration_allowed' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Token Management')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_card_registration_allowed"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='virtual_checkout' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Virtual Checkout')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("virtual_checkout"))));
                                                    %>
                                                </select>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Pay By Link
                                            </td>
                                        </tr>
                                        <tr>
                                           <td valign="middle" data-label="Sr No" align="center" class="<%=style%>">
                                               <select class="form-control" style="background: #ffffff" name='paybylink' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Pay By Link')">
                                                   <%
                                                       out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("paybylink"))));
                                                   %>
                                               </select>
                                           </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerpreference_Account_Details1%></strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        </tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Account_Summary%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Charges_Summary%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Transaction_Summary%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Reports%>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='accounts_account_summary_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Account Summary')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("accounts_account_summary_access")))); %>
                                            </select>
                                            </td>
                                            </select>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='accounts_charges_summary_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Charges Summary')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("accounts_charges_summary_access")))); %>
                                            </select>
                                            </td>
                                            </select>

                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='accounts_transaction_summary_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Transaction Summary')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("accounts_transaction_summary_access")))); %>
                                            </select>
                                            </td>
                                            </select>

                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='accounts_reports_summary_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Reports')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("accounts_reports_summary_access")))); %>
                                            </select>
                                            </td>
                                            </select>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerpreference_Settings1%></strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        </tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Merchant_Profile%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Organisation_Profile%>
                                            </td>
                                            <%--<td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Checkout Page
                                            </td>--%>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                View Key
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Generate_Key%>
                                            </td>
                                            <%-- <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Invoice Configuration</td>--%>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Merchant_Configuration%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Fraud_Rule_Configuration%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Whitelist_Details%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Block_Details%>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='settings_merchant_profile_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Merchant Profile')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_merchant_profile_access")))); %>
                                            </select>
                                            </td>
                                            </select>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='settings_organisation_profile_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Organisation Profile')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_organisation_profile_access")))); %>
                                            </select>
                                            </td>
                                            </select>
                                            <%-- <td valign="middle" data-label="Sr No" align="center"

                                                 class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                     name='settings_checkout_page_access' <%=isReadOnly%>>
                                                 <%
                                                     out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_checkout_page_access")))); %></select>
                                             </td>
                                             </select>--%>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='generateview' <%=isReadOnly%> onchange="ChangeFunction(this.value,'View Key')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("generateview")))); %></select>
                                            </td>
                                            </select>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='settings_generate_key_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Generate Key')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_generate_key_access")))); %></select>
                                            </td>
                                            </select>
                                            <%-- <td valign="middle" data-label="Sr No" align="center"  class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                     name='settings_invoice_config_access' <%=isReadOnly%>>
                                               <%
                                                 out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_invoice_config_access")))); %></select>
                                             </td>
                                             </select>--%>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='settings_merchant_config_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Merchant Configuration')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_merchant_config_access")))); %></select>
                                            </td>
                                            </select>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='settings_fraudrule_config_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Fraud Rule Configuration')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_fraudrule_config_access"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='settings_whitelist_details' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Whitelist Details')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_whitelist_details"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='settings_blacklist_details' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Block Details')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_blacklist_details"))));
                                                    %>
                                                </select>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerpreference_Transaction_Management%></strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Transactions%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Capture%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Reversal%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Payout%>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='transmgt_transaction_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Transactions')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("transmgt_transaction_access")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='transmgt_capture_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Capture')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("transmgt_capture_access")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='transmgt_reversal_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Reversal')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("transmgt_reversal_access")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff"
                                                        name='transmgt_payout_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Payout')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("transmgt_payout_access")))); %></select>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerpreference_Invocing%></strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Generate_Invoice%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Invoice_History%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Invoice_Configuration%>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff"
                                                        name='invoice_generate_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Generate Invoice')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("invoice_generate_access")))); %></select>
                                            </td>
                                            </select>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff"
                                                        name='invoice_history_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Invoice History')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("invoice_history_access")))); %></select>
                                            </td>
                                            </select>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff"
                                                        name='settings_invoice_config_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Invoice Configuration')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_invoice_config_access")))); %></select>
                                            </td>
                                            </select>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerpreference_Token_Management%></strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">

                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Registration_History%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Register_Card%>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='tokenmgt_registration_history_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Registration History')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("tokenmgt_registration_history_access")))); %></select>
                                            </td>
                                            </select>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='tokenmgt_register_card_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Register Card')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("tokenmgt_register_card_access")))); %></select>
                                            </td>
                                            </select>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerpreference_Merchant_Management%></strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_User_Management%>
                                            </td>
                                            <%--
                                                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Merchant Key </td>
                                            --%>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='merchantmgt_user_management_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'User Management')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchantmgt_user_management_access")))); %></select>
                                            </td>
                                            <%--<td valign="middle" data-label="Sr No" align="center"  class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                    name='isMerchantKey' <%=isReadOnly%>>
                                              <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isMerchantKey")))); %></select>
                                            </td>--%>
                                            </select>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerpreference_Template_Configuration%></strong>
                                </h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_IS_Pharma%>
                                            </td>
                                            <%--<td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is Powered By Logo
                                            </td>--%>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Template%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_PCI_Logo%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Partner_Logo%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Visa_Secure_Logo%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <label>isMerchantLogoBO</label>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_MC_Secure_Logo%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Consent%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Security_Logo%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Checkout_Timer%>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <%=partnerpreference_Checkout_Timer_Time%>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='is_pharma' <%=isReadOnly%> onchange="ChangeFunction(this.value,'IS Pharma(Y/N)')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_pharma")))); %></select>
                                                <input type="hidden" value="<%= temphash.get("is_powered_by")%>" name="is_powered_by">
                                            </td>
                                            <%-- <%
                                             PartnerModuleManager partnerModuleManager = new PartnerModuleManager();
                                             String disabled ="";
                                             String role= partnerModuleManager.getRoleofPartner(partnerId);
                                             if(role.contains("superpartner")){
                                             disabled ="disabled";
                                             }
                                                 %>
                                             <td valign="middle" data-label="Sr No" align="center"
                                                 class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                     name='is_powered_by' <%=disabled%>>
                                                 <%
                                                     out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_powered_by")))); %></select>
                                             </td>--%>

                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='template' onchange="ChangeFunction(this.value,'Template')">

                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("template")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='is_pci_logo' onchange="ChangeFunction(this.value,'PCI Logo')">
                                                <%
                                                    if ("Y".equals(temphash.get("is_pci_logo")))
                                                    {
                                                %>
                                                <option value="Y">Y</option>
                                                <option value="N">N</option>
                                                <%
                                                }
                                                else
                                                {
                                                %>
                                                <option value="N">N</option>
                                                <option value="Y">Y</option>
                                                <% }%>
                                            </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='is_partner_logo'<%--<%=isReadOnly%>--%> onchange="ChangeFunction(this.value,'Partner Logo')">
                                                <%
                                                    if ("Y".equals(temphash.get("is_partner_logo")))
                                                    {
                                                %>
                                                <option value="Y">Y</option>
                                                <option value="N">N</option>
                                                <%
                                                }
                                                else
                                                {
                                                %>
                                                <option value="N">N</option>
                                                <option value="Y">Y</option>
                                                <% }%>
                                            </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='vbvLogo'<%--<%=isReadOnly%>--%> onchange="ChangeFunction(this.value,'Visa Secure Logo')">
                                                <%
                                                    if ("Y".equals(temphash.get("vbvLogo")))
                                                    {
                                                %>
                                                <option value="Y">Y</option>
                                                <option value="N">N</option>
                                                <%
                                                }
                                                else
                                                {
                                                %>
                                                <option value="N">N</option>
                                                <option value="Y">Y</option>
                                                <% }%>
                                            </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                    class="<%=style%>"><select class="form-control" style="background: #ffffff" name='isMerchantLogoBO' <%--<%=isReadOnly%>--%> onchange="ChangeFunction(this.value, 'isMerchantLogoBO')">
                                                    <%
                                                        if ("Y".equals(temphash.get("isMerchantLogoBO")))
                                                        {
                                                    %>
                                                    <option value="Y">Y</option>
                                                    <option value="N">N</option>
                                                    <%
                                                        }
                                                        else
                                                        {
                                                    %>
                                                    <option value="N">N</option>
                                                    <option value="Y">Y</option>
                                                    <% } %>
                                                    </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='masterSecureLogo'<%--<%=isReadOnly%>--%> onchange="ChangeFunction(this.value,'MC Secure Logo')">
                                                <%
                                                    if ("Y".equals(temphash.get("masterSecureLogo")))
                                                    {
                                                %>
                                                <option value="Y">Y</option>
                                                <option value="N">N</option>
                                                <%
                                                }
                                                else
                                                {
                                                %>
                                                <option value="N">N</option>
                                                <option value="Y">Y</option>
                                                <% }%>
                                            </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='consent'<%--<%=isReadOnly%>--%>onchange="ChangeFunction(this.value,'Consent')">
                                                <%
                                                    if ("Y".equals(temphash.get("consent")))
                                                    {
                                                %>
                                                <option value="Y">Y</option>
                                                <option value="N">N</option>
                                                <%
                                                }
                                                else
                                                {
                                                %>
                                                <option value="N">N</option>
                                                <option value="Y">Y</option>
                                                <% }%>
                                            </select>
                                            </td>

                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='isSecurityLogo'<%--<%=isReadOnly%>--%> onchange="ChangeFunction(this.value,'Is Security Logo')">
                                                <%
                                                    if ("Y".equals(temphash.get("isSecurityLogo")))
                                                    {
                                                %>
                                                <option value="Y">Y</option>
                                                <option value="N">N</option>
                                                <%
                                                }
                                                else
                                                {
                                                %>
                                                <option value="N">N</option>
                                                <option value="Y">Y</option>
                                                <% }%>
                                            </select>
                                            </td>

                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='checkoutTimer' onchange="ChangeFunction(this.value,'Checkout Timer')">
                                                <%
                                                    if ("Y".equals(temphash.get("checkoutTimer")))
                                                    {
                                                %>
                                                <option value="Y">Y</option>
                                                <option value="N">N</option>
                                                <%
                                                }
                                                else
                                                {
                                                %>
                                                <option value="N">N</option>
                                                <option value="Y">Y</option>
                                                <% }%>
                                            </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff" name='checkoutTimerTime'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("checkoutTimerTime"))%>" onchange="ChangeFunction(this.value,'Checkout Timer Time')">
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Token Configuration</strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is TokenizationAllowed
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is AddressDetails Required
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Token Valid Days
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is Card Encryption Enable
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='is_tokenization_allowed' onchange="ChangeFunction(this.value,'Is TokenizationAllowed')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_tokenization_allowed")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='is_address_details_required' onchange="ChangeFunction(this.value,'Is AddressDetails Required')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_address_details_required")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff" name='token_valid_days'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("token_valid_days"))%>" onchange="ChangeFunction(this.value,'Token Valid Days')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='is_card_encryption_enable' onchange="ChangeFunction(this.value,'Is Card Encryption Enable')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_card_encryption_enable")))); %></select>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Fraud Defender Configuration</strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        </tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Purchase Inquiry Refund
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Purchase Inquiry Blacklist
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Fraud Determined Refund
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Fraud Determined Blacklist
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Dispute Initiated Refund
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Dispute Initiated Blacklist
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Exception file listing Blacklist
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Stop payment Blacklist
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="ispurchase_inquiry_refund" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='ispurchase_inquiry_refund' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Purchase Inquiry Refund	')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("ispurchase_inquiry_refund")))); %>
                                            </select>
                                            </td>
                                            </select>
                                            <td valign="middle" data-label="ispurchase_inquiry_blacklist" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='ispurchase_inquiry_blacklist' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Purchase Inquiry Blacklist')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("ispurchase_inquiry_blacklist")))); %>
                                            </select>
                                            </td>
                                            </select>
                                            <td valign="middle" data-label="isfraud_determined_refund" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='isfraud_determined_refund' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Fraud Determined Refund')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isfraud_determined_refund")))); %></select>
                                            </td>
                                            </select>
                                            <td valign="middle" data-label="isfraud_determined_blacklist" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='isfraud_determined_blacklist' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Fraud Determined Blacklist')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isfraud_determined_blacklist")))); %></select>
                                            </td>
                                            </select>
                                            <td valign="middle" data-label="isdispute_initiated_refund" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='isdispute_initiated_refund' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Dispute Initiated Refund')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isdispute_initiated_refund"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="isdispute_initiated_blacklist" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='isdispute_initiated_blacklist' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Dispute Initiated Blacklist')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isdispute_initiated_blacklist"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="isexception_file_listing_blacklist" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='isexception_file_listing_blacklist' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Exception file listing Blacklist')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isexception_file_listing_blacklist"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="isstop_payment_blacklist" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='isstop_payment_blacklist' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Stop payment Blacklist')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isstop_payment_blacklist"))));
                                                    %>
                                                </select>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Whitelisting Configuration</strong>
                                </h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is Card Whitelisted
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is Ip Whitelisted
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is IP Whitelisted For APIs
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is Domain Whitelisted
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='iswhitelisted' onchange="ChangeFunction(this.value,'Is Card Whitelisted')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("iswhitelisted")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='is_ip_whitelisted' onchange="ChangeFunction(this.value,'Is Ip Whitelisted')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_ip_whitelisted")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='is_rest_whitelisted' onchange="ChangeFunction(this.value,'Is IP Whitelisted For APIs')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_rest_whitelisted")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='isDomainWhitelisted' onchange="ChangeFunction(this.value,'Is Domain Whitelisted')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isDomainWhitelisted")))); %></select>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>HR Transaction Configuration</strong>
                                </h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                HR alertPROOF
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                HR Parameterized
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='hralertproof' <%=isReadOnly%> onchange="ChangeFunction(this.value,'HR alertPROOF')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("hralertproof")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='hrparameterised' <%=isReadOnly%> onchange="ChangeFunction(this.value,'HR Parameterized')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("hrparameterised")))); %></select>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Refund Configuration</strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is Refund
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Daily Refund Limit
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Refund Allowed Day's
                                            </td>
                                            <%--<td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is Refund Mail Sent
                                            </td>--%>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is Multiple Refund
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is Partial Refund
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='is_refund' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Refund')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_refund")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff" name='refund_daily_limit'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("refund_daily_limit"))%>" onchange="ChangeFunction(this.value,'Daily Refund Limit')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff" name='refundallowed_days'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("refundallowed_days"))%>" onchange="ChangeFunction(this.value,'Refund Allowed Day')">
                                            </td>

                                            <%--<td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='isRefundEmailSent' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Refund Mail Sent')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isRefundEmailSent")))); %></select>
                                            </td>--%>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='isMultipleRefund' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Multiple Refund')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isMultipleRefund")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff" name='isPartialRefund' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Partial Refund')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isPartialRefund")))); %></select>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Email Configuration</strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                IS Validate Email
                                            </td>
                                            <%--<td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer Reminder Email
                                            </td>--%>
                                            <%--<td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is Email Sent
                                            </td>--%>
                                            <%--<td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Chargeback Email (Y/N)
                                            </td>--%>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Email Template Language
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='is_validate_email' <%=isReadOnly%> onchange="ChangeFunction(this.value,'IS Validate Email')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_validate_email")))); %></select>
                                            </td>
                                            <%--<td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='cust_reminder_mail' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Reminder Email')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("cust_reminder_mail")))); %></select>
                                            </td>--%>
                                            <%--<td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='merchant_email_sent' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Email Sent')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchant_email_sent")))); %></select>
                                            </td>--%>

                                           <%-- <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='chargebackEmail' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Chargeback Email (Y/N)')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("chargebackEmail")))); %></select>
                                            </td>--%>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='emailTemplateLang' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Email Template Language')">
                                                <%
                                                    out.println(Functions.combovalLanguage(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("emailTemplateLang")))); %></select>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>SMS Configuration</strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Merchant SMS Activation
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Customer SMS Activation
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='merchant_sms_activation' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Merchant SMS Activation')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchant_sms_activation")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='customer_sms_activation' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer SMS Activation')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("customer_sms_activation")))); %></select>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Invoice Configuration</strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Invoice Merchant Details
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is IP whitelist for Invoice
                                            </td>
                                        </tr>
                                        <tr>
                                            <td align="center" align="center" class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                                                         name='invoice_template' onchange="ChangeFunction(this.value,'Invoice Merchant Details')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("invoice_template")))); %></select>
                                            </td>
                                            <td align="center" align="center" class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                                                         name='ip_whitelist_invoice' onchange="ChangeFunction(this.value,'Is IP whitelist for Invoice')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("ip_whitelist_invoice")))); %></select>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Fraud Configuration</strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Max Score Allowed
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Max Score Reversal
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Online Fraud Check
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Internal Fraud Check
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff" name='max_score_allowed'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("max_score_allowed"))%>" onchange="ChangeFunction(this.value,'Max Score Allowed')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><input type=text class="form-control" style="background: #ffffff"
                                                                          name='max_score_auto_reversal'
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("max_score_auto_reversal"))%>" onchange="ChangeFunction(this.value,'Max Score Reversal')">
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='online_fraud_check' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Online Fraud Check')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("online_fraud_check")))); %></select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                                           name='internalFraudCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Internal Fraud Check')">
                                                <%
                                                    out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("internalFraudCheck")))); %></select>

                                            </td>

                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Split Configuration</strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Split Payment Allowed
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Split Payment Type
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='is_split_payment' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Split Payment Allowed')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_split_payment")))); %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='split_payment_type' onchange="ChangeFunction(this.value,'Split Payment Type')">
                                                    <%
                                                        out.println(Functions.combovalForSplitPayment(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("split_payment_type")))); %>
                                                </select>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Invoizer Configuration</strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is Virtual Checkout Allowed
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is Phone Required
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is Email Required
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is Share Allowed
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is Signature Allowed
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Is Save Receipt Allowed
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                Default Language
                                            </td>
                                        </tr>
                                        <tr>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='isVirtualCheckoutAllowed' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Virtual Checkout Allowed')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isVirtualCheckoutAllowed")))); %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='isMobileAllowedForVC' onchange="ChangeFunction(this.value,'Is Phone Required')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isMobileAllowedForVC")))); %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='isEmailAllowedForVC' onchange="ChangeFunction(this.value,'Is Email Required')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isEmailAllowedForVC")))); %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='isShareAllowed' onchange="ChangeFunction(this.value,'Is Share Allowed')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isShareAllowed")))); %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='isSignatureAllowed' onchange="ChangeFunction(this.value,'Is Signature Allowed')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isSignatureAllowed")))); %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='isSaveReceiptAllowed' onchange="ChangeFunction(this.value,'Is Save Receipt Allowed')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isSaveReceiptAllowed")))); %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Sr No" align="center"

                                                class="<%=style%>">
                                                <select class="form-control" style="background: #ffffff" name='defaultLanguage' onchange="ChangeFunction(this.value,'Default Language')">
                                                    <%
                                                        out.println(Functions.combovalLang(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("defaultLanguage")))); %>
                                                </select>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                                    <%
                    }
                %>
                                <table align="center" id="smalltable" border="0" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 50%;">
                                    <tr style="background-color: #ffffff!important;">
                                        <td align="center" colspan="2">
                                            <button type="submit" value="Save" class="btn btn-default">
                                                Save
                                            </button>
                                        </td>
                                    </tr>
                                </table>
            </form>
            <br>
            <%
                }
                else
                {
                    out.println(Functions.NewShowConfirmation1("Sorry", "No Records Found."));
                }
            %>
        </div>
    </div>
</div>
</div>
</body>
</html>