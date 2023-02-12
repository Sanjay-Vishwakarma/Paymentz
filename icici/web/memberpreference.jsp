<%@ page import="com.directi.pg.Functions,com.directi.pg.Logger,com.directi.pg.Merchants" %>
<%@ page import="com.manager.dao.MerchantDAO" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="functions.jsp" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.LinkedHashMap" %>
<%@ include file="index.jsp" %>
<%!
    private static Logger log=new Logger("memberpreference.jsp");
%>

<script type="text/javascript" src="/icici/javascript/jquery.min.js?ver=1"></script>
<script src="/icici/css/jquery-ui.min.js"></script>
<script src="/icici/javascript/autocomplete.js"></script>

<script>
    function CHECK(Value){
        if(Value.checked){
            return "Y";
        }
        else{
            return "N";
        }
    }
    var lablevalues = new Array();
    function ChangeFunction(Value , lable){
        var finalvalue=lable+"="+Value;
        lablevalues.push(finalvalue);
        document.getElementById("onchangedvalue").value = lablevalues;
    }

    function change(dropdown,input){
        var val = dropdown.options[dropdown.selectedIndex].value;
        if(val.trim()==='N'){

            document.getElementById(input).disabled = true;

        }else{
            document.getElementById(input).disabled = false;
        }
    }

    function field(dropdown,input) {
        if(dropdown ==='N'){
            document.getElementById(input).disabled = true;
        }
        else{
            document.getElementById(input).disabled = false;
        }
    }

    function validateForm()
    {
        var check = document.getElementsByClassName('txtbox2')
        var field = document.getElementsByClassName('txtbox1')
        var i=0;
        var j=0;

        for(i;i < field.length; i++){
            for(j; j < check.length; j++)
            {
                if(check[j].value === 'Y' && field[i].value === "")
                {
                    alert('Please enter value in '+ field[i].name)
                    return false;
                }
                j++;
                break;
            }
        }
    }
</script>
<html>
<head>
</head>
<title> Merchants management > Merchant's Configuration </title>
<body class="bodybackground">
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default">
            <div class="panel-heading">
                Merchant Configuration
            </div>
            <% ctoken               = ((User) session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
                Merchants merchants = new Merchants();
                if (merchants.isLoggedIn(session))
                {
                    MerchantDAO merchantDAO                     = new MerchantDAO();
                    LinkedHashMap<Integer,String> memberMap     = merchantDAO.listAllMember();
                    String memberId1                            = Functions.checkStringNull(request.getParameter("memberid"))==null?"":request.getParameter("memberid");
            %>
            <form action="/icici/servlet/MemberDetails?ctoken=<%=ctoken%>" method="get" name="F1" onsubmit="">
                <input type="hidden" value="<%=ctoken%>" name="ctoken" id="ctoken">
                <br>
                <table align="center" width="80%" cellpadding="2" cellspacing="2" style="margin-left:60px; ">
                    <tr>
                        <td>
                            <%
                                String errormsg1 = (String) request.getAttribute("error");
                                if (errormsg1 != null)
                                {
                                    out.println("<center><font class=\"textb\"><b>" + errormsg1 + "<br></b></font></center>");
                                }
                                String errormsg = (String) request.getAttribute("cbmessage");
                                if (errormsg == null)
                                    errormsg = "";
                                out.println("<table align=\"center\"><tr><td valign=\"middle\"><font class=\"textb\" ><b>");
                                out.println(errormsg);
                                out.println("</b></font></td></tr></table>");
                            %>
                            <input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
                            <table border="0" cellpadding="5" cellspacing="0" width="100%" align="center">
                                <tr>
                                    <td width="2%" class="textb">&nbsp;</td>
                                    <td width="20%" class="textb" for="mid">Member Id</td>
                                    <td width="0%" class="textb"></td>
                                    <td width="22%" class="textb">
                                        <input name="memberid" id="mid" value="<%=memberId1%>" class="txtbox" autocomplete="on" >
                                    </td>
                                    <td width="10%" class="textb">&nbsp;</td>
                                    <td width="40%" class="textb"></td>
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
<form action="/icici/servlet/SetReserves?ctoken=<%=ctoken%>" method=post name="F2" onsubmit="return validateForm()">
    <input type="hidden" value="" name="onchangedvalue" id="onchangedvalue">   <%--***do not remove the field*****--%>
    <div class="reporttable" style="margin-bottom: 9px">
        <%
            Hashtable hash      = (Hashtable) request.getAttribute("memberdetails");
            Hashtable uhash     = (Hashtable) request.getAttribute("uhash");
            Hashtable temphash  = null;
            Hashtable partner   = (Hashtable) request.getAttribute("partners");
            Hashtable agent     = (Hashtable) request.getAttribute("agents");
            int pageno          = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
            int pagerecords     = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 15);
            int records         = 0;
            int totalrecords    = 0;
            int records1        = 0;
            int records2        = 0;
            String str          = "";
            String notificationUrl  = "";
            String termsUrl         = "";
            String privacyPolicyUrl = "";
            String success_url          = "";
            String failed_url           = "";
            String payout_success_url   = "";
            String payout_failed_url    = "";
            if ((hash != null && hash.size() > 0) && (partner != null && partner.size() > 0) && (agent != null && agent.size() > 0))
            {
                try
                {
                    records         = Integer.parseInt((String) hash.get("records"));
                    totalrecords    = Integer.parseInt((String) hash.get("totalrecords"));
                    records1        = Integer.parseInt((String) partner.get("records1"));
                    records2        = Integer.parseInt((String) agent.get("records2"));
                }
                catch (Exception ex)
                {
                    log.error("Records & TotalRecords is found null", ex);
                }
            }
            if (uhash != null && uhash.size() > 0)
            {
        %>
        <font class="info">Modification Successful</font>
        <% }
            if (records > 0)
            {
                str = str + "?SRecords=" + pagerecords;
                str = str + "&ctoken=" + ctoken;
                str = str + "&year=" + URLEncoder.encode((String) hash.get("year")) + "&month=" + URLEncoder.encode((String) hash.get("month"));
        %>
        <center><font class="textb">Total records: <%=totalrecords%>
        </font></center>
        <br>
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <table align=center class="table table-striped table-bordered  table-green dataTable"
               style="margin-bottom: 0px">
            <% String style = "td1";
                for (int pos = 1; pos <= records; pos++)
                {
                    String id   = Integer.toString(pos);
                    int srno    = Integer.parseInt(id) + ((pageno - 1) * pagerecords);
                    if (pos % 2 == 0)
                        style = "tr0";
                    else
                        style = "tr0";
                    temphash            = (Hashtable) hash.get(id);
                    String memberId     = (String) temphash.get("memberid");
                    String companyName  = (String) temphash.get("company_name");
                    String accountId    = Functions.checkStringNull((String) temphash.get("accountid"));
                    String aptprompt    = "0.00";
                    String isReadOnly   = "";
                    BigDecimal tmpObj   = new BigDecimal("0.01");
                    aptprompt           = ((new BigDecimal((String) temphash.get("aptprompt")).multiply(tmpObj))).toString();
                    Functions function  = new Functions();
                    if(function.isValueNull((String)temphash.get("notificationUrl"))){
                        notificationUrl = (String)temphash.get("notificationUrl");
                    }
                    if(function.isValueNull((String)temphash.get("termsUrl"))){
                        termsUrl    = (String)temphash.get("termsUrl");
                    }
                    if(function.isValueNull((String)temphash.get("privacyPolicyUrl"))){
                        privacyPolicyUrl    = (String)temphash.get("privacyPolicyUrl");
                    }

                    if(function.isValueNull(String.valueOf(temphash.get("success_url"))))
                        success_url = String.valueOf(temphash.get("success_url"));

                    if (function.isValueNull(String.valueOf(temphash.get("failed_url"))))
                        failed_url =  String.valueOf(temphash.get("failed_url"));

                    if (function.isValueNull(String.valueOf(temphash.get("payout_success_url"))))
                        payout_success_url = String.valueOf(temphash.get("payout_success_url"));

                    if (function.isValueNull(String.valueOf(temphash.get("payout_failed_url"))))
                        payout_failed_url = String.valueOf(temphash.get("payout_failed_url"));

            %>
            <thead>
            <tr>
                <td valign="middle" align="center" class="th0"> Sr. No.</td>
                <td valign="middle" align="center" class="th0"> Member Id</td>
                <td valign="middle" align="center" class="th0"> Company Name</td>
                <td valign="middle" align="center" class="th0"> Account ID</td>
            </tr>
            </thead>
            <tr>
                <td align="center" class="<%=style%>"><%=srno%>
                </td>
                <td align="center" class="<%=style%>"><%=memberId%><input type=hidden name="memberid"
                                                                          value="<%=ESAPI.encoder().encodeForHTMLAttribute(memberId)%>">
                </td>
                <td align="center" class="<%=style%>"><%=companyName%>
                </td>
                <%
                    Hashtable merchantDetailsHash   = (Hashtable) request.getAttribute("accoutIDwiseMerchantHash");
                    Enumeration enumr               = merchantDetailsHash.keys();
                    if ("".equals(accountId) || accountId == null)
                    {
                %>
                <td align="center" class="<%=style%>">
                    <select name=accountids>
                        <option value="">Select Merchant</option>
                        <%
                            while (enumr.hasMoreElements())
                            {
                                String accid = (String) enumr.nextElement();
                        %>
                        <option value="<%= accid%>"><%= merchantDetailsHash.get(accid) %>
                        </option>
                        <%}%>
                    </select></td>
            </tr>
            <%
            }
            else
            {
            %>
            <td align="center" class="<%=style%>"><input type="text" size=60 name="merchantid" class="txtbox"
                                                         style="width:300px"
                                                         value="<%=ESAPI.encoder().encodeForHTMLAttribute((String) merchantDetailsHash.get(accountId))%>"
                                                         readonly="true"></td>
            <input type="hidden" name="accountids" value="<%=accountId%>">
            </tr>
            <%}%>
        </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
        <table border="0" width="100%" class="table table-striped table-bordered table-green dataTable"
               style="margin-bottom: 0px">
            <thead>
            <tr class="th0">
                <td colspan="6">
                    <center><b>Member Limits</b></center>
                </td>
            </tr>
            </thead>
            <thead>
            <tr>
                <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Daily Amount Limit  </td>
                <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Weekly Amount Limit  </td>
                <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Monthly Amount Limit  </td>
                <td  style="height: 30px"valign="middle"  align="center"  class="tr1">  Daily Card Limit  </td>
                <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Weekly Card Limit  </td>
                <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Monthly Card Limit  </td>

            </tr>
            </thead>
            <tr>
                <td align="center" class="<%=style%>"><input type=text size=10 name='daily_amount_limit'
                                                             maxlength="9"  value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Daily Amount Limit')">
                </td>
                <td align="center" class="<%=style%>"><input type=text size=10 name='weekly_amount_limit'
                                                             maxlength="9" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Weekly Amount Limit')">
                </td>
                <td align="center" class="<%=style%>"><input type=text size=10 name='monthly_amount_limit'
                                                             maxlength="9" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Monthly Amount Limit')">
                </td>
                <td align="center" class="<%=style%>"><input type=text size=10 name='daily_card_limit'
                                                             value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_card_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Daily Card Limit')">
                    <select name='daily_card_limit_check' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Daily Card Limit Check')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("daily_card_limit_check"))));
                        %>
                    </select>
                </td>
                <td align="center" class="<%=style%>"><input type=text size=10 name='weekly_card_limit'
                                                             value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_card_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Weekly Card Limit')">
                    <select name='weekly_card_limit_check' <%=isReadOnly%> onchange="ChangeFunction(this.value,'weekly Card Limit check')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("weekly_card_limit_check"))));
                        %>
                    </select>

                </td>
                <td align="center" class="<%=style%>"><input type=text size=10 name='monthly_card_limit'
                                                             value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_card_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Monthly Card Limit')">
                    <select name='monthly_card_limit_check' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Monthly Card Limit Check')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("monthly_card_limit_check"))));
                        %>
                    </select>
                </td>

            </tr>
            <thead>
            <tr>
                <td valign="middle" align="center" class="tr1"> Daily Card Amount Limit</td>
                <td valign="middle" align="center" class="tr1"> Weekly Card Amount Limit</td>
                <td valign="middle" align="center" class="tr1"> Monthly Card Amount Limit</td>
                <td valign="middle" align="center" class="tr1"> Card Limit Check</td>
                <td valign="middle" align="center" class="tr1"> Card Amount Limit Check</td>
                <td valign="middle" align="center" class="tr1"> Amount Limit Check</td>
            </tr>
            </thead>
            <tr>
                <td align="center" class="<%=style%>"><input type=text size=10 name='daily_card_amount_limit'
                                                             maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_card_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Daily Card Amount Limit')">
                </td>
                <td align="center" class="<%=style%>"><input type=text size=10 name='weekly_card_amount_limit'
                                                             maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_card_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Weekly Card Amount Limit')">
                </td>
                <td align="center" class="<%=style%>"><input type=text size=10 name='monthly_card_amount_limit'
                                                             maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_card_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Monthly Card Amount Limit')">
                </td>
                <td align="center" class="<%=style%>"><select
                        name='card_check_limit' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Card Limit Check')">
                    <%
                        if (temphash.get("card_check_limit").equals("0"))
                        {
                    %>
                    <option value="<%=temphash.get("card_check_limit")%>"
                            selected="selected"><%=temphash.get("card_check_limit")%>
                    </option>
                    <option value="1">1</option>
                    <% }
                    else
                    {%>
                    <option value="0">0</option>
                    <option value="<%=temphash.get("card_check_limit")%>"
                            selected="selected"><%=temphash.get("card_check_limit")%>
                    </option>
                    <%}%></select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='card_transaction_limit' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Card Amount Limit Check')">
                    <%
                        if (temphash.get("card_transaction_limit").equals("0"))
                        {
                    %>
                    <option value="<%=temphash.get("card_transaction_limit")%>"
                            selected="selected"><%=temphash.get("card_transaction_limit")%>
                    </option>
                    <option value="1">1</option>
                    <% }
                    else
                    {%>
                    <option value="0">0</option>
                    <option value="<%=temphash.get("card_transaction_limit")%>"
                            selected="selected"><%=temphash.get("card_transaction_limit")%>
                    </option>
                    <%}%></select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='check_limit' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Amount Limit Check')">
                    <%
                        if (temphash.get("check_limit").equals("0"))
                        {
                    %>
                    <option value="<%=temphash.get("check_limit")%>"
                            selected="selected"><%=temphash.get("check_limit")%>
                    </option>
                    <option value="1">1</option>
                    <% }
                    else
                    {%>
                    <option value="0">0</option>
                    <option value="<%=temphash.get("check_limit")%>"
                            selected="selected"><%=temphash.get("check_limit")%>
                    </option>
                    <%}%></select>
                </td>
            </tr>
            <thead>
            <tr>
                <td valign="middle" align="center" class="tr1"> High Risk Amount</td>
                <td valign="middle" align="center" class="tr1"> Card Velocity Check</td>
                <td valign="middle" align="center" class="tr1"> Limit Routing</td>
                <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Daily Payout Amount Limit  </td>
                <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Weekly Payout Amount Limit  </td>
                <td  style="height: 30px"valign="middle" align="center" class="tr1" >  Monthly Payout Amount Limit  </td>
            </tr>
            </thead>
            <tr>
                <td align="center" class="<%=style%>"><input type=text size=10 name='aptprompt'
                                                             value="<%=ESAPI.encoder().encodeForHTMLAttribute(aptprompt)%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'High Risk Amount')">
                </td>
                <td align="center" class="<%=style%>">
                    <select name='card_velocity_check' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Card Velocity Check')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("card_velocity_check"))));
                        %>
                    </select>
                </td>
                <td align="center" class="<%=style%>">
                    <select name='limitRouting' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Limit Routing')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("limitRouting"))));
                        %>
                    </select>
                </td>
                <td align="center" class="<%=style%>"><input type=text size=10 name='daily_payout_amount_limit'
                                                             maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("daily_payout_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Daily Payout Amount Limit')">
                </td>
                <td align="center" class="<%=style%>"><input type=text size=10 name='weekly_payout_amount_limit'
                                                             maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("weekly_payout_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Weekly Payout Amount Limit')">
                </td>
                <td align="center" class="<%=style%>"><input type=text size=10 name='monthly_payout_amount_limit'
                                                             maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("monthly_payout_amount_limit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Monthly Payout Amount Limit')">
                </td>
            </tr>
            <thead>
            <tr>

                <td valign="middle" align="center" class="tr1"> Payout Amount Limit Check</td>
                <td valign="middle" align="center" class="tr1"> Payout Routing</td>

            </tr>
            </thead>
            <tr>
                <td align="center" class="<%=style%>">
                    <select name='payout_amount_limit_check' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Payout Amount Limit Check')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("payout_amount_limit_check"))));
                        %>
                </select>
                </td>
                <td align="center" class="<%=style%>">
                    <select name='payout_routing' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Payout Routing')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("payoutRouting"))));
                        %>
                </select>
                </td>
            </tr>
        </table>
    </div>

    <%-- flags for Transaction Limits --%>
    <div class="reporttable" style="margin-bottom: 9px;">
        <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
               style="margin-bottom: 0px">
            <thead>
            <tr class="th0">
                <td colspan="7" style="height: 30px">
                    <center><b>Transaction Limits</b></center>
                </td>
            </tr>
            </thead>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1">VPA Address Limit Check</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">VPA Address Daily Count</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">VPA Address Monthly Count</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">VPA Address Amount Limit Check</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">VPA Address Daily Amount Limit</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">VPA Address Monthly Amount Limit</td>
            </tr>

            <tr>
                <td align="center" class="<%=style%>">
                    <select name='vpaAddressLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'VPA Address Limit Check')">
                        <%
                            out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("vpaAddressLimitCheck"))));
                        %>
                    </select>
                </td>

                <td align="center" class="<%=style%>"><input type=text size=10 name='vpaAddressDailyCount'
                                                             value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressDailyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'VPA Address Daily Count')">
                </td>
                <td align="center" class="<%=style%>"><input type=text size=10 name='vpaAddressMonthlyCount'
                                                             value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressMonthlyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'VPA Address Monthly Count')">
                </td>

                <td align="center" class="<%=style%>">
                    <select name='vpaAddressAmountLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'VPA Address Amount Limit Check')">
                        <%
                            out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("vpaAddressAmountLimitCheck"))));
                        %>
                    </select>
                </td>
                <td align="center" class="<%=style%>"><input type=text size=10 name='vpaAddressDailyAmountLimit'
                                                             maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressDailyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'VPA Address Daily Amount Limit')">
                </td>
                <td align="center" class="<%=style%>"><input type=text size=10 name='vpaAddressMonthlyAmountLimit'
                                                             maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("vpaAddressMonthlyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'VPA Address Monthly Amount Limit')">
                </td>
            </tr>

            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Ip Count Limit Check</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Ip Daily Count</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Ip Monthly Count</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Ip Amount Limit Check</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Ip Daily Amount Limit </td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Ip Monthly Amount Limit </td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>">
                    <select name='customerIpLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Ip Count Limit Check')">
                        <%
                            out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpLimitCheck"))));
                        %>
                    </select>
                </td>
                <td align="center" class="<%=style%>"><input type="text" size="10" name='customerIpDailyCount'
                                                             value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpDailyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value, 'Customer Ip Daily Count')">
                </td>
                <td align="center" class="<%=style%>"><input type="text" size="10" name='customerIpMonthlyCount'
                                                             value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpMonthlyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value, 'Customer Ip Monthly Count')">
                </td>
                <td align="center" class="<%=style%>">
                    <select name='customerIpAmountLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Ip Amount Limit Check')">
                        <%
                            out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpAmountLimitCheck"))));
                        %>
                    </select>
                </td>
                <td align="center" class="<%=style%>"><input type="text" size="10" name='customerIpDailyAmountLimit'
                                                             maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpDailyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Ip Daily Amount Limit')">

                </td>
                <td align="center" class="<%=style%>"><input type="text" size="10" name='customerIpMonthlyAmountLimit'
                                                             maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerIpMonthlyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Ip Monthly Amount Limit')">

                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Name Count Limit Check</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Name Daily Count</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Name Monthly Count</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Name Amount Limit Check</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Name Daily Amount Limit</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Customer Name Monthly Amount Limit</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>">
                    <select name='customerNameLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Name Count Limit Check')">
                        <%
                            out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameLimitCheck"))));
                        %>
                    </select>
                </td>
                <td align="center" class="<%=style%>"><input type="text" size="10" name='customerNameDailyCount'
                                                             value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameDailyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Name Daily Count')">
                </td>
                <td align="center" class="<%=style%>"><input type="text" size="10" name='customerNameMonthlyCount'
                                                             value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameMonthlyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Name Daily Count')">
                </td>
                <td align="center" class="<%=style%>">
                    <select name='customerNameAmountLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Name Amount Limit Check')">
                        <%
                            out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameAmountLimitCheck"))));
                        %>
                    </select>
                </td>
                <td align="center" class="<%=style%>"><input type="text" size="10" name='customerNameDailyAmountLimit'
                                                             maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameDailyAmountLimit"))%>"  <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Name Daily Amount Limit')" >
                </td>
                <td align="center" class="<%=style%>"><input type="text" size="10" name='customerNameMonthlyAmountLimit'
                                                             maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerNameMonthlyAmountLimit"))%>"  <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Name Monthly Amount Limit')" >
                </td>
            </tr>
            <tr>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">Customer Email Count Limit Check</td>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">Customer Email Daily Count</td>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">Customer Email Monthly Count</td>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">Customer Email Amount Limit Check</td>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">Customer Email Daily Amount Limit</td>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">Customer Email Monthly Amount Limit</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>">
                    <select name='customerEmailLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Email Count Limit Check')">
                        <%
                            out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailLimitCheck"))));
                        %>
                    </select>
                </td>
                <td align="center" class="<%=style%>"><input type="text" size="10" name='customerEmailDailyCount'
                                                             maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailDailyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Email Daily Count')">

                </td>
                <td align="center" class="<%=style%>"><input type="text" size="10" name='customerEmailMonthlyCount'
                                                             maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailMonthlyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Email Monthly Count')">

                </td>
                <td align="center" class="<%=style%>">
                    <select name='customerEmailAmountLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Email Amount Limit Check')">
                        <%
                            out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailAmountLimitCheck"))));
                        %>
                    </select>
                </td>
                <td align="center" class="<%=style%>"><input type="text" size="10" name='customerEmailDailyAmountLimit'
                                                             value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailDailyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Email Daily Amount Limit')">
                </td>
                <td align="center" class="<%=style%>"><input type="text" size="10" name='customerEmailMonthlyAmountLimit'
                                                             value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerEmailMonthlyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Email Monthly Amount Limit')">
                </td>
            </tr>
            <tr>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">Customer Phone Count Limit Check</td>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">Customer Phone Daily Count</td>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">Customer Phone Monthly Count</td>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">Customer Phone Amount Limit Check</td>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">Customer Phone Daily Amount Limit</td>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">Customer Phone Monthly Amount Limit</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>">
                    <select name='customerPhoneLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Phone Count Limit Check')">
                        <%
                            out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneLimitCheck"))));
                        %>
                    </select>
                </td>
                <td align="center" class="<%=style%>"><input type="text" size="10" name='customerPhoneDailyCount'
                                                             maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneDailyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Phone Daily Count')">
                </td>
                <td align="center" class="<%=style%>"><input type="text" size="10" name='customerPhoneMonthlyCount'
                                                             maxlength="15" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneMonthlyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Phone Monthly Count')">
                </td>
                <td align="center" class="<%=style%>">
                    <select name='customerPhoneAmountLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Phone Amount Limit Check')">
                        <%
                            out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneAmountLimitCheck"))));
                        %>
                    </select>
                </td>
                <td align="center" class="<%=style%>"><input type="text" size="10" name='customerPhoneDailyAmountLimit'
                                                             value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneDailyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Phone Daily Amount Limit')" >

                </td>
                <td align="center" class="<%=style%>"><input type="text" size="10" name='customerPhoneMonthlyAmountLimit'
                                                             value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("customerPhoneMonthlyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Phone Daily Amount Limit')" >

                </td>
            </tr>
        </table>
    </div>

    <%--  flags for payout --%>
    <div class="reporttable" style="margin-bottom: 9px;">
        <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
               style="margin-bottom: 0px">
            <thead>
            <tr class="th0">
                <td colspan="7" style="height: 30px">
                    <center><b>Payout Limits</b></center>
                </td>
            </tr>
            </thead>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Payout Bank AccountNo Limit Check</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Bank AccountNo Daily Count</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Bank AccountNo Monthly Count</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Payout Bank AccountNo Amount Limit Check</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Bank AccountNo Daily Amount Limit</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Bank AccountNo Monthly Amount Limit</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Total Payout Amount</td>
            </tr>

            <tr>
                <td align="center" class="<%=style%>">
                    <select name='payoutBankAccountNoLimitCheck'  <%=isReadOnly%> onchange="ChangeFunction(this.value, 'Payout Bank AccountNo Limit Check')">
                        <%
                            out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("payoutBankAccountNoLimitCheck"))));
                        %>
                    </select>
                </td>
                <td align="center" class="<%=style%>">
                    <input type=text size=10 name='bankAccountNoDailyCount'
                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoDailyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Bank AccountNo Daily Count')">
                </td>
                <td align="center" class="<%=style%>">
                    <input type=text size=10 name='bankAccountNoMonthlyCount'
                           value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoMonthlyCount"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Bank AccountNo Monthly Count')">
                </td>
                <td align="center" class="<%=style%>">
                    <select name='payoutBankAccountNoAmountLimitCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Payout Bank AccountNo Amount Limit Check')">
                        <%
                            out.println(Functions.combovalSystem(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("payoutBankAccountNoAmountLimitCheck"))));
                        %>
                    </select>
                </td>
                <td align="center" class="<%=style%>">
                    <input type=text size=10 name='bankAccountNoDailyAmountLimit'
                           maxlength="15"  value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoDailyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Bank AccountNo Daily Amount Limit')">
                </td>
                <td align="center" class="<%=style%>">
                    <input type=text size=10 name='bankAccountNoMonthlyAmountLimit'
                           maxlength="15"  value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("bankAccountNoMonthlyAmountLimit"))%>" <%=isReadOnly%> onchange="ChangeFunction(this.value,'Bank AccountNo Monthly Amount Limit')">
                </td>

                <td align="center" class="<%=style%>">
                    <input type=number step=".01" name='totalPayoutAmount'
                            value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("totalPayoutAmount"))%>" onchange="ChangeFunction(this.value,'Total Payout Amount')">
                </td>
            </tr>
        </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
        <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
               style="margin-bottom: 0px">
            <tr class="th0">
                <td colspan="7" style="height: 30px">
                    <center><b>General Configuration</b></center>
                </td>
            </tr>
            </thead>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Is Activation</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> HasPaid</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Partner Id</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Agent Id</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select
                        name='activation' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Activation')">
                    <%
                        if (temphash.get("activation").equals("T"))
                        {
                    %>
                    <option value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("activation"))%>"
                            selected="selected"><%=ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("activation"))%>
                    </option>
                    <option value="Y">Y</option>
                    <option value="N">N</option>
                    <%
                    }
                    else if (temphash.get("activation").equals("N"))
                    {
                    %>
                    <option value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("activation"))%>"
                            selected="selected"><%=ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("activation"))%>
                    </option>
                    <option value="T">T</option>
                    <option value="Y">Y</option>
                    <%
                    }
                    else
                    {
                    %>
                    <option value="T">T</option>
                    <option value="N">N</option>
                    <option value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("activation"))%>"
                            selected="selected"><%=ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("activation"))%>
                    </option>
                    <% }%>
                </select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='haspaid' <%=isReadOnly%> onchange="ChangeFunction(this.value,'HasPaid')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("haspaid")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='partnerId' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Partner Id')">
                    <%
                        for (int i = 1; i <= records1; i++)
                        {
                            String id1 = Integer.toString(i);
                            Hashtable inner = (Hashtable) partner.get(id1);
                            String selected = "";
                            if (temphash.get("partnerid").equals(inner.get("partnerId")))
                            {
                                selected = "selected";
                            }
                            out.println("<option value=" + inner.get("partnerId") + " " + selected + ">" + inner.get("partnerName") + "</option>");
                        }%></select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='agentId' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Agent Id')">
                    <%
                        for (int i = 1; i <= records2; i++)
                        {
                            String id1 = Integer.toString(i);
                            Hashtable inner1 = (Hashtable) agent.get(id1);
                            String selected = "";
                            if (temphash.get("agentId").equals(inner1.get("agentId")))
                            {
                                selected = "selected";
                            }
                            out.println("<option value=" + inner1.get("agentId") + " " + selected + ">" + inner1.get("agentName") + "</option>");
                        }%></select>
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Is MerchantInterface Access</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Merchant Login with Otp</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Blacklist Transactions</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Flight Mode</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Is ExcessCaptureAllowed</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select
                        name='icici' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is MerchantInterface Access')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("icici")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='merchant_verify_otp' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Merchant Login with Otp')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchant_verify_otp")))); %></select>
                </td>
                <td align="center" class="<%=style%>">
                    <select name='blacklistTransaction' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Blacklist Transactions')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("blacklistTransaction")))); %></select>
                </td>
                <td align="center" class="<%=style%>">
                    <select name='flightMode' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Flight Mode')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("flightMode")))); %></select>
                </td>
                <td align="center" class="<%=style%>">
                    <select name='isExcessCaptureAllowed' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is ExcessCaptureAllowed')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isExcessCaptureAllowed")))); %>
                    </select>
                </td>
            </tr>
        </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
        <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
               style="margin-bottom: 0px">
            <tr class="th0">
                <td colspan="8" style="height: 30px">
                    <center><b>Transaction Configuration</b></center>
                </td>
            </tr>
            <tr>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >  Is Service  </td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >  Auto Redirect  </td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >  VBV  </td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >  MasterCardSupported  </td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >  Auto Select Terminal</td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >  Is POD Required </td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >  Is RestrictedTicket</td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >  EMI Support</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select
                        name='isservice' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Service')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isservice")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='autoredirect' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Auto Redirect')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("autoredirect")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='vbv' <%=isReadOnly%> onchange="ChangeFunction(this.value,'VBV')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("vbv")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='masterCardSupported' <%=isReadOnly%> onchange="ChangeFunction(this.value,'MasterCardSupported')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("masterCardSupported")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='autoSelectTerminal' onchange="ChangeFunction(this.value,'Auto Select Terminal')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("autoSelectTerminal")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='isPODRequired' onchange="ChangeFunction(this.value,'Is POD Required')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isPODRequired")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='isRestrictedTicket' onchange="ChangeFunction(this.value,'Is RestrictedTicket')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isRestrictedTicket")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='emiSupport' onchange="ChangeFunction(this.value,'EMI Support')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("emiSupport")))); %></select>
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Allowed Day's</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Is  Email Limit Enabled</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Bin Service</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Exp Date Offset</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Support Section</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Support Number Needed</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Card Whitelist Level</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Multi Currency Support</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><input type=text size=10 name='chargebackallowed_days' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("chargebackallowed_days"))%>" onchange="ChangeFunction(this.value,'Chargeback Allowed Days')">
                </td>
                <td align="center" class="<%=style%>"><select
                        name='emailLimitEnabled' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Email Limit Enabled')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("emailLimitEnabled")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='binService' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Bin Service')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("binService")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><input type=text size=10 name='expDateOffset'value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("expDateOffset"))%>" onchange="ChangeFunction(this.value,'Exp Date Offset')">
                </td>

                <td align="center" class="<%=style%>"><select
                        name='supportSection' <%=isReadOnly%> onchange="change(this,'supportNoNeeded')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("supportSection")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='supportNoNeeded' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Support Number Needed')" id="supportNoNeeded"  <%if(temphash.get("supportSection").equals("N")){  %>disabled<%}%>>
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("supportNoNeeded")))); %></select>
                    <input type=hidden size=10  name='supportNoNeeded' style="border: 1px solid #b2b2b2;font-weight:bold" value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("supportNoNeeded"))%>" >
                </td>
                <td align="center" class="<%=style%>">
                    <select name='card_whitelist_level' onchange="ChangeFunction(this.value,'Card Whitelist Level')">
                        <%
                            out.println(Functions.combovalForCardWhitelistLevel(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("card_whitelist_level")))); %>
                    </select>
                </td>

                <td align="center" class="<%=style%>"><select
                        name='multiCurrencySupport' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Multi Currency Support')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("multiCurrencySupport")))); %></select>
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1">IP Validation Required</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Bin Routing</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Personal Info Display</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Personal Info Validation</td>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">Terms Url</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Rest Checkout Page </td>
                <td style="height: 30px;" valign="middle" align="center" class="tr1"> Notification Url</td>
                <td style="height: 30px;" valign="middle" align="center" class="tr1"> Privacy Policy Url</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select name='ip_validation_required' onchange="ChangeFunction(this.value,'IP Validation Required')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("ip_validation_required")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='binRouting' onchange="ChangeFunction(this.value,'Bin Routing')">
                    <%
                        out.println(Functions.comboval9(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("binRouting")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='personal_info_display' onchange="ChangeFunction(this.value,'Personal Info Display')">
                    <%
                        out.println(Functions.comboPersonalInfo(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("personal_info_display")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='personal_info_validation' onchange="ChangeFunction(this.value,'Personal Info Validation')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("personal_info_validation")))); %></select>
                </td>
                <td valign="middle" data-label="Terms Url" align="center" class="<%=style%>">
                    <input class="txtbox" type="Text" maxlength="255"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(termsUrl)%>"
                           name="termsurl" size="255" onchange="ChangeFunction(this.value,'Terms Url')">
                </td>
                <td align="center" class="<%=style%>"><select name='hosted_payment_page' onchange="ChangeFunction(this.value,'Rest Checkout Page')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("hosted_payment_page")))); %></select>
                </td>
                <td valign="middle" data-label="Notification Url" align="center" class="<%=style%>">
                    <input class="txtbox" type="Text" maxlength="255"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(notificationUrl)%>" name="notificationurl" size="255" onchange="ChangeFunction(this.value,'Notification Url')">
                </td>

                <td valign="middle" data-label="Privacy Policy Url" align="center" class="<%=style%>">
                    <input class="txtbox" type="Text" maxlength="255"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(privacyPolicyUrl)%>" name="privacyPolicyUrl" size="255" onchange="ChangeFunction(this.value,'Privacy Policy Url')">
                </td>
            </tr>


            <tr>
                <td style="height: 30px;" valign="middle" align="center" class="tr1"> Merchant Order Details</td>

                <td style="height: 30px;" valign="middle" align="center" class="tr1">Market Place</td>
                <%--
                                <td style="height: 30px;" valign="middle" align="center" class="tr1">Is Cvv Store</td>
                --%>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">Is  Unique OrderId Required</td>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">Card Expiry Date Check</td>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">Is OTPRequired</td>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">Is CardStorageRequired</td>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">Is IgnorePaymode</td>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">Payout Personal Info Validation</td>
            </tr>

            <tr>
                <td align="center" class="<%=style%>"><select name='merchant_order_details' onchange="ChangeFunction(this.value,'Merchant Order Details')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchant_order_details")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='marketplace' onchange="ChangeFunction(this.value,'Market Place')">
                    <%
                    out.println(Functions.comboval4(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("marketplace")))); %></select>
                </td>
                <%--<td align="center" class="<%=style%>"><select name='isCvvStore' onchange="ChangeFunction(this.value,'Is Cvv Store')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isCvvStore")))); %></select>
                </td>--%>
                <td align="center" class="<%=style%>"><select name='isUniqueOrderIdRequired' onchange="ChangeFunction(this.value,'Is Unique OrderId Required')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isUniqueOrderIdRequired")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='cardExpiryDateCheck' onchange="ChangeFunction(this.value,'Card Expiry Date Check')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("cardExpiryDateCheck")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='isOTPRequired' onchange="ChangeFunction(this.value, 'Is OTPRequired')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isOTPRequired"))));
                    %>
                </select>
                </td>
                <td align="center" class="<%=style%>"><select name="isCardStorageRequired" onchange="ChangeFunction(this.value, 'Is CardStorageRequired')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isCardStorageRequired"))));
                    %>
                </select>
                </td>
                <td align="center" class="<%=style%>">
                    <select name="isIgnorePaymode" onchange="ChangeFunction(this.value, 'Is IgnorePaymode')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("IsIgnorePaymode"))));
                    %>
                </select>
                </td>
                <td align="center" class="<%=style%>">
                    <select name="payoutPersonalInfoValidation" onchange="ChangeFunction(this.value, 'Payout Personal Info Validation')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("payoutPersonalInfoValidation"))));
                        %>
                    </select>
                </td>
            </tr>

           <%-- NEW UPI INVOICE SUPPORT FLAGS--%>

            <tr>
                <td style="height: 30px;" valign="middle" align="center" class="tr1"> UPI Support Invoice</td>

                <td style="height: 30px;" valign="middle" align="center" class="tr1">UPI QR Support Invoice</td>

                <td style="height: 30px;" valign="middle" align="center" class="tr1">Pay By Link Support Invoice</td>
                <td style="height: 30px;" valign="middle" align="center" class="tr1">AEPS Support Invoice</td>

            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select name='upi_support_invoice' onchange="ChangeFunction(this.value,'UPI Support Invoice')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("upi_support_invoice")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='upi_qr_support_invoice' onchange="ChangeFunction(this.value,'UPI QR Support Invoice')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("upi_qr_support_invoice")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='paybylink_support_invoice' onchange="ChangeFunction(this.value,'Pay By Link Support Invoice')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("paybylink_support_invoice")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='AEPS_support_invoice' onchange="ChangeFunction(this.value,'AEPS Support Invoice')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("AEPS_support_invoice")))); %></select>
                </td>
            </tr>

        </table>
    </div>

    <%
        String disableSuccessUrl= "";
        String disableFailedUrl= "";
        String disablePayoutSuccess= "";
        String disablePayoutFailed= "";
        if ("N".equalsIgnoreCase((String)temphash.get("success_url_check")))
        {
            disableSuccessUrl="disabled";
        }
        if ("N".equalsIgnoreCase((String)temphash.get("failed_url_check")))
        {
            disableFailedUrl="disabled";
        }
        if ("N".equalsIgnoreCase((String)temphash.get("payout_success_url_check")))
        {
            disablePayoutSuccess="disabled";
        }
        if ("N".equalsIgnoreCase((String)temphash.get("payout_failed_url_check")))
        {
            disablePayoutFailed="disabled";
        }

    %>

    <div class="reporttable" style="margin-bottom: 9px;">
        <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
               style="margin-bottom: 0px">
            <tr class="th0">
                <td colspan="8" style="height: 30px">
                    <center><b> Merchant Notification URL Configuration </b></center>
                </td>
            </tr>
            <tr>
                <td style="height:30%;" valign="middle" align="right" class="tr1"> <b> Success Url : </b> &nbsp;&nbsp; </td>
                <td style="height: 100%; width: 100%" valign="middle" align="" class="tr1"> &nbsp;&nbsp;
                    <input type="text" class="txtbox1" style="height:100%; width:85%" maxlength="255" id="success_url" name="success_url" <%=disableSuccessUrl%> value="<%=ESAPI.encoder().encodeForHTMLAttribute(success_url)%>"> &nbsp;&nbsp;
                    <select name="success_url_check" class="txtbox2" onchange="ChangeFunction(this.value,'success_url_check');field(this.value,'success_url')">
                        <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("success_url_check")))); %>
                    </select>
                </td>
            </tr>
            <tr>
                <td style="height: 30%; width: 20%" valign="middle" align="right" class="tr1"> <b> Failed Url : </b> &nbsp;&nbsp; </td>
                <td style="height: 100%; width: 100%" valign="middle" align="" class="tr1"> &nbsp;&nbsp;
                    <input type="text" class="txtbox1" style="height:100%; width:85%" maxlength="255" id="failed_url" <%=disableFailedUrl%> name="failed_url" value="<%=ESAPI.encoder().encodeForHTMLAttribute(failed_url)%>"> &nbsp;&nbsp;
                    <select name="failed_url_check" class="txtbox2" onchange="ChangeFunction(this.value,'failed_url_check');field(this.value,'failed_url')">
                        <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("failed_url_check")))); %>
                    </select>
                </td>
            </tr>
            <tr>
                <td style="height: 30%; width: 20%" valign="middle" align="right" class="tr1"> <b> Payout Success Url :</b> &nbsp;&nbsp; </td>
                <td style="height: 100%; width: 100%" valign="middle" align="" class="tr1" > &nbsp;&nbsp;
                    <input type="text" class="txtbox1" style="height:100%; width:85%" maxlength="255" id="payout_success_url" <%=disablePayoutSuccess%> name="payout_success_url" value="<%=ESAPI.encoder().encodeForHTMLAttribute(payout_success_url)%>"> &nbsp;&nbsp;
                    <select name="payout_success_url_check" class="txtbox2" onchange="ChangeFunction(this.value,'payout_success_url_check');field(this.value,'payout_success_url')">
                        <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("payout_success_url_check")))); %>
                    </select>
                </td>
            </tr>
            <tr>
                <td style="height: 30%; width: 20%" valign="middle" align="right" class="tr1"> <b> Payout Failed Url : </b> &nbsp;&nbsp; </td>
                <td style="height: 100%; width: 100%" valign="middle" align="" class="tr1"> &nbsp;&nbsp;
                    <input type="text" class="txtbox1" style="height:100%; width:85%" maxlength="255" id="payout_failed_url" <%=disablePayoutFailed%> name="payout_failed_url" value="<%=ESAPI.encoder().encodeForHTMLAttribute(payout_failed_url)%>"> &nbsp;&nbsp;
                    <select name="payout_failed_url_check" class="txtbox2" onchange="ChangeFunction(this.value,'payout_failed_url_check');field(this.value,'payout_failed_url')">
                        <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("payout_failed_url_check")))); %>
                    </select>
                </td>
            </tr>
        </table>
    </div>

    <div class="reporttable" style="margin-bottom: 9px;">
        <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
               style="margin-bottom: 0px">
            <tr class="th0">
                <td colspan="7" style="height: 30px">
                    <center><b>BackOffice Access Management</b></center>
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> DashBoard </td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Account Details</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Settings</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Transaction Management</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Invoicing</td>
                <%--<td style="height: 30px" valign="middle" align="center" class="tr1"> EMI Configuration</td>--%>
            </tr>
            <tr>
                <td align="center" class="<%=style%>">
                    <select name='dashboard_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'DashBoard')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("dashboard_access")))); %>
                    </select>

                </td>
                <td align="center" class="<%=style%>"><select
                        name='accounting_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Account Details')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("accounting_access")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='setting_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Settings')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("setting_access")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='transactions_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Transaction Management')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("transactions_access")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='invoicing_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Invoicing')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("invoicing_access")))); %></select>
                </td>
                <%--<td align="center" class="<%=style%>"><select
                        name='emi_configuration' <%=isReadOnly%>>
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("emi_configuration")))); %></select>
                </td>--%>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Virtual Terminal</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Merchant Management</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Application Manager</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Recurring</td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" > Token Management</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select
                        name='virtualterminal_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Virtual Terminal')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("virtualterminal_access")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='merchantmgt_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Merchant Management')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchantmgt_access")))); %></select>
                </td>
                <td align="center" class="<%=style%>">
                    <select name='isappmanageractivate' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Application Manager')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isappmanageractivate")))); %></select>
                </td>
                <td align="center" class="<%=style%>">
                    <select name='is_recurring' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Recurring')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_recurring"))));
                        %>
                    </select>
                </td>
                <td align="center" class="<%=style%>">
                    <select name='iscardregistrationallowed' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Token Management')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("iscardregistrationallowed"))));
                        %>
                    </select>
            <tr>
                <td style="height: 30px"valign="middle" align="center" class="tr1" > Rejected Transaction </td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" > Virtual Checkout </td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" > Pay By Link </td>
            </tr>
            <td align="center" class="<%=style%>">
                <select name='rejected_transaction' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Rejected Transaction')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("rejected_transaction"))));
                    %>
                </select>
            <td align="center" class="<%=style%>">
                <select name='virtual_checkout' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Virtual Checkout')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("virtual_checkout"))));
                    %>
                </select>
            </td>

            <td align="center" class="<%=style%>">
                <select name='paybylink' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Pay By Link')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("paybylink"))));
                    %>
                </select>
            </td>
            </tr>
        </table>

        <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
            <tr class="td1">
                <td colspan="7" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
                    Account Details
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Account Summary  </td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Charges Summary </td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Transaction Summary</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Reports </td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select
                        name='accounts_account_summary_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Account Summary')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("accounts_account_summary_access")))); %>
                </select>
                </td>
                </select>
                <td align="center" class="<%=style%>"><select
                        name='accounts_charges_summary_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Charges Summary')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("accounts_charges_summary_access")))); %>
                </select>
                </td>
                </select>

                <td align="center" class="<%=style%>"><select
                        name='accounts_transaction_summary_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Transaction Summary')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("accounts_transaction_summary_access")))); %>
                </select>
                </td>
                </select>

                <td align="center" class="<%=style%>"><select
                        name='accounts_reports_summary_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Reports')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("accounts_reports_summary_access")))); %>
                </select>
                </td>
                </select>
            </tr>
        </table>
        <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
            <tr class="td1">
                <td colspan="9" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
                    Settings
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Merchant Profile </td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Organisation Profile</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Generate View</td>
                <%--<td style="height: 30px" valign="middle" align="center" class="tr1"> Checkout Page</td>--%>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Generate Key </td>
                <%--<td style="height: 30px" valign="middle" align="center" class="tr1"> Invoice Configuration</td>--%>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Merchant Configuration</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Checkout Config</td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" > Fraud Rule Configuration</td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" > Whitelist Details</td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" > Block Details</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select
                        name='settings_merchant_profile_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Merchant Profile')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_merchant_profile_access")))); %>
                </select>
                </td>
                </select>
                <td align="center" class="<%=style%>"><select
                        name='settings_organisation_profile_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Organisation Profile')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_organisation_profile_access")))); %>
                </select>
                </td>
                </select>
                <td align="center" class="<%=style%>"><select
                        name='generateview' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Generate View')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("generateview")))); %>
                </select>
                </td>
                </select>
                <%--<td align="center" class="<%=style%>"><select
                        name='settings_checkout_page_access' <%=isReadOnly%>>
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_checkout_page_access")))); %></select>
                </td>--%>
                </select>
                <td align="center" class="<%=style%>"><select
                        name='settings_generate_key_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Generate Key')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_generate_key_access")))); %></select>
                </td>
                </select>
                <%--<td align="center" class="<%=style%>"><select
                        name='settings_invoice_config_access' <%=isReadOnly%>>
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_invoice_config_access")))); %></select>
                </td>
                </select>--%>
                <td align="center" class="<%=style%>"><select
                        name='settings_merchant_config_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Merchant Configuration')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_merchant_config_access")))); %></select>
                </td>
                </select>
                <td align="center" class="<%=style%>"><select
                        name='checkout_config' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Checkout Config')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("checkout_config")))); %></select>
                </td>
                </select>
                <td align="center" class="<%=style%>">
                    <select name='settings_fraudrule_config_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Fraud Rule Configuration')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("settings_fraudrule_config_access"))));
                        %>
                    </select>
                </td>
                <td align="center" class="<%=style%>">
                    <select name='settings_whitelist_details' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Whitelist Details')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("settings_whitelist_details"))));
                        %>
                    </select>
                </td>
                <td align="center" class="<%=style%>">
                    <select name='settings_blacklist_details' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Block Details')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("settings_blacklist_details"))));
                        %>
                    </select>
                </td>
            </tr>
        </table>
        <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
            <tr class="td1">
                <td colspan="7" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
                    Transaction Management
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Transactions </td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Capture </td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Reversal</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Payout </td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Payout Transaction </td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select
                        name='transmgt_transaction_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Transactions')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("transmgt_transaction_access")))); %></select>
                </td>
                </select>
                <td align="center" class="<%=style%>"><select
                        name='transmgt_capture_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Capture')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("transmgt_capture_access")))); %></select>
                </td>
                </select>
                <td align="center" class="<%=style%>"><select
                        name='transmgt_reversal_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Reversal')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("transmgt_reversal_access")))); %></select>
                </td>
                </select>
                <td align="center" class="<%=style%>"><select
                        name='transmgt_payout_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Payout')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("transmgt_payout_access")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='transmgt_payout_transactions' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Payout Transaction')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("transmgt_payout_transactions")))); %></select>

                </td>
                </select>
            </tr>
        </table>
        <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
            <tr class="td1">
                <td colspan="7" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
                    Invoicing
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Generate Invoice </td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Invoice History </td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Invoice Configuration</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select
                        name='invoice_generate_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Generate Invoice')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("invoice_generate_access")))); %></select>
                </td>
                </select>
                <td align="center" class="<%=style%>"><select
                        name='invoice_history_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Invoice History')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("invoice_history_access")))); %></select>
                </td>
                </select>
                <td align="center" class="<%=style%>"><select
                        name='settings_invoice_config_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Invoice Configuration')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("settings_invoice_config_access")))); %></select>
                </td>
                </select>
            </tr>
        </table>
        <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
            <tr class="td1">
                <td colspan="7" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
                    Token Management
                </td>
            </tr>

            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Registration History </td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Register Card </td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select
                        name='tokenmgt_registration_history_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Registration History')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("tokenmgt_registration_history_access")))); %></select>
                </td>
                </select>
                <td align="center" class="<%=style%>"><select
                        name='tokenmgt_register_card_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Register Card')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("tokenmgt_register_card_access")))); %></select>
                </td>
                </select>
            </tr>
        </table>
        <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
            <tr class="td1">
                <td colspan="7" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
                    Merchant Management
                </td>
            </tr>

            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> User Management </td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select
                        name='merchantmgt_user_management_access' <%=isReadOnly%> onchange="ChangeFunction(this.value,'User Management')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("merchantmgt_user_management_access")))); %></select>
                </td>
                </select>
            </tr>
        </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
        <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
            <tr class="th0">
                <td colspan="13" style="height: 30px">
                    <center><b>Template Configuration</b></center>
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> IS Pharma(Y/N)</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Is Powered By Logo</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Template</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> PCI Logo</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Partner Logo</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Security Logo</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Is Merchant Logo</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Visa Secure Logo</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">isMerchantLogoBO</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> MC Secure Logo</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Consent</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Checkout Timer</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Checkout Timer Time</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select
                        name='isPharma' <%=isReadOnly%> onchange="ChangeFunction(this.value,'IS Pharma(Y/N)')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isPharma")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='isPoweredBy' onchange="ChangeFunction(this.value,'Is Powered By Logo')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isPoweredBy")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='template' onchange="ChangeFunction(this.value,'Template')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("template")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='ispcilogo'<%--<%=isReadOnly%>--%> onchange="ChangeFunction(this.value,'PCI Logo')">
                    <%
                        if("Y".equals(temphash.get("ispcilogo")))
                        {
                    %>
                    <option value="Y">Y</option>
                    <option value="N">N</option>
                    <%
                    }
                    else
                    {
                    %>
                    <option value="N">N</option>
                    <option value="Y">Y</option>
                    <% }%>
                </select>
                </td>
                <td align="center" class="<%=style%>"><select name='ispartnerlogo'<%--<%=isReadOnly%>--%> onchange="ChangeFunction(this.value,'Partner Logo')">
                    <%
                        if("Y".equals(temphash.get("ispartnerlogo")))
                        {
                    %>
                    <option value="Y">Y</option>
                    <option value="N">N</option>
                    <%
                    }
                    else
                    {
                    %>
                    <option value="N">N</option>
                    <option value="Y">Y</option>
                    <% }%>
                </select>
                </td>
                <td align="center" class="<%=style%>"><select name='isSecurityLogo'<%--<%=isReadOnly%>--%> onchange="ChangeFunction(this.value,'Security Logo')">
                    <%
                        if("Y".equals(temphash.get("isSecurityLogo")))
                        {
                    %>
                    <option value="Y">Y</option>
                    <option value="N">N</option>
                    <%
                    }
                    else
                    {
                    %>
                    <option value="N">N</option>
                    <option value="Y">Y</option>
                    <% }%>
                </select>
                </td>
                <td align="center" class="<%=style%>"><select name='ismerchantlogo'<%--<%=isReadOnly%>--%> onchange="ChangeFunction(this.value,'Is Merchant Logo')">
                    <%
                        if("Y".equals(temphash.get("ismerchantlogo")))
                        {
                    %>
                    <option value="Y">Y</option>
                    <option value="N">N</option>
                    <%
                    }
                    else
                    {
                    %>
                    <option value="N">N</option>
                    <option value="Y">Y</option>
                    <% }%>
                </select>
                </td>
                <td align="center" class="<%=style%>"><select name='vbvLogo'<%--<%=isReadOnly%>--%> onchange="ChangeFunction(this.value,'Visa Secure Logo')">
                    <%
                        if("Y".equals(temphash.get("vbvLogo")))
                        {
                    %>
                    <option value="Y">Y</option>
                    <option value="N">N</option>
                    <%
                    }
                    else
                    {
                    %>
                    <option value="N">N</option>
                    <option value="Y">Y</option>
                    <% }%>
                </select>
                </td>
                <td align="center" class="<%=style%>"><select name='isMerchantLogoBO' onchange="ChangeFunction(this.value, 'isMerchantLogoBO')">
                    <%
                        if("Y".equals(temphash.get("isMerchantLogoBO")))
                        {
                    %>
                    <option value="Y">Y</option>
                    <option value="N">N</option>
                    <%
                    }
                    else
                    {
                    %>
                    <option value="N">N</option>
                    <option value="Y">Y</option>
                    <%  }%>
                </select>
                </td>
                <td align="center" class="<%=style%>"><select name='masterSecureLogo'<%--<%=isReadOnly%>--%> onchange="ChangeFunction(this.value,'MC Secure Logo')">
                    <%
                        if("Y".equals(temphash.get("masterSecureLogo")))
                        {
                    %>
                    <option value="Y">Y</option>
                    <option value="N">N</option>
                    <%
                    }
                    else
                    {
                    %>
                    <option value="N">N</option>
                    <option value="Y">Y</option>
                    <% }%>
                </select>
                </td>
                <td align="center" class="<%=style%>"><select name='consent'<%--<%=isReadOnly%>--%> onchange="ChangeFunction(this.value,'Consent')">
                    <%
                        if("Y".equals(temphash.get("consent")))
                        {
                    %>
                    <option value="Y">Y</option>
                    <option value="N">N</option>
                    <%
                    }
                    else
                    {
                    %>
                    <option value="N">N</option>
                    <option value="Y">Y</option>
                    <% }%>
                </select>
                </td>
                <td align="center" class="<%=style%>"><select name='checkoutTimer'<%--<%=isReadOnly%>--%> onchange="ChangeFunction(this.value,'Checkout Timer')">
                    <%
                        if("Y".equals(temphash.get("checkoutTimer")))
                        {
                    %>
                    <option value="Y">Y</option>
                    <option value="N">N</option>
                    <%
                    }
                    else
                    {
                    %>
                    <option value="N">N</option>
                    <option value="Y">Y</option>
                    <% }%>
                </select>
                </td>
                <td align="center" class="<%=style%>">
                    <input type=text size=10 name='checkoutTimerTime' placeholder="00:00" id='checkoutTimerTime' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("checkoutTimerTime"))%>" onchange="ChangeFunction(this.value,'Checkout Timer Time')">
                </td>
            </tr>
        </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
        <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
               style="margin-bottom: 0px">
            <tr class="th0"><td colspan="5" style="height: 30px"><center><b>Token Configuration</b></center></td></tr>
            <tr>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >Is TokenizationAllowed</td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >Is AddressDetails Required</td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >Token Valid Days</td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >Is Card Encryption Enable</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select name='isTokenizationAllowed' onchange="ChangeFunction(this.value,'Is TokenizationAllowed')">
                    <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isTokenizationAllowed"))));  %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='isAddrDetailsRequired' onchange="ChangeFunction(this.value,'Is AddressDetails Required')">
                    <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isAddrDetailsRequired"))));  %></select>
                </td>
                <td align="center" class="<%=style%>"><input type=text size=10 name='tokenvaliddays' value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("tokenvaliddays"))%>" onchange="ChangeFunction(this.value,'Token Valid Days')">
                </td>
                <td align="center" class="<%=style%>"><select name='isCardEncryptionEnable' onchange="ChangeFunction(this.value,'Is Card Encryption Enable')">
                    <%out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("isCardEncryptionEnable"))));  %></select>
                </td>
            </tr>
        </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
        <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
               style="margin-bottom: 0px">
            <tr class="th0">
                <td colspan="7" style="height: 30px">
                    <center><b>Fraud Defender Configuration</b></center>
                </td>
            </tr>
        </table>

        <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
            <tr class="td1">
                <td colspan="7" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
                    Purchase Inquiry
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund  </td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Blacklist </td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select
                        name='ispurchase_inquiry_refund' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Purchase Inquiry Refund')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("ispurchase_inquiry_refund")))); %>
                </select>
                </td>
                </select>
                <td align="center" class="<%=style%>"><select
                        name='ispurchase_inquiry_blacklist' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Purchase Inquiry Blacklist')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("ispurchase_inquiry_blacklist")))); %>
                </select>
                </td>
                </select>
            </tr>
        </table>
        <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
            <tr class="td1">
                <td colspan="9" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
                    Fraud Determined
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund </td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Blacklist</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select
                        name='isfraud_determined_refund' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Fraud Determined Refund')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isfraud_determined_refund")))); %>
                </select>
                </td>
                </select>
                <td align="center" class="<%=style%>"><select
                        name='isfraud_determined_blacklist' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Fraud Determined Blacklist')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isfraud_determined_blacklist")))); %>
                </select>
                </td>
                </select>

            </tr>
        </table>
        <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
            <tr class="td1">
                <td colspan="9" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
                    Dispute Initiated
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund </td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Blacklist</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select
                        name='isdispute_initiated_refund' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Dispute Initiated Refund')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isdispute_initiated_refund")))); %>
                </select>
                </td>
                </select>
                <td align="center" class="<%=style%>"><select
                        name='isdispute_initiated_blacklist' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Dispute Initiated Blacklist')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isdispute_initiated_blacklist")))); %>
                </select>
                </td>
                </select>

            </tr>
        </table>
        <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
            <tr class="td1">
                <td colspan="9" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
                    Exception file listing
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Blacklist</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select
                        name='isexception_file_listing_blacklist' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Exception file listing Blacklist')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isexception_file_listing_blacklist")))); %>
                </select>
                </td>
            </tr>
        </table>
        <table  border="0" width="100%" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
            <tr class="td1">
                <td colspan="9" style="text-align: center;font-family:Open Sans Helvetica Neue Helvetica,Arial,sans-serif;font-size: 12px;">
                    Stop payment
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Blacklist</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select
                        name='isstop_payment_blacklist' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Stop payment Blacklist')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isstop_payment_blacklist")))); %>
                </select>
                </td>
            </tr>
        </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
        <table border="0" width="100%" style="width: 50%" align="center"
               class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">
            <tr class="th0">
                <td colspan="4" style="height: 30px">
                    <center><b>Whitelisting Configuration</b></center>
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Is Card Whitelisted</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Is Ip Whitelisted</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Is IP Whitelisted For APIs</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Is Domain Whitelisted</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select name='iswhitelisted' onchange="ChangeFunction(this.value,'Is Card Whitelisted')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("iswhitelisted")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='isipwhitelisted' onchange="ChangeFunction(this.value,'Is Ip Whitelisted')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isIpWhitelisted")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='is_rest_whitelisted' onchange="ChangeFunction(this.value,'Is IP Whitelisted For APIs')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("is_rest_whitelisted")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='isDomainWhitelisted' onchange="ChangeFunction(this.value,'Is Domain Whitelisted')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isDomainWhitelisted")))); %></select>
                </td>
            </tr>
        </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
        <table  border="0" width="100%" style="width: 50%" align="center" class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px" >
            <tr class="th0">
                <td colspan="2" style="height: 30px">
                    <center><b>HR Transaction Configuration</b></center>
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> HR alertPROOF</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> HR Parameterized</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select
                        name='hralertproof' <%=isReadOnly%> onchange="ChangeFunction(this.value,'HR alertPROOF')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("hralertproof")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='hrparameterised' <%=isReadOnly%> onchange="ChangeFunction(this.value,'HR Parameterized')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("hrparameterised")))); %></select>
                </td>
            </tr>
        </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
        <table border="0" width="100%" style="width: 50%" align="center"
               class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">
            <tr class="th0">
                <td colspan="8" style="height: 30px">
                    <center><b>Refund Configuration</b></center>
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Is Refund</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Is Multiple Refund</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Daily Refund Limit</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Allowed Day's</td>
                <%--<td style="height: 30px" valign="middle" align="center" class="tr1"> Is Refund Email Sent</td>--%>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> is Partial Refund</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select name='isrefund' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Refund')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isrefund")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='isMultipleRefund' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Multiple Refund')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isMultipleRefund")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><input type=text size=10 name='refunddailylimit'
                                                             value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("refunddailylimit"))%>" onchange="ChangeFunction(this.value,'Daily Refund Limit')">
                </td>
                <td align="center" class="<%=style%>"><input type=text size=10 name='refundallowed_days'
                                                             value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("refundallowed_days"))%>" onchange="ChangeFunction(this.value,'Refund Allowed Days')">
                </td>
                <%--<td align="center" class="<%=style%>"><select name='isRefundEmailSent' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Refund Email Sent')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isRefundEmailSent")))); %></select>
                </td>--%>
                <td align="center" class="<%=style%>"><select name='isPartialRefund' <%=isReadOnly%> onchange="ChangeFunction(this.value,'is Partial Refund')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isPartialRefund")))); %></select>
                </td>
            </tr>
        </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
        <table border="0" width="100%" style="width: 50%" align="center"
               class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">
            <tr class="th0">
                <td colspan="6" style="height: 30px">
                    <center><b>Email Configuration</b></center>
                </td>
            </tr>
            </thead>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> IS Validate Email</td>
                <%--<td style="height: 30px" valign="middle" align="center" class="tr1"> Customer Reminder Email</td>--%>
                <%--<td style="height: 30px" valign="middle" align="center" class="tr1"> Is Email Sent</td>--%>
                <%--<td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Email (Y/N)</td>--%>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Email Template Language</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select
                        name='isValidateEmail' <%=isReadOnly%> onchange="ChangeFunction(this.value,'IS Validate Email')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isValidateEmail")))); %></select>
                </td>
                <%--<td align="center" class="<%=style%>"><select
                        name='custremindermail' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer Reminder Email')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("custremindermail")))); %></select>
                </td>--%>
               <%-- <td align="center" class="<%=style%>"><select
                        name='emailSent' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Email Sent')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("emailSent")))); %></select>
                </td>--%>
                <%--<td align="center" class="<%=style%>"><select
                        name='chargebackEmail' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Chargeback Email (Y/N)')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("chargebackEmail")))); %></select>
                </td>--%>
                <td align="center" class="<%=style%>"><select
                        name='emailTemplateLang' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Email Template Language')">
                    <%
                        out.println(Functions.combovalLanguage(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("emailTemplateLang")))); %></select>
                </td>
            </tr>
        </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
        <table border="0" width="100%" style="width: 50%" align="center"
               class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">
            <tr class="th0">
                <td colspan="2" style="height: 30px">
                    <center><b>SMS Configuration</b></center>
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Merchant SMS Activation</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Customer SMS Activation</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select name='smsactivation' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Merchant SMS Activation')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("smsactivation")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select name='customersmsactivation' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Customer SMS Activation')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("customersmsactivation")))); %></select>
                </td>
            </tr>
        </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
        <table border="0" width="100%" style="width: 50%" align="center"
               class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">
            <tr class="th0">
                <td colspan="2" style="height: 30px">
                    <center><b>Invoice Configuration</b></center>
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Invoice Merchant Details</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Is IP whitelist for Invoice</td>
            </tr>
            <tr>
                <td align="center" align="center" class="<%=style%>"><select
                        name='invoicetemplate' onchange="ChangeFunction(this.value,'Invoice Merchant Details')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("invoicetemplate")))); %></select>
                </td>
                <td align="center" align="center" class="<%=style%>"><select
                        name='ip_whitelist_invoice' onchange="ChangeFunction(this.value,'Is IP whitelist for Invoice')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("ip_whitelist_invoice")))); %></select>
                </td>
            </tr>
        </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
        <table border="0" width="100%" style="width: 50%" align="center"
               class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">
            <tr class="th0">
                <td colspan="4" style="height: 30px">
                    <center>Fraud Configuration</center>
                </td>
            </tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Max Score Allowed</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Max Score Reversal</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Online Fraud Check</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1">Internal Fraud Check</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><input type=text size=10 name='maxscoreallowed'
                                                             value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("maxScoreAllowed"))%>" onchange="ChangeFunction(this.value,'Max Score Allowed')">
                </td>
                <td align="center" class="<%=style%>"><input type=text size=10 name='maxscoreautoreversal'
                                                             value="<%=ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("maxScoreAutoReversal"))%>" onchange="ChangeFunction(this.value,'Max Score Reversal')">
                </td>
                <td align="center" class="<%=style%>"><select
                        name='onlineFraudCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Online Fraud Check')">

                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("onlineFraudCheck")))); %></select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='internalFraudCheck' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Internal Fraud Check')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("internalFraudCheck"))));

                    %></select>
                </td>
            </tr>
        </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
        <table border="0" width="100%" style="width: 50%" align="center"
               class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">
            <tr class="th0"><td colspan="2" style="height: 30px"><center><b>Split transaction configuration</b></center></td></tr>
            <tr>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >Split Payment Allowed</td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >Split Payment Type</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>">
                    <select name='isSplitPayment' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Split Payment Allowed')">
                        <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isSplitPayment")))); %>
                    </select>
                </td>
                <td align="center" class="<%=style%>">
                    <select name='splitPaymentType' onchange="ChangeFunction(this.value,'Split Payment Type')">
                        <%
                            out.println(Functions.combovalForSplitPayment(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("splitPaymentType")))); %>
                    </select>
                </td>
            </tr>
        </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
        <table border="0" width="100%" style="width: 50%" align="center"
               class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">
            <tr class="th0"><td colspan="7" style="height: 30px"><center><b>Invoizer Configuration</b></center></td></tr>
            <tr>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >Is Virtual Checkout Allowed</td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >Is Phone Required</td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >Is Email Required</td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >Is Share Allowed</td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >Is Signature Allowed</td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >Is Save Receipt Allowed</td>
                <td style="height: 30px"valign="middle" align="center" class="tr1" >Default Language</td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>">
                    <select name='isVirtualCheckoutAllowed' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Virtual Checkout Allowed')">
                        <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isVirtualCheckoutAllowed")))); %>
                    </select>
                </td>
                <td align="center" class="<%=style%>">
                    <select name='isMobileAllowedForVC' onchange="ChangeFunction(this.value,'Is Phone Required')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isMobileAllowedForVC")))); %>
                    </select>
                </td>
                <td align="center" class="<%=style%>">
                    <select name='isEmailAllowedForVC' onchange="ChangeFunction(this.value,'Is Email Required')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isEmailAllowedForVC")))); %>
                    </select>
                </td>
                <td align="center" class="<%=style%>">
                    <select name='isShareAllowed' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Is Share Allowed')">
                        <% out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isShareAllowed")))); %>
                    </select>
                </td>
                <td align="center" class="<%=style%>">
                    <select name='isSignatureAllowed' onchange="ChangeFunction(this.value,'Is Signature Allowed')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isSignatureAllowed")))); %>
                    </select>
                </td>
                <td align="center" class="<%=style%>">
                    <select name='isSaveReceiptAllowed' onchange="ChangeFunction(this.value,'Is Save Receipt Allowed')">
                        <%
                            out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("isSaveReceiptAllowed")))); %>
                    </select>
                </td>
                <td align="center" class="<%=style%>"><select
                        name='defaultLanguage' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Default Language')">
                    <%
                        out.println(Functions.combovalLang(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("defaultLanguage")))); %></select>
                </td>
            </tr>
        </table>
    </div>

    <div class="reporttable" style="margin-bottom: 9px;">
        <table border="0" width="100%" style="width: 50%" align="center"
               class="table table-striped table-bordered  table-green dataTable" style="margin-bottom: 0px">
            <tr class="th0"><td colspan="6" style="height: 30px"><center><b>Merchant Notification Callback</b></center></td></tr>
            <tr>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Reconciliation </td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Transactions (3D/Non-3d/Both/No)</td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund  Notification </td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Notification </td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Payout Notification </td>
                <td style="height: 30px" valign="middle" align="center" class="tr1"> Inquiry Notification </td>
            </tr>
            <tr>
                <td align="center" class="<%=style%>"><select
                        name='reconciliationNotification' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Reconciliation')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("reconciliationNotification")))); %></select>
                </td>
                </select>
                <td align="center" class="<%=style%>"><select
                        name='transactionNotification' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Transactions (3D/Non-3d/Both/No)')">
                    <%
                        out.println(Functions.comboval3D(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("transactionNotification")))); %></select>
                </td>
                </select>
                <td align="center" class="<%=style%>"><select
                        name='refundNotification' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Refund  Notification')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("refundNotification")))); %></select>
                </td>
                </select>
                <td align="center" class="<%=style%>"><select
                        name='chargebackNotification' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Chargeback Notification')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("chargebackNotification")))); %></select>
                </td>
                </select>
                <td align="center" class="<%=style%>"><select
                        name='payoutNotification' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Payout Notification')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String)temphash.get("payoutNotification"))));
                    %>
                </select>
                <td align="center" class="<%=style%>"><select
                        name='inquiryNotification' <%=isReadOnly%> onchange="ChangeFunction(this.value,'Inquiry Notification')">
                    <%
                        out.println(Functions.comboval(ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("inquiryNotification"))));
                    %>
                </select>
                </td>
                </select>
            </tr>
        </table>
    </div>
    <div class="reporttable" style="margin-bottom: 9px;">
        <%--<div class="reporttable" style="margin-bottom: 9px;">--%>
            <table border="0" width="100%" align="center" class="table table-striped table-bordered  table-green dataTable"
                   style="margin-bottom: 0px">
                <tr class="th0">
                    <td colspan="7" style="height: 30px">
                        <center><b>Merchant Email Notification Settings</b></center>
                    </td>
                </tr>
            </table>
            <table border="0" width="100%" align="center" class="table table-striped table-bordered  table-green dataTable"
                   style="margin-bottom: 0px">
                <tr class="td1">
                    <td colspan="8" style="height: 30px">
                        <center><b>Setup Mails</b></center>
                    </td>
                </tr>
                </thead>
                <%
                    String merchantRegistrationMail="";
                    String merchantChangePassword="";
                    String merchantChangeProfile="";
                    if("Y".equals(temphash.get("merchantRegistrationMail")))
                        merchantRegistrationMail="checked";
                    if("Y".equals(temphash.get("merchantChangePassword")))
                        merchantChangePassword="checked";
                    if("Y".equals(temphash.get("merchantChangeProfile")))
                        merchantChangeProfile="checked";
                %>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Main Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Sales Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Fraud Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Technical Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Billing Contact</td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Registration</td>
                    <td align="center" class="tr0"><input type="checkbox" name='merchantRegistrationMail' <%=merchantRegistrationMail%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Registration')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Change Password</td>
                    <td align="center" class="tr0"><input type="checkbox" name='merchantChangePassword' <%=merchantChangePassword%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Change Password')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Change Profile</td>
                    <td align="center" class="tr0"><input type="checkbox" name='merchantChangeProfile' <%=merchantChangeProfile%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Change Profile')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
            </table>
            <br><br>
       <%-- </div>--%>
        <%--<div class="reporttable" style="margin-bottom: 9px;">--%>
            <table border="0" width="100%" align="center" class="table table-striped table-bordered  table-green dataTable"
                   style="margin-bottom: 0px">
                <tr class="td1">
                    <td colspan="8" style="height: 30px">
                        <center><b>Transaction Mails</b></center>
                    </td>
                </tr>
                </thead>
                <%
                    String transactionSuccessfulMail="";
                    String transactionFailMail="";
                    String transactionCapture="";
                    String transactionPayoutSuccess="";
                    String transactionPayoutFail="";
                    if("Y".equals(temphash.get("transactionSuccessfulMail")))
                        transactionSuccessfulMail="checked";
                    if("Y".equals(temphash.get("transactionFailMail")))
                        transactionFailMail="checked";
                    if("Y".equals(temphash.get("transactionCapture")))
                        transactionCapture="checked";
                    if("Y".equals(temphash.get("transactionPayoutSuccess")))
                        transactionPayoutSuccess="checked";
                    if("Y".equals(temphash.get("transactionPayoutFail")))
                        transactionPayoutFail="checked";
                %>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Main Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Sales Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Fraud Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Technical Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Billing Contact</td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Successful</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td align="center" class="tr0"><input type="checkbox" name='transactionSuccessfulMail' <%=transactionSuccessfulMail%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Transaction Successful Mail')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Failed</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td align="center" class="tr0"><input type="checkbox" name='transactionFailMail' <%=transactionFailMail%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Transaction Failed Mail')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Capture</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td align="center" class="tr0"><input type="checkbox" name='transactionCapture' <%=transactionCapture%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Transaction Capture Mail')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Payout Successful</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td align="center" class="tr0"><input type="checkbox" name='transactionPayoutSuccess' <%=transactionPayoutSuccess%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Transaction Payout Successful Mail')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Payout Fail</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td align="center" class="tr0"><input type="checkbox" name='transactionPayoutFail' <%=transactionPayoutFail%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Transaction Payout Fail Mail')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
            </table>
            <br><br>
       <%-- </div>--%>
        <%--<div class="reporttable" style="margin-bottom: 9px;">--%>
            <table border="0" width="100%"  align="center" class="table table-striped table-bordered  table-green dataTable"
                   style="margin-bottom: 0px">
                <tr class="td1">
                    <td colspan="8" style="height: 30px">
                        <center><b>Refund/Chargeback Mails</b></center>
                    </td>
                </tr>
                </thead>
                <%
                    String refundMail="";
                    String chargebackMail="";
                    if("Y".equals(temphash.get("refundMail")))
                        refundMail="checked";
                    if("Y".equals(temphash.get("chargebackMail")))
                        chargebackMail="checked";
                %>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Main Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Sales Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Fraud Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Technical Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Billing Contact</td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Refunds</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td align="center" class="tr0"><input type="checkbox" name='refundMail' <%=refundMail%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Refunds Mail')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Chargebacks</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td align="center" class="tr0"><input type="checkbox" name='chargebackMail' <%=chargebackMail%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Chargebacks Mail')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
            </table>
            <br><br>
       <%-- </div>--%>
        <%--<div class="reporttable" style="margin-bottom: 9px;">--%>
            <table border="0" width="100%" align="center" class="table table-striped table-bordered  table-green dataTable"
                   style="margin-bottom: 0px">
                <tr class="td1">
                    <td colspan="8" style="height: 30px">
                        <center><b>Invoice Mails</b></center>
                    </td>
                </tr>
                </thead>
                <%
                    String transactionInvoice="";
                    if("Y".equals(temphash.get("transactionInvoice")))
                        transactionInvoice="checked";
                %>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Main Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Sales Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Fraud Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Technical Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Billing Contact</td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Invoice</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td align="center" class="tr0"><input type="checkbox" name='transactionInvoice' <%=transactionInvoice%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Invoice Mail')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
            </table>
            <br><br>
       <%-- </div>--%>
       <%-- <div class="reporttable" style="margin-bottom: 9px;">--%>
            <table border="0" width="100%" align="center" class="table table-striped table-bordered  table-green dataTable"
                   style="margin-bottom: 0px">
                <tr class="td1">
                    <td colspan="8" style="height: 30px">
                        <center><b>Tokenization Mails</b></center>
                    </td>
                </tr>
                </thead>
                <%
                    String cardRegistration="";
                    if("Y".equals(temphash.get("cardRegistration")))
                        cardRegistration="checked";
                %>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Main Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Sales Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Fraud Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Technical Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Billing Contact</td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Card Registration</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td align="center" class="tr0"><input type="checkbox" name='cardRegistration' <%=cardRegistration%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Tokenization Card Registration Mails')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
            </table>
            <br><br>
       <%-- </div>--%>
       <%-- <div class="reporttable" style="margin-bottom: 9px;">--%>
            <table border="0" width="100%" align="center" class="table table-striped table-bordered  table-green dataTable"
                   style="margin-bottom: 0px">
                <tr class="td1">
                    <td colspan="8" style="height: 30px">
                        <center><b>Payout Mails</b></center>
                    </td>
                </tr>
                </thead>
                <%
                    String payoutReport="";
                    if("Y".equals(temphash.get("payoutReport")))
                        payoutReport="checked";
                %>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Main Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Sales Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Fraud Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Technical Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Billing Contact</td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Payout Report</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td align="center" class="tr0"><input type="checkbox" name='payoutReport' <%=payoutReport%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Payout Report Mails')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
            </table>
            <br><br>
        <%--</div>--%>
        <%--
                <div class="reporttable" style="margin-bottom: 9px;">
                    <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
                           style="margin-bottom: 0px">
                        <tr class="td1">
                            <td colspan="8" style="height: 30px">
                                <center><b>Monitoring Mails</b></center>
                            </td>
                        </tr>
                        </thead>
                        <%
                            String monitoringAlertMail="";
                            String monitoringSuspensionMail="";
                            if("Y".equals(temphash.get("monitoringAlertMail")))
                                monitoringAlertMail="checked";
                            if("Y".equals(temphash.get("monitoringSuspensionMail")))
                                monitoringSuspensionMail="checked";
                        %>
                        <tr>
                            <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
                            <td style="height: 30px" valign="middle" align="center" class="tr1"> Main Contact</td>
                            <td style="height: 30px" valign="middle" align="center" class="tr1"> Sales Contact</td>
                            <td style="height: 30px" valign="middle" align="center" class="tr1"> Fraud Contact</td>
                            <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Contact</td>
                            <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Contact</td>
                            <td style="height: 30px" valign="middle" align="center" class="tr1"> Technical Contact</td>
                            <td style="height: 30px" valign="middle" align="center" class="tr1"> Billing Contact</td>
                        </tr>
                        <tr>
                            <td style="height: 30px" valign="middle" align="center" class="tr0"> Alerts</td>
                            <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                            <td align="center" class="tr0"><input type="checkbox" name='monitoringAlertMail' <%=monitoringAlertMail%>
                                                                  value="Y">
                            <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                            <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                            <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                            <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                            <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                        </tr>
                        <tr>
                            <td style="height: 30px" valign="middle" align="center" class="tr0"> Suspension</td>
                            <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                            <td align="center" class="tr0"><input type="checkbox" name='monitoringSuspensionMail' <%=monitoringSuspensionMail%>
                                                                  value="Y">
                            <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                            <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                            <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                            <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                            <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                        </tr>
                    </table>

                </div>

                --%>
        <%--<div class="reporttable" style="margin-bottom: 9px;">--%>
            <table border="0" width="100%" align="center" class="table table-striped table-bordered  table-green dataTable"
                   style="margin-bottom: 0px">
                <tr class="td1">
                    <td colspan="8" style="height: 30px">
                        <center><b>Fraud Mails</b></center>
                    </td>
                </tr>
                </thead>
                <%
                    String highRiskRefunds="";
                    String fraudFailedTxn="";
                    String dailyFraudReport="";
                    if("Y".equals(temphash.get("highRiskRefunds")))
                        highRiskRefunds="checked";
                    if("Y".equals(temphash.get("fraudFailedTxn")))
                        fraudFailedTxn="checked";
                    if("Y".equals(temphash.get("dailyFraudReport")))
                        dailyFraudReport="checked";
                %>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Main Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Sales Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Fraud Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Technical Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Billing Contact</td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> High Risk Refunds</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td align="center" class="tr0"><input type="checkbox" name='highRiskRefunds' <%=highRiskRefunds%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'High Risk Refunds')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Fraud Failed Transaction</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td align="center" class="tr0"><input type="checkbox" name='fraudFailedTxn' <%=fraudFailedTxn%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Fraud Failed Transaction')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Daily Fraud Report</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td align="center" class="tr0"><input type="checkbox" name='dailyFraudReport' <%=dailyFraudReport%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Daily Fraud Report')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
            </table>
            <br><br>
        <%--</div>--%>

        <%--<div class="reporttable" style="margin-bottom: 9px;">--%>
            <table border="0" width="100%" align="center" class="table table-striped table-bordered  table-green dataTable"
                   style="margin-bottom: 0px">
                <tr class="td1">
                    <td colspan="8" style="height: 30px">
                        <center><b>Recon Mails</b></center>
                    </td>
                </tr>
                </thead>
                <%
                    String successReconMail="";
                    String refundReconMail="";
                    String chargebackReconMail="";
                    String payoutReconMail="";
                    if("Y".equals(temphash.get("successReconMail")))
                        successReconMail="checked";
                    if("Y".equals(temphash.get("refundReconMail")))
                        refundReconMail="checked";
                    if("Y".equals(temphash.get("chargebackReconMail")))
                        chargebackReconMail="checked";
                    if("Y".equals(temphash.get("payoutReconMail")))
                        payoutReconMail="checked";
                %>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Event Name</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Main Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Sales Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Fraud Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Chargeback Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Refund Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Technical Contact</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Billing Contact</td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Success Transaction</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td align="center" class="tr0"><input type="checkbox" name='successReconMail' <%=successReconMail%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Success Trnasaction')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Refund Transaction</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td align="center" class="tr0"><input type="checkbox" name='refundReconMail' <%=refundReconMail%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Refund Transaction')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> ChargebackReversed/Casefiling Transaction</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td align="center" class="tr0"><input type="checkbox" name='chargebackReconMail' <%=chargebackReconMail%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Chargeback Transaction')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
                <tr>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> Payout Transaction</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td align="center" class="tr0"><input type="checkbox" name='payoutReconMail' <%=payoutReconMail%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Payout Recon')">
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                    <td style="height: 30px" valign="middle" align="center" class="tr0"> <input type="checkbox" disabled></td>
                </tr>
            </table>
            <br><br>
        <%--</div>--%>
    </div>


    <div class="reporttable" style="margin-bottom: 9px;">
       <%-- <div class="reporttable" style="margin-bottom: 9px;">--%>
            <table border="0" width="100%" align="center" class="table table-striped table-bordered  table-green dataTable"
                   style="margin-bottom: 0px">
                <tr class="th0">
                    <td colspan="7" style="height: 30px">
                        <center><b>Customer Email Notification Settings</b></center>
                    </td>
                </tr>
            </table>
            <table border="0" width="100%" class="table table-striped table-bordered  table-green dataTable"
                   style="margin-bottom: 0px">
                <tr class="td1">
                    <td colspan="8" style="height: 30px">
                        <center><b>Customer Transaction Mails</b></center>
                    </td>
                </tr>
                </thead>
                <%
                    String customerTransactionSuccessfulMail="";
                    String customerTransactionFailMail="";
                    String customerTransactionPayoutSuccess="";
                    String customerTransactionPayoutFail="";
                    if("Y".equals(temphash.get("customerTransactionSuccessfulMail")))
                        customerTransactionSuccessfulMail="checked";
                    if("Y".equals(temphash.get("customerTransactionFailMail")))
                        customerTransactionFailMail="checked";
                    if("Y".equals(temphash.get("customerTransactionPayoutSuccess")))
                        customerTransactionPayoutSuccess="checked";
                    if("Y".equals(temphash.get("customerTransactionPayoutFail")))
                        customerTransactionPayoutFail="checked";
                %>
                <tr>
                    <td style="height: 30px; width: 50%" valign="middle" align="center" class="tr1"> Event Name</td>
                    <td style="height: 30px; width: 50%" valign="middle" align="center" class="tr1"> Customer</td>
                </tr>
                <tr>
                    <td style="height: 30px; width: 50%" valign="middle" align="center" class="tr0"> Successful</td>
                    <td align="center" class="tr0"><input type="checkbox" name='customerTransactionSuccessfulMail' <%=customerTransactionSuccessfulMail%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Customer Transaction Successful Mail')">
                </tr>
                <tr>
                    <td style="height: 30px; width: 50%" valign="middle" align="center" class="tr0"> Failed</td>
                    <td align="center" class="tr0"><input type="checkbox" name='customerTransactionFailMail' <%=customerTransactionFailMail%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Customer Transaction Failed Mail')">
                </tr>
                <tr>
                    <td style="height: 30px; width: 50%" valign="middle" align="center" class="tr0"> Payout Successful</td>
                    <td align="center" class="tr0"><input type="checkbox" name='customerTransactionPayoutSuccess' <%=customerTransactionPayoutSuccess%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Customer Transaction Payout Successful Mail')">
                </tr>
                <tr>
                    <td style="height: 30px; width: 50%" valign="middle" align="center" class="tr0"> Payout Fail</td>
                    <td align="center" class="tr0"><input type="checkbox" name='customerTransactionPayoutFail' <%=customerTransactionPayoutFail%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Customer Transaction Payout Fail Mail')">
                </tr>
            </table>
           <br><br>
        <%--</div>--%>
       <%--<div class="reporttable" style="margin-bottom: 9px;" align="center">--%>
            <table border="0" width="100%"  class="table table-striped table-bordered  table-green dataTable"
                   style="margin-bottom: 0px">
                <tr class="td1">
                    <td colspan="8" style="height: 30px">
                        <center><b>Customer Refund Mails</b></center>
                    </td>
                </tr>
                </thead>
                <%
                    String customerRefundMail="";
                    if("Y".equals(temphash.get("customerRefundMail")))
                        customerRefundMail="checked";
                %>
                <tr>
                    <td style="height: 30px;width: 50%" valign="middle" align="center" class="tr1"> Event Name</td>
                    <td style="height: 30px; width: 50%" valign="middle" align="center" class="tr1"> Customer</td>
                </tr>
                <tr>
                    <td style="height: 30px; width: 50%" valign="middle" align="center" class="tr0"> Refunds</td>
                    <td align="center" class="tr0"><input type="checkbox" name='customerRefundMail' <%=customerRefundMail%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Customer Refunds Mail')">
                </tr>
            </table>
           <br><br>
        <%--</div>--%>
       <%-- <div class="reporttable" style="margin-bottom: 9px;">--%>
            <table border="0" width="100%"  class="table table-striped table-bordered  table-green dataTable"
                   style="margin-bottom: 0px">
                <tr class="td1">
                    <td colspan="8" style="height: 30px">
                        <center><b>Customer Tokenization Mails</b></center>
                    </td>
                </tr>
                </thead>
                <%
                    String customerTokenizationMail="";
                    if("Y".equals(temphash.get("customerTokenizationMail")))
                        customerTokenizationMail="checked";
                %>
                <tr>
                    <td style="height: 30px;width: 50%" valign="middle" align="center" class="tr1"> Event Name</td>
                    <td style="height: 30px" valign="middle" align="center" class="tr1"> Customer</td>
                </tr>
                <tr>
                    <td style="height: 30px; width: 50%" valign="middle" align="center" class="tr0"> Card Registration</td>
                    <td align="center" class="tr0"><input type="checkbox" name='customerTokenizationMail' <%=customerTokenizationMail%>
                                                          value="Y" onchange="ChangeFunction(CHECK(this),'Customer Tokenization Card Registration Mail')">
                </tr>
            </table>
           <br><br>
        <%--</div>--%>
    </div>


    <%
        }
    %>
    <table align="center">
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
    }
    else
    {
        out.println(Functions.NewShowConfirmation("Sorry", "No records found."));
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