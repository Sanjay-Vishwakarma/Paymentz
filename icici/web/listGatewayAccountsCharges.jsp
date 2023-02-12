<%@ page import="com.directi.pg.Functions,org.owasp.esapi.ESAPI"%>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
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
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.manager.ChargeManager" %>
<%@ page import="com.manager.vo.ChargeVO" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.manager.vo.payoutVOs.ChargeMasterVO" %>
<%!
    private static Logger logger=new Logger("listGatewayAccountsCharges.jsp");
%>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>

    <%--<script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-accountid.js"></script>--%>
    <title>Settings> Charges Interface> Gateway Account Charges</title>
</head>
<body>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String role="Admin";
        String username=(String)session.getAttribute("username");
        String actionExecutorId=(String)session.getAttribute("merchantid");
        String actionExecutorName=role+"-"+username;

        String str2 = "";
        String pgtypeid = "";
        String currency= "";
        String chargeId="";
        currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");
        pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
        chargeId = Functions.checkStringNull(request.getParameter("chargeid"))==null?"":request.getParameter("chargeid");
        String accountid2 = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
        ChargeManager chargeManager=new ChargeManager();
        List<ChargeVO> chargeList=chargeManager.getListOfCharges();
        if(pgtypeid!=null)str2 = str2 + "&pgtypeid=" + pgtypeid;
        else
            pgtypeid="";
        if(accountid2!=null)str2 = str2 + "&accountid=" + accountid2;
        else
            accountid2="";
%>
<div class="row">
<div class="col-lg-12">
<div class="panel panel-default" >
<div class="panel-heading" >
    Gateway Accounts Charges
    <div style="float: right;">
        <form action="/icici/manageGatewayAccountsCharges.jsp?ctoken=<%=ctoken%>" method="POST">

            <button type="submit" value="Add New Gateway Account Charge Mapping" name="submit" class="addnewmember" style="width:330px ">
                <i class="fa fa-sign-in"></i>
                &nbsp;&nbsp;Add New Gateway Account Charge Mapping
            </button>
        </form>
    </div>
</div>
<br>
<form action="/icici/servlet/ListGatewayAccountsCharges?ctoken=<%=ctoken%>" method="post" name="forms" >
<input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
<%
    ChargeMasterVO chargeMasterVO = new ChargeMasterVO();
    Functions functions = new Functions();
   // String accountid=request.getParameter("accountid");
    String paymode=request.getParameter("paymode");
    String isinputrequired=request.getParameter("isinputrequired");
    String cardtype=request.getParameter("cardtype");
    String chargename=request.getParameter("chargename");
    String chargevalue=request.getParameter("chargevalue");
    String chargetype=request.getParameter("chargetype");


    int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
    int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

    String str="ctoken=" + ctoken;
  //  if (accountid != null) str = str + "&accountid=" + accountid;
    if (paymode != null) str = str + "&paymode=" + paymode;
    if (isinputrequired != null) str = str + "&isinputrequired=" + isinputrequired;
    if (cardtype != null) str = str + "&cardtype=" + cardtype;
    if (chargename != null) str = str + "&chargename=" + chargename;
    if (chargevalue != null) str = str + "&chargevalue=" + chargevalue;
    if (chargetype != null) str = str + "&chargetype=" + chargetype;
    if (pgtypeid != null) str = str + "&pgtypeid=" + pgtypeid;
    if (accountid2 != null) str = str + "&accountid=" + accountid2;



%>

<table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">
    <tr>
        <td>
            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr><td colspan="4">&nbsp;</td></tr>
                <tr>
                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >Gateway</td>
                    <td width="25%" class="textb">
                        <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                    <%--<select size="1" id="bank" class="txtbox" name="pgtypeid" style="margin-left: 3%" >
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

                    <td width="8%" class="textb" >Account ID</td>
                    <td width="22%" class="textb">
                        <input name="accountid" id="accountid1" value="<%=accountid2%>" class="txtbox" autocomplete="on">
                    <%-- <select size="1" id="accountid" name="accountid" style="margin-left: 1%" class="txtbox">
                            <option data-bank="all"  value="0">Select AccountID</option>
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
                                    else
                                        isSelected = "";
                            %>
                            <option data-bank="<%=gateway2+"-"+currency2+"-"+pgtype%>"  value="<%=sAccid%>" <%=isSelected%>><%=sAccid+"-"+g.getMerchantId()+"-"+g.getCurrency()%></option>
                            <%

                                }
                            %>
                           </select>--%>
                    </td>

                    </tr>
                <tr><td>&nbsp;&nbsp;</td></tr>
                <tr>
                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >Charge Name</td>
                    <td width="12%" class="textb">
                        <select class="txtbox" name="chargename" style="margin-left: 3%">
                                <option value="" selected>Select Charge Name</option>
                            <%
                                for(ChargeVO  chargeVO:chargeList)
                                {
                                    String isSelected="";
                                    String chargeID=chargeVO.getChargeid()+"-"+chargeVO.getChargename();
                                    if(chargeID.split("-")[0].equalsIgnoreCase(chargename))
                                    //if(chargeID.equalsIgnoreCase(chargename))
                                        isSelected="selected";
                                    else
                                        isSelected="";

                            %>
                      <option value="<%=chargeID.split("-")[0]%>" <%=isSelected%>><%=chargeID%></option>

                            <%
                                }
                            %>
                        </select>

                    </td>
                    <td width="12%" class="textb">
                        <button type="submit" class="buttonform" value="Search" style="margin-left: 100%">
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
<div class="reporttable">

    <%  Hashtable hash = (Hashtable) request.getAttribute("transdetails");

        Hashtable temphash = null;
        String error = (String) request.getAttribute("errormessage");
        if (error != null)
        {
            out.println(error);

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
                totalrecords=convertStringtoInt((String)hash.get("totalrecords"),0);
            }
            catch (NumberFormatException ex)
            {
                logger.error("Records & TotalRecords is found null",ex);
            }
            if (records > 0)
            {

    %>
    <table align=center width="70%" class="table table-striped table-bordered table-hover table-green dataTable">
        <thead>
        <tr>
            <td valign="middle" align="center" class="th0">Sr No</td>
            <td valign="middle" align="center" class="th0">Account Id</td>
            <td valign="middle" align="center" class="th0">Gateway Name</td>
            <td valign="middle" align="center" class="th0">Input Required</td>
            <td valign="middle" align="center" class="th0">Charge Name</td>
            <td valign="middle" align="center" class="th0">Charge Value</td>
            <td valign="middle" align="center" class="th0">Sequence No</td>
            <td valign="middle" align="center" class="th0" colspan="2">Action</td>
            <td valign="middle" align="center" class="th0">Action Executor Id</td>
            <td valign="middle" align="center" class="th0">Action Executor Name</td>
        </tr>
        </thead>
        <%
            String style = "class=td1";
            String ext = "light";

            for (int pos = 1; pos <= records; pos++)
            {
                String id = Integer.toString(pos);
                temphash = (Hashtable) hash.get(id);
                int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

                if (pos % 2 == 0)
                {
                    style = "class=tr0";
                    ext = "dark";
                }
                else
                {
                    style = "class=tr1";
                    ext = "light";
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

                out.println("<tr>");
                out.println("<td align=center "+style+">&nbsp;"+srno+ "</td>");
                String accountId = (String)temphash.get("accountid");
                GatewayAccount  gatewayAccount=GatewayAccountService.getGatewayAccount(accountId);
                String gatewayName=gatewayAccount.getGateway();
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(accountId)+"<input type=\"hidden\" name=\"accountid\" value=\""+accountId+"\"></td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(gatewayName)+"<input type=\"hidden\" name=\"gatewayName\" value=\""+gatewayName+"\"></td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("isinputrequired"))+"<input type=\"hidden\" name=\"isinputrequired\" value=\""+temphash.get("isinputrequired")+"\"></td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargename"))+"<input type=\"hidden\" name=\"chargename\" value=\""+temphash.get("chargename")+"\"></td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("chargevalue"))+"<input type=\"hidden\" name=\"chargevalue\" value=\""+temphash.get("chargevalue")+"\"></td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("sequencenum"))+"<input type=\"hidden\" name=\"sequencenum\" value=\""+temphash.get("sequencenum")+"\"></td>");
                out.println("<td align=\"center\" " + style + "><form action=\"/icici/servlet/ActionGatewayAccountsCharges?ctoken=" + ctoken + "\" method=\"post\"><input type=\"hidden\" name=\"mappingid\" value=\"" + temphash.get("mappingid") + "\"><input type=\"hidden\" name=\"action\" value=\"modify\"><input type=\"submit\" class=\"goto\" value=\"Modify\"></form></td>");
                out.println("<td align=\"center\" " + style + "><form action=\"/icici/servlet/ActionGatewayAccountsCharges?ctoken=" + ctoken + "\" method=\"post\"><input type=\"hidden\" name=\"mappingid\" value=\"" + temphash.get("mappingid") + "\"><input type=\"hidden\" name=\"action\" value=\"history\"><input type=\"submit\" class=\"goto\" value=\"History\"></form></td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(actionExecutorId)+"</td>");
                out.println("<td align=center "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML(actionExecutorName)+"</td>");
                out.println("</tr>");
            }
        %>
    </table>
    <table align=center valign=top><tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="ListGatewayAccountsCharges"/>
                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                <jsp:param name="orderby" value=""/>
            </jsp:include>
        </td>
    </tr>
    </table>
    <%  }
    else
    {
        out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
    }
    }
    else
    {
        out.println(Functions.NewShowConfirmation("Filter","fill in the required data for GatewayAccounts Charges details"));
    }
    }
    %>

</div>
</body>
</html>