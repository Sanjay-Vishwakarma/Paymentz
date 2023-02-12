package com.manager.vo;

import com.directi.pg.Logger;
import com.manager.dao.ActivityTrackerDAO;
import com.payment.exceptionHandler.PZDBViolationException;

import java.util.Hashtable;

/**
 * Created by Admin on 9/28/2020.
 */
public class ActivityTrackManager
{
    private static Logger logger=new Logger(ActivityTrackManager.class.getName());
    ActivityTrackerDAO activityDao=new ActivityTrackerDAO();

    public String CreateActivity(ActivityTrackerVOs activityTrackerVOs)throws PZDBViolationException
    {
        return activityDao.CreateActivity(activityTrackerVOs);
    }
    public Hashtable getActivityList(ActivityTrackerVOs activityTrackerVOs,int records, int pageno,String tdtstamp, String fdtstamp)
    {
        return activityDao.getActivityList(activityTrackerVOs, records, pageno,tdtstamp, fdtstamp);
    }
}
