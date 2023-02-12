package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ProfileManagementManager;
import com.manager.vo.ActionVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.userProfileVOs.MerchantVO;
import com.manager.vo.userProfileVOs.TemplateVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
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
 * Created on 23/9/15.
 */
public class AddOrUpdateUserProfile extends HttpServlet
{
    private static Logger logger = new Logger(AddOrUpdateUserProfile.class.getName());
    private Functions functions = new Functions();

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        HttpSession session = functions.getNewSession(request);

        PartnerFunctions partnerFunctions = new PartnerFunctions();
        if (!partnerFunctions.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }

        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        ValidationErrorList validationErrorList = null;

        //Vo instance
        MerchantVO merchantVO = new MerchantVO();
        TemplateVO templateVO = new TemplateVO();
        ActionVO actionVO = new ActionVO();

        //Manager instance
        ProfileManagementManager profileManagementManager=new ProfileManagementManager();

        int rowCount=0;

        //boolean Instance
        boolean isProfileSaved=false;
        boolean isProfileAdd=false;

        String businessId;
        String isApplicable;
        String value1;
        String value2;

        RequestDispatcher rdSuccess=null;
        RequestDispatcher rdError=request.getRequestDispatcher("/addOrUpdateUserProfile.jsp?MES=ERR&ctoken="+user.getCSRFToken());

        try
        {
            actionVO.setAllContentAuto(request.getParameter("action"));
            validationErrorList=validateMandatoryParameter(request,actionVO);
            if(!validationErrorList.isEmpty())
            {
                logger.error("validation error");

               if(((ActionVO)session.getAttribute("actionVO")).isEdit() || ((ActionVO)session.getAttribute("actionVO")).isView())
               {
                   request.setAttribute("userSetting",session.getAttribute("userSetting"));
               }
                request.setAttribute("actionVO",session.getAttribute("actionVO"));
                request.setAttribute("errorL",validationErrorList);
                rdError.forward(request,response);
                return;
            }

            actionVO.setAllContentAuto(request.getParameter("action"));

            merchantVO.setPartnerId(request.getParameter("partnerId"));
            //merchantVO.setMemberId(functions.isValueNull(request.getParameter("memberid")) ? request.getParameter("memberid") : "");
            merchantVO.setCurrency(request.getParameter("currency"));
            merchantVO.setCompanyName(request.getParameter("partnerName"));
            merchantVO.setContactEmail(request.getParameter("contact_emails"));
            merchantVO.setAddressVerification(request.getParameter("addressvalidation"));
            merchantVO.setAddressDetailDisplay(request.getParameter("addressdetaildisplay"));
            merchantVO.setAutoRedirect(request.getParameter("autoRedirect"));
            merchantVO.setDefaultMode(request.getParameter("defaultMode"));
            merchantVO.setRiskProfile(request.getParameter("profileid"));
            merchantVO.setBusinessProfile(request.getParameter("businessProfile"));
            merchantVO.setOfflineTransactionURL("https://" + request.getParameter("offlineProcessingUrl") + "/transactionServices");
            merchantVO.setOnlineTransactionURL("https://" + request.getParameter("onlineProcessingUrl") + "/transactionServices");
            merchantVO.setOnlineThreshold(Integer.valueOf(request.getParameter("onlineThreshold")));
            merchantVO.setOfflineThreshold(Integer.valueOf(request.getParameter("offlinethreshold")));

            templateVO.setBackgroundColor(functions.isValueNull(request.getParameter("background")) ? request.getParameter("background") : "");
            templateVO.setForegroundColor(functions.isValueNull(request.getParameter("foreground"))?request.getParameter("foreground"):"");
            templateVO.setFontColor(functions.isValueNull(request.getParameter("font"))?request.getParameter("font"):"");
            templateVO.setLogo(functions.isValueNull(request.getParameter("merchantlogo"))?request.getParameter("merchantlogo"):"");

            if(actionVO.isAdd())
            {

                isProfileAdd=profileManagementManager.insertUserProfile(merchantVO,templateVO,(String)session.getAttribute("merchantid"));

            }
            else
            {
                isProfileSaved=profileManagementManager.updateUserProfile(merchantVO, templateVO, actionVO.getActionCriteria().split("_")[0]);
            }
            if(isProfileSaved)
            {
                rdSuccess=request.getRequestDispatcher("/net/UserProfileList?toid=" +merchantVO.getMemberId()+"&ctoken="+user.getCSRFToken());
                request.setAttribute("update",isProfileSaved);
                rdSuccess.forward(request,response);
                return;
            }//Created condition to check result for added data.
            else if(isProfileAdd){
                rdSuccess=request.getRequestDispatcher("/net/UserProfileList?toid=" +merchantVO.getMemberId()+"&ctoken="+user.getCSRFToken());
                request.setAttribute("insert",isProfileAdd);
                rdSuccess.forward(request,response);
                return;
            }
            else
            {
                request.setAttribute("actionVO",session.getAttribute("actionVO"));
                request.setAttribute("catchErrorL","Profile not saved");
                rdError.forward(request,response);
                return;
            }

        }
        catch(PZDBViolationException e)
        {
            PZExceptionHandler.handleDBCVEException(e, null, null);
            request.setAttribute("actionVO",session.getAttribute("actionVO"));
            request.setAttribute("catchErrorL","Kindly check on for the User Profile after some time");
            rdError.forward(request,response);
            return;
        }
    }

    private ValidationErrorList validateMandatoryParameter(HttpServletRequest request,ActionVO actionVO)
    {
        ValidationErrorList validationErrorList= new ValidationErrorList();
        List<InputFields> mandatoryFields=new ArrayList<InputFields>();
       /* if(actionVO.isAdd())
        {
            mandatoryFields.add(InputFields.MEMBERID);
        }*/
        mandatoryFields.add(InputFields.CURRENCY);
        mandatoryFields.add(InputFields.ONLINEPROCESSINGURL);
        mandatoryFields.add(InputFields.OFFLINEPROCESSINGURL);
        mandatoryFields.add(InputFields.ONLINETHRESHOLD);
        mandatoryFields.add(InputFields.OFFLINETHRESHOLD);
        mandatoryFields.add(InputFields.DEFAULTMODE);
        mandatoryFields.add(InputFields.RISKPROFILEID);
        mandatoryFields.add(InputFields.BUSINESSPROFILE_ID);
        mandatoryFields.add(InputFields.SMALL_ACTION);


        InputValidator inputValidator = new InputValidator();
        inputValidator.InputValidations(request,mandatoryFields,validationErrorList,false);

        List<InputFields> optionalParameter=new ArrayList<InputFields>();
        mandatoryFields.add(InputFields.BACKGROUND);
        mandatoryFields.add(InputFields.FOREGROUND);
        mandatoryFields.add(InputFields.FONT);
        mandatoryFields.add(InputFields.MERCHANTLOGO);

        inputValidator.InputValidations(request,optionalParameter,validationErrorList,true);

        return validationErrorList;

    }
}
