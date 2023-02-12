<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>

<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 6/22/2018
  Time: 5:57 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  Functions functions = new Functions();
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  MerchantDetailsVO merchantDetailsVO= standardKitValidatorVO.getMerchantDetailsVO();
  String personalDetailDisplay = merchantDetailsVO.getPersonalInfoDisplay();

  String email      = "";
  String phonecc    = "";
  String phoneno    = "+880";
  String firstName  = "";
  String lastName   = "";
 // String displayMsgEnglish   = "For any issue pls contact support@seversent.com. If your amount deducted and not reflecting in account it wil be reversed in 48 hours";

  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getEmail())){
    email   = ESAPI.encoder().encodeForHTML(standardKitValidatorVO.getAddressDetailsVO().getEmail());
  }
  if (functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getTelnocc()))
  {
    phonecc = standardKitValidatorVO.getAddressDetailsVO().getTelnocc();
  }

  if (functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getPhone()))
  {
    phoneno = standardKitValidatorVO.getAddressDetailsVO().getPhone();
  }
  if (functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getFirstname()))
  {
    firstName = standardKitValidatorVO.getAddressDetailsVO().getFirstname();
  }
  if (functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getLastname()))
  {
    lastName = standardKitValidatorVO.getAddressDetailsVO().getLastname();
  }

  String payMode             = "CR";
  String tabId               = "CajaRural";
  String paymodeId    = "54";

  ResourceBundle rb1    = null;
  String lang           = "";
  String multiLanguage  = "";

  if (functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getLanguage())) {
    lang = standardKitValidatorVO.getAddressDetailsVO().getLanguage().toLowerCase();
    if ("ja".equalsIgnoreCase(lang)) {
      rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ja");
    }
    else if ("bg".equalsIgnoreCase(lang)) {
      rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_bg");
    }
    else if ("ro".equalsIgnoreCase(lang)) {
      rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ro");
    }
    else if ("sp".equalsIgnoreCase(lang)) {
        rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_sp");
    }
    else {
      rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
    }

  }
  else if (functions.isValueNull(request.getHeader("Accept-Language"))) {
    multiLanguage = request.getHeader("Accept-Language");
    String sLanguage[] = multiLanguage.split(",");
    if (functions.isValueNull(sLanguage[0])) {
      if ("ja".equalsIgnoreCase(sLanguage[0])) {
        rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ja");
      }
      else if ("bg".equalsIgnoreCase(sLanguage[0])) {
        rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_bg");
      }
      else if ("ro".equalsIgnoreCase(sLanguage[0])) {
        rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_ro");
      }
      else if ("sp".equalsIgnoreCase(sLanguage[0])) {
          rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_sp");
      }
      else {
        rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
      }
    }
    else {
      rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
    }
  }
  else {
    rb1 = LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_en-us");
  }

  String varEmailid   = rb1.getString("VAR_EMAILID");
  String varCountry   = rb1.getString("VAR_COUNTRY");
  String varPhoneno   = rb1.getString("VAR_PHONENO");
  String varFirstName = rb1.getString("VAR_FIRSTNAME");
  String varLastName  = rb1.getString("VAR_LASTNAME");
  String varNext      = rb1.getString("VAR_NEXT");

  Logger log          = new Logger("CajaRural.jsp");
  HashMap paymentMap  = standardKitValidatorVO.getMapOfPaymentCardType();


  List<String> cardList       = (List<String>)paymentMap.get(paymodeId);
%>

<div class="tab-pane" id="<%=tabId%>" style="height: 305px;" >
  <form id="<%=tabId%>Form" class="form-style" method="post">
      <%
      if("Y".equalsIgnoreCase(personalDetailDisplay)){
    %>
    <div class="tab" id="personalinfo" style="padding-top: 22px;height:auto;overflow:unset">
      <div id="emailPersonal">
        <div class="form-group has-float-label control-group-full" id="NBEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="email_nbi" placeholder=" " onchange="validateEmail(event.target.value ,'NBEmail')"
                 oninput="this.className = 'form-control input-control1'" name="emailaddr" value="<%=email%>" autofocus maxlength="50" autocomplete="off" />
          <label for="email_nbi" class="form-label"><%=varEmailid%></label>
        </div>
      </div>
      <div class="form-group has-float-label control-group-half">
        <input type="text" class="form-control input-control1" id="firstname_nbi" placeholder=" "
               oninput="this.className = 'form-control input-control1'" name="firstname" value="<%=firstName%>" autofocus maxlength="50" autocomplete="off" />
        <label for="firstname_nbi" class="form-label"><%=varFirstName%></label>
      </div>
      <div class="form-group has-float-label control-group-half">
        <input type="text" class="form-control input-control1" id="lastname_nbi" placeholder=" "
               oninput="this.className = 'form-control input-control1'" name="lastname" value="<%=lastName%>" autofocus maxlength="50" autocomplete="off" />
        <label for="lastname_nbi" class="form-label"><%=varLastName%></label>
      </div>
      <div id="mobilePersonal">
        <div class="form-group has-float-label control-group-half" style="width: 10% !important">
          <input type="text" class="form-control input-control1" name="phone-CC" id="phonecc_id_nbi" placeholder=" " onkeypress="return isNumberKey(event)"
                 onkeyup="onPasteNumCheck(event)" maxlength="3" onblur="setPhoneCC('phonecc_id_nbi','phonecc_nbi')" value="<%=phonecc%>"
                 oninput="this.className = 'form-control input-control1'"/>
          <label for="phonecc_id_nbi" class="form-label">CC</label>
          <input type="hidden" id="phonecc_nbi" name="phone-CC" value="<%=phonecc%>"/>
        </div>0
        <div class="form-group has-float-label control-group-half" style="width: 3% !important;padding: 6px 0px;">
          -
        </div>
        <div class="form-group has-float-label control-group-half" id="nbiPhoneNum" style="width: 35% !important">
          <span class="input-icon-half"><i class="fas fa-phone"></i> </span>
          <input type="text" class="form-control input-control1" id="RPphoneNum" placeholder=" " name="telno" value="<%=phoneno%>" maxlength="15"
                 onchange="validatePhoneForAll(event.target.value,'nbiPhoneNum',9,15)"
                 onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" oninput="this.className = 'form-control input-control1'" />
          <label for="RPphoneNum" class="form-label"><%=varPhoneno%></label>
        </div>
      </div>
    </div>
</div>
<%
  }
%>

<div class="tab" style="padding: 0px;height: auto;">
  <ul class="nav nav-tabs" id="CajaRuralTab">
    <%
      for(String cardId : cardList)
      {
        String cardName = GatewayAccountService.getCardType(cardId);
        String cardImg  = cardName+".png";
        cardName        = cardName.replaceAll(" ", "_");
    %>
    <li class="tabs-li-wallet"  onclick="CajaRuralHideShow('<%=cardName%>','<%=rb1.getString(cardName)%>',true)">
      <a href="#<%=cardName%>"  data-toggle="tab" title="<%=cardName%>" aria-expanded="false">
        <img class="images-style" src="/images/merchant/<%=cardImg%>" alt="<%=cardName%>">
        <div class="label-style"><%=cardName%></div>
      </a>
    </li>
    <%
      }
    %>
  </ul>
</div><%--
<div class="form-group " id="terms1">
  <label  style="font-size: 55%;position: absolute;margin: 0px; color:Gray; ">
    <p style="text-align:center;"> <b><%=displayMsgEnglish%></b></p>
  </label>
</div>--%>
<div style="overflow:hidden;" >
  <div style="float:right;">
    <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'<%=tabId%>Form')" >
      <i class="fas fa-angle-left"></i>
    </div>
  </div>
</div>
<div class="pay-btn" >
  <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'<%=tabId%>Form')">
    <%=varNext%>
  </div>
</div>
<%--<input type="hidden" name="country_input" value="IN">
&lt;%&ndash;<input type="hidden" name="emailaddr" value="<%=email%>">&ndash;%&gt;
<jsp:include page="requestParameters.jsp">
  <jsp:param name="paymentMode" value="<%=payMode%>" />
</jsp:include>--%>
</form>

</div>
<div class="tab-content">
  <%
    for(String cardId : cardList)
    {
      String cardName = GatewayAccountService.getCardType(cardId);
      String cn       = cardName;
      cardName        = cardName.replaceAll(" ", "_");
  %>
  <div class="tab-pane" id="<%=cardName%>">
    <form id="<%=cardName%>Form" name="<%=cardName%>Form" class="form-style" method="post" action="/transaction/SingleCallCheckout">
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="CajaRural">
      <%
        if("Y".equalsIgnoreCase(personalDetailDisplay)){
      %>
      <input type="hidden" id="emailaddr_<%=cardName%>" name="emailaddr" value="<%=email%>">
      <input type="hidden" id="firstname_<%=cardName%>" name="firstname" value="<%=firstName%>">
      <input type="hidden" id="lastname_<%=cardName%>" name="lastname" value="<%=lastName%>">
      <input type="hidden" id="phone-CC_<%=cardName%>" name="phone-CC" value="<%=phonecc%>">
      <input type="hidden" id="telno_<%=cardName%>" name="telno" value="<%=phoneno%>">

      <%--<input type="hidden" name="emailaddr" id="emailaddr_BDE" value="<%=email%>">
      <input type="hidden" name="firstname" id="firstname_BDE" value="<%=firstName%>">
      <input type="hidden" name="lastname" id="lastname_BDE" value="<%=lastName%>">
      <input type="hidden" name="phone-CC" id="phone-CC_BDE" value="<%=phonecc%>">
      <input type="hidden" name="telno" id="telno_BDE" value="<%=phoneno%>">--%>
      <%}%>

      <%-- <input type="hidden" name="paymentBrand" id="paymentBrand" value="<%=cn%>">--%>
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
  %>
</div>