<%@ page import="com.directi.pg.Database "%>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ include file="functions.jsp"%>

<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 11/8/12
  Time: 3:09 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.core.GatewayTypeService"%>
<%@ page import="com.manager.PayoutManager" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%@ page import="com.manager.vo.RollingReserveDateVO" %>
<%@ page import="com.manager.vo.SettlementDateVO" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.payoutVOs.*" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.TreeMap" %>
<%!
    Logger logger=new Logger("listMerchantPayoutReport.jsp");
%>

<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <%--Datepicker css format--%>
   <%-- <script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-account-member.js"></script>--%>

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>

    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: 'yy-mm-dd'});
        });

        function confirmsubmitreg()
        {
            var r= window.confirm("Are You Sure To Generate wire?");
            if(r==true)
            {
                document.getElementById("generatewire").submit();
            }
            else
            {

            }
        }
        function payoutcronconfirm()
        {
            var r= window.confirm("Are You Sure To Execute Payout Cron?");
            if(r==true)
            {
                document.getElementById("payoutcronfrm").submit();
            }
            else
            {

            }
        }
        </script>
    <title>Reports>  Merchant Payout Report</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String str = "";
        String pgtypeid = "";
        pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
        String accountid = Functions.checkStringNull(request.getParameter("accountid"))== null ? "" : request.getParameter("accountid");
        String memberid = Functions.checkStringNull(request.getParameter("toid")) == null ? "" : request.getParameter("toid");

        Map terminalMap = GatewayTypeService.getAllTerminalsGroupByMerchant();

        if(pgtypeid!=null)str = str + "&pgtypeid=" + pgtypeid;
        else
            pgtypeid="";

        String cardtypeid = request.getParameter("cardtypeid");
        String paymodeid = request.getParameter("paymodeid");
        PayoutManager payoutManager = new PayoutManager();
        List<TerminalVO> cardtypeList = payoutManager.loadcardtypeids();
        List<TerminalVO> paymenttypeList = payoutManager.loadPaymodeids();

        TerminalManager terminalManager = new TerminalManager();
        List<TerminalVO> terminalList = terminalManager.getAllMappedTerminals();
        //List<GatewayType> gatewayTypes = GatewayTypeService.getAllGatewayTypes();
        TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();
        TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();
        TreeMap<String,TerminalVO> memberMap = new TreeMap<String, TerminalVO>();

        for(TerminalVO terminalVO : terminalList)
        {
            String memberKey = terminalVO.getMemberId()+"-"+terminalVO.getAccountId()+"-"+terminalVO.getGateway().toUpperCase() + "-" + terminalVO.getCurrency() + "-" + terminalVO.getGateway_id();
            memberMap.put(memberKey,terminalVO);
        }


%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        Connection conn=null;
        try{
%>

<div class="row">
<div class="col-lg-12">
<div class="panel panel-default">
<div class="panel-heading" >
    Merchant Payout Report
    <div style="float: right;margin-left: 5px;">
        <form  id="consolidatatedreport" action="/icici/consolidatedReport.jsp?ctoken=<%=ctoken%>" method="POST">
            <input type="submit"  class="buttonform"  value="Consolidated Report ">
        </form>
    </div>
    <div style="float: right;">
        <form  id="merchantdynamicreport" action="/icici/merchantdynamicreport.jsp?ctoken=<%=ctoken%>" method="POST">
            <input type="submit"  class="buttonform"  value="Dynamic Report ">
        </form>
    </div>

    <%--<div style="float: right;">
        <form  id="payoutcronfrm" action="/icici/payoutcron.jsp?ctoken=<%=ctoken%>" method="POST">
            <input type="button"  class="buttonform"  value="Payout Cron " onclick="payoutcronconfirm()">
        </form>
    </div>--%>
    <div style="float: right;margin-right: 5px;">
        <form  id="" action="/icici/listMerchantPayoutReport.jsp?ctoken=<%=ctoken%>" method="POST">
            <input type="submit"  class="buttonform"  value="Payout Report">
        </form>
    </div>
</div>
<form action="/icici/servlet/GeneratePayoutServlet?ctoken=<%=ctoken%>" method="post" name="forms" >
<input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
<input type="hidden" value="generate" name="action">
<table width="100%">
    <tr>
        <td class="text" align="center">
            <%
                List<String> error=(List)request.getAttribute("errorList");
                if(error !=null)
                {
                    out.println("<center><font class=\"textb\"><b>"+error+"</b></font></center>");
                }
            %>
        </td>
    </tr>
</table>

<table  align="center" width="95%" cellpadding="2" cellspacing="2">
    <tr>
        <td>
            <table border="0" cellpadding="5" cellspacing="0" width="95%"  align="center">
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="13%" class="textb" >Gateway</td>
                    <td width="3%" class="textb"></td>
                    <td width="12%" class="textb">
                        <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                    <%-- <select size="1" id="bank" name="pgtypeid" class="txtbox"  style="width:200px;">
                            <option value="0" default>--All--</option>
                            <%
                                for(String gatewayType : gatewayTypeTreeMap.keySet())
                                {
                                    String isSelected = "";
                                    //String value = gatewayType.toUpperCase()+"-"+gatewayTypeTreeMap.get(gatewayType).getPgTypeId();
                                    if(gatewayType.equalsIgnoreCase(pgtypeid))
                                    {
                                        isSelected = "selected";
                                    }
                            %>
                            <option value="<%=gatewayType%>" <%=isSelected%>><%=gatewayType%></option>
                            <%
                                }
                            %>

                        </select>--%>
                    </td>

                    <td width="6%" class="textb">&nbsp;</td>
                    <td width="11%" class="textb" >AccountID*</td>
                    <td width="3%" class="textb"></td>
                    <td width="10%" class="textb">
                        <input name="accountid" id="accountid1" value="<%=accountid%>" class="txtbox" autocomplete="on">
                    <%-- <select size="1" id="accountid" name="accountid" class="txtbox" style="width: 200px">
                            <option data-bank="all" value="0">---Select AccountID---</option>
                            <%
                                for(Integer sAccid : accountDetails.keySet())
                                {
                                    GatewayAccount g = accountDetails.get(sAccid);
                                    String isSelected = "";
                                    String gateway2 = g.getGateway().toUpperCase();
                                    String currency2 = g.getCurrency();
                                    String pgtype = g.getPgTypeId();

                                    if (String.valueOf(sAccid).equals(accountid))
                                        isSelected = "selected";
                                    else
                                        isSelected = "";
                            %>
                            <option data-bank="<%=gateway2+"-"+currency2+"-"+pgtype%>"  value="<%=sAccid%>" <%=isSelected%>><%=sAccid+"-"+g.getMerchantId()+"-"+g.getCurrency()%></option>
                            <%
                                }
                            %>
                        </select>--%>
                        <%--<select size="1" id="accountid" name="accountid" class="txtbox" style="width: 200px">
                            <option data-bank="all" value="0">Select AccountID</option>
                            <%
                                for(Integer sAccountID:accountMap.keySet())
                                {
                                    String isSelected = "";
                                    String sAccid = String.valueOf(sAccountID);
                                    String aGateway = accountMap.get(sAccountID).getGateway().toUpperCase()+"-"+accountMap.get(sAccountID).getCurrency()+"-"+accountMap.get(sAccountID).getGateway_id();


                                    if (sAccid.equals(accountid))
                                        isSelected = "selected";
                                    else
                                        isSelected = "";
                            %>
                            <option data-bank="<%=aGateway%>"  value="<%=sAccountID.toString()%>" <%=isSelected%>><%=sAccountID+"-"+accountMap.get(sAccountID).getGwMid()+"-"+accountMap.get(sAccountID).getCurrency()%></option>
                            <%

                                }
                            %>
                        </select>--%>
                    </td>

                    <td width="2%" class="textb">&nbsp;</td>
                    <td width="11%" class="textb" >Member ID*</td>
                    <td width="3%" class="textb"></td>
                    <td width="12%" class="textb">
                        <input name="toid" id="memberid1" value="<%=memberid%>" class="txtbox" autocomplete="on">
                    <%--<select name="toid" id="memberid" class="txtbox" style="width:200px;">
                            <option data-bank="all"  data-accid="all" value="0" selected>--Select MemberID--</option>
                            <%
                                for(String sMemberId: memberMap.keySet())
                                {
                                    TerminalVO t = memberMap.get(sMemberId);
                                    String accId = t.getAccountId();
                                    String aContactPerson = t.getContactPerson();
                                    String aCompanyName = t.getCompany_name();
                                    String gateway2 = t.getGateway().toUpperCase();
                                    String currency2 = t.getCurrency();
                                    String pgtype = t.getGateway_id();
                                    String value = gateway2+"-"+currency2+"-"+pgtype;
                                    String isSelected = "";
                                    if(String.valueOf(t.getMemberId()+"-"+accId).equalsIgnoreCase(memberid+"-"+accId))
                                    {
                                        isSelected = "selected";
                                    }
                                    else
                                    {
                                        isSelected = "";
                                    }
                            %>
                            <option data-bank="<%=value%>"  data-accid="<%=accId%>" value="<%=t.getMemberId()%>" <%=isSelected%>><%=t.getMemberId()+"-"+accId+"-"+aCompanyName%></option>
                            <%
                                }
                            %>
                        </select>--%>
                    </td>

                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb"></td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="12%" class="textb">
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
                    <td width="11%" class="textb" >Paymode*</td>
                    <td width="3%" class="textb"></td>
                    <td width="12%" class="textb">
                        <select size="1" class="txtbox" name="paymode">
                            <option value="" selected>Select PaymentType</option>
                            <%
                                for(TerminalVO terminalVO:paymenttypeList)
                                {
                                    String isSelected="";
                                    if(terminalVO.getPaymodeId().equalsIgnoreCase(paymodeid))
                                        isSelected="selected";
                                    else
                                        isSelected="";
                            %>
                            <option value="<%=terminalVO.getPaymodeId()%>" <%=isSelected%>><%=terminalVO.getPaymentTypeName()%>
                            </option>
                            <%
                                }
                            %>
                        </select>
                        <%--<select name="paymode" class="txtbox">
                            <option value="" selected></option>
                            <%
                                conn=Database.getConnection();
                                String query = "SELECT paymodeid, paymentType FROM payment_type ORDER BY paymodeid ASC";
                                PreparedStatement pstmt = conn.prepareStatement( query );
                                ResultSet rs = pstmt.executeQuery();
                                while (rs.next()){
                                    out.println("<option value=\""+rs.getString("paymodeid")+"\">"+rs.getString("paymentType")+"</option>");
                                }
                            %>

                        </select>--%>
                    </td>

                    <td width="6%" class="textb">&nbsp;</td>
                    <td width="11%" class="textb" >CardType*</td>
                    <td width="3%" class="textb"></td>
                    <td width="10%" class="textb">
                        <select size="1" class="txtbox" name="cardtype">
                            <option value="" selected>Select CardType</option>
                            <%
                                for(TerminalVO terminalVO:cardtypeList)
                                {
                                    String isSelected="";
                                    if(terminalVO.getCardTypeId().equalsIgnoreCase(cardtypeid))
                                        isSelected="selected";
                                    else
                                        isSelected="";
                            %>
                            <option value="<%=terminalVO.getCardTypeId()%>" <%=isSelected%>><%=terminalVO.getCardType()%></option>
                            <%
                                }
                            %>
                        </select>
                        <%--<select name="cardtype" class="txtbox">
                            <option value="" selected></option>
                            <%
                                query ="SELECT cardtypeid, cardType FROM card_type ORDER BY cardtypeid ASC";
                                pstmt = conn.prepareStatement( query );
                                rs = pstmt.executeQuery();
                                while (rs.next()){
                                    out.println("<option value=\""+rs.getString("cardtypeid")+"\">"+rs.getString("cardType")+"</option>");
                                }

                            %>
                        </select>--%>
                    </td>

                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb">Report Type</td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="12%" class="textb">
                        <select name="reporttype" class="txtbox">
                            <option value="summary">Summary</option>
                            <option value="wirereport">WireReport</option>
                        </select>
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
                    <td width="11%" class="textb" >Start Date</td>
                    <td width="3%" class="textb"></td>
                    <td width="12%" class="textb">
                        <input type="text" readonly class="datepicker" style="width:142px" name="startDate">
                    </td>
                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="11%" class="textb" >End Date</td>
                    <td width="3%" class="textb"></td>
                    <td width="12%" class="textb">
                        <input type="text" readonly class="datepicker" style="width:142px" name="endDate" >
                    </td>

                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >&nbsp;</td>
                    <td width="3%" class="textb">&nbsp;</td>
                    <td width="12%" class="textb">
                        <button type="submit" class="buttonform" value="View Payout Report" style="width: 158px;">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;View Payout Report
                        </button>
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
                </tr>
            </table>
        </td>
    </tr>
</table>
</form>
</div>
</div>
</div>

<div class="reporttable">
<%
    MerchantPayoutReportVO merchantPayoutReportVO=(MerchantPayoutReportVO)request.getAttribute("merchantPayoutReportVO");
    if(merchantPayoutReportVO!=null)
    {
        List<ChargeDetailsVO> detailsVOList=merchantPayoutReportVO.getChargesDetailsVOsList();
        SettlementDateVO settlementDateVO=merchantPayoutReportVO.getSettlementDateVO();
        WireChargeVO wireChargeVO=merchantPayoutReportVO.getWireChargeVO();
        TerminalVO terminalVO=merchantPayoutReportVO.getTerminalVO();
        MerchantDetailsVO merchantDetailsVO=merchantPayoutReportVO.getMerchantDetailsVO();
        RollingReserveDateVO rollingReserveDateVO=merchantPayoutReportVO.getRollingReserveDateVO();
        List<ReserveGeneratedVO> reserveGeneratedVOList=merchantPayoutReportVO.getReserveGeneratedVOList();
        List<ReserveRefundVO> reserveRefundVOList=merchantPayoutReportVO.getReserveRefundVOsList();
        List<GrossChargeVO> grossChargeVOList=merchantPayoutReportVO.getGrossTypeChargeVOList();
%>

<table align=center width="80%" border="2px" style="border-color:#ffffff ">
<tr>
    <td  colspan="5" valign="middle" align="center" class="texthead" bgcolor="#008BBA"><b>Member Details</b></td>
</tr>
<tr>
    <td colspan="3" style="padding-left: 5%" valign="middle" align="left" class="textb" bgcolor="#f5f5f5">Member ID:</td>
    <td colspan="2" valign="middle" align="left" class="textb" bgcolor="#f5f5f5"><%=merchantDetailsVO.getMemberId()%></td>
</tr>
<tr>
    <td colspan="3" style="padding-left: 5%" valign="middle" align="left" class="textb" bgcolor="#f5f5f5">Terminal ID:</td>
    <td colspan="2" valign="middle" align="left" class="textb" bgcolor="#f5f5f5"><%=terminalVO.getTerminalId()%></td>
</tr>
<tr>
    <td colspan="3" style="padding-left: 5%" valign="middle" align="left" class="textb" bgcolor="#f5f5f5">Company Name:</td>
    <td colspan="2" valign="middle" align="left" class="textb" bgcolor="#f5f5f5"><%=merchantDetailsVO.getCompany_name()%></td>
</tr>
<tr>
    <td class="textb"  style="padding-left: 5%" colspan="3" valign="middle" align="left" bgcolor="#f5f5f5">Contact Person:</td>
    <td class="textb"   colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=merchantDetailsVO.getContact_persons()%></td>
</tr>
<tr>
    <td class="textb"  style="padding-left: 5%" colspan="3" valign="middle" align="left" bgcolor="#f5f5f5">Payment Mode:</td>
    <td class="textb"   colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=terminalVO.getPaymentTypeName()%></td>
</tr>
<tr>
    <td class="textb"  style="padding-left: 5%" colspan="3" valign="middle" align="left" bgcolor="#f5f5f5">Card Type:</td>
    <td class="textb"   colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=terminalVO.getCardType()%></td>
</tr>
<tr>
    <td class="textb"  style="padding-left: 5%" colspan="3" valign="middle" align="left" bgcolor="#f5f5f5">Currency:</td>
    <td class="textb"   colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=merchantPayoutReportVO.getCurrency()%></td>
</tr>
<tr>
    <td class="textb"  style="padding-left: 5%" colspan="3"  valign="middle" align="left" bgcolor="#f5f5f5">Settle Covered Upto:</td>
    <td class="textb" colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"> <%=settlementDateVO.getSettlementEndDate()%></td>
</tr>
<tr>
    <td class="textb"  style="padding-left: 5%" colspan="3"  valign="middle" align="left" bgcolor="#f5f5f5">Decline Covered Upto:</td>
    <td class="textb" colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=settlementDateVO.getDeclinedEndDate()%></td>
</tr>
<tr>
    <td class="textb"  style="padding-left: 5%" colspan="3"  valign="middle" align="left" bgcolor="#f5f5f5">Reversal Covered Upto:</td>
    <td class="textb" colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=settlementDateVO.getReversedEndDate()%></td>
</tr>
<tr>
    <td class="textb"  style="padding-left: 5%" colspan="3"  valign="middle" align="left" bgcolor="#f5f5f5">Chargeback Covered Upto:</td>
    <td class="textb" colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=settlementDateVO.getChargebackEndDate()%></td>
</tr>

<tr>
    <td class="textb"  style="padding-left: 5%" colspan="3"  valign="middle" align="left" bgcolor="#f5f5f5">Rolling Reserve Release Date Upto:</td>
    <td class="textb" colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=rollingReserveDateVO.getRollingReserveEndDate()%></td>
</tr>
<tr>
    <td colspan="5" valign="middle" align="center" class="texthead" bgcolor="#008BBA"><b>Charges Details</b></td>

</tr>
<tr>
    <td valign="middle" align="center" class="texthead">Charge Name</td>
    <td valign="middle" align="center" class="texthead">Rate/Fee</td>
    <td valign="middle" align="center" class="texthead">Counter</td>
    <td valign="middle" align="center" class="texthead">Amount</td>
    <td valign="middle" align="center" class="texthead">Total</td>

</tr>
<%
    for(ChargeDetailsVO chargeDetailsVO:detailsVOList)
    {
%>
<tr>
    <td class="textb"valign="middle" align="left" bgcolor="#f5f5f5"  style="padding-left: 5%"><%=chargeDetailsVO.getChargeName()%></td>
    <td class="textb" valign="middle" align="left" bgcolor="#f5f5f5" style="padding-left: 8%"><%=chargeDetailsVO.getChargeValue()%></td>
    <td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right: 7%"><%=chargeDetailsVO.getCount()%></td>
    <td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right: 7%"><%=Functions.round(chargeDetailsVO.getAmount(),2)%></td>
    <td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right: 7%"><%=Functions.round(chargeDetailsVO.getTotal(),2)%></td>
</tr>

<%
    }
%>
<tr>
    <td  colspan="4" valign="middle" align="right" bgcolor="#f5f5f5" class="textb"><b>Total:-&nbsp;&nbsp;</b></td>
    <td valign="middle" align="right" class="texthead" style="background-color:#2c3e50;padding-right: 7%"><font color="white"><b><%=Functions.round(merchantPayoutReportVO.getMerchantTotalChargesAmount(),2)%></b></font></td>
</tr>

<tr>
    <td valign="middle" align="center" bgcolor="#008BBA" class="texthead" colspan="5"><b>Generated Reserve Details</b></td>
</tr>

<%
    for(ReserveGeneratedVO reserveGeneratedVO:reserveGeneratedVOList)
    {

%>
<tr>
    <td class="textb" valign="middle" align="left"  bgcolor="#f5f5f5" style="padding-left:5%"><%=reserveGeneratedVO.getChargeName()%></td>
    <td class="textb" valign="middle" align="left"  bgcolor="#f5f5f5" style="padding-left:8%"><%=reserveGeneratedVO.getChargeValue()%></td>
    <td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right:7%"><%=reserveGeneratedVO.getCount()%></td>
    <td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right:7%"><%=Functions.round(reserveGeneratedVO.getAmount(),2)%></td>
    <td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right:7%"><%=Functions.round(reserveGeneratedVO.getTotal(),2)%></td>
</tr>
<%
    }
%>
<tr>
    <td  colspan="4" valign="middle" align="right" bgcolor="#f5f5f5" class="textb"><b>Total:-&nbsp;&nbsp;</b></td>
    <td valign="middle" align="right" class="texthead" style="background-color:#2c3e50;padding-right: 7%"><font color="white"><b><%=Functions.round(merchantPayoutReportVO.getMerchantRollingReserveAmount(),2)%></b></font></td>
</tr>
<tr>
    <td valign="middle" align="center"  class="texthead" colspan="5"><b>Refunded Reserve Details</b></td>
</tr>

<%
    for(ReserveRefundVO reserveRefundVO:reserveRefundVOList)
    {

%>
<tr>
    <td class="textb" valign="middle" align="left" bgcolor="#f5f5f5" style="padding-left: 5%"><%=reserveRefundVO.getChargeName()%></td>
    <td class="textb" valign="middle" align="left" bgcolor="#f5f5f5" style="padding-left:8%"><%=reserveRefundVO.getChargeValue()%></td>
    <td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right:7%"><%=reserveRefundVO.getCount()%></td>
    <td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right:7%"><%=Functions.round(reserveRefundVO.getAmount(),2)%></td>
    <td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right:7%"><%=Functions.round(reserveRefundVO.getTotal(),2)%></td>
</tr>
<%
    }
%>
<tr>

    <td  colspan="4" valign="middle" align="right" bgcolor="#f5f5f5" class="textb"><b>Total:-&nbsp;&nbsp;</b></td>
    <td valign="middle" align="right" class="texthead"style="background-color:#2c3e50;padding-right: 7% "><font color="white"><b><%=Functions.round(merchantPayoutReportVO.getMerchantRollingReleasedAmount(),2)%></b></font></td>
</tr>
<tr>
    <%--<td></td><td></td><td></td>--%><td colspan="5" valign="middle" class="texthead" align="center" bgcolor="#008BBA"><b>Summary</b></td>

</tr>
<tr>
    <%--<td></td><td></td><td></td>--%><td style="padding-left: 5%" class="textb" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">Total Processing Amount</td>
    <td class="textb" valign="middle"  align="right" bgcolor="#f5f5f5" style="padding-right:7%"><%=Functions.round(merchantPayoutReportVO.getMerchantTotalProcessingAmount(),2)%></td>
</tr>
<tr>
    <%--<td></td><td></td><td></td>--%><td style="padding-left: 5%" class="textb" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">Total Fees</td>
    <td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right:7%" ><%if(merchantPayoutReportVO.getMerchantTotalChargesAmount()>0){out.print("-"+Functions.round(merchantPayoutReportVO.getMerchantTotalChargesAmount(),2));}else{out.print(Functions.round(merchantPayoutReportVO.getMerchantTotalChargesAmount(),2));}%></td>
</tr>
<tr>
    <%--<td></td><td></td><td></td>--%><td style="padding-left: 5%" class="textb" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">Total Chargeback/Reversal</td>
    <td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right:7%"><%if(merchantPayoutReportVO.getMerchantTotalChargebackAmount()+merchantPayoutReportVO.getMerchantTotalReversedAmount()>0){out.print("-"+Functions.round(merchantPayoutReportVO.getMerchantTotalChargebackAmount()+merchantPayoutReportVO.getMerchantTotalReversedAmount(),2));}else{out.print(Functions.round(merchantPayoutReportVO.getMerchantTotalChargebackAmount()+merchantPayoutReportVO.getMerchantTotalReversedAmount(),2));}%></td>
</tr>
<tr>
    <%--<td></td><td></td><td></td>--%><td style="padding-left: 5%" class="textb" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">Generated Reserve</td>
    <td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right:7%"><%if(merchantPayoutReportVO.getMerchantRollingReserveAmount()>0){out.print("-"+Functions.round(merchantPayoutReportVO.getMerchantRollingReserveAmount(),2));}else{out.print(Functions.round(merchantPayoutReportVO.getMerchantRollingReserveAmount(),2));}%></td>
</tr>
<tr>
    <%--<td></td><td></td><td></td>--%><td colspan="4" valign="middle" class="texthead" align="center" bgcolor="#008BBA" style="padding-left: 15%"><b>Gross Amount</b></td>
    <td valign="middle" align="right" class="texthead" style="background-color:#2c3e50;padding-right:7%"><font color="#FFFFFF" ><b><%=Functions.round(merchantPayoutReportVO.getMerchantTotalBalanceAmount(),2)%></b></font></td>
</tr>
<%
    if(wireChargeVO!=null)
    {%>
<tr>
    <td class="textb" valign="middle" align="left" bgcolor="#f5f5f5" style="padding-left:5%" ><%=wireChargeVO.getChargeName()%></td>
    <td class="textb" valign="middle" align="left" bgcolor="#f5f5f5" style="padding-left:8%"><%=wireChargeVO.getChargeValue()%></td>
    <td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right:7%"><%=wireChargeVO.getCount()%></td>
    <td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right:7%"><%="-"%></td>
    <td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right:7%"><%="-"+Functions.round(wireChargeVO.getTotal(),2)%></td>
</tr>
<%}
%>

<%
    for(GrossChargeVO grossChargeVO:grossChargeVOList)
    {

%>
</tr>
<td class="textb" valign="middle" align="left" bgcolor="#f5f5f5" style="padding-left: 5%"><%=grossChargeVO.getChargeName()%></td>
<td class="textb" valign="middle" align="left" bgcolor="#f5f5f5" style="padding-left:8%"><%=grossChargeVO.getChargeValue()%></td>
<td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right:7%"><%=grossChargeVO.getCount()%></td>
<td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right:7%"><%=Functions.round(grossChargeVO.getAmount(),2)%></td>
<td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right:7%"><%if(grossChargeVO.getTotal()>0){out.print("-"+Functions.round(grossChargeVO.getTotal(),2));}else{out.print(Functions.round(grossChargeVO.getTotal(),2));}%></td>
</tr>
<%
    }
%>
<tr>
    <%--<td></td><td></td><td></td>--%><td style="padding-left: 5%" class="textb" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">Paid Amount</td>
    <td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right:7%"><%if(merchantPayoutReportVO.getMerchantWirePaidAmount()>0){out.print("-"+Functions.round(merchantPayoutReportVO.getMerchantWirePaidAmount(),2));}else{out.print(Functions.round(merchantPayoutReportVO.getMerchantWirePaidAmount(),2));}%></td>
</tr>
<tr>
    <%-- <td></td><td></td><td></td>--%><td style="padding-left: 5%" class="textb" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">Previous Balance Amount</td>
    <td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right:7%"><%=Functions.round(merchantPayoutReportVO.getMerchantWireUnpaidAmount(),2)%></td>
</tr>
<tr>
    <%--<td></td><td></td><td></td>--%><td style="padding-left: 5%" class="textb" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">Refunded Rolling Reserve</td>
    <td class="textb" valign="middle" align="right" bgcolor="#f5f5f5" style="padding-right:7%" ><%=Functions.round(merchantPayoutReportVO.getMerchantRollingReleasedAmount(),2)%></td>
</tr>
<tr>
    <%--<td></td><td></td><td></td>--%><td colspan="4" valign="middle" class="texthead" align="center" bgcolor="#008BBA" style="padding-left: 15%"><b>Total Amount Funded</b></td>
    <td class="textb" valign="middle" align="right" class="texthead" style="background-color:#2c3e50;padding-right:7%" ><font color="white"> <b><%=Functions.round(merchantPayoutReportVO.getMerchantTotalFundedAmount(),2)%></b></font></td>
</tr>
<tr></tr>
<tr></tr>
<%
    if(merchantPayoutReportVO.getWireReport())
    {
        session.setAttribute("merchantPayoutReportVO",merchantPayoutReportVO);
        session.setAttribute("entity","merchant");
%>
<tr></tr>
<tr></tr>
<tr>
    <td></td><td></td><td></td><td></td>
    <td align="right">
        <form name="generatewire" id="generatewire" method="post" action="/icici/servlet/GenerateWireServlet?ctoken=<%=ctoken%>">
            <br>
            <input type="button" class="buttonform"  value="Generate Wire" onclick="confirmsubmitreg()" >
        </form>
    </td>
</tr>

<%
    }
%>
</table>
<br>

<%
            }
            else if("success".equals(request.getAttribute("wireStatus")))
            {
                out.println(Functions.NewShowConfirmation("Thank You","Wire Generated Successfully."));
            }
            else if("exists".equals(request.getAttribute("wireStatus")))
            {
                out.println(Functions.NewShowConfirmation("Sorry","This Wire You Have Alleady Generated."));
            }
            else if("failure".equals(request.getAttribute("wireStatus")))
            {
                out.println(Functions.NewShowConfirmation("Sorry","Wire Generated Failure."));
            }
            else
            {
                out.println(Functions.NewShowConfirmation("Sorry","No records found."));
            }


        }
        catch(Exception e)
        {
            logger.error("Sql Exception in listMerchantPayoutReport:",e);
        }
        finally
        {
            Database.closeConnection(conn);

        }
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
    }
%>
<br>
<br>
<br>
</div>
</body>
</html>