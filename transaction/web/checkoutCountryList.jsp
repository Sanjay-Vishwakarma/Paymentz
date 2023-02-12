<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericAddressDetailsVO" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericTransDetailsVO" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.manager.vo.RecurringBillingVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.MarketPlaceVO" %>
<%@ page import="com.manager.MerchantConfigManager" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.codehaus.jettison.json.JSONObject" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="com.directi.pg.PzEncryptor" %>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  Functions functions=new Functions();
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  /*request.setAttribute("transDetails", standardKitValidatorVO);
  request.setAttribute("paymenttype", standardKitValidatorVO.getPaymentType());
  request.setAttribute("cardtype", "AppleTree");*/

 /* session.setAttribute("transDetails", standardKitValidatorVO);
  session.setAttribute("paymenttype", standardKitValidatorVO.getPaymentType());
  session.setAttribute("cardtype", standardKitValidatorVO.getCardType());*/

  GenericTransDetailsVO genericTransDetailsVO     = standardKitValidatorVO.getTransDetailsVO();
  GenericAddressDetailsVO genericAddressDetailsVO = standardKitValidatorVO.getAddressDetailsVO();
  MerchantDetailsVO merchantDetailsVO   = standardKitValidatorVO.getMerchantDetailsVO();

  Logger log              = new Logger("checkoutPayment.jsp");
  HashMap paymentMap      = standardKitValidatorVO.getMapOfPaymentCardType();
  HashMap terminalMap     = standardKitValidatorVO.getTerminalMap();
  LinkedHashMap<String,TerminalVO> terminalMapLimitRouting  = standardKitValidatorVO.getTerminalMapLimitRouting();
  List<MarketPlaceVO> mpDetailsList                         = standardKitValidatorVO.getMarketPlaceVOList();
  String ctoken = "";
  ctoken                = (String) session.getAttribute("ctoken");
  String invoicenumber  = getValue(standardKitValidatorVO.getInvoiceId());
  String reqPaymode     = standardKitValidatorVO.getPaymentType();
  String reqCardtype    = standardKitValidatorVO.getCardType();

%>


<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
  <base href="/">
  <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
  <meta http-equiv='cache-control' content='no-cache'>
  <meta http-equiv='expires' content='0'>
  <meta http-equiv='pragma' content='no-cache'>
  <!-- <link rel="icon" type="image/x-icon" href="favicon.ico"> -->
  <link rel="stylesheet" href="/merchant/transactionCSS/css/raleway-font.css" type="text/css">
  <link rel="stylesheet" href="/merchant/transactionCSS/css/orbitron-font.css" type="text/css">
  <link rel="stylesheet" href="/merchant/transactionCSS/css/montserrat-font.css" type="text/css">
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.1.0/css/all.css" integrity="sha384-lKuwvrZot6UHsBSfcMvOkWwlCMgc0TaWr+30HWe3a4ltaBwTZhyTEggF5tJv8tbt" crossorigin="anonymous">
  <link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap-4.4.1/css/bootstrap.min.css">
  <link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap-float-label.min.css"/>
  <link rel="stylesheet" href="/merchant/transactionCSS/css/styles.css"/>
  <link rel="stylesheet" href="/merchant/transactionCSS/css/step-form.css"/>
  <link rel="stylesheet" href="/merchant/transactionCSS/css/success.css"/>
  <link rel="stylesheet" href="/merchant/transactionCSS/css/autocomplete.css"/>

  <style>
    .tab
    {
      height:auto !important;
    }
    .pay-btn
    {
      z-index: 6 !important;
    }
    @media only screen
    and (min-device-width : 768px)
    and (max-device-width : 1024px){
      .mainDiv{
        width:100% !important;
      }
      .modal-dialog {
        /*max-width: 100%;*/
        margin: 1.75rem auto !important;
      }
    }

    @media (min-width: 1200px) {
      .mainDiv{
        /*width: 345px;*/
        width: 400px !important;
      }
      .modal-dialog {
        max-width: auto !important;
      }
    }

    @media (max-width:576px) {
      .mainDiv{
        width: 100% !important;
      }
      .modal-dialog {
        max-width: 100% !important;
        margin: 0px !important;
      }
    }

    @media (min-width: 576px) {
      .modal-sm {
        /*max-width: 346px;*/
        max-width: 400px !important;
      }
      .modal-sm-cancel {
        max-width: 250px !important;
      }
    }

    .mainDiv{
    <%=session.getAttribute("box_shadow")!=null?"box-shadow:"+session.getAttribute("box_shadow").toString():""%>;
    <%=session.getAttribute("NEW_CHECKOUT_BOX_SHADOW") != null ? "box-shadow:"+session.getAttribute("NEW_CHECKOUT_BOX_SHADOW").toString() : "" %>;
    }

    .background {
    <%=session.getAttribute("bodybgcolor")!=null?"background:"+session.getAttribute("bodybgcolor").toString():""%>;
    <%=session.getAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR") != null ? "background:"+session.getAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR").toString() : "" %>;
    }

    /* header */
    .header{
    <%=session.getAttribute("panelheading_color")!=null?"background-color:"+session.getAttribute("panelheading_color").toString()+"!important":""%>;
    <%=session.getAttribute("bodypanelfont_color")!=null?"color:"+session.getAttribute("bodypanelfont_color").toString()+"!important":""%>;
    <%=session.getAttribute("NEW_CHECKOUT_HEADERBACKGROUND_COLOR") != null ? "background-color:"+session.getAttribute("NEW_CHECKOUT_HEADERBACKGROUND_COLOR").toString() + "!important" : "" %>;
    <%=session.getAttribute("NEW_CHECKOUT_HEADER_FONT_COLOR")!=null?"color:"+session.getAttribute("NEW_CHECKOUT_HEADER_FONT_COLOR").toString()+"!important":""%>;
    }

    .close {
    <%=session.getAttribute("bodypanelfont_color")!=null?"color:"+session.getAttribute("bodypanelfont_color").toString()+"!important":""%>;
    <%=session.getAttribute("NEW_CHECKOUT_BUTTON_FONT_COLOR")!=null?"color:"+session.getAttribute("NEW_CHECKOUT_BUTTON_FONT_COLOR").toString()+"!important":""%>;
    <%=session.getAttribute("NEW_CHECKOUT_BUTTON_COLOR")!=null?"background-color:"+session.getAttribute("NEW_CHECKOUT_BUTTON_COLOR").toString()+"!important":""%>;
    }

    .logo{                /* background color of logo  */
      background:transparent !important;
      box-shadow: none !important;
    }

    .timeout{
    <%=session.getAttribute("timer_color")!=null?"color:"+session.getAttribute("timer_color").toString()+"!important":""%>;
    <%=session.getAttribute("NEW_CHECKOUT_TIMER_COLOR") != null ? "color:"+session.getAttribute("NEW_CHECKOUT_TIMER_COLOR").toString()+"!important":""%>;
    }
    /* end of header */

    /* body */
    .top-bar{               /* navigation bar background and font color */
    <%=session.getAttribute("panelbody_color")!=null?"background-color:"+session.getAttribute("panelbody_color").toString()+"!important":""%>;
    <%=session.getAttribute("navigation_font_color")!=null?"color:"+session.getAttribute("navigation_font_color").toString()+"!important":""%>;
    <%=session.getAttribute("NEW_CHECKOUT_NAVIGATIONBAR_COLOR") != null ? "background-color:"+session.getAttribute("NEW_CHECKOUT_NAVIGATIONBAR_COLOR").toString() + "!important" : "" %>;
    <%=session.getAttribute("NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR")!=null?"color:"+session.getAttribute("NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR").toString()+"!important":""%>;
    }

    .prev-btn{                /* form previous button on navigation bar */
    <%=session.getAttribute("navigation_font_color")!=null?"color:"+session.getAttribute("navigation_font_color").toString()+"!important":""%>;
    <%=session.getAttribute("NEW_CHECKOUT_BUTTON_FONT_COLOR")!=null?"color:"+session.getAttribute("NEW_CHECKOUT_BUTTON_FONT_COLOR").toString()+"!important":""%>;
    <%=session.getAttribute("NEW_CHECKOUT_BUTTON_COLOR")!=null?"background-color:"+session.getAttribute("NEW_CHECKOUT_BUTTON_COLOR").toString()+"!important":""%>;
    }

    .footer-tab{             /* home page background color */
    <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
    <%=session.getAttribute("NEW_CHECKOUT_BODYNFOOTER_COLOR") != null ? "background-color:"+session.getAttribute("NEW_CHECKOUT_BODYNFOOTER_COLOR").toString() + "!important" : "" %>;
    }

    .options{               /* body background color */
    <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
    <%=session.getAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR")!=null?"background-color:"+session.getAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR").toString()+"!important":""%>;
    }

    .tab-icon{               /* home page icon color */
    <%=session.getAttribute("icon_color")!=null?"color:"+session.getAttribute("icon_color").toString()+"!important":""%>;
    <%=session.getAttribute("NEW_CHECKOUT_ICON_COLOR")!=null ? "color:"+session.getAttribute("NEW_CHECKOUT_ICON_COLOR").toString()+"!important" : ""%>;
    }

    .label-style , .bank-label {            /* payment type label font color*/
    <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
    <%=session.getAttribute("NEW_CHECKOUT_LABEL_FONT_COLOR")!=null?"color:"+session.getAttribute("NEW_CHECKOUT_LABEL_FONT_COLOR").toString()+"!important":""%>;
    }

    .tabs-li-wallet{       /* payment type options border color */
    <%=session.getAttribute("panelbody_color")!=null?"border-right: 1px solid "+session.getAttribute("panelbody_color").toString()+"!important":""%>;
    <%=session.getAttribute("panelbody_color")!=null?"border-bottom: 1px solid "+session.getAttribute("panelbody_color").toString()+"!important":""%>;
    }

    .form-header{          /* form header label color(Personal,Address,Card)  */
    <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
    }

    .form-control , .form-control:focus /*, .form-control:disabled, .form-control[readonly]*/ {        /* input control focus disabled background and font color */
    <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
    <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
    }

    .has-float-label label, .has-float-label>span{            /* input control label font color */
    <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
    }

    .form-label , .terms-checkbox{            /* for labels as well as terms & condition*/
    <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
    <%=session.getAttribute("NEW_CHECKOUT_LABEL_FONT_COLOR")!=null?"color:"+session.getAttribute("NEW_CHECKOUT_LABEL_FONT_COLOR").toString()+"!important":""%>;
    }

    .selectedBank{ /*  selected bank option background and font color */
    <%=session.getAttribute("panelbody_color")!=null?"background-color:"+session.getAttribute("panelbody_color").toString()+"!important":""%>;
    <%--<%=session.getAttribute("navigation_font_color")!=null?"color:"+session.getAttribute("navigation_font_color").toString()+"!important":""%>;--%>
    }

    .selectedBank .bank-label{
    <%=session.getAttribute("navigation_font_color")!=null?"color:"+session.getAttribute("navigation_font_color").toString()+"!important":""%>;
    }

    .wallet-title{
    <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString():""%>;
    }

    .list :checked+label { /* wallet selected option background color */
    <%=session.getAttribute("textbox_color")!=null?"background-color:"+session.getAttribute("textbox_color").toString()+"!important":""%>;
    <%=session.getAttribute("headpanelfont_color")!=null?"color:"+session.getAttribute("headpanelfont_color").toString()+"!important":""%>;
    }

    .list :checked+label .wallet-title{
    <%=session.getAttribute("headpanelfont_color")!=null?"color:"+session.getAttribute("headpanelfont_color").toString()+"!important":""%>;
    }

    .pay-button{             /* pay button background and font color */
    <%=session.getAttribute("textbox_color")!=null?"background-color:"+session.getAttribute("textbox_color").toString()+"!important":""%>;
    <%=session.getAttribute("headpanelfont_color")!=null?"color:"+session.getAttribute("headpanelfont_color").toString()+"!important":""%>;
    <%=session.getAttribute("NEW_CHECKOUT_BUTTON_FONT_COLOR")!=null?"color:"+session.getAttribute("NEW_CHECKOUT_BUTTON_FONT_COLOR").toString()+"!important":""%>;
    <%=session.getAttribute("NEW_CHECKOUT_BUTTON_COLOR")!=null?"background-color:"+session.getAttribute("NEW_CHECKOUT_BUTTON_COLOR").toString()+"!important":""%>;
    }

    .merchant-details{
    <%=session.getAttribute("textbox_color")!=null?"background-color:"+session.getAttribute("textbox_color").toString()+"!important":""%>;
    <%=session.getAttribute("headpanelfont_color")!=null?"color:"+session.getAttribute("headpanelfont_color").toString()+"!important":""%>;
      border: none !important;
      margin: 0px;
      padding: 4px;
    }

    .emi{
    <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
    }

    /* loader */
    .loader , .loader-edge{                    /* loader color*/
    <%=session.getAttribute("textbox_color")!=null?" color:"+session.getAttribute("textbox_color").toString()+"!important":""%>;
      opacity: .8;
    }

    .check_mark , .sa-icon.sa-success::before, .sa-icon.sa-success::after , .sa-fix{       /* success ( animation background color ) */
    <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
    }
    /* end of loader */

    .error_body{
    <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
    <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
    <%=session.getAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR") != null ? "NEW_CHECKOUT_FULLBACKGROUND_COLOR:"+session.getAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR").toString() : "" %>;
    }
    /* end of body */

    /* footer */
    .modal-footer {
    <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
    <%=session.getAttribute("NEW_CHECKOUT_BODYNFOOTER_COLOR")!=null?"background-color:"+session.getAttribute("NEW_CHECKOUT_BODYNFOOTER_COLOR").toString()+"!important":""%>;
    }
    /* end of footer */
    #vMobileNo
    {
      margin-left:27px;
      padding-left: 15px !important;
      letter-spacing: 42px;
      border: 0;
      background-image: linear-gradient(to left, black 70%, rgba(255, 255, 255, 0) 0%);
      background-position: bottom;
      background-size: 50px 1px;
      background-repeat: repeat-x;
      background-position-x: 35px;
      width: 288px !important;
      outline : none;
    }
    #vEmailID
    {
      margin-left:27px;
      padding-left: 15px !important;
      letter-spacing: 42px;
      border: 0;
      background-image: linear-gradient(to left, black 70%, rgba(255, 255, 255, 0) 0%);
      background-position: bottom;
      background-size: 50px 1px;
      background-repeat: repeat-x;
      background-position-x: 35px;
      width: 288px !important;
      outline : none;
    }

    .ddcommon .ddChild li img
    {
      width: 45px;
      height: 30px;
      border-right: solid 1px black !important;
    }
    .dd .ddTitle .ddTitleText img {
      width: 45px;
      height: 30px;
      border-right: solid 1px black !important;
    }
    .dd .ddChild li .ddlabel
    {
      font-size: 10px;
      font-weight: 700;
      margin-left: 10px;
    }
    .dd .ddTitle .ddTitleText .ddlabel
    {
      font-size: 10px;
      font-weight: 700;
      margin-left: 10px;
    }
    .ddcommon
    {
      width:360px !important;
    }
  </style>

</head>

<%
  ResourceBundle rb = null;
  rb = LoadProperties.getProperty("com.directi.pg.CurrencySymbol");

  ResourceBundle rb1 = null;
  String lang = "";
  ResourceBundle rbError = null;
  String multiLanguage = "";

  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getLanguage()))
  {
    lang = standardKitValidatorVO.getAddressDetailsVO().getLanguage().toLowerCase();
    if ("ja".equalsIgnoreCase(lang))
    {
      rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ja");
    }
    else if ("bg".equalsIgnoreCase(lang))
    {
      rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_bg");
    }
    else if ("ro".equalsIgnoreCase(lang))
    {
      rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ro");
    }
    else if ("sp".equalsIgnoreCase(lang))
    {
      rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_sp");
    }
    else
    {
      rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
    }
  }
  else if(functions.isValueNull(request.getHeader("Accept-Language")))
  {
    multiLanguage = request.getHeader("Accept-Language");
    String sLanguage[] = multiLanguage.split(",");
    if(functions.isValueNull(sLanguage[0]))
    {
      if ("ja".equalsIgnoreCase(sLanguage[0]))
      {
        rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ja");
      }
      else if ("bg".equalsIgnoreCase(sLanguage[0]))
      {
        rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_bg");
      }
      else if ("ro".equalsIgnoreCase(sLanguage[0]))
      {
        rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ro");
      }
      else
      {
        rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
      }
    }
    else
    {
      rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
    }
  }
  else
  {
    rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
  }

  String varSelectPaymentMethod   = rb1.getString("VAR_PAYMENTMETHOD");
  String varNext    =rb1.getString("VAR_NEXT");
  String varERROR   =rb1.getString("VAR_ERROR");

  String payType = "";
  String payTypeLabel="";

  String error="";
  String supportName = "";
  if(functions.isValueNull((String)request.getAttribute("error")))
  {
    error = (String) request.getAttribute("error");
  }
  List<String> keys = new ArrayList<String>(paymentMap.keySet());
%>
<body style="font-family: Montserrat;" class="background">


<noscript class="noscript">
  <h2 id="div100">
    Please enable javascript in your browser ....
  </h2>
</noscript>

<%--<script src="https://cdnjs.cloudflare.com/ajax/libs/animejs/2.0.2/anime.min.js"></script> <!-- loader animation-->--%>
<%--<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.0/umd/popper.min.js"></script>--%>
<%--<script type = "text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery-validate/1.17.0/jquery.validate.js"></script>--%>
<script async src="https://cdnjs.cloudflare.com/ajax/libs/fingerprintjs2/2.1.0/fingerprint2.js" integrity="sha256-RBGR32F9JdIr/VzBmTp/iQ73Ibl6woprrQ4xj5Rr+mM=" crossorigin="anonymous"></script>
<script async src="https://faisalman.github.io/ua-parser-js/js/ua-parser.js"></script>
<script type = "text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/anime.min.js"></script> <!-- loader animation-->
<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.min.js"></script>
<%--<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery-3.3.1.min.js"></script>--%>
<%--<script type = "text/javascript" src="/merchant/transactionCSS/js/bootstrap/js/bootstrap.min.js" ></script> <%--bootstrap 4.1.1 --%>
<script type = "text/javascript" src="/merchant/transactionCSS/js/bootstrap-4.4.1/js/bootstrap.min.js" ></script>
<%--<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.cardcheck.js"></script>--%>
<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.cardcheck.min.js"></script>
<%--<script type = "text/javascript" src="/merchant/transactionCSS/js/autocomplete.js "></script>--%>
<script type = "text/javascript" src="/merchant/transactionCSS/js/autocomplete.min.js "></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/countrylist.js"></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/statelabel.min.js"></script>
<script>
  var funcParam = {
    city: 'cityLabel',
    zip: 'zipLabel'
  }
</script>
<link rel="stylesheet" href="/merchant/transactionCSS/css/msdropdown.css">
<script src="/merchant/transactionCSS/js/msdropdown.js"></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/content.min.js"></script>

<%

  String amount = getValue(genericTransDetailsVO.getAmount());
  String partnerName = getValue(standardKitValidatorVO.getPartnerName());

  log.debug("Card type in checkoutPayment ----" + getValue(standardKitValidatorVO.getCardType()));
  log.debug("vo----"+getValue(standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName()));
  log.debug("session----" + session.getAttribute("merchantLogoName"));

  String merchantLogoFlag   = getValue(standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo());
  String partnerLogoFlag    = getValue(standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag());
  String accountId          = "";
  session.setAttribute("paymentMap", paymentMap);
  session.setAttribute("terminalmap",terminalMap);
  session.setAttribute("terminalMapLimitRouting",terminalMapLimitRouting);
  session.setAttribute("mpDetailsList",mpDetailsList);
  if (request.getAttribute("accountId") != null){
    accountId = (String) request.getAttribute("accountId");
  }

  Calendar cal=Calendar.getInstance();
  cal.setTimeInMillis(session.getLastAccessedTime());

  String tmpl_amount = standardKitValidatorVO.getAddressDetailsVO().getTmpl_amount();
  if(functions.isEmptyOrNull(tmpl_amount))
    tmpl_amount = standardKitValidatorVO.getTransDetailsVO().getAmount();
  String currencySymbol = standardKitValidatorVO.getAddressDetailsVO().getTmpl_currency();
  if(rb.containsKey(standardKitValidatorVO.getAddressDetailsVO().getTmpl_currency()))
    currencySymbol = rb.getString(standardKitValidatorVO.getAddressDetailsVO().getTmpl_currency());

  Locale locale = request.getLocale();
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getTmpl_currency()))
  {
    NumberFormat formatter = null;
    if (standardKitValidatorVO.getAddressDetailsVO().getTmpl_currency().equalsIgnoreCase("JPY"))
    {
      formatter = NumberFormat.getCurrencyInstance(Locale.JAPAN);
      double dbl = Double.valueOf(tmpl_amount);
      tmpl_amount = formatter.format(dbl);
      currencySymbol = "";
    }
    if (standardKitValidatorVO.getAddressDetailsVO().getTmpl_currency().equalsIgnoreCase("EUR"))
    {
      //formatter = NumberFormat.getCurrencyInstance(Locale.GERMANY);
      System.out.println("Locale---"+locale);
      if(locale.toString().equalsIgnoreCase("de_AT") || locale.toString().equalsIgnoreCase("fr_BE") || locale.toString().equalsIgnoreCase("nl_BE") ||
              locale.toString().equalsIgnoreCase("el_CY") || locale.toString().equalsIgnoreCase("et_EE") || locale.toString().equalsIgnoreCase("fi_FI") ||
              locale.toString().equalsIgnoreCase("fr_FR") || locale.toString().equalsIgnoreCase("de_DE") || locale.toString().equalsIgnoreCase("el_GR") ||
              locale.toString().equalsIgnoreCase("ga_IE") || locale.toString().equalsIgnoreCase("it_IT") || locale.toString().equalsIgnoreCase("lv_LV") ||
              locale.toString().equalsIgnoreCase("lt_LT") || locale.toString().equalsIgnoreCase("fr_LU") || locale.toString().equalsIgnoreCase("de_LU") ||
              locale.toString().equalsIgnoreCase("mt_MT") || locale.toString().equalsIgnoreCase("nl_NL") || locale.toString().equalsIgnoreCase("pt_PT") ||
              locale.toString().equalsIgnoreCase("sk_SK") || locale.toString().equalsIgnoreCase("sl_SI") || locale.toString().equalsIgnoreCase("es_ES"))
      {
        System.out.println("If called---");
        formatter = NumberFormat.getCurrencyInstance(Locale.FRANCE);
        currencySymbol = "";
      }
      else
      {
        System.out.println("else called---");
        formatter = NumberFormat.getInstance(Locale.US);
        formatter.setMinimumFractionDigits(2);
      }

      double dbl = Double.valueOf(tmpl_amount);
      tmpl_amount = formatter.format(dbl);

    }
    if (standardKitValidatorVO.getAddressDetailsVO().getTmpl_currency().equalsIgnoreCase("GBP"))
    {
      formatter = NumberFormat.getCurrencyInstance(Locale.UK);
      double dbl = Double.valueOf(tmpl_amount);
      tmpl_amount = formatter.format(dbl);
      currencySymbol = "";
    }
    if (standardKitValidatorVO.getAddressDetailsVO().getTmpl_currency().equalsIgnoreCase("USD"))
    {
      formatter = NumberFormat.getCurrencyInstance(Locale.US);
      double dbl = Double.valueOf(tmpl_amount);
      tmpl_amount = formatter.format(dbl);
      currencySymbol = "";
    }
  }

  log.debug("bin routing ----"+standardKitValidatorVO.getMerchantDetailsVO().getBinRouting());
  log.debug("emi flag ----------------"+standardKitValidatorVO.getMerchantDetailsVO().getEmiSupport());
%>

<script>
  landingTab ="<%=varSelectPaymentMethod%>";
  SelectPayMethodText="<%=varSelectPaymentMethod%>";
  ErrorText="<%=varERROR%>";
  NextText = "<%=varNext%>";
  InvalidYear="<%=rb1.getString("VAR_INVALID_YEAR")%>";
  InvalidMonth="<%=rb1.getString("VAR_INVALID_MONTH")%>";
  InvalidDay ="<%=rb1.getString("VAR_INVALID_DAY")%>";
  InvalidDate="<%=rb1.getString("VAR_INVALID_DATE")%>";
  CannotBeCurrentMonth="<%=rb1.getString("VAR_CANNOT_BE_CURRENT_MONTH")%>";
  YearCannotBeBefore="<%=rb1.getString("VAR_YEAR_CANNOT_BE_BEFORE")%>";
  InvalidExpiry="<%=rb1.getString("VAR_INVALID_EXPIRY")%>";
  EmptyEmail="<%=rb1.getString("VAR_EMPTY_EMAIL")%>";
  InvalidEmail="<%=rb1.getString("VAR_INVALID_EMAIL")%>";
  EmptyAddress="<%=rb1.getString("VAR_EMPTY_ADDRESS")%>";
  //InvalidAddress="<%//=rb1.getString("VAR_INVALID_ADDRESS")%>";
  EmptyCity="<%=rb1.getString("VAR_EMPTY_CITY")%>";
  EmptyCountry="<%=rb1.getString("VAR_EMPTY_COUNTRY")%>";
  EmptyState="<%=rb1.getString("VAR_EMPTY_STATE")%>";
  EmptyZip="<%=rb1.getString("VAR_EMPTY_ZIP")%>";
  EmptyBirhtdate="<%=rb1.getString("VAR_EMPTY_BIRTHDATE")%>";
  InvalidVisa="<%=rb1.getString("VAR_INVALID_VISA")%>";
  VisaNotPermitted="<%=rb1.getString("VAR_VISA_NOT_PERMITTED")%>";
  InvalidMaster="<%=rb1.getString("VAR_INVALID_MASTER")%>";
  MasterNotPermitted="<%=rb1.getString("VAR_MASTER_NOT_PERMITTED")%>";
  InvalidDiner="<%=rb1.getString("VAR_INVALID_DINER")%>";
  DinerNotPermitted="<%=rb1.getString("VAR_DINER_NOT_PERMITTED")%>";
  InvalidAMEX="<%=rb1.getString("VAR_INVALID_AMEX")%>";
  AmexNotPermitted="<%=rb1.getString("VAR_AMEX_NOT_PERMITTED")%>";
  InvalidDISC="<%=rb1.getString("VAR_INVALID_DISC")%>";
  DiscNotPermitted="<%=rb1.getString("VAR_DIC_NOT_PERMITTED")%>";
  InvalidJCB="<%=rb1.getString("VAR_INVALID_JCB")%>";
  JcbNotPermitted="<%=rb1.getString("VAR_JCB_NOT_PERMITTED")%>";
  InvalidMAESTRO="<%=rb1.getString("VAR_INVALID_MAESTRO")%>";
  MaestroNotPermitted="<%=rb1.getString("VAR_MAESTRO_NOT_PERMITTED")%>";
  InvalidINSTAPAYMENT="<%=rb1.getString("VAR_INVALID_INSTAPAYMENT")%>";
  InstapaymentNotPermitted="<%=rb1.getString("VAR_INSTAPAYMENT_NOT_PERMITTED")%>";
  InvalidRUPAY="<%=rb1.getString("VAR_INVALID_RUPAY")%>";
  RUPAYNotPermitted="<%=rb1.getString("VAR_RUPAY_NOT_PERMITTED")%>";
  InvalidCard="<%=rb1.getString("VAR_INVALID_CARD")%>";
  InvalidCVV="<%=rb1.getString("VAR_INVALID_CVV")%>";
  InvalidEMI="<%=rb1.getString("VAR_INVLAID_EMI")%>";
  InvalidCardHolderName="<%=rb1.getString("VAR_INVLAID_CARDHOLDERNAME")%>";
  Months="<%=rb1.getString("VAR_MONTHS")%>";
  SelectMonths="<%=rb1.getString("VAR_SELECT_MONTHS")%>";
  EmptyPhoneNo="<%=rb1.getString("VAR_EMPTY_PHONENO")%>";
  InvalidPhoneNo="<%=rb1.getString("VAR_INVALID_PHONENO")%>";

  /*CreditCards ="<%=rb1.getString("CreditCards")%>";
   DebitCards ="<%=rb1.getString("DebitCards")%>";
   NetBanking ="<%=rb1.getString("NetBanking")%>";
   Vouchers = "<%=rb1.getString("Vouchers")%>";
   Wallet = "<%=rb1.getString("Wallet")%>";
   ClearSettle ="<%=rb1.getString("ClearSettle")%>";
   SEPA = "<%=rb1.getString("SEPA")%>";*/

</script>

<%
  ResourceBundle RB4 = LoadProperties.getProperty("com.directi.pg.countriesAfrica");

  StringBuffer optionList = new StringBuffer(" <option value=\"\">Select a Country</option>");
  Enumeration<String> country = RB4.getKeys();

  List<String> countryCode = Collections.list(country);
  TreeMap<String, String> stringTreeMap = new TreeMap<String, String>();
  for (String countryKey : countryCode)
  {
    String Key = countryKey;
   // stringTreeMap.put(countryKey,RB4.getString(countryKey));
    stringTreeMap.put(RB4.getString(countryKey),Key);
  }


  String varPersonalinfo=rb1.getString("VAR_PERSONALINFO");
  String varEmailid   = rb1.getString("VAR_EMAILID");
  String varPhoneno   = rb1.getString("VAR_PHONENO");
  String consent1     = rb1.getString("VAR_CONSENT_STMT1");
  String consent2     = rb1.getString("VAR_CONSENT_STMT2");
  String consent3     = rb1.getString("VAR_CONSENT_STMT3");
  String varAND       = rb1.getString("VAR_AND");
  String varAccountingnumber=rb1.getString("VAR_ACCOUNTINGNUMBER");
  String varName      =rb1.getString("VAR_NAME");
  String varCountry   =rb1.getString("VAR_COUNTRY");
  String varFirstName = rb1.getString("VAR_FIRSTNAME");
  String varLastName  = rb1.getString("VAR_LASTNAME");

  String email = "";
  String phonecc = "";
  String phoneno = "";
  String firstName = "";
  String lastName = "";

  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getEmail())){
    email = ESAPI.encoder().encodeForHTML(standardKitValidatorVO.getAddressDetailsVO().getEmail());
  }
  if (functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getTelnocc()))
  {
    phonecc = standardKitValidatorVO.getAddressDetailsVO().getTelnocc();
  }
  if (functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getPhone()))
  {
    phoneno = standardKitValidatorVO.getAddressDetailsVO().getPhone();
  }
  if (functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getFirstname()))
  {
    firstName = standardKitValidatorVO.getAddressDetailsVO().getFirstname();
  }
  if (functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getLastname()))
  {
    lastName = standardKitValidatorVO.getAddressDetailsVO().getLastname();
  }
  StringBuffer paymentCardTypeStr = new StringBuffer();
  StringBuffer stringBuffer2 = new StringBuffer();

  for(Object key :paymentMap.keySet()){
    List<String> stringList = (List<String>) paymentMap.get(key);
    stringBuffer2 = new StringBuffer();
    for(String paykey : stringList ){
      if(stringBuffer2.length() > 0){
        stringBuffer2.append("_"+paykey);
      }else{
        stringBuffer2.append(paykey);
      }
    }
    if(paymentCardTypeStr.length() >0){
      paymentCardTypeStr.append(","+key+"="+stringBuffer2);
    }else{
      paymentCardTypeStr.append(key+"="+stringBuffer2);
    }
  }
  JSONObject terminalMapJSONStr = new JSONObject();

  Iterator keySet   = standardKitValidatorVO.getTerminalMap().keySet().iterator();
  String keyValue      = "";
  while (keySet.hasNext())
  {
    keyValue                     = (String)keySet.next();
    TerminalVO terminalVO   = (TerminalVO) standardKitValidatorVO.getTerminalMap().get(keyValue);
    System.out.println("terminalVO >>>>>>>>>>>>>>> "+terminalVO);
    String initiatePaymentRequest= "{" +
            "\"memberId\": \""+terminalVO.getMemberId()+"\"," +
            "\"terminalId\": \""+terminalVO.getTerminalId()+"\"," +
            "\"accountId\": \""+terminalVO.getAccountId()+"\"," +
            "\"paymodeId\": \""+terminalVO.getPaymodeId()+"\"," +
            "\"cardTypeId\": \""+terminalVO.getCardTypeId()+"\"," +
            "\"paymentName\": \""+terminalVO.getPaymentName()+"\"," +
            "\"isActive\": \""+terminalVO.getIsActive()+"\"," +
            "\"max_transaction_amount\": \""+terminalVO.getMax_trans_amount()+"\"," +
            "\"gateway\": \""+terminalVO.getGateway()+"\"," +
            "\"currency\": \""+terminalVO.getCurrency()+"\"," +
            "\"isManualRecurring\": \""+terminalVO.getIsManualRecurring()+"\"," +
            "\"addressDetails\": \""+terminalVO.getAddressDetails()+"\"," +
            "\"addressValidation\": \""+terminalVO.getAddressValidation()+"\"," +
            "\"cardDetailRequired\": \""+terminalVO.getCardDetailRequired()+"\"," +
            "\"isTokenizationActive\": \""+terminalVO.getIsTokenizationActive()+"\"," +
            "\"amountLimitCheckAccountLevel\": \""+terminalVO.getAmountLimitCheckAccountLevel()+"\"," +
            "\"amountLimitCheckTerminalLevel\": \""+terminalVO.getAmountLimitCheckTerminalLevel()+"\"," +
            "\"cardLimitCheckAccountLevel\": \""+terminalVO.getCardLimitCheckAccountLevel()+"\"," +
            "\"cardLimitCheckTerminalLevel\": \""+terminalVO.getCardLimitCheckTerminalLevel()+"\"," +
            "\"cardAmountLimitCheckAccountLevel\": \""+terminalVO.getCardAmountLimitCheckAccountLevel()+"\"," +
            "\"cardAmountLimitCheckTerminalLevel\": \""+terminalVO.getCardAmountLimitCheckTerminalLevel()+"\"," +
            "\"minTransactionAmount\": \""+terminalVO.getMin_transaction_amount()+"\"," +
            "\"maxTransactionAmount\": \""+terminalVO.getMax_transaction_amount()+"\"," +
            "\"autoRedirectRequest\": \""+terminalVO.getAutoRedirectRequest()+"\"" +
            "}";
    terminalMapJSONStr.put(keyValue,initiatePaymentRequest);
  }

  String terminalMapStr = PzEncryptor.encryptPAN(terminalMapJSONStr.toString());


%>


  <!-- modal starts -->
  <form action="/transaction/AppleTreePaymentTypeServlet" method="post" name="countryAppletree" id="countryAppletree">
    <div class="modal show" style="display: inline-block" role="dialog">
      <div class="modal-dialog modal-sm modal-dialog-centered" >
        <div class="modal-content" style="background-color: white !important;">
          <%--<div class="tooltiptextOrderId" id="tooltiptextOrderId"> <%=standardKitValidatorVO.getTransDetailsVO().getOrderId()%> </div>--%>
          <div id="target" class="mainDiv" >
            <div class="header">
              <%
                String padForAmt = "";
                if(!merchantLogoFlag.equals(null) && merchantLogoFlag.equalsIgnoreCase("Y"))
                {
                  padForAmt = "line-height: 79px;";
              %>
              <div class="logo">
                <img class="images-style1" style="max-width: 100%;max-height:100%;" src="/images/merchant/<%=session.getAttribute("merchantLogoName")%>">
              </div>
              <%
              }
              else if(!partnerLogoFlag.equals(null) && partnerLogoFlag.equalsIgnoreCase("Y"))
              {
                supportName = standardKitValidatorVO.getMerchantDetailsVO().getPartnerName();
//            padForAmt = "padding: 20px 0px;";
                padForAmt = "line-height: 79px;";
              %>
              <div class="logo">
                <img class="images-style1" style="max-width: 100%;max-height:100%" src="/images/merchant/<%=standardKitValidatorVO.getMerchantDetailsVO().getLogoName()%>">
              </div>
              <%
                }
              %>
              <div class="name" style="text-align: right;">
                <%
                  String visibility = "block";
                  String amountStyle= "";
                  if(merchantDetailsVO.getMerchantOrderDetailsDisplay().equalsIgnoreCase("N"))
                  {
                    visibility  = "none";
//                amountStyle = "font-size: 25px;"+padForAmt;
                    amountStyle = padForAmt;
                  }
                %>
                <div class="client-name" style="display: <%=visibility%>" >
              <span class="tool"><%=standardKitValidatorVO.getMerchantDetailsVO().getCompany_name()%>
                <div class="tooltiptext"><%=standardKitValidatorVO.getMerchantDetailsVO().getCompany_name()%> </div>
              </span>
                </div>
                <div class="client-order" style="display: <%=visibility%>">
              <span class="toolOrderID" id="toolOrderID"><%=standardKitValidatorVO.getTransDetailsVO().getOrderId()%>
                <div class="tooltiptextOrderId" id="tooltiptextOrderId"> <%=standardKitValidatorVO.getTransDetailsVO().getOrderId()%> </div>
              </span>
                </div>
                <div class="client-amount" style="<%=amountStyle%>"><%=currencySymbol%> <%=tmpl_amount%></div>
              </div>
              <button type="button" id="closebtn" class="close hide"  data-dismiss="modal" aria-label="Close" onclick="cancelTransaction()">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>

            <div class="modal-body">
              <div class="top-bar">

                <div class="top-left" id="topLeft" >
                  <div id="payMethodDisplay" class="truncate" style="text-align: center"><%--Payer Information--%> Your Details</div>
                  <div id="payMethod" class="truncate" style="display: none">Select Payment Method</div>
                </div>
              </div>



            <div class="tab" style="display:inline-block !important;" id="countryOptions">
              <div class="footer-tab" id="countryInfo">
                 <input type="hidden" id="currency" name="currency" value="<%=standardKitValidatorVO.getTransDetailsVO().getCurrency()%>">
                 <input type="hidden" id="accountId" name="accountId" value="<%=accountId%>">
                 <input type="hidden" id="hiddenCountry"  value="">
                 <input type="hidden" id="country"  value="">
                 <input type="hidden" id="amount"  name="TMPL_AMOUNT" value="<%=standardKitValidatorVO.getAddressDetailsVO().getTmpl_amount()%>">
                 <input type="hidden" id="totype"  name="totype" value="<%=standardKitValidatorVO.getTransDetailsVO().getTotype()%>">
                 <input type="hidden" id="transDetails" name="transDetails"  value="<%=paymentCardTypeStr.toString()%>">
                 <input type="hidden" id="terminalMap" name="terminalMap"  value="<%=terminalMapStr%>">
                 <input type="hidden" id="postcode" name="postcode"  value="<%=standardKitValidatorVO.getAddressDetailsVO().getZipCode()%>">
                <input type="hidden" name="redirectUrl" value="<%=genericTransDetailsVO.getRedirectUrl()%>">
                <input type="hidden" name="notificationUrl" value="<%=genericTransDetailsVO.getNotificationUrl()%>">
                <input type="hidden" name="desc" value="<%=standardKitValidatorVO.getTransDetailsVO().getOrderId()%>" />
                <input type="hidden" name="amount" value="<%=standardKitValidatorVO.getTransDetailsVO().getAmount()%>" />
                <div class="form-group has-float-label control-group-full" style="margin-top: 15px;">
                  <input type="text" class="form-control input-control1" id="firstname_nbi" placeholder=" "
                         oninput="this.className = 'form-control input-control1'" name="firstname" value="<%=firstName%>" autofocus maxlength="50" autocomplete="off" />
                  <label for="firstname_nbi" class="form-label"><%=varFirstName%></label>
                </div>
                <div class="form-group has-float-label control-group-full">
                  <input type="text" class="form-control input-control1" id="lastname_nbi" placeholder=" "
                         oninput="this.className = 'form-control input-control1'" name="lastname" value="<%=lastName%>" autofocus maxlength="50" autocomplete="off" />
                  <label for="lastname_nbi" class="form-label"><%=varLastName%></label>
                </div>
                 <div class="form-group has-float-label control-group-full">
                   <div class="dropdown">
                     <input  id="country_input_optional" class="form-control input-control1" placeholder=" "
                            onblur="pincodecc('country_input_optional','country_optional','phonecc_id','phonecc','country_input','country','statelabel',funcParam);"
                            oninput="this.className = 'form-control input-control1'" onkeypress="return isLetterKey(event)" autocomplete="off">
                     <label for="country_input_optional" class="form-label"><%=varCountry%></label>
                     <input type="hidden" id="country_optional" name="country" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
                     <input type="hidden" id="country_input"  value="">
                     <label style="display:none;" id="statelabel" class="form-label">State</label>
                     <label style="display:none;" id="cityLabel" class="form-label">City</label>
                     <label style="display:none;" id="zipLabel" class="form-label">Zip</label>
                     <script>
                       setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>', 'country_input_optional');
                     </script>
                   </div>
                 </div>

                <div class="form-group has-float-label control-group-half" style="width: 10% !important">
                  <input type="text" class="form-control input-control1" name="phone-CC" id="phonecc_id" placeholder=" " onkeypress="return isNumberKey(event)"
                         onkeyup="onPasteNumCheck(event)" maxlength="3" onblur="setPhoneCC('phonecc_id','phonecc')" value="<%=phonecc%>"
                         oninput="this.className = 'form-control input-control1'"/>
                  <label for="phonecc_id" class="form-label">CC</label>
                  <input type="hidden" id="phonecc" name="phone-CC" value="<%=phonecc%>"/>
                </div>
                <div class="form-group has-float-label control-group-half" style="width: 3% !important;padding: 6px 0px;">
                  -
                </div>
                <div class="form-group has-float-label control-group-half" id="PhoneNum" style="width: 85% !important">
                  <span class="input-icon-half"><i class="fas fa-phone"></i> </span>
                  <input type="text" class="form-control input-control1" id="phone" placeholder=" " onkeypress="return isNumberKey(event)"
                         onkeyup="onPasteNumCheck(event)" oninput="this.className = 'form-control input-control1'" maxlength="24" name="telno" value="<%=phoneno%>"/>
                  <label for="phone" class="form-label"><%=varPhoneno%></label>
                </div>

                <div class="form-group has-float-label control-group-full" >
                  <span class="input-icon"><i class="far fa-envelope"></i></span>
                  <input type="email" class="form-control input-control1" id="Email" placeholder=" " onfocusout="validateEmail(event.target.value ,'Email')"
                         oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
                  <label for="Email" class="form-label"><%=varEmailid%></label>
                </div>
              </div>
            </div>
              <div class="pay-btn">
                <button type="submit" class="form-btn pay-button" id="submit"
                        tabindex="0">
                  <%--Submit--%>Next
                </button>
              </div>
            </div>
            <!-- merchant details -->
            <%
              String supportSection = getValue(standardKitValidatorVO.getMerchantDetailsVO().getSupportSection());
              String supportNoNeeded = getValue(standardKitValidatorVO.getMerchantDetailsVO().getSupportNoNeeded());
              String address = getValue(standardKitValidatorVO.getMerchantDetailsVO().getAddress())+" "+getValue(standardKitValidatorVO.getMerchantDetailsVO().getState())+" "+
                      getValue(standardKitValidatorVO.getMerchantDetailsVO().getCountry())+" "+getValue(standardKitValidatorVO.getMerchantDetailsVO().getZip());
              String telNo = "";
              String emailContact = getValue(standardKitValidatorVO.getMerchantDetailsVO().getContact_emails());
              String billigDesc = "";
              String billingStatement = "";
              if(functions.isValueNull(accountId))
              {
                billigDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                billingStatement = "The Charge Descriptor on your Bank statement will appear as <font style='font-weight: bold;'><br>"+billigDesc+"</font>";
              }

            %>


            <%
              if("Y".equalsIgnoreCase(supportSection))
              {
            %>


            <div class="merchant-details ">
              <div class="footer-details">
                <%--The Charge Descriptor on your Bank statement will appear as <b> SafeChargeEUR1 </b>--%>
                <%=billingStatement%>
              </div>
              <div class="footer-details">
                <%--Aditya Industrial Estate Mindspace, Malad West,Mumbai ,Maharashtra 400064--%>
                <%=address%>
              </div>
              <div class="footer-details" >
                <%
                  if("Y".equalsIgnoreCase(supportNoNeeded))
                  {
                    telNo = getValue(standardKitValidatorVO.getMerchantDetailsVO().getTelNo());
                %>
                <i class="fas fa-phone"></i>      <%=telNo%>       &nbsp;&nbsp;&nbsp;&nbsp;
                <%
                }
                else
                {
                %>
                </i>      <%=telNo%>       &nbsp;&nbsp;&nbsp;&nbsp;

                <%
                  }
                %>
                <i class="fas fa-envelope"></i>   <%=emailContact%>
              </div>
            </div>
            <%
              }
            %>
            <!-- end of merchant details -->

            <%
              String powerBy = getValue(standardKitValidatorVO.getMerchantDetailsVO().getPoweredBy());
              String sisaLogoFlag = getValue(standardKitValidatorVO.getMerchantDetailsVO().getSisaLogoFlag());
              String securityLogoFlag = getValue(standardKitValidatorVO.getMerchantDetailsVO().getSecurityLogo());
              String logoName = getValue(standardKitValidatorVO.getMerchantDetailsVO().getLogoName());
              log.debug("merchant logo name from checkoutPayment.jsp----"+logoName);

              String align = "";
            %>

            <div class="modal-footer" style="padding: 7px 12px;">
              <%
                if(functions.isValueNull(partnerLogoFlag) && partnerLogoFlag.equalsIgnoreCase("Y"))
                {
              %>
              <div class="footer-logo" style="text-align: left;">
                <img  class="logo-style" src="/images/merchant/<%=logoName%>">
              </div>
              <%
                }
                else if((functions.isValueNull(powerBy) && functions.isValueNull(securityLogoFlag)))
                {
                  if(("Y").equalsIgnoreCase(powerBy) && ("Y").equalsIgnoreCase(securityLogoFlag)){
                    align="text-align: left;";
                  }
                  else if(("Y").equalsIgnoreCase(powerBy) && ("N").equalsIgnoreCase(securityLogoFlag)){
                    align="text-align: left;";
                  }
                  else if(("N").equalsIgnoreCase(powerBy) && ("Y").equalsIgnoreCase(securityLogoFlag)){
                    align="text-align: left;";
                  }
                  else{
                    align="text-align: center;";
                  }
                }
                else
                {
                  align="text-align: left;";
                }
                if(functions.isValueNull(sisaLogoFlag) && sisaLogoFlag.equalsIgnoreCase("Y"))
                {
                  if((functions.isValueNull(partnerLogoFlag) && partnerLogoFlag.equalsIgnoreCase("Y")) &&
                          ((functions.isValueNull(powerBy) && ("Y").equalsIgnoreCase(powerBy)) ||
                                  (functions.isValueNull(securityLogoFlag) && ("Y").equalsIgnoreCase(securityLogoFlag))))
                  {
                    align="text-align: center;c6";
                  }
              %>
              <div class="footer-logo" style="<%=align%>">
                <img  class="logo-style" src="/images/merchant/PCI.png">
              </div>
              <%
                }
                if(functions.isValueNull(powerBy) && powerBy.equalsIgnoreCase("Y"))
                {
              %>
              <div class="footer-logo" style="text-align: right;"> <%--<%=standardKitValidatorVO.getMerchantDetailsVO().getLogoName()%>--%>
                <img  class="logo-style"  src="/images/merchant/poweredBy_logo.png">
              </div>
              <%
              }
              else if(functions.isValueNull(securityLogoFlag) && securityLogoFlag.equalsIgnoreCase("Y"))
              {
              %>
              <div class="footer-logo" style="text-align: right;">
                <img  class="logo-style" src="/images/merchant/security.png" style="width: auto;margin: 0px 15px;">
              </div>
              <%
                }
              %>
            </div>
            <% try
            {
            }
            catch (Exception e)
            {
            } %>
            <jsp:include page="requestParameters.jsp">
              <jsp:param name="paymentMode" value=""/>
            </jsp:include>



          </div>
          <!-- end of merchant details -->
        </div>

      </div>
    </div>
  </form>

</div>
<script type="text/javascript">
  $(document).ready(function() {
    $("#merchantcountry").msDropDown();
  });
</script>
</body>
</html>

<%!


  private String getValue(String data)
  {
    String tempVal="";
    if(data==null)
    {
      tempVal="";
    }
    else
    {
      tempVal= data;
    }
    return tempVal;
  }
%>