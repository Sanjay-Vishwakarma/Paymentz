package com.directi.pg.core.valueObjects;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 4, 2012
 * Time: 8:57:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class PayVTRequestVO extends GenericRequestVO
{

    private GenericCardDetailsVO cardDetails;


    private GenericAddressDetailsVO billingAddr;


    private GenericTransDetailsVO transDetails;



    public PayVTRequestVO(GenericCardDetailsVO cardDetails, GenericAddressDetailsVO billingAddr, GenericTransDetailsVO transDetails)
    {
        this.cardDetails = cardDetails;
        this.billingAddr = billingAddr;
        this.transDetails = transDetails;

    }

    public PayVTRequestVO(GenericTransDetailsVO transDetails)
    {
        this.transDetails = transDetails;
    }

    public PayVTRequestVO(GenericCardDetailsVO cardDetails)
    {
        this.cardDetails = cardDetails;
    }


    public PayVTRequestVO(GenericAddressDetailsVO billingAddr)
    {
        this.billingAddr = billingAddr;
    }


    
    public GenericTransDetailsVO getTransDetails()
    {
        return transDetails;
    }

    public void setTransDetails(GenericTransDetailsVO transDetails)
    {
        this.transDetails = transDetails;
    }


    public GenericAddressDetailsVO getBillingAddr()
    {
        return billingAddr;
    }

    public void setBillingAddr(GenericAddressDetailsVO billingAddr)
    {
        this.billingAddr = billingAddr;
    }


    public GenericCardDetailsVO getCardDetails()
    {
        return cardDetails;
    }

    public void setCardDetails(GenericCardDetailsVO cardDetails)
    {
        this.cardDetails = cardDetails;
    }


}
