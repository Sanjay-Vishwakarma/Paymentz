<%@ page import="com.directi.pg.Functions" %>
<%@ include file="top.jsp"%>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.enums.Currency" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.enums.SmsService" %>
<%--
  Created by IntelliJ IDEA.
  User: Ajit.k
  Date: 11/09/2019
  Time: 12:50 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","partnerlist");
  ResourceBundle rb1          = null;
  String language_property1   = (String) session.getAttribute("language_property");
  rb1                         = LoadProperties.getProperty(language_property1);

  String partnersignup_Partner            = StringUtils.isNotEmpty(rb1.getString("partnersignup_Partner")) ? rb1.getString("partnersignup_Partner") : "Partner";
  String partnersignup_Username           = StringUtils.isNotEmpty(rb1.getString("partnersignup_Username")) ? rb1.getString("partnersignup_Username") : "Username*";
  String partnersignup_special_characters = StringUtils.isNotEmpty(rb1.getString("partnersignup_special_characters")) ? rb1.getString("partnersignup_special_characters") : "(Username Should Not Contain Special Characters like !@#$%)";
  String partnersignup_Password           = StringUtils.isNotEmpty(rb1.getString("partnersignup_Password")) ? rb1.getString("partnersignup_Password") : "Password*";
  String partnersignup_alphabet           = StringUtils.isNotEmpty(rb1.getString("partnersignup_alphabet")) ? rb1.getString("partnersignup_alphabet") : "(Passwords length should be at least 8 and should contain alphabet, numeric, and special characters like !@#$)";
  String partnersignup_Confirm_Password   = StringUtils.isNotEmpty(rb1.getString("partnersignup_Confirm_Password")) ? rb1.getString("partnersignup_Confirm_Password") : "Confirm Password*";
  String partnersignup_same               = StringUtils.isNotEmpty(rb1.getString("partnersignup_same")) ? rb1.getString("partnersignup_same") : "(Should be same as PASSWORD)";
  String partnersignup_Partner_Organisation_Name = StringUtils.isNotEmpty(rb1.getString("partnersignup_Partner_Organisation_Name")) ? rb1.getString("partnersignup_Partner_Organisation_Name") : "Partner Organisation Name*";
  String partnersignup_Organisation_Name        = StringUtils.isNotEmpty(rb1.getString("partnersignup_Organisation_Name")) ? rb1.getString("partnersignup_Organisation_Name") : "Organisation Name";
  String partnersignup_wl_voice                 = StringUtils.isNotEmpty(rb1.getString("partnersignup_wl_voice")) ? rb1.getString("partnersignup_wl_voice") : "(Use only for WL invoice)";
  String partnersignup_Site_URL                 = StringUtils.isNotEmpty(rb1.getString("partnersignup_Site_URL")) ? rb1.getString("partnersignup_Site_URL") : "Site URL*";
  String partnersignup_http                     = StringUtils.isNotEmpty(rb1.getString("partnersignup_http")) ? rb1.getString("partnersignup_http") : "(Ex. http://www.abc.com)";
  String partnersignup_Domain                   = StringUtils.isNotEmpty(rb1.getString("partnersignup_Domain")) ? rb1.getString("partnersignup_Domain") : "Domain";
  String partnersignup_Contact_Telephone        = StringUtils.isNotEmpty(rb1.getString("partnersignup_Contact_Telephone")) ? rb1.getString("partnersignup_Contact_Telephone") : "Contact Telephone Number*";
  String partnersignup_numeric_values           = StringUtils.isNotEmpty(rb1.getString("partnersignup_numeric_values")) ? rb1.getString("partnersignup_numeric_values") : "(Accepts only Numeric values and [.+-#])";
  String partnersignup_Support_MailID           = StringUtils.isNotEmpty(rb1.getString("partnersignup_Support_MailID")) ? rb1.getString("partnersignup_Support_MailID") : "Support Mail ID*";
  String partnersignup_Admin_MailID             = StringUtils.isNotEmpty(rb1.getString("partnersignup_Admin_MailID")) ? rb1.getString("partnersignup_Admin_MailID") : "Admin Mail ID*";
  String partnersignup_Supporturl               = StringUtils.isNotEmpty(rb1.getString("partnersignup_Supporturl")) ? rb1.getString("partnersignup_Supporturl") : "Support url*";
  String partnersignup_Host_url                 = StringUtils.isNotEmpty(rb1.getString("partnersignup_Host_url")) ? rb1.getString("partnersignup_Host_url") : "Host url*";
  String partnersignup_Company_address          = StringUtils.isNotEmpty(rb1.getString("partnersignup_Company_address")) ? rb1.getString("partnersignup_Company_address") : "Company from address*";
  String partnersignup_SMTP_Host                = StringUtils.isNotEmpty(rb1.getString("partnersignup_SMTP_Host")) ? rb1.getString("partnersignup_SMTP_Host") : "SMTP Host*";
  String partnersignup_SMTP_Port                = StringUtils.isNotEmpty(rb1.getString("partnersignup_SMTP_Port")) ? rb1.getString("partnersignup_SMTP_Port") : "SMTP Port*";
  String partnersignup_Relay_With_Authorization = StringUtils.isNotEmpty(rb1.getString("partnersignup_Relay_With_Authorization")) ? rb1.getString("partnersignup_Relay_With_Authorization") : "Relay With Authorization";
  String partnersignup_Y                        = StringUtils.isNotEmpty(rb1.getString("partnersignup_Y")) ? rb1.getString("partnersignup_Y") : "Y";
  String partnersignup_N                        = StringUtils.isNotEmpty(rb1.getString("partnersignup_N")) ? rb1.getString("partnersignup_N") : "N";
  String partnersignup_SMTP_User                = StringUtils.isNotEmpty(rb1.getString("partnersignup_SMTP_User")) ? rb1.getString("partnersignup_SMTP_User") : "SMTP User*";
  String partnersignup_SMTP_Password            = StringUtils.isNotEmpty(rb1.getString("partnersignup_SMTP_Password")) ? rb1.getString("partnersignup_SMTP_Password") : "SMTP Password*";
  String partnersignup_SMS_User1                = StringUtils.isNotEmpty(rb1.getString("partnersignup_SMS_User1")) ? rb1.getString("partnersignup_SMS_User1") : "SMS User";
  String partnersignup_SMS_Password1            = StringUtils.isNotEmpty(rb1.getString("partnersignup_SMS_Password1")) ? rb1.getString("partnersignup_SMS_Password1") : "SMS Password";
  String partnersignup_From_SMS                 = StringUtils.isNotEmpty(rb1.getString("partnersignup_From_SMS")) ? rb1.getString("partnersignup_From_SMS") : "From SMS";
  String partnersignup_IP_Whitelisted           = StringUtils.isNotEmpty(rb1.getString("partnersignup_IP_Whitelisted")) ? rb1.getString("partnersignup_IP_Whitelisted") : "Is IP Whitelisted*";
  String partnersignup_Is_CardEncryption        = StringUtils.isNotEmpty(rb1.getString("partnersignup_Is_CardEncryption")) ? rb1.getString("partnersignup_Is_CardEncryption") : "Is CardEncryption Enable*";
  String partnersignup_Flight_Mode              = StringUtils.isNotEmpty(rb1.getString("partnersignup_Flight_Mode")) ? rb1.getString("partnersignup_Flight_Mode") : "Flight Mode*";
  String partnersignup_Split_Payment            = StringUtils.isNotEmpty(rb1.getString("partnersignup_Split_Payment")) ? rb1.getString("partnersignup_Split_Payment") : "Split Payment*";
  String partnersignup_Split_Payment_Type       = StringUtils.isNotEmpty(rb1.getString("partnersignup_Split_Payment_Type")) ? rb1.getString("partnersignup_Split_Payment_Type") : "Split Payment Type*";
  String partnersignup_Terminal                 = StringUtils.isNotEmpty(rb1.getString("partnersignup_Terminal")) ? rb1.getString("partnersignup_Terminal") : "Terminal";
  String partnersignup_Merchant                 = StringUtils.isNotEmpty(rb1.getString("partnersignup_Merchant")) ? rb1.getString("partnersignup_Merchant") : "Merchant";
  String partnersignup_Address_Validation       = StringUtils.isNotEmpty(rb1.getString("partnersignup_Address_Validation")) ? rb1.getString("partnersignup_Address_Validation") : "Address Validation*";
  String partnersignup_Address_Detail_Display   = StringUtils.isNotEmpty(rb1.getString("partnersignup_Address_Detail_Display")) ? rb1.getString("partnersignup_Address_Detail_Display") : "Address Detail Display*";
  String partnersignup_AutoRedirect             = StringUtils.isNotEmpty(rb1.getString("partnersignup_AutoRedirect")) ? rb1.getString("partnersignup_AutoRedirect") : "AutoRedirect*";
  String partnersignup_Template                 = StringUtils.isNotEmpty(rb1.getString("partnersignup_Template")) ? rb1.getString("partnersignup_Template") : "Template";
  String partnersignup_Checkout_Invoice         = StringUtils.isNotEmpty(rb1.getString("partnersignup_Checkout_Invoice")) ? rb1.getString("partnersignup_Checkout_Invoice") : "Checkout Invoice";
  String partnersignup_Bank_Card_Limit          = StringUtils.isNotEmpty(rb1.getString("partnersignup_Bank_Card_Limit")) ? rb1.getString("partnersignup_Bank_Card_Limit") : "Bank Card Limit";
  String partnersignup_Emi_Configuration        = StringUtils.isNotEmpty(rb1.getString("partnersignup_Emi_Configuration")) ? rb1.getString("partnersignup_Emi_Configuration") : "Emi Configuration";
  String partnersignup_Export_Transaction_Cron  = StringUtils.isNotEmpty(rb1.getString("partnersignup_Export_Transaction_Cron")) ? rb1.getString("partnersignup_Export_Transaction_Cron") : "Export Transaction Cron";
  String partnersignup_Country                  = StringUtils.isNotEmpty(rb1.getString("partnersignup_Country")) ? rb1.getString("partnersignup_Country") : "Country*";
  String partnersignup_Address                  = StringUtils.isNotEmpty(rb1.getString("partnersignup_Address")) ? rb1.getString("partnersignup_Address") : "Address";
  String partnersignup_Flight_Partner           = StringUtils.isNotEmpty(rb1.getString("partnersignup_Flight_Partner")) ? rb1.getString("partnersignup_Flight_Partner") : "Flight Partner";
  String partnersignup_Reporting_Currency       = StringUtils.isNotEmpty(rb1.getString("partnersignup_Reporting_Currency")) ? rb1.getString("partnersignup_Reporting_Currency") : "Reporting Currency";
  String partnersignup_Ip_Whitelisted_Invoice   = StringUtils.isNotEmpty(rb1.getString("partnersignup_Ip_Whitelisted_Invoice")) ? rb1.getString("partnersignup_Ip_Whitelisted_Invoice") : "Is Ip Whitelisted for Invoice";
  String partnersignup_Address_Validation_Invoice = StringUtils.isNotEmpty(rb1.getString("partnersignup_Address_Validation_Invoice")) ? rb1.getString("partnersignup_Address_Validation_Invoice") : "Address Validation For Invoice";
  String partnersignup_Default_Template_Theme     = StringUtils.isNotEmpty(rb1.getString("partnersignup_Default_Template_Theme")) ? rb1.getString("partnersignup_Default_Template_Theme") : "Default Template Theme";
  String partnersignup_Select_Theme               = StringUtils.isNotEmpty(rb1.getString("partnersignup_Select_Theme")) ? rb1.getString("partnersignup_Select_Theme") : "--Select Theme--";
  String partnersignup_PCI_Logo                   = StringUtils.isNotEmpty(rb1.getString("partnersignup_PCI_Logo")) ? rb1.getString("partnersignup_PCI_Logo") : "PCI Logo";
  String partnersignup_Bin_Service    = StringUtils.isNotEmpty(rb1.getString("partnersignup_Bin_Service")) ? rb1.getString("partnersignup_Bin_Service") : "Bin Service";
  String partnersignup_Old_Checkout   = StringUtils.isNotEmpty(rb1.getString("partnersignup_Old_Checkout")) ? rb1.getString("partnersignup_Old_Checkout") : "Is Old Checkout Template";
  String partnersignup_Email_Sent     = StringUtils.isNotEmpty(rb1.getString("partnersignup_Email_Sent")) ? rb1.getString("partnersignup_Email_Sent") : "Is Email Sent";
  String partnersignup_Support_Email  = StringUtils.isNotEmpty(rb1.getString("partnersignup_Support_Email")) ? rb1.getString("partnersignup_Support_Email") : "Support Email for Transaction Mail";
  String partnersignup_TermsURL       = StringUtils.isNotEmpty(rb1.getString("partnersignup_TermsURL")) ? rb1.getString("partnersignup_TermsURL") : "TermsURL";
  String partnersignup_PrivacyURL     = StringUtils.isNotEmpty(rb1.getString("partnersignup_PrivacyURL")) ? rb1.getString("partnersignup_PrivacyURL") : "PrivacyURL";
  String partnersignup_CookiesUrlURL  = StringUtils.isNotEmpty(rb1.getString("partnersignup_CookiesUrlURL")) ? rb1.getString("partnersignup_CookiesUrlURL") : "CookiesUrlURL";
  String partnersignup_Contact_Details  = StringUtils.isNotEmpty(rb1.getString("partnersignup_Contact_Details")) ? rb1.getString("partnersignup_Contact_Details") : "Contact Details";
  String partnersignup_Main             = StringUtils.isNotEmpty(rb1.getString("partnersignup_Main")) ? rb1.getString("partnersignup_Main") : "Main";
  String partnersignup_Contact          = StringUtils.isNotEmpty(rb1.getString("partnersignup_Contact")) ? rb1.getString("partnersignup_Contact") : "Contact*:";
  String partnersignup_Contact1         = StringUtils.isNotEmpty(rb1.getString("partnersignup_Contact1")) ? rb1.getString("partnersignup_Contact1") : "Contact:";
  String partnersignup_Save             = StringUtils.isNotEmpty(rb1.getString("partnersignup_Save")) ? rb1.getString("partnersignup_Save") : "Save";
  String partnersignup_Sales            = StringUtils.isNotEmpty(rb1.getString("partnersignup_Sales")) ? rb1.getString("partnersignup_Sales") : "Sales";
  String partnersignup_Billing          = StringUtils.isNotEmpty(rb1.getString("partnersignup_Billing")) ? rb1.getString("partnersignup_Billing") : "Billing";
  String partnersignup_Notify           = StringUtils.isNotEmpty(rb1.getString("partnersignup_Notify")) ? rb1.getString("partnersignup_Notify") : "Notify";
  String partnersignup_Fraud            = StringUtils.isNotEmpty(rb1.getString("partnersignup_Fraud")) ? rb1.getString("partnersignup_Fraud") : "Fraud";
  String partnersignup_Chargeback       = StringUtils.isNotEmpty(rb1.getString("partnersignup_Chargeback")) ? rb1.getString("partnersignup_Chargeback") : "Chargeback";
  String partnersignup_Refund           = StringUtils.isNotEmpty(rb1.getString("partnersignup_Refund")) ? rb1.getString("partnersignup_Refund") : "Refund";
  String partnersignup_Technical        = StringUtils.isNotEmpty(rb1.getString("partnersignup_Technical")) ? rb1.getString("partnersignup_Technical") : "Technical";
  String partnersignup_kindly           = StringUtils.isNotEmpty(rb1.getString("partnersignup_kindly")) ? rb1.getString("partnersignup_kindly") : "KINDLY KEEP JAVASCRIPT ENABLED IN YOUR BROWSER FOR SECURITY PURPOSES.";
%>
<html>
<head>
  <title><%=company%> | Partner SignUp</title>
  <script type="text/javascript" src="/partner/javascript/jquery.jcryption.js?ver=1"></script>
  <script>

    $(document).ready(function(){

      var w = $(window).width();

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
    });

    $(document).ready(function() {
      document.getElementById('btnsubmit').disabled =  false;

      $("#btnsubmit").click(function() {
        var encryptedString1 =  $.jCryption.encrypt($("#passwd").val(), $("#ctoken").val());
        document.getElementById('passwd').value =  encryptedString1;

        var encryptedString2 =  $.jCryption.encrypt($("#conpasswd").val(), $("#ctoken").val());
        document.getElementById('conpasswd').value =  encryptedString2;

        document.getElementById('isEncrypted').value =  true;
      });

      $('#yesSubmit').click(function(){
        document.getElementById('sendEmailNotification').value =  "Y";
        $('#form1').submit();
      });
      $('#noSubmit').click(function(){
        document.getElementById('sendEmailNotification').value =  "N";
        $('#form1').submit();
      });

    });

    $(document).ready(function(){
      var relaywithauth = document.getElementById("relaywithauth").value;
      console.log(relaywithauth);
      if (relaywithauth.value == 'Y') {
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

      /*table tr {
          padding-top: 20px;
          padding-bottom: 20px;
          display: block;
      }*/

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

    .table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td{border-top: 1px solid #ddd;}

    /********************Table Responsive Ends**************************/

    @media (min-width: 768px){
      .form-horizontal .control-label {
        text-align: left!important;
      }
    }

  </style>
  <style type="text/css">
    .field-icon
    {
      float:  right;
      margin-top: -25px;
      position: relative;
      z-index: 2;
    }
  </style>
</head>
<body>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/partnerlist.jsp?ctoken=<%=ctoken%>" method="post" name="form">
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
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=company%> <%=partnersignup_Partner%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <br><%--<br><br>

            <div align="center" class="textb"><h5><b><u>Merchant Information</u></b></h5></div>--%>
            <%

              HashMap<String, String> timezoneHash = Functions.getTimeZone();

              String partnerId = (String)session.getAttribute("merchantid");
              String partnername = (String)session.getAttribute("partnername");
              Map<String, String> ynMap = new LinkedHashMap<String, String>();
              ynMap.put("N", "No");
              ynMap.put("Y", "Yes");
              Hashtable details = (Hashtable) request.getAttribute("details");
              String username = "";
              String passwd = "";
              String conpasswd = "";
              String tpasswd = "";
              String contpasswd = "";
              String company_name = "";
              String partnerNameForWLInvoice = "";
              //String company_type = "";
              String brandname = "";
              String sitename = "";
              String domain = "";
              String relayWithAuth = "";
              String protocol = "";
              String contact_emails = "";
              String contact_persons = "";
              String telno = "";
              //String logo = "";
              String faxno = "";
              String address = "";
              String city = "";
              String state = "";
              String country = "";
              String zip = "";
              String notifyemail = "";
              String potentialbusiness = "";
              String fraudemailId = "";
              String hostUrl = "";
              String companyfromaddress = "";
              String supportfromaddress = "";
              String fraudemail = "";
              String smtp_host = "";
              String smtp_port = "";
              String smtp_user = "";
              String smtp_password = "";
              String sms_user = "";
              String sms_password = "";
              String from_sms = "";
              String hosturl = "";
              String supportmailid = "";
              String adminmailid = "";
              String supporturl = "";
              String documentationurl = "";
              String salesemail = "";
              String billingemail = "";

              String notifyContactName = "";
              String billingContactName = "";
              String salesContactName = "";
              String fraudContactName = "";
              String cbConatctName = "";
              String cbConatctMailId = "";
              String rfContactName = "";
              String rfConatctMailId = "";
              String techConatactName = "";
              String techContactMailId = "";
              String supportEmailForTransactionMail = "";

              String contact_ccEmail = "";
              String sale_ccEmail = "";
              String billing_ccEmail = "";
              String notify_ccEmail = "";
              String fraud_ccEmail = "";
              String chargeback_ccEmail = "";
              String refund_ccEmail = "";
              String technical_ccEmail = "";

              String defaulttheme = "";
              String termsurl = "";
              String privacyurl = "";
              String cookiesurl="";

              PartnerDAO partnerDAO = new PartnerDAO();
              Functions functions = new Functions();
              LinkedHashMap<String, Integer> thememap = partnerDAO.listDefaulttheme();
              String isipwhitelisted = request.getParameter("isipwhitelisted") == null ? "" : request.getParameter("isipwhitelisted");
              String iscardencryptionenable = request.getParameter("iscardencryptionenable") == null ? "" : request.getParameter("iscardencryptionenable");
              String splitpayment = request.getParameter("splitpayment") == null ? "" : request.getParameter("splitpayment");
              String splitpaymenttype = request.getParameter("splitpaymenttype") == null ? "" : request.getParameter("splitpaymenttype");
              String addressvalidation = request.getParameter("addressvalidation") == null ? "" : request.getParameter("addressvalidation");
              String addressdetaildisplay = request.getParameter("addressdetaildisplay") == null ? "" : request.getParameter("addressdetaildisplay");
              String autoRedirect = request.getParameter("autoRedirect") == null ? "" : request.getParameter("autoRedirect");
              String flightMode = request.getParameter("flightMode") == null ? "" : request.getParameter("flightMode");
              String template = request.getParameter("template") == null ? "" : request.getParameter("template");
              String checkoutInvoice = request.getParameter("checkoutInvoice") == null ? "" : request.getParameter("checkoutInvoice");
              String isflightPartner = request.getParameter("isflightPartner") == null ? "" : request.getParameter("isflightPartner");
              String ip_whitelist_invoice = request.getParameter("ip_whitelist_invoice") == null ? "" : request.getParameter("ip_whitelist_invoice");
              String address_validation_invoice = request.getParameter("address_validation_invoice") == null ? "" : request.getParameter("address_validation_invoice");
              String ispcilogo = request.getParameter("ispcilogo") == null ? "" : request.getParameter("ispcilogo");
              String binService = request.getParameter("binService") == null ? "" : request.getParameter("binService");
              String oldcheckout=request.getParameter("oldcheckout") == null ? "" : request.getParameter("oldcheckout");
              /*String emailSent = request.getParameter("emailSent") == null ? "" : request.getParameter("emailSent");*/

              String bankCardLimit = request.getParameter("bankCardLimit") == null ? "" : request.getParameter("bankCardLimit");
              String emi_configuration = request.getParameter("emi_configuration") == null ? "" : request.getParameter("emi_configuration");
              String exportTransactionCron = request.getParameter("exportTransactionCron") == null ? "" : request.getParameter("exportTransactionCron");
              String role = request.getParameter("role") == null ? "" : request.getParameter("role");
              String errormsg = ESAPI.encoder().encodeForHTML((String) request.getAttribute("error"));


              if (errormsg == null)
              {
                errormsg = "";
              }
              if (request.getParameter("MES") != null)
              {
                String mes = request.getParameter("MES");

                if (details.get("login") != null) username = (String) details.get("login");
                if (details.get("partnerName") != null) company_name = (String) details.get("partnerName");
                if (details.get("partnerNameForWLInvoice") != null)
                  partnerNameForWLInvoice = (String) details.get("partnerNameForWLInvoice");
                if (details.get("sitename") != null) sitename = (String) details.get("sitename");
                if (details.get("domain") != null) domain = (String) details.get("domain");
                if (details.get("relaywithauth") != null) relayWithAuth = (String) details.get("relaywithauth");
                if (details.get("protocol") != null) protocol = (String) details.get("protocol");
                if (details.get("contact_emails") != null) contact_emails = (String) details.get("contact_emails");
                if (details.get("contact_persons") != null)
                  contact_persons = (String) details.get("contact_persons");
                if (details.get("telno") != null) telno = (String) details.get("telno");
                if (details.get("country") != null) country = (String) details.get("country");
                if (details.get("address") != null) address = (String) details.get("address");
                if (details.get("notifyemail") != null) notifyemail = (String) details.get("notifyemail");
                if (details.get("fraudemailid") != null) fraudemailId = (String) details.get("fraudemailid");
                if (details.get("companysupportmailid") != null)
                  supportmailid = (String) details.get("companysupportmailid");
                if (details.get("adminmailid") != null) adminmailid = (String) details.get("adminmailid");
                if (details.get("supporturl") != null) supporturl = (String) details.get("supporturl");
                if (details.get("documentationurl") != null) documentationurl = (String) details.get("documentationurl");
                if (details.get("salesemail") != null) salesemail = (String) details.get("salesemail");
                if (details.get("billingemail") != null) billingemail = (String) details.get("billingemail");
                if (details.get("notifyemail") != null) notifyemail = (String) details.get("notifyemail");
                if (details.get("companyfromaddress") != null)
                  companyfromaddress = (String) details.get("companyfromaddress");
                if (details.get("supportfromaddress") != null)
                  supportfromaddress = (String) details.get("supportfromaddress");
                if (details.get("fraudemail") != null) fraudemail = (String) details.get("fraudemail");
                if (details.get("smtp_host") != null) smtp_host = (String) details.get("smtp_host");
                if (details.get("smtp_port") != null) smtp_port = (String) details.get("smtp_port");
                if (details.get("smtp_user") != null) smtp_user = (String) details.get("smtp_user");
                if (details.get("smtp_password") != null) smtp_password = (String) details.get("smtp_password");
                if (details.get("sms_user") != null) sms_user = (String) details.get("sms_user");
                if (details.get("sms_password") != null) sms_password = (String) details.get("sms_password");
                if (details.get("from_sms") != null) from_sms = (String) details.get("from_sms");
                if (details.get("hosturl") != null) hosturl = (String) details.get("hosturl");
                if (details.get("salescontactname") != null)
                  salesContactName = (String) details.get("salescontactname");
                if (details.get("fraudcontactname") != null)
                  fraudContactName = (String) details.get("fraudcontactname");
                if (details.get("technicalemailid") != null)
                  techContactMailId = (String) details.get("technicalemailid");
                if (details.get("technicalcontactname") != null)
                  techConatactName = (String) details.get("technicalcontactname");
                if (details.get("chargebackcontactname") != null)
                  cbConatctName = (String) details.get("chargebackcontactname");
                if (details.get("chargebackemailid") != null)
                  cbConatctMailId = (String) details.get("chargebackemailid");
                if (details.get("refundemailid") != null) rfConatctMailId = (String) details.get("refundemailid");
                if (details.get("refundcontactname") != null)
                  rfContactName = (String) details.get("refundcontactname");
                if (details.get("billingcontactname") != null)
                  billingContactName = (String) details.get("billingcontactname");
                if (details.get("notifycontactname") != null)
                  notifyContactName = (String) details.get("notifycontactname");
                if (details.get("defaulttemplatetheme") != null)
                  defaulttheme = (String) details.get("defaulttemplatetheme");  //Addedd for default theme
                if (details.get("supportemailfortransactionmail") != null)
                  supportEmailForTransactionMail = (String) details.get("supportemailfortransactionmail");
                if (details.get("termsUrl") != null)
                  termsurl = (String) details.get("termsUrl");
                if (details.get("privacyUrl") != null)
                  privacyurl = (String) details.get("privacyUrl");
                if (details.get("cookiesUrl") != null)
                  cookiesurl = (String) details.get("cookiesUrl");

                if (details.get("contact_ccmailid") != null)
                  contact_ccEmail = (String) details.get("contact_ccmailid");
                if (details.get("sales_ccemailid") != null) sale_ccEmail = (String) details.get("sales_ccemailid");
                if (details.get("billing_ccemailid") != null)
                  billing_ccEmail = (String) details.get("billing_ccemailid");
                if (details.get("notify_ccemailid") != null)
                  notify_ccEmail = (String) details.get("notify_ccemailid");
                if (details.get("fraud_ccemailid") != null) fraud_ccEmail = (String) details.get("fraud_ccemailid");
                if (details.get("chargeback_ccemailid") != null)
                  chargeback_ccEmail = (String) details.get("chargeback_ccemailid");
                if (details.get("refund_ccemailid") != null)
                  refund_ccEmail = (String) details.get("refund_ccemailid");
                if (details.get("technical_ccemailid") != null)
                  technical_ccEmail = (String) details.get("technical_ccemailid");
                if (details.get("bankCardLimit") != null)
                  bankCardLimit = (String) details.get("bankCardLimit");
                if (details.get("emi_configuration") != null)
                  emi_configuration = (String) details.get("emi_configuration");
                if (details.get("role") != null)
                  emi_configuration = (String) details.get("role");
                if (mes.equals("F"))
                {
                  out.println("<table align=\"center\" width=\"60%\" ><tr><td><font class=\"textb\" >You have <b>NOT FILLED</b> some of required details or some of details filled by you are incorrect. Please fill all the details completely before going for next step.</font>");
                  out.println("</td></tr><tr><td algin=\"center\" class=\"bg-infoorange\" style=\"text-align: center; ><font class=\"textb\"  size=\"2\">");
                  errormsg = errormsg.replace("&lt;BR&gt;", "<BR>");
                  out.println(errormsg);
                  out.println("</font>");
                  out.println("</td></tr></table>");

                }
              }
            %>


            <form action="/partner/net/NewPartner?ctoken=<%=ctoken%>" method="post" name="form1" id="form1" class="form-horizontal">
              <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
              <input id="isEncrypted" name="isEncrypted" type="hidden" value="false" >
              <input name="partner_id" type="hidden" value="<%=partnerId%>" >
              <input name="partner_Name" type="hidden" value="<%=company%>" >
              <input type="hidden" id="sendEmailNotification" name="sendEmailNotification" value="">
              <script type="text/javascript" src="/merchant/javascript/jquery.jcryption.js?ver=1"></script>
              <script language="javascript">

                function myjunk()
                {

                  var hat = this.document.form1.country.selectedIndex;
                  var hatto = this.document.form1.country.options[hat].value;
                  var countrycd = this.document.form1.phonecc.value = hatto.split("|")[1];
                  var telnumb = this.document.form1.telno.value;
                  // var cctel = countrycd.concat(telnumb);
                  if (hatto != 'Select one') {

                    this.document.form1.countrycode.value = hatto.split("|")[0];
                    this.document.form1.phonecc.value = hatto.split("|")[1];
                    this.document.form1.country.options[0].selected=false;

                  }


                }


              </script>

              <div class="widget-content padding">
                <div class="content">
                  <div class="modal" id="myModal" role="dialog">
                    <div id="target" class="modal-dialog" >
                      <div class="modal-content">

                        <div class="header">
                          <div class="logo">
                            <h4 class="modal-title">Send Notification</h4>
                          </div>
                        </div>

                        <div class="modal-body">
                          <p>Send email notification to Partner’s Main contact?.</p>
                        </div>
                        <div class="modal-footer" >
                          <button type="button" id="yesSubmit" class="btn btn-default" data-dismiss="modal">Yes</button>
                          <button type="button" id="noSubmit" class="btn btn-default" data-dismiss="modal">No</button>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Username%><br>
                    <%=partnersignup_special_characters%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100"  maxlength = 100 value="<%=ESAPI.encoder().encodeForHTMLAttribute(username)%>" name="username" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Password%><br>
                    <%=partnersignup_alphabet%></label>
                  <div class="col-md-4">
                    <input id="passwd" class="form-control" type="Password" maxlength="125"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(passwd)%>" name="passwd" size="35" autocomplete="off" readonly onfocus="this.removeAttribute('readonly');">
                    <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="showHidepass" onclick="hideshowpass('showHidepass','passwd')"></span>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Confirm_Password%><br>
                    <%=partnersignup_same%></label>
                  <div class="col-md-4">
                    <input id="conpasswd" class="form-control" type="Password" maxlength="125" value="<%=ESAPI.encoder().encodeForHTMLAttribute(conpasswd)%>" name="conpasswd" size="35" autocomplete="off" readonly onfocus="this.removeAttribute('readonly');">
                    <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="conshowHidepass" onclick="hideshowpass('conshowHidepass','conpasswd')"></span>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Partner_Organisation_Name%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(company_name)%>" name="partnerName" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Organisation_Name%><span class="textb"></span><br>
                    <%=partnersignup_wl_voice%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(partnerNameForWLInvoice)%>" name="partnerNameForWLInvoice" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Site_URL%><br>
                    <%=partnersignup_http%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(sitename)%>" name="sitename" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>
                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Domain%><br>
                    <%=partnersignup_http%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="1000"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(domain)%>" name="domain" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Contact_Telephone%><br><%=partnersignup_numeric_values%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="text" maxlength="20"  name="telno" size="35" value="<%=ESAPI.encoder().encodeForHTMLAttribute(telno)%>" >
                  </div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Support_MailID%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(supportmailid)%>" name="companysupportmailid" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>
                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Admin_MailID%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(adminmailid)%>" name="adminmailid" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Supporturl%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(supporturl)%>" name="supporturl" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label">Documentation url*</label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(documentationurl)%>" name="documentationurl" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Host_url%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(hosturl)%>" name="hosturl" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Company_address%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(companyfromaddress)%>" name="companyfromaddress" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <%--<div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label">Support from address*</label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(supportfromaddress)%>" name="supportfromaddress" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>--%>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_SMTP_Host%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(smtp_host)%>" name="smtp_host" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_SMTP_Port%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(smtp_port)%>" name="smtp_port" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Relay_With_Authorization%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="relaywithauth" id="relaywithauth" onchange="updatetextbox(relaywithauth)">
                      <%
                        if ("Y".equals(relayWithAuth))
                        {
                      %>
                      <option value="Y" selected><%=partnersignup_Y%></option>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <option value="Y"><%=partnersignup_Y%></option>
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
                    <select class="form-control" name="protocol" >
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
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(smtp_user)%>" name="smtp_user" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_SMTP_Password%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="password" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(smtp_password)%>" name="smtp_password" id="smtp_password" size="35"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="smtp" onclick="hideshowpass('smtp','smtp_password')"></span>
                    <input type="hidden"
                           value="<%=ESAPI.encoder().encodeForHTMLAttribute(smtp_password)%>"
                           name="smtp_password" >
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_SMS_User1%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(sms_user)%>" name="sms_user" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_SMS_Password1%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="password" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(sms_password)%>" name="sms_password" id="sms_password" size="35" autocomplete="off" readonly onfocus="this.removeAttribute('readonly');">
                    <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="sms" onclick="hideshowpass('sms','sms_password')"></span>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_From_SMS%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(from_sms)%>" name="from_sms" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_IP_Whitelisted%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="isipwhitelisted">
                      <%
                        if ("Y".equals(isipwhitelisted))
                        {
                      %>
                      <option value="Y" selected><%=partnersignup_Y%></option>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <option value="Y"><%=partnersignup_Y%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Is_CardEncryption%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="iscardencryptionenable">
                      <%
                        if ("Y".equals(iscardencryptionenable))
                        {
                      %>
                      <option value="Y" selected><%=partnersignup_Y%></option>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <option value="Y"><%=partnersignup_Y%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Flight_Mode%></label>
                  <div class="col-md-4">
                    <select class="form-control"
                            name="flightMode" id="flightid" onchange="hidblock(flightid)">
                      <%
                        if ("Y".equals(flightMode))
                        {
                      %>
                      <option value="Y" selected><%=partnersignup_Y%></option>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <option value="Y"><%=partnersignup_Y%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div id="div_split" <%--please do not remove this div--%>>
                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Split_Payment%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="splitpayment">
                      <%
                        if ("Y".equals(splitpayment))
                        {
                      %>
                      <option value="Y" selected><%=partnersignup_Y%></option>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <option value="Y"><%=partnersignup_Y%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Split_Payment_Type%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="splitpaymenttype">
                      <%
                        if ("Terminal".equals(splitpaymenttype))
                        {
                      %>
                      <option value="Terminal" selected><%=partnersignup_Terminal%></option>
                      <option value="Merchant"><%=partnersignup_Merchant%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="Merchant" selected><%=partnersignup_Merchant%></option>
                      <option value="Terminal"><%=partnersignup_Terminal%></option>
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
                  <label class="col-md-4 control-label"><%=partnersignup_Address_Validation%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="addressvalidation">
                      <%
                        if ("Y".equals(addressvalidation))
                        {
                      %>
                      <option value="Y" selected><%=partnersignup_Y%></option>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <option value="Y"><%=partnersignup_Y%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Address_Detail_Display%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="addressdetaildisplay">
                      <%
                        if ("Y".equals(addressdetaildisplay))
                        {
                      %>
                      <option value="Y" selected><%=partnersignup_Y%></option>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <option value="Y"><%=partnersignup_Y%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_AutoRedirect%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="autoRedirect">
                      <%
                        if ("Y".equals(autoRedirect))
                        {
                      %>
                      <option value="Y" selected><%=partnersignup_Y%></option>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <option value="Y"><%=partnersignup_Y%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>



                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Template%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="template">
                      <%
                        if ("Y".equals(template))
                        {
                      %>
                      <option value="Y" selected><%=partnersignup_Y%></option>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <option value="Y"><%=partnersignup_Y%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Checkout_Invoice%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="checkoutInvoice">
                      <%
                        if ("Y".equals(checkoutInvoice))
                        {
                      %>
                      <option value="Y" selected><%=partnersignup_Y%></option>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <option value="Y"><%=partnersignup_Y%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Bank_Card_Limit%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="bankCardLimit">
                      <%
                        if ("Y".equals(bankCardLimit))
                        {
                      %>
                      <option value="Y" selected><%=partnersignup_Y%></option>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <option value="Y"><%=partnersignup_Y%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6">
                    <input type="hidden" value="N" name="emi_configuration" >
                  </div>
                </div>

                <%--<div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Emi_Configuration%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="emi_configuration">
                      <%
                        if ("Y".equals(emi_configuration))
                        {
                      %>
                      <option value="Y" selected><%=partnersignup_Y%></option>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <option value="Y"><%=partnersignup_Y%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>--%>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Export_Transaction_Cron%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="exportTransactionCron">
                      <%if("N".equalsIgnoreCase(exportTransactionCron)){%>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <%}else{%>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                        }
                        Set timezoneSet = timezoneHash.keySet();
                        Iterator itr = timezoneSet.iterator();
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
                  <label class="col-md-4 control-label"><%=partnersignup_Country%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>" name="country" size="35">
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
                  <label class="col-md-4 control-label"><%=partnersignup_Address%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(address)%>" name="address" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Flight_Partner%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="isflightPartner">
                      <%
                        if ("Y".equals(isflightPartner))
                        {
                      %>
                      <option value="Y" selected><%=partnersignup_Y%></option>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <option value="Y"><%=partnersignup_Y%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Reporting_Currency%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="currency" class="txtbox">
                      </option>
                      <%
                        for (Currency currency : Currency.values())
                        {
                          String selected = "";
                          if (currency.toString().equals(ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("currency"))))
                          {
                            selected = "selected";
                          }
                          out.println("<option value=\"" + currency.toString() + "\" " + selected + ">" + currency.toString() + "</option>");
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label">SMS Service</label>
                  <div class="col-md-4">
                    <select class="form-control" name="sms_service" class="txtbox">
                      </option>
                      <%
                        for (SmsService smsService:SmsService.values())
                        {
                          String selected = "";
                          if (smsService.toString().equals(ESAPI.encoder().encodeForHTMLAttribute(request.getParameter("sms_service"))))
                          {
                            selected = "selected";
                          }
                          out.println("<option value=\"" + smsService.toString() + "\" " + selected + ">" + smsService.toString() + "</option>");
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>
                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Ip_Whitelisted_Invoice%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="ip_whitelist_invoice">
                      <%
                        if ("Y".equals(ip_whitelist_invoice))
                        {
                      %>
                      <option value="Y" selected><%=partnersignup_Y%></option>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <option value="Y"><%=partnersignup_Y%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Address_Validation_Invoice%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="address_validation_invoice">
                      <%
                        if ("Y".equals(address_validation_invoice))
                        {
                      %>
                      <option value="Y" selected><%=partnersignup_Y%></option>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <option value="Y"><%=partnersignup_Y%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Default_Template_Theme%></label>
                  <div class="col-md-4">
                    <select name="defaulttemplatetheme" class="form-control">
                      <option value="" selected><%=partnersignup_Select_Theme%></option>
                      <%
                        for (String themename : thememap.keySet())
                        {
                          String isSelected = "";
                          if (String.valueOf(themename).equalsIgnoreCase(defaulttheme))
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
                  <label class="col-md-4 control-label"><%=partnersignup_PCI_Logo%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="ispcilogo">
                      <%
                        if ("Y".equals(ispcilogo))
                        {
                      %>
                      <option value="Y" selected><%=partnersignup_Y%></option>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <option value="Y"><%=partnersignup_Y%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Bin_Service%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="binService">
                      <%
                        if ("Y".equals(binService))
                        {
                      %>
                      <option value="Y" selected><%=partnersignup_Y%></option>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <option value="Y"><%=partnersignup_Y%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

             <%--   <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Old_Checkout%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="oldcheckout">
                      <%
                        if ("Y".equals(oldcheckout))
                        {
                      %>
                      <option value="Y" selected><%=partnersignup_Y%></option>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <option value="Y"><%=partnersignup_Y%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>--%>


                <%--<div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Email_Sent%></label>
                  <div class="col-md-4">
                    <select class="form-control" name="emailSent">
                      <%
                        if ("N".equals(emailSent))
                        {
                      %>
                      <option value="N" selected><%=partnersignup_N%></option>
                      <option value="Y"><%=partnersignup_Y%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="Y" selected><%=partnersignup_Y%></option>
                      <option value="N"><%=partnersignup_N%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>--%>


                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_Support_Email%></label>
                  <div class="col-md-4">
                    <select name="supportemailfortransactionmail" class="form-control">
                      <%
                        if ("Partner".equals(supportEmailForTransactionMail))
                        {
                      %>
                      <option value="Partner" selected><%=partnersignup_Partner%></option>
                      <option value="Merchant"><%=partnersignup_Merchant%></option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="Merchant" selected><%=partnersignup_Merchant%></option>
                      <option value="Partner"><%=partnersignup_Partner%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_TermsURL%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(termsurl)%>" name="termsUrl" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_PrivacyURL%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(privacyurl)%>" name="privacyUrl" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="form-group">
                  <div class="col-md-2"></div>
                  <label class="col-md-4 control-label"><%=partnersignup_CookiesUrlURL%></label>
                  <div class="col-md-4">
                    <input class="form-control" type="Text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cookiesurl)%>" name="cookiesUrl" size="35">
                  </div>
                  <div class="col-md-6"></div>
                </div>

                <div class="widget-header transparent">
                  <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=partnersignup_Contact_Details%></strong></h2>
                  <div class="additional-btn">
                  </div>
                </div>

                <div class="widget-content padding">
                  <div id="horizontal-form">                        <%-- End Radio Button--%>
                    <div class="form-group col-md-12 has-feedback">
                      <div class="table-responsive">

                        <%-- <table align="center" width="90%" border="1">--%>
                        <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                          <tr id="mytabletr">
                            <td align="left" style="background-color: #7eccad !important;color: white;"><%=partnersignup_Main%>&nbsp;<%=partnersignup_Contact%></td>
                            <td align="center">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_persons)%>" name="contact_persons" placeholder="Name*" >
                            </td>
                            <td align="center">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_emails)%>" name="contact_emails" placeholder="Email*" >
                            </td>
                            <td align="center">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_ccEmail)%>" name="contact_ccmailid" placeholder="Email Cc"  >
                            </td>
                          </tr>

                          <tr id="mytabletr">
                            <td align="left" style="background-color: #7eccad !important;color: white;"><%=partnersignup_Sales%>&nbsp;<%=partnersignup_Contact%></td>
                            <td align="center">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContactName)%>" name="salescontactname" placeholder="Name*">
                            </td>
                            <td align="center">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesemail)%>" name="salesemail" placeholder="Email*">
                            </td>
                            <td align="center">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(sale_ccEmail)%>" name="sales_ccemailid" placeholder="Email Cc" >
                            </td>
                            <td align="center">
                            </td>
                          </tr>
                          <tr id="mytabletr">
                            <td align="left" style="background-color: #7eccad !important;color: white;"><%=partnersignup_Billing%>&nbsp;<%=partnersignup_Contact%></td>
                            <td align="center">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContactName)%>" name="billingcontactname" placeholder="Name*" >
                            </td>
                            <td align="center">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingemail)%>"name="billingemail" placeholder="Email*" >
                            </td>
                            <td align="center">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billing_ccEmail)%>" name="billing_ccemailid" placeholder="Email Cc"  >
                            </td>

                          </tr>
                          <tr id="mytabletr">
                            <td align="left" style="background-color: #7eccad !important;color: white;"><%=partnersignup_Notify%>&nbsp;<%=partnersignup_Contact%></td>
                            <td align="center">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(notifyContactName)%>" name="notifycontactname" placeholder="Name*" >
                            </td>
                            <td align="center"class="text">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(notifyemail)%>"name="notifyemail" placeholder="Email*" >
                            </td>
                            <td align="center" >
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(notify_ccEmail)%>" name="notify_ccemailid" placeholder="Email Cc"  >
                            </td>
                          </tr>
                          <tr id="mytabletr">
                            <td align="left" style="background-color: #7eccad !important;color: white;"><%=partnersignup_Fraud%>&nbsp;<%=partnersignup_Contact%></td>
                            <td align="center">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContactName)%>" name="fraudcontactname" placeholder="Name*" >
                            </td>
                            <td align="center" class="text">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudemail)%>"name="fraudemail" placeholder="Email*" >
                            </td>
                            <td align="center">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraud_ccEmail)%>" name="fraud_ccemailid" placeholder="Email Cc"  >
                            </td>
                          </tr>
                          <tr id="mytabletr">
                            <td align="left" style="background-color: #7eccad !important;color: white;"><%=partnersignup_Chargeback%>&nbsp;<%=partnersignup_Contact1%></td>
                            <td align="center">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbConatctName)%>" name="chargebackcontactname" placeholder="Name" >
                            </td>
                            <td align="center"class="text">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbConatctMailId)%>"name="chargebackemailid" placeholder="Email" >
                            </td>
                            <td align="center">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(chargeback_ccEmail)%>" name="chargeback_ccemailid" placeholder="Email CC"  >
                            </td>
                          </tr>
                          <tr id="mytabletr">
                            <td align="left" style="background-color: #7eccad !important;color: white;"><%=partnersignup_Refund%>&nbsp;<%=partnersignup_Contact1%></td>
                            <td align="center">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(rfContactName)%>" name="refundcontactname" placeholder="Name" >
                            </td>
                            <td align="center"class="text">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(rfConatctMailId)%>" name="refundemailid" placeholder="Email" >
                            </td>
                            <td align="center">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refund_ccEmail)%>" name="refund_ccemailid" placeholder="Email Cc"  >
                            </td>
                          </tr>
                          <tr id="mytabletr">
                            <td align="left" style="background-color: #7eccad !important;color: white;"><%=partnersignup_Technical%>&nbsp;<%=partnersignup_Contact1%></td>
                            <td align="center">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(techConatactName)%>" name="technicalcontactname" placeholder="Name" >
                            </td>
                            <td align="center"class="text">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(techContactMailId)%>" name="technicalemailid" placeholder="Email" >
                            </td>
                            <td align="center">
                              <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technical_ccEmail)%>" name="technical_ccemailid" placeholder="Email Cc"  >
                            </td>
                          </tr>
                        </table>
                      </div>
                    </div>
                    <div class="form-group col-md-12 has-feedback">
                      <center>
                        <label >&nbsp;</label>
                        <input type="hidden" value="-" name="bussinessdevexecutive"><input type="hidden"
                                                                                           value="1"
                                                                                           name="step">
                       <%-- <button type="submit" id="btnsubmit" class="btn btn-default"  disabled="disabled"  style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=partnersignup_Save%></button>--%>
                        <button type="button" id="btnsubmit" class="btn btn-default"  style="display: -webkit-box;" data-toggle="modal" data-target="#myModal"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=partnersignup_Save%></button>
                      </center>
                    </div>
                  </div>
                </div>
              </div>
            </form>
          </div>
          <h5 class="bg-infoorange" style="text-align: center;"><i class="fa fa-exclamation-triangle"></i>&nbsp;&nbsp;<%=partnersignup_kindly%></h5>
        </div>
      </div>
    </div>
  </div>
</div>

</body>
</html>