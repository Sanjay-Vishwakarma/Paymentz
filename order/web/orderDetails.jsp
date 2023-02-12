<%@ page import="com.directi.pg.Functions,java.util.Hashtable" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="ietest.jsp" %>
<%@ page import="org.owasp.esapi.User" %>
<%String company = (String)session.getAttribute("company");%>

<%
    User user =  (User)session.getAttribute("Anonymous");

    String ctoken= null;
    if(user!=null)
    {
        ctoken = user.getCSRFToken();

    }

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
    statushash.put("failed", "Validation Failed");
    statushash.put("cancelled", "Cancelled Transaction");
    statushash.put("authcancelled", "Authorisation Cancelled");

    String trackingid = Functions.checkStringNull(request.getParameter("STrackingid"));
    if (trackingid == null)
        trackingid = "";

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
    </script>

    <script src='/order/css/menu.js'></script>
    <script type='text/javascript' src='/order/css/menu_jquery.js'></script>
    <link rel="stylesheet"href="/order/css/styles123.css" type="text/css">
    <script src='/order/css/bootstrap.min.js'></script>
    <link href="/order/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet">
</head>

<body class="bodybackground">
<table border="0" cellpadding="4" cellspacing="" width="100%" bordercolor="#ffffff" align="center">
    <tr>
        <td align="center" >
            <img src="/images/order/<%=session.getAttribute("logo")%>" border="0">
        </td>
    </tr>
</table>

<p class="title" align="center" ><%=company%> Customer Order Tracking System</p>
<center><form action="/order/index.jsp" method="post"><input type="hidden" name="ctoken" value="<%=ctoken%>"><input type="hidden" name="partnerid" value="<%=session.getAttribute("partnerid")%>"><input type="hidden" name="fromtype" value="<%=session.getAttribute("company")%>"><input type="submit" value="HOME"></form></center>
<p>
<table border="0" width="15%" align="center" cellpadding="4" cellspacing="">
    <tr><td>&nbsp;</td></tr>
    <tr align="center" >
        <td class="subtitle" align="center" >Transaction Details</td>
    </tr>
</table>
</p>
<hr color="#007ACC" width="100%" align="center" size="1"><br><br>

<table border="0" cellpadding="5" cellspacing="2" bordercolor="#ffffff" align="center">
    <%
        try
        {
            Hashtable hash = (Hashtable) request.getAttribute("transactionsdetails");
            Hashtable innerhash = null;
            String value = "";
            //out.println(hash);
            if (hash != null)
            {
                String style = "class=tr0";

                innerhash = (Hashtable) hash.get(1 + "");
                int pos = 0;
                value = (String) innerhash.get("Tracking ID");

    %>
    <tr <%=style%>  >
        <td class="th1" colspan="2"><U>Transaction Details:</U></td>
    </tr>
    <tr <%=style%>  >
        <td class="tr0">Tracking ID: </td>
        <td class="tr1" align="center"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr"+ pos % 2 + "\"";
        value = (String) innerhash.get("Description");
        if (value == null)
            value = "-";

    %>
    <tr <%=style%> >
        <td class="tr0">Description: </td>
        <td class="tr1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Order Description");
        if (value == null)
            value = "-";

    %>
    <tr <%=style%> >
        <td class="tr0">Order Description: </td>
        <td class="tr1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>

    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Transaction Amount");
        if (value == null)
            value = "-";

    %>
    <tr <%=style%> >
        <td class="tr0">Transaction Amount: </td>
        <td class="tr1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>

    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Captured Amount");
        if (value == null)
            value = "-";

    %>
    <tr <%=style%> >
        <td class="tr0">Captured Amount: </td>
        <td class="tr1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>

    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Refund\\Chargeback Amount");
        if (value == null)
            value = "-";

    %>
    <tr <%=style%> >
        <td class="tr0">Refund\Chargeback Amount: </td>
        <td class="tr1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>

    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Date of transaction");
        if (value == null)
            value = "-";

    %>
    <tr <%=style%> >
        <td class="tr0">Date of transaction: </td>
        <td class="tr1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Last update");
        if (value == null)
            value = "-";

    %>
    <tr <%=style%> >
        <td class="tr0">Last update: </td>
        <td class="tr1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>

    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value =(String) innerhash.get("Status");
        if (value == null)
            value = "-";

    %>
    <tr <%=style%> >
        <td class="tr0">Status: </td>
        <td class="tr1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
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
        <td class="th1" colspan="2"><U>Customer's Details:</U></td>
    </tr>

    <tr <%=style%> >
        <td class="tr0">Name on Card: </td>
        <td class="tr1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("card");
        if (value == null)
            value = "-";

    %>
    <tr <%=style%> >
        <td class="tr0">Card Number: </td>
        <td class="tr1"><%=value%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Expiry date");
        if (value == null)
            value = "-";

    %>
    <tr <%=style%> >
        <td class="tr0">Expiry Date: </td>
        <td class="tr1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>

    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Customer's Emailaddress");
        if (value == null)
            value = "-";

    %>
    <tr <%=style%> >
        <td class="tr0">Customer's Emailaddress: </td>
        <td class="tr1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
    <%--
              pos++;
              style="class=\"tr"+pos%2+"\"";
              value=(String)innerhash.get("City");
              if(value==null)
              value="-";

    %>
        <tr <%=style%> >
          <td class="td0" >City: </td>
          <td  class="td1" ><%=value%></td>
        </tr>
    <%
              pos++;
              style="class=\"tr"+pos%2+"\"";
              value=(String)innerhash.get("Street");
              if(value==null)
              value="-";

    %>
        <tr <%=style%> >
          <td class="td0" >Street: </td>
          <td  class="td1" ><%=value%></td>
        </tr>
    <%
              pos++;
              style="class=\"tr"+pos%2+"\"";
              value=(String)innerhash.get("State");
              if(value==null)
              value="-";

    %>
        <tr <%=style%> >
          <td class="td0" >State: </td>
          <td  class="td1" ><%=value%></td>
        </tr>

    <%
         pos++;
         style="class=\"tr"+pos%2+"\"";
         value=(String)innerhash.get("Country");
         if(value==null)
         value="-";


    %>
        <tr <%=style%> >
          <td class="td0" >Country: </td>
          <td  class="td1" ><%=value%></td>
        </tr>   --%>
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
        <td class="th1" colspan="2"><U>Merchant's Details:</U></td>
    </tr>

    <tr <%=style%> >
        <td class="tr0">Name of Company: </td>
        <td class="tr1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>

    <%--
         pos++;
         style="class=\"tr"+pos%2+"\"";
         value=(String)innerhash.get("Contact person");
         if(value==null)
         value="-";

    %>
        <tr <%=style%> >
          <td class="td0" >Contact person: </td>
          <td  class="td1" ><%=value%></td>
        </tr>

    <%
         pos++;
         style="class=\"tr"+pos%2+"\"";
         value=(String)innerhash.get("Merchant's Emailaddress");
         if(value==null)
         value="-";


    %>
        <tr <%=style%> >
          <td class="td0" >Merchant's Emailaddress: </td>
          <td  class="td1" ><%=value%></td>
        </tr>

    --%>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Site URL");
        if (value == null)
            value = "-";

    %>
    <tr <%=style%> >
        <td class="tr0">Site URL: </td>
        <td class="tr1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("City");
        if (value == null)
            value = "-";

    %>
    <tr <%=style%> >
        <td class="tr0">Merchant's City: </td>
        <td class="tr1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>

    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Country");
        if (value == null)
            value = "-";

    %>
    <tr <%=style%> >
        <td class="tr0">Merchant's Country: </td>
        <td class="tr1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>

    <%
        pos++;
        style = "class=\"tr" + pos % 2 + "\"";
        value = (String) innerhash.get("Merchant's telephone Number");
        if (value == null)
            value = "-";

    %>
    <tr <%=style%> >
        <td class="tr0">Merchant's Telephone Number: </td>
        <td class="tr1"><%=ESAPI.encoder().encodeForHTML(value)%></td>
    </tr>
    <%
            }
            else
            {
                out.println(Functions.NewShowConfirmation("Sorry", "No records found for Tracking Id :" + ESAPI.encoder().encodeForHTML(trackingid)));
            }
        }
        catch (NumberFormatException nex)
        {

            System.out.print("inside error" + nex);
        }
    %>
</table>
<p>&nbsp;&nbsp; </p>
</body>
</html>
