<%@ page import="com.directi.pg.Functions,
                org.owasp.esapi.ESAPI,
                org.owasp.esapi.errors.ValidationException" %>
<%@ page import="com.fraud.FraudSystemService" %>
<%@ page import="com.directi.pg.Logger" %>
<%@ page import="java.util.*" %>
<%@ include file="index.jsp" %>
<%!
    private static Logger logger=new Logger("fraudTransactionDetails.jsp");
%>
<%
    Hashtable statushash = new Hashtable();
    statushash.put("Pending", "Pending");
    statushash.put("Process Failed", "Process Failed");
    statushash.put("Process Successfully", "Process Successfully");

    Hashtable gatewayHash = FraudSystemService.getFraudSystem();
    String fsid =Functions.checkStringNull(request.getParameter("fsid"));
    if (fsid == null)
    {fsid = "";}


    String membertrandid = Functions.checkStringNull(request.getParameter("toid"));
    if (membertrandid == null)
        membertrandid = "";

    String fraudtransid = Functions.checkStringNull(request.getParameter("fstransid"));
    if (fraudtransid == null)
        fraudtransid = "";



    String trackingid = Functions.checkStringNull(request.getParameter("STrackingid"));
    if (trackingid == null)
        trackingid = "";

    String fstransstatus = Functions.checkStringNull(request.getParameter("fstransstatus"));
    String fdate=null;
    String tdate=null;
    String fmonth=null;
    String tmonth=null;
    String fyear=null;
    String tyear=null;
    try
    {
        fdate = ESAPI.validator().getValidInput("fdate",request.getParameter("fdate"),"Days",2,true);
        tdate = ESAPI.validator().getValidInput("tdate",request.getParameter("tdate"),"Days",2,true);
        fmonth = ESAPI.validator().getValidInput("fmonth",request.getParameter("fmonth"),"Months",2,true);
        tmonth =  ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
        fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
        tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);
    }
    catch(ValidationException e)
    {
         logger.error("Date Format Exception",e);
    }
    Calendar rightNow = Calendar.getInstance();
    String str = "";
    //rightNow.setTime(new Date());
    String currentyear=""+rightNow.get(rightNow.YEAR);
    if (fdate == null) fdate = "" + 1;
    if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
    if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
    if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
    if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
    if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);


    if (fdate != null) str = str + "fdate=" + fdate;
    if (tdate != null) str = str + "&tdate=" + tdate;
    if (fmonth != null) str = str + "&fmonth=" + fmonth;
    if (tmonth != null) str = str + "&tmonth=" + tmonth;
    if (fyear != null) str = str + "&fyear=" + fyear;
    if (tyear != null) str = str + "&tyear=" + tyear;
    if (trackingid != null) str = str + "&STrackingid=" + trackingid;
    if (membertrandid != null) str = str + "&toid=" + membertrandid;
    if (fstransstatus != null) str = str + "&fstransstatus=" + fstransstatus;
    if (fsid !=null) str = str + "&fsid=" + fsid;

    int pageno = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
    int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 30);

%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title>Fraud Transactions</title>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
    </script>

</head>
<body>
<%
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
        try
        {

%>
<form name="form" method="post" action="/icici/servlet/FraudTransactionList?ctoken=<%=ctoken%>" onsubmit="return validateccnum()">
<div class="row">
<div class="col-lg-12">
<div class="panel panel-default" >
<div class="panel-heading" >
    Fraud Transactions Interface
</div>
<input type="hidden" id="ctoken" value="<%=ctoken%>" name="ctoken">
<table  align="center" width="95%" cellpadding="2" cellspacing="2" style="margin-left:2.5%;margin-right: 2.5% ">
<tr>
<td>
<table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
    <tr><td colspan="4">&nbsp;</td></tr>
    <tr>
        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb" >From</td>
        <td width="3%" class="textb"></td>
        <td width="12%" class="textb">
            <select size="1" name="fdate" class="textb">
                <%
                    if (fdate != null)
                        out.println(Functions.dayoptions(1, 31, fdate));
                    else
                        out.println(Functions.printoptions(1, 31));
                %>
            </select>
            <select size="1" name="fmonth" class="textb">
                <%
                    if (fmonth != null)
                        out.println(Functions.newmonthoptions(1, 12, fmonth));
                    else
                        out.println(Functions.printoptions(1, 12));
                %>
            </select>
            <select size="1" name="fyear" class="textb">
                <%
                    if (fyear != null)
                        out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), fyear));
                    else
                        out.println(Functions.printoptions(2005, 2013));
                %>
            </select>
        </td>
        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb" >To</td>
        <td width="3%" class="textb"></td>
        <td width="12%" class="textb">
            <select size="1" name="tdate" class="textb">
                <%
                    if (tdate != null)
                        out.println(Functions.dayoptions(1, 31, tdate));
                    else
                        out.println(Functions.printoptions(1, 31));
                %>
            </select>
            <select size="1" name="tmonth" class="textb">
                <%
                    if (tmonth != null)
                        out.println(Functions.newmonthoptions(1, 12, tmonth));
                    else
                        out.println(Functions.printoptions(1, 12));
                %>
            </select>
            <select size="1" name="tyear"  class="textb">
                <%
                    if (tyear != null)
                        out.println(Functions.yearoptions(2005, Integer.parseInt(currentyear), tyear));
                    else
                        out.println(Functions.printoptions(2005, 2013));
                %>
            </select>
        </td>
        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb" >Fraud ID</td>
        <td width="3%" class="textb"></td>
        <td width="10%" class="textb">
            <input type=text name="fraudtransid" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(fraudtransid)%>" class="txtbox" size="20">
        </td>
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
        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb" >Member ID</td>
        <td width="3%" class="textb"></td>
        <td width="12%" class="textb">
            <input type=text name="toid" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(membertrandid)%>" class="txtbox" size="20">
        </td>

        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb">Status</td>
        <td width="3%" class="textb">&nbsp;</td>
        <td width="12%" class="textb">
            <select size="1" name="fstransstatus" class="txtbox">
                <option value="">All</option>
                <%
                    Enumeration enu = statushash.keys();
                    String selected = "";
                    String key = "";
                    String value = "";


                    while (enu.hasMoreElements())
                    {
                        key = (String) enu.nextElement();
                        value = (String) statushash.get(key);

                        if (key.equals(fstransstatus))
                            selected = "selected";
                        else
                            selected = "";

                %>
                <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key)%>" <%=selected%>><%=ESAPI.encoder().encodeForHTML(value)%></option>
                <%
                    }
                %>
            </select>
        </td>

        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb">Fraud System</td>
        <td width="3%" class="textb">&nbsp;</td>
        <td width="12%" class="textb">
            <select size="1" name="fsid" class="txtbox">
                <option value="">All</option>
                <%
                    Enumeration enu2 = gatewayHash.keys();
                    String selected2 = "";
                    String key2 = "";
                    String value2 = "";


                    while (enu2.hasMoreElements())
                    {
                        key2 = (String) enu2.nextElement();
                        value2 = (String) gatewayHash.get(key2);

                        if (key2.equals(fsid))
                            selected2 = "selected";
                        else
                            selected2 = "";

                %>
                <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key2)%>" <%=selected2%>><%=ESAPI.encoder().encodeForHTML(value2)%></option>
                <%
                    }
                %>
            </select>
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
        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb" colspan="2">Tracking ID</td>
        <td width="12%" class="textb">
            <input type="text" maxlength="10" value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>"  name="STrackingid" size="10" class="txtbox">
        </td>

        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb" ></td>
        <td width="3%" class="textb">&nbsp;</td>
        <td width="12%" class="textb">

        </td>
        <td width="4%" class="textb">&nbsp;</td>
        <td width="8%" class="textb" ></td>
        <td width="3%" class="textb">&nbsp;</td>
        <td width="12%" class="textb">
            <button type="submit" class="buttonform">
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

</div>
</div>
</div>
</form>
<div class="reporttable" style="margin-left: 360px;margin-right: 200px">
<table border="1" cellpadding="5" cellspacing="0" bordercolor="#ffffff" align="center" class="table table-striped table-bordered  table-green dataTable">
    <%
        Hashtable hash = (Hashtable) request.getAttribute("transactionsdetails");
        Hashtable fraudRuleHash = (Hashtable) hash.get("fraudRule");
        Hashtable innerhash = null;
        if (hash != null && hash.size() > 0)
        {
            String style = "class=tr0";

            innerhash = (Hashtable) hash.get(1 + "");
            int pos = 0;
            value = (String) innerhash.get("trackingid");
            if (value == null)
                value = "-";
    %>
    <thead>
        <tr <%=style%> >
            <td class="texthead" colspan="2" align="center">Fraud Transaction Details</td>
        </tr>
    </thead>
    <tr <%=style%>>
        <td class="td0" align="center">Tracking ID: </td>
        <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("fstransid");
        if (value == null)
            value = "-";
    %>
    <tr <%=style%>>
        <td class="td0" align="center">Fraud Trans ID: </td>
        <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("memberid");
        if (value == null)
            value = "-";
    %>
    <tr <%=style%>>
        <td class="td0" align="center">Member ID: </td>
        <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML((value))%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("member_transid");
        if (value == null)
            value = "-";
    %>
    <tr <%=style%>>
        <td class="td0" align="center">Member Trans ID: </td>
        <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML((value))%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("score");
        if (value == null)
            value = "-";
    %>
    <tr <%=style%> >
        <td class="td0" align="center">Score:</td>
        <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("fsid");
        if (value == null)
            value = "-";
    %>
    <tr <%=style%>>
        <td class="td0" align="center">Fraud System Name: </td>
        <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(FraudSystemService.getFSGateway(value))%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("amount");
        if (value == null)
            value = "-";
    %>
    <tr <%=style%>>
        <td class="td0" align="center">Transaction Amount: </td>
        <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML((value))%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("fraudtransstatus");
        if (value == null)
            value = "-";
    %>
    <tr <%=style%>>
        <td class="td0" align="center">Fraud Transaction Status: </td>
        <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("dtstamp");
        if (value == null)
            value = "-";
    %>
    <tr <%=style%>>
        <td class="td0" align="center">Date: </td>
        <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("fs_responsecode");
        if (value == null)
            value = "-";
    %>
    <tr <%=style%>>
        <td class="td0" align="center">Response Code </td>
        <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML((value))%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("fs_responsedesc");
        if (value == null)
            value = "-";
    %>
    <tr <%=style%>>
        <td class="td0" align="center">Response Description </td>
        <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML((value))%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("fs_responserec");
        if (value == null)
            value = "-";
    %>
    <tr <%=style%>>
        <td class="td0" align="center">Recommendation</td>
        <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML((value))%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("isAlertSent");
        if (value == null)
            value = "-";
    %>
    <tr <%=style%>>
        <td class="td0" align="center">Is AlertSent: </td>
        <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML((value))%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("isReversed");
        if (value == null)
            value = "-";
    %>
    <tr <%=style%>>
        <td class="td0" align="center">Is Reversed: </td>
        <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML((value))%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("checkstatus");
        if (value == null)
            value = "-";
    %>
    <tr <%=style%>>
        <td class="td0" align="center">Status: </td>
        <td class="td0" align="center"><%=ESAPI.encoder().encodeForHTML((value))%></td>
    </tr>
    <%
        }
    %>

</table>

        <table border="0" cellpadding="5" cellspacing="2" color="#ffffff" width="450" class="table table-striped table-bordered table-hover table-green dataTable" align="center">
            <tr align="center">
                <td class="th0" align="center" colspan="7">Fraud Rules Triggered</td>
            </tr>
            <tr>
                <td class="th0" align="center">Fraud Rule Name</td>
                <td class="th0" align="center">Fraud Rule Score</td>
            </tr>
            <%
                if(fraudRuleHash != null && fraudRuleHash.size() > 0)
                {
                    Set set = fraudRuleHash.keySet();
                    Iterator iterator = set.iterator();
                    while(iterator.hasNext())
                    {
                        String ruleName = (String)iterator.next();
            %>
            <tr>
                <td class="tr1" align="center"><%=ESAPI.encoder().encodeForHTML(ruleName)%></td>
                <td class="tr1" align="center"><%=ESAPI.encoder().encodeForHTML((String)fraudRuleHash.get(ruleName))%></td>
            </tr>
            <%
                }
            }
            else
            {
            %>
            <tr><td colspan="2" class="tr1" align="center">No Triggered Rules Found</td></tr>
            <%
                }
            %>
        </table>
    </div>

<%
        }
        catch (Exception e)
        {
         logger.error("Exception in fraudTransactionDetails",e);
        }

    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>

</body>
</html>