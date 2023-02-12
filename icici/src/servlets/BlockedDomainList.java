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


public class BlockedDomainList extends HttpServlet
{
    private static Logger logger = new Logger(BlockedDomainList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {   logger.debug("Entering in BlockedDomainList");
        int start = 0; // start index
        int end = 0; // end index
        HttpSession session = req.getSession();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        int pageno=1;
        int records=15;
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
        Hashtable domainhash = null;
        String domainquery = "select emailaddr from blockedemail where type='domain' order by emailaddr asc LIMIT " + start + "," + end;
        String domaincountquery = "select count(*) from blockedemail where type='domain'";
        Connection cn = null;
        ResultSet rs=null;
        try
        {
            cn = Database.getConnection();
            domainhash = Database.getHashFromResultSet(Database.executeQuery(domainquery, cn));

            rs = Database.executeQuery(domaincountquery, cn);
            rs.next();
            int totaldomainrecords = rs.getInt(1);

            domainhash.put("totalrecords", "" + totaldomainrecords);
            domainhash.put("records", "0");

            if (totaldomainrecords > 0)
                domainhash.put("records", "" + (domainhash.size() - 2));

            req.setAttribute("blockeddomain", domainhash);

            RequestDispatcher rd = req.getRequestDispatcher("/blockeddomainlist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError se)
        {   logger.error("SystemError in BlockedDomainList:::::",se);
            out.println(Functions.ShowMessage("Error", "Internal Error while Block domain "));
        }
        catch (Exception e)
        {   logger.error("Exception in BlockedDomainList:::::",e);
            out.println(Functions.ShowMessage("Error!", "Internal Error while Block domain "));
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
