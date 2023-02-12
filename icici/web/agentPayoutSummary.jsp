<%@ page import="com.directi.pg.Functions" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
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
<%@ page import="com.manager.vo.payoutVOs.AgentPayoutReportVO" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.manager.PayoutManager" %>
<%@ page import="com.manager.vo.*" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%!
    Logger logger=new Logger("agentPayoutSummary.jsp");
%>

<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <%--Datepicker css format--%>
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: 'yy-mm-dd'});
        });

    </script>
   <%-- <script type="text/javascript" language="JavaScript" src="/icici/javascript/memberid_filter.js"></script>--%>
    <script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-account-member.js"></script>
    <title>Payment | Agent Payout Report</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        Connection conn=null;
        try{

        String str2 = "";
        String pgtypeid = "";
        String currency= "";
        currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");

        pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");

        String accountid = Functions.checkStringNull(request.getParameter("accountid"));
        String memberid = Functions.checkStringNull(request.getParameter("memberid"));


        Map terminalMap = GatewayTypeService.getAllTerminalsGroupByMerchant();

        if(pgtypeid!=null)str2 = str2 + "&pgtypeid=" + pgtypeid;
        else
            pgtypeid="";

        String cardtypeid = request.getParameter("cardtypeid");
        String paymodeid = request.getParameter("paymodeid");
        String agentid = request.getParameter("agentid");
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
</div>
<form action="/icici/servlet/GenerateAgentPayoutServlet?ctoken=<%=ctoken%>" method="post" name="forms" >
<input type="hidden" value="<%=ctoken%>" name="ctoken">
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
                            <select size="1" id="bank" name="pgtypeid" class="txtbox"  style="width:200px;">
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

                            </select>
                        </td>
                        <td width="4%" class="textb">&nbsp;</td>
                        <td width="11%" class="textb" >Account ID</td>
                        <td width="3%" class="textb"></td>
                        <td width="12%" class="textb">
                            <select size="1" id="accountid" name="accountid" class="txtbox" style="width: 200px">
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
                            </select>
                        </td>
                        <td width="4%" class="textb">&nbsp;</td>
                        <td width="11%" class="textb" >Member ID</td>
                        <td width="3%" class="textb"></td>
                        <td width="12%" class="textb">
                            <select name="memberid" id="memberid" class="txtbox" style="width:200px;">
                                <option data-bank="all"  data-accid="all" value="0" selected>--Select MemberID--</option>
                                <%

                                    for(String sMemberId: memberMap.keySet())
                                    {

                                        TerminalVO t = memberMap.get(sMemberId);
                                        String aGateway = memberMap.get(sMemberId).getGateway().toUpperCase()+"-"+memberMap.get(sMemberId).getCurrency()+"-"+memberMap.get(sMemberId).getGateway_id();
                                        String accId = t.getAccountId();
                                        String aContactPerson = t.getContactPerson();
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
                                <option data-bank="<%=value%>"  data-accid="<%=accId%>" value="<%=t.getMemberId()%>" <%=isSelected%>><%=t.getMemberId()+"-"+accId+"-"+aContactPerson%></option>
                                <%

                                    }
                                %>
                            </select>
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
                        <td width="11%" class="textb" >Agent Id</td>
                        <td width="3%" class="textb"></td>
                        <td width="12%" class="textb">

                            <select name="agentid" class="txtbox">
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

                            </select>
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
            HashMap<String, List<ChargeDetailsVO>> stringListHashMap=agentPayoutReportVO.getStringListHashMap();
            AgentDetailsVO agentDetailsVO=agentPayoutReportVO.getAgentDetailsVO();
            MerchantDetailsVO merchantDetailsVO=agentPayoutReportVO.getMerchantDetailsVO();
            SettlementDateVO settlementDateVO=agentPayoutReportVO.getSettlementDateVO();
            session.setAttribute("agentPayoutReportVO",agentPayoutReportVO);

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
            <td colspan="2" valign="middle" align="left" class="textb" bgcolor="#f5f5f5"><%=merchantDetailsVO.getMemberId()%></td>
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
            <td  colspan="5" valign="middle" align="center" class="texthead" bgcolor="#008BBA"><b>Charges Details</b></td>
        </tr>
        <tr>
            <td valign="middle" align="center" class="texthead">Charge Name</td>
            <td valign="middle" align="center" class="texthead">Rate/Fee</td>
            <td valign="middle" align="center" class="texthead">Counter</td>
            <td valign="middle" align="center" class="texthead">Amount</td>
            <td valign="middle" align="center" class="texthead">Total</td>

        </tr>
        <%
            Set<String> terminalIds=stringListHashMap.keySet();
            Iterator itr=terminalIds.iterator();
            while (itr.hasNext())
            {
                String terminalName=(String)itr.next();
                List<ChargeDetailsVO> chargeDetailsVOList=(List<ChargeDetailsVO>)stringListHashMap.get(terminalName);
        %>
        <tr>
            <td  colspan="5" valign="middle" align="center" class="textb"><b><%=terminalName%></b></td>
        </tr>
        <%
            if(chargeDetailsVOList.size()>0)
            {
                for (ChargeDetailsVO chargeDetailsVO:chargeDetailsVOList)
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
        }
        else
        {
        %>
        <tr>

            <td class="textb" valign="middle" colspan="5" align="center" bgcolor="#f5f5f5">No Charges On Terminal</td>
            <%--<td class="textb" valign="middle" colspan="2" align="center" bgcolor="#f5f5f5"></td>--%>

        </tr>
        <%
                }

            }


        %>
        <tr>
            <td colspan="4" valign="middle" class="texthead" align="right" bgcolor="#008BBA"><b>Total:</b></td>
            <td class="textb" valign="middle"  align="right" class="texthead" style="background-color:#2c3e50;padding-right:5%" ><font color="white"> <b><%=Functions.round(agentPayoutReportVO.getAgentTotalChargesAmount(),2)%></b></font></td>
        </tr>
    </table>
    <br>

    <%
                }
                else
                {
                    out.println(Functions.NewShowConfirmation("Sorry","No records found."));
                }
            }
            catch(Exception e)
            {
                logger.error("Sql Exception in agentPayoutSummary.jsp:",e);
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