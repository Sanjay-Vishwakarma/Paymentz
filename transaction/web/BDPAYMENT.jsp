<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.payment.aamarpay.AamarPayResponseVO" %>
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

  String paymodeId  = "55";
  String payMode    = "BD";

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


<div id="BDPAYMENTOption" class="option-class">
  <ul class="nav nav-tabs" id="BDPAYMENTTab">

    <%
      List<String> cardList = (List<String>)paymentMap.get(paymodeId);

      for(String cardId : cardList)
      {
        String cardName = GatewayAccountService.getCardType(cardId);
        String cardImg  = cardName+".png";
        cardName        = cardName.replaceAll(" ", "_");
    %>
    <li class="tabs-li-wallet" onclick="BDPAYMENTHideShow('<%=cardName%>','<%=rb.getString(cardName)%>')">
      <a href="#<%=cardName%>"  data-toggle="tab" title="<%=cardName%>" aria-expanded="false">
        <img class="images-style" src="/images/merchant/<%=cardImg%>" alt="<%=cardName%>">
        <div class="label-style"><%=rb.getString(cardName)%></div>
      </a>
    </li>

    <%
      }
    %>
  </ul>
</div>

<div class="tab-content">
    <%
      for(String cardId : cardList)
    {
      String cardName = GatewayAccountService.getCardType(cardId);
      String cn       = cardName;
      cardName        = cardName.replaceAll(" ", "_");
  %>
  <div class="tab-pane" id="<%=cardName%>">
    <form id="<%=cardName%>Form" name="<%=cardName%>Form" class="form-style" method="post" action="/transaction/SingleCallCheckout">
      <input type="hidden" name="paymentBrand" id="paymentBrand" value="<%=cn%>">
      <jsp:include page="requestParameters.jsp">
        <jsp:param name="paymentMode" value="<%=payMode%>" />
      </jsp:include>
    </form>
  </div>
    <%
    }
  %>

  <%--<%
    List<AamarPayResponseVO> paymentList = null;

    if(request.getAttribute("BankURLList") != null){
      paymentList = (List<AamarPayResponseVO>) request.getAttribute("BankURLList");
      System.out.println("paymentList ---------> "+paymentList.size());

    }

    if(paymentList != null && paymentList.size() > 0){
  %>

  <div class="modal show" style="display:inline-block">
    <div class="modal-dialog modal-sm modal-dialog-centered">
      <div class="modal-content">
        <div id="target" class="mainDiv" >
          </div>
      </div>
    </div>
  </div>

  <%}%>--%>
</div>
