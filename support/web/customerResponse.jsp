<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: Jul 20, 2012
  Time: 8:07:13 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head><title>Customer Responce for QWIPI</title></head>
  <body>
  <h1> Customer Responce for QWIPI </h1>
  <%
      String Ocode=request.getParameter("operation");

  %>
  <form name="form1" >
  <table>
  <tr>
    <td> Operation code: </td>
    <td> <%= request.getParameter("operation") %>   </td>
  </tr>

  <tr>
    <td> Result code: </td>
    <td> <%= request.getParameter("resultCode") %>   </td>
  </tr>

  <tr>
    <td> Merchant NO: </td>
    <td> <%= request.getParameter("merNo") %>   </td>
  </tr>

  <tr>
    <td> Customer email: </td>
    <td> <%= request.getParameter("email") %> </td>
  </tr>

  <tr>
    <td> Customer Card Number: </td>
    <td> <%= request.getParameter("cardNumber") %>   </td>
  </tr>

  <tr>
    <td> Customer registration datetime: </td>
    <td> <%= request.getParameter("dateRegister") %>  </td>
  </tr>

  <tr>
    <td> Customer card information register Id: </td>
    <td> <%= request.getParameter("registerId") %></td>
  </tr>

  <tr>
    <td> Activation URL: </td>
    <td> <%= request.getParameter("activationURL") %>  </td>
  </tr>

  <tr>
    <td> Remark: </td>
    <td> <%= request.getParameter("remark") %>  </td>
  </tr>

  <tr>
    <td> MD5 encrypt data: </td>
    <td> <%= request.getParameter("md5Info") %> </td>
  </tr>
  </table>
  </form>
  </body>
</html>