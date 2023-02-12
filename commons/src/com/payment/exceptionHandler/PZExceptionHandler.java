package com.payment.exceptionHandler;

import com.directi.pg.Logger;
import com.payment.Mail.AsynchronousMailService;
import com.payment.Mail.MailEventEnum;
import com.payment.Mail.MailPlaceHolder;
import com.payment.exceptionHandler.constraint.PZConstraint;
import com.payment.exceptionHandler.constraint.PZDBConstraint;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraint.PZTechnicalConstraint;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 11/21/14
 * Time: 9:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class PZExceptionHandler
{
    public static Logger log = new Logger(PZExceptionHandler.class.getName());
    public static void handleGenericCVEException(PZGenericConstraintViolationException gve,String toid,String operation)
    {
        AsynchronousMailService mailService = new AsynchronousMailService();
        //MailService mailService=new MailService();
        HashMap mailData=new HashMap();

        mailData.put(MailPlaceHolder.SUBJECT,"Generic Violation Exception");
        mailData.put(MailPlaceHolder.CLASS_NAME,gve.getPzGenericConstraint().getClassName());
        mailData.put(MailPlaceHolder.METHOD_NAME,gve.getPzGenericConstraint().getMethodName());
        mailData.put(MailPlaceHolder.MODULE_NAME,gve.getPzGenericConstraint().getModuleName());
        mailData.put(MailPlaceHolder.MESSAGE,gve.getPzGenericConstraint().getMessage());
        mailData.put(MailPlaceHolder.TOID,toid);
        mailData.put(MailPlaceHolder.OPERATION,operation);
        mailData.put(MailPlaceHolder.EXCEPTION_MESSAGE,gve.getPzGenericConstraint().getMessage());
        mailData.put(MailPlaceHolder.EXCEPTION_CAUSE,gve.getCause());
        if(gve.getCause()!=null)
        {
            mailData.put(MailPlaceHolder.EXCEPTION_CAUSE, getStackTrace(gve.getCause().getStackTrace()));
        }

        mailData.put(MailPlaceHolder.EXCEPTION,getStackTrace(gve.getStackTrace()));

        mailService.sendMerchantMonitoringAlert(MailEventEnum.EXCEPTION_DETAILS,mailData);

    }

    public static void handleCVEException(PZConstraintViolationException cve,String toid,String operation)
    {
        AsynchronousMailService mailService = new AsynchronousMailService();
        HashMap mailData=new HashMap();

        mailData.put(MailPlaceHolder.SUBJECT,"Constraint Violation Exception");
        mailData.put(MailPlaceHolder.CLASS_NAME,cve.getPzConstraint().getClassName());
        mailData.put(MailPlaceHolder.METHOD_NAME,cve.getPzConstraint().getMethodName());
        mailData.put(MailPlaceHolder.MODULE_NAME,cve.getPzConstraint().getModuleName());
        mailData.put(MailPlaceHolder.MESSAGE,cve.getPzConstraint().getMessage());
        mailData.put(MailPlaceHolder.TOID,toid);
        mailData.put(MailPlaceHolder.OPERATION,operation);
        mailData.put(MailPlaceHolder.EXCEPTION_MESSAGE,cve.getMessage());
        mailData.put(MailPlaceHolder.EXCEPTION_CAUSE,cve.getCause());
        if(cve.getCause()!=null)
        {
            mailData.put(MailPlaceHolder.EXCEPTION_CAUSE, getStackTrace(cve.getCause().getStackTrace()));
        }

        mailData.put(MailPlaceHolder.EXCEPTION,getStackTrace(cve.getStackTrace()));
        mailService.sendMerchantMonitoringAlert(MailEventEnum.EXCEPTION_DETAILS,mailData);

    }

    public static void handleDBCVEException(PZDBViolationException dbe,String toid,String operation)
    {
        AsynchronousMailService mailService = new AsynchronousMailService();
        //MailService mailService=new MailService();
        HashMap mailData=new HashMap();

        mailData.put(MailPlaceHolder.SUBJECT,"Database Violation Exception");
        mailData.put(MailPlaceHolder.CLASS_NAME,dbe.getPzdbConstraint().getClassName());
        mailData.put(MailPlaceHolder.METHOD_NAME,dbe.getPzdbConstraint().getMethodName());
        mailData.put(MailPlaceHolder.MODULE_NAME,dbe.getPzdbConstraint().getModuleName());
        mailData.put(MailPlaceHolder.MESSAGE,dbe.getPzdbConstraint().getMessage());
        mailData.put(MailPlaceHolder.TOID,toid);
        mailData.put(MailPlaceHolder.OPERATION,operation);
        mailData.put(MailPlaceHolder.EXCEPTION_MESSAGE,dbe.getMessage());
        mailData.put(MailPlaceHolder.EXCEPTION_CAUSE,dbe.getCause());
        if(dbe.getCause()!=null)
        {
            mailData.put(MailPlaceHolder.EXCEPTION_CAUSE, getStackTrace(dbe.getCause().getStackTrace()));
        }

        mailData.put(MailPlaceHolder.EXCEPTION,getStackTrace(dbe.getStackTrace()));

        mailService.sendMerchantMonitoringAlert(MailEventEnum.EXCEPTION_DETAILS,mailData);

    }

    public static void handleTechicalCVEException(PZTechnicalViolationException tve,String toid,String operation)
    {

        AsynchronousMailService mailService = new AsynchronousMailService();
        //MailService mailService=new MailService();
        HashMap mailData=new HashMap();

        mailData.put(MailPlaceHolder.SUBJECT,"Technical Violation Exception");
        mailData.put(MailPlaceHolder.CLASS_NAME,tve.getPzTechnicalConstraint().getClassName());
        mailData.put(MailPlaceHolder.METHOD_NAME,tve.getPzTechnicalConstraint().getMethodName());
        mailData.put(MailPlaceHolder.MODULE_NAME,tve.getPzTechnicalConstraint().getModuleName());
        mailData.put(MailPlaceHolder.MESSAGE,tve.getPzTechnicalConstraint().getMessage());
        mailData.put(MailPlaceHolder.TOID,toid);
        mailData.put(MailPlaceHolder.OPERATION,operation);
        mailData.put(MailPlaceHolder.EXCEPTION_MESSAGE,tve.getMessage());
        if(tve.getCause()!=null)
        {
            mailData.put(MailPlaceHolder.EXCEPTION_CAUSE, getStackTrace(tve.getCause().getStackTrace()));
        }

        mailData.put(MailPlaceHolder.EXCEPTION,getStackTrace(tve.getStackTrace()));

        mailService.sendMerchantMonitoringAlert(MailEventEnum.EXCEPTION_DETAILS,mailData);

    }


    public static void raiseConstraintViolationException(String cls, String method,String property,String moduleName, String msg, PZConstraintExceptionEnum constEnum,ErrorCodeListVO errorCodeListVO,String exceptionMsg,Throwable cause) throws PZConstraintViolationException
    {
        PZConstraint pzConstraint = new PZConstraint(cls,method,property,moduleName,msg,constEnum,errorCodeListVO);
        PZConstraintViolationException pzConstraintViolationException = new PZConstraintViolationException(pzConstraint,exceptionMsg,cause);
        throw pzConstraintViolationException;
    }

    public static void raiseDBViolationException(String cls, String method, String property,String moduleName,String msg, PZDBExceptionEnum constEnum,ErrorCodeListVO errorCodeListVO,String exceptionMsg,Throwable cause) throws PZDBViolationException
    {
        PZDBConstraint pzdbConstraint = new PZDBConstraint(cls,method,property,moduleName,msg, constEnum,errorCodeListVO);
        PZDBViolationException pzdbViolationException = new PZDBViolationException(pzdbConstraint,exceptionMsg,cause);
        throw pzdbViolationException;
    }

    public static void raiseTechnicalViolationException(String cls,String method,String property,String moduleName,String msg,PZTechnicalExceptionEnum constEnum,ErrorCodeListVO errorCodeListVO,String exceptionMsg,Throwable cause) throws PZTechnicalViolationException
    {
        PZTechnicalConstraint pzTechnicalConstraint = new PZTechnicalConstraint(cls,method,property,moduleName,msg,constEnum,errorCodeListVO);
        PZTechnicalViolationException pzTechnicalViolationException = new PZTechnicalViolationException(pzTechnicalConstraint,exceptionMsg,cause);
        throw pzTechnicalViolationException;
    }

    public static void raiseGenericViolationException(String cls,String method,String property,String moduleName,String msg,ErrorCodeListVO errorCodeListVO,String exceptionMsg,Throwable cause) throws PZGenericConstraintViolationException
    {
        PZGenericConstraint pzGenericConstraint = new PZGenericConstraint(cls, method,moduleName, property, msg,errorCodeListVO);
        PZGenericConstraintViolationException pzGenericConstraintViolationException = new PZGenericConstraintViolationException(pzGenericConstraint,exceptionMsg,cause);
        throw pzGenericConstraintViolationException;
    }

    public static void raiseAndHandleTechnicalViolationException(String cls,String method,String property,String moduleName,String msg,PZTechnicalExceptionEnum constEnum,ErrorCodeListVO errorCodeListVO,String exceptionMsg,Throwable cause,String toid, String operation)
    {
        try
        {
            raiseTechnicalViolationException(cls,method,property,moduleName,msg,constEnum,errorCodeListVO,exceptionMsg,cause);
        }
        catch(PZTechnicalViolationException tve)
        {
            log.error("----PZTechnicalViolationException in "+cls+"----",tve);
            handleTechicalCVEException(tve,toid,operation);
        }
    }
    public static void raiseAndHandleGenericViolationException(String cls,String method,String property,String moduleName,String msg,ErrorCodeListVO errorCodeListVO,String exceptionMsg,Throwable cause,String toid, String operation)
    {
        try
        {
            raiseGenericViolationException(cls,method,property,moduleName,msg,errorCodeListVO,exceptionMsg,cause);
        }
        catch(PZGenericConstraintViolationException gce)
        {
            log.error("----PZGenericConstraintViolationException in "+cls+"----",gce);
            handleGenericCVEException(gce,toid,operation);
        }
    }
    public static void raiseAndHandleDBViolationException(String cls,String method,String property,String moduleName,String msg,PZDBExceptionEnum constEnum,ErrorCodeListVO errorCodeListVO,String exceptionMsg,Throwable cause,String toid, String operation)
    {
        try
        {
            raiseDBViolationException(cls,method,property,moduleName,msg,constEnum,errorCodeListVO,exceptionMsg,cause);
        }
        catch(PZDBViolationException dbe)
        {
            log.error("----PZDBViolationException in "+cls+"----",dbe);
            handleDBCVEException(dbe,toid,operation);
        }
    }
    public static void raiseAndHandleConstraintViolationException(String cls,String method,String property,String moduleName,String msg,PZConstraintExceptionEnum constEnum,ErrorCodeListVO errorCodeListVO,String exceptionMsg,Throwable cause,String toid, String operation)
    {
        try
        {
            raiseConstraintViolationException(cls,method,property,moduleName,msg,constEnum,errorCodeListVO,exceptionMsg,cause);
        }
        catch(PZConstraintViolationException cve)
        {
            log.error("----PZConstraintViolationException in "+cls+"----",cve);
            handleCVEException(cve,toid,operation);
        }
    }

    private static String getStackTrace(StackTraceElement[] stackTraceElements)
    {
        log.debug("----inside getStackTrace ");
        StringBuilder stacktraces = new StringBuilder();
        if(stackTraceElements !=null)
        {
            int depth = stackTraceElements.length;

            for (int i = 0; i < depth; i++)
            {
                stacktraces.append("\n");
                stacktraces.append(stackTraceElements[i]);

            }
        }
        return stacktraces.toString();
    }

}
