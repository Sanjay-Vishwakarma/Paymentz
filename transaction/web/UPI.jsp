<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 7/6/2018
  Time: 6:16 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  Functions functions = new Functions();
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  MerchantDetailsVO merchantDetailsVO= standardKitValidatorVO.getMerchantDetailsVO();
  String personalDetailDisplay = merchantDetailsVO.getPersonalInfoDisplay();

  String email = "";
  String phonecc = "";
  String phoneno = "";
  String firstName = "";
  String lastName = "";

  if (functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getEmail()))
  {
    email = ESAPI.encoder().encodeForHTML(standardKitValidatorVO.getAddressDetailsVO().getEmail());
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
  

  ResourceBundle rb1 = null;
  String lang = "";
  String multiLanguage = "";

  if (functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getLanguage()))
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
  else if (functions.isValueNull(request.getHeader("Accept-Language")))
  {
    multiLanguage = request.getHeader("Accept-Language");
    String sLanguage[] = multiLanguage.split(",");
    if (functions.isValueNull(sLanguage[0]))
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

  String varEmailid = rb1.getString("VAR_EMAILID");
  String varCountry = rb1.getString("VAR_COUNTRY");
  String varPhoneno = rb1.getString("VAR_PHONENO");
  String varFirstName = rb1.getString("VAR_FIRSTNAME");
  String varLastName = rb1.getString("VAR_LASTNAME");
  String varNext = rb1.getString("VAR_NEXT");
%>

<div class="tab-pane" id="UPI" style="height: 305px;" >
  <form id="upiForm" class="form-style" method="POST" autocomplete="off">
    <%
        if("Y".equalsIgnoreCase(personalDetailDisplay)){
    %>
    <div class="tab" id="personalinfo" style="padding-top: 22px;height:auto;overflow:unset">
      <div id="emailPersonal">
      <div class="form-group has-float-label control-group-full" id="UPIEmail">
        <span class="input-icon"><i class="far fa-envelope"></i></span>
        <input type="email" class="form-control input-control1" id="email_upi" placeholder=" " onchange="validateEmail(event.target.value ,'UPIEmail')"
               oninput="this.className = 'form-control input-control1'" name="emailaddr" value="<%=email%>" autofocus maxlength="50" autocomplete="off" />
        <label for="email_upi" class="form-label"><%=varEmailid%></label>
      </div>
      </div>
      <div class="form-group has-float-label control-group-half">
        <input type="text" class="form-control input-control1" id="firstname_upi" placeholder=" "
               oninput="this.className = 'form-control input-control1'" name="firstname" value="<%=firstName%>" autofocus maxlength="50" autocomplete="off" />
        <label for="firstname_upi" class="form-label"><%=varFirstName%></label>
      </div>
      <div class="form-group has-float-label control-group-half">
        <input type="text" class="form-control input-control1" id="lastname_upi" placeholder=" "
               oninput="this.className = 'form-control input-control1'" name="lastname" value="<%=lastName%>" autofocus maxlength="50" autocomplete="off" />
        <label for="lastname_upi" class="form-label"><%=varLastName%></label>
      </div>
<%--      <div class="form-group has-float-label control-group-half" >
        <div class="dropdown">
          <input id="country_input_upi" class="form-control input-control1" placeholder=" " oninput="this.className = 'form-control input-control1'"
                 onblur="pincodecc('country_input_upi','country_upi','phonecc_id_upi','phonecc_upi'); " onkeypress="return isLetterKey(event)" />
          <label for="country_input_upi" class="form-label"><%=varCountry%></label>
          <input type="hidden" id="country_upi"  name="country_input" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
        </div>
      </div>--%>
      <div id="mobilePersonal">
      <div class="form-group has-float-label control-group-half" style="width: 10% !important">
        <input type="text" class="form-control input-control1" name="phone-CC" id="phonecc_id_upi" placeholder=" " onkeypress="return isNumberKey(event)"
               onkeyup="onPasteNumCheck(event)" maxlength="3" onblur="setPhoneCC('phonecc_id_upi','phonecc_upi')" value="<%=phonecc%>"
               oninput="this.className = 'form-control input-control1'"/>
        <label for="phonecc_id_upi" class="form-label">CC</label>
        <input type="hidden" id="phonecc_upi" name="phone-CC" value="<%=phonecc%>"/>
      </div>
      <div class="form-group has-float-label control-group-half" style="width: 3% !important;padding: 6px 0px;">
        -
      </div>
      <div class="form-group has-float-label control-group-half" id="upiPhoneNum" style="width: 35% !important">
        <span class="input-icon-half"><i class="fas fa-phone"></i> </span>
        <input type="text" class="form-control input-control1" id="INphoneNum_upi" placeholder=" " name="telno" value="<%=phoneno%>" maxlength="10"
               onchange="validateInPhone(event.target.value,'upiPhoneNum')"
               onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" oninput="this.className = 'form-control input-control1'" />
        <label for="INphoneNum_upi" class="form-label"><%=varPhoneno%></label>
      </div>
      </div>

<%--      <script>
        setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_upi');
        pincodecc('country_input_upi','country_upi','phonecc_id_upi','phonecc_upi');
      </script>--%>
    </div>
    <%
      }
    %>

    
    <div class="tab" style="height: 260px;">
      <p class="form-header" style="font-weight: bold;font-size: 15px;"> PAY USING</p>

      <div style="text-align: center;padding: 20px 0;">
        <img class="upi-images" src="/images/merchant/images/UPI/BHIM.png">
        <img class="upi-images" src="/images/merchant/images/UPI/GooglePay.png">
        <img class="upi-images" src="/images/merchant/images/UPI/PAYTM.png">
        <img class="upi-images" src="/images/merchant/images/UPI/PhonePe.png">
      </div>

      <div class="form-group has-float-label control-group-full" style="float: none;text-align: center;margin: auto;width: 98%"  >
        <input type="text" class="form-control input-control1" id="upi" placeholder=" " onkeypress="validateUPI('upi',event)"
               oninput="this.className = 'form-control input-control1'"  name="upi" />
        <label for="upi" class="form-label">UPI Address</label>
      </div>
    </div>

    <div style="overflow:hidden;" >
      <div style="float:right;">
        <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'upiForm')" >
          <i class="fas fa-angle-left"></i>
        </div>
      </div>
    </div>


    <div class="pay-btn" >
      <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'upiForm')">
        <%=varNext%>
      </div>
    </div>

    <input type="hidden" name="paymentBrand" id="paymentBrand" value="UPI">
    <jsp:include page="requestParameters.jsp">
      <jsp:param name="paymentMode" value="UPI" />
    </jsp:include>

  </form>
</div>