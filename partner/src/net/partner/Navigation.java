package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.AppRequestManager;
import com.manager.ApplicationManager;
import com.manager.FileManager;
import com.enums.Module;
import com.vo.applicationManagerVOs.NavigationVO;
import com.vo.applicationManagerVOs.AppValidationVO;
import com.vo.applicationManagerVOs.ApplicationManagerVO;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Created with IntelliJ IDEA.
 * User: Niket
 * Date: 1/21/15
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class Navigation extends HttpServlet
{
    private static Logger logger = new Logger(Navigation.class.getName());
    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request,response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

        Functions functions=new Functions();

        HttpSession session = Functions.getNewSession(request);
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            logger.debug("Partner is logout ");
            response.sendRedirect("/partner/logout.jsp");
            return;
        }
        //Manager instance
        AppRequestManager appRequestManager = new AppRequestManager();
        ApplicationManager applicationManager = new ApplicationManager();
        FileManager fileManager = new FileManager();
        //Vo instance
        ApplicationManagerVO applicationManagerVO=null;
        ApplicationManagerVO databaseApplicationManagerVO=null;
        NavigationVO navigationVO =null;
        AppValidationVO appValidationVO =null;

        //Collection
        Map<Integer, Map<Boolean, Set<BankInputName>>> fullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
        Map<Integer, Map<Boolean, Set<BankInputName>>> dependencyFullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
        Map<Integer, Map<Boolean, Set<BankInputName>>> dependencyOtherFullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
        Map<Boolean,Set<BankInputName>> dependencyPageViseValidation=new HashMap<Boolean, Set<BankInputName>>();
        Map<Boolean,Set<BankInputName>> dependencyOtherPageViseValidation=new HashMap<Boolean, Set<BankInputName>>();
        Map<Integer,Set<BankInputName>> otherValidation=new HashMap<Integer, Set<BankInputName>>();
        Set<BankInputName> otherValidationPageVise=new HashSet<BankInputName>();

        //ValidationErrorList instance
        ValidationErrorList validationErrorList = null;
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/appNavigation.jsp?MES=Success&ctoken="+user.getCSRFToken());
        RequestDispatcher rdError= request.getRequestDispatcher("/appNavigation.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSubmitsucess= request.getRequestDispatcher("/thankYou.jsp?ctoken="+user.getCSRFToken());
        try
        {
            //getting applicationManagerVO & navigationVO
            applicationManagerVO=appRequestManager.getApplicationManagerVO(session);
            navigationVO=appRequestManager.getNavigationVO(session, Module.PARTNER);
            appValidationVO=appRequestManager.getAppValidationVO(session);
            applicationManagerVO.setUser(Module.PARTNER.name());
            databaseApplicationManagerVO=applicationManagerVO;
            if(functions.isValueNull(request.getParameter("next")) || functions.isValueNull(request.getParameter("previous")) || functions.isValueNull(request.getParameter("save")) || functions.isValueNull(request.getParameter("appsubmit")) || functions.isValueNull(request.getParameter("apptab")))
            {
                if(functions.isValueNull(request.getParameter("next")))
                {
                    navigationVO.setNextPageNO(Integer.valueOf(request.getParameter("next")));

                }
                else if(functions.isValueNull(request.getParameter("previous")))
                {

                    navigationVO.setPreviousPageNO(Integer.valueOf(request.getParameter("previous")));

                }
                else if(functions.isValueNull(request.getParameter("save")))
                {
                    logger.debug("currentPageNo::"+request.getParameter("save"));
                    navigationVO.setCurrentPageNO(Integer.valueOf(request.getParameter("save")));
                }
                else if(functions.isValueNull(request.getParameter("appsubmit")))
                {
                    //no page info to be added
                }
                else if(functions.isValueNull(request.getParameter("apptab")))//this is for side bar
                {
                    navigationVO.setCurrentPageNO(Integer.valueOf(request.getParameter("currentPage")));
                    logger.debug("currentPage::"+navigationVO.getCurrentPageNO());
                }

                validationErrorList=appRequestManager.validationForInterfaceOrApi(request,null,navigationVO,applicationManagerVO,false,false,appValidationVO);
                if(session.getAttribute("validationErrorList")!=null)
                {
                    validationErrorList=appRequestManager.compareValidationErrorList(request,(ValidationErrorList)session.getAttribute("validationErrorList"),validationErrorList);

                    session.setAttribute("validationErrorList",validationErrorList);
                }

                //This is to retain the database value for that profile.
                if(validationErrorList!=null && !validationErrorList.isEmpty())
                {
                    appRequestManager.retainCurrentPage(applicationManagerVO,databaseApplicationManagerVO,navigationVO,validationErrorList);
                }

                //after validation Save to the dedicated table
                if(functions.isValueNull(request.getParameter("save")))
                {
                    boolean savedSuccess=applicationManager.saveCurrentPage(navigationVO,applicationManagerVO,true,null);
                    applicationManagerVO.setNotificationMessage(savedSuccess,applicationManagerVO.getNotificationMessage()," Saved Successfully");
                }
                //this is for all profile to be submitted
                if(functions.isValueNull(request.getParameter("appsubmit")) && (validationErrorList==null || validationErrorList.isEmpty()))
                {
                    logger.debug(" inside save of all profile");
                    boolean submitAllProfile=applicationManager.submitAllProfile(applicationManagerVO,false,false);

                    applicationManagerVO.setNotificationMessage(submitAllProfile,applicationManagerVO.getNotificationMessage()," Submitted Successfully<br> Thank you for choosing us for processing your transactions, Our team will get back to you  once your application is approved.");

                    if(applicationManagerVO.getNotificationMessage()!=null)
                    {
                        rdSubmitsucess.forward(request,response);
                    }
                }
                else if(functions.isValueNull(request.getParameter("appsubmit")) && !validationErrorList.isEmpty())
                {
                    session.setAttribute("validationErrorList",validationErrorList);
                }
            }
            else if(functions.isValueNull(request.getParameter("cancel")))
            {

            }
            applicationManager.setValidationForMember(applicationManagerVO.getMemberId(), navigationVO, fullValidationForStep, dependencyFullValidationForStep,dependencyPageViseValidation,otherValidation,dependencyOtherFullValidationForStep,otherValidationPageVise,dependencyOtherPageViseValidation,appValidationVO);

            request.setAttribute("fullValidationForStep", fullValidationForStep);
            request.setAttribute("dependencyFullValidationForStep", dependencyFullValidationForStep);
            request.setAttribute("otherValidation", otherValidation);
            request.setAttribute("dependencyOtherFullValidationForStep", dependencyOtherFullValidationForStep);
            request.setAttribute("validationErrorList",validationErrorList);
            session.setAttribute("applicationManagerVO",databaseApplicationManagerVO);
            request.setAttribute("selectedID",request.getParameter("selectedID"));
            //session.setAttribute("applicationManagerVO",applicationManagerVO);
            if(validationErrorList!=null && !validationErrorList.isEmpty())
            {
                rdError.forward(request,response);
            }
            else
            {
                rdSuccess.forward(request,response);
            }
        }
        catch(Exception e)
        {
            logger.error("Exception in Navigation Page",e);
        }
    }
}
