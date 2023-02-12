<%@ page import="com.directi.pg.Database" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%@ include file="functions.jsp"%>

<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 1/31/15
  Time: 2:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.manager.vo.SettlementDateVO"%>
<%@ page import="com.manager.vo.payoutVOs.BankPartnerPayoutReportVO" %>
<%@ page import="com.manager.vo.payoutVOs.ChargeDetailsVO" %>
<%@ page import="com.manager.vo.payoutVOs.GrossChargeVO" %>
<%@ page import="com.manager.vo.payoutVOs.WireChargeVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.List" %>
<%!
    Logger logger=new Logger("bankPartnerPayoutReport.jsp");
%>

<html>
<head>
    <META content="text/html; charset=utf-8" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
    <script src="/icici/javascript/gatewaytype-accountid.js"></script>

    <%--Datepicker css format--%>
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: 'yy-mm-dd'});
        });
        function confirmsubmitreq()
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
    </script>
    <%--<script type="text/javascript" language="JavaScript" src="/icici/javascript/accountid.js"></script>--%>
    <title>Reports> Bank Partner Payout Report</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String str2 = "";
        String pgtypeid = "";
        /*String currency= "";
        currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");*/
        String partnerid = Functions.checkStringNull(request.getParameter("partnerid"))==null?"":request.getParameter("partnerid");
        pgtypeid = Functions.checkStringNull(request.getParameter("gateway"))==null?"":request.getParameter("gateway");
        String accountid2 = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");

        List<String> gatewayCurrency = GatewayTypeService.loadCurrency();
        List<String> gatwayName = GatewayTypeService.loadGateway();
        Hashtable accountDetails = GatewayAccountService.getCommonAccountDetail();

        if(pgtypeid!=null)str2 = str2 + "&pgtypeid=" + pgtypeid;
        else
            pgtypeid="";
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>

<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading" >
                Bank Partner Payout Report
            </div>
            <form action="/icici/servlet/GenerateBankPartnerPayoutReport?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <table width="100%">
                    <tr>
                        <td class="text" align="center">
                            <%
                                String errorMsg=(String)request.getAttribute("errormsg");
                                if(errorMsg!=null)
                                {
                                    out.println("<center><font class=\"textb\">" + errorMsg + "</font></center>");
                                }
                            %>
                        </td>
                    </tr>
                </table>
                <table  align="center" width="100%" cellpadding="2" cellspacing="2">
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Partner Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="partnerid" id="pid1" value="<%=partnerid%>" class="txtbox" autocomplete="on">
                                        <%
                                            Connection conn = Database.getConnection();
                                        %>
                                    <%-- <select name="partnerid" class="txtbox"><option value="" selected></option>
                                            <%
                                                Connection conn = Database.getConnection();
                                                String  query = "SELECT partnerId,partnerName FROM partners WHERE activation='T' ORDER BY partnerId ASC";
                                                PreparedStatement pstmt = conn.prepareStatement( query );
                                                ResultSet rs = pstmt.executeQuery();
                                                while (rs.next())
                                                {
                                                    out.println("<option value=\""+rs.getInt("partnerId")+"\">"+rs.getInt("partnerId")+" - "+rs.getString("partnerName")+"</option>");
                                                }
                                            %>
                                        </select>--%>
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="13%" class="textb" >Bank/Gateway Type</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="gateway" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                                    <%--<select size="1" name="gateway" class="txtbox" style="width:216px">
                                            <option value="">All</option>
                                            <%
                                                Hashtable gatewayHash = GatewayTypeService.getGatewayTypes();
                                                Enumeration enu2 = gatewayHash.keys();
                                                String selected2 = "";
                                                String key2 = "";
                                                String value2 = "";
                                                while (enu2.hasMoreElements())
                                                {
                                                    key2 = (String) enu2.nextElement();
                                                    value2 = (String) gatewayHash.get(key2);

                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key2)%>" <%=selected2%>><%=ESAPI.encoder().encodeForHTML(value2)%></option>
                                            <%
                                                }
                                            %>
                                        </select>--%>
                                    </td>

                                    <td width="6%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Report Type</td>
                                    <td width="10%" class="textb">
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
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="13%" class="textb" >Account ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb" colspan="5">
                                        <input name="accountid" id="accountid1" value="<%=accountid2%>" class="txtbox" autocomplete="on">
                                   <%-- <select name="accountid" class="txtbox" style="width: 430px"><option value="" selected></option>
                                            <%
                                                try{

                                                    query = "SELECT * FROM gateway_accounts ORDER BY accountid ASC";
                                                    pstmt = conn.prepareStatement( query );
                                                    rs = pstmt.executeQuery();
                                                    while (rs.next())
                                                    {
                                                        String accountid=rs.getString("accountid");
                                                        GatewayAccount account =GatewayAccountService.gatewayAccounts.get(accountid);
                                                        String currency = account.getCurrency();
                                                        String merchantId = account.getMerchantId();
                                                        String gateway = account.getGateway();
                                                        String value =accountid+"-"+merchantId+"-"+ currency + " ( "+ gateway + " ) ";
                                                        out.println("<option value="+accountid+">"+value+"</option>");
                                                    }
                                            %>
                                        </select>--%>
                                    </td>

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
        try
        {
        BankPartnerPayoutReportVO bankPartnerPayoutReportVO=(BankPartnerPayoutReportVO)request.getAttribute("bankPartnerPayoutReportVO");
        if(bankPartnerPayoutReportVO!=null)
        {
            PartnerDetailsVO partnerDetailsVO=bankPartnerPayoutReportVO.getPartnerDetailsVO();
            List<ChargeDetailsVO> chargesDetailsVOs=bankPartnerPayoutReportVO.getChargesDetailsVOs();
            SettlementDateVO settlementDateVO=bankPartnerPayoutReportVO.getSettlementDateVO();
            GatewayAccount gatewayAccount=bankPartnerPayoutReportVO.getGatewayAccount();
            GatewayType gatewayType=bankPartnerPayoutReportVO.getGatewayType();
            List<GrossChargeVO> grossChargeVOList=bankPartnerPayoutReportVO.getGrossTypeChargeVOList();
            WireChargeVO wireChargeVO=bankPartnerPayoutReportVO.getWireChargeVO();
    %>
    <table align=center width="80%" border="2px" style="border-color:#ffffff ">
        <tr>
            <td  colspan="5" valign="middle" align="center" class="texthead" bgcolor="#008BBA"><b>Bank Partner Details</b></td>

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
            <td class="textb"  style="padding-left: 5%" colspan="3" valign="middle" align="left" bgcolor="#f5f5f5">Bank Name:</td>
            <td class="textb"   colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=gatewayType.getGateway()%></td>
        </tr>
        <tr>
            <td class="textb"  style="padding-left: 5%" colspan="3" valign="middle" align="left" bgcolor="#f5f5f5">Display Name:</td>
            <td class="textb"   colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=gatewayType.getName()%></td>
        </tr>
        <tr>
            <td class="textb"  style="padding-left: 5%" colspan="3" valign="middle" align="left" bgcolor="#f5f5f5">MID:</td>
            <td class="textb"   colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=gatewayAccount.getMerchantId()%></td>
        </tr>
        <tr>
            <td class="textb"  style="padding-left: 5%" colspan="3" valign="middle" align="left" bgcolor="#f5f5f5">Currency:</td>
            <td class="textb"   colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=gatewayType.getCurrency()%></td>
        </tr>
        <tr>
            <td class="textb"  style="padding-left: 5%" colspan="3" valign="middle" align="left" bgcolor="#f5f5f5">Account Id:</td>
            <td class="textb"   colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=gatewayAccount.getAccountId()%></td>
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
            <td valign="middle" style="padding-right:5%" align="right" class="texthead" style="background-color:#2c3e50 "><font color="white"><b><%=Functions.round(bankPartnerPayoutReportVO.getPartnerTotalChargesAmount(),2)%></b></font></td>
        </tr>
        <tr>
            <td style="padding-left: 5%" class="textb" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">Agent Charges Total Amount</td>
            <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=Functions.round(bankPartnerPayoutReportVO.getPartnerTotalChargesAmount(),2)%></td>
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
            for (GrossChargeVO grossChargeVO:grossChargeVOList)
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
            <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%if(bankPartnerPayoutReportVO.getPartnerWirePaidAmount()>0){out.print("-"+Functions.round(bankPartnerPayoutReportVO.getPartnerWirePaidAmount(),2));}else{out.print(Functions.round(bankPartnerPayoutReportVO.getPartnerWirePaidAmount(),2));}%></td>
        </tr>
        <tr>
            <td style="padding-left: 5%" class="textb" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">Previous Balance Amount</td>
            <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=Functions.round(bankPartnerPayoutReportVO.getPartnerTotalBalanceAmount(),2)%></td>
        </tr>
        <tr>
            <td colspan="4" valign="middle" class="texthead" align="left" bgcolor="#008BBA" style="padding-left: 2%"><b>Total Amount Funded</b></td>
            <td class="textb" valign="middle"  align="right" class="texthead" style="background-color:#2c3e50;padding-right:5%" ><font color="white"> <b><%=Functions.round(bankPartnerPayoutReportVO.getPartnerTotalFundedAmount(),2)%></b></font></td>
        </tr>
        <%
            if(bankPartnerPayoutReportVO.isWireReport())
            {
                session.setAttribute("bankPartnerPayoutReportVO",bankPartnerPayoutReportVO);
                session.setAttribute("entity","bankpartner");
        %>
        <tr>
            <td align="right" colspan="5">
                <form name="generatewire" id="generatewire" method="post" action="/icici/servlet/GenerateWireServlet?ctoken=<%=ctoken%>">
                    <br>
                    <input type="button" class="buttonform"  value="Generate Wire" onclick="confirmsubmitreq()" >
                </form>
            </td>
        </tr>
        <%
            }
        %>
    </table>

    <%
                    }
                    else if("success".equals(request.getAttribute("wireStatus")))
                    {
                        out.println(Functions.NewShowConfirmation("Thank You","Bank Partner Wire Generated Successfully."));
                    }
                    else
                    {
                        out.println(Functions.NewShowConfirmation("Sorry","No records found."));
                    }
                }
                catch(Exception e)
                {
                    logger.error("Sql Exception in bankPartnerPayoutReport.jsp",e);
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
    <br>
</div>
</body>
</html>