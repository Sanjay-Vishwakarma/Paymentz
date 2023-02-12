package com.payment.skrill;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Created by admin on 8/8/2017.
 */
public class SkrillPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(SkrillPaymentProcess.class.getName());
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.skrill");
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
            transactionLogger.debug("trackingid query----"+stmt);
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
        Functions functions = new Functions();
        try
        {
            connection = Database.getConnection();
            String query = "SELECT customerId,customerEmail,customerBankId FROM transaction_skrill_details WHERE trackingid=?";
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
            transactionLogger.debug("detail table for skrill---"+p);
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
        transactionLogger.debug("inside vouchermoney===");
        String accountId = commonValidatorVO.getMerchantDetailsVO().getAccountId();
        GatewayAccount gatewayAccount = GatewayAccountService.getGatewayAccount(accountId);
        String merchantId = gatewayAccount.getMerchantId();
        AsyncParameterVO asyncParameterVO = null;

        String status_url = RB.getString("STATUSURL");
        String email = commonValidatorVO.getAddressDetailsVO().getEmail();
        String amount = commonValidatorVO.getTransDetailsVO().getAmount();
        String currency = commonValidatorVO.getTransDetailsVO().getCurrency();
        String trackingID = commonValidatorVO.getTrackingid();
        String return_url = RB.getString("RETURNURL");

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("merchant_id");
        asyncParameterVO.setValue(merchantId);
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("status_url");
        asyncParameterVO.setValue(status_url);
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("pay_from_email");
        asyncParameterVO.setValue(email);
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("amount");
        asyncParameterVO.setValue(amount);
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("currency");
        asyncParameterVO.setValue(currency);
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("transaction_id");
        asyncParameterVO.setValue(trackingID);
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("return_url");
        asyncParameterVO.setValue(return_url);
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        directKitResponseVO.setBankRedirectionUrl(RB.getString("SALEURL")+"?sid="+commonValidatorVO.getCustomerId());

        return directKitResponseVO;
    }


}
