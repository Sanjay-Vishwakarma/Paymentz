<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.valueObjects.GenericTransDetailsVO" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>

<%@ page import="com.directi.pg.*" %>
<%@ page import="com.payment.exceptionHandler.errorcode.ErrorCodeUtils" %>
<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO" %>

<%@ page import="java.util.ResourceBundle" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<%@ page import="com.payment.cupUPI.UnionPayInternationalRequestVO" %>
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
    <link rel="stylesheet" href="/merchant/transactionCSS/css/step-form.css"/>


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
                /*margin: 1.75rem;*/
                margin: 0;
            }
        }

        @media (min-width: 576px) {
            .modal-sm {
                /*max-width: 346px;*/
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


        .digit-group input {
            width: 40px;
            height: 40px;
            border: none;
            line-height: 50px;
            text-align: center;
            font-size: 24px;
            font-weight: 200;
            color: black;
            border: 1px solid;
            margin: 0 2px;
            border-radius: 5px;
        }
        .digit-group .splitter {
            padding: 0 5px;
            color: white;
            font-size: 24px;
        }

        .prompt {
            margin-bottom: 20px;
            font-size: 20px;
            color: white;
        }

    </style>

</head>
<body style="font-family: Raleway;" class="background">
<%--<script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js" integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49" crossorigin="anonymous"></script>--%>
<%--<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js" integrity="sha384-smHYKdLADwkXOn1EmN1qk/HfnUcbVRZyYmZ4qpPea6sjB/pTJ0euyQp0Mk8ck+5T" crossorigin="anonymous"></script>--%>


<%
    Functions functions=new Functions();
    CommonValidatorVO commonValidatorVO= (CommonValidatorVO) request.getAttribute("transDetails");

    UnionPayInternationalRequestVO unionPayInternationalRequestVO= (UnionPayInternationalRequestVO) request.getAttribute("unionPayInternationalRequestVO");


    Logger log = new Logger("CupUpiSMS.jsp");
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
    String orderid="";
    log.debug("Card Number In jsp 1--------------" + commonValidatorVO.getCardDetailsVO().getCardNum());
    String cardNumber=commonValidatorVO.getCardDetailsVO().getCardNum();
    String expiryMonth=commonValidatorVO.getCardDetailsVO().getExpMonth();
    String expiryYear=commonValidatorVO.getCardDetailsVO().getExpYear();
    String cvv=commonValidatorVO.getCardDetailsVO().getcVV();
    String phoneNumber=commonValidatorVO.getAddressDetailsVO().getPhone();
    String phoneNumberCC=commonValidatorVO.getAddressDetailsVO().getTelnocc();
    log.debug("Card Expiry month ---------" + commonValidatorVO.getCardDetailsVO().getExpMonth());
    log.debug("Card Expiry Year ---------" + commonValidatorVO.getCardDetailsVO().getExpYear());
    //log.debug("Cvv  ---------" + commonValidatorVO.getCardDetailsVO().getcVV());
    log.debug("" + commonValidatorVO.getCardDetailsVO().getExpYear());
    String amount = commonValidatorVO.getDisplayAmount();
    // String currency = commonValidatorVO.getDisplayCurrency();
    String currency = commonValidatorVO.getTransDetailsVO().getCurrency();
    // String amount = commonValidatorVO.getTransDetailsVO().getAmount();
    System.out.println("currency----------------------------"+currency);
    System.out.println("currenccy  --------------"+currency);
    String accountId1 = commonValidatorVO.getMerchantDetailsVO().getAccountId();
    String emailsent=commonValidatorVO.getMerchantDetailsVO().getEmailSent();
    System.out.println("email sent in jsp -----"+emailsent);
    String autoRedirect=commonValidatorVO.getMerchantDetailsVO().getAutoRedirect();
    String isService=commonValidatorVO.getMerchantDetailsVO().getIsService();
    System.out.println("autoRedirect in jsp -----"+autoRedirect);

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

        System.out.println("amount   if else   -----"+amount);
        logoName = commonValidatorVO.getMerchantDetailsVO().getLogoName();
        partnerName = commonValidatorVO.getPartnerName();
        powerBy = commonValidatorVO.getMerchantDetailsVO().getPoweredBy();
        sisaLogoFlag = commonValidatorVO.getMerchantDetailsVO().getSisaLogoFlag();
        securityLogoFlag = commonValidatorVO.getMerchantDetailsVO().getSecurityLogo();
        partnerLogoFlag = commonValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
        merchantLogo = commonValidatorVO.getMerchantDetailsVO().getLogoName();
        log.debug("merchant logo name from confirmationpage jsp----"+merchantLogo);
        merchantLogoFlag = commonValidatorVO.getMerchantDetailsVO().getMerchantLogo();

/*        if(null != commonValidatorVO.getCardDetailsVO())
        {
            if (functions.isValueNull(commonValidatorVO.getCardDetailsVO().getCardNum()))
            {
                cardBin = commonValidatorVO.getCardDetailsVO().getCardNum().substring(0, 6);
                cardLast4Digits = commonValidatorVO.getCardDetailsVO().getCardNum().substring((commonValidatorVO.getCardDetailsVO().getCardNum().length() - 4), commonValidatorVO.getCardDetailsVO().getCardNum().length());
            }

        }*/



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


    }

//session.invalidate();
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


%>

<script type = "text/javascript" src="/merchant/transactionCSS/js/anime.min.js"></script> <!-- loader animation-->
<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.min.js"></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/bootstrap/js/bootstrap.min.js" ></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.cardcheck.js"></script>
<script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.cardcheck.min.js"></script>
<%--<script type = "text/javascript" src="/merchant/transactionCSS/js/content.js"></script>--%>
<script type = "text/javascript" src="/merchant/transactionCSS/js/content.min.js"></script>

<script type="text/javascript">

    function submitCode(){
        console.log("in continue button");

        $("#smsForm").submit();
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
                    %>
                    <div class="logo">
                        <img class="images-style1" style="width: 100%;" src="/images/merchant/<%=session.getAttribute("merchantLogoName")%>">
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
                    <%--<button type="button" id="closebtnConfirmation" class="close hide" data-dismiss="modal" aria-label="Close" onclick="closefunc()">--%>
                    <%--<span aria-hidden="true">&times;</span>--%>
                    <%--</button>--%>
                </div>

                <div class="modal-body">
                    <div class="top-bar">
                        <div class="top-right" id="topRight" style="cursor: pointer">
                            <span onclick="backToHome()"><i class="fas fa-home"></i></span>
                        </div>
                        <div class="top-left" id="topLeft" >
                            <span id="backArrow" style="float: left;width: 20px;cursor: pointer" onclick="back()" ><i class="fas fa-angle-left"></i></span>
                            <div id="payMethod" style="font-weight: bold;text-align: center" class="truncate">SMS</div>
                        </div>
                    </div>

                    <div id="options" style="height: 305px;overflow-y:auto;">
                        <div class="check_mark" style="height: 260px;">
                            <form id="smsForm" name="smsForm" action="/transaction/CupUpiSMS" method="post">

                                <input type="hidden" name="status" value="Success" />
                                <input type="hidden" name="trackingid" value=<%=trackingid%> />
                                <input type="hidden" name="cardNumber" value=<%=PzEncryptor.encryptPAN(cardNumber)%> />
                                <input type="hidden" name="expiryMonth" value=<%=PzEncryptor.encryptExpiryDate(expiryMonth)%> />
                                <input type="hidden" name="expiryYear" value=<%=PzEncryptor.encryptExpiryDate(expiryYear)%> />
                                <input type="hidden" name="cvv" value=<%=PzEncryptor.encryptCVV(cvv)%> />
                                <input type="hidden" name="phoneNumber" value=<%=phoneNumber%> />
                                <input type="hidden" name="phoneNumberCC" value=<%=phoneNumberCC%> />
                                <input type="hidden" name="emailsent" value=<%=emailsent%> />
                                <input type="hidden" name="autoRedirect" value=<%=autoRedirect%> />
                                <input type="hidden" name="currency" value=<%=currency%> />
                                <input type="hidden" name="tmpl_amount" value=<%=commonValidatorVO.getAddressDetailsVO().getTmpl_amount()%> />
                                <input type="hidden" name="tmpl_currency" value=<%=commonValidatorVO.getAddressDetailsVO().getTmpl_currency()%> />
                                <input type="hidden" name="isService" value=<%=isService%> />

                                <div class="tab" style="padding-top: 22px;display: inline-block ">
                                    <p class="form-header" style="font-weight: bold;font-size: 15px;"> Please enter the code received.</p>
                                    <div class="form-group has-float-label control-group-full" style="/*margin:30px 0px;*/" >
                                        <input type="text" class="form-control input-control1" id="code" placeholder=" "
                                        oninput="this.className = 'form-control input-control1'"  name="code" />
                                        <label for="code" class="form-label">SMS Code</label>

                                        <%--<div  class="digit-group" data-group-name="digits">
                                            <input type="text" id="digit-1" name="digit-1" data-next="digit-2" />
                                            <input type="text" id="digit-2" name="digit-2" data-next="digit-3" data-previous="digit-1" />
                                            <input type="text" id="digit-3" name="digit-3" data-next="digit-4" data-previous="digit-2" />
                                            &lt;%&ndash;<span class="splitter">&ndash;</span>&ndash;%&gt;
                                            <input type="text" id="digit-4" name="digit-4" data-next="digit-5" data-previous="digit-3" />
                                            <input type="text" id="digit-5" name="digit-5" data-next="digit-6" data-previous="digit-4" />
                                            <input type="text" id="digit-6" name="digit-6" data-previous="digit-5" />
                                        </div>--%>
                                    </div>
                                </div>
                                <jsp:include page="requestParameters.jsp">
                                    <jsp:param name="paymentMode" value="CupUpi" />
                                </jsp:include>

                            </form>
                        </div>
                        <div class="pay-btn" style="position: inherit" >
                            <div class="pay-button"  onclick="submitCode()">
                                <span><%=varCONTINUE%></span>
                            </div>
                        </div>
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
<%--<script>

    $('.digit-group').find('input').each(function() {
        $(this).attr('maxlength', 1);
        $(this).on('keyup', function(e) {
            var parent = $($(this).parent());

            if(e.keyCode === 8 || e.keyCode === 37) {
                var prev = parent.find('input#' + $(this).data('previous'));

                if(prev.length) {
                    $(prev).select();
                }
            } else if((e.keyCode >= 48 && e.keyCode <= 57) || (e.keyCode >= 65 && e.keyCode <= 90) || (e.keyCode >= 96 && e.keyCode <= 105) || e.keyCode === 39) {
                var next = parent.find('input#' + $(this).data('next'));
                console.log("next = ",next)
                if(next.length) {
                    $(next).select();
                } else {

                }
            }
        });
    });
</script>--%>
</body>
</html>