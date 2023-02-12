<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ include file="ietest.jsp" %>
<html>
<head>
	<title>Forgot Transaction Password</title>
	<link rel="stylesheet" type="text/css" href="/merchant/style.css" />
</head>
<body style="margin: 0px" text=#000000 vlink=#000000 alink=#000000 link=#000000 bgcolor=#ffffff leftmargin=0 topmargin=0 marginwidth="0" marginheight="0">
<%@ include file="Top.jsp" %>
<br>
<table class=search border="0"  cellpadding="2" cellspacing="0" width="50%" align=center valign="center">

</table>
<br><br>
<table  align="center" width="50%" cellpadding="2" cellspacing="2">
<input type="hidden" value="<%=ctoken%>" name="ctoken">
	<tr>
		<td>
			<table border="0" cellpadding="5" cellspacing="0" width="100%" bgcolor="#CCE0FF" align="center">
				<tr bgcolor="#007ACC">

		<td class="label" colspan="2" valign="center">Forgot Transaction Password</td>
	</tr>
	<tr><td>&nbsp;</td>
	<tr>
		<td>&nbsp;&nbsp;</td>
    	<td class="text">Your password has been sent to you by email to your mailing address set on our server.</td>
	</tr>
	<tr><td>&nbsp;</td>
</table>
</td>
</tr>
</table>
</body>
</html>


