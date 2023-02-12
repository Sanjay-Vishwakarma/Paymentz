<%@ include file="top.jsp" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.PartnerManager" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="java.util.TreeMap" %>
<%
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (partner.isLoggedInPartner(session))
    {
        String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
        String partnerId = (String) session.getAttribute("merchantid");

        session.setAttribute("submit", "List Settlement Cycle");
        String accountId1="";
        LinkedHashMap memberidDetails=null;
        Functions function = new Functions();
        String partnerid =nullToStr(request.getParameter("partnerid"));
        String partner_id = session.getAttribute("merchantid").toString();
        if(function.isValueNull(partnerid)){
            memberidDetails=partner.getPartnerMembersDetail(partnerid);
        }
        else{
            memberidDetails = partner.getSuperPartnerMembersDetail(partner_id);
        }

        String fromDate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? "" : request.getParameter("fromdate");
        String toDate = Functions.checkStringNull(request.getParameter("todate")) == null ? "" : request.getParameter("todate");
        String reqAccountId = Functions.checkStringNull(request.getParameter("reqaccountid")) == null ? "" : request.getParameter("reqaccountid");
        String reqPartnerId = Functions.checkStringNull(request.getParameter("partnerid")) == null ? "" : request.getParameter("partnerid");
        /*String settlementStatus = Functions.checkStringNull(request.getParameter("status")) == null ? "" : request.getParameter("status");*/
        String status = request.getParameter("status") == null ? "" : request.getParameter("status");
        PartnerManager partnerManager = new PartnerManager();
        TreeMap<Integer, String> integerStringTreeMap = partnerManager.loadGatewayAccounts(partnerId, "");
        PartnerFunctions partnerFunctions=new PartnerFunctions();
        TreeMap<String, String> subPartnersDetails = partnerFunctions.getPartnerDetailsForUI(partnerId);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        int pageno = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
        int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
        String roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));

        String errormsg2 = (String) request.getAttribute("error");
        String msg = (String) request.getAttribute("message");

        String str = "ctoken=" + ctoken;

        if (fromDate != null) str = str + "&fromdate=" + fromDate;
        if (toDate != null) str = str + "&todate=" + toDate;
        if (reqAccountId != null) str = str + "&reqaccountid=" + reqAccountId;
        if (reqPartnerId != null) str = str + "&partnerid=" + reqPartnerId;
        if (status != null) str = str + "&status=" + status;

        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String listSettlementCycle_Settlement_Cycle_Master = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_Settlement_Cycle_Master")) ? rb1.getString("listSettlementCycle_Settlement_Cycle_Master") : "Settlement Cycle Master";
        String listSettlementCycle_From = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_From")) ? rb1.getString("listSettlementCycle_From") : "From";
        String listSettlementCycle_To = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_To")) ? rb1.getString("listSettlementCycle_To") : "To";
        String listSettlementCycle_AccountId = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_AccountId")) ? rb1.getString("listSettlementCycle_AccountId") : "Account Id";
        String listSettlementCycle_Partner_Id = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_Partner_Id")) ? rb1.getString("listSettlementCycle_Partner_Id") : "Partner Id";
        String listSettlementCycle_Select_PartnerId = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_Select_PartnerId")) ? rb1.getString("listSettlementCycle_Select_PartnerId") : "Select Partner Id";
        String listSettlementCycle_Status = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_Status")) ? rb1.getString("listSettlementCycle_Status") : "Status";
        String listSettlementCycle_All = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_All")) ? rb1.getString("listSettlementCycle_All") : "All";
        String listSettlementCycle_Initiated = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_Initiated")) ? rb1.getString("listSettlementCycle_Initiated") : "Initiated";
        String listSettlementCycle_BankWireGenerated = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_BankWireGenerated")) ? rb1.getString("listSettlementCycle_BankWireGenerated") : "BankWireGenerated";
        String listSettlementCycle_SettlementUploaded = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_SettlementUploaded")) ? rb1.getString("listSettlementCycle_SettlementUploaded") : "SettlementUploaded";
        String listSettlementCycle_MerchantWireGenerated = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_MerchantWireGenerated")) ? rb1.getString("listSettlementCycle_MerchantWireGenerated") : "MerchantWireGenerated";
        String listSettlementCycle_Completed = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_Completed")) ? rb1.getString("listSettlementCycle_Completed") : "Completed";
        String listSettlementCycle_Path = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_Path")) ? rb1.getString("listSettlementCycle_Path") : "Path";
        String listSettlementCycle_Search = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_Search")) ? rb1.getString("listSettlementCycle_Search") : "Search";
        String listSettlementCycle_Report_Table = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_Report_Table")) ? rb1.getString("listSettlementCycle_Report_Table") : "Report Table";
        String listSettlementCycle_SrNo = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_SrNo")) ? rb1.getString("listSettlementCycle_SrNo") : "Sr No";
        String listSettlementCycle_Start_Date = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_Start_Date")) ? rb1.getString("listSettlementCycle_Start_Date") : "Start Date";
        String listSettlementCycle_End_Date = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_End_Date")) ? rb1.getString("listSettlementCycle_End_Date") : "End Date";
        String listSettlementCycle_AccountId1 = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_AccountId1")) ? rb1.getString("listSettlementCycle_AccountId1") : "AccountId";
        String listSettlementCycle_Creation_Time = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_Creation_Time")) ? rb1.getString("listSettlementCycle_Creation_Time") : "Creation Time";
        String listSettlementCycle_Action = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_Action")) ? rb1.getString("listSettlementCycle_Action") : "Action";
        String listSettlementCycle_Showing_Page = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_Showing_Page")) ? rb1.getString("listSettlementCycle_Showing_Page") : "Showing Page";
        String listSettlementCycle_of = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_of")) ? rb1.getString("listSettlementCycle_of") : "of";
        String listSettlementCycle_records = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_records")) ? rb1.getString("listSettlementCycle_records") : "records";
        String listSettlementCycle_Sorry = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_Sorry")) ? rb1.getString("listSettlementCycle_Sorry") : "Sorry";
        String listSettlementCycle_no = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_no")) ? rb1.getString("listSettlementCycle_no") : "No Records Found.";
        String listSettlementCycle_page_no = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_page_no")) ? rb1.getString("listSettlementCycle_page_no") : "Page number";
        String listSettlementCycle_total_no_of_records = StringUtils.isNotEmpty(rb1.getString("listSettlementCycle_total_no_of_records")) ? rb1.getString("listSettlementCycle_total_no_of_records") : "Total number of records";

%>
<%!
    private static Logger log = new Logger("partnerMerchantCharges.jsp");
%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
    <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
    <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
    <title><%=company%> Settlement Management> List Settlement Cycle </title>
    <script type="text/javascript">
        $('#sandbox-container input').datepicker({});
    </script>
    <script>
        $(function ()
        {
            $(".datepicker").datepicker({dateFormat: "yy-mm-dd"});
        });
    </script>
</head>
<title><%=company%> | Merchant Settlement Cycle Master</title>

<body>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="pull-right">
                <div class="btn-group">
                    <form action="/partner/addSettlementCycle.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" name="submit" value="Add Settlement Cycle" class="btn btn-default"
                                id="submit" style="display: -webkit-box;"><i class="fa fa-plus"></i>&nbsp;&nbsp;Add New
                            Settlement Cycle
                        </button>
                    </form>
                </div>
            </div>
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> <%=listSettlementCycle_Settlement_Cycle_Master%>
                                </strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding">
                            <div id="horizontal-form1">
                                <%
                                    if (partner.isValueNull(msg))
                                    {
                                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + msg + "</h5>");
                                    }
                                    if (partner.isValueNull(errormsg2))
                                    {
                                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormsg2 + "</h5>");
                                    }
                                %>
                                <form action="/partner/net/ListSettlementCycle?ctoken=<%=ctoken%>" method="get"
                                      name="f1" onsubmit="">
                                    <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                    <input type="hidden" value="<%=(String)session.getAttribute("merchantid")%>"
                                           id="partnerid" name="partnerId">

                                    <div class="widget-content padding">
                                        <div id="horizontal-form2">
                                            <div class="form-group col-md-4 has-feedback">
                                                <label><%=listSettlementCycle_From%></label>
                                                <input type="text" size="16" name="fromdate"
                                                       class="datepicker form-control"
                                                       value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromDate)%>"
                                                       readonly="readonly"
                                                       style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                            </div>
                                            <div class="form-group col-md-4 has-feedback">
                                                <label><%=listSettlementCycle_To%></label>
                                                <input type="text" size="16" name="todate"
                                                       class="datepicker form-control"
                                                       value="<%=ESAPI.encoder().encodeForHTMLAttribute(toDate)%>"
                                                       readonly="readonly"
                                                       style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                            </div>
                                          <%--  <div class="form-group col-md-4 has-feedback">
                                                <label><%=listSettlementCycle_AccountId%></label>
                                                <select name="reqaccountid" class="form-control">
                                                    <option value=""></option>
                                                    <%
                                                        for (Integer accountId : integerStringTreeMap.keySet())
                                                        {
                                                            String isSelected = "";
                                                            if (String.valueOf(accountId).equals(reqAccountId))
                                                            {
                                                                isSelected = "selected";
                                                            }
                                                    %>
                                                    <option value="<%=accountId%>" <%=isSelected%>><%=accountId%>
                                                    </option>
                                                    <%
                                                        }
                                                    %>
                                                </select>
                                            </div>--%>

                                            <div class="form-group col-md-4 has-feedback">
                                                <label ><%=listSettlementCycle_AccountId%></label>
                                                <input type="text" name="reqaccountid" id="accountid" class="form-control" value="<%=reqAccountId%>" autocomplete="on">
                                            </div>


                                            <%
                                                if(roles.contains("superpartner"))
                                                {
                                            %>

                                            <%--<div class="form-group col-md-4 has-feedback">
                                            <label class="col-sm-4 control-label"><%=listSettlementCycle_Partner_Id%></label>
                                            <div class="col-sm-8">
                                                <select class="form-control" name="partnerid" id="pid">
                                                    <option value="" default><%=listSettlementCycle_Select_PartnerId%></option>
                                                    <%
                                                        String Selected = "";
                                                        for(String pid : subPartnersDetails.keySet())
                                                        {
                                                            if(pid.toString().equals(partnerid))
                                                            {
                                                                Selected="selected";
                                                            }
                                                            else
                                                            {
                                                                Selected="";
                                                            }
                                                    %>
                                                    <option value="<%=pid%>" <%=Selected%>><%=subPartnersDetails.get(pid)%></option>
                                                    <%
                                                        }
                                                    %>
                                                </select>
                                            </div>
                                                </div>--%>


                                            <div class="form-group col-md-4 has-feedback">
                                                <label><%=listSettlementCycle_Partner_Id%></label>
                                                <select name="partnerid" class="form-control" id="pid">
                                                    <option value=""><%=listSettlementCycle_Select_PartnerId%></option>
                                                    <%
                                                        for(String pid : subPartnersDetails.keySet())
                                                        {
                                                            String isSelected = "";
                                                            if (pid.equals(reqPartnerId))
                                                            {
                                                                isSelected = "selected";
                                                            }
                                                    %>
                                                    <option value="<%=pid%>" <%=isSelected%>><%=subPartnersDetails.get(pid)%></option>                                                    </option>
                                                    <%
                                                        }
                                                    %>
                                                </select>
                                            </div>
                                            <%
                                                }
                                            %>
                                            <div class="form-group col-md-4 has-feedback">
                                                <label><%=listSettlementCycle_Status%></label>
                                                <select id = "status" size="1" name="status" class="form-control">
                                                    <option value=""><%=listSettlementCycle_All%></option>
                                                    <option value="Initiated"><%=listSettlementCycle_Initiated%></option>
                                                    <option value="BankWireGenerated"><%=listSettlementCycle_BankWireGenerated%></option>
                                                    <option value="SettlementUploaded"><%=listSettlementCycle_SettlementUploaded%></option>
                                                    <option value="MerchantWireGenerated"><%=listSettlementCycle_MerchantWireGenerated%></option>
                                                    <option value="Completed"><%=listSettlementCycle_Completed%></option>
                                                </select>
                                            </div>
                                            <div class="form-group col-md-4">
                                                <label style="color: transparent;"><%=listSettlementCycle_Path%></label>
                                                <button type="submit" class="btn btn-default" style="display:block;">
                                                    <i class="fa fa-clock-o"></i>
                                                    &nbsp;&nbsp;<%=listSettlementCycle_Search%>
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="row reporttable">
                        <div class="col-md-12">
                            <div class="widget">
                                <div class="widget-header">
                                    <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=listSettlementCycle_Report_Table%></strong></h2>

                                    <div class="additional-btn">
                                        <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                        <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                        <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                    </div>
                                </div>
                                <div class="widget-content padding" style="overflow-x: auto;">
                                    <%
                                        Hashtable hash = (Hashtable) request.getAttribute("transdetails");
                                        Hashtable tempHash = null;

                                        String error = (String) request.getAttribute("errormessage");
                                        if (error != null)
                                        {
                                            out.println(error);
                                        }
                                        if (hash != null && hash.size() > 0)
                                        {
                                            int records = 0;
                                            int totalRecords = 0;
                                            String currentBlock = request.getParameter("currentblock");

                                            if (currentBlock == null)
                                                currentBlock = "1";

                                            try
                                            {
                                                records = Functions.convertStringtoInt((String) hash.get("records"), 15);
                                                totalRecords = Functions.convertStringtoInt((String) hash.get("totalrecords"), 0);
                                            }
                                            catch (NumberFormatException ex)
                                            {
                                                log.error("Records & TotalRecords is found null", ex);
                                            }
                                            if (hash != null)
                                            {
                                                hash = (Hashtable) request.getAttribute("transdetails");
                                            }
                                            if (records > 0)
                                            {
                                    %>
                                    <table align=center width="50%"
                                           class="display table table table-striped table-bordered table-hover dataTable"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <thead>
                                        <tr>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=listSettlementCycle_SrNo%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=listSettlementCycle_Start_Date%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=listSettlementCycle_End_Date%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=listSettlementCycle_AccountId1%></b></td>
                                            <%
                                                if(roles.contains("superpartner")){
                                            %>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=listSettlementCycle_Partner_Id%></b></td>
                                            <%
                                                }
                                            %>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=listSettlementCycle_Status%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=listSettlementCycle_Creation_Time%></b></td>
                                            <td valign="middle" align="center"
                                                style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                                                <b><%=listSettlementCycle_Action%></b></td>
                                        </tr>
                                        </thead>
                                        <%
                                            String style = "class=td1";
                                            String ext = "light";
                                            for (int pos = 1; pos <= records; pos++)
                                            {
                                                String id = Integer.toString(pos);
                                                int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);
                                                if (pos % 2 == 0)
                                                {
                                                    style = "class=tr0";
                                                    ext = "dark";
                                                }
                                                else
                                                {
                                                    style = "class=tr1";
                                                    ext = "light";
                                                }

                                                tempHash = (Hashtable) hash.get(id);

                                                String settlementCycleId = (String) tempHash.get("id");
                                                String startDate = simpleDateFormat.format(simpleDateFormat.parse((String) tempHash.get("startdate")));
                                                String endDate = simpleDateFormat.format(simpleDateFormat.parse((String) tempHash.get("enddate")));
                                                String accountId = (String) tempHash.get("accountid");
                                                String partnerId1 = (String) tempHash.get("partnerid");
                                                String executed = (String) tempHash.get("status");
                                                String createdTime = simpleDateFormat.format(simpleDateFormat.parse((String) tempHash.get("creationon")));
                                                String settlementProcess = "";
                                                String actionServlet = "";
                                                if ("Completed".equalsIgnoreCase(executed) || "MerchantWireGenerated".equalsIgnoreCase(executed))
                                                {
                                                    settlementProcess = "disabled";
                                                }
                                                else if ("Initiated".equalsIgnoreCase(executed))
                                                {
                                                    actionServlet = "AddSettlementCycle";
                                                }
                                                else if ("BankWireGenerated".equalsIgnoreCase(executed))
                                                {
                                                    actionServlet = "AddBankWireAndDoStep1Recon";
                                                }
                                                else if ("SettlementUploaded".equalsIgnoreCase(executed))
                                                {
                                                    actionServlet = "ProcessSettlement";
                                                }

                                                out.println("<tr>");
                                                out.println("<td valign=\"middle\" data-label=\"Sr No\" align=\"center\" " + style + ">&nbsp;" + srno + "</td>");
                                                out.println("<td valign=\"middle\" data-label=\"Start Date\" align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(startDate) + "</td>");
                                                out.println("<td valign=\"middle\" data-label=\"End Date\" align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(endDate) + "</td>");
                                                out.println("<td valign=\"middle\" data-label=\"Account Id\" align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(accountId) + "</td>");
                                                if(roles.contains("superpartner"))
                                                {
                                                    out.println("<td valign=\"middle\" data-label=\"Partner Id\" align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(partnerId1) + "</td>");
                                                }
                                                out.println("<td valign=\"middle\" data-label=\"Status\" align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(executed) + "</td>");
                                                out.println("<td valign=\"middle\" data-label=\"Creation Time\" align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(createdTime) + "</td>");

                                                out.println("<td valign=\"middle\" data-label=\"Action\" align=\"center\"" + style + "><form action=\"/partner/net/" + actionServlet + "?ctoken=" + ctoken + "\" method=\"POST\"><input type=\"hidden\" name=\"settlementcycleid\" value=\"" + ESAPI.encoder().encodeForHTML(settlementCycleId) + "\"><input type=\"submit\" name=\"chk\" value=\"Continue\" class=\"gotoauto btn btn-default\" " + settlementProcess + " ><input type=\"hidden\" name=\"action\" value=\"Continue\"></form></td>");

                                                out.println("</tr>");
                                            }
                                        %>
                                    </table>
                                    <%
                                        int TotalPageNo;
                                        if(totalRecords%pagerecords!=0)
                                        {
                                            TotalPageNo=totalRecords/pagerecords+1;
                                        }
                                        else
                                        {
                                            TotalPageNo=totalRecords/pagerecords;
                                        }
                                    %>

                                    <div id="showingid"><strong><%=listSettlementCycle_page_no%>    <%=pageno%>  <%=listSettlementCycle_of%>   <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
                                    <div id="showingid"><strong><%=listSettlementCycle_total_no_of_records%>      <%=totalRecords%> </strong></div>

                                    <jsp:include page="page.jsp" flush="true">
                                        <jsp:param name="numrecords" value="<%=totalRecords%>"/>
                                        <jsp:param name="numrows" value="<%=pagerecords%>"/>
                                        <jsp:param name="pageno" value="<%=pageno%>"/>
                                        <jsp:param name="str" value="<%=str%>"/>
                                        <jsp:param name="page" value="ListSettlementCycle"/>
                                        <jsp:param name="currentblock" value="<%=currentBlock%>"/>
                                        <jsp:param name="orderby" value=""/>
                                    </jsp:include>
                                    <%
                                            }
                                            else
                                            {
                                                out.println(Functions.NewShowConfirmation1(listSettlementCycle_Sorry,listSettlementCycle_no ));
                                            }
                                        }
                                        else
                                        {
                                            out.println(Functions.NewShowConfirmation1(listSettlementCycle_Sorry, listSettlementCycle_no));
                                        }
                                        %>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
<script language="javascript">
    $(document).ready(function ()
    {
        var dd = document.getElementById("status");
        for (var i = 0; i < dd.options.length; i++)
        {
            if (dd.options[i].value === "<%=status%>")
            {
                dd.selectedIndex = i;
                break;
            }
        }
    });
</script>

</html>
<%
    }
    else
    {
        response.sendRedirect("/partner/logout.jsp");
        return;
    }
%>
<%!
    public static String nullToStr(String str)
    {
        if(str == null)
            return "";
        return str;
    }
%>