<%@ page import="com.invoice.vo.ProductList" %>
<%@ page import="java.util.*" %>
<%@ page import="com.invoice.vo.InvoiceVO" %>
<%@ page import="sun.font.Script" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ include file="ietest.jsp" %>
<%@ include file="Top.jsp"%>

<%
  String memberid = (String)session.getAttribute("merchantid");
  //String currency =  (String)session.getAttribute("currency");
  Hashtable hiddenvariables = (Hashtable)request.getAttribute("hiddenvariables");

%>


<html>
<head>
  <title>Merchant Order Payments Confirmation</title>

  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

</head>


<body class="pace-done widescreen fixed-left-void bodybackground">
<%--<div class="rowcontainer-fluid " >
    <div class="row rowadd" >
        <div class="col-md-8 col-md-offset-2" style="margin-left: 0;width:99.666667%">
            <div class="form foreground bodypanelfont_color panelbody_color">
                <h2 class="col-md-12 background panelheading_color headpanelfont_color" style="color:#34495e" ><i class="fa fa-th-large"></i>&nbsp;&nbsp;Invoice Confirmation</h2>
                <hr class="hrform">
            </div>--%>
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
  String status="";
  String message="";
  if(functions.isValueNull((String)request.getAttribute("status")))
    status = (String)request.getAttribute("status");
  else
    status = (String) dataHash.get("status");

  if(functions.isValueNull((String)request.getAttribute("statusDesc")))
    message = (String)request.getAttribute("statusDesc");
  else
    message = (String) dataHash.get("statusDesc");

%>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <form action="" name="myformname" method="post" accept-charset="UTF-8">

        <div class="row">
          <div class="col-sm-12 portlets ui-sortable">
            <div class="widget">

              <div class="widget-header transparent">
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Transaction Status</strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>



      <%--<div class="bg-info" style="text-align:center;background-color: #ffffff;color: #000000;border-left: 4px solid #34495e;border: #ffffff"><%=status%></div>
      <br/>--%>
      <b><div class="bg-info" style="text-align:center;background-color: #ffffff;color: #000000;border-left: 4px solid #34495e;border: #ffffff"><%=message%></div></b>
            </div>
          </div>
        </div>
        </form>
      </div>
    </div>
  </div>
</body>
</html>
