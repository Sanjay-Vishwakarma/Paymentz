<%--
  Created by IntelliJ IDEA.
  User: sandip
  Date: 11/25/14
  Time: 4:55 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="index.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.directi.pg.Admin" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.TreeMap" %>
<html>
<head>
    <META content="text/html; charset=windows-1252" http-equiv=Content-Type>
    <meta http-equiv="Expires" content="0">
    <meta http-equiv="Pragma" content="no-cache">
    <link rel="stylesheet" type="text/css" href="/icici/styyle.css">
    <title>Bank Rolling Reserve Manager</title>
    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>


    <link href="/icici/datepicker/datepicker/datepicker3.css" rel="stylesheet">
    <script src="/icici/datepicker/datepicker/bootstrap-datepicker.js"></script>
    <script>
        $(function ()
        {
            $(".datepicker").datepicker({dateFormat: 'dd/mm/yy'});
        });
    </script>

</head>
<body>
<%!
    private static Logger logger=new Logger("addNewBankRollingReserve.jsp");
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (Admin.isLoggedIn(session))
    {
%>
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (Admin.isLoggedIn(session))
    {
        String str2 = "";
        String pgtypeid = "";
        String currency= "";
        currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");
        pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");
        String accountId = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");

        List<String> gatewayCurrency = GatewayTypeService.loadCurrency();
        List<String> gatwayName = GatewayTypeService.loadGateway();


        if(pgtypeid!=null)str2 = str2 + "&pgtypeid=" + pgtypeid;
        else
            pgtypeid="";

        TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();
        TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();


%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Add New Bank Rolling Reserve Release
                <div style="float: right;">
                    <form  action="/icici/addNewBankRollingReserve.jsp?ctoken=<%=ctoken%>" method="POST">
                        <input type="submit"  class="buttonform"  value="Add Rolling Reserve">

                    </form>
                </div>
                <div style="float: right;">
                    <form  action="/icici/bankRollingReserveList.jsp?ctoken=<%=ctoken%>" method="POST">
                        <input type="submit"  class="buttonform"  value="Rolling Reserve List">
                    </form>
                </div>
            </div>

            <%
                String errormsg1 = (String)request.getAttribute("message");
                if (errormsg1 == null)
                {
                    errormsg1 = "";
                }
                else
                {
                    out.println("<table align=\"center\" class=\"textb\" ><tr><td valign=\"middle\"><font class=\"text\" >");

                    out.println(errormsg1);

                    out.println("</font></td></tr></table>");
                }
            %>
            <form action="/icici/servlet/UpdateBankRollingReserve?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">

                <center>
                    <table  border="0" cellpadding="0" cellspacing="0" width="10%" align="center">
                        <tbody>
                        <tr><td colspan="6">&nbsp;</td></tr>
                        <tr><td colspan="6">&nbsp;</td>
                        </tr>
                        <tr>

                            <td class="textb">
                                Gateway&nbsp;&nbsp;:&nbsp;&nbsp;&nbsp;
                            </td>
                            <td>
                                <input name="pgtypeid" id="gateway1" value="<%=pgtypeid%>" class="txtbox"
                                       autocomplete="on">
                               <%-- <select size="1" id="bank" name="pgtypeid" class="txtbox"  style="width:200px;">
                                    <option value="0" default>--All--</option>
                                    <%
                                        for(String gatewayType : gatewayTypeTreeMap.keySet())
                                        {
                                            String isSelected = "";
                                            String value = gatewayType.toUpperCase()+"-"+gatewayTypeTreeMap.get(gatewayType).getPgTypeId();
                                            //String value = g.getGateway().toUpperCase()+"-"+g.getCurrency()+"-"+g.getPgTypeId();
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
                            <td class="textb">
                                &nbsp;
                            </td>
                        <td>&nbsp;&nbsp;&nbsp;</td>

                            <td width="4%" class="textb">&nbsp;</td>
                            <td width="8%" class="textb">Account ID:</td>

                            <td width="12%" class="textb">
                                <input name="accountid" id="accountid1"
                                       value="<%=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid")%>"
                                       class="txtbox" autocomplete="on">
                              <%--  <select size="1" id="accountid" name="accountid" class="txtbox" style="width: 200px">
                                    <option data-bank="all" value="0">---Select AccountID---</option>
                                    <%
                                        for(Integer sAccid : accountDetails.keySet())
                                        {
                                            GatewayAccount g = accountDetails.get(sAccid);
                                            String isSelected = "";
                                            String gateway2 = g.getGateway().toUpperCase();
                                            String currency2 = g.getCurrency();
                                            String pgtype = g.getPgTypeId();
                                            //String aGateway = accountMap.get(sAccountID).getGateway().toUpperCase()+"-"+accountMap.get(sAccountID).getCurrency()+"-"+accountMap.get(sAccountID).getGateway_id();

                                            if (String.valueOf(sAccid).equals(accountId))
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
                          <%--  <td width="4%" class="textb">&nbsp;</td>
                            <td width="8%" class="textb">AccountId:</td>
                            <td width="10%" class="textb">&nbsp;</td>
                            <td width="10%" class="textb">
                                <select size="1" id="accountid" name="accountid" class="txtbox" style="margin-left: 25px; width: 250px"  >
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
                            </td>--%>
                        </tr>
                        <tr><td>&nbsp;</td></tr>
                        <tr><td colspan="6">&nbsp;</td></tr>
                        <tr><td colspan="6">&nbsp;</td></tr>
                        <tr>
                            <td align="right"  class="textb">Rolling&nbsp;Release&nbsp;Date:&nbsp;&nbsp;&nbsp;</td>
                            <td align="left" colspan="3" style="padding: 3px">
                                <input type="text" name="rollingreservedateupto" readonly class="datepicker">
                            </td>
                            <td>
                            <td align="right" class="textb">Time(HH:MM:SS):</td>
                            <td align="left"    style="padding: 3px">
                                <input type="text"  name="rollingRelease_Time" value="" class="textb">
                            </td>
                        </tr>
                        <tr><td colspan="6">&nbsp;</td></tr>
                        <tr><td colspan="6">&nbsp;</td></tr>
                        <tr>
                            <td align="center" colspan="6" class="textb">
                                <button type="submit" class="buttonform" style="margin-left: 100%">
                                    <i class="fa fa-sign-in"></i>
                                    &nbsp;&nbsp;Save
                                </button>
                            </td>
                        </tr>
                        </tbody>
                    </table>
                </center>
            </form>

        </div>
    </div>
</div>
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