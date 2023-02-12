package servlets;

import com.directi.pg.*;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import com.manager.utils.FileHandlingUtil;
import com.payment.request.PZFileVO;
import com.payment.response.PZChargebackRecord;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


public class UploadRefundFile extends HttpServlet
{
    private static Logger log = new Logger(UploadRefundFile.class.getName());
    private String userName;
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
        RequestDispatcher rd = req.getRequestDispatcher("/bulkRefundUpload.jsp?ctoken="+user.getCSRFToken());

        StringBuilder sErrorMessage = new StringBuilder();
        StringBuffer sSuccessMessage = new StringBuffer();
        userName = (String)session.getAttribute("username");
        log.error("userName ===== " + userName);
        Functions functions=new Functions();
        String path = null;
        path = ApplicationProperties.getProperty("MPR_FILE_STORE");

        FileUploadBean fub = new FileUploadBean();
        fub.setSavePath(path);

        try
        {
            fub.doUpload(req,null);
        }
        catch(SystemError sys){
            log.error("SystemError:::::",sys);
            req.setAttribute("errorMsg", "Please provide valid file or file name should not contains space");
            req.setAttribute("type",fub.getFieldValue("type"));
            req.setAttribute("gateway",fub.getFieldValue("gateway"));
            req.setAttribute("File",fub.getFilename());
            rd.forward(req, res);
        }
        String File=fub.getFilename();


        req.setAttribute("type", fub.getFieldValue("type"));
        req.setAttribute("gateway", fub.getFieldValue("gateway"));
        req.setAttribute("File",File);

        AuditTrailVO auditTrailVO = new AuditTrailVO();
        auditTrailVO.setActionExecutorName("Admin" + "-" + session.getAttribute("username").toString()+"(Bulk Refund Uploaded)");
        auditTrailVO.setActionExecutorId("0");

        if(sErrorMessage.length()>0)
        {
            req.setAttribute("errorMsg",sErrorMessage);
            FileHandlingUtil fileHandlingUtil=new FileHandlingUtil();
            fileHandlingUtil.deleteFile(File);
            rd.forward(req, res);
        }
        try
        {
            String fullFileLocation = ApplicationProperties.getProperty("MPR_FILE_STORE") + File;
            sSuccessMessage = processRefundWithoutGateway(fullFileLocation);

            req.setAttribute("error",sSuccessMessage);
            req.setAttribute("errorMsg",sErrorMessage);
        }
        catch (Exception e){
            req.setAttribute("error", e.getMessage());
        }
        StringBuilder refundMessage = new StringBuilder();
        refundMessage.append(sSuccessMessage.toString());
        refundMessage.append("<BR/>");
        refundMessage.append(sErrorMessage.toString());
        req.setAttribute("errorMsg", sErrorMessage.toString());
        req.setAttribute("error", refundMessage.toString());
        rd.forward(req, res);
    }

    private StringBuffer processRefundWithoutGateway(String completeFileName) throws SystemError
    {
        PZFileVO pzFileVO = new PZFileVO();
        pzFileVO.setFilepath(completeFileName);

        List<PZChargebackRecord> vTransactions = readChargebackFile(pzFileVO);
        log.error("-----After readRefundFile()----"+vTransactions.size());

        StringBuffer resultString = new StringBuffer();

        if(vTransactions.size() > 30)
            return resultString.append("Maximum 30 transactions are allowed at a time");

        if(vTransactions != null && vTransactions.size() > 0)
        {
            UploadRefundList uploadRefundList = new UploadRefundList();
            resultString = uploadRefundList.refund(vTransactions, userName);
        }
        else
        {
            resultString.append("No Transactions Found");
        }
        return resultString;
    }

    private List<PZChargebackRecord> readChargebackFile(PZFileVO fileName) throws SystemError
    {
        List<PZChargebackRecord> pzChargebackRecordList = new ArrayList();
        try
        {
            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(fileName.getFilepath()));
            HSSFSheet sheet = workbook.getSheetAt(0);
            Iterator rows = sheet.rowIterator();
            int cntRowToLeaveFromTop = 0;
            boolean isFileProceeding = false;
            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                if(cntRowToLeaveFromTop == 1)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if(!isFileProceeding)
            {
                throw new SystemError("Error : Invalid File format/Records not found");
            }

            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                PZChargebackRecord loadTransactions = new PZChargebackRecord();
                try
                {
                    loadTransactions.setTrackingid((long)row.getCell((short) 0).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setTrackingid(row.getCell((short) 0).getStringCellValue());
                }
                catch (NullPointerException ne)
                {
                    loadTransactions.setTrackingid("");
                }
                pzChargebackRecordList.add(loadTransactions);
            }
        }
        catch (Exception e)
        {
            log.error("IOException:::::", e);
        }

        return pzChargebackRecordList;
    }
}