package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.manager.AppFileManager;
import com.manager.ApplicationManager;
import com.vo.applicationManagerVOs.AppFileDetailsVO;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.utils.AppFileHandlingUtil;
import com.utils.FtpFileHandlingUtil;
import com.vo.applicationManagerVOs.ApplicationManagerVO;
import com.vo.applicationManagerVOs.BankApplicationMasterVO;
import com.vo.applicationManagerVOs.ConsolidatedApplicationVO;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * Created by Sagar on 30/06/2015.
 */
public class GeneratedPartnerConsolidatedApplication extends HttpServlet
{
    ResourceBundle applicationResourceBundle= LoadProperties.getProperty("com.directi.pg.documentServer");
    public String APPLICATION_ZIP_PATH = applicationResourceBundle.getString("APPLICATION_ZIP_PATH");
    public String APPLICATION_DOCUMENT_PATH = applicationResourceBundle.getString("APPLICATION_DOCUMENT_PATH");
    public String BANK_APPLICATION_PATH = applicationResourceBundle.getString("BANK_APPLICATION_PATH");

    private static Logger logger= new Logger(GeneratedPartnerConsolidatedApplication.class.getName());
    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        HttpSession session = request.getSession();

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        ApplicationManager applicationManager = new ApplicationManager();
        BankApplicationMasterVO bankApplicationMasterVO = null;
        ConsolidatedApplicationVO consolidatedApplicationVO =null;

        ValidationErrorList validationErrorList= new ValidationErrorList();
        ApplicationManagerVO applicationManagerVO=null;
        Functions functions=new Functions();
        Map<String,AppFileDetailsVO> fileDetailsVOMap=null;
        AppFileManager fileManager = new AppFileManager();
        Map<String,BankApplicationMasterVO> bankApplicationMasterVOMap = null;
        Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap = null;

        AppFileHandlingUtil fileHandlingUtil = new AppFileHandlingUtil();
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/net/PartnerConsolidatedApplication?MES=Success&ctoken="+user.getCSRFToken());
        RequestDispatcher rdError = request.getRequestDispatcher("/net/PartnerConsolidatedApplication?MES=ERR&ctoken="+user.getCSRFToken());

        try
        {
            logger.debug("ACTION---"+request.getParameter("action"));
            if("Generate ZIP".equals(request.getParameter("action")))
            {
                //System.out.println("ACTION generate");
                String[] bankapplicationIds = request.getParameterValues("bankapplicationId");
                logger.debug("bankapplicationIds......"+request.getParameter("bankapplicationId"));
                if(!functions.isValueNull(request.getParameter("bankapplicationId")))
                {
                    validationErrorList.addError("Please select one bank application form to be consolidated",new ValidationException("Please select one bank application form to be consolidated","Please select one bank application form to be consolidated"));
                    request.setAttribute("errorC",validationErrorList);
                    rdError.forward(request,response);
                    return;
                }
                //System.out.println("ACTION generate consolidated");
                fileDetailsVOMap = applicationManager.generateConsolidatedBankApplication(bankapplicationIds, request.getParameter("memberid"), request);
                request.setAttribute("consolidatedFileDetailsVO", fileDetailsVOMap);
            }
            else if(functions.isValueNull(request.getParameter("action3")) && request.getParameter("action3").contains("|ViewKYC"))
            {
                logger.debug("viewkyc action===="+request.getParameter("action3"));
                /*//TODO KYC code from the view of maf.
                fileHandlingUtil = fileManager.getFileHandlingUtilAccordingToTheProperty();
                FileDetailsVO fileDetailsVO=applicationManager.getSingleApplicationUploadedFileDetail(request.getParameter("action").split("_")[0]);
                File file = null;
                file = fileManager.getKYCDetails(fileDetailsVO,fileHandlingUtil);
                fileHandlingUtil.openAllTypeOfFile(file, response, ContentTypeEnum.getEnum(fileDetailsVO.getFileType().toUpperCase()));
                if(fileHandlingUtil instanceof FtpFileHandlingUtil)
                    fileManager.deleteFileAccordingToFTPProperty(file,null,(FtpFileHandlingUtil)fileHandlingUtil);
                return;*/

                /*String mappingid[] = request.getParameter("action").split("\\|");
                String fileName = mappingid[2];*/
                String fileName = request.getParameter("fileName");
                logger.debug("KYC file Name===="+fileName);
                String filePath = APPLICATION_DOCUMENT_PATH +request.getParameter("memberid")+ "/" +fileName;

                applicationManager.sendFile(filePath, fileName, response);
                return;
            }
            else if(functions.isValueNull(request.getParameter("action2")) && request.getParameter("action2").contains("|Download"))
            {
                logger.debug("download action===="+request.getParameter("action2"));
                /*//TODO TEST whether new tab gets closed.
                fileHandlingUtil=fileManager.getFileHandlingUtilAccordingToTheProperty();
                ConsolidatedApplicationVO downloadConsolidatedVO=new ConsolidatedApplicationVO();
                consolidatedApplicationVO=new ConsolidatedApplicationVO();
                consolidatedApplicationVO.setConsolidated_id(request.getParameter("action").split("_")[0]);
                consolidatedApplicationVOMap=applicationManager.getconsolidated_application(consolidatedApplicationVO);

                for(Map.Entry<String,ConsolidatedApplicationVO> consolidatedApplicationVOEntry:consolidatedApplicationVOMap.entrySet())
                {
                    downloadConsolidatedVO=consolidatedApplicationVOEntry.getValue();
                }
                File file=null;
                file=fileManager.getConsolidatedZipFile(downloadConsolidatedVO,fileHandlingUtil);
                fileHandlingUtil.sendFile(file,response);
                if(fileHandlingUtil instanceof FtpFileHandlingUtil)
                    fileManager.deleteFileAccordingToFTPProperty(file,null,(FtpFileHandlingUtil)fileHandlingUtil);
                return;*/

                /*String mappingid[] = request.getParameter("action").split("\\|");
                String fileName = mappingid[2];*/
                String fileName = request.getParameter("fileName");
                logger.debug("download file Name===="+fileName);
                String filePath = APPLICATION_ZIP_PATH +request.getParameter("memberid")+ "/" +fileName;

                applicationManager.sendFile(filePath, fileName, response);
                return;
            }
            else if(!functions.isValueNull(request.getParameter("action1")) || (functions.isValueNull(request.getParameter("action1")) && request.getParameter("action1").contains("|View")))
            {
                logger.debug("view action===="+request.getParameter("action1"));
                /*fileHandlingUtil = fileManager.getFileHandlingUtilAccordingToTheProperty();
                bankApplicationMasterVO=new BankApplicationMasterVO();
                bankApplicationMasterVO.setBankapplicationid(request.getParameter("action").split("_")[0]);
                bankApplicationMasterVOMap=applicationManager.getBankApplicationMasterVO(bankApplicationMasterVO);
                File file=null;
                file=fileManager.getBankGenerateTemplate(bankApplicationMasterVOMap.get(bankApplicationMasterVO.getBankapplicationid()),fileHandlingUtil);
                fileHandlingUtil.openPdfFile(file, response);
                if(fileHandlingUtil instanceof FtpFileHandlingUtil)
                    fileManager.deleteFileAccordingToFTPProperty(file,null,(FtpFileHandlingUtil)fileHandlingUtil);
                return;*/

                /*String mappingid[] = request.getParameter("action").split("\\|");
                String fileName = mappingid[2];*/
                String fileName = request.getParameter("fileName");
                logger.debug("View file Name===="+fileName);
                String filePath = BANK_APPLICATION_PATH +request.getParameter("memberid")+ "/" +fileName;

                applicationManager.sendFile(filePath, fileName, response);
                return;
            }
            rdSuccess.forward(request, response);

        }
        catch (PZTechnicalViolationException e)
        {
            validationErrorList.addError("Please Consolidate Application After Some time",new ValidationException("Please Consolidate Application After Some time","Please Consolidate Application After Some time"));
            request.setAttribute("errorC",validationErrorList);
            rdError.forward(request,response);
            return;
        }
        catch (Exception e)
        {
            logger.error("Main class exception::", e);
        }
        finally
        {
            try
            {
                if(fileHandlingUtil instanceof FtpFileHandlingUtil)
                    fileManager.closeFtpConnection(null,true,(FtpFileHandlingUtil)fileHandlingUtil);
            }
            catch (PZTechnicalViolationException e)
            {
                logger.error("PZTechnicalViolationException While closing ftp connection",e);
            }
        }
    }

}

