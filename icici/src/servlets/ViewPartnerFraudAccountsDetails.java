import com.directi.pg.*;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

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
import java.sql.SQLException;
import java.util.Hashtable;
/**
 Created by IntelliJ IDEA.
 User: Supriya
 Date: 10/2/16
 Time: 4:50 PM
 **/
public class ViewPartnerFraudAccountsDetails extends HttpServlet
{
    private static Logger log = new Logger(ViewPartnerFraudAccountsDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("ctoken==="+req.getParameter("ctoken"));
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        String partnerId = null;
        String partnerName = null;
        partnerId = req.getParameter("partnerid");
        partnerName=req.getParameter("partnerName");

        RequestDispatcher rd = req.getRequestDispatcher("/viewPartnerFraudAccountsDetails.jsp?ctoken="+user.getCSRFToken());
        StringBuffer errorMsg = new StringBuffer();

        if (!ESAPI.validator().isValidInput("partnerid",partnerId, "Numbers", 10, true))
        {
            errorMsg.append("Invalid Partner <BR>");
        }
        if (!ESAPI.validator().isValidInput("partnername", partnerName, "SafeString", 255, true))
        {
            errorMsg.append("Invalid Partner Name <BR>");
        }
        if(errorMsg.length()>0)
        {
            req.setAttribute("statusMsg", errorMsg.toString());
            rd.forward(req,res);
            return;
        }

        Functions functions = new Functions();
        Hashtable hash = null;
        int records=15;
        int pageno=1;
        int start = 0; // start index
        int end = 0; // end index
        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        Connection conn = null;
        PreparedStatement ps=null;
        ResultSet rs=null;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT p.partnerId,fs.fsname,fam.accountname,p.partnerName,pfm.isActive FROM partner_fsaccounts_mapping AS pfm JOIN fraudsystem_account_mapping AS fam ON pfm.fsaccountid=fam.fsaccountid JOIN fraudsystem_master AS fs ON fs.fsid=fam.fsid JOIN partners AS p ON p.partnerId=pfm.partnerid and p.partnerId>0 ");
            StringBuffer countquery = new StringBuffer("SELECT COUNT(*) FROM partner_fsaccounts_mapping AS pfm JOIN fraudsystem_account_mapping AS fam ON pfm.fsaccountid=fam.fsaccountid JOIN fraudsystem_master AS fs ON fs.fsid=fam.fsid JOIN partners AS p ON p.partnerId=pfm.partnerid and p.partnerId>0  ");

            if (functions.isValueNull(partnerId))
            {
                query.append(" and pfm.partnerid=" + ESAPI.encoder().encodeForSQL(me,partnerId));
                countquery.append(" and pfm.partnerid=" + ESAPI.encoder().encodeForSQL(me,partnerId));
            }
            query.append(" order by pfm.partnerid LIMIT " + start + "," + end);

            log.debug("Query:-"+query);
            log.debug("CountQuery:-" + countquery);

            ps = conn.prepareStatement(query.toString());
            rs = ps.executeQuery();
            hash = Database.getHashFromResultSetForTransactionEntry(rs);

            ps=conn.prepareStatement(countquery.toString());
            rs = ps.executeQuery();
            rs.next();

            int totalrecords = rs.getInt(1);
            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            req.setAttribute("transdetails", hash);
            req.setAttribute("partnerid", partnerId);
            rd.forward(req, res);
        }
        catch (SystemError s)
        {
            log.error("SystemError::::::",s);
            req.setAttribute("statusMsg","Internal error while processing your request");
            rd.forward(req,res);
            return;
        }
        catch (SQLException e)
        {
            log.error("SQLException::::::",e);
            req.setAttribute("statusMsg","Internal error while processing your request");
            rd.forward(req,res);
            return;
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closePreparedStatement(ps);
            Database.closeConnection(conn);
        }
    }
}
