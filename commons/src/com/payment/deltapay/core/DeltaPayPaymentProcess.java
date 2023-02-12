package com.payment.deltapay.core;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.paymentgateway.DeltaPaymentGateway;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZConstraintViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.PZTechnicalViolationException;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.PZInquiryRequest;
import com.payment.response.PZInquiryResponse;
import com.payment.response.PZResponseStatus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 16/8/13
 * Time: 6:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class DeltaPayPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(DeltaPayPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(DeltaPayPaymentProcess.class.getName());


    public PZInquiryResponse inquiry(PZInquiryRequest pzInquiryRequest)
    {
        log.debug("Inside deltapay Inquiry");
        transactionLogger.debug("Inside deltapay Inquiry");
        PZInquiryResponse inquiryResponse = new PZInquiryResponse();
        Connection conn=null;
        try
        {
            Integer trackingId = pzInquiryRequest.getTrackingId();
            Integer accountId = pzInquiryRequest.getAccountId();
            conn = Database.getConnection();

            CommRequestVO DeltaPayRequestVO= new CommRequestVO();
            CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
            CommResponseVO DeltaPayResponseVO = null;

            String transaction = "select trackingid,paymentid,accountid,amount,currency,status,refundamount,ipaddress from transaction_common where trackingid=?";
            PreparedStatement transPreparedStatement = conn.prepareStatement(transaction);
            transPreparedStatement.setString(1, String.valueOf(trackingId));
            ResultSet rstransaction = transPreparedStatement.executeQuery();
            if (rstransaction.next())
            {
                String trackingIdDB = rstransaction.getString("trackingid");
                String accountIdDB = rstransaction.getString("accountid");

                String paymentid=rstransaction.getString("paymentid");
                log.debug("Setting Details====");
                transactionLogger.debug("Setting Details====");
                commTransactionDetailsVO.setPreviousTransactionId(paymentid);
                DeltaPayRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                DeltaPaymentGateway pg= new DeltaPaymentGateway(accountIdDB);
                DeltaPayResponseVO= (CommResponseVO) pg.processQuery(trackingIdDB,DeltaPayRequestVO);

                if(DeltaPayResponseVO!=null)
                {
                    inquiryResponse.setStatus(PZResponseStatus.SUCCESS);
                    inquiryResponse.setTrackingId(trackingIdDB);
                    inquiryResponse.setResponseTransactionId(DeltaPayResponseVO.getTransactionId());
                    inquiryResponse.setResponseTransactionStatus(DeltaPayResponseVO.getTransactionStatus());
                    inquiryResponse.setResponseCode(DeltaPayResponseVO.getErrorCode());
                    inquiryResponse.setResponseDescription(DeltaPayResponseVO.getDescription());
                    inquiryResponse.setResponseTime(DeltaPayResponseVO.getResponseTime());
                }
                else
                {
                    log.debug("Error while Inquiry Transaction. TrackingId:"+trackingId);
                    transactionLogger.debug("Error while Inquiry Transaction. TrackingId:"+trackingId);
                    inquiryResponse.setStatus(PZResponseStatus.FAILED);
                    inquiryResponse.setResponseDesceiption("Error while Inquiry Transaction. TrackingId:"+trackingId);
                    return inquiryResponse;
                }
            }
            else
            {
                inquiryResponse.setStatus(PZResponseStatus.FAILED);
                inquiryResponse.setResponseDesceiption("Transaction not found");
            }
        }
        catch (PZTechnicalViolationException e)
        {
            log.error("PZTechnicalViolationException while inquiring transaction via DeltaPay", e);
            transactionLogger.error("PZTechnicalViolationException while inquiring transaction via DeltaPay", e);

            PZExceptionHandler.handleTechicalCVEException(e, null, "cancel");

            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Your transaction is fail. Please contact your Customer Support::: " + e.getMessage());
        }
        catch (PZConstraintViolationException e)
        {
            log.error("PZConstraintViolationException while inquiring transaction via DeltaPay", e);
            transactionLogger.error("PZConstraintViolationException while inquiring transaction via DeltaPay", e);

            PZExceptionHandler.handleCVEException(e, null, "cancel");

            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Your transaction is fail. Please contact your Customer Support::: " + e.getMessage());
        }
        catch (SQLException e)
        {
            log.error("SQLException while inquiring transaction via DeltaPay",e);
            transactionLogger.error("SQLException while inquiring transaction via DeltaPay",e);

            PZExceptionHandler.raiseAndHandleDBViolationException(DeltaPayPaymentProcess.class.getName(),"cancel()",null,"common","Db violation exception", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"cancel");

            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Your transaction is fail. Please contact your Customer Support::: " + e.getMessage());
        }
        catch (SystemError systemError)
        {
            log.error("SQLException while inquiring transaction via DeltaPay", systemError);
            transactionLogger.error("SQLException while inquiring transaction via DeltaPay", systemError);

            PZExceptionHandler.raiseAndHandleDBViolationException(DeltaPayPaymentProcess.class.getName(), "cancel()", null, "common", "Db violation exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), null, "cancel");

            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Your transaction is fail. Please contact your Customer Support::: " + systemError.getMessage());
        }
       /* catch (Exception e)
        {
            log.error("exception", e);
            transactionLogger.error("exception", e);
            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error during inquiry of transaction " + e.getMessage());
        }*/

        finally
        {
            Database.closeConnection(conn);
        }
        return inquiryResponse;
    }

}
