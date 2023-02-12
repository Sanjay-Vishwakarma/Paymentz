<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
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
%>

<div class="tab-pane" id="NetBankingBangladesh" style="height: 305px;" >
    <form id="netbankingindiaForm" class="form-style" method="post">
        <div class="tab" style="padding: 0px;height: auto;">

         <%--   <%
                HashMap paymentMap = standardKitValidatorVO.getMapOfPaymentCardType();
                String payMode = "NBI";
                String paymodeId = GatewayAccountService.getPaymentId(payMode.toString());

                List<String> cardList = (List<String>)paymentMap.get(paymodeId);
                Set<String> bankList = new HashSet<>();
                Set<String> terminalBank = new HashSet<>();
                bankList.add("SBI");
                bankList.add("HDFC");
                bankList.add("ICICI");
                bankList.add("AXIS");
                bankList.add("KOTAK");
                bankList.add("YES BANK");

                String propName = "";
                for(String cardId : cardList)
                {
                    String cardName = GatewayAccountService.getCardType(cardId);
                    if(cardName.contains("BANKS"))
                    {
                        propName = cardName;
                        break;
                    }
                }

                ResourceBundle rb = LoadProperties.getProperty("com.directi.pg."+propName);
            %>--%>
            <div <%--style="height: 178px"--%>>
                <ul class="nav nav-tabs" id="NetBankingIndiaTab">
                    <li class="tabs-li-bank" onclick="selectBank('HSBC')" id="HSBC">
                        <a title="" >
                            <img  class="bank-image" src="/images/merchant/images/bank/taka/HSBC.png">
                            <div class="bank-label">HSBC</div>
                        </a>
                    </li>
                    <li class="tabs-li-bank" onclick="selectBank('DutchBanglaBank')" id="DutchBanglaBank">
                        <a title="" >
                            <img  class="bank-image" src="/images/merchant/images/bank/taka/DutchBanglaBank.png">
                            <div class="bank-label">Dutch-Bangla Bank</div>
                        </a>
                    </li>
                    <li class="tabs-li-bank" onclick="selectBank('SonaliBank')" id="SonaliBank">
                        <a title="" >
                            <img  class="bank-image" src="/images/merchant/images/bank/taka/SonaliBank.png">
                            <div class="bank-label">Sonali Bank</div>
                        </a>
                    </li>
                    <li class="tabs-li-bank" onclick="selectBank('IBBL')" id="IBBL">
                        <a title="" >
                            <img  class="bank-image" src="/images/merchant/images/bank/taka/IBBL.png">
                            <div class="bank-label">IBBL</div>
                        </a>
                    </li>
                    <li class="tabs-li-bank" onclick="selectBank('GrameenBank')" id="GrameenBank">
                        <a title="" >
                            <img  class="bank-image" src="/images/merchant/images/bank/taka/GrameenBank.png">
                            <div class="bank-label">Grameen Bank</div>
                        </a>
                    </li>
                    <li class="tabs-li-bank" onclick="selectBank('JanataBank')" id="JanataBank">
                        <a title="" >
                            <img class="bank-image" src="/images/merchant/images/bank/taka/JanataBank.png">
                            <div class="bank-label">Janata Bank</div>
                        </a>
                    </li>
                </ul>
            </div>
            <div class="form-group has-float-label control-group-bank" >
                <select name="paymentBrand" class="form-control input-control1" id="netBankingBankName" onchange="dropdownbank(this)">
                    <option value="">Select a Bank Name</option>
                    <option value="HSBC">HSBC</option>
                    <option value="DutchBanglaBank">Dutch-Bangla Bank</option>
                    <option value="SonaliBank">Sonali Bank</option>
                    <option value="IBBL">Islami Bank Bangladesh Limited</option>
                    <option value="GrameenBank">Grameen Bank</option>
                    <option value="JanataBank">Janata Bank</option>
                    <option value="Standard Chartered Bank">Standard Chartered Bank</option>
                    <option value="Prime Bank Limited">Prime Bank Limited</option>
                    <option value="Habib Bank Limited">Habib Bank Limited</option>
                    <option value="State Bank of India">State Bank of India</option>
                </select>
                <label for="netBankingBankName" style="color:#757575">Bank Name</label>
            </div>
        </div>

        <div style="overflow:hidden;" >
            <div style="float:right;">
                <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'netbankingindiaForm')" >
                    <i class="fas fa-angle-left"></i>
                </div>
            </div>
        </div>
        <div class="pay-btn" >
            <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'netbankingindiaForm')">
                Next
            </div>
        </div>

    </form>

</div>
