<%@ include file="functions.jsp" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="top.jsp" %>

<%!
  private static Logger log = new Logger("partnerpreference");
%>
<%
  session.setAttribute("submit", "partnerEmailSetting");
  String company= (String)session.getAttribute("partnername");
%>
<% ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  String partnerid = request.getParameter("partnerid") == null ? "" : request.getParameter("partnerid");
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = com.directi.pg.LoadProperties.getProperty(language_property1);
  String partnerEmailSetting_Partner_Email = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Partner_Email")) ? rb1.getString("partnerEmailSetting_Partner_Email") : "Partner Email Notification";
  String partnerEmailSetting_Partner_ID = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Partner_ID")) ? rb1.getString("partnerEmailSetting_Partner_ID") : "Partner ID";
  String partnerEmailSetting_Search = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Search")) ? rb1.getString("partnerEmailSetting_Search") : "Search";
  String partnerEmailSetting_Sales_Contact = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Sales_Contact")) ? rb1.getString("partnerEmailSetting_Sales_Contact") : "Sales Contact";
  String partnerEmailSetting_Billing_Descriptor = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Billing_Descriptor")) ? rb1.getString("partnerEmailSetting_Billing_Descriptor") : "Billing Descriptor Update";
  String partnerEmailSetting_Merchant_Sales_Transaction = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Merchant_Sales_Transaction")) ? rb1.getString("partnerEmailSetting_Merchant_Sales_Transaction") : "Merchant Sales Transaction";
  String partnerEmailSetting_Admin_Failed_Transactions = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Admin_Failed_Transactions")) ? rb1.getString("partnerEmailSetting_Admin_Failed_Transactions") : "Auth Failed Transactions";
  String partnerEmailSetting_Partner_Card_Registration = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Partner_Card_Registration")) ? rb1.getString("partnerEmailSetting_Partner_Card_Registration") : "Partner Card Registration";
  String partnerEmailSetting_Merchant_Card_Registration = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Merchant_Card_Registration")) ? rb1.getString("partnerEmailSetting_Merchant_Card_Registration") : "Merchant Card Registration";
  String partnerEmailSetting_Payout_Transaction = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Payout_Transaction")) ? rb1.getString("partnerEmailSetting_Payout_Transaction") : "Payout Transaction";
  String partnerEmailSetting_Failed_Transaction = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Failed_Transaction")) ? rb1.getString("partnerEmailSetting_Failed_Transaction") : "Failed Transaction";
  String partnerEmailSetting_Reject_Transaction = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Reject_Transaction")) ? rb1.getString("partnerEmailSetting_Reject_Transaction") : "Reject Transaction";
  String partnerEmailSetting_Fraud_Contact = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Fraud_Contact")) ? rb1.getString("partnerEmailSetting_Fraud_Contact") : "Fraud Contact";
  String partnerEmailSetting_Fraud_Failed_Transaction = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Fraud_Failed_Transaction")) ? rb1.getString("partnerEmailSetting_Fraud_Failed_Transaction") : "Fraud Failed Transaction";
  String partnerEmailSetting_Chargeback_Contact = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Chargeback_Contact")) ? rb1.getString("partnerEmailSetting_Chargeback_Contact") : "Chargeback Contact";
  String partnerEmailSetting_Chargeback_Transaction = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Chargeback_Transaction")) ? rb1.getString("partnerEmailSetting_Chargeback_Transaction") : "Chargeback Transaction";
  String partnerEmailSetting_Refund_Contact = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Refund_Contact")) ? rb1.getString("partnerEmailSetting_Refund_Contact") : "Refund Contact";
  String partnerEmailSetting_Refund_Transaction = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Refund_Transaction")) ? rb1.getString("partnerEmailSetting_Refund_Transaction") : "Refund Transaction";
  String partnerEmailSetting_Save = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Save")) ? rb1.getString("partnerEmailSetting_Save") : "Save";
  String partnerEmailSetting_Sorry = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_Sorry")) ? rb1.getString("partnerEmailSetting_Sorry") : "Sorry";
  String partnerEmailSetting_no = StringUtils.isNotEmpty(rb1.getString("partnerEmailSetting_no")) ? rb1.getString("partnerEmailSetting_no") : "No Records Found.";

%>
<html>
<head>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <script>


    function changeModuleId(moduleid,list){

      for(var i=0;i<list.length;i++)
      {
        if (document.getElementById("moduleid_" + moduleid).checked)
        {
          document.getElementById("submoduleid_"+list[i]).disabled = false;
        }
        else
          document.getElementById("submoduleid_"+list[i]).disabled = true;
      }

    }
  </script>
</head>
<title><%=company%> Partner> Partner Email Settings </title>

<body class="bodybackground">
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=partnerEmailSetting_Partner_Email%>
              </strong></h2>

              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <div id="horizontal-form">
                <form action="/partner/net/PartnerEmailSetting?ctoken=<%=ctoken%>" method="post"
                      name="forms">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                  <input type="hidden" value="<%=String.valueOf(session.getAttribute("partnerId"))%>"
                         name="superAdminId" id="partnerid">
                  <%
                    Functions functions = new Functions();
                    if (functions.isValueNull((String)request.getAttribute("error")))
                    {
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + (String)request.getAttribute("error") + "</h5>");
                    }
                    String success = (String) request.getAttribute("cbmessage");
                    System.out.println("success" + success);
                    if (functions.isValueNull(success))
                    {
                      out.println("<h5 class=\"bg-info\" style=\"text-align: center;\"><i class=\"fa fa-check-circle\" style=\" font-size:initial !important;\" ></i>&nbsp;&nbsp;" + success + "</h5>");
                    }
                    Hashtable temphash = null;
                  %>

                  <div class="form-group col-md-4">
                    <label class="col-sm-3 control-label"><%=partnerEmailSetting_Partner_ID%></label>

                    <div class="col-sm-8">
                      <input name="partnerid" id="PID" value="<%=partnerid%>" class="form-control"
                             autocomplete="on">
                    </div>
                  </div>

                  <div class="form-group col-md-8">
                    <div class="col-sm-offset-2 col-sm-3">
                      <button type="submit" class="btn btn-default"><i class="fa fa-clock-o"></i>
                        &nbsp;&nbsp;<%=partnerEmailSetting_Search%>
                      </button>
                    </div>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>

      <form action="/partner/net/UpdateEmailSetting?ctoken=<%=ctoken%>" method=post>
        <input type="hidden" name="partnerid" value="<%=partnerid%>">
        <div class="row reporttable">
          <div class="col-md-12">
            <div class="widget">
              <div class="widget-header">
                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerEmailSetting_Sales_Contact%></strong></h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>
              <%
                Hashtable hash = (Hashtable) request.getAttribute("memberdetails");
                int pageno = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
                int pagerecords = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
                int records = 0;
                int totalrecords = 0;
                if ((hash != null && hash.size() > 0))
                {
                  try
                  {
                    records = Integer.parseInt((String) hash.get("records"));
                    totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
                  }
                  catch (Exception ex)
                  {
                    log.error("Records & TotalRecords is found null", ex);
                  }
                }
                if (records > 0)
                {
              %>
              <center>
                <div class="widget-content padding" style="overflow-x: auto;">
                  <table align=center width="50%"
                         class="display table table table-striped table-bordered table-hover dataTable"
                         style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                    <%
                      String style = "td1";
                      String isReadOnly="";
                      for (int pos = 1; pos <= records; pos++)
                      {
                        String id = Integer.toString(pos);
                        int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);
                        if (pos % 2 == 0)
                          style = "tr0";
                        else
                          style = "tr0";
                        temphash = (Hashtable) hash.get(id);
                    %>
                    <tr>
                      <td valign="middle" align="center"
                          style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                        <%=partnerEmailSetting_Billing_Descriptor%>
                      </td>
                      <td valign="middle" align="center"
                          style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                        <%=partnerEmailSetting_Merchant_Sales_Transaction%>
                      </td>
                      <td valign="middle" align="center"
                          style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                        <%=partnerEmailSetting_Admin_Failed_Transactions%>
                      </td>
                      <td valign="middle" align="center"
                          style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                        <%=partnerEmailSetting_Partner_Card_Registration%>
                      </td>
                      <td valign="middle" align="center"
                          style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                        <%=partnerEmailSetting_Merchant_Card_Registration%>
                      </td>
                      <td valign="middle" align="center"
                          style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                        <%=partnerEmailSetting_Payout_Transaction%>
                      </td>
                      <td valign="middle" align="center"
                          style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                        <%=partnerEmailSetting_Failed_Transaction%>
                      </td>
                      <td valign="middle" align="center"
                          style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                        <%=partnerEmailSetting_Reject_Transaction%>
                      </td>
                    </tr>
                    <tr>
                      <td valign="middle" data-label="Sr No" align="center"

                          class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                     name='billingDescriptor' <%=isReadOnly%>>
                        <%
                          out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("salesBillingDescriptor")))); %></select>
                      </td>
                      <td valign="middle" data-label="Sr No" align="center"

                          class="<%=style%>">
                        <select class="form-control" style="background: #ffffff" name='salesTransaction' <%=isReadOnly%>>
                          <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchantSalesTransaction")))); %>
                        </select>
                      </td>
                      <td valign="middle" data-label="Sr No" align="center"

                          class="<%=style%>">
                        <select class="form-control" style="background: #ffffff" name='adminFailedTransaction' <%=isReadOnly%>>
                          <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("salesAdminFailedTransaction")))); %></select>
                      </td>
                      <td valign="middle" data-label="Sr No" align="center"

                          class="<%=style%>">
                        <select class="form-control" style="background: #ffffff" name='partnerCardRegistration' <%=isReadOnly%>>
                          <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("salesPartnerCardRegistration")))); %></select>
                      </td>
                      <td valign="middle" data-label="Sr No" align="center"

                          class="<%=style%>">
                        <select class="form-control" style="background: #ffffff" name='merchantCardRegistration' <%=isReadOnly%>>
                          <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("salesMerchantCardRegistration")))); %></select>
                      </td>
                      <td valign="middle" data-label="Sr No" align="center"

                          class="<%=style%>">
                        <select class="form-control" style="background: #ffffff" name='payoutTransaction' <%=isReadOnly%>>
                          <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("salesPayoutTransaction")))); %></select>
                      </td>
                      <td valign="middle" data-label="Sr No" align="center"

                          class="<%=style%>">
                        <select class="form-control" style="background: #ffffff" name='failedTransaction' <%=isReadOnly%>>
                          <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("failedTransaction")))); %></select>
                      </td>
                      <td valign="middle" data-label="Sr No" align="center"

                          class="<%=style%>">
                        <select class="form-control" style="background: #ffffff" name='rejectTransaction' <%=isReadOnly%>>
                          <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("rejectTransaction")))); %></select>
                      </td>
                    </tr>
                  </table>
                </div>
              </center>
            </div>
          </div>
        </div>
        <div class="row reporttable">
          <div class="col-md-12">
            <div class="widget">
              <div class="widget-header">
                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerEmailSetting_Fraud_Contact%></strong>
                </h2>

                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>
              <center>
                <div class="widget-content padding" style="overflow-x: auto;">
                  <table align=center width="50%"
                         class="display table table table-striped table-bordered table-hover dataTable"
                         style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                    <tr>
                      <%--<td valign="middle" align="center"
                          style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                        Exception Details
                      </td>--%>
                      <td valign="middle" align="center"
                          style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                        <%=partnerEmailSetting_Fraud_Failed_Transaction%>
                      </td>
                    </tr>
                    <tr>
                      <%--<td valign="middle" data-label="Sr No" align="center"

                          class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                     name='exceptionDetails' <%=isReadOnly%>>
                        <%
                          out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("exceptionDetails")))); %></select>
                      </td>--%>
                      <td valign="middle" data-label="Sr No" align="center"

                          class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                     name='fraudFailedTransaction' <%=isReadOnly%>>
                        <%
                          out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("fraudFailedTransaction")))); %></select>
                      </td>
                    </tr>
                  </table>
                </div>
              </center>
            </div>
          </div>
        </div>
        <div class="row reporttable">
          <div class="col-md-12">
            <div class="widget">
              <div class="widget-header">
                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerEmailSetting_Chargeback_Contact%></strong>
                </h2>

                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>
              <center>
                <div class="widget-content padding" style="overflow-x: auto;">
                  <table align=center width="50%"
                         class="display table table table-striped table-bordered table-hover dataTable"
                         style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                    <tr>
                      <td valign="middle" align="center"
                          style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                        <%=partnerEmailSetting_Chargeback_Transaction%>
                      </td>
                    </tr>
                    <tr>
                      <td valign="middle" data-label="Sr No" align="center"
                          class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                     name='chargebackTransaction' <%=isReadOnly%>>
                        <%
                          out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("chargebackTransaction")))); %></select>
                      </td>
                    </tr>
                  </table>
                </div>
              </center>
            </div>
          </div>
        </div>

        <div class="row reporttable">
          <div class="col-md-12">
            <div class="widget">
              <div class="widget-header">
                <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerEmailSetting_Refund_Contact%></strong>
                </h2>
                <div class="additional-btn">
                  <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                  <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                  <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                </div>
              </div>
              <center>
                <div class="widget-content padding" style="overflow-x: auto;">
                  <table align=center width="50%"
                         class="display table table table-striped table-bordered table-hover dataTable"
                         style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                    <tr>
                      <td valign="middle" align="center"
                          style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;">
                        <%=partnerEmailSetting_Refund_Transaction%>
                      </td>
                    </tr>
                    <tr>
                      <td valign="middle" data-label="Sr No" align="center"
                          class="<%=style%>"><select class="form-control" style="background: #ffffff"
                                                     name='refundTransaction' <%=isReadOnly%>>
                        <%
                          out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("refundTransaction")))); %></select>
                      </td>
                    </tr>
                  </table>
                </div>
              </center>
            </div>
          </div>
        </div>

        <%
          }
        %>
        <table align="center" id="smalltable" border="0" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 50%;">
          <tr style="background-color: #ffffff!important;">
            <td align="center" colspan="2">
              <button type="submit" value="Save" class="btn btn-default">
                <%=partnerEmailSetting_Save%>
              </button>
            </td>
          </tr>
        </table>
      </form>
      <br>
      <%
        }
        else
        {
          out.println(Functions.NewShowConfirmation1(partnerEmailSetting_Sorry,partnerEmailSetting_no ));
        }
      %>
    </div>
  </div>
</div>
</div>
</body>
</html>