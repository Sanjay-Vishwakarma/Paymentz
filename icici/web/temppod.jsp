<%@ include file="functions.jsp" %>
<%@ page import="org.owasp.esapi.ESAPI,
                 com.directi.pg.Merchants,
                 com.directi.pg.Admin" %>
                 <%@ include file="index.jsp" %>
<html>
<head>
    <link rel="stylesheet" type="text/css" href="/icici/styyle.css">
</head>

<body>
<h1 align="center">Pending Capture List</h1>
<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();


    if (Admin.isLoggedIn(session))
    {
    %>
<br><br><br>

<%

        String errormsg = (String)request.getAttribute("cbmessage");
        if (errormsg == null)
        errormsg = "";

        out.println("<table align=\"center\"  ><tr><td valign=\"middle\"><font class=\"text\" >");

        out.println(errormsg);

        out.println("</font></td></tr></table>");


  %>


<%
    Hashtable hash = (Hashtable) request.getAttribute("poddetails");
    Hashtable temphash = null;
    //int pageno=1;
    //int records=30;
//out.println(hash);
    int hashsize = hash.size();

    int pageno = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SPageno")), 1);
    int pagerecords = convertStringtoInt(ESAPI.encoder().encodeForHTML(request.getParameter("SRecords")), 5);

    int records = 0;
    int totalrecords = 0;

    try
    {
        records = Integer.parseInt((String) hash.get("records"));
        totalrecords = Integer.parseInt((String) hash.get("totalrecords"));
    }
    catch (Exception ex)
    {
    }
   String str="";
        str = str + "?SRecords=" + pagerecords;
        str = str + "&ctoken=" + ctoken;

    if (records > 0)
    {
%>
<form action="/icici/servlet/TempPodSubmit?ctoken=<%=ctoken%>" method=post>
    <font class="info">totalrecords <%=totalrecords%></font>
    <table align=center>
        <tr>
            <td valign="middle" align="center" bgcolor="#008BBA">Sr no</td>
            <td valign="middle" align="center" bgcolor="#008BBA">Merchant Txn. ID</td>
            <td valign="middle" align="center" bgcolor="#008BBA">Merchantid</td>
            <td valign="middle" align="center" bgcolor="#008BBA">Description</td>
            <td valign="middle" align="center" bgcolor="#008BBA">Transaction Amount</td>
            <td valign="middle" align="center" bgcolor="#008BBA">Capture Amount</td>
            <td valign="middle" align="center" bgcolor="#008BBA">Company</td>
            <td valign="middle" align="center" bgcolor="#008BBA">Previous Transaction No.</td>
            <td valign="middle" align="center" bgcolor="#008BBA">Txn. Ref. No.</td>
            <td valign="middle" align="center" bgcolor="#008BBA">Authorization Code</td>
            <td valign="middle" align="center" bgcolor="#008BBA">RRN Number</td>
        </tr>
        <%
            String style = "class=td1";
            String ext = "light";

            for (int pos = 1; pos <= records; pos++)
            {
                String id = Integer.toString(pos);

                int srno = Integer.parseInt(id) + ((pageno - 1) * pagerecords);

                if (pos % 2 == 0)
                {
                    style = "class=td2";
                    ext = "dark";
                }
                else
                {
                    style = "class=td1";
                    ext = "light";
                }

                temphash = (Hashtable) hash.get(id);

                String tempcurrentstatus = (String) temphash.get("currentstatus");
                String tempprovstatus = (String) temphash.get("provstatus");
                String productname = (String) temphash.get("productkey");
                String path = productname + "/";

                out.println("<tr>");

                out.println("<td " + style + ">&nbsp;" + srno + "</td>");
                out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("icicitransid")) + "</a></td>");
                out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("icicimerchantid")) + "</td>");
                out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("description")) + "</td>");
                out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("amount")) + "</td>");
                out.println("<td " + style + " ><font color=\"red\">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("captureamount")) + "</font></td>");
                out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("company_name")) + "</td>");
                out.println("<td " + style + ">&nbsp;" + ESAPI.encoder().encodeForHTML((String) temphash.get("authid")) + "</td>");
                out.println("<td " + style + ">&nbsp;<input type=text name=" + ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("icicitransid")) + "></td>");
                out.println("<td " + style + ">&nbsp;<input type=text name=\"capturecode_" + ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("icicitransid")) + "\"></td>");
                out.println("<td " + style + ">&nbsp;<input type=text name=\"RRN_" + ESAPI.encoder().encodeForHTMLAttribute((String) temphash.get("icicitransid")) + "\"></td>");
                out.println("</tr>");
            }
        %>
        <tr>
            <td valign="middle" align="center" bgcolor="#008BBA" colspan=11><input type=submit value="Capture"></td>
        </tr>
    </table>
</form>


<table align=center valign=top><tr>
    <td align=center>
        <jsp:include page="page.jsp" flush="true">
            <jsp:param name="numrecords" value="<%=totalrecords%>"/>
            <jsp:param name="numrows" value="<%=pagerecords%>"/>
            <jsp:param name="pageno" value="<%=pageno%>"/>
            <jsp:param name="str" value="<%=ESAPI.encoder().encodeForURL(str)%>"/>
            <jsp:param name="page" value="TempPod"/>
            <jsp:param name="currentblock" value="1"/>
            <jsp:param name="orderby" value=""/>
        </jsp:include>
    </td>
</tr>
</table>
<%
    }
    else
    {
        out.println(ShowConfirmation("Sorry", "No records found."));
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
</body>
</html>