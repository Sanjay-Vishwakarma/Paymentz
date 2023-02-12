<%@ page import="com.directi.pg.Functions,org.owasp.esapi.ESAPI,org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%@ page import="com.fraud.FraudSystemService" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.LoadProperties" %>
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
  private static Logger logger=new Logger("fraudTransactionList.jsp");
%>

<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","Fraud Transactions List");
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
%>

<%
  Hashtable statushash = new Hashtable();
  statushash.put("Pending", "Pending");
  statushash.put("Process Failed", "Process Failed");
  statushash.put("Process Successfully", "Process Successfully");

  if (partner.isLoggedInPartner(session))
  {
    //LinkedHashMap memberidDetails=partner.getPartnerMembersDetail((String) session.getAttribute("merchantid"));
    String memberid=nullToStr(request.getParameter("memberid"));
    String pid=nullToStr(request.getParameter("pid"));
    String partnerid = session.getAttribute("partnerId").toString();
    String Config =null;
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    if(Roles.contains("superpartner")){

    }else{
      pid = String.valueOf(session.getAttribute("merchantid"));
      Config = "disabled";
    }

   // Hashtable gatewayHash = FraudSystemService.getFraudSystem();

    String fsid =Functions.checkStringNull(request.getParameter("fsid"));
    if (fsid == null)
    {fsid = "";}
   /* String membertrandid = Functions.checkStringNull(request.getParameter("memberid"));
    if (membertrandid == null)
      membertrandid = "";*/

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
      logger.error("Date Format Exception",e);
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
    if (memberid != null) str = str + "&memberid=" + memberid;
    if (pid != null) str = str + "&pid=" + pid;
    if (status != null) str = str + "&fstransstatus=" + status;

    if (fsid !=null) str = str + "&fsid=" + fsid;
    boolean archive = Boolean.valueOf(request.getParameter("archive")).booleanValue();
    str = str + "&archive=" + archive;

    int pageno = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
    int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);

    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);

    String fraudTransaction_Fraud_Transactions_List = rb1.getString("fraudTransaction_Fraud_Transactions_List");
    String fraudTransaction_From = rb1.getString("fraudTransaction_From");
    String fraudTransaction_To = rb1.getString("fraudTransaction_To");
    String fraudTransaction_PartnerID = rb1.getString("fraudTransaction_PartnerID");
    String fraudTransaction_MerchantID = rb1.getString("fraudTransaction_MerchantID");
    String fraudTransaction_Status = rb1.getString("fraudTransaction_Status");
    String fraudTransaction_All = rb1.getString("fraudTransaction_All");
    String fraudTransaction_TrackingID = rb1.getString("fraudTransaction_TrackingID");
    String fraudTransaction_Path = rb1.getString("fraudTransaction_Path");
    String fraudTransaction_Search = rb1.getString("fraudTransaction_Search");
    String fraudTransaction_Report_Table = rb1.getString("fraudTransaction_Report_Table");
    String fraudTransaction_Note = rb1.getString("fraudTransaction_Note");
    String fraudTransaction_Date = rb1.getString("fraudTransaction_Date");
    String fraudTransaction_FraudID = rb1.getString("fraudTransaction_FraudID");
    String fraudTransaction_Fraud_System = rb1.getString("fraudTransaction_Fraud_System");
    String fraudTransaction_Fraud_Status = rb1.getString("fraudTransaction_Fraud_Status");
    String fraudTransaction_Tracking_ID = rb1.getString("fraudTransaction_Tracking_ID");
    String fraudTransaction_Merchant_ID = rb1.getString("fraudTransaction_Merchant_ID");
    String fraudTransaction_Account_ID = rb1.getString("fraudTransaction_Account_ID");
    String fraudTransaction_Fraud_Alert_Score = rb1.getString("fraudTransaction_Fraud_Alert_Score");
    String fraudTransaction_Auto_Reversal = rb1.getString("fraudTransaction_Auto_Reversal");
    String fraudTransaction_Score = rb1.getString("fraudTransaction_Score");
    String fraudTransaction_Amount = rb1.getString("fraudTransaction_Amount");
    String fraudTransaction_Is_Alert_Sent = rb1.getString("fraudTransaction_Is_Alert_Sent");
    String fraudTransaction_IsReversed = rb1.getString("fraudTransaction_IsReversed");
    String fraudTransaction_Is_Fraud_Reverse = rb1.getString("fraudTransaction_Is_Fraud_Reverse");
    String fraudTransaction_Showing_Page = rb1.getString("fraudTransaction_Showing_Page");
    String fraudTransaction_of = rb1.getString("fraudTransaction_of");
    String fraudTransaction_records = rb1.getString("fraudTransaction_records");
    String fraudTransaction_Sorry = rb1.getString("fraudTransaction_Sorry");
    String fraudTransaction_No_records = rb1.getString("fraudTransaction_No_records");
    String fraudTransaction_page_no = rb1.getString("fraudTransaction_page_no");
    String fraudTransaction_total_no_of_records = rb1.getString("fraudTransaction_total_no_of_records");
%>

<html>
<head>
  <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>--%>
  <%--<script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

  <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
  <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>

  <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>

  <title><%=company%> Fraud Management> Fraud Transactions List</title>
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
    <style type="text/css">
        #ui-id-2
        {
            overflow: auto;
            max-height: 350px;
        }
    </style>
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
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp; <%=fraudTransaction_Fraud_Transactions_List%></strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>
              <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
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
                    <label ><%=fraudTransaction_From%></label>
                    <input type="text" size="16" name="fromdate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label><%=fraudTransaction_To%></label>
                    <input type="text" size="16" name="todate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                  </div>
                  <%--<div class="form-group col-md-4 has-feedback">
                    <label>Fraud ID</label>
                    <input type=text name="fraudtransid" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudtransid)%>" class="form-control" size="20">

                  </div>--%>
                  <div class="form-group col-md-4 has-feedback">
                    <label ><%=fraudTransaction_PartnerID%> </label>
                    <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                    <input name="pid" type="hidden" value="<%=pid%>" >
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label ><%=fraudTransaction_MerchantID%></label>
                    <input name="memberid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label><%=fraudTransaction_Status%></label>
                    <select size="1" name="fstransstatus" class="form-control">
                      <option value=""><%=fraudTransaction_All%></option>
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

                 <%-- <div class="form-group col-md-4 has-feedback">
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
                    <label ><%=fraudTransaction_TrackingID%></label>
                    <input type="text" maxlength="10" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>"  name="STrackingid" size="10" class="form-control">
                  </div>

                  <div class="form-group col-md-4">
                    <label style="color: transparent;"><%=fraudTransaction_Path%></label>
                    <button type="submit" class="btn btn-default" name="B1" style="display:block;">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;<%=fraudTransaction_Search%>
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
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=fraudTransaction_Report_Table%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-x: auto;">
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
              %>
              <%

                Hashtable hash = (Hashtable) request.getAttribute("transactionsdetails");
                Hashtable temphash = null;
               /* if(error !=null)
                {
                  out.println(error);

                }*/
                if (hash != null && hash.size() > 0)
                {
                  int records = 0;
                  int totalrecords = 0;
                  int currentblock = 1;

                  try
                  {
                    records=Functions.convertStringtoInt((String)hash.get("records"),15);
                    totalrecords=Functions.convertStringtoInt((String)hash.get("totalrecords"),0);
                    currentblock = Functions.convertStringtoInt((request.getParameter("currentblock")),1);
                  }
                  catch (NumberFormatException ex)
                  {
                    logger.error("Records & TotalRecords is found null",ex);
                  }

                  str = str + "SRecords=" + pagerecords;
                  str = str + "&ctoken=" + ctoken;
                  String style = "class=td0";
                  String ext = "light";

                  if (records > 0)
                  {
              %>

              <div id="showingid"><strong><%=fraudTransaction_Note%></strong></div>
              <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudTransaction_Date%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudTransaction_FraudID%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudTransaction_Fraud_System%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudTransaction_Fraud_Status%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudTransaction_Tracking_ID%> </b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudTransaction_Merchant_ID%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudTransaction_Account_ID%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudTransaction_Fraud_Alert_Score%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudTransaction_Auto_Reversal%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudTransaction_Score%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudTransaction_Amount%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudTransaction_Is_Alert_Sent%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudTransaction_IsReversed%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudTransaction_Is_Fraud_Reverse%></b></td>
                </tr>
                </thead>
                <input type="hidden" value="<%=ctoken%>" name="ctoken">

                <%

                  for (int pos = 1; pos <= records; pos++)
                  {
                    String id = Integer.toString(pos);

                    int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

                    style = "class=\"tr" + pos % 2 + "\"";

                    temphash = (Hashtable) hash.get(id);

                    String date1 = (String)temphash.get("dtstamp");

                    String fraudtransid1 = (String) temphash.get("fraudtransid");
                    String fsGateway =FraudSystemService.getFSGateway((String)temphash.get("fsid"));
                    String fraudtransstatus = (String) temphash.get("fraudtransstatus");
                    String icicitransid = (String) temphash.get("trackingid")==null?"-":(String) temphash.get("trackingid");

                    String memberId = (String) temphash.get("memberid");
                    String accountid = (String) temphash.get("accountid")==null?"-":(String) temphash.get("trackingid");
                    String maxScore = (String) temphash.get("maxScore");
                    if(!functions.isValueNull(maxScore)) {maxScore="";}
                    String autoReversalScore = (String) temphash.get("autoReversalScore");
                    if(!functions.isValueNull(autoReversalScore)) {autoReversalScore="";}
                    String score = (String) temphash.get("score");
                    String amount = (String) temphash.get("amount");
                    String isAlertsent = (String) temphash.get("isAlertSent");
                    String isReversalAction = (String) temphash.get("isRefund")==null?"-":(String) temphash.get("isRefund");
                    String isFraud = (String) temphash.get("isFraud")==null?"-":(String) temphash.get("isFraud");

                    out.println("<tr " + style + ">");
                    out.println("<td >" + ESAPI.encoder().encodeForHTML(date1) + "</td>");
                    out.println("<td align=\"center\"><form action=\"FraudTransactionList?ctoken="+ctoken+"\" method=\"post\"><input type=\"hidden\" name=\"fstransid\" value=\""+fraudtransid1+"\"><input type=\"hidden\" name=\"score\" value=\""+score+"\"><input type=\"hidden\" name=\"fsid\" value=\""+temphash.get("fsid")+"\"><input type=\"hidden\" name=\"ctoken\" value=\""+ctoken+"\")+\"\"><input type=\"submit\" class=\"goto\" name=\"submit\" value=\""+fraudtransid1+"\"></form></td>");
                    //out.println("<td align=\"center\"><form action=\"FraudTransactionList?ctoken="+ctoken+"\" method=\"post\"><input type=\"hidden\" name=\"fstransid\" value=\""+fraudtransid1+"\"><input type=\"hidden\" name=\"fsid\" value=\""+temphash.get("fsid")+"\"><input type=\"hidden\" name=\"ctoken\" value=\""+ctoken+"\")+\"\"><input type=\"submit\" class=\"goto\" name=\"submit\" value=\""+fraudtransid1+"\"></form></td>");
                    /*out.println("<td align=\"Center\"><a href=\"FraudTransactionList?fstransid=" + ESAPI.encoder().encodeForHTMLAttribute(fstransid)+ "&fsid=" +  ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("fsid")) +"&ctoken="+ctoken+"\">" +fstransid+ "</a></td>");*/
                    out.println("<td valign=\"middle\" data-label=\"Fraud System\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(fsGateway) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Fraud Transaction Status\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(fraudtransstatus) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Fraud Transaction ID\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(icicitransid) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"MerchantID\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(memberId) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"AccountID\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(accountid) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Fraud Score\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(maxScore) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Reversal Score\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(autoReversalScore) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Score\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(score) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Aount\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(amount) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Is Alert Sent\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(isAlertsent) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Reversal Action\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(isReversalAction) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Is Fraud\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(isFraud) + "</td>");
                    out.println("</tr>");
                  }

                %>
              </table>
            </div>
                           <%
                                int TotalPageNo;
                                if(totalrecords%pagerecords!=0)
                                {
                                    TotalPageNo=totalrecords/pagerecords+1;
                                }
                                else
                                {
                                    TotalPageNo=totalrecords/pagerecords;
                                }
                            %>
                            <div id="showingid"><strong><%=fraudTransaction_page_no%> <%=pageno%>  <%=fraudTransaction_of%>  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
                            <div id="showingid"><strong><%=fraudTransaction_total_no_of_records%>   <%=totalrecords%> </strong></div>

            <jsp:include page="page.jsp" flush="true">
              <jsp:param name="numrecords" value="<%=totalrecords%>"/>
              <jsp:param name="numrows" value="<%=pagerecords%>"/>
              <jsp:param name="pageno" value="<%=pageno%>"/>
              <jsp:param name="str" value="<%=str%>"/>
              <jsp:param name="page" value="FraudTransactionList"/>
              <jsp:param name="currentblock" value="<%=currentblock%>"/>
              <jsp:param name="orderby" value=""/>
            </jsp:include>
            <%

                }
                else
                {
                  out.println(Functions.NewShowConfirmation1(fraudTransaction_Sorry, fraudTransaction_No_records));
                }
              }
              else
              {
                out.println(Functions.NewShowConfirmation1(fraudTransaction_Sorry, fraudTransaction_No_records));
              }

            %>
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
<%!
  public static String nullToStr(String str)
  {
    if(str == null)
      return "";
    return str;
  }
%>
