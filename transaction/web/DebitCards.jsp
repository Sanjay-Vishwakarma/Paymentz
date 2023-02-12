<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.directi.pg.Functions" %>

<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 5/24/2018
  Time: 5:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  String ctoken = "";
  ctoken = (String) session.getAttribute("ctoken");
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  HashMap paymentMap = standardKitValidatorVO.getMapOfPaymentCardType();

  Functions functions = new Functions();
  String paymodeId = "4";

  ResourceBundle rb5 = null;
  String lang = "";
  String multiLanguage = "";

  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getLanguage()))
  {
    lang = standardKitValidatorVO.getAddressDetailsVO().getLanguage().toLowerCase();
    if ("ja".equalsIgnoreCase(lang))
    {
      rb5 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ja");
    }
    else
    {
      rb5 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
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
        rb5 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ja");
      }
      else
      {
        rb5 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
      }
    }
    else
    {
      rb5 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
    }
  }
  else
  {
    rb5 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
  }

  String varPersonalinfo=rb5.getString("VAR_PERSONALINFO");
  String varEmailid=rb5.getString("VAR_EMAILID");
  String varCountry=rb5.getString("VAR_COUNTRY");
  String varPhonecc=rb5.getString("VAR_PHONECC");
  String varPhoneno=rb5.getString("VAR_PHONENO");
  String varNext=rb5.getString("VAR_NEXT");
  String varAddressinfo=rb5.getString("VAR_ADDRESSINFO");
  String varAddress=rb5.getString("VAR_ADDRESS");
  String varCity=rb5.getString("VAR_CITY");
  String varZip=rb5.getString("VAR_ZIP");
  String varState=rb5.getString("VAR_STATE");
  String varCardinfo=rb5.getString("VAR_CARDINFO");
  String varCardnumber=rb5.getString("VAR_CARDNUMBER");
  String varExpiry=rb5.getString("VAR_EXPIRY");
  String varCvv=rb5.getString("VAR_CVV");
  String varCardHolderName=rb5.getString("VAR_CARDHOLDERNAME");
  String consent1 = rb5.getString("VAR_CONSENT_STMT1");
  String consent2 = rb5.getString("VAR_CONSENT_STMT2");
  String consent3 = rb5.getString("VAR_CONSENT_STMT3");
  String varAND=rb5.getString("VAR_AND");
  String varDebitCards=rb5.getString("DebitCards");

  HashMap terminalMap = standardKitValidatorVO.getTerminalMap();
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

  //TerminalVO terminalVO = new TerminalVO();
%>

<%--<div id="DebitCardsOption" class="option-class">
  <ul class="nav nav-tabs" id="DCardTab">--%>
<%

  List<String> cardList = (List<String>)paymentMap.get(paymodeId);
  for(String cardId : cardList)
  {
    String addressValidation = "";
    String addressDisplay = "";
    String addressValue = "";
    String terminalid = "";
    String cardName = GatewayAccountService.getCardType(cardId);
    String cardImg = cardName+".png";

    TerminalVO terminalVO = null;
    if(multiCurrency.equalsIgnoreCase("Y"))
    {
      terminalVO = (TerminalVO)terminalMap.get("DC-"+cardName+"-"+currency);
      if(terminalVO==null)
      {
        terminalVO = (TerminalVO)terminalMap.get("DC-"+cardName+"-ALL");
      }
    }
    else
      terminalVO = (TerminalVO)terminalMap.get("DC-"+cardName+"-"+currency);

    addressValidation = terminalVO.getAddressValidation();
    addressDisplay = terminalVO.getAddressDetails();
    addressValue = terminalVO.getAddressValidation();
    terminalid = terminalVO.getTerminalId();
%>
<%--<li class="tabs-li-wallet" onclick="debitCardHideShow('<%=cardName%>','<%=addressValue%>','<%=terminalid%>')">
  <a href="#dcard"  data-toggle="tab" title="profile" aria-expanded="false">
    <img class="images-style" src="/images/merchant/images/card/<%=cardImg%>" alt="<%=cardName%>">
    <div class="label-style"><%=cardName%></div>
  </a>
</li>--%>

<input type="hidden" name="<%=cardName%>" id="<%=cardName%>" value="<%=terminalid%>,<%=addressValidation%>,<%=addressDisplay%>">
<%
  }
%>
<%--<li class="tabs-li-wallet" onclick="debitCardHideShow('Visa')">
  <a href="#dcard"  data-toggle="tab" title="profile" aria-expanded="false">
    <img class="images-style" src="/images/merchant/images/card/VISA.png">
    <div class="label-style"><%=varVisacard%></div>
  </a>
</li>
<li class="tabs-li-wallet" onclick="debitCardHideShow('Master Card')">
  <a href="#dcard" data-toggle="tab" title="picture" aria-expanded="false">
    <img class="images-style" src="/images/merchant/images/card/MC.png">
    <div class="label-style"><%=varMastercard%></div>
  </a>
</li>
<li class="tabs-li-wallet" onclick="debitCardHideShow('American Express')">
  <a href="#dcard"  data-toggle="tab" title="profile" aria-expanded="false">
    <img class="images-style" src="/images/merchant/images/card/AMEX.png">
    <div class="label-style"><%=varAmericanexpress%></div>
  </a>
</li>--%>

<%--  </ul>
</div>--%>


<%--<div class="tab-content">--%>
<div class="tab-pane" id="<%=varDebitCards%>">
  <form id="dcardForm" class="form-style" method="post">
    <input type="hidden" name="ctoken" value="<%=ctoken%>">
    <input type="hidden" id="dterminalid" name="terminalid" value="">
    <input type="hidden" id="attemptThreeD" name="attemptThreeD" value="<%=standardKitValidatorVO.getAttemptThreeD()%>">

    <div class="tab" id="personalinfodebit" style="padding-top: 22px;"> <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
      <div id="emailPersonal">
      <div class="form-group has-float-label control-group-full" id="DCardEmail">
        <span class="input-icon"><i class="far fa-envelope"></i></span>
        <input type="email" class="form-control input-control1" id="demail" placeholder=" " onfocusout="validateEmail(event.target.value ,'DCardEmail')"
               oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
        <label for="demail" class="form-label"><%=varEmailid%></label>
      </div>
      </div>
      <div id="mobilePersonal">
      <div class="form-group has-float-label control-group-full" >
        <div class="dropdown">
          <input id="country_input_DC_optional" class="form-control input-control1" placeholder=" "  onblur="pincodecc('country_input_DC_optional','country2_optional','phonecc-id2','phonecc2','country_input2','country2','dstatelabel');"
                 oninput="this.className = 'form-control input-control1'" onkeypress="return isLetterKey(event)" <%--onblur="optionalCountry('country_input_DC_optional','country_input2','country2','dstatelabel')"--%> >
          <label for="country_input_DC_optional" class="form-label"><%=varCountry%></label>
          <input type="hidden" id="country2_optional"  name="country_input_DC_optional">
          <script>
            setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_DC_optional');
          </script>
        </div>
      </div>
      <div class="form-group has-float-label control-group-half">
        <input type="text" class="form-control input-control1" id="phonecc-id2" placeholder=" " onkeypress="return isNumberKey(event)"
               onkeyup="onPasteNumCheck(event)"  maxlength="3" onblur ="setPhoneCC('phonecc-id2','phonecc2')" value="<%=phonecc%>"
               oninput="this.className = 'form-control input-control1'" />
        <label for="phonecc-id2" class="form-label"><%=varPhonecc%></label>
        <input type="hidden" id="phonecc2"  name="phone-CC" value="<%=phonecc%>">
      </div>
      <div class="form-group has-float-label control-group-half">
        <input type="text" class="form-control input-control1" id="phoneno" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
               oninput="this.className = 'form-control input-control1'" name="phoneno" value="<%=phoneno%>" />
        <label for="phoneno" class="form-label"><%=varPhoneno%></label>
      </div>
      </div>
    </div>

    <div class="tab" id="dcardinfo" style="padding-top: 22px;"> <p class="form-header" style="display: none;"><%=varCardinfo%></p>

      <div class="form-group has-float-label control-group-seventy card" id="DCardNumber" >
        <input class="form-control input-control1" id="dcardNo" placeholder=" " type="text" onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
               onfocusout = "isValidCardCheck('dcardNo', 'DCardNumber')" <%--onfocusout="funcCheck('dcardNo','DCardNumber')"--%> autocomplete="off"
               maxlength="19" oninput="this.className = 'form-control input-control1'" name="cardnumber" />
        <label for="dcardNo" class="form-label"><%=varCardnumber%></label>
        <span class="card_icon"></span>
      </div>
      <div class="form-group has-float-label control-group-twenty-five" id="DCardExpiry" >
        <input type="text" class="form-control input-control1" name="expiry" id="dcardExpiry" placeholder="MM/YY" onkeypress="return isNumberKey(event)" onfocusout="expiryCheck('dcardExpiry','DCardExpiry')"
               onkeyup="addSlash(event,'dcardExpiry')" oninput="this.className = 'form-control input-control1'" />
        <label for="dcardExpiry" class="form-label"><%=varExpiry%></label>
      </div>
      <div class="form-group has-float-label control-group-seventy">
        <input type="text" class="form-control input-control1" id="dfirstname" placeholder=" "
               oninput="this.className = 'form-control input-control1'"  name="firstname" />
        <label for="dfirstname" class="form-label"><%=varCardHolderName%></label>
      </div>
      <div class="form-group has-float-label control-group-twenty-five" id="DCardCVV">
        <input type="password" class="form-control input-control1" id="dCVV" placeholder=" " onkeypress="return isNumberKey(event)"
               onkeyup="onPasteNumCheck(event)" maxlength="4" autocomplete="off" onblur="validateCVV('dCVV','DCardCVV')"
               oninput="this.className = 'form-control input-control1'" name="cvv"  />
        <label for="dCVV" class="form-label"><%=varCvv%></label>
      </div>

      <div id="dhideaddressinfo" style="margin: 80px 0px 0px 0px">
        <div class="hide" id="daddressinfo"> <p class="form-header"><%=varAddressinfo%></p>
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
              <input id="country_input2" class="form-control input-control1" placeholder=" " onfocusout="validateCountry(event.target.value ,'DCardCountry')"
                     onblur="pincodecc('country_input2','country2','phonecc-id2','phonecc2','country_input2','country2','dstatelabel')"
                     onkeypress="return isLetterKey(event)" oninput="this.className = 'form-control input-control1'" >
              <label for="country_input2" class="form-label"><%=varCountry%></label>
              <input type="hidden" id="country2"  name="country_input">
            </div>
          </div>
          <div class="form-group has-float-label control-group-half" id="DCardState" >
            <input type="text" class="form-control input-control1" id="dstate" placeholder=" " onfocusout="validateState(event.target.value ,'DCardState')"
                   oninput="this.className = 'form-control input-control1'" name="state" value="<%=state%>" />
            <label for="dstate" id="dstatelabel" class="form-label"><%=varState%></label>
            <script>
              pincodecc('country_input_DC_optional','country2_optional','phonecc-id2','phonecc2','country_input2','country2','dstatelabel');
              setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input2');
              validateCountry(document.getElementById('country_input2').value ,'DCardCountry');
              StateLabel('country_input2','country2','dstatelabel');
            </script>
          </div>
        </div>
      </div>

    </div>

    <%
      if(consentFlag.equalsIgnoreCase("Y"))
      {
    %>
    <div class="form-group terms-checkbox hide" id="terms">
      <input type="checkbox" class="" style="width: 5%" id="dTC" onclick="disablePayButton(this)">
      <label for="dTC" style="font-size: 85%;position: absolute;margin: 0px;">
        <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
      </label>
    </div>
    <%
      }
    %>
    <div style="overflow:hidden;" >
      <div style="float:right;">
        <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'dcardForm')" >
          <i class="fas fa-angle-left"></i>
        </div>
      </div>
    </div>

    <div class="pay-btn" >
      <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'dcardForm')">
        <%=varNext%>
      </div>
      <input type="hidden" name="paymentBrand" id="dpaymentBrand" value="">

      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="DC" />
      </jsp:include>

    </div>
  </form>
</div>

<%--</div>--%>




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