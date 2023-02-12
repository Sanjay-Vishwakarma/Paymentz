<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="com.directi.pg.*" %>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 8/3/15
  Time: 5:17 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@include file="ietest.jsp"%>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
  session.setAttribute("submit","Reports");
  response.setHeader("X-Frame-Options", "ALLOWALL");
  session.setAttribute("X-Frame-Options", "ALLOWALL");
  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);

  String listWireReportsRandomCharges_RandomCharges = !functions.isEmptyOrNull(rb1.getString("listWireReportsRandomCharges_RandomCharges"))?rb1.getString("listWireReportsRandomCharges_RandomCharges"): "Random Charges Applied In Current Wire";
  String listWireReportsRandomCharges_Result = !functions.isEmptyOrNull(rb1.getString("listWireReportsRandomCharges_Result"))?rb1.getString("listWireReportsRandomCharges_Result"): "Result";
  String listWireReportsRandomCharges_Randomnot = !functions.isEmptyOrNull(rb1.getString("listWireReportsRandomCharges_Randomnot"))?rb1.getString("listWireReportsRandomCharges_Randomnot"): "Random Charges Not Founds In Current Wire.";
  String listWireReportsRandomCharges_Go_Back = !functions.isEmptyOrNull(rb1.getString("listWireReportsRandomCharges_Go_Back"))?rb1.getString("listWireReportsRandomCharges_Go_Back"): "Go Back";
%>
<%@ include file="Top.jsp" %>
<html>
<head>
  <title><%=company%> Merchant | Accounts >Reports</title>
</head>
<body class="pace-done widescreen fixed-left bodybackground">
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

<%--<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>


<script type="text/javascript" src="/merchant/transactionCSS/js/jquery.js"></script>
<link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
<script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
<script>
  $(document).ready(function(){
    $('#myTable').dataTable();
  });
</script>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script type="text/javascript" src="http://cdn.datatables.net/1.10.2/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="http://maxcdn.bootstrapcdn.com/bootstrap/3.2.0/js/bootstrap.min.js"></script>--%>
<script type="text/javascript">
  $('#sandbox-container input').datepicker({
  });
</script>
<script>
  $(function() {
    $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
    /* $(".datepicker").datepicker({}).datepicker("setValue",new Date);*/
  });
</script>
<%!
  private static Logger logger = new Logger("listWireReportRandomCharges.jsp");
  private static TerminalManager terminalManager = new TerminalManager();
%>
<%
  if (merchants.isLoggedIn(session))
  {
    List<TerminalVO> terminalVO = terminalManager.getTerminalsByMerchantId(session.getAttribute("merchantid").toString());
    String fromdate = null;
    String todate = null;

    Date date = new Date();
    SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

    String Date = originalFormat.format(date);

    fromdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? Date : request.getParameter("fromdate");
    todate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");

    String terminalid =Functions.checkStringNull(request.getParameter("terminalid"))==null?"":request.getParameter("terminalid");
%>
<div class="content-page">
  <div class="content">

    <!-- Page Heading Start -->
    <div class="page-heading">
      <form action="/merchant/servlet/WireReports?ctoken=<%=ctoken%>&copyiframe=<%=copyiframe%>" name="form" method="post">
      <div class="pull-right">
        <div class="btn-group">

            <%
              Enumeration<String> stringEnumeration=request.getParameterNames();
              while(stringEnumeration.hasMoreElements())
              {
                String name=stringEnumeration.nextElement();
                /*if("terminalid".equals(name))
                {
                  out.println("<input type='hidden' name='"+name+"' value='"+request.getParameterValues(name)[1]+"'/>");
                }
                else*/
                out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
              }
            %>
            <%--<button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
              <img style="height: 35px;" src="/merchant/images/goBack.png">
            </button>--%>
          <button type="submit" name="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-arrow-circle-left" aria-hidden="true"></i>&nbsp;&nbsp;<%=listWireReportsRandomCharges_Go_Back%></button>


        </div>
      </div>
      </form>
      <br><br>
      <%--<div class="row">



        <div class="col-sm-12 portlets ui-sortable">

          <div class="widget">

            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Charges Summary</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <div id="horizontal-form">

                <form  name="form" method="post" action="/merchant/servlet/WireReports?ctoken=<%=ctoken%>">

                  <input type="hidden" value="<%=ctoken%>" name="ctoken">

                  <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                  <div class="form-group col-md-3">
                    <label>From</label>
                    <input type="text" size="16" name="fromdate" class="datepicker form-control" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;" value="<%=fromdate%>" readonly="readonly" style="width: auto">
                  </div>

                  <div class="form-group col-md-3">
                    <label>To</label>
                    <input type="text" size="16" name="todate" class="datepicker form-control" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;" value="<%=todate%>" readonly="readonly" style="width: auto">
                  </div>



                  <div class="form-group col-md-3">
                    <label>Terminal ID</label>
                    <select size="1" name="terminalid" class="form-control">
                      <option value="">All</option>
                      <%
                        Iterator i=terminalVO.iterator();
                        while(i.hasNext())
                        {
                          TerminalVO terminalVO1=(TerminalVO) i.next();
                      %>
                      <option value="<%=terminalVO1.getTerminalId()%>" >
                        <%=ESAPI.encoder().encodeForHTMLAttribute(terminalVO1.toString())%>
                      </option>
                      <%
                        }
                      %>
                    </select>
                  </div>

                  <div class="form-group col-md-3">
                    <label >is Paid</label>
                    <select size="1" name="paid" class="form-control">
                      <option value="all">All</option>
                      <option value="paid">Paid</option>
                      <option value="unpaid">Unpaid</option>
                    </select>
                  </div>

                  <div class="form-group col-md-9 has-feedback">&nbsp;</div>

                  &lt;%&ndash;<div class="form-group col-md-3">
                    <button type="submit" name="B1" class="btnblue" style="width:100px;margin-left: 20%; margin-top: 14px;background: rgb(126, 204, 173);">
                      <i class="fa fa-save"></i>
                      Search
                    </button>

                  </div>&ndash;%&gt;

                  <div class="form-group col-md-3">
                    <label>&nbsp;</label>
                    <button type="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;Search</button>

                  </div>

                </form>
              </div>
            </div>
          </div>
        </div>
      </div>--%>


      <div class="row">

        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=listWireReportsRandomCharges_RandomCharges%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content">

              <%

                Functions functions=new Functions();
                Hashtable hash = (Hashtable)request.getAttribute("transdetails");
                Hashtable temphash=null;
                int records=0;
                int totalrecords=0;

                String currentblock=request.getParameter("currentblock");

                if(currentblock==null)
                  currentblock="1";

                try
                {
                  records=Integer.parseInt((String)hash.get("records"));
                  totalrecords=Integer.parseInt((String)hash.get("totalrecords"));
                }
                catch(Exception ex)
                {

                }
                if(hash!=null)
                {
                  hash = (Hashtable)request.getAttribute("transdetails");
                }
                if(records>0)
                {
              %>

              <table class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr style="color: white;">
                  <th style="text-align: center">Sr No</th>
                  <th style="text-align: center">Terminal ID</th>
                  <th style="text-align: center">Charge Name</th>
                  <th style="text-align: center">Charge Rate</th>
                  <th style="text-align: center">Charge Counter</th>
                  <th style="text-align: center">Charge Amount</th>
                  <th style="text-align: center">Charge Value</th>
                  <th style="text-align: center">Charge Value Type</th>
                  <th style="text-align: center">Charge Remark</th>
                </tr>
                </thead>
                <tbody>
                <%
                      String style="class=td1";
                      String ext="light";
                      for(int pos=1;pos<=records;pos++)
                      {
                        String id=Integer.toString(pos);
                        int srno=Integer.parseInt(id);
                        if(pos%2==0)
                        {
                          style="class=tr0";
                          ext="dark";
                        }
                        else
                        {
                          style="class=tr1";
                          ext="light";
                        }
                        temphash=(Hashtable)hash.get(id);
                        out.println("<tr>");
                        out.println("<td data-label=\"Sr No\" style=\"text-align: center\">&nbsp;"+srno+ "</td>");
                        out.println("<td data-label=\"Terminal ID\" style=\"text-align: center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("terminalid"))+"</td>");
                        out.println("<td data-label=\"Charge Name\" style=\"text-align: center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargename"))+"</td>");
                        out.println("<td data-label=\"Charge Rate\" style=\"text-align: center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargerate"))+"</td>");
                        out.println("<td data-label=\"Charge Counter\" style=\"text-align: center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargecounter"))+"</td>");
                        out.println("<td data-label=\"Charge Amount\" style=\"text-align: center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargeamount"))+"</td>");
                        out.println("<td data-label=\"Charge Value\" style=\"text-align: center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargevalue"))+"</td>");
                        out.println("<td data-label=\"Charge Value Type\" style=\"text-align: center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("valuetype"))+"</td>");
                        out.println("<td data-label=\"Charge Remark\" style=\"text-align: center\">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargeremark"))+"</td>");
                        out.println("</tr>");
                      }
                    }
                    else if(functions.isValueNull((String)request.getAttribute("statusMsg")))
                    {
                      out.println(Functions.NewShowConfirmation1(listWireReportsRandomCharges_Result,(String)request.getAttribute("statusMsg")));
                    }
                    else
                    {
                      out.println(Functions.NewShowConfirmation1(listWireReportsRandomCharges_Result, listWireReportsRandomCharges_Randomnot));
                    }
                  }
                  else
                  {
                    response.sendRedirect("/merchant/Logout.jsp");
                    return;
                  }
                %>
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>