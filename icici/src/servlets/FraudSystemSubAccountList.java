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
 * Created by Sneha on 17/7/15.
 */
public class FraudSystemSubAccountList extends HttpServlet
{
    private static Logger logger = new Logger(FraudSystemSubAccountList.class.getName());
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

        Connection conn = null;
        ResultSet rs = null;
        int records=15;
        int pageno=1;

        String fsAccountId = null;
        String fsSubAccountId = null;

        StringBuffer sb=new StringBuffer();

        Functions functions = new Functions();
        Hashtable hash = null;

        fsAccountId = request.getParameter("fsaccountid");
        fsSubAccountId = request.getParameter("fssubaccountid");

        RequestDispatcher rd = request.getRequestDispatcher("/fraudSystemSubAccountList.jsp?ctoken="+user.getCSRFToken());
        if (!ESAPI.validator().isValidInput("fsaccountId", fsAccountId, "Numbers", 11, true))
        {
            sb.append("Invalid Fraud System Account Id<BR>");
        }
        if (!ESAPI.validator().isValidInput("fssubaccountId", fsSubAccountId, "Numbers", 11, true))
        {
            sb.append("Invalid Fraud Sub Account Id<BR>");
        }
        if(sb.length()>0)
        {
            request.setAttribute("statusMsg", sb.toString());
            logger.error(sb.toString());
            rd.forward(request, response);
            return;
        }

        int start = 0; // start index
        int end = 0; // end index

        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);

        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select asam.fsaccountid,asam.fssubaccountid,asam.subaccountname,asam.subusername,asam.isactive,fsm.fsname,fsam.accountname from fsaccount_subaccount_mapping as asam join fraudsystem_account_mapping as fsam on asam.fsaccountid=fsam.fsaccountid join fraudsystem_master as fsm on fsam.fsid=fsm.fsid ");
            StringBuffer countquery = new StringBuffer("select count(*) from fsaccount_subaccount_mapping as asam join fraudsystem_account_mapping as fsam on asam.fsaccountid=fsam.fsaccountid join fraudsystem_master as fsm on fsam.fsid=fsm.fsid ");
            if(functions.isValueNull(fsAccountId))
            {
                query.append(" and asam.fsaccountid=" + ESAPI.encoder().encodeForSQL(me,fsAccountId));
                countquery.append(" and asam.fsaccountid=" + ESAPI.encoder().encodeForSQL(me,fsAccountId));
            }
            if(functions.isValueNull(fsSubAccountId))
            {
                query.append(" and asam.fssubaccountid=" + ESAPI.encoder().encodeForSQL(me,fsSubAccountId));
                countquery.append(" and asam.fssubaccountid=" + ESAPI.encoder().encodeForSQL(me,fsSubAccountId));
            }
            query.append(" LIMIT " + start + "," + end);
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
            logger.debug("forward to jsp" + hash);
        }
        catch (SystemError systemError)
        {
            logger.error(systemError.getMessage());
            sb.append(systemError.getMessage());

        }
        catch (SQLException e)
        {
            logger.error(e.getMessage());
            sb.append(e.getMessage());
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        request.setAttribute("ststusMsg",sb.toString());
        rd.forward(request,response);
    }
}
