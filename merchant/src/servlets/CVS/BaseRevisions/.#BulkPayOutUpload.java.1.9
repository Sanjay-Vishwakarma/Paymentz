import com.directi.pg.*;
import com.directi.pg.fileupload.FileUploadBean;
import com.logicboxes.util.ApplicationProperties;
import com.manager.dao.PayoutDAO;
import com.payment.request.PZPayoutRequest;
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
import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Pramod on 20/01/2021.
 */
public class BulkPayOutUpload extends HttpServlet
{
    private static Logger log = new Logger(BulkPayOutUpload.class.getName());
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doPost(request, response);
    }
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        HttpSession session = request.getSession();
        User user           = (User) session.getAttribute("ESAPIUserSessionKey");

        Merchants merchants = new Merchants();

        if (!merchants.isLoggedIn(session))
        {   log.debug("member is logout ");
            response.sendRedirect("/merchant/Logout.jsp");
            return;
        }
        String memberId         = session.getAttribute("merchantid").toString();
        RequestDispatcher rd    = request.getRequestDispatcher("/bulkPayoutUpload.jsp?ctoken=" + user.getCSRFToken());
        String filePath         = ApplicationProperties.getProperty("BULKPAYOUT_FILE_STORE");
        String logPath          = ApplicationProperties.getProperty("LOG_STORE");
        String userName         = null;
        FileUploadBean fub                  = new FileUploadBean();
        Functions functions                 = new Functions();
        CommonBulkPayOut commonBulkPayOut   = new CommonBulkPayOut();
        PayoutDAO payoutDAO                 = new PayoutDAO();
        List<PZPayoutRequest> pZPayoutRequestList   = null;
        String action                               = "";
        Hashtable returnHashtable                   = null;
        String fullFileLocation                     = null;

        try
        {

            if(request.getParameter("perfromAction") != null){
                action           = request.getParameter("perfromAction");
            }

            if(action.equals("search")){
                pZPayoutRequestList      = payoutDAO.getPendingPayoutByMerchantId(memberId);

                if(pZPayoutRequestList != null && pZPayoutRequestList.size() > 0){
                    request.setAttribute("pZPayoutRequestList",pZPayoutRequestList);
                    request.setAttribute("search","search");
                }else{
                    request.setAttribute("ERROR", "No Record Found");
                }
                rd.forward(request,response);
                return;
            }

            if(action.equalsIgnoreCase("download")){
                String exportPath           = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
                String fileName             = "BulkUploadFormatExcel.xls";
                FileOutputStream fileOut    = new FileOutputStream(exportPath + "/" + fileName);
                downloadExcelFileFormat2(fileOut);
                sendFile(exportPath + "/" + fileName, fileName, response);
                return;
            }

            log.debug("BulkPayOut Upload Started---");
            fub.setSavePath(filePath);
            fub.setLogpath(logPath);

            AuditTrailVO auditTrailVO = new AuditTrailVO();
            auditTrailVO.setActionExecutorName("Merchant" + "-" + session.getAttribute("username").toString());
            auditTrailVO.setActionExecutorId("1");

            try
            {
                log.debug("Excel Uploaded Start");
                fub.doUpload(request, userName);
                log.debug("Excel Uploaded End");
            }
            catch (SystemError sys)
            {
                request.setAttribute("ERROR", sys.getMessage());
                rd.forward(request,response);
                return;
            }

            String filename    = fub.getFilename();
            String gateway     = fub.getFieldValue("gateway");
            String result      = "";

            try
            {
                log.debug("Excel Read Started");
                fullFileLocation      = ApplicationProperties.getProperty("BULKPAYOUT_FILE_STORE") + filename;
                log.debug("fullFileLocation ---->"+fullFileLocation);
                returnHashtable       = commonBulkPayOut.saveBulkPayOut(fullFileLocation, gateway, auditTrailVO,memberId);

                if(returnHashtable.get("ERROR") != null){
                    result     = (String) returnHashtable.get("ERROR");
                }

                if(!result.isEmpty() && result != null){
                    pZPayoutRequestList = (List<PZPayoutRequest>)returnHashtable.get("pzPayoutRequestList");


                    if(pZPayoutRequestList != null){
                        String batchCount   = (String) returnHashtable.get("batchCount")+"";
                        String batchAmount  = (String) returnHashtable.get("batchAmount")+"";

                        if(!functions.isValueNull(batchCount)){
                            batchCount = "0";
                        }
                        if(!functions.isValueNull(batchAmount)){
                            batchAmount = "0.00";
                        }
                        String totalCount   = pZPayoutRequestList.size()+"";

                        StringBuffer resultTable = new StringBuffer();
                        resultTable.append(getTableHeader(false));
                        resultTable.append(setTableData(batchCount, batchAmount, totalCount, "0.00" + "",false));

                        request.setAttribute("tableResult",resultTable.toString());
                    }

                    request.setAttribute("ERROR",result.toString());

                }else{
                    result              = (String) returnHashtable.get("SUCCESS");
                    log.debug("SUCCESS result ---->"+result);

                    String batchCount   = (String) returnHashtable.get("batchCount")+"";
                    String batchAmount  = (String) returnHashtable.get("batchAmount")+"";

                    pZPayoutRequestList  = payoutDAO.getPendingPayoutByMerchantId(memberId);

                    Double totalAmount   =  pZPayoutRequestList.stream().mapToDouble(x -> Double.parseDouble(x.getPayoutAmount())).sum();

                    if(!functions.isValueNull(batchCount)){
                        batchCount = "0";
                    }
                    if(!functions.isValueNull(batchAmount)){
                        batchAmount = "0.00";
                    }

                    String totalCount           = pZPayoutRequestList.size()+"";
                    StringBuffer resultTable    = new StringBuffer();
                    resultTable.append(getTableHeader(true));
                    resultTable.append(setTableData(batchCount, batchAmount, totalCount, totalAmount + "",true));

                    log.debug("resultTable  "+resultTable);
                    request.setAttribute("Message", result);
                    request.setAttribute("tableResult",resultTable.toString());
                }

                log.debug("Excel Read And Save Successfully ");

            }
            catch (Exception e)
            {
                log.debug("BulkPayOutUpload Exception ---" + e);
                boolean isFileDeleted = commonBulkPayOut.deleteErrorExcelFile(fullFileLocation);
                log.debug("BulkPayOutUpload File Delete 1 --->" + isFileDeleted);
                request.setAttribute("ERROR", e.getMessage());
                rd.forward(request,response);
                return;
            }

            if(pZPayoutRequestList != null && pZPayoutRequestList.size() > 0){
                request.setAttribute("pZPayoutRequestList",pZPayoutRequestList);
            }
            log.debug("BulkPayOutUpload End---");
            rd = request.getRequestDispatcher("/bulkPayoutUpload.jsp?ctoken="+user.getCSRFToken());
            rd.forward(request,response);

            return;
        }catch (Exception e){
            log.error("BulkPayOutUpload Exception ---" + e);
            boolean isFileDeleted = commonBulkPayOut.deleteErrorExcelFile(fullFileLocation);
            log.debug("BulkPayOutUpload File Delete 2 --->" + isFileDeleted);
            request.setAttribute("ERROR","Excel Not Uploaded");
            rd.forward(request,response);
            return;
        }

    }

    void downloadExcelFileFormat2(FileOutputStream fileOut){
        HSSFWorkbook workbook       = new HSSFWorkbook();
        HSSFSheet sheet             = workbook.createSheet("bulkUploadExcel");
        HSSFRow rowhead             = sheet.createRow(0);
        try
        {

            rowhead.createCell((short)0).setCellValue("BankAccountNo");
            rowhead.createCell((short)1).setCellValue("CustomerBankAccountName");
            rowhead.createCell((short)2).setCellValue("BankIfsc");
            rowhead.createCell((short)3).setCellValue("PayoutAmount");
            rowhead.createCell((short)4).setCellValue("BankTransferType");
            rowhead.createCell((short)5).setCellValue("orderId");
            //rowhead.createCell((short)5).setCellValue("TerminalId");

            workbook.write(fileOut);
            workbook.write(fileOut);
            workbook.write(fileOut);
            workbook.write(fileOut);
            fileOut.close();

        }
        catch (Exception e)
        {
            log.debug("BulkPayOutUpload Exception ---" + e);
        }
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

    public StringBuffer getTableHeader(boolean showColumn)
    {
        StringBuffer sHeader = new StringBuffer();

        sHeader.append("<table align=\"center\" cellspacing=\"1\"  border=\"1\" >");
        sHeader.append("<TR>");
        sHeader.append("<TD valign=\"middle\" align=\"center\" /*bgcolor=\"#0f8c93\"*/>");
        sHeader.append("<b><p align=\"center\"><font /*color=\"#FFFFFF\"*/ family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Current Batch Count</font></p></b>");
        sHeader.append("</TD>");

        sHeader.append("<TD valign=\"middle\" align=\"center\" /*bgcolor=\"#0f8c93\"*/>");
        sHeader.append("<b><p align=\"center\"><font /*color=\"#FFFFFF\"*/ family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Current Batch Amount</font></p></b>");
        sHeader.append("</TD>");

        if(showColumn){
            sHeader.append("<TD valign=\"middle\" align=\"center\" /*bgcolor=\"#0f8c93\"*/>");
            sHeader.append("<b><p align=\"center\"><font /*color=\"#FFFFFF\"*/ family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Total Payout Count</font></p></b>");
            sHeader.append("</TD>");

            sHeader.append("<TD valign=\"middle\" align=\"center\" /*bgcolor=\"#0f8c93\"*/>");
            sHeader.append("<b><p align=\"center\"><font /*color=\"#FFFFFF\"*/ family=\"Open Sans Helvetica Neue Helvetica,Arial,sans-serif\" size=\"2\">Total Payout Amount</font></p></b>");
            sHeader.append("</TD>");
        }

        sHeader.append("</TR>");
        return sHeader;
    }

    public StringBuffer setTableData(String batchCount, String batchAmount,String totalCount,String totalAmount,boolean showColumn)
    {
        StringBuffer nTransaction = new StringBuffer();

        nTransaction.append("<TR>");

        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" /*color=\"#001963\"*/ font-family=\"sans-serif\" font-size=\"2px\">"+batchCount+"</p>");
        nTransaction.append("</TD>");

        nTransaction.append("<TD>");
        nTransaction.append("<p align=\"center\" bgcolor=\"\" /*color=\"#001963\"*/ font-family=\"sans-serif\" font-size=\"2px\">"+batchAmount+"</p>");
        nTransaction.append("</TD>");

        if(showColumn){
            nTransaction.append("<TD>");
            nTransaction.append("<p align=\"center\" bgcolor=\"\" /*color=\"#001963\"*/ font-family=\"sans-serif\" font-size=\"2px\">"+totalCount+"</p>");
            nTransaction.append("</TD>");

            nTransaction.append("<TD>");
            nTransaction.append("<p align=\"center\" bgcolor=\"\" /*color=\"#001963\"*/ font-family=\"sans-serif\" font-size=\"2px\">"+totalAmount+"</p>");
            nTransaction.append("</TD>");
        }

        nTransaction.append("</TR>");
        nTransaction.append("</TABLE>");

        return nTransaction;
    }
}
