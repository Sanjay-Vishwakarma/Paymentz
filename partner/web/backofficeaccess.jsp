<%@ page import="com.directi.pg.Functions,com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="top.jsp" %>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.ResourceBundle" %>
<%!
    private static Logger log=new Logger("backofficeaccess.jsp");
%>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit","backofficeaccess");
    PartnerFunctions partnerFunctions=new PartnerFunctions();
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String backofficeaccess_Merchant_Access_Control = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Merchant_Access_Control")) ? rb1.getString("backofficeaccess_Merchant_Access_Control") : "Merchant Access Control";
    String backofficeaccess_PartnerID = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_PartnerID")) ? rb1.getString("backofficeaccess_PartnerID") : "Partner ID";
    String backofficeaccess_MerchantID = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_MerchantID")) ? rb1.getString("backofficeaccess_MerchantID") : "Merchant ID*";
    String backofficeaccess_Search = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Search")) ? rb1.getString("backofficeaccess_Search") : "Search";
    String backofficeaccess_Report_Table = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Report_Table")) ? rb1.getString("backofficeaccess_Report_Table") : "Report Table";
    String backofficeaccess_Sr_No = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Sr_No")) ? rb1.getString("backofficeaccess_Sr_No") : "Sr No";
    String backofficeaccess_Merchant_ID = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Merchant_ID")) ? rb1.getString("backofficeaccess_Merchant_ID") : "Merchant ID";
    String backofficeaccess_Company_Name = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Company_Name")) ? rb1.getString("backofficeaccess_Company_Name") : "Company Name";
    String backofficeaccess_Merchant_Interface = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Merchant_Interface")) ? rb1.getString("backofficeaccess_Merchant_Interface") : "Merchant Interface Access";
    String backofficeaccess_Merchant_configuration = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Merchant_configuration")) ? rb1.getString("backofficeaccess_Merchant_configuration") : "BackOffice Configuration";
    String backofficeaccess_Merchant_DashBoard = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Merchant_DashBoard")) ? rb1.getString("backofficeaccess_Merchant_DashBoard") : "DashBoard";
    String backofficeaccess_Accounts_Details = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Accounts_Details")) ? rb1.getString("backofficeaccess_Accounts_Details") : "Accounts Details";
    String backofficeaccess_Merchant_settings = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Merchant_settings")) ? rb1.getString("backofficeaccess_Merchant_settings") : "Merchant Settings";
    String backofficeaccess_Transaction_Management = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Transaction_Management")) ? rb1.getString("backofficeaccess_Transaction_Management") : "Transaction Management";
    String backofficeaccess_Merchant_Invoice = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Merchant_Invoice")) ? rb1.getString("backofficeaccess_Merchant_Invoice") : "Invoice";
    String backofficeaccess_Token_Management = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Token_Management")) ? rb1.getString("backofficeaccess_Token_Management") : "Token Management ";
    String backofficeaccess_Virtual_Terminal = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Virtual_Terminal")) ? rb1.getString("backofficeaccess_Virtual_Terminal") : "Virtual Terminal";
    String backofficeaccess_Merchant_management = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Merchant_management")) ? rb1.getString("backofficeaccess_Merchant_management") : "Merchant Management";
    String backofficeaccess_Merchant_application = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Merchant_application")) ? rb1.getString("backofficeaccess_Merchant_application") : "Merchant Application";
    String backofficeaccess_Recurring_Module = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Recurring_Module")) ? rb1.getString("backofficeaccess_Recurring_Module") : "Recurring Module";
    String backofficeaccess_Rejected_Transaction = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Rejected_Transaction")) ? rb1.getString("backofficeaccess_Rejected_Transaction") : "Rejected Transaction";
    String backofficeaccess_Virtual_Checkout = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Virtual_Checkout")) ? rb1.getString("backofficeaccess_Virtual_Checkout") : "Virtual Checkout";
    String backofficeaccess_Account_Details = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Account_Details")) ? rb1.getString("backofficeaccess_Account_Details") : "Account Details";
    String backofficeaccess_Account_summary = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Account_summary")) ? rb1.getString("backofficeaccess_Account_summary") : "Account Summary";
    String backofficeaccess_Charge_Summary = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Charge_Summary")) ? rb1.getString("backofficeaccess_Charge_Summary") : "Charge Summary";
    String backofficeaccess_Transaction_Summary = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Transaction_Summary")) ? rb1.getString("backofficeaccess_Transaction_Summary") : "Transaction Summary";
    String backofficeaccess_Report_Summary = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Report_Summary")) ? rb1.getString("backofficeaccess_Report_Summary") : "Report Summary";
    String backofficeaccess_Settings = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Settings")) ? rb1.getString("backofficeaccess_Settings") : "Settings";
    String backofficeaccess_Merchant_Profile = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Merchant_Profile")) ? rb1.getString("backofficeaccess_Merchant_Profile") : "Merchant Profile";
    String backofficeaccess_Organisation_Profile = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Organisation_Profile")) ? rb1.getString("backofficeaccess_Organisation_Profile") : "Organisation Profile";
    String backofficeaccess_Generate_Key = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Generate_Key")) ? rb1.getString("backofficeaccess_Generate_Key") : "Generate Key";
    String backofficeaccess_Merchant_Config = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Merchant_Config")) ? rb1.getString("backofficeaccess_Merchant_Config") : "Merchant Config";
    String backofficeaccess_Fraud_Rule = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Fraud_Rule")) ? rb1.getString("backofficeaccess_Fraud_Rule") : "Fraud Rule Config";
    String backofficeaccess_Whitelist_Details = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Whitelist_Details")) ? rb1.getString("backofficeaccess_Whitelist_Details") : "Whitelist Details";
    String backofficeaccess_Blacklist_Details = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Blacklist_Details")) ? rb1.getString("backofficeaccess_Blacklist_Details") : "Blacklist Details";
    String backofficeaccess_Transaction = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Transaction")) ? rb1.getString("backofficeaccess_Transaction") : "Transaction";
    String backofficeaccess_Capture = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Capture")) ? rb1.getString("backofficeaccess_Capture") : "Capture";
    String backofficeaccess_Reversal = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Reversal")) ? rb1.getString("backofficeaccess_Reversal") : "Reversal";
    String backofficeaccess_Payout = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Payout")) ? rb1.getString("backofficeaccess_Payout") : "Payout";
    String backofficeaccess_Generate_Invoice = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Generate_Invoice")) ? rb1.getString("backofficeaccess_Generate_Invoice") : "Generate Invoice";
    String backofficeaccess_Invoice_History = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Invoice_History")) ? rb1.getString("backofficeaccess_Invoice_History") : "Invoice History";
    String backofficeaccess_Invoice_Config = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Invoice_Config")) ? rb1.getString("backofficeaccess_Invoice_Config") : "Invoice Config";
    String backofficeaccess_Registration_History = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Registration_History")) ? rb1.getString("backofficeaccess_Registration_History") : "Registration History";
    String backofficeaccess_Register_Card = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Register_Card")) ? rb1.getString("backofficeaccess_Register_Card") : "Register Card";
    String backofficeaccess_Sorry = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Sorry")) ? rb1.getString("backofficeaccess_Sorry") : " Sorry";
    String backofficeaccess_Save = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_Save")) ? rb1.getString("backofficeaccess_Save") : " Save";
    String backofficeaccess_no = StringUtils.isNotEmpty(rb1.getString("backofficeaccess_no")) ? rb1.getString("backofficeaccess_no") : " No records found.";


%>
<html>
<head>
    <style type="text/css">
        @media (max-width: 640px){
            #smalltable{
                width: 100%!important;
            }
        }

        #ui-id-2
        {
            overflow: auto;
            max-height: 350px;
        }
    </style>
    <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
    <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
</head>
<title><%=company%> Merchant Settings> Merchant BackOffice Access </title>
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
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=backofficeaccess_Merchant_Access_Control%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                            if (partner.isLoggedInPartner(session))
                            {
                                //LinkedHashMap memberidDetails= partner.getPartnerMembersDetail((String) session.getAttribute("merchantid"));
                                String memberid=nullToStr(request.getParameter("memberid"));
                                String pid=nullToStr(request.getParameter("pid"));
                                String Config =null;
                                String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
                                if(Roles.contains("superpartner")){

                                }else{
                                    pid = String.valueOf(session.getAttribute("merchantid"));
                                    Config = "disabled";
                                }
                                String partnerid = session.getAttribute("partnerId").toString();
                        %>
                        <form action="/partner/net/BackOfficeAccess?ctoken=<%=ctoken%>" method="get" name="F1" onsubmit="" class="form-horizontal">
                            <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                            <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                            <br>
                            <div class="widget-content padding">
                                <div id="horizontal-form">
                                    <%
                                        String errormsg1 = (String) request.getAttribute("error");
                                        if (partnerFunctions.isValueNull(errormsg1))
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg1 + "</h5>");
                                        }
                                    %>
                                        <div class="form-group col-md-4">
                                            <label class="col-sm-4 control-label"><%=backofficeaccess_PartnerID%></label>
                                            <div class="col-sm-8">
                                                <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                                                <input name="pid" type="hidden" value="<%=pid%>" >
                                                </div>
                                            </div>



                                            <div class="form-group col-md-4">
                                        <label class="col-sm-4 control-label"><%=backofficeaccess_MerchantID%></label>
                                        <div class="col-sm-8">
                                            <input name="memberid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                                          <%--  <select name="memberid" class="form-control">
                                                <%
                                                    for(Object mid : memberidDetails.keySet())
                                                    {
                                                        String isSelected="";
                                                        if(mid.toString().equals(memberid))
                                                        {
                                                            isSelected="selected";
                                                        }
                                                        else
                                                        {
                                                            isSelected="";
                                                        }
                                                %>
                                                <option value="<%=mid%>" <%=isSelected%>><%=mid+"-"+memberidDetails.get(mid)%></option>
                                                <%
                                                    }
                                                %>
                                            </select>--%>
                                        </div>
                                    </div>
                                    <%--<div class="form-group col-md-3 has-feedback">&nbsp;</div>--%>
                                    <div class="form-group col-md-3">
                                        <div class="col-sm-offset-2 col-sm-3">
                                            <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                                                &nbsp;&nbsp;<%=backofficeaccess_Search%></button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <form action="/partner/net/SetReservesBackOffice?ctoken=<%=ctoken%>" method=post>
                <input type="hidden" value="" name="onchangedvalue" id="onchangedvalue">   <%--***do not remove the field*****--%>

                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=backofficeaccess_Report_Table%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <div class="widget-content padding" style="overflow-x: auto;">
                                <%
                                    String errormsg = (String)request.getAttribute("cbmessage");
                                    HashMap hash = (HashMap) request.getAttribute("memberdetails");
                                    Hashtable uhash = (Hashtable) request.getAttribute("uhash");
                                    HashMap temphash = null;
                                    int pageno =partnerFunctions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
                                    int pagerecords =partnerFunctions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
                                    Functions functions = new Functions();
                                    int records = 0;
                                    int totalrecords = 0;
                                    String str="";
                                    if((hash!=null && hash.size()>0) && (partner!=null))
                                    {
                                        try{
                                            records = Integer.parseInt((String) hash.get("records"));
                                            totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
                                        }
                                        catch (Exception ex)
                                        {
                                            log.error("Records & TotalRecords is found null",ex);
                                        }
                                    }
                                    if (uhash != null && uhash.size() > 0)
                                    {
                                %>
                                <font class="info">Modification Successful</font>
                                <% }

                                    if (records > 0)
                                    {  str = str + "?SRecords=" + pagerecords;
                                        str = str + "&ctoken=" + ctoken;
                                        str =str + "&year=" + URLEncoder.encode((String) hash.get("year")) + "&month=" + URLEncoder.encode((String) hash.get("month"));
                                %>
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <% String style = "td1";
                                        for (int pos = 1; pos <= records; pos++)
                                        {
                                            String id = Integer.toString(pos);
                                            int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

                                            if (pos % 2 == 0)
                                                style = "tr0";
                                            else
                                                style = "tr0";
                                            temphash = null;
                                            temphash = (HashMap) hash.get(id);

                                            String memberId = (String) temphash.get("memberid");
                                            String companyName = (String) temphash.get("company_name");
                                            String accountId = Functions.checkStringNull((String) temphash.get("accountid"));
                                            String aptprompt = "0.00";

                                            String isReadOnly = "readonly";
                                            String inactive="disabled=\"disabled\"";

                                            if (accountId != null)
                                            {
                                                isReadOnly = "";
                                                inactive="";
                                                BigDecimal tmpObj = new BigDecimal("0.01");
                                            }
                                    %>
                                    <thead>
                                    <tr>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=backofficeaccess_Sr_No%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=backofficeaccess_Merchant_ID%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=backofficeaccess_Company_Name%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=backofficeaccess_Merchant_Interface%></b></td>
                                        <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Merchant Login with Otp</b></td>
                                    </tr>
                                    </thead>
                                    <tr>
                                        <td valign="middle" data-label="Sr No" align="center" style="vertical-align: middle;" class="<%=style%>"><%=srno%></td>
                                        <td valign="middle" data-label="Merchant ID" align="center" style="vertical-align: middle;" class="<%=style%>"><%=memberId%><input type=hidden name="memberid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberId)%>"></td>
                                        <td valign="middle" data-label="Company Name" align="center" style="vertical-align: middle;" class="<%=style%>"><%=companyName%></td>
                                        <td valign="middle" data-label="Company Name" align="center" style="vertical-align: middle;" class="<%=style%>"><select name='icici' class="form-control"  style="background: #ffffff" onchange="ChangeFunction(this.value,'Merchant Interface Access')"><%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("icici"))));%></select></td>
                                        <td valign="middle" data-label="Merchant Login with Otp" align="center" style="vertical-align: middle;" class="<%=style%>"><select name='merchant_verify_otp' class="form-control"  style="background: #ffffff" onchange="ChangeFunction(this.value,'Merchant Login with Otp')"><%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("merchant_verify_otp"))));%></select></td>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=backofficeaccess_Merchant_configuration%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <center>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <table align=center width="100%" id="smalltable" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <thead>
                                        <tr>
                                            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b><%=backofficeaccess_Merchant_DashBoard%>             </b></td>
                                            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b><%=backofficeaccess_Accounts_Details%>      </b></td>
                                            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b><%=backofficeaccess_Merchant_settings%>     </b></td>
                                            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b><%=backofficeaccess_Transaction_Management%></b></td>
                                            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b><%=backofficeaccess_Merchant_Invoice%>             </b></td>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr>
                                            <td valign="middle" data-label="Dash Board Access" align="center">
                                                <select name='dashboard_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'DashBoard')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("dashboard_access"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Accounting Access" align="center">
                                                <select name='accounting_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Accounts Details')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("accounting_access"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Setting Access" align="center">
                                                <select name='setting_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Merchant Settings')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("setting_access"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Transactions Access" align="center">
                                                <select name='transactions_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Transaction Management')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("transactions_access"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Invoicing Access" align="center">
                                                <select name='invoicing_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Invoice')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("invoicing_access"))));
                                                    %>
                                                </select>
                                            </td>
                                        </tr>
                                        </tbody>
                                        <thead>
                                        <tr>
                                            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b><%=backofficeaccess_Token_Management%>     </b></td>
                                            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b><%=backofficeaccess_Virtual_Terminal%>      </b></td>
                                            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b><%=backofficeaccess_Merchant_management%>      </b></td>
                                            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b><%=backofficeaccess_Merchant_application%>   </b></td>
                                            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b><%=backofficeaccess_Recurring_Module%>            </b></td>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr>
                                            <td valign="middle" data-label="Token Management" align="center">
                                                <select name='iscardregistrationallowed' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Token Management')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("iscardregistrationallowed"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Virtual Terminal Access" align="center">
                                                <select name='virtualterminal_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Virtual Terminal')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("virtualterminal_access"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Merchant Management Access" align="center">
                                                <select name='merchantmgt_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Merchant Management')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("merchantmgt_access"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Merchant Application" align="center">
                                                <select name='isappmanageractivate' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Merchant Application')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isappmanageractivate"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Recurring Module" align="center">
                                                <select name='is_recurring' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Recurring Module')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("is_recurring"))));
                                                    %>
                                                </select>
                                            </td>
                                        </tr>
                                        </tbody>
                                        <thead>
                                        <tr>
                                            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b> <%=backofficeaccess_Rejected_Transaction%> </b></td>
                                            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b>&nbsp;</b><%=backofficeaccess_Virtual_Checkout%></td>
                                            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b>&nbsp;</b>Pay By Link</td>
                                            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b>&nbsp;</b></td>
                                            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;"><b>&nbsp;</b></td>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr>
                                            <td valign="middle" data-label="rejected_transaction" align="center">
                                                <select name='rejected_transaction' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Rejected Transaction')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("rejected_transaction"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="virtual_checkout" align="center">
                                                <select name='virtual_checkout' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Virtual Checkout')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("virtual_checkout"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="paybylink" align="center">
                                                <select name="paybylink" class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value, 'Pay By Link')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("paybylink"))));
                                                    %>
                                                </select>
                                            </td>
                                        <tr>
                                            <td  colspan="5" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;" width="100%"><b><%=backofficeaccess_Account_Details%> </b></td>
                                        </tr>
                                        <thead>
                                        <tr>
                                            <td  valign="middle" align="center" style="background-color: #ffffff"><b><%=backofficeaccess_Account_summary%>      </b></td>
                                            <td  valign="middle" align="center" style="background-color: #ffffff"><b><%=backofficeaccess_Charge_Summary%>       </b></td>
                                            <td  valign="middle" align="center" style="background-color: #ffffff"><b><%=backofficeaccess_Transaction_Summary%>   </b></td>
                                            <td  valign="middle" align="center" style="background-color: #ffffff" colspan="2"><b><%=backofficeaccess_Report_Summary%></b></td>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr>
                                            <td valign="middle" data-label="Account Summary" align="center">
                                                <select name='accounts_account_summary_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Account Summary')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("accounts_account_summary_access"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label=" Charge Summary" align="center">
                                                <select name='accounts_charges_summary_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Charge Summary')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("accounts_charges_summary_access"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Transaction Summary" align="center">
                                                <select name='accounts_transaction_summary_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Transaction Summary')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("accounts_transaction_summary_access"))));
                                                    %>
                                                </select>
                                            <td valign="middle" data-label="Report" align="center" colspan="2">
                                                <select name='accounts_reports_summary_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Report Summary')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("accounts_reports_summary_access"))));
                                                    %>
                                                </select>
                                            </td>
                                        </tr>
                                        </tbody>
                                        <tr>
                                            <td  colspan="5" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;" width="100%"><b><%=backofficeaccess_Settings%> </b></td>
                                        </tr>
                                        <thead>
                                        <tr>
                                            <td  colvalign="middle" align="center" style="background-color: #ffffff"><b><%=backofficeaccess_Merchant_Profile%> </b></td>
                                            <td  valign="middle" align="center" style="background-color: #ffffff"><b><%=backofficeaccess_Organisation_Profile%> </b></td>
                                            <td  valign="middle" align="center" style="background-color: #ffffff"><b>View Key </b></td>
                                            <%--<td  valign="middle" align="center" style="background-color: #ffffff"><b>Checkout Page   </b></td>--%>
                                            <td  valign="middle" align="center" style="background-color: #ffffff"><b><%=backofficeaccess_Generate_Key%></b></td>
                                            <td  colspan="2" valign="middle" align="center" style="background-color: #ffffff"><b><%=backofficeaccess_Merchant_Config%></b></td>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr>
                                            <td valign="middle" data-label="Merchant Profile" align="center" >
                                                <select name='settings_merchant_profile_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Merchant Profile')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("settings_merchant_profile_access"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label=" Organisation Profile" align="center">
                                                <select name='settings_organisation_profile_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Organisation Profile')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("settings_organisation_profile_access"))));
                                                    %>
                                                </select>
                                            </td>
                                            <%--<td valign="middle" data-label="Checkout Page" align="center" >
                                                <select name='settings_checkout_page_access' class="form-control" style="background: #ffffff">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("settings_checkout_page_access"))));
                                                    %>
                                                </select>--%>
                                            <td valign="middle" data-label="Generate key" align="center" >
                                                <select name='generateview' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Generate Key')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("generateview"))));
                                                    %>
                                                </select>
                                            <td valign="middle" data-label="Generate key" align="center" >
                                                <select name='settings_generate_key_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Generate Key')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("settings_generate_key_access"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td colspan="3" valign="middle" data-label="Merchant Config" align="center" >
                                                <select name='settings_merchant_config_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Merchant Config')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("settings_merchant_config_access"))));
                                                    %>
                                                </select>
                                            </td>
                                        </tr>
                                        </tbody>
                                        <thead>
                                        <tr>
                                            <td  colspan="3" valign="middle" align="center" style="background-color: #ffffff"><b><%=backofficeaccess_Fraud_Rule%></b></td>
                                            <td  valign="middle" align="center" style="background-color: #ffffff"><b><%=backofficeaccess_Whitelist_Details%></b></td>
                                            <td  valign="middle" align="center" style="background-color: #ffffff"><b><%=backofficeaccess_Blacklist_Details%></b></td>
                                            <%--<td  colspan="2" valign="middle" align="center" style="background-color: #ffffff"><b>Invoice Config</b></td>--%>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr>
                                            <td colspan="3" valign="middle" data-label="FraudRule Config" align="center" >
                                                <select name='settings_fraudrule_config_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Fraud Rule Config')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("settings_fraudrule_config_access"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="FraudRule Config" align="center" >
                                                <select name='settings_whitelist_details' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Whitelist Details')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("settings_whitelist_details"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="FraudRule Config" align="center" >
                                                <select name='settings_blacklist_details' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Blacklist Details')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("settings_blacklist_details"))));
                                                    %>
                                                </select>
                                            </td>
                                            <%--<td colspan="3" valign="middle" data-label="InoiceConfig" align="center">
                                                <select name='settings_invoice_config_access' class="form-control" style="background: #ffffff">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("settings_invoice_config_access"))));
                                                    %>
                                                </select>
                                            </td>--%>
                                        </tr>
                                        </tbody>
                                        <tr>
                                            <td  colspan="5" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;" width="100%"><b><%=backofficeaccess_Transaction_Management%> </b></td>
                                        </tr>
                                        <thead>
                                        <tr>
                                            <td  valign="middle" align="center" style="background-color: #ffffff"><b><%=backofficeaccess_Transaction%>     </b></td>
                                            <td  valign="middle" align="center" style="background-color: #ffffff"><b><%=backofficeaccess_Capture%>   </b></td>
                                            <td  valign="middle" align="center" style="background-color: #ffffff"><b><%=backofficeaccess_Reversal%>   </b></td>
                                            <td  valign="middle" align="center" style="background-color: #ffffff" colspan="2"><b><%=backofficeaccess_Payout%></b></td>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr>
                                            <td valign="middle" data-label="Transaction" align="center">
                                                <select name='transmgt_transaction_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Transaction')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("transmgt_transaction_access"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label=" Capture" align="center">
                                                <select name='transmgt_capture_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Capture')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("transmgt_capture_access"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" data-label="Reversal" align="center">
                                                <select name='transmgt_reversal_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Reversal')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("transmgt_reversal_access"))));
                                                    %>
                                                </select>
                                            <td valign="middle" data-label="Payout" align="center" colspan="2">
                                                <select name='transmgt_payout_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Payout')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("transmgt_payout_access"))));
                                                    %>
                                                </select>
                                            </td>
                                        </tr>
                                        </tbody>
                                        <tr>
                                            <td  colspan="5" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;" width="100%"><b><%=backofficeaccess_Merchant_Invoice%> </b></td>
                                        </tr>
                                        <thead>
                                        <tr>
                                            <td  valign="middle" colspan="2" align="center" style="background-color: #ffffff"><b><%=backofficeaccess_Generate_Invoice%>      </b></td>
                                            <td  valign="middle" colspan="2" align="center" style="background-color: #ffffff"><b><%=backofficeaccess_Invoice_History%>   </b></td>
                                            <td  colspan="2" valign="middle" align="center" style="background-color: #ffffff"><b><%=backofficeaccess_Invoice_Config%></b></td>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr>
                                            <td valign="middle" colspan="2" data-label="Generate Invoice" align="center">
                                                <select name='invoice_generate_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Generate Invoice')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("invoice_generate_access"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" colspan="2" data-label=" Invoice History" align="center">
                                                <select name='invoice_history_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Invoice History')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("invoice_history_access"))));
                                                    %>
                                                </select>
                                            </td>
                                            <%--<td valign="middle" colspan="2" data-label=" Invoice History" align="center">
                                                <select name='invoice_history_access' class="form-control" style="background: #ffffff">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("invoice_history_access"))));
                                                    %>
                                                </select>
                                            </td>--%>

                                            <td colspan="2" valign="middle" data-label="InoiceConfig" align="center">
                                                <select name='settings_invoice_config_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Invoice Config')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("settings_invoice_config_access"))));
                                                    %>
                                                </select>
                                            </td>
                                        </tr>
                                        </tbody>
                                        <tr>
                                            <td  colspan="5" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;width: 20%;" width="100%"><b><%=backofficeaccess_Token_Management%> </b></td>
                                        </tr>
                                        <thead>
                                        <tr>
                                            <td  valign="middle" colspan="3" align="center" style="background-color: #ffffff"><b><%=backofficeaccess_Registration_History%>     </b></td>
                                            <td  valign="middle" colspan="2" align="center" style="background-color: #ffffff"><b><%=backofficeaccess_Register_Card%>   </b></td>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr>
                                            <td valign="middle" colspan="3" data-label="Registeration History" align="center">
                                                <select name='tokenmgt_registration_history_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Registration History')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("tokenmgt_registration_history_access"))));
                                                    %>
                                                </select>
                                            </td>
                                            <td valign="middle" colspan="2" data-label=" Register Card" align="center">
                                                <select name='tokenmgt_register_card_access' class="form-control" style="background: #ffffff" onchange="ChangeFunction(this.value,'Register Card')">
                                                    <%
                                                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("tokenmgt_register_card_access"))));
                                                    %>
                                                </select>
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                    <%
                                        } //end for
                                    %>
                                    <table align="center" id="smalltable" border="0" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 50%;">
                                        <tr style="background-color: #ffffff!important;">
                                            <td align="center" colspan="2">
                                                <button type="submit" value="Save" class="btn btn-default">
                                                   <%=backofficeaccess_Save%>
                                                </button>
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </center>
                        </div>
                    </div>
                </div>
            </form>
            <br>
            <%
                }
                /*else if (functions.isValueNull(errormsg))
                {
                    System.out.println("inside else if::::"+errormsg);
                    out.print("<div class=\"bg-info\" style=\"text-align:center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+ errormsg +"</div>");
                }*/
                else
                {
                    String sSuccessMessage = (String)request.getAttribute("sSuccessMessage");
                    if (sSuccessMessage!=null)
                    {
                        out.println(Functions.NewShowConfirmation1("Sorry", sSuccessMessage));
                    }
                    else
                    {
                        out.println(Functions.NewShowConfirmation1(backofficeaccess_Sorry, backofficeaccess_no));
                    }
                    out.println("</div>");
                }
            %>
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
<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
</body>
</html>
<%!
    public static String nullToStr(String str)
    {
        if(str == null)
            return "";
        return str;
    }
%>