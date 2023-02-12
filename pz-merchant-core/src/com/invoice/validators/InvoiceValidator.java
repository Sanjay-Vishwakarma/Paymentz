package com.invoice.validators;

import com.directi.pg.Functions;
import com.directi.pg.Logger;
import com.invoice.vo.InvoiceVO;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZConstraintExceptionEnum;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by Jinesh on 5/18/2015.
 */
public class InvoiceValidator
{
    private static Logger log=new Logger(InvoiceValidator.class.getName());
    public String validateInvoice(InvoiceVO invoiceVO,String ctoken) throws PZConstraintViolationException, ParseException
    {
        String error = "";
        if(invoiceVO.getCtoken()==null || !ctoken.equals(invoiceVO.getCtoken()))
        {
            error = "Invalid C-token <br> Kindly Check the Invoice for the correct details" ;
            PZExceptionHandler.raiseConstraintViolationException("InvoiceValidator.java","validateInvoice()",null,"common",error, PZConstraintExceptionEnum.CTOKEN_MISMATCH,null,null,null);
        }
        //Invoice Expiration Limit
        log.debug("exp period in check---" + Integer.parseInt(invoiceVO.getExpirationPeriod()));
        String expPeriod = invoiceVO.getExpirationPeriod();
        long expiredTime= Functions.getInvoiceExpiredTime(invoiceVO.getTimestamp(), expPeriod);
        Date d=new Date();
        long currenctTime=d.getTime()/1000;
        log.error("expiredTime--->"+expiredTime);
        log.error("currenctTime--->"+currenctTime);
        if(!"0".equals(invoiceVO.getExpirationPeriod()) && expiredTime<currenctTime)
        {
            error = "Your invoice is Expired:::Please ask your merchant to generate a fresh Invoice" ;
            PZExceptionHandler.raiseConstraintViolationException("InvoiceValidator.java","validateInvoice()",null,"common",error, PZConstraintExceptionEnum.EXPIRED_INVOICE,null,null,null);
        }
        if((invoiceVO.getStatus()==null && !invoiceVO.getStatus().equalsIgnoreCase("mailsent")) && (invoiceVO.getTransactionStatus()!= null && !(invoiceVO.getTransactionStatus().equals("failed") || invoiceVO.getTransactionStatus().equals("authfailed"))))
        {
            error = "Invalid Transaction. This transaction has been already processed" ;
            PZExceptionHandler.raiseConstraintViolationException("InvoiceValidator.java","validateInvoice()",null,"common",error, PZConstraintExceptionEnum.INVALID_INVOICE_TRANSACTION,null,null,null);
        }

        return error;
    }
}
