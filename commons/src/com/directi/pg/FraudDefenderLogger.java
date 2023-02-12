package com.directi.pg;

import org.apache.log4j.LogManager;
import org.apache.log4j.RollingFileAppender;

/**
 * Created by Vivek on 6/1/2020.
 */
public class FraudDefenderLogger extends Logger
{
    private static Logger logger= new Logger("fraudDefender.test");

    private static volatile RollingFileAppender rollingFileAppender =setTransactionLogFileAppender() ;

    public FraudDefenderLogger(String classname)
    {
        super(classname);
        try
        {
            cat.addAppender(rollingFileAppender);
            cat.setAdditivity(LogManager.getLogger("frauddefenderlog").getAdditivity());
        }
        catch (Exception e )
        {
            logger.error("FILE formation error::",e);
        }
    }

    private static RollingFileAppender setTransactionLogFileAppender()
    {
        RollingFileAppender transactionLogAppender= (RollingFileAppender) LogManager.getLogger("frauddefenderlog").getAppender("frauddefenderProcess");
        logger.debug("List of appenders are as follows::" + transactionLogAppender.getName());
        return transactionLogAppender;
    }
}
