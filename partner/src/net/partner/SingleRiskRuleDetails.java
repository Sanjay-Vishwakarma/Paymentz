package net.partner;


import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ProfileManagementManager;
import com.manager.vo.ActionVO;
import com.manager.vo.riskRuleVOs.RiskProfile;
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

/**
 * Created by Pradeep on 31/08/2015.
 */
public class SingleRiskRuleDetails extends HttpServlet
{
    private static Logger logger =new Logger(SingleRiskRuleDetails.class.getName());

    private Functions functions = new Functions();

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        HttpSession session = functions.getNewSession(request);

        PartnerFunctions partnerFunctions=new PartnerFunctions();
        if (!partnerFunctions.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        ProfileManagementManager profileManagementManager=new ProfileManagementManager();
        //BusinessRuleManager businessRuleManager = new BusinessRuleManager();
        //VO
        ActionVO actionVO = new ActionVO();
        List<RuleVO> riskRuleVOList=null;
        RiskProfile riskProfile = null;
        //Validation Error List
        ValidationErrorList validationErrorList=null;

        //BOOLEAN
        boolean isDeleted=false;

        //Request Dispatcher
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/addRiskProfile.jsp?MES=SUCCESS&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdError= request.getRequestDispatcher("/net/RiskProfileList?MES=ERR&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdDelete= request.getRequestDispatcher("/riskProfile.jsp?MES=ERR&ctoken=" + user.getCSRFToken());

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
                riskProfile= profileManagementManager.getRiskProfileWithAllDetails((String)session.getAttribute("merchantid"),actionVO.getActionCriteria().split("_")[0],null);
                request.setAttribute("riskProfile",riskProfile);
                session.setAttribute("riskProfile",riskProfile);

                riskRuleVOList = profileManagementManager.getListOfRiskRuleDetails(null, null);
                request.setAttribute("riskRuleList", riskRuleVOList);
                session.setAttribute("riskRuleList", riskRuleVOList);
            }
           /* else if(actionVO.isEdit())
            {
                riskProfile = profileManagementManager.getRiskProfileWithAllDetails((String) session.getAttribute("merchantid"), actionVO.getActionCriteria().split("_")[0], null);
                request.setAttribute("riskProfile", riskProfile);

                riskRuleVOList = profileManagementManager.getListOfRiskRuleDetails(null, null);
                request.setAttribute("riskRuleList", riskRuleVOList);
            }*/
            else if(actionVO.isDelete())
            {
                isDeleted = profileManagementManager.deleteRiskProfile(actionVO.getActionCriteria().split("_")[0]);
                request.setAttribute("DELETED", isDeleted);
                rdDelete.forward(request,response);
                return;
            }
            else
            {
                riskRuleVOList = profileManagementManager.getListOfRiskRuleDetails(null, null);
                session.setAttribute("riskRuleList", riskRuleVOList);
                request.setAttribute("riskRuleList", riskRuleVOList);
            }

                session.setAttribute("actionVO", actionVO);
                request.setAttribute("actionVO", actionVO);
                rdSuccess.forward(request, response);

        }
        catch (PZDBViolationException e)
        {

            if(e.getPzdbConstraint().getPzDBEnum().name().equals(PZDBExceptionEnum.FOREIGN_KEY_CONSTRAINT.name()))
            {
                logger.error("Foreign key violation exception while deleting Risk profile details", e);
                request.setAttribute("catchError", "Profile is in use in User Profile");
                rdError.forward(request, response);
                return;
            }
            else
            {
                logger.error("Db violation exception while getting Riskprofile details", e);
                PZExceptionHandler.handleDBCVEException(e, null, null);
                request.setAttribute("catchError", "Kindly check for the Risk Profile after sometime");
                rdError.forward(request, response);
                return;
            }
        }

    }

   /* public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        doGet(request, response);
    }*/

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
