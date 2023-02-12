<%@ page import="com.directi.pg.SystemError,
                 com.directi.pg.Functions" %>
<%@ page import="com.directi.pg.Transaction" %>
<%@ page import="com.logicboxes.util.Util" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.Hashtable" %>
 <%@ include file="index.jsp"%>
<%@ page import="java.util.*,
                 com.directi.pg.Admin"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2//EN">
<%--<%@ page language="java" session="false" isErrorPage="false" %>--%>

<HTML>

<HEAD>
    <META HTTP-EQUIV="Content-Type" CONTENT="text/html;CHARSET=iso-8859-1">
    <TITLE>TRANSACTION STATUS</TITLE>
</HEAD>
<%
    Transaction transaction = new Transaction();
    String trackingId = request.getParameter("trackingid");
    String id = "";
    String code = "";
    String receiptNo = "";
    String status = "";
    try
    {
        if (trackingId != null)
        {
            Hashtable details = transaction.getDetails(trackingId);
            id =(String) details.get("id");
            code =(String) details.get("code");
            receiptNo =(String) details.get("receiptno");
            status =(String) details.get("status");
        }
        else
        {
            out.println("<center>Please enter the Trackingid to get the status</center>");

        }
    }
    catch (SystemError systemError)
    {
        application.log("Error " + Util.getStackTrace(systemError));
    }
    catch (SQLException e)
    {
        application.log("Error " + Util.getStackTrace(e));
    }


%>


<BODY>
<%  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {
    %>
<form name="f1" action="status.jsp?ctoken=<%=ctoken%>" method="post">
<center>
<table>
<input type="text" name="trackingid" >
<input type="submit" value="submit">
</table>
</center>
</form>
<% if(trackingId != null) { %>
<table border="1"   align="center"  cellpadding="2" cellspacing="2">
        <tr>
            <td>
                <table border="1" width="50%"  border="0" cellpadding="5" cellspacing="0"  bgcolor="#CCE0FF" align="center">


    <tr>
        <td>
            TrackingID
        </td>

        <td>
            Status
        </td>

        <td>
            Id
        </td>

        <td>
            Code
        </td>
        <td>
            Refrence No (RRN)
        </td>
    </tr>

    <tr>
        <td>
            <%=trackingId%>
        </td>

        <td>
            <%=status%>
        </td>

        <td>
            <%=id%>
        </td>

        <td>
            <%=code%>
        </td>
        <td>
            <%=receiptNo%>
        </td>
    </tr>
</table>
            </td>
        </tr>
    </table>
<%}


%>
<%
    }
    else
{
    response.sendRedirect("/icici/logout.jsp");
    return;
}
%>
</BODY>
</HTML>
