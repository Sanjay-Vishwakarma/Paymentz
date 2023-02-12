package net.partner;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.UploadWhitelistedCardDetails;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import com.manager.dao.MerchantDAO;
import com.manager.dao.PartnerDAO;
import com.manager.utils.FileHandlingUtil;
import com.manager.vo.MerchantDetailsVO;
import com.payment.MultiplePartnerUtill;
import com.payment.exceptionHandler.PZDBViolationException;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Admin on 7/14/2018.
 */
public class UploadBinDetails extends HttpServlet
{

    private static Logger log = new Logger(UploadBinDetails.class.getName());
    PartnerDAO partnerDAO = new PartnerDAO();
    MultiplePartnerUtill multiplePartnerUtill = new MultiplePartnerUtill();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException
    {
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        FileHandlingUtil fileHandlingUtil = new FileHandlingUtil();
        PartnerFunctions partner = new PartnerFunctions();
        StringBuilder success = new StringBuilder();
        StringBuilder failed = new StringBuilder();
        String status = "";
        RequestDispatcher rd = req.getRequestDispatcher("/uploadBins.jsp?ctoken=" + user.getCSRFToken());
        if (!partner.isLoggedInPartner(session))
        {
            res.sendRedirect("/partner/Logout.jsp");
            return;
        }
        String memberId = "";
        String accountId = "";
        StringBuffer errormsg1 = new StringBuffer();
        String EOL = "<br>";

        String filePath = "";

        FileUploadBean fub = new FileUploadBean();
        Functions function = new Functions();
        fub.setSavePath(ApplicationProperties.getProperty("MPR_FILE_STORE"));
        try
        {
            try
            {
                fub.doUpload(req,null);
            }
            catch(SystemError sys){
                log.error("SystemError:::::",sys);
                req.setAttribute("error","Your file already exists in the System. Please Upload new File.");
                rd.forward(req, res);
                return;
            }
            memberId = fub.getFieldValue("memberid");
            accountId = fub.getFieldValue("accountid");
            String pid = fub.getFieldValue("pid");
            filePath = ApplicationProperties.getProperty("MPR_FILE_STORE") + fub.getFilename();
            String partnerid = session.getAttribute("partnerId").toString();
            if (!ESAPI.validator().isValidInput("memberid", fub.getFieldValue("memberid"), "Numbers", 10, false))
            {
                errormsg1.append("Invalid MemberId / MemberId should not be empty" + EOL);
                req.setAttribute("error", errormsg1.toString());
                fileHandlingUtil.deleteFile(filePath);
                rd.forward(req, res);
                return;
            }
            if (!ESAPI.validator().isValidInput("pid", pid, "Numbers", 10, true))
            {
                errormsg1.append("Invalid PartnerId" + EOL);
                req.setAttribute("error", errormsg1.toString());
                fileHandlingUtil.deleteFile(filePath);
                rd.forward(req, res);
                return;
            }
           /* if (!ESAPI.validator().isValidInput("accountid", fub.getFieldValue("accountid"), "Numbers", 10, false))
            {
                errormsg1.append("Invalid Account ID / Account ID should not be empty" + EOL);
                req.setAttribute("error", errormsg1.toString());
                fileHandlingUtil.deleteFile(filePath);
                rd.forward(req, res);
                return;
            }*/
            try
            {
                if (function.isValueNull(pid) && !partner.isPartnerMemberMapped(memberId, pid))
                {
                    errormsg1.append("Partner ID/Member ID NOT mapped." + EOL);
                    req.setAttribute("error", errormsg1.toString());
                    fileHandlingUtil.deleteFile(filePath);
                    rd.forward(req, res);
                    return;
                }
                else if (!function.isValueNull(pid) && !partner.isPartnerSuperpartnerMembersMapped(memberId, partnerid))
                {
                    errormsg1.append("Partner ID/Member ID NOT mapped." + EOL);
                    req.setAttribute("error", errormsg1.toString());
                    fileHandlingUtil.deleteFile(filePath);
                    rd.forward(req, res);
                    return;
                }
            }catch(Exception e){
                log.error("Exception---" + e);
            }
            if(function.isValueNull(memberId) && function.isValueNull(accountId))
            {
                boolean check=partnerDAO.checkMemberAccountConfiguration(memberId,accountId);
                if(!check){
                    errormsg1.append("Invalid Member Account Configuration.");
                    req.setAttribute("error", errormsg1.toString());
                    fileHandlingUtil.deleteFile(filePath);
                    rd.forward(req, res);
                    return;
                }
            }

            List<String> queryBatch = fileHandlingUtil.readCardDetailsNewForGroup(filePath, fub.getFieldValue("accountid"), fub.getFieldValue("memberid"));
            //fileHandlingUtil.deleteFile(filePath);
            if (queryBatch.size() == 0)
            {
                req.setAttribute("cbmessage", "No Records found in file");
                rd.forward(req, res);
                return;
            }
            try
            {
                UploadWhitelistedCardDetails uploadWhitelistedCardDetails = new UploadWhitelistedCardDetails();
                MerchantDAO merchantDAO=new MerchantDAO();
                MerchantDetailsVO merchantDetailsVO = merchantDAO.getMemberDetails(fub.getFieldValue("memberid"));
                if (function.isValueNull(merchantDetailsVO.getMemberId()))
                {
                    success = uploadWhitelistedCardDetails.processSettlement(fub.getFieldValue("memberid"), fub.getFieldValue("accountid"), success, failed, queryBatch);
                    req.setAttribute("cbmessage", success.toString());
                    rd.forward(req, res);
                    return;
                }
                else
                {
                    StringBuilder response = new StringBuilder("Invalid MemberId");
                    req.setAttribute("cbmessage", response.toString());
                    rd.forward(req, res);
                    return;
                }
            }
            catch (Exception e)
            {
               log.error("Exception::::", e);
            }
        }
        catch (SystemError sys)
        {
            log.error("SystemError:::::", sys);
        }
        status = "something went wrong.Please check your support team";
        req.setAttribute("cbmessage", status);
        rd.forward(req, res);
        return;
    }
}
