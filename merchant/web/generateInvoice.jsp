<%@ page import="com.invoice.vo.DefaultProductList" %>
<%@ page import="com.invoice.vo.InvoiceVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.*" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>

<%@ include file="Top.jsp" %>
<% String company = (String) session.getAttribute("company");
    session.setAttribute("submit", "Generate Invoice");

    String multiCurrency = (String) session.getAttribute("multiCurrencySupport");
    TreeMap<String, String> currencyCodeHash = new TreeMap<String, String>();
    currencyCodeHash.put("AED", "784");
    currencyCodeHash.put("AFN", "971");
    currencyCodeHash.put("ALL", "008");
    currencyCodeHash.put("AMD", "051");
    currencyCodeHash.put("ANG", "532");
    currencyCodeHash.put("AOA", "973");
    currencyCodeHash.put("ARS", "032");
    currencyCodeHash.put("AUD", "036");
    currencyCodeHash.put("AWG", "533");
    currencyCodeHash.put("AZN", "944");
    currencyCodeHash.put("BAM", "977");
    currencyCodeHash.put("BBD", "052");
    currencyCodeHash.put("BDT", "050");
    currencyCodeHash.put("BGN", "975");
    currencyCodeHash.put("BHD", "048");
    currencyCodeHash.put("BIF", "108");
    currencyCodeHash.put("BMD", "060");
    currencyCodeHash.put("BND", "096");
    currencyCodeHash.put("BOB", "068");
    currencyCodeHash.put("BOV", "984");
    currencyCodeHash.put("BRL", "986");
    currencyCodeHash.put("BSD", "044");
    currencyCodeHash.put("BTN", "064");
    currencyCodeHash.put("BWP", "072");
    currencyCodeHash.put("BYR", "974");
    currencyCodeHash.put("BZD", "084");
    currencyCodeHash.put("CAD", "124");
    currencyCodeHash.put("CDF", "976");
    currencyCodeHash.put("CHE", "947");
    currencyCodeHash.put("CHF", "756");
    currencyCodeHash.put("CHW", "948");
    currencyCodeHash.put("CLF", "990");
    currencyCodeHash.put("CLP", "152");
    currencyCodeHash.put("CNY", "156");
    currencyCodeHash.put("COP", "170");
    currencyCodeHash.put("COU", "970");
    currencyCodeHash.put("CRC", "188");
    currencyCodeHash.put("CUC", "931");
    currencyCodeHash.put("CUP", "192");
    currencyCodeHash.put("CVE", "132");
    currencyCodeHash.put("CZK", "203");
    currencyCodeHash.put("DJF", "262");
    currencyCodeHash.put("DKK", "208");
    currencyCodeHash.put("DOP", "214");
    currencyCodeHash.put("DZD", "012");
    currencyCodeHash.put("EGP", "818");
    currencyCodeHash.put("ERN", "232");
    currencyCodeHash.put("ETB", "230");
    currencyCodeHash.put("EUR", "978");
    currencyCodeHash.put("FJD", "242");
    currencyCodeHash.put("FKP", "238");
    currencyCodeHash.put("GBP", "826");
    currencyCodeHash.put("GEL", "981");
    currencyCodeHash.put("GHS", "936");
    currencyCodeHash.put("GIP", "292");
    currencyCodeHash.put("GMD", "270");
    currencyCodeHash.put("GNF", "324");
    currencyCodeHash.put("GTQ", "320");
    currencyCodeHash.put("GYD", "328");
    currencyCodeHash.put("HKD", "344");
    currencyCodeHash.put("HNL", "340");
    currencyCodeHash.put("HRK", "191");
    currencyCodeHash.put("HTG", "332");
    currencyCodeHash.put("HUF", "348");
    currencyCodeHash.put("IDR", "360");
    currencyCodeHash.put("ILS", "376");
    currencyCodeHash.put("INR", "356");
    currencyCodeHash.put("IQD", "368");
    currencyCodeHash.put("IRR", "364");
    currencyCodeHash.put("ISK", "352");
    currencyCodeHash.put("JMD", "388");
    currencyCodeHash.put("JOD", "400");
    currencyCodeHash.put("JPY", "392");
    currencyCodeHash.put("KES", "404");
    currencyCodeHash.put("KGS", "417");
    currencyCodeHash.put("KHR", "116");
    currencyCodeHash.put("KMF", "174");
    currencyCodeHash.put("KPW", "408");
    currencyCodeHash.put("KRW", "410");
    currencyCodeHash.put("KWD", "414");
    currencyCodeHash.put("KYD", "136");
    currencyCodeHash.put("KZT", "398");
    currencyCodeHash.put("LAK", "418");
    currencyCodeHash.put("LBP", "422");
    currencyCodeHash.put("LKR", "144");
    currencyCodeHash.put("LRD", "430");
    currencyCodeHash.put("LSL", "426");
    currencyCodeHash.put("LTL", "440");
    currencyCodeHash.put("LVL", "428");
    currencyCodeHash.put("LYD", "434");
    currencyCodeHash.put("MAD", "504");
    currencyCodeHash.put("MDL", "498");
    currencyCodeHash.put("MGA", "969");
    currencyCodeHash.put("MKD", "807");
    currencyCodeHash.put("MMK", "104");
    currencyCodeHash.put("MNT", "496");
    currencyCodeHash.put("MOP", "446");
    currencyCodeHash.put("MRO", "478");
    currencyCodeHash.put("MUR", "480");
    currencyCodeHash.put("MVR", "462");
    currencyCodeHash.put("MWK", "454");
    currencyCodeHash.put("MXN", "484");
    currencyCodeHash.put("MXV", "979");
    currencyCodeHash.put("MYR", "458");
    currencyCodeHash.put("MZN", "943");
    currencyCodeHash.put("NAD", "516");
    currencyCodeHash.put("NGN", "566");
    currencyCodeHash.put("NIO", "558");
    currencyCodeHash.put("NOK", "578");
    currencyCodeHash.put("NPR", "524");
    currencyCodeHash.put("NZD", "554");
    currencyCodeHash.put("OMR", "512");
    currencyCodeHash.put("PAB", "590");
    currencyCodeHash.put("PEN", "604");
    currencyCodeHash.put("PGK", "598");
    currencyCodeHash.put("PHP", "608");
    currencyCodeHash.put("PKR", "586");
    currencyCodeHash.put("PLN", "985");
    currencyCodeHash.put("PYG", "600");
    currencyCodeHash.put("QAR", "634");
    currencyCodeHash.put("RON", "946");
    currencyCodeHash.put("RSD", "941");
    currencyCodeHash.put("RUB", "643");
    currencyCodeHash.put("RWF", "646");
    currencyCodeHash.put("SAR", "682");
    currencyCodeHash.put("SBD", "090");
    currencyCodeHash.put("SCR", "690");
    currencyCodeHash.put("SDG", "938");
    currencyCodeHash.put("SEK", "752");
    currencyCodeHash.put("SGD", "702");
    currencyCodeHash.put("SHP", "654");
    currencyCodeHash.put("SLL", "694");
    currencyCodeHash.put("SOS", "706");
    currencyCodeHash.put("SRD", "968");
    currencyCodeHash.put("SSP", "728");
    currencyCodeHash.put("STD", "678");
    currencyCodeHash.put("SVC", "222");
    currencyCodeHash.put("SYP", "760");
    currencyCodeHash.put("SZL", "748");
    currencyCodeHash.put("THB", "764");
    currencyCodeHash.put("TJS", "972");
    currencyCodeHash.put("TMT", "934");
    currencyCodeHash.put("TND", "788");
    currencyCodeHash.put("TOP", "776");
    currencyCodeHash.put("TRY", "949");
    currencyCodeHash.put("TTD", "780");
    currencyCodeHash.put("TWD", "901");
    currencyCodeHash.put("TZS", "834");
    currencyCodeHash.put("UAH", "980");
    currencyCodeHash.put("UGX", "800");
    currencyCodeHash.put("USD", "840");
    currencyCodeHash.put("USN", "997");
    currencyCodeHash.put("USS", "998");
    currencyCodeHash.put("UYI", "940");
    currencyCodeHash.put("UYU", "858");
    currencyCodeHash.put("UZS", "860");
    currencyCodeHash.put("VEF", "937");
    currencyCodeHash.put("VND", "704");
    currencyCodeHash.put("VUV", "548");
    currencyCodeHash.put("WST", "882");
    currencyCodeHash.put("XAF", "950");
    currencyCodeHash.put("XAG", "961");
    currencyCodeHash.put("XAU", "959");
    currencyCodeHash.put("XBA", "955");
    currencyCodeHash.put("XBB", "956");
    currencyCodeHash.put("XBC", "957");
    currencyCodeHash.put("XBD", "958");
    currencyCodeHash.put("XCD", "951");
    currencyCodeHash.put("XDR", "960");
    currencyCodeHash.put("XFU", "Nil");
    currencyCodeHash.put("XOF", "952");
    currencyCodeHash.put("XPD", "964");
    currencyCodeHash.put("XPF", "953");
    currencyCodeHash.put("XPT", "962");
    currencyCodeHash.put("XTS", "963");
    currencyCodeHash.put("XXX", "999");
    currencyCodeHash.put("YER", "886");
    currencyCodeHash.put("ZAR", "710");
    currencyCodeHash.put("ZMW", "967");
    currencyCodeHash.put("ZWL", "716");

    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String generateInvoice_invoice_details = StringUtils.isNotEmpty(rb1.getString("generateInvoice_invoice_details")) ? rb1.getString("generateInvoice_invoice_details") : "Invoice Details";
    String generateInvoice_merchant_id = StringUtils.isNotEmpty(rb1.getString("generateInvoice_merchant_id")) ? rb1.getString("generateInvoice_merchant_id") : "Merchant ID";
    String generateInvoice_order_id = StringUtils.isNotEmpty(rb1.getString("generateInvoice_order_id")) ? rb1.getString("generateInvoice_order_id") : "Order ID*";
    String generateInvoice_order_description = StringUtils.isNotEmpty(rb1.getString("generateInvoice_order_description")) ? rb1.getString("generateInvoice_order_description") : "Order Description";
    String generateInvoice_amount = StringUtils.isNotEmpty(rb1.getString("generateInvoice_amount")) ? rb1.getString("generateInvoice_amount") : "Amount*";
    String generateInvoice_currency = StringUtils.isNotEmpty(rb1.getString("generateInvoice_currency")) ? rb1.getString("generateInvoice_currency") : "Currency*";
    String generateInvoice_invoice_select_currency = StringUtils.isNotEmpty(rb1.getString("generateInvoice_invoice_select_currency")) ? rb1.getString("generateInvoice_invoice_select_currency") : "--Select Currency--";
    String generateInvoice_payment_option = StringUtils.isNotEmpty(rb1.getString("generateInvoice_payment_option")) ? rb1.getString("generateInvoice_payment_option") : "Select Terminal Type / Payment Option";
    String generateInvoice_select_terminalid = StringUtils.isNotEmpty(rb1.getString("generateInvoice_select_terminalid")) ? rb1.getString("generateInvoice_select_terminalid") : "--Select TerminalID--";
    String generateInvoice_select_terminalid1 = StringUtils.isNotEmpty(rb1.getString("generateInvoice_select_terminalid1")) ? rb1.getString("generateInvoice_select_terminalid1") : "--Select TerminalID--";
    String generateInvoice_product_details = StringUtils.isNotEmpty(rb1.getString("generateInvoice_product_details")) ? rb1.getString("generateInvoice_product_details") : "Product Details";
    String generateInvoice_product_description = StringUtils.isNotEmpty(rb1.getString("generateInvoice_product_description")) ? rb1.getString("generateInvoice_product_description") : "Product Description";
    String generateInvoice_unit = StringUtils.isNotEmpty(rb1.getString("generateInvoice_unit")) ? rb1.getString("generateInvoice_unit") : "Unit";
    String generateInvoice_product_amount = StringUtils.isNotEmpty(rb1.getString("generateInvoice_product_amount")) ? rb1.getString("generateInvoice_product_amount") : "Product Amount";
    String generateInvoice_quantity = StringUtils.isNotEmpty(rb1.getString("generateInvoice_quantity")) ? rb1.getString("generateInvoice_quantity") : "Quantity";
    String generateInvoice_tax = StringUtils.isNotEmpty(rb1.getString("generateInvoice_tax")) ? rb1.getString("generateInvoice_tax") : "Tax(%)";
    String generateInvoice_total = StringUtils.isNotEmpty(rb1.getString("generateInvoice_total")) ? rb1.getString("generateInvoice_total") : "Total";
    String generateInvoice_select_unit = StringUtils.isNotEmpty(rb1.getString("generateInvoice_select_unit")) ? rb1.getString("generateInvoice_select_unit") : "Select Unit";
    String generateInvoice_apply_gst = StringUtils.isNotEmpty(rb1.getString("generateInvoice_apply_gst")) ? rb1.getString("generateInvoice_apply_gst") : "Apply GST/VAT on Total Amount?";
    String generateInvoice_tax_amount = StringUtils.isNotEmpty(rb1.getString("generateInvoice_tax_amount")) ? rb1.getString("generateInvoice_tax_amount") : "Tax Amount :";
    String generateInvoice_grand_total = StringUtils.isNotEmpty(rb1.getString("generateInvoice_grand_total")) ? rb1.getString("generateInvoice_grand_total") : "Grand Total:";
    String generateInvoice_customers = StringUtils.isNotEmpty(rb1.getString("generateInvoice_customers")) ? rb1.getString("generateInvoice_customers") : "Customer Details";
    String generateInvoice_customer_name = StringUtils.isNotEmpty(rb1.getString("generateInvoice_customer_name")) ? rb1.getString("generateInvoice_customer_name") : "Customer Name";
    String generateInvoice_emailid = StringUtils.isNotEmpty(rb1.getString("generateInvoice_emailid")) ? rb1.getString("generateInvoice_emailid") : "Customer Email ID*";
    String generateInvoice_address = StringUtils.isNotEmpty(rb1.getString("generateInvoice_address")) ? rb1.getString("generateInvoice_address") : "Address";
    String generateInvoice_city = StringUtils.isNotEmpty(rb1.getString("generateInvoice_city")) ? rb1.getString("generateInvoice_city") : "City";
    String generateInvoice_zip_code = StringUtils.isNotEmpty(rb1.getString("generateInvoice_zip_code")) ? rb1.getString("generateInvoice_zip_code") : "Zip Code";
    String generateInvoice_country = StringUtils.isNotEmpty(rb1.getString("generateInvoice_country")) ? rb1.getString("generateInvoice_country") : "Country";
    String generateInvoice_select_a_country = StringUtils.isNotEmpty(rb1.getString("generateInvoice_select_a_country")) ? rb1.getString("generateInvoice_select_a_country") : "Select a Country";
    String generateInvoice_code = StringUtils.isNotEmpty(rb1.getString("generateInvoice_code")) ? rb1.getString("generateInvoice_code") : "Code";
    String generateInvoice_state = StringUtils.isNotEmpty(rb1.getString("generateInvoice_state")) ? rb1.getString("generateInvoice_state") : "State";
    String generateInvoice_state_code = StringUtils.isNotEmpty(rb1.getString("generateInvoice_state_code")) ? rb1.getString("generateInvoice_state_code") : "StateCode";
    String generateInvoice_country_code = StringUtils.isNotEmpty(rb1.getString("generateInvoice_country_code")) ? rb1.getString("generateInvoice_country_code") : "Country-code";
    String generateInvoice_phone_number = StringUtils.isNotEmpty(rb1.getString("generateInvoice_phone_number")) ? rb1.getString("generateInvoice_phone_number") : "Phone Number";
    String generateInvoice_configuration = StringUtils.isNotEmpty(rb1.getString("generateInvoice_configuration")) ? rb1.getString("generateInvoice_configuration") : "Invoice Configuration Details";
    String generateInvoice_expired = StringUtils.isNotEmpty(rb1.getString("generateInvoice_expired")) ? rb1.getString("generateInvoice_expired") : "Invoice will expired after* (Days)";
    String generateInvoice_url = StringUtils.isNotEmpty(rb1.getString("generateInvoice_url")) ? rb1.getString("generateInvoice_url") : "Redirect URL*";
    String generateInvoice_due_date = StringUtils.isNotEmpty(rb1.getString("generateInvoice_due_date")) ? rb1.getString("generateInvoice_due_date") : "Due Date";
    String generateInvoice_late_applied = StringUtils.isNotEmpty(rb1.getString("generateInvoice_late_applied")) ? rb1.getString("generateInvoice_late_applied") : "Late Fee Will be Applied After";
    String generateInvoice_generate_invoice = StringUtils.isNotEmpty(rb1.getString("generateInvoice_generate_invoice")) ? rb1.getString("generateInvoice_generate_invoice") : "Generate Invoice";


    String memberid = (String) session.getAttribute("merchantid");
    Functions functions = new Functions();
    Hashtable hash = (Hashtable) request.getAttribute("hiddenvariables");
    String autoSelectTerminal = (String) hash.get("autoselectterminal");
    InvoiceVO invoiceVO = (InvoiceVO) request.getAttribute("invoicevo");
    String orderid = (String) request.getAttribute("orderid");
    String expPeriod = invoiceVO.getExpirationPeriod();
    String duedate = invoiceVO.getDuedate();
    String invoiceduedate = invoiceVO.getInvoiceduedate();
    String initTerminalValue = (String) request.getAttribute("initTerminalValue");
    String amount = "";
    String custname = "";
    String custemail = "";
    String orderdesc = "";
    String address = "";
    String zipcode = "";
    String phonecc = "";
    String phone = "";
    String taxamount = "";
    String grandTotal = "";
    String curr = "";
    String defaultLanguage = "";
    //String currency = "";

    if (functions.isValueNull((String) request.getAttribute("amount")))
        amount = (String) request.getAttribute("amount");
    if (functions.isValueNull((String) request.getAttribute("custname")))
        custname = (String) request.getAttribute("custname");
    if (functions.isValueNull((String) request.getAttribute("custemail")))
        custemail = (String) request.getAttribute("custemail");
    if (functions.isValueNull((String) request.getAttribute("orderdesc")))
        orderdesc = (String) request.getAttribute("orderdesc");
    if (functions.isValueNull((String) request.getAttribute("address")))
        address = (String) request.getAttribute("address");
    if (functions.isValueNull((String) request.getAttribute("zipcode")))
        zipcode = (String) request.getAttribute("zipcode");
    if (functions.isValueNull((String) request.getAttribute("phonecc")))
        phonecc = (String) request.getAttribute("phonecc");
    if (functions.isValueNull((String) request.getAttribute("phone")))
        phone = (String) request.getAttribute("phone");
    if (functions.isValueNull((String) request.getAttribute("taxamount")))
        taxamount = (String) request.getAttribute("taxamount");
    if (functions.isValueNull((String) request.getAttribute("grandtotal")))
        grandTotal = (String) request.getAttribute("grandtotal");

    MerchantTerminalVo merchantTerminalVo = new MerchantTerminalVo();

    List<String> unitList = (List<String>) request.getAttribute("unitList");
    List<DefaultProductList> productList = (List<DefaultProductList>) request.getAttribute("productList");

    String currency = "";
    String terminalid = "";

    if (functions.isValueNull(invoiceVO.getCurrency()))
    {
        currency = invoiceVO.getCurrency();
        //System.out.println("invoiceVO.getCurrency:::::::::::::::::::"+invoiceVO.getCurrency() );
    }
    else
    {
        currency = "Please select currency in generate invoice .";
    }

    terminalid = invoiceVO.getTerminalid();
    //String terminalValue = (String) request.getAttribute("terminalValue");
    String city = "";
    String state = "";
    String country = "";
    String redirecturl = "";

    if (functions.isValueNull((String) request.getAttribute("city")))
        city = (String) request.getAttribute("city");
    else
    {
        if (functions.isValueNull(invoiceVO.getCity()))
        {
            city = invoiceVO.getCity();
        }
    }

    if (functions.isValueNull(invoiceVO.getRedirecturl()))
    {
        redirecturl = invoiceVO.getRedirecturl();
    }
    else
    {
        redirecturl = "";
    }

    if (functions.isValueNull((String) request.getAttribute("state")))
        state = (String) request.getAttribute("state");
    else
    {
        if (functions.isValueNull(invoiceVO.getState()))
        {
            state = invoiceVO.getState();
        }
    }


    if (functions.isValueNull((String) request.getAttribute("countrycode")))
        country = (String) request.getAttribute("countrycode");
    else
    {
        if (functions.isValueNull(invoiceVO.getCountry()))
        {
            country = invoiceVO.getCountry();
        }
    }

    if (functions.isValueNull(invoiceVO.getDefaultLanguage()))
    {
        defaultLanguage = invoiceVO.getDefaultLanguage();
    }
    else
    {
        defaultLanguage=getDefaultLanguage(memberid);
    }
%>

<html lang="en">
<head>
<%--
    <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
--%>
    <title><%=company%> Merchant Invoice> Generate Invoice</title>
    <script>

        $(document).ready(function ()
        {

            var w = $(window).width();

            //alert(w);

            if (w > 990)
            {
                //alert("It's greater than 990px");
                $("body").removeClass("smallscreen").addClass("widescreen");
                $("#wrapper").removeClass("enlarged");
            }
            else
            {
                //alert("It's less than 990px");
                $("body").removeClass("widescreen").addClass("smallscreen");
                $("#wrapper").addClass("enlarged");
                $(".left ul").removeAttr("style");
            }
        });

    </script>
    <script language="javascript">

        function myjunk()
        {
            var hat = this.document.myformname.country.selectedIndex
            var hatto = this.document.myformname.country.options[hat].value
            if (hatto == 'US|001')
            {
                $("#us_states_id").attr("disabled", false);
                document.getElementById("b_state").readOnly = true;
            }
            else
            {
                $("#us_states_id").attr("disabled", true);
                this.document.myformname.b_state.value = "";
            }
            if (hatto != 'Select one')
            {

                this.document.myformname.countrycode.value = hatto.split("|")[0];
                this.document.myformname.phonecc.value = hatto.split("|")[1];
                this.document.myformname.country.options[0].selected = false

            }


        }

        function usstate()
        {
            var hat = this.document.myformname.us_states.selectedIndex
            var hatto = this.document.myformname.us_states.options[hat].value

            if (hatto != 'Select one')
            {

                this.document.myformname.b_state.value = hatto;
                this.document.myformname.us_states.options[0].selected = false

            }
        }


    </script>
    <script onload="">

    </script>

    <script language="JavaScript">

        $(function ()
        {
            var selectedVal = $('#cc').val() + "|" + $('#telcc').val();
            var countrycode = $('#cc').val();
            var phonecc = $('#telcc').val();

            if (selectedVal !== 'null')
            {
                $("#country").val(selectedVal);
            }
            if (countrycode !== 'null')
            {
                $("#countrycode").val(countrycode);
            }
            if (phonecc !== 'null')
            {
                $("#phonecc").val(phonecc);
            }

        });
    </script>

    <script language="JavaScript">

        $("#defaultProdList").change(function ()
        {
            $("#textboxID").toggle();
        });

    </script>

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script src="/merchant/javascript/order-details.js"></script>
    <%--<script src="/merchant/javascript/loadDefaultProductList.js"></script>--%>
</head>

<body class="pace-done widescreen fixed-left-void">

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">
            <form action="InvoiceConfirm?ctoken=<%=ctoken%>" name="myformname" method="post">

                <input type="hidden" id="cc" value="<%=invoiceVO.getCountry()%>" name="cc">
                <input type="hidden" id="telcc" value="<%=invoiceVO.getTelCc()%>" name="telcc">

                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=generateInvoice_invoice_details%></strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>

                            <%
                                String action = ESAPI.encoder().encodeForHTML(request.getParameter("action"));
                                String error = (String) request.getAttribute("error");

                                if (error == null)
                                {
                                    error = "";
                                }
                                else
                                {
                                    out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");

                                }
                                if (action != null)
                                {
                            %>

                            <h5 class="bg-infoorange" style="text-align: center;"><i
                                    class="fa fa-exclamation-triangle"></i>&nbsp;&nbsp;
                                <%
                                    if (action.equals("F"))
                                    {
                                        out.println("*Your Login or Password was incorrect.");
                                    }
                                    if (action.equals("L"))
                                    {
                                        out.println("*Your Merchant Account has been Locked. Kindly Contact Support Desk.");
                                    }
                                    if (action.equals("D"))
                                    {
                                        out.println("*Your Merchant Account has been Disabled. Kindly Contact Support Desk.");
                                    }

                                %>
                            </h5>

                            <%
                                }
                            %>
                            <div class="widget-content padding">
                                <div id="horizontal-form">

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=generateInvoice_merchant_id%></label>
                                        <input type="text" name="memberid" class="form-control" size="10"
                                               disabled="true" value=<%=memberid%>>
                                        <input type="hidden" name="memberid" value=<%=memberid%>>
                                    </div>


                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=generateInvoice_order_id%></label>
                                        <input type="text" name="orderid" size="10" class="form-control"
                                               value="<%=orderid%>" disabled>
                                        <input type="hidden" name="orderid" size="10" value="<%=orderid%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=generateInvoice_order_description%></label>
                                        <input type="text" name="orderdesc" size="10" class="form-control"
                                               placeholder="Description about the transaction" value="<%=orderdesc%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=generateInvoice_amount%></label>
                                        <input type="text" class="form-control" name="amount" id="amount" size="10"
                                               value="<%=amount%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=generateInvoice_currency%></label>

                                        <%
                                            if ("Y".equalsIgnoreCase(multiCurrency))
                                            {
                                        %>
                                        <select name="curr" id="mid" class="form-control" <%--style="width:267px;"--%>>
                                            <option value="" selected><%=generateInvoice_invoice_select_currency%></option>
                                            <%
                                                for (String sCurr : currencyCodeHash.keySet())
                                                {
                                                    String isSelected = "";
                                                    if (sCurr.equalsIgnoreCase(curr))
                                                        isSelected = "selected";
                                            %>
                                            <option value="<%=sCurr%>" <%=isSelected%>><%=sCurr%>
                                            </option>
                                            <%
                                                }
                                            %>
                                        </select>
                                        <%
                                        }
                                        else
                                        {
                                        %>
                                        <select name="curr" id="mid" class="form-control" <%--style="width:267px;"--%>>
                                            <option value="" selected><%=generateInvoice_payment_option%></option>
                                            <%
                                                HashMap currencyMap = new HashMap();
                                                Set currencySet = new HashSet();
                                                Set terminalSet = new HashSet();
                                                currencyMap = (HashMap) session.getAttribute("terminallist");
                                                String currency1 = "";
                                                for (Object key : currencyMap.keySet())
                                                {
                                                    merchantTerminalVo = (MerchantTerminalVo) currencyMap.get(key);
                                                    currencySet.add(merchantTerminalVo.getCurrency());
                                                    terminalSet.add(merchantTerminalVo.getTerminalId());
                                                }


                                                Iterator i = currencySet.iterator();
                                                while (i.hasNext())
                                                {
                                                    currency1 = (String) i.next();

                                                    if (currency1.equalsIgnoreCase("ALL"))
                                                    {
                                                        continue;
                                                    }

                                                    String isSelected = "";
                                                    if (currency1.equalsIgnoreCase(curr))
                                                        isSelected = "selected";

                                            %>
                                            <option value="<%=currency1%>" <%=isSelected%>><%=currency1%>
                                            </option>
                                            <%
                                                }
                                            %>
                                        </select>
                                        <%
                                            }
                                        %>
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=generateInvoice_select_terminalid%></label>
                                        <select name="terminal" id="tid"
                                                class="form-control" <%--style="width:267px;"--%>>
                                            <option value="0" selected><%=generateInvoice_select_terminalid1%></option>
                                            <%
                                                HashMap terminals = (HashMap) session.getAttribute("terminallist");
                                                Map.Entry pair = null;
                                                String cardtypeName = "";
                                                String paymodeName = "";
                                                String terminalValue = "";
                                                String Currency = "";


                                                if (terminals != null)
                                                {
                                                    Iterator it = terminals.entrySet().iterator();
                                                    while (it.hasNext())
                                                    {
                                                        merchantTerminalVo = new MerchantTerminalVo();
                                                        pair = (Map.Entry) it.next();
                                                        merchantTerminalVo = (MerchantTerminalVo) pair.getValue();
                                                        cardtypeName = merchantTerminalVo.getCardTypeName();
                                                        paymodeName = merchantTerminalVo.getPaymodeName();
                                                        Currency= merchantTerminalVo.getCurrency();
                                                        terminalid = merchantTerminalVo.getTerminalId();
                                                        String isSelected = "";
                                                        terminalValue = terminalid + "-" + cardtypeName + "-" + paymodeName;
                                                        if (terminalValue.equalsIgnoreCase(initTerminalValue))
                                                        {

                                                            isSelected = "selected";
                                                        }
                                                        if ("Y".equalsIgnoreCase(merchantTerminalVo.getIsActive()))
                                                        {
                                                            if ("Y".equalsIgnoreCase(multiCurrency) && "ALL".equalsIgnoreCase(Currency))
                                                            {
                                            %>

                                            <option data-mid="<%=Currency%>"
                                                    value="<%=terminalValue%>" <%=isSelected%>><%=terminalValue%>
                                            </option>
                                            <%
                                            }
                                            else if (!"ALL".equalsIgnoreCase(Currency))
                                            {
                                            %>
                                            <%--<option value="<%=terminalValue%>"><%=terminalValue%>
                                            </option>--%>

                                            <option data-mid="<%=Currency%>"
                                                    value="<%=terminalValue%>" <%=isSelected%>><%=terminalValue%>
                                            </option>
                                            <%
                                                        }
                                                    }
                                                }
                                            %>
                                        </select>
                                       <%-- <%
                                             if (functions.isValueNull(terminalValue))
                                             {
                                         %>

                                         <div class="form-group col-md-4 has-feedback">
                                             <label  >Select Terminal Type</label>
                                             <input type="text" class="form-control" name="terminal" id="terminal" value="<%=terminalValue%>" disabled>
                                             <input type="hidden" name="terminal" value="<%=terminalValue%>">&ndash;%&gt;
                                        <select name="terminal" id="sub_groups" class="form-control" &lt;%&ndash;style="width:267px;"&ndash;%&gt;>
                                            <option value="0" selected>--Select TerminalID--</option>
                                            <%
                                                TreeMap terminals= (TreeMap) request.getAttribute("terminallist");

                                                    for (Object terminalid  : terminals.keySet())
                                                    {
                                                        String terminalValue = (String) terminals.get(terminalid);
                                            %>
                                            <option value="<%=terminalValue%>"><%=terminalValue%></option>

                                            <%

                                                }
                                            %>
                                        </select>
                                    </div>--%>
                                    <%
                                        }
                                    %>

                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <input type="hidden" value="<%=productList%>" name="productList" id="productList">
                <input type="hidden" value="<%=unitList%>" name="unitList" id="unitList">

                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=generateInvoice_product_details%></strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>
                            <div class="pull-left">
                                <div class="btn-group">
                                    <input type="button" class="btn btn-default" id="addrow" value="Add Product"
                                           style="margin-left: 17px;"/>
                                </div>
                            </div>

                            <div class="widget-content padding">
                                <div id="horizontal-form1">
                                    <%
                                        String flag = invoiceVO.getLoadDefaultProductList();
                                        int dProdCounter = 0;
                                        if (productList != null)
                                        {
                                            if (productList.size() > 0 && (flag.equalsIgnoreCase("Y") || functions.isValueNull(error)))
                                            {
                                    %>
                                    <div class="form1" id="topheader">

                                        <div class="form-group col-md-2 has-feedback">
                                            <label><%=generateInvoice_product_description%></label>
                                        </div>
                                        <div class="form-group col-md-2 has-feedback">
                                            <label><%=generateInvoice_unit%></label>
                                        </div>
                                        <div class="form-group col-md-2 has-feedback">
                                            <label><%=generateInvoice_product_amount%></label>
                                        </div>
                                        <div class="form-group col-md-2 has-feedback">
                                            <label><%=generateInvoice_quantity%></label>
                                        </div>
                                        <div class="form-group col-md-1 has-feedback">
                                            <label><%=generateInvoice_tax%></label>
                                        </div>
                                        <div class="form-group col-md-2 has-feedback">
                                            <label><%=generateInvoice_total%></label>
                                        </div>
                                    </div>
                                    <%
                                        int prodCounter = 1;
                                        dProdCounter = productList.size();
                                        for (DefaultProductList defaultProductList : productList)
                                        {
                                            String productDescription = defaultProductList.getProductDescription() == null ? "" : defaultProductList.getProductDescription();
                                            String productAmount = defaultProductList.getProductAmount() == null ? "" : defaultProductList.getProductAmount();
                                            String quantity = defaultProductList.getQuantity() == null ? "" : defaultProductList.getQuantity();
                                            String productTotal = defaultProductList.getProductTotal() == null ? "" : defaultProductList.getProductTotal();
                                    %>
                                    <div class="form1">
                                        <div class="form-group col-md-2 has-feedback">
                                            <input type="text" size="10" placeholder="Order Description"
                                                   name="product<%=prodCounter%>" class="form-control"
                                                   value="<%=productDescription%>">
                                        </div>

                                        <div class="form-group col-md-2 has-feedback">
                                            <select name="productunit<%=prodCounter%>" class="form-control">
                                                <option value=""><%=generateInvoice_select_unit%></option>
                                                <%
                                                    for (String defaultUnit : unitList)
                                                    {
                                                        String isSelected = "";

                                                        if (defaultProductList.getUnit().equalsIgnoreCase(defaultUnit))
                                                        {
                                                            isSelected = "selected";
                                                        }
                                                %>
                                                <option value="<%=defaultUnit%>" <%=isSelected%>><%=defaultUnit%>
                                                </option>
                                                <%
                                                    }
                                                %>
                                            </select>
                                        </div>

                                        <div class="form-group col-md-2 has-feedback">
                                            <input type="text" size="10" placeholder="Product Amount"
                                                   name="price<%=prodCounter%>" onkeyup="checkDec(this);"
                                                   class="form-control for-decimal" value="<%=productAmount%>">
                                        </div>

                                        <div class="form-group col-md-2 has-feedback">
                                            <input type="text" size="10" placeholder="Quantity"
                                                   name="qty<%=prodCounter%>" onkeypress="return isNumber(event)"
                                                   class="form-control" value="<%=quantity%>">
                                        </div>

                                        <div class="form-group col-md-1 has-feedback">
                                            <input type="text" size="10" placeholder="Tax" name="tax<%=prodCounter%>"
                                                   onkeyup="checkDec(this);" class="form-control for-decimal"
                                                   value="<%=defaultProductList.getTax()%>">
                                        </div>

                                        <div class="form-group col-md-2 has-feedback">
                                            <input type="text" size="10" placeholder="Total"
                                                   name="linetotal<%=prodCounter%>" class="form-control"
                                                   value="<%=productTotal%>" readonly="readonly">
                                        </div>

                                        <div class="form-group col-md-1 has-feedback">
                                            <input type="button" size="10" name="delete" class="btn btn-default"
                                                   value="Delete">
                                        </div>

                                    </div>
                                    <%
                                                    prodCounter++;
                                                }
                                            }
                                        }

                                    %>
                                </div>
                            </div>

                            <div class="col-md-6" style="float: right" id="horizontal-form2">
                                <input type="hidden" value="<%=invoiceVO.getGST()%>" name="gstcb" id="gstcb">

                                <div class="invoice-address" style="float: right;width: 100%;margin-bottom: 10px;">
                                    <%
                                        String taxPer = "0.00";
                                        if (functions.isValueNull(invoiceVO.getGST()))
                                        {
                                            taxPer = invoiceVO.getGST();
                                        }
                                        if (productList.size() > 0 && (flag.equalsIgnoreCase("Y") || functions.isValueNull(error)))
                                        {
                                    %>

                                    <table style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;border: 0px">
                                        <tbody>
                                        <tr>
                                            <label class="thcheckboxnew" id="dProList">
                                                <input type="checkbox" id="thcheckboxnew" name="addtax">&nbsp;&nbsp;&nbsp;<%=generateInvoice_apply_gst%><%=taxPer%>
                                                % </label>
                                        </tr>
                                        <tr>

                                            <td width="200"><%=generateInvoice_tax_amount%></td>
                                            <td class="text-right"><input type="text"
                                                                          style="margin-left: -42px;width: 84%;"
                                                                          class="form-control" id="tax" class="tax"
                                                                          name="taxamount" readonly="readonly" size="10"
                                                                          placeholder="0.00" value="<%=taxamount%>">
                                            </td>

                                        <tr>
                                            <td>&nbsp;</td>
                                        </tr>
                                        <tr>
                                            <td><%=generateInvoice_grand_total%></td>
                                            <td class="text-right"><input type="text" class="form-control"
                                                                          style="margin-left: -42px;width: 84%;"
                                                                          id="grandtotal" class="grandtotal"
                                                                          name="grandtotal" readonly="readonly"
                                                                          size="10" placeholder="Grand Total"
                                                                          value="<%=grandTotal%>">
                                            </td>
                                        </tr>
                                        </tbody>
                                    </table>
                                    <%
                                        }
                                    %>
                                </div>
                            </div>
                            <%

                            %>
                            <input type="hidden" name="productCounter" id="productCounter" value="<%=dProdCounter%>">

                        </div>
                    </div>
                </div>
                <input type="hidden" name="productCounter" id="productCounter" value="">

                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=generateInvoice_customers%></strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>

                            <div class="widget-content padding">
                                <div id="horizontal-form">

                                    <div class="form-group col-md-6 has-feedback">
                                        <label><%=generateInvoice_customer_name%></label>
                                        <input type="text" class="form-control" name="custname" size="20"
                                               placeholder="e.g John Doe" value="<%=custname%>">
                                    </div>

                                    <div class="form-group col-md-6 has-feedback">
                                        <label><%=generateInvoice_emailid%></label>
                                        <input type="text" class="form-control" name="custemail"
                                               placeholder="xyz@gmail.com" value="<%=custemail%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=generateInvoice_address%></label>
                                        <input type="text" class="form-control" name="address" size="40"
                                               value="<%=address%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=generateInvoice_city%></label>
                                        <input type="text" class="form-control" name="city" size="20" value="<%=city%>">
                                    </div>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=generateInvoice_zip_code%></label>
                                        <input type="text" class="form-control" name="zipcode" size="10"
                                               value="<%=zipcode%>">
                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label><%=generateInvoice_country%></label>
                                        <select name="country" class="form-control" id="country" onchange="myjunk();">
                                            <option value="--|--"><%=generateInvoice_select_a_country%></option>
                                            <option value="AF|093">Afghanistan</option>
                                            <option value="AX|358">Aland Islands</option>
                                            <option value="AL|355">Albania</option>
                                            <option value="DZ|231">Algeria</option>
                                            <option value="AS|684">American Samoa</option>
                                            <option value="AD|376">Andorra</option>
                                            <option value="AO|244">Angola</option>
                                            <option value="AI|001">Anguilla</option>
                                            <option value="AQ|000">Antarctica</option>
                                            <option value="AG|001">Antigua and Barbuda</option>
                                            <option value="AR|054">Argentina</option>
                                            <option value="AM|374">Armenia</option>
                                            <option value="AW|297">Aruba</option>
                                            <option value="AU|061">Australia</option>
                                            <option value="AT|043">Austria</option>
                                            <option value="AZ|994">Azerbaijan</option>
                                            <option value="BS|001">Bahamas</option>
                                            <option value="BH|973">Bahrain</option>
                                            <option value="BD|880">Bangladesh</option>
                                            <option value="BB|001">Barbados</option>
                                            <option value="BY|375">Belarus</option>
                                            <option value="BE|032">Belgium</option>
                                            <option value="BZ|501">Belize</option>
                                            <option value="BJ|229">Benin</option>
                                            <option value="BM|001">Bermuda</option>
                                            <option value="BT|975">Bhutan</option>
                                            <option value="BO|591">Bolivia</option>
                                            <option value="BA|387">Bosnia and Herzegovina</option>
                                            <option value="BW|267">Botswana</option>
                                            <option value="BV|000">Bouvet Island</option>
                                            <option value="BR|055">Brazil</option>
                                            <option value="IO|246">British Indian Ocean Territory</option>
                                            <option value="VG|001">British Virgin Islands</option>
                                            <option value="BN|673">Brunei</option>
                                            <option value="BG|359">Bulgaria</option>
                                            <option value="BF|226">Burkina Faso</option>
                                            <option value="BI|257">Burundi</option>
                                            <option value="KH|855">Cambodia</option>
                                            <option value="CM|237">Cameroon</option>
                                            <option value="CA|001">Canada</option>
                                            <option value="CV|238">Cape Verde</option>
                                            <option value="KY|001">Cayman Islands</option>
                                            <option value="CF|236">Central African Republic</option>
                                            <option value="TD|235">Chad</option>
                                            <option value="CL|056">Chile</option>
                                            <option value="CN|086">China</option>
                                            <option value="CX|061">Christmas Island</option>
                                            <option value="CC|061">Cocos (Keeling) Islands</option>
                                            <option value="CO|057">Colombia</option>
                                            <option value="KM|269">Comoros</option>
                                            <option value="CK|682">Cook Islands</option>
                                            <option value="CR|506">Costa Rica</option>
                                            <option value="CI|225">Cote d'Ivoire</option>
                                            <option value="HR|385">Croatia</option>
                                            <option value="CU|053">Cuba</option>
                                            <option value="CY|357">Cyprus</option>
                                            <option value="CZ|420">Czech Republic</option>
                                            <option value="CD|243">Democratic Republic of the Congo</option>
                                            <option value="DK|045">Denmark</option>
                                            <option value="DJ|253">Djibouti</option>
                                            <option value="DM|001">Dominica</option>
                                            <option value="DO|001">Dominican Republic</option>
                                            <option value="EC|593">Ecuador</option>
                                            <option value="EG|020">Egypt</option>
                                            <option value="SV|503">El Salvador</option>
                                            <option value="GQ|240">Equatorial Guinea</option>
                                            <option value="ER|291">Eritrea</option>
                                            <option value="EE|372">Estonia</option>
                                            <option value="ET|251">Ethiopia</option>
                                            <option value="FK|500">Falkland Islands</option>
                                            <option value="FO|298">Faroe Islands</option>
                                            <option value="FJ|679">Fiji</option>
                                            <option value="FI|358">Finland</option>
                                            <option value="FR|033">France</option>
                                            <option value="GF|594">French Guiana</option>
                                            <option value="PF|689">French Polynesia</option>
                                            <option value="TF|000">French Southern and Antarctic Lands</option>
                                            <option value="GA|241">Gabon</option>
                                            <option value="GM|220">Gambia</option>
                                            <option value="GE|995">Georgia</option>
                                            <option value="DE|049">Germany</option>
                                            <option value="GH|233">Ghana</option>
                                            <option value="GI|350">Gibraltar</option>
                                            <option value="GR|030">Greece</option>
                                            <option value="GL|299">Greenland</option>
                                            <option value="GD|001">Grenada</option>
                                            <option value="GP|590">Guadeloupe</option>
                                            <option value="GU|001">Guam</option>
                                            <option value="GT|502">Guatemala</option>
                                            <option value="GG|000">Guernsey</option>
                                            <option value="GN|224">Guinea</option>
                                            <option value="GW|245">Guinea-Bissau</option>
                                            <option value="GY|592">Guyana</option>
                                            <option value="HT|509">Haiti</option>
                                            <option value="HM|672">Heard Island & McDonald Islands</option>
                                            <option value="HN|504">Honduras</option>
                                            <option value="HK|852">Hong Kong</option>
                                            <option value="HU|036">Hungary</option>
                                            <option value="IS|354">Iceland</option>
                                            <option value="IN|091">India</option>
                                            <option value="ID|062">Indonesia</option>
                                            <option value="IR|098">Iran</option>
                                            <option value="IQ|964">Iraq</option>
                                            <option value="IE|353">Ireland</option>
                                            <option value="IL|972">Israel</option>
                                            <option value="IT|039">Italy</option>
                                            <option value="JM|001">Jamaica</option>
                                            <option value="JP|081">Japan</option>
                                            <option value="JE|044">Jersey</option>
                                            <option value="JO|962">Jordan</option>
                                            <option value="KZ|007">Kazakhstan</option>
                                            <option value="KE|254">Kenya</option>
                                            <option value="KI|686">Kiribati</option>
                                            <option value="KW|965">Kuwait</option>
                                            <option value="KG|996">Kyrgyzstan</option>
                                            <option value="LA|856">Laos</option>
                                            <option value="LV|371">Latvia</option>
                                            <option value="LB|961">Lebanon</option>
                                            <option value="LS|266">Lesotho</option>
                                            <option value="LR|231">Liberia</option>
                                            <option value="LY|218">Libya</option>
                                            <option value="LI|423">Liechtenstein</option>
                                            <option value="LT|370">Lithuania</option>
                                            <option value="LU|352">Luxembourg</option>
                                            <option value="MO|853">Macau, China</option>
                                            <option value="MK|389">Macedonia</option>
                                            <option value="MG|261">Madagascar</option>
                                            <option value="MW|265">Malawi</option>
                                            <option value="MY|060">Malaysia</option>
                                            <option value="MV|960">Maldives</option>
                                            <option value="ML|223">Mali</option>
                                            <option value="MT|356">Malta</option>
                                            <option value="MH|692">Marshall Islands</option>
                                            <option value="MQ|596">Martinique</option>
                                            <option value="MR|222">Mauritania</option>
                                            <option value="MU|230">Mauritius</option>
                                            <option value="YT|269">Mayotte</option>
                                            <option value="MX|052">Mexico</option>
                                            <option value="FM|691">Micronesia, Federated States of</option>
                                            <option value="MD|373">Moldova</option>
                                            <option value="MC|377">Monaco</option>
                                            <option value="MN|976">Mongolia</option>
                                            <option value="ME|382">Montenegro</option>
                                            <option value="MS|001">Montserrat</option>
                                            <option value="MA|212">Morocco</option>
                                            <option value="MZ|258">Mozambique</option>
                                            <option value="MM|095">Myanmar</option>
                                            <option value="NA|264">Namibia</option>
                                            <option value="NR|674">Nauru</option>
                                            <option value="NP|977">Nepal</option>
                                            <option value="AN|599">Netherlands Antilles</option>
                                            <option value="NL|031">Netherlands</option>
                                            <option value="NC|687">New Caledonia</option>
                                            <option value="NZ|064">New Zealand</option>
                                            <option value="NI|505">Nicaragua</option>
                                            <option value="NE|227">Niger</option>
                                            <option value="NG|234">Nigeria</option>
                                            <option value="NU|683">Niue</option>
                                            <option value="NF|672">Norfolk Island</option>
                                            <option value="KP|850">North Korea</option>
                                            <option value="MP|001">Northern Mariana Islands</option>
                                            <option value="NO|047">Norway</option>
                                            <option value="OM|968">Oman</option>
                                            <option value="PK|092">Pakistan</option>
                                            <option value="PW|680">Palau</option>
                                            <option value="PS|970">Palestinian Authority</option>
                                            <option value="PA|507">Panama</option>
                                            <option value="PG|675">Papua New Guinea</option>
                                            <option value="PY|595">Paraguay</option>
                                            <option value="PE|051">Peru</option>
                                            <option value="PH|063">Philippines</option>
                                            <option value="PN|064">Pitcairn Islands</option>
                                            <option value="PL|048">Poland</option>
                                            <option value="PT|351">Portugal</option>
                                            <option value="PR|001">Puerto Rico</option>
                                            <option value="QA|974">Qatar</option>
                                            <option value="CG|242">Republic of the Congo</option>
                                            <option value="RE|262">Reunion</option>
                                            <option value="RO|040">Romania</option>
                                            <option value="RU|007">Russia</option>
                                            <option value="RW|250">Rwanda</option>
                                            <option value="BL|590">Saint Barthelemy</option>
                                            <option value="SH|290">Saint Helena, Ascension & Tristan daCunha</option>
                                            <option value="KN|001">Saint Kitts and Nevis</option>
                                            <option value="LC|001">Saint Lucia</option>
                                            <option value="MF|590">Saint Martin</option>
                                            <option value="PM|508">Saint Pierre and Miquelon</option>
                                            <option value="VC|001">Saint Vincent and Grenadines</option>
                                            <option value="WS|685">Samoa</option>
                                            <option value="SM|378">San Marino</option>
                                            <option value="ST|239">Sao Tome and Principe</option>
                                            <option value="SA|966">Saudi Arabia</option>
                                            <option value="SN|221">Senegal</option>
                                            <option value="RS|381">Serbia</option>
                                            <option value="SC|248">Seychelles</option>
                                            <option value="SL|232">Sierra Leone</option>
                                            <option value="SG|065">Singapore</option>
                                            <option value="SK|421">Slovakia</option>
                                            <option value="SI|386">Slovenia</option>
                                            <option value="SB|677">Solomon Islands</option>
                                            <option value="SO|252">Somalia</option>
                                            <option value="ZA|027">South Africa</option>
                                            <option value="GS|000">South Georgia & South Sandwich Islands</option>
                                            <option value="KR|082">South Korea</option>
                                            <option value="ES|034">Spain</option>
                                            <option value="LK|094">Sri Lanka</option>
                                            <option value="SD|249">Sudan</option>
                                            <option value="SR|597">Suriname</option>
                                            <option value="SJ|047">Svalbard and Jan Mayen</option>
                                            <option value="SZ|268">Swaziland</option>
                                            <option value="SE|046">Sweden</option>
                                            <option value="CH|041">Switzerland</option>
                                            <option value="SY|963">Syria</option>
                                            <option value="TW|886">Taiwan</option>
                                            <option value="TJ|992">Tajikistan</option>
                                            <option value="TZ|255">Tanzania</option>
                                            <option value="TH|066">Thailand</option>
                                            <option value="TL|670">Timor-Leste</option>
                                            <option value="TG|228">Togo</option>
                                            <option value="TK|690">Tokelau</option>
                                            <option value="TO|676">Tonga</option>
                                            <option value="TT|001">Trinidad and Tobago</option>
                                            <option value="TN|216">Tunisia</option>
                                            <option value="TR|090">Turkey</option>
                                            <option value="TM|993">Turkmenistan</option>
                                            <option value="TC|001">Turks and Caicos Islands</option>
                                            <option value="TV|688">Tuvalu</option>
                                            <option value="UG|256">Uganda</option>
                                            <option value="UA|380">Ukraine</option>
                                            <option value="AE|971">United Arab Emirates</option>
                                            <option value="GB|044">United Kingdom</option>
                                            <option value="US|001">United States</option>
                                            <option value="VI|001">United States Virgin Islands</option>
                                            <option value="UY|598">Uruguay</option>
                                            <option value="UZ|998">Uzbekistan</option>
                                            <option value="VU|678">Vanuatu</option>
                                            <option value="VA|379">Vatican City</option>
                                            <option value="VE|058">Venezuela</option>
                                            <option value="VN|084">Vietnam</option>
                                            <option value="WF|681">Wallis and Futuna</option>
                                            <option value="EH|212">Western Sahara</option>
                                            <option value="YE|967">Yemen</option>
                                            <option value="ZM|260">Zambia</option>
                                            <option value="ZW|263">Zimbabwe</option>
                                        </select>

                                    </div>

                                    <div class="form-group col-md-1 has-feedback">
                                        <label><%=generateInvoice_code%></label>
                                        <input type="text" class="form-control" name="countrycode" id="countrycode"
                                               placeholder="CC" value="<%=country%>" <%--style="width: 50px"--%>
                                               size="10" readonly="readonly">
                                    </div>

                                    <div class="form-group col-md-3 has-feedback">
                                        <label><%=generateInvoice_state%></label>
                                        <select name="us_states" onchange="usstate();" id="us_states_id" disabled="true"
                                                class="form-control">

                                            <option value="Select one">Select a State for US</option>
                                            <option value="AL">ALABAMA</option>
                                            <option value="AK">ALASKA</option>
                                            <option value="AS">AMERICAN SAMOA</option>
                                            <option value="AZ">ARIZONA</option>
                                            <option value="AR">ARKANSAS</option>
                                            <option value="CA">CALIFORNIA</option>
                                            <option value="CO">COLORADO</option>
                                            <option value="CT">CONNECTICUT</option>
                                            <option value="DE">DELAWARE</option>
                                            <option value="DC">DISTRICT OF COLUMBIA</option>
                                            <option value="FM">FEDERATED STATES OF MICRONESIA</option>
                                            <option value="FL">FLORIDA</option>
                                            <option value="GA">GEORGIA</option>
                                            <option value="GU">GUAM GU</option>
                                            <option value="HI">HAWAII</option>
                                            <option value="ID">IDAHO</option>
                                            <option value="IL">ILLINOIS</option>
                                            <option value="IN">INDIANA</option>
                                            <option value="IA">IOWA</option>
                                            <option value="KS">KANSAS</option>
                                            <option value="KY">KENTUCKY</option>
                                            <option value="LA">LOUISIANA</option>
                                            <option value="ME">MAINE</option>
                                            <option value="MH">MARSHALL ISLANDS</option>
                                            <option value="MD">MARYLAND</option>
                                            <option value="MA">MASSACHUSETTS</option>
                                            <option value="MI">MICHIGAN</option>
                                            <option value="MN">MINNESOTA</option>
                                            <option value="MS">MISSISSIPPI</option>
                                            <option value="MO">MISSOURI</option>
                                            <option value="MT">MONTANA</option>
                                            <option value="NE">NEBRASKA</option>
                                            <option value="NV">NEVADA</option>
                                            <option value="NH">NEW HAMPSHIRE</option>
                                            <option value="NJ">NEW JERSEY</option>
                                            <option value="NM">NEW MEXICO</option>
                                            <option value="NY">NEW MEXICO</option>
                                            <option value="NC">NORTH CAROLINA</option>
                                            <option value="ND">NORTH DAKOTA</option>
                                            <option value="MP">NORTHERN MARIANA ISLANDS</option>
                                            <option value="OH">OHIO</option>
                                            <option value="OK">OKLAHOMA</option>
                                            <option value="OR">OREGON</option>
                                            <option value="PW">PALAU</option>
                                            <option value="PA">PENNSYLVANIA</option>
                                            <option value="PR">PUERTO RICO</option>
                                            <option value="RI">CRHODE ISLAND</option>
                                            <option value="SC">SOUTH CAROLINA</option>
                                            <option value="SD">SOUTH DAKOTA</option>
                                            <option value="TN">TENNESSEE</option>
                                            <option value="TX">TEXAS</option>
                                            <option value="UT">UTAH</option>
                                            <option value="VT">VERMONT</option>
                                            <option value="VI">VIRGIN ISLANDS</option>
                                            <option value="VA">VIRGINIA</option>
                                            <option value="WA">WASHINGTON</option>
                                            <option value="WV">WEST VIRGINIA</option>
                                            <option value="WI">WISCONSIN</option>
                                            <option value="WY">WYOMING</option>

                                        </select>
                                    </div>
                                    <div class="form-group col-md-1 has-feedback">
                                        <label><%=generateInvoice_state_code%></label>
                                        <input name="state" class="form-control" type="text"
                                               value="<%=state%>" <%--style="width:50px"--%> id="b_state" size="10">
                                    </div>


                                    <div class="form-group col-md-2 has-feedback">
                                        <label><%=generateInvoice_country_code%></label>
                                        <input type="text" class="form-control" name="phonecc" id="phonecc" size="5"
                                               readonly="readonly" placeholder="cc" value="<%=phonecc%>"></div>

                                    <div class="form-group col-md-2 has-feedback">
                                        <label><%=generateInvoice_phone_number%>&nbsp;</label>
                                        <input type="text" class="form-control" name="phone" size="20"
                                               placeholder="541-754-3010" value="<%=phone%>">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>


                <div class="row">
                    <div class="col-sm-12 portlets ui-sortable">
                        <div class="widget">

                            <div class="widget-header transparent">
                                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=generateInvoice_configuration%>
                                    </strong></h2>

                                <div class="additional-btn">
                                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                                </div>
                            </div>

                            <div class="widget-content padding">
                                <div id="horizontal-form">

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=generateInvoice_expired%></label>
                                        <input type="text" class="form-control" name="expire" value="<%=expPeriod%>"
                                               disabled>
                                        <input type="hidden" value="<%=expPeriod%>" name="expire">
                                    </div>
                                    <input type="hidden" name="frequency" value="days">

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=generateInvoice_url%></label>
                                        <input type="text" class="form-control" value="<%=redirecturl%>"
                                               name="redirecturl" >
                                        <input type="hidden" value="<%=redirecturl%>" name="redirecturl">
                                    </div>

                                    <%
                                        if (invoiceVO.getIslatefee().equals("Y"))

                                        {

                                    %>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label><%=generateInvoice_due_date%></label>
                                        <input type="text" class="form-control" name="duedate"
                                               value="<%=invoiceduedate%>" disabled>
                                        <input type="hidden" value="<%=invoiceduedate%>" name="duedate">
                                    </div>
                                    <%
                                        }
                                    %>

                                    <div class="form-group col-md-4 has-feedback">
                                        <label>Language For Invoice</label>
                                        <select class="form-control" style="background: #ffffff" name='defaultLanguage' onchange="ChangeFunction(this.value,'Default Language')">
                                            <%
                                                out.println(Functions.combovalLang(ESAPI.encoder().encodeForHTMLAttribute(defaultLanguage))); %>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <%
                    if (invoiceVO.getIslatefee().equals("Y"))

                    {
                %>

                <div class="bg-info" style="text-align:center;"><i class="fa fa-info-circle"></i>&nbsp;&nbsp
                    <%=generateInvoice_late_applied%>&nbsp;<%=invoiceVO.getInvoiceduedate()%>
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
                                <button type="submit" name="Submit" class="btn btn-default">
                                    <i class="fa fa-save"></i>
                                    &nbsp;&nbsp;<%=generateInvoice_generate_invoice%>
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
<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
<script type="text/javascript" language="JavaScript" src="/merchant/javascript/currency_terminal.js"></script>


</body>
</html>
<%!
    public String getDefaultLanguage(String memberid)
    {
        String defaultLang="";
        Connection con = null;
        java.sql.PreparedStatement pstmt=null;
        ResultSet rs=null;
        try
        {
            con = Database.getRDBConnection();
            String qry = "SELECT defaultLanguage FROM members WHERE memberid=?";
            pstmt = con.prepareStatement(qry);
            pstmt.setString(1, memberid);
            rs = pstmt.executeQuery();
            while (rs.next())
            {
                defaultLang = rs.getString("defaultLanguage");
            }
        }
        catch (Exception e)
        {
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }
        return defaultLang;
    }
%>