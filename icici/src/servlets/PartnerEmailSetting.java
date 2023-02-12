import com.directi.pg.*;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;

/* Created by IntelliJ IDEA.
        User: Mahima
        Date: 27/02/18
        Time: 3:22 PM
        To change this template use File | Settings | File Templates.
*/
public class PartnerEmailSetting extends HttpServlet
{
    private static Logger logger = new Logger(PartnerEmailSetting.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String errorMsg="";
        String partnerId="";

        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();

        int start = 0; // start index
        int end = 0; // end index
        int pageno=1;
        int records=15;

        if(!ESAPI.validator().isValidInput("partnerid", req.getParameter("partnerid"), "Numbers", 10, false)){
            errorMsg = errorMsg + "Invalid partnerId";
        }else{
            partnerId = req.getParameter("partnerid");
        }

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);

        start = (pageno - 1) * records;
        end = records;

        Hashtable hash = null;

        StringBuffer query=new StringBuffer("SELECT * from partner_configuration WHERE partnerId=?");
        StringBuffer countQuery = new StringBuffer("select count(*) from partner_configuration where partnerId=?");
        query.append(" order by partnerid asc LIMIT " + start + "," + end);
        Connection con = null;
        PreparedStatement p = null;
        PreparedStatement p1 = null;
        ResultSet rs = null;
        try
        {
            con = Database.getConnection();
            p=con.prepareStatement(query.toString());
            p.setString(1,partnerId);
            hash = Database.getHashFromResultSet(p.executeQuery());
            p1=con.prepareStatement(countQuery.toString());
            p1.setString(1,partnerId);
            rs = p1.executeQuery();
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");
            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            req.setAttribute("memberdetails", hash);
        }
        catch (SystemError se){
            logger.error("SystemError::::", se);
            sErrorMessage.append("Internal error while processing your request");
        }
        catch (Exception e){
            logger.error("Exception::::",e);
            sErrorMessage.append("Internal error while processing your request");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(p);
            Database.closePreparedStatement(p1);
            Database.closeConnection(con);
        }

        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        req.setAttribute("error", errorMsg);
        req.setAttribute("error", sErrorMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher("/partnerEmailSetting.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
}
