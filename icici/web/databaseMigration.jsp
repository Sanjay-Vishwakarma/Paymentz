<%@ page import="java.util.Hashtable" %>
<%@ page import="java.util.Enumeration" %>
<%@include file="index.jsp"%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Database Migration</title>
    <script>
        function myFunction()
        {
            if (confirm("Do you want to Migrate DB!!!"))
            {
                document.migrate.submit();
            }

        }
    </script>
</head>
<body>
<table border="0" align="center" width="100%">
    <tr>
        <td width="30" bgcolor="#2379A5" colspan="1" align="center"><font color="#FFFFFF" size="2" face="Verdana, Arial" >
            <form name="migrate" action="/icici/servlet/Migrate?ctoken=<%=ctoken%>" method="POST">
                <input type="button" value="MIGRATE DB" class="button" onclick="return myFunction()">
            </form></font>
        </td>
    </tr>
    <%
        Hashtable success = null;
        if (request.getAttribute("success") != null)
        {
            success = (Hashtable) request.getAttribute("success");
        }
        Hashtable error = null;
        if (request.getAttribute("error") != null)
        {
            error = (Hashtable) request.getAttribute("error");
        }
        out.println("<table><tr><td style=\"width:50%\">");
        if (error != null && !error.isEmpty())
        {
            Enumeration enu = error.keys();
            String key = "";
            String value = "";
            out.println("<table style=\"border:1px solid black;\"><tr><td colspan=\"3\">ERROR UPDATING</td><tr>");
            out.println("<tr><td>ROLE</td><td>ACCID</td><td>LOGIN NAME</td></tr>");
            while (enu.hasMoreElements())
            {

                out.println("<tr>");
                key = (String) enu.nextElement();
                value = (String) error.get(key);
                String []val=value.split(":");

                out.println("<td>"+val[0]+"</td><td>"+key+"</td><td>"+val[1]+"</td>");
                out.println("</tr>");
            }
            out.println("</table>");
        }
          out.println("</td><td style=\"width:50%\">");
        if (success != null && !success.isEmpty())
        {
            Enumeration enuS = success.keys();
            String keyS = "";
            String valueS = "";
            out.println("<table style=\"border:1px solid black;\"><tr><td colspan=\"3\">SUCCESS UPDATING</td><tr>");
            out.println("<tr><td>ROLE</td><td>ACCID</td><td>LOGIN NAME</td></tr>");
            while (enuS.hasMoreElements())
            {
                out.println("<tr>");
                keyS = (String) enuS.nextElement();
                valueS = (String) success.get(keyS);
                String []valS=valueS.split(":");
                out.println("<td>"+valS[0]+"</td><td>"+keyS+"</td><td>"+valS[1]+"</td>");
                out.println("</tr>");
            }
            out.println("</table>");
        }
        out.println("</td></tr></table>");
    %>
</table>
</body>
</html>