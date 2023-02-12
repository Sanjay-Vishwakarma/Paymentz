package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.PartnerManager;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ajit on 3/24/2018.
 */
public class MerchantUserUnblockedAccount extends HttpServlet
{
    private Logger logger = new Logger(MerchantUserUnblockedAccount.class.getName());

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        logger.debug("Entering in MerchantUserUnblockedAccount");
        HttpSession session = request.getSession();
        String partnerid=(String)session.getAttribute("merchantid");
        PartnerFunctions partner=new PartnerFunctions();
        PartnerManager partnerManager = new PartnerManager();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        int records=15;
        int pageno=1;
        response.setContentType("text/html");

        try
        {
            String error = "";
            error = error + validateOptionalParameter(request);
            if(error.length() > 0)
            {
                request.setAttribute("error",error);
                RequestDispatcher rd = request.getRequestDispatcher("/merchantUserUnblockedAccount.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
            }
        }
        catch (Exception e)
        {
            logger.error("Invalid page no or records",e);
            pageno = 1;
            records = 15;
        }
        pageno = Functions.convertStringtoInt(request.getParameter("SPageno"), 1);
        records = Functions.convertStringtoInt(request.getParameter("SRecords"), 15);
        HashMap merchanthash = null;
        try
        {
            merchanthash = partnerManager.getMerchantUserUnblockedAccountList(partnerid, pageno, records);
            request.setAttribute("blockedsubmerchant", merchanthash);
            RequestDispatcher rd = request.getRequestDispatcher("/merchantUserUnblockedAccount.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }
        catch (SystemError se)
        {
            logger.error("SystemError in BlockedEmailList::::",se);
        }
        catch (Exception e)
        {
            logger.error("Exception in BlockedEmailList",e);
        }
    }
    private String validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        String error = "";
        String EOL = "<BR>";
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);

        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputFieldsListMandatory,errorList ,true);

        if(!errorList.isEmpty())
        {
            for(InputFields inputFields :inputFieldsListMandatory)
            {
                if(errorList.getError(inputFields.toString())!=null)
                {
                    logger.debug(errorList.getError(inputFields.toString()).getLogMessage());
                    error = error.concat(errorList.getError(inputFields.toString()).getMessage()+EOL);
                }
            }
        }
        return error;
    }
}
