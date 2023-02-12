package com.directi.pg.core.valueObjects;

/**
 * Created by IntelliJ IDEA.
 * User: Acer
 * Date: Sep 4, 2012
 * Time: 8:57:05 AM
 * To change this template use File | Settings | File Templates.
 */
public class QwipiRequestVO extends GenericRequestVO
{

    private GenericCardDetailsVO cardDetails;


    private QwipiAddressDetailsVO billingAddr;


    private QwipiTransDetailsVO transDetails;

    public String getMd5Key()
    {
        return md5Key;
    }

    public void setMd5Key(String md5Key)
    {
        this.md5Key = md5Key;
    }

    private String md5Key;

    public String getKsnUrlFlag()
    {
        return isKsnUrlFlag;
    }

    public void setKsnUrlFlag(String ksnUrlFlag)
    {
        isKsnUrlFlag = ksnUrlFlag;
    }

    public String isKsnUrlFlag;


    public String getMiddleName()
    {
        return middleName;
    }

    public void setMiddleName(String middleName)
    {
        this.middleName = middleName;
    }

    public String middleName;

    public QwipiRequestVO(GenericCardDetailsVO cardDetails, QwipiAddressDetailsVO billingAddr, QwipiTransDetailsVO transDetails)
    {
        this.cardDetails = cardDetails;
        this.billingAddr = billingAddr;
        this.transDetails = transDetails;

    }

    public QwipiRequestVO(QwipiTransDetailsVO transDetails)
    {
        this.transDetails = transDetails;
    }

    public QwipiRequestVO(GenericCardDetailsVO cardDetails)
    {
        this.cardDetails = cardDetails;
    }


    public QwipiRequestVO(QwipiAddressDetailsVO billingAddr)
    {
        this.billingAddr = billingAddr;
    }


    public QwipiTransDetailsVO getTransDetails()
    {
        return transDetails;
    }

    public void setTransDetails(QwipiTransDetailsVO transDetails)
    {
        this.transDetails = transDetails;
    }


    public QwipiAddressDetailsVO getBillingAddr()
    {
        return billingAddr;
    }

    public void setBillingAddr(QwipiAddressDetailsVO billingAddr)
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
