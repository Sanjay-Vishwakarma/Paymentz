<%@ page import="com.directi.pg.Functions,com.directi.pg.Merchants,
                 org.owasp.esapi.ESAPI" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%@ include file="top.jsp" %>
<%@ page import="com.directi.pg.Logger"%>

<%!
    private static Logger log=new Logger("memberpreference.jsp");

%>

<html>
<head>

</head>
<title> Reserves and Amount per Transaction </title>

<body class="bodybackground">
<%
    PartnerFunctions partnerFunctions=new PartnerFunctions();
%>
<div class="row">
<div class="col-lg-12">
<div class="panel panel-default">
<div class="panel-heading">
    Merchant Details
</div>

<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (partner.isLoggedInPartner(session))
    {
        Hashtable memberidDetails=partner.getPartnerMemberDetails((String)session.getAttribute("merchantid"));
        String memberid=nullToStr(request.getParameter("memberid"));
%>
<form action="/partner/net/MemberDetails?ctoken=<%=ctoken%>" method="get" name="F1" onsubmit="">
<input type="hidden" value="<%=ctoken%>" name="ctoken">
    <br>
    <table  align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
        <tr>
            <td>
                <%
                    String errormsg1 = (String) request.getAttribute("error");
                    if (errormsg1 != null)
                    {
                        out.println("<center><font class=\"textb\"><b>"+errormsg1+"<br></b></font></center>");
                    }
                %>
                <%

                    String errormsg = (String)request.getAttribute("cbmessage");
                    if (errormsg == null)
                        errormsg = "";

                    out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" ><b>");

                    out.println(errormsg);

                    out.println("</b></font></td></tr></table>");


                %>
                <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                    <tr>
                        <td width="2%" class="textb">&nbsp;</td>
                        <td width="20%" class="textb" >Member Id</td>
                        <td width="0%" class="textb"></td>
                        <td width="22%" class="textb">
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
                            <%--<input name="memberid" size="10" class="txtbox">--%>

                        </td>

                        <td width="10%" class="textb">&nbsp;</td>
                        <td width="40%" class="textb" ></td>
                        <td width="5%" class="textb"></td>
                        <td width="50%" class="textb">
                            <button type="submit" class="buttonform" style="margin-left:40px; ">
                                <i class="fa fa-clock-o"></i>
                                &nbsp;&nbsp;Search
                            </button>
                        </td>

                    </tr>
                 </table>
            </td>
        </tr>
    </table>
</form>
</div>
</div>
</div>
<form action="/partner/net/SetReserves?ctoken=<%=ctoken%>" method=post>
<div class="reporttable" style="margin-bottom: 9px">
<%
    Hashtable hash = (Hashtable) request.getAttribute("memberdetails");
    Hashtable uhash = (Hashtable) request.getAttribute("uhash");
    Hashtable temphash = null;
    Hashtable hashtablepartner= (Hashtable)request.getAttribute("partners");
    Hashtable agent= (Hashtable)request.getAttribute("agents");
    int pageno =partnerFunctions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
    int pagerecords =partnerFunctions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
    Functions functions = new Functions();
    int records = 0;
    int totalrecords = 0;
    int records1 = 0;
    int records2 = 0;


    String str="";
    if((hash!=null && hash.size()>0) && (partner!=null && hashtablepartner.size()>0) && (agent!=null && agent.size()>0))
    {
        try{
        records = Integer.parseInt((String) hash.get("records"));


        totalrecords = Integer.parseInt((String) hash.get("totalrecords"));


        records1 = Integer.parseInt((String) hashtablepartner.get("records1"));


        records2 = Integer.parseInt((String) agent.get("records2"));
        }
        catch (Exception ex)
        {
            log.error("Records & TotalRecords is found null",ex);
        }

    }
        if (uhash != null && uhash.size() > 0)
        {

%>
<font class="info">Modification Successful</font>
<% }

    if (records > 0)
    {  str = str + "?SRecords=" + pagerecords;
        str = str + "&ctoken=" + ctoken;
        str =str + "&year=" + URLEncoder.encode((String) hash.get("year")) + "&month=" + URLEncoder.encode((String) hash.get("month"));
%>

<center><font class="textb">Total records: <%=totalrecords%></font></center>
<br>

<input type="hidden" value="<%=ctoken%>" name="ctoken">
<table align=center class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">

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
//        String DPODAmt = (String) temphash.get("dtstamp_podsentamount");
//        String TPODAmt = (String) temphash.get("timestamp_podsentamount");
//        String DSAmt = (String) temphash.get("dtstamp_settledamount");
//        String TSAmt = (String) temphash.get("timestamp_settledamount");
//        String DRAmt = (String) temphash.get("dtstamp_reversedamount");
//        String TRAmt = (String) temphash.get("timestamp_reversedamount");
//        String DCAmt = (String) temphash.get("dtstamp_chargebackamount");
//        String TCAmt = (String) temphash.get("timestamp_chargebackamount");
        String accountId = Functions.checkStringNull((String) temphash.get("accountid"));

        //String reserves = "N/A";
        String aptprompt = "0.00";
//        String chargeper = "N/A";
//        String fixamount = "N/A";
//        String reversalCharge = "N/A";
//        String withdrawalCharge = "N/A";
//        String chargebackCharge = "N/A";
//        String taxper = "N/A";
        //String reserveReason = "N/A";

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
//            reserves = ((new BigDecimal((String) temphash.get("reserves")).multiply(tmpObj))).toString();
            aptprompt = ((new BigDecimal((String) temphash.get("aptprompt")).multiply(tmpObj))).toString();
//            chargeper = ((new BigDecimal((String) temphash.get("chargeper")).multiply(tmpObj))).toString();
//            taxper = ((new BigDecimal((String) temphash.get("taxper"))).multiply(tmpObj)).toString();
//            fixamount = (new BigDecimal((String) temphash.get("fixamount"))).toString();
//            reversalCharge = (new BigDecimal((String) temphash.get("reversalcharge"))).toString();
//            withdrawalCharge = (new BigDecimal((String) temphash.get("withdrawalcharge"))).toString();
//            chargebackCharge = (new BigDecimal((String) temphash.get("chargebackcharge"))).toString();
//            reserveReason = (String) temphash.get("reserve_reason");
            daily_amount_limit=(new BigDecimal((String) temphash.get("daily_amount_limit"))).toString();
            monthly_amount_limit=(new BigDecimal((String) temphash.get("monthly_amount_limit"))).toString();
            daily_card_limit=(new BigDecimal((String) temphash.get("daily_card_limit"))).toString();
            weekly_card_limit=(new BigDecimal((String) temphash.get("weekly_card_limit"))).toString();
            monthly_card_limit=(new BigDecimal((String) temphash.get("monthly_card_limit"))).toString();
            daily_card_amount_limit=(new BigDecimal((String) temphash.get("daily_card_amount_limit"))).toString();
            weekly_card_amount_limit=(new BigDecimal((String) temphash.get("weekly_card_amount_limit"))).toString();
            monthly_card_amount_limit=(new BigDecimal((String) temphash.get("monthly_card_amount_limit"))).toString();



  //          if (reserveReason == null)
  // reserveReason = "";
        }

//        if (DPODAmt == null) DPODAmt = "-";
//        if (TPODAmt == null) TPODAmt = "-";
//        if (DSAmt == null) DSAmt = "-";
//        if (TSAmt == null) TSAmt = "-";
//        if (DRAmt == null) DRAmt = "-";
//        if (TRAmt == null) TRAmt = "-";
//        if (DCAmt == null) DCAmt = "-";
//        if (TCAmt == null) TCAmt = "-";
%>
    <thead>
    <tr>
        <td valign="middle" align="center" class="th0" > Sr. No. </td>
        <td valign="middle" align="center" class="th0" >  Member Id  </td>
        <td valign="middle" align="center" class="th0" >  Company Name  </td>
        <td valign="middle" align="center" class="th0" >  Account ID  </td>
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
        <td align="center" class="<%=style%>">
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
        <td align="center" class="<%=style%>"><input type="text" size=60 name="merchantid" class="txtbox" style="width:300px"
                                      value="<%=ESAPI.encoder().encodeForHTMLAttribute((String) merchantDetailsHash.get(accountId))%>" readonly="true"></td>
        <input type="hidden" name="accountids" value="<%=accountId%>">

    </tr>
    <%}%>
</table>

</div>


<div class="reporttable" style="margin-bottom: 9px;">

    <table  border="0" width="100%" class="table table-striped table-bordered table-green dataTable" style="margin-bottom: 0px">
    <thead>
    <tr class="th0"><td colspan="6"><center><b>Member Limits</b></center></td></tr></thead>
        <thead>
            <tr>

                <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Daily Amount Limit  </td>
                <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Monthly Amount Limit  </td>
                <td  style="height: 30px"valign="middle"  align="center"  class="tr1">  Daily Card Limit  </td>
                <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Weekly Card Limit  </td>
                <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Monthly Card Limit  </td>
                <td  style="height: 30px"valign="middle" align="center" class="tr1" >  High Risk Amount  </td>

            </tr>
        </thead>
        <tr>

            <td align="center" class="<%=style%>"><input type=text size=10 name='daily_amount_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_amount_limit"))%>" <%=isReadOnly%>></td>
            <td align="center" class="<%=style%>"><input type=text size=10 name='monthly_amount_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_amount_limit"))%>" <%=isReadOnly%>></td>
            <td align="center" class="<%=style%>"><input type=text size=10 name='daily_card_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_card_limit"))%>" <%=isReadOnly%>></td>
            <td align="center" class="<%=style%>"><input type=text size=10 name='weekly_card_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_card_limit"))%>" <%=isReadOnly%>></td>
            <td align="center" class="<%=style%>"><input type=text size=10 name='monthly_card_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_card_limit"))%>" <%=isReadOnly%>></td>
            <td align="center" class="<%=style%>"><input type=text size=10 name='aptprompt' value="<%=ESAPI.encoder().encodeForHTMLAttribute(aptprompt)%>" <%=isReadOnly%>></td>

        </tr>
    <thead>
        <tr>

            <td  valign="middle" align="center" class="tr1" >  Daily Card Amount Limit  </td>
            <td valign="middle" align="center" class="tr1" >  Weekly Card Amount Limit  </td>
            <td  valign="middle" align="center" class="tr1" >  Monthly Card Amount Limit  </td>
            <td valign="middle" align="center" class="tr1" > Card Check Limit</td>
            <td  valign="middle" align="center" class="tr1" > Card Transaction Limit</td>
            <td  valign="middle" align="center" class="tr1" > Check Limit  </td>

        </tr>
    </thead>
        <tr>
            <td align="center" class="<%=style%>"><input type=text size=10 name='daily_card_amount_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_card_amount_limit"))%>" <%=isReadOnly%>></td>
            <td align="center" class="<%=style%>"><input type=text size=10 name='weekly_card_amount_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_card_amount_limit"))%>" <%=isReadOnly%>></td>
            <td align="center" class="<%=style%>"><input type=text size=10 name='monthly_card_amount_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_card_amount_limit"))%>" <%=isReadOnly%>></td>
            <td align="center" class="<%=style%>"><select name='card_check_limit' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("check_limit"))%>--%>
                <%if(temphash.get("card_check_limit").equals("0")){  %>
                <option value="<%=temphash.get("card_check_limit")%>" selected="selected"><%=temphash.get("card_check_limit")%></option>
                <option value="1">1</option>
                <% }else{%>
                <option value="0">0</option>
                <option value="<%=temphash.get("card_check_limit")%>" selected="selected"><%=temphash.get("card_check_limit")%></option>
                <%}%></select>
            </td>
            <td align="center" class="<%=style%>"><select name='card_transaction_limit' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("check_limit"))%>--%>
                <%if(temphash.get("card_transaction_limit").equals("0")){  %>
                <option value="<%=temphash.get("card_transaction_limit")%>" selected="selected"><%=temphash.get("card_transaction_limit")%></option>
                <option value="1">1</option>
                <% }else{%>
                <option value="0">0</option>
                <option value="<%=temphash.get("card_transaction_limit")%>" selected="selected"><%=temphash.get("card_transaction_limit")%></option>
                <%}%></select>
            </td>
            <td align="center" class="<%=style%>"><select name='check_limit' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("check_limit"))%>--%>
                <%if(temphash.get("check_limit").equals("0")){  %>
                <option value="<%=temphash.get("check_limit")%>" selected="selected"><%=temphash.get("check_limit")%></option>
                <option value="1">1</option>
                <% }else{%>
                <option value="0">0</option>
                <option value="<%=temphash.get("check_limit")%>" selected="selected"><%=temphash.get("check_limit")%></option>
                <%}%></select>
            </td>
        </tr>
        <thead>
        <tr>

            <td  valign="middle" align="center" class="tr1" > Weekly Amount Limit   </td>

        </tr>
        </thead>
        <tr>
            <td align="center" class="<%=style%>"><input type=text size=10 name='weekly_amount_limit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_amount_limit"))%>" <%=isReadOnly%>></td>
        </tr>
    </table>
    </div>
<%--    </table>

    <table align=center>--%>
<div class="reporttable" style="margin-bottom: 9px;">
<table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >


        <tr class="th0"><td colspan="6" style="height: 30px"><center><b>General Configuration</b></center></td></tr></thead>

        <tr>

            <td style="height: 30px"valign="middle" align="center" class="tr1" >  Is Activation  </td>
            <td style="height: 30px"valign="middle" align="center" class="tr1" >  Is MerchantInterfaceAccess</td>
            <td style="height: 30px"valign="middle" align="center" class="tr1">  Partner Id  </td>
            <td style="height: 30px"valign="middle" align="center"  class="tr1">  Agent Id  </td>


        </tr>

        <tr>

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
            </select>
            </td>
            <td align="center" class="<%=style%>"><select name='icici' <%=isReadOnly%>> <%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("icici"))%>--%>
                <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("icici"))));  %></select>
            </td>
            <td align="center" class="<%=style%>"><select name='partnerId' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("haspaid"))%>--%>

                <%  Hashtable inner=new Hashtable();
                    for(int i=1;i<=records1;i++)
                    {
                        String id1=Integer.toString(i);
                        inner=(Hashtable)hashtablepartner.get(id1);
                        String selected="";
                        if(temphash.get("partnerid").equals(inner.get("partnerId")))
                        {
                            selected="selected";
                        }
                        out.println("<option value="+inner.get("partnerId")+" "+selected+">"+inner.get("partnerName")+"</option>");
                    }%></select>
            </td>
            <td align="center" class="<%=style%>"><select name='agentId' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("haspaid"))%>--%>
                       <%  Hashtable inner1=new Hashtable();
                    for(int i=1;i<=records2;i++)
                    {
                        String id1=Integer.toString(i);
                        inner1=(Hashtable)agent.get(id1);
                        String selected="";
                        if(temphash.get("agentId").equals(inner1.get("agentId")))
                        {
                            selected="selected";
                        }
                        out.println("<option value="+inner1.get("agentId")+" "+selected+">"+inner1.get("agentName")+"</option>");
                    }%></select>
            </td>
        </tr>
   </table>
</div>
<%-- <div class="reporttable" style="margin-bottom: 9px;">
     <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">


        <tr class="th0"><td colspan="6" style="height: 30px"><center><b>Transaction Configuration</b></center></td></tr>

            <tr>

                <td style="height: 30px"valign="middle" align="center" class="tr1" >  Is Service  </td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >  Auto Redirect  </td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >  VBV  </td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >  MasterCardSupported  </td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >  Auto Select Terminal</td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >  Is POD Required </td>

            </tr>
        <tr>

            <td align="center" class="<%=style%>"><select name='isservice' <%=isReadOnly%>> &lt;%&ndash;<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isservice"))%>&ndash;%&gt;
                <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isservice"))));  %></select>
            </td>
            <td align="center" class="<%=style%>"><select name='autoredirect' <%=isReadOnly%>>&lt;%&ndash;<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("autoredirect"))%>&ndash;%&gt;
                <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("autoredirect"))));  %></select>
            </td>
            <td align="center" class="<%=style%>"><select name='vbv' <%=isReadOnly%>> &lt;%&ndash;<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vbv"))%>&ndash;%&gt;
                <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vbv"))));  %></select>
            </td>
            <td align="center" class="<%=style%>"><select name='masterCardSupported' <%=isReadOnly%>>&lt;%&ndash;<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("masterCardSupported"))%>&ndash;%&gt;
                <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("masterCardSupported"))));  %></select>
            </td>
            <td align="center" class="<%=style%>"><select name='autoSelectTerminal' >
                <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("autoSelectTerminal"))));  %></select>
            </td>
            <td align="center" class="<%=style%>"><select name='isPODRequired' >
                <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isPODRequired"))));  %></select>
            </td>

        </tr>
     </table>
</div>
<div class="reporttable" style="margin-bottom: 9px;">
    <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >

        <tr class="th0"><td colspan="3" style="height: 30px"><center><b>Template Configuration</b></center></td></tr>

        <tr>

            <td style="height: 30px"valign="middle" align="center" class="tr1" > IS Pharma(Y/N)  </td>
            <td style="height: 30px"valign="middle" align="center" class="tr1" > Is Powered By Logo  </td>
            <td style="height: 30px"valign="middle" align="center" class="tr1" > Template  </td>
       </tr>

        <tr>

            <td align="center" class="<%=style%>"><select name='isPharma' <%=isReadOnly%>>&lt;%&ndash;<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isPharma"))%>&ndash;%&gt;
                <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isPharma"))));  %></select>
            </td>
            <td align="center" class="<%=style%>"><select name='isPoweredBy' >&lt;%&ndash;<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("hrparameterised"))%>&ndash;%&gt;
                <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isPoweredBy"))));  %></select>
            </td>
            <td align="center" class="<%=style%>"><select name='template' >&lt;%&ndash;<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("autoredirect"))%>&ndash;%&gt;
                <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("template"))));  %></select>
            </td>


        </tr>
    </table>
</div>--%>


<%--<div class="reporttable" style="margin-bottom: 9px;">
    <table  border="0" width="100%" style="width: 50%" align="center" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
        <tr class="th0"><td colspan="2" style="height: 30px"><center><b>Whitelisting Configuration</b></center></td></tr>
        <tr>

            <td style="height: 30px"valign="middle" align="center" class="tr1" > Is Card Whitelisted</td>
            <td style="height: 30px"valign="middle" align="center" class="tr1" > Is Ip Whitelisted</td>

        &lt;%&ndash;<td valign="middle" align="center" bgcolor="#CCCCFF" rowspan=2>  IS Customer Email(Y/N)  </td>&ndash;%&gt;
        </tr>
        <tr>

            <td align="center" class="<%=style%>"><select name='iswhitelisted' >
                <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("iswhitelisted"))));  %></select>
            </td>
            <td align="center" class="<%=style%>"><select name='isipwhitelisted' >
                <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isIpWhitelisted"))));  %></select>
            </td>
            &lt;%&ndash;<td class="<%=style%>"><input type=text size=10 name='isCustomerEmail' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isCustomerEmail"))%>" <%=isReadOnly%>></td>&ndash;%&gt;


        </tr>
    </table>
</div>--%>
<%--<div class="reporttable" style="margin-bottom: 9px;">
    <table  border="0" width="100%" style="width: 50%" align="center" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >

        <tr class="th0"><td colspan="2" style="height: 30px"><center><b>HR Transaction Configuration</b></center></td></tr>

        <tr>

            <td style="height: 30px"valign="middle" align="center" class="tr1" >  HR alertPROOF </td>
            <td style="height: 30px"valign="middle" align="center" class="tr1" >  HR Parameterized  </td>


        </tr>


        <tr>

            <td align="center" class="<%=style%>"><select name='hralertproof' <%=isReadOnly%>> &lt;%&ndash;<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("hralertproof"))%>&ndash;%&gt;
                <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("hralertproof"))));  %></select>
            </td>
            <td align="center" class="<%=style%>"><select name='hrparameterised' <%=isReadOnly%>>&lt;%&ndash;<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("hrparameterised"))%>&ndash;%&gt;
                <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("hrparameterised"))));  %></select>
            </td>


        </tr>
     </table>
</div>--%>
<div class="reporttable" style="margin-bottom: 9px;">
    <table  border="0" width="100%" style="width: 50%" align="center" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >

    <tr class="th0"><td colspan="2" style="height: 30px"><center><b>Refund Configuration</b></center></td></tr>
    <tr>

            <td style="height: 30px"valign="middle" align="center" class="tr1" > Is Refund</td>
            <td style="height: 30px"valign="middle" align="center" class="tr1" > Daily Refund Limit</td>


        </tr>
        <tr>

            <td align="center" class="<%=style%>"><select name='isrefund' <%=isReadOnly%>> <%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("hralertproof"))%>--%>
                <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isrefund"))));  %></select>
            </td>
            <td align="center" class="<%=style%>"><input type=text size=10 name='refunddailylimit' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("refunddailylimit"))%>" <%=isReadOnly%>></td>
        </tr>
    </table>
</div>
<div class="reporttable" style="margin-bottom: 9px;">
    <table  border="0" width="100%" style="width: 50%" align="center" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >

        <tr class="th0"><td colspan="2" style="height: 30px" ><center><b>Email Configuration</b></center></td></tr></thead>

        <tr>

            <td style="height: 30px"valign="middle" align="center" class="tr1" >  IS Validate Email(Y/N)  </td>
            <%--<td style="height: 30px"valign="middle" align="center" class="tr1" >  Customer Reminder Email(Y/N)  </td>--%>
        </tr>

        <tr>

            <td align="center" class="<%=style%>"><select name='isValidateEmail' <%=isReadOnly%>><%--<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isValidateEmail"))%>--%>
                <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isValidateEmail"))));  %></select>
            </td>

            <%--<td class="<%=style%>"><input type=text size=10 name='isCustomerEmail' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isCustomerEmail"))%>" <%=isReadOnly%>></td>--%>
            <%--<td align="center" class="<%=style%>"><select name='custremindermail' <%=isReadOnly%>>&lt;%&ndash;<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("custremindermail"))%>&ndash;%&gt;
                <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("custremindermail"))));  %></select>
            </td>--%>


        </tr>
    </table>
</div>
<%--<div class="reporttable" style="margin-bottom: 9px;">
    <table  border="0" width="100%" style="width: 50%" align="center" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >

        <tr class="th0"><td colspan="1" style="height: 30px"><center><b>Invoice Configuration</b></center></td></tr>

        <tr>

            <td style="height: 30px"valign="middle" align="center" class="tr1" >Invoice Merchant Details  </td>
        </tr>

        <tr>

            <td align="center" align="center" class="<%=style%>"><select name='invoicetemplate' >&lt;%&ndash;<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("hrparameterised"))%>&ndash;%&gt;
                <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("invoicetemplate"))));  %></select>
            </td>
        </tr>
    </table>
</div>--%>
<div class="reporttable" style="margin-bottom: 9px;">
    <table  border="0" width="100%" style="width: 50%" align="center" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
        <tr class="th0"><td colspan="2" style="height: 30px"><center>Fraud Configuration</center></td></tr>


        <tr>

            <td style="height: 30px"valign="middle" align="center" class="tr1" >Max Score Allowed</td>
            <td style="height: 30px"valign="middle" align="center" class="tr1" >Max Score Reversal</td>


        </tr>

        <tr>

            <td align="center" class="<%=style%>"><input type=text size=10 name='maxscoreallowed' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("maxScoreAllowed"))%>" <%=isReadOnly%>></td>
            <td align="center" class="<%=style%>"><input type=text size=10 name='maxscoreautoreversal' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("maxScoreAutoReversal"))%>" <%=isReadOnly%>></td>
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
<br>
<%
    int currentblock = 1;
    try
    {
        currentblock = Integer.parseInt(request.getParameter("currentblock"));
    }
    catch (Exception ex)
    {
        currentblock = 1;
    }

%>
<%--<table align=center valign=top style="margin-left: 49%"><tr>
    <td align=center>
        <jsp:include page="page.jsp" flush="true">
            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
            <jsp:param name="numrows" value="<%=pagerecords%>"/>
            <jsp:param name="pageno" value="<%=pageno%>"/>
            <jsp:param name="str" value="<%=ESAPI.encoder().encodeForURL(str)%>"/>
            <jsp:param name="page" value="MemberDetails"/>
            <jsp:param name="currentblock" value="<%=currentblock%>"/>
            <jsp:param name="orderby" value=""/>
        </jsp:include>
    </td>
</tr>
</table>--%>

 <%
     }
    else
    {
        out.println(Functions.NewShowConfirmation1("Sorry", "No records found."));
        out.println("</div>");
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
<%!
    public static String nullToStr(String str)
    {
        if(str == null)
            return "";
        return str;
    }
%>