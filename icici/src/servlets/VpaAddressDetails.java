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
 * Created by Admin on 12-07-2021.
 */
public class VpaAddressDetails extends HttpServlet
{
    private static Logger log= new Logger(VpaAddressDetails.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            log.debug("Admin is logout");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        RequestDispatcher rd= request.getRequestDispatcher("/uploadVpaAddressDetails.jsp?ctoken="+user.getCSRFToken());

        StringBuilder sErrorMessage = new StringBuilder();
        StringBuffer sSuccessMessage = new StringBuffer();
        FileUploadBean fub = new FileUploadBean();
        String action="";
        String fullFileLocation  = null;
        CommonBulkPayOut commonBulkPayOut   = new CommonBulkPayOut();

        try
        {
            if (request.getParameter("perfromAction") != null)
            {
                action = request.getParameter("perfromAction");
            }

            if (action.equalsIgnoreCase("download"))
            {
                String exportpath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
                String filename = "DetailsVPA.xls";
                FileOutputStream fileout = new FileOutputStream(exportpath + "/" + filename);
                downloadExcelFileFormat2(fileout);
                sendFile(exportpath + "/" + filename, filename, response);
                return;
            }
            String path = null;
            path = ApplicationProperties.getProperty("MPR_FILE_STORE");
            fub.setSavePath(path);

            try
            {
                fub.doUpload(request, null);
            }
            catch (SystemError systemError)
            {
                log.error("SystemError se:: ", systemError);
                request.setAttribute("errorMsg", "Please provide valid filename or filename should not contain space");
                request.setAttribute("vpaAddress", fub.getFieldValue("vpaAddress"));
                request.setAttribute("reason", fub.getFieldValue("reason"));
                request.setAttribute("File", fub.getFilename());
                rd.forward(request, response);
            }

            String File = fub.getFilename();

            request.setAttribute("vpaAddress", fub.getFieldValue("vpaAddress"));
            request.setAttribute("reason", fub.getFieldValue("reason"));
            request.setAttribute("File", File);

            AuditTrailVO auditTrailVO = new AuditTrailVO();
            auditTrailVO.setActionExecutorId("0");
            auditTrailVO.setActionExecutorName("Admin" + "-" + session.getAttribute("username").toString());


            if (sErrorMessage.length() > 0)
            {
                request.setAttribute("errorMsg", sErrorMessage);
                FileHandlingUtil fileHandlingUtil = new FileHandlingUtil();
                fileHandlingUtil.deleteFile(File);
                rd.forward(request, response);
            }

            try
            {
                VpaAddressDetails vpadetail = new VpaAddressDetails();
                String fullfilelocation = ApplicationProperties.getProperty("MPR_FILE_STORE") + File;
                String actionExecutorId = (String) session.getAttribute("merchantid");
                String role = "Admin";
                String username = (String) session.getAttribute("username");
                String actionExecutorName = role + "-" + username;
                sSuccessMessage = vpadetail.processBlockedVpaAddress(fullfilelocation, actionExecutorId, actionExecutorName);

                request.setAttribute("error", sSuccessMessage);
                request.setAttribute("errorMsg", sErrorMessage);
            }
            catch (Exception e)
            {
                request.setAttribute("error", e.getMessage());
            }
            StringBuilder message = new StringBuilder();
            message.append(sSuccessMessage.toString());
            message.append("<BR/>");
            message.append(sErrorMessage.toString());
            request.setAttribute("errorMsg", sErrorMessage.toString());
            request.setAttribute("error", message.toString());
            rd.forward(request, response);

        }
        catch (Exception ex)
        {
            log.error("Exception while VPADetails upload:: "+ex);
            boolean isFileDeleted= commonBulkPayOut.deleteErrorExcelFile(fullFileLocation);
            log.error("VPA BULK uploaded:: "+isFileDeleted);
            request.setAttribute("ERROR","Excel Not Uploaded");
            rd.forward(request,response);
            return;
        }

    }

    void downloadExcelFileFormat2(FileOutputStream fileout)
    {
        HSSFWorkbook workbook= new HSSFWorkbook();
        HSSFSheet sheet=  workbook.createSheet("vpadetail");
        HSSFRow rowhead=  sheet.createRow(0);

        try
        {
            rowhead.createCell((short)0).setCellValue("VPAAddress");
            rowhead.createCell((short)1).setCellValue("Reason");

            workbook.write(fileout);
            fileout.close();
        }
        catch (Exception e)
        {
            log.debug("VPAAddress bulk download exception..." +e);
        }
    }

    public static boolean sendFile(String filepath, String filename, HttpServletResponse response)throws Exception
    {
        System.out.println("FILE PATH:: "+filepath);
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
        File file = new File(filepath);
        file.delete();
        log.info("####### File Deleted Successfully #######");
        return true;

    }

    public StringBuffer processBlockedVpaAddress(String completefilepath,String actionExecutorId, String actionExecutorName)throws SystemError
    {
        PZFileVO pzFileVO= new PZFileVO();
        pzFileVO.setFilepath(completefilepath);
        Functions functions= new Functions();
        String smessage="";

        log.error("-----Before readingBlockedVpaAddress File----" + pzFileVO);
        FileHandlingUtil fileHandlingUtil= new FileHandlingUtil();
        List<BlacklistVO> vpatransactions= new ArrayList<>();
        vpatransactions = fileHandlingUtil.readBlockedVPAFile(pzFileVO);

        log.error("--------After readingBlockedVpaAddress File------" +vpatransactions.size());
        StringBuffer resultString = new StringBuffer();
        BlacklistManager blacklistManager =new BlacklistManager();

        int i=0;
        for(BlacklistVO vpa: vpatransactions)
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
                    count = blacklistManager.insertBlockedVpaAddress(vpa.getVpaAddress(), vpa.getBlacklistReason(), actionExecutorId, actionExecutorName);
                }
                catch (PZDBViolationException e)
                {
                    log.error("Exception:::", e);
                }
            }
                i = i + count;
            try
            {

                if ( (!ESAPI.validator().isValidInput(vpa.getVpaAddress(), vpa.getVpaAddress(), "VPAAddress", 50, false)))
                {
                     if (functions.isValueNull(vpa.getVpaAddress()) )
                    {
                        log.error("Inside VPA address..");
                    }
                }
                if ((!ESAPI.validator().isValidInput(vpa.getBlacklistReason(), vpa.getBlacklistReason(), "Description", 100, false)))
                {
                    if (functions.isValueNull(vpa.getBlacklistReason()) )
                    {
                         log.error("Inside Blacklist Reason..");
                    }
                }
                if (!functions.isValueNull(vpa.getVpaAddress()))
                {
                    log.error("Inside blank vpa address");
                }
                else
                {
                    log.error("INSIDE else ");
                    if (count!= 1 && (ESAPI.validator().isValidInput(vpa.getVpaAddress(), vpa.getVpaAddress(), "VPAAddress", 50, false)) && (ESAPI.validator().isValidInput(vpa.getBlacklistReason(), vpa.getBlacklistReason(), "Description", 100, false)))
                    {
                        smessage= vpa.getVpaAddress() + " is already blocked";
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

        resultString.append(i + " Records created successfully.");
        return resultString;
    }
}
