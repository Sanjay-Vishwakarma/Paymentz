<%@ page language="java" import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.paymentgateway.CredoraxPaymentGateway" %>
<%@ page import="com.directi.pg.core.paymentgateway.EcorePaymentGateway" %>
<%@ page import="com.directi.pg.core.paymentgateway.PayDollarPaymentGateway" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ include file="ietest.jsp" %>
<%@ include file="Top.jsp" %>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));
  ResourceBundle rb1 = null;
  String language_property1 = (String)session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String payoutreason_PayoutInformation = StringUtils.isNotEmpty(rb1.getString("payoutreason_PayoutInformation"))?rb1.getString("payoutreason_PayoutInformation"): "Payout Information";
  String payoutreason_GoBack = StringUtils.isNotEmpty(rb1.getString("payoutreason_GoBack"))?rb1.getString("payoutreason_GoBack"): "Go Back";
  String payoutreason_TrackingId = StringUtils.isNotEmpty(rb1.getString("payoutreason_TrackingId"))?rb1.getString("payoutreason_TrackingId"): "Tracking Id";
  String payoutreason_TransactionId = StringUtils.isNotEmpty(rb1.getString("payoutreason_TransactionId"))?rb1.getString("payoutreason_TransactionId"): "Transaction Id";
  String payoutreason_Description = StringUtils.isNotEmpty(rb1.getString("payoutreason_Description"))?rb1.getString("payoutreason_Description"): "Description";
  String payoutreason_initial = StringUtils.isNotEmpty(rb1.getString("payoutreason_initial"))?rb1.getString("payoutreason_initial"): "Initial Trans Amount (in";
  String payoutreason_Status = StringUtils.isNotEmpty(rb1.getString("payoutreason_Status"))?rb1.getString("payoutreason_Status"): "Status";
  String payoutreason_OrderId = StringUtils.isNotEmpty(rb1.getString("payoutreason_OrderId"))?rb1.getString("payoutreason_OrderId"): "OrderId *";
  String payoutreason_order = StringUtils.isNotEmpty(rb1.getString("payoutreason_order"))?rb1.getString("payoutreason_order"): "Order Description / CustomerId *";
  String payoutreason_payout = StringUtils.isNotEmpty(rb1.getString("payoutreason_payout"))?rb1.getString("payoutreason_payout"): "Payout Amount(in";
  String payoutreason_CustomerBank = StringUtils.isNotEmpty(rb1.getString("payoutreason_CustomerBank"))?rb1.getString("payoutreason_CustomerBank"): "Customer Bank Code*";
  String payoutreason_account1 = StringUtils.isNotEmpty(rb1.getString("payoutreason_account1"))?rb1.getString("payoutreason_account1"): "Customer Bank Account Number*";
  String payoutreason_bank = StringUtils.isNotEmpty(rb1.getString("payoutreason_bank"))?rb1.getString("payoutreason_bank"): "Customer Bank Account Name*";
  String payoutreason_Transfer_Type = StringUtils.isNotEmpty(rb1.getString("payoutreason_Transfer_Type"))?rb1.getString("payoutreason_Transfer_Type"): "Transfer_Type*";
  String payoutreason_bank1 = StringUtils.isNotEmpty(rb1.getString("payoutreason_bank1"))?rb1.getString("payoutreason_bank1"): "Bank_Account_No*";
  String payoutreason_Bank_Ifsc = StringUtils.isNotEmpty(rb1.getString("payoutreason_Bank_Ifsc"))?rb1.getString("payoutreason_Bank_Ifsc"): "Bank_Ifsc*";
  String payoutreason_account = StringUtils.isNotEmpty(rb1.getString("payoutreason_account"))?rb1.getString("payoutreason_account"): "Bank Account Name*";
  String payoutreason_NEFT = StringUtils.isNotEmpty(rb1.getString("payoutreason_NEFT"))?rb1.getString("payoutreason_NEFT"): "NEFT";
  String payoutreason_RTGS = StringUtils.isNotEmpty(rb1.getString("payoutreason_RTGS"))?rb1.getString("payoutreason_RTGS"): "RTGS";
  String payoutreason_IMPS = StringUtils.isNotEmpty(rb1.getString("payoutreason_IMPS"))?rb1.getString("payoutreason_IMPS"): "IMPS";
  String payoutreason_UPI = StringUtils.isNotEmpty(rb1.getString("payoutreason_UPI"))?rb1.getString("payoutreason_UPI"): "UPI";
  String payoutreason_Payout = StringUtils.isNotEmpty(rb1.getString("payoutreason_Payout"))?rb1.getString("payoutreason_Payout"): "Payout";
  String payoutreason_Sorry = StringUtils.isNotEmpty(rb1.getString("payoutreason_Sorry"))?rb1.getString("payoutreason_Sorry"): "Sorry";
  String payoutreason_no = StringUtils.isNotEmpty(rb1.getString("payoutreason_no"))?rb1.getString("payoutreason_no"): "No records found. Invalid TransactionID";

%>
<html>
<head>
  <title><%=company%> Merchant Settings > Merchant Profile</title>
  <script language="javascript">
    function isint(form){
      if (isNaN((form.numrows.value)))
        return false;
      else
        return true;
    }
    function formSubmit(){
      var form = document.form1;
      if (isNaN(form.refundamount.value) || parseFloat(form.refundamount.value) <= 0){
        alert("Please Enter a Valid Refund Amount greater than 0");
        return false;
      }
      return true;
    }
  </script>
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
  <style type="text/css">
    #main{background-color: #ffffff}
    :target:before {
      content: "";
      display: block;
      height: 50px;
      margin: -50px 0 0;
    }
    .table > thead > tr > th {font-weight: inherit;}
    :target:before {
      content: "";
      display: block;
      height: 90px;
      margin: -50px 0 0;
    }
    footer{border-top:none;margin-top: 0;padding: 0;}
    /********************Table Responsive Start**************************/
    @media (max-width: 640px){
      table {border: 0;}
      table thead { display: none;}
      tr:nth-child(odd), tr:nth-child(even) {background: #ffffff;}
      table td {
        display: block;
        border-bottom: none;
        padding-left: 0;
        padding-right: 0;
      }
      table td:before {
        content: attr(data-label);
        float: left;
        width: 100%;
        font-weight: bold;
      }
    }
    table {
      width: 100%;
      max-width: 100%;
      border-collapse: collapse;
      margin-bottom: 20px;
      display: table;
      border-collapse: separate;
      border-color: grey;
    }
    thead {
      display: table-header-group;
      vertical-align: middle;
      border-color: inherit;
    }
    tr:nth-child(odd) {background: #F9F9F9;}
    tr {
      display: table-row;
      vertical-align: inherit;
      border-color: inherit;
    }
    th {padding-right: 1em;text-align: left;font-weight: bold;}
    td, th {display: table-cell;vertical-align: inherit;}
    tbody {
      display: table-row-group;
      vertical-align: middle;
      border-color: inherit;
    }
    td {
      padding-top: 6px;
      padding-bottom: 6px;
      padding-left: 10px;
      padding-right: 10px;
      vertical-align: top;
      border-bottom: none;
    }
    .table>thead>tr>th, .table>tbody>tr>th, .table>tfoot>tr>th, .table>thead>tr>td, .table>tbody>tr>td, .table>tfoot>tr>td{border-top: 1px solid #ddd;}
    /********************Table Responsive Ends**************************/
  </style>
</head>
<body class="pace-done widescreen fixed-left-void">
<div class="content-page">
  <div class="content">
    <div class="page-heading">
      <div class="row">
        <div class="col-sm-12 portlets ui-sortable">
          <div class="widget">
            <div class="widget-header transparent">
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=payoutreason_PayoutInformation%></strong></h2>
              <div class="additional-btn">
                <a href="#" <%--class="hidden reload"--%>></a>
                <a href="#" class="widget-toggle"><i class="icon-down-open-2"></i></a>
                <a href="#" class="widget-close"><i class="icon-cancel-3"></i></a>
              </div>
            </div>

            <%
              String res=(String)request.getAttribute("message");
              if(res!=null)
              {
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\">&nbsp;&nbsp;"+res+"</h5>");
              }
              String errorMsg=(String)request.getAttribute("error");
              if(errorMsg!=null){
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+errorMsg+"</h5>");
              }
            %>

            <div class="pull-right">
              <div class="btn-group">

                <form name="form" method="post" action="/merchant/servlet/PayoutList?ctoken=<%=ctoken%>">
                  <%
                    Enumeration<String> aName=request.getParameterNames();
                    while(aName.hasMoreElements())
                    {
                      String name=aName.nextElement();
                      String value = request.getParameter(name);
                      if(value==null || value.equals("null"))
                      {
                        value = "";
                      }
                      //System.out.println("Value for rever reason is "+value);
                  %>

                  <input type=hidden name=<%=name%> value=<%=value%>>
                  <%
                    }
                  %>
                  <%--<button class="btn-xs" type="submit" name="B2" style="background: white;border: 0;">
                    <img style="height: 35px;" src="/merchant/images/goBack.png">
                  </button>--%>
                  <button type="submit" name="submit" class="btn btn-default" style="display: -webkit-box;"><i class="fa fa-arrow-circle-left" aria-hidden="true"></i>&nbsp;&nbsp;<%=payoutreason_GoBack%></button>
                </form>
              </div>
            </div>

            <div class="widget-content padding" style="overflow-y: auto;">
              <div id="horizontal-form">
                <%
                  Hashtable hash = (Hashtable) request.getAttribute("payoutdetails");
                  Hashtable temphash = (Hashtable) hash.get("1");
                  if(temphash!=null)
                  {
                    String disable = "";
                    String accountId = (String) temphash.get("accountid");
                    String fromType = "";
                    fromType = (String) temphash.get("fromtype");
                   // System.out.println("fromtype -----------------------fromtype-------------------------"+fromType);
                    String gatewayType = GatewayAccountService.getGatewayAccount(accountId).getGateway();
                    String currency = (String) temphash.get("currency");;
                    if(currency==null)
                    {
                      currency="";
                    }
                    if (PayDollarPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayType)||EcorePaymentGateway.GATEWAY_TYPE.equals(gatewayType)|| CredoraxPaymentGateway.GATEWAY_TYPE.equals(gatewayType)){
                      disable ="readonly";
                    }
                    String status = (String) temphash.get("status");
                    if (status != null && status.equals("settled") || status.equals("capturesuccess"))
                    {
                %>

                <form action="/merchant/servlet/DoPayoutTransaction" method="post" name="form1">
                  <table class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                    <thead>
                    <tr style="color: white;">
                      <th style="text-align: center"><%=payoutreason_TrackingId%></th>
                      <th style="text-align: center"><%=payoutreason_TransactionId%></th>
                      <th style="text-align: center"><%=payoutreason_Description%></th>
                      <th style="text-align: center"><%=payoutreason_initial%> <%=currency%>)</th>
                      <th style="text-align: center"><%=payoutreason_Status%></th>
                      <%if (functions.isValueNull(fromType))
                      {
                        if (fromType.equalsIgnoreCase("bitcoinpg") || fromType.equalsIgnoreCase("bitclear") )
                        {
                        %>
                          <th style="text-align: center">Customer Bitcoin Address*</th>
                        <%
                        }
                      }
                      %>
                      <th style="text-align: center"><%=payoutreason_OrderId%></th>
                      <th style="text-align: center"><%=payoutreason_order%></th>
                      <th style="text-align: center"><%=payoutreason_payout%><%=currency%>)*</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                      <td data-label="Tracking ID" style="text-align: center">
                        <%=(String) temphash.get("icicitransid")%>
                        <input type="Hidden" name="icicitransid" value="<%=(String)temphash.get("icicitransid")%>">
                      </td>
                      <td data-label="Transaction ID" style="text-align: center">
                        <%=(String) temphash.get("transid")%>
                        <input type="Hidden" name="transid" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("transid"))%>">
                      </td>
                      <input type="hidden" name="accountid" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("accountid"))%>">
                      <input type="hidden" name="terminalid" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("terminalid"))%>">
                      <input type="hidden" value="<%=fromType%>" name="fromType">
                      <td data-label="Description" style="text-align: center">
                        <%=ESAPI.encoder().encodeForHTML((String) temphash.get("description"))%>
                      </td>
                      <td data-label="Captured Amount (in <%=currency%>)" style="text-align: center">
                        <%=ESAPI.encoder().encodeForHTML((String) temphash.get("captureamount"))%><input type="hidden" name="captureamount" value="<%= (temphash.get("captureamount"))%>">
                      </td>
                      <td data-label="Status" style="text-align: center">
                        <%=ESAPI.encoder().encodeForHTML(status)%>
                      </td>
                      <%if (functions.isValueNull(fromType))
                      {
                         if (fromType.equalsIgnoreCase("bitcoinpg") || fromType.equalsIgnoreCase("bitclear") )
                         {
                          %>
                          <td data-label="Customer Bitcoin Address*" style="text-align: center">
                            <input class="form-control" style="text-align: center" type="Text" value="" name="customerbitcoinaddress" size="60">
                          </td>
                          <%
                         }
                      }
                      %>
                      <td data-label="orderid*" style="text-align: center">
                        <input class="form-control" style="text-align: center" type="Text" value="" name="orderid">
                      </td>
                      <td data-label="Order Description" style="text-align: center">
                        <input class="form-control" style="text-align: center" type="Text" value="" name="orderdescription">
                      </td>
                      <td data-label="Payout Amount*" style="text-align: center">
                        <input class="form-control" style="text-align: center" type="Text" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("captureamount"))%>"
                               name="payoutamount" size="30" <%=disable%> >
                      </td>
                    </tr>
                    </tbody>
                  </table>

                  <br>
                  <%--==for zotapay (bank details)==============--%>
                  <%if (functions.isValueNull(fromType))
                  {
                    if (fromType.equalsIgnoreCase("zota"))
                    {
                  %>
                  <table class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                    <thead>
                    <tr style="color: white;">
                      <th style="text-align: center"><%=payoutreason_CustomerBank%></th>
                      <th style="text-align: center"><%=payoutreason_account1%></th>
                      <th style="text-align: center"><%=payoutreason_bank%></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>

                      <td data-label="Customer Bank Code*" style="text-align: center">
                        <input class="form-control" style="text-align: center" type="Text" value="" name="bankCode">
                      </td>
                      <td data-label="Customer Bank Account Number*" style="text-align: center">
                        <input class="form-control" style="text-align: center" type="Text" value="" name="bankAccountNumber">
                      </td>
                      <td data-label="Customer Bank Account Name*" style="text-align: center">
                        <input class="form-control" style="text-align: center" type="Text" value="" name="bankAccountName">
                      </td>
                    </tr>
                    </tbody>
                  </table>
                  <%
                      }
                    }
                  %>
                  <%--===  for  safexpay integeration --%>
                  <%if (functions.isValueNull(fromType))
                  {
                    if (fromType.equalsIgnoreCase("safexpay"))
                    {
                  %>
                  <table class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                    <thead>
                    <tr style="color: white;">
                      <th style="text-align: center"><%=payoutreason_Transfer_Type%></th>
                      <th style="text-align: center"><%=payoutreason_bank1%></th>
                      <th style="text-align: center"><%=payoutreason_Bank_Ifsc%></th>
                      <th style="text-align: center"> <%=payoutreason_account%></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>

                      <td data-label="Transfer_Type*" style="text-align: center">
                      <!--  <label for="cars">Choose a car:</label>-->
                        <select id="transferType" name="transferType">
                          <option value="NEFT"><%=payoutreason_NEFT%></option>
                          <option value="RTGS"><%=payoutreason_RTGS%></option>
                          <option value="IMPS"><%=payoutreason_IMPS%></option>
                          <option value="UPI"><%=payoutreason_UPI%></option>
                        </select>
                      </td>
                      <td data-label="Bank_Account_No*" style="text-align: center">
                        <input class="form-control" style="text-align: center" type="Text" value="" name="bankAccountNumber" maxlength="18" >
                      </td>
                      <td data-label="Bank_Ifsc*" style="text-align: center">
                        <input class="form-control" style="text-align: center" type="Text" value="" name="bankIfsc" maxlength="11">
                      </td>
                      <td data-label="Bank Account Name*" style="text-align: center">
                        <input class="form-control" style="text-align: center" type="Text" value="" name="bankAccountName" maxlength="30">
                      </td>
                    </tr>
                    </tbody>
                  </table>
                  <%
                      }
                    }
                  %>

                  <div class="form-group col-md-9">
                  </div>
                  <div class="form-group col-md-3">
                    <br><br>
                    <button type="submit" class="btn btn-default" name="B1" value="Capture">
                      <span><i class="fa fa-save"></i></span>
                      &nbsp;&nbsp;<%=payoutreason_Payout%>
                    </button>

                  </div>
                </form>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
    <%
}
else
{

 }
    }
    else
    {
        out.println(Functions.NewShowConfirmation1(payoutreason_Sorry, payoutreason_no));
    }
%>
</body>
</html>