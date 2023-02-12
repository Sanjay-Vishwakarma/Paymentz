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
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.manager.vo.MarketPlaceVO" %>
<%--
  Created by IntelliJ IDEA.
  User: sagar
  Date: 8/27/14
  Time: 2:15 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Functions functions = new Functions();
    response.setHeader("Cache-control","no-store"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP1.0
    response.setDateHeader("Expire",0); //prevents caching at the proxy server
    if(functions.isValueNull((String)session.getAttribute("X-Frame-Options")) && "SAMEORIGIN".equals((String)session.getAttribute("X-Frame-Options")))
    {
        response.setHeader("X-Frame-Options", (String)session.getAttribute("X-Frame-Options"));
    }
   /* else
    {
        response.setHeader("X-Frame-Options","SAMEORIGIN");
    }*/

    Logger log = new Logger("confirmationpage.jsp");
%>
<html>
<head>
    <title></title>
    <style>
        .panelheading_color
        {
        <%=session.getAttribute("panelheading_color")!=null?"background-color:"+session.getAttribute("panelheading_color").toString()+"!important":""%>;
        }
        .headpanelfont_color
        {
        <%=session.getAttribute("headpanelfont_color")!=null?"color:"+session.getAttribute("headpanelfont_color").toString()+"!important":""%>;
        }
        .bodypanelfont_color
        {
        <%=session.getAttribute("bodypanelfont_color")!=null?"color:"+session.getAttribute("bodypanelfont_color").toString()+"!important":""%>;
        }
        .panelbody_color
        {
        <%=session.getAttribute("panelbody_color")!=null?"background-color:"+session.getAttribute("panelbody_color").toString()+"!important":""%>;
        }
        .mainbackgroundcolor /*box background*/
        {
        <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
        }
        .bodybackgroundcolor
        {
        <%=session.getAttribute("bodybgcolor")!=null?"background-color:"+session.getAttribute("bodybgcolor").toString()+"!important":""%>;
        }
        .bodyforegroundcolor
        {
        <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
        }
        .navigation_font_color
        {
        <%=session.getAttribute("navigation_font_color")!=null?"background-color:"+session.getAttribute("navigation_font_color").toString()+"!important":""%>;
        }
        .textbox_color
        {
        <%=session.getAttribute("textbox_color")!=null?"background-color:"+session.getAttribute("textbox_color").toString()+"!important":""%>;
        }
        .icon_color
        {
        <%=session.getAttribute("icon_color")!=null?"background-color:"+session.getAttribute("icon_color").toString()+"!important":""%>;
        }

    </style>

    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap.min.css">
    <link rel="stylesheet" href="/merchant/transactionCSS/css/main.css">
    <link rel="stylesheet" href="/merchant/transactionCSS/css/style_tr.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>
    <link href="/merchant/NewCss/libs/font-awesome/css/font-awesome.min.css" rel="stylesheet" />

</head>
<style>
    body {
    <%=session.getAttribute("bodybgcolor")!=null?"background-color:"+session.getAttribute("bodybgcolor").toString()+"!important":""%>;
    }

</style>
<body class="bodybackgroundcolor bodyforegroundcolor">

<%
    ResourceBundle rb = null;
    String sLanguage[] = null;
            //zh-CN;q=0.8,en-US;q=0.5,en;q=0.3
    String multiLanguage = request.getHeader("Accept-Language");
    if(functions.isValueNull(multiLanguage))
    {
        sLanguage = multiLanguage.split(",");
        if(sLanguage != null)
        {
            if ("zh-CN".contains(sLanguage[0]))
            {
                rb = LoadProperties.getProperty("com.directi.pg.creditpage", "zh");
            }
            else
            {
                rb = LoadProperties.getProperty("com.directi.pg.creditpage");
            }
        }
        else
        {
            rb = LoadProperties.getProperty("com.directi.pg.creditpage");
        }
    }
    else
    {
        rb = LoadProperties.getProperty("com.directi.pg.creditpage");
    }
    final String CRE_TRANSACTIONDETAILS=rb.getString("CRE_TRANSACTIONDETAILS");
//    final String KEY_TRACKINGID = rb.getString("CRE_TRACKINGID");
    final String KEY_PAYMENTID = rb.getString("CRE_PAYMENTID");
//    final String KEY_DESCRIPTION= rb.getString("CRE_DESCRIPTION");
    final String KEY_MERCHANTTRANSACTIONID= rb.getString("CRE_MERCHANTTRANSACTIONID");
    final String KEY_ORDERDESCRIPTION= rb.getString("CRE_ORDERDESCRIPTION");
    final String CRE_AMOUNT= rb.getString("CRE_AMOUNT");
    final String CRE_STATUS=rb.getString("CRE_STATUS");
    final String CRE_MANDATE=rb.getString("CRE_MANDATE");
    final String CRE_IMP=rb.getString("CRE_IMP");
    final String CRE_IMP2=rb.getString("CRE_IMP2");
    final String CRE_CONT=rb.getString("CRE_CONT");
    final String CRE_GATEWAY=rb.getString("CRE_GATEWAY");
    final String CRE_PAYMENTGATEWAY=rb.getString("CRE_PAYMENTGATEWAY");
    final String CRE_PAYMENTGATEWAY2=rb.getString("CRE_PAYMENTGATEWAY2");

    CommonValidatorVO commonValidatorVO= (CommonValidatorVO) request.getAttribute("transDetail");

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
    String redirectMethod = "";
    String bankCode="";
    String bankDescription="";
    String remark="";


    if(commonValidatorVO!=null && request.getAttribute("responceStatus")!=null /*&& request.getAttribute("displayName")!=null*/)
    {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        Date date = new Date();
        Transaction transaction = new Transaction();

        GenericTransDetailsVO genericTransDetailsVO=commonValidatorVO.getTransDetailsVO();
        GenericAddressDetailsVO addressDetailsVO = commonValidatorVO.getAddressDetailsVO();
        trackingid=commonValidatorVO.getTrackingid();
        orderid=genericTransDetailsVO.getOrderId();
        remark=commonValidatorVO.getReason();

        if(functions.isValueNull(remark))
            remark= (String) request.getAttribute("remark");

        /*if(functions.isValueNull(commonValidatorVO.getDisplayAmount()))//Used for only Voucher Money
            amount = commonValidatorVO.getDisplayAmount();*/
        if(commonValidatorVO.getAddressDetailsVO() != null && commonValidatorVO.getAddressDetailsVO().getTmpl_amount() != null)
            amount = commonValidatorVO.getAddressDetailsVO().getTmpl_amount();
        else
            amount = commonValidatorVO.getTransDetailsVO().getAmount();

        /*if(functions.isValueNull(commonValidatorVO.getDisplayCurrency()))
            currency = commonValidatorVO.getDisplayCurrency();*/
        if(commonValidatorVO.getAddressDetailsVO() != null && commonValidatorVO.getAddressDetailsVO().getTmpl_currency() != null)
            currency = commonValidatorVO.getAddressDetailsVO().getTmpl_currency();
        else
            currency = commonValidatorVO.getTransDetailsVO().getCurrency();

        orderDesc= genericTransDetailsVO.getOrderDesc();
        status= (String) request.getAttribute("responceStatus");
        partnerId = commonValidatorVO.getParetnerId();
        redirectUrl = commonValidatorVO.getTransDetailsVO().getRedirectUrl();
        logoName = commonValidatorVO.getMerchantDetailsVO().getLogoName();
        partnerName = commonValidatorVO.getPartnerName();
        powerBy = commonValidatorVO.getMerchantDetailsVO().getPoweredBy();
        sisaLogoFlag = commonValidatorVO.getMerchantDetailsVO().getSisaLogoFlag();
        partnerLogoFlag = commonValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
        merchantLogo = commonValidatorVO.getMerchantDetailsVO().getLogoName();
        log.debug("merchant logo name from confirmationpage jsp----"+merchantLogo);
        merchantLogoFlag = commonValidatorVO.getMerchantDetailsVO().getMerchantLogo();
        paymentMode = commonValidatorVO.getPaymentType();
        paymentBrand = commonValidatorVO.getCardType();
        pType = transaction.getPaymentModeForRest(paymentMode);
        cType = transaction.getPaymentBrandForRest(paymentBrand);
        if(commonValidatorVO.getAddressDetailsVO().getEmail() != null)
            email = ESAPI.encoder().encodeForHTML(commonValidatorVO.getAddressDetailsVO().getEmail());
        timestamp = String.valueOf(dateFormat.format(date));
        custAccountId = commonValidatorVO.getCustAccountId();
        customerId = commonValidatorVO.getCustomerId();
        bankAccountId = commonValidatorVO.getCustomerBankId();
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
        if (functions.isValueNull(commonValidatorVO.getTransDetailsVO().getRedirectMethod()))
            redirectMethod=commonValidatorVO.getTransDetailsVO().getRedirectMethod();

        bankCode=commonValidatorVO.getBankCode();
        bankDescription=commonValidatorVO.getBankDescription();
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
        //log.debug("status--->"+status);
        //log.debug("paymentMode--->"+paymentMode);
        //log.debug("currency--->"+currency);
        //log.debug("cardBin--->"+cardBin);
        //log.debug("cardLast4Digits--->"+cardLast4Digits);
        //log.debug("bankAccountIban--->"+bankAccountIban);
        //log.debug("bankAccountBIC--->"+bankAccountBIC);
        //log.debug("bankAccountNumber--->"+bankAccountNumber);
        //log.debug("bankAccountType--->"+bankAccountType);
        //log.debug("bankRoutingNumer--->"+bankRoutingNumer);
        //log.debug("amount--->"+commonValidatorVO.getTransDetailsVO().getAmount());
        //log.debug("currency--->"+commonValidatorVO.getTransDetailsVO().getCurrency());

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

        if(status!=null && (status.contains("Successful") || status.contains("Pending")))
        {
            bgcolor = "txtboxconfirm";
        }

        //System.out.println("erroName----"+errorName);
        //System.out.println("Status-----"+status);
        if(status!=null && (status.contains("Successful") || status.contains("success")) || status.contains("Approved") || status.contains("Success"))
        {
            respStatus = "Y";
            if (functions.isValueNull(errorName))
            {
                //System.out.println("erroNAme----"+errorName);
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

    session.invalidate();
    final ResourceBundle RBundel1 = LoadProperties.getProperty("com.directi.pg.CertificateLink");
    String pciurl= RBundel1.getString("PCILINK");
    log.debug("version for IOS----" + commonValidatorVO.getVersion());

%>
<script type="text/javascript">

    var version = "<%=commonValidatorVO.getVersion()%>";
    if(version != null && version == '1.0_MiOS')
    {


        window.webkit.messageHandlers.callbackHandler.postMessage({
            "trackingId" : "<%=trackingid%>",
            "status" : "<%=status%>",
            "billingDescriptor" : "<%=billingDesc%>",
            "firstName" : "<%=firstName%>",
            "lastName" : "<%=lastName%>",
            "amount" : "<%=amount%>",
            "checksum" : "<%=checksum%>",
            "token" : "<%=token%>",
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
            "custAccountId":"<%=commonValidatorVO.getCustAccountId()%>",
            "custEmail":"<%=email%>",
            "custMerchantId":"<%=commonValidatorVO.getCustomerId()%>",
            "custBankId":"<%=commonValidatorVO.getCustomerBankId()%>",
            "paymentMode":"<%=pType%>",
            "paymentBrand":"<%=cType%>",
            "eci":"<%=eci%>"
        });
    }
    else if( version != null && version == '1.0_M')
    {

        var jsonObjectResponse = {};
        jsonObjectResponse["trackingId"] = "<%=trackingid%>";
        jsonObjectResponse["status"] = "<%=status%>";
        jsonObjectResponse["billingDescriptor"] = "<%=billingDesc%>";
        jsonObjectResponse["firstName"] = "<%=firstName%>";
        jsonObjectResponse["lastName"] = "<%=lastName%>";
        jsonObjectResponse["amount"] = "<%=amount%>";
        jsonObjectResponse["checksum"] = "<%=checksum%>";
        jsonObjectResponse["token"] = "<%=token%>";
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
<style>

    .box {
        width: 30%;
        display:inline-block;
        margin:30px 0;
        border-radius:5px;
    }
    .text {
        padding: 10px 0;
        color:white;
        font-weight:bold;
        text-align:center;
    }
    #container {
        white-space:nowrap;
        text-align:center;
    }




    @media(max-width:768px)
    {
        #smallbox
        {
            margin-left: 0px;
        }
        #img1
        {
            width: 99px;
            height: 22px;
        }
        #img3
        {
            height: 21px;

        }
        #img2
        {
            margin-top: 0px;
        }
        #logo1
        {
            width: 259px;
            margin-top: 20px;
            margin-left: 0px;
        }
        #logo1footer
        {
            width: 259px;
            margin-top: 20px;
            margin-left: -70px;
        }
        #logo2
        {
            margin-left: -104px;
        }
        #logo3
        {
            margin-top: -47px;
            margin-left: 199px;
            right: 123px;
        }
        /*        #h4head
                {
                    margin-left: 17px;
                }*/
        #col-md-4-head
        {
            background-color: #ffffff;height:auto;
            /*border: 2px solid #dadada!important;*/
        }
        #IMAGE1
        {
            width: 58%;
        }
        #IMAGE3
        {
            width: 125px;
        }
        #logo1div
        {
            margin-bottom: -20px;
        }
        #footerlogo1
        {
            height: 18px;
            margin-left: -18px;
        }
        #container
        {
            margin-top: -32px;
        }
        #creditly-wrapper
        {
            margin-left:0px;

        }
        #img1
        {
            margin-left: -26px;
            margin-top: 6px;
        }
        #img2
        {
            margin-left: -10px;
        }
        #imghead
        {
            height: 50px;
            margin-top: 13px;
        }
        #img1
        {
            width: 99px;
            height: 22px;
        }
        #img3
        {
            height: 21px;

        }
        #img2
        {
            margin-top: 0px;
        }
        #logo1
        {
            width: 259px;
            margin-top: 20px;
            margin-left: 0px;
        }
        #logo1footer
        {
            width: 259px;
            margin-top: 20px;
            margin-left: -70px;
        }
        #logo2
        {
            margin-left: -104px;
        }
        #logo3
        {
            margin-top: -47px;
            margin-left: 199px;
            right: 123px;
        }
        /*        #h4head
                {
                    margin-left: 17px;
                }*/
        #col-md-4-head
        {
            background-color: #ffffff;height:auto;
            /*border: 2px solid #dadada!important;*/
        }
        #IMAGE1
        {
            width: 58%;
        }
        #IMAGE3
        {
            width: 125px;
        }
        #logo1div
        {
            margin-bottom: -20px;
        }
        #footerlogo1
        {
            height: 18px;
            margin-left: -18px;
        }
        #container
        {
            margin-top: -32px;
        }
        #creditly-wrapper
        {
            margin-left:0px;

        }
        #img1
        {
            margin-left: -26px;
            margin-top: 6px;
        }
        #img2
        {
            margin-left: -10px;
        }
        #container-fluid-align
        {
            text-align: center;

        }

    }
    @media(min-width:768px)
    {
        #logo1div
        {
            position: absolute;
            left: 42%;
            top: 0;
            z-index: 60;
            padding: 0;
            margin-top: 20px;
        }
        #logo1
        {
            width: 31%;
            FLOAT: LEFT;
            LEFT: 72px;
            MARGIN-TOP: 12px;
        }
        #logo1footer
        {
            width: 197px;
            margin-top: 15px;
            margin-left: 66px;
        }
        #logo2
        {
            MARGIN-LEFT: 9px
        }
        #logo3
        {
            float: right;
            margin-top: 20px;
            right: 20px;
        }
        #h4head
        {

            margin-bottom: 26px;
            /* margin-left: 111px;*/
        }
        #footerid
        {
            margin-left: 200px;
            margin-top: -24px;
        }
        #IMAGE1
        {
            width: 90%;
        }
        #IMAGE3
        {
            width: 144%;
        }
        #col-md-4-head
        {
            background-color: #ffffff;
            height:auto;
            /*border: 2px solid #dadada!important;*/
            margin-top: 100px;
        }
        #creditly-wrapper
        {
            margin-left: 100px;
        }
        #imghead
        {
            /*height: 35px;*/
            /*margin-top: 7px;*/
        }
        #btncontinue {
            margin-left: -5px;
        }
        #container-fluid-align
        {
            margin-bottom: 20px;
            text-align: center;
            margin-left: 10px;
        }

    }
    @media(min-width:890px) {
        #h4head {
            margin-bottom: 26px;
            margin-left: -14px;
        }
        #btncontinue
        {
            margin-left: 64px;
        }
        #container-fluid-align
        {
            margin-bottom: 20px;
            text-align: center;
           /* margin-left: 192px;*/
        }



    }

</style>

<div id="logo1div">

    <%--<p align="center"><a href="http://www.PZ.com"><img border="0" height="40" src="/merchant/images/Pay_Icon.png" width="90"></a></p>--%>
    <%--<center><img id="logo1" src="/images/merchant/<%=merchantLogo%>"></center>--%>

    <p align="center"><img border="0" id="imghead"  src="/images/merchant/<%=merchantLogo%>"></p>
    <br/>
</div>
<%if("GET".equalsIgnoreCase(redirectMethod)){%>
    <form action="<%=redirectUrl%>" method="get">
<%}else{%>
    <form action="<%=redirectUrl%>" method="post">
<%}%>

    <div class="col-md-6 col-md-offset-3 mainbackgroundcolor headpanelfont_color" id="col-md-4-head">

        <div class="container-fluid" id="container-fluid-align">
            <div class="row" style="margin-bottom: 20px;">
                <div class="col-md-12">
                    <div class="widget">
                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <h4 id="h4head" style="text-align: center;" class="headpanelfont_color" ><%=CRE_TRANSACTIONDETAILS%></h4>
                                <div class="form-group">
                                    <label class="col-sm-6 control-label"><%=KEY_PAYMENTID%></label>
                                    <%
                                        if (!functions.isValueNull(trackingid))
                                        {
                                            trackingid = "-";
                                        }
                                    %>
                                    <div class="col-sm-6">
                                        <label  id="trackingid" class="headpanelfont_color"><%=trackingid%></label>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-6 control-label"><%=KEY_MERCHANTTRANSACTIONID%></label>
                                    <div class="col-sm-6">
                                        <%
                                            if (!functions.isValueNull(orderid))
                                            {
                                                orderid = "-";
                                            }
                                        %>
                                        <label id="description" name="description" class="headpanelfont_color"><%=orderid%></label>
                                    </div>
                                </div>

                                <div class="form-group ">
                                    <label class="col-sm-6 control-label"><%=KEY_ORDERDESCRIPTION%></label>
                                    <div class="col-sm-6 has-feedback">
                                        <%
                                            if(!functions.isValueNull(orderDesc))
                                            {
                                                orderDesc = "-";
                                            }
                                        %>
                                        <label  id="orderdescription"  name="description" class="headpanelfont_color"><%=orderDesc%></label>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-6 control-label"><%=CRE_AMOUNT%></label>
                                    <div class="col-sm-6 has-feedback">
                                        <label id="amount" class="headpanelfont_color"><%=currency%> <%=amount%></label>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-sm-6 control-label"> <%=CRE_STATUS%></label>
                                    <div class="col-sm-6 has-feedback">
                                        <%
                                            if (!functions.isValueNull(status))
                                            {
                                                status = "-";
                                            }
                                        %>
                                        <label  id="status" class="headpanelfont_color"><%=status%></label>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <%
                                        if (!functions.isValueNull(billingDesc))
                                        {
                                            billingDesc = "-";
                                        }
                                    %>
                                    <label class="col-sm-6 control-label"> Billing Descriptor</label>
                                    <div class="col-sm-6 has-feedback">
                                        <label  id="status" class="headpanelfont_color"><%=billingDesc%></label>
                                    </div>
                                </div>

                                <%if(functions.isValueNull(mandateId)){%>
                                <div class="form-group">
                                    <label class="col-sm-6 control-label"><%=CRE_MANDATE%></label>
                                    <%
                                        if (!functions.isValueNull(mandateId))
                                        {
                                            mandateId = "-";
                                        }
                                    %>
                                    <div class="col-sm-6 has-feedback">
                                        <label  id="mandateId" readonly="" class="headpanelfont_color"><%=mandateId%></label>
                                    </div>
                                    <%}else{%>

                                    <%
                                            mandateId="";
                                        }
                                    %>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="container-fluid">
                <div class="row" style="margin-bottom: 20px;">
                    <div class="col-md-8 col-md-offset-2">

                        <div class="logo" >

                            <div align="center" class="textb">
                                <%if(!"MerchantVT".equalsIgnoreCase(commonValidatorVO.getActionType())){%>
                                <input type="submit" name="submit" value="<%=CRE_CONT%>" class="btn btn-info panelbody_color" id="btncontinue">
                                <%}%>
                            </div>

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
                            <input type="hidden" name="lastName" value="<%=lastName%>">
                            <!--//as per new requirenments-->
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
                            <%if(functions.isValueNull(bankCode) && functions.isValueNull(bankDescription)){%>
                            <input type="hidden" name="bankCode" value="<%=bankCode%>">      <!--//as per phoneix new requirenments-->
                            <input type="hidden" name="bankDescription" value="<%=bankDescription%>"><!--//as per phoneix new requirenments-->
                            <%}%>
                            <%if(functions.isValueNull(remark)){%>
                            <input type="hidden" name="remark" value="<%=remark%>">      <!--//as per new requirenments-->
                            <%}%>
                            <%
                                if(functions.isValueNull(cardBin))
                                {
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
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>


    <div class="col-md-6 col-md-offset-3">


        <%
            if(partnerLogoFlag != null && partnerLogoFlag.equalsIgnoreCase("Y"))
            {
        %>

        <div id="container">
            <div class="box">
                <div class="text"><img border="0" height="30" id="img1" src="/images/merchant/<%=logoName%>"></div>
            </div>

            <%
                }
                if(sisaLogoFlag != null && sisaLogoFlag.equalsIgnoreCase("Y"))
                {
            %>
            <div class="box">
                <div class="text"><a target="_blank" href="<%=pciurl%>"><IMG
                        border="0" height="30" id="img2" src="/images/merchant/pci_dss_logo.png" style="width: 70px;"></a></div>
            </div>
            <%
                }
                if(powerBy != null && powerBy.equalsIgnoreCase("Y"))
                {
            %>

            <div class="box">
                <div class="text"><a href="http://www.pz.com"><img border="0" height="30" id="img3" src="/images/merchant/poweredBy_logo.png" ></a></div>
            </div>
        </div>
        <%
            }
        %>



    </div>


</form>
</body>
</html>