<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 7/29/13
  Time: 2:13 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<html>
<head>

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid1.js"></script>

   <%-- <script type="text/javascript" language="JavaScript" src="/icici/javascript/accountid.js"></script>
    <script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-accountid.js"></script>--%>
    <title>Settings> Compliance</title>
</head>

<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String str="";

        str = str + "ctoken=" + ctoken;

        String firstsix = Functions.checkStringNull(request.getParameter("firstsix"))==null?"":request.getParameter("firstsix");
        String lastfour = Functions.checkStringNull(request.getParameter("lastfour"))==null?"":request.getParameter("lastfour");
        firstsix=(String)request.getAttribute("firstsix");
        lastfour=(String)request.getAttribute("lastfour");

        if (firstsix != null) str = str + "&firstsix=" + firstsix;
        else
            firstsix="";
        if (lastfour != null) str = str + "&lastfour=" + lastfour;
        else
            lastfour="";
        //if (account != null) str = str + "&accountid=" + account;

        TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();
        TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();

        String pgtypeid = "";
        String currency= "";
        currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");
        pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");

        String accountid = Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid");
       /* accountid2 = (String) request.getAttribute("accountid");*/

        List<String> gatewayCurrency = GatewayTypeService.loadCurrency();
        List<String> gatwayName = GatewayTypeService.loadGateway();

        if(pgtypeid!=null)str = str + "&pgtypeid=" + pgtypeid;
        else
            pgtypeid="";
        if(currency!=null)str = str + "&currency=" + currency;
        else
            currency="";
        if(accountid!=null)str = str + "&accountid=" + accountid;
        else
            accountid="";

        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);
%>

<body>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
                Compliance
            </div>
            <br>
            <form action="/icici/servlet/Compliance?ctoken=<%=ctoken%>" method="post" name="forms" >
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">
                    <%
                        String error=(String ) request.getAttribute("error");
                        if(error !=null)
                        {
                            out.println("<BR>");
                            out.println("<table align=\"center\"  ><tr><td valign=\"middle\"><font class=\"text\" ><b>");
                            out.println(error);
                            out.println("</b></font></td></tr></table>");
                            out.println("<BR>");
                        }
                    %>
                    <tr>
                        <td>
                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                                <tr>
                                    <td colspan="4">&nbsp;</td>
                                </tr>
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
                                        <input name="accountid" id="accountid1" value="<%=accountid%>" class="txtbox" autocomplete="on">
                                    <%--<select size="1" id="accountid" name="accountid" style="margin-left: 1%" class="txtbox">
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
                                    <td width="0%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb" >First_SIX & Last_Four</td>
                                    <td width="200%" class="textb">
                                        <input size="8" maxlength="6" type="text"  class="txtboxsmall" name="firstsix"  value="<%=request.getParameter("firstsix")==null?"":request.getParameter("firstsix")%>">******<input maxlength="4" size="6" type="text" name="lastfour" class="txtboxsmall" value="<%=request.getParameter("lastfour")==null?"":request.getParameter("lastfour")%>">
                                    </td>

                                    <td width="200%" class="textb">&nbsp;
                                        <button type="submit" class="buttonform" value="Submit" style="margin-left: 100%">
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
    <%  Hashtable hash = (Hashtable)request.getAttribute("transdetails");

        Hashtable temphash=null;
        int records=0;
        int totalrecords=0;

        String currentblock=request.getParameter("currentblock");

        if(currentblock==null)
            currentblock="1";

        try
        {
            records=Integer.parseInt((String)hash.get("records"));
            totalrecords=Integer.parseInt((String)hash.get("totalrecords"));
        }
        catch(Exception ex)
        {

        }
        if(hash!=null)
        {
            hash = (Hashtable)request.getAttribute("transdetails");
        }
        if(records>0)
        {
    %>
    <form  action="?ctoken=<%=ctoken%>" method="post">
        <table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
            <thead>
            <tr>
                <td valign="middle" align="center" class="th0">Sr no</td>
                <td valign="middle" align="center" class="th0">TrackingID</td>
                <td valign="middle" align="center" class="th0">toid</td>
                <td valign="middle" align="center" class="th0">First_Six Last_Four</td>
                <td valign="middle" align="center" class="th0">Accountid</td>
                <td valign="middle" align="center" class="th0">Amount</td>
                <td valign="middle" align="center" class="th0">status</td>
                <td valign="middle" align="center" class="th0">EmailID</td>
            </tr>
            </thead>
            <%
                String style="class=td1";
                String ext="light";

                for(int pos=1;pos<=records;pos++)
                {
                    String id=Integer.toString(pos);
                    int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);
                    if(pos%2==0)
                    {
                        style="class=tr0";
                        ext="dark";
                    }
                    else
                    {
                        style="class=tr1";
                        ext="light";
                    }
                    temphash=(Hashtable)hash.get(id);
                    out.println("<tr>");

                    out.println("<td "+style+">&nbsp;"+srno+ "</td>");
                    out.println("<td "+style+">&nbsp;" +ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"))+"<input type=\"hidden\" name=\"trackingid\" value=\""+temphash.get("trackingid")+"\"></td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("toid"))+"<input type=\"hidden\" name=\"toid\" value=\""+temphash.get("toid")+"\"></td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("first_six"))+" XXXXXX "+ESAPI.encoder().encodeForHTML((String)temphash.get("last_four"))+"</td>");

                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("accountid"))+"</td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("amount"))+"</td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("status"))+"</td>");
                    out.println("<td "+style+">&nbsp;"+ESAPI.encoder().encodeForHTML((String)temphash.get("emailaddr"))+"</td>");

                    out.println("</tr>");
                }
            %>

        </table>

    </form>
    <table align=center valign=top><tr>
        <td align=center>
            <jsp:include page="page.jsp" flush="true">
                <jsp:param name="numrecords" value="<%=totalrecords%>"/>
                <jsp:param name="numrows" value="<%=pagerecords%>"/>
                <jsp:param name="pageno" value="<%=pageno%>"/>
                <jsp:param name="str" value="<%=str%>"/>
                <jsp:param name="page" value="Compliance"/>
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
            out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
        }
    %>
</div>
<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</body>
</html>