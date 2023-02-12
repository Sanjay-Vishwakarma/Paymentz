package com.payment.elegro;

import com.directi.pg.ElegroLogger;
import com.directi.pg.TransactionLogger;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZPayoutRequest;

/**
 * Created by Admin on 1/24/2019.
 */
public class ElegroPaymentProcess extends CommonPaymentProcess
{
    //private  static ElegroLogger transactionLogger= new ElegroLogger(ElegroPaymentProcess.class.getName());
    private  static TransactionLogger transactionLogger= new TransactionLogger(ElegroPaymentProcess.class.getName());


    @Override
    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        CommTransactionDetailsVO commTransactionDetailsVO= requestVO.getTransDetailsVO();
        commTransactionDetailsVO.setCustomerId(payoutRequest.getOrderDescription());
        transactionLogger.debug("customertid-----"+commTransactionDetailsVO.getCustomerId());
        requestVO.setTransDetailsVO(commTransactionDetailsVO);

    }
}
