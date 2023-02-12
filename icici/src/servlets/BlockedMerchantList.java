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

/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Aug 17, 2012
 * Time: 1:11:10 AM
 * To change this template use File | Settings | File Templates.
 */


public class BlockedMerchantList extends HttpServlet
{
    private static Logger logger = new Logger(BlockedMerchantList.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering in BlockedEmailList");
        int start = 0; // start index
        int end = 0; // end index
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
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

        Hashtable merchanthash = null;

        String emailquery = "select m.memberid,m.login,m.contact_emails from members as m ,user as u where u.unblocked='locked' and u.faillogincount>='3' and m.login=u.login and u.roles='merchant' order by m.memberid desc LIMIT " + start + "," + end;
        String emailcountquery = "select count(*) from members as m ,user as u where u.unblocked='locked' and u.faillogincount>='3' and m.login=u.login and u.roles='merchant'";

        Connection cn = null;
        ResultSet rs=null;
        try
        {
            cn = Database.getConnection();
            merchanthash = Database.getHashFromResultSet(Database.executeQuery(emailquery, cn));

            rs = Database.executeQuery(emailcountquery, cn);
            rs.next();
            int totalemailrecords = rs.getInt(1);

            merchanthash.put("totalrecords", "" + totalemailrecords);
            merchanthash.put("records", "0");

            if (totalemailrecords > 0)
                merchanthash.put("records", "" + (merchanthash.size() - 2));
            if(req.getAttribute("msg")!=null)
            {
                req.setAttribute("msg",req.getAttribute("msg"));
            }
            req.setAttribute("blockedmerchant", merchanthash);

            RequestDispatcher rd = req.getRequestDispatcher("/blockedmerchantlist.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError se)
        {   logger.error("SystemError in BlockedEmailList::::",se);

        }
        catch (Exception e)
        {
            logger.error("Exception in BlockedEmailList",e);
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
