<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericAddressDetailsVO" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericTransDetailsVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>

<%@ page import="com.directi.pg.*" %>
<%@ page import="com.payment.exceptionHandler.errorcode.ErrorCodeUtils" %>
<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName" %>
<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO" %>
<%@ page import="java.security.NoSuchAlgorithmException" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<%@ page import="com.manager.vo.TokenResponseVO" %>
<%@ page import="com.manager.vo.TokenDetailsVO" %>
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
            background-color: #7eccad;
            color: white;
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
            color: #000000;
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
<%--<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>--%>
<%--<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js" integrity="sha384-smHYKdLADwkXOn1EmN1qk/HfnUcbVRZyYmZ4qpPea6sjB/pTJ0euyQp0Mk8ck+5T" crossorigin="anonymous"></script>--%>
<script type = "text/javascript" src="/merchant/transactionCSS/js/anime.min.js"></script> <!-- loader animation-->
<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.min.js"></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/bootstrap/js/bootstrap.min.js" ></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.cardcheck.js"></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.cardcheck.min.js"></script>
<%--<script type = "text/javascript" src="/merchant/transactionCSS/js/content.js"></script>--%>
<script type = "text/javascript" src="/merchant/transactionCSS/js/content.min.js"></script>

<script type="text/javascript">

    /*$(function () {
        var seconds = 5;
        $("#lblCount").html(seconds);
        var interval = setInterval(function () {
            if(seconds != 0)
            {seconds--;}
            $("#lblCount").html(seconds);
            if (seconds == 0) {
                success_button();
                document.confirmationForm.submit();
                clearInterval(interval);
            }
        }, 1000);
    });*/

    function success_button(){
        console.log("in continue button");
        closefunc();
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
    CommonValidatorVO commonValidatorVO = (CommonValidatorVO) request.getAttribute("transDetail");
    Logger log = new Logger("confirmationCardTokenization.jsp");

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

    ErrorCodeVO errorCodeVO = new ErrorCodeVO();
    ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
    String status= "";
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
    String cardBin = "";
    String cardLast4Digits = "";
    String cardType="";
    String timestamp = "";
    String email = "";
    String customerId = "";
    String firstName="";
    String lastName="";
    String redirectMethod="";
    String cardholderName="";
    String errorName = "";

    TokenResponseVO tokenResponseVO = null;
    tokenResponseVO=(TokenResponseVO)request.getAttribute("tokenDetails");

    String tokenStatus = "";
    String registrationId = "";
    String tokenID = "";

    if(tokenResponseVO != null){
        if(functions.isValueNull(tokenResponseVO.getStatus())){
            tokenStatus = tokenResponseVO.getStatus();
        }
        if(functions.isValueNull(tokenResponseVO.getRegistrationToken())){
            registrationId = tokenResponseVO.getRegistrationToken();
        }
        if(functions.isValueNull(tokenResponseVO.getTokenId())){
            tokenID = tokenResponseVO.getTokenId();
        }

    }

    if(commonValidatorVO!=null)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        Date date = new Date();
        Transaction transaction = new Transaction();

        GenericTransDetailsVO genericTransDetailsVO=commonValidatorVO.getTransDetailsVO();

        logoName = commonValidatorVO.getMerchantDetailsVO().getLogoName();
        partnerName = commonValidatorVO.getPartnerName();
        powerBy = commonValidatorVO.getMerchantDetailsVO().getPoweredBy();
        sisaLogoFlag = commonValidatorVO.getMerchantDetailsVO().getSisaLogoFlag();
        securityLogoFlag = commonValidatorVO.getMerchantDetailsVO().getSecurityLogo();
        partnerLogoFlag = commonValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
        merchantLogo = commonValidatorVO.getMerchantDetailsVO().getLogoName();
        log.debug("merchant logo name from confirmationpage jsp----"+merchantLogo);
        merchantLogoFlag = commonValidatorVO.getMerchantDetailsVO().getMerchantLogo();
        redirectUrl = genericTransDetailsVO.getRedirectUrl();
        cardType=commonValidatorVO.getCardType();

        timestamp = String.valueOf(dateFormat.format(date));

        if(functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getFirstname()))
            firstName=commonValidatorVO.getAddressDetailsVO().getFirstname();
        else
            firstName="";
        if (functions.isValueNull(commonValidatorVO.getAddressDetailsVO().getLastname()))
            lastName=commonValidatorVO.getAddressDetailsVO().getLastname();
        else
            lastName="";
        System.out.println("genericTransDetailsVO-------->"+genericTransDetailsVO.getRedirectMethod());
        if (functions.isValueNull(genericTransDetailsVO.getRedirectMethod()))
            redirectMethod=genericTransDetailsVO.getRedirectMethod();
        else
            redirectMethod="";
        if (functions.isValueNull(commonValidatorVO.getCustomerId()))
            customerId=commonValidatorVO.getCustomerId();

        errorName = (String) request.getAttribute("errorName");
        if(null != commonValidatorVO.getCardDetailsVO())
        {
            if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                cardBin = commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6);
                cardLast4Digits = commonValidatorVO.getCardDetailsVO().getCardNum().substring((commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4), commonValidatorVO.getCardDetailsVO().getCardNum().length());
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

        if(tokenStatus!=null && tokenStatus.equalsIgnoreCase("success"))
        {
            respStatus = "Y";
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.SUCCESSFUL_TOKEN_GENERATION);
        }
        else
        {
            respStatus = "N";
            errorCodeVO = errorCodeUtils.getErrorCodeFromName(ErrorName.TOKEN_CREATION_FAILED);
        }

        try
        {
            checksum = Checksum.generateChecksumForCardRegistration(getValue(commonValidatorVO.getMerchantDetailsVO().getMemberId()),tokenStatus, getValue(commonValidatorVO.getMerchantDetailsVO().getKey()));
            //checksum = Checksum.generateChecksumForCardRegistration(getValue(commonValidatorVO.getMerchantDetailsVO().getMemberId()), getValue(commonValidatorVO.getMerchantDetailsVO().getKey()), String.valueOf(response));
        }
        catch (NoSuchAlgorithmException e)
        {

        }
    }
%>

<script type="text/javascript">
    var device = "<%=commonValidatorVO.getDevice()%>";
    if(device != null && device == 'ios')
    {
        window.webkit.messageHandlers.callbackHandler.postMessage({
            "status" : "<%=tokenStatus%>",
            "firstName" : "<%=firstName%>",
            "lastName" : "<%=lastName%>",
            "checksum" : "<%=checksum%>",
            "timestamp":"<%=timestamp%>",
            "resultCode":"<%=errorCodeVO.getApiCode()%>",
            "resultDescription":"<%=errorCodeVO.getApiDescription()%>",
            "cardBin":"<%=cardBin%>",
            "cardLast4Digits":"<%=cardLast4Digits%>",
            "cardType":"<%=cardType%>"
        });
    }
    else if( device != null && device == 'android')
    {
        var jsonObjectResponse = {};
        jsonObjectResponse["status"] = "<%=tokenStatus%>";
        jsonObjectResponse["firstName"] = "<%=firstName%>";
        jsonObjectResponse["lastName"] = "<%=lastName%>";
        jsonObjectResponse["checksum"] = "<%=checksum%>";
        jsonObjectResponse["timestamp"] = "<%=timestamp%>";
        jsonObjectResponse["resultCode"] = "<%=errorCodeVO.getApiCode()%>";
        jsonObjectResponse["resultDescription"] = "<%=errorCodeVO.getApiDescription()%>";
        jsonObjectResponse["cardBin"] = "<%=cardBin%>";
        jsonObjectResponse["cardLast4Digits"] = "<%=cardLast4Digits%>";
        jsonObjectResponse["cardType"] = "<%=cardType%>";
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
                            <div id="payMethod" style="font-weight: bold;text-align: center" class="truncate">CARD REGISTRATION</div>
                        </div>
                    </div>

                    <div id="options" style="height: 350px;overflow-y:auto;">
                        <!-- start of successs -->
                        <div class="check_mark" style="height: 350px;">
                            <div class="loader hide">
                            </div>
                            <%
                                if(tokenStatus!=null && (tokenStatus.contains("success") || tokenStatus.contains("Successful") ))
                                {
                            %>

                            <!-- success -->
                            <div id="success_div" class="succces ">
                                <div class="circle-loader">
                                    <div class="checkmark draw"></div>
                                </div>
                                <div class="success-message">REGISTRATION SUCCESSFUL !</div>
                            </div>
                            <!-- end of success -->

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
                                <div class="error-message">REGISTRATION FAILED !</div>
                            </div>
                            <!-- end of failure -->

                            <%
                                }
                            %>

                            <div class="confirmation-div">
                                <div class="confirm-box">
                                    <div class="confirmation-label"><%=varSTATUS%></div>
                                    <div class="confirmation-data">
                                        <%=tokenStatus%>
                                    </div>
                                </div>
                                <div class="confirm-box">
                                    <div class="confirmation-label"> Registration Token</div>
                                    <div class="confirmation-data">
                                        <%=registrationId%>
                                    </div>
                                </div>
                            </div>


                            <div class="pay-btn "  id="continuebutton">
                                <div class="pay-button"  onclick="success_button()">
                                    <span><%=varCONTINUE%></span>
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


<%if("GET".equalsIgnoreCase(redirectMethod)){%>
<form id="confirmationForm" name="confirmationForm" action="<%=redirectUrl%>" method="get">
<%}else{%>
<form id="confirmationForm" name="confirmationForm" action="<%=redirectUrl%>" method="post">
<%}%>

    <input type="hidden" name="resultCode" value="<%=errorCodeVO.getApiCode()%>">      <!--//as per new requirenments-->
    <input type="hidden" name="resultDescription" value="<%=errorCodeVO.getApiDescription()%>">
    <input type="hidden" name="timestamp" value="<%=timestamp%>">
    <input type="hidden" name="firstName" value="<%=firstName%>">
    <input type="hidden" name="lastName" value="<%=lastName%>">
    <input type="hidden" name="checksum" value="<%=checksum%>">
    <input type="hidden" name="status" value="<%=respStatus%>">

    <%
        if (functions.isValueNull(registrationId))
        {
    %>
    <input type="hidden" name="registrationId" value="<%=registrationId%>">      <!--//as per new requirenments-->
    <%
        }
    %>
    <%
        if(functions.isValueNull(cardBin)){
    %>
    <input type="hidden" name="cardBin" value="<%=cardBin%>">      <!--//as per new requirenments-->
    <input type="hidden" name="cardLast4Digits" value="<%=cardLast4Digits%>">     <!--//as per new requirenments-->
    <input type="hidden" name="cardType" value="<%=cardType%>">     <!--//as per new requirenments-->
    <input type="hidden" name="customerId" value="<%=customerId%>">     <!--//as per new requirenments-->
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