package com.payment.wirecard.core;

import com.directi.pg.PzEncryptor;
import com.directi.pg.TransactionLogger;
import com.manager.TransactionManager;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.exceptionHandler.PZDBViolationException;
import com.payment.request.PZPayoutRequest;

/**
 * Created by IntelliJ IDEA.
 * User: mukesh.a
 * Date: 7/27/13
 * Time: 9:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class WireCardPaymentProcess extends CommonPaymentProcess
{
    TransactionLogger transactionLogger = new TransactionLogger(WireCardPaymentProcess.class.getName());

    @Override
    public void setPayoutVOParamsextension(CommRequestVO requestVO, PZPayoutRequest payoutRequest) throws PZDBViolationException
    {
        transactionLogger.error(":::::enter into setPayoutVOParamsextension:::::");
        TransactionManager transactionManager = new TransactionManager();
        CommAddressDetailsVO commAddressDetailsVO = new CommAddressDetailsVO();
        CommCardDetailsVO commCardDetailsVO = new CommCardDetailsVO();

        TransactionDetailsVO transVo = transactionManager.getTransDetailFromCommon(String.valueOf(payoutRequest.getTrackingId()));
        if (transVo != null)
        {
            commAddressDetailsVO.setFirstname(transVo.getFirstName());
            commAddressDetailsVO.setLastname(transVo.getLastName());
            commAddressDetailsVO.setCity(transVo.getCity());
            commAddressDetailsVO.setCountry(transVo.getCountry());
            commAddressDetailsVO.setEmail(transVo.getEmailaddr());
            commAddressDetailsVO.setPhone(transVo.getTelno());
            commAddressDetailsVO.setIp(transVo.getIpAddress());
            commAddressDetailsVO.setZipCode(transVo.getZip());
            commAddressDetailsVO.setState(transVo.getState());
            commAddressDetailsVO.setStreet(transVo.getStreet());
            commCardDetailsVO.setCardNum(PzEncryptor.decryptPAN(transVo.getCcnum()));
            String expdate = transVo.getExpdate();
            if (expdate != null)
            {
                String eDate = PzEncryptor.decryptExpiryDate(expdate);
                String expMonth = eDate.split("\\/")[0];
                String expYear = eDate.split("\\/")[1];
                commCardDetailsVO.setExpMonth(expMonth);
                commCardDetailsVO.setExpYear(expYear);
            }
            commCardDetailsVO.setCardType(transVo.getCardtype());

            requestVO.setCardDetailsVO(commCardDetailsVO);
            requestVO.setAddressDetailsVO(commAddressDetailsVO);

        }
    }

}
