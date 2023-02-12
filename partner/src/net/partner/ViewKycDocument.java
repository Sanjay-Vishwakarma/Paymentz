package net.partner;

import com.directi.pg.Functions;
import com.directi.pg.LoadProperties;
import com.directi.pg.Logger;
import com.manager.AppFileManager;
import com.manager.AppRequestManager;
import com.manager.ApplicationManager;
import com.utils.AppFileHandlingUtil;
import com.vo.applicationManagerVOs.NavigationVO;
import com.vo.applicationManagerVOs.ApplicationManagerVO;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ResourceBundle;

/**
 * Created by Niket on 22/09/2015.
 */
public class ViewKycDocument extends HttpServlet
{
    private static Logger logger = new Logger(ViewKycDocument.class.getName());
    private Functions functions = new Functions();
    ResourceBundle applicationResourceBundle = LoadProperties.getProperty("com.directi.pg.documentServer");
    public String APPLICATION_DOCUMENT_PATH = applicationResourceBundle.getString("APPLICATION_DOCUMENT_PATH");

    public void doGet(HttpServletRequest request,HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request,HttpServletResponse response) throws IOException,ServletException
    {
        //instance created for Vo
        ApplicationManagerVO applicationManagerVO=new ApplicationManagerVO();
        NavigationVO navigationVO = new NavigationVO();
        //instance of manager
        AppRequestManager appRequestManager = new AppRequestManager();
        ApplicationManager applicationManager = new ApplicationManager();
        AppFileManager fileManager = new AppFileManager();
        //Handling Util
        AppFileHandlingUtil fileHandlingUtil = new AppFileHandlingUtil();
        HttpSession session = request.getSession();

        PartnerFunctions partner=new PartnerFunctions();
        if (!partner.isLoggedInPartner(session))
        {
            response.sendRedirect("/partner/Logout.jsp");
            return;
        }
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        RequestDispatcher rdSuccess = request.getRequestDispatcher("/appManagerView.jsp?MES=Success&ctoken="+user.getCSRFToken());
        RequestDispatcher rdError = request.getRequestDispatcher("/appManagerView.jsp?MES=ERR&ctoken="+user.getCSRFToken());
        try
        {
            if(functions.isValueNull(request.getParameter("action")) && request.getParameter("action").contains("|View"))
            {
                /*applicationManagerVO=appRequestManager.getApplicationManagerVO(session);
                if(applicationManagerVO.getFileDetailsVOs()!=null && applicationManagerVO.getFileDetailsVOs().containsKey(request.getParameter("action").split("_")[0]))
                {
                    List<FileDetailsVO> files = applicationManagerVO.getFileDetailsVOs().get(request.getParameter("action").split("_")[0]);
                    if(files.size()>0)
                    {
                        for(int i=0;i<files.size();i++)
                        {
                            FileDetailsVO fileDetailsVO = files.get(i);
                            if(fileDetailsVO.getMappingId().equals(request.getParameter("action").split("_")[2]))
                            {
                                fileHandlingUtil = fileManager.getFileHandlingUtilAccordingToTheProperty();
                                File file = null;
                                file = fileManager.getKYCDetails(fileDetailsVO, fileHandlingUtil);
                                logger.debug("File type:::" + ContentTypeEnum.getEnum(fileDetailsVO.getFileType().toUpperCase()));
                                fileHandlingUtil.openAllTypeOfFile(file, response, ContentTypeEnum.getEnum(fileDetailsVO.getFileType().toUpperCase()));
                                fileManager.deleteFileAccordingToFTPProperty(file, null, (FtpFileHandlingUtil) fileHandlingUtil);
                            }
                        }
                    }
                }*/
                String mappingId[] = request.getParameter("action").split("\\|");
                String fileName = mappingId[3];
                String filePath = APPLICATION_DOCUMENT_PATH + request.getParameter("memberid") + "/" + fileName;

                applicationManager.sendFile(filePath, fileName, response);
                return;
            }
        }
        catch (Exception e)
        {
            logger.error("Exception---" + e);
            logger.error("Exception while viewing KYC");
        }
        /*finally
        {
            try
            {
                fileManager.closeFtpConnection(null,true,(FtpFileHandlingUtil)fileHandlingUtil);
            }
            catch (PZTechnicalViolationException e)
            {
                logger.error("PZTechnicalViolation Exception while closing FTP connection");
            }
        }*/
    }
}
