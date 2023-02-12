<%@ page import="com.directi.pg.SystemError" %>
<%@ page import="com.directi.pg.Transaction" %>
<%@ page import="com.logicboxes.util.Util" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.util.Hashtable" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 3.2//EN">
<%@ page language="java" session="false" isErrorPage="false" %>


<HTML>

<HEAD>
    <META HTTP-EQUIV="Content-Type" CONTENT="text/html;CHARSET=iso-8859-1">
    <TITLE>TRANSACTION STATUS</TITLE>
</HEAD>
<%
    String trackingId = request.getParameter("trackingid");
    Transaction transaction = new Transaction();
    String id = "";
    String code = "";
    String receiptNo = "";
    String status = "";
    try
    {
        if (trackingId != null)
        {
            Hashtable<String, String> details = transaction.getDetails(trackingId);
            id = details.get("id");
            code = details.get("code");
            receiptNo = details.get("receiptno");
            status = details.get("status");
        }
        else
        {
            out.println("Please enter the Trackingid to get the status");

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


<BODY bgcolor='#83a1C6'>

<form name="f1" action="status.jsp" method="post">
<input type="text" name="trackingid" >
<input type="submit" value="submit">
</form>
<% if(trackingId != null) { %>
<table align="center" width="40%" border="2">

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
<%}%>
</BODY>
</HTML>
