<%@ page import="com.directi.pg.Functions,com.directi.pg.LoadProperties" %>
<%@ include file="top.jsp" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.PartnerModuleManager" %>
<%@ page import="com.manager.enums.TemplatePreference" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.ResourceBundle" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
    PartnerFunctions partnerFunctions = new PartnerFunctions();
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String transactionsprocess_Merchant_Transaction = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Merchant_Transaction")) ? rb1.getString("transactionsprocess_Merchant_Transaction") : "Merchant Transaction Settings";
    String transactionsprocess_PartnerID = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_PartnerID")) ? rb1.getString("transactionsprocess_PartnerID") : "Partner ID";
    String transactionsprocess_MerchantID = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_MerchantID")) ? rb1.getString("transactionsprocess_MerchantID") : "Merchant ID*";
    String transactionsprocess_Search = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Search")) ? rb1.getString("transactionsprocess_Search") : "Search";
    String transactionsprocess_Transaction_Configuration = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Transaction_Configuration")) ? rb1.getString("transactionsprocess_Transaction_Configuration") : "Transaction Configuration";
    String transactionsprocess_Is_Activation = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Is_Activation")) ? rb1.getString("transactionsprocess_Is_Activation") : "Is Activation";
    String transactionsprocess_Instant_Capture = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Instant_Capture")) ? rb1.getString("transactionsprocess_Instant_Capture") : "Instant Capture";
    String transactionsprocess_Master_Card = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Master_Card")) ? rb1.getString("transactionsprocess_Master_Card") : "Master Card Supported";
    String transactionsprocess_Is_ExcessCaptureAllowed = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Is_ExcessCaptureAllowed")) ? rb1.getString("transactionsprocess_Is_ExcessCaptureAllowed") : "Is ExcessCaptureAllowed";
    String transactionsprocess_Is_POD = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Is_POD")) ? rb1.getString("transactionsprocess_Is_POD") : "Is POD Required";
    String transactionsprocess_Exp_Date = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Exp_Date")) ? rb1.getString("transactionsprocess_Exp_Date") : "Exp Date Offset";
    String transactionsprocess_Multicurrency = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Multicurrency")) ? rb1.getString("transactionsprocess_Multicurrency") : "Multicurrency Supported";
    String transactionsprocess_Autoredirect = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Autoredirect")) ? rb1.getString("transactionsprocess_Autoredirect") : "Autoredirect";
    String transactionsprocess_IP_Validation = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_IP_Validation")) ? rb1.getString("transactionsprocess_IP_Validation") : "IP Validation Required";
    String transactionsprocess_Merchant_Order = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Merchant_Order")) ? rb1.getString("transactionsprocess_Merchant_Order") : "Merchant Order Details";
    String transactionsprocess_Y = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Y")) ? rb1.getString("transactionsprocess_Y") : "Y";
    String transactionsprocess_N = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_N")) ? rb1.getString("transactionsprocess_N") : "N";
    String transactionsprocess_T = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_T")) ? rb1.getString("transactionsprocess_T") : "T";
    String transactionsprocess_Bin_Routing = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Bin_Routing")) ? rb1.getString("transactionsprocess_Bin_Routing") : "Bin Routing";
    String transactionsprocess_Personal_Info = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Personal_Info")) ? rb1.getString("transactionsprocess_Personal_Info") : "Personal Info Display";
    String transactionsprocess_Validation = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Validation")) ? rb1.getString("transactionsprocess_Validation") : "Personal Info Validation";
    String transactionsprocess_Rest_Checkout = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Rest_Checkout")) ? rb1.getString("transactionsprocess_Rest_Checkout") : "Rest Checkout Page";
    String transactionsprocess_Ip_Whitelisted = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Ip_Whitelisted")) ? rb1.getString("transactionsprocess_Ip_Whitelisted") : "Is Ip Whitelisted";
    String transactionsprocess_whitelisted = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_whitelisted")) ? rb1.getString("transactionsprocess_whitelisted") : "Is IP Whitelisted For APIs";
    String transactionsprocess_Blacklist_Transactions = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Blacklist_Transactions")) ? rb1.getString("transactionsprocess_Blacklist_Transactions") : "Blacklist Transactions";
    String transactionsprocess_Notification_Url = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Notification_Url")) ? rb1.getString("transactionsprocess_Notification_Url") : "Notification Url";
    String transactionsprocess_TermsUrl = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_TermsUrl")) ? rb1.getString("transactionsprocess_TermsUrl") : "Terms Url";
    String transactionsprocess_Privacy_Policy = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Privacy_Policy")) ? rb1.getString("transactionsprocess_Privacy_Policy") : "Privacy Policy Url";
    String transactionsprocess_Emi_Support = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Emi_Support")) ? rb1.getString("transactionsprocess_Emi_Support") : "Emi Support";
    String transactionsprocess_Bin_Service = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Bin_Service")) ? rb1.getString("transactionsprocess_Bin_Service") : "Bin Service";
    String transactionsprocess_Support_Section = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Support_Section")) ? rb1.getString("transactionsprocess_Support_Section") : "Support Section";
    String transactionsprocess_Market_Place = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Market_Place")) ? rb1.getString("transactionsprocess_Market_Place") : "Market Place";
    String transactionsprocess_Cvv_Store = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Cvv_Store")) ? rb1.getString("transactionsprocess_Cvv_Store") : "Is Cvv Store ";
    String transactionsprocess_isUniqueOrderIdRequired = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_isUniqueOrderIdRequired")) ? rb1.getString("transactionsprocess_isUniqueOrderIdRequired") : "Is Unique OrderId Required";
    String transactionsprocess_isDomainWhitelisted = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_isDomainWhitelisted")) ? rb1.getString("transactionsprocess_isDomainWhitelisted") : "Is Domain Whitelisted";
    String transactionsprocess_Refund_Configuration = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Refund_Configuration")) ? rb1.getString("transactionsprocess_Refund_Configuration") : "Refund Configuration";
    String transactionsprocess_Refund_Allowed = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Refund_Allowed")) ? rb1.getString("transactionsprocess_Refund_Allowed") : "Is Refund Allowed";
    String transactionsprocess_Multiple_Refund = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Multiple_Refund")) ? rb1.getString("transactionsprocess_Multiple_Refund") : "Is Multiple Refund";
    String transactionsprocess_Daily_Refund = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Daily_Refund")) ? rb1.getString("transactionsprocess_Daily_Refund") : "Daily Refund Limit";
    String transactionsprocess_Refund_Allowed1 = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Refund_Allowed1")) ? rb1.getString("transactionsprocess_Refund_Allowed1") : "Refund Allowed Day's";
    String transactionsprocess_Partial_Refund = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Partial_Refund")) ? rb1.getString("transactionsprocess_Partial_Refund") : "Is Partial Refund";
    String transactionsprocess_SMS_Configuration = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_SMS_Configuration")) ? rb1.getString("transactionsprocess_SMS_Configuration") : "SMS Configuration";
    String transactionsprocess_SMS_Activation = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_SMS_Activation")) ? rb1.getString("transactionsprocess_SMS_Activation") : "SMS Activation";
    String transactionsprocess_Customer_Activation = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Customer_Activation")) ? rb1.getString("transactionsprocess_Customer_Activation") : "Customer Activation";
    String transactionsprocess_Token_Configuration = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Token_Configuration")) ? rb1.getString("transactionsprocess_Token_Configuration") : "Token Configuration";
    String transactionsprocess_TokenizationAllowed = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_TokenizationAllowed")) ? rb1.getString("transactionsprocess_TokenizationAllowed") : "Is TokenizationAllowed";
    String transactionsprocess_Card_Encryption = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Card_Encryption")) ? rb1.getString("transactionsprocess_Card_Encryption") : "Is Card Encryption Enable ";
    String transactionsprocess_Is_AddressDetails = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Is_AddressDetails")) ? rb1.getString("transactionsprocess_Is_AddressDetails") : "Is AddressDetails Required";
    String transactionsprocess_Virtual_Checkout = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Virtual_Checkout")) ? rb1.getString("transactionsprocess_Virtual_Checkout") : "=Virtual Checkout Configuration";
    String transactionsprocess_Virtual_Checkout1 = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Virtual_Checkout1")) ? rb1.getString("transactionsprocess_Virtual_Checkout1") : "Is Virtual Checkout Allowed";
    String transactionsprocess_Phone_Required = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Phone_Required")) ? rb1.getString("transactionsprocess_Phone_Required") : "Is Phone Required";
    String transactionsprocess_Email_Required = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Email_Required")) ? rb1.getString("transactionsprocess_Email_Required") : "Is Email Required";
    String transactionsprocess_checkout = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_checkout")) ? rb1.getString("transactionsprocess_checkout") : "Checkout Template Configuration";
    String transactionsprocess_Is_Pharma = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Is_Pharma")) ? rb1.getString("transactionsprocess_Is_Pharma") : "Is Pharma(Y/N)";
    String transactionsprocess_Powered_Logo = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Powered_Logo")) ? rb1.getString("transactionsprocess_Powered_Logo") : "Is Powered By Logo";
    String transactionsprocess_Template = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Template")) ? rb1.getString("transactionsprocess_Template") : "Template";
    String transactionsprocess_Merchant_Logo = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Merchant_Logo")) ? rb1.getString("transactionsprocess_Merchant_Logo") : "Is Merchant Logo";
    String transactionsprocess_Security_Logo = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Security_Logo")) ? rb1.getString("transactionsprocess_Security_Logo") : "Is Security Logo";
    String transactionsprocess_PCI_Logo = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_PCI_Logo")) ? rb1.getString("transactionsprocess_PCI_Logo") : "PCI Logo";
    String transactionsprocess_Partner_Logo = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Partner_Logo")) ? rb1.getString("transactionsprocess_Partner_Logo") : "Partner Logo";
    String transactionsprocess_Visa_Secure = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Visa_Secure")) ? rb1.getString("transactionsprocess_Visa_Secure") : "Visa Secure Logo";
    String transactionsprocess_MC_Secure = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_MC_Secure")) ? rb1.getString("transactionsprocess_MC_Secure") : "MC Secure Logo";
    String transactionsprocess_Consent = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Consent")) ? rb1.getString("transactionsprocess_Consent") : "Consent";
    String transactionsprocess_Checkout_Timer = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Checkout_Timer")) ? rb1.getString("transactionsprocess_Checkout_Timer") : "Checkout Timer";
    String transactionsprocess_Checkout_Template = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Checkout_Template")) ? rb1.getString("transactionsprocess_Checkout_Template") : "Checkout Template Colors";
    String transactionsprocess_used = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_used")) ? rb1.getString("transactionsprocess_used") : "(Used for merchants on Old checkout page)";
    String transactionsprocess_Template1 = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Template1")) ? rb1.getString("transactionsprocess_Template1") : "Template";
    String transactionsprocess_BackGround = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_BackGround")) ? rb1.getString("transactionsprocess_BackGround") : "BackGround";
    String transactionsprocess_Color = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Color")) ? rb1.getString("transactionsprocess_Color") : "Color";
    String transactionsprocess_Panel = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Panel")) ? rb1.getString("transactionsprocess_Panel") : "Panel";
    String transactionsprocess_Heading = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Heading")) ? rb1.getString("transactionsprocess_Heading") : "Heading";
    String transactionsprocess_Left = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Left")) ? rb1.getString("transactionsprocess_Left") : "Left";
    String transactionsprocess_Navigation = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Navigation")) ? rb1.getString("transactionsprocess_Navigation") : "Navigation";
    String transactionsprocess_Label = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Label")) ? rb1.getString("transactionsprocess_Label") : "Label";
    String transactionsprocess_Font = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Font")) ? rb1.getString("transactionsprocess_Font") : "Font";
    String transactionsprocess_Body = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Body")) ? rb1.getString("transactionsprocess_Body") : "Body";
    String transactionsprocess_ForeGround = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_ForeGround")) ? rb1.getString("transactionsprocess_ForeGround") : "ForeGround";
    String transactionsprocess_Textbox = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Textbox")) ? rb1.getString("transactionsprocess_Textbox") : "Textbox";
    String transactionsprocess_Vector = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Vector")) ? rb1.getString("transactionsprocess_Vector") : "Vector";
    String transactionsprocess_Icon = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Icon")) ? rb1.getString("transactionsprocess_Icon") : "Icon";
    String transactionsprocess_new = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_new")) ? rb1.getString("transactionsprocess_new") : "NEW Checkout Template Colors";
    String transactionsprocess_used1 = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_used1")) ? rb1.getString("transactionsprocess_used1") : "(Used for merchants on New checkout page)";
    String transactionsprocess_Header = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Header")) ? rb1.getString("transactionsprocess_Header") : "Header";
    String transactionsprocess_Bar = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Bar")) ? rb1.getString("transactionsprocess_Bar") : "Bar";
    String transactionsprocess_Button = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Button")) ? rb1.getString("transactionsprocess_Button") : "Button";
    String transactionsprocess_Footer = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Footer")) ? rb1.getString("transactionsprocess_Footer") : "Footer";
    String transactionsprocess_Background = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Background")) ? rb1.getString("transactionsprocess_Background") : "Background";
    String transactionsprocess_Full = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Full")) ? rb1.getString("transactionsprocess_Full") : "Full";
    String transactionsprocess_Timer = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Timer")) ? rb1.getString("transactionsprocess_Timer") : "Timer";
    String transactionsprocess_Shadow = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Shadow")) ? rb1.getString("transactionsprocess_Shadow") : "Shadow";
    String transactionsprocess_Box = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Box")) ? rb1.getString("transactionsprocess_Box") : "Box";
    String transactionsprocess_Flight_Mode = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Flight_Mode")) ? rb1.getString("transactionsprocess_Flight_Mode") : "Flight Mode";
    String transactionsprocess_FlightMode = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_FlightMode")) ? rb1.getString("transactionsprocess_FlightMode") : "FlightMode";
    String transactionsprocess_Split_Transaction = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Split_Transaction")) ? rb1.getString("transactionsprocess_Split_Transaction") : "Split Transaction Configuration";
    String transactionsprocess_Split_payment = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Split_payment")) ? rb1.getString("transactionsprocess_Split_payment") : "Split Payment Allowed";
    String transactionsprocess_Split_type = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Split_type")) ? rb1.getString("transactionsprocess_Split_type") : "Split Payment Type";
    String transactionsprocess_mail = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_mail")) ? rb1.getString("transactionsprocess_mail") : "Mail Template Colors";
    String transactionsprocess_Save = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Save")) ? rb1.getString("transactionsprocess_Save") : "Save";
    String transactionsprocess_Report_Table = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Report_Table")) ? rb1.getString("transactionsprocess_Report_Table") : "Report Table";
    String transactionsprocess_Sorry = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Sorry")) ? rb1.getString("transactionsprocess_Sorry") : "Sorry";
    String transactionsprocess_no = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_no")) ? rb1.getString("transactionsprocess_no") : "No Records Found.";
    String transactionsprocess_Table = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Table")) ? rb1.getString("transactionsprocess_Table") : "Table";
    String transactionsprocess_Checkout_Timer_Time = StringUtils.isNotEmpty(rb1.getString("transactionsprocess_Checkout_Timer_Time")) ? rb1.getString("transactionsprocess_Checkout_Timer_Time") : "Checkout Timer Time";
    String isMerchantLogoBO= Functions.checkStringNull(request.getParameter("isMerchantLogoBO"));

    String str=null;
    if (isMerchantLogoBO != null) str = str + "&isMerchantLogoBO=" + isMerchantLogoBO;
%>
<%!
    private static Logger log = new Logger("transactionsprocess.jsp");
%>
<html>
<head>
    <title><%=company%> Merchant Settings> Merchant Transaction Settings </title>
    <style type="text/css">
        @media (max-width: 640px) {
            #smalltable {
                width: 100% !important;
            }
        }

        @media (min-width: 641px) {
            #flightid {
                width: inherit;
            }
        }
        #ui-id-2
        {
            overflow: auto;
            max-height: 350px;
        }
    </style>
    <%-- <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
     <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
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
<script type="text/javascript">
    function mySave()
    {
        document.getElementById('myformnameSave').submit();
    }


    $(document).ready(function(){
        var relaywithauth = document.getElementById("flightid").value;
        if (relaywithauth == 'Y') {
            $('#div_split').show();
        }
        else
        {
            $('#div_split').hide();
        }
    });

    function hidblock(flightid)
    {
        if(flightid.value=='Y')
        {
            $('#div_split').show();
        }
        else
        {
            $('#div_split').hide();
        }
    }

</script>

<body class="bodybackground">
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <%--<div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/addPartnerLogo.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button class="btn-xs" type="submit" value="transactionsprocess" name="submit" name="B1"
                                style="background: transparent;border: 0;">
                            <img style="height: 45px;width: 200px;" src="/partner/images/addnewmerchantlogo.png">
                        </button>
                    </form>
                </div>
            </div>
            <br>
            <br>--%>
            <br>

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=transactionsprocess_Merchant_Transaction%></strong>
                            </h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <% ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                            session.setAttribute("submit", "transactionsprocess");

                            if (partner.isLoggedInPartner(session))
                            {
                                // LinkedHashMap memberidDetails= partner.getPartnerMembersDetail((String) session.getAttribute("merchantid"));
                                String memberid = nullToStr(request.getParameter("memberid"));
                                String pid = nullToStr(request.getParameter("pid"));
                                String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
                                String Config =null;
                                if(Roles.contains("superpartner")){

                                }else{
                                    pid = String.valueOf(session.getAttribute("merchantid"));
                                    Config = "disabled";
                                }
                                String partnerid = session.getAttribute("partnerId").toString();
                        %>
                        <form action="/partner/net/TransactionProcessing?ctoken=<%=ctoken%>" method="get" name="F1"
                              onsubmit="" class="form-horizontal">
                            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                            <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                            <br>

                            <div class="widget-content padding">
                                <div id="horizontal-form">
                                    <%
                                        String errormsg = (String) request.getAttribute("cbmessage");
                                        String errormsg1 = (String) request.getAttribute("error");
                                        String error = (String) request.getAttribute("errormessage");
                                        if (partnerFunctions.isValueNull(errormsg1))
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg1 + "</h5>");
                                        }
                                    %>
                                    <div class="form-group col-md-4">
                                        <label class="col-sm-4 control-label"><%=transactionsprocess_PartnerID%></label>
                                        <div class="col-sm-8">
                                            <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                                            <input name="pid" type="hidden" value="<%=pid%>">
                                        </div>
                                    </div>

                                    <div class="form-group col-md-4">
                                        <label class="col-sm-4 control-label"><%=transactionsprocess_MerchantID%></label>
                                        <div class="col-sm-8">
                                            <input name="memberid" id="member" value="<%=memberid%>" class="form-control"
                                                   autocomplete="on">
                                        </div>
                                    </div>
                                    <div class="form-group col-md-3">
                                        <div class="col-sm-offset-2 col-sm-3">
                                            <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                                                &nbsp;&nbsp;<%=transactionsprocess_Search%>
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <%
                Map<String, Object> merchantTemplateSetting = null;
                if (request.getAttribute("merchantTemplateSetting") != null)
                {
                    merchantTemplateSetting = (Map<String, Object>) request.getAttribute("merchantTemplateSetting");
                }
            %>
            <form action="/partner/net/SetReservesTransaction?ctoken=<%=ctoken%>" method=post name="myformnameSave"
                  id="myformnameSave">
                <input type="hidden" value="" name="onchangedvalue" id="onchangedvalue">   <%--***do not remove the field*****--%>

                    <%
                    Hashtable hash = (Hashtable) request.getAttribute("memberdetails");
                    Hashtable tempHash = null;
                    Hashtable hashTablePartner= (Hashtable)request.getAttribute("partners");
                    Hashtable agent= (Hashtable)request.getAttribute("agents");
                    Functions functions = new Functions();
                    int records = 0;
                    String memberId=null;
                    String notificationUrl= "";
                    String termsUrl="";
                    String privacyPolicyUrl="";
                    if((hash!=null && hash.size()>0) && (partner!=null && hashTablePartner.size()>0) && (agent!=null && agent.size()>0))
                    {
                        try{
                            records = Integer.parseInt((String) hash.get("records"));
                    System.out.println("records ::::"+records);

                           }
                        catch(Exception ex){
                            log.error("Records & TotalRecords is found null",ex);
                        }
                    }
                    if(records > 0)
                    {
                    String style = "td1";
                    for (int pos = 1; pos <= records; pos++)
                    {
                        String id = Integer.toString(pos);

                        if (pos % 2 == 0)
                            style = "tr0";
                        else
                            style = "tr0";
                        tempHash = (Hashtable) hash.get(id);
                        memberId= (String) tempHash.get("memberid");

                        if (functions.isValueNull((String)tempHash.get("notificationUrl"))){
                            notificationUrl=(String)tempHash.get("notificationUrl");
                        }
                        if (functions.isValueNull((String)tempHash.get("termsUrl"))){
                            termsUrl=(String)tempHash.get("termsUrl");
                        }
                        if (functions.isValueNull((String)tempHash.get("privacyPolicyUrl"))){
                            privacyPolicyUrl=(String)tempHash.get("privacyPolicyUrl");
                        }

                    %>
                <input type=hidden name="memberid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberId)%>">

                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=transactionsprocess_Transaction_Configuration%></strong>
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
                                        <thead>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Is_Activation%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Instant_Capture%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Master_Card%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Is_ExcessCaptureAllowed%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Is_POD%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Exp_Date%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Multicurrency%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Autoredirect%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_IP_Validation%></b></td>
                                            <td valign="middle" align="center"
                                                style=" background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Merchant_Order%> </b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b> </b></td>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr>
                                            <td valign="middle" data-label="Is Activation" align="center"
                                                class="<%=style%>">
                                                <select name='activation' class="form-control"style="width: auto" <%----%> onchange="ChangeFunction(this.value,'Is Activation')">
                                                    <%
                                                        if (tempHash.get("activation").equals("T"))
                                                        {
                                                    %>
                                                    <option value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)tempHash.get("activation"))%>"
                                                            selected="selected"><%=ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("activation"))%>
                                                    </option>
                                                    <option value="Y"><%=transactionsprocess_Y%></option>
                                                    <option value="N"><%=transactionsprocess_N%></option>
                                                    <%
                                                    }
                                                    else if (tempHash.get("activation").equals("Y"))
                                                    {
                                                    %>
                                                    <option value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)tempHash.get("activation"))%>"
                                                            selected="selected"><%=ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("activation"))%>
                                                    </option>
                                                    <option value="T"><%=transactionsprocess_T%></option>style="width: auto"
                                                    <option value="N"><%=transactionsprocess_N%></option>
                                                    <%
                                                    }
                                                    else
                                                    {
                                                    %>
                                                    <option value="T"><%=transactionsprocess_T%></option>
                                                    <option value="Y"><%=transactionsprocess_Y%></option>
                                                    <option value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)tempHash.get("activation"))%>"
                                                            selected="selected"><%=ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("activation"))%>
                                                    </option>
                                                    <% }%>
                                                </select>
                                            </td>

                                            <td valign="middle" data-label="Instant Capture" align="center" class="<%=style%>">
                                                <select name='isservice' class="form-control" style="width: auto"<%----%> onchange="ChangeFunction(this.value,'Instant Capture')">
                                                    <%
                                                        if (tempHash.get("isservice").equals("Y"))
                                                        {
                                                    %>
                                                    <option value="Y"><%=transactionsprocess_Y%></option>
                                                    <option value="N"><%=transactionsprocess_N%></option>
                                                    <%
                                                    }
                                                    else
                                                    {
                                                    %>
                                                    <option value="N"><%=transactionsprocess_N%></option>
                                                    <option value="Y"><%=transactionsprocess_Y%></option>
                                                    <% }%>
                                                </select>
                                            </td>

                                            <td valign="middle" data-label="Master Card Supported" align="center"
                                                class="<%=style%>">
                                                <select name='masterCardSupported'
                                                        class="form-control" <%----%> onchange="ChangeFunction(this.value,'Master Card Supported')">
                                                    <%
                                                        if (tempHash.get("masterCardSupported").equals("Y"))
                                                        {
                                                    %>
                                                    <option value="Y"><%=transactionsprocess_Y%></option>
                                                    <option value="N"><%=transactionsprocess_N%></option>
                                                    <%
                                                    }
                                                    else
                                                    {
                                                    %>
                                                    <option value="N"><%=transactionsprocess_N%></option>
                                                    <option value="Y"><%=transactionsprocess_Y%></option>
                                                    <% }%>
                                                </select>
                                            </td>

                                            <%--<td valign="middle" data-label="Is Refund Allowed" align="center" class="<%=style%>">
                                                <select name='isRefundAllowed' class="form-control" &lt;%&ndash;&ndash;%&gt;>
                                                    <%
                                                        if("Y".equals(tempHash.get("isrefund")))
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
                                            </td>--%>
                                            <td valign="middle" data-label="Is ExcessCaptureAllowed" align="center"
                                                class="<%=style%>">
                                                <select name='isExcessCaptureAllowed'
                                                        class="form-control" <%----%> onchange="ChangeFunction(this.value,'Is ExcessCaptureAllowed')">
                                                    <%
                                                        if ("Y".equals(tempHash.get("isExcessCaptureAllowed")))
                                                        {
                                                    %>
                                                    <option value="Y"><%=transactionsprocess_Y%></option>
                                                    <option value="N"><%=transactionsprocess_N%></option>
                                                    <%
                                                    }
                                                    else
                                                    {
                                                    %>
                                                    <option value="N"><%=transactionsprocess_N%></option>
                                                    <option value="Y"><%=transactionsprocess_Y%></option>
                                                    <% }%>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Is POD Reguired" align="center"
                                                class="<%=style%>">
                                                <select name='isPODRequired' class="form-control" &lt;%&ndash;&ndash;%&gt; onchange="ChangeFunction(this.value,'Is POD Required')">
                                                    <%
                                                        if ("Y".equals(tempHash.get("isPODRequired")))
                                                        {
                                                    %>
                                                    <option value="Y"><%=transactionsprocess_Y%></option>
                                                    <option value="N"><%=transactionsprocess_N%></option>
                                                    <%
                                                    }
                                                    else
                                                    {
                                                    %>
                                                    <option value="N"><%=transactionsprocess_N%></option>
                                                    <option value="Y"><%=transactionsprocess_Y%></option>
                                                    <% }%>
                                                </select>
                                            </td>

                                            <td valign="middle" data-label="Exp Date Offset" align="center"
                                                class="<%=style%>" onchange="ChangeFunction(this.value,'Exp Date Offset')">
                                                <input name="expDateOffset" class="form-control"
                                                       value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)tempHash.get("expDateOffset"))%>">
                                            </td>
                                            <td valign="middle" data-label="Multicurrency Supported" align="center"
                                                class="<%=style%>">
                                                <select name='multiCurrencySupport'
                                                        class="form-control" <%----%> onchange="ChangeFunction(this.value,'Multicurrency Supported')">
                                                    <%
                                                        if ("Y".equals(tempHash.get("multiCurrencySupport")))
                                                        {
                                                    %>
                                                    <option value="Y"><%=transactionsprocess_Y%></option>
                                                    <option value="N"><%=transactionsprocess_N%></option>
                                                    <%
                                                    }
                                                    else
                                                    {
                                                    %>
                                                    <option value="N"><%=transactionsprocess_N%></option>
                                                    <option value="Y"><%=transactionsprocess_Y%></option>
                                                    <% }%>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Autoredirect" align="center" class="<%=style%>">
                                                <select name='autoredirect' class="form-control" <%----%> onchange="ChangeFunction(this.value,'Autoredirect')">
                                                    <%
                                                        if ("Y".equals(tempHash.get("autoredirect")))
                                                        {
                                                    %>
                                                    <option value="Y"><%=transactionsprocess_Y%></option>
                                                    <option value="N"><%=transactionsprocess_N%></option>
                                                    <%
                                                    }
                                                    else
                                                    {
                                                    %>
                                                    <option value="N"><%=transactionsprocess_N%></option>
                                                    <option value="Y"><%=transactionsprocess_Y%></option>
                                                    <% }%>
                                                </select>
                                            </td>

                                            <td valign="middle" data-label="IP Validation Required" align="center"
                                                class="<%=style%>" onchange="ChangeFunction(this.value,'IP Validation Required')">

                                                <select name='ip_validation_required' class="form-control">
                                                    <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("ip_validation_required")))); %>
                                                </select>
                                            </td>

                                            <td>
                                                <select name='merchant_order_details' class="form-control" onchange="ChangeFunction(this.value,'Merchant Order Details')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("merchant_order_details")))); %>
                                                </select>
                                            </td>

                                            <td>
                                                <%--<select name='' class="form-control">
                                                    <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)tempHash.get(""))));  %>
                                                </select>--%>
                                            </td>

                                        </tr>
                                        </tbody>
                                    </table>
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 550;width: 100%;">
                                        <thead>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Bin_Routing%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Personal_Info%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Validation%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Rest_Checkout%> </b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Ip_Whitelisted%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_whitelisted%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Blacklist_Transactions%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Notification_Url%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_TermsUrl%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Privacy_Policy%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Emi_Support%> </b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Bin_Service%> </b></td>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr>
                                            <td valign="middle" data-label="Bin Routing" align="center"
                                                class="<%=style%>" onchange="ChangeFunction(this.value,'Bin Routing')">
                                                <select name='binRouting' class="form-control" style="width: auto">
                                                    <%
                                                        out.println(Functions.comboval9(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("binRouting")))); %>
                                                </select>
                                            </td>

                                            <td valign="middle" data-label="Personal Info Display" align="center"
                                                class="<%=style%>" onchange="ChangeFunction(this.value,'Personal Info Display')">

                                                <select name='personal_info_display' class="form-control" style="width: auto">
                                                    <%
                                                        out.println(Functions.comboPersonalInfo(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("personal_info_display")))); %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Personal Info Validation" align="center"
                                                class="<%=style%>" onchange="ChangeFunction(this.value,'Personal Info Validation')">

                                                <select name='personal_info_validation' class="form-control" style="width: auto">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("personal_info_validation")))); %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Rest Checkout Page " align="center"
                                                class="<%=style%>" onchange="ChangeFunction(this.value,'Rest Checkout Page')">

                                                <select name='hosted_payment_page' class="form-control" style="width: auto">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("hosted_payment_page")))); %>
                                                </select>
                                            </td>
                                            <td>
                                                <select name='isIpWhitelisted' class="form-control" onchange="ChangeFunction(this.value,'Is Ip Whitelisted')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("isIpWhitelisted")))); %>
                                                </select>
                                            </td>
                                            <td>
                                                <select name='isIpWhitelistedAPI' class="form-control" onchange="ChangeFunction(this.value,'Is IP Whitelisted For APIs')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("is_rest_whitelisted")))); %>
                                                </select>
                                            </td>
                                            <td>
                                                <select name='blacklistTransaction' class="form-control" onchange="ChangeFunction(this.value,'Blacklist Transactions')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("blacklistTransaction")))); %>
                                                </select>
                                            </td>

                                            <td valign="middle" data-label="Notification Url" align="center"
                                                class="<%=style%>" onchange="ChangeFunction(this.value,'Notification Url')">
                                                <input class="form-control" type="Text" maxlength="255"
                                                       value="<%=ESAPI.encoder().encodeForHTMLAttribute(notificationUrl)%>"
                                                       name="notificationurl" size="255">
                                            </td>
                                            <td valign="middle" data-label="Terms Url" align="center"
                                                class="<%=style%>" onchange="ChangeFunction(this.value,'Terms Url')">
                                                <input class="form-control" type="Text" maxlength="255"
                                                       value="<%=ESAPI.encoder().encodeForHTMLAttribute(termsUrl)%>"
                                                       name="termsurl" size="255">
                                            </td>
                                            <td valign="middle" data-label="Privacy Policy Url" align="center"
                                                class="<%=style%>" onchange="ChangeFunction(this.value,'Privacy Policy Url')">
                                                <input class="form-control" type="Text" maxlength="255"
                                                       value="<%=ESAPI.encoder().encodeForHTMLAttribute(privacyPolicyUrl)%>"
                                                       name="privacyPolicyUrl" size="255">
                                            </td>

                                            <td>
                                                <select name='emiSupport' style="width: auto" class="form-control" onchange="ChangeFunction(this.value,'Emi Support')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("emiSupport")))); %>
                                                </select>
                                            </td>
                                            <td>
                                                <select name='binservice' class="form-control" style="width: auto" onchange="ChangeFunction(this.value,'Bin Service')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("binservice")))); %>
                                                </select>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>

                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <thead>
                                        <tr>

                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b>isMerchantLogoBO</b></td>

                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Market_Place%> </b></td>
                                            <%--<td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_Cvv_Store%></b></td>--%>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_isUniqueOrderIdRequired%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b>Auto Select Terminal</b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=transactionsprocess_isDomainWhitelisted%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b>Is OTPRequired</b>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b>Is CardStorageRequired</b>
                                            </td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b>Payout Personal Info Validation</b>
                                            </td>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr>

                                            <td valign="middle" data-label="isMerchantLogoBO" align="center"
                                                class="<%=style%>">
                                                <select name="isMerchantLogoBO"  style="width: auto" class="form-control"
                                                        onchange="ChangeFunction(this.value, 'isMerchantLogoBO')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(functions.isValueNull((String) tempHash.get("isMerchantLogoBO")) ? (String) tempHash.get("isMerchantLogoBO") : "N"))); %>

                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Market Place" align="center"
                                                class="<%=style%>" >
                                                <select name='marketplace' class="form-control" style="width: auto" onchange="ChangeFunction(this.value,'Market Place')">
                                                    <%
                                                        out.println(Functions.comboval4(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("marketplace")))); %>
                                                </select>
                                            </td>
                                            <%-- <td valign="middle" data-label="Market Place" align="center"
                                                 class="<%=style%>" onchange="ChangeFunction(this.value,'Is Cvv Store')">
                                                 <select name='isCvvStore' class="form-control" style="width: auto">
                                                     <%
                                                         out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("isCvvStore")))); %>
                                                 </select>
                                             </td>--%>
                                            <td valign="middle" data-label="Market Place" align="center"
                                                class="<%=style%>" >
                                                <select name='isUniqueOrderIdRequired' class="form-control" style="width: auto" onchange="ChangeFunction(this.value,'Is Unique OrderId Required')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("isUniqueOrderIdRequired")))); %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Market Place" align="center"
                                                class="<%=style%>" >
                                                <select name='autoSelectTerminal' class="form-control" style="width: auto" onchange="ChangeFunction(this.value,'Auto Select Terminal')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("autoSelectTerminal")))); %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Market Place" align="center"
                                                class="<%=style%>" >
                                                <select name='isDomainWhitelisted' class="form-control" style="width: auto" onchange="ChangeFunction(this.value,'Is Domain Whitelisted')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("isDomainWhitelisted")))); %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Is OTPRequired" align="center" class="<%=style%>">
                                                <select name="isOTPRequired" class="form-control" style="width: auto" onchange="ChangeFunction(this.value,'Is OTPRequired')">
                                                    <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)tempHash.get("isOTPRequired"))));%>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Is CardStorageRequired" align="center" class="<%=style%>">
                                                <select name="isCardStorageRequired" class="form-control" style="width: auto" onchange="ChangeFunction(this.value,'Is CardStorageRequired')">
                                                    <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)tempHash.get("isCardStorageRequired"))));%>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Payout Personal Info Validation" align="center" class="<%=style%>">
                                                <select name="payoutPersonalInfoValidation" class="form-control" style="width: auto" onchange="ChangeFunction(this.value,'Payout Personal Info Validation')">
                                                    <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)tempHash.get("payoutPersonalInfoValidation"))));%>
                                                </select>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
        </div>

        <div class="row reporttable">
            <div class="col-md-12">
                <div class="widget">
                    <div class="widget-header">
                        <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=transactionsprocess_Refund_Configuration%></strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <center>
                        <div class="widget-content padding" style="overflow-x: auto;">
                            <table align=center width="30%"
                                   class="display table table table-striped table-bordered table-hover dataTable"
                                   style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 60%;">
                                <thead>
                                <tr>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Refund_Allowed%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Multiple_Refund%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Daily_Refund%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Refund_Allowed1%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Partial_Refund%> </b></td>


                                </tr>
                                </thead>

                                <tbody>
                                <tr>
                                    <td valign="middle" data-label="Is Refund Allowed" align="center"
                                        class="<%=style%>">
                                        <select name='isRefundAllowed' class="form-control"
                                                &lt;%&ndash;<%----%>&ndash;%&gt; onchange="ChangeFunction(this.value,'Is Refund Allowed')">
                                            <%
                                                if ("Y".equals(tempHash.get("isrefund")))
                                                {
                                            %>
                                            <option value="Y"><%=transactionsprocess_Y%></option>
                                            <option value="N"><%=transactionsprocess_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N"><%=transactionsprocess_N%></option>
                                            <option value="Y"><%=transactionsprocess_Y%></option>
                                            <% }%>
                                        </select>
                                    </td>
                                    <td valign="middle" data-label="Is Multiple Refund" align="center"
                                        class="<%=style%>" >
                                        <select name='isMultipleRefund' class="form-control"
                                                &lt;%&ndash;<%----%>&ndash;%&gt; onchange="ChangeFunction(this.value,'Is Multiple Refund')">
                                            <%
                                                if ("Y".equals(tempHash.get("isMultipleRefund")))
                                                {
                                            %>
                                            <option value="Y"><%=transactionsprocess_Y%></option>
                                            <option value="N"><%=transactionsprocess_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N"><%=transactionsprocess_N%></option>
                                            <option value="Y"><%=transactionsprocess_Y%></option>
                                            <% }%>
                                        </select>
                                    </td>
                                    <td valign="middle" data-label="Daily Refund Limit" align="center"
                                        class="<%=style%>" onchange="ChangeFunction(this.value,'Daily Refund Limit')">
                                        <input name="dailyRefundLimit" class="form-control"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)tempHash.get("refunddailylimit"))%>">
                                    <td valign="middle" data-label="Refund Allowed Days" align="center"
                                        class="<%=style%>" onchange="ChangeFunction(this.value,'Refund Allowed Days')">
                                        <input name="refundAllowedDays" class="form-control"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)tempHash.get("refundallowed_days"))%>">
                                        <!-- isPartialRefund -->
                                    <td valign="middle" data-label="Is Refund Allowed" align="center"
                                        class="<%=style%>" >
                                        <select name='isPartialRefund' class="form-control"
                                                &lt;%&ndash;<%----%>&ndash;%&gt; onchange="ChangeFunction(this.value,'Is Partial Refund')">
                                            <%
                                                if ("Y".equals(tempHash.get("isPartialRefund")))
                                                {
                                            %>
                                            <option value="Y"><%=transactionsprocess_Y%></option>
                                            <option value="N"><%=transactionsprocess_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N"><%=transactionsprocess_N%></option>
                                            <option value="Y"><%=transactionsprocess_Y%></option>
                                            <% }%>
                                        </select>
                                    </td>

                                </tr>
                                </tbody>
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
                        <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=transactionsprocess_SMS_Configuration%></strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <center>
                        <div class="widget-content padding" style="overflow-x: auto;">
                            <table align=center width="30%"
                                   class="display table table table-striped table-bordered table-hover dataTable"
                                   style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 60%;">
                                <thead>
                                <tr>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_SMS_Activation%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Customer_Activation%> </b></td>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td valign="middle" data-label="SMS Activation" align="center"
                                        class="<%=style%>">
                                        <select name='smsactivation' class="form-control" onchange="ChangeFunction(this.value,'SMS Activation')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("smsactivation")))); %>
                                        </select>
                                    </td>
                                    <td valign="middle" data-label="Customer Activation" align="center"
                                        class="<%=style%>">
                                        <select name='customersmsactivation' class="form-control" onchange="ChangeFunction(this.value,'Customer Activation')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("customersmsactivation")))); %>
                                        </select>
                                    </td>
                                </tr>
                                </tbody>
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
                            <table align=center width="30%"
                                   class="display table table table-striped table-bordered table-hover dataTable"
                                   style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 60%;">
                                <thead>
                                <tr>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b>Is IP Whitelist for Invoice</b></td>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td valign="middle" data-label="Is IP whitelist for Invoice" align="center"
                                        class="<%=style%>">
                                        <select name='ip_whitelist_invoice' class="form-control" onchange="ChangeFunction(this.value,'Is IP whitelist for Invoice')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("ip_whitelist_invoice")))); %>
                                        </select>
                                    </td>
                                </tr>
                                </tbody>
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
                        <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=transactionsprocess_Token_Configuration%></strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <center>
                        <div class="widget-content padding" style="overflow-x: auto;">
                            <table align=center width="20%"
                                   class="display table table table-striped table-bordered table-hover dataTable"
                                   style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 60%;">
                                <thead>
                                <tr>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_TokenizationAllowed%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Card_Encryption%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Is_AddressDetails%></b></td>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td valign="middle" data-label="Is TokenizationAllowed" align="center"
                                        class="<%=style%>">

                                        <select name='isTokenizationAllowed' class="form-control" onchange="ChangeFunction(this.value,'Is TokenizationAllowed')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("isTokenizationAllowed")))); %>
                                        </select>
                                    </td>
                                    <td valign="middle" data-label="Is Card Encryption Enable" align="center"
                                        class="<%=style%>" >
                                        <select name='isCardEncryptionEnable' class="form-control" onchange="ChangeFunction(this.value,'Is Card Encryption Enable')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("isCardEncryptionEnable")))); %>
                                        </select>
                                    </td>
                                    <td valign="middle" data-label="Is AddressDetails Required" align="center"
                                        class="<%=style%>">
                                        <select name='isAddrDetailsRequired' class="form-control" onchange="ChangeFunction(this.value,'Is AddressDetails Required')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("addressDetails")))); %>
                                        </select>
                                    </td>
                                </tr>
                                </tbody>
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
                        <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Invoizer Configuration
                        </strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <center>
                        <div class="widget-content padding" style="overflow-x: auto;">
                            <table align=center width="100%"
                                   class="display table table table-striped table-bordered table-hover dataTable"
                                   style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>
                                <tr>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Virtual_Checkout1%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Phone_Required%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Email_Required%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b>Is Share Allowed</b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b>Is Signature Allowed</b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b>Is Save Receipt Allowed</b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;">
                                        <b>Default Language</b></td>
                                </tr>
                                </thead>

                                <tbody>
                                <tr>
                                    <td valign="middle" data-label="Is Virtual Checkout Allowed" align="center"
                                        class="<%=style%>">

                                        <select name='isVirtualCheckoutAllowed' class="form-control" onchange="ChangeFunction(this.value,'Is Virtual Checkout Allowed')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("isVirtualCheckoutAllowed")))); %>
                                        </select>
                                    </td>

                                    <td valign="middle" data-label="Is Mobile Required" align="center"
                                        class="<%=style%>" >
                                        <select name='isMobileAllowedForVC' class="form-control" onchange="ChangeFunction(this.value,'Is Mobile Required')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("isMobileAllowedForVC")))); %>
                                        </select>
                                    </td>
                                    <td valign="middle" data-label="Is Email Required" align="center"
                                        class="<%=style%>">
                                        <select name='isEmailAllowedForVC' class="form-control" onchange="ChangeFunction(this.value,'Is Email Required')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("isEmailAllowedForVC")))); %>
                                        </select>
                                    </td>
                                    <td valign="middle" data-label="Is Share Allowed" align="center"
                                        class="<%=style%>">
                                        <select name='isShareAllowed' class="form-control" onchange="ChangeFunction(this.value,'Is Share Allowed')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("isShareAllowed")))); %>
                                        </select>
                                    </td>
                                    <td valign="middle" data-label="Is Signature Allowed" align="center"
                                        class="<%=style%>">
                                        <select name='isSignatureAllowed' class="form-control" onchange="ChangeFunction(this.value,'Is Signature Allowed')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("isSignatureAllowed")))); %>
                                        </select>
                                    </td>
                                    <td valign="middle" data-label="Is Save Receipt Allowed" align="center"
                                        class="<%=style%>">
                                        <select name='isSaveReceiptAllowed' class="form-control" onchange="ChangeFunction(this.value,'Is Save Receipt Allowed')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("isSaveReceiptAllowed")))); %>
                                        </select>
                                    </td>
                                    <td valign="middle" data-label="Default Language" align="center"
                                        class="<%=style%>">
                                        <select name='defaultLanguage' class="form-control" onchange="ChangeFunction(this.value,'Default Language')">
                                            <%
                                                out.println(Functions.combovalLang(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("defaultLanguage")))); %>
                                        </select>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </center>
                </div>
            </div>
        </div>
        <%--<div class="row reporttable">
            <div class="col-md-12">
                <div class="widget">
                    <div class="widget-header">
                        <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=transactionsprocess_checkout%></strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <%
                        PartnerModuleManager partnerModuleManager = new PartnerModuleManager();
                        String Hide = "true";
                        String superAdminId = partnerModuleManager.getSuperAdminId(String.valueOf(session.getAttribute("merchantid")));

                    %>
                    <center>
                        <div class="widget-content padding" style="overflow-x: auto;">
                            <table align=center width="50%"
                                   class="display table table table-striped table-bordered table-hover dataTable"
                                   style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 80%;">
                                <thead>
                                <tr>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Is_Pharma%></b></td>
                                    &lt;%&ndash;<%
                                        if(superAdminId.equals("0") || superAdminId.equals("1")){

                                    %>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Powered_Logo%></b></td>
                                    <%
                                        }
                                    %>&ndash;%&gt;&ndash;%&gt;
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Template%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Merchant_Logo%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Security_Logo%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_PCI_Logo%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Partner_Logo%></b></td>
                                    &lt;%&ndash;<td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b></b></td>&ndash;%&gt;
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td valign="middle" data-label="Is Pharma(Y/N)" align="center"
                                        class="<%=style%>">
                                        <select name='isPharma' class="form-control" onchange="ChangeFunction(this.value,'Is Pharma(Y/N)')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("isPharma")))); %>
                                        </select>
                                    </td>
                                    &lt;%&ndash; <%
                                         if(superAdminId.equals("0") || superAdminId.equals("1")){

                                     %>
                                     <td valign="middle" data-label="Is Powered By Logo" align="center"
                                         class="<%=style%>">
                                         <select name='isPoweredBy' class="form-control">
                                             <%
                                                 out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("isPoweredBy")))); %>
                                         </select>
                                     </td>
                                     <%
                                         }
                                     %>&ndash;%&gt;
                                    <td valign="middle" data-label="Template" align="center" class="<%=style%>">
                                        <select name='template' class="form-control" onchange="ChangeFunction(this.value,'Template')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("template")))); %>
                                        </select>
                                    </td>

                                    <td valign="middle" data-label="Is Merchant Logo" align="center"
                                        class="<%=style%>">
                                        <select name='ismerchantlogo' class="form-control" onchange="ChangeFunction(this.value,'Is Merchant Logo')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(functions.isValueNull((String) tempHash.get("ismerchantlogo")) ? (String) tempHash.get("ismerchantlogo") : "N"))); %>
                                        </select>
                                    </td>
                                    <td valign="middle" data-label="Template" align="center" class="<%=style%>">
                                        <select name='isSecurityLogo' class="form-control" onchange="ChangeFunction(this.value,'Is Security Logo')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(functions.isValueNull((String) tempHash.get("isSecurityLogo")) ? (String) tempHash.get("isSecurityLogo") : "N"))); %>
                                        </select>
                                    </td>
                                    <td valign="middle" data-label="PCI Logo" align="center" class="<%=style%>" onchange="ChangeFunction(this.value,'PCI Logo')">
                                        <select name='ispcilogo' class="form-control" &lt;%&ndash;&ndash;%&gt;>
                                            <%
                                                if ("Y".equals(tempHash.get("ispcilogo")))
                                                {
                                            %>
                                            <option value="Y"><%=transactionsprocess_Y%></option>
                                            <option value="N"><%=transactionsprocess_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N"><%=transactionsprocess_N%></option>
                                            <option value="Y"><%=transactionsprocess_Y%></option>
                                            <% }%>
                                        </select>
                                    </td>
                                    <td valign="middle" data-label="Partner Logo" align="center"
                                        class="<%=style%>" onchange="ChangeFunction(this.value,'Partner Logo')">
                                        <select name='ispartnerlogo'
                                                class="form-control" &lt;%&ndash;&ndash;%&gt;>
                                            <%
                                                if ("Y".equals(tempHash.get("ispartnerlogo")))
                                                {
                                            %>
                                            <option value="Y"><%=transactionsprocess_Y%></option>
                                            <option value="N"><%=transactionsprocess_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N"><%=transactionsprocess_N%></option>
                                            <option value="Y"><%=transactionsprocess_Y%></option>
                                            <% }%>
                                        </select>
                                    </td>
                                </tr>
                                </tbody>
                                <thead>
                                <tr>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Visa_Secure%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><label>isMerchantLogoBO</label></b>
                                    </td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_MC_Secure%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Consent%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Checkout_Timer%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Checkout_Timer_Time%></b></td>
                                    &lt;%&ndash; <td valign="middle" align="center"
                                         style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                         <b>&nbsp</b></td>&ndash;%&gt;
                                </tr>
                                </thead>
                                <tr>


                                    <td valign="middle" data-label="Visa Secure Logo" align="center"
                                        class="<%=style%>">
                                        <select name='vbvLogo' class="form-control" onchange="ChangeFunction(this.value,'Visa Secure Logo')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(functions.isValueNull((String) tempHash.get("vbvLogo")) ? (String) tempHash.get("vbvLogo") : "N"))); %>
                                            *
                                        </select>
                                    </td>
                                    <td valign="middle" data-label="isMerchantLogoBO" align="center"
                                        class="<%=style%>">
                                        <select name="isMerchantLogoBO" class="form-control" onchange="ChangeFunction(this.value, 'isMerchantLogoBO')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(functions.isValueNull((String) tempHash.get("isMerchantLogoBO")) ? (String) tempHash.get("isMerchantLogoBO") : "N"))); %>

                                        </select>
                                    </td>
                                    <td valign="middle" data-label="Master Secure Logo" align="center"
                                        class="<%=style%>">
                                        <select name='masterSecureLogo' class="form-control" onchange="ChangeFunction(this.value,'Master Secure Logo')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(functions.isValueNull((String) tempHash.get("masterSecureLogo")) ? (String) tempHash.get("masterSecureLogo") : "N"))); %>
                                        </select>
                                    </td>
                                    <td valign="middle" data-label="Consent" align="center" class="<%=style%>">
                                        <select name='consent' class="form-control" onchange="ChangeFunction(this.value,'Consent')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute(functions.isValueNull((String) tempHash.get("consent")) ? (String) tempHash.get("consent") : "Y"))); %>
                                        </select>
                                    </td>
                                    <td valign="middle" data-label="Checkout Timer" align="center" class="<%=style%>" >
                                        <select name='checkoutTimer' class="form-control" &lt;%&ndash;&ndash;%&gt; onchange="ChangeFunction(this.value,'Checkout Timer')">
                                            <%
                                                if ("Y".equals(tempHash.get("checkoutTimer")))
                                                {
                                            %>
                                            <option value="Y"><%=transactionsprocess_Y%></option>
                                            <option value="N"><%=transactionsprocess_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N"><%=transactionsprocess_N%></option>
                                            <option value="Y"><%=transactionsprocess_Y%></option>
                                            <% }%>
                                        </select>
                                    </td>
                                    <td valign="middle" data-label="Checkout Timer Time" align="center" class="<%=style%>">
                                        <input class="form-control" type="Text" placeholder="00:00" maxlength="5" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)tempHash.get("checkoutTimerTime"))%>" name="checkoutTimerTime" size="50" onchange="ChangeFunction(this.value,'Checkout Timer Time')">
                                    </td>
                                </tr>
                                &lt;%&ndash;
                                  <thead>
                                  <tr>
                                      <td valign="middle" align="center"
                                          style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                          <b>&nbsp</b></td>
                                      <td valign="middle" align="center"
                                          style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                          <b>&nbsp</b></td>
                                      <td valign="middle" align="center"
                                          style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                          <b>&nbsp</b></td>
                                  </tr>
                                  </thead>

                                  <tbody>
                                  <tr>
                                  </tr>
                                  </tbody>  &ndash;%&gt;
                            </table>
                        </div>
                    </center>
                </div>
            </div>
        </div>--%>
       <%-- <%

            String partner1= partnerFunctions.getPartnerId(request.getParameter("memberid"));
            String oldcheckout=partnerFunctions.getOldCheckFlag(partner1);
            if(oldcheckout.equals("Y")){
        %>--%>
      <%--  <div class="row reporttable">
            <div class="col-md-12">
                <div class="widget">
                    <div class="widget-header">
                        <h2><b class="fa fa-table"></b>&nbsp;&nbsp;<strong><%=transactionsprocess_Checkout_Template%></strong>&lt;%&ndash;(Old checkout page)&ndash;%&gt;
                        </h2>
                        <h2><%=transactionsprocess_used%></h2>


                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>

                    &nbsp;
                    <center>
                        <div class="widget-content padding" style="overflow-x: auto;">
                            <table align=center width="50%"
                                   class="display table table table-striped table-bordered table-hover dataTable"
                                   style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>
                                <tr>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Template1%>&nbsp;<%=transactionsprocess_BackGround%>&nbsp;<%=transactionsprocess_Color%>&nbsp;</b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Panel%>&nbsp;<%=transactionsprocess_Heading%>&nbsp;<%=transactionsprocess_Color%>&nbsp;</b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Left%>&nbsp;<%=transactionsprocess_Navigation%>&nbsp;<%=transactionsprocess_Color%>&nbsp;</b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Label%>&nbsp;<%=transactionsprocess_Font%>&nbsp;<%=transactionsprocess_Color%>&nbsp;</b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Heading%>&nbsp;<%=transactionsprocess_Label%>&nbsp;<%=transactionsprocess_Font%>&nbsp;<%=transactionsprocess_Color%>&nbsp;</b></td>
                                </tr>
                                </thead>

                                <tbody>
                                <tr>
                                    <td valign="middle" data-label="Template BackGround Color" align="center"
                                        class="<%=style%>">
                                        <input type="text" class="form-control"
                                               name='<%=TemplatePreference.MAINBACKGROUNDCOLOR.toString()%>'
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.MAINBACKGROUNDCOLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.MAINBACKGROUNDCOLOR.toString()):""%>"/>
                                    </td>
                                    <td valign="middle" data-label="Panel Heading Color" align="center"
                                        class="<%=style%>">
                                        <input name='<%=TemplatePreference.PANELHEADING_COLOR.toString()%>'
                                               class="form-control"
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.PANELHEADING_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.PANELHEADING_COLOR.toString()):""%>"/>
                                    </td>
                                    <td valign="middle" data-label="Panel Body Color" align="center"
                                        class="<%=style%>">
                                        <input name='<%=TemplatePreference.PANELBODY_COLOR.toString()%>'
                                               class="form-control"
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.PANELBODY_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.PANELBODY_COLOR.toString()):""%>"/>
                                    </td>
                                    <td valign="middle" data-label="Heading Font BackGround Color"
                                        align="center" class="<%=style%>">
                                        <input name='<%=TemplatePreference.HEADPANELFONT_COLOR.toString()%>'
                                               class="form-control"
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.HEADPANELFONT_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.HEADPANELFONT_COLOR.toString()):""%>"/>
                                    </td>
                                    <td valign="middle" data-label="Body Font BackGround Color" align="center"
                                        class="<%=style%>">
                                        <input name='<%=TemplatePreference.BODYPANELFONT_COLOR.toString()%>'
                                               class="form-control"
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.BODYPANELFONT_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.BODYPANELFONT_COLOR.toString()):""%>"/>
                                    </td>
                                </tr>
                                </tbody>

                                <thead>
                                <tr>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Body%>&nbsp;<%=transactionsprocess_BackGround%>&nbsp;<%=transactionsprocess_Color%>&nbsp;</b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Body%>&nbsp;<%=transactionsprocess_ForeGround%>&nbsp;<%=transactionsprocess_Color%>&nbsp;</b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Navigation%>&nbsp;<%=transactionsprocess_Font%>&nbsp;<%=transactionsprocess_Color%>&nbsp;</b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Textbox%>&nbsp;<%=transactionsprocess_Color%>&nbsp;</b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Icon%>&nbsp;<%=transactionsprocess_Vector%>&nbsp;<%=transactionsprocess_Color%>&nbsp;</b></td>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td valign="middle" data-label="Body BackGround Color" align="center"
                                        class="<%=style%>">
                                        <input type="text" class="form-control"
                                               name='<%=TemplatePreference.BODY_BACKGROUND_COLOR.toString()%>'
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.BODY_BACKGROUND_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.BODY_BACKGROUND_COLOR.toString()):""%>"/>
                                    </td>

                                    <td valign="middle" data-label="Body ForeGround Color" align="center"
                                        class="<%=style%>">
                                        <input name='<%=TemplatePreference.BODY_FOREGROUND_COLOR.toString()%>'
                                               class="form-control"
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.BODY_FOREGROUND_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.BODY_FOREGROUND_COLOR.toString()):""%>"/>
                                    </td>

                                    <td valign="middle" data-label="Navigation Font Color" align="center"
                                        class="<%=style%>">
                                        <input name='<%=TemplatePreference.NAVIGATION_FONT_COLOR.toString()%>'
                                               class="form-control"
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NAVIGATION_FONT_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NAVIGATION_FONT_COLOR.toString()):""%>"/>
                                    </td>

                                    <td valign="middle" data-label="Textbox Color" align="center"
                                        class="<%=style%>">
                                        <input name='<%=TemplatePreference.TEXTBOX_COLOR.toString()%>'
                                               class="form-control"
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.TEXTBOX_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.TEXTBOX_COLOR.toString()):""%>"/>
                                    </td>

                                    <td valign="middle" data-label="Icon Vector Color" align="center"
                                        class="<%=style%>">
                                        <input name='<%=TemplatePreference.ICON_VECTOR_COLOR.toString()%>'
                                               class="form-control"
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.ICON_VECTOR_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.ICON_VECTOR_COLOR.toString()):""%>"/>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                    </center>
                </div>
            </div>
        </div>
        <%
            }
        %>--%>
        <%--new checkout template colors--%>
        <%--<div class="row reporttable">
            <div class="col-md-12">
                <div class="widget">
                    <div class="widget-header">
                        <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=transactionsprocess_new%></strong>
                        </h2>
                        <h2><%=transactionsprocess_used1%></h2>



                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    &nbsp;
                    <center>
                        <div class="widget-content padding" style="overflow-x: auto;">
                            <table align=center width="50%"
                                   class="display table table table-striped table-bordered table-hover dataTable"
                                   style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead>
                                <tr>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Body%>&nbsp;&&nbsp;<%=transactionsprocess_Footer%>&nbsp;<%=transactionsprocess_Color%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Header%>&nbsp;<%=transactionsprocess_BackGround%>&nbsp;<%=transactionsprocess_Color%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Navigation%>&nbsp;<%=transactionsprocess_Bar%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Button%>&nbsp;<%=transactionsprocess_Font%>&nbsp;<%=transactionsprocess_Color%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Header%>&nbsp;<%=transactionsprocess_Font%>&nbsp;<%=transactionsprocess_Color%></b></td>
                                </tr>
                                </thead>

                                <tbody>
                                <tr>
                                    <td valign="middle" data-label="Body & Footer Color" align="center"
                                        class="<%=style%>">
                                        <span class="input-icon">
                                            <input type="color"
                                                   style="position: absolute;width: 26px;height: 25px;margin-left: 78px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()):"#4d95ac"%>"></span>
                                        <input type="text" class="form-control"
                                               id='<%=TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()%>'
                                               name='<%=TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()%>'
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BODYNFOOTER_COLOR.toString()):""%>"/>

                                    </td>
                                    <td valign="middle" data-label="Header Background Color" align="center"
                                        class="<%=style%>">
                                      <span class="input-icon">
                                            <input type="color"
                                                   style="position: absolute;width: 26px;height: 25px;margin-left: 78px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()):"#4d95ac"%>"></span>
                                        <input id='<%=TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()%>'
                                               name='<%=TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()%>'
                                               class="form-control"
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_HEADERBACKGROUND_COLOR.toString()):""%>"/>
                                    </td>
                                    <td valign="middle" data-label="Navigation Bar" align="center"
                                        class="<%=style%>">
                                        <span class="input-icon">
                                            <input type="color"
                                                   style="position: absolute;width: 26px;height: 25px;margin-left: 78px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()):"#4d95ac"%>"></span>
                                        <input id='<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()%>'
                                               name='<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()%>'
                                               class="form-control"
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_COLOR.toString()):""%>"/>
                                    </td>
                                    <td valign="middle" data-label="Button Font Color" align="center"
                                        class="<%=style%>">
                                        <span class="input-icon">
                                            <input type="color"
                                                   style="position: absolute;width: 26px;height: 25px;margin-left: 78px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()):"#4d95ac"%>"></span>
                                        <input id='<%=TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()%>'
                                               name='<%=TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()%>'
                                               class="form-control"
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BUTTON_FONT_COLOR.toString()):""%>"/>
                                    </td>
                                    <td valign="middle" data-label="Header Font Color" align="center"
                                        class="<%=style%>">
                                        <span class="input-icon">
                                            <input type="color"
                                                   style="position: absolute;width: 26px;height: 25px;margin-left: 78px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()):"#4d95ac"%>"></span>
                                        <input id='<%=TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()%>'
                                               name='<%=TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()%>'
                                               class="form-control"
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_HEADER_FONT_COLOR.toString()):""%>"/>
                                    </td>
                                </tr>
                                </tbody>

                                <tbody>
                                <thead>
                                <tr>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Full%>&nbsp;<%=transactionsprocess_Background%>&nbsp;<%=transactionsprocess_Color%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Label%>&nbsp;<%=transactionsprocess_Font%>&nbsp;<%=transactionsprocess_Color%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Navigation%>&nbsp;<%=transactionsprocess_Bar%>&nbsp;<%=transactionsprocess_Font%>&nbsp;<%=transactionsprocess_Color%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Button%>&nbsp;<%=transactionsprocess_Color%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Icon%>&nbsp;<%=transactionsprocess_Color%></b></td>
                                </tr>
                                </thead>
                                </tbody>
                                <tbody>
                                <tr>
                                    <td valign="middle" data-label="Full Background Color" align="center"
                                        class="<%=style%>">
                                        <span class="input-icon">
                                            <input type="color"
                                                   style="position: absolute;width: 26px;height: 25px;margin-left: 78px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()):"#4d95ac"%>"></span>
                                        <input type="text" class="form-control"
                                               id='<%=TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()%>'
                                               name='<%=TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()%>'
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_FULLBACKGROUND_COLOR.toString()):""%>"/>
                                    </td>

                                    <td valign="middle" data-label="Label Font Color" align="center"
                                        class="<%=style%>">
                                        <span class="input-icon">
                                            <input type="color"
                                                   style="position: absolute;width: 26px;height: 25px;margin-left: 78px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()):"#4d95ac"%>"></span>
                                        <input id='<%=TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()%>'
                                               name='<%=TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()%>'
                                               class="form-control"
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_LABEL_FONT_COLOR.toString()):""%>"/>
                                    </td>

                                    <td valign="middle" data-label="Navigation Bar Font Color" align="center"
                                        class="<%=style%>">
                                        <span class="input-icon">
                                            <input type="color"
                                                   style="position: absolute;width: 26px;height: 25px;margin-left: 78px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()):"#4d95ac"%>"></span>
                                        <input id='<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()%>'
                                               name='<%=TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()%>'
                                               class="form-control"
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR.toString()):""%>"/>
                                    </td>

                                    <td valign="middle" data-label="Button Color" align="center"
                                        class="<%=style%>">
                                        <span class="input-icon">
                                            <input type="color"
                                                   style="position: absolute;width: 26px;height: 25px;margin-left: 78px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()):"#4d95ac"%>"></span>
                                        <input id='<%=TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()%>'
                                               name='<%=TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()%>'
                                               class="form-control"
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BUTTON_COLOR.toString()):""%>"/>
                                    </td>

                                    <td valign="middle" data-label="Icon Color" align="center"
                                        class="<%=style%>">
                                        <span class="input-icon">
                                            <input type="color"
                                                   style="position: absolute;width: 26px;height: 25px;margin-left: 78px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()):"#4d95ac"%>"></span>
                                        <input id='<%=TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()%>'
                                               name='<%=TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()%>'
                                               class="form-control"
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_ICON_COLOR.toString()):""%>"/>
                                    </td>
                                </tr>
                                </tbody>
                                <tbody>
                                <thead>
                                <tr>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Timer%>&nbsp;<%=transactionsprocess_Color%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Box%>&nbsp;<%=transactionsprocess_Shadow%></b></td>
                                </tr>
                                </thead>
                                </tbody>
                                <tbody>
                                <tr>
                                    <td valign="middle" data-label="Timer Color" align="center"
                                        class="<%=style%>">
                                        <span class="input-icon">
                                            <input type="color"
                                                   style="position: absolute;width: 26px;height: 25px;margin-left: 78px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()).toString()) )?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()):"#4d95ac"%>"></span>
                                        <input type="text" class="form-control"
                                               id='<%=TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()%>'
                                               name='<%=TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()%>'
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_TIMER_COLOR.toString()):""%>"/>
                                    </td>

                                    <td valign="middle" data-label="Label Font Color" align="center"
                                        class="<%=style%>">
                                        <span class="input-icon">
                                            <input type="color"
                                                   style="position: absolute;width: 26px;height: 25px;margin-left: 78px;"
                                                   id="<%=TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()%>_picker"
                                                   onchange="colorPicker('<%=TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()%>_picker','<%=TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()%>');"
                                                   value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()) && functions.isValueNull(merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()).toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()):"#4d95ac"%>"></span>
                                        <input id='<%=TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()%>'
                                               name='<%=TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()%>'
                                               class="form-control"
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()))?merchantTemplateSetting.get(TemplatePreference.NEW_CHECKOUT_BOX_SHADOW.toString()):""%>"/>
                                    </td>
                            </table>
                        </div>
                    </center>
                </div>
            </div>
        </div>--%>
        <%--end of new checkout template colors--%>

        <div class="row reporttable" style="margin-bottom: 9px;">
            <div class="col-md-12">
                <div class="widget">
                    <div class="widget-header">
                        <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=transactionsprocess_Flight_Mode%></strong></h2>

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
                                <tr class="th0">
                                    <td colspan="4" valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <center><b><%=transactionsprocess_Flight_Mode%></b></center>
                                    </td>
                                </tr>
                                </thead>
                                <tbody>
                                <tr>
                                    <td style="height: 30px" valign="middle" align="center" class="tr1">
                                        <%=transactionsprocess_FlightMode%>
                                    </td>
                                </tr>
                                <tr>
                                    <td align="center" data-label="Flight Mode" class="<%=style%>" onchange="ChangeFunction(this.value,'Flight Mode')">
                                        <select name='flightMode' class="form-control" id="flightid" onchange="hidblock(flightid)">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("flightMode")))); %>
                                        </select>
                                    </td>
                                </tr>
                                </tbody>
                            </table>
                        </div>

                    </center>
                </div>
            </div>
        </div>
        <div class="row reporttable" id="div_split">
            <div class="col-md-12">
                <div class="widget">
                    <div class="widget-header">
                        <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=transactionsprocess_Split_Transaction%>
                        </strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <center>
                        <div class="widget-content padding" style="overflow-x: auto;">
                            <table align=center width="30%"
                                   class="display table table table-striped table-bordered table-hover dataTable"
                                   style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 60%;">
                                <thead>
                                <tr>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Split_payment%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Split_type%></b></td>
                                </tr>
                                </thead>

                                <tbody>
                                <tr>
                                    <td valign="middle" data-label="Split Payment Allowed" align="center"
                                        class="<%=style%>" >

                                        <select name='isSplitPayment' class="form-control" onchange="ChangeFunction(this.value,'Split Payment Allowed')">
                                            <%
                                                out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("isSplitPayment")))); %>
                                        </select>
                                    </td>
                                    <td valign="middle" data-label="Split Payment Type" align="center"
                                        class="<%=style%>" >
                                        <select name='splitPaymentType' class="form-control" onchange="ChangeFunction(this.value,'Split Payment Type')">
                                            <%
                                                out.println(Functions.combovalForSplitPayment(ESAPI.encoder().encodeForHTMLAttribute((String) tempHash.get("splitPaymentType")))); %>
                                        </select>
                                    </td>
                                </tr>
                                </tbody>
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
                        <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=transactionsprocess_mail%></strong></h2>

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
                                        <b><%=transactionsprocess_Template1%>&nbsp;<%=transactionsprocess_BackGround%>&nbsp;<%=transactionsprocess_Color%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Panel%>&nbsp;<%=transactionsprocess_Heading%>&nbsp;<%=transactionsprocess_Color%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Label%>&nbsp;<%=transactionsprocess_Font%>&nbsp;<%=transactionsprocess_Color%></b></td>
                                    <td valign="middle" align="center"
                                        style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                        <b><%=transactionsprocess_Table%>&nbsp;<%=transactionsprocess_Color%></b></td>
                                </tr>
                                </thead>

                                <tbody>
                                <tr>
                                    <td valign="middle" data-label="Template BackGround Color" align="center"
                                        class="<%=style%>">

                                        <input type="text" class="form-control" id='<%=TemplatePreference. MAILBACKGROUNDCOLOR.toString()%>'
                                               name='<%=TemplatePreference. MAILBACKGROUNDCOLOR.toString()%>'
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference. MAILBACKGROUNDCOLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.MAILBACKGROUNDCOLOR.toString()):""%>"/>
                                    </td>

                                    <td valign="middle" data-label="Panel Heading Color" align="center"
                                        class="<%=style%>">

                                        <input id='<%=TemplatePreference.MAIL_PANELHEADING_COLOR.toString()%>' name='<%=TemplatePreference.MAIL_PANELHEADING_COLOR.toString()%>'
                                               class="form-control"
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.MAIL_PANELHEADING_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.MAIL_PANELHEADING_COLOR.toString()):""%>"/>
                                    </td>

                                    <td valign="middle" data-label="Label Font Color" align="center"
                                        class="<%=style%>">

                                        <input  id='<%=TemplatePreference.MAIL_HEADPANELFONT_COLOR.toString()%>' name='<%=TemplatePreference.MAIL_HEADPANELFONT_COLOR.toString()%>'
                                                class="form-control"
                                                value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.MAIL_HEADPANELFONT_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.MAIL_HEADPANELFONT_COLOR.toString()):""%>"/>
                                    </td>

                                    <td valign="middle" data-label="Table Color" align="center"
                                        class="<%=style%>">

                                        <input id='<%=TemplatePreference.MAIL_BODYPANELFONT_COLOR.toString()%>' name='<%=TemplatePreference.MAIL_BODYPANELFONT_COLOR.toString()%>'
                                               class="form-control"
                                               value="<%=(merchantTemplateSetting!=null && merchantTemplateSetting.containsKey(TemplatePreference.MAIL_BODYPANELFONT_COLOR.toString()))?merchantTemplateSetting.get(TemplatePreference.MAIL_BODYPANELFONT_COLOR.toString()):""%>"/>
                                    </td>

                                </tr>
                                </tbody>

                            </table>
                        </div>
                        <td align="center" colspan="2">
                            <button type="submit" value="Save" class="buttonform btn btn-default" name="Save"
                                    onclick="mySave()">
                                <%=transactionsprocess_Save%>
                            </button>
                        </td>
                        <BR>
                        <BR>
                    </center>
                </div>

            </div>
        </div>
        <%
            } //end for
        %>
        </form>
        <br>
        <%
        }
        else if (functions.isValueNull(errormsg))
        {%>
        <div class="row reporttable">
            <div class="col-md-12">
                <div class="widget">
                    <div class="widget-header">
                        <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=transactionsprocess_Report_Table%></strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding" style="overflow-x: auto;">
                        <%
                            out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
                        %>
                    </div>
                </div>
            </div>
        </div>
        <%
        }
        else
        {
        %>
        <div class="row reporttable">
            <div class="col-md-12">
                <div class="widget">
                    <div class="widget-header">
                        <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=transactionsprocess_Report_Table%></strong></h2>

                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding" style="overflow-x: auto;">
                        <%
                            out.println(Functions.NewShowConfirmation1(transactionsprocess_Sorry, transactionsprocess_no));
                        %>
                    </div>
                    <br>
                    <%
                        }
                    %>
                </div>
            </div>
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
    <script>
        //onload
        function colorPicker(first,to)
        {
            $("#"+to).val($("#"+first).val());
        }
    </script>

</body>
</html>
<%!
    public static String nullToStr(String str)
    {
        if (str == null)
            return "";
        return str;
    }
%>