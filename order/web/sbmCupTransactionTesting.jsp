<%--
<%@ page import="java.io.BufferedReader" %>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.OutputStreamWriter" %>
<%@ page import="java.net.HttpURLConnection" %>
<%@ page import="java.net.URL" %>
<%@ page import="java.net.URLConnection" %>
<%@ page import="java.util.StringTokenizer" %>
<%@ page import="ru.bpc.Plugin" %>
<%@ page import="ru.bpc.message.RegisterRequest" %>
<%@ page import="ru.bpc.message.RegisterAndPayRequest" %>
<%@ page import="ru.bpc.message.RegisterAndPayResponse" %>
<%@ page import="ru.bpc.message.RegisterResponse" %>
<%@ page import="ru.bpc.message.OrderStatusRequest" %>
<%@ page import="ru.bpc.message.OrderStatusResponse" %>
&lt;%&ndash;
  Created by IntelliJ IDEA.
  User: admin
  Date: 1/9/13
  Time: 12:22 AM
  To change this template use File | Settings | File Templates.
&ndash;%&gt;
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Initial Transaction Request</title>

</head>
<body>

<%
    /*Plugin p = Plugin.newInstance("PROCESS-API.properties");*/
    Plugin p = Plugin.newInstance("PAYMENT-API.properties");
    /*RegisterRequest rr = new RegisterRequest();
    rr.setCurrency("344");
    //rr.set
    rr.setMerchantOrderNumber("78351236");
     rr.setAmount("12.33");
     rr.setLanguage("en");
     rr.setReturnUrl("http://localhost:8081/first.jsp");
     rr.setDescription("This is my first Order");
     rr.setCup();
     System.out.println("IS CUP = " + rr.getIsCup());
     RegisterResponse regResp = p.registerRequest(rr);
     Integer i = 1;
     System.out.println("response order id =" +regResp.getOrderId());

     System.out.println("Redirect Form URL ="+regResp.getFormUrl());

     response.sendRedirect(regResp.getFormUrl());*/
      //
    RegisterAndPayRequest rr = new RegisterAndPayRequest();
    rr.setCurrency("810");
    rr.setMerchantOrderNumber("eu1234567");
    rr.setAmount("101.12");
    rr.setLanguage("en");
    rr.setReturnUrl("http://unesistedurlmerchant.ru/finish.html");
    rr.setDescription("");
    rr.setPan("4111111111111111");
    rr.setCvc("123");
    rr.setExpiration("201512");
    rr.setCardholder("Va Kee");
    RegisterAndPayResponse resp = p.registerAndPay(rr);
    System.out.println("Final Order ID ="+resp.getOrderId());
    out.println("Final Order ID ="+resp.getOrderId());
    ///

/* OrderStatusRequest orderReq = new OrderStatusRequest();
 orderReq.setLanguage("en");
 orderReq.setOrderId(regResp.getOrderId());
 OrderStatusResponse orderResp = p.getOrderStatus(orderReq);
 System.out.println("Final Order Status ="+orderResp.getOrderStatus());
 out.println("Final Order Status ="+orderResp.getOrderStatus());*/

%>
&lt;%&ndash;<form action="https://test.paymentgate.ru/payment/rest/register.do" method="get">
    <input type="hidden" name="userName" value="PAYMENT-API">
    <input type="hidden" name="password"  value="Password1!">
    <input type="hidden" name="orderNumber" value="123" >
    <input type="hidden" name="amount" value="11.33" >
    <input type="hidden" name="currency" value="344" >
    <input type="hidden" name="returnUrl" value="http://www.<company address>.com/" >
    <input type="hidden" name="language" value="en" >
    <input type="submit" name="submit" value="Submit">
</form>&ndash;%&gt;
</body></html>--%>
