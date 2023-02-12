<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException"%>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>
<%@ include file="ecoretab.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 11/9/12
  Time: 7:29 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <title></title>
</head>
<body class="bodybackground">
<div class="reporttable">
<% ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>


<%  Hashtable hash=(Hashtable)request.getAttribute("inquirydetails");

    //out.println(hash);
    if(hash!=null)
    {
        String style="class=tr0";
        String value="";

        int pos=0;



%>
<table border="1" cellpadding="5" cellspacing="0" style="width:50%" align="center" class="table table-striped table-bordered table-green dataTable">
<thead>
    <tr>
        <td  colspan="2" class="texthead">Enquiry</td>
    </tr>
</thead>
<tr>
    <td class="tr1" style="width: 43%;" align="center">Response code: </td>

    <td class="tr1" ><%=ESAPI.encoder().encodeForHTML((String)hash.get("responsecode"))%></td>
</tr>

<tr>
    <td class="tr1" style="width: 43%;" align="center">Description: </td>
    <td class="tr1" ><%=ESAPI.encoder().encodeForHTML((String)hash.get("description"))%></td>
</tr>

<tr>
    <td class="tr1" style="width: 43%;" align="center">Transaction Id: </td>
    <td class="tr1" ><%=ESAPI.encoder().encodeForHTML((String)hash.get("transactionId"))%></td>
</tr>

<tr>
    <td class="tr1" style="width: 43%;" align="center">Status Code: </td>
    <td class="tr1" ><%=ESAPI.encoder().encodeForHTML((String)hash.get("statusCode"))%></td>
</tr>

<tr>
    <td class="tr1" style="width: 43%;" align="center">Order status: </td>
    <td class="tr1" ><%=ESAPI.encoder().encodeForHTML((String)hash.get("status"))%></td>
</tr>

<%
}
else
{
    out.println(Functions.NewShowConfirmation("Sorry","your record is not found at bank side"));
}

%>
</table>
<p>&nbsp;&nbsp; </p>

<%
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</div>
</body>
</html>