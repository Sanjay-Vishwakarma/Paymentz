package com.directi.pg;

import org.apache.log4j.LogManager;
import org.apache.log4j.RollingFileAppender;

/**
 * Created by Admin on 2/14/2019.
 */
public class ElegroLogger extends  Logger
{
    private static Logger logger= new Logger("elegro.test");

    private static volatile RollingFileAppender rollingFileAppender =setTransactionLogFileAppender() ;

    public ElegroLogger(String classname)
    {
        super(classname);
        try
        {
            cat.addAppender(rollingFileAppender);
            cat.setAdditivity(LogManager.getLogger("elegrolog").getAdditivity());
        }
        catch (Exception e )
        {
            logger.error("FILE formation error::",e);
        }
    }

    private static RollingFileAppender setTransactionLogFileAppender()
    {
        RollingFileAppender transactionLogAppender= (RollingFileAppender) LogManager.getLogger("elegrolog").getAppender("elegroProcess");
        logger.debug("List of appenders are as follows::" + transactionLogAppender.getName());
        return transactionLogAppender;
    }
}
