package com.payment.mtn;

import com.directi.pg.Functions;
import com.directi.pg.TransactionLogger;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZPayoutRequest;
import com.payment.request.PZRefundRequest;

/**
 * Created by admin on 12-Mar-22.
 */
public class MTNUgandaPaymentProcess extends CommonPaymentProcess
{
    TransactionLogger transactionLogger= new TransactionLogger(MTNUgandaPaymentProcess.class.getName());
    private Functions functions = new Functions();

    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        transactionLogger.error("inside payout vo of MTNUgandaPaymentProcess -- >");

        if(functions.isValueNull(payoutRequest.getPhoneCountryCode()))
        {
            transactionLogger.error("req phone no cc ===== " + payoutRequest.getPhoneCountryCode());
            requestVO.getAddressDetailsVO().setTelnocc(payoutRequest.getPhoneCountryCode());
        }
        if(functions.isValueNull(payoutRequest.getPhone()))
        {
            transactionLogger.error("req phone no ===== " + payoutRequest.getPhone());
            requestVO.getAddressDetailsVO().setPhone(payoutRequest.getPhone());
        }
    }

    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest) throws PZDBViolationException
    {
        int trackingid=refundRequest.getTrackingId();
        TransactionManager transactionManager= new TransactionManager();
        TransactionDetailsVO transactionVO =transactionManager.getTransDetailFromCommon(String.valueOf(trackingid));
//        transactionLogger.error("podbatch ----- " + transactionVO.getPodBatch());
        requestVO.getTransDetailsVO().setSessionId(transactionVO.getPodBatch());
        requestVO.getAddressDetailsVO().setTelnocc(transactionVO.getTelcc());
        requestVO.getAddressDetailsVO().setPhone(transactionVO.getTelno());
    }
}
