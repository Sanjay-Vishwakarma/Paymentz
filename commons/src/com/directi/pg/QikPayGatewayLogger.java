package com.directi.pg;

import org.apache.log4j.LogManager;
import org.apache.log4j.RollingFileAppender;

/**
 * Created by Admin on 7/6/2021.
 */
public class QikPayGatewayLogger extends Logger
{
    private static Logger logger= new Logger("qikpaygateway.test");
    private static volatile RollingFileAppender rollingFileAppender =setTransactionLogFileAppender() ;
    public QikPayGatewayLogger(String classname)
    {
        super(classname);
        try
        {
            cat.addAppender(rollingFileAppender);
            cat.setAdditivity(LogManager.getLogger("qikpaygatewaylog").getAdditivity());
        }
        catch (Exception e )
        {
            logger.error("FILE formation error::",e);
        }
    }
    private static RollingFileAppender setTransactionLogFileAppender()
    {
        RollingFileAppender transactionLogAppender= (RollingFileAppender) LogManager.getLogger("qikpaygatewaylog").getAppender("qikpaygatewayProcess");
        logger.debug("List of appenders are as follows::" + transactionLogAppender.getName());
        return transactionLogAppender;
    }
}
