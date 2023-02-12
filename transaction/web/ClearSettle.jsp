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
  Date: 9/14/2018
  Time: 3:13 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%

  String ctoken = "";
  ctoken = (String) session.getAttribute("ctoken");
  Functions functions = new Functions();
  //String invoicenumber = getValue(standardKitValidatorVO.getInvoiceId());

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
  String street = "";
  String city = "";
  String state = "";
  String zip = "";
  String birthdate = "";

  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getEmail())){
    email =  standardKitValidatorVO.getAddressDetailsVO().getEmail();
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
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getBirthdate())){
    birthdate =  standardKitValidatorVO.getAddressDetailsVO().getBirthdate();
  }


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
  HashMap terminalMap = standardKitValidatorVO.getTerminalMap();

  String paymodeId = "16";

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
  String varNext=rb1.getString("VAR_NEXT");

  String varBirthdate=rb1.getString("VAR_BIRTHDATE");
  String varEmailid=rb1.getString("VAR_EMAILID");

  String varAddressinfo=rb1.getString("VAR_ADDRESSINFO");
  String varAddress=rb1.getString("VAR_ADDRESS");
  String varCity=rb1.getString("VAR_CITY");
  String varZip=rb1.getString("VAR_ZIP");
  String varState=rb1.getString("VAR_STATE");
  String varCountry=rb1.getString("VAR_COUNTRY");


  String consent1 = rb1.getString("VAR_CONSENT_STMT1");
  String consent2 = rb1.getString("VAR_CONSENT_STMT2");
  String consent3 = rb1.getString("VAR_CONSENT_STMT3");

%>

<%--<div id="CreditCardsOption" class="option-class">
  <ul class="nav nav-tabs" id="CardTab">--%>

<%
  List<String> cardList = (List<String>)paymentMap.get(paymodeId);

  standardKitValidatorVO.setPaymentMode(paymodeId);


  for(String cardId : cardList)
  {
    String addressValidation = "";
    String addressDisplay = "";
    String terminalid = "";
    String cardName = GatewayAccountService.getCardType(cardId);
    TerminalVO terminalVO = null;

    if(multiCurrency.equalsIgnoreCase("Y"))
    {
      terminalVO = (TerminalVO)terminalMap.get("CS-"+cardName+"-"+currency);
      if(terminalVO==null || terminalMap.size()==0)
        terminalVO = (TerminalVO)terminalMap.get("CS-"+cardName+"-ALL");
    }
    else
      terminalVO = (TerminalVO)terminalMap.get("CS-"+cardName+"-"+currency);

    addressValidation = terminalVO.getAddressValidation();
    addressDisplay = terminalVO.getAddressDetails();
    terminalid = terminalVO.getTerminalId();

%>
<input type="hidden" name="<%=cardName%>" id="<%=cardName%>" value="<%=terminalid%>,<%=addressValidation%>,<%=addressDisplay%>">
<%
  }

%>


<%--
</ul>
</div>
--%>

<%--<div class="tab-content">--%>
<div class="tab-pane" id="clearsettle">
  <form id="ClearSettleForm" name="ClearSettleForm" class="form-style" method="post" >
    <input type="hidden" name="ctoken" value="<%=ctoken%>">
    <input type="hidden" id="terminalid" name="terminalid" value="">
    <input type="hidden" id="attemptThreeD" name="attemptThreeD" value="<%=standardKitValidatorVO.getAttemptThreeD()%>">
    <input type="hidden" id="consentStmnt" name="consentStmnt" value="<%=consent1+consent2+consent3%>">

    <div class="tab" id="personalinfoCS"> <p class="form-header"><%=varPersonalinfo%></p>

      <div class="form-group has-float-label control-group-full" id="clearSettleBirthDate">
        <input type="text" class="form-control input-control1" id="csbirthdate" name="birthDate" placeholder="YYYYmmDD" onblur="clearSettleBirthDate('csbirthdate','clearSettleBirthDate')"
               oninput="this.className = 'form-control input-control1'" value="<%=birthdate%>" />
        <label for="csbirthdate" style="color:#757575"><%=varBirthdate%></label>
      </div>
      <div class="form-group has-float-label control-group-full" id="clearSettleEmail">
        <span class="input-icon"><i class="far fa-envelope"></i></span>
        <input type="email" class="form-control input-control1" id="csemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'clearSettleEmail')"
               oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
        <label for="csemail" style="color:#757575"><%=varEmailid%></label>
      </div>
      <div class="form-group has-float-label control-group-full">
        <input type="text" class="form-control input-control1" id="cscardholdername" placeholder=" "
               oninput="this.className = 'form-control input-control1'"  name="firstname" />
        <label for="cscardholdername" class="form-label">Card Holder Name</label>
      </div>
      <script>
        clearSettleBirthDate('csbirthdate','clearSettleBirthDate')
      </script>
    </div>

    <div class="tab" id="addressinfo"> <p class="form-header"> <%=varAddressinfo%> </p>
      <div class="form-group has-float-label control-group-full" id="clearSettleAddress">
        <input type="text" class="form-control input-control1" id="address" placeholder=" " onfocusout="validateAddress(event.target.value ,'clearSettleAddress')"
               oninput="this.className = 'form-control input-control1'" name="street" value="<%=street%>"/>
        <label for="address" class="form-label"><%=varAddress%></label>
      </div>
      <div class="form-group has-float-label control-group-half" id="clearSettleCity" >
        <input type="text" class="form-control input-control1" id="city" placeholder=" " onfocusout="validateCity(event.target.value ,'clearSettleCity')"
               oninput="this.className = 'form-control input-control1'" name="city" value="<%=city%>"/>
        <label for="city" class="form-label"><%=varCity%></label>
      </div>
      <div class="form-group has-float-label control-group-half" id="clearSettleZip" >
        <input type="text" class="form-control input-control1" id="zip" placeholder=" " onfocusout="validateZip(event.target.value ,'clearSettleZip')"
               oninput="this.className = 'form-control input-control1'" name="zip" value="<%=zip%>"/>
        <label for="zip" class="form-label"><%=varZip%></label>
      </div>
      <div class="form-group has-float-label control-group-half" id="clearSettleCountry">
        <div class="dropdown">
          <input id="country_input_cs" class="form-control input-control1"  placeholder=" " onblur="StateLabel('country_input_cs','country_cs','statelabel_cs');"
                 oninput="this.className = 'form-control input-control1'" onfocusout="validateCountry(event.target.value ,'clearSettleCountry')"
                 onkeypress="return isLetterKey(event)"  >
          <label for="country_input_cs" class="form-label"><%=varCountry%></label>
          <input type="hidden" id="country_cs"  name="country_input">
        </div>
      </div>
      <div class="form-group has-float-label control-group-half" id="clearSettleState" >
        <input type="text" class="form-control input-control1" id="state" oninput="stateinp()" placeholder=" " onblur="validateState(event.target.value ,'clearSettleState')"
               oninput="this.className = 'form-control input-control1'" name="state" value="<%=state%>"/>
        <label for="state" id="statelabel_cs" class="form-label"><%=varState%></label>
        <script>
          setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_cs');
          validateCountry(document.getElementById('country_input_cs').value ,'clearSettleCountry');
          StateLabel('country_input_cs','country_cs','statelabel_cs');
        </script>
      </div>
    </div>

    <%
      if(consentFlag.equalsIgnoreCase("Y"))
      {
    %>
    <div class="form-group terms-checkbox hide" id="terms">
      <input type="checkbox" class="" style="width: 5%" id="TC" onclick="disablePayButton(this)" >
      <label for="TC" style="font-size: 85%;position: absolute;margin: 0px;">
        <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> and <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
      </label>
    </div>
    <%
      }
    %>
    <div style="overflow:hidden" >
      <div style="float:right;">
        <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'ClearSettleForm')">
          <i class="fas fa-angle-left"></i>
        </div>
      </div>
    </div>

    <div class="pay-btn" >
      <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1,'ClearSettleForm')">
        <%=varNext%>
      </div>
    </div>

    <input type="hidden" name="paymentBrand" id="paymentBrand" value="CLEARSETTLE">

    <jsp:include page="requestParameters.jsp">
      <jsp:param name="paymentMode" value="CS" />
    </jsp:include>

  </form>
</div>
<%--</div>--%>