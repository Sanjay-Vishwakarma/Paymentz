<%@ page import="com.directi.pg.Functions" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 10/5/13
  Time: 5:13 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<div id="statusChartDiv" align="center">Status Chart.</div>
<script type="text/javascript">
    var myChart = new FusionCharts("/partner/FusionCharts/Funnel.swf", "statusChart", "450", "450","0","0");
    myChart.setDataURL("/partner/FusionCharts/data/StatusData_<%=session.getAttribute("merchantid")+ "_"+ Functions.getFormattedDate("yyMM")%>.xml");
    myChart.render("statusChartDiv");
</script>
</body>
</html>