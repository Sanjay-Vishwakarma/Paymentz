<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="Top.jsp"%>
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
  <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
  <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
</head>
<body class="bodybackground">
<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  session.setAttribute("submit","Black List");
%>
<div class="content-page">
  <div class="content" style="padding: 20px 20px 0px;">
    <div >
      <div >
        <div class="row">
          <div class="col-sm-12 portlets ui-sortable">
            <div class="widget">
              <div class="widget-header transparent">
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Blacklist Details</strong>
                </h2>
              </div>
              <div class="widget-content">
                <div id="horizontal-form">
                  <div class="form-group  ">
                    <form action="/merchant/blockCountry.jsp?ctoken=<%=ctoken%>" method="post" name="forms" >
                      <button class="btn btn-default col-sm-2" value="Black List" name="submit" style="    margin: 0px 55px 15px;"
                              <%
                                if(buttonvalue!=null)
                                {
                                  if(buttonvalue.equals("Black List"))
                                  {   %>
                              style="margin-bottom: 15px";
                              <%}}
                              %>>
                        Blocked Country
                      </button>
                    </form>
                  </div>
                  <div class="form-group">
                    <form action="/merchant/blockBin.jsp?ctoken=<%=ctoken%>" method="post" name="forms" >
                      <button class="btn btn-default col-sm-2"  value="Black List" name="submit" style="    margin: 0px 55px 15px;"
                              <%
                                if(buttonvalue!=null)
                                {
                                  if(buttonvalue.equals("Block Bin"))
                                  {   %>
                              style="margin-bottom: 15px";
                              style="background-color:#2c3e50;color: #ffffff;"
                              <%}
                              }
                              %>>
                        Blocked Bin
                      </button>
                    </form>
                  </div>
                  <div class="form-group">
                    <form action="/merchant/blockiplist.jsp?ctoken=<%=ctoken%>" method="post" name="forms" >
                      <button class="btn btn-default col-sm-2"  value="Black List" name="submit" style="    margin: 0px 55px 15px;"
                              <%
                                if(buttonvalue!=null)
                                {
                                  if(buttonvalue.equals("Block IP"))
                                  {   %>
                              style="margin-bottom: 15px";
                              style="background-color:#2c3e50;color: #ffffff;"
                              <%}
                              }
                              %>>
                        Blocked IP
                      </button>
                    </form>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>