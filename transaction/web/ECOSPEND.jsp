<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.directi.pg.Functions" %>
<%--
  Created by IntelliJ IDEA.
  User: Rihen
  Date: 9/7/2021
  Time: 3:58 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  CommonValidatorVO standardKitValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  HashMap paymentMap = standardKitValidatorVO.getMapOfPaymentCardType();

  String paymodeId = "46";
  String payMode = "ES";

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


<div id="EcospendOption" class="option-class">
  <ul class="nav nav-tabs" id="EcospendTab">

    <%

      List<String> cardList = (List<String>)paymentMap.get(paymodeId);
      for(String cardId : cardList)
      {
        String cardName = GatewayAccountService.getCardType(cardId);
        String cardImg = cardName+".png";
    %>
    <li class="tabs-li-wallet" onclick="ecospendHideShow('<%=cardName%>','<%=rb.getString(cardName)%>')">
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
<div class="tab-content">
  <input type="hidden" id="pay_ModeId" value="<%=paymodeId%>">
  <input type="hidden" id="card_TypeId" value="<%=standardKitValidatorVO.getCardType()%>">
  <%
  if(cardList.contains("127"))
  {
  %>
  <form method="post" name="STANDINGORDERSform_ES" id="STANDINGORDERSform_ES" action="/transaction/SingleCallCheckout">
    <input type="hidden" name="country_input" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
    <input type="hidden" name="currency" value="<%=standardKitValidatorVO.getTransDetailsVO().getCurrency()%>">

    <input type="hidden" name="paymentBrand" id="paymentBrand" value="STANDINGORDERS">
    <jsp:include page="requestParameters.jsp">
      <jsp:param name="paymentMode" value="<%=payMode%>" />
    </jsp:include>
  </form>
  <%
    }
    if(cardList.contains("128"))
    {
  %>
  <form method="post" name="INSTANTPAYMENTform_ES" id="INSTANTPAYMENTform_ES" action="/transaction/SingleCallCheckout">
    <input type="hidden" name="country_input" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
    <input type="hidden" name="currency" value="<%=standardKitValidatorVO.getTransDetailsVO().getCurrency()%>">

    <input type="hidden" name="paymentBrand" id="paymentBrand" value="INSTANTPAYMENT">
    <jsp:include page="requestParameters.jsp">
      <jsp:param name="paymentMode" value="<%=payMode%>" />
    </jsp:include>
  </form>
  <%
    }
    if(cardList.contains("129")){
  %>
  <form method="post" name="PAYBYLINKform_ES" id="PAYBYLINKform_ES" action="/transaction/SingleCallCheckout">
    <input type="hidden" name="country_input" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
    <input type="hidden" name="currency" value="<%=standardKitValidatorVO.getTransDetailsVO().getCurrency()%>">

    <input type="hidden" name="paymentBrand" id="paymentBrand" value="PAYBYLINK">
    <jsp:include page="requestParameters.jsp">
      <jsp:param name="paymentMode" value="<%=payMode%>" />
    </jsp:include>
  </form>
  <%
    }
    if(cardList.contains("130")){
  %>
  <form method="post" name="SCHEDULEDPAYMENTform_ES" id="SCHEDULEDPAYMENTform_ES" action="/transaction/SingleCallCheckout">
    <input type="hidden" name="country_input" value="<%=standardKitValidatorVO.getAddressDetailsVO().getCountry()%>">
    <input type="hidden" name="currency" value="<%=standardKitValidatorVO.getTransDetailsVO().getCurrency()%>">

    <input type="hidden" name="paymentBrand" id="paymentBrand" value="SCHEDULEDPAYMENT">
    <jsp:include page="requestParameters.jsp">
      <jsp:param name="paymentMode" value="<%=payMode%>" />
    </jsp:include>
  </form>
  <%
    }
  %>
</div>
<script>
var paymode = $("#pay_ModeId").val();
var cardType = $("#card_TypeId").val();
  if(paymode == "46" && (cardType == "127" || cardType == "128" || cardType == "129" || cardType == "130"))
  {
    $(".images-style").click();
  }
</script>