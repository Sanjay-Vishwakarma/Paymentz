<%@ page import="com.logicboxes.util.ApplicationProperties,
                 com.directi.pg.Merchants" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp"%>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title> Admin Change Password</title>
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
         Merchants merchants = new Merchants();
         if (merchants.isLoggedIn(session))
        {
    %>


<p>
    <table border="0" width="750" align="center">
        <tr><td>&nbsp;</td></tr>
        <tr>
            <td class="subtitle" align="center"><h1>Password Changed</h1></td>
        </tr>

    </table>


    <br>

</p>

<p>&nbsp;&nbsp; </p>
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
