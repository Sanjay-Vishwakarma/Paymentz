<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.*" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.List" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="java.util.Locale" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ include file="Top.jsp" %>
<%@include file="payoutPercentage.jsp"%>
<%String company = (String)session.getAttribute("company");
    session.setAttribute("submit","Payout");
%>
<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <title><%=company%> Merchant Transaction Management >Payout</title>
    <script language="javascript">
        function isint(form){
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
        function DoPayout(accountid,id,ctoken,terminal,terminalBuffer,desc,auth,pageNo,records)
        {
            if (confirm("Do you really want to payout this transaction."))
            {
                document.location.href = "GetPayoutDetails?ctoken="+ctoken+"&icicitransid="+id+"&accountid="+accountid+"&terminalid="+terminal+"&terminalbuffer="+terminalBuffer+"&SDescription="+desc+"&paymentid="+auth+"&SPageno="+pageNo+"&SRecords="+records ;
            }
        }
    </script>
    <%--  <style>
         .avail{
             float:right;
             padding-left: 10px;

         }
          .btn btn-default av{
              padding-left: 50px;
          }

      </style>--%>
</head>
<script>
</script>
<body class="pace-done widescreen fixed-left-void">
<%
    String uId = "";
    String merchantid = "";
    if(session.getAttribute("role").equals("submerchant")){
        uId = (String) session.getAttribute("userid");
        merchantid = (String) session.getAttribute("merchantid");
    }
    else{
        uId = (String) session.getAttribute("merchantid");
    }

    String bank = "";
    String str="";
    if(session.getAttribute("bank") != null)
    {
        bank = (String) session.getAttribute("bank");
    }

    String terminalid           = Functions.checkStringNull(request.getParameter("terminalid"))==null?"":request.getParameter("terminalid");
    String pTerminalBuffer      = request.getParameter("terminalbuffer");

    String sTrackingid          = Functions.checkStringNull(request.getParameter("STrakingid"))==null?"":request.getParameter("STrakingid");
    String sAuthCode            = Functions.checkStringNull(request.getParameter("paymentid"))==null?"":request.getParameter("paymentid");
    String sDesc                = Functions.checkStringNull(request.getParameter("SDescription"))==null?"":request.getParameter("SDescription");

    if(request.getAttribute("terminalid")!=null)
    {
        terminalid = request.getAttribute("terminalid").toString();
    }

    int pageno      = 1;
    int pagerecords = 15;
    try
    {
        pageno      = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Spageno",request.getParameter("SPageno"),"Numbers",3,true), 1);
        pagerecords = Functions.convertStringtoInt(ESAPI.validator().getValidInput("Srecord",request.getParameter("SRecords"),"Numbers",3,true), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));
    }
    catch (ValidationException ex)
    {
        pageno = 1;
        pagerecords = 15;
    }

    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String payoutlist_transaction_payout    = StringUtils.isNotEmpty(rb1.getString("payoutlist_transaction_payout"))?rb1.getString("payoutlist_transaction_payout"): "Transaction Payout";
    String payoutlist_tracking_id           = StringUtils.isNotEmpty(rb1.getString("payoutlist_tracking_id"))?rb1.getString("payoutlist_tracking_id"): "Tracking ID";
    String payoutlist_terminalid            = StringUtils.isNotEmpty(rb1.getString("payoutlist_terminalid"))?rb1.getString("payoutlist_terminalid"): "Terminal ID";
    String payoutlist_all                   = StringUtils.isNotEmpty(rb1.getString("payoutlist_all"))?rb1.getString("payoutlist_all"): "All";
    String payoutlist_no_terminal_allocated = StringUtils.isNotEmpty(rb1.getString("payoutlist_no_terminal_allocated"))?rb1.getString("payoutlist_no_terminal_allocated"): "No Terminal Allocated";
    String payoutlist_order_id              = StringUtils.isNotEmpty(rb1.getString("payoutlist_order_id"))?rb1.getString("payoutlist_order_id"): "Order ID";
    String payoutlist_search                = StringUtils.isNotEmpty(rb1.getString("payoutlist_search"))?rb1.getString("payoutlist_search"): "Search";
    String payoutlist_payout_list           = StringUtils.isNotEmpty(rb1.getString("payoutlist_payout_list"))?rb1.getString("payoutlist_payout_list"): "Payout List";
    String payoutlist_tracking_id1          = StringUtils.isNotEmpty(rb1.getString("payoutlist_tracking_id1"))?rb1.getString("payoutlist_tracking_id1"): "Tracking ID";
    String payoutlist_transaction_id        = StringUtils.isNotEmpty(rb1.getString("payoutlist_transaction_id"))?rb1.getString("payoutlist_transaction_id"): "Transaction ID";
    String payoutlist_order_id1             = StringUtils.isNotEmpty(rb1.getString("payoutlist_order_id1"))?rb1.getString("payoutlist_order_id1"): "Order ID";
    String payoutlist_captured_amount       = StringUtils.isNotEmpty(rb1.getString("payoutlist_captured_amount"))?rb1.getString("payoutlist_captured_amount"): "Captured Amount";
    String payoutlist_currency              = StringUtils.isNotEmpty(rb1.getString("payoutlist_currency"))?rb1.getString("payoutlist_currency"): "Currency";
    String payoutlist_terminal              = StringUtils.isNotEmpty(rb1.getString("payoutlist_terminal"))?rb1.getString("payoutlist_terminal"): "Terminal";
    String payoutlist_showing_page_records  = StringUtils.isNotEmpty(rb1.getString("payoutlist_showing_page_records"))?rb1.getString("payoutlist_showing_page_records"): "Showing Page of records";
    String payoutlist_sorry                 = StringUtils.isNotEmpty(rb1.getString("payoutlist_sorry"))?rb1.getString("payoutlist_sorry"): "Sorry";
    String payoutlist_no_records_found      = StringUtils.isNotEmpty(rb1.getString("payoutlist_no_records_found"))?rb1.getString("payoutlist_no_records_found"): "No records found.";
    String payoutlist_filter                = StringUtils.isNotEmpty(rb1.getString("payoutlist_filter"))?rb1.getString("payoutlist_filter"): "Filter";
    String payoutlist_trackingid_terminalid = StringUtils.isNotEmpty(rb1.getString("payoutlist_trackingid_terminalid"))?rb1.getString("payoutlist_trackingid_terminalid"): "Please provide the trackingID or TerminalID for payout List.";
    String payoutlist_Payout                = StringUtils.isNotEmpty(rb1.getString("payoutlist_Payout"))?rb1.getString("payoutlist_Payout"): "Payout";
    String payoutlist_page_no               = StringUtils.isNotEmpty(rb1.getString("payoutlist_page_no"))?rb1.getString("payoutlist_page_no"):"Page number";
    String payoutlist_total_no_of_records   = StringUtils.isNotEmpty(rb1.getString("payoutlist_total_no_of_records"))?rb1.getString("payoutlist_total_no_of_records"):"Total number of records";
    String payoutlist_Balance_Cal   = StringUtils.isNotEmpty(rb1.getString("payoutlist_Balance_Cal"))?rb1.getString("payoutlist_Balance_Cal"):"Balance Cal";
    String payoutlist_Bulk_Payout_Upload  = StringUtils.isNotEmpty(rb1.getString("payoutlist_Bulk_Payout_Upload"))?rb1.getString("payoutlist_Bulk_Payout_Upload"):"Bulk Payout Upload";
%>
<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <%--<form name="form" method="post" action="/merchant/servlet/PayoutList">--%>
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">
                        <div class="widget-header transparent" style="width:100% ;">
                            <div style="float: left;" width="50%">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=payoutlist_transaction_payout%></strong></h2>
                            </div>

                            <%-- <%
                                 String payoutsum ="0.00";
                                 if(request.getAttribute("payoutsumm") != null){
                                     payoutsum = (String)request.getAttribute("payoutsumm");
                                 }
                             %>
                             <div style="margin-left: 75%;padding-top: 7px;">
                                 <form action="/merchant/servlet/PayoutList?ctoken=<%=ctoken%>" method="POST">
                                     <input  type="submit" name="submit" value="Check Balance" style="height: 31px;width: 118px;" class="btn btn-default">
                                     <input  type="hidden" name="checkBalance" value="checkBalance" class="btn btn-default">
                                     <label style="color: black;font-family: Open Sans;font-size: 14px !important;font-weight: 600;">Available Balance : <%=payoutsum%> </label>
                                 </form>
                             </div>--%>

                            <div class="pull-right"style="padding: 10px;">
                                <div class="btn-group">
                                    <form action="/merchant/balanceCalculation.jsp?ctoken=<%=ctoken%>" method="POST" style="margin:0;">
                                        <button type="hidden" name="submit" <%--value="Balance Calc."--%> class="btn btn-default" style="display: -webkit-box; margin-right: 0px;"><%--<i class="fa fa-upload" aria-hidden="true"></i>--%>&nbsp;&nbsp;<%=payoutlist_Balance_Cal%></button>
                                    </form>
                                </div>



                                <div class="btn-group">
                                    <form action="/merchant/bulkPayoutUpload.jsp?ctoken=<%=ctoken%>" method="POST" style="margin:0;">
                                        <button type="hidden" name="submit" value="Bulk Payout Upload" class="btn btn-default" style="display: -webkit-box; margin-right: 0px;"><i class="fa fa-upload" aria-hidden="true"></i>&nbsp;&nbsp;<%=payoutlist_Bulk_Payout_Upload%></button>
                                    </form>
                                </div>

                                <%
                                    String payoutsum        = "0.00";
                                    String percentage       = "0.6";
                                    String payoutAmount     = "0.00";
                                    if(merchantid == null || merchantid.equals("null") || merchantid.trim().equals(""))
                                    {
                                        merchantid = uId;
                                    }
                                    String totalPayoutAmount     = merchantDAO.getTotalPayoutAmount(merchantid);
                                    String availableBalance = "0.00";


                                    DecimalFormat decimalFormat = new DecimalFormat("0.00");
                                    if(request.getAttribute("payoutsumm") != null){
                                        payoutsum = (String)request.getAttribute("payoutsumm");

                                        if(request.getAttribute("payoutAmount") != null){
                                            payoutAmount = (String)request.getAttribute("payoutAmount");
                                        }

                                        if(payoutBalPercentage.containsKey(uId)){
                                            percentage =(String) payoutBalPercentage.get(uId);
                                        }
                                        payoutsum = ((Double.parseDouble(payoutsum) * Double.parseDouble(percentage))) - Double.parseDouble(payoutAmount) +"";


                                        if(payoutsum.contains("-"))
                                        {
                                            availableBalance = String.valueOf(Double.valueOf(totalPayoutAmount) + Double.valueOf(payoutsum));
                                        }
                                        else
                                        {
                                            availableBalance = String.valueOf(Double.valueOf(totalPayoutAmount) - Double.valueOf(payoutsum));
                                        }
                                    }
                                %>
                                <div style="display: inline-block;">
                                    <form action="/merchant/servlet/PayoutList?ctoken=<%=ctoken%>" method="POST" style="margin:0;">
                                        <input  type="submit" name="submit" value="Available Balance : <%=decimalFormat.format(Double.parseDouble(availableBalance))%>" class="btn btn-default">
                                        <input  type="hidden" name="checkBalance" value="checkBalance" class="btn btn-default">
                                        <%--<label style="color: black;font-family: Open Sans;font-size: 14px !important;font-weight: 600;">Available Balance : <%=payoutsum%> </label>--%>
                                    </form>
                                </div>

                                <div style="display: inline-block;">
                                    <%--<form action="/merchant/servlet/PayoutList?ctoken=<%=ctoken%>" method="POST" style="margin:0;">--%>
                                    <input  type="submit" name="submit" value="Total Balance : <%=decimalFormat.format(Double.parseDouble(totalPayoutAmount))%>" class="btn btn-default">
                                    <input  type="hidden" name="checkBalance" value="checkBalance" class="btn btn-default">
                                    <%--</form>--%>
                                </div>

                            </div>
                        </div>



                        <%-- <div class="additional-btn">
                             <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                             <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                             <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                         </div>--%>
                    </div>

                    <%--<input type="hidden" value="<%=ctoken%>" name="ctoken">--%>
                    <%
                        String terminalCurrency         = "";
                        StringBuffer terminalBuffer     = new StringBuffer();
                        String errorMsg                 = (String)request.getAttribute("error");
                        Functions functions             = new Functions();
                        if(functions.isValueNull(errorMsg))
                        {
                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errorMsg + "</h5>");
                        }

                        String res  = (String)request.getAttribute("message");

                        if(res != null)
                        {
                            out.println(Functions.NewShowConfirmation1("Filter ", res));
                        }
                    %>
                    <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                    <div class="widget-content padding">
                        <form name="form" method="post" action="/merchant/servlet/PayoutList?ctoken=<%=ctoken%>">
                            <div id="horizontal-form">
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <div class="form-group col-md-3 has-feedback">
                                    <label><%=payoutlist_tracking_id%></label>
                                    <input type="text" name="STrakingid" class="form-control" placeholder="<%=payoutlist_tracking_id%>" value="<%=sTrackingid%>">
                                </div>

                                <div class="form-group col-md-3 has-feedback">
                                    <label><%=payoutlist_terminalid%></label>
                                    <select size="1" name="terminalid" class="form-control">
                                        <%--<option value="">All</option>--%>
                                        <%
                                            terminalBuffer.append("(");
                                            //terminalid="";
                                            TerminalManager terminalManager=new TerminalManager();
                                            List<TerminalVO> terminalVOList=terminalManager.getMemberandUserTerminalList(uId, String.valueOf(session.getAttribute("role")));

                                            if(terminalVOList.size()>0)
                                            {

                                        %>
                                        <option value="all"><%=payoutlist_all%></option>
                                        <%
                                            for (TerminalVO terminalVO:terminalVOList)
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
                                                if(terminalid.equals(terminalVO.getTerminalId()))
                                                {
                                                    terminalCurrency = terminalVO.getCurrency();
                                                    str1= "selected";
                                                }
                                                if(terminalBuffer.length()!=0 && terminalBuffer.length()!=1)
                                                {
                                                    terminalBuffer.append(",");
                                                }
                                                terminalBuffer.append(terminalVO.getTerminalId());
                                        %>
                                        <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO.getTerminalId())%>" <%=str1%>> <%=ESAPI.encoder().encodeForHTML(terminalVO.getTerminalId()+"-"+terminalVO.getPaymentName()+"-"+terminalVO.getCardType()+"-"+terminalVO.getCurrency()+"-"+active)%> </option>
                                        <%
                                            }
                                            terminalBuffer.append(")");
                                        }
                                        else
                                        {
                                        %>
                                        <option value="NoTerminal"><%=payoutlist_no_terminal_allocated%></option>
                                        <%

                                            }
                                        %>
                                    </select>
                                </div>


                                <%-- <div class="form-group col-md-3 has-feedback">
                                     <label>Auth Code</label>
                                     <input type="text" name="paymentid" value="<%=sAuthCode%>" class="form-control" placeholder="Auth code">
                                 </div>--%>

                                <div class="form-group col-md-3 has-feedback">
                                    <label><%=payoutlist_order_id%></label>
                                    <input type="text" name="SDescription" value="<%=sDesc%>" class="form-control" placeholder="<%=payoutlist_order_id%>">

                                </div>

                                <%--   <div class="form-group col-md-9 has-feedback">&nbsp;</div>

                                   <div class="form-group col-md-3">
                                       <button type="submit" name="B1" class="btn btn-default">
                                           <i class="fa fa-save"></i>
                                           &nbsp;&nbsp;Search
                                       </button>
                                   </div>--%>

                                <div class="form-group col-md-3 has-feedback">
                                    <%--<button type="submit" name="B1" class="btnblue" style="width:100px;margin-left: 20%; margin-top: 25px;background: rgb(126, 204, 173);">
                                        <i class="fa fa-save"></i>
                                        Search
                                    </button>--%>
                                    <label >&nbsp;</label>
                                    <button type="submit" name="B1" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=payoutlist_search%></button>
                                </div>

                                <input type="hidden" value="<%=terminalBuffer.toString()%>" name="terminalbuffer">
                                <input type="hidden" name="currency" value=<%=terminalCurrency%>>
                                <input type="hidden" name="terminalid" value=<%=terminalid%>>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        </form>


        <div class="row">
            <div class="col-md-12">
                <div class="widget">
                    <div class="widget-header">
                        <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=payoutlist_payout_list%></strong></h2>
                        <div class="additional-btn">
                            <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                            <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                            <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                        </div>
                    </div>
                    <div class="widget-content padding" style="overflow-y: auto;">
                        <%
                            String desc = Functions.checkStringNull(request.getParameter("SDescription"));
                            //str="";
                            Hashtable hash = (Hashtable) request.getAttribute("transdetails");
                            Hashtable temphash = null;
                            String currency = (String) session.getAttribute("currency");

                            if(!"Reversal".equals(request.getParameter("submit")))
                            {
                                if(hash!=null)
                                {
                                    int records = 0;
                                    int totalrecords = 0;

                                    try
                                    {
                                        records = Integer.parseInt((String) hash.get("records"));
                                        totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
                                    }
                                    catch (Exception ex)
                                    {
                                    }

                                    str = "ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                                    if(functions.isValueNull(terminalid)) str = str+"&terminalid="+terminalid;
                                    if(pTerminalBuffer != null) str = str+"&terminalbuffer="+pTerminalBuffer;
                                    if(terminalCurrency != null) str = str+"&currency="+terminalCurrency;
                                    str = str + "&SRecords=" + pagerecords;
                                    if (records > 0)
                                    {
                        %>
                        <br>
                        <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">


                            <thead>
                            <tr style="color: white;">

                                <th style="text-align: center"><%=payoutlist_tracking_id1%></th>
                                <%--  <th style="text-align: center" >Auth Code</th>--%>
                                <th style="text-align: center" ><%=payoutlist_transaction_id%></th>
                                <th style="text-align: center" ><%=payoutlist_order_id1%></th>
                                <th style="text-align: center" ><%=payoutlist_captured_amount%></th>
                                <th style="text-align: center" ><%=payoutlist_currency%></th>
                                <th style="text-align: center" ><%=payoutlist_terminal%></th>
                                <th >&nbsp;</th>
                            </tr>
                            </thead>
                            <tbody>
                            <%

                                String style = "class=td0";
                                for (int pos = 1; pos <= records; pos++)
                                {
                                    String id = Integer.toString(pos);

                                    int srno        = Integer.parseInt(id) + ((pageno - 1) * pagerecords);
                                    style           = "class=\"tr" + (pos + 1) % 2 + "\"";
                                    temphash        = (Hashtable) hash.get(id);
                                    String tempcurrentstatus    = ESAPI.encoder().encodeForHTML((String) temphash.get("currentstatus"));
                                    String tempprovstatus       = ESAPI.encoder().encodeForHTML((String) temphash.get("provstatus"));
                                    String productname          = ESAPI.encoder().encodeForHTML((String) temphash.get("productkey"));
                                    String path             = productname + "/";
                                    String icicitransid     = ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"));
                                    String paymentId        = ESAPI.encoder().encodeForHTML((String) temphash.get("paymentid"));
                                    String captureamount    = ESAPI.encoder().encodeForHTML((String) temphash.get("captureamount"));
                                    String currency1        = ESAPI.encoder().encodeForHTML((String) temphash.get("currency"));
                                    if ("JPY".equalsIgnoreCase(currency1))
                                    {
                                        captureamount = functions.printNumber(Locale.JAPAN, captureamount);
                                    }
                                    else if ("EUR".equalsIgnoreCase(currency1))
                                    {
                                        captureamount = functions.printNumber(Locale.FRANCE, captureamount);
                                    }
                                    else if ("GBP".equalsIgnoreCase(currency1))
                                    {
                                        captureamount = functions.printNumber(Locale.UK, captureamount);
                                    }
                                    else if ("USD".equalsIgnoreCase(currency1))
                                    {
                                        captureamount = functions.printNumber(Locale.US, captureamount);
                                    }
                                    if(captureamount==null || captureamount==""){
                                        captureamount="-";
                                    }
                                    if (paymentId==null || paymentId=="")
                                    {
                                        paymentId="-";
                                    }
                                    String accountid    = ESAPI.encoder().encodeForHTML((String) temphash.get("accountid"));
                                    ctoken              = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                                    out.println("<tr>");
                                    out.println("<td data-label=\"Tracking ID\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(icicitransid) + "</a></td>");
                                        /*out.println("<td data-label=\"Auth Code\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(paymentId) + "</a></td>");*/
                                    out.println("<td data-label=\"Transaction ID\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("transid")) + "</td>");
                                    out.println("<td data-label=\"Description\" align=\"center\">"+ESAPI.encoder().encodeForHTML((String) temphash.get("description")) + "</td>");
                                    out.println("<td data-label=\"Captured Amount\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(captureamount) + "</td>");
                                    out.println("<td data-label=\"Currency\" align=\"center\">&nbsp;"+ ESAPI.encoder().encodeForHTML(currency1) + "</td>");
                                    out.println("<td data-label=\"Terminal\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("terminalid")) + "</td>");
                                    out.print("<td " + style + " align=\"center\">");
                            %>
                            <input type="hidden" value="<%=ctoken%>" name="ctoken">
                            <%
                                    String trackingid = "";
                                    if (functions.isValueNull((String)request.getAttribute("trackingid")))
                                    {
                                        trackingid  = request.getAttribute("trackingid").toString();
                                    }
                                    else
                                    {
                                        trackingid  = icicitransid;
                                    }
                                    out.print("<input class=\"btn btn-default\" type=\"button\" value="+payoutlist_Payout+"  onClick=\"return DoPayout("+accountid+"," + ESAPI.encoder().encodeForHTMLAttribute(trackingid) + ",'"+ctoken+"','"+request.getAttribute("terminal")+"','"+request.getAttribute("terminalbuffer")+"','"+request.getAttribute("desc")+"','"+request.getAttribute("paymentid")+"','"+request.getParameter("SPageno")+"','"+request.getParameter("SRecords")+"')\" >");
                                    out.println("</td>");
                                    out.println("</tr>");
                                }
                            %>
                            </tbody>
                        </table>
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


                    </div>
                </div>
            </div>
        </div>
        <%
            int TotalPageNo;
            if(totalrecords%pagerecords!=0)
            {
                TotalPageNo = totalrecords/pagerecords+1;
            }
            else
            {
                TotalPageNo = totalrecords/pagerecords;
            }
        %>
        <div id="showingid"><strong><%=payoutlist_page_no%> <%=pageno%> of  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
        <div id="showingid"><strong><%=payoutlist_total_no_of_records%>   <%=totalrecords%> </strong></div>

        <jsp:include page="page.jsp" flush="true">
            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
            <jsp:param name="numrows" value="<%=pagerecords%>"/>
            <jsp:param name="pageno" value="<%=pageno%>"/>
            <jsp:param name="str" value="<%=str%>"/>
            <jsp:param name="page" value="PayoutList"/>
            <jsp:param name="currentblock" value="<%=currentblock%>"/>
            <jsp:param name="orderby" value=""/>
        </jsp:include>
        <%
                    }
                    else
                    {
                        out.println(Functions.NewShowConfirmation1(payoutlist_sorry, payoutlist_trackingid_terminalid));
                    }
                }
                else
                {
                    out.println(Functions.NewShowConfirmation1(payoutlist_sorry,payoutlist_trackingid_terminalid));
                }
            }
            else   if("Reversal".equals(request.getParameter("submit")))
            {
                out.println(Functions.NewShowConfirmation1(payoutlist_filter, payoutlist_trackingid_terminalid));
            }
        %>
    </div>
</div>
</div>
</body>
</html>