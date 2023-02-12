<%@ page import="org.owasp.esapi.ESAPI" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.directi.pg.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="com.payment.statussync.StatusSyncDAO" %>
<%@ include file="index.jsp"%>
<%--
  Created by IntelliJ IDEA.
  User: Naushad
  Date: 11/14/15
  Time: 2:56 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<META content="text/html; charset=windows-1252" http-equiv=Content-Type>
<meta http-equiv="Expires" content="0">
<meta http-equiv="Pragma" content="no-cache">
<html>
<head>Common Manual Settlement</head>
<body>
<%
  ctoken = ((User)session.getAttribute("ESAPIUserSessionKey")).getCSRFToken();
  session.setAttribute("submit","Manual Settlement");
  Logger logger = new Logger("commonManualCheckSettlement.jsp");
  if (com.directi.pg.Admin.isLoggedIn(session))
  {
  %>
<form action="/icici/commonManualCheckSettlement.jsp?ctoken=<%=ctoken%>" method="post">
  <div class="row" style="margin-top: 0px">
    <div class="col-lg-12">
      <div class="panel panel-default" style="margin-top: 100px">
        <div class="panel-heading" >
          Common Manual Settlement
        </div>
        <input type="hidden" value="<%=ctoken%>" name="ctoken">
        <table  align="center" width="80%" cellpadding="2" cellspacing="2" >
          <tr>
            <td>
              <table border="0" cellpadding="5" cellspacing="0" width="100%"  align="center">
                <tr>
                  <td colspan="4">&nbsp;</td>
                </tr>
                <tr>
                  <td class="textb">&nbsp;</td>
                  <td  class="textb" >Tracking Id</td>
                  <td  class="textb"></td>
                  <td  class="textb">
                    <input maxlength="15" type="text" name="trackingid" class="txtbox" value="">
                  </td>
                  <td  class="textb">&nbsp;</td>
                  <td  class="textb"></td>
                  <td  class="textb"></td>
                  <td  class="textb">
                    <button type="submit" class="buttonform" style="margin-left:40px; ">
                      <i class="fa fa-clock-o"></i>
                      &nbsp;&nbsp;Process
                    </button>
                  </td>
                </tr>
                <tr>
                  <td colspan="4">&nbsp;</td>
                </tr>
              </table>
            </td>
          </tr>
        </table>
      </div>
    </div>
  </div>
</form>
  <%--<h4 align="center" style="margin-top: 70px"><p class="textb">Common Manual Settlement</p></h4>--%>
  <%
      String trackingid="";
      if (!ESAPI.validator().isValidInput("trackingid ",request.getParameter("trackingid"),"Numbers",15,false))
      {
        out.println("<div class=\"reporttable\"");
        out.println(Functions.NewShowConfirmation("Sorry", "Please enter valid trackingId"));
        out.println("</div>");
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
          String qry = "select captureamount,accountid from transaction_common where trackingid=? and status='capturesuccess'";
          PreparedStatement p = con.prepareStatement(qry);
          p.setString(1, trackingid);
          ResultSet rs = p.executeQuery();
          AuditTrailVO auditTrailVO = new AuditTrailVO();
          auditTrailVO.setActionExecutorId("0");
          auditTrailVO.setActionExecutorName("Admin" + "-" + session.getAttribute("username").toString());
          if (rs.next())
          {
            amount = rs.getString("captureamount");
            accountId = rs.getString("accountid");
            TransactionEntry transactionEntry = null;
            StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
            BigDecimal amt = new BigDecimal(amount);
            amt = amt.setScale(2, BigDecimal.ROUND_HALF_UP);

            transactionEntry = new TransactionEntry();
            transactionEntry.newGenericCreditTransaction(String.valueOf(trackingid), amt, String.valueOf(accountId), null, auditTrailVO);
            statusSyncDAO.updateStatusSyncTransactionCronFlag(trackingid,"settled",con);
            out.println("<div class=\"reporttable\">");
            out.println(Functions.NewShowConfirmation("Success", "Trackingid :" + trackingid + " is successfully SETTLED ."));
            out.println("</div>");
            return;
          }
          else
          {
            out.println("<div class=\"reporttable\">");
            out.println(Functions.NewShowConfirmation("Sorry", " TrackingId :" + trackingid + " does not exist in system or not available in captured mode."));
            out.println("</div>");
            return;
          }
        }
        catch (Exception e)
        {
          out.println("<div class=\"reporttable\">");
          out.println(Functions.NewShowConfirmation("Sorry",e.getMessage()));
          out.println("</div>");
          //logger.error("Exception:::"+e);
        }
        finally
        {
          Database.closeConnection(con);
        }
      }

  %>
</div>
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