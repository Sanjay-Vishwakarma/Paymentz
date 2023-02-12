<%@ page import="com.directi.pg.Functions,org.owasp.esapi.ESAPI" %>
<%@ include file="top.jsp"%>
<%@ include file="functions.jsp"%>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="java.util.ResourceBundle" %>
<%
    String partnerid1 = String.valueOf(session.getAttribute("partnerId"));
    session.setAttribute("submit","agentInterface");
%>
<%--
  Created by IntelliJ IDEA.
  User: Kanchan
  Date: 28-01-2021
  Time: 18:31
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <title></title>

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

    <script src="/merchant/javascript/hidde.js"></script>

    <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
    <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

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
    </script>

</head>
<body>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    Functions functions= new Functions();
    if (partner.isLoggedInPartner(session))
    {
%>

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">

            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/agentInterface.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
                            <img style="height: 35px;" src="/partner/images/goBack.png">
                        </button>
                    </form>
                </div>
            </div>
            <br>
            <br>
            <br>

            <div class="row reporttable">
                <div class="col-md-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Agent Details</strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <br>

                        <%
                            ResourceBundle rb1 = null;
                            String language_property1 = (String) session.getAttribute("language_property");
                            rb1 = LoadProperties.getProperty(language_property1);
                            String passwd = "";
                            String conpasswd = "";
                            String company_name = "";
                            String sitename = "";
                            String supporturl = "";
                            String notifyemail = "";

                            String contact_emails = "";
                            String contact_persons = "";
                            String mainContact_cCmailid="";
                            String mainContact_phone="";

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
                            String emailTemplateLang="";
                            String telno= "";
                            String country= "";
                            String role="Partner";
                            String username= (String)session.getAttribute("username");
                            String actionExecutorId=(String)session.getAttribute("merchantid");
                            String actionExecutorName=role+"-"+username;
                            String agentName="";
                            String memberdetaillist_Save = StringUtils.isNotEmpty(rb1.getString("memberdetaillist_Save")) ? rb1.getString("memberdetaillist_Save") : "Save";

                        %>
                        <form action="/partner/net/EditAgentDetails?ctoken=<%=ctoken%>" method="POST" class="form-horizontal">
                            <input type="hidden" name="partnerid" value="<%=partnerid1%>" id="partnerid" >
                            <input type="hidden" name="ctoken" value="<%=ctoken%>" id="ctoken">
                        <%
                            String agentid= (String)request.getAttribute("agentid");
                            String action= (String) request.getAttribute("action");
                            String message=(String)request.getAttribute("message");

                            Hashtable hash= (Hashtable)request.getAttribute("agentdetails");
                            String isreadonly= (String)request.getAttribute("isreadonly");

                            String conf= " ";
                            Hashtable partners= (Hashtable)request.getAttribute("partners");
                            Hashtable innerhash= new Hashtable();

                            if (isreadonly.equalsIgnoreCase("View"))
                            {
                                conf= "disabled";
                            }

                            if (hash!= null && hash.size()>0 && partners!=null && partners.size()>0)
                            {
                                innerhash=(Hashtable)hash.get(1+ "");
                                if (innerhash.get("login") != null) username = (String) innerhash.get("login");
                                if (innerhash.get("agentName") != null)agentName= (String)innerhash.get("agentName");
                                if (innerhash.get("siteurl") != null) sitename = (String) innerhash.get("siteurl");
                                if (innerhash.get("supporturl") != null) supporturl = (String) innerhash.get("supporturl");
                                if (innerhash.get("notifyemail") != null) notifyemail = (String) innerhash.get("notifyemail");
                                if (innerhash.get("telno") != null) telno = (String) innerhash.get("telno");
                                if (innerhash.get("country") != null) country = (String) innerhash.get("country");

                                if (innerhash.get("contact_emails") != null) contact_emails = (String) innerhash.get("contact_emails");
                                if (innerhash.get("contact_persons") != null) contact_persons = (String) innerhash.get("contact_persons");
                                if (innerhash.get("maincontact_ccmailid") != null) mainContact_cCmailid = (String) innerhash.get("maincontact_ccmailid");
                                if (innerhash.get("maincontact_phone") != null) mainContact_phone = (String) innerhash.get("maincontact_phone");

                                if (innerhash.get("salescontact_name") != null) salesContact_name = (String) innerhash.get("salescontact_name");
                                if (innerhash.get("salescontact_mailid") != null) salesContact_mailid = (String) innerhash.get("salescontact_mailid");
                                if (innerhash.get("salescontact_ccmailid") != null) salesContact_cCmailId = (String) innerhash.get("salescontact_ccmailid");
                                if (innerhash.get("salescontact_phone") != null) salesContact_phone = (String) innerhash.get("salescontact_phone");

                                if (innerhash.get("refundcontact_name") != null) refundContact_name = (String) innerhash.get("refundcontact_name");
                                if (innerhash.get("refundcontact_mailid") != null) refundContact_mailId = (String) innerhash.get("refundcontact_mailid");
                                if (innerhash.get("refundcontact_ccmailid") != null) refundContact_cCmail = (String) innerhash.get("refundcontact_ccmailid");
                                if (innerhash.get("refundcontact_phone") != null) refundContact_phone = (String) innerhash.get("refundcontact_phone");

                                if (innerhash.get("cbcontact_name") != null) cbContact_name = (String) innerhash.get("cbcontact_name");
                                if (innerhash.get("cbcontact_mailid") != null) cbContact_mailId = (String) innerhash.get("cbcontact_mailid");
                                if (innerhash.get("cbcontact_ccmailid") != null) cbContact_cCmailId = (String) innerhash.get("cbcontact_ccmailid");
                                if (innerhash.get("cbcontact_phone") != null) cbContact_phone = (String) innerhash.get("cbcontact_phone");

                                if (innerhash.get("billingcontact_name") != null) billingContact_name = (String) innerhash.get("billingcontact_name");
                                if (innerhash.get("billingcontact_mailid") != null) billingContact_mailid = (String) innerhash.get("billingcontact_mailid");
                                if (innerhash.get("billingcontact_ccmailid") != null) billingContact_cCmailId = (String) innerhash.get("billingcontact_ccmailid");
                                if (innerhash.get("billingcontact_phone") != null) billingContact_phone = (String) innerhash.get("billingcontact_phone");

                                if (innerhash.get("fraudcontact_name") != null) fraudContact_name = (String) innerhash.get("fraudcontact_name");
                                if (innerhash.get("fraudcontact_mailid") != null) fraudContact_mailid = (String) innerhash.get("fraudcontact_mailid");
                                if (innerhash.get("fraudcontact_ccmailid") != null) fraudContact_cCmailId = (String) innerhash.get("fraudcontact_ccmailid");
                                if (innerhash.get("fraudcontact_phone") != null) fraudContact_phone = (String) innerhash.get("fraudcontact_phone");

                                if (innerhash.get("technicalcontact_name") != null) technicalContact_name = (String) innerhash.get("technicalcontact_name");
                                if (innerhash.get("technicalcontact_mailid") != null) technicalContact_mailId = (String) innerhash.get("technicalcontact_mailid");
                                if (innerhash.get("technicalcontact_ccmailid") != null) technicalContact_cCmailId = (String) innerhash.get("technicalcontact_ccmailid");
                                if (innerhash.get("technicalcontact_phone") != null) technicalContact_phone = (String) innerhash.get("technicalcontact_phone");
                                if (innerhash.get("emailTemplateLang") != null) emailTemplateLang = (String) innerhash.get("emailTemplateLang");
                        %>

                            <br>
                            <div class="widget-content padding">

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label">Username*</label>
                                    <div class="col-md-4">
                                        <input class="form-control" type="text"   value="<%=ESAPI.encoder().encodeForHTMLAttribute(username)%>" name="username" size="35" disabled>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label">Agent Name*</label>
                                    <div class="col-md-4">
                                        <input class="form-control" type="text"   value="<%=ESAPI.encoder().encodeForHTMLAttribute(agentName)%>" name="agentName" size="35" disabled>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label">Agent Sitename*</label>
                                    <div class="col-md-4">
                                        <input type="text" class="form-control" size="35" name="sitename" value="<%=ESAPI.encoder().encodeForHTMLAttribute(sitename)%>" <%=conf%>>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label" >Support URL* </label>

                                    <div class="col-md-4">
                                        <input type="text" class="form-control" name="supporturl"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(supporturl)%>"  <%=conf%>>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label" >Notify Email Id*</label>

                                    <div class="col-md-4">
                                        <input type="text" class="form-control" name="notifyemail"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(notifyemail)%>"  <%=conf%>>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label" >Country*</label>

                                    <div class="col-md-4">
                                        <input type="text" class="form-control" name="country"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(country)%>"  <%=conf%>>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label" >Contact No*</label>

                                    <div class="col-md-4">
                                        <input type="text" class="form-control" name="telno"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(telno)%>"  <%=conf%>>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label" >Is IP Whitelisted </label>

                                    <div class="col-md-4">
                                        <select class="form-control" name="isipwhitelisted" <%=conf%>>
                                            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)innerhash.get("isIpWhitelisted"))));  %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label" >Email Template Language </label>
                                    <div class="col-md-4">
                                        <select class="form-control" name="emailTemplateLang" <%=conf%>>
                                            <%out.println(Functions.combovalLanguage(ESAPI.encoder().encodeForHTMLAttribute((String) innerhash.get("emailTemplateLang"))));  %>
                                        </select>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label" >Partner Id </label>
                                    <div class="col-md-4">
                                        <input id="pid" name="pid" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String) innerhash.get("partnerId"))%>"  autocomplete="on" <%=conf%>>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <%
                                    if(functions.isValueNull((String)innerhash.get("actionExecutorId")))
                                    {
                                        actionExecutorId= (String)innerhash.get("actionExecutorId");
                                    }
                                    else
                                    {
                                        actionExecutorId="-";
                                    }
                                    if(functions.isValueNull((String) innerhash.get("actionExecutorName")))
                                    {
                                        actionExecutorName=(String)innerhash.get("actionExecutorName");
                                    }
                                    else
                                    {
                                        actionExecutorName="-";
                                    }
                                %>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label">Action Executor Id</label>

                                    <div class="col-md-4">
                                        <input type="text" class="form-control" name="actionExecutorId"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(actionExecutorId)%>" disabled>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>

                                <div class="form-group">
                                    <div class="col-md-2"></div>
                                    <label class="col-md-4 control-label" >Action Executor Name</label>

                                    <div class="col-md-4">
                                        <input type="text" class="form-control" name="actionExecutorName"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(actionExecutorName)%>" disabled>
                                    </div>
                                    <div class="col-md-6"></div>
                                </div>
                                    </div>
                                <br>

                                <div class="widget-header transparent">
                                    <h2 align="center"><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Contact Details</strong></h2>
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
                                                            style="background-color: #7eccad !important;color: white;">Main Contact*:
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
                                                            Chargeback Contact:
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

                                                    <tr id="mytabletr2">
                                                        <td align="left"
                                                            style="background-color: #7eccad !important;color: white;">
                                                            Refund Contact:
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

                                                    <tr id="mytabletr3">
                                                        <td align="left"
                                                            style="background-color: #7eccad !important;color: white;">Sales Contact:
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

                                                    <tr id="mytabletr4">
                                                        <td align="left"
                                                            style="background-color: #7eccad !important;color: white;">
                                                            Billing Contact:
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

                                                    <tr>
                                                        <td align="left"
                                                            style="background-color: #7eccad !important;color: white;">Fraud Contact:
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

                                                    <tr id="mytabletr5">
                                                        <td align="left"
                                                            style="background-color: #7eccad !important;color: white;">
                                                            Technical Contact:
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
                                                    <tr>
                                                        <td>
                                                            <input type="hidden" name="agentid" value="<%=agentid%>">
                                                            <input type="hidden" name="action" value="<%=action%>">
                                                        </td>
                                                    </tr>
                                                </table>
                                            </div>
                                        </div>

                                        <div class="form-group col-md-12 has-feedback">
                                            <center>
                                                <%
                                                    out.println("<input type=\"hidden\" name=\"agentid\" value=\"" + innerhash.get("agentid") + "\"><input type=\"hidden\" name=\"action\" value=\"" + action + "\"><input type=\"submit\" class=\"gotoauto btn btn-default\" name=\"modify\" value="+memberdetaillist_Save+" " + conf + ">");
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
                        else if (partner.isValueNull(message))
                        {
                            out.println("<center><div class=\"bg-info\">" + message + "</div></center>");
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
