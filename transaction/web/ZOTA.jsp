<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%--
  Created by IntelliJ IDEA.
  User: Rihen
  Date: 2/10/2020
  Time: 5:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  HashMap paymentMap = standardKitValidatorVO.getMapOfPaymentCardType();

  String paymodeId = "30";
  String payMode = "ZOTA";

  Functions functions = new Functions();

  String merchantLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo();
  String partnerLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
  String consentFlag = standardKitValidatorVO.getMerchantDetailsVO().getConsentFlag();

  String termsLink = "";
  String privacyLink = "";
  String target = "";
  if(!merchantLogoFlag.equals(null) && merchantLogoFlag.equalsIgnoreCase("Y"))
  {
    termsLink = standardKitValidatorVO.getMerchantDetailsVO().getTcUrl();
    privacyLink = standardKitValidatorVO.getMerchantDetailsVO().getPrivacyPolicyUrl();
    target = "target=\"_blank\"";
    if(functions.isEmptyOrNull(termsLink))
      termsLink = "/#";
    if(functions.isEmptyOrNull(privacyLink))
      privacyLink = "/#";

  }
  else if(!partnerLogoFlag.equals(null) && partnerLogoFlag.equalsIgnoreCase("Y"))
  {
    termsLink = standardKitValidatorVO.getMerchantDetailsVO().getPartnerTcUrl();
    privacyLink = standardKitValidatorVO.getMerchantDetailsVO().getPartnerPrivacyUrl();
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


  String email ="";
  String street = "";
  String city = "";
  String zip = "";
  String firstName="";
  String lastName="";
  String phonecc = "";
  String phoneno = "";

  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getTelnocc())){
    phonecc =  standardKitValidatorVO.getAddressDetailsVO().getTelnocc();
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getPhone())){
    phoneno =  standardKitValidatorVO.getAddressDetailsVO().getPhone();
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getFirstname())){
    firstName = ESAPI.encoder().encodeForHTML(standardKitValidatorVO.getAddressDetailsVO().getFirstname());
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getLastname())){
    lastName = ESAPI.encoder().encodeForHTML(standardKitValidatorVO.getAddressDetailsVO().getLastname());
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getEmail())){
    email = ESAPI.encoder().encodeForHTML(standardKitValidatorVO.getAddressDetailsVO().getEmail());
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getStreet())){
    street =  standardKitValidatorVO.getAddressDetailsVO().getStreet();
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getCity())){
    city =  standardKitValidatorVO.getAddressDetailsVO().getCity();
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getZipCode())){
    zip =  standardKitValidatorVO.getAddressDetailsVO().getZipCode();
  }


  ResourceBundle rb = null;
  String lang = "";
  String multiLanguage = "";

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
    else
    {
      rb = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
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


  String varPersonalinfo=rb.getString("VAR_PERSONALINFO");
  String varEmailid=rb.getString("VAR_EMAILID");
  String varFirstname=rb.getString("VAR_FIRSTNAME");
  String varLastname=rb.getString("VAR_LASTNAME");
  String varCountry=rb.getString("VAR_COUNTRY");
  String varNext=rb.getString("VAR_NEXT");
  String varAddress=rb.getString("VAR_ADDRESS");
  String varCity=rb.getString("VAR_CITY");
  String varZip=rb.getString("VAR_ZIP");
  String consent1 = rb.getString("VAR_CONSENT_STMT1");
  String consent2 = rb.getString("VAR_CONSENT_STMT2");
  String consent3 = rb.getString("VAR_CONSENT_STMT3");
  String varPhonecc=rb.getString("VAR_PHONECC");
  String varPhoneno=rb.getString("VAR_PHONENO");
  String varAND=rb.getString("VAR_AND");

%>


<%--<div id="ZOTAOption" class="option-class">
  <ul class="nav nav-tabs" id="ZOTATab">

    <%

      List<String> cardList = (List<String>)paymentMap.get(paymodeId);
      for(String cardId : cardList)
      {
        String cardName = GatewayAccountService.getCardType(cardId);
        String cardImg = cardName+".png";
    %>
    <li class="tabs-li-wallet" onclick="zotapayHideShow('<%=cardName%>','<%=rb.getString(cardName)%>')">
      <a href="#<%=cardName%>"  data-toggle="tab" title="<%=cardName%>" aria-expanded="false">
        <img class="images-style" src="/images/merchant/images/<%=cardImg%>" alt="<%=cardName%>">
        <div class="label-style"><%=rb.getString(cardName)%></div>
      </a>
    </li>

    <%
      }
    %>
  </ul>
</div>--%>


<div class="tab-pane" id="ZOTA">
  <form id="zotaForm" class="form-style" method="post" >
    <div class="tab" id="add" style="padding-top: 22px;"> <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
      <div class="form-group has-float-label control-group-half">
        <input type="text" class="form-control input-control1" id="zpfirstname" placeholder=" "
               oninput="this.className = 'form-control input-control1'"  name="firstname" value="<%=firstName%>"  />
        <label for="zpfirstname" class="form-label"><%=varFirstname%></label>
      </div>
      <div class="form-group has-float-label control-group-half">
        <input type="text" class="form-control input-control1" id="zplastname" placeholder=" "
               oninput="this.className = 'form-control input-control1'"  name="lastname" value="<%=lastName%>" />
        <label for="zplastname" class="form-label"><%=varLastname%></label>
      </div>
      <div class="form-group has-float-label control-group-full" id="ZPEmail">
        <span class="input-icon"><i class="far fa-envelope"></i></span>
        <input type="email" class="form-control input-control1" id="email" placeholder=" " onfocusout="validateEmail(event.target.value ,'ZPEmail')"
               oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" autofocus />
        <label for="email" class="form-label"><%=varEmailid%></label>
      </div>

      <div class="form-group has-float-label control-group-half">
        <input type="text" class="form-control input-control1" name="phone-CC" id="phonecc-id" placeholder=" " onkeypress="return isNumberKey(event)"
               onkeyup="onPasteNumCheck(event)"  maxlength="3" onblur ="setPhoneCC('phonecc-id','phonecc')" value="<%=phonecc%>"
               oninput="this.className = 'form-control input-control1'" />
        <label for="phonecc-id" class="form-label"><%=varPhonecc%></label>
        <input type="hidden" id="phonecc"  name="phone-CC" value="<%=phonecc%>" />
      </div>
      <div class="form-group has-float-label control-group-half">
        <span class="input-icon-half"><i class="fas fa-phone"></i> </span>
        <input type="text" class="form-control input-control1" id="phonenophonecc" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
               oninput="this.className = 'form-control input-control1'" name="telno" value="<%=phoneno%>" />
        <label for="phonenophonecc" class="form-label"><%=varPhoneno%></label>
      </div>

      <div class="form-group has-float-label control-group-full" id="ZPAddress">
        <input type="text" class="form-control input-control1" id="address" placeholder=" " onfocusout="validateAddress(event.target.value ,'ZPAddress')"
               oninput="this.className = 'form-control input-control1'" name="street" value="<%=street%>"/>
        <label for="address" class="form-label"><%=varAddress%></label>
      </div>
      <div class="form-group has-float-label control-group-half" id="ZPCity" >
        <input type="text" class="form-control input-control1" id="city" placeholder=" " onfocusout="validateCity(event.target.value ,'ZPCity')"
               oninput="this.className = 'form-control input-control1'" name="city" value="<%=city%>"/>
        <label for="city" class="form-label"><%=varCity%></label>
      </div>
      <div class="form-group has-float-label control-group-half" id="ZPZip" >
        <input type="text" class="form-control input-control1" id="zip" placeholder=" " onfocusout="validateZip(event.target.value ,'ZPZip')"
               oninput="this.className = 'form-control input-control1'" name="zip" value="<%=zip%>"/>
        <label for="zip" class="form-label"><%=varZip%></label>
      </div>
      <div class="form-group has-float-label control-group-half" id="ZPCountry">
        <div class="dropdown">
          <input id="country_input_zp" class="form-control input-control1"  placeholder=" " onfocusout="validateCountry(event.target.value ,'ZPCountry')"
                 onblur="pincodecc('country_input_zp','country_zp');" onkeypress="return isLetterKey(event)" oninput="this.className = 'form-control input-control1'" />
          <label for="country_input_zp" class="form-label"><%=varCountry%></label>
          <input type="hidden" id="country_zp"  name="country_input" >
        </div>
      </div>

      <script>
        setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_zp');
        pincodecc('country_input_zp','country_zp');
        validateCountry(document.getElementById('country_input_zp').value ,'ZPCountry');
      </script>

    </div>


    <%
      if(consentFlag.equalsIgnoreCase("Y"))
      {
    %>
    <div class="form-group terms-checkbox hide" id="terms">
      <input type="checkbox" class="" style="width: 5%" id="TC_zp" onclick="disablePayButton(this)" >
      <label for="TC_zp" style="font-size: 85%;position: absolute;margin: 0px;">
        <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
      </label>
    </div>
    <%
      }
    %>
    <div style="overflow:hidden" >
      <div style="float:right;"> <!-- Previous button-->
        <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'zotaForm')">
          <i class="fas fa-angle-left"></i>
        </div>
      </div>
    </div>

    <div class="pay-btn">
      <button type="button" class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1,'zotaForm')"  tabindex="0">
        <%=varNext%>
      </button>
    </div>
    <input type="hidden" name="paymentBrand" id="paymentBrand" value="ZOTA">
    <jsp:include page="requestParameters.jsp">
      <jsp:param name="paymentMode" value="<%=payMode%>" />
    </jsp:include>

  </form>
</div>
