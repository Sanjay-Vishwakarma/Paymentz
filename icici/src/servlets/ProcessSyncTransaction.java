
import com.directi.pg.*;
import com.logicboxes.util.Util;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import org.owasp.esapi.User;


/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Jul 4, 2012
 * Time: 2:18:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class ProcessSyncTransaction extends HttpServlet
{
    private static Logger logger = new Logger(ProcessSyncTransaction.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
     public synchronized void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

            logger.debug("success");

            if (!Admin.isLoggedIn(session))
            {   logger.debug("invalid user");
                res.sendRedirect("/icici/admin/logout.jsp");
                return;
            }
        PrintWriter out = res.getWriter();
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        Connection conn = null;
        PreparedStatement p1 = null;
        try
        {
        conn = Database.getConnection();



        String[] icicitransidStr = req.getParameterValues("sync");

        sErrorMessage.append("Following Transactions are failed while Processing in Sync Transaction <br> \r\n");
        sSuccessMessage.append("Following Transactions are successful during Processing in Sync Transaction <br> \r\n");

            for (String icicitransid : icicitransidStr)
            {
                if (!icicitransid.trim().equals(""))
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
                try
                {
                  conn = Database.getConnection();
                    String query = "update chargeback_transaction_list set processed='Y' where icicitransid=?";
                    p1=conn.prepareStatement(query);
                    p1.setString(1,icicitransid);
                    int rs = p1.executeUpdate();


                    if (rs != 1)
                    {
                        logger.error("This transaction cannot be Sync for Tracking Id="+icicitransid);
                        sErrorMessage.append("Tracking Id="+icicitransid +"<br>");
                        continue;

                    }
                    sSuccessMessage.append("Tracking Id="+icicitransid + "<br>" );
                }
                catch (Exception e)
                {
                    logger.error("SQL ERROR",e);
                    sErrorMessage.append("Tracking Id="+icicitransid +"<br>");
                    continue;

                }
             }
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
            Database.closePreparedStatement(p1);
            Database.closeConnection(conn);
        }
    }
}