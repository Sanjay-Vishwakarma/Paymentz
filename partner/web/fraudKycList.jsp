<%@ page import="com.directi.pg.Functions,com.directi.pg.Logger,com.fraud.FraudSystemService" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.LoadProperties" %>
<%@ include file="top.jsp"%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  Created by IntelliJ IDEA.
  User: Surajt
  Date: 5/7/2018
  Time: 1:23 PM
  To change this template use File | Settings | File Templates.
--%>

<%!
  private static Logger logger=new Logger("fraudKycList.jsp");
%>

<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","Fraud Kyc List");
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

  Hashtable statushash = new Hashtable();
  statushash.put("Pending", "Pending");
  statushash.put("Processed", "Processed");
  statushash.put("Completed", "Completed");
  statushash.put("Failed", "Failed");

  if (partner.isLoggedInPartner(session))
  {
    Functions functions = new Functions();
    String partnerid = session.getAttribute("partnerId").toString();
    String status = Functions.checkStringNull(request.getParameter("fskycstatus"));
    String custRegId = Functions.checkStringNull(request.getParameter("custRegId"))==null?"":request.getParameter("custRegId");

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

    if (fdate != null) str = str + "fromdate=" + fdate;
    if (tdate != null) str = str + "&todate=" + tdate;

    boolean archive = Boolean.valueOf(request.getParameter("archive")).booleanValue();
    str = str + "&archive=" + archive;

    if (functions.isValueNull(request.getParameter("fskycstatus")))
    {
      String fskycstatus= request.getParameter("fskycstatus");
      str = str +"&fskycstatus="+fskycstatus;
    }
    int pageno = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
    int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);

    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);

    String fraudKycList_Fraud_KYC_List = rb1.getString("fraudKycList_Fraud_KYC_List");
    String fraudKycList_From = rb1.getString("fraudKycList_From");
    String fraudKycList_To = rb1.getString("fraudKycList_To");
    String fraudKycList_Status = rb1.getString("fraudKycList_Status");
    String fraudKycList_All = rb1.getString("fraudKycList_All");
    String fraudKycList_Customer_reg_ID = rb1.getString("fraudKycList_Customer_reg_ID");
    String fraudKycList_Path = rb1.getString("fraudKycList_Path");
    String fraudKycList_Search = rb1.getString("fraudKycList_Search");
    String fraudKycList_Report_Table = rb1.getString("fraudKycList_Report_Table");
    String fraudKycList_Note = rb1.getString("fraudKycList_Note");
    String fraudKycList_Date = rb1.getString("fraudKycList_Date");
    String fraudKycList_Customer_regID = rb1.getString("fraudKycList_Customer_regID");
    String fraudKycList_Fraud_System = rb1.getString("fraudKycList_Fraud_System");
    String fraudKycList_Fraud_Reg_Status = rb1.getString("fraudKycList_Fraud_Reg_Status");
    String fraudKycList_Cust_Request_ID = rb1.getString("fraudKycList_Cust_Request_ID");
    String fraudKycList_First_Name = rb1.getString("fraudKycList_First_Name");
    String fraudKycList_LastName = rb1.getString("fraudKycList_LastName");
    String fraudKycList_Email = rb1.getString("fraudKycList_Email");
    String fraudKycList_Score = rb1.getString("fraudKycList_Score");
    String fraudKycList_Action = rb1.getString("fraudKycList_Action");
    String fraudKycList_Showing_Page = rb1.getString("fraudKycList_Showing_Page");
    String fraudKycList_of = rb1.getString("fraudKycList_of");
    String fraudKycList_records = rb1.getString("fraudKycList_records");
    String fraudKycList_Sorry = rb1.getString("fraudKycList_Sorry");
    String fraudKycList_No = rb1.getString("fraudKycList_No");
    String fraudKycList_page_no = rb1.getString("fraudKycList_page_no");
    String fraudKycList_total_no_of_records = rb1.getString("fraudKycList_total_no_of_records");

%>

<html>
<head>
  <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
<%--  <script src="/partner/NewCss/js/jquery-1.12.4.min.js"></script>--%>
  <%--<script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

  <link rel="stylesheet" href=" /merchant/transactionCSS/css/jquery.dataTables.min.css">
  <script type="text/javascript" src="/merchant/transactionCSS/js/jquery.dataTables.min.js"></script>

  <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script src="/partner/NewCss/libs/jquery-icheck/icheck.min.js"></script>

  <title><%=company%> Fraud Management> Fraud KYC List</title>
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
                <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=fraudKycList_Fraud_KYC_List%></strong></h2>
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
                    <label ><%=fraudKycList_From%></label>
                    <input type="text" size="16" name="fromdate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(fdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label><%=fraudKycList_To%></label>
                    <input type="text" size="16" name="todate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(tdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label><%=fraudKycList_Status%></label>
                    <select size="1" name="fskycstatus" class="form-control">
                      <option value=""><%=fraudKycList_All%></option>
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
                    <label ><%=fraudKycList_Customer_reg_ID%></label>
                    <input type="text" maxlength="10" value="<%=custRegId%>"  name="custRegId" size="10" class="form-control">
                  </div>

                  <div class="form-group col-md-4">
                    <label style="color: transparent;"><%=fraudKycList_Path%></label>
                    <button type="submit" class="btn btn-default" name="B1" style="display:block;">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;<%=fraudKycList_Search%>
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
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=fraudKycList_Report_Table%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-x: auto;">
              <%  String errormsg1 = (String)request.getAttribute("message");
                if (errormsg1 == null) {
                  errormsg1 = "";
                }
                else{
                  out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" >");
                  out.println(errormsg1);
                  out.println("</font></td></tr></table>");
                }

                Hashtable hash = (Hashtable) request.getAttribute("kycdetails");
                Hashtable temphash = null;

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

                  str = str + "&SRecords=" + pagerecords;
                  str = str + "&ctoken=" + ctoken;
                  String style = "class=td0";
                  String ext = "light";

                  if (records > 0)
                  {
              %>

              <div id="showingid"><strong><%=fraudKycList_Note%></strong></div>
              <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                <thead>
                <tr>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudKycList_Date%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudKycList_Customer_regID%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudKycList_Fraud_System%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudKycList_Fraud_Reg_Status%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudKycList_Cust_Request_ID%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudKycList_First_Name%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudKycList_LastName%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudKycList_Email%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudKycList_Score%></b></td>
                  <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudKycList_Action%></b></td>
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

                    String fraudCustRegId = (String) temphash.get("id");
                    String fsGateway =FraudSystemService.getFSGateway((String)temphash.get("fsid"));
                    String fraudKycStatus = (String) temphash.get("reg_status");
                    String custRequestId = (String) temphash.get("cust_request_id");
                    String firstName = (String) temphash.get("firstName");
                    String lastName = (String) temphash.get("lastName");
                    String emailId = (String) temphash.get("emailId");

                    if (!functions.isValueNull(emailId))
                      emailId="-";

                    String score = (String) temphash.get("score");
                    if (!functions.isValueNull(score))
                      score="-";

                    String action = (String) temphash.get("ACTION");

                    out.println("<tr " + style + ">");
                    out.println("<td align=\"center\">" + ESAPI.encoder().encodeForHTML(date1) + "</td>");
                    out.println("<td align=\"center\"><form action=\"FraudKycList?ctoken="+ctoken+"\" method=\"post\"><input type=\"hidden\" name=\"fsCustRegId\" value=\""+fraudCustRegId+"\"><input type=\"hidden\" name=\"fsid\" value=\""+temphash.get("fsid")+"\"><input type=\"hidden\" name=\"ctoken\" value=\""+ctoken+"\")+\"\"><input type=\"submit\" class=\"goto\" name=\"submit\" value=\""+fraudCustRegId+"\"></form></td>");
                    out.println("<td valign=\"middle\" data-label=\"Fraud System\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(fsGateway) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Fraud Kyc Reg Status\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(fraudKycStatus) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Cust Request ID\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(custRequestId) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"First Name\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(firstName) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Last Name\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(lastName) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Email Id\" align=\"center\"" + style + ">" + functions.getEmailMasking(emailId) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Score\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(score) + "</td>");
                    out.println("<td valign=\"middle\" data-label=\"Action\" align=\"center\"" + style + ">" + ESAPI.encoder().encodeForHTML(action) + "</td>");;
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
            <div id="showingid"><strong><%=fraudKycList_page_no%> <%=pageno%>  <%=fraudKycList_of%>  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
            <div id="showingid"><strong><%=fraudKycList_total_no_of_records%>   <%=totalrecords%> </strong></div>
            <jsp:include page="page.jsp" flush="true">
              <jsp:param name="numrecords" value="<%=totalrecords%>"/>
              <jsp:param name="numrows" value="<%=pagerecords%>"/>
              <jsp:param name="pageno" value="<%=pageno%>"/>
              <jsp:param name="str" value="<%=str%>"/>
              <jsp:param name="page" value="FraudKycList"/>
              <jsp:param name="currentblock" value="<%=currentblock%>"/>
              <jsp:param name="orderby" value=""/>
            </jsp:include>
            <%

                }
                else
                {
                  out.println(Functions.NewShowConfirmation1(fraudKycList_Sorry, fraudKycList_No));
                }
              }
              else
              {
                out.println(Functions.NewShowConfirmation1(fraudKycList_Sorry, fraudKycList_No));
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

