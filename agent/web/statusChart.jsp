<%@ page import="com.directi.pg.Functions" %>
<%--
  Created by IntelliJ IDEA.
  User: saurabh
  Date: 2/24/14
  Time: 4:13 PM
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
    var myChart = new FusionCharts("/agent/FusionCharts/Funnel.swf", "statusChart", "350", "350","0","0");
    <%--myChart.setDataURL("/agent/FusionCharts/data/StatusData_<%=session.getAttribute("merchantid")+ "_"+ Functions.getFormattedDate("yyMM")%>.xml");--%>
    myChart.setDataURL("/agent/FusionCharts/data/StatusData_<%=session.getAttribute("merchantid")+ "_"+ session.getAttribute("charttoken")%>.xml");
    myChart.render("statusChartDiv");
</script>
</body>
</html>