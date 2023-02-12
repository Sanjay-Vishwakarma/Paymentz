package com.directi.pg;

import com.payment.statussync.StatusSyncDAO;
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
 * User: Admin
 * Date: 21-May-2012
 * Time: 20:56:51
 * To change this template use File | Settings | File Templates.
 */
public class EcoreSettlementCron
{
    private static Logger log = new Logger(EcoreSettlementCron.class.getName());

    public static void main(String[] args) throws SystemError
    {
        EcoreSettlementCron ecoreSettlementCron = new EcoreSettlementCron();
        //String error = ecoreSettlementCron.processSettlement("ecorepay_test_settlement.xls","E:\\PZ\\Ecore\\Cron\\ecorepay_test_settlement.xls");
        String error = ecoreSettlementCron.processSettlement("ecorepay_sfreport_10248_22Nov2013.xls","E:\\PZ\\Ecore\\Cron\\ecorepay_sfreport_10248_22Nov2013.xls");
        //System.out.println(error);
    }

    public String processSettlement(String fileName, String fullFileName)
    {
        List<SettleTransVO> vTransactions;
        StringBuffer errString = new StringBuffer();
        errString.append("<br>|  Tracking ID  |   Merchant ID  |   Account ID  |        Order ID       |  Amount  |  Current Status  |      Treatment Given     |" +  "\r\n");
        StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
        if(!isFileNameValid(fileName))
        {
            log.error("Ecore Settlement Cron Processor : Error : Settlement Filename is invalid");
            return "Error : Settlement Filename is invalid";
        }

        //Loading the Transactions from Excel File
        try
        {
            vTransactions = loadTransactions(fullFileName);
        }catch(SystemError se)
        {
            log.error("Ecore Settlement Cron Processor : Error While Loading the Transactions : "+ se.getMessage());
            return se.getMessage();
        }

        TransactionEntry transactionEntry = null;
        Transaction transaction = new Transaction();
        Connection conn;
        try
        {
            transactionEntry = new TransactionEntry();
            conn = Database.getConnection();
        }
        catch (Exception e)
        {
            log.error("Ecore Settlement Cron Processor : Error : "+ e.getMessage());
            return  e.getMessage();
        }
        List vErrRecords = new ArrayList();
        try
        {
            for(SettleTransVO vSettleTransVO : vTransactions)
            {
                int trackingId=0;
                try
                {
                    String vPaymentOrderId = vSettleTransVO.getPaymentOrderId();
                    //Processing Failed Transactions
                    if(!vSettleTransVO.isStatus())
                    {
                        //Variable Definition

                        String query = "select toid,fromid,description,captureamount,amount,chargeper,fixamount,taxper,trackingid,accountid,status from transaction_ecore where ecorePaymentOrderNumber=? and status=?" ;
                        PreparedStatement p1=conn.prepareStatement(query);
                        p1.setString(1,vPaymentOrderId);
                        p1.setString(2,"authfailed");
                        ResultSet rs = p1.executeQuery();
                        boolean isRecordFound = rs.next();

                        if(isRecordFound )
                        {
                            int toid = rs.getInt("toid");
                            //String fromid = rs.getString("fromid");
                            String description = rs.getString("description");
                            BigDecimal amount = new BigDecimal(rs.getString("amount"));
                            amount.setScale(2, BigDecimal.ROUND_HALF_UP);
                            //String chargeper = rs.getString("chargeper");
                            //BigDecimal chargePer = new BigDecimal(chargeper);
                            //String transFixFee = rs.getString("fixamount");
                            //String taxPer = rs.getString("taxper");
                            trackingId = rs.getInt("trackingid");
                            int accountId = rs.getInt("accountid");

                            //GatewayAccount vGatewayAccount= GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
                            transactionEntry.newGenericFailedTransaction(String.valueOf(trackingId),String.valueOf(accountId),null);
                            statusSyncDAO.updateSettlementTransactionCronFlag(String.valueOf(trackingId),"authfailed",conn);
                            errString.append("<br>" + trackingId + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Transaction Settled" + "\r\n");
                        }
                        else
                        {
                            query = "select toid,fromid,description,captureamount,amount,chargeper,fixamount,taxper,trackingid,accountid,status from transaction_ecore where trackingid=? and status=?";
                            p1=conn.prepareStatement(query);
                            p1.setString(1,vSettleTransVO.getTrackingId());
                            p1.setString(2,"authstarted");

                            rs = p1.executeQuery();
                            isRecordFound = rs.next();
                            if(isRecordFound)
                            {
                                int toid = rs.getInt("toid");
                                //String fromid = rs.getString("fromid");
                                String description = rs.getString("description");
                                BigDecimal amount = new BigDecimal(rs.getString("amount"));
                                amount.setScale(2, BigDecimal.ROUND_HALF_UP);
                                //String chargeper = rs.getString("chargeper");
                                //BigDecimal chargePer = new BigDecimal(chargeper);
                                //String transFixFee = rs.getString("fixamount");
                                //String taxPer = rs.getString("taxper");
                                trackingId = rs.getInt("trackingid");
                                int accountId = rs.getInt("accountid");

                                //update the status, ecorePaymentOrderNumber and remark column for this transaction
                                query = "update transaction_ecore set ecorePaymentOrderNumber=?, status='authfailed', remark='Bank Connectivity Issue' where trackingid=? and status='authstarted'";
                                PreparedStatement ps=conn.prepareStatement(query);
                                ps.setString(1,vSettleTransVO.getTrackingId());

                                int result = ps.executeUpdate();

                                if(result==1)
                                {
                                    //insert the status change action entry for authfailed to details table
                                    query = "insert into transaction_ecore_details (parentid,action,status,timestamp,amount,operationCode,responseResultCode,responseDateTime,ecorePaymentOrderNumber,responseRemark,responseMD5Info,responseBillingDescription) values (?,'Authorisation Failed','authfailed','',?,01,1,'',?,'Payment Declined','','')";
                                    PreparedStatement p=conn.prepareStatement(query);
                                    p.setInt(1,trackingId);
                                    p.setString(2,amount.toString());
                                    p.setString(3,vPaymentOrderId);
                                    int num = p.executeUpdate();
                                    if (num == 1)
                                    {
                                        //now as it is in normal authfailed state, you may apply failure charges
                                        transactionEntry.newGenericFailedTransaction(String.valueOf(vSettleTransVO.getTrackingId()),String.valueOf(accountId),null);
                                        statusSyncDAO.updateSettlementTransactionCronFlag(String.valueOf(trackingId),"authfailed",conn);
                                        errString.append("<br>" + vSettleTransVO.getTrackingId() + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Status Correction done. Transaction Settled" + "\r\n");
                                    }
                                }
                                //Now if there are more records of the same Description in authstarted all should be marked as 'Failed'
                                while(rs.next())
                                {
                                    int trackingIdFailed = rs.getInt("trackingid");
                                    int toidFailed = rs.getInt("toid");
                                    int accountIdFailed = rs.getInt("accountid");
                                    String descriptionFailed = rs.getString("description");
                                    BigDecimal amountFailed = new BigDecimal(rs.getString("amount"));
                                    amountFailed.setScale(2, BigDecimal.ROUND_HALF_UP);
                                    String query2 = "update transaction_ecore set status='failed' where trackingid=?";
                                    PreparedStatement ps2=conn.prepareStatement(query2);
                                    ps2.setInt(1,trackingIdFailed);
                                    ps2.executeUpdate();
                                    statusSyncDAO.updateSettlementTransactionCronFlag(String.valueOf(trackingId),"failed",conn);
                                    errString.append("<br>" + trackingIdFailed + "  |  " + toidFailed + "  |  " + accountIdFailed + "  |  " + descriptionFailed + "  |  " + amountFailed + "  |  " + rs.getString("status") + "  |  " + "Status Correction done. Transaction now in - Failed" + "\r\n");
                                }

                            }
                            else
                            {
                                vErrRecords.add(vSettleTransVO.getTrackingId());
                                errString.append("<br>"+vPaymentOrderId +"  |  " + vSettleTransVO.getTrackingId() + "  |  " + "Transaction NOT FOUND" + "\r\n");
                            }
                        }
                    }
                    //Processing Successful Transactions which are not Refunded or not Chargebacked
                    if(vSettleTransVO.isStatus() && !vSettleTransVO.isRefund() && !vSettleTransVO.isChargeback() && !vSettleTransVO.isPendingApproval())
                    {

                        String query = "select toid,fromid,description,captureamount,amount,chargeper,fixamount,taxper,trackingid,accountid,status from transaction_ecore where ecorePaymentOrderNumber=? and status=?" ;
                        PreparedStatement p1=conn.prepareStatement(query);
                        p1.setString(1,vPaymentOrderId);
                        p1.setString(2,"capturesuccess");
                        ResultSet rs = p1.executeQuery();

                        if(rs.next())
                        {
                            int toid = rs.getInt("toid");
                            //String fromid = rs.getString("fromid");
                            String description = rs.getString("description");
                            BigDecimal amount = new BigDecimal(rs.getString("captureamount"));
                            amount.setScale(2, BigDecimal.ROUND_HALF_UP);
                            //String chargeper = rs.getString("chargeper");
                            //BigDecimal chargePer = new BigDecimal(chargeper);
                            //String transFixFee = rs.getString("fixamount");
                            //String taxPer = rs.getString("taxper");
                            trackingId = rs.getInt("trackingid");
                            int accountId = rs.getInt("accountid");
                            AuditTrailVO auditTrailVO=new AuditTrailVO();
                            auditTrailVO.setActionExecutorId("0");
                            auditTrailVO.setActionExecutorName("Admin");
                            transactionEntry.newGenericCreditTransaction(String.valueOf(trackingId),amount,String.valueOf(accountId),null,auditTrailVO);
                            statusSyncDAO.updateSettlementTransactionCronFlag(String.valueOf(trackingId),"capturesuccess",conn);
                            errString.append("<br>" + trackingId + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Transaction Settled" + "\r\n");

                        }
                        else
                        {
                            vErrRecords.add(trackingId);
                            errString.append("<br>"+vPaymentOrderId +"  |  " + vSettleTransVO.getTrackingId() + "  |  " + "Transaction NOT FOUND" + "\r\n");
                        }
                    }
                    ////Processing Chargeback Transactions
                    if(vSettleTransVO.isStatus() && vSettleTransVO.isChargeback())
                    {
                        statusSyncDAO.updateSettlementTransactionCronFlag(String.valueOf(trackingId),"chargeback",conn);
                        errString.append("<br>"+vPaymentOrderId +"  |  " + vSettleTransVO.getTrackingId() + "  |  " + "Transaction Must be in Chargeback Status" + "\r\n");
                    }
                    //Processing Refund
                    else if(vSettleTransVO.isStatus() && vSettleTransVO.isRefund())
                    {
                        statusSyncDAO.updateSettlementTransactionCronFlag(String.valueOf(trackingId),"reversed",conn);
                        errString.append("<br>"+vPaymentOrderId +"  |  " + vSettleTransVO.getTrackingId() + "  |  " + "Transaction Must be in Reverse Status" + "\r\n");
                    }
                    //Processing Pending Approval
                    else if(vSettleTransVO.isStatus() && vSettleTransVO.isPendingApproval())
                    {
                        errString.append("<br>"+vPaymentOrderId +"  |  " + vSettleTransVO.getTrackingId() + "  |  " + "Transaction is in status Pending Approval. Please settle manually." + "\r\n");
                    }
                    ////////////////////////////////////////End///////////

                }catch(Exception e)
                {
                    log.error("[EcoreSettlementCron] : Error while settling the transaction for Tracking ID = " +trackingId + ". Exception =" + e);
                    vErrRecords.add(trackingId);
                }
            }
        }
        finally
        {
            Database.closeConnection(conn);
        }
        String vMailSend = "";
        try
        {
            transaction.sendSettlementCron(errString,"ECORE");
        }
        catch (SystemError systemError)
        {
            log.error("[EcoreSettlementCron] : Error while sending  the settled transaction mail");
            vMailSend="Mail Sending to admin is failed.";
        }
        return "Settlement File uploaded successfully. "+ vMailSend + "\r\n" + errString.toString();
    }

    private boolean isFileNameValid(String fileName)
    {

        if(fileName.length()<2)
        {
            return false;
        }
        if(!fileName.endsWith(".xls"))
        {
            return false;
        }
        return true;
    }

    public List<SettleTransVO> loadTransactions(String fileName) throws SystemError
    {
        //System.out.println("11111111111-----"+fileName);
        InputStream inputStream = null;
        List<SettleTransVO> vList = new ArrayList<SettleTransVO>();

        try
        {
            inputStream = new BufferedInputStream(new FileInputStream(fileName));
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
                //cntRowToLeaveFromTop++;

                // Skip the header rows.
                if (cntRowToLeaveFromTop==0)
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
                SettleTransVO vSettleTransVO = new SettleTransVO();
                while (cells.hasNext())
                {
                    HSSFCell cell = (HSSFCell) cells.next();
                    if(cell.getCellNum()==0)
                    {
                        String vPayOrderId = cell.getStringCellValue();
                        if(vPayOrderId==null || vPayOrderId=="")
                        {
                            throw new SystemError("ERROR : PaymentOrderID not Found from the File");
                        }

                        vSettleTransVO.setPaymentOrderId(cell.getStringCellValue().trim());
                        continue;
                    }
                    if(cell.getCellNum()==2)
                    {
                        vSettleTransVO.setStatus(cell.getStringCellValue().equalsIgnoreCase("Declined")? false:true);
                        vSettleTransVO.setPendingApproval(cell.getStringCellValue().equalsIgnoreCase("Pending Approval") ? true:false);
                        //vSettleTransVO.setRefund(cell.getStringCellValue().equalsIgnoreCase("Refunded") ?true:false);
                        //vSettleTransVO.setChargeback(cell.getStringCellValue().equalsIgnoreCase("Chargeback")?true:false);

                        continue;
                    }
                    if(cell.getCellNum()==4)
                    {
                        vSettleTransVO.setAmount(cell.getStringCellValue());
                        continue;
                    }
                    if(cell.getCellNum()==12)
                    {
                        vSettleTransVO.setTrackingId(cell.getStringCellValue());
                        continue;
                    }
                }
                if(vSettleTransVO!= null)
                {
                    vList.add(vSettleTransVO);
                }
            }
        }
        catch (IOException e)
        {
            throw new SystemError("ERROR: Settlement File can't be read");
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
                    throw new SystemError("ERROR: Settlement File can't be closed successfully");
                }
            }
        }
        return (List<SettleTransVO>)vList;
    }
}