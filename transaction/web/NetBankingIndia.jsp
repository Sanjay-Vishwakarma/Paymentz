<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.payment.safexpay.SafexPayPaymentGateway" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.payment.billdesk.BillDeskPaymentGateway" %>
<%@ page import="com.payment.bhartiPay.BhartiPayPaymentGateway" %>
<%@ page import="com.payment.qikpay.QikpayPaymentGateway" %>
<%@ page import="com.payment.LetzPay.LetzPayPaymentGateway" %>
<%@ page import="com.payment.verve.VervePaymentGateway" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.payment.easypaymentz.EasyPaymentzPaymentGateway" %>
<%@ page import="com.payment.imoneypay.IMoneyPayPaymentGateway" %>
<%@ page import="com.payment.cashfree.CashFreePaymentGateway" %>
<%@ page import="com.payment.payg.PayGPaymentGateway" %>
<%@ page import="com.payment.apexpay.ApexPayPaymentGateway" %>
<%@ page import="com.payment.payu.PayUPaymentGateway" %>
<%@ page import="com.payment.qikpayv2.QikPayV2PaymentGateway" %>
<%@ page import="com.payment.onepay.OnePayPaymentGateway" %>
<%@ page import="com.payment.lyra.LyraPaymentGateway" %>

<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 6/22/2018
  Time: 5:57 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="IndianBanksAndWalletList.jsp"%>
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

    if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getEmail())){
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

    String payMode  = "";
    String tabId    = "";
    if(functions.isValueNull(request.getParameter("id"))){
        payMode     = request.getParameter("id");
        tabId       = "NetBankingBangla";
    }
    else{
        payMode = "NBI";
        tabId   ="NetBankingIndia";
    }

    ResourceBundle rb1 = null;
    String lang = "";
    String multiLanguage = "";

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
            <%-- <div class="form-group has-float-label control-group-half">
                 <input type="text" class="form-control input-control1" id="firstname_nbi" placeholder=" "
                        oninput="this.className = 'form-control input-control1'" name="firstname" value="<%=firstName%>" autofocus maxlength="50" autocomplete="off" />
                 <label for="firstname_nbi" class="form-label"><%=varFirstName%></label>
             </div>
             <div class="form-group has-float-label control-group-half">
                 <input type="text" class="form-control input-control1" id="lastname_nbi" placeholder=" "
                        oninput="this.className = 'form-control input-control1'" name="lastname" value="<%=lastName%>" autofocus maxlength="50" autocomplete="off" />
                 <label for="lastname_nbi" class="form-label"><%=varLastName%></label>
             </div>--%>
            <%--            <div class="form-group has-float-label control-group-half" >
                            <div class="dropdown">
                                <input id="country_input_nbi" class="form-control input-control1" placeholder=" " oninput="this.className = 'form-control input-control1'"
                                       onblur="pincodecc('country_input_nbi','country_nbi','phonecc_id_nbi','phonecc_nbi'); " onkeypress="return isLetterKey(event)" />
                                <label for="country_input_nbi" class="form-label"><%=varCountry%></label>
                                <input type="hidden" id="country_nbi"  name="country_input" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
                            </div>
                        </div>--%>
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
                    <input type="text" class="form-control input-control1" id="INphoneNum_nbi" placeholder=" " name="telno" value="<%=phoneno%>" maxlength="10"
                           onchange="validateInPhone(event.target.value,'nbiPhoneNum')"
                           onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" oninput="this.className = 'form-control input-control1'" />
                    <label for="INphoneNum_nbi" class="form-label"><%=varPhoneno%></label>
                </div>
            </div>


            <%--            <script>
                            setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_nbi');
                            pincodecc('country_input_nbi','country_nbi','phonecc_id_nbi','phonecc_nbi');
                        </script>--%>
        </div>
        <%
            }
        %>

        <div class="tab" style="padding: 0px;height: auto;">

            <%  Logger log          = new Logger("NetBankingIndia.jsp");
                HashMap paymentMap  = standardKitValidatorVO.getMapOfPaymentCardType();
                String paymodeId    = GatewayAccountService.getPaymentId(payMode.toString());
                System.out.println(" NetBankingIndia paymentMap ------>"+paymentMap);

                List<String> cardList       = (List<String>)paymentMap.get(paymodeId);
                Set<String> bankList        = new HashSet<>();
                Set<String> terminalBank    = new HashSet<>();
                bankList.add("SBI");
                bankList.add("HDFC");
                bankList.add("ICICI");
                bankList.add("AXIS");
                bankList.add("KOTAK");
                bankList.add("YES BANK");

                bankList.add("Dutch Bangla Bank");
                bankList.add("Sonali Bank");
                bankList.add("Grameen Bank");
                bankList.add("Janata Bank");
                bankList.add("HSBC");
                bankList.add("IBBL");

                String propName = "";
                for(String cardId : cardList)
                {
                    String cardName = GatewayAccountService.getCardType(cardId);
                    if(cardName.contains("BANKS") || cardName.contains("Banks"))
                    {
                        propName = cardName;
                        log.error("inside for propName ------>"+propName);
                        break;
                    }
                }

//                System.out.println("Prop name---"+propName);
                //ResourceBundle rb = ResourceBundle.getBundle("SEPBANKS.properties");
                Set<String> keys2               = null;
                Set<String> keys2_New               = null;
                ResourceBundle rb               = null;
                boolean isotherbanks            = false;
                Map<String,String> bankList1    = null;
                TreeMap<String,String> tempbankListMap    = null;


                if(functions.isValueNull(propName)&&(bankList.contains(propName))){
                    //    rb = LoadProperties.getProperty("com.directi.pg."+propName);
//                    System.out.println("propName------------>"+propName);
                    bankList1    = bankNameMap.get(propName);
                    keys2       = bankList1.keySet();
                }
                else
                {  isotherbanks = true;

                    HashMap terminalMap = standardKitValidatorVO.getTerminalMap();

                    HashMap <String,TerminalVO>limitRoutingTerminalMap  = standardKitValidatorVO.getTerminalMapLimitRouting();
                    TerminalVO terminalVO2=null;
                    System.out.println("terminalmsp::"+terminalMap);
                    System.out.println("limitRoutingTerminalMap::"+limitRoutingTerminalMap);
                    String currency ="";
                    String terminalid="";
                    String cardName ="";
                    String accountid ="";
                    String gateway = "";
                    String seprator="_";
                    for (String cardId : cardList)
                    {
                        currency = standardKitValidatorVO.getTransDetailsVO().getCurrency();
                        terminalid = standardKitValidatorVO.getTerminalId();
                        cardName = GatewayAccountService.getCardType(cardId);
                        accountid = "";
                        gateway = "";
                    }
                    if(limitRoutingTerminalMap!=null)
                    {
                        terminalVO2   = (TerminalVO) limitRoutingTerminalMap.get(terminalid);
                        accountid          = terminalVO2.getAccountId();
                        gateway          = terminalVO2.getGateway();
                        System.out.println("inside if netbankingindia accountid--->"+accountid+" gateway::"+gateway);

                    }
                    else  if(terminalMap!=null)
                    {
                        TerminalVO terminalVO   = (TerminalVO) terminalMap.get("NBI-" + cardName + "-" + currency);
                        System.out.println("terminalVO.getGateway()::"+terminalVO.getGateway());
                        accountid          = terminalVO.getAccountId();
                        gateway            = terminalVO.getGateway();
                    }

                    if(limitRoutingTerminalMap != null && limitRoutingTerminalMap.size()>1)
                    {

                        gateway         ="gateway";
                        Iterator keys   = limitRoutingTerminalMap.keySet().iterator();
                        HashMap <String,String>combineBankMap=new HashMap();
                        bankList1   = new HashMap<>();

                        while (keys.hasNext())
                        {

                            String key= (String) keys.next();
                            TerminalVO terminalVO  = (TerminalVO)limitRoutingTerminalMap.get(key) ;

                            System.out.println("terminalVO.getTerminalId() >>>>> " + terminalVO.getTerminalId());
                            String cardtype=GatewayAccountService.getCardType(terminalVO.getCardTypeId());
                            String terminal=terminalVO.getTerminalId();
                            String accounyid=terminalVO.getAccountId();
                            String gatewayName= terminalVO.getGateway();

                            String combime=cardtype+seprator+accounyid;
                            System.out.println("gatewayName >>>>> " +gatewayName);
                            System.out.println("combime >>>>> "+combime);

                            if(gatewayName.equalsIgnoreCase(SafexPayPaymentGateway.GATEWAY_TYPE)){
                                System.out.println("bankNameMap.containsKey(cardtype+seprator+accountid)  >>>>> "+bankNameMap.containsKey(cardtype+seprator+accountid));

                                if(bankNameMap.containsKey(cardtype+seprator+accounyid)){
                                    System.out.println("inside cardtype accountid tempbankListMap  >>>>> "+cardtype+seprator+accountid);

                                    tempbankListMap = bankNameMap.get(cardtype+seprator+accounyid);
                                    System.out.println("inside accountid tempbankListMap  >>>>> "+tempbankListMap);

                                }
                               else{
                                    tempbankListMap = bankNameMap.get(cardtype);
                                    System.out.println("tempbankListMap  >>>>> "+tempbankListMap);

                                }
                            }
                            else if(bankNameMap.containsKey(cardtype))
                                tempbankListMap   = bankNameMap.get(cardtype);
                            if(tempbankListMap!=null){
                                System.out.println("tempbankListMap  >>>>> "+tempbankListMap);

                                for (String key1 : tempbankListMap.keySet()) {

                                    bankList1.put(key1, tempbankListMap.get(key1)+"-"+cardtype+"-"+terminal+"-"+accounyid);
                                }
                                tempbankListMap=new TreeMap<>();
                            }
                        }
                        keys2   = bankList1.keySet();
                    }
                    else
                    {
                        if(gateway.equalsIgnoreCase(SafexPayPaymentGateway.GATEWAY_TYPE)){
                            if(bankNameMap.containsKey(propName+seprator+accountid))  {
                                bankList1   = bankNameMap.get(propName+seprator+accountid);
                                System.out.println("inside else condition with account id bankList1  >>>>> "+bankList1);
                                }
                            else{
                                bankList1   = bankNameMap.get(propName);
                            }
                        }
                        else if (bankNameMap.containsKey(propName)){
                            bankList1   = bankNameMap.get(propName);
                        }
                        keys2 = bankList1.keySet();
                    }
                }



            %>
            <div <%--style="height: 178px"--%>>
                <ul class="nav nav-tabs" id="NetBankingIndiaTab">
                    <%
                        for(String cardId : cardList)
                        {
                            String cardName = GatewayAccountService.getCardType(cardId);
                            terminalBank.add(cardName);
                            if(bankList.contains(cardName))
                            {
                    %>
                    <li class="tabs-li-bank" onclick="selectBank('<%=cardName%>')" id="<%=cardName%>">
                        <a title="<%=cardName%>" >
                            <img  class="bank-image" src="/images/merchant/images/bank/<%=cardName%>.png">
                            <div class="bank-label"><%=cardName%></div>
                        </a>
                    </li>
                    <%
                            }
                        }
                    %>
                </ul>
            </div>

            <div class="form-group has-float-label control-group-bank" >
                <select name="paymentBrand" class="form-control input-control1 nbi-dropdown" id="netBankingBankName" onchange="dropdownbank(this)">
                    <option value="">Select a Bank Name</option>
                    <%

                        if(keys2!=null && isotherbanks){
                            for (String key:keys2)
                            {

                                System.out.println("key---->"+key);
                                String bankName = key.replaceAll("\\_"," ");//Bank Name
                                System.out.println("inside while replase key------->"+key);
                                //  String value = rb.getString(key);//PgID
                                String value = bankList1.get(key);//PgID
                                System.out.println("inside while replase value------->"+value);
                                if(!terminalBank.contains(bankName))
                                    bankName = propName;
                    %>
                    <%--<option value="<%=key%>-<%=value%>"><%=rb.getString(key)%></option>--%>
                    <option value="<%=value%>-<%=bankName%>-<%=key%>"><%=key.replaceAll("\\_"," ")%></option>
                    <%
                        }
                    }
                    else{
                        for(String cardId : cardList)
                        {
                            String cardName = GatewayAccountService.getCardType(cardId);
                            for (String key:keys2)
                            {

                                if(key.contains(cardName)){
                                    log.error("key----------------------------->"+key);

                                    System.out.println("inside while replase key------->"+key);
                                    // String value = rb.getString(key);
                                    String value = bankList1.get(key);
                                    System.out.println("inside while replase value------->"+value);
                                    System.out.println("inside while keys2------->");
                    %>
                    <option value="<%=value%>-<%=cardName%>-<%=key%>"><%=key.replaceAll("\\_"," ")%></option>
                    <%}}}}%>

                </select>
                <label for="netBankingBankName" style="color:#757575">Bank Name</label>
            </div>
        </div>

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
        <input type="hidden" name="country_input" value="IN">
        <%--<input type="hidden" name="emailaddr" value="<%=email%>">--%>
        <jsp:include page="requestParameters.jsp">
            <jsp:param name="paymentMode" value="<%=payMode%>" />
        </jsp:include>
    </form>

</div>