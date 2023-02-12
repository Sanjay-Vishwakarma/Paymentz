package com.manager.vo;

import com.payment.validators.vo.CommonValidatorVO;

/**
 * Created by 123 on 10/29/2015.
 */
public class SplitPaymentVO extends CommonValidatorVO
{
    private String splitPaymentDetail;


    public String getSplitPaymentDetail()
    {
        return splitPaymentDetail;
    }

    public void setSplitPaymentDetail(String splitPaymentDetail)
    {
        this.splitPaymentDetail = splitPaymentDetail;
    }


}
