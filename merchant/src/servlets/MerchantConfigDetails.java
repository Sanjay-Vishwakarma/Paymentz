import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.manager.MerchantConfigManager;
import com.manager.TerminalManager;
import com.manager.utils.CommonFunctionUtil;
import com.manager.vo.TerminalVO;
import com.manager.vo.memeberConfigVOS.MerchantConfigCombinationVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import com.directi.pg.SystemAccessLogger;
//import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Niket
 * Date: 12/11/14
 * Time: 3:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantConfigDetails extends HttpServlet
{
    private static Logger logger = new Logger(MerchantConfigDetails.class.getName());
    //instance for validation error list messages
    private CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

        Merchants merchants = new Merchants();
        // manager instance
        TerminalManager terminalManager = new TerminalManager();
        MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
        //Vo instance
        MerchantConfigCombinationVO merchantConfigCombinationVO=null;
        TerminalVO terminalVO = null;
        //this to check whether the merchant is session or not if not sent to sessionout.jsp
        HttpSession session = Functions.getNewSession(request);
        if (!merchants.isLoggedIn(session))
        {
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        RequestDispatcher rdError=request.getRequestDispatcher("/merchantConfigDetails.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/merchantConfigDetails.jsp?Success=YES&ctoken="+user.getCSRFToken());

        //String terminalid = request.getParameter("terminalid");
       // String accountid = null;
        Functions functions = new Functions();
        String error = "";
        //validation done
        ValidationErrorList mandatoryErrorList=validateMandatoryParameters(request);
        try
        {
            if(!mandatoryErrorList.isEmpty() || !functions.isValueNull("terminalid"))
            {
                logger.debug("invalid data Provided for Member Config Details");
                StringBuffer errorMessage= new StringBuffer();
                commonFunctionUtil.getErrorMessage(errorMessage, mandatoryErrorList);
                error = errorMessage.toString();
                PZExceptionHandler.raiseConstraintViolationException(MerchantConfigDetails.class.getName(), "doPost()", null, "Merchant", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null, null, null);
            }
            logger.debug("Valid data provided for Merchant Config Details");
            if (functions.isValueNull(request.getParameter("terminalid")))
            {
                terminalVO = terminalManager.getTerminalByTerminalId(request.getParameter("terminalid"));
            }

            if (terminalVO == null)
            {
                logger.debug("inside terminalvo null----");
                PZExceptionHandler.raiseConstraintViolationException(MerchantConfigDetails.class.getName(), "doPost()", null, "Merchant", "Please select correct terminalid", PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null, null, null);
            }


            merchantConfigCombinationVO=merchantConfigManager.setMemberDetailsAndMemberAccountDetails(terminalVO);

            request.setAttribute("terminalid",request.getParameter("terminalid"));
            request.setAttribute("merchantConfigCombinationVO",merchantConfigCombinationVO);
            rdSuccess.forward(request,response);


        }
        catch (PZConstraintViolationException cve)
        {
            logger.error("Constraint violation Exception Member Config Details::",cve);
            PZExceptionHandler.handleCVEException(cve,session.getAttribute("merchantid").toString(), PZOperations.MEMBER_CONFIG);
            request.setAttribute("error",error);
            rdError.forward(request,response);
            return;
        }
        catch (PZDBViolationException dbe)
        {
            logger.error("Db exception while getting Data for Member Config Details",dbe);
            PZExceptionHandler.handleDBCVEException(dbe,session.getAttribute("merchantid").toString(),PZOperations.MEMBER_CONFIG);
            request.setAttribute("catchError","Kindly check for the Member Config Details after sometime");
            rdError.forward(request,response);
            return;
        }

    }

    public ValidationErrorList validateMandatoryParameters(HttpServletRequest req)
    {
        List<InputFields> inputMandatoryParameter= new ArrayList<InputFields>();
        inputMandatoryParameter.add(InputFields.TERMINALID);
        InputValidator inputValidator = new InputValidator();
        ValidationErrorList errorList = new ValidationErrorList();
        inputValidator.InputValidations(req,inputMandatoryParameter, errorList,false);

        return errorList;
    }
}
