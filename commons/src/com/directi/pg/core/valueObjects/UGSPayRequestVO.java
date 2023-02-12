package com.directi.pg.core.valueObjects;

import com.payment.common.core.CommRequestVO;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Mar 5, 2013
 * Time: 8:33:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class UGSPayRequestVO extends CommRequestVO
{


    private Generic3DDetailsVO generic3DDetails;


    private int UGSTransId ;       //To be used for ReferralDetailedRequest or ReferralRequest

    private String amount;          //To be used for ReferralDetailedRequest or ReferralRequest

    public UGSPayRequestVO()
    {
    }

    public UGSPayRequestVO(Generic3DDetailsVO generic3DDetails, int UGSTransId, String amount)
    {
        this.generic3DDetails = generic3DDetails;
        this.UGSTransId = UGSTransId;
        this.amount = amount;
    }

    public Generic3DDetailsVO getGeneric3DDetails()
    {
        return generic3DDetails;
    }

    public void setGeneric3DDetails(Generic3DDetailsVO generic3DDetails)
    {
        this.generic3DDetails = generic3DDetails;
    }

    public int getUGSTransId()
    {
        return UGSTransId;
    }

    public void setUGSTransId(int UGSTransId)
    {
        this.UGSTransId = UGSTransId;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }


}
