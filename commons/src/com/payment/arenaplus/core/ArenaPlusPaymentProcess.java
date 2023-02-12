package com.payment.arenaplus.core;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.payment.PZTransactionStatus;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.request.PZSettlementFile;
import com.payment.response.PZSettlementRecord;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 1/31/14
 * Time: 1:58 AM
 * To change this template use File | Settings | File Templates.
 */
public class ArenaPlusPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(ArenaPlusPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(ArenaPlusPaymentProcess.class.getName());

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
                if (cntRowToLeaveFromTop==3)
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
                if (((HSSFCell) row.getCell((short) 4)).getStringCellValue().trim().equals("Debit"))
                {
                    if (((HSSFCell) row.getCell((short) 5)).getStringCellValue().trim().equals("OK"))
                    {

                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.SETTLED));

                    }
                    else
                    {
                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.CAPTURE_FAILED));
                    }
                }
                else if (((HSSFCell) row.getCell((short) 4)).getStringCellValue().equals("Credit"))
                {
                    if (((HSSFCell) row.getCell((short) 5)).getStringCellValue().trim().equals("OK"))
                    {
                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.REVERSED));
                    }
                    else
                    {
                        loadTransResponse.setStatusDetail(String.valueOf(PZTransactionStatus.MARKED_FOR_REVERSAL));
                    }
                }
                //loadTransResponse.setSDateTime(((HSSFCell) row.getCell((short)2)).getStringCellValue()+"");
                String sAmount = (float) row.getCell((short) 6).getNumericCellValue() + "";
                loadTransResponse.setAmount(new BigDecimal(sAmount).divide(new BigDecimal(100.00)).floatValue()+ "");
                loadTransResponse.setMerchantTXNid(row.getCell((short) 1).getStringCellValue() + "");
                //loadTransResponse.setPaymentid((long)((HSSFCell) row.getCell((short)0)).getNumericCellValue()+"");

                if(loadTransResponse!= null)
                {
                    vList.add(loadTransResponse);
                }
            }
        }
        catch (IOException e)
        {
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

                                /*auditTrailVO.setActionExecutorId(String.valueOf(toid));
                                auditTrailVO.setActionExecutorName("Customer");*/
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

                                /*auditTrailVO.setActionExecutorId(String.valueOf(toid));
                                auditTrailVO.setActionExecutorName("Customer");*/
                                int num = actionEntry(vTrackingId, amount.toString(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, null,auditTrailVO,null);
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
        finally
        {
            Database.closeConnection(conn);
        }
        return "Settlement File uploaded successfully. " + vMailSend + "\r\n" + errString.toString();
    }
}
