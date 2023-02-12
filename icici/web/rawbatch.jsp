<%@ page import="java.util.*,com.directi.pg.*,
                 org.owasp.esapi.User,
                 org.owasp.esapi.ESAPI"%>
<%@ include file="functions.jsp"%>
<%@ include file="index.jsp"%>
<html>
<head>
<meta http-equiv="Expires" content="0">
<meta http-equiv="Pragma" content="no-cache">
<link rel="stylesheet" type="text/css" href="/icici/styyle.css" />
<script language="javascript">

function confirmbatch()
{

    var status=confirm("Do u really want to confirm the batch");
	if(status)
	{
		document.f1.mybutton.disabled=true;
		return true;

	}
    else
    {
	    return false;
    }
}
</script>
</head>
<body align="center">
<h1 align="center">Settle Transaction</h1>
<br>
<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();

    if (Admin.isLoggedIn(session))
    {
    %>
<form name=f1 action="/icici/servlet/RawCheck?ctoken=<%=ctoken%>" method=post onSubmit="return confirmbatch();" ENCTYPE="multipart/form-data">
<center><b>Raw Deal</b></center>
<br><br>
<!--<textarea name=rawdata cols=75 rows=50>
</textarea>
-->
<table  align="center"  cellpadding="2" cellspacing="2">
        <tr>
            <td>
                <table border="0" cellpadding="5" cellspacing="0" width="100%" bgcolor="#CCE0FF" align="center">
                <tr>
                <td><input name=rawfile type="file" value="Select Raw File"></td>
                <td>    <input type="hidden" value="<%=ctoken%>" name="ctoken"></td>
                <td>    <input name=mybutton type="submit" value="Confirm"></td>
                    </tr>

                    </table>
            </td>
        </tr>
    </table>
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