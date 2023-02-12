<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%--
  Created by IntelliJ IDEA.
  User: Rihen
  Date: 1/23/2019
  Time: 5:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  HashMap paymentMap = standardKitValidatorVO.getMapOfPaymentCardType();
  Logger log = new Logger("ROMCARD.jsp");
  log.error("inside ROMCARD.jsp");

  String paymodeId = "21";
  String payMode = "ROM";

  Functions functions = new Functions();

  String merchantLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo();
  String partnerLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
  String consentFlag = standardKitValidatorVO.getMerchantDetailsVO().getConsentFlag();

  String termsLink = "";
  String privacyLink = "";
  String target = "";
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
  if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getEmail())){
    email =  standardKitValidatorVO.getAddressDetailsVO().getEmail();
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


  String varInstallmentInfo    = rb.getString("VAR_INSTALLMENTINFO");
  String varINSTALLMENT = rb.getString("VAR_INSTALLMENT");
  String consent1       = rb.getString("VAR_CONSENT_STMT1");
  String consent2       = rb.getString("VAR_CONSENT_STMT2");
  String consent3       = rb.getString("VAR_CONSENT_STMT3");
  String varAND         = rb.getString("VAR_AND");
  String varNext        = rb.getString("VAR_NEXT");
  String varMonths      = rb.getString("VAR_MONTHS");
  String varSelectMonths= rb.getString("VAR_SELECT_MONTHS");
  String noInstallment  = rb.getString("VAR_NO_INSTALLMENT_AVAILABLE");
  String varCardnumber  =rb.getString("VAR_CARDNUMBER");
  String varExpiry      =rb.getString("VAR_EXPIRY");
  String varCvv         =rb.getString("VAR_CVV");
  String varCardHolderName=rb.getString("VAR_CARDHOLDERNAME");

  TerminalVO terminalVO = null;
  HashMap terminalMap = standardKitValidatorVO.getTerminalMap();
  String currency = standardKitValidatorVO.getTransDetailsVO().getCurrency();
  String terminalid = "";
  String emiSupport = "";

  log.error("in PaymodeID 21");
  if(paymodeId.equalsIgnoreCase("21"))
  {
    log.error("in if PaymodeID 21");
    terminalVO = (TerminalVO) terminalMap.get("ROM-ROMCARD-" + currency);
    terminalid = terminalVO.getTerminalId();
    emiSupport = terminalVO.getIsEmi_support();

%>
<input type="hidden" name="terminalEMI" class="" id="terminalNEMI" value="<%=terminalid%>,<%=emiSupport%>">

<%
  }
%>

<%--<div id="CryptoOption" class="option-class">
  <ul class="nav nav-tabs" id="CryptoTab">

    <%

      List<String> cardList = (List<String>)paymentMap.get(paymodeId);
      for(String cardId : cardList)
      {
        String cardName = GatewayAccountService.getCardType(cardId);
        String cardImg = cardName+".png";
    %>
    <li class="tabs-li-wallet" onclick="cryptoHideShow('<%=cardName%>','<%=rb.getString(cardName)%>')">
      <a href="#<%=cardName%>"  data-toggle="tab" title="<%=cardName%>" aria-expanded="false">
        <img class="images-style" src="/images/merchant/images/crypto/<%=cardImg%>" alt="<%=cardName%>">
        <div class="label-style"><%=rb.getString(cardName)%></div>
      </a>
    </li>

    <%
      }
    %>
  </ul>
</div>--%>

<%--<div class="tab-content">--%>

<%
  log.error("terminal id -----"+terminalid);
  log.error("emiSupport  -----"+emiSupport);
  String disabled ="";
  if(functions.isValueNull(emiSupport))
  {
    if (emiSupport.equalsIgnoreCase("N"))
    {
      disabled = "disabled";
    }
    else
    {
      disabled = "";
    }
  }
  else
  {
    log.error("Null Emi Support");
  }
%>


<div class="tab-pane" id="ROMCARD">
  <form id="romcardForm" class="form-style" method="post">
    <div class="tab" ><p class="form-header" style="display: none"><%=varInstallmentInfo%></p>

      <div class="form-group has-float-label control-group-seventy card" id="RCCardNumber">
        <input class="form-control input-control1" id="rccardNum" placeholder=" " type="text" onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" autocomplete="off"
               maxlength="19" oninput="this.className = 'form-control input-control1'"  name="cardnumber" />
        <label for="rccardNum" class="form-label"><%=varCardnumber%></label>
        <span class="card_icon"></span>
      </div>
      <div class="form-group has-float-label control-group-twenty-five" id="RCCardExpiry">
        <input type="text" class="form-control input-control1" id="rcExpiry" placeholder="MM/YY" onkeypress="return isNumberKey(event)" onblur="expiryCheck('rcExpiry','RCCardExpiry')"  autocomplete="off"
               onkeyup="addSlash(event,'rcExpiry')" oninput="this.className = 'form-control input-control1'" name="expiry" maxlength="5"/>
        <label for="rcExpiry" class="form-label"><%=varExpiry%></label>
      </div>
      <div class="form-group has-float-label control-group-seventy" id="RCCardName">
        <input type="text" class="form-control input-control1" id="rcfname" placeholder=" " onblur="validateCardHolderName('rcfname','RCCardName')"
               oninput="this.className = 'form-control input-control1'"  name="firstname" />
        <label for="rcfname" class="form-label"><%=varCardHolderName%></label>
      </div>
      <div class="form-group has-float-label control-group-twenty-five" id="RCCardCVV">
        <input type="password" class="form-control input-control1" id="rcCVV" placeholder=" " onkeypress="return isNumberKey(event)"
               onkeyup="onPasteNumCheck(event)" maxlength="4"  autocomplete="off" onblur="validateCVV('rcCVV','RCCardCVV')"
               oninput="this.className = 'form-control input-control1'" name="cvv"  />
        <label for="rcCVV" class="form-label"><%=varCvv%></label>
      </div>

      <div class="form-group emi" id="EMIOption" style="float: left;padding: 10px 0px;width: 100%;margin: 0px;">
        <input type="checkbox" name="emiOption" id="installmentRomcard" style="width: 7%" onchange="toggleEMIRomCard('installmentRomcard','installmentsROMCARD')" <%=disabled%> >
        <label for="emi" style="font-size:85%;width: 92%;float: right">
          <div class="form-group has-float-label control-group-full" style="margin: 0px;" id="emiSelect">
            <div><%=varINSTALLMENT%></div>
            <div>
              <select class="form-control input-control1" id="installmentsROMCARD" name="emiCount" style="height: 26px;padding: 0px !important;" disabled >
                <option value=""><%=varSelectMonths%></option>
                <option value="2">2 <%=varMonths%></option>
                <option value="4">4 <%=varMonths%></option>
                <option value="6">6 <%=varMonths%></option>
                <option value="8">8 <%=varMonths%></option>
                <option value="10">10 <%=varMonths%></option>
                <option value="12">12 <%=varMonths%></option>
              </select>
            </div>
          </div>
        </label>
      </div>

      <%
        if(functions.isValueNull(emiSupport))
        {
          if(emiSupport.equalsIgnoreCase("N"))
          {
      %>
      <span style="float: left;padding: 20px 0px 0px 0px;;color: red;font-size: 12px;"><%=noInstallment%></span>
      <%
          }
        }
      %>


      <input type="hidden" name="emailaddr" value="<%=email%>">
    </div>
    <div style="overflow:hidden;" >
      <div style="float:right;">
        <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'romcardForm')" >
          <i class="fas fa-angle-left"></i>
        </div>
      </div>
    </div>
    <%
      if(consentFlag.equalsIgnoreCase("Y"))
      {
    %>
    <div class="form-group terms-checkbox hide" id="terms">
      <input type="checkbox" class="" style="width: 5%" id="TC_rc" onclick="disablePayButton(this)">
      <label for="TC_rc" style="font-size: 85%;position: absolute;margin: 0px;">
        <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>> <%=consent2%></a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>> <%=consent3%> </a></b>.
      </label>
    </div>
    <%
      }
    %>
    <div class="pay-btn" >
      <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'romcardForm')">
        <%=varNext%>
      </div>
    </div>
    <input type="hidden" name="paymentBrand" id="paymentBrand" value="ROMCARD">
    <jsp:include page="requestParameters.jsp">
      <jsp:param name="paymentMode" value="<%=payMode%>" />
    </jsp:include>
  </form>
</div>

<%--</div>--%>