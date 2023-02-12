<%@ page errorPage="" import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));%>
<%@ include file="top.jsp" %>
<html>
<head>
  <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
  <title><%=company%> Merchant POD-Partial Capture</title>

  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <%--<link rel="stylesheet" href=" /merchant/transactionCSS/css/main.css">
  <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
  <script type="text/javascript" src="/merchant/transactionCSS/js/creditly.js"></script>--%>

  <%--<script type="text/javascript" src='/merchant/css/new/html5shiv.min.js'></script>
  <script type="text/javascript" src='/merchant/css/new/respond.min.js'></script>--%>
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

      /*tr:nth-child(odd), tr:nth-child(even) {background: #ffffff;}*/

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

    /*tr:nth-child(odd) {background: #F9F9F9;}*/

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

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <form name="form" method="post" action="/partner/net/PartnerCapture?ctoken=<%=ctoken%>">
        <div class="pull-right">
          <div class="btn-group">

            <input type=hidden name=partialCapture value=>
            <input type=hidden name=trackingid value=>
            <%

              Enumeration<String> aName=request.getParameterNames();
              while(aName.hasMoreElements())
              {
                String name=aName.nextElement();
                String value = request.getParameter(name);
                if(value==null || value.equals("null"))
                {
                  value = "";
                }
            %>

            <input type=hidden name=<%=name%> value=<%=value%>>
            <%
              }

            %>
            <button class="btn-xs" type="submit" name="B2" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/merchant/images/goBack.png">
            </button>

          </div>
        </div>
      </form>
      <br><br><br>

      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Partial Capture</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <div class="widget-content padding">
              <div id="horizontal-form">                        <%-- End Radio Button--%>


                <%

                  HashMap hash = (HashMap) request.getAttribute("poddetails");

                  //out.println(hash);

                  HashMap temphash = null;

                  int records = 0;

                  try
                  {
                    records = Integer.parseInt((String) hash.get("records"));
                  }
                  catch (Exception ex)
                  {
                  }

                  String style = "class=td0";
                  String ext = "light";

                  if (request.getAttribute("message") != null)
                  {
                    out.println((String) request.getAttribute("message"));
                  }


                  if (records > 0 )
                  {


                    temphash = (HashMap) hash.get("1");
                    if (temphash.get("status").equals("capturesuccess") || temphash.get("status").equals("capturestarted"))
                    {
                      out.println(Functions.NewShowConfirmation1("Done", "This transaction has already been captured."));
                    }

                %>


                <%--<table border="0" cellpadding="0" cellspacing="0" width="95%" bordercolor="#FFFFFF" align="center">
                    <tr>
                        <td>&nbsp;</td>
                    </tr>
                    <tr>
                        &lt;%&ndash;<td align="center" class="textb">You can capture as much amount as you want.Make sure that it should not greater
                            than transaction amount.This partial capture will be consider as final capture and you will not be able to
                            capture remaining amount again.</td>&ndash;%&gt;
                            <td align="center" class="textb">You can capture as much amount as you want.
                                Make sure that capture amount should not be greater than Transaction Amount + OverCapture% allowed by bank.
                                This partial capture will be considered as final capture and you will not be able to capture remaining amount again.</td>
                    </tr>
                    <tr>
                        <td>&nbsp;</td>
                    </tr>
                </table>--%>

                <label class="bg-info" style="font-family:Open Sans;font-size: 13px;font-weight: 600;text-align: center; ">
                  You can capture as much amount as you want.
                  Make sure that capture amount should not be greater than Transaction Amount + OverCapture% allowed by bank.
                  This partial capture will be considered as final capture and you will not be able to capture remaining amount again.
                </label><br>
                <form name="f1" method="post" action="/partner/net/PartialCapture">


                  <%-- <table align="center" width="90%" border="1">--%>
                  <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                    <thead style="background-color: #7eccad !important;color: white;">
                    <tr>
                      <th style="background-color: #7eccad;text-align: center" >Tracking ID</th>
                      <th style="background-color: #7eccad;text-align: center" >Transaction Amount</th>
                      <th style="background-color: #7eccad;text-align: center" >Description</th>
                      <th style="background-color: #7eccad;text-align: center" >Capture Amount</th>
                      <th style="background-color: #7eccad;text-align: center" >POD*</th>
                    </tr>
                    </thead>

                    <tbody>
                    <tr>
                      <td data-label="Tracking ID" style="text-align: center"><%=ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))%></td>
                      <td data-label="Transaction Amount" style="text-align: center"><%=ESAPI.encoder().encodeForHTML((String) temphash.get("amount"))%></td>

                      <%
                        ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                      %>

                      <input type="hidden" value="<%=ctoken%>" name="ctoken">
                      <td data-label="Description" style="text-align: center"><%=ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("description"))%></td>
                      <td data-label="Capture Amount" style="text-align: center">
                        <input class="form-control" type="text" name="captureamount" style="text-align: center" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("amount"))%>"></td>
                      <td data-label="POD*" value="" style="text-align: center">
                        <input type="text" style="text-align: center" class="form-control" name="pod" value=""></td>
                      <input type="hidden" name="memberid" value="<%=request.getParameter("memberid")%>">



                    </tr></tbody>

                    <%--<tr>


                    </tr>

                    <tr>


                    </tr>--%>
                  </table>
                  <br>


                  <div class="form-group col-md-5"></div>
                  <div class="form-group col-md-4">
                    <button type="submit" class="btn btn-default" name="B1" value="Capture" >
                      <span><i class="fa fa-save"></i></span>
                      &nbsp;&nbsp;Capture
                    </button>

                  </div>
                  <input type="hidden" name="icicitransid" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("trackingid"))%>">
                  <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("accountid"))%>" name="accountid">
                </form>

                <%
                  }

                  else
                  {
                    String errormsg=(String) request.getAttribute("error");

                    out.println(Functions.NewShowConfirmation1("Information", "No records found."+errormsg));

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
</body>

</html>