package com.payment.FlutterWave;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.ApplicationProperties;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.PZTransactionStatus;
import com.payment.apco.core.ApcoPayPaymentProcess;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.statussync.StatusSyncDAO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.codecs.MySQLCodec;

import java.sql.Connection;
import java.util.concurrent.*;

/**
 * Created by Admin on 12/4/2020.
 */
public class AsyncFlutterwaveQueryService
{
    private static FlutterWaveLogger transactionLogger = new FlutterWaveLogger(AsyncFlutterwaveQueryService.class.getName());

    private final static String COREPOOL = ApplicationProperties.getProperty("CORE_POOL_SIZE");
    private static String MAXPOOL = ApplicationProperties.getProperty("MAX_POOL_SIZE");
    private static String KEEPALIVE = ApplicationProperties.getProperty("KEEP_ALIVE");

    public static ExecutorService executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL),Integer.parseInt(MAXPOOL),Long.parseLong(KEEPALIVE), TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    public String paymentId;
    public String trackingid;
    public String isService;
    public String accountId;
    public String afterExecuteTime;
    public String delayTime;
    public String maxExecutionTime;
    ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    public static  AsyncFlutterwaveQueryService asyncFlutterwaveQueryService = null;
    public   AsyncFlutterwaveQueryService()
    {
        //executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL),Integer.parseInt(MAXPOOL),Long.parseLong(KEEPALIVE), TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    }
    //Timer timer;

    public AsyncFlutterwaveQueryService(String trackingid,String accountId,String paymentId,String isService,String afterExecuteTime,String delayTime,String maxExecutionTime)
    {
        //timer = new Timer();
        //timer.schedule(new RemindTask(), seconds*1000);

        callAsyncFlutterQuery(trackingid, accountId, paymentId, isService, afterExecuteTime, delayTime, maxExecutionTime);

        this.paymentId = paymentId;
        this.trackingid = trackingid;
        this.isService = isService;
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

    public static AsyncFlutterwaveQueryService getInstance()
    {
        if(asyncFlutterwaveQueryService !=null)
        {
            return asyncFlutterwaveQueryService;
        }
        else
        {
            return new AsyncFlutterwaveQueryService();
        }
    }


    public Future<String> callAsyncFlutterQuery(String trackingid,String accountId,String paymentId,String isService,String afterExecuteTime,String delayTime,String maxExecutionTime)
    {
        return executorService.submit(new FlutterPayQueryCallable(trackingid,accountId,paymentId,isService, afterExecuteTime, delayTime, maxExecutionTime));
    }
    public class FlutterPayQueryCallable implements Callable<String>
    {
        //public HashMap hashMap;
        public String paymentId;
        public String trackingid;
        public String isService;
        public String accountId;
        public String afterExecuteTime;
        public String delayTime;
        public String maxExecutionTime;

        private FlutterPayQueryCallable(String trackingid,String accountId,String paymentId,String isService,String afterExecuteTime,String delayTime,String maxExecutionTime)
        {
            this.paymentId = paymentId;
            this.trackingid = trackingid;
            this.isService = isService;
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
                        transactionLogger.error("------Inside AsyncApcoPayQueryService run() method for trackingid--------" + trackingid);

                        String status=callQuery(trackingid,accountId,paymentId,isService);
                        transactionLogger.error("ScheduledExecutorService Shutdown Called---");
                        if(!"pending".equalsIgnoreCase(status))
                            exec.shutdownNow();
                        //if(status=="successful")
                        //exec.shutdownNow();
                    }
                }, Long.parseLong(afterExecuteTime), Long.parseLong(delayTime), TimeUnit.SECONDS);
                if (!exec.awaitTermination(Long.parseLong(maxExecutionTime), TimeUnit.SECONDS))
                {
                    transactionLogger.error("Async ApcoQury awaitTermination---"+trackingid);
                    exec.shutdownNow();
                }
            }
            catch (InterruptedException ie)
            {
                transactionLogger.error("InterruptedException in ApcoQuery---",ie);
            }

            return "Success";
        }
    }
    public String callQuery(String trackingid,String accountId,String paymentId,String isService)
    {
        transactionLogger.error("------Inside AsyncFlutterwavePayQueryService callQuery method--------");
        Functions functions=new Functions();
        transactionLogger.error("paymentId--->"+paymentId);
        FlutterWavePaymentGateway flutterWavePaymentGateway=new FlutterWavePaymentGateway(accountId);
        transactionLogger.error("Inside call for AsyncApcoPayQueryService---"+trackingid+"--");
        CommTransactionDetailsVO commTransactionDetailsVO = new CommTransactionDetailsVO();
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
        CommMerchantVO commMerchantVO = new CommMerchantVO();
        MerchantDetailsVO merchantDetailsVO=new MerchantDetailsVO();
        MerchantDAO merchantDAO =new MerchantDAO();
        TransactionManager transactionManager=new TransactionManager();
        CommRequestVO commRequestVO = new CommRequestVO();
        AuditTrailVO auditTrailVO = new AuditTrailVO();
        ActionEntry entry = new ActionEntry();
        ApcoPayPaymentProcess paymentProcess = new ApcoPayPaymentProcess();
        StatusSyncDAO statusSyncDAO = new StatusSyncDAO();
        CommResponseVO genericResponseVO = null;
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
        try
        {
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);
            if (transactionDetailsVO != null)
            {
                toid = transactionDetailsVO.getToid();
                accountId = transactionDetailsVO.getAccountId();
                amount = transactionDetailsVO.getAmount();

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
                    paymentId=transactionDetailsVO.getPaymentId();
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                commRequestVO.setAddressDetailsVO(commAddressDetailsVO);
                commRequestVO.setCommMerchantVO(commMerchantVO);
                commTransactionDetailsVO.setPreviousTransactionId(paymentId);
                commTransactionDetailsVO.setOrderId(trackingid);
                commRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);
                dbStatus = transactionDetailsVO.getStatus();
                transactionLogger.error("dbStatus-----" + dbStatus);
                if (dbStatus.equalsIgnoreCase(PZTransactionStatus.AUTH_STARTED.toString()))
                {
                    entry.actionEntryFor3DCommon(trackingid, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, genericResponseVO, commRequestVO, auditTrailVO, null);
                    genericResponseVO = (CommResponseVO) flutterWavePaymentGateway.processInquiry(commRequestVO);

                    transactionLogger.error("dbstatus for trackingid--->"+dbStatus+"---"+trackingid);

                    transactionStatus = genericResponseVO.getTransactionStatus();
                    transactionId = genericResponseVO.getTransactionId();
                    message = genericResponseVO.getDescription();
                    transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);
                    dbStatus = transactionDetailsVO.getStatus();
                    transactionLogger.error("dbStatus-----" + dbStatus);
                    transactionLogger.error("transactionStatus-----" + transactionStatus);
                    if (genericResponseVO != null && dbStatus.equalsIgnoreCase(PZTransactionStatus.AUTH_STARTED.toString()))
                    {
                        StringBuffer dbBuffer = new StringBuffer();
                        if ("success".equalsIgnoreCase(transactionStatus))
                        {
                            notificationUrl=transactionDetailsVO.getNotificationUrl();
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";
                            genericResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                            dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingid);
                            transactionLogger.error("-----dbBuffer-----" + dbBuffer);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            paymentProcess.actionEntry(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, genericResponseVO, null, auditTrailVO);
                            statusSyncDAO.updateAllTransactionFlowFlag(trackingid, "capturesuccess");
                        }
                        else if ("pending".equalsIgnoreCase(transactionStatus))
                        {
                            status = "pending";
                        }
                        else if("fail".equalsIgnoreCase(transactionStatus) || "failed".equalsIgnoreCase(transactionStatus))
                        {
                            notificationUrl=transactionDetailsVO.getNotificationUrl();
                            status = "fail";
                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingid);
                            con = Database.getConnection();

                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, genericResponseVO, auditTrailVO, null);
                        }
                        if (!"pending".equalsIgnoreCase(transactionStatus))
                        {

                            if (functions.isValueNull(notificationUrl) /*&& ("3D".equals(merchantDetailsVO.getTransactionNotification())||"Both".equals(merchantDetailsVO.getTransactionNotification()))*/)
                            {
                                transactionLogger.error("inside sending notification from AsyncApcoQuery---" + notificationUrl);
                                TransactionDetailsVO transactionDetailsVO1 = transactionManager.getTransDetailFromCommon(trackingid);
                                transactionDetailsVO1.setSecretKey(merchantDetailsVO.getKey());
                                transactionDetailsVO1.setBillingDesc(billingDesc);
                                transactionDetailsVO1.setCcnum(cnum);
                                transactionDetailsVO1.setExpdate(expDate);
                                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                asyncNotificationService.sendNotification(transactionDetailsVO1, trackingid, transactionDetailsVO1.getStatus(), message);
                            }
                        }
                    }else {
                        if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                        {
                            status = "success";
                        }
                        else
                        {
                            status = "fail";
                        }
                    }
                }else {
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                    {
                        status = "success";
                    }
                    else
                    {
                        status = "fail";
                    }
                }
            }
        }
        catch (PZDBViolationException e)
        {
            transactionLogger.error("PZDBViolationException e-",e);
        }
        catch (PZGenericConstraintViolationException e)
        {
            transactionLogger.error("PZGenericConstraintViolationException e-",e);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("SystemError systemError-",systemError);
        }
        finally
        {
            Database.closeConnection(con);
        }
        return status;
    }
}
