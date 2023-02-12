package com.directi.pg;

import org.apache.log4j.LogManager;
import org.apache.log4j.RollingFileAppender;

/**
 * Created by Jitendra on 18-Jul-19.
 */
public class UnionPayInternationalLogger extends Logger
{
    private static Logger logger= new Logger("unionpayinternational.test");
    private static volatile RollingFileAppender rollingFileAppender =setTransactionLogFileAppender() ;

    private static RollingFileAppender setTransactionLogFileAppender()
    {
        RollingFileAppender transactionLogAppender= (RollingFileAppender) LogManager.getLogger("unionpayinternationallog").getAppender("unionpayinternationalprocess");
        logger.debug("List of appenders are as follows::" + transactionLogAppender.getName());
        return transactionLogAppender;
    }

    public UnionPayInternationalLogger(String classname)
    {
        super(classname);
        try
        {
            cat.addAppender(rollingFileAppender);
            cat.setAdditivity(LogManager.getLogger("unionpayinternationallog").getAdditivity());
        }
        catch (Exception e )
        {
            logger.error("FILE formation error::",e);
        }
    }

}
