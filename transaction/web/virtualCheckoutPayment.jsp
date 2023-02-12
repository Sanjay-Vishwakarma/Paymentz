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

<%--
  Created by IntelliJ IDEA.
  User: Rihen
  Date: 9/16/2019
  Time: 1:54 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%!
  public String getPaymentTypeIcon(String paymentId)
  {
    String iClassIcon = "";
    if("CreditCards".equals(paymentId))
      iClassIcon = "far fa-credit-card";
    if("VirtualAccount".equals(paymentId))
      iClassIcon = "fas fa-desktop";
    if("Wallet".equals(paymentId) || "Wallets".equals(paymentId))
      iClassIcon = "far fa-money-bill-alt";
    if("DebitCards".equals(paymentId))
      iClassIcon = "far fa-credit-card";
    if("NetBanking".equals(paymentId) || "Net-Banking".equals(paymentId))
      iClassIcon = "fas fa-university";
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

    return iClassIcon;
  }
%>

<%
  Functions functions=new Functions();
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");

  GenericTransDetailsVO genericTransDetailsVO = standardKitValidatorVO.getTransDetailsVO();
  GenericAddressDetailsVO genericAddressDetailsVO = standardKitValidatorVO.getAddressDetailsVO();
  MerchantDetailsVO merchantDetailsVO= standardKitValidatorVO.getMerchantDetailsVO();
  RecurringBillingVO recurringBillingVO= standardKitValidatorVO.getRecurringBillingVO();
  String personalDetailValidation = merchantDetailsVO.getPersonalInfoValidation();
  String personalDetailDisplay = merchantDetailsVO.getPersonalInfoDisplay();

  String merchantSiteName = getValue(standardKitValidatorVO.getMerchantDetailsVO().getMerchantDomain());
  Logger log = new Logger("virtualCheckoutPayment.jsp");
  HashMap paymentMap = standardKitValidatorVO.getMapOfPaymentCardType();
  HashMap terminalMap = standardKitValidatorVO.getTerminalMap();
  HashMap currencyListMap = standardKitValidatorVO.getCurrencyListMap();

  LinkedHashMap<String,TerminalVO> terminalMapLimitRouting = standardKitValidatorVO.getTerminalMapLimitRouting();
  List<MarketPlaceVO> mpDetailsList=standardKitValidatorVO.getMarketPlaceVOList();
  String ctoken = "";
  ctoken = (String) session.getAttribute("ctoken");
  String reqPaymode = standardKitValidatorVO.getPaymentType();
  String reqCardtype = standardKitValidatorVO.getCardType();
%>


<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title></title>
  <base href="/">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="/merchant/transactionCSS/css/raleway-font.css" type="text/css">
  <link rel="stylesheet" href="/merchant/transactionCSS/css/orbitron-font.css" type="text/css">
  <link rel="stylesheet" href="/merchant/transactionCSS/css/montserrat-font.css" type="text/css">
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.1.0/css/all.css" integrity="sha384-lKuwvrZot6UHsBSfcMvOkWwlCMgc0TaWr+30HWe3a4ltaBwTZhyTEggF5tJv8tbt" crossorigin="anonymous">
  <link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap/css/bootstrap.min.css">
  <link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap-float-label.min.css"/>
  <link rel="stylesheet" href="/merchant/transactionCSS/css/styles.css"/>
  <link rel="stylesheet" href="/merchant/transactionCSS/css/step-form.css"/>
  <link rel="stylesheet" href="/merchant/transactionCSS/css/success.css"/>
  <link rel="stylesheet" href="/merchant/transactionCSS/css/autocomplete.css"/>

  <style>
    /* for ipad */
    @media only screen
    and (min-device-width : 768px)
    and (max-device-width : 1024px){
      .mainDiv{
        width:100%;
      }
      .modal-dialog {
        /*max-width: 100%;*/
        margin: 1.75rem auto;
      }
    }

    @media (min-width: 1200px) {
      .mainDiv{
        /*width: 345px;*/
        width: 400px;
      }
      .modal-dialog {
        max-width: auto;
      }
    }

    @media (max-width:576px) {
      .mainDiv{
        width: 100%;
      }
      .modal-dialog {
        max-width: 100%;
        margin: 0px;
      }
    }

    @media (min-width: 576px) {
      .modal-sm {
        /*max-width: 346px;*/
        max-width: 400px;
      }
      .modal-sm-cancel {
        max-width: 250px;
      }
    }

    .background {
    <%=session.getAttribute("bodybgcolor")!=null?"background:"+session.getAttribute("bodybgcolor").toString():""%>;
    }

    /* header */
    .header{
    <%=session.getAttribute("panelheading_color")!=null?"background-color:"+session.getAttribute("panelheading_color").toString()+"!important":""%>;
    <%=session.getAttribute("bodypanelfont_color")!=null?"color:"+session.getAttribute("bodypanelfont_color").toString()+"!important":""%>;
    }

    .close {
    <%=session.getAttribute("bodypanelfont_color")!=null?"color:"+session.getAttribute("bodypanelfont_color").toString()+"!important":""%>;
    }

    .logo{                /* background color of logo  */
      background:transparent !important;
      box-shadow: none !important;
    }
    /* end of header */

    /* body */
    .top-bar{               /* navigation bar background and font color */
    <%=session.getAttribute("panelbody_color")!=null?"background-color:"+session.getAttribute("panelbody_color").toString()+"!important":""%>;
    <%=session.getAttribute("navigation_font_color")!=null?"color:"+session.getAttribute("navigation_font_color").toString()+"!important":""%>;
    }

    .prev-btn{                /* form previous button on navigation bar */
    <%=session.getAttribute("navigation_font_color")!=null?"color:"+session.getAttribute("navigation_font_color").toString()+"!important":""%>;
    }

    .footer-tab{             /* home page background color */
    <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
    }

    .options{               /* body background color */
    <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
    }

    .tab-icon{               /* home page icon color */
    <%=session.getAttribute("icon_color")!=null?"color:"+session.getAttribute("icon_color").toString()+"!important":""%>;
    }

    .label-style , .bank-label {            /* payment type label font color*/
    <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
    }

    .tabs-li-wallet{       /* payment type options border color */
    <%=session.getAttribute("panelheading_color")!=null?"border-right: 1px solid "+session.getAttribute("panelbody_color").toString()+"!important":""%>;
    <%=session.getAttribute("panelheading_color")!=null?"border-bottom: 1px solid "+session.getAttribute("panelbody_color").toString()+"!important":""%>;
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
      top: -0.9em;
    }

    .form-label , .terms-checkbox{            /* for labels as well as terms & condition*/
    <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
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
    }

    .merchant-details{
    <%=session.getAttribute("textbox_color")!=null?"background-color:"+session.getAttribute("textbox_color").toString()+"!important":""%>;
    <%=session.getAttribute("headpanelfont_color")!=null?"color:"+session.getAttribute("headpanelfont_color").toString()+"!important":""%>;
      border: none !important;
      margin: 0px;
      padding: 4px;
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
    }
    /* end of body */

    /* footer */
    .modal-footer {
    <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
    }
    /* end of footer */
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
    if(GatewayAccountService.getPaymentTypes(pId.toString())!= null){
      String pt = GatewayAccountService.getPaymentTypes(pId.toString());
      if(pt.equals("WalletIndia"))
        pt = "Wallets";
      if(pt.equals("NetBankingIndia"))
        pt = "Net-Banking";
      payTypeLabel = rb1.getString(pt);
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

    if(paymentType.equals("WalletIndia"))
      paymentType = "Wallets";
    if(paymentType.equals("NetBankingIndia"))
      paymentType = "Net-Banking";

    if(rb1.containsKey(paymentType))
    {
      paymentTypeDisplayLabel = rb1.getString(paymentType);
    }

    String cardType = GatewayAccountService.getCardType(reqCardtype.toString());
    String cardTypeDisplayLabel ="";
    log.debug("card value---"+cardType);
    if(rb1.containsKey(cardType))
    {
      cardTypeDisplayLabel = rb1.getString(cardType);
    }
%>

<body style="font-family: Montserrat;" class="background" onload="loadFromBody('<%=paymentTypeDisplayLabel%>','<%=paymentType%>','<%=cardType%>','<%=cardTypeDisplayLabel%>','<%=error.toString()%>','<%=keys%>','<%=personalDetailDisplay%>','<%=personalDetailValidation%>','<%=payType%>','<%=payTypeLabel%>',<%=paymentMap.size()%>)">
  <%
  }
  else if(functions.isValueNull(reqPaymode))
  {
    String paymentType = GatewayAccountService.getPaymentTypes(reqPaymode.toString());
    log.debug("if body load---"+paymentType);
    String paymentTypeDisplayLabel ="";

    if(paymentType.equals("WalletIndia"))
      paymentType = "Wallets";
    if(paymentType.equals("NetBankingIndia"))
      paymentType = "Net-Banking";

    if(rb1.containsKey(paymentType))
    {
      paymentTypeDisplayLabel = rb1.getString(paymentType);
    }
  %>
<body style="font-family: Montserrat;" class="background" onload="loadFromBody('<%=paymentTypeDisplayLabel%>','<%=paymentType%>',null,null,'<%=error%>',null,'<%=personalDetailDisplay%>','<%=personalDetailValidation%>','<%=payType%>','<%=payTypeLabel%>',<%=paymentMap.size()%>)">
  <%
  }
  else
  {
%>
<body style="font-family: Montserrat;" class="background" onload="loadFromBody(null ,null ,null,null,'<%=error%>','<%=keys%>','<%=personalDetailDisplay%>','<%=personalDetailValidation%>','<%=payType%>','<%=payTypeLabel%>',<%=paymentMap.size()%>)">
<%
    log.debug("else body load---");
  }
%>

<noscript class="noscript">
  <h2 id="div100">
    Please enable javascript in your browser ....
  </h2>
</noscript>
<script type = "text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/anime.min.js"></script> <!-- loader animation-->
<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.min.js"></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/bootstrap/js/bootstrap.min.js" ></script> <%--bootstrap 4.1.1 --%>
<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.cardcheck.js"></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.cardcheck.min.js"></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/autocomplete.js "></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/countrylist.js"></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/vcheckout.js"></script>

<%

//  String amount = getValue(genericTransDetailsVO.getAmount());

  String merchantLogoFlag = getValue(standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo());
  String consentFlag = getValue(standardKitValidatorVO.getMerchantDetailsVO().getConsentFlag());
  String partnerLogoFlag = getValue(standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag());

  String accountId = "";

  if (merchantDetailsVO != null && functions.isValueNull(merchantDetailsVO.getAccountId()))
    accountId = merchantDetailsVO.getAccountId();

  session.setAttribute("paymentMap",paymentMap);
  session.setAttribute("terminalmap",terminalMap);
  session.setAttribute("terminalMapLimitRouting",terminalMapLimitRouting);
  session.setAttribute("mpDetailsList",mpDetailsList);
  session.setAttribute("currencyList",currencyListMap);

  Calendar cal=Calendar.getInstance();
  cal.setTimeInMillis(session.getLastAccessedTime());

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
  InvalidAddress="<%=rb1.getString("VAR_INVALID_ADDRESS")%>";
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
  InvalidCard="<%=rb1.getString("VAR_INVALID_CARD")%>";
  InvalidCVV="<%=rb1.getString("VAR_INVALID_CVV")%>";
  InvalidEMI="<%=rb1.getString("VAR_INVLAID_EMI")%>";
  InvalidCardHolderName="<%=rb1.getString("VAR_INVLAID_CARDHOLDERNAME")%>";
  Months="<%=rb1.getString("VAR_MONTHS")%>";
  SelectMonths="<%=rb1.getString("VAR_SELECT_MONTHS")%>";

</script>



<input type="hidden" id="memberId" value="<%=standardKitValidatorVO.getMerchantDetailsVO().getMemberId()%>" />
<input type="hidden" id="binRouting" value="<%=standardKitValidatorVO.getMerchantDetailsVO().getBinRouting()%>"/>
<input type="hidden" id="emiSupport" value="<%=standardKitValidatorVO.getMerchantDetailsVO().getEmiSupport()%>"/>
<input type="hidden" id="consentFlag" value="<%=consentFlag%>" />

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
              padForAmt = "padding: 20px 0px;";
          %>
          <div class="logo">
            <img class="images-style1" style="width: 100%;" src="/images/merchant/<%=session.getAttribute("merchantLogoName")%>">
          </div>
          <%
          }
          else if(!partnerLogoFlag.equals(null) && partnerLogoFlag.equalsIgnoreCase("Y"))
          {
            supportName = standardKitValidatorVO.getMerchantDetailsVO().getPartnerName();
            padForAmt = "padding: 20px 0px;";
          %>
          <div class="logo">
            <img class="images-style1" style="width: 100%;" src="/images/merchant/<%=standardKitValidatorVO.getMerchantDetailsVO().getLogoName()%>">
          </div>
          <%
            }
          %>
          <div class="name" style="text-align: right;">
            <%
              String visibility = "block";
//              String amountStyle= "";
              if(merchantDetailsVO.getMerchantOrderDetailsDisplay().equalsIgnoreCase("N"))
              {
                visibility  = "none";
//                amountStyle = "font-size: 25px;"+padForAmt;
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
            <%--<div class="client-amount" style="<%=amountStyle%>"><%=currencySymbol%> <%=tmpl_amount%></div>--%>
          </div>
          <button type="button" id="closebtn" class="close hide"  data-dismiss="modal" aria-label="Close" onclick="cancelTransaction()">
            <span aria-hidden="true">&times;</span>
          </button>
          <div class="timeout"><%=varTIMEOUT%> = <span id="timer"></span></div>

        </div>

        <div class="modal-body">
          <div class="top-bar">
            <div class="top-left" id="topLeft" >
              <span class="" id="backArrow" style="float: left;width: 20px;cursor: pointer" onclick="back()" ><i class="fas fa-angle-left"></i></span>
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
                  %>
                  <li>
                    <%=rbError.containsKey(singleError)?rbError.getString(singleError).replaceAll("\\*",supportName):singleError%>
                  </li>
                  <%
                    }
                  }
                  else
                  {
                  %>
                  <li>
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

          <div id="options">
            <div class="footer-tab">
              <ul class="nav nav-tabs" id="myTab">
                <%
                  for(Object paymentId : paymentMap.keySet())
                  {
                    String paymentType = GatewayAccountService.getPaymentTypes(paymentId.toString());

                    if(paymentType.equals("WalletIndia"))
                      paymentType = "Wallets";
                    if(paymentType.equals("NetBankingIndia"))
                      paymentType = "Net-Banking";
                %>
                <li class="tabs-li" onclick="PaymentOptionHideShow('<%=rb1.getString(paymentType)%>', '<%=paymentType%>')">
                  <a href="#<%=paymentType%>"  data-toggle="tab" title="<%=paymentType%>" aria-expanded="false">
                    <span class="round-tabs two tab-icon " >
                      <%
                        if(!paymentType.equalsIgnoreCase("ACH") && !paymentType.equalsIgnoreCase("CHK") && !paymentType.equalsIgnoreCase("UPI")
                                && !paymentType.equalsIgnoreCase("ROMCARD") && !paymentType.equalsIgnoreCase("TOJIKA") && !paymentType.equalsIgnoreCase("VOGUEPAY"))
                        {
                      %>
                        <i class="<%=getPaymentTypeIcon(paymentType)%>"></i>
                      <%
                      }
                      else{
                      %>
                      <img src="images/merchant/<%=paymentType%>.png" height="49px">
                      <%
                        }
                      %>
                    </span>
                    <div class="label-style"><%=rb1.getString(paymentType)%></div>
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

                String fileName = "";
                String val ="";

                if(paymentMode.equals("DC")){
                  fileName = "virtualCreditCards.jsp";
                  val = "dc";
                }
                else if(paymentMode.equals("DCI")){
                  fileName = "CreditCardsIndia.jsp";
                  val = "dc";
                }
                else{
                  fileName = "virtual"+paymentType+".jsp";
                  val = "";
                }

                log.debug("file name---"+fileName);
            %>
            <div class="tab-pane options" id="<%=displayType%>">
              <jsp:include page="<%=fileName%>">
                <jsp:param name="id" value="<%=val%>"/>
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
                <span><%=varPAY%></span>
                <%--<span><%=varPAY%> <%=currencySymbol%> <%=tmpl_amount%></span>--%>
              </button>
            </div>
          </div>

          <%--Cancel button popup--%>
          <div class="modal fade" id="CancelModal">
            <div class="modal-dialog-cancel modal-sm-cancel modal-dialog-centered">
              <div class="modal-content-cancel">
                <div class="modal-header-cancel">
                  <div class="modal-title-cancel"><%=rb1.getString("VAR_CANCEL_TITLE")%> </div>
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
                  </div>
                </div>
              </div>
            </div>
          </div>
          <%--end of cancel button popup--%>

        </div>

        <!-- merchant details -->
        <%
          String supportSection = getValue(standardKitValidatorVO.getMerchantDetailsVO().getSupportSection());
          String address = getValue(standardKitValidatorVO.getMerchantDetailsVO().getAddress())+" "+getValue(standardKitValidatorVO.getMerchantDetailsVO().getState())+" "+
                  getValue(standardKitValidatorVO.getMerchantDetailsVO().getCountry())+" "+getValue(standardKitValidatorVO.getMerchantDetailsVO().getZip());
          String telNo = getValue(standardKitValidatorVO.getMerchantDetailsVO().getTelNo());
          String email = getValue(standardKitValidatorVO.getMerchantDetailsVO().getContact_emails());
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
            <i class="fas fa-phone"></i>      <%=telNo%>       &nbsp;&nbsp;&nbsp;&nbsp;
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

        <%
          if(!partnerLogoFlag.equalsIgnoreCase("N") || !sisaLogoFlag.equalsIgnoreCase("N") || !powerBy.equalsIgnoreCase("N") )
          {
        %>
        <div class="modal-footer" style="padding: 7px;">
          <%
            if(functions.isValueNull(partnerLogoFlag) && partnerLogoFlag.equalsIgnoreCase("Y"))
            {
          %>

          <div class="footer-logo" style="text-align: left;">
            <img  class="logo-style" src="/images/merchant/<%=logoName%>">
          </div>
          <%
            }
            else
            {
              align="text-align: left";
            }
            if(functions.isValueNull(sisaLogoFlag) && sisaLogoFlag.equalsIgnoreCase("Y"))
            {
          %>
          <div class="footer-logo" style="<%=align%>">
            <img  class="logo-style" src="/images/merchant/PCI.png">
          </div>
          <%
            }
            if(functions.isValueNull(powerBy) && powerBy.equalsIgnoreCase("Y"))
            {
          %>
          <div class="footer-logo" style="text-align: right;">
            <img  class="logo-style"  src="/images/merchant/poweredBy_logo.png">
          </div>
          <%
          }
          else if(functions.isValueNull(powerBy) && securityLogoFlag.equalsIgnoreCase("Y"))
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
        <%
          }
        %>
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
  <%--<input type="hidden" name="amount" value="<%=standardKitValidatorVO.getTransDetailsVO().getAmount()%>" />--%>
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
<form action="/transaction/SingleCallVirtualCheckout" method="post" id="cancelForm" name="cancelForm">
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


<%
  if(genericTransDetailsVO!=null && functions.isValueNull(genericTransDetailsVO.getTotype()) && genericTransDetailsVO.getTotype().equalsIgnoreCase("paynetics"))
  {
%>
<jsp:include page="payneticstlsc.jsp"></jsp:include>
<%
  }
%>

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