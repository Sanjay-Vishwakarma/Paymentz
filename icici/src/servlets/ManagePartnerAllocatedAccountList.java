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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
/**
 Created by IntelliJ IDEA.
 User: Shipra
 Date: 10/12/18
 **/
public class ManagePartnerAllocatedAccountList extends HttpServlet
{
    private static Logger logger = new Logger(ManagePartnerAllocatedAccountList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doProcess(request, response);
    }
    public void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("session out");
            response.sendRedirect("/icici/sessionout.jsp");
            return;
        }
        RequestDispatcher rd = request.getRequestDispatcher("/managePartnerAllocatedAccountList.jsp?ctoken="+user.getCSRFToken());

        String partnerid = null;
        String fsAccount = null;
        partnerid = request.getParameter("partnerid");
        fsAccount = request.getParameter("fsAccount");
        System.out.println("partnerid::"+partnerid);
        System.out.println("fsAccount::"+fsAccount);

        StringBuffer errorMsg = new StringBuffer();
        if (!ESAPI.validator().isValidInput("partnerid", partnerid, "Numbers", 10, true))
        {
            errorMsg.append("Invalid Partner ID<BR>");
        }
        if (!ESAPI.validator().isValidInput("fsAccount", fsAccount, "SafeString", 100, true))
        {
            errorMsg.append("Invalid FraudSystem Account<BR>");
        }
        if(errorMsg.length()>0)
        {
            request.setAttribute("statusMsg", errorMsg.toString());
            rd.forward(request, response);
            return;
        }

        Functions functions = new Functions();
        Hashtable hash = null;
        int records=15;
        int pageno=1;
        int start = 0; // start index
        int end = 0; // end index
        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        Connection conn = null;
        ResultSet rs = null;
        try
        {
            System.out.println("Inside try");
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("SELECT pfs.`partnerid`,pfs.`fsaccountid`,fam.accountname,pfs.`isActive` FROM partner_fsaccounts_mapping AS pfs JOIN `fraudsystem_account_mapping` AS fam ON fam.fsaccountid=pfs.`fsaccountid` ");
            StringBuffer countquery = new StringBuffer("select count(*) from partner_fsaccounts_mapping As pfs join fraudsystem_account_mapping As fam ON fam.fsaccountid=pfs.`fsaccountid`");
            if(functions.isValueNull(partnerid))
            {
                query.append(" and pfs.partnerid=" + ESAPI.encoder().encodeForSQL(me,partnerid));
                countquery.append(" and pfs.partnerid=" + ESAPI.encoder().encodeForSQL(me,partnerid));
            }
            if(functions.isValueNull(fsAccount))
            {
                query.append(" and fsam.fsaccountid=" + ESAPI.encoder().encodeForSQL(me,fsAccount));
                countquery.append(" and fsam.fsaccountid=" + ESAPI.encoder().encodeForSQL(me,fsAccount));
            }
            query.append(" order by pfs.partnerid desc LIMIT " + start + "," + end);

            logger.debug("Query:-" + query);
            logger.debug("CountQuery:-"+countquery);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);
            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");
            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            request.setAttribute("transdetails", hash);
            rd.forward(request, response);
        }
        catch (SystemError s)
        {
            logger.error("SystemError::::::",s);
            request.setAttribute("statusMsg","Internal Error while processing your request");
            rd.forward(request,response);
            return;
        }
        catch (SQLException e)
        {
            logger.error("SQLException::::::",e);
            request.setAttribute("statusMsg","Internal Error while processing your request");
            rd.forward(request,response);
            return;
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
    }
}
