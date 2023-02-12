package net.agent;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
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

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Nishant
 * Date: 5/8/14
 * Time: 1:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class AgentMerchantDetailConfig extends HttpServlet
{
    private static Logger logger = new Logger(AgentMerchantDetailConfig.class.getName());

    //instance for validation error list messages
    private static CommonFunctionUtil commonFunctionUtil = new CommonFunctionUtil();

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

        AgentFunctions agentFunctions = new AgentFunctions();
        // manager instance
        TerminalManager terminalManager = new TerminalManager();
        MerchantConfigManager merchantConfigManager = new MerchantConfigManager();
        //Vo instance
        MerchantConfigCombinationVO merchantConfigCombinationVO=null;
        TerminalVO terminalVO = null;
        //this to check whether the merchant is session or not if not sent to sessionout.jsp
        HttpSession session = Functions.getNewSession(request);
        if (!agentFunctions.isLoggedInAgent(session))
        {
            response.sendRedirect("/agent/Logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        RequestDispatcher rdError=request.getRequestDispatcher("/viewmerchantdetails.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/viewmerchantdetails.jsp?Success=YES&ctoken="+user.getCSRFToken());
        //validation done
        ValidationErrorList mandatoryErrorList=validateMandatoryParameters(request);
        try
        {
            if(!mandatoryErrorList.isEmpty())
            {
                logger.debug("Invalid Data Provided For Member Config Details");
                StringBuffer    errorMessage= new StringBuffer();
                commonFunctionUtil.getErrorMessage(errorMessage,mandatoryErrorList);
                PZExceptionHandler.raiseConstraintViolationException(AgentMerchantDetailConfig.class.getName(), "doPost()", null, "Agent", errorMessage.toString(), PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED,null, null, null);
            }
            logger.debug("Valid Data Provided For Merchant Config Details");
            terminalVO=terminalManager.getActiveInActiveTerminalInfo(request.getParameter("terminalid"));

            merchantConfigCombinationVO=merchantConfigManager.setMemberDetailsAndMemberAccountDetails(terminalVO);
            request.setAttribute("merchantConfigCombinationVO",merchantConfigCombinationVO);
            request.setAttribute("memberid",request.getParameter("toid"));
            request.setAttribute("terminalvo",terminalVO);
            rdSuccess.forward(request,response);
        }
        catch (PZConstraintViolationException cve)
        {
            logger.error("PZConstraintViolationException while getting Data for Member Config Details::",cve);
            PZExceptionHandler.handleCVEException(cve,session.getAttribute("merchantid").toString(), PZOperations.MEMBER_CONFIG);
            request.setAttribute("error","Internal error while accessing data.");
            rdError.forward(request,response);
            return;
        }
        catch (PZDBViolationException dbe)
        {
            logger.error("PZDBViolationException while getting Data for Member Config Details",dbe);
            PZExceptionHandler.handleDBCVEException(dbe,session.getAttribute("merchantid").toString(),PZOperations.MEMBER_CONFIG);
            request.setAttribute("catchError","Internal error while accessing data.");
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
