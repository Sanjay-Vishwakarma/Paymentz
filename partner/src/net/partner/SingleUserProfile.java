package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ProfileManagementManager;
import com.manager.vo.ActionVO;
import com.manager.vo.userProfileVOs.UserSetting;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
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

/**
 * Created on 21/9/15.
 */
public class SingleUserProfile extends HttpServlet
{

    private static Logger logger =new Logger(SingleUserProfile.class.getName());

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
        //VO
        ActionVO actionVO = new ActionVO();
        UserSetting userSetting =null;

        //Validation Error List
        ValidationErrorList validationErrorList=null;

        //BOOLEAN
        boolean isDeleted=false;

        //Request Dispatcher
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/addOrUpdateUserProfile.jsp?MES=SUCCESS&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdError= request.getRequestDispatcher("/net/UserProfileList?MES=ERR&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdDelete= request.getRequestDispatcher("/userProfile.jsp?MES=ERR&ctoken=" + user.getCSRFToken());

        try
        {
            validationErrorList = validateMandatorParameter(request);
            if (!validationErrorList.isEmpty())
            {
                logger.error("validation error");
                request.setAttribute("error", validationErrorList);
                rdError.forward(request, response);
                return;
            }

            actionVO.setAllContentAuto(request.getParameter("action"));

            if (actionVO.isEdit() || actionVO.isView())
            {
                //this is to get the current configuration of the user
                userSetting = profileManagementManager.getSingleUserProfile(actionVO.getActionCriteria().split("_")[0]);
                request.setAttribute("userSetting",userSetting);
                session.setAttribute("userSetting",userSetting);
            }
            else if (actionVO.isDelete())
            {
                //Delete User Profile
                isDeleted=profileManagementManager.deleteUserProfileById(actionVO.getActionCriteria().split("_")[0]);
                request.setAttribute("DELETED", isDeleted);
                rdDelete.forward(request,response);
                return;
            }
        }
        catch (PZDBViolationException e)
        {
            logger.error("PZDViolation Exception while getting User profile details:::", e);
            PZExceptionHandler.handleDBCVEException(e, "partnerId:::" + (String) session.getAttribute("merchantid"), PZOperations.USERID);
            request.setAttribute("catchError","Kindly check for the User Profile after sometime");
            rdError.forward(request,response);
            return;
        }
        session.setAttribute("actionVO", actionVO);
        request.setAttribute("actionVO", actionVO);
        rdSuccess.forward(request, response);



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
