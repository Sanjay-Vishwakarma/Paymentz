<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.PartnerManager" %>
<%@ page import="com.manager.utils.CommonFunctionUtil" %>
<%@ page import="com.manager.vo.payoutVOs.SettlementCycleVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="java.util.TreeMap" %>
<%@ include file="top.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
  Created by IntelliJ IDEA.
  User: SurajT
  Date: 1/24/2018
  Time: 4:59 PM
  To change this template use File | Settings | File Templates.
--%>

<%
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String addSettlementCycle_Add_Settlement_Cycle = StringUtils.isNotEmpty(rb1.getString("addSettlementCycle_Add_Settlement_Cycle")) ? rb1.getString("addSettlementCycle_Add_Settlement_Cycle") : "Add Settlement Cycle";
    String addSettlementCycle_PartnerID = StringUtils.isNotEmpty(rb1.getString("addSettlementCycle_PartnerID")) ? rb1.getString("addSettlementCycle_PartnerID") : "Partner ID*";
    String addSettlementCycle_Bank_Account = StringUtils.isNotEmpty(rb1.getString("addSettlementCycle_Bank_Account")) ? rb1.getString("addSettlementCycle_Bank_Account") : "Bank Account*";
    String addSettlementCycle_Bank_Settlement_Start_Date = StringUtils.isNotEmpty(rb1.getString("addSettlementCycle_Bank_Settlement_Start_Date")) ? rb1.getString("addSettlementCycle_Bank_Settlement_Start_Date") : "Bank Settlement Start Date*";
    String addSettlementCycle_Bank_Settlement_End_Date = StringUtils.isNotEmpty(rb1.getString("addSettlementCycle_Bank_Settlement_End_Date")) ? rb1.getString("addSettlementCycle_Bank_Settlement_End_Date") : "Bank Settlement End Date*";
    String addSettlementCycle_Transaction_File_Available = StringUtils.isNotEmpty(rb1.getString("addSettlementCycle_Transaction_File_Available")) ? rb1.getString("addSettlementCycle_Transaction_File_Available") : "Transaction File Available*";
    String addSettlementCycle_N = StringUtils.isNotEmpty(rb1.getString("addSettlementCycle_N")) ? rb1.getString("addSettlementCycle_N") : "N";
    String addSettlementCycle_Y = StringUtils.isNotEmpty(rb1.getString("addSettlementCycle_Y")) ? rb1.getString("addSettlementCycle_Y") : "Y";
    String addSettlementCycle_Cancel = StringUtils.isNotEmpty(rb1.getString("addSettlementCycle_Cancel")) ? rb1.getString("addSettlementCycle_Cancel") : "Cancel";
    String addSettlementCycle_Create_Settlement_Cycle = StringUtils.isNotEmpty(rb1.getString("addSettlementCycle_Create_Settlement_Cycle")) ? rb1.getString("addSettlementCycle_Create_Settlement_Cycle") : "Create Settlement Cycle";

    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (partner.isLoggedInPartner(session))
    {
        String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
        String partnerId = (String) session.getAttribute("merchantid");

        session.setAttribute("submit", "Add Settlement Cycle");

        String reqStartDate = Functions.checkStringNull(request.getParameter("startdate")) == null ? "" : request.getParameter("startdate");
         String reqStartTime = Functions.checkStringNull(request.getParameter("starttime")) == null ? "" : request.getParameter("starttime");
        String reqEndDate = Functions.checkStringNull(request.getParameter("enddate")) == null ? "" : request.getParameter("enddate");
        String reqEndTime = Functions.checkStringNull(request.getParameter("endtime")) == null ? "" : request.getParameter("endtime");
        String reqAccountId = Functions.checkStringNull(request.getParameter("accountid")) == null ? "" : request.getParameter("accountid");
        String reqPartnerId = Functions.checkStringNull(request.getParameter("partnerId")) == null ? "" : request.getParameter("partnerId");
        String action = Functions.checkStringNull(request.getParameter("action")) == null ? "" : request.getParameter("action");
        String isTransactionFileAvailable = Functions.checkStringNull(request.getParameter("istransactionfileavailable")) == null ? "" : request.getParameter("istransactionfileavailable");

        PartnerManager partnerManager = new PartnerManager();
        PartnerFunctions partnerFunctions=new PartnerFunctions();
        Functions functions = new Functions();
        CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();

        TreeMap<Integer, String> integerStringTreeMap = partnerManager.loadGatewayAccounts(partnerId, "");
        TreeMap<String, String> subPartnersDetails = partnerFunctions.getPartnerDetailsForUI(partnerId);

        SettlementCycleVO settlementCycleVO = (SettlementCycleVO) session.getAttribute("settlementCycleVO");
        if (settlementCycleVO != null)
        {
            if (!functions.isValueNull(reqStartDate))
            {
                if (functions.isValueNull(settlementCycleVO.getStartDate()))
                {
                    reqStartDate = settlementCycleVO.getStartDate();
                }
            }
            if (!functions.isValueNull(reqStartTime))
            {
                if (functions.isValueNull(settlementCycleVO.getStartTime()))
                {
                    reqStartTime = settlementCycleVO.getStartTime();
                }
            }
            if (!functions.isValueNull(reqEndDate))
            {
                if (functions.isValueNull(settlementCycleVO.getEndDate()))
                {
                    reqEndDate = settlementCycleVO.getEndDate();
                }
            }
            if (!functions.isValueNull(reqEndTime))
            {
                if (functions.isValueNull(settlementCycleVO.getEndTime()))
                {
                    reqEndTime = settlementCycleVO.getEndTime();
                }
            }
            if (!functions.isValueNull(reqAccountId))
            {
                reqAccountId = settlementCycleVO.getAccountId();
            }
            if (!functions.isValueNull(reqPartnerId))
            {
                reqPartnerId = settlementCycleVO.getPartnerId();
            }
            if (!functions.isValueNull(isTransactionFileAvailable))
            {
                if (functions.isValueNull(settlementCycleVO.getIsTransactionFileAvailable()))
                {
                    isTransactionFileAvailable = settlementCycleVO.getIsTransactionFileAvailable();
                }
            }

        }
        if (functions.isValueNull((String) request.getAttribute("nextstartdate")))
        {
            reqStartDate = (String) request.getAttribute("nextstartdate");
        }
        if (functions.isValueNull((String) request.getAttribute("nextstarttime")))
        {
            reqStartTime = (String) request.getAttribute("nextstarttime");
        }
        if ("cancel".equalsIgnoreCase(action))
        {
            reqStartDate = "";
            reqEndDate = "";
            reqAccountId = "";
            isTransactionFileAvailable = "";
            reqPartnerId = "";
        }

        String errorMessage = (String) request.getAttribute("sberror");
        SettlementCycleVO previousSettlementCycleVO = (SettlementCycleVO) request.getAttribute("previousSettlementCycleVO");
        String roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
        if (previousSettlementCycleVO != null)
        {
            if (functions.isValueNull(previousSettlementCycleVO.getEndDate()))
            {
                reqStartDate = previousSettlementCycleVO.getEndDate();

            }
        }

%>
<html>
<head>
    <title><%=company%> Settlement Management> Add Settlement Cycle</title>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script type="text/javascript">
        $('#sandbox-container input').datepicker({});
    </script>
    <script>
        $(function ()
        {
            $(".datepicker").datepicker({dateFormat: "yy-mm-dd HH:mm:ss"});
        });
    </script>
    <script type="text/javascript">
        function getAccountDetails(ctoken)
        {
            var accountId = document.getElementById("accountid").value;
            document.f1.action = "/partner/net/AddSettlementCycle?ctoken=" + ctoken + "&action=getinfo";
            document.f1.submit();
        }
    </script>
    <style type="text/css">
        #main {
            background-color: #ffffff
        }

        :target:before {
            content: "";
            display: block;
            height: 50px;
            margin: -50px 0 0;
        }

        .table > thead > tr > th {
            font-weight: inherit;
        }

        :target:before {
            content: "";
            display: block;
            height: 90px;
            margin: -50px 0 0;
        }

        footer {
            border-top: none;
            margin-top: 0;
            padding: 0;
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
        @media (min-width: 768px) {
            .form-horizontal .control-label {
                text-align: left !important;
            }
        }
    </style>
</head>
<body>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-md-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=addSettlementCycle_Add_Settlement_Cycle%></strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <br>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <form action="/partner/net/AddSettlementCycle?ctoken=<%=ctoken%>" method="post"
                                      name="f1" class="form-horizontal">
                                    <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>">

                                    <div class="widget-content padding">
                                        <div class="form-group">
                                            <div class="col-md-12">
                                                <%
                                                    if (functions.isValueNull(errorMessage))
                                                    {
                                                        out.print("<center>" + errorMessage + "</center>");
                                                    }
                                                %>
                                            </div>
                                        </div>

                                        <%
                                            if(roles.contains("superpartner"))
                                            {
                                        %>

                                        <div class="form-group">
                                            <div class="col-md-2"></div>
                                            <label class="col-md-2 control-label"><%=addSettlementCycle_PartnerID%><br>
                                            </label>

                                            <div class="col-md-4">
                                                <select name="partnerId" class="form-control">
                                                    <option value=""></option>
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
                                            <div class="col-md-6"></div>
                                        </div>

                                        <%
                                            }
                                        %>

                                        <div class="form-group">
                                            <div class="col-md-2"></div>
                                            <label class="col-md-2 control-label"><%=addSettlementCycle_Bank_Account%><br>
                                            </label>

                                            <div class="col-md-4">
                                                <select name="accountid" id="accountid" class="form-control"
                                                        onChange="getAccountDetails('<%=ctoken%>')">
                                                    <option value=""></option>
                                                    <%
                                                        for (Integer accountId : integerStringTreeMap.keySet())
                                                        {
                                                            String isSelected = "";
                                                            String mid=integerStringTreeMap.get(accountId);
                                                            if (String.valueOf(accountId).equals(reqAccountId))
                                                            {
                                                                isSelected = "selected";
                                                            }
                                                    %>
                                                    <option value="<%=accountId%>" <%=isSelected%>><%=accountId%>-<%=mid%>
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
                                            <label class="col-md-2 control-label"><%=addSettlementCycle_Bank_Settlement_Start_Date%><br>
                                            </label>

                                            <div class="col-md-4">
                                                <input type="text" size="16" name="startdate"
                                                       class="datepicker form-control"
                                                       value="<%=ESAPI.encoder().encodeForHTMLAttribute (reqStartDate)%>"
                                                       readonly="readonly"
                                                       style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                            </div>
                                            <div class="col-md-2">
                                                <input type="text" class="form-control" name="starttime"
                                                       value="<%= ESAPI.encoder().encodeForHTMLAttribute (reqStartTime)%>"
                                                       size="8" placeholder="00:00:00">
                                            </div>
                                            <div class="col-md-4"></div>
                                        </div>
                                        <div class="form-group">
                                            <div class="col-md-2"></div>
                                            <label class="col-md-2 control-label"><%=addSettlementCycle_Bank_Settlement_End_Date%><br>
                                            </label>

                                            <div class="col-md-4">
                                                <input type="text" size="16" name="enddate"
                                                       class="datepicker form-control"
                                                       value="<%= ESAPI.encoder().encodeForHTMLAttribute (reqEndDate)%>"
                                                       readonly="readonly"
                                                       style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                            </div>
                                            <div class="col-md-2">
                                                <input type="text" class="form-control" name="endtime"
                                                       value="<%= ESAPI.encoder().encodeForHTMLAttribute (reqEndTime)%>"
                                                       size="8" placeholder="00:00:00">
                                            </div>
                                            <div class="col-md-4"></div>
                                        </div>
                                        <div class="form-group">
                                            <div class="col-md-2"></div>
                                            <label class="col-md-2 control-label"><%=addSettlementCycle_Transaction_File_Available%><br>
                                            </label>

                                            <div class="col-md-4">
                                                <select name="istransactionfileavailable"
                                                        id="istransactionfileavailable" class="form-control">
                                                    <%
                                                        if ("Y".equalsIgnoreCase(isTransactionFileAvailable))
                                                        {
                                                    %>
                                                    <option value="N"><%=addSettlementCycle_N%></option>
                                                    <option value="Y" selected><%=addSettlementCycle_Y%></option>
                                                    <%
                                                    }
                                                    else
                                                    {
                                                    %>
                                                    <option value="N"><%=addSettlementCycle_N%></option>
                                                    <option value="Y"><%=addSettlementCycle_Y%></option>
                                                    <%
                                                        }
                                                    %>
                                                </select>
                                            </div>
                                            <div class="col-md-6"></div>
                                        </div>

                                        <div class="form-group col-md-12 has-feedback">
                                            <div class="col-md-6" align="right">
                                                <button type="submit" value="cancel" name="action"
                                                        class="btn btn-default" id="action"
                                                        style="display: -webkit-box;"><i class="fa fa-save">&nbsp;&nbsp;<%=addSettlementCycle_Cancel%></i>
                                                </button>
                                            </div>
                                            <div class="col-md-6" align="left">
                                                <button type="submit" value="next" name="action" class="btn btn-default"
                                                        id="next" style="display: -webkit-box;"><i class="fa fa-save">&nbsp;&nbsp;<%=addSettlementCycle_Create_Settlement_Cycle%>
                                                    </i></button>
                                            </div>
                                        </div>

                                        <div class="form-group col-md-8 has-feedback"></div>
                                        <div class="form-group col-md-8 has-feedback"></div>

                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
<%
    }
    else
    {
        response.sendRedirect("/partner/logout.jsp");
        return;
    }
%>