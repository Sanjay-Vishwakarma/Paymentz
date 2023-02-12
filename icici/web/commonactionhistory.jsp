<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.core.GatewayAccount" %>
<%@ page import="com.directi.pg.core.GatewayAccountService" %>
<%@ page import="com.directi.pg.core.GatewayType" %>
<%@ page import="com.directi.pg.core.GatewayTypeService" %>
<%@ page import="com.manager.TerminalManager" %>
<%@ page import="com.manager.vo.TerminalVO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 4/9/13
  Time: 2:34 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
    <script src="/icici/css/jquery-ui.min.js"></script>
    <script src="/icici/javascript/gateway-account-member-terminalid.js"></script>

    <%--<script type="text/javascript" language="JavaScript" src="/icici/javascript/memberid_filter.js"></script>--%>
        <%--<script type="text/javascript" language="JavaScript" src="/icici/javascript/gateway-account-member.js"></script>--%>
    <title></title>

    </head>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        //Hashtable statushash = new Hashtable();
        HashMap<String,String> statushash = new LinkedHashMap<String, String>();

        statushash.put("authfailed", "Auth Failed");
        statushash.put("authstarted", "Auth Started");
        statushash.put("authsuccessful", "Auth Successful");
        statushash.put("authcancelled", "Authorisation Cancelled");
        statushash.put("begun", "Begun Processing");
        statushash.put("cancelstarted","Cancel Initiated");
        statushash.put("cancelled", "Cancelled Transaction");
        statushash.put("capturefailed", "Capture Failed");
        statushash.put("capturestarted", "Capture Started");
        statushash.put("capturesuccess", "Capture Successful");
        statushash.put("chargeback", "Chargeback");
        statushash.put("partialrefund", "Partial Refund");
        statushash.put("payoutfailed", "Payout Failed");
        statushash.put("payoutstarted", "Payout Started");
        statushash.put("payoutsuccessful", "Payout Successful");
        statushash.put("podsent", "POD Sent ");
        statushash.put("proofrequired", "Proof Required");
        statushash.put("markedforreversal", "Reversal Request Sent");
        statushash.put("reversed", "Reversed");
        statushash.put("settled", "Settled");
        statushash.put("failed", "Validation Failed");

        String str="";

        str = str + "ctoken=" + ctoken;
        String trackingid=request.getParameter("trackingid");
        String paymentid=request.getParameter("paymentid");
        String reqaccountid=request.getParameter("accountid");
        String reqgateway=request.getParameter("gateway");
        String memberid2=request.getParameter("memberid");
        String toid=request.getParameter("toid");
        if(trackingid!=null)
        {
            str = str + "&trackingid="+trackingid;
        }
        if(paymentid!=null)
        {
            str = str + "&paymentid="+paymentid;
        }
        if(reqaccountid!=null)
        {
            str = str + "&accountid="+reqaccountid;
        }
        if(reqgateway!=null)
        {
            str = str + "&gateway="+reqgateway;
        }
        if(memberid2!=null)
        {
            str = str + "&memberid="+memberid2;
        }
        if(toid!=null)
        {
            str = str + "&toid="+toid;
        }
        str+="&status="+request.getParameter("status");
        int pageno=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")),1);
        int pagerecords=convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")),15);

%>
<body>

<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        String str2 = "";
        String pgtypeid = "";
        String currency= "";
        currency = Functions.checkStringNull(request.getParameter("currency"))==null?"":request.getParameter("currency");

        pgtypeid = Functions.checkStringNull(request.getParameter("pgtypeid"))==null?"":request.getParameter("pgtypeid");

       /* TerminalManager terminalManager = new TerminalManager();
        List<TerminalVO> terminalList = terminalManager.getAllMappedTerminals();
        TreeMap<String,GatewayType> gatewayTypeTreeMap = GatewayTypeService.getAllGatewayTypesMap();
        TreeMap<Integer, GatewayAccount> accountDetails = GatewayAccountService.getAccountDetails();*/

       // String accountid = Functions.checkStringNull(request.getParameter("accountid"));
        String memberid = Functions.checkStringNull(request.getParameter("toid"))==null?"":request.getParameter("toid");

        /*TreeMap<String,TerminalVO> memberMap = new TreeMap<String, TerminalVO>();

        for(TerminalVO terminalVO : terminalList)
        {
            String memberKey = terminalVO.getMemberId()+"-"+terminalVO.getAccountId()+"-"+terminalVO.getGateway().toUpperCase() + "-" + terminalVO.getCurrency() + "-" + terminalVO.getGateway_id();
            memberMap.put(memberKey,terminalVO);
        }*/

%>

<form action="/icici/servlet/CommonActionHistory?ctoken=<%=ctoken%>" method="post" name="forms" >
    <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">

<div class="row">
<div class="col-lg-12">
<div class="panel panel-default">
<div class="panel-heading">
    Common Action History
</div>
    <table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:1.0%;margin-right: 2.5% ">

        <tr>
            <td>

                <table border="0" cellpadding="5" cellspacing="0" width="95%"  align="center">


                    <tr><td colspan="4">&nbsp;</td></tr>

                    <tr>
                        <td width="4%" class="textb">&nbsp;</td>
                        <td width="8%" class="textb" >Gateway</td>
                        <td width="3%" class="textb"></td>
                        <td width="12%" class="textb">
                            <input name="pgtypeid" id="gateway" value="<%=pgtypeid%>" class="txtbox" autocomplete="on">
                         <%--   <select size="1" id="bank" name="pgtypeid" class="txtbox"  style="width:200px;">
                                <option value="0" default>--All--</option>
                                <%

                                    for(String gatewayType : gatewayTypeTreeMap.keySet())
                                    {
                                        String isSelected = "";
                                        //String value = gatewayType+"-"+gatewayTypeTreeMap.get(gatewayType).getPgTypeId();
                                        //String value = g.getGateway().toUpperCase()+"-"+g.getCurrency()+"-"+g.getPgTypeId();
                                        if(gatewayType.equalsIgnoreCase(pgtypeid))
                                        {
                                            isSelected = "selected";
                                        }
                                    /*for(String sGateway : gatewayMap.keySet())
                                    {
                                        //String gateway2 = "";
                                        String isSelected = "";
                                        TerminalVO terminalVO = gatewayMap.get(sGateway);
                                        String gateway2 = terminalVO.getGateway();
                                        String currency2 = terminalVO.getCurrency();
                                        String value = gateway2+"-"+currency2+"-"+terminalVO.getGateway_id();
                                        if(pgtypeid.equalsIgnoreCase(value))
                                        {
                                            isSelected = "selected";
                                        }*/
                                %>
                                <option value="<%=gatewayType%>" <%=isSelected%>><%=gatewayType%></option>
                                &lt;%&ndash;<option value="<%=value%>" <%=isSelected%>><%=sGateway%></option>&ndash;%&gt;
                                <%
                                    }
                                %>

                            </select>--%>
                        </td>

                        <td width="4%" class="textb">&nbsp;</td>
                        <td width="8%" class="textb" >Account ID</td>
                        <td width="3%" class="textb"></td>
                        <td width="12%" class="textb">
                            <input name="accountid" id="accountid" value="<%=Functions.checkStringNull(request.getParameter("accountid"))==null?"":request.getParameter("accountid")%>" class="txtbox" autocomplete="on">
                        <%-- <select size="1" id="accountid" name="accountid" class="txtbox" style="width: 200px">
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


                                        if (String.valueOf(sAccid).equals(accountid))
                                            isSelected = "selected";
                                        else
                                            isSelected = "";
                                    /*for(String sAccountID:accountMap.keySet())
                                    {
                                        String isSelected = "";
                                        String sAccid = accountMap.get(sAccountID).getAccountId();
                                        String gateway2 = accountMap.get(sAccountID).getGateway();
                                        String currency2 = accountMap.get(sAccountID).getCurrency();
                                        //String aGateway = accountMap.get(sAccountID).getGateway().toUpperCase()+"-"+accountMap.get(sAccountID).getCurrency()+"-"+accountMap.get(sAccountID).getGateway_id();


                                        if (sAccid.equals(accountid))
                                            isSelected = "selected";
                                        else
                                            isSelected = "";*/
                                %>
                                <option data-bank="<%=gateway2+"-"+currency2+"-"+pgtype%>"  value="<%=sAccid%>" <%=isSelected%>><%=sAccid+"-"+g.getMerchantId()+"-"+g.getCurrency()%></option>
                                &lt;%&ndash;<option data-bank="<%=gateway2+"-"+currency2+"-"+accountMap.get(sAccountID).getGateway_id()%>"  value="<%=sAccid%>" <%=isSelected%>><%=sAccountID+"-"+accountMap.get(sAccountID).getGwMid()+"-"+accountMap.get(sAccountID).getCurrency()%></option>&ndash;%&gt;
                                <%

                                    }
                                %>
                            </select>--%>
                        </td>



                        <td width="4%" class="textb">&nbsp;</td>
                        <td width="8%" class="textb" >Member ID</td>
                        <td width="3%" class="textb"></td>
                        <td width="12%" class="textb">
                            <input name="toid" id="memberid" value="<%=memberid%>" class="txtbox" autocomplete="on">
                           <%-- <select name="toid" id="memberid" class="txtbox" style="width:200px;">
                                <option data-bank="all"  data-accid="all" value="0" selected>--Select MemberID--</option>
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
                                    //System.out.println("map---"+memberMap);
                                    /*for(String sMemberId: memberMap.keySet())
                                    {
                                        //System.out.println("----"+t.getMemberId()+"-"+t.getGateway());
                                        TerminalVO t = memberMap.get(sMemberId);
                                        String aGateway = memberMap.get(sMemberId).getGateway().toUpperCase()+"-"+memberMap.get(sMemberId).getCurrency()+"-"+memberMap.get(sMemberId).getGateway_id();
                                        String accId = t.getAccountId();
                                        String aContactPerson = t.getContactPerson();
                                        String gateway2 = t.getGateway();
                                        String currency2 = t.getCurrency();
                                        String pgtype = t.getGateway_id();
                                        String isSelected = "";
                                        if(String.valueOf(t.getMemberId()+"-"+accId).equalsIgnoreCase(memberid+"-"+accountid))
                                        {
                                            isSelected = "selected";
                                        }
                                        else
                                        {
                                            isSelected = "";
                                        }*/
                                %>
                                <option data-bank="<%=value%>"  data-accid="<%=accId%>" value="<%=t.getMemberId()%>" <%=isSelected%>><%=t.getMemberId()+"-"+accId+"-"+aCompanyName%></option>
                                &lt;%&ndash;<option data-bank="<%=gateway2+"-"+currency2+"-"+pgtype%>"  data-accid="<%=accId%>" value="<%=t.getMemberId()%>" <%=isSelected%>><%=t.getMemberId()+"-"+accId+"-"+aContactPerson%></option>&ndash;%&gt;
                                <%

                                    }
                                %>
                            </select>--%>
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

                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >Tracking ID</td>
                    <td width="3%" class="textb"></td>
                    <td width="12%" class="textb">
                            <input maxlength="15" type="text" name="trackingid" class="txtbox" value="<%=request.getParameter("trackingid")==null?"":request.getParameter("trackingid")%>">

                        </td>



                    <td width="4%" class="textb">&nbsp;</td>
                    <td width="8%" class="textb" >Status</td>
                    <td width="3%" class="textb"></td>
                    <td width="12%" class="textb">
                            <select size="1" name="status" class="txtbox" style="margin-left: 13%">
                                <option value="">All</option>
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
                        <td width="70%" class="textb">&nbsp;</td>
                        <td width="150%" class="textb"></td>
                        <td width="30%" class="textb"></td>
                        <td width="80%" class="textb"><button type="submit" class="buttonform" style="margin-left: 13%">
                            <i class="fa fa-clock-o"></i>
                            &nbsp;&nbsp;Search
                        </button>
                        </td>

                    </tr>
                    <tr>
                        <td width="2%" class="textb">&nbsp;</td>

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
    String errormsg1 = (String)request.getAttribute("cbmessage");
    if (errormsg1 == null)
    {
        errormsg1 = "";
    }
    else
    {   out.println("<BR>");
        out.println("<table align=\"center\"  ><tr><td valign=\"middle\"><font class=\"text\" >");

        out.println(errormsg1);

        out.println("</font></td></tr></table>");
        out.println("<BR>");
    }

    String error=(String ) request.getAttribute("errormessage");
    if(error !=null)
    {
        out.println("<b>");
        out.println(error);
        out.println("</b>");
    }
%>

<%  Hashtable hash = (Hashtable)request.getAttribute("transdetails");

    Hashtable temphash=null;
    str = str + "&pgtypeid=" + pgtypeid;
    str = str + "&currency=" + currency;
    int records=0;
    int totalrecords=0;
    error=(String ) request.getAttribute("error");
    if(error !=null)
    {
        out.println("<center><font class=\"text\" face=\"arial\">"+error+"</font></center>");
    }


    String currentblock=request.getParameter("currentblock");

    if(currentblock==null)
    {
        currentblock="1";
    }
    try
    {
        records=Integer.parseInt((String)hash.get("records"));
        totalrecords=Integer.parseInt((String)hash.get("totalrecords"));

        //System.out.println("records---"+records+"--total---"+totalrecords);
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

    <table align=center width="50%" class="table table-striped table-bordered table-hover table-green dataTable">
         <thead>
            <tr>
                <td valign="middle" align="center" class="th0">Sr&nbsp;no</td>
                <td valign="middle" align="center" class="th0">TrackingID</td>
                <td valign="middle" align="center" class="th0">Action</td>
                <td valign="middle" align="center" class="th0">Status</td>
                <td valign="middle" align="center" class="th0">TimeStamp</td>
                <td valign="middle" align="center" class="th0">Customer IpAddress</td>
                <td valign="middle" align="center" class="th0">Amount</td>
                <td valign="middle" align="center" class="th0">Remark</td>
                <td valign="middle" align="center" class="th0">RES&nbsp;TransactionID</td>
                <td valign="middle" align="center" class="th0">RES&nbsp;Status</td>
                <td valign="middle" align="center" class="th0">RES&nbsp;Code</td>
                <td valign="middle" align="center" class="th0">RES&nbsp;Description</td>
                <td valign="middle" align="center" class="th0">Actionex Id</td>
                <td valign="middle" align="center" class="th0">Actionex Name</td>
            </tr>
         </thead>
        <%
            String style="class=td1";
            String ext="light";
            String previousicicitransid ="";
            String previousStyle="class=tr0";
            String previousExt="dark";
            String currentStyle="class=tr1";
            String currentExt="light";
            String trackingId = "";
            //String action = "";
            //String status = "";
            //String timeStamp = "";
            //String ipaddr = "";
            //String amount = "";
            //String remark = "";
            //String responseTransaId = "";
            //String responseTransaStatus = "";
            //String responseCode = "";
           // String resDescription = "";
            //String actionExecutorId = "";
            //String actionExecutorName = "";
            Functions functions = new Functions();
            for(int pos=1;pos<=records;pos++)
            {
                String id=Integer.toString(pos);
                int srno=Integer.parseInt(id)+ ((pageno-1)*pagerecords);
                temphash=(Hashtable)hash.get(id);
                if(!previousicicitransid.equals((String) temphash.get("trackingid")))
                {
                    String tempStyle="";
                    String tempExt="";

                    previousicicitransid = (String) temphash.get("trackingid");

                    tempStyle = previousStyle;
                    tempExt =  previousExt;

                    previousStyle=currentStyle;
                    previousExt=currentExt;

                    currentStyle=tempStyle;
                    currentExt=tempExt;
                    trackingId = ESAPI.encoder().encodeForHTML((String) temphash.get("trackingid"));
                    String action = ESAPI.encoder().encodeForHTML((String) temphash.get("action"));
                    String status = ESAPI.encoder().encodeForHTML((String) temphash.get("status"));
                    String timeStamp = ESAPI.encoder().encodeForHTML((String)temphash.get("timestamp"));
                    String ipaddr = ESAPI.encoder().encodeForHTML((String)temphash.get("ipaddress"));
                    String amount = ESAPI.encoder().encodeForHTML((String)temphash.get("amount"));
                    String remark = ESAPI.encoder().encodeForHTML((String)temphash.get("remark"));
                    String responseTransaId = ESAPI.encoder().encodeForHTML((String)temphash.get("responsetransactionid"));
                    String responseTransaStatus = ESAPI.encoder().encodeForHTML((String)temphash.get("responsetransactionstatus"));
                    String responseCode = ESAPI.encoder().encodeForHTML((String)temphash.get("responsecode"));
                    String resDescription = ESAPI.encoder().encodeForHTML((String)temphash.get("responsedescription"));
                    String actionExecutorId = ESAPI.encoder().encodeForHTML((String)temphash.get("actionexecutorid"));
                    String actionExecutorName = ESAPI.encoder().encodeForHTML((String)temphash.get("actionexecutorname"));
                }
                style=currentStyle;
                ext=currentExt;

                out.println("<tr>");

                out.println("<td align=center "+style+">"+srno+ "</td>");
                if (functions.isValueNull(trackingId))
                {
                    out.println("<td align=center " + style + ">" + trackingId + "</td>");
                }
                else
                {
                    out.println("<td align=center " + style + ">" + "-" + "</td>");
                }

                if (functions.isValueNull((String) temphash.get("action")))
                {
                    out.println("<td align=center " + style + ">" + (String) temphash.get("action") + "</td>");
                }
                else
                {
                    out.println("<td align=center " + style + ">" + "-" + "</td>");
                }

                if (functions.isValueNull((String) temphash.get("status")))
                {
                    out.println("<td align=center " + style + ">" + (String)temphash.get("status")+ "</td>");
                }
                else
                {
                    out.println("<td align=center " + style + ">" + "-" + "</td>");

                }

                if (functions.isValueNull((String) temphash.get("timestamp")))
                {
                    out.println("<td align=center " + style + ">" + (String) temphash.get("timestamp") + "</td>");
                }
                else
                {
                    out.println("<td align=center " + style + ">" + "-" + "</td>");
                }

                if (functions.isValueNull((String) temphash.get("ipaddress")))
                {
                    out.println("<td align=center "+style+">"+(String) temphash.get("ipaddress")+"</td>");
                }
                else
                {
                    out.println("<td align=center "+style+">"+"-"+"</td>");
                }

                if (functions.isValueNull((String) temphash.get("amount")))
                {
                    out.println("<td align=center "+style+">"+(String) temphash.get("amount")+"</td>");
                }
                else
                {
                    out.println("<td align=center "+style+">"+"-"+"</td>");
                }

                if (functions.isValueNull((String) temphash.get("responsetransactionid")))
                {
                    out.println("<td align=center " + style + ">" + (String) temphash.get("responsetransactionid") + "</td>");
                }
                else
                {
                    out.println("<td align=center " + style + ">" + "-" + "</td>");
                }

                if (functions.isValueNull((String) temphash.get("responsetransactionid")))
                {
                    out.println("<td align=center " + style + ">" + (String) temphash.get("responsetransactionid") + "</td>");
                }
                else
                {
                    out.println("<td align=center " + style + ">" + "-" + "</td>");
                }

                if (functions.isValueNull((String) temphash.get("responsetransactionstatus")))
                {
                    out.println("<td align=center " + style + ">" + (String) temphash.get("responsetransactionstatus") + "</td>");
                }
                else
                {
                    out.println("<td align=center " + style + ">" + "-" + "</td>");
                }

                if (functions.isValueNull((String) temphash.get("responsecode")))
                {
                    out.println("<td align=center "+style+">"+(String) temphash.get("responsecode")+"</td>");
                }
                else
                {
                    out.println("<td align=center "+style+">"+ "-" +"</td>");
                }

                if (functions.isValueNull((String) temphash.get("responsedescription")))
                {
                    out.println("<td align=center " + style + ">" + (String) temphash.get("responsedescription") + "</td>");
                }
                else
                {
                    out.println("<td align=center "+style+">"+ "-" +"</td>");
                }

                if (functions.isValueNull((String) temphash.get("actionexecutorid")))
                {
                    out.println("<td "+style+">&nbsp;"+(String) temphash.get("actionexecutorid")+"</td>");
                }
                else
                {
                    out.println("<td "+style+">&nbsp;"+ "-" +"</td>");
                }

                if (functions.isValueNull((String) temphash.get("actionexecutorname")))
                {
                    out.println("<td " + style + ">&nbsp;" + (String) temphash.get("actionexecutorname") + "</td>");
                }
                else
                {
                    out.println("<td "+style+">&nbsp;"+ "-" +"</td>");

                }

                //out.println("<td " + style + ">&nbsp;<font face=\"arial,verdana,helvetica\"  size=\"1\" ><a href=\"/icici/servlet/CommonInquiry?trackingid=" + ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("trackingid"))+"&description="+(String) temphash.get("description")+"&ctoken="+ctoken+"&accountid="+temphash.get("accountid")+"&toid="+temphash.get("toid")+"\"> <font class=\"textb\"> Transection Inquiry </font></a></font></td>");

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
                <jsp:param name="page" value="CommonActionHistory"/>
                <jsp:param name="currentblock" value="<%=currentblock%>"/>
                <jsp:param name="orderby" value=""/>
            </jsp:include>
        </td>
    </tr>
    </table>
</div>
<%
    }
    else
    {
        out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
    }


%>

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