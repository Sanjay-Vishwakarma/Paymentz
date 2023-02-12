<%@ page import="com.manager.vo.merchantmonitoring.MonitoringParameterVO" %>
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
  <title>Risk Rule Information</title>
</head>
<body>
<%
  MonitoringParameterVO monitoringParameterVO=(MonitoringParameterVO) request.getAttribute("monitoringParameterVO");
  if(monitoringParameterVO==null){


%>
<h1> Risk Rule Information</h1>
<center> <font color=red size=2> Sorry , You have not Selected any gateway</font></center>
<%
}else
{
%>

<h1 align=center>Risk Rule Information</h1>
<table align=center border="1" bordercolorlight="#000000">

  <tr>
    <td colspan=5 bgcolor="#CCCCFF" align=center><b>Risk Rule Information </b>&nbsp; &nbsp; &nbsp; <%--<b>PG TYPE ID </b>: <%=hash.get("pgtypeid")%>--%> </td>
  </tr>

  <tr>
    <td  colspan=5 bgcolor="#CCCCCC" align=center><b>Rule Id</b>: <%=monitoringParameterVO.getMonitoringParameterId()%> &nbsp; &nbsp; &nbsp; <%--<b>Display Name</b>: <%=hash.get("displayname")%>--%></td>
  </tr>
  <tr>
    <td  colspan=5 bgcolor="#CCCCCC" align=center><b>Rule Name</b>: <%=monitoringParameterVO.getMonitoringParameterName()%> &nbsp; &nbsp; &nbsp; <%--<b>Display Name</b>: <%=hash.get("displayname")%>--%></td>
  </tr>
  <tr>
    <td  colspan=5 bgcolor="#CCCCCC" align=center><b>Rule Description</b>: <%=monitoringParameterVO.getMonitoingParaTechName()%> &nbsp; &nbsp; &nbsp; <%--<b>Display Name</b>: <%=hash.get("displayname")%>--%></td>
  </tr>

  <%--<tr>
    <td colspan=5 bgcolor="#CCCCFF" align=center> <b>MID</b>: <%=hash.get("merchantid")%></td>
  </tr>--%>
  <%--<tr>
    <td colspan=5 bgcolor="#CC9966" align=center><b>Limits</b></td>
  </tr>
  <tr bgcolor="#CCCCCC"><th>Daily Amount Limit</th><th>Monthly Amount Limit</th><th>Daily Card Limit</th><th>Weekly Card Limit</th><th>Monthly Card Limit</th></tr>
  <tr bgcolor="CCCCFF"><td><%=hash.get("dailyamountlimit")%></td><td><%=hash.get("monthlyamountlimit")%></td><td><%=hash.get("dailycardlimit")%></td><td><%=hash.get("weeklycardlimit")%></td><td><%=hash.get("monthlycardlimit")%></td>
  </tr>
  <tr>
    <td colspan=5  bgcolor="#CCCCCC" align=center> <b>Min Txn. Amount</b>: <%=hash.get("mintxnamount")%> &nbsp; &nbsp; &nbsp; <b>Max Txn. Amount.</b>: <%=hash.get("maxtxnamount")%></td>
    </td>
  </tr>--%>
</table>
<%
  }
%>
</body>
</html>