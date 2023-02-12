<%@ page language="java" import="com.directi.pg.Functions,
                                 com.directi.pg.LoadProperties,
                                 com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.paymentgateway.CredoraxPaymentGateway" %>
<%@ page import="com.directi.pg.core.paymentgateway.EcorePaymentGateway" %>
<%@ page import="com.directi.pg.core.paymentgateway.PayDollarPaymentGateway" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.payment.ninja.NinjaWalletPaymentGateway" %>
<%@ page import="com.payment.quickcard.QuickCardPaymentGateway" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.ResourceBundle" %>
<%@ include file="ietest.jsp" %>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));
  ResourceBundle rb1 = null;
  String language_property1 = (String) session.getAttribute("language_property");
  rb1 = LoadProperties.getProperty(language_property1);
  String reversereason_Reversal_Information = StringUtils.isNotEmpty(rb1.getString("reversereason_Reversal_Information")) ? rb1.getString("reversereason_Reversal_Information") : "Reversal Information";
  String reversereason_TrackingID = StringUtils.isNotEmpty(rb1.getString("reversereason_TrackingID")) ? rb1.getString("reversereason_TrackingID") : "Tracking ID";
  String reversereason_TransactionID = StringUtils.isNotEmpty(rb1.getString("reversereason_TransactionID")) ? rb1.getString("reversereason_TransactionID") : "Transaction ID";
  String reversereason_Description = StringUtils.isNotEmpty(rb1.getString("reversereason_Description")) ? rb1.getString("reversereason_Description") : "Description";
  String reversereason_captured = StringUtils.isNotEmpty(rb1.getString("reversereason_captured")) ? rb1.getString("reversereason_captured") : "Captured Amount (in";
  String reversereason_Currency = StringUtils.isNotEmpty(rb1.getString("reversereason_Currency")) ? rb1.getString("reversereason_Currency") : "Currency";
  String reversereason_Status = StringUtils.isNotEmpty(rb1.getString("reversereason_Status")) ? rb1.getString("reversereason_Status") : "Status";
  String reversereason_Reason_reversal = StringUtils.isNotEmpty(rb1.getString("reversereason_Reason_reversal")) ? rb1.getString("reversereason_Reason_reversal") : "Reason for reversal*";
  String reversereason_Reason_amount = StringUtils.isNotEmpty(rb1.getString("reversereason_Reason_amount")) ? rb1.getString("reversereason_Reason_amount") : "Reversal Amount*";
  String reversereason_IsFraudTrans = StringUtils.isNotEmpty(rb1.getString("reversereason_IsFraudTrans")) ? rb1.getString("reversereason_IsFraudTrans") : "IsFraudTrans";

%>
<html>
<head>
  <%--<META HTTP-EQUIV="Pragma" CONTENT="no-cache">--%>
  <script language="javascript">
    function isint(form)
    {
      if (isNaN((form.numrows.value)))
        return false;
      else
        return true;
    }
    function formSubmit()
    {
      var form = document.form1;
      if (isNaN(form.refundamount.value) || parseFloat(form.refundamount.value) <= 0)
      {
        alert("Please Enter a Valid Refund Amount greater than 0");
        return false;
      }
      return true;
    }
    function cal()
    {
      var checkbox=document.getElementsByClassName("checkbox_id");
      var len=checkbox.length;
      var checked=0;
      var sumofamt=parseFloat(0);
      for (i = 1; i <= len; i++)
      {
        if (checkbox[i - 1].checked)
        {
          childamt = parseFloat(document.getElementById("childrefundamount" + i).value);
          if(!isNaN(childamt))
            sumofamt = sumofamt + childamt;
          checked++;
        }

      }
      /*if(checked==0)
      {
        for(i=1; i<=len; i++ )
        {
          childamt = parseFloat(document.getElementById("childrefundamount"+i).value);
          sumofamt=sumofamt+childamt;
          checked++;

        }
      }*/
      console.log("sumofamt------>",sumofamt)
      document.getElementById("refundamount").value=parseFloat(sumofamt).toFixed(2);
    }
  </script>
</head>
<body class="bodybackground">
<%@ include file="top.jsp" %>
<div class="content-page">
  <div class="content">
    <!-- Page Heading Start -->
    <div class="page-heading">
      <div class="pull-right">
        <div class="btn-group">
          <form name="form" method="post" action="/partner/net/PartnerMerchantRefundList?ctoken=<%=ctoken%>">
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
            %>
            <input type=hidden name=<%=name%> value=<%=value%>>
            <%
              }
            %>
            <button class="btn-xs" type="submit" name="B2" style="background: transparent;border: 0;">
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
              <h2><strong><i class="fa fa-th-large"></i>&nbsp;&nbsp;<%=reversereason_Reversal_Information%></strong></h2>
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
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+res+"</h5>");
              }
              String errorMsg=(String)request.getAttribute("error");
              if(errorMsg!=null)
              {
                out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+errorMsg+"</h5>");
              }
            %>
            <div class="widget-content padding" style="overflow-y: auto;">
              <div id="horizontal-form">
                <form action="/partner/net/DoPartnerMerchantReverseTransaction" method="post" name="form1">
                  <%
                    String currency = (String) session.getAttribute("currency");
                    Hashtable hash = (Hashtable) request.getAttribute("refunddetails");
                    Hashtable temphash = (Hashtable) hash.get("1");
                    Hashtable parenthash=(Hashtable)request.getAttribute("parentreversaldetails");
                    Hashtable tempparenthash=null;
                    Hashtable childhash=(Hashtable)request.getAttribute("childreversaldetails");
                    Hashtable tempchildhash=null;
                    String marketPlaceFlag="";
                    MerchantDetailsVO merchantDetailsVO=(MerchantDetailsVO)request.getAttribute("merchantDetailsVO");
                    if(merchantDetailsVO!=null)
                      marketPlaceFlag=merchantDetailsVO.getMarketPlace();
                    if(temphash!=null)
                    {
                      String disable = "";
                      String accountId = (String) temphash.get("accountid");
                      String gatewayType = GatewayAccountService.getGatewayAccount(accountId).getGateway();
                      currency = GatewayAccountService.getGatewayAccount(accountId).getCurrency();
                      if (PayDollarPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayType)|| EcorePaymentGateway.GATEWAY_TYPE.equals(gatewayType)|| CredoraxPaymentGateway.GATEWAY_TYPE.equals(gatewayType) || QuickCardPaymentGateway.GATEWAY_TYPE.equals(gatewayType) ||
                              NinjaWalletPaymentGateway.GATEWAY_TYPE.equals(gatewayType)){
                        disable ="readonly";
                      }
                      double amount = 0.00;
                      double parentamount = 0.00;
                      String capturedAmount = (String) temphash.get("captureamount");
                      String refundedAmount = (String) temphash.get("refundamount");
                      String parentTrackingid=temphash.get("parentTrackingid")==null? "":(String)temphash.get("parentTrackingid");
                      String parentcaptureAmount = "";
                      String parentrefundAmount = "";
                      if(temphash.get("parentTrackingid")!=null && parenthash!=null)
                      {
                        tempparenthash=(Hashtable)parenthash.get("1");
                        parentcaptureAmount=(String)tempparenthash.get("captureamount");
                        parentrefundAmount=(String)tempparenthash.get("refundamount");
                        if (!parentrefundAmount.equals("0.00"))
                        {
                          parentamount = Double.parseDouble(parentcaptureAmount) - Double.parseDouble(parentrefundAmount);
                        }
                        else
                        {
                          parentamount = Double.parseDouble(parentcaptureAmount);
                        }
                      }
                      if (!refundedAmount.equals("0.00"))
                      {
                        amount = Double.parseDouble(capturedAmount) - Double.parseDouble(refundedAmount);
                      }
                      else
                      {
                        amount = Double.parseDouble(capturedAmount);
                      }
                      String status = (String) temphash.get("status");
                      if (status != null && (status.equals("settled") || status.equals("capturesuccess") || status.equals("reversed")))
                      {
                        if(childhash != null && childhash.size()>0)
                        {
                  %>
                  <table class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                    <input type="hidden" value="<%=(String)temphash.get("toid")%>" name="toid">
                    <input type="hidden" name="marketPlaceFlag" value="<%=marketPlaceFlag%>">
                    <input type="hidden" name="childhashsize" value="<%=childhash.size()%>">
                    <thead>
                    <tr style="background-color: #7eccad !important;color: white;">
                      <%if(childhash!=null){%>
                      <th style="text-align: center"></th>
                      <%}%>
                      <th style="text-align: center"><%=reversereason_TrackingID%></th>
                      <th style="text-align: center"><%=reversereason_TransactionID%></th>
                      <th style="text-align: center"><%=reversereason_Description%></th>
                      <th style="text-align: center"><%=reversereason_captured%> <%=currency%>)</th>
                      <th style="text-align: center"><%=reversereason_Currency%></th>
                      <th style="text-align: center"><%=reversereason_Status%></th>
                      <th style="text-align: center"><%=reversereason_Reason_reversal%></th>
                      <th style="text-align: center"><%=reversereason_Reason_amount%></th>
                      <th style="text-align: center"><%=reversereason_IsFraudTrans%></th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                      <%if(childhash!=null){%>
                      <td data-label="Parent" style="text-align: center">
                      </td>
                      <%}%>
                      <td data-label="Tracking ID" style="text-align: center">
                        <%=(String) temphash.get("icicitransid")%>
                        <input type="Hidden" name="icicitransid" value="<%=(String)temphash.get("icicitransid")%>">
                      </td>
                      <td data-label="Transaction ID" style="text-align: center">
                        <%=(String) temphash.get("transid")%>
                        <input type="Hidden" name="transid" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("transid"))%>">
                      </td>
                      <input type="Hidden" name="accountid" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("accountid"))%>">
                      <td data-label="Description" style="text-align: center">
                        <%=ESAPI.encoder().encodeForHTML((String) temphash.get("description"))%>
                      </td>
                      <td data-label="Captured Amount (in <%=currency%>)" style="text-align: center">
                        <%=ESAPI.encoder().encodeForHTML((String) temphash.get("captureamount"))%>
                        <input type="hidden" name="captureamount" value="<%= (temphash.get("captureamount"))%>">
                      </td>
                      <td data-label="Child Currency" style="text-align: center">
                        <%=ESAPI.encoder().encodeForHTML((String) temphash.get("currency"))%>
                        <input type="hidden" name="currency" value="<%= (temphash.get("currency"))%>">
                        <%--<%=currency%>--%>
                      </td>
                      <td data-label="Status" style="text-align: center">
                        <%=ESAPI.encoder().encodeForHTML(status)%>
                      </td>
                      <td data-label="Reason for reversal*" style="text-align: center">
                        <input class="form-control" style="text-align: center" type="Text" maxlength="100"  value="" name="reason" size="30">
                      </td>
                      <td data-label="Reversal Amount*" style="text-align: center">
                        <input class="form-control" style="text-align: center" type="Text" maxlength="20"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(String.format("%.2f",amount))%>"  id="refundamount" name="refundamount" size="30" readonly>
                      </td>
                      <td data-label="Child IsFraudTrans" style="text-align: center">
                        <select class="form-control" name="isFraud" style="border: 1px solid #ddd;font-weight:bold; width: auto">
                          <option value="N" default="">N</option>
                          <option value="Y" default="">Y</option>
                        </select>
                      </td>
                    </tr>
                    </tbody>
                    <%
                      if(childhash!=null && !"N".equalsIgnoreCase(marketPlaceFlag)){
                        for(int i=1;i<=childhash.size();i++)
                        {
                          tempchildhash=(Hashtable)childhash.get(""+i);
                          capturedAmount = (String) tempchildhash.get("captureamount");
                          refundedAmount = (String) tempchildhash.get("refundamount");
                          double cCaptureAmount=Double.parseDouble(capturedAmount);
                          double cRefundedAmount=Double.parseDouble(refundedAmount);
                          if (!refundedAmount.equals("0.00"))
                          {
                            amount = Double.parseDouble(capturedAmount) - Double.parseDouble(refundedAmount);
                          }
                          else
                          {
                            amount = Double.parseDouble(capturedAmount);
                          }
                    %>
                    <tbody>
                    <script type="text/javascript">
                      $(function ()
                      {
                        var sumofamt=parseFloat(0);
                        $('#checkbox_id<%=i%>').on('ifUnchecked', function ()
                        {
                          sumofamt=parseFloat(document.getElementById("refundamount").value);
                          childamt = parseFloat(document.getElementById("childrefundamount"+<%=i%>).value);
                          console.log("childamt------->",childamt);
                          sumofamt=sumofamt-childamt;
                          console.log('sumofamt----->',sumofamt);
                          document.getElementById("refundamount").value=parseFloat(sumofamt).toFixed(2);
                        });
                        $('#checkbox_id<%=i%>').on('ifChecked', function ()
                        {
                          sumofamt=parseFloat(document.getElementById("refundamount").value);
                          childamt = parseFloat(document.getElementById("childrefundamount"+<%=i%>).value);
                          console.log("childamt------->",childamt);
                          sumofamt=sumofamt+childamt;
                          console.log('sumofamt----->',sumofamt);
                          document.getElementById("refundamount").value=parseFloat(sumofamt).toFixed(2);
                        })
                      });
                    </script>
                    <tr>
                      <td data-label="Parent" style="text-align: center">
                        <input type="checkbox" class="checkbox_id" name="checkbox_id" id="checkbox_id<%=i%>" value="<%=i%>" checked>
                      </td>
                      <td data-label="Tracking ID" style="text-align: center">
                        <%=(String) tempchildhash.get("icicitransid")%>
                        <input type="Hidden" name="childicicitransid<%=i%>" value="<%=(String)tempchildhash.get("icicitransid")%>">
                      </td>
                      <td data-label="Transaction ID" style="text-align: center">
                        <%=(String) tempchildhash.get("transid")%>
                        <input type="Hidden" name="childtransid<%=i%>" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)tempchildhash.get("transid"))%>">
                      </td>
                      <input type="Hidden" name="childaccountid<%=i%>" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)tempchildhash.get("accountid"))%>">
                      <td data-label="Description" style="text-align: center">
                        <%=ESAPI.encoder().encodeForHTML((String) tempchildhash.get("description"))%>
                      </td>
                      <td data-label="Captured Amount (in <%=currency%>)" style="text-align: center">
                        <%=ESAPI.encoder().encodeForHTML((String) tempchildhash.get("captureamount"))%><input type="hidden" name="childcaptureamount<%=i%>" value="<%= (tempchildhash.get("captureamount"))%>">
                      </td>
                      <td data-label="Currency" style="text-align: center">
                        <%=ESAPI.encoder().encodeForHTML((String) tempchildhash.get("currency"))%>
                        <input type="Hidden" name="childcurrency<%=i%>" value="<%=(String)tempchildhash.get("currency")%>">
                      </td>
                      <td data-label="Status" style="text-align: center">
                        <%=ESAPI.encoder().encodeForHTML((String) tempchildhash.get("status"))%>
                      </td>
                      <td data-label="Reason for reversal*" style="text-align: center">
                        <input class="form-control" style="text-align: center" type="Text" value="" name="childreason<%=i%>">
                      </td>
                      <td data-label="Reversal Amount*" style="text-align: center">
                        <input class="form-control" style="text-align: center" type="Text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(String.format("%.2f",amount))%>"
                              id="childrefundamount<%=i%>" name="childrefundamount<%=i%>" size="30" <%=disable%> onkeyup="return cal();" onselect="return cal();">
                      </td>
                      <td data-label="IsFraudTrans" style="text-align: center">
                        <select class="form-control" name="isFraud<%=i%>" style="border: 1px solid #ddd;font-weight:bold; width: auto">
                          <option value="N" default="">N</option>
                          <option value="Y" default="">Y</option>
                        </select>
                      </td>
                    </tr>
                    </tbody>
                    <%}}%>
                  </table>
                  <div class="form-group col-md-12">
                    <br><br>
                    <center>
                      <button type="submit" class="btn btn-default" value="Reverse" name="submit" onclick="return formSubmit()">
                        <span><i class="fa fa-save"></i></span>
                        &nbsp;&nbsp;Reverse
                      </button>
                    </center>
                  </div>
                  <%}else{
                    boolean isAllow=true;
                    if(temphash.get("parentTrackingid")!=null && parenthash!=null && "N".equalsIgnoreCase(marketPlaceFlag))
                    {
                      errorMsg="Refund functionality is not supported for Vendor. Please contact your Administrator.";
                      if(errorMsg!=null)
                      {
                        //out.println("<center><font face=\"arial\" color=\"#2C3E50\" size=\"2\"><b>"+errorMsg+"</b></font></center>");
                        out.println("<h5 class=\"bg-infoorange\" style=\"text-align: center;\"><i class=\"fa fa-exclamation-triangle\"></i>&nbsp;&nbsp;"+errorMsg+"</h5>");
                      }
                      isAllow=false;
                    }
                    else if (temphash.get("parentTrackingid")!=null && parenthash!=null &&!"N".equalsIgnoreCase(marketPlaceFlag))
                    {
                      tempparenthash = (Hashtable) parenthash.get("1");
                      parentcaptureAmount = (String) tempparenthash.get("captureamount");
                      parentrefundAmount = (String) tempparenthash.get("refundamount");
                      if (!parentrefundAmount.equals("0.00"))
                      {
                        parentamount = Double.parseDouble(parentcaptureAmount) - Double.parseDouble(parentrefundAmount);
                      }
                      else
                      {
                        parentamount = Double.parseDouble(parentcaptureAmount);
                      }
                      isAllow=true;
                    }
                    if(isAllow)
                    {
                  %>
                  <table class="table table-striped table-bordered" style="font-family:Open Sans;font-size: 13px;font-weight: 600;width: 100%;">
                    <input type="hidden" value="<%=ctoken%>" name="ctoken">
                    <input type="hidden" value="<%=(String)temphash.get("toid")%>" name="toid">
                    <input type="hidden" name="parentTrackingid" value="<%=parentTrackingid%>">
                    <input type="hidden" name="parentcaptureAmount" value="<%=parentcaptureAmount%>">
                    <input type="hidden" name="parentrefundAmount" value="<%=parentamount%>">
                    <input type="hidden" name="marketPlaceFlag" value="<%=marketPlaceFlag%>">
                    <thead>
                    <tr style="background-color: #7eccad !important;color: white;">
                      <th style="text-align: center">Tracking ID</th>
                      <th style="text-align: center">Transaction ID</th>
                      <th style="text-align: center">Description</th>
                      <th style="text-align: center">Captured Amount (in <%=currency%>)</th>
                      <th style="text-align: center">Currency</th>
                      <th style="text-align: center">Status</th>
                      <th style="text-align: center">Reason for reversal*</th>
                      <th style="text-align: center">Reversal Amount*</th>
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
                      <input type="Hidden" name="accountid" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("accountid"))%>">
                      <td data-label="Description" style="text-align: center">
                        <%=ESAPI.encoder().encodeForHTML((String) temphash.get("description"))%>
                      </td>
                      <td data-label="Captured Amount (in <%=currency%>)" style="text-align: center">
                        <%=ESAPI.encoder().encodeForHTML((String) temphash.get("captureamount"))%><input type="hidden" name="captureamount" value="<%= (temphash.get("captureamount"))%>">
                      </td>
                      <td data-label="Currency" style="text-align: center">
                        <%=ESAPI.encoder().encodeForHTML((String) temphash.get("currency"))%>
                        <input type="Hidden" name="currency" value="<%=(String)temphash.get("currency")%>">
                      </td>
                      <td data-label="Status" style="text-align: center">
                        <%=ESAPI.encoder().encodeForHTML(status)%>
                      </td>
                      <td data-label="Reason for reversal*" style="text-align: center">
                        <input class="form-control" style="text-align: center" type="Text" value="" name="reason">
                      </td>
                      <td data-label="Reversal Amount*" style="text-align: center">
                        <input class="form-control" style="text-align: center" type="Text" value="<%=ESAPI.encoder().encodeForHTMLAttribute(String.format("%.2f",amount))%>"
                               name="refundamount" size="30" <%=disable%> >
                      </td>
                    </tr>
                    </tbody>
                  </table>


                  <div class="form-group col-md-9">
                  </div>
                  <div class="form-group col-md-3">
                    <br><br>
                    <button type="submit" class="btn btn-default" name="B1" <%--style="margin-left:147%;"--%> value="Capture">
                      <span><i class="fa fa-save"></i></span>
                      &nbsp;&nbsp;Reverse
                    </button>
                  </div>
                  <%}}%>
                </form>
              </div>
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
%>
The transaction status is <%=ESAPI.encoder().encodeForHTML(status)%> instead of "settled"
<%
    }
  }
  else
  {
    out.println(Functions.NewShowConfirmation1("Sorry", "No records found. Invalid TransactionID<br><br>"));
  }
%>
</body>
</html>
