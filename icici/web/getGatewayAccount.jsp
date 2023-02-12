<%@ page import="java.util.Hashtable" %>
<%--
  Created by IntelliJ IDEA.
  User: admin1
  Date: 2/9/13
  Time: 9:17 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Gateway Account Information</title>
</head>
<body>
       <%
           Hashtable hash=(Hashtable) request.getAttribute("hiddenvariables");
           if(hash==null){


       %>
         <h1> Gateway Account Information</h1>
         <center> <font color=red size=2> Sorry , You have not Selected any gateway</font></center>
<%
    }else
    {
%>

       <h1 align=center> Gateway Account Information</h1>
       <table align=center border="1" bordercolorlight="#000000">


    <tr>
    <td colspan=5 bgcolor="#CCCCFF" align=center><b>Account ID </b>: <%=hash.get("accountid")%>  &nbsp; &nbsp; &nbsp; <b>PG TYPE ID </b>: <%=hash.get("pgtypeid")%> </td>
    </tr>

    <tr>
    <td  colspan=5 bgcolor="#CCCCCC" align=center><b>Alias Name</b>: <%=hash.get("aliasname")%> &nbsp; &nbsp; &nbsp; <b>Display Name</b>: <%=hash.get("displayname")%></td>
    </tr>

    <tr>
    <td colspan=5 bgcolor="#CCCCFF" align=center> <b>MID</b>: <%=hash.get("merchantid")%></td>
    </tr>
    <tr>
    <td colspan=5 bgcolor="#CC9966" align=center><b>Limits</b></td>
    </tr>
    <tr bgcolor="#CCCCCC"><th>Daily Amount Limit</th><th>Monthly Amount Limit</th><th>Daily Card Limit</th><th>Weekly Card Limit</th><th>Monthly Card Limit</th></tr>
    <tr bgcolor="CCCCFF"><td><%=hash.get("dailyamountlimit")%></td><td><%=hash.get("monthlyamountlimit")%></td><td><%=hash.get("dailycardlimit")%></td><td><%=hash.get("weeklycardlimit")%></td><td><%=hash.get("monthlycardlimit")%></td>
    </tr>
   <tr>
       <td colspan=5  bgcolor="#CCCCCC" align=center> <b>Min Txn. Amount</b>: <%=hash.get("mintxnamount")%> &nbsp; &nbsp; &nbsp; <b>Max Txn. Amount.</b>: <%=hash.get("maxtxnamount")%></td>
       </td>
   </tr>



</table>






       <%
    }
%>
</body>
</html>