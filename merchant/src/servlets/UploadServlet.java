import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.Merchants;
import com.enums.Module;
import com.manager.AppRequestManager;
import com.manager.ApplicationManager;
import com.manager.dao.PartnerDAO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.operations.PZOperations;
import com.validators.BankInputName;
import com.vo.applicationManagerVOs.*;
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
 * Servlet implementation class UploadServlet
 * @version 1.0
 * @author Niket
 */

public class UploadServlet extends HttpServlet
{
    Logger logger = new Logger(UploadServlet.class.getName());
    //private static String log_store = ApplicationProperties.getProperty("LOG_STORE");
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException
    {

        logger.debug("Content Type-----"+request.getContentType());

        String alternate_name=request.getParameter("alternate_name");

        Merchants merchants= new Merchants();
        Functions functions = new Functions();

        //Manager Instance
        AppRequestManager appRequestManager = new AppRequestManager();
        //VO instance
        //list of VO instance
        HashMap<String,AppFileDetailsVO> fileDetailsVOHashMap=null;
        //Validation error
        ValidationErrorList validationErrorList = null;
        HttpSession session = Functions.getNewSession(request);
        if (!merchants.isLoggedIn(session))
        {
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        //This code is added here for iframe in crossdomain
        ApplicationManagerVO applicationManagerVO=new ApplicationManagerVO();
        NavigationVO navigationVO = new NavigationVO();
        ApplicationManager applicationManager = new ApplicationManager();
        AppValidationVO appValidationVO = null;
        String pagenoforiframe=(String)session.getAttribute("pageno");
        String memberid=(String)session.getAttribute("merchantid");
        if(functions.isValueNull(pagenoforiframe)){
           /* RequestDispatcher rd = request.getRequestDispatcher("/servlet/PopulateApplication?ctoken=" + user.getCSRFToken());
            rd.forward(request, response);
            return;*/

            Map<Integer, Map<Boolean, Set<BankInputName>>> fullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
            Map<Integer, Map<Boolean, Set<BankInputName>>> dependencyFullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
            Map<Integer, Map<Boolean, Set<BankInputName>>> dependencyOtherFullValidationForStep=new HashMap<Integer, Map<Boolean, Set<BankInputName>>>();
            Map<Boolean,Set<BankInputName>> dependencyPageViseValidation=new HashMap<Boolean, Set<BankInputName>>();
            Map<Boolean,Set<BankInputName>> dependencyOtherPageViseValidation=new HashMap<Boolean, Set<BankInputName>>();
            Map<Integer,Set<BankInputName>> otherValidation=new HashMap<Integer, Set<BankInputName>>();
            Set<BankInputName> otherValidationPageVise=new HashSet<BankInputName>();
            PartnerDAO partnerDAO = new PartnerDAO();
            //Request Dispatcher
             try
            {
                logger.debug("inside Upload servlet Application");
                applicationManagerVO.setMemberId(memberid);
                applicationManagerVO.setPartnerid(session.getAttribute("partnerid").toString());
                session.setAttribute("company", partnerDAO.getPartnerName((String)session.getAttribute("partnerid").toString()));
                appValidationVO=applicationManager.loadAllMerchantBankMapping(applicationManagerVO.getMemberId());
                if(functions.isValueNull(String.valueOf(session.getAttribute("applicationManagerVO"))))
                {
                    applicationManagerVO=(ApplicationManagerVO)session.getAttribute("applicationManagerVO");
                }
                else
                {
                    applicationManager.populateAppllicationData(applicationManagerVO);
                }
                applicationManager.setValidationForMember(applicationManagerVO.getMemberId(), navigationVO, fullValidationForStep, dependencyFullValidationForStep,dependencyPageViseValidation,otherValidation,dependencyOtherFullValidationForStep,otherValidationPageVise,dependencyOtherPageViseValidation,appValidationVO);

                //set in session value
                session.setAttribute("applicationManagerVO",applicationManagerVO);
                session.setAttribute("navigationVO",navigationVO);
                session.setAttribute("appValidationVO",appValidationVO);
                request.setAttribute("fullValidationForStep", fullValidationForStep);
                request.setAttribute("dependencyFullValidationForStep", dependencyFullValidationForStep);
                request.setAttribute("otherValidation", otherValidation);
                request.setAttribute("dependencyOtherFullValidationForStep", dependencyOtherFullValidationForStep);
                //pages in order how to be navigated
                navigationVO.addStepAndPageName("companyprofile.jsp");
                navigationVO.addStepAndPageName("ownershipprofile.jsp");
                navigationVO.addStepAndPageName("businessprofile.jsp");
                navigationVO.addStepAndPageName("bankapplication.jsp");
                navigationVO.addStepAndPageName("cardholderprofile.jsp");
                navigationVO.addStepAndPageName("upload.jsp");
                //navigationVO.addStepAndPageName("reports.jsp");
            }
            catch (Exception e)
            {
                logger.error("Mail class exception::",e);
            }
        }
        // Iframe code ends here
        response.setHeader("X-Frame-Options", "ALLOWALL");
        session.setAttribute("X-Frame-Options", "ALLOWALL");
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/appNavigation.jsp?upload=6&Success=YES&ctoken="+user.getCSRFToken());
        RequestDispatcher rdError= request.getRequestDispatcher("/appNavigation.jsp?upload=6&MES=ERR&ctoken="+user.getCSRFToken());
        try
        {

            applicationManagerVO=appRequestManager.getApplicationManagerVO(session);
            navigationVO=appRequestManager.getNavigationVO(session, Module.MERCHANT);
            navigationVO.setCurrentPageNO(6);
            navigationVO.setUploadHit(true);
            //changes for single file upload --SurajT.
            if (functions.isValueNull(alternate_name)){
                //method for single button file upload
                applicationManagerVO.setSubmittedFileDetailsVO(applicationManager.uploadMultipleFileAppManagerNew(request, applicationManagerVO, false, null, alternate_name));
            }
            else {
                //method for multiple file upload on one click
                applicationManagerVO.setSubmittedFileDetailsVO(applicationManager.uploadMultipleFileAppManager(request, applicationManagerVO, false, null));
            }

            appValidationVO=applicationManager.loadAllMerchantBankMapping(applicationManagerVO.getMemberId());
            //validation for the upload
            validationErrorList=appRequestManager.validationForInterfaceOrApi(request,null,navigationVO,applicationManagerVO,false,false,appValidationVO);
            logger.debug("before if validationErrorList...."+validationErrorList);
            if(session.getAttribute("validationErrorList")!=null)
            {
                logger.debug("after getting value in seesion inside if......"+validationErrorList);
                validationErrorList=appRequestManager.compareFileUploadValidationErrorList(applicationManagerVO,(ValidationErrorList)session.getAttribute("validationErrorList"),validationErrorList);

                session.setAttribute("validationErrorList",validationErrorList);
                logger.debug("Session value......."+validationErrorList);
            }
            navigationVO.setUploadHit(false);

            logger.debug("setting request attribute");
            request.setAttribute("validationErrorList",validationErrorList);
            if(applicationManagerVO.getFileDetailsVOs()!=null )
            {   for (FileDetailsListVO fileDetailsVOlist :applicationManagerVO.getFileDetailsVOs().values() )
            {
                for(AppFileDetailsVO fileDetailsVO:fileDetailsVOlist.getFiledetailsvo())
                {
                    logger.debug("uploadServlet before setting session..... " + applicationManagerVO.getFileDetailsVOs().get(fileDetailsVO.getFieldName()));
                    if (applicationManagerVO.getFileDetailsVOs().get(fileDetailsVO.getFieldName()) != null)
                    {

                            //logger.debug("uploadServlet before setting session........ " + applicationManagerVO.getFileDetailsVOs().get(fileDetailsVO.getFieldName()).isSuccess());
                        }
                    }
                }
            }
            session.setAttribute("applicationManagerVO",applicationManagerVO);
            session.setAttribute("navigationVO",navigationVO);
            logger.debug("after setting request attribute");
            if(validationErrorList!=null && !validationErrorList.isEmpty())
            {
                logger.debug("dispatch");
                rdError.forward(request,response);
            }
            else
            {
                logger.debug("2 dispatch");
                rdSuccess.forward(request,response);
            }
        }
        catch (PZTechnicalViolationException tve)
        {
            logger.error("PZ technical voilation exception while uploading data", tve);
            applicationManagerVO.setNotificationMessage(false," Kyc",null);
            session.setAttribute("applicationManagerVO",applicationManagerVO);
            PZExceptionHandler.handleTechicalCVEException(tve,session.getAttribute("merchantid").toString(), PZOperations.APPLICATION_MANAGER_UPLOAD);
            rdError.forward(request,response);
            return;
        }
        catch (Exception e)
        {
            logger.error("Exception while uploading data",e);
        }

    }
}
