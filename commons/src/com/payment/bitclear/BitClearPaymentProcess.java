package com.payment.bitclear;

import com.directi.pg.Database;
import com.directi.pg.Functions;
import com.directi.pg.SystemError;
import com.directi.pg.TransactionLogger;
import com.manager.TransactionManager;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.bitcoinpayget.BitcoinPaygateUtils;
import com.payment.common.core.*;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.exceptionHandler.PZExceptionHandler;
import com.payment.exceptionHandler.constraintType.PZDBExceptionEnum;
import com.payment.request.PZPayoutRequest;
import com.payment.validators.vo.CommonValidatorVO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by Admin on 3/8/2021.
 */
public class BitClearPaymentProcess extends CommonPaymentProcess
{
    private static TransactionLogger transactionlogger = new TransactionLogger(BitClearPaymentProcess.class.getName());

    public String get3DConfirmationForm(CommonValidatorVO commonValidatorVO, Comm3DResponseVO response3D){
        transactionlogger.error("inside  BitClearPaymentProcess Form ---");

        String form = "<form name=\"creditcard_checkout\" method=\"GET\" action=\"" +response3D.getUrlFor3DRedirect()+ "\">" ;
         form+="</form><script language=\"javascript\">document.creditcard_checkout.submit();</script>";

        transactionlogger.error("BitClearPaymentProcess Form---"+form.toString());

        return form.toString();
    }

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionlogger.error("inside PayG Process --->"+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        transactionlogger.error("inside PayG getBankRedirectionUrl Process -->"+directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }


    public void set3DResponseVO(DirectKitResponseVO directKitResponseVO, Comm3DResponseVO response3D, CommonValidatorVO commonValidatorVO)
    {


        directKitResponseVO.setBankRedirectionUrl(response3D.getUrlFor3DRedirect());
    }

   /* public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        transactionlogger.error("inside payout vo params etension -- >");
        CommAddressDetailsVO commAddressDetailsVO           = requestVO.getAddressDetailsVO();
        CommTransactionDetailsVO commTransactionDetailsVO   = requestVO.getTransDetailsVO();
        CommCardDetailsVO commCardDetailsVO                 = requestVO.getCardDetailsVO();
        TransactionManager transactionManager               = new TransactionManager();
        TransactionDetailsVO transactionDetailsVO           = transactionManager.getTransDetailFromCommon(String.valueOf(payoutRequest.getTrackingId()));

        commAddressDetailsVO.setStreet(transactionDetailsVO.getStreet());
        commAddressDetailsVO.setZipCode(transactionDetailsVO.getZip());
        commAddressDetailsVO.setCity(transactionDetailsVO.getCity());
        commAddressDetailsVO.setState(transactionDetailsVO.getState());
        commAddressDetailsVO.setCountry(transactionDetailsVO.getCountry());
        commAddressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
        //commTransactionDetailsVO.setCurrency(transactionDetailsVO.getCurrency());
        commAddressDetailsVO.setPhone(transactionDetailsVO.getTelno());

        commTransactionDetailsVO.setCustomerBankAccountName(payoutRequest.getCustomerBankAccountName());
        commTransactionDetailsVO.setBankIfsc(payoutRequest.getBankIfsc());
        commTransactionDetailsVO.setBankAccountNo(payoutRequest.getBankAccountNo());
        commTransactionDetailsVO.setBankTransferType(payoutRequest.getBankTransferType());


        requestVO.setAddressDetailsVO(commAddressDetailsVO);
        //requestVO.setCardDetailsVO(commCardDetailsVO);
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
    }*/

    public int actionEntryExtension(int newDetailId, String trackingId, String amount, String action, String status, CommResponseVO responseVO, CommRequestVO commRequestVO) throws PZDBViolationException
    {
        transactionlogger.debug("Entering ActionEntry for PayGPaymentGateway");
        System.out.println("entering");

        int results     = 0;
        Connection cn   = null;

        try
        {
            String ifsc         ="";
            String bankaccount  ="";
            String fullname     ="";
            String bankRefNo    ="";
            String spkRefNo     ="";
            if(responseVO != null){
                ifsc        = responseVO.getIfsc();
                bankaccount = responseVO.getBankaccount();
                fullname    = responseVO.getFullname();
                bankRefNo   = responseVO.getBankRefNo();
                spkRefNo    = responseVO.getSpkRefNo();
            }
            if(commRequestVO != null){
                ifsc            = commRequestVO.getTransDetailsVO().getBankIfsc();
                bankaccount     = commRequestVO.getTransDetailsVO().getBankAccountNo();
                fullname        = commRequestVO.getTransDetailsVO().getCustomerBankAccountName();

            }

            cn          = Database.getConnection();
            String sql  = "insert into transaction_safexpay_details(trackingId,fullname,bankaccount,ifsc,amount,status,dtstamp) values (?,?,?,?,?,?,unix_timestamp(now()))";

            PreparedStatement pstmt = cn.prepareStatement(sql);
            pstmt.setString(1, trackingId);
            pstmt.setString(2, fullname);
            pstmt.setString(3, bankaccount);
            pstmt.setString(4, ifsc);
            pstmt.setString(5,amount);
            pstmt.setString(6,status);

            results = pstmt.executeUpdate();
            transactionlogger.error("SqlQuery PayG-----" + pstmt);

        }
        catch (SQLException se)
        {
            PZExceptionHandler.raiseDBViolationException(BitClearPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.SQL_EXCEPTION, null, se.getMessage(), se.getCause());
        }
        catch (SystemError systemError)
        {
            PZExceptionHandler.raiseDBViolationException(BitClearPaymentProcess.class.getName(), "actionEntryExtension()", null, "common", "Technical exception", PZDBExceptionEnum.DB_CONNECTION_ISSUE, null, systemError.getMessage(), systemError.getCause());
        }
        finally
        {
            Database.closeConnection(cn);
        }
        return results;
    }


    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        transactionlogger.error("Inside into setPayoutVOParamsextension ------> "+payoutRequest.getCustomerBitcoinAddress());
        Functions functions                                 = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO   = requestVO.getTransDetailsVO();

        String trackingId               = String.valueOf(payoutRequest.getTrackingId());
        String customerBitcoinAddress   = "";

        if (functions.isValueNull(payoutRequest.getCustomerBitcoinAddress())){
            customerBitcoinAddress = payoutRequest.getCustomerBitcoinAddress();
        }else if (functions.isValueNull(payoutRequest.getWalletId())){
            customerBitcoinAddress = payoutRequest.getWalletId();
        }
        else{
            customerBitcoinAddress = "";
        }

        HashMap hashMap                         = new HashMap();
        BitClearUtils bitClearUtils             = new BitClearUtils();
        hashMap                                 = bitClearUtils.getResponsehashinfo(trackingId);
        String responceHashInfoAddress          = (String) hashMap.get("responsehashinfo");

        if (functions.isValueNull(customerBitcoinAddress))
        {
            transactionlogger.error("customerBitcoinAddress ---> "+trackingId+" "+customerBitcoinAddress);
            commTransactionDetailsVO.setWalletId(customerBitcoinAddress);
        }// Customers bitcoin id
        else
        {
            transactionlogger.error("responceHashInfoAddress ---> "+trackingId+" "+responceHashInfoAddress);
            commTransactionDetailsVO.setWalletId(responceHashInfoAddress); // or else bitcoin backoffice payout id
        }
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
        transactionlogger.error("Wallet id for payout ----"+commTransactionDetailsVO.getWalletId());
    }

}
