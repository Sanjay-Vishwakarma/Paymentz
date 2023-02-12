<%@ page import="com.directi.pg.*" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericTransDetailsVO" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.payment.cupUPI.UnionPayInternationalRequestVO" %>
<%@ page import="com.payment.exceptionHandler.errorcode.ErrorCodeUtils" %>

<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>

<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="com.payment.Ecospend.EcospendRequestVo" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%--
Created by IntelliJ IDEA.
User: Rihen
Date: 9/8/2021
Time: 10:58 PM
To change this template use File | Settings | File Templates.
--%>

<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>



<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <title></title>
  <base href="/">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <link rel="stylesheet" href="/merchant/transactionCSS/css/raleway-font.css" type="text/css">
  <link rel="stylesheet" href="/merchant/transactionCSS/css/orbitron-font.css" type="text/css">
  <%--<link rel="stylesheet" href="/merchant/transactionCSS/css/montserrat-font.css" type="text/css">--%>
  <link href="https://fonts.googleapis.com/css2?family=Montserrat:ital,wght@0,100;0,200;0,300;0,400;0,500;0,600;0,700;0,800;0,900;1,100;1,200;1,300;1,400;1,500;1,600;1,700;1,800;1,900&display=swap" rel="stylesheet">
  <link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.1.0/css/all.css" integrity="sha384-lKuwvrZot6UHsBSfcMvOkWwlCMgc0TaWr+30HWe3a4ltaBwTZhyTEggF5tJv8tbt" crossorigin="anonymous">
  <link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap-float-label.min.css"/>
  <link rel="stylesheet" href="/merchant/transactionCSS/css/styles.css"/>
  <link rel="stylesheet" href="/merchant/transactionCSS/css/step-form.css"/>


  <style>

    @media only screen
    and (min-device-width : 768px)
    and (max-device-width : 1024px){
      .mainDiv{
        width:100%;
      }
      .modal-dialog {
        margin: 1.75rem auto;
      }
    }

    @media (min-width: 1200px) {
      .mainDiv{
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
        margin: 0;
      }
    }

    @media (min-width: 576px) {
      .modal-sm {
        max-width: 400px;
      }
    }

    .background{
    <%=session.getAttribute("bodybgcolor")!=null?"background:"+session.getAttribute("bodybgcolor").toString():""%>;
    }

    /* header */
    .header{
    <%=session.getAttribute("panelheading_color")!=null?"background-color:"+session.getAttribute("panelheading_color").toString()+"!important":""%>;
    <%=session.getAttribute("bodypanelfont_color")!=null?"color:"+session.getAttribute("bodypanelfont_color").toString()+"!important":""%>;
    }

    .close{
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

    .label-style{            /* payment type label font color*/
    <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
    }

    .tabs-li-wallet{       /* payment type options border color */
    <%=session.getAttribute("panelheading_color")!=null?"border-right: 1px solid "+session.getAttribute("panelbody_color").toString()+"!important":""%>;
    <%=session.getAttribute("panelheading_color")!=null?"border-bottom: 1px solid "+session.getAttribute("panelbody_color").toString()+"!important":""%>;
    }

    .form-header{          /* form header label color(Personal,Address,Card)  */
    <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
      opacity: .8;
    }

    .form-control , .form-control:focus {        /* input control focus disabled background and font color */
    <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
    <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
    }

    .has-float-label label, .has-float-label>span{            /* input control label font color */
    <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
    }

    .form-label{            /* for labels as well as terms & condition*/
    <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
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
    .loader{                    /* loader color*/
    <%=session.getAttribute("textbox_color")!=null?" color:"+session.getAttribute("textbox_color").toString()+"!important":""%>;
      opacity: .8;
    }

    .check_mark , .sa-icon.sa-success::before, .sa-icon.sa-success::after , .sa-fix{       /* success ( animation background color ) */
    <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
    }
    /* end of loader */

    .confirmation-div{
    <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
    <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
    }

    .confirm-box{
    <%=session.getAttribute("bodyfgcolor")!=null?"border-bottom:2px solid "+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
    }
    /* end of body */

    /* footer */
    .modal-footer{
    <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
    }
    /* end of footer */

    .hide{
      display: none;
    }

    .displayLabel{
      border: none !important;
      font-weight: 600 !important;
      pointer-events: none !important;
    }

    .form-control:disabled, .form-control[readonly] {
      background-color: #e9ecef !important;
      opacity: 1;
      color: black !important;
      cursor: not-allowed;
      pointer-events: none;
    }

    .input-control1{
      margin-top: 5px !important;
    }

    .ddcommon .ddChild li img
    {
      width: 70px;
      height: 23px;
      border-right: solid 1px black !important;
    }
    .dd .ddTitle .ddTitleText img {
      width: 70px;
      height: 23px;
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
  </style>

</head>
<body style="font-family: Montserrat;" class="background">

<%
  Functions functions=new Functions();
  CommonValidatorVO commonValidatorVO= (CommonValidatorVO) request.getAttribute("transDetails");
  TreeMap<String , String> banklist = (TreeMap)request.getAttribute("BankidList");

  EcospendRequestVo ecospendRequestVo= (EcospendRequestVo) request.getAttribute("EcospendRequestVo");


  Logger log = new Logger("EcospendBankList.jsp");
  ResourceBundle rb = null;
  rb = LoadProperties.getProperty("com.directi.pg.CurrencySymbol");

  ResourceBundle rb1 = null;
  String lang = "";
  String multiLanguage = "";

  if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLanguage()))
  {
    lang = commonValidatorVO.getAddressDetailsVO().getLanguage().toLowerCase();
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

  String varCONTINUE = rb1.getString("VAR_CONTINUE");


  ErrorCodeVO errorCodeVO = new ErrorCodeVO();
  ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
  String trackingid= (String) request.getAttribute("trackingid");
  log.debug("Card Number In jsp 1--------------" + commonValidatorVO.getCardDetailsVO().getCardNum());
  String AccessAuthtoken=ecospendRequestVo.getAccessTken();
  String accountNumber=ecospendRequestVo.getCreditorID();
  String processerName=ecospendRequestVo.getCreditorName();
  String sortCode=ecospendRequestVo.getCreditorBic();
  String phoneNumber=commonValidatorVO.getAddressDetailsVO().getPhone();
  String orderDesc=ecospendRequestVo.getTransDetailsVO().getOrderDesc();
  String email = "";
  String country = "";
  String phoneNumberCC=commonValidatorVO.getAddressDetailsVO().getTelnocc();
  String amount = commonValidatorVO.getDisplayAmount();
  String currency = commonValidatorVO.getTransDetailsVO().getCurrency();
  String accountId= commonValidatorVO.getMerchantDetailsVO().getAccountId();
  String emailsent=commonValidatorVO.getMerchantDetailsVO().getEmailSent();
  String autoRedirect=commonValidatorVO.getMerchantDetailsVO().getAutoRedirect();
  String isService=commonValidatorVO.getMerchantDetailsVO().getIsService();

  String paymentDate = "";
  String paymentPeritod = "";
  String noOfPayment = "";
  String firstPaymentAmount = "";
  String lastPaymentAmount = "";
  String formattedDate = "";
  if(functions.isValueNull(commonValidatorVO.getReserveField2VO().getReservefield1()))
  {
    paymentDate = commonValidatorVO.getReserveField2VO().getReservefield1();
    Date d = new Date(paymentDate);
    SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd");
    formattedDate = dateformatter.format(d);

  }
  if (functions.isValueNull(commonValidatorVO.getReserveField2VO().getReservefield2()))
    paymentPeritod=commonValidatorVO.getReserveField2VO().getReservefield2();
  if (functions.isValueNull(commonValidatorVO.getReserveField2VO().getReservefield3()))
    noOfPayment=commonValidatorVO.getReserveField2VO().getReservefield3();
  if (functions.isValueNull(commonValidatorVO.getReserveField2VO().getReservefield4()))
    firstPaymentAmount=commonValidatorVO.getReserveField2VO().getReservefield4();
  if (functions.isValueNull(commonValidatorVO.getReserveField2VO().getReservefield5()))
    lastPaymentAmount=commonValidatorVO.getReserveField2VO().getReservefield5();

  String readonly1="";
  String readonly2="";
  String readonly="";
  String chdisabled="";
  String chchecked="";
  if (functions.isValueNull(formattedDate))
  {
    readonly1="readonly";
    chdisabled="disabled";
    chchecked="checked";
  }
  else
  {
    readonly1="disabled";
  }
  if (functions.isValueNull(firstPaymentAmount))
  {
    readonly="readonly";
  }
  else
  {
    readonly="disabled";
  }
  if (functions.isValueNull(lastPaymentAmount))
  {
    readonly2="readonly";
  }
  else
  {
    readonly2="disabled";
  }

//  System.out.println("formatted = "+format);
//  System.out.println("d = "+d);
//  System.out.println("Date = "+paymentDate);

  String partnerId = "";
  String logoName = "";
  String partnerName = "";
  String powerBy = "";
  String redirectUrl="";
  String sisaLogoFlag="Y";
  String securityLogoFlag="N";
  String partnerLogoFlag="Y";
  String merchantLogo="";
  String merchantLogoFlag="";


  if(commonValidatorVO!=null)
  {
    Transaction transaction = new Transaction();
    GenericTransDetailsVO genericTransDetailsVO=commonValidatorVO.getTransDetailsVO();

    if(commonValidatorVO.getAddressDetailsVO() != null && commonValidatorVO.getAddressDetailsVO().getTmpl_amount() != null)
      amount = commonValidatorVO.getAddressDetailsVO().getTmpl_amount();
    else
      amount = commonValidatorVO.getTransDetailsVO().getAmount();

    if(commonValidatorVO.getAddressDetailsVO() != null && commonValidatorVO.getAddressDetailsVO().getTmpl_currency() != null)
      currency = commonValidatorVO.getAddressDetailsVO().getTmpl_currency();
    else
      currency = commonValidatorVO.getTransDetailsVO().getCurrency();

    logoName = commonValidatorVO.getMerchantDetailsVO().getLogoName();
    partnerName = commonValidatorVO.getPartnerName();
    powerBy = commonValidatorVO.getMerchantDetailsVO().getPoweredBy();
    sisaLogoFlag = commonValidatorVO.getMerchantDetailsVO().getSisaLogoFlag();
    securityLogoFlag = commonValidatorVO.getMerchantDetailsVO().getSecurityLogo();
    partnerLogoFlag = commonValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
    merchantLogo = commonValidatorVO.getMerchantDetailsVO().getLogoName();
    merchantLogoFlag = commonValidatorVO.getMerchantDetailsVO().getMerchantLogo();

    if("Y".equalsIgnoreCase(merchantLogoFlag)) {
      merchantLogo = (String)session.getAttribute("merchantLogoName");
      log.debug("merchantLogo session----"+merchantLogo);
      if(!functions.isValueNull(merchantLogo)){
        merchantLogo=commonValidatorVO.getMerchantDetailsVO().getMerchantLogoName();
      }
    }
    else {
      merchantLogo = commonValidatorVO.getMerchantDetailsVO().getLogoName();
      log.debug("merchantLogo vo----"+merchantLogo);
    }
  }

  String currencySymbol = currency;
  if(rb.containsKey(currencySymbol))
    currencySymbol = rb.getString(currencySymbol);

  if(functions.isValueNull(currency))
  {
    NumberFormat formatter = null;
    if (currency.equalsIgnoreCase("JPY"))
    {
      formatter = NumberFormat.getCurrencyInstance(Locale.JAPAN);
      double dbl = Double.valueOf(amount);
      amount = formatter.format(dbl);
      currencySymbol = "";
    }
    if (currency.equalsIgnoreCase("EUR"))
    {
      formatter = NumberFormat.getCurrencyInstance(Locale.FRANCE);
      double dbl = Double.valueOf(amount);
      amount = formatter.format(dbl);
      currencySymbol = "";
    }
    if (currency.equalsIgnoreCase("GBP"))
    {
      formatter = NumberFormat.getCurrencyInstance(Locale.UK);
      double dbl = Double.valueOf(amount);
      amount = formatter.format(dbl);
      currencySymbol = "";
    }
    if (currency.equalsIgnoreCase("USD"))
    {
      formatter = NumberFormat.getCurrencyInstance(Locale.US);
      double dbl = Double.valueOf(amount);
      amount = formatter.format(dbl);
      currencySymbol = "";
    }
  }

  String supportName = "";
  String cardTypeid = commonValidatorVO.getCardType();
  String cardType = GatewayAccountService.getCardType(cardTypeid);
  String termsLink = "";
  String privacyLink = "";
  String target = "";
  String consent1 = rb1.getString("VAR_CONSENT_STMT1");
  String consent2 = rb1.getString("VAR_CONSENT_STMT2");
  String consent3 = rb1.getString("VAR_CONSENT_STMT3");
  String varAND = rb1.getString("VAR_AND");
  String ecospend = "Ecospend Technologies Limited needs your permission to securely initiate a payment order from your bank.";
  String ecospend2 = "Provided by Ecospend, licensed by the FCA";
  if(!merchantLogoFlag.equals(null) && merchantLogoFlag.equalsIgnoreCase("Y"))
  {
    termsLink = commonValidatorVO.getMerchantDetailsVO().getTcUrl();
    privacyLink = commonValidatorVO.getMerchantDetailsVO().getPrivacyPolicyUrl();
    target = "target=\"_blank\"";
    if(functions.isEmptyOrNull(termsLink))
      termsLink = "/#";
    if(functions.isEmptyOrNull(privacyLink))
      privacyLink = "/#";

  }
  else if(!partnerLogoFlag.equals(null) && partnerLogoFlag.equalsIgnoreCase("Y"))
  {
    termsLink = commonValidatorVO.getMerchantDetailsVO().getPartnerTcUrl();
    privacyLink = commonValidatorVO.getMerchantDetailsVO().getPartnerPrivacyUrl();
    target = "target=\"_blank\"";

    if(functions.isEmptyOrNull(termsLink))
      termsLink = "#";
    if(functions.isEmptyOrNull(privacyLink))
      privacyLink = "#";
  }
  else
  {
    termsLink = "#";
    privacyLink = "#";
  }
%>

<script type = "text/javascript" src="/merchant/transactionCSS/js/anime.min.js"></script> <!-- loader animation-->
<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.min.js"></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/bootstrap/js/bootstrap.min.js" ></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.cardcheck.js"></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.cardcheck.min.js"></script>
<link rel="stylesheet" href="/merchant/transactionCSS/css/msdropdown.css">
<script src="/merchant/transactionCSS/js/msdropdown.js"></script>
<%--<script type = "text/javascript" src="/merchant/transactionCSS/js/content.js"></script>--%>
<script type = "text/javascript" src="/merchant/transactionCSS/js/content.min.js"></script>



<!-- modal starts -->
<div class="modal show" style="display: inline-block">
  <div class="modal-dialog modal-sm modal-dialog-centered">
    <div class="modal-content">
      <div id="target" class="mainDiv" >
        <div class="header">
          <%
            String padForAmt = "";
            if(!merchantLogoFlag.equals(null) && merchantLogoFlag.equalsIgnoreCase("Y"))
            {
              supportName = commonValidatorVO.getMerchantDetailsVO().getCompany_name();
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
            supportName = commonValidatorVO.getMerchantDetailsVO().getPartnerName();
//            padForAmt = "padding: 20px 0px;";
            padForAmt = "line-height: 79px;";
          %>
          <div class="logo">
            <img class="images-style1" style="max-width: 100%;max-height:100%" src="/images/merchant/<%=commonValidatorVO.getMerchantDetailsVO().getLogoName()%>">
          </div>
          <%
            }
          %>
          <div class="name" style="text-align: right;">
            <%
              String visibility = "block";
              String amountStyle= "";
              if(commonValidatorVO.getMerchantDetailsVO().getMerchantOrderDetailsDisplay().equalsIgnoreCase("N"))
              {
                visibility  = "none";
//                amountStyle = "font-size: 25px;"+padForAmt;
                amountStyle = padForAmt;
              }
            %>
            <div class="client-name" style="display: <%=visibility%>" >
              <span class="tool"><%=commonValidatorVO.getMerchantDetailsVO().getCompany_name()%>
                <div class="tooltiptext"><%=commonValidatorVO.getMerchantDetailsVO().getCompany_name()%> </div>
              </span>
            </div>
            <div class="client-order" style="display: <%=visibility%>">
              <span class="toolOrderID" id="toolOrderID"><%=commonValidatorVO.getTransDetailsVO().getOrderId()%>
                <div class="tooltiptextOrderId" id="tooltiptextOrderId"> <%=commonValidatorVO.getTransDetailsVO().getOrderId()%> </div>
              </span>
            </div>
            <div class="client-amount" style="<%=amountStyle%>"><%=currencySymbol%><%=amount%></div>
          </div>
        </div>

        <div class="modal-body">
          <div class="top-bar">
            <div class="top-right" id="topRight" style="cursor: pointer">
              <span onclick="backToHome()"><i class="fas fa-home"></i></span>
            </div>
            <div class="top-left" id="topLeft" >
              <span id="arrow" class="hide" style="float: left;width: 20px;cursor: pointer" onclick="backArrowbutton()" >
                <i class="fas fa-angle-left"></i>
              </span>
              <div id="payMethod" style="font-weight: 600;text-align: center" class="truncate">Bank Details</div>
            </div>
          </div>

          <div id="options">
            <div class="footer-tab" style="height:335px">
              <form id="smsForm" name="smsForm" action="/transaction/EcospendProcess" method="post">
                <input type="hidden" name="status" value="Success" />
                <input type="hidden" name="trackingid" value=<%=trackingid%> />
                <input type="hidden" name="phoneNumber" value=<%=phoneNumber%> />
                <input type="hidden" name="orderDesc" value=<%=orderDesc%> />
                <input type="hidden" name="emailsent" value=<%=emailsent%> />
                <input type="hidden" name="accountid" value=<%=accountId%> />
                <input type="hidden" name="paymentType" value=<%=commonValidatorVO.getPaymentType()%> />
                <input type="hidden" name="cardtype" value=<%=commonValidatorVO.getCardType()%> />
                <input type="hidden" name="Fromtype" value=<%=commonValidatorVO.getTransDetailsVO().getFromtype()%> />
                <input type="hidden" name="autoRedirect" value=<%=autoRedirect%> />
                <input type="hidden" name="currency" value=<%=currency%> />
                <input type="hidden" name="accesstoken" value=<%=AccessAuthtoken%> />
                <input type="hidden" name="tmpl_amount" value=<%=commonValidatorVO.getAddressDetailsVO().getTmpl_amount()%> />
                <input type="hidden" name="tmpl_currency" value=<%=commonValidatorVO.getAddressDetailsVO().getTmpl_currency()%> />
                <input type="hidden" name="isService" value=<%=isService%> />
                <input type="hidden" name="terminalId" value=<%=commonValidatorVO.getTerminalId()%> />

                <div class="tab" style="display: inline-block;height:330px;">
                  <%--<p class="form-header" style="font-weight: bold;font-size: 15px;"> Please enter the below details.</p>--%>

<%--                  <div class="form-group has-float-label" style="margin: 15px 0px 0px 2px !important;width: 100%;" >
                    <select name="paymentMethod" class="form-control input-control1 nbi-dropdown" id="paymentMethod" onchange="selectedOption()">
                      <option value="">Select Payment method</option>
                      <option value="Instant Payment">Instant Payment</option>
                      <option value="Scheduled Payment">Scheduled Payment</option>
                      &lt;%&ndash;<option value="Standing Orders">Standing Orders</option>&ndash;%&gt;
                      <option value="Pay By Link">Pay By Link</option>
                    </select>
                    <label for="paymentMethod" style="color:#757575">Payment method</label>
                  </div>--%>

                  <div class="form-group has-float-label control-group-full">
                    <select name="esbankid" class="form-control input-control1 nbi-dropdown" id="esbanklist" onchange="BankES()" style="margin: -2px 0; " >
                      <option value="">Select Bank</option>
                      <%
                        Set statusSet     = banklist.keySet();
                        Iterator iterator = statusSet.iterator();
                        String selected   = "";
                        String key        = "";
                        String value      = "";
                        String value1     = "";
                        String value2     = "";
                        String value3     = "";
                        while (iterator.hasNext())
                        {
                          key     = (String)iterator.next();
                          value   = (String) banklist.get(key);
                          value1  = value.split("_")[0];
                          value2  = value.split("_")[1];
                          value3  = value.split("_")[2];
                      %>
                      <option title="<%=value2%>" value="<%=ESAPI.encoder().encodeForHTMLAttribute(key+"|"+value1+"|"+value3)%>"><%=ESAPI.encoder().encodeForHTML(value1)%>
                      </option>
                      <%
                        }
                      %>
                    </select>
                    <label for="esbanklist" style="color:#757575;position: absolute;top: -17px;">Bank Name</label>
                  </div>


                  <%--<div class="form-group has-float-label" style="margin: 15px 0 0 0!important;width: 100%;">
                    <input class="form-control input-control1" id="paymentReference" name="paymentReference" placeholder=" "
                           type="text" maxlength="200" oninput="this.className = 'form-control input-control1'"  />
                    <label for="paymentReference" class="form-label"> Payment Reference </label>
                  </div>--%>

<%--                  <p class="form-header" style="font-weight: bold;font-size: 15px;clear:both;padding:15px 0;">Creditor Account</p>

                  <div class="form-group has-float-label control-group-half" >
                    <select class="form-control input-control1 nbi-dropdown" id="craccountType" name="craccountType" style="margin:-2px;">
                      <option value="">Select Account Type</option>
                      <option value="SortCode">Sort Code</option>
                      <option value="Iban">IBAN</option>
                      <option value="Bban">BBAN</option>
                    </select>
                    <label for="craccountType" style="color:#757575">Account Type</label>
                  </div>

                  <div class="form-group has-float-label control-group-half">
                    <input class="form-control input-control1" id="craccountIdentification" name="craccountIdentification" placeholder=" "
                           type="text" maxlength="200" oninput="this.className = 'form-control input-control1'"  />
                    <label for="craccountIdentification" class="form-label"> Account identification</label>
                  </div>

                  <div class="form-group has-float-label control-group-half">
                    <input class="form-control input-control1" id="crownerName" name="crownerName" placeholder=" "
                           type="text" maxlength="200" oninput="this.className = 'form-control input-control1'"  />
                    <label for="crownerName" class="form-label"> Owner Name </label>
                  </div>

                  <div class="form-group has-float-label control-group-half">
                    <input class="form-control input-control1" id="crcurrency" name="crcurrency" placeholder=" "
                           type="text" maxlength="200" oninput="this.className = 'form-control input-control1'"  />
                    <label for="crcurrency" class="form-label"> Currency </label>
                  </div>

                  <div class="form-group has-float-label control-group-half">
                    <input class="form-control input-control1" id="crbic" name="crbic" placeholder=" "
                           type="text" maxlength="200" oninput="this.className = 'form-control input-control1'"  />
                    <label for="crbic" class="form-label"> BIC </label>
                  </div>
--%>

                    <%--<p class="form-header" style="font-weight: bold;font-size: 15px;clear:both;padding:10px 0;">
                    <div class="form-group terms-checkbox hide" id="terms">
                      <input type="checkbox" style="width: 5%;height:15px;" id="Debtoraccount" value="OFF" name="Debtoraccount" onchange="box()">
                      <label for="Debtoraccount" style="font-weight: bold;font-size: 15px;">
                        Debtor Account
                      </label>
                    </div>
                  </p>--%>

                  <%--<div class="form-group has-float-label control-group-half" >
                    <select class="form-control input-control1 nbi-dropdown" id="draccountType" name="draccountType" style="margin:-2px;">
                      <option value="">Select Account Type</option>
                      <option value="SortCode">Sort Code</option>
                      <option value="Iban">IBAN</option>
                      <option value="Bban">BBAN</option>
                    </select>
                    <label for="draccountType" style="color:#757575">Account Type</label>
                  </div>--%>


                  <%--<div class="form-group has-float-label control-group-half">
                    <input class="form-control input-control1" id="draccountIdentification" name="draccountIdentification" placeholder=" "
                           type="text" maxlength="200" oninput="this.className = 'form-control input-control1'"/>
                    <label for="draccountIdentification" class="form-label"> Account identification</label>
                  </div>--%>

                 <%-- <div class="form-group has-float-label control-group-half">
                    <input class="form-control input-control1" id="drownerName" name="drownerName" placeholder=" "
                           type="text" maxlength="200" oninput="this.className = 'form-control input-control1'"/>
                    <label for="drownerName" class="form-label"> Owner Name </label>
                  </div>--%>

<%--                  <div class="form-group has-float-label control-group-half">
                    <input class="form-control input-control1" id="drcurrency" name="drcurrency" placeholder=" "
                           type="text" maxlength="200" oninput="this.className = 'form-control input-control1'"/>
                    <label for="drcurrency" class="form-label"> Currency </label>
                  </div>--%>

                  <%--<div class="form-group has-float-label control-group-half">
                    <input class="form-control input-control1" id="drbic" name="drbic" placeholder=" "
                           type="text" maxlength="200" oninput="this.className = 'form-control input-control1'" />
                    <label for="drbic" class="form-label"> BIC </label>
                  </div>--%>


                    <%
                      if(("StandingOrders").equalsIgnoreCase(cardType)) {
                    %>

                    <%--<div class="form-group has-float-label control-group-half">
                      <input class="form-control input-control1" id="beneficiary" name="beneficiary" placeholder=" "
                             type="text" maxlength="200" oninput="this.className = 'form-control input-control1'" />
                      <label for="beneficiary" class="form-label"> Beneficiary </label>
                    </div>--%>

                    <%--<div class="form-group has-float-label control-group-half">
                      <input class="form-control input-control1" id="drSortCode" name="drSortCode" placeholder=" "
                             type="text" maxlength="200" oninput="this.className = 'form-control input-control1'" />
                      <label for="drSortCode" class="form-label"> Sort Code </label>
                    </div>--%>

                    <%--<div class="form-group has-float-label control-group-half" >
                      <select class="form-control input-control1 nbi-dropdown" id="drSortCode" name="drSortCode" style="margin:-2px;">
                        <option value="">Select Account Type</option>
                        <option value="SortCode">Sort Code</option>
                        <option value="Iban">IBAN</option>
                        <option value="Bban">BBAN</option>
                      </select>
                      <label for="drSortCode" style="color:#757575">Account Type</label>
                    </div>--%>

<%--                    <div class="form-group has-float-label control-group-half">
                      <input class="form-control input-control1" id="drAccNumber" name="drAccNumber" placeholder=" "
                             type="text" maxlength="200" oninput="this.className = 'form-control input-control1'" />
                      <label for="drAccNumber" class="form-label">Account Number </label>
                    </div>--%>

<%--                    <div class="form-group has-float-label control-group-half">
                      <input class="form-control input-control1" id="drAmount" name="drAmount" placeholder=" "
                             type="text" maxlength="200" oninput="this.className = 'form-control input-control1'" />
                      <label for="drAmount" class="form-label"> Amount </label>
                    </div>--%>

                    <div class="form-group has-float-label control-group-half">
                      <input class="form-control input-control1" id="firstPaymentDate" name="firstPaymentDate" placeholder=" "
                             type="date" oninput="this.className = 'form-control input-control1'" style="margin: -2px 0;" value="<%=formattedDate%>" <%=functions.isValueNull(formattedDate) ? "readonly" : "" %> />
                      <label for="firstPaymentDate" class="form-label"> First Payment Date </label>
                    </div>

                    <div class="form-group has-float-label control-group-half" >
                      <select class="form-control input-control1 nbi-dropdown" id="period" name="period" style="margin:-2px 0;" value="<%=paymentPeritod%>" <%=functions.isValueNull(paymentPeritod) ? "readonly" : "" %> >
                        <option value="Monthly">Monthly</option>
                        <option value="Weekly">Weekly</option>
                      </select>
                      <label for="period" style="color:#757575">Period</label>
                    </div>

                    <div class="form-group has-float-label control-group-half">
                      <input class="form-control input-control1" id="noOfPayments" name="noOfPayments" placeholder=" " <%=functions.isValueNull(noOfPayment) ? "readonly" : "" %>
                             type="text" oninput="this.className = 'form-control input-control1'" value="<%=noOfPayment%>" >
                      <label for="noOfPayments" class="form-label"> Number of Payments </label>
                    </div>

<%--                    <div class="form-group has-float-label control-group-half">
                      <input class="form-control input-control1" id="reference" name="reference" placeholder=" "
                             type="text" oninput="this.className = 'form-control input-control1'" />
                      <label for="reference" class="form-label"> Reference </label>
                    </div>--%>


                    <p class="form-header" style="font-weight: bold;font-size: 15px;clear:both;padding:10px 0;">
                    <div class="form-group" id="terms" style="width: 100%;height: 30px;margin: 0px;">
                      <input type="checkbox" style="width: 5%;height:15px;" id="Debtoraccount" value="OFF" name="Debtoraccount" onchange="box()">
                      <label for="Debtoraccount" style="font-weight: 600;font-size: 15px;vertical-align: middle;margin-bottom: 6px;">
                        Different first and/or last amount
                      </label>
                    </div>
                    </p>

                    <div class="form-group has-float-label control-group-half">
                      <input class="form-control input-control1" id="firstPaymentAmount" name="firstPaymentAmount" placeholder=" "
                             type="text" maxlength="200" oninput="this.className = 'form-control input-control1'" value="<%=firstPaymentAmount%>" <%=readonly%>>
                      <label for="firstPaymentAmount" class="form-label"> First Payment Amount </label>
                    </div>

                    <div class="form-group has-float-label control-group-half">
                      <input class="form-control input-control1" id="lastPaymentAmount" name="lastPaymentAmount" placeholder=" "
                             type="text" maxlength="200" oninput="this.className = 'form-control input-control1'" value="<%=lastPaymentAmount%>" <%=readonly2%>>
                      <label for="lastPaymentAmount" class="form-label"> Last Payment Amount </label>
                    </div>

                  <%
                    }
                  %>

                  <%--<%
                    if(("InstantPayment").equalsIgnoreCase(cardType))
                    {
                  %>

                    <p class="form-header" style="font-weight: bold;font-size: 15px;clear:both;padding:10px 0;">
                    <div class="form-group" id="terms" style="width: 100%;height: 30px;">
                      <input type="checkbox" style="width: 5%;height:15px;" id="Debtoraccount" value="OFF" name="Debtoraccount" onchange="box()">
                      <label for="Debtoraccount" style="font-weight: 600;font-size: 15px;vertical-align: middle;margin-bottom: 6px;">
                        Scheduled Payment
                      </label>
                    </div>
                    </p>

                    <div class="form-group has-float-label control-group-half">
                      <input class="form-control input-control1" id="scheduledPaymentDate" name="scheduledPaymentDate" placeholder=" " disabled
                             type="date" oninput="this.className = 'form-control input-control1'" />
                      <label for="scheduledPaymentDate" class="form-label"> Scheduled Payment Date </label>
                    </div>

                  <%
                    }
                  %>--%>



                    <%
                      if(("SCHEDULEDPAYMENT").equalsIgnoreCase(cardType))
                      {
                    %>

                    <p class="form-header" style="font-weight: bold;font-size: 15px;clear:both;padding:10px 0;">
                    <div class="form-group" id="terms" style="width: 100%;height: 30px;">
                      <input type="checkbox" style="width: 5%;height:15px;" id="Debtoraccount" value="OFF" <%=chdisabled%> <%=chchecked%> name="Debtoraccount" onchange="box()">
                      <label for="Debtoraccount" style="font-weight: 600;font-size: 15px;vertical-align: middle;margin-bottom: 6px;">
                        Scheduled Payment
                      </label>
                    </div>
                    </p>

                    <div class="form-group has-float-label control-group-half">
                      <input class="form-control input-control1" id="scheduledPaymentDate" name="scheduledPaymentDate" placeholder=" "
                             type="date" oninput="this.className = 'form-control input-control1'" value="<%=formattedDate%>" <%=readonly1%>/>
                      <label for="scheduledPaymentDate" class="form-label"> Scheduled Payment Date </label>
                    </div>

                    <%
                      }
                    %>


                    <%
                      if(("Paybylink").equalsIgnoreCase(cardType))
                      {
                    %>

                    <div class="form-group has-float-label control-group-full" id="email_ES">
                      <span class="input-icon"><i class="far fa-envelope"></i></span>
                      <input type="email" class="form-control input-control1" id="esemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'email_ES')"
                             oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
                      <label for="esemail" class="form-label">Email</label>
                    </div>

                    <div class="form-group has-float-label control-group-half">
                      <div class="dropdown">
                        <input id="country_input_pbl" class="form-control input-control1" placeholder=" " oninput="this.className = 'form-control input-control1'"
                               onblur="pincodecc('country_input_pbl','country_pbl','phonecc_id_pbl','phonecc_pbl'); "
                               onkeypress="return isLetterKey(event)" />
                        <label for="country_input_pbl" class="form-label">Country</label>
                        <input type="hidden" id="country_pbl"  name="country_input" value="<%=country%>">
                      </div>
                    </div>

                    <div class="form-group has-float-label control-group-half" style="width: 10% !important">
                      <input type="text" class="form-control input-control1" name="phone-CC" id="phonecc_id_pbl" placeholder=" "
                             onkeypress="return isNumberKey(event)"
                             onkeyup="onPasteNumCheck(event)" maxlength="3" onblur="setPhoneCC('phonecc_id_pbl','phonecc_pbl')" value="<%=phoneNumberCC%>"
                             oninput="this.className = 'form-control input-control1'"/>
                      <label for="phonecc_id_pbl" class="form-label">CC</label>
                      <input type="hidden" id="phonecc_pbl" name="phone-CC" value=""/>
                    </div>
                    <div class="form-group has-float-label control-group-half" style="width: 3% !important;padding: 6px 0px;">
                      -
                    </div>
                    <div class="form-group has-float-label control-group-half" id="ESPhoneNum" style="width: 35% !important">
                      <span class="input-icon-half"><i class="fas fa-phone"></i> </span>
                      <input type="text" class="form-control input-control1" id="phone_pbl" placeholder=" " onkeypress="return isNumberKey(event)"
                             onkeyup="onPasteNumCheck(event)" oninput="this.className = 'form-control input-control1'" maxlength="24" name="telno" value="<%=phoneNumber%>"/>
                      <label for="phone_pbl" class="form-label">Phone Number</label>
                    </div>

                    <script>
                      $( "#country_input_pbl").autocomplete({
                        source: availableTags
                      });
                      setCountry('<%=country%>','country_input_pbl');
                      pincodecc('country_input_pbl','country_pbl','phonecc_id_pbl','phonecc_pbl');
                    </script>

                    <%
                      }
                    %>

                    <div class="tab2" style="display: none"  id="parameter">
                    <div class="form-group has-float-label control-group-half" id="accountNumber">
                      <input type="text" class="form-control input-control1" id="Account Number"
                             oninput="this.className = 'form-control input-control1'"  name="accountNumber" value="<%=accountNumber%>" readonly />
                      <label for="accountNumber" class="form-label">Account Number</label>
                    </div>
                    <div class="form-group has-float-label control-group-half" id="ProcesserName">
                      <input type="text" class="form-control input-control1" id="Processer Name"
                             oninput="this.className = 'form-control input-control1'"  name="accountNumber" value="<%=processerName%>" readonly />
                      <label for="processerName" class="form-label">Payee</label>
                    </div>
                    <div class="form-group has-float-label control-group-half" id="SortCode">
                      <input type="text" class="form-control input-control1" id="Sort Code"
                             oninput="this.className = 'form-control input-control1'"  name="sortCode" value="<%=sortCode%>" readonly />
                      <label for="SortCode" class="form-label">Sort Code</label>
                    </div>
                      <div class="form-group has-float-label control-group-half" id="Reference">
                      <input type="text" class="form-control input-control1" id="Reference id"
                             oninput="this.className = 'form-control input-control1'"  name="sortCode" value="<%=trackingid%>" readonly />
                      <label for="Reference" class="form-label">Reference</label>
                    </div>
                </div>

                </div>

                <jsp:include page="requestParameters.jsp">
                  <jsp:param name="paymentMode" value="ECOSPEND" />
                </jsp:include>

              </form>
            </div>

            <div class="form-group " id="terms1">
              <input type="checkbox" class="" style="width: 5%" id="TC_DC_IN" onclick="BankES1()">
              <label for="TC_DC_IN" style="font-size: 85%;position: absolute;margin: 0px;">
                <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
              </label>
            </div>
           <%-- <div class="form-group " id="terms1">
              <label for="TC_DC_IN" style="font-size: 85%;position: absolute;margin: 0px;">
              <p style="font-size:11px" style="text-align:center;"> <%=ecospend%></p> <br>  <p style="font-size:5px"><%=ecospend2%></p>
              </label>
            </div>--%>
            <div class="form-group " id="terms1">
              <label for="TC_DC_IN" style="font-size: 65%;position: absolute;margin: 0px; color:Gray; ">
                <p style="text-align:center;"> <b><%=ecospend%></b></p>  <p style="text-align:center;">  <b><%=ecospend2%> </b> </p>
              </label>
            </div>

            <%--<div class="form-group" id="terms" style="width: 100%;height: 30px;margin: 0px;">
              <input type="checkbox" style="width: 5%;height:15px;" id="Debtoraccount" value="OFF" name="Debtoraccount" onchange="box()">
              <label for="Debtoraccount" style="font-weight: 600;font-size: 15px;vertical-align: middle;margin-bottom: 6px;">
                Different first and/or last amount
              </label>
            </div>--%>


            <div class="pay-btn" style="position: inherit" >
              <div class="pay-button disabledbutton" id="confirmCode" onclick="confirmCode()">
                <span><%=varCONTINUE%></span>
              </div>
              <div class="pay-button hide" id="submitForm" onclick="submitForm()">
                <span>Confirm </span>
              </div>
            </div>
          </div>


          <%-- footer starts--%>
          <%
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
          <!-- footer  -->
        </div>
      </div>
    </div>
  </div>
</div>

<script type="text/javascript">

  var continueButton  = document.getElementById('confirmCode');
  var confirmButton   = document.getElementById('submitForm');
  var backArrow       = document.getElementById('arrow');
  var title           = document.getElementById('payMethod');
  var checkboxButton  = document.getElementById('Debtoraccount');

  function backArrowbutton() {
    console.log("in continue button");

    backArrow.classList.contains("hide") ? "" : backArrow.classList.add("hide");
    confirmButton.classList.contains("hide") ? "" : confirmButton.classList.add("hide");
    continueButton.classList.contains("hide") ?  continueButton.classList.remove("hide") : "";
    checkboxButton.disabled = false;
    title.innerHTML = "Bank Details";

    var checkboxButton2     = document.getElementById('TC_DC_IN');
    checkboxButton2.disabled = false;

    $(".form-control").each(function(currentValue, element, array) {
      element.classList.remove("displayLabel")
    });
  }

  function confirmCode() {
    console.log("in continue button");

    backArrow.classList.contains("hide") ? backArrow.classList.remove("hide") : "";
    continueButton.classList.contains("hide") ? "" : continueButton.classList.add("hide");
    confirmButton.classList.contains("hide") ?  confirmButton.classList.remove("hide") : "";
    if(checkboxButton!=null){
      checkboxButton.disabled = true;
    }

    title.innerHTML = "Summary";

    var checkboxButton2     = document.getElementById('TC_DC_IN');
    checkboxButton2.disabled = true;

    $(".form-control").each(function(currentValue, element, array) {
      element.classList.add("displayLabel")
    });
  }

  function submitForm() {
    console.log("in submit form");
    $("#smsForm").submit();
  }

  function box() {
    var checkBox = document.getElementById("Debtoraccount");

    if (checkBox.checked == false) {
      document.getElementById("firstPaymentAmount") ? document.getElementById("firstPaymentAmount").disabled = true : "";
      document.getElementById("lastPaymentAmount") ? document.getElementById("lastPaymentAmount").disabled = true: "";
      document.getElementById("scheduledPaymentDate") ? document.getElementById("scheduledPaymentDate").disabled = true: "";
    }

    if (checkBox.checked == true) {
      document.getElementById("firstPaymentAmount") ? document.getElementById("firstPaymentAmount").disabled = false : "";
      document.getElementById("lastPaymentAmount") ? document.getElementById("lastPaymentAmount").disabled = false : "";
      document.getElementById("scheduledPaymentDate") ? document.getElementById("scheduledPaymentDate").disabled = false : "";
    }
  }


/*  function BankES()
  {

    var select = document.getElementById("esbanklist");

    if (select.selectedIndex)
    {console.log("inside if ");
      $("#confirmCode").removeClass("disabledbutton");
    }
    else
    {
      console.log("inside else ");
      $("#confirmCode").addClass("disabledbutton");

    }
  }*/
  /*function myFunction() {
    var x = document.getElementById("myDIV");
    if (x.style.display === "none") {
      x.style.display = "block";
    } else {
      x.style.display = "none";
    }
  }*/
  function BankES()
  {  var x = document.getElementById("parameter");
    var select = document.getElementById("esbanklist");
    if (select.selectedIndex)
    {console.log("inside if block style ");
     // $("#parameter").removeClass("hide");
     // $("#confirmCode").removeClass("disabledbutton");
      x.style.display = "block";
    }
    else
    {
      console.log("inside else unblock style ");
    //  $("#parameter").addClass("hide");
     // $("#confirmCode").addClass("disabledbutton");
      x.style.display = "none";
    }
  }

  function BankES1()
  {
    var select2 = document.getElementById("esbanklist");
    var select = document.getElementById("TC_DC_IN");
    console.log(" checkbox "+select2.selectedIndex);
    if (select.checked && select2.selectedIndex )
    {console.log("inside if checkbox "+select2.selectedIndex);
      $("#confirmCode").removeClass("disabledbutton");
    }
    else
    {
      console.log("inside else checkbox ");

      $("#confirmCode").addClass("disabledbutton");

    }
  }


  /*[inputs, knapp] = [
    $(':input:not([type=hidden])'),
    $('#confirmCode')]
  knapp.disabled = true

  console.log("knapp", knapp);
  console.log("inputs = ", inputs)

  for (i = 0; i < inputs.length; i++) {
    inputs[i].addEventListener('input',() => {
      let values = []
      inputs.forEach(v => values.push(v.value))
      knapp.disabled = values.includes('')
    })
  }*/
</script>
<script type="text/javascript">
  $(document).ready(function() {
    $("#esbanklist").msDropDown();
  });
</script>
</body>
</html>