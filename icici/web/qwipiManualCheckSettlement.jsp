<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.directi.pg.*" %>
<%--
  Created by IntelliJ IDEA.
  User: admin
  Date: 6/12/13
  Time: 6:59 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="qwipitab.jsp"%>

<html>
<head>
    <title></title>
</head>

<div class="reporttable">
    <%
        session.setAttribute("submit","Manual Settlement");
    %>
<h4 align="center"><p class="textb">QWIPI Manual Settlement Transaction</p></h4>
<%
    Logger logger = new Logger("qwipiManualCheckSettlement.jsp");
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {

        String trackingid="";
        if (!ESAPI.validator().isValidInput("trackingid ",request.getParameter("trackingid"),"Numbers",15,false))
        {
            out.println(Functions.NewShowConfirmation("Sorry", "Please enter valid transaction ID . Only numeric value is allowed."));
            return;
        }
        else
        {
            trackingid=request.getParameter("trackingid");
            String amount="";
            String accountId="";
            Connection con = null;
            try
            {
                con = Database.getConnection();
                String qry = "select captureamount,accountid from transaction_qwipi where trackingid=? and status='capturesuccess'";
                PreparedStatement p = con.prepareStatement(qry);
                p.setString(1, trackingid);
                ResultSet rs = p.executeQuery();
                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId("0");
                auditTrailVO.setActionExecutorName("Admin");
                if (rs.next())
                {
                    amount = rs.getString("captureamount");
                    accountId = rs.getString("accountid");
                    TransactionEntry transactionEntry = null;
                    BigDecimal amt = new BigDecimal(amount);
                    amt = amt.setScale(2, BigDecimal.ROUND_HALF_UP);

                    transactionEntry = new TransactionEntry();
                    transactionEntry.newGenericCreditTransaction(String.valueOf(trackingid), amt, String.valueOf(accountId), null, auditTrailVO);
                    out.println(Functions.NewShowConfirmation("Success", "Trackingid :" + trackingid + " is successfully SETTLED ."));

                }
                else
                {
                    out.println(Functions.NewShowConfirmation("Sorry", "Trackingid :" + trackingid + " does not exist in Database."));
                    return;
                }
            }
            catch (Exception e)
            {
                logger.error("Exception:::"+e);
            }
            finally
            {
                Database.closeConnection(con);
            }
        }
    }
    else
    {
        response.sendRedirect("/icici/logout.jsp");
        return;
    }
%>
</div>
</body>
</html>