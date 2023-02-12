package com.invoice.manager;

import com.auth.AuthFunctions;
import com.directi.pg.*;
import com.invoice.dao.InvoiceEntry;
import com.invoice.vo.InvoiceVO;
import com.invoice.vo.ProductList;
import com.manager.RestDirectTransactionManager;
import com.manager.TransactionManager;
import com.manager.utils.TransactionUtil;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.merchant.manager.RegistrationManager;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeVO;
import com.payment.validators.vo.CommonValidatorVO;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.User;
import org.owasp.esapi.reference.DefaultEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Sneha on 2/9/2017.
 */
public class InvoiceManager
{
    private static Logger logger = new Logger(InvoiceManager.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(InvoiceManager.class.getName());
    private static Functions functions = new Functions();
    private static ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();

    public ErrorCodeVO formSystemErrorCodeVO(ErrorName errorName,String reason)
    {
        logger.debug("formSystemErrorCodeVO::::::::");
        logger.debug("errorName:::"+errorName);
        logger.debug("reason:::"+reason);
        ErrorCodeVO errorCodeVO = errorCodeUtils.getErrorCodeFromName(errorName);
        logger.debug("errorCodeUtils::::"+errorCodeUtils.getErrorCodeFromName(errorName));
        if(errorCodeVO!=null)
            errorCodeVO.setErrorReason(reason);

        return errorCodeVO;
    }

    public InvoiceVO processGenerateInvoice(InvoiceVO invoiceVO) throws PZDBViolationException
    {
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        //invoiceVO.setOrderDescription(invoiceVO.getDescription());
        //  invoiceVO.setOrderDescription(invoiceVO.getDescription());
        invoiceVO.setCtoken(ESAPI.randomizer().getRandomString(16, DefaultEncoder.CHAR_ALPHANUMERICS));
      //  invoiceVO = invoiceEntry.getInvoiceConfigurationDetails(invoiceVO.getMemberid());

/*
        if (invoiceVO.getCustomerDetailList() != null)
        {
            boolean isAmountMatch = invoiceEntry.getSplitInvoiceAmount(invoiceVO.getCustomerDetailList(), invoiceVO.getAmount());

            if (!isAmountMatch)
            {
                String error = "Invalid split amount added";
                errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.VALIDATION_AMOUNT, error));
                PZExceptionHandler.raiseConstraintViolationException(InvoiceManager.class.getName(), "processGenerateInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));

            }
        }
*/
        invoiceVO = invoiceEntry.insertInvoice(invoiceVO, false);

        invoiceVO = invoiceEntry.getInvoiceDetails(invoiceVO);
        return invoiceVO;
    }

    public InvoiceVO processCancelInvoice (InvoiceVO invoiceVO) throws SystemError, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        String userName=invoiceVO.getUserName();
        if(!functions.isValueNull(userName)){
            userName=invoiceVO.getMerchantDetailsVO().getLogin();
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = new Date();

        try
        {
            invoiceVO = invoiceEntry.getInvoiceDetailsByInvoiceid(invoiceVO.getMemberid(), invoiceVO.getInvoiceno(), invoiceVO);

            if (invoiceVO.getStatus().equalsIgnoreCase("Record not found"))
            {
                String error = "Memberid or Invoice id is invalid";
                errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.VALIDATION_INVOICE_NO, error));
                PZExceptionHandler.raiseConstraintViolationException(InvoiceManager.class.getName(), "processRegenerateInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));

            }

            if(functions.isValueNull(userName) && !userName.equalsIgnoreCase(invoiceVO.getRaisedby()))
            {
                String error = "You cannot Cancel invoice";
                errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.SYS_CANNOT_CANCEL_INVOICE, error));
                PZExceptionHandler.raiseConstraintViolationException(InvoiceManager.class.getName(), "processCancelInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
            }

            String expPeriod = invoiceVO.getExpirationPeriod();
            long expiredTime=Functions.getInvoiceExpiredTime(invoiceVO.getTimestamp(),expPeriod);
            Date d=new Date();
            long currenctTime=d.getTime()/1000;

            if(expiredTime<=currenctTime )
            {
                String error = "You cannot cancel invoice";
                errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.SYS_CANNOT_CANCEL_INVOICE, error));
                PZExceptionHandler.raiseConstraintViolationException(InvoiceManager.class.getName(), "processCancelInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));

            }

            if(!functions.isValueNull(invoiceVO.getStatus()))
            {
                String error = "InvoiceId not found";
                errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.SYS_INVOICE_NOT_PRESENT, error));
                PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
            }
            else if("cancelled".equals(invoiceVO.getStatus()))
            {
                String error = "You cannot cancel invoice";
                errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.SYS_CANNOT_CANCEL_INVOICE, error));
                PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
            }
            else if("processed".equals(invoiceVO.getStatus()))
            {
                String error = "You cannot cancel invoice";
                errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.SYS_CANNOT_CANCEL_INVOICE, error));
                PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
            }
            else if("regenerated".equals(invoiceVO.getStatus()))
            {
                String error = "You cannot cancel invoice";
                errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.SYS_CANNOT_CANCEL_INVOICE, error));
                PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
            }

            invoiceEntry.cancelInvoiceForRESTAPI(invoiceVO.getInvoiceno(), invoiceVO.getCancelReason());
            invoiceVO.setStatus("cancelled");
        }
        catch (ParseException e)
        {
            PZExceptionHandler.raiseGenericViolationException(InvoiceManager.class.getName(), "processCancelInvoice()", null, "Common", "System Error while connecting to the DB::", null, e.getMessage(), e.getCause());
        }

        return invoiceVO;
    }

    public InvoiceVO processRegenerateInvoice(InvoiceVO invoiceVO) throws SystemError, PZConstraintViolationException, PZDBViolationException, PZGenericConstraintViolationException
    {
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        String merchantInvoiceId = invoiceVO.getDescription();
        String userName=invoiceVO.getUserName();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = new Date();

        try
        {
            invoiceVO = invoiceEntry.getInvoiceDetailsByInvoiceid(invoiceVO.getMemberid(), invoiceVO.getInvoiceno(), invoiceVO);

            String expPeriod = invoiceVO.getExpirationPeriod();
            long expiredTime=Functions.getInvoiceExpiredTime(invoiceVO.getTimestamp(),expPeriod);
            Date d=new Date();
            long currenctTime=d.getTime()/1000;

            if(functions.isValueNull(userName) && !userName.equalsIgnoreCase(invoiceVO.getRaisedby()))
            {
                String error = "You cannot regenerate invoice";
                errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.SYS_REGENERATE_INVOICE, error));
                PZExceptionHandler.raiseConstraintViolationException(InvoiceManager.class.getName(), "processRegenerateInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
            }

            if (expiredTime>=currenctTime &&  invoiceVO.getStatus().equalsIgnoreCase("mailsent"))
            {
                String error = "You cannot regenerate invoice";
                errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.SYS_REGENERATE_INVOICE, error));
                PZExceptionHandler.raiseConstraintViolationException(InvoiceManager.class.getName(), "processRegenerateInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));

            }
            if (invoiceVO.getStatus().equalsIgnoreCase("regenerated"))
            {
                String error = "You cannot regenerate invoice";
                errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.SYS_REGENERATE_INVOICE, error));
                PZExceptionHandler.raiseConstraintViolationException(InvoiceManager.class.getName(), "processRegenerateInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
            }
            //for item list
            List<ProductList> list = new ArrayList<ProductList>();
            list=invoiceEntry.getItemList(invoiceVO.getInvoiceno());
            invoiceVO.setProductList(list);

            invoiceVO.setDescription(merchantInvoiceId);
            invoiceEntry.regenerateInvoice(invoiceVO.getInvoiceno());
            invoiceVO.setCtoken(ESAPI.randomizer().getRandomString(16, DefaultEncoder.CHAR_ALPHANUMERICS));
            invoiceVO = invoiceEntry.insertInvoice(invoiceVO, true);
            invoiceVO.setStatus("mailsent");
        }
        catch (PZDBViolationException e)
        {
            PZExceptionHandler.raiseDBViolationException(InvoiceManager.class.getName(), "processRegenerateInvoice()", null, "Common", "System Error while connecting to the DB::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, e.getMessage(), e.getCause());
        }
        catch (ParseException e)
        {
            PZExceptionHandler.raiseGenericViolationException(InvoiceManager.class.getName(), "processRegenerateInvoice()", null, "Common", "System Error while connecting to the DB::", null, e.getMessage(), e.getCause());
        }

        return invoiceVO;
    }

    public InvoiceVO processRemindInvoice(InvoiceVO invoiceVO) throws SystemError, PZConstraintViolationException, PZGenericConstraintViolationException
    {
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1 = new Date();

        try
        {
            invoiceVO = invoiceEntry.getInvoiceDetailsByInvoiceid(invoiceVO.getMemberid(), invoiceVO.getInvoiceno(), invoiceVO);

            invoiceVO = invoiceEntry.getInvoiceConfigurationDetails(invoiceVO);

            if (invoiceVO.getStatus().equalsIgnoreCase("Record not found"))
            {
                String error = "Invalid memberid or invoice id";
                errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.VALIDATION_INVOICE_NO, error));
                PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));

            }

            String expPeriod = invoiceVO.getExpirationPeriod();
            long expiredTime=Functions.getInvoiceExpiredTime(invoiceVO.getTimestamp(),expPeriod);
            Date d=new Date();
            long currenctTime=d.getTime()/1000;


            if(!functions.isValueNull(invoiceVO.getStatus()))
            {
                String error = "InvoiceId not found";
                errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.SYS_INVOICE_NOT_PRESENT, error));
                PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
            }
            else if("cancelled".equals(invoiceVO.getStatus()))
            {
                String error = "You cannot cancel invoice";
                errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.SYS_INVOICE_CANCELLED, error));
                PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
            }

            if(expiredTime<=currenctTime)
            {
                String error = "You cannot remind invoice";
                errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.SYS_CANNOT_REMIND_INVOICE, error));
                PZExceptionHandler.raiseConstraintViolationException(InvoiceManager.class.getName(), "processRegenerateInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));

            }
            /*if(invoiceVO.getReminderCounter())>6)
            {
                String error = "You cannot remind invoice more than 6 times";
                errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.SYS_CANNOT_REMIND, error));
                PZExceptionHandler.raiseConstraintViolationException(InvoiceManager.class.getName(), "processRegenerateInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
            }*/

            InvoiceEntry.remindInvoice(invoiceVO);

        }
        catch (ParseException e)
        {
            PZExceptionHandler.raiseGenericViolationException(InvoiceManager.class.getName(), "processRegenerateInvoice()", null, "Common", "System Error while connecting to the DB::", null, e.getMessage(), e.getCause());
        }

        return invoiceVO;
    }

    public InvoiceVO processInquiryInvoice(InvoiceVO invoiceVO)
    {
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        invoiceVO = invoiceEntry.getInvoiceDetails(invoiceVO);
        return invoiceVO;
    }

    public InvoiceVO GetInvoiceConfig(InvoiceVO invoiceVO)
    {
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        invoiceVO = invoiceEntry.getInvoiceConfigurationDetails(invoiceVO.getMemberid());
        return invoiceVO;
    }


    public static String generateRandom(int size)
    {
        String tokenData = "1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int len = tokenData.length();
        Date date = new Date();
        Random rand = new Random(date.getTime());
        StringBuffer token = new StringBuffer();
        int index = -1;

        for (int i = 0; i < size; i++)
        {
            index = rand.nextInt(len);
            token.append(tokenData.substring(index, index + 1));
        }
        return token.toString();
    }

    public InvoiceVO processInvoiceTransInquiry(InvoiceVO invoiceVO) throws PZConstraintViolationException
    {
        TransactionDetailsVO transactionDetailsVO = null;
        TransactionManager transactionManager = new TransactionManager();
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

        invoiceVO = invoiceEntry.getInvoiceDetails(invoiceVO);
        if(functions.isValueNull(invoiceVO.getStatus()))
        {
            transactionDetailsVO = transactionManager.getTransDetailsFromCommon(invoiceVO.getDescription(), invoiceVO.getMemberid());
            if(transactionDetailsVO == null)
            {
                String error = "INVOICE_NOT_PAID";
                errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.VALIDATION_INVOICE_NOT_PAID, error));
                PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
            }
            invoiceVO.setTransactionStatus(transactionDetailsVO.getStatus());
            invoiceVO.setAmount(transactionDetailsVO.getAmount());
            invoiceVO.setRefundAmount(transactionDetailsVO.getRefundAmount());
            invoiceVO.setCurrency(transactionDetailsVO.getCurrency());
            invoiceVO.setOrderDescription(transactionDetailsVO.getOrderDescription());
            invoiceVO.setTrackingId(transactionDetailsVO.getTrackingid());
        }
        else
        {
            String error = "No record found";
            errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.SYS_NO_RECORD_FOUND, error));
            PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
        }
        return invoiceVO;
    }


    public List<InvoiceVO> processGetInvoiceList (InvoiceVO invoiceVO) throws PZConstraintViolationException
    {
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        List<InvoiceVO> list = new ArrayList<InvoiceVO>();
        Hashtable hash = new Hashtable();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date= null;
        String fdtstamp = "";
        String tdtstamp = "";

        try
        {
            if (functions.isValueNull(invoiceVO.getPaginationVO().getStartdate()) && functions.isValueNull(invoiceVO.getPaginationVO().getEnddate()))
            {
                date = sdf.parse(invoiceVO.getPaginationVO().getStartdate());
                rightNow.setTime(date);
                String fromdate = String.valueOf(rightNow.get(Calendar.DATE));
                String frommonth = String.valueOf(rightNow.get(Calendar.MONTH));
                String fromyear = String.valueOf(rightNow.get(Calendar.YEAR));

                date = sdf.parse(invoiceVO.getPaginationVO().getEnddate());
                rightNow.setTime(date);
                String tdate = String.valueOf(rightNow.get(Calendar.DATE));
                String tmonth = String.valueOf(rightNow.get(Calendar.MONTH));
                String tyear = String.valueOf(rightNow.get(Calendar.YEAR));

                 fdtstamp = Functions.converttomillisec(frommonth, fromdate, fromyear, "0", "0", "0");
                tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");

            }

            list = invoiceEntry.listInvoicesForAPI(invoiceVO, fdtstamp, tdtstamp);
        }
        catch (ParseException e)
        {
            String error = "No record found";
            errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.SYS_NO_RECORD_FOUND, error));
            PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
        }


        return list;
    }


    //new
    public List<InvoiceVO> processGetInvoiceDetails (InvoiceVO invoiceVO) throws ParseException
    {
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        List<InvoiceVO> list = new ArrayList<InvoiceVO>();
        Hashtable hash = new Hashtable();

        String fromdate = invoiceVO.getPaginationVO().getStartdate();
        String todate = invoiceVO.getPaginationVO().getEnddate();

        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date date= null;
        String fdtstamp = "";
        String tdtstamp = "";



        if (functions.isValueNull(invoiceVO.getPaginationVO().getStartdate()) && functions.isValueNull(invoiceVO.getPaginationVO().getEnddate()))
        {
            date = sdf.parse(fromdate);
            rightNow.setTime(date);
            String fdate = String.valueOf(rightNow.get(Calendar.DATE));
            String fmonth = String.valueOf(rightNow.get(Calendar.MONTH));
            String fyear = String.valueOf(rightNow.get(Calendar.YEAR));

            //to Date
            date = sdf.parse(todate);
            rightNow.setTime(date);
            String tdate = String.valueOf(rightNow.get(Calendar.DATE));
            String tmonth = String.valueOf(rightNow.get(Calendar.MONTH));
            String tyear = String.valueOf(rightNow.get(Calendar.YEAR));

            logger.debug("fmonth " + fmonth);
            logger.debug("tmonth " + tmonth);
            fdtstamp = Functions.converttomillisec(fmonth, fdate, fyear, "0", "0", "0");
            tdtstamp = Functions.converttomillisec(tmonth, tdate, tyear, "23", "59", "59");
        }


        list = invoiceEntry.InvoiceDetailsForAPI(invoiceVO, fdtstamp, tdtstamp);

        return list;
    }

    public String processSetInvoiceConfigDetails(InvoiceVO invoiceVO) throws PZConstraintViolationException
    {
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();

/*
        if (functions.isValueNull(invoiceVO.getCurrency()))
        {
            boolean isCurrency = invoiceEntry.getMerchantCurrency(invoiceVO.getMemberid(),invoiceVO.getCurrency());
            if (!isCurrency)
            {
                String error = "You have entered invalid currency";
                errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.VALIDATION_CURRENCY, error));
                PZExceptionHandler.raiseConstraintViolationException(RestDirectTransactionManager.class.getName(), "processInvoice()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
            }
        }
*/




        boolean status = invoiceEntry.updateInvoiceConfigurationDetails(invoiceVO);
        String successStatus = "";

        if (status)
        {
            successStatus = "success";
        }
        return successStatus;
    }

    public String processGetOrderID(InvoiceVO invoiceVO)
    {
        InvoiceEntry invoiceEntry = new InvoiceEntry();
        String orderid = "";
        invoiceVO = invoiceEntry.getInvoiceConfigurationDetails(invoiceVO.getMemberid());

        String order = invoiceEntry.getOrderId(invoiceVO.getMemberid());

        orderid = invoiceVO.getInitial() + "_" + invoiceVO.getMemberid() + order;

        return orderid;

    }

    public DirectKitResponseVO getAuthToken(CommonValidatorVO commonValidatorVO,@Context HttpServletRequest request,@Context HttpServletResponse response) throws PZConstraintViolationException
    {
        HttpSession session = Functions.getNewSession(request);
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String loginName = commonValidatorVO.getMerchantDetailsVO().getLogin();
        String role = (String)request.getAttribute("role");
        String partnerid = commonValidatorVO.getParetnerId();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        AuthFunctions authFunctions = new AuthFunctions();

        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        Member member = null;
        Merchants merchants=new Merchants();

        String status = "";
        String key = "";
        String authToken = "";
        key = commonValidatorVO.getMerchantDetailsVO().getKey();

        try
        {
            //session.invalidate();
            if (functions.isValueNull(key))
            {
                authToken = authFunctions.getAuthToken(loginName,role);
            }
            else
            {
                session.invalidate();
                member = merchants.authenticate(loginName, partnerid,request);
                User user = ESAPI.authenticator().login(request, response);
                authToken = authFunctions.getAuthToken(loginName,role);
            }
            merchants.updateAuthtoken(loginName,role,authToken);

        }
        catch (Exception e)
        {
            logger.error("error in catch login---",e);
            String error = "Token generation failed";
            errorCodeListVO.addListOfError(formSystemErrorCodeVO(ErrorName.SYS_TOKEN_GENERATION_FAILED, error));
            PZExceptionHandler.raiseConstraintViolationException(InvoiceManager.class.getName(), "getAuthToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));
        }
        logger.debug("successfull login----");
        status = "success";

        directKitResponseVO.setStatus(status);
        directKitResponseVO.setAuthToken(authToken);
        directKitResponseVO.setMemberId(commonValidatorVO.getMerchantDetailsVO().getMemberId());

        directKitResponseVO.setErrorCodeListVO(errorCodeListVO);


        return directKitResponseVO;
    }

    public DirectKitResponseVO regenerateAuthToken(CommonValidatorVO commonValidatorVO,@Context HttpServletRequest request,@Context HttpServletResponse response) throws PZConstraintViolationException
    {
        HttpSession session = Functions.getNewSession(request);
        DirectKitResponseVO directKitResponseVO = new DirectKitResponseVO();
        String loginName = commonValidatorVO.getMerchantDetailsVO().getLogin();
        TransactionUtil transactionUtil = new TransactionUtil();

        String partnerid = commonValidatorVO.getParetnerId();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        AuthFunctions authFunctions = new AuthFunctions();

        ESAPI.httpUtilities().setCurrentHTTP(request, response);
        Member member = null;
        Merchants merchants=new Merchants();

        String status = "";
        String authToken = "";
        String error = "";
        String role = (String)request.getAttribute("role");
        try
        {
            boolean isTokenExpired = authFunctions.verifyExpiry(commonValidatorVO.getAuthToken(),loginName,role);
            if (isTokenExpired)
            {
                member = merchants.authenticate(loginName, partnerid, request);
                authToken = authFunctions.getAuthToken(loginName, role);
            }
            else
            {
                error = "Invalid Token";
                errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_TOKEN_GENERATION_FAILED, error));
                PZExceptionHandler.raiseConstraintViolationException(RegistrationManager.class.getName(), "processGenerateToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));

            }
        }
        catch (Exception e)
        {
            logger.error("error in catch login---",e);
            error = "Authentication failed";
            directKitResponseVO.setStatusMsg("change password failed");
            errorCodeListVO.addListOfError(transactionUtil.formSystemErrorCodeVO(ErrorName.SYS_TOKEN_GENERATION_FAILED, error));
            PZExceptionHandler.raiseConstraintViolationException(RegistrationManager.class.getName(), "processGenerateToken()", null, "Common", error, PZConstraintExceptionEnum.INVALID_PARAMETER_ENTERED, errorCodeListVO, error, new Throwable(error));

        }
        logger.debug("successfull login----");
        status = "success";

        directKitResponseVO.setStatus(status);
        directKitResponseVO.setAuthToken(authToken);
        directKitResponseVO.setMemberId(String.valueOf(member.memberid));
        directKitResponseVO.setErrorCodeListVO(errorCodeListVO);
        return directKitResponseVO;
    }



}