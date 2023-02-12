<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: Jul 20, 2012
  Time: 8:07:13 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head><title>Payment Responce for QWIPI</title></head>
  <body>
  <h1> Payment Responce for QWIPI </h1>
  <%
      String resultcode= request.getParameter("merNo");
      /*System.out.println(resultcode);*/
  %>
  <form name="form1" >
  <table>
  <tr>
    <td> Result code: </td>
    <td> <input type="text" maxlength="2"  value="<%= request.getParameter("resultCode") %>">    </td>
  </tr>

  <tr>
    <td> Merchant NO: </td>
    <td> <input type="text"  maxlength="10"  value="<%= request.getParameter("merNo") %>">    </td>
  </tr>

  <tr>
    <td> Merchant�s unique order NO.: </td>
    <td> <input type="text"  maxlength="40"  value="<%= request.getParameter("billNo") %>">    </td>
  </tr>

  <tr>
    <td> Total amount of this order: </td>
    <td> <input type="text" maxlength="20" value="<%= request.getParameter("amount") %>">    </td>
  </tr>

  <tr>
    <td> Transaction currency: </td>
    <td> <input type="text" maxlength="3" value="<%= request.getParameter("currency") %>">    </td>
  </tr>

  <tr>
    <td> Trade time: </td>
    <td> <input type="text" maxlength="14" value="<%= request.getParameter("dateTime") %>">    </td>
  </tr>

  <tr>
    <td> Payment system order NO: </td>
    <td> <input type="text" maxlength="20" value="<%= request.getParameter("paymentOrderNo") %>" >   </td>
  </tr>

  <tr>
    <td> Remark: </td>
    <td> <input type="text" maxlength="1000" value="<%= request.getParameter("remark") %>" >   </td>
  </tr>

  <tr>
    <td> MD5 encrypt data: </td>
    <td> <input type="text" maxlength="32" value="<%= request.getParameter("md5Info") %>" >   </td>
  </tr>
  </table>
  </form>
  </body>
</html>