<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.directi.pg.Functions" %>
<%--
  Created by IntelliJ IDEA.
  User: Rihen
  Date: 9/15/2021
  Time: 7:00 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  HashMap paymentMap = standardKitValidatorVO.getMapOfPaymentCardType();

  String paymodeId  = "48";
  String payMode    = "IBT";

  Functions functions     = new Functions();
  String merchantLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo();
  String partnerLogoFlag  = standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
  String consentFlag      = standardKitValidatorVO.getMerchantDetailsVO().getConsentFlag();

  String termsLink    = "";
  String privacyLink  = "";
  String target       = "";

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

  List<String> cardList = (List<String>)paymentMap.get(paymodeId);
  String varPersonalinfo=rb.getString("VAR_PERSONALINFO");
  String varEmailid=rb.getString("VAR_EMAILID");
  String varPhoneno=rb.getString("VAR_PHONENO");
  String varNext=rb.getString("VAR_NEXT");
  String consent1 = rb.getString("VAR_CONSENT_STMT1");
  String consent2 = rb.getString("VAR_CONSENT_STMT2");
  String consent3 = rb.getString("VAR_CONSENT_STMT3");
  String varAND = rb.getString("VAR_AND");
  String varAccountingnumber=rb.getString("VAR_ACCOUNTINGNUMBER");
  String varName=rb.getString("VAR_NAME");
  String varCountry=rb.getString("VAR_COUNTRY");
%>


<div id="InstantBankTransferOption" class="option-class">
  <ul class="nav nav-tabs" id="InstantBankTransferTab">

    <%

      for(String cardId : cardList)
      {
        String cardName = GatewayAccountService.getCardType(cardId);
        String cardImg = cardName+".png";
        cardName = cardName.replaceAll(" ", "_");
    %>
    <li class="tabs-li-wallet" onclick="instantBankTransferHideShow('<%=cardName%>','<%=rb.getString(cardName)%>')">
      <a href="#<%=cardName%>"  data-toggle="tab" title="<%=cardName%>" aria-expanded="false">
        <img class="images-style" src="/images/merchant/images/<%=cardImg%>" alt="<%=cardName%>">
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
  for(String cardId : cardList)
  {
    String cardName = GatewayAccountService.getCardType(cardId);
    String cn = cardName;
    cardName = cardName.replaceAll(" ", "_")+"_IBT";
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
          $( "#country_input_<%=cardName%>").autocomplete({
            source: availableTags
          });
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