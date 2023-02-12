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
  String partnerid= String.valueOf(session.getAttribute("partnerId"));
  session.setAttribute("submit","Fraud Upload");
  Logger logger = new Logger("markFraudTransaction.jsp");
%>
<html lang="en">
<head>
  <script type="text/javascript" src='/partner/css/new/html5shiv.min.js'></script>
  <script type="text/javascript" src='/partner/css/new/respond.min.js'></script>
  <script type="text/javascript" src='/partner/css/new/html5.js'></script>
  <%--<script type="text/javascript" src='/partner/javascript/jquery.min.js?ver=1'></script>
  <script src="/partner/css/jquery-ui.min.js"></script>--%>
  <title><%=company%> Fraud Management> Fraud Upload</title>

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

    .fraudupload-btn{
      background-color: #98A3A3;
      border-radius: 25px;color: white;
      padding: 12px 16px;
      font-size: 16px;
      cursor: pointer;
    }

    .upload-fraud{
      margin-left: 64px;
      background-color: #d2d2d7;
    }
    #yourBtn {
      top: 150px;
      font-family: calibri;
      width: 222px;
      padding: 10px;
      border: 1px dashed #BBB;
      text-align: center;
      cursor: pointer;
      background-color: #98A3A3;
      border-radius: 25px;
      color: white;
      padding: 12px 16px;
      font-size: 16px;
    }
  </style>
  <style type="text/css">
    #ui-id-4
    {
      overflow: auto;
      max-height: 250px;
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
    function doFraudRequest()
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
  HashMap<String, String> statusHash = new LinkedHashMap();
  statusHash.put("authsuccessful", "Auth Successful");
  statusHash.put("capturesuccess", "Capture Successful");
  statusHash.put("chargeback", "Chargeback Reserved");
  statusHash.put("partialrefund", "Partial Refund");
  statusHash.put("markedforreversal", "Reversal Request Sent");
  statusHash.put("reversed", "Reversed");
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
    String pid=Functions.checkStringNull(request.getParameter("pid"));
    String Config =null;
    String Roles = partner.getRoleofPartner(String.valueOf(session.getAttribute("merchantid")));
    if(Roles.contains("superpartner")){

    }else{
      pid = String.valueOf(session.getAttribute("merchantid"));
      Config = "disabled";
    }
    String accountId = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
    String pgtypeId = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
    String fromdate=null;
    String todate=null;
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
    int pagerecords = Functions.convertStringtoInt(request.getParameter("SRecords"), Integer.parseInt((String) application.getAttribute("NOOFRECORDSPERPAGE")));

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
    String markFraudTransaction_Merchant_Fraud = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Merchant_Fraud")) ? rb1.getString("markFraudTransaction_Merchant_Fraud") : "Merchant Fraud Upload";
    String markFraudTransaction_From = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_From")) ? rb1.getString("markFraudTransaction_From") : "From";
    String markFraudTransaction_To = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_To")) ? rb1.getString("markFraudTransaction_To") : "To";
    String markFraudTransaction_EmailId = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_EmailId")) ? rb1.getString("markFraudTransaction_EmailId") : "Email Id";
    String markFraudTransaction_Tracking_Id = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Tracking_Id")) ? rb1.getString("markFraudTransaction_Tracking_Id") : "Tracking Id";
    String markFraudTransaction_Payment_ID = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Payment_ID")) ? rb1.getString("markFraudTransaction_Payment_ID") : "Payment ID";
    String markFraudTransaction_Merchant_OrderID = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Merchant_OrderID")) ? rb1.getString("markFraudTransaction_Merchant_OrderID") : "Merchant OrderID";
    String markFraudTransaction_First_Six = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_First_Six")) ? rb1.getString("markFraudTransaction_First_Six") : "First Six";
    String markFraudTransaction_Last_Four = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Last_Four")) ? rb1.getString("markFraudTransaction_Last_Four") : "Last Four";
    String markFraudTransaction_Customer_Name = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Customer_Name")) ? rb1.getString("markFraudTransaction_Customer_Name") : "Customer Name";
    String markFraudTransaction_Status = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Status")) ? rb1.getString("markFraudTransaction_Status") : "Status";
    String markFraudTransaction_Partner_ID = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Partner_ID")) ? rb1.getString("markFraudTransaction_Partner_ID") : "Partner ID";
    String markFraudTransaction_All = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_All")) ? rb1.getString("markFraudTransaction_All") : "All";
    String markFraudTransaction_Bank_Name = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Bank_Name")) ? rb1.getString("markFraudTransaction_Bank_Name") : "Bank Name";
    String markFraudTransaction_Bank_AccountID = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Bank_AccountID")) ? rb1.getString("markFraudTransaction_Bank_AccountID") : "Bank Account ID";
    String markFraudTransaction_Merchant_id = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Merchant_id")) ? rb1.getString("markFraudTransaction_Merchant_id") : "Merchant ID";
    String markFraudTransaction_Path = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Path")) ? rb1.getString("markFraudTransaction_Path") : "Path";
    String markFraudTransaction_Search = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Search")) ? rb1.getString("markFraudTransaction_Search") : "Search";
    String markFraudTransaction_Report_Table = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Report_Table")) ? rb1.getString("markFraudTransaction_Report_Table") : "Report Table";
    String markFraudTransaction_Sr_No = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Sr_No")) ? rb1.getString("markFraudTransaction_Sr_No") : "Sr. No.";
    String markFraudTransaction_PartnerID = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_PartnerID")) ? rb1.getString("markFraudTransaction_PartnerID") : "PartnerID";
    String markFraudTransaction_MemberID = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_MemberID")) ? rb1.getString("markFraudTransaction_MemberID") : "MemberID";
    String markFraudTransaction_AccountID = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_AccountID")) ? rb1.getString("markFraudTransaction_AccountID") : "Account ID";
    String markFraudTransaction_TrackingID = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_TrackingID")) ? rb1.getString("markFraudTransaction_TrackingID") : "Tracking ID";
    String markFraudTransaction_Merchant_OrderID1 = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Merchant_OrderID1")) ? rb1.getString("markFraudTransaction_Merchant_OrderID1") : "Merchant OrderID";
    String markFraudTransaction_PaymentID = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_PaymentID")) ? rb1.getString("markFraudTransaction_PaymentID") : "PaymentID";
    String markFraudTransaction_Customer_Name1 = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Customer_Name1")) ? rb1.getString("markFraudTransaction_Customer_Name1") : "Customer Name";
    String markFraudTransaction_Email = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Email")) ? rb1.getString("markFraudTransaction_Email") : "Email";
    String markFraudTransaction_CardNumber = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_CardNumber")) ? rb1.getString("markFraudTransaction_CardNumber") : "Card Number";
    String markFraudTransaction_Status1 = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Status1")) ? rb1.getString("markFraudTransaction_Status1") : "Status";
    String markFraudTransaction_Fraud_Reason = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Fraud_Reason")) ? rb1.getString("markFraudTransaction_Fraud_Reason") : "Fraud Reason";
    String markFraudTransaction_Remark = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Remark")) ? rb1.getString("markFraudTransaction_Remark") : "Remark";
    String markFraudTransaction_Showing_Page = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Showing_Page")) ? rb1.getString("markFraudTransaction_Showing_Page") : "Showing Page";
    String markFraudTransaction_of = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_of")) ? rb1.getString("markFraudTransaction_of") : "of";
    String markFraudTransaction_records = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_records")) ? rb1.getString("markFraudTransaction_records") : "records";
    String markFraudTransaction_Sorry = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Sorry")) ? rb1.getString("markFraudTransaction_Sorry") : "Sorry";
    String markFraudTransaction_no = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_no")) ? rb1.getString("markFraudTransaction_no") : "No records found.";
    String markFraudTransaction_filter = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_filter")) ? rb1.getString("markFraudTransaction_filter") : "filter";
    String markFraudTransaction_Upload_Fraud = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_Upload_Fraud")) ? rb1.getString("markFraudTransaction_Upload_Fraud") : "Upload Fraud";
    String markFraudTransaction_page_no = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_page_no")) ? rb1.getString("markFraudTransaction_page_no") : "Page number";
    String markFraudTransaction_total_no_of_records = StringUtils.isNotEmpty(rb1.getString("markFraudTransaction_total_no_of_records")) ? rb1.getString("markFraudTransaction_total_no_of_records") : "Total number of records";

%>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form action="/partner/uploadFraud.jsp?ctoken=<%=ctoken%>" method="POST">
            <button class="btn-xs" type="submit" value="Fraud Upload" name="submit" name="B1" style="background-color: #98A3A3; border-radius: 25px;color: white;padding: 8px 35px;font-size: 12px;cursor: pointer;">
              <%=markFraudTransaction_Upload_Fraud%>  &nbsp;<i class ="fa fa-upload"></i>
            </button>
          </form>
        </div>
      </div>
      <br>
      <br>
      <br>
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
          <div class="widget-header transparent">
            <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=company%><%=markFraudTransaction_Merchant_Fraud%></strong></h2>
            <div class="additional-btn">
              <a href="#" class="hidden reload"><i class="icon-ccw-1"></i></a>
              <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
              <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
            </div>
          </div>
            <div class="widget-content padding">
              <div id="horizontal-form">
                <form action="/partner/net/PartnerMarkFraudTransaction?ctoken=<%=ctoken%>" method="post" name="forms">
                  <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                  <input type="hidden" value="<%=partnerid%>" name="partnerid" id="partnerid">
                  <%
                      String message = (String) request.getAttribute("error");
                      if(functions.isValueNull(message))
                      {
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + message + "</h5>");
                      }
                    String errorMsg = (String) request.getAttribute("errorMsg");
                    if(functions.isValueNull(errorMsg))
                    {
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errorMsg + "</h5>");
                    }
                    String errormes=(String) request.getAttribute("errormes");
                    if(functions.isValueNull(errormes))
                    {
                      out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;" + errormes + "</h5>");
                    }
                  %>

                  <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                  <div class="form-group col-md-4 has-feedback">
                    <label><%=markFraudTransaction_From%></label>
                    <input type="text" size="16" name="fromdate" class="datepicker form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute (fromdate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                  </div>
                  <div class="form-group col-md-4 has-feedback">
                    <label><%=markFraudTransaction_To%></label>
                    <input type="text" size="16" name="todate" class="datepicker form-control" value="<%= ESAPI.encoder().encodeForHTMLAttribute (todate)%>" readonly="readonly" style="cursor: auto;background-color: #ffffff;opacity: 1;width: 100%;">
                  </div>
                  <div class="ui-widget form-group col-md-4 has-feedback">
                    <label><%=markFraudTransaction_EmailId%></label>
                    <input name="emailaddr" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailAddr)%>">
                  </div>

                  <div class="ui-widget form-group col-md-4 has-feedback">
                    <label><%=markFraudTransaction_Tracking_Id%></label>
                    <input name="STrackingid"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingId)%>" class="form-control" maxlength="100" autocomplete="on">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label><%=markFraudTransaction_Payment_ID%></label>
                    <input  type="text" name="paymentid" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(paymentid)%>">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label><%=markFraudTransaction_Merchant_OrderID%></label>
                    <input  type="text" name="description" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(description)%>">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label ><%=markFraudTransaction_First_Six%></label>
                    <input  type="text" name="firstsix" maxlength="6" class="form-control" value="<%=firstSix%>">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label ><%=markFraudTransaction_Last_Four%></label>
                    <input  type="text" name="lastfour" maxlength="4" class="form-control" value="<%=lastFour%>">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label ><%=markFraudTransaction_Customer_Name%></label>
                    <input  type="text" name="name" class="form-control" value="<%=ESAPI.encoder().encodeForHTMLAttribute(name)%>">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label><%=markFraudTransaction_Status%></label>
                    <select size="1" name="status" class="form-control">
                        <option value=""><%=markFraudTransaction_All%></option>
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
                    <label><%=markFraudTransaction_Partner_ID%></label>
                    <input name="pid" id="pid" value="<%=pid%>" class="form-control" autocomplete="on" <%=Config%>>
                    <input name="pid" type="hidden" value="<%=pid%>">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label><%=markFraudTransaction_Bank_Name%></label>
                    <input name="pgtypeid" id="bank_name" value="<%=ESAPI.encoder().encodeForHTMLAttribute(pgtypeId)%>" class="form-control" autocomplete="on">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label><%=markFraudTransaction_Bank_AccountID%></label>
                    <input name="accountid" id="acc_id" value="<%=ESAPI.encoder().encodeForHTMLAttribute(accountId)%>" class="form-control" autocomplete="on">
                  </div>

                  <div class="form-group col-md-4 has-feedback">
                    <label ><%=markFraudTransaction_Merchant_id%></label>
                    <input name="toid" id="member_id" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberId)%>" class="form-control" autocomplete="on">
                  </div>

                  <div class="form-group col-md-4">
                    <label style="color: transparent;"><%=markFraudTransaction_Path%></label>
                    <button type="submit" class="btn btn-default" style="display:block;">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;<%=markFraudTransaction_Search%>
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
              <h2><i class="fa fa-table"></i>&nbsp;&nbsp;<strong><%=markFraudTransaction_Report_Table%></strong></h2>
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

                if(currentblock==null)
                  currentblock="1";
                if(request.getAttribute("cbVO")!=null)
                {
                  List<TransactionVO> cbVOList= (List<TransactionVO>) request.getAttribute("cbVO");
                  List<TransactionVO> reasonList= (List<TransactionVO>) request.getAttribute("reasonList");
                  PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
                  paginationVO.setInputs(paginationVO.getInputs() + "&ctoken=" + ctoken);
                  if(cbVOList.size()>0)
                  {
              %>
              <form name="MerchantChargeback" action="/partner/net/PartnerMarkFraudChargeback?ctoken=<%=ctoken%>" method="post">
                <table align=center width="50%" class="display table table table-striped table-bordered table-hover dataTable" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                  <thead>
                  <tr>
                    <td valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=markFraudTransaction_Sr_No%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=markFraudTransaction_PartnerID%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=markFraudTransaction_MemberID%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=markFraudTransaction_AccountID%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=markFraudTransaction_TrackingID%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=markFraudTransaction_Merchant_OrderID1%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=markFraudTransaction_PaymentID%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b></b><%=markFraudTransaction_Customer_Name1%></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b></b><%=markFraudTransaction_Email%></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b></b><%=markFraudTransaction_CardNumber%></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=markFraudTransaction_Status1%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=markFraudTransaction_Fraud_Reason%></b></td>
                    <td  valign="middle" align="center" style="background-color: #7eccad !important;color: white;padding-top: 13px;padding-bottom: 13px;"><b><%=markFraudTransaction_Remark%></b></td>
                  </tr>
                  <input type="hidden" value="<%=ctoken%>" name="ctoken">
                  </thead>
                  <%
                    int sno = 1;
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

                      String payment_id = "";
                      if(functions.isValueNull(transactionVO.getPaymentId()))
                      {
                        payment_id=transactionVO.getPaymentId();
                      }
                      else {
                        payment_id="-";
                      }
                      PartnerFunctions partnerFunctions = new PartnerFunctions();
                      int srno=sno+ ((pageno-1)*pagerecords);
                      out.println("<tr>");
                      out.println("<td " + style + " align=\"center\">&nbsp;<input type=\"checkbox\" name=\"trackingid\" value=\"" + transactionVO.getTrackingId()+ "\"></td>");
                      out.println("<td valign=\"middle\" align=\"center\">&nbsp;" + srno+"</td>");
                      out.println("<td align=center " + style + ">&nbsp;<input type=\"hidden\" class=\"txtboxtabel\" name=\"pid_" + transactionVO.getTrackingId() + "\" value=\"" + partnerFunctions.getPartnerId(transactionVO.getToid()) + "\">"+partnerFunctions.getPartnerId(transactionVO.getToid())+"</td>");
                      out.println("<td align=center " + style + ">&nbsp;<input type=\"hidden\" class=\"txtboxtabel\" name=\"toid_" + transactionVO.getTrackingId() + "\" value=\"" + transactionVO.getToid() + "\">"+transactionVO.getToid()+"</td>");
                      out.println("<td align=center " + style + ">&nbsp;<input type=\"hidden\" class=\"txtboxtabel\" name=\"accountid_" + transactionVO.getTrackingId() + "\" value=\"" + transactionVO.getAccountId() + "\">"+transactionVO.getAccountId()+"</td>");
                      out.println("<td align=center " + style + ">&nbsp;<input type=\"hidden\" class=\"txtboxtabel\" name=\"accountid_" + transactionVO.getTrackingId() + "\" value=\"" + transactionVO.getTrackingId() + "\">"+transactionVO.getTrackingId()+"</td>");
                      out.println("<td align=center " + style + ">&nbsp;<input type=\"hidden\" class=\"txtboxtabel\" name=\"description_" + transactionVO.getTrackingId() + "\" value=\"" + transactionVO.getOrderDesc() + "\">"+transactionVO.getOrderDesc()+"</td>");
                      out.println("<td align=center " + style + ">&nbsp;"+payment_id+"</td>");
                      out.println("<td align=center " + style + ">&nbsp;"+nameStr+"</td>");
                      out.println("<td align=center " + style + ">&nbsp;<input type=\"hidden\" class=\"txtboxtabel\" name=\"emailaddr_" + transactionVO.getTrackingId() + "\" value=\"" + transactionVO.getEmailAddr() + "\">"+transactionVO.getEmailAddr()+"</td>");
                      out.println("<td valign=\"middle\" align=\"center\">&nbsp;" + cardnumber+"</td>");
                      out.println("<td valign=\"middle\" align=\"center\">&nbsp;" + ESAPI.encoder().encodeForHTML(transactionVO.getStatus() )+ "</td>");
                      out.println("<td align=center "+style+"><select class=\"txtbox\" name=\"fraudreason"+ESAPI.encoder().encodeForHTML(transactionVO.getTrackingId())+"\">");
                      out.println("<option value=\"0\"  default>Select Reason</option>");

                      for(TransactionVO transactionVO1 : reasonList)
                      {
                        String reason=transactionVO1.getFraudreason();
                        String type=transactionVO1.getType();
                        String code=transactionVO1.getCode();
                        out.println("<option value=\""+reason+"\">"+type+"-"+code+"-"+reason+"</option>");
                      }
                      out.println("</select></td>");
                      out.println("<td valign=\"middle\" align=\"center\"><input type=\"text\" class=\"txtbox\" name=\"reason_"+transactionVO.getTrackingId()+"\" value=\"\"></td>");
                      out.println("</tr>");
                      sno++;
                    }
                  %>
                </table>
                <table width="100%">
                  <thead>
                  <tr>
                    <td width="15%" align="center">
                        <input type="submit" name="submitbutton" class="addnewmember" value="Submit" onclick="return doFraudRequest();">
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
      <div id="showingid"><strong><%=markFraudTransaction_page_no%> <%=pageno%>  of  <%=TotalPageNo%></strong></div> &nbsp;&nbsp;
      <div id="showingid"><strong><%=markFraudTransaction_total_no_of_records%>   <%=paginationVO.getTotalRecords()%> </strong></div>
      <jsp:include page="page.jsp" flush="true">
        <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
        <jsp:param name="numrows" value="<%=pagerecords%>"/>
        <jsp:param name="pageno" value="<%=pageno%>"/>
        <jsp:param name="str" value="<%=str%>"/>
        <jsp:param name="page" value="PartnerMarkFraudTransaction"/>
        <jsp:param name="currentblock" value="<%=currentblock%>"/>
        <jsp:param name="orderby" value=""/>
      </jsp:include>
      <%
          }
          else
          {
            out.println(Functions.NewShowConfirmation1(markFraudTransaction_Sorry, markFraudTransaction_no));
          }
        }
        else if(flag)
        {
          out.println(Functions.NewShowConfirmation1(markFraudTransaction_filter, markFraudTransaction_no));
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
