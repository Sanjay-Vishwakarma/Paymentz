<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.json.JSONObject" %>

<%--
  Created by IntelliJ IDEA.
  User: Rihen
  Date: 5/24/2018
  Time: 5:29 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%

  String ctoken = "";
  ctoken = (String) session.getAttribute("ctoken");
  Functions functions = new Functions();

  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  HashMap paymentMap = standardKitValidatorVO.getMapOfPaymentCardType();
  String currency = standardKitValidatorVO.getTransDetailsVO().getCurrency();
  String multiCurrency = standardKitValidatorVO.getMerchantDetailsVO().getMultiCurrencySupport();
  String merchantLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo();
  String partnerLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
  String consentFlag = standardKitValidatorVO.getMerchantDetailsVO().getConsentFlag();

  String termsLink = "";
  String privacyLink = "";
  String target = "";


  String email ="";
  String phonecc = "";
  String phoneno = "";
  String street = "";
  String city = "";
  String state = "";
  String zip = "";

  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getEmail())){
    email =  standardKitValidatorVO.getAddressDetailsVO().getEmail();
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getTelnocc())){
    phonecc =  standardKitValidatorVO.getAddressDetailsVO().getTelnocc();
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getPhone())){
    phoneno =  standardKitValidatorVO.getAddressDetailsVO().getPhone();
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getStreet())){
    street =  standardKitValidatorVO.getAddressDetailsVO().getStreet();
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getCity())){
    city =  standardKitValidatorVO.getAddressDetailsVO().getCity();
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getState())){
    state =  standardKitValidatorVO.getAddressDetailsVO().getState();
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getZipCode())){
    zip =  standardKitValidatorVO.getAddressDetailsVO().getZipCode();
  }

  if(functions.isValueNull(merchantLogoFlag) && merchantLogoFlag.equalsIgnoreCase("Y"))
  {
    termsLink = standardKitValidatorVO.getMerchantDetailsVO().getTcUrl();
    privacyLink = standardKitValidatorVO.getMerchantDetailsVO().getPrivacyPolicyUrl();
    target = "target=\"_blank\"";
    if(functions.isEmptyOrNull(termsLink))
      termsLink = "/#";
    if(functions.isEmptyOrNull(privacyLink))
      privacyLink = "/#";

  }
  else if(functions.isValueNull(partnerLogoFlag) && partnerLogoFlag.equalsIgnoreCase("Y"))
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
  HashMap terminalMap = standardKitValidatorVO.getTerminalMap();

  String paymodeId = "7";

  ResourceBundle rb1 = null;
  String lang = "";
  String multiLanguage = "";

  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getLanguage()))
  {
    lang = standardKitValidatorVO.getAddressDetailsVO().getLanguage().toLowerCase();
    if ("ja".equalsIgnoreCase(lang))
    {
      rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ja");
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

  String varEmailid=rb1.getString("VAR_EMAILID");
  String varCountry=rb1.getString("VAR_COUNTRY");
  String varPhonecc=rb1.getString("VAR_PHONECC");
  String varPhoneno=rb1.getString("VAR_PHONENO");
  String varNext=rb1.getString("VAR_NEXT");
  String varAddressinfo=rb1.getString("VAR_ADDRESSINFO");
  String varAddress=rb1.getString("VAR_ADDRESS");
  String varCity=rb1.getString("VAR_CITY");
  String varZip=rb1.getString("VAR_ZIP");
  String varState=rb1.getString("VAR_STATE");
  String varCardinfo=rb1.getString("VAR_CARDINFO");
  String consent1 = rb1.getString("VAR_CONSENT_STMT1");
  String consent2 = rb1.getString("VAR_CONSENT_STMT2");
  String consent3 = rb1.getString("VAR_CONSENT_STMT3");
  String varAND=rb1.getString("VAR_AND");

  String varAccountingnumber=rb1.getString("VAR_ACCOUNTINGNUMBER");
  String varRoutingnumber=rb1.getString("VAR_ROUTINGNUMBER");
  String varAccounttype=rb1.getString("VAR_ACCOUNTTYPE");
  String varFirstname=rb1.getString("VAR_FIRSTNAME");
  String varLastname=rb1.getString("VAR_LASTNAME");
  String varACH=rb1.getString("ACH");


%>

<div class="tab-pane" id="<%=varACH%>">
  <form id="achForm" class="form-style" method="post">

    <%-- card details --%>
    <div class="tab" id="cardinfoACH" ><p class="form-header" style="display: none;"><%=varCardinfo%></p>
      <div class="form-group has-float-label control-group-half">
        <input type="text" class="form-control input-control1" id="achfirstname" placeholder=" "
               oninput="this.className = 'form-control input-control1'"  name="firstname" />
        <label for="achfirstname" class="form-label"><%=varFirstname%></label>
      </div>
      <div class="form-group has-float-label control-group-half">
        <input type="text" class="form-control input-control1" id="achlastname" placeholder=" "
               oninput="this.className = 'form-control input-control1'"  name="lastname" />
        <label for="achlastname" class="form-label"><%=varLastname%></label>
      </div>
      <div class="form-group has-float-label control-group-half" >
        <input class="form-control input-control1" id="achAccNo" placeholder=" " type="text" onkeypress="return isNumberKey(event)"
               maxlength="10" oninput="this.className = 'form-control input-control1'" name="accountnumber" />
        <label for="achAccNo" class="form-label"><%=varAccountingnumber%></label>
      </div>
      <div class="form-group has-float-label control-group-half" >
        <input class="form-control input-control1" id="achRoutNo" placeholder=" " type="text" onkeypress="return isNumberKey(event)"
               maxlength="9" oninput="this.className = 'form-control input-control1'" name="routingnumber" />
        <label for="achRoutNo" class="form-label"><%=varRoutingnumber%></label>
      </div>
      <div class="form-group has-float-label control-group-full" >
        <select class="form-control input-control1" name="accountType" id="achAccType">
          <option value="PC">Personal Checking</option>
          <option value="PS">Personal Savings</option>
          <option value="CC">Commercial Checking</option>
        </select>
        <label for="achAccType" class="form-label"><%=varAccounttype%></label>
      </div>
    </div>

    <%-- address details --%>
      <div class="tab" id="addressinfoACH" > <p class="form-header" style="display: none;"><%=varAddressinfo%></p>
        <div class="form-group has-float-label control-group-full" id="DCardAddress" >
          <input type="text" class="form-control input-control1" id="daddress" placeholder=" " onfocusout="validateAddress(event.target.value ,'DCardAddress')"
                 oninput="this.className = 'form-control input-control1'"  name="street" value="<%=street%>" />
          <label for="daddress" class="form-label"><%=varAddress%></label>
        </div>
        <div class="form-group has-float-label control-group-half" id="DCardCity">
          <input type="text" class="form-control input-control1" id="dcity" placeholder=" " onfocusout="validateCity(event.target.value ,'DCardCity')"
                 oninput="this.className = 'form-control input-control1'"  name="city" value="<%=city%>" />
          <label for="dcity" class="form-label"><%=varCity%></label>
        </div>
        <div class="form-group has-float-label control-group-half" id="DCardZip" >
          <input type="text" class="form-control input-control1" id="dzip" placeholder=" " onfocusout="validateZip(event.target.value ,'DCardZip')"
                 oninput="this.className = 'form-control input-control1'" name="zip" value="<%=zip%>" />
          <label for="dzip" class="form-label"><%=varZip%></label>
        </div>
        <div class="form-group has-float-label control-group-half" id="DCardCountry" >
          <div class="dropdown">
            <input id="country_input_ACH" class="form-control input-control1" placeholder=" " onfocusout="validateCountry(event.target.value ,'DCardCountry')"
                   onblur="pincodecc('country_input_ACH','country_ACH','phonecc-id_ACH','phonecc2_ACH','country_input_ACH','country_ACH','statelabel_ACH')"
                   onkeypress="return isLetterKey(event)" oninput="this.className = 'form-control input-control1'"  >
            <label for="country_input_ACH" class="form-label"><%=varCountry%></label>
            <input type="hidden" id="country_ACH"  name="country_input">
          </div>
        </div>
        <div class="form-group has-float-label control-group-half" id="ACHState" >
          <input type="text" class="form-control input-control1" id="state_ach" placeholder=" " oninput="this.className = 'form-control input-control1'; stateinp();"
                 onfocusout="validateState(event.target.value ,'ACHState')" name="state" value="<%=state%>" />
          <label for="state_ach" id="statelabel_ACH" class="form-label"><%=varState%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phonecc-id_ACH" placeholder=" " onkeypress="return isNumberKey(event)"
                 onkeyup="onPasteNumCheck(event)"  maxlength="3" onblur ="setPhoneCC('phonecc-id_ACH','phonecc2_ACH')" value="<%=phonecc%>"
                 oninput="this.className = 'form-control input-control1'" />
          <label for="phonecc-id_ACH" class="form-label"><%=varPhonecc%></label>
          <input type="hidden" id="phonecc2_ACH"  name="phone-CC" value="<%=phonecc%>">
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phoneno" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'" name="telno" value="<%=phoneno%>" />
          <label for="phoneno" class="form-label"><%=varPhoneno%></label>
        </div>
        <div class="form-group has-float-label control-group-full" id="AchEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="achemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'AchEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="achemail" class="form-label"><%=varEmailid%></label>
        </div>

        <script>
          setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_ACH');
          pincodecc('country_input_ACH','country_ACH','phonecc-id_ACH','phonecc2_ACH','country_input_ACH','country_ACH','statelabel_ACH');
          validateCountry(document.getElementById('country_input_ACH').value ,'DCardCountry');
          StateLabel('country_input_ACH','country_ACH','statelabel_ACH');
        </script>

      </div>


    <%
      if(consentFlag.equalsIgnoreCase("Y"))
      {
    %>
    <div class="form-group terms-checkbox " id="terms">
      <input type="checkbox" class="" style="width: 5%" id="TC_ACH" onclick="disablePayButton(this)" >
      <label for="TC_ACH" style="font-size: 85%;position: absolute;margin: 0px;">
        <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
      </label>
    </div>
    <%
      }
    %>

    <div style="overflow:hidden;" >
      <div style="float:right;">
        <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'achForm')" >
          <i class="fas fa-angle-left"></i>
        </div>
      </div>
    </div>

    <div class="pay-btn" >
      <button type="button" class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1,'achForm')" tabindex="0">
        <%=varNext%>
      </button>
    </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="ACH">
    <jsp:include page="requestParameters.jsp">
      <jsp:param name="paymentMode" value="ACH" />
    </jsp:include>
  </form>
</div>