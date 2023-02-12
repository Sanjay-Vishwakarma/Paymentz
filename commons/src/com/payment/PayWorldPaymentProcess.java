package com.payment;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.paymentgateway.PayWorldPaymentGateway;
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
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 7/24/13
 * Time: 3:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class PayWorldPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(PayWorldPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayWorldPaymentProcess.class.getName());

    public PZInquiryResponse inquiry(PZInquiryRequest pzInquiryRequest)
    {
        log.debug("Inside payworld Inquiry");
        transactionLogger.debug("Inside payworld Inquiry");
        PZInquiryResponse inquiryResponse = new PZInquiryResponse();
        Connection conn=null;
        try
        {
            Integer trackingId = pzInquiryRequest.getTrackingId();
            Integer accountId = pzInquiryRequest.getAccountId();
            conn = Database.getConnection();

            CommRequestVO PayworldRequestVO= new CommRequestVO();
            CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
            CommResponseVO PayworldResponseVO = null;

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
                PayworldRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                PayWorldPaymentGateway pg= new PayWorldPaymentGateway(accountIdDB);
                PayworldResponseVO= (CommResponseVO) pg.processQuery(trackingIdDB,PayworldRequestVO);

                if(PayworldResponseVO!=null)
                {
                    inquiryResponse.setStatus(PZResponseStatus.SUCCESS);
                    inquiryResponse.setTrackingId(trackingIdDB);
                    inquiryResponse.setResponseTransactionId(PayworldResponseVO.getTransactionId());
                    inquiryResponse.setResponseTransactionStatus(PayworldResponseVO.getStatus());
                    inquiryResponse.setResponseCode(PayworldResponseVO.getErrorCode());
                    inquiryResponse.setResponseDescription(PayworldResponseVO.getTransactionType());
                    inquiryResponse.setResponseTime(PayworldResponseVO.getResponseTime());
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
            log.error("PzTechnical Exception while refunding transaction VIA Pay World",e);
            transactionLogger.error("PzTechnical Exception while refunding transaction VIA Pay World",e);

            PZExceptionHandler.handleTechicalCVEException(e, null, "inquiry");
            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error during reversal of transaction " + e.getMessage());
        }
        catch (SQLException e)
        {
            log.error("SQLException while refunding transaction VIA Pay World",e);
            transactionLogger.error("SQLException while refunding transaction VIA Pay World",e);

            PZExceptionHandler.raiseAndHandleDBViolationException(PayWorldPaymentProcess.class.getName(),"inquiry",null,"common","Technical Exception", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"inquiry");
            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error during reversal of transaction " + e.getMessage());
        }
        catch (SystemError systemError)
        {
            log.error("PZDBViolationException while refunding transaction VIA Pay World",systemError);
            transactionLogger.error("PZDBViolationException while refunding transaction VIA Pay World",systemError);

            PZExceptionHandler.raiseAndHandleDBViolationException(PayWorldPaymentProcess.class.getName(),"inquiry",null,"common","Technical Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),null,"inquiry");
            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error during reversal of transaction " + systemError.getMessage());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return inquiryResponse;
    }
}
