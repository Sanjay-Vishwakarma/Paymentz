package com.manager.vo;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 26/7/14
 * Time: 8:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class TerminalVO
{
    String memberId;
    String terminalId;
    String accountId;
    String paymodeId;
    String cardTypeId;
    String customerId;
    String paymentName;
    String CardType;
    String paymentTypeName;
    String isActive;
    String displayName;
    float max_transaction_amount;
    String activationDate;
    String isRecurring;
    float min_transaction_amount;
    String memberCycleDays;
    String gateway;
    String inactivePeriodAllowed;
    String firstSubmissionAllowed;
    String transactionType;
    String currency;
    String partnerId;
    String gwMid;
    String contactPerson;
    String company_name;
    String gateway_name;
    String gateway_id;
    String daily_amount_limit;
    String monthly_amount_limit;
    String daily_card_limit;
    String weekly_card_limit;
    String monthly_card_limit;
    String daily_card_amount_limit;
    String weekly_card_amount_limit;
    String monthly_card_amount_limit;
    String min_trans_amount;
    String max_trans_amount;
    String priority;
    String isTest;
    String weekly_amount_limit;
    String isRestrictedTicketActive;
    String isManualRecurring;
    String addressDetails;
    String addressValidation;
    String cardDetailRequired;
    String isCardEncryptionEnable;
    String riskRuleActivation;
    String daily_avg_ticket;
    String weekly_avg_ticket;
    String monthly_avg_ticket;
    String settlementCurrency;
    String minPayoutAmount;
    String payoutActivation;
    String isPSTTerminal;
    String isTokenizationActive;
    String partnerName;
    String autoRedirectRequest;
    String reject3DCard;
    String currencyConversion;
    String conversionCurrency;
    String isCardWhitelisted;
    String isEmailWhitelisted;
    String isbin_routing;
    String isEmi_support;
    String whitelisting;
    String cardLimitCheckTerminalLevel;
    String cardLimitCheckAccountLevel;
    String cardAmountLimitCheckTerminalLevel;
    String cardAmountLimitCheckAccountLevel;
    String amountLimitCheckTerminalLevel;
    String amountLimitCheckAccountLevel;
    double conversionRate;
    String agentId;
    String wireId;
    String actionExecutorName;
    String actionExecutorId;
    String processor_partnerid;
    String is3DSupport;
    String startDate;
    String endDate;
    String payout_priority;
    String daily_amount_limit_check;
    String weekly_amount_limit_check;
    String monthly_amount_limit_check;
    String daily_card_limit_check;
    String weekly_card_limit_check;
    String monthly_card_limit_check;
    String daily_card_amount_limit_check;
    String weekly_card_amount_limit_check;
    String monthly_card_amount_limit_check;
    String parentBankWireId;
    String cardLimitCheck;
    String cardAmountLimitCheck;

    public String getDaily_amount_limit_check()
    {
        return daily_amount_limit_check;
    }

    public void setDaily_amount_limit_check(String daily_amount_limit_check)
    {
        this.daily_amount_limit_check = daily_amount_limit_check;
    }

    public String getWeekly_amount_limit_check()
    {
        return weekly_amount_limit_check;
    }

    public void setWeekly_amount_limit_check(String weekly_amount_limit_check)
    {
        this.weekly_amount_limit_check = weekly_amount_limit_check;
    }

    public String getMonthly_amount_limit_check()
    {
        return monthly_amount_limit_check;
    }

    public void setMonthly_amount_limit_check(String monthly_amount_limit_check)
    {
        this.monthly_amount_limit_check = monthly_amount_limit_check;
    }

    public String getDaily_card_limit_check()
    {
        return daily_card_limit_check;
    }

    public void setDaily_card_limit_check(String daily_card_limit_check)
    {
        this.daily_card_limit_check = daily_card_limit_check;
    }

    public String getWeekly_card_limit_check()
    {
        return weekly_card_limit_check;
    }

    public void setWeekly_card_limit_check(String weekly_card_limit_check)
    {
        this.weekly_card_limit_check = weekly_card_limit_check;
    }

    public String getMonthly_card_limit_check()
    {
        return monthly_card_limit_check;
    }

    public void setMonthly_card_limit_check(String monthly_card_limit_check)
    {
        this.monthly_card_limit_check = monthly_card_limit_check;
    }

    public String getDaily_card_amount_limit_check()
    {
        return daily_card_amount_limit_check;
    }

    public void setDaily_card_amount_limit_check(String daily_card_amount_limit_check)
    {
        this.daily_card_amount_limit_check = daily_card_amount_limit_check;
    }

    public String getWeekly_card_amount_limit_check()
    {
        return weekly_card_amount_limit_check;
    }

    public void setWeekly_card_amount_limit_check(String weekly_card_amount_limit_check)
    {
        this.weekly_card_amount_limit_check = weekly_card_amount_limit_check;
    }

    public String getMonthly_card_amount_limit_check()
    {
        return monthly_card_amount_limit_check;
    }

    public void setMonthly_card_amount_limit_check(String monthly_card_amount_limit_check)
    {
        this.monthly_card_amount_limit_check = monthly_card_amount_limit_check;
    }

    public String getWhitelisting()
    {
        return whitelisting;
    }

    public void setWhitelisting(String whitelisting)
    {
        this.whitelisting = whitelisting;
    }

    public String getIsbin_routing()
    {
        return isbin_routing;
    }

    public void setIsbin_routing(String isbin_routing)
    {
        this.isbin_routing = isbin_routing;
    }

    public String getIsEmi_support()
    {
        return isEmi_support;
    }

    public void setIsEmi_support(String isEmi_support)
    {
        this.isEmi_support = isEmi_support;
    }

    public String getCustomerId()
    {
        return customerId;
    }

    public void setCustomerId(String customerId)
    {
        this.customerId = customerId;
    }

    public String getGateway_id()
    {
        return gateway_id;
    }

    public void setGateway_id(String gateway_id)
    {
        this.gateway_id = gateway_id;
    }

    public String getCompany_name()
    {
        return company_name;
    }

    public void setCompany_name(String company_name)
    {
        this.company_name = company_name;
    }

    public String getPartnerId()
    {
        return partnerId;
    }

    public void setPartnerId(String partnerId)
    {
        this.partnerId = partnerId;
    }

    public String getTransactionType()
    {
        return transactionType;
    }

    public void setTransactionType(String transactionType)
    {
        this.transactionType = transactionType;
    }

    public String getIsTokenizationActive()
    {
        return isTokenizationActive;
    }

    public void setIsTokenizationActive(String isTokenizationActive)
    {
        this.isTokenizationActive = isTokenizationActive;
    }

    public String getGateway()
    {
        return gateway;
    }

    public void setGateway(String gateway)
    {
        this.gateway = gateway;
    }

    public String getCardDetailRequired()
    {
        return cardDetailRequired;
    }

    public void setCardDetailRequired(String cardDetailRequired)
    {
        this.cardDetailRequired = cardDetailRequired;
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

    public String getAddressDetails()
    {
        return addressDetails;
    }

    public void setAddressDetails(String addressDetails)
    {
        this.addressDetails = addressDetails;
    }

    public String getAddressValidation()
    {
        return addressValidation;
    }

    public void setAddressValidation(String addressValidation)
    {
        this.addressValidation = addressValidation;
    }

    public String getMemberCycleDays()
    {
        return memberCycleDays;
    }

    public void setMemberCycleDays(String memberCycleDays)
    {
        this.memberCycleDays = memberCycleDays;
    }

    public String getTerminalId()
    {
        return terminalId;
    }

    public void setTerminalId(String terminalId)
    {
        this.terminalId=terminalId;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId=accountId;
    }

    public String getPaymodeId()
    {
        return paymodeId;
    }

    public void setPaymodeId(String paymodeId)
    {
        this.paymodeId=paymodeId;
    }

    public String getCardTypeId()
    {
        return cardTypeId;
    }

    public void setCardTypeId(String cardTypeId)
    {
        this.cardTypeId =cardTypeId;
    }

    public String getPaymentName()
    {
        return paymentName;
    }

    public void setPaymentName(String paymentName)
    {
        this.paymentName=paymentName;
    }

    public String getCardType()
    {
        return CardType;
    }

    public void setCardType(String cardType)
    {
        this.CardType=cardType;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId=memberId;
    }

    public String toString()
    {
        return terminalId+"-"+paymentName+"-"+CardType;
    }

    public String getPaymentTypeName()
    {
        return paymentTypeName;
    }

    public void setPaymentTypeName(String paymentTypeName)
    {
        this.paymentTypeName = paymentTypeName;
    }

    public String getIsActive()
    {
        return isActive;
    }

    public void setIsActive(String isActive)
    {
        this.isActive = isActive;
    }

    public float getMax_transaction_amount()
    {
        return max_transaction_amount;
    }

    public void setMax_transaction_amount(float max_transaction_amount)
    {
        this.max_transaction_amount = max_transaction_amount;
    }

    public float getMin_transaction_amount()
    {
        return min_transaction_amount;
    }

    public void setMin_transaction_amount(float min_transaction_amount)
    {
        this.min_transaction_amount = min_transaction_amount;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getActivationDate()
    {
        return activationDate;
    }

    public void setActivationDate(String activationDate)
    {
        this.activationDate = activationDate;
    }

    public String getFirstSubmissionAllowed()
    {
        return firstSubmissionAllowed;
    }

    public void setFirstSubmissionAllowed(String firstSubmissionAllowed)
    {
        this.firstSubmissionAllowed = firstSubmissionAllowed;
    }

    public String getInactivePeriodAllowed()
    {
        return inactivePeriodAllowed;
    }

    public void setInactivePeriodAllowed(String inactivePeriodAllowed)
    {
        this.inactivePeriodAllowed = inactivePeriodAllowed;
    }

    public String getIsPSTTerminal()
    {
        return isPSTTerminal;
    }

    public void setIsPSTTerminal(String isPSTTerminal)
    {
        this.isPSTTerminal = isPSTTerminal;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getGwMid()
    {
        return gwMid;
    }

    public void setGwMid(String gwMid)
    {
        this.gwMid = gwMid;
    }

    public String getContactPerson()
    {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson)
    {
        this.contactPerson = contactPerson;
    }

    public String getGateway_name()
    {
        return gateway_name;
    }

    public void setGateway_name(String gateway_name)
    {
        this.gateway_name = gateway_name;
    }

       public String getDaily_amount_limit()
    {
        return daily_amount_limit;
    }

    public void setDaily_amount_limit(String daily_amount_limit)
    {
        this.daily_amount_limit = daily_amount_limit;
    }

    public String getMonthly_amount_limit()
    {
        return monthly_amount_limit;
    }

    public void setMonthly_amount_limit(String monthly_amount_limit)
    {
        this.monthly_amount_limit = monthly_amount_limit;
    }

    public String getDaily_card_limit()
    {
        return daily_card_limit;
    }

    public void setDaily_card_limit(String daily_card_limit)
    {
        this.daily_card_limit = daily_card_limit;
    }

    public String getWeekly_card_limit()
    {
        return weekly_card_limit;
    }

    public void setWeekly_card_limit(String weekly_card_limit)
    {
        this.weekly_card_limit = weekly_card_limit;
    }

    public String getMonthly_card_limit()
    {
        return monthly_card_limit;
    }

    public void setMonthly_card_limit(String monthly_card_limit)
    {
        this.monthly_card_limit = monthly_card_limit;
    }

    public String getDaily_card_amount_limit()
    {
        return daily_card_amount_limit;
    }

    public void setDaily_card_amount_limit(String daily_card_amount_limit)
    {
        this.daily_card_amount_limit = daily_card_amount_limit;
    }

    public String getWeekly_card_amount_limit()
    {
        return weekly_card_amount_limit;
    }

    public void setWeekly_card_amount_limit(String weekly_card_amount_limit)
    {
        this.weekly_card_amount_limit = weekly_card_amount_limit;
    }

    public String getMonthly_card_amount_limit()
    {
        return monthly_card_amount_limit;
    }

    public void setMonthly_card_amount_limit(String monthly_card_amount_limit)
    {
        this.monthly_card_amount_limit = monthly_card_amount_limit;
    }

    public String getMin_trans_amount()
    {
        return min_trans_amount;
    }

    public void setMin_trans_amount(String min_trans_amount)
    {
        this.min_trans_amount = min_trans_amount;
    }

    public String getMax_trans_amount()
    {
        return max_trans_amount;
    }

    public void setMax_trans_amount(String max_trans_amount)
    {
        this.max_trans_amount = max_trans_amount;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public String getIsTest()
    {
        return isTest;
    }

    public void setIsTest(String isTest)
    {
        this.isTest = isTest;
    }

    public String getWeekly_amount_limit()
    {
        return weekly_amount_limit;
    }

    public void setWeekly_amount_limit(String weekly_amount_limit)
    {
        this.weekly_amount_limit = weekly_amount_limit;
    }

    public String getIsRestrictedTicketActive()
    {
        return isRestrictedTicketActive;
    }

    public void setIsRestrictedTicketActive(String isRestrictedTicketActive)
    {
        this.isRestrictedTicketActive = isRestrictedTicketActive;
    }

    public String getIsCardEncryptionEnable()
    {
        return isCardEncryptionEnable;
    }

    public void setIsCardEncryptionEnable(String isCardEncryptionEnable)
    {
        this.isCardEncryptionEnable = isCardEncryptionEnable;
    }

    public String getRiskRuleActivation()
    {
        return riskRuleActivation;
    }

    public void setRiskRuleActivation(String riskRuleActivation)
    {
        this.riskRuleActivation = riskRuleActivation;
    }

    public String getDaily_avg_ticket()
    {
        return daily_avg_ticket;
    }

    public void setDaily_avg_ticket(String daily_avg_ticket)
    {
        this.daily_avg_ticket = daily_avg_ticket;
    }

    public String getWeekly_avg_ticket()
    {
        return weekly_avg_ticket;
    }

    public void setWeekly_avg_ticket(String weekly_avg_ticket)
    {
        this.weekly_avg_ticket = weekly_avg_ticket;
    }

    public String getMonthly_avg_ticket()
    {
        return monthly_avg_ticket;
    }

    public void setMonthly_avg_ticket(String monthly_avg_ticket)
    {
        this.monthly_avg_ticket = monthly_avg_ticket;
    }

    public String getSettlementCurrency()
    {
        return settlementCurrency;
    }

    public void setSettlementCurrency(String settlementCurrency)
    {
        this.settlementCurrency = settlementCurrency;
    }

    public String getMinPayoutAmount()
    {
        return minPayoutAmount;
    }

    public void setMinPayoutAmount(String minPayoutAmount)
    {
        this.minPayoutAmount = minPayoutAmount;
    }

    public String getPartnerName()
    {
        return partnerName;
    }

    public void setPartnerName(String partnerName)
    {
        this.partnerName = partnerName;
    }

    public String getPayoutActivation()
    {
        return payoutActivation;
    }

    public void setPayoutActivation(String payoutActivation)
    {
        this.payoutActivation = payoutActivation;
    }

    public String getAutoRedirectRequest()
    {
        return autoRedirectRequest;
    }

    public void setAutoRedirectRequest(String autoRedirectRequest)
    {
        this.autoRedirectRequest = autoRedirectRequest;
    }

    public String getReject3DCard()
    {
        return reject3DCard;
    }

    public void setReject3DCard(String reject3DCard)
    {
        this.reject3DCard = reject3DCard;
    }

    public String getCurrencyConversion()
    {
        return currencyConversion;
    }

    public void setCurrencyConversion(String currencyConversion)
    {
        this.currencyConversion = currencyConversion;
    }

    public String getConversionCurrency()
    {
        return conversionCurrency;
    }

    public void setConversionCurrency(String conversionCurrency)
    {
        this.conversionCurrency = conversionCurrency;
    }

    public String getIsCardWhitelisted()
    {
        return isCardWhitelisted;
    }

    public void setIsCardWhitelisted(String isCardWhitelisted)
    {
        this.isCardWhitelisted = isCardWhitelisted;
    }

    public String getIsEmailWhitelisted()
    {
        return isEmailWhitelisted;
    }

    public void setIsEmailWhitelisted(String isEmailWhitelisted)
    {
        this.isEmailWhitelisted = isEmailWhitelisted;
    }

    public String getCardLimitCheckAccountLevel()
    {
        return cardLimitCheckAccountLevel;
    }

    public void setCardLimitCheckAccountLevel(String cardLimitCheckAccountLevel)
    {
        this.cardLimitCheckAccountLevel = cardLimitCheckAccountLevel;
    }

    public String getCardLimitCheckTerminalLevel()
    {
        return cardLimitCheckTerminalLevel;
    }

    public void setCardLimitCheckTerminalLevel(String cardLimitCheckTerminalLevel)
    {
        this.cardLimitCheckTerminalLevel = cardLimitCheckTerminalLevel;
    }

    public String getCardAmountLimitCheckTerminalLevel()
    {
        return cardAmountLimitCheckTerminalLevel;
    }

    public void setCardAmountLimitCheckTerminalLevel(String cardAmountLimitCheckTerminalLevel)
    {
        this.cardAmountLimitCheckTerminalLevel = cardAmountLimitCheckTerminalLevel;
    }

    public String getCardAmountLimitCheckAccountLevel()
    {
        return cardAmountLimitCheckAccountLevel;
    }

    public void setCardAmountLimitCheckAccountLevel(String cardAmountLimitCheckAccountLevel)
    {
        this.cardAmountLimitCheckAccountLevel = cardAmountLimitCheckAccountLevel;
    }

    public String getAmountLimitCheckTerminalLevel()
    {
        return amountLimitCheckTerminalLevel;
    }

    public void setAmountLimitCheckTerminalLevel(String amountLimitCheckTerminalLevel)
    {
        this.amountLimitCheckTerminalLevel = amountLimitCheckTerminalLevel;
    }

    public String getAmountLimitCheckAccountLevel()
    {
        return amountLimitCheckAccountLevel;
    }

    public void setAmountLimitCheckAccountLevel(String amountLimitCheckAccountLevel)
    {
        this.amountLimitCheckAccountLevel = amountLimitCheckAccountLevel;
    }

    public double getConversionRate()
    {
        return conversionRate;
    }

    public void setConversionRate(double conversionRate)
    {
        this.conversionRate = conversionRate;
    }

    public String getAgentId()
    {
        return agentId;
    }

    public void setAgentId(String agentId)
    {
        this.agentId = agentId;
    }

    public String getWireId()
    {
        return wireId;
    }

    public void setWireId(String wireId)
    {
        this.wireId = wireId;
    }

    public String getActionExecutorId()
    {
        return actionExecutorId;
    }

    public void setActionExecutorId(String actionExecutorId)
    {
        this.actionExecutorId = actionExecutorId;
    }

    public String getActionExecutorName()
    {
        return actionExecutorName;
    }

    public void setActionExecutorName(String actionExecutorName)
    {
        this.actionExecutorName = actionExecutorName;
    }

    public String getProcessor_partnerid()
    {
        return processor_partnerid;
    }

    public void setProcessor_partnerid(String processor_partnerid)
    {
        this.processor_partnerid = processor_partnerid;
    }

    public String getIs3DSupport()
    {
        return is3DSupport;
    }

    public void setIs3DSupport(String is3DSupport)
    {
        this.is3DSupport = is3DSupport;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public String getPayout_priority()
    {
        return payout_priority;
    }

    public void setPayout_priority(String payout_priority)
    {
        this.payout_priority = payout_priority;
    }

    public String getParentBankWireId()
    {
        return parentBankWireId;
    }

    public void setParentBankWireId(String parentBankWireId)
    {
        this.parentBankWireId = parentBankWireId;
    }

    public String getCardLimitCheck()
    {
        return cardLimitCheck;
    }

    public void setCardLimitCheck(String cardLimitCheck)
    {
        this.cardLimitCheck = cardLimitCheck;
    }

    public String getCardAmountLimitCheck()
    {
        return cardAmountLimitCheck;
    }

    public void setCardAmountLimitCheck(String cardAmountLimitCheck)
    {
        this.cardAmountLimitCheck = cardAmountLimitCheck;
    }
}