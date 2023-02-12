package com.manager.vo.payoutVOs;

/**
 * Created by Admin on 8/13/2019.
 */
public class PayoutDetailsVO
{
    String settledId;
    String payoutDate;
    String payoutCurrency;
    String conversionRate;
    String payoutAmount;
    String BeneficiaryBankDetails;
    String RemitterBankDetails;
    String Remarks;
    String SwiftMessage;
    String swiftUpload;
    String payoutId;
    String paymentReceiptDate;
    String paymentReceiptConfirmation;
    String cycleid;

    public String getCycleid()
    {
        return cycleid;
    }

    public void setCycleid(String cycleid)
    {
        this.cycleid = cycleid;
    }

    public String getPaymentReceiptConfirmation()
    {
        return paymentReceiptConfirmation;
    }

    public void setPaymentReceiptConfirmation(String paymentReceiptConfirmation)
    {
        this.paymentReceiptConfirmation = paymentReceiptConfirmation;
    }


    public String getPaymentReceiptDate()
{
    return paymentReceiptDate;
}

    public void setPaymentReceiptDate(String paymentReceiptDate)
    {
        this.paymentReceiptDate = paymentReceiptDate;
    }



    public String getSwiftUpload()
    {
        return swiftUpload;
    }

    public void setSwiftUpload(String swiftUpload)
    {
        this.swiftUpload = swiftUpload;
    }

    public String getSettledId()
    {
        return settledId;
    }

    public void setSettledId(String settledId)
    {
        this.settledId = settledId;
    }

    public String getPayoutDate()
    {
        return payoutDate;
    }

    public void setPayoutDate(String payoutDate)
    {
        this.payoutDate = payoutDate;
    }

    public String getPayoutCurrency()
    {
        return payoutCurrency;
    }

    public void setPayoutCurrency(String payoutCurrency)
    {
        this.payoutCurrency = payoutCurrency;
    }

    public String getConversionRate()
    {
        return conversionRate;
    }

    public void setConversionRate(String conversionRate)
    {
        this.conversionRate = conversionRate;
    }

    public String getBeneficiaryBankDetails()
    {
        return BeneficiaryBankDetails;
    }

    public void setBeneficiaryBankDetails(String beneficiaryBankDetails)
    {
        BeneficiaryBankDetails = beneficiaryBankDetails;
    }

    public String getRemitterBankDetails()
    {
        return RemitterBankDetails;
    }

    public void setRemitterBankDetails(String remitterBankDetails)
    {
        RemitterBankDetails = remitterBankDetails;
    }

    public String getRemarks()
    {
        return Remarks;
    }

    public void setRemarks(String remarks)
    {
        Remarks = remarks;
    }

    public String getSwiftMessage()
    {
        return SwiftMessage;
    }

    public void setSwiftMessage(String swiftMessage)
    {
        SwiftMessage = swiftMessage;
    }

    public String getPayoutId()
    {
        return payoutId;
    }

    public void setPayoutId(String payoutId)
    {
        this.payoutId = payoutId;
    }

    public String getPayoutAmount()
    {
        return payoutAmount;
    }

    public void setPayoutAmount(String payoutAmount)
    {
        this.payoutAmount = payoutAmount;
    }
    /* public String toString()
    {
        return settledId+"\n"+payoutDate+"\n"+payoutCurrency+"\n"+conversionRate+"\n"+payoutAmount+"\n"+BeneficiaryBankDetails+"\n"+RemitterBankDetails+"\n"+Remarks+"\n"+SwiftMessage+"\n";
    }*/
}
