package com.directi.pg;

import org.apache.log4j.LogManager;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.MDC;
import java.util.*;


/**
 * Created by Trupti on 16/5/2016.
 */
public class SystemAccessLogger extends Logger
{
    private static Logger logger = new Logger("systemAccess.test");

    private static volatile RollingFileAppender rollingFileAppender =setSystemAccessLogFileAppender();

    public SystemAccessLogger(String classname)
    {
        super(classname);
        try
        {
            cat.addAppender(rollingFileAppender);
            cat.setAdditivity(LogManager.getLogger("systemAccessLog").getAdditivity());
        }
        catch (Exception e )
        {
            logger.error("FILE formation error::",e);
        }
    }

    private static RollingFileAppender setSystemAccessLogFileAppender()
    {
        RollingFileAppender systemAccessLogAppender = (RollingFileAppender) LogManager.getLogger("systemAccessLog").getAppender("systemAccessProcess");
        logger.debug("List of appenders are as follows::" + systemAccessLogAppender.getName());
        return systemAccessLogAppender;

    }
}
