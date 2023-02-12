<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="com.manager.vo.RecurringBillingVO" %>
<%@ page import="java.util.List" %>
<%@ page import="com.directi.pg.Functions" %>
<%@include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Diksha
  Date: 20-Jan-21
  Time: 7:46 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <script type="text/javascript" language="JavaScript" src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  String partnerid = session.getAttribute("partnerId").toString();
  String pid =  Functions.checkStringNull(request.getParameter("pid"));
%>
  <title> <%=company%> | Recurring Module</title>

</head>
<style type="text/css">

  #myTable th{
    text-align: center;
  }

</style>
<body>
  <form name="form" method="post" action="RecurringModule?ctoken=<%=ctoken%>">
    <div class="content-page">
      <div class="content">
        <!-- Page Heading Start -->
        <div class="page-heading">

          <div class="pull-right">
            <div class="btn-group">
              <form action="/partner/net/RecurringModule?ctoken=<%=ctoken%>" name="form" method="post">
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                <input type=hidden name="pid" id="pid" value="<%=pid%>">


                <%
                  Enumeration<String> stringEnumeration=request.getParameterNames();
                  while(stringEnumeration.hasMoreElements())
                  {
                    String name=stringEnumeration.nextElement();
                    if ("trackingid".equals(name))
                    {
                      out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                    }
                    else
                    {
                      out.println("<input type='hidden' name='"+name+"' value='"+request.getParameter(name)+"'/>");
                    }
                  }
                %>

                <button class="btn-xs" type="submit" name="B1" style="background: transparent;border: 0;">
                  <img style="height: 35px;" src="/partner/images/goBack.png">
                </button>
              </form>
            </div>
          </div>
          <br><br><br>
          <div class="row">
            <div class="col-sm-12 portlets ui-sortable">
              <div class="widget">

                <div class="widget-header transparent">
                  <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Recurring Subscription Details</strong></h2>
                  <div class="additional-btn">
                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                  </div>
                </div>
                <br>
                <div class="widget-content padding" style="overflow-y: auto;">
                  <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                    <thead>

                    <tr>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Recurring Billing ID</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Interval</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Frequency</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Run Date</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Recurring Subscription Date</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Subscribe Amount</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Card Number</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Card Holder</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>status</b></td>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                      RecurringBillingVO recurringBillingVO = null;

                      recurringBillingVO = (RecurringBillingVO) request.getAttribute("recurringsub");
                    %>
                    <%
                      out.println("<tr>");
                      if (recurringBillingVO.getRecurring_subscrition_id()!=null)
                      {
                        out.println("<td align=center class=\"textb\" data-label=\"recurring_subscription_id\">&nbsp;" + recurringBillingVO.getRecurring_subscrition_id() + "</td>");
                      }
                      else if (recurringBillingVO.getRecurring_subscrition_id()==null)
                      {
                        out.println("<td align=center class=\"textb\" data-label=\"recurring_subscription_id\">&nbsp;"+"-"+"</td>");
                      }
                      if (recurringBillingVO.getInterval()!=null)
                      {
                        out.println("<td align=center class=\"textb\" data-label=\"Interval\">&nbsp;" + recurringBillingVO.getInterval() + "</td>");
                      }
                      else if (recurringBillingVO.getInterval()==null)
                      {
                        out.println("<td align=center class=\"textb\" data-label=\"Interval\">&nbsp;"+"-"+"</td>");
                      }

                      if (recurringBillingVO.getFrequency()!=null)
                      {
                        out.println("<td align=center class=\"textb\" data-label=\"Frequency\">&nbsp;" + recurringBillingVO.getFrequency() + "</td>");
                      }
                      else if (recurringBillingVO.getFrequency()==null)
                      {
                        out.println("<td align=center class=\"textb\" data-label=\"Frequency\">&nbsp;"+"-"+"</td>");
                      }

                      if (recurringBillingVO.getRunDate()!=null)
                      {
                        out.println("<td align=center class=\"textb\" data-label=\"Run Date\">&nbsp;" + recurringBillingVO.getRunDate() + "</td>");
                      }
                      else if (recurringBillingVO.getRunDate()==null)
                      {
                        out.println("<td align=center class=\"textb\" data-label=\"Run Date\">&nbsp;"+"-"+"</td>");
                      }

                      if(recurringBillingVO.getRecurringRegisterDate()!=null)
                      {
                          out.println("<td align=center class=\"textb\" data-label=\"Recurring Subscription Date\">&nbsp;" + recurringBillingVO.getRecurringRegisterDate() + "</td>");
                      }
                      else if(recurringBillingVO.getRecurringRegisterDate()==null)
                      {
                          out.println("<td align=center class=\"textb\" data-label=\"Recurring Subscription Date\">&nbsp;"+"-"+"</td>");
                      }

                      if(recurringBillingVO.getAmount()!=null)
                      {
                        out.println("<td align=center class=\"textb\" data-label=\"Subscribe Amount\">&nbsp;"+recurringBillingVO.getAmount()+"</td>");
                      }
                      else if (recurringBillingVO.getAmount()==null)
                      {
                        out.println("<td align=center class=\"textb\" data-label=\"Subscribe Amount\">&nbsp;"+"-"+"</td>");
                      }
                      if(recurringBillingVO.getFirstSix()!=null)
                      {
                        out.println("<td align=center class=\"textb\" data-label=\"Card Number\">&nbsp;"+recurringBillingVO.getFirstSix()+"****"+recurringBillingVO.getLastFour()+"</td>");
                      }
                      else if (recurringBillingVO.getFirstSix()==null)
                      {
                        out.println("<td align=center class=\"textb\" data-label=\"Card Number\">&nbsp;"+"-"+"</td>");
                      }
                      if(recurringBillingVO.getCardHolderName()!=null)
                      {
                        out.println("<td align=center class=\"textb\" data-label=\"Card Holder\">&nbsp;"+recurringBillingVO.getCardHolderName()+"</td>");;
                      }
                      else if (recurringBillingVO.getCardHolderName()==null)
                      {
                        out.println("<td align=center class=\"textb\" data-label=\"Card Holder\">&nbsp;"+"-"+"</td>");
                      }

                      if(recurringBillingVO.getRecurringStatus()!=null)
                      {
                        out.println("<td align=center class=\"textb\" data-label=\"Recurring Status\">&nbsp;"+recurringBillingVO.getRecurringStatus()+"</td>");
                      }
                      else if (recurringBillingVO.getRecurringStatus()==null)
                      {
                        out.println("<td align=center class=\"textb\" data-label=\"Recurring Status\">&nbsp;"+"-"+"</td>");
                      }
                      out.println("</tr>");
                    %>

                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          </div>


          <%-- Recurring Transaction Details --%>

          <%
            if(request.getAttribute("transactionList")!=null)
            {
              List<RecurringBillingVO> rbList = (List<RecurringBillingVO>) request.getAttribute("transactionList");
              if(rbList.size()>0)
              {
          %>
          <div class="row">
            <div class="col-sm-12 portlets ui-sortable">
              <div class="widget">

                <div class="widget-header transparent">
                  <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;Recurring Transaction Details</strong></h2>
                  <div class="additional-btn">
                    <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                    <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                    <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
                  </div>
                </div>
                <br>
                <div class="widget-content padding" style="overflow-y: auto;">
                  <table id="myTable" class="display table table-striped table-bordered" width="100%" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                    <thead>

                    <tr style="color: white;">
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Sr.No</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>TrackingId</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Amount</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Transaction Date(yyyy/MM/dd)</b></td>
                      <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b>Transaction Status</b></td>
                    </tr>
                    </thead>
                    <tbody>
                    <%
                          String style="class=td1";
                          String ext="light";
                          int pos = 1;
                          for(RecurringBillingVO recVO : rbList)
                          {
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

                            out.println("<tr>");
                            out.println("<td align=center data-label=\"Sr.No\">" +pos+"</td>");
                            out.println("<td align=center data-label=\"TrackingId\">" +recVO.getNewPzTransactionID()+"</td>");
                            out.println("<td align=center data-label=\"Amount\">" +recVO.getAmount()+"</td>");

                            if ("null".equals(recVO.getRecurringRunDate()))
                            {
                              out.println("<td align=center data-label=\"Transaction Date(yyyy/MM/dd)\">" +"-"+"</td>");

                            }
                            else if (!"null".equals(recVO.getRecurringRunDate()))
                            {
                              out.println("<td align=center data-label=\"Transaction Date(yyyy/MM/dd)\">" +recVO.getRecurringRunDate()+"</td>");
                            }

                            out.println("<td align=center data-label=\"Transaction Status\">" +recVO.getTransactionStatus()+"</td>");
                            out.println("</tr>");
                            pos++;
                          }
                        }
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
    </form>
</body>
</html>
