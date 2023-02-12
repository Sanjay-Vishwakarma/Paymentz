<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.google.zxing.qrcode.QRCodeWriter" %>
<%@ page import="com.google.zxing.common.BitMatrix" %>
<%@ page import="com.google.zxing.BarcodeFormat" %>
<%@ page import="java.nio.file.Path" %>
<%@ page import="java.nio.file.FileSystems" %>
<%@ page import="com.google.zxing.client.j2se.MatrixToImageWriter" %>
<%--
  Created by IntelliJ IDEA.
  User: Rihen
  Date: 2/26/2021
  Time: 5:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  HashMap paymentMap = standardKitValidatorVO.getMapOfPaymentCardType();

  String paymodeId = "41";
  String payMode = "MB";

  Functions functions = new Functions();
  String merchantLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo();
  String partnerLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
  String consentFlag = standardKitValidatorVO.getMerchantDetailsVO().getConsentFlag();

  String termsLink = "";
  String privacyLink = "";
  String target = "";

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

  ResourceBundle rb1 = LoadProperties.getProperty("com.directi.pg.QRCode");
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
  String varPhonecc=rb.getString("VAR_PHONECC");
  String varPhoneno=rb.getString("VAR_PHONENO");
  String varNext=rb.getString("VAR_NEXT");
  String varFirstname=rb.getString("VAR_FIRSTNAME");
  String varLastname=rb.getString("VAR_LASTNAME");
  String consent1 = rb.getString("VAR_CONSENT_STMT1");
  String consent2 = rb.getString("VAR_CONSENT_STMT2");
  String consent3 = rb.getString("VAR_CONSENT_STMT3");
  String varAND = rb.getString("VAR_AND");
  String varAccountingnumber=rb.getString("VAR_ACCOUNTINGNUMBER");
  String varName=rb.getString("VAR_NAME");
  String varCountry=rb.getString("VAR_COUNTRY");
%>


<div id="MobileBankingOption" class="option-class">
  <ul class="nav nav-tabs" id="MobileBankingTab">

    <%

      List<String> cardList = (List<String>)paymentMap.get(paymodeId);
      for(String cardId : cardList)
      {
        String cardName = GatewayAccountService.getCardType(cardId);
        String cardImg = cardName+".png";
    %>
    <li class="tabs-li-wallet" onclick="mobileBankingHideShow('<%=cardName%>','<%=rb.getString(cardName)%>')">
      <a href="#<%=cardName%>"  data-toggle="tab" title="<%=cardName%>" aria-expanded="false">
        <img class="images-style" src="/images/merchant/images/mobilebanking/<%=cardImg%>" alt="<%=cardName%>">
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
    if(cardList.contains("98"))
    {
  %>
  <!--TELECASH-96-->
  <div class="tab-pane" id="TELECASH_MB">
    <form id="telecashForm_MB" class="form-style" method="post">

      <div class="tab">
        <%--<p class="form-header"><%=varPersonalinfo%></p>--%>

        <div class="form-group has-float-label control-group-full" id="TelecashEmail_MB">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="telecashemail_MB" placeholder=" " onfocusout="validateEmail(event.target.value ,'TelecashEmail_MB')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="telecashemail_MB" class="form-label"><%=varEmailid%></label>
        </div>

        <div class="form-group has-float-label control-group-half" >
          <div class="dropdown">
            <input id="country_input_telecash_MB" class="form-control input-control1" placeholder=" " oninput="this.className = 'form-control input-control1'"
                   onblur="pincodecc('country_input_telecash_MB','country_telecash_MB','phonecc_id_telecash_MB','phonecc_telecash_MB'); " onkeypress="return isLetterKey(event)" />
            <label for="country_input_telecash_MB" class="form-label"><%=varCountry%></label>
            <input type="hidden" id="country_telecash_MB"  name="country_input" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
          </div>
        </div>

        <div class="form-group has-float-label control-group-half" style="width: 10% !important">
          <input type="text" class="form-control input-control1" name="phone-CC" id="phonecc_id_telecash_MB" placeholder=" " onkeypress="return isNumberKey(event)"
                 onkeyup="onPasteNumCheck(event)" maxlength="3" onblur="setPhoneCC('phonecc_id_telecash_MB','phonecc_telecash_MB')" value="<%=phonecc%>"
                 oninput="this.className = 'form-control input-control1'"/>
          <label for="phonecc_id_telecash_MB" class="form-label">CC</label>
          <input type="hidden" id="phonecc_telecash_MB" name="phone-CC" value="<%=phonecc%>"/>
        </div>
        <div class="form-group has-float-label control-group-half" style="width: 3% !important;padding: 6px 0px;">
          -
        </div>
        <div class="form-group has-float-label control-group-half" id="TelecashPhoneNum_MB" style="width: 35% !important">
          <span class="input-icon-half"><i class="fas fa-phone"></i> </span>
          <input type="text" class="form-control input-control1" id="phone_telecash_MB" placeholder=" " onkeypress="return isNumberKey(event)"
                 onkeyup="onPasteNumCheck(event)" oninput="this.className = 'form-control input-control1'" maxlength="24" name="telno" value="<%=phoneno%>"/>
          <label for="phone_telecash_MB" class="form-label"><%=varPhoneno%></label>
        </div>

        <%--<div class="form-group has-float-label control-group-half">
          <input class="form-control input-control1" id="telecashAccNo_MB" placeholder=" " type="text" onkeypress="return isNumberKey(event)"
                 maxlength="17" oninput="this.className = 'form-control input-control1'" name="accountnumber" />
          <label for="telecashAccNo_MB" class="form-label"><%=varAccountingnumber%></label>
        </div>--%>

        <div class="form-group has-float-label control-group-full" id="TelecashName_MB">
          <input type="text" class="form-control input-control1" id="telecashname_MB" placeholder=" "
                 oninput="this.className = 'form-control input-control1'" name="firstname"/>
          <label for="telecashname_MB" class="form-label"><%=varName%></label>
        </div>

        <script>
          setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_telecash_MB');
          pincodecc('country_input_telecash_MB','country_telecash_MB','phonecc_id_telecash_MB','phonecc_telecash_MB');
        </script>

      </div>

      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'telecashForm_MB')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_telecash_MB" onclick="disablePayButton(this)">
        <label for="TC_telecash_MB" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'telecashForm_MB')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="TELECASH">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>

    </form>
  </div>


  <%
    }
    if(cardList.contains("97"))
    {
  %>
  <!--ECOCASH-97-->
  <div class="tab-pane" id="ECOCASH_MB">
    <form id="ecocashForm_MB" class="form-style" method="post">

      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>

        <div class="form-group has-float-label control-group-full" id="EcocashEmail_MB">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="ecocashEmail_MB" placeholder=" " onfocusout="validateEmail(event.target.value ,'EcocashEmail_MB')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="ecocashEmail_MB" class="form-label"><%=varEmailid%></label>
        </div>

        <div class="form-group has-float-label control-group-half">
          <div class="dropdown">
            <input id="country_input_ecocash_MB" class="form-control input-control1" placeholder=" " oninput="this.className = 'form-control input-control1'"
                   onblur="pincodecc('country_input_ecocash_MB','country_ecocash_MB','phonecc_id_ecocash_MB','phonecc_ecocash_MB'); " onkeypress="return isLetterKey(event)" />
            <label for="country_input_ecocash_MB" class="form-label"><%=varCountry%></label>
            <input type="hidden" id="country_ecocash_MB"  name="country_input" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
          </div>
        </div>

        <div class="form-group has-float-label control-group-half" style="width: 10% !important">
          <input type="text" class="form-control input-control1" name="phone-CC" id="phonecc_id_ecocash_MB" placeholder=" " onkeypress="return isNumberKey(event)"
                 onkeyup="onPasteNumCheck(event)" maxlength="3" onblur="setPhoneCC('phonecc_id_ecocash_MB','phonecc_ecocash_MB')" value="<%=phonecc%>"
                 oninput="this.className = 'form-control input-control1'"/>
          <label for="phonecc_id_ecocash_MB" class="form-label">CC</label>
          <input type="hidden" id="phonecc_ecocash_MB" name="phone-CC" value="<%=phonecc%>"/>
        </div>
        <div class="form-group has-float-label control-group-half" style="width: 3% !important;padding: 6px 0px;">
          -
        </div>
        <div class="form-group has-float-label control-group-half" id="EcocashPhoneNum_MB" style="width: 35% !important">
          <span class="input-icon-half"><i class="fas fa-phone"></i> </span>
          <input type="text" class="form-control input-control1" id="phone_ecocash_MB" placeholder=" " onkeypress="return isNumberKey(event)"
                 onkeyup="onPasteNumCheck(event)" oninput="this.className = 'form-control input-control1'" maxlength="24" name="telno" value="<%=phoneno%>"/>
          <label for="phone_ecocash_MB" class="form-label"><%=varPhoneno%></label>
        </div>

        <%--<div class="form-group has-float-label control-group-half">
          <input class="form-control input-control1" id="ecocashAccNo_MB" placeholder=" " type="text" onkeypress="return isNumberKey(event)"
                 maxlength="17" oninput="this.className = 'form-control input-control1'" name="accountnumber" />
          <label for="ecocashAccNo_MB" class="form-label"><%=varAccountingnumber%></label>
        </div>--%>

        <div class="form-group has-float-label control-group-full" id="EcocashName_MB">
          <input type="text" class="form-control input-control1" id="ecocashname_MB" placeholder=" "
                 oninput="this.className = 'form-control input-control1'" name="firstname"/>
          <label for="ecocashname_MB" class="form-label"><%=varName%></label>
        </div>

        <script>
          setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_ecocash_MB');
          pincodecc('country_input_ecocash_MB','country_ecocash_MB','phonecc_id_ecocash_MB','phonecc_ecocash_MB');
        </script>

      </div>

      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'ecocashForm_MB')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_ecocash_MB" onclick="disablePayButton(this)">
        <label for="TC_ecocash_MB" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'ecocashForm_MB')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="ECOCASH">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>

    </form>
  </div>
  <%
    }
  %>




  <%
    for(String cardId : cardList)
    {
      String cardName = GatewayAccountService.getCardType(cardId);
      cardName = cardName.replaceAll(" ", "_");
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
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="<%=cardName%>">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>

    </form>
  </div>
  <%
    }
  %>



</div>