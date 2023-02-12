import com.directi.pg.*;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;
import org.owasp.esapi.errors.ValidationException;

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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: saurabh
 * Date: 2/11/14
 * Time: 5:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class ListAgentDetails extends HttpServlet
{
    static Logger log = new Logger(ListAgentDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in ListAgentDetails");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        log.debug("ctoken==="+req.getParameter("ctoken"));
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }


        Connection conn = null;
        int records=15;
        int pageno=1;
        String errormsg="";
        Functions functions = new Functions();
        Hashtable hash = null;
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            log.error("Enter valid input", e);
            errormsg += "<center><font class=\"text\" face=\"arial\"><b>"+e.getMessage()+ "</b></font></center>";
            req.setAttribute("errormessage",errormsg);
            RequestDispatcher rd = req.getRequestDispatcher("/agentInterface.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }

        String agentId = req.getParameter("agentid");
        String agentName = req.getParameter("agentname");
        String partnername= req.getParameter("partnerName");
        String login= req.getParameter("login");

        int start = 0; // start index
        int end = 0; // end index

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
        ResultSet rs = null;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select a.agentId,a.agentName,a.login,p.partnerName from agents as a inner join partners as p ON a.partnerId=p.partnerId where agentId>0 ");
            StringBuffer countquery = new StringBuffer("select count(*) from agents a inner join partners p ON a.partnerId=p.partnerId where agentId>0 ");

            if (functions.isValueNull(agentId))
            {
                query.append(" and agentId='" + ESAPI.encoder().encodeForSQL(me,agentId)+"'");
                countquery.append(" and agentId='" + ESAPI.encoder().encodeForSQL(me,agentId)+"'");
            }

            if (functions.isValueNull(agentName))
            {
                query.append(" and agentName LIKE '%" + ESAPI.encoder().encodeForSQL(me,agentName) + "%'");
                countquery.append(" and agentName LIKE'%" + ESAPI.encoder().encodeForSQL(me,agentName) + "%'");
            }

            if (functions.isValueNull(login))
            {
                query.append(" and a.login='" + ESAPI.encoder().encodeForSQL(me,login) + "'");
                countquery.append(" and a.login='" + ESAPI.encoder().encodeForSQL(me,login) + "'");
            }
            if (functions.isValueNull(partnername))
            {
                query.append(" and partnerName='" + ESAPI.encoder().encodeForSQL(me,partnername) + "'");
                countquery.append(" and partnerName='" + ESAPI.encoder().encodeForSQL(me,partnername) + "'");
            }
            query.append(" order by agentId desc LIMIT " + start + "," + end);

            /*System.out.println("select query for agent list"+query);
            System.out.println("count query for agent list"+countquery);
            log.debug("Query:-"+query);
            log.debug("CountQuery:-"+countquery);*/


            hash = Database.getHashFromResultSetForTransactionEntry(Database.executeQuery(query.toString(), conn));

            rs = Database.executeQuery(countquery.toString(), conn);
            rs.next();
            int totalrecords = rs.getInt(1);

            hash.put("totalrecords", "" + totalrecords);
            hash.put("records", "0");

            if (totalrecords > 0)
                hash.put("records", "" + (hash.size() - 2));
            req.setAttribute("transdetails", hash);
            log.debug("forward to jsp"+hash);

        }
        catch (SystemError s)
        {
            log.error("System error while performing select query",s);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        catch (SQLException e)
        {
            log.error("SQL error",e);
            Functions.ShowMessage("Error", "Internal System Error while getting list of Transactions");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        RequestDispatcher rd = req.getRequestDispatcher("/agentInterface.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.AGENT_ID);
        inputFieldsListMandatory.add(InputFields.AGENT_NAME);
        inputFieldsListMandatory.add(InputFields.LOGIN);

        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }

}
