<%@ page import="com.directi.pg.Functions" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ include file="functions.jsp"%>

<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 1/31/15
  Time: 2:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.directi.pg.Logger"%>
<%@ page import="com.manager.vo.payoutVOs.ChargeDetailsVO" %>
<%@ page import="com.manager.vo.AgentDetailsVO" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.manager.vo.payoutVOs.BankAgentPayoutReportVO" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.manager.vo.payoutVOs.GrossChargeVO" %>
<%@ page import="com.manager.vo.payoutVOs.WireChargeVO" %>
<%@ page import="com.manager.vo.SettlementDateVO" %>
<%@ page import="java.sql.SQLException" %>
<%!
    Logger logger=new Logger("bankAgentPayoutReport.jsp");
%>

<html>
<head>
    <META content="text/html; charset=utf-8" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

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
    <script type="text/javascript" language="JavaScript" src="/icici/javascript/accountid.js"></script>
    <title>Bank Agent Payout Report</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String str2 = "";
        String pgtypeid = "";
        String currency= "";
        currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");
        pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
        String accountid2 = Functions.checkStringNull(request.getParameter("accountid"));

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
                Bank Agent Payout Report
            </div>
            <form action="/icici/servlet/GenerateBankAgentPayoutReport?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
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
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Agent Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select name="agentid" class="txtbox"><option value="" selected></option>
                                            <%
                                                Connection conn=null;
                                                try
                                                {
                                                    conn = Database.getConnection();
                                                    String query = "SELECT agentid,agentName FROM agents WHERE activation='T' ORDER BY agentid ASC";
                                                    PreparedStatement pstmt = conn.prepareStatement(query);
                                                    ResultSet rs = pstmt.executeQuery();
                                                    while (rs.next())
                                                    {
                                                        out.println("<option value=\"" + rs.getInt("agentid") + "\">" + rs.getInt("agentid") + " - " + rs.getString("agentName") + "</option>");
                                                    }
                                                }
                                                catch(SQLException e)
                                                {
                                                    logger.error("SQL Exception occured");
                                                }
                                                finally
                                                {
                                                    Database.closeConnection(conn);
                                                }
                                            %>
                                        </select>

                                    </td>
                                    <td width="6%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Report Type</td>
                                 <%--   <td width="3%" class="textb"></td>--%>
                                    <td width="10%" class="textb">
                                        <select name="reporttype" class="txtbox">
                                            <option value="summary">Summary</option>
                                            <option value="wirereport">WireReport</option>
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
                                    <td width="13%" class="textb" >Gateway Account</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select size="1" id="bank" class="txtbox" name="pgtypeid">
                                            <option value="--All--" default>--All--</option>
                                            <%
                                                StringBuilder sb = new StringBuilder();
                                                for(String gatewayType : gatwayName)
                                                {
                                                    String st = "";
                                                    String name = gatewayType;
                                                    if(name != null)
                                                    {
                                                        if(pgtypeid.equalsIgnoreCase(gatewayType))
                                                            st = "<option value='" + gatewayType + "'selected>" + gatewayType.toUpperCase() + "</option>";
                                                        else
                                                            st = "<option value='" + gatewayType + "'>" + gatewayType.toUpperCase() + "</option>";
                                                        sb.append(st);
                                                    }
                                                }
                                            %>
                                            <%=sb.toString()%>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="13%" class="textb" >Currency</td>
                                    <td width="12%" class="textb">
                                        <select size="1" id="currency" class="txtbox" name="currency">
                                            <option value="--All--" default>--All--</option>
                                            <%
                                                StringBuilder sb1 = new StringBuilder();
                                                for(String currency2 : gatewayCurrency)
                                                {
                                                    String st = "";
                                                    String name = currency2;
                                                    if(name != null)
                                                    {
                                                        if(currency.equalsIgnoreCase(currency2))
                                                            st = "<option value='" + currency2 + "'selected>" + currency2 + "</option>";
                                                        else
                                                            st = "<option value='" + currency2 + "'>" + currency2 + "</option>";
                                                        sb1.append(st);
                                                    }

                                                }
                                            %>
                                            <%=sb1.toString()%>
                                        </select>
                                    </td>
                                    <td width="6%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Accounts</td>
                                    <%--   <td width="3%" class="textb"></td>--%>
                                    <td width="10%" class="textb">
                                        <select size="1" id="accountid" name="accountid" class="txtbox">
                                            <option data-bank="all" data-curr="all" value="0">Select AccountID</option>
                                            <%
                                                try{
                                                TreeSet accountSet = new TreeSet<Integer>();
                                                accountSet.addAll(accountDetails.keySet());
                                                Iterator enu3 = accountSet.iterator();
                                                String selected3 = "";
                                                GatewayAccount value3 = null;
                                                while (enu3.hasNext())
                                                {
                                                    value3 = (GatewayAccount)accountDetails.get(enu3.next());
                                                    int acId = value3.getAccountId();
                                                    String currency2 = value3.getCurrency();
                                                    String mid = value3.getMerchantId();
                                                    String gateway2 = value3.getGateway();
                                                    String gatewayName = value3.getGatewayName();
                                                    //newly added
                                                    if (String.valueOf(acId).equals(accountid2))
                                                        selected3 = "selected";
                                                    else
                                                        selected3 = "";

                                            %>
                                            <option data-bank="<%=value3.getGateway()%>" data-curr="<%=value3.getCurrency()%>" value="<%=value3.getAccountId()%>" <%=selected3%>><%=acId+"-"+currency2+"-"+mid+"-"+gateway2+"-"+gatewayName%></option>
                                            <%
                                                }
                                            %>
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
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">&nbsp;
                                        <button type="submit" class="buttonform" value="View Payout Report" style="width: 158px;  margin-left: 165%">
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

    BankAgentPayoutReportVO bankAgentPayoutReportVO=(BankAgentPayoutReportVO)request.getAttribute("bankAgentPayoutReportVO");
    if(bankAgentPayoutReportVO!=null)
    {
        AgentDetailsVO agentDetailsVO=bankAgentPayoutReportVO.getAgentDetailsVO();
        List<ChargeDetailsVO> chargesDetailsVOs=bankAgentPayoutReportVO.getChargesDetailsVOs();
        SettlementDateVO settlementDateVO=bankAgentPayoutReportVO.getSettlementDateVO();
        GatewayAccount gatewayAccount=bankAgentPayoutReportVO.getGatewayAccount();
        GatewayType gatewayType=bankAgentPayoutReportVO.getGatewayType();
        List<GrossChargeVO> grossChargeVOList=bankAgentPayoutReportVO.getGrossTypeChargeVOList();
        WireChargeVO wireChargeVO=bankAgentPayoutReportVO.getWireChargeVO();

%>
<table align=center width="80%" border="2px" style="border-color:#ffffff ">
    <tr>
        <td  colspan="5" valign="middle" align="center" class="texthead" bgcolor="#008BBA"><b>Bank Agent Details</b></td>

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
        <td valign="middle" style="padding-right:5%" align="right" class="texthead" style="background-color:#2c3e50 "><font color="white"><b><%=Functions.round(bankAgentPayoutReportVO.getAgentTotalChargesAmount(),2)%></b></font></td>
    </tr>
    <tr>
        <td style="padding-left: 5%" class="textb" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">Agent Charges Total Amount</td>
        <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=Functions.round(bankAgentPayoutReportVO.getAgentTotalChargesAmount(),2)%></td>
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
        <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%if(bankAgentPayoutReportVO.getAgentWirePaidAmount()>0){out.print("-"+Functions.round(bankAgentPayoutReportVO.getAgentWirePaidAmount(),2));}else{out.print(Functions.round(bankAgentPayoutReportVO.getAgentWirePaidAmount(),2));}%></td>
    </tr>
    <tr>
        <td style="padding-left: 5%" class="textb" colspan="4" valign="middle" align="left" bgcolor="#f5f5f5">Previous Balance Amount</td>
        <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=Functions.round(bankAgentPayoutReportVO.getAgentTotalBalanceAmount(),2)%></td>
    </tr>
    <tr>
        <td colspan="4" valign="middle" class="texthead" align="left" bgcolor="#008BBA" style="padding-left: 2%"><b>Total Amount Funded</b></td>
        <td class="textb" valign="middle"  align="right" class="texthead" style="background-color:#2c3e50;padding-right:5%" ><font color="white"> <b><%=Functions.round(bankAgentPayoutReportVO.getAgentTotalFundedAmount(),2)%></b></font></td>
    </tr>
    <%
        if(bankAgentPayoutReportVO.isWireReport())
        {
            session.setAttribute("bankAgentPayoutReportVO",bankAgentPayoutReportVO);
            session.setAttribute("entity","bankagent");
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
                out.println(Functions.NewShowConfirmation("Thank You","Agent Wire Generated Successfully."));
            }
            else
            {
                out.println(Functions.NewShowConfirmation("Sorry","No Records Found."));
            }

        }
        catch(Exception e)
        {
            logger.error("Sql Exception in bankAgentPayoutReport.jsp",e);
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