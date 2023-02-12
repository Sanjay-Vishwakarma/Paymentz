
import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemAccessLogger;
import com.manager.ProfileManagementManager;
import com.manager.vo.ActionVO;
import com.manager.vo.riskRuleVOs.RuleVO;

import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
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
 * Created by Admin on 17/8/15.
 */
public class SingleRiskRuleDetails extends HttpServlet
{
    private static Logger logger =new Logger(SingleRiskRuleDetails.class.getName());

    private Functions functions = new Functions();

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        HttpSession session = functions.getNewSession(request);

        Admin admin=new Admin();
        if (!admin.isLoggedIn(session))
        {
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        User user =  (User)session.getAttribute("ESAPIUserSessionKey");


        ProfileManagementManager profileManagementManager = new ProfileManagementManager();
        //VO
        ActionVO actionVO = new ActionVO();
        RuleVO ruleVO = null;
        //Validation Error List
        ValidationErrorList validationErrorList=null;
        //Boolean
        boolean isDeleted=false;
        //Request Dispatcher
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/addOrUpdateRiskRule.jsp?MES=SUCCESS&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdError= request.getRequestDispatcher("/servlet/RiskRuleDetails?MES=ERR&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdDelete= request.getRequestDispatcher("/riskRuleManagement.jsp?MES=ERR&ctoken=" + user.getCSRFToken());

        try
        {
            validationErrorList=validateMandatorParameter(request);
            if(!validationErrorList.isEmpty())
            {
                logger.error("validation error");
                request.setAttribute("error",validationErrorList);
                rdError.forward(request,response);
                return;
            }

            actionVO.setAllContentAuto(request.getParameter("action"));

            if(actionVO.isEdit() || actionVO.isView())
            {
                ruleVO = profileManagementManager.getSingleRiskRuleWithRuleOperation(actionVO.getActionCriteria().split("_")[0]);
            }
            else if(actionVO.isDelete())
            {
                isDeleted=profileManagementManager.deleteRuleDefinition(actionVO.getActionCriteria().split("_")[0]);
                request.setAttribute("DELETED",isDeleted);
                rdDelete.forward(request,response);
                return;
            }

            request.setAttribute("ruleVO",ruleVO);
            request.setAttribute("actionVO",actionVO);
            session.setAttribute("actionVO",actionVO);
            rdSuccess.forward(request,response);
        }
        catch (PZDBViolationException e)
        {
            if(e.getPzdbConstraint().getPzDBEnum().name().equals(PZDBExceptionEnum.FOREIGN_KEY_CONSTRAINT.name()))
            {
                logger.error("Foreign key violation exception while deleting Risk profile details", e);
                request.setAttribute("catchError", "Profile is in use in Risk Profile");
                rdError.forward(request, response);
                return;
            }
            else
            {
                logger.error("Db violation exception while getting Risk details", e);
                PZExceptionHandler.handleDBCVEException(e, null, null);
                request.setAttribute("catchError", "Kindly check for the Risk Rule Details after sometime");
                rdError.forward(request, response);
                return;
            }
        }

    }

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        doPost(request, response);
    }

    private ValidationErrorList validateMandatorParameter(HttpServletRequest request)
    {
        List<InputFields> inputMandatoryParameter= new ArrayList<InputFields>();
        inputMandatoryParameter.add(InputFields.SMALL_ACTION);


        InputValidator inputValidator = new InputValidator();
        ValidationErrorList validationErrorList = new ValidationErrorList();

        inputValidator.InputValidations(request,inputMandatoryParameter,validationErrorList,false);

        return validationErrorList;
    }
}