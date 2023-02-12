<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.ChargeVO" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="com.manager.vo.payoutVOs.SettlementCycleVO" %>
<%@ page import="com.manager.SettlementManager" %>
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
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (partner.isLoggedInPartner(session))
    {
        session.setAttribute("submit", "Add Settlement Cycle");

        String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
        String partnerId = null;

        Functions functions = new Functions();
        String errorMessage = (String) request.getAttribute("sberror");

        //HashMap<TerminalVO,List<ChargeVO>> stringListHashMap=(HashMap)request.getAttribute("stringListHashMap");
        Set<String> settlementCurrSet = (Set) request.getAttribute("settlementCurrSet");
        String processingCurrency = (String) request.getAttribute("processingCurrency");
        System.out.println("SETLEMENT CYCLE ID::::"+session.getAttribute("settlementCycleId"));
        SettlementManager settlementManager=new SettlementManager();
        SettlementCycleVO settlementCycleVO = settlementManager.getSettlementCycleInfo(String.valueOf(session.getAttribute("settlementCycleId")), "SettlementUploaded");
        partnerId=settlementCycleVO.getPartnerId();
        System.out.println("Partner Id::::"+partnerId);
%>
<html>
<head>
    <title><%=company%> | Add New Settlement Cycle</title>
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
    <
    <script type="text/javascript">
        function getAccountDetails(ctoken)
        {
            var accountId = document.getElementById("accountid").value;
            document.f1.action = "/partner/net/AddSettlementCycle?ctoken=" + ctoken + "&accountid=" + accountId + "&action=getinfo";
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
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Exchange Rates</strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <br>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <form action="/partner/net/GenerateMerchantPayoutReports?ctoken=<%=ctoken%>"
                                      method="post" name="f1" class="form-horizontal">
                                    <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>">
                                    <input id="step" name="step" type="hidden" value="step2">

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
                                        <div class="form-group">
                                            <div class="col-md-2"></div>
                                            <label class="col-md-2 control-label">Processing Currency*<br>
                                            </label>

                                            <div class="col-md-4">
                                                <%=processingCurrency%>
                                            </div>
                                            <div class="col-md-6"></div>
                                        </div>
                                        <%
                                            StringBuffer settlementCurrency = new StringBuffer();
                                            for (String currency : settlementCurrSet)
                                            {
                                                if (settlementCurrency.length() > 0)
                                                {
                                                    settlementCurrency.append(",");
                                                }
                                                settlementCurrency.append(currency);
                                        %>
                                        <div class="form-group">
                                            <div class="col-md-2"></div>
                                            <label class="col-md-2 control-label"><%=currency%>&nbsp; Conversion Rate
                                                *<br>
                                            </label>

                                            <div class="col-md-4">
                                                <input type="text" class="form-control"
                                                       name="<%=currency%>_conversion_rate" value="" size="35">
                                            </div>
                                            <div class="col-md-6"></div>
                                        </div>
                                        <%
                                            }
                                        %>
                                        <input type="hidden" name="settlementcurrency"
                                               value="<%=settlementCurrency.toString()%>">
                                        <input type="hidden" name="partnerId"
                                               value="<%=partnerId%>">

                                        <div class="form-group col-md-12 has-feedback">
                                            <div class="col-md-3"></div>
                                            <div class="col-md-3" align="center">
                                                <button type="submit" value="next" name="action" class="btn btn-default"
                                                        id="next" style="display: -webkit-box;"><i class="fa fa-save">Generate
                                                    Merchant Payout</i></button>
                                            </div>
                                            &nbsp;
                                            <div class="col-md-3" align="center"><input type="hidden" name="partnerId" value="<%=partnerId%>">
                                                <button type="submit" value="next" name="manual" formaction="/partner/manualReportGenerate.jsp" class="btn btn-default"
                                                        id="next" style="display: -webkit-box;"><i class="fa fa-save">Generate Manual
                                                    Merchant Payout Report</i></button>
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