<%@ page import="com.directi.pg.Functions,com.directi.pg.LoadProperties,com.directi.pg.Logger" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.TransactionVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Ajit
  Date: 11/27/18
  Time: 2:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%
  String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  ResourceBundle rb= LoadProperties.getProperty("com.directi.pg.chargebackFraud");
  String partnerid= String.valueOf(session.getAttribute("partnerId"));
  session.setAttribute("submit","Fraud Intimation & Action");
  Logger logger = new Logger("fraudNotification.jsp");
%>
<html lang="en">
<head>
  <script type="text/javascript" src='/partner/css/new/html5shiv.min.js'></script>
  <script type="text/javascript" src='/partner/css/new/respond.min.js'></script>
  <script type="text/javascript" src='/partner/css/new/html5.js'></script>
  <title><%=company%> Fraud Management> Fraud Intimation & Action</title>

  <%--<script type="text/javascript" src="/partner/javascript/jquery.min.js?ver=1"></script>
  <script src="/partner/NewCss/js/jquery-ui.min.js"></script>--%>
  <script src="/partner/javascript/autocomplete_partner_memberid.js"></script>

  <link href="/merchant/datepicker/datepicker/datepicker3.css" rel="stylesheet">
  <script src="/merchant/datepicker/datepicker/bootstrap-datepicker.js"></script>
  <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
  <meta http-equiv="Expires" content="0">
  <meta http-equiv="Pragma" content="no-cache">

  <style type="text/css">
    @media(max-width: 991px) {
      .additional-btn {
        float: left;
        margin-left: 30px;
        margin-top: 10px;
        position: inherit!important;
      }
    }
  </style>
  <script type="text/javascript">
    $('#sandbox-container input').datepicker({
    });
  </script>
  <script>
    $(function() {
      $( ".datepicker" ).datepicker({dateFormat: "yy-mm-dd"});
    });
  </script>

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
    function fraudAlertsNotification()
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
        return flag;
      }
      var con=confirm("Do you really want to update all selected transaction.");

      if(con==true){
        return true;
      }
      else{
        return false;
      }
    }
  </script>
  <style type="text/css">
    #ui-id-4
    {
      overflow: auto;
      max-height: 250px;
    }
  </style>
</head>
<body>
<%
  HashMap<String, String> statusHash = new LinkedHashMap();
  statusHash.put("capturesuccess", "Capture Successful");
  statusHash.put("partialrefund", "Partial Refund");
  statusHash.put("settled", "Settled");

  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  Functions functions = new Functions();
  String status=Functions.checkStringNull(request.getParameter("status"))==null?"":request.getParameter("status");
  if (partner.isLoggedInPartner(session))
  {
    String str = "";

    String trackingId = Functions.checkStringNull(request.getParameter("STrackingid"));
    String firstSix = Functions.checkStringNull(request.getParameter("firstsix"));
    String lastFour = Functions.checkStringNull(request.getParameter("lastfour"));
    String emailAddr = Functions.checkStringNull(request.getParameter("emailaddr"))==null?"":request.getParameter("emailaddr");
    String name = Functions.checkStringNull(request.getParameter("name"));
    String memberId = request.getParameter("toid")==null?"":request.getParameter("toid");
    String description=Functions.checkStringNull(request.getParameter("description"));
    String paymentid=Functions.checkStringNull(request.getParameter("paymentid"));
    String accountId = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
    String pgtypeId = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
    String fromdate=null;
    String todate=null;

    String pid=Functions.checkStringNull(request.getParameter("pid"));
    String Config =null;
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    if(Roles.contains("superpartner")){

    }else{
      pid = String.valueOf(session.getAttribute("merchantid"));
      Config = "disabled";
    }

    str= str + "&ctoken="+((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if(trackingId!=null)str = str + "&STrackingid=" + trackingId;
    else
      trackingId="";
    if(pid!=null)str = str + "&pid=" + pid;
    else
      pid="";
    if(paymentid!=null)str = str + "&paymentid=" + paymentid;
    else
      paymentid="";
    if(description!=null)str = str + "&description=" + description;
    else
      description="";
    if(firstSix!=null)str = str + "&firstsix=" + firstSix;
    else
      firstSix="";
    if(lastFour!=null)str = str + "&lastfour=" + lastFour;
    else
      lastFour="";
    if(name!=null)str = str + "&name=" + name;
    else
      name="";
    if(status!=null)str = str + "&status=" + status;
    else
      status="";
    if(pgtypeId!=null)str = str + "&pgtypeid=" + pgtypeId;
    else
      pgtypeId="";
    if(accountId!=null)str = str + "&accountid=" + accountId;
    else
      accountId="";
    if(memberId!=null)str = str + "&toid=" + memberId;
    else
      memberId="";

    int pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
    int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);

    str = str + "&SRecords=" + pagerecords;
    Date date = new Date();
    SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

    String Date = originalFormat.format(date);
    date.setDate(1);
    String fromDate = originalFormat.format(date);

    fromdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ? fromDate : request.getParameter("fromdate");
    todate = Functions.checkStringNull(request.getParameter("todate")) == null ? Date : request.getParameter("todate");
    if (fromdate!=null) str =str + "&fromdate="+fromdate;
    //else fromdate="";
    if (todate!=null) str =str + "&todate="+todate;
    //else todate="";
    ResourceBundle rb1 = null;
    String language_property1 = (String) session.getAttribute("language_property");
    rb1 = LoadProperties.getProperty(language_property1);
    String fraudNotification_Merchant_Fraud = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Merchant_Fraud")) ? rb1.getString("fraudNotification_Merchant_Fraud") : "Merchant Fraud Reversal & Intimation";
    String fraudNotification_From = StringUtils.isNotEmpty(rb1.getString("fraudNotification_From")) ? rb1.getString("fraudNotification_From") : "From";
    String fraudNotification_To = StringUtils.isNotEmpty(rb1.getString("fraudNotification_To")) ? rb1.getString("fraudNotification_To") : "To";
    String fraudNotification_EmailId = StringUtils.isNotEmpty(rb1.getString("fraudNotification_EmailId")) ? rb1.getString("fraudNotification_EmailId") : "Email Id";
    String fraudNotification_TrackingId = StringUtils.isNotEmpty(rb1.getString("fraudNotification_TrackingId")) ? rb1.getString("fraudNotification_TrackingId") : "Tracking Id";
    String fraudNotification_PaymentID = StringUtils.isNotEmpty(rb1.getString("fraudNotification_PaymentID")) ? rb1.getString("fraudNotification_PaymentID") : "Payment ID";
    String fraudNotification_Description = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Description")) ? rb1.getString("fraudNotification_Description") : "Description";
    String fraudNotification_First_Six = StringUtils.isNotEmpty(rb1.getString("fraudNotification_First_Six")) ? rb1.getString("fraudNotification_First_Six") : "First Six";
    String fraudNotification_Last_Four = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Last_Four")) ? rb1.getString("fraudNotification_Last_Four") : "Last Four";
    String fraudNotification_Customer_Name = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Customer_Name")) ? rb1.getString("fraudNotification_Customer_Name") : "Customer Name";
    String fraudNotification_Status = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Status")) ? rb1.getString("fraudNotification_Status") : "Status";
    String fraudNotification_All = StringUtils.isNotEmpty(rb1.getString("fraudNotification_All")) ? rb1.getString("fraudNotification_All") : "All";
    String fraudNotification_Partner_ID = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Partner_ID")) ? rb1.getString("fraudNotification_Partner_ID") : "Partner ID";
    String fraudNotification_Bank_Name = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Bank_Name")) ? rb1.getString("fraudNotification_Bank_Name") : "Bank Name";
    String fraudNotification_Bank_AccountID = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Bank_AccountID")) ? rb1.getString("fraudNotification_Bank_AccountID") : "Bank Account ID";
    String fraudNotification_MerchantID = StringUtils.isNotEmpty(rb1.getString("fraudNotification_MerchantID")) ? rb1.getString("fraudNotification_MerchantID") : "Merchant ID";
    String fraudNotification_Path = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Path")) ? rb1.getString("fraudNotification_Path") : "Path";
    String fraudNotification_Search = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Search")) ? rb1.getString("fraudNotification_Search") : "Search";
    String fraudNotification_Report_Table = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Report_Table")) ? rb1.getString("fraudNotification_Report_Table") : "Report Table";
    String fraudNotification_SrNo = StringUtils.isNotEmpty(rb1.getString("fraudNotification_SrNo")) ? rb1.getString("fraudNotification_SrNo") : "Sr. No.";
    String fraudNotification_Date = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Date")) ? rb1.getString("fraudNotification_Date") : "Date";
    String fraudNotification_TrackingID = StringUtils.isNotEmpty(rb1.getString("fraudNotification_TrackingID")) ? rb1.getString("fraudNotification_TrackingID") : "TrackingID.";
    String fraudNotification_PartnerID = StringUtils.isNotEmpty(rb1.getString("fraudNotification_PartnerID")) ? rb1.getString("fraudNotification_PartnerID") : "PartnerID";
    String fraudNotification_MemberID = StringUtils.isNotEmpty(rb1.getString("fraudNotification_MemberID")) ? rb1.getString("fraudNotification_MemberID") : "MemberID";
    String fraudNotification_Card_Holder = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Card_Holder")) ? rb1.getString("fraudNotification_Card_Holder") : "Card Holder's Name";
    String fraudNotification_Email = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Email")) ? rb1.getString("fraudNotification_Email") : "Email";
    String fraudNotification_Amount = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Amount")) ? rb1.getString("fraudNotification_Amount") : "Amount";
    String fraudNotification_Refunded_Amount = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Refunded_Amount")) ? rb1.getString("fraudNotification_Refunded_Amount") : "Refunded Amount";
    String fraudNotification_Currency = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Currency")) ? rb1.getString("fraudNotification_Currency") : "Currency";
    String fraudNotification_CardNum = StringUtils.isNotEmpty(rb1.getString("fraudNotification_CardNum")) ? rb1.getString("fraudNotification_CardNum") : "Card Num";
    String fraudNotification_IsFraud = StringUtils.isNotEmpty(rb1.getString("fraudNotification_IsFraud")) ? rb1.getString("fraudNotification_IsFraud") : "IsFraud";
    String fraudNotification_Status1 = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Status1")) ? rb1.getString("fraudNotification_Status1") : "Status";
    String fraudNotification_Reason = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Reason")) ? rb1.getString("fraudNotification_Reason") : "Reason";
    String fraudNotification_Showing_Page = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Showing_Page")) ? rb1.getString("fraudNotification_Showing_Page") : "Showing Page";
    String fraudNotification_of = StringUtils.isNotEmpty(rb1.getString("fraudNotification_of")) ? rb1.getString("fraudNotification_of") : "of";
    String fraudNotification_records = StringUtils.isNotEmpty(rb1.getString("fraudNotification_records")) ? rb1.getString("fraudNotification_records") : "records";
    String fraudNotification_Sorry = StringUtils.isNotEmpty(rb1.getString("fraudNotification_Sorry")) ? rb1.getString("fraudNotification_Sorry") : "Sorry";
    String fraudNotification_No = StringUtils.isNotEmpty(rb1.getString("fraudNotification_No")) ? rb1.getString("fraudNotification_No") : "No records found.";
    String fraudNotification_filter = StringUtils.isNotEmpty(rb1.getString("fraudNotification_filter")) ? rb1.getString("fraudNotification_filter") : "filter";
    String fraudNotification_page_no = StringUtils.isNotEmpty(rb1.getString("fraudNotification_page_no")) ? rb1.getString("fraudNotification_page_no") : "Page number";
    String fraudNotification_total_no_of_records = StringUtils.isNotEmpty(rb1.getString("fraudNotification_total_no_of_records")) ? rb1.getString("fraudNotification_total_no_of_records") : "Total number of records";

%>

<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%> <%=fraudNotification_Merchant_Fraud%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding">
              <div id="horizontal-form">
                <form action="/partner/net/PartnerFraudNotification?ctoken=<%=ctoken%>" method="post" name="forms">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                  <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                  <%
                    String message = (String) request.getAttribute("error");
                    if(functions.isValueNull(message))
                    {
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                    }
                    String msg = (String) request.getAttribute("errorMsg");
                    if(functions.isValueNull(msg))
                    {
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + msg + "</h5>");
                    }
                  %>

                  <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                  <div class="form-group col-md-4 has-feedback">
                    <label><%=fraudNotification_From%></label>
                    <input type="text" size="16" name="fromdate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute (fromdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label><%=fraudNotification_To%></label>
                    <input type="text" size="16" name="todate" class="datepicker form-control" value="<%= ESAPI.encoder().encodeForHTMLAttribute (todate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                  </div>

                  <div class="ui-widget form-group col-md-4 has-feedback">
                    <label><%=fraudNotification_EmailId%></label>
                    <input name="emailaddr" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailAddr)%>">
                  </div>

                  <div class="ui-widget form-group col-md-4 has-feedback">
                    <label><%=fraudNotification_TrackingId%></label>
                    <input name="STrackingid"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingId)%>" class="form-control" autocomplete="on">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label><%=fraudNotification_PaymentID%></label>
                    <input  type="text" name="paymentid" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(paymentid)%>">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label><%=fraudNotification_Description%></label>
                    <input  type="text" name="description" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(description)%>">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label ><%=fraudNotification_First_Six%></label>
                    <input  type="text" name="firstsix" maxlength="6" class="form-control" value="<%=firstSix%>">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label ><%=fraudNotification_Last_Four%></label>
                    <input  type="text" name="lastfour" maxlength="4" class="form-control" value="<%=lastFour%>">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label ><%=fraudNotification_Customer_Name%></label>
                    <input  type="text" name="name" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(name)%>">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label><%=fraudNotification_Status%></label>
                    <select size="1" name="status" class="form-control">
                      <option value=""><%=fraudNotification_All%></option>
                      <%
                        Set statusSet = statusHash.keySet();
                        Iterator iterator=statusSet.iterator();
                        String selected = "";
                        String key = "";
                        String value = "";

                        while (iterator.hasNext())
                        {
                          key = (String)iterator.next();
                          value = (String) statusHash.get(key);

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
                    <label ><%=fraudNotification_Partner_ID%></label>
                    <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                    <input name="pid" type="hidden" value="<%=pid%>" >
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label><%=fraudNotification_Bank_Name%></label>
                    <input name="pgtypeid" id="bank_name" value="<%=ESAPI.encoder().encodeForHTMLAttribute(pgtypeId)%>" class="form-control" autocomplete="on">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label><%=fraudNotification_Bank_AccountID%></label>
                    <input name="accountid" id="acc_id" value="<%=ESAPI.encoder().encodeForHTMLAttribute(accountId)%>" class="form-control" autocomplete="on">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label ><%=fraudNotification_MerchantID%></label>
                    <input name="toid" id="member_id" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberId)%>" class="form-control" autocomplete="on">
                  </div>

                  <div class="form-group col-md-4">
                    <label style="color: transparent;"><%=fraudNotification_Path%></label>
                    <button type="submit" class="btn btn-default" style="display:block;">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;<%=fraudNotification_Search%>
                    </button>
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
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=fraudNotification_Report_Table%></strong></h2>
              <div class="additional-btn">
                <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>
            <div class="widget-content padding" style="overflow-y: auto;">
              <%
                boolean flag=true;
                String currentblock=request.getParameter("currentblock");
                String error=(String) request.getAttribute("successmsg");
                if(error !=null)
                {
                  out.println("<center><font class=\"textb\"><b>"+error+"<b></font></center><br>");
                }

                if(currentblock==null)
                  currentblock="1";
                if(request.getAttribute("cbVO")!=null)
                {
                  List<TransactionVO> cbVOList= (List<TransactionVO>) request.getAttribute("cbVO");
                  PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
                  paginationVO.setInputs(paginationVO.getInputs() + "&ctoken=" + ctoken);
                  if(cbVOList.size()>0)
                  {
              %>
              <form name="MerchantChargeback" action="/partner/net/PartnerFraudNotificationAction?ctoken=<%=ctoken%>" method="post">
                <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                  <thead>
                  <tr>
                    <td valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudNotification_SrNo%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudNotification_Date%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudNotification_TrackingID%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudNotification_PartnerID%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudNotification_MemberID%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudNotification_Card_Holder%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudNotification_Email%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b></b><%=fraudNotification_Amount%></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b></b><%=fraudNotification_Refunded_Amount%></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b></b><%=fraudNotification_Currency%></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b></b><%=fraudNotification_CardNum%></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b></b><%=fraudNotification_IsFraud%></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudNotification_Status1%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=fraudNotification_Reason%></b></td>
                  </tr>
                  <input type="hidden" value="<%=ctoken%>" name="ctoken">
                  </thead>
                  <%
                    int sno = 1;
                    String style="class=td1";
                    String ext="light";
                    Hashtable inner=null;

                    String reason = rb.getString("reason");
                    String reason1[] = reason.split("\\|");
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
                      if(functions.isValueNull(transactionVO.getFirstSix())&& functions.isValueNull(transactionVO.getLastFour())){
                        cardnumber=transactionVO.getFirstSix()+"******"+transactionVO.getLastFour();
                      }
                      else
                      {
                        cardnumber="";
                      }
                      String nameStr = "";
                      if(functions.isValueNull(transactionVO.getCustFirstName())&& functions.isValueNull(transactionVO.getCustLastName()))
                      {
                        nameStr=transactionVO.getCustFirstName()+" "+transactionVO.getCustLastName();
                      }
                      else {
                        nameStr="-";
                      }
                      String amountStr="";
                      if(functions.isValueNull(transactionVO.getAmount()))
                      {
                        amountStr=transactionVO.getAmount();
                      }
                      else {
                        amountStr="";
                      }
                      String refundamount="";
                      if(functions.isValueNull(transactionVO.getRefundAmount()))
                      {
                        refundamount=transactionVO.getRefundAmount();
                      }
                      else {
                        refundamount="";
                      }
                      PartnerFunctions pfunction = new PartnerFunctions();
                      int srno=sno+ ((pageno-1)*pagerecords);
                      out.println("<tr>");
                      out.println("<td " + style + " align=\"center\">&nbsp;<input type=\"checkbox\" name=\"trackingid\" value=\"" + transactionVO.getTrackingId()+ "\"></td>");
                      out.println("<td valign=\"middle\" align=\"center\">&nbsp;" + srno+"</td>");
                      out.println("<td valign=\"middle\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(transactionVO.getTimestamp())+"</td>");
                      out.println("<td valign=\"middle\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(transactionVO.getTrackingId())+"</td>");
                      out.println("<td valign=\"middle\" align=\"center\">&nbsp;" +pfunction.getPartnerId(ESAPI.encoder().encodeForHTML(transactionVO.getToid()))+"</td>");
                      out.println("<td valign=\"middle\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(transactionVO.getToid())+"</td>");
                      out.println("<td valign=\"middle\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(nameStr)+"</td>");
                      out.println("<td align=center " + style + ">&nbsp;<input type=\"hidden\" class=\"txtboxtabel\" name=\"emailaddr_" + transactionVO.getTrackingId() + "\" value=\"" + transactionVO.getEmailAddr() + "\">" + transactionVO.getEmailAddr()+"</td>");
                      out.println("<td valign=\"middle\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(amountStr)+"</td>");
                      out.println("<td valign=\"middle\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(refundamount)+"</td>");
                      out.println("<td valign=\"middle\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(transactionVO.getCurrency())+"</td>");
                      out.println("<td valign=\"middle\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(cardnumber)+"</td>");
                      out.println("<td valign=\"middle\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(transactionVO.getFraudRequest())+"</td>");
                      out.println("<td align=center " + style + ">&nbsp;<input type=\"hidden\" class=\"txtboxtabel\" name=\"status_" + transactionVO.getTrackingId() + "\" value=\"" + transactionVO.getStatus() + "\">"+transactionVO.getStatus()+"</td>");
                      out.println("<td align=center " + style + ">&nbsp; <select class=\"form-control\" style=\"width:150px\" name=\"reason_" + ESAPI.encoder().encodeForHTML(transactionVO.getTrackingId()) + "\">");
                      out.println("<option value=\"0\"  default>Select Reason</option>");

                      for (String remark:reason1)
                      {
                        out.println("<option value=\"" + remark + "\">" +remark+ "</option>");
                      }
                      out.println("</select></td>");
                      out.println("</tr>");
                      sno++;
                    }
                  %>
                </table>
                <table width="100%">
                  <thead>
                  <tr>
                      <td align="center">
                        <input type="submit" name="submitbutton" class="addnewmember" value="Fraud Intimation" onclick="return fraudAlertsNotification();">
                        <input type="submit" name="submitbutton" class="addnewmember" value="Fraud Reversal & Intimation" onclick="return fraudAlertsNotification();">
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
      <div id="showingid"><strong><%=fraudNotification_page_no%> <%=pageno%>  of  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
      <div id="showingid"><strong><%=fraudNotification_total_no_of_records%>   <%=paginationVO.getTotalRecords()%> </strong></div>

      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
        <jsp:param name="numrows" value="<%=pagerecords%>"/>
        <jsp:param name="pageno" value="<%=pageno%>"/>
        <jsp:param name="str" value="<%=str%>"/>
        <jsp:param name="page" value="PartnerFraudNotification"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
      <%
          }
          else
          {
            out.println(Functions.NewShowConfirmation1(fraudNotification_SrNo, fraudNotification_No));
          }
        }
        else if(flag)
        {
          out.println(Functions.NewShowConfirmation1(fraudNotification_filter, fraudNotification_No));
        }
      %>
    </div>
      <%
        }
        else
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
%>
    <script type="text/javascript" src="/partner/cookies/jquery.ihavecookies.js"></script>
    <script type="text/javascript" src="/partner/cookies/cookies_popup.js"></script>
    <link href="/partner/cookies/quicksand_font.css" rel="stylesheet">
    <link href="/partner/cookies/cookies_popup.css" rel="stylesheet">
</body>
</html>
