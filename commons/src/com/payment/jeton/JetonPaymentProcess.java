package com.payment.jeton;

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
 * Created by Uday on 8/17/17.
 */
public class JetonPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger= new TransactionLogger(JetonPaymentProcess.class.getName());
    public CommCardDetailsVO getCustomerAccountDetails(String previouTransTrackingId) throws PZDBViolationException
    {
        CommCardDetailsVO commCardDetailsVO=null;
        Connection conn=null;
        PreparedStatement stmt=null;
        try{
            conn= Database.getConnection();
            String query="select customer_number from transaction_jeton_details where trackingid=?";
            stmt= conn.prepareStatement(query);
            stmt.setString(1,previouTransTrackingId);
            ResultSet rs=stmt.executeQuery();
            if(rs.next()){
                commCardDetailsVO=new CommCardDetailsVO();
                commCardDetailsVO.setAccountNumber(rs.getString("customer_number"));
            }
            transactionLogger.debug("query:::::"+stmt);
            //System.out.println("query:::::"+stmt);

        }catch(SystemError se){
            transactionLogger.error("SystemError:::::"+se);
        }
        catch(SQLException e){
            transactionLogger.error("SQLException:::::"+e);
        }
        finally {
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
            String query = "select customer_number from transaction_jeton_details where trackingid=?";
            PreparedStatement p = connection.prepareStatement(query);
            p.setString(1, commonValidatorVO.getTrackingid());
            ResultSet rs = p.executeQuery();
            if(rs.next())
            {
                if(functions.isValueNull(rs.getString("customer_number")))
                    commonValidatorVO.setCustAccount(rs.getString("customer_number"));

            }
            transactionLogger.debug("detail table for jetonwallet---"+p);
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
        transactionLogger.debug("inside JetonPaymentProcess===");
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }
}
