import com.directi.pg.*;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class BlockedEmailList extends HttpServlet
{
    private static Logger logger = new Logger(BlockedEmailList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {   logger.debug("Entering in BlockedEmailList");
        int start = 0; // start index
        int end = 0; // end index
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        int records=15;
        int pageno=1;
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }

        pageno = Functions.convertStringtoInt(req.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(req.getParameter("SRecords"), 15);
        start = (pageno - 1) * records;
        end = records;

        Hashtable emailhash = null;
        String emailquery = "select emailaddr from blockedemail where type='email' order by emailaddr asc LIMIT " + start + "," + end;
        String emailcountquery = "select count(*) from blockedemail where type='email'";
        Connection cn = null;
        ResultSet rs=null;
        try
        {
            cn = Database.getConnection();
            emailhash = Database.getHashFromResultSet(Database.executeQuery(emailquery, cn));

            rs = Database.executeQuery(emailcountquery, cn);
            rs.next();
            int totalemailrecords = rs.getInt(1);

            emailhash.put("totalrecords", "" + totalemailrecords);
            emailhash.put("records", "0");

            if (totalemailrecords > 0)
                emailhash.put("records", "" + (emailhash.size() - 2));

            req.setAttribute("blockedemail", emailhash);

            RequestDispatcher rd = req.getRequestDispatcher("/blockedemaillist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError se)
        {   logger.error("SystemError in BlockedEmailList::::",se);
            out.println(Functions.ShowMessageForAdmin("Error", "Internal Error while listing Blocked emails"));
        }
        catch (Exception e)
        {   logger.error("Exception in while getting blocked email Address",e);
            out.println(Functions.ShowMessageForAdmin("Error!", "Internal Error while listing Blocked emails"));
        }
        finally
        {
            Database.closeResultSet(rs);
            Database.closeConnection(cn);
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
