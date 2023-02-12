<%@ page import="com.directi.pg.Functions" %>
<%@ page import="java.util.*" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ include file="functions.jsp"%>

<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 2/10/15
  Time: 6:07 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page import="com.directi.pg.Logger"%>
<%@ page import="com.manager.vo.payoutVOs.ChargeDetailsVO" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.manager.vo.payoutVOs.BankPartnerPayoutReportVO" %>
<%@ page import="com.manager.vo.PartnerDetailsVO" %>
<%!
    Logger logger=new Logger("bankPartnerConsolidatePayoutSummary.jsp");
%>

<html>
<head>
    <META content="text/html; charset=utf-8" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/autocomplete.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid.js"></script>

    <%--Datepicker css format--%>
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: 'yy-mm-dd'});
        });

    </script>
    <title>Bank Agent Payout Report</title>
</head>
<body>
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
                                String partnerid = Functions.checkStringNull(request.getParameter("partnerid"))==null?"":request.getParameter("partnerid");
                                String pgtypeid = Functions.checkStringNull(request.getParameter("gateway"))==null?"":request.getParameter("gateway");
                                String accountid = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
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
                                    <td width="11%" class="textb" >Partner Id</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="partnerid" id="pid1" value="<%=partnerid%>" class="txtbox" autocomplete="on">

                                        <%
                                            Connection conn = Database.getConnection();
                                        %>
                                        <%--<select name="partnerid" class="txtbox"><option value="" selected></option>
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
                                        <input name="gateway" id="gateway" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
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
                                    <td width="3%" class="textb"></td>
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
                                    <td width="13%" class="textb" >Account ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb" colspan="5">
                                        <input name="accountid" id="accountid" value="<%=accountid%>" class="txtbox" autocomplete="on">
                                       <%-- <select name="accountid" class="txtbox" style="width: 430px"><option value="" selected></option>
                                            <%
                                                Connection conn = Database.getConnection();
                                                try{

                                                    String query = "SELECT * FROM gateway_accounts ORDER BY accountid ASC";
                                                    PreparedStatement pstmt = conn.prepareStatement( query );
                                                    ResultSet rs = pstmt.executeQuery();
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

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <button type="submit" class="buttonform" value="View Payout Report" style="width: 158px;">
                                            <i class="fa fa-sign-in"></i>
                                            &nbsp;&nbsp;View Payout Report
                                        </button>
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">

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
        //HashMap<String,List<ChargeDetailsVO>> stringListHashMap=null;//bankAgentPayoutReportVO.getStringListHashMap();
        HashMap<String, HashMap<String, List<ChargeDetailsVO>>> stringHashMapHashMap=bankPartnerPayoutReportVO.getStringHashMapHashMap();
        PartnerDetailsVO partnerDetailsVO=bankPartnerPayoutReportVO.getPartnerDetailsVO();

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
        <td class="textb"  style="padding-left: 5%" colspan="3"  valign="middle" align="left" bgcolor="#f5f5f5">Settle Covered Upto:</td>
        <td class="textb" colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"> <%=bankPartnerPayoutReportVO.getReportingDate()%></td>
    </tr>
    <tr>
        <td class="textb"  style="padding-left: 5%" colspan="3"  valign="middle" align="left" bgcolor="#f5f5f5">Decline Covered Upto:</td>
        <td class="textb" colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=bankPartnerPayoutReportVO.getReportingDate()%></td>
    </tr>
    <tr>
        <td class="textb"  style="padding-left: 5%" colspan="3"  valign="middle" align="left" bgcolor="#f5f5f5">Reversal Covered Upto:</td>
        <td class="textb" colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=bankPartnerPayoutReportVO.getReportingDate()%></td>
    </tr>
    <tr>
        <td class="textb"  style="padding-left: 5%" colspan="3"  valign="middle" align="left" bgcolor="#f5f5f5">Chargeback Covered Upto:</td>
        <td class="textb" colspan="2" valign="middle" align="left" bgcolor="#f5f5f5"><%=bankPartnerPayoutReportVO.getReportingDate()%></td>
    </tr>

    <%
        Set set=stringHashMapHashMap.keySet();
        Iterator iterator=set.iterator();
        while (iterator.hasNext())
        {
            String gateway=(String)iterator.next();
            HashMap<String,List<ChargeDetailsVO>> stringListHashMap=stringHashMapHashMap.get(gateway);
            String gatewayArr[]=gateway.split(":");
            String gatewayName=gatewayArr[0];
            String gatewayCurrency=gatewayArr[1];
    %>
    <tr>
        <td  colspan="5" valign="middle" align="left" class="texthead" bgcolor="#008BBA">Bank-<b><%=gatewayName%></b>&nbsp;&nbsp;&nbsp;&nbsp;Currency-<b><%=gatewayCurrency%></b></td>
    </tr>
    <tr>
        <td valign="middle" align="left" class="textb" style="padding-left: 5%">Charge Name</td>
        <td valign="middle" align="left" class="textb" style="padding-left: 7%">Rate/Fee</td>
        <td valign="middle" align="right" class="textb" style="padding-right: 5%">Counter</td>
        <td valign="middle" align="right" class="textb" style="padding-right: 8%">Amount</td>
        <td valign="middle" align="right" class="textb" style="padding-right: 5%">Total</td>

    </tr>
    <%
        Set<String> midSet=stringListHashMap.keySet();
        if(midSet.size()>0)
        {
            Iterator midItr=midSet.iterator();
            while (midItr.hasNext())
            {
                String mid=(String)midItr.next();
                String strArr[]=mid.split(":");
                String midString=strArr[0];
                String midChargeAmount=Functions.round(Double.valueOf(strArr[1]),2);
                String midPaidAmount=Functions.round(Double.valueOf(strArr[2]),2);
                String midFunded=Functions.round(Double.valueOf(strArr[3]),2);
    %>
    <tr>
        <td  colspan="5" valign="middle" align="center" class="textb"><b>MID-<%=midString%></b></td>
    </tr>
    <%
        List<ChargeDetailsVO> chargeDetailsVOList=stringListHashMap.get(mid);
        if(chargeDetailsVOList.size()>0)
        {

            for (ChargeDetailsVO chargeDetailsVO:chargeDetailsVOList)
            {
    %>
    <tr>
        <td class="textb"valign="middle"  style="padding-left: 5%" align="left"  bgcolor="#f5f5f5"><%=chargeDetailsVO.getChargeName()%></td>
        <td class="textb" valign="middle" style="padding-left: 7%" align="left" bgcolor="#f5f5f5"><%=chargeDetailsVO.getChargeValue()%></td>
        <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=chargeDetailsVO.getCount()%></td>
        <td class="textb" valign="middle" style="padding-right:9%" align="right" bgcolor="#f5f5f5"><%=Functions.round(chargeDetailsVO.getAmount(),2)%></td>
        <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=Functions.round(chargeDetailsVO.getTotal(),2)%></td>
    </tr>
    <%
        }

    %>
    <tr>
        <td  colspan="4" valign="middle" align="right" class="textb" bgcolor="#f5f5f5"><b><%=gatewayCurrency%> Charges Total:</b></td>
        <td class="textb" valign="middle" style="padding-right:5%;" align="right" bgcolor="#f5f5f5"><b><%=midChargeAmount%></b></td>
    </tr>
    <tr>
        <td  colspan="4" valign="middle" align="right" class="textb" bgcolor="#f5f5f5"><b><%=gatewayCurrency%> Paid Total:</b></td>
        <td class="textb" valign="middle" style="padding-right:5%;" align="right" bgcolor="#f5f5f5"><b><%if(Double.valueOf(midPaidAmount)>0){out.println("-"+midPaidAmount);}else{out.println(midPaidAmount);}%></b></td>
    </tr>
    <tr>
        <td  colspan="4" valign="middle" align="right" class="textb" bgcolor="#f5f5f5"><b><%=gatewayCurrency%> Funded Total:</b></td>
        <td class="textb" valign="middle" style="padding-right:5%;" align="right" bgcolor="#2c3e50"><font color="white"> <b><%=midFunded%></b></font></td>
    </tr>
    <%

    }
    else
    {
    %>
    <tr>
        <td  colspan="5" class="textb"valign="middle"  align="center"  bgcolor="#f5f5f5">Charges Not Found on Gateway Account</td>
    </tr>
    <%
            }

        }
    }
    else
    {
    %>
    <tr>
        <td  colspan="5" class="textb"valign="middle"  align="center"  bgcolor="#f5f5f5">Gateway Account Not Found</td>
    </tr>
    <%
            }


        }

    %>
    <tr>
        <td  colspan="5" valign="middle" align="left" class="texthead" bgcolor="#008BBA"><b>Per Currency Gross Amount</b></td>
    </tr>
    <%
        if(bankPartnerPayoutReportVO.isINRAccount())
        {
    %>
    <tr>
        <td  colspan="4" valign="middle" align="right" class="textb" bgcolor="#f5f5f5"><b>INR Total:</b></td>
        <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=Functions.round(bankPartnerPayoutReportVO.getINR_CHARGE_AMOUNT(),2)%></td>
    </tr>
    <%
        }
    %>
    <%
        if(bankPartnerPayoutReportVO.isUSDAccount())
        {
    %>
    <tr>
        <td  colspan="4" valign="middle" align="right" class="textb" bgcolor="#f5f5f5"><b>USD Total:</b></td>
        <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=Functions.round(bankPartnerPayoutReportVO.getUSD_CHARGE_AMOUNT(),2)%></td>
    </tr>
    <%
        }
    %>
    <%
        if(bankPartnerPayoutReportVO.isEURAccount())
        {
    %>
    <tr>
        <td  colspan="4" valign="middle" align="right" class="textb" bgcolor="#f5f5f5"><b>EUR Total:</b></td>
        <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=Functions.round(bankPartnerPayoutReportVO.getEUR_CHARGE_AMOUNT(),2)%></td>
    </tr>
    <%
        }
    %>
    <%
        if(bankPartnerPayoutReportVO.isGBPAccount())
        {
    %>
    <tr>
        <td  colspan="4" valign="middle" align="right" class="textb" bgcolor="#f5f5f5"><b>GBP Total:</b></td>
        <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=Functions.round(bankPartnerPayoutReportVO.getGBP_CHARGE_AMOUNT(),2)%></td>
    </tr>
    <%
        }
    %>
    <%
        if(bankPartnerPayoutReportVO.isJPYAccount())
        {
    %>
    <tr>
        <td  colspan="4" valign="middle" align="right" class="textb" bgcolor="#f5f5f5"><b>JPY Total:</b></td>
        <td class="textb" valign="middle" style="padding-right:5%" align="right" bgcolor="#f5f5f5"><%=Functions.round(bankPartnerPayoutReportVO.getJPY_CHARGE_AMOUNT(),2)%></td>
    </tr>
    <%
        }
    %>
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
            logger.error("Sql Exception in bankPartnerConsolidatePayoutSummary.jsp:",e);
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

</div>
</body>
</html>
