package com.payment.safexpay;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccountService;
import com.logicboxes.util.ApplicationProperties;
import com.manager.BlacklistManager;
import com.manager.TransactionManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.BlacklistVO;
import com.manager.vo.MerchantDetailsVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Enum.PZProcessType;
import com.payment.PZTransactionStatus;
import com.payment.apco.core.ApcoPayPaymentProcess;
import com.payment.apco.core.ApcoPaymentGateway;
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
 * Created by Admin on 6/25/2020.
 */
public class AsyncSafexPayQueryService
{
    private static SafexPayGatewayLogger transactionLogger = new SafexPayGatewayLogger(AsyncSafexPayQueryService.class.getName());

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
    public static  AsyncSafexPayQueryService asyncSafexPayQueryService = null;
    public   AsyncSafexPayQueryService()
    {
        //executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL),Integer.parseInt(MAXPOOL),Long.parseLong(KEEPALIVE), TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    }
    //Timer timer;

    public AsyncSafexPayQueryService(String trackingid,String accountId,String paymentId,String afterExecuteTime,String delayTime,String maxExecutionTime)
    {
        //timer = new Timer();
        //timer.schedule(new RemindTask(), seconds*1000);
        transactionLogger.error("inside AsyncSafexPayQueryService -------");

        callAsyncSafexPayQuery(trackingid, accountId, paymentId, afterExecuteTime, delayTime, maxExecutionTime);

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

    public static AsyncSafexPayQueryService getInstance()
    {
        if(asyncSafexPayQueryService !=null)
        {
            return asyncSafexPayQueryService;
        }
        else
        {
            return new AsyncSafexPayQueryService();
        }
    }


    public Future<String> callAsyncSafexPayQuery(String trackingid,String accountId,String paymentId,String afterExecuteTime,String delayTime,String maxExecutionTime)
    {
        return executorService.submit(new SafexPayQueryCallable(trackingid,accountId,paymentId, afterExecuteTime, delayTime, maxExecutionTime));
    }
    public class SafexPayQueryCallable implements Callable<String>
    {
        //public HashMap hashMap;
        public String paymentId;
        public String trackingid;

        public String accountId;
        public String afterExecuteTime;
        public String delayTime;
        public String maxExecutionTime;

        private SafexPayQueryCallable(String trackingid,String accountId,String paymentId,String afterExecuteTime,String delayTime,String maxExecutionTime)
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
                        transactionLogger.error("------Inside AsyncSafexPayQueryService run() method for trackingid--------" + trackingid);

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
                    transactionLogger.error("Async safexpay awaitTermination---"+trackingid);
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
        transactionLogger.error("------Inside AsyncSafexPayQueryService callQuery method--------");
        Functions functions=new Functions();
        transactionLogger.error("paymentId--->"+paymentId);
        SafexPayPaymentGateway safexPayPaymentGateway=new SafexPayPaymentGateway(accountId);
        transactionLogger.error("Inside call for AsyncSafexPayQueryService---"+trackingid+"--");
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
        String responseamount = "";
        String RESPONSE_CODE = "";
        String custId = "";
        String firstSix = "";
        String lastFour = "";
        String cardHolderName = "";

        try
        {
            Codec me = new MySQLCodec(MySQLCodec.Mode.STANDARD);
            TransactionDetailsVO transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);
            if (transactionDetailsVO != null)
            {

                BlacklistManager blacklistManager=new BlacklistManager();
                BlacklistVO blacklistVO=new BlacklistVO();
                toid = transactionDetailsVO.getToid();
                accountId = transactionDetailsVO.getAccountId();
                amount = transactionDetailsVO.getAmount();
                dbStatus=transactionDetailsVO.getStatus();
                custId=transactionDetailsVO.getCustomerId();
                notificationUrl="";

                if(functions.isValueNull(transactionDetailsVO.getCcnum())){
                    cnum= PzEncryptor.decryptPAN(transactionDetailsVO.getCcnum());
                    firstSix=functions.getFirstSix(cnum);
                    lastFour=functions.getLastFour(cnum);
                }
                if(functions.isValueNull(transactionDetailsVO.getName())){
                    cardHolderName=transactionDetailsVO.getName();
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
                transactionLogger.debug("dbStatus-----" + dbStatus);
                if (dbStatus.equalsIgnoreCase(PZTransactionStatus.AUTH_STARTED.toString()))
                {

                        entry.actionEntryFor3DCommon(trackingid, amount, ActionEntry.ACTION_3D_CONFIRMATION_STARTED, ActionEntry.STATUS_3D_CONFIRMATION, genericResponseVO, commRequestVO, auditTrailVO, null);
                        genericResponseVO = (CommResponseVO) safexPayPaymentGateway.processInquiry(commRequestVO);

                    transactionLogger.error("dbstatus for trackingid--->"+dbStatus+"---"+trackingid);
                    transactionLogger.error("genericResponseVO.getTransactionStatus()--->"+genericResponseVO.getTransactionStatus());

                    transactionStatus = genericResponseVO.getTransactionStatus();
                    transactionId = genericResponseVO.getTransactionId();
                    message = genericResponseVO.getDescription();
                    transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingid);
                    dbStatus = transactionDetailsVO.getStatus();
                    responseamount=genericResponseVO.getAmount();

                    Double compRsAmount= Double.valueOf(responseamount);
                    Double compDbAmount= Double.valueOf(amount);
                    transactionLogger.error("common inquiry response amount --->"+compRsAmount);
                    transactionLogger.error(" DB Amount--->"+compDbAmount);
                    if(!compDbAmount.equals(compRsAmount)&&!"Pending".equalsIgnoreCase(transactionStatus)){
                        transactionStatus= "fail";
                        message="Failed-IRA";
                        genericResponseVO.setRemark(message);
                        genericResponseVO.setDescription(message);
                        transactionLogger.error("inside else Amount incorrect--->"+responseamount);
                        RESPONSE_CODE="11111";
                        blacklistVO.setVpaAddress(custId);
                       // blacklistVO.setIpAddress(ipAddress);
                        blacklistVO.setEmailAddress(email);
                        blacklistVO.setActionExecutorId(toid);
                        blacklistVO.setActionExecutorName("AsyncSafexPayQueryService");
                        blacklistVO.setRemark("IncorrectAmount");
                        blacklistVO.setFirstSix(firstSix);
                        blacklistVO.setLastFour(lastFour);
                        blacklistVO.setName(cardHolderName);
                        blacklistManager.commonBlackListing(blacklistVO);

                    }

                    transactionLogger.debug("dbStatus-----" + dbStatus);
                    if (genericResponseVO != null && dbStatus.equalsIgnoreCase(PZTransactionStatus.AUTH_STARTED.toString()))
                    {
                        StringBuffer dbBuffer = new StringBuffer();
                        if ("success".equals(transactionStatus))
                        {
                            notificationUrl=transactionDetailsVO.getNotificationUrl();
                            billingDesc = GatewayAccountService.getGatewayAccount(accountId).getDisplayName();
                            status = "success";

                                genericResponseVO.setTransactionType(PZProcessType.THREE_D_SALE.toString());
                                dbBuffer.append("update transaction_common set captureamount='" + amount + "',paymentid='" + transactionId + "',status='capturesuccess',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "' where trackingid = " + trackingid);
                                transactionLogger.debug("db query-----" + dbBuffer);
                                con = Database.getConnection();
                                Database.executeUpdate(dbBuffer.toString(), con);
                                paymentProcess.actionEntry(trackingid, amount, ActionEntry.ACTION_CAPTURE_SUCCESSFUL, ActionEntry.STATUS_CAPTURE_SUCCESSFUL, genericResponseVO, null, auditTrailVO);
                                statusSyncDAO.updateAllTransactionFlowFlag(trackingid, "capturesuccess");


                        }
                        else if ("pending".equalsIgnoreCase(transactionStatus) || "PSP - Ticket Unavailable!".equalsIgnoreCase(message) || "ENROLLED".equalsIgnoreCase(message))
                        {
                            status = "pending";
                        }
                        else if("fail".equalsIgnoreCase(transactionStatus))
                        {
                            notificationUrl=transactionDetailsVO.getNotificationUrl();
                            status = "failed";
                            dbBuffer.append("update transaction_common set status='authfailed',paymentid='" + transactionId + "',remark='" + ESAPI.encoder().encodeForSQL(me, message) + "'");
                            dbBuffer.append(" where trackingid = " + trackingid);
                            transactionLogger.debug("db query-----" + dbBuffer);
                            con = Database.getConnection();
                            Database.executeUpdate(dbBuffer.toString(), con);
                            entry.actionEntryForCommon(trackingid, amount, ActionEntry.ACTION_AUTHORISTION_FAILED, ActionEntry.STATUS_AUTHORISTION_FAILED, genericResponseVO, auditTrailVO, null);
                        }


                        if (!"pending".equalsIgnoreCase(status))
                        {

                            transactionLogger.debug("-----dbBuffer-----" + dbBuffer);

                            if (functions.isValueNull(notificationUrl))
                            {
                                transactionLogger.error("inside sending notification from AsyncSafexQuery---" + notificationUrl);
                                TransactionDetailsVO transactionDetailsVO1 = transactionManager.getTransDetailFromCommon(trackingid);
                                transactionDetailsVO1.setSecretKey(merchantDetailsVO.getKey());
                                transactionDetailsVO1.setBillingDesc(billingDesc);
                                transactionDetailsVO1.setCcnum(cnum);
                                transactionDetailsVO1.setExpdate(expDate);
                                AsyncNotificationService asyncNotificationService = AsyncNotificationService.getInstance();
                                asyncNotificationService.sendNotification(transactionDetailsVO1, trackingid, status, message);
                            }
                        }
                    }else {
                        if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                        {
                            status = "success";
                        }
                        else if(PZTransactionStatus.AUTH_FAILED.toString().equals(dbStatus)){
                            status = "failed";
                        }
                        else
                        {
                            status = "pending";
                        }
                    }
                }else {
                    if (PZTransactionStatus.CAPTURE_SUCCESS.toString().equals(dbStatus) || PZTransactionStatus.AUTH_SUCCESS.toString().equals(dbStatus))
                    {
                        status = "success";
                    }
                    else
                    {
                        status = "failed";
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
