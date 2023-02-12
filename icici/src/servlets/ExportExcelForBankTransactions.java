package servlets;

import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.logicboxes.util.ApplicationProperties;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.dao.PartnerDAO;
import com.manager.dao.TransactionDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TerminalVO;
import com.manager.vo.TransactionVO;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.owasp.esapi.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Ajit.k on 19/07/2019.
 */
public class ExportExcelForBankTransactions extends HttpServlet
{

    private static Logger logger = new Logger(ExportExcelForBankTransactions.class.getName());

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doService(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doService(request, response);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {
            logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        Functions functions = new Functions();
        TransactionVO transactionVO = null;
        TransactionManager transactionManager = new TransactionManager();
        String startDate = request.getParameter("startDate");
        String startTime = request.getParameter("startTime");
        String endDate = request.getParameter("endDate");
        String endTime = request.getParameter("endTime");
        String gateway = request.getParameter("gateway");
        String merchantid = request.getParameter("memberId");
        String accoutId = request.getParameter("accountId");
        String partnerId = request.getParameter("partnerName");
        String terminalId = request.getParameter("terminalId");
        String gatewayName = "";
        String currency = "";
        Set<String> gatewaySet = (Set) session.getAttribute("gatewaySet");
        Hashtable statusHash = (Hashtable) session.getAttribute("statushash");
        RequestDispatcher rd = request.getRequestDispatcher("/bankTransactionReport.jsp?ctoken=" + user.getCSRFToken());


        if (!"0".equals(gateway) && functions.isValueNull(gateway))
        {
            String arrGateway[] = gateway.split("-");
            gatewayName = arrGateway[0];
            currency = arrGateway[1];
        }

        StringBuffer msg = new StringBuffer();
        String fileName = "";
        String filePath = "";
        String initDateFormat = "dd/MM/yyyy HH:mm:ss";
        String targetDateFormat = "yyyy-MM-dd HH:mm:ss";

        try
        {
            startDate = formatDate(startDate + " " + startTime, initDateFormat, targetDateFormat);
            endDate = formatDate(endDate + " " + endTime, initDateFormat, targetDateFormat);

            transactionVO = new TransactionVO();
            Map<String, List<TransactionVO>> transactionVOMap = null;
            List<TransactionVO> accountWiseDetails=null;
            Map<String, List<TransactionVO>> transactionVORefundMap = null;
            MerchantDAO merchantDAO=new MerchantDAO();

            List<List<TransactionVO>> salesReportList=new ArrayList<>();
            String partnerName=null;
            if(functions.isValueNull(partnerId)){
                PartnerDAO partnerDAO=new PartnerDAO();
                partnerName=partnerDAO.getPartnerName(partnerId);
            }

            transactionVO.setStartDate(startDate);
            transactionVO.setEndDate(endDate);
            transactionVO.setMemberId(merchantid);
            transactionVO.setAccountId(accoutId);
            transactionVO.setToType(partnerName);
            transactionVO.setGatewayName(gatewayName);
            transactionVO.setCurrency(currency);
            transactionVO.setTerminalId(terminalId);
            Date d=new Date();
            logger.error("getBankTransactionStatus Start Time :::"+d.getTime());
            transactionVOMap = transactionManager.getBankTransactionStatus(transactionVO, gatewaySet);
            HashMap<String,TransactionVO> getListOfAccountId=transactionManager.getAccountIdListFromTransaction(gatewayName,currency,startDate,endDate);
            accountWiseDetails = transactionManager.getAccountWiseTransactionDetails(transactionVO);
            System.out.println("accountWiseDetails::::"+accountWiseDetails);
            logger.error("getBankTransactionStatus End Time :::"+new Date().getTime());
            logger.error("getBankTransactionStatus End Time :::" + (new Date().getTime() - d.getTime()));
            d=new Date();
            logger.error("getBankTransactionRefundStatus Start Time :::"+d.getTime());
            transactionVORefundMap = transactionManager.getBankTransactionRefundStatus(transactionVO, gatewaySet);
            logger.error("getBankTransactionRefundStatus End Time :::"+new Date().getTime());
            logger.error("getBankTransactionRefundStatus End Time :::" + (new Date().getTime() - d.getTime()));
            //List<TransactionVO> getListOfMemberId=transactionManager.getMemberListFromTransaction(partnerName,gatewayName,currency,startDate,endDate);
            d=new Date();
            logger.error("getMemberListFromTransaction Start Time :::"+d.getTime());
            HashMap<String,TransactionVO> getListOfMemberId=transactionManager.getMemberListFromTransaction(merchantid,partnerName,gatewayName,currency,startDate,endDate);
            logger.error("getMemberListFromTransaction End Time :::"+new Date().getTime());
            logger.error("getMemberListFromTransaction End Time :::" + (new Date().getTime() - d.getTime()));
            if(getListOfMemberId!=null && getListOfMemberId.size()>0)
            {
                d=new Date();
                logger.error("getSalesReportMap Start Time :::"+d.getTime());
                salesReportList.add(transactionManager.getSalesReportMap(transactionVO,getListOfMemberId));
                logger.error("getSalesReportMap End Time :::"+new Date().getTime());
                logger.error("getSalesReportMap End Time :::" + (new Date().getTime() - d.getTime()));
            }


            HSSFWorkbook workbook = new HSSFWorkbook();

            HSSFCellStyle style = workbook.createCellStyle();
            style.setFillBackgroundColor(HSSFColor.GREY_40_PERCENT.index);
            style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            style.setBorderLeft((short) 1);
            style.setBorderRight((short) 1);
            style.setBorderTop((short) 1);
            style.setBorderBottom((short) 1);

            HSSFCellStyle textBoldStyle = workbook.createCellStyle();
            HSSFFont font = workbook.createFont();
            font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
            textBoldStyle.setFont(font);
            textBoldStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            textBoldStyle.setBorderLeft((short) 1);
            textBoldStyle.setBorderRight((short) 1);
            textBoldStyle.setBorderTop((short) 1);
            textBoldStyle.setBorderBottom((short) 1);
            //Excel sheet 1 for search criteria
            HSSFSheet sheet = workbook.createSheet("Summary");
            generateSummary(sheet, style, textBoldStyle, partnerName, startDate, endDate, gatewayName, currency, accoutId, merchantid);

            //Excel sheet 2 for consolidate data
            HSSFSheet sheet1 = workbook.createSheet("Consolidated Report");
            generateConsolidatedReport(sheet1, style, textBoldStyle, partnerName, startDate, endDate, transactionVOMap, transactionVORefundMap, gatewaySet, statusHash);

            if(salesReportList!=null && salesReportList.size()>0)
            {
                HSSFSheet sheet2 = workbook.createSheet("Sales Report");
                generateSalesReport(sheet2, style, textBoldStyle, partnerName, gatewayName, startDate, endDate, salesReportList);
            }

            HSSFSheet sheet3 = workbook.createSheet("Account Wise Report");
            generateAccountWiseReport(sheet3, style, textBoldStyle, partnerName, gatewayName, startDate, endDate, accountWiseDetails);

            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMdd_HHmmss");
            String currentSystemDate = dateFormater.format(new Date());
            fileName = /*partnerName + */" Transaction Report_" + currentSystemDate;
            fileName = fileName + ".xls";

            filePath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
            filePath = filePath + fileName;

            FileOutputStream fileOut = new FileOutputStream(filePath);
            workbook.write(fileOut);
            fileOut.close();

            sendFile(filePath, fileName, response);
            return;
        }
        catch (Exception e)
        {
            logger.error("Exception", e);
            request.setAttribute("errormessage", "Internal error while processing your request.");
            rd.forward(request, response);
            return;
        }
    }

    public static boolean sendFile(String filepath, String filename, HttpServletResponse response) throws Exception
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
        File file = new File(filepath);
        file.delete();
        logger.info("Successful#######");
        return true;
    }

    public String formatDate(String date, String initDateFormat, String endDateFormat) throws ParseException
    {
        Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        String parsedDate = formatter.format(initDate);
        return parsedDate;
    }

    public void generateSummary(HSSFSheet sheet, HSSFCellStyle style, HSSFCellStyle textBoldStyle, String partnerDetails, String startDate, String endDate, String gatewayName, String currency, String accoutId, String merchantid)
    {
        Functions functions = new Functions();
        sheet.setColumnWidth((short) 1, (short) 202000);
        sheet.setColumnWidth((short) 2, (short) 202000);
        sheet.setColumnWidth((short) 3, (short) 202000);
        sheet.setColumnWidth((short) 4, (short) 202000);
        sheet.setColumnWidth((short) 5, (short) 202000);
        sheet.setColumnWidth((short) 6, (short) 202000);

        HSSFRow fromDate = sheet.createRow(1);
        HSSFCell fromDateCell0 = fromDate.createCell((short) 1);
        HSSFCell fromDateCell1 = fromDate.createCell((short) 2);
        fromDateCell0.setCellStyle(textBoldStyle);
        fromDateCell0.setCellValue("From Date");
        fromDateCell1.setCellStyle(style);
        fromDateCell1.setCellValue(startDate);

        HSSFRow toDate = sheet.createRow(2);
        HSSFCell toDateCell0 = toDate.createCell((short) 1);
        HSSFCell toDateCell1 = toDate.createCell((short) 2);
        toDateCell0.setCellStyle(textBoldStyle);
        toDateCell0.setCellValue("To Date");
        toDateCell1.setCellStyle(style);
        toDateCell1.setCellValue(endDate);

       /* HSSFRow partner = sheet.createRow(3);
        HSSFCell partnerCell0 = partner.createCell((short) 1);
        HSSFCell partnerCell1 = partner.createCell((short) 2);
        partnerCell0.setCellStyle(textBoldStyle);
        partnerCell0.setCellValue("Partner ID");
        partnerCell1.setCellStyle(style);
        partnerCell1.setCellValue(*//*partnerDetails*//*"-");*/

        HSSFRow gateway1 = sheet.createRow(3);
        HSSFCell gatewayCell0 = gateway1.createCell((short) 1);
        HSSFCell gatewayCell1 = gateway1.createCell((short) 2);
        gatewayCell0.setCellStyle(textBoldStyle);
        gatewayCell0.setCellValue("Gateway Name");
        gatewayCell1.setCellStyle(style);
        if (!functions.isValueNull(gatewayName))
        {
            gatewayCell1.setCellValue("-");
        }
        else
        {
            gatewayCell1.setCellValue(gatewayName);
        }

        HSSFRow currency1 = sheet.createRow(4);
        HSSFCell currencyCell0 = currency1.createCell((short) 1);
        HSSFCell currencyCell1 = currency1.createCell((short) 2);
        currencyCell0.setCellStyle(textBoldStyle);
        currencyCell0.setCellValue("Currency");
        currencyCell1.setCellStyle(style);
        if (!functions.isValueNull(currency))
        {
            currencyCell1.setCellValue("-");
        }
        else
        {
            currencyCell1.setCellValue(currency);
        }

        HSSFRow account = sheet.createRow(5);
        HSSFCell accountCell0 = account.createCell((short) 1);
        HSSFCell accountCell1 = account.createCell((short) 2);
        accountCell0.setCellStyle(textBoldStyle);
        accountCell0.setCellValue("Account ID");
        accountCell1.setCellStyle(style);
        if (accoutId.equals("0"))
        {
            accountCell1.setCellValue("-");
        }
        else
        {
            accountCell1.setCellValue(accoutId);
        }
    }

    private void generateConsolidatedReport(HSSFSheet sheet, HSSFCellStyle style, HSSFCellStyle textBoldStyle, String partnerName, String startDate, String endDate, Map<String, List<TransactionVO>> transactionVOMap, Map<String, List<TransactionVO>> refundTransactionVOMap, Set<String> gatewaySet, Hashtable statusHash)
    {
        int rowno = 1;
        String status = "";
        long count = 0;
        double statusPercentage = 0.00;
        double refcbkPercentage = 0.00;
        double amount = 0.0;
        double captureAmount = 0;
        double refundAmount = 0.00;
        double chargebackAmount = 0.00;
        double totalPercentage = 0.00;


        if (transactionVOMap != null && !(transactionVOMap.size() == 0))
        {
            List<TransactionVO> transactionVOList = transactionVOMap.get("transactionStatus");
            if (transactionVOList != null)
            {
                long totalCount = 0;
                double totalAmount = 0.0;
                double totalCaptureAmount = 0.0;

                HSSFRow dateHeader1 = sheet.createRow(rowno);
                sheet.addMergedRegion(new Region(rowno, (short) 1, rowno, (short) 5));
                rowno++;
                HSSFRow dateHeader = sheet.createRow(rowno);
                sheet.addMergedRegion(new Region(rowno, (short) 1, rowno, (short) 5));
                rowno++;
                HSSFRow header1 = sheet.createRow(rowno);
                rowno++;

                sheet.setColumnWidth((short) 1, (short) 202000);
                sheet.setColumnWidth((short) 2, (short) 202000);
                sheet.setColumnWidth((short) 3, (short) 202000);
                sheet.setColumnWidth((short) 4, (short) 202000);
                sheet.setColumnWidth((short) 5, (short) 202000);
                sheet.setColumnWidth((short) 6, (short) 202000);

                HSSFCell dateCell1 = dateHeader1.createCell((short) 1);
                HSSFCell dateCell = dateHeader.createCell((short) 1);
                HSSFCell cell11 = header1.createCell((short) 1);
                HSSFCell cell22 = header1.createCell((short) 2);
                HSSFCell cell33 = header1.createCell((short) 3);
                HSSFCell cell44 = header1.createCell((short) 4);
                HSSFCell cell55 = header1.createCell((short) 5);

                dateCell1.setCellStyle(textBoldStyle);
                dateCell.setCellStyle(textBoldStyle);
                cell11.setCellStyle(textBoldStyle);
                cell22.setCellStyle(textBoldStyle);
                cell33.setCellStyle(textBoldStyle);
                cell44.setCellStyle(textBoldStyle);
                cell55.setCellStyle(textBoldStyle);

                //dateCell1.setCellValue("transactionStatus");
                dateCell.setCellValue(/*partnerName + */" Bank Transaction Report(Capture) Received (" + startDate + " - " + endDate + ")");
                cell11.setCellValue("Status");
                cell22.setCellValue("No of Transaction");
                cell33.setCellValue("Amount");
                cell44.setCellValue("Capture Amount");
                cell55.setCellValue("Percentage");

                for (TransactionVO transactionVO : transactionVOList)
                {
                    System.out.println("\ntransactionVO --"+transactionVO.toString());
                    HSSFRow row1 = sheet.createRow((short) rowno);

                    status = transactionVO.getStatus();
                    count = transactionVO.getCount();
                    amount = Double.valueOf(transactionVO.getAmount());
                    System.out.println("status "+status +" count"+count +" amount"+amount);
                    captureAmount = transactionVO.getCaptureAmount();
                    statusPercentage = Double.parseDouble(Functions.round(transactionVO.getCount() / Double.parseDouble(transactionVO.getTotalTransCount()) * 100, 2));

                    totalCount = totalCount + count;
                    totalAmount = totalAmount + amount;
                    totalCaptureAmount = totalCaptureAmount + captureAmount;
                    totalPercentage = totalPercentage + statusPercentage;

                    HSSFCell cellr00 = row1.createCell((short) 1);
                    HSSFCell cellr11 = row1.createCell((short) 2);
                    HSSFCell cellr22 = row1.createCell((short) 3);
                    HSSFCell cellr33 = row1.createCell((short) 4);
                    HSSFCell cellr44 = row1.createCell((short) 5);

                    cellr00.setCellStyle(style);
                    cellr11.setCellStyle(style);
                    cellr22.setCellStyle(style);
                    cellr33.setCellStyle(style);
                    cellr44.setCellStyle(style);

                    cellr00.setCellValue(status);
                    cellr11.setCellValue(count);
                    System.out.println("count::"+count);
                    cellr22.setCellValue(amount);
                    cellr33.setCellValue(captureAmount);
                    cellr44.setCellValue(statusPercentage);
                    rowno++;
                }
                HSSFRow header3 = sheet.createRow(rowno);
                HSSFCell total = header3.createCell((short) 0);
                total.setCellStyle(textBoldStyle);
                total.setCellValue("Total");
                HSSFCell status1 = header3.createCell((short) 1);
                HSSFCell totalCountCell = header3.createCell((short) 2);
                HSSFCell totalAmountCell = header3.createCell((short) 3);
                HSSFCell totalCapture = header3.createCell((short) 4);
                HSSFCell totalPer = header3.createCell((short) 5);

                status1.setCellStyle(textBoldStyle);
                totalCountCell.setCellStyle(textBoldStyle);
                totalAmountCell.setCellStyle(textBoldStyle);
                totalCapture.setCellStyle(textBoldStyle);
                totalPer.setCellStyle(textBoldStyle);
                System.out.println("total count "+totalCount+" totalAmount "+totalAmount+" totalCaptureAmount "+totalCaptureAmount);
                status1.setCellValue("-");
                totalCountCell.setCellValue(totalCount);
                totalAmountCell.setCellValue(totalAmount);
                totalCapture.setCellValue(totalCaptureAmount);
                totalPer.setCellValue(totalPercentage);
                rowno = rowno + 2;
            }
            rowno = rowno + 1;
        }
        else
        {
            HSSFRow noDataFoundRow = sheet.createRow(rowno);
            sheet.addMergedRegion(new Region(rowno, (short) 1, rowno, (short) 5));
            rowno++;
            HSSFCell noDataFoundCell = noDataFoundRow.createCell((short) 1);
            noDataFoundCell.setCellStyle(style);
            noDataFoundCell.setCellValue("No Transactions Received");
            rowno++;
        }

        if (refundTransactionVOMap != null && !(refundTransactionVOMap.size() == 0))
        {
            List<TransactionVO> transactionVORefundList = refundTransactionVOMap.get("transactionRefundStatus");
            logger.debug("transactionVORefundList====" + transactionVORefundList);
            if (transactionVORefundList != null)
            {
                HSSFRow dateHeader = sheet.createRow(rowno);
                sheet.addMergedRegion(new Region(rowno, (short) 1, rowno, (short) 6));
                rowno++;
                HSSFRow header1 = sheet.createRow(rowno);
                rowno++;

                sheet.setColumnWidth((short) 1, (short) 202000);
                sheet.setColumnWidth((short) 2, (short) 202000);
                sheet.setColumnWidth((short) 3, (short) 202000);
                sheet.setColumnWidth((short) 4, (short) 202000);
                sheet.setColumnWidth((short) 5, (short) 202000);
                sheet.setColumnWidth((short) 6, (short) 202000);

                HSSFCell dateCell = dateHeader.createCell((short) 1);
                HSSFCell cell11 = header1.createCell((short) 1);
                HSSFCell cell22 = header1.createCell((short) 2);
                HSSFCell cell33 = header1.createCell((short) 3);
                HSSFCell cell44 = header1.createCell((short) 4);
                HSSFCell cell55 = header1.createCell((short) 5);
                HSSFCell cell66 = header1.createCell((short) 6);

                dateCell.setCellStyle(textBoldStyle);
                cell11.setCellStyle(textBoldStyle);
                cell22.setCellStyle(textBoldStyle);
                cell33.setCellStyle(textBoldStyle);
                cell44.setCellStyle(textBoldStyle);
                cell55.setCellStyle(textBoldStyle);
                cell66.setCellStyle(textBoldStyle);

                dateCell.setCellValue(/*partnerName + */" Bank Transaction Report(Refund & Chargeback) Received (" + startDate + " - " + endDate + ")");
                cell11.setCellValue("Status");
                cell22.setCellValue("No of Transaction");
                cell33.setCellValue("Amount");
                cell44.setCellValue("Refund Amount");
                cell55.setCellValue("Chargeback Amount");
                cell66.setCellValue("Percentage");

                for (TransactionVO transactionVO : transactionVORefundList)
                {
                    HSSFRow row1 = sheet.createRow((short) rowno);

                    status = (String) transactionVO.getStatus();
                    count = transactionVO.getCount();
                    amount = Double.valueOf(transactionVO.getAmount());
                    refundAmount = Double.valueOf(transactionVO.getRefundAmount());
                    chargebackAmount = Double.valueOf(transactionVO.getChargebackAmount());
                    refcbkPercentage = Double.parseDouble(Functions.round(transactionVO.getCount() / Double.parseDouble(transactionVO.getTotalTransCount()) * 100, 2));
                    System.out.println("inside for chargebackAmount "+chargebackAmount);

                    HSSFCell cellr00 = row1.createCell((short) 1);
                    HSSFCell cellr11 = row1.createCell((short) 2);
                    HSSFCell cellr22 = row1.createCell((short) 3);
                    HSSFCell cellr33 = row1.createCell((short) 4);
                    HSSFCell cellr44 = row1.createCell((short) 5);
                    HSSFCell cellr55 = row1.createCell((short) 6);

                    cellr00.setCellStyle(style);
                    cellr11.setCellStyle(style);
                    cellr22.setCellStyle(style);
                    cellr33.setCellStyle(style);
                    cellr44.setCellStyle(style);
                    cellr55.setCellStyle(style);

                    cellr00.setCellValue(status);
                    cellr11.setCellValue(count);
                    cellr22.setCellValue(amount);
                    cellr33.setCellValue(refundAmount);
                    cellr44.setCellValue(chargebackAmount);
                    cellr55.setCellValue(refcbkPercentage);
                    rowno++;
                }
            }
        }
        else
        {
            HSSFRow noDataFoundRow = sheet.createRow(rowno);
            sheet.addMergedRegion(new Region(rowno, (short) 1, rowno, (short) 6));

            rowno++;
            HSSFCell noDataFoundCell = noDataFoundRow.createCell((short) 1);
            noDataFoundCell.setCellStyle(style);
            noDataFoundCell.setCellValue("No Refund and Chargeback Received");
        }
        rowno = rowno + 2;

    }

    private void generateAccountWiseReport(HSSFSheet sheet, HSSFCellStyle style, HSSFCellStyle textBoldStyle, String partnerName,String gatewayName, String startDate, String endDate, List<TransactionVO> transactionVOList)
    {
        String merchantName="";
        String gateway=gatewayName;
        String memberId="";
        String currency="";
        String cardType="";
        double totalSalesCount=0;
        double totalSalesAmount=0.00;
        double approveCount=0;
        double approveAmount=0.00;
        double approveCountRatio=0;
        double approveAmountRatio=0.00;
        double refundCount=0;
        double refundAmount=0.00;
        double refundCountRatio=0;
        double refundAmountRatio=0.00;
        double chargebackCount=0;
        double chargebackAmount=0.00;
        double chargebackCountRatio=0;
        double chargebackAmountRatio=0.00;
        /*double declineCount=0;
        double declineAmount=0.00;*/
        double declineCountRatio=0;
        double declineAmountRatio=0.00;

        double markforreversalCount=0;
        double markforreversalAmount=0.00;

        double authstartedCount=0;
        double authstartedAmount=0;
        double authstartedCountRatio=0;
        double authstartedAmountRatio=0;
        double authfailedCount=0;
        double authfailedAmount=0;
        double failedCount=0;
        double failedAmount=0.00;
        double payoutCount=0;
        double payoutAmount=0.00;
        double payoutCountRatio=0;
        double payoutAmountRatio=0.00;
        double begunCount=0;
        double begunAmount=0.00;
        System.out.println("transactionVOList:::"+transactionVOList+"---"+transactionVOList.size());
        int rowno = 0;
        int srno=1;
        if (transactionVOList != null && !(transactionVOList.size() == 0))
        {
            //sheet.addMergedRegion(new Region(rowno, (short) 1, rowno, (short) 5));
            //rowno++;
            //HSSFRow dateHeader = sheet.createRow(rowno);
            //sheet.addMergedRegion(new Region(rowno, (short) 1, rowno, (short) 5));
            //rowno++;
            HSSFRow header1 = sheet.createRow(rowno);
            rowno++;

            sheet.setColumnWidth((short) 0, (short) 202000);
            sheet.setColumnWidth((short) 1, (short) 202000);
            sheet.setColumnWidth((short) 2, (short) 202000);
            sheet.setColumnWidth((short) 3, (short) 202000);
            sheet.setColumnWidth((short) 4, (short) 202000);
            sheet.setColumnWidth((short) 5, (short) 202000);
            sheet.setColumnWidth((short) 6, (short) 202000);
            sheet.setColumnWidth((short) 7, (short) 202000);
            sheet.setColumnWidth((short) 8, (short) 202000);
            sheet.setColumnWidth((short) 9, (short) 202000);
            sheet.setColumnWidth((short) 10, (short) 202000);
            sheet.setColumnWidth((short) 11, (short) 202000);
            sheet.setColumnWidth((short) 12, (short) 202000);
            sheet.setColumnWidth((short) 13, (short) 202000);
            sheet.setColumnWidth((short) 14, (short) 202000);
            sheet.setColumnWidth((short) 15, (short) 202000);
            sheet.setColumnWidth((short) 16, (short) 202000);
            sheet.setColumnWidth((short) 17, (short) 202000);
            sheet.setColumnWidth((short) 18, (short) 202000);
            sheet.setColumnWidth((short) 19, (short) 202000);
            sheet.setColumnWidth((short) 20, (short) 202000);
            sheet.setColumnWidth((short) 21, (short) 202000);
            sheet.setColumnWidth((short) 22, (short) 202000);
            sheet.setColumnWidth((short) 23, (short) 202000);
            sheet.setColumnWidth((short) 24, (short) 202000);
            sheet.setColumnWidth((short) 25, (short) 202000);
            sheet.setColumnWidth((short) 26, (short) 202000);
            sheet.setColumnWidth((short) 27, (short) 202000);
            sheet.setColumnWidth((short) 28, (short) 202000);
            sheet.setColumnWidth((short) 29, (short) 202000);
            sheet.setColumnWidth((short) 30, (short) 202000);
            sheet.setColumnWidth((short) 31, (short) 202000);
            sheet.setColumnWidth((short) 32, (short) 202000);
            sheet.setColumnWidth((short) 33, (short) 202000);
            sheet.setColumnWidth((short) 34, (short) 202000);
            sheet.setColumnWidth((short) 35, (short) 202000);
            sheet.setColumnWidth((short) 36, (short) 202000);
            sheet.setColumnWidth((short) 37, (short) 202000);
            sheet.setColumnWidth((short) 38, (short) 202000);

            HSSFCell cell0 = header1.createCell((short) 0);
            HSSFCell cell1 = header1.createCell((short) 1);
            HSSFCell cell2 = header1.createCell((short) 2);
            HSSFCell cell3 = header1.createCell((short) 3);
            HSSFCell cell4 = header1.createCell((short) 4);
            HSSFCell cell5 = header1.createCell((short) 5);
            HSSFCell cell6 = header1.createCell((short) 6);
            HSSFCell cell7 = header1.createCell((short) 7);
            HSSFCell cell8 = header1.createCell((short) 8);
            HSSFCell cell9 = header1.createCell((short) 9);
            HSSFCell cell10 = header1.createCell((short) 10);
            HSSFCell cell11 = header1.createCell((short) 11);
            HSSFCell cell12 = header1.createCell((short) 12);
            HSSFCell cell13 = header1.createCell((short) 13);
            HSSFCell cell14 = header1.createCell((short) 14);
            HSSFCell cell15 = header1.createCell((short) 15);
            HSSFCell cell16 = header1.createCell((short) 16);
            HSSFCell cell17 = header1.createCell((short) 17);
            HSSFCell cell18 = header1.createCell((short) 18);
            HSSFCell cell19 = header1.createCell((short) 19);
            HSSFCell cell20 = header1.createCell((short) 20);
            HSSFCell cell21 = header1.createCell((short) 21);
            HSSFCell cell22 = header1.createCell((short) 22);
            HSSFCell cell23 = header1.createCell((short) 23);
            HSSFCell cell24 = header1.createCell((short) 24);
            HSSFCell cell25 = header1.createCell((short) 25);
            HSSFCell cell26 = header1.createCell((short) 26);
            HSSFCell cell27 = header1.createCell((short) 27);
            HSSFCell cell28 = header1.createCell((short) 28);
            HSSFCell cell29 = header1.createCell((short) 29);
            HSSFCell cell30 = header1.createCell((short) 30);
            HSSFCell cell31 = header1.createCell((short) 31);
            HSSFCell cell32 = header1.createCell((short) 32);
            HSSFCell cell33 = header1.createCell((short) 33);
            HSSFCell cell34 = header1.createCell((short) 34);
            HSSFCell cell35 = header1.createCell((short) 35);
            HSSFCell cell36 = header1.createCell((short) 36);
            HSSFCell cell37 = header1.createCell((short) 37);
            HSSFCell cell38 = header1.createCell((short) 38);


            //dateCell.setCellStyle(textBoldStyle);
            cell0.setCellStyle(textBoldStyle);
            cell1.setCellStyle(textBoldStyle);
            cell2.setCellStyle(textBoldStyle);
            cell3.setCellStyle(textBoldStyle);
            cell4.setCellStyle(textBoldStyle);
            cell5.setCellStyle(textBoldStyle);
            cell6.setCellStyle(textBoldStyle);
            cell7.setCellStyle(textBoldStyle);
            cell8.setCellStyle(textBoldStyle);
            cell9.setCellStyle(textBoldStyle);
            cell10.setCellStyle(textBoldStyle);
            cell11.setCellStyle(textBoldStyle);
            cell12.setCellStyle(textBoldStyle);
            cell13.setCellStyle(textBoldStyle);
            cell14.setCellStyle(textBoldStyle);
            cell15.setCellStyle(textBoldStyle);
            cell16.setCellStyle(textBoldStyle);
            cell17.setCellStyle(textBoldStyle);
            cell18.setCellStyle(textBoldStyle);
            cell19.setCellStyle(textBoldStyle);
            cell20.setCellStyle(textBoldStyle);
            cell21.setCellStyle(textBoldStyle);
            cell22.setCellStyle(textBoldStyle);
            cell23.setCellStyle(textBoldStyle);
            cell24.setCellStyle(textBoldStyle);
            cell25.setCellStyle(textBoldStyle);
            cell26.setCellStyle(textBoldStyle);
            cell27.setCellStyle(textBoldStyle);
            cell28.setCellStyle(textBoldStyle);
            cell29.setCellStyle(textBoldStyle);
            cell30.setCellStyle(textBoldStyle);
            cell31.setCellStyle(textBoldStyle);
            cell32.setCellStyle(textBoldStyle);
            cell33.setCellStyle(textBoldStyle);
            cell34.setCellStyle(textBoldStyle);
            cell35.setCellStyle(textBoldStyle);
            cell36.setCellStyle(textBoldStyle);
            cell37.setCellStyle(textBoldStyle);
            cell38.setCellStyle(textBoldStyle);


            cell0.setCellValue("Sr.No");
            cell1.setCellValue("Account Id");
            cell2.setCellValue("Merchant Id");
            cell3.setCellValue("Gateway Name");
            cell4.setCellValue("Alias Name");
            cell5.setCellValue("Display Name");
            cell6.setCellValue("3D Support");
            cell7.setCellValue("Currency");//SALES(cb,Capture Success,Settled,Reversed,payoutsuccessful,declined)
            cell8.setCellValue("Card Type");
            cell9.setCellValue("Total COUNT");
            cell10.setCellValue("Total AMOUNT");
            cell11.setCellValue("Approved Count(SALES-Capture Success,Settled,Reversed,Chargeback,Auth Successful)\n");
            cell12.setCellValue("Approved Amount(SALES-Capture Success,Settled,Reversed,Chargeback)\n");
            cell13.setCellValue("Approved Count Ratio");
            cell14.setCellValue("Approved Amount Ratio");
            cell15.setCellValue("Refund Count");
            cell16.setCellValue("Refund Amount");
            cell17.setCellValue("Marked for Reversal Count");
            cell18.setCellValue("Marked for Reversal Amount");
            cell19.setCellValue("Refund Count Ratio(Refund+Mark For Reversal)");
            cell20.setCellValue("Refund Amount Ratio(Refund+Mark For Reversal)");
            cell21.setCellValue("Chargeback Count");
            cell22.setCellValue("Chargeback Amount");
            cell23.setCellValue("Chargeback Count Ratio");
            cell24.setCellValue("Chargeback Amount Ratio");
            cell25.setCellValue("AuthFailed Count");
            cell26.setCellValue("AuthFailed Amount");
            cell27.setCellValue("Failed Count");
            cell28.setCellValue("Failed Amount");
            cell29.setCellValue("Declined Count Ratio(AuthFailed+Failed)");
            cell30.setCellValue("Declined Amount Ratio(AuthFailed+Failed)");
            cell31.setCellValue("AuthStarted Count");
            cell32.setCellValue("AuthStarted Amount");
            cell33.setCellValue("AuthStarted Count Ratio");
            cell34.setCellValue("AuthStarted Amount Ratio");
            cell35.setCellValue("Payout Count");
            cell36.setCellValue("Payout Amount");
            cell37.setCellValue("Begun Count");
            cell38.setCellValue("Begun Amount");

            TransactionDAO transactionDAO=new TransactionDAO();
            for (TransactionVO transactionVO : transactionVOList)
            {
                TerminalVO terminalVO=transactionDAO.getGatewayAccountsDetails(transactionVO.getAccountId());
                currency = transactionVO.getCurrency();
                cardType=transactionVO.getCardTypeId();
                totalSalesCount = Double.parseDouble(transactionVO.getTotalTransCount());
                totalSalesAmount = Double.parseDouble(transactionVO.getAmount());
                logger.error("MemberId---"+memberId+"---totalSalesCount:::"+totalSalesCount+"totalSalesAmount---"+totalSalesAmount);

                approveCount = Double.parseDouble(transactionVO.getCaptureCount());
                approveAmount = transactionVO.getCaptureAmount();
                logger.error("MemberId---"+memberId+"---approveCount:::"+approveCount+"approveAmount---"+approveAmount);

                refundCount = Double.parseDouble(transactionVO.getRefundcount());
                refundAmount = Double.parseDouble(transactionVO.getReverseAmount());
                logger.error("MemberId---"+memberId+"---refundCount:::"+refundCount+"refundAmount---"+refundAmount);

                markforreversalCount = Double.parseDouble(transactionVO.getMarkforreversalCount());
                markforreversalAmount = Double.parseDouble(transactionVO.getMarkforreversalAmount());

                chargebackCount = Double.parseDouble(transactionVO.getChargebackCount());
                chargebackAmount = Double.parseDouble(transactionVO.getChargebackAmount());
                logger.error("MemberId---"+memberId+"---chargebackCount:::"+chargebackCount+"chargebackAmount---"+chargebackAmount);

                authstartedCount = Double.parseDouble(transactionVO.getAuthstartedCount());
                authstartedAmount = Double.parseDouble(transactionVO.getAuthstartedAmount());

                authfailedCount = Double.parseDouble(transactionVO.getDeclineCount());
                authfailedAmount = Double.parseDouble(transactionVO.getDeclineAmount());

                failedCount = Double.parseDouble(transactionVO.getFailedCount());
                failedAmount = Double.parseDouble(transactionVO.getFailedAmount());

                payoutCount = Double.parseDouble(transactionVO.getPayoutCount());
                payoutAmount = Double.parseDouble(transactionVO.getPayoutAmount());

                begunCount = Double.parseDouble(transactionVO.getBegunCount());
                begunAmount = Double.parseDouble(transactionVO.getBegunAmount());

                logger.error("MemberId---"+memberId+"---declineCount:::"+authfailedCount+"declineAmount---"+authfailedAmount);
                //logger.error("----------"+refundCount / approveCount * 100);

                if(totalSalesCount!=0){
                    if(approveCount!=0.0)
                        approveCountRatio = Double.parseDouble(Functions.round(approveCount / totalSalesCount * 100, 2));
                    else
                        approveCountRatio=0;
                    if(refundCount!=0.0 && approveCount!=0.0)
                        refundCountRatio = Double.parseDouble(Functions.round((refundCount+markforreversalCount) / approveCount * 100, 2));
                    else if(refundCount!=0.0)
                        refundCountRatio = Double.parseDouble(Functions.round((refundCount+markforreversalCount) / (refundCount+markforreversalCount) * 100, 2));
                    else
                        refundCountRatio=0;
                    if(chargebackCount!=0.0 && approveCount!=0.0)
                        chargebackCountRatio = Double.parseDouble(Functions.round(chargebackCount / approveCount * 100, 2));
                    else if(chargebackCount!=0.0)
                        chargebackCountRatio = Double.parseDouble(Functions.round(chargebackCount / chargebackCount * 100, 2));
                    else
                        chargebackCountRatio=0;
                    if(authfailedCount!=0.0 || failedCount!=0.00)
                        declineCountRatio = Double.parseDouble(Functions.round((authfailedCount+failedCount) / totalSalesCount * 100, 2));
                    else
                        declineCountRatio=0;
                    if(authstartedCount!=0.0)
                        authstartedCountRatio = Double.parseDouble(Functions.round(authstartedCount / totalSalesCount * 100, 2));
                    else
                        authstartedCountRatio=0;
                    if(payoutCount!=0.0)
                        payoutCountRatio = Double.parseDouble(Functions.round(payoutCount / totalSalesCount * 100, 2));
                    else
                        payoutCountRatio=0;


                }else {
                    approveCountRatio = 0;
                    if(refundCount!=0.0)
                        refundCountRatio = Double.parseDouble(Functions.round((refundCount+markforreversalCount) / (refundCount+markforreversalCount) * 100, 2));
                    else
                        refundCountRatio = 0;
                    if(chargebackCount!=0.0)
                        chargebackCountRatio = Double.parseDouble(Functions.round(chargebackCount / chargebackCount * 100, 2));
                    else
                        chargebackCountRatio = 0;

                    declineCountRatio = 0;
                    authstartedCountRatio=0;
                    payoutCountRatio=0;
                }

                if(totalSalesAmount!=0.00)
                {
                    if(approveAmount!=0.0)
                        approveAmountRatio = Double.parseDouble(Functions.round(approveAmount / totalSalesAmount * 100, 2));
                    else
                        approveAmountRatio=0.00;
                    if(refundAmount!=0.0 && approveAmount!=0.0)
                        refundAmountRatio = Double.parseDouble(Functions.round((refundAmount+markforreversalAmount) / approveAmount * 100, 2));
                    else if(refundAmount!=0.0)
                        refundAmountRatio = Double.parseDouble(Functions.round((refundAmount+markforreversalAmount) / (refundAmount+markforreversalAmount) * 100, 2));
                    else
                        refundAmountRatio=0.00;

                    if(chargebackAmount!=0.0 && approveAmount!=0.0)
                        chargebackAmountRatio = Double.parseDouble(Functions.round(chargebackAmount / approveAmount * 100, 2));
                    else if(chargebackAmount!=0.0)
                        chargebackAmountRatio = Double.parseDouble(Functions.round(chargebackAmount / chargebackAmount * 100, 2));
                    else
                        chargebackAmountRatio=0.00;

                    if(authfailedAmount!=0.0 || failedAmount!=0.00)
                        declineAmountRatio = Double.parseDouble(Functions.round((authfailedAmount+failedAmount) / totalSalesAmount * 100, 2));
                    else
                        declineAmountRatio=0;
                    if(authstartedAmount!=0.0)
                        authstartedAmountRatio = Double.parseDouble(Functions.round(authstartedAmount / totalSalesAmount * 100, 2));
                    else
                        authstartedAmountRatio=0;
                    if(payoutAmount!=0.0)
                        payoutAmountRatio = Double.parseDouble(Functions.round(payoutAmount / totalSalesAmount * 100, 2));
                    else
                        payoutAmountRatio=0;
                }else {
                    approveAmountRatio = 0.00;
                    if(refundAmount!=0.0)
                        refundAmountRatio = Double.parseDouble(Functions.round((refundAmount+markforreversalAmount) / (refundAmount+markforreversalAmount) * 100, 2));
                    else
                        refundAmountRatio=0.00;
                    if(chargebackAmount!=0.00)
                        chargebackAmountRatio = Double.parseDouble(Functions.round(chargebackAmount / chargebackAmount * 100, 2));
                    else
                        chargebackAmountRatio = 0.00;
                    declineAmountRatio = 0.00;
                    authstartedAmountRatio=0.00;
                    payoutAmountRatio=0.00;
                }

                HSSFRow row1 = sheet.createRow((short) rowno);
                HSSFCell cellr0 = row1.createCell((short) 0);
                HSSFCell cellr1 = row1.createCell((short) 1);
                HSSFCell cellr2 = row1.createCell((short) 2);
                HSSFCell cellr3 = row1.createCell((short) 3);
                HSSFCell cellr4 = row1.createCell((short) 4);
                HSSFCell cellr5 = row1.createCell((short) 5);
                HSSFCell cellr6 = row1.createCell((short) 6);
                HSSFCell cellr7 = row1.createCell((short) 7);
                HSSFCell cellr8 = row1.createCell((short) 8);
                HSSFCell cellr9 = row1.createCell((short) 9);
                HSSFCell cellr10 = row1.createCell((short) 10);
                HSSFCell cellr11 = row1.createCell((short) 11);
                HSSFCell cellr12 = row1.createCell((short) 12);
                HSSFCell cellr13 = row1.createCell((short) 13);
                HSSFCell cellr14 = row1.createCell((short) 14);
                HSSFCell cellr15 = row1.createCell((short) 15);
                HSSFCell cellr16 = row1.createCell((short) 16);
                HSSFCell cellr17 = row1.createCell((short) 17);
                HSSFCell cellr18 = row1.createCell((short) 18);
                HSSFCell cellr19 = row1.createCell((short) 19);
                HSSFCell cellr20 = row1.createCell((short) 20);
                HSSFCell cellr21 = row1.createCell((short) 21);
                HSSFCell cellr22 = row1.createCell((short) 22);
                HSSFCell cellr23 = row1.createCell((short) 23);
                HSSFCell cellr24 = row1.createCell((short) 24);
                HSSFCell cellr25 = row1.createCell((short) 25);
                HSSFCell cellr26 = row1.createCell((short) 26);
                HSSFCell cellr27 = row1.createCell((short) 27);
                HSSFCell cellr28 = row1.createCell((short) 28);
                HSSFCell cellr29 = row1.createCell((short) 29);
                HSSFCell cellr30 = row1.createCell((short) 30);
                HSSFCell cellr31 = row1.createCell((short) 31);
                HSSFCell cellr32 = row1.createCell((short) 32);
                HSSFCell cellr33 = row1.createCell((short) 33);
                HSSFCell cellr34 = row1.createCell((short) 34);
                HSSFCell cellr35 = row1.createCell((short) 35);
                HSSFCell cellr36 = row1.createCell((short) 36);
                HSSFCell cellr37 = row1.createCell((short) 37);
                HSSFCell cellr38 = row1.createCell((short) 38);

                cellr0.setCellStyle(style);
                cellr1.setCellStyle(style);
                cellr2.setCellStyle(style);
                cellr3.setCellStyle(style);
                cellr4.setCellStyle(style);
                cellr5.setCellStyle(style);
                cellr6.setCellStyle(style);
                cellr7.setCellStyle(style);
                cellr8.setCellStyle(style);
                cellr9.setCellStyle(style);
                cellr10.setCellStyle(style);
                cellr11.setCellStyle(style);
                cellr12.setCellStyle(style);
                cellr13.setCellStyle(style);
                cellr14.setCellStyle(style);
                cellr15.setCellStyle(style);
                cellr16.setCellStyle(style);
                cellr17.setCellStyle(style);
                cellr18.setCellStyle(style);
                cellr19.setCellStyle(style);
                cellr20.setCellStyle(style);
                cellr21.setCellStyle(style);
                cellr22.setCellStyle(style);
                cellr23.setCellStyle(style);
                cellr24.setCellStyle(style);
                cellr25.setCellStyle(style);
                cellr26.setCellStyle(style);
                cellr27.setCellStyle(style);
                cellr28.setCellStyle(style);
                cellr29.setCellStyle(style);
                cellr30.setCellStyle(style);
                cellr31.setCellStyle(style);
                cellr32.setCellStyle(style);
                cellr33.setCellStyle(style);
                cellr34.setCellStyle(style);
                cellr35.setCellStyle(style);
                cellr36.setCellStyle(style);
                cellr37.setCellStyle(style);
                cellr38.setCellStyle(style);

                cellr0.setCellValue(srno);
                cellr1.setCellValue(transactionVO.getAccountId());
                cellr2.setCellValue(terminalVO.getMemberId());
                cellr3.setCellValue(gatewayName);
                cellr4.setCellValue(terminalVO.getGateway_name());
                cellr5.setCellValue(terminalVO.getDisplayName());
                cellr6.setCellValue(terminalVO.getIs3DSupport());
                cellr7.setCellValue(currency);
                cellr8.setCellValue(cardType);
                cellr9.setCellValue(totalSalesCount);
                cellr10.setCellValue(totalSalesAmount);
                cellr11.setCellValue(approveCount);
                cellr12.setCellValue(approveAmount);
                cellr13.setCellValue(approveCountRatio + "%");
                cellr14.setCellValue(approveAmountRatio + "%");
                cellr15.setCellValue(refundCount);
                cellr16.setCellValue(refundAmount);
                cellr17.setCellValue(markforreversalCount);
                cellr18.setCellValue(markforreversalAmount);
                cellr19.setCellValue(refundCountRatio + "%");
                cellr20.setCellValue(refundAmountRatio + "%");
                cellr21.setCellValue(chargebackCount);
                cellr22.setCellValue(chargebackAmount);
                cellr23.setCellValue(chargebackCountRatio + "%");
                cellr24.setCellValue(chargebackAmountRatio + "%");
                cellr25.setCellValue(authfailedCount);
                cellr26.setCellValue(authfailedAmount);
                cellr27.setCellValue(failedCount);
                cellr28.setCellValue(failedAmount);
                cellr29.setCellValue(declineCountRatio + "%");
                cellr30.setCellValue(declineAmountRatio + "%");
                cellr31.setCellValue(authstartedCount);
                cellr32.setCellValue(authstartedAmount);
                cellr33.setCellValue(authstartedCountRatio + "%");
                cellr34.setCellValue(authstartedAmountRatio + "%");
                cellr35.setCellValue(payoutCount);
                cellr36.setCellValue(payoutAmount);
                cellr37.setCellValue(begunCount);
                cellr38.setCellValue(begunAmount);
                rowno++;
                srno++;
            }
        }
    }

    private void generateSalesReport(HSSFSheet sheet, HSSFCellStyle style, HSSFCellStyle textBoldStyle, String partnerName,String gatewayName, String startDate, String endDate, List<List<TransactionVO>> transactionVOList)
    {
        int rowno = 0;

        String merchantName="";
        String gateway=gatewayName;
        String memberId="";
        String currency="";
        String cardType="";
        double totalSalesCount=0;
        double totalSalesAmount=0.00;
        double approveCount=0;
        double approveAmount=0.00;
        double approveCountRatio=0;
        double approveAmountRatio=0.00;
        double refundCount=0;
        double refundAmount=0.00;
        double refundCountRatio=0;
        double refundAmountRatio=0.00;
        double chargebackCount=0;
        double chargebackAmount=0.00;
        double chargebackCountRatio=0;
        double chargebackAmountRatio=0.00;
        /*double declineCount=0;
        double declineAmount=0.00;*/
        double declineCountRatio=0;
        double declineAmountRatio=0.00;

        double markforreversalCount=0;
        double markforreversalAmount=0.00;

        double authstartedCount=0;
        double authstartedAmount=0;
        double authstartedCountRatio=0;
        double authstartedAmountRatio=0;
        double authfailedCount=0;
        double authfailedAmount=0;
        double failedCount=0;
        double failedAmount=0.00;
        double payoutCount=0;
        double payoutAmount=0.00;
        double payoutCountRatio=0;
        double payoutAmountRatio=0.00;
        double begunCount=0;
        double begunAmount=0.00;

        if (transactionVOList != null && !(transactionVOList.size() == 0))
        {
            sheet.addMergedRegion(new Region(rowno, (short) 1, rowno, (short) 5));
            rowno++;
            HSSFRow dateHeader = sheet.createRow(rowno);
            sheet.addMergedRegion(new Region(rowno, (short) 1, rowno, (short) 5));
            rowno++;
            HSSFRow header1 = sheet.createRow(rowno);
            rowno++;

            sheet.setColumnWidth((short) 0, (short) 202000);
            sheet.setColumnWidth((short) 1, (short) 202000);
            sheet.setColumnWidth((short) 2, (short) 202000);
            sheet.setColumnWidth((short) 3, (short) 202000);
            sheet.setColumnWidth((short) 4, (short) 202000);
            sheet.setColumnWidth((short) 5, (short) 202000);
            sheet.setColumnWidth((short) 6, (short) 202000);
            sheet.setColumnWidth((short) 7, (short) 202000);
            sheet.setColumnWidth((short) 8, (short) 202000);
            sheet.setColumnWidth((short) 9, (short) 202000);
            sheet.setColumnWidth((short) 10, (short) 202000);
            sheet.setColumnWidth((short) 11, (short) 202000);
            sheet.setColumnWidth((short) 12, (short) 202000);
            sheet.setColumnWidth((short) 13, (short) 202000);
            sheet.setColumnWidth((short) 14, (short) 202000);
            sheet.setColumnWidth((short) 15, (short) 202000);
            sheet.setColumnWidth((short) 16, (short) 202000);
            sheet.setColumnWidth((short) 17, (short) 202000);
            sheet.setColumnWidth((short) 18, (short) 202000);
            sheet.setColumnWidth((short) 19, (short) 202000);
            sheet.setColumnWidth((short) 20, (short) 202000);
            sheet.setColumnWidth((short) 21, (short) 202000);
            sheet.setColumnWidth((short) 22, (short) 202000);
            sheet.setColumnWidth((short) 23, (short) 202000);
            sheet.setColumnWidth((short) 24, (short) 202000);
            sheet.setColumnWidth((short) 25, (short) 202000);
            sheet.setColumnWidth((short) 26, (short) 202000);
            sheet.setColumnWidth((short) 27, (short) 202000);
            sheet.setColumnWidth((short) 28, (short) 202000);
            sheet.setColumnWidth((short) 29, (short) 202000);
            sheet.setColumnWidth((short) 30, (short) 202000);
            sheet.setColumnWidth((short) 31, (short) 202000);
            sheet.setColumnWidth((short) 32, (short) 202000);
            sheet.setColumnWidth((short) 33, (short) 202000);
            sheet.setColumnWidth((short) 34, (short) 202000);
            sheet.setColumnWidth((short) 35, (short) 202000);
            sheet.setColumnWidth((short) 36, (short) 202000);

            HSSFCell dateCell = dateHeader.createCell((short) 0);
            HSSFCell cell0 = header1.createCell((short) 0);
            HSSFCell cell1 = header1.createCell((short) 1);
            HSSFCell cell2 = header1.createCell((short) 2);
            HSSFCell cell3 = header1.createCell((short) 3);
            HSSFCell cell4 = header1.createCell((short) 4);
            HSSFCell cell5 = header1.createCell((short) 5);
            HSSFCell cell6 = header1.createCell((short) 6);
            HSSFCell cell7 = header1.createCell((short) 7);
            HSSFCell cell8 = header1.createCell((short) 8);
            HSSFCell cell9 = header1.createCell((short) 9);
            HSSFCell cell10 = header1.createCell((short) 10);
            HSSFCell cell11 = header1.createCell((short) 11);
            HSSFCell cell12 = header1.createCell((short) 12);
            HSSFCell cell13 = header1.createCell((short) 13);
            HSSFCell cell14 = header1.createCell((short) 14);
            HSSFCell cell15 = header1.createCell((short) 15);
            HSSFCell cell16 = header1.createCell((short) 16);
            HSSFCell cell17 = header1.createCell((short) 17);
            HSSFCell cell18 = header1.createCell((short) 18);
            HSSFCell cell19 = header1.createCell((short) 19);
            HSSFCell cell20 = header1.createCell((short) 20);
            HSSFCell cell21 = header1.createCell((short) 21);
            HSSFCell cell22 = header1.createCell((short) 22);
            HSSFCell cell23 = header1.createCell((short) 23);
            HSSFCell cell24 = header1.createCell((short) 24);
            HSSFCell cell25 = header1.createCell((short) 25);
            HSSFCell cell26 = header1.createCell((short) 26);
            HSSFCell cell27 = header1.createCell((short) 27);
            HSSFCell cell28 = header1.createCell((short) 28);
            HSSFCell cell29 = header1.createCell((short) 29);
            HSSFCell cell30 = header1.createCell((short) 30);
            HSSFCell cell31 = header1.createCell((short) 31);
            HSSFCell cell32 = header1.createCell((short) 32);
            HSSFCell cell33 = header1.createCell((short) 33);
            HSSFCell cell34 = header1.createCell((short) 34);
            HSSFCell cell35 = header1.createCell((short) 35);
            //HSSFCell cell36 = header1.createCell((short) 36);


            dateCell.setCellStyle(textBoldStyle);
            cell0.setCellStyle(textBoldStyle);
            cell1.setCellStyle(textBoldStyle);
            cell2.setCellStyle(textBoldStyle);
            cell3.setCellStyle(textBoldStyle);
            cell4.setCellStyle(textBoldStyle);
            cell5.setCellStyle(textBoldStyle);
            cell6.setCellStyle(textBoldStyle);
            cell7.setCellStyle(textBoldStyle);
            cell8.setCellStyle(textBoldStyle);
            cell9.setCellStyle(textBoldStyle);
            cell10.setCellStyle(textBoldStyle);
            cell11.setCellStyle(textBoldStyle);
            cell12.setCellStyle(textBoldStyle);
            cell13.setCellStyle(textBoldStyle);
            cell14.setCellStyle(textBoldStyle);
            cell15.setCellStyle(textBoldStyle);
            cell16.setCellStyle(textBoldStyle);
            cell17.setCellStyle(textBoldStyle);
            cell18.setCellStyle(textBoldStyle);
            cell19.setCellStyle(textBoldStyle);
            cell20.setCellStyle(textBoldStyle);
            cell21.setCellStyle(textBoldStyle);
            cell22.setCellStyle(textBoldStyle);
            cell23.setCellStyle(textBoldStyle);
            cell24.setCellStyle(textBoldStyle);
            cell25.setCellStyle(textBoldStyle);
            cell26.setCellStyle(textBoldStyle);
            cell27.setCellStyle(textBoldStyle);
            cell28.setCellStyle(textBoldStyle);
            cell29.setCellStyle(textBoldStyle);
            cell30.setCellStyle(textBoldStyle);
            cell31.setCellStyle(textBoldStyle);
            cell32.setCellStyle(textBoldStyle);
            cell33.setCellStyle(textBoldStyle);
            cell34.setCellStyle(textBoldStyle);
            cell35.setCellStyle(textBoldStyle);
            //cell36.setCellStyle(textBoldStyle);


            dateCell.setCellValue("Sales Monitoring (" + startDate + " - " + endDate + ")");
            cell0.setCellValue("Bank");
            cell1.setCellValue("Partner Name");
            cell2.setCellValue("Merchant Name");
            cell3.setCellValue("MID");
            cell4.setCellValue("Currency");
            cell5.setCellValue("Card Type");
            cell6.setCellValue("Total COUNT");
            cell7.setCellValue("Total AMOUNT");//SALES(cb,Capture Success,Settled,Reversed,payoutsuccessful,declined)
            cell8.setCellValue("Approved Count(SALES-Capture Success,Settled,Reversed,Chargeback,Auth Successful,Marked for reversal,Chargeback Reversed)");
            cell9.setCellValue("Approved Amount(SALES-Capture Success,Settled,Reversed,Chargeback,Auth Successful,Marked for reversal,Chargeback Reversed)");
            cell10.setCellValue("Approved Count Ratio");
            cell11.setCellValue("Approved Amount Ratio");
            cell12.setCellValue("Refund Count");
            cell13.setCellValue("Refund Amount");
            cell14.setCellValue("Marked for Reversal Count");
            cell15.setCellValue("Marked for Reversal Amount");
            cell16.setCellValue("Refund Count Ratio(Refund+Mark For Reversal)");
            cell17.setCellValue("Refund Amount Ratio(Refund+Mark For Reversal)");
            cell18.setCellValue("Chargeback Count");
            cell19.setCellValue("Chargeback Amount");
            cell20.setCellValue("Chargeback Count Ratio");
            cell21.setCellValue("Chargeback Amount Ratio");
            cell22.setCellValue("AuthFailed Count");
            cell23.setCellValue("AuthFailed Amount");
            cell24.setCellValue("Failed Count");
            cell25.setCellValue("Failed Amount");
            cell26.setCellValue("Declined Count Ratio(AuthFailed+Failed)");
            cell27.setCellValue("Declined Amount Ratio(AuthFailed+Failed)");
            cell28.setCellValue("AuthStarted Count");
            cell29.setCellValue("AuthStarted Amount");
            cell30.setCellValue("AuthStarted Count Ratio");
            cell31.setCellValue("AuthStarted Amount Ratio");
            cell32.setCellValue("Payout Count");
            cell33.setCellValue("Payout Amount");
            /*cell33.setCellValue("Payout Count Ratio");
            cell34.setCellValue("Payout Amount Ratio");*/
            cell34.setCellValue("Begun Count");
            cell35.setCellValue("Begun Amount");

            for(int i=0;i<transactionVOList.size();i++)
            {
                List<TransactionVO> transactionVOs = transactionVOList.get(i);
                for (TransactionVO transactionVO : transactionVOs)
                {
                    System.out.println("transactionVO name====="+transactionVO.getName());
                    merchantName = transactionVO.getName();
                    memberId = transactionVO.getMemberId();
                    currency = transactionVO.getCurrency();
                    cardType=transactionVO.getCardTypeId();
                    String partnername = transactionVO.getToType();
                    totalSalesCount = Double.parseDouble(transactionVO.getTotalTransCount());
                    totalSalesAmount = Double.parseDouble(transactionVO.getAmount());
                    System.out.println("count ==== "+transactionVO.getTotalTransCount());
                    System.out.println("amount ==== "+transactionVO.getAmount());
                    logger.error("MemberId---"+memberId+"---totalSalesCount:::"+totalSalesCount+"totalSalesAmount---"+totalSalesAmount);

                    approveCount = Double.parseDouble(transactionVO.getCaptureCount());
                    approveAmount = transactionVO.getCaptureAmount();
                    logger.error("MemberId---"+memberId+"---approveCount:::"+approveCount+"approveAmount---"+approveAmount);

                    refundCount = Double.parseDouble(transactionVO.getRefundcount());
                    refundAmount = Double.parseDouble(transactionVO.getReverseAmount());
                    logger.error("MemberId---"+memberId+"---refundCount:::"+refundCount+"refundAmount---"+refundAmount);

                    markforreversalCount = Double.parseDouble(transactionVO.getMarkforreversalCount());
                    markforreversalAmount = Double.parseDouble(transactionVO.getMarkforreversalAmount());

                    chargebackCount = Double.parseDouble(transactionVO.getChargebackCount());
                    chargebackAmount = Double.parseDouble(transactionVO.getChargebackAmount());
                    logger.error("MemberId---"+memberId+"---chargebackCount:::"+chargebackCount+"chargebackAmount---"+chargebackAmount);

                    authstartedCount = Double.parseDouble(transactionVO.getAuthstartedCount());
                    authstartedAmount = Double.parseDouble(transactionVO.getAuthstartedAmount());

                    authfailedCount = Double.parseDouble(transactionVO.getDeclineCount());
                    authfailedAmount = Double.parseDouble(transactionVO.getDeclineAmount());

                    failedCount = Double.parseDouble(transactionVO.getFailedCount());
                    failedAmount = Double.parseDouble(transactionVO.getFailedAmount());

                    payoutCount = Double.parseDouble(transactionVO.getPayoutCount());
                    payoutAmount = Double.parseDouble(transactionVO.getPayoutAmount());

                    begunCount = Double.parseDouble(transactionVO.getBegunCount());
                    begunAmount = Double.parseDouble(transactionVO.getBegunAmount());

                    logger.error("MemberId---"+memberId+"---declineCount:::"+authfailedCount+"declineAmount---"+authfailedAmount);
                    //logger.error("----------"+refundCount / approveCount * 100);

                    if(totalSalesCount!=0){
                        if(approveCount!=0.0)
                            approveCountRatio = Double.parseDouble(Functions.round(approveCount / totalSalesCount * 100, 2));
                        else
                            approveCountRatio=0;
                        if(refundCount!=0.0 && approveCount!=0.0)
                            refundCountRatio = Double.parseDouble(Functions.round((refundCount+markforreversalCount) / approveCount * 100, 2));
                        else if(refundCount!=0.0)
                            refundCountRatio = Double.parseDouble(Functions.round((refundCount+markforreversalCount) / (refundCount+markforreversalCount) * 100, 2));
                        else
                            refundCountRatio=0;
                        if(chargebackCount!=0.0 && approveCount!=0.0)
                            chargebackCountRatio = Double.parseDouble(Functions.round(chargebackCount / approveCount * 100, 2));
                        else if(chargebackCount!=0.0)
                            chargebackCountRatio = Double.parseDouble(Functions.round(chargebackCount / chargebackCount * 100, 2));
                        else
                            chargebackCountRatio=0;
                        if(authfailedCount!=0.0 || failedCount!=0.00)
                            declineCountRatio = Double.parseDouble(Functions.round((authfailedCount+failedCount) / totalSalesCount * 100, 2));
                        else
                            declineCountRatio=0;
                        if(authstartedCount!=0.0)
                            authstartedCountRatio = Double.parseDouble(Functions.round(authstartedCount / totalSalesCount * 100, 2));
                        else
                            authstartedCountRatio=0;
                        if(payoutCount!=0.0)
                            payoutCountRatio = Double.parseDouble(Functions.round(payoutCount / totalSalesCount * 100, 2));
                        else
                            payoutCountRatio=0;


                    }else {
                        approveCountRatio = 0;
                        if(refundCount!=0.0)
                            refundCountRatio = Double.parseDouble(Functions.round(refundCount / refundCount * 100, 2));
                        else
                            refundCountRatio = 0;
                        if(chargebackCount!=0.0)
                            chargebackCountRatio = Double.parseDouble(Functions.round(chargebackCount / chargebackCount * 100, 2));
                        else
                            chargebackCountRatio = 0;

                        declineCountRatio = 0;
                        authstartedCountRatio=0;
                        payoutCountRatio=0;
                    }

                    if(totalSalesAmount!=0.00)
                    {
                        if(approveAmount!=0.0)
                            approveAmountRatio = Double.parseDouble(Functions.round(approveAmount / totalSalesAmount * 100, 2));
                        else
                            approveAmountRatio=0.00;
                        if(refundAmount!=0.0 && approveAmount!=0.0)
                            refundAmountRatio = Double.parseDouble(Functions.round((refundAmount+markforreversalAmount) / approveAmount * 100, 2));
                        else if(refundAmount!=0.0)
                            refundAmountRatio = Double.parseDouble(Functions.round((refundAmount+markforreversalAmount) / (refundAmount+markforreversalAmount) * 100, 2));
                        else
                            refundAmountRatio=0.00;

                        if(chargebackAmount!=0.0 && approveAmount!=0.0)
                            chargebackAmountRatio = Double.parseDouble(Functions.round(chargebackAmount / approveAmount * 100, 2));
                        else if(chargebackAmount!=0.0)
                            chargebackAmountRatio = Double.parseDouble(Functions.round(chargebackAmount / chargebackAmount * 100, 2));
                        else
                            chargebackAmountRatio=0.00;

                        if(authfailedAmount!=0.0 || failedAmount!=0.00)
                            declineAmountRatio = Double.parseDouble(Functions.round((authfailedAmount+failedAmount) / totalSalesAmount * 100, 2));
                        else
                            declineAmountRatio=0;
                        if(authstartedAmount!=0.0)
                            authstartedAmountRatio = Double.parseDouble(Functions.round(authstartedAmount / totalSalesAmount * 100, 2));
                        else
                            authstartedAmountRatio=0;
                        if(payoutAmount!=0.0)
                            payoutAmountRatio = Double.parseDouble(Functions.round(payoutAmount / totalSalesAmount * 100, 2));
                        else
                            payoutAmountRatio=0;
                    }else {
                        approveAmountRatio = 0.00;
                        if(refundAmount!=0.0)
                            refundAmountRatio = Double.parseDouble(Functions.round((refundAmount+markforreversalAmount) / (refundAmount+markforreversalAmount) * 100, 2));
                        else
                            refundAmountRatio=0.00;
                        if(chargebackAmount!=0.00)
                            chargebackAmountRatio = Double.parseDouble(Functions.round(chargebackAmount / chargebackAmount * 100, 2));
                        else
                            chargebackAmountRatio = 0.00;
                        declineAmountRatio = 0.00;
                        authstartedAmountRatio=0.00;
                        payoutAmountRatio=0.00;
                    }
                    System.out.println(" inside for chargebackCount "+chargebackCount +" authstartedCount "+authstartedCount +" authstartedCount "+authstartedCount +"failedCount "+failedCount);

                    HSSFRow row1 = sheet.createRow((short) rowno);
                    HSSFCell cellr0 = row1.createCell((short) 0);
                    HSSFCell cellr1 = row1.createCell((short) 1);
                    HSSFCell cellr2 = row1.createCell((short) 2);
                    HSSFCell cellr3 = row1.createCell((short) 3);
                    HSSFCell cellr4 = row1.createCell((short) 4);
                    HSSFCell cellr5 = row1.createCell((short) 5);
                    HSSFCell cellr6 = row1.createCell((short) 6);
                    HSSFCell cellr7 = row1.createCell((short) 7);
                    HSSFCell cellr8 = row1.createCell((short) 8);
                    HSSFCell cellr9 = row1.createCell((short) 9);
                    HSSFCell cellr10 = row1.createCell((short) 10);
                    HSSFCell cellr11 = row1.createCell((short) 11);
                    HSSFCell cellr12 = row1.createCell((short) 12);
                    HSSFCell cellr13 = row1.createCell((short) 13);
                    HSSFCell cellr14 = row1.createCell((short) 14);
                    HSSFCell cellr15 = row1.createCell((short) 15);
                    HSSFCell cellr16 = row1.createCell((short) 16);
                    HSSFCell cellr17 = row1.createCell((short) 17);
                    HSSFCell cellr18 = row1.createCell((short) 18);
                    HSSFCell cellr19 = row1.createCell((short) 19);
                    HSSFCell cellr20 = row1.createCell((short) 20);
                    HSSFCell cellr21 = row1.createCell((short) 21);
                    HSSFCell cellr22 = row1.createCell((short) 22);
                    HSSFCell cellr23 = row1.createCell((short) 23);
                    HSSFCell cellr24 = row1.createCell((short) 24);
                    HSSFCell cellr25 = row1.createCell((short) 25);
                    HSSFCell cellr26 = row1.createCell((short) 26);
                    HSSFCell cellr27 = row1.createCell((short) 27);
                    HSSFCell cellr28 = row1.createCell((short) 28);
                    HSSFCell cellr29 = row1.createCell((short) 29);
                    HSSFCell cellr30 = row1.createCell((short) 30);
                    HSSFCell cellr31 = row1.createCell((short) 31);
                    HSSFCell cellr32 = row1.createCell((short) 32);
                    HSSFCell cellr33 = row1.createCell((short) 33);
                    HSSFCell cellr34 = row1.createCell((short) 34);
                    HSSFCell cellr35 = row1.createCell((short) 35);
                    //HSSFCell cellr36 = row1.createCell((short) 36);

                    cellr0.setCellStyle(style);
                    cellr1.setCellStyle(style);
                    cellr2.setCellStyle(style);
                    cellr3.setCellStyle(style);
                    cellr4.setCellStyle(style);
                    cellr5.setCellStyle(style);
                    cellr6.setCellStyle(style);
                    cellr7.setCellStyle(style);
                    cellr8.setCellStyle(style);
                    cellr9.setCellStyle(style);
                    cellr10.setCellStyle(style);
                    cellr11.setCellStyle(style);
                    cellr12.setCellStyle(style);
                    cellr13.setCellStyle(style);
                    cellr14.setCellStyle(style);
                    cellr15.setCellStyle(style);
                    cellr16.setCellStyle(style);
                    cellr17.setCellStyle(style);
                    cellr18.setCellStyle(style);
                    cellr19.setCellStyle(style);
                    cellr20.setCellStyle(style);
                    cellr21.setCellStyle(style);
                    cellr22.setCellStyle(style);
                    cellr23.setCellStyle(style);
                    cellr24.setCellStyle(style);
                    cellr25.setCellStyle(style);
                    cellr26.setCellStyle(style);
                    cellr27.setCellStyle(style);
                    cellr28.setCellStyle(style);
                    cellr29.setCellStyle(style);
                    cellr30.setCellStyle(style);
                    cellr31.setCellStyle(style);
                    cellr32.setCellStyle(style);
                    cellr33.setCellStyle(style);
                    cellr34.setCellStyle(style);
                    cellr35.setCellStyle(style);
                    //cellr36.setCellStyle(style);

                    cellr0.setCellValue(gateway);
                    cellr1.setCellValue(partnername);
                    cellr2.setCellValue(merchantName);
                    cellr3.setCellValue(memberId);
                    cellr4.setCellValue(currency);
                    cellr5.setCellValue(cardType);
                    cellr6.setCellValue(totalSalesCount);
                    cellr7.setCellValue(totalSalesAmount);
                    cellr8.setCellValue(approveCount);
                    System.out.println("approveCount---"+approveCount + " approveAmount"+approveAmount);
                    cellr9.setCellValue(approveAmount);
                    cellr10.setCellValue(approveCountRatio + "%");
                    cellr11.setCellValue(approveAmountRatio + "%");
                    cellr12.setCellValue(refundCount);
                    cellr13.setCellValue(refundAmount);
                    cellr14.setCellValue(markforreversalCount);
                    cellr15.setCellValue(markforreversalAmount);
                    cellr16.setCellValue(refundCountRatio + "%");
                    cellr17.setCellValue(refundAmountRatio + "%");
                    cellr18.setCellValue(chargebackCount);
                    cellr19.setCellValue(chargebackAmount);
                    cellr20.setCellValue(chargebackCountRatio + "%");
                    cellr21.setCellValue(chargebackAmountRatio + "%");
                    cellr22.setCellValue(authfailedCount);
                    cellr23.setCellValue(authfailedAmount);
                    cellr24.setCellValue(failedCount);
                    cellr25.setCellValue(failedAmount);
                    cellr26.setCellValue(declineCountRatio + "%");
                    cellr27.setCellValue(declineAmountRatio + "%");
                    cellr28.setCellValue(authstartedCount);
                    cellr29.setCellValue(authstartedAmount);
                    cellr30.setCellValue(authstartedCountRatio + "%");
                    cellr31.setCellValue(authstartedAmountRatio + "%");
                    cellr32.setCellValue(payoutCount);
                    cellr33.setCellValue(payoutAmount);
                    /*cellr33.setCellValue(payoutCountRatio + "%");
                    cellr34.setCellValue(payoutAmountRatio + "%");*/
                    cellr34.setCellValue(begunCount);
                    cellr35.setCellValue(begunAmount);
                    rowno++;

                }
                System.out.println("chargebackCount"+chargebackCount +" authstartedCount"+authstartedCount +" authstartedCount"+authstartedCount +"failedCount "+failedCount);
            }
        }
    }

    public static void main(String args[])
    {
        ExportExcelForBankTransactions exportExcelForBankTransactions = new ExportExcelForBankTransactions();
        TransactionVO transactionVO = new TransactionVO();
        Map<String, List<TransactionVO>> transactionVOMap = null;
        Map<String, List<TransactionVO>> transactionVORefundMap = null;
        TransactionManager transactionManager = new TransactionManager();
        Set<String> gatewaySet = null;
        try
        {
            transactionVO.setTerminalId("");
            transactionVO.setStartDate("2019-07-01 00:00:00");
            transactionVO.setEndDate("2019-07-22 23:59:59");
            transactionVO.setGatewayName("PAYNETICS");
            transactionVO.setCurrency("EUR");

            transactionVOMap = transactionManager.getBankTransactionStatus(transactionVO, gatewaySet);
            transactionVORefundMap = transactionManager.getBankTransactionRefundStatus(transactionVO, gatewaySet);
        }
        catch (Exception e)
        {
           logger.error("Catch exception...",e);
        }

    }
}
