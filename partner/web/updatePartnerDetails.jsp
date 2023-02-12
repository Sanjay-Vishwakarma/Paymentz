<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ include file="top.jsp" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.manager.enums.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    session.setAttribute("submit", "partnerlist");
    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
    String partnerid = String.valueOf(session.getAttribute("partnerId"));
    session.setAttribute("submit", "partnerlist");
%>
<html>
<script>
function hideshowpass(spanid,inputid)
{
var x = document.getElementById(inputid);
if (x.type === "password")
{
$("#"+spanid).removeClass('fa-eye-slash').addClass('fa-eye')
x.type = "text";
}
else
{
$("#"+spanid).removeClass('fa-eye').addClass('fa-eye-slash')
x.type = "password";
}
}
</script>

<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title><%=company%> | Partner Profile Updation</title>
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

    <style type="text/css">
        .table > thead > tr > th {
            font-weight: inherit;
        }

        /********************Table Responsive Start**************************/
        @media (max-width: 640px) {
            table {
                border: 0;
            }

            table thead {
                display: none;
            }

            tr:nth-child(odd), tr:nth-child(even) {
                background: #ffffff;
            }

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

        tr:nth-child(odd) {
            background: #F9F9F9;
        }

        tr {
            display: table-row;
            vertical-align: inherit;
            border-color: inherit;
        }

        th {
            padding-right: 1em;
            text-align: left;
            font-weight: bold;
        }

        td, th {
            display: table-cell;
            vertical-align: inherit;
        }

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

        .table > thead > tr > th, .table > tbody > tr > th, .table > tfoot > tr > th, .table > thead > tr > td, .table > tbody > tr > td, .table > tfoot > tr > td {
            border-top: 1px solid #ddd;
        }

        /********************Table Responsive Ends**************************/
        @media (max-width: 991px) {
            .additional-btn {
                float: left;
                margin-left: 30px;
                margin-top: 10px;
                position: inherit !important;
            }
        }

        @media (min-width: 768px) {
            .form-horizontal .control-label {
                text-align: left !important;
            }
        }

    </style>
    <style type="text/css">
        .field-icon {
            float: right;
            margin-top: -25px;
            position: relative;
            z-index: 2;
        }
    </style>


</head>
<body class=" widescreen pace-done" onload="bodyonload()">
<script src="/merchant/javascript/hidde.js"></script>
<%--<script src="/partner/NewCss/js/jquery-1.12.4.min.js"></script>--%>
<script src="/partner/NewCss/js/jquery-ui.min.js"></script>
<script>
    /*$(document).ready(function(){
     var w = $(window).width();

     //alert(w);

     if(w > 990){
     //alert("It's greater than 990px");
     $("body").removeClass("smallscreen").addClass("widescreen");
     $("#wrapper").removeClass("enlarged");
     }
     else{
     //alert("It's less than 990px");
     $("body").removeClass("widescreen").addClass("smallscreen");
     $("#wrapper").addClass("enlarged");
     $(".left ul").removeAttr("style");
     }
     });*/

</script>


<script language="javascript">

    function myjunk()
    {
        var hat = this.document.form2.country.selectedIndex;
        var hatto = this.document.form2.country.options[hat].value;
        var countrycd = this.document.form2.phonecc.value = hatto.split("|")[1];
        var telnumb = this.document.form2.telno.value;
        // var cctel = countrycd.concat(telnumb);
        if (hatto != 'Select one')
        {

            this.document.form2.countrycode.value = hatto.split("|")[0];
            this.document.form2.phonecc.value = hatto.split("|")[1];
            this.document.form2.country.options[0].selected = false;
        }
    }
</script>
<script>

    $(document).ready(function(){
        var relaywithauth = document.getElementById("relaywithauth").value;
        console.log(relaywithauth);
        if (relaywithauth == 'Y') {
            document.getElementById("smtp_password").disabled=false;
        }
        else
        {
            document.getElementById("smtp_password").disabled=true;
            document.getElementById("smtp_password").value="";
        }
    });

    function updatetextbox(relaywithauth)
    {
        if(relaywithauth.value=='Y')
        {
            document.getElementById("smtp_password").disabled=false;
        }
        else
        {
            document.getElementById("smtp_password").disabled=true;
            document.getElementById("smtp_password").value="";
        }
    }

    $(document).ready(function(){
        var flightid = document.getElementById("flightid").value;
        if (flightid == 'Y') {
            $('#div_split').show();
        }
        else
        {
            $('#div_split').hide();
        }
    });

    function hidblock(flightid)
    {
        if (flightid.value == 'Y')
        {
            $('#div_split').show();
        }
        else
        {
            $('#div_split').hide();
        }
    }

</script>


<%
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (partner.isLoggedInPartner(session))
    {
        PartnerFunctions partnerFunctions = new PartnerFunctions();
        Map<String, String> ynMap = new HashMap<String, String>();
        ynMap.put("N", "No");
        ynMap.put("Y", "Yes");
        String fromdate = "";
        /*String todate="";*/
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String updatePartnerDetails_Report_Table = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Report_Table")) ? rb1.getString("updatePartnerDetails_Report_Table") : "Report Table";
        String updatePartnerDetails_Username = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Username")) ? rb1.getString("updatePartnerDetails_Username") : "Username*";
        String updatePartnerDetails_username_special = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_username_special")) ? rb1.getString("updatePartnerDetails_username_special") : "(Username Should Not Contain Special Characters like !@#$%)";
        String updatePartnerDetails_Partner_Organisation_Name = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Partner_Organisation_Name")) ? rb1.getString("updatePartnerDetails_Partner_Organisation_Name") : "Partner Organisation Name*";
        String updatePartnerDetails_Organisation_Name = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Organisation_Name")) ? rb1.getString("updatePartnerDetails_Organisation_Name") : "Organisation Name";
        String updatePartnerDetails_Site_URL = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Site_URL")) ? rb1.getString("updatePartnerDetails_Site_URL") : "Site URL*";
        String updatePartnerDetails_http = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_http")) ? rb1.getString("updatePartnerDetails_http") : "(Ex. http://www.abc.com)";
        String updatePartnerDetails_Domain = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Domain")) ? rb1.getString("updatePartnerDetails_Domain") : "Domain";
        String updatePartnerDetails_Contact_Telephone_Number = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Contact_Telephone_Number")) ? rb1.getString("updatePartnerDetails_Contact_Telephone_Number") : "Contact Telephone Number*";
        String updatePartnerDetails_accepts_numeric = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_accepts_numeric")) ? rb1.getString("updatePartnerDetails_accepts_numeric") : "(Accepts only Numeric values and [.+-#])";
        String updatePartnerDetails_Support_MailID = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Support_MailID")) ? rb1.getString("updatePartnerDetails_Support_MailID") : "Support Mail ID*";
        String updatePartnerDetails_Admin_MailID = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Admin_MailID")) ? rb1.getString("updatePartnerDetails_Admin_MailID") : "Admin Mail ID*";
        String updatePartnerDetails_Support_url = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Support_url")) ? rb1.getString("updatePartnerDetails_Support_url") : "Support url*";
        String updatePartnerDetails_Host_url = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Host_url")) ? rb1.getString("updatePartnerDetails_Host_url") : "Host url*";
        String updatePartnerDetails_Company_address = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Company_address")) ? rb1.getString("updatePartnerDetails_Company_address") : "Company from address*";
        String updatePartnerDetails_SMTP_Host = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_SMTP_Host")) ? rb1.getString("updatePartnerDetails_SMTP_Host") : "SMTP Host*";
        String updatePartnerDetails_SMTP_Port = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_SMTP_Port")) ? rb1.getString("updatePartnerDetails_SMTP_Port") : "SMTP Port*";
        String updatePartnerDetails_Relay_Authorization = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Relay_Authorization")) ? rb1.getString("updatePartnerDetails_Relay_Authorization") : "Relay With Authorization";
        String updatePartnerDetails_Y = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Y")) ? rb1.getString("updatePartnerDetails_Y") : "Y";
        String updatePartnerDetails_N = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_N")) ? rb1.getString("updatePartnerDetails_N") : "N";
        String updatePartnerDetails_SMTP_User = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_SMTP_User")) ? rb1.getString("updatePartnerDetails_SMTP_User") : "SMTP User*";
        String updatePartnerDetails_SMTP_Password = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_SMTP_Password")) ? rb1.getString("updatePartnerDetails_SMTP_Password") : "SMTP Password*";
        String updatePartnerDetails_SMTP_User1 = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_SMTP_User1")) ? rb1.getString("updatePartnerDetails_SMTP_User1") : "SMS User";
        String updatePartnerDetails_SMS_Password1 = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_SMS_Password1")) ? rb1.getString("updatePartnerDetails_SMS_Password1") : "SMS Password";
        String updatePartnerDetails_From_SMS = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_From_SMS")) ? rb1.getString("updatePartnerDetails_From_SMS") : "From SMS";
        String updatePartnerDetails_Is_IP_Whitelisted = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Is_IP_Whitelisted")) ? rb1.getString("updatePartnerDetails_Is_IP_Whitelisted") : "Is IP Whitelisted*";
        String updatePartnerDetails_Is_CardEncryption_Enable = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Is_CardEncryption_Enable")) ? rb1.getString("updatePartnerDetails_Is_CardEncryption_Enable") : "Is CardEncryption Enable*";
        String updatePartnerDetails_Flight_Mode = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Flight_Mode")) ? rb1.getString("updatePartnerDetails_Flight_Mode") : "Flight Mode*";
        String updatePartnerDetails_Split_Payment = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Split_Payment")) ? rb1.getString("updatePartnerDetails_Split_Payment") : "Split Payment*";
        String updatePartnerDetails_Split_Payment_Type = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Split_Payment_Type")) ? rb1.getString("updatePartnerDetails_Split_Payment_Type") : "Split Payment Type*";
        String updatePartnerDetails_Terminal = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Terminal")) ? rb1.getString("updatePartnerDetails_Terminal") : "Terminal";
        String updatePartnerDetails_Merchant = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Merchant")) ? rb1.getString("updatePartnerDetails_Merchant") : "Merchant";
        String updatePartnerDetails_Address_Validation = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Address_Validation")) ? rb1.getString("updatePartnerDetails_Address_Validation") : "Address Validation*";
        String updatePartnerDetails_Address_Detail_Display = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Address_Detail_Display")) ? rb1.getString("updatePartnerDetails_Address_Detail_Display") : "Address Detail Display*";
        String updatePartnerDetails_AutoRedirect = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_AutoRedirect")) ? rb1.getString("updatePartnerDetails_AutoRedirect") : "AutoRedirect*";
        String updatePartnerDetails_Template = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Template")) ? rb1.getString("updatePartnerDetails_Template") : "Template";
        String updatePartnerDetails_Checkout_Invoice = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Checkout_Invoice")) ? rb1.getString("updatePartnerDetails_Checkout_Invoice") : "Checkout Invoice";
        String updatePartnerDetails_Bank_Card_Limit = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Bank_Card_Limit")) ? rb1.getString("updatePartnerDetails_Bank_Card_Limit") : "Bank Card Limit";
        String updatePartnerDetails_Emi_Configuration = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Emi_Configuration")) ? rb1.getString("updatePartnerDetails_Emi_Configuration") : "Emi Configuration";
        String updatePartnerDetails_Export_Transaction_Cron = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Export_Transaction_Cron")) ? rb1.getString("updatePartnerDetails_Export_Transaction_Cron") : "Export Transaction Cron";
        String updatePartnerDetails_Country = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Country")) ? rb1.getString("updatePartnerDetails_Country") : "Country*";
        String updatePartnerDetails_Address = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Address")) ? rb1.getString("updatePartnerDetails_Address") : "Address";
        String updatePartnerDetails_ResponseType = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_ResponseType")) ? rb1.getString("updatePartnerDetails_ResponseType") : "ResponseType*";
        String updatePartnerDetails_ResponseLength = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_ResponseLength")) ? rb1.getString("updatePartnerDetails_ResponseLength") : "ResponseLength*";
        String updatePartnerDetails_Flight_Partner = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Flight_Partner")) ? rb1.getString("updatePartnerDetails_Flight_Partner") : "Flight Partner";
        String updatePartnerDetails_Reporting_Currency = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Reporting_Currency")) ? rb1.getString("updatePartnerDetails_Reporting_Currency") : "Reporting Currency";
        String updatePartnerDetails_Is_Refund = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Is_Refund")) ? rb1.getString("updatePartnerDetails_Is_Refund") : "Is Refund";
        String updatePartnerDetails_Is_Ip_Whitelisted = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Is_Ip_Whitelisted")) ? rb1.getString("updatePartnerDetails_Is_Ip_Whitelisted") : "Is Ip Whitelisted for Invoice";
        String updatePartnerDetails_Address_Validation_Invoice = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Address_Validation_Invoice")) ? rb1.getString("updatePartnerDetails_Address_Validation_Invoice") : "Address Validation For Invoice";
        String updatePartnerDetails_Default_Template = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Default_Template")) ? rb1.getString("updatePartnerDetails_Default_Template") : "Default Template Theme";
        String updatePartnerDetails_Select_theme = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Select_theme")) ? rb1.getString("updatePartnerDetails_Select_theme") : "--Select Theme--";
        String updatePartnerDetails_PCI_Logo = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_PCI_Logo")) ? rb1.getString("updatePartnerDetails_PCI_Logo") : "PCI Logo";
        String updatePartnerDetails_Bin_Service = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Bin_Service")) ? rb1.getString("updatePartnerDetails_Bin_Service") : "Bin Service";
        String updatePartnerDetails_Old_Checkout_Template = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Old_Checkout_Template")) ? rb1.getString("updatePartnerDetails_Old_Checkout_Template") : "Is Old Checkout Template";
        String updatePartnerDetails_Is_Email_Sent = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Is_Email_Sent")) ? rb1.getString("updatePartnerDetails_Is_Email_Sent") : "Is Email Sent";
        String updatePartnerDetails_Support_Email = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Support_Email")) ? rb1.getString("updatePartnerDetails_Support_Email") : "Support Email for Transaction Mail";
        String updatePartnerDetails_Partner = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Partner")) ? rb1.getString("updatePartnerDetails_Partner") : "Partner";
        String updatePartnerDetails_TermsURL = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_TermsURL")) ? rb1.getString("updatePartnerDetails_TermsURL") : "TermsURL";
        String updatePartnerDetails_PrivacyURL = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_PrivacyURL")) ? rb1.getString("updatePartnerDetails_PrivacyURL") : "PrivacyURL";
        String updatePartnerDetails_CookiesURL = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_CookiesURL")) ? rb1.getString("updatePartnerDetails_CookiesURL") : "CookiesURL";
        String updatePartnerDetails_Contact_Details = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Contact_Details")) ? rb1.getString("updatePartnerDetails_Contact_Details") : "Contact Details";
        String updatePartnerDetails_Main = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Main")) ? rb1.getString("updatePartnerDetails_Main") : "Main";
        String updatePartnerDetails_Contact = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Contact")) ? rb1.getString("updatePartnerDetails_Contact") : "Contact*:";
        String updatePartnerDetails_Sales = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Sales")) ? rb1.getString("updatePartnerDetails_Sales") : "Sales";
        String updatePartnerDetails_Billing = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Billing")) ? rb1.getString("updatePartnerDetails_Billing") : "Billing";
        String updatePartnerDetails_Notify = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Notify")) ? rb1.getString("updatePartnerDetails_Notify") : "Notify";
        String updatePartnerDetails_Fraud = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Fraud")) ? rb1.getString("updatePartnerDetails_Fraud") : "Fraud";
        String updatePartnerDetails_Chargeback = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Chargeback")) ? rb1.getString("updatePartnerDetails_Chargeback") : "Chargeback";
        String updatePartnerDetails_Contact1 = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Contact1")) ? rb1.getString("updatePartnerDetails_Contact1") : "Contact:";
        String updatePartnerDetails_Refund = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Refund")) ? rb1.getString("updatePartnerDetails_Refund") : "Refund";
        String updatePartnerDetails_Technical = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Technical")) ? rb1.getString("updatePartnerDetails_Technical") : "Technical";
        String updatePartnerDetails_Save = StringUtils.isNotEmpty(rb1.getString("updatePartnerDetails_Save")) ? rb1.getString("updatePartnerDetails_Save") : "Save";

%>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/partnerlist.jsp?ctoken=<%=ctoken%>" method="post" name="form1">
                        <%
                            Enumeration<String> stringEnumeration = request.getParameterNames();

                            while (stringEnumeration.hasMoreElements())
                            {
                                String name = stringEnumeration.nextElement();
                                if ("fromdate".equals(name))
                                {
                                    fromdate = request.getParameter(name);
                                }
                                if ("memberid".equals(name))
                                {
                                    out.println("<input type='hidden' name='" + name + "' value='" + request.getParameterValues(name)[0] + "'/>");
                                }
                                else
                                    out.println("<input type='hidden' name='" + name + "' value='" + request.getParameter(name) + "'/>");
                            }
                        %>
                        <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
                            <img style="height: 35px;" src="/partner/images/goBack.png">
                        </button>
                    </form>
                </div>
            </div>
            <br><br><br>

            <div class="row reporttable">
                <div class="col-md-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=updatePartnerDetails_Report_Table%></strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <form action="/partner/net/UpdatePartnerDetails?ctoken=<%=ctoken%>" method="post" name="form2"
                              class="form-horizontal">
                            <input type="hidden" value="" name="onchangedvalue" id="onchangedvalue">   <%--***do not remove the field*****--%>
                            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                            <input type="hidden" name="fromdate" value="<%=fromdate%>">
                              <input type="hidden" name="partner_name1" value="<%=company%>">
                                <%
                                  HashMap<String, String> timezoneHash = Functions.getTimeZone();
                                String action = (String) request.getAttribute("action");
                                String errormsg=(String)request.getAttribute("error");
                                String message=(String)request.getAttribute("message");
                                HashMap hash = (HashMap) request.getAttribute("partnerDetail");
                                String isreadonly =(String) request.getAttribute("isreadonly");
                                String conf = " ";
                                String config1 = " ";
                                String update=" ";

                                HashMap innerhash = new HashMap();
                                if(isreadonly.equalsIgnoreCase("view"))
                                {
                                    conf = "disabled";
                                    config1="style=\"display: none;\"";
                                }
                                else
                                {
                                    update="update";
                                    action="modify";
                                }
                                 String partnerName="";
                                 String username = "";
                                 String partnerNameForWLInvoice="";
                                 String sitename="";
                                 String domain="";
                                 String telno="";
                                 String companysupportmailid="";
                                 String adminmailid="";
                                 String supporturl = "";
                                 String documentationurl = "";
                                 String hosturl = "";
                                 String companyfromaddress="";
                                 String smtp_host="";
                                 String smtp_port="";
                                 String relaywithauth="";
                                 String protocol="";
                                 String smtp_user="";
                                 String smtp_password="";
                                 String sms_user="";
                                 String sms_password="";
                                 String from_sms="";
                                 String isipwhitelisted="";
                                 String iscardencryptionenable="";
                                 String splitpayment="";
                                 String splitpaymenttype="";
                                 String addressvalidation="";
                                 String addressdetaildisplay="";
                                 String autoRedirect="";
                                 String flightMode="";
                                 String template="";
                                 String checkoutInvoice="";
                                 String bankCardLimit="";
                                 String emi_configuration="";
                                 String exportTransactionCron="";
                                 String country="";
                                 String address="";
                                 String responseType="";
                                 String responseLength="";
                                 String isflightPartner="";
                                 String isTokenizationAllowed="";
                                 String currency="";
                                 String isRefund="";
                                 String ip_whitelist_invoice="";
                                 String address_validation_invoice="";
                                 String defaulttemplatetheme="";
                                 String ispcilogo="";
                                 String binService="";
                                 String oldcheckout="";
                                 //String emailSent="";
                                 String supportemailfortransactionmail="";
                                 String termsUrl="";
                                 String privacyUrl="";
                                 String cookiesUrl="";
                                 String logoheight="";
                                 String logowidth="";
                                 String contact_persons="";
                                 String contact_emails="";
                                 String contact_ccmailid="";
                                 String salescontactname="";
                                 String salesemail="";
                                 String sales_ccemailid="";
                                 String billingemail="";
                                 String billingContactName ="";
                                 String billingccEmail ="";
                                 String notifycontactname="";
                                 String notifyemail="";
                                 String notify_ccemailid="";
                                 String fraudcontactname="";
                                 String fraudemail="";
                                 String fraud_ccemailid="";
                                 String chargebackcontactname="";
                                 String chargebackemailid="";
                                 String chargeback_ccemailid="";
                                 String refundcontactname="";
                                 String refundemailid="";
                                 String refund_ccemailid="";
                                 String technicalcontactname="";
                                 String technicalemailid="";
                                 String technical_ccemailid="";
                                 String sms_service="";

                                if (hash != null && hash.size() > 0)
                                {
                                    String style="class=tr0";
                                    innerhash = (HashMap) hash.get(1 + "");


                                    Functions functions = new Functions();
                                    PartnerDAO partnerDAO = new PartnerDAO();
                                    LinkedHashMap<String, Integer> thememap = partnerDAO.listDefaulttheme();
                                    if (functions.isValueNull((String)innerhash.get("login"))) username = (String) innerhash.get("login");
                                    if (functions.isValueNull((String)innerhash.get("partnerName"))) partnerName = (String) innerhash.get("partnerName");
                                    if (functions.isValueNull((String)innerhash.get("partnerOrgnizationForWL_Invoice"))) partnerNameForWLInvoice = (String) innerhash.get("partnerOrgnizationForWL_Invoice");
                                    if (functions.isValueNull((String)innerhash.get("contact_persons"))) contact_persons = (String) innerhash.get("contact_persons");
                                    if (functions.isValueNull((String) innerhash.get("contact_emails"))) contact_emails = (String) innerhash.get("contact_emails");
                                    if (functions.isValueNull((String) innerhash.get("country"))) country = (String) innerhash.get("country");
                                    if (functions.isValueNull((String)innerhash.get("telno"))) telno = (String) innerhash.get("telno");
                                    if (functions.isValueNull((String)innerhash.get("siteurl"))) sitename = (String) innerhash.get("siteurl");
                                    if (functions.isValueNull((String)innerhash.get("domain"))) domain = (String) innerhash.get("domain");
                                    if (functions.isValueNull((String)innerhash.get("relayWithAuth"))) relaywithauth = (String) innerhash.get("relayWithAuth");
                                    if (functions.isValueNull((String)innerhash.get("protocol"))) protocol = (String) innerhash.get("protocol");
                                    if (functions.isValueNull((String)innerhash.get("supporturl"))) supporturl = (String) innerhash.get("supporturl");
                                    if (functions.isValueNull((String)innerhash.get("documentationurl"))) documentationurl = (String) innerhash.get("documentationurl");
                                    if (functions.isValueNull((String)innerhash.get("hosturl"))) hosturl = (String) innerhash.get("hosturl");
                                    if (functions.isValueNull((String)innerhash.get("companyadminid"))) adminmailid = (String) innerhash.get("companyadminid");
                                    if (functions.isValueNull((String)innerhash.get("companysupportmailid"))) companysupportmailid = (String) innerhash.get("companyadminid");
                                    if (functions.isValueNull((String)innerhash.get("salesemail"))) salesemail = (String) innerhash.get("salesemail");
                                    if (functions.isValueNull((String)innerhash.get("billingemail"))) billingemail = (String) innerhash.get("billingemail");
                                    if (functions.isValueNull((String)innerhash.get("companyfromemail"))) companyfromaddress = (String) innerhash.get("companyfromemail");
                                    if (functions.isValueNull((String)innerhash.get("supportfromemail"))) companysupportmailid = (String) innerhash.get("supportfromemail");
                                    if (functions.isValueNull((String)innerhash.get("notifyemail"))) notifyemail = (String) innerhash.get("notifyemail");
                                    if (functions.isValueNull((String)innerhash.get("smtp_host"))) smtp_host = (String) innerhash.get("smtp_host");
                                    if (functions.isValueNull((String)innerhash.get("smtp_port"))) smtp_port = (String) innerhash.get("smtp_port");
                                    if (functions.isValueNull((String)innerhash.get("smtp_user"))) smtp_user = (String) innerhash.get("smtp_user");
                                    if (functions.isValueNull((String)innerhash.get("sms_user"))) sms_user = (String) innerhash.get("sms_user");
                                    if (functions.isValueNull((String)innerhash.get("sms_password"))) sms_password = (String) innerhash.get("sms_password");
                                    if (functions.isValueNull((String)innerhash.get("smtp_password"))) smtp_password = (String) innerhash.get("smtp_password");
                                    if (functions.isValueNull((String)innerhash.get("from_sms"))) from_sms = (String) innerhash.get("from_sms");
                                    if (functions.isValueNull((String)innerhash.get("isIpWhitelisted"))) isipwhitelisted = (String) innerhash.get("isIpWhitelisted");
                                    if (functions.isValueNull((String)innerhash.get("isCardEncryptionEnable"))) iscardencryptionenable = (String) innerhash.get("isCardEncryptionEnable");
                                    if (functions.isValueNull((String)innerhash.get("fraudemailid"))) fraudemail = (String) innerhash.get("fraudemailid");
                                    if (functions.isValueNull((String)innerhash.get("isFlightPartner"))) isflightPartner = (String) innerhash.get("isFlightPartner");
                                    if (functions.isValueNull((String)innerhash.get("isTokenizationAllowed"))) isTokenizationAllowed = (String) innerhash.get("isTokenizationAllowed");
                                    if (functions.isValueNull((String)innerhash.get("salescontactname"))) salescontactname = (String) innerhash.get("salescontactname");
                                    if (functions.isValueNull((String)innerhash.get("fraudcontactname"))) fraudcontactname = (String) innerhash.get("fraudcontactname");
                                    if (functions.isValueNull((String)innerhash.get("technicalemailid"))) technicalemailid = (String) innerhash.get("technicalemailid");
                                    if (functions.isValueNull((String)innerhash.get("technicalcontactname"))) technicalcontactname = (String) innerhash.get("technicalcontactname");
                                    if (functions.isValueNull((String)innerhash.get("chargebackemailid"))) chargebackemailid = (String) innerhash.get("chargebackemailid");
                                    if (functions.isValueNull((String)innerhash.get("chargebackcontactname"))) chargebackcontactname = (String) innerhash.get("chargebackcontactname");
                                    if (functions.isValueNull((String)innerhash.get("refundemailid"))) refundemailid = (String) innerhash.get("refundemailid");
                                    if (functions.isValueNull((String)innerhash.get("refundcontactname"))) refundcontactname = (String) innerhash.get("refundcontactname");
                                    if (functions.isValueNull((String)innerhash.get("billingcontactname"))) billingContactName = (String) innerhash.get("billingcontactname");
                                    if (functions.isValueNull((String)innerhash.get("notifycontactname"))) notifycontactname = (String) innerhash.get("notifycontactname");
                                    if (functions.isValueNull((String)innerhash.get("splitpayment"))) splitpayment = (String) innerhash.get("splitpayment");
                                    if (functions.isValueNull((String)innerhash.get("splitpaymenttype"))) splitpaymenttype = (String) innerhash.get("splitpaymenttype");
                                    if (functions.isValueNull((String)innerhash.get("flightMode"))) flightMode = (String) innerhash.get("flightMode");
                                    if (functions.isValueNull((String)innerhash.get("template"))) template = (String) innerhash.get("template");
                                    if (functions.isValueNull((String)innerhash.get("address"))) address = (String) innerhash.get("address");
                                    if (functions.isValueNull((String)innerhash.get("responseType"))) responseType = (String) innerhash.get("responseType");
                                    if (functions.isValueNull((String)innerhash.get("responseLength"))) responseLength = (String) innerhash.get("responseLength");
                                    if (functions.isValueNull((String)innerhash.get("reporting_currency"))) currency = (String) innerhash.get("reporting_currency");
                                    if (functions.isValueNull((String)innerhash.get("sms_service"))) sms_service = (String) innerhash.get("sms_service");
                                    if (functions.isValueNull((String)innerhash.get("isRefund"))) isRefund = (String) innerhash.get("isRefund");
                                    if (functions.isValueNull((String)innerhash.get("default_theme"))) defaulttemplatetheme = (String) innerhash.get("default_theme");
                                    if (functions.isValueNull((String)innerhash.get("autoRedirect"))) autoRedirect = (String) innerhash.get("autoRedirect");
                                    if (functions.isValueNull((String)innerhash.get("ip_whitelist_invoice"))) ip_whitelist_invoice = (String) innerhash.get("ip_whitelist_invoice");
                                    if (functions.isValueNull((String)innerhash.get("address_validation_invoice"))) address_validation_invoice = (String) innerhash.get("address_validation_invoice");
                                    if (functions.isValueNull((String)innerhash.get("ispcilogo"))) ispcilogo = (String) innerhash.get("ispcilogo");
                                    if (functions.isValueNull((String)innerhash.get("binService"))) binService = (String) innerhash.get("binService");
                                      if (functions.isValueNull((String)innerhash.get("oldcheckout"))) oldcheckout = (String) innerhash.get("oldcheckout");
                                     /*if (functions.isValueNull((String)innerhash.get("emailSent"))) emailSent = (String) innerhash.get("emailSent");*/
                                     if (functions.isValueNull((String)innerhash.get("support_email_for_transaction_mail"))) supportemailfortransactionmail = (String) innerhash.get("support_email_for_transaction_mail");
                                    if (functions.isValueNull((String)innerhash.get("termsUrl"))) termsUrl = (String) innerhash.get("termsUrl");
                                    if (functions.isValueNull((String)innerhash.get("privacyUrl"))) privacyUrl = (String) innerhash.get("privacyUrl");
                                    if (functions.isValueNull((String)innerhash.get("cookiesUrl"))) cookiesUrl = (String) innerhash.get("cookiesUrl");
                                    if (functions.isValueNull((String)innerhash.get("logoheight"))) logoheight = (String) innerhash.get("logoheight");
                                    if (functions.isValueNull((String)innerhash.get("logowidth"))) logowidth = (String) innerhash.get("logowidth");
                                    if (functions.isValueNull((String)innerhash.get("checkoutInvoice"))) checkoutInvoice = (String) innerhash.get("checkoutInvoice");
                                    if (functions.isValueNull((String)innerhash.get("bankCardLimit"))) bankCardLimit = (String) innerhash.get("bankCardLimit");
                                    if (functions.isValueNull((String)innerhash.get("contact_ccmailid"))) contact_ccmailid = (String) innerhash.get("contact_ccmailid");
                                    if (functions.isValueNull((String)innerhash.get("sales_ccemailid"))) sales_ccemailid = (String) innerhash.get("sales_ccemailid");
                                    if (functions.isValueNull((String)innerhash.get("billing_ccemailid"))) billingccEmail = (String) innerhash.get("billing_ccemailid");
                                    if (functions.isValueNull((String)innerhash.get("notify_ccemailid"))) notify_ccemailid = (String) innerhash.get("notify_ccemailid");
                                    if (functions.isValueNull((String)innerhash.get("fraud_ccemailid"))) fraud_ccemailid = (String) innerhash.get("fraud_ccemailid");
                                    if (functions.isValueNull((String)innerhash.get("chargeback_ccemailid"))) chargeback_ccemailid = (String) innerhash.get("chargeback_ccemailid");
                                    if (functions.isValueNull((String)innerhash.get("refund_ccemailid"))) refund_ccemailid = (String) innerhash.get("refund_ccemailid");
                                    if (functions.isValueNull((String)innerhash.get("technical_ccemailid"))) technical_ccemailid = (String) innerhash.get("technical_ccemailid");
                                    if (functions.isValueNull((String)innerhash.get("emi_configuration"))) emi_configuration = (String) innerhash.get("emi_configuration");
                                    if (functions.isValueNull((String)innerhash.get("exportTransactionCron"))) exportTransactionCron = (String) innerhash.get("exportTransactionCron");
                                    if (functions.isValueNull((String)innerhash.get("addressvalidation"))) addressvalidation = (String) innerhash.get("addressvalidation");
                                    if (functions.isValueNull((String)innerhash.get("addressdetaildisplay"))) addressdetaildisplay = (String) innerhash.get("addressdetaildisplay");
                            %>
                            <input type="hidden" size="30" name="update" value="<%=update%>">
                            <input type="hidden" size="30" name="action" value="<%=action%>">
                            <input type="hidden" size="30" name="partnerid" value="<%=(String) innerhash.get("partnerId")%>">

                            <div class="widget-content padding">
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Username%><br>
                                        <%=updatePartnerDetails_username_special%></label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100" maxlength=100
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(username)%>"
                                               name="username" size="35" disabled>
                                        <input class="form-control" type="hidden" maxlength="100" maxlength=100
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(username)%>"
                                               name="username" size="35">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Partner_Organisation_Name%></label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(partnerName)%>"
                                               name="partnerName" size="35" disabled>
                                        <input class="form-control" type="hidden" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(partnerName)%>"
                                               name="partnerName" size="35">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Organisation_Name%><span
                                            class="textb"></span><br>
                                        (Use only for WL invoice)</label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(partnerNameForWLInvoice)%>"
                                               name="partnerNameForWLInvoice" size="35" <%=conf%> onchange="ChangeFunction(this.value,'Organisation Name')">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Site_URL%><br>
                                        <%=updatePartnerDetails_http%></label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(sitename)%>"
                                               name="sitename" size="35" <%=conf%> onchange="ChangeFunction(this.value,'Site URL')">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Domain%><br>
                                        <%=updatePartnerDetails_http%></label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="1000"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(domain)%>" name="domain"
                                               size="35" <%=conf%> onchange="ChangeFunction(this.value,'Domain')">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Contact_Telephone_Number%><br><%=updatePartnerDetails_accepts_numeric%>
                                        </label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="text" maxlength="20" name="telno" size="35"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(telno)%>" <%=conf%> onchange="ChangeFunction(this.value,'Contact Telephone Number')">
                                    </div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Support_MailID%></label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(companysupportmailid)%>"
                                               name="companysupportmailid" size="35" <%=conf%> onchange="ChangeFunction(this.value,'Support Mail ID')">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Admin_MailID%></label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(adminmailid)%>"
                                               name="adminmailid" size="35" <%=conf%> onchange="ChangeFunction(this.value,'Admin Mail ID')">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Support_url%></label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(supporturl)%>"
                                               name="supporturl" size="35" <%=conf%> onchange="ChangeFunction(this.value,'Support url')">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label">Documentation url*</label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(documentationurl)%>"
                                               name="documentationurl" size="35" <%=conf%> onchange="ChangeFunction(this.value,'Documentation url')">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Host_url%></label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(hosturl)%>"
                                               name="hosturl" size="35" <%=conf%> onchange="ChangeFunction(this.value,'Host url')">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Company_address%></label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(companyfromaddress)%>"
                                               name="companyfromaddress" size="35" <%=conf%> onchange="ChangeFunction(this.value,'Company from address')">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>




                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_SMTP_Host%></label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(smtp_host)%>"
                                               name="smtp_host" size="35" <%=conf%> onchange="ChangeFunction(this.value,'SMTP Host')">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_SMTP_Port%></label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(smtp_port)%>"
                                               name="smtp_port" size="35" <%=conf%> onchange="ChangeFunction(this.value,'SMTP Port')">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Relay_Authorization%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="relaywithauth" id="relaywithauth"
                                                onchange="updatetextbox(relaywithauth)" <%=conf%> onchange="ChangeFunction(this.value,'Relay With Authorization')">
                                            <%
                                                if ("Y".equals(relaywithauth))
                                                {
                                            %>
                                            <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <option value="Y"><%=updatePartnerDetails_Y%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label">Protocol</label>
                                    <div class="col-md-4">
                                        <select class="form-control" name="protocol" <%=conf%> onchange="ChangeFunction(this.value,'Protocol')">
                                            <%
                                                if ("SSL".equals(protocol))
                                                {
                                            %>
                                            <option value="SSL" selected>SSL</option>
                                            <option value="TLS">TLS</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="TLS" selected>TLS</option>
                                            <option value="SSL">SSL</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label">SMTP User*</label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(smtp_user)%>"
                                               name="smtp_user" size="35" <%=conf%> onchange="ChangeFunction(this.value,'SMTP User')">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group" <%=config1%>>
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_SMTP_Password%></label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="password" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(smtp_password)%>"
                                               name="smtp_password" id="smtp_password" size="35" <%=conf%>><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="smtp" onclick="hideshowpass('smtp','smtp_password')"></span>
                                        <input type="hidden"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(smtp_password)%>"
                                               name="smtp_password" <%=conf%>>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_SMTP_User1%></label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(sms_user)%>"
                                               name="sms_user" size="35" <%=conf%> onchange="ChangeFunction(this.value,'SMS User1')">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_SMS_Password1%></label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="password" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(sms_password)%>"
                                               name="sms_password" id="sms_password" size="35" <%=conf%> onchange="ChangeFunction(this.value,'SMS Password')" autocomplete="off" readonly onfocus="this.removeAttribute('readonly');">
                                        <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="sms" onclick="hideshowpass('sms','sms_password')"></span>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_From_SMS%></label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(from_sms)%>"
                                               name="from_sms" size="35" <%=conf%> onchange="ChangeFunction(this.value,'From SMS')">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Is_IP_Whitelisted%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="isipwhitelisted" <%=conf%> onchange="ChangeFunction(this.value,'Is IP Whitelisted')">
                                            <%
                                                if ("Y".equals(isipwhitelisted))
                                                {
                                            %>
                                            <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <option value="Y"><%=updatePartnerDetails_Y%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Is_CardEncryption_Enable%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="iscardencryptionenable" <%=conf%> onchange="ChangeFunction(this.value,'Is CardEncryption Enable')">
                                            <%
                                                if ("Y".equals(iscardencryptionenable))
                                                {
                                            %>
                                            <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <option value="Y"><%=updatePartnerDetails_Y%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Flight_Mode%></label>

                                    <div class="col-md-4">
                                        <select class="form-control"
                                                name="flightMode" <%=conf%> id="flightid" onchange="hidblock(flightid);ChangeFunction(this.value,'Flight Mode')">
                                            <%
                                                if ("Y".equals(flightMode))
                                                {
                                            %>
                                            <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <option value="Y"><%=updatePartnerDetails_Y%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div id="div_split">
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Split_Payment%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="splitpayment" <%=conf%> onchange="ChangeFunction(this.value,'Split Payment')">
                                            <%
                                                if ("Y".equals(splitpayment))
                                                {
                                            %>
                                            <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <option value="Y"><%=updatePartnerDetails_Y%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>


                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Split_Payment_Type%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="splitpaymenttype"<%=conf%> onchange="ChangeFunction(this.value,'Split Payment Type')">
                                            <%
                                                if ("terminal".equals(splitpaymenttype))
                                                {
                                            %>
                                            <option value="terminal" selected><%=updatePartnerDetails_Terminal%></option>
                                            <option value="merchant"><%=updatePartnerDetails_Merchant%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="merchant" selected><%=updatePartnerDetails_Merchant%></option>
                                            <option value="terminal"><%=updatePartnerDetails_Terminal%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Address_Validation%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="addressvalidation" <%=conf%> onchange="ChangeFunction(this.value,'Address Validation')">
                                            <%
                                                if ("Y".equals(addressvalidation))
                                                {
                                            %>
                                            <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <option value="Y"><%=updatePartnerDetails_Y%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Address_Detail_Display%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="addressdetaildisplay" <%=conf%> onchange="ChangeFunction(this.value,'Address Detail Display')">
                                            <%
                                                if ("Y".equals(addressdetaildisplay))
                                                {
                                            %>
                                            <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <option value="Y"><%=updatePartnerDetails_Y%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_AutoRedirect%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="autoRedirect" <%=conf%> onchange="ChangeFunction(this.value,'AutoRedirect')">
                                            <%
                                                if ("Y".equals(autoRedirect))
                                                {
                                            %>
                                            <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <option value="Y"><%=updatePartnerDetails_Y%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Template%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="template" <%=conf%> onchange="ChangeFunction(this.value,'Template')">
                                            <%
                                                if ("Y".equals(template))
                                                {
                                            %>
                                            <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <option value="Y"><%=updatePartnerDetails_Y%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Checkout_Invoice%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="checkoutInvoice" <%=conf%> onchange="ChangeFunction(this.value,'Checkout Invoice')">
                                            <%
                                                if ("Y".equals(checkoutInvoice))
                                                {
                                            %>
                                            <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <option value="Y"><%=updatePartnerDetails_Y%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Bank_Card_Limit%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="bankCardLimit" <%=conf%> onchange="ChangeFunction(this.value,'Bank Card Limit')">
                                            <%
                                                if ("Y".equals(bankCardLimit))
                                                {
                                            %>
                                            <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <option value="Y"><%=updatePartnerDetails_Y%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6">
                                        <input name="emi_configuration" value="<%=emi_configuration%>" type="hidden">
                                    </div>
                                </div>

                                <%--<div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Emi_Configuration%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="emi_configuration" <%=conf%>>
                                            <%
                                                if ("Y".equals(emi_configuration))
                                                {
                                            %>
                                            <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <option value="Y"><%=updatePartnerDetails_Y%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>--%>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Export_Transaction_Cron%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="exportTransactionCron" <%=conf%> onchange="ChangeFunction(this.value,'Export Transaction Cron')">
                                            <%
                                                if ("N".equalsIgnoreCase(exportTransactionCron))
                                                {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                                }
                                                Set timezoneSet = timezoneHash.keySet();
                                                java.util.Iterator itr = timezoneSet.iterator();
                                                String selected4 = "";
                                                String timezonekey = "";
                                                String timezonevalue = "";
                                                while (itr.hasNext())
                                                {
                                                    timezonekey = (String) itr.next();
                                                    timezonevalue = timezoneHash.get(timezonekey);
                                                    if (timezonekey.equals(exportTransactionCron))
                                                        selected4 = "selected";
                                                    else
                                                        selected4 = "";

                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(timezonekey)%>" <%=selected4%>><%=ESAPI.encoder().encodeForHTML(timezonevalue)%>
                                            </option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Country%></label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>"
                                               name="country" size="35" <%=conf%> onchange="ChangeFunction(this.value,'Country')">
                                        <%--<select name="country" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>"  onchange="myjunk();"  >
                                            <option value="-|phonecc">Select a Country*</option>
                                            <option value="AF|093">Afghanistan</option>
                                            <option value="AX|358">Aland Islands</option>
                                            <option value="AL|355">Albania</option>
                                            <option value="DZ|231">Algeria</option>
                                            <option value="AS|684">American Samoa</option>
                                            <option value="AD|376">Andorra</option>
                                            <option value="AO|244">Angola</option>
                                            <option value="AI|001">Anguilla</option>
                                            <option value="AQ|000">Antarctica</option>
                                            <option value="AG|001">Antigua and Barbuda</option>
                                            <option value="AR|054">Argentina</option>
                                            <option value="AM|374">Armenia</option>
                                            <option value="AW|297">Aruba</option>
                                            <option value="AU|061">Australia</option>
                                            <option value="AT|043">Austria</option>
                                            <option value="AZ|994">Azerbaijan</option>
                                            <option value="BS|001">Bahamas</option>
                                            <option value="BH|973">Bahrain</option>
                                            <option value="BD|880">Bangladesh</option>
                                            <option value="BB|001">Barbados</option>
                                            <option value="BY|375">Belarus</option>
                                            <option value="BE|032">Belgium</option>
                                            <option value="BZ|501">Belize</option>
                                            <option value="BJ|229">Benin</option>
                                            <option value="BM|001">Bermuda</option>
                                            <option value="BT|975">Bhutan</option>
                                            <option value="BO|591">Bolivia</option>
                                            <option value="BA|387">Bosnia and Herzegovina</option>
                                            <option value="BW|267">Botswana</option>
                                            <option value="BV|000">Bouvet Island</option>
                                            <option value="BR|055">Brazil</option>
                                            <option value="IO|246">British Indian Ocean Territory</option>
                                            <option value="VG|001">British Virgin Islands</option>
                                            <option value="BN|673">Brunei</option>
                                            <option value="BG|359">Bulgaria</option>
                                            <option value="BF|226">Burkina Faso</option>
                                            <option value="BI|257">Burundi</option>
                                            <option value="KH|855">Cambodia</option>
                                            <option value="CM|237">Cameroon</option>
                                            <option value="CA|001">Canada</option>
                                            <option value="CV|238">Cape Verde</option>
                                            <option value="KY|001">Cayman Islands</option>
                                            <option value="CF|236">Central African Republic</option>
                                            <option value="TD|235">Chad</option>
                                            <option value="CL|056">Chile</option>
                                            <option value="CN|086">China</option>
                                            <option value="CX|061">Christmas Island</option>
                                            <option value="CC|061">Cocos (Keeling) Islands</option>
                                            <option value="CO|057">Colombia</option>
                                            <option value="KM|269">Comoros</option>
                                            <option value="CK|682">Cook Islands</option>
                                            <option value="CR|506">Costa Rica</option>
                                            <option value="CI|225">Cote d'Ivoire</option>
                                            <option value="HR|385">Croatia</option>
                                            <option value="CU|053">Cuba</option>
                                            <option value="CW|599">Curacao</option>
                                            <option value="CY|357">Cyprus</option>
                                            <option value="CZ|420">Czech Republic</option>
                                            <option value="CD|243">Democratic Republic of the Congo</option>
                                            <option value="DK|045">Denmark</option>
                                            <option value="DJ|253">Djibouti</option>
                                            <option value="DM|001">Dominica</option>
                                            <option value="DO|001">Dominican Republic</option>
                                            <option value="EC|593">Ecuador</option>
                                            <option value="EG|020">Egypt</option>
                                            <option value="SV|503">El Salvador</option>
                                            <option value="GQ|240">Equatorial Guinea</option>
                                            <option value="ER|291">Eritrea</option>
                                            <option value="EE|372">Estonia</option>
                                            <option value="ET|251">Ethiopia</option>
                                            <option value="FK|500">Falkland Islands</option>
                                            <option value="FO|298">Faroe Islands</option>
                                            <option value="FJ|679">Fiji</option>
                                            <option value="FI|358">Finland</option>
                                            <option value="FR|033">France</option>
                                            <option value="GF|594">French Guiana</option>
                                            <option value="PF|689">French Polynesia</option>
                                            <option value="TF|000">French Southern and Antarctic Lands</option>
                                            <option value="GA|241">Gabon</option>
                                            <option value="GM|220">Gambia</option>
                                            <option value="GE|995">Georgia</option>
                                            <option value="DE|049">Germany</option>
                                            <option value="GH|233">Ghana</option>
                                            <option value="GI|350">Gibraltar</option>
                                            <option value="GR|030">Greece</option>
                                            <option value="GL|299">Greenland</option>
                                            <option value="GD|001">Grenada</option>
                                            <option value="GP|590">Guadeloupe</option>
                                            <option value="GU|001">Guam</option>
                                            <option value="GT|502">Guatemala</option>
                                            <option value="GG|000">Guernsey</option>
                                            <option value="GN|224">Guinea</option>
                                            <option value="GW|245">Guinea-Bissau</option>
                                            <option value="GY|592">Guyana</option>
                                            <option value="HT|509">Haiti</option>
                                            <option value="HM|672">Heard Island & McDonald Islands</option>
                                            <option value="HN|504">Honduras</option>
                                            <option value="HK|852">Hong Kong</option>
                                            <option value="HU|036">Hungary</option>
                                            <option value="IS|354">Iceland</option>
                                            <option value="IN|091">India</option>
                                            <option value="ID|062">Indonesia</option>
                                            <option value="IR|098">Iran</option>
                                            <option value="IQ|964">Iraq</option>
                                            <option value="IE|353">Ireland</option>
                                            <option value="IL|972">Israel</option>
                                            <option value="IT|039">Italy</option>
                                            <option value="JM|001">Jamaica</option>
                                            <option value="JP|081">Japan</option>
                                            <option value="JE|044">Jersey</option>
                                            <option value="JO|962">Jordan</option>
                                            <option value="KZ|007">Kazakhstan</option>
                                            <option value="KE|254">Kenya</option>
                                            <option value="KI|686">Kiribati</option>
                                            <option value="KW|965">Kuwait</option>
                                            <option value="KG|996">Kyrgyzstan</option>
                                            <option value="LA|856">Laos</option>
                                            <option value="LV|371">Latvia</option>
                                            <option value="LB|961">Lebanon</option>
                                            <option value="LS|266">Lesotho</option>
                                            <option value="LR|231">Liberia</option>
                                            <option value="LY|218">Libya</option>
                                            <option value="LI|423">Liechtenstein</option>
                                            <option value="LT|370">Lithuania</option>
                                            <option value="LU|352">Luxembourg</option>
                                            <option value="MO|853">Macau, China</option>
                                            <option value="MK|389">Macedonia</option>
                                            <option value="MG|261">Madagascar</option>
                                            <option value="MW|265">Malawi</option>
                                            <option value="MY|060">Malaysia</option>
                                            <option value="MV|960">Maldives</option>
                                            <option value="ML|223">Mali</option>
                                            <option value="MT|356">Malta</option>
                                            <option value="MH|692">Marshall Islands</option>
                                            <option value="MQ|596">Martinique</option>
                                            <option value="MR|222">Mauritania</option>
                                            <option value="MU|230">Mauritius</option>
                                            <option value="YT|269">Mayotte</option>
                                            <option value="MX|052">Mexico</option>
                                            <option value="FM|691">Micronesia, Federated States of</option>
                                            <option value="MD|373">Moldova</option>
                                            <option value="MC|377">Monaco</option>
                                            <option value="MN|976">Mongolia</option>
                                            <option value="ME|382">Montenegro</option>
                                            <option value="MS|001">Montserrat</option>
                                            <option value="MA|212">Morocco</option>
                                            <option value="MZ|258">Mozambique</option>
                                            <option value="MM|095">Myanmar</option>
                                            <option value="NA|264">Namibia</option>
                                            <option value="NR|674">Nauru</option>
                                            <option value="NP|977">Nepal</option>
                                            <option value="AN|599">Netherlands Antilles</option>
                                            <option value="NL|031">Netherlands</option>
                                            <option value="NC|687">New Caledonia</option>
                                            <option value="NZ|064">New Zealand</option>
                                            <option value="NI|505">Nicaragua</option>
                                            <option value="NE|227">Niger</option>
                                            <option value="NG|234">Nigeria</option>
                                            <option value="NU|683">Niue</option>
                                            <option value="NF|672">Norfolk Island</option>
                                            <option value="KP|850">North Korea</option>
                                            <option value="MP|001">Northern Mariana Islands</option>
                                            <option value="NO|047">Norway</option>
                                            <option value="OM|968">Oman</option>
                                            <option value="PK|092">Pakistan</option>
                                            <option value="PW|680">Palau</option>
                                            <option value="PS|970">Palestinian Authority</option>
                                            <option value="PA|507">Panama</option>
                                            <option value="PG|675">Papua New Guinea</option>
                                            <option value="PY|595">Paraguay</option>
                                            <option value="PE|051">Peru</option>
                                            <option value="PH|063">Philippines</option>
                                            <option value="PN|064">Pitcairn Islands</option>
                                            <option value="PL|048">Poland</option>
                                            <option value="PT|351">Portugal</option>
                                            <option value="PR|001">Puerto Rico</option>
                                            <option value="QA|974">Qatar</option>
                                            <option value="CG|242">Republic of the Congo</option>
                                            <option value="RE|262">Reunion</option>
                                            <option value="RO|040">Romania</option>
                                            <option value="RU|007">Russia</option>
                                            <option value="RW|250">Rwanda</option>
                                            <option value="BL|590">Saint Barthelemy</option>
                                            <option value="SH|290">Saint Helena, Ascension & Tristan daCunha</option>
                                            <option value="KN|001">Saint Kitts and Nevis</option>
                                            <option value="LC|001">Saint Lucia</option>
                                            <option value="MF|590">Saint Martin</option>
                                            <option value="PM|508">Saint Pierre and Miquelon</option>
                                            <option value="VC|001">Saint Vincent and Grenadines</option>
                                            <option value="WS|685">Samoa</option>
                                            <option value="SM|378">San Marino</option>
                                            <option value="ST|239">Sao Tome and Principe</option>
                                            <option value="SA|966">Saudi Arabia</option>
                                            <option value="SN|221">Senegal</option>
                                            <option value="RS|381">Serbia</option>
                                            <option value="SC|248">Seychelles</option>
                                            <option value="SL|232">Sierra Leone</option>
                                            <option value="SG|065">Singapore</option>
                                            <option value="SK|421">Slovakia</option>
                                            <option value="SI|386">Slovenia</option>
                                            <option value="SB|677">Solomon Islands</option>
                                            <option value="SO|252">Somalia</option>
                                            <option value="ZA|027">South Africa</option>
                                            <option value="GS|000">South Georgia & South Sandwich Islands</option>
                                            <option value="KR|082">South Korea</option>
                                            <option value="ES|034">Spain</option>
                                            <option value="LK|094">Sri Lanka</option>
                                            <option value="SD|249">Sudan</option>
                                            <option value="SR|597">Suriname</option>
                                            <option value="SJ|047">Svalbard and Jan Mayen</option>
                                            <option value="SZ|268">Swaziland</option>
                                            <option value="SE|046">Sweden</option>
                                            <option value="CH|041">Switzerland</option>
                                            <option value="SY|963">Syria</option>
                                            <option value="TW|886">Taiwan</option>
                                            <option value="TJ|992">Tajikistan</option>
                                            <option value="TZ|255">Tanzania</option>
                                            <option value="TH|066">Thailand</option>
                                            <option value="TL|670">Timor-Leste</option>
                                            <option value="TG|228">Togo</option>
                                            <option value="TK|690">Tokelau</option>
                                            <option value="TO|676">Tonga</option>
                                            <option value="TT|001">Trinidad and Tobago</option>
                                            <option value="TN|216">Tunisia</option>
                                            <option value="TR|090">Turkey</option>
                                            <option value="TM|993">Turkmenistan</option>
                                            <option value="TC|001">Turks and Caicos Islands</option>
                                            <option value="TV|688">Tuvalu</option>
                                            <option value="UG|256">Uganda</option>
                                            <option value="UA|380">Ukraine</option>
                                            <option value="AE|971">United Arab Emirates</option>
                                            <option value="GB|044">United Kingdom</option>
                                            <option value="US|001">United States</option>
                                            <option value="VI|001">United States Virgin Islands</option>
                                            <option value="UY|598">Uruguay</option>
                                            <option value="UZ|998">Uzbekistan</option>
                                            <option value="VU|678">Vanuatu</option>
                                            <option value="VA|379">Vatican City</option>
                                            <option value="VE|058">Venezuela</option>
                                            <option value="VN|084">Vietnam</option>
                                            <option value="WF|681">Wallis and Futuna</option>
                                            <option value="EH|212">Western Sahara</option>
                                            <option value="YE|967">Yemen</option>
                                            <option value="ZM|260">Zambia</option>
                                            <option value="ZW|263">Zimbabwe</option>
                                          </select>--%>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Address%></label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(address)%>"
                                               name="address" size="35" <%=conf%> onchange="ChangeFunction(this.value,'Address')">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_ResponseType%></label>
                                    <div class="col-md-4">
                                        <select class="form-control" name="responsetype" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'ResponseType')">
                                            </option>
                                            <%
                                                for (ResponseType responseType1 : ResponseType.values())
                                                {
                                                    String selected = "";
                                                    if (responseType1.toString().equals(responseType))
                                                    {
                                                        selected = "selected";
                                                    }
                                                    out.println("<option value=\"" + responseType1.toString() + "\" " + selected + ">" + responseType1.toString() + "</option>");
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_ResponseLength%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="responselength" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'ResponseLength')">
                                            </option>
                                            <%
                                                for (ResponseLength responseLength1 : ResponseLength.values())
                                                {
                                                    String selected = "";

                                                    if (responseLength1.toString().equals(responseLength))
                                                    {
                                                        selected = "selected";
                                                    }
                                                    out.println("<option value=\"" + responseLength1.toString() + "\" " + selected + ">" + responseLength1.toString() + "</option>");
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>


                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Flight_Partner%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="isflightPartner" <%=conf%> onchange="ChangeFunction(this.value,'Flight Partner')">
                                            <%
                                                if ("Y".equals(isflightPartner))
                                                {
                                            %>
                                            <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <option value="Y"><%=updatePartnerDetails_Y%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Reporting_Currency%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="currency" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'Reporting Currency')">
                                            </option>
                                            <%
                                                for (Currency currency1 : Currency.values())
                                                {
                                                    String selected = "";
                                                    if (currency1.toString().equals(currency))
                                                    {
                                                        selected = "selected";
                                                    }
                                                    out.println("<option value=\"" + currency1.toString() + "\" " + selected + ">" + currency1.toString() + "</option>");
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                    <input type="hidden" name="isrefund" value="<%=isRefund%>">
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label">SMS Service</label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="sms_service" class="txtbox" <%=conf%> onchange="ChangeFunction(this.value,'Reporting Currency')">
                                            </option>
                                            <%
                                                for (SmsService smsService:SmsService.values())
                                                {
                                                    String selected = "";
                                                    if (smsService.toString().equals(sms_service))
                                                    {
                                                        selected = "selected";
                                                    }
                                                    out.println("<option value=\"" + smsService.toString() + "\" " + selected + ">" + smsService.toString() + "</option>");
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                    <input type="hidden" name="isrefund" value="<%=isRefund%>">
                                </div>

                            <%--<div class="form-group">
                                <div class="col-md-2"></div>
                                <label class="col-md-4 control-label"><%=updatePartnerDetails_Is_Refund%></label>

                                <div class="col-md-4">
                                    <select class="form-control" name="isrefund" <%=conf%>>
                                        <%
                                            if ("Y".equals(isRefund))
                                            {
                                        %>
                                        <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                        <option value="N"><%=updatePartnerDetails_N%></option>
                                        <%
                                        }
                                        else
                                        {
                                        %>
                                        <option value="N" selected><%=updatePartnerDetails_N%></option>
                                        <option value="Y"><%=updatePartnerDetails_Y%></option>
                                        <%
                                            }
                                        %>
                                    </select>
                                </div>
                                <div class="col-md-6"></div>
                            </div>--%>


                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label">Tokenization Allowed</label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="isTokenizationAllowed" <%=conf%> onchange="ChangeFunction(this.value,'Tokenization Allowed:')">
                                            <%
                                                if ("Y".equals(isTokenizationAllowed))
                                                {
                                            %>
                                            <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <option value="Y"><%=updatePartnerDetails_Y%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Is_Ip_Whitelisted%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="ip_whitelist_invoice" <%=conf%> onchange="ChangeFunction(this.value,'Is Ip Whitelisted for Invoice')">
                                            <%
                                                if ("Y".equals(ip_whitelist_invoice))
                                                {
                                            %>
                                            <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <option value="Y"><%=updatePartnerDetails_Y%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Address_Validation_Invoice%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="address_validation_invoice" <%=conf%> onchange="ChangeFunction(this.value,'Address Validation For Invoice')">
                                            <%
                                                if ("Y".equals(address_validation_invoice))
                                                {
                                            %>
                                            <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <option value="Y"><%=updatePartnerDetails_Y%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Default_Template%></label>

                                    <div class="col-md-4">
                                        <select name="defaulttemplatetheme" class="form-control" <%=conf%> onchange="ChangeFunction(this.value,'Default Template Theme')">
                                            <option value="" selected><%=updatePartnerDetails_Select_theme%></option>
                                            <%
                                                for (String themename : thememap.keySet())
                                                {
                                                    String isSelected = "";
                                                    if (String.valueOf(themename).equalsIgnoreCase(defaulttemplatetheme))
                                                    {
                                                        isSelected = "selected";
                                                    }

                                            %>
                                            <option value="<%=themename%>"<%=isSelected%>><%=themename%>
                                            </option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_PCI_Logo%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="ispcilogo" <%=conf%> onchange="ChangeFunction(this.value,'PCI Logo')">
                                            <%
                                                if ("Y".equals(ispcilogo))
                                                {
                                            %>
                                            <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <option value="Y"><%=updatePartnerDetails_Y%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Bin_Service%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="binService" <%=conf%> onchange="ChangeFunction(this.value,'Bin Service')">
                                            <%
                                                if ("Y".equals(binService))
                                                {
                                            %>
                                            <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <option value="Y"><%=updatePartnerDetails_Y%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                        <%--        <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Old_Checkout_Template%></label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="oldcheckout" <%=conf%> onchange="ChangeFunction(this.value,'Is Old Checkout Template')">
                                            <%
                                                if ("Y".equals(oldcheckout))
                                                {
                                            %>
                                            <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                            <option value="N"><%=updatePartnerDetails_N%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected><%=updatePartnerDetails_N%></option>
                                            <option value="Y"><%=updatePartnerDetails_Y%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>--%>

                                <%--<div class="form-group">
                                <div class="col-md-2"></div>
                                <label class="col-md-4 control-label"><%=updatePartnerDetails_Is_Email_Sent%></label>

                                <div class="col-md-4">
                                    <select class="form-control" name="emailSent" <%=conf%> onchange="ChangeFunction(this.value,'Is Email Sent')">
                                        <%
                                            if ("Y".equals(emailSent))
                                            {
                                        %>
                                        <option value="Y" selected><%=updatePartnerDetails_Y%></option>
                                        <option value="N"><%=updatePartnerDetails_N%></option>
                                        <%
                                        }
                                        else
                                        {
                                        %>
                                        <option value="N" selected><%=updatePartnerDetails_N%></option>
                                        <option value="Y"><%=updatePartnerDetails_Y%></option>
                                        <%
                                            }
                                        %>
                                    </select>
                                </div>
                                <div class="col-md-6"></div>
                            </div>--%>


                            <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_Support_Email%></label>

                                    <div class="col-md-4">
                                        <select name="supportemailfortransactionmail" class="form-control" <%=conf%> onchange="ChangeFunction(this.value,'Support Email for Transaction Mail')">
                                            <%
                                                if ("Partner".equals(supportemailfortransactionmail))
                                                {
                                            %>
                                            <option value="Partner" selected><%=updatePartnerDetails_Partner%></option>
                                            <option value="Merchant"><%=updatePartnerDetails_Merchant%></option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="Merchant" selected><%=updatePartnerDetails_Merchant%></option>
                                            <option value="Partner"><%=updatePartnerDetails_Partner%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_TermsURL%></label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(termsUrl)%>"
                                               name="termsUrl" size="35" <%=conf%> onchange="ChangeFunction(this.value,'TermsURL')">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=updatePartnerDetails_PrivacyURL%></label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(privacyUrl)%>"
                                               name="privacyUrl" size="35" <%=conf%> onchange="ChangeFunction(this.value,'PrivacyURL')">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                            <div class="form-group">
                                <div class="col-md-2"></div>
                                <label class="col-md-4 control-label"><%=updatePartnerDetails_CookiesURL%></label>

                                <div class="col-md-4">
                                    <input class="form-control" type="Text" maxlength="100"
                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute(cookiesUrl)%>"
                                           name="cookiesUrl" size="35" <%=conf%> onchange="ChangeFunction(this.value,'CookiesURL')">
                                </div>
                                <div class="col-md-6"></div>
                            </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label">Logo Height</label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(logoheight)%>"
                                               name="logoheight" size="35" <%=conf%> onchange="ChangeFunction(this.value,'LogoHeight')">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label">Logo Width</label>

                                    <div class="col-md-4">
                                        <input class="form-control" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(logowidth)%>"
                                               name="logowidth" size="35" <%=conf%> onchange="ChangeFunction(this.value,'LogoWidth')">
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>



                                <div class="widget-header transparent">
                                    <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=updatePartnerDetails_Contact_Details%></strong></h2>

                                    <div class="additional-btn">
                                    </div>
                                </div>

                                <div class="widget-content padding">
                                    <div id="horizontal-form">
                                        <div class="form-group col-md-12 has-feedback">
                                            <div class="table-responsive">
                                                <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600; width:100%">
                                                     <tr id="mytabletr">
                                                        <td align="left"
                                                            style="background-color: #7eccad !important;color: white;">
                                                            <%=updatePartnerDetails_Main%>&nbsp;<%=updatePartnerDetails_Contact%>
                                                        </td>
                                                        <td align="center">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_persons)%>"
                                                                   name="contact_persons" placeholder="Name*" <%=conf%> onchange="ChangeFunction(this.value,'Main Contact')">
                                                        </td>
                                                        <td align="center">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_emails)%>"
                                                                   name="contact_emails" placeholder="Email*" <%=conf%> onchange="ChangeFunction(this.value,'Main Contact Email')">
                                                        </td>
                                                        <td align="center">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_ccmailid)%>"
                                                                   name="contact_ccmailid" placeholder="Email Cc" <%=conf%> onchange="ChangeFunction(this.value,'Main Contact EmailCc')">
                                                        </td>
                                                    </tr>

                                                    <tr id="mytabletr">
                                                        <td align="left"
                                                            style="background-color: #7eccad !important;color: white;">
                                                            <%=updatePartnerDetails_Sales%>&nbsp;<%=updatePartnerDetails_Contact%>
                                                        </td>
                                                        <td align="center">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(salescontactname)%>"
                                                                   name="salescontactname" placeholder="Name*" <%=conf%> onchange="ChangeFunction(this.value,'Sales Contact')">
                                                        </td>
                                                        <td align="center">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesemail)%>"
                                                                   name="salesemail" placeholder="Email*" <%=conf%> onchange="ChangeFunction(this.value,'Sales Contact Email')">
                                                        </td>
                                                        <td align="center">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(sales_ccemailid)%>"
                                                                   name="sales_ccemailid" placeholder="Email Cc" <%=conf%> onchange="ChangeFunction(this.value,'Sales Contact EmailCc')">
                                                        </td>
                                                    </tr>
                                                    <tr id="mytabletr">
                                                        <td align="left"
                                                            style="background-color: #7eccad !important;color: white;">
                                                            <%=updatePartnerDetails_Billing%>&nbsp;<%=updatePartnerDetails_Contact%>
                                                        </td>
                                                        <td align="center">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContactName)%>"
                                                                   name="billingcontactname" placeholder="Name*" <%=conf%> onchange="ChangeFunction(this.value,'Billing Contact')">
                                                        </td>
                                                        <td align="center">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingemail)%>"
                                                                   name="billingemail" placeholder="Email*" <%=conf%> onchange="ChangeFunction(this.value,'Billing Contact Email')">
                                                        </td>
                                                        <td align="center">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingccEmail)%>"
                                                                   name="billing_ccemailid" placeholder="Email Cc" <%=conf%> onchange="ChangeFunction(this.value,'Billing Contact EmailCc')">
                                                        </td>

                                                    </tr>
                                                    <tr id="mytabletr">
                                                        <td align="left"
                                                            style="background-color: #7eccad !important;color: white;">
                                                            <%=updatePartnerDetails_Notify%>&nbsp;<%=updatePartnerDetails_Contact%>
                                                        </td>
                                                        <td align="center">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(notifycontactname)%>"
                                                                   name="notifycontactname" placeholder="Name*" <%=conf%> onchange="ChangeFunction(this.value,'Notify Contact')">
                                                        </td>
                                                        <td align="center" class="text">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(notifyemail)%>"
                                                                   name="notifyemail" placeholder="Email*" <%=conf%> onchange="ChangeFunction(this.value,'Notify Contact Email')">
                                                        </td>
                                                        <td align="center">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(notify_ccemailid)%>"
                                                                   name="notify_ccemailid" placeholder="Email Cc" <%=conf%> onchange="ChangeFunction(this.value,'Notify Contact EmailCc')">
                                                        </td>
                                                    </tr>
                                                    <tr id="mytabletr">
                                                        <td align="left"
                                                            style="background-color: #7eccad !important;color: white;">
                                                            <%=updatePartnerDetails_Fraud%>&nbsp;<%=updatePartnerDetails_Contact%>
                                                        </td>
                                                        <td align="center">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudcontactname)%>"
                                                                   name="fraudcontactname" placeholder="Name*" <%=conf%> onchange="ChangeFunction(this.value,'Fraud Contact')">
                                                        </td>
                                                        <td align="center" class="text">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudemail)%>"
                                                                   name="fraudemail" placeholder="Email*" <%=conf%> onchange="ChangeFunction(this.value,'Fraud Contact Email')">
                                                        </td>
                                                        <td align="center">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraud_ccemailid)%>"
                                                                   name="fraud_ccemailid" placeholder="Email Cc" <%=conf%> onchange="ChangeFunction(this.value,'Fraud Contact EmailCc')">
                                                        </td>
                                                    </tr>
                                                    <tr id="mytabletr">
                                                        <td align="left"
                                                            style="background-color: #7eccad !important;color: white;">
                                                            <%=updatePartnerDetails_Chargeback%>&nbsp;<%=updatePartnerDetails_Contact1%>
                                                        </td>
                                                        <td align="center">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(chargebackcontactname)%>"
                                                                   name="chargebackcontactname" placeholder="Name" <%=conf%> onchange="ChangeFunction(this.value,'Chargeback Contact')">
                                                        </td>
                                                        <td align="center" class="text">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(chargebackemailid)%>"
                                                                   name="chargebackemailid" placeholder="Email" <%=conf%> onchange="ChangeFunction(this.value,'Chargeback Contact Email')">
                                                        </td>
                                                        <td align="center">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(chargeback_ccemailid)%>"
                                                                   name="chargeback_ccemailid" placeholder="Email CC" <%=conf%> onchange="ChangeFunction(this.value,'Chargeback Contact EmailCc')">
                                                        </td>
                                                    </tr>
                                                    <tr id="mytabletr">
                                                        <td align="left"
                                                            style="background-color: #7eccad !important;color: white;">
                                                            <%=updatePartnerDetails_Refund%>&nbsp;<%=updatePartnerDetails_Contact1%>
                                                        </td>
                                                        <td align="center">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundcontactname)%>"
                                                                   name="refundcontactname" placeholder="Name" <%=conf%> onchange="ChangeFunction(this.value,'Refund Contact')">
                                                        </td>
                                                        <td align="center" class="text">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundemailid)%>"
                                                                   name="refundemailid" placeholder="Email" <%=conf%> onchange="ChangeFunction(this.value,'Refund Contact Email')">
                                                        </td>
                                                        <td align="center">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(refund_ccemailid)%>"
                                                                   name="refund_ccemailid" placeholder="Email Cc" <%=conf%> onchange="ChangeFunction(this.value,'Refund Contact EmailCc')">
                                                        </td>
                                                    </tr>
                                                    <tr id="mytabletr">
                                                        <td align="left"
                                                            style="background-color: #7eccad !important;color: white;">
                                                            <%=updatePartnerDetails_Technical%>&nbsp;<%=updatePartnerDetails_Contact1%>
                                                        </td>
                                                        <td align="center">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalcontactname)%>"
                                                                   name="technicalcontactname" placeholder="Name" <%=conf%> onchange="ChangeFunction(this.value,'Technical Contact')">
                                                        </td>
                                                        <td align="center" class="text">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalemailid)%>"
                                                                   name="technicalemailid" placeholder="Email" <%=conf%> onchange="ChangeFunction(this.value,'Technical Contact Email')">
                                                        </td>
                                                        <td align="center">
                                                            <input class="form-control" type="text" maxlength="100"
                                                                   value="<%=ESAPI.encoder().encodeForHTMLAttribute(technical_ccemailid)%>"
                                                                   name="technical_ccemailid" placeholder="Email Cc" <%=conf%> onchange="ChangeFunction(this.value,'Technical Contact Email')">
                                                        </td>
                                                    </tr>
                                                 </table>
                                            </div>
                                        </div>
                                        <div class="form-group col-md-12 has-feedback">
                                            <center>
                                                <label>&nbsp;</label>
                                                <input type="hidden" value="-" name="bussinessdevexecutive"><input
                                                    type="hidden"
                                                    value="1"
                                                    name="step">
                                                <button type="submit" class="btn btn-default" id="submit"
                                                        <%=conf%> style="display: -webkit-box;"><i
                                                        class="fa fa-save"></i>&nbsp;&nbsp;<%=updatePartnerDetails_Save%>
                                                </button>
                                            </center>
                                        </div>
                                    </div>
                                </div>
                            </div>
                    </div>
                </div>
            </div>
            </form>
        </div>
    </div>
    <%
            }
            else if (partnerFunctions.isValueNull(message))
            {
                out.println("<center><div class=\"bg-info\">" + message + "</div></center>");
            }
            else if(partnerFunctions.isValueNull(errormsg)){
                out.println("<center><div class=\"bg-infoorange\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</div></center>");
            }
        }
        else
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
    %>
</div>
</div>
</div>
</div>
</body>
</html>