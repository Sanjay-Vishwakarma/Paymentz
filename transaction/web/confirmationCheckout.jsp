<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericTransDetailsVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>

<%@ page import="com.directi.pg.*" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericTransDetailsVO" %>
<%@ page import="com.payment.exceptionHandler.errorcode.ErrorCodeUtils" %>
<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName" %>
<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="java.security.NoSuchAlgorithmException" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="com.manager.vo.MarketPlaceVO" %>
<%@ page import="java.util.*" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ page import="com.payment.validators.CommonInputValidator" %>
<%--
Created by IntelliJ IDEA.
User: Rihen
Date: 6/21/2018
Time: 2:20 PM
To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>


<%
    Logger log = new Logger("confirmationCheckout.jsp");
    Functions functions=new Functions();
    CommonValidatorVO commonValidatorVO= (CommonValidatorVO) request.getAttribute("transDetail");
    MerchantDetailsVO merchantDetailsVO=commonValidatorVO.getMerchantDetailsVO();
    CommonInputValidator commonInputValidator=new CommonInputValidator();
    Enumeration enumeration=session.getAttributeNames();
    List<String> sessionKeyList=new ArrayList<>();
    while (enumeration.hasMoreElements())
    {
        sessionKeyList.add((String)enumeration.nextElement());
    }
    if(!functions.isValueNull(commonValidatorVO.getVersion()))
        commonValidatorVO.setVersion("2");
    if(!sessionKeyList.contains("bodypanelfont_color") || !sessionKeyList.contains("panelheading_color") || !sessionKeyList.contains("bodybgcolor") || !sessionKeyList.contains("navigation_font_color") || !sessionKeyList.contains("merchantLogoName") || !sessionKeyList.contains("panelbody_color") || !sessionKeyList.contains("bodyfgcolor") || !sessionKeyList.contains("icon_color") || !sessionKeyList.contains("paymentMap") || !sessionKeyList.contains("textbox_color") || !sessionKeyList.contains("mainbackgroundcolor") || !sessionKeyList.contains("headpanelfont_color"))
    {
        PartnerDetailsVO partnerDetailsVO = new PartnerDetailsVO();
        if (merchantDetailsVO != null)
        {
            partnerDetailsVO.setPartnerId(merchantDetailsVO.getPartnerId());
            partnerDetailsVO.setPartnertemplate(merchantDetailsVO.getPartnertemplate());
            commonInputValidator.setAllTemplateInformationRelatedToMerchant(merchantDetailsVO, commonValidatorVO.getVersion());
            commonInputValidator.setAllTemplateInformationRelatedToPartner(partnerDetailsVO, commonValidatorVO.getVersion());
            commonValidatorVO.setPartnerDetailsVO(partnerDetailsVO);
            commonInputValidator.getPaymentPageTemplateDetails(commonValidatorVO, session);
        }
    }
%>
<!DOCTYPE html>
<html lang="en">
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
    <link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap-4.4.1/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.1.0/css/all.css" integrity="sha384-lKuwvrZot6UHsBSfcMvOkWwlCMgc0TaWr+30HWe3a4ltaBwTZhyTEggF5tJv8tbt" crossorigin="anonymous">
    <link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap-float-label.min.css"/>
    <link rel="stylesheet" href="/merchant/transactionCSS/css/styles.css"/>
    <link rel="stylesheet" href="/merchant/transactionCSS/css/success.css"/>
    <link rel="stylesheet" href="/merchant/transactionCSS/css/fail.css"/>

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
        /* end of header */


        /* body */
        .top-bar{               /* navigation bar background and font color */
        <%=session.getAttribute("panelbody_color")!=null?"background-color:"+session.getAttribute("panelbody_color").toString()+"!important":""%>;
        <%=session.getAttribute("navigation_font_color")!=null?"color:"+session.getAttribute("navigation_font_color").toString()+"!important":""%>;
        <%=session.getAttribute("NEW_CHECKOUT_NAVIGATIONBAR_COLOR") != null ? "background-color:"+session.getAttribute("NEW_CHECKOUT_NAVIGATIONBAR_COLOR").toString() + "!important" : "" %>;
        <%=session.getAttribute("NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR")!=null?"color:"+session.getAttribute("NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR").toString()+"!important":""%>;
        }

        .pay-button{             /* pay button background and font color */
        <%=session.getAttribute("textbox_color")!=null?"background-color:"+session.getAttribute("textbox_color").toString()+"!important":""%>;
        <%=session.getAttribute("headpanelfont_color")!=null?"color:"+session.getAttribute("headpanelfont_color").toString()+"!important":""%>;
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
        <%=session.getAttribute("NEW_CHECKOUT_FOOTER_FONT_COLOR")!=null?"color:"+session.getAttribute("NEW_CHECKOUT_FOOTER_FONT_COLOR").toString()+"!important":""%>;
        <%=session.getAttribute("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR")!=null?"background-color:"+session.getAttribute("NEW_CHECKOUT_FOOTER_BACKGROUND_COLOR").toString()+"!important":"background-color:#7eccad!important"%>;
        }
        /* end of footer */
    </style>

</head>
<body style="font-family: Raleway;" class="background">


<%


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
            else if ("sp".equalsIgnoreCase(sLanguage[0]))
            {
                rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_sp");
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

    String varTRANSACTIONSTATUS         = rb1.getString("VAR_TRANSACTION_STATUS");
    String varCONTINUE                  = rb1.getString("VAR_CONTINUE");
    String varTRANSACTION_SUCCESSFUL    = rb1.getString("VAR_TRANSACTION_SUCCESSFUL");
    String varTRANSACTION_FAILED        = rb1.getString("VAR_TRANSACTION_FAILED");
    String varTRANSACTION_PENDING       = rb1.getString("VAR_TRANSACTION_PENDING");
    String varTRACKING_ID               = rb1.getString("VAR_TRACKING_ID");
    String varSTATUS                    = rb1.getString("VAR_STATUS");
    String varAUTOREDIRECT_IN           = rb1.getString("VAR_AUTOREDIRECT_IN");
    String varBillingDescriptor         = rb1.getString("VAR_BILLING_DESCRIPTOR");
    String VAR_REVERSAL_SUCCESSFUL      = rb1.getString("VAR_REVERSAL_SUCCESSFUL");


    ErrorCodeVO errorCodeVO         = new ErrorCodeVO();
    ErrorCodeUtils errorCodeUtils   = new ErrorCodeUtils();
    String trackingid   = "";
    String orderid      = "";
    String amount       = commonValidatorVO.getDisplayAmount();
    String currency     = commonValidatorVO.getDisplayCurrency();
    String orderDesc    = "";
    String status       = "";
    String billingDesc  = "";
    String bgcolor      = "txtboxerror";
    String partnerId    = "";
    String logoName     = "";
    String partnerName = "";
    String powerBy      = "";
    String redirectUrl  ="";
    String sisaLogoFlag     = "Y";
    String securityLogoFlag = "N";
    String partnerLogoFlag  = "Y";
    String merchantLogo     = "";
    String merchantLogoFlag = "";
    String checksum         = "";
    String respStatus       = "N";
    String token            = "";
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
    String splitTrans="";
    String redirectMethod="";
    String bankCode="";
    String bankDescription="";
    String remark="";

    if(commonValidatorVO!=null && request.getAttribute("responceStatus")!=null /*&& request.getAttribute("displayName")!=null*/)
    {

        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        Date date = new Date();
        Transaction transaction = new Transaction();

        GenericTransDetailsVO genericTransDetailsVO = commonValidatorVO.getTransDetailsVO();
        trackingid  = commonValidatorVO.getTrackingid();
        orderid     = genericTransDetailsVO.getOrderId();

        if (commonValidatorVO.getAddressDetailsVO() != null && commonValidatorVO.getAddressDetailsVO().getTmpl_amount() != null)
            amount = commonValidatorVO.getAddressDetailsVO().getTmpl_amount();
        else
            amount = commonValidatorVO.getTransDetailsVO().getAmount();

/*if(functions.isValueNull(commonValidatorVO.getDisplayCurrency()))
currency = commonValidatorVO.getDisplayCurrency();*/

        log.error("inside tmpl currency if"+commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
        log.error("inside  currency ELSE"+commonValidatorVO.getTransDetailsVO().getCurrency());


        if (commonValidatorVO.getAddressDetailsVO() != null && commonValidatorVO.getAddressDetailsVO().getTmpl_currency() != null)
        {
            log.error("inside tmpl currency if"+commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
            currency = commonValidatorVO.getAddressDetailsVO().getTmpl_currency();
        }
        else
        {
            log.error("inside  currency ELSE"+commonValidatorVO.getTransDetailsVO().getCurrency());
            currency = commonValidatorVO.getTransDetailsVO().getCurrency();
        }

        orderDesc           = genericTransDetailsVO.getOrderDesc();
        status              = (String) request.getAttribute("responceStatus");
        partnerId           = commonValidatorVO.getParetnerId();
        redirectUrl         = commonValidatorVO.getTransDetailsVO().getRedirectUrl();
        logoName            = commonValidatorVO.getMerchantDetailsVO().getLogoName();
        partnerName         = commonValidatorVO.getPartnerName();
        powerBy             = commonValidatorVO.getMerchantDetailsVO().getPoweredBy();
        sisaLogoFlag        = commonValidatorVO.getMerchantDetailsVO().getSisaLogoFlag();
        securityLogoFlag    = commonValidatorVO.getMerchantDetailsVO().getSecurityLogo();
        partnerLogoFlag     = commonValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
        merchantLogo        = commonValidatorVO.getMerchantDetailsVO().getLogoName();
        log.debug("merchant logo name from confirmationpage jsp----"+merchantLogo);
        merchantLogoFlag    = commonValidatorVO.getMerchantDetailsVO().getMerchantLogo();
        paymentMode         = commonValidatorVO.getPaymentType();
        paymentBrand        = commonValidatorVO.getCardType();
        pType               = transaction.getPaymentModeForRest(paymentMode);
        cType               = transaction.getPaymentBrandForRest(paymentBrand);
        if(commonValidatorVO.getAddressDetailsVO().getEmail() != null)
            email = commonValidatorVO.getAddressDetailsVO().getEmail();
        timestamp       = String.valueOf(dateFormat.format(date));
        custAccountId   = commonValidatorVO.getCustAccountId();
        customerId      = commonValidatorVO.getCustomerId();
        bankAccountId   = commonValidatorVO.getCustomerBankId();
        accountId       = commonValidatorVO.getMerchantDetailsVO().getAccountId();
        remark          = commonValidatorVO.getReason();
        if(functions.isValueNull(remark))
            remark = (String) request.getAttribute("remark");
        if(commonValidatorVO.getEci()!=null)
            eci = commonValidatorVO.getEci();
        else
            eci = "";

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname()))
            firstName   = commonValidatorVO.getAddressDetailsVO().getFirstname();
        else
            firstName="";
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
            lastName    = commonValidatorVO.getAddressDetailsVO().getLastname();
        else
            lastName="";
        if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getRedirectMethod()))
            redirectMethod=commonValidatorVO.getTransDetailsVO().getRedirectMethod();

        bankCode        = commonValidatorVO.getBankCode();
        bankDescription = commonValidatorVO.getBankDescription();
        errorName       = (String) request.getAttribute("errorName");
        if(null != commonValidatorVO.getCardDetailsVO())
        {
            if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                cardBin         = commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6);
                cardLast4Digits = commonValidatorVO.getCardDetailsVO().getCardNum().substring((commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4), commonValidatorVO.getCardDetailsVO().getCardNum().length());
            }
            if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getIBAN()))
            {
                bankAccountIban = ("******************" + commonValidatorVO.getCardDetailsVO().getIBAN().substring((commonValidatorVO.getCardDetailsVO().getIBAN().length() - 4), commonValidatorVO.getCardDetailsVO().getIBAN().length()));
                bankAccountBIC  = commonValidatorVO.getCardDetailsVO().getBIC();
            }
        }
        if(null != commonValidatorVO.getReserveField2VO())
        {
            if (commonValidatorVO.getReserveField2VO() != null && functions.isValueNull(commonValidatorVO.getReserveField2VO().getAccountNumber()))
            {
                String accNum       = commonValidatorVO.getReserveField2VO().getAccountNumber();
                accNum              = accNum.substring(accNum.length() - 4, accNum.length());
                bankAccountNumber   = ("********************" + accNum);
                bankAccountType     = commonValidatorVO.getReserveField2VO().getAccountType();
                bankRoutingNumer    = commonValidatorVO.getReserveField2VO().getRoutingNumber();
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
                merchantLogo    = commonValidatorVO.getMerchantDetailsVO().getMerchantLogoName();
            }
        }
        else
        {
            merchantLogo = commonValidatorVO.getMerchantDetailsVO().getLogoName();
            log.debug("merchantLogo vo----"+merchantLogo);
        }

        log.debug("merchantLogo vo----"+status);

        if(status != null && (status.contains("Successful") || status.contains("success")) || status.contains("Approved") || status.contains("Success"))
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
        else if(status != null && (status.contains("Pending") || status.contains("pending")))
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
            mandateId   = commonValidatorVO.getCardDetailsVO().getMandateId();
        }

    }

    log.error("amount---->"+commonValidatorVO.getTransDetailsVO().getAmount());
    log.error("currency---->"+commonValidatorVO.getTransDetailsVO().getCurrency());
    log.error("currency 2323---->"+commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
    log.error("trackingid---->"+commonValidatorVO.getTrackingid());
    log.error("currency---->"+ currency);

//session.invalidate();
    String currencySymbol = currency;
    if(rb.containsKey(currencySymbol))
        currencySymbol = rb.getString(currencySymbol);

    if(functions.isValueNull(currency))
    {
        NumberFormat formatter = null;
        if (currency.equalsIgnoreCase("JPY"))
        {
            formatter   = NumberFormat.getCurrencyInstance(Locale.JAPAN);
            double dbl  = Double.valueOf(amount);
            amount      = formatter.format(dbl);
            currencySymbol = "";
        }
        if (currency.equalsIgnoreCase("EUR"))
        {
            formatter   = NumberFormat.getCurrencyInstance(Locale.FRANCE);
            double dbl  = Double.valueOf(amount);
            amount      = formatter.format(dbl);
            currencySymbol = "";
        }
        if (currency.equalsIgnoreCase("GBP"))
        {
            formatter   = NumberFormat.getCurrencyInstance(Locale.UK);
            double dbl  = Double.valueOf(amount);
            amount      = formatter.format(dbl);
            currencySymbol = "";
        }
        if (currency.equalsIgnoreCase("USD"))
        {
            formatter   = NumberFormat.getCurrencyInstance(Locale.US);
            double dbl  = Double.valueOf(amount);
            amount      = formatter.format(dbl);
            currencySymbol = "";
        }
    }
    if(functions.isValueNull(commonValidatorVO.getFailedSplitTransactions()))
        splitTrans=commonValidatorVO.getFailedSplitTransactions();

    log.error("----- Device version ---- " + commonValidatorVO.getVersion());
    log.error("----- Device ---- " + commonValidatorVO.getDevice());

    log.error("trackingid = "+trackingid);
    log.error("status = "+status);
    log.error("splitTransaction = "+splitTrans);
    log.error("firstName = "+firstName);
    log.error("lastName = "+lastName);
    log.error("checksum = "+checksum);
    log.error("orderDesc = "+orderDesc);
    log.error("currency = "+commonValidatorVO.getTransDetailsVO().getCurrency());
    log.error("amount = "+commonValidatorVO.getTransDetailsVO().getAmount());
    log.error("tmpl_currency = "+commonValidatorVO.getAddressDetailsVO().getTmpl_currency());
    log.error("tmpl_amount = "+commonValidatorVO.getAddressDetailsVO().getTmpl_amount());
    log.error("timestamp = "+timestamp);
    log.error("resultCode = "+errorCodeVO.getApiCode());
    log.error("resultDescription = "+errorCodeVO.getApiDescription());
    log.error("cardBin = "+cardBin);
    log.error("cardLast4Digits = "+cardLast4Digits);
    log.error("custEmail = "+email);
    log.error("paymentMode = "+pType);
    log.error("paymentBrand = "+cType);
    log.error("eci = "+eci);
%>

<script type = "text/javascript" src="/merchant/transactionCSS/js/anime.min.js"></script> <!-- loader animation-->
<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.min.js"></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/bootstrap-4.4.1/js/bootstrap.min.js" ></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/content.min.js"></script>

<script type="text/javascript">

    $(function () {
        var seconds = 5;
        $("#lblCount").html(seconds);
        var interval = setInterval(function () {
            if(seconds != 0)
            {seconds--;}
            $("#lblCount").html(seconds);
            if (seconds == 0) {
                success_button();
                //                document.confirmationForm.submit();
                clearInterval(interval);
            }
        }, 1000);
    });

    function success_button(){
        console.log("in continue button");
        getResponseFromCheckout('<%=redirectUrl%>',document.getElementsByName('confirmationForm')[0]);

        $("#confirmationForm").submit();
    }

    // success animation
    setTimeout(explode,1000);
    function explode(){
        $('.circle-loader').toggleClass('load-complete');
        $('.checkmark').toggle();
    }
</script>

<script type="text/javascript">

    var device = "<%=commonValidatorVO.getDevice()%>";

    if(device != null && device == 'ios')
    {
        window.webkit.messageHandlers.callbackHandler.postMessage({
            "trackingId" : "<%=trackingid%>",
            "status" : "<%=status%>",
            "splitTransaction" : "<%=splitTrans%>",
            <%--"billingDescriptor" : "<%=billingDesc%>",--%>
            "firstName" : "<%=firstName%>",
            "lastName" : "<%=lastName%>",
            "checksum" : "<%=checksum%>",
            <%--"token" : "<%=token%>",--%>
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
            <%--"custAccountId":"<%=commonValidatorVO.getCustAccountId()%>",--%>
            "custEmail":"<%=email%>",
            <%--"custMerchantId":"<%=commonValidatorVO.getCustomerId()%>",--%>
            <%--"custBankId":"<%=commonValidatorVO.getCustomerBankId()%>",--%>
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
        jsonObjectResponse["splitTransaction"] = "<%=splitTrans%>";
        <%--jsonObjectResponse["billingDescriptor"] = "<%=billingDesc%>";--%>
        jsonObjectResponse["firstName"] = "<%=firstName%>";
        jsonObjectResponse["lastName"] = "<%=lastName%>";
        jsonObjectResponse["checksum"] = "<%=checksum%>";
        <%--jsonObjectResponse["token"] = "<%=token%>";--%>
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
                    <%
                        String padForAmt = "";
                        if(functions.isValueNull(merchantLogoFlag) && merchantLogoFlag.equalsIgnoreCase("Y"))
                        {
                            padForAmt = "padding: 20px 0px;";
                            String merchantLogoName=(String)session.getAttribute("merchantLogoName");
                            if(!functions.isValueNull(merchantLogoName))
                                merchantLogoName=commonValidatorVO.getMerchantDetailsVO().getMerchantLogoName();
                    %>
                    <div class="logo">
                        <img class="images-style1" style="width: 100%;" src="/images/merchant/<%=merchantLogoName%>">
                    </div>
                    <%
                    }
                    else if(functions.isValueNull(partnerLogoFlag) && partnerLogoFlag.equalsIgnoreCase("Y"))
                    {
                        padForAmt = "padding: 20px 0px;";
                    %>
                    <div class="logo">
                        <img class="images-style1" style="width: 100%;" src="/images/merchant/<%=commonValidatorVO.getMerchantDetailsVO().getLogoName()%>">
                    </div>
                    <%
                        }
                    %>
                    <div class="name" style="text-align: right">
                        <%
                            String visibility = "block";
                            String amountStyle= "";
                            if(commonValidatorVO.getMerchantDetailsVO().getMerchantOrderDetailsDisplay().equalsIgnoreCase("N"))
                            {
                                visibility  = "none";
                                amountStyle = "font-size: 25px;"+padForAmt;
                            }
                        %>
                        <div class="client-name" style="display: <%=visibility%>" >
                            <span class="tool"><%=commonValidatorVO.getMerchantDetailsVO().getCompany_name()%>
                                <div class="tooltiptext"><%=commonValidatorVO.getMerchantDetailsVO().getCompany_name()%> </div>
                            </span>
                        </div>
                        <div class="client-order" style="display: <%=visibility%>" >
                            <span class="tool"><%=commonValidatorVO.getTransDetailsVO().getOrderId()%>
                                <div class="tooltiptext"> <%=commonValidatorVO.getTransDetailsVO().getOrderId()%> </div>
                            </span>
                        </div>
                        <div class="client-amount" style="<%=amountStyle%>" > <%=currencySymbol%> <%=amount%> </div>
                    </div>
                    <%--<button type="button" id="close" class="close" aria-label="Close"  onclick="m()">    <!-- onclick="activeModal.dismiss('Cross click')"-->
                    <span aria-hidden="true">&times;</span>
                    </button>--%>
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

                    <div id="options" style="height: 350px;overflow-y:auto;">
                        <!-- start of successs -->
                        <div class="check_mark" style="height: 350px;">
                            <div class="loader hide">
                            </div>
                            <%
                                if(status!=null && (status.contains("success") || status.contains("Successful") ))
                                {
                            %>

                            <!-- success -->
                            <div id="success_div" class="succces ">
                                <%--<div class="sa-icon sa-success animate">
                                <span class="sa-line sa-tip animateSuccessTip"></span>
                                <span class="sa-line sa-long animateSuccessLong"></span>
                                <div class="sa-placeholder"></div>
                                <div class="sa-fix"></div>
                                </div>--%>
                                <div class="circle-loader">
                                    <div class="checkmark draw"></div>
                                </div>
                                <div class="success-message"><%=varTRANSACTION_SUCCESSFUL%></div>
                            </div>
                            <!-- end of success -->

                            <%
                            }
                            else if(status!=null && (status.contains("reversed") || status.contains("Reversed") ))
                            {
                            %>

                            <!-- success -->
                            <div id="success_div" class="succces ">
                                <%--<div class="sa-icon sa-success animate">
                                <span class="sa-line sa-tip animateSuccessTip"></span>
                                <span class="sa-line sa-long animateSuccessLong"></span>
                                <div class="sa-placeholder"></div>
                                <div class="sa-fix"></div>
                                </div>--%>
                                <div class="circle-loader">
                                    <div class="checkmark draw"></div>
                                </div>
                                <div class="success-message"><%=VAR_REVERSAL_SUCCESSFUL%></div>
                            </div>
                            <!-- end of success -->

                            <%
                            }
                            else if(status.contains("Pending") || status.contains("pending"))
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

                            <div class="confirmation-div">
                                <div class="confirm-box">
                                    <div class="confirmation-label"><%=varTRACKING_ID%></div>
                                    <div class="confirmation-data">
                                        <%=trackingid%>
                                    </div>
                                </div>
                                <div class="confirm-box">
                                    <div class="confirmation-label"><%=varSTATUS%></div>
                                    <div class="confirmation-data">
                                        <%=status%>
                                    </div>
                                </div>
                                <%
                                    if(functions.isValueNull(billingDesc)){
                                %>
                                <div class="confirm-box">
                                    <div class="confirmation-label"><%=varBillingDescriptor%></div>
                                    <div class="confirmation-data">
                                        <%=billingDesc%>
                                    </div>
                                </div>
                                <%
                                    }
                                %>
                            </div>


                            <div class="pay-btn "  id="continuebutton">
                                <div class="pay-button"  onclick="success_button()">
                                    <span><%=varCONTINUE%></span>
                                    <span style="font-size: 12px"> ( <%=varAUTOREDIRECT_IN%> <span id="lblCount"></span> ) </span>
                                </div>
                            </div>

                        </div>
                        <!-- end of success -->

                    </div>

                    <%
                        String align = "";
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
                            else if((functions.isValueNull(powerBy) && ("N").equalsIgnoreCase(powerBy)) &&
                                    (functions.isValueNull(securityLogoFlag) && ("N").equalsIgnoreCase(securityLogoFlag))){
                                align="text-align: center";
                            }
                            else {
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
                    <%
                        }
                    %>
                    <!-- footer  -->

                </div>
            </div>
        </div>
    </div>
</div>
<!-- modal ends -->

<%if("GET".equalsIgnoreCase(redirectMethod)){%>
<form id="confirmationForm" name="confirmationForm" action="<%=redirectUrl%>" method="get">
        <%}else{%>
    <form id="confirmationForm" name="confirmationForm" action="<%=redirectUrl%>" method="post">
        <%}%>

        <input type="hidden" name="trackingid" value="<%=trackingid%>">
        <input type="hidden" name="paymentId" value="<%=trackingid%>">       <!--//as per new requirenments-->
        <input type="hidden" name="desc" value="<%=orderid%>">
        <input type="hidden" name="merchantTransactionId" value="<%=orderid%>"> <!--//as per new requirenments-->
        <input type="hidden" name="status" value="<%=respStatus%>">
        <input type="hidden" name="checksum" value="<%=checksum%>">
        <%
            if (functions.isValueNull(splitTrans))
            {
        %>
        <input type="hidden" name="splitTransaction" value="<%=splitTrans%>">

        <%
            }
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
        <%if(functions.isValueNull(remark)){%>
        <input type="hidden" name="remark" value="<%=remark%>">      <!--//as per new requirenments-->
        <%}%>
        <%if(functions.isValueNull(bankCode) && functions.isValueNull(bankDescription)){%>
        <input type="hidden" name="bankCode" value="<%=bankCode%>">      <!--//as per phoneix new requirenments-->
        <input type="hidden" name="bankDescription" value="<%=bankDescription%>"><!--//as per phoneix new requirenments-->
        <%}%>
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
        /*if(functions.isValueNull(eci))
        {*/
        %>
        <input type="hidden" name="eci" value="<%=eci%>">     <!--//as per new requirenments-->
        <%
            /*}*/
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
            if(commonValidatorVO.getMarketPlaceVOList()!=null && commonValidatorVO.getMarketPlaceVOList().size()>0)
            {
                for(MarketPlaceVO marketPlaceVO:commonValidatorVO.getMarketPlaceVOList())
                {
                    if (functions.isValueNull(marketPlaceVO.getTrackingid()))
                        out.println("<input type=\"hidden\" name=\"MP_Trackingid[]\" value=\"" + marketPlaceVO.getTrackingid() + "\">");
                    if (functions.isValueNull(marketPlaceVO.getOrderid()))
                        out.println("<input type=\"hidden\" name=\"MP_Orderid[]\" value=\"" + marketPlaceVO.getOrderid() + "\">");
                    if (functions.isValueNull(marketPlaceVO.getAmount()))
                        out.println("<input type=\"hidden\" name=\"MP_Amount[]\" value=\"" + marketPlaceVO.getAmount() + "\">");
                }
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