<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.Functions" %>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 5/24/2018
  Time: 5:46 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  HashMap paymentMap = standardKitValidatorVO.getMapOfPaymentCardType();

  String paymodeId = "9";
  String payMode = "SEPA";

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

  ResourceBundle rb7 = null;
  String lang = "";
  String multiLanguage = "";

  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getLanguage()))
  {
    lang = standardKitValidatorVO.getAddressDetailsVO().getLanguage().toLowerCase();
    if ("ja".equalsIgnoreCase(lang))
    {
      rb7 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ja");
    }
    else
    {
      rb7 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
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
        rb7 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ja");
      }
      else
      {
        rb7 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
      }
    }
    else
    {
      rb7 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
    }
  }
  else
  {
    rb7 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
  }

  String varIban=rb7.getString("VAR_IBAN");
  String varBic=rb7.getString("VAR_BIC");
  String varGivenname=rb7.getString("VAR_GIVENNAME");
  String varFamilyname=rb7.getString("VAR_FAMILYNAME");
  String varMandateid=rb7.getString("VAR_MANDATEID");
  String varPersonalinfo=rb7.getString("VAR_PERSONALINFO");
  String varEmailid=rb7.getString("VAR_EMAILID");
  String varCountry=rb7.getString("VAR_COUNTRY");
  String varPhoneno=rb7.getString("VAR_PHONENO");
  String varNext=rb7.getString("VAR_NEXT");
  String varAddressinfo=rb7.getString("VAR_ADDRESSINFO");
  String varAddress=rb7.getString("VAR_ADDRESS");
  String varCity=rb7.getString("VAR_CITY");
  String varZip=rb7.getString("VAR_ZIP");
  String varState=rb7.getString("VAR_STATE");
  String varCardinfo=rb7.getString("VAR_CARDINFO");
  String varFirstname=rb7.getString("VAR_FIRSTNAME");
  String varLastname=rb7.getString("VAR_LASTNAME");
  String consent1 = rb7.getString("VAR_CONSENT_STMT1");
  String consent2 = rb7.getString("VAR_CONSENT_STMT2");
  String consent3 = rb7.getString("VAR_CONSENT_STMT3");
  String varAND = rb7.getString("VAR_AND");
%>

<div id="SEPAOption" class="option-class">
  <ul class="nav nav-tabs" id="SepaTab">

    <%
        List<String> cardList = (List<String>)paymentMap.get(paymodeId);
        for(String cardId : cardList)
        {
          String cardName = GatewayAccountService.getCardType(cardId);
          String cardImg = cardName+".png";
    %>

    <li class="tabs-li-wallet" onclick="sepaHideShow('<%=cardName%>','<%=rb7.getString(cardName)%>')">
      <a href="#<%=cardName%>"  data-toggle="tab" title="<%=cardName%>" aria-expanded="false">
        <img  class="images-style" src="/images/merchant/images/sepa/<%=cardImg%>" alt="<%=cardName%>">
        <div class="label-style"><%=rb7.getString(cardName)%></div>
      </a>
    </li>

    <%
        }

    %>

    <%--<li class="tabs-li-wallet" onclick="sepaHideShow('sepa')">
      <a href="#sepa"  data-toggle="tab" title="profile" aria-expanded="false">
        <img  class="images-style" src="/images/merchant/images/sepa/sepa (2).png">
        <div class="label-style"><%=varSepa%></div>
      </a>
    </li>
    <li class="tabs-li-wallet" onclick="sepaHideShow('sepaexpress')">
      <a href="#sepaexpress" data-toggle="tab" title="picture" aria-expanded="false">
        <img class="images-style"  src="/images/merchant/images/sepa/SEPAEXPRESS.png">
        <div class="label-style"><%=varSepaexpress%></div>
      </a>
    </li>--%>
  </ul>
</div>
<div class="tab-content">

  <%-- SEPA --%>
  <div class="tab-pane" id="SEPA">
    <form id="sepaForm" class="form-style" method="post">

      <%-- card info --%>
      <div class="tab" id="cardinfoSepa"> <p class="form-header" style="display: none"><%=varCardinfo%></p>
        <div class="form-group has-float-label control-group-half" >
          <input type="text" class="form-control input-control1" id="sepafirstname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="sepafirstname" style="color:#757575"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <input type="text" class="form-control input-control1" id="sepalastname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="lastname" />
          <label for="sepalastname" style="color:#757575"><%=varLastname%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <input type="text" class="form-control input-control1" id="sepaIBAN" placeholder=" "
                 oninput="this.className = 'form-control input-control1'" name="iban" />
          <label for="sepaIBAN" style="color:#757575"><%=varIban%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <input type="text" class="form-control input-control1" id="sepaBIC" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="bic" />
          <label for="sepaBIC" style="color:#757575"><%=varBic%></label>
        </div>
        <div class="form-group" style="float: left;width: 100%;height: 26px;padding: 20px 0px;">
          <input type="checkbox" class="" style="width: 5%" id="Mandate" >
          <label for="Mandate" style="color:#757575;font-size: 85%;position: absolute;margin: 0px;">
            Process Transaction Using Mandate.
          </label>
        </div>
      </div>

      <%-- personal info --%>
      <div class="tab" id="personalinfosepa"> <p class="form-header" style="display: none"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-full" id="SepaEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="sepaemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'SepaEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="sepaemail" style="color:#757575"><%=varEmailid%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <div class="dropdown">
            <input id="country_input3" class="form-control input-control1" placeholder=" "  onchange="pincodecc('country_input3','country3','phonecc-id3','phonecc3');"
                   onkeypress="return isLetterKey(event)" oninput="this.className = 'form-control input-control1'" >
            <label for="country_input3"><%=varCountry%></label>
            <input type="hidden" id="country3"  name="country_input">
          </div>
        </div>
        <div class="form-group has-float-label control-group-half" style="width: 10% !important" >
          <input type="text" class="form-control input-control1" id="phonecc-id3" placeholder=" " disabled value="<%=phonecc%>"
                 oninput="this.className = 'form-control input-control1'" />
          <input type="hidden" id="phonecc3"  name="telnocc">
        </div>
        <div class="hyphen form-group has-float-label control-group-half" style="width: 2% !important;padding: 6px 0px;">
          -
        </div>
        <div class="form-group has-float-label control-group-half phonelabel" style="width: 35% !important">
          <input type="text" class="form-control input-control1" id="phonenophonecc3" placeholder=" " onkeypress="return isNumberKey(event)"
                 oninput="this.className = 'form-control input-control1'"  name="telno" value="<%=phoneno%>" />
          <label for="phonenophonecc3" style="color:#757575"><%=varPhoneno%></label>
        </div>
      </div>

      <%-- address info --%>
      <div class="tab" id="addressinfosepa"> <p class="form-header" style="display: none"><%=varAddressinfo%></p>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="sepaaddress" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="street" value="<%=street%>" />
          <label for="sepaaddress" style="color:#757575"><%=varAddress%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="sepacity" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="city" value="<%=city%>" />
          <label for="sepacity" style="color:#757575"><%=varCity%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="sepazip" placeholder=" " onkeypress="return isNumberKey(event)"
                 oninput="this.className = 'form-control input-control1'"  name="zip" value="<%=zip%>" />
          <label for="sepazip" style="color:#757575"><%=varZip%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="sepastate" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="state" value="<%=state%>" />
          <label for="sepastate" style="color:#757575"><%=varState%></label>
        </div>
      </div>

      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'sepaForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_sepa" onclick="disablePayButton(this)" />
        <label for="TC_sepa" style="color:#757575;font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'sepaForm')">
          <%=varNext%>
        </div>
      </div>
    </form>
  </div>

  <%--SEPA EXPRESS --%>
  <div class="tab-pane" id="SEPAEXPRESS">
    <form id="sepaexpressForm" class="form-style" method="post">

      <%-- card info--%>
      <div class="tab" id="cardinfosepaexp"> <p class="form-header" style="display: none;"><%=varCardinfo%></p>
        <div class="form-group has-float-label control-group-half" >
          <input type="text" class="form-control input-control1" id="givenname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" value="" />
          <label for="givenname" class="form-label"><%=varGivenname%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <input type="text" class="form-control input-control1" id="familyname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'" name="lastname" value="" />
          <label for="familyname" class="form-label"><%=varFamilyname%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <input type="text" class="form-control input-control1" id="IBAN" placeholder=" " onkeypress="return alphaNumCheck(event)" onkeyup="onPasteAlphaNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'" name="iban"  value="" />
          <label for="IBAN" class="form-label"><%=varIban%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <input type="text" class="form-control input-control1" id="BIC" placeholder=" " onkeypress="return alphaNumCheck(event)" onkeyup="onPasteAlphaNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'"  name="bic" value="" />
          <label for="BIC" class="form-label"><%=varBic%></label>
        </div>
        <div class="form-group has-float-label control-group-full" >
          <input type="text" class="form-control input-control1" id="sepaExpMandate" placeholder=" " onkeypress="return alphaNumCheck(event)" onkeyup="onPasteAlphaNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'"  name="mandateId" value="" />
          <label for="sepaExpMandate" class="form-label"><%=varMandateid%></label>
        </div>
      </div>

      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'sepaexpressForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_SepaExpress" onclick="disablePayButton(this)"  />
        <label for="TC_SepaExpress" style="color:#757575;font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'sepaexpressForm')">
          <%=varNext%>
        </div>
      </div>

      <input type="hidden" name="paymentBrand" id="paymentBrand" value="SEPAEXPRESS">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>

    </form>
  </div>


  <%-- DIRECTDEBIT --%>
  <div class="tab-pane" id="DIRECTDEBIT">
    <form id="directdebitForm" class="form-style" method="post">

      <%-- card info --%>
      <div class="tab" id="cardinfoDB"> <p class="form-header" style="display: none"><%=varCardinfo%></p>
        <div class="form-group has-float-label control-group-half" >
          <input type="text" class="form-control input-control1" id="dbfirstname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="dbfirstname" style="color:#757575"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <input type="text" class="form-control input-control1" id="dblastname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="lastname" />
          <label for="dblastname" style="color:#757575"><%=varLastname%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <input type="text" class="form-control input-control1" id="dbIBAN" placeholder=" "
                 oninput="this.className = 'form-control input-control1'" name="iban" />
          <label for="dbIBAN" style="color:#757575"><%=varIban%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <input type="text" class="form-control input-control1" id="dbBIC" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="bic" />
          <label for="dbBIC" style="color:#757575"><%=varBic%></label>
        </div>
        <div class="form-group" style="float: left;width: 100%;height: 26px;padding: 20px 0px;">
          <input type="checkbox" class="" style="width: 5%" id="Mandate" >
          <label for="Mandate" style="color:#757575;font-size: 85%;position: absolute;margin: 0px;">
            Process Transaction Using Mandate.
          </label>
        </div>
      </div>

      <%-- personal info --%>
      <div class="tab" id="personalinfoDB"> <p class="form-header" style="display: none"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-full" id="DBEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="dbemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'DBEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="dbemail" style="color:#757575"><%=varEmailid%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <div class="dropdown">
            <input id="country_input_db" class="form-control input-control1" placeholder=" "  onchange="pincodecc('country_input_db','country_db','phonecc_id_db','phonecc_db');"
                   onkeypress="return isLetterKey(event)" oninput="this.className = 'form-control input-control1'" >
            <label for="country_input_db"><%=varCountry%></label>
            <input type="hidden" id="country_db" name="country_input">
          </div>
        </div>
        <div class="form-group has-float-label control-group-half" style="width: 10% !important" >
          <input type="text" class="form-control input-control1" id="phonecc_id_db" placeholder=" " disabled value="<%=phonecc%>"
                 oninput="this.className = 'form-control input-control1'" />
          <input type="hidden" id="phonecc_db"  name="telnocc">
        </div>
        <div class="hyphen form-group has-float-label control-group-half" style="width: 2% !important;padding: 6px 0px;">
          -
        </div>
        <div class="form-group has-float-label control-group-half phonelabel" style="width: 35% !important">
          <input type="text" class="form-control input-control1" id="phoneno_db" placeholder=" " onkeypress="return isNumberKey(event)"
                 oninput="this.className = 'form-control input-control1'"  name="telno" value="<%=phoneno%>" />
          <label for="phoneno_db" style="color:#757575"><%=varPhoneno%></label>
        </div>
      </div>

      <%-- address info --%>
      <div class="tab" id="addressinfoDB"> <p class="form-header" style="display: none"><%=varAddressinfo%></p>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="dbaddress" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="street" value="<%=street%>" />
          <label for="dbaddress" style="color:#757575"><%=varAddress%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="dbcity" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="city" value="<%=city%>" />
          <label for="dbcity" style="color:#757575"><%=varCity%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="dbzip" placeholder=" " onkeypress="return isNumberKey(event)"
                 oninput="this.className = 'form-control input-control1'"  name="zip" value="<%=zip%>" />
          <label for="dbzip" style="color:#757575"><%=varZip%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="dbstate" placeholder=" "
                 oninput="this.className = 'form-control input-control1';stateinp()"  name="state" value="<%=state%>" />
          <label for="dbstate" style="color:#757575"><%=varState%></label>
        </div>
      </div>

      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'directdebitForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_db" onclick="disablePayButton(this)" />
        <label for="TC_db" style="color:#757575;font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'directdebitForm')">
       <%-- <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'directdebitForm')">--%>
          <%=varNext%>
        </div>
      </div>
        <input type="hidden" name="paymentBrand" id="paymentBrand" value="DIRECTDEBIT">
        <jsp:include page="requestParameters.jsp">
          <jsp:param name="paymentMode" value="<%=payMode%>" />
        </jsp:include>
    </form>
  </div>


</div>