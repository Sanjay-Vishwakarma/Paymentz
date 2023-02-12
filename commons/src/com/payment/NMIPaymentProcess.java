package com.payment;

import com.directi.pg.Database;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.directi.pg.core.paymentgateway.NMIPaymentGateway;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZConstraintViolationException;
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
 * User: Administrator
 * Date: 8/14/13
 * Time: 2:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class NMIPaymentProcess  extends CommonPaymentProcess
{
    private static Logger log = new Logger(NMIPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(NMIPaymentProcess.class.getName());

    public PZInquiryResponse inquiry(PZInquiryRequest pzInquiryRequest)
    {
        log.debug("Inside NMI Inquiry");
        transactionLogger.debug("Inside NMI Inquiry");
        PZInquiryResponse inquiryResponse = new PZInquiryResponse();
        Connection conn=null;
        try
        {
            Integer trackingId = pzInquiryRequest.getTrackingId();
            Integer accountId = pzInquiryRequest.getAccountId();
            conn = Database.getConnection();

            CommRequestVO NMIRequestVO=new CommRequestVO();
            CommTransactionDetailsVO commTransactionDetailsVO=new CommTransactionDetailsVO();
            CommResponseVO NMIResponseVO=null;

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
                NMIRequestVO.setTransDetailsVO(commTransactionDetailsVO);
                NMIPaymentGateway pg=new NMIPaymentGateway(accountIdDB);
                NMIResponseVO=(CommResponseVO)pg.processQuery(trackingIdDB,NMIRequestVO);

                if(NMIResponseVO!=null)
                {
                    inquiryResponse.setStatus(PZResponseStatus.SUCCESS);
                    inquiryResponse.setTrackingId(trackingIdDB);
                    inquiryResponse.setResponseTransactionId(NMIResponseVO.getTransactionId());
                    inquiryResponse.setResponseTransactionStatus(NMIResponseVO.getStatus());
                    inquiryResponse.setResponseCode(NMIResponseVO.getErrorCode());
                    inquiryResponse.setResponseDescription(NMIResponseVO.getDescription());
                    inquiryResponse.setResponseName1("ipAddress");
                    inquiryResponse.setResponseValue1(NMIResponseVO.getIpaddress());
                    inquiryResponse.setResponseName2("Amount");
                    inquiryResponse.setResponseValue2(NMIResponseVO.getAmount());
                    inquiryResponse.setResponseName3("Transaction_Type");
                    inquiryResponse.setResponseValue3(NMIResponseVO.getTransactionType());
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
        }
        catch (PZTechnicalViolationException e)
        {
            log.error("PzTechnical Exception while refunding transaction VIA NMI",e);
            transactionLogger.error("PzTechnical Exception while refunding transaction VIA NMI",e);

            PZExceptionHandler.handleTechicalCVEException(e, null, "inquiry");
            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error during reversal of transaction " + e.getMessage());
        }
        catch (PZConstraintViolationException e)
        {
            log.error("PZConstraintViolationException while refunding transaction VIA NMI",e);
            transactionLogger.error("PZConstraintViolationException while refunding transaction VIA NMI",e);

            PZExceptionHandler.handleCVEException(e, null, "inquiry");
            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error during reversal of transaction " + e.getMessage());
        }
        catch (PZDBViolationException e)
        {
            log.error("PZConstraintViolationException while refunding transaction VIA NMI",e);
            transactionLogger.error("PZConstraintViolationException while refunding transaction VIA NMI",e);

            PZExceptionHandler.handleDBCVEException(e, null, "inquiry");
            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error during reversal of transaction " + e.getMessage());
        }
        catch (SQLException e)
        {
            log.error("SQLException while refunding transaction VIA NMI",e);
            transactionLogger.error("SQLException while refunding transaction VIA NMI",e);

            PZExceptionHandler.raiseAndHandleDBViolationException(NMIPaymentProcess.class.getName(),"inquiry",null,"common","Technical Exception", PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause(),null,"inquiry");
            inquiryResponse.setStatus(PZResponseStatus.ERROR);
            inquiryResponse.setResponseDesceiption("Error during reversal of transaction " + e.getMessage());
        }
        catch (SystemError systemError)
        {
            log.error("PZDBViolationException while refunding transaction VIA NMI",systemError);
            transactionLogger.error("PZDBViolationException while refunding transaction VIA NMI",systemError);

            PZExceptionHandler.raiseAndHandleDBViolationException(NMIPaymentProcess.class.getName(),"inquiry",null,"common","Technical Exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause(),null,"inquiry");
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

