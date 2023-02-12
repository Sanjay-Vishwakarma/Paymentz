<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 6/23/2018
  Time: 7:17 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%
    Functions functions = new Functions();

    String email = "";
    CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
    if(functions.isValueNull(standardKitValidatorVO.getAddressDetailsVO().getEmail())){
        email = ESAPI.encoder().encodeForHTML(standardKitValidatorVO.getAddressDetailsVO().getEmail());
    }
%>


<div class="tab-pane" id="WalletIndia" style="height: 305px;" >
    <form id="walletindiaForm" class="form-style"  method="post">
        <div class="tab" style="height: 247px;" >
            <div class="list" id="wallets">


                <div class="item" onclick="selectedWallet()">
                    <input type="radio" class="form-control input-control1" name="paymentBrand" id="bKash" style="display: none"
                           oninput="this.className = 'form-control input-control1'" value="bKash" />
                    <label for="bKash" >
                        <%--<span class="checkbox"></span>--%>
                        <img class="wallet-image" src="/images/merchant/images/walletIn/taka/bKash.png" />
                        <span class="wallet-title">bKash</span>
                    </label>
                </div>
                <div class="item" onclick="selectedWallet()">
                    <input type="radio" class="form-control input-control1" name="paymentBrand" id="dmoney" style="display: none"
                           oninput="this.className = 'form-control input-control1'" value="dmoney" />
                    <label for="dmoney" >
                        <%--<span class="checkbox"></span>--%>
                        <img class="wallet-image" src="/images/merchant/images/walletIn/taka/dmoney.png" />
                        <span class="wallet-title">dmoney</span>
                    </label>
                </div>
                <div class="item" onclick="selectedWallet()">
                    <input type="radio" class="form-control input-control1" name="paymentBrand" id="gpay" style="display: none"
                           oninput="this.className = 'form-control input-control1'" value="gpay" />
                    <label for="gpay" >
                        <%--<span class="checkbox"></span>--%>
                        <img class="wallet-image" src="/images/merchant/images/walletIn/taka/gpay.png" />
                        <span class="wallet-title">gpay</span>
                    </label>
                </div>
                <div class="item" onclick="selectedWallet()">
                    <input type="radio" class="form-control input-control1" name="paymentBrand" id="iPay" style="display: none"
                           oninput="this.className = 'form-control input-control1'" value="iPay" />
                    <label for="iPay" >
                        <%--<span class="checkbox"></span>--%>
                        <img class="wallet-image" src="/images/merchant/images/walletIn/taka/iPay.png" />
                        <span class="wallet-title">iPay</span>
                    </label>
                </div>
                <div class="item" onclick="selectedWallet()">
                    <input type="radio" class="form-control input-control1" name="paymentBrand" id="nexuspay" style="display: none"
                           oninput="this.className = 'form-control input-control1'" value="nexuspay" />
                    <label for="nexuspay" >
                        <%--<span class="checkbox"></span>--%>
                        <img class="wallet-image" src="/images/merchant/images/walletIn/taka/nexuspay.png" />
                        <span class="wallet-title">nexuspay</span>
                    </label>
                </div>
                <div class="item" onclick="selectedWallet()">
                    <input type="radio" class="form-control input-control1" name="paymentBrand" id="rocket" style="display: none"
                           oninput="this.className = 'form-control input-control1'" value="rocket" />
                    <label for="rocket" >
                        <%--<span class="checkbox"></span>--%>
                        <img class="wallet-image" src="/images/merchant/images/walletIn/taka/rocket.png" />
                        <span class="wallet-title">rocket</span>
                    </label>
                </div>
                <div class="item" onclick="selectedWallet()">
                    <input type="radio" class="form-control input-control1" name="paymentBrand" id="upay" style="display: none"
                           oninput="this.className = 'form-control input-control1'" value="upay" />
                    <label for="upay" >
                        <%--<span class="checkbox"></span>--%>
                        <img class="wallet-image" src="/images/merchant/images/walletIn/taka/upay.png" />
                        <span class="wallet-title">upay</span>
                    </label>
                </div>


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
                Next
            </div>
        </div>
        <input type="hidden" name="country_input" id="country_input" value="IN">
        <input type="hidden" name="emailaddr" value="<%=email%>">

    </form>

</div>
