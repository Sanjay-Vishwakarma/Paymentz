import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.ApplicationManager;
import com.enums.ApplicationStatus;
import com.manager.vo.ActionVO;
import com.vo.applicationManagerVOs.NavigationVO;
import com.vo.applicationManagerVOs.AppValidationVO;
import com.vo.applicationManagerVOs.ApplicationManagerVO;

import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import com.validators.BankInputName;
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
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 1/20/15
 * Time: 8:15 PM
 * To change this template use File | Settings | File Templates.
 */

public class PopulateApplication extends HttpServlet
{

    Logger logger=new Logger(PopulateApplication.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response)throws ServletException,IOException
    {

        //instance created for util classes
        Functions functions=new Functions();

        //instance created for Vo
        ApplicationManagerVO applicationManagerVO=new ApplicationManagerVO();
        NavigationVO navigationVO = new NavigationVO();
        AppValidationVO appValidationVO = new AppValidationVO();
        ActionVO actionVO = new ActionVO();
        //instance of manager
        ApplicationManager applicationManager = new ApplicationManager();

        ValidationErrorList validationErrorList=null;

        HttpSession session = Functions.getNewSession(request);
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        Map<Integer, Map<Boolean, Set<BankInputName>>> fullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
        Map<Integer, Map<Boolean, Set<BankInputName>>> dependencyFullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
        Map<Integer, Map<Boolean, Set<BankInputName>>> dependencyOtherFullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
        Map<Boolean,Set<BankInputName>> dependencyPageViseValidation=new HashMap<Boolean, Set<BankInputName>>();
        Map<Boolean,Set<BankInputName>> dependencyOtherPageViseValidation=new HashMap<Boolean, Set<BankInputName>>();
        Map<Integer,Set<BankInputName>> otherValidation=new HashMap<Integer, Set<BankInputName>>();
        Set<BankInputName> otherValidationPageVise=new HashSet<BankInputName>();
        String update = request.getParameter("update");

        RequestDispatcher rdSuccessU = request.getRequestDispatcher("/appNavigation.jsp?MES=Success&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccessV = request.getRequestDispatcher("/appManagerView.jsp?MES=Success&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccessM = null;
        RequestDispatcher rdError = request.getRequestDispatcher("/listofapplicationmember.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdViewStep1 = request.getRequestDispatcher("/speedoptionview.jsp?MES=Success&ctoken="+user.getCSRFToken());
        try
        {
            session.setAttribute("applicationManagerVO",null);
            session.setAttribute("navigationVO",null);
            session.setAttribute("appValidationVO",null);
            logger.debug("inside populate Application");
            validationErrorList=validateMandatoryParameters(request);
            if(!validationErrorList.isEmpty())
            {
                request.setAttribute("error",validationErrorList);
                rdError.forward(request,response);
                return;
            }
            actionVO.setAllContentAuto(request.getParameter("action"));
            applicationManagerVO.setMemberId(request.getParameter("action").split("_")[0]);
            applicationManager.populateAppllicationData(applicationManagerVO);

            appValidationVO = applicationManager.loadAllMerchantBankMapping(applicationManagerVO.getMemberId());


            //pages in order how to be navigated
            navigationVO.addStepAndPageName("companyprofile.jsp");
            navigationVO.addStepAndPageName("ownershipprofile.jsp");
            navigationVO.addStepAndPageName("businessprofile.jsp");
            navigationVO.addStepAndPageName("bankapplication.jsp");
            navigationVO.addStepAndPageName("cardholderprofile.jsp");
            navigationVO.addStepAndPageName("extradetailsprofile.jsp");
            navigationVO.addStepAndPageName("upload.jsp");

            applicationManager.setValidationForMember(applicationManagerVO.getMemberId(), navigationVO, fullValidationForStep, dependencyFullValidationForStep,dependencyPageViseValidation,otherValidation,dependencyOtherFullValidationForStep,otherValidationPageVise,dependencyOtherPageViseValidation,appValidationVO);


            //set in session value
            session.setAttribute("apptoid",request.getParameter("apptoid"));
            session.setAttribute("applicationManagerVO",applicationManagerVO);
            session.setAttribute("navigationVO",navigationVO);
            session.setAttribute("appValidationVO",appValidationVO);
            session.setAttribute("actionVO",actionVO);

            request.setAttribute("isUpdate",update);
            request.setAttribute("fullValidationForStep", fullValidationForStep);
            request.setAttribute("dependencyFullValidationForStep", dependencyFullValidationForStep);
            request.setAttribute("otherValidation", otherValidation);
            request.setAttribute("dependencyOtherFullValidationForStep", dependencyOtherFullValidationForStep);

            //using for step 1 view and update

            if(actionVO.getActionCriteria().contains("SPEED"))
            {
                if(actionVO.isView())
                {
                    rdViewStep1.forward(request, response);
                    return;
                }
                else if((actionVO.isEdit()))
                {
                    rdViewStep1.forward(request, response);
                    return;
                }
            }
            else
            {
                //using for commom flow view and update
                if (actionVO.isView())
                {
                    rdSuccessV.forward(request, response);
                    return;
                }

                if (actionVO.isUpdate())
                {
                    rdSuccessM = request.getRequestDispatcher("/servlet/AppManagerStatus?status=" + ApplicationStatus.MODIFIED.name() + "&SPageno=" + Functions.convertStringtoInt(request.getParameter("SPageno"), 1) + "&SRecords=" + Functions.convertStringtoInt(request.getParameter("SRecords"), 15) + "&ctoken=" + user.getCSRFToken());
                    rdSuccessM.forward(request, response);
                    return;
                }

                rdSuccessU.forward(request,response);
            }
        }
        catch (Exception e)
        {
            logger.error("Mail class exception::",e);
        }
    }
    public ValidationErrorList validateMandatoryParameters(HttpServletRequest req)
    {
        List<InputFields> inputOptionalField = new ArrayList<InputFields>();
        InputValidator inputValidator = new InputValidator();
        ValidationErrorList errorList = new ValidationErrorList();
        //inputOptionalField.add(InputFields.SMALL_ACTION);
        inputValidator.InputValidations(req,inputOptionalField, errorList,true);
        return errorList;
    }

}

