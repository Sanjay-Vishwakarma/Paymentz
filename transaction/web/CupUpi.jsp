<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="org.owasp.esapi.ESAPI" %>

<%--
  Created by IntelliJ IDEA.
  User: Rihen
  Date: 5/24/2018
  Time: 5:29 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>

<%

  String ctoken = "";
  ctoken = (String) session.getAttribute("ctoken");
  Functions functions = new Functions();

  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  HashMap paymentMap = standardKitValidatorVO.getMapOfPaymentCardType();

  String payMode = "CU";

  String merchantLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo();
  String partnerLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
  String consentFlag = standardKitValidatorVO.getMerchantDetailsVO().getConsentFlag();
  String limitRouting = standardKitValidatorVO.getMerchantDetailsVO().getLimitRouting();

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
    email = ESAPI.encoder().encodeForHTML(standardKitValidatorVO.getAddressDetailsVO().getEmail());
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

  String paymodeId = "1";

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

  String varPersonalinfo=rb1.getString("VAR_PERSONALINFO");
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
  String varCardnumber=rb1.getString("VAR_CARDNUMBER");
  String varExpiry=rb1.getString("VAR_EXPIRY");
  String varCvv=rb1.getString("VAR_CVV");
  String varCardHolderName=rb1.getString("VAR_CARDHOLDERNAME");
  String consent1 = rb1.getString("VAR_CONSENT_STMT1");
  String consent2 = rb1.getString("VAR_CONSENT_STMT2");
  String consent3 = rb1.getString("VAR_CONSENT_STMT3");
  String varAND=rb1.getString("VAR_AND");
  String varINSTALLMENT=rb1.getString("VAR_INSTALLMENT");

//  String id = request.getParameter("id");
//  if(functions.isValueNull(id)){
    paymodeId = "25";
//  }
  standardKitValidatorVO.setPaymentMode(paymodeId);

%>

<div id="CupUpiOption" class="option-class">
  <ul class="nav nav-tabs" id="CupUpiTab">
    <%
      List<String> cardList = (List<String>)paymentMap.get(paymodeId);
      for(String cardId : cardList)
      {
        String cardName = GatewayAccountService.getCardType(cardId);
        String cardImg = cardName+".png";
    %>
    <li class="tabs-li-wallet" onclick="CupUpiHideShow('<%=cardName%>','<%=rb1.getString(cardName)%>')">
      <a href="#<%=cardName%>"  data-toggle="tab" title="<%=cardName%>" aria-expanded="false">
        <img class="images-style" src="/images/merchant/images/cupupi/<%=cardImg%>" alt="<%=cardName%>">
        <div class="label-style"><%=rb1.getString(cardName)%></div>
      </a>
    </li>
    <%
      }
    %>
  </ul>
</div>

<div class="tab-content">
  <div class="tab-pane" id="CupUPI">
    <form id="CupUPIForm" class="form-style" method="post" >
      <input type="hidden" name="ctoken" id="CuToken" value="<%=ctoken%>">
      <input type="hidden" id="terminalid" name="terminalid" value="">
      <input type="hidden" id="attemptThreeD" name="attemptThreeD" value="<%=standardKitValidatorVO.getAttemptThreeD()%>">
      <input type="hidden" id="consentStmnt" name="consentStmnt" value="<%=consent1+consent2+consent3%>">
      <input type="hidden" name="limitRouting" id="limitRouting"  value="<%=limitRouting%>">
      <input type="hidden" name="merchantOrderDetails" id="merchantOrderDetails" value="<%=standardKitValidatorVO.getMerchantDetailsVO().getMerchantOrderDetailsDisplay()%>">

      <div class="tab" id="personalinfo" style="padding-top: 22px;"> <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
        <div id="emailPersonal">
        <div class="form-group has-float-label control-group-full" id="CUEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="email" placeholder=" " onfocusout="validateEmail(event.target.value ,'CUEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" autofocus />
          <label for="email" class="form-label"><%=varEmailid%></label>

        </div>
        </div>
        <div id="mobilePersonal">
        <div class="form-group has-float-label control-group-full" >
          <div class="dropdown">
            <input id="country_input_cu" class="form-control input-control1"  placeholder=" "
                   onblur="pincodecc('country_input_cu','country_cu','phonecc_id_cu','phonecc_cu','country_input_cu_2','country_cu_2','statelabel');"
                   oninput="this.className = 'form-control input-control1'" onkeypress="return isLetterKey(event)"
                   autocomplete="off" >
            <label for="country_input_cu" class="form-label"><%=varCountry%></label>
            <input type="hidden" id="country_cu"  name="country_input_cu" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
            <script>
              setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_cu');
            </script>
          </div>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" name="phone-CC" id="phonecc_id_cu" placeholder=" " onkeypress="return isNumberKey(event)"
                 onkeyup="onPasteNumCheck(event)"  maxlength="3" onblur ="setPhoneCC('phonecc_id_cu','phonecc_cu')" value="<%=phonecc%>"
                 oninput="this.className = 'form-control input-control1'" />
          <label for="phonecc_id_cu" class="form-label"><%=varPhonecc%></label>
          <input type="hidden" id="phonecc_cu"  name="phone-CC" value="<%=phonecc%>" />
        </div>
        <div class="form-group has-float-label control-group-half">
          <span class="input-icon-half"><i class="fas fa-phone"></i> </span>
          <input type="text" class="form-control input-control1" id="phoneno_cu" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'" name="telno" value="<%=phoneno%>" />
          <label for="phoneno_cu" class="form-label"><%=varPhoneno%></label>
        </div>
      </div>
      </div>

      <div class="tab" id="cardinfo_cu" style="padding-top: 22px;"> <p class="form-header" style="display: none;"><%=varCardinfo%></p>

        <div class="form-group has-float-label control-group-seventy card" id="CUCardNumber">
          <input class="form-control input-control1" id="cardNo_cu" placeholder=" " type="text" onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
                 onfocusout = "isValidCardCheck('cardNo_cu', 'CUCardNumber','onfocusout')" autocomplete="off"
                 maxlength="19" oninput="this.className = 'form-control input-control1'"  name="cardnumber" />
          <label for="cardNo_cu" class="form-label"><%=varCardnumber%></label>
          <span class="card_icon"></span>
        </div>
        <div class="form-group has-float-label control-group-twenty-five" id="CUCardExpiry"  >
          <input type="text" class="form-control input-control1" id="Expiry_cu" placeholder="MM/YY" onkeypress="return isNumberKey(event)" onblur="expiryCheck('Expiry_cu','CUCardExpiry')"  autocomplete="off"
                 onkeyup="addSlash(event,'Expiry_cu')" oninput="this.className = 'form-control input-control1'" name="expiry" maxlength="5"/>
          <label for="Expiry_cu" class="form-label"><%=varExpiry%></label>
        </div>
        <div class="form-group has-float-label control-group-seventy" id="CUCardName">
          <input type="text" class="form-control input-control1" id="fname_cu" placeholder=" " onblur="validateCardHolderName('fname_cu','CUCardName')"
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="fname_cu" class="form-label"><%=varCardHolderName%></label>
        </div>
        <div class="form-group has-float-label control-group-twenty-five" id="CUCardCVV">
          <input type="password" class="form-control input-control1" id="CU_CVV" placeholder=" " onkeypress="return isNumberKey(event)"
                 onkeyup="onPasteNumCheck(event)" maxlength="4"  autocomplete="off" onblur="validateCVV('CU_CVV','CUCardCVV')"
                 oninput="this.className = 'form-control input-control1'" name="cvv"  />
          <label for="CU_CVV" class="form-label"><%=varCvv%></label>
        </div>

        <div id="hideaddressinfo_cu" style="margin: 80px 0px 0px 0px">
          <div class="hide" id="addressinfo_cu"> <p class="form-header"> <%=varAddressinfo%> </p>
            <div class="form-group has-float-label control-group-full" id="CUCardAddress">
              <input type="text" class="form-control input-control1" id="address_cu" placeholder=" " onfocusout="validateAddress(event.target.value ,'CUCardAddress')"
                     oninput="this.className = 'form-control input-control1'" name="street" value="<%=street%>"/>
              <label for="address_cu" class="form-label"><%=varAddress%></label>
            </div>
            <div class="form-group has-float-label control-group-half" id="CUCardCity" >
              <input type="text" class="form-control input-control1" id="city_cu" placeholder=" " onfocusout="validateCity(event.target.value ,'CUCardCity')"
                     oninput="this.className = 'form-control input-control1'" name="city" value="<%=city%>"/>
              <label for="city_cu" class="form-label"><%=varCity%></label>
            </div>
            <div class="form-group has-float-label control-group-half" id="CUCardZip" >
              <input type="text" class="form-control input-control1" id="zip_cu" placeholder=" " onfocusout="validateZip(event.target.value ,'CUCardZip')"
                     oninput="this.className = 'form-control input-control1'" name="zip" value="<%=zip%>"/>
              <label for="zip_cu" class="form-label"><%=varZip%></label>
            </div>
            <div class="form-group has-float-label control-group-half" id="CUCardCountry">
              <div class="dropdown">
                <input id="country_input_cu_2" class="form-control input-control1"  placeholder=" " onfocusout="validateCountry(event.target.value ,'CUCardCountry')"
                       onblur="pincodecc('country_input_cu_2','country_cu_2','phonecc_id_cu','phonecc_cu','country_input_cu_2','country_cu_2','statelabel');"
                       onkeypress="return isLetterKey(event)" oninput="this.className = 'form-control input-control1'" >
                <label for="country_input_cu_2" class="form-label"><%=varCountry%></label>
                <input type="hidden" id="country_cu_2"  name="country_input" >
              </div>
            </div>
            <div class="form-group has-float-label control-group-half" id="CUCardState" >
              <div class="dropdown">
                <input type="text" class="form-control input-control1" id="state_cu" oninput="this.className = 'form-control input-control1';stateinp();" placeholder=" "
                       onblur="validateState(event.target.value ,'CUCardState')" name="state" value="<%=state%>"/>
                <label for="state_cu" id="statelabel" class="form-label"><%=varState%></label>
                <script>
                  pincodecc('country_input_cu','country_cu','phonecc_id_cu','phonecc_cu','country_input_cu_2','country_cu_2','statelabel');
                  setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_cu_2');
                  validateCountry(document.getElementById('country_input_cu_2').value ,'CUCardCountry');
                  StateLabel('country_input_cu_2','country_cu_2','statelabel');
                </script>
              </div>
            </div>

          </div>
        </div>

      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_CU" onclick="disablePayButton(this)" >
        <label for="TC_CU" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div style="overflow:hidden" >
        <div style="float:right;"> <!-- Previous button-->
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'CupUPIForm')">
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>

      <div class="pay-btn">
        <button type="button" class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1,'CupUPIForm')"  tabindex="0">
          <%=varNext%>
        </button>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="CupUPI">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="CU" />
      </jsp:include>

    </form>
  </div>


  <%--Secure Pay --%>
  <form method="post" name="securePayForm" id="securePayForm" action="/transaction/SingleCallCheckout">

    <input type="hidden" name="paymentBrand" id="paymentBrand" value="SecurePay">
    <jsp:include page="requestParameters.jsp">
      <jsp:param name="paymentMode" value="<%=payMode%>" />
    </jsp:include>
  </form>

</div>



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