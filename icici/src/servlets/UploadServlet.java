import com.directi.pg.*;
import com.manager.AppRequestManager;
import com.manager.ApplicationManager;
import com.enums.Module;
import com.vo.applicationManagerVOs.*;

import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.operations.PZOperations;
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

import java.util.*;


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

        // Merchants merchants= new Merchants();

        //Manager Instance
        ApplicationManager applicationManager = new ApplicationManager();
        AppRequestManager appRequestManager = new AppRequestManager();
        //VO instance
        ApplicationManagerVO applicationManagerVO=null;
        NavigationVO navigationVO=null;
        AppValidationVO appValidationVO=null;
        //list of VO instance
        HashMap<String,AppFileDetailsVO> fileDetailsVOHashMap=null;
        //Validation error
        ValidationErrorList validationErrorList = null;
        HttpSession session = Functions.getNewSession(request);
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        RequestDispatcher rdSuccess= request.getRequestDispatcher("/appNavigation.jsp?upload=7&Success=YES&ctoken="+user.getCSRFToken());
        RequestDispatcher rdError= request.getRequestDispatcher("/appNavigation.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        try
        {
            applicationManagerVO=appRequestManager.getApplicationManagerVO(session);
            navigationVO=appRequestManager.getNavigationVO(session, Module.ADMIN);
            appValidationVO=appRequestManager.getAppValidationVO(session);
            navigationVO.setCurrentPageNO(7);
            navigationVO.setUploadHit(true);
            applicationManagerVO.setSubmittedFileDetailsVO(applicationManager.uploadMultipleFileAppManager(request, applicationManagerVO,false,null));
            //validation for the upload
            validationErrorList=appRequestManager.validationForInterfaceOrApi(request,null,navigationVO,applicationManagerVO,false,false,appValidationVO);
            if(session.getAttribute("validationErrorList")!=null)
            {

                //validationErrorList=appRequestManager.compareValidationErrorList(request,(ValidationErrorList)session.getAttribute("validationErrorList"),validationErrorList);
                validationErrorList=appRequestManager.compareFileUploadValidationErrorList(applicationManagerVO,(ValidationErrorList)session.getAttribute("validationErrorList"),validationErrorList);

                session.setAttribute("validationErrorList",validationErrorList);
            }
            navigationVO.setUploadHit(false);

            logger.debug("setting request attribute");
            request.setAttribute("validationErrorList",validationErrorList);

            //Added for multiple KYC

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
            applicationManagerVO.setNotificationMessage(false,"Kyc",null);
            session.setAttribute("applicationManagerVO",applicationManagerVO);
            PZExceptionHandler.handleTechicalCVEException(tve,session.getAttribute("merchantid").toString(), PZOperations.APPLICATION_MANAGER_UPLOAD);
            rdError.forward(request,response);
            return;
        }
        catch (Exception e)
        {
            logger.error("Exception while uploading data", e);
        }

    }
}
