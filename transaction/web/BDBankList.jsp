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
<%@ page import="com.payment.aamarpay.AamarPayResponseVO" %>
<%

  response.setHeader("Pragma", "No-cache");
  response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
  response.setDateHeader("Expires", -1);


%>
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
  <%--<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">--%>
  <meta http-equiv='cache-control' content='no-cache'>
  <meta http-equiv='expires' content='0'>
  <meta http-equiv='pragma' content='no-cache'>
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
  <%!
    private final static ResourceBundle RB      = LoadProperties.getProperty("com.directi.pg.aamarpay");
    String RETURN_URL    = RB.getString("RETURN_URL");
  %>
  <script>
    function update(cartType){

      $.ajax( {
        url: '<%=RETURN_URL%>'+$('#trackingId').val(),
        //url: "http://localhost:8081/transaction/AMPayFrontEndServlet?",
        dataType: "json",
        data: {
          ctoken: $('#ctoken').val(),
          method:"updateCardType",
          trackingID: $('#trackingId').val(),
          cartType: cartType
        },
        success: function( data ) {
        console.log('updatedd '+data);
        }
      } );
    }
  </script>

</head>
<body style="font-family: Montserrat;" class="background">

<%
  Functions functions=new Functions();
  CommonValidatorVO commonValidatorVO   = (CommonValidatorVO) request.getAttribute("transDetails");
  List<AamarPayResponseVO> paymentList  = null;
  paymentList                           = (List<AamarPayResponseVO>) request.getAttribute("BankURLList");
  String payment_Type                   = (String) request.getAttribute("payment_Type");
  commonValidatorVO.getTrackingid();
  String ctoken = "";
  ctoken                = (String) session.getAttribute("ctoken");

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
  String currencySymbol = "";
  String amount= "0.00";


  if(commonValidatorVO!=null)
  {
    Transaction transaction = new Transaction();
    GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();

    amount = commonValidatorVO.getDisplayAmount();
    if (commonValidatorVO.getAddressDetailsVO() != null && commonValidatorVO.getAddressDetailsVO().getTmpl_amount() != null){
      amount = commonValidatorVO.getAddressDetailsVO().getTmpl_amount();
    }else{
      amount = commonValidatorVO.getTransDetailsVO().getAmount();
    }

    String currency = "";

    if(commonValidatorVO.getAddressDetailsVO() != null && commonValidatorVO.getAddressDetailsVO().getTmpl_currency() != null)
    {  currency = commonValidatorVO.getAddressDetailsVO().getTmpl_currency();
    }
    else
    {
      currency = commonValidatorVO.getTransDetailsVO().getCurrency();
    }

    currencySymbol = currency;
    if(rb.containsKey(currencySymbol))
      currencySymbol = rb.getString(currencySymbol);


    logoName      = commonValidatorVO.getMerchantDetailsVO().getLogoName();
    partnerName   = commonValidatorVO.getPartnerName();
    powerBy       = commonValidatorVO.getMerchantDetailsVO().getPoweredBy();
    sisaLogoFlag      = commonValidatorVO.getMerchantDetailsVO().getSisaLogoFlag();
    securityLogoFlag  = commonValidatorVO.getMerchantDetailsVO().getSecurityLogo();
    partnerLogoFlag   = commonValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
    merchantLogo      = commonValidatorVO.getMerchantDetailsVO().getLogoName();
    merchantLogoFlag  = commonValidatorVO.getMerchantDetailsVO().getMerchantLogo();

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



  String supportName = "";
  String cardTypeid = commonValidatorVO.getCardType();
  String cardType = GatewayAccountService.getCardType(cardTypeid);
  String termsLink = "";
  String privacyLink = "";
  String target = "";
  String consent1 = rb1.getString("VAR_CONSENT_STMT1");
  String consent2 = rb1.getString("VAR_CONSENT_STMT2");
  String consent3 = rb1.getString("VAR_CONSENT_STMT3");
  String varAND   = rb1.getString("VAR_AND");
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
             <%if(payment_Type.equalsIgnoreCase("MFS")){%>
                <div id="payMethod" style="font-weight: 600;text-align: center" class="truncate">Mobile Banking</div>
              <%}else{%>
                <div id="payMethod" style="font-weight: 600;text-align: center" class="truncate">CARDS</div>
              <%}%>
            </div>
          </div>

          <div id="options">
            <div class="footer-tab" style="height:335px">
              <div class="tab" style="display: inline-block;height:330px;">
                <ul class="nav nav-tabs" id="WalletTab">
                  <%
                    for(AamarPayResponseVO aamarPayResponseVO : paymentList)
                    {
                  %>
                  <li class="tabs-li-wallet" onclick="update('<%=aamarPayResponseVO.getCard_type()%>')" >
                    <input type="hidden" value="<%=commonValidatorVO.getTrackingid()%>" name="ctoken" id="trackingId">
                    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                    <a href="<%=aamarPayResponseVO.getUrl()%>"  title="<%=aamarPayResponseVO.getCard_type()%>" aria-expanded="false">
                      <img class="images-style" src="<%=aamarPayResponseVO.getImg_medium()%>" alt="<%=aamarPayResponseVO.getCard_type()%>"/>
                      <div class="label-style"><%=aamarPayResponseVO.getCard_type()%></div>
                    </a>
                  </li>
                  <%
                    }
                  %>
                </ul>
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
        <%--</div>--%>
      </div>
    </div>
  </div>
</div>

<script type="text/javascript">
</script>
</body>
</html>