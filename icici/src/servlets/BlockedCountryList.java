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

public class BlockedCountryList extends HttpServlet
{
    private static Logger logger = new Logger(BlockedCountryList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {   logger.debug("Entering in BlockedCountryList");
        int start = 0; // start index
        int end = 0; // end index
        int records=15;
        int pageno=1;
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
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

        Hashtable countryHash = null;
        String countryQuery = "select country,countryname from blockedcountry order by country asc LIMIT " + start + "," + end;
        String countryCountQuery = "select count(*) from blockedcountry";
        Connection cn = null;
        ResultSet rs=null;
        try
        {
            cn = Database.getConnection();
            countryHash = Database.getHashFromResultSet(Database.executeQuery(countryQuery, cn));

            rs = Database.executeQuery(countryCountQuery, cn);
            rs.next();
            int totalCountryRecs = rs.getInt(1);

            countryHash.put("totalrecords", "" + totalCountryRecs);
            countryHash.put("records", "0");

            if (totalCountryRecs > 0)
                countryHash.put("records", "" + (countryHash.size() - 2));

            req.setAttribute("blockedcountry", countryHash);

            RequestDispatcher rd = req.getRequestDispatcher("/blockedcountrylist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError se)
        {   logger.error("SystemError in::::::BlockedCountryList",se);
            out.println(Functions.ShowMessage("Error", "Internal System Error While Block Country"));
        }
        catch (Exception e)
        {   logger.error("Exception:::::",e);
            out.println(Functions.ShowMessage("Error!", "Internal System Error While Block Country"));
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
