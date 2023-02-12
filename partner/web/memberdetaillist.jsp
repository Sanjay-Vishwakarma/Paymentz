<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="top.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
    String partnerid = String.valueOf(session.getAttribute("partnerId"));
    session.setAttribute("submit", "memberdetaillist");
%>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title><%=company%> | Partner Merchant Profile Updation</title>

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
        function submitForm(confirmEmail)
        {
            $("#sendEmailNotification").val(confirmEmail)
            document.getElementById("form2").submit();

        }
    </script>
    <script src="/merchant/javascript/hidde.js"></script>
    <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
    <script src="/partner/NewCss/js/jquery-ui.min.js"></script>

    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>


</head>
<body class="pace-done widescreen fixed-left-void" onload="bodyonload()">
<%
    ctoken                      = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    String role                 = (String)session.getAttribute("role");
    String username             = (String)session.getAttribute("username");
    String actionExecutorId     = (String)session.getAttribute("merchantid");
    String actionExecutorName   = role+"-"+username;
    if (partner.isLoggedInPartner(session))
    {
        PartnerFunctions partnerFunctions   = new PartnerFunctions();
        Map<String, String> ynMap           = new HashMap<String, String>();
        ynMap.put("N", "No");
        ynMap.put("Y", "Yes");
        String fromdate = "";
        /*String todate="";*/
        ResourceBundle rb1                  = null;
        String language_property1           = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String memberdetaillist_Report_Table    = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Report_Table")) ? rb1.getString("memberdetaillist_Report_Table") : "Report Table";
        String memberdetaillist_Partner_Id      = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Partner_Id")) ? rb1.getString("memberdetaillist_Partner_Id") : "Partner Id*";
        String memberdetaillist_Merchant_Id     = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Merchant_Id")) ? rb1.getString("memberdetaillist_Merchant_Id") : "Merchant Id*";
        String memberdetaillist_User_Name       = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_User_Name")) ? rb1.getString("memberdetaillist_User_Name") : "User Name*";
        String memberdetaillist_Organisation    = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Organisation")) ? rb1.getString("memberdetaillist_Organisation") : "Organisation Name*";
        String memberdetaillist_Brand_Name      = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Brand_Name")) ? rb1.getString("memberdetaillist_Brand_Name") : "Brand Name";
        String memberdetaillist_SiteURL         = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_SiteURL")) ? rb1.getString("memberdetaillist_SiteURL") : "Site URL*";
        String memberdetaillist_Domain          = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Domain")) ? rb1.getString("memberdetaillist_Domain") : "Domain";
        String memberdetaillist_Address         = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Address")) ? rb1.getString("memberdetaillist_Address") : "Address";
        String memberdetaillist_City            = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_City")) ? rb1.getString("memberdetaillist_City") : "City";
        String memberdetaillist_State           = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_State")) ? rb1.getString("memberdetaillist_State") : "State";
        String memberdetaillist_Postal_Code     = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Postal_Code")) ? rb1.getString("memberdetaillist_Postal_Code") : "Postal Code";
        String memberdetaillist_Country         = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Country")) ? rb1.getString("memberdetaillist_Country") : "Country*";
        String memberdetaillist_Select_Country  = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Select_Country")) ? rb1.getString("memberdetaillist_Select_Country") : "Select a Country*";
        String memberdetaillist_Support_Number  = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Support_Number")) ? rb1.getString("memberdetaillist_Support_Number") : "Support Number*";
        String memberdetaillist_Merchant_Key    = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Merchant_Key")) ? rb1.getString("memberdetaillist_Merchant_Key") : "Merchant Key";
        String memberdetaillist_action          = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_action")) ? rb1.getString("memberdetaillist_action") : "Action Executor Id";
        String memberdetaillist_action_name     = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_action_name")) ? rb1.getString("memberdetaillist_action_name") : "Action Executor Name";
        String memberdetaillist_Contact_Details = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Contact_Details")) ? rb1.getString("memberdetaillist_Contact_Details") : "Contact Details";
        String memberdetaillist_Main            = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Main")) ? rb1.getString("memberdetaillist_Main") : "Main";
        String memberdetaillist_Contact         = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Contact")) ? rb1.getString("memberdetaillist_Contact") : "Contact*:";
        String memberdetaillist_Customer        = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Customer")) ? rb1.getString("memberdetaillist_Customer") : "Customer";
        String memberdetaillist_Support         = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Support")) ? rb1.getString("memberdetaillist_Support") : "Support*:";
        String memberdetaillist_Chargeback      = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Chargeback")) ? rb1.getString("memberdetaillist_Chargeback") : "Chargeback";
        String memberdetaillist_Contact1        = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Contact1")) ? rb1.getString("memberdetaillist_Contact1") : "Contact:";
        String memberdetaillist_Refund          = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Refund")) ? rb1.getString("memberdetaillist_Refund") : "Refund";
        String memberdetaillist_Sales           = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Sales")) ? rb1.getString("memberdetaillist_Sales") : "Sales";
        String memberdetaillist_Billing         = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Billing")) ? rb1.getString("memberdetaillist_Billing") : "Billing";
        String memberdetaillist_Fraud           = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Fraud")) ? rb1.getString("memberdetaillist_Fraud") : "Fraud";
        String memberdetaillist_Technical       = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Technical")) ? rb1.getString("memberdetaillist_Technical") : "Technical";
        String memberdetaillist_Save            = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Save")) ? rb1.getString("memberdetaillist_Save") : "Save";



%>
<div class="content-page">
    <div class="content">
        <div class="modal" id="myModal" role="dialog">
            <div id="target" class="modal-dialog" >
                <div class="modal-content">
                    <div class="header">
                        <div class="logo">
                            <h4 class="modal-title">Update Notification</h4>
                        </div>
                    </div>
                    <div class="modal-body">
                        <p>Do You want to send Email Notification.</p>
                    </div>
                    <div class="modal-footer" >
                        <button type="button" onclick="submitForm('Y')" class="btn btn-default" data-dismiss="modal">Yes</button>
                        <button type="button" onclick="submitForm('N')" class="btn btn-default" data-dismiss="modal">No</button>
                    </div>
                </div>
            </div>
        </div>
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/net/MemberDetailList?ctoken=<%=ctoken%>" method="post" name="form1">
                        <%
                            Enumeration<String> stringEnumeration = request.getParameterNames();

                            while (stringEnumeration.hasMoreElements())
                            {
                                String name = stringEnumeration.nextElement();
                                if ("fromdate".equals(name))
                                {
                                    fromdate = request.getParameter(name);
                                }
                                /*String name1=stringEnumeration.nextElement();
                                if ("todate".equals(name1))
                                {
                                    todate = request.getParameter(name1);
                                }*/
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
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=memberdetaillist_Report_Table%></strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <form action="/partner/net/UpdateMemberDetails?ctoken=<%=ctoken%>" method="post" name="form2" id="form2"
                              class="form-horizontal">
                            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                            <input type="hidden" name="fromdate" value="<%=fromdate%>">
                            <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                            <input type="hidden" id="sendEmailNotification" name="sendEmailNotification" value="">
                            <%-- <input type="hidden" name="partner_name1" value="<%=company%>">--%>
                                <%
                                String action       = (String) request.getAttribute("action");
                                String errormsg     = (String)request.getAttribute("error");
                                HashMap hash        = (HashMap) request.getAttribute("memberDetail");
                                String isreadonly   = (String) request.getAttribute("isreadonly");
                                String conf         = " ";
                                String conf1        = " ";
                                String update       = " ";
                                String Hide         = " ";

                                HashMap innerhash   = new HashMap();
                                String Roles        = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
                                /*if(Roles.equals("superpartner")){

                                      }else{
                                         conf1 = "disabled";
                                }*/

                                int records=0;
                                if(isreadonly.equalsIgnoreCase("view"))
                                {
                                    conf = "disabled";
                                }
                                else
                                {
                                    update  = "update";
                                    action  = "modify";
                                }
                               /*String style1 = "class=a";*/


                                if (hash != null && hash.size() > 0)
                                {
                                    String style    = "class=tr0";
                                    innerhash       = (HashMap) hash.get(1 + "");

                                    //String username = "";
                                    String passwd       = "";
                                    String conpasswd    = "";


                                    String contact_persons      = "";
                                    String contact_emails       = "";
                                    String mainContact_cCmailid = "";
                                    String mainContact_phone    = "";

                                    String support_persons      = "";
                                    String support_emails       = "";
                                    String support_cCmailid     = "";
                                    String support_phone        = "";

                                    String refundContact_name   = "";
                                    String refundContact_mailId = "";
                                    String refundContact_cCmail = "";
                                    String refundContact_phone  = "";

                                    String cbContact_name       = "";
                                    String cbContact_mailId     = "";
                                    String cbContact_cCmailId   = "";
                                    String cbContact_phone      = "";

                                    String salesContact_name    = "";
                                    String salesContact_mailid  = "";
                                    String salesContact_cCmailId="";
                                    String salesContact_phone   = "";

                                    String billingContact_name      = "";
                                    String billingContact_mailid    = "";
                                    String billingContact_cCmailId  = "";
                                    String billingContact_phone     = "";

                                    String fraudContact_name        = "";
                                    String fraudContact_mailid      = "";
                                    String fraudContact_cCmailId    = "";
                                    String fraudContact_phone       = "";

                                    String technicalContact_name        = "";
                                    String technicalContact_mailId      = "";
                                    String technicalContact_cCmailId    = "";
                                    String technicalContact_phone       = "";

                                    String company_name ="";
                                    String sitename     ="";
                                    String domain       ="";
                                    String[] splitvalue ={};
                                    String phonecc      ="";
                                    String supportNo    = "";
                                    String telno        ="";
                                    String country      = "";
                                    String memberId     = "";
                                    String pid          = "";
                                    String brandName    = "";
                                    String street       = "";
                                    String city         = "";
                                    String state        = "";
                                    String zip          = "";
                                    String merchant_key = "";
                                    String mainContactBccMailId         ="";
                                    String support_bccMailid            ="";
                                    String cbContactBccMailId           ="";
                                    String refundContactBccMailId       ="";
                                    String salesContactBccMailId        ="";
                                    String fraudContactBccMailId        ="";
                                    String technicalContactBccMailId    ="";
                                    String billingContactBccMailId      ="";



                                    Functions functions = new Functions();

                                    if (functions.isValueNull((String)innerhash.get("brandname"))) brandName = (String) innerhash.get("brandname");
                                    if (functions.isValueNull((String)innerhash.get("memberid"))) memberId = (String) innerhash.get("memberid");
                                    if (functions.isValueNull((String)innerhash.get("partnerId"))) pid = (String) innerhash.get("partnerId");
                                    if (functions.isValueNull((String)innerhash.get("login"))) username = (String) innerhash.get("login");
                                    if (functions.isValueNull((String)innerhash.get("company_name"))) company_name = (String) innerhash.get("company_name");
                                    if (functions.isValueNull((String)innerhash.get("sitename"))) sitename = (String) innerhash.get("sitename");
                                    if (functions.isValueNull((String)innerhash.get("domain"))) domain = (String) innerhash.get("domain");
                                    if (functions.isValueNull((String)innerhash.get("country"))) country = (String) innerhash.get("country");
                                    if (functions.isValueNull((String)innerhash.get("city"))) city = (String) innerhash.get("city");
                                    if (functions.isValueNull((String)innerhash.get("state"))) state = (String) innerhash.get("state");
                                    if (functions.isValueNull((String)innerhash.get("address"))) street = (String) innerhash.get("address");
                                    if (functions.isValueNull((String)innerhash.get("zip"))) zip = (String) innerhash.get("zip");
                                    if (functions.isValueNull((String)innerhash.get("telno"))) supportNo = (String) innerhash.get("telno");
                                    if (functions.isValueNull((String)innerhash.get("clkey"))) merchant_key = (String) innerhash.get("clkey");

                                  /*  if (functions.isValueNull((String)innerhash.get("actionExecutorId"))) actionExecutorId = (String) innerhash.get("actionExecutorId");
                                    if (functions.isValueNull((String)innerhash.get("actionExecutorName"))) actionExecutorName = (String) innerhash.get("actionExecutorName");
*/


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

                                    if (functions.isValueNull((String)innerhash.get("contact_emails"))) contact_emails = (String) innerhash.get("contact_emails");
                                    if (functions.isValueNull((String)innerhash.get("contact_persons"))) contact_persons = (String) innerhash.get("contact_persons");
                                    if (functions.isValueNull((String)innerhash.get("maincontact_ccmailid"))) mainContact_cCmailid = (String) innerhash.get("maincontact_ccmailid");
                                    if (functions.isValueNull((String)innerhash.get("maincontact_bccmailid"))) mainContactBccMailId = (String) innerhash.get("maincontact_bccmailid");
                                    if (functions.isValueNull((String)innerhash.get("maincontact_phone"))) mainContact_phone = (String) innerhash.get("maincontact_phone");

                                    if (functions.isValueNull((String) innerhash.get("support_persons"))) support_persons = (String) innerhash.get("support_persons");
                                    if (functions.isValueNull((String) innerhash.get("support_emails"))) support_emails = (String) innerhash.get("support_emails");
                                    if (functions.isValueNull((String)innerhash.get("support_ccmailid"))) support_cCmailid = (String) innerhash.get("support_ccmailid");
                                    if (functions.isValueNull((String)innerhash.get("support_bccmailid"))) support_bccMailid = (String) innerhash.get("support_bccmailid");
                                    if (functions.isValueNull((String)innerhash.get("support_phone"))) support_phone = (String) innerhash.get("support_phone");


                                    if (functions.isValueNull((String)innerhash.get("salescontact_name"))) salesContact_name = (String) innerhash.get("salescontact_name");
                                    if (functions.isValueNull((String)innerhash.get("salescontact_mailid"))) salesContact_mailid = (String) innerhash.get("salescontact_mailid");
                                    if (functions.isValueNull((String)innerhash.get("salescontact_ccmailid"))) salesContact_cCmailId = (String) innerhash.get("salescontact_ccmailid");
                                    if (functions.isValueNull((String)innerhash.get("salescontact_bccmailid"))) salesContactBccMailId = (String) innerhash.get("salescontact_bccmailid");
                                    if (functions.isValueNull((String)innerhash.get("salescontact_phone"))) salesContact_phone = (String) innerhash.get("salescontact_phone");

                                    if (functions.isValueNull((String)innerhash.get("refundcontact_name"))) refundContact_name = (String) innerhash.get("refundcontact_name");
                                    if (functions.isValueNull((String)innerhash.get("refundcontact_mailid"))) refundContact_mailId = (String) innerhash.get("refundcontact_mailid");
                                    if (functions.isValueNull((String)innerhash.get("refundcontact_ccmailid"))) refundContact_cCmail = (String) innerhash.get("refundcontact_ccmailid");
                                    if (functions.isValueNull((String)innerhash.get("refundcontact_bccmailid"))) refundContactBccMailId = (String) innerhash.get("refundcontact_bccmailid");
                                    if (functions.isValueNull((String)innerhash.get("refundcontact_phone"))) refundContact_phone = (String) innerhash.get("refundcontact_phone");

                                    if (functions.isValueNull((String)innerhash.get("cbcontact_name"))) cbContact_name = (String) innerhash.get("cbcontact_name");
                                    if (functions.isValueNull((String)innerhash.get("cbcontact_mailid"))) cbContact_mailId = (String) innerhash.get("cbcontact_mailid");
                                    if (functions.isValueNull((String)innerhash.get("cbcontact_ccmailid"))) cbContact_cCmailId = (String) innerhash.get("cbcontact_ccmailid");
                                    if (functions.isValueNull((String)innerhash.get("cbcontact_bccmailid"))) cbContactBccMailId = (String) innerhash.get("cbcontact_bccmailid");
                                    if (functions.isValueNull((String)innerhash.get("cbcontact_phone"))) cbContact_phone = (String) innerhash.get("cbcontact_phone");

                                    if (functions.isValueNull((String)innerhash.get("billingcontact_name"))) billingContact_name = (String) innerhash.get("billingcontact_name");
                                    if (functions.isValueNull((String)innerhash.get("billingcontact_mailid"))) billingContact_mailid = (String) innerhash.get("billingcontact_mailid");
                                    if (functions.isValueNull((String)innerhash.get("billingcontact_ccmailid"))) billingContact_cCmailId = (String) innerhash.get("billingcontact_ccmailid");
                                    if (functions.isValueNull((String)innerhash.get("billingcontact_bccmailid"))) billingContactBccMailId = (String) innerhash.get("billingcontact_bccmailid");
                                    if (functions.isValueNull((String)innerhash.get("billingcontact_phone"))) billingContact_phone = (String) innerhash.get("billingcontact_phone");

                                    if (functions.isValueNull((String)innerhash.get("fraudcontact_name"))) fraudContact_name = (String) innerhash.get("fraudcontact_name");
                                    if (functions.isValueNull((String)innerhash.get("fraudcontact_mailid"))) fraudContact_mailid = (String) innerhash.get("fraudcontact_mailid");
                                    if (functions.isValueNull((String)innerhash.get("fraudcontact_ccmailid"))) fraudContact_cCmailId = (String) innerhash.get("fraudcontact_ccmailid");
                                    if (functions.isValueNull((String)innerhash.get("fraudcontact_bccmailid"))) fraudContactBccMailId = (String) innerhash.get("fraudcontact_bccmailid");
                                    if (functions.isValueNull((String)innerhash.get("fraudcontact_phone"))) fraudContact_phone = (String) innerhash.get("fraudcontact_phone");

                                    if (functions.isValueNull((String)innerhash.get("technicalcontact_name"))) technicalContact_name = (String) innerhash.get("technicalcontact_name");
                                    if (functions.isValueNull((String)innerhash.get("technicalcontact_mailid"))) technicalContact_mailId = (String) innerhash.get("technicalcontact_mailid");
                                    if (functions.isValueNull((String)innerhash.get("technicalcontact_ccmailid"))) technicalContact_cCmailId = (String) innerhash.get("technicalcontact_ccmailid");
                                    if (functions.isValueNull((String)innerhash.get("technicalcontact_bccmailid"))) technicalContactBccMailId = (String) innerhash.get("technicalcontact_bccmailid");
                                    if (functions.isValueNull((String)innerhash.get("technicalcontact_phone"))) technicalContact_phone = (String) innerhash.get("technicalcontact_phone");
                            %>
                            <input type="hidden" size="30" name="update" value="<%=update%>">
                            <input type="hidden" size="30" name="action" value="<%=action%>">

                            <div class="widget-content padding">
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=memberdetaillist_Partner_Id%></label>

                                    <div class="col-md-4">
                                        <%--<label class="control-label"><strong><%=ESAPI.encoder().encodeForHTMLAttribute(memberId)%></strong></label>--%>
                                        <input class="form-control" name="pid"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(pid)%>" id="pid"
                                               autocomplete="on" disabled>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=memberdetaillist_Merchant_Id%></label>

                                    <div class="col-md-4">
                                        <%--<label class="control-label"><strong><%=ESAPI.encoder().encodeForHTMLAttribute(memberId)%></strong></label>--%>
                                        <input type="text" class="form-control" size="35" name="memberId"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberId)%>" disabled>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=memberdetaillist_User_Name%></label>

                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="35" name="username"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(username)%>" disabled>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=memberdetaillist_Organisation%></label>

                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="35" name="company_name"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(company_name)%>" <%=conf%>>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=memberdetaillist_Brand_Name%></label>

                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="30" name="brandname"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(brandName)%>" <%=conf%>>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=memberdetaillist_SiteURL%></label>

                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="30" name="sitename"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(sitename)%>" <%=conf%>>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=memberdetaillist_Domain%></label>

                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="30" name="domain"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(domain)%>" <%=conf%>>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=memberdetaillist_Address%></label>

                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="30" name="address"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(street)%>" <%=conf%>>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=memberdetaillist_City%></label>

                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="30" name="city"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(city)%>" <%=conf%>>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=memberdetaillist_State%></label>

                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="30" name="state"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(state)%>" <%=conf%>>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=memberdetaillist_Postal_Code%></label>

                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="30" name="zip"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(zip)%>" <%=conf%>>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=memberdetaillist_Country%></label>

                                    <div class="col-md-4">
                                        <select name="country" placeholder="Country*" class="form-control"
                                                value="<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>"
                                                onchange="myjunk();" <%=conf%> >
                                            <option value="-|phonecc"><%=memberdetaillist_Select_Country%></option>
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
                                        </select>
                                        <input value='<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>'
                                               id="getcountval" type="hidden">
                                        <jsp:include page="countrycodevalues.jsp"></jsp:include>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=memberdetaillist_Support_Number%></label>

                                    <div class="col-md-1">
                                        <input class="form-control" type="text" maxlength="05" name="phonecc" size="35"
                                               size="20" placeholder="Country-code"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(phonecc)%>" <%=conf%>>
                                    </div>
                                    <div class="col-md-3">
                                        <input class="form-control" type="text" maxlength="15" name="telno" size="35"
                                               size="20"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(telno)%>" <%=conf%>>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                    <%
                                    if(functions.isValueNull((String)innerhash.get("actionExecutorId"))){
                    actionExecutorId=(String)innerhash.get("actionExecutorId");
                }
                else {
                    actionExecutorId="-";
                }
                if(functions.isValueNull((String)innerhash.get("actionExecutorName"))){
                    actionExecutorName=(String)innerhash.get("actionExecutorName");
                }
                else {
                    actionExecutorName="-";
                }

                                %>

                            <%
                                    String hide_merchant = (String) request.getAttribute("merchant_key1");
                                    if (hide_merchant.equals("Y"))
                                    {
                                %>
                                <div class="form-group" id="merchantkey">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=memberdetaillist_Merchant_Key%></label>

                                    <div class="col-md-4">
                                        <%--<input type="text" class="form-control" size="30" name="merchant_key" value="<%=ESAPI.encoder().encodeForHTMLAttribute(merchant_key)%>" disabled >--%>
                                        <input class="form-control" type="hidden" id="merchant_key_hidden"
                                               maxlength="100" name="merchant_key" placeholder="Merchant Key"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(merchant_key)%>"
                                               disabled>
                                        <input class="form-control" type="text" id="merchant_key" maxlength="100"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(merchant_key)%>"
                                               placeholder="Merchant Key" onchange="setvalue('merchant_key')"
                                               disabled><span toggle="#password-field"
                                                              class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                              onclick="hideMerchantKey('merchant_key')"></span>

                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=memberdetaillist_action%></label>

                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="35" name="actionExecutorId"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(actionExecutorId)%>" disabled>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label"><%=memberdetaillist_action_name%></label>

                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="35" name="actionExecutorName"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(actionExecutorName)%>" disabled>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                <%
                               }

                                %>

                            </div>
                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=memberdetaillist_Contact_Details%></strong></h2>

                                <div class="additional-btn">
                                </div>
                            </div>
                            <div class="widget-content padding">
                                <div id="horizontal-form">
                                    <div class="form-group col-md-12 has-feedback">
                                        <div class="table-responsive">
                                            <table id="myTable" class="display table table-striped table-bordered"
                                                   width="100%"
                                                   style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                                <tr id="mytabletr1">
                                                    <td align="left"
                                                        style="background-color: #7eccad !important;color: white;"><%=memberdetaillist_Main%>&nbsp;<%=memberdetaillist_Contact%>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_persons)%>"
                                                               name="contact_persons" placeholder="Name*" <%=conf%>>
                                                    </td>
                                                    <td align="center">
                                                        <%-- <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_emails)%>" name="contact_emails" placeholder="Email*" <%=conf%> >--%>
                                                        <input class="form-control" type="hidden"
                                                               id="contact_emails_hidden" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_emails)%>"
                                                               name="contact_emails" placeholder="Email*">
                                                        <input class="form-control" type="text" id="contact_emails"
                                                               maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(contact_emails)%>"
                                                               placeholder="Email*"
                                                               onchange="setvalue('contact_emails')" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('contact_emails')"></span>

                                                    </td>
                                                    <td align="center">
                                                        <%-- <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_cCmailid)%>" name="maincontact_ccmailid" placeholder="Email Cc"  <%=conf%> >--%>
                                                        <input class="form-control" type="hidden"
                                                               id="maincontact_ccmailid_hidden"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_cCmailid)%>"
                                                               name="maincontact_ccmailid" placeholder="Email Cc">
                                                        <input class="form-control" type="text"
                                                               id="maincontact_ccmailid"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_cCmailid)%>"
                                                               onchange="setvalue('maincontact_ccmailid')"
                                                               placeholder="Email Cc" <%=conf%> ><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('maincontact_ccmailid')"></span>

                                                    </td>
                                                    <td align="center">
                                                        <%-- <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_cCmailid)%>" name="maincontact_ccmailid" placeholder="Email Cc"  <%=conf%> >--%>
                                                        <input class="form-control" type="hidden"
                                                               id="maincontact_bccmailid_hidden"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContactBccMailId)%>"
                                                               name="maincontact_bccmailid" placeholder="Email Bcc">
                                                        <input class="form-control" type="text"
                                                               id="maincontact_bccmailid"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContactBccMailId)%>"
                                                               onchange="setvalue('maincontact_bccmailid')"
                                                               placeholder="Email Bcc" <%=conf%> ><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('maincontact_bccmailid')"></span>

                                                    </td>

                                                    <td align="center">
                                                        <%--<input class="form-control" type="Text" maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_phone)%>" name="maincontact_phone" size="35" placeholder="Phone" <%=conf%>>--%>
                                                        <input class="form-control" id="maincontact_phone_hidden"
                                                               type="hidden" maxlength="11"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_phone)%>"
                                                               name="maincontact_phone" size="35" placeholder="Phone">
                                                        <input class="form-control" id="maincontact_phone" type="text"
                                                               maxlength="11"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(mainContact_phone)%>"
                                                               onchange="setvalue('maincontact_phone')" size="35"
                                                               placeholder="Phone" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hidePhone('maincontact_phone')"></span>

                                                    </td>
                                                </tr>

                                                <tr id="mytabletr2">
                                                    <td align="left"
                                                        style="background-color: #7eccad !important;color: white;">
                                                        <%=memberdetaillist_Customer%>&nbsp;<%=memberdetaillist_Support%>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_persons)%>"
                                                               name="support_persons" placeholder="Name*" <%=conf%>>
                                                    </td>
                                                    <td align="center">
                                                        <%-- <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_emails)%>" name="support_emails" placeholder="Email*" <%=conf%>>--%>
                                                        <input class="form-control" type="hidden"
                                                               id="support_emails_hidden" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_emails)%>"
                                                               name="support_emails" placeholder="Email*">
                                                        <input class="form-control" type="text" id="support_emails"
                                                               maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_emails)%>"
                                                               onchange="setvalue('support_emails')"
                                                               placeholder="Email*" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('support_emails')"></span>

                                                    </td>
                                                    <td align="center">
                                                        <%--<input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_cCmailid)%>" name="support_ccmailid" placeholder="Email Cc" <%=conf%>>--%>
                                                        <input class="form-control" type="hidden"
                                                               id="support_ccmailid_hidden"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_cCmailid)%>"
                                                               name="support_ccmailid" placeholder="Email Cc">
                                                        <input class="form-control" type="text" id="support_ccmailid"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_cCmailid)%>"
                                                               onchange="setvalue('support_ccmailid')"
                                                               placeholder="Email Cc" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('support_ccmailid')"></span>


                                                    </td>

                                                    <td align="center">
                                                        <%--<input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_cCmailid)%>" name="support_ccmailid" placeholder="Email Cc" <%=conf%>>--%>
                                                        <input class="form-control" type="hidden"
                                                               id="support_bccmailid_hidden"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_bccMailid)%>"
                                                               name="support_bccmailid" placeholder="Email Bcc">
                                                        <input class="form-control" type="text" id="support_bccmailid"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_bccMailid)%>"
                                                               onchange="setvalue('support_bccmailid')"
                                                               placeholder="Email Bcc" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('support_bccmailid')"></span>


                                                    </td>

                                                    <td align="center">
                                                        <%--<input class="form-control" type="Text" maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_phone)%>" name="support_phone" placeholder="Phone" <%=conf%>>--%>
                                                        <input class="form-control" type="hidden"
                                                               id="support_phone_hidden" maxlength="15"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_phone)%>"
                                                               name="support_phone" size="35" placeholder="Phone">
                                                        <input class="form-control" type="Text" id="support_phone"
                                                               maxlength="15"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(support_phone)%>"
                                                               onchange="setvalue('support_phone')" size="35"
                                                               placeholder="Phone" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hidePhone('support_phone')"></span>

                                                    </td>
                                                </tr>
                                                <tr id="mytabletr2">
                                                    <td align="left"
                                                        style="background-color: #7eccad !important;color: white;">
                                                        <%=memberdetaillist_Chargeback%>&nbsp;<%=memberdetaillist_Contact1%>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_name)%>"
                                                               name="cbcontact_name" placeholder="Name" <%=conf%> >
                                                    </td>
                                                    <td align="center">
                                                        <%--<input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_mailId)%>"name="cbcontact_mailid" placeholder="Email"  <%=conf%>>--%>
                                                        <input class="form-control" type="hidden"
                                                               id="cbcontact_mailid_hidden" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_mailId)%>"
                                                               name="cbcontact_mailid" placeholder="Email">
                                                        <input class="form-control" type="text" id="cbcontact_mailid"
                                                               maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_mailId)%>"
                                                               onchange="setvalue('cbcontact_mailid')"
                                                               placeholder="Email" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('cbcontact_mailid')"></span>

                                                    </td>
                                                    <td align="center">
                                                        <%--<input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_cCmailId)%>" name="cbcontact_ccmailid" placeholder="Email Cc"  <%=conf%> >--%>
                                                        <input class="form-control" type="hidden"
                                                               id="cbcontact_ccmailid_hidden"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_cCmailId)%>"
                                                               name="cbcontact_ccmailid" placeholder="Email Cc">
                                                        <input class="form-control" type="text" id="cbcontact_ccmailid"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_cCmailId)%>"
                                                               onchange="setvalue('cbcontact_ccmailid')"
                                                               placeholder="Email Cc" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('cbcontact_ccmailid')"></span>

                                                    </td>
                                                    <td align="center">
                                                        <%--<input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_cCmailId)%>" name="cbcontact_ccmailid" placeholder="Email Cc"  <%=conf%> >--%>
                                                        <input class="form-control" type="hidden"
                                                               id="cbcontact_bccmailid_hidden"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContactBccMailId)%>"
                                                               name="cbcontact_bccmailid" placeholder="Email Bcc">
                                                        <input class="form-control" type="text"
                                                               id="cbcontact_bccmailid"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContactBccMailId)%>"
                                                               onchange="setvalue('cbcontact_bccmailid')"
                                                               placeholder="Email Bcc" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('cbcontact_bccmailid')"></span>

                                                    </td>

                                                    <td align="center">
                                                        <%--<input class="form-control" type="Text" maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_phone)%>" name="cbcontact_phone" size="35" placeholder="Phone" <%=conf%>>--%>
                                                        <input class="form-control" type="hidden"
                                                               id="cbcontact_phone_hidden" maxlength="50"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_phone)%>"
                                                               name="cbcontact_phone" size="35" placeholder="Phone">
                                                        <input class="form-control" type="Text" id="cbcontact_phone"
                                                               maxlength="50"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(cbContact_phone)%>"
                                                               onchange="setvalue('cbcontact_phone')" size="35"
                                                               placeholder="Phone" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hidePhone('cbcontact_phone')"></span>

                                                    </td>
                                                </tr>
                                                <tr id="mytabletr3">
                                                    <td align="left"
                                                        style="background-color: #7eccad !important;color: white;">
                                                        <%=memberdetaillist_Refund%>&nbsp;<%=memberdetaillist_Contact1%>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_name)%>"
                                                               name="refundcontact_name" placeholder="Name"  <%=conf%>>
                                                    </td>
                                                    <td align="center" class="text">
                                                        <%--<input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_mailId)%>"name="refundcontact_mailid" placeholder="Email" <%=conf%> >--%>
                                                        <input class="form-control" type="hidden"
                                                               id="refundcontact_mailid_hidden" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_mailId)%>"
                                                               name="refundcontact_mailid" placeholder="Email">
                                                        <input class="form-control" type="text"
                                                               id="refundcontact_mailid" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_mailId)%>"
                                                               onchange="setvalue('refundcontact_mailid')"
                                                               placeholder="Email" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('refundcontact_mailid')"></span>
                                                    </td>
                                                    <td align="center">
                                                        <%--<input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_cCmail)%>" name="refundcontact_ccmailid" placeholder="Email Cc"  <%=conf%> >--%>
                                                        <input class="form-control" type="hidden"
                                                               id="refundcontact_ccmailid_hidden"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_cCmail)%>"
                                                               name="refundcontact_ccmailid" placeholder="Email Cc">
                                                        <input class="form-control" type="text"
                                                               id="refundcontact_ccmailid"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_cCmail)%>"
                                                               onchange="setvalue('refundcontact_ccmailid')"
                                                               placeholder="Email Cc" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('refundcontact_ccmailid')"></span>
                                                    </td>
                                                    <td align="center">
                                                        <%--<input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_cCmail)%>" name="refundcontact_ccmailid" placeholder="Email Cc"  <%=conf%> >--%>
                                                        <input class="form-control" type="hidden"
                                                               id="refundcontact_bccmailid_hidden"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContactBccMailId)%>"
                                                               name="refundcontact_bccmailid" placeholder="Email Bcc">
                                                        <input class="form-control" type="text"
                                                               id="refundcontact_bccmailid"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContactBccMailId)%>"
                                                               onchange="setvalue('refundcontact_bccmailid')"
                                                               placeholder="Email Bcc" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('refundcontact_bccmailid')"></span>
                                                    </td>
                                                    <td align="center" class="text">
                                                        <%-- <input class="form-control" type="Text" maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_phone)%>" name="refundcontact_phone" size="35" placeholder="Phone" <%=conf%>>--%>
                                                        <input class="form-control" type="hidden"
                                                               id="refundcontact_phone_hidden" maxlength="50"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_phone)%>"
                                                               name="refundcontact_phone" size="35" placeholder="Phone">
                                                        <input class="form-control" type="Text" id="refundcontact_phone"
                                                               maxlength="50"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(refundContact_phone)%>"
                                                               onchange="setvalue('refundcontact_phone')" size="35"
                                                               placeholder="Phone" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hidePhone('refundcontact_phone')"></span>

                                                    </td>
                                                </tr>
                                                <tr id="mytabletr4">
                                                    <td align="left"
                                                        style="background-color: #7eccad !important;color: white;"><%=memberdetaillist_Sales%>&nbsp;<%=memberdetaillist_Contact1%>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_name)%>"
                                                               name="salescontact_name" placeholder="Name" <%=conf%> >
                                                    </td>
                                                    <td align="center" class="text">
                                                        <%-- <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_mailid)%>"name="salescontact_mailid" placeholder="Email" <%=conf%>>--%>
                                                        <input class="form-control" type="hidden"
                                                               id="salescontact_mailid_hidden" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_mailid)%>"
                                                               name="salescontact_mailid" placeholder="Email">
                                                        <input class="form-control" type="text" id="salescontact_mailid"
                                                               maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_mailid)%>"
                                                               onchange="setvalue('salescontact_mailid')"
                                                               placeholder="Email" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('salescontact_mailid')"></span>

                                                    </td>
                                                    <td align="center">
                                                        <%-- <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_cCmailId)%>" name="salescontact_ccmailid" placeholder="Email Cc" <%=conf%> >--%>
                                                        <input class="form-control" type="hidden"
                                                               id="salescontact_ccmailid_hidden"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_cCmailId)%>"
                                                               name="salescontact_ccmailid" placeholder="Email Cc">
                                                        <input class="form-control" type="text"
                                                               id="salescontact_ccmailid"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_cCmailId)%>"
                                                               onchange="setvalue('salescontact_ccmailid')"
                                                               placeholder="Email Cc" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('salescontact_ccmailid')"></span>

                                                    </td>
                                                    <td align="center">
                                                        <%-- <input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_cCmailId)%>" name="salescontact_ccmailid" placeholder="Email Cc" <%=conf%> >--%>
                                                        <input class="form-control" type="hidden"
                                                               id="salescontact_bccmailid_hidden"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContactBccMailId)%>"
                                                               name="salescontact_bccmailid" placeholder="Email Bcc">
                                                        <input class="form-control" type="text"
                                                               id="salescontact_bccmailid"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContactBccMailId)%>"
                                                               onchange="setvalue('salescontact_bccmailid')"
                                                               placeholder="Email Bcc" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('salescontact_bccmailid')"></span>

                                                    </td>
                                                    <td align="center" class="text">
                                                        <%--<input class="form-control" type="Text" maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_phone)%>" name="salescontact_phone" size="35" placeholder="Phone" <%=conf%>>--%>
                                                        <input class="form-control" type="hidden"
                                                               id="salescontact_phone_hidden" maxlength="50"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_phone)%>"
                                                               name="salescontact_phone" size="35" placeholder="Phone">
                                                        <input class="form-control" type="Text" id="salescontact_phone"
                                                               maxlength="50"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(salesContact_phone)%>"
                                                               onchange="setvalue('salescontact_phone')" size="35"
                                                               placeholder="Phone" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hidePhone('salescontact_phone')"></span>

                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="left"
                                                        style="background-color: #7eccad !important;color: white;">
                                                       <%=memberdetaillist_Billing%>&nbsp;<%=memberdetaillist_Contact1%>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_name)%>"
                                                               name="billingcontact_name" placeholder="Name" <%=conf%> >
                                                    </td>
                                                    <td align="center" class="text">
                                                        <%--<input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_mailid)%>"name="billingcontact_mailid" placeholder="Email" <%=conf%> >--%>
                                                        <input class="form-control" type="hidden"
                                                               id="billingcontact_mailid_hidden" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_mailid)%>"
                                                               name="billingcontact_mailid" placeholder="Email">
                                                        <input class="form-control" type="text"
                                                               id="billingcontact_mailid" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_mailid)%>"
                                                               onchange="setvalue('billingcontact_mailid')"
                                                               placeholder="Email" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('billingcontact_mailid')"></span>

                                                    </td>
                                                    <td align="center">
                                                        <%--<input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_cCmailId)%>" name="billingcontact_ccmailid" placeholder="Email Cc" <%=conf%>  >--%>
                                                        <input class="form-control" type="hidden"
                                                               id="billingcontact_ccmailid_hidden"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_cCmailId)%>"
                                                               name="billingcontact_ccmailid" placeholder="Email Cc">
                                                        <input class="form-control" type="text"
                                                               id="billingcontact_ccmailid"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_cCmailId)%>"
                                                               onchange="setvalue('billingcontact_ccmailid')"
                                                               placeholder="Email Cc" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('billingcontact_ccmailid')"></span>
                                                    </td>
                                                    <td align="center">
                                                        <%--<input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_cCmailId)%>" name="billingcontact_ccmailid" placeholder="Email Cc" <%=conf%>  >--%>
                                                        <input class="form-control" type="hidden"
                                                               id="billingcontact_bccmailid_hidden"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContactBccMailId)%>"
                                                               name="billingcontact_bccmailid" placeholder="Email Bcc">
                                                        <input class="form-control" type="text"
                                                               id="billingcontact_bccmailid"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContactBccMailId)%>"
                                                               onchange="setvalue('billingcontact_bccmailid')"
                                                               placeholder="Email Bcc" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('billingcontact_bccmailid')"></span>
                                                    </td>
                                                    <td align="center">
                                                        <%--<input class="form-control" type="Text" maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_phone)%>" name="billingcontact_phone" size="35" placeholder="Phone" <%=conf%>>--%>
                                                        <input class="form-control" type="hidden"
                                                               id="billingcontact_phone_hidden" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_phone)%>"
                                                               name="billingcontact_phone" placeholder="Phone">
                                                        <input class="form-control" type="Text"
                                                               id="billingcontact_phone" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(billingContact_phone)%>"
                                                               onchange="setvalue('billingcontact_phone')"
                                                               placeholder="Phone" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hidePhone('billingcontact_phone')"></span>

                                                    </td>
                                                </tr>
                                                <tr id="mytabletr5">
                                                    <td align="left"
                                                        style="background-color: #7eccad !important;color: white;"><%=memberdetaillist_Fraud%>&nbsp;<%=memberdetaillist_Contact1%>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_name)%>"
                                                               name="fraudcontact_name" placeholder="Name"  <%=conf%>>
                                                    </td>
                                                    <td align="center" class="text">
                                                        <%--<input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_mailid)%>" name="fraudcontact_mailid" placeholder="Email" <%=conf%> >--%>
                                                        <input class="form-control" type="hidden"
                                                               id="fraudcontact_mailid_hidden" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_mailid)%>"
                                                               name="fraudcontact_mailid" placeholder="Email">
                                                        <input class="form-control" type="text" id="fraudcontact_mailid"
                                                               maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_mailid)%>"
                                                               onchange="setvalue('fraudcontact_mailid')"
                                                               placeholder="Email" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('fraudcontact_mailid')"></span>

                                                    </td>
                                                    <td align="center">
                                                        <%--<input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_cCmailId)%>" name="fraudcontact_ccmailid" placeholder="Email Cc"<%=conf%> >--%>
                                                        <input class="form-control" type="hidden"
                                                               id="fraudcontact_ccmailid_hidden"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_cCmailId)%>"
                                                               name="fraudcontact_ccmailid" placeholder="Email Cc">
                                                        <input class="form-control" type="text"
                                                               id="fraudcontact_ccmailid"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_cCmailId)%>"
                                                               onchange="setvalue('fraudcontact_ccmailid')"
                                                               placeholder="Email Cc" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('fraudcontact_ccmailid')"></span>

                                                    </td>
                                                    <td align="center">
                                                        <%--<input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_cCmailId)%>" name="fraudcontact_ccmailid" placeholder="Email Cc"<%=conf%> >--%>
                                                        <input class="form-control" type="hidden"
                                                               id="fraudcontact_bccmailid_hidden"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContactBccMailId)%>"
                                                               name="fraudcontact_bccmailid" placeholder="Email Bcc">
                                                        <input class="form-control" type="text"
                                                               id="fraudcontact_bccmailid"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContactBccMailId)%>"
                                                               onchange="setvalue('fraudcontact_bccmailid')"
                                                               placeholder="Email Bcc" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('fraudcontact_bccmailid')"></span>

                                                    </td>
                                                    <td align="center" class="text">
                                                        <%--<input class="form-control" type="Text" maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_phone)%>" name="fraudcontact_phone" size="35" placeholder="Phone" <%=conf%>>--%>
                                                        <input class="form-control" type="hidden"
                                                               id="fraudcontact_phone_hidden" maxlength="50"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_phone)%>"
                                                               name="fraudcontact_phone" size="35" placeholder="Phone">
                                                        <input class="form-control" type="Text" id="fraudcontact_phone"
                                                               maxlength="50"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudContact_phone)%>"
                                                               onchange="setvalue('fraudcontact_phone')" size="35"
                                                               placeholder="Phone" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hidePhone('fraudcontact_phone')"></span>

                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td align="left"
                                                        style="background-color: #7eccad !important;color: white;">
                                                        <%=memberdetaillist_Technical%>&nbsp;<%=memberdetaillist_Contact1%>
                                                    </td>
                                                    <td align="center">
                                                        <input class="form-control" type="text" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_name)%>"
                                                               name="technicalcontact_name"
                                                               placeholder="Name" <%=conf%> >
                                                    </td>
                                                    <td align="center" class="text">
                                                        <%--<input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_mailId)%>" name="technicalcontact_mailid" placeholder="Email"<%=conf%>>--%>
                                                        <input class="form-control" type="hidden"
                                                               id="technicalcontact_mailid_hidden" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_mailId)%>"
                                                               name="technicalcontact_mailid" placeholder="Email">
                                                        <input class="form-control" type="text"
                                                               id="technicalcontact_mailid" maxlength="100"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_mailId)%>"
                                                               onchange="setvalue('technicalcontact_mailid')"
                                                               placeholder="Email" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('technicalcontact_mailid')"></span>

                                                    </td>
                                                    <td align="center">
                                                        <%--<input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_cCmailId)%>" name="technicalcontact_ccmailid" placeholder="Email Cc"<%=conf%>>--%>
                                                        <input class="form-control" type="hidden"
                                                               id="technicalcontact_ccmailid_hidden"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_cCmailId)%>"
                                                               name="technicalcontact_ccmailid" placeholder="Email Cc">
                                                        <input class="form-control" type="text"
                                                               id="technicalcontact_ccmailid"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_cCmailId)%>"
                                                               onchange="setvalue('technicalcontact_ccmailid')"
                                                               placeholder="Email Cc" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('technicalcontact_ccmailid')"></span>

                                                    </td>
                                                    <td align="center">
                                                        <%--<input class="form-control" type="text" maxlength="100" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_cCmailId)%>" name="technicalcontact_ccmailid" placeholder="Email Cc"<%=conf%>>--%>
                                                        <input class="form-control" type="hidden"
                                                               id="technicalcontact_bccmailid_hidden"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContactBccMailId)%>"
                                                               name="technicalcontact_bccmailid" placeholder="Email Bcc">
                                                        <input class="form-control" type="text"
                                                               id="technicalcontact_bccmailid"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContactBccMailId)%>"
                                                               onchange="setvalue('technicalcontact_bccmailid')"
                                                               placeholder="Email Bcc" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hideemail('technicalcontact_bccmailid')"></span>

                                                    </td>
                                                    <td align="center" class="text">
                                                        <%--<input class="form-control" type="Text" maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_phone)%>" name="technicalcontact_phone" size="35" placeholder="Phone"<%=conf%>>--%>
                                                        <input class="form-control" type="hidden"
                                                               id="technicalcontact_phone_hidden" maxlength="11"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_phone)%>"
                                                               name="technicalcontact_phone" size="35"
                                                               placeholder="Phone">
                                                        <input class="form-control" type="Text"
                                                               id="technicalcontact_phone" maxlength="11"
                                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(technicalContact_phone)%>"
                                                               onchange="setvalue('technicalcontact_phone')" size="35"
                                                               placeholder="Phone" <%=conf%>><span
                                                            toggle="#password-field"
                                                            class="fa fa-fw fa-eye-slash field-icon toggle-password"
                                                            onclick="hidePhone('technicalcontact_phone')"></span>

                                                    </td>
                                                </tr>
                                            </table>
                                        </div>
                                    </div>
                                    <div class="form-group col-md-12 has-feedback">
                                        <center>
                                            <%
                                                /*out.println("<input type=\"hidden\" name=\"memberid\" value=\"" + innerhash.get("memberid") + "\"><input type=\"hidden\" name=\"action\" value=\"" + action + "\"><input type=\"submit\" class=\"gotoauto btn btn-default\" name=\"modify\" value="+memberdetaillist_Save+" " + conf + ">");*/
                                                out.println("<input type=\"hidden\" name=\"memberid\" value=\"" + innerhash.get("memberid") + "\"><input type=\"hidden\" name=\"action\" value=\"" + action + "\"><input type=\"button\"  data-toggle=\"modal\" data-target=\"#myModal\" class=\"gotoauto btn btn-default\" name=\"modify\" value="+memberdetaillist_Save+" " + conf + ">");
                                            %>
                                        </center>
                                    </div>
                                </div>
                            </div>
                    </div>
                    </form>
                </div>
            </div>
            <%
                    }
                    else if (partnerFunctions.isValueNull(errormsg))
                    {
                        out.println("<center><div class=\"bg-info\">" + errormsg + "</div></center>");
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