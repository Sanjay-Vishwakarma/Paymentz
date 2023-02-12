package net.partner;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.manager.PartnerManager;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kanchan on 27-06-2022.
 */
public class UnblockAgent extends HttpServlet
{
    Logger logger= new Logger(UnblockAgent.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException
    {
        logger.error("inside UnblockAgent servlet");
        HttpSession session= request.getSession();
        User user= (User)session.getAttribute("ESAPIUserSessionKey");
        PartnerFunctions partner=new PartnerFunctions();
        PartnerManager partnerManager = new PartnerManager();


        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
        String login= request.getParameter("login");
        try
        {
            InputValidator inputValidator= new InputValidator();
            List<InputFields> inputFieldsList= new ArrayList<InputFields>();
            inputFieldsList.add(InputFields.LOGIN);
            inputValidator.InputValidations(request,inputFieldsList,false);
        }
        catch (ValidationException e)
        {
            logger.error("ValidationException ::: ",e);
        }

        try
        {
            boolean flag= partnerManager.getUnBlockedAccount(login);
            String msg="";
            if (flag)
            {
                logger.error("process done successfully.");
                msg= login+ " Unblocked account successfully";
            }
            request.setAttribute("unblockmsg", msg);
            RequestDispatcher rd= request.getRequestDispatcher("/agentInterface.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request,response);
        }
        catch (SystemError systemError)
        {
            logger.error("SystemError::: ",systemError);
        }
        catch (Exception e)
        {
            logger.error("Exception::: ",e);
        }
    }
}
