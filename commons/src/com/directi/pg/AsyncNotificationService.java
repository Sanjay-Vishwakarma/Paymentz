package com.directi.pg;

import com.logicboxes.util.ApplicationProperties;
import com.manager.PaymentManager;
import com.manager.vo.TokenRequestVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.validators.vo.CommonValidatorVO;

import java.util.HashMap;
import java.util.concurrent.*;

/**
 * Created by Admin on 1/31/2018.
 */
public class AsyncNotificationService
{
    //private Session session;
    private static TransactionLogger transactionLogger = new TransactionLogger(AsyncNotificationService.class.getName());

    //final static ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.Settings");
    private final static String COREPOOL = ApplicationProperties.getProperty("CORE_POOL_SIZE");
    private static String MAXPOOL = ApplicationProperties.getProperty("MAX_POOL_SIZE");
    private static String KEEPALIVE = ApplicationProperties.getProperty("KEEP_ALIVE");

    public static ExecutorService executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL),Integer.parseInt(MAXPOOL),Long.parseLong(KEEPALIVE), TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    public static  AsyncNotificationService asyncNotificationService = null;
    private AsyncNotificationService()
    {
        //executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL),Integer.parseInt(MAXPOOL),Long.parseLong(KEEPALIVE), TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    }

    public static AsyncNotificationService getInstance()
    {
        if(asyncNotificationService !=null)
        {
            return asyncNotificationService;
        }
        else
        {
            return new AsyncNotificationService();
        }
    }

    public Future<String> sendNotification(TransactionDetailsVO transactionDetailsVO,String trackingid,String status,String resMessage)
    {
        return executorService.submit(new NotificatioCallable(transactionDetailsVO,trackingid,status,resMessage));
    }
    public Future<String> sendNotification(CommonValidatorVO commonValidatorVO,String status,String registrationToken)
    {
        return executorService.submit(new NotificationCallable(commonValidatorVO,status,registrationToken));
    }
    public class NotificatioCallable implements Callable<String>
    {
        //public HashMap hashMap;
        public TransactionDetailsVO transactionDetailsVO;
        public String trackingid;
        public String status;
        public String resMessage;
        //public String table;

        private NotificatioCallable(TransactionDetailsVO transactionDetailsVO,String trackingid,String status,String resMessage)
        {
            //this.hashMap = hashMap;
            this.transactionDetailsVO = transactionDetailsVO;
            this.trackingid = trackingid;
            this.status = status;
            this.resMessage = resMessage;
            //this.table = table;
        }

        public String call() throws Exception
        {
            transactionLogger.error("Inside call for AsyncNotificationService---"+trackingid+"--"+status);
            transactionLogger.error("Notification URL---"+transactionDetailsVO.getNotificationUrl());
            PaymentManager paymentManager = new PaymentManager();
            //HashMap hashMap = paymentManager.getExtnDetailsforNotification(trackingid,table);

            Functions transactionUtility = new Functions();
            transactionUtility.setMerchantNotification(transactionDetailsVO, trackingid, status, resMessage);
            transactionLogger.error("After sending Merchant Async Notification---"+trackingid);

            return "Success";
        }
    }
    public class NotificationCallable implements Callable<String>
    {
        public CommonValidatorVO commonValidatorVO;
        public String status;
        public String registrationToken;

        private NotificationCallable(CommonValidatorVO commonValidatorVO,String status,String registrationToken)
        {
            transactionLogger.error("inside NotificationCallable");
            this.commonValidatorVO=commonValidatorVO;
            this.status=status;
            this.registrationToken=registrationToken;
        }

        public String call() throws Exception
        {
            transactionLogger.error("Notification URL---"+commonValidatorVO.getTransDetailsVO().getNotificationUrl());
            Functions transactionUtility = new Functions();
            transactionUtility.setTokenNotification(commonValidatorVO,status,registrationToken);
            return "Success";
        }
    }
}
