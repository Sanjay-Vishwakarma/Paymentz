import com.directi.pg.*;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import com.manager.WhiteListManager;
import com.manager.dao.MerchantDAO;
import com.manager.utils.FileHandlingUtil;
import com.manager.vo.MerchantDetailsVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 9/4/13
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadCardFile extends HttpServlet
{
    private static Logger log = new Logger(UploadCardFile.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");
        if (!Admin.isLoggedIn(session))
        {   log.debug("Admin is logout ");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        RequestDispatcher rd = req.getRequestDispatcher("/uploadwhitelistcarddetails.jsp?ctoken="+user.getCSRFToken());

        StringBuilder sErrorMessage = new StringBuilder();
        StringBuilder sSuccessMessage = new StringBuilder();

        MerchantDAO merchantDAO=new MerchantDAO();
        UploadWhitelistedCardDetails uploadWhitelistedCardDetails = new UploadWhitelistedCardDetails();
        Functions function=new Functions();
        FileUploadBean fub = new FileUploadBean();
        WhiteListManager whiteListManager=new WhiteListManager();

        if("getdata".equals(req.getParameter("action")) && function.isValueNull(req.getParameter("toid")))
        {
            try
            {
                String whitelistlevel=whiteListManager.getWhitelistingLevel(req.getParameter("toid"));
                if(!function.isValueNull(whitelistlevel)){
                    if(function.isValueNull(req.getParameter("toid"))){
                        boolean isPresent=merchantDAO.isMemberExist(req.getParameter("toid"));
                        if(!isPresent){
                            log.debug("Merchant does not exist in the system");
                            sErrorMessage.append("Merchant does not exist in the system");
                        }
                    }
                }
                req.setAttribute("whitelistlevel",whitelistlevel);
                req.setAttribute("sErrorMessage", sErrorMessage.toString());
                rd.forward(req, res);
                return;
            }
            catch (Exception e)
            {
               log.error("Catch Exception...",e);
            }
        }else
        {
            fub.setSavePath(ApplicationProperties.getProperty("MPR_FILE_STORE"));
            try
            {
                fub.doUpload(req, null);
            }
            catch (SystemError sys)
            {
                log.error("SystemError:::::", sys);
                req.setAttribute("sErrorMessage", "Please provide valid file or file name should not contains space.");
                rd.forward(req, res);
                return;
            }

            String EOL = "<br>";
            String filePath = ApplicationProperties.getProperty("MPR_FILE_STORE") + fub.getFilename();
            String whiteListingLevel = fub.getFieldValue("whitelistlevel");
            if (!ESAPI.validator().isValidInput("toid", fub.getFieldValue("toid"), "Numbers", 50, false))
            {
                sErrorMessage.append("Invalid MemberId / MemberId should not be empty" + EOL);
            }
            if (function.isValueNull(whiteListingLevel))
            {
                if (whiteListingLevel.equals("Account") && !ESAPI.validator().isValidInput("accountid", fub.getFieldValue("accountid"), "Numbers", 50, false))
                {
                    sErrorMessage.append("Invalid AccountId / AccountId should not be empty" + EOL);
                }
            }
            /*if(function.isValueNull(fub.getFieldValue("toid"))){
                boolean isPresent=merchantDAO.isMemberExist(fub.getFieldValue("toid"));
                if(!isPresent){
                    log.debug("Merchant does not exist in the system");
                    sErrorMessage.append("Merchant does not exist in the system" + EOL);
                }
            }*/


            if (sErrorMessage.length() > 0)
            {
                req.setAttribute("sErrorMessage", sErrorMessage);
                FileHandlingUtil fileHandlingUtil = new FileHandlingUtil();
                //fileHandlingUtil.deleteFile(filePath);
                rd.forward(req, res);
                return;
            }

            try
            {
                FileHandlingUtil fileHandlingUtil = new FileHandlingUtil();
                List<String> queryBatch = fileHandlingUtil.readCardDetailsNewForGroup(filePath, fub.getFieldValue("accountid"), fub.getFieldValue("toid"));
                //fileHandlingUtil.deleteFile(filePath);
                if (queryBatch.size() == 0)
                {
                    req.setAttribute("sErrorMessage", "No Records found in file");
                    rd.forward(req, res);
                }
                try
                {
                    MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(fub.getFieldValue("toid"));
                    if (function.isValueNull(merchantDetailsVO.getMemberId()))
                    {
                        sSuccessMessage = uploadWhitelistedCardDetails.processSettlement(fub.getFieldValue("toid"), fub.getFieldValue("accountid"), sSuccessMessage, sErrorMessage, queryBatch);
                        req.setAttribute("sSuccessMessage", sSuccessMessage);
                        rd.forward(req, res);
                    }
                    else
                    {
                        StringBuilder response = new StringBuilder("Invalid MemberId");
                        req.setAttribute("sErrorMessage", response.toString());
                        rd.forward(req, res);
                    }
                }
                catch (Exception e)
                {
                    req.setAttribute("sErrorMessage", e.getMessage());
                    rd.forward(req, res);
                }
            }
            catch (SystemError sys)
            {
                log.error("SystemError:::::", sys);
                req.setAttribute("sErrorMessage", "Please provide valid file");
                rd.forward(req, res);
            }
        }
    }
}
