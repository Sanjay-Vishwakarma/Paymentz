package com.payment.neteller.response;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Admin on 9/15/17.
 */
public class NetellerPaymentProcess extends CommonPaymentProcess
{
    TransactionLogger transactionLogger= new TransactionLogger(NetellerPaymentProcess.class.getName());
    public CommCardDetailsVO getCustomerAccountDetails(String previousTransTrackingId)throws PZDBViolationException
    {
        CommCardDetailsVO commCardDetailsVO=null;
        Connection conn = null;
        PreparedStatement stmt = null;
        try
        {
            conn = Database.getConnection();
            String query = "select emailaddr from transaction_common where trackingid=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,previousTransTrackingId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                commCardDetailsVO=new CommCardDetailsVO();
                commCardDetailsVO.setAccountNumber(rs.getString("emailaddr"));
            }
        }
        catch (SystemError se){
            transactionLogger.error("SystemError::::::",se);
        }
        catch (SQLException e){
            transactionLogger.error("SQLException::::::",e);
        }
        finally{
            Database.closeConnection(conn);
        }
        return commCardDetailsVO;
    }

    public CommonValidatorVO getExtentionDetails(CommonValidatorVO commonValidatorVO)
    {
        Connection connection = null;
        HashMap hashMap = new HashMap();
        Functions functions = new Functions();
        try
        {
            connection = Database.getConnection();
            String query = "SELECT customerId,customerEmail,customerBankId FROM transaction_neteller_details WHERE trackingid=?";
            PreparedStatement p = connection.prepareStatement(query);
            p.setString(1, commonValidatorVO.getTrackingid());
            ResultSet rs = p.executeQuery();
            if(rs.next())
            {
                if(functions.isValueNull(rs.getString("customerId")))
                    commonValidatorVO.setCustomerId(rs.getString("customerId"));
                if(functions.isValueNull(rs.getString("customerEmail")))
                    commonValidatorVO.getAddressDetailsVO().setEmail(rs.getString("customerEmail"));
                if(functions.isValueNull(rs.getString("customerBankId")))
                    commonValidatorVO.setCustomerBankId(rs.getString("customerBankId"));
            }
            transactionLogger.debug("detail table for neteller---"+p);
        }
        catch (SystemError systemError)
        {
            transactionLogger.error("System error",systemError);
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
        transactionLogger.debug("inside neteller===");
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }
}



