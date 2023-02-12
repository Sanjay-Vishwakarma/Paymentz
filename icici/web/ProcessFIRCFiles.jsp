<%--
  Created by IntelliJ IDEA.
  User: jignesh.r
  Date: Jul 11, 2006
  Time: 1:20:54 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="index.jsp" %>
<html>
<head>
    <title>Process FIRC Files</title>
    <script type="text/javascript" language="javascript">
        function submitForm()
        {
            var form=document.FIRCForm;
            if(isNaN(parseInt(form.merchantId.value))
            {
                alert("Please enter numeric value for Merchant ID.");
                return false;
            }
            if(form.fircFile.value.length == 0)
            {
                alert("Please enter valid FIRC file to upload.");
                return false;
            }
            form.submit();
        }
    </script>
</head>

    <body align="center">

    <h1 align="center">Process FIRC Files</h1>

<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
    %>

    <form name = "FIRCForm" action="/icici/servlet/ProcessFIRCServlet?ctoken=<%=ctoken%>" method="post" ENCTYPE="multipart/form-data">
        <center>
        <table  border="0" cellpadding="5" cellspacing="0" bgcolor="#CCE0FF" align="center">
            <tr>
              <td>
                 Merchant ID :
              </td>
              <td>
                 <input name="merchantId" type="text" >
              </td>
            </tr>

            <tr>
              <td>
                FIRC File    :
              </td>
              <td>
                <input name=fircFile type="file" value="Select FIRC File">
              </td>
            </tr>
            <tr>
            <td colspan="2" align="center">
                <input name=mybutton type="button" value="Upload" onclick="submitForm()">
            </td>
            </tr>
        </table>
      </center>
    </form>
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