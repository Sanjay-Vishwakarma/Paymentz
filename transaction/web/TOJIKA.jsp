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
  Date: 25/03/2018
  Time: 5:29 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>


<%

  String ctoken = "";
  ctoken = (String) session.getAttribute("ctoken");
  System.out.println("ctoken in JSP---"+ctoken);
  Functions functions = new Functions();

  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  String merchantLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo();
  String partnerLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
  String consentFlag = standardKitValidatorVO.getMerchantDetailsVO().getConsentFlag();

  String termsLink = "";
  String privacyLink = "";
  String target = "";

  String email ="";
  String street = "";
  String city = "";
  String zip = "";
  String firstName="";
  String lastName="";

  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getFirstname())){
    firstName = ESAPI.encoder().encodeForHTML(standardKitValidatorVO.getAddressDetailsVO().getFirstname());
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getLastname())){
    lastName = ESAPI.encoder().encodeForHTML(standardKitValidatorVO.getAddressDetailsVO().getLastname());
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getEmail())){
    email = ESAPI.encoder().encodeForHTML(standardKitValidatorVO.getAddressDetailsVO().getEmail());
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getStreet())){
    street =  standardKitValidatorVO.getAddressDetailsVO().getStreet();
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getCity())){
    city =  standardKitValidatorVO.getAddressDetailsVO().getCity();
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
  String varFirstname=rb1.getString("VAR_FIRSTNAME");
  String varLastname=rb1.getString("VAR_LASTNAME");
  String varCountry=rb1.getString("VAR_COUNTRY");
  String varNext=rb1.getString("VAR_NEXT");
  String varAddress=rb1.getString("VAR_ADDRESS");
  String varCity=rb1.getString("VAR_CITY");
  String varZip=rb1.getString("VAR_ZIP");
  String consent1 = rb1.getString("VAR_CONSENT_STMT1");
  String consent2 = rb1.getString("VAR_CONSENT_STMT2");
  String consent3 = rb1.getString("VAR_CONSENT_STMT3");
  String varAND=rb1.getString("VAR_AND");

  String id = request.getParameter("id");
  if(functions.isValueNull(id)){
    paymodeId = "4";
  }

%>

<%--<div id="CreditCardsOption" class="option-class">
  <ul class="nav nav-tabs" id="CardTab">--%>
<%--<li class="tabs-li-wallet" onclick="cardHideShow('Visa Gold')">
    <a href="#card"  data-toggle="tab" title="profile" aria-expanded="false">
      <img class="images-style" src="/images/merchant/images/card/VISA_GOLD.png">
      <div class="label-style"><%=varVisagold%></div>
    </a>
  </li>
 --%>

<%--
</ul>
</div>
--%>



<%--<div class="tab-content">--%>
<div class="tab-pane" id="TOJIKA">
  <form id="tojikaForm" class="form-style" method="post" >
    <div class="tab" id="personalinfo" style="padding-top: 22px;"> <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
      <div class="form-group has-float-label control-group-half">
        <input type="text" class="form-control input-control1" id="tojikafirstname" placeholder=" "
               oninput="this.className = 'form-control input-control1'"  name="firstname" value="<%=firstName%>"  />
        <label for="tojikafirstname" class="form-label"><%=varFirstname%></label>
      </div>
      <div class="form-group has-float-label control-group-half">
        <input type="text" class="form-control input-control1" id="tojikalastname" placeholder=" "
               oninput="this.className = 'form-control input-control1'"  name="lastname" value="<%=lastName%>" />
        <label for="tojikalastname" class="form-label"><%=varLastname%></label>
      </div>
      <div class="form-group has-float-label control-group-full" id="TojikaEmail">
        <span class="input-icon"><i class="far fa-envelope"></i></span>
        <input type="email" class="form-control input-control1" id="email" placeholder=" " onfocusout="validateEmail(event.target.value ,'TojikaEmail')"
               oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" autofocus />
        <label for="email" class="form-label"><%=varEmailid%></label>
      </div>

      <div class="form-group has-float-label control-group-full" id="TojikaAddress">
        <input type="text" class="form-control input-control1" id="address" placeholder=" " onfocusout="validateAddress(event.target.value ,'TojikaAddress')"
               oninput="this.className = 'form-control input-control1'" name="street" value="<%=street%>"/>
        <label for="address" class="form-label"><%=varAddress%></label>
      </div>
      <div class="form-group has-float-label control-group-half" id="TojikaCity" >
        <input type="text" class="form-control input-control1" id="city" placeholder=" " onfocusout="validateCity(event.target.value ,'TojikaCity')"
               oninput="this.className = 'form-control input-control1'" name="city" value="<%=city%>"/>
        <label for="city" class="form-label"><%=varCity%></label>
      </div>
      <div class="form-group has-float-label control-group-half" id="TojikaZip" >
        <input type="text" class="form-control input-control1" id="zip" placeholder=" " onfocusout="validateZip(event.target.value ,'TojikaZip')"
               oninput="this.className = 'form-control input-control1'" name="zip" value="<%=zip%>"/>
        <label for="zip" class="form-label"><%=varZip%></label>
      </div>
      <div class="form-group has-float-label control-group-half" id="TojikaCountry">
        <div class="dropdown">
          <input id="country_input_tojika" class="form-control input-control1"  placeholder=" " onfocusout="validateCountry(event.target.value ,'TojikaCountry')"
                 onblur="pincodecc('country_input_tojika','country_tojika');" onkeypress="return isLetterKey(event)" oninput="this.className = 'form-control input-control1'" />
          <label for="country_input_tojika" class="form-label"><%=varCountry%></label>
          <input type="hidden" id="country_tojika"  name="country_input" >
        </div>
      </div>

      <script>
        pincodecc('country_input_tojika','country_tojika');
        setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_tojika');
        validateCountry(document.getElementById('country_input_tojika').value ,'TojikaCountry');
      </script>

    </div>


    <%
      if(consentFlag.equalsIgnoreCase("Y"))
      {
    %>
    <div class="form-group terms-checkbox hide" id="terms">
      <input type="checkbox" class="" style="width: 5%" id="TC_tojika" onclick="disablePayButton(this)" >
      <label for="TC_tojika" style="font-size: 85%;position: absolute;margin: 0px;">
        <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
      </label>
    </div>
    <%
      }
    %>
    <div style="overflow:hidden" >
      <div style="float:right;"> <!-- Previous button-->
        <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'tojikaForm')">
          <i class="fas fa-angle-left"></i>
        </div>
      </div>
    </div>

    <div class="pay-btn">
      <button type="button" class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1,'tojikaForm')"  tabindex="0">
        <%=varNext%>
      </button>
    </div>
    <input type="hidden" name="paymentBrand" id="paymentBrand" value="TOJIKA">
    <jsp:include page="requestParameters.jsp">
      <jsp:param name="paymentMode" value="TP" />
    </jsp:include>

  </form>
</div>
<%--</div>--%>