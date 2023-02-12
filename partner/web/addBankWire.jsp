</html>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.utils.CommonFunctionUtil" %>
<%@ page import="com.manager.vo.BankWireManagerVO" %>
<%@ page import="com.manager.vo.payoutVOs.SettlementCycleVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.ResourceBundle" %>
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
    String addBankWire_Bank_Wire_Creation = StringUtils.isNotEmpty(rb1.getString("addBankWire_Bank_Wire_Creation")) ? rb1.getString("addBankWire_Bank_Wire_Creation") : "Bank Wire Creation";
    String addBankWire_AccountID = StringUtils.isNotEmpty(rb1.getString("addBankWire_AccountID")) ? rb1.getString("addBankWire_AccountID") : "Account ID";
    String addBankWire_Bank_Settlement_Start = StringUtils.isNotEmpty(rb1.getString("addBankWire_Bank_Settlement_Start")) ? rb1.getString("addBankWire_Bank_Settlement_Start") : "Bank Settlement Start Date*";
    String addBankWire_Bank_Settlement_End = StringUtils.isNotEmpty(rb1.getString("addBankWire_Bank_Settlement_End")) ? rb1.getString("addBankWire_Bank_Settlement_End") : "Bank Settlement End Date*";
    String addBankWire_Server_Settlement_Start = StringUtils.isNotEmpty(rb1.getString("addBankWire_Server_Settlement_Start")) ? rb1.getString("addBankWire_Server_Settlement_Start") : "Server Settlement Start Date*";
    String addBankWire_Server_Settlement_End = StringUtils.isNotEmpty(rb1.getString("addBankWire_Server_Settlement_End")) ? rb1.getString("addBankWire_Server_Settlement_End") : "Server Settlement End Date*";
    String addBankWire_Is_Money_Received = StringUtils.isNotEmpty(rb1.getString("addBankWire_Is_Money_Received")) ? rb1.getString("addBankWire_Is_Money_Received") : "Is Money Received*";
    String addBankWire_Y = StringUtils.isNotEmpty(rb1.getString("addBankWire_Y")) ? rb1.getString("addBankWire_Y") : "Y";
    String addBankWire_N = StringUtils.isNotEmpty(rb1.getString("addBankWire_N")) ? rb1.getString("addBankWire_N") : "N";
    String addBankWire_Money_Received_Date = StringUtils.isNotEmpty(rb1.getString("addBankWire_Money_Received_Date")) ? rb1.getString("addBankWire_Money_Received_Date") : "Money Received Date";
    String addBankWire_Received_Amount = StringUtils.isNotEmpty(rb1.getString("addBankWire_Received_Amount")) ? rb1.getString("addBankWire_Received_Amount") : "Received Amount";
    String addBankWire_UnPaid_Amount = StringUtils.isNotEmpty(rb1.getString("addBankWire_UnPaid_Amount")) ? rb1.getString("addBankWire_UnPaid_Amount") : "UnPaid Amount";
    String addBankWire_RR_Release_Date = StringUtils.isNotEmpty(rb1.getString("addBankWire_RR_Release_Date")) ? rb1.getString("addBankWire_RR_Release_Date") : "RR Release Date*";
    String addBankWire_Transaction_File = StringUtils.isNotEmpty(rb1.getString("addBankWire_Transaction_File")) ? rb1.getString("addBankWire_Transaction_File") : "Transaction File From Bank(.xls)*";
    String addBankWire_Back = StringUtils.isNotEmpty(rb1.getString("addBankWire_Back")) ? rb1.getString("addBankWire_Back") : "Back";
    String addBankWire_Create_Bank_Wire = StringUtils.isNotEmpty(rb1.getString("addBankWire_Create_Bank_Wire")) ? rb1.getString("addBankWire_Create_Bank_Wire") : "Create Bank Wire";

    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (partner.isLoggedInPartner(session))
    {
        String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("partnername"));
        session.setAttribute("submit", "Add Settlement Cycle");

        BankWireManagerVO bankWireManagerVO = (BankWireManagerVO) session.getAttribute("bankWireManagerVO");
        SettlementCycleVO settlementCycleVO = (SettlementCycleVO) session.getAttribute("settlementCycleVO");
        Functions functions = new Functions();
        String errorMessage = (String) request.getAttribute("sberror");

        System.out.println("PartnerId in addBankWire:::::"+settlementCycleVO.getPartnerId());

        String settledDate = "";
        String rrDate = "";
        String netFinalAmount = "";
        String unPaidAmount = "";


        CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();

        if (bankWireManagerVO != null && functions.isValueNull(bankWireManagerVO.getSettleddate()))
        {
            settledDate = commonFunctionUtil.convertTimestampToDatepicker(bankWireManagerVO.getSettleddate());
        }
        if (bankWireManagerVO != null && functions.isValueNull(bankWireManagerVO.getRollingreservereleasedateupto()))
        {
            rrDate = commonFunctionUtil.convertTimestampToDatepicker(bankWireManagerVO.getRollingreservereleasedateupto());
        }
        if (bankWireManagerVO != null && functions.isValueNull(bankWireManagerVO.getNetfinal_amount()))
        {
            netFinalAmount = Functions.round(Double.valueOf(bankWireManagerVO.getNetfinal_amount()), 2);
        }
        if (bankWireManagerVO != null && functions.isValueNull(bankWireManagerVO.getUnpaid_amount()))
        {
            unPaidAmount = Functions.round(Double.valueOf(bankWireManagerVO.getUnpaid_amount()), 2);
        }
%>
<html>
<head>
    <title><%=company%> | Add Bank Wire</title>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
    <script type="text/javascript" language="JavaScript" src="/partner/javascript/memberid_terminal.js"></script>
    <script type="text/javascript" language="JavaScript">
        function check()
        {
            var retPath = document.FIRCForm.file.value;
            var ISFA = document.FIRCForm.issft.value;
            if (retPath != '' && 'Y' == ISFA)
            {
                var pos = retPath.lastIndexOf(".");
                var filename = "";
                if (pos != -1)
                    filename = retPath.substring(pos + 1);
                else
                    filename = retPath;
                if (filename == ('xls'))
                {
                    return true;
                }
                document.FIRCForm.file.value = "";
                alert('Please select a .xls file! ');
                return false;
            }
            else if (retPath == '' && 'Y' == ISFA)
            {
                document.FIRCForm.file.value = "";
                alert('Please select bank transaction file');
                return false;
            }
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
                <div class="col-md-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=addBankWire_Bank_Wire_Creation%></strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <br>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <form name="FIRCForm"
                                      action="/partner/net/AddBankWireAndDoStep1Recon?ctoken=<%=ctoken%>" method="post"
                                      ENCTYPE="multipart/form-data" class="form-horizontal">
                                    <input type="hidden" value="<%=settlementCycleVO.getPartnerId()%>"
                                           name="partnerId">
                                    <input type="hidden" value="<%=settlementCycleVO.getIsTransactionFileAvailable()%>"
                                           name="issft">

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
                                            <label class="col-md-2 control-label"><%=addBankWire_AccountID%><br>
                                            </label>

                                            <div class="col-md-4">
                                                <input type="text" class="form-control" name="acid"
                                                       value="<%=bankWireManagerVO.getAccountId()%>" size="35" readonly>
                                            </div>
                                            <div class="col-md-6"></div>
                                        </div>
                                        <div class="form-group">
                                            <div class="col-md-2"></div>
                                            <label class="col-md-2 control-label"><%=addBankWire_Bank_Settlement_Start%><br>
                                            </label>

                                            <%
                                                String start_date[] = commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getBank_start_date());
                                                String Bank_start_date = start_date[0]+" "+start_date[1];

                                                String end_date[] = commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getBank_end_date());
                                                String Bank_end_date = end_date[0]+" "+end_date[1];

                                                String server_start_date[] = commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getServer_start_date());
                                                String Server_start_date = server_start_date[0]+" "+server_start_date[1];

                                                String server_end_date[] = commonFunctionUtil.convertTimestampToDateTimePicker(bankWireManagerVO.getServer_end_date());
                                                String Server_end_date = server_end_date[0]+" "+server_end_date[1];
                                            %>
                                            <div class="col-md-4">
                                                <input type="text" class="form-control" name="bsdate"
                                                       value="<%=Bank_start_date%>" size="35"
                                                       readonly>
                                            </div>
                                            <div class="col-md-6"></div>
                                        </div>
                                        <div class="form-group">
                                            <div class="col-md-2"></div>
                                            <label class="col-md-2 control-label"><%=addBankWire_Bank_Settlement_End%><br>
                                            </label>

                                            <div class="col-md-4">
                                                <input type="text" class="form-control" name="bedate"
                                                       value="<%=Bank_end_date%>" size="35"
                                                       readonly>
                                            </div>
                                            <div class="col-md-6"></div>
                                        </div>
                                        <div class="form-group">
                                            <div class="col-md-2"></div>
                                            <label class="col-md-2 control-label"><%=addBankWire_Server_Settlement_Start%><br>
                                            </label>

                                            <div class="col-md-4">
                                                <input type="text" class="form-control" name="ssdate"
                                                       value="<%=Server_start_date%>" size="35"
                                                       readonly>
                                            </div>
                                            <div class="col-md-6"></div>
                                        </div>
                                        <div class="form-group">
                                            <div class="col-md-2"></div>
                                            <label class="col-md-2 control-label"><%=addBankWire_Server_Settlement_End%><br>
                                            </label>

                                            <div class="col-md-4">
                                                <input type="text" class="form-control" name="sedate"
                                                       value="<%=Server_end_date%>" size="35"
                                                       readonly>
                                            </div>
                                            <div class="col-md-6"></div>
                                        </div>
                                        <div class="form-group">
                                            <div class="col-md-2"></div>
                                            <label class="col-md-2 control-label"><%=addBankWire_Is_Money_Received%><br>
                                            </label>

                                            <div class="col-md-4">
                                                <select name="ispaid" class="form-control">
                                                    <%
                                                        if ("N".equalsIgnoreCase(bankWireManagerVO.getIspaid()))
                                                        {%>
                                                    <option value="Y"><%=addBankWire_Y%></option>
                                                    <option value="N" selected><%=addBankWire_N%></option>
                                                    <%
                                                    }
                                                    else
                                                    {
                                                    %>
                                                    <option value="Y"><%=addBankWire_Y%></option>
                                                    <option value="N"><%=addBankWire_N%></option>
                                                    <%
                                                        }
                                                    %>
                                                </select>
                                            </div>
                                            <div class="col-md-6"></div>
                                        </div>

                                        <div class="form-group">
                                            <div class="col-md-2"></div>
                                            <label class="col-md-2 control-label"><%=addBankWire_Money_Received_Date%><br>
                                            </label>

                                            <div class="col-md-4">
                                                <input type="text" size="16" name="sdate"
                                                       class="datepicker form-control"
                                                       value="<%=ESAPI.encoder().encodeForHTMLAttribute (settledDate)%>"
                                                       readonly="readonly"
                                                       style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                            </div>
                                            <div class="col-md-6"></div>
                                        </div>

                                        <div class="form-group">
                                            <div class="col-md-2"></div>
                                            <label class="col-md-2 control-label"><%=addBankWire_Received_Amount%><br>
                                            </label>

                                            <div class="col-md-4">
                                                <input type="text" class="form-control" name="nfamt"
                                                       value="<%=netFinalAmount%>" placeHolder="0.00" size="9">
                                            </div>
                                            <div class="col-md-6"></div>
                                        </div>

                                        <div class="form-group">
                                            <div class="col-md-2"></div>
                                            <label class="col-md-2 control-label"><%=addBankWire_UnPaid_Amount%><br>
                                            </label>

                                            <div class="col-md-4">
                                                <input type="text" class="form-control" name="unamt"
                                                       value="<%=unPaidAmount%>" placeHolder="0.00" size="9">
                                            </div>
                                            <div class="col-md-6"></div>
                                        </div>

                                        <div class="form-group">
                                            <div class="col-md-2"></div>
                                            <label class="col-md-2 control-label"><%=addBankWire_RR_Release_Date%><br>
                                            </label>

                                            <div class="col-md-4">
                                                <input type="text" size="16" name="rrdate"
                                                       class="datepicker form-control"
                                                       value="<%=ESAPI.encoder().encodeForHTMLAttribute (rrDate)%>"
                                                       readonly="readonly"
                                                       style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                                            </div>
                                            <div class="col-md-6"></div>
                                        </div>
                                        <%
                                            if ("Y".equalsIgnoreCase(settlementCycleVO.getIsTransactionFileAvailable()))
                                            {%>
                                        <div class="form-group ">
                                            <div class="col-md-2"></div>
                                            <label class="col-md-2 control-label"><%=addBankWire_Transaction_File%><br>
                                            </label>

                                            <div class="col-md-8">
                                                <input type="file" class="btn btn-default" name="file" value=""
                                                       onchange="return check()">
                                            </div>
                                            <div class="col-md-6"></div>
                                        </div>
                                        <%
                                            }
                                        %>
                                        <div class="form-group col-md-12 has-feedback">
                                            <div class="col-md-6" align="right">
                                                <button type="submit" value="back" name="action" class="btn btn-default"
                                                        id="back" style="display: -webkit-box;"><i class="fa fa-save"><%=addBankWire_Back%></i>
                                                </button>
                                            </div>
                                            <div class="col-md-6" align="left">
                                                <button type="submit" value="next" name="action" class="btn btn-default"
                                                        id="next" onclick="return check()"
                                                        style="display: -webkit-box;"><i class="fa fa-save"><%=addBankWire_Create_Bank_Wire%>
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