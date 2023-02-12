package com.directi.pg;

import com.logicboxes.util.ApplicationProperties;
import com.manager.vo.ActivityTrackManager;
import com.manager.vo.ActivityTrackerVOs;
import com.payment.request.PZRefundRequest;
import java.util.concurrent.*;

/**
 * Created by Admin on 9/28/2020.
 */
public class AsyncActivityTracker
{
    //private Session session;
    private static Logger logger = new Logger(AsyncActivityTracker.class.getName());

    //final static ResourceBundle rb = LoadProperties.getProperty("com.directi.pg.Settings");
    private final static String COREPOOL = ApplicationProperties.getProperty("CORE_POOL_SIZE");
    private static String MAXPOOL = ApplicationProperties.getProperty("MAX_POOL_SIZE");
    private static String KEEPALIVE = ApplicationProperties.getProperty("KEEP_ALIVE");

    public static ExecutorService executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL),Integer.parseInt(MAXPOOL),Long.parseLong(KEEPALIVE), TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    public static  AsyncActivityTracker asyncActivityTracker = null;
    private AsyncActivityTracker()
    {
        //executorService = new ThreadPoolExecutor(Integer.parseInt(COREPOOL),Integer.parseInt(MAXPOOL),Long.parseLong(KEEPALIVE), TimeUnit.MILLISECONDS,new LinkedBlockingQueue<Runnable>());
    }

    public static AsyncActivityTracker getInstance()
    {
        if(asyncActivityTracker !=null)
        {
            return asyncActivityTracker;
        }
        else
        {
            return new AsyncActivityTracker();
        }
    }

    public Future<String> asyncActivity(ActivityTrackerVOs activityTrackerVOs)
    {
        return executorService.submit(new ActivityCreateCallable(activityTrackerVOs));
    }
    public class ActivityCreateCallable implements Callable<String>
    {
        public PZRefundRequest refundRequest=null;
        public ActivityTrackerVOs activityTrackerVOs=null;
        private ActivityCreateCallable(ActivityTrackerVOs activityTrackerVOs)
        {
            logger.error("inside ActivityCallable");
            this.activityTrackerVOs=activityTrackerVOs;
        }
        @Override
        public String call() throws Exception
        {
            logger.error("Inside call for ActivityCreateCallable Save data---");
            ActivityTrackManager ActivityTrackManager = new ActivityTrackManager();
            String CreatedActivity = ActivityTrackManager.CreateActivity(activityTrackerVOs);
            logger.error("Status of created activity::::::::::" + CreatedActivity);

            return CreatedActivity;
        }
    }
}
