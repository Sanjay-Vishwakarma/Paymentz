package net.partner;


import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ProfileManagementManager;
import com.manager.vo.ActionVO;
import com.manager.vo.PayIfeTableInfo;
import com.manager.vo.businessRuleVOs.BusinessProfile;
import com.manager.vo.businessRuleVOs.RuleVO;
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
import java.util.*;

/**
 * Created by Pradeep on 31/08/2015.
 */
public class SingleBusinessRuleDetails extends HttpServlet
{
    private static Logger logger =new Logger(SingleBusinessRuleDetails.class.getName());

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
        Map<String,RuleVO> businessRuleVOList=null;
        Map<String,PayIfeTableInfo> payIfeTableInfo=null;
        Set<String> tableAliasName=new HashSet<String>();
        BusinessProfile businessProfile = null;
        //Validation Error List
        ValidationErrorList validationErrorList=null;

        //Boolean instance
        boolean isDelete=false;
        //Request Dispatcher
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/addBusinessProfile.jsp?MES=SUCCESS&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdError= request.getRequestDispatcher("/net/BusinessProfileList?MES=ERR&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdDelete= request.getRequestDispatcher("/businessProfile.jsp?MES=ERR&ctoken=" + user.getCSRFToken());

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

            if(actionVO.isView() || actionVO.isEdit())
            {
                businessProfile= profileManagementManager.getBusinessProfileWithAllDetails((String) session.getAttribute("merchantid"), actionVO.getActionCriteria().split("_")[0], null);
                request.setAttribute("businessProfile",businessProfile);

                businessRuleVOList = profileManagementManager.getMapOfBusinessRuleDetails(null, null);
                request.setAttribute("businessRuleList", businessRuleVOList);
                session.setAttribute("businessRuleList", businessRuleVOList);

                payIfeTableInfo=profileManagementManager.getAllPayIfeFieldsInformation(null, null,tableAliasName);
                session.setAttribute("payIfeTableInfo", payIfeTableInfo);
                request.setAttribute("payIfeTableInfo", payIfeTableInfo);
                session.setAttribute("tableAliasName", tableAliasName);
                request.setAttribute("tableAliasName", tableAliasName);
            }
            else if(actionVO.isDelete())
            {
                isDelete=profileManagementManager.deleteBusinessProfile(actionVO.getActionCriteria().split("_")[0]);
                request.setAttribute("DELETED", isDelete);
                rdDelete.forward(request,response);
                return;
            }
            else
            {
                businessRuleVOList = profileManagementManager.getMapOfBusinessRuleDetails(null, null);

                payIfeTableInfo=profileManagementManager.getAllPayIfeFieldsInformation(null, null,tableAliasName);

                session.setAttribute("businessRuleList", businessRuleVOList);
                request.setAttribute("businessRuleList", businessRuleVOList);
                session.setAttribute("payIfeTableInfo", payIfeTableInfo);
                request.setAttribute("payIfeTableInfo", payIfeTableInfo);
                session.setAttribute("tableAliasName", tableAliasName);
                request.setAttribute("tableAliasName", tableAliasName);
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
                logger.error("Db violation exception while getting plane details", e);
                PZExceptionHandler.handleDBCVEException(e, null, null);
                request.setAttribute("catchError", "Kindly check for the Business Profile after sometime");
                rdError.forward(request, response);
                return;
            }
        }

    }

   /* public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        doGet(request, response);
    }
*/
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
