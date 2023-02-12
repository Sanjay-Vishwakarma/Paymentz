<%@ page errorPage="error.jsp"
         import="com.directi.pg.Functions,
                 com.directi.pg.TransactionEntry,
                 com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>

<%@ page import="com.directi.pg.core.paymentgateway.CredoraxPaymentGateway" %>
<%@ page import="com.directi.pg.core.paymentgateway.EcorePaymentGateway" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.payment.sbm.core.SBMPaymentGateway" %>
<%@ page import="org.owasp.esapi.*" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ include file="Top.jsp" %>

<%
    String company = ESAPI.encoder().encodeForHTML((String) session.getAttribute("company"));
    session.setAttribute("submit", "Capture");
%>
<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title><%=company%> Merchant Transaction Management > capture</title>

    <script language="javascript">

        function cancelTrans(icicitransid, ctoken, accountid, terminalbuffer, currency)
        {
            //alert(icicitransid)
            if (confirm("Do u really want to cancel this transaction.\n\n Tracking : " + icicitransid))
            {
                var e = eval("document.f1.mybutton" + icicitransid)
                e.disabled = true;
                //document.f1.mybutton.disabled=true;
                document.location.href = "/merchant/servlet/CancelTransaction?ctoken=" + ctoken + "&icicitransid=" + icicitransid + "&accountid=" + accountid + "&terminalbuffer=" + terminalbuffer + "&currency=" + currency;
            }

        }
        function partialCapter(accountid, trackingid, ctoken, terminalbuffer, terminal, auth)
        {
            //alert(icicitransid)
            if (confirm("Do u really want to Partial Capture this transaction.\n\n Tracking : " + trackingid))
            {
                var e = eval("document.f1.mybutton" + trackingid)
                e.disabled = true;
                //document.f1.mybutton.disabled=true;
                document.location.href = "/merchant/servlet/Pod?ctoken=" + ctoken + "&bank=" + accountid + "&partialCapture='true'&trackingid=" + trackingid + "&terminalbuffer=" + terminalbuffer + "&terminalid=" + terminal + "&paymentid=" + auth;
            }

        }
        function ToggleAll(checkbox)
        {
            flag = checkbox.checked;
            var checkboxes = document.getElementsByName("trackingid");
            var total_boxes = checkboxes.length;

            for (i = 0; i < total_boxes; i++)
            {
                checkboxes[i].checked = flag;
            }
            function MakeSelect2()
            {
                $('select').select2();
                $('.dataTables_filter').each(function ()
                {
                    $(this).find('label input[type=text]').attr('placeholder', 'Search');
                });
            }

            $(document).ready(function ()
            {
                // Load Datatables and run plugin on tables
                LoadDataTablesScripts(AllTables);
                // Add Drag-n-Drop feature

                WinMove();
            });
        }

    </script>

    <style type="text/css">

        @media (max-width: 640px) {

            .icheckbox_square-aero {
                background-color: white !important;
                border: 1px solid #29aaa1 !important;
            }
        }

    </style>

</head>
<body>

<%

    String uId = "";
    if (session.getAttribute("role").equals("submerchant"))
    {
        uId = (String) session.getAttribute("userid");
    }
    else
    {
        uId = (String) session.getAttribute("merchantid");
    }

    TransactionEntry transactionentry = (TransactionEntry) session.getAttribute("transactionentry");
    String bank = (String) session.getAttribute("bank");
    Hashtable bankhash = transactionentry.getBankHash((String) session.getAttribute("merchantid"));

    String terminalid = "";
    String trackingId = "";
    String paymentId = "";
    trackingId = request.getParameter("trackingid");
    paymentId = request.getParameter("paymentid");
    terminalid = request.getParameter("terminalid");
    String pTerminalBuffer = request.getParameter("terminalbuffer");

    String str = "";

    Hashtable gatewayHash = GatewayTypeService.getGatewayTypes();
    String gateway = Functions.checkStringNull(request.getParameter("gateway"));
    if (gateway == null)
    {
        gateway = "";
    }

    if (trackingId != null) str = str + "trackingid=" + trackingId;
    else trackingId = "";
    if (paymentId != null) str = str + "paymentid=" + paymentId;
    else paymentId = "";
    if (terminalid != null) str = str + "terminalid=" + terminalid;
    else
        terminalid = "";
    if (pTerminalBuffer != null) str = str + "terminalbuffer=" + pTerminalBuffer;
    else
        pTerminalBuffer = "";

    if (request.getAttribute("terminalid") != null)
    {
        terminalid = request.getAttribute("terminalid").toString();
    }

    int pageno = 1;
    int pagerecords = 30;
    try
    {
        pageno = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno", request.getParameter("SPageno"), "Numbers", 3, true), 1);
        pagerecords = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord", request.getParameter("SRecords"), "Numbers", 3, true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
    }
    catch (ValidationException ex)
    {
        pageno = 1;
        pagerecords = 30;
    }

    str = str + "&SRecords=" + pagerecords;
    str = str + "&bank=" + bank;

    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String pod_merchant1 = StringUtils.isNotEmpty(rb1.getString("pod_merchant1")) ? rb1.getString("pod_merchant1") : "Merchant Proof Of Delivery";
    String pod_Tracking_ID = StringUtils.isNotEmpty(rb1.getString("pod_Tracking_ID")) ? rb1.getString("pod_Tracking_ID") : "Tracking ID";
    String pod_Terminal_ID = StringUtils.isNotEmpty(rb1.getString("pod_Terminal_ID")) ? rb1.getString("pod_Terminal_ID") : "Terminal ID";
    String pod_All = StringUtils.isNotEmpty(rb1.getString("pod_All")) ? rb1.getString("pod_All") : "All";
    String pod_No_Terminals_Allocated = StringUtils.isNotEmpty(rb1.getString("pod_No_Terminals_Allocated")) ? rb1.getString("pod_No_Terminals_Allocated") : "No Terminals Allocated";
    String pod_Search = StringUtils.isNotEmpty(rb1.getString("pod_Search")) ? rb1.getString("pod_Search") : "Search";
    String pod_Capture_Data = StringUtils.isNotEmpty(rb1.getString("pod_Capture_Data")) ? rb1.getString("pod_Capture_Data") : "Capture Data";
    String pod_Date1 = StringUtils.isNotEmpty(rb1.getString("pod_Date1")) ? rb1.getString("pod_Date1") : "Date";
    String pod_Tracking_ID1 = StringUtils.isNotEmpty(rb1.getString("pod_Tracking_ID1")) ? rb1.getString("pod_Tracking_ID1") : "Tracking ID";
    String pod_Order_ID = StringUtils.isNotEmpty(rb1.getString("pod_Order_ID")) ? rb1.getString("pod_Order_ID") : "Order ID";
    String pod_Pay_Mode = StringUtils.isNotEmpty(rb1.getString("pod_Pay_Mode")) ? rb1.getString("pod_Pay_Mode") : "Pay Mode";
    String pod_Brand = StringUtils.isNotEmpty(rb1.getString("pod_Brand")) ? rb1.getString("pod_Brand") : "Brand";
    String pod_Amount = StringUtils.isNotEmpty(rb1.getString("pod_Amount")) ? rb1.getString("pod_Amount") : "Amount";
    String pod_Currency = StringUtils.isNotEmpty(rb1.getString("pod_Currency")) ? rb1.getString("pod_Currency") : "Currency";
    String pod_Status = StringUtils.isNotEmpty(rb1.getString("pod_Status")) ? rb1.getString("pod_Status") : "Status";
    String pod_Terminal = StringUtils.isNotEmpty(rb1.getString("pod_Terminal")) ? rb1.getString("pod_Terminal") : "Terminal";
    String pod_POD = StringUtils.isNotEmpty(rb1.getString("pod_POD")) ? rb1.getString("pod_POD") : "POD (Shipment";
    String pod_Tracking = StringUtils.isNotEmpty(rb1.getString("pod_Tracking")) ? rb1.getString("pod_Tracking") : "Tracking";
    String pod_No = StringUtils.isNotEmpty(rb1.getString("pod_No")) ? rb1.getString("pod_No") : "No)*";
    String pod_Shipment = StringUtils.isNotEmpty(rb1.getString("pod_Shipment")) ? rb1.getString("pod_Shipment") : "Shipment Tracker Site*";
    String pod_Action = StringUtils.isNotEmpty(rb1.getString("pod_Action")) ? rb1.getString("pod_Action") : "Action";
    String pod_please = StringUtils.isNotEmpty(rb1.getString("pod_please")) ? rb1.getString("pod_please") : "* Please do not insert the POD value as N/A or NA.";
    String pod_capture = StringUtils.isNotEmpty(rb1.getString("pod_capture")) ? rb1.getString("pod_capture") : "Capture AND/OR Submit Shipping Details";
    String pod_Showing_Page = StringUtils.isNotEmpty(rb1.getString("pod_Showing_Page")) ? rb1.getString("pod_Showing_Page") : "Showing Page";
    String pod_of = StringUtils.isNotEmpty(rb1.getString("pod_of")) ? rb1.getString("pod_of") : "of";
    String pod_records = StringUtils.isNotEmpty(rb1.getString("pod_records")) ? rb1.getString("pod_records") : "records";
    String pod_Sorry = StringUtils.isNotEmpty(rb1.getString("pod_Sorry")) ? rb1.getString("pod_Sorry") : "Sorry";
    String pod_No_records_found = StringUtils.isNotEmpty(rb1.getString("pod_No_records_found")) ? rb1.getString("pod_No_records_found") : "No records found.";
    String pod_Filter = StringUtils.isNotEmpty(rb1.getString("pod_Filter")) ? rb1.getString("pod_Filter") : "Filter";
    String pod_provide = StringUtils.isNotEmpty(rb1.getString("pod_provide")) ? rb1.getString("pod_provide") : "Please provide the TrackingID or TerminalID for Captured Transaction.";
    String pod_Cancel = StringUtils.isNotEmpty(rb1.getString("pod_Cancel")) ? rb1.getString("pod_Cancel") : "Cancel";
    String pod_Partial_Capture = StringUtils.isNotEmpty(rb1.getString("pod_Partial_Capture")) ? rb1.getString("pod_Partial_Capture") : "Partial Capture";
    String pod_page_no=StringUtils.isNotEmpty(rb1.getString("pod_page_no"))?rb1.getString("pod_page_no"):"Page number";
    String pod_total_no_of_records=StringUtils.isNotEmpty(rb1.getString("pod_total_no_of_records"))?rb1.getString("pod_total_no_of_records"):"Total number of records";
%>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">

            <div class="row">

                <div class="col-sm-12 portlets ui-sortable">

                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> <%=pod_merchant1%>
                            </strong></h2>

                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <form name="form" method="post" action="/merchant/servlet/Pod?ctoken=<%=ctoken%>">
                            <%
                                String terminalCurrency = "";
                                StringBuffer terminalBuffer = new StringBuffer();
                                String error = (String) request.getAttribute("error");
                                /*terminalid = "";*/
                                if (error != null)
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                                }
                                /*if(request.getAttribute("terminalid")!=null)
                                {
                                    terminalid = (String) request.getAttribute("terminalid");
                                }*/

                            %>
                            <div class="widget-content padding">
                                <div id="horizontal-form">

                                    <div class="form-group col-md-3 has-feedback">
                                        <label><%=pod_Tracking_ID%>
                                        </label>
                                        <input type="text" name="trackingid" class="form-control"
                                               placeholder="Tracking ID"
                                               value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingId)%>">
                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label><%=pod_Terminal_ID%>
                                        </label>
                                        <select size="1" name="terminalid" class="form-control">
                                            <%
                                                terminalBuffer.append("(");
                                                TerminalManager terminalManager = new TerminalManager();
                                                List<TerminalVO> terminalVOList = terminalManager.getMemberandUserTerminalList(uId, String.valueOf(session.getAttribute("role")));
                                                if (terminalVOList.size() > 0)
                                                {
                                            %>
                                            <option value=""><%=pod_All%>
                                            </option>
                                            <%
                                                for (TerminalVO terminalVO : terminalVOList)
                                                {
                                                    String str1 = "";
                                                    String active = "";
                                                    if (terminalVO.getIsActive().equalsIgnoreCase("Y"))
                                                    {
                                                        active = "Active";
                                                    }
                                                    else
                                                    {
                                                        active = "InActive";
                                                    }
                                                    if (terminalid.equals(terminalVO.getTerminalId()))
                                                    {
                                                        terminalCurrency = terminalVO.getCurrency();
                                                        str1 = "selected";
                                                    }
                                                    if (terminalBuffer.length() != 0 && terminalBuffer.length() != 1)
                                                    {
                                                        terminalBuffer.append(",");
                                                    }
                                                    terminalBuffer.append(terminalVO.getTerminalId());
                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO.getTerminalId())%>" <%=str1%>><%=ESAPI.encoder().encodeForHTML(terminalVO.getTerminalId() + "-" + terminalVO.getPaymentName() + "-" + terminalVO.getCardType() + "-" + terminalVO.getCurrency() + "-" + active)%>
                                            </option>
                                            <%
                                                }
                                                terminalBuffer.append(")");
                                            }
                                            else
                                            {
                                            %>
                                            <option value="NoTerminals"><%=pod_No_Terminals_Allocated%>
                                            </option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </div>
                                    <%-- <div class="form-group col-md-3 has-feedback">
                                         <label>Auth Code</label>
                                         <input type=text name="paymentid" class="form-control" maxlength="100"  placeholder="Auth code" value="<%=ESAPI.encoder().encodeForHTMLAttribute(paymentId)%>">
                                     </div>
 --%>
                                    <div class="form-group col-md-3 has-feedback">
                                        <%--<label style="color: transparent;">Path</label>--%>
                                        <label>&nbsp;</label>
                                        <button type="submit" name="B1" class="btn btn-default" style="display:block;">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;<%=pod_Search%>
                                        </button>
                                    </div>

                                    <input type="hidden" value="<%=terminalBuffer.toString()%>" name="terminalbuffer">
                                    <input type="hidden" name="currency" value=<%=terminalCurrency%>>
                                    <input type="hidden" name="terminalid" value=<%=terminalid%>>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <form name="f1" method="post" action="/merchant/servlet/PodSubmit">
                <input type="hidden" value="<%=terminalBuffer.toString()%>" name="terminalbuffer">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">

                <div class="row reporttable">
                    <div class="col-md-12">
                        <div class="widget">
                            <div class="widget-header">
                                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=pod_Capture_Data%>
                                </strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <div class="widget-content padding" style="overflow-y: auto;">
                                <%

                                    Hashtable hash = (Hashtable) request.getAttribute("poddetails");

                                    //out.println(hash);
                                    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                                    str = "";
                                    Hashtable temphash = null;
                                    pageno = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
                                    pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), Integer.parseInt((String) application.getAttribute("PODNOOFRECORDSPERPAGE")));
                                    str = str + "&terminalid=" + terminalid;
                                    str = str + "&SRecords=" + pagerecords;
                                    str = str + "&ctoken=" + ctoken;
                                    str = str + "&terminalid=" + terminalid;
                                    str = str + "&terminalbuffer=" + pTerminalBuffer;
                                    str = str + "&currency=" + terminalCurrency;
                                    int records = 0;
                                    int totalrecords = 0;
                                    int currentblock = 1;

                                    try
                                    {
                                        records = Integer.parseInt((String) hash.get("records"));
                                        totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
                                        currentblock = Integer.parseInt(request.getParameter("currentblock"));
                                    }
                                    catch (Exception ex)
                                    {
                                    }

                                    String style = "class=tr1";
                                    String ext = "light";


                                    if (!"Capture".equals(request.getParameter("submit")))
                                    {
                                        if (records > 0)
                                        {
                                            session.setAttribute("poddetails", hash);
                                %>
                                <table id="myTable" class="display table table-striped table-bordered"
                                       style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead style="color: white;">
                                    <tr>
                                        <td align="center"><input type="checkbox" onclick="ToggleAll(this);"
                                                                  name="alltrans"></td>
                                        <td width="50%" style="text-align: center"><%=pod_Date1%>
                                        </td>
                                        <td style="text-align: center"><%=pod_Tracking_ID1%>
                                        </td>
                                        <td style="text-align: center"><%=pod_Order_ID%>
                                        </td>
                                        <td style="text-align: center"><%=pod_Pay_Mode%>
                                        </td>
                                        <td style="text-align: center"><%=pod_Brand%>
                                        </td>
                                        <td style="text-align: center"><%=pod_Amount%>
                                        </td>
                                        <td style="text-align: center"><%=pod_Currency%>
                                        </td>
                                        <td style="text-align: center"><%=pod_Status%>
                                        </td>
                                        <td style="text-align: center"><%=pod_Terminal%>
                                        </td>
                                        <td style="text-align: center"><%=pod_POD%>&nbsp;<%=pod_Tracking%>
                                            &nbsp;<%=pod_No%>
                                        </td>
                                        <td style="text-align: center"><%=pod_Shipment%>
                                        </td>
                                        <td colspan="2" style="text-align: center"><%=pod_Action%>
                                        </td>

                                    </tr>
                                    </thead>
                                    <%

                                        ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

                                        for (int pos = 1; pos <= records; pos++)
                                        {
                                            String id = Integer.toString(pos);

                                            int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);
                                            style = "class=\"tr" + pos % 2 + "\"";
                                            temphash = (Hashtable) hash.get(id);

                                            String date = Functions.convertDtstamptoDate((String) temphash.get("dtstamp"));
                                            String tempcurrentstatus = (String) temphash.get("currentstatus");
                                            String tempprovstatus = (String) temphash.get("provstatus");
                                            ctoken = request.getParameter("ctoken");
                                            String productname = (String) temphash.get("productkey");
                                            String path = productname + "/";
                                            ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                                            out.println("<tr>");
                                    %>
                                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                    <tbody>

                                    <%

                                        String paymodeid = (String) temphash.get("paymodeid");
                                        String cardtypeid = (String) temphash.get("cardtype");
                                        String amount = (String) temphash.get("amount");
                                        String currency = (String) temphash.get("currency");
                                        Functions functions = new Functions();
                                        if ("JPY".equalsIgnoreCase(currency))
                                        {
                                            amount = functions.printNumber(Locale.JAPAN, amount);
                                        }
                                        else if ("EUR".equalsIgnoreCase(currency))
                                        {
                                            amount = functions.printNumber(Locale.FRANCE, amount);
                                        }
                                        else if ("GBP".equalsIgnoreCase(currency))
                                        {
                                            amount = functions.printNumber(Locale.UK, amount);
                                        }
                                        else if ("USD".equalsIgnoreCase(currency))
                                        {
                                            amount = functions.printNumber(Locale.US, amount);
                                        }
                                        out.println("<td " + style + " align=\"center\">&nbsp;<input type=\"checkbox\" name=\"trackingid\" value=\"" + temphash.get("trackingid") + "\"></td>");
                                        out.println("<td style=\"text-align: center\" data-label=\"Date\">&nbsp;" + date + "</td>");
                                        out.println("<td style=\"text-align: center\" data-label=\"Transaction ID\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid")) + "</td>");
                                        out.println("<td style=\"text-align: center\" data-label=\"Description\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("description")) + "</td>");


                                        if (paymodeid == null || paymodeid.equals(""))
                                        {
                                            out.println("<td style=\"text-align: center\">&nbsp;" + "-" + "</td>");
                                        }
                                        else
                                        {

                                            out.println("<td style=\"text-align: center\" data-label=\"Pay Mode\" align=\"center\">&nbsp;" + paymodeid + "</td>");
                                        }
                                        if (cardtypeid == null || cardtypeid.equals(""))
                                        {
                                            out.println("<td style=\"text-align: center\">&nbsp;" + "-" + "</td>");
                                        }
                                        else
                                        {
                                            out.println("<td style=\"text-align: center\" data-label=\"Card Type\" align=\"center\">&nbsp; <img src=\"/merchant/images/cardtype/" + ESAPI.encoder().encodeForHTML(cardtypeid) + ".png\" style=\"height:22;\"></td>");


                                        }
                                        out.println("<td style=\"text-align: center\" data-label=\"Amount\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(amount) + "</td>");
                                        out.println("<td " + style + " align=\"center\" data-label=\"Currency\"><input type=hidden name=currency value=" + temphash.get("currency") + ">&nbsp;" + ESAPI.encoder().encodeForHTML(currency) + "</td>");
                                        out.println("<td style=\"text-align: center\" data-label=\"Status\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("status")) + "</td>");
                                        out.println("<td style=\"text-align: center\" data-label=\"Terminal\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("terminalid")) + "</td>");
                                        out.println("<td style=\"text-align: center\" data-label=\"POD (Shipment Tracking No)*\" align=\"center\"><center><input type=text maxlength=\"25\" name=podNO_" + ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid")) + " value=\"\" class=\"form-control\" style=\"width: inherit;\"></center><input type=hidden name= accountid_" + ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid")) + " value=\"" + (String) temphash.get("accountid") + "\"</td>");
                                        out.println("<td style=\"text-align: center;\" data-label=\"Shipment Tracker Site*\" align=\"center\"><center><input type=text maxlength=\"40\" name=podSITE_" + ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid")) + " value=\"\" class=\"form-control\" style=\"width: inherit;\"></center></td>");

                                        String accountId = (String) temphash.get("accountid");
                                        String fromtype = GatewayAccountService.getGatewayAccount(accountId).getGateway();

                                        if (temphash.get("status").equals("authsuccessful") || (temphash.get("status").equals("capturesuccess") && SBMPaymentGateway.GATEWAY_TYPE.equals(fromtype)))
                                        {
                                            out.println("<td " + style + " data-label=\"Action\" align=\"center\"><input type=button class=\"buttonpod1hide btn btn-default\" name=mybutton" + (String) temphash.get("trackingid") + " value=" + pod_Cancel + " class=\"buttonpod1 btn btn-default\" onClick=\"cancelTrans('" + (String) temphash.get("trackingid") + "','" + ctoken + "','" + (String) temphash.get("accountid") + "','" + terminalBuffer + "','" + temphash.get("currency") + "');\" ></td>");
                                        }
                                        else
                                        {
                                            out.println("<td " + style + " data-label=\"Action\" align=\"center\"><input type=button class=\"buttonpod1hide btn btn-default\" name=mybutton" + (String) temphash.get("trackingid") + " value=" + pod_Cancel + " class=\"buttonpod1 btn btn-default\" onClick=\"cancelTrans('" + (String) temphash.get("trackingid") + "','" + ctoken + "','" + (String) temphash.get("accountid") + "','" + terminalBuffer + "','" + temphash.get("currency") + "');\"  disabled></td>");
                                        }


                                        if (EcorePaymentGateway.GATEWAY_TYPE.equals(fromtype) || CredoraxPaymentGateway.GATEWAY_TYPE.equals(fromtype))
                                        {
                                    %>
                                    <%--
                                                                                    //out.println("<td " + style + ">&nbsp;<font face=\"arial,verdana,helvetica\"  size=\"1\" ><a href=\"#\" onclick=\"(alert('Partial capture is not supported on your account'))\"> <font class=\"textb\"> Partial Capture </font></a></font></td>");
                                    --%>
                                    <td style="text-align: center" data-label="Action" align="center"><input type=button
                                                                                                             class="buttonpod2hide btn btn-default"
                                                                                                             name="mybutton<%=(String) temphash.get("trackingid")%>" value="<%=pod_Partial_Capture%>"
                                                                                                             disabled>
                                    </td>
                                    <%
                                    }
                                    else
                                    {
                                        //out.println("<td " + style + ">&nbsp;<font face=\"arial,verdana,helvetica\"  size=\"1\" ><a href=\"/merchant/servlet/Pod?bank="+ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("accountid"))+"&partialCapture=true &trackingid=" + ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("trackingid")) + "&ctoken="+ctoken+"\"> <font class=\"textb\"> Partial Capture </font></a></font></td>");
                                        if (temphash.get("status").equals("authsuccessful"))
                                        {
                                    %>
                                    <td style="text-align: center" data-label="Action" align="center"><input type=button
                                                                                                             class="buttonpod2 btn btn-default"
                                                                                                             name="mybutton<%=(String) temphash.get("trackingid")%>"
                                                                                                             value="<%=pod_Partial_Capture%>"
                                                                                                             onClick="partialCapter('<%=(String)temphash.get("accountid")%>','<%=(String) temphash.get("trackingid")%>','<%=ctoken%>','<%=terminalBuffer%>','<%=request.getAttribute("terminal")%>','<%=request.getAttribute("paymentid")%>');">
                                    </td>

                                    <%
                                    }
                                    else
                                    {
                                    %>

                                    <td style="text-align: center" data-label="Action" align="center"><input type=button
                                                                                                             class="buttonpod2hide btn btn-default"
                                                                                                             name="mybutton<%=(String) temphash.get("trackingid")%>"
                                                                                                             value="<%=pod_Partial_Capture%>"
                                                                                                             disabled>
                                    </td>
                                    <%
                                                }
                                            }
                                        }
                                    %>


                                    </tbody>
                                    <%--<thead style="color: white;">
                                    <tr>
                                        <td align="center">Total Records : <%=totalrecords%></td>
                                        <td  align="center">Page No <%=pageno%></td>
                                        <td  >&nbsp;</td>
                                        <td  >&nbsp;</td>
                                        <td  >&nbsp;</td>
                                        <td  >&nbsp;</td>
                                        <td  >&nbsp;</td>
                                        <td  >&nbsp;</td>
                                        <td  >&nbsp;</td>
                                        <td  >&nbsp;</td>
                                        <td  >&nbsp;</td>
                                        <td  >&nbsp;</td>
                                        <td  >&nbsp;</td>
                                        <td  >&nbsp;</td>
                                    </tr>
                                    </thead>--%>
                                </table>
                                <table width="100%">
                                    <tr>
                                        <td class="textb" align="center"><%=pod_please%>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="textb">&nbsp;</td>
                                    </tr>
                                    <tr>
                                        <td width="15%" align="center">
                                            <button type="submit" class="btn btn-default"
                                                    value="Capture AND/OR Submit Shipping Details">
                                                <span><i class="fa fa-clock-o"></i></span>
                                                &nbsp;&nbsp;<%=pod_capture%>
                                            </button>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td class="textb">&nbsp;</td>

                                    </tr>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
            <%
                int TotalPageNo;
                if(totalrecords%pagerecords!=0)
                {
                    TotalPageNo =totalrecords/pagerecords+1;
                }
                else
                {
                    TotalPageNo=totalrecords/pagerecords;
                }
            %>

            <div id="showingid"><strong><%=pod_page_no%> <%=pageno%> of  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
            <div id="showingid"><strong><%=pod_total_no_of_records%>   <%=totalrecords%> </strong></div>

            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="Pod"/>
                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                <jsp:param name="orderby" value=""/>
            </jsp:include>
            <%
                    }
                    else
                    {
                        if (request.getAttribute("message") != null)
                        {
                            out.println(Functions.NewShowConfirmation1(pod_Sorry, ESAPI.encoder().encodeForHTML((String) request.getAttribute("message"))));
                        }
                        else
                            out.println(Functions.NewShowConfirmation1(pod_Sorry, pod_No_records_found));
                    }
                }
                else /*if("Capture".equals(request.getParameter("submit")))*/
                {
                    out.println(Functions.NewShowConfirmation1(pod_Filter, pod_provide));
                }
            %>

        </div>
    </div>
</div>

</body>
</html>