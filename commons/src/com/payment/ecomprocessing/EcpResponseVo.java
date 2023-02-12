package com.payment.ecomprocessing;

/**
 * Created by Balaji on 21-Nov-19.
 */
public class EcpResponseVo
{
    String merchantNumber;
    String issuerNumber;
    String originalBatchNumber;
    String description;
    String type;
    String arn;
    String postDate;
    String reasonCode;
    String reasonDescription;
    String authorizationCode;
    String batchNumber;
    String amount;
    String currency;
    String chargebackAmount;
    String chargebackCurrency;
    String originalTransactionAmount;
    String originalTransactionCurrency;
    String merchantSettlementAmount;
    String merchantSettlementCurrency;
    String networkSettlementAmount;
    String networkSettlementCurrency;
    String merchantDbaName;
    String originalType;
    String originalPostDate;
    String originalTransactionDate;
    String originalSlip;
    String itemSlipNumber;
    String cardNumber;
    String cardBrand;
    String customerEmail;
    String transactionType;
    String originalTransactionUniqueId;

//extra for card_not_present transaction
    String transactionDate;
    String cardScheme;
    String serviceTypeDesc;
    String binCountry;
    String merchantCountry;
    String areaOfEvent;
    String crossRate;
    String authCode;
    String cardPresent;
    String depositSlipNumber;
    String batchSlipNumber;

    public String getTransactionDate()
    {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate)
    {
        this.transactionDate = transactionDate;
    }

    public String getCardScheme()
    {
        return cardScheme;
    }

    public void setCardScheme(String cardScheme)
    {
        this.cardScheme = cardScheme;
    }

    public String getServiceTypeDesc()
    {
        return serviceTypeDesc;
    }

    public void setServiceTypeDesc(String serviceTypeDesc)
    {
        this.serviceTypeDesc = serviceTypeDesc;
    }

    public String getBinCountry()
    {
        return binCountry;
    }

    public void setBinCountry(String binCountry)
    {
        this.binCountry = binCountry;
    }

    public String getMerchantCountry()
    {
        return merchantCountry;
    }

    public void setMerchantCountry(String merchantCountry)
    {
        this.merchantCountry = merchantCountry;
    }

    public String getAreaOfEvent()
    {
        return areaOfEvent;
    }

    public void setAreaOfEvent(String areaOfEvent)
    {
        this.areaOfEvent = areaOfEvent;
    }

    public String getCrossRate()
    {
        return crossRate;
    }

    public void setCrossRate(String crossRate)
    {
        this.crossRate = crossRate;
    }

    public String getAuthCode()
    {
        return authCode;
    }

    public void setAuthCode(String authCode)
    {
        this.authCode = authCode;
    }

    public String getCardPresent()
    {
        return cardPresent;
    }

    public void setCardPresent(String cardPresent)
    {
        this.cardPresent = cardPresent;
    }

    public String getDepositSlipNumber()
    {
        return depositSlipNumber;
    }

    public void setDepositSlipNumber(String depositSlipNumber)
    {
        this.depositSlipNumber = depositSlipNumber;
    }

    public String getBatchSlipNumber()
    {
        return batchSlipNumber;
    }

    public void setBatchSlipNumber(String batchSlipNumber)
    {
        this.batchSlipNumber = batchSlipNumber;
    }

    public String getMerchantNumber(){ return merchantNumber;}

    public void setMerchantNumber(String merchantNumber)
    {
        this.merchantNumber = merchantNumber;
    }

    public String getIssuerNumber()
    {
        return issuerNumber;
    }

    public void setIssuerNumber(String issuerNumber)
    {
        this.issuerNumber = issuerNumber;
    }

    public String getOriginalBatchNumber()
    {
        return originalBatchNumber;
    }

    public void setOriginalBatchNumber(String originalBatchNumber)
    {
        this.originalBatchNumber = originalBatchNumber;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getArn()
    {
        return arn;
    }

    public void setArn(String arn)
    {
        this.arn = arn;
    }

    public String getPostDate()
    {
        return postDate;
    }

    public void setPostDate(String postDate)
    {
        this.postDate = postDate;
    }

    public String getReasonCode()
    {
        return reasonCode;
    }

    public void setReasonCode(String reasonCode)
    {
        this.reasonCode = reasonCode;
    }

    public String getReasonDescription()
    {
        return reasonDescription;
    }

    public void setReasonDescription(String reasonDescription)
    {
        this.reasonDescription = reasonDescription;
    }

    public String getAuthorizationCode()
    {
        return authorizationCode;
    }

    public void setAuthorizationCode(String authorizationCode)
    {
        this.authorizationCode = authorizationCode;
    }

    public String getBatchNumber()
    {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber)
    {
        this.batchNumber = batchNumber;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getChargebackAmount()
    {
        return chargebackAmount;
    }

    public void setChargebackAmount(String chargebackAmount)
    {
        this.chargebackAmount = chargebackAmount;
    }

    public String getChargebackCurrency()
    {
        return chargebackCurrency;
    }

    public void setChargebackCurrency(String chargebackCurrency)
    {
        this.chargebackCurrency = chargebackCurrency;
    }

    public String getOriginalTransactionAmount()
    {
        return originalTransactionAmount;
    }

    public void setOriginalTransactionAmount(String originalTransactionAmount)
    {
        this.originalTransactionAmount = originalTransactionAmount;
    }

    public String getOriginalTransactionCurrency()
    {
        return originalTransactionCurrency;
    }

    public void setOriginalTransactionCurrency(String originalTransactionCurrency)
    {
        this.originalTransactionCurrency = originalTransactionCurrency;
    }

    public String getMerchantSettlementAmount()
    {
        return merchantSettlementAmount;
    }

    public void setMerchantSettlementAmount(String merchantSettlementAmount)
    {
        this.merchantSettlementAmount = merchantSettlementAmount;
    }

    public String getMerchantSettlementCurrency()
    {
        return merchantSettlementCurrency;
    }

    public void setMerchantSettlementCurrency(String merchantSettlementCurrency)
    {
        this.merchantSettlementCurrency = merchantSettlementCurrency;
    }

    public String getNetworkSettlementAmount()
    {
        return networkSettlementAmount;
    }

    public void setNetworkSettlementAmount(String networkSettlementAmount)
    {
        this.networkSettlementAmount = networkSettlementAmount;
    }

    public String getNetworkSettlementCurrency()
    {
        return networkSettlementCurrency;
    }

    public void setNetworkSettlementCurrency(String networkSettlementCurrency)
    {
        this.networkSettlementCurrency = networkSettlementCurrency;
    }

    public String getMerchantDbaName()
    {
        return merchantDbaName;
    }

    public void setMerchantDbaName(String merchantDbaName)
    {
        this.merchantDbaName = merchantDbaName;
    }

    public String getOriginalType()
    {
        return originalType;
    }

    public void setOriginalType(String originalType)
    {
        this.originalType = originalType;
    }

    public String getOriginalPostDate()
    {
        return originalPostDate;
    }

    public void setOriginalPostDate(String originalPostDate)
    {
        this.originalPostDate = originalPostDate;
    }

    public String getOriginalTransactionDate()
    {
        return originalTransactionDate;
    }

    public void setOriginalTransactionDate(String originalTransactionDate)
    {
        this.originalTransactionDate = originalTransactionDate;
    }

    public String getOriginalSlip()
    {
        return originalSlip;
    }

    public void setOriginalSlip(String originalSlip)
    {
        this.originalSlip = originalSlip;
    }

    public String getItemSlipNumber()
    {
        return itemSlipNumber;
    }

    public void setItemSlipNumber(String itemSlipNumber)
    {
        this.itemSlipNumber = itemSlipNumber;
    }

    public String getCardNumber()
    {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber)
    {
        this.cardNumber = cardNumber;
    }

    public String getCardBrand()
    {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand)
    {
        this.cardBrand = cardBrand;
    }

    public String getCustomerEmail()
    {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail)
    {
        this.customerEmail = customerEmail;
    }

    public String getTransactionType()
    {
        return transactionType;
    }

    public void setTransactionType(String transactionType)
    {
        this.transactionType = transactionType;
    }

    public String getOriginalTransactionUniqueId()
    {
        return originalTransactionUniqueId;
    }

    public void setOriginalTransactionUniqueId(String originalTransactionUniqueId)
    {
        this.originalTransactionUniqueId = originalTransactionUniqueId;
    }
}

