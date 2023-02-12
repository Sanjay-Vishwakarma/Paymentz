<%@ page import="com.directi.pg.*,
                 org.owasp.esapi.errors.ValidationException,
                 org.owasp.esapi.ESAPI,
                 java.sql.PreparedStatement" %>
<%@ page import="com.logicboxes.util.Util" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.Hashtable" %>
<%--
  Created by IntelliJ IDEA.
  User: jignesh.r
  Date: Nov 5, 2007
  Time: 7:07:52 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%

    Logger log4j = new Logger("MyJSP");
    String FT=null;
    String transactionId=null;



    try
    {
    FT = ESAPI.validator().getValidInput("captureid",request.getParameter("captureid"),"Numbers",10,true);
    transactionId = ESAPI.validator().getValidInput("transid",request.getParameter("transid"),"Numbers",10,true);
    }
    catch(ValidationException e)
    {

       log4j.error("Invalid Input ",e);

    }



    Connection conn = null;
    Hashtable transactionDetails = null;
    Hashtable creditDetails = null;
    Hashtable reversalDetails = null;
    Hashtable otherReversalDetails = null;

    if (FT != null || transactionId != null)
    {
        try
        {
            conn = Database.getConnection();
            String liveDBQuery = null;
            if (FT != null && transactionId != null)
            {
                liveDBQuery = "select icicitransid,captureid as captureFT,refundid as refundFT,authcode,status,transid,amount,captureamount,refundamount,from_unixtime(dtstamp) as creationdt,timestamp from transaction_icicicredit where ( captureid = " + FT + " or refundid =  " + FT + " ) and transid =  " + transactionId;
            }
            else if (FT != null)
            {
                liveDBQuery = "select icicitransid,captureid as captureFT,refundid as refundFT,authcode,status,transid,amount,captureamount,refundamount,from_unixtime(dtstamp) as creationdt,timestamp from transaction_icicicredit where captureid = " + FT + " or refundid =  " + FT;
            }
            else if (transactionId != null)
            {
                liveDBQuery = "select icicitransid,captureid as captureFT,refundid as refundFT,authcode,status,transid,amount,captureamount,refundamount,from_unixtime(dtstamp) as creationdt,timestamp from transaction_icicicredit where transid = " + transactionId;
            }
            //out.println("LQ : " + liveDBQuery);
            ResultSet rs = Database.executeQuery(liveDBQuery, conn);

            transactionDetails = Util.getHashFromResultSet(rs);
            if (transactionDetails != null && transactionDetails.size() > 0)
            {
                transactionDetails = (Hashtable) transactionDetails.get("1");
            }
            else
            {
                String archiveDBQuery = null;
                if (FT != null && transactionId != null)
                {
                    archiveDBQuery = "select icicitransid,captureid as captureFT,refundid as refundFT,authcode,status,transid,amount,captureamount,refundamount,from_unixtime(dtstamp) as creationdt,timestamp from transaction_icicicredit_archive where ( captureid = " + FT + " or refundid =  " + FT + " ) and transid =  " + transactionId;
                }
                else if (FT != null)
                {
                    archiveDBQuery = "select icicitransid,captureid as captureFT,refundid as refundFT,authcode,status,transid,amount,captureamount,refundamount,from_unixtime(dtstamp) as creationdt,timestamp from transaction_icicicredit_archive where captureid = " + FT + " or refundid =  " + FT;
                }
                else if (transactionId != null)
                {
                    archiveDBQuery = "select icicitransid,captureid as captureFT,refundid as refundFT,authcode,status,transid,amount,captureamount,refundamount,from_unixtime(dtstamp) as creationdt,timestamp from transaction_icicicredit_archive where transid = " + transactionId;
                }
                //out.println("AQ : " + archiveDBQuery);
                rs = Database.executeQuery(archiveDBQuery, conn);
                transactionDetails = Util.getHashFromResultSet(rs);
                if (transactionDetails != null && transactionDetails.size() > 0)
                {
                    transactionDetails = (Hashtable) transactionDetails.get("1");
                }
                else
                {
                    transactionDetails = null;
                    out.println("No Matching transaction");
                }

            }

            String transId = (String) transactionDetails.get("transid");
            log4j.info(" Transid : " + transId);

            if (transId != null)
            {
                log4j.info("Transid not null getting details");
                String query="select *,from_unixtime(dtstamp) as EntryDate from transactions where transid = ?";
                PreparedStatement pstmt= conn.prepareStatement(query);
                pstmt.setString(1,transId);
                ResultSet rs1 = pstmt.executeQuery();

                creditDetails = Util.getHashFromResultSet(rs1);
                String query1="select *,from_unixtime(dtstamp) as EntryDate from transactions where type in ('chargeback','reversal','internaltransfer') and description like '% ? %'";
                PreparedStatement p1= conn.prepareStatement(query1);
                p1.setString(1,transId);
                ResultSet rs2 = p1.executeQuery();
                reversalDetails = Util.getHashFromResultSet(rs2);

                if (reversalDetails != null && reversalDetails.get("1") != null)
                {
                    String revTransid = (String) ((Hashtable) reversalDetails.get("1")).get("transid");
                    if (revTransid != null)
                    {
                        String query2="select *,from_unixtime(dtstamp) as EntryDate from transactions where description like '% ? %'";
                        PreparedStatement p2=conn.prepareStatement(query2);
                        p2.setString(1,revTransid);
                        ResultSet rs3 = p2.executeQuery();
                        otherReversalDetails = Util.getHashFromResultSet(rs3);

                    }
                }


            }

        }
        catch (Exception
                e)
        {
            try
            {
                Mail.sendmail("jignesh.r@directi.com", "aaa@directi.com", "CheckEntires .jsp", Util.getStackTrace(e));
            }
            catch (SystemError systemError)
            {
                // Ignore error while sending mail.
            }
        }
        finally
        {
            Database.closeConnection(conn);
        }


    }


%>

<html>
<head><title>All Entries For transaction</title></head>

<body>

<form action="checkEntries.jsp">
    FT : <input type="text" maxlength="10"  name="captureid"> OR <br>
    Credit transaction Id : <input type="text" maxlength="10"  name="transid">
    <input type="submit" value="Get Details">
</form>

<%
    if (transactionDetails != null)
    {
        //out.println("<br>" + transactionDetails.toString() + "<br>");
%>
<br><br>
Transaction Details:
<table border="1" cellpadding="1" cellspacing="1" align="center">
    <tr>
        <td>captureFT</td>
        <td>refundFT</td>
        <td>Auth code</td>
        <td>Tracking ID</td>
        <td>Status</td>
        <td>Transid</td>
        <td>Amount</td>
        <td>Capture Amount</td>
        <td>Refund Amount</td>
        <td>CreationDT</td>
        <td>Timestamp</td>
    </tr>

    <tr>
        <td><%=ESAPI.encoder().encodeForHTML((String)transactionDetails.get("captureFT"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)transactionDetails.get("refundFT"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)transactionDetails.get("authcode"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)transactionDetails.get("icicitransid"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)transactionDetails.get("status"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)transactionDetails.get("transid"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)transactionDetails.get("amount"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)transactionDetails.get("captureamount"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)transactionDetails.get("refundamount"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)transactionDetails.get("creationdt"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)transactionDetails.get("timestamp"))%></td>
    </tr>
</table>
<br><br>
<%
    }
    if (creditDetails != null)
    {
        creditDetails = (Hashtable) creditDetails.get("1");
        //out.println("<br>" + creditDetails.toString() + "<br>");
%>
<br><br>
Credit Details :
<table border="1" cellpadding="1" cellspacing="1" align="center">
    <tr>
        <td>Entry Date</td>
        <td>Transid</td>
        <td>Toid</td>
        <td>From Id</td>
        <td>Type</td>
        <td>Amount</td>
        <td>Sescription</td>
        <td>CreationDT</td>
        <td>Timestamp</td>
    </tr>

    <tr>
        <td><%=ESAPI.encoder().encodeForHTML((String)creditDetails.get("EntryDate"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)creditDetails.get("transid"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)creditDetails.get("toid"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)creditDetails.get("fromid"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)creditDetails.get("type"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)creditDetails.get("amount"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)creditDetails.get("description"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)creditDetails.get("dtstamp"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)creditDetails.get("timestamp"))%></td>
    </tr>
</table>
<br><br>
<%
    }
    if (reversalDetails != null)
    {
        //out.println("<br>" + reversalDetails.toString() + "<br>");
%>
<br><br>
Reversal/Chargeback Details :
<table border="1" cellpadding="1" cellspacing="1" align="center">
    <tr>
        <td>Entry Date</td>
        <td>TransId</td>
        <td>Toid</td>
        <td>From Id</td>
        <td>Type</td>
        <td>Amount</td>
        <td>Sescription</td>
        <td>CreationDT</td>
        <td>Timestamp</td>
    </tr>
    <%
        for (int i = 1; i <= reversalDetails.size(); i++)
        {
            Hashtable innerHash = (Hashtable) reversalDetails.get(String.valueOf(i));
    %>
    <tr>
        <td><%=ESAPI.encoder().encodeForHTML((String)innerHash.get("EntryDate"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)innerHash.get("transid"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)innerHash.get("toid"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)innerHash.get("fromid"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)innerHash.get("type"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)innerHash.get("amount"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)innerHash.get("description"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)innerHash.get("dtstamp"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)innerHash.get("timestamp"))%></td>
    </tr>
    <%
        }
    %>
</table>
<br><br>
<%
    }
%>

<%
    if (otherReversalDetails != null)
    {
%>
<br><br>
Any other Details :
<table border="1" cellpadding="1" cellspacing="1" align="center">
    <tr>
        <td>Entry Date</td>
        <td>Transid</td>
        <td>Toid</td>
        <td>From Id</td>
        <td>Type</td>
        <td>Amount</td>
        <td>Sescription</td>
        <td>CreationDT</td>
        <td>Timestamp</td>
    </tr>

    <%
        for (int i = 1; i <= otherReversalDetails.size(); i++)
        {
            Hashtable innerHash = (Hashtable) otherReversalDetails.get(String.valueOf(i));
    %>

    <tr>
        <td><%=ESAPI.encoder().encodeForHTML((String)innerHash.get("EntryDate"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)innerHash.get("transid"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)innerHash.get("toid"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)innerHash.get("fromid"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)innerHash.get("type"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)innerHash.get("amount"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)innerHash.get("description"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)innerHash.get("dtstamp"))%></td>
        <td><%=ESAPI.encoder().encodeForHTML((String)innerHash.get("timestamp"))%></td>
    </tr>
    <%
        }
    %>
</table>
<br><br>
<%
    }
%>


</body>
</html>