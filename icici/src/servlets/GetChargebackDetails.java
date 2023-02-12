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
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class GetChargebackDetails extends HttpServlet
{
    public static Logger logger = new Logger(GetChargebackDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        String icicitransid=null;
        String action=null;
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("member is logout ");
            res.sendRedirect("/icici/admin/logout.jsp");
            return;
        }
        try
        {
            validateOptionalParameter(req);
        }
        catch(ValidationException e)
        {
            logger.error("Error in icicitransid",e);
            out.println(Functions.ShowMessage("Sorry", "No Record Found"));
            return;
        }

        icicitransid = req.getParameter("icicitransid");
        action = Functions.checkStringNull(req.getParameter("action"));

        StringBuffer query = new StringBuffer("select T.status,T.icicitransid,T.transid,T.description,T.captureamount-T.refundamount as amount,T.toid,M.company_name,T.accountid as accountid from transaction_icicicredit as T,members as M where icicitransid=? and M.memberid=T.toid");

        Connection conn = null;
        PreparedStatement preparedStatement=null;
        try
        {
            conn = Database.getConnection();
            preparedStatement=conn.prepareStatement(query.toString());
            preparedStatement.setString(1, icicitransid);
            Hashtable hash = Database.getHashFromResultSet(preparedStatement.executeQuery());
            String rr_status = Transaction.isRetrivalRequestAlreadySent(icicitransid);
            req.setAttribute("chargebackdetails", hash);
            req.setAttribute("action", action);
            req.setAttribute("rr_status", rr_status);
            RequestDispatcher rd = req.getRequestDispatcher("/chargebackreason.jsp?ctoken="+user.getCSRFToken());
            rd.forward(req, res);
        }
        catch (SystemError se)
        {   logger.error("System Error in GetChargebackDetails::::::",se);
            out.println(Functions.ShowMessage("Error", "Internal System Error"));

        }
        catch (Exception e)
        {   logger.error("Exception in GetChargebackDetails:::::::",e);
            out.println(Functions.ShowMessage("Error!", "Internal System Error"));
        }
        finally
        {
            Database.closePreparedStatement(preparedStatement);
            Database.closeConnection(conn);
        }
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.ICICITRANSEID);
        inputFieldsListMandatory.add(InputFields.ACTION);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
