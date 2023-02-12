package com.payment.cashflow.core;

import com.directi.pg.*;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZChargebackRequest;
import com.payment.request.PZFileVO;
import com.payment.request.PZTC40Record;
import com.payment.response.PZChargebackRecord;
import com.payment.response.PZChargebackResponse;
import com.payment.response.PZResponseStatus;
import com.payment.response.PZSettlementRecord;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by
 * blue team
 * 22/7/2016
 */
public class CashFlowPaymentProcess extends CommonPaymentProcess
{
    private static Logger logger = new Logger(CashFlowPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(CashFlowPaymentProcess.class.getName());

    public static void main(String[] args) throws SystemError
    {
        //System.out.println("Inside main method---");

        String filePath = "C:\\Users\\ThinkPadT410\\Desktop\\Chargeback_Demo\\final testing files\\cb_cashflow3.xls";

        PZFileVO pzFileVO = new PZFileVO();
        pzFileVO.setFilepath(filePath);
       // System.out.println("filepath---" + pzFileVO.getFilepath());

        CashFlowPaymentProcess cashFlowPaymentProcess = new CashFlowPaymentProcess();

        List<PZChargebackRecord> pzChargebackRecords = cashFlowPaymentProcess.readChargebackFile(pzFileVO);

        for (PZChargebackRecord pzChargebackRecord : pzChargebackRecords)
        {
           /* System.out.println("response----" + pzChargebackRecord.getPaymentid() + "--" + pzChargebackRecord.getChargebackAmount());
            System.out.println("size---" + pzChargebackRecords.size());*/
        }
    }

    public List<PZTC40Record> readTC40file(PZFileVO fileName) throws SystemError
    {
        logger.debug("Entering in Cashflow readSettlementFile");
        transactionLogger.debug("Entering in Cashflow readSettlementFile");

        InputStream inputStream = null;
        List<PZTC40Record> vList = new ArrayList<PZTC40Record>();

        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(fileName.getFilepath()));
            POIFSFileSystem fs = new POIFSFileSystem(inputStream);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);

            if (inputStream == null)
            {
                throw new SystemError("TC40 File is not found");
            }

            // get rows of sheet
            Iterator rows = sheet.rowIterator();

            int cntRowToLeaveFromTop = 0;
            int cntTotalRecords = 0;
            boolean isFileProceeding = false;

            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;

                // Skip the header rows.
                if (cntRowToLeaveFromTop == 1)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if (!isFileProceeding)
            {
                throw new SystemError("ERROR: Invalid File format/Records not found");
            }

            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntTotalRecords++;
                Iterator cells = row.cellIterator();

                if(null!=row.getCell((short) 6).getStringCellValue() && !("").equals(row.getCell((short) 6).getStringCellValue()))
                {
                    PZTC40Record pztc40Record = new PZTC40Record();
                    pztc40Record.setOrderno(row.getCell((short) 6).getStringCellValue() + "");

                    vList.add(pztc40Record);
                }
            }
        }
        catch (NullPointerException b)
        {
            logger.error("NullPointerException--->",b);
        }
        catch (FileNotFoundException e1)
        {
            logger.error("FileNotFoundException--->", e1);
        }
        catch (IOException ie)
        {
            logger.error("IOException--->", ie);
        }

        return vList;
    }

    public List<PZChargebackRecord> readChargebackFile(PZFileVO fileName) throws SystemError
    {
        logger.debug("Inside readChargebackFile in CashFlowPaymentProcess :::");

        List<PZChargebackRecord> pzChargebackRecordList = new ArrayList<PZChargebackRecord>();

        try
        {
            //Create a new workbook instance
            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(fileName.getFilepath()));

            //Get first sheet from the workbook
            HSSFSheet sheet = workbook.getSheetAt(0);

            //Iterate through each rows one by one
            Iterator rows = sheet.rowIterator();

            int cntRowToLeaveFromTop = 0;
            int cntTotalRecords = 0;
            boolean isFileProcceding = false;

            //Skip header rows
            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                if (cntRowToLeaveFromTop == 1)
                {
                    isFileProcceding = true;
                    break;
                }
            }

            if (!isFileProcceding)
            {
                throw new SystemError("ERROR: Invalid File format/Records not found");
            }

            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntTotalRecords++;

                //For each row, iterate through all the columns
                if (null != row.getCell((short) 6).getStringCellValue() && !("").equals(row.getCell((short) 6).getStringCellValue()))
                {
                    PZChargebackRecord loadTransResponse = new PZChargebackRecord();

                    loadTransResponse.setPaymentid(row.getCell((short) 6).getStringCellValue() + "");
                    loadTransResponse.setChargebackAmount(row.getCell((short) 10).getNumericCellValue() + "");

                    pzChargebackRecordList.add(loadTransResponse);
                    logger.debug("Size of Chargeback List---" + pzChargebackRecordList.size());
                }
            }
            logger.debug("Totalsize---" + cntTotalRecords);
        }
        catch (NullPointerException b)
        {
            logger.error("NullPointerException--->", b);
        }
        catch (FileNotFoundException e)
        {
            logger.error("FileNotFoundException--->", e);
        }
        catch (IOException e)
        {
            logger.error("IOException--->", e);
        }
        return pzChargebackRecordList;
    }

    /*public static void main(String[] args) throws SystemError
    {
        System.out.println("Inside in---");

        String filepath = "D:\\Tc40Cashflow.xls";

        PZFileVO pzFileVO = new PZFileVO();
        pzFileVO.setFilepath(filepath);

        CashFlowPaymentProcess cashFlowPaymentProcess = new CashFlowPaymentProcess();

        List<PZTC40Record> pztc40Records = cashFlowPaymentProcess.readTC40file(pzFileVO);

        System.out.println("chargebackFile----"+pztc40Records.size());

        for(PZTC40Record pztc40Record : pztc40Records)
        {
            System.out.println("paymentid--"+pztc40Record.getOrderno());

        }
    }*/

    public StringBuffer processChargeback(List<PZChargebackRecord> vTransactions, String gateway, AuditTrailVO auditTrailVO)
    {

        StringBuffer retuenMsg = new StringBuffer();
        String mailStatus = "Fail";
        Functions functions = new Functions();
        TransactionManager transactionManager = new TransactionManager();

        retuenMsg.append("<br><br><b>   Payment Id    |  Merchant Id  |   Account Id  |  Amount  |  CB Amount  |    Status   |   Status Description    |   Previous Status   |   Current Status   " + "</b>\r\n");
        for (PZChargebackRecord record : vTransactions)
        {
            PZChargebackRequest request = new PZChargebackRequest();
            CommonPaymentProcess commProcess = new CommonPaymentProcess();

            String trackingId = "";
            String paymentId = "";
            String amount = "";
            String cbamount = "";
            String transStatus = "";
            String toId = "";
            String accountId = "";
            Connection conn = null;

            try
            {
                if (functions.isValueNull(record.getPaymentid()))
                {
                    paymentId = record.getPaymentid();
                }
                else
                {
                    retuenMsg.append("<br>" + paymentId + "  |    -   |  " + accountId + "  | " + record.getAmount() + "  |  -  |  " + "   failed   |    Missing PaymentId   |  -  | - \r\n");
                    continue;
                }

                if (functions.isValueNull(record.getChargebackAmount()))
                {
                    cbamount = record.getChargebackAmount();
                }
                else
                {
                    retuenMsg.append("<br>" + paymentId + "  |  -  |  " + accountId + "   |    " + amount + "  |  " + record.getChargebackAmount() + "  |  " + "   failed   |    Missing chargeback amount  | - | - \r\n");
                    continue;
                }

                TransactionDetailsVO transactionDetailsVO = transactionManager.getCommonTransactionDetailsForChargeBackByBankId(paymentId);
                if (transactionDetailsVO != null)
                {
                    trackingId = transactionDetailsVO.getTrackingid();
                    toId = transactionDetailsVO.getToid();
                    transStatus = transactionDetailsVO.getStatus();
                    amount = transactionDetailsVO.getAmount();
                }
                else
                {
                    //Tracking ID exist in DB or Not
                    retuenMsg.append("<br>" + paymentId + "  |    -   |  " + accountId + " |  " + amount + "  |  -  |  " + "   failed   |    Record not found  | - | - \r\n");
                    continue;
                }

                request.setPaymentid(paymentId);
                request.setTrackingId(Integer.valueOf(trackingId));
                request.setAccountId(Integer.parseInt(accountId));
                request.setMemberId(Integer.parseInt(toId));
                request.setCbAmount(cbamount);
                request.setCbReason(record.getChargebackReason());
                request.setAdmin(true);
                request.setAuditTrailVO(auditTrailVO);

                PZChargebackResponse response = commProcess.doChargeback(request);
                if (response != null && (response.getStatus()).equals(PZResponseStatus.SUCCESS))
                {
                    retuenMsg.append("<br>" + paymentId + "  |  " + toId + "  |  " + accountId + "  |  " + amount + "  |  " + String.format("%.2f", Double.valueOf(record.getChargebackAmount())) + "  |  " + "   success   |   " + response.getResponseDesceiption() + "  |  " + transStatus + "  |  " + "  chargeback \r\n");
                    mailStatus = "success";
                }
                else
                {
                    if (response.getDbStatus() == null)
                    {
                        retuenMsg.append("<br>" + paymentId + "  |  " + toId + "  |  " + accountId + "  |  " + amount + "  |  " + String.format("%.2f", Double.valueOf(record.getChargebackAmount())) + "  |  " + "   failed   |   " + response.getResponseDesceiption() + "  |  " + transStatus + "  |  - \r\n");
                    }
                    else
                    {
                        retuenMsg.append("<br>" + paymentId + "  |  " + toId + "  |  " + accountId + "  |  " + amount + "  |  " + String.format("%.2f", Double.valueOf(record.getChargebackAmount())) + "  |  " + "   failed   |   " + response.getResponseDesceiption() + "  |  " + response.getDbStatus() + "  |  " + response.getDbStatus() + "\r\n");
                    }
                    continue;
                }
                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.CHARGEBACK_TRANSACTION, trackingId, mailStatus, request.getCbReason(), null);
            }
            catch (PZDBViolationException se)
            {
                logger.error("SQLException:::::", se);
            }
        }
        return retuenMsg;
    }

}

