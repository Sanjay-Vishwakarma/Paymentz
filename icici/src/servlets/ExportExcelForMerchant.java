import com.directi.pg.Admin;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.directi.pg.core.GatewayTypeService;
import com.logicboxes.util.ApplicationProperties;
import com.manager.TerminalManager;
import com.manager.TransactionManager;
import com.manager.vo.TerminalVO;
import com.manager.vo.TransactionVO;
import com.payment.validators.InputFields;
import com.payment.validators.InputValidator;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.hssf.util.Region;
import org.owasp.esapi.User;
import org.owasp.esapi.errors.ValidationException;

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
 * Created by Vishal on 6/1/2017.
 */
public class ExportExcelForMerchant extends HttpServlet
{
    private static Logger logger = new Logger(ExportExcelForMerchant.class.getName());
    private static Functions functions = new Functions();

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
        File file = new File(filepath);
        file.delete();
        logger.info("Successful#######");
        return true;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        doService(request, response);
    }

    public void doService(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        logger.debug("Inside ExportExcelForMerchant");
        HttpSession session = request.getSession();
        User user =  (User)session.getAttribute("ESAPIUserSessionKey");

        if (!Admin.isLoggedIn(session))
        {   logger.debug("Admin is logout ");
            response.sendRedirect("/icici/logout.jsp");
            return;
        }

        StringBuffer msg = new StringBuffer();
        TransactionManager transactionManager = new TransactionManager();
        TransactionVO transactionVO = null;
        String memberId = request.getParameter("merchantid");
        String terminal = request.getParameter("terminalid");
        String gateway = request.getParameter("gateway");
        String startDate = request.getParameter("startDate");
        String startdate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String enddate = request.getParameter("endDate");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String status = request.getParameter("status");
        String fileName = "";
        String filePath = "";
        String cardTypeId = "";
        String paymodeId="";
        String terminalId="";
        logger.debug("gateway from ExportExcelForMerchant---" + gateway);

        if("0".equals(gateway))
            gateway="";

        Set<String> gatewaySet = (Set) session.getAttribute("gatewaySet");
        HashMap statushash = (HashMap) session.getAttribute("statushash");
        logger.debug("gatewaySet in excel===="+gatewaySet);
        String initDateFormat="dd/MM/yyyy HH:mm:ss";
        String targetDateFormat="yyyy-MM-dd HH:mm:ss";
        try
        {
            if(!"0".equals(terminal) && functions.isValueNull(terminal))
            {
                if(terminal.contains("-"))
                {
                    String arrTerminal[] = terminal.split("-");
                    terminalId = arrTerminal[0];
                    cardTypeId = arrTerminal[1];
                    paymodeId = arrTerminal[2];
                }
                else
                {
                    TerminalManager terminalManager = new TerminalManager();
                    TerminalVO terminalVO = terminalManager.getTerminalByTerminalId(terminal);
                    cardTypeId = terminalVO.getCardTypeId();
                    paymodeId = terminalVO.getPaymodeId();
                }
            }

            try
            {
                startDate = formatDate(startDate + " "+startTime,initDateFormat,targetDateFormat);
                endDate = formatDate(endDate + " "+endTime,initDateFormat,targetDateFormat);
                validateOptionalParameter(request);
            }
            catch (ParseException e)
            {
                request.setAttribute("error",e.getMessage());
                RequestDispatcher rd = request.getRequestDispatcher("/merchanttransmaillist.jsp?ctoken="+user.getCSRFToken());
                rd.forward(request, response);
                return;
            }
            catch(ValidationException e)
            {
                logger.error("Invalid page no or records",e);
            }

            RequestDispatcher rd = request.getRequestDispatcher("/merchanttransmaillist.jsp?ctoken=" + user.getCSRFToken());

            transactionVO = new TransactionVO();
            Map<String, List<TransactionVO>> transactionVOMap = null;
            Map<String, List<TransactionVO>> transactionVORefundMap = null;

            transactionVO.setMemberId(memberId);
            transactionVO.setTerminalId(terminalId);
            transactionVO.setCardTypeId(cardTypeId);
            transactionVO.setPaymodeid(paymodeId);
            transactionVO.setStatus(status);
            transactionVO.setStartDate(startDate);
            transactionVO.setEndDate(endDate);

            if(gatewaySet != null && gatewaySet.size()>0)
            {
                transactionVOMap = transactionManager.getGatewayWiseMerchantTransactionStatus(transactionVO, gatewaySet);
                transactionVORefundMap = transactionManager.getGatewayWiseMerchantRefundStatus(transactionVO, gatewaySet);
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

            if (!memberId.equals(""))
            {
                HSSFSheet sheet = workbook.createSheet("Summary");
                generateSummary(sheet, style, textBoldStyle, startdate, enddate, gateway, memberId, terminalId);

                HSSFSheet sheet1 = workbook.createSheet("Consolidated Report");
                generateConsolidatedReport(sheet1, style, textBoldStyle, startdate, enddate, transactionVOMap, transactionVORefundMap, gatewaySet, statushash);

                SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String currentSystemDate = dateFormater.format(new Date());
                fileName = "Transaction Report_" + currentSystemDate;
                fileName = fileName + ".xls";

                filePath = ApplicationProperties.getProperty("EXPORT_FILE_LOCAL_PATH");
                filePath = filePath + fileName;

                FileOutputStream fileOut = new FileOutputStream(filePath);
                workbook.write(fileOut);
                fileOut.close();

                sendFile(filePath, fileName, response);
                return;
            }
            else
            {
                msg.append("Invalid Member.");
                request.setAttribute("msg", msg.toString());
                rd.forward(request, response);
                return;
            }
        }
        catch (Exception e)
        {
           logger.error("Catch Exception...",e);
        }
    }

    public Set<String> getGatewayHash(String gateway)
    {
        Set<String> gatewaySet = new HashSet<String>();

        if(gateway==null || gateway.equals("") || gateway.equals("null"))
        {
            gatewaySet.addAll(GatewayTypeService.getGateways());
        }
        else
        {
            gatewaySet.add(GatewayTypeService.getGatewayType(gateway).getGateway());
        }
        return gatewaySet;
    }

    private void validateOptionalParameter(HttpServletRequest req) throws ValidationException
    {
        InputValidator inputValidator = new InputValidator();
        List<InputFields> inputFieldsListMandatory = new ArrayList<InputFields>();
        inputFieldsListMandatory.add(InputFields.PAGENO);
        inputFieldsListMandatory.add(InputFields.RECORDS);
        inputFieldsListMandatory.add(InputFields.GATEWAY);
        inputFieldsListMandatory.add(InputFields.STATUS);
        inputFieldsListMandatory.add(InputFields.TERMINALID);

        inputValidator.InputValidations(req,inputFieldsListMandatory,true);
    }

    public String formatDate (String date, String initDateFormat, String endDateFormat) throws ParseException
    {
        Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        String parsedDate = formatter.format(initDate);
        return parsedDate;
    }

    public void generateSummary(HSSFSheet sheet, HSSFCellStyle style, HSSFCellStyle textBoldStyle,String startDate, String endDate, String gateway, String memberId, String terminalId)
    {
        logger.debug("Inside generateSummary");
        sheet.setColumnWidth((short) 1, (short) 202000);
        sheet.setColumnWidth((short) 2, (short) 202000);
        sheet.setColumnWidth((short) 3, (short) 202000);
        sheet.setColumnWidth((short) 4, (short) 202000);
        sheet.setColumnWidth((short) 5, (short) 202000);

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

        HSSFRow partner = sheet.createRow(3);
        HSSFCell partnerCell0 = partner.createCell((short) 1);
        HSSFCell partnerCell1 = partner.createCell((short) 2);
        partnerCell0.setCellStyle(textBoldStyle);
        partnerCell0.setCellValue("Member ID");
        partnerCell1.setCellStyle(style);
        partnerCell1.setCellValue(memberId);

        HSSFRow gateway1 = sheet.createRow(4);
        HSSFCell gatewayCell0 = gateway1.createCell((short) 1);
        HSSFCell gatewayCell1 = gateway1.createCell((short) 2);
        gatewayCell0.setCellStyle(textBoldStyle);
        gatewayCell0.setCellValue("Gateway");
        gatewayCell1.setCellStyle(style);
        String gatewayName = functions.getGatewayName(gateway);
        if (!functions.isValueNull(gatewayName)){
            gatewayCell1.setCellValue("-");
        }
        else {
            gatewayCell1.setCellValue(gatewayName);
        }

        HSSFRow currenct1 = sheet.createRow(5);
        HSSFCell currenctCell0 = currenct1.createCell((short) 1);
        HSSFCell currenctCell1 = currenct1.createCell((short) 2);
        currenctCell0.setCellStyle(textBoldStyle);
        currenctCell0.setCellValue("Currency");
        currenctCell1.setCellStyle(style);
        String currency = functions.getCurrency(gateway);
        if (!functions.isValueNull(currency)){
            currenctCell1.setCellValue("-");
        }
        else {
            currenctCell1.setCellValue(currency);
        }

        HSSFRow account = sheet.createRow(6);
        HSSFCell accountCell0 = account.createCell((short) 1);
        HSSFCell accountCell1 = account.createCell((short) 2);
        accountCell0.setCellStyle(textBoldStyle);
        accountCell0.setCellValue("Terminal ID");
        accountCell1.setCellStyle(style);
        if (!functions.isValueNull(terminalId)){
            accountCell1.setCellValue("-");
        }
        else {
            accountCell1.setCellValue(terminalId);
        }
    }

    public void generateConsolidatedReport(HSSFSheet sheet, HSSFCellStyle style, HSSFCellStyle textBoldStyle, String startDate, String endDate, Map<String, List<TransactionVO>> transactionVOMap, Map<String, List<TransactionVO>> transactionRefundVOMap, Set<String> gatewaySet, HashMap statusHash)
    {
        logger.debug("Inside generateConsolidatedReport");
        int rowno  = 1;
        String status = "";
        long count = 0;
        double statusPercentage = 0.00;
        double refcbkPercentage = 0.00;
        double amount = 0.0;
        double captureAmount = 0;
        double refundAmount = 0.00;
        double chargebackAmount = 0.00;
        double totalPercentage = 0.00;

        for (String gatewayCurrency : gatewaySet)
        {
            if (transactionVOMap != null && transactionVOMap.size() > 0)
            {
                logger.debug("transactionVOMap in excel===="+transactionVOMap);
                List<TransactionVO> transactionVOList = transactionVOMap.get(gatewayCurrency);
                logger.debug("transactionVOList in excel===="+transactionVOList);
                if (transactionVOList != null)
                {
                    long totalCount = 0;
                    double totalAmount = 0.0;
                    double totalCaptureAmount = 0.0;
                    double totalRefundAmount = 0.00;
                    double totalChargebackAmount = 0.00;

                    HSSFRow dateHeader1 = sheet.createRow(rowno);
                    sheet.addMergedRegion(new Region(rowno, (short) 1, rowno, (short) 7));
                    rowno++;
                    HSSFRow dateHeader = sheet.createRow(rowno);
                    sheet.addMergedRegion(new Region(rowno, (short) 1, rowno, (short) 7));
                    rowno++;
                    HSSFRow header1 = sheet.createRow(rowno);
                    rowno++;

                    sheet.setColumnWidth((short) 1, (short) 202000);
                    sheet.setColumnWidth((short) 2, (short) 202000);
                    sheet.setColumnWidth((short) 3, (short) 202000);
                    sheet.setColumnWidth((short) 4, (short) 202000);
                    sheet.setColumnWidth((short) 5, (short) 202000);
                    sheet.setColumnWidth((short) 6, (short) 202000);
                    sheet.setColumnWidth((short) 7, (short) 202000);

                    HSSFCell dateCell1 = dateHeader1.createCell((short) 1);
                    HSSFCell dateCell = dateHeader.createCell((short) 1);
                    HSSFCell cell11 = header1.createCell((short) 1);
                    HSSFCell cell22 = header1.createCell((short) 2);
                    HSSFCell cell33 = header1.createCell((short) 3);
                    HSSFCell cell44 = header1.createCell((short) 4);
                    HSSFCell cell55 = header1.createCell((short) 5);
                    HSSFCell cell66 = header1.createCell((short) 6);
                    HSSFCell cell77 = header1.createCell((short) 7);

                    dateCell1.setCellStyle(textBoldStyle);
                    dateCell.setCellStyle(textBoldStyle);
                    cell11.setCellStyle(textBoldStyle);
                    cell22.setCellStyle(textBoldStyle);
                    cell33.setCellStyle(textBoldStyle);
                    cell44.setCellStyle(textBoldStyle);
                    cell55.setCellStyle(textBoldStyle);
                    cell66.setCellStyle(textBoldStyle);
                    cell77.setCellStyle(textBoldStyle);

                    dateCell1.setCellValue(gatewayCurrency);
                    dateCell.setCellValue("Member Transactions Received (" + startDate + " - " + endDate + ")");
                    cell11.setCellValue("Status");
                    cell22.setCellValue("No of Transaction");
                    cell33.setCellValue("Amount");
                    cell44.setCellValue("Capture Amount");
                    cell55.setCellValue("Refund Amount");
                    cell66.setCellValue("Chargeback Amount");
                    cell77.setCellValue("Percentage");

                    for (TransactionVO transactionVO : transactionVOList)
                    {
                        HSSFRow row1 = sheet.createRow((short) rowno);

                        status = (String) statusHash.get(transactionVO.getStatus());
                        count = transactionVO.getCount();
                        amount = Double.valueOf(transactionVO.getAmount());
                        captureAmount = transactionVO.getCaptureAmount();
                        refundAmount = Double.parseDouble(transactionVO.getRefundAmount());
                        chargebackAmount = Double.parseDouble(transactionVO.getChargebackAmount());
                        statusPercentage = Double.parseDouble(Functions.round(transactionVO.getCount() / Double.parseDouble(transactionVO.getTotalTransCount()) * 100, 2));

                        totalCount = totalCount + count;
                        totalAmount = totalAmount + amount;
                        totalCaptureAmount = totalCaptureAmount + captureAmount;
                        totalRefundAmount = totalRefundAmount + refundAmount;
                        totalChargebackAmount = totalChargebackAmount + chargebackAmount;
                        totalPercentage = totalPercentage + statusPercentage;

                        HSSFCell cellr00 = row1.createCell((short) 1);
                        HSSFCell cellr11 = row1.createCell((short) 2);
                        HSSFCell cellr22 = row1.createCell((short) 3);
                        HSSFCell cellr33 = row1.createCell((short) 4);
                        HSSFCell cellr44 = row1.createCell((short) 5);
                        HSSFCell cellr55 = row1.createCell((short) 6);
                        HSSFCell cellr66 = row1.createCell((short) 7);

                        cellr00.setCellStyle(style);
                        cellr11.setCellStyle(style);
                        cellr22.setCellStyle(style);
                        cellr33.setCellStyle(style);
                        cellr44.setCellStyle(style);
                        cellr55.setCellStyle(style);
                        cellr66.setCellStyle(style);

                        cellr00.setCellValue(status);
                        cellr11.setCellValue(count);
                        cellr22.setCellValue(amount);
                        cellr33.setCellValue(captureAmount);
                        cellr44.setCellValue(transactionVO.getRefundAmount());
                        cellr55.setCellValue(transactionVO.getChargebackAmount());
                        cellr66.setCellValue(statusPercentage+"%");
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
                    HSSFCell totalRefund = header3.createCell((short) 5);
                    HSSFCell totalCharge = header3.createCell((short) 6);

                    status1.setCellStyle(textBoldStyle);
                    totalCountCell.setCellStyle(textBoldStyle);
                    totalAmountCell.setCellStyle(textBoldStyle);
                    totalCapture.setCellStyle(textBoldStyle);
                    totalRefund.setCellStyle(textBoldStyle);
                    totalCharge.setCellStyle(textBoldStyle);

                    status1.setCellValue("-");
                    totalCountCell.setCellValue(totalCount);
                    totalAmountCell.setCellValue(totalAmount);
                    totalCapture.setCellValue(totalCaptureAmount);
                    totalRefund.setCellValue(totalRefundAmount);
                    totalCharge.setCellValue(totalChargebackAmount);
                    rowno = rowno + 2;
                }
                rowno = rowno + 1;
            }
            else
            {
                HSSFRow noDataFoundRow = sheet.createRow(rowno);
                sheet.addMergedRegion(new Region(rowno, (short) 1, rowno, (short) 7));
                rowno++;
                HSSFCell noDataFoundCell = noDataFoundRow.createCell((short) 1);
                noDataFoundCell.setCellStyle(style);
                noDataFoundCell.setCellValue("No Transactions Received");
                rowno++;
            }

            if (transactionRefundVOMap != null && transactionRefundVOMap.size() > 0)
            {
                logger.debug("transactionRefundVOMap in excel===="+transactionRefundVOMap);
                List<TransactionVO> transactionVORefundList = transactionRefundVOMap.get(gatewayCurrency);
                logger.debug("transactionVORefundList in excel===="+transactionVORefundList);

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

                    dateCell.setCellValue("Member Refund and Chargeback Received (" + startDate + " - " + endDate + ")");
                    cell11.setCellValue("Status");
                    cell22.setCellValue("No of Transaction");
                    cell33.setCellValue("Amount");
                    cell44.setCellValue("Refund Amount");
                    cell55.setCellValue("Chargeback Amount");
                    cell66.setCellValue("Percentage");

                    for (TransactionVO transactionVO : transactionVORefundList)
                    {
                        HSSFRow row1 = sheet.createRow((short) rowno);

                        status = (String) statusHash.get(transactionVO.getStatus());
                        count = transactionVO.getCount();
                        amount = Double.valueOf(transactionVO.getAmount());
                        refundAmount = Double.valueOf(transactionVO.getRefundAmount());
                        chargebackAmount = Double.valueOf(transactionVO.getChargebackAmount());
                        refcbkPercentage = Double.parseDouble(Functions.round(transactionVO.getCount() / Double.parseDouble(transactionVO.getTotalTransCount()) * 100, 2));

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
                        cellr55.setCellValue(refcbkPercentage+"%");
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
    }
}
