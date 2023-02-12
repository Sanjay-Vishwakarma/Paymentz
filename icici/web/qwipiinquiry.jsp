<%@ page import="com.directi.pg.Functions,
                 org.owasp.esapi.ESAPI,
                 org.owasp.esapi.errors.ValidationException"%>
<%@ page import="java.util.*" %>
<%@ include file="functions.jsp"%>
<%@ include file="qwipitab.jsp"%>
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
<body>

<div class="reporttable">
<%
    session.setAttribute("submit","Enquiry");
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
%>

<%  Hashtable hash=(Hashtable)request.getAttribute("inquirydetails");

    //out.println(hash);
    if(hash!=null /*&& !hash.get("resultcode").equals("X")*/)
    {
        /*String style="class=tr0";
        String value="";

        int pos=0;*/
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
            <td class="tr1" style="width: 43%;" align="center">Result code: </td>

            <td class="tr1" ><%=ESAPI.encoder().encodeForHTML((String)hash.get("resultcode"))%></td>
        </tr>
        <tr>
            <td class="tr1" style="width: 43%;" align="center">Error code: </td>
            <td class="tr1" ><%=ESAPI.encoder().encodeForHTML((String)hash.get("errorcode"))%></td>
        </tr>
        <tr>
            <td class="tr1" style="width: 43%;" align="center">Error text: </td>
            <td class="tr1" ><%=ESAPI.encoder().encodeForHTML((String)hash.get("errortext"))%></td>
        </tr>
        <tr>
            <td class="tr1" style="width: 43%;" align="center">Order Id: </td>
            <td class="tr1" ><%=ESAPI.encoder().encodeForHTML((String)hash.get("orderid"))%></td>
        </tr>
        <tr>
            <td class="tr1" style="width: 43%;" align="center">Order status: </td>
            <td class="tr1" ><%=ESAPI.encoder().encodeForHTML((String)hash.get("status"))%></td>
        </tr>
        <tr>
            <td class="tr1" style="width: 43%;" align="center">Billing number: </td>
            <td class="tr1" ><%=ESAPI.encoder().encodeForHTML((String)hash.get("billno"))%></td>
        </tr>
        <tr>
            <td class="tr1" style="width: 43%;" align="center">Order amount: </td>
            <td class="tr1" ><%=ESAPI.encoder().encodeForHTML((String)hash.get("amount"))%></td>
        </tr>
        <tr>
            <td class="tr1" style="width: 43%;" align="center">Order date: </td>
            <td class="tr1"><%=ESAPI.encoder().encodeForHTML((String)hash.get("date"))%></td>
        </tr>
        <tr>
            <td class="tr1" style="width: 43%;" align="center">Currency: </td>
            <td class="tr1"><%=ESAPI.encoder().encodeForHTML((String)hash.get("currency"))%></td>
        </tr>

        <tr>
            <td class="tr1" style="width: 43%;" align="center">Refund status: </td>
            <td class="tr1"><%=ESAPI.encoder().encodeForHTML((String)hash.get("refundcode"))%></td>
        </tr>

        <tr>
            <td class="tr1" style="width: 43%;" align="center">Refund status text: </td>
            <td class="tr1"><%=ESAPI.encoder().encodeForHTML((String)hash.get("refundtext"))%></td>
        </tr>

        <tr>
            <td class="tr1" style="width: 43%;" align="center">Refund amount: </td>
            <td class="tr1"><%=ESAPI.encoder().encodeForHTML((String)hash.get("refundamount"))%></td>
        </tr>

        <tr >
            <td class="tr1" style="width: 43%;" align="center">Refund date: </td>
            <td class="tr1"><%=ESAPI.encoder().encodeForHTML((String)hash.get("refunddate"))%></td>
        </tr>

        <tr >
            <td class="tr1" style="width: 43%;" align="center">Refund remark: </td>
            <td class="tr1"><%=ESAPI.encoder().encodeForHTML((String)hash.get("refundremark"))%></td>
        </tr>

        <tr>
            <td class="tr1" style="width: 43%;" align="center">Refund message:</td>
            <td class="tr1"><%=ESAPI.encoder().encodeForHTML((String)hash.get("refundmsg"))%></td>
        </tr>

        <tr>
            <td class="tr1" style="width: 43%;" align="center">Chargeback code: </td>
            <td class="tr1"><%=ESAPI.encoder().encodeForHTML((String)hash.get("chargebackcode"))%></td>
        </tr>

        <tr>
            <td class="tr1" style="width: 43%;" align="center">Chargeback status: </td>
            <td class="tr1"><%=ESAPI.encoder().encodeForHTML((String)hash.get("chargebacktext"))%></td>
        </tr>

        <tr>
            <td class="tr1" style="width: 43%;" align="center">Settle code: </td>
            <td class="tr1"><%=ESAPI.encoder().encodeForHTML((String)hash.get("settlecode"))%></td>
        </tr>
        <tr>
            <td class="tr1" style="width: 43%;" align="center">Settle status: </td>
            <td class="tr1"><%=ESAPI.encoder().encodeForHTML((String)hash.get("settletext"))%></td>
        </tr>
    </table>
<%
}
else
{
    out.println(Functions.NewShowConfirmation("Sorry","your record is not found at bank side"));
}


%>
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