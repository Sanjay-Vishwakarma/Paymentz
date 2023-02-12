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
  String gateway = "";
  String id       = request.getParameter("id");
  String cardTypeId = "";

  if(paymentMap != null && paymentMap.size() > 0 && paymentMap.containsKey("41"))
  {
    List card = (List)paymentMap.get("41");

    if(card.contains("160") || card.contains("159"))
      gateway = "africawallet";
      cardTypeId = (String) card.get(0);
  }


  HashMap<String, ArrayList<TerminalVO>> paymentTypeHM = (HashMap<String, ArrayList<TerminalVO>>) request.getAttribute("paymentTypeHM");
  String payment_Id       = "";
  String payMode          = "";
  String payment_Type     = "";

  Functions functions     = new Functions();
  String merchantLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo();
  String partnerLogoFlag  = standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
  String consentFlag      = standardKitValidatorVO.getMerchantDetailsVO().getConsentFlag();

  if(functions.isValueNull(request.getParameter("id"))){
    payMode     = request.getParameter("id");
  }
  if(functions.isValueNull(request.getParameter("payment_Type"))){
    payment_Type     = request.getParameter("payment_Type");
  }

  if(functions.isValueNull(request.getParameter("payment_Id"))){
    payment_Id     = request.getParameter("payment_Id");
  }
  String tabId      = "MobileMoneyAfrica";

  String termsLink    = "";
  String privacyLink  = "";
  String target       = "";

  String email    = "";
  String phonecc  = "";
  String phoneno  = "";
  String firstName = "";
  String lastName = "";


  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getEmail())){
    email =  standardKitValidatorVO.getAddressDetailsVO().getEmail();
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getTelnocc())){
    phonecc =  standardKitValidatorVO.getAddressDetailsVO().getTelnocc();
  }
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getPhone())){
    phoneno =  standardKitValidatorVO.getAddressDetailsVO().getPhone();
  }
  if (functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getFirstname()))
  {
    firstName = standardKitValidatorVO.getAddressDetailsVO().getFirstname();
  }
  if (functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getLastname()))
  {
    lastName = standardKitValidatorVO.getAddressDetailsVO().getLastname();
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


  List<String> cardList = (List<String>)paymentMap.get(payment_Id);

  String varPersonalinfo=rb.getString("VAR_PERSONALINFO");
  String varEmailid   = rb.getString("VAR_EMAILID");
  String varPhoneno   = rb.getString("VAR_PHONENO");
  String varNext      = rb.getString("VAR_NEXT");
  String consent1     = rb.getString("VAR_CONSENT_STMT1");
  String consent2     = rb.getString("VAR_CONSENT_STMT2");
  String consent3     = rb.getString("VAR_CONSENT_STMT3");
  String varAND       = rb.getString("VAR_AND");
  String varAccountingnumber=rb.getString("VAR_ACCOUNTINGNUMBER");
  String varName      =rb.getString("VAR_NAME");
  String varCountry   =rb.getString("VAR_COUNTRY");

  String varFirstName = rb.getString("VAR_FIRSTNAME");
  String varLastName  = rb.getString("VAR_LASTNAME");



  ArrayList<TerminalVO> terminalList = null;
  if(paymentTypeHM != null)
  {
    terminalList = paymentTypeHM.get(payment_Type);
  }

%>
<%if(payment_Type.equalsIgnoreCase("GiftCardAfrica")){
  System.out.println("payment_Type >>>>>>>>>>> "+payment_Type);
  String paymentOtionCode = "";
  String cardName         ="";
  String paymentBrand_Id  = "";
  String paymentBrand     ="";
  String instructions     ="";

  for(TerminalVO terminalVO : terminalList)
  {
    cardName           = terminalVO.getCardType();
    cardName            = cardName.replaceAll(" ", "_");

    if(terminalVO.getAddressDetails() != null)
    {
      instructions = terminalVO.getAddressDetails();
    }
    paymentOtionCode = terminalVO.getCardTypeId();
    if(cardList != null && cardList.size() > 0){
      paymentBrand_Id = cardList.get(0);
      paymentBrand    = GatewayAccountService.getCardType(paymentBrand_Id);
    }
  }
%>
<form id="<%=cardName%>Form" class="form-style" method="post">
  <input type="hidden" id="FinancialServiceName_<%=cardName%>"  name="financialServiceName" value="<%=cardName%>">
  <input type="hidden" id="paymentOtionCode<%=cardName%>"  name="paymentOtionCode" value="<%=paymentOtionCode%>">
  <input type="hidden" id="country_<%=cardName%>"  name="country_input" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
  <input type="hidden" id="firstname_<%=cardName%>"  name="firstname" value="<%=firstName%>">
  <input type="hidden" id="lastname_<%=cardName%>"  name="lastname" value="<%=lastName%>">
  <input type="hidden" id="lastname_<%=cardName%>"  name="lastname" value="<%=lastName%>">
  <input type="hidden" id="emailaddr_<%=cardName%>"  name="emailaddr" value="<%=email%>">
  <input type="hidden" id="phone-CC_<%=cardName%>"  name="phone-CC"  value="<%=phonecc%>">
  <input type="hidden" id="telno_<%=cardName%>"  name="telno" value="<%=phoneno%>">


  <div class="tab">
    <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>

    <%
      varAccountingnumber = "RedBoxx Gift Card Code";
    %>
    <div class="form-group has-float-label control-group-full" style="height: auto">
      <p style="font-size: 85%;"><%=instructions%></p>
    </div>
    <div class="form-group has-float-label control-group-full" id="AccNo<%=cardName%>" style="margin-top: 15px;">
      <input class="form-control input-control1" id="AccNo_<%=cardName%>" placeholder=" " type="text" onchange="return valiDateData(event.target.value,'AccNo<%=cardName%>')"
             maxlength="17" oninput="this.className = 'form-control input-control1'" name="accountnumber" />
      <label for="AccNo_<%=cardName%>" class="form-label"><%=varAccountingnumber%></label>
    </div>
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
  <input type="hidden" name="paymentBrand" id="paymentBrand" value="<%=paymentBrand%>">
  <jsp:include page="requestParameters.jsp">
    <jsp:param name="paymentMode" value="<%=payMode%>" />
  </jsp:include>

</form>

<%}

else if("MobileMoneyAfrica".equalsIgnoreCase(payment_Type) && "africawallet".equalsIgnoreCase(gateway))
{
%>
<div id="<%=payment_Type%>Option" class="option-class">
  <ul class="nav nav-tabs" id="<%=payment_Type%>Tab">
    <%
      for(String cardId : cardList)
      {
        String cardName = GatewayAccountService.getCardType(cardId);
        String cardImg  = cardName+".png";
        cardName        = cardName.replaceAll(" ", "_");
        String displayLabel   = "";
        if(rb.containsKey(cardName)){
          displayLabel = rb.getString(cardName);
        }else{
          displayLabel = cardName;
        }
    %>
    <li class="tabs-li"  onclick="MobileMoneyHideShow('<%=cardName%>','<%=payment_Type%>')">
      <a href="#<%=cardName%>"  data-toggle="tab" title="<%=cardName%>" aria-expanded="false">
        <div class="row">
          <div class="col-sm">
            <%--<img style="width:100px;" class="images-style" src="/images/merchant/images/<%=cardImg%>" alt="<%=cardName%>">--%>
            <img style="width:100px;" class="images-style" src="/images/merchant/<%=cardImg%>" alt="<%=cardName%>">
          </div>
          <div class="col-sm">
            <div class="label-style"><%=displayLabel%></div>
          </div>
        </div>
      </a>
    </li>

    <%
      }
    %>
  </ul>
</div>

<%
}
else
{
%>
<div id="<%=payment_Type%>Option" class="option-class">
  <ul class="nav nav-tabs" id="<%=payment_Type%>Tab">
    <%

      for(TerminalVO terminalVO : terminalList)
      {
        //String cardName = GatewayAccountService.getCardType(cardId);
        String cardName        = terminalVO.getCardType().replaceAll(" ", "_");
        //String cardImg          = terminalVO.getCardType().replaceAll(" ", "_")+".png";
        String cardImg          = terminalVO.getDisplayName();
        String displayLabel   = "";
        if(rb.containsKey(cardName)){
          displayLabel = rb.getString(cardName);
        }else{
          if(cardName.contains("_")){
            displayLabel = cardName.replaceAll("_", " ");
          }else{
            displayLabel = cardName;
          }

        }
    %>
    <li class="tabs-li"  onclick="instantBankTransferHideShow('<%=cardName%>','<%=displayLabel%>','<%=payment_Type%>','<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>')">
      <a href="#<%=cardName%>"  data-toggle="tab" title="<%=cardName%>" aria-expanded="false">
        <div class="row">
          <div class="col-sm">
            <%--<img style="width:100px;" class="images-style" src="/images/merchant/images/<%=cardImg%>" alt="<%=cardName%>">--%>
            <img style="width:100px;" class="images-style" src="<%=cardImg%>" alt="<%=cardName%>">
          </div>
          <div class="col-sm">
            <div class="label-style"><%=displayLabel%></div>
          </div>
        </div>
      </a>
    </li>

    <%
      }
    %>
  </ul>
</div>

<%}%>

<div class="tab-content">

  <%
    if(terminalList != null)
    {
      for(TerminalVO terminalVO : terminalList)
      {
        String cardName           = terminalVO.getCardType();
        String paymentBrand_Id    = "";
        String paymentBrand       = "";
        String instructions       = "";
        cardName                  = cardName.replaceAll(" ", "_");
        String financialServiceName = "";

        if(cardName.contains("_")){
          financialServiceName = cardName.replaceAll("_", " ");
        }else{
          financialServiceName = cardName;
        }


        if(terminalVO.getAddressDetails() != null)
        {
          instructions = terminalVO.getAddressDetails();
        }

        if(cardList != null && cardList.size() > 0){
          paymentBrand_Id = cardList.get(0);
          paymentBrand    = GatewayAccountService.getCardType(paymentBrand_Id);
        }
  %>
  <div class="tab-pane" id="<%=cardName%>">
    <form id="<%=cardName%>Form" class="form-style" method="post">
      <input type="hidden" id="FinancialServiceName_<%=cardName%>"  name="financialServiceName" value="<%=financialServiceName%>">
      <input type="hidden" id="paymentOtionCode<%=cardName%>"  name="paymentOtionCode" value="<%=terminalVO.getCardTypeId()%>">
      <input type="hidden" id="country_<%=cardName%>"  name="country_input" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
      <input type="hidden" id="firstname_<%=cardName%>"  name="firstname" value="<%=firstName%>">
      <input type="hidden" id="lastname_<%=cardName%>"  name="lastname" value="<%=lastName%>">
      <input type="hidden" id="lastname_<%=cardName%>"  name="lastname" value="<%=lastName%>">
      <input type="hidden" id="emailaddr_<%=cardName%>"  name="emailaddr" value="<%=email%>">
      <input type="hidden" id="phone-CC_<%=cardName%>"  name="phone-CC"  value="<%=phonecc%>">
      <input type="hidden" id="telno_<%=cardName%>"  name="telno" value="<%=phoneno%>">


      <div class="tab">
        <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
        <%
          if(payment_Type.equalsIgnoreCase("WalletAfrica")){
            varAccountingnumber = "E-mail Address /Mobile Number";
          }
          if(payment_Type.equalsIgnoreCase("MobileMoneyAfrica")){
            varAccountingnumber = "Mobile Number";
          }
          if(payment_Type.equalsIgnoreCase("GiftCardAfrica")){
            varAccountingnumber = "RedBoxx Gift Card Code";
          }
        %>
        <div class="form-group has-float-label control-group-full" style="height: auto">
          <p style="font-size: 85%;"><%=instructions%></p>
        </div>
        <div class="form-group has-float-label control-group-full" id="AccNo<%=cardName%>" style="margin-top: 15px;">
          <%if(payment_Type.equalsIgnoreCase("MobileMoneyAfrica")){%>
            <input type="text" value="" class="form-control input-control1" id="AccNo_<%=cardName%>" placeholder=" " oninput="this.className = 'form-control input-control1'" name="accountnumber" autofocus maxlength="17" autocomplete="off" onkeypress="return isNumberKey(event)"/>
            <label for="AccNo_<%=cardName%>" class="form-label"><%=varAccountingnumber%></label>
          <%}else{%>
            <input value="" class="form-control input-control1" id="AccNo_<%=cardName%>" placeholder=" " type="text" onchange="return valiDateData(event.target.value,'AccNo<%=cardName%>')"  <%--onkeypress="return isNumberKey(event)"--%>
                   maxlength="17" oninput="this.className = 'form-control input-control1'" name="accountnumber" />
            <label for="AccNo_<%=cardName%>" class="form-label"><%=varAccountingnumber%></label>
          <%}%>
        </div>
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
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="<%=paymentBrand%>">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
  <%
    }
  }else if("MobileMoneyAfrica".equalsIgnoreCase(payment_Type) && "africawallet".equalsIgnoreCase(gateway))
  {
    for(String cardId : cardList)
    {
      String cardName = GatewayAccountService.getCardType(cardId);
      String cardImg  = cardName+".png";
      cardName        = cardName.replaceAll(" ", "_");
      String displayLabel   = "";
      if(rb.containsKey(cardName)){
        displayLabel = rb.getString(cardName);
      }else{
        displayLabel = cardName;
      }
      String paymentBrand       = "";
      String instructions       = "";

      /*if(cardList != null && cardList.size() > 0){
        String paymentBrand_Id = cardList.get(0);
        paymentBrand    = GatewayAccountService.getCardType(paymentBrand_Id);
      }*/

  %>
  <div class="tab-pane" id="<%=cardName%>">
    <form id="<%=cardName%>Form" class="form-style" method="post">
      <input type="hidden" id="FinancialServiceName_<%=cardName%>"  name="financialServiceName" value="<%=cardName%>">
      <input type="hidden" id="country_<%=cardName%>"  name="country_input" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
      <input type="hidden" id="firstname_<%=cardName%>"  name="firstname" value="<%=firstName%>">
      <input type="hidden" id="lastname_<%=cardName%>"  name="lastname" value="<%=lastName%>">
      <input type="hidden" id="lastname_<%=cardName%>"  name="lastname" value="<%=lastName%>">
      <input type="hidden" id="emailaddr_<%=cardName%>"  name="emailaddr" value="<%=email%>">
      <input type="hidden" id="phone-CC_<%=cardName%>"  name="phone-CC"  value="<%=phonecc%>">
      <input type="hidden" id="telno_<%=cardName%>"  name="telno" value="<%=phoneno%>">


      <div class="tab" id="personalinfo" style="padding-top: 22px;height:auto;overflow:unset">
        <div id="emailPersonal">
          <div class="form-group has-float-label control-group-full" id="NBEmail">
            <span class="input-icon"><i class="far fa-envelope"></i></span>
            <input type="email" class="form-control input-control1" id="email_nbi" placeholder=" " onchange="validateEmail(event.target.value ,'NBEmail')"
                   oninput="this.className = 'form-control input-control1'" name="emailaddr" value="<%=email%>" autofocus maxlength="50" autocomplete="off" />
            <label for="email_nbi" class="form-label"><%=varEmailid%></label>
          </div>
        </div>

        <div id="mobilePersonal">

          <div class="form-group has-float-label control-group-half" style="width: 10% !important">
            <input type="text" class="form-control input-control1" name="phone-CC" id="phonecc_id_nbi" placeholder=" " onkeypress="return isNumberKey(event)"
                   onkeyup="onPasteNumCheck(event)" maxlength="3" onblur="setPhoneCC('phonecc_id_nbi','phonecc_nbi')" value="<%=phonecc%>"
                   oninput="this.className = 'form-control input-control1'"/>
            <label for="phonecc_id_nbi" class="form-label">CC</label>
            <input type="hidden" id="phonecc_nbi" name="phone-CC" value="<%=phonecc%>"/>
          </div>
          <div class="form-group has-float-label control-group-half" style="width: 3% !important;padding: 6px 0px;">
            -
          </div>

          <div class="form-group has-float-label control-group-half" id="nbiPhoneNum" style="width: 35% !important">
            <span class="input-icon-half"><i class="fas fa-phone"></i> </span>
          <%
            if(cardTypeId.equals("160"))
            {
          %>
            <input type="text" class="form-control input-control1" id="AFphoneNum_nbi" placeholder=" " name="telno" value="<%=phoneno%>" maxlength="10"
                   onchange="validatePhoneAfrica(event.target.value,'nbiPhoneNum', 9)"
                   onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" oninput="this.className = 'form-control input-control1'" />
            <%
              }
              else if(cardTypeId.equals("159"))
              {
            %>
            <input type="text" class="form-control input-control1" id="AFphoneNum_nbi" placeholder=" " name="telno" value="<%=phoneno%>" maxlength="10"
                   onchange="validatePhoneAfrica(event.target.value,'nbiPhoneNum', 10)"
                   onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" oninput="this.className = 'form-control input-control1'" />
            <%
              }
            %>
            <label for="AFphoneNum_nbi" class="form-label"><%=varPhoneno%></label>
          </div>

        </div>
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
      <div class="form-group terms-checkbox hide" style="margin-top: 55px;" id="terms">
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
      }}
  %>

</div>