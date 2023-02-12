package com.directi.pg;

import org.apache.log4j.LogManager;
import org.apache.log4j.RollingFileAppender;

/**
 * Created by Admin on 5/14/2019.
 */
public class CardPayLogger extends Logger
{
    private static Logger logger= new Logger("cardpay.test");

    private static volatile RollingFileAppender rollingFileAppender =setTransactionLogFileAppender() ;

    public CardPayLogger(String classname)
    {
        super(classname);
        try
        {
            cat.addAppender(rollingFileAppender);
            cat.setAdditivity(LogManager.getLogger("cardpaylog").getAdditivity());
        }
        catch (Exception e )
        {
            logger.error("FILE formation error::",e);
        }
    }

    private static RollingFileAppender setTransactionLogFileAppender()
    {
        RollingFileAppender transactionLogAppender= (RollingFileAppender) LogManager.getLogger("cardpaylog").getAppender("cardpayProcess");
        logger.debug("List of appenders are as follows::" + transactionLogAppender.getName());
        return transactionLogAppender;
    }
}
