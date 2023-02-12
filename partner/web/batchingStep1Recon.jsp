</html>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.vo.SettlementDateVO" %>
<%@ page import="com.manager.vo.payoutVOs.SettlementCycleVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Hashtable" %>
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
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (partner.isLoggedInPartner(session))
    {
        String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
        session.setAttribute("submit", "Add Settlement Cycle");

        Hashtable hashTable1 = (Hashtable) request.getAttribute("hashTable1");
        Hashtable hashTable2 = (Hashtable) request.getAttribute("hashTable2");
        String accountId = (String) request.getAttribute("accountid");

        SettlementDateVO settlementDateVO1 = (SettlementDateVO) request.getAttribute("settlementDateVO1");
        SettlementDateVO settlementDateVO2 = (SettlementDateVO) request.getAttribute("settlementDateVO2");
        SettlementDateVO settlementDateVO3 = (SettlementDateVO) request.getAttribute("settlementDateVO3");

        session.setAttribute("settlementDateVO1", settlementDateVO1);
        session.setAttribute("settlementDateVO2", settlementDateVO2);
        //session.setAttribute("settlementDateVO3",settlementDateVO3);
        Functions functions = new Functions();

        SettlementCycleVO settlementCycleVO = (SettlementCycleVO) session.getAttribute("settlementCycleVO");
        String isTransactionFileAvailable = "";
        if (settlementCycleVO != null && functions.isValueNull(settlementCycleVO.getIsTransactionFileAvailable()))
        {
            isTransactionFileAvailable = settlementCycleVO.getIsTransactionFileAvailable();
        }

        session.setAttribute("accountid", accountId);
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String batchingStep1Recon_Bank_Transaction_Report = StringUtils.isNotEmpty(rb1.getString("batchingStep1Recon_Bank_Transaction_Report")) ? rb1.getString("batchingStep1Recon_Bank_Transaction_Report") : "Bank Transaction Report";
        String batchingStep1Recon_Transaction_Data = StringUtils.isNotEmpty(rb1.getString("batchingStep1Recon_Transaction_Data")) ? rb1.getString("batchingStep1Recon_Transaction_Data") : "Transaction data in PZ system for given settlement period";
        String batchingStep1Recon_Status = StringUtils.isNotEmpty(rb1.getString("batchingStep1Recon_Status")) ? rb1.getString("batchingStep1Recon_Status") : "Status";
        String batchingStep1Recon_Count = StringUtils.isNotEmpty(rb1.getString("batchingStep1Recon_Count")) ? rb1.getString("batchingStep1Recon_Count") : "Count";
        String batchingStep1Recon_Amount = StringUtils.isNotEmpty(rb1.getString("batchingStep1Recon_Amount")) ? rb1.getString("batchingStep1Recon_Amount") : "Amount";
        String batchingStep1Recon_Capture_Amount = StringUtils.isNotEmpty(rb1.getString("batchingStep1Recon_Capture_Amount")) ? rb1.getString("batchingStep1Recon_Capture_Amount") : "Capture Amount";
        String batchingStep1Recon_Refund_Amount = StringUtils.isNotEmpty(rb1.getString("batchingStep1Recon_Refund_Amount")) ? rb1.getString("batchingStep1Recon_Refund_Amount") : "Refund Amount";
        String batchingStep1Recon_Chargeback_Amount = StringUtils.isNotEmpty(rb1.getString("batchingStep1Recon_Chargeback_Amount")) ? rb1.getString("batchingStep1Recon_Chargeback_Amount") : "Chargeback Amount";
        String batchingStep1Recon_no = StringUtils.isNotEmpty(rb1.getString("batchingStep1Recon_no")) ? rb1.getString("batchingStep1Recon_no") : "No Records Found";
        String batchingStep1Recon_uploaded_file = StringUtils.isNotEmpty(rb1.getString("batchingStep1Recon_uploaded_file")) ? rb1.getString("batchingStep1Recon_uploaded_file") : "Transaction data from uploaded file for date range";
        String batchingStep1Recon_Back = StringUtils.isNotEmpty(rb1.getString("batchingStep1Recon_Back")) ? rb1.getString("batchingStep1Recon_Back") : "Back";
        String batchingStep1Recon_Upload_Settlement = StringUtils.isNotEmpty(rb1.getString("batchingStep1Recon_Upload_Settlement")) ? rb1.getString("batchingStep1Recon_Upload_Settlement") : "Upload Settlement";
%>
<html>
<head>
    <title><%=company%> | Merchant Step1 Recon</title>
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
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=batchingStep1Recon_Bank_Transaction_Report%></strong></h2>
                        </div>
                        <div class="ibox-content">
                            <form action="/partner/net/ProcessSettlement?ctoken=<%=ctoken%>" method="post" name="form"
                                  class="form-horizontal">
                                <div class="flot-chart">
                                    <table class="table table-striped table-bordered"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <thead>
                                        <tr>
                                            <td class="mtransreport" colspan="3" align="left"><%=batchingStep1Recon_Transaction_Data%>

                                                [<%=settlementDateVO1.getSettlementStartDate()%>
                                                To <%=settlementDateVO1.getSettlementEndDate()%>]
                                            </td>
                                            <td class="mtransreport" colspan="3" align="center"></td>
                                        </tr>
                                        </thead>
                                        <thead>
                                        <tr style="background-color: #7eccad !important;color: white;">
                                            <th style="text-align: center"><%=batchingStep1Recon_Status%></th>
                                            <th style="text-align: center"><%=batchingStep1Recon_Count%></th>
                                            <th style="text-align: center"><%=batchingStep1Recon_Amount%></th>
                                            <th style="text-align: center"><%=batchingStep1Recon_Capture_Amount%></th>
                                            <th style="text-align: center"><%=batchingStep1Recon_Refund_Amount%></th>
                                            <th style="text-align: center"><%=batchingStep1Recon_Chargeback_Amount%></th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <%
                                            if (hashTable1.size() > 0)
                                            {
                                                for (int pos = 1; pos <= hashTable1.size(); pos++)
                                                {
                                                    Hashtable innerhash = (Hashtable) hashTable1.get(pos + "");
                                        %>
                                        <tr>
                                            <td data-label="Status"
                                                style="text-align: center"><%=innerhash.get("STATUS")%>
                                            </td>
                                            <td data-label="Count"
                                                style="text-align: center"><%=innerhash.get("count")%>
                                            </td>
                                            <td data-label="Amount"
                                                style="text-align: center"><%=innerhash.get("amount")%>
                                            </td>
                                            <td data-label="Capture Amount"
                                                style="text-align: center"><%=innerhash.get("captureamount")%>
                                            </td>
                                            <td data-label="Refund Amount"
                                                style="text-align: center"><%=innerhash.get("refundamount")%>
                                            </td>
                                            <td data-label="Chargeback Amount"
                                                style="text-align: center"><%=innerhash.get("chargebackamount")%>
                                            </td>
                                        </tr>
                                        <%
                                            }
                                        }
                                        else
                                        {
                                        %>
                                        <tr>
                                            <td data-label="Status" style="text-align: center" colspan="6"><%=batchingStep1Recon_no%>

                                            </td>
                                        </tr>
                                        <%
                                            }
                                        %>
                                        </tbody>
                                    </table>
                                </div>
                                <BR>
                                <%
                                    if ("Y".equalsIgnoreCase(isTransactionFileAvailable))
                                    {%>
                                <div class="flot-chart">
                                    <table class="table table-striped table-bordered"
                                           style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <thead>
                                        <tr>
                                            <td class="mtransreport" colspan="3" align="left"><%=batchingStep1Recon_uploaded_file%>

                                                [<%=settlementDateVO2.getSettlementStartDate()%>
                                                To <%=settlementDateVO2.getSettlementEndDate()%>]
                                            </td>
                                            <td class="mtransreport" colspan="3" align="center"></td>
                                        </tr>
                                        </thead>
                                        <thead>
                                        <tr style="background-color: #7eccad !important;color: white;">
                                            <th style="text-align: center"><%=batchingStep1Recon_Status%></th>
                                            <th style="text-align: center"><%=batchingStep1Recon_Count%></th>
                                            <th style="text-align: center"><%=batchingStep1Recon_Amount%></th>
                                            <th style="text-align: center"><%=batchingStep1Recon_Capture_Amount%></th>
                                            <th style="text-align: center"><%=batchingStep1Recon_Refund_Amount%></th>
                                            <th style="text-align: center"><%=batchingStep1Recon_Chargeback_Amount%></th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <%
                                            if (hashTable2.size() > 0)
                                            {
                                                for (int pos = 1; pos <= hashTable2.size(); pos++)
                                                {
                                                    Hashtable innerhash = (Hashtable) hashTable2.get(pos + "");
                                                    String transactionStatus = (String) innerhash.get("STATUS");
                                                    if (transactionStatus.equals("authstarted"))
                                                    {
                                                        transactionStatus = "Authorization Started";
                                                    }
                                                    else if (transactionStatus.equals("authsuccessful"))
                                                    {
                                                        transactionStatus = "Authorization Successful";
                                                    }
                         /* else if(transactionStatus.equals("capturesuccess")){
                            transactionStatus="Capture Successful";
                          }*/
                                                    else if (transactionStatus.equals("authfailed"))
                                                    {
                                                        transactionStatus = "Failed";
                                                    }
                                                    else if (transactionStatus.equals("authcancelled"))
                                                    {
                                                        transactionStatus = "Authorization Cancelled";
                                                    }
                                                    else if (transactionStatus.equals("cancelstarted"))
                                                    {
                                                        transactionStatus = "Cancel Initiated";
                                                    }
                                                    else if (transactionStatus.equals("markedforreversal"))
                                                    {
                                                        transactionStatus = "Reversal Request Sent";
                                                    }
                                                    else if (transactionStatus.equals("reversed"))
                                                    {
                                                        transactionStatus = "Refunded";
                                                    }
                                                    else if (transactionStatus.equals("chargeback"))
                                                    {
                                                        transactionStatus = "Chargeback";
                                                    }
                                                    else if (transactionStatus.equals("payoutstarted"))
                                                    {
                                                        transactionStatus = "Payout Request Sent";
                                                    }
                                                    else if (transactionStatus.equals("payoutfailed"))
                                                    {
                                                        transactionStatus = "Payout Failed";
                                                    }
                                                    else if (transactionStatus.equals("payoutsuccessful"))
                                                    {
                                                        transactionStatus = "Payout Successful";
                                                    }
                                                    else if (transactionStatus.equals("settled") || transactionStatus.equals("capturesuccess"))
                                                    {
                                                        transactionStatus = "Successful";
                                                    }
                                                    else if (transactionStatus.equals("failed"))
                                                    {
                                                        transactionStatus = "failed";
                                                    }
                                        %>
                                        <tr>
                                            <td data-label="Status"
                                                style="text-align: center"><%=transactionStatus%>
                                            </td>
                                            <td data-label="Count"
                                                style="text-align: center"><%=innerhash.get("COUNT")%>
                                            </td>
                                            <td data-label="Amount"
                                                style="text-align: center"><%=innerhash.get("amount")%>
                                            </td>
                                            <td data-label="Capture Amount"
                                                style="text-align: center"><%=innerhash.get("captureamount")%>
                                            </td>
                                            <td data-label="Refund Amount"
                                                style="text-align: center"><%=innerhash.get("refundamount")%>
                                            </td>
                                            <td data-label="Chargeback Amount"
                                                style="text-align: center"><%=innerhash.get("chargebackamount")%>
                                            </td>
                                        </tr>
                                        <%
                                            }
                                        }
                                        else
                                        {
                                        %>
                                        <tr>
                                            <td data-label="Status" style="text-align: center" colspan="6"><%=batchingStep1Recon_no%>

                                            </td>
                                        </tr>
                                        <%
                                            }
                                        %>
                                        </tbody>
                                    </table>
                                </div>
                                <%
                                    }
                                %>
                                <BR>

                                <div class="form-group col-md-12 has-feedback">
                                    <div class="col-md-6" align="right">
                                        <button type="submit" value="back" name="action" class="btn btn-default"
                                                id="back" style="display: -webkit-box;"><i class="fa fa-save"><%=batchingStep1Recon_Back%></i>
                                        </button>
                                    </div>
                                    <%
                                        if(hashTable1.size() > 0 || hashTable2.size() > 0){
                                    %>
                                    <div class="col-md-6" align="left">
                                        <button type="submit" value="next" name="action" class="btn btn-default"
                                                id="next" style="display: -webkit-box;"><i class="fa fa-save"><%=batchingStep1Recon_Upload_Settlement%>
                                        </i></button>
                                    </div>
                                    <%
                                        }
                                    %>
                                </div>
                                <div class="form-group col-md-8 has-feedback"></div>
                                <div class="form-group col-md-8 has-feedback"></div>
                            </form>
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