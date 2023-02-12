package payment;

import com.directi.pg.TransactionLogger;
import com.fraud.vo.PZFraudDocVerifyResponseVO;
import com.logicboxes.util.ApplicationProperties;
import com.payment.payforasia.core.PayforasiaUtils;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.*;

/**
 * Created by SurajT on 1/31/2018.
 */
public class AsyncFraudNotificationService
{
    //private Session session;
    private static TransactionLogger transactionLogger = new TransactionLogger(AsyncFraudNotificationService.class.getName());
    //public ExecutorService executorService;

    //final static ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.Settings");
    private final static String COREPOOL = ApplicationProperties.getProperty("CORE_POOL_SIZE");
    private static String MAXPOOL = ApplicationProperties.getProperty("MAX_POOL_SIZE");
    private static String KEEPALIVE = ApplicationProperties.getProperty("KEEP_ALIVE");

    public static ExecutorService executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL),Integer.parseInt(MAXPOOL),Long.parseLong(KEEPALIVE), TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    public static AsyncFraudNotificationService asyncNotificationService = null;
    private AsyncFraudNotificationService()
    {
        //executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL),Integer.parseInt(MAXPOOL),Long.parseLong(KEEPALIVE), TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    }

    public static AsyncFraudNotificationService getInstance()
    {
        if(asyncNotificationService !=null)
        {
            return asyncNotificationService;
        }
        else
        {
            return new AsyncFraudNotificationService();
        }
    }

    public Future<String> sendNotification(PZFraudDocVerifyResponseVO pzFraudDocVerifyResponseVO,HttpServletResponse response)
    {
        return executorService.submit(new NotificatioCallable(pzFraudDocVerifyResponseVO,response));
    }
    public class NotificatioCallable implements Callable<String>
    {
        //public HashMap hashMap;

        public PZFraudDocVerifyResponseVO pzFraudDocVerifyResponseVO;
        public HttpServletResponse response;

        private NotificatioCallable(PZFraudDocVerifyResponseVO pzFraudDocVerifyResponseVO,HttpServletResponse response)
        {
            //this.hashMap = hashMap;
            this.pzFraudDocVerifyResponseVO = pzFraudDocVerifyResponseVO;
            this.response = response;

        }

        public String call() throws Exception
        {
            transactionLogger.error("Inside call for AsyncNotificationService---"+pzFraudDocVerifyResponseVO.getReference_id());
            transactionLogger.error("Notification URL---"+pzFraudDocVerifyResponseVO.getNotificationUrl());

            /*TransactionUtility transactionUtility = new TransactionUtility();
            transactionUtility.doAutoRedirectFraud(response,pzFraudDocVerifyResponseVO);*/

            String res = PayforasiaUtils.doPostHTTPSURLConnection(pzFraudDocVerifyResponseVO.getNotificationUrl(),"score="+pzFraudDocVerifyResponseVO.getScore()+"&reference_id="+pzFraudDocVerifyResponseVO.getReference_id());
            //System.out.println("response---"+res);

            transactionLogger.error("After sending Fraud Async Notification---"+pzFraudDocVerifyResponseVO.getNotificationUrl());

            return "Success";
        }
    }
}
