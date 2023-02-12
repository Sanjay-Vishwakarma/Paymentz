<%@page import = "java.util.Hashtable"%>
<%@ page import="com.invoice.vo.InvoiceVO" %>
<%@ include file="Top.jsp" %>
<%
    String currency =  (String)session.getAttribute("currency");
    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String invoiceGenerated_Invoice_Generated = StringUtils.isNotEmpty(rb1.getString("invoiceGenerated_Invoice_Generated"))?rb1.getString("invoiceGenerated_Invoice_Generated"): "Invoice Generated Successfully";
    String invoiceGenerated_Invoiceno = StringUtils.isNotEmpty(rb1.getString("invoiceGenerated_Invoiceno"))?rb1.getString("invoiceGenerated_Invoiceno"): "Invoice No:";
    String invoiceGenerated_date = StringUtils.isNotEmpty(rb1.getString("invoiceGenerated_date"))?rb1.getString("invoiceGenerated_date"): "Date:";
    String invoiceGenerated_Invoicesent = StringUtils.isNotEmpty(rb1.getString("invoiceGenerated_Invoicesent"))?rb1.getString("invoiceGenerated_Invoicesent"): "Invoice Sent To :";
    String invoiceGenerated_sms = StringUtils.isNotEmpty(rb1.getString("invoiceGenerated_sms"))?rb1.getString("invoiceGenerated_sms"): "SMS Sent To :";
    String invoiceGenerated_Customer_Details = StringUtils.isNotEmpty(rb1.getString("invoiceGenerated_Customer_Details"))?rb1.getString("invoiceGenerated_Customer_Details"): "Customer Details";
    String invoiceGenerated_Customer_Name = StringUtils.isNotEmpty(rb1.getString("invoiceGenerated_Customer_Name"))?rb1.getString("invoiceGenerated_Customer_Name"): "Customer Name:";
    String invoiceGenerated_phone = StringUtils.isNotEmpty(rb1.getString("invoiceGenerated_phone"))?rb1.getString("invoiceGenerated_phone"): "Customer Phone NO:";
    String invoiceGenerated_Address = StringUtils.isNotEmpty(rb1.getString("invoiceGenerated_Address"))?rb1.getString("invoiceGenerated_Address"): "Customer Address";
    String invoiceGenerated_InvoiceDetails = StringUtils.isNotEmpty(rb1.getString("invoiceGenerated_InvoiceDetails"))?rb1.getString("invoiceGenerated_InvoiceDetails"): "Invoice Details";
    String invoiceGenerated_orderid = StringUtils.isNotEmpty(rb1.getString("invoiceGenerated_orderid"))?rb1.getString("invoiceGenerated_orderid"): "Order ID:";
    String invoiceGenerated_description = StringUtils.isNotEmpty(rb1.getString("invoiceGenerated_description"))?rb1.getString("invoiceGenerated_description"): "Order Description:";
    String invoiceGenerated_amount= StringUtils.isNotEmpty(rb1.getString("invoiceGenerated_amount"))?rb1.getString("invoiceGenerated_amount"): "Order Amount:";
    String invoiceGenerated_total = StringUtils.isNotEmpty(rb1.getString("invoiceGenerated_total"))?rb1.getString("invoiceGenerated_total"): "Total amount after Due Date";
    String invoiceGenerated_click = StringUtils.isNotEmpty(rb1.getString("invoiceGenerated_click"))?rb1.getString("invoiceGenerated_click"): "Click here to go to Invoice";

    Hashtable hiddenvariables = (Hashtable)request.getAttribute("hiddenvariables");
    InvoiceVO invoiceVO = new InvoiceVO();
    invoiceVO= (InvoiceVO) hiddenvariables.get("invoicevo");
    String telcc = "";
    String address = "";
    String city = "";
    String state = "";
    String country = "";
    String zip = "";
    String telno = "";
    String defaultLanguage = "";

    if (functions.isValueNull(String.valueOf(hiddenvariables.get("phonecc"))))
    {
        if (functions.isValueNull(String.valueOf(hiddenvariables.get("phonecc"))))
        {
            telcc = String.valueOf(hiddenvariables.get("phonecc"));
        }
    }
    if (functions.isValueNull(String.valueOf(hiddenvariables.get("phone"))))
    {
        telno = String.valueOf(hiddenvariables.get("phone"));
    }
    if (functions.isValueNull(String.valueOf(hiddenvariables.get("address"))))
    {
        address = String.valueOf(hiddenvariables.get("address"));
    }
    if (functions.isValueNull(String.valueOf(hiddenvariables.get("city"))))
    {
        city = String.valueOf(hiddenvariables.get("city"));
    }
    if (functions.isValueNull(String.valueOf(hiddenvariables.get("state"))))
    {
        state = String.valueOf(hiddenvariables.get("state"));
    }
    if (functions.isValueNull(String.valueOf(hiddenvariables.get("country"))))
    {
        country = String.valueOf(hiddenvariables.get("country"));
    }
    if (functions.isValueNull(String.valueOf(hiddenvariables.get("zipcode"))))
    {
        zip = String.valueOf(hiddenvariables.get("zipcode"));
    }
    if (functions.isValueNull(String.valueOf(hiddenvariables.get("defaultLanguage"))))
    {
        defaultLanguage = String.valueOf(hiddenvariables.get("defaultLanguage"));
    }

%>
<html lang="en">
<head>
    <title> Invoice </title>

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

</head>
<body class="pace-done widescreen fixed-left-void">
<script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>

<script type="text/javascript">

$(document).ready(function(){

        var amount = $('#amount').text();
        var latefee = $('#latefee').text();
        var result = parseFloat(amount) + parseFloat(latefee);
        var total_id = document.getElementById("total_id").value;
        var currency_id = document.getElementById("currency_id").value;
        total_id = result.toFixed(2) + ' ' + ' ' + currency_id;
        var total = $('#result').text(total_id).toFixed(2);

});

</script>
<%--<div class="rowcontainer-fluid " >
    <div class="row rowadd" >
        <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">
            <div class="form foreground bodypanelfont_color panelbody_color">
                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="color:#34495e" ><i class="fa fa-th-large"></i>&nbsp;&nbsp;Invoice</h2>
                <hr class="hrform">
            </div>--%>

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">

                <div class="col-sm-12 portlets ui-sortable">

                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=invoiceGenerated_Invoice_Generated%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div class="row">
                                <div class="col-sm-4">
                                    <div class="invoice-address">
                                        <h4><b><%=invoiceGenerated_Invoiceno%> </b></h4><h5><b><%=hiddenvariables.get("invoiceno")%></b></h5>
                                        <h4><b><%=invoiceGenerated_date%> </b></h4><h5><b><%=hiddenvariables.get("date")%></b></h5>

                                    </div>


                                </div>
                                <%
                                    if (invoiceVO.getIsemail().equals("Y"))
                                    {
                                %>

                                <div class="push-down-10 pull-right">
                                    <div class="company-column">
                                        <h4><b><%=invoiceGenerated_Invoicesent%></b></h4>
                                        <h5><b><%=hiddenvariables.get("custemail")%></b></h5>
                                        <%
                                            }
                                        %>
                                        <%
                                            if (invoiceVO.getIssms().equals("Y"))
                                            {
                                        %>
                                        <h4><b><%=invoiceGenerated_sms%></b></h4>
                                        <h5><b><%=telcc%>-<%=telno%></b></h5>
                                        <%
                                            }
                                        %>
                                    </div>
                                </div>






                                </div>

                            </div>

                        </div>


                        <%--<div class="row" >
                            <div class="col-lg-12">
                                <div class="panel panel-default" >
                                    <div class="panel-heading" >
                                        Invoice Generated
                                    </div>
                                    <br><br>
                                    <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
                                        <tr>
                                            <td>
                                                <table width="100%" border="0" cellspacing="0" cellpadding="0">
                                                    <tr>
                                                        <td colspan="2"></td>
                                                    </tr>
                                                    <tr>
                                                        <td colspan="2">

                                                            <table width="80%" border="0" cellspacing="0" cellpadding="0" align="center" >
                                                                <tr>
                                                                    <td width="100%" align=center colspan=3 class="textb">
                                                                        <div align="center">Invoice No:
                                                                            &nbsp;&nbsp;
                                                                            <%=hiddenvariables.get("invoiceno")%>
                                                                        </div><br>
                                                                        <div align="right">Date.
                                                                            &nbsp;&nbsp;
                                                                            <%=hiddenvariables.get("date")%>
                                                                        </div>
                                                                    </td>
                                                                </tr>
                                                                <tr >
                                                                    <td width="21%">&nbsp;</td>
                                                                    <td width="1%">&nbsp;</td>
                                                                    <td colspan="6" width="78%">&nbsp;</td>
                                                                </tr>
                                                                <tr class="textb">
                                                                    <td width="21%">Invoice Sent To
                                                                        :&nbsp; &nbsp;</td>
                                                                    <td width="1%">&nbsp;</td>
                                                                    <td colspan="6" width="78%" align=left>
                                                                        <%=hiddenvariables.get("custemail")%></td>
                                                                </tr>
                                                                <tr >
                                                                    <td width="21%">&nbsp;</td>
                                                                    <td width="1%">&nbsp;</td>
                                                                    <td colspan="6" width="78%">&nbsp;</td>
                                                                </tr>
                                                                <tr class="textb">
                                                                    <td width="21%">Customer Details:&nbsp; &nbsp;
                                                                    </td>
                                                                    <td width="1%">&nbsp;</td>
                                                                    <td colspan="6" width="78%">&nbsp;</td>
                                                                </tr>
                                                                <tr class="textb">
                                                                    <td width="21%">&nbsp; &nbsp;
                                                                    </td>
                                                                    <td width="1%" >&nbsp;</td>
                                                                    <td width="78%" colspan="6" align=left>Customer Name.
                                                                        :&nbsp;&nbsp;
                                                                        <%=hiddenvariables.get("custname")%></td>
                                                                </tr>
                                                                <tr class="textb">
                                                                    <td width="21%">&nbsp; &nbsp;

                                                                    </td>
                                                                    <td width="1%" >&nbsp;</td>
                                                                    <td width="78%" colspan="6" align=left>Customer Phone No.
                                                                        :&nbsp;&nbsp;
                                                                        <%=hiddenvariables.get("phonecc")%>-<%=hiddenvariables.get("phone")%></td>
                                                                </tr>
                                                                <tr class="textb">
                                                                    <td width="21%">&nbsp; &nbsp;
                                                                    </td>
                                                                    <td width="1%" >&nbsp;</td>
                                                                    <td width="78%" colspan="6" align=left>
                                                                        <table>
                                                                            <tr class="textb">
                                                                                <td>Customer Address.
                                                                                    :&nbsp;&nbsp;
                                                                                    <%=hiddenvariables.get("address")%> ,<BR> <%=hiddenvariables.get("city")%> , <%=hiddenvariables.get("state")%> , <%=hiddenvariables.get("country")%> - <%=hiddenvariables.get("zipcode")%>
                                                                                </td>
                                                                            </tr>
                                                                        </table>
                                                                    </td>
                                                                </tr>
                                                                <tr >
                                                                    <td width="21%">&nbsp;</td>
                                                                    <td width="1%">&nbsp;</td>
                                                                    <td colspan="6" width="78%">&nbsp;</td>
                                                                </tr>
                                                                <tr class="textb">
                                                                    <td width="21%"><div align="left">Invoice Details
                                                                    </div></td>
                                                                    <td width="1%">&nbsp;</td>
                                                                    <td colspan="6" width="78%">&nbsp;</td>
                                                                </tr>
                                                                <tr class="textb">
                                                                    <td width="21%">&nbsp; &nbsp;

                                                                    </td>
                                                                    <td width="1%" >&nbsp;</td>
                                                                    <td width="78%" colspan="6" align=left>Order
                                                                        ID:&nbsp;&nbsp;
                                                                        <%=hiddenvariables.get("orderid")%></td>
                                                                </tr>

                                                                <tr class="textb">
                                                                    <td width="21%">&nbsp; &nbsp;

                                                                    </td>
                                                                    <td width="1%" >&nbsp;</td>
                                                                    <td width="78%" colspan="6" align=left>Order
                                                                        Description:&nbsp;&nbsp;
                                                                        <%=hiddenvariables.get("orderdesc")%></td>
                                                                </tr>
                                                                <tr class="textb">
                                                                    <td>&nbsp; &nbsp;</td>
                                                                    <td >&nbsp;</td>
                                                                    <td colspan="6">Order Amount:
                                                                        <%=hiddenvariables.get("amount")%>&nbsp;&nbsp;<%=hiddenvariables.get("currency")%></td>
                                                                </tr>
                                                                <tr >
                                                                    <td width="21%">&nbsp;</td>
                                                                    <td width="1%">&nbsp;</td>
                                                                    <td colspan="6" width="78%">&nbsp;</td>
                                                                </tr>
                                                                <tr >
                                                                    <td width="21%">&nbsp;</td>
                                                                    <td width="1%">&nbsp;</td>
                                                                    <td colspan="6" width="78%">&nbsp;</td>
                                                                </tr>
                                                                <tr >
                                                                    <td width="21%">&nbsp;</td>
                                                                    <td width="1%">&nbsp;</td>
                                                                    <td colspan="6" width="78%">
                                                                        <form action=/merchant/servlet/Invoice?ctoken=<%=ctoken%>">
                                                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                                                        <button type="submit" name="submit"  class="buttonform" style="width:250px;float:right;">
                                                                            <i class="fa fa-repeat" ></i>&nbsp;&nbsp;Click Here to Go To Invoice
                                                                        </button>
                                                                        </form>
                                                                    </td>

                                                                </tr>
                                                                <tr >
                                                                    <td width="21%">&nbsp;</td>
                                                                    <td width="1%">&nbsp;</td>
                                                                    <td colspan="6" width="78%">&nbsp;</td>
                                                                </tr>
                                                            </table>
                                                        </td>
                                                    </tr>
                                                </table>
                                            </td>
                                        </tr>

                                    </table>

                                </div>
                            </div>
                        </div>--%>
                        <div class="invoice" style="float: left; width: 100%;">
                            <%--<div class="push-down-10 pull-right">

                                <div class="invoice-address">
                                    <h5>Invoice Sent To : </h5>
                                    <p><%=hiddenvariables.get("custemail")%></p>
                                </div>
                            </div>--%>
                            <%--<div class="col-md-4">

                                <div class="invoice-address" style="float: left;width: 100%;margin-bottom: 10px;">
                                    <h5>Invoice Sent To : </h5>
                                    <p><%=hiddenvariables.get("custemail")%></p>
                                </div>

                            </div>--%>
                            <div class="col-md-6">

                                <div class="invoice-address" style="float: left;width: 100%;margin-bottom: 10px;">
                                    <h5><b><%=invoiceGenerated_Customer_Details%></b></h5>
                                    <table class="table table-striped" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tbody><tr>
                                            <td width="200"><%=invoiceGenerated_Customer_Name%></td><td class="text-right"><%=hiddenvariables.get("custname")%></td>
                                        </tr>
                                        <tr>
                                            <td><%=invoiceGenerated_phone%></td><td class="text-right"> <%=telcc%>-<%=telno%></td>
                                        </tr>
                                        <tr>
                                            <td><%=invoiceGenerated_Address%></td><td class="text-right"><%=address%> <BR> <%=city%>  <%=state%>  <%=country%>  <%=zip%></td>
                                        </tr>
                                        </tbody></table>

                                </div>

                            </div>
                            <div class="col-md-6">

                                <div class="invoice-address" style="float: left;width: 100%;margin-bottom: 10px;">
                                    <h5><b><%=invoiceGenerated_InvoiceDetails%></b></h5>
                                    <table class="table table-striped" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tbody><tr>
                                            <td width="200"><%=invoiceGenerated_orderid%></td><td class="text-right"><%=hiddenvariables.get("orderid")%></td>
                                        </tr>
                                        <tr>
                                            <td><%=invoiceGenerated_description%></td><td class="text-right"><%=hiddenvariables.get("orderdesc")%></td>
                                        </tr>
                                        <tr>
                                            <td><%=invoiceGenerated_amount%></td><td class="text-right" id="amount"><%=hiddenvariables.get("amount")%>&nbsp;&nbsp;<%=hiddenvariables.get("currency")%></td>
                                        </tr>
                                        <%

                                            if (invoiceVO.getIslatefee().equals("Y"))
                                            {
                                        %>

                                        <tr>
                                            <input type="hidden" id="total_id" value="">
                                            <input type="hidden" id="currency_id" value="<%=hiddenvariables.get("currency")%>">
                                            <td><%=invoiceGenerated_total%></td><td class="text-right"id="result"></td>
                                        </tr>

                                            <td class="text-right"id="latefee"style="display:none;visibility: hidden; user-select: none"><%=invoiceVO.getLatefee()%>
                                            &nbsp;<%=hiddenvariables.get("currency")%></td>



                                        <%
                                            }
                                        %>
                                        </tbody>
                                    </table>
                                </div>
                            </div>

                            <div class="col-md-12">
                                <div class="pull-right push-down-20">
                                    <form action=/merchant/servlet/Invoice?ctoken=<%=ctoken%>">
                                        <input type="hidden" value="<%=ctoken%>" name="ctoken">
                                        <%--<input type="hidden" value="<%=hiddenvariables.get("date")%>" name="fdate">--%>
                                        <input type="hidden" value="<%=hiddenvariables.get("invoiceno")%>" name="invoiceno">
                                        <%--<input type="hidden" value="<%=hiddenvariables.get("date")%>" name="tdate">--%>
                                        <button type="submit"  name="submit" class="btn btn-default"><span class="fa fa-credit-card"></span> <%=invoiceGenerated_click%></button>
                                    </form>
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
</html>
