package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.enums.ConsolidatedAppStatus;
import com.manager.ApplicationManager;
import com.manager.FileManager;
import com.manager.vo.ActionVO;
import com.manager.vo.gatewayVOs.GatewayTypeVO;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import com.vo.applicationManagerVOs.AppFileDetailsVO;
import com.vo.applicationManagerVOs.ApplicationManagerVO;
import com.vo.applicationManagerVOs.ConsolidatedApplicationVO;
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
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Created by admin on 07-Apr-17.
 */
public class ConsolidatedHistoryAction extends HttpServlet
{
    private static Logger logger =new Logger(ConsolidatedHistoryAction.class.getName());
    ResourceBundle applicationResourceBundle= LoadProperties.getProperty("com.directi.pg.documentServer");
    public String APPLICATION_ZIP_PATH = applicationResourceBundle.getString("APPLICATION_ZIP_PATH");
    private Functions functions = new Functions();

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        logger.debug("inside 0000000000........");

        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
        ApplicationManager applicationManager=new ApplicationManager();
        //VO
        ActionVO actionVO = new ActionVO();
        ConsolidatedApplicationVO consolidatedApplicationVO=new ConsolidatedApplicationVO();
        GatewayTypeVO gatewayTypeVO=null;
        ApplicationManagerVO applicationManagerVO=new ApplicationManagerVO();
        Map<String,AppFileDetailsVO> fileDetailsVOMap=null;
        AppFileDetailsVO fileDetailsVO=new AppFileDetailsVO();
        FileManager fileManager=new FileManager();
        Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap = null;

                //Enum instance
        ConsolidatedAppStatus consolidatedAppStatus=null;
        //Validation Error List
        ValidationErrorList validationErrorList=null;
        //Boolean
        boolean isUpdated=false;
       // boolean isDeleted=false;


        String directoryPath="";
        String ftpPath="";
        //Request Dispatcher
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/consolidatedHistory.jsp?MES=SUCCESS&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdError= request.getRequestDispatcher("/consolidatedHistory.jsp?MES=ERR&ctoken=" + user.getCSRFToken());
        logger.debug("inside 1111111........");
        try
        {
            /*if (functions.isValueNull(request.getParameter("action")))
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
            }*/

            if(actionVO.isDelete())
            {
                String MemberID=request.getParameter("memberid");
                String Name=request.getParameter("gateway");
                String consolidatedId=request.getParameter("consolidatedId");
                String filename = request.getParameter("filename");
                logger.debug("consolidated ID...."+consolidatedApplicationVO.getConsolidated_id());
                logger.debug("Member ID.........."+request.getParameter("memberid"));

                //deleting Zip in consolidated ZIp Folder

                fileDetailsVOMap = applicationManager.deleteConsolidatedZip(consolidatedApplicationVO.getConsolidated_id(), request.getParameter("memberid"), request,filename);
                boolean isDeleted = applicationManager.deleteConsolidated_application_History(actionVO.getActionCriteria().split("_")[0]);
                if (isDeleted){
                    request.setAttribute("message","consolidated Application is deleted successfully");
                }
                else{
                    request.setAttribute("error","consolidated Application not deleted successfully");
                }


            }
            else if(isUpdated)
                rdSuccess=request.getRequestDispatcher("/servlet/ManageBankApp?consolidatedId="+actionVO.getActionCriteria().split("_")[0]+"&MES=SUCCESS&ctoken=" + user.getCSRFToken());
            else if(!functions.isValueNull(request.getParameter("action")) || (functions.isValueNull(request.getParameter("action")) && request.getParameter("action").contains("_Download")))
            {
                String fileName = request.getParameter("fileName");
                String filePath = APPLICATION_ZIP_PATH +request.getParameter("memberid")+ "/" +fileName;

                applicationManager.sendFile(filePath, fileName, response);
                return;
            }
            else
            {
                request.setAttribute("actionVO",actionVO);
                request.setAttribute("catchError","Kindly check for the consolidated Id");
                rdError.forward(request,response);
                return;
            }


            request.setAttribute("isUpdated",isUpdated);
            request.setAttribute("actionVO",actionVO);
            session.setAttribute("actionVO",actionVO);
            rdSuccess.forward(request,response);
        }

        catch (PZTechnicalViolationException e)
        {
            logger.error("Technical exception while getting Consolidated App Status", e);
            PZExceptionHandler.handleTechicalCVEException(e, null, null);
            request.setAttribute("catchError", "invalid Status");
            rdError.forward(request, response);
            return;
        }
        /*catch (PZDBViolationException e)
        {
            logger.error("Db violation exception while getting Consolidated App Status", e);
            PZExceptionHandler.handleDBCVEException(e, null, null);
            request.setAttribute("catchError", "Invalid Status");
            rdError.forward(request, response);
            return;
        }*/
        catch (Exception e)
        {

            logger.error("Exception---"+e);
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
