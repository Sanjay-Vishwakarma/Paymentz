package com.payment.gold24.core;

import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.paymentgateway.AbstractPaymentGateway;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.directi.pg.core.valueObjects.GenericResponseVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZGenericConstraintViolationException;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraint.PZGenericConstraint;
import com.payment.exceptionHandler.constraintType.PZTechnicalExceptionEnum;
import com.payment.exceptionHandler.errorcode.ErrorCodeUtils;
import com.payment.exceptionHandler.errorcode.errorcodeEnum.ErrorName;
import com.payment.exceptionHandler.errorcode.errorcodeVo.ErrorCodeListVO;
import com.payment.exceptionHandler.operations.PZOperations;
import com.payment.gold24.core.message.*;

import javax.xml.rpc.ServiceException;
import java.math.BigDecimal;
import java.rmi.RemoteException;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 11/1/12
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class Gold24PaymentGateway extends AbstractPaymentGateway
{

    private static Logger log = new Logger(Gold24PaymentGateway.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(Gold24PaymentGateway.class.getName());
    public static final String GATEWAY_TYPE = "Gold24";

    public Gold24PaymentGateway(String accountId)
    {
        this.accountId = accountId;
    }

    public Gold24PaymentGateway()
    {
    }

    @Override
    public GenericResponseVO processSale(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {

        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        SalesRequest salesRequest = new SalesRequest();

        salesRequest.setMerchantorderid(trackingID);
        CommMerchantVO commMerchantAccountVO = commRequestVO.getCommMerchantVO();
        salesRequest.setMerchantid(Integer.parseInt(commMerchantAccountVO.getMerchantId()));
        salesRequest.setPassword(commMerchantAccountVO.getPassword());

        GenericTransDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();
        try
        {

            String amount = transDetailsVO.getAmount();
            int txnPaise = (new BigDecimal(amount).multiply(new BigDecimal(100.00))).intValue();
            salesRequest.setAmount(txnPaise);

            salesRequest.setCurrency(transDetailsVO.getCurrency());

            CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
            salesRequest.setCardnumber(cardDetailsVO.getCardNum());
            salesRequest.setCardsecuritycode(cardDetailsVO.getcVV());
            salesRequest.setCardexpiremonth(cardDetailsVO.getExpMonth());
            salesRequest.setCardexpireyear(cardDetailsVO.getExpYear());
            log.error("cvvvvv -------->>>>>   "+cardDetailsVO.getcVV());
            transactionLogger.error("cvvvvv -------->>>>>   "+cardDetailsVO.getcVV());

            GenericAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
            salesRequest.setCardholderfirstname(addressDetailsVO.getFirstname());
            salesRequest.setCardholdersurname(addressDetailsVO.getLastname());
            salesRequest.setCardholderip(addressDetailsVO.getIp());

            salesRequest.setCardholderaddress(addressDetailsVO.getStreet());
            salesRequest.setCardholderzipcode(addressDetailsVO.getZipCode());
            salesRequest.setCardholdercity(addressDetailsVO.getCity());
            salesRequest.setCardholderstate(addressDetailsVO.getState());
            salesRequest.setCardholdercountrycode(addressDetailsVO.getCountry());
            salesRequest.setCardholderphone(addressDetailsVO.getPhone());
            salesRequest.setCardholderemail(addressDetailsVO.getEmail());

            SalesResponse salesResponse = getCommBindingStub().sales(salesRequest);

            String descritopn = G24ErrorCodes.getDescritopn(salesResponse.getErrorcode());

            commResponseVO.setStatus(salesResponse.getStatus());
            commResponseVO.setErrorCode(salesResponse.getErrorcode());
            commResponseVO.setMerchantOrderId(salesResponse.getMerchantorderid());
            commResponseVO.setTransactionId(String.valueOf(salesResponse.getTransactionid()));
            commResponseVO.setDescription(descritopn);
            commResponseVO.setDescriptor(salesResponse.getDescriptor());

        }
        catch (RemoteException re)
        {
            PZExceptionHandler.raiseTechnicalViolationException(Gold24PaymentGateway.class.getName(),"processSale()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,re.getMessage(),re.getCause());
        }
        catch (ServiceException se)
        {
            PZExceptionHandler.raiseTechnicalViolationException(Gold24PaymentGateway.class.getName(),"processSale()",null,"common","Technical Exception Occurred", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null,se.getMessage(),se.getCause());
        }
        return commResponseVO;

    }

    @Override
    public GenericResponseVO processAuthentication(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {


            CommRequestVO commRequestVO = (CommRequestVO) requestVO;
            CommResponseVO commResponseVO = new CommResponseVO();

            AuthorizeRequest authorizeRequest = new AuthorizeRequest();

            authorizeRequest.setMerchantorderid(trackingID);
            CommMerchantVO commMerchantAccountVO = commRequestVO.getCommMerchantVO();
            authorizeRequest.setMerchantid(Integer.parseInt(commMerchantAccountVO.getMerchantId()));
            authorizeRequest.setPassword(commMerchantAccountVO.getPassword());


            CommTransactionDetailsVO transDetailsVO = commRequestVO.getTransDetailsVO();

        try
        {
            String amount = transDetailsVO.getAmount();
            int txnPaise = (new BigDecimal(amount).multiply(new BigDecimal(100.00))).intValue();
            authorizeRequest.setAmount(txnPaise);

            authorizeRequest.setCurrency(transDetailsVO.getCurrency());

            CommCardDetailsVO cardDetailsVO = commRequestVO.getCardDetailsVO();
            authorizeRequest.setCardnumber(cardDetailsVO.getCardNum());
            authorizeRequest.setCardsecuritycode(cardDetailsVO.getcVV());
            authorizeRequest.setCardexpiremonth(cardDetailsVO.getExpMonth());
            authorizeRequest.setCardexpireyear(cardDetailsVO.getExpYear());

            CommAddressDetailsVO addressDetailsVO = commRequestVO.getAddressDetailsVO();
            authorizeRequest.setCardholderfirstname(addressDetailsVO.getFirstname());
            authorizeRequest.setCardholdersurname(addressDetailsVO.getLastname());
            authorizeRequest.setCardholderip(addressDetailsVO.getIp());

            authorizeRequest.setCardholderaddress(addressDetailsVO.getStreet());
            authorizeRequest.setCardholderzipcode(addressDetailsVO.getZipCode());
            authorizeRequest.setCardholdercity(addressDetailsVO.getCity());
            authorizeRequest.setCardholderstate(addressDetailsVO.getState());
            authorizeRequest.setCardholdercountrycode(addressDetailsVO.getCountry());
            authorizeRequest.setCardholderphone(addressDetailsVO.getPhone());
            authorizeRequest.setCardholderemail(addressDetailsVO.getEmail());

            AuthorizeResponse authorizeResponse = getCommBindingStub().authorize(authorizeRequest);

            commResponseVO.setStatus(authorizeResponse.getStatus());
            commResponseVO.setErrorCode(authorizeResponse.getErrorcode());
            String descritopn = G24ErrorCodes.getDescritopn(authorizeResponse.getErrorcode());
            commResponseVO.setMerchantOrderId(authorizeResponse.getMerchantorderid());
            commResponseVO.setTransactionId(String.valueOf(authorizeResponse.getTransactionid()));
            commResponseVO.setDescription(descritopn);
            commResponseVO.setDescriptor(authorizeResponse.getDescriptor());

        }
        catch (RemoteException re)
        {
            PZExceptionHandler.raiseTechnicalViolationException(Gold24PaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null, re.getMessage(), re.getCause());
        }
        catch (ServiceException se)
        {
            PZExceptionHandler.raiseTechnicalViolationException(Gold24PaymentGateway.class.getName(), "processAuthentication()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null, se.getMessage(), se.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processVoid(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {


            CommRequestVO commRequestVO = (CommRequestVO) requestVO;
            CommResponseVO commResponseVO = new CommResponseVO();

            CancelRequest cancelRequest = new CancelRequest();
            CommMerchantVO commMerchantAccountVO = commRequestVO.getCommMerchantVO();
        try
        {
            cancelRequest.setMerchantid(Integer.parseInt(commMerchantAccountVO.getMerchantId()));
            cancelRequest.setPassword(commMerchantAccountVO.getPassword());
            cancelRequest.setTransactionid(commRequestVO.getTransDetailsVO().getPreviousTransactionId());

            CancelResponse cancelResponse = getCommBindingStub().cancel(cancelRequest);

            commResponseVO.setStatus(cancelResponse.getStatus());
            commResponseVO.setErrorCode(cancelResponse.getErrorcode());
            commResponseVO.setTransactionId(cancelResponse.getTransactionid());
            String descritopn = G24ErrorCodes.getDescritopn(cancelResponse.getErrorcode());
            commResponseVO.setDescription(descritopn);

        }
        catch (RemoteException re)
        {
            PZExceptionHandler.raiseTechnicalViolationException(Gold24PaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null, re.getMessage(), re.getCause());
        }
        catch (ServiceException se)
        {
            PZExceptionHandler.raiseTechnicalViolationException(Gold24PaymentGateway.class.getName(), "processVoid()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null, se.getMessage(), se.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processRefund(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {

            CommRequestVO commRequestVO = (CommRequestVO) requestVO;
            CommResponseVO commResponseVO = new CommResponseVO();

            RefundRequest refundRequest = new RefundRequest();
        try
        {

            CommMerchantVO commMerchantAccountVO = commRequestVO.getCommMerchantVO();

            refundRequest.setMerchantid(Integer.parseInt(commMerchantAccountVO.getMerchantId()));
            refundRequest.setPassword(commMerchantAccountVO.getPassword());
            refundRequest.setTransactionid(commRequestVO.getTransDetailsVO().getPreviousTransactionId());

            String amount = commRequestVO.getTransDetailsVO().getAmount();
            int txnPaise = (new BigDecimal(amount).multiply(new BigDecimal(100.00))).intValue();
            refundRequest.setAmount(txnPaise);


            RefundResponse refundResponse = getCommBindingStub().refund(refundRequest);

            commResponseVO.setStatus(refundResponse.getStatus());
            commResponseVO.setErrorCode(refundResponse.getErrorcode());
            String descritopn = G24ErrorCodes.getDescritopn(refundResponse.getErrorcode());
            commResponseVO.setTransactionId(refundResponse.getTransactionid());
            commResponseVO.setDescription(descritopn);


        }
        catch (RemoteException re)
        {
            PZExceptionHandler.raiseTechnicalViolationException(Gold24PaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception Occurred",PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null,re.getMessage(), re.getCause());
        }
        catch (ServiceException se)
        {
            PZExceptionHandler.raiseTechnicalViolationException(Gold24PaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception Occurred",PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null,se.getMessage(), se.getCause());
        }
        return commResponseVO;
    }

    @Override
    public GenericResponseVO processCapture(String trackingID, GenericRequestVO requestVO) throws PZTechnicalViolationException
    {


        CommRequestVO commRequestVO = (CommRequestVO) requestVO;
        CommResponseVO commResponseVO = new CommResponseVO();

        CaptureRequest captureRequest = new CaptureRequest();

        CommMerchantVO commMerchantAccountVO = commRequestVO.getCommMerchantVO();

        captureRequest.setMerchantid(Integer.parseInt(commMerchantAccountVO.getMerchantId()));
        captureRequest.setPassword(commMerchantAccountVO.getPassword());
        captureRequest.setTransactionid(commRequestVO.getTransDetailsVO().getPreviousTransactionId());

        try
        {
            String amount = commRequestVO.getTransDetailsVO().getAmount();
            int txnPaise = (new BigDecimal(amount).multiply(new BigDecimal(100.00))).intValue();
            captureRequest.setAmount(txnPaise);

            CaptureResponse captureResponse = getCommBindingStub().capture(captureRequest);
            commResponseVO.setStatus(captureResponse.getStatus());
            commResponseVO.setErrorCode(captureResponse.getErrorcode());
            commResponseVO.setTransactionId(captureResponse.getTransactionid());
            String descritopn = G24ErrorCodes.getDescritopn(captureResponse.getErrorcode());
            commResponseVO.setDescription(descritopn);

        }
        catch (RemoteException re)
        {
            PZExceptionHandler.raiseTechnicalViolationException(Gold24PaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.REMOTE_EXCEPTION,null, re.getMessage(), re.getCause());
        }
        catch (ServiceException se)
        {
            PZExceptionHandler.raiseTechnicalViolationException(Gold24PaymentGateway.class.getName(), "processRefund()", null, "common", "Technical Exception Occurred", PZTechnicalExceptionEnum.SERVICE_EXCEPTION,null, se.getMessage(), se.getCause());
        }
        return commResponseVO;

    }

    @Override
    public GenericResponseVO processQuery(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        throw new PZGenericConstraintViolationException("This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::");
    }

    public GenericResponseVO processRebilling(String trackingID, GenericRequestVO requestVO) throws PZGenericConstraintViolationException
    {
        ErrorCodeUtils errorCodeUtils = new ErrorCodeUtils();
        ErrorCodeListVO errorCodeListVO = new ErrorCodeListVO();
        errorCodeListVO.addListOfError(errorCodeUtils.getErrorCodeFromName(ErrorName.SYS_RECURRINGALLOW));
        PZGenericConstraint genConstraint = new PZGenericConstraint("Gold24PaymentGateway","processRebilling",null,"common","This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",errorCodeListVO);
        throw new PZGenericConstraintViolationException(genConstraint,"This Functionality is not supported by processing gateway. Please contact your Tech. support Team:::",null);
    }

    @Override
    public String getMaxWaitDays()
    {
        return "5";
    }


    public Gold24BindingStub getCommBindingStub() throws ServiceException
    {
        Gold24BindingStub gold24BindingStub = null;

        gold24BindingStub = (Gold24BindingStub) new Gold24ServiceLocator().getInterfacePort();

        return gold24BindingStub;
    }

}