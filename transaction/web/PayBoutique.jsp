<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.directi.pg.Functions" %>
<%--
  Created by IntelliJ IDEA.
  User: Rihen
  Date: 2/14/2020
  Time: 5:43 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  HashMap paymentMap = standardKitValidatorVO.getMapOfPaymentCardType();

  String paymodeId = "29";
  String payMode = "PB";

  Functions functions = new Functions();


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
%>


<div id="PayBoutiqueOption" class="option-class">
  <ul class="nav nav-tabs" id="PayBoutiqueTab">

    <%

      List<String> cardList = (List<String>)paymentMap.get(paymodeId);
      for(String cardId : cardList)
      {
        String cardName = GatewayAccountService.getCardType(cardId);
        String cardImg = cardName+".png";
    %>
    <li class="tabs-li-wallet" onclick="payBoutiqueHideShow('<%=cardName%>','<%=rb.getString(cardName)%>')">
      <a href="#<%=cardName%>"  data-toggle="tab" title="<%=cardName%>" aria-expanded="false">
        <img class="images-style" src="/images/merchant/images/<%=cardImg%>" alt="<%=cardName%>">
        <div class="label-style"><%=rb.getString(cardName)%></div>
      </a>
    </li>

    <%
      }
    %>
  </ul>
</div>





<form method="post" name="pbForm" id="pbForm" action="/transaction/SingleCallCheckout">

  <input type="hidden" name="paymentBrand" id="paymentBrand" value="PayBoutique">
  <jsp:include page="requestParameters.jsp">
    <jsp:param name="paymentMode" value="<%=payMode%>" />
  </jsp:include>
</form>