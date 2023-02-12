<%@ page import="com.manager.vo.merchantmonitoring.MonitoringParameterVO" %>
<%--
  Created by IntelliJ IDEA.
  User: Vishal
  Date: 3/6/2017
  Time: 1:32 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Risk Rule Information</title>
  <link href="/partner/NewCss/css/style.css" rel="stylesheet" type="text/css" />

  <style type="text/css">
    .table > thead > tr > th {font-weight: inherit;}

    /********************Table Responsive Start**************************/

    @media (max-width: 640px){

      table {border: 0;}

      /*table tr {
          padding-top: 20px;
          padding-bottom: 20px;
          display: block;
      }*/

      table thead { display: none;}

      tr:nth-child(odd), tr:nth-child(even) {background: #ffffff;}

      table td {
        display: block;
        border-bottom: none;
        padding-left: 0;
        padding-right: 0;
      }

      table td:before {
        content: attr(data-label);
        float: left;
        width: 100%;
        font-weight: bold;
      }

      /*table tr:nth-child(odd) {background: #cacaca!important;}*/

    }

    table {
      width: 100%;
      max-width: 100%;
      border-collapse: collapse;
      margin-bottom: 20px;
      display: table;
      border-collapse: separate;
      border-color: grey;
    }

    thead {
      display: table-header-group;
      vertical-align: middle;
      border-color: inherit;

    }

    tr:nth-child(odd) {background: #F9F9F9;}

    tr {
      display: table-row;
      vertical-align: inherit;
      border-color: inherit;
    }

    th {padding-right: 1em;text-align: left;font-weight: bold;}

    td, th {display: table-cell;vertical-align: inherit;}

    tbody {
      display: table-row-group;
      vertical-align: middle;
      border-color: inherit;
    }

    td {
      padding-top: 6px;
      padding-bottom: 6px;
      padding-left: 10px;
      padding-right: 10px;
      vertical-align: top;
      border-bottom: none;
    }

    .table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td{border-top: 1px solid #ddd;}

    .table-bordered {
      border: 1px solid #ddd;
    }

    /********************Table Responsive Ends**************************/
  </style>

</head>
<body style="background-color: #ffffff;">
<%
  MonitoringParameterVO monitoringParameterVO = (MonitoringParameterVO) request.getAttribute("monitoringParameterVO");
  if(monitoringParameterVO==null){
%>
<h1> Risk Rule Information</h1>
<center> <font color=red size=2> Sorry , You have not Selected any gateway</font></center>
<%
}else
{
%>

<h1 align=center>Risk Rule Information</h1>
<div style="padding-left: 15px; padding-right: 15px;">
<table <%--align=center border="1" bordercolorlight="#000000"--%> class="table table-striped table-bordered table-hover dataTable" >

  <tr>
    <td colspan=5 bgcolor=""  style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" align=center><b>Risk Rule Information </b>&nbsp; &nbsp; &nbsp; <%--<b>PG TYPE ID </b>: <%=hash.get("pgtypeid")%>--%> </td>
  </tr>

  <tr>
    <td  colspan=5 bgcolor="" align=center><b>Rule Id</b>: <%=monitoringParameterVO.getMonitoringParameterId()%> &nbsp; &nbsp; &nbsp; <%--<b>Display Name</b>: <%=hash.get("displayname")%>--%></td>
  </tr>
  <tr>
    <td  colspan=5 bgcolor="" align=center><b>Rule Name</b>: <%=monitoringParameterVO.getMonitoringParameterName()%> &nbsp; &nbsp; &nbsp; <%--<b>Display Name</b>: <%=hash.get("displayname")%>--%></td>
  </tr>
  <tr>
    <td  colspan=5 bgcolor="" align=center><b>Rule Description</b>: <%=monitoringParameterVO.getMonitoingParaTechName()%> &nbsp; &nbsp; &nbsp; <%--<b>Display Name</b>: <%=hash.get("displayname")%>--%></td>
  </tr>

</table>
</div>
<%
  }
%>
</body>
</html>
