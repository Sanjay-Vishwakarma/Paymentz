package com.payment.perfectmoney;

import com.directi.pg.*;
import com.directi.pg.core.GatewayAccount;
import com.directi.pg.core.GatewayAccountService;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZPayoutRequest;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Created by admin on 7/19/2017.
 */
public class PerfectMoneyPaymentProcess extends CommonPaymentProcess
{
    final static ResourceBundle RB = LoadProperties.getProperty("com.directi.pg.perfectmoney");
    //private static PerfectMoneyLogger transactionLogger = new PerfectMoneyLogger(PerfectMoneyPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PerfectMoneyPaymentProcess.class.getName());

    public CommCardDetailsVO getCustomerAccountDetails(String previousTransTrackingId)throws PZDBViolationException
    {
        transactionLogger.error("------inside getCustomerAccountDetails------");
        CommCardDetailsVO commCardDetailsVO=null;
        Connection conn = null;
        PreparedStatement stmt = null;
        try
        {
            conn = Database.getConnection();
            String query = "select payee_account from transaction_perfectmoney_details where trackingid=?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1,previousTransTrackingId);
            ResultSet rs = stmt.executeQuery();
            if(rs.next()){
                commCardDetailsVO=new CommCardDetailsVO();
                commCardDetailsVO.setAccountNumber(rs.getString("payee_account"));
            }

            transactionLogger.debug("query----"+stmt);
            //System.out.println("query----"+stmt);
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

    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        //System.out.println("inside PM Payout Extention---");
        CommAddressDetailsVO commAddressDetailsVO=requestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=requestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=requestVO.getCardDetailsVO();

        commTransactionDetailsVO.setCustomerBankId(payoutRequest.getCustomerBankId());
        commTransactionDetailsVO.setCustomerId(payoutRequest.getCustomerId());
        commAddressDetailsVO.setEmail(payoutRequest.getCustomerEmail());

        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }

    public CommonValidatorVO getExtentionDetails(CommonValidatorVO commonValidatorVO)
    {
        Connection connection = null;
        Functions functions = new Functions();
        try
        {
            connection = Database.getConnection();
            String query = "SELECT customerEmail,payer_account FROM transaction_perfectmoney_details WHERE trackingid=?";
            PreparedStatement p = connection.prepareStatement(query);
            p.setString(1, commonValidatorVO.getTrackingid());
            ResultSet rs = p.executeQuery();
            if(rs.next())
            {
                /*if(functions.isValueNull(rs.getString("customerId")))
                    commonValidatorVO.setCustomerId(rs.getString("customerId"));*/
                if(functions.isValueNull(rs.getString("customerEmail")))
                    commonValidatorVO.getAddressDetailsVO().setEmail(rs.getString("customerEmail"));
                if(functions.isValueNull(rs.getString("payer_account")))
                    commonValidatorVO.setCustomerBankId(rs.getString("payer_account"));
            }
            transactionLogger.debug("detail table for PM---"+p);
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
        AsyncParameterVO asyncParameterVO = null;
        PerfectMoneyUtils perfectMoneyUtils = new PerfectMoneyUtils();

        String status_url = RB.getString("STATUSURL");
        String no_payment_url = RB.getString("NOPAYMENTURL");
        String payment_url = RB.getString("PAYMENTURL");
        String sale_url = RB.getString("SALEURL");
        String amount = commonValidatorVO.getTransDetailsVO().getAmount();
        String payee_account = gatewayAccount.getMerchantId();
        String payee_name = gatewayAccount.getDisplayName();
        String payment_id = commonValidatorVO.getTrackingid();
        String payment_type = perfectMoneyUtils.getForcePaymentMethod(commonValidatorVO.getPaymentType());
        String payment_unit = gatewayAccount.getCurrency();

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("PAYEE_ACCOUNT");
        asyncParameterVO.setValue(payee_account);
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("STATUS_URL");
        asyncParameterVO.setValue(status_url);
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("PAYEE_NAME");
        asyncParameterVO.setValue(payee_name);
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("PAYMENT_AMOUNT");
        asyncParameterVO.setValue(amount);
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("PAYMENT_ID");
        asyncParameterVO.setValue(payment_id);
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("PAYMENT_UNITS");
        asyncParameterVO.setValue(payment_unit);
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("FORCED_PAYMENT_METHOD");
        asyncParameterVO.setValue(payment_type);
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("PAYMENT_URL");
        asyncParameterVO.setValue(payment_url);
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        asyncParameterVO = new AsyncParameterVO();
        asyncParameterVO.setName("NOPAYMENT_URL");
        asyncParameterVO.setValue(no_payment_url);
        directKitResponseVO.addListOfAsyncParameters(asyncParameterVO);

        directKitResponseVO.setBankRedirectionUrl(sale_url);

        return directKitResponseVO;
    }

}
