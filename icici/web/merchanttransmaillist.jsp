<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.vo.TransactionVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 2/4/13
  Time: 11:18 AM
  To change this template use File | Settings | File Templates.
--%>
<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
<script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>
<script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-account-member.js"></script>

<script>
    $(function() {
        $( ".datepicker" ).datepicker();
    });
</script>
<%!
    Logger logger=new Logger("merchanttransmaillist.jsp");
%>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
       // Hashtable statushash = new Hashtable();
        HashMap<String,String> statushash = new LinkedHashMap<String, String>();
        TerminalManager terminalManager = new TerminalManager();

        statushash.put("authfailed", "Auth Failed");
        statushash.put("authstarted", "Auth Started");
        statushash.put("authstarted_3D", "Auth Started 3D");
        statushash.put("authsuccessful", "Auth Successful");
        statushash.put("authcancelled", "Authorisation Cancelled");
        statushash.put("begun", "Begun Processing");
        statushash.put("cancelstarted", "Cancel Strarted");
        statushash.put("failed", "Cancelled by Customer");
        statushash.put("cancelled", "Cancelled Transaction");
        statushash.put("capturefailed", "Capture Failed");
        statushash.put("capturestarted", "Capture Started");
        statushash.put("capturesuccess", "Capture Successful");
        statushash.put("chargeback", "Chargeback");
        statushash.put("chargebackreversed", "Chargeback Reversed");
        statushash.put("partialrefund", "Partial Refund");
        statushash.put("payoutfailed", "Payout Failed");
        statushash.put("payoutstarted", "Payout Started");
        statushash.put("payoutsuccessful", "Payout Successful");
        statushash.put("podsent", "POD Sent ");
        statushash.put("proofrequired", "Proof Required");
        statushash.put("markedforreversal", "Reversal Request Sent");
        statushash.put("reversed", "Reversed");
        statushash.put("settled", "Settled");


        Hashtable gatewayHash = GatewayTypeService.getGatewayTypes();
        //List<TerminalVO> sTerminal = payoutManager.getTerminalForMerchantWireList();
        List<TerminalVO> sTerminal = terminalManager.getAllMappedTerminal();
        TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();

        String toId=Functions.checkStringNull(request.getParameter("toid"));
        String gateway = Functions.checkStringNull(request.getParameter("gateway"))==null?"":request.getParameter("gateway");
        String startDate=Functions.checkStringNull(request.getParameter("startdate"));
        String startTime=Functions.checkStringNull(request.getParameter("starttime"));
        String endDate=Functions.checkStringNull(request.getParameter("enddate"));
        String endTime=Functions.checkStringNull(request.getParameter("endTime"));
        String terminalId = Functions.checkStringNull(request.getParameter("terminalid"))==null?"":request.getParameter("terminalid");
        //System.out.println("tid::::"+terminalId);
        String memberid = Functions.checkStringNull(request.getParameter("merchantid"))==null?"":request.getParameter("merchantid");
  /*      String status = Functions.checkStringNull(request.getParameter("status"));*/
        String status = request.getParameter("status") == null ? "" : request.getParameter("status");

        if (toId == null)
            toId = "";

        if (gateway == null)
            gateway = "";

        if (startDate == null)
            startDate = "";

        if (startTime == null)
            startTime = "00:00:00";

        if (endDate == null)
            endDate = "";

        if (endTime== null)
            endTime = "23:59:59";

        if (terminalId== null)
            terminalId = "";

        TreeMap<String,TerminalVO> memberMap = new TreeMap<String, TerminalVO>();
        List<TerminalVO> terminalList = terminalManager.getAllMappedTerminals();

        for(TerminalVO terminalVO : terminalList)
        {
            String memberKey = terminalVO.getMemberId()+"-"+terminalVO.getGateway().toUpperCase() + "-" + terminalVO.getCurrency() + "-" + terminalVO.getGateway_id();
            memberMap.put(memberKey,terminalVO);
           /* String memberKey = terminalVO.getMemberId();
            memberMap.put(memberKey, terminalVO);*/
        }
%>
<html>
<head>
    <title>Reports> Member Transaction Report</title>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
</head>
<body>

<form action="/icici/servlet/MerchantTransMailList?ctoken=<%=ctoken%>" method="post" name="forms" >
    <div class="row">
        <div class="col-lg-12">
            <div class="panel panel-default" >
                <div class="panel-heading" >
                    Member Transaction Report
                </div>
                <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                <%String errormsg = (String) request.getAttribute("errormsg");
                    if(errormsg!=null)
                    {
                        out.println("<table align=\"center\"  >");
                        out.println("<tr><td algin=\"center\" ><font class=\"textb\"><b>");

                        out.println(errormsg);
                        out.println("</b></font>");
                        out.println("</td></tr></table>");
                    }
                %>
                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">

                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >From*</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input type="text" readonly class="datepicker" name="startdate" style="width:142px;height: 25px"  value="<%=startDate%>">
                                    </td>

                                    <td width="1%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Time(HH:MM:SS)</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input type="text" name="starttime" value="<%=request.getParameter("starttime")==null?"00:00:00":request.getParameter("starttime")%>" class="txtbox">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >To*</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input type="text" readonly class="datepicker" name="enddate"  style="width:142px;height: 25px"value="<%=endDate%>">
                                    </td>

                                    <td width="1%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Time(HH:MM:SS)</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input type="text"   name="endtime" value="<%=request.getParameter("endtime")==null?"23:59:59":request.getParameter("endtime")%>" class="txtbox">
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
                                    <td width="8%" class="textb" >Gateway</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input name="gateway" id="gateway1" value="<%=gateway%>" class="txtbox" autocomplete="on">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Member ID*</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <input name="merchantid" id="memberid1" value="<%=memberid%>" class="txtbox" autocomplete="on">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Terminal ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <input name="terminalid" id="tid2" value="<%=terminalId%>" class="txtbox" autocomplete="on">
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Status</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <select id = "status" size="1" name="status" class="txtbox">
                                            <option value="">Select Status</option>
                                            <%
                                                //Enumeration enu = statushash.keys();
                                                Set statusSet = statushash.keySet();
                                                Iterator iterator = statusSet.iterator();
                                                String key = "";
                                                String value = "";


                                                while (iterator.hasNext())
                                                {
                                                    key = (String) iterator.next();
                                                    value = (String) statushash.get(key);
                                            %>
                                            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key)%>"><%=ESAPI.encoder().encodeForHTML(value)%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
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

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb"></td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <button type="submit" class="buttonform" >
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                </tr>
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
    Map<String, List<TransactionVO>> transactionVOMap = (Map<String, List<TransactionVO>>) request.getAttribute("transactionVOMap");
    Map<String, List<TransactionVO>> transactionVORefundMap = (Map<String, List<TransactionVO>>) request.getAttribute("transactionVORefundMap");
    Set<String> gatewaySet = (Set<String>) request.getAttribute("gatewaySet");

    logger.debug("transactionVOMap in jsp===="+transactionVOMap);
    logger.debug("transactionVORefundMap in jsp===="+transactionVORefundMap);
    if(((transactionVOMap != null && transactionVOMap.size()>0)) || ((transactionVORefundMap != null && transactionVORefundMap.size()>0)))
    {
    //System.out.println("In if condition");
%>
    <form name="exportform" method="post" action="/icici/servlet/ExportExcelForMerchant?ctoken=<%=ctoken%>">
        <input type="hidden" value="<%=startDate%>" name="startDate">
        <input type="hidden" value="<%=endDate%>" name="endDate">
        <input type="hidden" value="<%=startTime%>" name="startTime">
        <input type="hidden" value="<%=endTime%>" name="endTime">
        <input type="hidden" value="<%=gateway%>" name="gateway">
        <input type="hidden" value="<%=memberid%>" name="merchantid">
        <input type="hidden" value="<%=terminalId%>" name="terminalid">
        <input type="hidden" value="<%=status%>" name="status">
        <%
            session.setAttribute("gatewaySet", gatewaySet);
            session.setAttribute("statushash", statushash);
        %>
        <button type="submit" class="button3" style="width:15%;margin-left:85% ;margin-top:0px"><b>Export to excel</b>&nbsp;&nbsp;&nbsp;<img width="20%" height="100%" border="0" src="/merchant/images/excel.png"></button>
    </form>

        <%
            for (String gatewayName : gatewaySet)
            {
                if (transactionVOMap != null && transactionVOMap.size()>0)
                {
                    List<TransactionVO> transactionVOList = transactionVOMap.get(gatewayName);
                    logger.debug("transactionVOList in jsp For Gateway "+gatewayName+ " ------- "+transactionVOList);
                    if (transactionVOList != null)
                    {
        %>
    <table align="center" border="2" class="table table-striped table-bordered table-green dataTable">
        <tr>
            <td align="center" class="textb" style="border: 4px solid #000;";>
                <table border="0" cellpadding="3" cellspacing="3" bordercolor="#2379A5" width="40%"  align="center" class="table table-striped table-bordered table-green dataTable">
                    <tr>
                        <td  class="textb" colspan="7" align="center" ><b> <%=gatewayName%> </b></td>
                    </tr>
                    <tr>
                        <td  class="textb" colspan="7" align="center"><b>Member Transaction Status Report (<%=startDate%> - <%=endDate%>)</b></td>
                    </tr>
                    <tr>
                        <td style="height: 30px" align="center" class="th0">Status</td>
                        <td style="height: 30px" align="center" class="th0">No Of Transactions</td>
                        <td style="height: 30px" align="center" class="th0">Amount</td>
                        <td style="height: 30px" align="center" class="th0">Capture Amount</td>
                        <td style="height: 30px" align="center" class="th0">Refund Amount</td>
                        <td style="height: 30px" align="center" class="th0">Chargeback Amount</td>
                        <td style="height: 30px" align="center" class="th0">Percentage</td>
                    </tr>
                    <%
                        for (TransactionVO transactionVO : transactionVOList)
                        {
                    %>
                    <tr>
                        <td style="height: 30px" align="center" class="tr1" ><%=statushash.get(transactionVO.getStatus())%></td>
                        <td style="height: 30px" align="center" class="tr0" ><%=transactionVO.getCount()%></td>
                        <td style="height: 30px" align="center" class="tr1" ><%=transactionVO.getAmount()%></td>
                        <td style="height: 30px" align="center" class="tr1" ><%=Double.valueOf(transactionVO.getCaptureAmount())%></td>
                        <td style="height: 30px" align="center" class="tr1" ><%=Double.valueOf(transactionVO.getRefundAmount())%></td>
                        <td style="height: 30px" align="center" class="tr1" ><%=Double.valueOf(transactionVO.getChargebackAmount())%></td>
                        <td style="height: 30px" align="center" class="tr1" ><%=Functions.round(transactionVO.getCount()/ Double.parseDouble(transactionVO.getTotalTransCount())*100,2)%>% </td>
                    </tr>
                    <%
                            }
                        }
                    }
//                    else
//                    {
                    %>
                    <%--<tr>
                        <td style="height: 30px" align="center" colspan="7" class="tr1" >No Data Found</td>
                    </tr>--%>
                    <%--<%--%>
                        <%--}--%>
                    <%--%>--%>
                </table>
                    <%
                        if (transactionVORefundMap != null && transactionVORefundMap.size()>0)
                        {
                            List<TransactionVO> transactionVORefundList = transactionVORefundMap.get(gatewayName);
                            logger.debug("transactionVORefundList in jsp===="+transactionVORefundList);
                            if (transactionVORefundList != null)
                            {
                    %>
                <table border="0" cellpadding="3" cellspacing="3" bordercolor="#2379A5" width="40%"  align="center" class="table table-striped table-bordered table-green dataTable">
                    <tr>
                        <td  class="textb" colspan="6" align="center"><b>Member Transaction Report(Reversal & ChargeBack) (<%=startDate%> - <%=endDate%>)</b></td>
                    </tr>
                    <tr>
                        <td style="height: 30px" align="center" class="th0">Status</td>
                        <td style="height: 30px" align="center" class="th0">No Of Transactions</td>
                        <td style="height: 30px" align="center" class="th0">Amount</td>
                        <td style="height: 30px" align="center" class="th0">Refund Amount</td>
                        <td style="height: 30px" align="center" class="th0">Chargeback Amount</td>
                        <td style="height: 30px" align="center" class="th0">Percentage</td>
                    </tr>
                    <%
                        for (TransactionVO transactionVO : transactionVORefundList)
                        {
                    %>
                    <tr>
                        <td style="height: 30px" align="center" class="tr1" ><%=statushash.get(transactionVO.getStatus())%> </td>
                        <td style="height: 30px" align="center" class="tr0" ><%=transactionVO.getCount()%> </td>
                        <td style="height: 30px" align="center" class="tr1" ><%=transactionVO.getAmount()%> </td>
                        <td style="height: 30px" align="center" class="tr1" ><%=transactionVO.getRefundAmount()%> </td>
                        <td style="height: 30px" align="center" class="tr1" ><%=transactionVO.getChargebackAmount()%> </td>
                        <td style="height: 30px" align="center" class="tr1" ><%=Functions.round(transactionVO.getCount()/ Double.parseDouble(transactionVO.getTotalTransCount())*100,2)%>% </td>
                    </tr>
                    <%
                            }
                        }
                    }
//                    else
//                    {
                    %>
                   <%-- <tr>
                        <td style="height: 30px" align="center" colspan="6" class="tr1" >No Revesal Chargeback Records Founds </td>
                    </tr>--%>
                    <%--<%
                        }
                    %>--%>
                </table>
            </td>
        </tr>
    </table>
        <%
        }
    }
        else
        {
            out.println(Functions.NewShowConfirmation("Sorry","No records found."));
        }
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</body>
<%
    String status = request.getParameter("status") == null ? "" : request.getParameter("status");
%>

<script type="text/javascript">
    $('#sandbox-container input').datepicker({

    });
    $(document).ready(function ()
    {
        var dd = document.getElementById("status");
        for (var i = 0; i < dd.options.length; i++)
        {
            if (dd.options[i].value === "<%=status%>")
            {
                dd.selectedIndex = i;
                break;
            }
        }
    });
</script>
</html>