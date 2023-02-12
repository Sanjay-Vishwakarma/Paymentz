<%@ page import="com.directi.pg.Functions" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ include file="functions.jsp"%>

<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 12/10/14
  Time: 2:59 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Logger"%>
<%@ page import="com.manager.vo.payoutVOs.ChargeDetailsVO" %>
<%@ page import="com.manager.vo.payoutVOs.GrossChargeVO" %>
<%@ page import="com.manager.vo.payoutVOs.WireChargeVO" %>
<%@ page import="com.manager.vo.payoutVOs.AgentPayoutReportVO" %>
<%@ page import="com.manager.vo.AgentDetailsVO" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.SettlementDateVO" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.manager.PayoutManager" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.manager.vo.AgentManager" %>

<%!
    Logger logger=new Logger("agentPayoutReport.jsp");
%>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: 'yy-mm-dd'});
        });

        function confirmsubmitreg()
        {
            var r= window.confirm("Are You Sure To Generate Agent Wire?");
            if(r==true)
            {
                document.getElementById("generatewire").submit();
            }
            else
            {

            }
        }
        /*function commissioncronconfirm()
        {
            var r= window.confirm("Are You Sure To Execute Agent Commission Cron?");
            if(r==true)
            {
                document.getElementById("payoutcronfrm").submit();
            }
            else
            {

            }
        }*/
    </script>
    <title>Reports> Merchant Agent Payout Report</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        Connection conn=null;
        try{
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String str2 = "";
        String pgtypeid = "";
        String currency= "";
        currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");

        pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");

        String accountid = Functions.checkStringNull(request.getParameter("accountid")) == null ? "" : request.getParameter("accountid");
        String memberid = Functions.checkStringNull(request.getParameter("memberid")) == null ? "" : request.getParameter("memberid");


        Map terminalMap = GatewayTypeService.getAllTerminalsGroupByMerchant();

        if(pgtypeid!=null)str2 = str2 + "&pgtypeid=" + pgtypeid;
        else
            pgtypeid="";

        String cardtypeid = request.getParameter("cardtypeid");
        String paymodeid = request.getParameter("paymodeid");
        String agentId=Functions.checkStringNull(request.getParameter("agentid"))==null?"":request.getParameter("agentid");
        PayoutManager payoutManager = new PayoutManager();
        AgentManager agentManager = new AgentManager();
        List<TerminalVO> cardtypeList = payoutManager.loadcardtypeids();
        List<TerminalVO> paymenttypeList = payoutManager.loadPaymodeids();

        TerminalManager terminalManager = new TerminalManager();
        List<TerminalVO> terminalList = terminalManager.getAllMappedTerminals();
        List<AgentDetailsVO> agentList = agentManager.getAgentDetails();
        //List<GatewayType> gatewayTypes = GatewayTypeService.getAllGatewayTypes();
        TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();
        TreeMap<String, GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();
        TreeMap<String,TerminalVO> memberMap = new TreeMap<String, TerminalVO>();

        for(TerminalVO terminalVO : terminalList)
        {
            String memberKey = terminalVO.getMemberId()+"-"+terminalVO.getAccountId()+"-"+terminalVO.getGateway().toUpperCase() + "-" + terminalVO.getCurrency() + "-" + terminalVO.getGateway_id();
            memberMap.put(memberKey,terminalVO);
        }


%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading" >
                Merchant Agent Payout Report
                <div style="float: right;">
                    <form  id="payoutcronfrm" action="/icici/agentcommissioncron.jsp?ctoken=<%=ctoken%>" method="POST">
                        <input type="submit"  class="buttonform"  value="Payout Cron ">
                    </form>
                </div>
                <div style="float: right;margin-right:5px;">
                    <form action="/icici/agentPayoutReport.jsp?ctoken=<%=ctoken%>" method="POST">
                        <input type="submit"  class="buttonform"  value="Payout Report">
                    </form>
                </div>
            </div>
            <form action="/icici/servlet/GenerateAgentPayoutServlet?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
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
                <table  align="center" width="100%" cellpadding="2" cellspacing="2">
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="97%"  align="center">
                                <tr><td colspan="4">&nbsp;</td></tr>


                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="13%" class="textb" >Gateway/Account</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                                    <%--<select size="1" id="bank" name="pgtypeid" class="txtbox"  style="width:200px;">
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
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Account ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
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

                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Member ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="memberid" id="memberid1" value="<%=memberid%>" class="txtbox" autocomplete="on">
                                    <%--  <select name="memberid" id="memberid" class="txtbox" style="width:200px;">
                                            <option data-bank="all"  data-accid="all" value="0" selected>--Select MemberID--</option>
                                            <%

                                                for(String sMemberId: memberMap.keySet())
                                                {

                                                    TerminalVO t = memberMap.get(sMemberId);
                                                    String aGateway = memberMap.get(sMemberId).getGateway().toUpperCase()+"-"+memberMap.get(sMemberId).getCurrency()+"-"+memberMap.get(sMemberId).getGateway_id();
                                                    String accId = t.getAccountId();
                                                    String aContactPerson = t.getContactPerson();
                                                    String aCompanyName =t.getCompany_name();
                                                    String gateway2 = t.getGateway().toUpperCase();
                                                    String currency2 = t.getCurrency();
                                                    String pgtype = t.getGateway_id();
                                                    String value = gateway2+"-"+currency2+"-"+pgtype;
                                                    String isSelected = "";
                                                    if(String.valueOf(t.getMemberId()+"-"+accId).equalsIgnoreCase(memberid + "-" + accId))
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
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Agent Id*</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="agentid" id="agnt" value="<%=agentId%>" class="txtbox" autocomplete="on">
                                        <%--<select name="agentid" class="txtbox">
                                            <option value="" selected>---ALL--</option>
                                            <%
                                                String isSelected="";
                                                for(AgentDetailsVO agentvo : agentList)
                                                {
                                                    if (agentvo.getAgentId().equalsIgnoreCase(agentid))
                                                    {
                                                        isSelected = "selected";
                                                    }
                                                    else
                                                    {
                                                        isSelected = "";
                                                    }

                                            %>
                                            <option  value="<%=agentvo.getAgentId()%>" <%=isSelected%>>
                                                <%=agentvo.getAgentId()%>-<%=agentvo.getAgentName()%></option>
                                            <%
                                                }
                                            %>

                                        </select>--%>
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Card Type</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select size="1" class="txtbox" name="cardtype">
                                            <option value="" selected>Select CardType</option>
                                            <%
                                                for(TerminalVO terminalVO:cardtypeList)
                                                {
                                                    String isSelected1="";
                                                    if(terminalVO.getCardTypeId().equalsIgnoreCase(cardtypeid))
                                                        isSelected1="selected";
                                                    else
                                                        isSelected1="";
                                            %>
                                            <option value="<%=terminalVO.getCardTypeId()%>" <%=isSelected1%>><%=terminalVO.getCardType()%></option>
                                            <%
                                                }
                                            %>
                                        </select>

                                    </td>
                                    <td width="6%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Paymode</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <select size="1" class="txtbox" name="paymode">
                                            <option value="" selected>Select PaymentType</option>
                                            <%
                                                for(TerminalVO terminalVO:paymenttypeList)
                                                {
                                                    String isSelected2="";
                                                    if(terminalVO.getPaymodeId().equalsIgnoreCase(paymodeid))
                                                        isSelected2="selected";
                                                    else
                                                        isSelected2="";
                                            %>
                                            <option value="<%=terminalVO.getPaymodeId()%>" <%=isSelected2%>><%=terminalVO.getPaymentTypeName()%>
                                            </option>
                                            <%
                                                }
                                            %>
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
                                    <td width="6%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Report Type</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <select name="reporttype" class="txtbox">
                                            <option value="summary">Summary</option>
                                            <%--<option value="wirereport">WireReport</option>--%>
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
                                   <%-- <td width="2%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Card type</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select name="cardtype" class="txtbox"><option value="" selected></option>
                                            <%
                                                try
                                                {
                                                    query ="SELECT cardtypeid, cardType FROM card_type ORDER BY cardtypeid ASC";
                                                    pstmt = conn.prepareStatement( query );
                                                    rs = pstmt.executeQuery();
                                                    while (rs.next()){
                                                        out.println("<option value=\""+rs.getString("cardtypeid")+"\">"+rs.getString("cardType")+"</option>");
                                                    }

                                            %>
                                        </select>
                                    </td>--%>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" colspan="2"></td>
                                    <td width="12%" class="textb">
                                    </td>

                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" ></td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <button type="submit" class="buttonform" value="View Payout Report" style="width: 158px;">
                                            <i class="fa fa-sign-in"></i>
                                            &nbsp;&nbsp;View Payout Report
                                        </button>
                                    </td>
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

        AgentPayoutReportVO agentPayoutReportVO=(AgentPayoutReportVO)request.getAttribute("agentPayoutReportVO");

        if(agentPayoutReportVO!=null)
        {
            AgentDetailsVO agentDetailsVO=agentPayoutReportVO.getAgentDetailsVO();
            TerminalVO terminalVO=agentPayoutReportVO.getTerminalVO();
            SettlementDateVO settlementDateVO=agentPayoutReportVO.getSettlementDateVO();
            WireChargeVO wireChargeVO=agentPayoutReportVO.getWireChargeVO();
            List<GrossChargeVO> grossTypeChargeVOList=agentPayoutReportVO.getGrossTypeChargeVOList();
            List<ChargeDetailsVO> chargesDetailsVOs=agentPayoutReportVO.getChargesDetailsVOs();

            session.setAttribute("agentPayoutReportVO",agentPayoutReportVO);
            session.setAttribute("entity","agent");
    %>
    <table align=center width="80%" border="2px" style="border-color:#ffffff ">
        <tr>
            <td  colspan="5" valign="middle" align="center" class="texthead" bgcolor="#008BBA"><b>Agent Details</b></td>

        </tr>
        <tr>
            <td colspan="3" style="padding-left: 5%" valign="middle" align="left" class="textb" bgcolor="#f5f5f5">Agent ID:</td>
            <td colspan="2" valign="middle" align="left" class="textb" bgcolor="#f5f5f5"><%=agentDetailsVO.getAgentId()%></td>
        </tr>
        <tr>
            <td colspan="3" style="padding-left: 5%" valign="middle" align="left" class="textb" bgcolor="#f5f5f5">Company Name:</td>
            <td colspan="2" valign="middle" align="left" class="textb" bgcolor="#f5f5f5"><%=agentDetailsVO.getAgentName()%></td>
        </tr>
        <tr>
            <td class="textb"  style="padding-left: 5%" colspan="3" valign="middle" align="left" bgcolor="#f5f5f5">Contact Person:</td>
            <td class="textb"   colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=agentDetailsVO.getContactPerson()%></td>
        </tr>
        <tr>
            <td colspan="3" style="padding-left: 5%" valign="middle" align="left" class="textb" bgcolor="#f5f5f5">Member ID:</td>
            <td colspan="2" valign="middle" align="left" class="textb" bgcolor="#f5f5f5"><%=terminalVO.getMemberId()%></td>
        </tr>
        <tr>
            <td colspan="3" style="padding-left: 5%" valign="middle" align="left" class="textb" bgcolor="#f5f5f5">Terminal ID:</td>
            <td colspan="2" valign="middle" align="left" class="textb" bgcolor="#f5f5f5"><%=terminalVO.getTerminalId()%></td>
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
            <td class="textb"   colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=agentPayoutReportVO.getCurrency()%></td>
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
            for(ChargeDetailsVO chargeDetailsVO:chargesDetailsVOs)
            {
        %>
        <tr>
            <td class="textb"valign="middle"  style="padding-left: 5%" align="left"  bgcolor="#f5f5f5"><%=chargeDetailsVO.getChargeName()%></td>
            <td class="textb" valign="middle" style="padding-left: 7%" align="left" bgcolor="#f5f5f5"><%=chargeDetailsVO.getChargeValue()%></td>
            <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=chargeDetailsVO.getCount()%></td>
            <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=Functions.round(chargeDetailsVO.getAmount(),2)%></td>
            <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=Functions.round(chargeDetailsVO.getTotal(),2)%></td>
        </tr>
        <%
            }

        %>
        <tr>
            <td colspan="4" valign="middle" class="texthead" align="right" bgcolor="#008BBA"><b>Total</b></td>
            <td valign="middle" style="padding-right:5%" align="right" class="texthead" style="background-color:#2c3e50 "><font color="white"><b><%=Functions.round(agentPayoutReportVO.getAgentTotalChargesAmount(),2)%></b></font></td>
        </tr>
        <tr>
            <td style="padding-left: 5%" class="textb" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">Agent Charges Total Amount</td>
            <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=Functions.round(agentPayoutReportVO.getAgentTotalChargesAmount(),2)%></td>
        </tr>

        <%
            if(wireChargeVO!=null)
            {%>
        <tr>
            <td class="textb" valign="middle" style="padding-left: 5%" align="left"  bgcolor="#f5f5f5"><%=wireChargeVO.getChargeName()%></td>
            <td class="textb" valign="middle" style="padding-left: 7%" align="left"  bgcolor="#f5f5f5"><%=wireChargeVO.getChargeValue()%></td>
            <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=wireChargeVO.getCount()%></td>
            <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%="-"%></td>
            <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%if(wireChargeVO.getTotal()>0){out.print("-"+Functions.round(wireChargeVO.getTotal(),2));}else{out.print(Functions.round(wireChargeVO.getTotal(),2));}%></td>
        </tr>
        <%}
        %>

        <%


            for (GrossChargeVO grossChargeVO:grossTypeChargeVOList)
            {


        %>
        </tr>
        <td class="textb" valign="middle" style="padding-left: 5%" align="left"  bgcolor="#f5f5f5"><%=grossChargeVO.getChargeName()%></td>
        <td class="textb" valign="middle" style="padding-left: 7%" align="left"  bgcolor="#f5f5f5"><%=grossChargeVO.getChargeValue()%></td>
        <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=grossChargeVO.getCount()%></td>
        <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=Functions.round(grossChargeVO.getAmount(),2)%></td>
        <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%if(grossChargeVO.getTotal()>0){out.print("-"+Functions.round(grossChargeVO.getTotal(),2));}else{out.print(Functions.round(grossChargeVO.getTotal(),2));}%></td>
        </tr>
        <%
            }
        %>
        <tr>
            <td style="padding-left: 5%" class="textb" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">Paid Amount</td>
            <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%if(agentPayoutReportVO.getAgentWirePaidAmount()>0){out.print("-"+Functions.round(agentPayoutReportVO.getAgentWirePaidAmount(),2));}else{out.print(Functions.round(agentPayoutReportVO.getAgentWirePaidAmount(),2));}%></td>
        </tr>
        <tr>
            <td style="padding-left: 5%" class="textb" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">Previous Balance Amount</td>
            <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=Functions.round(agentPayoutReportVO.getAgentTotalBalanceAmount(),2)%></td>
        </tr>
        <tr>
            <td colspan="4" valign="middle" class="texthead" align="left" bgcolor="#008BBA"><b>Total Amount Funded</b></td>
            <td class="textb" valign="middle"  align="right" class="texthead" style="background-color:#2c3e50;padding-right:5%" ><font color="white"> <b><%=Functions.round(agentPayoutReportVO.getAgentTotalFundedAmount(),2)%></b></font></td>
        </tr>
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

    </table>
    <br>
    <%
                }
                else if("success".equals(request.getAttribute("wireStatus")))
                {
                    out.println(Functions.NewShowConfirmation("Thank You","Agent Wire Generated Successfully."));
                }
                else if("exists".equals(request.getAttribute("wireStatus")))
                {
                    out.println(Functions.NewShowConfirmation("Sorry","This Wire You Have Already Generated."));
                }
                else if("failure".equals(request.getAttribute("wireStatus")))
                {
                    out.println(Functions.NewShowConfirmation("Sorry","Agent Wire Generated Failure."));
                }
                else
                {
                    out.println(Functions.NewShowConfirmation("Sorry","No Records Found."));
                }
            }

            }




            catch(Exception e)
            {
                logger.error("Sql Exception in agentPayoutReport.jsp:",e);
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



    %>
    <br><br><br><br><br><br><br><br><br><br>
</div>
</body>
</html>