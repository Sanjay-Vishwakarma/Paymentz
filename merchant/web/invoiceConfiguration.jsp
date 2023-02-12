<%@ page language="java" import="com.directi.pg.MerchantTerminalVo" %>
<%@ page import = "com.invoice.dao.InvoiceEntry" %>
<%@ page import="com.invoice.vo.DefaultProductList" %>
<%@ page import="com.invoice.vo.InvoiceVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="com.directi.pg.*" %>

<%@ include file="Top.jsp" %>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
  session.setAttribute("submit","Invoice Configuration");
  //String multiCurrency = (String)session.getAttribute("multiCurrencySupport");
  String disabled="";

 /* if("submerchant".equalsIgnoreCase((String)session.getAttribute("role")))
    disabled="disabled";*/

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
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);


  String invoiceConfiguration_invoice_configuration = rb1.getString("invoiceConfiguration_invoice_configuration");
  String invoiceConfiguration_redirecturl1 = rb1.getString("invoiceConfiguration_redirecturl1");
  String invoiceConfiguration_initial = rb1.getString("invoiceConfiguration_initial");
  String invoiceConfiguration_expiration_period = rb1.getString("invoiceConfiguration_expiration_period");
  String invoiceConfiguration_gst = rb1.getString("invoiceConfiguration_gst");
  String invoiceConfiguration_add_unit = rb1.getString("invoiceConfiguration_add_unit");
  String invoiceConfiguration_product_units = rb1.getString("invoiceConfiguration_product_units");
  String invoiceConfiguration_default_product = rb1.getString("invoiceConfiguration_default_product");
  String invoiceConfiguration_load_products = rb1.getString("invoiceConfiguration_load_products");
  String invoiceConfiguration_product_description = rb1.getString("invoiceConfiguration_product_description");
  String invoiceConfiguration_unit = rb1.getString("invoiceConfiguration_unit");
  String invoiceConfiguration_tax = rb1.getString("invoiceConfiguration_tax");
  String invoiceConfiguration_select_unit = rb1.getString("invoiceConfiguration_select_unit");
  String invoiceConfiguration_default_customer = rb1.getString("invoiceConfiguration_default_customer");
  String invoiceConfiguration_city = rb1.getString("invoiceConfiguration_city");
  String invoiceConfiguration_country = rb1.getString("invoiceConfiguration_country");
  String invoiceConfiguration_select_a_contry = rb1.getString("invoiceConfiguration_select_a_contry");
  String invoiceConfiguration_countrycode = rb1.getString("invoiceConfiguration_countrycode");
  String invoiceConfiguration_state = rb1.getString("invoiceConfiguration_state");
  String invoiceConfiguration_statecode = rb1.getString("invoiceConfiguration_statecode");
  String invoiceConfiguration_select_a_state = rb1.getString("invoiceConfiguration_select_a_state");
  String invoiceConfiguration_phonecc = rb1.getString("invoiceConfiguration_phonecc");
  String invoiceConfiguration_sms_email = rb1.getString("invoiceConfiguration_sms_email");
  String invoiceConfiguration_isSMS = rb1.getString("invoiceConfiguration_isSMS");
  String invoiceConfiguration_isEmail = rb1.getString("invoiceConfiguration_isEmail");
  String invoiceConfiguration_payment = rb1.getString("invoiceConfiguration_payment");
  String invoiceConfiguration_is_late_fee = rb1.getString("invoiceConfiguration_is_late_fee");
  String invoiceConfiguration_due_date = rb1.getString("invoiceConfiguration_due_date");
  String invoiceConfiguration_late_fee = rb1.getString("invoiceConfiguration_late_fee");
  String invoiceConfiguration_unit1 = rb1.getString("invoiceConfiguration_unit1");
  String invoiceConfiguration_select_unit1 = rb1.getString("invoiceConfiguration_select_unit1");
  String invoiceConfiguration_percentage = rb1.getString("invoiceConfiguration_percentage");
  String invoiceConfiguration_flat = rb1.getString("invoiceConfiguration_flat");
  String invoiceConfiguration_terms_condition = rb1.getString("invoiceConfiguration_terms_condition");
  String invoiceConfiguration_save = rb1.getString("invoiceConfiguration_save");

%>

<html lang="en">
<head>
  <title><%=company%> Merchant Invoice > Invoice Configuration</title>



<%--
  <script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
--%>
    <%--<script type="text/javascript" language="JavaScript" src="/merchant/javascript/currency_terminal.js"></script>--%>
    <script src="/merchant/javascript/Unit.js"></script>
<%--
  <script src="/merchant/NewCss/libs/jquery-icheck/icheck.min.js"></script>
--%>

  <script>

    $(document).ready(function(){

      var w = $(window).width();

      //alert(w);

      if(w > 990){
        //alert("It's greater than 990px");
        $("body").removeClass("smallscreen").addClass("widescreen");
        $("#wrapper").removeClass("enlarged");
      }
      else{
        //alert("It's less than 990px");
        $("body").removeClass("widescreen").addClass("smallscreen");
        $("#wrapper").addClass("enlarged");
        $(".left ul").removeAttr("style");
      }
    });

  </script>

  <script language="JavaScript">

    $(function(){
      var selectedVal = $('#cc').val()+"|"+$('#telcc').val();
      var countrycode = $('#cc').val();
      var phonecc = $('#telcc').val();

      if(selectedVal!=='null')
      {
        $("#country").val(selectedVal);
      }
      if(countrycode!=='null')
      {
        $("#countrycode").val(countrycode);
      }
      if(phonecc!=='null')
      {
        $("#phonecc").val(phonecc);
      }

    });
  </script>



  <script language="javascript">

    function myjunk()
    {


      var e = document.getElementById("country");
      var strUser = e.options[e.selectedIndex].value;

      if(strUser == 'Select one')
      {
        $("#countrycode").val('');
        $("#phonecc").val('');
      }

      if (strUser != 'Select one')
      {
        $("#countrycode").val(strUser.split("|")[0]);
        $("#phonecc").val(strUser.split("|")[1]);

        if(strUser == 'US|001')
        {
          $("#us_states_id").attr("disabled", false);
          document.getElementById("b_state").readOnly = true;
        }
        else
        {
          $("#us_states_id").attr("disabled", true);
        }

      }

    }

    function usstate()
    {

      var e = document.getElementById("us_states_id");
      var usUser = e.options[e.selectedIndex].value;

      if (usUser != 'Select one') {

        $("#b_state").val(usUser);

      }
    }


  </script>

  <%--<script language="javascript">

    function myjunk()
    {
      var hat = this.document.myformname.country.selectedIndex
      var hatto = this.document.myformname.country.options[hat].value
      if(hatto == 'US|001')
      {
        $("#us_states_id").attr("disabled", false);
        document.getElementById("b_state").readOnly = true;
      }
      else
      {
        $("#us_states_id").attr("disabled", true);
        this.document.myformname.b_state.value = "";
      }
      if (hatto != 'Select one') {

        this.document.myformname.countrycode.value = hatto.split("|")[0];
        this.document.myformname.phonecc.value = hatto.split("|")[1];
        this.document.myformname.country.options[0].selected=false

      }


    }

    function usstate()
    {
      var hat = this.document.myformname.us_states.selectedIndex
      var hatto = this.document.myformname.us_states.options[hat].value

      if (hatto != 'Select one') {

        this.document.myformname.b_state.value = hatto;
        this.document.myformname.us_states.options[0].selected=false

      }
    }



  </script>--%>
  <script onload="">

  </script>


  <script language="JavaScript">


    $(function(){
      var selectedVal = $('#cc').val()+"|"+$('#telcc').val();
      var countrycode = $('#cc').val();
      var phonecc = $('#telcc').val();

      if(selectedVal!=='null')
      {
        $("#country").val(selectedVal);
      }
      if(countrycode!=='null')
      {
        $("#countrycode").val(countrycode);
      }
      if(phonecc!=='null')
      {
        $("#phonecc").val(phonecc);
      }

    });
  </script>

  <script type="text/javascript">

    $(function(){

      if($('input:checkbox[name=islatefee]:checked').val()=="Y")
      {
        document.getElementById("duedate").disabled = false;
        document.getElementById("latefee").disabled = false;
        document.getElementById("unit1").disabled = false;

      }
      else
      {
        document.getElementById("duedate").disabled = true;
        document.getElementById("latefee").disabled = true;
        document.getElementById("unit1").disabled = true;
      }


    });

    $(document).ready(function(){


      if($('input:checkbox[name=islatefee]:checked').val()=="Y")
      {
        document.getElementById("duedate").disabled = false;
        document.getElementById("latefee").disabled = false;
        document.getElementById("unit1").disabled = false;

      }
      else
      {
        document.getElementById("duedate").disabled = true;
        document.getElementById("latefee").disabled = true;
        document.getElementById("unit1").disabled = true;
      }

      $( "input[name='islatefee']").on( "click", function() {

        if($('input:checkbox[name=islatefee]:checked').val()=="Y")
        {
          document.getElementById("duedate").disabled = false;
          document.getElementById("latefee").disabled = false;
          document.getElementById("unit1").disabled = false;

        }
        else
        {
          document.getElementById("duedate").disabled = true;
          document.getElementById("latefee").disabled = true;
          document.getElementById("unit1").disabled = true;
        }
      });

    });

  </script>


  <script src="/merchant/javascript/product-details.js"></script>


  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

  <script>
    function mySave(ctoken,data)
    {
//alert("data......"+data.value);
      document.myformname.action = "/merchant/servlet/Navigation?ctoken="+ctoken+"&save="+data.value;
      document.getElementById("myformname").submit();
    }
  </script>

  <script>
    function isNumber(evt) {
      evt = (evt) ? evt : window.event;
      var charCode = (evt.which) ? evt.which : evt.keyCode;
      if (charCode > 31 && (charCode < 48 || charCode > 57 || charCode == 46)) {
        return false;
      }
      return true;
    }

  </script>

</head>

<body class="pace-done widescreen fixed-left-void">
<div class="content-page">
  <div class="content">
    <div class="page-heading">
      <form action="/merchant/servlet/UpdateInvoiceConfiguration?ctoken=<%=ctoken%>"  method="post" id="myformname"  >

        <div class="row">
          <div class="col-sm-12 portlets ui-sortable">
            <div class="widget">
              <div class="widget-header transparent">
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=invoiceConfiguration_invoice_configuration%></strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>

              <div class="widget-content padding">
                <div id="horizontal-form">

                  <%
                    InvoiceVO invoiceVO = (InvoiceVO) request.getAttribute("invoicevo");
                    InvoiceEntry invoiceEntry = new InvoiceEntry();
                    String error = (String) request.getAttribute("error");
                    MerchantTerminalVo merchantTerminalVo = new MerchantTerminalVo();
                    List<String> unitList = (List<String>) request.getAttribute("unitList");
                    List<DefaultProductList> dProductList = (List<DefaultProductList>) request.getAttribute("defaultProductList");


                    Functions functions = new Functions();
                    if (functions.isValueNull(error))
                    {
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                    }

                    String successMsg = (String) request.getAttribute("successMsg");
                    if (functions.isValueNull(successMsg))
                    {
                      out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-info-circle\"></i>&nbsp;&nbsp;" + successMsg + "</h5>");
                    }
                    String redirectUrl = "";
                    if (functions.isValueNull(invoiceVO.getRedirecturl()))
                    {
                      redirectUrl = invoiceVO.getRedirecturl();
                    }
                    else
                    {
                      redirectUrl = "";
                    }
                    String initial = invoiceVO.getInitial();
                    String expirationPeriod=invoiceVO.getExpirationPeriod();
                    String memberid = (String) session.getAttribute("merchantid");
                    /*String curr = "";
                    if (functions.isValueNull(invoiceVO.getCurrency()))
                    {
                      curr=invoiceVO.getCurrency();
                    }*/
                    String terminal = invoiceVO.getTerminalid();
                    String paymentterms="";
                    String unit = invoiceVO.getUnit();

                    String duedate="";
                    if (functions.isValueNull(invoiceVO.getDuedate()))
                    {
                      duedate=invoiceVO.getDuedate();
                    }
                    String latefee ="";
                    if(functions.isValueNull(invoiceVO.getLatefee()))
                    {
                      latefee=invoiceVO.getLatefee();
                    }
                    String city = "";
                    String country = "";
                    String telnocc = "";
                    if (functions.isValueNull(invoiceVO.getCity()))
                    {
                      city = invoiceVO.getCity();
                    }
                    String state = "";
                    if (functions.isValueNull(invoiceVO.getState()))
                    {
                      state = invoiceVO.getState();
                    }
                    if (!functions.isValueNull(initial))
                      initial = "";

                    if (!functions.isValueNull(expirationPeriod))
                      expirationPeriod = "";

                    //List<String> currencyList = invoiceEntry.getCurrencyList(memberid);
                    String initTerminalValue = (String) request.getAttribute("initTerminalValue");
                    String GST = "";
                    String tax = "";
                    if (functions.isValueNull(invoiceVO.getGST()))
                    {
                      GST = invoiceVO.getGST();
                    }
                    if (functions.isValueNull(invoiceVO.getCountry()))
                    {
                      country = invoiceVO.getCountry();
                    }
                    else
                    {
                      country = "";
                    }
                    if (functions.isValueNull(invoiceVO.getTelCc()))
                    {
                      telnocc = invoiceVO.getTelCc();
                    }
                    String issms="" ;
                    String isemail= "";
                    String isapp= "";
                    String isduedate="";
                    String islatefee="";
                    String loadDefaultProdList = "";
                    String defaultProdListChecked = "";
                    if(functions.isValueNull(invoiceVO.getIssms()))
                    {
                      issms = ("Y");
                    }
                    else
                    {
                      issms=("N");
                    }
                    if(functions.isValueNull(request.getParameter("isEmail")))
                    {
                      isemail = ("Y");
                    }
                    else
                    {
                      isemail=("N");
                    }

                    if (functions.isValueNull(invoiceVO.getIsduedate()))
                    {
                      isduedate = ("Y");
                    }
                    else
                    {
                      isduedate=("N");
                    }
                    if (functions.isValueNull(invoiceVO.getIslatefee()))
                    {
                      islatefee = ("Y");
                    }
                    else
                    {
                      islatefee=("N");
                    }

                    if (invoiceVO.getLoadDefaultProductList().equals("Y"))
                    {
                      loadDefaultProdList = "Y";
                      defaultProdListChecked = "checked";
                    }
                    else
                    {
                      loadDefaultProdList = "N";
                    }


                  %>

                  <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                  <input type="hidden" id="memberid" value="<%=memberid%>" name="memberid">

                  <input type="hidden" id="cc" value="<%=invoiceVO.getCountry()%>" name="cc">
                  <input type="hidden" id="telcc" value="<%=invoiceVO.getTelCc()%>" name="telcc">

                  <div class="form-group col-md-4 has-feedback">

                    <label  ><%=invoiceConfiguration_redirecturl1%></label>
                    <%--<input type="hidden" name="redirecturl" value="<%=ESAPI.encoder().encodeForHTMLAttribute(redirectUrl)%>">--%>
                    <input  class="form-control"  type="Text" maxlength="100"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(redirectUrl)%>"
                            name="redirecturl" size="30" <%=disabled%>>
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label  ><%=invoiceConfiguration_initial%></label>
                    <input  class="form-control"  type="Text" maxlength="100"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(initial)%>"
                            name="initial" size="30" <%=disabled%>>
                  </div>


                  <div class="form-group col-md-4 has-feedback">
                    <label ><%=invoiceConfiguration_expiration_period%></label>
                    <input class="form-control "onkeypress="return isNumber(event)" id="expirationperiod" type="Text"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(expirationPeriod)%>"
                           name="expirationperiod" size="30" <%=disabled%>>
                  </div>

                  <%--<div class="form-group col-md-4 has-feedback">
                    <label >Currency*</label>

                    <%
                      if("Y".equalsIgnoreCase(multiCurrency))
                      {
                       %>
                    <select name="currency" id="mid" class="form-control" &lt;%&ndash;style="width:267px;"&ndash;%&gt;>
                      <option value="" selected>--Select Currency--</option>
                      <%
                        for(String sCurr:currencyCodeHash.keySet())
                        {
                          String isSelected = "";
                          if (sCurr.equalsIgnoreCase(curr))
                            isSelected = "selected";
                          %>
                      <option value="<%=sCurr%>" <%=isSelected%>><%=sCurr%></option>
                      <%
                        }
                      %>
                      </select>
                    <%
                      }
                      else
                      {
                    %>
                    <select name="currency" id="mid" class="form-control" &lt;%&ndash;style="width:267px;"&ndash;%&gt;>
                      <option value="" selected>--Select Currency--</option>
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

                          if(currency1.equalsIgnoreCase("ALL"))
                          {
                            continue;
                          }

                          String isSelected = "";
                          if (currency1.equalsIgnoreCase(curr))
                            isSelected = "selected";

                      %>
                      <option value="<%=currency1%>" <%=isSelected%>><%=currency1%></option>
                      <%
                        }
                      %>
                    </select>
                    <%
                      }
                    %>
                  </div>--%>

                  <div class="form-group col-md-4 has-feedback">
                    <label  ><%=invoiceConfiguration_gst%></label>
                    <input type="text" class="form-control" value="<%=GST%>" name="gst"maxlength="4" <%=disabled%>>
                    <%--<span class="input-group-addon" style="font-weight: 800;">%</span>--%>
                    <%--<%if(validationErrorList.getError("methodofacceptance_internet")!=null){%><span class="apperrormsg" style="display: table-row;">Invalid Internet</span><%}%>--%>
                  </div>


                </div>
              </div>

            </div>
          </div>
        </div>

        <%--<div class="row">
          <div class="col-sm-12 portlets ui-sortable">
            <div class="widget">

              <div class="widget-header transparent">
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Terminal Details</strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>


              <div class="form-group col-md-4 has-feedback">
                <label  >Select Terminal Type / Payment Option</label>
                <select name="terminal" id="tid" class="form-control" &lt;%&ndash;style="width:267px;"&ndash;%&gt;>
                  <option value="0" selected>--Select TerminalID--</option>
                  <%
                    HashMap terminals= (HashMap) session.getAttribute("terminallist");
                    Map.Entry pair = null;
                    String cardtypeName = "";
                    String paymodeName = "";
                    String terminalid = "";
                    //String Currency ="";
                    String terminalValue = "";


                    if(terminals!=null)
                    {
                      Iterator it = terminals.entrySet().iterator();
                      while (it.hasNext())
                      {
                        merchantTerminalVo = new MerchantTerminalVo();
                        pair = (Map.Entry)it.next();
                        merchantTerminalVo= (MerchantTerminalVo) pair.getValue();
                        cardtypeName=merchantTerminalVo.getCardTypeName();
                        paymodeName=merchantTerminalVo.getPaymodeName();
                        //Currency= merchantTerminalVo.getCurrency();
                        terminalid = merchantTerminalVo.getTerminalId();
                        String isSelected = "";
                        terminalValue = terminalid+"-"+cardtypeName+"-"+paymodeName;
                        if (terminalValue.equalsIgnoreCase(initTerminalValue))
                        {

                          isSelected = "selected";
                        }
                        if("Y".equalsIgnoreCase(merchantTerminalVo.getIsActive()))
                        {
                        /*if("Y".equalsIgnoreCase(multiCurrency) && "ALL".equalsIgnoreCase(Currency))
                        {*/
                      %>

                         &lt;%&ndash; <option data-mid="<%=Currency%>" value="<%=terminalValue%>" <%=isSelected%>><%=terminalValue%></option>&ndash;%&gt;
                      &lt;%&ndash;<%
                        }
                        else if(!"ALL".equalsIgnoreCase(Currency))
                        {
                      %>&ndash;%&gt;
                      &lt;%&ndash;<option value="<%=terminalValue%>"><%=terminalValue%></option>&ndash;%&gt;

                          &lt;%&ndash;<option data-mid="<%=Currency%>" value="<%=terminalValue%>" <%=isSelected%>><%=terminalValue%></option>&ndash;%&gt;
                      <%
                          }
                          }
                        }
                      %>
                </select>
              </div>
            </div>
          </div>
        </div>

        <%
        %>
--%>
        <div class="row">
          <div class="col-sm-12 portlets ui-sortable">
            <div class="widget">
              <div class="widget-header transparent">
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;<%=invoiceConfiguration_add_unit%></strong> </h2>&nbsp;&nbsp;&nbsp;
                <%--<div class="form-group col-md-4 has-feedback">
                  <label>
                    *To see the added units in Product List, please click on Save button
                  </label>
                </div>--%>

                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>
              <div class="btn-group">
                <input type="button" class="btn btn-default" id="addrow1" value="Add Unit" style="margin-left: 17px;" <%=disabled%> />&nbsp;&nbsp;&nbsp;
                <label>
                 <%=invoiceConfiguration_product_units%>
                </label>
              </div>
              <div class="widget-content padding">
                <div id="horizontal-form3">
                  <%
                    int unitSize = 0;
                    if (unitList != null)
                    {
                      if (unitList.size() > 0)
                      {
                        unitSize = unitList.size();
                        int counter = 1;
                        for (String unit1 : unitList)
                        {

                  %>
                  <div class="form3">
                    <div class="form-group col-md-4 has-feedback">
                      <input type="text" size="10" placeholder="Unit" name="defaultunit<%=counter%>" class="form-control" value="<%=unit1%>" <%=disabled%>>
                    </div>
                    <div class="form-group col-md-2 has-feedback">
                      <input type="button" class="btn btn-default" name="delete" value="Delete" size="" <%=disabled%>>
                    </div>
                  </div>
                  <%
                          counter++;
                        }
                      }
                      else
                      {
                      }
                    }

                  %>
                  <input type="hidden" name="unitCounter" id="unitCounter" value="<%=unitSize%>">
                </div>
              </div>
            </div>
          </div>
        </div>
        <input type="hidden" name="productList" id="productList" value="<%=dProductList%>">

        <div class="row">
          <div class="col-sm-12 portlets ui-sortable">
            <div class="widget">

              <div class="widget-header transparent">
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=invoiceConfiguration_default_product%></strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>
              <div class="pull-left">
                <div class="btn-group">
                  <input type="button" class="btn btn-default" id="addrow" value="Add Product" style="margin-left: 17px;" <%=disabled%>/>
                </div>
              </div>

              <div class="form-group col-md-4 has-feedback">
                <label class="thcheckboxnew">

                <input type="checkbox" name="loadDefaultProdList" id="loadDefaultProdList"  style="margin-left: 25px" value="<%=loadDefaultProdList%>" <%=defaultProdListChecked%> <%=disabled%>>&nbsp;&nbsp;&nbsp;&nbsp;<%=invoiceConfiguration_load_products%></label>

              </div>

              <input type="hidden" name="unitList" id="unitList" value="<%=unitList%>">

              <div class="widget-content padding" >
                <div id="horizontal-form1">
                  <%--<div class="form1">

                        <div class="form-group col-md-4 has-feedback">
                          <label>Product Description</label>
                        </div>
                        <div class="form-group col-md-2 has-feedback">
                          <label>Product Amount</label>
                        </div>
                        <div class="form-group col-md-2 has-feedback">
                          <label>Unit</label>
                        </div>
                        <div class="form-group col-md-3 has-feedback">
                          <label>Tax</label>
                        </div>
                      </div>--%>



                    <%
                      int dProdCounter = 0;
                      if (dProductList != null)
                      {
                        if (dProductList.size() > 0)
                        {
                          %>
                    <div class="form1" id="topheader">

                      <div class="form-group col-md-4 has-feedback">
                        <label><%=invoiceConfiguration_product_description%></label>
                      </div>
                      <%--<div class="form-group col-md-2 has-feedback">
                        <label>Product Amount</label>
                      </div>--%>
                      <div class="form-group col-md-2 has-feedback">
                        <label><%=invoiceConfiguration_unit%></label>
                      </div>
                      <div class="form-group col-md-3 has-feedback" >
                        <label><%=invoiceConfiguration_tax%></label>
                      </div>
                      </div>
                          <%
                          int prodCounter = 1;
                          dProdCounter = dProductList.size();
                          for (DefaultProductList defaultProductList : dProductList)
                          {
                            String defaultProdDesc = "";
                            String defaultTax = "";
                            if(functions.isValueNull(defaultProductList.getProductDescription()))
                            {
                              defaultProdDesc = defaultProductList.getProductDescription();
                            }

                            if(functions.isValueNull(defaultProductList.getTax()))
                            {
                              defaultTax = defaultProductList.getTax();
                            }
                            %>
                    <div class="form2">
                    <div class="form-group col-md-4 has-feedback">
                      <input type="text" size="10" placeholder="Order Description" name="productDescription<%=prodCounter%>" class="form-control" value="<%=defaultProdDesc%>" >
                    </div>

                    <%--<div class="form-group col-md-2 has-feedback">
                      <input type="text" size="10" placeholder="Order Amount" name="productAmount<%=prodCounter%>" class="form-control" value="<%=defaultProductList.getProductAmount()%>" >
                    </div>--%>

                    <div class="form-group col-md-2 has-feedback">
                      <select name="productunit<%=prodCounter%>" class="form-control">
                        <option value="" ><%=invoiceConfiguration_select_unit%></option>
                        <%
                          for (String defaultUnit : unitList)
                          {
                            String isSelected = "";

                            if(defaultProductList.getUnit().equalsIgnoreCase(defaultUnit))
                            {
                              isSelected = "selected";
                            }
                        %>
                        <option value="<%=defaultUnit%>" <%=isSelected%>><%=defaultUnit%></option>
                        <%
                          }
                        %>
                      </select>
                    </div>

                    <div class="form-group col-md-3 has-feedback">
                      <input type="text" maxlength="3" placeholder="Tax" name="tax<%=prodCounter%>" class="form-control" onkeyup="checkDec(this);" value="<%=defaultTax%>" >
                    </div>

                    <div class="form-group col-md-1 has-feedback">
                      <input type="button" size="10" name="delete" class="btn btn-default" value="Delete">
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
                </div>
              </div>
              <input type="hidden" name="productCounter" id="productCounter" value="<%=dProdCounter%>">

            </div>

    <%--<div class="row">
      <div class="col-sm-12 portlets ui-sortable">
        <div class="widget">
          <div class="widget-header transparent">
            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Add Unit</strong></h2>
            <div class="additional-btn">
              <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
              <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
              <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
            </div>
          </div>

          <div class="widget-content padding">
            <div id="horizontal-form3">
              <form action="/merchant/servlet/UpdateInvoiceConfiguration?ctoken=<%=ctoken%>" id="form_id" method="post" form name="subForm">
                <%
                  if(unitList!=null && unitList.size()>0)
                  {
                    for(String prodUnit : unitList)
                    {
                %>
                <div class="form3">
                  <div class="form-group col-md-8 has-feedback"><input type="text" size="10" placeholder="Unit" class="form-control" name="pUnit" value="<%=prodUnit%>"></div>
                  <div class="form-group col-md-2 has-feedback"><input type="button" class="btn btn-default" name="delete" value="Delete" size="" placeholder=""></div>
                  <div class="form-group col-md-2 has-feedback"><input type="button" class="btn btn-default" name="add" value="Add" size="" placeholder=""></div>
                </div>
                <%
                    }
                  }
                %>
                <div class="form3">
                  <div class="form-group col-md-8 has-feedback"><input type="text" size="10" placeholder="Unit" class="form-control" name="pUnit"></div>
                  <div class="form-group col-md-2 has-feedback"><input type="button" class="btn btn-default" name="delete" value="Delete" size="" placeholder=""></div>
                  <div class="form-group col-md-2 has-feedback"><input type="button" class="btn btn-default" name="add" value="Add" size="" placeholder=""></div>
                </div>
              </form>
            </div>
          </div>

        </div>



      </div>
    </div>--%>

    <div class="row">
      <div class="col-sm-12 portlets ui-sortable">
        <div class="widget">

          <div class="widget-header transparent">
            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=invoiceConfiguration_default_customer%></strong></h2>
            <div class="additional-btn">
              <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
              <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
              <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
            </div>
          </div>

          <div class="form-group col-md-4 has-feedback">
            <label ><%=invoiceConfiguration_city%></label>
            <input  class="form-control"  type="Text" maxlength="100"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(city)%>" name="city" size="30" <%=disabled%>>
          </div>

          <div class="form-group col-md-3 has-feedback">
            <label  ><%=invoiceConfiguration_country%></label>
            <select name="country" id="country" class="form-control" onchange="myjunk();" <%=disabled%> >
              <option value="Select one"><%=invoiceConfiguration_select_a_contry%></option>
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
            <label  ><%=invoiceConfiguration_countrycode%></label>
            <input type="text" class="form-control" name="countrycode" id="countrycode" placeholder="CC" <%--value="<%=country%>"--%> <%--style="width: 50px"--%>  size="10" readonly="readonly" <%=disabled%>>
          </div>

          <div class="form-group col-md-3 has-feedback">
            <label  ><%=invoiceConfiguration_state%></label>
            <select name="us_states" onchange="usstate();" id="us_states_id" disabled="true" class="form-control" <%=disabled%>>

              <option value="Select one"><%=invoiceConfiguration_select_a_state%></option>
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
            <label  ><%=invoiceConfiguration_statecode%></label>
            <input name="state" class="form-control" type="text" value="<%=state%>" <%--style="width:50px"--%> id="b_state" size="10" <%=disabled%>>
          </div>


          <div class="form-group col-md-2 has-feedback">
            <label><%=invoiceConfiguration_phonecc%></label>
            <input type="text" class="form-control" name="phonecc" id="phonecc" size="5" readonly="readonly" placeholder="cc" <%=disabled%>></div>

        </div>
      </div>
    </div>

    <div class="row">
      <div class="col-sm-12 portlets ui-sortable">
        <div class="widget">

          <div class="widget-header transparent">
            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=invoiceConfiguration_sms_email%></strong></h2>
            <div class="additional-btn">
              <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
              <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
              <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
            </div>
          </div>

          <%
            String smsChecked = "";
            String emailChecked = "";
            String feeChecked ="";


            if(invoiceVO.getIssms().equalsIgnoreCase("Y"))
            {
              smsChecked = "checked";
            }
            if (invoiceVO.getIsemail().equalsIgnoreCase("Y"))
            {
              emailChecked="checked";
            }
            if (invoiceVO.getIslatefee().equalsIgnoreCase("Y"))
            {
              feeChecked="checked";
            }
            else



          %>
          <div class="form-group col-md-4 has-feedback">
            <label class="thcheckboxnew">
              <%
                if (invoiceVO.getSmsactivation().equals("Y"))
                {
              %>
              <input type="checkbox" name="isSMS" id="isSMS" style="margin-left: 25px"  value="<%=issms%>" <%=smsChecked%> <%=disabled%>>&nbsp;&nbsp;&nbsp;&nbsp;<%=invoiceConfiguration_isSMS%></label>

            <%
            }
            else
            {
            %>
            <input type="checkbox" name="isSMS" id="isSMS"  style="margin-left: 25px" value="<%=issms%>" <%=smsChecked%> disabled <%=disabled%>>&nbsp;&nbsp;&nbsp;&nbsp;<%=invoiceConfiguration_isSMS%></label>
            <%
              }
            %>
            <div class="form-group col-md-4 has-feedback">
              <label class="thcheckboxnew"><input type="checkbox" name="isEmail" value="<%=isemail%>"<%=emailChecked%> <%=disabled%>>&nbsp;&nbsp;&nbsp;&nbsp;<%=invoiceConfiguration_isEmail%></label>
            </div>
          </div>


          <%-- <div class="form-group col-md-4 has-feedback">
           <label class="thcheckboxnew"><input type="checkbox" name="isApp" value="<%=isapp%>"<%=appChecked%>>&nbsp;&nbsp;&nbsp;&nbsp;App Sharing</label>
             </div>--%>

        </div>
      </div>
    </div>

    <div class="row">

      <div class="col-sm-12 portlets ui-sortable">
        <%--<div class="widget green-1" style="background-color:  #68c39f;color: #fff;font-family: Helvetica Neue, Helvetica, Arial, sans-serif;font-size: 14px;line-height: 1.42857143;">--%>
        <div class="widget">
          <div class="widget-header transparent">
            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=invoiceConfiguration_payment%></strong></h2>
            <div class="additional-btn">
              <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
              <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
              <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
            </div>
          </div>

          <div>
            <label >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=invoiceConfiguration_is_late_fee%>&nbsp;&nbsp;&nbsp;</label>
            <input type="checkbox" name="islatefee"   value="<%=islatefee%>" <%=feeChecked%> <%=disabled%>>

          </div>

          <div class="widget-content padding">

            <div class="form-group col-md-3 has-feedback">
              <label  ><%=invoiceConfiguration_due_date%></label>
              <input type="text" class="form-control" name="duedate" id="duedate" size="10" value="<%=duedate%>" <%=disabled%>>
            </div>


            <div class="form-group col-md-3 has-feedback">
              <label><%=invoiceConfiguration_late_fee%></label>
              <input type="text" class="form-control" id="latefee" name="latefee" value="<%=latefee%>"   size="10" <%=disabled%>>

            </div>

            <div class="form-group col-md-3 has-feedback">
              <label><%=invoiceConfiguration_unit1%></label>
              <select name="unit" id="unit1" class="form-control" <%=disabled%>>
                <%
                  if ("Percentage".equals(unit))
                  {
                %>
                <option value="" selected><%=invoiceConfiguration_select_unit1%></option>
                <option value="Percentage" selected="selected"><%=invoiceConfiguration_percentage%></option>
                <option value="FlatFee" ><%=invoiceConfiguration_flat%></option>

                <%
                  }
                  else if("FlatFee".equals(invoiceVO.getUnit()))
                  {

                %>
                <option value="" selected><=invoiceConfiguration_select_unit1</option>
                <option value="Percentage" ><%=invoiceConfiguration_percentage%></option>
                <option value="FlatFee" selected="selected" ><%=invoiceConfiguration_flat%></option>
                <%
                  }
                  else
                  {
                %>
                <option value="" selected="selected"><%=invoiceConfiguration_select_unit1%></option>
                <option value="Percentage" ><%=invoiceConfiguration_percentage%></option>
                <option value="FlatFee"  ><%=invoiceConfiguration_flat%></option>
                <%
                  }
                %>

              </select>
            </div>

            <div class="form-group col-md-12 has-feedback">
              <label><%=invoiceConfiguration_terms_condition%>&nbsp;&nbsp;&nbsp;</label>
              <%
                if(invoiceVO.getPaymentterms()!=null)
                {
                  paymentterms = invoiceVO.getPaymentterms();
                }
              %>
              <textarea rows="4" style="width: inherit;" cols="150" name="paymentterms" value="" <%=disabled%>><%= paymentterms.trim() %></textarea>

            </div>
          </div>
        </div>
      </div>
    </div>


    <div class="form-group col-md-12 has-feedback">
      <center>
        <label >&nbsp;</label>
        <button type="submit" class="btn btn-default" style="display: -webkit-box;" <%=disabled%>><i class="fa fa-save" ></i>&nbsp;&nbsp;<%=invoiceConfiguration_save%></button>
      </center>
    </div>
    </form>
  </div>
</div>
</div>
<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
</body>
</html>