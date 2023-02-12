package servlets;
import com.directi.pg.*;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import com.manager.BlacklistManager;
import com.manager.TransactionManager;
import com.manager.dao.BlacklistDAO;
import com.manager.dao.MerchantDAO;
import com.manager.utils.FileHandlingUtil;
import com.manager.vo.BlacklistVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.PaginationVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.emexpay.vo.request;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZFileVO;
import com.payment.response.PZChargebackRecord;
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
import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Admin on 12-07-2021.
 */
public class UploadBankAccount extends HttpServlet
{
    private static Logger log= new Logger(UploadBankAccount.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");
        String action = "";
        String fullFileLocation                     = null;

        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        RequestDispatcher rd= request.getRequestDispatcher("/uploadbankaccount.jsp?ctoken="+user.getCSRFToken());

        StringBuilder sErrorMessage = new StringBuilder();
        StringBuffer sSuccessMessage = new StringBuffer();
        FileUploadBean fub = new FileUploadBean();
        Functions functions = new Functions();
        CommonBulkPayOut commonBulkPayOut   = new CommonBulkPayOut();

        try
        {
            if(request.getParameter("perfromAction") != null){
                action           = request.getParameter("perfromAction");
            }

            if (action.equalsIgnoreCase("download"))
            {
                System.out.println("download");
                String exportPath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
                String fileName = "UploadBlockPhoneExcel.xls";
                FileOutputStream fileOut = new FileOutputStream(exportPath + "/" + fileName);
                downloadExcelFileFormat2(fileOut);
                sendFile(exportPath + "/" + fileName, fileName, response);
                System.out.println("fileOut"+fileOut);
            }

            String path= null;
            path= ApplicationProperties.getProperty("MPR_FILE_STORE");
            fub.setSavePath(path);

            try
            {
                fub.doUpload(request,null);
            }
            catch (SystemError systemError)
            {
                log.error("SystemError se:: ", systemError);
                request.setAttribute("errorMsg","Please provide valid filename or filename should not contain space");
                request.setAttribute("bankAccountNo",fub.getFieldValue("bankAccountNo"));
                request.setAttribute("reason", fub.getFieldValue("reason"));
                request.setAttribute("File",fub.getFilename());
                rd.forward(request,response);
            }

            String File= fub.getFilename();
            String bankAccountNo= fub.getFieldValue("bankAccountNo");
            String reason= fub.getFieldValue("reason");

            request.setAttribute("bankAccountNo",fub.getFieldValue("bankAccountNo"));
            request.setAttribute("reason",fub.getFieldValue("reason"));
            request.setAttribute("File", File);

            AuditTrailVO auditTrailVO= new AuditTrailVO();
            auditTrailVO.setActionExecutorId("0");
            auditTrailVO.setActionExecutorName("Admin" + "-" + session.getAttribute("username").toString());


            if (sErrorMessage.length()>0)
            {
                request.setAttribute("errorMsg",sErrorMessage);
                FileHandlingUtil fileHandlingUtil= new FileHandlingUtil();
                fileHandlingUtil.deleteFile(File);
                rd.forward(request,response);
            }

            try
            {
                UploadBankAccount bankAccounts= new UploadBankAccount();
                String fullfilelocation= ApplicationProperties.getProperty("MPR_FILE_STORE") + File;
                String actionExecutorId=(String)session.getAttribute("merchantid");
                String role="Admin";
                String username=(String)session.getAttribute("username");
                String actionExecutorName=role+"-"+username;
                sSuccessMessage= bankAccounts.processBlockedUploadBankAccount(fullfilelocation, actionExecutorId, actionExecutorName);

                request.setAttribute("error",sSuccessMessage);
                request.setAttribute("errorMsg",sErrorMessage);
            }
            catch (Exception e)
            {
                request.setAttribute("error", e.getMessage());
            }
            StringBuilder message= new StringBuilder();
            message.append(sSuccessMessage.toString());
            message.append("<BR/>");
            message.append(sErrorMessage.toString());
            request.setAttribute("errorMsg",sErrorMessage.toString());
            request.setAttribute("error",message.toString());
            rd.forward(request,response);
        }
        catch (Exception e)
        {
            log.error("BulkPayOutUpload Exception ---" + e);
            boolean isFileDeleted = commonBulkPayOut.deleteErrorExcelFile(fullFileLocation);
            log.debug("BulkPayOutUpload File Delete 2 --->" + isFileDeleted);
            request.setAttribute("ERROR", "Excel Not Uploaded");
            rd.forward(request, response);
            return;
        }
    }
    void downloadExcelFileFormat2(FileOutputStream fileOut){
        HSSFWorkbook workbook       = new HSSFWorkbook();
        HSSFSheet sheet             = workbook.createSheet("UploadBlockPhoneExcel");
        HSSFRow rowhead             = sheet.createRow(0);
        try
        {

            rowhead.createCell((short)0).setCellValue("BankAccountNumber");
            rowhead.createCell((short)1).setCellValue("Reason");

            workbook.write(fileOut);
            fileOut.close();

        }
        catch (Exception e)
        {
            log.debug("BulkPayOutUpload Exception ---" + e);
        }
    }

    public StringBuffer processBlockedUploadBankAccount(String completefilepath,String actionExecutorId, String actionExecutorName)throws SystemError
    {
        PZFileVO pzFileVO= new PZFileVO();
        pzFileVO.setFilepath(completefilepath);
        Functions functions= new Functions();

        log.error("-----Before readingBlockedBankAccount----" + pzFileVO);
        FileHandlingUtil fileHandlingUtil= new FileHandlingUtil();
        List<BlacklistVO> readBlockedBankAccount= new ArrayList<>();
        readBlockedBankAccount = fileHandlingUtil.readBlockedBankAccount(pzFileVO);
        String smessage="";

        log.error("--------After readingBlockedBankAccount File------" +readBlockedBankAccount.size());
        StringBuffer resultString = new StringBuffer();
        BlacklistManager blacklistManager =new BlacklistManager();
        int i=0;
        for(BlacklistVO vpa: readBlockedBankAccount)
        {
            int count = 0;
            if (functions.isValueNull(vpa.getError()))
            {
                resultString.append(vpa.getError());
            }
            else
            {
                try
                {
                    count = blacklistManager.insertBlockedBankAccNo(vpa.getBlacklistBankAccountNo(), vpa.getBlacklistReason(), actionExecutorId, actionExecutorName);
                }
                catch (PZDBViolationException e)
                {
                    log.error("Exception:::", e);
                }
            }
            i = i + count;
            try
            {

                if ( (!ESAPI.validator().isValidInput(vpa.getBlacklistBankAccountNo(), vpa.getBlacklistBankAccountNo(), "Numbers", 30, false)))
                {
                    if (functions.isValueNull(vpa.getBlacklistBankAccountNo()))
                    {
                        log.error("Inside Bank Account Number..");
                    }
                }
                if ((!ESAPI.validator().isValidInput(vpa.getBlacklistReason(), vpa.getBlacklistReason(), "Description", 100, false)))
                {
                    if (functions.isValueNull(vpa.getBlacklistReason()) )
                    {
                        log.error("Inside Blacklist Reason..");
                    }
                }
                else
                {
                    log.error("INSIDE else ");
                    if (count!= 1 && (ESAPI.validator().isValidInput(vpa.getBlacklistBankAccountNo(), vpa.getBlacklistBankAccountNo(), "Numbers", 30, false)) && (ESAPI.validator().isValidInput(vpa.getBlacklistReason(), vpa.getBlacklistReason(), "Description", 100, false)))
                    {
                        smessage= "Bank Account Number" +vpa.getBlacklistBankAccountNo() + " is already blocked";
                        resultString.append(smessage + "<BR/>");
                    }
                }
            }
            catch (Exception e)
            {
                log.error("Exception:: ",e);
                if(functions.isValueNull(e.getMessage()))
                {
                    vpa.setError(e.getMessage() + "<BR/>");
                }
            }
        }

        resultString.append(i+" Records created successfully.");
        return resultString;
    }
    public static boolean sendFile(String filepath, String filename, HttpServletResponse response) throws Exception
    {

        File f          = new File(filepath);
        int length      = 0;

        // Set browser download related headers
        response.setContentType("application/ms-excel");
        response.setContentLength((int) f.length());
        response.addHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        javax.servlet.ServletOutputStream op = response.getOutputStream();

        byte[] bbuf         = new byte[1024];
        DataInputStream in  = new DataInputStream(new FileInputStream(f));

        while ((in != null) && ((length = in.read(bbuf)) != -1))
        {
            op.write(bbuf, 0, length);
        }
        log.info("####### File Create Successfully #######");
        in.close();
        op.flush();
        op.close();

        // file must be deleted after transfer...
        // caution: select to download only files which are temporarily created zip files
        // do not call this servlets with any other files which may be required later on.
        File file = new File(filepath);
        file.delete();
        log.info("####### File Deleted Successfully #######");
        return true;

    }
}
