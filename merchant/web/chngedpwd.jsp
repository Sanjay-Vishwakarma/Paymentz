<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));%>
<%@ include file="Top.jsp" %>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">
    <title><%=company%> Merchant Settings > Change Password</title>
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
<p class="title"><%=company%> Merchant Change Password</p>
 <input type="hidden" value="<%=ctoken%>" name="ctoken">
<p>
    <table border="0" width="750" align="center">
        <tr><td>&nbsp;</td></tr>
        <tr>
            <td class="subtitle" align="center">Password Changed</td>
        </tr>

        <tr><td>&nbsp;</td></tr>
    </table>


    <br>

</p>

<p>&nbsp;&nbsp; </p>
</body>

</html>
