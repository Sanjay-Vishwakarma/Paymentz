<%--
  Created by IntelliJ IDEA.
  User: Dhiresh
  Date: 30/12/12
  Time: 12:46 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title></title>
</head>
<body>
<%@ include file="/index.jsp"%>
<%int invoiceno = (Integer) request.getAttribute("invoiceno");
  String reason= (String) request.getAttribute("cancelreason");
    %>
<%--<h1 align=center> Invoice Cancelled</h1>
            <table align=center width="750" border="0" cellspacing="0" cellpadding="2" bordercolor="#E4D6C9" bgcolor="#CCE0FF">
                <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>

                <tr>
                    <td rowspan=3 colspan=3 align=center>
                    The Invoice with Invoice Number <%=invoiceno%> has been cancelled <br> The Reason Mentioned was <%=reason%>
              </td></tr>
                <tr></tr><tr></tr><tr></tr><tr></tr>
                <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>

                <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                <tr><td align=center colspan=3>&nbsp; Click <a href="/icici/servlet/Invoice?ctoken=<%=ctoken%>" > Here </a> to Go Back &nbsp;</td></tr>





            </table>--%>


<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading">
                Invoice Cancelled
            </div>
            <br><br>
            <table align=center width="100%" border="0" cellspacing="0" cellpadding="2" bordercolor="#E4D6C9" bgcolor="#ffffff">
                <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>

                <tr>
                    <td rowspan=3 colspan=3 align=center class="textb">
                        The Invoice with Invoice Number <%=invoiceno%> has been cancelled <br> The Reason Mentioned was <%=reason%>
                    </td></tr>
                <tr></tr><tr></tr><tr></tr><tr></tr>
                <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>

                <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                <tr><td align=center colspan=3 class="textb">&nbsp;<a href="/icici/servlet/Invoice?ctoken=<%=ctoken%>">Click Here </a> to Go Back &nbsp;</td></tr>
                <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
            </table>
        </div>
    </div>
</div>

</body>
</html>