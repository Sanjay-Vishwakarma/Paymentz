package com.directi.pg;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.LogManager;


/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 11/4/14
 * Time: 1:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayGTransactionLogger extends Logger
{
    private static Logger logger= new Logger("payggateway.test");

    private static volatile DailyRollingFileAppender rollingFileAppender =setTransactionLogFileAppender() ;

    public PayGTransactionLogger(String classname)
    {
        super(classname);
        try
        {
            cat.addAppender(rollingFileAppender);
            cat.setAdditivity(LogManager.getLogger("payggatewaylog").getAdditivity());
        }
        catch (Exception e )
        {
            logger.error("FILE formation error::",e);
        }
    }

     private static DailyRollingFileAppender setTransactionLogFileAppender()
     {
         DailyRollingFileAppender transactionLogAppender= (DailyRollingFileAppender) LogManager.getLogger("payggatewaylog").getAppender("payggatewayProcess");
         logger.debug("List of appenders are as follows::" + transactionLogAppender.getName());
         return transactionLogAppender;
     }
}
