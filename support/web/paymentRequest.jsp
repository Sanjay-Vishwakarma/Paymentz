<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: Jul 20, 2012
  Time: 8:06:38 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head><title>Payment Request for QWIPI</title></head>
  <body>
  <h1> Payment Request for QWIPI </h1>
  <form action="https://secure.qwipi.com/xcp/payments.jsp" method="post"  >
  <table>
  <tr>
      <td> Merchant No: </td>
      <td> <td><input name="merNo" type="Text" maxlength="10"></td></td>
  </tr>
  <tr>
      <td> Trade time: </td>
      <td> <td><input name="dateTime" type="Text" maxlength="14"></td></td>
  </tr>
  <tr>
      <td> Unique order NO: </td>
      <td> <td><input name="billNo" type="Text" maxlength="40"></td></td>
  </tr>
  <tr>
      <td> Transaction currency: </td>
      <td> <td><input name="currency" type="Text" maxlength="3"> e.g. "USD" </td></td>
  </tr>
  <tr>
      <td> Total amount of this order, USD: </td>
      <td> <td><input name="amount" type="Text" maxlength="20"></td></td>
  </tr>
  <tr>
      <td> Transaction language: </td>
      <td> <td><input name="language" type="Text" maxlength="3"> e.g. "ENG - English" </td></td>
  </tr>
  <tr>
      <td> returning response URL: </td>
      <td> <td><input name="returnURL" type="Text" maxlength="200" value="http://localhost:8081/support/redirect.jsp" ></td></td>
  </tr>
  <tr >
  <td colspan="2" align="center">
     ::::Billing information::::
  </td>
  </tr>
  <tr>
      <td> First name </td>
      <td> <td><input name="firstName" type="Text" maxlength="100"></td></td>
  </tr>
  <tr>
      <td> Last name </td>
      <td> <td><input name="lastName" type="Text" maxlength="100"></td></td>
  </tr>
  <tr>
      <td> Email: </td>
      <td> <td><input name="email" type="Text" maxlength="150"></td></td>
  </tr>
  <tr>
      <td> Telephone NO: </td>
      <td> <td><input name="phone" type="Text" maxlength="50"></td></td>
  </tr>
  <tr>
      <td> Zip code: </td>
      <td> <td><input name="zipCode" type="Text" maxlength="100"></td></td>
  </tr>
  <tr>
      <td> Address: </td>
      <td> <td><input name="address" type="Text" maxlength="400"></td></td>
  </tr>
  <tr>
      <td> City: </td>
      <td> <td><input name="city" type="Text" maxlength="100"></td></td>
  </tr>
  <tr>
      <td> State: </td>
      <td> <td><input name="state" type="Text" maxlength="100"></td></td>
  </tr>
  <tr>
      <td> Country: </td>
      <td> <td><input name="country" type="Text" maxlength="100"></td></td>
  </tr>
  <tr  >
  <td colspan="2" align="center">
     ::::Shipping information::::
  </td>
  </tr>
  <tr>
      <td> Consignee's First name: </td>
      <td> <td><input name="shippingFirstName" type="Text" maxlength="100"></td></td>
  </tr>
  <tr>
      <td> Consignee's Last name: </td>
      <td> <td><input name="shippingLastName" type="Text" maxlength="100"></td></td>
  </tr>
  <tr>
      <td> Consignee's Email: </td>
      <td> <td><input name="shippingEmail" type="Text" maxlength="150"></td></td>
  </tr>
  <tr>
      <td> Consignee's Telephone NO: </td>
      <td> <td><input name="shippingPhone" type="Text" maxlength="50"></td></td>
  </tr>
  <tr>
      <td> Consignee's Zip code: </td>
      <td> <td><input name="shippingZipcode" type="Text" maxlength="100"></td></td>
  </tr>
  <tr>
      <td> Consignee's Address: </td>
      <td> <td><input name="shippingAddress" type="Text" maxlength="400"></td></td>
  </tr>
  <tr>
      <td> Consignee's City: </td>
      <td> <td><input name="shippingCity" type="Text" maxlength="100"></td></td>
  </tr>
  <tr>
      <td> Consignee's State: </td>
      <td> <td><input name="shippingState" type="Text" maxlength="100"></td></td>
  </tr>
  <tr>
      <td> Consignee's Country: </td>
      <td> <td><input name="shippingCountry" type="Text" maxlength="100"></td></td>
  </tr>
  <tr >
  <td colspan="2" align="center">
     ::::Other information::::
  </td>
  </tr>
  <tr>
      <td> Merchandise information: </td>
      <td> <td><input name="products" type="Text" maxlength="1000"></td></td>
  </tr>
  <tr>
      <td> Trade additional information: </td>
      <td> <td><input name="remark" type="Text" maxlength="200"></td></td>
  </tr>
  <tr><td colspan="2" align="center"><INPUT type="submit"  value="Pay"  ></td></tr>
  </table>
  </form>
  </body>
</html>