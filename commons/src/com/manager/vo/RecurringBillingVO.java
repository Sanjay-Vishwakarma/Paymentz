package com.manager.vo;

import com.payment.common.core.CommRequestVO;

/**
 * Created with IntelliJ IDEA.
 * User: Jinesh
 * Date: 3/7/15
 * Time: 6:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class RecurringBillingVO extends CommRequestVO
{
    private String isRecurring;
    private String interval;
    private String frequency;
    private String runDate;
    private String recurring_subscrition_id;
    private String bankRecurringBillingID;
    private String parentBankTransactionID;
    private String newBankTransactionID;
    private String parentPzTransactionID;
    private String newPzTransactionID;
    private String amount;
    private String description;
    private String transactionStatus;
    private String originTrackingId;
    private String originalBankTransactionId;
    private String cardHolderName;
    private String firstSix;
    private String lastFour;
    private String memberId;
    private String recurringStatus;
    private String rbid;
    private String recurringRunDate;
    private String activeDeactive;
    private String recurringRegisterDate;
    private String recurringType;
    private String isManualRecurring;
    private String isAutomaticRecurring;
    private String terminalid;
    private String IBAN;
    private String BIC;
    private String cardType;
    private String paymentType;
    private String accountNumber;
    private String routingNumber;
    private String accountType;
    private String reqField1;
    private String partnerId;

    public String getAccountNumber()
    {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber)
    {
        this.accountNumber = accountNumber;
    }

    public String getRoutingNumber()
    {
        return routingNumber;
    }

    public void setRoutingNumber(String routingNumber)
    {
        this.routingNumber = routingNumber;
    }

    public String getAccountType()
    {
        return accountType;
    }

    public void setAccountType(String accountType)
    {
        this.accountType = accountType;
    }

    public String getCardType()
    {
        return cardType;
    }

    public void setCardType(String cardType)
    {
        this.cardType = cardType;
    }

    public String getPaymentType()
    {
        return paymentType;
    }

    public void setPaymentType(String paymentType)
    {
        this.paymentType = paymentType;
    }

    public String getIBAN()
    {
        return IBAN;
    }

    public void setIBAN(String IBAN)
    {
        this.IBAN = IBAN;
    }

    public String getBIC()
    {
        return BIC;
    }

    public void setBIC(String BIC)
    {
        this.BIC = BIC;
    }

    public String getTerminalid()
    {
        return terminalid;
    }

    public void setTerminalid(String terminalid)
    {
        this.terminalid = terminalid;
    }

    public String getIsAutomaticRecurring()
    {
        return isAutomaticRecurring;
    }

    public void setIsAutomaticRecurring(String isAutomaticRecurring)
    {
        this.isAutomaticRecurring = isAutomaticRecurring;
    }

    public String getIsRecurring()
    {
        return isRecurring;
    }

    public void setIsRecurring(String isRecurring)
    {
        this.isRecurring = isRecurring;
    }

    public String getIsManualRecurring()
    {
        return isManualRecurring;
    }

    public void setIsManualRecurring(String isManualRecurring)
    {
        this.isManualRecurring = isManualRecurring;
    }

    public String getRecurringType()
    {
        return recurringType;
    }

    public void setRecurringType(String recurringType)
    {
        this.recurringType = recurringType;
    }

    public String getReqField1()
    {
        return reqField1;
    }

    public void setReqField1(String reqField1)
    {
        this.reqField1 = reqField1;
    }

    public String getRecurring()
    {
        return isRecurring;
    }

    public void setRecurring(String recurring)
    {
        isRecurring = recurring;
    }

    public String getInterval()
    {
        return interval;
    }

    public void setInterval(String interval)
    {
        this.interval = interval;
    }

    public String getFrequency()
    {
        return frequency;
    }

    public void setFrequency(String frequency)
    {
        this.frequency = frequency;
    }

    public String getRunDate()
    {
        return runDate;
    }

    public void setRunDate(String runDate)
    {
        this.runDate = runDate;
    }

    public String getRecurring_subscrition_id()
    {
        return recurring_subscrition_id;
    }

    public void setRecurring_subscrition_id(String recurring_subscrition_id)
    {
        this.recurring_subscrition_id = recurring_subscrition_id;
    }

    public String getBankRecurringBillingID()
    {
        return bankRecurringBillingID;
    }

    public void setBankRecurringBillingID(String bankRecurringBillingID)
    {
        this.bankRecurringBillingID = bankRecurringBillingID;
    }

    public String getParentBankTransactionID()
    {
        return parentBankTransactionID;
    }

    public void setParentBankTransactionID(String parentBankTransactionID)
    {
        this.parentBankTransactionID = parentBankTransactionID;
    }

    public String getNewBankTransactionID()
    {
        return newBankTransactionID;
    }

    public void setNewBankTransactionID(String newBankTransactionID)
    {
        this.newBankTransactionID = newBankTransactionID;
    }

    public String getParentPzTransactionID()
    {
        return parentPzTransactionID;
    }

    public void setParentPzTransactionID(String parentPzTransactionID)
    {
        this.parentPzTransactionID = parentPzTransactionID;
    }

    public String getNewPzTransactionID()
    {
        return newPzTransactionID;
    }

    public void setNewPzTransactionID(String newPzTransactionID)
    {
        this.newPzTransactionID = newPzTransactionID;
    }

    public String getAmount()
    {
        return amount;
    }

    public void setAmount(String amount)
    {
        this.amount = amount;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getTransactionStatus()
    {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus)
    {
        this.transactionStatus = transactionStatus;
    }

    public String getOriginTrackingId()
    {
        return originTrackingId;
    }

    public void setOriginTrackingId(String originTrackingId)
    {
        this.originTrackingId = originTrackingId;
    }

    public String getCardHolderName()
    {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName)
    {
        this.cardHolderName = cardHolderName;
    }

    public String getFirstSix()
    {
        return firstSix;
    }

    public void setFirstSix(String firstSix)
    {
        this.firstSix = firstSix;
    }

    public String getLastFour()
    {
        return lastFour;
    }

    public void setLastFour(String lastFour)
    {
        this.lastFour = lastFour;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public String getRecurringStatus()
    {
        return recurringStatus;
    }

    public void setRecurringStatus(String recurringStatus)
    {
        this.recurringStatus = recurringStatus;
    }

    public String getRbid()
    {
        return rbid;
    }

    public void setRbid(String rbid)
    {
        this.rbid = rbid;
    }

    public String getRecurringRunDate()
    {
        return recurringRunDate;
    }

    public void setRecurringRunDate(String recurringRunDate)
    {
        this.recurringRunDate = recurringRunDate;
    }

    public String getActiveDeactive()
    {
        return activeDeactive;
    }

    public void setActiveDeactive(String activeDeactive)
    {
        this.activeDeactive = activeDeactive;
    }

    public String getRecurringRegisterDate()
    {
        return recurringRegisterDate;
    }

    public void setRecurringRegisterDate(String recurringRegisterDate)
    {
        this.recurringRegisterDate = recurringRegisterDate;
    }

    public String getOriginalBankTransactionId()
    {
        return originalBankTransactionId;
    }

    public void setOriginalBankTransactionId(String originalBankTransactionId)
    {
        this.originalBankTransactionId = originalBankTransactionId;
    }

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }
}
