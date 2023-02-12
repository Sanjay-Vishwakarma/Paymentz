<%@ page import="com.payment.validators.vo.CommonValidatorVO" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ page import="com.manager.vo.MarketPlaceVO" %>
<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 6/22/2018
  Time: 6:40 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>

<%
  CommonValidatorVO commonValidatorVO = (CommonValidatorVO) request.getAttribute("transDetails");
  MerchantDetailsVO merchantDetailsVO = commonValidatorVO.getMerchantDetailsVO();
  //System.out.println("req pm---"+request.getParameter("paymode"));
  //System.out.println("req pb---"+request.getParameter("paymentBrand"));
  /*String PM_PB = request.getParameter("paymode")+"-"+request.getParameter("paymentBrand");
  String currency = commonValidatorVO.getTransDetailsVO().getCurrency();
  System.out.println("PM_PB---"+PM_PB+"-"+currency);*/

  //HashMap terminalMap = commonValidatorVO.getTerminalMap();
  //System.out.println("terminal map---"+terminalMap);
  //TerminalVO terminalVO = (TerminalVO)terminalMap.get(PM_PB+"-"+currency);
  //System.out.println("terminal accid---"+terminalVO.getAccountId());

  String ctoken = (String) session.getAttribute("ctoken");

%>
<input type="hidden" name="trackingid" value="<%=commonValidatorVO.getTrackingid()%>" />
<%--<input type="hidden" name="paymenttype" value="<%=commonValidatorVO.getPaymentMode()%>" />
<input type="hidden" name="cardtype" value="<%=commonValidatorVO.getCardType()%>" />
<input type="hidden" name="paymentBrand" value="<%=commonValidatorVO.getPaymentMode()%>" />
<input type="hidden" name="paymentMode" value="<%=commonValidatorVO.getPaymentBrand()%>" />--%>

<%--<input type="hidden" id="paymentBrand" name="paymentBrand" value="SKRILL">--%>
<input type="hidden" name="paymentMode" value="<%=request.getParameter("paymentMode")%>">
<input type="hidden" name="ctoken" value="<%=ctoken%>">
<%--<input type="hidden" id="consentStmnt" name="consentStmnt" value="<%=consent1+consent2+consent3%>">--%>
<!--Merchant VO-->
<input type="hidden" name="toid" value="<%=merchantDetailsVO.getMemberId()%>" />
<input type="hidden" name="clkey" value="<%=merchantDetailsVO.getKey()%>">
<input type="hidden" name="partnerId" value="<%=merchantDetailsVO.getPartnerId()%>">
<input type="hidden" name="isPoweredBy" value="<%=merchantDetailsVO.getPoweredBy()%>">
<input type="hidden" name="onlineFraudCheck" value="<%=merchantDetailsVO.getOnlineFraudCheck()%>">
<input type="hidden" name="multiCurrencySupport" value="<%=merchantDetailsVO.getMultiCurrencySupport()%>">
<input type="hidden" name="binRouting" value="<%=merchantDetailsVO.getBinRouting()%>">
<input type="hidden" name="merchantlogoname" value="<%=session.getAttribute("merchantLogoName")%>">
<input type="hidden" name="partnerlogoname" value="<%=merchantDetailsVO.getLogoName()%>">
<input type="hidden" name="partnerlogoflag" value="<%=merchantDetailsVO.getPartnerLogoFlag()%>">
<input type="hidden" name="merchantlogoflag" value="<%=merchantDetailsVO.getMerchantLogo()%>">
<input type="hidden" name="supportSection" value="<%=merchantDetailsVO.getSupportSection()%>">
<input type="hidden" name="street" value="<%=commonValidatorVO.getAddressDetailsVO().getStreet()%>">
<input type="hidden" name="city" value="<%=commonValidatorVO.getAddressDetailsVO().getCity()%>">
<input type="hidden" name="state" value="<%=commonValidatorVO.getAddressDetailsVO().getState()%>">
<input type="hidden" name="zip" value="<%=commonValidatorVO.getAddressDetailsVO().getZipCode()%>">
<input type="hidden" name="country_input" value="<%=commonValidatorVO.getAddressDetailsVO().getCountry()%>">
<input type="hidden" name="telno" value="<%=commonValidatorVO.getAddressDetailsVO().getPhone()%>">
<input type="hidden" name="merchant_address" value="<%=merchantDetailsVO.getAddress()%>">
<input type="hidden" name="merchant_city" value="<%=merchantDetailsVO.getCity()%>">
<input type="hidden" name="merchant_state" value="<%=merchantDetailsVO.getState()%>">
<input type="hidden" name="merchant_zip" value="<%=merchantDetailsVO.getZip()%>">
<input type="hidden" name="merchant_country" value="<%=merchantDetailsVO.getCountry()%>">
<input type="hidden" name="merchant_telno" value="<%=merchantDetailsVO.getTelNo()%>">
<input type="hidden" name="company_name" value="<%=merchantDetailsVO.getCompany_name()%>">
<input type="hidden" name="ip" value="<%=commonValidatorVO.getAddressDetailsVO().getIp()%>">
<input type="hidden" name="customerIp" value="<%=commonValidatorVO.getAddressDetailsVO().getCardHolderIpAddress()%>">
<input type="hidden" name="invoicenumber" value="<%=commonValidatorVO.getInvoiceId()%>">
<input type="hidden" name="language" value="<%=commonValidatorVO.getAddressDetailsVO().getLanguage()%>">
<input type="hidden" name="device" value="<%=commonValidatorVO.getDevice()%>">
<input type="hidden" name="customerId" value="<%=commonValidatorVO.getCustomerId()%>">
<%--<input type="hidden" id="customerId" name="customerId" value="<%=merchantDetailsVO.getCustomerId()%>">--%>
<input type="hidden" name="marketPlace" value="<%=merchantDetailsVO.getMarketPlace()%>">
<input type="hidden" name="requestfirstname" value="<%=commonValidatorVO.getAddressDetailsVO().getFirstname()%>">
<input type="hidden" name="requestlastname" value="<%=commonValidatorVO.getAddressDetailsVO().getLastname()%>">
<input type="hidden" name="requestemailaddr" value="<%=commonValidatorVO.getAddressDetailsVO().getEmail()%>">
<input type="hidden" name="requestAccountid" value="<%=commonValidatorVO.getAccountId()%>">
<input type="hidden" name="requestAccountid" value="<%=commonValidatorVO.getMerchantDetailsVO().getCheckoutTimerFlag()%>">
<input type="hidden" name="requestAccountid" value="<%=commonValidatorVO.getMerchantDetailsVO().getCheckoutTimerTime()%>">
<input type="hidden" name="limitRouting" value="<%=commonValidatorVO.getMerchantDetailsVO().getLimitRouting()%>">
<%
  if(commonValidatorVO.getReserveField2VO() != null)
  {
%>
<input type="hidden" name="reservedField1" value="<%=commonValidatorVO.getReserveField2VO().getReservefield1()%>">
<input type="hidden" name="reservedField2" value="<%=commonValidatorVO.getReserveField2VO().getReservefield2()%>">
<input type="hidden" name="reservedField3" value="<%=commonValidatorVO.getReserveField2VO().getReservefield3()%>">
<input type="hidden" name="reservedField4" value="<%=commonValidatorVO.getReserveField2VO().getReservefield4()%>">
<input type="hidden" name="reservedField5" value="<%=commonValidatorVO.getReserveField2VO().getReservefield5()%>">
<%
  }
%>

<input type="hidden" name="fingerprints" value="">

<%
  if(!"N".equalsIgnoreCase(merchantDetailsVO.getMarketPlace()) && commonValidatorVO.getMarketPlaceVOList()!=null)
  {
    MarketPlaceVO marketPlaceVO=new MarketPlaceVO();
    for(int i=0;i<commonValidatorVO.getMarketPlaceVOList().size();i++)
    {
      marketPlaceVO=commonValidatorVO.getMarketPlaceVOList().get(i);
     %>
<input type="hidden" id="marketPlaceTrackingid[]" name="marketPlaceTrackingid[]" value="<%=marketPlaceVO.getTrackingid()%>">
<%
    }
  }
%>

</body>
</html>