<%@ page import="com.directi.pg.Functions,com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="top.jsp" %>
<%@ page import="java.math.BigDecimal"%>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit","merchantmailaccess");
    PartnerFunctions partnerFunctions=new PartnerFunctions();

    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);


    String merchantmailaccess_Merchant_Mail_Settings = rb1.getString("merchantmailaccess_Merchant_Mail_Settings");
    String merchantmailaccess_Partner_ID = rb1.getString("merchantmailaccess_Partner_ID");
    String merchantmailaccess_Merchant_ID = rb1.getString("merchantmailaccess_Merchant_ID");
    String merchantmailaccess_Search = rb1.getString("merchantmailaccess_Search");
    String merchantmailaccess_Report_Table = rb1.getString("merchantmailaccess_Report_Table");
    String merchantmailaccess_IS_Validate_Email = rb1.getString("merchantmailaccess_IS_Validate_Email");
    String merchantmailaccess_Customer_Reminder_Email = rb1.getString("merchantmailaccess_Customer_Reminder_Email");
    String merchantmailaccess_Merchant_Mail_Sent = rb1.getString("merchantmailaccess_Merchant_Mail_Sent");
    String merchantmailaccess_Is_Refund_Mail_Sent = rb1.getString("merchantmailaccess_Is_Refund_Mail_Sent");
    String merchantmailaccess_Chargeback_Email = rb1.getString("merchantmailaccess_Chargeback_Email");
    String merchantmailaccess_Save = rb1.getString("merchantmailaccess_Save");
    String merchantmailaccess_Sorry = rb1.getString("merchantmailaccess_Sorry");
    String merchantmailaccess_No = rb1.getString("merchantmailaccess_No");

%>
<%!
    private static Logger log=new Logger("merchantmailaccess.jsp");
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
<title><%=company%> Merchant Settings> Merchant Mail Settings </title>
<body class="bodybackground">

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=merchantmailaccess_Merchant_Mail_Settings%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                            if (partner.isLoggedInPartner(session))
                            {
                                // LinkedHashMap memberidDetails= partner.getPartnerMembersDetail((String) session.getAttribute("merchantid"));
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
                        <form action="/partner/net/MerchantMailAccess?ctoken=<%=ctoken%>" method="get" name="F1" onsubmit="" class="form-horizontal">
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
                                        <label class="col-sm-4 control-label"><%=merchantmailaccess_Partner_ID%></label>
                                        <div class="col-sm-8">
                                            <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                                            <input name="pid" type="hidden" value="<%=pid%>">
                                        </div>
                                    </div>

                                    <div class="form-group col-md-4">
                                        <label class="col-sm-4 control-label"><%=merchantmailaccess_Merchant_ID%></label>
                                        <div class="col-sm-8">
                                            <input name="memberid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                                        </div>
                                    </div>
                                    <%--<div class="form-group col-md-3 has-feedback">&nbsp;</div>--%>
                                    <div class="form-group col-md-3">
                                        <div class="col-sm-offset-2 col-sm-3">
                                            <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                                                &nbsp;&nbsp;<%=merchantmailaccess_Search%></button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <form action="/partner/net/SetReservesMailSetting?ctoken=<%=ctoken%>" method=post>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=merchantmailaccess_Report_Table%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <div class="widget-content padding" style="overflow-x: auto;">
                                <%
                                    String errormsg = (String)request.getAttribute("cbmessage");
                                    Hashtable hash = (Hashtable) request.getAttribute("memberdetails");
                                    Hashtable uhash = (Hashtable) request.getAttribute("uhash");
                                    Hashtable temphash = null;
                                    Hashtable hashtablepartner= (Hashtable)request.getAttribute("partners");
                                    Hashtable agent= (Hashtable)request.getAttribute("agents");
                                    int pageno =partnerFunctions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
                                    int pagerecords =partnerFunctions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
                                    Functions functions = new Functions();
                                    int records = 0;
                                    int totalrecords = 0;
                                    int records1 = 0;
                                    int records2 = 0;

                                    String str="";
                                    if((hash!=null && hash.size()>0) && (partner!=null && hashtablepartner.size()>0) && (agent!=null && agent.size()>0))
                                    {
                                        try{
                                            records = Integer.parseInt((String) hash.get("records"));
                                            totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
                                            records1 = Integer.parseInt((String) hashtablepartner.get("records1"));
                                            records2 = Integer.parseInt((String) agent.get("records2"));
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
                                <center>
                                    <div class="reporttable" style="margin-bottom: 9px">
                                        <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
                                               style="margin-bottom: 0px">
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
                                                temphash = (Hashtable) hash.get(id);

                                                String memberId = (String) temphash.get("memberid");
                                                String companyName = (String) temphash.get("company_name");
                                                String accountId = Functions.checkStringNull((String) temphash.get("accountid"));

                                                String aptprompt = "0.00";
                                                String isReadOnly = "readonly";
                                                String inactive="disabled=\"disabled\"";
                                                String daily_card_amount_limit="N/A";
                                                String weekly_card_amount_limit="N/A";
                                                String monthly_card_amount_limit="N/A";
                                                String daily_amount_limit="100.00",monthly_amount_limit="100.00",daily_card_limit="5",weekly_card_limit="10",monthly_card_limit="20";
                                                if (accountId != null)
                                                {
                                                    isReadOnly = "";
                                                    inactive="";
                                                    BigDecimal tmpObj = new BigDecimal("0.01");
                                                    aptprompt = ((new BigDecimal((String) temphash.get("aptprompt")).multiply(tmpObj))).toString();
                                                    daily_amount_limit=(new BigDecimal((String) temphash.get("daily_amount_limit"))).toString();
                                                    monthly_amount_limit=(new BigDecimal((String) temphash.get("monthly_amount_limit"))).toString();
                                                    daily_card_limit=(new BigDecimal((String) temphash.get("daily_card_limit"))).toString();
                                                    weekly_card_limit=(new BigDecimal((String) temphash.get("weekly_card_limit"))).toString();
                                                    monthly_card_limit=(new BigDecimal((String) temphash.get("monthly_card_limit"))).toString();
                                                    daily_card_amount_limit=(new BigDecimal((String) temphash.get("daily_card_amount_limit"))).toString();
                                                    weekly_card_amount_limit=(new BigDecimal((String) temphash.get("weekly_card_amount_limit"))).toString();
                                                    monthly_card_amount_limit=(new BigDecimal((String) temphash.get("monthly_card_amount_limit"))).toString();

                                                }

                                        %>
                                        <input type="hidden" name="accountids" value="<%=accountId%>">
                                        <input type=hidden name="memberid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberId)%>">
                                        <thead>
                                        <tr>
                                            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=merchantmailaccess_IS_Validate_Email%></b></td>
                                            <%--<td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=merchantmailaccess_Customer_Reminder_Email%></b></td>
                                            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=merchantmailaccess_Merchant_Mail_Sent%></b></td>--%>
                                            <%--<td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=merchantmailaccess_Is_Refund_Mail_Sent%></b></td>
                                            <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=merchantmailaccess_Chargeback_Email%></b></td>--%>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <tr>
                                            <td valign="middle" data-label="IS Validate Email(Y/N)" align="center" class="<%=style%>">
                                                <select name='isValidateEmail' class="form-control" <%--<%=isReadOnly%>--%>>
                                                    <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isValidateEmail"))));  %></select>
                                            </td>
                                            <%--<td valign="middle" data-label="Customer Reminder Email(Y/N)" align="center" class="<%=style%>">
                                                <select name='custremindermail' class="form-control" &lt;%&ndash;<%=isReadOnly%>&ndash;%&gt;>
                                                    <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("custremindermail"))));  %></select>
                                            </td>--%>
                                            <%--<td valign="middle" data-label="Merchant Mail Sent(Y/N)" align="center" class="<%=style%>">
                                                <select name='emailSent' class="form-control" &lt;%&ndash;<%=isReadOnly%>&ndash;%&gt;>
                                                    <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("emailSent"))));  %></select>
                                            </td>--%>

                                            <%--<td valign="middle" data-label="Is Refund Mail Sent(Y/N)" align="center" class="<%=style%>">
                                                <select name='isRefundEmailSent' class="form-control" &lt;%&ndash;<%=isReadOnly%>&ndash;%&gt;>
                                                    <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isRefundEmailSent"))));  %></select>
                                            </td>

                                            <td valign="middle" data-label="Chargeback Email (Y/N)" align="center" class="<%=style%>">
                                                <select name='chargebackEmail' class="form-control" &lt;%&ndash;<%=isReadOnly%>&ndash;%&gt;>
                                                    <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("chargebackEmail"))));  %></select>
                                            </td>--%>

                                        </tr>
                                        </tbody>
                                    </table>
                                        </div>
                                        <br>
                                    <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
                                           style="margin-bottom: 0px">
                                        <tr class="th0">
                                            <td valign="middle" align="center"  colspan="8"  style="height: 30px; background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <center><b>Merchant Email Notification Settings</b></center>
                                            </td>
                                        </tr>
                                    </table>
                                        <div class="reporttable" style="margin-bottom: 9px;">
                                            <table border="0" width="100%" class="display table table table-striped table-bordered table-hover dataTable"
                                                   style="margin-bottom: 0px">
                                                <tr class="td1">
                                                    <td valign="middle" align="center"  colspan="8"  style="height: 30px; background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                        <center><b>Setup Mails</b></center>
                                                    </td>
                                                </tr>
                                                </thead>
                                                <%
                                                    String merchantRegistrationMail="";
                                                    String merchantChangePassword="";
                                                    String merchantChangeProfile="";
                                                    if("Y".equals(temphash.get("merchantRegistrationMail")))
                                                        merchantRegistrationMail="checked";
                                                    if("Y".equals(temphash.get("merchantChangePassword")))
                                                        merchantChangePassword="checked";
                                                    if("Y".equals(temphash.get("merchantChangeProfile")))
                                                        merchantChangeProfile="checked";
                                                    System.out.println("merchantRegistrationMail----"+temphash.get("merchantChangeProfile"));
                                                %>
                                                <tr>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Event Name</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Main Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Sales Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Fraud Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Chargeback Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Refund Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Technical Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Billing Contact</td>
                                                </tr>
                                                <tr>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Registration</td>
                                                    <td align="center" class="tr0"><input type="checkbox" name='merchantRegistrationMail' <%=merchantRegistrationMail%>
                                                                                          value="Y">
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                </tr>
                                                <tr>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Change Password</td>
                                                    <td align="center" class="tr0"><input type="checkbox" name='merchantChangePassword' <%=merchantChangePassword%>
                                                                                          value="Y">
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                </tr>
                                                <tr>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Change Profile</td>
                                                    <td align="center" class="tr0"><input type="checkbox" name='merchantChangeProfile' <%=merchantChangeProfile%>
                                                                                          value="Y">
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                </tr>
                                            </table>
                                        </div>
                                        <div class="reporttable" style="margin-bottom: 9px;">
                                            <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
                                                   style="margin-bottom: 0px">
                                                <tr class="td1">
                                                    <td valign="middle" align="center"  colspan="8"  style="height: 30px; background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                        <center><b>Transaction Mails</b></center>
                                                    </td>
                                                </tr>
                                                </thead>
                                                <%
                                                    String transactionSuccessfulMail="";
                                                    String transactionFailMail="";
                                                    String transactionCapture="";
                                                    String transactionPayoutSuccess="";
                                                    String transactionPayoutFail="";
                                                    if("Y".equals(temphash.get("transactionSuccessfulMail")))
                                                        transactionSuccessfulMail="checked";
                                                    if("Y".equals(temphash.get("transactionFailMail")))
                                                        transactionFailMail="checked";
                                                    if("Y".equals(temphash.get("transactionCapture")))
                                                        transactionCapture="checked";
                                                    if("Y".equals(temphash.get("transactionPayoutSuccess")))
                                                        transactionPayoutSuccess="checked";
                                                    if("Y".equals(temphash.get("transactionPayoutFail")))
                                                        transactionPayoutFail="checked";
                                                %>
                                                <tr>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Event Name</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Main Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Sales Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Fraud Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Chargeback Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Refund Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Technical Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Billing Contact</td>
                                                </tr>
                                                <tr>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Successful</td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td align="center" class="tr0"><input type="checkbox" name='transactionSuccessfulMail' <%=transactionSuccessfulMail%>
                                                                                          value="Y">
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                </tr>
                                                <tr>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Failed</td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td align="center" class="tr0"><input type="checkbox" name='transactionFailMail' <%=transactionFailMail%>
                                                                                          value="Y">
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                </tr>
                                                <tr>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Capture</td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td align="center" class="tr0"><input type="checkbox" name='transactionCapture' <%=transactionCapture%>
                                                                                          value="Y">
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                </tr>
                                                <tr>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Payout Successful</td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td align="center" class="tr0"><input type="checkbox" name='transactionPayoutSuccess' <%=transactionPayoutSuccess%>
                                                                                          value="Y">
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                </tr>
                                                <tr>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Payout Fail</td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td align="center" class="tr0"><input type="checkbox" name='transactionPayoutFail' <%=transactionPayoutFail%>
                                                                                          value="Y">
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                </tr>
                                            </table>
                                        </div>
                                        <div class="reporttable" style="margin-bottom: 9px;">
                                            <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
                                                   style="margin-bottom: 0px">
                                                <tr class="td1">
                                                    <td valign="middle" align="center"  colspan="8"  style="height: 30px; background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                        <center><b>Refund/Chargeback Mails</b></center>
                                                    </td>
                                                </tr>
                                                </thead>
                                                <%
                                                    String refundMail="";
                                                    String chargebackMail="";
                                                    if("Y".equals(temphash.get("refundMail")))
                                                        refundMail="checked";
                                                    if("Y".equals(temphash.get("chargebackMail")))
                                                        chargebackMail="checked";
                                                %>
                                                <tr>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Event Name</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Main Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Sales Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Fraud Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Chargeback Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Refund Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Technical Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Billing Contact</td>
                                                </tr>
                                                <tr>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Refunds</td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td align="center" class="tr0"><input type="checkbox" name='refundMail' <%=refundMail%>
                                                                                          value="Y">
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                </tr>
                                                <tr>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Chargebacks</td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td align="center" class="tr0"><input type="checkbox" name='chargebackMail' <%=chargebackMail%>
                                                                                          value="Y">
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                </tr>
                                            </table>
                                        </div>
                                        <div class="reporttable" style="margin-bottom: 9px;">
                                            <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
                                                   style="margin-bottom: 0px">
                                                <tr class="td1">
                                                    <td valign="middle" align="center"  colspan="8"  style="height: 30px; background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                        <center><b>Invoice Mails</b></center>
                                                    </td>
                                                </tr>
                                                </thead>
                                                <%
                                                    String transactionInvoice="";
                                                    if("Y".equals(temphash.get("transactionInvoice")))
                                                        transactionInvoice="checked";
                                                %>
                                                <tr>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Event Name</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Main Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Sales Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Fraud Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Chargeback Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Refund Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Technical Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Billing Contact</td>
                                                </tr>
                                                <tr>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Invoice</td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td align="center" class="tr0"><input type="checkbox" name='transactionInvoice' <%=transactionInvoice%>
                                                                                          value="Y">
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                </tr>
                                            </table>
                                        </div>
                                        <div class="reporttable" style="margin-bottom: 9px;">
                                            <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
                                                   style="margin-bottom: 0px">
                                                <tr class="td1">
                                                    <td valign="middle" align="center"  colspan="8"  style="height: 30px; background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                        <center><b>Tokenization Mails</b></center>
                                                    </td>
                                                </tr>
                                                </thead>
                                                <%
                                                    String cardRegistration="";
                                                    if("Y".equals(temphash.get("cardRegistration")))
                                                        cardRegistration="checked";
                                                %>
                                                <tr>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Event Name</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Main Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Sales Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Fraud Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Chargeback Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Refund Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Technical Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Billing Contact</td>
                                                </tr>
                                                <tr>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Card Registration</td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td align="center" class="tr0"><input type="checkbox" name='cardRegistration' <%=cardRegistration%>
                                                                                          value="Y">
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                </tr>
                                            </table>
                                        </div>
                                        <div class="reporttable" style="margin-bottom: 9px;">
                                            <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
                                                   style="margin-bottom: 0px">
                                                <tr class="td1">
                                                    <td valign="middle" align="center"  colspan="8"  style="height: 30px; background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                        <center><b>Payout Mails</b></center>
                                                    </td>
                                                </tr>
                                                </thead>
                                                <%
                                                    String payoutReport="";
                                                    if("Y".equals(temphash.get("payoutReport")))
                                                        payoutReport="checked";
                                                %>
                                                <tr>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Event Name</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Main Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Sales Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Fraud Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Chargeback Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Refund Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Technical Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Billing Contact</td>
                                                </tr>
                                                <tr>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Payout Report</td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td align="center" class="tr0"><input type="checkbox" name='payoutReport' <%=payoutReport%>
                                                                                          value="Y">
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                </tr>
                                            </table>
                                        </div>
                                        <div class="reporttable" style="margin-bottom: 9px;">
                                            <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
                                                   style="margin-bottom: 0px">
                                                <tr class="td1">
                                                    <td valign="middle" align="center"  colspan="8"  style="height: 30px; background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                        <center><b>Fraud Mails</b></center>
                                                    </td>
                                                </tr>
                                                </thead>
                                                <%
                                                    String highRiskRefunds="";
                                                    String fraudFailedTxn="";
                                                    String dailyFraudReport="";
                                                    if("Y".equals(temphash.get("highRiskRefunds")))
                                                        highRiskRefunds="checked";
                                                    if("Y".equals(temphash.get("fraudFailedTxn")))
                                                        fraudFailedTxn="checked";
                                                    if("Y".equals(temphash.get("dailyFraudReport")))
                                                        dailyFraudReport="checked";
                                                %>
                                                <tr>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Event Name</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Main Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Sales Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Fraud Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Chargeback Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Refund Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Technical Contact</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Billing Contact</td>
                                                </tr>
                                                <tr>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> High Risk Refunds</td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td align="center" class="tr0"><input type="checkbox" name='highRiskRefunds' <%=highRiskRefunds%>
                                                                                          value="Y">
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                </tr>
                                                <tr>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Fraud Failed Transaction</td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td align="center" class="tr0"><input type="checkbox" name='fraudFailedTxn' <%=fraudFailedTxn%>
                                                                                          value="Y">
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                </tr>
                                                <tr>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Daily Fraud Report</td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td align="center" class="tr0"><input type="checkbox" name='dailyFraudReport' <%=dailyFraudReport%>
                                                                                          value="Y">
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                                                </tr>
                                            </table>
                                        </div>

                                    <br>
                                    <div class="reporttable" style="margin-bottom: 9px;">
                                        <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
                                               style="margin-bottom: 0px">
                                            <tr class="th0">
                                                <td valign="middle" align="center"  colspan="8"  style="height: 30px; background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                    <center><b>Customer Email Notification Settings</b></center>
                                                </td>
                                            </tr>
                                        </table>

                                        <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
                                               style="margin-bottom: 0px">
                                            <tr class="td1">
                                                <td valign="middle" align="center"  colspan="8"  style="height: 30px; background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                    <center><b>Customer Transaction Mails</b></center>
                                                </td>
                                            </tr>
                                            </thead>
                                            <%
                                                String customerTransactionSuccessfulMail="";
                                                String customerTransactionFailMail="";
                                                String customerTransactionPayoutSuccess="";
                                                String customerTransactionPayoutFail="";
                                                if("Y".equals(temphash.get("customerTransactionSuccessfulMail")))
                                                    customerTransactionSuccessfulMail="checked";
                                                if("Y".equals(temphash.get("customerTransactionFailMail")))
                                                    customerTransactionFailMail="checked";
                                                if("Y".equals(temphash.get("customerTransactionPayoutSuccess")))
                                                    customerTransactionPayoutSuccess="checked";
                                                if("Y".equals(temphash.get("customerTransactionPayoutFail")))
                                                    customerTransactionPayoutFail="checked";
                                            %>
                                            <tr>
                                                <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Event Name</td>
                                                <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Customer</td>
                                            </tr>
                                            <tr>
                                                <td style="height: 30px" valign="middle" align="center" class="tr0"> Successful</td>
                                                <td align="center" class="tr0"><input type="checkbox" name='customerTransactionSuccessfulMail' <%=customerTransactionSuccessfulMail%>
                                                                                      value="Y">
                                            </tr>
                                            <tr>
                                                <td style="height: 30px" valign="middle" align="center" class="tr0"> Failed</td>
                                                <td align="center" class="tr0"><input type="checkbox" name='customerTransactionFailMail' <%=customerTransactionFailMail%>
                                                                                      value="Y">
                                            </tr>
                                            <tr>
                                                <td style="height: 30px" valign="middle" align="center" class="tr0"> Payout Successful</td>
                                                <td align="center" class="tr0"><input type="checkbox" name='customerTransactionPayoutSuccess' <%=customerTransactionPayoutSuccess%>
                                                                                      value="Y">
                                            </tr>
                                            <tr>
                                                <td style="height: 30px" valign="middle" align="center" class="tr0"> Payout Fail</td>
                                                <td align="center" class="tr0"><input type="checkbox" name='customerTransactionPayoutFail' <%=customerTransactionPayoutFail%>
                                                                                      value="Y">
                                            </tr>
                                        </table>
                                    </div>
                                    <div class="reporttable" style="margin-bottom: 9px;">
                                        <center>
                                            <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
                                                   style="margin-bottom: 0px">
                                                <tr class="td1">
                                                    <td valign="middle" align="center"  colspan="8"  style="height: 30px; background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                        <center><b>Customer Refund Mails</b></center>
                                                    </td>
                                                </tr>
                                                </thead>
                                                <%
                                                    String customerRefundMail="";
                                                    if("Y".equals(temphash.get("customerRefundMail")))
                                                        customerRefundMail="checked";
                                                %>
                                                <tr>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Event Name</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Customer</td>
                                                </tr>
                                                <tr>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Refunds</td>
                                                    <td align="center" class="tr0"><input type="checkbox" name='customerRefundMail' <%=customerRefundMail%>
                                                                                          value="Y">
                                                </tr>
                                            </table>
                                        </center>
                                    </div>
                                    <div class="reporttable" style="margin-bottom: 9px;">
                                        <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
                                                   style="margin-bottom: 0px">
                                            <tr class="td1">
                                                <td valign="middle" align="center"  colspan="8"  style="height: 30px; background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                        <center><b>Customer Tokenization Mails</b></center>
                                                </td>
                                            </tr>
                                            </thead>
                                                <%
                                                    String customerTokenizationMail="";
                                                    if("Y".equals(temphash.get("customerTokenizationMail")))
                                                        customerTokenizationMail="checked";
                                                %>
                                                <tr>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Event Name</td>
                                                    <td valign="middle" align="center" class="tr1"  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"> Customer</td>
                                                </tr>
                                                <tr>
                                                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Card Registration</td>
                                                    <td align="center" class="tr0"><input type="checkbox" name='customerTokenizationMail' <%=customerTokenizationMail%>
                                                                                          value="Y">
                                                </tr>
                                            </table>
                                        </div>
                                </center>
                                <center>
                                    <button type="submit" value="Save" class="btn btn-default">
                                       <%=merchantmailaccess_Save%>
                                    </button>
                                </center>
                            </div>
                        </div>
                    </div>
                </div>
                <br>
                    <%
                int currentblock = 1;
                try
                {
                    currentblock = Integer.parseInt(request.getParameter("currentblock"));
                }
                catch (Exception ex)
                {
                    currentblock = 1;
                }

            %>
                    <%
                }
                }
                else if (functions.isValueNull(errormsg))
                {
                    out.print("<div class=\"bg-info\" style=\"text-align:center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;"+ errormsg +"</div>");
                }
                else
                {
                    out.println(Functions.NewShowConfirmation1(merchantmailaccess_Sorry, merchantmailaccess_No));
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