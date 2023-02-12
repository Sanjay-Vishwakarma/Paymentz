package com.payment.payvoucher.core;

import com.directi.pg.TransactionLogger;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.PZRefundRequest;
import com.directi.pg.Logger;
import com.directi.pg.SystemError;
import com.directi.pg.Database;
import com.directi.pg.core.valueObjects.PayLineVoucherResponseVO;
import com.directi.pg.core.valueObjects.SwiffpayRequestVO;
import com.directi.pg.core.valueObjects.PayLineVoucherRequestVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Jul 22, 2013
 * Time: 1:30:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayVoucherPaymentProcess  extends CommonPaymentProcess
{

    private static Logger log = new Logger(PayVoucherPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayVoucherPaymentProcess.class.getName());


    @Override
    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest)
    {
        PayLineVoucherRequestVO payLineVoucherRequestVO=(PayLineVoucherRequestVO) requestVO;
        payLineVoucherRequestVO.setReferenceId(payLineVoucherRequestVO.getTransDetailsVO().getPreviousTransactionId());

    }

    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO commResponseVO, CommRequestVO commRequestVO) throws PZDBViolationException
    {
            log.debug("enter in payvoucher actionentery extension");
            transactionLogger.debug("enter in payvoucher actionentery extension");
            int i=0;
            Connection conn= null;
            PayLineVoucherResponseVO responseVO= (PayLineVoucherResponseVO) commResponseVO;
            String channel = "";
            String shortid = "";
            String uniqueid = "";
            String paymentcode = "";
            String returncurrency = "";
            String processingcode = "";
            String timestampreturned = "";
            String result = "";
            String statuscode = "";
            String respstatus = "";
            String reasoncode = "";
            String reason = "";
            String returncode = "";
            String respreturn = "";
            String returnamount = "";
            if (responseVO != null)
            {
                channel = responseVO.getChannel();
                shortid = responseVO.getShortId();
                uniqueid = responseVO.getUniqueId();
                paymentcode = responseVO.getPaymentCode();
                returncurrency = responseVO.getCurrency();
                processingcode = responseVO.getProcessingCode();
                timestampreturned = responseVO.getTimeStamp();
                result = responseVO.getResult();
                statuscode = responseVO.getStatusCode();
                respstatus = responseVO.getStatus();
                reasoncode = responseVO.getErrorCode();
                reason = responseVO.getDescription();
                returncode = responseVO.getReturnCode();
                respreturn = responseVO.getReturnMessage();
                returnamount = responseVO.getClearingAmount();
            }

            try
            {
                conn=Database.getConnection();

                String sql = "insert into transaction_payvoucher_details(parentid,amount,action,status,channel,shortid,uniqueid,paymentcode,returncurrency,processingcode,timestampreturned,result,statuscode,respstatus,reasoncode,reason,returncode,returnmessage,returnamount) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, trackingId);
                pstmt.setString(2, amount);
                pstmt.setString(3, action);
                pstmt.setString(4, status);
                pstmt.setString(5, channel);
                pstmt.setString(6, shortid);
                pstmt.setString(7, uniqueid);
                pstmt.setString(8, paymentcode);
                pstmt.setString(9, returncurrency);
                pstmt.setString(10, processingcode);
                pstmt.setString(11, timestampreturned);
                pstmt.setString(12, result);
                pstmt.setString(13, statuscode);
                pstmt.setString(14, respstatus);
                pstmt.setString(15, reasoncode);
                pstmt.setString(16, reason);
                pstmt.setString(17, returncode);
                pstmt.setString(18, respreturn);
                pstmt.setString(19, returnamount);

                int results = pstmt.executeUpdate();


            }
            catch (SQLException e)
            {
                PZExceptionHandler.raiseDBViolationException(PayVoucherPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
            }
            catch (SystemError systemError)
            {
                PZExceptionHandler.raiseDBViolationException(PayVoucherPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
            }
            finally
            {
                Database.closeConnection(conn);
            }
            return i;
        }

}
