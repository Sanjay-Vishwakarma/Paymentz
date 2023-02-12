import com.directi.pg.*;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.paymentgateway.EcorePaymentGateway;
import com.directi.pg.core.valueObjects.*;

import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.*;


/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 11/9/12
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class EcoreInquiry extends HttpServlet
{
    static Logger log = new Logger(EcoreInquiry.class.getName());
    static TransactionLogger transactionLogger = new TransactionLogger(EcoreInquiry.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        log.debug("Entering in EcoreInquiry");
        transactionLogger.debug("Entering in EcoreInquiry");
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        log.debug("ctoken==="+req.getParameter("ctoken"));
        transactionLogger.debug("ctoken==="+req.getParameter("ctoken"));


        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout ");
            transactionLogger.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        //String merchantNo=null;
        //String orderDescription=null;
        //String md5string=null;
        //String md5Info=null;
        EcorePaymentGateway pg = null;
        String accountid=null;
        Connection con=null;

        String mid =null;
        String midkey =null;
        String trackingId =req.getParameter("trackingid");
        String transactionId =req.getParameter("transId");
        EcoreResponseVO transRespDetails=null;
        try
        {
            validateOptionalParameter(req);
        }
        catch (ValidationException e)
        {
            log.error("not valid data",e);
            transactionLogger.error("not valid data",e);
        }

        accountid = (String) req.getParameter("accountid");

        String query="select mid, midkey from gateway_accounts_ecore where accountid=?";

        try
        {
            con= Database.getConnection();
            PreparedStatement p= con.prepareStatement(query);
            p.setString(1,accountid);
            ResultSet rs =p.executeQuery();
            if(rs.next())
            {
                mid=rs.getString("mid");
                midkey=rs.getString("midkey");
            }
        }
        catch (SystemError systemError)
        {
            log.error("NoSuchAlgorithmException",systemError);
            transactionLogger.error("NoSuchAlgorithmException",systemError);
        }
        catch (SQLException xe)
        {
            log.error("NoSuchAlgorithmException",xe);
            transactionLogger.error("NoSuchAlgorithmException",xe);
        }
        finally
        {
            Database.closeConnection(con);
        }

        try
        {
            pg = (EcorePaymentGateway)AbstractPaymentGateway.getGateway(accountid);
        }
        catch (SystemError systemError)
        {
           log.error("System errror while getting gateway details",systemError);
           transactionLogger.error("System errror while getting gateway details",systemError);
        }

        try
        {
            log.debug("calling inquiry method");
            transactionLogger.debug("calling inquiry method");
            transRespDetails = (EcoreResponseVO) pg.processInquiry(mid,midkey,transactionId,trackingId);
        }
        catch (PZTechnicalViolationException e)
        {
            log.error("PZTechnical Violation Exception::::",e);
            transactionLogger.error("PZTechnical Violation Exception::::",e);
            PZExceptionHandler.handleTechicalCVEException(e,"trackingId::::"+trackingId,"Technical exception while Enquiring via Ecore");
        }
        log.debug("successful");
        transactionLogger.debug("successful");
        Hashtable hash=new Hashtable();
        if(transRespDetails!=null)
        {
            if(transRespDetails.getResponseCode()!=null)
                hash.put("responsecode",transRespDetails.getResponseCode());
            if(transRespDetails.getDescription()!=null)
                hash.put("description",transRespDetails.getDescription());
            if(transRespDetails.getTransactionID()!=null)
                hash.put("transactionId",transRespDetails.getTransactionID());
            if(transRespDetails.getStatusCode()!=null)
                hash.put("statusCode",transRespDetails.getStatusCode());
            if(transRespDetails.getStatusDescription()!=null)
                hash.put("status",transRespDetails.getStatusDescription());

            req.setAttribute("inquirydetails", hash);
        }
        RequestDispatcher rd = req.getRequestDispatcher("/ecoreinquiry.jsp?ctoken="+user.getCSRFToken());
        rd.forward(req, res);
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.ACCOUNTID_SMALL);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }
}
