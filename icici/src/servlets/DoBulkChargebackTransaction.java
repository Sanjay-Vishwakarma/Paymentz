
import com.directi.pg.*;
import com.logicboxes.util.Util;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: May 27, 2012
 * Time: 8:36:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class DoBulkChargebackTransaction extends HttpServlet
{
     static Logger logger = new Logger(DoBulkChargebackTransaction.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");


            if (!Admin.isLoggedIn(session))
            {   logger.debug("invalid user");
                res.sendRedirect("/icici/admin/logout.jsp");
                return;
            }
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        Transaction transaction = new Transaction();
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement p1 = null;
        PreparedStatement p2 = null;
        ResultSet rsCbTrLst = null;
        TransactionEntry transactionEntry = null;
        try
        {
            conn = Database.getConnection();
            transactionEntry = new TransactionEntry();


            String[] icicitransidStr = req.getParameterValues("chargeback");

            if (Functions.checkArrayNull(icicitransidStr) == null)
            {
                out.print(Functions.ShowMessage("Error", "Select at least one transaction"));
                return;
            }



            String cbDate = null;
            String cbRefNumber = null;
            String cbAmount = null;
            String cbReason = null;
            String cbPartial = null;
            String fileName = null;
            StringBuilder sSuccessMessage = new StringBuilder();
            StringBuilder sErrorMessage = new StringBuilder();
            sErrorMessage.append("Following Transactions are failed while Chargeback Processing <br> \r\n");
            sSuccessMessage.append("Following Transactions are successful during Chargeback Processing <br>  \r\n");


            for (String icicitransid : icicitransidStr)
            {
                int intChargebackProcessId = -1;
                try
                {
                    intChargebackProcessId = Integer.parseInt(req.getParameter("hid_" + icicitransid ));
                }
                catch (NumberFormatException ex)
                {
                    logger.error("Invalid Chargeback Process Id" + req.getParameter("hid_" + icicitransid  + " for Tracking Id="+icicitransid));
                    sErrorMessage.append("Tracking Id="+icicitransid +"<br>");
                    continue;
                }
                /*PreparedStatement pstmt = null;
                PreparedStatement p1 = null;
                PreparedStatement p2 = null;
                ResultSet rsCbTrLst = null;*/
                String sQuery = "Select CT.icicitransid,CT.cb_date,CT.amount,CT.cb_reason,CT.cb_partial,CH.file_name from chargeback_transaction_list as CT, chargeback_report_process_history as CH where CT.fk_chargeback_process_id = CH.chargeback_process_id and icicitransid=? and fk_chargeback_process_id=?";
                try
                {   pstmt= conn.prepareStatement(sQuery);
                    pstmt.setString(1,icicitransid);
                    pstmt.setInt(2,intChargebackProcessId);
                    rsCbTrLst = pstmt.executeQuery();
                    if(rsCbTrLst.next())
                    {
                    cbDate = rsCbTrLst.getString("cb_date");
                    fileName = rsCbTrLst.getString("file_name");
                    cbRefNumber = fileName.substring(0, fileName.length() - 4) + "-" + Calendar.getInstance().getTimeInMillis();
                    cbAmount = rsCbTrLst.getString("amount");
                    cbReason = rsCbTrLst.getString("cb_reason");
                    cbPartial= rsCbTrLst.getString("cb_partial");
                    }
                }
                catch(Exception se)
                {
                    logger.error("Error while Fetching Data from Chargeback_Transaction_list=" + se + " for Tracking Id="+icicitransid,se);
                    sErrorMessage.append("Tracking Id="+icicitransid +"<br>");
                    continue;
                }

                try
                {
                    //Processing Chargeback

                    transaction.processChargeback(icicitransid, cbDate, cbRefNumber, cbAmount, cbReason, cbPartial);
                }
                catch (SystemError se)
                {
                    logger.error("Error while Processing Chargeback=" + se.getMessage() + " for Tracking Id="+icicitransid);
                    sErrorMessage.append("Tracking Id="+icicitransid +"<br>");
                    continue;
                }
                catch (Exception e)
                {
                    logger.info("Error while Processing Chargeback=" + e.getMessage() + " for Tracking Id="+icicitransid);
                    sErrorMessage.append("Tracking Id="+icicitransid +"<br>");
                    continue;
                }
                try
                {
                    //Marking this transaction as processed
                    sQuery = "update chargeback_transaction_list set processed='Y' where icicitransid=? and fk_chargeback_process_id=?";
                    p1= conn.prepareStatement(sQuery);

                    p1.setString(1,icicitransid);
                    p1.setInt(2,intChargebackProcessId);
                    p1.executeUpdate();

                    sQuery = "update chargeback_report_process_history set parsed='Y' where chargeback_process_id=? and no_of_rows=no_of_transactions and (select count(*) from chargeback_transaction_list where fk_chargeback_process_id=? and processed!='y')=0";
                    p2= conn.prepareStatement(sQuery);

                    p2.setInt(1,intChargebackProcessId);
                    p2.setInt(2,intChargebackProcessId);
                    p2.executeUpdate();

                    sSuccessMessage.append("Tracking Id="+icicitransid + "<br>" );
                }
                catch (Exception e)
                {
                    logger.error("Error while Processing Chargeback=" + e.getMessage() + " for Tracking Id="+icicitransid);
                    sErrorMessage.append("Transaction successfully chargeback but not marked as 'Y' in chargeback_transaction_list. Tracking Id="+icicitransid +"<br>");

                }

                SendTransactionEventMailUtil sendTransactionEventMail=new SendTransactionEventMailUtil();
                sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.CHARGEBACK_TRANSACTION,icicitransid,"",cbReason,null);

            }

            StringBuilder chargeBackMessage = new StringBuilder();
            chargeBackMessage.append(sSuccessMessage.toString());
            chargeBackMessage.append("<BR/>");
            chargeBackMessage.append(sErrorMessage.toString());

            String redirectpage = "/servlet/AdminChargebackList?ctoken="+user.getCSRFToken();
            req.setAttribute("cbmessage", chargeBackMessage.toString());
            RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
            rd.forward(req, res);

        }

        catch (SystemError systemError)
        {
            logger.info("Error while Chargeback :" + Util.getStackTrace(systemError));
            out.print(Functions.ShowMessage("Error", "Error while connecting to Database"));
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closePreparedStatement(p1);
            Database.closePreparedStatement(p2);
            Database.closeResultSet(rsCbTrLst);
            Database.closeConnection(conn);
        }

    }
}
