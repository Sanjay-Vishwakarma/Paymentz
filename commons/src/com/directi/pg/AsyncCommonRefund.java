package com.directi.pg;

import com.logicboxes.util.ApplicationProperties;
import com.manager.TransactionManager;
import com.manager.utils.FraudDefenderUtil;
import com.manager.vo.TransactionDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.PZTransactionStatus;
import com.payment.PaymentProcessFactory;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.request.PZRefundRequest;
import com.payment.response.PZRefundResponse;
import com.payment.response.PZResponseStatus;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;

import java.util.Date;
import java.util.concurrent.*;

/**
 * Created by Admin on 6/5/2020.
 */
public class AsyncCommonRefund
{
    //private Session session;
    private static FraudDefenderLogger transactionLogger = new FraudDefenderLogger(AsyncCommonRefund.class.getName());

    //final static ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.Settings");
    private final static String COREPOOL = ApplicationProperties.getProperty("CORE_POOL_SIZE");
    private static String MAXPOOL = ApplicationProperties.getProperty("MAX_POOL_SIZE");
    private static String KEEPALIVE = ApplicationProperties.getProperty("KEEP_ALIVE");

    public static ExecutorService executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL),Integer.parseInt(MAXPOOL),Long.parseLong(KEEPALIVE), TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    public static  AsyncCommonRefund asyncCommonRefund = null;
    private AsyncCommonRefund()
    {
        //executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL),Integer.parseInt(MAXPOOL),Long.parseLong(KEEPALIVE), TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    }

    public static AsyncCommonRefund getInstance()
    {
        if(asyncCommonRefund !=null)
        {
            return asyncCommonRefund;
        }
        else
        {
            return new AsyncCommonRefund();
        }
    }

    public Future<String> asyncRefund(PZRefundRequest refundRequest,CommonValidatorVO commonValidatorVO)
    {
        return executorService.submit(new FraudDefenderRefundCallable(refundRequest,commonValidatorVO));
    }
    public class FraudDefenderRefundCallable implements Callable<String>
    {
        public PZRefundRequest refundRequest=null;
        public CommonValidatorVO commonValidatorVO=null;
        private FraudDefenderRefundCallable(PZRefundRequest refundRequest,CommonValidatorVO commonValidatorVO)
        {
            transactionLogger.error("inside RefundCallable");
            this.refundRequest=refundRequest;
            this.commonValidatorVO=commonValidatorVO;
        }
        @Override
        public String call() throws Exception
        {
            transactionLogger.error("inside call()");
            transactionLogger.error("refundRequest.getAccountId()------>"+refundRequest.getAccountId());
            String transactionStatus=refundRequest.getTransactionStatus();
            double refundAmount=Double.parseDouble(refundRequest.getRefundAmount());
            FraudDefenderUtil fraudDefenderUtil=new FraudDefenderUtil();
            TransactionManager transactionManager=new TransactionManager();
            StatusSyncDAO statusSyncDAO=new StatusSyncDAO();
            PZResponseStatus responseStatus=null;
            ErrorCodeListVO errorCodeListVO=null;
            errorCodeListVO=fraudDefenderUtil.commonCallTypeCheckRefundFlag(commonValidatorVO);
            if (errorCodeListVO == null)
            {
                if (refundAmount > 0 && (transactionStatus.equalsIgnoreCase(PZTransactionStatus.CAPTURE_SUCCESS.toString()) || transactionStatus.equalsIgnoreCase(PZTransactionStatus.SETTLED.toString()) || transactionStatus.equalsIgnoreCase(PZTransactionStatus.REVERSED.toString())))
                {
                    Date d1 = new Date();
                    transactionLogger.error("Async Refund Start Time ####---->" + d1.getTime());
                    AbstractPaymentProcess paymentProcess = PaymentProcessFactory.getPaymentProcessInstance(refundRequest.getAccountId());
                    PZRefundResponse refundResponse = paymentProcess.refund(refundRequest);
                    responseStatus = refundResponse.getStatus();
                    transactionLogger.error("responseStatus---"+refundRequest.getTrackingId()+"--"+responseStatus);
                    try
                    {
                        if (PZResponseStatus.SUCCESS.equals(responseStatus))
                        {
                            String mailStatus = "successful";
                            String refundreasonForMail = "Current transaction have been identified as fraudulent by the system. Auto refunded by the system to avoid any future chargebacks.";
                            statusSyncDAO.updateAllRefundTransactionFlowFlag(commonValidatorVO.getTrackingid(), "reversed");
                            transactionManager.updateQueryRefundFraudTransactionDetails(commonValidatorVO.getFraudId(), "Y", String.format("%.2f", refundAmount));
                            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                            asynchronousMailService.sendEmail(MailEventEnum.REFUND_TRANSACTION, String.valueOf(refundRequest.getTrackingId()), mailStatus, refundreasonForMail, null);
                        }
                    }catch (Exception e)
                    {
                        transactionLogger.error("Execption ---"+refundRequest.getTrackingId()+"--",e);
                    }
                    transactionLogger.error("Async Refund End Time ####---->" + new Date().getTime());
                    transactionLogger.error("Async Refund Diff Time ####---->" + (new Date().getTime() - d1.getTime()));
                }
            }
            fraudDefenderUtil.commonCallTypeCheckBlacklistFlag(commonValidatorVO);
            return responseStatus.toString();
        }
    }
}
