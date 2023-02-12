package com.payment.cupUPI;

import com.directi.pg.*;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Jitendra.P on 06-Jul-19.
 */
public class UnionPayInternationalPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(UnionPayInternationalPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(UnionPayInternationalPaymentProcess.class.getName());

    //private static Logger log = new Logger(UnicreditPaymentProcess.class.getName());
    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        log.debug("inside UnionPayInternationalPaymentProcess===");
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        return directKitResponseVO;
    }


    /*public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO) throws PZDBViolationException
    {
        log.debug("---Entering ActionEntry for PayMitcoPaymentProcess Details---");
        transactionLogger.debug("---Entering ActionEntry for PayMitcoPaymentProcess Details---");
        Functions functions=new Functions();

        //PayMitcoResponseVO payMitcoResponseVO=null;

        Connection conn = null;
        int k=0;
        try
        {
            conn = Database.getConnection();
            String sql = "insert into transaction_cupupi_details(detailid,trackingid,amount,status) values (?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,newDetailId);
            pstmt.setString(2, trackingId);
            pstmt.setString(3, amount);
            pstmt.setString(4, status);
            transactionLogger.error("pstmt------------>"+pstmt);
            k = pstmt.executeUpdate();

        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(UnionPayInternationalPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(UnionPayInternationalPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return k;
    }*/
}
