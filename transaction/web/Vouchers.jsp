<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.Functions" %>

<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 5/24/2018
  Time: 5:45 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  HashMap paymentMap = standardKitValidatorVO.getMapOfPaymentCardType();
  Functions functions = new Functions();
  String merchantLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo();
  String partnerLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
  String consentFlag = standardKitValidatorVO.getMerchantDetailsVO().getConsentFlag();

  String termsLink = "";
  String privacyLink = "";
  String target = "";
  String customerID="";
  if(functions.isValueNull(standardKitValidatorVO.getCustomerId())){
    customerID=standardKitValidatorVO.getCustomerId();
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

  String email ="";
  String phonecc = "";
  String phoneno = "";

  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getEmail())){
    email =  standardKitValidatorVO.getAddressDetailsVO().getEmail();
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getTelnocc())){
    phonecc =  standardKitValidatorVO.getAddressDetailsVO().getTelnocc();
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getPhone())){
    phoneno =  standardKitValidatorVO.getAddressDetailsVO().getPhone();
  }

  ResourceBundle rb4 = null;
  String lang = "";
  String multiLanguage = "";

  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getLanguage()))
  {
    lang = standardKitValidatorVO.getAddressDetailsVO().getLanguage().toLowerCase();
    if ("ja".equalsIgnoreCase(lang))
    {
      rb4 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ja");
    }
    else if ("bg".equalsIgnoreCase(lang))
    {
      rb4 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_bg");
    }
    else
    {
      rb4 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
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
        rb4 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ja");
      }
      else if ("bg".equalsIgnoreCase(sLanguage[0]))
      {
        rb4 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_bg");
      }
      else
      {
        rb4 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
      }
    }
    else
    {
      rb4 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
    }
  }
  else
  {
    rb4 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
  }

  String varPersonalinfo      = rb4.getString("VAR_PERSONALINFO");
  String varEmailid           = rb4.getString("VAR_EMAILID");
  String varPhonecc           = rb4.getString("VAR_PHONECC");
  String varPhoneno           = rb4.getString("VAR_PHONENO");
  String varNext              = rb4.getString("VAR_NEXT");
  String varExpiry            = rb4.getString("VAR_EXPIRY");
  String varFirstname         = rb4.getString("VAR_FIRSTNAME");
  String varLastname          = rb4.getString("VAR_LASTNAME");
  String varCustomerid        = rb4.getString("VAR_CUSTOMERID");
  String varCustomerVoucherPin= rb4.getString("VAR_VOUCHER_FLEXEPIN");
  String varVouchernumber     = rb4.getString("VAR_VOUCHERNUMBER");
  String varSecuritycode      = rb4.getString("VAR_SECURITYCODE");
  String varBirthdate         = rb4.getString("VAR_BIRTHDATE");
  String consent1             = rb4.getString("VAR_CONSENT_STMT1");
  String consent2             = rb4.getString("VAR_CONSENT_STMT2");
  String consent3             = rb4.getString("VAR_CONSENT_STMT3");
  String varAND               = rb4.getString("VAR_AND");
  String varName              = rb4.getString("VAR_NAME");
  String varCountry           = rb4.getString("VAR_COUNTRY");
  String varAccountingnumber  = rb4.getString("VAR_ACCOUNTINGNUMBER");
  String varIdentifynumber    = rb4.getString("VAR_IDENTIFYNUMBER");
  String varVerificationcode  = rb4.getString("VAR_VERIFICATIONCODE");
%>

<div id="VouchersOption" class="option-class">
  <ul class="nav nav-tabs" id="VoucherTab">

    <%
      String paymodeId = "6";
      String payMode = "PV";
      List<String> cardList = (List<String>)paymentMap.get(paymodeId);
      //System.out.println("contain check---"+cardList.contains("28"));

      //System.out.println("card list---"+cardList);
      for(String cardId : cardList)
      {
        String cardName = GatewayAccountService.getCardType(cardId);
        String cardImg = cardName+".png";
        cardName = cardName.replaceAll(" ", "_");
    %>
    <li class="tabs-li-wallet" onclick="VoucherHideShow('<%=cardName%>','<%=rb4.getString(cardName)%>')">
      <a href="#<%=cardName%>"  data-toggle="tab" title="<%=cardName%>" aria-expanded="false">
        <img  class="images-style"src="/images/merchant/images/voucher/<%=cardImg%>" alt="<%=cardName%>">
        <div class="label-style"><%=rb4.getString(cardName)%></div>
      </a>
    </li>
    <%
      }

    %>

  </ul>
</div>

<div class="tab-content">
  <%
    if(cardList.contains("8"))
    {
  %>
  <div class="tab-pane" id="PAYSAFECARD_V">
    <form id="paysafeCardForm_V" class="form-style" method="post">
      <div class="tab"><p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="paysafecardfirstname_V" name="firstname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'" />
          <label for="paysafecardfirstname_V" style="color:#757575"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="paysafecardlastname_V" name="lastname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'" />
          <label for="paysafecardlastname_V" style="color:#757575"><%=varLastname%></label>
        </div>
        <div class="form-group has-float-label control-group-full" id="PaysafeEmail_V">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="paysafecardemail_V" name="emailaddr" placeholder=" " value="<%=email%>"
                 oninput="this.className = 'form-control input-control1'" onfocusout="validateEmail(event.target.value ,'PaysafeEmail_V')" />
          <label for="paysafecardemail_V" style="color:#757575"><%=varEmailid%></label>
        </div>
        <div class="form-group has-float-label control-group-full" id="PaysafecardBirthDate_V">
          <input type="date" class="form-control input-control1" id="psbirthdate_V" name="paysafecarddate" placeholder=" "
                 oninput="this.className = 'form-control input-control1'" onfocusout="birthdayCheck('psbirthdate' ,'PaysafecardBirthDate_V')" />
          <label for="psbirthdate_V" style="color:#757575"><%=varBirthdate%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'paysafeCardForm_V')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_PaySafeCard_V" onclick="disablePayButton(this)">
        <label for="TC_PaySafeCard_V" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'paysafeCardForm_V')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="PAYSAFECARD">

      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("29"))
    {
  %>
  <!--VOUCHERMONEY-29-->
  <div class="tab-pane" id="VOUCHERMONEY_V">
    <form id="vouchermoneyForm_V" class="form-style" method="post">
      <div class="tab"> <p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-full" id="VoucherMoneyEmail_V">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="vmemail_V" name="emailaddr" placeholder=" " value="<%=email%>"
                 oninput="this.className = 'form-control input-control1'" onfocusout="validateEmail(event.target.value ,'VoucherMoneyEmail_V')" />
          <label for="vmemail_V" style="color:#757575"><%=varEmailid%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phone-CC_voucherMoney_V" name="telnocc" placeholder=" " maxlength="3" value="<%=phonecc%>"
                 oninput="this.className = 'form-control input-control1'" onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" />
          <label for="phone-CC_voucherMoney_V" style="color:#757575"><%=varPhonecc%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phoneno_vm_V" name="telno" placeholder=" " value="<%=phoneno%>"
                 oninput="this.className = 'form-control input-control1'" onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" />
          <label for="phoneno_vm_V" style="color:#757575"><%=varPhoneno%></label>
        </div>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="customerID_vm_V" name="customerid" placeholder=" "  value="<%=customerID%>"
                 oninput="this.className = 'form-control input-control1'" />
          <label for="customerID_vm_V" style="color:#757575"><%=varCustomerid%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'vouchermoneyForm_V')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_VoucherMoney_V" onclick="disablePayButton(this)">
        <label for="TC_VoucherMoney_V" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'vouchermoneyForm_V')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="VOUCHERMONEY">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("33"))
    {
  %>
  <!--JETONVOUCHER-33-->
  <div class="tab-pane" id="JETONVOUCHER_V">
    <form id="jetonvoucherForm_V" class="form-style" method="post">
      <div class="tab"><p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-full" >
          <input type="text" class="form-control input-control1" id="voucherno_jv_V" name="vouchernumber" placeholder=" "
                 oninput="this.className = 'form-control input-control1'" onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" />
          <label for="voucherno_jv_V" style="color:#757575"><%=varVouchernumber%></label>
        </div>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="securitycode_jv_V" name="securityCode" placeholder=" "
                 oninput="this.className = 'form-control input-control1'" onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" />
          <label for="securitycode_jv_V" style="color:#757575"><%=varSecuritycode%></label>
        </div>
        <div class="form-group has-float-label control-group-half" id="JetonVoucherExpiry_V" >
          <input type="text" class="form-control input-control1" id="Expiry_jv_V" name="expiry" placeholder="MM/YY"
                 oninput="this.className = 'form-control input-control1'" onkeyup="addSlash(event,'Expiry_jv_V')" onkeypress="return isNumberKey(event)"
                 onblur="expiryCheck('Expiry_jv_V','JetonVoucherExpiry_V')" />
          <label for="Expiry_jv_V" style="color:#757575"><%=varExpiry%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'jetonvoucherForm_V')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_JetonVoucher_V" onclick="disablePayButton(this)">
        <label for="TC_JetonVoucher_V" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'jetonvoucherForm_V')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="JETONVOUCHER">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("18"))
    {
  %>
  <!--NEOSURF-18-->
  <div class="tab-pane" id="NEOSURF_V">
    <form id="neosurfForm_V" class="form-style" method="post">
      <div class="tab">
        <p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-full" id="NeosurfEmail_V">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="neoemail_V" placeholder=" " onfocusout="validateEmail(event.target.value ,'NeosurfEmail_V')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="neoemail_V" style="color:#757575"><%=varEmailid%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="neosurffirstname_V" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="neosurffirstname_V" style="color:#757575"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="neosurflastname_V" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="lastname" />
          <label for="neosurflastname_V" style="color:#757575"><%=varLastname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phone-CC_ns_V" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" maxlength="3"
                 oninput="this.className = 'form-control input-control1'"  name="telnocc" value="<%=phonecc%>" />
          <label for="phone-CC_ns_V" style="color:#757575"><%=varPhonecc%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phoneno_ns_V" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'"  name="telno" value="<%=phoneno%>" />
          <label for="phoneno_ns_V" style="color:#757575"><%=varPhoneno%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'neosurfForm_V')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_Neosurf_V" onclick="disablePayButton(this)">
        <label for="TC_Neosurf_V" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'neosurfForm_V')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="NEOSURF">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("39"))
    {
  %>
  <!--PURPLEPAY-39-->
  <div class="tab-pane" id="PURPLEPAY_V">
    <form id="purplepayForm_V" class="form-style" method="post">
      <div class="tab"><p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="purplepayfirstname_V" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="purplepayfirstname_V" class="form-label"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="purplepaylastname_V" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="lastname" />
          <label for="purplepaylastname_V" class="form-label"><%=varLastname%></label>
        </div>
        <div class="form-group has-float-label control-group-full" id="PurplepayEmail_V">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="ppemail_V" placeholder=" " onfocusout="validateEmail(event.target.value ,'PurplepayEmail_V')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="ppemail_V" class="form-label"><%=varEmailid%></label>
        </div>
        <div class="form-group has-float-label control-group-full" id="PurplepayBirthDate_V">
          <input type="date" class="form-control input-control1" id="ppbirthdate_V" placeholder=" " onfocusout="birthdayCheck('ppbirthdate' ,'PurplepayBirthDate_V')"
                 oninput="this.className = 'form-control input-control1'"  name="purplepaybirthdate_V" />
          <label for="ppbirthdate_V" class="form-label"><%=varBirthdate%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'purplepayForm_V')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_PurplePay_V" onclick="disablePayButton(this)">
        <label for="TC_PurplePay_V" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'purplepayForm_V')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="PURPLEPAY">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("164"))
    {
  %>
  <!--VOUCHERMONEY-29-->
  <div class="tab-pane" id="FLEXEPIN_VOUCHER_V">
    <form id="FLEXEPINForm_V" class="form-style" method="post">
      <div class="tab"> <p class="form-header"><%--<%=varCustomerVoucherPin%>--%></p>
        <br>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="FLEXEPIN_V" name="voucherPin" maxlength="20" placeholder=" "  value=""
                 oninput="this.className = 'form-control input-control1'" />
          <label for="FLEXEPIN_V" style="color:#757575"><%=varCustomerVoucherPin%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'FLEXEPIN_V')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_FLEXEPIN_VOUCHER_V" onclick="disablePayButton(this)">
        <label for="TC_FLEXEPIN_VOUCHER_V" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'TC_FLEXEPIN_VOUCHER_V')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="FLEXEPIN_VOUCHER">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
  if(cardList.contains("195"))
    {
  %>
  <div class="tab-pane" id="Rapipago_V">
    <form id="Rapipago_VForm" class="form-style" method="post">
      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
        <br>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="Rapipago_V_identifynumber" name="number" maxlength="20" placeholder=""  value=""
                 oninput="this.className = 'form-control input-control1'" />
          <label for="Rapipago_V_identifynumber" style="color:#757575"><%=varIdentifynumber%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'Rapipago_V')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_Rapipago_V" onclick="disablePayButton(this)">
        <label for="TC_Rapipago_V" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'TC_Rapipago_V')">
          <%="Pay"%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="Rapipago">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
  %>



  <% // for appletree integration
    for(String cardId : cardList)
    {
      String cardName = GatewayAccountService.getCardType(cardId);
      String cn = cardName;
      cardName = cardName.replaceAll(" ", "_")+"_V";
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
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="<%=cn%>">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>

    </form>
  </div>
  <%
    }
  %>


</div>
