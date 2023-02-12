<%@ page import="com.directi.pg.Functions,com.directi.pg.LoadProperties,com.directi.pg.Logger" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.TransactionVO" %>
<%@ page import="com.manager.vo.payoutVOs.CBReasonsVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  session.setAttribute("submit","MerchantChargeback");
  Logger logger = new Logger("partnerMerchantChargebackList.jsp");
%>
<style type="text/css">
  #ui-id-2
  {
    overflow: auto;
    max-height: 350px;
  }
</style>
<html lang="en">
<head>
  <script type="text/javascript" src='/partner/css/new/html5shiv.min.js'></script>
  <script type="text/javascript" src='/partner/css/new/respond.min.js'></script>
  <script type="text/javascript" src='/partner/css/new/html5.js'></script>
  <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/css/jquery-ui.min.js"></script>--%>
  <title><%=company%> Transaction Management> Merchant Chargeback</title>

  <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>
  <script language="javascript">
    function ToggleAll(checkbox)
    {
      flag = checkbox.checked;
      var checkboxes = document.getElementsByName("trackingid");
      var total_boxes = checkboxes.length;

      for(i=0; i<total_boxes; i++ )
      {
        checkboxes[i].checked =flag;
      }
    }
    function DoChargeback()
    {
      var checkboxes = document.getElementsByName("trackingid");
      var total_boxes = checkboxes.length;
      flag = false;

      for(i=0; i<total_boxes; i++ )
      {
        if(checkboxes[i].checked)
        {
          flag= true;
          break;
        }
      }
      if(!flag)
      {
        alert("Select at least one transaction");
        return false;
      }

      if (confirm("Do you really want to chargeback all selected transaction."))
      {
        document.MerchantChargeback.submit();
      }
    }
    function doUpdateRetrivalRequest()
    {
      var checkboxes = document.getElementsByName("trackingid");
      var total_boxes = checkboxes.length;
      flag = false;

      for(i=0; i<total_boxes; i++ )
      {
        if(checkboxes[i].checked)
        {
          flag= true;
          break;
        }
      }
      if(!flag)
      {
        alert("Select at least one transaction");
        return false;
      }
      if (confirm("Do you really want to update all selected transaction."))
      {
        document.MerchantChargeback.submit();
      }
    }
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
<%
  Functions functions = new Functions();
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
    String str = "";
    str ="&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    String memberid=Functions.checkStringNull(request.getParameter("toid"))==null?"":request.getParameter("toid");
    String pid=Functions.checkStringNull(request.getParameter("pid"))==null?"":request.getParameter("pid");
    String desc = Functions.checkStringNull(request.getParameter("description"));
    String paymentid = Functions.checkStringNull(request.getParameter("paymentid"))==null?"":request.getParameter("paymentid");
    String trackingid = Functions.checkStringNull(request.getParameter("trackingid"))==null?"":request.getParameter("trackingid");
    String accountid=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
    String partnerid = session.getAttribute("partnerId").toString();
    String Config =null;
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    if(Roles.contains("superpartner")){

    }else{
      pid = String.valueOf(session.getAttribute("merchantid"));
      Config = "disabled";
    }
    if(memberid!=null)str = str + "&toid=" + memberid;
    else
      memberid="";
    if(pid!=null)str = str + "&toid=" + pid;
    else
      pid="";
    if(desc!=null)str = str + "&description=" + desc;
    else
      desc="";
    if(paymentid!=null)str = str + "&paymentid=" + paymentid;
    else
      paymentid="";
    if(trackingid!=null)str = str + "&trackingid=" + trackingid;
    else
      trackingid="";

    int pageno=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
    int pagerecords=Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
    str = str + "&SRecords=" + pagerecords;

    ResourceBundle rb1 = null;
    String language_property1 = (String)session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);


    String partnerMerchantChargebackList_Merchants_Chargeback = rb1.getString("partnerMerchantChargebackList_Merchants_Chargeback");
    String partnerMerchantChargebackList_Tracking_ID = rb1.getString("partnerMerchantChargebackList_Tracking_ID");
    String partnerMerchantChargebackList_Partner_ID = rb1.getString("partnerMerchantChargebackList_Partner_ID");
    String partnerMerchantChargebackList_Merchant_ID = rb1.getString("partnerMerchantChargebackList_Merchant_ID");
    String partnerMerchantChargebackList_Order_ID = rb1.getString("partnerMerchantChargebackList_Order_ID");
    String partnerMerchantChargebackList_Payment_ID = rb1.getString("partnerMerchantChargebackList_Payment_ID");
    String partnerMerchantChargebackList_Search = rb1.getString("partnerMerchantChargebackList_Search");
    String partnerMerchantChargebackList_ReportTable = rb1.getString("partnerMerchantChargebackList_ReportTable");
    String partnerMerchantChargebackList_TransactionDate = rb1.getString("partnerMerchantChargebackList_TransactionDate");
    String partnerMerchantChargebackList_PartnerID = rb1.getString("partnerMerchantChargebackList_PartnerID");
    String partnerMerchantChargebackList_MerchantID = rb1.getString("partnerMerchantChargebackList_MerchantID");
    String partnerMerchantChargebackList_AccountID = rb1.getString("partnerMerchantChargebackList_AccountID");
    String partnerMerchantChargebackList_TrackingID = rb1.getString("partnerMerchantChargebackList_TrackingID");
    String partnerMerchantChargebackList_PaymentID = rb1.getString("partnerMerchantChargebackList_PaymentID");
    String partnerMerchantChargebackList_CustomerName = rb1.getString("partnerMerchantChargebackList_CustomerName");
    String partnerMerchantChargebackList_CardNumber = rb1.getString("partnerMerchantChargebackList_CardNumber");
    String partnerMerchantChargebackList_OrderID = rb1.getString("partnerMerchantChargebackList_OrderID");
    String partnerMerchantChargebackList_Currency = rb1.getString("partnerMerchantChargebackList_Currency");
    String partnerMerchantChargebackList_CapturedAmount = rb1.getString("partnerMerchantChargebackList_CapturedAmount");
    String partnerMerchantChargebackList_RefundedAmount = rb1.getString("partnerMerchantChargebackList_RefundedAmount");
    String partnerMerchantChargebackList_Status = rb1.getString("partnerMerchantChargebackList_Status");
    String partnerMerchantChargebackList_CBAmount = rb1.getString("partnerMerchantChargebackList_CBAmount");
    String partnerMerchantChargebackList_CBReason = rb1.getString("partnerMerchantChargebackList_CBReason");
    String partnerMerchantChargebackList_Showing_Page = rb1.getString("partnerMerchantChargebackList_Showing_Page");
    String partnerMerchantChargebackList_of = rb1.getString("partnerMerchantChargebackList_of");
    String partnerMerchantChargebackList_records = rb1.getString("partnerMerchantChargebackList_records");
    String partnerMerchantChargebackList_Sorry = rb1.getString("partnerMerchantChargebackList_Sorry");
    String partnerMerchantChargebackList_No = rb1.getString("partnerMerchantChargebackList_No");
    String partnerMerchantChargebackList_filter = rb1.getString("partnerMerchantChargebackList_filter");
    String partnerMerchantChargebackList_Please = rb1.getString("partnerMerchantChargebackList_Please");
    String partnerMerchantChargebackList_page_no = rb1.getString("partnerMerchantChargebackList_page_no");
    String partnerMerchantChargebackList_total_no_of_records = rb1.getString("partnerMerchantChargebackList_total_no_of_records");

%>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/uploadMerchantChargeback.jsp?ctoken=<%=ctoken%>" method="POST">
            <button  class="btn-xs" type="submit" value="chargebackupload" name="submit" name="B1"
                     style="background-color: #98A3A3; border-radius: 29px;color: white;padding: 11px 21px;font-size: 12px;cursor: pointer">
              Upload Chargeback <i class ="fa fa-upload"></i>
            </button>
          </form>
        </div>
      </div>
      </br>
      </br>
      </br>
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> <%=partnerMerchantChargebackList_Merchants_Chargeback%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <div id="horizontal-form">
                <form action="/partner/net/PartnerMerchantChargebackList" method="post" name="forms" >
                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                  <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                  <%
                    boolean flag=true;
                    String error=(String) request.getAttribute("error");
                    if(error !=null && error!="")
                    {
                      flag=false;
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + error + "</h5>");
                    }
                    else
                    {
                      error = "";
                    }
                  %>

                  <div class="form-group col-md-3">
                    <label><%=partnerMerchantChargebackList_Tracking_ID%></label>
                    <input type="text" name="trackingid"  value="<%=trackingid%>" maxlength="100" class="form-control">
                  </div>

                  <div class="form-group col-md-3">
                    <label for="pid"><%=partnerMerchantChargebackList_Partner_ID%></label>
                    <input name="pid" id="pid" maxlength="20" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                    <input name="pid" type="hidden" value="<%=pid%>">
                  </div>

                  <div class="form-group col-md-3">
                    <div class="ui-widget">
                      <label for="member"><%=partnerMerchantChargebackList_Merchant_ID%></label>
                      <input name="toid" id="member" value="<%=memberid%>" class="form-control" autocomplete="on">
                    </div>
                  </div>

                  <div class="form-group col-md-3">
                    <label><%=partnerMerchantChargebackList_Order_ID%></label>
                    <input type="text" name="description" maxlength="100" value="<%=desc%>" class="form-control">
                  </div>

                  <div class="form-group col-md-3">
                    <label><%=partnerMerchantChargebackList_Payment_ID%></label>
                    <input type="text" name="paymentid" maxlength="100" value="<%=paymentid%>" class="form-control">
                  </div>
                  <div class="form-group col-md-3">
                    <label>&nbsp;</label>
                    <button type="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-save"></i>&nbsp;&nbsp;<%=partnerMerchantChargebackList_Search%></button>
                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
      <br>

      <div class="row reporttable">
        <div class="col-md-12">
          <div class="widget">
            <div class="widget-header">
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=partnerMerchantChargebackList_ReportTable%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-y: auto;">
              <%
                flag=true;
                String currentblock=request.getParameter("currentblock");
                String errormsg1 = (String)request.getAttribute("message");
                if (errormsg1 == null)
                {
                  errormsg1 = "";
                }
                else
                {
                  flag=false;
                  out.println("<center><table align=\"center\" class=\"textb\" >" );
                  out.println(errormsg1);
                  out.println("</table></center>");
                }

                if(currentblock==null)
                  currentblock="1";

                if(currentblock==null)
                  currentblock="1";
                if(request.getAttribute("cbVO")!=null)
                {
                  List<TransactionVO> cbVOList= (List<TransactionVO>) request.getAttribute("cbVO");
                  List<CBReasonsVO> reasonVOList= (List<CBReasonsVO>) request.getAttribute("reason");
                  PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
                  paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);
                  if(cbVOList.size()>0)
                  {
              %>
              <form name="MerchantChargeback" action="/partner/net/PartnerDoChargeback?ctoken=<%=ctoken%>" method="post">
                <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                  <thead>
                  <tr>
                    <td style="display:none"></td>
                    <td valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantChargebackList_TransactionDate%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantChargebackList_PartnerID%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantChargebackList_MerchantID%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantChargebackList_AccountID%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantChargebackList_TrackingID%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantChargebackList_PaymentID%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b></b><%=partnerMerchantChargebackList_CustomerName%></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b></b><%=partnerMerchantChargebackList_CardNumber%></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantChargebackList_OrderID%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantChargebackList_Currency%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantChargebackList_CapturedAmount%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantChargebackList_RefundedAmount%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantChargebackList_Status%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantChargebackList_CBAmount%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=partnerMerchantChargebackList_CBReason%></b></td>
                  </tr>
                  <input type="hidden" value="<%=ctoken%>" name="ctoken">
                  </thead>
                  <%
                    String style="class=td1";
                    String ext="light";
                    Hashtable inner=null;
                    for(TransactionVO transactionVO : cbVOList)
                    {
                      int pos=1;
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
                      String cardnumber = "";
                      if (functions.isValueNull(transactionVO.getCardNumber()))
                      {
                        cardnumber = transactionVO.getCardNumber();
                      }
                      else
                      {
                        cardnumber = "-";
                      }

                      String currency = transactionVO.getCurrency();
                      String captureamount=transactionVO.getCapAmount();
                      String refundamount=transactionVO.getRefundAmount();
                      if ("JPY".equalsIgnoreCase(currency))
                      {
                        captureamount = functions.printNumber(Locale.JAPAN, captureamount);
                        refundamount = functions.printNumber(Locale.JAPAN, refundamount);
                      }
                      else if ("EUR".equalsIgnoreCase(currency))
                      {
                        captureamount = functions.printNumber(Locale.FRANCE, captureamount);
                        refundamount = functions.printNumber(Locale.FRANCE, refundamount);
                      }
                      else if ("GBP".equalsIgnoreCase(currency))
                      {
                        captureamount = functions.printNumber(Locale.UK, captureamount);
                        refundamount = functions.printNumber(Locale.UK, refundamount);
                      }
                      else if ("USD".equalsIgnoreCase(currency))
                      {
                        captureamount = functions.printNumber(Locale.US, captureamount);
                        refundamount = functions.printNumber(Locale.US, refundamount);
                      }
                      out.println("<tr>");
                      out.println("<td style=\"display:none\"><input type=\"hidden\" name=\"notificationURL_"+transactionVO.getTrackingId()+"\"  value=\""+transactionVO.getNotificationUrl()+"\"></td>");
                      out.println("<td " + style + " align=\"center\">&nbsp;<input type=\"checkbox\" name=\"trackingid\" value=\"" + transactionVO.getTrackingId()+ "\"></td>");
                      out.println("<td valign=\"middle\" data-label=\"Transaction Date\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(transactionVO.getTimestamp()) + "</td>");
                      out.println("<td align=center " + style + ">&nbsp;<input type=\"hidden\" class=\"txtboxtabel\" name=\"pid_" + partner.getPartnerIdBYname(transactionVO.getToType()) + "\" value=\"" + partner.getPartnerIdBYname(transactionVO.getToType()) + "\">"+partner.getPartnerIdBYname(transactionVO.getToType())+"</td>");
                      out.println("<td align=center " + style + ">&nbsp;<input type=\"hidden\" class=\"txtboxtabel\" name=\"toid_" + transactionVO.getTrackingId() + "\" value=\"" + transactionVO.getToid() + "\">"+transactionVO.getToid()+"</td>");
                      out.println("<td align=center " + style + ">&nbsp;<input type=\"hidden\" class=\"txtboxtabel\" name=\"accountid_" + transactionVO.getTrackingId()  + "\" value=\"" + transactionVO.getAccountId() + "\">"+transactionVO.getAccountId()+"</td>");
                      out.println("<td align=center " + style + ">&nbsp;<input type=\"hidden\" class=\"txtboxtabel\" name=\"trackingid_" + transactionVO.getTrackingId() + "\" value=\"" + transactionVO.getTrackingId() + "\">"+transactionVO.getTrackingId()+"</td>");
                      out.println("<td align=center " + style + ">&nbsp;<input type=\"hidden\" class=\"txtboxtabel\" name=\"paymentid_" + transactionVO.getPaymentId() + "\" value=\"" + transactionVO.getPaymentId() + "\">"+transactionVO.getPaymentId()+"</td>");
                      out.println("<td align=center " + style + ">&nbsp;<input type=\"hidden\" class=\"txtboxtabel\" name=\"customername" + transactionVO.getCustFirstName() + "\" value=\"" + transactionVO.getCustFirstName() + "\">"+transactionVO.getCustFirstName()+" "+transactionVO.getCustLastName()+"</td>");
                      out.println("<td align=center " + style + ">&nbsp;<input type=\"hidden\" class=\"txtboxtabel\" name=\"cardnumber" + cardnumber + "\" value=\"" + cardnumber + "\">"+cardnumber+"</td>");
                      out.println("<td valign=\"middle\" data-label=\"Order Desc\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(transactionVO.getOrderDesc())+"</td>");
                      out.println("<td valign=\"middle\" data-label=\"Currency\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(currency)+"</td>");
                      out.println("<td align=center " + style + ">&nbsp;<input type=\"hidden\" class=\"txtboxtabel\" name=\"captureamount_" + transactionVO.getTrackingId() + "\" value=\"" + ESAPI.encoder().encodeForHTML(transactionVO.getCapAmount()) + "\">"+ESAPI.encoder().encodeForHTML(captureamount)+"</td>");
                      out.println("<td align=center " + style + ">&nbsp;<input type=\"hidden\" class=\"txtboxtabel\" name=\"refundamount_" + transactionVO.getTrackingId() + "\" value=\"" + ESAPI.encoder().encodeForHTML(transactionVO.getRefundAmount()) + "\">"+ESAPI.encoder().encodeForHTML(refundamount)+"</td>");

                      //out.println("<td valign=\"middle\" data-label=\"Captured Amount\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(captureamount)+"</td>");
                      // out.println("<td valign=\"middle\" data-label=\"Refunded Amount\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(refundamount)+ "</td>");
                      out.println("<td valign=\"middle\" data-label=\"Status\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(transactionVO.getStatus() )+ "</td>");
                      out.println("<td align=center " + style + ">&nbsp;<input type=\"text\" class=\"form-control\" name=\"cbamount_" + transactionVO.getTrackingId() + "\" value=\"" + transactionVO.getAmount() + "\"></td>");
                      out.println("<td align=center " + style + ">&nbsp; <select class=\"form-control\" style=\"width:150px\" name=\"chargebackreason_" + ESAPI.encoder().encodeForHTML(transactionVO.getTrackingId()) + "\">");
                      out.println("<option value=\"0\"  default>Select Reason</option>");

                      for (CBReasonsVO transactionVO1 : reasonVOList)
                      {
                        String reason = transactionVO1.getCbreason();
                        String reasonId = transactionVO1.getCbReasonId();
                        String type=transactionVO1.getType();
                        out.println("<option value=\"" + reason + "\">" +type+" - "+reasonId+" "+reason+ "</option>");
                      }
                      out.println("</select></td>");
                      out.println("</tr>");
                    }
                  %>
                </table>
                <table width="100%">
                  <thead>
                  <tr>
                    <td width="15%" align="center">
                      <input type="button" name="submitbutton" class="addnewmember" value="Chargeback Selected" onclick="return DoChargeback();">
                    </td>
                  </tr>
                  </thead>
                </table>
              </form>
            </div>
          </div>
        </div>
      </div>
      <%
        int TotalPageNo;
        if(paginationVO.getTotalRecords()%pagerecords!=0)
        {
          TotalPageNo=paginationVO.getTotalRecords()/pagerecords+1;
        }
        else
        {
          TotalPageNo=paginationVO.getTotalRecords()/pagerecords;
        }
      %>
      <div id="showingid"><strong><%=partnerMerchantChargebackList_page_no%> <%=pageno%> <%=partnerMerchantChargebackList_of%> <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
      <div id="showingid"><strong><%=partnerMerchantChargebackList_total_no_of_records%>   <%=paginationVO.getTotalRecords()%> </strong></div>
      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
        <jsp:param name="numrows" value="<%=pagerecords%>"/>
        <jsp:param name="pageno" value="<%=pageno%>"/>
        <jsp:param name="str" value="<%=str%>"/>
        <jsp:param name="page" value="PartnerMerchantChargebackList"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
      <%
          }
          else
          {
            out.println(Functions.NewShowConfirmation1(partnerMerchantChargebackList_Sorry, partnerMerchantChargebackList_No));
          }
        }
        else if(flag)
        {
          out.println(Functions.NewShowConfirmation1(partnerMerchantChargebackList_filter, partnerMerchantChargebackList_Please));
        }
      %>
    </div>
      <%
        }
        else
        {
            response.sendRedirect("/partner/logout.jsp");
            return;
        }
%>
    <script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
    <script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
    <link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
    <link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
</body>
</html>
