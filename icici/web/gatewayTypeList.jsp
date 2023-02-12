<%@ page import="org.owasp.esapi.ESAPI" %>
<%--
  Created by IntelliJ IDEA.
  User: mukesh.a
  Date: 1/23/14
  Time: 12:52 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Functions,org.owasp.esapi.ESAPI,org.owasp.esapi.errors.ValidationException"%>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head><title>Add New Gateway Type</title>
    <script type="text/javascript" src="/icici/javascript/jquery.jcryption.js"></script>
    <script type="text/javascript">
    </script>

</head>

<body>
<%
   ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
   if (!com.directi.pg.Admin.isLoggedIn(session))
   {
       response.sendRedirect("/icici/logout.jsp");
       return;
   }

%>
<center><h2>GateWay Types List</h2></center>
<br>

<center>
    <table border="0">
        <tr>
            <td><form action="/icici/gatewayTypeList.jsp?ctoken=<%=ctoken%>" method="POST"><input type="submit" value="GateType Lists" name="submit" class="buttonadmin"></form>&nbsp;&nbsp;&nbsp;</td>
            <td><form action="/icici/addGatewayType.jsp?ctoken=<%=ctoken%>" method="POST"><input type="submit" value="Add New Gateway Type" name="submit" class="buttonadmin"></form>&nbsp;&nbsp;&nbsp;</td>
            <td><form action="/icici/gatewayAccountLists.jsp?ctoken=<%=ctoken%>" method="POST"><input type="submit" value="Gateway Accounts List" name="submit" class="buttonadmin"></form>&nbsp;&nbsp;&nbsp;</td>
            <td><form action="/icici/addNewGatewayAccount.jsp?ctoken=<%=ctoken%>" method="POST"><input type="submit" value="Add New GateWay Account" name="submit" class="buttonadmin"></form>&nbsp;&nbsp;&nbsp;</td>
        </tr>
    </table>

</center>

<table border="0" align="center" width="50%">

<tr bgcolor="#007ACC">
    <th>GateWay type Id</th>
    <th>Gateway</th>
    <th>Currency</th>
    <th>Name</th>
    <th>Charge Percentage</th>
    <th>Tax Percentage</th>
    <th>WithDrawal Charges</th>
    <th>Reversal Charges</th>
    <th>CharegeBack Charges</th>
    <th>TaxAccount</th>
    <th>HighRisk Account</th>
    <th>Address</th>
    <th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
</tr>


<%

        StringBuilder sb  = new StringBuilder();
        int i = 0;
        String tdClass ="";
        ArrayList<GatewayType> gatewayTypes = GatewayTypeService.getAllGatewayTypes();

       for(GatewayType gatewayType : gatewayTypes)
       {

            i++;
            if(i%2 == 0)
                tdClass  = "treven";
            else
                tdClass  = "trodd";

         sb.append("<tr class='" + tdClass  + "'>");


         sb.append("<td>");
         sb.append(gatewayType.getPgTypeId());
         sb.append("</td>");

         sb.append("<td>");
         sb.append(gatewayType.getGateway());
         sb.append("</td>");

         sb.append("<td>");
         sb.append(gatewayType.getCurrency());
         sb.append("</td>");

         sb.append("<td>");
         sb.append(gatewayType.getName());
         sb.append("</td>");

         sb.append("<td>");
         sb.append(gatewayType.getChargePercentage());
         sb.append("</td>");

         sb.append("<td>");
         sb.append(gatewayType.getTaxPercentage());
         sb.append("</td>");

         sb.append("<td>");
         sb.append(gatewayType.getWithdrawalCharge());
         sb.append("</td>");

         sb.append("<td>");
         sb.append(gatewayType.getReversalCharge());
         sb.append("</td>");

         sb.append("<td>");
         sb.append(gatewayType.getChargebackCharge());
         sb.append("</td>");

         sb.append("<td>");
         sb.append(gatewayType.getTaxAccount());
         sb.append("</td>");

         sb.append("<td>");
         sb.append(gatewayType.getHighRiskAmount());
         sb.append("</td>");

         sb.append("<td>");
         sb.append(gatewayType.getAddress());
         sb.append("</td>");

         String token = ctoken;

         String id = gatewayType.getPgTypeId();

         String tempData = "<form action='/icici/editGatewayType.jsp?ctoken=" + token + "' method='POST'>" +
          "<input type='hidden' name='pgtypeid' id='pgtypeid' value='" + id + "'>" +
          "<input type='submit' value='Edit' name='submit' class='buttonadmin'>" +
          "<input type='hidden' value='1' name='showEdit'>" +

          "</form>";

         sb.append("<td>");
         sb.append(tempData);
         sb.append("</td>");

         sb.append("</tr>");
       }
%>
    <%=sb.toString()%>

</body>
</html>