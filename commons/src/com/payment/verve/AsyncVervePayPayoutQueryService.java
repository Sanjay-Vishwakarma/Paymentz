package com.payment.verve;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.ApplicationProperties;
import com.manager.PaymentManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.PZTransactionStatus;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.safexpay.AsyncSafexPayQueryService;
import com.payment.safexpay.SafexPayPaymentGateway;
import com.payment.safexpay.SafexPayPaymentProcess;
import com.payment.statussync.StatusSyncDAO;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.sql.Connection;
import java.util.concurrent.*;

/**
 * Created by Admin on 5/17/2021.
 */
public class AsyncVervePayPayoutQueryService
{

    private static VervePayGatewayLogger transactionLogger = new VervePayGatewayLogger(AsyncVervePayPayoutQueryService.class.getName());

    //final static ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.Settings");
    private final static String COREPOOL = ApplicationProperties.getProperty("CORE_POOL_SIZE");
    private static String MAXPOOL = ApplicationProperties.getProperty("MAX_POOL_SIZE");
    private static String KEEPALIVE = ApplicationProperties.getProperty("KEEP_ALIVE");

    public static ExecutorService executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL),Integer.parseInt(MAXPOOL),Long.parseLong(KEEPALIVE), TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    public String paymentId;
    public String trackingid;
    public String accountId;
    public String afterExecuteTime;
    public String delayTime;
    public String maxExecutionTime;
    ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    public static AsyncVervePayPayoutQueryService asyncVervePayQueryService = null;
    public   AsyncVervePayPayoutQueryService()
    {
        //executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL),Integer.parseInt(MAXPOOL),Long.parseLong(KEEPALIVE), TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    }
    //Timer timer;

    public AsyncVervePayPayoutQueryService(String trackingid,String accountId,String paymentId,String afterExecuteTime,String delayTime,String maxExecutionTime)
    {
        //timer = new Timer();
        //timer.schedule(new RemindTask(), seconds*1000);
        transactionLogger.error("inside AsyncVervePayPayoutQueryService -------");

        callAsyncVervePayQuery(trackingid, accountId, paymentId, afterExecuteTime, delayTime, maxExecutionTime);

        this.paymentId = paymentId;
        this.trackingid = trackingid;

        this.accountId = accountId;
        this.afterExecuteTime = afterExecuteTime;
        this.delayTime = delayTime;
        this.maxExecutionTime = maxExecutionTime;

    }
    /*class RemindTask extends TimerTask
    {
        public void run()
        {


            timer.cancel(); //Terminate the timer thread
        }
    }*/

    public static AsyncVervePayPayoutQueryService getInstance()
    {
        if(asyncVervePayQueryService !=null)
        {
            return asyncVervePayQueryService;
        }
        else
        {
            return new AsyncVervePayPayoutQueryService();
        }
    }


    public Future<String> callAsyncVervePayQuery(String trackingid,String accountId,String paymentId,String afterExecuteTime,String delayTime,String maxExecutionTime)
    {
        return executorService.submit(new VervePayQueryCallable(trackingid,accountId,paymentId, afterExecuteTime, delayTime, maxExecutionTime));
    }
    public class VervePayQueryCallable implements Callable<String>
    {
        //public HashMap hashMap;
        public String paymentId;
        public String trackingid;

        public String accountId;
        public String afterExecuteTime;
        public String delayTime;
        public String maxExecutionTime;

        private VervePayQueryCallable(String trackingid,String accountId,String paymentId,String afterExecuteTime,String delayTime,String maxExecutionTime)
        {
            this.paymentId = paymentId;
            this.trackingid = trackingid;
            this.accountId = accountId;
            this.afterExecuteTime = afterExecuteTime;
            this.delayTime = delayTime;
            this.maxExecutionTime = maxExecutionTime;
        }

        public String call() throws Exception
        {
            try
            {

                transactionLogger.error("afterExecuteTime-->"+afterExecuteTime);
                transactionLogger.error("delayTime-->"+delayTime);
                transactionLogger.error("maxExecutionTime-->"+maxExecutionTime);
                exec.scheduleAtFixedRate(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        // do stuff
                        transactionLogger.error("------Inside AsyncVervePayPayoutQueryService run() method for trackingid--------" + trackingid);

                        String status=callQuery(trackingid,accountId,paymentId);
                        transactionLogger.error("ScheduledExecutorService Shutdown Called---");
                        if(!"pending".equalsIgnoreCase(status))
                            exec.shutdownNow();
                        //if(status=="successful")
                        //exec.shutdownNow();
                    }
                }, Long.parseLong(afterExecuteTime), Long.parseLong(delayTime), TimeUnit.SECONDS);
                if (!exec.awaitTermination(Long.parseLong(maxExecutionTime), TimeUnit.SECONDS))
                {
                    transactionLogger.error("Async VervePay awaitTermination---"+trackingid);
                    exec.shutdownNow();
                }
            }
            catch (InterruptedException ie)
            {
                transactionLogger.error("InterruptedException in VervePayQuery---",ie);
            }

            return "Success";
        }
    }
    public String callQuery(String trackingid,String accountId,String paymentId)
    {
        transactionLogger.error("------Inside AsyncVervePayPayoutQueryService callQuery method--------");
        Functions functions=new Functions();
        transactionLogger.error("paymentId--->"+paymentId);
        VervePaymentGateway vervePaymentGateway=new VervePaymentGateway(accountId);
        transactionLogger.error("Inside call for AsyncVervePayPayoutQueryService---"+trackingid+"--");
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
        CommMerchantVO commMerchantVO = new CommMerchantVO();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        MerchantDAO merchantDAO =new MerchantDAO();
        TransactionManager transactionManager=new TransactionManager();
        CommRequestVO commRequestVO = new CommRequestVO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        ActionEntry entry = new ActionEntry();
        SafexPayPaymentProcess paymentProcess=new SafexPayPaymentProcess();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        CommResponseVO genericResponseVO = null;
        LimitChecker limitChecker=new LimitChecker();
        Connection con=null;
        String transType="Sale";
        String transactionStatus = "";
        String transactionId = "";
        String message = "";
        String amount = "";
        String status = "";
        String dbStatus = "";
        String billingDesc = "";
        String toid = "";
        String notificationUrl = "";
        String cnum = "";
        String expDate = "";
        String email = "";
        String authorization_code = "";
        try
        {
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);
            if (transactionDetailsVO != null)
            {
                toid = transactionDetailsVO.getToid();
                accountId = transactionDetailsVO.getAccountId();
                amount = transactionDetailsVO.getAmount();
                dbStatus=transactionDetailsVO.getStatus();
                authorization_code=transactionDetailsVO.getAuthorization_code();
                notificationUrl=transactionDetailsVO.getNotificationUrl();

                if(functions.isValueNull(transactionDetailsVO.getCcnum())){
                    cnum= PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                }
                if(functions.isValueNull(transactionDetailsVO.getExpdate())){
                    expDate=PzEncryptor.decryptExpiryDate(transactionDetailsVO.getExpdate());
                }
                email = transactionDetailsVO.getEmailaddr();

                auditTrailVO.setActionExecutorName("AcquirerGateway");
                auditTrailVO.setActionExecutorId(toid);
                merchantDetailsVO = merchantDAO.getMemberDetails(toid);

                if(!functions.isValueNull(paymentId))
                {
                    paymentId=transactionDetailsVO.getPaymentId();
                }
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
                commRequestVO.setCommMerchantVO(commMerchantVO);
                commTransactionDetailsVO.setPreviousTransactionId(paymentId);
                commTransactionDetailsVO.setOrderId(trackingid);
                commTransactionDetailsVO.setAuthorization_code(authorization_code);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);
                dbStatus = transactionDetailsVO.getStatus();
                transactionLogger.debug("dbStatus-----" + dbStatus);
                if (dbStatus.equalsIgnoreCase(PZTransactionStatus.PAYOUT_STARTED.toString()))
                {

                    // entry.actionEntryFor3DCommon(trackingid, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, genericResponseVO, commRequestVO, auditTrailVO, null);
                    genericResponseVO = (CommResponseVO) vervePaymentGateway.processPayoutInquiry(trackingid, commRequestVO);

                    transactionLogger.error("dbstatus for trackingid--->"+dbStatus+"---"+trackingid);
                    transactionLogger.error("genericResponseVO.getTransactionStatus()--->"+genericResponseVO.getTransactionStatus());

                    transactionStatus = genericResponseVO.getTransactionStatus();
                    transactionId = genericResponseVO.getTransactionId();
                    message = genericResponseVO.getDescription();
                    transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);
                    dbStatus = transactionDetailsVO.getStatus();
                    transactionLogger.debug("dbStatus-----" + dbStatus);
                    PaymentManager paymentManager=new PaymentManager();
                    if (genericResponseVO != null && dbStatus.equalsIgnoreCase(PZTransactionStatus.PAYOUT_STARTED.toString()))
                    {
                        StringBuffer dbBuffer = new StringBuffer();
                        if ("success".equals(transactionStatus))
                        {
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";

                           /* dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='payoutsuccessful',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingid);
                            transactionLogger.debug("db query-----" + dbBuffer);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);*/
                            paymentManager.updateTransactionStatusAfterResponse(String.valueOf(trackingid), ActionEntry.STATUS_PAYOUT_SUCCESSFUL, amount, genericResponseVO.getDescription(), genericResponseVO.getTransactionId(),null);
                            paymentProcess.actionEntry(String.valueOf(trackingid), amount, ActionEntry.ACTION_PAYOUT_SUCCESSFUL, ActionEntry.STATUS_PAYOUT_SUCCESSFUL, genericResponseVO, null, auditTrailVO, "Payout Created Successfully");
                            //update status flags
                            statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid, transactionStatus);
                            limitChecker.updatePayoutAmountOnAccountid(accountId, amount);

                        }
                        else if ("pending".equalsIgnoreCase(transactionStatus) || "PSP - Ticket Unavailable!".equalsIgnoreCase(message) || "ENROLLED".equalsIgnoreCase(message))
                        {
                            status = "pending";
                        }
                        else if("fail".equalsIgnoreCase(transactionStatus)||"failed".equalsIgnoreCase(transactionStatus))
                        {
                            status = "failed";

                            paymentManager.updateTransactionStatusAfterResponse(String.valueOf(trackingid), ActionEntry.STATUS_PAYOUT_FAILED, amount, genericResponseVO.getDescription(), genericResponseVO.getTransactionId(),null);
                            paymentProcess.actionEntry(String.valueOf(trackingid), amount, ActionEntry.ACTION_PAYOUT_FAILED, ActionEntry.STATUS_PAYOUT_FAILED, genericResponseVO, null, auditTrailVO, "Payout Created Successfully");

                            statusSyncDAO.updateReconciliationTransactionCronFlag(trackingid, transactionStatus);

                        }


                        if (!"pending".equalsIgnoreCase(transactionStatus))
                        {

                            if (functions.isValueNull(notificationUrl))
                            {
                                transactionLogger.error("inside sending notification from AsyncpayoutSafexQuery---" + notificationUrl);
                                TransactionDetailsVO transactionDetailsVO1 = transactionManager.getTransDetailFromCommon(trackingid);
                                transactionDetailsVO1.setSecretKey(merchantDetailsVO.getKey());
                                transactionDetailsVO1.setBillingDesc(billingDesc);
                                transactionDetailsVO1.setCcnum(cnum);
                                transactionDetailsVO1.setExpdate(expDate);
                                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                asyncNotificationService.sendNotification(transactionDetailsVO1, trackingid, status, message);
                            }
                        }
                    }
                }
            }
        }

        /*catch (PZConstraintViolationException e)
        {
            transactionLogger.error("PZConstraintViolationException e-",e);
        }*/
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException e-",e);
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException e-",e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }
}
