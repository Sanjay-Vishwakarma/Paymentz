package com.directi.pg;

import org.apache.log4j.LogManager;
import org.apache.log4j.RollingFileAppender;

/**
 * Created by Vivek on 7/7/2021.
 */
public class LetzPayGatewayLogger extends Logger
{
    private static Logger logger= new Logger("letzpaygateway.test");
    private static volatile RollingFileAppender rollingFileAppender =setTransactionLogFileAppender() ;
    public LetzPayGatewayLogger(String classname)
    {
        super(classname);
        try
        {
            cat.addAppender(rollingFileAppender);
            cat.setAdditivity(LogManager.getLogger("letzpaygatewaylog").getAdditivity());
        }
        catch (Exception e )
        {
            logger.error("FILE formation error::",e);
        }
    }
    private static RollingFileAppender setTransactionLogFileAppender()
    {
        RollingFileAppender transactionLogAppender= (RollingFileAppender) LogManager.getLogger("letzpaygatewaylog").getAppender("letzpaygatewayProcess");
        logger.debug("List of appenders are as follows::" + transactionLogAppender.getName());
        return transactionLogAppender;
    }
}
