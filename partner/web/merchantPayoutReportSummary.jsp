</html>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="top.jsp" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
  Created by IntelliJ IDEA.
  User: Sandip
  Date: 01/13/2018
  Time: 4:59 PM
  To change this template use File | Settings | File Templates.
--%>

<%
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String merchantPayoutReportSummary_Wire_creation_status = StringUtils.isNotEmpty(rb1.getString("merchantPayoutReportSummary_Wire_creation_status")) ? rb1.getString("merchantPayoutReportSummary_Wire_creation_status") : "Wire creation status";
    String merchantPayoutReportSummary_CycleId = StringUtils.isNotEmpty(rb1.getString("merchantPayoutReportSummary_CycleId")) ? rb1.getString("merchantPayoutReportSummary_CycleId") : "Cycle Id";
    String merchantPayoutReportSummary_MemberId = StringUtils.isNotEmpty(rb1.getString("merchantPayoutReportSummary_MemberId")) ? rb1.getString("merchantPayoutReportSummary_MemberId") : "Member Id";
    String merchantPayoutReportSummary_AccountId = StringUtils.isNotEmpty(rb1.getString("merchantPayoutReportSummary_AccountId")) ? rb1.getString("merchantPayoutReportSummary_AccountId") : "Account Id";
    String merchantPayoutReportSummary_TerminalId = StringUtils.isNotEmpty(rb1.getString("merchantPayoutReportSummary_TerminalId")) ? rb1.getString("merchantPayoutReportSummary_TerminalId") : "Terminal Id";
    String merchantPayoutReportSummary_Status = StringUtils.isNotEmpty(rb1.getString("merchantPayoutReportSummary_Status")) ? rb1.getString("merchantPayoutReportSummary_Status") : "Status";
    String merchantPayoutReportSummary_Description = StringUtils.isNotEmpty(rb1.getString("merchantPayoutReportSummary_Description")) ? rb1.getString("merchantPayoutReportSummary_Description") : "Description";
    String merchantPayoutReportSummary_Records_Uploaded = StringUtils.isNotEmpty(rb1.getString("merchantPayoutReportSummary_Records_Uploaded")) ? rb1.getString("merchantPayoutReportSummary_Records_Uploaded") : "Records Uploaded Successfully";
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (partner.isLoggedInPartner(session))
    {
        String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
        session.setAttribute("submit", "Add Settlement Cycle");
        List<String> stringList = (List) request.getAttribute("stringList");
        String status=ESAPI.encoder().encodeForHTML((String)session.getAttribute("success"));
%>
<html>
<head>
    <title><%=company%> | Wire creation status</title>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script type="text/javascript" language="JavaScript" src="/partner/javascript/memberid_terminal.js"></script>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
    </script>
    <script type="text/javascript">
        $('#sandbox-container input').datepicker({});
    </script>
    <script>
        $(function ()
        {
            $(".datepicker").datepicker({dateFormat: "yy-mm-dd", endDate: '+10y'});
        });
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
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=merchantPayoutReportSummary_Wire_creation_status%></strong>
                            </h2>
                        </div>
                        <div class="ibox-content">
                            <div class="flot-chart">
                                <table class="table table-striped table-bordered"
                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>
                                    </thead>

                                    <%
                                        if (stringList!=null)
                                        {
                                    %>
                                    <thead>
                                    <tr style="background-color: #7eccad !important;color: white;">
                                        <th style="text-align: center"><%=merchantPayoutReportSummary_CycleId%></th>
                                        <th style="text-align: center"><%=merchantPayoutReportSummary_MemberId%></th>
                                        <th style="text-align: center"><%=merchantPayoutReportSummary_AccountId%></th>
                                        <th style="text-align: center"><%=merchantPayoutReportSummary_TerminalId%></th>
                                        <th style="text-align: center"><%=merchantPayoutReportSummary_Status%></th>
                                        <th style="text-align: center"><%=merchantPayoutReportSummary_Description%></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <%
                                        for (String s : stringList)
                                        {
                                            String responseArr[] = s.split(":");
                                    %>
                                    <tr>
                                        <td data-label="Status" style="text-align: center"><%=responseArr[0]%>
                                        </td>
                                        <td data-label="Transaction" style="text-align: center"><%=responseArr[1]%>
                                        </td>
                                        <td data-label="Total Amount" style="text-align: center"><%=responseArr[2]%>
                                        </td>
                                        <td data-label="Total Amount" style="text-align: center"><%=responseArr[3]%>
                                        </td>
                                        <td data-label="Total Amount" style="text-align: center"><%=responseArr[4]%>
                                        </td>
                                        <td data-label="Total Amount" style="text-align: center"><%=responseArr[5]%>
                                        </td>
                                    </tr>
                                    <%
                                        }
                                    %>
                                    </tbody>
                                    <%
                                        }
                                        else if("success".equalsIgnoreCase(status))
                                        {
                                    %>
                                    <thead>
                                    <tr style="background-color: #7eccad !important;color: white;">
                                        <th style="text-align: center">Result</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <tr>
                                        <td data-label="Status" style="text-align: center"><%=merchantPayoutReportSummary_Records_Uploaded%>
                                        </td>
                                    </tr>
                                    </tbody>
                                    <%
                                        }
                                    %>
                                </table>
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