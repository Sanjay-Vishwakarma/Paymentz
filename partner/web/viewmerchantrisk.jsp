<%@ page import="com.directi.pg.Functions,com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.math.BigDecimal" %>
<%@ include file="top.jsp" %>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%
    String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
    session.setAttribute("submit","viewmerchantrisk");
    PartnerFunctions partnerFunctions=new PartnerFunctions();

    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);


    String viewmerchantrisk_Merchant_Limit_Management = rb1.getString("viewmerchantrisk_Merchant_Limit_Management");
    String viewmerchantrisk_Partner_ID = rb1.getString("viewmerchantrisk_Partner_ID");
    String viewmerchantrisk_Merchant_ID = rb1.getString("viewmerchantrisk_Merchant_ID");
    String viewmerchantrisk_Search = rb1.getString("viewmerchantrisk_Search");
    String viewmerchantrisk_Report_Table = rb1.getString("viewmerchantrisk_Report_Table");
    String viewmerchantrisk_Limits = rb1.getString("viewmerchantrisk_Limits");
    String viewmerchantrisk_AMOUNTVELOCITY = rb1.getString("viewmerchantrisk_AMOUNTVELOCITY");
    String viewmerchantrisk_Amount_Velocity = rb1.getString("viewmerchantrisk_Amount_Velocity");
    String viewmerchantrisk_Disable = rb1.getString("viewmerchantrisk_Disable");
    String viewmerchantrisk_Enable = rb1.getString("viewmerchantrisk_Enable");
    String viewmerchantrisk_Daily_Amt_Limit = rb1.getString("viewmerchantrisk_Daily_Amt_Limit");
    String viewmerchantrisk_Weekly_Amt_Limit = rb1.getString("viewmerchantrisk_Weekly_Amt_Limit");
    String viewmerchantrisk_Monthly_Amt_Limit = rb1.getString("viewmerchantrisk_Monthly_Amt_Limit");
    String viewmerchantrisk_CARD_VELOCITY = rb1.getString("viewmerchantrisk_CARD_VELOCITY");
    String viewmerchantrisk_Card_Velocity = rb1.getString("viewmerchantrisk_Card_Velocity");
    String viewmerchantrisk_Daily_Card_Limit = rb1.getString("viewmerchantrisk_Daily_Card_Limit");
    String viewmerchantrisk_Weekly_Card_Limit = rb1.getString("viewmerchantrisk_Weekly_Card_Limit");
    String viewmerchantrisk_Monthly_Card_Limit = rb1.getString("viewmerchantrisk_Monthly_Card_Limit");
    String viewmerchantrisk_CARD_VOLUME_VELOCITY = rb1.getString("viewmerchantrisk_CARD_VOLUME_VELOCITY");
    String viewmerchantrisk_Card_Amount_Velocity = rb1.getString("viewmerchantrisk_Card_Amount_Velocity");
    String viewmerchantrisk_Daily_Card_Amt_Limit = rb1.getString("viewmerchantrisk_Daily_Card_Amt_Limit");
    String viewmerchantrisk_Weekly_Card_Amt_Limit = rb1.getString("viewmerchantrisk_Weekly_Card_Amt_Limit");
    String viewmerchantrisk_Monthly_Card_Amt_Limit = rb1.getString("viewmerchantrisk_Monthly_Card_Amt_Limit");
    String viewmerchantrisk_OTHER = rb1.getString("viewmerchantrisk_OTHER");
    String viewmerchantrisk_High_Risk_Amt = rb1.getString("viewmerchantrisk_High_Risk_Amt");
    String viewmerchantrisk_Card_Velocity_Check = rb1.getString("viewmerchantrisk_Card_Velocity_Check");
    String viewmerchantrisk_Limit_Routing = rb1.getString("viewmerchantrisk_Limit_Routing");
    String viewmerchantrisk_Save = rb1.getString("viewmerchantrisk_Save");
    String viewmerchantrisk_Sorry = rb1.getString("viewmerchantrisk_Sorry");
    String viewmerchantrisk_No = rb1.getString("viewmerchantrisk_No");
%>
<%!
    private static Logger log=new Logger("viewmerchantrisk.jsp");
%>
<html>
<head>
    <style type="text/css">
        #maintitle{
            text-align: center;
            background: #7eccad;
            color: #fff;
            font-size: 14px;
        }

        fieldset {
            padding: .35em .625em .75em !IMPORTANT;
            margin: 0px 5px 30px;
            border: 1px solid silver !IMPORTANT;
        }

        .legend-label{
            display: block;
            margin-inline-start: 2px;
            margin-inline-end: 2px;
            padding-block-start: 0.35em;
            padding-inline-start: 0.75em;
            padding-inline-end: 0.75em;
            padding-block-end: 0.625em;
            min-inline-size: min-content;
            border-width: 2px;
            width: auto;
            text-align: left;
            border:none;
            margin-bottom: 5px;
            font-size: 18px;
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

    <script>
        function changeAmtText(dropdown,input1,input2,input3){
            var val = dropdown.options[dropdown.selectedIndex].value;
            if(val.trim()==='0'){

                document.getElementById(input1).disabled = true;
                document.getElementById(input2).disabled = true;
                document.getElementById(input3).disabled = true;
            }else{
                document.getElementById(input1).disabled = false;
                document.getElementById(input2).disabled = false;
                document.getElementById(input3).disabled = false;
            }
        }
    </script>

    <script type="text/javascript">
        function changeVal(dropdown,input1,input2)
        {
            console.log("dropdown"+dropdown+ "input1"+input1 +" input2"+ input2)
            var val = dropdown.options[dropdown.selectedIndex].value;
            console.log("dropdown val+++"+val)
            if(val =='N')
            {
                console.log("inside if")
                document.getElementById(input1).disabled = true;
                document.getElementById(input2).disabled = true;
            }else{
                console.log("inside else")

                document.getElementById(input1).disabled = false;
                document.getElementById(input2).disabled = false;
            }
        }

    </script>
</head>
<title><%=company%> Merchant Settings> Merchant Limit Management </title>
<body class="bodybackground">
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=viewmerchantrisk_Merchant_Limit_Management%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                            if (partner.isLoggedInPartner(session))
                            {
                                //LinkedHashMap memberidDetails=partner.getPartnerMembersDetail((String) session.getAttribute("merchantid"));
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
                        <form action="/partner/net/MerchantRisk?ctoken=<%=ctoken%>" method="get" name="F1" onsubmit="" class="form-horizontal">
                            <input type="hidden" value="<%=ctoken%>" name="ctoken">
                            <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">

                            <div class="widget-content padding">
                                <div id="horizontal-form">
                                    <%
                                        String success = (String)request.getAttribute("success");
                                        String errormsg1 = (String) request.getAttribute("error");
                                        if (partnerFunctions.isValueNull(errormsg1))
                                        {
                                            //out.println("<center><div class=\"textb\" style=\"text-align: center;color: red;\"><b>"+errormsg1+"<br></b></div></center>");
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg1 + "</h5>");
                                        }
                                    %>
                                    <%
                                        String errormsg = (String)request.getAttribute("cbmessage");
                                        if (partnerFunctions.isValueNull(errormsg))
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg + "</h5>");
                                        }
                                    %>
                                    <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                                    <div class="form-group col-md-4">
                                        <label class="col-sm-4 control-label"><%=viewmerchantrisk_Partner_ID%></label>
                                        <div class="col-sm-8">
                                            <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                                            <input name="pid" type="hidden" value="<%=pid%>">
                                        </div>
                                    </div>

                                    <div class="form-group col-md-4">
                                        <label class="col-sm-4 control-label"><%=viewmerchantrisk_Merchant_ID%></label>
                                        <div class="col-sm-8">
                                            <input name="memberid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                                            <%-- <select name="memberid" class="form-control">
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
                                                &nbsp;&nbsp;<%=viewmerchantrisk_Search%></button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
            <form action="/partner/net/SetReservesRiskmgmt?ctoken=<%=ctoken%>" method=post>
                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=viewmerchantrisk_Report_Table%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <div class="widget-content padding" style="overflow-x: auto;">
                                <%
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
                                <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;border:none">
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
                                                // isReadOnly = "";
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
                                    <center>
                                        <div class="widget-content" style="overflow-x: auto;">
                                            <br>
                                            <div class="widget-header transparent" style="padding-left: 15px; padding-right: 15px;">
                                                <h2 id="maintitle"><strong>&nbsp;&nbsp;<%=viewmerchantrisk_Limits%></strong></h2>
                                            </div>
                                            <br>
                                            <fieldset>

                                                <legend class="legend-label"><%=viewmerchantrisk_AMOUNTVELOCITY%></legend>

                                                <div class="form-group col-md-3 has-feedback">
                                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=viewmerchantrisk_Amount_Velocity%></label>
                                                    <select name='check_limit' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold" id="check_limit" onchange="changeAmtText(this,'daily','weekly','monthly')"><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("check_limit"))%>--%>
                                                        <%if(temphash.get("check_limit").equals("0")){  %>
                                                        <option value="<%=temphash.get("check_limit")%>" selected="selected"><%=viewmerchantrisk_Disable%></option>
                                                        <option value="1"><%=viewmerchantrisk_Enable%></option>
                                                        <% }else{%>
                                                        <option value="0"><%=viewmerchantrisk_Disable%></option>
                                                        <option value="<%=temphash.get("check_limit")%>" selected="selected"><%=viewmerchantrisk_Enable%></option>
                                                        <%}%></select>
                                                </div>

                                                <div class="form-group col-md-3 has-feedback">
                                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=viewmerchantrisk_Daily_Amt_Limit%></label>
                                                    <input type=text size=10 class="form-control" name='daily_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_amount_limit"))%>" id="daily"  <%if(temphash.get("check_limit").equals("0")){  %>disabled<%}%>>
                                                    <input type=hidden size=10 class="form-control" name='daily_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_amount_limit"))%>" >
                                                </div>
                                                <div class="form-group col-md-3 has-feedback">
                                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=viewmerchantrisk_Weekly_Amt_Limit%></label>
                                                    <input type=text class="form-control" size=10 name='weekly_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_amount_limit"))%>" id="weekly" <%if(temphash.get("check_limit").equals("0")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='weekly_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_amount_limit"))%>">
                                                </div>
                                                <div class="form-group col-md-3 has-feedback">
                                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=viewmerchantrisk_Monthly_Amt_Limit%></label>
                                                    <input type=text class="form-control" size=10 name='monthly_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_amount_limit"))%>" id="monthly" <%if(temphash.get("check_limit").equals("0")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='monthly_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_amount_limit"))%>" >
                                                </div>
                                            </fieldset>

                                            <fieldset>

                                                <legend class="legend-label"><%=viewmerchantrisk_CARD_VELOCITY%></legend>

                                                <div class="form-group col-md-3 has-feedback">
                                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=viewmerchantrisk_Card_Velocity%></label>
                                                    <select name='card_check_limit' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold" onchange="changeAmtText(this,'daily1','weekly1','monthly1')"><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("check_limit"))%>--%>
                                                        <%if(temphash.get("card_check_limit").equals("0")){  %>
                                                        <option value="<%=temphash.get("card_check_limit")%>" selected="selected"><%=viewmerchantrisk_Disable%></option>
                                                        <option value="1"><%=viewmerchantrisk_Enable%></option>
                                                        <% }else{%>
                                                        <option value="0"><%=viewmerchantrisk_Disable%></option>
                                                        <option value="<%=temphash.get("card_check_limit")%>" selected="selected"><%=viewmerchantrisk_Enable%></option>
                                                        <%}%></select>
                                                </div>
                                                <div class="form-group col-md-3 has-feedback">
                                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=viewmerchantrisk_Daily_Card_Limit%></label>
                                                    <input type=text class="form-control" size=10 name='daily_card_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_card_limit"))%>" id="daily1" <%if(temphash.get("card_check_limit").equals("0")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='daily_card_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_card_limit"))%>">

                                                </div>
                                                <div class="form-group col-md-3 has-feedback">
                                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=viewmerchantrisk_Weekly_Card_Limit%></label>
                                                    <input type=text class="form-control" size=10 name='weekly_card_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_card_limit"))%>" id="weekly1" <%if(temphash.get("card_check_limit").equals("0")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='weekly_card_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_card_limit"))%>">
                                                </div>
                                                <div class="form-group col-md-3 has-feedback">
                                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=viewmerchantrisk_Monthly_Card_Limit%></label>
                                                    <input type=text class="form-control" size=10 name='monthly_card_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_card_limit"))%>" id="monthly1" <%if(temphash.get("card_check_limit").equals("0")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='monthly_card_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_card_limit"))%>">
                                                </div>

                                            </fieldset>

                                            <fieldset>

                                                <legend class="legend-label"><%=viewmerchantrisk_CARD_VOLUME_VELOCITY%></legend>

                                                <div class="form-group col-md-3 has-feedback">
                                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=viewmerchantrisk_Card_Amount_Velocity%></label>
                                                    <select name='card_transaction_limit' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold" onchange="changeAmtText(this,'daily2daily2','weekly2','monthly2')"><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("check_limit"))%>--%>
                                                        <%if(temphash.get("card_transaction_limit").equals("0")){  %>
                                                        <option value="<%=temphash.get("card_transaction_limit")%>" selected="selected"><%=viewmerchantrisk_Disable%></option>
                                                        <option value="1"><%=viewmerchantrisk_Enable%></option>
                                                        <% }else{%>
                                                        <option value="0"><%=viewmerchantrisk_Disable%></option>
                                                        <option value="<%=temphash.get("card_transaction_limit")%>" selected="selected"><%=viewmerchantrisk_Enable%></option>
                                                        <%}%>
                                                    </select>
                                                </div>

                                                <div class="form-group col-md-3 has-feedback">
                                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=viewmerchantrisk_Daily_Card_Amt_Limit%></label>
                                                    <input type=text class="form-control" size=10 name='daily_card_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_card_amount_limit"))%>" id="daily2" <%if(temphash.get("card_transaction_limit").equals("0")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='daily_card_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_card_amount_limit"))%>" >
                                                </div>
                                                <div class="form-group col-md-3 has-feedback">
                                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=viewmerchantrisk_Weekly_Card_Amt_Limit%></label>
                                                    <input type=text class="form-control" size=10 name='weekly_card_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_card_amount_limit"))%>" id="weekly2" <%if(temphash.get("card_transaction_limit").equals("0")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='weekly_card_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_card_amount_limit"))%>">
                                                </div>
                                                <div class="form-group col-md-3 has-feedback">
                                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=viewmerchantrisk_Monthly_Card_Amt_Limit%></label>
                                                    <input type=text class="form-control" size=10 name='monthly_card_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_card_amount_limit"))%>" id="monthly2" <%if(temphash.get("card_transaction_limit").equals("0")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='monthly_card_amount_limit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_card_amount_limit"))%>">
                                                </div>

                                            </fieldset>


                                            <fieldset>
                                                <legend class="legend-label"> VPA Address</legend>
                                                <div class="ui-widget form-group col-md-4 has-feedback">
                                                    <label>VPA Address Limit Check </label>
                                                    <select name='vpaAddressLimitCheck'  class="form-control" style="border: 1px solid #b2b2b2;" onchange="changeVal(this,'vpa1','vpa2')"><%--onchange="ChangeFunction(this.value,'VPA Address Limit Check')">--%>
                                                        <option value="N" <%="N".equals(temphash.get("vpaAddressLimitCheck"))?"selected":""%>>N</option>
                                                        <option value="System" <%="System".equals(temphash.get("vpaAddressLimitCheck"))?"selected":""%>>System</option>
                                                        <option value="Member" <%="Member".equals(temphash.get("vpaAddressLimitCheck"))?"selected":""%>>Member</option>
                                                    </select>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label>VPA Address Daily Count</label>
                                                    <input type=text class="form-control"  name='vpaAddressDailyCount' style="border: 1px solid #b2b2b2;" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressDailyCount"))%>" id="vpa1" <%if(temphash.get("vpaAddressLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='vpaAddressDailyCount' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressDailyCount"))%>">

                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label>VPA Address Monthly Count</label>
                                                    <input type=text  class="form-control"  name='vpaAddressMonthlyCount' style="border: 1px solid #b2b2b2;" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressMonthlyCount"))%>" id="vpa2" <%if(temphash.get("vpaAddressLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='vpaAddressMonthlyCount' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressMonthlyCount"))%>">

                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label>VPA Address Amount Limit Check</label>
                                                    <select name='vpaAddressAmountLimitCheck' class="form-control" style="border: 1px solid #b2b2b2;" onchange="changeVal(this,'vpam1','vpam2')"> <%--onchange="ChangeFunction(this.value,'VPA Address Amount Limit Check')">--%>
                                                        <option value="N" <%="N".equals(temphash.get("vpaAddressAmountLimitCheck"))?"selected":""%>>N</option>
                                                        <option value="System" <%="System".equals(temphash.get("vpaAddressAmountLimitCheck"))?"selected":""%>>System</option>
                                                        <option value="Member" <%="Member".equals(temphash.get("vpaAddressAmountLimitCheck"))?"selected":""%>>Member</option>
                                                    </select>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label>VPA Address Daily Amount Limit</label>
                                                    <input type=text class="form-control"  name='vpaAddressDailyAmountLimit' style="border: 1px solid #b2b2b2;" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressDailyAmountLimit"))%>" id="vpam1" <%if(temphash.get("vpaAddressAmountLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='vpaAddressDailyAmountLimit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressDailyAmountLimit"))%>">

                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label>VPA Address Monthly Amount Limit</label>
                                                    <input type=text class="form-control"  name='vpaAddressMonthlyAmountLimit' style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressMonthlyAmountLimit"))%>" id="vpam2" <%if(temphash.get("vpaAddressAmountLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='vpaAddressMonthlyAmountLimit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressMonthlyAmountLimit"))%>">

                                                </div>
                                            </fieldset>
                                            <fieldset>
                                                <legend class="legend-label">Customer Ip Transaction Limits</legend>
                                                <div class="ui-widget form-group col-md-4 has-feedback">
                                                    <label> Customer Ip Count Limit Check</label>
                                                    <select name='customerIpLimitCheck' class="form-control" style="border: 1px solid #b2b2b2;" onchange="changeVal(this,'cip1','cip2')">
                                                        <option value="N" <%="N".equals(temphash.get("customerIpLimitCheck"))?"selected":""%>>N</option>
                                                        <option value="System" <%="System".equals(temphash.get("customerIpLimitCheck"))?"selected":""%>>System</option>
                                                        <option value="Member" <%="Member".equals(temphash.get("customerIpLimitCheck"))?"selected":""%>>Member</option>
                                                    </select>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Ip Daily Count</label>
                                                    <input type=text  class="form-control"  name='customerIpDailyCount' style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpDailyCount"))%>" id="cip1" <%if(temphash.get("customerIpLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='customerIpDailyCount' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpDailyCount"))%>">

                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Ip Monthly Count</label>
                                                    <input type=text  class="form-control"  name='customerIpMonthlyCount' style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpMonthlyCount"))%>" id="cip2" <%if(temphash.get("customerIpLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='customerIpMonthlyCount' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpMonthlyCount"))%>">

                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Ip Amount Limit Check</label>
                                                    <select name='customerIpAmountLimitCheck' class="form-control" style="border: 1px solid #b2b2b2;" onchange="changeVal(this,'ciplc1','ciplc2')">
                                                        <option value="N" <%="N".equals(temphash.get("customerIpAmountLimitCheck"))?"selected":""%>>N</option>
                                                        <option value="System" <%="System".equals(temphash.get("customerIpAmountLimitCheck"))?"selected":""%>>System</option>
                                                        <option value="Member" <%="Member".equals(temphash.get("customerIpAmountLimitCheck"))?"selected":""%>>Member</option>
                                                    </select>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Ip Daily Amount Limit</label>
                                                    <input type=text  class="form-control" name='customerIpDailyAmountLimit' style="border: 1px solid #b2b2b2;" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpDailyAmountLimit"))%>" id="ciplc1" <%if(temphash.get("customerIpAmountLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='customerIpDailyAmountLimit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpDailyAmountLimit"))%>">

                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Ip Monthly Amount Limit</label>
                                                    <input type=text class="form-control" name='customerIpMonthlyAmountLimit' style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpMonthlyAmountLimit"))%>" id="ciplc2" <%if(temphash.get("customerIpAmountLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='customerIpMonthlyAmountLimit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpMonthlyAmountLimit"))%>">

                                                </div>
                                            </fieldset>
                                            <fieldset>
                                                <legend class="legend-label"> Customer Name Transaction Limits</legend>
                                                <div class="ui-widget form-group col-md-4 has-feedback">
                                                    <label> Customer Name Count Limit Check</label>
                                                    <select name='customerNameLimitCheck' class="form-control" style="border: 1px solid #b2b2b2;" onchange="changeVal(this,'cnl','cn2')">
                                                        <option value="N" <%="N".equals(temphash.get("customerNameLimitCheck"))?"selected":""%>>N</option>
                                                        <option value="System" <%="System".equals(temphash.get("customerNameLimitCheck"))?"selected":""%>>System</option>
                                                        <option value="Member" <%="Member".equals(temphash.get("customerNameLimitCheck"))?"selected":""%>>Member</option>
                                                    </select>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Name Daily Count</label>
                                                    <input type=text  class="form-control" name='customerNameDailyCount' style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameDailyCount"))%>" id="cnl" <%if(temphash.get("customerNameLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='customerNameDailyCount' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameDailyCount"))%>">

                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Name Monthly Count</label>
                                                    <input type=text  class="form-control" name='customerNameMonthlyCount' style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameMonthlyCount"))%>" id="cn2" <%if(temphash.get("customerNameLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='customerNameMonthlyCount' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameMonthlyCount"))%>">

                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Name Amount Limit Check</label>
                                                    <select name='customerNameAmountLimitCheck' class="form-control" style="border: 1px solid #b2b2b2;" onchange="changeVal(this,'cndl','cnd2')">
                                                        <option value="N" <%="N".equals(temphash.get("customerNameAmountLimitCheck"))?"selected":""%>>N</option>
                                                        <option value="System" <%="System".equals(temphash.get("customerNameAmountLimitCheck"))?"selected":""%>>System</option>
                                                        <option value="Member" <%="Member".equals(temphash.get("customerNameAmountLimitCheck"))?"selected":""%>>Member</option>
                                                    </select>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Name Daily Amount Limit</label>
                                                    <input type=text  name='customerNameDailyAmountLimit' class="form-control" style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameDailyAmountLimit"))%>" id="cndl" <%if(temphash.get("customerNameAmountLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='customerNameDailyAmountLimit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameDailyAmountLimit"))%>">

                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Name Monthly Amount Limit</label>
                                                    <input type=text name='customerNameMonthlyAmountLimit' class="form-control" style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameMonthlyAmountLimit"))%>" id="cnd2" <%if(temphash.get("customerNameAmountLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='customerNameMonthlyAmountLimit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameMonthlyAmountLimit"))%>">

                                                </div>
                                            </fieldset>
                                            <fieldset>
                                                <legend class="legend-label"> Customer Email Transaction Limits</legend>
                                                <div class="ui-widget form-group col-md-4 has-feedback">
                                                    <label> Customer Email Count Limit Check</label>
                                                    <select name='customerEmailLimitCheck' class="form-control" style="border: 1px solid #b2b2b2;" onchange="changeVal(this,'celt','celt2')">
                                                        <option value="N" <%="N".equals(temphash.get("customerEmailLimitCheck"))?"selected":""%>>N</option>
                                                        <option value="System" <%="System".equals(temphash.get("customerEmailLimitCheck"))?"selected":""%>>System</option>
                                                        <option value="Member" <%="Member".equals(temphash.get("customerEmailLimitCheck"))?"selected":""%>>Member</option>
                                                    </select>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Email Daily Count</label>
                                                    <input type=text  name='customerEmailDailyCount' class="form-control" style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailDailyCount"))%>" id="celt" <%if(temphash.get("customerEmailLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='customerEmailDailyCount' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailDailyCount"))%>">

                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Email Monthly Count</label>
                                                    <input type=text  name='customerEmailMonthlyCount' class="form-control" style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailMonthlyCount"))%>" id="celt2" <%if(temphash.get("customerEmailLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='customerEmailMonthlyCount' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailMonthlyCount"))%>">

                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Email Amount Limit Check</label>
                                                    <select name='customerEmailAmountLimitCheck' class="form-control" style="border: 1px solid #b2b2b2;" onchange="changeVal(this,'celc1','celc2')">
                                                        <option value="N" <%="N".equals(temphash.get("customerEmailAmountLimitCheck"))?"selected":""%>>N</option>
                                                        <option value="System" <%="System".equals(temphash.get("customerEmailAmountLimitCheck"))?"selected":""%>>System</option>
                                                        <option value="Member" <%="Member".equals(temphash.get("customerEmailAmountLimitCheck"))?"selected":""%>>Member</option>
                                                    </select>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Email Daily Amount Limit</label>
                                                    <input type=text  name='customerEmailDailyAmountLimit' class="form-control" style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailDailyAmountLimit"))%>" id="celc1" <%if(temphash.get("customerEmailAmountLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='customerEmailDailyAmountLimit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailDailyAmountLimit"))%>">

                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Email Monthly Amount Limit</label>
                                                    <input type=text  name='customerEmailMonthlyAmountLimit' class="form-control" style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailMonthlyAmountLimit"))%>" id="celc2" <%if(temphash.get("customerEmailAmountLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='customerEmailMonthlyAmountLimit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailMonthlyAmountLimit"))%>">

                                                </div>
                                            </fieldset>
                                            <fieldset>
                                                <legend class="legend-label"> Customer Phone Transaction Limits</legend>
                                                <div class="ui-widget form-group col-md-4 has-feedback">
                                                    <label> Customer Phone Count Limit Check</label>
                                                    <select name='customerPhoneLimitCheck' class="form-control" style="border: 1px solid #b2b2b2;" onchange="changeVal(this,'cpt1','cpt2')">
                                                        <option value="N" <%="N".equals(temphash.get("customerPhoneLimitCheck"))?"selected":""%>>N</option>
                                                        <option value="System" <%="System".equals(temphash.get("customerPhoneLimitCheck"))?"selected":""%>>System</option>
                                                        <option value="Member" <%="Member".equals(temphash.get("customerPhoneLimitCheck"))?"selected":""%>>Member</option>
                                                    </select>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Phone Daily Count</label>
                                                    <input type=text  name='customerPhoneDailyCount' class="form-control" style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneDailyCount"))%>" id="cpt1" <%if(temphash.get("customerPhoneLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='customerPhoneDailyCount' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneDailyCount"))%>">

                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Phone Monthly Count</label>
                                                    <input type=text  name='customerPhoneMonthlyCount' class="form-control" style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneMonthlyCount"))%>" id="cpt2" <%if(temphash.get("customerPhoneLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='customerPhoneMonthlyCount' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneMonthlyCount"))%>">

                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Phone Amount Limit Check</label>
                                                    <select name='customerPhoneAmountLimitCheck' class="form-control" style="border: 1px solid #b2b2b2;" onchange="changeVal(this,'cpa1','cpa2')">
                                                        <option value="N" <%="N".equals(temphash.get("customerPhoneAmountLimitCheck"))?"selected":""%>>N</option>
                                                        <option value="System" <%="System".equals(temphash.get("customerPhoneAmountLimitCheck"))?"selected":""%>>System</option>
                                                        <option value="Member" <%="Member".equals(temphash.get("customerPhoneAmountLimitCheck"))?"selected":""%>>Member</option>
                                                    </select>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Phone Daily Amount Limit</label>
                                                    <input type=text  name='customerPhoneDailyAmountLimit' class="form-control" style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneDailyAmountLimit"))%>" id="cpa1" <%if(temphash.get("customerPhoneAmountLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='customerPhoneDailyAmountLimit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneDailyAmountLimit"))%>">

                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Customer Phone Monthly Amount Limit</label>
                                                    <input type=text  name='customerPhoneMonthlyAmountLimit' class="form-control" style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneMonthlyAmountLimit"))%>" id="cpa2" <%if(temphash.get("customerPhoneAmountLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='customerPhoneMonthlyAmountLimit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneMonthlyAmountLimit"))%>">

                                                </div>
                                            </fieldset>
                                            <fieldset>
                                                <legend class="legend-label"> Payout</legend>
                                                <div class="ui-widget form-group col-md-4 has-feedback">
                                                    <label> Payout Bank AccountNo Limit Check</label>
                                                    <select name='payoutBankAccountNoLimitCheck' class="form-control" style="border: 1px solid #b2b2b2;" onchange="changeVal(this,'pba1','pba2')">
                                                        <option value="N" <%="N".equals(temphash.get("payoutBankAccountNoLimitCheck"))?"selected":""%>>N</option>
                                                        <option value="System" <%="System".equals(temphash.get("payoutBankAccountNoLimitCheck"))?"selected":""%>>System</option>
                                                        <option value="Member" <%="Member".equals(temphash.get("payoutBankAccountNoLimitCheck"))?"selected":""%>>Member</option>
                                                    </select>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Bank AccountNo Daily Count</label>
                                                    <input type=text  class="form-control"  name='bankAccountNoDailyCount' style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoDailyCount"))%>" id="pba1" <%if(temphash.get("payoutBankAccountNoLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='bankAccountNoDailyCount' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoDailyCount"))%>">

                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Bank AccountNo Monthly Count</label>
                                                    <input type=text  class="form-control"  name='bankAccountNoMonthlyCount' style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoMonthlyCount"))%>"  id="pba2" <%if(temphash.get("payoutBankAccountNoLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='bankAccountNoMonthlyCount' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoMonthlyCount"))%>">

                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label>Payout Bank AccountNo Amount Limit Check</label>
                                                    <select name='payoutBankAccountNoAmountLimitCheck' class="form-control" style="border: 1px solid #b2b2b2;" onchange="changeVal(this,'pbam1','pbam2')">
                                                        <option value="N" <%="N".equals(temphash.get("customerPhoneAmountLimitCheck"))?"selected":""%>>N</option>
                                                        <option value="System" <%="System".equals(temphash.get("payoutBankAccountNoAmountLimitCheck"))?"selected":""%>>System</option>
                                                        <option value="Member" <%="Member".equals(temphash.get("payoutBankAccountNoAmountLimitCheck"))?"selected":""%>>Member</option>
                                                    </select>
                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Bank AccountNo Daily Amount Limit</label>
                                                    <input type=text  class="form-control"  name='bankAccountNoDailyAmountLimit' style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoDailyAmountLimit"))%>" id="pbam1" <%if(temphash.get("payoutBankAccountNoAmountLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='bankAccountNoDailyAmountLimit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoDailyAmountLimit"))%>">

                                                </div>
                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Bank AccountNo Monthly Amount Limit</label>
                                                    <input type=text  class="form-control"  name='bankAccountNoMonthlyAmountLimit' style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoMonthlyAmountLimit"))%>" id="pbam2" <%if(temphash.get("payoutBankAccountNoAmountLimitCheck").equals("N")){  %>disabled<%}%>>
                                                    <input type=hidden class="form-control" size=10 name='bankAccountNoMonthlyAmountLimit' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoMonthlyAmountLimit"))%>">

                                                </div>

                                                <div class="form-group col-md-4 has-feedback">
                                                    <label> Total Payout Amount</label>
                                                    <input type=number step=".01" class="form-control"  name='totalPayoutAmount' style="border: 1px solid #b2b2b2;"
                                                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("totalPayoutAmount"))%>" id="totalPayoutAmount">
                                                    <input type=hidden class="form-control" name='totalPayoutAmount' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("totalPayoutAmount"))%>">

                                                </div>

                                            </fieldset>
                                            <fieldset>

                                                <legend class="legend-label"> <%=viewmerchantrisk_OTHER%></legend>

                                                <div class="form-group col-md-3 has-feedback">
                                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=viewmerchantrisk_High_Risk_Amt%></label>
                                                    <input type=text class="form-control" size=10 name='aptprompt' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute(aptprompt)%>">
                                                </div>
                                                <div class="form-group col-md-3 has-feedback">
                                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=viewmerchantrisk_Card_Velocity_Check%></label>
                                                    <select name='card_velocity_check' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold">
                                                        <%if(temphash.get("card_velocity_check").equals("N")){  %>
                                                        <option value="<%=temphash.get("card_velocity_check")%>" selected="selected"><%=viewmerchantrisk_Disable%></option>
                                                        <option value="Y"><%=viewmerchantrisk_Enable%></option>
                                                        <% }else{%>
                                                        <option value="N"><%=viewmerchantrisk_Disable%></option>
                                                        <option value="<%=temphash.get("card_velocity_check")%>" selected="selected"><%=viewmerchantrisk_Enable%></option>
                                                        <%}%>
                                                    </select>
                                                </div>
                                                <div class="form-group col-md-3 has-feedback">
                                                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=viewmerchantrisk_Limit_Routing%></label>
                                                    <select name='limitRouting' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold">
                                                        <%if(temphash.get("limitRouting").equals("N")){  %>
                                                        <option value="<%=temphash.get("limitRouting")%>" selected="selected"><%=viewmerchantrisk_Disable%></option>
                                                        <option value="Y"><%=viewmerchantrisk_Enable%></option>
                                                        <% }else{%>
                                                        <option value="N"><%=viewmerchantrisk_Disable%></option>
                                                        <option value="<%=temphash.get("limitRouting")%>" selected="selected"><%=viewmerchantrisk_Enable%></option>
                                                        <%}%>
                                                    </select>
                                                </div>

                                            </fieldset>

                                        </div>
                                    </center>
                                </table>
                                <center>
                                <button type="submit" value="Save" class="btn btn-default">
                                    <%=viewmerchantrisk_Save%>
                                </button>
                            </center>
                            </div>
                        </div>
                    </div>
                </div>
                <%
                    }//end for
                %>
            </form>
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
                else if (functions.isValueNull(success))
                {
                    out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + success + "</h5>");
                }
                else
                {
                    out.println(Functions.NewShowConfirmation1(viewmerchantrisk_Sorry,viewmerchantrisk_No));
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
        response.sendRedirect("/partner/logout.jsp");
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