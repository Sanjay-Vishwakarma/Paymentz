import com.directi.pg.Admin;
import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.manager.AuthenticationManager;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by NIKET on 11-06-2016.
 */
public class UnlockUser extends HttpServlet
{
    private Logger logger = new Logger(UnlockUser.class.getName());

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException
    {
        logger.debug("Entering in BlockedEmailList");
        HttpSession session = request.getSession();
        AuthenticationManager authenticationManager = new AuthenticationManager();

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }


        logger.debug("success");

        response.setContentType("text/html");
        logger.debug("Get requested parameter");
        String login=null;
        try
        {
            InputValidator inputValidator = new InputValidator();
            List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
            inputFieldsListMandatory.add(InputFields.LOGIN);

            inputValidator.InputValidations(request,inputFieldsListMandatory,false);
        }
        catch(ValidationException e)
        {
            logger.error("Invalid User login",e);
        }
        login = request.getParameter("login");
        Connection con = null;
        PreparedStatement pstmt = null;
        try
        {
            String msg= "";
            boolean flag = authenticationManager.getUnBlockedAccount(login);
            if (flag)
            {
                logger.debug("process is done successfully");
                msg= login+" Account has been Unblocked successfully";
            }
            request.setAttribute("msg",msg);
            RequestDispatcher rd = request.getRequestDispatcher("/unblockuser.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request, response);
        }
        catch (Exception e)
        {
            logger.error("SystemError in UnblockMerchant::::::",e);
        }
        finally
        {
            Database.closePreparedStatement(pstmt);
            Database.closeConnection(con);
        }


        /*//Manager Instance
        AuthenticationManager authenticationManager = new AuthenticationManager();

        //Vo Instance
        ActionVO actionVO = new ActionVO();

        ValidationErrorList validationErrorList=null;

        boolean isUpdate=false;

        RequestDispatcher rdError=request.getRequestDispatcher("BlockedUserList?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess= request.getRequestDispatcher("BlockedUserList?Success=YES&ctoken="+user.getCSRFToken());

        try
        {
            validationErrorList=validateParameters(request);

            if(!validationErrorList.isEmpty())
            {
                request.setAttribute("error",validationErrorList);
                rdError.forward(request,response);
                return;
            }

            actionVO.setAllContentAuto(request.getParameter("action"));

            if(actionVO.isUpdate())
                isUpdate=authenticationManager.unlockUser(actionVO.getActionCriteria().split("_")[0]);

            request.setAttribute("Update",isUpdate);
            rdSuccess.forward(request,response);
            return;
        }
        catch (PZDBViolationException e)
        {
            PZExceptionHandler.handleDBCVEException(e,"","Failure While Unblocking user from the user table");
        }*/

    }

    private ValidationErrorList validateParameters(HttpServletRequest request)
    {
        List<InputFields> inputFieldsList = new ArrayList<InputFields>();
        inputFieldsList.add(InputFields.SMALL_ACTION);

        InputValidator inputValidator = new InputValidator();
        ValidationErrorList validationErrorList = new ValidationErrorList();

        inputValidator.InputValidations(request, inputFieldsList, validationErrorList, false);

        return validationErrorList;
    }
}
