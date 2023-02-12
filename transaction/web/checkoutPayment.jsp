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
<%

  response.setHeader("Pragma", "No-cache");
  response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
  response.setDateHeader("Expires", -1);


%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%!
  public String getPaymentTypeIcon(String paymentId)
  {
    String iClassIcon = "";
    if("CreditCards".equals(paymentId))
      iClassIcon = "far fa-credit-card";
    if("VirtualAccount".equals(paymentId))
      iClassIcon = "fas fa-desktop";
    if("Wallet".equals(paymentId) || "Wallets".equals(paymentId) || "MobileBankingBangla".equals(paymentId) || "WalletAfrica".equals(paymentId))
      iClassIcon = "far fa-money-bill-alt";
    if("DebitCards".equals(paymentId))
      iClassIcon = "far fa-credit-card";
    if("NetBanking".equals(paymentId) || "Net-Banking".equals(paymentId) || "Net Banking".equals(paymentId)
            || "MobileBanking".equals(paymentId) || "BankTransfer".equals(paymentId) || "InstantBankTransfer".equals(paymentId)
            || "BankTransferAfrica".equals(paymentId) || "TIGERPAY".equals(paymentId)
            )
      iClassIcon = "fas fa-university";
    if("MobileMoneyAfrica".equals(paymentId))
      iClassIcon = "fas fa-mobile-alt";
    if("Vouchers".equals(paymentId))
      iClassIcon = "fas fa-tag";
    if("SEPA".equals(paymentId))
      iClassIcon = "fas fa-euro-sign";
    if("PrepaidCards".equals(paymentId))
      iClassIcon = "fas fa-credit-card";
    if("PostpaidCards".equals(paymentId))
      iClassIcon = "fas fa-credit-card";
    if("ACH".equals(paymentId) || "CHK".equals(paymentId))
      iClassIcon = "fas fa-university";
    if("ClearSettle".equals(paymentId))
      iClassIcon = "fas fa-credit-card";
    if("CRYPTO".equals(paymentId))
      iClassIcon = "fab fa-keycdn";
    if("PLMP".equals(paymentId))
      iClassIcon = "fas fa-qrcode";
    if("BITCOIN".equals(paymentId))
      iClassIcon = "fab fa-bitcoin";
    if("PAYG".equals(paymentId))
      iClassIcon = "far fa-credit-card";
    if("GiftCardAfrica".equals(paymentId))
      iClassIcon = "fas fa-gift";
    if("CARD".equals(paymentId))
      iClassIcon = "fas fa-credit-card";
    if("CASH".equals(paymentId))
      iClassIcon = "far fa-money-bill-alt";


    return iClassIcon;
  }
%>
<%
  Functions functions=new Functions();
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");

  GenericTransDetailsVO genericTransDetailsVO     = standardKitValidatorVO.getTransDetailsVO();
  GenericAddressDetailsVO genericAddressDetailsVO = standardKitValidatorVO.getAddressDetailsVO();
  MerchantDetailsVO merchantDetailsVO   = standardKitValidatorVO.getMerchantDetailsVO();
  RecurringBillingVO recurringBillingVO = standardKitValidatorVO.getRecurringBillingVO();
  String personalDetailValidation       = merchantDetailsVO.getPersonalInfoValidation();
  String personalDetailDisplay          = merchantDetailsVO.getPersonalInfoDisplay();
  String isOTPRequired          =     merchantDetailsVO.getIsOTPRequired();

  String merchantSiteName = getValue(standardKitValidatorVO.getMerchantDetailsVO().getMerchantDomain());
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

  //String partnerSiteUrl = getValue(standardKitValidatorVO.getMerchantDetailsVO().getPartnerDomain());
  /*Logger log = new Logger("commonPayment.jsp");

  String headerVal = "";

  if(functions.isValueNull((String)session.getAttribute("X-Frame-Options")) && "SAMEORIGIN".equals((String)session.getAttribute("X-Frame-Options")))
  {
    response.setHeader("X-Frame-Options", (String) session.getAttribute("X-Frame-Options"));
  }
  else
  {
    String responseOrigin = request.getHeader("referer");
    if(functions.isValueNull(responseOrigin) && functions.isValueNull(merchantSiteName) && responseOrigin.contains(merchantSiteName))
    {

      headerVal = functions.getIFrameHeaderValue(responseOrigin,merchantSiteName);
      if("SAMEORIGIN".equals(headerVal))
      {
        response.setHeader("X-Frame-Options",headerVal);
        session.setAttribute("X-Frame-Options", headerVal);
      }

    }
    else
    {
      headerVal = "SAMEORIGIN";
      response.setHeader("X-Frame-Options",headerVal);
      session.setAttribute("X-Frame-Options", headerVal);
    }

  }

  log.debug("header---" + headerVal);
  response.setHeader("Cache-control", "no-store"); //HTTP 1.1
  response.setHeader("Pragma","no-cache"); //HTTP1.0
  response.setDateHeader("Expire", 0); //prevents caching at the proxy server
  response.setCharacterEncoding("UTF-8");
  response.setContentType("text/html");*/
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
    <%=session.getAttribute("NEW_CHECKOUT_BODYNFOOTER_COLOR") != null ? "background-color:"+session.getAttribute("NEW_CHECKOUT_BODYNFOOTER_COLOR").toString() + "!important" : "" %>;
    }
    /* end of footer */
  </style>

  <%--<script src="https://cdnjs.cloudflare.com/ajax/libs/animejs/2.0.2/anime.min.js"></script> <!-- loader animation-->--%>
  <%--<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.0/umd/popper.min.js"></script>--%>
  <%--<script type = "text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery-validate/1.17.0/jquery.validate.js"></script>--%>
  <script async src="https://cdnjs.cloudflare.com/ajax/libs/fingerprintjs2/2.1.0/fingerprint2.js" integrity="sha256-RBGR32F9JdIr/VzBmTp/iQ73Ibl6woprrQ4xj5Rr+mM=" crossorigin="anonymous"></script>
  <script async src="https://faisalman.github.io/ua-parser-js/js/ua-parser.js"></script>
  <script defer  type = "text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
  <script defer  type = "text/javascript" src="/merchant/transactionCSS/js/anime.min.js"></script> <!-- loader animation-->
  <script defer  type = "text/javascript" src="/merchant/transactionCSS/js/jquery.min.js"></script>
  <%--<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery-3.3.1.min.js"></script>--%>
  <%--<script type = "text/javascript" src="/merchant/transactionCSS/js/bootstrap/js/bootstrap.min.js" ></script> <%--bootstrap 4.1.1 --%>
  <script defer  type = "text/javascript" src="/merchant/transactionCSS/js/bootstrap-4.4.1/js/bootstrap.min.js" ></script>
  <%--<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.cardcheck.js"></script>--%>
  <script defer  type = "text/javascript" src="/merchant/transactionCSS/js/jquery.cardcheck.min.js"></script>
  <%--<script type = "text/javascript" src="/merchant/transactionCSS/js/autocomplete.js "></script>--%>
  <script defer  type = "text/javascript" src="/merchant/transactionCSS/js/autocomplete.min.js "></script>
  <script defer  type = "text/javascript" src="/merchant/transactionCSS/js/countrylist.js"></script>
  <script defer  type = "text/javascript" src="/merchant/transactionCSS/js/statelabel.min.js"></script>

  <script defer  type="text/javascript" src="/merchant/transactionCSS/js/content.min.js"></script>
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
      rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_ja");
    }
    else if ("bg".equalsIgnoreCase(lang))
    {
      rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_bg");
      rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_en-us");
    }
    else if ("ro".equalsIgnoreCase(lang))
    {
      rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ro");
      rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_en-us");
    }
    else if("sp".equalsIgnoreCase(lang))
    {
      rb1=LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_sp");
      rbError=LoadProperties.getProperty("com.direct1.pg.CheckoutError_en_us");
    }
    else
    {
      rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
      rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_en-us");
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
        rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_ja");
      }
      else if ("bg".equalsIgnoreCase(sLanguage[0]))
      {
        rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_bg");
        rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_en-us");
      }
      else if ("ro".equalsIgnoreCase(sLanguage[0]))
      {
        rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ro");
        rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_en-us");
      }
      else if ("sp".equalsIgnoreCase(sLanguage[0]))
      {
        rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_sp");
        rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_en-us");
      }
      else
      {
        rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
        rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_en-us");
      }
    }
    else
    {
      rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
      rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_en-us");
    }
  }
  else
  {
    rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
    rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_en-us");
  }

  String varSelectPaymentMethod = rb1.getString("VAR_PAYMENTMETHOD");
  String varPAY=rb1.getString("VAR_PAY");
  String varNext=rb1.getString("VAR_NEXT");
  String varERROR=rb1.getString("VAR_ERROR");
  String varTIMEOUT=rb1.getString("VAR_TIMEOUT");

  String payType = "";
  String payTypeLabel="";
  if(paymentMap.size() == 1)
  {
    String pId = (String)paymentMap.keySet().toArray()[0];
    log.debug("paymentMap---->"+paymentMap);
    if(GatewayAccountService.getPaymentTypes(pId.toString())!= null){
      String pt = GatewayAccountService.getPaymentTypes(pId.toString());
      if(pt.equals("WalletIndia") || pt.equals("WalletBangla"))
        pt = "Wallets";
      if(pt.equals("NetBankingIndia") || pt.equals("NetBankingBangla"))
        pt = "Net-Banking";
      if(rb1.containsKey(pt))
      {
        payTypeLabel = rb1.getString(pt);
      }else{
        payTypeLabel = pt;
      }
      payType = pt;
    }
  }

  String error="";
  String supportName = "";
  if(functions.isValueNull((String)request.getAttribute("error")))
  {
    error = (String) request.getAttribute("error");
  }
  List<String> keys = new ArrayList<String>(paymentMap.keySet());
  if(functions.isValueNull(reqPaymode) && functions.isValueNull(reqCardtype))
  {
    log.debug("else if body---");
    String paymentType = GatewayAccountService.getPaymentTypes(reqPaymode.toString());
    String paymentTypeDisplayLabel ="";

    if(paymentType.equals("WalletIndia") || paymentType.equals("WalletBangla"))
      paymentType = "Wallets";
    if(paymentType.equals("NetBankingIndia") || paymentType.equals("NetBankingBangla") )
      paymentType = "Net-Banking";

    if(rb1.containsKey(paymentType))
    {
      paymentTypeDisplayLabel = rb1.getString(paymentType);
    }
    else{
      paymentTypeDisplayLabel = paymentType;
    }

    String cardType = GatewayAccountService.getCardType(reqCardtype.toString());
    cardType = cardType.replaceAll(" ", "_");
    String cardTypeDisplayLabel ="";
    log.debug("card value---"+cardType);
    if(rb1.containsKey(cardType))
    {
      cardTypeDisplayLabel = rb1.getString(cardType);
    }
%>

<body style="font-family: Montserrat;" class="background" onload="loadFromBody('<%=paymentTypeDisplayLabel%>','<%=paymentType%>','<%=cardType%>','<%=cardTypeDisplayLabel%>','<%=error.toString()%>','<%=keys%>','<%=personalDetailDisplay%>','<%=personalDetailValidation%>','<%=payType%>','<%=payTypeLabel%>',<%=paymentMap.size()%>,'<%=genericAddressDetailsVO.getCountry()%>','<%=isOTPRequired%>')">
  <%
  }
  else if(functions.isValueNull(reqPaymode))
  {
    String paymentType = GatewayAccountService.getPaymentTypes(reqPaymode.toString());
    log.debug("if body load---"+paymentType);
    String paymentTypeDisplayLabel ="";

    if(paymentType.equals("WalletIndia") || paymentType.equals("WalletBangla"))
      paymentType = "Wallets";
    if(paymentType.equals("NetBankingIndia") || paymentType.equals("NetBankingBangla"))
      paymentType = "Net-Banking";

    if(rb1.containsKey(paymentType))
    {
      paymentTypeDisplayLabel = rb1.getString(paymentType);
    }else{
          paymentTypeDisplayLabel = paymentType;
    }
  %>
<body style="font-family: Montserrat;" class="background" onload="loadFromBody('<%=paymentTypeDisplayLabel%>','<%=paymentType%>',null,null,'<%=error%>',null,'<%=personalDetailDisplay%>','<%=personalDetailValidation%>','<%=payType%>','<%=payTypeLabel%>',<%=paymentMap.size()%>,'<%=genericAddressDetailsVO.getCountry()%>','<%=isOTPRequired%>')">
  <%
  }
  else
  {
%>
<body style="font-family: Montserrat;" class="background" onload="loadFromBody(null ,null ,null,null,'<%=error%>','<%=keys%>','<%=personalDetailDisplay%>','<%=personalDetailValidation%>','<%=payType%>','<%=payTypeLabel%>',<%=paymentMap.size()%>,'<%=genericAddressDetailsVO.getCountry()%>','<%=isOTPRequired%>')">
<%
    log.debug("else body load---");
  }
%>

<noscript class="noscript">
  <h2 id="div100">
    Please enable javascript in your browser ....
  </h2>
</noscript>


<%

  String amount = getValue(genericTransDetailsVO.getAmount());
  String partnerName = getValue(standardKitValidatorVO.getPartnerName());
  log.debug("Card type in checkoutPayment ----" + getValue(standardKitValidatorVO.getCardType()));
  log.debug("vo----"+getValue(standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogoName()));
  log.debug("session----" + session.getAttribute("merchantLogoName"));

  String merchantLogoFlag     = getValue(standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo());
  String consentFlag          = getValue(standardKitValidatorVO.getMerchantDetailsVO().getConsentFlag());
  String checkoutTimerFlag    = getValue(standardKitValidatorVO.getMerchantDetailsVO().getCheckoutTimerFlag());
  String checkoutTimerTime    = getValue(standardKitValidatorVO.getMerchantDetailsVO().getCheckoutTimerTime());
  String partnerLogoFlag      = getValue(standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag());
  String cardExpiryDateCheck  = getValue(standardKitValidatorVO.getMerchantDetailsVO().getCardExpiryDateCheck());
  String checksum             = getValue(standardKitValidatorVO.getTransDetailsVO().getChecksum());

  String attemptThreeD = "";
  attemptThreeD = getValue(standardKitValidatorVO.getAttemptThreeD());
  String isEmailWhiteListed = "";
  String memberId="";
  String accountId = "";
  String isCardDetailsRequired = "";

  String firstName = getValue(genericAddressDetailsVO.getFirstname());
  String lastName = getValue(genericAddressDetailsVO.getLastname());
  String dob = getValue(genericAddressDetailsVO.getBirthdate());

  if (merchantDetailsVO != null && functions.isValueNull(merchantDetailsVO.getAccountId()))
    accountId = merchantDetailsVO.getAccountId();
  if (merchantDetailsVO != null && functions.isValueNull(merchantDetailsVO.getMemberId()))
    memberId = merchantDetailsVO.getMemberId();
  session.setAttribute("paymentMap", paymentMap);
  session.setAttribute("terminalmap",terminalMap);
  session.setAttribute("terminalMapLimitRouting",terminalMapLimitRouting);
  session.setAttribute("mpDetailsList",mpDetailsList);

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


</script>



<input type="hidden" id="memberId" value="<%=standardKitValidatorVO.getMerchantDetailsVO().getMemberId()%>" />
<input type="hidden" id="clkey" value="<%=merchantDetailsVO.getKey()%>">
<input type="hidden" id="userName" value="<%=merchantDetailsVO.getLogin()%>">
<input type="hidden" id="partId" value="<%=merchantDetailsVO.getPartnerId()%>">
<input type="hidden" id="transactionOrder_id" value="<%=standardKitValidatorVO.getTransDetailsVO().getOrderId()%>" />
<input type="hidden" id="binRouting" value="<%=standardKitValidatorVO.getMerchantDetailsVO().getBinRouting()%>"/>
<input type="hidden" id="emiSupport" value="<%=standardKitValidatorVO.getMerchantDetailsVO().getEmiSupport()%>"/>
<input type="hidden" id="consentFlag" value="<%=consentFlag%>" />
<input type="hidden" id="checkoutTimerFlag" value="<%=checkoutTimerFlag%>">
<input type="hidden" id="checkoutTimerTime" value="<%=checkoutTimerTime%>">
<input type="hidden" id="cardExpiryDateCheck" value="<%=cardExpiryDateCheck%>">
<!-- modal starts -->
<div class="modal show" style="display: inline-block" role="dialog">
  <div class="modal-dialog modal-sm modal-dialog-centered" >
    <div class="modal-content">
      <%--<div class="tooltiptextOrderId" id="tooltiptextOrderId"> <%=standardKitValidatorVO.getTransDetailsVO().getOrderId()%> </div>--%>
      <div id="target" class="mainDiv" >
        <div class="header">
          <%
            String padForAmt = "";
            if(!merchantLogoFlag.equals(null) && merchantLogoFlag.equalsIgnoreCase("Y"))
            {
              supportName = standardKitValidatorVO.getMerchantDetailsVO().getCompany_name();
//              padForAmt = "padding: 20px 0px;";
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
          <%--<button type="button" id="close" class="close" aria-label="Close"  onclick="m()">    <!-- onclick="activeModal.dismiss('Cross click')"-->
            <span aria-hidden="true">&times;</span>
          </button>--%>
          <button type="button" id="closebtn" class="close hide"  data-dismiss="modal" aria-label="Close" onclick="cancelTransaction()">
            <span aria-hidden="true">&times;</span>
          </button>
          <%
            if (checkoutTimerFlag.equalsIgnoreCase("Y")){%>
          <input type="hidden" id="timeoutValue" value="<%=checkoutTimerTime%>">
          <div class="timeout"><%=varTIMEOUT%> = <span id="timer"></span></div>

          <%}%>
        </div>

        <div class="modal-body">
          <div class="top-bar">
            <%--<div class="top-right" id="topRight" style="cursor: pointer">F
              <span onclick="backToHome()"><i class="fas fa-home"></i></span>
            </div>--%>

            <div class="top-left" id="topLeft" >
              <span class="" id="backArrow" style="float: left;width: 20px;cursor: pointer" onclick="back()" ><i class="fas fa-angle-left"></i></span>
              <%--<span class="hide" id="errorBackArrow" style="color:blue;float: left;width: 20px;cursor: pointer" onclick="backFromError()" ><i class="fas fa-angle-left"></i></span>--%>
              <span class="hide" id="cancelbackArrow" style="float: left;width: 20px;cursor: pointer" onclick="cancel()" >
                <i class="fas fa-angle-left"></i>
              </span>
              <div id="payMethodDisplay" class="truncate" style="text-align: center"><%=varSelectPaymentMethod%></div>
              <div id="payMethod" class="truncate" style="display: none">Select a Payment Method!</div>
            </div>
          </div>


          <%
            if(functions.isValueNull(error))
            {
              log.debug("inside error---"+error);
          %>

          <div class="check_mark">
            <div id="load" class="loader hide">
            </div>


            <div id="error">
              <%-- start of error --%>

              <div id="" style="text-align: left;padding: 10px 12px 10px 0px;height: 305px;overflow: auto;" class="error_body">
                <ul>
                  <%
                    if(error.contains("<BR>"))
                    {
                      String errorMsg[] = error.split("<BR>");

                      for(String singleError : errorMsg)
                      {
                        if(singleError!=null&&functions.isValueNull(singleError))
                        {


                  %>
                  <li>
                    <%--<%=rbError.getString(singleError).replaceAll("\\*", supportName)%>--%>
                    <%=rbError.containsKey(singleError)?rbError.getString(singleError).replaceAll("\\*",supportName):singleError%>
                  </li>
                  <%    }
                  }
                  }
                  else
                  {
                  %>
                  <li>
                    <%--<%=rbError.getString(error).replaceAll("\\*",supportName)%>--%>
                    <%=rbError.containsKey(error)?rbError.getString(error).replaceAll("\\*",supportName):error%>
                  </li>
                  <%
                    }
                  %>
                </ul>
              </div>

              <%-- end of error --%>
            </div>
          </div>
          <%
          }
          else
          {
          %>

          <div class="check_mark hide">
            <div id="load" class="loader hide">
            </div>
          </div>

          <%
            }
          %>

          <div id="options" <%--style="max-height: 305px;overflow-y:auto;"--%>>
            <div class="footer-tab">
              <ul class="nav nav-tabs" id="myTab">

                <%
                  String gateWayName  = "";
                  String payementImg  = "";
                  String paymentTypeLable  = "";

                  for(Object paymentId : paymentMap.keySet())
                  {
                    log.debug("paymentType---->"+GatewayAccountService.getPaymentTypes(paymentId.toString())+"   paymentId--->"+paymentId.toString());

                    String paymentType  = GatewayAccountService.getPaymentTypes(paymentId.toString());

                    if(paymentId.toString().equalsIgnoreCase("1")){
                      List<String> cardList   = (List<String>) paymentMap.get(paymentId.toString());
                      for (String cardId : cardList)
                      {
                        String cardName         = GatewayAccountService.getCardType(cardId);
                        TerminalVO terminalVO   = null;
                        String currency         = standardKitValidatorVO.getTransDetailsVO().getCurrency();
                        String multiCurrency    = standardKitValidatorVO.getMerchantDetailsVO().getMultiCurrencySupport();

                        if (multiCurrency.equalsIgnoreCase("Y"))
                        {
                          String terminalkey = "CC-" + cardName + "-" + "ALL";
                          terminalVO         = (TerminalVO) terminalMap.get(terminalkey);

                          if (terminalVO == null || terminalMap.size() == 0){
                            terminalVO = (TerminalVO) terminalMap.get("CC-" + paymentType + currency);
                          }
                        }
                        else{
                          terminalVO = (TerminalVO) terminalMap.get("CC-" + paymentType + "-" + currency);
                        }
                        if(terminalVO != null){
                          if(terminalVO.getGateway().equalsIgnoreCase("Appletree")){
                            gateWayName = "Appletree";
                            payementImg = "AppletreeCard";
                          }
                        }
                      }
                    }
                    if (paymentType.equals("WalletIndia") || paymentType.equals("WalletBangla"))
                      paymentType = "Wallets";
                    if(paymentType.equals("NetBankingIndia") || paymentType.equals("NetBankingBangla"))
                      paymentType = "Net-Banking";

                    if(paymentId.toString().equalsIgnoreCase("1") && gateWayName.equalsIgnoreCase("Appletree")){
                      paymentTypeLable = rb1.getString("apppleTreeCard");
                    }else{
                      if(rb1.containsKey(paymentType))
                      {
                        paymentTypeLable = rb1.getString(paymentType);
                      }
                    }
                %>
                <%--<li class="tabs-li" onclick="PaymentOptionHideShow('<%=rb1.getString(paymentType)%>', '<%=paymentType%>')" style="pointer-events: none">--%>
                <li class="tabs-li" onclick="PaymentOptionHideShow('<%=paymentTypeLable%>', '<%=paymentType%>')" style="pointer-events: none">
                  <%if(paymentType.equalsIgnoreCase("BankTransferAfrica") || paymentType.equalsIgnoreCase("GiftCardAfrica")
                          ||paymentType.equalsIgnoreCase("MobileMoneyAfrica") ||paymentType.equalsIgnoreCase("WalletAfrica") ){
                    if(rb1.containsKey(paymentType)) {%>
                  <a href="#<%=paymentType%>"  data-toggle="tab" title="<%=rb1.getString(paymentType)%>" aria-expanded="false">
                      <%}else
                      {
                  %>
                      <a href="#<%=paymentType%>"  data-toggle="tab" title="<%=paymentType%>" aria-expanded="false">
                  <%}
                    }else{%>
                      <a href="#<%=paymentType%>"  data-toggle="tab" title="<%=paymentType%>" aria-expanded="false">
                  <%}%>

                    <span class="round-tabs two tab-icon " >
                      <%
                        if(!paymentType.equalsIgnoreCase("ACH") && !paymentType.equalsIgnoreCase("CHK") && !paymentType.equalsIgnoreCase("UPI")
                                && !paymentType.equalsIgnoreCase("ROMCARD") && !paymentType.equalsIgnoreCase("TOJIKA") && !paymentType.equalsIgnoreCase("VOGUEPAY")
                                && !paymentType.equalsIgnoreCase("eCheck" ) && !paymentType.equalsIgnoreCase("MobileBanking") && !paymentType.equalsIgnoreCase("DOKU")
                                && !paymentType.equalsIgnoreCase("FASTPAY") && !paymentType.equalsIgnoreCase("ECOSPEND")
                                && !paymentType.equalsIgnoreCase("BankTransferAfrica") && !paymentType.equalsIgnoreCase("GiftCardAfrica") && !paymentType.equalsIgnoreCase("SMARTFASTPAY")
                                && !paymentType.equalsIgnoreCase("MobileMoneyAfrica") && !paymentType.equalsIgnoreCase("WalletAfrica") && !paymentType.equalsIgnoreCase("CajaRural")
                                ) {
                      %>
                      <%if(gateWayName.equalsIgnoreCase("Appletree")){%>
                          <img src="/images/merchant/<%=payementImg%>.png" height="49px">
                      <%}else{%>
                          <i class="<%=getPaymentTypeIcon(paymentType)%>"></i>
                      <%}%>
                      <%
                      }
                      else{
                      %>
                      <img src="/images/merchant/<%=paymentType%>.png" height="49px">
                      <%
                        }
                      %>
                    </span>
                       <%if(gateWayName.equalsIgnoreCase("Appletree") && paymentId.toString().equalsIgnoreCase("1")){%>
                        <div class="label-style"><%=rb1.getString("apppleTreeCard")%></div>
                      <%}else{
                      if(rb1.containsKey(paymentType))
                      {
                      %>
                        <div class="label-style"><%=rb1.getString(paymentType)%></div>
                      <%}
                      }%>
                  </a>
                </li>
                <%
                  }
                %>
              </ul>
            </div>
          </div>


          <div class="tab-content">
            <%
              for(Object paymentId : paymentMap.keySet())
              {
                String paymentType = GatewayAccountService.getPaymentTypes(paymentId.toString());
                String paymentMode = GatewayAccountService.getPaymentMode(paymentId.toString());

                String displayType = paymentType;
                if(paymentMode.equals("CCI"))
                {
                  paymentType = "CreditCardsIndia";
                  displayType = "CreditCards";
                }
                if(paymentMode.equals("EWI"))
                {
                  paymentType = "WalletIndia";
                  displayType = "Wallets";
                }
                if(paymentMode.equals("EWB")) {
                  paymentType = "WalletBangla";
                  displayType = "Wallets";
                }
                if(paymentMode.equals("DCI"))
                {
                  paymentType = "DebitCardsIndia";
                  displayType = "DebitCards";
                }
                if(paymentMode.equals("NBI"))
                {
                  paymentType = "NetBankingIndia";
                  displayType = "Net-Banking";
                }
                if(paymentMode.equals("NBB")) {
                  paymentType = "NetBankingBangla";
                  displayType = "Net-Banking";
                }

                String fileName = "";
                String val ="";

                if(paymentMode.equals("DC")){
                  fileName = "CreditCards.jsp";
                  val = "dc";
                }
                else if(paymentMode.equals("DCI")){
                  fileName = "CreditCardsIndia.jsp";
                  val = "dc";
                }
                else if(paymentMode.equals("NBB")){
                  fileName = "NetBankingIndia.jsp";
                  val = "NBB";
                }
                else if(paymentMode.equals("EWB")){
                  fileName = "WalletIndia.jsp";
                  val = "EWB";
                }else if(paymentMode.equals("BTA") || paymentMode.equals("GCA")  || paymentMode.equals("MMA") || paymentMode.equals("EWA")){
                  fileName = "appleTreeAfrica.jsp";
                  val = paymentMode;
                }
                else{
                  fileName = paymentType+".jsp";
                  val = "";
                }

                log.debug("file name---"+fileName);
            %>
            <div class="tab-pane options" id="<%=displayType%>">
              <jsp:include page="<%=fileName%>">
                <jsp:param name="id" value="<%=val%>"/>
                <jsp:param name="payment_Type" value="<%=paymentType%>"/>
                <jsp:param name="payment_Id" value="<%=paymentId.toString()%>"/>
              </jsp:include>
            </div>
            <%
              }
            %>


            <%
              String disabledButton = "";
              if(consentFlag.equalsIgnoreCase("Y"))
                disabledButton = "disabledbutton";
            %>

            <div class="pay-btn hide <%=disabledButton%>"  id="paybutton" >
              <button type="button" class="pay-button" onclick="pay()" tabindex="0">
                <span id="payNowLabel"><%=varPAY%> <%=currencySymbol%> <%=tmpl_amount%></span>
              </button>
            </div>
          </div>

          <%--Cancel button popup--%>
          <div class="modal fade" id="CancelModal">
            <div class="modal-dialog-cancel modal-sm-cancel modal-dialog-centered">
              <div class="modal-content-cancel">
                <div class="modal-header-cancel">
                  <div class="modal-title-cancel"><%=rb1.getString("VAR_CANCEL_TITLE")%> </div>
                  <%--<button type="button" class="close" data-dismiss="modal">&times;</button>--%>
                </div>
                <div class="modal-body-cancel">
                  <%=rb1.getString("VAR_CANCEL_TEXT_1")%> <br>
                  <%=rb1.getString("VAR_CANCEL_TEXT_2")%>
                </div>
                <div class="modal-footer-cancel">
                  <div>
                    <button type="button" class="btn cancel-modal-button" onclick="closeCancel()"> <%=rb1.getString("VAR_CANCEL_CLOSE")%></button>
                  </div>
                  <div>
                      <button type="button" class="btn cancel-modal-button" onclick="cancelTransaction()"><%=rb1.getString("VAR_CANCEL_DEPOSIT")%></button>
                    <%--<%if(gateWayName.equalsIgnoreCase("Appletree")){%>
                      <button type="button" class="btn cancel-modal-button" onclick="cancelTransaction()">Cancel Purchase</button>
                    <%}else{%>
                      <button type="button" class="btn cancel-modal-button" onclick="cancelTransaction()"><%=rb1.getString("VAR_CANCEL_DEPOSIT")%></button>
                    <%}%>--%>
                  </div>
                </div>
              </div>
            </div>
          </div>
          <%--end of cancel button popup--%>

        </div>

        <!-- merchant details -->
        <%
          String supportSection   = getValue(standardKitValidatorVO.getMerchantDetailsVO().getSupportSection());
          String supportNoNeeded  = getValue(standardKitValidatorVO.getMerchantDetailsVO().getSupportNoNeeded());
          String address          = getValue(standardKitValidatorVO.getMerchantDetailsVO().getAddress())+" "+getValue(standardKitValidatorVO.getMerchantDetailsVO().getState())+" "+
                  getValue(standardKitValidatorVO.getMerchantDetailsVO().getCountry())+" "+getValue(standardKitValidatorVO.getMerchantDetailsVO().getZip());
          String telNo            = "";
          String email            = getValue(standardKitValidatorVO.getMerchantDetailsVO().getContact_emails());
          String billigDesc       = "";
          String billingStatement = "";
          if(functions.isValueNull(accountId))
          {
            billigDesc        = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
            billingStatement  = "The Charge Descriptor on your Bank statement will appear as <font style='font-weight: bold;'><br>"+billigDesc+"</font>";
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
            <i class="fas fa-envelope"></i>   <%=email%>
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

        <%--<%
          if(!partnerLogoFlag.equalsIgnoreCase("N") || !sisaLogoFlag.equalsIgnoreCase("N") || !powerBy.equalsIgnoreCase("N") )
          {
        %>--%>
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
        <!-- footer  -->
        <%--<%
          }
        %>--%>

      </div>

    </div>
  </div>
</div>
<div class="modal fade" style="display: none;" id="CancelModalCreditcard">
  <div class="modal-dialog-cancel modal-sm-cancel modal-dialog-centered" style="max-width: 385px !important; top:2px;">
    <div class="modal-content-cancel">
      <div class="container d-flex justify-content-center align-items-center" >

        <div class="card text-center" style="width: 370px;">

          <div class="header p-3 w-100 h-auto">
            <h5 class="verification m-auto font-weight-bold">OTP VERIFICATION</h5>
          </div>

          <p id="mainTimer"  style="margin-top: 10px;margin-left: 200px;white-space: nowrap;">03:00</p>
          <div>
            <span  id="messageOTP"  style="color:darkblue;font-size: 15px;"></span>
            <input type="hidden" id="SuccessOTP" value="">
          </div>
          <%
            if(functions.isValueNull(personalDetailDisplay))
            {
              if(personalDetailDisplay.equals("Y"))
              {
          %>
          <!-- OTP Number -->
          <div id="mobileIdVerify">
          <label  class="mt-2 font-weight-bold" style="font-family: system-ui; ">Verify Mobile OTP:</label>
          <div id="otpM" class="input-container d-flex flex-row justify-content-center">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)" id="fisrtM" class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"  id="secondM"  class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"   id="thirdM" class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"  id="fourthM"  class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"  id="fifthM"  class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"   id="sixthM" class="m-1 text-center form-control rounded" maxlength="1">
          </div>
          <p   class="mt-2" style="font-family: sans-serif;">Please enter the OTP received via Mobile at registered Mobile number. <b><span style="font-weight: 600;" id="copyOTPPhoneNumber"></span></b></p>
          </div>
          <!-- OTP Email -->
          <div id="emailIdVerify">
          <label class="mt-3 font-weight-bold" style="font-family: system-ui;">Verify Email OTP:</label>
          <div id="otpE" class="input-container d-flex flex-row justify-content-center">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"  id="fisrtE"  class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"  id="secondE"  class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"  id="thirdE" class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"  id="fourthE" class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)" id="fifthE"  class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)" id="sixthE"  class="m-1 text-center form-control rounded" maxlength="1">
          </div>
          <p class="mt-2" style="font-family: sans-serif;">Please enter the OTP received via Email at registered email id. <b><span style="font-weight: 600;" id="copyOTPEmail"></span></b></p>
</div>
          <%
          }
          else if(personalDetailDisplay.equals("M"))
          {
          %>
          <div id="mobileIdVerify">
          <label  class="mt-2 font-weight-bold" style="font-family: system-ui; ">Verify Mobile OTP:</label>
          <div id="otpM" class="input-container d-flex flex-row justify-content-center">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)" id="fisrtM" class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"  id="secondM"  class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"   id="thirdM" class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"  id="fourthM"  class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"  id="fifthM"  class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"   id="sixthM" class="m-1 text-center form-control rounded" maxlength="1">
          </div>
          <p class="mt-2" style="font-family: sans-serif;">Please enter the OTP received via Mobile at registered Mobile number. <b><span style="font-weight: 600;" id="copyOTPPhoneNumber"></span></b></p>
         </div>
          <%
          }
          else if(personalDetailDisplay.equals("E"))
          {
          %>
          <div id="emailIdVerify">
          <label class="mt-3 font-weight-bold" style="font-family: system-ui;">Verify Email OTP:</label>
          <div id="otpE" class="input-container d-flex flex-row justify-content-center">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"  id="fisrtE"  class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"  id="secondE"  class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"  id="thirdE" class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)"  id="fourthE" class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)" id="fifthE"  class="m-1 text-center form-control rounded" maxlength="1">
            <input type="text" oninput="inputInsideOtpInput(this)"  onkeypress="return isNumberKey(event)" id="sixthE"  class="m-1 text-center form-control rounded" maxlength="1">
          </div>
          <p class="mt-2" style="font-family: sans-serif;">Please enter the OTP received via Email at registered email id. <b><span style="font-weight: 600;" id="copyOTPEmail"></span></b></p>
          </div>
            <%
          }
            }
          %>
          <div>
            <small>
              didn't get the otp?
              <p id="regenerateOTPText" style="pointer-events: none;color: blue;font-size: 16px;cursor: pointer;" onclick="regenerateOTP('<%=personalDetailDisplay%>');" class="text-decoration-none">Resend</p>
            </small>
          </div>

          <!-- <h6 class="m-4 font-weight-bold" style="font-family: sans-serif;">Please enter the OTP received via Email at registered email id.</h6> -->

          <div class="mt-3 mb-5">
            <button class="btn btn-success px-4 verify-btn" onclick="verifyOTP('<%=personalDetailDisplay%>')">verify</button>
            <button onclick="closeCancelEmailModal()" id="cancleOtpPopup" class="btn btn-success px-4 verify-btn">Cancel</button>
          </div>

        </div>

      </div>
    </div>
  </div>
</div>
<!-- modal ends -->



<form action="/transaction/sessionout.jsp" method="post" name="timeoutform" id="sessionout">
  <input type="hidden" name="ctoken" value="<%=ctoken%>">
  <input type="hidden" name="redirectUrl" value="<%=genericTransDetailsVO.getRedirectUrl()%>">
  <input type="hidden" name="notificationUrl" value="<%=genericTransDetailsVO.getNotificationUrl()%>">
  <input type="hidden" name="trackingid" value="<%=standardKitValidatorVO.getTrackingid()%>" />
  <input type="hidden" name="desc" value="<%=standardKitValidatorVO.getTransDetailsVO().getOrderId()%>" />
  <input type="hidden" name="amount" value="<%=standardKitValidatorVO.getTransDetailsVO().getAmount()%>" />
  <input type="hidden" name="status" value="N" />
  <input type="hidden" name="clkey" value="<%=merchantDetailsVO.getKey()%>">
  <input type="hidden" name="language" value="<%=standardKitValidatorVO.getAddressDetailsVO().getLanguage()%>">
  <input type="hidden" name="currency" value="<%=standardKitValidatorVO.getTransDetailsVO().getCurrency()%>">
  <input type="hidden" name="marketPlaceFlag" value="<%=standardKitValidatorVO.getMerchantDetailsVO().getMarketPlace()%>">
  <%
    if(!"N".equalsIgnoreCase(standardKitValidatorVO.getMerchantDetailsVO().getMarketPlace()) && standardKitValidatorVO.getMarketPlaceVOList()!=null)
    {
      MarketPlaceVO marketPlaceVO=new MarketPlaceVO();
      for(int i=0;i<standardKitValidatorVO.getMarketPlaceVOList().size();i++)
      {
        marketPlaceVO=standardKitValidatorVO.getMarketPlaceVOList().get(i);
  %>
  <input type="hidden" id="marketPlaceTrackingid[]" name="marketPlaceTrackingid[]" value="<%=marketPlaceVO.getTrackingid()%>">
  <%
      }
    }
  %>
</form>
<%if("split".equalsIgnoreCase(standardKitValidatorVO.getMerchantDetailsVO().getMarketPlace()))
{%>
<form action="/transaction/SingleCallCheckoutSplit" method="post" id="cancelForm" name="cancelForm">
  <input type="hidden" name="ctoken" value="<%=ctoken%>" />
  <input type="hidden" name="action" value="cancel" />
  <input type="hidden" name="toid" value="<%=merchantDetailsVO.getMemberId()%>" />
  <input type="hidden" name="trackingid" value="<%=standardKitValidatorVO.getTrackingid()%>" />
  <input type="hidden" id="clkey" name="clkey" value="<%=merchantDetailsVO.getKey()%>">
  <input type="hidden" id="paymentMode" name="paymentMode" value="<%=request.getParameter("paymentMode")%>">
  <input type="hidden" id="partnerId" name="partnerId" value="<%=merchantDetailsVO.getPartnerId()%>">
  <input type="hidden" id="marketPlace" name="marketPlace" value="<%=merchantDetailsVO.getMarketPlace()%>">
</form>
<%}
else{%>
<form action="/transaction/SingleCallCheckout" method="post" id="cancelForm" name="cancelForm">
  <input type="hidden" name="ctoken" value="<%=ctoken%>" />
  <input type="hidden" name="action" value="cancel" />
  <input type="hidden" name="toid" value="<%=merchantDetailsVO.getMemberId()%>" />
  <input type="hidden" name="trackingid" value="<%=standardKitValidatorVO.getTrackingid()%>" />
  <input type="hidden" id="clkey" name="clkey" value="<%=merchantDetailsVO.getKey()%>">
  <input type="hidden" id="paymentMode" name="paymentMode" value="<%=request.getParameter("paymentMode")%>">
  <input type="hidden" id="partnerId" name="partnerId" value="<%=merchantDetailsVO.getPartnerId()%>">
  <input type="hidden" id="marketPlace" name="marketPlace" value="<%=merchantDetailsVO.getMarketPlace()%>">
</form>
<%}%>


<script>
  console.log("window.requestIdleCallback = ",window.requestIdleCallback);
  var fingerPrintElem = document.getElementsByName('fingerprints');
  var options = {fonts: {extendedJsFonts: true}, excludes: {userAgent: false}, preprocessor: function(key, value) {
    if (key == "userAgent") {
      var parser = new UAParser(value); // https://github.com/faisalman/ua-parser-js
      var userAgentMinusVersion = parser.getOS().name + ' ' + parser.getBrowser().name;
      console.log("parsed value = ",userAgentMinusVersion);
      return userAgentMinusVersion
    }
    return value
  }
  };
  if (window.requestIdleCallback) {
    requestIdleCallback(function () {
      Fingerprint2.get(options,function (components) {
        console.log("if =",components) // an array of components: {key: ..., value: ...}
//        document.getElementById("fingerprints").value = JSON.stringify(components);
        var values = components.map(function (component) { return component.value })
        var murmur = Fingerprint2.x64hash128(values.join(''), 31)
        console.log("murur = ",murmur);
        fingerPrintElem.forEach((ele) => {
          ele.value = JSON.stringify(components);
      });
    })
  })
  } else {
    setTimeout(function () {
      Fingerprint2.get(options,function (components) {
        console.log("else =",components) // an array of components: {key: ..., value: ...}
//        document.getElementById("fingerprints").value = JSON.stringify(components);
        var values = components.map(function (component) { return component.value });
        var murmur = Fingerprint2.x64hash128(values.join(''), 31);
        console.log("murur = ",murmur);
        fingerPrintElem.forEach((ele) => {
          ele.value = JSON.stringify(components);
      });
    })
  }, 500)
  }
</script>
<jsp:include page="payneticstlsc.jsp"></jsp:include>

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