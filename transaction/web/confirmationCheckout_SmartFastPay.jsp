<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.payment.common.core.Comm3DResponseVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.google.zxing.qrcode.QRCodeWriter" %>
<%@ page import="com.google.zxing.common.BitMatrix" %>
<%@ page import="com.google.zxing.qrcode.QRCodeWriter" %>
<%@ page import="com.google.zxing.common.BitMatrix" %>
<%@ page import="java.nio.file.Path" %>
<%@ page import="java.nio.file.FileSystems" %>
<%@ page import="com.google.zxing.client.j2se.MatrixToImageWriter" %>
<%@ page import="com.google.zxing.BarcodeFormat" %>

<%--
Created by IntelliJ IDEA
User: Rihen
Date: 4/30/2020
Time: 4:32 PM
To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>



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
                /*width: 400px !important;*/
                width: 100% !important;
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
        }

        .mainDiv{
        <%=session.getAttribute("box_shadow")!=null?"box-shadow:"+session.getAttribute("box_shadow").toString():""%>;
            width: 100%;
        }

        .background{
        <%=session.getAttribute("bodybgcolor")!=null?"background:"+session.getAttribute("bodybgcolor").toString():""%>;
        }

        .header{
            padding: 10px 15px;
        <%=session.getAttribute("panelheading_color")!=null?"background-color:"+session.getAttribute("panelheading_color").toString()+"!important":""%>;
        <%=session.getAttribute("bodypanelfont_color")!=null?"color:"+session.getAttribute("bodypanelfont_color").toString()+"!important":""%>;
        }

        .logo{                /* background color of logo  */
            background:transparent !important;
            box-shadow: none !important;
        }

        .top-bar{               /* navigation bar background and font color */
        <%=session.getAttribute("panelbody_color")!=null?"background-color:"+session.getAttribute("panelbody_color").toString()+"!important":""%>;
        <%=session.getAttribute("navigation_font_color")!=null?"color:"+session.getAttribute("navigation_font_color").toString()+"!important":""%>;
            height: auto;
        }

        .header-section{
            font-weight: bold;
            text-align: center;
            line-height: 1.5;
            font-size: 14px;
            padding: 5px;
        }

        .data-section{
        <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
        <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
            padding:10px !important;
            max-height: none !important;
        }

        .data-head-title{
            padding: 8px;
            border-bottom: 2px solid;
            font-size: 0.9rem;
            font-weight: bold;
            text-align: center;
        }

        .data-row{
            border-bottom: 1px dashed #808080!important;
            display: flex;
            justify-content: space-between;
            padding: 8px 0;
            margin: 0;
        }

        .data-row:last-child{
            border: none !important;
        }

        .data-label{
            font-size:0.8rem;
            font-weight: bold;
            text-transform: uppercase;
        }

        .data-value{
            padding: 0px 10px;
            font-size:0.9rem;
        }

        .support-sec{
            text-align: center;
            line-height: 1.5;
            font-size: 0.8rem;
            padding: 5px;
        }

        .modal-footer{
        <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
        }

    </style>


    <script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.min.js"></script>
    <script type = "text/javascript" src="/merchant/transactionCSS/js/bootstrap-4.4.1/js/bootstrap.min.js" ></script>

</head>

<body style="font-family: Raleway;" class="background">

<%
    Functions functions                 = new Functions();
    CommonValidatorVO commonValidatorVO = (CommonValidatorVO) request.getAttribute("transDetail");
    Logger log                          = new Logger("confirmationCheckout_SmartFastPay.jsp");
    String ctoken = "";
    ctoken = (String) session.getAttribute("ctoken");

    String logoName = "";
    String powerBy = "";
    String sisaLogoFlag="Y";
    String securityLogoFlag="N";
    String partnerLogoFlag="Y";
    String merchantLogoFlag="";
    String trackingId="";
    String amount ="";

    if(commonValidatorVO!=null)
    {
        logoName            = commonValidatorVO.getMerchantDetailsVO().getLogoName();
        powerBy             = commonValidatorVO.getMerchantDetailsVO().getPoweredBy();
        sisaLogoFlag        = commonValidatorVO.getMerchantDetailsVO().getSisaLogoFlag();
        securityLogoFlag    = commonValidatorVO.getMerchantDetailsVO().getSecurityLogo();
        partnerLogoFlag     = commonValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
        merchantLogoFlag    = commonValidatorVO.getMerchantDetailsVO().getMerchantLogo();
        trackingId          = commonValidatorVO.getTrackingid();
//        trackingId=commonValidatorVO.getTrackingid();
        // amount=commonValidatorVO.getTransactionDetailsVO().getAmount();
    }


    Comm3DResponseVO tigerPayResponseVO   = (Comm3DResponseVO) request.getAttribute("transRespDetails");

    String name = "";
    String code = "";
    String agency = "";
    String account = "";
    String account_operation = "";
    String document = "";
    String beneficiary = "";
    int cardType = 0;
    String cardName = "";
    String qrcode       = "";
    String reference    = "";

    amount          = tigerPayResponseVO.getAmount();
    ResourceBundle rb1    = LoadProperties.getProperty("com.directi.pg.QRCode");

    amount     = commonValidatorVO.getTransDetailsVO().getAmount();


    HashMap<String,String> requestParamMap        = new HashMap<>();

    if(request.getAttribute("requestParamMap") != null){
        requestParamMap = (HashMap<String, String>) request.getAttribute("requestParamMap");
    }
    if(request.getAttribute("cardType") != null){
        cardType  = (int) request.getAttribute("cardType");
        cardName  = GatewayAccountService.getCardType(cardType+"");
        if(cardName.equalsIgnoreCase("TED")){
            cardName = "Bank Transfer";
        }
    }

    if(requestParamMap != null){
        if(requestParamMap.containsKey("name")){
            name = requestParamMap.getOrDefault("name","--");
        }
        if(requestParamMap.containsKey("code")){
            code = requestParamMap.getOrDefault("code","--");
        }
        if(requestParamMap.containsKey("agency")){
            agency = requestParamMap.getOrDefault("agency","--");
        }
        if(requestParamMap.containsKey("account")){
            account = requestParamMap.getOrDefault("account","--");
        }
        if(requestParamMap.containsKey("account_operation")){
            account_operation = requestParamMap.getOrDefault("account_operation","--");
        }
        if(requestParamMap.containsKey("document")){
            document = requestParamMap.getOrDefault("document","--");
        }
        if(requestParamMap.containsKey("beneficiary")){
            beneficiary = requestParamMap.getOrDefault("beneficiary","--");
        }
        if(requestParamMap.containsKey("qrcode")){
            qrcode = requestParamMap.getOrDefault("qrcode","--");
        }
        if(requestParamMap.containsKey("reference")){
            reference = requestParamMap.getOrDefault("reference","--");
        }

    }
    String filePath ="";
    if("PICPAY".equalsIgnoreCase(cardName) || "PIX".equalsIgnoreCase(cardName)){
        String text               = qrcode;

        filePath           = rb1.getString("PATH")+"MyQR_"+trackingId+".png";

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix       = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200,200);

        Path path                = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

%>

<!-- modal starts -->
<div class="modal show" style="display: inline-block;position: absolute;overflow: auto;">
    <div class="modal-dialog modal-md modal-dialog-centered">
        <div class="modal-content">
            <div id="target" class="mainDiv">
                <%
                    if((functions.isValueNull(merchantLogoFlag) && merchantLogoFlag.equalsIgnoreCase("Y")) ||
                            functions.isValueNull(partnerLogoFlag) && partnerLogoFlag.equalsIgnoreCase("Y")){
                %>
                <div class="header">
                    <%
                        if(functions.isValueNull(merchantLogoFlag) && merchantLogoFlag.equalsIgnoreCase("Y"))
                        {
                    %>
                    <div class="logo">
                        <img class="images-style1" style="width: 100%;" src="/images/merchant/<%=session.getAttribute("merchantLogoName")%>">
                    </div>
                    <%
                    }
                    else if(functions.isValueNull(partnerLogoFlag) && partnerLogoFlag.equalsIgnoreCase("Y"))
                    {
                    %>
                    <div class="logo">
                        <img class="images-style1" style="width: 100%;" src="/images/merchant/<%=commonValidatorVO.getMerchantDetailsVO().getLogoName()%>">
                    </div>
                    <%
                        }
                    %>
                </div>
                <%
                    }
                %>

                <div class="modal-body">
                    <div>
                        <div class="data-head-title">
                            <%=cardName%>
                        </div>

                        <div class="data-section">
                            <%
                                if(cardName.equalsIgnoreCase("Bank Transfer"))
                            {
                            %>

                            <%
                                if(functions.isValueNull(name)){
                            %>
                            <div class="data-row">
                                <div class="data-label"> Bank Name </div>
                                <div class="data-value"> <%=ESAPI.encoder().encodeForHTML(name)%> </div>
                            </div>
                            <%
                                }
                                if(functions.isValueNull(code)){
                            %>
                            <div class="data-row">
                                <div class="data-label"> Bank Code </div>
                                <div class="data-value"> <%=ESAPI.encoder().encodeForHTML(code)%> </div>
                            </div>
                            <%
                                }
                                if(functions.isValueNull(account)){
                            %>
                            <div class="data-row">
                                <div class="data-label"> Account number </div>
                                <div class="data-value"> <%=ESAPI.encoder().encodeForHTML(account)%> </div>
                            </div>
                            <%
                                }
                                if(functions.isValueNull(agency)){
                            %>
                            <div class="data-row">
                                <div class="data-label"> Agency number </div>
                                <div class="data-value"> <%=ESAPI.encoder().encodeForHTML(agency)%> </div>
                            </div>
                            <%
                                }
                                if(functions.isValueNull(account_operation)){
                            %>
                            <div class="data-row">
                                <div class="data-label"> Account Type </div>
                                <div class="data-value"> <%=ESAPI.encoder().encodeForHTML(account_operation)%> </div>
                            </div>
                            <%
                                }
                                if(functions.isValueNull(beneficiary)){
                            %>
                            <div class="data-row">
                                <div class="data-label"> Beneficiary </div>
                                <div class="data-value"> <%=ESAPI.encoder().encodeForHTML(beneficiary)%> </div>
                            </div>
                            <%
                                }
                                if(functions.isValueNull(document)){
                            %>
                            <div class="data-row">
                                <div class="data-label"> Document  </div>
                                <div class="data-value"> <%=ESAPI.encoder().encodeForHTML(document )%> </div>
                            </div>
                            <%
                                }
                            %>
                            <%}%>
                            <%
                                if("PICPAY".equalsIgnoreCase(cardName) || "PIX".equalsIgnoreCase(cardName))
                                {
                            %>
                            <div class="tab-pane active" id="QRCode" style="text-align: center">
                                <form id="QRForm" class="form-style" method="post" >
                                    <input type="hidden" id="QR_ctoken" value="<%=ctoken%>" />

                                    <div align="center">
                                        <div><img src="/images/merchant/QRCodes/MyQR_<%=trackingId%>.png" alt="MyQR" /> </div>
                                    </div>
                                    <%--<div class="data-row" style="word-wrap: anywhere">
                                        <div class="data-label"> Reference  </div>
                                        <div class="data-value"> <%=ESAPI.encoder().encodeForHTML(reference )%> </div>
                                    </div>--%>
                                    <div class="data-row">
                                        <div class="data-label"> Beneficiary </div>
                                        <div class="data-value"> <%=ESAPI.encoder().encodeForHTML(beneficiary)%> </div>
                                    </div>
                                    <div id="successMsg" style="font-weight: bold;font-size: 25px;" class="form-label hide">

                                    </div>
                                </form>
                            </div>
                            <%
                                }
                            %>
                            <%
                                if(functions.isValueNull(amount)){
                            %>
                            <div class="data-row">
                                <div class="data-label">Amount </div>
                                <div class="data-value"><%=amount%> </div>
                            </div>
                            <%}%>
                        </div>
                    </div>
                </div>
                <%
                    String align = "";
                    if(!partnerLogoFlag.equalsIgnoreCase("N") || !sisaLogoFlag.equalsIgnoreCase("N") || !powerBy.equalsIgnoreCase("N") )
                    {
                %>
                <div class="modal-footer" style="padding: 7px 12px;">
                    <%
                        if(functions.isValueNull(partnerLogoFlag) && partnerLogoFlag.equalsIgnoreCase("Y"))
                        {
                    %>
                    <div class="footer-logo" style="text-align: left;">
                        <img class="logo-style" src="/images/merchant/<%=logoName%>">
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
                            align="text-align: left";
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
                        <img class="logo-style" src="/images/merchant/PCI.png">
                    </div>
                    <%
                        }
                        if(functions.isValueNull(powerBy) && powerBy.equalsIgnoreCase("Y"))
                        {
                    %>
                    <div class="footer-logo" style="text-align: right;">
                        <img class="logo-style"  src="/images/merchant/poweredBy_logo.png">
                    </div>
                    <%
                    }
                    else if(functions.isValueNull(securityLogoFlag) && securityLogoFlag.equalsIgnoreCase("Y")) {
                    %>
                    <div class="footer-logo" style="text-align: right;">
                        <img class="logo-style" src="/images/merchant/security.png" style="width: auto;margin: 0px 15px;">
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
<!-- modal ends -->
<form name="successForm" action="/transaction/checkConfirmation" method="post">
    <input type="hidden" name="success_status" id="success_status" value="" />
    <input type="hidden" name="trackingId" value="<%=trackingId%>" />
</form>
<script>
    <% if("PICPAY".equalsIgnoreCase(cardName) || "PIX".equalsIgnoreCase(cardName)){%>
        console.log("QR Started ");

        var confirm_status = "";
        var timeInterval = 5000;

        var apiCall  =  setInterval('status()',timeInterval);
        var timeout  = setTimeout(function(){
            clearInterval(apiCall);
            console.log("end timeout >>>>> ",new Date())
            document.getElementById("success_status").value = "pending";
            document.successForm.submit();
        },180000);

        function status()
        {

            console.log(new Date());

            if(document.getElementById("QRCode").classList.contains("active"))
            {
                var token = document.getElementById("QR_ctoken").value;

                $.ajax({
                    url: '/transaction/checkConfirmation?ctoken=' + token,
                    async: false,
                    type: 'POST',
                    data: {trackingId: "<%=trackingId%>", param: "ajax"},
                    success: function (data, status)
                    {
                        console.log(data);
                        confirm_status = data;
                    },
                    error: function (xhr, status, error)
                    {
                        console.log(status, error);
                    }
                });

                console.log("AFTER AJAX  ------- status ---------", confirm_status);

                if (confirm_status.status == "success" || confirm_status.status == "fail")
                {
                    document.getElementById("successMsg").classList.remove("hide");
                    document.getElementById("successMsg").innerHTML = confirm_status.status == "success" ? "Transaction Successful !" : "Transaction Failed !";
                    document.getElementById("success_status").value = confirm_status.status;
                    clearInterval(apiCall);
                    document.successForm.submit();
                }

            }

        }
    <%}%>
</script>
</body>
</html>