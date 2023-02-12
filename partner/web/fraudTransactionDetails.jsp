<%@ page import="com.directi.pg.Functions,com.directi.pg.TransactionEntry,org.owasp.esapi.ESAPI,org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%@ page import="com.fraud.FraudSystemService" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Naushad
  Date: 03/01/18
  Time: 2:47 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%!
  private static Logger logger=new Logger("fraudTransactionDetails.jsp");
%>

<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","Fraud Transactions List");
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
%>

<%
 // LinkedHashMap memberidDetails=partner.getPartnerMembersDetail((String) session.getAttribute("merchantid"));
  /*String memberid = request.getParameter("memberid");*/
  String memberid=nullToStr(request.getParameter("memberid"));
  String partnerid = session.getAttribute("partnerId").toString();

  Hashtable statushash = new Hashtable();
  statushash.put("Pending", "Pending");
  statushash.put("Process Failed", "Process Failed");
  statushash.put("Process Successfully", "Process Successfully");

  Hashtable gatewayHash = FraudSystemService.getFraudSystem();

  String fsid =Functions.checkStringNull(request.getParameter("fsid"));
  if (fsid == null)
  {fsid = "";}


  String membertrandid = Functions.checkStringNull(request.getParameter("toid"));
  if (membertrandid == null)
    membertrandid = "";

  String fraudtransid = Functions.checkStringNull(request.getParameter("fraudtransid"));
  if (fraudtransid == null)
    fraudtransid = "";



  String trackingid = Functions.checkStringNull(request.getParameter("STrackingid"));
  if (trackingid == null)
    trackingid = "";

  String status = Functions.checkStringNull(request.getParameter("fstransstatus"));
  String fdate=null;
  String tdate=null;
  try
  {

    fdate = ESAPI.validator().getValidInput("fromdate",request.getParameter("fromdate"),"fromDate",10,true);
    tdate = ESAPI.validator().getValidInput("todate",request.getParameter("todate"),"fromDate",10,true);
  }
  catch(ValidationException e)
  {
    logger.error("Date FOrmat Exception",e);
  }

  Date date = new Date();
  SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

  String Date = originalFormat.format(date);
  date.setDate(1);
  String fromDate = originalFormat.format(date);

  fdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDate : request.getParameter("fromdate");
  tdate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");

  Calendar rightNow = Calendar.getInstance();
  String str = "";
  if (fdate == null) fdate = "" + 1;
  if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

  String currentyear = "" + rightNow.get(rightNow.YEAR);

  if (fdate != null) str = str + "fromdate=" + fdate;
  if (tdate != null) str = str + "&todate=" + tdate;

  if (trackingid != null) str = str + "&STrackingid=" + trackingid;
  if (membertrandid != null) str = str + "&toid=" + membertrandid;
  if (status != null) str = str + "&status=" + status;
  if (fsid !=null) str = str + "&fsid=" + fsid;
  boolean archive = Boolean.valueOf(request.getParameter("archive")).booleanValue();
  str = str + "&archive=" + archive;

  int pageno = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
  int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 30);

%>

<html>
<head>
  <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

  <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
  <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

  <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>

  <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>

  <title><%=company%> Fraud Transactions Detail</title>
  <script language="javascript">
    function isint(form)
    {
      if (isNaN(form.numrows.value))
        return false;
      else
        return true;
    }
  </script>
  <script type="text/javascript">

    $('#sandbox-container input').datepicker({
    });
  </script>
  <script>
    $(function() {
      $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
    });
  </script>
</head>
<body>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <form name="form" method="post" action="/partner/net/FraudTransactionList?ctoken=<%=ctoken%>">
        <div class="row">
          <div class="col-sm-12 portlets ui-sortable">
            <div class="widget">

              <div class="widget-header transparent">
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp; Fraud Transactions Detail</strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>
              <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken" id="ctoken">
              <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
              <%
                Functions functions = new Functions();
                String error=(String ) request.getAttribute("errormessage");
                if(functions.isValueNull(error))
                {
                  out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                }
              %>
              <div class="widget-content padding">
                <div id="horizontal-form">
                  <div class="form-group col-md-4 has-feedback">
                    <label >From</label>
                    <input type="text" size="16" name="fromdate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label>To</label>
                    <input type="text" size="16" name="todate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                  </div>
                  <%--<div class="form-group col-md-4 has-feedback">
                    <label>Fraud ID</label>
                    <input type=text name="fraudtransid" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudtransid)%>" class="form-control" size="20">

                  </div>--%>

                  <div class="form-group col-md-4 has-feedback">
                    <label >Merchant Id </label>
                    <input name="memberid" id="mid" value="<%=memberid%>" class="form-control" autocomplete="on">
                   <%-- <select name="memberid" class="form-control">
                      <option value="">--Select Merchant Id--</option>
                      <%
                        String isSelected="";
                        for(Object mid : memberidDetails.keySet())
                        {
                          if(mid.toString().equals(memberid))
                          {
                            isSelected="selected";
                          }
                          else
                          {
                            isSelected="";
                          }
                      %>
                      <option value="<%=mid%>" <%=isSelected%>><%=mid+"-"+memberidDetails.get(mid)%></option>
                      <%
                        }
                      %>
                    </select>--%>
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label>Status</label>
                    <select size="1" name="fstransstatus" class="form-control">
                      <option value="">All</option>
                      <%
                        Enumeration enu = statushash.keys();
                        String selected = "";
                        String key = "";
                        String value = "";


                        while (enu.hasMoreElements())
                        {
                          key = (String) enu.nextElement();
                          value = (String) statushash.get(key);

                          if (key.equals(status))
                            selected = "selected";
                          else
                            selected = "";

                      %>
                      <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key)%>" <%=selected%>><%=ESAPI.encoder().encodeForHTML(value)%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>

                  <%--<div class="form-group col-md-4 has-feedback">
                    <label>Fraud System</label>
                    <select size="1" name="fsid" class="form-control">
                      <option value="">All</option>
                      <%
                        Enumeration enu2 = gatewayHash.keys();
                        String selected2 = "";
                        String key2 = "";
                        String value2 = "";


                        while (enu2.hasMoreElements())
                        {
                          key2 = (String) enu2.nextElement();
                          value2 = (String) gatewayHash.get(key2);

                          if (key2.equals(fsid))
                            selected2 = "selected";
                          else
                            selected2 = "";

                      %>
                      <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key2)%>" <%=selected2%>><%=ESAPI.encoder().encodeForHTML(value2)%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>--%>

                  <div class="form-group col-md-4 has-feedback">
                    <label >Tracking ID</label>
                    <input type="text" maxlength="10" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>"  name="STrackingid" size="10" class="form-control">
                  </div>

                  <div class="form-group col-md-4">
                    <label style="color: transparent;">Path</label>
                    <button type="submit" class="btn btn-default" name="B1" style="display:block;">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Search
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </form>

      <%--Start Report Data--%>
      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Report Table</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-x: auto;">
              <%
                Hashtable hash = (Hashtable) request.getAttribute("transactionsdetails");
                Hashtable fraudRuleHash = (Hashtable) hash.get("fraudRule");
                Hashtable innerhash = null;
                if (hash != null && hash.size() > 0)
                {
                  String style = "class=tr0";

                  innerhash = (Hashtable) hash.get(1 + "");
                  int pos = 0;
                  value = (String) innerhash.get("trackingid");
                  if (value == null)
                    value = "-";
              %>

              <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr>
                  <td  colspan="5" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Fraud Transaction Details</b></td>
                </tr>
                </thead>

                <tr>
                  <td colspan="3" valign="middle" align="left" >Tracking ID:</td>
                  <td colspan="2" valign="middle" align="left"> <%=ESAPI.encoder().encodeForHTML(value)%></td>
                </tr>
                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("fstransid");
                  if (value == null)
                    value = "-";
                %>
                <tr>
                  <td colspan="3" valign="middle" align="left" >Fraud ID:</td>
                  <td colspan="2" valign="middle" align="left" > <%=ESAPI.encoder().encodeForHTML(value)%></td>
                </tr>
                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("memberid");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td colspan="3" valign="middle" align="left" >Merchant ID:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>
                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("member_transid");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >Merchant Trans ID:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("firstname");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >First Name:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("lastname");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >Last Name:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("emailaddr");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >Email Address:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("address1");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >Address:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("city");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >City:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("state");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >State:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("countrycode");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >Country Code:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("zip");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >Zip:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("phone");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >Phone:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("ipaddrs");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >IP Address:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("firstsix");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >First Six:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("lastfour");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >Last Four:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("dailycardminlimit");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >Daily Card Minimum Limit:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("dailycardlimit");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >Daily Card Limit:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("weeklycardlimit");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >Weekly Card Limit:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("monthlycardlimit");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >Monthly Card Limit:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("paymenttype");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >Payment Type:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>


                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("website");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >Website:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("username");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >User Name:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("usernumber");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >User Number:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("currency");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >Currency:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>






                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("score");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td colspan="3" valign="middle" align="left" >Score:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML(value)%></td>
                </tr>
                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("fsid");
                  if (value == null)
                    value = "-";
                %>
                <tr>
                  <td colspan="3" valign="middle" align="left" >Fraud System Name:</td>
                  <td colspan="2" valign="middle" align="left" ><<%=ESAPI.encoder().encodeForHTML(FraudSystemService.getFSGateway(value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("amount");
                  if (value == null)
                    value = "-";
                %>
                <tr>
                  <td colspan="3" valign="middle" align="left" >Transaction Amount:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                  </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("fraudtransstatus");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td colspan="3" valign="middle" align="left" >Fraud Transaction Status:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML(value)%></td>
                </tr>
                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("dtstamp");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td colspan="3" valign="middle" align="left" >Date:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML(value)%></td>
                </tr>
                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("fs_responsecode");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                <td colspan="3" valign="middle" align="left" >Response Code:</td>
                <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
              </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("fs_responsedesc");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td colspan="3" valign="middle" align="left" >Response Description:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("fs_responserec");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td colspan="3" valign="middle" align="left" >Recommendation:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("isAlertSent");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td colspan="3" valign="middle" align="left" >Is AlertSent:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("isReversed");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td colspan="3" valign="middle" align="left" >Is Reversed:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("checkstatus");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td colspan="3" valign="middle" align="left" >Status:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>


                <%
                  }
                %>

                <thead>
                <tr>
                  <td  colspan="5" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Fraud Rules Triggered</b></td>
                </tr>
                </thead>

                <tr>
                  <td colspan="3" valign="middle" align="left" style="background-color: #7eccad !important;color: white;padding-top: 4px;padding-bottom: 4px;">Fraud Rule Name</td>
                  <td colspan="3" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 4px;padding-bottom: 4px;">Fraud Rule Score</td>
                </tr>

                <%
                  if(fraudRuleHash != null && fraudRuleHash.size() > 0)
                  {
                    Set set = fraudRuleHash.keySet();
                    Iterator iterator = set.iterator();
                    while(iterator.hasNext())
                    {
                      String ruleName = (String)iterator.next();
                %>
                <tr>
                  <td colspan="3" class="tr1" align="left"><%=ESAPI.encoder().encodeForHTML(ruleName)%></td>
                  <td colspan="3" class="tr1" align="center"><%=ESAPI.encoder().encodeForHTML((String)fraudRuleHash.get(ruleName))%></td>
                </tr>
                <%
                  }
                }
                else
                {
                %>
                <tr><td colspan="2" class="tr1" align="right">No Triggered Rules Found</td></tr>
                <%
                  }
                %>

                <%--<tr>
                  <td valign="middle" align="center" data-label="FEES" ><%=chargeDetailsVO.getChargeName()%></td>
                  <td valign="middle" align="center" data-label="Rates/Fees" ><%=chargeDetailsVO.getChargeValue()%></td>
                </tr>--%>


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
<%!
  public static String nullToStr(String str)
  {
    if(str == null)
      return "";
    return str;
  }
%>
