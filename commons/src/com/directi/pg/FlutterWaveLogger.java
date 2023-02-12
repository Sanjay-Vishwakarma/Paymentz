package com.directi.pg;

import org.apache.log4j.LogManager;
import org.apache.log4j.RollingFileAppender;

/**
 * Created by Jitendra on 13-Jun-19.
 */
public class FlutterWaveLogger extends Logger
{
    private static Logger logger= new Logger("flutterwave.test");
    private static volatile RollingFileAppender rollingFileAppender =setTransactionLogFileAppender() ;

    private static RollingFileAppender setTransactionLogFileAppender()
    {
        RollingFileAppender transactionLogAppender= (RollingFileAppender) LogManager.getLogger("flutterwavelog").getAppender("flutterwaveProcess");
        logger.debug("List of appenders are as follows::" + transactionLogAppender.getName());
        return transactionLogAppender;
    }

    public FlutterWaveLogger(String classname)
    {
        super(classname);
        try
        {
            cat.addAppender(rollingFileAppender);
            cat.setAdditivity(LogManager.getLogger("flutterwavelog").getAdditivity());
        }
        catch (Exception e )
        {
            logger.error("FILE formation error::",e);
        }
    }

}
