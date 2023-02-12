<%@ page
        import="com.directi.pg.Functions,com.directi.pg.core.GatewayAccount,com.directi.pg.core.GatewayAccountService,java.util.Calendar,
                org.owasp.esapi.ESAPI" %>
<%@ page import="java.util.Enumeration" %>
<%@ page import="java.util.Hashtable" %>

<style>
    .td0 {
        color: #7D0000;
        background: #F1EDE0;
        text-align: left;
        font-family: verdana, arial;
        font-size: 12px;
        FONT-WEIGHT: bold;
    }

    .td1 {
        color: #7D0000;
        background: #E4D6C9;
        text-align: left;
        font-family: verdana, arial;
        font-size: 12px;
        FONT-WEIGHT: bold
    }
</style>
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

    String trackingid = Functions.checkStringNull(request.getParameter("STrackingid"));
    if (trackingid == null)
        trackingid = "";

    boolean archive = Boolean.valueOf(request.getParameter("archive")).booleanValue();
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


    String fdate = null, tdate = null, fmonth = null, tmonth = null, fyear = null, tyear = null, status = "";


    Calendar rightNow = Calendar.getInstance();

    //rightNow.setTime(new Date());
    fdate = "" + 1;
    tdate = "" + rightNow.get(rightNow.DATE);
    fmonth = "" + rightNow.get(rightNow.MONTH);
    tmonth = "" + rightNow.get(rightNow.MONTH);
    fyear = "" + rightNow.get(rightNow.YEAR);
    tyear = "" + rightNow.get(rightNow.YEAR);

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
        <input type="text" value="" name="STrackingid" size="10" class="textBoxes">
    </td>
    <td valign="middle" align="center" bgcolor="#9A1305" class="label">Order Description:
        <input type=text name="orderdesc" value="" class="textBoxes" size="20">
    </td>

    <td valign="middle" align="left" bgcolor="#9A1305" class="label">
        Status:
        <select size="1" name="status" class="textBoxes">
            <option value="">All</option>
            <%
                Enumeration enu = statushash.keys();
                String key = "";
                String value = "";


                while (enu.hasMoreElements())
                {
                    key = (String) enu.nextElement();
                    value = (String) statushash.get(key);

            %>
            <option value="<%=ESAPI.encoder().encodeForHTMLAttribute(key)%>"><%=ESAPI.encoder().encodeForHTML(value)%></option>
            <%
                }
            %>
        </select>
    </td>

    <td valign="middle" align="left" bgcolor="#9A1305" class="label" width="20%">Data Source :
        <select size="1" name="archive" class="textBoxes">
            <option value="true" <%=selectedArchived%>>Archives</option>
            <option value="false" <%=selectedCurrent%>>Current</option>
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
        <input type=text name="name" value="" class="textBoxes" size="10">
    </td>


    <td valign="middle" align="center" bgcolor="#9A1305" class="label">
        Description:
        <input type=text name="desc" value="" class="textBoxes" size="20">
    </td>
    <td valign="middle" align="left" bgcolor="#9A1305" class="label">
        <input type=checkbox name="perfectmatch" value="yes">Show Perfect Match
    </td>
    <td valign="middle" align="center" bgcolor="#9A1305" class="label" rowspan="3">
        <input type="submit" value="Submit" class="button">
    </td>

</tr>
<tr class="label">
    <td valign="middle" align="center" bgcolor="#9A1305" class="label">
        Merchant ID:
        <input type=text name="toid" value="" class="textBoxes" size="4">
    </td>
    <td valign="middle" align="left" bgcolor="#9A1305" class="label">
        Amount:
        <input type=text name="amount" value="" class="textBoxes" size="10">
    </td>
    <td valign="middle" align="center" bgcolor="#9A1305" class="label">
        Card Number:<br>
        <input type=text  name="firstfourofccnum" maxlength="6" size="5" ><input type=text name="lastfourofccnum" maxlength="4" size="4" ><br>(Enter First six & Last four digit of Credit Card)
    </td>
    <td valign="middle" align="left" bgcolor="#9A1305" class="label">
        Email Address:
        <input type=text name="emailaddr" value="" class="textBoxes" size="20">
    </td>
</tr>
</table>
</form>
<br>
<table border="1" cellpadding="5" cellspacing="0" bordercolor="#ffffff" align="center">
<%

    Hashtable hash = (Hashtable) request.getAttribute("transactionsdetails");
    Hashtable innerhash = null;
    //out.println(hash);
    if (hash != null && hash.size() > 0)
    {
        String style = "class=tr0";

        innerhash = (Hashtable) hash.get(1 + "");
        int pos = 0;
        value = (String) innerhash.get("Tracking ID");

%>
<tr <%=style%>  >
    <td class="td1" colspan="2"><U>Transaction Details:</U></td>
</tr>
<tr <%=style%>  >
    <td class="td0">Tracking ID: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>
<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("Description");
    if (value == null)
        value = "-";


%>
<tr <%=style%> >
    <td class="td0">Description: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>
<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("Order Description");
    if (value == null)
        value = "-";


%>
<tr <%=style%> >
    <td class="td0">Order Description: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>

<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    String currency = null;

    String accountId = (String) innerhash.get("accountid");
    if (accountId != null)
    {
        GatewayAccount account = GatewayAccountService.getGatewayAccount(accountId);
        currency = account.getCurrency();
    }
    value = (String) innerhash.get("Transaction Amount");
    if (value == null)
        value = "-";
    else
        value = currency + " " + value;

%>
<tr <%=style%> >
    <td class="td0">Transaction Amount: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>


<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("Captured Amount");
    if (value == null)
        value = "-";
    else
        value = currency + " " + value;


%>
<tr <%=style%> >
    <td class="td0">Captured Amount: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>

<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("Refund Amount");
    if (value == null)
        value = "-";
    else
        value = currency + " " + value;


%>
<tr <%=style%> >
    <td class="td0">Refund Amount: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>
<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("Chargeback Amount");
    if (value == null)
        value = "-";
    else
        value = currency + " " + value;


%>
<tr <%=style%> >
    <td class="td0">Chargeback Amount: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>

<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("Date of transaction");
    if (value == null)
        value = "-";


%>
<tr <%=style%> >
    <td class="td0">Date of transaction: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>
<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("Last update");
    if (value == null)
        value = "-";


%>
<tr <%=style%> >
    <td class="td0">Last update: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>

<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("Status");
    if (value == null)
        value = "-";


%>
<tr <%=style%> >
    <td class="td0">Status: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>

<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("Auth Response Description");
    if (value == null)
        value = "-";


%>
<tr <%=style%> >
    <td class="td0">Auth Response Description: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>

<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("HRCode");
    if (value == null)
        value = "-";


%>
<tr <%=style%> >
    <td class="td0">HRCode: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>

<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("Cardholder's Name");
    if (value == null)
        value = "-";


%>
<tr>
    <td colspan="2"><img src="/merchant/point.gif" height="1"/></td>
</tr>
<tr <%=style%>  >
    <td class="td1" colspan="2"><U>Customer's Details:</U></td>
</tr>

<tr <%=style%> >
    <td class="td0">Cardholder's Name: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>
<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("ccnum");
    if (value == null)
        value = "-";


%>
<tr <%=style%> >
    <td class="td0">Card Number: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>
<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("Expiry date");
    if (value == null)
        value = "-";


%>
<tr <%=style%> >
    <td class="td0">Expiry Date: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>

<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("Customer's Emailaddress");
    if (value == null)
        value = "-";


%>
<tr <%=style%> >
    <td class="td0">Customer's Emailaddress: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>
<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("City");
    if (value == null)
        value = "-";


%>
<tr <%=style%> >
    <td class="td0">City: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>
<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("Street");
    if (value == null)
        value = "-";


%>
<tr <%=style%> >
    <td class="td0">Street: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>
<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("State");
    if (value == null)
        value = "-";


%>
<tr <%=style%> >
    <td class="td0">State: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>

<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("Country");
    if (value == null)
        value = "-";


%>
<tr <%=style%> >
    <td class="td0">Country: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>
<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("Name of Merchant");
    if (value == null)
        value = "-";


%>
<tr>
    <td colspan="2"><img src="/merchant/point.gif" height="1"/></td>
</tr>
<tr <%=style%>  >
    <td class="td1" colspan="2"><U>Merchant's Details:</U></td>
</tr>

<tr <%=style%> >
    <td class="td0">Member ID: </td>
    <td class="td1"><%=(String) innerhash.get("memberid")%></td>
</tr>

<tr <%=style%> >
    <td class="td0">Name of Merchant: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>

<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("Contact person");
    if (value == null)
        value = "-";


%>
<tr <%=style%> >
    <td class="td0">Contact person: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>

<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("Merchant's Emailaddress");
    if (value == null)
        value = "-";


%>
<tr <%=style%> >
    <td class="td0">Merchant's Emailaddress: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>


<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("Site URL");
    if (value == null)
        value = "-";


%>
<tr <%=style%> >
    <td class="td0">Site URL: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>


<%
    pos++;
    style = "class=\"tr" + pos % 2 + "\"";
    value = (String) innerhash.get("Merchant's telephone Number");
    if (value == null)
        value = "-";


%>
<tr <%=style%> >
    <td class="td0">Merchant's Telephone Number: </td>
    <td class="td1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
</tr>


<%       value=null;
        }
        else
        {
            out.println(Functions.ShowConfirmation("Sorry", "No records found for Tracking Id :" + trackingid));
        }

    }
    catch (Exception e)
    {
        log.error("Exception while getting the transaction details",e);

    }
%>
</table>

<p>&nbsp;&nbsp; </p>
</body>

</html>
