<%@ page language="java" import="java.util.Hashtable" %>
<%@ page import = "org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Iterator" %>
<%@ include file="Top.jsp" %>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","Merchant Profile");

    HashMap<String, String> timezoneHash = new HashMap();
    timezoneHash.put("Etc/GMT+12|(GMT-12:00)","(GMT-12:00) International Date Line West");
    timezoneHash.put("Pacific/Midway|(GMT-11:00","(GMT-11:00) Midway Island, Samoa");
    timezoneHash.put("Pacific/Honolulu|(GMT-10:00)","(GMT-10:00) Hawaii");
    timezoneHash.put("US/Alaska|(GMT-09:00)","(GMT-09:00) Alaska");
    timezoneHash.put("America/Los_Angeles|(GMT-08:00)","(GMT-08:00) Pacific Time (US & Canada)");
    timezoneHash.put("America/Tijuana|(GMT-08:00)","(GMT-08:00) Tijuana, Baja California");
    timezoneHash.put("US/Arizona|(GMT-07:00)","(GMT-07:00) Arizona");
    timezoneHash.put("America/Chihuahua|(GMT-07:00)","(GMT-07:00) Chihuahua, La Paz, Mazatlan");
    timezoneHash.put("US/Mountain|(GMT-07:00)","(GMT-07:00) Mountain Time (US & Canada)");
    timezoneHash.put("America/Managua|(GMT-06:00)","(GMT-06:00) Central America");
    timezoneHash.put("US/Central|(GMT-06:00)","(GMT-06:00) Central Time (US & Canada)");
    timezoneHash.put("America/Mexico_City|(GMT-06:00)","(GMT-06:00) Guadalajara, Mexico City, Monterrey");
    timezoneHash.put("Canada/Saskatchewan|(GMT-06:00)","(GMT-06:00) Saskatchewan");
    timezoneHash.put("America/Bogota|(GMT-05:00)","(GMT-05:00) Bogota, Lima, Quito, Rio Branco");
    timezoneHash.put("US/Eastern|(GMT-05:00)","(GMT-05:00) Eastern Time (US & Canada)");
    timezoneHash.put("US/East-Indiana|(GMT-05:00)","(GMT-05:00) Indiana (East)");
    timezoneHash.put("Canada/Atlantic|(GMT-04:00)","(GMT-04:00) Atlantic Time (Canada)");
    timezoneHash.put("America/Caracas|(GMT-04:00)","(GMT-04:00) Caracas, La Paz");
    timezoneHash.put("America/Manaus|(GMT-04:00)","(GMT-04:00) Manaus");
    timezoneHash.put("America/Santiago|(GMT-04:00)","(GMT-04:00) Santiago");
    timezoneHash.put("Canada/Newfoundland|(GMT-03:30)","(GMT-03:30) Newfoundland");
    timezoneHash.put("America/Sao_Paulo|(GMT-03:00)","(GMT-03:00) Brasilia");
    timezoneHash.put("America/Argentina/Buenos_Aires|(GMT-03:00)","(GMT-03:00) Buenos Aires, Georgetown");
    timezoneHash.put("America/Godthab|(GMT-03:00)","(GMT-03:00) Greenland");
    timezoneHash.put("America/Montevideo|(GMT-03:00)","(GMT-03:00) Montevideo");
    timezoneHash.put("America/Noronha|(GMT-02:00)","(GMT-02:00) Mid-Atlantic");
    timezoneHash.put("Atlantic/Cape_Verde|(GMT-01:00)","(GMT-01:00) Cape Verde Is.");
    timezoneHash.put("Atlantic/Azores|(GMT-01:00)","(GMT-01:00) Azores");
    timezoneHash.put("Africa/Casablanca|(GMT+00:00)","(GMT+00:00) Casablanca, Monrovia, Reykjavik");
    timezoneHash.put("Etc/Greenwich|(GMT+00:00)","(GMT+00:00) Greenwich Mean Time : Dublin, Edinburgh, Lisbon, London");
    timezoneHash.put("Europe/Amsterdam|(GMT+01:00)","(GMT+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna");
    timezoneHash.put("Europe/Belgrade|(GMT+01:00)","(GMT+01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague");
    timezoneHash.put("Europe/Brussels|(GMT+01:00)","(GMT+01:00) Brussels, Copenhagen, Madrid, Paris");
    timezoneHash.put("Europe/Sarajevo|(GMT+01:00)","(GMT+01:00) Sarajevo, Skopje, Warsaw, Zagreb");
    timezoneHash.put("Africa/Lagos|(GMT+01:00)","(GMT+01:00) West Central Africa");
    timezoneHash.put("Asia/Amman|(GMT+02:00)","(GMT+02:00) Amman");
    timezoneHash.put("Europe/Athens|(GMT+02:00)","(GMT+02:00) Athens, Bucharest, Istanbul");
    timezoneHash.put("Asia/Beirut|(GMT+02:00)","(GMT+02:00) Beirut");
    timezoneHash.put("Africa/Cairo|(GMT+02:00)","(GMT+02:00) Cairo");
    timezoneHash.put("Africa/Harare|(GMT+02:00)","(GMT+02:00) Harare, Pretoria");
    timezoneHash.put("Europe/Helsinki|(GMT+02:00)","(GMT+02:00) Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius");
    timezoneHash.put("Asia/Jerusalem|(GMT+02:00)","(GMT+02:00) Jerusalem");
    timezoneHash.put("Europe/Minsk|(GMT+02:00)","(GMT+02:00) Minsk");
    timezoneHash.put("Africa/Windhoek|(GMT+02:00)","(GMT+02:00) Windhoek");
    timezoneHash.put("Asia/Kuwait|(GMT+03:00)","(GMT+03:00) Kuwait, Riyadh, Baghdad");
    timezoneHash.put("Europe/Moscow|(GMT+03:00)","(GMT+03:00) Moscow, St. Petersburg, Volgograd");
    timezoneHash.put("Africa/Nairobi|(GMT+03:00)","(GMT+03:00) Nairobi");
    timezoneHash.put("Asia/Tbilisi|(GMT+03:00)","(GMT+03:00) Tbilisi");
    timezoneHash.put("Asia/Tehran|(GMT+03:30)","(GMT+03:30) Tehran");
    timezoneHash.put("Asia/Muscat|(GMT+04:00)","(GMT+04:00) Abu Dhabi, Muscat");
    timezoneHash.put("Asia/Baku|(GMT+04:00)","(GMT+04:00) Baku");
    timezoneHash.put("Asia/Yerevan|(GMT+04:00)","(GMT+04:00) Yerevan");
    timezoneHash.put("Asia/Kabul|(GMT+04:30)","(GMT+04:30) Kabul");
    timezoneHash.put("Asia/Yekaterinburg|(GMT+05:00)","(GMT+05:00) Yekaterinburg");
    timezoneHash.put("Asia/Karachi|(GMT+05:00)","(GMT+05:00) Islamabad, Karachi, Tashkent");
    timezoneHash.put("Asia/Calcutta|(GMT+05:30)","(GMT+05:30) Chennai, Kolkata, Mumbai, New Delhi");
    timezoneHash.put("Asia/Katmandu|(GMT+05:45)","(GMT+05:45) Kathmandu");
    timezoneHash.put("Asia/Almaty|(GMT+06:00)","(GMT+06:00) Almaty, Novosibirsk");
    timezoneHash.put("Asia/Dhaka|(GMT+06:00)","(GMT+06:00) Astana, Dhaka");
    timezoneHash.put("Asia/Rangoon|(GMT+06:30)","(GMT+06:30) Yangon (Rangoon)");
    timezoneHash.put("Asia/Bangkok|(GMT+07:00)","(GMT+07:00) Bangkok, Hanoi, Jakarta");
    timezoneHash.put("Asia/Krasnoyarsk|(GMT+07:00)","(GMT+07:00) Krasnoyarsk");
    timezoneHash.put("Asia/Hong_Kong|(GMT+08:00)","(GMT+08:00) Beijing, Chongqing, Hong Kong, Urumqi");
    timezoneHash.put("Asia/Kuala_Lumpur|(GMT+08:00)","(GMT+08:00) Kuala Lumpur, Singapore");
    timezoneHash.put("Asia/Irkutsk|(GMT+08:00)","(GMT+08:00) Irkutsk, Ulaan Bataar");
    timezoneHash.put("Australia/Perth|(GMT+08:00)","(GMT+08:00) Perth");
    timezoneHash.put("Asia/Taipei|(GMT+08:00)","(GMT+08:00) Taipei");
    timezoneHash.put("Asia/Tokyo|(GMT+09:00)","(GMT+09:00) Osaka, Sapporo, Tokyo");
    timezoneHash.put("Asia/Seoul|(GMT+09:00)","(GMT+09:00) Seoul");
    timezoneHash.put("Asia/Yakutsk|GMT+09:00)","(GMT+09:00) Yakutsk");
    timezoneHash.put("Australia/Adelaide|(GMT+09:30)","(GMT+09:30) Adelaide");
    timezoneHash.put("Australia/Darwin|(GMT+09:30)","(GMT+09:30) Darwin");
    timezoneHash.put("Australia/Brisbane|(GMT+10:00)","(GMT+10:00) Brisbane");
    timezoneHash.put("Australia/Canberra|(GMT+10:00)","(GMT+10:00) Canberra, Melbourne, Sydney");
    timezoneHash.put("Australia/Hobart|(GMT+10:00)","(GMT+10:00) Hobart");
    timezoneHash.put("Pacific/Guam|(GMT+10:00)","(GMT+10:00) Guam, Port Moresby");
    timezoneHash.put("Asia/Vladivostok|(GMT+10:00)","(GMT+10:00) Vladivostok");
    timezoneHash.put("Asia/Magadan|(GMT+11:00)","(GMT+11:00) Magadan, Solomon Is., New Caledonia");
    timezoneHash.put("Pacific/Auckland|(GMT+12:00)","(GMT+12:00) Auckland, Wellington");
    timezoneHash.put("Pacific/Fiji|(GMT+12:00)","(GMT+12:00) Fiji, Kamchatka, Marshall Is.");
    timezoneHash.put("Pacific/Tongatapu|(GMT+13:00)","(GMT+13:00) Nuku'alofa");

    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String merchprofile_merchant1 = !functions.isEmptyOrNull(rb1.getString("merchprofile_merchant1"))?rb1.getString("merchprofile_merchant1"): "Merchant Profile";
    String merchprofile_company_name = !functions.isEmptyOrNull(rb1.getString("merchprofile_company_name"))?rb1.getString("merchprofile_company_name"): "Organization Name*";
    String merchprofile_email1 = !functions.isEmptyOrNull(rb1.getString("merchprofile_email1"))?rb1.getString("merchprofile_email1"): "Notification email address*";
    String merchprofile_brand_name = !functions.isEmptyOrNull(rb1.getString("merchprofile_brand_name"))?rb1.getString("merchprofile_brand_name"): "Brand Name";
    String merchprofile_site_url1 = !functions.isEmptyOrNull(rb1.getString("merchprofile_site_url1"))?rb1.getString("merchprofile_site_url1"): "Site URL*";
    String merchprofile_address1 = !functions.isEmptyOrNull(rb1.getString("merchprofile_address1"))?rb1.getString("merchprofile_address1"): "Address";
    String merchprofile_phone = !functions.isEmptyOrNull(rb1.getString("merchprofile_phone"))?rb1.getString("merchprofile_phone"): "Phone Number";
    String merchprofile_pinn = !functions.isEmptyOrNull(rb1.getString("merchprofile_pinn"))?rb1.getString("merchprofile_pinn"): "Pin Code";
    String merchprofile_city = !functions.isEmptyOrNull(rb1.getString("merchprofile_city"))?rb1.getString("merchprofile_city"): "City";
    String merchprofile_state = !functions.isEmptyOrNull(rb1.getString("merchprofile_state"))?rb1.getString("merchprofile_state"): "State";
    String merchprofile_country = !functions.isEmptyOrNull(rb1.getString("merchprofile_country"))?rb1.getString("merchprofile_country"): "Country*";
    String merchprofile_select_country = !functions.isEmptyOrNull(rb1.getString("merchprofile_select_country"))?rb1.getString("merchprofile_select_country"): "Select a Country";
    String merchprofile_support = !functions.isEmptyOrNull(rb1.getString("merchprofile_support"))?rb1.getString("merchprofile_support"): "Support Number*";
    String merchprofile_fraud = !functions.isEmptyOrNull(rb1.getString("merchprofile_fraud"))?rb1.getString("merchprofile_fraud"): "Fraud Alert Score*";
    String merchprofile_reversal = !functions.isEmptyOrNull(rb1.getString("merchprofile_reversal"))?rb1.getString("merchprofile_reversal"): "Fraud Auto Reversal Score*";
    String merchprofile_select_time_zone = !functions.isEmptyOrNull(rb1.getString("merchprofile_select_time_zone"))?rb1.getString("merchprofile_select_time_zone"): "--Select Time Zone--";
    String merchprofile_time = !functions.isEmptyOrNull(rb1.getString("merchprofile_time"))?rb1.getString("merchprofile_time"): "Time Zone";
    String merchprofile_proof = !functions.isEmptyOrNull(rb1.getString("merchprofile_proof"))?rb1.getString("merchprofile_proof"): "Proof require for HIGH RISK FRAUD ALERT transaction?";
    String merchprofile_Yes = !functions.isEmptyOrNull(rb1.getString("merchprofile_Yes"))?rb1.getString("merchprofile_Yes"): "Yes";
    String merchprofile_No = !functions.isEmptyOrNull(rb1.getString("merchprofile_No"))?rb1.getString("merchprofile_No"): "No";
    String merchprofile_billing = !functions.isEmptyOrNull(rb1.getString("merchprofile_billing"))?rb1.getString("merchprofile_billing"): "Proof require for Billing Address data mismatch ?";
    String merchprofile_Yes1 = !functions.isEmptyOrNull(rb1.getString("merchprofile_Yes1"))?rb1.getString("merchprofile_Yes1"): "Yes";
    String merchprofile_No1 = !functions.isEmptyOrNull(rb1.getString("merchprofile_No1"))?rb1.getString("merchprofile_No1"): "No";
    String merchprofile_risk = !functions.isEmptyOrNull(rb1.getString("merchprofile_risk"))?rb1.getString("merchprofile_risk"): "Send Risk Mitigation and Fraud Report Mail ?";
    String merchprofile_Yes2 = !functions.isEmptyOrNull(rb1.getString("merchprofile_Yes2"))?rb1.getString("merchprofile_Yes2"): "Yes";
    String merchprofile_No2 = !functions.isEmptyOrNull(rb1.getString("merchprofile_No2"))?rb1.getString("merchprofile_No2"): "No";
    String merchprofile_contact1 = !functions.isEmptyOrNull(rb1.getString("merchprofile_contact1"))?rb1.getString("merchprofile_contact1"): "Contact Details";
    String merchprofile_main_contact = !functions.isEmptyOrNull(rb1.getString("merchprofile_main_contact"))?rb1.getString("merchprofile_main_contact"): "Main Contact*:";
    String merchprofile_customer = !functions.isEmptyOrNull(rb1.getString("merchprofile_customer"))?rb1.getString("merchprofile_customer"): "Customer Support*:";
    String merchprofile_charge_back = !functions.isEmptyOrNull(rb1.getString("merchprofile_charge_back"))?rb1.getString("merchprofile_charge_back"): "Chargeback Contact:";
    String merchprofile_refund = !functions.isEmptyOrNull(rb1.getString("merchprofile_refund"))?rb1.getString("merchprofile_refund"): "Refund Contact:";
    String merchprofile_sales = !functions.isEmptyOrNull(rb1.getString("merchprofile_sales"))?rb1.getString("merchprofile_sales"): "Sales Contact:";
    String merchprofile_billing1 = !functions.isEmptyOrNull(rb1.getString("merchprofile_billing1"))?rb1.getString("merchprofile_billing1"): "Billing Contact:";
    String merchprofile_fruadcontact = !functions.isEmptyOrNull(rb1.getString("merchprofile_fruadcontact"))?rb1.getString("merchprofile_fruadcontact"): "Fraud Contact:";
    String merchprofile_technical = !functions.isEmptyOrNull(rb1.getString("merchprofile_technical"))?rb1.getString("merchprofile_technical"): "Technical Contact:";
    String merchprofile_update = !functions.isEmptyOrNull(rb1.getString("merchprofile_update"))?rb1.getString("merchprofile_update"): "UPDATE";

%>

<html lang="en">
<head>
    <title><%=company%> Merchant Settings > Merchant Profile</title>
    <script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.min.js"></script>
    <%--<script type = "text/javascript" src="/merchant/transactionCSS/js/bootstrap-4.4.1/js/bootstrap.min.js" ></script>
    <script type = "text/javascript" src="/merchant/transactionCSS/js/content.min.js"></script>
    <script type = "text/javascript" src="/merchant/transactionCSS/js/anime.min.js"></script> <!-- loader animation-->--%>
    <script language="javascript">
        function isint(form)
        {

            if (isNaN(form.numrows.value))
            {
                return false;
            }
            else
            {
                return true;
            }
        }

        function showHelp(title)
        {
            var a= document.getElementById('ctoken').value;
            newwindow = window.open('/merchant/help.jsp?helptitle=' + title, '&ctoken='+a+'', 'left=10,top=10,width=500,height=370,resizable=no,scrollbars=yes');
            return false;
        }
        function submitForm(ans)
        {

            //console.log("confirmEmail "+ans);
            $("#sendEmailNotification").val(ans);
             $('#form1').submit();

        }
    </script>

    <style type="text/css">

        @media(max-width: 991px){
            #phonecc, #telno{
                width: 100%;
            }
        }

        @media(min-width: 992px){
            #phonecc{
                width: 20%;
            }

            #telno{
                width: 80%;
            }
        }
        .field-icon
        {
            float:  right;
            margin-top: -25px;
            position: relative;
            z-index: 2;
        }
        .container{
            /*padding-top:50px;*/
            margin: auto;
        }

    </style>

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <script src="/merchant/javascript/hidde.js"></script>
</head>

<body class="pace-done widescreen fixed-left-void" onload="bodyonload()">
<div class="content-page">
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
                        <p>Send email notification to your Main contact.</p>
                    </div>
                    <div class="modal-footer" >
                        <button type="button" onclick="submitForm('Y')" class="btn btn-default" data-dismiss="modal">Yes</button>
                        <button type="button" onclick="submitForm('N')" class="btn btn-default" data-dismiss="modal">No</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
        <!-- Page Heading Start -->
        <div class="page-heading">
            <form action="/merchant/servlet/UpdateMerchant?ctoken=<%=ctoken%>" method="post" name="form1" id="form1" >
                <div class="row">

                    <div class="col-sm-12 portlets ui-sortable">

                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=merchprofile_merchant1%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>

                            <div class="widget-content padding">
                                <div id="horizontal-form">

                                    <%
                                        Hashtable details = (Hashtable) request.getAttribute("details");
                                        if (request.getParameter("MES") != null)
                                        {
                                            String mes = ESAPI.encoder().encodeForHTML((String) request.getParameter("MES"));
                                            if (mes.equals("F"))
                                            {
                                                String errormsg = ESAPI.encoder().encodeForHTML((String) request.getAttribute("error"));
                                                //out.println("<center><li class=\"alert alert-danger alert-dismissable\"><b>"+errormsg+"</b></li></center>");
                                                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
                                            }
                                        }

                                        String contact_persons = "";
                                        String contact_emails = "";
                                        String mainContact_cCmailid="";
                                        String mainContact_phone="";

                                        String support_persons = "";
                                        String support_emails = "";
                                        String support_cCmailid="";
                                        String support_phone="";

                                        String refundContact_name="";
                                        String refundContact_mailId="";
                                        String refundContact_cCmail="";
                                        String refundContact_phone="";

                                        String cbContact_name="";
                                        String cbContact_mailId="";
                                        String cbContact_cCmailId="";
                                        String cbContact_phone="";

                                        String salesContact_name="";
                                        String salesContact_mailid="";
                                        String salesContact_cCmailId="";
                                        String salesContact_phone="";

                                        String billingContact_name="";
                                        String billingContact_mailid="";
                                        String billingContact_cCmailId="";
                                        String billingContact_phone="";

                                        String fraudContact_name="";
                                        String fraudContact_mailid="";
                                        String fraudContact_cCmailId="";
                                        String fraudContact_phone="";

                                        String technicalContact_name="";
                                        String technicalContact_mailId="";
                                        String technicalContact_cCmailId="";
                                        String technicalContact_phone="";
                                        String timezone="";

                                        String company_name="";
                                        String sitename="";
                                        String supportNo = "";
                                        String[] splitvalue={};
                                        String telno="";
                                        String phonecc="";
                                        String country = "";
                                        String brandName = "";
                                        String street = "";
                                        String city = "";
                                        String state = "";
                                        String zip = "";
                                        String faxNo = "";
                                        String maxScoreAutoReversal = "";
                                        String maxScoreAllowed = "";
                                        String dataMisMatchproof = "";
                                        String hralertproof = "";
                                        String mitigationMail = "";
                                        String notifyemail = "";
                                        if (details.get("brandname") != null) brandName = (String) details.get("brandname");
                                        if (details.get("company_name") != null) company_name = (String) details.get("company_name");
                                        if (details.get("sitename") != null) sitename = (String) details.get("sitename");
                                        if (details.get("country") != null) country = (String) details.get("country");
                                        if (details.get("city") != null) city = (String) details.get("city");
                                        if (details.get("state") != null) state = (String) details.get("state");
                                        if (details.get("address") != null) street = (String) details.get("address");
                                        if (details.get("zip") != null) zip = (String) details.get("zip");
                                        if (details.get("faxno") != null) faxNo = (String) details.get("faxno");
                                        if (details.get("telno") != null) supportNo = (String) details.get("telno");
                                        if(supportNo.contains("-"))
                                        {
                                            splitvalue=supportNo.split("-");
                                            phonecc=splitvalue[0];
                                            telno=splitvalue[1];
                                        }
                                        else
                                        {
                                            telno=supportNo;
                                        }
                                        if (details.get("maxscoreautoreversal") != null) maxScoreAutoReversal = (String) details.get("maxscoreautoreversal");
                                        if (details.get("maxscoreallowed") != null) maxScoreAllowed = (String) details.get("maxscoreallowed");
                                        if (details.get("datamismatchproof") != null) dataMisMatchproof = (String) details.get("datamismatchproof");
                                        if (details.get("hralertproof") != null) hralertproof = (String) details.get("hralertproof");
                                        if (details.get("mitigationmail") != null) mitigationMail = (String) details.get("mitigationmail");
                                        if (details.get("notifyemail") != null) notifyemail = (String) details.get("notifyemail");

                                        if (details.get("contact_emails") != null) contact_emails = (String) details.get("contact_emails");
                                        if (details.get("contact_persons") != null) contact_persons = (String) details.get("contact_persons");
                                        if (details.get("maincontact_ccmailid") != null) mainContact_cCmailid = (String) details.get("maincontact_ccmailid");
                                        if (details.get("maincontact_phone") != null) mainContact_phone = (String) details.get("maincontact_phone");

                                        if (details.get("support_persons") != null) support_persons = (String) details.get("support_persons");
                                        if (details.get("support_emails") != null) support_emails = (String) details.get("support_emails");
                                        if (details.get("support_ccmailid") != null) support_cCmailid = (String) details.get("support_ccmailid");
                                        if (details.get("support_phone") != null) support_phone = (String) details.get("support_phone");


                                        if (details.get("salescontact_name") != null) salesContact_name = (String) details.get("salescontact_name");
                                        if (details.get("salescontact_mailid") != null) salesContact_mailid = (String) details.get("salescontact_mailid");
                                        if (details.get("salescontact_ccmailid") != null) salesContact_cCmailId = (String) details.get("salescontact_ccmailid");
                                        if (details.get("salescontact_phone") != null) salesContact_phone = (String) details.get("salescontact_phone");

                                        if (details.get("refundcontact_name") != null) refundContact_name = (String) details.get("refundcontact_name");
                                        if (details.get("refundcontact_mailid") != null) refundContact_mailId = (String) details.get("refundcontact_mailid");
                                        if (details.get("refundcontact_ccmailid") != null) refundContact_cCmail = (String) details.get("refundcontact_ccmailid");
                                        if (details.get("refundcontact_phone") != null) refundContact_phone = (String) details.get("refundcontact_phone");

                                        if (details.get("cbcontact_name") != null) cbContact_name = (String) details.get("cbcontact_name");
                                        if (details.get("cbcontact_mailid") != null) cbContact_mailId = (String) details.get("cbcontact_mailid");
                                        if (details.get("cbcontact_ccmailid") != null) cbContact_cCmailId = (String) details.get("cbcontact_ccmailid");
                                        if (details.get("cbcontact_phone") != null) cbContact_phone = (String) details.get("cbcontact_phone");

                                        if (details.get("billingcontact_name") != null) billingContact_name = (String) details.get("billingcontact_name");
                                        if (details.get("billingcontact_mailid") != null) billingContact_mailid = (String) details.get("billingcontact_mailid");
                                        if (details.get("billingcontact_ccmailid") != null) billingContact_cCmailId = (String) details.get("billingcontact_ccmailid");
                                        if (details.get("billingcontact_phone") != null) billingContact_phone = (String) details.get("billingcontact_phone");

                                        if (details.get("fraudcontact_name") != null) fraudContact_name = (String) details.get("fraudcontact_name");
                                        if (details.get("fraudcontact_mailid") != null) fraudContact_mailid = (String) details.get("fraudcontact_mailid");
                                        if (details.get("fraudcontact_ccmailid") != null) fraudContact_cCmailId = (String) details.get("fraudcontact_ccmailid");
                                        if (details.get("fraudcontact_phone") != null) fraudContact_phone = (String) details.get("fraudcontact_phone");

                                        if (details.get("technicalcontact_name") != null) technicalContact_name = (String) details.get("technicalcontact_name");
                                        if (details.get("technicalcontact_mailid") != null) technicalContact_mailId = (String) details.get("technicalcontact_mailid");
                                        if (details.get("technicalcontact_ccmailid") != null) technicalContact_cCmailId = (String) details.get("technicalcontact_ccmailid");
                                        if (details.get("technicalcontact_phone") != null) technicalContact_phone = (String) details.get("technicalcontact_phone");
                                        if (details.get("timezone") != null) timezone = (String) details.get("timezone");
                                    %>

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

                                    <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                                    <input type="hidden" id="sendEmailNotification" value="" name="sendEmailNotification">

                                    <div class="form-group col-md-4 has-feedback">

                                        <label  ><%=merchprofile_company_name%></label>
                                        <input type="hidden" name="company_name" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("company_name"))%>">
                                        <input type="text" name="company_name" maxlength="100" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(company_name)%>"
                                               size="30" disabled style="border: 1px solid #b2b2b2;font-weight:bold">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback ">
                                        <label  ><%=merchprofile_email1%></label>
                                        <input class="form-control" type="hidden" id="notifyemail_hidden" class="form-control" maxlength="30"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(notifyemail)%>" name="notifyemail" size="30">
                                        <input  class="form-control hidedivemail"  type="Text" id="notifyemail" maxlength="100"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(notifyemail)%>" onchange="setvalue('notifyemail')"  size="30"><span toggle="#password-field" class="fa fa-fw fa-eye field-icon toggle-password" onclick="hideemail('notifyemail')"></span>
                                    </div>


                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=merchprofile_brand_name%></label>
                                        <input class="form-control" id="inputSuccess1" type="Text" maxlength="100"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(brandName)%>"
                                               name="brandname" size="30">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=merchprofile_site_url1%></label>
                                        <input  class="form-control"  type="Text" maxlength="100"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(sitename)%>"
                                                name="sitename" size="30">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=merchprofile_address1%></label>
                                        <input type="text" name="address" rows="5"  class="form-control"
                                               cols="30" value="<%=ESAPI.encoder().encodeForHTML(street)%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback  " >
                                        <label  ><%=merchprofile_phone%></label>
                                        <%--<input class="form-control" type="Text" class="form-control" maxlength="30"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(faxNo)%>" name="faxno" size="30">--%>
                                        <input class="form-control" type="hidden" id="faxNo_hidden" class="form-control" maxlength="30"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(faxNo)%>" name="faxno" size="30">
                                        <input class="form-control hidedivphone" type="Text" id="faxNo"  maxlength="50" value="<%=ESAPI.encoder().encodeForHTMLAttribute(faxNo)%>" onchange="setvalue('faxNo')" size="35"><span toggle="#password-field" class="fa fa-fw fa-eye field-icon toggle-password" onclick="hidePhone('faxNo')"></span>

                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=merchprofile_pinn%></label>
                                        <input class="form-control" type="Text" maxlength="30"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(zip)%>" name="zip" size="30">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=merchprofile_city%></label>
                                        <input class="form-control" type="Text"class="form-control" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(city)%>" name="city" size="30">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=merchprofile_state%></label>
                                        <input class="form-control" type="Text" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(state)%>" name="state" size="30">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=merchprofile_country%></label>
                                        <select name="country" placeholder="Country*" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>" onchange="myjunk();"  >
                                            <option value="-|Countrycode"><%=merchprofile_select_country%></option>
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
                                            <option value="CUW|599">Curacao</option>
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
                                        </select>

                                        <input value='<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>' id="getcountval" type="hidden">

                                        <script>

                                            var countryval = document.getElementById('getcountval').value;

                                            $('[name=country] option').filter(function() {
                                                if (countryval == 'GB') {
                                                    return ($(this).text() == 'United Kingdom');
                                                } else if (countryval == 'US') {
                                                    return ($(this).text() == 'United States');
                                                } else if (countryval == 'AF') {
                                                    return ($(this).text() == 'Afghanistan');
                                                } else if (countryval == 'AX') {
                                                    return ($(this).text() == 'Aland Islands');
                                                } else if (countryval == 'AL') {
                                                    return ($(this).text() == 'Albania');
                                                } else if (countryval == 'DZ') {
                                                    return ($(this).text() == 'Algeria');
                                                } else if (countryval == 'AS') {
                                                    return ($(this).text() == 'American Samoa');
                                                } else if (countryval == 'AD') {
                                                    return ($(this).text() == 'Andorra');
                                                } else if (countryval == 'AO') {
                                                    return ($(this).text() == 'Angola');
                                                } else if (countryval == 'AI') {
                                                    return ($(this).text() == 'Anguilla');
                                                } else if (countryval == 'AQ') {
                                                    return ($(this).text() == 'Antarctica');
                                                } else if (countryval == 'AG') {
                                                    return ($(this).text() == 'Antigua and Barbuda');
                                                } else if (countryval == 'AR') {
                                                    return ($(this).text() == 'Argentina');
                                                } else if (countryval == 'AM') {
                                                    return ($(this).text() == 'Armenia');
                                                } else if (countryval == 'AW') {
                                                    return ($(this).text() == 'Aruba');
                                                } else if (countryval == 'AU') {
                                                    return ($(this).text() == 'Australia');
                                                } else if (countryval == 'AT') {
                                                    return ($(this).text() == 'Austria');
                                                } else if (countryval == 'AZ') {
                                                    return ($(this).text() == 'Azerbaijan');
                                                } else if (countryval == 'BS') {
                                                    return ($(this).text() == 'Bahamas');
                                                } else if (countryval == 'BH') {
                                                    return ($(this).text() == 'Bahrain');
                                                } else if (countryval == 'BD') {
                                                    return ($(this).text() == 'Bangladesh');
                                                } else if (countryval == 'BB') {
                                                    return ($(this).text() == 'Barbados');
                                                } else if (countryval == 'BY') {
                                                    return ($(this).text() == 'Belarus');
                                                } else if (countryval == 'BE') {
                                                    return ($(this).text() == 'Belgium');
                                                } else if (countryval == 'BZ') {
                                                    return ($(this).text() == 'Belize');
                                                } else if (countryval == 'BJ') {
                                                    return ($(this).text() == 'Benin');
                                                } else if (countryval == 'BM') {
                                                    return ($(this).text() == 'Bermuda');
                                                } else if (countryval == 'BT') {
                                                    return ($(this).text() == 'Bhutan');
                                                } else if (countryval == 'BO') {
                                                    return ($(this).text() == 'Bolivia');
                                                } else if (countryval == 'BA') {
                                                    return ($(this).text() == 'Bosnia and Herzegovina');
                                                } else if (countryval == 'BW') {
                                                    return ($(this).text() == 'Botswana');
                                                } else if (countryval == 'BV') {
                                                    return ($(this).text() == 'Bouvet Island');
                                                } else if (countryval == 'BR') {
                                                    return ($(this).text() == 'Brazil');
                                                } else if (countryval == 'IO') {
                                                    return ($(this).text() == 'British Indian Ocean Territory');
                                                } else if (countryval == 'VG') {
                                                    return ($(this).text() == 'British Virgin Islands');
                                                } else if (countryval == 'BN') {
                                                    return ($(this).text() == 'Brunei');
                                                } else if (countryval == 'BG') {
                                                    return ($(this).text() == 'Bulgaria');
                                                } else if (countryval == 'BF') {
                                                    return ($(this).text() == 'Burkina Faso');
                                                } else if (countryval == 'BI') {
                                                    return ($(this).text() == 'Burundi');
                                                } else if (countryval == 'KH') {
                                                    return ($(this).text() == 'Cambodia');
                                                } else if (countryval == 'CM') {
                                                    return ($(this).text() == 'Cameroon');
                                                } else if (countryval == 'CA') {
                                                    return ($(this).text() == 'Canada');
                                                } else if (countryval == 'CV') {
                                                    return ($(this).text() == 'Cape Verde');
                                                } else if (countryval == 'KY') {
                                                    return ($(this).text() == 'Cayman Islands');
                                                } else if (countryval == 'CF') {
                                                    return ($(this).text() == 'Central African Republic');
                                                } else if (countryval == 'TD') {
                                                    return ($(this).text() == 'Chad');
                                                } else if (countryval == 'CL') {
                                                    return ($(this).text() == 'Chile');
                                                } else if (countryval == 'CN') {
                                                    return ($(this).text() == 'China');
                                                } else if (countryval == 'CX') {
                                                    return ($(this).text() == 'Christmas Island');
                                                } else if (countryval == 'CC') {
                                                    return ($(this).text() == 'Cocos (Keeling) Islands');
                                                } else if (countryval == 'CO') {
                                                    return ($(this).text() == 'Colombia');
                                                } else if (countryval == 'KM') {
                                                    return ($(this).text() == 'Comoros');
                                                } else if (countryval == 'CK') {
                                                    return ($(this).text() == 'Cook Islands');
                                                } else if (countryval == 'CR') {
                                                    return ($(this).text() == 'Costa Rica');
                                                } else if (countryval == 'CI') {
                                                    return ($(this).text() == 'Cote d` Ivoire ');
                                                } else if (countryval == 'HR') {
                                                    return ($(this).text() == 'Croatia');
                                                }
                                                else if (countryval == 'CU')
                                                {
                                                    return ($(this).text() == 'Cuba');

                                                }
                                                else if (countryval == 'CW')
                                                {
                                                    return ($(this).text() == 'Curacao');

                                                }
                                                else if (countryval == 'CY') {
                                                    return ($(this).text() == 'Cyprus');
                                                } else if (countryval == 'CZ') {
                                                    return ($(this).text() == 'Czech Republic');
                                                } else if (countryval == 'CD') {
                                                    return ($(this).text() == 'Democratic Republic of the Congo');
                                                } else if (countryval == 'DK') {
                                                    return ($(this).text() == 'Denmark');
                                                } else if (countryval == 'DJ') {
                                                    return ($(this).text() == 'Djibouti');
                                                } else if (countryval == 'DM') {
                                                    return ($(this).text() == 'Dominica');
                                                } else if (countryval == 'DO') {
                                                    return ($(this).text() == 'Dominican Republic');
                                                } else if (countryval == 'EC') {
                                                    return ($(this).text() == 'Ecuador');
                                                } else if (countryval == 'EG') {
                                                    return ($(this).text() == 'Egypt');
                                                } else if (countryval == 'SV') {
                                                    return ($(this).text() == 'El Salvador');
                                                } else if (countryval == 'GQ') {
                                                    return ($(this).text() == 'Equatorial Guinea');
                                                } else if (countryval == 'ER') {
                                                    return ($(this).text() == 'Eritrea');
                                                } else if (countryval == 'EE') {
                                                    return ($(this).text() == 'Estonia');
                                                } else if (countryval == 'ET') {
                                                    return ($(this).text() == 'Ethiopia');
                                                } else if (countryval == 'FK') {
                                                    return ($(this).text() == 'Falkland Islands');
                                                } else if (countryval == 'FO') {
                                                    return ($(this).text() == 'Faroe Islands');
                                                } else if (countryval == 'FJ') {
                                                    return ($(this).text() == 'Fiji');
                                                } else if (countryval == 'FI') {
                                                    return ($(this).text() == 'Finland');
                                                } else if (countryval == 'FR') {
                                                    return ($(this).text() == 'France');
                                                } else if (countryval == 'GF') {
                                                    return ($(this).text() == 'French Guiana');
                                                } else if (countryval == 'PF') {
                                                    return ($(this).text() == 'French Polynesia');
                                                } else if (countryval == 'TF') {
                                                    return ($(this).text() == 'French Southern and Antarctic Lands');
                                                } else if (countryval == 'GA') {
                                                    return ($(this).text() == 'Gabon');
                                                } else if (countryval == 'GM') {
                                                    return ($(this).text() == 'Gambia');
                                                } else if (countryval == 'GE') {
                                                    return ($(this).text() == 'Georgia');
                                                } else if (countryval == 'DE') {
                                                    return ($(this).text() == 'Germany');
                                                } else if (countryval == 'GH') {
                                                    return ($(this).text() == 'Ghana');
                                                } else if (countryval == 'GI') {
                                                    return ($(this).text() == 'Gibraltar');
                                                } else if (countryval == 'GR') {
                                                    return ($(this).text() == 'Greece');
                                                } else if (countryval == 'GL') {
                                                    return ($(this).text() == 'Greenland');
                                                } else if (countryval == 'GD') {
                                                    return ($(this).text() == 'Grenada');
                                                } else if (countryval == 'GP') {
                                                    return ($(this).text() == 'Guadeloupe');
                                                } else if (countryval == 'GU') {
                                                    return ($(this).text() == 'Guam');
                                                } else if (countryval == 'GT') {
                                                    return ($(this).text() == 'Guatemala');
                                                } else if (countryval == 'GG') {
                                                    return ($(this).text() == 'Guernsey');
                                                } else if (countryval == 'GN') {
                                                    return ($(this).text() == 'Guinea');
                                                } else if (countryval == 'GW') {
                                                    return ($(this).text() == 'Guinea-Bissau');
                                                } else if (countryval == 'GY') {
                                                    return ($(this).text() == 'Guyana');
                                                } else if (countryval == 'HT') {
                                                    return ($(this).text() == 'Haiti');
                                                } else if (countryval == 'HM') {
                                                    return ($(this).text() == 'Heard Island & McDonald Islands');
                                                } else if (countryval == 'HN') {
                                                    return ($(this).text() == 'Honduras');
                                                } else if (countryval == 'HK') {
                                                    return ($(this).text() == 'Hong Kong');
                                                } else if (countryval == 'HU') {
                                                    return ($(this).text() == 'Hungary');
                                                } else if (countryval == 'IS') {
                                                    return ($(this).text() == 'Iceland');
                                                } else if (countryval == 'IN') {
                                                    return ($(this).text() == 'India');
                                                } else if (countryval == 'ID') {
                                                    return ($(this).text() == 'Indonesia');
                                                } else if (countryval == 'IR') {
                                                    return ($(this).text() == 'Iran');
                                                } else if (countryval == 'IQ') {
                                                    return ($(this).text() == 'Iraq');
                                                } else if (countryval == 'IE') {
                                                    return ($(this).text() == 'Ireland');
                                                } else if (countryval == 'IL') {
                                                    return ($(this).text() == 'Israel');
                                                } else if (countryval == 'IT') {
                                                    return ($(this).text() == 'Italy');
                                                } else if (countryval == 'JM') {
                                                    return ($(this).text() == 'Jamaica');
                                                } else if (countryval == 'JP') {
                                                    return ($(this).text() == 'Japan');
                                                } else if (countryval == 'JE') {
                                                    return ($(this).text() == 'Jersey');
                                                } else if (countryval == 'JO') {
                                                    return ($(this).text() == 'Jordan');
                                                } else if (countryval == 'KZ') {
                                                    return ($(this).text() == 'Kazakhstan');
                                                } else if (countryval == 'KE') {
                                                    return ($(this).text() == 'Kenya');
                                                } else if (countryval == 'KI') {
                                                    return ($(this).text() == 'Kiribati');
                                                } else if (countryval == 'KW') {
                                                    return ($(this).text() == 'Kuwait');
                                                } else if (countryval == 'KG') {
                                                    return ($(this).text() == 'Kyrgyzstan');
                                                } else if (countryval == 'LA') {
                                                    return ($(this).text() == 'Laos');
                                                } else if (countryval == 'LV') {
                                                    return ($(this).text() == 'Latvia');
                                                } else if (countryval == 'LB') {
                                                    return ($(this).text() == 'Lebanon');
                                                } else if (countryval == 'LS') {
                                                    return ($(this).text() == 'Lesotho');
                                                } else if (countryval == 'LR') {
                                                    return ($(this).text() == 'Liberia');
                                                } else if (countryval == 'LY') {
                                                    return ($(this).text() == 'Libya');
                                                } else if (countryval == 'LI') {
                                                    return ($(this).text() == 'Liechtenstein');
                                                } else if (countryval == 'LT') {
                                                    return ($(this).text() == 'Lithuania');
                                                } else if (countryval == 'LU') {
                                                    return ($(this).text() == 'Luxembourg');
                                                } else if (countryval == 'MO') {
                                                    return ($(this).text() == 'Macau, China');
                                                } else if (countryval == 'MK') {
                                                    return ($(this).text() == 'Macedonia');
                                                } else if (countryval == 'MG') {
                                                    return ($(this).text() == 'Madagascar');
                                                } else if (countryval == 'MW') {
                                                    return ($(this).text() == 'Malawi');
                                                } else if (countryval == 'MY') {
                                                    return ($(this).text() == 'Malaysia');
                                                } else if (countryval == 'MV') {
                                                    return ($(this).text() == 'Maldives');
                                                } else if (countryval == 'ML') {
                                                    return ($(this).text() == 'Mali');
                                                } else if (countryval == 'MT') {
                                                    return ($(this).text() == 'Malta');
                                                } else if (countryval == 'MH') {
                                                    return ($(this).text() == 'Marshall Islands');
                                                } else if (countryval == 'MQ') {
                                                    return ($(this).text() == 'Martinique');
                                                } else if (countryval == 'MR') {
                                                    return ($(this).text() == 'Mauritania');
                                                } else if (countryval == 'MU') {
                                                    return ($(this).text() == 'Mauritius');
                                                } else if (countryval == 'YT') {
                                                    return ($(this).text() == 'Mayotte');
                                                } else if (countryval == 'MX') {
                                                    return ($(this).text() == 'Mexico');
                                                } else if (countryval == 'FM') {
                                                    return ($(this).text() == 'Micronesia, Federated States of');
                                                } else if (countryval == 'MD') {
                                                    return ($(this).text() == 'Moldova');
                                                } else if (countryval == 'MC') {
                                                    return ($(this).text() == 'Monaco');
                                                } else if (countryval == 'MN') {
                                                    return ($(this).text() == 'Mongolia');
                                                } else if (countryval == 'ME') {
                                                    return ($(this).text() == 'Montenegro');
                                                } else if (countryval == 'MS') {
                                                    return ($(this).text() == 'Montserrat');
                                                } else if (countryval == 'MA') {
                                                    return ($(this).text() == 'Morocco');
                                                } else if (countryval == 'MZ') {
                                                    return ($(this).text() == 'Mozambique');
                                                } else if (countryval == 'MM') {
                                                    return ($(this).text() == 'Myanmar');
                                                } else if (countryval == 'NA') {
                                                    return ($(this).text() == 'Namibia');
                                                } else if (countryval == 'NR') {
                                                    return ($(this).text() == 'Nauru');
                                                } else if (countryval == 'NP') {
                                                    return ($(this).text() == 'Nepal');
                                                } else if (countryval == 'AN') {
                                                    return ($(this).text() == 'Netherlands Antilles');
                                                } else if (countryval == 'NL') {
                                                    return ($(this).text() == 'Netherlands');
                                                } else if (countryval == 'NC') {
                                                    return ($(this).text() == 'New Caledonia');
                                                } else if (countryval == 'NZ') {
                                                    return ($(this).text() == 'New Zealand');
                                                } else if (countryval == 'NI') {
                                                    return ($(this).text() == 'Nicaragua');
                                                } else if (countryval == 'NE') {
                                                    return ($(this).text() == 'Niger');
                                                } else if (countryval == 'NG') {
                                                    return ($(this).text() == 'Nigeria');
                                                } else if (countryval == 'NU') {
                                                    return ($(this).text() == 'Niue');
                                                } else if (countryval == 'NF') {
                                                    return ($(this).text() == 'Norfolk Island');
                                                } else if (countryval == 'KP') {
                                                    return ($(this).text() == 'North Korea');
                                                } else if (countryval == 'MP') {
                                                    return ($(this).text() == 'Northern Mariana Islands');
                                                } else if (countryval == 'NO') {
                                                    return ($(this).text() == 'Norway');
                                                } else if (countryval == 'OM') {
                                                    return ($(this).text() == 'Oman');
                                                } else if (countryval == 'PK') {
                                                    return ($(this).text() == 'Pakistan');
                                                } else if (countryval == 'PW') {
                                                    return ($(this).text() == 'Palau');
                                                } else if (countryval == 'PS') {
                                                    return ($(this).text() == 'Palestinian Authority');
                                                } else if (countryval == 'PA') {
                                                    return ($(this).text() == 'Panama');
                                                } else if (countryval == 'PG') {
                                                    return ($(this).text() == 'Papua New Guinea');
                                                } else if (countryval == 'PY') {
                                                    return ($(this).text() == 'Paraguay');
                                                } else if (countryval == 'PE') {
                                                    return ($(this).text() == 'Peru');
                                                } else if (countryval == 'PH') {
                                                    return ($(this).text() == 'Philippines');
                                                } else if (countryval == 'PN') {
                                                    return ($(this).text() == 'Pitcairn Islands');
                                                } else if (countryval == 'PL') {
                                                    return ($(this).text() == 'Poland');
                                                } else if (countryval == 'PT') {
                                                    return ($(this).text() == 'Portugal');
                                                } else if (countryval == 'PR') {
                                                    return ($(this).text() == 'Puerto Rico');
                                                } else if (countryval == 'QA') {
                                                    return ($(this).text() == 'Qatar');
                                                } else if (countryval == 'CG') {
                                                    return ($(this).text() == 'Republic of the Congo');
                                                } else if (countryval == 'RE') {
                                                    return ($(this).text() == 'Reunion');
                                                } else if (countryval == 'RO') {
                                                    return ($(this).text() == 'Romania');
                                                } else if (countryval == 'RU') {
                                                    return ($(this).text() == 'Russia');
                                                } else if (countryval == 'RW') {
                                                    return ($(this).text() == 'Rwanda');
                                                } else if (countryval == 'BL') {
                                                    return ($(this).text() == 'Saint Barthelemy');
                                                } else if (countryval == 'SH') {
                                                    return ($(this).text() == 'Saint Helena, Ascension & Tristan daCunha');
                                                } else if (countryval == 'KN') {
                                                    return ($(this).text() == 'Saint Kitts and Nevis');
                                                } else if (countryval == 'LC') {
                                                    return ($(this).text() == 'Saint Lucia');
                                                } else if (countryval == 'MF') {
                                                    return ($(this).text() == 'Saint Martin');
                                                } else if (countryval == 'PM') {
                                                    return ($(this).text() == 'Saint Pierre and Miquelon');
                                                } else if (countryval == 'VC') {
                                                    return ($(this).text() == 'Saint Vincent and Grenadines');
                                                } else if (countryval == 'WS') {
                                                    return ($(this).text() == 'Samoa');
                                                } else if (countryval == 'SM') {
                                                    return ($(this).text() == 'San Marino');
                                                } else if (countryval == 'ST') {
                                                    return ($(this).text() == 'Sao Tome and Principe');
                                                } else if (countryval == 'SA') {
                                                    return ($(this).text() == 'Saudi Arabia');
                                                } else if (countryval == 'SN') {
                                                    return ($(this).text() == 'Senegal');
                                                } else if (countryval == 'RS') {
                                                    return ($(this).text() == 'Serbia');
                                                } else if (countryval == 'SC') {
                                                    return ($(this).text() == 'Seychelles');
                                                } else if (countryval == 'SL') {
                                                    return ($(this).text() == 'Sierra Leone');
                                                } else if (countryval == 'SG') {
                                                    return ($(this).text() == 'Singapore');
                                                } else if (countryval == 'SK') {
                                                    return ($(this).text() == 'Slovakia');
                                                } else if (countryval == 'SI') {
                                                    return ($(this).text() == 'Slovenia');
                                                } else if (countryval == 'SB') {
                                                    return ($(this).text() == 'Solomon Islands');
                                                } else if (countryval == 'SO') {
                                                    return ($(this).text() == 'Somalia');
                                                } else if (countryval == 'ZA') {
                                                    return ($(this).text() == 'South Africa');
                                                } else if (countryval == 'GS') {
                                                    return ($(this).text() == 'South Georgia & South Sandwich Islands');
                                                } else if (countryval == 'KR') {
                                                    return ($(this).text() == 'South Korea');
                                                } else if (countryval == 'ES') {
                                                    return ($(this).text() == 'Spain');
                                                } else if (countryval == 'LK') {
                                                    return ($(this).text() == 'Sri Lanka');
                                                } else if (countryval == 'SD') {
                                                    return ($(this).text() == 'Sudan');
                                                } else if (countryval == 'SR') {
                                                    return ($(this).text() == 'Suriname');
                                                } else if (countryval == 'SJ') {
                                                    return ($(this).text() == 'Svalbard and Jan Mayen');
                                                } else if (countryval == 'SZ') {
                                                    return ($(this).text() == 'Swaziland');
                                                } else if (countryval == 'SE') {
                                                    return ($(this).text() == 'Sweden');
                                                } else if (countryval == 'CH') {
                                                    return ($(this).text() == 'Switzerland');
                                                } else if (countryval == 'SY') {
                                                    return ($(this).text() == 'Syria');
                                                } else if (countryval == 'TW') {
                                                    return ($(this).text() == 'Taiwan');
                                                } else if (countryval == 'TJ') {
                                                    return ($(this).text() == 'Tajikistan');
                                                } else if (countryval == 'TZ') {
                                                    return ($(this).text() == 'Tanzania');
                                                } else if (countryval == 'TH') {
                                                    return ($(this).text() == 'Thailand');
                                                } else if (countryval == 'TL') {
                                                    return ($(this).text() == 'Timor-Leste');
                                                } else if (countryval == 'TG') {
                                                    return ($(this).text() == 'Togo');
                                                } else if (countryval == 'TK') {
                                                    return ($(this).text() == 'Tokelau');
                                                } else if (countryval == 'TO') {
                                                    return ($(this).text() == 'Tonga');
                                                } else if (countryval == 'TT') {
                                                    return ($(this).text() == 'Trinidad and Tobago');
                                                } else if (countryval == 'TN') {
                                                    return ($(this).text() == 'Tunisia');
                                                } else if (countryval == 'TR') {
                                                    return ($(this).text() == 'Turkey');
                                                } else if (countryval == 'TM') {
                                                    return ($(this).text() == 'Turkmenistan');
                                                } else if (countryval == 'TC') {
                                                    return ($(this).text() == 'Turks and Caicos Islands');
                                                } else if (countryval == 'TV') {
                                                    return ($(this).text() == 'Tuvalu');
                                                } else if (countryval == 'UG') {
                                                    return ($(this).text() == 'Uganda');
                                                } else if (countryval == 'UA') {
                                                    return ($(this).text() == 'Ukraine');
                                                } else if (countryval == 'AE') {
                                                    return ($(this).text() == 'United Arab Emirates');
                                                } else if (countryval == 'VI') {
                                                    return ($(this).text() == 'United States Virgin Islands');
                                                } else if (countryval == 'UY') {
                                                    return ($(this).text() == 'Uruguay');
                                                } else if (countryval == 'UZ') {
                                                    return ($(this).text() == 'Uzbekistan');
                                                } else if (countryval == 'VU') {
                                                    return ($(this).text() == 'Vanuatu');
                                                } else if (countryval == 'VA') {
                                                    return ($(this).text() == 'Vatican City');
                                                } else if (countryval == 'VE') {
                                                    return ($(this).text() == 'Venezuela');
                                                } else if (countryval == 'VN') {
                                                    return ($(this).text() == 'Vietnam');
                                                } else if (countryval == 'WF') {
                                                    return ($(this).text() == 'Wallis and Futuna');
                                                } else if (countryval == 'EH') {
                                                    return ($(this).text() == 'Western Sahara');
                                                } else if (countryval == 'YE') {
                                                    return ($(this).text() == 'Yemen');
                                                } else if (countryval == 'ZM') {
                                                    return ($(this).text() == 'Zambia');
                                                } else if (countryval == 'ZW') {
                                                    return ($(this).text() == 'Zimbabwe');
                                                } else {
                                                    return ($(this).text() == 'Select Country');
                                                }
                                            }).prop('selected', true);

                                        </script>

                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=merchprofile_support%></label><br>

                                        <input class="form-control col-md-3" type="Text" maxlength="05" id="phonecc" name="phonecc" value="<%=ESAPI.encoder().encodeForHTMLAttribute(phonecc)%>" placeholder="Countrycode" size="35" size="20">


                                        <input class="form-control" type="hidden" id="telno_hidden" class="form-control" maxlength="30"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(telno)%>" name="telno" size="30">
                                        <input class="form-control col-md-9 hidedivphone" type="Text" maxlength="11" id="telno"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(telno)%>" onchange="setvalue('telno')" size="30"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hidePhone('telno')"></span>

                                    </div>



                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=merchprofile_fraud%></label>
                                        <input  type="Text" class="form-control" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(maxScoreAllowed)%>" name="maxscoreallowed"
                                                size="30">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=merchprofile_reversal%> </label>
                                        <input  type="Text" class="form-control" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(maxScoreAutoReversal)%>" name="maxscoreautoreversal"
                                                size="30">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=merchprofile_time%> </label>
                                            <select size="1"  name="timezone" class="form-control">
                                                <option value=""><%=merchprofile_select_time_zone%></option>
                                                <%
                                                    Set timezoneSet = timezoneHash.keySet();
                                                    Iterator itr = timezoneSet.iterator();
                                                    String selected4 = "";
                                                    String timezonekey = "";
                                                    String timezonevalue = "";
                                                    while (itr.hasNext())
                                                    {
                                                        timezonekey = (String) itr.next();
                                                        timezonevalue = timezoneHash.get(timezonekey);

                                                        if (timezonekey.equals(timezone))
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

                                    <%-- Start Radio Button--%>
                                    <div class="form-group col-md-12 has-feedback">
                                        <label class="col-md-5 control-label" ><%=merchprofile_proof%>
                                            <a href="" onclick="return showHelp('<%="HIGHRISKALERT"%>');"><font size="1" color="#001962" ><b></b></font></a></label>
                                        <%
                                            String yesselected = "";
                                            String noselected = "";
                                            if ((ESAPI.encoder().encodeForHTML(hralertproof)).equalsIgnoreCase("Y"))
                                            {
                                                yesselected = "checked";
                                            }
                                            else
                                            {
                                                noselected = "checked";
                                            }
                                        %>

                                        <div class="col-md-4">
                                            <input type="radio"  style="color:#008000; " maxlength="10"  value="Y" name="hralertproof" <%=yesselected%>>&nbsp;<%=merchprofile_Yes%>&nbsp;&nbsp;
                                            <input type="radio" value="N" name="hralertproof" <%=noselected%>>&nbsp;<%=merchprofile_No%>
                                        </div>
                                    </div>

                                    <div class="form-group col-md-12 has-feedback">
                                        <label class="col-md-5 control-label"><%=merchprofile_billing%>
                                            <a href=""  onclick="return showHelp('<%="DATAMISMATCHALERT"%>');"><font size="1" color="#001962"><b></b></font></a></label>
                                        <%
                                            yesselected = "";
                                            noselected = "";

                                            if ((ESAPI.encoder().encodeForHTML(dataMisMatchproof)).equalsIgnoreCase("Y"))
                                            {
                                                yesselected = "checked";
                                            }
                                            else
                                            {
                                                noselected = "checked";
                                            }
                                        %>
                                        <div class="col-md-4">
                                            <input type="radio" class="textb" value="Y" name="datamismatchproof" <%=yesselected%>>&nbsp;<%=merchprofile_Yes1%>&nbsp;&nbsp;
                                            <input type="radio" value="N" name="datamismatchproof" <%=noselected%>>&nbsp;<%=merchprofile_No1%>
                                        </div>
                                    </div>

                                    <div class="form-group col-md-12 has-feedback">
                                        <label class="col-md-5 control-label"><%=merchprofile_risk%>
                                            <a href="" onclick="return showHelp('<%="MITIGATIONMAIL"%>');"><font size="1" color="#001962"><b>?</b></font></a></label>
                                        <%
                                            yesselected = "";
                                            noselected = "";

                                            if ((ESAPI.encoder().encodeForHTML(mitigationMail)).equalsIgnoreCase("Y"))
                                            {

                                                yesselected = "checked";
                                            }
                                            else
                                            {
                                                noselected = "checked";
                                            }
                                        %>
                                        <div class="col-md-4">
                                            <input type="radio" class="textb" value="Y" name="mitigationmail" <%=yesselected%>>&nbsp;<%=merchprofile_Yes2%>&nbsp;&nbsp;
                                            <input type="radio" value="N" name="mitigationmail" <%=noselected%>>&nbsp;<%=merchprofile_No2%>
                                        </div>
                                    </div>
                                            </div>
                                        </div>
                                </div>
                            </div>
                        </div>

                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=merchprofile_contact1%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>

                            <div class="widget-content padding">
                                <div id="horizontal-form">                        <%-- End Radio Button--%>
                                    <div class="form-group col-md-12 has-feedback">
                                        <div class="table-responsive" class="container">

                                            <%-- <table align="center" width="90%" border="1">--%>
                                            <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                                <tr>
                                                    <td align="left" id="thead_main" style="color: white;vertical-align: middle;text-align: center;">&nbsp;<%=merchprofile_main_contact%></td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_persons)%>" name="contact_persons" placeholder="Name*">
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="hidden" id="contact_emails_hidden" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_emails)%>" name="contact_emails" placeholder="Email*">
                                                        <input class="form-control" type="text" id="contact_emails" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_emails)%>" placeholder="Email*" onchange="setvalue('contact_emails')"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('contact_emails')"></span>

                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="hidden" id="maincontact_ccmailid_hidden" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_cCmailid)%>" name="maincontact_ccmailid" placeholder="Email Cc" >
                                                        <input class="form-control" type="text" id="maincontact_ccmailid" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_cCmailid)%>" onchange="setvalue('maincontact_ccmailid')" placeholder="Email Cc" ><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('maincontact_ccmailid')" ></span>

                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" id="maincontact_phone_hidden" type="hidden" maxlength="11" value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_phone)%>" name="maincontact_phone" size="35" placeholder="Phone">
                                                        <input class="form-control" id="maincontact_phone" type="text" maxlength="11" value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_phone)%>" onchange="setvalue('maincontact_phone')" size="35" placeholder="Phone"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hidePhone('maincontact_phone')"></span>

                                                    </td>
                                                </tr>

                                                <tr>
                                                    <td align="left" id="thead_main" style="color: white;vertical-align: middle;text-align: center;">&nbsp;<%=merchprofile_customer%></td>
                                                    <td align="center" width="20%">
                                                        <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_persons)%>" name="support_persons" placeholder="Name*">
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="hidden" id="support_emails_hidden" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_emails)%>" name="support_emails" placeholder="Email*">
                                                        <input class="form-control" type="text" id="support_emails" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_emails)%>" onchange="setvalue('support_emails')" placeholder="Email*"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('support_emails')"></span>

                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="hidden" id="support_ccmailid_hidden"  maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_cCmailid)%>" name="support_ccmailid" placeholder="Email Cc" >
                                                        <input class="form-control" type="text" id="support_ccmailid" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_cCmailid)%>" onchange="setvalue('support_ccmailid')" placeholder="Email Cc" ><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('support_ccmailid')"></span>

                                                    </td >
                                                    <td align="center" width="15%">
                                                        <input class="form-control" type="hidden" id="support_phone_hidden" maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_phone)%>" name="support_phone" size="35" placeholder="Phone">
                                                        <input class="form-control" type="Text" id="support_phone"  maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_phone)%>" onchange="setvalue('support_phone')" size="35" placeholder="Phone"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hidePhone('support_phone')"></span>

                                                    </td>
                                                </tr>

                                                <tr>
                                                    <td align="left" id="thead_main" style="color: white;vertical-align: middle;text-align: center;">&nbsp;<%=merchprofile_charge_back%></td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_name)%>" name="cbcontact_name" placeholder="Name">
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="hidden" id="cbcontact_mailid_hidden" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_mailId)%>"name="cbcontact_mailid" placeholder="Email">
                                                        <input class="form-control" type="text" id="cbcontact_mailid" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_mailId)%>" onchange="setvalue('cbcontact_mailid')" placeholder="Email"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('cbcontact_mailid')"></span>

                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="hidden" id="cbcontact_ccmailid_hidden" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_cCmailId)%>" name="cbcontact_ccmailid" placeholder="Email Cc" >
                                                        <input class="form-control" type="text" id="cbcontact_ccmailid" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_cCmailId)%>" onchange="setvalue('cbcontact_ccmailid')" placeholder="Email Cc" ><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('cbcontact_ccmailid')"></span>

                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="hidden" id="cbcontact_phone_hidden"  maxlength="50" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_phone)%>" name="cbcontact_phone" size="35" placeholder="Phone">
                                                        <input class="form-control" type="Text" id="cbcontact_phone"  maxlength="50" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_phone)%>" onchange="setvalue('cbcontact_phone')" size="35" placeholder="Phone"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hidePhone('cbcontact_phone')"></span>

                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="left" id="thead_main" style="color: white;vertical-align: middle;text-align: center;">&nbsp;<%=merchprofile_refund%></td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_name)%>" name="refundcontact_name" placeholder="Name">
                                                    </td>
                                                    <td align="center"class="text">
                                                        <input class="form-control" type="hidden" id="refundcontact_mailid_hidden" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_mailId)%>"name="refundcontact_mailid" placeholder="Email">
                                                        <input class="form-control" type="text" id="refundcontact_mailid" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_mailId)%>" onchange="setvalue('refundcontact_mailid')" placeholder="Email"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('refundcontact_mailid')"></span>
                                                    </td>
                                                    <td align="center" >
                                                        <input class="form-control" type="hidden" id="refundcontact_ccmailid_hidden" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_cCmail)%>" name="refundcontact_ccmailid" placeholder="Email Cc">
                                                        <input class="form-control" type="text" id="refundcontact_ccmailid" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_cCmail)%>" onchange="setvalue('refundcontact_ccmailid')" placeholder="Email Cc"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('refundcontact_ccmailid')"></span>
                                                    </td>
                                                    <td align="center"class="text">
                                                        <input class="form-control" type="hidden" id="refundcontact_phone_hidden" maxlength="50" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_phone)%>" name="refundcontact_phone" size="35" placeholder="Phone">
                                                        <input class="form-control" type="Text" id="refundcontact_phone" maxlength="50" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_phone)%>" onchange="setvalue('refundcontact_phone')" size="35" placeholder="Phone"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hidePhone('refundcontact_phone')"></span>
                                                    </td>
                                                </tr>
                                                <tr >
                                                    <td align="left" id="thead_main" style="color: white;vertical-align: middle;text-align: center;">&nbsp;<%=merchprofile_sales%></td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_name)%>" name="salescontact_name" placeholder="Name" >
                                                    </td>
                                                    <td align="center" class="text">
                                                        <input class="form-control" type="hidden" id="salescontact_mailid_hidden" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_mailid)%>"name="salescontact_mailid" placeholder="Email">
                                                        <input class="form-control" type="text" id="salescontact_mailid" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_mailid)%>" onchange="setvalue('salescontact_mailid')"  placeholder="Email"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('salescontact_mailid')"></span>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="hidden" id="salescontact_ccmailid_hidden" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_cCmailId)%>" name="salescontact_ccmailid" placeholder="Email Cc">
                                                        <input class="form-control" type="text" id="salescontact_ccmailid" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_cCmailId)%>" onchange="setvalue('salescontact_ccmailid')" placeholder="Email Cc"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('salescontact_ccmailid')"></span>
                                                    </td>
                                                    <td align="center"class="text">
                                                        <input class="form-control" type="hidden" id="salescontact_phone_hidden" maxlength="50" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_phone)%>" name="salescontact_phone" size="35" placeholder="Phone">
                                                        <input class="form-control" type="Text" id="salescontact_phone" maxlength="50" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_phone)%>" onchange="setvalue('salescontact_phone')" size="35" placeholder="Phone"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hidePhone('salescontact_phone')"></span>
                                                    </td>
                                                </tr>
                                                <tr >
                                                    <td align="left" id="thead_main" style="color: white;vertical-align: middle;text-align: center;">&nbsp;<%=merchprofile_billing1%></td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_name)%>" name="billingcontact_name" placeholder="Name">
                                                    </td>
                                                    <td align="center"class="text">
                                                        <input class="form-control" type="hidden" id="billingcontact_mailid_hidden" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_mailid)%>"name="billingcontact_mailid" placeholder="Email">
                                                        <input class="form-control" type="text" id="billingcontact_mailid" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_mailid)%>" onchange="setvalue('billingcontact_mailid')" placeholder="Email"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('billingcontact_mailid')"></span>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="hidden" id="billingcontact_ccmailid_hidden" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_cCmailId)%>" name="billingcontact_ccmailid" placeholder="Email Cc">
                                                        <input class="form-control" type="text" id="billingcontact_ccmailid" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_cCmailId)%>" onchange="setvalue('billingcontact_ccmailid')" placeholder="Email Cc"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('billingcontact_ccmailid')"></span>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="hidden" id="billingcontact_phone_hidden" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_phone)%>" name="billingcontact_phone" placeholder="Phone">
                                                        <input class="form-control" type="Text" id="billingcontact_phone" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_phone)%>" onchange="setvalue('billingcontact_phone')" placeholder="Phone"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hidePhone('billingcontact_phone')"></span>
                                                    </td>
                                                </tr>
                                                <tr >
                                                    <td align="left" id="thead_main" style="color: white;vertical-align: middle;text-align: center;">&nbsp;<%=merchprofile_fruadcontact%></td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_name)%>" name="fraudcontact_name" placeholder="Name">
                                                    </td>
                                                    <td align="center"class="text">
                                                        <input class="form-control" type="hidden" id="fraudcontact_mailid_hidden" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_mailid)%>" name="fraudcontact_mailid" placeholder="Email">
                                                        <input class="form-control" type="text" id="fraudcontact_mailid" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_mailid)%>" onchange="setvalue('fraudcontact_mailid')" placeholder="Email"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('fraudcontact_mailid')"></span>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="hidden" id="fraudcontact_ccmailid_hidden" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_cCmailId)%>" name="fraudcontact_ccmailid" placeholder="Email Cc">
                                                        <input class="form-control" type="text" id="fraudcontact_ccmailid" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_cCmailId)%>" onchange="setvalue('fraudcontact_ccmailid')" placeholder="Email Cc"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('fraudcontact_ccmailid')"></span>
                                                    </td>
                                                    <td align="center"class="text">
                                                        <input class="form-control" type="hidden" id="fraudcontact_phone_hidden" maxlength="50" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_phone)%>" name="fraudcontact_phone" size="35" placeholder="Phone">
                                                        <input class="form-control" type="Text" id="fraudcontact_phone" maxlength="50" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_phone)%>" onchange="setvalue('fraudcontact_phone')" size="35" placeholder="Phone"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hidePhone('fraudcontact_phone')"></span>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="left" id="thead_main" style="color: white;vertical-align: middle;text-align: center;">&nbsp;<%=merchprofile_technical%></td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_name)%>" name="technicalcontact_name" placeholder="Name">
                                                    </td>
                                                    <td align="center"class="text">
                                                        <input class="form-control" type="hidden" id="technicalcontact_mailid_hidden" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_mailId)%>" name="technicalcontact_mailid" placeholder="Email">
                                                        <input class="form-control" type="text" id="technicalcontact_mailid" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_mailId)%>" onchange="setvalue('technicalcontact_mailid')" placeholder="Email"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('technicalcontact_mailid')"></span>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="hidden" id="technicalcontact_ccmailid_hidden" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_cCmailId)%>" name="technicalcontact_ccmailid" placeholder="Email Cc">
                                                        <input class="form-control" type="text" id="technicalcontact_ccmailid" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_cCmailId)%>" onchange="setvalue('technicalcontact_ccmailid')" placeholder="Email Cc"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hideemail('technicalcontact_ccmailid')"></span>
                                                    </td>
                                                    <td align="center"class="text">
                                                        <input class="form-control" type="hidden" id="technicalcontact_phone_hidden" maxlength="11" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_phone)%>" name="technicalcontact_phone" size="35" placeholder="Phone">
                                                        <input class="form-control" type="Text" id="technicalcontact_phone" maxlength="11" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_phone)%>" onchange="setvalue('technicalcontact_phone')" size="35" placeholder="Phone"><span toggle="#password-field" class="fa fa-fw fa-eye-slash field-icon toggle-password" onclick="hidePhone('technicalcontact_phone')"></span>
                                                    </td>
                                                </tr>
                                            </table>
                                        </div>
                                    </div>
                                    <div class="form-group col-md-12 has-feedback">
                                        <center>
                                            <label >&nbsp;</label>
                                           <%--<button type="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=merchprofile_update%></button>--%>
                                            <button type="button" class="btn btn-default" style="display: -webkit-box;" data-toggle="modal" data-target="#myModal"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=merchprofile_update%></button>
                                        </center>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
</bod