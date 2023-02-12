package com.payment.bitcoinpayget;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.manager.vo.AsyncParameterVO;
import com.manager.vo.DirectKitResponseVO;
import com.payment.common.core.Comm3DResponseVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZPayoutRequest;
import com.payment.validators.vo.CommonValidatorVO;

import java.util.HashMap;

/**
 * Created by Jitendra on 02-Jul-19.
 */
public class BitcoinPaygatePaymentProcess  extends CommonPaymentProcess
{
    private static TransactionLogger transactionLogger = new TransactionLogger(BitcoinPaygatePaymentProcess.class.getName());

    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        transactionLogger.error("Inside into setPayoutVOParamsextension --- ");
        Functions functions = new Functions();
        CommTransactionDetailsVO commTransactionDetailsVO = requestVO.getTransDetailsVO();
        transactionLogger.debug("payoutRequest.getCustomerBitcoinAddress() ---------------"+payoutRequest.getCustomerBitcoinAddress());
        String trackingId = String.valueOf(payoutRequest.getTrackingId());
        String customerBitcoinAddress = "";
        if (functions.isValueNull(payoutRequest.getCustomerBitcoinAddress()))
            customerBitcoinAddress = payoutRequest.getCustomerBitcoinAddress();
        else
            customerBitcoinAddress = "";

        transactionLogger.debug("customerBitcoinAddress ---------------"+customerBitcoinAddress);
        HashMap hashMap = new HashMap();
        BitcoinPaygateUtils bitcoinPaygateUtils = new BitcoinPaygateUtils();
        hashMap = bitcoinPaygateUtils.getResponsehashinfo(trackingId);
        String responceHashInfoAddress = (String) hashMap.get("responsehashinfo");
        transactionLogger.error("responceHashInfoAddress ------------ " + responceHashInfoAddress);

        if (functions.isValueNull(customerBitcoinAddress))
        {
            commTransactionDetailsVO.setWalletId(customerBitcoinAddress);
        }// Customers bitcoin id
        else
        {
            commTransactionDetailsVO.setWalletId(responceHashInfoAddress); // or else bitcoin backoffice payout id
        }
        requestVO.setTransDetailsVO(commTransactionDetailsVO);
        transactionLogger.error("Wallet id for payout ----"+commTransactionDetailsVO.getWalletId());
    }

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        transactionLogger.debug("inside BitcoinPayment Process ---==="+directKitResponseVO.getBankRedirectionUrl());
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        transactionLogger.debug("inside getBankRedirectionUrl Process ---==="+directKitResponseVO.getBankRedirectionUrl());

        return directKitResponseVO;
    }
}