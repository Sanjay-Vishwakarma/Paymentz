<%@ page import="com.directi.pg.Functions" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 10/5/13
  Time: 5:14 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<div id="salesChartDiv" align="center">Sales Chart</div>
<script type="text/javascript">
    var myChart = new FusionCharts("/agent/FusionCharts/Column3D.swf?ver=1", "salesChart","350", "350");
    <%--myChart.setDataURL("/agent/FusionCharts/data/SalesData_<%=session.getAttribute("merchantid")+ "_"+ Functions.getFormattedDate("yyMM")%>.xml");--%>
    myChart.setDataURL("/agent/FusionCharts/data/SalesData_<%=session.getAttribute("merchantid")+ "_"+ session.getAttribute("charttoken")%>.xml");
    myChart.render("salesChartDiv");
</script>
</body>
</html>