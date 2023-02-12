import com.directi.pg.Admin;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.Transaction;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
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
import java.util.ArrayList;
import java.util.List;


public class DoChargebackTransaction extends HttpServlet
{

    static Logger logger = new Logger(DoChargebackTransaction.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public synchronized void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        logger.debug("Entering in DoChargebackTransaction");
         HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        Transaction transaction = new Transaction();

        if (!Admin.isLoggedIn(session))
        {   logger.debug("invalid user");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        StringBuilder sSuccessMessage = new StringBuilder();
        StringBuilder sErrorMessage = new StringBuilder();
        PrintWriter out = res.getWriter();
        res.setContentType("text/html");
        String icicitransid=null;
        String date=null;
        String cbrefnumber=null;
        String cbamount=null;
        String cbreason=null;
        String partial=null;
        try
        {
            validateMandatoryParameter(req);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid Input",e);
            sErrorMessage.append("Invalid Input");
        }

        icicitransid = req.getParameter("icicitransid");
        date = req.getParameter("date");
        cbrefnumber = req.getParameter("cbrefnumber");
        cbamount = req.getParameter("cbamount");
        cbreason = req.getParameter("cbreason");
        partial = req.getParameter("partial");

        try
        {
            transaction.processChargeback(icicitransid, date, cbrefnumber, cbamount, cbreason, partial);

            sSuccessMessage.append("Transaction has been chargedback.");
        }
        catch (SystemError se)
        {   logger.error("Error while reversal in DoChargebackTransaction :",se);

            sErrorMessage.append("Internal Error While perfoming ChargeBack");
        }
        catch (Exception e)
        {
            logger.error("Error while reversal in DoChargebackTransaction :",e);
            sErrorMessage.append("Internal Error While perfoming ChargeBack");
        }
        SendTransactionEventMailUtil sendTransactionEventMail=new SendTransactionEventMailUtil();
        sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.CHARGEBACK_TRANSACTION,icicitransid,"",cbreason,null);

        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());

        String redirectpage = "/chargebacklist.jsp?ctoken="+user.getCSRFToken();
        req.setAttribute("cbmessage", chargeBackMessage.toString());
        RequestDispatcher rd = req.getRequestDispatcher(redirectpage);
        rd.forward(req, res);

    }

    private void validateMandatoryParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();

        inputFieldsListMandatory.add(InputFields.ICICITRANSEID);
        inputFieldsListMandatory.add(InputFields.DATE_SMALL);
        inputFieldsListMandatory.add(InputFields.CB_REF_NO);
        inputFieldsListMandatory.add(InputFields.CB_AMOUNT);
        inputFieldsListMandatory.add(InputFields.CB_REASON);

        inputValidator.InputValidations(req,inputFieldsListMandatory,false);
    }
}