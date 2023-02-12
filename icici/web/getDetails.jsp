<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.Map" %>
<%--
  Created by IntelliJ IDEA.
  User: jignesh.r
  Date: Mar 14, 2007
  Time: 7:38:44 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Hashtable<String, String> details = (Hashtable<String, String>) request.getAttribute("details");
    application.log(details.toString());
    boolean processingCompleted = Boolean.valueOf(details.get("processingCompleted"));
    String refreshUrl = request.getRequestURL() + "?" + request.getQueryString();
    String postUrl = details.get("POSTURL");

%>
<html>
<head>
    <title>Redirecting....</title>
    <style type=text/css>
        body
        .redirect {
            background: #E2E2E2;
            color: #000000;
            font: 10pt verdana, geneva, lucida, 'lucida grande', arial, helvetica, sans-serif;
        }

        .page {
            background: #FFFFFF;
            font: 10pt verdana, geneva, lucida, 'lucida grande', arial, helvetica, sans-serif;
        }

        td.redirect {
            font: 10pt verdana, geneva, lucida, 'lucida grande', arial, helvetica, sans-serif;
        }

        .smallfont {
            font: 10px verdana, geneva, lucida, 'lucida grande', arial, helvetica, sans-serif;
        }
    </style>
    <%
        if (!processingCompleted)
        {
    %>
    <meta http-equiv="refresh" content="10;url=<%=refreshUrl%>">
    <%
        }
    %>


</head>

<body>
<%
    if (!processingCompleted)
    {

%>
<table width="100%" border="0" cellspacing="0" cellpadding="0" height="95%" align="center">
    <tr align="center" valign="middle">
        <td class="redirect">
            <table border="0" cellspacing="1" cellpadding="10" class="tborder" width="70%">
                <tr>
                    <td class="page" align="center">
                        <p>&nbsp;</p>
                        <b>Processing your Transaction ... </b><br/>
                        <br/>
                        <span class="smallfont"> Please wait while the browser redirects you. </span><br/>
                        <span class="smallfont"> If you are not redirected within 60 secs Please contact <a
                                href="http://support.pz.com">Support</a> </span>

                        <p>&nbsp;</p></td>
                </tr>
            </table>
            <br><br>
        </td>
    </tr>
</table>
<%
    }
%>

<%
    if (processingCompleted)
    {
%>
<form name="postpay" action="<%=postUrl%>" method="POST">
    <%

        for (Map.Entry<String, String> e : details.entrySet())
        {
            String name = e.getKey();
            String value = e.getValue();

    %>
    <input type="hidden" name="<%=name%>" value="<%=value%>">
    <%
        }
    %>


</form>

<script type="text/javascript" language="javascript">
    document.postpay.submit();
</script>
</body>
</html>

<%
    }
%>

</body>
</html>