<%@ page import="org.owasp.esapi.ESAPI,
                 org.owasp.esapi.User"%>
                 <%@ include file="index.jsp"%>
                 <%@ page import="com.directi.pg.PzEncryptor"%>
 <%--
  Created by IntelliJ IDEA.
  User: admin1
  Date: Jul 14, 2012
  Time: 1:22:35 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head><title>Generated Key</title></head>
  <body>
 <%

     user =  (User)session.getAttribute("ESAPIUserSessionKey");
    ctoken= null;
     if(user!=null)
     {
         ctoken = user.getCSRFToken();

     }
    if (com.directi.pg.Admin.isLoggedIn(session))
    {


    %>


  <input type="hidden" value="<%=ctoken%>" name="ctoken">
 <table border="0" bgcolor="#CCE0FF" width="70%" align="center" cellpadding="2" cellspacing="2">
 
<tr>
  <td class="textb" align="center"> SecretKey is updated </td>
  <td>
  </td>

</tr>





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