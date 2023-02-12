
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN">
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="top.jsp"%>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("agentname"));%>
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
    if (agent.isLoggedInAgent(session))
    {

%>
<input type="hidden" value="<%=ctoken%>" name="ctoken">
</br></br>


<%
    }
    else
    {

        response.sendRedirect("/agent/logout.jsp");
        return;
    }
%>
