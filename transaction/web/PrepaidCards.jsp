<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>

<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 5/24/2018
  Time: 5:41 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  HashMap paymentMap = standardKitValidatorVO.getMapOfPaymentCardType();

  ResourceBundle rb6 = null;
  rb6 = LoadProperties.getProperty("com.directi.pg.CheckouLanguage");

  String varAstropay=rb6.getString("VAR_ASTROPAY");
  String varPersonalinfo=rb6.getString("VAR_PERSONALINFO");
  String varEmailid=rb6.getString("VAR_EMAILID");
  String varPhonecc=rb6.getString("VAR_PHONECC");
  String varPhoneno=rb6.getString("VAR_PHONENO");
  String varNext=rb6.getString("VAR_NEXT");
  String varFirstname=rb6.getString("VAR_FIRSTNAME");
  String varLastname=rb6.getString("VAR_LASTNAME");

%>


<div id="PrepaidCardsOption" class="option-class">
  <ul class="nav nav-tabs" id="PrepaidCardTab">

    <%
      for(Object paymentId : paymentMap.keySet())
      {
        List<String> cardList = (List<String>)paymentMap.get(paymentId);
        for(String cardId : cardList)
        {
          String cardName = GatewayAccountService.getCardType(cardId);
          String cardImg = cardName+".png";
    %>
        <li class="tabs-li-wallet" onclick="prepaidCardHideShow('<%=cardName%>','<%=rb6.getString(cardName)%>')">
          <a href="#<%=cardName%>"  data-toggle="tab" title="profile" aria-expanded="false">
            <img class="images-style" src="/images/merchant/images/prepaidcards/<%=cardImg%>" alt="<%=cardName%>">
            <div class="label-style"><%=cardName%></div>
          </a>
        </li>
    <%
        }
      }
    %>
    <%--<li class="tabs-li-wallet" onclick="prepaidCardHideShow('astropay')">
      <a href="#astropay"  data-toggle="tab" title="profile" aria-expanded="false">
        <img class="images-style" src="/images/merchant/images/prepaidcards/ASTROPAY.png">
        <div class="label-style"><%=varAstropay%></div>
      </a>
    </li>--%>
  </ul>
</div>
<div class="tab-content">
  <div class="tab-pane" id="ASTROPAY">
    <form id="astropayForm" class="form-style" >
      <div class="tab"> <p class="form-header"><%=varPersonalinfo%></p>
        <div class="form-group has-float-label control-group-full" id="AstropayEmail">
          <span class="input-icon"><i class="far fa-envelope"></i></span>
          <input type="email" class="form-control input-control1" id="astropayemail" placeholder=" " onfocusout="validateEmail(event.target.value ,'AstropayEmail')"
                 oninput="this.className = 'form-control input-control1'"  name="emailaddr" />
          <label for="astropayemail" class="form-label"><%=varEmailid%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="astropayfirstname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="firstname" />
          <label for="astropayfirstname" class="form-label"><%=varFirstname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="astropaylastname" placeholder=" "
                 oninput="this.className = 'form-control input-control1'"  name="lastname" />
          <label for="astropaylastname" class="form-label"><%=varLastname%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phone-CC" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)" maxlength="3"
                 oninput="this.className = 'form-control input-control1'"  name="telnocc" />
          <label for="phone-CC" class="form-label"><%=varPhonecc%></label>
        </div>
        <div class="form-group has-float-label control-group-half">
          <input type="text" class="form-control input-control1" id="phoneno" placeholder=" " onkeypress="return isNumberKey(event)" onkeyup="onPasteNumCheck(event)"
                 oninput="this.className = 'form-control input-control1'"  name="telno" />
          <label for="phoneno" class="form-label"><%=varPhoneno%></label>
        </div>
      </div>
      <div style="overflow:hidden;" >
        <div style="float:right;">
          <div class="form-btn prev-btn" id="prevBtn"  onclick="nextPrev(-1 ,'astropayForm')" >
            <i class="fas fa-angle-left"></i>
          </div>
        </div>
      </div>
      <div class="form-group terms-checkbox hide" id="terms">
        <input type="checkbox" class="" style="width: 5%" id="TC" >
        <label for="TC" style="font-size: 85%;position: absolute;margin: 0px;">
          &nbsp; I confirm that I’ve read and agreed to the <b> Privacy Policy </b> and <b> Terms of Use </b>.
        </label>
      </div>
      <div class="pay-btn" >
        <div class="form-btn pay-button" id="nextBtn"  onclick="nextPrev(1 ,'astropayForm')">
          <%=varNext%>
        </div>
      </div>
    </form>
  </div>
</div>