<%@ page import="com.directi.pg.Database" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.errors.ValidationException" %>

<%@ page import="static com.directi.pg.Functions.convertStringtoInt" %>
<%@ page import="java.util.*" %>
<%--
  Created by IntelliJ IDEA.
  User: NISHANT
  Date: 6/4/14
  Time: 1:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="index.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Logger"%>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.manager.PayoutManager" %>
<%@ page import="com.manager.dao.PayoutDAO" %>

<%!
    private static Logger logger=new Logger("actionwiremanager.jsp");
%>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/gateway-account-member-terminalid.js"></script>

<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <link rel="stylesheet" href="//code.jquery.com/ui/1.10.4/themes/smoothness/jquery-ui.css">
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script type="text/javascript" language="JavaScript" src="/icici/javascript/memberid_filter.js"></script>

    <script language="javascript">

        function getPdfFile(settledid)
        {
            if (confirm("Do you really want to downloads selected file."))
            {
                document.getElementById("pdfform"+settledid).submit();
            }
        }

    </script>

    <script language="javascript">

        function getWordFile(settledid)
        {
            if (confirm("Do you really want to downloads selected file."))
            {
                document.getElementById("wordform"+settledid).submit();
            }
        }

    </script>

    <script type="text/javascript">
        $('#sandbox-container input').datepicker({

        });
    </script>
    <script>
        var count=0;
        $(function() {
            $( ".datepicker" ).datepicker();

        });
        function addTextBox()
        {
            if(count<4)
            {
                count++;
                var element = document.createElement("input");
                element.setAttribute("type", "text");
                element.setAttribute("name", "receiverBankDetails_" + count);
                element.setAttribute("class", "txtbox1");
                element.setAttribute("style", "margin-right:5px;margin-top:5px;");
                element.setAttribute("size", "30");
                var spanvar = document.getElementById("beneficiaryText");
                spanvar.appendChild(element);
                document.getElementById("receiverBankDetailsCount").value=count;
            }
        }
        function showDetails()
        {
            count=document.getElementById("receiverBankDetailsCount").value;
            console.log("count---->"+count)
            var receiverBankDetails=document.getElementById("receiverBankDetails").value;
            var disabled=document.getElementById("receiverBankDetails").getAttributeNode("disabled")!=null?true:false;
            if(receiverBankDetails.includes(","))
            {
                var s=receiverBankDetails.split(",");
                for(var i=0;i< s.length;i++)
                {
                    if(i!=0)
                    {
                        var element = document.createElement("input");
                        element.setAttribute("type", "text");
                        element.setAttribute("name", "receiverBankDetails_" + i);
                        element.setAttribute("class", "txtbox1");
                        element.setAttribute("style", "margin-right:5px;margin-top:5px;");
                        element.setAttribute("size", "30");
                        element.setAttribute("value", s[i]);
                        element.disabled=disabled;
                        var spanvar = document.getElementById("beneficiaryText");
                        spanvar.appendChild(element);
                    }
                    else
                    {
                        document.getElementById("receiverBankDetails").value=s[i];
                    }
                }
            }
        }
    </script>

    <%--<script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-account-member.js"></script>--%>
    <title></title>
</head>
<body onload="showDetails()">
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {

%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Merchant Wire Manager
                <div style="float: right;">
                    <form action="/icici/addwire.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" value="Add New wire" name="submit" class="addnewmember" style="width:300px ">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New wire
                        </button>
                    </form>
                </div>
                <div style="float: right;">
                    <form action="/icici/addWireForAllTerminal.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" value="Add New wire" name="submit" class="addnewmember" style="width:300px ">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New wire For ALL Terminal
                        </button>
                    </form>
                </div>
            </div><br>
            <form action="/icici/servlet/WireList?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <%
                    String str="";
                    String fdate=null;
                    String tdate=null;
                    String fmonth=null;
                    String tmonth=null;
                    String fyear=null;
                    String tyear=null;
                    String toid=null;
                    String accountid=null;
                    String terminalid=null;
                    String ispaid=null;

                    String pgtypeid = "";

                    pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
                    String accountid2 = Functions.checkStringNull(request.getParameter("accountid"));
                    String memberid = Functions.checkStringNull(request.getParameter("toid")) == null ? "" : request.getParameter("toid");
                    PayoutManager payoutManager = new PayoutManager();
                    List<TerminalVO> sTerminal = payoutManager.getTerminalForMerchantWireList();

                    TerminalManager terminalManager = new TerminalManager();
                    List<TerminalVO> terminalList = terminalManager.getAllMappedTerminals();
                    TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();
                    TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();
                    TreeMap<String,TerminalVO> memberMap = new TreeMap<String, TerminalVO>();

                    Map terminalMap = GatewayTypeService.getAllTerminalsGroupByMerchant();
                    for(TerminalVO terminalVO : terminalList)
                    {
                        String memberKey = terminalVO.getMemberId()+"-"+terminalVO.getAccountId()+"-"+terminalVO.getGateway().toUpperCase() + "-" + terminalVO.getCurrency() + "-" + terminalVO.getGateway_id();
                        memberMap.put(memberKey,terminalVO);
                    }

                    try
                    {
                        fdate = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"Days",2,true);
                        tdate = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"Days",2,true);
                        fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
                        tmonth =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
                        fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
                        tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);
                        toid = ESAPI.validator().getValidInput("toid",request.getParameter("toid"),"Numbers",10,true);
                        accountid = ESAPI.validator().getValidInput("accountid",request.getParameter("accountid"),"Numbers",10,true);
                        terminalid = ESAPI.validator().getValidInput("accountid",request.getParameter("tyear"),"Numbers",10,true);
                    }
                    catch(ValidationException e)
                    {
                        logger.error("validation exception",e);
                    }
                    Calendar rightNow = Calendar.getInstance();
                    if (fdate == null) fdate = "" + 1;
                    if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
                    if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
                    if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
                    if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
                    if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);
                    str = str + "ctoken=" + ctoken;
                    if (fdate != null) str = str + "&fdate=" + fdate;
                    if (tdate != null) str = str + "&tdate=" + tdate;
                    if (fmonth != null) str = str + "&fmonth=" + fmonth;
                    if (tmonth != null) str = str + "&tmonth=" + tmonth;
                    if (fyear != null) str = str + "&fyear=" + fyear;
                    if (tyear != null) str = str + "&tyear=" + tyear;
                    if (toid != null) str = str + "&toid=" + toid;
                    if (accountid != null) str = str + "&accountid=" + accountid;
                    if (terminalid != null) str = str + "&terminalid=" + terminalid;

                    if(pgtypeid!=null)str = str + "&pgtypeid=" + pgtypeid;
                    else
                        pgtypeid="";

                    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
                    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

                    str = str + "&SRecords=" + pagerecords;

                    int year = Calendar.getInstance().get(Calendar.YEAR);
                    String status = (String) request.getAttribute("message");
                    Functions functions1=new Functions();
                    if(functions1.isValueNull(status))
                    {
                        out.println("<center> <font class=\"textb\" size=\"28\" style=\"display: inline-block;\" face=\"arial\"> " + status + "</font></center>");
                    }
                %>
                <table border="0" cellpadding="5" cellspacing="0" width="95%"  align="center">
                    <tr><td colspan="12">&nbsp;</td></tr>
                    <tr>
                        <td colspan="2" class="textb" >From</td>
                        <td colspan="2" class="textb">

                            <select size="1" name="fdate"  value="<%=request.getParameter("fdate")==null?"":request.getParameter("fdate")%>" >
                                <%
                                    if (fdate != null)
                                        out.println(Functions.dayoptions(1, 31, fdate));
                                    else
                                        out.println(Functions.printoptions(1, 31));
                                %>
                            </select>
                            <select size="1" name="fmonth"  value="<%=request.getParameter("fmonth")==null?"":request.getParameter("fmonth")%>" >


                                <%

                                    if (fmonth != null)
                                        out.println(Functions.newmonthoptions(1, 12, fmonth));
                                    else
                                        out.println(Functions.printoptions(1, 12));
                                %>
                            </select>
                            <select size="1" name="fyear"  value="<%=request.getParameter("fyear")==null?"":request.getParameter("fyear")%>" >
                                <%
                                    if (fyear != null)
                                        out.println(Functions.yearoptions(2005, year, fyear));
                                    else
                                        out.println(Functions.printoptions(2005, year));
                                %>
                            </select>
                        </td>
                        <td colspan="2" class="textb" >To</td>
                        <td colspan="2" class="textb">
                            <select size="1" name="tdate" >
                                <%
                                    if (tdate != null)
                                        out.println(Functions.dayoptions(1, 31, tdate));
                                    else
                                        out.println(Functions.printoptions(1, 31));
                                %>
                            </select>

                            <select size="1" name="tmonth" >
                                <%
                                    if (tmonth != null)
                                        out.println(Functions.newmonthoptions(1, 12, tmonth));
                                    else
                                        out.println(Functions.printoptions(1, 12));
                                %>
                            </select>

                            <select size="1" name="tyear" >
                                <%
                                    if (tyear != null)
                                        out.println(Functions.yearoptions(2005, year, tyear));
                                    else
                                        out.println(Functions.printoptions(2005, year));
                                %>
                            </select>
                        </td>
                        <%-- <td colspan="1" class="textb">Terminal ID</td>
                         <td colspan="2" class="textb">
                             <select size="1" class="txtbox" name="terminalid">
                                 <option value="" selected>--ALL--</option>
                                 <%
                                     for(TerminalVO terminalVO:sTerminal)
                                     {
                                         String isSelected="";
                                         if(terminalVO.getTerminalId().equalsIgnoreCase(terminalid))
                                             isSelected="selected";
                                         else
                                             isSelected="";
                                 %>
                                 <option value="<%=terminalVO.getTerminalId()%>" <%=isSelected%>><%=terminalVO.getTerminalId()%></option>
                                 <%
                                     }
                                 %>
                             </select>
                         </td>--%>

                        <%--<td>&nbsp;&nbsp;&nbsp;&nbsp;</td>
                        <td colspan="2" class="textb">Is Paid</td>
                        <td colspan="2" class="textb">
                            <Select name="isrr"><option value=""> </option><option value="N">Unpaid </option> <option value="Y"> Paid </option> </Select>
                        </td>--%>
                        <td class="textb" colspan="2">Is Paid</td>
                        <td class="textb" colspan="2">
                            <select name="isrr" >
                                <%
                                    if("paid".equals(ispaid))
                                    {
                                %>
                                <option value="">--ALL--</option>
                                <option value="paid" selected>Paid</option>
                                <option value="unpaid">UnPaid</option>
                                <option value="carryforward"> CARRYFORWARD</option>
                                <option value="partialPaid"> PARTIAL PAID</option>
                                <%
                                }
                                else if("unpaid".equals(ispaid))
                                {
                                %>
                                <option value="">--ALL--</option>
                                <option value="unpaid" selected>Unpaid</option>
                                <option value="paid">Paid</option>
                                <option value="carryforward"> CARRYFORWARD</option>
                                <option value="partialPaid"> PARTIAL PAID</option>
                                <%
                                }
                                else if("carryforward".equals(ispaid))
                                {
                                %>
                                <option value="">--ALL--</option>
                                <option value="unpaid">Unpaid</option>
                                <option value="paid">Paid</option>
                                <option value="carryforward" selected> CARRYFORWARD</option>
                                <option value="partialPaid"> PARTIAL PAID</option>
                                <%
                                }
                                else if("partialPaid".equals(ispaid))
                                {
                                %>
                                <option value="">--ALL--</option>
                                <option value="unpaid">Unpaid</option>
                                <option value="paid">Paid</option>
                                <option value="carryforward"> CARRYFORWARD</option>
                                <option value="partialPaid" selected> PARTIAL PAID</option>
                                <%
                                }
                                else
                                {
                                %>
                                <option value="" selected>--ALL--</option>
                                <option value="unpaid" >Unpaid</option>
                                <option value="paid">Paid</option>
                                <option value="carryforward"> CARRYFORWARD</option>
                                <option value="partialPaid"> PARTIAL PAID</option>
                                <% }%>
                            </select>
                        </td>
                    </tr>

                    <tr><td colspan="12">&nbsp;</td></tr>
                    <tr><td colspan="12">&nbsp;</td></tr>

                    <tr>
                        <td colspan="2" class="textb">Gateway</td>
                        <td colspan="2" class="textb">
                            <input name="pgtypeid" id="gateway" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
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

                        <td colspan="2" class="textb">AccountID</td>
                        <td colspan="2" class="textb" >
                            <input name="accountid" id="accountid" value="<%=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid")%>" class="txtbox" autocomplete="on">
                            <%-- <select size="1" id="accountid" name="accountid" class="txtbox" style="width: 200px">
                                    <option data-bank="all" value="0">---ALL---</option>
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

                        <td colspan="2" class="textb">Member ID</td>
                        <td class="textb" >
                            <input name="toid" id="memberid" value="<%=memberid%>" class="txtbox" autocomplete="on">
                            <%-- <select name="toid" id="memberid" class="txtbox" style="width:200px;">
                                 <option data-bank="all"  data-accid="all" value="0" selected>--ALL--</option>
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
                                         if(String.valueOf(t.getMemberId()+"-"+accId).equalsIgnoreCase(memberid+"-"+accId))
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

                        <td colspan="1" class="textb">Terminal ID</td>
                        <td colspan="2" class="textb">
                            <input name="terminalid" id="tid" value="<%=Functions.checkStringNull(request.getParameter("terminalid"))==null?"":request.getParameter("terminalid")%>" class="txtbox" autocomplete="on">
                        </td>

                        <td colspan="1" class="textb">Report ID</td>
                        <td colspan="2" class="textb">
                            <input name="reportid" value="<%=Functions.checkStringNull(request.getParameter("reportid"))==null?"":request.getParameter("reportid")%>" class="txtbox">
                        </td>
                    </tr>

                    <tr><td colspan="12">&nbsp;</td></tr>

                    <tr>
                        <td colspan="12" class="textb">
                            <button type="submit" class="buttonform" style="margin-left: 95%">
                                <i class="fa fa-clock-o"></i>
                                &nbsp;&nbsp;Search
                            </button>
                        </td>
                    </tr>
                    <tr><td colspan="12">&nbsp;</td></tr>
                </table>
            </form>
        </div>
    </div>
</div>
<div class="reporttable">
    <%
        Functions functions=new Functions();
        String action = (String) request.getAttribute("action");
        String conf = " ";
        if (action != null)
        {
            if (action.equalsIgnoreCase("view"))
            {
                conf = "disabled";
            }
            Hashtable data = (Hashtable) request.getAttribute("data");
            if (data != null)
            {
                String style="class=tr1";
                Hashtable innerhash = (Hashtable) data.get(1 + "");
                String payoutCurrency="";
                String payoutAmount="";
                String payerBankDetails="";
                String receiverBankDetails="";
                String paymentReceiptDate="";
                String conversionRate="";
                String settledid=(String)innerhash.get("settledid");
                String reportid=(String) innerhash.get("reportid");
                if(!functions.isValueNull(reportid)){
                    reportid = String.valueOf(PayoutDAO.UpdateOLDReportid(settledid));
                }
                if(functions.isValueNull((String)innerhash.get("payoutCurrency")))
                    payoutCurrency=(String)innerhash.get("payoutCurrency");
                if(functions.isValueNull((String)innerhash.get("payoutAmount")))
                    payoutAmount=(String)innerhash.get("payoutAmount");
                if(functions.isValueNull((String)innerhash.get("payerBankDetails")))
                    payerBankDetails=(String)innerhash.get("payerBankDetails");
                if(functions.isValueNull((String)innerhash.get("receiverBankDetails")))
                    receiverBankDetails=(String)innerhash.get("receiverBankDetails");
                if(functions.isValueNull((String) innerhash.get("paymentReceiptDate")))
                    paymentReceiptDate=(String) innerhash.get("paymentReceiptDate");
                if(functions.isValueNull((String) innerhash.get("conversionRate")))
                    conversionRate=(String) innerhash.get("conversionRate");
                String[] receiverBankDetails1= null;
                int count=0;
                if(receiverBankDetails.contains(","))
                {
                    receiverBankDetails1 = receiverBankDetails.split(",");
                    for (int i=1;i<receiverBankDetails1.length;i++)
                    {
                        count++;
                    }
                }
    %>
    <form action="/icici/servlet/UpdateMerchantWire?ctoken=<%=ctoken%>" method="post" name="forms" >
        <input type="hidden" name="id" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("settledid"))%>" <%=conf%>>
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <input type="hidden" value="<%=ctoken%>" name="payoutid" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("payoutid"))%>">

        <table border="1" bordercolor="#ffffff" align="center" style="width:80%" class="table table-striped table-bordered table-green dataTable">
            <tr <%=style%>>
                <td class="th0" colspan="2">Wire Manager:</td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">SettleId :</td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="settledid" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("settledid"))%>" disabled>  </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Memberid :</td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="toid" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("toid"))%>" disabled>  </td>
                <input type="hidden" class="txtbox1" size="30" name="toid" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("toid"))%>">
            </tr>
            <tr <%=style%>>
                <td class="tr1">Accountid : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="accountid" value="<%=ESAPI.encoder().encodeForHTML((String)innerhash.get("accountid"))%>" disabled> </td>
                <input type="hidden" class="txtbox1" size="30" name="accountid" value="<%=ESAPI.encoder().encodeForHTML((String)innerhash.get("accountid"))%>">

            </tr>
            <tr <%=style%>>
                <td class="tr1">Terminalid: </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="terminalid" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("terminalid"))%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Pay Mode : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="paymodeid" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("paymodeid"))%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Card Type : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="cardtypeid" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("cardtypeid"))%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Processing Currency : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="currency" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("currency"))%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Processing Amount : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="20" name="amount" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("amount"))%>"  disabled <%=conf%>> <%=ESAPI.encoder().encodeForHTML((String) innerhash.get("currency"))%> </td>
               <input type="hidden" class="txtbox1" size="20" name="amount" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("amount"))%>">
            </tr>
            <tr <%=style%>>
                <td class="tr1">Gross Amount : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="balanceamount" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("balanceamount"))%>" disabled <%=conf%>> <%=ESAPI.encoder().encodeForHTML((String) innerhash.get("currency"))%> </td>
                <input type="hidden" class="txtbox1" size="30" name="balanceamount" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("balanceamount"))%>">
            </tr>

            <tr <%=style%>>
                <td class="tr1">Net Final Amount : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="famount" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("netfinalamount"))%>" disabled <%=conf%>> <%=ESAPI.encoder().encodeForHTML((String) innerhash.get("currency"))%> </td>
                <input type="hidden" class="txtbox1" size="30" name="famount" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("netfinalamount"))%>">
            </tr>

            <tr <%=style%>>
                <td class="tr1">Unpaid Amount : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="unpaidamount" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("unpaidamount"))%>"  <%=conf%>> <%=ESAPI.encoder().encodeForHTML((String) innerhash.get("currency"))%> </td>
            </tr>

            <tr <%=style%>>
                <td class="tr1">Status : </td>
                <td class="tr1">
                    <% if(action.equalsIgnoreCase("view")){ %>
                    <input type="text" size="30"  class="txtbox" name="status" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("status"))%>" <%=conf%>> </td>
                <% }else{  %>
                <select name="status">
                    <% if( innerhash.get("status").equals("paid")){ %>
                    <option value="paid" selected> PAID</option>
                    <option value="unpaid"> UNPAID</option>
                    <option value="carryforward"> CARRYFORWARD</option>
                    <option value="partialPaid"> PARTIAL PAID</option>
                    <%}else if(innerhash.get("status").equals("carryforward")){%>
                    <option value="paid"> PAID</option>
                    <option value="unpaid"> UNPAID</option>
                    <option value="carryforward" selected> CARRYFORWARD</option>
                    <option value="partialPaid"> PARTIAL PAID</option>
                    <%}else if(innerhash.get("status").equals("partialPaid")){%>
                    <option value="paid"> PAID</option>
                    <option value="unpaid"> UNPAID</option>
                    <option value="carryforward"> CARRYFORWARD</option>
                    <option value="partialPaid" selected> PARTIAL PAID</option>
                    <% }
                    else{
                    %>
                    <option value="paid"> PAID</option>
                    <option value="unpaid" selected> UNPAID</option>
                    <option value="carryforward"> CARRYFORWARD</option>
                    <option value="partialPaid"> PARTIAL PAID</option>
                    <% }%>
                </select>
                <%}%>

            </tr>
            <tr <%=style%>>
                <td class="tr1">Settle Date : </td>
                <td class="tr1">
                    <%
                        if(action.equalsIgnoreCase("view"))
                        {
                    %>
                    <input type="text" class="txtbox1" size="30" name="settledate" readonly class="datepicker"  value="<%=functions.isValueNull(ESAPI.encoder().encodeForHTML((String)innerhash.get("settledate")))?ESAPI.encoder().encodeForHTML((String)innerhash.get("settledate")):""%>"<%=conf%>>
                    <%
                    }
                    else
                    {
                    %>  <input type="text"  readonly class="datepicker" name="settledate">
                    <%
                        }
                    %>
                </td>
            </tr>

            <%-- <tr <%=style%>>
                 <td class="tr1">Conversion Rate : </td>
                 <td class="tr1">
                 <%if(action.equalsIgnoreCase("view")) {%>
                 <input type="text" class="txtbox1" size="30" name="conversionRate" value="<%=conversionRate%>" disabled>
                 <%}else{%>
                 <input type="text" class="txtbox1" size="30" name="conversionRate" value="<%=conversionRate%>">
                 <%}%></td>
             </tr>--%>

            <%-- <tr <%=style%>>
                 <td class="tr1">Payout Currency : </td>
                 <td class="tr1"><input type="text" class="txtbox1" size="30" name="payoutCurrency" value="<%=ESAPI.encoder().encodeForHTML(payoutCurrency)%>" <%=conf%>> </td>
             </tr>
             <tr <%=style%>>
                 <td class="tr1">Payout Amount : </td>
                 <td class="tr1"><input type="text" class="txtbox1" size="30" name="payoutAmount" value="<%=ESAPI.encoder().encodeForHTML(payoutAmount)%>" <%=conf%>> </td>
             </tr>
             <tr <%=style%>>
                 <td class="tr1">Remitter Bank Details: </td>
                 <td class="tr1"><input type="text" class="txtbox1" size="30" name="payerBankDetails" value="<%=ESAPI.encoder().encodeForHTML(payerBankDetails)%>" <%=conf%>> </td>
             </tr>
             <tr <%=style%>>
                 <td class="tr1">Beneficiary Bank Details : </td>
                 <input type="hidden" name="receiverBankDetailsCount" id="receiverBankDetailsCount" value="<%=count%>">
                 <td class="tr1"><input type="text" class="txtbox1" size="30" name="receiverBankDetails" id="receiverBankDetails" style="margin-right: 5px;" value="<%=ESAPI.encoder().encodeForHTML(receiverBankDetails)%>" <%=conf%>>
                 <span id="beneficiaryText"></span>
                     <%if(!action.equalsIgnoreCase("view")) {%><span style="z-index: 2;margin-left: 5px;margin-top:5px;margin-right:5px;font-size:15px;" class="glyphicon glyphicon-plus-sign" onmousedown="addTextBox()"  id="add_btn" ></span><%}%></td>
             </tr>--%>
            <%-- <tr <%=style%>>
                 <td class="tr1">Payment Receipt Date : </td>
                 <td class="tr1">
                     <%
                         if(action.equalsIgnoreCase("view"))
                         {
                     %><input type="text" readonly class="datepicker" size="30" name="paymentReceiptDate" value="<%=ESAPI.encoder().encodeForHTML(paymentReceiptDate)%>" <%=conf%>>
                     <%
                     }
                     else
                     {
                     %>  <input type="text"  readonly class="datepicker" name="paymentReceiptDate">
                     <%
                         }
                     %>
                 </td>
             </tr>

             <tr <%=style%>>
                 <td class="tr1">Payment Receipt Confirmation : </td>
                 <td class="tr1">
                     <% if(action.equalsIgnoreCase("view")){ %>
                     <input type="text" size="30"  class="txtbox" name="paymentConfirmation" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("paymentConfirmation"))%>" <%=conf%>> </td>
                 <% }else{  %>
                 <select name="paymentConfirmation">
                     <% if( innerhash.get("paymentConfirmation").equals("Yes")){ %>
                     <option value="Yes" selected> Yes</option>
                     <option value="No"> No</option>
                     <% }else{
                     %>
                     <option value="Yes" > Yes</option>
                     <option value="No" selected> No</option>
                     <% }%>
                 </select>
                 <%}%>
             </tr>--%>

            <%--<tr <%=style%>>
                <td class="tr1">Remark : </td>
                <td class="tr1">
                    <%
                        if(action.equalsIgnoreCase("view"))
                        {
                    %>
                    <input type="text" class="txtbox1" size="30" name="remark" value="<%=functions.isValueNull(ESAPI.encoder().encodeForHTML((String)innerhash.get("remark")))?ESAPI.encoder().encodeForHTML((String)innerhash.get("remark")):""%>" disabled>
                    <%
                    }
                    else
                    {
                    %>
                    <input type="text" class="txtbox1" size="30" name="remark" value="">
                    <%
                        }
                    %>

                </td>
            </tr>--%>
            <%--<tr <%=style%>>
                <td class="tr1">Payout Date : </td>
                <td class="tr1">
                    <%
                        if(action.equalsIgnoreCase("view"))
                        {
                    %>
                    <input type="text" class="txtbox1" size="30" name="settledate" readonly class="datepicker"  value="<%=functions.isValueNull(ESAPI.encoder().encodeForHTML((String)innerhash.get("settledate")))?ESAPI.encoder().encodeForHTML((String)innerhash.get("settledate")):""%>"<%=conf%>>
                    <%
                    }
                    else
                    {
                    %>  <input type="text"  readonly class="datepicker" name="settledate">
                    <%
                        }
                    %>
                </td>
            </tr>--%>
            <tr <%=style%>>
                <td class="tr1">Wire Creation Date : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="wirecreationdate" value="<%=ESAPI.encoder().encodeForHTML((String)innerhash.get("wirecreationdate"))%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">First Date : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="firstdate" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("firstdate"))%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Last Date : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="lastdate" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("lastdate"))%>" disabled> </td>
            </tr>
            <tr <%=style%>>

                <td class="tr1">Reserve Release Date : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="rrdate" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("rollingreservereleasedateupto"))%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="td0">Decline Covered Upto : </td>
                <td class="td0"><input type="text" size="30" name="lastdate" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("declinedcoverdateupto"))%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="td0">Reversal Covered Upto : </td>
                <td class="td0"><input type="text" size="30" name="lastdate" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("reversedcoverdateupto"))%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="td0">Chargeback Covered Upto : </td>
                <td class="td0"><input type="text" size="30" name="lastdate" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("chargebackcoverdateupto"))%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="td0">Reserve Release Date : </td>
                <td class="td0"><input type="text" size="30" name="rrdate" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("rollingreservereleasedateupto"))%>" disabled> </td>

            </tr>
            <tr <%=style%>>
                <td class="tr1">Settlement Cycle No : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" name="cycleno" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("settlementcycle_no"))%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Settlement Report File Path : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" style="width:90%" name="settlementreportfilepath" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("settlementreportfilepath"))%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1">Settled Transaction File Path : </td>
                <td class="tr1"><input type="text" class="txtbox1" size="30" style="width:90%" name="settledtransactionfilepath" value="<%=ESAPI.encoder().encodeForHTML((String) innerhash.get("settledtransactionfilepath"))%>" disabled> </td>
            </tr>
            <tr <%=style%>>
                <td class="tr1" colspan="" align="center"><input type="submit" value="Update" name="Update" class="buttonform" <%=conf%>></td>
                <td class="tr1" colspan="1" align="center"><input type="submit" value="Update For ALL" name="UpdateForAll" class="buttonform" <%=conf%>></td>
            </tr>
        </table>
    </form>
    <%
        if(!functions.isValueNull((String) request.getAttribute("payoutid"))){
    %>
    <div>
        <form action="/icici/addPayout.jsp?ctoken=<%=ctoken%>" method="post" style="text-align: center">
            <input type="hidden" name="settledid" value="<%=settledid%>"/>
            <input type="hidden" name="cycleid" value="<%=reportid%>"/>
            <input type="hidden" name="action" value="update"/>
            <input type="submit" class="buttonform" value="Add Manual Payout Details" style="display: inline-block;width: 171px;">
        </form>
    </div>

    <!-- Start --->
    <%
        }
        Hashtable payoutDetails = (Hashtable) request.getAttribute("payoutDetails");
        //System.out.println("payoutDetails----------------->"+payoutDetails);

        if (payoutDetails != null)
        {
            //System.out.println("payoutDetails----------------->"+payoutDetails);
    %>
    <div style="width: 100%;overflow: auto">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <table border="1" cellpadding="1" cellspacing="0" bordercolor="#ffffff" align="center" class="table table-striped table-bordered table-hover table-green dataTable">
            <thead>
            <tr <%--<%=style%>--%>>
                <td class="th0" colspan = "16" align="center"> Manual Payout Details </td>
            </tr>

            <tr>
                <td class="th0">Report Id</td>
                <td class="th0">Payout Id </td>
                <td class="th0">Payout Date</td>
                <td class="th0">Payout Currency</td>
                <td class="th0">Conversion Rate</td>
                <td class="th0">Payout Amount</td>
                <td class="th0">Payment Receipt Date</td>
                <td class="th0">Payment Receipt Confirmation</td>
                <td class="th0">Beneficiary Bank Details</td>
                <td class="th0">Remitter Bank Details</td>
                <td class="th0">Remarks</td>
                <td class="th0">Swift Message</td>
                <td class="th0" colspan="2">Swift Upload</td>
                <%--<td class="th0">Swift Upload</td>--%>
                <td class="th0" colspan="2">Action</td>
            </tr>
            </thead>

            <%
                for (int i=1;i<=payoutDetails.size();i++)
                {
                    Hashtable innerpayoutDetails = (Hashtable) payoutDetails.get(String.valueOf(i));
                    //System.out.println("payment_receipt_date::::::: inside jsp "+ innerpayoutDetails.get("payment_receipt_date"));
                    out.println("<tr "+style+">");
                    out.println("<td align=\"center\">"+ reportid +"</td>");
                    out.println("<td align=\"center\">"+ innerpayoutDetails.get("payout_id") +"</td>");
                    out.println("<td align=\"center\">"+innerpayoutDetails.get("payout_date") + "</td>");
                    out.println("<td align=\"center\">"+ innerpayoutDetails.get("payout_currency") +"</td>");
                    out.println("<td align=\"center\">"+ innerpayoutDetails.get("conversion_rate") +"</td>");
                    out.println("<td align=\"center\">"+ innerpayoutDetails.get("payout_amount")+"</td>");
                    //System.out.println("payment_receipt_date::::::: inside jsp actionwiremanager "+ innerpayoutDetails.get("payment_receipt_date"));
                    String receiptDate ="";
                    if(functions.isValueNull((String) innerpayoutDetails.get("payment_receipt_date")))
                    {
                        if(!("0000-00-00 00:00:00".equals(innerpayoutDetails.get("payment_receipt_date"))))
                        {
                            receiptDate = (String) innerpayoutDetails.get("payment_receipt_date");
                        }
                    }

                    out.println("<td align=\"center\">"+ receiptDate +"</td>");
                    out.println("<td align=\"center\">"+ innerpayoutDetails.get("payment_receipt_confirmation") +"</td>");
                    out.println("<td align=\"justify\">"+ innerpayoutDetails.get("beneficiary_bank_details") +"</td>");
                    out.println("<td align=\"justify\">"+ innerpayoutDetails.get("remitter_bank_details") + "</td>");
                    out.println("<td align=\"center\">"+ innerpayoutDetails.get("remarks") +"</td>");
                    out.println("<td align=\"center\">"+ innerpayoutDetails.get("swift_message") +"</td>");

//                    System.out.println("swift_upload::::::::::::::;"+innerpayoutDetails.get("swift_upload"));
                    String wordFile="",pdfFile="";
                    if (functions.isValueNull((String)innerpayoutDetails.get("swift_upload")))
                    {
                        String swift_upload = (String)innerpayoutDetails.get("swift_upload");
                        String file = swift_upload.substring(swift_upload.lastIndexOf("."));
                        if (".docs".equals(file)|| ".docx".equals(file) || ".doc".equals(file))
                            wordFile = "word.png";
                        else if (".pdf".equals(file))
                            pdfFile = "pdflogo.jpg";
                    }

                    if(functions.isValueNull(wordFile))
                    {
                        //System.out.println("in word file = "+wordFile);
                        out.println("<td align=\"center\""+style+">" +
                                "<form id=\"wordform"+innerpayoutDetails.get("payout_id")+"\" action=\"/icici/servlet/ActionWireManager?ctoken="+ctoken+"\" method=\"POST\">" +
                                "<input type=\"hidden\" name=\"payoutId1\" value="+ESAPI.encoder().encodeForHTML((String) innerpayoutDetails.get("payout_id"))+">" +
                                "<input type=\"hidden\" name=\"settledid\" value="+ESAPI.encoder().encodeForHTML((String) innerpayoutDetails.get("settledid"))+">" +
                                "<input type=\"hidden\" name=\"action\" value='sendPdfFilePayout'>" +
                                "</form>" +
                                "<a href=\"javascript: getWordFile('"+innerpayoutDetails.get("payout_id")+"')\">" +
                                "<img width=\"20\" height=\"28\" border=\"0\" src=\"/icici/images/"+wordFile +"\">" +
                                "</a>" +
                                "</td>");
                    }
                    else
                    {
                        out.println("<td align=\"center\""+style+">-</td>");
                    }

                    if(functions.isValueNull(pdfFile))
                    {

                        out.println("<td align=\"center\""+style+">" +
                                "<form id=\"pdfform"+innerpayoutDetails.get("payout_id")+"\" action=\"/icici/servlet/ActionWireManager?ctoken="+ctoken+"\" method=\"POST\">" +
                                "<input type=\"hidden\" name=\"payoutId1\" value="+ESAPI.encoder().encodeForHTML((String) innerpayoutDetails.get("payout_id"))+">" +
                                "<input type=\"hidden\" name=\"settledid\" value="+ESAPI.encoder().encodeForHTML((String) innerpayoutDetails.get("settledid"))+">" +
                                "<input type=\"hidden\" name=\"action\" value='sendPdfFilePayout'>" +
                                "</form>" +
                                "<a href=\"javascript: getPdfFile('"+innerpayoutDetails.get("payout_id")+"')\">" +
                                "<img width=\"20\" height=\"28\" border=\"0\" src=\"/icici/images/"+pdfFile+" \">" +
                                "</a>" +
                                "</td>");
                    }
                    else {
                        out.println("<td align=\"center\" "+style+">-</td>");
                    }

                    out.println("<td align=\"center\""+style+"><form action=\"/icici/servlet/ActionWireManager?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"payout_id\" value=\""+ESAPI.encoder().encodeForHTML((String) innerpayoutDetails.get("payout_id"))+"\"><input type=\"hidden\" name=\"reportid\" value=\""+reportid+"\"><input type=\"hidden\" name=\"settledid\" value=\""+ESAPI.encoder().encodeForHTML((String) innerpayoutDetails.get("settledid"))+"\"><input type=\"submit\" name=\"submit\" value=\"View\" class=\"gotoauto\" ><input type=\"hidden\" name=\"action\" value=\"PayoutView\"></form></td>");
                    out.println("<td align=\"center\""+style+"><form action=\"/icici/servlet/ActionWireManager?ctoken="+ctoken+"\" method=\"POST\"><input type=\"hidden\" name=\"payout_id\" value=\""+ESAPI.encoder().encodeForHTML((String) innerpayoutDetails.get("payout_id"))+"\"><input type=\"hidden\" name=\"reportid\" value=\""+reportid+"\"><input type=\"hidden\" name=\"settledid\" value=\""+ESAPI.encoder().encodeForHTML((String) innerpayoutDetails.get("settledid"))+"\"><input type=\"submit\" name=\"submit\" value=\"Edit\" class=\"gotoauto\"><input type=\"hidden\" name=\"action\" value=\"PayoutUpdate\"></form></td>");
                    out.println("</tr>");
                }
            %>
        </table>
    </div>

    <%
                    }

                    else
                    {
                        out.println(Functions.NewShowConfirmation("","No Records Founds"));
                    }
                }
            }
            else
            {
                response.sendRedirect("/icici/logout.jsp");
                return;
            }
        }
    %>
</div>
</body>
</html>
