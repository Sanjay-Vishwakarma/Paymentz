<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%String company = ESAPI.encoder().encodeForHTML((String)session.getAttribute("company"));%>
<html>
<head>
    <%--<META HTTP-EQUIV="Pragma" CONTENT="no-cache">--%>
    <title><%=company%> Merchant Reversals</title>
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

<%@ include file="Top.jsp" %>



<p align="center" class="title"><%=company%> Merchant Transaction Reversals</p>

<form name="form" method="post" action="">
    <table border="0" cellpadding="4" cellspacing="0" width="750" align="center">
        <tr>
            <td colspan="8" valign="middle" align="center" bgcolor="#9A1305" class="label">
                Tracking ID:
                <input type="text" name="desc" class="textBoxes" size="20">
            </td>
            <td colspan="8" valign="middle" align="center" bgcolor="#9A1305" class="label">
                Description:
                <input type="text" name="desc" class="textBoxes" size="20">
            </td>
            <td colspan="8" valign="middle" align="left" bgcolor="#9A1305" class="label">
                <input type="submit" value="Search" name="B1" onclick="return isint(document.form)" class="button">
            </td>
        </tr>
    </table>
</form>
<form name="form" method="post" action="">

    <br>
    <table border="1" cellpadding="5" cellspacing="0" width="750" bordercolor="#ffffff" align="center">

        <tr>
            <td width="15%" class="th1">Tracking ID</td>
            <td width="18%" class="th0">Transaction ID</td>
            <td width="42%" class="th1">Description</td>
            <td width="15%" class="th0">Amount</td>
            <td width="10%" class="th1"></td>
        </tr>

        <tr class="tr0">
            <td>12009</td>
            <td>12009</td>
            <td align="center">j128ujndf8765gff5</td>
            <td>1,000.00</td>
            <td><input type="submit" value="Reverse" name="B1" onclick="return isint(document.form)" class="button">
            </td>
        </tr>
        <tr class="tr1">
            <td>12009</td>
            <td>12009</td>
            <td align="center">j128ujndf8765gff5</td>
            <td>1,000.00</td>
            <td><input type="submit" value="Reverse" name="B1" onclick="return isint(document.form)" class="button">
            </td>
        </tr>

        <tr class="tr0">
            <td>12009</td>
            <td>78</td>
            <td align="center">j128ujndf8765gff5</td>
            <td>1,000.00</td>
            <td><input type="submit" value="Reverse" name="B1" onclick="return isint(document.form)" class="button">
            </td>
        </tr>
        <tr class="tr1">
            <td>12009</td>
            <td>1234</td>
            <td align="center">j128ujndf8765gff5</td>
            <td>1,000.00</td>
            <td><input type="submit" value="Reverse" name="B1" onclick="return isint(document.form)" class="button">
            </td>
        </tr>

    </table>
</form>


<p>&nbsp;&nbsp; </p>
</body>

</html>
