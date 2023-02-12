<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.payment.safexpay.SafexPayPaymentGateway" %>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 6/23/2018
  Time: 7:17 PM
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

    String payMode = "";
    String tabId= "";
    if(functions.isValueNull(request.getParameter("id"))){
        payMode = request.getParameter("id");
        tabId= "WalletBangla";
    }
    else{
        payMode = "EWI";
        tabId="WalletIndia";
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

    String varEmailid = rb1.getString("VAR_EMAILID");
    String varCountry = rb1.getString("VAR_COUNTRY");
    String varPhoneno = rb1.getString("VAR_PHONENO");
    String varFirstName = rb1.getString("VAR_FIRSTNAME");
    String varLastName = rb1.getString("VAR_LASTNAME");
    String varNext = rb1.getString("VAR_NEXT");
%>


<div class="tab-pane" id="<%=tabId%>" style="height: 305px;" >
    <form id="walletindiaForm" class="form-style"  method="post">
        <%
            if("Y".equalsIgnoreCase(personalDetailDisplay)){
        %>
        <div class="tab" id="personalinfo" style="padding-top: 22px;height:auto;overflow:unset">
            <div id="emailPersonal">
                <div class="form-group has-float-label control-group-full" id="WIEmail">
                    <span class="input-icon"><i class="far fa-envelope"></i></span>
                    <input type="email" class="form-control input-control1" id="email_wi" placeholder=" " onchange="validateEmail(event.target.value ,'WIEmail')"
                           oninput="this.className = 'form-control input-control1'" name="emailaddr" value="<%=email%>" autofocus maxlength="50" autocomplete="off" />
                    <label for="email_wi" class="form-label"><%=varEmailid%></label>
                </div>
            </div>
            <%-- <div class="form-group has-float-label control-group-half">
                 <input type="text" class="form-control input-control1" id="firstname_wi" placeholder=" "
                        oninput="this.className = 'form-control input-control1'" name="firstname" value="<%=firstName%>" autofocus maxlength="50" autocomplete="off" />
                 <label for="firstname_wi" class="form-label"><%=varFirstName%></label>
             </div>
             <div class="form-group has-float-label control-group-half">
                 <input type="text" class="form-control input-control1" id="lastname_wi" placeholder=" "
                        oninput="this.className = 'form-control input-control1'" name="lastname" value="<%=lastName%>" autofocus maxlength="50" autocomplete="off" />
                 <label for="lastname_wi" class="form-label"><%=varLastName%></label>
             </div>--%>
            <%--            <div class="form-group has-float-label control-group-half" >
                            <div class="dropdown">
                                <input id="country_input_wi" class="form-control input-control1" placeholder=" " oninput="this.className = 'form-control input-control1'"
                                       onblur="pincodecc('country_input_wi','country_wi','phonecc_id_wi','phonecc_wi'); " onkeypress="return isLetterKey(event)" />
                                <label for="country_input_wi" class="form-label"><%=varCountry%></label>
                                <input type="hidden" id="country_wi"  name="country_input" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
                            </div>
                        </div>--%>
            <div id="mobilePersonal">
                <div class="form-group has-float-label control-group-half" style="width: 10% !important">
                    <input type="text" class="form-control input-control1" name="phone-CC" id="phonecc_id_wi" placeholder=" " onkeypress="return isNumberKey(event)"
                           onkeyup="onPasteNumCheck(event)" maxlength="3" onblur="setPhoneCC('phonecc_id_wi','phonecc_wi')" value="<%=phonecc%>"
                           oninput="this.className = 'form-control input-control1'"/>
                    <label for="phonecc_id_wi" class="form-label">CC</label>
                    <input type="hidden" id="phonecc_wi" name="phone-CC" value="<%=phonecc%>"/>
                </div>
                <div class="form-group has-float-label control-group-half" style="width: 3% !important;padding: 6px 0px;">
                    -
                </div>
                <div class="form-group has-float-label control-group-half" id="wiPhoneNum" style="width: 35% !important">
                    <span class="input-icon-half"><i class="fas fa-phone"></i> </span>
                    <input type="text" class="form-control input-control1" id="INphoneNum_wi" placeholder=" " name="telno" value="<%=phoneno%>" maxlength="10"
                           onchange="validateInPhone(event.target.value,'wiPhoneNum')"
                           onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" oninput="this.className = 'form-control input-control1'" />
                    <label for="INphoneNum_wi" class="form-label"><%=varPhoneno%></label>
                </div>
            </div>


            <%--            <script>
                            setCountry('<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>','country_input_wi');
                            pincodecc('country_input_wi','country_wi','phonecc_id_wi','phonecc_wi');
                        </script>--%>
        </div>
        <%
            }
        %>


        <div class="tab" style="height: 247px;" >
            <div class="list" id="wallets">

                <%
                    standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
                    HashMap paymentMap = standardKitValidatorVO.getMapOfPaymentCardType();
                    //   System.out.println("walletindia condition paymentMap:::"+paymentMap);
                    Map<String,String> bankWalletList    =null;
                    TreeMap<String,String> tempbankListMap    = null;
                    HashMap <String,TerminalVO>limitRoutingTerminalMap  = standardKitValidatorVO.getTerminalMapLimitRouting();
                    TerminalVO terminalVO2=null;
                    String accountid ="";
                    String gateway = "";
                    String terminalid = "";
                    String seprator="_";
                    String walletName = "";
                    String paymodeId = GatewayAccountService.getPaymentId(payMode.toString());

                    List<String> cardList = (List<String>)paymentMap.get(paymodeId);
                    Set walletList = new LinkedHashSet<>();
                    Set walletTerminalList = new LinkedHashSet<>();
                    String aggregator = "";
                    Set<String> keys2               = null;
                    for(String cardId : cardList)
                    {
                        String cardName = GatewayAccountService.getCardType(cardId);

                        walletList.add(cardName);
                        walletTerminalList.add(cardName);
                    }
                    String propName = "";
                    for(String cardId : cardList)
                    {
                        String cardName = GatewayAccountService.getCardType(cardId);
                        //   System.out.println("inside for loop walletindia condition cardName:::"+cardName);

                        if(cardName.contains("WALLETS") || cardName.contains("Wallets"))
                        {
                            propName = cardName;
                            break;
                        }
                    }
                    terminalid = standardKitValidatorVO.getTerminalId();
                    if(limitRoutingTerminalMap!=null)
                    {
                        terminalVO2   = (TerminalVO) limitRoutingTerminalMap.get(terminalid);
                        accountid          = terminalVO2.getAccountId();
                        gateway          = terminalVO2.getGateway();
                        System.out.println("inside if wallet india  accountid--->"+accountid+" gateway::"+gateway);

                    }
                    //  ResourceBundle rb = LoadProperties.getProperty("com.directi.pg."+propName);
                    if(limitRoutingTerminalMap != null && limitRoutingTerminalMap.size()>1)
                    {

                        gateway         ="gateway";
                        Iterator keys   = limitRoutingTerminalMap.keySet().iterator();
                        HashMap <String,String>combineBankMap=new HashMap();
                        bankWalletList   = new HashMap<>();

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
                                System.out.println("tempwalletListMap.containsKey(cardtype+seprator+accountid)  >>>>> "+bankNameMap.containsKey(cardtype+seprator+accountid));

                                if(bankNameMap.containsKey(cardtype+seprator+accounyid)){
                                    System.out.println("inside cardtype accountid tempwalletListMap  >>>>> "+cardtype+seprator+accountid);

                                    tempbankListMap = bankNameMap.get(cardtype+seprator+accounyid);
                                    System.out.println("inside accountid tempwalletListMap  >>>>> "+tempbankListMap);

                                }
                                else{
                                    tempbankListMap = bankNameMap.get(cardtype);
                                    System.out.println("tempwalletListMap  >>>>> "+tempbankListMap);

                                }
                            }
                            else if(bankNameMap.containsKey(cardtype))
                                  tempbankListMap   = bankNameMap.get(cardtype);

                            if(tempbankListMap!=null){
                                System.out.println("tempwalletListMap  >>>>> "+tempbankListMap);

                                for (String key1 : tempbankListMap.keySet()) {

                                    bankWalletList.put(key1, tempbankListMap.get(key1)+"-"+cardtype+"-"+terminal+"-"+accounyid);
                                }
                                tempbankListMap=new TreeMap<>();
                            }
                        }
                        keys2   = bankWalletList.keySet();
                    }
                    else
                    {
                        if(gateway.equalsIgnoreCase(SafexPayPaymentGateway.GATEWAY_TYPE)){
                            if(bankNameMap.containsKey(propName+seprator+accountid))  {
                                bankWalletList   = bankNameMap.get(propName+seprator+accountid);
                                System.out.println("inside else condition with account id bankWalletList  >>>>> "+bankWalletList);
                            }
                            else{
                                bankWalletList   = bankNameMap.get(propName);
                            }
                        }
                        else if (bankNameMap.containsKey(propName)){
                            bankWalletList   = bankNameMap.get(propName);
                        }
                        keys2 = bankWalletList.keySet();
                    }




                    if(keys2!=null ){
                        for (String key:keys2)
                        {

                            System.out.println("key---->"+key);
                            String bankName = key.replaceAll("\\_"," ");//wallet Name
                            System.out.println("inside while replase key------->"+key);
                            String walletValue = bankWalletList.get(key);//PgID
                            System.out.println("inside while replase value------->"+walletValue);
                            if(!walletList.contains(bankName))
                                walletName = propName;

                %>
                <div class="item" onclick="selectedWallet()">
                    <input type="radio" class="form-control input-control1" name="paymentBrand" id="<%=key%>" style="display: none"
                           oninput="this.className = 'form-control input-control1'" value="<%=walletValue%>-<%=walletName%>-<%=key%>" />
                    <label for="<%=key%>" >
                        <%--<span class="checkbox"></span>--%>
                        <img class="wallet-image" src="/images/merchant/images/walletIn/<%=key.toUpperCase()%>.png" />
                        <span class="wallet-title"><%=key%></span>
                    </label>
                </div>
                <%
                    }
                }
                else{
                    for (String key:keys2)
                    {

                        System.out.println("key---->"+key);
                        String bankName = key.replaceAll("\\_"," ");//Bank Name
                        System.out.println("inside while replase key------->"+key);
                        //  String value = rb.getString(key);//PgID
                        String value = bankWalletList.get(key);//PgID
                        System.out.println("inside while replase value------->"+value);
                        if(!walletList.contains(bankName))
                            walletName = propName;

                %>
                <div class="item" onclick="selectedWallet()">
                    <input type="radio" class="form-control input-control1" name="paymentBrand" id="<%=key%>" style="display: none"
                           oninput="this.className = 'form-control input-control1'" value="<%=key%>-<%=walletName%>-<%=key%>" />
                    <label for="<%=key%>" >
                        <%--<span class="checkbox"></span>--%>
                        <img class="wallet-image" src="/images/merchant/images/walletIn/<%=key.toUpperCase()%>.png" />
                        <span class="wallet-title"><%=key%></span>
                    </label>
                </div>
                <%
                        }
                    }
                %>
            </div>
        </div>

        <div style="overflow:hidden;" >
            <div style="float:right;">
                <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'walletindiaForm')" >
                    <i class="fas fa-angle-left"></i>
                </div>
            </div>
        </div>
        <div class="pay-btn" >
            <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'walletindiaForm')">
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