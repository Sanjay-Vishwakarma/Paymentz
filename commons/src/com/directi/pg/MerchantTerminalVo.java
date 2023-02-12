package com.directi.pg;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: admin
 * Date: 7/22/14
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class MerchantTerminalVo implements Serializable
{
        public String getTerminalId()
        {
            return terminalId;
        }

        public void setTerminalId(String terminalId)
        {
            this.terminalId = terminalId;
        }

        public String getAccountId()
        {
            return accountId;
        }

        public void setAccountId(String accountId)
        {
            this.accountId = accountId;
        }

        public String getCardType()
        {
            return cardType;
        }

        public void setCardType(String cardType)
        {
            this.cardType = cardType;
        }

        public String getPaymodeId()
        {
            return paymodeId;
        }

        public void setPaymodeId(String paymodeId)
        {
            this.paymodeId = paymodeId;
        }

        private String terminalId;
        private String accountId;
    public String addressDetails;
       public String addressValidation;

    public String getIsManualRecurring()
    {
        return isManualRecurring;
    }

    public void setIsManualRecurring(String isManualRecurring)
    {
        this.isManualRecurring = isManualRecurring;
    }

    public String isManualRecurring;

    public String getAddressValidation()
    {
        return addressValidation;
    }

    public void setAddressValidation(String addressValidation)
    {
        this.addressValidation = addressValidation;
    }

    public String getAddressDetails()
    {
        return addressDetails;
    }

    public void setAddressDetails(String addressDetails)
    {
        this.addressDetails = addressDetails;
    }

    public String getFileName()
    {
        return fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    private String fileName;
    public String getCardTypeName()
    {
        return cardTypeName;
    }

    public void setCardTypeName(String cardTypeName)
    {
        this.cardTypeName = cardTypeName;
    }

    public String getPaymodeName()
    {
        return paymodeName;
    }

    public void setPaymodeName(String paymodeName)
    {
        this.paymodeName = paymodeName;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    private String cardTypeName;
    private String paymodeName;
    private String cardType;
    private String paymodeId;
    private String currency;
    private String isActive;

    public String getIsActive()
    {
        return isActive;
    }

    public void setIsActive(String isActive)
    {
        this.isActive = isActive;
    }

    public String getcCPaymentFileName()
    {
        return cCPaymentFileName;
    }

    public void setcCPaymentFileName(String cCPaymentFileName)
    {
        this.cCPaymentFileName = cCPaymentFileName;
    }

    private String cCPaymentFileName;
    private String isRecurring;

    public String getIsRecurring()
    {
        return isRecurring;
    }

    public void setIsRecurring(String isRecurring)
    {
        this.isRecurring = isRecurring;
    }
}
