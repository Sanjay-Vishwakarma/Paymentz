<%@ page import="com.directi.pg.Functions,com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
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
<%@ page import="com.directi.pg.core.GatewayTypeService"%>
<%@ page import="com.manager.PayoutManager" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.ChargeVO" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ page import="servlets.ChargesUtils" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.TreeMap" %>
<%@ page import="com.manager.ChargeManager" %>
<%!
    private static Logger logger=new Logger("listMemberAccountsCharges.jsp");
%>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>

    <%--<script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-account-member.js"></script>--%>
    <title> Merchant Charges</title>
</head>

<html lang="en">
<head>
    <script type="text/javascript" src='/partner/css/new/html5shiv.min.js'></script>
    <script type="text/javascript" src='/partner/css/new/respond.min.js'></script>
    <script type="text/javascript" src='/partner/css/new/html5.js'></script>
    <title> Merchant Delete</title>
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
        <script language="javascript">
        function Delete()
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
            if (confirm("Do you really want to Delete all selected Data."))
            {
                document.delete.submit();
            }
        }
    </script></head>

<body>
<%
    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
    String str="ctoken=" + ctoken;
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        TerminalManager terminalManager = new TerminalManager();
        TreeMap<String, TerminalVO> memberMap = new TreeMap();
        PayoutManager payoutManager = new PayoutManager();
        List<TerminalVO> terminalList = terminalManager.getAllMappedTerminals();

        TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();
        TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();
        String accountid2 = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");

        for(TerminalVO terminalVO : terminalList)
        {
            String memberKey = terminalVO.getMemberId()+"-"+terminalVO.getAccountId()+"-"+terminalVO.getGateway().toUpperCase() + "-" + terminalVO.getCurrency() + "-" + terminalVO.getGateway_id();
            memberMap.put(memberKey,terminalVO);
        }

        List<ChargeVO> chargeNameList = PayoutManager.loadchargename();
        List<TerminalVO> sTerminal = payoutManager.getTerminalForMerchantWireList();

        String pgTypeId = Functions.checkStringNull(request.getParameter("pgtypeid")) == null ? "" : request.getParameter("pgtypeid") == "0" ? "": request.getParameter("pgtypeid");
        String memberId = Functions.checkStringNull(request.getParameter("memberid")) == null ? "" : request.getParameter("memberid");
        String accountId = Functions.checkStringNull(request.getParameter("accountid")) == null ? "" : request.getParameter("accountid") == "0" ? "": request.getParameter("accountid");
        String chargeName = request.getParameter("chargename") == null ? "" : request.getParameter("chargename");
        String chargeType = request.getParameter("chargetype") == null ? "" : request.getParameter("chargetype");
        String terminalId = request.getParameter("terminalid") == null ? "" : request.getParameter("terminalid");

        if (pgTypeId != null) str = str + "&pgtypeid=" + pgTypeId;
        if (memberId != null) str = str + "&memberid=" + memberId;
        if (accountId != null) str = str + "&accountid=" + accountId;
        if (chargeName != null) str = str + "&chargename=" + chargeName;
        if (chargeType != null) str = str + "&chargetype=" + chargeType;
        if (terminalId != null) str = str + "&terminalid=" + terminalId;
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Member Account Charges
                <div style="float: right;">
                    <form action="/icici/manageMemberAccountsCharges.jsp?ctoken=<%=ctoken%>" method="POST">
                        <button type="submit" class="addnewmember" value="Add New Member Account Charge Mapping" name="submit" style="width:350px ">
                            <i class="fa fa-sign-in"></i>
                            &nbsp;&nbsp;Add New Member Account Charge Mapping
                        </button>
                    </form>
                </div>
            </div><br>
            <form action="/icici/servlet/ListMemberAccountsCharges?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <table border="0" align="center" width="98%" cellpadding="2" cellspacing="2">
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr>
                                    <td colspan="6">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb" align="center"><b>Bank Name:</b></td>
                                    <td class="textb" align="center">
                                        <input name="pgtypeid" id="gateway1" value="<%=pgTypeId%>" class="txtbox" autocomplete="on">
                                        <%-- <select size="1" id="bank" class="txtbox" name="pgtypeid">
                                                <option value="0" default></option>
                                                <%
                                                    for(String gatewayType : gatewayTypeTreeMap.keySet())
                                                    {
                                                        String isSelected = "";
                                                        if (gatewayType.equalsIgnoreCase(pgTypeId))
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
                                    <td class="textb" align="center"><b>Account Id:</b></td>
                                    <td class="textb" align="center">
                                        <input name="accountid" id="accountid1" value="<%=accountId%>" class="txtbox" autocomplete="on">
                                        <%--<select size="1" id="accountid" name="accountid" class="txtbox">
                                                <option data-bank="all" value="0"></option>
                                                <%
                                                    for(Integer sAccid : accountDetails.keySet())
                                                    {
                                                        GatewayAccount g = accountDetails.get(sAccid);
                                                        String isSelected = "";
                                                        String gateway2 = g.getGateway().toUpperCase();
                                                        String currency2 = g.getCurrency();
                                                        String pgtype = g.getPgTypeId();
                                                        if (String.valueOf(sAccid).equals(accountid2))
                                                            isSelected = "selected";
                                                %>
                                                <option data-bank="<%=gateway2+"-"+currency2+"-"+pgtype%>"  value="<%=sAccid%>" <%=isSelected%>><%=sAccid+"-"+g.getMerchantId()+"-"+g.getCurrency()%></option>
                                                <%
                                                    }
                                                %>
                                            </select>--%>
                                    </td>
                                    <td class="textb" align="center"><b>Member Id:</b></td>
                                    <td class="textb" align="center">
                                        <input name="memberid" id="memberid1" value="<%=memberId%>" class="txtbox" autocomplete="on">
                                        <%--<select name="memberid" id="memberid" class="txtbox">
                                                <option data-bank="all" data-accid="all" value="0" selected></option>
                                                <%
                                                    for(String sMemberId: memberMap.keySet()){
                                                        TerminalVO t = memberMap.get(sMemberId);
                                                        String accId = t.getAccountId();
                                                        //String aContactPerson = t.getContactPerson();
                                                        //String aCompanyName = t.getCompany_name();
                                                        String gateway2 = t.getGateway().toUpperCase();
                                                        String currency2 = t.getCurrency();
                                                        String pgtype = t.getGateway_id();
                                                        String value = gateway2+"-"+currency2+"-"+pgtype;
                                                        String isSelected = "";
                                                        if (t.getMemberId().equals(memberId)){
                                                            isSelected = "selected";
                                                        }
                                                %>
                                                <option data-bank="<%=value%>"  data-accid="<%=accId%>" value="<%=t.getMemberId()%>" <%=isSelected%>><%=t.getMemberId()+"-"+accId%></option>
                                                <%
                                                    }
                                                %>
                                            </select>--%>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="6">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td class="textb" align="center"><b>Terminal Id:</b></td>
                                    <td class="textb" align="center">
                                        <input name="terminalid" id="tid3" value="<%=terminalId%>" class="txtbox" autocomplete="on">
                                        <%--<select size="1" class="txtbox" name="terminalid">
                                            <option value="" selected></option>
                                            <%
                                                for(TerminalVO terminalVO:sTerminal)
                                                {
                                                    String isSelected="";
                                                    if (terminalVO.getTerminalId().equalsIgnoreCase(terminalId))
                                                        isSelected="selected";
                                            %>
                                            <option value="<%=terminalVO.getTerminalId()%>" <%=isSelected%>><%=terminalVO.getTerminalId()%></option>
                                            <%
                                                }
                                            %>
                                        </select>--%>
                                    </td>
                                    <td class="textb" align="center"><b>Charge Name:</b></td>
                                    <td class="textb" align="center">
                                        <select size="1" class="txtbox" name="chargename">
                                            <option value="" selected>Select Charge Name</option>
                                            <%
                                                for (ChargeVO chargeVO : chargeNameList)
                                                {
                                                    String isSelected = "";
                                                    if (chargeVO.getChargeid().equalsIgnoreCase(chargeName))
                                                        isSelected = "selected";
                                                    else
                                                        isSelected = "";
                                            %>
                                            <option value="<%=chargeVO.getChargeid()%>" <%=isSelected%>><%=chargeVO.getChargename()%>
                                            </option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                    <td class="textb" align="center"><b>Charge Unit:</b></td>
                                    <td class="textb" align="center">
                                        <select name="chargetype" class="txtbox">
                                            <option value="" selected>Select Charge Unit</option>
                                                    <%
                                                for (ChargesUtils.unit unit : ChargesUtils.unit.values())
                                                {
                                                    String isSelected = "";
                                                    if (chargeType.equalsIgnoreCase(unit.name()))
                                                        isSelected = "selected";
                                                    %>
                                            <option value="<%=unit.name()%>" <%=isSelected%>><%=unit.name()%> </option>;
                                            <%
                                                }
                                            %></select>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="6">&nbsp;</td>
                                </tr>
                                <tr>
                                    <td colspan="5">&nbsp;</td>
                                    <td class="textb">
                                        <button type="submit" class="buttonform" value="Submit">
                                            <i class="fa fa-clock-o"></i>
                                            &nbsp;&nbsp;Search
                                        </button>
                                    </td>
                                </tr>
                                <tr>
                                    <td colspan="6">&nbsp;</td>
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
        String success1 = (String) request.getAttribute("success1");
        if(success1==null)
        {
            success1 ="0";

        Hashtable hash = (Hashtable) request.getAttribute("transdetails");
        Hashtable temphash = null;
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;

        String error = (String) request.getAttribute("errormessage");
        if (error != null)
        {
            out.println("<center class='textb'>" + error + "</center>");

        }
        if (hash != null && hash.size() > 0)
        {
            int records = 0;
            int totalrecords = 0;
            String currentblock = request.getParameter("currentblock");

            if (currentblock == null)
                currentblock = "1";

            try
            {
                records=Functions.convertStringtoInt((String)hash.get("records"),15);
                totalrecords=Functions.convertStringtoInt((String)hash.get("totalrecords"),0);
            }
            catch (NumberFormatException ex)
            {
                logger.error("Records & TotalRecords is found null",ex);
            }
            if (hash != null)
            {
                hash = (Hashtable) request.getAttribute("transdetails");
            }
            if (records > 0)
            {
    %>
    <form name="exportform" method="post" action="ExportChargeDetails?ctoken=<%=ctoken%>" >
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(pgTypeId)%>" name="pgtypeid">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberId)%>" name="memberid">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(accountId)%>" name="accountid">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(chargeName)%>" name="chargename">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(chargeType)%>" name="chargetype">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(terminalId)%>" name="terminalid">
        <input type="hidden" value="<%=ESAPI.encoder().encodeForHTMLAttribute(pgTypeId)%>" name="pgtypeid">
        <button type="submit" class="button3" style="width:15%;margin-left:85% ;margin-top:0px"><b>Export to excel</b>&nbsp;&nbsp;&nbsp;<img width="20%" height="100%" border="0" src="/merchant/images/excel.png"></button>
    </form>
    <div class="scroll">
            <table align=center width="90%" class="table table-striped table-bordered table-hover table-green dataTable">
                <thead>
                <tr>
                    <td valign="middle" align="center" class="th0"><b><input type="checkbox" onClick="ToggleAll(this);" name="alltrans"></b></td>
                    <td valign="middle" align="center" class="th0">Member Id</td>
                    <td valign="middle" align="center" class="th0">Account Id</td>
                    <td valign="middle" align="center" class="th0">Terminal Id</td>
                    <td valign="middle" align="center" class="th0">Pay Mode</td>
                    <td valign="middle" align="center" class="th0">Card Type</td>
                    <td valign="middle" align="center" class="th0">Charge Name</td>
                    <td valign="middle" align="center" class="th0">Member Value</td>
                    <td valign="middle" align="center" class="th0">Agent Value</td>
                    <td valign="middle" align="center" class="th0">Partner Value</td>
                    <td valign="middle" align="center" class="th0">Charge Unit</td>
                    <td valign="middle" align="center" class="th0">Category</td>
                    <td valign="middle" align="center" class="th0">Seq No</td>
                    <td valign="middle" align="center" class="th0">Keyword</td>
                    <td valign="middle" align="center" class="th0">Sub Keyword</td>
                    <td valign="middle" align="center" class="th0">Frequency</td>
                    <td valign="middle" align="center" class="th0" colspan="2">Action</td>
                    <td valign="middle" align="center" class="th0">Action Executor Id</td>
                    <td valign="middle" align="center" class="th0">Action Executor Name</td>
                </tr>
                </thead>
                <%

                    ChargeVO chargeVO = new ChargeVO();
                    Functions functions = new Functions();
                    String style = "class=td1";
                    String ext = "light";
                    for (int pos = 1; pos <= records; pos++)
                    {
                        String id = Integer.toString(pos);
                        temphash = (Hashtable) hash.get(id);
                        int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);
                        if (pos % 2 == 0)
                        {
                            style="class=tr0";
                            ext="dark";
                        }
                        else
                        {
                            style="class=tr1";
                            ext="light";
                        }
                        if(functions.isValueNull((String)temphash.get("actionExecutorId")))
                        {
                            actionExecutorId=(String)temphash.get("actionExecutorId");
                        }
                        else
                        {
                            actionExecutorId="-";
                        }
                        if(functions.isValueNull((String)temphash.get("actionExecutorName")))
                        {
                            actionExecutorName=(String)temphash.get("actionExecutorName");
                        }
                        else
                        {
                            actionExecutorName="-";
                        }


                        String memberid = (String) temphash.get("memberid");
                        String accountid = (String) temphash.get("accountid");
                        String terminalid = (String) temphash.get("terminalid");
                        String chargename = (String) temphash.get("chargename");
                        String negativebalance=(String)temphash.get("negativebalance");
                        String message ="";
                        ChargeManager chargeManager = new ChargeManager();
                        boolean Negativebalanceshow =chargeManager.Negativebalanceshow((String)temphash.get("chargeid"));
                        if(Negativebalanceshow){
                            if(negativebalance.equals("Y")){
                                message="<br> Negative Balance Settings: [Positive Settlement Charges]";
                            }else{
                                message="<br> Negative Balance Settings: [No Settlement Charges]";
                            }
                        }

                        out.println("<tr>");
                        out.println("<td align=\"center\" " + style + ">&nbsp;<input type=\"checkbox\" name=\"id\" value=\""  +ESAPI.encoder().encodeForHTML((String)temphash.get("mappingid"))+ "\"></td>");
                        out.println("<td align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML(memberid) + "<input type=\"hidden\" name=\"memberid\" value=\"" + memberid + "\"></td>");
                        out.println("<td align=\"center\" " + style + ">" + ESAPI.encoder().encodeForHTML(accountid) + "<input type=\"hidden\" name=\"accountid\" value=\"" + accountid + "\"></td>");
                        out.println("<td align=\"center\" " + style + ">" + ESAPI.encoder().encodeForHTML(terminalid) + "<input type=\"hidden\" name=\"terminalid\" value=\"" + terminalid + "\"></td>");
                        out.println("<td align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("paymodeid")) + "<input type=\"hidden\" name=\"paymodeid\" value=\"" + temphash.get("paymodeid") + "\"></td>");
                        out.println("<td align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("cardtypeid")) + "<input type=\"hidden\" name=\"cardtypeid\" value=\"" + temphash.get("cardtypeid") + "\"></td>");
                        out.println("<td align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("chargename"))+ message + "<input type=\"hidden\" name=\"chargeid\" value=\"" + temphash.get("chargeid") + "\"></td>");
                        out.println("<td align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("chargevalue")) + "<input type=\"hidden\" name=\"chargevalue\" value=\"" + temphash.get("chargevalue") + "\"></td>");
                        out.println("<td align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("agentchargevalue")) + "<input type=\"hidden\" name=\"agentchargevalue\" value=\"" + temphash.get("agentchargevalue") + "\"></td>");
                        out.println("<td align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("partnerchargevalue")) + "<input type=\"hidden\" name=\"partnerchargevalue\" value=\"" + temphash.get("partnerchargevalue") + "\"></td>");
                        out.println("<td align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("valuetype")) + "<input type=\"hidden\" name=\"valuetype\" value=\"" + temphash.get("valuetype") + "\"></td>");
                        out.println("<td align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("category")) + "<input type=\"hidden\" name=\"category\" value=\"" + temphash.get("category") + "\"></td>");
                        out.println("<td align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("sequencenum")) + "<input type=\"hidden\" name=\"sequencenum\" value=\"" + temphash.get("sequencenum") + "\"></td>");
                        out.println("<td align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("keyword")) + "<input type=\"hidden\" name=\"keyword\" value=\"" + temphash.get("keyword") + "\"></td>");
                        out.println("<td align=\"center\" " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("subkeyword")) + "<input type=\"hidden\" name=\"subkeyword\" value=\"" + temphash.get("subkeyword") + "\"></td>");
                        out.println("<td align=\"center\" " + style + ">" + ESAPI.encoder().encodeForHTML((String) temphash.get("frequency")) + "<input type=\"hidden\" name=\"frequency\" value=\"" + temphash.get("frequency") + "\"></td>");
                        out.println("<td align=\"center\" " + style + "><form action=\"/icici/servlet/ActionMemberAccountsCharges?ctoken=" + ctoken + "\" method=\"post\"><input type=\"hidden\" name=\"mappingid\" value=\"" + temphash.get("mappingid") + "\"><input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"submit\" class=\"goto\" value=\"Modify\"></form></td>");
                        out.println("<td align=\"center\" " + style + "><form action=\"/icici/servlet/ActionMemberAccountsCharges?ctoken=" + ctoken + "\" method=\"post\"><input type=\"hidden\" name=\"mappingid\" value=\"" + temphash.get("mappingid") + "\"><input type=\"hidden\" name=\"action\" value=\"history\"><input type=\"submit\" class=\"goto\" value=\"History\"></form></td>");
                        out.println("<td align=\"center\" " + style + ">&nbsp;"+ESAPI.encoder().encodeForHTML(actionExecutorId)+"</td>");
                        out.println("<td align=\"center\" " + style + ">&nbsp;"+ESAPI.encoder().encodeForHTML(actionExecutorName)+"</td>");
                        out.println("</tr>");
                    }

                %>
            </table>
        <form name="delete" action="/icici/servlet/ListMemberAccountsCharges?ctoken=<%=ctoken%>" method="post">
            <table width="100%">
                <thead>
                <tr>
                    <td width="15%" align="center">
                        <input type="hidden" name="delete" value="delete">
                        <input id="ids" type="hidden" name="ids" value="">
                        <input type="button" name="delete" class="btn btn-default center-block" value="Delete" onclick="return Delete();">
                    </td>
                </tr>
                </thead>
            </table>
        </form>
        &nbsp;&nbsp;
            <table align=center valign=top><tr>

                <td align=center>
                    <jsp:include page="page.jsp" flush="true">
                        <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                        <jsp:param name="numrows" value="<%=pagerecords%>"/>
                        <jsp:param name="pageno" value="<%=pageno%>"/>
                        <jsp:param name="str" value="<%=str%>"/>
                        <jsp:param name="page" value="ListMemberAccountsCharges"/>
                        <jsp:param name="currentblock" value="<%=currentblock%>"/>
                        <jsp:param name="orderby" value=""/>
                    </jsp:include>
                </td>
            </tr>
            </table>
                <%
                    }
                    else
                    {
                        out.println(Functions.NewShowConfirmation("Sorry", "No Records Found."));
                    }
                }
                else
                {
                    out.println(Functions.NewShowConfirmation("Filter","No Records Found"));
                }
            }
            else
            {
                out.println(Functions.NewShowConfirmation("Result",success1));
                }
        %>
    </div>
</div>
<%
    }
%>
</body>
</html>