<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.google.zxing.qrcode.QRCodeWriter" %>
<%@ page import="com.google.zxing.common.BitMatrix" %>
<%@ page import="com.google.zxing.BarcodeFormat" %>
<%@ page import="java.nio.file.Path" %>
<%@ page import="java.nio.file.FileSystems" %>
<%@ page import="com.google.zxing.client.j2se.MatrixToImageWriter" %>
<%@ page import="com.directi.pg.TransactionLogger" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.payment.common.core.CommTransactionDetailsVO" %>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 5/24/2018
  Time: 5:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  HashMap paymentMap                      = standardKitValidatorVO.getMapOfPaymentCardType();
  TransactionLogger transactionLogger     = new TransactionLogger("Wallet.jsp");

  String paymodeId  = "3";
  String payMode    = "EW";

  Functions functions       = new Functions();
  String merchantLogoFlag   = standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo();
  String partnerLogoFlag    = standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
  String consentFlag        = standardKitValidatorVO.getMerchantDetailsVO().getConsentFlag();

  String termsLink      = "";
  String privacyLink    = "";
  String target         = "";

  String email    = "";
  String phonecc  = "";
  String phoneno  = "";

  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getEmail())){
    email =  standardKitValidatorVO.getAddressDetailsVO().getEmail();
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getTelnocc())){
    phonecc =  standardKitValidatorVO.getAddressDetailsVO().getTelnocc();
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getPhone())){
    phoneno =  standardKitValidatorVO.getAddressDetailsVO().getPhone();
  }


  if(!merchantLogoFlag.equals(null) && merchantLogoFlag.equalsIgnoreCase("Y"))
  {
    termsLink     = standardKitValidatorVO.getMerchantDetailsVO().getTcUrl();
    privacyLink   = standardKitValidatorVO.getMerchantDetailsVO().getPrivacyPolicyUrl();
    target        = "target=\"_blank\"";
    if(functions.isEmptyOrNull(termsLink))
      termsLink = "/#";
    if(functions.isEmptyOrNull(privacyLink))
      privacyLink = "/#";

  }
  else if(!partnerLogoFlag.equals(null) && partnerLogoFlag.equalsIgnoreCase("Y"))
  {
    termsLink   = standardKitValidatorVO.getMerchantDetailsVO().getPartnerTcUrl();
    privacyLink = standardKitValidatorVO.getMerchantDetailsVO().getPartnerPrivacyUrl();
    target      = "target=\"_blank\"";

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

  ResourceBundle rb1    = LoadProperties.getProperty("com.directi.pg.QRCode");
  ResourceBundle rb     = null;
  String lang           = "";
  String multiLanguage  = "";

  transactionLogger.error("Inside Wallet.jsp >>>>>>>>>>>>>");
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getLanguage()))
  {
    lang = standardKitValidatorVO.getAddressDetailsVO().getLanguage().toLowerCase();
    if ("ja".equalsIgnoreCase(lang))
    {
      rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ja");
    }
    else if ("bg".equalsIgnoreCase(lang))
    {
      rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_bg");
    }
    else if ("sp".equalsIgnoreCase(lang))
    {
      rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_sp");
    }
    else
    {
      rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
    }
  }
  else if(functions.isValueNull(request.getHeader("Accept-Language")))
  {
    multiLanguage       = request.getHeader("Accept-Language");
    String sLanguage[]  = multiLanguage.split(",");
    if(functions.isValueNull(sLanguage[0]))
    {
      if ("ja".equalsIgnoreCase(sLanguage[0]))
      {
        rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ja");
      }
      else if ("bg".equalsIgnoreCase(sLanguage[0]))
      {
        rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_bg");
      }
      else
      {
        rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
      }
    }
    else
    {
      rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
    }
  }
  else
  {
    rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
  }

 // String cardName = "";
  String varPersonalinfo  = rb.getString("VAR_PERSONALINFO");
  String varEmailid       = rb.getString("VAR_EMAILID");
  String varPhonecc       = rb.getString("VAR_PHONECC");
  String varPhoneno       = rb.getString("VAR_PHONENO");
  String varNext          = rb.getString("VAR_NEXT");
  String varFirstname     = rb.getString("VAR_FIRSTNAME");
  String varLastname      = rb.getString("VAR_LASTNAME");
  String varCustomerid    = rb.getString("VAR_CUSTOMERID");
  String consent1         = rb.getString("VAR_CONSENT_STMT1");
  String consent2         = rb.getString("VAR_CONSENT_STMT2");
  String consent3         = rb.getString("VAR_CONSENT_STMT3");
  String varAND           = rb.getString("VAR_AND");
  String varAddress       = rb.getString("VAR_ADDRESS");
  String varBirthdate     = rb.getString("VAR_BIRTHDATE");
  String varName          = rb.getString("VAR_NAME");
  String varCountry       = rb.getString("VAR_COUNTRY");
  String varAccountingnumber  = rb.getString("VAR_ACCOUNTINGNUMBER");
  String varIdentifynumber    = rb.getString("VAR_IDENTIFYNUMBER");
  String varVerificationcode  = rb.getString("VAR_VERIFICATIONCODE");
  String multiCurrency        = standardKitValidatorVO.getMerchantDetailsVO().getMultiCurrencySupport();

%>


<div id="WalletOption" class="option-class">
  <ul class="nav nav-tabs" id="WalletTab">

    <%
      HashMap terminalMap   = standardKitValidatorVO.getTerminalMap();
      TerminalVO terminalVO = null;

      System.out.println("Terminal Map is ===================> "+terminalMap);


      List<String> cardList = (List<String>)paymentMap.get(paymodeId);
      for(String cardId : cardList)
      {
        System.out.println("Inside 1 Wallet.jsp >>>>>"+cardId);
        String cardName = GatewayAccountService.getCardType(cardId);
        System.out.println("Inside 2 Wallet.jsp >>>>>"+cardName);
        cardName        = cardName.replaceAll(" ", "_");
        String cardImg  = cardName+".png";

    %>
    <li class="tabs-li-wallet" onclick="walletHideShow('<%=cardName%>','<%=rb.getString(cardName)%>')">
      <a href="#<%=cardName%>"  data-toggle="tab" title="<%=cardName%>" aria-expanded="false">
        <img class="images-style" src="/images/merchant/images/wallet/<%=cardImg%>" alt="<%=cardName%>">
        <div class="label-style"><%=rb.getString(cardName)%></div>
      </a>
    </li>

    <%
      }

    %>
  </ul>
</div>
<div class="tab-content">

  <%
    if(cardList.contains("24"))
    {
  %>
  <!--SKRILL-24-->
  <div class="tab-pane" id="SKRILL">
    <form id="skrillForm" class="form-style" method="post">

      <div class="tab"><p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-full" id="SkrillEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="skrillemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'SkrillEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="skrillemail" class="form-label"><%=varEmailid%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'skrillForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_skrill" onclick="disablePayButton(this)">
        <label for="TC_skrill" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'skrillForm')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="SKRILL">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>

    </form>
  </div>
  <%
    }
    if(cardList.contains("25"))
    {
  %>
  <!--QIWI-25-->
  <div class="tab-pane" id="QIWI">
    <form id="qiwiForm" class="form-style" method="post">
      <div class="tab"><p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-full" id="QiwiEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="qiwiemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'QiwiEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="qiwiemail" class="form-label"><%=varEmailid%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="qiwifirstname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="qiwifirstname" class="form-label"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="qiwilastname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="lastname" />
          <label for="qiwilastname" class="form-label"><%=varLastname%></label>
        </div>

        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phone-CC" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"  maxlength="3"
                 oninput="this.className = 'form-control input-control1'"  name="telnocc" value="<%=phonecc%>" />
          <label for="phone-CC" class="form-label"><%=varPhonecc%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phoneno" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'"  name="telno" value="<%=phoneno%>" />
          <label for="phoneno" class="form-label"><%=varPhoneno%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'qiwiForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_qiwi" onclick="disablePayButton(this)">
        <label for="TC_qiwi" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'qiwiForm')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="QIWI">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("26"))
    {
  %>
  <!--YANDEX-26-->
  <div class="tab-pane" id="YANDEX">
    <form id="yandexForm" class="form-style" method="post">
      <div class="tab"><p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-full" id="YandexEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="yandexemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'YandexEmail')"
                 oninput="this.className = 'form-control input-control1'" name="emailaddr" value="<%=email%>" />
          <label for="yandexemail" class="form-label"><%=varEmailid%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="yandexfirstname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="yandexfirstname" class="form-label"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="yandexlastname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="lastname" />
          <label for="yandexlastname" class="form-label"><%=varLastname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phone-CC" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"  maxlength="3"
                 oninput="this.className = 'form-control input-control1'"  name="telnocc" value="<%=phonecc%>" />
          <label for="phone-CC" class="form-label"><%=varPhonecc%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phoneno" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'"  name="telno" value="<%=phoneno%>"/>
          <label for="phoneno" class="form-label"><%=varPhoneno%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'yandexForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_yandex" onclick="disablePayButton(this)">
        <label for="TC_yandex" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'yandexForm')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="YANDEX">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("28"))
    {
  %>
  <!--PERFECTMONEY-28-->
  <div class="tab-pane" id="PERFECTMONEY">
    <form id="perfectMoneyForm" class="form-style" method="post">
      <div class="tab"><p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-full" id="PerfectmoneyEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="perfectMoneyemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'PerfectmoneyEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="email" class="form-label"><%=varEmailid%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="perfectMoneyfirstname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="perfectMoneyfirstname" class="form-label"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="perfectMoneylastname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="lastname" />
          <label for="perfectMoneylastname" class="form-label"><%=varLastname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phone-CC" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" maxlength="3"
                 oninput="this.className = 'form-control input-control1'"  name="telnocc" value="<%=phonecc%>" />
          <label for="phone-CC" class="form-label"><%=varPhonecc%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phoneno" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'"  name="telno" value="<%=phoneno%>" />
          <label for="phoneno" class="form-label"><%=varPhoneno%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'perfectMoneyForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_perfectMoney" onclick="disablePayButton(this)">
        <label for="TC_perfectMoney" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'perfectMoneyForm')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="PERFECTMONEY">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("22"))
    {
  %>
  <!--NETELLER-22-->
  <div class="tab-pane" id="<%=rb.getString("NETELLER")%>">
    <form id="netellerForm" class="form-style" method="post">
      <div class="tab"><p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-full" id="NetellerEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="netelleremail" placeholder=" " onfocusout="validateEmail(event.target.value ,'NetellerEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="netelleremail" class="form-label"><%=varEmailid%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'netellerForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_neteller" onclick="disablePayButton(this)">
        <label for="TC_neteller" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'netellerForm')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="NETELLER">
      <input type="hidden" name="language" id="language" value="en_US">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>

  <%
    }
    if(cardList.contains("32"))
    {
  %>
  <!--JETON-32-->
  <div class="tab-pane" id="<%=rb.getString("JETON")%>">
    <form id="jetonForm" class="form-style" method="post">
      <div class="tab"><p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-full" id="JetonEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="jetonemail" placeholder=" "  onfocusout="validateEmail(event.target.value ,'JetonEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="jetonemail" class="form-label"><%=varEmailid%></label>
        </div>
<%--
        <div class="form-group has-float-label control-group-half" >
          <div class="dropdown">
            <input id="country_input_jeton" class="form-control input-control1" placeholder=" " onblur="pincodecc('country_input_jeton','country_jeton');"
                   oninput="this.className = 'form-control input-control1'" onkeypress="return isLetterKey(event)"  />
            <label for="country_input_jeton" class="form-label"><%=varCountry%></label>
            <input type="hidden" id="country_jeton"  name="country_input" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
          </div>
        </div>

        <script>
          setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_jeton');
          pincodecc('country_input_jeton','country_jeton');
        </script>
--%>

      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'jetonForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_jeton" onclick="disablePayButton(this)">
        <label for="TC_jeton" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'jetonForm')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="JETON">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>

  <%
    }
    if(cardList.contains("34"))
    {
  %>
  <!--EPAY-34-->
  <div class="tab-pane" id="<%=rb.getString("EPAY")%>">
    <form id="epayForm" class="form-style" method="post">
      <div class="tab"><p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="customerID" placeholder=" " onkeypress="return alphaNumCheck(event);" onkeyup="onPasteAlphaNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'"  name="customerBankId" maxlength="20" />
          <label for="customerID" class="form-label"><%=varCustomerid%></label>
        </div>
        <div class="form-group has-float-label control-group-full" id="EpayEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="epayemail" placeholder=" "  onfocusout="validateEmail(event.target.value ,'EpayEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="epayemail" class="form-label"><%=varEmailid%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'epayForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_epay" onclick="disablePayButton(this)">
        <label for="TC_epay" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'epayForm')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="EPAY">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("67"))
    {
  %>
  <!--PaySend-- 67-->
  <div class="tab-pane" id="PaySend">
    <form id="paysendForm" class="form-style" method="post">
      <div class="tab"><p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-full" id="PaySendEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="paysendemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'PaySendEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="paysendemail" class="form-label"><%=varEmailid%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="paysendfirstname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="paysendfirstname" class="form-label"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="paysendlastname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="lastname" />
          <label for="paysendlastname" class="form-label"><%=varLastname%></label>
        </div>
        <div class="form-group has-float-label control-group-full" id="PaySendBirthDate">
          <input type="date" class="form-control input-control1" id="paysendbirthdate" name="birthDate" placeholder=" "
                 oninput="this.className = 'form-control input-control1'" onfocusout="birthdayCheck('paysendbirthdate' ,'PaySendBirthDate')" />
          <label for="paysendbirthdate" style="color:#757575"><%=varBirthdate%></label>
        </div>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="paysendaddress" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="street" value="" />
          <label for="paysendaddress" class="form-label"><%=varAddress%></label>
        </div>


      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'paysendForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_paysend" onclick="disablePayButton(this)">
        <label for="TC_paysend" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'paysendForm')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="PaySend">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>

  <%
    }
    if(cardList.contains("172"))
    {
  %>
  <!--Paysec : 14-->
  <div class="tab-pane" id="Momo_wallet">
    <form method="post" name="Momo_walletForm" id="Momo_walletForm" action="/transaction/SingleCallCheckout">
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="Momo wallet">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("173"))
    {
  %>
  <!--Paysec : 14-->
  <div class="tab-pane" id="Zalo_Pay">
    <form method="post" name="Zalo_PayForm" id="Zalo_PayForm" action="/transaction/SingleCallCheckout">
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="Zalo Pay">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("174"))
    {
  %>
  <!--Paysec : 14-->
  <div class="tab-pane" id="VIETTEL_PAY">
    <form method="post" name="VIETTEL_PAYForm" id="VIETTEL_PAYForm" action="/transaction/SingleCallCheckout">
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="VIETTEL PAY">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("175"))
    {
  %>
  <!--Paysec : 14-->
  <div class="tab-pane" id="TRUEWALLET">
    <form method="post" name="TRUEWALLETForm" id="TRUEWALLETForm" action="/transaction/SingleCallCheckout">
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="TRUEWALLET">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
  %>

  <%-- Dynamic QR--%>
  <%
    if(cardList.contains("64"))
    {
      String trackingId = standardKitValidatorVO.getTrackingid();
      String amount = standardKitValidatorVO.getTransDetailsVO().getAmount();
      String currency = standardKitValidatorVO.getTransDetailsVO().getCurrency();
      String merchantTransactionID = standardKitValidatorVO.getTransDetailsVO().getOrderId();

      String text = trackingId+" "+amount+" "+currency+" "+merchantTransactionID;
//      String text = trackingId;
      String filePath = rb1.getString("PATH")+"MyQR_"+trackingId+".png";
      QRCodeWriter qrCodeWriter = new QRCodeWriter();
      BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200,200);

      Path path = FileSystems.getDefault().getPath(filePath);
      MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
  %>
  <div class="tab-pane" id="QR" style="text-align: center">
    <form id="QRForm" class="form-style" method="post">
      <div class="tab" style="height: 100%">
        <%-- start of qr code --%>
        <div>
          <div class="form-label" style="font-weight: bold;">
            Scan the QR Code in your wallet
          </div>
          <div>
            <img src="images/merchant/QRCodes/MyQR_<%=trackingId%>.png" alt="MyQR" />
          </div>
        </div>
        <%-- end of qr code --%>

        <%-- start of confirmation --%>
        <div id="successMsg" style="font-weight: bold;font-size: 25px;" class="form-label hide"></div>
        <%-- end of confirmation --%>
      </div>

    </form>
  </div>

  <form name="QRconfirmForm" action="/transaction/checkConfirmation" method="post">
    <input type="hidden" name="success_status" id="success_status" value="" />
    <input type="hidden" name="trackingId" id="QRTrackingID" value="<%=standardKitValidatorVO.getTrackingid()%>" />
  </form>
  <%
    }
  %>

  <%
    if(cardList.contains("167"))
    {
      System.out.println("Inside 167>>>>>>>>>>>>>>>>>");
      transactionLogger.error("Inside 167 wallet.jsp >>>>>>>>>>>>..");
  %>
  <div class="tab-pane" id="PICPAY">
    <form id="PicPayForm" class="form-style" method="post">
      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
        <br>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="PicPay_identifynumber" name="number" maxlength="20" placeholder=""  value=""
                 oninput="this.className = 'form-control input-control1'" />
          <label for="PicPay_identifynumber" style="color:#757575"><%=varIdentifynumber%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'PicPay')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_PICPAY" onclick="disablePayButton(this)">
        <label for="TC_PICPAY" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'TC_PICPAY')">
          <%="Pay"%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="PICPAY">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("176"))
    {
      System.out.println("Inside 176 >>>>>>>>>>");
      transactionLogger.error("Inside 176 wallet.jsp >>>>>>>>>>>>..");
  %>
  <div class="tab-pane" id="AME">
    <form id="AMEForm" class="form-style" method="post">
      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
        <br>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="AME_identifynumber" name="number" maxlength="20" placeholder=""  value=""
                 oninput="this.className = 'form-control input-control1'" />
          <label for="AME_identifynumber" style="color:#757575"><%=varIdentifynumber%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'AME')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_AME" onclick="disablePayButton(this)">
        <label for="TC_AME" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'TC_AME')">
          <%="Pay"%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="AME">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("177"))
    {
  %>
  <div class="tab-pane" id="Paypal">
    <form id="PaypalForm" class="form-style" method="post">
      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
        <br>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="Paypal_identifynumber" name="number" maxlength="20" placeholder=""  value=""
                 oninput="this.className = 'form-control input-control1'" />
          <label for="Paypal_identifynumber" style="color:#757575"><%=varIdentifynumber%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'Paypal')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_Paypal" onclick="disablePayButton(this)">
        <label for="TC_Paypal" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'TC_Paypal')">
          <%="Pay"%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="Paypal">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("178"))
    {
  %>
  <div class="tab-pane" id="Todito">
    <form id="ToditoForm" class="form-style" method="post">
      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
        <br>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="Todito_identifynumber" name="number" maxlength="20" placeholder=""  value=""
                 oninput="this.className = 'form-control input-control1'" />
          <label for="Todito_identifynumber" style="color:#757575"><%=varIdentifynumber%></label>
        </div>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="Todito_number" name="accountnumber" maxlength="20" placeholder=""  value=""
                 oninput="this.className = 'form-control input-control1'" />
          <label for="Todito_number" style="color:#757575"><%=varAccountingnumber%></label>
        </div>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="Todito_verifycode" name="verifycode" maxlength="20" placeholder=""  value=""
                 oninput="this.className = 'form-control input-control1'" />
          <label for="Todito_verifycode" style="color:#757575"><%=varVerificationcode%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'Todito')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_Todito" onclick="disablePayButton(this)">
        <label for="TC_Todito" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'TC_Todito')">
          <%="Pay"%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="Todito">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("179"))
    {
  %>
  <div class="tab-pane" id="TPaga">
    <form id="TPagaForm" class="form-style" method="post">
      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
        <br>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="TPaga_identifynumber" name="number" maxlength="20" placeholder=""  value=""
                 oninput="this.className = 'form-control input-control1'" />
          <label for="TPaga_identifynumber" style="color:#757575"><%=varIdentifynumber%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'TPaga')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_TPaga" onclick="disablePayButton(this)">
        <label for="TC_TPaga" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'TC_TPaga')">
          <%="Pay"%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="TPaga">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("180"))
    {
  %>
  <div class="tab-pane" id="Mach">
    <form id="MachForm" class="form-style" method="post">
      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
        <br>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="Mach_identifynumber" name="number" maxlength="20" placeholder=""  value=""
                 oninput="this.className = 'form-control input-control1'" />
          <label for="Mach_identifynumber" style="color:#757575"><%=varIdentifynumber%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'Mach')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_Mach" onclick="disablePayButton(this)">
        <label for="TC_Mach" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'TC_Mach')">
          <%="Pay"%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="Mach">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("181"))
    {
  %>
  <div class="tab-pane" id="Vita">
    <form id="VitaForm" class="form-style" method="post">
      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
        <br>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="Vita_identifynumber" name="number" maxlength="20" placeholder=""  value=""
                 oninput="this.className = 'form-control input-control1'" />
          <label for="Vita_identifynumber" style="color:#757575"><%=varIdentifynumber%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'Vita')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_Vita" onclick="disablePayButton(this)">
        <label for="TC_Vita" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'TC_Vita')">
          <%="Pay"%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="Vita">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("192"))
    {
  %>
  <!--Paysec : 14-->
  <div class="tab-pane" id="OVOWALLET">
    <form method="post" name="OVOWALLETForm" id="OVOWALLETForm" action="/transaction/SingleCallCheckout">
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="OVOWALLET">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("193"))
    {
  %>
  <!--Paysec : 14-->
  <div class="tab-pane" id="GOPAY">
    <form method="post" name="GOPAYForm" id="GOPAYForm" action="/transaction/SingleCallCheckout">
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="GOPAY">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
  %>
<%-- Static QR --%>
  <%
    if(cardList.contains("67"))
    {
      String memberId = standardKitValidatorVO.getMerchantDetailsVO().getMemberId();

      String text = memberId;
      String filePath = rb1.getString("PATH")+"MyQR_"+memberId+".png";
      QRCodeWriter qrCodeWriter = new QRCodeWriter();
      BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 200,200);

      Path path = FileSystems.getDefault().getPath(filePath);
      MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
  %>
  <div class="tab-pane" id="SQR" style="text-align: center">
    <form id="SQRForm" class="form-style" method="post">
      <div class="tab" style="height: 100%">
        <%-- start of qr code --%>
        <div>
          <div class="form-label" style="font-weight: bold;">
            Scan the QR Code in your wallet
          </div>
          <div>
            <img src="images/merchant/QRCodes/MyQR_<%=memberId%>.png" alt="MyQR" />
          </div>
        </div>
        <%-- end of qr code --%>

        <%-- start of confirmation --%>
        <div id="successMsg" style="font-weight: bold;font-size: 25px;" class="form-label hide"></div>
        <%-- end of confirmation --%>
      </div>

    </form>
  </div>
  <%
    }
  %>



  <%
    for(String cardId : cardList)
    {
      String cardName = GatewayAccountService.getCardType(cardId);
      cardName = cardName.replaceAll(" ", "_");
  %>
  <div class="tab-pane" id="<%=cardName%>">
    <form id="<%=cardName%>Form" class="form-style" method="post">

      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>

        <div class="form-group has-float-label control-group-full" id="<%=cardName%>Email">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="<%=cardName%>Email" placeholder=" " onfocusout="validateEmail(event.target.value ,'<%=cardName%>Email')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="<%=cardName%>Email" class="form-label"><%=varEmailid%></label>
        </div>

        <div class="form-group has-float-label control-group-half">
          <div class="dropdown">
            <input id="country_input_<%=cardName%>" class="form-control input-control1" placeholder=" " oninput="this.className = 'form-control input-control1'"
                   onblur="pincodecc('country_input_<%=cardName%>','country_<%=cardName%>','phonecc_id_<%=cardName%>','phonecc_<%=cardName%>'); " onkeypress="return isLetterKey(event)" />
            <label for="country_input_<%=cardName%>" class="form-label"><%=varCountry%></label>
            <input type="hidden" id="country_<%=cardName%>"  name="country_input" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
          </div>
        </div>

        <div class="form-group has-float-label control-group-half" style="width: 10% !important">
          <input type="text" class="form-control input-control1" name="phone-CC" id="phonecc_id_<%=cardName%>" placeholder=" " onkeypress="return isNumberKey(event)"
                 onkeyup="onPasteNumCheck(event)" maxlength="3" onblur="setPhoneCC('phonecc_id_<%=cardName%>','phonecc_<%=cardName%>')" value="<%=phonecc%>"
                 oninput="this.className = 'form-control input-control1'"/>
          <label for="phonecc_id_<%=cardName%>" class="form-label">CC</label>
          <input type="hidden" id="phonecc_<%=cardName%>" name="phone-CC" value="<%=phonecc%>"/>
        </div>
        <div class="form-group has-float-label control-group-half" style="width: 3% !important;padding: 6px 0px;">
          -
        </div>
        <div class="form-group has-float-label control-group-half" id="<%=cardName%>PhoneNum" style="width: 35% !important">
          <span class="input-icon-half"><i class="fas fa-phone"></i> </span>
          <input type="text" class="form-control input-control1" id="phone_<%=cardName%>" placeholder=" " onkeypress="return isNumberKey(event)"
                 onkeyup="onPasteNumCheck(event)" oninput="this.className = 'form-control input-control1'" maxlength="24" name="telno" value="<%=phoneno%>"/>
          <label for="phone_<%=cardName%>" class="form-label"><%=varPhoneno%></label>
        </div>

        <div class="form-group has-float-label control-group-half">
          <input class="form-control input-control1" id="<%=cardName%>AccNo" placeholder=" " type="text" onkeypress="return isNumberKey(event)"
                 maxlength="17" oninput="this.className = 'form-control input-control1'" name="accountnumber" />
          <label for="<%=cardName%>AccNo" class="form-label"><%=varAccountingnumber%></label>
        </div>

        <div class="form-group has-float-label control-group-half" id="<%=cardName%>Name">
          <input type="text" class="form-control input-control1" id="<%=cardName%>name" placeholder=" " oninput="this.className = 'form-control input-control1'" name="firstname"/>
          <label for="<%=cardName%>name" class="form-label"><%=varName%></label>
        </div>

        <script>
          setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_<%=cardName%>');
          pincodecc('country_input_<%=cardName%>','country_<%=cardName%>','phonecc_id_<%=cardName%>','phonecc_<%=cardName%>');
        </script>

      </div>

      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'<%=cardName%>Form')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_<%=cardName%>" onclick="disablePayButton(this)">
        <label for="TC_<%=cardName%>" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'<%=cardName%>Form')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="<%=cardName%>">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>

    </form>
  </div>
  <%
    }
  %>
  <script type="text/javascript">
    function BankES(dropDownId,inputID)
    {
      console.log("dropDownId >> "+dropDownId)
      console.log("inputID >> "+inputID)

      /*var select = document.getElementById(dropDownId);

       if (select.selectedIndex)
       {
       $("#paybutton").removeClass("disabledbutton");
       }
       else
       {
       $("#paybutton").addClass("disabledbutton");

       }*/

      document.getElementById(inputID).value = document.getElementById(dropDownId).value;
    }
  </script>



</div>