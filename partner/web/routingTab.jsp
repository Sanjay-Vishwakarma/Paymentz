<%--
  Created by IntelliJ IDEA.
  User: Vivek
  Date: 3/5/2020
  Time: 12:16 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <style type="text/css">
    #main{background-color: #ffffff}
    :target:before {
      content: "";
      display: block;
      height: 50px;
      margin: -50px 0 0;
    }
    .table > thead > tr > th {font-weight: inherit;}
    :target:before {
      content: "";
      display: block;
      height: 90px;
      margin: -50px 0 0;
    }
    footer{border-top:none;margin-top: 0;padding: 0;}
    /********************Table Responsive Start**************************/
    @media (max-width: 640px){
      table {border: 0;}
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
    /********************Table Responsive Ends**************************/
  </style>
  <link rel="stylesheet" href="/partner/NewCss/css/jquery.dataTables.min.css">
  <script src="/partner/NewCss/js/jquery.dataTables.min.js"></script>
</head>
<body class="bodybackground">
<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  session.setAttribute("submit","White List");
%>
<div class="content-page">
  <div class="content" style="padding: 20px 20px 0px;">
    <div >
      <div >
        <%--<div class="row">--%>
          <div class="col-sm-12 portlets ui-sortable">
              <div class="widget-content ">
                <div id="horizontal-form" align="right">
                  <div class="form-group" style="display: inline-block;">
                    <form action="/partner/binRouting.jsp?ctoken=<%=ctoken%>" method="post" name="forms" >
                      <button class="btn btn-default" value="Bin List" name="submit"
                              <%
                                if(buttonvalue!=null)
                                {
                                  if(buttonvalue.equals("Bin Routing"))
                                  {   %>
                              style="background-color:#2c3e50;color: #ffffff;"
                              <%}
                              }
                              %>>
                        Bin Routing
                      </button>
                    </form>
                  </div>

                  <div class="form-group" style="display: inline-block;">
                    <form action="/partner/binCountryRouting.jsp?ctoken=<%=ctoken%>" method="post" name="forms" >
                      <button class="btn btn-default"  value="White List" name="submit"
                              <%
                                if(buttonvalue!=null)
                                {
                                  if(buttonvalue.equals("Bin Country Routing"))
                                  {   %>
                              style="background-color:#2c3e50;color: #ffffff;"
                              <%}
                              }
                              %>>
                        Bin Country Routing
                      </button>
                    </form>
                  </div>
                </div>
              </div>
            <%--</div>--%>
          </div>
       <%-- </div>--%>
      </div>
    </div>
  </div>
</div>
</body>
</html>