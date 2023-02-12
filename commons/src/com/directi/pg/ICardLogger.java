package com.directi.pg;

import org.apache.log4j.LogManager;
import org.apache.log4j.RollingFileAppender;

/**
 * Created by Admin on 5/9/2019.
 */
public class ICardLogger extends Logger
{
    private static Logger logger= new Logger("icard.test");

    private static volatile RollingFileAppender rollingFileAppender =setTransactionLogFileAppender() ;

    public ICardLogger(String classname)
    {
        super(classname);
        try
        {
            cat.addAppender(rollingFileAppender);
            cat.setAdditivity(LogManager.getLogger("icardlog").getAdditivity());
        }
        catch (Exception e )
        {
            logger.error("FILE formation error::",e);
        }
    }

    private static RollingFileAppender setTransactionLogFileAppender()
    {
        RollingFileAppender transactionLogAppender= (RollingFileAppender) LogManager.getLogger("icardlog").getAppender("icardProcess");
        logger.debug("List of appenders are as follows::" + transactionLogAppender.getName());
        return transactionLogAppender;
    }
}
