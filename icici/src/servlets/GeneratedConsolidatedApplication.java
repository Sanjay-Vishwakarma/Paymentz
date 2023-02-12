import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.manager.ApplicationManager;
import com.manager.AppFileManager;
import com.utils.AppFileHandlingUtil;
import com.utils.FtpFileHandlingUtil;
import com.vo.applicationManagerVOs.AppFileDetailsVO;
import com.vo.applicationManagerVOs.BankApplicationMasterVO;
import com.vo.applicationManagerVOs.ConsolidatedApplicationVO;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import org.owasp.esapi.User;
import org.owasp.esapi.ValidationErrorList;
import org.owasp.esapi.errors.ValidationException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;


/**
 * Created by Sagar on 30/06/2015.
 */
public class GeneratedConsolidatedApplication extends HttpServlet
{

    private static Logger logger= new Logger(GeneratedConsolidatedApplication.class.getName());
    ResourceBundle applicationResourceBundle= LoadProperties.getProperty("com.directi.pg.documentServer");
    public String BANK_APPLICATION_PATH = applicationResourceBundle.getString("BANK_APPLICATION_PATH");
    public String APPLICATION_DOCUMENT_PATH = applicationResourceBundle.getString("APPLICATION_DOCUMENT_PATH");
    public String APPLICATION_ZIP_PATH = applicationResourceBundle.getString("APPLICATION_ZIP_PATH");

    protected void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {

        HttpSession session = request.getSession();

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        ApplicationManager applicationManager = new ApplicationManager();
        BankApplicationMasterVO bankApplicationMasterVO = null;
        ConsolidatedApplicationVO consolidatedApplicationVO =null;

        ValidationErrorList validationErrorList= new ValidationErrorList();
        Functions functions=new Functions();
        Map<String,AppFileDetailsVO> fileDetailsVOMap=null;
        AppFileManager fileManager = new AppFileManager();
        Map<String,BankApplicationMasterVO> bankApplicationMasterVOMap = null;
        Map<String,ConsolidatedApplicationVO> consolidatedApplicationVOMap = null;

        AppFileHandlingUtil fileHandlingUtil = new AppFileHandlingUtil();

        //RequestDispatcher rdError = request.getRequestDispatcher("/consolidatedapplication.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        RequestDispatcher rdSuccess = request.getRequestDispatcher("/servlet/ConsolidatedApplication?MES=Success&ctoken="+user.getCSRFToken());
        RequestDispatcher rdError = request.getRequestDispatcher("/servlet/ConsolidatedApplication?MES=ERR&ctoken="+user.getCSRFToken());

        try
        {
            if("Generate ZIP".equals(request.getParameter("action")))
            {
                String[] bankapplicationIds = request.getParameterValues("bankapplicationId");
                logger.debug("bankapplicationIds..."+request.getParameterValues("bankapplicationId"));
                if(!functions.isValueNull(request.getParameter("bankapplicationId")))
                {
                    validationErrorList.addError("Please select one bank application form to be consolidated",new ValidationException("Please select one bank application form to be consolidated","Please select one bank application form to be consolidated"));
                    request.setAttribute("errorC",validationErrorList);
                    rdError.forward(request,response);
                    return;
                }
                fileDetailsVOMap = applicationManager.generateConsolidatedBankApplication(bankapplicationIds, request.getParameter("memberid"), request);

                request.setAttribute("consolidatedFileDetailsVO", fileDetailsVOMap);

            }
            else if(functions.isValueNull(request.getParameter("action")) && request.getParameter("action").contains("|ViewKYC"))
            {
                logger.debug("viewKYC===="+request.getParameter("action"));
                //TODO KYC code from the view of maf.
                /*fileHandlingUtil = fileManager.getFileHandlingUtilAccordingToTheProperty();
                FileDetailsVO fileDetailsVO=applicationManager.getSingleApplicationUploadedFileDetail(request.getParameter("action").split("_")[0]);
                File file = null;
                file = fileManager.getKYCDetails(fileDetailsVO,fileHandlingUtil);
                fileHandlingUtil.openAllTypeOfFile(file, response, ContentTypeEnum.getEnum(fileDetailsVO.getFileType().toUpperCase()));
                if(fileHandlingUtil instanceof FtpFileHandlingUtil)
                fileManager.deleteFileAccordingToFTPProperty(file,null,(FtpFileHandlingUtil)fileHandlingUtil);
                return;*/

                /*String mappingId[] = request.getParameter("action").split("\\|");
                String fileName = mappingId[2];*/
                String fileName = request.getParameter("fileName");
                String filePath = APPLICATION_DOCUMENT_PATH +request.getParameter("memberid")+ "/" +fileName;

                applicationManager.sendFile(filePath, fileName, response);
                return;
            }
            else if(!functions.isValueNull(request.getParameter("action")) || (functions.isValueNull(request.getParameter("action")) && request.getParameter("action").contains("|View")))
            {
                logger.debug("view===="+request.getParameter("action"));
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

                /*String mappingId[] = request.getParameter("action").split("\\|");
                String fileName = mappingId[2];*/
                String fileName = request.getParameter("fileName");
                String filePath = BANK_APPLICATION_PATH +request.getParameter("memberid")+ "/" +fileName;

                applicationManager.sendFile(filePath, fileName, response);
                return;
            }
            else if(functions.isValueNull(request.getParameter("action")) && request.getParameter("action").contains("|Download"))
            {
                logger.debug("Download===="+request.getParameter("action"));
                //TODO TEST whether new tab gets closed.
                /*fileHandlingUtil=fileManager.getFileHandlingUtilAccordingToTheProperty();
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

                String fileName = request.getParameter("fileName");
                String filePath = APPLICATION_ZIP_PATH + request.getParameter("memberid") + "/" + fileName;

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

    public static boolean sendFile(String filepath, String filename, HttpServletResponse response)throws Exception
    {
        File f = new File(filepath);
        int length = 0;

        // Set browser download related headers
        response.setContentType("application/octat-stream");
        response.setContentLength((int) f.length());
        response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        javax.servlet.ServletOutputStream op = response.getOutputStream();

        byte[] bbuf = new byte[1024];
        DataInputStream in = new DataInputStream(new FileInputStream(f));

        while ((in != null) && ((length = in.read(bbuf)) != -1))
        {
            op.write(bbuf, 0, length);
        }

        in.close();
        op.flush();
        op.close();

        // file must be deleted after transfer...
        // caution: select to download only files which are temporarily created zip files
        // do not call this servlets with any other files which may be required later on.
        /*File file = new File(filepath);
        file.delete();*/
        logger.info("Successful#######");
        return true;
    }

}
