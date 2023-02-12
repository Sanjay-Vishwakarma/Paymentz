<%@ page import="java.util.*" %>
<%@ page import="servlets.ChargesUtils" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="com.directi.pg.Database" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="servlets.BusinessDashboardUtils" %>
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
<%@ page import="com.directi.pg.Logger"%>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.morrisBarVOs.Data" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.manager.TerminalManager" %>
<%
    Logger logger=new Logger("businessDashboard.jsp");
%>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title>Reports> Business Dashboard</title>
    <script language="JavaScript" src="/merchant/FusionCharts/FusionCharts.js?ver=1"></script>
    <script language="javascript">
        function blockTransaction()
        {
            var msg = confirm("Do you really want to block all Transactions from this merchant?");
            if(msg == true){
                document.businessDashboard.blockTrans.value="true";
                document.businessDashboard.submit();
            }
        }
        function blockRefund()
        {
            var msg =confirm("Do you really want to block all Refunds from this merchant?");
            if(msg == true){
                document.businessDashboard.blockRef.value="true";
                document.businessDashboard.submit();
            }
        }
        function getTimeForURL()
        {
            var dt = new Date();
            var strOutput = "";
            strOutput = dt.getHours() + "_" + dt.getMinutes() + "_" + dt.getSeconds() + "_" + dt.getMilliseconds();
            return strOutput;
        }
    </script>

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>

    <%--<script type="text/javascript" language="JavaScript" src="/icici/javascript/memberid_filter.js"></script>--%>
    <%--<script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-account-member.js"></script>--%>
    <%--<link rel="stylesheet" type="text/css" href="/merchant/style.css">--%>
    <link rel="stylesheet" type="text/css" href="/merchant/FusionCharts/style.css" >
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String isSubmitted =  request.getParameter("isSubmitted");
        String memberId =  request.getParameter("memberid");
        String accountId =  request.getParameter("accountid");
        String payMode =  request.getParameter("paymode");
        String cardType =  request.getParameter("cardtype");
        String blockTrans =  request.getParameter("blockTrans");
        String blockRef =  request.getParameter("blockRef");
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String str = "";
        String pgtypeid = "";

        pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");

        String accountid = Functions.checkStringNull(request.getParameter("accountid"))== null ? "" : request.getParameter("accountid");
        String memberid = Functions.checkStringNull(request.getParameter("memberid"))==null ? "" : request.getParameter("memberid");

        TreeMap<String,TerminalVO> memberMap = new TreeMap<String, TerminalVO>();
        TreeMap<Integer,String> paymodeDetails = GatewayAccountService.getPaymentModes();
        TreeMap<Integer,String> cardTypeDetails = GatewayAccountService.getCardTypes();

        TerminalManager terminalManager = new TerminalManager();
        List<TerminalVO> terminalList = terminalManager.getAllMappedTerminals();



        for(TerminalVO terminalVO : terminalList)
        {
            String memberKey = terminalVO.getMemberId()+"-"+terminalVO.getAccountId()+"-"+terminalVO.getGateway().toUpperCase() + "-" + terminalVO.getCurrency() + "-" + terminalVO.getGateway_id();
            memberMap.put(memberKey,terminalVO);
        }



        if(pgtypeid!=null)str = str + "&pgtypeid=" + pgtypeid;
        else
            pgtypeid="";


%>

<form action="/icici/businessDashboard.jsp?ctoken=<%=ctoken%>" method="post" name="businessDashboard">
    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
    <input type="hidden" value="true" name="isSubmitted">
    <input type="hidden" name="blockTrans" value="false">
    <input type="hidden" name="blockRef" value="false">
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default">
                <div class="panel-heading" >
                    Business Dashboard
                </div>
                <table  align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:1%;">
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="95%"  align="center">
                                <tr><td colspan="4">&nbsp;</td></tr>


                                <tr>
                                    <td width="0%" class="textb">&nbsp;</td>
                                    <td width="10%" class="textb" >Gateway/Account</td>
                                    <td width="5%" class="textb"></td>
                                    <td width="0%" class="textb">
                                        <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                                    <%--<select size="1" id="bank" name="pgtypeid" class="txtbox"  style="width:200px;">
                                            <option value="0" default>--All--</option>
                                            <%
                                                for(String gatewayType : gatewayTypeTreeMap.keySet())
                                                {
                                                    String isSelected = "";
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

                                 <%--   <td>&nbsp;&nbsp;</td>--%>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">AccountID*&nbsp;&nbsp;</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input name="accountid" id="accountid1" value="<%=accountid%>" class="txtbox" autocomplete="on">
                                    <%--   <select size="1" id="accountid" name="accountid" class="txtbox">
                                            <option data-bank="all" data-curr="all" value="0">Select AccountID</option>
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

                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Member ID*</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="memberid" id="memberid1" value="<%=memberid%>" class="txtbox" autocomplete="on">
                                    <%-- <select name="memberid" id="memberid" class="txtbox" style="width:200px;">
                                            <option data-bank="all"  data-accid="all" value="0" >--Select MemberID--</option>
                                            <%
                                                for(String sMemberId: memberMap.keySet())
                                                {

                                                    TerminalVO t = memberMap.get(sMemberId);
                                                    String aGateway = memberMap.get(sMemberId).getGateway().toUpperCase()+"-"+memberMap.get(sMemberId).getCurrency()+"-"+memberMap.get(sMemberId).getGateway_id();
                                                    String accId = t.getAccountId();
                                                    String aContactPerson = t.getContactPerson();
                                                    String aCompanyName = t.getCompany_name();
                                                    String gateway2 = t.getGateway().toUpperCase();
                                                    String currency2 = t.getCurrency();
                                                    String pgtype = t.getGateway_id();
                                                    String value = gateway2+"-"+currency2+"-"+pgtype;
                                                    String isSelected = "";
                                                    if(String.valueOf(t.getMemberId()+"-"+accId).equalsIgnoreCase(memberid+"-"+accountid))
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
                                    <td width="8%" class="textb" >Paymode</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select name="paymode" class="txtbox"><option value="" selected>--Select Payment type--</option>
                                                <%
                                                for (Integer paymodeId : paymodeDetails.keySet())
                                                {
                                                    String paymentType = paymodeDetails.get(paymodeId);
                                                    String isSelected = "";
                                                    if (String.valueOf(paymodeId).equalsIgnoreCase(payMode))
                                                    {
                                                        isSelected = "selected";
                                                    }

					                            %>
                                            <option value="<%=paymodeId%>" <%=isSelected%>><%=paymodeId+"-"+paymentType%></option>
                                            <%
                                                }
                                            %>
                                            </select>
                                    </td>

                                    <td width="6%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Card Type</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <select name="cardtype" class="txtbox"><option value="" selected>--Select Card type--</option>
                                            <%
                                                for (Integer cardTypeId : cardTypeDetails.keySet())
                                                {
                                                    String cardType1 = cardTypeDetails.get(cardTypeId);
                                                    String isSelected = "";

                                                    if (String.valueOf(cardTypeId).equalsIgnoreCase(cardType))
                                                    {
                                                        isSelected = "selected";
                                                    }
                                                    %>
                                            <option value="<%=cardTypeId%>" <%=isSelected%>><%=cardTypeId+"-"+cardType1%></option>

                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                    </td>
                                </tr>

                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>

                                </tr>
                                <tr>
                                    <td width="0%" class="textb">&nbsp;</td>
                                    <td width="15%" class="textb" ></td>
                                    <td width="17%" class="textb"></td>
                                    <td width="40%" class="textb"></td>
                                    <td width="5%" class="textb">&nbsp;</td>

                                    <td width="20%" class="textb" ></td>
                                    <td width="10%" class="textb"></td>
                                    <td width="200%" class="textb"></td>
                                    <td width="70%" class="textb">&nbsp;
                                    <button type="submit" class="buttonform" style="margin-left: 145%" >
                                        <i class="fa fa-clock-o"></i>
                                        &nbsp;&nbsp;Draw Chart
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
    <div class="reporttable">
        <table align=center width="90%">
            <tr>
                <%
                    Functions functions = new Functions();

                    if ("true".equals(isSubmitted))
                    {
                        /*if ("0".equals(memberId) || "0".equals(accountId))*/
                        if (functions.isEmptyOrNull(memberid) || functions.isEmptyOrNull(accountid))
                        {
                %>
                <td align="center" colspan="2">
                    <font color="red" size="3"><b>MemberID OR Account ID is invalid.</b></font>
                </td>
                <%
                }
                else
                {
                    BusinessDashboardUtils.prepareAllChartsData(memberId, accountId, payMode, cardType);
                    if ("true".equals(blockTrans))
                    {
                        BusinessDashboardUtils.blockTransactions(memberId);
                    }
                    else if ("true".equals(blockRef))
                    {
                        BusinessDashboardUtils.blockRefunds(memberId);
                    }
                %>
                <td colspan="2" class="texthead" style="color: #ffffff;">
                    <div align="center" >Sales Chart</div>
                </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr>
                <td width="50%" class="textb">
                    <div id="salesChartDiv"  align="center">Sales Chart.</div>
                    <script type="text/javascript">
                        var myChart = new FusionCharts("/merchant/FusionCharts/Column3D.swf?ver=1>", "statusChart", "400", "250");
                        myChart.setDataURL("/merchant/FusionCharts/data/SalesData.xml?currTime=" + getTimeForURL());
                        myChart.render("salesChartDiv");
                    </script></td>
                <td width="50%" class="textb">
                    <div id="salesMeterDiv" align="center">Sales Chart.</div>
                    <script type="text/javascript">
                        var myChart = new FusionCharts("/merchant/FusionCharts/AngularGauge.swf?ver=1>", "statusChart", "400", "200");
                        myChart.setDataURL("/merchant/FusionCharts/data/SalesMeter.xml?currTime=" + getTimeForURL());
                        myChart.render("salesMeterDiv");
                    </script>
                    <div align="center"><br/><input type="button"  value=" Block Transactions" onclick="blockTransaction();" style="width: 200px; height: 40px; color: red" ></div></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr>
                <td colspan="2" class="texthead" style="color: #ffffff;">
                    <div align="center">Refund Chart</div>
                </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr>
                <td width="50%" class="textb">
                    <div id="refundChartDiv" align="center">Refund Chart.</div>
                    <script type="text/javascript">
                        var myChart = new FusionCharts("/merchant/FusionCharts/Column3D.swf?ver=1>", "statusChart", "400", "250");
                        myChart.setDataURL("/merchant/FusionCharts/data/RefundData.xml?currTime=" + getTimeForURL());
                        myChart.render("refundChartDiv");
                    </script></td>
                <td width="50%" class="textb">
                    <div id="refundMeterDiv" align="center" class="textb">Refund Chart.</div>
                    <script type="text/javascript">
                        var myChart = new FusionCharts("/merchant/FusionCharts/AngularGauge.swf?ver=1>", "statusChart", "400", "200");
                        myChart.setDataURL("/merchant/FusionCharts/data/RefundMeter.xml?currTime=" + getTimeForURL());
                        myChart.render("refundMeterDiv");
                    </script>
                    <div align="center"><br/><input type="button" value=" Block Refunds "  onclick="blockRefund();" style="width: 200px; height: 40px; color: red" ></div></td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr>
                <td colspan="2" class="texthead" style="color: #ffffff;">
                    <div align="center" >Chargeback Chart</div>
                </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr>
                <td width="50%">
                    <div id="chargebackChartDiv" align="center" class="textb">Chargeback Chart.</div>
                    <script type="text/javascript">
                        var myChart = new FusionCharts("/merchant/FusionCharts/Column3D.swf?ver=1>", "statusChart", "400", "250");
                        myChart.setDataURL("/merchant/FusionCharts/data/ChargebackData.xml?currTime=" + getTimeForURL());
                        myChart.render("chargebackChartDiv");
                    </script></td>
                <td width="50%">
                    <div id="chargebackMeterDiv" align="center" class="textb">Sales Chart.</div>
                    <script type="text/javascript">
                        var myChart = new FusionCharts("/merchant/FusionCharts/AngularGauge.swf?ver=1>", "statusChart", "400", "200");
                        myChart.setDataURL("/merchant/FusionCharts/data/ChargebackMeter.xml?currTime=" + getTimeForURL());
                        myChart.render("chargebackMeterDiv");
                    </script></td>
                </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr><td colspan="2" class="texthead" style="color: #ffffff;">
                <div align="center">Fraud Chart</div>
            </td>
            </tr>
            <tr><td>&nbsp;</td></tr>
            <tr>
                <td width="50%">
                    <div id="fraudChartDiv" align="center">Fraud Chart.</div>
                    <script type="text/javascript">
                        var myChart = new FusionCharts("/merchant/FusionCharts/Column3D.swf?ver=1>", "statusChart", "400", "250");
                        myChart.setDataURL("/merchant/FusionCharts/data/FraudData.xml?currTime=" + getTimeForURL());
                        myChart.render("fraudChartDiv");
                    </script></td>
                <td width="50%">
                    <div id="fraudMeterDiv" align="center" class="textb">Sales Chart.</div>
                    <script type="text/javascript">
                        var myChart = new FusionCharts("/merchant/FusionCharts/AngularGauge.swf?ver=1>", "statusChart", "400", "200");
                        myChart.setDataURL("/merchant/FusionCharts/data/FraudMeter.xml?currTime=" + getTimeForURL());
                        myChart.render("fraudMeterDiv");
                    </script></td>
                </td>
                <%
                    }
                }
                else{
                %>
                <td align="center" colspan="2" class="textb">
                    <p>Please select Chart Parameters and Click on Draw Chart.</p></font>
                </td>
                <%
                    }
                %>
            </tr>
        </table>
    </div>
</form>
<br/>
<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
    }
%>
</body>
</html>