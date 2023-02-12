<%@ page import="com.invoice.vo.ProductList" %>
<%@ page import="java.util.*" %>
<%@ page import="com.invoice.vo.InvoiceVO" %>
<%@ page import="sun.font.Script" %>
<%@ include file="ietest.jsp" %>
<%@ include file="Top.jsp"%>

<%
    String memberid = (String)session.getAttribute("merchantid");
    //String currency =  (String)session.getAttribute("currency");
    Hashtable hiddenvariables = (Hashtable)request.getAttribute("hiddenvariables");
    InvoiceVO invoiceVO = (InvoiceVO) hiddenvariables.get("invoicevo");

    String autoSelectTerminal = (String) hiddenvariables.get("autoselectterminal");

    List<ProductList> listProducts = (List<ProductList>) hiddenvariables.get("listofproducts");
    session.setAttribute("listofproducts",listProducts);
    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String invoiceConfirm_Invoice_Confirmation = StringUtils.isNotEmpty(rb1.getString("invoiceConfirm_Invoice_Confirmation"))?rb1.getString("invoiceConfirm_Invoice_Confirmation"): "Invoice Confirmation";
    String invoiceConfirm_Order_ID = StringUtils.isNotEmpty(rb1.getString("invoiceConfirm_Order_ID"))?rb1.getString("invoiceConfirm_Order_ID"): "Order ID";
    String invoiceConfirm_Order_Description = StringUtils.isNotEmpty(rb1.getString("invoiceConfirm_Order_Description"))?rb1.getString("invoiceConfirm_Order_Description"): "Order Description";
    String invoiceConfirm_Order_Amount = StringUtils.isNotEmpty(rb1.getString("invoiceConfirm_Order_Amount"))?rb1.getString("invoiceConfirm_Order_Amount"): "Order Amount";
    String invoiceConfirm_Customer_Email = StringUtils.isNotEmpty(rb1.getString("invoiceConfirm_Customer_Email"))?rb1.getString("invoiceConfirm_Customer_Email"): "Customer Email ID";
    String invoiceConfirm_Customer_Name = StringUtils.isNotEmpty(rb1.getString("invoiceConfirm_Customer_Name"))?rb1.getString("invoiceConfirm_Customer_Name"): "Customer Name";
    String invoiceConfirm_Invoice = StringUtils.isNotEmpty(rb1.getString("invoiceConfirm_Invoice"))?rb1.getString("invoiceConfirm_Invoice"): "Your Invoice will expired after";
    String invoiceConfirm_ProductConfirmation = StringUtils.isNotEmpty(rb1.getString("invoiceConfirm_ProductConfirmation"))?rb1.getString("invoiceConfirm_ProductConfirmation"): "Product Confirmation";
    String invoiceConfirm_Description = StringUtils.isNotEmpty(rb1.getString("invoiceConfirm_Description"))?rb1.getString("invoiceConfirm_Description"): "Description";
    String invoiceConfirm_Unit = StringUtils.isNotEmpty(rb1.getString("invoiceConfirm_Unit"))?rb1.getString("invoiceConfirm_Unit"): "Unit";
    String invoiceConfirm_Amount = StringUtils.isNotEmpty(rb1.getString("invoiceConfirm_Amount"))?rb1.getString("invoiceConfirm_Amount"): "Amount";
    String invoiceConfirm_Quantity = StringUtils.isNotEmpty(rb1.getString("invoiceConfirm_Quantity"))?rb1.getString("invoiceConfirm_Quantity"): "Quantity";
    String invoiceConfirm_tax = StringUtils.isNotEmpty(rb1.getString("invoiceConfirm_tax"))?rb1.getString("invoiceConfirm_tax"): "Tax(%)";
    String invoiceConfirm_Sub_Total = StringUtils.isNotEmpty(rb1.getString("invoiceConfirm_Sub_Total"))?rb1.getString("invoiceConfirm_Sub_Total"): "Sub Total";
    String invoiceConfirm_Generate_Invoice = StringUtils.isNotEmpty(rb1.getString("invoiceConfirm_Generate_Invoice"))?rb1.getString("invoiceConfirm_Generate_Invoice"): "Generate Invoice";
%>


<html>
<head>
    <title>Merchant Order Payments Confirmation</title>

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

</head>


<body class="pace-done widescreen fixed-left-void bodybackground">
<%--<div class="rowcontainer-fluid " >
    <div class="row rowadd" >
        <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">
            <div class="form foreground bodypanelfont_color panelbody_color">
                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="color:#34495e" ><i class="fa fa-th-large"></i>&nbsp;&nbsp;Invoice Confirmation</h2>
                <hr class="hrform">
            </div>--%>

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <form name="form" method="post" action="InvoiceGenerator?ctoken=<%=ctoken%>">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=invoiceConfirm_Invoice_Confirmation%></strong></h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">

                                <div class="form-group col-md-6 has-feedback">
                                    <label  class="col-sm-6 control-label" ><%=invoiceConfirm_Order_ID%></label>
                                    <input type="text" class="form-control" size="10" disabled="true" value=<%=hiddenvariables.get("orderid")%> >
                                </div>

                                <div class="form-group col-md-6 has-feedback">
                                    <label  class="col-sm-6 control-label" ><%=invoiceConfirm_Order_Description%></label>
                                    <input type="text" class="form-control" size="10" disabled="true" value="<%=hiddenvariables.get("orderdesc")%>">
                                </div>

                                <div class="form-group col-md-6 has-feedback">
                                    <label  class="col-sm-6 control-label"><%=invoiceConfirm_Order_Amount%></label>
                                    <input type="text" class="form-control" size="10" disabled="true" value=<%=hiddenvariables.get("amount")%>&nbsp;&nbsp;<%=hiddenvariables.get("currency")%>>
                                </div>

                                <%--<%
                                    if (functions.isValueNull((String) hiddenvariables.get("terminalid")))
                                    {
                                %>

                                <div class="form-group col-md-6 has-feedback">
                                    <label  class="col-sm-6 control-label" >Terminal ID</label>
                                    <input type="text" class="form-control" size="10" disabled="true" value=<%=hiddenvariables.get("terminalid")%> >
                                </div>

                                <div class="form-group col-md-6 has-feedback">
                                    <label  class="col-sm-6 control-label" >Pay Mode-Card Type</label>
                                    <input type="text" class="form-control" size="10" disabled="true" value=<%=hiddenvariables.get("paymodeid")%>-<%=hiddenvariables.get("cardname")%> >
                                </div>
                                <%
                                    }
                                    else
                                    {
                                %>

                                <div class="form-group col-md-6 has-feedback">
                                    <label  class="col-sm-6 control-label" >Terminal ID</label>
                                    <input type="text" class="form-control" size="10" disabled="true" value="-" >
                                </div>

                                <div class="form-group col-md-6 has-feedback">
                                    <label  class="col-sm-6 control-label" >Pay Mode-Card Type</label>
                                    <input type="text" class="form-control" size="10" disabled="true" value="-" >
                                </div>

                                <%
                                    }
                                %>--%>

                                <div class="form-group col-md-6 has-feedback">
                                    <label  class="col-sm-6 control-label" ><%=invoiceConfirm_Customer_Email%></label>
                                    <input type="text" class="form-control" size="10" disabled="true" value=<%=hiddenvariables.get("custemail")%> >
                                </div>

                                <div class="form-group col-md-6 has-feedback">
                                    <label  class="col-sm-6 control-label" ><%=invoiceConfirm_Customer_Name%></label>
                                    <input type="text" class="form-control" size="10" disabled="true" value="<%=hiddenvariables.get("custname")%>">
                                </div>

                                <div class="form-group col-md-6 has-feedback">
                                    <label  class="col-sm-6 control-label" ><%=invoiceConfirm_Invoice%></label>
                                    <input type="text" class="form-control" size="10" disabled="true" value=<%=hiddenvariables.get("expTime")%>&nbsp;<%=hiddenvariables.get("frequency")%> >
                                </div>

                                <div class="form-group col-md-6 has-feedback">
                                    <label  class="col-sm-6 control-label" >Language For Invoice</label>
                                    <input type="text" class="form-control" size="10" disabled="true" value=<%=hiddenvariables.get("defaultLanguage")%>>
                                </div>


                                <div class="form-group col-md-6 has-feedback">

                                        <input type=hidden name=memberid value="<%=hiddenvariables.get("memberid")%>">
                                        <input type=hidden name=totype value="<%=hiddenvariables.get("totype")%>">

                                        <input type=hidden name=amount value="<%=hiddenvariables.get("amount")%>">
                                        <input type=hidden name=orderid value="<%=hiddenvariables.get("orderid")%>">
                                        <input type=hidden name=orderdesc value="<%=hiddenvariables.get("orderdesc")%>">
                                        <input type=hidden name=redirecturl value="<%=hiddenvariables.get("redirecturl")%>">
                                        <input type="hidden" name="currency" value="<%=hiddenvariables.get("currency")%>">
                                        <input type="hidden" name="custname" value="<%=hiddenvariables.get("custname")%>">
                                        <input type="hidden" name="custemail" value="<%=hiddenvariables.get("custemail")%>">

                                        <input type=hidden name=address value="<%=hiddenvariables.get("address")%>">
                                        <input type=hidden name=city value="<%=hiddenvariables.get("city")%>">
                                        <input type=hidden name=state value="<%=hiddenvariables.get("state")%>">
                                        <input type=hidden name=zipcode value="<%=hiddenvariables.get("zipcode")%>">
                                        <input type=hidden name=country value="<%=hiddenvariables.get("country")%>">
                                        <input type=hidden name=phonecc value="<%=hiddenvariables.get("phonecc")%>">
                                        <input type=hidden name=phone value="<%=hiddenvariables.get("phone")%>">
                                        <input type="hidden" name="paymodeid" value="<%=hiddenvariables.get("paymode")%>">
                                        <input type="hidden" name="cardtype" value="<%=hiddenvariables.get("cardname")%>">
                                        <input type="hidden" name="terminalid" value="<%=hiddenvariables.get("terminalid")%>">
                                        <input type="hidden" name="isCredential" value="<%=hiddenvariables.get("isCredential")%>">
                                        <input type="hidden" name="username" value="<%=hiddenvariables.get("username")%>">
                                        <input type="hidden" name="password" value="<%=hiddenvariables.get("password")%>">
                                        <input type="hidden" name="question" value="<%=hiddenvariables.get("question")%>">
                                        <input type="hidden" name="answer" value="<%=hiddenvariables.get("answer")%>">
                                        <input type="hidden" name="expPeriod" value="<%=hiddenvariables.get("expPeriod")%>">
                                        <input type="hidden" name="expTime" value="<%=hiddenvariables.get("expTime")%>">
                                        <input type="hidden" name="frequency" value="<%=hiddenvariables.get("frequency")%>">
                                        <input type="hidden" name="taxamount" value="<%=hiddenvariables.get("taxamount")%>">
                                        <input type="hidden" name="defaultLanguage" value="<%=hiddenvariables.get("defaultLanguage")%>">
                                </div>

                            </div>
                        </div>
                    </div>

                    <%
                        if(listProducts.size()>0)
                        {
                    %>

                    <div class="row">
                        <div class="col-sm-12 portlets ui-sortable">
                            <div class="widget">

                                <div class="widget-header transparent">
                                    <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=invoiceConfirm_ProductConfirmation%></strong></h2>
                                    <div class="additional-btn">
                                        <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                        <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                        <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                    </div>
                                </div>

                                <div class="widget-content padding">
                                    <table id="myTable" class="display table table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <%--<table  id="datatables-2" class="table table-striped table-bordered" cellspacing="0" width="100%">--%>
                                        <thead>
                                        <tr style="color: white;">
                                            <th align="center"><%=invoiceConfirm_Description%></th>
                                            <th align="center"><%=invoiceConfirm_Unit%></th>
                                            <th align="center"><%=invoiceConfirm_Amount%></th>
                                            <th align="center"><%=invoiceConfirm_Quantity%></th>
                                            <th align="center"><%=invoiceConfirm_tax%></th>
                                            <th align="center"><%=invoiceConfirm_Sub_Total%></th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                            <%
                                                String grandTotal = (String)hiddenvariables.get("grandtotal");
                                                String taxAmount = (String)hiddenvariables.get("taxamount");
                                                String gstcb = (String)hiddenvariables.get("gstcb");
                                                for(ProductList productList : listProducts)
                                                {
                                                    out.println("<tr>");
                                                    out.println("<td data-label=\"Description\" >" +productList.getProductDescription()+ "</td>");
                                                    out.println("<td data-label=\"Unit\" >" +productList.getProductUnit()+ "</td>");
                                                    out.println("<td data-label=\"Amount\">" +productList.getProductAmount()+ "</td>");
                                                    out.println("<td data-label=\"Quantity\" >" +productList.getQuantity()+ "</td>");
                                                    out.println("<td data-label=\"Tax\" >" +productList.getTax()+ "</td>");
                                                    out.println("<td data-label=\"Sub Total\">" +productList.getProductTotal()+ "</td>");
                                                    out.println("</tr>");

                                                }
                                                out.println("<tr>");
                                                out.println("<td></td>");
                                                out.println("<td></td>");
                                                out.println("<td></td>");
                                                out.println("<td></td>");
                                                out.println("<td>Tax Amount ("+gstcb+"%)</td>");
                                                out.println("<td>"+taxAmount+"</td>");
                                                out.println("</tr>");
                                                out.println("<tr>");
                                                out.println("<td></td>");
                                                out.println("<td></td>");
                                                out.println("<td></td>");
                                                out.println("<td></td>");
                                                out.println("<td>Grand Total</td>");
                                                out.println("<td>"+grandTotal+"</td>");
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

                    <div class="row">
                        <div class="col-sm-12 portlets ui-sortable">
                            <div class="widget">


                                <br>
                                <input type="hidden" name="ctokenctoken" value=<%=ctoken%>>
                                <input type="hidden" name="autoselectterminal" value=<%=autoSelectTerminal%>>
                                <div class="form-group col-md-5 has-feedback"></div>
                                <div class="form-group col-md-2 has-feedback">
                                    <button type="submit" name="Submit" class="btn btn-default" >
                                        <i class="fa fa-save"></i>
                                        &nbsp;&nbsp;<%=invoiceConfirm_Generate_Invoice%>
                                    </button>
                                </div>
                                <div class="form-group col-md-5 has-feedback"></div>
                                <br>
                            </div>
                        </div>
                    </div>
                </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
