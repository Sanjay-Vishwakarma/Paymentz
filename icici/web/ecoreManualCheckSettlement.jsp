<%@ page import="com.directi.pg.TransactionEntry" %>
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
<%@ include file="ecoretab.jsp" %>
<html>
<head>
    <title></title>
</head>
<body>
<div class="reporttable">
<p align="center" class="textb"><b>Ecore Manual Settlement Transaction</b></p>
<%!private static Logger logger = new Logger("ecoreManualCheckSettlement.jsp");%>
<%
    session.setAttribute("submit","Manual Settlement");
    ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
    if (com.directi.pg.Admin.isLoggedIn(session))
    {

        String trackingid="";
        if (!ESAPI.validator().isValidInput("trackingid ",request.getParameter("trackingid"),"Numbers",15,false))
        {
            out.println(Functions.NewShowConfirmation("Sorry", "Please enter valid transactionID . allow only numeric value."));
            return;
        }
        else
        {
            Connection con = null;
            try
            {
                trackingid = request.getParameter("trackingid");
                String amount = "";
                String accountId = "";
                con = Database.getConnection();
                String qry = "select captureamount,accountid from transaction_ecore where trackingid=? and status='capturesuccess'";
                PreparedStatement p = con.prepareStatement(qry);
                p.setString(1, trackingid);
                ResultSet rs = p.executeQuery();
                AuditTrailVO auditTrailVO=new AuditTrailVO();
                auditTrailVO.setActionExecutorId("0");
                auditTrailVO.setActionExecutorId("Admin");
                if (rs.next())


                {
                    amount = rs.getString("captureamount");
                    accountId = rs.getString("accountid");
                    TransactionEntry transactionEntry = null;
                    BigDecimal amt = new BigDecimal(amount);
                    amt = amt.setScale(2, BigDecimal.ROUND_HALF_UP);
                    try
                    {
                        transactionEntry = new TransactionEntry();
                        transactionEntry.newGenericCreditTransaction(String.valueOf(trackingid), amt, String.valueOf(accountId), null,auditTrailVO);
                        out.println(Functions.ShowMessage("Success", "Trackingid :" + trackingid + " is successfully SETTLED ."));
                    }
                    catch (SystemError se)
                    {
                        out.println(Functions.NewShowConfirmation("ERROR", "[Manual Settlement Cron] : Error while settling the transaction for Tracking ID = " + trackingid + ". Exception =" + se));
                    }

                }
                else
                {
                    out.println(Functions.NewShowConfirmation("Sorry", "Trackingid :" + trackingid + " does not exist in system."));
                    return;
                }
            }
            catch (Exception e)
            {

                logger.error("Sql Exception in ecoreManualCheckSettlement:", e);
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