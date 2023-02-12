package com.payment.trustly;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.morrisBarVOs.Data;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by uday on 11/27/17.
 */
public class TrustlyPaymentProcess extends CommonPaymentProcess
{
    TransactionLogger transactionLogger= new TransactionLogger(TrustlyPaymentProcess.class.getName());

    @Override
    public CommCardDetailsVO getCustomerAccountDetails(String previousTransTrackingId) throws PZDBViolationException
    {
        transactionLogger.error("-----inside trustly payment process-------");
        CommCardDetailsVO commCardDetailsVO=null;
        Connection conn=null;
        PreparedStatement ps=null;
        try{
            conn= Database.getConnection();
            String sqlQuery="Select customer_account_id from transaction_trustly_details where trackingid=?";
            ps=conn.prepareStatement(sqlQuery);
            ps.setString(1,previousTransTrackingId);
           ResultSet rs= ps.executeQuery();
            if(rs.next()){
                commCardDetailsVO=new CommCardDetailsVO();
                commCardDetailsVO.setAccountNumber(rs.getString("customer_account_id"));
            }

        }catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java", "insertTrustlyBankDetails()", null, "Common", "SystemError Thrown:::", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException("PaymentDAO.java","insertTrustlyBankDetails()",null,"Common","SQLException Thrown:::",PZDBExceptionEnum.SQL_EXCEPTION,null,e.getMessage(),e.getCause());
        }finally
        {
            Database.closeConnection(conn);
        }
        return commCardDetailsVO;
    }

    public CommonValidatorVO getExtentionDetails(CommonValidatorVO commonValidatorVO)
    {
        Connection connection = null;
        Functions functions = new Functions();
        try
        {
            connection = Database.getConnection();
            String query = "Select customer_account_id,customerId from transaction_trustly_details where trackingid=?";
            PreparedStatement p = connection.prepareStatement(query);
            p.setString(1, commonValidatorVO.getTrackingid());
            ResultSet rs = p.executeQuery();
            if(rs.next())
            {
                if(functions.isValueNull(rs.getString("customer_account_id")))
                    commonValidatorVO.setCustomerBankId(rs.getString("customer_account_id"));
                if(functions.isValueNull(rs.getString("customerId")))
                    commonValidatorVO.setCustomerId(rs.getString("customerId"));
            }
            transactionLogger.debug("detail table for trustly---"+p);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("System error", systemError);
        }
        catch (SQLException e)
        {
            transactionLogger.error("SQLException",e);
        }
        finally
        {
            Database.closeConnection(connection);
        }
        return commonValidatorVO;
    }

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.debug("inside TrustlyPaymentProcess===");
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }
}
