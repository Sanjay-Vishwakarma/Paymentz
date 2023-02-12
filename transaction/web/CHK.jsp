<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.Functions" %>

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

  String paymodeId = "8";

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

  String varBankName=rb1.getString("VAR_BANK_NAME");
  String varBankAddress=rb1.getString("VAR_BANK_ADDRESS");
  String varBankCity=rb1.getString("VAR_BANK_CITY");
  String varBankZip=rb1.getString("VAR_BANK_ZIP");
  String varBankState=rb1.getString("VAR_BANK_STATE");
  String varCheckNumber=rb1.getString("VAR_CHECK_NUMBER");

%>

<div id="CHKOption" class="option-class">
  <ul class="nav nav-tabs" id="CHKTab">

    <%
      List<String> cardList = (List<String>)paymentMap.get(paymodeId);
      for(String cardId : cardList)
      {
        String cardName = GatewayAccountService.getCardType(cardId);
        String cardImg = cardName+".png";
    %>
    <li class="tabs-li-wallet" onclick="chkHideShow('<%=cardName%>','<%=rb1.getString(cardName)%>')">
      <a href="#<%=cardName%>"  data-toggle="tab" title="<%=cardName%>" aria-expanded="false">
        <img class="images-style" src="images/merchant/<%=cardImg%>" alt="<%=cardName%>">
        <div class="label-style"><%=rb1.getString(cardName)%></div>
      </a>
    </li>

    <%
      }

    %>

<%--    <li class="tabs-li-wallet" onclick="chkHideShow('<%=rb1.getString("DUSPAY")%>','DUSPAY')">
      <a href="#DUSPAY"  data-toggle="tab" title="DUSPAY" aria-expanded="false">
        <img class="images-style" src="images/merchant/DUSPAY.png" alt="DUSPAY">
        <div class="label-style">DUSPAY</div>
      </a>
    </li>--%>

  </ul>
</div>
<div class="tab-content">

  <div class="tab-pane" id="CHKID">
    <form id="chkForm" class="form-style" method="post">

      <%-- card details --%>
      <div class="tab" id="cardinfoCHK" style="padding-top: 20px;height: 238px;" ><p class="form-header" style="display: none;"><%=varCardinfo%></p>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="chkfirstname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="chkfirstname" class="form-label"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="chklastname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="lastname" />
          <label for="chklastname" class="form-label"><%=varLastname%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <input class="form-control input-control1" id="chkAccNo" placeholder=" " type="text" onkeypress="return isNumberKey(event)"
                 maxlength="10" oninput="this.className = 'form-control input-control1'" name="accountnumber" />
          <label for="chkAccNo" class="form-label"><%=varAccountingnumber%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <input class="form-control input-control1" id="chkRoutNo" placeholder=" " type="text" onkeypress="return isNumberKey(event)"
                 maxlength="9" oninput="this.className = 'form-control input-control1'" name="routingnumber" />
          <label for="chkRoutNo" class="form-label"><%=varRoutingnumber%></label>
        </div>
        <div class="form-group has-float-label control-group-full" >
          <select class="form-control input-control1" name="accountType" id="chkAccType">
            <option value="PC">Personal Checking</option>
            <option value="PS">Personal Savings</option>
            <option value="CC">Commercial Checking</option>
          </select>
          <label for="chkAccType" class="form-label"><%=varAccounttype%></label>
        </div>
      </div>

      <%-- address details --%>
      <div class="tab" id="addressinfoCHK" style="padding-top: 20px;" > <p class="form-header" style="display: none;"><%=varAddressinfo%></p>
        <div class="form-group has-float-label control-group-full" id="CHKAddress" >
          <input type="text" class="form-control input-control1" id="chkaddress" placeholder=" " onfocusout="validateAddress(event.target.value ,'CHKAddress')"
                 oninput="this.className = 'form-control input-control1'"  name="street" value="<%=street%>" />
          <label for="chkaddress" class="form-label"><%=varAddress%></label>
        </div>
        <div class="form-group has-float-label control-group-half" id="CHKCity">
          <input type="text" class="form-control input-control1" id="chkcity" placeholder=" " onfocusout="validateCity(event.target.value ,'CHKCity')"
                 oninput="this.className = 'form-control input-control1'"  name="city" value="<%=city%>" />
          <label for="chkcity" class="form-label"><%=varCity%></label>
        </div>
        <div class="form-group has-float-label control-group-half" id="CHKZip" >
          <input type="text" class="form-control input-control1" id="chkzip" placeholder=" " onfocusout="validateZip(event.target.value ,'CHKZip')"
                 oninput="this.className = 'form-control input-control1'" name="zip" value="<%=zip%>" />
          <label for="chkzip" class="form-label"><%=varZip%></label>
        </div>
        <div class="form-group has-float-label control-group-half" id="CHKCountry" >
          <div class="dropdown">
            <input id="country_input_CHK" class="form-control input-control1" placeholder=" " onfocusout="validateCountry(event.target.value ,'CHKCountry')"
                   onblur="pincodecc('country_input_CHK','country_CHK','phonecc-id_CHK','phonecc2_CHK','country_input_CHK','country_CHK','statelabel_CHK')"
                   onkeypress="return isLetterKey(event)" oninput="this.className = 'form-control input-control1'"  >
            <label for="country_input_CHK" class="form-label"><%=varCountry%></label>
            <input type="hidden" id="country_CHK"  name="country_input">
          </div>
        </div>
        <div class="form-group has-float-label control-group-half" id="CHKState" >
          <input type="text" class="form-control input-control1" id="chkstate" placeholder=" " oninput="this.className = 'form-control input-control1'; stateinp();"
                 onfocusout="validateState(event.target.value ,'CHKState')" name="state" value="<%=state%>" />
          <label for="chkstate" id="statelabel_CHK" class="form-label"><%=varState%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phonecc-id_CHK" placeholder=" " onkeypress="return isNumberKey(event)"
                 onkeyup="onPasteNumCheck(event)"  maxlength="3" onblur ="setPhoneCC('phonecc-id_CHK','phonecc2_CHK')" value="<%=phonecc%>"
                 oninput="this.className = 'form-control input-control1'" />
          <label for="phonecc-id_CHK" class="form-label"><%=varPhonecc%></label>
          <input type="hidden" id="phonecc2_CHK"  name="phone-CC" value="<%=phonecc%>">
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phoneno" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'" name="telno" value="<%=phoneno%>" />
          <label for="phoneno" class="form-label"><%=varPhoneno%></label>
        </div>
        <div class="form-group has-float-label control-group-full" id="CHKEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="chkemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'CHKEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="chkemail" class="form-label"><%=varEmailid%></label>
        </div>

        <script>
          setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_CHK');
          pincodecc('country_input_CHK','country_CHK','phonecc-id_CHK','phonecc2_CHK','country_input_CHK','country_CHK','statelabel_CHK');
          validateCountry(document.getElementById('country_input_CHK').value ,'CHKCountry');
          StateLabel('country_input_CHK','country_CHK','statelabel_CHK');
        </script>

      </div>


      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox " id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_CHK" onclick="disablePayButton(this)" >
        <label for="TC_CHK" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>

      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'chkForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>

      <div class="pay-btn" >
        <button type="button" class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1,'chkForm')" tabindex="0">
          <%=varNext%>
        </button>
      </div>

      <input type="hidden" name="paymentBrand" id="paymentBrand" value="CHK">

      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="CHK" />
      </jsp:include>

    </form>
  </div>




  <%-- eCheck --%>

  <div class="tab-pane" id="eCheckCHKID">
    <form id="eCheckForm" class="form-style" method="post">

      <%-- card details --%>
      <div class="tab" id="cardinfoDP" style="padding-top: 20px;height: 238px;" ><p class="form-header" style="display: none;"><%=varCardinfo%></p>
        <div class="form-group has-float-label control-group-half" id="DPFirstname">
          <input type="text" class="form-control input-control1" id="dpfirstname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="dpfirstname" class="form-label"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="dplastname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="lastname" />
          <label for="dplastname" class="form-label"><%=varLastname%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <input class="form-control input-control1" id="dpAccNo" placeholder=" " type="text" onkeypress="return isNumberKey(event)"
                 maxlength="16" oninput="this.className = 'form-control input-control1'" name="accountnumber" />
          <label for="dpAccNo" class="form-label"><%=varAccountingnumber%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <input class="form-control input-control1" id="dpRoutNo" placeholder=" " type="text" onkeypress="return isNumberKey(event)"
                 maxlength="9" oninput="this.className = 'form-control input-control1'" name="routingnumber" />
          <label for="dpRoutNo" class="form-label"><%=varRoutingnumber%></label>
        </div>
      </div>


      <%-- bank details  --%>
        <div class="tab" id="bankInfoDusPay" style="padding-top: 20px;height: 238px;" > <p class="form-header" style="display: none;"><%=varAddressinfo%></p>
          <div class="form-group has-float-label control-group-full">
            <input type="text" class="form-control input-control1" id="dpBankname" placeholder=" "
                   oninput="this.className = 'form-control input-control1'"  name="bankName" />
            <label for="dpBankname" class="form-label"><%=varBankName%></label>
          </div>
          <div class="form-group has-float-label control-group-full" id="DPBankAddress" >
            <input type="text" class="form-control input-control1" id="dpbankaddress" placeholder=" " onfocusout="validateAddress(event.target.value ,'DPBankAddress')"
                   oninput="this.className = 'form-control input-control1'"  name="bankAddress" value="<%=street%>" />
            <label for="dpbankaddress" class="form-label"><%=varBankAddress%></label>
          </div>
          <div class="form-group has-float-label control-group-half" id="DPBankCity">
            <input type="text" class="form-control input-control1" id="dpbankcity" placeholder=" " onfocusout="validateCity(event.target.value ,'DPBankCity')"
                   oninput="this.className = 'form-control input-control1'"  name="bankCity" value="<%=city%>" />
            <label for="dpbankcity" class="form-label"><%=varBankCity%></label>
          </div>
          <div class="form-group has-float-label control-group-half" id="DPBankZip" >
            <input type="text" class="form-control input-control1" id="dpbankzip" placeholder=" " onfocusout="validateZip(event.target.value ,'DPBankZip')"
                   oninput="this.className = 'form-control input-control1'" name="bankZipcode" value="<%=zip%>" />
            <label for="dpbankzip" class="form-label"><%=varBankZip%></label>
          </div>
          <div class="form-group has-float-label control-group-full" id="DPBankState" >
            <input type="text" class="form-control input-control1" id="dpBankstate" placeholder=" " oninput="this.className = 'form-control input-control1';"
                   onfocusout="validateState(event.target.value ,'DPBankState')" name="bankState" value="<%=state%>" />
            <label for="dpBankstate" id="statelabel_bank_DP" class="form-label"><%=varBankState%></label>
          </div>
        </div>


      <%-- address details --%>
      <div class="tab" id="addressinfoDusPay" style="padding-top: 20px;" > <p class="form-header" style="display: none;"><%=varAddressinfo%></p>
        <div class="form-group has-float-label control-group-full" id="DPAddress" >
          <input type="text" class="form-control input-control1" id="dpaddress" placeholder=" " onfocusout="validateAddress(event.target.value ,'DPAddress')"
                 oninput="this.className = 'form-control input-control1'"  name="street" value="<%=street%>" />
          <label for="dpaddress" class="form-label"><%=varAddress%></label>
        </div>
        <div class="form-group has-float-label control-group-half" id="DPCity">
          <input type="text" class="form-control input-control1" id="dpcity" placeholder=" " onfocusout="validateCity(event.target.value ,'DPCity')"
                 oninput="this.className = 'form-control input-control1'"  name="city" value="<%=city%>" />
          <label for="dpcity" class="form-label"><%=varCity%></label>
        </div>
        <div class="form-group has-float-label control-group-half" id="DPZip" >
          <input type="text" class="form-control input-control1" id="dpzip" placeholder=" " onfocusout="validateZip(event.target.value ,'DPZip')"
                 oninput="this.className = 'form-control input-control1'" name="zip" value="<%=zip%>" />
          <label for="dpzip" class="form-label"><%=varZip%></label>
        </div>
        <div class="form-group has-float-label control-group-half" id="DPCountry" >
          <div class="dropdown">
            <input id="country_input_DP" class="form-control input-control1" placeholder=" " onfocusout="validateCountry(event.target.value ,'DPCountry')"
                   onblur="pincodecc('country_input_DP','country_DP','phonecc-id_DP','phonecc2_DP','country_input_DP','country_DP','statelabel_DP')"
                   onkeypress="return isLetterKey(event)" oninput="this.className = 'form-control input-control1'"  >
            <label for="country_input_DP" class="form-label"><%=varCountry%></label>
            <input type="hidden" id="country_DP"  name="country_input">
          </div>
        </div>
        <div class="form-group has-float-label control-group-half" id="DPState" >
          <input type="text" class="form-control input-control1" id="dpstate" placeholder=" " oninput="this.className = 'form-control input-control1'; stateinp();"
                 onfocusout="validateState(event.target.value ,'DPState')" name="state" value="<%=state%>" />
          <label for="dpstate" id="statelabel_DP" class="form-label"><%=varState%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phonecc-id_DP" placeholder=" " onkeypress="return isNumberKey(event)"
                 onkeyup="onPasteNumCheck(event)"  maxlength="3" onblur ="setPhoneCC('phonecc-id_DP','phonecc2_DP')" value="<%=phonecc%>"
                 oninput="this.className = 'form-control input-control1'" />
          <label for="phonecc-id_DP" class="form-label"><%=varPhonecc%></label>
          <input type="hidden" id="phonecc2_DP"  name="phone-CC" value="<%=phonecc%>">
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phonenoDP" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'" name="telno" value="<%=phoneno%>" />
          <label for="phonenoDP" class="form-label"><%=varPhoneno%></label>
        </div>
        <div class="form-group has-float-label control-group-full" id="DPEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="dpemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'DPEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="dpemail" class="form-label"><%=varEmailid%></label>
        </div>

        <script>
          setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_DP');
          pincodecc('country_input_DP','country_DP','phonecc-id_DP','phonecc2_DP','country_input_DP','country_DP','statelabel_DP');
          validateCountry(document.getElementById('country_input_DP').value ,'DPCountry');
          StateLabel('country_input_DP','country_DP','statelabel_DP');
        </script>

      </div>


      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox " id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_duspay" onclick="disablePayButton(this)" >
        <label for="TC_duspay" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>

      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'eCheckForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>

      <div class="pay-btn" >
        <button type="button" class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1,'eCheckForm')" tabindex="0">
          <%=varNext%>
        </button>
      </div>

      <input type="hidden" name="paymentBrand" id="paymentBrand" value="eCheck">

      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="CHK" />
      </jsp:include>

    </form>
  </div>


  <%-- ezPay --%>
  <div class="tab-pane" id="EZPAY">
    <form id="ezPayForm" class="form-style" method="post">

      <%-- card details --%>
      <div class="tab" id="cardinfoEP" style="padding-top: 20px;height: 238px;" ><p class="form-header" style="display: none;"><%=varCardinfo%></p>
        <div class="form-group has-float-label control-group-half" id="EPFirstname">
          <input type="text" class="form-control input-control1" id="epfirstname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="epfirstname" class="form-label"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="eplastname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="lastname" />
          <label for="eplastname" class="form-label"><%=varLastname%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <input class="form-control input-control1" id="epAccNo" placeholder=" " type="text" onkeypress="return isNumberKey(event)"
                 maxlength="16" oninput="this.className = 'form-control input-control1'" name="accountnumber" />
          <label for="epAccNo" class="form-label"><%=varAccountingnumber%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <input class="form-control input-control1" id="epRoutNo" placeholder=" " type="text" onkeypress="return isNumberKey(event)"
                 maxlength="9" oninput="this.className = 'form-control input-control1'" name="routingnumber" />
          <label for="epRoutNo" class="form-label"><%=varRoutingnumber%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <input class="form-control input-control1" id="epCheckNo" placeholder=" " type="text" onkeypress="return isNumberKey(event)"
                 maxlength="12" oninput="this.className = 'form-control input-control1'" name="checknumber" />
          <label for="epCheckNo" class="form-label"><%=varCheckNumber%></label>
        </div>
      </div>

      <%-- bank details  --%>
      <div class="tab" id="bankInfoEzPay" style="padding-top: 20px;height: 238px;" > <p class="form-header" style="display: none;"><%=varAddressinfo%></p>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="epBankname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="bankName" />
          <label for="epBankname" class="form-label"><%=varBankName%></label>
        </div>
        <div class="form-group has-float-label control-group-full" id="EPBankAddress" >
          <input type="text" class="form-control input-control1" id="epbankaddress" placeholder=" " onfocusout="validateAddress(event.target.value ,'EPBankAddress')"
                 oninput="this.className = 'form-control input-control1'"  name="bankAddress" value="<%=street%>" />
          <label for="epbankaddress" class="form-label"><%=varBankAddress%></label>
        </div>
        <div class="form-group has-float-label control-group-half" id="EPBankCity">
          <input type="text" class="form-control input-control1" id="epbankcity" placeholder=" " onfocusout="validateCity(event.target.value ,'EPBankCity')"
                 oninput="this.className = 'form-control input-control1'"  name="bankCity" value="<%=city%>" />
          <label for="epbankcity" class="form-label"><%=varBankCity%></label>
        </div>
        <div class="form-group has-float-label control-group-half" id="EPBankZip" >
          <input type="text" class="form-control input-control1" id="epbankzip" placeholder=" " onfocusout="validateZip(event.target.value ,'EPBankZip')"
                 oninput="this.className = 'form-control input-control1'" name="bankZipcode" value="<%=zip%>" />
          <label for="epbankzip" class="form-label"><%=varBankZip%></label>
        </div>
        <div class="form-group has-float-label control-group-full" id="EPBankState" >
          <input type="text" class="form-control input-control1" id="epBankstate" placeholder=" " oninput="this.className = 'form-control input-control1';"
                 onfocusout="validateState(event.target.value ,'EPBankState')" name="bankState" value="<%=state%>" />
          <label for="epBankstate" id="statelabel_bank_EP" class="form-label"><%=varBankState%></label>
        </div>
      </div>

      <%-- address details --%>
      <div class="tab" id="addressinfoEzPay" style="padding-top: 20px;" > <p class="form-header" style="display: none;"><%=varAddressinfo%></p>
        <div class="form-group has-float-label control-group-full" id="EPAddress" >
          <input type="text" class="form-control input-control1" id="epaddress" placeholder=" " onfocusout="validateAddress(event.target.value ,'EPAddress')"
                 oninput="this.className = 'form-control input-control1'"  name="street" value="<%=street%>" />
          <label for="epaddress" class="form-label"><%=varAddress%></label>
        </div>
        <div class="form-group has-float-label control-group-half" id="EPCity">
          <input type="text" class="form-control input-control1" id="epcity" placeholder=" " onfocusout="validateCity(event.target.value ,'EPCity')"
                 oninput="this.className = 'form-control input-control1'"  name="city" value="<%=city%>" />
          <label for="epcity" class="form-label"><%=varCity%></label>
        </div>
        <div class="form-group has-float-label control-group-half" id="EPZip" >
          <input type="text" class="form-control input-control1" id="epzip" placeholder=" " onfocusout="validateZip(event.target.value ,'EPZip')"
                 oninput="this.className = 'form-control input-control1'" name="zip" value="<%=zip%>" />
          <label for="epzip" class="form-label"><%=varZip%></label>
        </div>
        <div class="form-group has-float-label control-group-half" id="EPCountry" >
          <div class="dropdown">
            <input id="country_input_EP" class="form-control input-control1" placeholder=" " onfocusout="validateCountry(event.target.value ,'EPCountry')"
                   onblur="pincodecc('country_input_EP','country_EP','phonecc-id_EP','phonecc2_EP','country_input_EP','country_EP','statelabel_EP')"
                   onkeypress="return isLetterKey(event)" oninput="this.className = 'form-control input-control1'"  >
            <label for="country_input_EP" class="form-label"><%=varCountry%></label>
            <input type="hidden" id="country_EP"  name="country_input">
          </div>
        </div>
        <div class="form-group has-float-label control-group-half" id="EPState" >
          <input type="text" class="form-control input-control1" id="epstate" placeholder=" " oninput="this.className = 'form-control input-control1'; stateinp();"
                 onfocusout="validateState(event.target.value ,'EPState')" name="state" value="<%=state%>" />
          <label for="epstate" id="statelabel_EP" class="form-label"><%=varState%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phonecc-id_EP" placeholder=" " onkeypress="return isNumberKey(event)"
                 onkeyup="onPasteNumCheck(event)"  maxlength="3" onblur ="setPhoneCC('phonecc-id_EP','phonecc2_EP')" value="<%=phonecc%>"
                 oninput="this.className = 'form-control input-control1'" />
          <label for="phonecc-id_EP" class="form-label"><%=varPhonecc%></label>
          <input type="hidden" id="phonecc2_EP"  name="phone-CC" value="<%=phonecc%>">
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phonenoEP" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'" name="telno" value="<%=phoneno%>" />
          <label for="phonenoEP" class="form-label"><%=varPhoneno%></label>
        </div>
        <div class="form-group has-float-label control-group-full" id="EPEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="epemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'EPEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="epemail" class="form-label"><%=varEmailid%></label>
        </div>

        <script>
          setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_EP');
          pincodecc('country_input_EP','country_EP','phonecc-id_EP','phonecc2_EP','country_input_EP','country_EP','statelabel_EP');
          validateCountry(document.getElementById('country_input_EP').value ,'EPCountry');
          StateLabel('country_input_EP','country_EP','statelabel_EP');
        </script>

      </div>


      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox " id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_EzPay" onclick="disablePayButton(this)" >
        <label for="TC_EzPay" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>

      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'ezPayForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>

      <div class="pay-btn" >
        <button type="button" class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1,'ezPayForm')" tabindex="0">
          <%=varNext%>
        </button>
      </div>

      <input type="hidden" name="paymentBrand" id="paymentBrand" value="EZPAY">

      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="CHK" />
      </jsp:include>

    </form>
  </div>



</div>