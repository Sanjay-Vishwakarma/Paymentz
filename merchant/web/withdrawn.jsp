<%@ page import="com.directi.pg.Member" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="org.owasp.esapi.ESAPI" %>

<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title><%=company%> Merchant Settings</title>
    <link rel="stylesheet" type="text/css" href="/merchant/style.css"/>
</head>

<body>
<%@ include file="Top.jsp" %>


<p class="title"><%=company%> Merchant Settings</p>

   
<table border="0"  align="center" width="600" cellpadding="2" cellspacing="2">
<tr>
<td>
<table border="0" cellpadding="5" cellspacing="0" width="100%" bgcolor="#CCE0FF" align="center">
<%
    user =  (User)session.getAttribute("ESAPIUserSessionKey");
     if(user==null ||user.getCSRFToken()==null || user.getCSRFToken().equals(""))
        {

            response.sendRedirect("/merchant/Logout.jsp");
        }
         if(!user.getCSRFToken().equals(ctoken))
        {

            response.sendRedirect("/merchant/Logout.jsp");
        }
    String type = ESAPI.encoder().encodeForHTML((String) request.getAttribute("type"));
    if (type.equals("done"))
    {
        String merchantid = (String) session.getAttribute("merchantid");
        String currency = (String) session.getAttribute("currency");
%>
<tr bgcolor="#007ACC">
    <td width="10" class="textb">&nbsp;</td>
    <td colspan="3" class="label">
        Withdrawal Information

    </td>
    <input type="hidden" value="<%=ctoken%>" name="ctoken">

</tr>
<tr><td colspan="4">&nbsp;</td></tr>
<tr>
    <td width="10" class="textb">&nbsp;</td>
    <td width="260" class="textb">Transaction ID</td>
    <td width="10" class="textb">:</td>
    <td width="220" class="text">
        <%=ESAPI.encoder().encodeForHTML((String) request.getAttribute("transid"))%>
    </td>
</tr>
<tr>
    <td class="textb">&nbsp;</td>
    <td class="textb">Amount (<%=currency%>)</td>
    <td class="textb">:</td>
    <td class="text">
        <%=ESAPI.encoder().encodeForHTML((String) request.getAttribute("withdrawamt"))%>
    </td></tr>
<tr>
    <td class="textb">&nbsp;</td>
    <td class="textb">Party Name</td>
    <td class="textb">:</td>
    <td class="text">
        <%=merchants.getCompany(merchantid)%>
    </td></tr>
<tr>
    <td class="textb">&nbsp;</td>
    <td class="textb" valign="top">Address</td>
    <td class="textb" valign="top">:</td>
    <td class="text" valign="top">
        <%=((Member) session.getAttribute("memberObj")).address%>
    </td></tr>
<tr><td colspan="4">&nbsp;</td></tr>
<tr><td colspan="4" class="textb" align="center">
    Your request for Cheque/DD of <%=currency%> <%=ESAPI.encoder().encodeForHTML((String) request.getAttribute("withdrawamt"))%>  has been processed
    successfully </td></tr>
<%
}
else if (type.equals("nomoney"))
{
%>
<tr bgcolor="#007ACC">
    <td width="10" class="textb">&nbsp;</td>
    <td width="590" colspan="3" class="label">
        Withdrawal Information
    </td>

</tr>
<tr><td colspan="4">&nbsp;</td></tr>
<tr>
    <td width="10" class="textb">&nbsp;</td>
    <td width="590" colspan="3" class="textb">
        There is not enough money in your account to withdraw at the moment.
    </td>
</tr>
<tr><td colspan="4">&nbsp;</td></tr>
<%
}
else if (type.equals("simul"))
{
%>
<tr bgcolor="#007ACC">
    <td width="10" class="textb">&nbsp;</td>
    <td colspan="3" class="label">
        Withdrawal Information

    </td>

</tr>
<tr><td colspan="4">&nbsp;</td></tr>
<tr>
    <td width="10" class="textb">&nbsp;</td>
    <td width="590" colspan="3" class="textb">
        You cannot make simultaneous withdrawal request.
    </td>
</tr>
<tr><td colspan="4">&nbsp;</td></tr>
<%
}
else if (type.equals("invalid"))
{
%>
<tr bgcolor="#007ACC">
    <td width="10" class="textb">&nbsp;</td>
    <td colspan="3" class="label">
        Withdrawal Information
    </td>
</tr>
<tr><td colspan="4">&nbsp;</td></tr>
<tr>
    <td width="10" class="textb">&nbsp;</td>
    <td width="590" colspan="3" class="textb">
        Invalid Transaction Password.<br> If you want to try this transaction again please <a
            href="/merchant/withdrawal.jsp" class="link">click here</a>.
    </td>
</tr>
<tr><td colspan="4">&nbsp;</td></tr>
<%
    }
%>
</table>
</td></tr></table>

</body>

</html>
