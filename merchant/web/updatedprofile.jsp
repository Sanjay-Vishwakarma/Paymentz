<%@ page language="java" import="java.util.Hashtable" %>
<%@ page import = "org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ include file="Top.jsp" %>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));%>

<html>
<head>

    <title><%=company%> Merchant Settings > Merchant Profile > Updated</title>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
    </script>
    <script language="javascript">
        function comName()
        {
            var hat = this.document.form1.comtype.selectedIndex
            var hatto = this.document.form1.comtype.options[hat].value

            if(hatto == 'Propritory')
            {
                document.getElementById('proprietor').style.display=""
                document.getElementById('partnership').style.display="none"
                document.getElementById('private').style.display="none"
                document.getElementById('submit').style.display=""
            }
            else if(hatto == 'Partnership')
            {
                document.getElementById('proprietor').style.display="none"
                document.getElementById('partnership').style.display=""
                document.getElementById('private').style.display="none"
                document.getElementById('submit').style.display=""
            }
            else if(hatto == 'Private')
            {
                document.getElementById('proprietor').style.display="none"
                document.getElementById('partnership').style.display="none"
                document.getElementById('private').style.display=""
                document.getElementById('submit').style.display=""
            }
            else
            {
                document.getElementById('proprietor').style.display="none"
                document.getElementById('partnership').style.display="none"
                document.getElementById('private').style.display="none"
                document.getElementById('submit').style.display="none"
            }

        }
    </script>
    <%
        Functions functions = new Functions();
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

        String updatedprofile_Merchant_Profile = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Merchant_Profile"))?rb1.getString("updatedprofile_Merchant_Profile"): "Merchant Profile";
        String updatedprofile_CompanyName = !functions.isEmptyOrNull(rb1.getString("updatedprofile_CompanyName"))?rb1.getString("updatedprofile_CompanyName"): "Company Name*";
        String updatedprofile_email = !functions.isEmptyOrNull(rb1.getString("updatedprofile_email"))?rb1.getString("updatedprofile_email"): "Notification email address *";
        String updatedprofile_Brand_Name = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Brand_Name"))?rb1.getString("updatedprofile_Brand_Name"): "Brand Name";
        String updatedprofile_siteurl = !functions.isEmptyOrNull(rb1.getString("updatedprofile_siteurl"))?rb1.getString("updatedprofile_siteurl"): "Site URL*";
        String updatedprofile_supportnumber = !functions.isEmptyOrNull(rb1.getString("updatedprofile_supportnumber"))?rb1.getString("updatedprofile_supportnumber"): "Support Number*";
        String updatedprofile_Contact = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Contact"))?rb1.getString("updatedprofile_Contact"): "Contact Fax Number";
        String updatedprofile_Pin_Code = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Pin_Code"))?rb1.getString("updatedprofile_Pin_Code"): "Pin Code";
        String updatedprofile_City = !functions.isEmptyOrNull(rb1.getString("updatedprofile_City"))?rb1.getString("updatedprofile_City"): "City";
        String updatedprofile_State = !functions.isEmptyOrNull(rb1.getString("updatedprofile_State"))?rb1.getString("updatedprofile_State"): "State";
        String updatedprofile_Country = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Country"))?rb1.getString("updatedprofile_Country"): "Country*";
        String updatedprofile_Address = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Address"))?rb1.getString("updatedprofile_Address"): "Address";
        String updatedprofile_Fraud_Alert = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Fraud_Alert"))?rb1.getString("updatedprofile_Fraud_Alert"): "Fraud Alert Score*";
        String updatedprofile_Fraud_Auto = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Fraud_Auto"))?rb1.getString("updatedprofile_Fraud_Auto"): "Fraud Auto Reversal Score*";
        String updatedprofile_proof = !functions.isEmptyOrNull(rb1.getString("updatedprofile_proof"))?rb1.getString("updatedprofile_proof"): "Proof require for Billing Address data mismatch";
        String updatedprofile_send_risk = !functions.isEmptyOrNull(rb1.getString("updatedprofile_send_risk"))?rb1.getString("updatedprofile_send_risk"): "Send Risk Mitigation & Fraud ReportMail";
        String updatedprofile_high_risk = !functions.isEmptyOrNull(rb1.getString("updatedprofile_high_risk"))?rb1.getString("updatedprofile_high_risk"): "Proof require for HIGH RISK FRAUD ALERT transaction";
        String updatedprofile_Time_Zone = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Time_Zone"))?rb1.getString("updatedprofile_Time_Zone"): "Time Zone";
        String updatedprofile_select_timezone = !functions.isEmptyOrNull(rb1.getString("updatedprofile_select_timezone"))?rb1.getString("updatedprofile_select_timezone"): "--Select Time Zone--";
        String updatedprofile_Contact_Details = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Contact_Details"))?rb1.getString("updatedprofile_Contact_Details"): "Contact Details";
        String updatedprofile_Main = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Main"))?rb1.getString("updatedprofile_Main"): "Main";
        String updatedprofile_Contact1 = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Contact1"))?rb1.getString("updatedprofile_Contact1"): "Contact*";
        String updatedprofile_Customer = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Customer"))?rb1.getString("updatedprofile_Customer"): "Customer";
        String updatedprofile_Support = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Support"))?rb1.getString("updatedprofile_Support"): "Support*:";
        String updatedprofile_Chargeback = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Chargeback"))?rb1.getString("updatedprofile_Chargeback"): "Chargeback";
        String updatedprofile_Contact2 = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Contact2"))?rb1.getString("updatedprofile_Contact2"): "Contact:";
        String updatedprofile_Refund = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Refund"))?rb1.getString("updatedprofile_Refund"): "Refund";
        String updatedprofile_Sales = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Sales"))?rb1.getString("updatedprofile_Sales"): "Sales";
        String updatedprofile_Billing = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Billing"))?rb1.getString("updatedprofile_Billing"): "Billing";
        String updatedprofile_Fraud = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Fraud"))?rb1.getString("updatedprofile_Fraud"): "Fraud";
        String updatedprofile_Technical = !functions.isEmptyOrNull(rb1.getString("updatedprofile_Technical"))?rb1.getString("updatedprofile_Technical"): "Technical";

        String disabled = "disabled";
        int result = merchants.isAuthorised(session);

        if (result > 0)
        {
            disabled = "";

        }

        String company_type = "";
        String proprietor = "";
        String proprietorAddress = "";
        String proprietorPhNo = "";
        String OrganisationRegNo = "";
        String partnerNameAddress = "";
        String directorsNameAddress = "";
        String pan = "";
        String directors = "";
        String employees = "";
        String potentialbusiness = "";
        String registeredaddress = "";
        String bussinessaddress = "";
        String notifyemail = "";
        String timezone = "";
        String acdetails = "";
        String bankname = "";
        String branch = "";
        String AcType = "";
        String AcNumber = "";
        String disabled1 = "";

        Hashtable details = (Hashtable) request.getAttribute("details");
        if (details != null)


            if ((String) details.get("company_type") != null)
            {
                company_type = (String) details.get("company_type");

            }
        if ((String) details.get("proprietor") != null) proprietor =(String) details.get("proprietor");
        if ((String) details.get("proprietorAddress") != null)
            proprietorAddress = (String) details.get("proprietorAddress");
        if ((String) details.get("proprietorPhNo") != null) proprietorPhNo = (String) details.get("proprietorPhNo");
        if ((String) details.get("OrganisationRegNo") != null)
            OrganisationRegNo = (String) details.get("OrganisationRegNo");
        if ((String) details.get("partnerNameAddress") != null)
            partnerNameAddress = (String) details.get("partnerNameAddress");
        if ((String) details.get("directorsNameAddress") != null)
            directorsNameAddress = (String) details.get("directorsNameAddress");
        if ((String) details.get("pan") != null) pan = (String) details.get("pan");
        if ((String) details.get("directors") != null) directors = (String) details.get("directors");
        if ((String) details.get("employees") != null) employees = (String) details.get("employees");
        if ((String) details.get("potentialbusiness") != null)
            potentialbusiness = (String) details.get("potentialbusiness");
        if ((String) details.get("registeredaddress") != null)
            registeredaddress = (String) details.get("registeredaddress");
        if ((String) details.get("bankname") != null) bankname = (String) details.get("bankname");
        if ((String) details.get("branch") != null) branch =(String) details.get("branch");
        if ((String) details.get("acctype") != null) AcType = (String) details.get("acctype");
        if ((String) details.get("AcNumber") != null) AcNumber = (String) details.get("AcNumber");
        if ((String) details.get("acdetails") != null) acdetails = (String) details.get("acdetails");
        if ((String) details.get("bussinessaddress") != null) bussinessaddress =(String) details.get("bussinessaddress");
        if ((String) details.get("notifyemail") != null) notifyemail = (String) details.get("notifyemail");
        if ((String) details.get("timezone") != null) timezone = (String) details.get("timezone");
        if (functions.isValueNull(timezone))
            disabled1 = "disabled";
        else
            disabled1 = "";
    %>
    <%--    <style type="text/css">

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

        </style>--%>
</head>
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<%--<link rel="stylesheet" href=" /merchant/transactionCSS/css/main.css">
<script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
<script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>
<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>--%>
<%--<%
    Hashtable details = (Hashtable) request.getAttribute("details");
%>--%>

<body class="pace-done widescreen fixed-left-void">

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <form action="/merchant/servlet/UpdateMerchant?ctoken=<%=ctoken%>" method="post" name="form1">
                <div class="row">

                    <div class="col-sm-12 portlets ui-sortable">

                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=updatedprofile_Merchant_Profile%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <%

                                if (request.getAttribute("MES") != null)
                                {

                                    String mes = (String) request.getAttribute("MES");
                                    String errormsg = (String) request.getAttribute("error");
                                   /* System.out.println(mes);
                                    System.out.println(errormsg);*/
                                    if (mes.equals("F"))
                                    {
                                        //out.println("<center><li class=\"alert alert-danger alert-dismissable\"><b>"+errormsg+"</b></li></center><br/><br/>");
                                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");

                                    }
                                    else if (mes.equals("X"))
                                    {
                                        //out.println("<center><li class=\"alert alert-danger alert-dismissable\"><b>"+errormsg+"</b></li></center><br/><br/>");
                                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");

                                    }
                                    else
                                    {
                                        //out.println("<center><li class=\"alert alert-danger alert-dismissable\"><b>"+mes+"</b></li></center><br/><br/>");
                                        out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + mes + "</h5>");
                                    }
                                }
                            %>
                            <div class="widget-content padding">
                                <div id="horizontal-form">

                                    <%--<input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">--%>
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">


                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=updatedprofile_CompanyName%></label>
                                        <input type="text" name="company_name" class="form-control" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("company_name"))%>">
                                    </div>


                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=updatedprofile_email%></label>
                                        <input  class="form-control"  type="Text"  disabled maxlength="100"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(notifyemail)%>"
                                                name="notifyemail" size="30">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=updatedprofile_Brand_Name%></label>
                                        <input class="form-control" id="inputSuccess1" type="Text" disabled maxlength="100"  value="<%=ESAPI.encoder().encodeForHTML((String) details.get("brandname"))%>"
                                               name="brandname" size="30">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=updatedprofile_siteurl%></label>
                                        <input  class="form-control"  type="Text" maxlength="100" disabled value="<%=ESAPI.encoder().encodeForHTML((String) details.get("sitename"))%>"
                                                name="sitename" size="30">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=updatedprofile_supportnumber%></label>
                                        <input class="form-control" type="Text"class="form-control" maxlength="30" disabled value="<%=ESAPI.encoder().encodeForHTML((String) details.get("telno"))%>" name="telno"
                                               size="30">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback ">
                                        <label  ><%=updatedprofile_Contact%></label>
                                        <input class="form-control" type="Text" class="form-control" maxlength="30" disabled value="<%=ESAPI.encoder().encodeForHTML((String) details.get("faxno"))%>" name="faxno" size="30">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=updatedprofile_Pin_Code%></label>
                                        <input class="form-control" type="Text" maxlength="30" disabled value="<%=ESAPI.encoder().encodeForHTML((String) details.get("zip"))%>" name="zip" size="30">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=updatedprofile_City%></label>
                                        <input class="form-control" type="Text"class="form-control" maxlength="50" disabled value="<%=ESAPI.encoder().encodeForHTML((String) details.get("city"))%>" name="city" size="30">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=updatedprofile_State%></label>
                                        <input class="form-control" type="Text" maxlength="50" disabled value="<%=ESAPI.encoder().encodeForHTML((String) details.get("state"))%>" name="state" size="30">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=updatedprofile_Country%></label>
                                        <input class="form-control" type="Text" maxlength="50" class="form-control" disabled value="<%=ESAPI.encoder().encodeForHTML((String) details.get("country"))%>" name="country" size="30">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=updatedprofile_Address%></label>
                                        <input type="text" name="address" rows="5" disabled class="form-control" cols="30" value="<%=ESAPI.encoder().encodeForHTML((String) details.get("address"))%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=updatedprofile_Fraud_Alert%></label>
                                        <input  type="Text" class="form-control" maxlength="50" disabled value="<%=ESAPI.encoder().encodeForHTML((String) details.get("maxscoreallowed"))%>" name="maxscoreallowed"
                                                size="30">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=updatedprofile_Fraud_Auto%></label>
                                        <input  type="Text" class="form-control" maxlength="50" disabled value="<%=ESAPI.encoder().encodeForHTML((String) details.get("maxscoreautoreversal"))%>" name="maxscoreautoreversal"
                                                size="30">
                                    </div>



                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=updatedprofile_proof%></label>
                                        <%
                                            String datamismatchproof = "No";
                                            if ((ESAPI.encoder().encodeForHTML((String) details.get("datamismatchproof"))).equalsIgnoreCase("Y"))
                                                datamismatchproof = "Yes";
                                        %>
                                        <input  type="Text" class="form-control" disabled value="<%=datamismatchproof%>">
                                    </div>


                                    <div class="form-group col-md-4 has-feedback">
                                        <label  ><%=updatedprofile_send_risk%></label>
                                        <%
                                            String mitigationmail = "No";
                                            if ((ESAPI.encoder().encodeForHTML((String) details.get("mitigationmail"))).equalsIgnoreCase("Y"))
                                                mitigationmail = "Yes";
                                        %>

                                        <input  type="Text" class="form-control" disabled value="<%=mitigationmail%>">
                                    </div>
                                    <div class="form-group col-md-4 has-feedback">
                                        <label ><%=updatedprofile_high_risk%></label>
                                        <%
                                            String proofrequire = "No";
                                            if ((ESAPI.encoder().encodeForHTML((String) details.get("hralertproof"))).equalsIgnoreCase("Y"))
                                                proofrequire = "Yes";
                                        %>
                                        <input  type="Text" class="form-control" disabled value="<%=proofrequire%>">
                                    </div>

                                        <div class="form-group col-md-4 has-feedback">
                                            <label  ><%=updatedprofile_Time_Zone%></label>
                                            <select size="1"  name="timezone" class="form-control" <%=disabled1%>>
                                                <option value=""><%=updatedprofile_select_timezone%></option>
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
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=updatedprofile_Contact_Details%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>

                            <div class="widget-content padding">
                                <div id="horizontal-form">
                                    <div class="form-group col-md-12 has-feedback">
                                        <div class="table-responsive">

                                            <%-- <table align="center" width="90%" border="1">--%>
                                            <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                                <tr>
                                                    <td align="left" id="thead_main" style="color: white;vertical-align: middle;text-align: center;"><%=updatedprofile_Main%>&nbsp<%=updatedprofile_Contact1%></td>
                                                    <td align="center">
                                                        <input class="form-control"type="text" disabled maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("contact_persons"))%>" name="contact_persons" placeholder="Name*" readonly>
                                                    </td>
                                                    <td align="center" class="txtbox">
                                                        <input class="form-control" type="text" disabled maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("contact_emails"))%>" name="contact_emails" placeholder="Email*" readonly>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" disabled maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("maincontact_ccmailid"))%>" name="maincontact_ccmailid" placeholder="Email Cc"readonly >
                                                    </td>
                                                    <td align="right"class="txtbox">
                                                        <input class="form-control" type="Text" disabled maxlength="11" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("maincontact_phone"))%>" name="maincontact_phone" size="35" placeholder="Phone" readonly>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="left" id="thead_main" style="color: white;vertical-align: middle;text-align: center;"><%=updatedprofile_Customer%>&nbsp<%=updatedprofile_Support%></td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("support_persons"))%>" name="support_persons" placeholder="Name*"readonly>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("support_emails"))%>" name="support_emails" placeholder="Email*" readonly>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("support_ccmailid"))%>" name="support_ccmailid" placeholder="Email Cc"readonly >
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="Text" maxlength="11" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("support_phone"))%>" name="support_phone" size="35" placeholder="Phone" readonly>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="left" id="thead_main" style="color: white;vertical-align: middle;text-align: center;"><%=updatedprofile_Chargeback%>&nbsp;<%=updatedprofile_Contact2%></td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("cbcontact_name"))%>" name="cbcontact_name" placeholder="Name"readonly>
                                                    </td>
                                                    <td align="center"class="txtbox">
                                                        <input class="form-control" type="text" maxlength="100" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("cbcontact_mailid"))%>"name="cbcontact_mailid" placeholder="Email" readonly>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("cbcontact_ccmailid"))%>" name="cbcontact_ccmailid" placeholder="Email Cc" readonly>
                                                    </td>
                                                    <td align="right"class="txtbox">
                                                        <input class="form-control" type="Text" maxlength="50" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("cbcontact_phone"))%>" name="cbcontact_phone" size="35" placeholder="Phone" readonly>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="left" id="thead_main" style="color: white;vertical-align: middle;text-align: center;"><%=updatedprofile_Refund%>&nbsp<%=updatedprofile_Contact2%></td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("refundcontact_name"))%>" name="refundcontact_name" placeholder="Name" readonly>
                                                    </td>
                                                    <td align="center"class="txtbox">
                                                        <input class="form-control" type="text" maxlength="100" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("refundcontact_mailid"))%>"name="refundcontact_mailid" placeholder="Email" readonly>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("refundcontact_ccmailid"))%>" name="refundcontact_ccmailid" placeholder="Email Cc" readonly>
                                                    </td>
                                                    <td align="right"class="txtbox">
                                                        <input class="form-control" type="Text" maxlength="50" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("refundcontact_phone"))%>" name="refundcontact_phone" size="35" placeholder="Phone" readonly>
                                                    </td>
                                                </tr>
                                                <tr >
                                                    <td align="left" id="thead_main" style="color: white;vertical-align: middle;text-align: center;"><%=updatedprofile_Sales%>&nbsp;<%=updatedprofile_Contact2%></td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("salescontact_name"))%>" name="salescontact_name" placeholder="Name" readonly>
                                                    </td>
                                                    <td align="center" class="txtbox">
                                                        <input class="form-control" type="text" maxlength="100" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("salescontact_mailid"))%>"name="salescontact_mailid" placeholder="Email" readonly>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("salescontact_ccmailid"))%>" name="salescontact_ccmailid" placeholder="Email Cc" readonly>
                                                    </td>
                                                    <td align="right"class="txtbox">
                                                        <input class="form-control" type="Text" maxlength="50" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("salescontact_phone"))%>" name="salescontact_phone" size="35" placeholder="Phone" readonly>
                                                    </td>
                                                </tr>
                                                <tr >
                                                    <td align="left" id="thead_main" style="color: white;vertical-align: middle;text-align: center;"><%=updatedprofile_Billing%>&nbsp<%=updatedprofile_Contact2%></td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("billingcontact_name"))%>" name="billingcontact_name" placeholder="Name" readonly>
                                                    </td>
                                                    <td align="center"class="txtbox">
                                                        <input class="form-control" type="text" maxlength="100" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("billingcontact_mailid"))%>"name="billingcontact_mailid" placeholder="Email" readonly>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("billingcontact_ccmailid"))%>" name="billingcontact_ccmailid" placeholder="Email Cc" readonly>
                                                    </td>
                                                    <td align="right">
                                                        <input class="form-control" type="Text" maxlength="50" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("billingcontact_phone"))%>" name="billingcontact_phone" size="35" placeholder="Phone" readonly>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="left" id="thead_main" style="color: white;vertical-align: middle;text-align: center;"><%=updatedprofile_Fraud%>&nbsp<%=updatedprofile_Contact2%></td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("fraudcontact_name"))%>" name="fraudcontact_name" placeholder="Name" readonly>
                                                    </td>
                                                    <td align="center"class="txtbox">
                                                        <input class="form-control" type="text" maxlength="100" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("fraudcontact_mailid"))%>" name="fraudcontact_mailid" placeholder="Email" readonly>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("fraudcontact_ccmailid"))%>" name="fraudcontact_ccmailid" placeholder="Email Cc" readonly>
                                                    </td>
                                                    <td align="right"class="txtbox">
                                                        <input class="form-control" type="Text" maxlength="50" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("fraudcontact_phone"))%>" name="fraudcontact_phone" size="35" placeholder="Phone" readonly>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="left" id="thead_main" style="color: white;vertical-align: middle;text-align: center;"><%=updatedprofile_Technical%>&nbsp<%=updatedprofile_Contact2%></td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("technicalcontact_name"))%>" name="technicalcontact_name" placeholder="Name" readonly>
                                                    </td>
                                                    <td align="center"class="txtbox">
                                                        <input class="form-control" type="text" maxlength="100" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("technicalcontact_mailid"))%>" name="technicalcontact_mailid" placeholder="Email" readonly>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("technicalcontact_ccmailid"))%>" name="technicalcontact_ccmailid" placeholder="Email Cc" readonly>
                                                    </td>
                                                    <td align="right"class="txtbox">
                                                        <input class="form-control" type="Text" maxlength="11" disabled value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)details.get("technicalcontact_phone"))%>" name="technicalcontact_phone" size="35" placeholder="Phone" readonly>
                                                    </td>
                                                </tr>
                                            </table>
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
</div>
</body>
</html>