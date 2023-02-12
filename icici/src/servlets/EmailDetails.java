package servlets;
import com.directi.pg.*;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import com.manager.BlacklistManager;
import com.manager.utils.FileHandlingUtil;
import com.manager.vo.BlacklistVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZFileVO;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 16-07-2021.
 */
public class EmailDetails extends HttpServlet
{

    private static Logger logger= new Logger(EmailDetails.class.getName());
    public void doGet(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
        doPost(req,res);
    }
    public void doPost(HttpServletRequest req, HttpServletResponse res)throws ServletException, IOException
    {
        HttpSession session= req.getSession();
        User user= (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            logger.error("Admin is logout");
            res.sendRedirect("/icici/logout.jsp");
            return;
        }
        RequestDispatcher rd= req.getRequestDispatcher("/uploadEmails.jsp?ctoken="+user.getCSRFToken());

        StringBuilder sErrormessage= new StringBuilder();
        StringBuffer sSuccessMessage= new StringBuffer();
        FileUploadBean fub= new FileUploadBean();
        String action="";
        String fullFileLocation= null;
        CommonBulkPayOut commonBulkPayOut= new CommonBulkPayOut();

        try
        {
            if (req.getParameter("perfromAction")!= null)
            {
                action = req.getParameter("perfromAction");
            }
            if (action.equalsIgnoreCase("download"))
            {
                String exportpath= ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
                String filename= "MAIL.xls";
                FileOutputStream fileout = new FileOutputStream(exportpath +"/"+ filename);
                downloadExcelFileFormat2(fileout);
                sendFile(exportpath + "/" + filename,filename,res);
                return;
            }
             String path= ApplicationProperties.getProperty("MPR_FILE_STORE");
             fub.setSavePath(path);

            try
            {
                fub.doUpload(req,null);
            }
            catch (SystemError systemError)
            {
                 logger.error("SystemError::: ",systemError);
                 req.setAttribute("errorMsg","Please provide valid filename or filename not contain space");
                 req.setAttribute("emailAddress",fub.getFieldValue("emailAddress"));
                 req.setAttribute("File",fub.getFilename());
                 rd.forward(req,res);
            }

            String File= fub.getFilename();

            req.setAttribute("File",File);
            req.setAttribute("emailAddress",fub.getFieldValue("emailAddress"));

            AuditTrailVO auditTrailVO= new AuditTrailVO();
            auditTrailVO.setActionExecutorId("0");
            auditTrailVO.setActionExecutorName("Admin"+ "-" + session.getAttribute("username").toString());

            if (sErrormessage.length()>0)
            {
                 req.setAttribute("errorMsg",sErrormessage);
                 FileHandlingUtil fileHandlingUtil= new FileHandlingUtil();
                 fileHandlingUtil.deleteFile(File);
                 rd.forward(req,res);
            }

            try
            {
                 EmailDetails emailDetails= new EmailDetails();
                 String fullfilelocation= ApplicationProperties.getProperty("MPR_FILE_STORE")+ File;
                 String actionExecutorId= (String) session.getAttribute("merchantid");
                 String role= "Admin";
                 String username= (String)session.getAttribute("username");
                 String actionExecutorName= role+"-"+username;
                 sSuccessMessage= emailDetails.processBlockedEmail(fullfilelocation,actionExecutorId,actionExecutorName);

                 req.setAttribute("error",sSuccessMessage);
                 req.setAttribute("errorMsg",sErrormessage);
            }
            catch (Exception e)
            {
                req.setAttribute("error",e.getMessage());
            }
            StringBuilder message= new StringBuilder();
            message.append(sSuccessMessage.toString());
            message.append("<BR/>");
            message.append(sErrormessage.toString());
            req.setAttribute("errorMsg",sErrormessage.toString());
            req.setAttribute("error",message.toString());
            rd.forward(req,res);
        }
        catch (Exception e)
        {
            logger.error("Exception while upload bulk for Email data.. "+e);
            boolean isFileDeleted= commonBulkPayOut.deleteErrorExcelFile(fullFileLocation);
            logger.error("EMAIL BULK UPLOADED: "+isFileDeleted);
            req.setAttribute("ERROR","Excel Not Uploaded");
            rd.forward(req,res);
            return;
        }
    }

    void downloadExcelFileFormat2(FileOutputStream fileout)
    {
        HSSFWorkbook workbook= new HSSFWorkbook();
        HSSFSheet sheet= workbook.createSheet("emaildetail");
        HSSFRow rowhead= sheet.createRow(0);

        try
        {
            rowhead.createCell((short)0).setCellValue("EmailAddress");
            workbook.write(fileout);
            fileout.close();
        }
        catch (Exception e)
        {
            logger.debug("Exception while download Email details format..." + e.getMessage());
        }
    }

    public static boolean sendFile(String filepath, String filename, HttpServletResponse response)throws Exception
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
        logger.info("####### File Create Successfully #######");
        in.close();
        op.flush();
        op.close();

        // file must be deleted after transfer...
        // caution: select to download only files which are temporarily created zip files
        // do not call this servlets with any other files which may be required later on.
        File file = new File(filepath);
        file.delete();
        logger.info("####### File Deleted Successfully #######");
        return true;
    }

    public StringBuffer processBlockedEmail(String completefilepath, String actionExecutorId, String actionExecutorName) throws SystemError
    {
        PZFileVO pzFileVO= new PZFileVO();
        pzFileVO.setFilepath(completefilepath);
        Functions functions = new Functions();

        logger.error("------Before reading BlockedEmail File-----" +pzFileVO);
        FileHandlingUtil fileHandlingUtil= new FileHandlingUtil();
        List<BlacklistVO> emaillist= new ArrayList<>();
        emaillist= fileHandlingUtil.readBlockedEmail(pzFileVO);
        logger.error("-----After reading BlockedEmail FIle----- "+emaillist);

        StringBuffer resultstring= new StringBuffer();
        BlacklistManager blacklistManager= new BlacklistManager();
        String smessage="";

        int i=0;
        for (BlacklistVO emailrecords: emaillist)
        {
            int count=0;
            if (functions.isValueNull(emailrecords.getError()))
            {
                resultstring.append(emailrecords.getError());
            }
            else
            {
                try
                {
                    count= blacklistManager.insertBlockedEmailAddress(emailrecords.getEmailAddress(),emailrecords.getBlacklistReason(),emailrecords.getRemark(),actionExecutorId,actionExecutorName);
                }
                catch (PZDBViolationException e)
                {
                   logger.error("Exception::: " + e.getMessage());
                }
            }
            i= i+ count;
            try
            {

                if (!ESAPI.validator().isValidInput(emailrecords.getEmailAddress(), emailrecords.getEmailAddress(), "Email", 100, false))
                {
                    if (functions.isValueNull(emailrecords.getEmailAddress()))
                    {
                        logger.error("Inside Email address...");
                    }
                }
                else
                {
                    logger.error("Inside else..");
                    if (count!=1)
                    {
                        smessage = emailrecords.getEmailAddress()+ " is already blocked";
                        resultstring.append(smessage+ "<BR/>");
                    }
                }
            }
            catch (Exception e)
            {
                logger.error("Exception...",e);
                if (functions.isValueNull(e.getMessage()))
                {
                    emailrecords.setError(e.getMessage()+ "<BR/>");
                }
            }
        }
        resultstring.append(i + " Records created successfully.");
        return resultstring;
    }
}
