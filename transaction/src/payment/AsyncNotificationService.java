package payment;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.directi.pg.VoucherMoneyLogger;
import com.logicboxes.util.ApplicationProperties;
import com.manager.PaymentManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.Mail.SendTransactionEventMailUtil;

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
    private  AsyncNotificationService()
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


    public Future<String> sendNotification(TransactionDetailsVO transactionDetailsVO,String trackingid,String status,String resMessage,String table)
    {
        return executorService.submit(new NotificatioCallable(transactionDetailsVO,trackingid,status,resMessage,table));
    }
    public class NotificatioCallable implements Callable<String>
    {
        //public HashMap hashMap;
        public TransactionDetailsVO transactionDetailsVO;
        public String trackingid;
        public String status;
        public String resMessage;
        public String table;

        private NotificatioCallable(TransactionDetailsVO transactionDetailsVO,String trackingid,String status,String resMessage,String table)
        {
            //this.hashMap = hashMap;
            this.transactionDetailsVO = transactionDetailsVO;
            this.trackingid = trackingid;
            this.status = status;
            this.resMessage = resMessage;
            this.table = table;
        }

        public String call() throws Exception
        {
            Functions functions=new Functions();
            boolean isNotificationSend=true;
            transactionLogger.error("transactionDetailsVO.getTransactionMode()---" + trackingid + "--" + transactionDetailsVO.getTransactionMode());
            transactionLogger.error("transactionDetailsVO.getTransactionNotification()---" + trackingid + "--" + transactionDetailsVO.getTransactionNotification());
            if(functions.isValueNull(transactionDetailsVO.getTransactionMode()) && functions.isValueNull(transactionDetailsVO.getTransactionNotification())
                    && !(("Non-3D".equalsIgnoreCase(transactionDetailsVO.getTransactionMode()) && ("Non-3D".equalsIgnoreCase(transactionDetailsVO.getTransactionNotification()) || "Both".equalsIgnoreCase(transactionDetailsVO.getTransactionNotification())))
                     ||(("3D".equalsIgnoreCase(transactionDetailsVO.getTransactionMode()) || "3Dv1".equalsIgnoreCase(transactionDetailsVO.getTransactionMode()) || "3Dv2".equalsIgnoreCase(transactionDetailsVO.getTransactionMode()))&& ("3D".equalsIgnoreCase(transactionDetailsVO.getTransactionNotification()) || "Both".equalsIgnoreCase(transactionDetailsVO.getTransactionNotification())))))
            {
                isNotificationSend=false;
            }
            transactionLogger.error("isNotificationSend---" + trackingid + "--" + isNotificationSend);
            if(isNotificationSend)
            {
                transactionLogger.error("Inside call for AsyncNotificationService---" + trackingid + "--" + status);
                transactionLogger.error("Notification URL---" + transactionDetailsVO.getNotificationUrl());
                PaymentManager paymentManager = new PaymentManager();
                HashMap hashMap = paymentManager.getExtnDetailsforNotification(trackingid, table);

                TransactionUtility transactionUtility = new TransactionUtility();
                transactionUtility.setMerchantNotification(hashMap, transactionDetailsVO, trackingid, status, resMessage);
                transactionLogger.error("After sending Merchant Async Notification---" + trackingid);
            }

            return "Success";
        }
    }
}
