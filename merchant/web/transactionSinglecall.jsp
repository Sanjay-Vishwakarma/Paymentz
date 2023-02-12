<%@ page import="java.util.Hashtable" %>
<%@ page import="com.directi.pg.Functions" %>
<%--
  Created by IntelliJ IDEA.
  User: sagar
  Date: 6/17/14
  Time: 2:43 PM
  To change this template use File | Settings | File Templates.
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="functions.jsp" %>
<%@ include file="Top.jsp" %>

<html>
<head>
    <title></title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
    <%--<link rel="stylesheet" href=" /merchant/transactionCSS/css/main.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>--%>

    <link rel="stylesheet" type="text/css" href="/merchant/style/poptuk.css">
    <%--<link rel="stylesheet" href=" /merchant/transactionCSS/css/main.css">
    <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
    <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>
    <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>--%>

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
</head>

<body class="pace-done widescreen fixed-left-void">




<%
    System.out.println("Inside Confirmation==");
    Functions functions = new Functions();
    Hashtable dataHash = (Hashtable) session.getAttribute("hiddenResponse");
    System.out.println("hidden data session---"+dataHash);

    if (dataHash == null)
    {
        dataHash = (Hashtable) request.getAttribute("hiddenResponse");
        System.out.println("hidden data---"+dataHash);
    }

    String trackingid = (String) dataHash.get("trackingid");
    String resAmount = (String) dataHash.get("resAmount");
    String status = (String) dataHash.get("status");
    String orderid = (String) dataHash.get("orderid");
    String billingDescriptor = (String) dataHash.get("billingDescriptor");
    String currency = (String) dataHash.get("currency");
    String message = "";

    if(functions.isValueNull((String)request.getAttribute("status")))
        status = (String)request.getAttribute("status");
    else
        status = (String) dataHash.get("status");

    if(functions.isValueNull((String)request.getAttribute("statusDesc")))
        message = (String)request.getAttribute("statusDesc");
    else
        message = (String) dataHash.get("statusDesc");

    if(functions.isValueNull((String)request.getAttribute("billingDescriptor")))
        billingDescriptor = (String)request.getAttribute("billingDescriptor");
    else
        billingDescriptor = (String) dataHash.get("billingDescriptor");
%>

<div class="content-page">
    <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">

            <div class="row">
                <div class="col-sm-12 portlets ui-sortable">
                    <div class="widget">

                        <div class="widget-header transparent">
                            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Transaction Status</strong>

                            </h2>
                            <div class="additional-btn">
                                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                            </div>
                        </div>

                        <div class="widget-content padding">
                            <div id="horizontal-form">
                                <div class="table table-responsive">
                                    <table class="table table-striped table-bordered" id="textheadid" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                                        <tr >
                                            <td class="tr0"  align="center" style="color: white;border-bottom: 1px solid #ddd;">Tracking ID</td>
                                            <td class="tr1" align="center"><%=trackingid%></td>
                                        </tr>
                                        <tr >
                                            <td class="tr0" align="center" style="color: white;border-bottom: 1px solid #ddd;">Order Amount </td>
                                            <td class="tr1" align="center"><%=resAmount%></td>
                                        </tr>
                                        <tr >
                                            <td class="tr0" align="center" style="color: white;border-bottom: 1px solid #ddd;">Currency </td>
                                            <td class="tr1" align="center"><%=currency%></td>
                                        </tr>
                                        <tr >
                                            <td class="tr0" align="center" style="color: white;border-bottom: 1px solid #ddd;">Description </td>
                                            <td class="tr1" align="center"><%=orderid%></td>
                                        </tr>
                                        <%
                                            System.out.println("status confirmation---"+status);
                                            if(status.equalsIgnoreCase("Y"))
                                            {
                                                status = "Approved";
                                            }
                                            else if(status.equalsIgnoreCase("P"))
                                            {
                                                status="Pending";
                                            }
                                            else
                                            {
                                                status = "Failed";
                                            }
                                        %>
                                        <tr >
                                            <td class="tr0" align="center" style="color: white;border-bottom: 1px solid #ddd;">Status </td>
                                            <td class="tr1" align="center"><%=status%></td>
                                        </tr>
                                        <tr >
                                            <td class="tr0" align="center" style="color: white;border-bottom: 1px solid #ddd;">Message </td>
                                            <td class="tr1" align="center"><%=message%></td>
                                        </tr>
                                        <%
                                            if(request.getAttribute("mandateId")!=null)
                                            {
                                        %>
                                        <tr >
                                            <td class="tr0" align="center" style="color: white;border-bottom: 1px solid #ddd;">MandateId </td>
                                            <td class="tr1" align="center"><%=(String)request.getAttribute("mandateId")%></td>
                                        </tr>
                                        <%
                                            }
                                        %>
                                        <tr >
                                            <td class="tr0" align="center" style="color: white;">Billing Descriptor </td>
                                            <td class="tr1" align="center"><%=functions.isValueNull(billingDescriptor)?billingDescriptor:""%></td>
                                        </tr>
                                    </table>

                                </div>
                                <%
                                    if(status.equalsIgnoreCase("Pending"))
                                    {
                                %>
                                <form method="post" action="/merchant/servlet/UpdateTransaction?ctoken=<%=ctoken%>">
                                <div class="form-group col-md-3">
                                    <button type="submit" name="B1" class="btn btn-default" style="display: -webkit-box;">Check Status</button>
                                </div>
                                    <input type="hidden" value="<%=trackingid%>" name="trackingid">
                                </form>
                                <%
                                    }
                                %>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<%=request.getAttribute("html")%>
</body>
</html>