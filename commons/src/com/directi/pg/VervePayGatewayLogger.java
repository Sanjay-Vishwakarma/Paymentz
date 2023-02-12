package com.directi.pg;

import org.apache.log4j.LogManager;
import org.apache.log4j.RollingFileAppender;

/**
 * Created by Vivek on 7/6/2021.
 */
public class VervePayGatewayLogger extends Logger
{
    private static Logger logger= new Logger("vervepaygateway.test");
    private static volatile RollingFileAppender rollingFileAppender =setTransactionLogFileAppender() ;
    public VervePayGatewayLogger(String classname)
    {
        super(classname);
        try
        {
            cat.addAppender(rollingFileAppender);
            cat.setAdditivity(LogManager.getLogger("vervepaygatewaylog").getAdditivity());
        }
        catch (Exception e )
        {
            logger.error("FILE formation error::",e);
        }
    }

    private static RollingFileAppender setTransactionLogFileAppender()
    {
        RollingFileAppender transactionLogAppender= (RollingFileAppender) LogManager.getLogger("vervepaygatewaylog").getAppender("vervepaygatewayProcess");
        logger.debug("List of appenders are as follows::" + transactionLogAppender.getName());
        return transactionLogAppender;
    }
}
