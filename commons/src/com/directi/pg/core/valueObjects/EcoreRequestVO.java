package com.directi.pg.core.valueObjects;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 4, 2012
 * Time: 8:57:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class EcoreRequestVO extends GenericRequestVO
{

    private GenericCardDetailsVO cardDetails;


    private EcoreAddressDetailsVO billingAddr;


    private EcoreTransDetailsVO transDetails;




    public EcoreRequestVO(GenericCardDetailsVO cardDetails, EcoreAddressDetailsVO billingAddr, EcoreTransDetailsVO transDetails)
    {
        this.cardDetails = cardDetails;
        this.billingAddr = billingAddr;
        this.transDetails = transDetails;

    }

    public EcoreRequestVO(EcoreTransDetailsVO transDetails)
    {
        this.transDetails = transDetails;
    }

    public EcoreRequestVO(GenericCardDetailsVO cardDetails)
    {
        this.cardDetails = cardDetails;
    }


    public EcoreRequestVO(EcoreAddressDetailsVO billingAddr)
    {
        this.billingAddr = billingAddr;
    }


    public EcoreTransDetailsVO getTransDetails()
    {
        return transDetails;
    }

    public void setTransDetails(EcoreTransDetailsVO transDetails)
    {
        this.transDetails = transDetails;
    }


    public EcoreAddressDetailsVO getBillingAddr()
    {
        return billingAddr;
    }

    public void setBillingAddr(EcoreAddressDetailsVO billingAddr)
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
