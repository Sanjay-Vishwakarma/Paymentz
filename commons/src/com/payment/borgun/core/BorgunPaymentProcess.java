package com.payment.borgun.core;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.SendTransactionEventMailUtil;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.*;
import com.payment.response.*;
import com.payment.statussync.StatusSyncDAO;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import javax.xml.crypto.Data;
import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 10/5/13
 * Time: 3:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class BorgunPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(BorgunPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(BorgunPaymentProcess.class.getName());
    private Functions functions = new Functions();

    public static void main(String[] args)
    {
        //System.out.println("inside main method :::");

        String filepath = "C:\\Users\\ThinkPadT410\\Desktop\\Chargeback_Demo\\final testing files\\ChargebackGrid_Borgun042.xls";

        PZFileVO fileVO = new PZFileVO();
        fileVO.setFilepath(filepath);

        //System.out.println("filepath---" + fileVO.getFilepath());

        BorgunPaymentProcess borgunPaymentProcess = new BorgunPaymentProcess();

        List<PZChargebackRecord> pzChargebackRecords = borgunPaymentProcess.readChargebackFile(fileVO);

        for (PZChargebackRecord pzChargebackRecord : pzChargebackRecords)
        {
            //System.out.println("response----" + pzChargebackRecord.getRrn() + "--" + pzChargebackRecord.getChargebackAmount());
        }
    }

    public List<PZSettlementRecord> readSettlementFile(PZSettlementFile fileName) throws SystemError
    {
        InputStream inputStream = null;
        List<PZSettlementRecord> vList = new ArrayList<PZSettlementRecord>();

        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(fileName.getFilepath()));
            POIFSFileSystem fs = new POIFSFileSystem(inputStream);
            HSSFWorkbook wb = new HSSFWorkbook(fs);
            HSSFSheet sheet = wb.getSheetAt(0);

            if (inputStream == null)
            {
                throw new SystemError("Settlement File is not found");
            }

            // get rows of sheet
            Iterator rows = sheet.rowIterator();

            int cntRowToLeaveFromTop=0;
            int cntTotalRecords=0;
            boolean isFileProceeding = false;
            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntRowToLeaveFromTop++;
                // Skip the header rows.
                if (cntRowToLeaveFromTop==1)
                {
                    isFileProceeding = true;
                    break;
                }
            }
            if(!isFileProceeding)
            {
                throw new SystemError("Settlement Cron Error : Invalid File Format");
            }
            while(rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntTotalRecords++;
                Iterator cells = row.cellIterator();
                PZSettlementRecord loadTransResponse=new PZSettlementRecord();
                String trackingId=((HSSFCell) row.getCell((short)2)).getStringCellValue() + "";
                String Amount=(float)((HSSFCell) row.getCell((short)4)).getNumericCellValue()+"";
              /* if((((HSSFCell) row.getCell((short)2)).getStringCellValue())==null || ((float)((HSSFCell) row.getCell((short)4)).getNumericCellValue()).equals(""))
                {
                    continue;
                }*/
                String vTransactionType= row.getCell((short)4).getNumericCellValue() >= 0?"Sale":"Refund";
                if(vTransactionType.equals("Sale"))
                {
                    loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.SETTLED));
                }
                else if(vTransactionType.equals("Refund"))
                {
                    loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.REVERSED));
                }
                //loadTransResponse.setSDateTime(((HSSFCell) row.getCell((short)1)).getStringCellValue()+"");
                String sAmount = row.getCell((short)4).getNumericCellValue() >= 0?(float)((HSSFCell) row.getCell((short)4)).getNumericCellValue()+"":((-1)* row.getCell((short)4).getNumericCellValue())+"";
                loadTransResponse.setAmount(sAmount);
                loadTransResponse.setMerchantTXNid(((HSSFCell) row.getCell((short)2)).getStringCellValue());
                loadTransResponse.setPaymentid(((HSSFCell) row.getCell((short)0)).getStringCellValue()+"");

                if(loadTransResponse!= null)
                {
                    vList.add(loadTransResponse);

                }
            }
        }
        catch (IOException e)
        {
            transactionLogger.error("IOException readSettlementFile ",e);
            throw new SystemError("ERROR: Settlement File cant be read");
        }
        finally
        {

            if (inputStream != null)
            {
                try
                {
                    inputStream.close();
                }
                catch (IOException e)
                {
                    throw new SystemError("ERROR: Settlement File cant be closed successfully");
                }

            }
        }
        return vList;
    }

    public String processSettlement(int accountid, List<PZSettlementRecord> vTransactions, String toAddress, AuditTrailVO auditTrailVO,String tablename)
    {
        StringBuffer errString = new StringBuffer();
        errString.append("<br>|  Tracking ID  |   Merchant ID  |   Account ID  |        Order ID       |  Amount  |  Current Status  |      Treatment Given     |" + "\r\n");

        TransactionEntry transactionEntry = null;
        Transaction transaction = null;
        Connection conn = null;
        try
        {
            transactionEntry = new TransactionEntry();
            transaction = new Transaction();
            conn = Database.getConnection();
        }
        catch (Exception e)
        {
            log.error("Common Settlement Cron for ArenaPlus Processor : Error : " + e.getMessage());
            transactionLogger.error("Common Settlement Cron for ArenaPlus Processor : Error : " + e.getMessage());
            return e.getMessage();
        }
        List vErrRecords = new ArrayList();
        int vSuccessTotalCount = 0;
        int vRefundTotalCount = 0;
        int vChargebackTotalCount = 0;
        int vAuthFailedTotalCount = 0;
        int vProcessingFailedCount = 0;

        for (PZSettlementRecord vSettleTransVO : vTransactions)
        {
            int trackingId = 0;
            try
            {
                //String vPaymentOrderId = vSettleTransVO.getPaymentid();
                String vPaymentOrderId = null;
                String vTrackingId = vSettleTransVO.getMerchantTXNid();
                String vAmount = vSettleTransVO.getAmount();
                conn=Database.getConnection();

                String query = "select toid,fromid,description,captureamount,amount,trackingid,accountid,status,paymentid from transaction_common where trackingid=?";
                PreparedStatement p1 = conn.prepareStatement(query);
                p1.setString(1, vTrackingId);
                ResultSet rs = p1.executeQuery();
                boolean isRecordFound = false;
                isRecordFound = rs.next();

                if (isRecordFound)
                {
                    int toid = rs.getInt("toid");
                    String fromid = rs.getString("fromid");
                    String description = rs.getString("description");
                    trackingId = rs.getInt("trackingid");
                    int accountId = rs.getInt("accountid");
                    vPaymentOrderId = rs.getString("paymentid");

                    //To be used to updated detail table
                    CommResponseVO commResponseVO = new CommResponseVO();
                    commResponseVO.setErrorCode(vPaymentOrderId);
                    commResponseVO.setDescription("Updated via gateway Settlement cron");
                    commResponseVO.setTransactionId(vPaymentOrderId);
                    //Processing Failed Transactions
                    if (vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.AUTH_FAILED))
                            || vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.CAPTURE_FAILED)))
                    {
                        //Variable Definition
                        if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.AUTH_FAILED))
                                || rs.getString("status").equals(String.valueOf(PZTransactionStatus.CAPTURE_FAILED)))
                        {
                            BigDecimal amount = new BigDecimal(rs.getString("amount"));
                            amount.setScale(2, BigDecimal.ROUND_HALF_UP);
                            transactionEntry.newGenericFailedTransaction(String.valueOf(trackingId), String.valueOf(accountId), null);
                            vSuccessTotalCount++;
                            errString.append("<br>" + trackingId + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Transaction Settled" + "\r\n");
                        }
                        else if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.AUTH_STARTED))
                                || rs.getString("status").equals(String.valueOf(PZTransactionStatus.CAPTURE_STARTED)))
                        {

                            BigDecimal amount = new BigDecimal(rs.getString("amount"));
                            amount.setScale(2, BigDecimal.ROUND_HALF_UP);


                            //update the status, PaymentOrderNumber and remark column for this transaction
                            if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.AUTH_STARTED)))
                                query = "update transaction_common set paymentid=?, status='authfailed', remark='Bank Connectivity Issue' where trackingid=? ";
                            else
                                query = "update transaction_common set paymentid=?, status='capturefailed', remark='Bank Connectivity Issue' where trackingid=? ";
                            PreparedStatement ps = conn.prepareStatement(query);
                            ps.setString(1, vPaymentOrderId);
                            ps.setString(2, vTrackingId);
                            int result = ps.executeUpdate();

                            if (result == 1)
                            {

                                //insert the status change action entry for authfailed to details table
                                int num = actionEntry(vTrackingId, amount.toString(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, null,auditTrailVO,null);
                                if (num == 1)
                                {
                                    //now as it is in normal authfailed state, you may apply failure charges
                                    transactionEntry.newGenericFailedTransaction(String.valueOf(trackingId), String.valueOf(accountId), null);
                                    vSuccessTotalCount++;
                                    errString.append("<br>" + trackingId + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Status Correction done. Transaction Settled" + "\r\n");
                                }

                            }

                        }
                        else
                        {
                            vErrRecords.add(trackingId);
                            errString.append("<br>" + vTrackingId + "  |  " + vSettleTransVO.getPaymentid() + "  |  " + "Transaction NOT FOUND" + "\r\n");
                        }

                    }
                    //Processing Successful Transactions which are not Refunded or not Chargebacked
                    if (vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.SETTLED))
                            || vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.CAPTURE_SUCCESS)))
                    {
                        if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.CAPTURE_SUCCESS)))
                        {
                            BigDecimal amount = new BigDecimal(rs.getString("captureamount"));
                            amount.setScale(2, BigDecimal.ROUND_HALF_UP);

                            GatewayAccount vGatewayAccount = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
                            transactionEntry.newGenericCreditTransaction(String.valueOf(trackingId), amount, String.valueOf(accountId), null,auditTrailVO);
                            vSuccessTotalCount++;
                            errString.append("<br>" + trackingId + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Transaction Settled" + "\r\n");


                        }

                        else if (rs.getString("status").equals(String.valueOf(PZTransactionStatus.AUTH_STARTED))
                                || rs.getString("status").equals(String.valueOf(PZTransactionStatus.CAPTURE_STARTED)))
                        {

                            BigDecimal amount = new BigDecimal(vAmount);
                            amount.setScale(2, BigDecimal.ROUND_HALF_UP);

                            //update the status,captureamount, PaymentOrderNumber and remark column for this transaction
                            query = "update transaction_common set paymentid=?, status='capturesuccess',captureamount=?, remark='Settled via gateway report' where trackingid=? ";
                            PreparedStatement ps = conn.prepareStatement(query);
                            ps.setString(1, vPaymentOrderId);
                            ps.setString(2, amount.toString());
                            ps.setString(3, vTrackingId);
                            int result = ps.executeUpdate();

                            if (result == 1)
                            {
                                //insert the status change action entry for capture success to details table
                                int num = actionEntry(vTrackingId, amount.toString(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, null,auditTrailVO);
                                if (num == 1)
                                {
                                    //now as it is in normal capture sucess state, you may apply settle charges
                                    GatewayAccount vGatewayAccount = GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
                                    transactionEntry.newGenericCreditTransaction(String.valueOf(trackingId), amount, String.valueOf(accountId), null,auditTrailVO);
                                    vSuccessTotalCount++;
                                    errString.append("<br>" + trackingId + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Transaction Settled" + "\r\n");


                                }
                            }

                        }
                        else
                        {
                            vErrRecords.add(trackingId);
                            errString.append("<br>" + vTrackingId + "  |  " + vSettleTransVO.getPaymentid() + "  |  " + "Transaction NOT FOUND" + "\r\n");
                        }


                    }
                    ////Processing Chargeback Transactions
                    if (vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.CHARGEBACK)))
                    {
                        errString.append("<br>" + vTrackingId + "  |  " + vSettleTransVO.getPaymentid() + "  |  " + "Transaction Must be in Chargeback Status" + "\r\n");
                    }
                    //Processing Refund
                    if (vSettleTransVO.getStatusDetail().equals(String.valueOf(PZTransactionStatus.REVERSED)))
                    {
                        if (!rs.getString("status").equals("reversed"))
                        {
                            errString.append("<br>" + vTrackingId + "  |  " + vSettleTransVO.getPaymentid() + "  |  " + "Transaction Must be in Reverse Status" + "\r\n");
                        }
                    }

                }
                else
                {

                    vErrRecords.add(trackingId);
                    errString.append("<br>" + vTrackingId + "  |  " + vSettleTransVO.getPaymentid() + "  |  " + "Transaction NOT FOUND" + "\r\n");
                }


            }
            catch (Exception e)
            {
                log.error("ERROR", e);
                log.error("[CommonSettlementCron] : Error while settling the transaction for Tracking ID = " + trackingId + ". Exception =" + e);

                transactionLogger.error("ERROR", e);
                transactionLogger.error("[CommonSettlementCron] : Error while settling the transaction for Tracking ID = " + trackingId + ". Exception =" + e);
                vErrRecords.add(trackingId);
            }
            finally
            {
                Database.closeConnection(conn);
                //transactionEntry.closeConnection();
            }
        }
        String vMailSend = "";
        try
        {
            transaction.sendSettlementCron(errString, toAddress);
        }
        catch (SystemError systemError)
        {
            log.error("[CommonSettlementCron] : Error while sending  the settled transaction mail");
            transactionLogger.error("[CommonSettlementCron] : Error while sending  the settled transaction mail");
            vMailSend = "Mail Sending to admin is failed.";
        }
        return "Settlement File uploaded successfully. " + vMailSend + "\r\n" + errString.toString();
    }

    public StringBuffer processChargeback(List<PZChargebackRecord> vTransactions, String gateway,AuditTrailVO auditTrailVO)
    {
        StringBuffer msg = new StringBuffer();
        String mailStatus = "";
        msg.append("<br><br><b>Payment Id  |   Merchant ID  |   Account ID  |  Amount  |  CB Amount  |    Status   |   StatusDescription    |   Previous Status   |   Current Status   " + "</b>\r\n");

        CommonPaymentProcess commProcess = new CommonPaymentProcess();
        TransactionManager transactionManager = new TransactionManager();

        String trackingId = "";
        String RRN = "";
        String amount = "";
        String chargebackAmount = "";
        String transStatus = "";
        String toId = "";
        String accountId = "";

        for (PZChargebackRecord record : vTransactions)
        {
            try
            {
                if (functions.isValueNull(record.getRrn()))
                {
                    RRN = record.getRrn();
                }
                else
                {
                    msg.append("<br>" + RRN + "  |    -   |  " + "  |    -   |  " + "  |   -   |  " + record.getChargebackAmount() + "  |  " + "   failed   |    Payment Id Missing   |  -  |   -  \r\n");
                    continue;
                }

                if (functions.isValueNull(record.getChargebackAmount()))
                {
                    chargebackAmount = record.getChargebackAmount();
                }
                else
                {
                    msg.append("<br>" + RRN + "  |    -   |  " + "  |    -   |  " + "  |   -   |  " + record.getChargebackAmount() + "  |  " + "   failed   |    Chargeback amount is missing   |  -  |   -  \r\n");
                    continue;
                }

                TransactionDetailsVO transactionDetailsVO = transactionManager.getBorgunCommonTransactionDetailsForChargeBack(RRN);
                if (transactionDetailsVO != null)
                {
                    toId = transactionDetailsVO.getToid();
                    trackingId = transactionDetailsVO.getTrackingid();
                    transStatus = transactionDetailsVO.getStatus();
                    accountId = transactionDetailsVO.getAccountId();
                    amount = transactionDetailsVO.getAmount();
                }
                else
                {
                    msg.append("<br>" + RRN + "  |    -   |  " + "  |    -   |  " + "  |  " + amount + "  |  " + record.getChargebackAmount() + "  |  " + "   failed   |    Record not found   |   -  |   -  \r\n");
                    continue;
                }

                PZChargebackRequest request = new PZChargebackRequest();
                request.setPaymentid(RRN);
                request.setTrackingId(Integer.parseInt(trackingId));
                request.setAccountId(Integer.parseInt(accountId));
                request.setMemberId(Integer.parseInt(toId));
                request.setCbAmount(chargebackAmount);
                request.setCbReason(record.getChargebackReason());
                request.setAdmin(true);
                request.setAuditTrailVO(auditTrailVO);

                PZChargebackResponse pzChargebackResponse = commProcess.doChargeback(request);

                if (pzChargebackResponse != null && (pzChargebackResponse.getStatus()).equals(PZResponseStatus.SUCCESS))
                {
                    msg.append("<br>" + RRN + "  |  " + toId + "  |  " + accountId + "  |  " + amount + "  |  " + String.format("%.2f", Double.valueOf(record.getChargebackAmount())) + "  |  " + "   success   |   " + pzChargebackResponse.getResponseDesceiption() + "  |  " + transStatus + "  |  " + "  chargeback \r\n");
                    mailStatus = "success";
                }
                else
                {
                    if (pzChargebackResponse.getDbStatus() == null)
                    {
                        msg.append("<br>" + RRN + "  |  " + toId + "  |  " + accountId + "  |  " + amount + "  |  " + String.format("%.2f", Double.valueOf(record.getChargebackAmount())) + "  |  " + "   failed   |   " + pzChargebackResponse.getResponseDesceiption() + "  |  " + transStatus + "  |  - \r\n");
                    }
                    else
                    {
                        msg.append("<br>" + RRN + "  |  " + toId + "  |  " + accountId + "  |  " + amount + "  |  " + String.format("%.2f", Double.valueOf(record.getChargebackAmount())) + "  |  " + "   failed   |   " + pzChargebackResponse.getResponseDesceiption() + "  |  " + pzChargebackResponse.getDbStatus() + "  |  " + pzChargebackResponse.getDbStatus() + "\r\n");
                    }
                    continue;
                }
                AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                asynchronousMailService.sendEmail(MailEventEnum.CHARGEBACK_TRANSACTION, trackingId, mailStatus, request.getCbReason(), null);
            }
            catch (PZDBViolationException se)
            {
                log.error("SQLException:::::", se);
            }
        }
        return msg;
    }

    public List<PZChargebackRecord> readChargebackFile(PZFileVO fileName)
    {
        List<PZChargebackRecord> pzChargebackRecordList = new ArrayList();
        try
        {
            //Create a new workbook instance.
            HSSFWorkbook workbook = new HSSFWorkbook(new FileInputStream(fileName.getFilepath()));
            //Get first sheet from the workbook.
            HSSFSheet sheet = workbook.getSheetAt(0);
            //Iterator through each row one by one.
            Iterator rows = sheet.rowIterator();
            int cntRowToLeaveFromTop = 0;
            boolean isFileProceeding = false;
            //Skip header rows.
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
                    loadTransactions.setRrn(row.getCell((short) 8).getStringCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setRrn(row.getCell((short) 8).getStringCellValue() + "");
                }
                try
                {
                    loadTransactions.setChargebackAmount(row.getCell((short) 5).getNumericCellValue() + "");
                }
                catch (NumberFormatException nfe)
                {
                    loadTransactions.setChargebackAmount(row.getCell((short) 5).getStringCellValue() + "");
                }
                loadTransactions.setChargebackReason(row.getCell((short) 9).getStringCellValue());
                pzChargebackRecordList.add(loadTransactions);
            }
        }
        catch (IOException e)
        {
            log.error("IOException:::::", e);
        }
        catch (SystemError systemError)
        {
            log.error("SystemError::::", systemError);
        }
        return pzChargebackRecordList;
    }

    public  List<PZTC40Record> readTC40file(PZFileVO fileName) throws SystemError
    {
        log.debug("Entering in PayForAsia readSettlementFile");
        transactionLogger.debug("Entering in PayForAsia readSettlementFile");

        InputStream inputStream = null;
        List<PZTC40Record> vList = new ArrayList<PZTC40Record>();

        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(fileName.getFileName()));
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
                throw new SystemError("Invalid File Format");
            }

            while (rows.hasNext())
            {
                HSSFRow row = (HSSFRow) rows.next();
                cntTotalRecords++;
                Iterator cells = row.cellIterator();

                if(null!=row.getCell((short) 6).getStringCellValue() && !("").equals(row.getCell((short) 6).getStringCellValue()))
                {
                    PZTC40Record pztc40Record = new PZTC40Record();
                    pztc40Record.setRrn(row.getCell((short) 6).getStringCellValue() + "");

                    vList.add(pztc40Record);
                }
            }
            log.debug("Total Count Size---"+cntTotalRecords);
        }
        catch (NullPointerException b)
        {
            log.error("NullPointerException--->",b);
        }
        catch (FileNotFoundException e1)
        {
            log.error("FileNotFoundException--->",e1);
        }
        catch (IOException ie)
        {
            log.error("IOException--->", ie);
        }

        return vList;
    }

    public StringBuffer processFraudulentTransactions(int accountid, List<PZTC40Record> vTransactions, String toAddress, String isRefund)
    {
        StringBuffer eString = new StringBuffer();
        StringBuffer errString = new StringBuffer();
        errString.append("<br><b>  Tracking ID  |   Merchant ID  |   Account ID  |   Order ID   |   Remark   </b>");

        StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
        Connection conn = null;
        PZRefundRequest refundRequest = new PZRefundRequest();
        PZRefundResponse response = new PZRefundResponse();
        AuditTrailVO auditTrailVO = new AuditTrailVO();

        for(PZTC40Record pztc40Record : vTransactions)
        {
            log.debug("Display error message-----");
            String paymentId = "";
            if (functions.isValueNull(pztc40Record.getOrderno()))
            {
                paymentId = pztc40Record.getOrderno();
            }
            else
            {
                errString.append("<br>  -  |   -  |   -  |   -   |   Invalid/Missing Payment Order Number   ");
                continue;
            }

            try
            {
                conn = Database.getConnection();
                String query = "select toid,description,captureamount,trackingid,status from transaction_common where paymentid=? and accountid=?";
                PreparedStatement pstmt = conn.prepareStatement(query);
                pstmt.setString(1, paymentId);
                pstmt.setString(2, String.valueOf(accountid));
                log.debug("Get details from transaction_common---" + pstmt);
                ResultSet res = pstmt.executeQuery();
                if (res.next())
                {
                    continue;
                }
                else
                {
                    eString.append("Selected Bank Account does not match with the provided TC40 File transaction records");
                    //break;
                    return eString;
                }
            }
            catch (Exception e)
            {
                log.error("Exception---",e);
            }
            finally
            {
                Database.closeConnection(conn);
            }
            return eString;
        }

        try
        {
            conn = Database.getConnection();

            auditTrailVO.setActionExecutorId("0");
            auditTrailVO.setActionExecutorName("Upload Bulk Fraud");
            for (PZTC40Record pztc40Record : vTransactions)
            {

                String rrn = "";
                if (functions.isValueNull(pztc40Record.getRrn()))
                {
                    rrn = pztc40Record.getRrn();
                }
                else
                {
                    errString.append("<br>  -  |   -  |   -  |   -   |   Invalid/Missing Payment Order Number   ");
                    continue;
                }

                String toid = "";
                String description = "";
                String captureAmount = "";
                String trackingid = "";
                String status = "";

                String query = "SELECT tc.toid,tc.description,tc.captureamount,tc.trackingid,tc.status FROM transaction_common AS tc JOIN transaction_borgun_details AS tb WHERE tc.trackingid = tb.trackingid AND tb.rrn =?";
                PreparedStatement p1 = conn.prepareStatement(query);
                p1.setString(1, rrn);
                ResultSet rs = p1.executeQuery();

                if(rs.next())
                {
                    toid = rs.getString("toid");
                    description = rs.getString("description");
                    captureAmount = rs.getString("captureamount");
                    trackingid = rs.getString("trackingid");
                    status = rs.getString("status");
                }
                else
                {
                    errString.append("<br>  -  |   -  |   -  |   -   |   Transaction not Found   ");
                    continue;
                }

                if("Y".equalsIgnoreCase(isRefund))
                {
                    refundRequest.setMemberId(Integer.valueOf(toid));
                    refundRequest.setAccountId(Integer.valueOf(accountid));
                    refundRequest.setTrackingId(Integer.parseInt(trackingid));
                    refundRequest.setRefundAmount(captureAmount);
                    refundRequest.setRefundReason("Fraudulent Transaction");
                    refundRequest.setAdmin(true);
                    refundRequest.setIpAddress("");
                    refundRequest.setFraud(true);
                    refundRequest.setAuditTrailVO(auditTrailVO);

                    response = refund(refundRequest);

                    PZResponseStatus refundStatus = response.getStatus();
                    String mailStatus = "";
                    if (response != null && PZResponseStatus.SUCCESS.equals(refundStatus))
                    {
                        errString.append("<br>  " + trackingid + "  |   " + toid + "  |   " + accountid + "  |   " + description + "   |   "+response.getResponseDesceiption()+"   ");
                        //update ststus
                        statusSyncDAO.updateAllRefundTransactionFlowFlag(trackingid,"fraud",conn);
                        mailStatus = "successful";
                    }
                    else
                    {
                        mailStatus = "failed";
                        errString.append("<br>  " + trackingid + "  |   " + toid + "  |   " + accountid + "  |   " + description + "   |   "+response.getResponseDesceiption()+"   ");
                        continue;
                    }

                    SendTransactionEventMailUtil sendTransactionEventMail=new SendTransactionEventMailUtil();
                    sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.REFUND_TRANSACTION,trackingid,mailStatus,"Refund Fraudulent Transaction",null);
                }
                else
                {
                    statusSyncDAO.updateAllFraudulantTransactionFlowFlag(trackingid, "fraud", conn);
                    errString.append("<br>|  " + trackingid + "  |   " + toid + "  |   " + accountid + "  |   " + description + "   |   Transaction marked as Fraud   |");

                    SendTransactionEventMailUtil sendTransactionEventMail=new SendTransactionEventMailUtil();
                    sendTransactionEventMail.sendTransactionEventMail(MailEventEnum.FRAUD_TRANSACTION_MARKED,trackingid,"","Fraudulent Transaction Marked",null);
                }

            }
        }
        catch (SystemError se)
        {
            log.error("Exception in processFradulantTransactions---",se);
        }
        catch (SQLException se)
        {
            log.error("Exception in processFradulantTransactions---",se);
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return errString;
    }


    /*  public static void main(String[] args) throws SystemError
    {
        System.out.println("Inside in---");

        String filepath = "D:\\borgun tc40.xls";

        PZFileVO pzFileVO = new PZFileVO();
        pzFileVO.setFilepath(filepath);

        BorgunPaymentProcess borgunPaymentProcess = new BorgunPaymentProcess();

        List<PZTC40Record> pztc40Records = borgunPaymentProcess.readTC40file(pzFileVO);

        System.out.println("chargebackFile----"+pztc40Records.size());

        for(PZTC40Record pztc40Record : pztc40Records)
        {
            System.out.println("paymentid--"+pztc40Record.getOrderno());

        }
    }*/

}