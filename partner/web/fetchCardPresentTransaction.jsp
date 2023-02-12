<%@ page import="com.directi.pg.Functions,com.directi.pg.TransactionEntry,com.directi.pg.core.GatewayAccountService,org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.text.ParseException" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 10/8/13
  Time: 2:47 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","CardPresentTransaction");
%>
<%


  HashMap<String, String> timezoneHash = new HashMap<>();
  timezoneHash.put("Etc/GMT+12|(GMT-12:00)","(GMT-12:00) International Date Line West");
  timezoneHash.put("Pacific/Midway|(GMT-11:00","(GMT-11:00) Midway Island, Samoa");
  timezoneHash.put("Pacific/Honolulu|(GMT-10:00)","(GMT-10:00) Hawaii");
  timezoneHash.put("US/Alaska|(GMT-09:00)","(GMT-09:00) Alaska");
  timezoneHash.put("America/Los_Angeles|(GMT-08:00)","(GMT-08:00) Pacific Time (US & Canada)");
  timezoneHash.put("America/Tijuana|(GMT-08:00)","(GMT-08:00) Tijuana, Baja California");
  timezoneHash.put("US/Arizona|(GMT-07:00)","(GMT-07:00) Arizona");
  timezoneHash.put("America/Chihuahua|(GMT-07:00)","(GMT-07:00) Chihuahua, La Paz, Mazatlan");
  timezoneHash.put("US/Mountain|(GMT-07:00)","(GMT-07:00) Mountain Time (US & Canada)");
  timezoneHash.put("America/Managua|(GMT-06:00)","(GMT-06:00) Central America");
  timezoneHash.put("US/Central|(GMT-06:00)","(GMT-06:00) Central Time (US & Canada)");
  timezoneHash.put("America/Mexico_City|(GMT-06:00)","(GMT-06:00) Guadalajara, Mexico City, Monterrey");
  timezoneHash.put("Canada/Saskatchewan|(GMT-06:00)","(GMT-06:00) Saskatchewan");
  timezoneHash.put("America/Bogota|(GMT-05:00)","(GMT-05:00) Bogota, Lima, Quito, Rio Branco");
  timezoneHash.put("US/Eastern|(GMT-05:00)","(GMT-05:00) Eastern Time (US & Canada)");
  timezoneHash.put("US/East-Indiana|(GMT-05:00)","(GMT-05:00) Indiana (East)");
  timezoneHash.put("Canada/Atlantic|(GMT-04:00)","(GMT-04:00) Atlantic Time (Canada)");
  timezoneHash.put("America/Caracas|(GMT-04:00)","(GMT-04:00) Caracas, La Paz");
  timezoneHash.put("America/Manaus|(GMT-04:00)","(GMT-04:00) Manaus");
  timezoneHash.put("America/Santiago|(GMT-04:00)","(GMT-04:00) Santiago");
  timezoneHash.put("Canada/Newfoundland|(GMT-03:30)","(GMT-03:30) Newfoundland");
  timezoneHash.put("America/Sao_Paulo|(GMT-03:00)","(GMT-03:00) Brasilia");
  timezoneHash.put("America/Argentina/Buenos_Aires|(GMT-03:00)","(GMT-03:00) Buenos Aires, Georgetown");
  timezoneHash.put("America/Godthab|(GMT-03:00)","(GMT-03:00) Greenland");
  timezoneHash.put("America/Montevideo|(GMT-03:00)","(GMT-03:00) Montevideo");
  timezoneHash.put("America/Noronha|(GMT-02:00)","(GMT-02:00) Mid-Atlantic");
  timezoneHash.put("Atlantic/Cape_Verde|(GMT-01:00)","(GMT-01:00) Cape Verde Is.");
  timezoneHash.put("Atlantic/Azores|(GMT-01:00)","(GMT-01:00) Azores");
  timezoneHash.put("Africa/Casablanca|(GMT+00:00)","(GMT+00:00) Casablanca, Monrovia, Reykjavik");
  timezoneHash.put("Etc/Greenwich|(GMT+00:00)","(GMT+00:00) Greenwich Mean Time : Dublin, Edinburgh, Lisbon, London");
  timezoneHash.put("Europe/Amsterdam|(GMT+01:00)","(GMT+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna");
  timezoneHash.put("Europe/Belgrade|(GMT+01:00)","(GMT+01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague");
  timezoneHash.put("Europe/Brussels|(GMT+01:00)","(GMT+01:00) Brussels, Copenhagen, Madrid, Paris");
  timezoneHash.put("Europe/Sarajevo|(GMT+01:00)","(GMT+01:00) Sarajevo, Skopje, Warsaw, Zagreb");
  timezoneHash.put("Africa/Lagos|(GMT+01:00)","(GMT+01:00) West Central Africa");
  timezoneHash.put("Asia/Amman|(GMT+02:00)","(GMT+02:00) Amman");
  timezoneHash.put("Europe/Athens|(GMT+02:00)","(GMT+02:00) Athens, Bucharest, Istanbul");
  timezoneHash.put("Asia/Beirut|(GMT+02:00)","(GMT+02:00) Beirut");
  timezoneHash.put("Africa/Cairo|(GMT+02:00)","(GMT+02:00) Cairo");
  timezoneHash.put("Africa/Harare|(GMT+02:00)","(GMT+02:00) Harare, Pretoria");
  timezoneHash.put("Europe/Helsinki|(GMT+02:00)","(GMT+02:00) Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius");
  timezoneHash.put("Asia/Jerusalem|(GMT+02:00)","(GMT+02:00) Jerusalem");
  timezoneHash.put("Europe/Minsk|(GMT+02:00)","(GMT+02:00) Minsk");
  timezoneHash.put("Africa/Windhoek|(GMT+02:00)","(GMT+02:00) Windhoek");
  timezoneHash.put("Asia/Kuwait|(GMT+03:00)","(GMT+03:00) Kuwait, Riyadh, Baghdad");
  timezoneHash.put("Europe/Moscow|(GMT+03:00)","(GMT+03:00) Moscow, St. Petersburg, Volgograd");
  timezoneHash.put("Africa/Nairobi|(GMT+03:00)","(GMT+03:00) Nairobi");
  timezoneHash.put("Asia/Tbilisi|(GMT+03:00)","(GMT+03:00) Tbilisi");
  timezoneHash.put("Asia/Tehran|(GMT+03:30)","(GMT+03:30) Tehran");
  timezoneHash.put("Asia/Muscat|(GMT+04:00)","(GMT+04:00) Abu Dhabi, Muscat");
  timezoneHash.put("Asia/Baku|(GMT+04:00)","(GMT+04:00) Baku");
  timezoneHash.put("Asia/Yerevan|(GMT+04:00)","(GMT+04:00) Yerevan");
  timezoneHash.put("Asia/Kabul|(GMT+04:30)","(GMT+04:30) Kabul");
  timezoneHash.put("Asia/Yekaterinburg|(GMT+05:00)","(GMT+05:00) Yekaterinburg");
  timezoneHash.put("Asia/Karachi|(GMT+05:00)","(GMT+05:00) Islamabad, Karachi, Tashkent");
  timezoneHash.put("Asia/Calcutta|(GMT+05:30)","(GMT+05:30) Chennai, Kolkata, Mumbai, New Delhi");
  timezoneHash.put("Asia/Katmandu|(GMT+05:45)","(GMT+05:45) Kathmandu");
  timezoneHash.put("Asia/Almaty|(GMT+06:00)","(GMT+06:00) Almaty, Novosibirsk");
  timezoneHash.put("Asia/Dhaka|(GMT+06:00)","(GMT+06:00) Astana, Dhaka");
  timezoneHash.put("Asia/Rangoon|(GMT+06:30)","(GMT+06:30) Yangon (Rangoon)");
  timezoneHash.put("Asia/Bangkok|(GMT+07:00)","(GMT+07:00) Bangkok, Hanoi, Jakarta");
  timezoneHash.put("Asia/Krasnoyarsk|(GMT+07:00)","(GMT+07:00) Krasnoyarsk");
  timezoneHash.put("Asia/Hong_Kong|(GMT+08:00)","(GMT+08:00) Beijing, Chongqing, Hong Kong, Urumqi");
  timezoneHash.put("Asia/Kuala_Lumpur|(GMT+08:00)","(GMT+08:00) Kuala Lumpur, Singapore");
  timezoneHash.put("Asia/Irkutsk|(GMT+08:00)","(GMT+08:00) Irkutsk, Ulaan Bataar");
  timezoneHash.put("Australia/Perth|(GMT+08:00)","(GMT+08:00) Perth");
  timezoneHash.put("Asia/Taipei|(GMT+08:00)","(GMT+08:00) Taipei");
  timezoneHash.put("Asia/Tokyo|(GMT+09:00)","(GMT+09:00) Osaka, Sapporo, Tokyo");
  timezoneHash.put("Asia/Seoul|(GMT+09:00)","(GMT+09:00) Seoul");
  timezoneHash.put("Asia/Yakutsk|GMT+09:00)","(GMT+09:00) Yakutsk");
  timezoneHash.put("Australia/Adelaide|(GMT+09:30)","(GMT+09:30) Adelaide");
  timezoneHash.put("Australia/Darwin|(GMT+09:30)","(GMT+09:30) Darwin");
  timezoneHash.put("Australia/Brisbane|(GMT+10:00)","(GMT+10:00) Brisbane");
  timezoneHash.put("Australia/Canberra|(GMT+10:00)","(GMT+10:00) Canberra, Melbourne, Sydney");
  timezoneHash.put("Australia/Hobart|(GMT+10:00)","(GMT+10:00) Hobart");
  timezoneHash.put("Pacific/Guam|(GMT+10:00)","(GMT+10:00) Guam, Port Moresby");
  timezoneHash.put("Asia/Vladivostok|(GMT+10:00)","(GMT+10:00) Vladivostok");
  timezoneHash.put("Asia/Magadan|(GMT+11:00)","(GMT+11:00) Magadan, Solomon Is., New Caledonia");
  timezoneHash.put("Pacific/Auckland|(GMT+12:00)","(GMT+12:00) Auckland, Wellington");
  timezoneHash.put("Pacific/Fiji|(GMT+12:00)","(GMT+12:00) Fiji, Kamchatka, Marshall Is.");
  timezoneHash.put("Pacific/Tongatapu|(GMT+13:00)","(GMT+13:00) Nuku'alofa");

  HashMap<String, String> statusFlagHash = new LinkedHashMap();

  statusFlagHash.put("isChargeback", "Chargeback");
  statusFlagHash.put("isFraud", "Fraud");
  statusFlagHash.put("isRefund", "Refund");
  statusFlagHash.put("isRollingReserveKept", "RollingReserveKept");
  statusFlagHash.put("isRollingReserveReleased", "RollingReserveReleased");
  statusFlagHash.put("isSettled", "Settled");
  statusFlagHash.put("isSuccessful", "Successful");

  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
    TransactionEntry transactionentry = new TransactionEntry();
    SortedMap sortedMap =transactionentry.getSortedMap();
    String memberid = nullToStr(request.getParameter("memberid"));
    String pid = nullToStr(request.getParameter("pid"));
    String Config =null;
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    if(Roles.contains("superpartner")){

    }else{
      pid = String.valueOf(session.getAttribute("merchantid"));
      Config = "disabled";
    }
    String remark = request.getParameter("remark");
    String partnerid = session.getAttribute("partnerId").toString();
    String paymentId=null;
    String str = "";
    String desc = Functions.checkStringNull(request.getParameter("desc"))==null?"":request.getParameter("desc");
    String trackingid = Functions.checkStringNull(request.getParameter("trackingid"))==null?"":request.getParameter("trackingid");
    String customerId = Functions.checkStringNull(request.getParameter("customerid")) == null ? "" : request.getParameter("customerid");
    String firstName=Functions.checkStringNull(request.getParameter("firstname"))== null ? "" : request.getParameter("firstname");
    String lastName=Functions.checkStringNull(request.getParameter("lastname"))== null ? "" : request.getParameter("lastname");
    String emailAddress=Functions.checkStringNull(request.getParameter("emailaddr"))==null?"":request.getParameter("emailaddr");
    paymentId = Functions.checkStringNull(request.getParameter("paymentid"))==null?"":request.getParameter("paymentid");
    String dateType = Functions.checkStringNull(request.getParameter("datetype"));
    String accountId = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
    String pgtypeId = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
    String issuingbank = Functions.checkStringNull(request.getParameter("issuingbank"))==null?"":request.getParameter("issuingbank");
    String flaghash = Functions.checkStringNull(request.getParameter("statusflag"));
    String timezone = Functions.checkStringNull(request.getParameter("timezone"))==null?"":request.getParameter("timezone");
    String fdate=null;
    String tdate=null;
    Date date = new Date();
    SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

    String Date = originalFormat.format(date);
    date.setDate(1);
    String fromDate = originalFormat.format(date);

    fdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDate : request.getParameter("fromdate");
    tdate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");
    String status = Functions.checkStringNull(request.getParameter("status"));
    String startTime = Functions.checkStringNull(request.getParameter("starttime"));
    String endTime = Functions.checkStringNull(request.getParameter("endtime"));
    if (startTime == null) startTime = "00:00:00";
    if (endTime == null) endTime = "23:59:59";
    Calendar rightNow = Calendar.getInstance();
    if (fdate == null) fdate = "" + 1;
    if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);

    String currentyear = "" + rightNow.get(rightNow.YEAR);

    if (dateType == null) dateType = "";
    if (dateType != null) str = str + "&datetype=" + dateType;
    if (fdate != null) str = str + "&fromdate=" + fdate;
    if (tdate != null) str = str + "&todate=" + tdate;
    if (startTime != null) str = str + "&starttime=" + startTime;
    if (endTime != null) str = str + "&endtime=" + endTime;
    if (desc != null) str = str + "&desc=" + desc;
    if (memberid != null) str = str + "&memberid=" + memberid;
    if (trackingid != null) str = str + "&trackingid=" + trackingid;
    if (accountId != null) str = str + "&accountid=" + accountId;
    if (pgtypeId != null) str = str + "&pgtypeid=" + pgtypeId;
    if (customerId != null) str = str + "&customerid=" + customerId;
    if (firstName != null) str = str + "&firstname=" + firstName;
    if (lastName != null) str = str + "&lastname=" + lastName;
    if (emailAddress != null) str = str + "&emailaddr=" + emailAddress;
    if (issuingbank != null) str = str + "&issuingbank=" + issuingbank;
    if (timezone != null) str = str + "&timezone=" + timezone;
    if (pid != null) str = str + "&pid=" + pid;
    str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    boolean archive = Boolean.valueOf(request.getParameter("archive")).booleanValue();
    str = str + "&archive=" + archive;
    String archivalString = "Archived";
    String currentString = "Current";
    if(status!=null)str =str + "&status=" + status;
    else
      status="";
    str = str+"&statusflag="+request.getParameter("statusflag");
    String selectedArchived = "", selectedCurrent = "";
    if (archive)
    {
      selectedArchived = "selected";
      selectedCurrent = "";
    }
    else
    {
      selectedArchived = "";
      selectedCurrent = "selected";
    }
    int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),30);
    str = str + "&SRecords=" + pagerecords;
%>
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
<style type="text/css">
  #ui-id-2
  {
    overflow: auto;
    max-height: 350px;
  }
</style>
<html>
<head>
  <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

  <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">

  <%--<script src="/partner/NewCss/js/jquery-1.12.4.min.js"></script>--%>
  <%--<script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
  <script type="text/javascript" language="JavaScript" src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
  <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
  <script src="/merchant/datepicker/datetimepicker/moment-with-locales.js"></script>
  <script src="/merchant/datepicker/datetimepicker/bootstrap-datetimepicker.min.js"></script>


  <title><%=company%> Transaction Management> Fetch Card Present Transaction</title>
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
    $(function() {
      $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
    });

    $(function() {
      $('#datetimepicker12').datetimepicker({
        format: 'HH:mm:ss',
        useCurrent: true
      });
    });

    $(function() {
      $('#datetimepicker13').datetimepicker({
        format: 'HH:mm:ss',
        useCurrent: true
      });
    });
  </script>

  <script>

    $(document).ready(function(){

      var w = $(window).width();

      //alert(w);

      if(w > 990){
        //alert("It's greater than 990px");
        $("body").removeClass("smallscreen").addClass("widescreen");
        $("#wrapper").removeClass("enlarged");
      }
      else{
        //alert("It's less than 990px");
        $("body").removeClass("widescreen").addClass("smallscreen");
        $("#wrapper").addClass("enlarged");
        $(".left ul").removeAttr("style");
      }
    });

  </script>
</head>
<body>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <form name="form" method="post" action="/partner/net/FetchCardPresentTransaction?ctoken=<%=ctoken%>">
        <div class="row">
          <div class="col-sm-12 portlets ui-sortable">
            <div class="widget">

              <div class="widget-header transparent">
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> Merchant <%=archive ? archivalString : currentString%> Transactions</strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>
              <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
              <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
              <input type="hidden" value="<%=company%>" name="partnername" id="partnername">
              <%
                Functions functions = new Functions();
                String error=(String ) request.getAttribute("error");
                if(functions.isValueNull(error))
                {
                  out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                }
              %>
              <div class="widget-content padding">
                <div id="horizontal-form">
                  <div class="form-group col-md-3">
                  <label>From</label>
                      <input type="text" name="fromdate" id="From_dt" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">
                    </div>
                    <%--<div id="From_div" class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4" style="padding: 0;/*width: inherit;*/">
                      <div class="form-group">
                        <label class="hide_label">From</label>
                        <div class='input-group date' >
                          <input type='text' id='datetimepicker12' class="form-control" placeholder="HH:MM:SS" name="starttime" maxlength="8" value="<%=startTime%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                        </div>
                      </div>
                    </div>--%>

                <div class="form-group col-md-3">
                <label>To</label>
                      <input type="text" name="todate" id="To_dt" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;border-radius: 0;">
                    </div>

                    <%--<div id="From_div" class="form-group col-xs-6 col-sm-6 col-md-6 col-lg-4" style="padding: 0;/*width: inherit;*/">
                      <div class="form-group">
                        <label class="hide_label">To</label>
                        <div class='input-group date' >
                          <input type='text' id='datetimepicker13' class="form-control" placeholder="HH:MM:SS" name="endtime" maxlength="8" value="<%=endTime%>" style="cursor: auto;background-color: #ffffff;opacity: 1;"/>
                        </div>
                      </div>
                    </div>--%>


                 <%-- <div class="form-group col-xs-12 col-sm-6 col-md-6 col-lg-4">
                    <label>Acquirer Bank*</label>
                    <input name="pgtypeid" id="gateway" value="<%=ESAPI.encoder().encodeForHTMLAttribute(pgtypeId)%>" class="form-control" autocomplete="on">
                  </div>--%>

                  <div class="form-group col-md-3">
                    <label>Member ID</label>
                    <input name="memberid" id="memberid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberid)%>" class="form-control">
                  </div>

                  <div class="form-group col-md-3">
                    <label>Acquirer Account ID</label>
                    <input name="accountid" id="accountid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(accountId)%>" class="form-control">
                  </div>
                  <div class="form-group col-md-3">
                    <label >&nbsp;</label>
                    <button type="submit" name="B1" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;Fetch Transaction</button>
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
            <div class="widget-content padding"<%-- style="overflow-x: auto;"--%>>
              <%  String errormsg1 = (String)request.getAttribute("message");
                if (errormsg1 == null)
                {
                  errormsg1 = "";
                }
                else
                {
                  out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" >");
                  out.println(errormsg1);
                  out.println("</font></td></tr></table>");
                }
                if(request.getAttribute("transactionCounter")!=null)
                {
                  int totalCount = (int) request.getAttribute("transactionCounter");
                  int successCount = (int) request.getAttribute("successCounter");
                  int duplicateCount = (int) request.getAttribute("duplicateCounter");
                  if (totalCount > 0)
                  {
                    out.println(Functions.NewShowConfirmation1("Details","Fetch Transaction Details From : "+fdate+" To : "+tdate+ "<BR><BR>Total Transaction Count : " + totalCount + "<BR>Success Count : " + successCount + "<BR>Duplicate Count : " + duplicateCount));
                  }
                  else
                  {
                    out.println(Functions.NewShowConfirmation1("Sorry", "No records found.<br><br>Date :<br>From " + fdate + " <br>To " + tdate));
                  }
                }
                else
                {
                  out.println(Functions.NewShowConfirmation1("Sorry", "No records found.<br><br>Date :<br>From " + fdate + " <br>To " + tdate));
                }
              %>
              <%!
                public static String nullToStr(String str)
                {
                  if(str == null)
                    return "";
                  return str;
                }
                public static String getStatus(String str)
                {
                  if(str.equals("Y"))
                    return "Active";
                  else if(str.equals("N"))
                    return "Inactive";
                  else if(str.equals("T"))
                    return "Test";

                  return str;
                }
              %>
            </div>
          </div>
        </div>
      </div>
      <%
        }
        else
        {
          response.sendRedirect("/partner/logout.jsp");
          return;
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