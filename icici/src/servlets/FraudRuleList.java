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
 * Created with IntelliJ IDEA.
 * User: kiran
 * Date: 7/15/15
 * Time: 3:51PM
 * To change this template use File | Settings | File Templates.
 */

public class FraudRuleList extends HttpServlet
{
    static Logger logger = new Logger(FraudRuleList.class.getName());
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

        String ruleid=req.getParameter("ruleid");
        String ruleName=req.getParameter("rulename");
        String ruleGroup=req.getParameter("rulegroup");
        String ruleDefaultScore=req.getParameter("score");

        StringBuffer sb=new StringBuffer();
        //RequestDispatcher rd = req.getRequestDispatcher("/fraudRuleList.jsp?ctoken="+user.getCSRFToken());

        if (!ESAPI.validator().isValidInput("ruleid",ruleid,"Numbers",10,true))
        {
            sb.append("Invalid Rule Id,");
        }
        if (!ESAPI.validator().isValidInput("rulename",ruleName,"Description",255,true))
        {
            sb.append("Invalid Rule Name,");
        }
        if (!ESAPI.validator().isValidInput("rulegroup",ruleGroup,"Description",255,true))
        {
            sb.append("Invalid Rule Group,");
        }
        if (!ESAPI.validator().isValidInput("ruledefaultscore",ruleDefaultScore,"Numbers",2,true))
        {
            sb.append("Invalid Default Score,");
        }
        if(sb.length()>0)
        {
            req.setAttribute("statusMsg",sb.toString());
            RequestDispatcher rd = req.getRequestDispatcher("/fraudRuleList.jsp?ctoken="+user.getCSRFToken());
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
            StringBuffer query = new StringBuffer("select * from rule_master where ruleid>0 ");
            StringBuffer countquery = new StringBuffer("select count(*) from rule_master where ruleid>0 ");

            Functions functions=new Functions();
            if(functions.isValueNull(ruleid))
            {
                query.append(" and ruleid='"+ruleid+"'");
                countquery.append(" and ruleid='"+ruleid+"'");
            }
            if(functions.isValueNull(ruleName))
            {
                query.append(" and rulename='"+ruleName+"'");
                countquery.append(" and rulename='"+ruleName+"'");
            }
            if(functions.isValueNull(ruleDefaultScore))
            {
                query.append(" and score='"+ruleDefaultScore+"'");
                countquery.append(" and score='"+ruleDefaultScore+"'");
            }
            if(functions.isValueNull(ruleGroup))
            {
                query.append(" and rulegroup='"+ruleGroup+"'");
                countquery.append(" and rulegroup='"+ruleGroup+"'");
            }
            query.append(" order by ruleid desc LIMIT " + start + "," + end);

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
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (SQLException e)
        {
            logger.error("SQL error", e);
            sb.append("SQL Exception while perform select query"+ e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        req.setAttribute("statusMsg",sb.toString());
        RequestDispatcher rd = req.getRequestDispatcher("/fraudRuleList.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req,res);
        return;
    }
}
