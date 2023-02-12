package com.directi.pg;

import org.apache.log4j.LogManager;
import org.apache.log4j.RollingFileAppender;


/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 11/4/14
 * Time: 1:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class EmexpayLogger extends Logger
{
    private static Logger logger= new Logger("emexpay.test");

    private static volatile RollingFileAppender rollingFileAppender =setTransactionLogFileAppender() ;

    public EmexpayLogger(String classname)
    {
        super(classname);
        try
        {
            cat.addAppender(rollingFileAppender);
            cat.setAdditivity(LogManager.getLogger("Emexpay").getAdditivity());
        }
        catch (Exception e )
        {
            logger.error("FILE formation error::",e);
        }
    }

     private static RollingFileAppender setTransactionLogFileAppender()
     {
         RollingFileAppender transactionLogAppender= (RollingFileAppender) LogManager.getLogger("Emexpay").getAppender("emexpayProcess");
         logger.debug("List of appenders are as follows::" + transactionLogAppender.getName());
         return transactionLogAppender;
     }
}
