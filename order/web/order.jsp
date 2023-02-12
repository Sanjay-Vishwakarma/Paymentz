<%@ page import="java.util.Hashtable" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.directi.pg.Functions" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ include file="ietest.jsp" %>
<%String company = (String)session.getAttribute("company");%>
<%
    User user =  (User)session.getAttribute("Anonymous");
    String ctoken= null;
    if(user!=null)
    {
        ctoken = user.getCSRFToken();

    }
%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title><%=company%> Order Tracking System > Order Details</title>
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
<table border="0" cellpadding="5" cellspacing="0" width="100%" bordercolor="#ffffff" align="center">
    <tr>
        <td align="center" >
            &nbsp;
        </td>
    </tr>
    <tr>
        <td align="center" >
            <img src="/images/order/<%=session.getAttribute("logo")%>" border="0">
        </td>
    </tr>
</table>
<br><br><br>
<p class="textb" align="center" style="font-weight: bold;font-size:13px; "><%=company%> Customer Order Tracking System</p>
<p>
<table border="0" width="100%" align="center">
    <tr><td>&nbsp;</td></tr>
    <tr>
        <td class="textb" align="center" >Order Details</td>
    </tr>
    <tr>
        <td class="textb" align="center" >The following set of orders have been performed on the specified card through
            the <%=company%> Payment Gateway Service</td>
    </tr>
</table>
</p>
<hr class="hrnew"><br><br>
<center><form action="/order/index.jsp" method="post"><input type="hidden" name="ctoken" value="<%=ctoken%>"><input type="hidden" name="partnerid" value="<%=session.getAttribute("partnerid")%>"><input type="hidden" name="fromtype" value="<%=session.getAttribute("company")%>"><input type="submit" value="HOME"></form></center>
<form>
    <div class="reporttable" style="margin-left:30px; ">
        <%

            Hashtable hash = (Hashtable) request.getAttribute("orderdetails");
            String billingDescriptor=(String)request.getAttribute("billingDescriptor");
            Hashtable temphash = null;

            int records = 0;
            int totalrecords = 0;
            try
            {
                records = Integer.parseInt(ESAPI.encoder().encodeForHTML((String) hash.get("records")));
                totalrecords = Integer.parseInt(ESAPI.encoder().encodeForHTML((String) hash.get("totalrecords")));

            }
            catch (Exception ex)
            {
                Functions.NewShowConfirmation("Error ","Error while getting records");
            }
            String style = "class=tr0";
            String ext = "light";

            if (records > 0)
            {
        %>

        <input id="ctoken" name="ctoken" type="hidden" value="<%=ctoken%>" >
        <br><br>
        <table align=center border="0" cellspacing="0" cellpadding="2" bordercolor="#FFFFFF" color="#FFFFFF" width="100%">
            <tr>
                <td>
                    <table border="0" cellpadding="5" cellspacing="2" width="100%" bordercolor="#ffffff" align="center" class="table table-striped table-bordered table-hover table-green dataTable">
                        <thead>
                        <tr>
                            <td width="12%" class="th0">Date</td>
                            <td width="13%" class="th1">Tracking ID</td>
                            <%--<td width="13%" class="th1">Descriptor</td>
                            <td width="15%" class="th0">Company Name</td>--%>
                            <td width="20%" class="th1">Description</td>
                            <td width="15%" class="th1">Currency</td>
                            <td width="15%" class="th0">Amount</td>
                            <td width="10%" class="th1">Status</td>
                        </tr>
                        </thead>

                        <%
                            String ccnum= request.getParameter("ccnum");
                            request.setAttribute("ccnum",ccnum);

                            ccnum=null;
                            for (int pos = 1; pos <= records; pos++)
                            {
                                String id = Integer.toString(pos);

                                style = "class=\"tr" + pos % 2 + "\"";

                                temphash = (Hashtable) hash.get(id);
                                String currency=(String) temphash.get("currency");
                                if(currency==null)
                                {
                                    currency="";
                                }
                                String date = (String) temphash.get("date");
                                String trackingid = (String) temphash.get("trackingid");
                                String description = (String) temphash.get("description");
                                String amount =   (String) temphash.get("amount");
                                String companyname = (String) temphash.get("companyname");
                                String brandname = (String) temphash.get("brandname");
                                String status = (String) temphash.get("status");
                                String accountid = (String) temphash.get("accountid");
                                String displayName = (String) temphash.get("displayname");
                                if (status.equals("begun"))
                                    status = "Begun Processing";
                                if (status.equals("proofrequired"))
                                    status = "Transaction Proof Required<br> <a href=\"ProofLetter?trackingid=" + trackingid + "\" class=\"link\">View letter Format</a> </br>";
                                if (status.equals("authsuccessful"))
                                    status = "Auth Successful";
                                if (status.equals("authfailed"))
                                    status = "Auth Failed";
                                if (status.equals("capturestarted"))
                                    status = "Capture Started";
                                if (status.equals("capturesuccess"))
                                    status = "Capture Successful";
                                if (status.equals("capturefailed"))
                                    status = "Capture Failed";
                                if (status.equals("podsent"))
                                    status = "POD Sent ";
                                if (status.equals("settled"))
                                    status = "Settled";
                                if (status.equals("markedforreversal"))
                                    status = "Reversal Request Sent";
                                if (status.equals("reversed"))
                                    status = "Reversed";
                                if (status.equals("chargeback"))
                                    status = "Chargeback";
                                if (status.equals("failed"))
                                    status = "Failed/Cancelled by Customer";
                                if (status.equals("cancelled"))
                                    status = "Cancelled Transaction";
                                if (status.equals("authcancelled"))
                                    status = "Authorisation Cancelled";

                        %>
                        <tr>
                            <td align="center"<%=style%>><%=ESAPI.encoder().encodeForHTML(date)%></td>
                            <td align="center"<%=style%>><%=trackingid%></td>
                            <%--<td align="center"<%=style%>><%=billingDescriptor%></td>
                            &lt;%&ndash; <td <%=style%>><b><a <%=style%> href='OrderDetails?STrackingid=<%=ESAPI.encoder().encodeForURL(trackingid)%>'><U><%=trackingid%></U>&ndash;%&gt;
                            &lt;%&ndash; <input type="hidden" name="trackingid" value="<%= trackingid %>" >&ndash;%&gt;

                            <td align="center" <%=style%>><%=companyname%>(<%=ESAPI.encoder().encodeForHTML(brandname)%>)</td>--%>
                            <td align="center"<%=style%>><%=ESAPI.encoder().encodeForHTML(description)%></td>
                            <td align="center"<%=style%>><%=ESAPI.encoder().encodeForHTML(currency)%></td>
                            <td align="center"<%=style%>><%=ESAPI.encoder().encodeForHTML(amount)%></td>
                            <td align="center"<%=style%>><%=status%></td>
                        </tr>
                        <%
                            }
                        %>
                    </table>
                </td>
            </tr>
        </table>
    </div>
</form>
<%
    }
    else
    {
        out.println(Functions.NewShowConfirmation("Sorry","Records not found. "));
    }
%>
</body>
</html>