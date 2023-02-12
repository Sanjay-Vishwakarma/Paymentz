<%@ page import="com.directi.pg.Functions"%>
 <%--
  Created by IntelliJ IDEA.
  User: admin
  Date: Nov 13, 2010
  Time: 11:05:38 PM
  To change this template use File | Settings | File Templates.
--%>


<%
    String charttoken=(String)session.getAttribute("charttoken");
    String fileType = (String)request.getAttribute("fileType");
%>
 <html>
 <HEAD>
     <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
     <meta http-equiv="Expires" content="0">
     <meta http-equiv="Pragma" content="no-cache">

     <script src="/merchant/NewCss/chartJS/Chart.bundle.js"></script>
     <style>
         canvas {
             -moz-user-select: none;
             -webkit-user-select: none;
             -ms-user-select: none;
         }
     </style>
 </HEAD>
 <body>
<div id="statusChartDiv" align="center">Status Chart.</div>
	<script type="text/javascript">
        var myChart = new FusionCharts("/merchant/FusionCharts/<%=fileType%>?ver=1", "statusChart", "450", "450","0","0");
		myChart.setDataURL("/merchant/FusionCharts/data/StatusData_<%=session.getAttribute("merchantid")+ "_"+ Functions.getFormattedDate("yyMM")+"_"+charttoken%>.xml");
		myChart.render("statusChartDiv");
	</script>
 </body>
</html>