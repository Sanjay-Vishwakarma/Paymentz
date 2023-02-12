<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI "%>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.payment.safexpay.SafexPayPaymentGateway" %>
<%@ page import="com.payment.verve.VervePaymentGateway" %>
<%@ page import="com.payment.bhartiPay.BhartiPayPaymentGateway" %>
<%@ page import="com.payment.qikpay.QikpayPaymentGateway" %>
<%@ page import="com.payment.payneteasy.PayneteasyGateway" %>
<%@ page import="com.payment.easypaymentz.EasyPaymentzPaymentGateway" %>
<%@ page import="com.payment.LetzPay.LetzPayPaymentGateway" %>
<%@ page import="com.payment.imoneypay.IMoneyPayPaymentGateway" %>
<%@ page import="com.payment.asiancheckout.AsianCheckoutPaymentGateway" %>
<%@ page import="com.payment.cashfree.CashFreePaymentGateway" %>
<%@ page import="com.payment.payg.PayGPaymentGateway" %>
<%@ page import="com.payment.apexpay.ApexPayPaymentGateway" %>
<%@ page import="com.payment.qikpayv2.QikPayV2PaymentGateway" %>
<%@ page import="com.payment.payaidpayments.PayaidPaymentGateway" %>
<%@ page import="com.payment.tigerpay.TigerPayPaymentGateway" %>
<%@ page import="com.payment.onepay.OnePayPaymentGateway" %>
<%@ page import="com.payment.aamarpay.AamarPayPaymentGateway" %>
<%@ page import="com.payment.paytm.PayTMPaymentGateway" %>
<%@ page import="com.payment.paynetics.core.PayneticsGateway" %>
<%@ page import="com.payment.lyra.LyraPaymentGateway" %>
<%@ page import="com.payment.payu.PayUPaymentGateway" %>
<%@ page import="com.payment.bnmquick.BnmQuickPaymentGateway" %>
<%@ page import="com.payment.FlutterWave.FlutterWavePaymentGateway" %>
<%@ page import="com.manager.AdminModuleManager" %>
<%@ page import="com.payment.airpay.AirpayPaymentGateway" %>
<%@ page import="com.payment.omnipay.OmnipayPaymentGateway" %>
<%@ page import="com.payment.cardpay.CardPayPaymentGateway" %>
<%@ page import="com.payment.Gpay.GpayPaymentzGateway" %>
<%@ include file="functions.jsp"%>

<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Jinesh
  Date: 6/2/14
  Time: 4:55 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>
<script>
    var lablevalues = new Array();
    function ChangeFunction(Value , lable){
        var previous= document.getElementById("onchangedvalue").value;
        console.log(lablevalues.length);
        if(lablevalues.length == 0 & previous != ""){
            lablevalues.push(previous);
        }
        console.log("Value" + Value + "lable" + lable);
        var finalvalue  =lable+"="+Value;
        console.log("finalvalue" + finalvalue );
        if(lablevalues.lastIndexOf(finalvalue)<0)
        {
            lablevalues.push(finalvalue);
        }
        console.log(lablevalues);
        document.getElementById("onchangedvalue").value = lablevalues;
    }

        $(function () {
            $('#gateway1').on('change', function(request, response ) {
                $.ajax( {
                    url: "/icici/servlet/GetDetailsAPI",
                    dataType: "json",
                    data: {
                        ctoken: $('#ctoken').val(),
                        method:"paymodeList",
                        gateway: $('#gateway1').val(),
                        term: request.term
                    },
                    success: function( data ) {
                        $('#paymodeid').find('option').not(':first').remove();
                        $.each(data.aaData,function(i,data)
                        {
                            console.log(data.value);
                            var div_data="<option value="+data.value+">"+data.value + data.text +"</option>";
                            $(div_data).appendTo('#paymodeid');
                        });

                        $('#cardtypeid').find('option').not(':first').remove();
                        $.each(data.bbData,function(i,data)
                        {
                            console.log(data.value);
                            var div_data="<option value="+data.value+">"+data.value + data.text +"</option>";
                            $(div_data).appendTo('#cardtypeid');
                        });
                    }
                } );
                minLength: 0
            });

            $(document).ready(function(request, response ) {
                $.ajax( {
                    url: "/icici/servlet/GetDetailsAPI",
                    dataType: "json",
                    data: {
                        ctoken: $('#ctoken').val(),
                        method:"paymodeList",
                        gateway: $('#gateway1').val(),
                        term: request.term
                    },
                    success: function( data ) {
                        $('#paymodeid').find('option').not(':first').remove();
                        $.each(data.aaData,function(i,data)
                        {
                            console.log(data.value);
                            var div_data="<option value="+data.value+">"+data.value + data.text +"</option>";
                            $(div_data).appendTo('#paymodeid');
                        });

                        $('#cardtypeid').find('option').not(':first').remove();
                        $.each(data.bbData,function(i,data)
                        {
                            console.log(data.value);
                            var div_data="<option value="+data.value+">"+data.value + data.text +"</option>";
                            $(div_data).appendTo('#cardtypeid');
                        });
                    }
                } );
                minLength: 0
            });
        });

</script>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <%--<script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-account-member.js"></script>--%>


    <script type="text/javascript">
        $('#sandbox-container input').datepicker({

        });

      function  submitForm()
      {
          var enddate     =  $('#enddate').val();
          var enddateStr  = enddate.split("/");
          var enddateNew  = enddateStr[2]+"-"+enddateStr[1]+"-"+enddateStr[0];

          var currentDate = new Date();
          currentDate.setDate(currentDate.getDate() - 2);

          var date2 = new Date(enddateNew);

          if(date2.getTime() > currentDate.getTime()){
              alert("Please Select End Date Before 3 days");
              return false;
          }else{
            $('#authFailedCron').submit();
          }

      }

        function  submitForm1()
        {
            var enddate     =  $('#enddate').val();
            var enddateStr  = enddate.split("/");
            var enddateNew  = enddateStr[2]+"-"+enddateStr[1]+"-"+enddateStr[0];

            var currentDate = new Date();
            currentDate.setDate(currentDate.getDate() - 3);

            var date2 = new Date(enddateNew);

            if(date2.getTime() > currentDate.getTime()){
                alert("Please Select End Date Before 3 days");
                return false;
            }else{
                $('#payoutFailedCron').submit();
            }

        }

    </script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker();

        });
    </script>
    <title>Reports> Bank Transaction Report</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String adminid      = "";
        String gateway      = Functions.checkStringNull(request.getParameter("gateway"));
        String style        = "class=\"tr0\"";
        String accountid    = Functions.checkStringNull(request.getParameter("accountid"));
        String merchantid   = Functions.checkStringNull(request.getParameter("merchantid"));
        adminid             = (String)session.getAttribute("merchantid");
        String startDate    = request.getParameter("startdate")==null?"":request.getParameter("startdate");
        String startTime    = request.getParameter("starttime")==null?"00:00:00":request.getParameter("starttime");
        String endDate      = request.getParameter("enddate")==null?"":request.getParameter("enddate");
        String endTime      = request.getParameter("endtime")==null?"23:59:59":request.getParameter("endtime");

        String currency     = "";
        Functions functions = new Functions();
        AdminModuleManager adminModuleManager = new AdminModuleManager();

        if(functions.isValueNull(request.getParameter("gateway"))){
            String aGatewaySet[] = request.getParameter("gateway").split("-");
            if (aGatewaySet.length == 3){
                currency = aGatewaySet[1];
            }
        }

        Hashtable innerhash     = new Hashtable();
        Hashtable innerhash2    = new Hashtable();
        String memberid         = Functions.checkStringNull(request.getParameter("merchantid")) == null ? "" : request.getParameter("merchantid");
        String pgtypeid         = Functions.checkStringNull(request.getParameter("gateway")) == null ? "" : request.getParameter("gateway");
        String paymodeid        = Functions.checkStringNull(request.getParameter("paymodeid")) == null ? "" : request.getParameter("paymodeid");
        String cardtypeid       = Functions.checkStringNull(request.getParameter("cardtypeid")) == null ? "" : request.getParameter("cardtypeid");
        String onchangedvalue   = Functions.checkStringNull(request.getParameter("onchangedvalue")) == null ? "" : request.getParameter("onchangedvalue");
        String terminalId       = Functions.checkStringNull(request.getParameter("terminalid")==null ? "" : request.getParameter("terminalid"));
        String partnerName      = request.getParameter("partnerName")==null ? "" : request.getParameter("partnerName");

        String gatewayName          = request.getAttribute("gatewayName")==null ? "" : (String) request.getAttribute("gatewayName");
        String InquirySupported     = request.getAttribute("InquirySupported")==null ? "" : (String) request.getAttribute("InquirySupported");
        String ReversalSupported    = request.getAttribute("ReversalSupported")==null ? "" : (String) request.getAttribute("ReversalSupported");

        TerminalManager terminalManager     = new TerminalManager();
        List<TerminalVO> terminalList       = terminalManager.getAllMappedTerminals();

        Hashtable dataHash      = (Hashtable) request.getAttribute("bankdetails");
        Hashtable refundHash    = (Hashtable) request.getAttribute("bankrefunddetails");
        Hashtable payoutHash = (Hashtable) request.getAttribute("bankpayoutdetails");

        String errorMsg         = (String) request.getAttribute("errormsg");

        String timeDifference = (String) request.getAttribute("timediff");
        if(timeDifference == null || timeDifference.equalsIgnoreCase("null"))
        {
            timeDifference  = "";
        }
        if (gateway == null)
        {
            gateway = "";
        }
        if (accountid == null)
        {
            accountid = "";
        }
        if(merchantid == null)
        {
            merchantid = "";
        }
        if(terminalId == null)
        {
            terminalId = "";
        }

        TreeMap<String,TerminalVO> memberMap            = new TreeMap<String, TerminalVO>();
        TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();
        TreeMap<String,GatewayType> gatewayTypeTreeMap  = GatewayTypeService.getAllGatewayTypesMap();
        for(TerminalVO terminalVO : terminalList)
        {
            String memberKey = terminalVO.getMemberId()+"-"+terminalVO.getAccountId()+"-"+terminalVO.getGateway().toUpperCase() + "-" + terminalVO.getCurrency() + "-" + terminalVO.getGateway_id();
            memberMap.put(memberKey,terminalVO);
        }
%>

<form action="/icici/servlet/BankTransactionReport?ctoken=<%=ctoken%>" method="post" name="forms" >
    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
    <input type="hidden" id="onchangedvalue" name="onchangedvalue" value="<%=onchangedvalue%>">

    <div class="row" style="margin-left: 210px;">
        <div class="col-lg-12">
            <div class="panel panel-default" style="margin-left:0px">
                <div class="panel-heading" >
                    Bank Transaction Report
                </div>
                <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                <table align=center>
                    <%
                        if(errorMsg != null)
                        {%>

                    <tr>
                        <td colspan="10" align="center" class="textb"><%=errorMsg%></td>

                    </tr>

                    <%}
                    %>
                </table>

                <table  align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:0%;">
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" >Start Date *</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input type="text" readonly class="datepicker" name="startdate" style="width:142px;height: 25px"  onchange="ChangeFunction(this.value,'Start Date')" value="<%=request.getParameter("startdate")==null?"":request.getParameter("startdate")%>">

                                    </td>
                                    <td width="1%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Time(HH:MM:SS)</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">

                                        <input type="text" name="starttime"  onchange="ChangeFunction(this.value,'Start Time')" value="<%=request.getParameter("starttime")==null?"00:00:00":request.getParameter("starttime")%>" class="txtbox">

                                    </td>
                                    <%
                                        String time = (gateway.equals("")||gateway.equals("null")) ? "00:00:00" : timeDifference;
                                    %>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" >Time Difference</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <input type="text" class="txtbox" onchange="ChangeFunction(this.value,'Time Difference')" disabled value=<%=time%>   >

                                    </td>
                                    </td>

                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >End Date *</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input type="text" readonly class="datepicker" name="enddate" onchange="ChangeFunction(this.value,'End Date')" style="width:142px;height: 25px"value="<%=request.getParameter("enddate")==null?"":request.getParameter("enddate")%>">

                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Time(HH:MM:SS)</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input type="text"  name="endtime" onchange="ChangeFunction(this.value,'End Time');showButton(this.value)" value="<%=request.getParameter("endtime")==null?"23:59:59":request.getParameter("endtime")%>" class="txtbox">

                                    </td>
                                    <td width="6%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Day Light Saving</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <select name="daylightsaving" class="txtbox" onchange="ChangeFunction(this.value,'Day Light Saving')">
                                            <option value="y">Yes</option>
                                            <option value="n" selected="selected">No</option>
                                        </select>
                                    </td>
                                    </td>

                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Gateway *</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input name="gateway" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on" onchange="ChangeFunction(this.value,'Gateway')">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>

                                    <td width="8%" class="textb">Payment Mode</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                       <%-- <input name="paymodeid" id="paymodeid" value="<%=paymodeid%>" class="txtbox" autocomplete="on" onchange="ChangeFunction(this.value,'Payment Mode')">--%>
                                        <select size="1" id="paymodeid" name="paymodeid" class="txtboxsmall" style="width: 140px;height: 23px;" onchange="ChangeFunction(this.value,'Payment Mode')">
                                                <option value="0" default>Select payment mode</option>
                                        </select>
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Payment Brand</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <%--<input name="cardtypeid" id="" value="<%=cardtypeid%>" class="txtbox" autocomplete="" onchange="ChangeFunction(this.value,'CardType ID')">--%>
                                        <select size="1" id="cardtypeid" name="cardtypeid" class="txtboxsmall" style="width: 140px;height: 23px;" onchange="ChangeFunction(this.value,'CardType ID')">
                                            <option value="0" default>Select Payment Brand</option>
                                        </select>
                                    </td>

                                </tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                </tr>

                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Partner ID </td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input name="partnerName" id="pid1" value="<%=partnerName%>" class="txtbox" autocomplete="on" onchange="ChangeFunction(this.value,'Partner ID')">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Account ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb" >
                                        <input name="accountid" id="accountid1" onchange="ChangeFunction(this.value,'Account ID')" value="<%=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid")%>" class="txtbox" autocomplete="on">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td colspan="2" class="textb">Member ID</td>
                                    <td colspan="2" class="textb" >
                                        <input name="merchantid" id="memberid1" value="<%=memberid%>" onchange="ChangeFunction(this.value,'Member ID')" class="txtbox" autocomplete="on">
                                    </td>
                                    </td>

                                </tr>

                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                </tr>

                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Terminal ID</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input name="terminalid" id="tid3" value="<%=terminalId%>" onchange="ChangeFunction(this.value,'Terminal ID')" class="txtbox" autocomplete="on">
                                    </td>

                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <button type="submit" class="buttonform" name="button" value="search" id="submit_button" >
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                </tr>

                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</form>

<div class="reporttable">
    <%
        String status = (String) request.getAttribute("status");
        boolean isMappingAvaliable=adminModuleManager.isCronMappingAvailable(adminid);

        if(status==null)
        {
            status ="0";
    %>
    <%
        if(dataHash != null && innerhash != null)
        {
            int total=Integer.parseInt((String)request.getAttribute("total"));
    %>

    <form name="exportform" method="post" action="/icici/servlet/ExportExcelForBankTransactions?ctoken=<%=ctoken%>" >
        <input type="hidden" value="<%=startDate%>" name="startDate">
        <input type="hidden" value="<%=endDate%>" name="endDate">
        <input type="hidden" value="<%=startTime%>" name="startTime">
        <input type="hidden" value="<%=endTime%>" name="endTime">
        <input type="hidden" value="<%=gateway%>" name="gateway">
        <input type="hidden" value="<%=partnerName%>" name="partnerName">
        <input type="hidden" value="<%=accountid%>" name="accountId">
        <input type="hidden" value="<%=memberid%>" name="memberId">
        <input type="hidden" value="<%=terminalId%>" name="terminalId">
        <button type="submit" class="button3" style="width:15%;margin-left:85% ;margin-top:0px"><b>Export to excel</b>&nbsp;&nbsp;&nbsp;<img width="20%" height="100%" border="0" src="/merchant/images/excel.png"></button>
    </form>

    <table border="0" cellpadding="3" cellspacing="3" bordercolor="#2379A5" width="50%"  align="center" class="table table-striped table-bordered table-green dataTable"  >
        <tr>
            <td  class="textb" colspan="6"><center><b>Bank Transaction Report(Deposit)</b></center></td>
        </tr>
        <tr <%=style%>>
            <td  class="th0" >Status</td>
            <td  class="th0" >No. Of Transaction</td>
            <td  class="th0" >Amount</td>
            <td  class="th0" >Capture Amount</td>
            <td  class="th0" >Percentage(%)</td>
            <%
                if(isMappingAvaliable){
            %>
            <td  class="th0">Action</td>
            <%}%>
        </tr>

        <%
            for(int pos = 1; pos <= dataHash.size(); pos++)
            {
                innerhash = (Hashtable) dataHash.get(pos + "");
                if (pos % 2 == 0)
                {
                    style = "class=tr0";

                }
                else
                {
                    style = "class=tr1";

                }
                String status1 = (String) innerhash.get("STATUS");
                System.out.println("status----"+status1);

                    out.println("<tr  " + style + ">");

                    out.println("<td align=\"center\" >" + innerhash.get("STATUS") + " </td>");
                    out.println("<td align=\"center\" >" + innerhash.get("COUNT(*)") + " </td>");
                    out.println("<td align=\"center\" >" + innerhash.get("amount") + " </td>");
                    out.println("<td align=\"center\" >" + innerhash.get("captureamount") + " </td>");
                    out.println("<td align=\"center\" >" + Functions.round(Double.parseDouble((String) innerhash.get("COUNT(*)")) / total * 100, 2) + "%</td>");

                if(isMappingAvaliable)
                {
                    if (status1.equalsIgnoreCase("capturesuccess"))
                    {
                        out.println("<td align=\"center\" " + style + ">" +
                                "<form name =\"update\" action=\"/icici/servlet/BankTransactionReport?ctoken=" + ctoken + "\" method=\"post\">" +
                                "<input type=\"hidden\" name=\"startdate\" value=\"" + startDate + "\">" +
                                "<input type=\"hidden\" name=\"starttime\" value=\"" + startTime + "\">" +
                                "<input type=\"hidden\" name=\"enddate\" value=\"" + endDate + "\">" +
                                "<input type=\"hidden\" name=\"endtime\" value=\"" + endTime + "\">" +
                                "<input type=\"hidden\" name=\"gateway\" value=\"" + pgtypeid + "\">" +
                                "<input type=\"hidden\" name=\"merchantid\" value=\"" + merchantid + "\">" +
                                "<input type=\"hidden\" name=\"accountid\" value=\"" + accountid + "\">" +
                                "<input type=\"hidden\" name=\"terminalid\" value=\"" + terminalId + "\">" +
                                "<input type=\"hidden\" name=\"partnerName\" value=\"" + partnerName + "\">" +
                                "<input type=\"hidden\" name=\"button1\" value=\"settledAll\">" +
                                "<input type=\"hidden\" name=\"action\" value=\"settledAll\">" +
                                "<input type=\"submit\" class=\"goto\" value=\"Settle Transactions\"></form></td>");
                    }
                    else if (status1.equalsIgnoreCase("settled"))
                    {
                        out.println("<td align=\"center\" " + style + ">" +
                                "<form name =\"updateSettled\" action=\"/icici/servlet/BankTransactionReport?ctoken=" + ctoken + "\" method=\"post\">" +
                                "<input type=\"hidden\" name=\"startdate\" value=\"" + startDate + "\">" +
                                "<input type=\"hidden\" name=\"starttime\" value=\"" + startTime + "\">" +
                                "<input type=\"hidden\" name=\"enddate\" value=\"" + endDate + "\">" +
                                "<input type=\"hidden\" name=\"endtime\" value=\"" + endTime + "\">" +
                                "<input type=\"hidden\" name=\"gateway\" value=\"" + pgtypeid + "\">" +
                                "<input type=\"hidden\" name=\"merchantid\" value=\"" + merchantid + "\">" +
                                "<input type=\"hidden\" name=\"accountid\" value=\"" + accountid + "\">" +
                                "<input type=\"hidden\" name=\"terminalid\" value=\"" + terminalId + "\">" +
                                "<input type=\"hidden\" name=\"partnerName\" value=\"" + partnerName + "\">" +
                                "<input type=\"hidden\" name=\"button2\" value=\"captureAll\">" +
                                "<input type=\"hidden\" name=\"action\" value=\"captureAll\">" +
                                "<input type=\"submit\" class=\"goto\" value=\"Capture Transactions\"></form></td>");
                    }
                    else if (status1.equalsIgnoreCase("authstarted") && InquirySupported.equals("supported"))
                    {
                        out.println("<td align=\"center\" " + style + ">" +
                                "<form name =\"updateSettled\" action=\"/icici/servlet/BankTransactionInquiry?ctoken=" + ctoken + "\" method=\"post\">" +
                                "<input type=\"hidden\" name=\"startdate\" value=\"" + startDate + "\">" +
                                "<input type=\"hidden\" name=\"starttime\" value=\"" + startTime + "\">" +
                                "<input type=\"hidden\" name=\"enddate\" value=\"" + endDate + "\">" +
                                "<input type=\"hidden\" name=\"endtime\" value=\"" + endTime + "\">" +
                                "<input type=\"hidden\" name=\"gateway\" value=\"" + pgtypeid + "\">" +
                                "<input type=\"hidden\" name=\"merchantid\" value=\"" + merchantid + "\">" +
                                "<input type=\"hidden\" name=\"accountid\" value=\"" + accountid + "\">" +
                                "<input type=\"hidden\" name=\"terminalid\" value=\"" + terminalId + "\">" +
                                "<input type=\"hidden\" name=\"partnerName\" value=\"" + partnerName + "\">" +
                                "<input type=\"hidden\" name=\"currency\" value=\"" + currency + "\">" +
                                "<input type=\"hidden\" name=\"status\" value=\"" + status1 + "\">" +
                                "<input type=\"hidden\" name=\"onchangedvalue\" value=\"" + onchangedvalue + "\" >" +
                                "<input type=\"hidden\" name=\"countbefore\" value=\"" + innerhash.get("COUNT(*)") + "\" >" +
                                "<input type=\"hidden\" name=\"ActivityAction\" value=\"AuthStarted Inquiry Cron\">" +
//                            "<input type=\"hidden\" name=\"button2\" value=\"captureAll\">" +
//                            "<input type=\"hidden\" name=\"action\" value=\"captureAll\">" +
                                "<input type=\"submit\" class=\"goto\" value=\"AuthStarted Inquiry Cron\"></form>");

                        out.println("<form id=\"notFoundFailedCron\" name =\"notFoundFailedCron\" action=\"/icici/servlet/BankTransactionInquiry?ctoken=" + ctoken + "\" method=\"post\">" +
                                "<input type=\"hidden\" name=\"startdate\" value=\"" + startDate + "\">" +
                                "<input type=\"hidden\" name=\"starttime\" value=\"" + startTime + "\">" +
                                "<input type=\"hidden\" id=\"enddate\" name=\"enddate\" value=\"" + endDate + "\">" +
                                "<input type=\"hidden\" name=\"endtime\" value=\"" + endTime + "\">" +
                                "<input type=\"hidden\" name=\"gateway\" value=\"" + pgtypeid + "\">" +
                                "<input type=\"hidden\" name=\"merchantid\" value=\"" + merchantid + "\">" +
                                "<input type=\"hidden\" name=\"accountid\" value=\"" + accountid + "\">" +
                                "<input type=\"hidden\" name=\"terminalid\" value=\"" + terminalId + "\">" +
                                "<input type=\"hidden\" name=\"partnerName\" value=\"" + partnerName + "\">" +
                                "<input type=\"hidden\" name=\"currency\" value=\"" + currency + "\">" +
                                "<input type=\"hidden\" name=\"status\" value=\"" + status1 + "\">" +
                                "<input type=\"hidden\" name=\"onchangedvalue\" value=\"" + onchangedvalue + "\">" +
                                "<input type=\"hidden\" name=\"countbefore\" value=\"" + innerhash.get("COUNT(*)") + "\" >" +
                                "<input type=\"hidden\" name=\"ActivityAction\" value=\"NotFoundFailedCron\">" +
                                "<input type=\"submit\" style=\"background: #c1c1c1;color: #000;text-shadow: none;\" class=\"goto\" value=\"BankTransactionsNotFoundInquiryCron\"></form>" +
                                "</td>");

                    }
                else if (status1.equalsIgnoreCase("authfailed") && (GpayPaymentzGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || CardPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || PayUPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || OmnipayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) ||AirpayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||CashFreePaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||FlutterWavePaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)|| LyraPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||PayneticsGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)|| PayTMPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||AamarPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||OnePayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||PayaidPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)|| QikPayV2PaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || ApexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || PayGPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || IMoneyPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || LetzPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||EasyPaymentzPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||QikpayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||BhartiPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||VervePaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||SafexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || PayneteasyGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)))
                    {
                        out.println("<td align=\"center\" " + style + ">");
                        out.println("<form name =\"updateSettled\" action=\"/icici/servlet/BankTransactionInquiry?ctoken=" + ctoken + "\" method=\"post\">" +
                                "<input type=\"hidden\" name=\"startdate\" value=\"" + startDate + "\">" +
                                "<input type=\"hidden\" name=\"starttime\" value=\"" + startTime + "\">" +
                                "<input type=\"hidden\" name=\"enddate\" value=\"" + endDate + "\">" +
                                "<input type=\"hidden\" name=\"endtime\" value=\"" + endTime + "\">" +
                                "<input type=\"hidden\" name=\"gateway\" value=\"" + pgtypeid + "\">" +
                                "<input type=\"hidden\" name=\"merchantid\" value=\"" + merchantid + "\">" +
                                "<input type=\"hidden\" name=\"accountid\" value=\"" + accountid + "\">" +
                                "<input type=\"hidden\" name=\"terminalid\" value=\"" + terminalId + "\">" +
                                "<input type=\"hidden\" name=\"partnerName\" value=\"" + partnerName + "\">" +
                                "<input type=\"hidden\" name=\"currency\" value=\"" + currency + "\">" +
                                "<input type=\"hidden\" name=\"status\" value=\"" + status1 + "\">" +
                                "<input type=\"hidden\" name=\"onchangedvalue\" value=\"" + onchangedvalue + "\">" +
                                "<input type=\"hidden\" name=\"countbefore\" value=\"" + innerhash.get("COUNT(*)") + "\" >" +
                                "<input type=\"hidden\" name=\"ActivityAction\" value=\"AuthFailed Inquiry Cron\">" +
//                            "<input type=\"hidden\" name=\"button2\" value=\"captureAll\">" +
//                            "<input type=\"hidden\" name=\"action\" value=\"captureAll\">" +
                                "<input type=\"submit\" class=\"goto\" value=\"AuthFailed Inquiry Cron\"></form>");

                        out.println("<form id=\"authFailedCron\" name =\"authFailedCron\" action=\"/icici/servlet/BankTransactionInquiry?ctoken=" + ctoken + "\" method=\"post\">" +
                                "<input type=\"hidden\" name=\"startdate\" value=\"" + startDate + "\">" +
                                "<input type=\"hidden\" name=\"starttime\" value=\"" + startTime + "\">" +
                                "<input type=\"hidden\" id=\"enddate\" name=\"enddate\" value=\"" + endDate + "\">" +
                                "<input type=\"hidden\" name=\"endtime\" value=\"" + endTime + "\">" +
                                "<input type=\"hidden\" name=\"gateway\" value=\"" + pgtypeid + "\">" +
                                "<input type=\"hidden\" name=\"merchantid\" value=\"" + merchantid + "\">" +
                                "<input type=\"hidden\" name=\"accountid\" value=\"" + accountid + "\">" +
                                "<input type=\"hidden\" name=\"terminalid\" value=\"" + terminalId + "\">" +
                                "<input type=\"hidden\" name=\"partnerName\" value=\"" + partnerName + "\">" +
                                "<input type=\"hidden\" name=\"currency\" value=\"" + currency + "\">" +
                                "<input type=\"hidden\" name=\"status\" value=\"" + status1 + "\">" +
                                "<input type=\"hidden\" name=\"onchangedvalue\" value=\"" + onchangedvalue + "\">" +
                                "<input type=\"hidden\" name=\"countbefore\" value=\"" + innerhash.get("COUNT(*)") + "\" >" +
                                "<input type=\"hidden\" name=\"ActivityAction\" value=\"AuthFailedCron\">" +
                                "<input type=\"button\" style=\"background: #c1c1c1;color: #000;text-shadow: none;\" onClick=\"submitForm()\" class=\"goto\" value=\"AuthFailed Transactions \"></form></td>");

                } else if (status1.equalsIgnoreCase("payoutfailed") && (LetzPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) ||PayGPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||LetzPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||IMoneyPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||EasyPaymentzPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||QikpayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||BhartiPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||VervePaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||SafexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)))
                    {
                    out.println("<td align=\"center\" " + style + ">" );
                    out.println("<form name =\"updateSettled\" action=\"/icici/servlet/BankTransactionInquiry?ctoken="+ctoken+"\" method=\"post\">"+
                            "<input type=\"hidden\" name=\"startdate\" value=\""+startDate+"\">"+
                            "<input type=\"hidden\" name=\"starttime\" value=\""+startTime+"\">"+
                            "<input type=\"hidden\" name=\"enddate\" value=\""+endDate+"\">"+
                            "<input type=\"hidden\" name=\"endtime\" value=\""+endTime+"\">"+
                            "<input type=\"hidden\" name=\"gateway\" value=\""+pgtypeid+"\">"+
                            "<input type=\"hidden\" name=\"merchantid\" value=\""+merchantid+"\">"+
                            "<input type=\"hidden\" name=\"accountid\" value=\""+accountid+"\">"+
                            "<input type=\"hidden\" name=\"terminalid\" value=\""+terminalId+"\">"+
                            "<input type=\"hidden\" name=\"partnerName\" value=\""+partnerName+"\">"+
                            "<input type=\"hidden\" name=\"currency\" value=\""+currency+"\">"+
                            "<input type=\"hidden\" name=\"status\" value=\""+status1+"\">"+
                            "<input type=\"hidden\" name=\"onchangedvalue\" value=\""+onchangedvalue+"\">"+
                            "<input type=\"hidden\" name=\"countbefore\" value=\""+innerhash.get("COUNT(*)")+"\" >"+
                            "<input type=\"hidden\" name=\"ActivityAction\" value=\"Payoutfailed Inquiry Cron\">"+

//                            "<input type=\"hidden\" name=\"button2\" value=\"captureAll\">" +
//                            "<input type=\"hidden\" name=\"action\" value=\"captureAll\">" +
                                "<input type=\"submit\" class=\"goto\" value=\"Payoutfailed Inquiry Cron\"></form>");

                        out.println("<form id=\"payoutFailedCron\" name =\"payoutFailedCron\" action=\"/icici/servlet/BankTransactionInquiry?ctoken=" + ctoken + "\" method=\"post\">" +
                                "<input type=\"hidden\" name=\"startdate\" value=\"" + startDate + "\">" +
                                "<input type=\"hidden\" name=\"starttime\" value=\"" + startTime + "\">" +
                                "<input type=\"hidden\" id=\"enddate\" name=\"enddate\" value=\"" + endDate + "\">" +
                                "<input type=\"hidden\" name=\"endtime\" value=\"" + endTime + "\">" +
                                "<input type=\"hidden\" name=\"gateway\" value=\"" + pgtypeid + "\">" +
                                "<input type=\"hidden\" name=\"merchantid\" value=\"" + merchantid + "\">" +
                                "<input type=\"hidden\" name=\"accountid\" value=\"" + accountid + "\">" +
                                "<input type=\"hidden\" name=\"terminalid\" value=\"" + terminalId + "\">" +
                                "<input type=\"hidden\" name=\"partnerName\" value=\"" + partnerName + "\">" +
                                "<input type=\"hidden\" name=\"currency\" value=\"" + currency + "\">" +
                                "<input type=\"hidden\" name=\"onchangedvalue\" value=\"" + onchangedvalue + "\">" +
                                "<input type=\"hidden\" name=\"countbefore\" value=\"" + innerhash.get("COUNT(*)") + "\" >" +
                                "<input type=\"hidden\" name=\"ActivityAction\" value=\"PayoutFailedCron\">" +
                                "<input type=\"button\" style=\"background: #c1c1c1;color: #000;text-shadow: none;\" onClick=\"submitForm1()\" class=\"goto\" value=\"PayoutFailed Transaction \"></form></td>");


                } else if (status1.equalsIgnoreCase("payoutstarted") && (LetzPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || TigerPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || QikPayV2PaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || ApexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || PayGPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || IMoneyPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||EasyPaymentzPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) ||QikpayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||BhartiPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||VervePaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||SafexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)))
                    {
                        out.println("<td align=\"center\" " + style + ">" +
                            "<form name =\"updateSettled\" action=\"/icici/servlet/BankTransactionInquiry?ctoken="+ctoken+"\" method=\"post\">"+
                            "<input type=\"hidden\" name=\"startdate\" value=\""+startDate+"\">"+
                            "<input type=\"hidden\" name=\"starttime\" value=\""+startTime+"\">"+
                            "<input type=\"hidden\" name=\"enddate\" value=\""+endDate+"\">"+
                            "<input type=\"hidden\" name=\"endtime\" value=\""+endTime+"\">"+
                            "<input type=\"hidden\" name=\"gateway\" value=\""+pgtypeid+"\">"+
                            "<input type=\"hidden\" name=\"merchantid\" value=\""+merchantid+"\">"+
                            "<input type=\"hidden\" name=\"accountid\" value=\""+accountid+"\">"+
                            "<input type=\"hidden\" name=\"terminalid\" value=\""+terminalId+"\">"+
                            "<input type=\"hidden\" name=\"partnerName\" value=\""+partnerName+"\">"+
                            "<input type=\"hidden\" name=\"currency\" value=\""+currency+"\">"+
                            "<input type=\"hidden\" name=\"status\" value=\""+status1+"\">"+
                            "<input type=\"hidden\" name=\"onchangedvalue\" value=\""+onchangedvalue+"\">"+
                            "<input type=\"hidden\" name=\"countbefore\" value=\""+innerhash.get("COUNT(*)")+"\" >"+
                            "<input type=\"hidden\" name=\"ActivityAction\" value=\"Payoutstarted Inquiry Cron\">"+
//                            "<input type=\"hidden\" name=\"button2\" value=\"captureAll\">" +
//                            "<input type=\"hidden\" name=\"action\" value=\"captureAll\">" +
                                "<input type=\"submit\" class=\"goto\" value=\"Payoutstarted Inquiry Cron\"></form></td>");

                }else if (status1.equalsIgnoreCase("payoutsuccessful") && (TigerPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || QikPayV2PaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || ApexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || PayGPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || IMoneyPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||IMoneyPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)|| QikpayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || BhartiPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||VervePaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||SafexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)))
                    {
                        out.println("<td align=\"center\" " + style + ">" +
                            "<form name =\"updateSettled\" action=\"/icici/servlet/BankTransactionInquiry?ctoken="+ctoken+"\" method=\"post\">"+
                            "<input type=\"hidden\" name=\"startdate\" value=\""+startDate+"\">"+
                            "<input type=\"hidden\" name=\"starttime\" value=\""+startTime+"\">"+
                            "<input type=\"hidden\" name=\"enddate\" value=\""+endDate+"\">"+
                            "<input type=\"hidden\" name=\"endtime\" value=\""+endTime+"\">"+
                            "<input type=\"hidden\" name=\"gateway\" value=\""+pgtypeid+"\">"+
                            "<input type=\"hidden\" name=\"merchantid\" value=\""+merchantid+"\">"+
                            "<input type=\"hidden\" name=\"accountid\" value=\""+accountid+"\">"+
                            "<input type=\"hidden\" name=\"terminalid\" value=\""+terminalId+"\">"+
                            "<input type=\"hidden\" name=\"partnerName\" value=\""+partnerName+"\">"+
                            "<input type=\"hidden\" name=\"currency\" value=\""+currency+"\">"+
                            "<input type=\"hidden\" name=\"status\" value=\""+status1+"\">"+
                            "<input type=\"hidden\" name=\"onchangedvalue\" value=\""+onchangedvalue+"\">"+
                            "<input type=\"hidden\" name=\"countbefore\" value=\""+innerhash.get("COUNT(*)")+"\" >"+
                            "<input type=\"hidden\" name=\"ActivityAction\" value=\"Payoutstarted Inquiry Cron\">"+
//                            "<input type=\"hidden\" name=\"button2\" value=\"captureAll\">" +
//                            "<input type=\"hidden\" name=\"action\" value=\"captureAll\">" +
                                "<input type=\"submit\" class=\"goto\" value=\"payoutsuccessful Inquiry Cron\"></form></td>");

                    }
                    else if (status1.equalsIgnoreCase("markedforreversal") && ReversalSupported.equals("supported"))
                    {
                        out.println("<td align=\"center\" " + style + ">" +
                                "<form name =\"updateSettled\" action=\"/icici/servlet/RefundBankTransactionInquiry?ctoken=" + ctoken + "\" method=\"post\">" +
                                "<input type=\"hidden\" name=\"startdate\" value=\"" + startDate + "\">" +
                                "<input type=\"hidden\" name=\"starttime\" value=\"" + startTime + "\">" +
                                "<input type=\"hidden\" name=\"enddate\" value=\"" + endDate + "\">" +
                                "<input type=\"hidden\" name=\"endtime\" value=\"" + endTime + "\">" +
                                "<input type=\"hidden\" name=\"gateway\" value=\"" + pgtypeid + "\">" +
                                "<input type=\"hidden\" name=\"merchantid\" value=\"" + merchantid + "\">" +
                                "<input type=\"hidden\" name=\"accountid\" value=\"" + accountid + "\">" +
                                "<input type=\"hidden\" name=\"terminalid\" value=\"" + terminalId + "\">" +
                                "<input type=\"hidden\" name=\"partnerName\" value=\"" + partnerName + "\">" +
                                "<input type=\"hidden\" name=\"currency\" value=\"" + currency + "\">" +
                                "<input type=\"hidden\" name=\"status\" value=\"" + status1 + "\">" +
                                "<input type=\"hidden\" name=\"onchangedvalue\" value=\"" + onchangedvalue + "\" >" +
                                "<input type=\"hidden\" name=\"countbefore\" value=\"" + innerhash.get("COUNT(*)") + "\" >" +
                                "<input type=\"hidden\" name=\"ActivityAction\" value=\"Markedforreversal Inquiry Cron\">" +
//
//                            "<input type=\"hidden\" name=\"button2\" value=\"captureAll\">" +
//                            "<input type=\"hidden\" name=\"action\" value=\"captureAll\">" +
                                "<input type=\"submit\" class=\"goto\" value=\"Markedforreversal Inquiry Cron\"></form></td>");
                    }
                }
                    out.println("</tr>");


            }
        %>
    </table>
<%
        if(payoutHash != null)
        {
            int payouttotal=Integer.parseInt((String)request.getAttribute("payouttotal"));
    %>

    <table border="0" cellpadding="3" cellspacing="3" bordercolor="#2379A5" width="50%"  align="center" class="table table-striped table-bordered table-green dataTable"  >
        <tr>
            <td  class="textb" colspan="6"><center><b>Bank Transaction Report(Payout)</b></center></td>
        </tr>
        <tr <%=style%>>
            <td  class="th0" >Status</td>
            <td  class="th0" >No. Of Transaction</td>
            <td  class="th0" >Amount</td>
            <td  class="th0" >Payout Amount</td>
            <td  class="th0" >Percentage(%)</td>
            <%
                if(isMappingAvaliable){
            %>
            <td  class="th0">Action</td>
            <%
                }
            %>
        </tr>

        <%
            for(int pos = 1; pos <= payoutHash.size(); pos++)
            {
                innerhash = (Hashtable) payoutHash.get(pos + "");
                if (pos % 2 == 0)
                {
                    style = "class=tr0";

                }
                else
                {
                    style = "class=tr1";

                }
                String status1 = (String) innerhash.get("STATUS");

                    out.println("<tr  " + style + ">");
                    out.println("<td align=\"center\" >" + innerhash.get("STATUS") + " </td>");
                    out.println("<td align=\"center\" >" + innerhash.get("COUNT(*)") + " </td>");
                    out.println("<td align=\"center\" >" + innerhash.get("amount") + " </td>");
                    out.println("<td align=\"center\" >" + innerhash.get("payoutamount") + " </td>");

                    out.println("<td align=\"center\" >" + Functions.round(Double.parseDouble((String) innerhash.get("COUNT(*)")) / payouttotal * 100, 2) + "%</td>");

            if(isMappingAvaliable)
            {

                    if (status1.equalsIgnoreCase("payoutfailed") && (LetzPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||QikPayV2PaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || ApexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || PayGPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || IMoneyPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||EasyPaymentzPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||QikpayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||BhartiPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||VervePaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||SafexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)))
                {
                        out.println("<td align=\"center\" " + style + ">" );
                        out.println("<form name =\"updateSettled\" action=\"/icici/servlet/BankTransactionInquiry?ctoken="+ctoken+"\" method=\"post\">"+
                                "<input type=\"hidden\" name=\"startdate\" value=\""+startDate+"\">"+
                                "<input type=\"hidden\" name=\"starttime\" value=\""+startTime+"\">"+
                                "<input type=\"hidden\" name=\"enddate\" value=\""+endDate+"\">"+
                                "<input type=\"hidden\" name=\"endtime\" value=\""+endTime+"\">"+
                                "<input type=\"hidden\" name=\"gateway\" value=\""+pgtypeid+"\">"+
                                "<input type=\"hidden\" name=\"merchantid\" value=\""+merchantid+"\">"+
                                "<input type=\"hidden\" name=\"accountid\" value=\""+accountid+"\">"+
                                "<input type=\"hidden\" name=\"terminalid\" value=\""+terminalId+"\">"+
                                "<input type=\"hidden\" name=\"partnerName\" value=\""+partnerName+"\">"+
                                "<input type=\"hidden\" name=\"currency\" value=\""+currency+"\">"+
                                "<input type=\"hidden\" name=\"status\" value=\""+status1+"\">"+
                                "<input type=\"hidden\" name=\"onchangedvalue\" value=\""+onchangedvalue+"\">"+
                                "<input type=\"hidden\" name=\"countbefore\" value=\""+innerhash.get("COUNT(*)")+"\" >"+
                                "<input type=\"hidden\" name=\"ActivityAction\" value=\"Payoutfailed Inquiry Cron\">"+

//                            "<input type=\"hidden\" name=\"button2\" value=\"captureAll\">" +
//                            "<input type=\"hidden\" name=\"action\" value=\"captureAll\">" +
                            "<input type=\"submit\" class=\"goto\" value=\"Payoutfailed Inquiry Cron\"></form>");

                    out.println("<form id=\"payoutFailedCron\" name =\"payoutFailedCron\" action=\"/icici/servlet/BankTransactionInquiry?ctoken=" + ctoken + "\" method=\"post\">" +
                            "<input type=\"hidden\" name=\"startdate\" value=\"" + startDate + "\">" +
                            "<input type=\"hidden\" name=\"starttime\" value=\"" + startTime + "\">" +
                            "<input type=\"hidden\" id=\"enddate\" name=\"enddate\" value=\"" + endDate + "\">" +
                            "<input type=\"hidden\" name=\"endtime\" value=\"" + endTime + "\">" +
                            "<input type=\"hidden\" name=\"gateway\" value=\"" + pgtypeid + "\">" +
                            "<input type=\"hidden\" name=\"merchantid\" value=\"" + merchantid + "\">" +
                            "<input type=\"hidden\" name=\"accountid\" value=\"" + accountid + "\">" +
                            "<input type=\"hidden\" name=\"terminalid\" value=\"" + terminalId + "\">" +
                            "<input type=\"hidden\" name=\"partnerName\" value=\"" + partnerName + "\">" +
                            "<input type=\"hidden\" name=\"currency\" value=\"" + currency + "\">" +
                            "<input type=\"hidden\" name=\"onchangedvalue\" value=\"" + onchangedvalue + "\">" +
                            "<input type=\"hidden\" name=\"countbefore\" value=\"" + innerhash.get("COUNT(*)") + "\" >" +
                            "<input type=\"hidden\" name=\"ActivityAction\" value=\"PayoutFailedCron\">" +
                            "<input type=\"button\" style=\"background: #c1c1c1;color: #000;text-shadow: none;\" onClick=\"submitForm1()\" class=\"goto\" value=\"PayoutFailed Transaction \"></form></td>");


                    } else if (status1.equalsIgnoreCase("payoutstarted") && (LetzPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||QikPayV2PaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || ApexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || PayGPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || IMoneyPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||EasyPaymentzPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) ||QikpayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||BhartiPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||VervePaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||SafexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)))
                {
                    out.println("<td align=\"center\" " + style + ">" +
                                "<form name =\"updateSettled\" action=\"/icici/servlet/BankTransactionInquiry?ctoken="+ctoken+"\" method=\"post\">"+
                                "<input type=\"hidden\" name=\"startdate\" value=\""+startDate+"\">"+
                                "<input type=\"hidden\" name=\"starttime\" value=\""+startTime+"\">"+
                                "<input type=\"hidden\" name=\"enddate\" value=\""+endDate+"\">"+
                                "<input type=\"hidden\" name=\"endtime\" value=\""+endTime+"\">"+
                                "<input type=\"hidden\" name=\"gateway\" value=\""+pgtypeid+"\">"+
                                "<input type=\"hidden\" name=\"merchantid\" value=\""+merchantid+"\">"+
                                "<input type=\"hidden\" name=\"accountid\" value=\""+accountid+"\">"+
                                "<input type=\"hidden\" name=\"terminalid\" value=\""+terminalId+"\">"+
                                "<input type=\"hidden\" name=\"partnerName\" value=\""+partnerName+"\">"+
                                "<input type=\"hidden\" name=\"currency\" value=\""+currency+"\">"+
                                "<input type=\"hidden\" name=\"status\" value=\""+status1+"\">"+
                                "<input type=\"hidden\" name=\"onchangedvalue\" value=\""+onchangedvalue+"\">"+
                                "<input type=\"hidden\" name=\"countbefore\" value=\""+innerhash.get("COUNT(*)")+"\" >"+
                                "<input type=\"hidden\" name=\"ActivityAction\" value=\"Payoutstarted Inquiry Cron\">"+
//                            "<input type=\"hidden\" name=\"button2\" value=\"captureAll\">" +
//                            "<input type=\"hidden\" name=\"action\" value=\"captureAll\">" +
                            "<input type=\"submit\" class=\"goto\" value=\"Payoutstarted Inquiry Cron\"></form></td>");

                    }else if (status1.equalsIgnoreCase("payoutsuccessful") && (QikPayV2PaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || ApexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || PayGPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || IMoneyPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||IMoneyPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)|| QikpayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName) || BhartiPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||VervePaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)||SafexPayPaymentGateway.GATEWAY_TYPE.equalsIgnoreCase(gatewayName)))
                {
                    out.println("<td align=\"center\" " + style + ">" +
                                "<form name =\"updateSettled\" action=\"/icici/servlet/BankTransactionInquiry?ctoken="+ctoken+"\" method=\"post\">"+
                                "<input type=\"hidden\" name=\"startdate\" value=\""+startDate+"\">"+
                                "<input type=\"hidden\" name=\"starttime\" value=\""+startTime+"\">"+
                                "<input type=\"hidden\" name=\"enddate\" value=\""+endDate+"\">"+
                                "<input type=\"hidden\" name=\"endtime\" value=\""+endTime+"\">"+
                                "<input type=\"hidden\" name=\"gateway\" value=\""+pgtypeid+"\">"+
                                "<input type=\"hidden\" name=\"merchantid\" value=\""+merchantid+"\">"+
                                "<input type=\"hidden\" name=\"accountid\" value=\""+accountid+"\">"+
                                "<input type=\"hidden\" name=\"terminalid\" value=\""+terminalId+"\">"+
                                "<input type=\"hidden\" name=\"partnerName\" value=\""+partnerName+"\">"+
                                "<input type=\"hidden\" name=\"currency\" value=\""+currency+"\">"+
                                "<input type=\"hidden\" name=\"status\" value=\""+status1+"\">"+
                                "<input type=\"hidden\" name=\"onchangedvalue\" value=\""+onchangedvalue+"\">"+
                                "<input type=\"hidden\" name=\"countbefore\" value=\""+innerhash.get("COUNT(*)")+"\" >"+
                                "<input type=\"hidden\" name=\"ActivityAction\" value=\"Payoutstarted Inquiry Cron\">"+
//                            "<input type=\"hidden\" name=\"button2\" value=\"captureAll\">" +
//                            "<input type=\"hidden\" name=\"action\" value=\"captureAll\">" +
                            "<input type=\"submit\" class=\"goto\" value=\"payoutsuccessful Inquiry Cron\"></form></td>");

                }
            }
                    out.println("</tr>");

            }
        %>
    </table>

    <%
        if(refundHash != null)
        {
            int refundtotal=Integer.parseInt((String)request.getAttribute("refundtotal"));
    %>

    <table border="0" cellpadding="3" cellspacing="3" bordercolor="#2379A5" width="50%"  align="center" class="table table-striped table-bordered table-green dataTable" >
        <tr>
            <td  class="textb" colspan="6"><center><b>Bank Transaction Report(Refund & Chargeback)</b></center></td>
        </tr>
        <tr <%=style%>>
            <td  class="th0" >Status</td>
            <td  class="th0" >No. Of Transaction</td>
            <td  class="th0" >Amount</td>
            <td  class="th0" >Refund Amount</td>
            <td  class="th0" >Chargeback Amount</td>
            <td  class="th0" >Percentage(%)</td>

        </tr>

        <%
            for(int pos1 = 1; pos1 <= refundHash.size(); pos1++)
            {
                innerhash2 = (Hashtable) refundHash.get(pos1 + "");
                if(pos1%2==0)
                {
                    style="class=tr0";

                }
                else
                {
                    style="class=tr1";

                }
                out.println("<tr  "+style+">");
                out.println("<td align=\"center\" >"+innerhash2.get("STATUS")+ " </td>");
                out.println("<td align=\"center\" >"+innerhash2.get("COUNT(*)")+ " </td>");
                out.println("<td align=\"center\" >"+innerhash2.get("amount")+ " </td>");
                out.println("<td align=\"center\" >"+innerhash2.get("refundamount")+ " </td>");
                out.println("<td align=\"center\" >"+innerhash2.get("chargebackamount")+ " </td>");
                    out.println("<td align=\"center\" >"+Functions.round(Double.parseDouble((String)innerhash2.get("COUNT(*)"))/refundtotal*100,2)+"%</td>");
                out.println("</tr>");
            }
        %>
    </table>

</div>
<%
                }
                }
            }
            else
            {
                out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
            }
        }
        else
        {
            out.println(Functions.NewShowConfirmation("Result",status));
        }
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>

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