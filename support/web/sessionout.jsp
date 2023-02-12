
<%@ page import="com.directi.pg.TransactionEntry" %>
<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="com.logicboxes.util.ApplicationProperties" %>
<%
    session.invalidate();
    ESAPI.authenticator().logout();
%>
<html>
<head>
    <title>Session Expired</title>
    <link rel="stylesheet" type="text/css" href="/support/css/styles123.css">

</head>
<body style="background-color:#ecf0f1; " >
<br>
<br>
<br>
<br>
<br>

<table bgcolor="#2c3e50" align="center" width="50%" cellpadding="2" cellspacing="0"  >
    <tr>
        <td class="texthead">Session Expired.
            <table bgcolor="#ffffff"  width="100%" align="center" cellpadding="2" cellspacing="0" style="height:260px; border:1px solid #2c3e50 ">
                <tr>
                    <td align="center" style="padding-top:5px; ">
                        <%--<img src="/support/images/session.jpg">--%>
                    </td>
                </tr>

                <tr style="padding-top: 10px">
                    <td align="center" class="textb" >Suggestion *For security reasons, we have disabled double clicks and Back, Forward and Refresh tabs of the browser. Also,&nbsp;the&nbsp;session will expire automatically, if the browser window is idle for a long time. <br><br>*If the problem persists, please try again after clearing the <strong>Temporary Files </strong>from your web browser. <br></td>

                </tr>
             <%--   <tr >
                    <td align="center" class="textb" ><form action="/support/custSuppLogin.jsp" method="post">
                    <button type="submit" value="LOGIN"  style="background:#34495e;color:#edeef4;border:0;width: 60px;height: 30px">Login</button>
                    </form> </td>

                </tr>--%>
                <tr >
                    <td align="center" class="textb" >&nbsp;</td>

                </tr>
                <tr >
                    <td align="center" class="textb" >&nbsp;</td>

                </tr>
                <tr >
                    <td align="center" class="textb" >&nbsp;</td>

                </tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>




