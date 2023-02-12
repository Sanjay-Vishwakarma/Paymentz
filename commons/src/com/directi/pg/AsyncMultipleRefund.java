package com.directi.pg;

import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.ApplicationProperties;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.AbstractPaymentProcess;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.MailService;
import com.payment.PZTransactionStatus;
import com.payment.PaymentProcessFactory;
import com.payment.request.PZRefundRequest;
import com.payment.response.PZRefundResponse;
import com.payment.response.PZResponseStatus;
import com.payment.sms.AsynchronousSmsService;
import com.payment.statussync.StatusSyncDAO;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by Admin on 6/12/2020.
 */
public class AsyncMultipleRefund
{
    //private Session session;
    private static Logger logger = new Logger(AsyncMultipleRefund.class.getName());

    //final static ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.Settings");
    private final static String COREPOOL = ApplicationProperties.getProperty("CORE_POOL_SIZE");
    private static String MAXPOOL = ApplicationProperties.getProperty("MAX_POOL_SIZE");
    private static String KEEPALIVE = ApplicationProperties.getProperty("KEEP_ALIVE");

    public static ExecutorService executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL),Integer.parseInt(MAXPOOL),Long.parseLong(KEEPALIVE), TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    public static AsyncMultipleRefund asyncMultipleRefund = null;
    private AsyncMultipleRefund()
    {
        //executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL),Integer.parseInt(MAXPOOL),Long.parseLong(KEEPALIVE), TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    }

    public static AsyncMultipleRefund getInstance()
    {
        if(asyncMultipleRefund !=null)
        {
            return asyncMultipleRefund;
        }
        else
        {
            return new AsyncMultipleRefund();
        }
    }

    public Future<String> asyncRefund(TransactionDetailsVO transactionDetailsVO,String actionExecutorId,String actionExecutorName,String refundReason,String mailReason,String status)
    {
        return executorService.submit(new MultipleRefundCallable(transactionDetailsVO,actionExecutorId,actionExecutorName,refundReason,mailReason,status));
    }
    public class MultipleRefundCallable implements Callable<String>
    {
        public TransactionDetailsVO transactionDetailsVO=null;
        String actionExecutorId="";
        String actionExecutorName="";
        String refundReason="";
        String mailReason="";
        String status="";
        private MultipleRefundCallable(TransactionDetailsVO transactionDetailsVO,String actionExecutorId,String actionExecutorName,String refundReason,String mailReason,String status)
        {
            logger.error("inside MultipleRefundCallable");
            this.transactionDetailsVO=transactionDetailsVO;
            this.actionExecutorId=actionExecutorId;
            this.actionExecutorName=actionExecutorName;
            this.refundReason=refundReason;
            this.mailReason=mailReason;
            this.status=status;
        }
        @Override
        public String call() throws Exception
        {
            Connection conn=null;
            TransactionManager transactionManager=new TransactionManager();
            String cardNumber= PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
            MerchantDAO merchantDAO=new MerchantDAO();
            MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
            logger.error("cardNumber Dyscrypt::::"+cardNumber);
            String firstSix=Functions.getFirstSix(cardNumber);
            String lastFour=Functions.getLastFour(cardNumber);
            List<TransactionDetailsVO> transactionListBin = transactionManager.getTransactionIdFromBin(firstSix, lastFour, transactionDetailsVO.getName());
            AbstractPaymentProcess process = null;
            String refundreasonForMail = mailReason;
            String refundreason = refundReason;
            List<TransactionDetailsVO> transactionDetailsVOList = new ArrayList<>();
            HashMap<String,List<TransactionDetailsVO>> memberTransactionMap=new HashMap<>();
            String mailStatus="";
            for (TransactionDetailsVO transactionDetailsVO1 : transactionListBin)
            {
                //TransactionDetailsVO transactionDetailsVO1 = transactionManager.getTransDetailFromCommon(transactionDetailsVO1.getTrackingid());
                GatewayAccount account = GatewayAccountService.getGatewayAccount(transactionDetailsVO1.getAccountId());
                merchantDetailsVO=merchantDAO.getMemberDetails(transactionDetailsVO1.getToid());
                List<String> refundFailedTransaction = new ArrayList<String>();
                List successlist = new ArrayList();
                String transactionStatus = "";
                PZRefundRequest refundRequest = new PZRefundRequest();
                String currency = account.getCurrency();
                process = PaymentProcessFactory.getPaymentProcessInstance(Integer.parseInt(transactionDetailsVO1.getTrackingid()), Integer.parseInt(transactionDetailsVO1.getAccountId()));
                logger.error("Toid:::::" + transactionDetailsVO1.getToid() + "----trackingId----" + transactionDetailsVO1.getTrackingid() + "------captureAmount----" + transactionDetailsVO1.getCaptureAmount()+"-------"+transactionDetailsVO1.getRefundAmount());
                String remainingAmt=String.format("%.2f",Double.parseDouble(transactionDetailsVO1.getCaptureAmount())-Double.parseDouble(transactionDetailsVO1.getRefundAmount()));
                logger.error("Balance Amount=" + Double.parseDouble(remainingAmt));

                refundRequest.setMemberId(Integer.valueOf(transactionDetailsVO1.getToid()));
                refundRequest.setAccountId(Integer.parseInt(transactionDetailsVO1.getAccountId()));
                refundRequest.setTrackingId(Integer.parseInt(transactionDetailsVO1.getTrackingid()));
                refundRequest.setRefundAmount(remainingAmt);
                refundRequest.setRefundReason(refundreason);
                refundRequest.setReversedAmount(transactionDetailsVO1.getRefundAmount());
                refundRequest.setTransactionStatus(transactionDetailsVO1.getStatus());
                refundRequest.setCaptureAmount(transactionDetailsVO1.getCaptureAmount());
                refundRequest.setAdmin(true);
                refundRequest.setIpAddress(null);
                //refundRequest.setFraud("Y".equals(fraud));
                currency = account.getCurrency();
                //newly added
                AuditTrailVO auditTrailVO = new AuditTrailVO();
                auditTrailVO.setActionExecutorId("0");
                auditTrailVO.setActionExecutorName(actionExecutorName);
                auditTrailVO.setCbReason(refundreason);
                refundRequest.setAuditTrailVO(auditTrailVO);

                if ("N".equalsIgnoreCase(merchantDetailsVO.getMultipleRefund()) && transactionStatus.equalsIgnoreCase(PZTransactionStatus.REVERSED.toString()))
                {
                    logger.error("Multiple refund not allowed.");
                }
                else
                {
                    PZRefundResponse refundResponse = process.refund(refundRequest);
                    logger.error("RefundResponse-------"+refundResponse);
                    PZResponseStatus refundResponseStatus = refundResponse.getStatus();
                    logger.error("status-------"+refundResponseStatus);
                    Hashtable refundDetails = null;
                    conn=Database.getConnection();
                    StatusSyncDAO statusSyncDAO = new StatusSyncDAO();

                    if (refundResponse != null && PZResponseStatus.SUCCESS.equals(refundResponseStatus))
                    {
                        successlist.add(transactionDetailsVO1.getTrackingid() + "<BR>");
                        refundDetails = new Hashtable();
                        refundDetails.put("icicitransid", transactionDetailsVO1.getTrackingid());

                        refundDetails.put("description", refundResponse.getResponseDesceiption());
                        mailStatus = "successful";
                        statusSyncDAO.updateAllRefundTransactionFlowFlag(transactionDetailsVO1.getTrackingid(), status, conn);
                        logger.error("after reversed:::");
                        if(memberTransactionMap.containsKey(transactionDetailsVO1.getToid()))
                        {
                            transactionDetailsVOList = memberTransactionMap.get(transactionDetailsVO1.getToid());
                            transactionDetailsVOList.add(transactionDetailsVO1);
                        }
                        else
                        {
                            transactionDetailsVOList = new ArrayList<>();
                            transactionDetailsVOList.add(transactionDetailsVO1);
                        }

                        memberTransactionMap.put(transactionDetailsVO1.getToid(),transactionDetailsVOList);
                    }
                    else if (refundResponse != null && PZResponseStatus.PENDING.equals(refundResponseStatus))
                    {
                        mailStatus = refundResponse.getResponseDesceiption();
                        //sErrorMessage.append(getMessageForRefund(transactionDetailsVO1.getTrackingid(), refundResponse.getStatus().toString(), refundResponse.getResponseDesceiption(), "Refund"));
                    }
                    else
                    {
                        refundFailedTransaction.add(transactionDetailsVO1.getTrackingid());
                        //sErrorMessage.append(getMessageForRefund(transactionDetailsVO1.getTrackingid(), refundResponse.getStatus().toString(), refundResponse.getResponseDesceiption(), "Refund"));
                        continue;
                    }
                }
                            /*logger.error("after AsynchronousMailService:::");
                            AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                            asynchronousMailService.sendEmail(MailEventEnum.REFUND_TRANSACTION, transactionDetailsVO1.getTrackingid(), mailStatus, refundreasonForMail, null);
                            logger.error("before AsynchronousMailService:::");*/

                logger.error("after AsynchronousSmsService:::");
                AsynchronousSmsService smsService = new AsynchronousSmsService();
                smsService.sendSMS(MailEventEnum.REFUND_TRANSACTION, transactionDetailsVO1.getTrackingid(), mailStatus, refundreasonForMail, null);
                logger.error("after AsynchronousSmsService:::");
            }
            if(memberTransactionMap != null)
            {
                Set<String> keySet = memberTransactionMap.keySet();
                for (String memberId:keySet)
                {
                    transactionDetailsVOList=memberTransactionMap.get(memberId);
                    if (transactionDetailsVOList != null && transactionDetailsVOList.size() > 0)
                    {
                        logger.error("before AsynchronousMailService:::");
                        AsynchronousMailService asynchronousMailService = new AsynchronousMailService();
                        HashMap rfMail = new HashMap();
                        MailService mailService = new MailService();
                        rfMail.put(MailPlaceHolder.TOID, memberId);
                        rfMail.put(MailPlaceHolder.STATUS, mailStatus);
                        rfMail.put(MailPlaceHolder.REASON, refundreasonForMail);
                        rfMail.put(MailPlaceHolder.MULTIPALTRANSACTION, mailService.getMultipleRefundTransaction(transactionDetailsVOList, refundreasonForMail));
                        asynchronousMailService.sendMerchantMonitoringAlert(MailEventEnum.REFUND_TRANSACTION, rfMail);
                    }
                }
            }
            return "success";
        }
    }
}
