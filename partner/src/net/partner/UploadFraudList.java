package net.partner;

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
import org.owasp.esapi.ESAPI;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Namrata Bari on 24-09-2019.
 */
public class UploadFraudList
{
    private static Logger log = new Logger(UploadFraudList.class.getName());
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
        String Currency = "";
        String Currency1 = "";
        int i= 0;
        try
        {
           // vTransactions =fileHandlingUtil.readFraudFile(fullFileName);
            if (fullFileName.contains("xlsx"))
            {
                vTransactions = fileHandlingUtil.readFraudFileXlsx(fullFileName);
            }
            else
            {
                vTransactions = fileHandlingUtil.readFraudFile(fullFileName);

            }
            fileHandlingUtil.deleteFile(fullFileName);

            sSuccessMessage.append("<tr class=\"tdstyle\"><td>  Tracking ID  </td><td> Payment ID </td><td> Reason </td><td>  Remark  </td></tr>");
            for(TransactionVO transactionVO : vTransactions)
            {
                i=i+1;
                if (functions.isValueNull(transactionVO.getTrackingId()))
                    trackingId = transactionVO.getTrackingId();
                if(functions.isValueNull(transactionVO.getPaymentId()))
                    paymentId=transactionVO.getPaymentId();

                stringBuffer.append(trackingId);
                if(vTransactions.size()>i){
                    stringBuffer.append(",");
                }
            }
            BlacklistManager blacklistManager = new BlacklistManager();
            WhiteListManager whiteListManager=new WhiteListManager();
            TransactionManager transactionManager=new TransactionManager();
            boolean isValid=false;
            for (TransactionVO transactionVO1 : vTransactions)
            {
                trackingId=transactionVO1.getTrackingId();
                paymentId=transactionVO1.getPaymentId();
                fraudReason = transactionVO1.getFraudreason();
                Currency =transactionVO1.getCurrency();
                TransactionDetailsVO transactionDetailsVO = transactionManager.getCommonTransactionDetailsForChargeBack(trackingId);
                if (transactionDetailsVO != null)
                {
                    Currency1 = transactionDetailsVO.getCurrency();
                }
                if (functions.isValueNull(transactionVO1.getTrackingId()) && functions.isValueNull(transactionVO1.getPaymentId()))
                {
                    isValid = transactionDAO.isValid(trackingId, paymentId);
                }
                if (!ESAPI.validator().isValidInput("trackingid", trackingId, "Numbers", 10, false))
                {
                    sSuccessMessage.append("<tr><td> " + trackingId + "  </td><td>   " + paymentId + "</td><td>" + fraudReason + " </td><td> " + "Invalid Tracking ID.</td></tr>");
                }
                if (!ESAPI.validator().isValidInput("fraudReason", fraudReason, "StrictString", 255, false))
                {
                    sSuccessMessage.append("<tr><td> " + trackingId + "  </td><td>   " + paymentId + "</td><td>" + fraudReason + " </td><td> " + "Invalid Fraud Reason.</td></tr>");

                }
                if (!functions.isValueNull(Currency))
                {
                    sSuccessMessage.append("<tr><td> " + trackingId + "  </td><td>   " + paymentId + "</td><td>" + fraudReason + " </td><td> " + "Currency Should not be Empty.</td></tr>");
                }
                if (functions.isValueNull(Currency) && !Currency.equals(Currency1))
                {
                    sSuccessMessage.append("<tr><td> " + trackingId+ "  </td><td>   " + paymentId + "</td><td>" + fraudReason + " </td><td> " + "Invalid Currency OR Mismatch Currency.</td></tr>");
                }
                else
                {
                    if (isValid == true)
                    {
                        boolean status = transactionDAO.isTransactionFraud(trackingId);
                        if (status == false)
                        {
                            i = i + 1;
                            stringBuffer.append(trackingId);
                            if (vTransactions.size() > i)
                            {
                                stringBuffer.append(",");
                            }
                             if (functions.isValueNull(fraudReason))
                            {

                                fraudReason = transactionVO1.getFraudreason();
                            }
                            else
                            {
                                fraudReason = "Fraudulent Transaction";
                            }
                            boolean isRecordInserted = transactionDAO.getUploadBulkFraudReason(trackingId, fraudReason);
                            reason = "Fraudulent Transaction Uploaded by partner" + "(" + (trackingId) + ")";
                            //String reason = "Fraudulent Transaction Uploaded by admin" + "(" + (trackingId) + ")";
                    /*if (functions.isValueNull(transactionDetailsVO.getName()))
                    {*/
                            try
                            {
                                blacklistManager.blacklistEntities(trackingId, reason,BlacklistEnum.Fraud_Received.toString(),auditTrailVO);
                            }
                            catch (PZDBViolationException e)
                            {
                                log.error("Duplicate Entry:::" , e);
                            }
                            try
                            {
                                whiteListManager.whiteListEntities(trackingId);
                            }
                            catch (PZDBViolationException e)
                            {
                                log.error("Duplicate Entry:::", e);
                            }
                            try
                            {
                                Set<String> cardNum = new HashSet<>();
                                Set<String> emailAddr = new HashSet<>();
                                Set<String> phone = new HashSet<>();
                                if (functions.isValueNull(transactionDetailsVO.getName()))
                                {
                                    transactionManager.addCustomerName(transactionDetailsVO.getName(), cardNum, emailAddr,phone);
                                }
                                try
                                {
                                    blacklistManager.addCustomerCardBatch(cardNum, reason, BlacklistEnum.Fraud_Received.toString(),auditTrailVO);
                                }
                                catch (PZDBViolationException e)
                                {
                                    log.error("Duplicate Entry:::", e);
                                }
                                try
                                {
                                    blacklistManager.addCustomerEmailBatch(emailAddr, reason,auditTrailVO);
                                }
                                catch (PZDBViolationException e)
                                {
                                    log.error("Duplicate Entry:::" , e);
                                }/*try
                                {
                                    blacklistManager.addCustomerPhoneBatch(phone, reason,auditTrailVO);
                                }
                                catch (PZDBViolationException e)
                                {
                                    log.error("Duplicate Entry:::" , e);
                                }*/
                                try
                                {
                                    whiteListManager.updateCardEmailEntry(emailAddr, cardNum);
                                }
                                catch (PZDBViolationException e)
                                {
                                    log.error("Duplicate Entry:::", e);
                                }

                            }
                            catch (PZDBViolationException e)
                            {
                                log.error("Duplicate Entry:::" , e);
                            }

                            sSuccessMessage.append("<tr><td>  " + trackingId + "  </td><td>   " + paymentId + "</td><td>" + fraudReason + "</td><td>" + "Records Updated Successfully.</td></tr>");
                        }
                        else
                        {
                            sSuccessMessage.append("<tr><td> " + trackingId + "  </td><td>   " + paymentId + "</td><td>" + "-" + " </td><td> " + "Transaction is already marked as Fraud.</td></tr>");
                        }
                    }
                    else
                    {
                        sSuccessMessage.append("<tr><td> " + trackingId + "  </td><td>   " + paymentId + "</td><td>" + "-" + " </td><td> " + "Invalid Tracking ID and Payment ID Configuration.</td></tr>");
                    }
                }
               /* }
                else
                {
                    sSuccessMessage.append("<tr><td> " + trackingId + "  </td><td>   " + paymentId + "</td><td>" + "-" + " </td><td> " + "Records Updated Failed.</td></tr>");
                }*/
            }


        }
        catch(Exception e){
            sSuccessMessage=new StringBuilder(e.getMessage());
        }
        StringBuilder chargeBackMessage = new StringBuilder();
        chargeBackMessage.append(sSuccessMessage.toString());
        chargeBackMessage.append("<BR/>");
        chargeBackMessage.append(sErrorMessage.toString());
        return chargeBackMessage;
    }

}
