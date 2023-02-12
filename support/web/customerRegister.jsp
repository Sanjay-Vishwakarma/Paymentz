<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: Jul 20, 2012
  Time: 8:06:38 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head><title>Customer Register for QWIPI</title></head>
  <body>
  <h1> Customer Register for QWIPI </h1>
  <form action="https://secure.qwipi.com/xcp/register.jsp" method="post"  >
  <table>
  <tr>
      <td> Merchant No: </td>
      <td><input name="merNo" type="Text" maxlength="10"></td>
  </tr>
  <tr>
      <td> Email: </td>
      <td> <input name="email" type="Text" maxlength="150"></td>
  </tr>
  <tr>
      <td> Card Number: </td>
      <td> <input name="cardNumber" type="Text" maxlength="16"></td>
  </tr>
  <tr>
      <td> Trade time: </td>
      <td> <input name="dateRequest" type="Text" maxlength="14"> e.g. "USD" </td>
  </tr>
  <tr>
      <td> Transaction language: </td>
      <td> <input name="language" type="Text" maxlength="3">e.g. "ENG - English"</td>
  </tr>
  <tr>
      <td> Notify URL: </td>
      <td> <input name="notifyURL" type="Text" maxlength="200" value="http://localhost:8081/support/customerresponce.jsp">  </td>
  </tr>
  <tr>
      <td> MD5 encrypt data: </td>
      <td> <input name="md5Info" type="Text" maxlength="32"></td>
  </tr>
  <tr >
  <td colspan="2" align="center">
     ::::Billing information::::
  </td>
  </tr>
  <tr>
      <td> First name </td>
      <td> <input name="firstName" type="Text" maxlength="100"></td>
  </tr>
  <tr>
      <td> Last name </td>
      <td> <input name="lastName" type="Text" maxlength="100"></td>
  </tr>
  <tr>
      <td> Email: </td>
      <td> <input name="email" type="Text" maxlength="150"></td>
  </tr>
  <tr>
      <td> Telephone NO: </td>
      <td> <input name="phone" type="Text" maxlength="50"></td>
  </tr>
  <tr>
      <td> Zip code: </td>
      <td> <input name="zipCode" type="Text" maxlength="100"></td>
  </tr>
  <tr>
      <td> Address: </td>
      <td> <input name="address" type="Text" maxlength="400"></td>
  </tr>
  <tr>
      <td> City: </td>
      <td> <input name="city" type="Text" maxlength="100"></td>
  </tr>
  <tr>
      <td> State: </td>
      <td><input name="state" type="Text" maxlength="100"></td>
  </tr>
  <tr>
      <td> Country: </td>
      <td> <td><input name="country" type="Text" maxlength="100"></td></td>
  </tr>

  <tr >
  <td colspan="2" align="center">
     ::::Other information::::
  </td>
  </tr>
  <tr>
      <td> remark: </td>
      <td> <input name="remark" type="Text" maxlength="1000"></td>
  </tr>

  <tr><td colspan="2" align="center"><INPUT type="submit"  value="Pay"  ></td></tr>
  </table>
  </form>
  </body>
</html>