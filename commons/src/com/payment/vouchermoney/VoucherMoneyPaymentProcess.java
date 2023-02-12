package com.payment.vouchermoney;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.manager.PaymentManager;
import com.manager.dao.MerchantDAO;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.MerchantDetailsVO;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.PZPayoutRequest;
import com.payment.response.PZPayoutResponse;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Uday on 9/20/17.
 */
public class VoucherMoneyPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger= new TransactionLogger(VoucherMoneyPaymentProcess.class.getName());
    @Override
    public CommCardDetailsVO getCustomerAccountDetails(String previousTransTrackingId) throws PZDBViolationException
    {
        transactionLogger.error(":::::enter into voucherMoneyPaymentProcess:::::");
        CommCardDetailsVO commCardDetailsVO=null;
        Connection conn=null;
        PreparedStatement pstmt=null;
        try{
            conn= Database.getConnection();
            String query="SELECT customerid,customerEmail,customerBankId FROM `transaction_vouchermoney_details` WHERE trackingid=?";
            transactionLogger.debug("query:::::"+query);
            pstmt=conn.prepareStatement(query);
            pstmt.setString(1,previousTransTrackingId);
            ResultSet rs=pstmt.executeQuery();
            if(rs.next())
            {
                commCardDetailsVO= new CommCardDetailsVO() ;
                commCardDetailsVO.setAccountNumber(rs.getString("trackingid"));
            }
        }catch(SystemError error){
            transactionLogger.error("SystemError::::::",error);
        }
        catch(SQLException e){
            transactionLogger.error("SystemError::::::",e);
        } finally{
            Database.closeConnection(conn);
        }
        return  commCardDetailsVO;
    }

    @Override
    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO commRequestVO) throws PZDBViolationException
    {
        transactionLogger.debug("enter in vouchermoney actionentry extension");
        int i=0;

        String merchantUserComm = "";
        String commCurrency = "";
        String commToPay = "";
        String commToUserCurrency = "";

        Connection conn= null;
        PreparedStatement pstmt = null;
        PaymentManager paymentManager = new PaymentManager();
        VoucherMoneyResponse vmResponse=(VoucherMoneyResponse)responseVO;

        if(vmResponse!=null)
        {
            System.out.println("vm response---"+vmResponse.getMerchantUsersCommission());
            System.out.println("vm response deposit comm---"+vmResponse.getCommissionPaidCurrency());
            merchantUserComm = vmResponse.getMerchantUsersCommission();
            commCurrency = vmResponse.getCommissionCurrency();
            commToPay = vmResponse.getCommissionToPay();
            commToUserCurrency = vmResponse.getCommissionPaidCurrency();
        }

        if (!paymentManager.isTransactionExist(trackingId))
        {
            transactionLogger.error("inside insert-----trackingId--"+trackingId+"----AccountNumber"+commRequestVO.getCardDetailsVO().getAccountNumber());

            try
            {
                conn = Database.getConnection();
                String sql = "insert into transaction_vouchermoney_details(trackingid,customerid,merchantUsersCommission,merchantUserCommCurrency,commissionPaidToUser,commPaidToUserCurrency) values (?,?,?,?,?,?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, trackingId + "");
                pstmt.setString(2, commRequestVO.getCardDetailsVO().getAccountNumber() + "");
                pstmt.setString(3, merchantUserComm);
                pstmt.setString(4, commCurrency);
                pstmt.setString(5, commToPay);
                pstmt.setString(6, commToUserCurrency);

                i = pstmt.executeUpdate();

            }
            catch (SQLException e)
            {
                PZExceptionHandler.raiseDBViolationException(VoucherMoneyPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
            }
            catch (SystemError systemError)
            {
                PZExceptionHandler.raiseDBViolationException(VoucherMoneyPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
            }
            finally
            {
                Database.closePreparedStatement(pstmt);
                Database.closeConnection(conn);
            }
        }
        else
        {
            try
            {
                conn = Database.getConnection();
                String sql = "update transaction_vouchermoney_details set merchantUsersCommission=?,merchantUserCommCurrency=?,commissionPaidToUser=?,commPaidToUserCurrency=? where trackingid=?";
                pstmt = conn.prepareStatement(sql);

                pstmt.setString(1, merchantUserComm);
                pstmt.setString(2, commCurrency);
                pstmt.setString(3, commToPay);
                pstmt.setString(4, commToUserCurrency);
                pstmt.setString(5, trackingId + "");

                i = pstmt.executeUpdate();

            }
            catch (SQLException e)
            {
                PZExceptionHandler.raiseDBViolationException(VoucherMoneyPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
            }
            catch (SystemError systemError)
            {
                PZExceptionHandler.raiseDBViolationException(VoucherMoneyPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
            }
            finally
            {
                Database.closePreparedStatement(pstmt);
                Database.closeConnection(conn);
            }
        }
        return i;
    }

    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        //System.out.println("inside VM Payout Extention---");
        CommAddressDetailsVO commAddressDetailsVO=requestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO=requestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO=requestVO.getCardDetailsVO();
        int memberId=payoutRequest.getMemberId();

        MerchantDAO merchantDAO=new MerchantDAO();
        MerchantDetailsVO merchantDetailsVO=merchantDAO.getMemberDetails(String.valueOf(memberId));

        commTransactionDetailsVO.setCustomerBankId(payoutRequest.getCustomerBankId());
        commTransactionDetailsVO.setCustomerId(payoutRequest.getCustomerId());
        commAddressDetailsVO.setEmail(payoutRequest.getCustomerEmail());
        if(merchantDetailsVO!=null){
            commAddressDetailsVO.setBirthdate(merchantDetailsVO.getExpDateOffset());
            commAddressDetailsVO.setCountry(merchantDetailsVO.getCountry());
        }
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }
    public void setPayoutTransactionResponseVOParamsExtension(CommResponseVO responseVO,PZPayoutResponse payoutResponse) throws  PZDBViolationException
    {
        VoucherMoneyResponse response=(VoucherMoneyResponse)responseVO;
        if(response!=null)
        {
            payoutResponse.setVoucherNumber(response.getNewVoucherSerialNumber());
            payoutResponse.setTmpl_currency(response.getTmpl_currency());
            payoutResponse.setTmpl_amount(response.getTmpl_amount());
            payoutResponse.setMerchantUsersCommission(response.getMerchantUsersCommission());
            payoutResponse.setCommissionCurrency(response.getCommissionCurrency());
            payoutResponse.setCommissionToPay(response.getCommissionToPay());
        }

    }

    public CommonValidatorVO getExtentionDetails(CommonValidatorVO commonValidatorVO)
    {
        Connection connection = null;
        HashMap hashMap = new HashMap();
        Functions functions = new Functions();
        try
        {
            connection = Database.getConnection();
            String query = "SELECT customerid,customerEmail,customerBankId FROM transaction_vouchermoney_details WHERE trackingid=?";
            PreparedStatement p = connection.prepareStatement(query);
            p.setString(1, commonValidatorVO.getTrackingid());
            ResultSet rs = p.executeQuery();
            if(rs.next())
            {
                if(functions.isValueNull(rs.getString("customerid")))
                    commonValidatorVO.setCustomerId(rs.getString("customerid"));
                if(functions.isValueNull(rs.getString("customerEmail")))
                    commonValidatorVO.getAddressDetailsVO().setEmail(rs.getString("customerEmail"));
                if(functions.isValueNull(rs.getString("customerBankId")))
                    commonValidatorVO.setCustomerBankId(rs.getString("customerBankId"));
                if (functions.isValueNull(commonValidatorVO.getTrackingid()))
                    commonValidatorVO.setCustAccount(commonValidatorVO.getTrackingid());
            }
            transactionLogger.debug("detail table for VM---"+p);
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
            directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }


}
