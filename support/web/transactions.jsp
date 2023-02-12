<%@ page
        import="com.directi.pg.Functions,com.directi.pg.core.GatewayAccount,com.directi.pg.core.GatewayAccountService,java.util.Calendar,
                org.owasp.esapi.ESAPI,
                org.owasp.esapi.errors.ValidationException" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>
<%@ page import="com.directi.pg.Logger"%>

<%!
    Logger log=new Logger("test1");

%>
<%
    Hashtable statushash = new Hashtable();

    statushash.put("begun", "Begun Processing");
    statushash.put("authstarted", "Auth Started");
    statushash.put("proofrequired", "Proof Required");
    statushash.put("authsuccessful", "Auth Successful");
    statushash.put("authfailed", "Auth Failed");
    statushash.put("capturestarted", "Capture Started");
    statushash.put("capturesuccess", "Capture Successful");
    statushash.put("capturefailed", "Capture Failed");
    statushash.put("podsent", "POD Sent ");
    statushash.put("settled", "Settled");
    statushash.put("markedforreversal", "Reversal Request Sent");
    statushash.put("reversed", "Reversed");
    statushash.put("chargeback", "Chargeback");
    statushash.put("failed", "Cancelled by Customer");
    statushash.put("cancelled", "Cancelled Transaction");
    statushash.put("authcancelled", "Authorisation Cancelled");

    String desc = Functions.checkStringNull(request.getParameter("desc"));
    if (desc == null)
        desc = "";

    String amt = Functions.checkStringNull(request.getParameter("amount"));
    if (amt == null)
        amt = "";

    String emailaddr = Functions.checkStringNull(request.getParameter("emailaddr"));
    if (emailaddr == null)
        emailaddr = "";

    String orderdesc = Functions.checkStringNull(request.getParameter("orderdesc"));
    if (orderdesc == null)
        orderdesc = "";

    String name = Functions.checkStringNull(request.getParameter("name"));
    if (name == null)
        name = "";

    String trackingid = Functions.checkStringNull(request.getParameter("STrackingid"));
    if (trackingid == null)
        trackingid = "";

    String toid = Functions.checkStringNull(request.getParameter("toid"));
    if (toid == null)
        toid = "";

    String firstfourofccnum = Functions.checkStringNull(request.getParameter("firstfourofccnum"));
    String lastfourofccnum = Functions.checkStringNull(request.getParameter("lastfourofccnum"));
    //String ccnum=Functions.checkStringNull(request.getParameter("ccnum"));
    if (firstfourofccnum == null)
        firstfourofccnum = "";
    if (lastfourofccnum == null)
        lastfourofccnum = "";
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
            tmonth = ESAPI.validator().getValidInput("tmonth",request.getParameter("tmonth"),"Months",2,true);
            fyear = ESAPI.validator().getValidInput("fyear",request.getParameter("fyear"),"Years",4,true);
            tyear = ESAPI.validator().getValidInput("tyear",request.getParameter("tyear"),"Years",4,true);
    }
    catch(ValidationException e)
    {

    }
    String status = Functions.checkStringNull(request.getParameter("status"));


    Calendar rightNow = Calendar.getInstance();
    String str = "";
    //rightNow.setTime(new Date());
    if (fdate == null) fdate = "" + 1;
    if (tdate == null) tdate = "" + rightNow.get(rightNow.DATE);
    if (fmonth == null) fmonth = "" + rightNow.get(rightNow.MONTH);
    if (tmonth == null) tmonth = "" + rightNow.get(rightNow.MONTH);
    if (fyear == null) fyear = "" + rightNow.get(rightNow.YEAR);
    if (tyear == null) tyear = "" + rightNow.get(rightNow.YEAR);

    if (fdate != null) str = str + "&fdate=" + fdate;
    if (tdate != null) str = str + "&tdate=" + tdate;
    if (fmonth != null) str = str + "&fmonth=" + fmonth;
    if (tmonth != null) str = str + "&tmonth=" + tmonth;
    if (fyear != null) str = str + "&fyear=" + fyear;
    if (tyear != null) str = str + "&tyear=" + tyear;
    if (desc != null) str = str + "&desc=" + desc;
    if (amt != null) str = str + "&amount=" + amt;
    if (emailaddr != null) str = str + "&emailaddr=" + emailaddr;
    if (name != null) str = str + "&name=" + name;
    if (trackingid != null) str = str + "&STrackingid=" + trackingid;
    if (orderdesc != null) str = str + "&orderdesc=" + orderdesc;
    if (toid != null) str = str + "&toid=" + toid;
    if (status != null) str = str + "&status=" + status;
    if (firstfourofccnum != null) str = str + "&firstfourofccnum=" + firstfourofccnum;
    if (lastfourofccnum != null) str = str + "&lastfourofccnum=" + lastfourofccnum;
    firstfourofccnum=null;
    lastfourofccnum=null;
    boolean archive = Boolean.valueOf(request.getParameter("archive")).booleanValue();
    str = str + "&archive=" + archive;
    String archivalString = "Archived";
    String currentString = "Current";
    String selectedArchived, selectedCurrent;
    if (archive)
    {
        selectedArchived = "selected";
        selectedCurrent = "";
    }
    else
    {
        selectedArchived = "";
        selectedCurrent = "selected";
    }

    int pageno = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
    int pagerecords = Functions.convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 30);

%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title>Transactions</title>
    <script language="javascript">
        function isint(form)
        {
            if (isNaN(form.numrows.value))
                return false;
            else
                return true;
        }
        function validateccnum()
        {

            var firstfourofccnum = document.form.firstfourofccnum.value;
            var lastfourofccnum= document.form.lastfourofccnum.value;
            if(firstfourofccnum.length==0 && lastfourofccnum.length==0 )
                return true;
            if(firstfourofccnum.length<4)
            {

                 alert("Enter first four of ccnum");
                return false;
            }

            if( lastfourofccnum.length<4)
            {
                 alert("Enter last four of ccnum");
                return false;
             }

        }
    </script>
    <link rel="stylesheet" type="text/css" href="/merchant/style.css"/>
</head>

<body>
<p align="center" class="title"><%=archive ? archivalString : currentString%> Transactions Details</p>
<br>
<%
    try
    {

%>
<form name="form" method="post" action="TransactionDetails" onsubmit="return validateccnum()">
<table border="0" cellpadding="4" cellspacing="0" width="750" align="center">
<tr class="label">
    <td valign="middle" align="left" bgcolor="#9A1305" width="28%">
        From:<select size="1" name="fdate" class="textBoxes">
        <%
            if (fdate != null)
                out.println(Functions.dayoptions(1, 31, fdate));
            else
                out.println(Functions.printoptions(1, 31));
        %>
    </select>
        <select size="1" name="fmonth" class="textBoxes">
            <%
                if (fmonth != null)
                    out.println(Functions.newmonthoptions(1, 12, fmonth));
                else
                    out.println(Functions.printoptions(1, 12));
            %>
        </select>
        <select size="1" name="fyear" class="textBoxes">
            <%
                int currentYear = Calendar.getInstance().get(Calendar.YEAR);
                if (fyear != null)
                    out.println(Functions.yearoptions(2000, currentYear, fyear));
                else
                    out.println(Functions.printoptions(2000, currentYear));
            %>
        </select>
    </td>
    <td valign="middle" align="left" bgcolor="#9A1305" class="label">
        Tracking ID:<br>
        <input type="text" maxlength="10"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(trackingid)%>"  name="STrackingid" size="10" class="textBoxes">
    </td>
    <td valign="middle" align="center" bgcolor="#9A1305" class="label">Order Description:
        <input type=text name="orderdesc" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(orderdesc)%>" class="textBoxes" size="20">
    </td>

    <td valign="middle" align="left" bgcolor="#9A1305" class="label">
        Status:
        <select size="1" name="status" class="textBoxes">
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

                    if (key.equals(status))
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

    <td valign="middle" align="left" bgcolor="#9A1305" class="label" width="20%">Data Source :
        <select size="1" name="archive" class="textBoxes">
            <option value="true" <%=ESAPI.encoder().encodeForHTML(selectedArchived)%>>Archives</option>
            <option value="false" <%=ESAPI.encoder().encodeForHTML(selectedCurrent)%>>Current</option>
        </select>
    </td>

</tr>
<tr class="label">
    <td valign="middle" align="left" bgcolor="#9A1305" width="28%">
        To:
        <select size="1" name="tdate" class="textBoxes">

            <%
                if (tdate != null)
                    out.println(Functions.dayoptions(1, 31, tdate));
                else
                    out.println(Functions.printoptions(1, 31));
            %>
        </select>

        <select size="1" name="tmonth" class="textBoxes">

            <%
                if (tmonth != null)
                    out.println(Functions.newmonthoptions(1, 12, tmonth));
                else
                    out.println(Functions.printoptions(1, 12));
            %>
        </select>

        <select size="1" name="tyear" class="textBoxes">

            <%
                if (tyear != null)
                    out.println(Functions.yearoptions(2000, currentYear, tyear));
                else
                    out.println(Functions.printoptions(2000, currentYear));
            %>
        </select>
    </td>

    <td valign="middle" align="left" bgcolor="#9A1305" class="label">Name:
        <input type=text name="name" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(name)%>" class="textBoxes" size="10">
    </td>


    <td valign="middle" align="center" bgcolor="#9A1305" class="label">
        Description:
        <input type=text name="desc" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(desc)%>" class="textBoxes" size="20">
    </td>
    <td valign="middle" align="left" bgcolor="#9A1305" class="label">
        <input type=checkbox name="perfectmatch" value="yes">Show Perfect Match
    </td>

    <td valign="middle" align="center" bgcolor="#9A1305" class="label" rowspan="2">
        <input type="submit" value="Submit" class="button">
    </td>
</tr>
<tr class="label">
    <td valign="middle" align="center" bgcolor="#9A1305" class="label">
        Merchant ID:
        <input type=text name="toid" maxlength="10"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(toid)%>" class="textBoxes" size="4">
    </td>
    <td valign="middle" align="left" bgcolor="#9A1305" class="label">
        Amount:
        <input type=text name="amount" maxlength="10"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(amt)%>" class="textBoxes" size="10">
    </td>
    <td valign="middle" align="center" bgcolor="#9A1305" class="label">
        Card Number:<br>
        <input type=text  name="firstfourofccnum" maxlength="6" size="5" ><input type=text name="lastfourofccnum" maxlength="4" size="4" ><br>(Enter First six & Last four digit of Credit Card)
    </td>
    <td valign="middle" align="left" bgcolor="#9A1305" class="label">
        Email Address:
        <input type=text name="emailaddr" maxlength="50"  value="<%=ESAPI.encoder().encodeForHTMLAttribute(emailaddr)%>" class="textBoxes" size="20">
    </td>
</tr>
</table>
<br>

<%

    Hashtable hash = (Hashtable) request.getAttribute("transactionsdetails");
    Hashtable temphash = null;
    //out.println(hash);


    int records = 0;
    int totalrecords = 0;
    int currentblock = 1;

    try
    {
        records = Integer.parseInt((String) hash.get("records"));
        totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
        currentblock = Integer.parseInt(request.getParameter("currentblock"));
    }
    catch (Exception ex)
    {
        log.error("parse Exception while getting transactions",ex);
    }

    String style = "class=td0";
    String ext = "light";


    if (records > 0)
    {
%>
<table border="1" cellpadding="5" cellspacing="0" width="750" bordercolor="#ffffff" align="center">
    <tr>
        <td width="14%" class="th0">Date</td>
        <td width="10%" class="th1">Tracking ID</td>
        <td width="17%" class="th0">Description</td>
        <td width="20%" class="th1">Order Description</td>
        <td width="15%" class="th0">Card Holder's Name</td>
        <td width="10%" class="th1">Amount</td>
        <td width="17%" class="th0">Status</td>
        <td width="12%" class="th0">HRCode</td>
    </tr>
    <%

        for (int pos = 1; pos <= records; pos++)
        {
            String id = Integer.toString(pos);

            int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

            style = "class=\"tr" + pos % 2 + "\"";

            temphash = (Hashtable) hash.get(id);

            String date = Functions.convertDtstamptoDate((String) temphash.get("dtstamp"));
            String description = (String) temphash.get("description");
            String orderdescription = (String) temphash.get("orderdescription");
            String icicitransid = (String) temphash.get("icicitransid");
            String custname = (String) temphash.get("name");
            String hrcode = (String) temphash.get("hrcode");
            if (hrcode == null) hrcode = "-";
            if (custname == null) custname = "-";
            String currency = null;
            String accountId = (String) temphash.get("accountid");
            if (accountId != null)
            {
                GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
                currency = account.getCurrency();
            }
            String amount = currency + " " + (String) temphash.get("amount");
            String tempstatus = (String) statushash.get((String) temphash.get("status"));
            out.println("<tr " + style + ">");
            out.println("<td >" + date + "</td>");
            out.println("<td ><a href=\"TransactionDetails?STrackingid=" + ESAPI.encoder().encodeForHTMLAttribute(icicitransid) + "&archive=" + archive + "\">" + icicitransid + "</a></td>");
            out.println("<td align=\"left\">" + ESAPI.encoder().encodeForHTML(description) + "</td>");
            out.println("<td align=\"left\">" + ESAPI.encoder().encodeForHTML(orderdescription) + "</td>");
            out.println("<td align=\"left\">" + ESAPI.encoder().encodeForHTML(custname) + "</td>");
            out.println("<td align=\"right\">" + ESAPI.encoder().encodeForHTML(amount) + "</td>");
            out.println("<td align=\"left\">" + ESAPI.encoder().encodeForHTML(tempstatus) + "</td>");
            out.println("<td align=\"left\">" + ESAPI.encoder().encodeForHTML(hrcode) + "</td>");
            out.println("</tr>");

        }
    %>
    <tr><td colspan=3 align="left" class=textb>Total Records: <%=totalrecords%></td><td colspan=3 align="right"
                                                                                        class=textb>Page
        No <%=pageno%></td></tr>
</table>
</form>


<table align=center valign=top><tr>
    <td align=center>
        <jsp:include page="page.jsp" flush="true">
            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
            <jsp:param name="numrows" value="<%=pagerecords%>"/>
            <jsp:param name="pageno" value="<%=pageno%>"/>
            <jsp:param name="str" value="<%=ESAPI.encoder().encodeForURL(str)%>"/>
            <jsp:param name="page" value="TransactionDetails"/>
            <jsp:param name="currentblock" value="<%=currentblock%>"/>
            <jsp:param name="orderby" value=""/>
        </jsp:include>
    </td>

</table>
<%
        }
        else
        {
            out.println(Functions.ShowConfirmation("Sorry", "No records found.<br><br>Date :<br>From " + fdate + "/" + (Integer.parseInt(fmonth) + 1) + "/" + fyear + "<br>To " + tdate + "/" + (Integer.parseInt(tmonth) + 1) + "/" + tyear));
        }
    }
    catch (Exception e)
    {
        log.error("Exception while getting displaying  transactions ",e);

    }
%>
<p>&nbsp;&nbsp; </p>
</body>

</html>
