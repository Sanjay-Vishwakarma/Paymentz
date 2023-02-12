<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.manager.vo.TransactionDetailsVO" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%--
  Created by IntelliJ IDEA.
  User: Diksha
  Date: 28-Aug-20
  Time: 2:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="top.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","bankTransactionReportDetails");
%>
<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
  <title>Bank Transaction Report Details</title>
  <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">

  <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">

  <script type="text/javascript" language="JavaScript" src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>
  <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>
  <script src="/merchant/datepicker/datetimepicker/moment-with-locales.js"></script>
  <script src="/merchant/datepicker/datetimepicker/bootstrap-datetimepicker.min.js"></script>

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
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (partner.isLoggedInPartner(session))
  {
    String gateway = Functions.checkStringNull(request.getParameter("gateway"));
    String style="class=\"tr0\"";
    String accountid = Functions.checkStringNull(request.getParameter("accountid"));
    String merchantid=Functions.checkStringNull(request.getParameter("merchantid"));

    String str="";
    Date date = new Date();
    SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

    String Date = originalFormat.format(date);
    date.setDate(1);
    String fromDate = originalFormat.format(date);

    String startDate=request.getParameter("fromdate")==null?fromDate:request.getParameter("fromdate");
    String startTime=request.getParameter("starttime")==null?"00:00:00":request.getParameter("starttime");
    String endDate=request.getParameter("todate")==null?Date:request.getParameter("todate");
    String endTime=request.getParameter("endtime")==null?"23:59:59":request.getParameter("endtime");
    String dayLight = request.getParameter("daylightsaving");


    Calendar rightNow = Calendar.getInstance();
    if (startDate == null) startDate = "" + 1;
    if (endDate == null) endDate = "" + rightNow.get(rightNow.DATE);


    if (startDate != null) str = str + "&fromdate=" + startDate;
    if (endDate != null) str = str + "&todate=" + endDate;

    String currency = "";
    Functions functions=new Functions();
    if(functions.isValueNull(request.getParameter("gateway"))){
      String aGatewaySet[] = request.getParameter("gateway").split("-");
      if (aGatewaySet.length == 3){
        currency = aGatewaySet[1];
      }
    }
    String partnerid= String.valueOf(session.getAttribute("partnerId"));
    String partnerId=null;
    String pid=null;
    String Config=null;
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("partnerId")));
    if(Roles.contains("superpartner") || Roles.contains("childsuperpartner"))
    {
      pid = String.valueOf(request.getParameter("partnerName"));
    }
    else
    {
     // pid = String.valueOf(session.getAttribute("merchantid"));
      pid = String.valueOf(request.getParameter("partnerName"));
      Config="disabled";
    }

    String memberid = Functions.checkStringNull(request.getParameter("memberid")) == null ? "" : request.getParameter("memberid");
    String pgtypeid = Functions.checkStringNull(request.getParameter("gateway")) == null ? "" : request.getParameter("gateway");
    String terminalId= Functions.checkStringNull(request.getParameter("terminalid")==null ? "" : request.getParameter("terminalid"));
    String BankTransactionInquiry=request.getAttribute("BankTransactionInquiry")==null ? "" : (String) request.getAttribute("BankTransactionInquiry");
    String successCounter=request.getAttribute("successCounter")==null ? "" : (String) request.getAttribute("successCounter");
    String failCounter=request.getAttribute("failCounter")==null ? "" : (String) request.getAttribute("failCounter");
    String authStarted=request.getAttribute("authStarted")==null ? "" : (String) request.getAttribute("authStarted");

    TerminalManager terminalManager = new TerminalManager();
    List<TerminalVO> terminalList = terminalManager.getAllMappedTerminals();
    List<TransactionDetailsVO> transactionDetailsVOs = (List<TransactionDetailsVO>)request.getAttribute("trackinglist");
    List<TerminalVO> accountList = (List<TerminalVO>) request.getAttribute("accountList");

    String errorMsg =(String) request.getAttribute("errormsg");

    String timeDifference =(String) request.getAttribute("timediff");

    if(timeDifference==null || timeDifference.equalsIgnoreCase("null"))
    {
      timeDifference="";
    }
    if (gateway == null)
    {
      gateway = "";
    }
    if (accountid== null)
    {
      accountid = "";
    }
    if(merchantid==null)
    {
      merchantid="";
    }
    if(terminalId==null)
    {
      terminalId="";
    }

    TreeMap<String,TerminalVO> memberMap = new TreeMap<String, TerminalVO>();
    TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();
    TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();
    for(TerminalVO terminalVO : terminalList)
    {
      String memberKey = terminalVO.getMemberId()+"-"+terminalVO.getAccountId()+"-"+terminalVO.getGateway().toUpperCase() + "-" + terminalVO.getCurrency() + "-" + terminalVO.getGateway_id();
      memberMap.put(memberKey,terminalVO);
    }
%>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <form name="forms" method="post" action="/partner/net/BankTransactionReport?ctoken=<%=ctoken%>" >
        <div class="row">
          <div class="col-sm-12 portlets ui-sortable">
            <div class="widget">

              <div class="widget-header transparent">
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> Bank Transaction Report</strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>
              <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
              <input type="hidden" value="<%=company%>" name="partnername" id="partnername">
              <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
              <%
                if (functions.isValueNull(errorMsg))
                {
                  out.println("<p><h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errorMsg + "</h5></p>");
                }
              %>

              <div class="widget-content padding">
                <div id="horizontal-form">

                  <div class="form-group col-md-4"  style="display: inline-block;">
                    <label>Start Date</label>
                    <input type="text" name="fromdate" class="datepicker form-control"
                           value="<%=ESAPI.encoder().encodeForHTMLAttribute(startDate)%>"
                           readonly="readonly"
                           style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Start Time(HH:MM:SS)</label>
                    <input type='text' id='datetimepicker12' class="form-control" placeholder="HH:MM:SS" name="starttime" maxlength="8" value="<%=request.getParameter("starttime")==null?"00:00:00":request.getParameter("starttime")%>" style="border: 1px solid #b2b2b2;font-weight:bold">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Day Light Saving</label>
                    <select name='daylightsaving' class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold">
                      <%
                        if ("Y".equalsIgnoreCase(dayLight))
                        {
                      %>
                      <option value="N">No</option>
                      <option value="Y" selected>Yes</option>
                      <%
                      }
                      else
                      {
                      %>
                      <option value="N">No</option>
                      <option value="Y">Yes</option>
                      <%
                        }
                      %>
                    </select>
                  </div>
                  <div class="form-group col-md-4"  style="display: inline-block;">
                    <label>End Date</label>
                    <input type="text" name="todate" class="datepicker form-control"
                           value="<%=ESAPI.encoder().encodeForHTMLAttribute(endDate)%>"
                           readonly="readonly"
                           style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">End Time(HH:MM:SS)</label>
                    <input type='text' id='datetimepicker13' class="form-control" placeholder="HH:MM:SS" name="endtime" maxlength="8" value="<%=request.getParameter("endtime")==null?"23:59:59":request.getParameter("endtime")%>" style="border: 1px solid #b2b2b2;font-weight:bold">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <%
                      String time = (gateway.equals("")||gateway.equals("null")) ? "00:00:00" : timeDifference;
                    %>
                    <label style="font-family:Open Sans;font-size: 13px;font-weight: 600;">Time Diffrence</label>
                    <input type=text class="form-control" style="border: 1px solid #b2b2b2;font-weight:bold" disabled value="<%=time%>">
                    <input type=hidden  value="" >
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label >Gateway*</label>
                    <input type=text name="gateway" id="gateway" maxlength="20" class="form-control" size="15" value="<%=pgtypeid%>" autocomplete="on">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label>Partner ID*</label>
                    <input type=text name="pid" id="pid" maxlength="100"  class="form-control"  value="<%=pid%>" autocomplete="on" <%=Config%>>
                    <input type="hidden" name="pid" value="<%=pid%>">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label>Account ID</label>
                    <input type=text name="accountid" maxlength="100" id="accountid" class="form-control"  value="<%=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid")%>" autocomplete="on">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label for="memberid">Member ID</label>
                    <input type=text name="memberid" maxlength="100"  class="form-control" id="memberid" value="<%=memberid%>" autocomplete="on">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label>Terminal ID</label>
                    <input type="text"  class="form-control" value="<%=terminalId%>" id="terminalid" name="terminalid" autocomplete="on">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label >&nbsp;</label>
                    <button type="submit"  name="button" value="search" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;Search</button>
                  </div>

                </div>
              </div>
            </div>
          </div>
        </div>
      </form>
      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong>Bank Transaction Inquiry Report</strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
          <%

            if(BankTransactionInquiry.equals("BankTransactionInquiry")){

          %>
          <table border="0" cellpadding="3" cellspacing="3" bordercolor="#2379A5" width="50%"  align="center"  class="table table-striped table-bordered table-hover table-green dataTable"  >
            <tr>
              <td  width="50%" class=" th0" colspan="6" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;" ><b>Success Transaction Count</b></td>
              <td style="text-align: center"><b><%= successCounter%></b></td>
            </tr>
            <tr>
              <td  width="50%" class="th0" colspan="6" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Fail Transaction Count</b></td>
              <td style="text-align: center"><b><%= failCounter%></b></td>
            </tr>
            <tr>
              <td  width="50%" class="th0" colspan="6" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>AuthStarted Transaction Count</b></td>
              <td style="text-align: center"><b><%= authStarted%></b></td>
            </tr>
          </table>
              <%}%>

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
  </div>
</body>
</html>
