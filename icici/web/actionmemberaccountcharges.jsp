<%@ page import="com.directi.pg.Functions,com.directi.pg.Logger"%>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.manager.PayoutManager" %>
<%@ page import="com.manager.TerminalManager"%>
<%@ page import="com.manager.utils.CommonFunctionUtil" %>
<%@ page import="com.manager.vo.ChargeVO" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="servlets.ChargesUtils" %>
<%@ page import="java.text.ParseException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Nishant
  Date: 5/26/14
  Time: 3:39 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <title>Merchant Charges</title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>
    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script>
        $(function() {
            $( ".datepicker" ).datepicker({dateFormat: 'yy-mm-dd',endDate:'+10y'});
        });
    </script>
</head>
<body>
<%!
    private static Logger logger=new Logger("actionmemberaccountcharges.jsp");
    private static CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
%>
<%
    String str = "ctoken=" + ctoken;
    ctoken = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        TerminalManager terminalManager = new TerminalManager();
        TreeMap<String, TerminalVO> memberMap = new TreeMap();
        PayoutManager payoutManager = new PayoutManager();
        List<TerminalVO> terminalList = terminalManager.getAllMappedTerminals();

        TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();
        TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();
        String accountid2 = Functions.checkStringNull(request.getParameter("accountid")) == null ? "" : request.getParameter("accountid");

        for(TerminalVO terminalVO : terminalList)
        {
            String memberKey = terminalVO.getMemberId()+"-"+terminalVO.getAccountId()+"-"+terminalVO.getGateway().toUpperCase() + "-" + terminalVO.getCurrency() + "-" + terminalVO.getGateway_id();
            memberMap.put(memberKey,terminalVO);
        }

        List<ChargeVO> chargeNameList = PayoutManager.loadchargename();
        List<TerminalVO> sTerminal = payoutManager.getTerminalForMerchantWireList();

        String pgTypeId = Functions.checkStringNull(request.getParameter("pgtypeid")) == null ? "" : request.getParameter("pgtypeid");
        String memberId = Functions.checkStringNull(request.getParameter("memberid")) == null ? "" : request.getParameter("memberid");
        String accountId = Functions.checkStringNull(request.getParameter("accountid")) == null ? "" : request.getParameter("accountid");
        String chargeName = request.getParameter("chargename") == null ? "" : request.getParameter("chargename");
        String chargeType = request.getParameter("chargetype") == null ? "" : request.getParameter("chargetype");
        String terminalId = request.getParameter("terminalid") == null ? "" : request.getParameter("terminalid");

        if (pgTypeId != null) str = str + "&pgtypeid=" + pgTypeId;
        if (memberId != null) str = str + "&memberid=" + memberId;
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

            <form action="/icici/servlet/ListMemberAccountsCharges?ctoken=<%=ctoken%>" method="post" name="forms">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">
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
        String error = (String) request.getAttribute("errormessage");
        logger.error(error);
        if (error != null)
        {
            out.println("<center><font class=\"textb\"><b>");
            out.println(error);
            out.println("</b></font></center>");

        }
        HashMap hash = (HashMap) request.getAttribute("chargedetails");
        String action = (String) request.getAttribute("action");
        if (hash != null)
        {
            if (action.equalsIgnoreCase("history"))
            {
    %>

    <table align=center class="table table-striped table-bordered table-green dataTable">
        <tr>
            <td valign="middle" align="center" class="th0" >Sr. No</td>
            <td valign="middle" align="center" class="th0" >Mapping Id</td>
            <td valign="middle" align="center" class="th0" >Merchant Id</td>
            <td valign="middle" align="center" class="th0" >Account Id</td>
            <td valign="middle" align="center" class="th0" >Terminal Id</td>
            <td valign="middle" align="center" class="th0" >Partner Charge</td>
            <td valign="middle" align="center" class="th0" >Agent Charge</td>
            <td valign="middle" align="center" class="th0" >Member Charge</td>
            <td valign="middle" align="center" class="th0" >Start Date</td>
            <td valign="middle" align="center" class="th0" >End Date</td>
        </tr>
        <%
            Object mappingId;
            TreeMap mappingMap = new TreeMap(hash);
            Iterator itr2 = mappingMap.keySet().iterator();
            int i = 1;
            while (itr2.hasNext())
            {
                String style = "class=td1";
                mappingId = itr2.next();
                HashMap innerhash = (HashMap) mappingMap.get(mappingId);
                out.println("<tr>");
                out.println("<td " + style + " >&nbsp;" + i + "</td>");
                out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerhash.get("mappingid")) + "</td>");
                out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerhash.get("memberid")) + "</td>");
                out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerhash.get("accountid")) + "</td>");
                out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerhash.get("terminalid")) + "</td>");
                out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerhash.get("partnerCommision")) + "</td>");
                out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerhash.get("agentCommision")) + "</td>");
                out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerhash.get("merchantChargeValue")) + "</td>");
                out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerhash.get("effectiveStartDate")) + "</td>");
                out.println("<td " + style + " >&nbsp;" + ESAPI.encoder().encodeForHTML((String) innerhash.get("effectiveEndDate")) + "</td>");
                out.println("</tr>");
                i = i + 1;
            }

        %>
    </table>

    <%
    }
    else
    {
        String style = "class=tr0";
        String accountid = (String) hash.get("accountid");
        String gatewayName = ChargesUtils.getGatewayName(accountid);
    %> <form action="/icici/servlet/UpdateMemberAccountsCharges?ctoken=<%=ctoken%>" method="post" name="forms" >
    <input type="hidden" name="mappingid" value="<%=(String)hash.get("mappingid")%>">
    <table align=center class="table table-striped table-bordered table-green dataTable" style="width:50% ">
        <tr <%=style%>>
            <td class="th0" colspan="2"><b>Update Charges</b></td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Member Id : </td>
            <td class="tr1"><input type="text" class="txtbox1"  size="30" name="username" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("memberid"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Account Id : </td>
            <td class="tr1"><input type="text" class="txtbox1" size="30" name="username" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("accountid"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Terminal Id : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="username" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("terminalid"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Gateway Name : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="username" value="<%=ESAPI.encoder().encodeForHTML(gatewayName)%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Pay Mode : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="username" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("paymodeid"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Card Type : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="username" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("cardtypeid"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Charge Id : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="username" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("chargeid"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Charge Name : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="username" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("chargename"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Dynamic Input : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="username" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("isinput_required"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Member Charge Value : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="memberchargeval" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("chargevalue"))%>"> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Agent Charge Value : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="agentchargeval" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("agentchargevalue"))%>"> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Partner Charge Value : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="partnerchargeval" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("partnerchargevalue"))%>"> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Start Date : </td>
            <td class="tr1">
                <%
                    if("false".equals((String)hash.get("version"))){%>
                <input type="text"  size="30" readonly class="datepicker" name="startdate" value="<%=commonFunctionUtil.convertTimestampToDatepicker((String) hash.get("effectiveStartDate"))%>">
                <%}
                else{
                %>
                <input type="text"  size="30" readonly class="datepicker" name="startdate" value="<%=commonFunctionUtil.convertTimestampToDatepicker(getPreviousDate((String) hash.get("lastupdateDate")))%>">
                <%}
                %>
            </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">End Date : </td>
            <td class="tr1">
                <%
                    if("false".equals((String)hash.get("version"))){%>
                <input type="text"  size="30" readonly class="datepicker" name="enddate" value="<%=commonFunctionUtil.convertTimestampToDatepicker((String) hash.get("lastupdateDate"))%>">
                <%}
                else{
                %>
                <input type="text"  size="30" readonly class="datepicker" name="enddate" value="">
                <%}
                %>
            </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Charge Unit : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="username" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("valuetype"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Category : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="username" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("category"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Seq No : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="username" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("sequencenum"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Keyword : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="username" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("keyword"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Sub Keyword : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="username" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("subkeyword"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="tr1">Frequency : </td>
            <td class="tr1"><input type="text"  class="txtbox1" size="30" name="username" value="<%=ESAPI.encoder().encodeForHTML((String) hash.get("frequency"))%>" disabled> </td>
        </tr>
        <tr <%=style%>>
            <td class="td1"></td>
            <td class="td1"><input type="submit" align="center" class="button" value="Update"></td>
        </tr>
    </table>
    <input type="hidden" size="30" name="pstartdate" value="<%=(String) hash.get("effectiveStartDate")%>">
    <input type="hidden" size="30" name="penddate" value="<%=(String) hash.get("lastupdateDate")%>">
    <input type="hidden" size="30" name="version" value="<%=(String) hash.get("version")%>">
</form>

    <%
                }
            }
            else
            {
                out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
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
    public String getPreviousDate(String source)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date2 = null;
        try
        {
            date2 = sdf.parse(source);
        }
        catch (ParseException e)
        {
            logger.error("Parse Exception while  getting  PreviousDate",e);
        }
        Date dateBefore = new Date(date2.getTime() + 1 * 24 * 3600 * 1000 );
        String sDate2=sdf.format(dateBefore);
        return sDate2;
    }
%>