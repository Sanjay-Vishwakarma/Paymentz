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
 Created by IntelliJ IDEA.
 User: Supriya
 Date: 10/2/16
 Time: 4:50 PM
 **/
public class FraudSystemAccountList extends HttpServlet
{
    private static Logger logger = new Logger(FraudSystemAccountList.class.getName());
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
        RequestDispatcher rd = request.getRequestDispatcher("/fraudSystemAccountList.jsp?ctoken="+user.getCSRFToken());
        String errormsg = "";
        String EOL = "<BR>";
        try
        {
            validateOptionalParameter(request);
        }
        catch (ValidationException e)
        {
            logger.error("Enter valid input",e);
            errormsg +=  errormsg + e.getMessage() + EOL ;
            request.setAttribute("msg",errormsg);
            rd.forward(request, response);
            return;
        }

        String fsId = null;
        String fsAccountId = null;
        fsId = request.getParameter("fsid");
        fsAccountId = request.getParameter("fsaccountid");

        StringBuffer errorMsg = new StringBuffer();
        if (!ESAPI.validator().isValidInput("fsid", fsId, "Numbers", 10, true))
        {
            errorMsg.append("Invalid Fraud System<BR>");
        }
        if (!ESAPI.validator().isValidInput("fsaccountid", fsAccountId, "Numbers", 11, true))
        {
            errorMsg.append("Invalid Account<BR>");
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
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select fsam.fsaccountid,fsm.fsname,fsam.accountname,fsam.username,fsam.isTest,fsam.contact_name,fsam.contact_email from fraudsystem_account_mapping as fsam join fraudsystem_master as fsm on fsam.fsid=fsm.fsid ");
            StringBuffer countquery = new StringBuffer("select count(*) from fraudsystem_account_mapping as fsam join fraudsystem_master as fsm on fsam.fsid=fsm.fsid ");
            if(functions.isValueNull(fsId))
            {
                query.append(" and fsam.fsid=" + ESAPI.encoder().encodeForSQL(me,fsId));
                countquery.append(" and fsam.fsid=" + ESAPI.encoder().encodeForSQL(me,fsId));
            }
            if(functions.isValueNull(fsAccountId))
            {
                query.append(" and fsam.fsaccountid=" + ESAPI.encoder().encodeForSQL(me,fsAccountId));
                countquery.append(" and fsam.fsaccountid=" + ESAPI.encoder().encodeForSQL(me,fsAccountId));
            }
            query.append(" order by fsam.fsaccountid desc LIMIT " + start + "," + end);

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
    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
