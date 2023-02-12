<%@ page import="com.directi.pg.Functions,com.directi.pg.Logger,com.fraud.FraudSystemService,org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%@ page import="java.util.Date" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.Iterator" %>
<%@ include file="top.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%--
  Created by IntelliJ IDEA.
  User: SurajT.
  Date: 5/7/2018
  Time: 6:08 PM
  To change this template use File | Settings | File Templates.
--%>

<%!
  private static Logger logger=new Logger("fraudKycList.jsp");
%>

<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","Fraud Kyc List");
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
%>

<%
    Hashtable statushash = new Hashtable();
    statushash.put("Pending", "Pending");
    statushash.put("Processed", "Processed");
    statushash.put("Completed", "Completed");
    statushash.put("Failed", "Failed");


    Functions functions = new Functions();

    String partnerid = session.getAttribute("partnerId").toString();
    String status = Functions.checkStringNull(request.getParameter("fskycstatus"));

    String fdate=null;
    String tdate=null;

    try
    {
      fdate = ESAPI.validator().getValidInput("fromdate",request.getParameter("fromdate"),"fromDate",10,true);
      tdate = ESAPI.validator().getValidInput("todate",request.getParameter("todate"),"fromDate",10,true);
    }
    catch(ValidationException e)
    {
      logger.error("Date Format Exception",e);
    }

    Date date = new Date();
    SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

    String Date = originalFormat.format(date);
    date.setDate(1);
    String fromDate = originalFormat.format(date);

    fdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDate : request.getParameter("fromdate");
    tdate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");

%>

<html>
<head>
  <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

  <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
  <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>

  <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>

  <title><%=company%> Fraud KYC List</title>
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
      <form name="form" method="post" action="/partner/net/FraudKycList?ctoken=<%=ctoken%>">
        <div class="row">
          <div class="col-sm-12 portlets ui-sortable">
            <div class="widget">

              <div class="widget-header transparent">
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp; Fraud KYC List</strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>
              <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
              <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
              <%
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

                  <div class="form-group col-md-4 has-feedback">
                    <label>Status</label>
                    <select size="1" name="fskycstatus" class="form-control">
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

                  <div class="form-group col-md-4 has-feedback">
                    <label >Customer reg ID</label>
                    <input type="text" maxlength="10" value=""  name="custRegId" size="10" class="form-control">
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
                Hashtable hash = (Hashtable) request.getAttribute("kycdetails");
                Hashtable fraudRuleHash = (Hashtable) hash.get("fraudRule");
                Hashtable innerhash = null;
                if (hash != null && hash.size() > 0)
                {
                  String style = "class=tr0";

                  innerhash = (Hashtable) hash.get(1 + "");
                  int pos = 0;

                  value = (String) innerhash.get("id");
                  if (value == null)
                    value = "-";

              %>

              <table id="myTable" class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr>
                  <td  colspan="5" valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Fraud KYC Details</b></td>
                </tr>
                </thead>

                <tr>
                  <td colspan="3" valign="middle" align="left" >Customer Reg ID:</td>
                  <td colspan="2" valign="middle" align="left"> <%=ESAPI.encoder().encodeForHTML(value)%></td>
                </tr>
                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("customer_registration_id");
                  if (value == null)
                    value = "-";
                %>
                <tr>
                  <td colspan="3" valign="middle" align="left" >Fraud Registration ID:</td>
                  <td colspan="2" valign="middle" align="left" > <%=ESAPI.encoder().encodeForHTML(value)%></td>
                </tr>
                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("cust_request_id");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td colspan="3" valign="middle" align="left" >Customer Request Id :</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>
                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("firstName");
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
                  value = (String) innerhash.get("lastName");
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
                  value = (String) innerhash.get("emailId");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >Email Id:</td>
                  <td colspan="2" valign="middle" align="left" ><%=functions.getEmailMasking((value))%></td>
                </tr>
                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("countryCode");
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
                  value = (String) innerhash.get("phone");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >phone no:</td>
                  <td colspan="2" valign="middle" align="left" ><%=functions.getPhoneNumMasking((value))%></td>
                </tr>
                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("reg_date");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >Reg Date:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("customerIpAddress");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >customer Ip Address:</td>
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
                  <td  colspan="3" valign="middle" align="left" >website:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>


                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("reg_status");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td  colspan="3" valign="middle" align="left" >Registration status:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("description");
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
                  value = (String) innerhash.get("recommendation");
                  if (value == null)
                    value = "-";
                %>


                <tr>
                  <td  colspan="3" valign="middle" align="left" >Recommendation:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML((value))%></td>
                </tr>

                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("confidence_level");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td colspan="3" valign="middle" align="left" >Confidence Level:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML(value)%></td>
                </tr>


                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("ACTION");
                  if (value == null)
                    value = "-";
                %>

                <tr>
                  <td colspan="3" valign="middle" align="left" >ACTION:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML(value)%></td>
                </tr>


                <%
                  pos++;
                  style = "class=\"tr" + pos % 2 + "\"";
                  value = (String) innerhash.get("fsid");

                  //System.out.println("fsid : "+value);
                  if (value == null)
                    value = "-";
                  //String fsGateway =FraudSystemService.getFSGateway(value);
                %>
                <tr>
                  <td colspan="3" valign="middle" align="left" >Fraud System Name:</td>
                  <td colspan="2" valign="middle" align="left" ><%=ESAPI.encoder().encodeForHTML(FraudSystemService.getFSGateway(value))%></td>
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
               <%-- <%
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
--%>

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

