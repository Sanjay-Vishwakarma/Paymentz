f<%@ page import="com.invoice.dao.InvoiceEntry,
                 org.owasp.esapi.ESAPI,
                 java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.invoice.vo.ProductList" %>
<%@ page import="java.util.*" %>
<%--
  Created by IntelliJ IDEA.
  User: jignesh.r
  Date: Oct 11, 2006
  Time: 4:31:18 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="Top.jsp" %>
<%

    InvoiceEntry invoiceEntry = new InvoiceEntry();
    Hashtable statushash = invoiceEntry.getStatusHash();

    Hashtable hash = (Hashtable) request.getAttribute("hiddenvariables");
    //Hashtable transactionHash = (Hashtable) request.getAttribute("transactionDetails");
    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String invoiceDetails_Invoice_Details = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Invoice_Details"))?rb1.getString("invoiceDetails_Invoice_Details"): "Invoice Details";
    String invoiceDetails_Tracking_Id = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Tracking_Id"))?rb1.getString("invoiceDetails_Tracking_Id"): "Tracking ID";
    String invoiceDetails_Invoice_No = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Invoice_No"))?rb1.getString("invoiceDetails_Invoice_No"): "Invoice No";
    String invoiceDetails_Order_ID = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Order_ID"))?rb1.getString("invoiceDetails_Order_ID"): "Order ID";
    String invoiceDetails_Order_Description = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Order_Description"))?rb1.getString("invoiceDetails_Order_Description"): "Order Description";
    String invoiceDetails_Tax_Amount = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Tax_Amount"))?rb1.getString("invoiceDetails_Tax_Amount"): "Tax Amount";
    String invoiceDetails_Invoice_Amount = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Invoice_Amount"))?rb1.getString("invoiceDetails_Invoice_Amount"): "Invoice Amount";
    String invoiceDetails_Date_Invoice = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Date_Invoice"))?rb1.getString("invoiceDetails_Date_Invoice"): "Date of Invoice";
    String invoiceDetails_Invoice_Status = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Invoice_Status"))?rb1.getString("invoiceDetails_Invoice_Status"): "Invoice Status";
    String invoiceDetails_Detail = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Detail"))?rb1.getString("invoiceDetails_Detail"): "Customer's Details";
    String invoiceDetails_name = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_name"))?rb1.getString("invoiceDetails_name"): "Customers's Name";
    String invoiceDetails_email = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_email"))?rb1.getString("invoiceDetails_email"): "Customer's Emailaddress";
    String invoiceDetails_Telno = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Telno"))?rb1.getString("invoiceDetails_Telno"): "Tel No";
    String invoiceDetails_City = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_City"))?rb1.getString("invoiceDetails_City"): "City";
    String invoiceDetails_Street = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Street"))?rb1.getString("invoiceDetails_Street"): "Street";
    String invoiceDetails_State = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_State"))?rb1.getString("invoiceDetails_State"): "State";
    String invoiceDetails_Country = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Country"))?rb1.getString("invoiceDetails_Country"): "Country";
    String invoiceDetails_Pin_Code = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Pin_Code"))?rb1.getString("invoiceDetails_Pin_Code"): "Pin Code";
    String invoiceDetails_Transaction_Details = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Transaction_Details"))?rb1.getString("invoiceDetails_Transaction_Details"): "Transaction Details";
    String invoiceDetails_TrackingId = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_TrackingId"))?rb1.getString("invoiceDetails_TrackingId"): "Tracking Id";
    String invoiceDetails_AuthCode = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_AuthCode"))?rb1.getString("invoiceDetails_AuthCode"): "Auth Code";
    String invoiceDetails_Transaction_Amount = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Transaction_Amount"))?rb1.getString("invoiceDetails_Transaction_Amount"): "Transaction Amount";
    String invoiceDetails_date = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_date"))?rb1.getString("invoiceDetails_date"): "Date of Transaction";
    String invoiceDetails_Status = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Status"))?rb1.getString("invoiceDetails_Status"): "Transaction Status";
    String invoiceDetails_Status_Detail = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Status_Detail"))?rb1.getString("invoiceDetails_Status_Detail"): "Status Detail";
    String invoiceDetails_Product_Details = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Product_Details"))?rb1.getString("invoiceDetails_Product_Details"): "Product Details";
    String invoiceDetails_Description = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Description"))?rb1.getString("invoiceDetails_Description"): "Product Description";
    String invoiceDetails_Product_Unit = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Product_Unit"))?rb1.getString("invoiceDetails_Product_Unit"): "Product Unit";
    String invoiceDetails_Product_Amount = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Product_Amount"))?rb1.getString("invoiceDetails_Product_Amount"): "Product Amount";
    String invoiceDetails_Quantity = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Quantity"))?rb1.getString("invoiceDetails_Quantity"): "Quantity";
    String invoiceDetails_tax = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_tax"))?rb1.getString("invoiceDetails_tax"): "Tax(%)";
    String invoiceDetails_Sub_Total = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Sub_Total"))?rb1.getString("invoiceDetails_Sub_Total"): "Sub Total";
    String invoiceDetails_Go_Back = StringUtils.isNotEmpty(rb1.getString("invoiceDetails_Go_Back"))?rb1.getString("invoiceDetails_Go_Back"): "Go Back";

    List<ProductList> listProducts = (List<ProductList>) hash.get("listOfProducts");

    //System.out.println("sizee in InvoiceDetils jsp---"+listProducts.size());
    String invoiceno= (String) hash.get("invoiceno");
    String trackingid=(String) hash.get("trackingid");
    String orderid=(String) hash.get("orderid");
    String orderdesc=(String) hash.get("orderdesc");



    orderid = Functions.checkStringNull(request.getParameter("orderid"))==null?"":request.getParameter("orderid");
    orderdesc = Functions.checkStringNull(request.getParameter("orderdesc"))==null?"":request.getParameter("orderdesc");

    trackingid = Functions.checkStringNull(request.getParameter("trackingid"))==null?"":request.getParameter("trackingid");
    invoiceno = Functions.checkStringNull(request.getParameter("invoiceno"))==null?"":request.getParameter("invoiceno");
    String fdate=null;
    String tdate=null;

    String status = Functions.checkStringNull(request.getParameter("status"));

    Calendar rightNow = Calendar.getInstance();
    String str = "";
    if (fdate == null) fdate = "" + 1;
    if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
    String currentyear= ""+rightNow.get(rightNow.YEAR);

    if (fdate != null) str = str + "fdate=" + fdate;
    if (tdate != null) str = str + "&tdate=" + tdate;
    if (orderid != null) str = str + "&orderid=" + orderid;
    if (orderdesc != null) str = str + "&orderdesc=" + orderdesc;

    str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();


    int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
    int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

    str = str + "&SRecords=" + pagerecords;

%>
<%
    String fromdate = null;
    String todate = null;

    Date date1 = new Date();
    SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

    String Date = originalFormat.format(date1);
    date1.setDate(1);
    String fromDate = originalFormat.format(date1);

    fromdate = Functions.checkStringNull(request.getParameter("fdate")) == null ? fromDate : request.getParameter("fdate");
    todate = Functions.checkStringNull(request.getParameter("tdate")) == null ? Date : request.getParameter("tdate");


%>

<html>
<head>

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

    <title>Invoice Details</title>
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
<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
<body class="pace-done widescreen fixed-left-void">

<form name="form" method="post" action="Invoice?ctoken=<%=ctoken%>">
    <div class="content-page">
        <div class="content">
            <!-- Page Heading Start -->
            <div class="page-heading">

                <div class="pull-right">
                    <div class="btn-group">
                        <form action="/merchant/servlet/Transactions?ctoken=<%=ctoken%>" method="POST">
                            <%
                                Enumeration<String> requestName=request.getParameterNames();
                                while(requestName.hasMoreElements())
                                {
                                    String name=requestName.nextElement();
                                    if("invoiceno".equals(name))
                                    {
                                        String[] values=request.getParameterValues(name);

                                        //out.println("<input type='hidden' name='"+name+"' value='"+request.getParameterValues(name)[1]+"'/>");
                                        if(values.length>1){
                                            out.println("<input type='hidden' name='"+name+"' value='"+request.getParameterValues(name)[1]+"'/>");
                                        }else{
                                            out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                                        }
                                    }
                                    else
                                        out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                                }
                            %>
                            <%--<button class="btn-xs" type="submit" name="submit" name="B1" style="background: transparent;border: 0;">
                                <img style="height: 35px;" src="/merchant/images/goBack.png">
                            </button>--%>

                            <button type="submit" name="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-arrow-circle-left" aria-hidden="true"></i>&nbsp;&nbsp;<%=invoiceDetails_Go_Back%></button>

                        </form>
                    </div>
                </div>
                <br><br><br>


                <%
                    if(hash!=null)
                    {
                        String style = "class=td0";
                        String ext = "light";

                %>

                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=invoiceDetails_Invoice_Details%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>

                            <div class="widget-content padding" style="overflow-y: auto;">

                                <table class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead >

                                    <tr style="color: white;">

                                        <th style="text-align: center;"><%=invoiceDetails_Tracking_Id%></th>
                                        <th style="text-align: center;"><%=invoiceDetails_Invoice_No%> </th>
                                        <th style="text-align: center;"><%=invoiceDetails_Order_ID%></th>
                                        <th style="text-align: center;"><%=invoiceDetails_Order_Description%></th>
                                        <th style="text-align: center;"><%=invoiceDetails_Tax_Amount%></th>
                                        <th style="text-align: center;"><%=invoiceDetails_Invoice_Amount%></th>
                                        <th style="text-align: center;"><%=invoiceDetails_Date_Invoice%></th>
                                        <th style="text-align: center;"><%=invoiceDetails_Invoice_Status%></th>

                                    </tr>
                                    </thead>
                                    <tbody>
                                    <%
                                        invoiceno= (String) hash.get("invoiceno");
                                        trackingid=(String) hash.get("trackingid");
                                        if (trackingid == null) trackingid = "-";
                                        orderid=(String) hash.get("orderid");
                                        orderdesc=(String) hash.get("orderdesc");

                                        String amount = (String) hash.get("amount");
                                        String taxamount = "";

                                        if (functions.isValueNull((String) hash.get("taxamount")))
                                        {
                                            taxamount = (String) hash.get("taxamount");
                                        }
                                        String date = (String) hash.get("date");
                                        status = (String) hash.get("status");

                                        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                        Date date2 = new Date();
                                        long generatedTime = (dateFormat.parse(hash.get("timestamp").toString()).getTime())/(60*60*1000);
                                        long currentTime = (dateFormat.parse(String.valueOf(dateFormat.format(date2))).getTime())/(60*60*1000);
                                        long diffTime = currentTime - generatedTime;
                                        String expPeriod = (String) hash.get("expirationPeriod");
                                        String data = "";

                                        if(Integer.parseInt(expPeriod)<=diffTime)
                                        {
                                            data = status.equals("mailsent") || status.equals("generated") ? "expired" : status;
                                        }
                                        else
                                        {
                                            data = status;
                                        }

                                        out.println("<tr " + style + ">");
                                        out.println("<td align=\"center\" data-label=\"Tracking Id\">" + ESAPI.encoder().encodeForHTML(trackingid) + "</td>");
                                        out.println("<td align=\"center\" data-label=\"Invoice No\">" + ESAPI.encoder().encodeForHTML(invoiceno) +  "</td>");
                                        out.println("<td align=\"center\" data-label=\"Order ID\">" + ESAPI.encoder().encodeForHTML(orderid) +  "</td>");
                                        out.println("<td align=\"center\" data-label=\"Order Description\">" + ESAPI.encoder().encodeForHTML(orderdesc) +  "</td>");
                                        out.println("<td align=\"center\" data-label=\"Tax Amount\">" + ESAPI.encoder().encodeForHTML(taxamount) +  "</td>");
                                        out.println("<td align=\"center\" data-label=\"Invoice Amount\">" + ESAPI.encoder().encodeForHTML(amount) +  "</td>");
                                        out.println("<td align=\"center\" data-label=\"Date of Invoice\">" + ESAPI.encoder().encodeForHTML(date) +  "</td>");
                                        out.println("<td align=\"center\" data-label=\"Invoice Status\">" + ESAPI.encoder().encodeForHTML(data) +  "</td>");

                                    %>
                                    </tbody>

                                </table>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=invoiceDetails_Detail%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>

                            <div class="widget-content padding" style="overflow-y: auto;">

                                <table class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>

                                    <tr style="color: white;">

                                        <td style="text-align: center;"><%=invoiceDetails_name%></td>
                                        <td style="text-align: center;"><%=invoiceDetails_email%></td>
                                        <td style="text-align: center;"><%=invoiceDetails_Telno%></td>
                                        <td style="text-align: center;"><%=invoiceDetails_City%></td>
                                        <td style="text-align: center;"><%=invoiceDetails_Street%></td>
                                        <td style="text-align: center;" ><%=invoiceDetails_State%></td>
                                        <td style="text-align: center;"><%=invoiceDetails_Country%></td>
                                        <td style="text-align: center;" ><%=invoiceDetails_Pin_Code%></td>

                                    </tr>
                                    </thead>
                                    <tbody>
                                    <%
                                        String custname = (String) hash.get("custname");
                                        if (custname == null) custname = "-";
                                        String email = (String) hash.get("custemail");
                                        if (email == null) email = "-";
                                        String phonecc = (String) hash.get("phonecc");
                                        if (phonecc == null) phonecc = "-";
                                        String phone = (String) hash.get("phone");
                                        if (phone == null) phone = "-";
                                        String city = (String) hash.get("city");
                                        if (city == null) city = "-";
                                        String street = (String) hash.get("address");
                                        if (street == null) street = "-";
                                        String zip = (String) hash.get("zipcode");
                                        if (zip == null) zip = "-";
                                        String state = (String) hash.get("state");
                                        if (state == null) state = "-";
                                        String country = (String) hash.get("country");
                                        if (country == null) country = "-";

                                        out.println("<tr " + style + ">");
                                        out.println("<td align=\"center\" data-label=\"Customer's Name\">" + ESAPI.encoder().encodeForHTML(custname) + "</td>");
                                        out.println("<td align=\"center\" data-label=\"Customer's Emailaddress\">" + functions.getEmailMasking(email) +  "</td>");
                                        out.println("<td align=\"center\" data-label=\"Tel No\">" + functions.getPhoneNumMasking(phone) +  "</td>");
                                        out.println("<td align=\"center\" data-label=\"City\">" + ESAPI.encoder().encodeForHTML(city) +  "</td>");
                                        out.println("<td align=\"center\" data-label=\"Street\">" + ESAPI.encoder().encodeForHTML(street) +  "</td>");
                                        out.println("<td align=\"center\" data-label=\"State\">" + ESAPI.encoder().encodeForHTML(state) +  "</td>");
                                        out.println("<td align=\"center\" data-label=\"Country\">" + ESAPI.encoder().encodeForHTML(country) +  "</td>");
                                        out.println("<td align=\"center\" data-label=\"Pin Code\">" + ESAPI.encoder().encodeForHTML(zip) +  "</td>");

                                    %>
                                    </tbody>


                                </table>

                            </div>
                        </div>
                    </div>
                </div>

                <%
                    }

                %>

                <%
                    Hashtable transactionDetails = (Hashtable) request.getAttribute("transactionDetails");
                    Hashtable innerHash = (Hashtable) transactionDetails.get("1");// Since we'll get details of one transaction only

                    if(innerHash!=null)
                    {
                %>

                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=invoiceDetails_Transaction_Details%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>


                            <div class="widget-content padding" style="overflow-y: auto;">
                                <%--<div class="table-responsive">--%>
                                <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">

                                    <thead>

                                    <tr style="color: white;">
                                        <th style="text-align: center"><%=invoiceDetails_TrackingId%> </th>
                                        <th style="text-align: center"><%=invoiceDetails_AuthCode%> </th>
                                        <th style="text-align: center"><%=invoiceDetails_Transaction_Amount%> </th>
                                        <th style="text-align: center"><%=invoiceDetails_date%></th>
                                        <th style="text-align: center"><%=invoiceDetails_Status%></th>
                                        <th style="text-align: center"><%=invoiceDetails_Status_Detail%></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <%
                                        String trackingId = (String) innerHash.get("icicitransid");
                                        String paymentIdFromHash = (String) innerHash.get("paymentid");
                                        if(paymentIdFromHash==null)
                                        {
                                            paymentIdFromHash="-";
                                        }
                                        String amount = (String) innerHash.get("amount");
                                        String date = (String) innerHash.get("date");
                                        String transStatus = (String) innerHash.get("status");
                                        String transStatusDetail = "Not Available";
                                        if(Functions.checkStringNull((String) innerHash.get("authqsiresponsecode"))!=null)
                                        {
                                            transStatusDetail = (String) innerHash.get("authqsiresponsecode");
                                        }

                                        //out.println("<tr " + style + ">");
                                        out.println("<td data-label=\"Tracking Id\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(trackingId) + "</td>");
                                        out.println("<td data-label=\"Auth Code\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(paymentIdFromHash) + "</td>");
                                        out.println("<td data-label=\"Transaction Amount\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(amount) +  "</td>");
                                        out.println("<td data-label=\"Date of Transaction\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(date) +  "</td>");
                                        out.println("<td data-label=\"Transaction Status\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(transStatus) +  "</td>");
                                        out.println("<td data-label=\"Status Detail\" style=\"text-align: center\">" + ESAPI.encoder().encodeForHTML(transStatusDetail) +  "</td>");
                                        out.println("</tr>");

                                    %>
                                    </tbody>
                                </table>
                                <%--</div>--%>
                            </div>

                        </div>
                    </div>
                </div>

                <%
                    }

                %>


                <%
                    if(listProducts.size()>0)
                    {
                %>
                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=invoiceDetails_Product_Details%></strong></h2>
                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>

                            <div class="widget-content padding" style="overflow-y: auto;">

                                <table class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                    <thead>

                                    <tr style="color: white;">

                                        <td style="text-align: center;"><%=invoiceDetails_Description%> </td>
                                        <td style="text-align: center;"><%=invoiceDetails_Product_Unit%></td>
                                        <td style="text-align: center;"><%=invoiceDetails_Product_Amount%></td>
                                        <td style="text-align: center;"><%=invoiceDetails_Quantity%></td>
                                        <td style="text-align: center;"><%=invoiceDetails_tax%></td>
                                        <td style="text-align: center;"><%=invoiceDetails_Sub_Total%></td>

                                    </tr>
                                    </thead>
                                    <tbody>
                                    <%
                                        String taxAmt = "0.00";

                                        for(ProductList productList : listProducts)
                                        {
                                            String prodUnit = "";
                                            String prodTax = "";
                                            if(functions.isValueNull(productList.getProductUnit()))
                                            {
                                                prodUnit = productList.getProductUnit();
                                            }
                                            if(functions.isValueNull(productList.getTax()))
                                            {
                                                prodTax = productList.getTax();
                                            }
                                            out.println("<tr>");
                                            out.println("<td align=\"center\" data-label=\"Product Description\">" + productList.getProductDescription() + "</td>");
                                            out.println("<td align=\"center\" data-label=\"Product Unit\">" + prodUnit + "</td>");
                                            out.println("<td align=\"center\" data-label=\"Product Amount\">" + productList.getProductAmount() + "</td>");
                                            out.println("<td align=\"center\" data-label=\"Quantity\">" + productList.getQuantity() + "</td>");
                                            out.println("<td align=\"center\" data-label=\"Tax\">" + prodTax + "</td>");
                                            out.println("<td align=\"center\" data-label=\"Sub Total\">" + productList.getProductTotal() + "</td>");
                                            out.println("</tr>");
                                        }
                                        if(functions.isValueNull((String) hash.get("taxamount")))
                                        {
                                            taxAmt = (String) hash.get("taxamount");
                                        }

                                        out.println("<tr>");
                                        out.println("<td></td>");
                                        out.println("<td></td>");
                                        out.println("<td></td>");
                                        out.println("<td></td>");
                                        out.println("<td align=\"center\">Tax Amount</td>");
                                        out.println("<td align=\"center\">"+taxAmt+"</td>");

                                        out.println("</tr>");

                                        out.println("<tr>");
                                        out.println("<td></td>");
                                        out.println("<td></td>");
                                        out.println("<td></td>");
                                        out.println("<td></td>");
                                        out.println("<td align=\"center\">Grand Total</td>");
                                        out.println("<td align=\"center\">"+(String) hash.get("amount")+"</td>");

                                        out.println("</tr>");

                                    %>
                                    </tbody>


                                </table>
                            </div>
                        </div>
                    </div>
                </div>
                <%
                    }
                %>

            </div>
        </div>
    </div>
</form>
</body>
</html>