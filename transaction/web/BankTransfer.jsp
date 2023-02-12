<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.directi.pg.Functions" %>
<%--
  Created by IntelliJ IDEA.
  User: Rihen
  Date: 1/23/2019
  Time: 5:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  CommonValidatorVO standardKitValidatorVO  = (CommonValidatorVO) request.getAttribute("transDetails");
  HashMap paymentMap                        = standardKitValidatorVO.getMapOfPaymentCardType();

  String paymodeId = "33";
  String payMode = "BT";

  Functions functions       = new Functions();
  String merchantLogoFlag   = standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo();
  String partnerLogoFlag    = standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
  String consentFlag        = standardKitValidatorVO.getMerchantDetailsVO().getConsentFlag();

  String termsLink    = "";
  String privacyLink  = "";
  String target       = "";

  String email    = "";
  String phonecc  = "";
  String phoneno  = "";
  String currencyPg = "";

  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getEmail())){
    email =  standardKitValidatorVO.getAddressDetailsVO().getEmail();
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getTelnocc())){
    phonecc =  standardKitValidatorVO.getAddressDetailsVO().getTelnocc();
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getPhone())){
    phoneno =  standardKitValidatorVO.getAddressDetailsVO().getPhone();
  }
  if(functions.isValueNull(standardKitValidatorVO.getTransDetailsVO().getCurrency())){
    currencyPg =  standardKitValidatorVO.getTransDetailsVO().getCurrency();
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
    termsLink   = standardKitValidatorVO.getMerchantDetailsVO().getPartnerTcUrl();
    privacyLink = standardKitValidatorVO.getMerchantDetailsVO().getPartnerPrivacyUrl();
    target      = "target=\"_blank\"";

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
    multiLanguage       = request.getHeader("Accept-Language");
    String sLanguage[]  = multiLanguage.split(",");
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

  List<String> cardList   = (List<String>)paymentMap.get(paymodeId);
  String varPersonalinfo  = rb.getString("VAR_PERSONALINFO");
  String varEmailid       = rb.getString("VAR_EMAILID");
  String varPhoneno       = rb.getString("VAR_PHONENO");
  String varNext          = rb.getString("VAR_NEXT");
  String consent1             = rb.getString("VAR_CONSENT_STMT1");
  String consent2             = rb.getString("VAR_CONSENT_STMT2");
  String consent3             = rb.getString("VAR_CONSENT_STMT3");
  String varAND               = rb.getString("VAR_AND");
  String varAccountingnumber  = rb.getString("VAR_ACCOUNTINGNUMBER");
  String varName              = rb.getString("VAR_NAME");
  String varCountry           = rb.getString("VAR_COUNTRY");
  String varIdentifynumber    = rb.getString("VAR_IDENTIFYNUMBER");
  //String varBankName          = rb.getString("VAR_BANKNAME");
%>


<div id="BankTransferOption" class="option-class">
  <ul class="nav nav-tabs" id="BankTransferTab">

    <%

      for(String cardId : cardList)
      {
        String cardName = GatewayAccountService.getCardType(cardId);
        String cardImg  = cardName+".png";
        cardName        = cardName.replaceAll(" ", "_");
        Boolean isSubmit = false;
        if(cardName.equals("Transfr"))
        {
          isSubmit = true;
        }
    %>
    <li class="tabs-li-wallet" onclick="bankTransferGetHideShow('<%=cardName%>','<%=rb.getString(cardName)%>',<%=isSubmit%>)">
      <a href="#<%=cardName%>"  data-toggle="tab" title="<%=cardName%>" aria-expanded="false">
        <img class="images-style" src="/images/merchant/images/bankTransfer/<%=cardImg%>" alt="<%=cardName%>">
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
    if(cardList.contains("81"))
    {
  %>
  <form method="post" name="JPBANKForm" id="JPBANKForm" action="/transaction/SingleCallCheckout">
    <input type="hidden" name="paymentBrand" id="paymentBrand" value="JPBANK">
    <jsp:include page="requestParameters.jsp">
      <jsp:param name="paymentMode" value="<%=payMode%>" />
    </jsp:include>
  </form>


  <%
      cardList.remove("81");
    }
    if(cardList.contains("98"))
    {
  %>
  <!--TELECASH-96-->
  <div class="tab-pane" id="TELECASH">
    <form id="telecashForm" class="form-style" method="post">

      <div class="tab">
        <%--<p class="form-header"><%=varPersonalinfo%></p>--%>

        <div class="form-group has-float-label control-group-full" id="TelecashEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="tcEmail" placeholder=" " onfocusout="validateEmail(event.target.value ,'TelecashEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="tcEmail" class="form-label"><%=varEmailid%></label>
        </div>

        <div class="form-group has-float-label control-group-half" >
          <div class="dropdown">
            <input id="country_input_telecash" class="form-control input-control1" placeholder=" " oninput="this.className = 'form-control input-control1'"
                   onblur="pincodecc('country_input_telecash','country_telecash','phonecc_id_telecash','phonecc_telecash'); " onkeypress="return isLetterKey(event)" />
            <label for="country_input_telecash" class="form-label"><%=varCountry%></label>
            <input type="hidden" id="country_telecash"  name="country_input" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
          </div>
        </div>

        <div class="form-group has-float-label control-group-half" style="width: 10% !important">
          <input type="text" class="form-control input-control1" name="phone-CC" id="phonecc_id_telecash" placeholder=" " onkeypress="return isNumberKey(event)"
                 onkeyup="onPasteNumCheck(event)" maxlength="3" onblur="setPhoneCC('phonecc_id_telecash','phonecc_telecash')" value="<%=phonecc%>"
                 oninput="this.className = 'form-control input-control1'"/>
          <label for="phonecc_id_telecash" class="form-label">CC</label>
          <input type="hidden" id="phonecc_telecash" name="phone-CC" value="<%=phonecc%>"/>
        </div>
        <div class="form-group has-float-label control-group-half" style="width: 3% !important;padding: 6px 0px;">
          -
        </div>
        <div class="form-group has-float-label control-group-half" id="TelecashPhoneNum" style="width: 35% !important">
          <span class="input-icon-half"><i class="fas fa-phone"></i> </span>
          <input type="text" class="form-control input-control1" id="phone_telecash" placeholder=" " onkeypress="return isNumberKey(event)"
                 onkeyup="onPasteNumCheck(event)" oninput="this.className = 'form-control input-control1'" maxlength="24" name="telno" value="<%=phoneno%>"/>
          <label for="phone_telecash" class="form-label"><%=varPhoneno%></label>
        </div>

        <div class="form-group has-float-label control-group-half">
          <input class="form-control input-control1" id="telecashAccNo" placeholder=" " type="text" onkeypress="return isNumberKey(event)"
                 maxlength="17" oninput="this.className = 'form-control input-control1'" name="accountnumber" />
          <label for="telecashAccNo" class="form-label"><%=varAccountingnumber%></label>
        </div>

        <div class="form-group has-float-label control-group-half" id="TelecashName">
          <input type="text" class="form-control input-control1" id="telecashname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'" name="firstname"/>
          <label for="telecashname" class="form-label"><%=varName%></label>
        </div>

        <script>
          setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_telecash');
          pincodecc('country_input_telecash','country_telecash','phonecc_id_telecash','phonecc_telecash');
        </script>

      </div>

      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'telecashForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_telecash" onclick="disablePayButton(this)">
        <label for="TC_telecash" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'telecashForm')">
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
      cardList.remove("98");
    }
    if(cardList.contains("169"))
    {
      System.out.println("Inside 169 -------------------->");
  %>
  <div class="tab-pane" id="PIX">
    <form id="PIXForm" class="form-style" method="post">
      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
        <br>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="PIX_identifynumber" name="number" maxlength="20" placeholder=""  value=""
                 oninput="this.className = 'form-control input-control1'" />
          <label for="PIX_identifynumber" style="color:#757575"><%=varIdentifynumber%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'PIX')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_PIX" onclick="disablePayButton(this)">
        <label for="TC_PIX" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'TC_PIX')">
          <%="Pay"%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="PIX">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("182"))
    {
  %>
  <div class="tab-pane" id="DepositExpress">
    <form id="DepositExpressForm" class="form-style" method="post">
      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
        <br>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="DepositExpress_identifynumber" name="number" maxlength="20" placeholder=""  value=""
                 oninput="this.className = 'form-control input-control1'" />
          <label for="DepositExpress_identifynumber" style="color:#757575"><%=varIdentifynumber%></label>
        </div>
        <div class="form-group has-float-label control-group-full" >
          <select name="bankName" class="form-control input-control1 nbi-dropdow" id="allpayBankName" onchange="validateAllPay()">
            <option value="">Select a Bank Name</option>
            <option value="itau">Ttau</option>
            <option value="santander">Santander</option>
            <option value="bradesco">Bradesco</option>
            <option value="caixa">Caixa</option>
          </select>
          <%--<label for="allpayBankName" style="color:#757575"><%=varBankName%></label>--%>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'DepositExpress')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_DepositExpress" onclick="disablePayButton(this)">
        <label for="TC_DepositExpress" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'TC_DepositExpress')">
          <%="Pay"%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="DepositExpress">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("183"))
    {
  %>
  <div class="tab-pane" id="SPEI">
    <form id="SPEIForm" class="form-style" method="post">
      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
        <br>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="SPEI_identifynumber" name="number" maxlength="20" placeholder=""  value=""
                 oninput="this.className = 'form-control input-control1'" />
          <label for="SPEI_identifynumber" style="color:#757575"><%=varIdentifynumber%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'SPEI')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_SPEI" onclick="disablePayButton(this)">
        <label for="TC_SPEI" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'TC_SPEI')">
          <%="Pay"%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="SPEI">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("184"))
    {
  %>
  <div class="tab-pane" id="CoDi">
    <form id="CoDiForm" class="form-style" method="post">
      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
        <br>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="CoDi_identifynumber" name="number" maxlength="20" placeholder=""  value=""
                 oninput="this.className = 'form-control input-control1'" />
          <label for="CoDi_identifynumber" style="color:#757575"><%=varIdentifynumber%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'CoDi')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_CoDi" onclick="disablePayButton(this)">
        <label for="TC_CoDi" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'TC_CoDi')">
          <%="Pay"%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="CoDi">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("185"))
    {
  %>
  <div class="tab-pane" id="PSE">
    <form id="PSEForm" class="form-style" method="post">
      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
        <br>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="PSE_identifynumber" name="number" maxlength="20" placeholder=""  value=""
                 oninput="this.className = 'form-control input-control1'" />
          <label for="PSE_identifynumber" style="color:#757575"><%=varIdentifynumber%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'PSE')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_PSE" onclick="disablePayButton(this)">
        <label for="TC_PSE" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'TC_PSE')">
          <%="Pay"%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="PSE">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("186"))
    {
  %>
  <div class="tab-pane" id="Khipu">
    <form id="KhipuForm" class="form-style" method="post">
      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
        <br>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="Khipu_identifynumber" name="number" maxlength="20" placeholder=""  value=""
                 oninput="this.className = 'form-control input-control1'" />
          <label for="Khipu_identifynumber" style="color:#757575"><%=varIdentifynumber%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'Khipu')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_Khipu" onclick="disablePayButton(this)">
        <label for="TC_Khipu" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'TC_Khipu')">
          <%="Pay"%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="Khipu">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("194"))
    {
  %>
  <div class="tab-pane" id="BankTransferPG">
    <form id="BankTransferPGForm" class="form-style" method="post">
      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
        <br>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="BankTransferPG_identifynumber" name="number" maxlength="20" placeholder=""  value=""
                 oninput="this.className = 'form-control input-control1'" />
          <label for="BankTransferPG_identifynumber" style="color:#757575"><%=varIdentifynumber%></label>
        </div>
        <% if(currencyPg.equalsIgnoreCase("GTQ")){%>
        <div class="form-group has-float-label control-group-full">
          <select name="bankName" class="form-control input-control1 nbi-dropdow" id="allpayBankName" onchange="validateAllPay()">
            <option value="">Select a Bank Name</option>
            <option value="8404">Caja de Desarrollo</option>
            <option value="8404">Banco de Antigua</option>
            <option value="8404">Super 24</option>
            <option value="8404">AKISI Puedo</option>
            <option value="8404">Fundación Génesis</option>
          </select>
        </div>
        <%}else if(currencyPg.equalsIgnoreCase("CRC")){%>
        <div class="form-group has-float-label control-group-full">
          <select name="bankName" class="form-control input-control1 nbi-dropdow" id="allpayBankName" onchange="validateAllPay()">
            <option value="">Select a Bank Name</option>
            <option value="1021">Banco Nacional de Costa Rica</option>
          </select>
        </div>
        <%}%>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'BankTransferPG')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_BankTransferPG" onclick="disablePayButton(this)">
        <label for="TC_BankTransferPG" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'TC_BankTransferPG')">
          <%="Pay"%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="BankTransferPG">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("187"))
    {
  %>
  <div class="tab-pane" id="Pagpeffectivo">
    <form id="PagpeffectivoForm" class="form-style" method="post">
      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
        <br>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="Pagpeffectivo_identifynumber" name="number" maxlength="20" placeholder=""  value=""
                 oninput="this.className = 'form-control input-control1'" />
          <label for="Pagpeffectivo_identifynumber" style="color:#757575"><%=varIdentifynumber%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'Pagpeffectivo')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_Pagpeffectivo" onclick="disablePayButton(this)">
        <label for="TC_Pagpeffectivo" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'TC_Pagpeffectivo')">
          <%="Pay"%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="Pagpeffectivo">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("203"))
    {
  %>
  <!--Paysec : 14-->
  <div class="tab-pane" id="EFT">
    <form method="post" name="EFTForm" id="EFTForm" action="/transaction/SingleCallCheckout">
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="EFT">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("205"))
    {
  %>
  <!--Paysec : 14-->
  <div class="tab-pane" id="VFD">
    <form method="post" name="VFDForm" id="VFDForm" action="/transaction/SingleCallCheckout">
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="VFD">
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
  <div class="tab-pane" id="ECOCASH">
    <form id="ecocashForm" class="form-style" method="post">

      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>

        <div class="form-group has-float-label control-group-full" id="EcocashEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="ecocashEmail" placeholder=" " onfocusout="validateEmail(event.target.value ,'EcocashEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="ecocashEmail" class="form-label"><%=varEmailid%></label>
        </div>

        <div class="form-group has-float-label control-group-half">
          <div class="dropdown">
            <input id="country_input_ecocash" class="form-control input-control1" placeholder=" " oninput="this.className = 'form-control input-control1'"
                   onblur="pincodecc('country_input_ecocash','country_ecocash','phonecc_id_ecocash','phonecc_ecocash'); " onkeypress="return isLetterKey(event)" />
            <label for="country_input_ecocash" class="form-label"><%=varCountry%></label>
            <input type="hidden" id="country_ecocash"  name="country_input" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
          </div>
        </div>

        <div class="form-group has-float-label control-group-half" style="width: 10% !important">
          <input type="text" class="form-control input-control1" name="phone-CC" id="phonecc_id_ecocash" placeholder=" " onkeypress="return isNumberKey(event)"
                 onkeyup="onPasteNumCheck(event)" maxlength="3" onblur="setPhoneCC('phonecc_id_ecocash','phonecc_ecocash')" value="<%=phonecc%>"
                 oninput="this.className = 'form-control input-control1'"/>
          <label for="phonecc_id_ecocash" class="form-label">CC</label>
          <input type="hidden" id="phonecc_ecocash" name="phone-CC" value="<%=phonecc%>"/>
        </div>
        <div class="form-group has-float-label control-group-half" style="width: 3% !important;padding: 6px 0px;">
          -
        </div>
        <div class="form-group has-float-label control-group-half" id="EcocashPhoneNum" style="width: 35% !important">
          <span class="input-icon-half"><i class="fas fa-phone"></i> </span>
          <input type="text" class="form-control input-control1" id="phone_ecocash" placeholder=" " onkeypress="return isNumberKey(event)"
                 onkeyup="onPasteNumCheck(event)" oninput="this.className = 'form-control input-control1'" maxlength="24" name="telno" value="<%=phoneno%>"/>
          <label for="phone_ecocash" class="form-label"><%=varPhoneno%></label>
        </div>

        <div class="form-group has-float-label control-group-half">
          <input class="form-control input-control1" id="ecocashAccNo" placeholder=" " type="text" onkeypress="return isNumberKey(event)"
                 maxlength="17" oninput="this.className = 'form-control input-control1'" name="accountnumber" />
          <label for="ecocashAccNo" class="form-label"><%=varAccountingnumber%></label>
        </div>

        <div class="form-group has-float-label control-group-half" id="EcocashName">
          <input type="text" class="form-control input-control1" id="ecocashname" placeholder=" " oninput="this.className = 'form-control input-control1'" name="firstname"/>
          <label for="ecocashname" class="form-label"><%=varName%></label>
        </div>

        <script>
          setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_ecocash');
          pincodecc('country_input_ecocash','country_ecocash','phonecc_id_ecocash','phonecc_ecocash');
        </script>

      </div>

      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'ecocashForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_ecocash" onclick="disablePayButton(this)">
        <label for="TC_ecocash" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'ecocashForm')">
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
      cardList.remove("97");
    }

  for(String cardId : cardList)
  {
    if(!cardId.equals("163") || !cardId.equals("169"))
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
    }

    if(cardList.contains("163"))
    {
  %>
    <form method="post" name="TransfrForm" id="TransfrForm" action="/transaction/SingleCallCheckout">
    <input type="hidden" name="paymentBrand" id="paymentBrand" value="Transfr">
    <jsp:include page="requestParameters.jsp">
      <jsp:param name="paymentMode" value="<%=payMode%>" />
    </jsp:include>
    </form>

<%
      cardList.remove("163");
    }
%>
    </div>