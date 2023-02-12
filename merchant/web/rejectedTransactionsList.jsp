<%@ page import="com.directi.pg.TransactionLogger" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="com.payment.cardinity.exceptions.ValidationException" %>
<%@ page import="com.manager.enums.TransReqRejectCheck" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@include file="Top.jsp" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
  session.setAttribute("submit","rejectedTransactionsList");
%>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: march 9, 2017
  Time: 4:31:18 PM
  To change this template use File | Settings | File Templates.
--%>

<%

  Functions functions=new Functions();
  //LinkedHashMap memberidDetails=partner.getPartnerMembersDetail((String) session.getAttribute("merchantid"));
  //String memberid=nullToStr(request.getParameter("toid"));

  String str = "";
  str= str + "&ctoken="+ctoken;

  String gateway = Functions.checkStringNull(request.getParameter("gateway"));
  if (gateway == null)
  {gateway = "";}

  String desc = Functions.checkStringNull(request.getParameter("description"));
  if (desc == null)
    desc = "";

  String amt = Functions.checkStringNull(request.getParameter("amount"));
  if (amt == null)
    amt = "";

  String emailaddr = Functions.checkStringNull(request.getParameter("emailaddr"));
  if (emailaddr == null)
    emailaddr = "";

  String orderdesc = Functions.checkStringNull(request.getParameter("orderdesc"));
  if (orderdesc == null)
    orderdesc = "";

  String name1 = Functions.checkStringNull(request.getParameter("name"));
  if (name1 == null)
    name1 = "";

  String trackingid = Functions.checkStringNull(request.getParameter("STrackingid"));
  if (trackingid == null)
    trackingid = "";

  String toid = Functions.checkStringNull(request.getParameter("toid"));
  if (toid == null)
    toid = "";

  String firstsix = Functions.checkStringNull(request.getParameter("firstsix"));
  if (firstsix == null)
    firstsix = "";

  String lastfour = Functions.checkStringNull(request.getParameter("lastfour"));
  if (lastfour == null)
    lastfour = "";

  String rejectreason = Functions.checkStringNull(request.getParameter("rejectreason"));
  if (rejectreason == null)
    rejectreason = "";

  String fdate=null;
  String tdate=null;

  fdate = ESAPI.validator().getValidInput("fromdate",request.getParameter("fromdate"),"fromDate",10,true);
  tdate = ESAPI.validator().getValidInput("todate",request.getParameter("todate"),"fromDate",10,true);

  String startTime    = Functions.checkStringNull(request.getParameter("starttime"));
  String endTime      = Functions.checkStringNull(request.getParameter("endtime"));

  Date date = new Date();
  SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

  String Date = originalFormat.format(date);
  date.setDate(1);
  String fromDate = originalFormat.format(date);

  fdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDate : request.getParameter("fromdate");
  tdate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");

  String status = Functions.checkStringNull(request.getParameter("status"));

  Calendar rightNow = Calendar.getInstance();
  if (fdate == null) fdate = "" + 1;
  if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

  if (startTime == null) startTime = "00:00:00";
  if (endTime == null) endTime = "23:59:59";

  String currentyear = "" + rightNow.get(rightNow.YEAR);

  if (fdate != null) str = str + "&fromdate=" + fdate;
  if (tdate != null) str = str + "&todate=" + tdate;
  if (startTime != null) str = str + "&starttime=" + startTime;
  if (endTime != null) str = str + "&endtime=" + endTime;
  if (desc != null) str = str + "&desc=" + desc;
  if (amt != null) str = str + "&amount=" + amt;
  if (emailaddr != null) str = str + "&emailaddr=" + emailaddr;
  if (name1 != null) str = str + "&name=" + name1;
  if (trackingid != null) str = str + "&STrackingid=" + trackingid;
  if (orderdesc != null) str = str + "&orderdesc=" + orderdesc;
  if (toid != null) str = str + "&toid=" + toid;
  if (status != null) str = str + "&status=" + status;
  if (firstsix != null) str = str + "&firstsix=" + firstsix;
  if (lastfour != null) str = str + "&lastfour=" + lastfour;
  if (gateway !=null) str = str + "&gateway=" + gateway;
  if (rejectreason != null) str = str + "&rejectreason=" + rejectreason;
  if (toid != null) str = str + "&toid=" + toid;

  int pageno = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
  int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 30);

  str = str + "&SRecords=" + pagerecords;


  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String rejectedTransactionsList_rejected_transaction = rb1.getString("rejectedTransactionsList_rejected_transaction");
  String rejectedTransactionsList_from = rb1.getString("rejectedTransactionsList_from");
  String rejectedTransactionsList_to = rb1.getString("rejectedTransactionsList_to");
  String rejectedTransactionsList_rejected_reason = rb1.getString("rejectedTransactionsList_rejected_reason");
  String rejectedTransactionsList_all = rb1.getString("rejectedTransactionsList_all");
  String rejectedTransactionsList_first_six = rb1.getString("rejectedTransactionsList_first_six");
  String rejectedTransactionsList_last_four = rb1.getString("rejectedTransactionsList_last_four");
  String rejectedTransactionsList_amount = rb1.getString("rejectedTransactionsList_amount");
  String rejectedTransactionsList_email_id = rb1.getString("rejectedTransactionsList_email_id");
  String rejectedTransactionsList_name = rb1.getString("rejectedTransactionsList_name");
  String rejectedTransactionsList_description = rb1.getString("rejectedTransactionsList_description");
  String rejectedTransactionsList_search = rb1.getString("rejectedTransactionsList_search");
  String rejectedTransactionsList_summary_data = rb1.getString("rejectedTransactionsList_summary_data");
  String rejectedTransactionsList_note = rb1.getString("rejectedTransactionsList_note");
  String rejectedTransactionsList_date = rb1.getString("rejectedTransactionsList_date");
  String rejectedTransactionsList_tracking_id = rb1.getString("rejectedTransactionsList_tracking_id");
  String rejectedTransactionsList_description1 = rb1.getString("rejectedTransactionsList_description1");
  String rejectedTransactionsList_amount1 = rb1.getString("rejectedTransactionsList_amount1");
  String rejectedTransactionsList_currency = rb1.getString("rejectedTransactionsList_currency");
  String rejectedTransactionsList_first_name = rb1.getString("rejectedTransactionsList_first_name");
  String rejectedTransactionsList_last_name = rb1.getString("rejectedTransactionsList_last_name");
  String rejectedTransactionsList_email = rb1.getString("rejectedTransactionsList_email");
  String rejectedTransactionsList_ip_address = rb1.getString("rejectedTransactionsList_ip_address");
  String rejectedTransactionsList_reject_reason = rb1.getString("rejectedTransactionsList_reject_reason");
  String rejectedTransactionsList_terminal_id = rb1.getString("rejectedTransactionsList_terminal_id");
  String rejectedTransactionsList_showing = rb1.getString("rejectedTransactionsList_showing");
  String rejectedTransactionsList_sorry = rb1.getString("rejectedTransactionsList_sorry");
  String rejectedTransactionsList_no = rb1.getString("rejectedTransactionsList_no");
  String rejectedTransactionsList_filter = rb1.getString("rejectedTransactionsList_filter");
  String rejectedTransactionsList_provide = rb1.getString("rejectedTransactionsList_provide");
  String rejectedTransactionsList_page_no=rb1.getString("rejectedTransactionsList_page_no");
  String rejectedTransactionsList_total_no_of_records=rb1.getString("rejectedTransactionsList_total_no_of_records");
%>
<html>
<head>
  <title><%=company%> Merchant Rejected Transaction</title>
  <style type="text/css">
    #myTable th{text-align: center;}

    #myTable td{
      font-family: Open Sans;
      font-size: 13px;
      font-weight: 600;
    }

    #myTable .button3 {
      text-indent: 0!important;
      width: 100%;
      height: inherit!important;
      display: block;
      color: #000000;
      padding: 0px;
      background: transparent!important;
      border: 0px solid #dedede;
      text-align: center!important;
      font-family: "Open Sans","Helvetica Neue",Helvetica,Arial,sans-serif;
      font-size: 12px;
    }

  </style>

  <style type="text/css">
    .table#myTable > thead > tr > th {font-weight: inherit;text-align: center;}

    .hide_label{
      color: transparent;
      user-select: none;
    }
    .table-condensed a.btn{
      padding: 0;
    }

    .table-condensed .separator{
      padding-left: 0;
      padding-right: 0;
    }

  </style>
</head>
<body class="bodybackground">
<script type="text/javascript" src="/merchant/javascript/jquery.min.js?ver=1"></script>
<script src="/partner/NewCss/js/jquery-ui.min.js"></script>
<script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

<link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
<script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
<link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
<script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>

<title><%=company%> Merchant Transactions</title>
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
<script>
  function download()
  {
    var answer = confirm("do you want to Download File?");
    if(answer == true)
    {
      return true;
    }
    else
    {
      return false;
    }
  }
</script>
<%
  session.setAttribute("submit","rejectedTransactionsList");
%>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">

      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=rejectedTransactionsList_rejected_transaction%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <form  name="form" method="post" action="/merchant/servlet/RejectedTransactionsList?ctoken=<%=ctoken%>">
              <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
              <%
                if(request.getParameter("MES")!=null)
                {
                  String mes=request.getParameter("MES");
                  if(mes.equals("ERR"))
                  {
                    if(request.getAttribute("validationErrorList")!=null)
                    {
                      ValidationErrorList error= (ValidationErrorList) request.getAttribute("validationErrorList");
                      for(Object errorList : error.errors())
                      {
                        ValidationException ve = (ValidationException) errorList;
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + ve.getMessage() + "</h5>");
                      }
                    }
                    else if(request.getAttribute("catchError")!=null)
                    {
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + request.getAttribute("catchError") + "</h5>");
                    }
                  }
                }
              %>
              <div class="widget-content padding">
                <div id="horizontal-form">
                  <%--<div class="form-group col-md-4 has-feedback">--%>
                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-4">

                      <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                    <label><%=rejectedTransactionsList_from%></label>
                    <input type="text" size="16" name="fromdate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                  </div>
                      <div id="From_div" class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4" style="padding: 0;/*width: inherit;*/">
                        <div class="form-group">
                          <label class="hide_label">From</label>
                          <div class='input-group date' >
                            <input type='text' id='datetimepicker12' class="form-control"  placeholder="HH:MM:SS" name="starttime" maxlength="8" value="<%=startTime%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                            <%--<div id="datetimepicker12"></div>--%>
                          </div>
                        </div>
                      </div>
                    </div>
                  <%--<div class="form-group col-md-4 has-feedback">--%>
                    <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-4">

                      <div class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-8" style="padding: 0;">
                    <label><%=rejectedTransactionsList_to%></label>
                    <input type="text" size="16" name="todate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                  </div>
                      <div id="From_div" class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4" style="padding: 0;/*width: inherit;*/">
                        <div class="form-group">
                          <label class="hide_label">To</label>
                          <div class='input-group date' >
                            <input type='text' id='datetimepicker13' class="form-control" placeholder="HH:MM:SS" name="endtime" maxlength="8" value="<%=endTime%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                          </div>
                        </div>
                      </div>
                    </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label><%=rejectedTransactionsList_rejected_reason%></label>
                    <select size="1" name="rejectreason" class="form-control">
                      <option value=""><%=rejectedTransactionsList_all%></option>
                      <%
                        String selected2="";
                        for (TransReqRejectCheck transReqRejectCheck: TransReqRejectCheck.values()){
                          if (transReqRejectCheck.name().equals(rejectreason))
                            selected2 = "selected";
                          else
                            selected2 = "";
                      %>
                      <option value="<%=transReqRejectCheck.name()%>" <%=selected2%>><%=transReqRejectCheck.name()%></option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                 <%-- <div class="form-group col-md-2 has-feedback">--%>
                    <div class="form-group col-xs-12 col-md-12 has-feedback" style="margin:0; padding: 0;"></div>
                    <div class="form-group col-md-4 has-feedback">
                    <label ><%=rejectedTransactionsList_first_six%></label>
                    <input name="firstsix" size="6" class="form-control" maxlength="6" value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstsix)%>">
                  </div>
                  <%--<div class="form-group col-md-2 has-feedback">--%>
                    <div class="form-group col-md-4 has-feedback">
                    <label ><%=rejectedTransactionsList_last_four%></label>
                    <input name="lastfour" size="4" class="form-control" maxlength="4" value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastfour)%>">
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label><%=rejectedTransactionsList_amount%></label>
                    <input type=text name="amount" maxlength="10"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(amt)%>"  class="form-control" size="10">
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label><%=rejectedTransactionsList_email_id%></label>
                    <input type=text name="emailaddr" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailaddr)%>" class="form-control" size="20">
                  </div>
                  <%--<div class="form-group col-md-4 has-feedback">
                    <label>Merchant ID</label>

                    <input name="toid" id="mid" value="<%=memberid%>" class="form-control" autocomplete="on">
                  </div>--%>
                  <div class="form-group col-md-4 has-feedback">
                    <label><%=rejectedTransactionsList_name%></label>
                    <input type=text name="name" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(name1)%>" class="form-control" size="10">
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label><%=rejectedTransactionsList_description%></label>
                    <input type=text name="description" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>" class="form-control" size="10">
                  </div>
                 <%-- <div class="form-group col-md-4 has-feedback">
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                  </div>--%>
                  <div class="form-group col-md-4 has-feedback">
                    <label style="color: transparent;"><%=rejectedTransactionsList_search%></label>
                    <button type="submit" name="B1" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-clock-o"></i>&nbsp;&nbsp;<%=rejectedTransactionsList_search%></button>
                  </div>
                </div>
              </div>
            </form>
          </div>
        </div>
      </div>
      <%--start report data--%>
      <div class="row">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=rejectedTransactionsList_summary_data%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <%----------------------Report Start-----------------------------------%>
              <%
                if(request.getAttribute("transactionsdetails")!=null)
                {
                  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                  Hashtable hash = (Hashtable) request.getAttribute("transactionsdetails");
                  Hashtable temphash = null;

                  str = str + "&firstsix=" + firstsix;
                  str = str + "&lastfour=" + lastfour;
                  str = str + "&emailaddr=" + emailaddr;
                  str = str + "&toid=" + toid;
                  str = str + "&name=" + name1;
                  str = str + "&desc=" + desc;
                  str = str + "&ctoken=" + ctoken;
                  str = str + "&rejectreason=" + rejectreason;

                  String error=(String ) request.getAttribute("errormessage");
                  if(error !=null)
                  {
                    out.println("<b>");
                    out.println(error);
                    out.println("</b>");
                  }
                  int records = 0;
                  int totalrecords = 0;
                  int currentblock = 1;
                  try
                  {
                    records = Integer.parseInt((String) hash.get("records"));
                    totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
                    currentblock = Integer.parseInt(request.getParameter("currentblock"));
                  }
                  catch (Exception ex)
                  {
                  }
                  String style = "class=tr0";
                  if (records > 0)
                  {
              %>

              <div id="showingid"><strong><%=rejectedTransactionsList_note%></strong></div>
              <div class="pull-right">
                <div class="btn-group">
                  <form name="exportform" method="post" action="/merchant/servlet/RejectedExportTransactions?ctoken=<%=ctoken%>" >
                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" name="fdate">
                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" name="tdate">
                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(rejectreason)%>" name="rejectreason">
                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(firstsix)%>" name="firstsix">
                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(lastfour)%>" name="lastfour">
                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailaddr)%>" name="emailaddr">
                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(toid)%>" name="toid">
                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(name1)%>" name="name">
                    <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>" name="description">
                    <input type="hidden" value="<%=ctoken%>" name="ctoken">

                    <button class="btn-xs" type="submit" style="background: white;border: 0;">
                      <img style="height: 40px;" src="/merchant/images/excel.png">
                    </button>
                  </form>
                </div>
              </div>

              <div id="containrecord"></div>

              <div class="widget-content padding" style="overflow-x: auto;">

                <table id="myTable" class="display table table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                  <thead>
                  <tr style="background-color: #7eccad !important;color: white;">
                    <th><%=rejectedTransactionsList_date%></th>
                    <th><%=rejectedTransactionsList_tracking_id%></th>
                    <%--<th>Merchant ID</th>--%>
                    <%--<th>Totype</th>--%>
                    <th><%=rejectedTransactionsList_description1%></th>
                    <th><%=rejectedTransactionsList_amount1%></th>
                    <th><%=rejectedTransactionsList_currency%></th>
                    <th><%=rejectedTransactionsList_first_name%></th>
                    <th><%=rejectedTransactionsList_last_name%></th>
                    <th><%=rejectedTransactionsList_email%></th>
                    <th><%=rejectedTransactionsList_ip_address%></th>
                    <th><%=rejectedTransactionsList_reject_reason%></th>
                    <th><%=rejectedTransactionsList_terminal_id%></th>
                  </tr>
                  </thead>
                  <tbody>
                  <input type="hidden" value="<%=ctoken%>" name="ctoken">
                  <%
                    StringBuffer requestParameter = new StringBuffer();
                    Enumeration<String> stringEnumeration = request.getParameterNames();
                    while(stringEnumeration.hasMoreElements())
                    {
                      String name=stringEnumeration.nextElement();
                      if("SPageno".equals(name) || "SRecords".equals(name))
                      {

                      }
                      else
                        requestParameter.append("<input type='hidden' name='"+name+"' value=\""+request.getParameter(name)+"\"/>");
                    }
                    requestParameter.append("<input type='hidden' name='SPageno' value='"+pageno+"'/>");
                    requestParameter.append("<input type='hidden' name='SRecords' value='"+pagerecords+"'/>");

                    for (int pos = 1; pos <= records; pos++)
                    {
                      String id = Integer.toString(pos);
                      int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);
                      style = "class=\"tr" + pos % 2 + "\"";
                      temphash = (Hashtable) hash.get(id);
                      String date2 = Functions.convertDtstampToDBFormat((String) temphash.get("dtstamp"));
                      String searchId = (String) temphash.get("id");
                      //String merchantId = (String) temphash.get("toid");
                      //String totype = (String) temphash.get("totype");
                      String description = (String) temphash.get("description");
                      String amount = (String)temphash.get("amount");
                      String currency = (String)temphash.get("currency");
                      String terminalId = (String) temphash.get("terminalid");
                      String firstName = (String) temphash.get("firstname");
                      String lastName = (String) temphash.get("lastname");
                      String email = (String) temphash.get("email");
                      String requestedIp = (String) temphash.get("requestedip");
                      String rejectReason = (String) temphash.get("rejectreason");
                      //System.out.println("1---"+rejectReason);

                      if(temphash.get("rejectreason").toString().contains("<BR>"))
                      {
                        rejectReason = ((String)temphash.get("rejectreason")).replaceAll("<BR>","\n");
                        //System.out.println(rejectReason);
                      }

                      if(!functions.isValueNull(requestedIp))
                      {
                        requestedIp = "-";
                      }
                      if(!functions.isValueNull(amount))
                      {
                        amount = "-";
                      }
                      if(!functions.isValueNull(currency))
                      {
                        currency = "-";
                      }
                      if(!functions.isValueNull(terminalId))
                      {
                        terminalId="-";
                      }
                      if(!functions.isValueNull(description))
                      {
                        description="-";
                      }
                      if(!functions.isValueNull(firstName))
                      {
                        firstName="-";
                      }
                      if (!functions.isValueNull(lastName))
                      {
                        lastName="-";
                      }
                      if(!functions.isValueNull(email))
                      {
                        email="-";
                      }

                      /*if(!functions.isValueNull(totype))
                      {
                        totype="-";
                      }*/

                      out.println("<tr " + style + ">");
                      out.println("<td align=center >" + date2 + "</td>");
                      out.println("<td align=center><form action=\"/merchant/servlet/RejectedTransactionDetails?ctoken=" + ctoken + "\" method=\"post\"><input type=\"hidden\" name=\"action\" value=\"RejectedTransactionDetails\"><input type=\"hidden\" name=\"STrackingid\" value=\"" + searchId + "\"><input type=\"hidden\" name=\"ctoken\" value=\"" + ctoken + "\"><input type=\"hidden\" name=\"archive\" value=\"false\"><input type=\"hidden\" name=\"accountid\" value=\"" + searchId + "\"><input type=\"submit\" class=\"goto\" name=\"submit\" value=\"" + searchId + "\">");
                      out.println(requestParameter.toString());
                      out.print("</form></td>");
                     // out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(merchantId) + "</td>");
                      //out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(totype) + "</td>");
                      out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(description) + "</td>");
                      out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(amount) + "</td>");
                      out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(currency) + "</td>");
                      out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(firstName) + "</td>");
                      out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(lastName) + "</td>");
                      out.println("<td align=center>" + functions.getEmailMasking(email)+ "</td>");
                      out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(requestedIp) + "</td>");
                      out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(rejectReason) + "</td>");
                      out.println("<td align=center>" + ESAPI.encoder().encodeForHTML(terminalId) + "</td>");
                      out.println("</tr>");
                    }
                  %>
                  </tbody>
                </table>

              </div>

            </div>
          </div>
        </div>
      </div>
      <%
        int TotalPageNo;
        if(totalrecords%pagerecords!=0)
        {
          TotalPageNo =totalrecords/pagerecords+1;
        }
        else
        {
          TotalPageNo=totalrecords/pagerecords;
        }
      %>
      <div id="showingid"><strong><%=rejectedTransactionsList_page_no%> <%=pageno%>  of  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
      <div id="showingid"><strong><%=rejectedTransactionsList_total_no_of_records%>   <%=totalrecords%> </strong></div>

      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
        <jsp:param name="numrows" value="<%=pagerecords%>"/>
        <jsp:param name="pageno" value="<%=pageno%>"/>
        <jsp:param name="str" value="<%=str%>"/>
        <jsp:param name="page" value="RejectedTransactionsList"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
      <%
          }
          else
          {
            out.println(Functions.NewShowConfirmation1(rejectedTransactionsList_sorry, rejectedTransactionsList_no));
          }
        }
        else
        {
          out.println(Functions.NewShowConfirmation1(rejectedTransactionsList_filter,rejectedTransactionsList_provide));
        }
      %>
    </div>
  </div>
</div>
<script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
<script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
<link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
<link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
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
