package com.payment.paySafeCard;

import com.manager.TransactionManager;
import com.manager.vo.DirectKitResponseVO;
import com.manager.vo.TransactionDetailsVO;
import com.payment.common.core.CommAddressDetailsVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommonPaymentProcess;
import com.payment.request.PZRefundRequest;
import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 12/18/14
 * Time: 7:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class PaySafeCardPaymentProcess extends CommonPaymentProcess
{
    @Override
    public void setRefundVOParamsextension(CommRequestVO requestVO, PZRefundRequest refundRequest)
    {
        String trackingId= String.valueOf(refundRequest.getTrackingId());
        TransactionDetailsVO transactionDetailsVO=null;
        TransactionManager transactionManager=new TransactionManager();
        transactionDetailsVO = transactionManager.getTransDetailFromCommon(trackingId);
        CommAddressDetailsVO commAddressDetailsVO = requestVO.getAddressDetailsVO();
        commAddressDetailsVO.setEmail(transactionDetailsVO.getEmailaddr());
        requestVO.setAddressDetailsVO(commAddressDetailsVO);
    }

    public DirectKitResponseVO setNBResponseVO(DirectKitResponseVO directKitResponseVO, CommonValidatorVO commonValidatorVO)
    {
        directKitResponseVO.setBankRedirectionUrl(directKitResponseVO.getBankRedirectionUrl());
        return directKitResponseVO;
    }
}
