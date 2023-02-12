<%--
  Created by IntelliJ IDEA.
  User: Admin
  Date: 5/2/2019
  Time: 1:50 PM
  To change this templateuse File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<%
String memberId="11344";
String checksum="85b7005f7956548a50520923a2d62b";
String totype="docspartner";
String merchantTransactionId="Transaction01";
String amount="1.00";
String TMPL_AMOUNT="1.00";
String orderDescription="Test Transaction";
String merchantRedirectUrl="https://www.merchantRedirectUrl.com";
String notificationUrl="https://www.merchantNotificationUrl.com";
String country="IN";
String city="Mumbai";
String state="MH";
String postcode="400064";
String street="Malad";
String telnocc="091";
String phone="9854785236";
String email="john.d@domain.com";
String ip="192.168.0.1";
String currency="USD";
String TMPL_CURRENCY="USD";
String terminalid="1106";
String reservedField1="";
String paymentMode="CC";
String paymentBrand="VISA";
  String MP_Memberid[]="{10958,10961,10962}";
String MP_Amount[]={"10.00,10.00,10.00"};
String MP_Orderid[]={"Transaction01-1,Transaction01-2,Transaction01-3"};
String MP_Order_Description[]={"Test Transaction,Test Transaction,Test Transaction"};
%>
<form method="POST" action="https://preprod.paymentz.com/transaction/Checkout" >
  <input type="hidden"  name="memberId"  value="<%=memberId%>"/>
  <input type="hidden"  name="checksum"  value="<%=checksum%>"/>
  <input type="hidden"  name="totype"  value="<%=totype%>"/>
  <input type="hidden"  name="merchantTransactionId"  value="<%=merchantTransactionId%>"/>
  <input type="hidden"  name="amount"  value="<%=amount%>"/>
  <input type="hidden"  name="TMPL_AMOUNT"  value="<%=TMPL_AMOUNT%>"/>
  <input type="hidden"  name="orderDescription"  value="<%=orderDescription%>"/>
  <input type="hidden"  name="merchantRedirectUrl"  value="<%=merchantRedirectUrl%>"/>
  <input type="hidden"  name="notificationUrl"  value="<%=notificationUrl%>"/>
  <input type="hidden"  name="country"  value="<%=country%>"/>
  <input type="hidden"  name="city"  value="<%=city%>"/>
  <input type="hidden"  name="state"  value="<%=state%>"/>
  <input type="hidden"  name="postcode"  value="<%=postcode%>"/>
  <input type="hidden"  name="street"  value="<%=street%>"/>
  <input type="hidden"  name="telnocc"  value="<%=telnocc%>"/>
  <input type="hidden"  name="phone"  value="<%=phone%>"/>
  <input type="hidden"  name="email"  value="<%=email%>"/>
  <input type="hidden"  name="ip"  value="<%=ip%>"/>
  <input type="hidden"  name="currency"  value="<%=currency%>"/>
  <input type="hidden"  name="TMPL_CURRENCY"  value="<%=TMPL_CURRENCY%>"/>
  <input type="hidden"  name="terminalid"  value="<%=terminalid%>"/>
  <input type="hidden"  name="reservedField1"  value="<%=reservedField1%>"/>
  <input type="hidden"  name="paymentMode"  value="<%=paymentMode%>"/>
  <input type="hidden"  name="paymentBrand"  value="<%=paymentBrand%>"/>
  <input type="hidden"  name="MP_Memberid[]"  value="<%=MP_Memberid[]%>"/>
  <input type="hidden"  name="MP_Amount[]"  value="<%=MP_Amount[]%>"/>
  <input type="hidden"  name="MP_Orderid[]"  value="<%=MP_Orderid[]%>"/>
  <input type="hidden"  name="MP_Order_Description[]"  value="<%=MP_Order_Description[]%>"/>
</form>

</body>
</html>
