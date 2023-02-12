<%@ page import="com.directi.pg.Functions,com.directi.pg.TransactionEntry,com.logicboxes.util.ApplicationProperties,
                 org.owasp.esapi.errors.ValidationException" %>
<%@ page import="org.owasp.esapi.*" %>
<%@ page import="com.invoice.dao.InvoiceEntry" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="java.util.*" %>
<%@ page import="com.payment.exceptionHandler.PZDBViolationException" %>
<%@ page import="com.payment.twoGatePay.Interface_pkg.SaleRequest" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ include file="Top.jsp" %>

<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
    session.setAttribute("submit","New_Invoice");

%>
<%int invoiceno11 = 0;
    if(request.getAttribute("invoiceno")!=null)
    {
        invoiceno11 = (Integer) request.getAttribute("invoiceno");
    }

%>
<%int remindercounter = 0;
    if(request.getAttribute("remindercounter")!=null)
    {
        remindercounter = (Integer) request.getAttribute("remindercounter");
    }

%>

<%
    String currency = (String) session.getAttribute("currency");

    InvoiceEntry invoiceEntry   = new InvoiceEntry();
    Hashtable statushash        = invoiceEntry.getStatusHash();
    SortedMap sortedMap         = invoiceEntry.getSortedMap();
    String orderid              = Functions.checkStringNull(request.getParameter("orderid"))==null?"":request.getParameter("orderid");
    String orderdesc            = Functions.checkStringNull(request.getParameter("orderdesc"))==null?"":request.getParameter("orderdesc");
    String trackingid           = Functions.checkStringNull(request.getParameter("trackingid"))==null?"":request.getParameter("trackingid");
    String invoiceno            = Functions.checkStringNull(request.getParameter("invoiceno"))==null?"":request.getParameter("invoiceno");
    String fromdate             = null;
    String todate               = null;

    Date date                           = new Date();
    SimpleDateFormat originalFormat     = new SimpleDateFormat("dd/MM/yyyy");

    String Date     = originalFormat.format(date);
    date.setDate(1);
    String fromDate = originalFormat.format(date);

    fromdate        = Functions.checkStringNull(request.getParameter("fdate")) == null ? fromDate : request.getParameter("fdate");
    todate          = Functions.checkStringNull(request.getParameter("tdate")) == null ? Date : request.getParameter("tdate");
    String status   = Functions.checkStringNull(request.getParameter("status"));

    Calendar rightNow = Calendar.getInstance();
    String str = "";
    if (fromdate == null) fromdate = "" + 1;
    if (todate == null) todate = "" + rightNow.get(rightNow.DATE);
    String currentyear= ""+rightNow.get(rightNow.YEAR);

    if (fromdate != null) str = str + "fdate=" + fromdate;
    if (todate != null) str = str + "&tdate=" + todate;
    if (status != null) str = str + "&status=" + status;

    if (orderid != null) str = str + "&orderid=" + orderid;
    if (orderdesc != null) str = str + "&orderdesc=" + orderdesc;
    if (trackingid != null) str = str + "&trackingid=" + trackingid;
    if (invoiceno != null) str = str + "&invoiceno=" + invoiceno;

    str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();


    int pageno      = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
    int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

    str = str + "&SRecords=" + pagerecords;

    ResourceBundle rb1          = null;
    String language_property1   = (String)session.getAttribute("language_property");
    rb1                         = LoadProperties.getProperty(language_property1);
    String invoice_invoice      = StringUtils.isNotEmpty(rb1.getString("invoice_invoice"))?rb1.getString("invoice_invoice"): "Invoice";
    String invoice_fdate        = StringUtils.isNotEmpty(rb1.getString("invoice_fdate"))?rb1.getString("invoice_fdate"): "From";
    String invoice_tdate        = StringUtils.isNotEmpty(rb1.getString("invoice_tdate"))?rb1.getString("invoice_tdate"): "To";
    String invoice_status       = StringUtils.isNotEmpty(rb1.getString("invoice_status"))?rb1.getString("invoice_status"): "Status:";
    String invoice_all          = StringUtils.isNotEmpty(rb1.getString("invoice_all"))?rb1.getString("invoice_all"): "All";
    String invoice_trackingid   = StringUtils.isNotEmpty(rb1.getString("invoice_trackingid"))?rb1.getString("invoice_trackingid"): "Tracking ID";
    String invoice_invoiceno    = StringUtils.isNotEmpty(rb1.getString("invoice_invoiceno"))?rb1.getString("invoice_invoiceno"): "Invoice ID";
    String invoice_orderdesc    = StringUtils.isNotEmpty(rb1.getString("invoice_orderdesc"))?rb1.getString("invoice_orderdesc"): "Description";
    String invoice_rows         = StringUtils.isNotEmpty(rb1.getString("invoice_rows"))?rb1.getString("invoice_rows"): "Rows/Page";
    String invoice_orderid      = StringUtils.isNotEmpty(rb1.getString("invoice_orderid"))?rb1.getString("invoice_orderid"): "Order ID";
    String invoice_search       = StringUtils.isNotEmpty(rb1.getString("invoice_search"))?rb1.getString("invoice_search"): "Search";
    String invoice_report       = StringUtils.isNotEmpty(rb1.getString("invoice_report"))?rb1.getString("invoice_report"): "Invoice Report";
    String invoice_date         = StringUtils.isNotEmpty(rb1.getString("invoice_date"))?rb1.getString("invoice_date"): "Date";
    String invoice_invoice_id   = StringUtils.isNotEmpty(rb1.getString("invoice_invoice_id"))?rb1.getString("invoice_invoice_id"): "Invoice ID";
    String invoice_merchant     = StringUtils.isNotEmpty(rb1.getString("invoice_merchant"))?rb1.getString("invoice_merchant"): "Merchant OrderId";
    String invoice_order_description    = StringUtils.isNotEmpty(rb1.getString("invoice_order_description"))?rb1.getString("invoice_order_description"): "Order Description";
    String invoice_amount        = StringUtils.isNotEmpty(rb1.getString("invoice_amount"))?rb1.getString("invoice_amount"): "Amount";
    String invoice_currency      = StringUtils.isNotEmpty(rb1.getString("invoice_currency"))?rb1.getString("invoice_currency"): "Currency";
    String invoice_status1       = StringUtils.isNotEmpty(rb1.getString("invoice_status1"))?rb1.getString("invoice_status1"): "Status";
    String invoice_raisedby      = StringUtils.isNotEmpty(rb1.getString("invoice_raisedby"))?rb1.getString("invoice_raisedby"): "RaisedBy";
    String invoice_action        = StringUtils.isNotEmpty(rb1.getString("invoice_action"))?rb1.getString("invoice_action"): "Action";
    String invoice_sorry         = StringUtils.isNotEmpty(rb1.getString("invoice_sorry"))?rb1.getString("invoice_sorry"): "Sorry";
    String invoice_no_records    = StringUtils.isNotEmpty(rb1.getString("invoice_no_records"))?rb1.getString("invoice_no_records"): "No records found for given search criteria.";
    String invoice_filter        = StringUtils.isNotEmpty(rb1.getString("invoice_filter"))?rb1.getString("invoice_filter"): "Filter";
    String invoice_Remind        = StringUtils.isNotEmpty(rb1.getString("invoice_Remind"))?rb1.getString("invoice_Remind"): "Remind";
    String invoice_Regenerate    = StringUtils.isNotEmpty(rb1.getString("invoice_Regenerate"))?rb1.getString("invoice_Regenerate"): "Regenerate";
    String invoice_Cancel        = StringUtils.isNotEmpty(rb1.getString("invoice_Cancel"))?rb1.getString("invoice_Cancel"): "Cancel";
    String invoice_showing       = StringUtils.isNotEmpty(rb1.getString("invoice_showing"))?rb1.getString("invoice_showing"): "Showing Page records";
    String invoice_page_no       = StringUtils.isNotEmpty(rb1.getString("invoice_page_no"))?rb1.getString("invoice_page_no"):"Page number";
    String invoice_total_no_of_records  = StringUtils.isNotEmpty(rb1.getString("invoice_total_no_of_records"))?rb1.getString("invoice_total_no_of_records"):"Total number of records";
%>
<%

%>
<html lang="en">
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>

    <title><%=company%> Merchant Invoice > Invoice History</title>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
        function cancelreason(invoiceno)
        {
            var r= window.prompt("Please Enter Reason why to cancel the Invoice No:"+invoiceno,"");
            if(r.trim()!="")
            {
                document.getElementById("cancelreason"+invoiceno).value=r+"";
            }
            else
            {  window.alert("Please Enter a Valid Reason");
                cancelreason(invoiceno);
            }
        }
        function confirmsubmit(invoiceno)
        {
            var r= window.confirm("Are You Sure you want to cancel the Invoice No:"+invoiceno);
            if(r==true)
            {
                cancelreason(invoiceno);

                document.getElementById("cancelform"+invoiceno).submit();
            }
            else
            {

            }
        }
        function confirmsubmitreg(invoiceno)
        {
            var r= window.confirm("Are You Sure you want to Regenerate the Invoice No:"+invoiceno +"");
            if(r==true)
            {
                document.getElementById("regenerateform"+invoiceno).submit();

            }
            else
            {

            }
        }
    </script>
    <script type="text/javascript">

        $('#sandbox-container input').datepicker({
        });
    </script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
        });
    </script>

</head>

<body class="pace-done widescreen fixed-left-void">


<div class="content-page">
    <div class="content">
        <div class="page-heading">
            <form name="form" method="post" action="/merchant/servlet/Invoice?ctoken=<%=ctoken%>">
                <div class="row">

                    <div class="col-sm-12 portlets ui-sortable">

                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=invoice_invoice%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>

                            <div class="widget-content padding">
                                <div id="horizontal-form">

                                    <%
                                        String error    =(String) request.getAttribute("error");
                                        if( error   != null)
                                        {
                                            out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                                        }
                                    %>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label><%=invoice_fdate%></label>
                                        <input type="text" size="16" name="fdate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fromdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;" >

                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label><%=invoice_tdate%></label>
                                        <input type="text" size="16" name="tdate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(todate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">

                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label  style="font-family:Open Sans;font-size: 13px;font-weight: 600;"><%=invoice_status%></label>
                                        <select name="status"  class="form-control">
                                            <option value=""><%=invoice_all%></option>
                                            <%
                                                if(sortedMap.size()>0)
                                                {
                                                    Set<String> setStatus   = sortedMap.entrySet();
                                                    Iterator i              = setStatus.iterator();
                                                    while(i.hasNext())
                                                    {
                                                        Map.Entry entryMap  = (Map.Entry) i.next();
                                                        String status1      = (String) entryMap.getKey();
                                                        String status2      = (String) entryMap.getValue();

                                                        String select = "";
                                                        if(status1.equalsIgnoreCase(status))
                                                        {
                                                            select = "selected";
                                                        }
                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(status1)%>" <%=select%>>
                                                <%=ESAPI.encoder().encodeForHTML(status2)%>
                                            </option>
                                            <%
                                                    }
                                                }
                                            %>
                                        </select>
                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label  ><%=invoice_trackingid%></label>
                                        <input type=text name="trackingid" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>">
                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label  ><%=invoice_invoiceno%></label>
                                        <input type=text name="INVOICE_NO"  class="form-control"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(invoiceno)%>" size="15">
                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label  ><%=invoice_orderdesc%></label>
                                        <input type=text name="orderdesc" maxlength="100"  size="15" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(orderdesc)%>">
                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label  ><%=invoice_rows%></label>
                                        <input type="text" maxlength="3"  size="15" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(new Integer(pagerecords).toString())%>" name="SRecords" size="2" />
                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label  ><%=invoice_orderid%></label>
                                        <input type=text name="orderid" maxlength="100" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(orderid)%>">
                                    </div>

                                    <div class="form-group col-md-3">

                                    </div>
                                    <div class="form-group col-md-3">

                                    </div>
                                    <div class="form-group col-md-3">

                                    </div>
                                    <div class="form-group col-md-3">
                                        <button type="submit" name="B1" class="btn btn-default">
                                            <i class="fa fa-save"></i>
                                            &nbsp;&nbsp;<%=invoice_search%>
                                        </button>

                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form>

            <div class="row">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-header">
                            <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=invoice_report%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>
                        <div class="widget-content padding" style="overflow-y: auto;">



                            <% if(request.getAttribute("error")==null && invoiceno11!=0 && remindercounter!=0)
                            {
                                int temp= 8 - remindercounter;
                                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;  An E-Mail has Been Sent Again to the Customers E-Mail Address for Invoice Number"+invoiceno11+"</h5>");
                            }
                            else
                            {
                            %>

                            <%}%>

                            <br>

                            <%

                                if(request.getAttribute("transactionsdetails")!=null)
                                {
                                    Hashtable hash      = (Hashtable) request.getAttribute("transactionsdetails");
                                    Hashtable temphash  = null;

                                    int records         = 0;
                                    int totalrecords    = 0;
                                    int currentblock    = 1;

                                    try
                                    {
                                        records         = Integer.parseInt((String) hash.get("records"));
                                        totalrecords    = Integer.parseInt((String) hash.get("totalrecords"));
                                        currentblock    = Integer.parseInt(request.getParameter("currentblock"));
                                    }
                                    catch (Exception ex)
                                    {

                                    }

                                    String style        = "class=tr0";
                                    String ext          = "light";
                                    String style1       = "class=\"textb\"";
                                    if (records > 0)
                                    {
                            %>
                            <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                <thead >
                                <tr style="color: white;">
                                    <th style="text-align: center"><%=invoice_date%></th>
                                    <%--<th style="text-align: center">Invoice No</th>--%>
                                    <th style="text-align: center"><%=invoice_invoice_id%></th>
                                    <%--<th style="text-align: center">Tracking ID</th>--%>
                                    <th style="text-align: center"><%=invoice_merchant%></th>
                                    <th style="text-align: center"><%=invoice_order_description%></th>
                                    <th style="text-align: center"><%=invoice_amount%></th>
                                    <th style="text-align: center"><%=invoice_currency%></th>
                                    <th style="text-align: center"><%=invoice_status1%></th>
                                    <th style="text-align: center"><%=invoice_raisedby%></th>
                                    <th style="text-align: center"><%=invoice_action%></th>
                                </tr>
                                </thead>
                                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                <%
                                    StringBuffer requestParameter   = new StringBuffer();
                                    Enumeration<String> requestName = request.getParameterNames();
                                    while(requestName.hasMoreElements())
                                    {
                                        String name = requestName.nextElement();
                                        if("SPageno".equals(name) || "SRecords".equals(name))
                                        {

                                        }
                                        else
                                                requestParameter.append("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                                        }
                                        requestParameter.append("<input type='hidden' name='SPageno' value='"+pageno+"'/>");
                                        requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");

                                        for (int pos = 1; pos <= records; pos++)
                                        {
                                            String id = Integer.toString(pos);

                                        int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

                                        style = "class=\"tr" + (pos + 1) % 2 + "\"";

                                        temphash            = (Hashtable) hash.get(id);
                                        String date2        = Functions.convertDtstamptoDate((String)temphash.get("dtstamp"));

                                        long expiredTime    = Functions.getInvoiceExpiredTime(temphash.get("timestamp").toString(),temphash.get("expirationPeriod").toString());
                                        Date d              = new Date();
                                        long currentTime    = d.getTime()/1000;
                                        orderid             = (String)temphash.get("orderid");
                                            Functions functions = new Functions();
                                            if (functions.isValueNull((String)temphash.get("orderdescription")))
                                        orderdesc = (String)temphash.get("orderdescription");
                                        else
                                        orderdesc   = "";
                                        trackingid  = (String)temphash.get("trackingid");
                                        invoiceno   = (String)temphash.get("invoiceno");

                                        ctoken              = request.getParameter("ctoken");
                                        String amount       = (String) temphash.get("amount");
                                        currency            = (String) temphash.get("currency");
                                        status              = (String) temphash.get("status");
                                        String accountid    = (String) temphash.get("accountid");
                                        String useraccname  = (String) temphash.get("raisedBy");
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

                                        ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

                                        out.println("<tr " + style + ">");
                                        out.println("<td align=\"center\" data-label=\"Date\">" + ESAPI.encoder().encodeForHTML(date2) + "</td>");
                                        out.println("<td align=\"center\" data-label=\"Invoice No\"><form action=\"InvoiceDetails?ctoken="+ctoken+"\" method=\"post\"><input type=\"hidden\" name=\"invoiceno\" value=\""+invoiceno+"\"><input type=\"submit\" class=\"btn btn-default\" name=\"submit\" value=\""+invoiceno+"\"><input type='hidden' name='action' value='search'/>");
                                        out.println(requestParameter);
                                        out.println("</form></td>");

                                        /*if (trackingid == null || trackingid.equals(""))
                                            out.println("<td data-label=\"Tracking ID\" align=\"center\" " + style + ">&nbsp;" + "" + "</td>");
                                        else
                                            out.println("<td align=\"center\" data-label=\"Tracking ID\"><form action=\"TransactionDetails?ctoken="+ctoken+"\" method=\"post\"><input type=\"hidden\" name=\"trackingid\" value=\""+trackingid+"\"><input type=\"hidden\" name=\"ctoken\" value=\""+ctoken+"\"><input type=\"hidden\" name=\"buttonValue\" value=\"invoice\"><input type=\"hidden\" name=\"archive\" value=\"false\"><input type=\"hidden\" name=\"accountid\" value=\""+accountid+"\"><input type=\"submit\" class=\"btn btn-default\" name=\"submit\" value=\""+trackingid+"\">");
*/                                        out.println(requestParameter);
                                        out.println("</form></td>");

                                        String data = "";
                                        if(expiredTime<=currentTime)
                                        {
                                            data = status.equals("mailsent") || status.equals("generated") ? "expired" : status;
                                        }
                                        else
                                        {
                                            data = status;
                                        }

                                        out.println("<td align=\"center\" data-label=\"Order ID\">" + ESAPI.encoder().encodeForHTML(orderid) + "</td>");
                                        out.println("<td align=\"center\" data-label=\"Order Description\">" + ESAPI.encoder().encodeForHTML(orderdesc) + "</td>");
                                        out.println("<td align=\"center\" data-label=\"Amount\">" + ESAPI.encoder().encodeForHTML(amount) + "</td>");
                                        out.println("<td align=\"center\" data-label=\"Currency\">" + ESAPI.encoder().encodeForHTML(currency) + "</td>");
                                        out.println("<td align=\"center\" data-label=\"Status\">" + data + "</td>");
                                        out.println("<td align=\"center\" data-label=\"RaisedBy\">" + ESAPI.encoder().encodeForHTML(useraccname) + "</td>");
                                        out.println("<td align=\"center\" data-label=\"Action\"> ");

                                        if(ESAPI.encoder().encodeForHTML(status).equals("generated"))
                                        {
                                %>
                                <table>
                                    <tr <%=style%>>

                                    <%
                                        if(Integer.parseInt((String)((Hashtable)(new InvoiceEntry()).getInvoiceDetails(invoiceno)).get("remindercounter"))<8)
                                        {
                                    %>

                                        <td>

                                            <%
                                                if(expiredTime<currentTime)
                                                {

                                            %>
                                        <form name=remindform action="RemindInvoice?ctoken=<%=ctoken%>" method="post">
                                            <input type="hidden" name="invoiceno" value=<%=invoiceno%> >
                                            <input type="submit" name="Remind" value="<%=invoice_Remind%>" class="btn btn-default" >
                                        </form>
                                            <%
                                            }
                                            else
                                            {

                                            %>
                                                <form name=regenerateform<%=invoiceno%> id="regenerateform<%=invoiceno%>" action="RegenerateInvoiceConfirm?ctoken=<%=ctoken%>" method="post">
                                                    <input type="hidden" name="invoiceno" value=<%=invoiceno%> >
                                                    <input type="submit" name="Regenerate" value="<%=invoice_Regenerate%>" class="btn btn-default" onclick=confirmsubmitreg(<%=invoiceno%>)>
                                                </form>
                                            <%
                                            }
                                            %>

                                        </td>
                                        <%
                                        }
                                        %>

                                        <td>
                                            <form name=cancelform<%=invoiceno%> id="cancelform<%=invoiceno%>" action="CancelInvoice?ctoken=<%=ctoken%>" method="post" >
                                                <input type="hidden" name=invoiceno value=<%=invoiceno%> >
                                                <input type="hidden" name=cancelreason id=cancelreason<%=invoiceno%>  >
                                                <%

                                                    if(expiredTime<currentTime)
                                                    {
                                                %>
                                                <input type="button" name="Cancel" value="<%=invoice_Cancel%>" class="btn btn-default" onclick=confirmsubmit(<%=invoiceno%>) >


                                                <%
                                                }
                                                %>
                                            </form>
                                        </td>
                                    </tr>
                                </table>

                                <%
                                    }
                                    if(ESAPI.encoder().encodeForHTML(status).equals("mailsent"))
                                    {
                                %>
                                <table>
                                    <tr <%=style%>>
                                <%
                                    if(Integer.parseInt((String)((Hashtable)(new InvoiceEntry()).getInvoiceDetails(invoiceno)).get("remindercounter"))<8)
                                    {
                                %>

                                    <td>

                                            <%

                                        if(expiredTime>=currentTime)
                                        {
                                            %>
                                                <form name=remindform action="RemindInvoice?ctoken=<%=ctoken%>" method="post">
                                                    <input type="hidden" name=invoiceno value=<%=invoiceno%> >

                                                <input type="submit" name="Remind" value="<%=invoice_Remind%>" class="btn btn-default" >
                                                </form>
                                            <%
                                        }
                                        else
                                        {
                                            %>
                                                <form name=regenerateform<%=invoiceno%> id="regenerateform<%=invoiceno%>" action="RegenerateInvoiceConfirm?ctoken=<%=ctoken%>" method="post">
                                                    <input type="hidden" name="invoiceno" value=<%=invoiceno%> >
                                                    <input type="button" name="Regenerate" value="<%=invoice_Regenerate%>" class="btn btn-default" onclick=confirmsubmitreg(<%=invoiceno%>)>
                                                </form>
                                            <%
                                        }
                                            %>
                                    </td>

                                <% }%>

                                        <%

                                            if(expiredTime>=currentTime)
                                            {

                                        %>

                                <td>


                                    <form name=cancelform<%=invoiceno%> id="cancelform<%=invoiceno%>" action="CancelInvoice?ctoken=<%=ctoken%>" method="post" >
                                        <input type="hidden" name=invoiceno value=<%=invoiceno%> >
                                        <input type="hidden" name=cancelreason id=cancelreason<%=invoiceno%> >
                                        <input type="button" name="Cancel" value="<%=invoice_Cancel%>" class="btn btn-default" onclick=confirmsubmit(<%=invoiceno%>) >
                                    </form>




                            </td>
                                        <%
                                            }
                                        %>

                                <%
                                    if(Integer.parseInt((String)((Hashtable)(new InvoiceEntry()).getInvoiceDetails(invoiceno)).get("remindercounter"))>=100)
                                    {
                                %>
                                <td>
                                    <form name=regenerateform<%=invoiceno%> id="regenerateform<%=invoiceno%>" action="RegenerateInvoiceConfirm?ctoken=<%=ctoken%>" method="post">
                                        <input type="hidden" name="invoiceno" value=<%=invoiceno%> >
                                        <input type="button" name="Regenerate" value="<%=invoice_Regenerate%>" class="btn btn-default" onclick=confirmsubmitreg(<%=invoiceno%>)>
                                    </form>
                                </td>
                                <% } %>

                            </tr>
                            </table>


                                <%
                                    }
                                    if(ESAPI.encoder().encodeForHTML(status).equals("processed"))
                                    {
                                %>

                                <table>
                                    <tr <%=style%>>

                                        <td>
                                            <form name=regenerateform<%=invoiceno%> id="regenerateform<%=invoiceno%>" action="RegenerateInvoiceConfirm?ctoken=<%=ctoken%>" method="post">
                                                <input type="hidden" name=invoiceno value=<%=invoiceno%> >
                                                <input type="button" name="Regenerate" value="<%=invoice_Regenerate%>" class="btn btn-default" onclick=confirmsubmitreg(<%=invoiceno%>)>
                                            </form>
                                        </td>

                                    </tr>
                                </table>

                                <%
                                    }

                                    if(ESAPI.encoder().encodeForHTML(status).equals("cancelled"))
                                    {
                                %>

                                <table>
                                    <tr <%=style%>>

                                        <td>
                                            <form name=regenerateform<%=invoiceno%> id="regenerateform<%=invoiceno%>" action="RegenerateInvoiceConfirm?ctoken=<%=ctoken%>" method="post">
                                                <input type="hidden" name=invoiceno value=<%=invoiceno%> >
                                                <input type="hidden" name=trackingid value=<%=trackingid%> >
                                                <input type="button" name="Regenerate" value="<%=invoice_Regenerate%>" class="btn btn-default" onclick=confirmsubmitreg(<%=invoiceno%>)>
                                            </form>
                                        </td>

                                    </tr>
                                </table>

                                <%
                                        }
                                    }
                                %>

                            </table>

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
                        <div id="showingid"><strong><%=invoice_page_no%> <%=pageno%>  of  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
                        <div id="showingid"><strong><%=invoice_total_no_of_records%>   <%=totalrecords%> </strong></div>

                        <jsp:include page="page.jsp" flush="true">

                            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                            <jsp:param name="numrows" value="<%=pagerecords%>"/>
                            <jsp:param name="pageno" value="<%=pageno%>"/>
                            <jsp:param name="str" value="<%=str%>"/>
                            <jsp:param name="page" value="Invoice"/>
                            <jsp:param name="currentblock" value="<%=currentblock%>"/>
                            <jsp:param name="orderby" value=""/>
                        </jsp:include>

                        <%
                                }
                                else
                                {
                                    out.println(Functions.NewShowConfirmation1(invoice_sorry, invoice_no_records));
                                }
                            }
                            else if (request.getAttribute("catchError")!=null && request.getAttribute("catchError")!="")
                            {
                                out.println(Functions.NewShowConfirmation1("catchError", (String)request.getAttribute("catchError")));
                            }
                            else
                            {
                                out.println(Functions.NewShowConfirmation1(invoice_filter,  invoice_no_records));
                            }
                        %>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>