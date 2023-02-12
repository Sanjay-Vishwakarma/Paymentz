package com.directi.pg;

import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.*;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.statussync.StatusSyncDAO;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Jimmy Mehta
 * Date: 21-May-2012
 * Time: 20:56:51
 * To change this template use File | Settings | File Templates.
 */
public class QWIPISettlementCron
{
    private static Logger log = new Logger(QWIPISettlementCron.class.getName());


    private File[] getFileListFromSystem(String settlementFileStore)
    {
        File[] files = null;
        File f = new File(settlementFileStore);
        if (f.isDirectory())
        {
            files = f.listFiles();
        }

        return files;
    }

    public static void main(String[] args) throws SystemError
    {
        QWIPISettlementCron qwipiSettlementCron1 = new QWIPISettlementCron();
        //String error =qwipiSettlementCron1.processSettlement("settlementDetailsMERCHANT_201392819306_10265_18Mar2014_1.xls","C:\\excelReader\\settlementDetailsMERCHANT_201392819306_10265_18Mar2014_1.xls");
        //String error = qwipiSettlementCron1.processSettlement("settlementDetailsMERCHANT_201212192328.xls","D:\\settlementDetailsMERCHANT_201212192328.xls");
        //System.out.println(error);
    }

    public String processSettlement(String fileName, String fullFileName)throws Exception
    {
        List<SettleTransVO> vTransactions =null;
        StringBuffer errString = new StringBuffer();
        errString.append("<br>|  Tracking ID  |   Merchant ID  |   Account ID  |        Order ID       |  Amount  |  Current Status  |      Treatment Given     |" +  "\r\n");

        if(!isFileNameValid(fileName))
        {
            log.error("QWIPI Settlement Cron Processor : Error : Settlement Filename is invalid");
            //return "Error : Settlement Filename is invalid";
            throw new SystemError("QWIPI Settlement Cron Processor : Error : Settlement Filename is invalid");
        }

        //Loading the Transactions from Excel File
        try
        {
            vTransactions = loadTransactions(fullFileName);
        }
        catch(SystemError se)
        {
             log.error("QWIPI Settlement Cron Processor : Error While Loading the Transactions : "+ se);
             throw new SystemError("QWIPI Settlement Cron Processor : Error While Loading the Transactions : "+se);
        }

        TransactionEntry transactionEntry = null;
        Transaction transaction = null;
        Connection conn = null;
        try
        {
            transactionEntry = new TransactionEntry();
            transaction = new Transaction();
            conn = Database.getConnection();
        }
        catch (SystemError se)
        {
            log.error("QWIPI Settlement Cron Processor : Error : "+ se);
            throw new SystemError("QWIPI Settlement Cron Processor : Error : "+ se);
        }
        List vErrRecords = new ArrayList();
        int vSuccessTotalCount = 0;
        int vRefundTotalCount = 0;
        int vChargebackTotalCount = 0;
        int vAuthFailedTotalCount = 0;
        int vProcessingFailedCount = 0;
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
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

                        String query = "select toid,fromid,description,captureamount,amount,chargeper,fixamount,taxper,trackingid,accountid,status from transaction_qwipi where qwipiPaymentOrderNumber=? and description=? and status=?" ;
                        PreparedStatement p1=conn.prepareStatement(query);
                        p1.setString(1,vPaymentOrderId);
                        p1.setString(2,vSettleTransVO.getMerchantOrderId());
                        p1.setString(3,"authfailed");
                        ResultSet rs = p1.executeQuery();
                        boolean isRecordFound =false;
                        isRecordFound = rs.next();

                        if(isRecordFound )
                        {
                            int toid = rs.getInt("toid");
                            String fromid = rs.getString("fromid");
                            String description = rs.getString("description");
                            BigDecimal amount = new BigDecimal(rs.getString("amount"));
                            amount.setScale(2, BigDecimal.ROUND_HALF_UP);
                            String chargeper = rs.getString("chargeper");
                            BigDecimal chargePer = new BigDecimal(chargeper);
                            String transFixFee = rs.getString("fixamount");
                            String taxPer = rs.getString("taxper");
                            trackingId = rs.getInt("trackingid");
                            int accountId = rs.getInt("accountid");

                            //GatewayAccount vGatewayAccount= GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
                            transactionEntry.newGenericFailedTransaction(String.valueOf(trackingId),String.valueOf(accountId),null);
                            vSuccessTotalCount++;
                            statusSyncDAO.updateSettlementTransactionCronFlag(String.valueOf(trackingId),"authfailed",conn);
                            errString.append("<br>" + trackingId + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Transaction Settled" + "\r\n");
                        }
                        else
                        {
                            query = "select toid,fromid,description,captureamount,amount,chargeper,fixamount,taxper,trackingid,accountid,status from transaction_qwipi where description=? and status=? and fromid=?";
                            p1=conn.prepareStatement(query);
                            p1.setString(1,vSettleTransVO.getMerchantOrderId());
                            p1.setString(2,"authstarted");
                            p1.setString(3,vPaymentOrderId.substring(0,5));


                            rs = p1.executeQuery();
                            isRecordFound =false;
                            isRecordFound = rs.next();
                            if(isRecordFound)
                            {
                                int toid = rs.getInt("toid");
                                String fromid = rs.getString("fromid");
                                String description = rs.getString("description");
                                BigDecimal amount = new BigDecimal(rs.getString("amount"));
                                amount.setScale(2, BigDecimal.ROUND_HALF_UP);
                                String chargeper = rs.getString("chargeper");
                                BigDecimal chargePer = new BigDecimal(chargeper);
                                String transFixFee = rs.getString("fixamount");
                                String taxPer = rs.getString("taxper");
                                trackingId = rs.getInt("trackingid");
                                int accountId = rs.getInt("accountid");

                                //update the status, qwipiPaymentOrderNumber and remark column for this transaction
                                query = "update transaction_qwipi set qwipiPaymentOrderNumber=?, status='authfailed', remark='Bank Connectivity Issue' where trackingid=? and status='authstarted'";
                                PreparedStatement ps=conn.prepareStatement(query);
                                ps.setString(1,vPaymentOrderId);
                                ps.setInt(2,trackingId);
                                int result = ps.executeUpdate();

                                if(result==1)
                                {
                                    //insert the status change action entry for authfailed to details table
                                    //statusSyncDAO.updateSettlementTransactionCronFlag(String.valueOf(trackingId),"authfailed",conn);
                                    query = "insert into transaction_qwipi_details (parentid,action,status,timestamp,amount,operationCode,responseResultCode,responseDateTime,qwipiPaymentOrderNumber,responseRemark,responseMD5Info,responseBillingDescription) values (?,'Authorisation Failed','authfailed','',?,01,1,'',?,'Payment Declined','','')";
                                    PreparedStatement p=conn.prepareStatement(query);
                                    p.setInt(1,trackingId);
                                    p.setString(2,amount.toString());
                                    p.setString(3,vPaymentOrderId);
                                    int num = p.executeUpdate();
                                    if (num == 1)
                                    {   statusSyncDAO.updateSettlementTransactionCronFlag(String.valueOf(trackingId),"authfailed",conn);
                                        //now as it is in normal authfailed state, you may apply failure charges
                                        transactionEntry.newGenericFailedTransaction(String.valueOf(trackingId),String.valueOf(accountId),null);
                                        vSuccessTotalCount++;
                                        errString.append("<br>" + trackingId + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Status Correction done. Transaction Settled" + "\r\n");
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
                                    String query2 = "update transaction_qwipi set status='failed' where trackingid=?";
                                    PreparedStatement ps2=conn.prepareStatement(query2);
                                    ps2.setInt(1,trackingIdFailed);
                                    ps2.executeUpdate();
                                    statusSyncDAO.updateSettlementTransactionCronFlag(String.valueOf(trackingId),"failed",conn);
                                    errString.append("<br>" + trackingIdFailed + "  |  " + toidFailed + "  |  " + accountIdFailed + "  |  " + descriptionFailed + "  |  " + amountFailed + "  |  " + rs.getString("status") + "  |  " + "Status Correction done. Transaction now in - Failed" + "\r\n");
                                }

                            }
                            else
                            {
                                vErrRecords.add(trackingId);
                                errString.append("<br>"+vPaymentOrderId +"  |  " + vSettleTransVO.getMerchantOrderId() + "  |  " + "Transaction NOT FOUND" + "\r\n");
                            }
                        }
                    }
                    //Processing Successful Transactions which are not Refunded or not Chargebacked
                    AuditTrailVO auditTrailVO=new AuditTrailVO();

                    if((vSettleTransVO.isStatus()) && (!vSettleTransVO.isRefund()) && (!vSettleTransVO.isChargeback()))
                    {

                        String query = "select toid,fromid,description,captureamount,amount,chargeper,fixamount,taxper,trackingid,accountid,status from transaction_qwipi where qwipiPaymentOrderNumber=? and description=? and status=?" ;
                        PreparedStatement p1=conn.prepareStatement(query);
                        p1.setString(1,vPaymentOrderId);
                        p1.setString(2,vSettleTransVO.getMerchantOrderId());
                        p1.setString(3,"capturesuccess");
                        ResultSet rs = p1.executeQuery();

                        if(rs.next())
                        {

                            int toid = rs.getInt("toid");
                            String fromid = rs.getString("fromid");
                            String description = rs.getString("description");
                            BigDecimal amount = new BigDecimal(rs.getString("captureamount"));
                            amount.setScale(2, BigDecimal.ROUND_HALF_UP);
                            String chargeper = rs.getString("chargeper");
                            BigDecimal chargePer = new BigDecimal(chargeper);
                            String transFixFee = rs.getString("fixamount");
                            String taxPer = rs.getString("taxper");
                            trackingId = rs.getInt("trackingid");
                            int accountId = rs.getInt("accountid");

                            //checking status at QWIPI End.
                            String queryMIDKey="select midkey from gateway_accounts_qwipi where mid=? and accountid=?";
                            String md5string=null;
                            String md5Info=null;
                            PreparedStatement p= conn.prepareStatement(queryMIDKey);
                            p.setString(1,fromid);
                            p.setInt(2,accountId);
                            ResultSet rsMD5 =p.executeQuery();
                            if(rsMD5.next())
                            {
                                md5string=rsMD5.getString("midkey");
                                md5Info=Functions.convertmd5(fromid+description+md5string);
                            }
                            AbstractPaymentGateway pg =null;
                            pg = AbstractPaymentGateway.getGateway(String.valueOf(accountId));
                            QwipiAddressDetailsVO vAddressDetail = new QwipiAddressDetailsVO();
                            vAddressDetail.setMd5info(md5Info);

                            QwipiTransDetailsVO vTransDetailsVO =  new QwipiTransDetailsVO();
                            vTransDetailsVO.setMerNo(fromid);
                            vTransDetailsVO.setOrderDesc(description);

                            GenericCardDetailsVO vGenericCardDetails = new GenericCardDetailsVO();
                            QwipiRequestVO requestDetail = new QwipiRequestVO(vGenericCardDetails,vAddressDetail, vTransDetailsVO);
                            QwipiResponseVO transRespDetails = null;
                            try
                            {   log.debug("calling inquiry method");
                                transRespDetails = (QwipiResponseVO) pg.processInquiry(requestDetail);

                            }
                            catch (PZTechnicalViolationException e)
                            {
                                log.error("Technical Exception while inquiry");
                                PZExceptionHandler.handleTechicalCVEException(e,String.valueOf(toid),"Qwipi Settlement Cron");
                            }
                            catch (PZConstraintViolationException e)
                            {
                                log.error("Constraint Violation Exception while inquiry");
                                PZExceptionHandler.handleCVEException(e,String.valueOf(toid),"Qwipi Settlement Cron");
                            }
                            catch (PZDBViolationException e)
                            {
                                log.error("DB Violation Exception while inquiry");
                                PZExceptionHandler.handleDBCVEException(e,String.valueOf(toid),"Qwipi Settlement Cron");
                            }

                            //Checking Successful Transactions which are not Refunded or Chargebacked
                            boolean isRecordSync = false;
                            if(transRespDetails != null)
                            {
                                if(transRespDetails.getResult()!=null && transRespDetails.getResult().trim().equalsIgnoreCase("O"))
                                {
                                    if(transRespDetails.getStatus()!=null && transRespDetails.getStatus().trim().equalsIgnoreCase("A"))
                                    {
                                        if(transRespDetails.getRefundText()!=null && transRespDetails.getRefundText().trim().equalsIgnoreCase("No"))
                                        {
                                            if(transRespDetails.getCbText()!=null && transRespDetails.getCbText().trim().equalsIgnoreCase("No"))
                                            {
                                                if(transRespDetails.getStCode()!=null && transRespDetails.getStCode().trim().equalsIgnoreCase("Y"))
                                                {
                                                   isRecordSync = true;
                                                   statusSyncDAO.updateSettlementTransactionCronFlag(String.valueOf(trackingId),"capturesuccess",conn);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            //Completion of Checking Status at QWIPI End
                            if(!isRecordSync)
                            {
                                vErrRecords.add(trackingId);

                                errString.append("<br>"+trackingId +"  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " +"NOT SETTLED. Synchronization Failed" + "\r\n");
                            }
                            else
                            {
                                GatewayAccount vGatewayAccount= GatewayAccountService.getGatewayAccount(String.valueOf(accountId));
                                auditTrailVO.setActionExecutorId("0");
                                auditTrailVO.setActionExecutorName("Admin");
                                transactionEntry.newGenericCreditTransaction(String.valueOf(trackingId),amount,String.valueOf(accountId),null,auditTrailVO);
                                vSuccessTotalCount++;
                                errString.append("<br>" + trackingId + "  |  " + toid + "  |  " + accountId + "  |  " + description + "  |  " + amount + "  |  " + rs.getString("status") + "  |  " + "Transaction Settled" + "\r\n");
                            }
                        }
                        else
                        {
                            vErrRecords.add(trackingId);
                            errString.append("<br>"+vPaymentOrderId +"  |  " + vSettleTransVO.getMerchantOrderId() + "  |  " + "Transaction NOT FOUND" + "\r\n");
                        }
                    }
                    ////Processing Chargeback Transactions
                    if((vSettleTransVO.isStatus()) && (vSettleTransVO.isChargeback()))
                    {   //chargeback flag
                        statusSyncDAO.updateSettlementTransactionCronFlag(String.valueOf(trackingId),"chargeback",conn);
                        errString.append("<br>"+vPaymentOrderId +"  |  " + vSettleTransVO.getMerchantOrderId() + "  |  " + "Transaction Must be in Chargeback Status" + "\r\n");
                    }
                    //Processing Refund
                    if((vSettleTransVO.isStatus()) && (vSettleTransVO.isRefund()) && (!vSettleTransVO.isChargeback()))
                    {   //refund flag
                        statusSyncDAO.updateSettlementTransactionCronFlag(String.valueOf(trackingId),"reversed",conn);
                        errString.append("<br>"+vPaymentOrderId +"  |  " + vSettleTransVO.getMerchantOrderId() + "  |  " + "Transaction Must be in Reverse Status" + "\r\n");
                    }
                    ////////////////////////////////////////End///////////

                }catch(Exception e)
                {
                    log.error("[QWIPISettlementCron] : Error while settling the transaction for Tracking ID = " +trackingId + ". Exception =" + e);
                    vErrRecords.add(trackingId);
                    throw new Exception("[QWIPISettlementCron] : Error while settling the transaction for Tracking ID = " +trackingId + ". Exception =" + e);

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
            transaction.sendSettlementCron(errString,"QWIPI");
        }
        catch (SystemError systemError)
        {
            log.error("[QWIPISettlementCron] : Error while sending  the settled transaction mail");
            vMailSend="Mail Sending to admin is failed.";
            throw new SystemError("[QWIPISettlementCron] : Error while sending  the settled transaction mail:"+systemError);

        }
        return "Settlement File uploaded successfully. "+ vMailSend + "\r\n" + errString.toString();
    }

    private synchronized boolean validateFileFormat(String sFilename)
    {
        boolean isValidFile = false;

        try
        {
            isValidFile=true;
        }
        catch(Exception e)
        {
            //throw new SystemError("Error :  " + Util.getStackTrace(e));
            log.error("QWIPISettlementCron : Error : "+ e.getMessage());
            isValidFile=false;
        }

        return  isValidFile;
    }

    private boolean isFileNameValid(String fileName)
    {
        //Sample Valid Filename: settlementDetailsMERCHANT_201212192328.xls
        boolean isValidFilename = false;

        if(fileName.length()<42)
        {
            return isValidFilename;
        }
        if(!fileName.endsWith(".xls"))
        {
            return isValidFilename;
        }
        /*if((!fileName.substring(9,10).equalsIgnoreCase("V")) && (!fileName.substring(9,10).equalsIgnoreCase("M")))
        {
            return isValidFilename;
        }*/

        isValidFilename = true;
        return isValidFilename;
    }

    /*private boolean moveFile(String sourceFolder, String destinationFolder, String fileName)
    {
        boolean isFileMoved =false;
        File sourceFile = new File(sourceFolder + fileName);
        File destinationFile = new File(destinationFolder+fileName);
        if(sourceFile.exists())
        {
            if(destinationFile.exists())
            {

                destinationFile.renameTo(new File(destinationFolder + fileName.substring(0,fileName.length()-4) + "_" + getCurrentTimeStamp() + ".TXT"));
                isFileMoved = true;
            }
        }
        sourceFile = null;
        destinationFile = null;
        return isFileMoved;
    }*/

    private String getCurrentTimeStamp()
    {
        java.util.Date vDt = new Date();
        TimeZone mauritiusTZ = TimeZone.getTimeZone("Indian/Mauritius");
        Calendar cal = Calendar.getInstance(mauritiusTZ);
        cal.setTime(vDt);
        java.util.Date currentDate = cal.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMMyyyyHHmmss");
        String vTimeStamp = dateFormat.format(currentDate);
        return vTimeStamp;
    }

    public List<SettleTransVO> loadTransactions(String fileName) throws SystemError
    {
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
                SettleTransVO vSettleTransVO = new SettleTransVO();
                while (cells.hasNext())
                {
                    HSSFCell cell = (HSSFCell) cells.next();
                    if(cell.getCellNum()==1)
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
                        String vMerchantOrderId = cell.getStringCellValue();
                        if(vMerchantOrderId==null || vMerchantOrderId=="")
                        {
                            throw new SystemError("ERROR : MerchantOrderId not Found from the File");
                        }

                        vSettleTransVO.setMerchantOrderId(cell.getStringCellValue());

                        continue;
                    }
                    if(cell.getCellNum()==4)
                    {

                        vSettleTransVO.setAmount(cell.getNumericCellValue()+"");
                        continue;
                    }
                    if(cell.getCellNum()==6)
                    {

                        vSettleTransVO.setStatus(cell.getStringCellValue().equalsIgnoreCase("Successful")?true:false);
                        continue;
                    }
                    if(cell.getCellNum()==7)
                    {

                        vSettleTransVO.setRefund(cell.getStringCellValue().equalsIgnoreCase("Yes") || cell.getStringCellValue().equalsIgnoreCase("Partial")?true:false);
                        continue;
                    }
                    if(cell.getCellNum()==8)
                    {

                        vSettleTransVO.setChargeback(cell.getStringCellValue().equalsIgnoreCase("Yes")?true:false);
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
        return (List<SettleTransVO>)vList;
    }
}