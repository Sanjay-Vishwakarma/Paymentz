
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="org.owasp.esapi.User" %>
<%@ include file="top.jsp"%>
<%//String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("partnername"));%>
<head>
    <title>company </title>


</head>

<%
    user =  (User)session.getAttribute("ESAPIUserSessionKey");

    ctoken= null;
    if(user!=null)
    {
        ctoken = user.getCSRFToken();

    }
    if (partner.isLoggedInPartner(session))
    {

%>
<input type="hidden" value="<%=ctoken%>" name="ctoken">

<%--<table align="center" width="95%">
    <tr>
        <td align="left"><jsp:include page="statusChart.jsp" /></td>
        <td align="right"><jsp:include page="salesChart.jsp" /></td>
    </tr>
</table>--%>

<%
    }
    else
    {

        response.sendRedirect("/partner/logout.jsp");
        return;
    }
%>
