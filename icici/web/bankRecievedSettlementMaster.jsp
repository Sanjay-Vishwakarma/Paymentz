<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>
<%@ page import="static com.directi.pg.Functions.convertStringtoInt" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="org.owasp.esapi.ValidationErrorList" %>
<%@ page import="com.manager.vo.BankRecievedSettlementCycleVO" %>
<%@ page import="com.manager.GatewayManager" %>
<%@ page import="com.manager.vo.gatewayVOs.GatewayTypeVO" %>
<%@ page import="com.manager.vo.gatewayVOs.GatewayAccountVO" %>
<%@ page import="com.manager.vo.PaginationVO" %>
<%@ page import="com.manager.vo.ActionVO" %>
<%@ page import="com.manager.dao.MerchantDAO" %>
<%@ page import="com.manager.vo.MerchantDetailsVO" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 8/30/14
  Time: 5:04 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="index.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<head>
    <%--Datepicker css format--%>
    <%--<link rel="stylesheet" href="/icici/olddatepicker1/jquery-ui.css">
    <script src="/icici/olddatepicker1/jquery-1.9.1.js"></script>
    <script src="/icici/olddatepicker1/jquery-ui.js"></script>
    <link rel="stylesheet" href="/resources/demos/style.css">
    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: 'dd/mm/yy'});
        });
    </script>--%>


        <script type="text/javascript" language="JavaScript" src="/icici/javascript/accountid.js"></script>

        <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
        <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

        <script type="text/javascript">
            $('#sandbox-container input').datepicker({
            });
        </script>
        <script>
            $(function() {
                $( ".datepicker" ).datepicker({dateFormat: 'yy-mm-dd'});
            });
        </script>

            <title>Admin |Bank Settlement</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String str = "";
        String pgtypeid = "";
        String currency ="";
        currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");
        pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
        String accountid2 = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");

        List<String> gatewayCurrency = GatewayTypeService.loadCurrency();
        List<String> gatwayName = GatewayTypeService.loadGateway();
        Hashtable accountDetails = GatewayAccountService.getCommonAccountDetail();

        if(pgtypeid!=null)str = str + "&pgtypeid=" + pgtypeid;
        else
            pgtypeid="";

        if(currency!=null)str = str + "&currency=" + currency;
        else
            currency="";

        if(accountid2!=null)str = str + "&accountid=" + accountid2;
        else
            accountid2="";


%>
<div class="row" style="margin-left: 210px;">
    <div class="col-lg-12">
        <div class="panel panel-default" style="margin-left:0px">
            <div class="panel-heading" >
                Bank RecievedSettlementMaster
                <div style="float: right;">
                    <form action="/icici/bankRecievedSettlementMaster.jsp?ctoken=<%=ctoken%>" method="POST">

                        <button type="submit" class="addnewmember" value="Add" name="submit" style="width:300px ">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add Bank Received Settlement
                        </button>
                    </form>
                </div>
            </div>
            <form action="/icici/servlet/BankReceivedSettlementList?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
                <%!
                    Logger logger = new Logger("test");
                    MerchantDAO merchantDAO = new MerchantDAO();
                %>
                <%
                    GatewayManager gatewayManager = new GatewayManager();


                    List<GatewayTypeVO> gatewayTypeVOList=gatewayManager.getListOfAllGatewayType();
                    List<GatewayAccountVO> gatewayAccountVOList= gatewayManager.getListOfAllGatewayAccount();

                    String selected="";
                    HashMap<String,String> dropdown = new HashMap<String, String>();
                    dropdown.put("Y","YES");
                    dropdown.put("N","NO");
                %>
                <%
                    try
                    {
                        session.setAttribute("submit","Reports");
                        String fromdate = null;
                        String todate = null;
                        try
                        {
                            Date date = new Date();
                            SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy");

                            String Date = originalFormat.format(date);

                            fromdate = Functions.checkStringNull(request.getParameter("fromdate")) == null ?"" : request.getParameter("fromdate");
                            todate = Functions.checkStringNull(request.getParameter("todate")) == null ? "" : request.getParameter("todate");
                        }
                        catch (Exception e)
                        {
                            logger.error("JSP page exception ::",e);
                        }

                %>
                <%
                    if(request.getParameter("MES")!=null)
                    {
                        String mes=request.getParameter("MES");
                        if(mes.equals("ERR"))
                        {
                            ValidationErrorList error= (ValidationErrorList) request.getAttribute("error");
                            for(Object errorList : error.errors())
                            {
                                ValidationException ve = (ValidationException) errorList;
                                out.println("<center><font class=\"textb\">" + ve.getMessage() + "</font></center>");
                            }
                        }

                    }
                %>

                <table  align="center" width="100%" cellpadding="2" cellspacing="2" style="margin-left:1%;">

                    <tr>
                        <td>

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >From*</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input size="6" name="fromdate" readonly class="datepicker" style="width: 142px;height: 25px;" value="<%=fromdate%>">

                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >To*</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <input size="6" name="todate" style="width: 142px;height: 25px;" readonly class="datepicker" value="<%=todate%>">

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
                                    <td width="8%" class="textb" >Account/gateway</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb"><select size="1" id="bank" class="txtbox"  name="pgtypeid" style="width:144px;margin-right: 50px">
                                            <option value="" default>--All--</option>
                                            <%
                                                StringBuilder sb = new StringBuilder();
                                                for(String gatewayType : gatwayName)
                                                {
                                                    String st = "";
                                                    String name = gatewayType;
                                                    if(name != null)
                                                    {
                                                        //newly added upercase
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
                                    <td width="8%" class="textb">Currency</td>
                                    <td width="3%" class="textb">&nbsp;</td>
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

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb">Account/id</td>
                                    <td width="12%" class="textb">
                                        <select size="1" id="accountid" name="accountid" class="txtbox" style=width:150px>
                                            <option data-bank="all" data-curr="all" value="0">Select AccountID</option>
                                            <%
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
                                    <td width="12%" class="textb">&nbsp;</td>

                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" >&nbsp;</td>
                                    <td width="12%" class="textb">
                                        <button type="submit" class="buttonform" value="Submit">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
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
<div class="reporttable" style="margin-left: 222px;margin-right: 11px;">

<%

    List<BankRecievedSettlementCycleVO> bankSettlementCycleVOList = (List<BankRecievedSettlementCycleVO>) request.getAttribute("BankSettlementCycleVOs");
    if(bankSettlementCycleVOList !=null)
    {
        if(bankSettlementCycleVOList.size()>0)
        {
            PaginationVO paginationVO = (PaginationVO) request.getAttribute("paginationVO");
            paginationVO.setInputs(paginationVO.getInputs()+"&ctoken="+ctoken);

            int srno=((paginationVO.getPageNo()-1)*paginationVO.getRecordsPerPage())+1;
%>
<form action="/icici/servlet/ViewOrEditBankReceivedSettlement?ctoken=<%=ctoken%>" method="post">
    <center><h4 class="textb"><b>Bank Recieved Settlement List</b></h4></center>

    <table align=center width="80%" border="1" class="table table-striped table-bordered table-green dataTable">
        <tr>
            <td width="2%" class="th0">Sr&nbsp;No</td>
            <td width="8%" class="th0">Settlement Date</td>
            <td width="8%" class="th0">Expected StartDate</td>
            <td width="8%" class="th0">Expected EndDate</td>
            <td width="8%" class="th0">Actual StartDate</td>
            <td width="8%" class="th0">Actual EndDate</td>
            <td width="8%" class="th0">Bank SettlementId</td>
            <td width="8%" class="th0">AccountId</td>
            <td width="8%" class="th0">PgtypeId</td>
            <td width="8%" class="th0">MId</td>
            <td width="8%" class="th0">isSettlement<br>CronExecuted</td>
            <td width="8%" class="th0">isPayout<br>CronExecuted</td>
            <td colspan="2" class="th0" >Action</td>
        </tr>
        <%
            for(BankRecievedSettlementCycleVO bankSettlementCycleVO : bankSettlementCycleVOList)
            {

        %>
        <tr>

            <td class="tr0" align="center"><%=srno%></td>
            <td class="tr0" align="center"><%=bankSettlementCycleVO.getSettlementDate()%></td>
            <td class="tr0" align="center"><%=bankSettlementCycleVO.getExpected_startDate()%></td>
            <td class="tr0" align="center"><%=bankSettlementCycleVO.getExpected_endDate()%></td>
            <td class="tr0" align="center"><%=bankSettlementCycleVO.getActual_startDate()%></td>
            <td class="tr0" align="center"><%=bankSettlementCycleVO.getActual_endDate()%></td>
            <td class="tr0" align="center"><%=bankSettlementCycleVO.getBank_settlementId()%></td>
            <td class="tr0" align="center"><%=bankSettlementCycleVO.getAccountId()%></td>
            <td class="tr0" align="center"><%=bankSettlementCycleVO.getPgTypeId()%></td>
            <td class="tr0" align="center"><%=bankSettlementCycleVO.getMerchantId()%></td>
            <td class="tr0" align="center"><%=bankSettlementCycleVO.getSettlementCronExecuted()%></td>
            <td class="tr0" align="center"><%=bankSettlementCycleVO.getPayoutCronExecuted()%></td>
            <td class="tr0" align="center">
                <button type="submit" class="goto" value="<%=bankSettlementCycleVO.getBankSettlementReceivedId()+"_View"%>"  name="action">View</button>
            </td>
            <td class="tr0" style="border: 1px solid black">
                <button type="submit"  class="goto" value="<%=bankSettlementCycleVO.getBankSettlementReceivedId()+"_Edit"%>" name="action">Edit</button>
            </td>
        </tr>

        <%
                srno++;
            }
        %>
    </table>
</form>
<div >
    <center>
        <jsp:include page="page.jsp" flush="true">
            <jsp:param name="numrecords" value="<%=paginationVO.getTotalRecords()%>"/>
            <jsp:param name="numrows" value="<%=paginationVO.getRecordsPerPage()%>"/>
            <jsp:param name="pageno" value="<%=paginationVO.getPageNo()%>"/>
            <jsp:param name="str" value="<%=paginationVO.getInputs()%>"/>
            <jsp:param name="page" value="<%=paginationVO.getPage()%>"/>
            <jsp:param name="currentblock" value="<%=paginationVO.getCurrentBlock()%>"/>
            <jsp:param name="orderby" value=""/>
        </jsp:include>
    </center>
</div>

<%
    }
    else
    {
        out.println(Functions.NewShowConfirmation("Sorry","No records found"));
    }
}
else
{
    //action specific code
    BankRecievedSettlementCycleVO singleBankRecievedSettlementCycleVO= (BankRecievedSettlementCycleVO) request.getAttribute("singleBankRecievedSettlementCycleVO");
    if(request.getParameter("UPDATE")!=null)
    {
        String update=request.getParameter("UPDATE");
        if(update.equals("Success"))
        {
            out.println("<center><font class=\"textb\">Bank received settlement updated</font></center>");
        }
    }
    if(singleBankRecievedSettlementCycleVO!=null || ("Add").equals(request.getParameter("submit")))
    {


        String bankSettlementReceivedId = "";
        String settlementDate = "";
        String expected_startDate = "";
        String expected_EndDate = "";
        String actual_startDate = "";
        String actual_endDate = "";
        String settlementCycle_id = "";
        String accountId = "";
        String pgTypeId = "";
        String merchantId = "";
        String isSettlementCronExecuted="N";
        String isPayoutCronExecuted="N";
        ActionVO actionVO = null;
        if (("Add").equals(request.getParameter("submit")))
        {
            actionVO= new ActionVO();
            actionVO.setAdd();
        }
        if (!("Add").equals(request.getParameter("submit")))
        {
            actionVO = (ActionVO) request.getAttribute("actionVO");
            bankSettlementReceivedId = singleBankRecievedSettlementCycleVO.getBankSettlementReceivedId();
            settlementDate = singleBankRecievedSettlementCycleVO.getSettlementDate();
            expected_startDate = singleBankRecievedSettlementCycleVO.getExpected_startDate();
            expected_EndDate = singleBankRecievedSettlementCycleVO.getExpected_endDate();
            actual_startDate = singleBankRecievedSettlementCycleVO.getActual_startDate();
            actual_endDate = singleBankRecievedSettlementCycleVO.getActual_endDate();
            settlementCycle_id = singleBankRecievedSettlementCycleVO.getBank_settlementId();
            accountId = singleBankRecievedSettlementCycleVO.getAccountId();
            pgTypeId = singleBankRecievedSettlementCycleVO.getPgTypeId();
            merchantId = singleBankRecievedSettlementCycleVO.getMerchantId();
            isSettlementCronExecuted=singleBankRecievedSettlementCycleVO.getSettlementCronExecuted();
            isPayoutCronExecuted=singleBankRecievedSettlementCycleVO.getPayoutCronExecuted();

            session.setAttribute("singleBankRecievedSettlementCycleVO",singleBankRecievedSettlementCycleVO);
        }

%>
<form action="/icici/servlet/UpdateBankReceivedSettlement?ctoken=<%=ctoken%>" method="post">
    <script type="text/javascript" language="JavaScript" src="https://ajax.googleapis.com/ajax/libs/jquery/2.0.0/jquery.min.js"></script>
    <script language="javascript">

        $(function(){
            $('#bank2').on('change', function(){
                var val = $(this).val();
                var sub = $('#accountid2');
                var val2 = $('#currency2').val();
                if(val == '--All--') {
                    if(val2 == '--All--') {
                        $('#accountid2').find('option').show();
                    }
                    else {
                        sub.find('option').not(':first').hide();
                        $('option', sub).filter(function(){
                            if($(this).attr('data-curr') == val2){
                                $(this).show();
                            }
                        });
                    }
                }
                else {

                    if(val2 == '--All--') {
                        sub.find('option').not(':first').hide();
                        $('option', sub).filter(function(){
                            if($(this).attr('data-bank') == val){
                                $(this).show();
                            }
                        });
                    }
                    else {
                        sub.find('option').not(':first').hide();
                        $('option', sub).filter(function(){
                            if($(this).attr('data-bank') == val && $(this).attr('data-curr') == val2){
                                $(this).show();
                            }
                        });
                    }

                }
                sub.val(0);
            });
        });


        $(function(){
            $('#currency2').on('change', function(){
                var val = $(this).val();
                var sub = $('#accountid2');
                var val2 = $('#bank2').val();
                if(val == '--All--') {
                    if(val2 == '--All--') {
                        $('#accountid2').find('option').show();
                    }
                    else {
                        sub.find('option').not(':first').hide();
                        $('option', sub).filter(function(){
                            if($(this).attr('data-bank') == val2){
                                $(this).show();
                            }
                        });
                    }
                }
                else {
                    if(val2 == '--All--') {
                        sub.find('option').not(':first').hide();
                        $('option', sub).filter(function(){
                            if($(this).attr('data-curr') == val){
                                $(this).show();
                            }
                        });
                    }
                    else {
                        sub.find('option').not(':first').hide();
                        $('option', sub).filter(function(){
                            if($(this).attr('data-curr') == val && $(this).attr('data-bank') == val2){
                                $(this).show();
                            }
                        });
                    }

                }
                sub.val(0);
            });
        });
    </script>

    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>

    <script type="text/javascript">
        $('#sandbox-container input').datepicker({
        });
    </script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: 'yy-mm-dd'});
        });
    </script>

    <table align=center width="80%" border="0">
        <tr>
            <td align="left" colspan="2"><center><B><%if (actionVO.isView())
            {%>
                View Bank Received Settlement
                <%
                    }
                    if (actionVO.isEdit())
                    {
                %>
                Edit Bank Received Settlement
                <%
                    }
                    if(actionVO.isAdd())
                    {
                %>
                Add new bank received settlement
                <%
                    }
                %>
            </B></center> </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <%if (!actionVO.isAdd()){%>
        <tr class="tr0">
            <td valign="middle" align="center" class="td0">Id</td> <td  class="td0"><input type="text" name="bankreceivedid" value="<%=bankSettlementReceivedId%>"  readonly=""></td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <%}else{%>
        <tr class="tr0">
            <td valign="middle" align="center" class="td0">Day Light Saving</td> <td  class="td0"><select type="text" name="isdaylight"  <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
            <%
                for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                {
                    out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                }
            %>
        </select>
        </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <%}%>
        <tr class="tr0">
            <td valign="middle" align="center" class="td0">Gateway</td> <td  class="td0">
            <select size="1" id="bank2" class="txtbox" name="pgtypeid" >
                <option value="--All--" default>--All--</option>
                <%
                    StringBuilder sb2 = new StringBuilder();
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
                            sb2.append(st);
                        }
                    }
                %>
                <%=sb2.toString()%>
            </select>
        </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr class="tr0">
            <td valign="middle" align="center" class="td0">Currency</td> <td  class="td0">
            <select size="1" id="currency2" class="txtbox" name="currency">
                <option value="--All--" default>--All--</option>
                <%
                    StringBuilder sb3 = new StringBuilder();
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
                            sb3.append(st);
                        }

                    }
                %>
                <%=sb3.toString()%>
            </select>
        </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr class="tr0">
            <td valign="middle" align="center" class="td0">Accounts</td> <td  class="td0">
            <select size="1" id="accountid2" name="accountid" class="txtbox">
                <option data-bank="all" data-curr="all" value="">Select AccountID</option>
                <%
                    TreeSet accountSet2 = new TreeSet<Integer>();
                    accountSet2.addAll(accountDetails.keySet());
                    Iterator enu4 = accountSet2.iterator();
                    String selected4 = "";
                    GatewayAccount value4 = null;
                    while (enu4.hasNext())
                    {
                        value4 = (GatewayAccount)accountDetails.get(enu4.next());
                        int acId = value4.getAccountId();
                        String currency2 = value4.getCurrency();
                        String mid = value4.getMerchantId();
                        String gateway2 = value4.getGateway();
                        String gatewayName = value4.getGatewayName();
                        //newly added
                        if (String.valueOf(acId).equals(accountid2))
                            selected4 = "selected";
                        else
                            selected4 = "";

                %>
                <option data-bank="<%=value4.getGateway()%>" data-curr="<%=value4.getCurrency()%>" value="<%=value4.getAccountId()%>" <%=selected4%>><%=acId+"-"+currency2+"-"+mid+"-"+gateway2+"-"+gatewayName%></option>
                <%
                    }
                %>
            </select>
            </td>
        </tr>

        <%if (actionVO.isView())
        {
        %>
        <tr><td>&nbsp;</td></tr>
        <tr class="tr0">
            <td  valign="middle" align="center" class="td0">MerchantId</td> <td  class="td0"><input type="text" name="mid" value="<%=merchantId%>" disabled>
            <%
                }
            %>
        </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr class="tr0">
            <td valign="middle" align="center" class="td0">Expected&nbsp;Start&nbsp;Date</td> <td class="td0"><input type="text" class="datepicker" readonly name="expected_startDate" value="<%=expected_startDate%>" <%if (actionVO.isView()){%> disabled><%}%></td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr class="tr0">
            <td valign="middle" align="center" class="td0">Expected End Date</td>  <td  class="td0"><input type="text" class="datepicker" readonly name="expected_endDate" value="<%=expected_EndDate%>" <%if (actionVO.isView()){%> disabled><%}%></td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <%if (!actionVO.isAdd()){%>
        <tr class="tr0">
            <td  valign="middle" align="center" class="td0">Actual&nbsp;Start&nbsp;Date</td> <td  class="td0"><input type="text" class="datepicker" readonly name="actual_startDate" value="<%=actual_startDate%>" <%if (actionVO.isView()){%> disabled><%}%></td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr class="tr0">
            <td  valign="middle" align="center" class="td0">Actual&nbsp;End&nbsp;Date</td> <td  class="td0"><input type="text" class="datepicker" readonly name="actual_endDate" value="<%=actual_endDate%>" <%if (actionVO.isView()){%> disabled><%}%></td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <%}%>
        <tr class="tr0">
            <td  valign="middle" align="center" class="td0">Bank&nbsp;SettlementCycle&nbsp;Id</td> <td  class="td0"><input type="text" class="txtbox" name="settlementcycleid"  value="<%=settlementCycle_id%>" <%if (!actionVO.isAdd()){%> readonly=""><%}%></td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <%if (!actionVO.isAdd()){%>
        <tr class="tr0">
            <td valign="middle" align="center" class="td0">Settlement&nbsp;Date</td>  <td  class="td0"><input type="text" name="settlementdate" class="datepicker" readonly value="<%=settlementDate%>" <%if (actionVO.isView()){%> disabled><%}%></td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr class="tr0">
            <td  valign="middle" align="center" class="td0">isSettlementCronExecuted</td> <td  class="td0"><select type="text" name="issettlementcron"  <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
            <%
                for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                {
                    selected="";
                    if(yesNoPair.getKey().equals(isSettlementCronExecuted))
                    {
                        selected="selected";
                    }
                    out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                }
            %>
        </select>
        </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr class="tr0">
            <td  valign="middle" align="center" class="td0">isPayoutCronExecuted</td> <td  class="td0"><select type="text" name="ispayoutcron"  <%if (actionVO.isView()){%> disabled><%}else{ out.print(">");}%>
            <%
                for(Map.Entry<String,String> yesNoPair : dropdown.entrySet())
                {
                    selected="";
                    if(yesNoPair.getKey().equals(isPayoutCronExecuted))
                    {
                        selected="selected";
                    }
                    out.println("<option value="+yesNoPair.getKey()+" "+selected+">"+yesNoPair.getValue()+"</option>");
                }
            %>
        </select>
        </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <%}%>
        <%if (actionVO.isEdit())
        {
        %>
        <tr class="tr0">
            <td colspan="2" valign="middle" align="center"  ><button type="submit" name="action" class="buttonform" value="<%=singleBankRecievedSettlementCycleVO.getBankSettlementReceivedId()%>_Update">Update</button></td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <%
            }
            if (actionVO.isAdd())
            {
        %>
        <tr class="tr0">
            <td colspan="2" valign="middle" align="center"  ><input type="hidden" name="submit" value="Add"><button type="submit" name="action" value="1_Add" class="buttonform">Add</button></td>
        </tr>
        <%
            }
        %>
        <tr><td>&nbsp;</td></tr>
    </table>
</form>

<%
            }
            else
            {
                out.println(Functions.NewShowConfirmation("Filter","fill in the required data for Bank Settlement List"));
            }
        }
    }
    catch (Exception e)
    {
        logger.error(" bankRecievedSettlementMaster.jsp error::",e);
    }
    }
%>
</div>

</body>
</html>c