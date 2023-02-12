<%@ page import="com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Partner" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>

<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 10/16/13
  Time: 3:37 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="top.jsp"%>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));%>
<html>
<head>
    <title><%=company%> Member Commercials and Limits</title>
</head>
<%
        ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

        if (partner.isLoggedInPartner(session))
        {
            Hashtable memberidDetails=partner.getPartnerMemberDetails((String)session.getAttribute("merchantid"));
            String memberid=nullToStr(request.getParameter("memberid"));
%>
<body>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading" >
            <%=company%> Member Commercials and Limits
            </div>

            <form name="form" method="post" action="/partner/net/PartnerMemberConfig?ctoken=<%=ctoken%>">
                <input type="hidden" value="<%=ctoken%>" name="ctoken">

                <table  align="center" width="75%" cellpadding="2" cellspacing="2" style="margin-left:9.5%;margin-right: 2.5% ">

                    <tr>
                        <td>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">

                            <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">


                                <tr><td colspan="4">&nbsp;</td></tr>
                                <tr>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb" >Member ID</td>
                                    <td width="3%" class="textb"></td>
                                    <td width="12%" class="textb">
                                        <select size="1" name="memberid" class="txtbox">

                                            <%
                                                Enumeration enu3 = memberidDetails.keys();
                                                String selected3 = "";
                                                String key3 = "";
                                                String value3 = "";
                                                while (enu3.hasMoreElements())
                                                {
                                                    key3 = (String) enu3.nextElement();
                                                    value3 = (String) memberidDetails.get(key3);
                                                    if (value3.equals(memberid))
                                                        selected3 = "selected";
                                                    else
                                                        selected3 = "";
                                            %>
                                            <option value="<%=value3%>" <%=selected3%>><%=value3%></option>
                                            <%
                                                }
                                            %>
                                        </select>
                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="11%" class="textb"></td>
                                    <td width="5%" class="textb"></td>
                                    <td width="12%" class="textb">

                                    </td>
                                    <td width="4%" class="textb">&nbsp;</td>
                                    <td width="8%" class="textb" ></td>
                                    <td width="3%" class="textb"></td>
                                    <td width="10%" class="textb">
                                        <button type="submit" value="Submit" name="B1" class="buttonform" >
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
<form action="/partner/net/PartnerSetMemberConfig?ctoken=<%=ctoken%>" method=post>
<div class="reporttable" style="margin-bottom: 9px">
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

<center><font class="textb">Total records: <%=totalrecords%></font></center>

<input type="hidden" value="<%=ctoken%>" name="ctoken">
<table align=center border="1" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">



    <tr>

    </tr>
    <% String style = "td1";
        for (int pos = 1; pos <= records; pos++)
        {
            String id = Integer.toString(pos);
            int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

            if (pos % 2 == 0)
                style = "tr0";
            else
                style = "tr0";
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
    <thead>
        <tr>
            <td  style="height: 30px"valign="middle" align="center" class="th0" > Sr. No. </td>
            <td  style="height: 30px"valign="middle" align="center" class="th0" >  Member Id  </td>
            <td  style="height: 30px"valign="middle" align="center" class="th0" >  Company Name  </td>
            <td  style="height: 30px"valign="middle" align="center" class="th0" >  Account ID  </td>
        </tr>
    </thead>
        <tr>
        <td align="center" class="<%=style%>"><%=srno%></td>
        <td align="center" class="<%=style%>"><%=memberId%><input type=hidden name="memberid" value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberId)%>"></td>
        <td align="center" class="<%=style%>"><%=companyName%></td>
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
    <td align="center" class="<%=style%>"><input type="text" size=30 name="merchantid"
                                  value="<%=ESAPI.encoder().encodeForHTMLAttribute((String) merchantDetailsHash.get(accountId))%>" readonly="true"></td>
    <input type="hidden" name="accountids" value="<%=accountId%>">

    </tr>
    <%}%>
</table>
</div>
<div class="reporttable" style="margin-bottom: 9px;">
<table align=center border="1" class="table table-striped table-bordered table-green dataTable" style="margin-bottom: 0px">
    <thead>
    <tr class="th0" align="center"><td colspan="8" align="center"><b>Member Charges</b></td></tr>
    </thead>
    <tr>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Reserve   </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Reversal Charge  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Reason for Reserve  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Fix charge amount  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Withdrawal Charge  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Chargeback Charge  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Charge percentage(in %)  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Tax Percentage(in %)  </td>
    </tr>
    <tr>
        <td align="center"  class="<%=style%>"><input type=text align="center" size=10 name='reserves' value="<%=reserves%>" <%=isReadOnly%> ></td>
        <td align="center"  class="<%=style%>"><input type=text size=10 name='reversalcharge' value="<%=ESAPI.encoder().encodeForHTMLAttribute(reversalCharge)%>" <%=isReadOnly%>></td>
        <td align="center"  class="<%=style%>"><input type=text size=25 name='reserve_reason' value="<%=ESAPI.encoder().encodeForHTMLAttribute(reserveReason)%>" <%=isReadOnly%>>
        <td align="center"   class="<%=style%>"><input type=text size=10 name='fixamount' value="<%=ESAPI.encoder().encodeForHTMLAttribute(fixamount)%>" <%=isReadOnly%>></td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='withdrawalcharge' value="<%=ESAPI.encoder().encodeForHTMLAttribute(withdrawalCharge)%>" <%=isReadOnly%>></td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='chargebackcharge' value="<%=ESAPI.encoder().encodeForHTMLAttribute(chargebackCharge)%>" <%=isReadOnly%>></td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='chargeper' value="<%=ESAPI.encoder().encodeForHTMLAttribute(chargeper)%>" <%=isReadOnly%>></td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='taxper' value="<%=ESAPI.encoder().encodeForHTMLAttribute(taxper)%>" <%=isReadOnly%>></td>
    </tr>
    </table>
</div>
    <%--    </table>

   <table align=center>--%>
<div class="reporttable" style="margin-bottom: 9px;">
    <table align=center border="1" class="table table-striped table-bordered table-green dataTable" style="margin-bottom: 0px">
    <tr class="th0"><td align="center" colspan="6"><center><b>Member Limits</b></center></td></tr>
    <tr>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Daily Amount Limit  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Monthly Amount Limit  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Daily Card Limit  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Weekly Card Limit  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Monthly Card Limit  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  High Risk Amount  </td>

    </tr>
    <tr>
        <td align="center" class="<%=style%>"><input type=text size=10 name='daily_amount_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_amount_limit"))%>" <%=isReadOnly%>></td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='monthly_amount_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_amount_limit"))%>" <%=isReadOnly%>></td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='daily_card_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_card_limit"))%>" <%=isReadOnly%>></td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='weekly_card_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_card_limit"))%>" <%=isReadOnly%>></td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='monthly_card_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_card_limit"))%>" <%=isReadOnly%>></td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='aptprompt' value="<%=ESAPI.encoder().encodeForHTMLAttribute(aptprompt)%>" <%=isReadOnly%>>
    </tr>
    <tr>

        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Daily Card Amount Limit  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Weekly Card Amount Limit  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Monthly Card Amount Limit  </td>


    </tr>
    <tr>
        <td align="center" class="<%=style%>"><input type=text size=10 name='daily_card_amount_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_card_amount_limit"))%>" <%=isReadOnly%>></td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='weekly_card_amount_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_card_amount_limit"))%>" <%=isReadOnly%>></td>
        <td align="center" class="<%=style%>"><input type=text size=10 name='monthly_card_amount_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_card_amount_limit"))%>" <%=isReadOnly%>></td>

    </tr>
</table>
    </div>

    <%--    </table>

   <table align=center>--%>
<div class="reporttable" style="margin-bottom: 9px;">
    <table align=center border="1" class="table table-striped table-bordered table-green dataTable" style="margin-bottom: 0px">
    <tr class="th0"><td align="center" colspan="8"><center><b>Member Configuration</b></center></td></tr>
    <tr>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  IS Pharma(Y/N)  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  IS Validate Email(Y/N)  </td>
        <%--<td  style="height: 30px"valign="middle" align="center" class="tr1" rowspan=2>  IS Customer Email(Y/N)  </td>--%>
        <%--<td  style="height: 30px"valign="middle" align="center" class="tr1" >  Customer Reminder Email(Y/N)  </td>--%>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Is Activation  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Is ICICI  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  HasPaid  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Is Service  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >   HR alertPROOF </td>
    </tr>
    <tr>
        <td align="center" class="<%=style%>"><select name='isPharma' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isPharma"))%>--%>
            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isPharma"))));  %></select></td>
        <td align="center" class="<%=style%>"><select name='isValidateEmail' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isValidateEmail"))%>--%>
            <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isValidateEmail"))));  %></select></td>

        <%--<td align="center" class="<%=style%>"><input type=text size=10 name='isCustomerEmail' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isCustomerEmail"))%>" <%=isReadOnly%>></td>--%>
        <%--<td align="center" class="<%=style%>"><select name='custremindermail' <%=isReadOnly%>>&lt;%&ndash;<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("custremindermail"))%>&ndash;%&gt;
            <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("custremindermail"))));  %></select></td>--%>
        <td align="center" class="<%=style%>"><select name='activation' <%=isReadOnly%>> <%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("activation"))%>--%>
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
        <td align="center" class="<%=style%>"><select name='icici' <%=isReadOnly%>> <%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("icici"))%>--%>
            <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("icici"))));  %></select></td>
        <td align="center" class="<%=style%>"><select name='haspaid' <%=isReadOnly%>> <%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("haspaid"))%>--%>
            <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("haspaid"))));  %></select></td>
        <td align="center" class="<%=style%>"><select name='isservice' <%=isReadOnly%>> <%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isservice"))%>--%>
            <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isservice"))));  %></select></td>
        <td align="center" class="<%=style%>"><select name='hralertproof' <%=isReadOnly%>> <%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("hralertproof"))%>--%>
            <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("hralertproof"))));  %></select></td>
    </tr>

    <tr>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Auto Redirect  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  VBV  </td>
        <%--<td  style="height: 30px"valign="middle" align="center" class="tr1" rowspan=2>  IS Customer Email(Y/N)  </td>--%>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  HR Parameterized  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Check Limit  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  MasterCardSupported  </td>

        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Invoice Merchant Details  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Is Powered By Logo  </td>
    </tr>
    <tr>
        <td align="center" class="<%=style%>"><select name='autoredirect' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("autoredirect"))%>--%>
            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("autoredirect"))));  %></select></td>
        <td align="center" class="<%=style%>"><select name='vbv' <%=isReadOnly%>> <%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vbv"))%>--%>
            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vbv"))));  %></select></td>
        <%--<td align="center" class="<%=style%>"><input type=text size=10 name='isCustomerEmail' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isCustomerEmail"))%>" <%=isReadOnly%>></td>--%>
        <td align="center" class="<%=style%>"><select name='hrparameterised' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("hrparameterised"))%>--%>
            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("hrparameterised"))));  %></select></td>
        <td align="center" class="<%=style%>"><select name='check_limit' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("check_limit"))%>--%>
            <%if(temphash.get("check_limit").equals("0")){  %>
            <option value="<%=temphash.get("check_limit")%>" selected="selected"><%=temphash.get("check_limit")%></option>
            <option value="1">1</option>
            <% }else{%>
            <option value="0">0</option>
            <option value="<%=temphash.get("check_limit")%>" selected="selected"><%=temphash.get("check_limit")%></option>
            <%}%>
        </select></td>
        <td align="center" class="<%=style%>"><select name='masterCardSupported' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("masterCardSupported"))%>--%>
            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("masterCardSupported"))));  %></select></td>

        <td align="center" class="<%=style%>"><select name='invoicetemplate' ><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("hrparameterised"))%>--%>
            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("invoicetemplate"))));  %></select></td>
        <td align="center" class="<%=style%>"><select name='isPoweredBy' ><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("hrparameterised"))%>--%>
            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isPoweredBy"))));  %></select></td>
    </tr>

    <tr>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" > Template  </td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" > Is Whitelisted</td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" > Card Check Limit</td>
        <td  style="height: 30px"valign="middle" align="center" class="tr1" > Card Transaction Limit</td>

    </tr>
    <tr>
        <td align="center" class="<%=style%>"><select name='template' ><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("autoredirect"))%>--%>
            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("template"))));  %></select></td>
        <td align="center" class="<%=style%>"><select name='iswhitelisted' >
            <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("iswhitelisted"))));  %></select></td>
        <td align="center" class="<%=style%>"><select name='card_check_limit' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("check_limit"))%>--%>
            <%if(temphash.get("card_check_limit").equals("0")){  %>
            <option value="<%=temphash.get("card_check_limit")%>" selected="selected"><%=temphash.get("card_check_limit")%></option>
            <option value="1">1</option>
            <% }else{%>
            <option value="0">0</option>
            <option value="<%=temphash.get("card_check_limit")%>" selected="selected"><%=temphash.get("card_check_limit")%></option>
            <%}%></select></td>
        <td align="center" class="<%=style%>"><select name='card_transaction_limit' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("check_limit"))%>--%>
            <%if(temphash.get("card_transaction_limit").equals("0")){  %>
            <option value="<%=temphash.get("card_transaction_limit")%>" selected="selected"><%=temphash.get("card_transaction_limit")%></option>
            <option value="1">1</option>
            <% }else{%>
            <option value="0">0</option>
            <option value="<%=temphash.get("card_transaction_limit")%>" selected="selected"><%=temphash.get("card_transaction_limit")%></option>
            <%}%></select></td>
        <td align="center" class="<%=style%>">&nbsp;</td>
        <td class="<%=style%>">&nbsp;</td>
        <td class="<%=style%>">&nbsp;</td>
        <td class="<%=style%>">&nbsp;</td>
    </tr>

</table>
    </div>

<%


        } //end for

        %>
<table align="center" >
    <tr>
        <td>
            <button type="submit" value="Save" class="buttonform" style="margin-left: 76%">
                Save
            </button>
        </td>
    </tr>
</table>
</form>
<%
    }
    else
    {
        out.println(Functions.NewShowConfirmation1("Sorry", "No records found.<br><br>Either the Member is Not activated"));
    }
%>


</body>
</div>
<%
    }
    else
    {
        response.sendRedirect("/partner/logout.jsp");
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
    public String NewShowConfirmation(String msg, String stat)
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
