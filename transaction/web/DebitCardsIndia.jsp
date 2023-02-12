<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.directi.pg.Functions" %>

<%--
Created by IntelliJ IDEA.
User: Rihen
Date: 9/4/2018
Time: 3:07 PM
To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  String ctoken = "";
  ctoken = (String) session.getAttribute("ctoken");
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  HashMap paymentMap = standardKitValidatorVO.getMapOfPaymentCardType();

  Functions functions = new Functions();
  String paymodeId = "104";

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
  String varAND = rb5.getString("VAR_AND");

  HashMap terminalMap = standardKitValidatorVO.getTerminalMap();
  String currency = standardKitValidatorVO.getTransDetailsVO().getCurrency();
  String multiCurrency = standardKitValidatorVO.getMerchantDetailsVO().getMultiCurrencySupport();
  String merchantLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo();
  String partnerLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();

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
%>

<%--<div id="DebitCardsOption" class="option-class">
<ul class="nav nav-tabs" id="DCardTab">--%>
<%--<%

List<String> cardList = (List<String>)paymentMap.get(paymodeId);
for(String cardId : cardList)
{
  String addressValidation = "";
  String addressDisplay = "";
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
  terminalid = terminalVO.getTerminalId();
%>
<input type="hidden" name="<%=cardName%>" id="<%=cardName%>" value="<%=terminalid%>,<%=addressValidation%>,<%=addressDisplay%>">
<%
}
%>--%>

<%--  </ul>
</div>--%>


<%--<div class="tab-content">--%>
<div class="tab-pane" id="DebitCardsIndia">
  <form id="dcardFormIndia" class="form-style" method="post">
    <input type="hidden" name="ctoken" value="<%=ctoken%>">
    <input type="hidden" id="dterminalid" name="terminalid" value="">
    <input type="hidden" id="attemptThreeD" name="attemptThreeD" value="<%=standardKitValidatorVO.getAttemptThreeD()%>">

    <div class="tab" id="personalInfoDCIndia" style="padding-top: 22px;"> <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
      <div id="emailPersonal">
      <div class="form-group has-float-label control-group-full" id="InDCardEmail">
        <span class="input-icon"><i class="far fa-envelope"></i></span>
        <input type="email" class="form-control input-control1" id="demailIn" name="emailaddr" placeholder=" " value="<%=email%>"
               oninput="this.className = 'form-control input-control1'" onfocusout="validateEmail(event.target.value ,'InDCardEmail')" />
        <label for="demailIn" class="form-label"><%=varEmailid%></label>
      </div>
      </div>
      <div id="mobilePersonal">
      <div class="form-group has-float-label control-group-half">
        <input type="text" class="form-control input-control1" id="phonecc-idDCIn" placeholder=" " maxlength="3" value="<%=phonecc%>"
               oninput="this.className = 'form-control input-control1'" onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
               onblur ="setPhoneCC('phonecc-idDCIn','phonecc_DC_IN')" />
        <label for="phonecc-idDCIn" class="form-label"><%=varPhonecc%></label>
        <input type="hidden" id="phonecc_DC_IN"  name="phone-CC" value="<%=phonecc%>">
      </div>
      <div class="form-group has-float-label control-group-half">
        <input type="text" class="form-control input-control1" id="phoneno_DC_IN" name="phoneno" placeholder=" " value="<%=phoneno%>"
               oninput="this.className = 'form-control input-control1'" onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" />
        <label for="phoneno_DC_IN" class="form-label"><%=varPhoneno%></label>
      </div>
      </div>
    </div>

    <div class="tab" id="dCardInfoIndia" style="padding-top: 22px;"> <p class="form-header" style="display: none;"><%=varCardinfo%></p>
      <div class="form-group has-float-label control-group-seventy card" id="InDCardNumber" >
        <input type="text" class="form-control input-control1" id="dcardNoIn" name="cardnumber" placeholder=" " maxlength="19" autocomplete="off"
               oninput="this.className = 'form-control input-control1'" onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
               onfocusout = "isValidCardCheck('dcardNoIn', 'InDCardNumber')" <%--onfocusout="funcCheck('dcardNoIn','InDCardNumber')"--%>  />
        <label for="dcardNoIn" class="form-label"><%=varCardnumber%></label>
        <span class="card_icon"></span>
      </div>
      <div class="form-group has-float-label control-group-twenty-five" id="InDCardExpiry" >
        <input type="text" class="form-control input-control1" id="dcardExpIn" name="expiry" placeholder="MM/YY"
               oninput="this.className = 'form-control input-control1'" onkeypress="return isNumberKey(event)" onkeyup="addSlash(event,'dcardExpIn')"
               onfocusout="expiryCheck('dcardExpIn','InDCardExpiry')" />
        <label for="dcardExpIn" class="form-label"><%=varExpiry%></label>
      </div>
      <div class="form-group has-float-label control-group-seventy">
        <input type="text" class="form-control input-control1" id="dfirstnameIn" name="firstname" placeholder=" " oninput="this.className = 'form-control input-control1'" />
        <label for="dfirstnameIn" class="form-label"><%=varCardHolderName%></label>
      </div>
      <div class="form-group has-float-label control-group-twenty-five" id="InDCardCVV">
        <input type="password" class="form-control input-control1" id="dCVVIn" name="cvv" placeholder=" " maxlength="4" autocomplete="off" onkeypress="return isNumberKey(event)"
               oninput="this.className = 'form-control input-control1'" onkeyup="onPasteNumCheck(event)" onblur="validateCVV('dCVVIn','InDCardCVV')" />
        <label for="dCVVIn" class="form-label"><%=varCvv%></label>
      </div>

      <div id="dcIndiaHideAddressInfo" style="margin: 80px 0px 0px 0px">
        <div class="hide" id="daddressinfoIn"> <p class="form-header"><%=varAddressinfo%></p>
          <div class="form-group has-float-label control-group-full" id="InDCardAddress" >
            <input type="text" class="form-control input-control1" id="daddressIn" placeholder=" " name="street" value="<%=street%>"
                   oninput="this.className = 'form-control input-control1'" onfocusout="validateAddress(event.target.value ,'InDCardAddress')" />
            <label for="daddressIn" class="form-label"><%=varAddress%></label>
          </div>
          <div class="form-group has-float-label control-group-half" id="InDCardCity">
            <input type="text" class="form-control input-control1" id="dcityIn" name="city" placeholder=" " value="<%=city%>"
                   oninput="this.className = 'form-control input-control1'" onfocusout="validateCity(event.target.value ,'InDCardCity')" />
            <label for="dcityIn" class="form-label"><%=varCity%></label>
          </div>
          <div class="form-group has-float-label control-group-half" id="InDCardZip" >
            <input type="text" class="form-control input-control1" id="dzipIn" name="zip" placeholder=" " value="<%=zip%>"
                   oninput="this.className = 'form-control input-control1'" onfocusout="validateZip(event.target.value ,'InDCardZip')" />
            <label for="dzipIn" class="form-label"><%=varZip%></label>
          </div>
          <div class="form-group has-float-label control-group-half" id="InDCardCountry" >
            <div class="dropdown">
              <input type="text" class="form-control input-control1" id="country_input_DC_IN" placeholder=" "
                     oninput="this.className = 'form-control input-control1'" onblur="StateLabel('country_input_DC_IN','country_DC_IN','dstatelabel_DC_IN')" onkeypress="return isLetterKey(event)"
                     onfocusout="validateCountry(event.target.value ,'InDCardCountry')" />
              <label for="country_input_DC_IN" class="form-label"><%=varCountry%></label>
              <input type="hidden" id="country_DC_IN"  name="country_input">
            </div>
          </div>
          <div class="form-group has-float-label control-group-half" id="InDCardState" >
            <input type="text" class="form-control input-control1" id="dstate" name="state" placeholder=" " value="<%=state%>"
                   oninput="this.className = 'form-control input-control1'" onfocusout="validateState(event.target.value ,'InDCardState')" />
            <label for="dstate" id="dstatelabel_DC_IN" class="form-label"><%=varState%></label>
            <script>
              //pincodecc('country_input_DC_IN','country_DC_IN','phonecc-id2','phonecc2','country_input_DC_IN','country_DC_IN','dstatelabel_DC_IN');
              setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_DC_IN');
              validateCountry(document.getElementById('country_input_DC_IN').value ,'InDCardCountry');
              StateLabel('country_input_DC_IN','country_DC_IN','dstatelabel_DC_IN');
            </script>
          </div>
        </div>
      </div>

    </div>
    <div class="form-group terms-checkbox hide" id="terms">
      <input type="checkbox" class="" style="width: 5%" id="TC_DC_IN" onclick="disablePayButton(this)">
      <label for="TC_DC_IN" style="font-size: 85%;position: absolute;margin: 0px;">
        <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
      </label>
    </div>

    <div style="overflow:hidden;" >
      <div style="float:right;">
        <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'dcardFormIndia')" >
          <i class="fas fa-angle-left"></i>
        </div>
      </div>
    </div>

    <div class="pay-btn" >
      <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'dcardFormIndia')">
        <%=varNext%>
      </div>
      <input type="hidden" name="paymentBrand" id="dpaymentBrand" value="">

      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="DCI" />
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