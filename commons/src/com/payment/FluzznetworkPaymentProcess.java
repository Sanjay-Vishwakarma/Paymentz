package com.payment;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.paymentgateway.FluzznetworkPaymentGateway;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
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
 * User: saurabh.b
 * Date: 11/8/13
 * Time: 5:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class FluzznetworkPaymentProcess  extends CommonPaymentProcess
{
    private static Logger log = new Logger(FluzznetworkPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(FluzznetworkPaymentProcess.class.getName());

    public PZInquiryResponse inquiry(PZInquiryRequest pzInquiryRequest)
    {
        log.debug("Inside Fluzznetwork Inquiry");
        transactionLogger.debug("Inside Fluzznetwork Inquiry");
        PZInquiryResponse inquiryResponse = new PZInquiryResponse();
        Connection conn=null;
        try
        {
            Integer trackingId = pzInquiryRequest.getTrackingId();
            Integer accountId = pzInquiryRequest.getAccountId();
            conn = Database.getConnection();

            CommRequestVO fluzznetworkRequestVO=new CommRequestVO();
            CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
            CommResponseVO fluzznetworkResponseVO=null;

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
                fluzznetworkRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                FluzznetworkPaymentGateway pg=new FluzznetworkPaymentGateway(accountIdDB);
                fluzznetworkResponseVO=(CommResponseVO)pg.processQuery(trackingIdDB,fluzznetworkRequestVO);

                if(fluzznetworkResponseVO!=null)
                {
                    inquiryResponse.setStatus(PZResponseStatus.SUCCESS);
                    inquiryResponse.setTrackingId(trackingIdDB);
                    inquiryResponse.setResponseTransactionId(fluzznetworkResponseVO.getTransactionId());
                    inquiryResponse.setResponseTransactionStatus(fluzznetworkResponseVO.getStatus());
                    inquiryResponse.setResponseCode(fluzznetworkResponseVO.getErrorCode());
                    inquiryResponse.setResponseDescription(fluzznetworkResponseVO.getDescription());
                    inquiryResponse.setResponseName1("ipAddress");
                    inquiryResponse.setResponseValue1(fluzznetworkResponseVO.getIpaddress());
                    inquiryResponse.setResponseName2("Amount");
                    inquiryResponse.setResponseValue2(fluzznetworkResponseVO.getAmount());
                    inquiryResponse.setResponseName3("Transaction_Type");
                    inquiryResponse.setResponseValue3(fluzznetworkResponseVO.getTransactionType());
                    inquiryResponse.setResponseDescriptor("");

                }
                else
                {
                    log.debug("Error while Inquiry Transaction. TrackingId:"+trackingId);
                    transactionLogger.debug("Error while Inquiry Transaction. TrackingId:"+trackingId);
                    inquiryResponse.setStatus(PZResponseStatus.FAILED);
                    inquiryResponse.setResponseDescription("Error while Inquiry Transaction. TrackingId:"+trackingId);
                    return inquiryResponse;
                }

            }
            else
            {
                inquiryResponse.setStatus(PZResponseStatus.FAILED);
                inquiryResponse.setResponseDesceiption("Transaction not found");
            }
        }catch (PZTechnicalViolationException e)
        {
            log.error("PZTechnicalViolationException while inquiring transaction via FluzzNetwork", e);
            transactionLogger.error("PZTechnicalViolationException while inquiring transaction via FluzzNetwork", e);

            PZExceptionHandler.handleTechicalCVEException(e, null, "cancel");

            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error during inquiry of transaction " + e.getMessage());
        }
        catch (SQLException e)
        {
            log.error("SQLException while inquiring transaction via FluzzNetwork",e);
            transactionLogger.error("SQLException while inquiring transaction via FluzzNetwork",e);

            PZExceptionHandler.raiseAndHandleDBViolationException(FluzznetworkPaymentProcess.class.getName(),"cancel()",null,"common","Db violation exception", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"cancel");

            inquiryResponse .setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error during inquiry of transaction " + e.getMessage());
        }
        catch (SystemError systemError)
        {
            log.error("SQLException while inquiring transaction via FluzzNetwork", systemError);
            transactionLogger.error("SQLException while inquiring transaction via FluzzNetwork", systemError);

            PZExceptionHandler.raiseAndHandleDBViolationException(FluzznetworkPaymentProcess.class.getName(), "cancel()", null, "common", "Db violation exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause(), null, "cancel");
            inquiryResponse.setResponseDesceiption("Error during inquiry of transaction " + systemError.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return inquiryResponse;
    }
}



