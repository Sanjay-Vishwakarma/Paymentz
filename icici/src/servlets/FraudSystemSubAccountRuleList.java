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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;


/**
 * Created by Admin on 17/7/15.
 */
public class FraudSystemSubAccountRuleList extends HttpServlet
{
    static Logger logger = new Logger(FraudSystemSubAccountRuleList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {

        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        Connection conn = null;
        ResultSet rs = null;

        int records=15;
        int pageno=1;

        Hashtable hash = null;

        String fssubaccountid=req.getParameter("fssubaccountid");
        String status=req.getParameter("status");

        StringBuffer sb=new StringBuffer();
        RequestDispatcher rd = req.getRequestDispatcher("/fraudSystemSubAccountRuleList.jsp?ctoken="+user.getCSRFToken());

        if (!ESAPI.validator().isValidInput("status",status,"SafeString",25,true))
        {
            sb.append("Invalid Status<BR>");
        }
        if (!ESAPI.validator().isValidInput("fsaccountid",fssubaccountid,"Numbers",11,true))
        {
            sb.append("Invalid Fraud Sub Account<BR>");
        }
        if(sb.length()>0)
        {
            req.setAttribute("statusMsg",sb.toString());
            rd.forward(req,res);
            return;
        }

        int start = 0; // start index
        int end = 0; // end index

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;

        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select rm.ruleid,rm.rulename,sarm.score,sarm.value,sarm.status,sarm.fssubaccountid,fsam.subaccountname from subaccount_rule_mapping as sarm join rule_master as rm on sarm.ruleid=rm.ruleid join fsaccount_subaccount_mapping as fsam on sarm.fssubaccountid=fsam.fssubaccountid");
            StringBuffer countquery = new StringBuffer("select count(*) from subaccount_rule_mapping as sarm join rule_master as rm on sarm.ruleid=rm.ruleid join fsaccount_subaccount_mapping as fsam on sarm.fssubaccountid=fsam.fssubaccountid");

            Functions functions=new Functions();

            if(functions.isValueNull(fssubaccountid))
            {
                query.append(" and sarm.fssubaccountid='"+fssubaccountid+"'");
                countquery.append(" and sarm.fssubaccountid='"+fssubaccountid+"'");
            }
            if(functions.isValueNull(status))
            {
                query.append(" and sarm.status='"+status+"'");
                countquery.append(" and sarm.status='"+status+"'");
            }
            query.append(" LIMIT " + start + "," + end);

            logger.debug("Query:-" + query);
            logger.debug("CountQuery:-" + countquery);

            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));
            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));

            req.setAttribute("transdetails", hash);

        }
        catch (SystemError s)
        {
            logger.error("System error while perform select query", s);
            sb.append("System error while perform select query"+s);

        }
        catch (SQLException e)
        {
            logger.error("SQL error", e);
            sb.append("SQL Exception while perform select query"+ e);

        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        req.setAttribute("statusMsg",sb.toString());
        rd.forward(req,res);
        return;
    }
}
