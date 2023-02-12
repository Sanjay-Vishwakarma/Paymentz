package com.payment.mtn;

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
import com.payment.response.PZResponseStatus;
import com.payment.statussync.StatusSyncDAO;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;


import java.net.URLDecoder;
import java.sql.Connection;
import java.util.concurrent.*;

/**
 * Created by Admin on 10/21/2020.
 */
public class AsyncMTNUgandaRefundQueryService
{
    private static TransactionLogger transactionLogger = new TransactionLogger(AsyncMTNUgandaRefundQueryService.class.getName());

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
//    public static  AsyncSafexPayQueryService asyncSafexPayQueryService = null;
    public AsyncMTNUgandaRefundQueryService()
    {
        //executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL),Integer.parseInt(MAXPOOL),Long.parseLong(KEEPALIVE), TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    }
    //Timer timer;

    public AsyncMTNUgandaRefundQueryService(String trackingid, String accountId, String paymentId, String afterExecuteTime, String delayTime, String maxExecutionTime)
    {
        //timer = new Timer();
        //timer.schedule(new RemindTask(), seconds*1000);
        transactionLogger.error("inside AsyncMTNUgandaRefundQueryService -------");

        callAsyncMTNUgandaQuery(trackingid, accountId, paymentId, afterExecuteTime, delayTime, maxExecutionTime);

        this.paymentId = paymentId;
        this.trackingid = trackingid;

        this.accountId = accountId;
        this.afterExecuteTime = afterExecuteTime;
        this.delayTime = delayTime;
        this.maxExecutionTime = maxExecutionTime;

    }

    public Future<String> callAsyncMTNUgandaQuery(String trackingid,String accountId,String paymentId,String afterExecuteTime,String delayTime,String maxExecutionTime)
    {
        return executorService.submit(new MTNUgandaQueryCallable(trackingid,accountId,paymentId, afterExecuteTime, delayTime, maxExecutionTime));
    }
    public class MTNUgandaQueryCallable implements Callable<String>
    {
        //public HashMap hashMap;
        public String paymentId;
        public String trackingid;

        public String accountId;
        public String afterExecuteTime;
        public String delayTime;
        public String maxExecutionTime;

        private MTNUgandaQueryCallable(String trackingid,String accountId,String paymentId,String afterExecuteTime,String delayTime,String maxExecutionTime)
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
                        transactionLogger.error("------Inside AsyncMTNUgandaRefundQueryService run() method for trackingid--------" + trackingid);

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
                    transactionLogger.error("Async mtnuganda awaitTermination---"+trackingid);
                    exec.shutdownNow();
                }
            }
            catch (InterruptedException ie)
            {
                transactionLogger.error("InterruptedException in SafexPayQuery---",ie);
            }

            return "Success";
        }
    }
    public String callQuery(String trackingid,String accountId,String paymentId)
    {
        transactionLogger.error("------Inside AsyncMTNUgandaRefundQueryService callQuery method--------");
        Functions functions=new Functions();
        transactionLogger.error("paymentId--->"+paymentId);

        MTNUgandaPaymentGateway mtnUgandaPaymentGateway = new MTNUgandaPaymentGateway(accountId);
        transactionLogger.error("Inside call for AsyncMTNUgandaRefundQueryService --- "+ trackingid + " --");
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
        CommMerchantVO commMerchantVO = new CommMerchantVO();
        MerchantDetailsVO merchantDetailsVO = new MerchantDetailsVO();
        MerchantDAO merchantDAO =new MerchantDAO();
        TransactionManager transactionManager=new TransactionManager();
        CommRequestVO commRequestVO = new CommRequestVO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        ActionEntry entry = new ActionEntry();
        CommResponseVO commResponseVO = new CommResponseVO();
//        SafexPayPaymentProcess paymentProcess=new SafexPayPaymentProcess();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        CommResponseVO transRespDetails = null;
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
                transactionLogger.error("dbStatus 1 ====== " + dbStatus);
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

                commAddressDetailsVO.setCountry(transactionDetailsVO.getCountry());

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
                commTransactionDetailsVO.setSessionId(transactionDetailsVO.getPodBatch());
                commTransactionDetailsVO.setPrevTransactionStatus(dbStatus);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);
                dbStatus = transactionDetailsVO.getStatus();
                transactionLogger.debug("dbStatus 2 -----" + dbStatus);
                if (dbStatus.equalsIgnoreCase(PZTransactionStatus.MARKED_FOR_REVERSAL.toString()))
                {

                    transRespDetails = (CommResponseVO) mtnUgandaPaymentGateway.processQuery(trackingid, commRequestVO);

                    String responseAmount = transRespDetails.getAmount();
                    String remark = transRespDetails.getRemark();
                    String paymentid = transRespDetails.getTransactionId();
                    String responseCurrency = transRespDetails.getCurrency();
                    String responseStatus = transRespDetails.getTransactionStatus();

                    transactionLogger.error("dbstatus for trackingid--->"+dbStatus+"---"+trackingid);
                    transactionLogger.error("responseStatus--->"+responseStatus);
                    transactionLogger.error("response amount ---> "+ responseAmount);
                    transactionLogger.error("response paymentid ---> "+ paymentid);
                    transactionLogger.error("responseCurrency ---> "+ responseCurrency);

                    transactionLogger.error("--- in REFUND , reversesuccess ---"+PZTransactionStatus.MARKED_FOR_REVERSAL.toString());
                    status = "success";
                    String displayName = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();

                    auditTrailVO.setActionExecutorName("AcquirerBackEnd");
                    auditTrailVO.setActionExecutorId(toid);
                    String confirmStatus = "Y";
                    dbStatus = PZTransactionStatus.REVERSED.toString();
                    String refundstatus = "reversed";
                    String refundedAmount = transactionDetailsVO.getRefundAmount();
                    String captureAmount = transactionDetailsVO.getCaptureAmount();
                    double refAmount = Double.parseDouble(refundedAmount) + Double.parseDouble(responseAmount);

                    transactionLogger.error("status============>"+status+ "remark====>"+remark);
                    transactionLogger.error("double refAmount================>"+refAmount);
//                    transactionLogger.error("amount================>"+amount);
                    String actionEntryAction = ActionEntry.ACTION_REVERSAL_SUCCESSFUL;
                    String actionEntryStatus = ActionEntry.STATUS_REVERSAL_SUCCESSFUL;
                    transactionStatus = PZResponseStatus.SUCCESS.toString();

                    commResponseVO.setStatus("success");
                    commResponseVO.setRemark(status);
                    commResponseVO.setDescriptor(displayName);
                    commResponseVO.setTransactionStatus(status);
                    commResponseVO.setTransactionId(paymentid);
                    commResponseVO.setCurrency(responseCurrency);
                    commResponseVO.setTmpl_Amount(responseAmount);
                    commResponseVO.setTmpl_Currency(transactionDetailsVO.getTemplatecurrency());
                    commResponseVO.setDescription("Reversal Successful");

                    transactionLogger.error("captureAmount==========>"+captureAmount);
                    transactionLogger.error("refAmount============>"+refAmount);
                    if (Double.parseDouble(captureAmount) > Double.parseDouble(String.valueOf(refAmount)))
                    {
                        transactionLogger.error("inside if captureAmount > refAmount");
                        status = "reversed";
                        dbStatus = "";
                        actionEntryAction = ActionEntry.ACTION_PARTIAL_REFUND;
                        actionEntryStatus = ActionEntry.STATUS_PARTIAL_REFUND;
                        transactionStatus = PZResponseStatus.PARTIALREFUND.toString();
                    }

                    if (captureAmount.equals(String.format("%.2f", refAmount)))
                    {
                        transactionLogger.error("inside if captureAmount refAmount");
                        status = "reversed";
                        actionEntryAction = ActionEntry.ACTION_REVERSAL_SUCCESSFUL;
                        actionEntryStatus = ActionEntry.STATUS_REVERSAL_SUCCESSFUL;
                        transactionStatus = PZResponseStatus.SUCCESS.toString();
                    }
                    StringBuffer dbBuffer=new StringBuffer();

                    String updatedStatus = PZTransactionStatus.MARKED_FOR_REVERSAL.toString();

                    if("Success".equalsIgnoreCase(responseStatus))
                    {
                        dbBuffer.append("update transaction_common set status='reversed',refundAmount='" + String.format("%.2f", refAmount) + "',refundtimestamp='" + functions.getTimestamp() + "'where trackingid = " + trackingid);
                        transactionLogger.error("dbBuffer->" + dbBuffer);

                        con = Database.getConnection();
                        Database.executeUpdate(dbBuffer.toString(), con);

                        entry.actionEntryForCommon(trackingid, String.format("%.2f", refAmount), actionEntryAction, actionEntryStatus, commResponseVO, auditTrailVO, null);
                        updatedStatus = PZTransactionStatus.REVERSED.toString();
                        statusSyncDAO.updateAllTransactionFlowFlag(trackingid, updatedStatus);
                    }
                    if (functions.isValueNull(notificationUrl))
                    {
                        transactionLogger.error("inside sending notification---" + notificationUrl);
                        AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                        transactionDetailsVO.setBillingDesc(displayName);
                        asyncNotificationService.sendNotification(transactionDetailsVO, trackingid, updatedStatus, remark);
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
        catch (SystemError e)
        {
            transactionLogger.error("SystemError e-", e);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }

}
