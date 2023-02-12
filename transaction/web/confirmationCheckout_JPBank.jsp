<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.directi.pg.Logger" %>
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
<%@ page import="com.directi.pg.core.valueObjects.JPBankTransferVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>

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
    Functions functions=new Functions();
    CommonValidatorVO commonValidatorVO= (CommonValidatorVO) request.getAttribute("transDetail");
    Logger log = new Logger("confirmationCheckout_JPBank.jsp");

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
        logoName = commonValidatorVO.getMerchantDetailsVO().getLogoName();
        powerBy = commonValidatorVO.getMerchantDetailsVO().getPoweredBy();
        sisaLogoFlag = commonValidatorVO.getMerchantDetailsVO().getSisaLogoFlag();
        securityLogoFlag = commonValidatorVO.getMerchantDetailsVO().getSecurityLogo();
        partnerLogoFlag = commonValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
        merchantLogoFlag = commonValidatorVO.getMerchantDetailsVO().getMerchantLogo();
//        trackingId=commonValidatorVO.getTrackingid();
        // amount=commonValidatorVO.getTransactionDetailsVO().getAmount();
    }


    JPBankTransferVO jpBankTransferVO = (JPBankTransferVO) request.getAttribute("transRespDetails");
    String bankName = "";
    String shitenName = "";
    String kouzaType = "";
    String kouzaMeigi = "";
    String company = "";
    String kouzaNm = "";
    String result = "";
    String shitenNm = "";
    String bid = "";
    String tel = "";
    String email = "";
    String nameId = "";

    bankName =jpBankTransferVO.getBankName();
    amount=jpBankTransferVO.getAmount();
    shitenName =jpBankTransferVO.getShitenName();
    kouzaType =jpBankTransferVO.getKouzaType();
    kouzaMeigi =jpBankTransferVO.getKouzaMeigi();
    nameId = jpBankTransferVO.getNameId();
    kouzaNm =jpBankTransferVO.getKouzaNm();
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
                    <div class="top-bar">
                        <div class="header-section">
                            <div style="padding: 0 0 5px 0;">
                                <span style="color:red">【重要】</span>お振込時には必ず「口座名義」の前に7桁の<span style="color:red">「お振込ID」</span>をご入力下さい。
                                振込元名義：<span style="color:red">7桁のお振込ID + 口座名義</span>（例）1234567カワセタロウ
                            </div>
                            <div>
                                [Important] Please write the <span style="color:red"> transfer ID </span> before <u>your name</u> in the transfer holder's field
                            </div>
                        </div>
                    </div>
                    <div>
                        <div class="data-head-title">
                            Payee Information 振込先情報
                        </div>
                        <div class="data-section">
                            <%
                                if(functions.isValueNull(bankName)){
                            %>
                            <div class="data-row">
                                <div class="data-label"> Bank Name 銀行名 </div>
                                <div class="data-value"> <%=ESAPI.encoder().encodeForHTML(bankName)%> </div>
                            </div>
                            <%
                                }
                                if(functions.isValueNull(shitenName)){
                            %>
                            <div class="data-row">
                                <div class="data-label"> Branch Name 支店名 </div>
                                <div class="data-value"> <%=ESAPI.encoder().encodeForHTML(shitenName)%> </div>
                            </div>
                            <%
                                }
                                if(functions.isValueNull(kouzaType)){
                            %>
                            <div class="data-row">
                                <div class="data-label"> Account Type  口座の種類 </div>
                                <div class="data-value"> <%=ESAPI.encoder().encodeForHTML(kouzaType)%> </div>
                            </div>
                            <%
                                }
                                if(functions.isValueNull(kouzaNm)){
                            %>
                            <div class="data-row">
                                <div class="data-label"> Account Number 口座番号 </div>
                                <div class="data-value"> <%=ESAPI.encoder().encodeForHTML(kouzaNm)%> </div>
                            </div>
                            <%
                                }
                                if(functions.isValueNull(kouzaMeigi)){
                            %>
                            <div class="data-row">
                                <div class="data-label"> Account Holder 口座名義 </div>
                                <div class="data-value"> <%=ESAPI.encoder().encodeForHTML(kouzaMeigi)%> </div>
                            </div>
                            <%
                                }
                                if(functions.isValueNull(nameId)){
                            %>
                            <div class="data-row">
                                <div class="data-label" style="color:red"> Transfer ID お振込みID </div>
                                <div class="data-value" style="color:red"> <%=ESAPI.encoder().encodeForHTML(nameId)%> </div>
                            </div>
                            <%
                                }
                                if(functions.isValueNull(amount)){
                            %>
                            <div class="data-row">
                                <div class="data-label"> Amount of Money 金額 </div>
                                <div class="data-value"> <b>¥</b>  <%=amount%> </div>
                            </div>
                            <%}%>
                        </div>
                    </div>
                    <div class="modal-body">
                        <div class="top-bar">
                            <div class="support-sec">
                                <div style="padding: 0 0 5px 0"> 決済に関するお問い合わせはお気軽にお電話ください </div>
                                <div>【電話】03-4413-2113 &nbsp &nbsp 【email】jpsupport@deltapay.biz</div>
                            </div>
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

</body>
</html>