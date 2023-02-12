package com.payment.PayMitco.core;

import com.directi.pg.*;
import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericTransDetailsVO;
import com.manager.vo.ReserveField2VO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommResponseVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Nikita on 12/31/2015.
 */
public class PayMitcoPaymentProcess extends CommonPaymentProcess
{
    private static Logger log = new Logger(PayMitcoPaymentProcess.class.getName());
    private static TransactionLogger transactionLogger = new TransactionLogger(PayMitcoPaymentProcess.class.getName());

    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO requestVO) throws PZDBViolationException
    {
        log.debug("---Entering ActionEntry for PayMitcoPaymentProcess Details---");
        transactionLogger.debug("---Entering ActionEntry for PayMitcoPaymentProcess Details---");
        Functions functions=new Functions();

        PayMitcoResponseVO payMitcoResponseVO=null;
        String paymentType="";
        String transactionType="";
        String accountType="";
        String accountNumber="";
        String routingNumber="";
        String customerId="";
        String checknumber="";
        String bankName="";
        String bankAddress="";
        String bankCity="";
        String bankState="";
        String bankZipcode="";
        String transactionID="";

        if(responseVO != null  && !responseVO.equals(""))
        {
            transactionLogger.debug("---inside responseVO for actionEntry in paymitcoDetails---");
            payMitcoResponseVO = (PayMitcoResponseVO)responseVO;
            paymentType = payMitcoResponseVO.getPaymentType();
            transactionType = payMitcoResponseVO.getPaymitcoTransactionType();
            accountType = payMitcoResponseVO.getAccountType();
            accountNumber = payMitcoResponseVO.getAccountNumber();
            routingNumber = payMitcoResponseVO.getRoutingNumber();
            customerId = payMitcoResponseVO.getCustomerId();
            checknumber = payMitcoResponseVO.getChecknumber();
            bankName=payMitcoResponseVO.getBankName();
            bankAddress=payMitcoResponseVO.getBankAddress();
            bankCity=payMitcoResponseVO.getBankCity();
            bankState=payMitcoResponseVO.getBankState();
            bankZipcode=payMitcoResponseVO.getBankZipcode();
            transactionID=payMitcoResponseVO.getTransactionID();
            transactionLogger.debug("---transactionId---"+transactionID);
        }
        else if(requestVO !=null && !requestVO.equals(""))
        {
            transactionLogger.debug("---inside requestVO for actionEntry in paymitcoDetails---");
            GenericTransDetailsVO transDetailsVO = requestVO.getTransDetailsVO();
            GenericAddressDetailsVO addressDetailsVO = requestVO.getAddressDetailsVO();
            ReserveField2VO reserveField2VO = requestVO.getReserveField2VO();

            paymentType = transDetailsVO.getPaymentType();
            customerId = addressDetailsVO.getCustomerid();
            transactionType =transDetailsVO.getTransactionType();
            transactionLogger.debug("---1.transaction type"+transactionType);
            accountType = reserveField2VO.getAccountType();
            accountNumber = reserveField2VO.getAccountNumber();
            routingNumber = reserveField2VO.getRoutingNumber();
            bankName=reserveField2VO.getBankName();
            bankAddress=reserveField2VO.getBankAddress();
            bankCity=reserveField2VO.getBankCity();
            bankState=reserveField2VO.getBankState();
            bankZipcode=reserveField2VO.getBankZipcode();

        }

        Connection conn = null;
        int k=0;
        try
        {
            conn = Database.getConnection();
            String sql = "insert into transaction_paymitco_details(detailid,trackingid,amount,status,paymentType,transactionType,accountType,accountNumber,routingNumber,customerId,checknumber,bankName,bankAddress,bankCity,bankState,bankZipcode,transactionID) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,newDetailId);
            pstmt.setString(2, trackingId);
            pstmt.setString(3, amount);
            pstmt.setString(4, status);
            pstmt.setString(5,paymentType);
            pstmt.setString(6,transactionType);
            pstmt.setString(7,accountType);
            pstmt.setString(8,accountNumber);
            pstmt.setString(9,routingNumber);
            pstmt.setString(10,customerId);
            pstmt.setString(11,checknumber);
            pstmt.setString(12,bankName);
            pstmt.setString(13,bankAddress);
            pstmt.setString(14,bankCity);
            pstmt.setString(15,bankState);
            pstmt.setString(16,bankZipcode);
            pstmt.setString(17,transactionID);
            transactionLogger.error("pstmt------------>"+pstmt);
            k = pstmt.executeUpdate();

        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(PayMitcoPaymentProcess.class.getName(),"actionEntryExtension()",null,"common","Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE,null,systemError.getMessage(),systemError.getCause());
        }
        catch (SQLException e)
        {
            PZExceptionHandler.raiseDBViolationException(PayMitcoPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, e.getMessage(), e.getCause());
        }
        finally
        {
            Database.closeConnection(conn);
        }
        return k;
    }

}
