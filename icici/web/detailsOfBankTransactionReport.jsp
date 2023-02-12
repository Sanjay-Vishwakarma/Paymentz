<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI "%>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.manager.vo.TransactionDetailsVO" %>
<%@ include file="functions.jsp"%>

<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: shipra
  Date: 6/2/14
  Time: 4:55 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>

<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

    <script type="text/javascript">
        $('#sandbox-container input').datepicker({

        });
    </script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker();

        });
    </script>
    <script language="javascript">
        function Update()
        {
            var checkboxes = document.getElementsByName("id");
            var checked=[];

            var total_boxes = checkboxes.length;
            flag = false;

            for(i=0; i<total_boxes; i++ )
            {
                if(checkboxes[i].checked)
                {
                    flag= true;
                    checked.push(checkboxes[i].value);
                    checked.join(',');
                }
            }
            document.getElementById("ids").value=checked;
            if(!flag)
            {
                alert("Select at least one record");
                return false;
            }
            if (confirm("Do you really want to Settle records?"))
            {
                document.update.submit();
            }
        }
    </script>
    <script language="javascript">
        function UpdateSettled()
        {
            var checkboxes = document.getElementsByName("id");
            var checked=[];

            var total_boxes = checkboxes.length;
            flag = false;

            for(i=0; i<total_boxes; i++ )
            {
                if(checkboxes[i].checked)
                {
                    flag= true;
                    checked.push(checkboxes[i].value);
                    checked.join(',');
                }
            }
            document.getElementById("ids1").value=checked;
            if(!flag)
            {
                alert("Select at least one record");
                return false;
            }
            if (confirm("Do you really want to Capture records?"))
            {
                document.updateSettled.submit();
            }
        }
    </script>
    <script language="javascript">
        function ToggleAll(checkbox)
        {
            flag = checkbox.checked;
            var checkboxes = document.getElementsByName("id");
            var total_boxes = checkboxes.length;

            for(i=0; i<total_boxes; i++ )
            {
                checkboxes[i].checked =flag;
            }
        }
    </script>
    <script type="text/javascript">
        function getAccountDetails(ctoken)
        {
            console.log("inside")
            var accountId = document.getElementById("gateway1").value;
            document.forms.action = "/icici/servlet/BankTransactionReport?ctoken=" + ctoken + "&action=getinfo";
            document.getElementById("submit_button").click();
        }
    </script>
    <title>Bank Transaction Report</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String gateway = Functions.checkStringNull(request.getParameter("gateway"));
        String style="class=\"tr0\"";

        String accountid = Functions.checkStringNull(request.getParameter("accountid"));
        String merchantid=Functions.checkStringNull(request.getParameter("merchantid"));
        String startDate=request.getParameter("startdate")==null?"":request.getParameter("startdate");
        String startTime=request.getParameter("starttime")==null?"00:00:00":request.getParameter("starttime");
        String endDate=request.getParameter("enddate")==null?"":request.getParameter("enddate");
        String endTime=request.getParameter("endtime")==null?"23:59:59":request.getParameter("endtime");
        String memberid = Functions.checkStringNull(request.getParameter("merchantid")) == null ? "" : request.getParameter("merchantid");
        String pgtypeid = Functions.checkStringNull(request.getParameter("gateway")) == null ? "" : request.getParameter("gateway");
        String terminalId=Functions.checkStringNull(request.getParameter("terminalid")==null ? "" : request.getParameter("terminalid"));
        String partnerName= request.getParameter("partnerName")==null ? "" : request.getParameter("partnerName");


        String BankTransactionInquiry=request.getAttribute("BankTransactionInquiry")==null ? "" : (String) request.getAttribute("BankTransactionInquiry");
        String TransactionInquiry=request.getAttribute("TransactionInquiry")==null ? "" : (String) request.getAttribute("TransactionInquiry");
        String successCounter=request.getAttribute("successCounter")==null ? "" : (String) request.getAttribute("successCounter");
        String failCounter=request.getAttribute("failCounter")==null ? "" : (String) request.getAttribute("failCounter");
        String authStarted=request.getAttribute("authStarted")==null ? "" : (String) request.getAttribute("authStarted");
        String payoutsuccessCounter=request.getAttribute("payoutsuccessCounter")==null ? "" : (String) request.getAttribute("payoutsuccessCounter");
        String payoutfailCounter=request.getAttribute("payoutfailCounter")==null ? "" : (String) request.getAttribute("payoutfailCounter");
        String payoutStarted=request.getAttribute("payoutStarted")==null ? "" : (String) request.getAttribute("payoutStarted");
//        System.out.println("BankTransactionInquiry------"+BankTransactionInquiry);
        TerminalManager terminalManager = new TerminalManager();
        List<TerminalVO> terminalList = terminalManager.getAllMappedTerminals();
        List<TransactionDetailsVO> transactionDetailsVOs = (List<TransactionDetailsVO>)request.getAttribute("trackinglist");
        List<TerminalVO> accountList = (List<TerminalVO>) request.getAttribute("accountList");

        String errorMsg =(String) request.getAttribute("errormsg");

        String timeDifference =(String) request.getAttribute("timediff");
        if(timeDifference==null || timeDifference.equalsIgnoreCase("null"))
        {
            timeDifference="";
        }
        if (gateway == null)
        {
            gateway = "";
        }
        if (accountid== null)
        {
            accountid = "";
        }
        if(merchantid==null)
        {
            merchantid="";
        }
        if(terminalId==null)
        {
            terminalId="";
        }

        TreeMap<String,TerminalVO> memberMap = new TreeMap<String, TerminalVO>();
        TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();
        TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();
        for(TerminalVO terminalVO : terminalList)
        {
            String memberKey = terminalVO.getMemberId()+"-"+terminalVO.getAccountId()+"-"+terminalVO.getGateway().toUpperCase() + "-" + terminalVO.getCurrency() + "-" + terminalVO.getGateway_id();
            memberMap.put(memberKey,terminalVO);
        }

%>
<form action="/icici/servlet/BankTransactionReport?ctoken=<%=ctoken%>" method="post" name="forms" >
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
                                        <input type="text" readonly class="datepicker" name="startdate" style="width:142px;height: 25px"  value="<%=request.getParameter("startdate")==null?"":request.getParameter("startdate")%>">

                                    </td>
                                    <td width="1%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Time(HH:MM:SS)</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">

                                        <input type="text" name="starttime" value="<%=request.getParameter("starttime")==null?"00:00:00":request.getParameter("starttime")%>" class="txtbox">

                                    </td>
                                    <%
                                        String time = (gateway.equals("")||gateway.equals("null")) ? "00:00:00" : timeDifference;
                                    %>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" >Time Difference</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <input type="text" class="txtbox" disabled value=<%=time%>>

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
                                        <input type="text" readonly class="datepicker" name="enddate"  style="width:142px;height: 25px"value="<%=request.getParameter("enddate")==null?"":request.getParameter("enddate")%>">

                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Time(HH:MM:SS)</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input type="text"   name="endtime" value="<%=request.getParameter("endtime")==null?"23:59:59":request.getParameter("endtime")%>" class="txtbox">

                                    </td>
                                    <td width="6%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Day Light Saving</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <select name="daylightsaving" class="txtbox">
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
                                        <input name="gateway" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Partner Id </td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input name="partnerName" id="pid1" value="<%=partnerName%>" class="txtbox" autocomplete="on">
                                    </td>


                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >Account ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb" >
                                        <input name="accountid" id="accountid1" value="<%=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid")%>" class="txtbox" autocomplete="on">
                                    </td>
                                </tr>

                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>

                                </tr>

                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td colspan="2" class="textb">Member ID</td>
                                    <td colspan="2" class="textb" >
                                        <input name="merchantid" id="memberid1" value="<%=memberid%>" class="txtbox" autocomplete="on">
                                    </td>
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Terminal ID</td>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <input name="terminalid" id="tid3" value="<%=terminalId%>" class="txtbox" autocomplete="on">
                                    </td>


                                    <%--<td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>--%>
                                    <td width="3%" class="textb">&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <button type="submit" class="buttonform" name="button" value="search" >
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb" >
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

        if(BankTransactionInquiry.equals("BankTransactionInquiry")){

    %>
        <div class="panel-heading" > Bank Transaction Inquiry Report</div><br>
            <table border="0" cellpadding="3" cellspacing="3" bordercolor="#2379A5" width="50%"  align="center" class="table table-striped table-bordered table-hover table-green dataTable"  >
                <%

                    if(TransactionInquiry.equals("PayoutInquiry")){

                %>

                <tr>
                    <td  width="50%" class="th0" colspan="6"><b>Payout Success Transaction Count</b></td>
                    <td style="text-align: center"><b><%= payoutsuccessCounter%></b></td>
                </tr>
                <tr>
                    <td  width="50%" class="th0" colspan="6"><b>Payout Failed Transaction Count</b></td>
                    <td style="text-align: center"><b><%= payoutfailCounter%></b></td>
                </tr>
                <tr>
                    <td  width="50%" class="th0" colspan="6"><b>Payout Started Transaction Count</b></td>
                    <td style="text-align: center"><b><%= payoutStarted%></b></td>
                </tr>
                <%
                }
                   else {

                %>

                <tr>
                    <td  width="50%" class=" th0" colspan="6"><b>Success Transaction Count</b></td>
                    <td style="text-align: center"><b><%= successCounter%></b></td>
                </tr>
                <tr>
                    <td  width="50%" class="th0" colspan="6"><b>Fail Transaction Count</b></td>
                    <td style="text-align: center"><b><%= failCounter%></b></td>
                </tr>
                <tr>

                    <td  width="50%" class="th0" colspan="6"><b>AuthStarted Transaction Count</b></td>
                    <td style="text-align: center"><b><%= authStarted%></b></td>
                </tr>
            </table>
            <%

        }
                }
        String status = (String) request.getAttribute("status");

        if(status==null)
        {
            status ="0";
    %>
    <%

        if (transactionDetailsVOs!=null&&transactionDetailsVOs.size()>0)
        {
    %>
        <table border="0" cellpadding="3" cellspacing="3" bordercolor="#2379A5" width="50%"  align="center" class="table table-striped table-bordered table-hover table-green dataTable"  >
            <tr>
                <td  class="textb" colspan="6"><center><b>List Of Tracking ID's</b></center></td>
            </tr>
            <tr <%=style%>>
                <td  class="th0"><b><input type="checkbox" onClick="return ToggleAll(this);" name="alltrans"></b></td>
                <td  class="th0" >Tracking ID</td>
                <td  class="th0" >Terminal ID</td>
                <td  class="th0" >Member ID</td>
                <td  class="th0" >Account ID</td>
                <td  class="th0" >Status</td>
            </tr>

            <%
                String status1 = "";
                for(TransactionDetailsVO transactionDetailsVO:transactionDetailsVOs)
            {
                status1 = transactionDetailsVO.getStatus();
                out.println("<tr>");
                out.println("<td align=\"center\" "+style+">&nbsp;<input type=\"checkbox\" name=\"id\" value="+transactionDetailsVO.getTrackingid()+"></td>");
                out.println("<td align=\"center\" "+style+">"+transactionDetailsVO.getTrackingid()+"</td>");
                out.println("<td align=\"center\" "+style+">"+transactionDetailsVO.getTerminalId()+"</td>");
                out.println("<td align=\"center\" "+style+">"+transactionDetailsVO.getToid()+"</td>");
                out.println("<td align=\"center\" "+style+">"+transactionDetailsVO.getAccountId()+"</td>");
                out.println("<td align=\"center\" "+style+">"+transactionDetailsVO.getStatus()+"</td>");
                out.println("</tr>");
            }
            %>
        </table>
    <%
        if (status1.equalsIgnoreCase("capturesuccess"))
        {
    %>
        <form name="update" action="/icici/servlet/BankTransactionReport?ctoken=<%=ctoken%>" method="post">
            <table width="100%">
                <thead>
                <tr>
                    <td width="15%" align="center">
                        <input id="ids" type="hidden" name="ids" value="">
                        <input type="hidden" name="action1" value="updatesettled">
                        <input type="hidden" name="startdate" value="<%=startDate%>">
                        <input type="hidden" name="starttime" value="<%=startTime%>">
                        <input type="hidden" name="enddate" value="<%=endDate%>">
                        <input type="hidden" name="endtime" value="<%=endTime%>">
                        <input type="hidden" name="gateway" value="<%=pgtypeid%>">
                        <input type="hidden" name="merchantid" value="<%=memberid%>">
                        <input type="hidden" name="accountid" value="<%=accountid%>">
                        <input type="hidden" name="terminalid" value="<%=terminalId%>">
                        <input type="hidden" name="partnerName" value="<%=partnerName%>">
                        <input type="button" name="action1" class="btn btn-default center-block" value="Settle Selected" onclick="return Update();">
                    </td>
                </tr>
                </thead>
            </table>
    </form>
    <%
        }
        else if(status1.equalsIgnoreCase("settled"))
        {
    %>
    <form name="updateSettled" action="/icici/servlet/BankTransactionReport?ctoken=<%=ctoken%>" method="post">
        <table width="100%">
            <thead>
            <tr>
                <td width="15%" align="center">
                    <input id="ids1" type="hidden" name="ids1" value="">
                    <input type="hidden" name="action1" value="updatecapture">
                    <input type="hidden" name="startdate" value="<%=startDate%>">
                    <input type="hidden" name="starttime" value="<%=startTime%>">
                    <input type="hidden" name="enddate" value="<%=endDate%>">
                    <input type="hidden" name="endtime" value="<%=endTime%>">
                    <input type="hidden" name="gateway" value="<%=pgtypeid%>">
                    <input type="hidden" name="merchantid" value="<%=memberid%>">
                    <input type="hidden" name="accountid" value="<%=accountid%>">
                    <input type="hidden" name="terminalid" value="<%=terminalId%>">
                    <input type="hidden" name="partnerName" value="<%=partnerName%>">
                    <input type="button" name="action1" class="btn btn-default center-block" value="Capture Selected" onclick="return UpdateSettled();">
                </td>
            </tr>
            </thead>
        </table>
    </form>
    <%
        }
    %>
<%
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
</div>
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