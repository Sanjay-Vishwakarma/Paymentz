package com.directi.pg;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.LogManager;
import org.apache.log4j.RollingFileAppender;

/**
 * Created by Vivek on 7/5/2021.
 */
public class SafexPayGatewayLogger extends Logger
{
    private static Logger logger= new Logger("safexpaygateway.test");
    //private static volatile RollingFileAppender rollingFileAppender =setTransactionLogFileAppender() ;
    private static volatile DailyRollingFileAppender rollingFileAppender =setTransactionLogFileAppender() ;
    public SafexPayGatewayLogger(String classname)
    {
        super(classname);
        try
        {
            cat.addAppender(rollingFileAppender);
            cat.setAdditivity(LogManager.getLogger("safexpaygatewaylog").getAdditivity());
        }
        catch (Exception e )
        {
            logger.error("FILE formation error::",e);
        }
    }
    private static DailyRollingFileAppender setTransactionLogFileAppender()
    {
        //RollingFileAppender transactionLogAppender= (RollingFileAppender) LogManager.getLogger("safexpaygatewaylog").getAppender("safexpaygatewayProcess");
        DailyRollingFileAppender transactionLogAppender= (DailyRollingFileAppender) LogManager.getLogger("safexpaygatewaylog").getAppender("safexpaygatewayProcess");
        logger.debug("List of appenders are as follows::" + transactionLogAppender.getName());
        return transactionLogAppender;
    }
}
