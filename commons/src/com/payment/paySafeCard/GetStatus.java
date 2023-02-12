package com.payment.paySafeCard;

import com.directi.pg.ActionEntry;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.exceptionHandler.*;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 2/10/15
 * Time: 5:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetStatus
{
    private static Logger log = new Logger(GetStatus.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(GetStatus.class.getName());
    public CommResponseVO checkDispostionState(String trackingId,boolean flag)
    {
        AbstractPaymentGateway pg = null;
        TransactionDetailsVO transactionDetailsVO=null;
        TransactionManager transactionManager=new TransactionManager();
        transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
        CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO=new CommAddressDetailsVO();
        CommResponseVO commResponseVO=null;
        PaySafeCardUtils paySafeCardUtils=new PaySafeCardUtils();
        ActionEntry entry=new ActionEntry();

        PaymentManager paymentManager=new PaymentManager();
        CommRequestVO commRequestVO = new CommRequestVO();
        if(transactionDetailsVO!=null)
        {
            try
            {
                pg =  AbstractPaymentGateway.getGateway(transactionDetailsVO.getAccountId());
                commTransactionDetailsVO.setAmount(transactionDetailsVO.getAmount());
                commAddressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
                commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                commResponseVO = (CommResponseVO) pg.processQuery(trackingId,commRequestVO);
                if(flag)
                {
                    if(commResponseVO!=null)
                    {
                        if(commResponseVO.getStatus().equalsIgnoreCase("success"))
                        {
                            log.debug(commResponseVO.getTransactionStatus());
                            commTransactionDetailsVO.setPreviousTransactionId(commResponseVO.getTransactionId());
                            commTransactionDetailsVO.setAmount(transactionDetailsVO.getAmount());
                            commAddressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
                            commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
                            commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                            if(commResponseVO.getTransactionStatus().equalsIgnoreCase("S"))
                            {
                                log.error("Auth success entry");
                                paymentManager.updatePaymentIdForCommon(commResponseVO.getTransactionId(), trackingId, "authsuccessful");
                                entry.actionEntryForPaysafeCard(trackingId, transactionDetailsVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_SUCCESSFUL, ActionEntry.STATUS_AUTHORISTION_SUCCESSFUL, commResponseVO, null, null);
                                String paymentId = commResponseVO.getTransactionId();

                                paymentManager.updatePaymentIdForCommon(paymentId, trackingId, "capturestarted");
                                entry.actionEntryForPaysafeCard(trackingId, transactionDetailsVO.getAmount(), ActionEntry.ACTION_CAPTURE_STARTED, ActionEntry.STATUS_CAPTURE_STARTED, commResponseVO, null, null);

                                commResponseVO = (CommResponseVO) pg.processCapture(trackingId,commRequestVO);

                                if(commResponseVO.getStatus().equalsIgnoreCase("success"))
                                {   log.error("execute Debit success capture success");
                                    paymentManager.updateCapturesuccessForPaySafeCard(paymentId,trackingId,"capturesuccess",transactionDetailsVO.getAmount());
                                    entry.actionEntryForPaysafeCard(trackingId, transactionDetailsVO.getAmount(), ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, commResponseVO, null, null);
                                }
                                else
                                {   log.error("execute Debit success capture fail");
                                    paymentManager.updatePaymentIdForCommon(paymentId, trackingId, "capturefailed");
                                    entry.actionEntryForPaysafeCard(trackingId, transactionDetailsVO.getAmount(), ActionEntry.ACTION_CAPTURE_FAILED, ActionEntry.STATUS_CAPTURE_FAILED, commResponseVO, null, null);
                                }
                            }
                        }
                        else
                        {
                            paymentManager.updatePaymentIdForCommon(commResponseVO.getTransactionId(),trackingId,"authfailed");
                            entry.actionEntryForPaysafeCard(trackingId,transactionDetailsVO.getAmount(), ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, commResponseVO, null, null);
                        }
                    }
                }
            }
            catch (SystemError systemError)
            {
                log.error("error", systemError);
                transactionLogger.error("error", systemError);
                PZExceptionHandler.raiseAndHandleDBViolationException(GetStatus.class.getName(),"checkDispostionState()",null,"common()","Technical Exception::::", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),"TrackingId:::"+trackingId,"checkDispostionState");
            }
            catch (PZDBViolationException e)
            {
                log.error("error",e);
                transactionLogger.error("error",e);
                PZExceptionHandler.handleDBCVEException(e,"TrackingId:::"+trackingId,"checkDispostionState");
            }
            catch (PZConstraintViolationException e)
            {
                log.error("error", e);
                transactionLogger.error("error", e);
                PZExceptionHandler.handleCVEException(e,"TrackingId:::"+trackingId,"checkDispostionState");
            }
            catch (PZTechnicalViolationException e)
            {
                log.error("error", e);
                transactionLogger.error("error", e);
                PZExceptionHandler.handleTechicalCVEException(e,"TrackingId:::"+trackingId,"checkDispostionState");
            }
            catch (PZGenericConstraintViolationException e)
            {
                log.error("error", e);
                transactionLogger.error("error", e);
                PZExceptionHandler.handleGenericCVEException(e,"TrackingId:::"+trackingId,"checkDispostionState");
            }
        }
        return commResponseVO;
    }
}
