<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.TreeMap" %>
<%--
  Created by IntelliJ IDEA.
  User: Saurabh
  Date: 2/24/14
  Time: 3:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("agentname"));%>
<html>
<head>
    <title><%=company%> Merchant Commercials and Limits</title>
</head>
<%
        ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

        if (agent.isLoggedInAgent(session))
        {
            Hashtable memberidDetails=agent.getAgentMemberDetails((String)session.getAttribute("merchantid"));
            String memberid=nullToStr(request.getParameter("memberid"));
%>
<body>
<p align="center" class="title"><%=company%> Merchant Commercials and Limits</p>
<form name="form" method="post" action="/agent/net/AgentMemberConfig?ctoken=<%=ctoken%>">
    <table border="0" cellpadding="4" cellspacing="0" width="100%" align="center">
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <tr>
            <td valign="middle" align="right" bgcolor="#6B7A8A" width="1%" class="label">&nbsp;</td>
            <td valign="middle" align="center" bgcolor="#6B7A8A" class="label" width="20%">
                Merchant ID
                <select size="1" name="memberid" class="textBoxes">

                    <%
                        String selected3 = "";
                        String key3 = "";
                        String value3 = "";
                        TreeMap treeMap = new TreeMap(memberidDetails);
                        Iterator itr = treeMap.keySet().iterator();
                        while (itr.hasNext())
                        {
                            key3 = (String) itr.next();
                            value3 = treeMap.get(key3).toString();
                            if (value3.equals(memberid))
                                selected3 = "selected";
                            else
                                selected3 = "";
                    %>
                    <option value="<%=key3%>" <%=selected3%>><%=key3%>---<%=value3%></option>
                    <%
                        }
                    %>
                </select>
            </td>
            <td valign="middle" align="left" bgcolor="#6B7A8A" class="label" width="8%" rowspan="2">
                <input type="submit" value="Submit" name="B1" class="button">
            </td>

            <td valign="middle" align="right" bgcolor="#6B7A8A" width="1%" class="label">&nbsp;</td>
        </tr>

    </table>
</form>
<br>
<%

    String errormsg = (String)request.getAttribute("cbmessage");
    if (errormsg == null)
        errormsg = "";

    out.println("<table align=\"center\"  ><tr><td valign=\"middle\"><font class=\"text\" >");

    out.println(errormsg);

    out.println("</font></td></tr></table>");


%>

<%
    Hashtable hash = (Hashtable) request.getAttribute("memberdetails");
    Hashtable uhash = (Hashtable) request.getAttribute("uhash");
    Hashtable temphash = null;


    int pageno = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
    int pagerecords = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);

    int records = 0;
    int totalrecords = 0;
    int records1 = 0;

    try
    {
        records = Integer.parseInt((String) hash.get("records"));
        totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
    }
    catch (Exception ex)
    {

    }
    String str="";

    if (uhash != null && uhash.size() > 0)
    {
%>
<font class="info">Modification Successful</font>
<% }

    if (records > 0)
    {  str = str + "?SRecords=" + pagerecords;
        str = str + "&ctoken=" + ctoken;

%>


<br>
<center><font class="info">Total records: <%=totalrecords%></font></center>
<form action="/agent/net/AgentSetMemberConfig?ctoken=<%=ctoken%>" method=post>
<input type="hidden" value="<%=ctoken%>" name="ctoken">
<table align=center border="1">



    <tr>

    </tr>
    <% String style = "td1";
        for (int pos = 1; pos <= records; pos++)
        {
            String id = Integer.toString(pos);
            int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

            if (pos % 2 == 0)
                style = "td2";
            else
                style = "td1";
            temphash = null;
            temphash = (Hashtable) hash.get(id);

            String memberId = (String) temphash.get("memberid");
            String companyName = (String) temphash.get("company_name");
            String accountId = Functions.checkStringNull((String) temphash.get("accountid"));

            String reserves = "N/A";
            String aptprompt = "N/A";
            String chargeper = "N/A";
            String fixamount = "N/A";
            String reversalCharge = "N/A";
            String withdrawalCharge = "N/A";
            String chargebackCharge = "N/A";
            String taxper = "N/A";
            String reserveReason = "N/A";
            String isReadOnly = "readonly";
            String inactive="disabled=\"disabled\"";
            /*String card_transaction_limit="";
           String card_check_limit*/
            String daily_card_amount_limit="N/A";
            String weekly_card_amount_limit="N/A";
            String monthly_card_amount_limit="N/A";
            String daily_amount_limit="100.00",monthly_amount_limit="100.00",daily_card_limit="5",weekly_card_limit="10",monthly_card_limit="20";
            if (accountId != null)
            {
                isReadOnly = "";
                inactive="";
                BigDecimal tmpObj = new BigDecimal("0.01");
                reserves = ((new BigDecimal((String) temphash.get("reserves")).multiply(tmpObj))).toString();
                aptprompt = ((new BigDecimal((String) temphash.get("aptprompt")).multiply(tmpObj))).toString();
                chargeper = ((new BigDecimal((String) temphash.get("chargeper")).multiply(tmpObj))).toString();
                taxper = ((new BigDecimal((String) temphash.get("taxper"))).multiply(tmpObj)).toString();
                fixamount = (new BigDecimal((String) temphash.get("fixamount"))).toString();
                reversalCharge = (new BigDecimal((String) temphash.get("reversalcharge"))).toString();
                withdrawalCharge = (new BigDecimal((String) temphash.get("withdrawalcharge"))).toString();
                chargebackCharge = (new BigDecimal((String) temphash.get("chargebackcharge"))).toString();
                reserveReason = (String) temphash.get("reserve_reason");
                daily_amount_limit=(new BigDecimal((String) temphash.get("daily_amount_limit"))).toString();
                monthly_amount_limit=(new BigDecimal((String) temphash.get("monthly_amount_limit"))).toString();
                daily_card_limit=(new BigDecimal((String) temphash.get("daily_card_limit"))).toString();
                weekly_card_limit=(new BigDecimal((String) temphash.get("weekly_card_limit"))).toString();
                monthly_card_limit=(new BigDecimal((String) temphash.get("monthly_card_limit"))).toString();
                daily_card_amount_limit=(new BigDecimal((String) temphash.get("daily_card_amount_limit"))).toString();
                weekly_card_amount_limit=(new BigDecimal((String) temphash.get("weekly_card_amount_limit"))).toString();
                monthly_card_amount_limit=(new BigDecimal((String) temphash.get("monthly_card_amount_limit"))).toString();
                if (reserveReason == null) reserveReason = "";
            }
    %>
    <tr>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >Sr. No. </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >Member Id  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >Company Name  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >Merchant ID  </td>
    </tr>
    <tr>
        <td class="<%=style%>"><%=srno%></td>
        <td class="<%=style%>"><%=memberId%><input type=hidden name="memberid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberId)%>"></td>
        <td class="<%=style%>"><%=companyName%></td>
        <%
            Hashtable merchantDetailsHash = (Hashtable) request.getAttribute("accoutIDwiseMerchantHash");
            Enumeration enumr = merchantDetailsHash.keys();
            if ("".equals(accountId) || accountId == null)
            {
        %>
        <td class="<%=style%>">
            <select name=accountids>
                <option value="">Select Merchant</option>
                <%
                    //for (Map.Entry sa : merchantDetailsHash.entrySet() )
                    while (enumr.hasMoreElements())
                    {
                        String accid = (String) enumr.nextElement();
                %>
                <option value="<%= accid%>"><%= merchantDetailsHash.get(accid) %></option>
                <%}%>
            </select></td></tr>
    <%
    }
    else
    {
    %>
    <td class="<%=style%>"><input type="text" size=30 name="merchantid"
                                  value="<%=ESAPI.encoder().encodeForHTMLAttribute((String) merchantDetailsHash.get(accountId))%>" readonly="true"></td>
    <input type="hidden" name="accountids" value="<%=accountId%>">

    </tr>
    <%}%>
</table><BR>
<table align=center border="1">
    <tr><td colspan="8" bgcolor="#2379A5"><center><font color="#FFFFFF" size="2"><b>Member Charges</b></font></center></td></tr>
    <tr>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Reserve   </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Reversal Charge  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Reason for Reserve  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Fix charge amount  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Withdrawal Charge  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Chargeback Charge  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Charge percentage(in %)  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Tax Percentage(in %)  </td>
    </tr>
    <tr>
        <td class="<%=style%>"><input type=text align="center" size=10 name='reserves' value="<%=reserves%>" <%=isReadOnly%> ></td>
        <td class="<%=style%>"><input type=text size=10 name='reversalcharge' value="<%=ESAPI.encoder().encodeForHTMLAttribute(reversalCharge)%>" <%=isReadOnly%>></td>
        <td class="<%=style%>"><input type=text size=25 name='reserve_reason' value="<%=ESAPI.encoder().encodeForHTMLAttribute(reserveReason)%>" <%=isReadOnly%>>
        <td class="<%=style%>"><input type=text size=10 name='fixamount' value="<%=ESAPI.encoder().encodeForHTMLAttribute(fixamount)%>" <%=isReadOnly%>></td>
        <td class="<%=style%>"><input type=text size=10 name='withdrawalcharge' value="<%=ESAPI.encoder().encodeForHTMLAttribute(withdrawalCharge)%>" <%=isReadOnly%>></td>
        <td class="<%=style%>"><input type=text size=10 name='chargebackcharge' value="<%=ESAPI.encoder().encodeForHTMLAttribute(chargebackCharge)%>" <%=isReadOnly%>></td>
        <td class="<%=style%>"><input type=text size=10 name='chargeper' value="<%=ESAPI.encoder().encodeForHTMLAttribute(chargeper)%>" <%=isReadOnly%>></td>
        <td class="<%=style%>"><input type=text size=10 name='taxper' value="<%=ESAPI.encoder().encodeForHTMLAttribute(taxper)%>" <%=isReadOnly%>></td>
    </tr> <tr><td>&nbsp;</td></tr>
    <%--    </table>

   <table align=center>--%>
    <tr><td>&nbsp;</td><td colspan="6" bgcolor="#2379A5"><center><font color="#FFFFFF" size="2"><b>Member Limits</b></font></center></td></tr>
    <tr>
        <td>&nbsp;</td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Daily Amount Limit  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Monthly Amount Limit  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Daily Card Limit  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Weekly Card Limit  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Monthly Card Limit  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  High Risk Amount  </td>
        <td>&nbsp;</td>
    </tr>
    <tr><td>&nbsp;</td>
        <td class="<%=style%>"><input type=text size=10 name='daily_amount_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_amount_limit"))%>" <%=isReadOnly%>></td>
        <td class="<%=style%>"><input type=text size=10 name='monthly_amount_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_amount_limit"))%>" <%=isReadOnly%>></td>
        <td class="<%=style%>"><input type=text size=10 name='daily_card_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_card_limit"))%>" <%=isReadOnly%>></td>
        <td class="<%=style%>"><input type=text size=10 name='weekly_card_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_card_limit"))%>" <%=isReadOnly%>></td>
        <td class="<%=style%>"><input type=text size=10 name='monthly_card_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_card_limit"))%>" <%=isReadOnly%>></td>
        <td class="<%=style%>"><input type=text size=10 name='aptprompt' value="<%=ESAPI.encoder().encodeForHTMLAttribute(aptprompt)%>" <%=isReadOnly%>></td><td>&nbsp;</td>
    </tr>   <tr><td>&nbsp;</td></tr>

    <tr><td>&nbsp;</td><td colspan="3" bgcolor="#2379A5"><center><font color="#FFFFFF" size="2"></font></center></td></tr>
    <tr>
        <td>&nbsp;</td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Daily Card Amount Limit  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Weekly Card Amount Limit  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Monthly Card Amount Limit  </td>

        <td>&nbsp;</td>
    </tr>
    <tr><td>&nbsp;</td>
        <td class="<%=style%>"><input type=text size=10 name='daily_card_amount_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_card_amount_limit"))%>" <%=isReadOnly%>></td>
        <td class="<%=style%>"><input type=text size=10 name='weekly_card_amount_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_card_amount_limit"))%>" <%=isReadOnly%>></td>
        <td class="<%=style%>"><input type=text size=10 name='monthly_card_amount_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_card_amount_limit"))%>" <%=isReadOnly%>></td>

    </tr>   <tr><td>&nbsp;</td></tr>

    <%--    </table>

   <table align=center>--%>
    <tr><td colspan="8" bgcolor="#2379A5"><center><font color="#FFFFFF" size="2"><b>Member Configuration</b></font></center></td></tr>
    <tr>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  IS Pharma(Y/N)  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  IS Validate Email(Y/N)  </td>
        <%--<td valign="middle" align="center" bgcolor="#CCCCFF" rowspan=2>  IS Customer Email(Y/N)  </td>--%>
        <%--<td valign="middle" align="center" bgcolor="#CCCCFF" >  Customer Reminder Email(Y/N)  </td>--%>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Is Activation  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Is ICICI  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  HasPaid  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Is Service  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >   HR alertPROOF </td>
    </tr>
    <tr>
        <td class="<%=style%>"><select name='isPharma' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isPharma"))%>--%>
            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isPharma"))));  %></select></td>
        <td class="<%=style%>"><select name='isValidateEmail' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isValidateEmail"))%>--%>
            <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isValidateEmail"))));  %></select></td>

        <%--<td class="<%=style%>"><input type=text size=10 name='isCustomerEmail' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isCustomerEmail"))%>" <%=isReadOnly%>></td>--%>
        <%--<td class="<%=style%>"><select name='custremindermail' <%=isReadOnly%>>&lt;%&ndash;<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("custremindermail"))%>&ndash;%&gt;
            <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("custremindermail"))));  %></select></td>--%>
        <td class="<%=style%>"><select name='activation' <%=isReadOnly%>> <%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("activation"))%>--%>
            <%
                if(temphash.get("activation").equals("T"))
                {
            %>      <option value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("activation"))%>" selected="selected"><%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("activation"))%></option>
            <option value="Y">Y</option>
            <%
            }
            else
            {
            %>  <option value="T">T</option>
            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("activation"))%>" selected="selected"><%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("activation"))%></option>
            <% }%>
        </select></td>
        <td class="<%=style%>"><select name='icici' <%=isReadOnly%>> <%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("icici"))%>--%>
            <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("icici"))));  %></select></td>
        <td class="<%=style%>"><select name='haspaid' <%=isReadOnly%>> <%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("haspaid"))%>--%>
            <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("haspaid"))));  %></select></td>
        <td class="<%=style%>"><select name='isservice' <%=isReadOnly%>> <%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isservice"))%>--%>
            <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isservice"))));  %></select></td>
        <td class="<%=style%>"><select name='hralertproof' <%=isReadOnly%>> <%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("hralertproof"))%>--%>
            <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("hralertproof"))));  %></select></td>
    </tr>
    <tr><td>&nbsp;</td></tr>
    <tr><td colspan="8" bgcolor="#2379A5"><center><font color="#FFFFFF" size="2"></font></center></td></tr>
    <tr>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Auto Redirect  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  VBV  </td>
        <%--<td valign="middle" align="center" bgcolor="#CCCCFF" rowspan=2>  IS Customer Email(Y/N)  </td>--%>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  HR Parameterized  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Check Limit  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  MasterCardSupported  </td>

        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Invoice Merchant Details  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  Is Powered By Logo  </td>
    </tr>
    <tr>
        <td class="<%=style%>"><select name='autoredirect' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("autoredirect"))%>--%>
            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("autoredirect"))));  %></select></td>
        <td class="<%=style%>"><select name='vbv' <%=isReadOnly%>> <%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vbv"))%>--%>
            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vbv"))));  %></select></td>
        <%--<td class="<%=style%>"><input type=text size=10 name='isCustomerEmail' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isCustomerEmail"))%>" <%=isReadOnly%>></td>--%>
        <td class="<%=style%>"><select name='hrparameterised' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("hrparameterised"))%>--%>
            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("hrparameterised"))));  %></select></td>
        <td class="<%=style%>"><select name='check_limit' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("check_limit"))%>--%>
            <%if(temphash.get("check_limit").equals("0")){  %>
            <option value="<%=temphash.get("check_limit")%>" selected="selected"><%=temphash.get("check_limit")%></option>
            <option value="1">1</option>
            <% }else{%>
            <option value="0">0</option>
            <option value="<%=temphash.get("check_limit")%>" selected="selected"><%=temphash.get("check_limit")%></option>
            <%}%>
        </select></td>
        <td class="<%=style%>"><select name='masterCardSupported' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("masterCardSupported"))%>--%>
            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("masterCardSupported"))));  %></select></td>

        <td class="<%=style%>"><select name='invoicetemplate' ><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("hrparameterised"))%>--%>
            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("invoicetemplate"))));  %></select></td>
        <td class="<%=style%>"><select name='isPoweredBy' ><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("hrparameterised"))%>--%>
            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isPoweredBy"))));  %></select></td>
    </tr>
    <tr><td>&nbsp;</td></tr>

    <tr><td colspan="8" bgcolor="#2379A5"><center><font color="#FFFFFF" size="2"></font></center></td></tr>
    <tr>
        <td valign="middle" align="center" bgcolor="#CCCCFF" > Template  </td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" > Is Whitelisted</td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" > Card Check Limit</td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" > Card Transaction Limit</td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  &nbsp;</td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  &nbsp;</td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  &nbsp;</td>
        <td valign="middle" align="center" bgcolor="#CCCCFF" >  &nbsp;</td>
    </tr>
    <tr>
        <td class="<%=style%>"><select name='template' ><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("autoredirect"))%>--%>
            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("template"))));  %></select></td>
        <td class="<%=style%>"><select name='iswhitelisted' >
            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("iswhitelisted"))));  %></select></td>
        <td class="<%=style%>"><select name='card_check_limit' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("check_limit"))%>--%>
            <%if(temphash.get("card_check_limit").equals("0")){  %>
            <option value="<%=temphash.get("card_check_limit")%>" selected="selected"><%=temphash.get("card_check_limit")%></option>
            <option value="1">1</option>
            <% }else{%>
            <option value="0">0</option>
            <option value="<%=temphash.get("card_check_limit")%>" selected="selected"><%=temphash.get("card_check_limit")%></option>
            <%}%></select></td>
        <td class="<%=style%>"><select name='card_transaction_limit' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("check_limit"))%>--%>
            <%if(temphash.get("card_transaction_limit").equals("0")){  %>
            <option value="<%=temphash.get("card_transaction_limit")%>" selected="selected"><%=temphash.get("card_transaction_limit")%></option>
            <option value="1">1</option>
            <% }else{%>
            <option value="0">0</option>
            <option value="<%=temphash.get("card_transaction_limit")%>" selected="selected"><%=temphash.get("card_transaction_limit")%></option>
            <%}%></select></td>
        <td class="<%=style%>">&nbsp;</td>
        <td class="<%=style%>">&nbsp;</td>
        <td class="<%=style%>">&nbsp;</td>
        <td class="<%=style%>">&nbsp;</td>
    </tr>

</table>

<%


        } //end for

        %>
<center><input type="submit" value="Save"></center>
</form>
<%
    }
    else
    {
        out.println(ShowConfirmation("Sorry", "No records found.<br><br>Either the Member is Not activated"));
    }
%>


</body>

<%
    }
    else
    {
        response.sendRedirect("/agent/logout.jsp");
        return;
    }
%>
</html>
<%!
    public static String nullToStr(String str)
    {
        if(str == null)
            return "";
        return str;
    }
    public static String getStatus(String str)
    {
        if(str.equals("Y"))
            return "Active";
        else if(str.equals("N"))
            return "Inactive";
        else if(str.equals("T"))
            return "Test";

        return str;
    }
    public int convertStringtoInt(String convertstr, int defaultval)
    {
        int val = defaultval;

        if (convertstr != null)
        {
            convertstr = convertstr.trim();

            if (!convertstr.equals(""))
            {
                try
                {
                    val = Integer.parseInt(convertstr);
                }
                catch (NumberFormatException nfe)
                {
                    val = defaultval;
                }
            }

        }
        return val;
    }
    public String ShowConfirmation(String msg, String stat)
    {
        String str = "";

        str = str + "<table width=\"400\" align=center border=1 cellpadding=2 cellspacing=0  bordercolorlight=#000000 bordercolordark=#FFFFFF>";
        str = str + "<tr>";
        str = str + "<td  bgcolor=\"#2379A5\" colspan=\"3\"><font color=\"#FFFFFF\" size=\"1\" face=\"Verdana, Arial\"><b>" + msg + "</b></font></td>";
        str = str + "</tr>";
        str = str + "<tr>";
        str = str + "<td align=center>";

        str = str + "<br><br><font face=\"verdana,arial\" size=\"1\">" + stat + "</font><br><br><br>";
        str = str + "</td></tr>";
        str = str + "</table>";
        return str;
    }
%>