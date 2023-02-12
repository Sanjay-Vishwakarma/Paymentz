<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericAddressDetailsVO" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericTransDetailsVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>

<%@ page import="com.directi.pg.*" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericAddressDetailsVO" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericTransDetailsVO" %>
<%@ page import="com.payment.exceptionHandler.errorcode.ErrorCodeUtils" %>
<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName" %>
<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="java.security.NoSuchAlgorithmException" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<%--
Created by IntelliJ IDEA.
User: Rihen
Date: 6/21/2018
Time: 2:20 PM
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
    <!-- <link rel="icon" type="image/x-icon" href="favicon.ico"> -->
    <link rel="stylesheet" href="/merchant/transactionCSS/css/raleway-font.css" type="text/css">
    <link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.1.0/css/all.css" integrity="sha384-lKuwvrZot6UHsBSfcMvOkWwlCMgc0TaWr+30HWe3a4ltaBwTZhyTEggF5tJv8tbt" crossorigin="anonymous">
    <link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap-float-label.min.css"/>
    <link rel="stylesheet" href="/merchant/transactionCSS/css/styles.css"/>
    <link rel="stylesheet" href="/merchant/transactionCSS/css/success.css"/>

    <style>

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
                width: 345px;
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
                /*margin: 1.75rem;*/
            }
        }

        @media (min-width: 576px) {
            .modal-sm {
                max-width: 346px;
            }
        }

        .mainDiv{
        <%=session.getAttribute("box_shadow")!=null?"box-shadow:"+session.getAttribute("box_shadow").toString():""%>;
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

        .form-control , .form-control:focus , .form-control:disabled, .form-control[readonly] {        /* input control focus disabled background and font color */
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
    </style>

</head>
<body style="font-family: Raleway;" class="background">
<script type = "text/javascript" src="/merchant/transactionCSS/js/anime.min.js"></script> <!-- loader animation-->
<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.min.js"></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/bootstrap/js/bootstrap.min.js" ></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.cardcheck.js"></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.cardcheck.min.js"></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/content.js"></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/content.min.js"></script>

<script type="text/javascript">

    function success_button(){
        console.log("in continue button");
        $("#confirmationForm").submit();
    }

    // success animation
    setTimeout(explode,1000);
    function explode(){
        $('.circle-loader').toggleClass('load-complete');
        $('.checkmark').toggle();
    }
</script>


<%

    Functions functions=new Functions();
    CommonValidatorVO commonValidatorVO= (CommonValidatorVO) request.getAttribute("transDetail");

    Logger log = new Logger("confirmationCheckout.jsp");
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

    String varTRANSACTIONSTATUS = rb1.getString("VAR_TRANSACTION_STATUS");
    String varCONTINUE = rb1.getString("VAR_CONTINUE");
    String varTRANSACTION_SUCCESSFUL = rb1.getString("VAR_TRANSACTION_SUCCESSFUL");
    String varTRANSACTION_FAILED = rb1.getString("VAR_TRANSACTION_FAILED");
    String varTRANSACTION_PENDING = rb1.getString("VAR_TRANSACTION_PENDING");
    String varTRACKING_ID = rb1.getString("VAR_TRACKING_ID");
    String varSTATUS = rb1.getString("VAR_STATUS");
    String varAUTOREDIRECT_IN = rb1.getString("VAR_AUTOREDIRECT_IN");
    String varBankName = rb1.getString("VAR_BANK_NAME");
    String varCardNumber = rb1.getString("VAR_CARDNUMBER");
    String varCardName = rb1.getString("VAR_CARDHOLDERNAME");
    String varLocation = rb1.getString("VAR_LOCATION");
    String varError = rb1.getString("VAR_ERROR");


    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
    ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
    String trackingid="";
    String orderid="";
    String amount = commonValidatorVO.getDisplayAmount();
    String currency = commonValidatorVO.getDisplayCurrency();
    String orderDesc= "";
    String status= "";
    String billingDesc= "";
    String bgcolor="txtboxerror";
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
    String checksum="";
    String respStatus="N";
    String token="";
    String mandateId="";
    String paymentBrand = "";
    String paymentMode = "";
    String cardBin = "";
    String cardLast4Digits = "";
    String bankAccountIban = "";
    String bankAccountBIC = "";
    String bankAccountNumber = "";
    String bankRoutingNumer = "";
    String bankAccountType = "";
    String timestamp = "";
    String pType = "";
    String cType = "";
    String email = "";
    String custAccountId = "";
    String customerId = "";
    String bankAccountId = "";
    String eci="";
    String firstName="";
    String lastName="";
    String cardholderName="";
    String errorName = "";
    String accountId = "";

    String bankName = "";
    String cardNumber = "";
    String cardName = "";
    String location = "";
    String errorMessage = "";


    if(commonValidatorVO!=null && request.getAttribute("responceStatus")!=null /*&& request.getAttribute("displayName")!=null*/)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        Date date = new Date();
        Transaction transaction = new Transaction();

        GenericTransDetailsVO genericTransDetailsVO=commonValidatorVO.getTransDetailsVO();
        trackingid=commonValidatorVO.getTrackingid();
        orderid=genericTransDetailsVO.getOrderId();

        if(commonValidatorVO.getAddressDetailsVO() != null && commonValidatorVO.getAddressDetailsVO().getTmpl_amount() != null)
            amount = commonValidatorVO.getAddressDetailsVO().getTmpl_amount();
        else
            amount = commonValidatorVO.getTransDetailsVO().getAmount();

        if(commonValidatorVO.getAddressDetailsVO() != null && commonValidatorVO.getAddressDetailsVO().getTmpl_currency() != null)
            currency = commonValidatorVO.getAddressDetailsVO().getTmpl_currency();
        else
            currency = commonValidatorVO.getTransDetailsVO().getCurrency();


        if(functions.isValueNull(commonValidatorVO.getReserveField2VO().getBankName())){
            bankName = commonValidatorVO.getReserveField2VO().getBankName();
        }
        if(functions.isValueNull(commonValidatorVO.getReserveField2VO().getCardname())){
            cardName = commonValidatorVO.getReserveField2VO().getCardname();
        }
        if(functions.isValueNull(commonValidatorVO.getReserveField2VO().getCardnumber())){
            cardNumber = commonValidatorVO.getReserveField2VO().getCardnumber();
        }
        if(functions.isValueNull(commonValidatorVO.getReserveField2VO().getLocation())){
            location = commonValidatorVO.getReserveField2VO().getLocation();
        }

        orderDesc= genericTransDetailsVO.getOrderDesc();
        status= (String) request.getAttribute("responceStatus");
        if(!status.contains("success")){
            errorMessage = status;
        }
        partnerId = commonValidatorVO.getParetnerId();
        redirectUrl = commonValidatorVO.getTransDetailsVO().getRedirectUrl();
        logoName = commonValidatorVO.getMerchantDetailsVO().getLogoName();
        partnerName = commonValidatorVO.getPartnerName();
        powerBy = commonValidatorVO.getMerchantDetailsVO().getPoweredBy();
        sisaLogoFlag = commonValidatorVO.getMerchantDetailsVO().getSisaLogoFlag();
        securityLogoFlag = commonValidatorVO.getMerchantDetailsVO().getSecurityLogo();
        partnerLogoFlag = commonValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
        merchantLogo = commonValidatorVO.getMerchantDetailsVO().getLogoName();
        log.debug("merchant logo name from confirmationpage jsp----"+merchantLogo);
        merchantLogoFlag = commonValidatorVO.getMerchantDetailsVO().getMerchantLogo();
        paymentMode = commonValidatorVO.getPaymentType();
        paymentBrand = commonValidatorVO.getCardType();
        pType = transaction.getPaymentModeForRest(paymentMode);
        cType = transaction.getPaymentBrandForRest(paymentBrand);
        if(commonValidatorVO.getAddressDetailsVO().getEmail() != null)
            email = commonValidatorVO.getAddressDetailsVO().getEmail();
        timestamp = String.valueOf(dateFormat.format(date));
        custAccountId = commonValidatorVO.getCustAccountId();
        customerId = commonValidatorVO.getCustomerId();
        bankAccountId = commonValidatorVO.getCustomerBankId();
        accountId = commonValidatorVO.getMerchantDetailsVO().getAccountId();
        if(commonValidatorVO.getEci()!=null)
            eci=commonValidatorVO.getEci();
        else
            eci="";

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname()))
            firstName=commonValidatorVO.getAddressDetailsVO().getFirstname();
        else
            firstName="";
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
            lastName=commonValidatorVO.getAddressDetailsVO().getLastname();
        else
            lastName="";

        errorName = (String) request.getAttribute("errorName");
        if(null != commonValidatorVO.getCardDetailsVO())
        {
            if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                cardBin = commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6);
                cardLast4Digits = commonValidatorVO.getCardDetailsVO().getCardNum().substring((commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4), commonValidatorVO.getCardDetailsVO().getCardNum().length());
            }
            if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getIBAN()))
            {
                bankAccountIban = ("******************" + commonValidatorVO.getCardDetailsVO().getIBAN().substring((commonValidatorVO.getCardDetailsVO().getIBAN().length() - 4), commonValidatorVO.getCardDetailsVO().getIBAN().length()));
                bankAccountBIC = commonValidatorVO.getCardDetailsVO().getBIC();
            }
        }
        if(null != commonValidatorVO.getReserveField2VO())
        {
            if (commonValidatorVO.getReserveField2VO() != null && functions.isValueNull(commonValidatorVO.getReserveField2VO().getAccountNumber()))
            {
                String accNum = commonValidatorVO.getReserveField2VO().getAccountNumber();
                accNum = accNum.substring(accNum.length() - 4, accNum.length());
                bankAccountNumber = ("********************" + accNum);
                bankAccountType = commonValidatorVO.getReserveField2VO().getAccountType();
                bankRoutingNumer = commonValidatorVO.getReserveField2VO().getRoutingNumber();
            }
        }
        if(functions.isValueNull(commonValidatorVO.getToken()))
        {
            token = commonValidatorVO.getToken();
        }

        if("Y".equalsIgnoreCase(merchantLogoFlag))
        {
            merchantLogo = (String)session.getAttribute("merchantLogoName");
            log.debug("merchantLogo session----"+merchantLogo);
            if(!functions.isValueNull(merchantLogo)){
                merchantLogo=commonValidatorVO.getMerchantDetailsVO().getMerchantLogoName();
            }
        }
        else
        {
            merchantLogo = commonValidatorVO.getMerchantDetailsVO().getLogoName();
            log.debug("merchantLogo vo----"+merchantLogo);
        }

        if(status!=null && (status.contains("Successful") || status.contains("success")) || status.contains("Approved") || status.contains("Success"))
        {
            respStatus = "Y";
            if (functions.isValueNull(errorName))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.valueOf(errorName));
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TRANSACTION_SUCCEED);
            }
            billingDesc = (String) request.getAttribute("displayName");
        }
        else if(status!=null && (status.contains("Pending") || status.contains("pending")))
        {
            respStatus = "P";
            if (functions.isValueNull(errorName))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.valueOf(errorName));
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SUCCESSFULL_PENDING_TRANSACTION);
            }

            billingDesc = (String) request.getAttribute("displayName");
        }
        else
        {
            respStatus = "N";
            if (functions.isValueNull(errorName))
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.valueOf(errorName));
                if (errorCodeVO == null)
                {
                    errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.valueOf(errorName));
                }
            }
            else
            {
                errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TRANSACTION_REJECTED);
            }

        }
        try
        {
            checksum = Checksum.generateChecksumForStandardKit(commonValidatorVO.getTrackingid(), commonValidatorVO.getTransDetailsVO().getOrderId(), commonValidatorVO.getTransDetailsVO().getAmount(), respStatus, commonValidatorVO.getMerchantDetailsVO().getKey());
        }
        catch (NoSuchAlgorithmException e)
        {

        }

        if(commonValidatorVO.getCardDetailsVO()!=null)
        {
            mandateId=commonValidatorVO.getCardDetailsVO().getMandateId();
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

    log.debug("version for IOS----" + commonValidatorVO.getVersion());
    log.debug("Device----" + commonValidatorVO.getDevice());

%>


<script type="text/javascript">

    var device = "<%=commonValidatorVO.getDevice()%>";

    if(device != null && device == 'ios')
    {
        window.webkit.messageHandlers.callbackHandler.postMessage({
            "trackingId" : "<%=trackingid%>",
            "status" : "<%=status%>",
            "firstName" : "<%=firstName%>",
            "lastName" : "<%=lastName%>",
            "checksum" : "<%=checksum%>",
            "desc" : "<%=orderDesc%>",
            "currency":"<%=commonValidatorVO.getTransDetailsVO().getCurrency()%>",
            "amount":"<%=commonValidatorVO.getTransDetailsVO().getAmount()%>",
            "tmpl_currency":"<%=commonValidatorVO.getAddressDetailsVO().getTmpl_currency()%>",
            "tmpl_amount":"<%=commonValidatorVO.getAddressDetailsVO().getTmpl_amount()%>",
            "timestamp":"<%=timestamp%>",
            "resultCode":"<%=errorCodeVO.getApiCode()%>",
            "resultDescription":"<%=errorCodeVO.getApiDescription()%>",
            "cardBin":"<%=cardBin%>",
            "cardLast4Digits":"<%=cardLast4Digits%>",
            "custEmail":"<%=email%>",
            "paymentMode":"<%=pType%>",
            "paymentBrand":"<%=cType%>",
            "eci":"<%=eci%>"
        });
    }
    else if( device != null && device == 'android')
    {
        var jsonObjectResponse = {};
        jsonObjectResponse["trackingId"] = "<%=trackingid%>";
        jsonObjectResponse["status"] = "<%=status%>";
        jsonObjectResponse["firstName"] = "<%=firstName%>";
        jsonObjectResponse["lastName"] = "<%=lastName%>";
        jsonObjectResponse["checksum"] = "<%=checksum%>";
        jsonObjectResponse["desc"] = "<%=orderDesc%>";
        jsonObjectResponse["currency"] = "<%=commonValidatorVO.getTransDetailsVO().getCurrency()%>";
        jsonObjectResponse["amount"] = "<%=commonValidatorVO.getTransDetailsVO().getAmount()%>";
        jsonObjectResponse["tmpl_currency"] = "<%=commonValidatorVO.getAddressDetailsVO().getTmpl_currency()%>";
        jsonObjectResponse["tmpl_amount"] = "<%=commonValidatorVO.getAddressDetailsVO().getTmpl_amount()%>";
        jsonObjectResponse["timestamp"] = "<%=timestamp%>";
        jsonObjectResponse["resultCode"] = "<%=errorCodeVO.getApiCode()%>";
        jsonObjectResponse["resultDescription"] = "<%=errorCodeVO.getApiDescription()%>";
        jsonObjectResponse["cardBin"] = "<%=cardBin%>";
        jsonObjectResponse["cardLast4Digits"] = "<%=cardLast4Digits%>";
        jsonObjectResponse["custEmail"] = "<%=email%>";
        jsonObjectResponse["paymentMode"] = "<%=pType%>";
        jsonObjectResponse["paymentBrand"] = "<%=cType%>";
        jsonObjectResponse["eci"] = "<%=eci%>";

        android.paymentResultListener(JSON.stringify(jsonObjectResponse));
    }


</script>
<!-- modal starts -->
<div class="modal show" style="display: inline-block">
    <div class="modal-dialog modal-sm modal-dialog-centered">
        <div class="modal-content">
            <div id="target" class="mainDiv" >
                <div class="header">
                    <div class="logo">
                        <%
                            if(functions.isValueNull(merchantLogoFlag) && merchantLogoFlag.equalsIgnoreCase("Y"))
                            {
                        %>
                        <img class="images-style1" style="width: 100%;" src="/images/merchant/<%=session.getAttribute("merchantLogoName")%>">
                        <%
                        }
                        else if(functions.isValueNull(partnerLogoFlag) && partnerLogoFlag.equalsIgnoreCase("Y"))
                        {
                        %>
                        <img class="images-style1" style="width: 100%;" src="/images/merchant/<%=commonValidatorVO.getMerchantDetailsVO().getLogoName()%>">
                        <%
                        }
                        else
                        {
                        %>
                        <img class="images-style1" style="width: 100%;" src="/images/merchant/<%=commonValidatorVO.getMerchantDetailsVO().getLogoName()%>">
                        <%
                            }
                        %>
                    </div>
                    <div class="name">
                        <div class="client-name tool"><%=commonValidatorVO.getMerchantDetailsVO().getCompany_name()%>
                            <div class="tooltiptext"><%=commonValidatorVO.getMerchantDetailsVO().getCompany_name()%> </div>
                        </div>
                        <div class="client-order tool"><%=commonValidatorVO.getTransDetailsVO().getOrderId()%>
                            <div class="tooltiptext"> <%=commonValidatorVO.getTransDetailsVO().getOrderId()%> </div>
                        </div>
                        <div class="client-amount"><%=currencySymbol%> <%=amount%></div>
                    </div>
                    <button type="button" id="closebtnConfirmation" class="close hide" data-dismiss="modal" aria-label="Close" onclick="closefunc()">
                        <span aria-hidden="true">&times;</span>
                    </button>
                </div>

                <div class="modal-body">
                    <div class="top-bar">
                        <div class="top-right" id="topRight" style="cursor: pointer">
                            <span onclick="backToHome()"><i class="fas fa-home"></i></span>
                        </div>
                        <div class="top-left" id="topLeft" >
                            <span id="backArrow" style="float: left;width: 20px;cursor: pointer" onclick="back()" ><i class="fas fa-angle-left"></i></span>
                            <div id="payMethod" style="font-weight: bold;text-align: center" class="truncate"><%=varTRANSACTIONSTATUS%></div>
                        </div>
                    </div>

                    <div id="options" style="height: 375px;overflow-y:auto;">
                        <!-- start of successs -->
                        <div class="check_mark" style="height: 375px;">
                            <div class="loader hide">
                            </div>
                            <%
                                if(status!=null && (status.contains("success") || status.contains("Successful") ))
                                {
                            %>

                            <!-- success -->
                            <div id="success_div" class="succces ">
                                <div class="circle-loader">
                                    <div class="checkmark draw"></div>
                                </div>
                                <div class="success-message"><%=varTRANSACTION_SUCCESSFUL%></div>
                            </div>
                            <!-- end of success -->

                            <%
                            }
                            else if(status.contains("Pending"))
                            {
                            %>

                            <!-- pending -->
                            <div id="success_div" class="succces ">
                                <div class="circle-loader">
                                    <div class="checkmark draw"></div>
                                </div>
                                <div class="success-message"><%=varTRANSACTION_PENDING%></div>
                            </div>
                            <!-- end of pending -->

                            <%
                            }
                            else
                            {
                            %>

                            <!-- failure -->
                            <div id="error_div" class="error ">
                                <svg version="1.1" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 130.2 130.2">
                                    <circle class="path circle" fill="none" stroke="#D06079" stroke-width="6" stroke-miterlimit="10" cx="65.1" cy="65.1" r="62.1"/>
                                    <line class="path line" fill="none" stroke="#D06079" stroke-width="6" stroke-linecap="round" stroke-miterlimit="10" x1="34.4" y1="37.9" x2="95.8" y2="92.3"/>
                                    <line class="path line" fill="none" stroke="#D06079" stroke-width="6" stroke-linecap="round" stroke-miterlimit="10" x1="95.8" y1="38" x2="34.4" y2="92.2"/>
                                </svg>
                                <div class="error-message"><%=varTRANSACTION_FAILED%></div>
                            </div>
                            <!-- end of failure -->

                            <%
                                }
                            %>

                            <div class="confirmation-div" style="max-height: 230px;">
                                <%
                                    if(functions.isValueNull(bankName))
                                    {
                                %>
                                <div class="confirm-box">
                                    <div class="confirmation-label"><%=varBankName%></div>
                                    <div class="confirmation-data">
                                        <%=bankName%>
                                    </div>
                                </div>
                                <%
                                    }
                                    if(functions.isValueNull(cardNumber))
                                    {
                                %>
                                <div class="confirm-box">
                                    <div class="confirmation-label"><%=varCardNumber%></div>
                                    <div class="confirmation-data">
                                        <%=cardNumber%>
                                    </div>
                                </div>
                                <%
                                    }
                                    if(functions.isValueNull(cardName))
                                    {
                                %>
                                <div class="confirm-box">
                                    <div class="confirmation-label"><%=varCardName%></div>
                                    <div class="confirmation-data">
                                        <%=cardName%>
                                    </div>
                                </div>
                                <%
                                    }
                                    if(functions.isValueNull(location))
                                    {
                                %>
                                <div class="confirm-box">
                                    <div class="confirmation-label"><%=varLocation%></div>
                                    <div class="confirmation-data">
                                        <%=location%>
                                    </div>
                                </div>
                                <%
                                    }
                                    if(functions.isValueNull(errorMessage))
                                    {
                                %>
                                <div class="confirm-box">
                                    <div class="confirmation-label"><%=varError%></div>
                                    <div class="confirmation-data">
                                        <%=errorMessage%>
                                    </div>
                                </div>
                                <%
                                    }
                                %>
                            </div>


                            <div class="pay-btn "  id="continuebutton">
                                <div class="pay-button"  onclick="success_button()">
                                    <span><%=varCONTINUE%></span>
                                    <%--<span style="font-size: 12px"> ( <%=varAUTOREDIRECT_IN%> <span id="lblCount"></span> ) </span>--%>
                                </div>
                            </div>

                        </div>


                    </div>


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
                            if(functions.isValueNull(sisaLogoFlag) && sisaLogoFlag.equalsIgnoreCase("Y"))
                            {
                        %>
                        <div class="footer-logo">
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

                </div>
            </div>
        </div>
    </div>
</div>
<!-- modal ends -->


<form id="confirmationForm" name="confirmationForm" action="<%=redirectUrl%>" method="post">

    <input type="hidden" name="trackingid" value="<%=trackingid%>">
    <input type="hidden" name="paymentId" value="<%=trackingid%>">       <!--//as per new requirenments-->
    <input type="hidden" name="desc" value="<%=orderid%>">
    <input type="hidden" name="merchantTransactionId" value="<%=orderid%>"> <!--//as per new requirenments-->
    <input type="hidden" name="status" value="<%=respStatus%>">
    <input type="hidden" name="checksum" value="<%=checksum%>">
    <%
        if (functions.isValueNull(billingDesc))
        {
    %>
    <input type="hidden" name="descriptor" value="<%=billingDesc%>">
    <%
        }
        if (functions.isValueNull(token))
        {
    %>
    <input type="hidden" name="token" value="<%=token%>">   <!--//as per new requirenments-->
    <input type="hidden" name="registrationId" value="<%=token%>">      <!--//as per new requirenments-->
    <%
        }
    %>
    <input type="hidden" name="firstName" value="<%=firstName%>">     <!--//as per new requirenments-->
    <input type="hidden" name="lastName" value="<%=lastName%>">     <!--//as per new requirenments-->
    <% if(functions.isValueNull(mandateId))
    {
    %>
    <input type="hidden" name="mandateid" value="<%=mandateId%>">
    <%
        }
    %>
    <input type="hidden" name="currency" value="<%=commonValidatorVO.getTransDetailsVO().getCurrency()%>">      <!--//as per new requirenments-->
    <input type="hidden" name="amount" value="<%=commonValidatorVO.getTransDetailsVO().getAmount()%>">
    <%
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_currency()))
        {
    %>
    <input type="hidden" name="tmpl_currency" value="<%=commonValidatorVO.getAddressDetailsVO().getTmpl_currency()%>"><!--//as per new requirenments-->
    <%
        }
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getTmpl_amount()))
        {
    %>
    <input type="hidden" name="tmpl_amount" value="<%=commonValidatorVO.getAddressDetailsVO().getTmpl_amount()%>">
    <%
        }
    %>
    <input type="hidden" name="timestamp" value="<%=timestamp%>">      <!--//as per new requirenments-->
    <input type="hidden" name="resultCode" value="<%=errorCodeVO.getApiCode()%>">      <!--//as per new requirenments-->
    <input type="hidden" name="resultDescription" value="<%=errorCodeVO.getApiDescription()%>">      <!--//as per new requirenments-->
    <%
        if(functions.isValueNull(cardBin)){
    %>
    <input type="hidden" name="cardBin" value="<%=cardBin%>">      <!--//as per new requirenments-->
    <input type="hidden" name="cardLast4Digits" value="<%=cardLast4Digits%>">     <!--//as per new requirenments-->
    <%
    }
    else if(functions.isValueNull(bankAccountIban)) {
    %>
    <input type="hidden" name="bankIBAN" value="<%=bankAccountIban%>">      <!--//as per new requirenments-->
    <input type="hidden" name="bankAccountBIC" value="<%=bankAccountBIC%>">     <!--//as per new requirenments-->
    <%
    }
    else if(functions.isValueNull(bankAccountNumber)){
    %>
    <input type="hidden" name="bankAccountNumber" value="<%=bankAccountNumber%>">      <!--//as per new requirenments-->
    <input type="hidden" name="bankAccountType" value="<%=bankAccountType%>">     <!--//as per new requirenments-->
    <input type="hidden" name="bankRoutingNumer" value="<%=bankRoutingNumer%>">     <!--//as per new requirenments-->



    <%
        }

        if (commonValidatorVO.getCustAccountId() != null)
        {
    %>
    <input type="hidden" name="custAccountId" value="<%=custAccountId%>">     <!--//as per new requirenments-->
    <%
        }
    %>
    <%
        if (functions.isValueNull(email))
        {
    %>
    <input type="hidden" name="custEmail" value="<%=email%>">     <!--//as per new requirenments-->
    <%
        }
        if(functions.isValueNull(eci))
        {
    %>
    <input type="hidden" name="eci" value="<%=eci%>">     <!--//as per new requirenments-->
    <%
        }
    %>
    <input type="hidden" name="paymentMode" value="<%=pType%>">     <!--//as per new requirenments-->
    <input type="hidden" name="paymentBrand" value="<%=cType%>">     <!--//as per new requirenments-->

    <%
        if (functions.isValueNull(customerId))
        {
    %>

    <input type="hidden" name="custMerchantId" value="<%=customerId%>">
    <%
        }
        if (functions.isValueNull(bankAccountId))
        {
    %>
    <input type="hidden" name="custBankId" value="<%=bankAccountId%>">
    <%
        }
        if (functions.isValueNull(commonValidatorVO.getCommissionPaidToUser()))
        {
    %>
    <input type="hidden" name="commissionPaidToUser" value="<%=commonValidatorVO.getCommissionPaidToUser()%>">
    <%
        }
        if (functions.isValueNull(commonValidatorVO.getCommPaidToUserCurrency()))
        {
    %>
    <input type="hidden" name="commissionPaidToUserCurrency" value="<%=commonValidatorVO.getCommPaidToUserCurrency()%>">
    <%
        }
    %>
</form>

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