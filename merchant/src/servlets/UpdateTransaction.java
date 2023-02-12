import com.directi.pg.*;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
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
import java.util.HashMap;
import java.util.Hashtable;


/**
 * Created by Admin on 4/18/2018.
 */
public class UpdateTransaction extends HttpServlet
{

    private static Logger log = new Logger(UpdateTransaction.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        Merchants merchants = new Merchants();

        log.debug("Enter in UpdateTransaction");
        if (!merchants.isLoggedIn(session))
        {
            res.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        String status = "";

        Hashtable transactionMap = this.getTransactionDetails(req.getParameter("trackingid"));
        if("capturesuccess".equals((String)transactionMap.get("status")) || "authsuccessful".equals((String)transactionMap.get("status")))
            status = "Y";
        else if("authstarted".equals((String)transactionMap.get("status")))
            status = "P";
        else
            status = "N";

        //System.out.println("map---"+transactionMap);

        req.setAttribute("status",status);
        req.setAttribute("statusDesc",transactionMap.get("remark"));
        req.setAttribute("billingDescriptor",transactionMap.get("billingDescriptor"));
        //req.setAttribute("hiddenResponse",transactionMap);

        RequestDispatcher rd = req.getRequestDispatcher("/transactionSinglecall.jsp?ctoken="+user.getCSRFToken());
        log.debug("Forwarding to cancel.jsp");
        rd.forward(req, res);


    }

    public Hashtable getTransactionDetails(String trackingid)
    {
        Hashtable terminaldetails = new Hashtable();
        Connection con = null;
        PreparedStatement pstmt2 = null;
        ResultSet rs2 = null;
        try
        {
            con=Database.getConnection();
            //String query2 = "select status,remark from transaction_common where trackingid =?";
            String query2 = "SELECT t.status,t.remark,responsedescriptor FROM transaction_common AS t, transaction_common_details AS td WHERE t.trackingid=? AND t.trackingid=td.trackingid ORDER BY `detailid` DESC LIMIT 1";
            pstmt2 = con.prepareStatement(query2);
            pstmt2.setString(1, trackingid);
            //System.out.println("pstmt2==="+pstmt2);
            rs2 = pstmt2.executeQuery();
            if (rs2.next())
            {
                terminaldetails.put("status",rs2.getString("status")) ;
                terminaldetails.put("remark",rs2.getString("remark")) ;
                terminaldetails.put("billingDescriptor",rs2.getString("responsedescriptor")) ;
            }
        }
        catch (Exception e)
        {
            log.error("Exception occur", e);
        }
        finally
        {
            Database.closeResultSet(rs2);
            Database.closePreparedStatement(pstmt2);
            Database.closeConnection(con);
        }

        return terminaldetails;
    }
}
