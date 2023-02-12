package com.payment.payon.core;

import com.directi.pg.core.valueObjects.GenericAddressDetailsVO;
import com.directi.pg.core.valueObjects.GenericRequestVO;
import com.payment.common.core.CommCardDetailsVO;
import com.payment.common.core.CommMerchantVO;
import com.payment.common.core.CommRequestVO;
import com.payment.common.core.CommTransactionDetailsVO;
import com.payment.payon.core.message.*;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

/**
 * Created with IntelliJ IDEA.
 * User: Jignesh
 * Date: 10/13/12
 * Time: 8:43 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayOnRequestVO extends CommRequestVO
{
    private PayOnMerchantAccountVO payOnMerchantAccountVO;
    private PayOnTransactionDetailsVO payOnTransactionDetailsVO;
    private PayOnCardDetailsVO payOnCardDetailsVO;
    private PayOnVBVDetailsVO payOnVBVDetailsVO;
    private Identification identification;


    public PayOnRequestVO()
    {
        payOnMerchantAccountVO = new PayOnMerchantAccountVO();
        payOnTransactionDetailsVO = new PayOnTransactionDetailsVO();
        payOnCardDetailsVO = new PayOnCardDetailsVO();
        payOnVBVDetailsVO = new PayOnVBVDetailsVO();
    }

    @Override
    public void setCommMerchantVO(CommMerchantVO commMerchantVO)
    {
        payOnMerchantAccountVO.setAliasName(commMerchantVO.getAliasName());
        payOnMerchantAccountVO.setMerchantId(commMerchantVO.getMerchantId());
        payOnMerchantAccountVO.setDisplayName(commMerchantVO.getDisplayName());
        payOnMerchantAccountVO.setPassword(commMerchantVO.getPassword());
        payOnMerchantAccountVO.setMerchantKey(commMerchantVO.getMerchantKey());
        payOnMerchantAccountVO.setMerchantUsername(commMerchantVO.getMerchantUsername());

    }

    @Override
    public void setTransDetailsVO(CommTransactionDetailsVO transDetailsVO)
    {
        payOnTransactionDetailsVO.setCurrency(transDetailsVO.getCurrency());
        payOnTransactionDetailsVO.setAmount(transDetailsVO.getAmount());
        payOnTransactionDetailsVO.setDetailId(transDetailsVO.getDetailId());
        payOnTransactionDetailsVO.setOrderDesc(transDetailsVO.getOrderDesc());
        payOnTransactionDetailsVO.setOrderId(transDetailsVO.getOrderId());
        payOnTransactionDetailsVO.setPreviousTransactionId(transDetailsVO.getPreviousTransactionId());
        payOnTransactionDetailsVO.setPrevTransactionStatus(transDetailsVO.getPrevTransactionStatus());
    }

    @Override
    public void setCardDetailsVO(CommCardDetailsVO cardDetailsVO)
    {
        payOnCardDetailsVO.setCardHolderFirstName(cardDetailsVO.getCardHolderFirstName());
        payOnCardDetailsVO.setCardHolderSurname(cardDetailsVO.getCardHolderSurname());
        payOnCardDetailsVO.setCardHolderName(cardDetailsVO.getCardHolderName());
        payOnCardDetailsVO.setCardNum(cardDetailsVO.getCardNum());
        payOnCardDetailsVO.setCardType(cardDetailsVO.getCardType());
        payOnCardDetailsVO.setcVV(cardDetailsVO.getcVV());
        payOnCardDetailsVO.setExpMonth(cardDetailsVO.getExpMonth());
        payOnCardDetailsVO.setExpYear(cardDetailsVO.getExpYear());

    }

    public Identification getIdentification()
    {
        return identification;
    }

    public void setIdentification(Identification identification)
    {
        this.identification = identification;
    }

    public PayOnMerchantAccountVO getPayOnMerchantAccountVO()
    {
        return payOnMerchantAccountVO;
    }

    public void setPayOnMerchantAccountVO(PayOnMerchantAccountVO payOnMerchantAccountVO)
    {
        this.payOnMerchantAccountVO = payOnMerchantAccountVO;
    }

    public PayOnTransactionDetailsVO getPayOnTransactionDetailsVO()
    {
        return payOnTransactionDetailsVO;
    }

    public void setPayOnTransactionDetailsVO(PayOnTransactionDetailsVO payOnTransactionDetailsVO)
    {
        this.payOnTransactionDetailsVO = payOnTransactionDetailsVO;
    }

    public PayOnCardDetailsVO getPayOnCardDetailsVO()
    {
        return payOnCardDetailsVO;
    }

    public void setPayOnCardDetailsVO(PayOnCardDetailsVO payOnCardDetailsVO)
    {
        this.payOnCardDetailsVO = payOnCardDetailsVO;
    }

    public PayOnVBVDetailsVO getPayOnVBVDetailsVO()
    {
        return payOnVBVDetailsVO;
    }

    public void setPayOnVBVDetailsVO(PayOnVBVDetailsVO payOnVBVDetailsVO)
    {
        this.payOnVBVDetailsVO = payOnVBVDetailsVO;
    }


}
