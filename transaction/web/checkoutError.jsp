<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO" %>
<%@ page import="com.payment.exceptionHandler.errorcode.ErrorCodeUtils" %>
<%@ page import="com.directi.pg.Checksum" %>
<%@ page import="com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName" %>
<%@ page import="com.manager.vo.TransactionDetailsVO" %>
<%@ page import="payment.AsyncNotificationService" %>
<%@ page import="payment.TransactionUtility" %>
<%@ page import="org.owasp.esapi.ESAPI" %>

<%--
  Created by IntelliJ IDEA.
  User: Rihen
  Date: 6/27/2018
  Time: 4:19 PM
  To change this template use File | Settings | File Templates.
--%>
<script>
  if ( window.history.replaceState ) {
    window.history.replaceState( null, null, window.location.href );
  }
</script>
<%

  response.setHeader("Pragma", "No-cache");
  response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
  response.setDateHeader("Expires", -1);


%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--<script type="text/javascript">
  var res=true;
  console.log("indise backpress function"+res);
  function pressback(){window.history.forward(); res=false;}
  setTimeout("pressback()",0);
  window.onload=function(){null};
  console.log("afer execute backpress function"+res);

</script>--%>

<%
  Functions functions=new Functions();
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetail");

  if(null==standardKitValidatorVO)
    standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");


  ResourceBundle rb = null;
  ResourceBundle rbError = null;
  String multiLanguage = "";
  String lang = "";

  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getLanguage()))
  {
    lang = standardKitValidatorVO.getAddressDetailsVO().getLanguage().toLowerCase();
    if ("ja".equalsIgnoreCase(lang))
    {
      rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ja");
      rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_ja");
    }
    else if ("ro".equalsIgnoreCase(lang))
    {
      rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ro");
      rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_en-us");
    }
    else if ("sp".equalsIgnoreCase(lang))
    {
      rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_sp");
      rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_en-us");
    }
    else
    {
      rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
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
        rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ja");
        rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_ja");
      }
      else if ("ro".equalsIgnoreCase(sLanguage[0]))
      {
        rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ro");
        rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_en-us");
      }
      else if ("sp".equalsIgnoreCase(sLanguage[0]))
      {
        rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_sp");
        rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_en-us");
      }
      else
      {
        rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
        rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_en-us");
      }
    }
    else
    {
      rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
      rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_en-us");
    }
  }
  else
  {
    rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
    rbError = LoadProperties.getProperty("com.directi.pg.CheckoutError_en-us");
  }
  String varERROR = rb.getString("VAR_ERROR");
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
  <link rel="stylesheet" href="/merchant/transactionCSS/css/raleway-font.css" type="text/css" >
  <link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap-4.4.1/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.1.0/css/all.css" integrity="sha384-lKuwvrZot6UHsBSfcMvOkWwlCMgc0TaWr+30HWe3a4ltaBwTZhyTEggF5tJv8tbt" crossorigin="anonymous">
  <link rel="stylesheet" href="/merchant/transactionCSS/css/bootstrap-float-label.min.css"/>
  <link rel="stylesheet" href="/merchant/transactionCSS/css/styles.css"/>
  <link rel="stylesheet" href="/merchant/transactionCSS/css/success.css"/>

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

    .background{
    <%=session.getAttribute("bodybgcolor")!=null?"background:"+session.getAttribute("bodybgcolor").toString():""%>;
    <%=session.getAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR") != null ? "background:"+session.getAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR").toString() : "" %>;
    }

    /* header */
    .header{
    <%=session.getAttribute("panelheading_color")!=null?"background-color:"+session.getAttribute("panelheading_color").toString()+"!important":""%>;
    <%=session.getAttribute("bodypanelfont_color")!=null?"color:"+session.getAttribute("bodypanelfont_color").toString()+"!important":""%>;
    <%=session.getAttribute("NEW_CHECKOUT_HEADER_FONT_COLOR")!=null?"color:"+session.getAttribute("NEW_CHECKOUT_HEADER_FONT_COLOR").toString()+"!important":""%>;
    <%=session.getAttribute("NEW_CHECKOUT_HEADERBACKGROUND_COLOR") != null ? "background-color:"+session.getAttribute("NEW_CHECKOUT_HEADERBACKGROUND_COLOR").toString() : "" %>;
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
    <%=session.getAttribute("NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR") != null ? "color:"+session.getAttribute("NEW_CHECKOUT_NAVIGATIONBAR_FONT_COLOR").toString() + "!important" : "" %>;
    <%=session.getAttribute("NEW_CHECKOUT_NAVIGATIONBAR_COLOR") != null ? "background-color:"+session.getAttribute("NEW_CHECKOUT_NAVIGATIONBAR_COLOR").toString() + "!important" : "" %>;
    }

    .error_body{
    <%=session.getAttribute("mainbackgroundcolor")!=null?"background-color:"+session.getAttribute("mainbackgroundcolor").toString()+"!important":""%>;
    <%=session.getAttribute("bodyfgcolor")!=null?"color:"+session.getAttribute("bodyfgcolor").toString()+"!important":""%>;
    <%=session.getAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR") != null ? "color:"+session.getAttribute("NEW_CHECKOUT_FULLBACKGROUND_COLOR").toString() : "" %>;
    }
    /* end of body */

  </style>


  <script type = "text/javascript" src="/merchant/transactionCSS/js/jquery.min.js"></script>
  <script type = "text/javascript" src="/merchant/transactionCSS/js/bootstrap-4.4.1/js/bootstrap.min.js" ></script>
  <script type = "text/javascript" src="/merchant/transactionCSS/js/content.min.js"></script>

</head>
<body style="font-family: Raleway;" class="background">

<%
  Logger log = new Logger("checkoutError.jsp");

  String redirect = "";
  String trackingid = "";
  String desc = "";
  String amount = "";
  String status = "";
  String clkey = "";
  String currency = "";

  redirect = standardKitValidatorVO.getTransDetailsVO().getRedirectUrl();
  if(functions.isValueNull(redirect)){
    redirect = ESAPI.encoder().encodeForHTML(redirect.replaceAll("\\<","").replaceAll("\\>",""));
  }

  if(functions.isValueNull(standardKitValidatorVO.getTrackingid()))
  {
    trackingid = standardKitValidatorVO.getTrackingid();
  }
  else{
    trackingid = "";
  }
  desc = standardKitValidatorVO.getTransDetailsVO().getOrderId();
  amount = standardKitValidatorVO.getTransDetailsVO().getAmount();
  status = "N";
  clkey = standardKitValidatorVO.getMerchantDetailsVO().getKey();
  currency =standardKitValidatorVO.getTransDetailsVO().getCurrency();


  String partnerId="";
  String logoName="";
  String error="";
  String merchantLogoFlag = "";
  String partnerLogoFlag = "";
  String mLogo = "";
  String supportName = "";

  if(request.getAttribute("error")!=null && !request.getAttribute("error").equals(""))
  {
    error = (String) request.getAttribute("error");
  }

  logoName = (String)session.getAttribute("merchantLogoName");
  merchantLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo();
  partnerLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
  mLogo = standardKitValidatorVO.getMerchantDetailsVO().getLogoName();
%>

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
              supportName = standardKitValidatorVO.getMerchantDetailsVO().getCompany_name();
          %>
            <img class="images-style1" style="width: 100%;" src="/images/merchant/<%=logoName%>">
          <%
          }
          else if(functions.isValueNull(partnerLogoFlag) && partnerLogoFlag.equalsIgnoreCase("Y") )
          {
            supportName = standardKitValidatorVO.getMerchantDetailsVO().getPartnerName();
          %>
            <img class="images-style1" style="width: 100%;" src="/images/merchant/<%=mLogo%>">
          <%
            }
          %>

          </div>
          <button type="button" id="closebtnError" class="close hide" data-dismiss="modal" aria-label="Close" onclick="getResponseFromCheckout('<%=redirect%>',document.getElementsByName('errorCancelForm')[0])">
            <span aria-hidden="true">&times;</span>
          </button>
        </div>

        <div class="modal-body">
          <div class="top-bar">
            <%--<div class="top-right hide" id="topRightErrorPage">
              <span onclick="backToHome()"><i class="fas fa-home"></i></span>
            </div>--%>
            <div class="top-left" id="topLeft" >
              <%
                if( !error.contains("30029")){
              %>
              <span id="backArrowErrorPage" style="float: left;width: 20px;cursor: pointer;" onclick="cancelError();getResponseFromCheckout('<%=redirect%>',document.getElementsByName('errorCancelForm')[0])" ><i class="fas fa-angle-left"></i></span>
              <%
                }
              %>
              <div id="payMethod" class="truncate" style="text-align: center;font-weight: bold"><%=varERROR%></div>
            </div>
          </div>

          <div style="text-align: left;padding: 10px 12px 10px 0px;/*height: 302px*/height:349px;overflow: auto;" class="error_body">
            <ul>
              <%
                log.debug("error in error page ----" + error);
                if(error!=null)
                {
                  if(error.contains("<BR>"))
                  {
                    String errorMsg[] = error.split("<BR>");

                    for(String singleError : errorMsg)
                    {
              %>
              <li>
                <%--<%=rbError.getString(singleError).replaceAll("\\*",supportName)%>--%>
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

                }
              %>
            </ul>
          </div>

        </div>

      </div>
    </div>
  </div>
</div>
<!-- modal ends -->
<%
  String notificationUrl = standardKitValidatorVO.getTransDetailsVO().getNotificationUrl();
  if(functions.isValueNull(notificationUrl))
  {
   /* TransactionUtility transactionUtility = new TransactionUtility();
    TransactionDetailsVO transactionDetailsVO1=transactionUtility.getTransactionDetails(standardKitValidatorVO);
    AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
    asyncNotificationService.sendNotification(transactionDetailsVO1,trackingid,"failed","Transaction Failed","");*/
  }

  ErrorCodeVO errorCodeVO = new ErrorCodeVO();
  ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
  String checkSum = Checksum.generateChecksumForStandardKit(trackingid, desc, amount, status, clkey);
  errorCodeVO = errorCodeUtils.getRejectedErrorCode(ErrorName.TRANSACTION_REJECTED);

  String ApiCode = "";
  String ApiDescription = "";

  if(errorCodeVO.getApiCode() != null){
    ApiCode = errorCodeVO.getApiCode();
  }
  if(errorCodeVO.getApiDescription() != null){
    ApiDescription = errorCodeVO.getApiDescription();
  }

%>
<form action="<%=redirect%>" method="post" name="errorCancelForm">
  <input type="hidden" name="trackingid" value="<%=trackingid%>">
  <input type="hidden" name="paymentId" value="<%=trackingid%>">
  <input type="hidden" name="desc" value="<%=desc%>">
  <input type="hidden" name="merchantTransactionId" value="<%=desc%>">
  <input type="hidden" name="amount" value="<%=amount%>">
  <input type="hidden" name="currency" value="<%=currency%>">
  <input type="hidden" name="status" value="N">
  <input type="hidden" name="checksum" value="<%=checkSum%>">
  <input type="hidden" name="resultCode" value="<%=errorCodeVO.getApiCode()%>">
  <input type="hidden" name="resultDescription" value="<%=errorCodeVO.getApiDescription()%>">

<%--    <input type="hidden" name="trackingid" value="<%=ESAPI.encoder().encodeForHTML(trackingid.replaceAll("\\<","").replaceAll("\\>",""))%>">
    <input type="hidden" name="paymentId" value="<%=ESAPI.encoder().encodeForHTML(trackingid.replaceAll("\\<","").replaceAll("\\>",""))%>">
    <input type="hidden" name="desc" value="<%=ESAPI.encoder().encodeForHTML(desc.replaceAll("\\<","").replaceAll("\\>",""))%>">
    <input type="hidden" name="merchantTransactionId" value="<%=ESAPI.encoder().encodeForHTML(desc.replaceAll("\\<","").replaceAll("\\>",""))%>">
    <input type="hidden" name="amount" value="<%=ESAPI.encoder().encodeForHTML(amount.replaceAll("\\<","").replaceAll("\\>",""))%>">
    <input type="hidden" name="currency" value="<%=ESAPI.encoder().encodeForHTML(currency.replaceAll("\\<","").replaceAll("\\>",""))%>">
    <input type="hidden" name="status" value="N">
    <input type="hidden" name="checksum" value="<%=ESAPI.encoder().encodeForHTML(checkSum.replaceAll("\\<","").replaceAll("\\>",""))%>">
    <input type="hidden" name="resultCode" value="<%=ESAPI.encoder().encodeForHTML(ApiCode.replaceAll("\\<","").replaceAll("\\>",""))%>">
  <input type="hidden" name="resultDescription" value="<%=ESAPI.encoder().encodeForHTML(ApiDescription.replaceAll("\\<","").replaceAll("\\>",""))%>">
  --%>
</form>

</body>
</html>