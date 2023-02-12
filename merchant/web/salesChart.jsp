<%@ page import="java.util.Hashtable,
                 java.io.File,
                 java.io.FileWriter,
                 java.io.IOException,
                 com.directi.pg.Mail,
                 com.logicboxes.util.Util,
                 com.directi.pg.Functions"%>
 <%--
  Created by IntelliJ IDEA.
  User: admin
  Date: Nov 13, 2010
  Time: 11:05:38 PM
  To change this template use File | Settings | File Templates.
--%>
<% String charttoken=(String)session.getAttribute("charttoken");%>
<html>
<HEAD>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
</HEAD>
<body>
<div id="salesChartDiv" align="center">Sales Chart.</div>
	<script type="text/javascript">
		var myChart = new FusionCharts("/merchant/FusionCharts/FCF_StackedColumn3D.swf?ver=1>", "salesChart", "450", "450");
		myChart.setDataURL("/merchant/FusionCharts/data/SalesData_<%=session.getAttribute("merchantid")+ "_"+ Functions.getFormattedDate("yyMM")+"_"+charttoken%>.xml");
		myChart.render("salesChartDiv");
	</script>
</body>
</html>