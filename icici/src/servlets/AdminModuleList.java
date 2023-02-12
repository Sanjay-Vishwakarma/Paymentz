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
 * User: Naushad
 * Date: 11/2/16
 * Time: 2:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class AdminModuleList extends HttpServlet
{
    static Logger logger = new Logger(AdminModuleList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        Functions functions = new Functions();
        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }

        RequestDispatcher rd = req.getRequestDispatcher("/adminModuleList.jsp?ctoken="+user.getCSRFToken());
        String errormsg = "";
        String EOL = "<BR>";
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            logger.error("Enter valid input",e);
            errormsg +=  errormsg + e.getMessage() + EOL ;
            req.setAttribute("errormessage",errormsg);
            rd.forward(req, res);
            return;
        }
        int records=15;
        int pageno=1;

        String moduleName = req.getParameter("modulename");

        if (!ESAPI.validator().isValidInput("modulename", moduleName, "SafeString", 100, true))
        {
            req.setAttribute("errormessage","Invalid module name");
            rd.forward(req,res);
            return;
        }

        int start = 0; // start index
        int end = 0; // end index

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;
        Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);

        Connection conn = null;
        ResultSet rs=null;
        Hashtable hash = null;
        try
        {
            conn = Database.getRDBConnection();
            StringBuffer query = new StringBuffer("select moduleid,modulename,modulecreationtime,FROM_UNIXTIME(modulecreationtime) AS 'modulecreationtime' FROM admin_modules_master where moduleid>0");
            StringBuffer countquery = new StringBuffer("select count(*) from admin_modules_master where moduleid>0");
            if (functions.isValueNull(moduleName))
            {
                query.append(" and modulename LIKE '%" + ESAPI.encoder().encodeForSQL(me,moduleName) + "%'");
                countquery.append(" and modulename LIKE'%" + ESAPI.encoder().encodeForSQL(me,moduleName) + "%'");
            }

            query.append(" order by moduleid LIMIT " + start + "," + end);

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
            logger.error("SystemError:::::::", s);
            Functions.ShowMessage("Error", "Internal error while processing your request");
        }
        catch (SQLException e)
        {
            logger.error("SQLException:::::::", e);
            Functions.ShowMessage("Error", "Internal error while processing your request");
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(conn);
        }
        rd.forward(req,res);
        return;
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
