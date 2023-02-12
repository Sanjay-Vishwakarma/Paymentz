package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.enums.ApplicationManagerTypes;
import com.enums.ConsolidatedAppStatus;
import com.manager.AppFileManager;
import com.manager.ApplicationManager;
import com.manager.vo.ActionVO;
import com.manager.vo.gatewayVOs.GatewayTypeVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import com.utils.AppFileHandlingUtil;
import com.vo.applicationManagerVOs.ApplicationManagerVO;
import com.vo.applicationManagerVOs.ConsolidatedApplicationVO;
import org.apache.commons.lang.StringUtils;
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
 * Created by admin on 10/1/2015.
 */
public class SendMailToBank extends HttpServlet
{
    private static Logger logger =new Logger(SendMailToBank.class.getName());
    ResourceBundle applicationResourceBundle= LoadProperties.getProperty("com.directi.pg.documentServer");
    public String APPLICATION_ZIP_PATH = applicationResourceBundle.getString("APPLICATION_ZIP_PATH");
    private Functions functions = new Functions();

    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

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
        ConsolidatedApplicationVO consolidatedApplicationVO=null;
        GatewayTypeVO gatewayTypeVO=null;
        ApplicationManagerVO applicationManagerVO=new ApplicationManagerVO();

        //MailService mailService = new MailService();
        AsynchronousMailService asynchronousMailService=new AsynchronousMailService();
        HashMap applySendMailtoBank = new HashMap();

        AppFileHandlingUtil fileHandlingUtil = new AppFileHandlingUtil();
        AppFileManager fileManager = new AppFileManager();
        Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap = null;

        //Enum instance
        ConsolidatedAppStatus consolidatedAppStatus=null;
        //Validation Error List
        ValidationErrorList validationErrorList=null;
        //Boolean
        boolean isUpdated=false;
        boolean isDeleted=false;
        boolean saved=false;
        //Request Dispatcher
        RequestDispatcher rdSuccess= request.getRequestDispatcher("/manageBankApp.jsp?MES=SUCCESS&ctoken=" + user.getCSRFToken());
        RequestDispatcher rdError= request.getRequestDispatcher("/manageBankApp.jsp?MES=ERR&ctoken=" + user.getCSRFToken());
        ResourceBundle rb1 = null;
        String language_property1 = (String) session.getAttribute("language_property");
        rb1 = LoadProperties.getProperty(language_property1);
        String SendMailToBank_consolidated_errormsg = StringUtils.isNotEmpty(rb1.getString("SendMailToBank_consolidated_errormsg")) ? rb1.getString("SendMailToBank_consolidated_errormsg") : "consolidated application deleted successfully.";
        String SendMailToBank_kindly_errormsg = StringUtils.isNotEmpty(rb1.getString("SendMailToBank_kindly_errormsg")) ? rb1.getString("SendMailToBank_kindly_errormsg") : "Kindly check for the consolidated Id";
        String SendMailToBank_status_errormsg = StringUtils.isNotEmpty(rb1.getString("SendMailToBank_status_errormsg")) ? rb1.getString("SendMailToBank_status_errormsg") : "Invalid Status";


        try
        {
            if (functions.isValueNull(request.getParameter("action")))
            {
                validationErrorList = validateMandatorParameter(request);
                if(!validationErrorList.isEmpty())
                {
                    logger.error("validation error");
                    request.setAttribute("error",validationErrorList);
                    rdError.forward(request,response);
                    return;
                }
                actionVO.setAllContentAuto(request.getParameter("action"));
            }
            if(actionVO.isEdit())
            {
                consolidatedAppStatus= ConsolidatedAppStatus.getEnum(request.getParameter(actionVO.getActionCriteria().split("_")[0] + "_status"));
                isUpdated = applicationManager.updateConsolidatedAppStatus(consolidatedAppStatus, actionVO.getActionCriteria().split("_")[0]);
            }
            /*else if(actionVO.isDownload())
            {
                logger.debug("action in........");
                fileHandlingUtil=fileManager.getFileHandlingUtilAccordingToTheProperty();
                logger.debug("action in 111........");
                ConsolidatedApplicationVO downloadConsolidatedVO=new ConsolidatedApplicationVO();
                logger.debug("action in 2222........");
                consolidatedApplicationVO=new ConsolidatedApplicationVO();
                consolidatedApplicationVO.setConsolidated_id(request.getParameter("action").split("_")[0]);
                logger.debug("set Consolidated_id........" + request.getParameter("action").split("_")[0]);
                consolidatedApplicationVOMap=applicationManager.getconsolidated_application(consolidatedApplicationVO);
                logger.debug("consolidatedApplicationVOMap......" + consolidatedApplicationVOMap);

                for(Map.Entry<String,ConsolidatedApplicationVO> consolidatedApplicationVOEntry:consolidatedApplicationVOMap.entrySet())
                {
                    downloadConsolidatedVO=consolidatedApplicationVOEntry.getValue();
                    logger.debug("downloadConsolidatedVO......"+downloadConsolidatedVO);
                }
                File file=null;
                logger.debug("inside file.....");
                file=fileManager.getConsolidatedZipFile(downloadConsolidatedVO,fileHandlingUtil);
                fileHandlingUtil.sendFile(file,response);
                if(fileHandlingUtil instanceof FtpFileHandlingUtil)
                    fileManager.deleteFileAccordingToFTPProperty(file,null,(FtpFileHandlingUtil)fileHandlingUtil);
                return;
            }*/
            else if(actionVO.isMail())
            {
                logger.error("Mail in");
                //Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap=null;

                consolidatedApplicationVOMap=applicationManager.getconsolidated_applicationForMemberIdOrPgTypeId(null,null,actionVO.getActionCriteria().split("_")[0]);

                consolidatedApplicationVO=consolidatedApplicationVOMap.get(actionVO.getActionCriteria().split("_")[0]);

                applicationManagerVO.setMemberId(consolidatedApplicationVO.getMemberid());
                logger.error("Memberid....."+consolidatedApplicationVO.getMemberid());
                applicationManager.populateAppllicationData(applicationManagerVO);

                applySendMailtoBank.put(MailPlaceHolder.PGTYPEID, consolidatedApplicationVO.getPgtypeid());

                logger.error("PGTYPEID........"+consolidatedApplicationVO.getPgtypeid());

                applySendMailtoBank.put(MailPlaceHolder.COMPANYNAME, applicationManagerVO.getCompanyProfileVO().getCompanyProfile_addressVOMap().get(ApplicationManagerTypes.COMPANY).getCompany_name());

                asynchronousMailService.sendMerchantSignup(MailEventEnum.BANK_APPLICATION_URL_NOTIFICATION, applySendMailtoBank);

                //Todo Mail send msg
                rdSuccess.forward(request,response);

                logger.error("Mail Out");
                return;
            }
            else if(actionVO.isDelete())
            {
                consolidatedAppStatus= ConsolidatedAppStatus.getEnum(request.getParameter(actionVO.getActionCriteria().split("_")[0] + "_status"));
                isDeleted = applicationManager.deleteConsolidated_application(actionVO.getActionCriteria().split("_")[0]);
                if (isDeleted){
                    request.setAttribute("message",SendMailToBank_consolidated_errormsg);
                }
                saved = applicationManager.updateConsolidatedAppHistory(consolidatedAppStatus, actionVO.getActionCriteria().split("_")[0]);
            }
            else /*if(functions.isValueNull(request.getParameter("action")) && request.getParameter("action").contains("_Download"))*/
            {
                String fileName = request.getParameter("fileName");
                String filePath = APPLICATION_ZIP_PATH +request.getParameter("memberid")+ "/" +fileName;

                applicationManager.sendFile(filePath, fileName, response);
                return;
            }
            if(isUpdated)
                rdSuccess=request.getRequestDispatcher("/net/ManageBankApp?consolidatedId="+actionVO.getActionCriteria().split("_")[0]+"&MES=SUCCESS&ctoken=" + user.getCSRFToken());
            else
            {
                request.setAttribute("actionVO",actionVO);
                request.setAttribute("catchError",SendMailToBank_kindly_errormsg);
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
        catch (PZDBViolationException e)
        {
            logger.error("Db violation exception while getting Consolidated App Status", e);
            PZExceptionHandler.handleDBCVEException(e, null, null);
            request.setAttribute("catchError",SendMailToBank_status_errormsg);
            rdError.forward(request, response);
            return;
        }
        catch (Exception e)
        {
            logger.error("Exception---" + e);
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


