package com.directi.pg;

import com.logicboxes.util.ApplicationProperties;
import com.manager.RestDirectTransactionManager;
import com.payment.validators.vo.CommonValidatorVO;

import java.util.concurrent.*;

/**
 * Created by Admin on 6/15/2018.
 */
public class AsyncAPICall
{
    private static TransactionLogger transactionLogger = new TransactionLogger(AsyncAPICall.class.getName());
    public ExecutorService executorService;

    //final static ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.Settings");
    private final static String COREPOOL = ApplicationProperties.getProperty("CORE_POOL_SIZE");
    private static String MAXPOOL = ApplicationProperties.getProperty("MAX_POOL_SIZE");
    private static String KEEPALIVE = ApplicationProperties.getProperty("KEEP_ALIVE");

    private AsyncAPICall()
    {
        //executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL),Integer.parseInt(MAXPOOL),Long.parseLong(KEEPALIVE), TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    }

    public static AsyncAPICall asyncAPICall = null;
    public static AsyncAPICall getInstance()
    {
        if(asyncAPICall !=null)
        {
            return asyncAPICall;
        }
        else
        {
            return new AsyncAPICall();
        }
    }

    public Future<String> customerValidation(CommonValidatorVO commonValidatorVO)
    {
        return executorService.submit(new AsyncCallable(commonValidatorVO));
    }

    public class AsyncCallable implements Callable<String>
    {
        public CommonValidatorVO commonValidatorVO;

        private AsyncCallable(CommonValidatorVO commonValidatorVO)
        {
            this.commonValidatorVO = commonValidatorVO;
        }

        public String call() throws Exception
        {
            transactionLogger.error("Inside call for AsyncAPICall customer validation---");

            RestDirectTransactionManager restDirectTransactionManager = new RestDirectTransactionManager();

            String isValid = restDirectTransactionManager.getCustomerValidation(commonValidatorVO);

            return isValid;
        }
    }

}
