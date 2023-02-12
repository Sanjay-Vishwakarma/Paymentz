
<%@ page import="com.directi.pg.TransactionEntry" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ include file="ietest.jsp" %>

<%String company = ESAPI.encoder().encodeForHTML(ApplicationProperties.getProperty("COMPANY"));%>
<html>
<head>
    <title><%=company%> Administration Logout</title>

    <script src='/order/css/menu.js'></script>
    <script type='text/javascript' src='/order/css/menu_jquery.js'></script>
    <link rel="stylesheet"href="/order/css/styles123.css" type="text/css">
    <script src='/order/css/bootstrap.min.js'></script>
    <link href="/order/css/icons/font-awesome/css/font-awesome.min.css" rel="stylesheet"></head>


<body style="margin: 0px" text=#000000 vlink=#000000 alink=#000000 link=#000000 bgcolor=#ffffff leftmargin=0 topmargin=0
      marginwidth="0" marginheight="0">

<form action="/order/index.jsp" method=post>
    <br>

    <table class=search border="0" cellpadding="2" cellspacing="0" width="50%" align=center valign="center">
        <tr>
            <td align="center">
                <img src="/order/images/logo.jpg" border="0">
            </td>
        </tr>
    </table>
    <br>
    <table class=search border="0" bgcolor="#CCE0FF" cellpadding="2" cellspacing="0" width="600"
           bordercolorlight="#000000" bordercolordark="#FFFFFF" align=center valign="center">
        <tr>
            <td bgcolor="#007ACC" class="label" align="left">&nbsp;&nbsp;Order Tracking System</td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp;Thank you for visiting <%=company%>'s Order Tracking System</td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        <tr align="center">
            <td class="text">&nbsp;&nbsp; <form action="/order/index.jsp" method="post"><input type="submit" value="Click Here" name="submit" ></form> to go back to the
                Order Tracking System </td>
        </tr>
        <tr><td>&nbsp;</td></tr>
        

    </table>
	<table class=search border="0" cellpadding="2" cellspacing="0" width="600" align=center valign="center">
        <tr>
            <%--<td align="center">

                    <a target="_blank" href="http://abc.com/site/certificate/13835412899812182686"><IMG border=0 height=120 src="./images/Certificatelogo.png" width=210></a>
            </td>--%>
        </tr>
    </table>
</form>
</body>
</html>


