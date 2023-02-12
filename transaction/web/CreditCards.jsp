<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.json.JSONObject" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%--
  Created by IntelliJ IDEA.
  User: Rihen
  Date: 5/24/2018
  Time: 5:29 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" pageEncoding="UTF-8" %>


<%

    String ctoken = "";
    ctoken = (String) session.getAttribute("ctoken");
    System.out.println("ctoken in JSP---" + ctoken);
    Functions functions = new Functions();

    CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
    HashMap paymentMap = standardKitValidatorVO.getMapOfPaymentCardType();
    String currency = standardKitValidatorVO.getTransDetailsVO().getCurrency();
    String multiCurrency = standardKitValidatorVO.getMerchantDetailsVO().getMultiCurrencySupport();
    String merchantLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getMerchantLogo();
    String partnerLogoFlag = standardKitValidatorVO.getMerchantDetailsVO().getPartnerLogoFlag();
    String consentFlag = standardKitValidatorVO.getMerchantDetailsVO().getConsentFlag();

    String termsLink = "";
    String privacyLink = "";
    String target = "";

    String email = "";
    String phonecc = "";
    String phoneno = "";
    String birthDate = "";
    String street = "";
    String city = "";
    String state = "";
    String zip = "";

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
    if (functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getBirthdate()))
    {
        birthDate = standardKitValidatorVO.getAddressDetailsVO().getBirthdate();
    }
    if (functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getStreet()))
    {
        street = standardKitValidatorVO.getAddressDetailsVO().getStreet();
    }
    if (functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getCity()))
    {
        city = standardKitValidatorVO.getAddressDetailsVO().getCity();
    }
    if (functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getState()))
    {
        state = standardKitValidatorVO.getAddressDetailsVO().getState();
    }
    if (functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getZipCode()))
    {
        zip = standardKitValidatorVO.getAddressDetailsVO().getZipCode();
    }

    if (functions.isValueNull(merchantLogoFlag) && merchantLogoFlag.equalsIgnoreCase("Y"))
    {
        termsLink = standardKitValidatorVO.getMerchantDetailsVO().getTcUrl();
        privacyLink = standardKitValidatorVO.getMerchantDetailsVO().getPrivacyPolicyUrl();
        target = "target=\"_blank\"";
        if (functions.isEmptyOrNull(termsLink))
            termsLink = "/#";
        if (functions.isEmptyOrNull(privacyLink))
            privacyLink = "/#";

    }
    else if (functions.isValueNull(partnerLogoFlag) && partnerLogoFlag.equalsIgnoreCase("Y"))
    {
        termsLink = standardKitValidatorVO.getMerchantDetailsVO().getPartnerTcUrl();
        privacyLink = standardKitValidatorVO.getMerchantDetailsVO().getPartnerPrivacyUrl();
        target = "target=\"_blank\"";

        if (functions.isEmptyOrNull(termsLink))
            termsLink = "#";
        if (functions.isEmptyOrNull(privacyLink))
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
        else if("sp".equalsIgnoreCase(lang))
        {
            rb1=LoadProperties.getProperty("com.directi.pg.CheckoutLanguage_sp");
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

    String varPersonalinfo = rb1.getString("VAR_PERSONALINFO");
    String varEmailid = rb1.getString("VAR_EMAILID");
    String varCountry = rb1.getString("VAR_COUNTRY");
    String varPhonecc = rb1.getString("VAR_PHONECC");
    String varPhoneno = rb1.getString("VAR_PHONENO");
    String varNext = rb1.getString("VAR_NEXT");
    String varAddressinfo = rb1.getString("VAR_ADDRESSINFO");
    String varAddress = rb1.getString("VAR_ADDRESS");
    String varCity = rb1.getString("VAR_CITY");
    String varZip = rb1.getString("VAR_ZIP");
    String varState = rb1.getString("VAR_STATE");
    String varCardinfo = rb1.getString("VAR_CARDINFO");
    String varCardnumber = rb1.getString("VAR_CARDNUMBER");
    String varExpiry = rb1.getString("VAR_EXPIRY");
    String varCvv = rb1.getString("VAR_CVV");
    String varCardHolderName = rb1.getString("VAR_CARDHOLDERNAME");
    String consent1 = rb1.getString("VAR_CONSENT_STMT1");
    String consent2 = rb1.getString("VAR_CONSENT_STMT2");
    String consent3 = rb1.getString("VAR_CONSENT_STMT3");
    String varAND = rb1.getString("VAR_AND");
    String varINSTALLMENT = rb1.getString("VAR_INSTALLMENT");
    String varBirthdate = rb1.getString("VAR_BIRTHDATE");
    //String varCreditCards=rb1.getString("CreditCards");

    String id = request.getParameter("id");
    String payment_Type = "";
    if (functions.isValueNull(id))
    {
        paymodeId = "4";
    }
    if(functions.isValueNull(request.getParameter("payment_Type"))){
        payment_Type     = request.getParameter("payment_Type");
    }

    String paymentOtionCode = "";
    HashMap<String, ArrayList<TerminalVO>> paymentTypeHM = null;
       if(request.getAttribute("paymentTypeHM") != null){
           paymentTypeHM = (HashMap<String, ArrayList<TerminalVO>>) request.getAttribute("paymentTypeHM");
           ArrayList<TerminalVO>  terminalVOList = null;
           if(paymentTypeHM.containsKey(payment_Type)){
               terminalVOList = paymentTypeHM.get(payment_Type);
            }
           if(terminalVOList != null){
               StringBuffer paymentOtionCodeBuf = new StringBuffer();
               for(TerminalVO terminalVO1 :terminalVOList){
                   if(paymentOtionCodeBuf.length()>0){
                       paymentOtionCodeBuf.append(","+terminalVO1.getCardType()+"="+terminalVO1.getCardTypeId());
                   }else{
                       paymentOtionCodeBuf.append(terminalVO1.getCardType()+"="+terminalVO1.getCardTypeId());
                   }
               }
               paymentOtionCode = paymentOtionCodeBuf.toString();
           }

       }


%>

<%--<div id="CreditCardsOption" class="option-class">
  <ul class="nav nav-tabs" id="CardTab">--%>

<%
    List<String> cardList = (List<String>) paymentMap.get(paymodeId);

    standardKitValidatorVO.setPaymentMode(paymodeId);

    String gateway = "";

    for (String cardId : cardList)
    {
        String addressValidation = "";
        String addressDisplay = "";
        String terminalid = "";
        String cardName = GatewayAccountService.getCardType(cardId);
        TerminalVO terminalVO = null;


        if (paymodeId.equals("1"))
        {
            if (multiCurrency.equalsIgnoreCase("Y"))
            {
                terminalVO = (TerminalVO) terminalMap.get("CC-" + cardName + "-" + currency);
                if (terminalVO == null || terminalMap.size() == 0)
                    terminalVO = (TerminalVO) terminalMap.get("CC-" + cardName + "-ALL");
            }
            else
                terminalVO = (TerminalVO) terminalMap.get("CC-" + cardName + "-" + currency);

            addressValidation = terminalVO.getAddressValidation();
            addressDisplay = terminalVO.getAddressDetails();

            terminalid = terminalVO.getTerminalId();

%>
<input type="hidden" name="<%=cardName%>" class="" id="<%=cardName%>"
       value="<%=terminalid%>,<%=addressValidation%>,<%=addressDisplay%>,<%=currency%>">

<%
    System.out.println("Gateway name---" + terminalVO.getGateway());
    gateway = terminalVO.getGateway();
    if (terminalVO.getGateway().equalsIgnoreCase("wland"))
    {
%>
<jsp:include page="wland.jsp">
    <jsp:param name="accountId" value="<%=terminalVO.getAccountId()%>"/>
</jsp:include>
<%
    }
}
else
{
    if (multiCurrency.equalsIgnoreCase("Y"))
    {
        terminalVO = (TerminalVO) terminalMap.get("DC-" + cardName + "-" + currency);
        if (terminalVO == null)
            terminalVO = (TerminalVO) terminalMap.get("DC-" + cardName + "-ALL");
    }
    else
        terminalVO = (TerminalVO) terminalMap.get("DC-" + cardName + "-" + currency);

    addressValidation = terminalVO.getAddressValidation();
    addressDisplay = terminalVO.getAddressDetails();

    terminalid = terminalVO.getTerminalId();

%>
<input type="hidden" name="<%=cardName%>" class="" id="<%=cardName%>_DC"
       value="<%=terminalid%>,<%=addressValidation%>,<%=addressDisplay%>,<%=currency%>">
<%
    }

%>
<%--<input type="hidden" name="<%=cardName%>" class="" id="<%=cardName%>" value="<%=terminalid%>,<%=addressValidation%>,<%=addressDisplay%>">--%>
<%
    }

%>


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

<%
    String value = "";
    if (id.equals("dc"))
    {
        value = "DebitCards";
    }
    else
    {
        value = "CreditCards";
    }
%>

<script>
    var funcParam = {
        city: 'cityLabel<%=id%>',
        zip: 'zipLabel<%=id%>'
    }
</script>

<%--<div class="tab-content">--%>
<div class="tab-pane" id="<%=value%>">
    <form id="cardForm<%=id%>" class="form-style" method="post" autocomplete="off">
        <input type="hidden" name="ctoken" id="CCToken" value="<%=ctoken%>">
        <input type="hidden" id="terminalid<%=id%>" name="terminalid" value="">
        <input type="hidden" id="paymentOtionCode"  name="paymentOtionCode" value="<%=paymentOtionCode%>">
        <input type="hidden" id="attemptThreeD" name="attemptThreeD"
               value="<%=standardKitValidatorVO.getAttemptThreeD()%>">
        <input type="hidden" id="consentStmnt" name="consentStmnt" value="<%=consent1+consent2+consent3%>">
        <input type="hidden" name="merchantOrderDetails" id="merchantOrderDetails<%=id%>"
               value="<%=standardKitValidatorVO.getMerchantDetailsVO().getMerchantOrderDetailsDisplay()%>">

        <div class="tab" id="personalinfo<%=id%>" style="padding-top: 22px;height:auto;overflow:unset">
            <p class="form-header" style="display: none;"><%=varPersonalinfo%></p>
            <div id="emailPersonal">
                <div class="form-group has-float-label control-group-full" id="CardEmail<%=id%>">
                    <span class="input-icon"><i class="far fa-envelope"></i></span>
                    <input type="email" class="form-control input-control1" id="email<%=id%>" placeholder=" " onchange="validateEmail(event.target.value ,'CardEmail<%=id%>')"
                           oninput="this.className = 'form-control input-control1'" name="emailaddr" value="<%=email%>" autofocus maxlength="50" autocomplete="off" />
                    <label for="email<%=id%>" class="form-label"><%=varEmailid%></label>
                </div>
            </div>
            <div id="mobilePersonal">
                <div class="form-group has-float-label control-group-full">
                    <div class="dropdown">
                        <input id="country_input_optional<%=id%>" class="form-control input-control1" placeholder=" "
                               onblur="pincodecc('country_input_optional<%=id%>','country_optional<%=id%>','phonecc-id<%=id%>','phonecc<%=id%>','country_input<%=id%>','country<%=id%>','statelabel<%=id%>',funcParam);"
                               oninput="this.className = 'form-control input-control1'" onkeypress="return isLetterKey(event)" autocomplete="off">
                        <label for="country_input_optional<%=id%>" class="form-label"><%=varCountry%></label>
                        <input type="hidden" id="country_optional<%=id%>" name="country_input_optional" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
                        <script>
                            setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>', 'country_input_optional<%=id%>');
                        </script>
                    </div>
                </div>
                <div class="form-group has-float-label control-group-half">
                    <input type="text" class="form-control input-control1" name="phone-CC" id="phonecc-id<%=id%>" placeholder=" " onkeypress="return isNumberKey(event)"
                           onkeyup="onPasteNumCheck(event)" maxlength="3" onblur="setPhoneCC('phonecc-id<%=id%>','phonecc<%=id%>')" value="<%=phonecc%>"
                           oninput="this.className = 'form-control input-control1'"/>
                    <label for="phonecc-id<%=id%>" class="form-label"><%=varPhonecc%></label>
                    <input type="hidden" id="phonecc<%=id%>" name="phone-CC" value="<%=phonecc%>"/>
                </div>
                <div class="form-group has-float-label control-group-half" id="CardPhoneNum<%=id%>">
                    <span class="input-icon-half"><i class="fas fa-phone"></i> </span>
                    <input type="text" class="form-control input-control1" id="phonenophonecc<%=id%>" placeholder=" " onkeypress="return isNumberKey(event)"
                           onkeyup="onPasteNumCheck(event)" oninput="this.className = 'form-control input-control1'" maxlength="24" name="telno" value="<%=phoneno%>"/>
                    <label for="phonenophonecc<%=id%>" class="form-label"><%=varPhoneno%></label>
                </div>
            </div>
            <%
                if (gateway.equalsIgnoreCase("truevo") || "ilixium".equalsIgnoreCase(gateway) || "CFCaibo".equalsIgnoreCase(gateway))
                {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
                    if (functions.isValueNull(birthDate))
                        birthDate = dateFormat.format(dateFormat1.parse(birthDate));
            %>
            <div class="form-group has-float-label control-group-full" id="CardBirthDate">
                <input type="date" class="form-control input-control1" id="cardbirthdate" name="birthDate" placeholder=" " onblur="birthdayCheck('cardbirthdate','CardBirthDate')"
                       oninput="this.className = 'form-control input-control1'" value="<%=birthDate%>"/>
                <label for="cardbirthdate" style="color:#757575"><%=varBirthdate%></label>
            </div>
            <%
                }
            %>
        </div>

        <div class="tab" id="cardinfo<%=id%>" style="padding-top: 22px;">
            <p class="form-header" style="display: none;"><%=varCardinfo%></p>

            <div class="form-group has-float-label control-group-seventy card" id="CardNumber<%=id%>">
                <input class="form-control input-control1" id="cardNo<%=id%>" placeholder=" " type="text" onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
                       onfocusout="isValidCardCheck('cardNo<%=id%>', 'CardNumber<%=id%>','onfocusout')" <%--onfocusout = "funcCheck('cardNo', 'CardNumber')"--%>
                       autocomplete="off" maxlength="19" oninput="this.className = 'form-control input-control1'" name="cardnumber"/>
                <label for="cardNo<%=id%>" class="form-label"><%=varCardnumber%></label>
                <span class="card_icon"></span>
            </div>
            <div class="form-group has-float-label control-group-twenty-five" id="CardExpiry<%=id%>">
                <input type="text" class="form-control input-control1" id="Expiry<%=id%>" placeholder="MM/YY"
                       onkeypress="return isNumberKey(event)" onblur="expiryCheck('Expiry<%=id%>','CardExpiry<%=id%>')" autocomplete="off"
                       onkeyup="addSlash(event,'Expiry<%=id%>')" oninput="this.className = 'form-control input-control1'" name="expiry" maxlength="5"/>
                <label for="Expiry<%=id%>" class="form-label"><%=varExpiry%></label>
            </div>
            <div class="form-group has-float-label control-group-seventy" id="CardName<%=id%>">
                <input type="text" class="form-control input-control1" id="fname<%=id%>" placeholder=" " onblur="validateCardHolderName('fname<%=id%>','CardName<%=id%>')"
                       oninput="this.className = 'form-control input-control1'" name="firstname"/>
                <label for="fname<%=id%>" class="form-label"><%=varCardHolderName%></label>
            </div>
            <div class="form-group has-float-label control-group-twenty-five" id="CardCVV<%=id%>">
                <input type="password" class="form-control input-control1" id="CVV<%=id%>" placeholder=" " onkeypress="return isNumberKey(event)"
                       onkeyup="onPasteNumCheck(event)" maxlength="4" autocomplete="off" onblur="validateCVV('CVV<%=id%>','CardCVV<%=id%>')"
                       oninput="this.className = 'form-control input-control1'" name="cvv"/>
                <label for="CVV<%=id%>" class="form-label"><%=varCvv%></label>
            </div>

            <div class="form-group emi" id="EMIOption<%=id%>"
                 style="float: left;padding: 10px 0px;width: 100%;margin: 0px;display: none">
                <input type="checkbox" class="" style="width: 7%" id="emi<%=id%>" onchange="toggleEMI()">
                <label for="emi<%=id%>" style="font-size:85%;/*position: absolute;*/width: 92%;float: right">
                    <div class="form-group has-float-label control-group-full" style="margin: 0px;"
                         id="emiSelect<%=id%>">
                        <div style="/*float: left;*/"><%=varINSTALLMENT%></div>
                        <div style="/*float: left;width: 16% !important;margin: 0px 9px;*/">
                            <select class="form-control input-control1" id="installment<%=id%>" name="emiCount" style="height: 26px;;padding: 0px !important;" disabled></select>
                        </div>
                    </div>
                </label>
            </div>

            <div id="hideaddressinfo<%=id%>" style="margin: 80px 0px 0px 0px">
                <div class="hide" id="addressinfo<%=id%>">
                    <p class="form-header"><%=varAddressinfo%>
                    </p>

                    <div class="form-group has-float-label control-group-full" id="CardAddress<%=id%>">
                        <input type="text" class="form-control input-control1" id="address<%=id%>" placeholder=" " onfocusout="validateAddress(event.target.value ,'CardAddress<%=id%>')"
                               oninput="this.className = 'form-control input-control1'" name="street" value="<%=street%>"/>
                        <label for="address<%=id%>" class="form-label"><%=varAddress%></label>
                    </div>
                    <div class="form-group has-float-label control-group-half" id="CardCity<%=id%>">
                        <input type="text" class="form-control input-control1" id="city<%=id%>" placeholder=" " onfocusout="validateCity(event.target.value ,'CardCity<%=id%>')"
                               oninput="this.className = 'form-control input-control1'" name="city" value="<%=city%>"/>
                        <label for="city<%=id%>" id="cityLabel<%=id%>" class="form-label"><%=varCity%></label>
                    </div>
                    <div class="form-group has-float-label control-group-half" id="CardZip<%=id%>">
                        <input type="text" class="form-control input-control1" id="zip<%=id%>" placeholder=" " onfocusout="validateZip(event.target.value ,'CardZip<%=id%>')"
                               oninput="this.className = 'form-control input-control1'" name="zip" value="<%=zip%>"/>
                        <label for="zip<%=id%>" id="zipLabel<%=id%>" class="form-label"><%=varZip%></label>
                    </div>
                    <div class="form-group has-float-label control-group-half" id="CardCountry<%=id%>">
                        <div class="dropdown">
                            <input id="country_input<%=id%>" class="form-control input-control1" placeholder=" " onfocusout="validateCountry(event.target.value ,'CardCountry<%=id%>')"
                                   onblur="pincodecc('country_input<%=id%>','country<%=id%>','phonecc-id<%=id%>','phonecc<%=id%>','country_input<%=id%>','country<%=id%>','statelabel<%=id%>',funcParam);"
                                   onkeypress="return isLetterKey(event)" oninput="this.className = 'form-control input-control1'">
                            <label for="country_input<%=id%>" class="form-label"><%=varCountry%></label>
                            <input type="hidden" id="country<%=id%>" name="country_input">
                        </div>
                    </div>
                    <div class="form-group has-float-label control-group-half" id="CardState<%=id%>">
                        <div class="dropdown">
                            <input type="text" class="form-control input-control1" id="state<%=id%>" oninput="this.className = 'form-control input-control1';stateinp();" placeholder=" "
                                   onblur="validateState(event.target.value ,'CardState<%=id%>')" name="state" value="<%=state%>" autocomplete="off"/>
                            <label for="state<%=id%>" id="statelabel<%=id%>" class="form-label"><%=varState%></label>
                            <script>
                                pincodecc('country_input_optional<%=id%>', 'country_optional<%=id%>', 'phonecc-id<%=id%>', 'phonecc<%=id%>', 'country_input<%=id%>', 'country<%=id%>', 'statelabel<%=id%>', funcParam);
                                setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>', 'country_input<%=id%>');
                                validateCountry(document.getElementById('country_input<%=id%>').value, 'CardCountry<%=id%>');
                                StateLabel('country_input<%=id%>', 'country<%=id%>', 'statelabel<%=id%>');
                                cityLabel("country<%=id%>", "cityLabel<%=id%>");
                                zipCodeLabel("country<%=id%>", "zipLabel<%=id%>");
                            </script>
                        </div>
                    </div>
                </div>
            </div>

        </div>
        <%
            if (consentFlag.equalsIgnoreCase("Y"))
            {
        %>
        <div class="form-group terms-checkbox hide" id="terms">
            <input type="checkbox" class="" style="width: 5%" id="TC<%=id%>" onclick="disablePayButton(this)">
            <label for="TC<%=id%>" style="font-size: 85%;position: absolute;margin: 0px;">
                <%=consent1%> <b> <a href="<%=privacyLink%>" <%=target%>><%=consent2%>
            </a> </b> <%=varAND%> <b> <a href="<%=termsLink%>" <%=target%>><%=consent3%>
            </a></b>.
            </label>
        </div>
        <%
            }
        %>
        <div style="overflow:hidden">
            <div style="float:right;"> <!-- Previous button-->
                <div class="form-btn prev-btn" id="prevBtn" onclick="nextPrev(-1 ,'cardForm<%=id%>')">
                    <i class="fas fa-angle-left"></i>
                </div>
            </div>
        </div>

        <div class="pay-btn">
            <button type="button" class="form-btn pay-button" id="nextBtn" onclick="nextPrev(1,'cardForm<%=id%>')"
                    tabindex="0">
                <%=varNext%>
            </button>
        </div>
        <input type="hidden" name="paymentBrand" id="paymentBrand<%=id%>" value="">
        <%
            String paym = "";
            if (id.equals("dc"))
            {
                paym = "DC";
            }
            else
            {
                paym = "CC";
            }
        %>
        <jsp:include page="requestParameters.jsp">
            <jsp:param name="paymentMode" value="<%=paym%>"/>
        </jsp:include>

        <input type="hidden" name="deviceNo" id="deviceNo"/>
        <input type="hidden" name="uniqueId" id="uniqueId"/>
        <!--Passing parameters hidden for SinglecallPayment-->
    </form>
</div>
<%--</div>--%>


<%!
    private String getValue(String data)
    {
        String tempVal = "";
        if (data == null)
        {
            tempVal = "";
        }
        else
        {
            tempVal = data;
        }
        return tempVal;
    }
%>