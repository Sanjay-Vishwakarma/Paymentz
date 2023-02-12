<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.dao.PartnerDAO" %>
<%@ page import="com.manager.enums.Currency" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.manager.enums.SmsService" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 11/26/13
  Time: 2:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="functions.jsp" %>
<%@ include file="index.jsp" %>
<html>
<head>
    <title> WL PSP Setup</title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script type="text/javascript" src="/icici/javascript/jquery.jcryption.js"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
    <script type="text/javascript">
        $(document).ready(function(){
            var relaywithauth = document.getElementById("relaywithauth").value;
            console.log(relaywithauth);
            if (relaywithauth.value == 'Y') {
                document.getElementById("smtp_password").disabled=false;
            }
            else
            {
                document.getElementById("smtp_password").disabled=true;
            }

            $('#yesSubmit').click(function(){
                console.log("yesSubmit")
                document.getElementById('sendEmailNotification').value =  "Y";
                document.getElementById('submit').type = 'submit';
                var pagebutton= document.getElementById("submit");
                pagebutton.click();
            });
            $('#noSubmit').click(function(){
                console.log("noSubmit")
                document.getElementById('sendEmailNotification').value =  "N";
                document.getElementById('submit').type = 'submit';
                var pagebutton= document.getElementById("submit");
                pagebutton.click();
            });
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

</head>
<%
    Logger logger       = new Logger("partnersignup.jsp");
    ctoken              = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>
<body class="bodybackground">
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                New Partner Signup Form
                <div style="float: right;">
                    <form action="/icici/partnerInterface.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" name="submit" class="addnewmember">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Partner Details
                        </button>
                    </form>
                </div>
            </div>

            <%
                HashMap<String, String> timezoneHash = Functions.getTimeZone();

                Map<String, String> ynMap = new LinkedHashMap<String, String>();
                ynMap.put("N", "No");
                ynMap.put("Y", "Yes");

                Map<String,String> erjbMap= new LinkedHashMap<String, String>();
                erjbMap.put("RO","RO");
                erjbMap.put("JA","JA");
                erjbMap.put("BG","BG");
                erjbMap.put("EN","EN");

                Hashtable details = (Hashtable) request.getAttribute("details");
                String username = "";
                String passwd = "";
                String conpasswd = "";
                String tpasswd = "";
                String contpasswd = "";
                String company_name = "";
                String partnerNameForWLInvoice = "";
                String processor_partnerid = "";
                //String company_type = "";
                String brandname = "";
                String sitename = "";
                String domain = "";
                String relayWithAuth = "";
                String protocol = "";
                String contact_emails = "";
                String contact_persons = "";
                String telno = "";
                String logo = "";
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
                //String supportfromaddress = "";
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
                String documentationUrl = "";
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
                String superPartnerId = "";

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
                String signupPrevent = request.getParameter("signupPrevent") == null ? "" : request.getParameter("signupPrevent");
                String oldcheckout = request.getParameter("oldcheckout") == null ? "" : request.getParameter("oldcheckout");

                /*String emailSent = request.getParameter("emailSent") == null ? "" : request.getParameter("emailSent");*/
                String bankCardLimit = request.getParameter("bankCardLimit") == null ? "" : request.getParameter("bankCardLimit");
                String emi_configuration = request.getParameter("emi_configuration") == null ? "" : request.getParameter("emi_configuration");
                String exportTransactionCron = request.getParameter("exportTransactionCron") == null ? "" : request.getParameter("exportTransactionCron");
                String role = request.getParameter("role") == null ? "" : request.getParameter("role");
                String errormsg = ESAPI.encoder().encodeForHTML((String) request.getAttribute("error"));
                String emailTemplateLang = request.getParameter("emailTemplateLang") == null ? "" : request.getParameter("emailTemplateLang");

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
                    if (details.get("documentationurl") != null) documentationUrl = (String) details.get("documentationurl");
                    if (details.get("salesemail") != null) salesemail = (String) details.get("salesemail");
                    if (details.get("billingemail") != null) billingemail = (String) details.get("billingemail");
                    if (details.get("notifyemail") != null) notifyemail = (String) details.get("notifyemail");
                    if (details.get("companyfromaddress") != null)
                        companyfromaddress = (String) details.get("companyfromaddress");
                   /* if (details.get("supportfromaddress") != null)
                        supportfromaddress = (String) details.get("supportfromaddress");*/
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
                    if(details.get("cookiesUrl") !=null)
                        cookiesurl= (String) details.get("cookiesUrl");
                    if (details.get("superPartnerId") != null)
                        superPartnerId = (String) details.get("superPartnerId");
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
                    if (details.get("processor_partnerid") != null)
                        processor_partnerid = (String) details.get("processor_partnerid");
                    if (details.get("role") != null)
                        role = (String) details.get("role");
                    if (details.get("emailTemplateLang") != null)
                        emailTemplateLang = (String)details.get("emailTemplateLang");
                    if (mes.equals("F"))
                    {
                        out.println("<table align=\"center\" width=\"60%\" ><tr><td><font class=\"textb\" >You have <b>NOT FILLED</b> some of required details or some of details filled by you are incorrect. Please fill all the details completely before going for next step.</font>");
                        out.println("</td></tr><tr><td algin=\"center\" ><font class=\"textb\"  size=\"2\">");
                        errormsg = errormsg.replace("&lt;BR&gt;", "<BR>");
                        out.println(errormsg);
                        out.println("</font>");
                        out.println("</td></tr></table>");
                    }
                }
            %>

            <table align="center" width="70%" cellpadding="2" cellspacing="2">
                <div class="content">
                    <div class="modal" id="myModal" role="dialog">
                        <div id="target" class="modal-dialog" >
                            <div class="modal-content" style="font-size: 21px">
                                <div class="header">
                                    <div class="logo">
                                        <h4 class="modal-title">Send Notification</h4>
                                    </div>
                                </div>

                                <div class="modal-body">
                                    <p>Send email notification to Partner’s Main contact?</p>
                                </div>
                                <div class="modal-footer" >
                                    <button type="button" id="yesSubmit" class="btn btn-default" data-dismiss="modal">Yes</button>
                                    <button type="button" id="noSubmit" class="btn btn-default" data-dismiss="modal">No</button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <form id="form1" action="/icici/servlet/NewPartner?ctoken=<%=ctoken%>"  method="post" name="form1">
                    <input type="hidden" id="sendEmailNotification" name="sendEmailNotification" value="">
                    <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>">
                    <input id="isEncrypted" name="isEncrypted" type="hidden" value="false">
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="43%" class="textb"><span class="textb">Username*</span><br>
                                        (Username Should Not Contain Special Characters like !@#$%)
                                    </td>
                                    <td width="5%" class="textb">:</td>
                                    <td width="50%"><input class="txtbox" type="Text" maxlength="100" maxlength=100
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute(username)%>"
                                                           name="username" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"><span class="textb">Password*</span><br>
                                        (Passwords length should be at least 8 and should contain alphabet, numeric, and
                                        special characters like !@#$)
                                    </td>
                                    <td class="textb">:</td>
                                    <td><input id="passwd" class="txtbox" type="Password" maxlength="125" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(passwd)%>" name="passwd" size="35">
                                        <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="showHidepass" onclick="hideshowpass('showHidepass','passwd')" style="position: relative;right: 27;margin-top: 3;"></span>

                                    </td>

                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"><span class="textb">Confirm Password*</span><br>
                                        (Should be same as PASSWORD)
                                    </td>
                                    <td class="textb">:</td>
                                    <td><input id="conpasswd" class="txtbox" type="Password" maxlength="125" autocomplete="off" readonly  onfocus="this.removeAttribute('readonly');"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(conpasswd)%>"
                                               name="conpasswd" size="35">
                                        <span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" id="conshowHidepass" onclick="hideshowpass('conshowHidepass','conpasswd')" style="position: relative;right: 27;margin-top: 3;"></span>

                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td colspan="4" class="hrnew"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Partner Organisation Name*</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(company_name)%>"
                                               name="partnerName" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"><span class="textb">Organisation Name</span><br>
                                        (Use only for WL invoice)
                                    </td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(partnerNameForWLInvoice)%>"
                                               name="partnerNameForWLInvoice" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"><span class="textb">Processor Partner ID:</span><br>
                                        (Use only for Merchant Payout Report)
                                    </td>
                                    <td class="textb">:</td>
                                    <td>
                                        <input class="txtbox" type="Text" maxlength="20"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(processor_partnerid)%>"
                                               name="processor_partnerid" id="allpid" size="35"  autocomplete="on">
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"><span class="textb">Site URL*</span><br>
                                        (Ex. http://www.abc.com)
                                    </td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(sitename)%>"
                                               name="sitename" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"><span class="textb">Domain</span><br>
                                        (Ex. http://www.abc.com)
                                    </td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="1000"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(domain)%>" name="domain"
                                               size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"><span class="textb">Contact Telephone Number*</span><br>
                                        (Accepts only Numeric values and [.+-#])
                                    </td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="20"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(telno)%>" name="telno"
                                               size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>

                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Support Mail ID*</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(supportmailid)%>"
                                               name="companysupportmailid" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb" valign="top">Admin Mail ID*</td>
                                    <td class="textb" valign="top">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(adminmailid)%>"
                                               name="adminmailid" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Support URL*</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(supporturl)%>"
                                               name="supporturl" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Documentation URL*</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(documentationUrl)%>"
                                               name="documentationurl" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Host URL*</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(hosturl)%>"
                                               name="hosturl" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Company from address*</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(companyfromaddress)%>"
                                               name="companyfromaddress" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <%--<tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Support from address*</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(supportfromaddress)%>"
                                               name="supportfromaddress" size="35"></td>
                                </tr>--%>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">SMTP Host*</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(smtp_host)%>"
                                               name="smtp_host" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">SMTP Port*</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="5"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(smtp_port)%>"
                                               name="smtp_port" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Relay With Authrization</td>
                                    <td class="textb">:</td>
                                    <td>
                                        <select name="relaywithauth" id="relaywithauth" onchange="updatetextbox(relaywithauth)">
                                            <%
                                                if ("Y".equals(relayWithAuth))
                                                {
                                            %>
                                            <option value="Y" selected>Y</option>
                                            <option value="N">N</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected>N</option>
                                            <option value="Y">Y</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Protocol</td>
                                    <td class="textb">:</td>
                                    <td>
                                        <select name="protocol" id="protocol">
                                            <%
                                                if ("TLS".equals(protocol))
                                                {
                                            %>
                                            <option value="TLS" selected>TLS</option>
                                            <option value="SSL">SSL</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="SSL">SSL</option>
                                            <option value="TLS" selected>TLS</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">SMTP User*</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(smtp_user)%>"
                                               name="smtp_user" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">SMTP Password*</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="password" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(smtp_password)%>"
                                               name="smtp_password"  id="smtp_password" size="35"></td>
                                    <input type="hidden"
                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute(smtp_password)%>"
                                           name="smtp_password" ></td>




                                    <%--      <div class="form-group">
                                              <div class="col-md-2"></div>
                                              <label class="col-md-4 control-label">SMTP Password*</label>
                                              <div class="col-md-4">
                                                  <input class="form-control" type="password" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(smtp_password)%>"
                                                  name="smtp_password"
                                                         id="smtp_password" size="35">
                                                  <input type="hidden"
                                                         value="<%=ESAPI.encoder().encodeForHTMLAttribute(smtp_password)%>"
                                                         name="smtp_password" >
                                              </div>
                                              <div class="col-md-6"></div>--%>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">SMS User</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(sms_user)%>"
                                               name="sms_user" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">SMS Password</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="password" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(sms_password)%>"
                                               name="sms_password" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">From SMS</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(from_sms)%>"
                                               name="from_sms" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Is IP Whitelisted*</td>
                                    <td class="textb">:</td>
                                    <td>
                                        <select name="isipwhitelisted">
                                            <%
                                                if ("Y".equals(isipwhitelisted))
                                                {
                                            %>
                                            <option value="Y" selected>Y</option>
                                            <option value="N">N</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected>N</option>
                                            <option value="Y">Y</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Is CardEncryption Enable*</td>
                                    <td class="textb">:</td>
                                    <td>
                                        <select name="iscardencryptionenable">
                                            <%
                                                if ("Y".equals(iscardencryptionenable))
                                                {
                                            %>
                                            <option value="Y" selected>Y</option>
                                            <option value="N">N</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected>N</option>
                                            <option value="Y">Y</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Split Payment*</td>
                                    <td class="textb">:</td>
                                    <td>
                                        <select name="splitpayment">
                                            <%
                                                if ("Y".equals(splitpayment))
                                                {
                                            %>
                                            <option value="Y" selected>Y</option>
                                            <option value="N">N</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected>N</option>
                                            <option value="Y">Y</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Split Payment Type*</td>
                                    <td class="textb">:</td>
                                    <td><select name="splitpaymenttype">
                                        <%
                                            if ("Terminal".equals(splitpaymenttype))
                                            {
                                        %>
                                        <option value="Terminal" selected>Terminal</option>
                                        <option value="Merchant">Merchant</option>
                                        <%
                                        }
                                        else
                                        {
                                        %>
                                        <option value="Merchant" selected>Merchant</option>
                                        <option value="Terminal">Terminal</option>
                                        <%
                                            }
                                        %>
                                    </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Address Validation*</td>
                                    <td class="textb">:</td>
                                    <td><select name="addressvalidation">
                                        <%
                                            if ("Y".equals(addressvalidation))
                                            {
                                        %>
                                        <option value="Y" selected>Y</option>
                                        <option value="N">N</option>
                                        <%
                                        }
                                        else
                                        {
                                        %>
                                        <option value="N" selected>N</option>
                                        <option value="Y">Y</option>
                                        <%
                                            }
                                        %>
                                    </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Address Detail Display*</td>
                                    <td class="textb">:</td>
                                    <td><select name="addressdetaildisplay">
                                        <%
                                            if ("Y".equals(addressdetaildisplay))
                                            {
                                        %>
                                        <option value="Y" selected>Y</option>
                                        <option value="N">N</option>
                                        <%
                                        }
                                        else
                                        {
                                        %>
                                        <option value="N" selected>N</option>
                                        <option value="Y">Y</option>
                                        <%
                                            }
                                        %>
                                    </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">AutoRedirect*</td>
                                    <td class="textb">:</td>
                                    <td><select name="autoRedirect">
                                        <%
                                            if ("Y".equals(autoRedirect))
                                            {
                                        %>
                                        <option value="Y" selected>Y</option>
                                        <option value="N">N</option>
                                        <%
                                        }
                                        else
                                        {
                                        %>
                                        <option value="N" selected>N</option>
                                        <option value="Y">Y</option>
                                        <%
                                            }
                                        %>
                                    </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Flight Mode*</td>
                                    <td class="textb">:</td>
                                    <td><select name="flightMode">
                                        <%
                                            if ("Y".equals(flightMode))
                                            {
                                        %>
                                        <option value="Y" selected>Y</option>
                                        <option value="N">N</option>
                                        <%
                                        }
                                        else
                                        {
                                        %>
                                        <option value="N" selected>N</option>
                                        <option value="Y">Y</option>
                                        <%
                                            }
                                        %>
                                    </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Template</td>
                                    <td class="textb">:</td>
                                    <td><select name="template">
                                        <%
                                            if ("Y".equals(template))
                                            {
                                        %>
                                        <option value="Y" selected>Y</option>
                                        <option value="N">N</option>
                                        <%
                                        }
                                        else
                                        {
                                        %>
                                        <option value="N" selected>N</option>
                                        <option value="Y">Y</option>
                                        <%
                                            }
                                        %>
                                    </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Checkout Invoice</td>
                                    <td class="textb">:</td>

                                    <td><select name="checkoutInvoice">
                                        <%
                                            if ("Y".equals(checkoutInvoice))
                                            {
                                        %>
                                        <option value="Y" selected>Y</option>
                                        <option value="N">N</option>
                                        <%
                                        }
                                        else
                                        {
                                        %>
                                        <option value="N" selected>N</option>
                                        <option value="Y">Y</option>
                                        <%
                                            }
                                        %>
                                    </select>

                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"> Bank Card Limit</td>
                                    <td class="textb">:</td>
                                    <td><select name="bankCardLimit">
                                        <%
                                            if ("Y".equals(bankCardLimit))
                                            {
                                        %>
                                        <option value="Y" selected>Y</option>
                                        <option value="N">N</option>
                                        <%
                                        }
                                        else
                                        {
                                        %>
                                        <option value="N" selected>N</option>
                                        <option value="Y">Y</option>
                                        <%
                                            }
                                        %>
                                    </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                    <input type="hidden" name ="emi_configuration" value="N">
                                </tr>
                                <%-- <tr>

                                     &lt;%&ndash;<td class="textb">&nbsp;</td>
                                     <td class="textb"> Emi Configuration</td>
                                     <td class="textb">:</td>
                                     <td><select name="emi_configuration">
                                         <%
                                             if ("Y".equals(emi_configuration))
                                             {
                                         %>
                                         <option value="Y" selected>Y</option>
                                         <option value="N">N</option>
                                         <%
                                         }
                                         else
                                         {
                                         %>
                                         <option value="N" selected>N</option>
                                         <option value="Y">Y</option>
                                         <%
                                             }
                                         %>
                                     </select>
                                     </td>&ndash;%&gt;
                                 </tr>--%>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"> Export Transaction Cron</td>
                                    <td class="textb">:</td>
                                    <td><select name="exportTransactionCron" style="width: 186px;">
                                        <%if("N".equalsIgnoreCase(exportTransactionCron)){%>
                                        <option value="N" selected>N</option>
                                        <%}else{%>
                                        <option value="N">N</option>
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
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Country*</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>"
                                               name="country" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Address</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="255"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(address)%>"
                                               name="address" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"><label>Flight Partner</label></td>
                                    <td class="textb">:</td>
                                    <td class="textb">
                                        <select name="isflightPartner" class="txtbox"></option>
                                            <%
                                                if ("Y".equals(isflightPartner))
                                                {
                                            %>
                                            <option value="Y" selected>Yes</option>
                                            <option value="N">No</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected>No</option>
                                            <option value="Y">Yes</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>

                                    </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Reporting Currency</td>
                                    <td class="textb">:</td>
                                    <td class="textb">
                                        <select name="currency" class="txtbox">
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
                                    </td>
                                </tr>

                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">SMS Service</td>
                                    <td class="textb">:</td>
                                    <td class="textb">
                                        <select name="sms_service" class="txtbox">
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
                                    </td>
                                </tr>

                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Is Ip Whitelisted for Invoice</td>
                                    <td class="textb">:</td>
                                    <td class="textb">
                                        <select name="ip_whitelist_invoice" class="txtbox"></option>
                                            <%
                                                if ("Y".equals(ip_whitelist_invoice))
                                                {
                                            %>
                                            <option value="Y" selected>Yes</option>
                                            <option value="N">No</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected>No</option>
                                            <option value="Y">Yes</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Address Validation For Invoice</td>
                                    <td class="textb">:</td>
                                    <td class="textb">
                                        <select name="address_validation_invoice" class="txtbox"></option>
                                            <%
                                                if ("Y".equals(address_validation_invoice))
                                                {
                                            %>
                                            <option value="Y" selected>Yes</option>
                                            <option value="N">No</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected>No</option>
                                            <option value="Y">Yes</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Default Template Theme</td>
                                    <td class="textb">:</td>
                                    <td class="textb">
                                        <select name="defaulttemplatetheme" class="txtbox" style="width:186px">
                                            <option value="" selected>--Select Theme--</option>
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
                                    </td>
                                </tr>

                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">PCI Logo</td>
                                    <td class="textb">:</td>
                                    <td class="textb">
                                        <select name="ispcilogo" class="txtbox">
                                            <%
                                                if ("Y".equals(ispcilogo))
                                                {
                                            %>
                                            <option value="Y" selected>Yes</option>
                                            <option value="N">No</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected>No</option>
                                            <option value="Y">Yes</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Bin Service</td>
                                    <td class="textb">:</td>
                                    <td class="textb">
                                        <select name="binService" class="txtbox">
                                            <%
                                                if ("Y".equals(binService))
                                                {
                                            %>
                                            <option value="Y" selected>Yes</option>
                                            <option value="N">No</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected>No</option>
                                            <option value="Y">Yes</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Signup Prevent</td>
                                    <td class="textb">:</td>
                                    <td class="textb">
                                        <select name="signupPrevent" class="txtbox">
                                            <%
                                                if ("Y".equals(signupPrevent))
                                                {
                                            %>
                                            <option value="Y" selected>Yes</option>
                                            <option value="N">No</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected>No</option>
                                            <option value="Y">Yes</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>

                                <%--<tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                             <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Is Old Checkout Template</td>
                                    <td class="textb">:</td>
                                    <td class="textb">
                                        <select name="oldcheckout" class="txtbox">
                                            <%
                                                if ("Y".equals(oldcheckout))
                                                {
                                            %>
                                            <option value="Y" selected>Yes</option>
                                            <option value="N">No</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="N" selected>No</option>
                                            <option value="Y">Yes</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr> --%>
                                <%--<tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Is Email Sent</td>
                                    <td class="textb">:</td>
                                    <td class="textb">
                                        <select name="emailSent" class="txtbox">
                                            <%
                                                if ("N".equals(emailSent))
                                                {
                                            %>
                                            <option value="N" selected>No</option>
                                            <option value="Y">Yes</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="Y" selected>Yes</option>
                                            <option value="N">No</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                      </tr>--%>

                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>

                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Email Template Language</td>
                                    <td class="textb">:</td>
                                    <td class="textb">
                                        <select name="emailTemplateLang" class="txtbox">
                                            <%
                                                if ("RO".equals(emailTemplateLang))
                                                {
                                            %>
                                            <option value="RO" selected>RO</option>
                                            <option value="JA">JA</option>
                                            <option value="BG">BG</option>
                                            <option value="EN">EN</option>
                                            <%
                                            }
                                            else if ("JA".equals(emailTemplateLang))
                                            {
                                            %>
                                            <option value="RO">RO</option>
                                            <option value="JA" selected>JA</option>
                                            <option value="BG">BG</option>
                                            <option value="EN">EN</option>
                                            <%
                                            }
                                            else if ("BG".equals(emailTemplateLang))
                                            {
                                            %>
                                            <option value="RO">RO</option>
                                            <option value="JA">JA</option>
                                            <option value="BG" selected>BG</option>
                                            <option value="EN">EN</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="RO">RO</option>
                                            <option value="JA">JA</option>
                                            <option value="BG">BG</option>
                                            <option value="EN" selected>EN</option>
                                            <% }
                                            %>
                                        </select>
                                    </td>
                                </tr>

                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Support Email for Transaction Mail</td>
                                    <td class="textb">:</td>
                                    <td><select name="supportemailfortransactionmail" class="txtbox">
                                        <%
                                            if ("Partner".equals(supportEmailForTransactionMail))
                                            {
                                        %>
                                        <option value="Partner" selected>Partner</option>
                                        <option value="Merchant">Merchant</option>
                                        <%
                                        }
                                        else
                                        {
                                        %>
                                        <option value="Merchant" selected>Merchant</option>
                                        <option value="Partner">Partner</option>
                                        <%
                                            }
                                        %>
                                    </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">TermsURL</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="255"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(termsurl)%>"
                                               name="termsUrl" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">PrivacyURL</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="255"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(privacyurl)%>"
                                               name="privacyUrl" size="35"></td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">CookiesURL</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="255"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(cookiesurl)%>"
                                               name="cookiesUrl" size="35"></td>
                                </tr>


                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Role</td>
                                    <td class="textb">:</td>
                                    <td class="textb">
                                        <select name="role" class="txtbox">
                                            <%
                                                if ("superPartner".equals(role))
                                                {
                                            %>
                                            <option value="partner">Partner</option>
                                            <option value="superPartner" selected>Super Partner</option>
                                            <%
                                            }
                                            else
                                            {
                                            %>
                                            <option value="partner" selected>Partner</option>
                                            <option value="superPartner">Super Partner</option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Super PartnerId</td>
                                    <td class="textb">:</td>
                                    <td><input class="txtbox" type="Text" maxlength="255"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(superPartnerId)%>"
                                               name="superPartnerId" size="35"></td>
                                </tr>


                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"></td>
                                    <td class="textb"></td>
                                    <td></td>
                                </tr>
                                <td colspan="3" align="right" class="textb" style="margin-right: 50%"><h5><b><u>Contact
                                    Info</u></b></h5></td>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"></td>
                                    <td class="textb"></td>
                                    <td></td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"></td>
                                    <td class="textb"></td>
                                    <td></td>
                                </tr>

                                <tr>
                                    <td colspan="4" style="=margin:0px">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Main Contact*</td>
                                    <%--<td align="left" class="textb">:</td>--%>
                                    <td>
                                        <input class="txtbox" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_persons)%>"
                                               name="contact_persons" size="35" placeholder="Name*">
                                    </td>
                                    <td align="center">
                                        <input class="txtbox" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_emails)%>"
                                               name="contact_emails" size="35" placeholder="Email*">
                                    </td>
                                    <td>
                                        <input class="txtbox" type="Text" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_ccEmail)%>"
                                               name="contact_ccmailid" size="35" placeholder="Email Cc">
                                    </td>
                                </tr>
                                </tr>

                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Sales Contact*</td>
                                    <%--<td class="textb">:</td>--%>
                                    <td>
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContactName)%>"
                                               name="salescontactname" size="35" placeholder="Name*">
                                    </td>
                                    <td align="center">
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesemail)%>"
                                               name="salesemail" size="35" placeholder="Email*">
                                    </td>
                                    <td>
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(sale_ccEmail)%>"
                                               name="sales_ccemailid" size="35" placeholder="Email Cc">
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Billing Contact*</td>
                                    <%--<td class="textb">:</td>--%>
                                    <td width="5%">
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContactName)%>"
                                               name="billingcontactname" size="35" placeholder="Name*">
                                    </td>
                                    <td align="center">
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingemail)%>"
                                               name="billingemail" size="35" placeholder="Email*">
                                    </td>

                                    <td>
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(billing_ccEmail)%>"
                                               name="billing_ccemailid" size="35" placeholder="Email Cc">
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Notify Contact*</td>
                                    <%--<td class="textb">:</td>--%>
                                    <td width="5%">
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(notifyContactName)%>"
                                               name="notifycontactname" size="35" placeholder="Name*">
                                    </td>
                                    <td align="center">
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(notifyemail)%>"
                                               name="notifyemail" size="35" placeholder="Email*">
                                    </td>
                                    <td>
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(notify_ccEmail)%>"
                                               name="notify_ccemailid" size="35" placeholder="Email Cc">
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Fraud Contact*</td>
                                    <%-- <td class="textb">:</td>--%>
                                    <td width="5%">
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContactName)%>"
                                               name="fraudcontactname" size="35" placeholder="Name*">
                                    </td>
                                    <td align="center">
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudemail)%>"
                                               name="fraudemail" size="35" placeholder="Email*">
                                    </td>

                                    <td>
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraud_ccEmail)%>"
                                               name="fraud_ccemailid" size="35" placeholder="Email Cc">
                                    </td>
                                </tr>

                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Chargeback Contact</td>
                                    <%--<td class="textb">:</td>--%>
                                    <td width="5%">
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbConatctName)%>"
                                               name="chargebackcontactname" size="35" placeholder="Name">
                                    </td>
                                    <td align="center">
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbConatctMailId)%>"
                                               name="chargebackemailid" size="35" placeholder="Email">
                                    </td>

                                    <td>
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(chargeback_ccEmail)%>"
                                               name="chargeback_ccemailid" size="35" placeholder="Email Cc">
                                    </td>

                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Refund Contact</td>
                                    <%--<td class="textb">:</td>--%>
                                    <td width="5%">
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(rfContactName)%>"
                                               name="refundcontactname" size="35" placeholder="Name">
                                    </td>
                                    <td align="center">
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(rfConatctMailId)%>"
                                               name="refundemailid" size="35" placeholder="Email">
                                    </td>

                                    <td>
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(refund_ccEmail)%>"
                                               name="refund_ccemailid" size="35" placeholder="Email CC">
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb">Technical Contact</td>
                                    <%-- <td class="textb">:</td>--%>
                                    <td width="5%">
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(techConatactName)%>"
                                               name="technicalcontactname" size="35" placeholder="Name">
                                    </td>
                                    <td align="center">
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(techContactMailId)%>"
                                               name="technicalemailid" size="35" placeholder="Email">
                                    </td>

                                    <td>
                                        <input class="txtbox" type="Text" maxlength="50"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(technical_ccEmail)%>"
                                               name="technical_ccemailid" size="35" placeholder="Email Cc">
                                    </td>
                                </tr>

                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb">&nbsp;</td>
                                    <td class="textb"></td>
                                    <td class="textb"></td>
                                    <td>
                                        <input type="hidden" value="-" name="bussinessdevexecutive"><input type="hidden"
                                                                                                           value="1"
                                                                                                           name="step">
                                        <button id="submit"  value="submit" name="submit" data-toggle="modal" data-target="#myModal"
                                                class="buttonform" type="button">
                                            submit
                                        </button>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </form>
            </table>
        </div>
    </div>
</div>
<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</body>
</html>