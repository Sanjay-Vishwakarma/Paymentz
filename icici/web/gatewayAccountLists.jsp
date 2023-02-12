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

<center><h2>GateWay Accounts List</h2></center>
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
        <th>Account Id</th>
        <th>Merchant Id</th>
        <th>Gateway Id</th>
        <th>Aliasname</th>
        <th>Displayname</th>
        <th>Ismastercardsupported</th>
        <th>Shortname</th>
        <th>Site</th>
        <th>Path </th>
        <th>Username </th>
        <th>Password</th>
        <th>Chargeback Path</th>
        <th>IsCVVrequired</th>
        <th>Daily Card Limit</th>
        <th>Daily Amount Limit</th>
        <th>Weekly Card Limit</th>
        <th>Monthly Card Limit </th>
        <th>Monthly Amount Limit</th>
        <th>Min Transaction Amount</th>
        <th>Max Transacton Amount</th>
        <th>Daily Card Amount Limit</th>
        <th>Weekly Card Amount Limit</th>
        <th>Montly Card Amount Limit </th>
    </tr>

<%
    StringBuilder sb  = new StringBuilder();
    int i = 0;
    String tdClass ="";

    ArrayList<GatewayAccount> gatewayAccountArrayList = GatewayAccountService.getAllGatewayAccounts();
        //gatewayAccountArrayList.

        for(GatewayAccount gatewayAccount : gatewayAccountArrayList)
        {
            i++;
            if(i%2 == 0)
                tdClass  = "treven";
            else
                tdClass  = "trodd";

             sb.append("<tr class='" + tdClass  + "'>");
             sb.append("<td>");
             sb.append(gatewayAccount.getAccountId());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.getMerchantId());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.getGatewayName());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.getAliasName());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.getDisplayName());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.isMasterCardSupported());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.getFRAUD_FILE_SHORT_NAME());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.getFRAUD_FTP_SITE());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.getFRAUD_FTP_PATH());
             sb.append("</td>");


            sb.append("<td>");
            sb.append(gatewayAccount.getFRAUD_FTP_USERNAME());
            sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.getPassword());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.getCHARGEBACK_FTP_PATH());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.isCvvRequired());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.getDailyCardLimit());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.getDailyAmountLimit());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.getWeeklyCardLimit());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.getMonthlyCardLimit());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.getMonthlyAmountLimit());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.getMinTransactionAmount());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.getMazTransactionAmount());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.getDailyCardAmountLimit());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.getWeeklyCardAmountLimit());
             sb.append("</td>");

             sb.append("<td>");
             sb.append(gatewayAccount.getMonthlyCardAmountLimit());
             sb.append("</td>");

             sb.append("</tr>");
        }
%>

    <%=sb.toString()%>

</table>
</body>
</html>
