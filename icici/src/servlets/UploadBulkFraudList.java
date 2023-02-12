package servlets;

import com.directi.pg.AuditTrailVO;
import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.manager.BlacklistManager;
import com.manager.TransactionManager;
import com.manager.WhiteListManager;
import com.manager.dao.TransactionDAO;
import com.manager.enums.BlacklistEnum;
import com.manager.utils.FileHandlingUtil;
import com.manager.vo.TransactionDetailsVO;
import com.manager.vo.TransactionVO;
import com.payment.exceptionHandler.PZDBViolationException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Mahima
 * Date: 5/2/18
 * Time: 3:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class UploadBulkFraudList
{
    private static Logger log = new Logger(UploadBulkFraudList.class.getName());
    public StringBuilder uploadFraud(String fullFileName, StringBuilder sSuccessMessage, StringBuilder sErrorMessage,AuditTrailVO auditTrailVO)
    {
        Functions functions= new Functions();
        List<TransactionVO> vTransactions =null;
        FileHandlingUtil fileHandlingUtil=new FileHandlingUtil();
        TransactionDAO transactionDAO=new TransactionDAO();
        StringBuffer stringBuffer=new StringBuffer();
        String trackingId = "";
        String paymentId = "";
        String fraudReason = "";
        String reason = "";
        int i= 0;
        try
        {
            vTransactions =fileHandlingUtil.readFraudDetails(fullFileName);
            fileHandlingUtil.deleteFile(fullFileName);
            sSuccessMessage.append("<br><b>  Tracking ID  | Payment ID | Reason |  Remark  </b>");
            log.debug("vTransactions:::::::::::::::"+vTransactions);
            for(TransactionVO transactionVO : vTransactions)
            {
                i=i+1;
                if(transactionVO!=null)
                {
                    if (functions.isValueNull(transactionVO.getTrackingId()))
                        trackingId = transactionVO.getTrackingId();
                    if (functions.isValueNull(transactionVO.getPaymentId()))
                        paymentId = transactionVO.getPaymentId();

                    stringBuffer.append(trackingId);
                    if (vTransactions.size() > i)
                    {
                        stringBuffer.append(",");
                    }
                }
                else {
                    break;
                }
            }

            BlacklistManager blacklistManager = new BlacklistManager();
            WhiteListManager whiteListManager=new WhiteListManager();
            TransactionManager transactionManager=new TransactionManager();
            boolean isValid=false;
            for (TransactionVO transactionVO1 : vTransactions)
            {
                if(transactionVO1!=null)
                {
                    trackingId = transactionVO1.getTrackingId();
                    paymentId = transactionVO1.getPaymentId();
                    if (functions.isValueNull(transactionVO1.getTrackingId()) && functions.isValueNull(transactionVO1.getPaymentId()))
                    {
                        isValid = transactionDAO.isValid(trackingId, paymentId);
                    }
                    if (isValid == true)
                    {
                        i = i + 1;
                        trackingId = transactionVO1.getTrackingId();
                        if (functions.isValueNull(transactionVO1.getTrackingId()))
                            trackingId = transactionVO1.getTrackingId();
                        stringBuffer.append(trackingId);
                        if (vTransactions.size() > i)
                        {
                            stringBuffer.append(",");
                        }
                        TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
                        fraudReason = transactionVO1.getFraudreason();
                        if (functions.isValueNull(fraudReason))
                        {

                            fraudReason = transactionVO1.getFraudreason();
                        }
                        else
                        {
                            fraudReason = "Fraudulent Transaction";
                        }
                        boolean isRecordInserted = transactionDAO.getUploadBulkFraudReason(trackingId, fraudReason);
                        reason = "Fraudulent Transaction Uploaded by admin" + "(" + (trackingId) + ")";
                        //String reason = "Fraudulent Transaction Uploaded by admin" + "(" + (trackingId) + ")";
                        if (functions.isValueNull(transactionDetailsVO.getName()))
                        {
                            try
                            {
                                blacklistManager.blacklistEntities(trackingId, reason,BlacklistEnum.Fraud_Received.toString(),auditTrailVO);
                            }
                            catch (PZDBViolationException e)
                            {
                                log.info("Duplicate Entry:::" + e);
                            }
                            try
                            {
                                whiteListManager.whiteListEntities(trackingId);
                            }
                            catch (PZDBViolationException e)
                            {
                                log.info("Duplicate Entry:::" + e);
                            }
                            try
                            {
                                Set<String> cardNum = new HashSet<>();
                                Set<String> emailAddr = new HashSet<>();
                                Set<String> phone = new HashSet<>();
                                transactionManager.addCustomerName(transactionDetailsVO.getName(), cardNum, emailAddr,phone);
                                try
                                {
                                    blacklistManager.addCustomerCardBatch(cardNum, reason, BlacklistEnum.Fraud_Received.toString(),auditTrailVO);
                                }
                                catch (PZDBViolationException e)
                                {
                                    log.info("Duplicate Entry:::" + e);
                                }
                                try
                                {
                                    blacklistManager.addCustomerEmailBatch(emailAddr, reason,auditTrailVO);
                                }
                                catch (PZDBViolationException e)
                                {
                                    log.info("Duplicate Entry:::" + e);
                                }
                                /*try
                                {
                                    blacklistManager.addCustomerPhoneBatch(phone, reason, auditTrailVO);
                                }
                                catch (PZDBViolationException e)
                                {
                                    log.info("Duplicate Entry:::" + e);
                                }*/
                                try
                                {
                                    whiteListManager.updateCardEmailEntry(emailAddr, cardNum);
                                }
                                catch (PZDBViolationException e)
                                {
                                    log.info("Duplicate Entry:::" + e);
                                }
                            }
                            catch (PZDBViolationException e)
                            {
                                log.info("Duplicate Entry:::" + e);
                            }
                            sSuccessMessage.append("<br>  " + trackingId + "  |   " + paymentId + "|" + fraudReason + "|" + "Records Updated Successfully.");
                        }
                        else
                        {
                            sSuccessMessage.append("<br>  " + trackingId + "  |   " + paymentId + "|" + "-" + " | " + "Records Updated Failed.");
                        }
                    }
                    else
                    {
                        sSuccessMessage.append("<br>  " + trackingId + "  |   " + paymentId + "|" + "-" + " | " + "Records Updated Failed.");
                    }
                }
                else
                    break;
            }
            if(!functions.isValueNull(trackingId) || !functions.isValueNull(paymentId))
            {
                sSuccessMessage=new StringBuilder();
                sSuccessMessage.append("<b>Invalid File Content.</b>");
            }

        }
        catch(Exception e){
            sSuccessMessage=new StringBuilder("File Not Found");
            log.debug("Exception:::::"+e);
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        return chargeBackMessage;
    }
}