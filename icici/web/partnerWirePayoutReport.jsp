<%@ page import="com.directi.pg.Database" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ page import="com.manager.vo.SettlementDateVO" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 1/12/14
  Time: 8:39 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.payoutVOs.ChargeDetailsVO" %>
<%@ page import="com.manager.vo.payoutVOs.GrossChargeVO" %>
<%@ page import="com.manager.vo.payoutVOs.PartnerPayoutReportVO" %>
<%@ page import="com.manager.vo.payoutVOs.WireChargeVO" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.*" %>
<%!
    Logger logger=new Logger("partnerWirePayoutReport.jsp");
%>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>

    <script type="text/javascript" language="JavaScript" src="/icici/javascript/memberid_filter.js"></script>
    <%--Datepicker css format--%>
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: 'yy-mm-dd'});
        });
        function confirmsubmitreg()
        {
            var r= window.confirm("Are You Sure To Generate Partner Wire?");
            if(r==true)
            {
                document.getElementById("partnerwire").submit();
            }
            else
            {

            }
        }
        function commissioncronconfirm()
        {
            var r= window.confirm("Are You Sure To Execute Partner Commission Cron?");
            if(r==true)
            {
                document.getElementById("payoutcronfrm").submit();
            }
            else
            {

            }
        }
    </script>
    <title>Reports> Merchant Partner Payout Report</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
        if (com.directi.pg.Admin.isLoggedIn(session))
        {
            String str2 = "";
            String pgtypeid = "";
            String currency = "";
            currency = Functions.checkStringNull(request.getParameter("currency")) == null ? "" : request.getParameter("currency");
            pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid")) == null ? "" : request.getParameter("pgtypeid");
            String accountid = Functions.checkStringNull(request.getParameter("accountid")) == null ? "" : request.getParameter("accountid");
            String memberid = Functions.checkStringNull(request.getParameter("memberid")) == null ? "" : request.getParameter("memberid");
            String mailevent = Functions.checkStringNull(request.getParameter("mailevent"));
            String partnerId = Functions.checkStringNull(request.getParameter("partnerid"))==null?"":request.getParameter("partnerid");

            Map terminalMap = GatewayTypeService.getAllTerminalsGroupByMerchant();
            if (pgtypeid != null) str2 = str2 + "&pgtypeid=" + pgtypeid;
            else
                pgtypeid = "";
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading" >
                Merchant Partner Payout Report
                <div style="float: right;">
                    <form  id="payoutcronfrm" action="/icici/partnercommissioncron.jsp?ctoken=<%=ctoken%>" method="POST">
                        <input type="button"  class="buttonform"  value="Payout Cron " onclick="commissioncronconfirm()">
                    </form>
                </div>
                <div style="float: right;margin-right:5px;">
                    <form action="/icici/partnerWirePayoutReport.jsp?ctoken=<%=ctoken%>" method="POST">
                        <input type="submit"  class="buttonform"  value="Payout Report">
                    </form>
                </div>
            </div>
            <form action="/icici/servlet/GeneratePartnerPayoutServlet?ctoken=<%=ctoken%>" method="post" name="forms" >
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

                                        <%
                                            Set<String> gatewaySet = new HashSet<String>();
                                            Set<String> currencySet = new HashSet<String>();
                                            Set<TerminalVO> accountSet = new HashSet<TerminalVO>();
                                            Set<String> memberSet = new HashSet<String>();
                                        %>
                                    <%--<select size="1" id="groups" name="pgtypeid" class="txtbox"  style="width:200px;">
                                            <option value="--All--" default>--All--</option>
                                            <%
                                                Set<String> gatewaySet = new HashSet<String>();
                                                Set<String> currencySet = new HashSet<String>();
                                                Set<TerminalVO> accountSet = new HashSet<TerminalVO>();
                                                Set<String> memberSet = new HashSet<String>();

                                                for(Object key : terminalMap.keySet())
                                                {
                                                    for(TerminalVO t : (List<TerminalVO>)terminalMap.get(key))
                                                    {
                                                        gatewaySet.add(t.getGateway());
                                                        currencySet.add(t.getCurrency());
                                                        accountSet.add(t);
                                                        memberSet.add(t.getMemberId());
                                                    }
                                                }
                                                Iterator gatewayIterator = gatewaySet.iterator();
                                                String gateway = "";
                                                while(gatewayIterator.hasNext())
                                                {
                                                    gateway = (String)gatewayIterator.next();
                                                    String isSelected = "";
                                                    if(pgtypeid.equalsIgnoreCase(gateway))
                                                    {
                                                        isSelected = "selected";
                                                    }

                                            %>
                                            <option value="<%=gateway.toUpperCase()%>" <%=isSelected%>><%=gateway.toUpperCase()%></option>
                                            <%

                                                }

                                            %>

                                        </select>--%>
                                    </td>
                                    <td width="6%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >AccountID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <input name="accountid" id="accountid1" value="<%=accountid%>" class="txtbox" autocomplete="on">
                                    <%-- <select size="1" id="sub_groups" name="accountid" class="txtbox" style="width: 200px">
                                            <option data-bank="all" data-curr="all" value="0">Select AccountID</option>
                                            <%
                                                Set accSet = terminalMap.keySet();
                                                Iterator accountIterator = accSet.iterator();
                                                //String sCurrency = "";
                                                while(accountIterator.hasNext())
                                                {
                                                    String accId = (String)accountIterator.next();
                                                    GatewayAccount t = GatewayAccountService.getGatewayAccount(accId);
                                                    String isSelected = "";

                                                    String aGateway = t.getGateway();
                                                    String aCurrency = t.getCurrency();
                                                    //String accId = t.getAccountId();
                                                    String aMid = t.getMerchantId();

                                                    //TerminalVO t = (TerminalVO)accountIterator.next();
                                                    if(accId.equalsIgnoreCase(accountid))
                                                    {
                                                        isSelected = "selected";
                                                    }
                                            %>
                                            <option data-group="<%=aGateway.toUpperCase()%>" data-curr="<%=aCurrency%>" value="<%=accId%>" <%=isSelected%>><%=accId+"-"+"-"+aMid+"-"+aCurrency+"-"+aGateway%></option>
                                            <%
                                                }
                                            %>
                                        </select>--%>
                                    </td>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Member Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="memberid" id="memberid1" value="<%=memberid%>" class="txtbox" autocomplete="on">

                                        <%-- <select name="memberid" id="member_groups" class="txtbox" style="width:200px;">
                                                <option value="0" selected>--Select MemberID--</option>
                                                <%
                                                    try
                                                    {
                                                        Iterator memberIterator = accountSet.iterator();
                                                        //String sCurrency = "";
                                                        while (memberIterator.hasNext())
                                                        {
                                                            //System.out.println("----"+t.getMemberId()+"-"+t.getGateway());
                                                            TerminalVO t = (TerminalVO) memberIterator.next();
                                                            String aGateway = t.getGateway();
                                                            String aCurrency = t.getCurrency();
                                                            String accId = t.getAccountId();
                                                            String aMemberId = t.getMemberId();
                                                            String aContactPerson = t.getContactPerson();
                                                            String aCompanyName = t.getCompany_name();
                                                            String isSelected = "";
                                                            if (aMemberId.equals(memberid))
                                                            {
                                                                isSelected = "selected";
                                                            }
                                                %>
                                                <option data-group="<%=aGateway.toUpperCase()%>" data-curr="<%=aCurrency%>" data-accid="<%=accId%>" value="<%=aMemberId%>" <%=isSelected%>><%=aMemberId+"-"+aCompanyName%></option>
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
                                    <td width="11%" class="textb" >Currency</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select size="1" id="currency" class="txtbox" name="currency" style="width: 200px">
                                            <option value="--All--" default>--All--</option>
                                            <%
                                                Iterator currencyIterator = currencySet.iterator();
                                                String sCurrency = "";
                                                while(currencyIterator.hasNext())
                                                {
                                                    sCurrency = (String)currencyIterator.next();
                                                    String isSelected = "";
                                                    if(currency.equalsIgnoreCase(sCurrency))
                                                    {
                                                        isSelected = "selected";
                                                    }
                                            %>
                                            <option value="<%=sCurrency%>" <%=isSelected%>><%=sCurrency%></option>
                                            <%
                                                }
                                            %>
                                        </select>

                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Partner Id*</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="partnerid" id="pid1" value="<%=partnerId%>" class="txtbox" autocomplete="on">
                                        <%--<select name="partnerid" class="txtbox"><option value="" selected></option>
                                            <%
                                                Connection conn = null;
                                                PreparedStatement pstmt = null;
                                                ResultSet rs = null;
                                                try
                                                {
                                                    conn = Database.getConnection();
                                                    String query = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
                                                    pstmt = conn.prepareStatement(query);
                                                    rs = pstmt.executeQuery();
                                                    while (rs.next())
                                                    {
                                                        out.println("<option value=\"" + rs.getInt("partnerId") + "\">" + rs.getInt("partnerId") + " - " + rs.getString("partnerName") + "</option>");
                                                    }
                                                }
                                                catch (Exception e)
                                                {
                                                    logger.error("Exception:::::::" + e);
                                                }
                                                finally
                                                {
                                                    Database.closeConnection(conn);
                                                }
                                            %>
                                        </select>--%>
                                    </td>
                                    <td width="6%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Paymode</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <select name="paymode" class="txtbox"><option value="" selected></option>
                                            <%
                                                Connection conn = null;
                                                PreparedStatement pstmt = null;
                                                ResultSet rs = null;
                                                try
                                                {
                                                    conn = Database.getConnection();
                                                    String query = "SELECT paymodeid, paymentType FROM payment_type ORDER BY paymodeid ASC";
                                                    pstmt = conn.prepareStatement(query);
                                                    rs = pstmt.executeQuery();
                                                    while (rs.next())
                                                    {
                                                        out.println("<option value=\"" + rs.getString("paymodeid") + "\">" + rs.getString("paymentType") + "</option>");
                                                    }
                                                }
                                                catch (Exception e)
                                                {
                                                    logger.error("Exception:::::::" + e);
                                                }
                                                finally
                                                {
                                                    Database.closeConnection(conn);
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

                                </tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" colspan="2"></td>
                                    <td width="12%" class="textb">
                                    </td>


                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">CardType</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <select name="cardtype" class="txtbox"><option value="" selected></option>
                                            <%
                                                try
                                                {
                                                    conn = Database.getConnection();
                                                    String query = "SELECT cardtypeid, cardType FROM card_type ORDER BY cardtypeid ASC";
                                                    pstmt = conn.prepareStatement(query);
                                                    rs = pstmt.executeQuery();
                                                    while (rs.next())
                                                    {
                                                        out.println("<option value=\"" + rs.getString("cardtypeid") + "\">" + rs.getString("cardType") + "</option>");
                                                    }
                                                }
                                                catch (Exception e)
                                                {
                                                    logger.error("Exception:::::::" + e);
                                                }
                                                finally
                                                {
                                                    Database.closeConnection(conn);
                                                }
                                            %>
                                        </select>
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

        PartnerPayoutReportVO partnerPayoutReportVO=(PartnerPayoutReportVO)request.getAttribute("partnerPayoutReportVO");
        if(partnerPayoutReportVO!=null)
        {

            PartnerDetailsVO partnerDetailsVO=partnerPayoutReportVO.getPartnerDetailsVO();
            TerminalVO terminalVO=partnerPayoutReportVO.getTerminalVO();
            SettlementDateVO settlementDateVO=partnerPayoutReportVO.getSettlementDateVO();
            WireChargeVO wireChargeVO=partnerPayoutReportVO.getWireChargeVO();
            List<GrossChargeVO> grossTypeChargeVOList=partnerPayoutReportVO.getGrossTypeChargeVOList();
            List<ChargeDetailsVO> chargesDetailsVOs=partnerPayoutReportVO.getChargesDetailsVOs();

            session.setAttribute("partnerPayoutReportVO",partnerPayoutReportVO);
            session.setAttribute("entity","partner");
    %>
    <table align=center width="80%" border="2px" style="border-color:#ffffff ">
        <tr>
            <td  colspan="5" valign="middle" align="center" class="texthead" bgcolor="#008BBA"><b>Partner Details</b></td>

        </tr>
        <tr>
            <td colspan="3" style="padding-left: 5%" valign="middle" align="left" class="textb" bgcolor="#f5f5f5">Partner ID:</td>
            <td colspan="2" valign="middle" align="left" class="textb" bgcolor="#f5f5f5"><%=partnerDetailsVO.getPartnerId()%></td>
        </tr>
        <tr>
            <td colspan="3" style="padding-left: 5%" valign="middle" align="left" class="textb" bgcolor="#f5f5f5">Company Name:</td>
            <td colspan="2" valign="middle" align="left" class="textb" bgcolor="#f5f5f5"><%=partnerDetailsVO.getCompanyName()%></td>
        </tr>
        <tr>
            <td class="textb"  style="padding-left: 5%" colspan="3" valign="middle" align="left" bgcolor="#f5f5f5">Contact Person:</td>
            <td class="textb"   colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=partnerDetailsVO.getContactPerson()%></td>
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
            <td class="textb"   colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=partnerPayoutReportVO.getCurrency()%></td>
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
            <td valign="middle" style="padding-right:5%" align="right" class="texthead" style="background-color:#2c3e50 "><font color="white"><b><%=Functions.round(partnerPayoutReportVO.getPartnerTotalChargesAmount(),2)%></b></font></td>
        </tr>
        <tr>
            <td style="padding-left: 5%" class="textb" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">Partner Charges Total Amount</td>
            <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=Functions.round(partnerPayoutReportVO.getPartnerTotalChargesAmount(),2)%></td>
        </tr>
        <%
            if(wireChargeVO!=null)
            {%>
        <tr>
            <td class="textb" valign="middle" style="padding-left: 5%" align="left"  bgcolor="#f5f5f5"><%=wireChargeVO.getChargeName()%></td>
            <td class="textb" valign="middle" style="padding-left: 7%" align="left"  bgcolor="#f5f5f5"><%=wireChargeVO.getChargeValue()%></td>
            <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=wireChargeVO.getCount()%></td>
            <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%="-"%></td>
            <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%="-"+Functions.round(wireChargeVO.getTotal(),2)%></td>
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
            <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%if(partnerPayoutReportVO.getPartnerWirePaidAmount()>0){out.print("-"+Functions.round(partnerPayoutReportVO.getPartnerWirePaidAmount(),2));}else{out.print(Functions.round(partnerPayoutReportVO.getPartnerWirePaidAmount(),2));}%></td>
        </tr>
        <tr>
            <td style="padding-left: 5%" class="textb" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">Previous Balance Amount</td>
            <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=Functions.round(partnerPayoutReportVO.getPartnerTotalBalanceAmount(),2)%></td>
        </tr>
        <tr>
            <td colspan="4" valign="middle" class="texthead" align="left" bgcolor="#008BBA"><b>Total Amount Funded</b></td>
            <td class="textb" valign="middle"  align="right" class="texthead" style="background-color:#2c3e50;padding-right:5%" ><font color="white"> <b><%=Functions.round(partnerPayoutReportVO.getPartnerTotalFundedAmount(),2)%></b></font></td>
        </tr>
        <tr></tr>
        <tr></tr>
        <tr>
            <td></td><td></td><td></td><td></td>
            <td align="right">
                <form name="partnerwire" id="partnerwire" method="post" action="/icici/servlet/GenerateWireServlet?ctoken=<%=ctoken%>">
                    <br>
                    <input type="button" class="buttonform"  value="Generate Wire" onclick="confirmsubmitreg()" >
                </form>
            </td>
        </tr>

    </table>
    <br>

    <%

                    }
                    else if ("success".equals(request.getAttribute("wireStatus")))
                    {
                        out.println(Functions.NewShowConfirmation("Thank You", "Partner Wire Generated Successfully."));
                    }
                    else if ("exists".equals(request.getAttribute("wireStatus")))
                    {
                        out.println(Functions.NewShowConfirmation("Sorry", "This Wire You Have Allready Generated."));
                    }
                    else if ("failure".equals(request.getAttribute("wireStatus")))
                    {
                        out.println(Functions.NewShowConfirmation("Sorry", "Partner Wire Generated Failure."));
                    }
                    else
                    {
                        out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
                    }


                }
              /*  catch (Exception e)
                {
                    logger.error("Sql Exception in partnerWirePayoutReport.jsp:", e);
                }
                finally
                {


                }*/
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