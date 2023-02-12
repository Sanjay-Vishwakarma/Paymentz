<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.payment.common.core.CommTransactionDetailsVO" %>

<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 5/24/2018
  Time: 5:39 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  HashMap paymentMap = standardKitValidatorVO.getMapOfPaymentCardType();
  Functions functions = new Functions();

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
    else if("sp".equalsIgnoreCase(lang))
    {
      rb1=LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_sp");
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
  String varCountry=rb1.getString("VAR_COUNTRY");
  String varPhonecc=rb1.getString("VAR_PHONECC");
  String varPhoneno=rb1.getString("VAR_PHONENO");
  String varNext=rb1.getString("VAR_NEXT");
  String varAddressinfo=rb1.getString("VAR_ADDRESSINFO");
  String varAddress=rb1.getString("VAR_ADDRESS");
  String varCity=rb1.getString("VAR_CITY");
  String varZip=rb1.getString("VAR_ZIP");
  String varState=rb1.getString("VAR_STATE");
  String varFirstname=rb1.getString("VAR_FIRSTNAME");
  String varLastname=rb1.getString("VAR_LASTNAME");
  String varAccountingnumber=rb1.getString("VAR_ACCOUNTINGNUMBER");
  String varRoutingnumber=rb1.getString("VAR_ROUTINGNUMBER");
  String varAccounttype=rb1.getString("VAR_ACCOUNTTYPE");
  String consent2 = rb1.getString("VAR_CONSENT_STMT2");
  String consent1 = rb1.getString("VAR_CONSENT_STMT1");
  String consent3 = rb1.getString("VAR_CONSENT_STMT3");
  String varAND = rb1.getString("VAR_AND");
  String varBankName = rb1.getString("VAR_BANK_NAME");

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
%>

<div id="NetBankingOption" class="option-class">
  <ul class="nav nav-tabs" id="NetBankingTab">
    <%
      String paymodeId = "5";
      String payMode = "NB";
      List<String> cardList = (List<String>)paymentMap.get(paymodeId);
      for(String cardId : cardList)
      {
        String cardName = GatewayAccountService.getCardType(cardId);

        cardName = cardName.replaceAll(" ", "_");
        String cardImg = cardName+".png";

    %>
    <li class="tabs-li-wallet" onclick="netBankingHideShow('<%=cardName%>','<%=rb1.getString(cardName)%>')">
      <a href="#<%=cardName%>"  data-toggle="tab" title="<%=cardName%>" aria-expanded="false">
        <img class="images-style" src="/images/merchant/images/netbanking/<%=cardImg%>" alt="<%=cardName%>">
        <div class="label-style"><%=rb1.getString(cardName)%></div>
      </a>
    </li>
    <%
      }
    %>


 <%--   <li class="tabs-li-wallet" onclick="netBankingHideShow('AllPay88')">
      <a href="#AllPay88"  data-toggle="tab" title="AllPay88" aria-expanded="false">
        <img class="images-style" src="/images/merchant/images/wallet/AllPay88" alt="AllPay88">
        <div class="label-style">AllPay88</div>
      </a>
    </li>--%>

  </ul>
</div>

<div class="tab-content">
  <%
    if(cardList.contains("10"))
    {
  %>
  <!--Sofort : 10-->
  <div class="tab-pane" id="Sofort">
    <form id="sofortForm" class="form-style" method="post">
      <div class="tab"><p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-full" id="SofortEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="sofortemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'SofortEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="sofortemail" class="form-label"><%=varEmailid%></label>
        </div>
        <%--<div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phoneno" placeholder=" " onkeypress="return isNumberKey(event)"
                 oninput="this.className = 'form-control input-control1'"  name="telno" />
          <label for="phoneno" class="form-label"><%=varPhoneno%></label>
        </div>--%>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phone-CC" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" maxlength="3"
                 oninput="this.className = 'form-control input-control1'"  name="telno" value="<%=phonecc%>"/>
          <label for="phone-CC" class="form-label"><%=varPhonecc%></label>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_sofort" onclick="disablePayButton(this)">
        <label for="TC_sofort" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'sofortForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>

      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1,'sofortForm')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="Sofort">

      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("11"))
    {
  %>
  <!--ideal : 11-->
  <div class="tab-pane" id="Ideal">
    <form id="idealForm" class="form-style">
      <div class="tab"><p class="form-header">Bank</p>
        <div class="form-group has-float-label control-group-full" >
          <select class="form-control input-control1" id="idealBankName">
            <option value="HDFC" >HDFC</option>
            <option value="ICICI" >ICICI</option>
            <option value="BOI">Bank of India</option>
          </select>
          <label for="idealBankName" class="form-label">Bank Name</label>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_ideal" onclick="disablePayButton(this)">
        <label for="TC_ideal" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'idealForm')" >
            <i class="fas fa-angle-left"></i>
          </div>

        </div>
      </div>

      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1,'idealForm')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="Ideal">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
  %>
  <%--<div class="tab-pane" id="ACH">
    <!--ACH : 13-->
    <form id="achForm" class="form-style">
      <div class="tab"><p class="form-header"><%=varCardinfo%></p>
        <div class="form-group has-float-label control-group-full" >
          <input class="form-control input-control1" id="achAccNo" placeholder=" " type="text" onkeypress="return isNumberKey(event)"
                 maxlength="17" oninput="this.className = 'form-control input-control1'" name="accountnumber" />
          <label for="achAccNo" class="form-label"><%=varAccountingnumber%></label>
        </div>
        <div class="form-group has-float-label control-group-full" >
          <input class="form-control input-control1" id="achRoutNo" placeholder=" " type="text" onkeypress="return isNumberKey(event)"
                 maxlength="15" oninput="this.className = 'form-control input-control1'" name="routingnumber" />
          <label for="achRoutNo" class="form-label"><%=varRoutingnumber%></label>
        </div>
        <div class="form-group has-float-label control-group-full" >
          <select class="form-control input-control1" name="accountType" id="achAccType">
            <option>Personal Checking</option>
            <option>Personal Savings</option>
            <option>Commercial Checking</option>
          </select>
          <label for="achAccType" class="form-label"><%=varAccounttype%></label>
        </div>
      </div>
      <div class="tab"><p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="achfirstname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="achfirstname" class="form-label"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="achlastname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="lastname" />
          <label for="achlastname" class="form-label"><%=varLastname%></label>
        </div>
      </div>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC" >
        <label for="TC" class="form-label" style="font-size: 85%;position: absolute;margin: 0px;">
          &nbsp; I confirm that I’ve read and agreed to the <b> Privacy Policy </b> and <b>  Terms of Use </b>.
        </label>
      </div>

      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'achForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>

      <div class="pay-btn" >
        <!-- <div class="pay-button hide" onclick="pay()" id="pay_button">
            <span>PAY  ₹ 1</span>
        </div> Next button -->
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1,'achForm')">
          <%=varNext%>
        </div>
      </div>
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymode" value="<%=payMode%>" />
        <jsp:param name="paymentBrand" value="13" />
      </jsp:include>
    </form>
  </div>


  <div class="tab-pane" id="chk">
    <!--CHK : 15-->
    <form id="chkForm" class="form-style">
      <div class="tab"><p class="form-header"><%=varCardinfo%></p>
        <div class="form-group has-float-label control-group-full" >
          <input class="form-control input-control1" id="chkAccNo" placeholder=" " type="text" onkeypress="return isNumberKey(event)"
                 maxlength="17" oninput="this.className = 'form-control input-control1'" name="accountnumber" />
          <label for="chkAccNo" class="form-label"><%=varAccountingnumber%></label>
        </div>
        <div class="form-group has-float-label control-group-full" >
          <input class="form-control input-control1" id="chkRoutNo" placeholder=" " type="text" onkeypress="return isNumberKey(event)"
                 maxlength="15" oninput="this.className = 'form-control input-control1'" name="routingnumber" />
          <label for="chkRoutNo" class="form-label"><%=varRoutingnumber%></label>
        </div>
        <div class="form-group has-float-label control-group-full" >
          <select class="form-control input-control1" name="accountType" id="chkAccType">
            <option>Personal Checking</option>
            <option>Personal Savings</option>
            <option>Commercial Checking</option>
          </select>
          <label for="chkAccType" class="form-label"><%=varAccounttype%></label>
        </div>
      </div>
      <div class="tab"><p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="chkfirstname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="chkfirstname" class="form-label"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="chklastname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="lastname" />
          <label for="chklastname" class="form-label"><%=varLastname%></label>
        </div>
      </div>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC" >
        <label for="TC" class="form-label" style="font-size: 85%;position: absolute;margin: 0px;">
          &nbsp; I confirm that I’ve read and agreed to the <b> Privacy Policy </b> and <b> Terms of Use </b>.
        </label>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'chkForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>

      <div class="pay-btn" >
        <!-- <div class="pay-button hide" onclick="pay()" id="pay_button">
            <span>PAY  ₹ 1</span>
        </div> Next button -->
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1,'chkForm')">
          <%=varNext%>
        </div>
      </div>
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymode" value="<%=payMode%>" />
        <jsp:param name="paymentBrand" value="15" />
      </jsp:include>
    </form>
  </div>--%>

  <%
    if(cardList.contains("35"))
    {
  %>
  <!--Trustly : 35-->
  <div class="tab-pane" id="TRUSTLY">
    <form id="trustlyForm" class="form-style" method="post">
      <div class="tab"><p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="trustlyfirstname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="trustlyfirstname" class="form-label"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="trustlylastname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="lastname" />
          <label for="trustlylastname" class="form-label"><%=varLastname%></label>
        </div>
        <div class="form-group has-float-label control-group-full" id="TrustlyEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="trustlyemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'TrustlyEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="trustlyemail" class="form-label"><%=varEmailid%></label>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox  hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_trustly" onclick="disablePayButton(this)">
        <label for="TC_trustly" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'trustlyForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>

      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1,'trustlyForm')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="TRUSTLY">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("19"))
    {
  %>
  <!--Giropay : 19-->
  <div class="tab-pane" id="GIROPAY">

    <form id="giropayForm" class="form-style" method="post">
      <div class="tab"><p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="giropayfirstname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="giropayfirstname" class="form-label"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="giropaylastname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="lastname" />
          <label for="giropaylastname" class="form-label"><%=varLastname%></label>
        </div>
        <div class="form-group has-float-label control-group-full" id="GiroPayEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="giropayemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'GiroPayEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="giropayemail" class="form-label"><%=varEmailid%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="giropayphone-CC" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"  maxlength="3"
                 oninput="this.className = 'form-control input-control1'"  name="telnocc" value="<%=phonecc%>"/>
          <label for="giropayphone-CC" class="form-label"><%=varPhonecc%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="giropayphoneno" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'"  name="telno" value="<%=phoneno%>"/>
          <label for="giropayphoneno" class="form-label"><%=varPhoneno%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'giropayForm')" >
            <i class="fas fa-angle-left"></i>
          </div>

        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_giropay" onclick="disablePayButton(this)">
        <label for="TC_giropay" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1,'giropayForm')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="GIROPAY">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("40"))
    {
  %>
  <!--Aldrapay : 40-->
  <div class="tab-pane" id="ALDRAPAY">

    <form id="aldrapayForm" class="form-style" method="post">
      <div class="tab"><p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="aldrapayfirstname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="aldrapayfirstname" class="form-label"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="aldrapaylastname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="lastname" />
          <label for="aldrapaylastname" class="form-label"><%=varLastname%></label>
        </div>
        <div class="form-group has-float-label control-group-full" id="AldrapayEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="aldrapayemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'AldrapayEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="aldrapayemail" class="form-label"><%=varEmailid%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="aldrapayphone-CC" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" maxlength="3"
                 oninput="this.className = 'form-control input-control1'"  name="telnocc" value="<%=phonecc%>" />
          <label for="aldrapayphone-CC" class="form-label"><%=varPhonecc%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="aldrapayphoneno" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'"  name="telno" value="<%=phoneno%>" />
          <label for="aldrapayphoneno" class="form-label"><%=varPhoneno%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'aldrapayForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_aldrapay" onclick="disablePayButton(this)">
        <label for="TC_aldrapay" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1,'aldrapayForm')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="ALDRAPAY">

      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("9"))
    {
  %>
  <!--Inpay : 9-->
  <div class="tab-pane" id="InPay">
    <form id="inpayForm" class="form-style" method="post">
      <div class="tab"> <p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="inpayfirstname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="inpayfirstname" class="form-label"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="inpaylastname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="lastname" />
          <label for="inpaylastname" class="form-label"><%=varLastname%></label>
        </div>
        <div class="form-group has-float-label control-group-full" id="InpayEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="inpayemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'InpayEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="inpayemail" class="form-label"><%=varEmailid%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <div class="dropdown">
            <input id="country_input4" class="form-control input-control1" placeholder=" "  onblur="pincodecc('country_input4','country4','phonecc-id4','phonecc4'); "
                   onkeypress="return isLetterKey(event)" onfocusout="StateLabel('country_input4','country4','inpayStateLabel')" onchange="hideCCLabel('hideCCinPay',event)"
                   oninput="this.className = 'form-control input-control1'"  />
            <label for="country_input4" class="form-label"><%=varCountry%></label>
            <input type="hidden" id="country4"  name="country_input" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
          </div>
        </div>
        <div class="form-group has-float-label control-group-half" id="hideCCinPay" style="width: 10% !important">
          <input type="text" class="form-control input-control1" id="phonecc-id4" placeholder=" " onblur ="setPhoneCC('phonecc-id4','phonecc4')"
                 oninput="this.className = 'form-control input-control1'" value="<%=phonecc%>" />
          <label for="phonecc-id4 " style="color:#757575">CC</label>
          <input type="hidden" id="phonecc4"  name="telnocc" value="<%=standardKitValidatorVO.getAddressDetailsVO().getTelnocc()%>" />
        </div>
        <div class="form-group has-float-label control-group-half" style="width: 2% !important;padding: 6px 0px;">
          -
        </div>
        <div class="form-group has-float-label control-group-half" style="width: 35% !important">
          <input type="text" class="form-control input-control1" id="phonenophonecc4" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'"  name="telno" value="<%=phoneno%>" />
          <label for="phonenophonecc4" class="form-label"><%=varPhoneno%></label>
        </div>
      </div>

      <div class="tab"> <p class="form-header"><%=varAddressinfo%></p>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="inpayaddress" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="street" value="<%=street%>" />
          <label for="inpayaddress" class="form-label"><%=varAddress%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="inpaycity" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="city" value="<%=city%>"/>
          <label for="inpaycity" class="form-label"><%=varCity%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <input type="text" class="form-control input-control1" id="inpayzip" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'" name="zip" value="<%=zip%>" />
          <label for="inpayzip" class="form-label"><%=varZip%></label>
        </div>

        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="inpaystate" placeholder=" "
                 oninput="this.className = 'form-control input-control1'" name="state" value="<%=state%>" />
          <label for="inpaystate" id="inpayStateLabel" class="form-label"><%=varState%></label>
        </div>
        <script>
          setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input4');
          pincodecc('country_input4','country4','phonecc-id4','phonecc4');
          StateLabel('country_input4','country4','inpayStateLabel')
        </script>
      </div>

      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'inpayForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_inpay" onclick="disablePayButton(this)">
        <label for="TC_inpay" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'inpayForm')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="InPay">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("14"))
    {
  %>
  <!--Paysec : 14-->
  <div class="tab-pane" id="PaySec">
    <form id="paysecForm" class="form-style" method="post">
      <div class="tab"> <p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="paysecfirstname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="paysecfirstname" class="form-label"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="payseclastname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="lastname" />
          <label for="payseclastname" class="form-label"><%=varLastname%></label>
        </div>
        <div class="form-group has-float-label control-group-full" id="PaysecEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="paysecemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'PaysecEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" value="<%=email%>" />
          <label for="paysecemail" class="form-label"><%=varEmailid%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <div class="dropdown">
            <input id="country_input5" class="form-control input-control1" placeholder=" "  onblur="pincodecc('country_input5','country5','phonecc-id5','phonecc5');"
                   oninput="this.className = 'form-control input-control1'" onchange="hideCCLabel('hideCCPaysec',event)"
                   onfocusout="StateLabel('country_input5','country5','paysecstateLabel')"  onkeypress="return isLetterKey(event)"  >
            <label for="country_input5" class="form-label"><%=varCountry%></label>
            <input type="hidden" id="country5"  name="country_input" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
          </div>
        </div>
        <div class="form-group has-float-label control-group-half" id="hideCCPaysec" style="width: 10% !important">
          <input type="text" class="form-control input-control1" id="phonecc-id5" placeholder=" " onblur ="setPhoneCC('phonecc-id5','phonecc5')"
                 oninput="this.className = 'form-control input-control1'" value="<%=phonecc%>" />
          <label for="phonecc-id5" style="color:#757575">CC</label>
          <input type="hidden" id="phonecc5"  name="telnocc" value="<%=phonecc%>">
        </div>
        <div class="form-group has-float-label control-group-half" style="width: 2% !important;padding: 6px 0px;">
          -
        </div>
        <div class="form-group has-float-label control-group-half" style="width: 35% !important">
          <input type="text" class="form-control input-control1" id="phonenophonecc5" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'"  name="telno" value="<%=phoneno%>" />
          <label for="phonenophonecc5" class="form-label"><%=varPhoneno%></label>
        </div>
      </div>

      <div class="tab"> <p class="form-header"><%=varAddressinfo%></p>
        <div class="form-group has-float-label control-group-full">
          <input type="text" class="form-control input-control1" id="paysecaddress" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="street" value="<%=street%>" />
          <label for="paysecaddress" class="form-label"><%=varAddressinfo%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="payseccity" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="payseccity" value="<%=city%>" />
          <label for="payseccity" class="form-label"><%=varCity%></label>
        </div>
        <div class="form-group has-float-label control-group-half" >
          <input type="text" class="form-control input-control1" id="payseczip" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'" name="zip" value="<%=zip%>"/>
          <label for="payseczip" class="form-label"><%=varZip%></label>
        </div>

        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="paysecstate" placeholder=" "
                 oninput="this.className = 'form-control input-control1'" name="state" value="<%=state%>" />
          <label for="paysecstate" id="paysecstateLabel" class="form-label"><%=varState%></label>
        </div>
        <script>
          setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input5');
          pincodecc('country_input5','country5','phonecc-id5','phonecc5');
          StateLabel('country_input5','country5','paysecstateLabel');
        </script>
      </div>

      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'paysecForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_paysec" onclick="disablePayButton(this)">
        <label for="TC_paysec" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'paysecForm')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="PaySec">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
    if(cardList.contains("170") )
    {
  %>
  <!--Paysec : 14-->
  <div class="tab-pane" id="Internet_Banking">
    <form id="Internet_BankingForm" class="form-style" method="post">
    <%
      List<CommTransactionDetailsVO> bankCodeList = new ArrayList();

      if(request.getAttribute("bankcodeList") != null){
        bankCodeList = (List<CommTransactionDetailsVO>) request.getAttribute("bankcodeList");
      }

    %>
    <div class="tab">
      <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>

      <div class="form-group has-float-label control-group-full" id="Internet_Banking_DropDown">
        <input type="hidden" class="" name="Internet_BankingFormBankCode" id="Internet_BankingFormBankCode" value="">
        <select name="bankCode" class="form-control input-control1 nbi-dropdown" id="Internet_BankingOption" onchange="BankES('Internet_BankingOption','Internet_BankingFormBankCode');" style="margin: -2px 0; " >
          <option value="">Select Bank</option>
          <%
            for(CommTransactionDetailsVO banCode: bankCodeList)
            {
          %>
          <option value="<%=banCode.getBankAccountNo()+"_"+banCode.getCustomerBankAccountName()%>"><%=banCode.getCustomerBankAccountName()%></option>
          <%}%>
        </select>
      </div>
    </div>

    <div style="overflow:hidden;" >
      <div style="float:right;">
        <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'Internet_BankingForm')" >
          <i class="fas fa-angle-left"></i>
        </div>
      </div>
    </div>
    <%
      if(consentFlag.equalsIgnoreCase("Y"))
      {
    %>
    <div class="form-group terms-checkbox hide" id="terms">
      <input type="checkbox" class="" style="width: 5%" id="TC_Internet_Banking" onclick="disablePayButton(this)">
      <label for="TC_Internet_Banking" style="font-size: 85%;position: absolute;margin: 0px;">
        <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
      </label>
    </div>
    <%
      }
    %>
    <div class="pay-btn" >
      <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'Internet_BankingForm')">
        <%=varNext%>
      </div>
    </div>
    <input type="hidden" name="paymentBrand" id="paymentBrand" value="Internet Banking">
    <jsp:include page="requestParameters.jsp">
      <jsp:param name="paymentMode" value="<%=payMode%>" />
    </jsp:include>

  </form>
</div>

  <%
    }
    if(cardList.contains("171") )
    {
  %>
  <!--Paysec : 14-->
  <div class="tab-pane" id="Bank_Transfer">
    <form id="Bank_TransferForm" class="form-style" method="post">
      <%
        List<CommTransactionDetailsVO> bankCodeList = new ArrayList();

        if(request.getAttribute("bankcodeList") != null){
          bankCodeList = (List<CommTransactionDetailsVO>) request.getAttribute("bankcodeList");
        }

      %>
      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>

        <div class="form-group has-float-label control-group-full" id="Bank_Transfer_DropDown">
          <input type="hidden" class="" name="Bank_TransferFormBankCode" id="Bank_TransferFormBankCode" value="">
          <select name="bankCode" class="form-control input-control1 nbi-dropdown" id="Bank_TransferFormOption" onchange="BankES('Bank_TransferFormOption','Bank_TransferFormBankCode');" style="margin: -2px 0; " >
            <option value="">Select Bank</option>
            <%
              for(CommTransactionDetailsVO banCode: bankCodeList)
              {
            %>
            <option value="<%=banCode.getBankAccountNo()+"_"+banCode.getCustomerBankAccountName()%>"><%=banCode.getCustomerBankAccountName()%></option>
            <%}%>
          </select>
        </div>
      </div>

      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'Bank_TransferForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <%
        if(consentFlag.equalsIgnoreCase("Y"))
        {
      %>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC_Bank_Transfer" onclick="disablePayButton(this)">
        <label for="TC_Bank_Transfer" style="font-size: 85%;position: absolute;margin: 0px;">
          <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
        </label>
      </div>
      <%
        }
      %>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'Bank_TransferForm')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="Bank Transfer">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>

    </form>
  </div>

  <%
    }
    if(cardList.contains("191"))
    {
  %>
  <!--Paysec : 14-->
  <div class="tab-pane" id="VirtualAccount">
    <form method="post" name="VirtualAccountForm" id="VirtualAccountForm" action="/transaction/SingleCallCheckout">
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="VirtualAccount">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
  %>



  <%-- AllPay88--%>
  <div class="tab-pane" id="AllPay88">
    <form id="allpayForm" class="form-style" method="post">
      <div class="tab" >
        <div class="form-group has-float-label control-group-bank" >
          <select name="bankName" class="form-control input-control1" id="allpayBankName" onchange="validateAllPay()">
            <option value="">Select a Bank Name</option>
            <option value="ICBC">Industrial and Commercial Bank of China</option>
            <option value="ABC">Agricultural Bank of China</option>
            <option value="CCB">China Construction Bank</option>
            <option value="SPDB">Shanghai Pudong Development Bank</option>
            <option value="CIB">Industrial Bank</option>
            <option value="CMBC">China Minsheng Bank</option>
            <option value="BCM">Bank of Communication</option>
            <option value="CNCB">China CITIC Bank</option>
            <option value="CEB">China Everbright Bank</option>
            <option value="CMB">China Merchants Bank</option>
            <option value="GDB">China Guangfa Bank</option>
            <option value="BOC">Bank of China</option>
            <option value="HXB">Hua Xia Bank</option>
            <option value="PAB">Ping An Bank</option>
            <option value="PSBC">Postal Savings Bank of China</option>
            <option value="SDB">Shenzhen Development Bank</option>
            <option value="RCC">Rural Credit Cooperatives</option>
            <option value="BCCB">Bank of Beijing</option>
            <option value="SHB">Bank of Shanghai</option>
          </select>
          <label for="allpayBankName" style="color:#757575"><%=varBankName%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'epayForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>

      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'allpayForm')">
          <%=varNext%>
        </div>
      </div>
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="AllPay88">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
        <jsp:param name="paymentBrand" value="AllPay88" />
      </jsp:include>

    </form>
  </div>
    <script type="text/javascript">
     function BankES(dropDownId,inputID)
      {
        console.log("dropDownId >> "+dropDownId)
        console.log("inputID >> "+inputID)

        /*var select = document.getElementById(dropDownId);

        if (select.selectedIndex)
        {
          $("#paybutton").removeClass("disabledbutton");
        }
        else
        {
          $("#paybutton").addClass("disabledbutton");

        }*/

        document.getElementById(inputID).value = document.getElementById(dropDownId).value;
      }
      </script>
</div>