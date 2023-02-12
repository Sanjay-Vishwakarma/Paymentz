<%--
  Created by IntelliJ IDEA.
  User: Dhiresh
  Date: 3/1/13
  Time: 3:39 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <META HTTP-EQUIV="Pragma" CONTENT="no-cache">

    <title>Invoice Management System</title>
</head>
<body class="bodybackground">
<%@ include file="/index.jsp"%>
<%int invoiceno = (Integer) request.getAttribute("invoiceno");
%>
<%int remindercounter = (Integer) request.getAttribute("remindercounter");
%>
<div class="row">
    <div class="col-lg-12">
        <div class="panel panel-default" >
            <div class="panel-heading">
                Invoice Reminder
            </div>
            <br><br>
            <table align=center width="750" border="0" cellspacing="0" cellpadding="2" bordercolor="#E4D6C9" >
                <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                <tr>
                    <td rowspan=3 colspan=3 align=center class="textb">
                        <% if(request.getAttribute("error")==null)
                        { %>

                        An E-Mail has Been Sent Again to the Customers E-Mail Address for Invoice Number <%=invoiceno%>
                        <% }
                        else
                        { %>

                        <%=(String)request.getAttribute("error")%>

                        <%}%>
                    </td></tr>
                <tr></tr><tr></tr><tr></tr><tr></tr>
                <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>

                <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>
                <tr><td align=center class="textb" colspan=3>&nbsp;<a href="/icici/servlet/Invoice?ctoken=<%=ctoken%>" ><b>Click Here</b> </a> to Go Back &nbsp;</td></tr>
                <tr><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td><td>&nbsp; &nbsp; &nbsp;</td></tr>


            </table>
        </div>
    </div>
</div>


</body>
</html>