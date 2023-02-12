package com.manager.vo;


/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 9/1/14
 * Time: 2:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class BankRecievedSettlementCycleVO
{
    String bankSettlementReceivedId;
    String accountId;
    String pgTypeId;
    String merchantId;
    String settlementDate;
    String expected_startDate;
    String expected_endDate;
    String actual_startDate;
    String actual_endDate;
    String bank_settlementId;
    String isSettlementCronExecuted;
    String isPayoutCronExecuted;

    public String getBankSettlementReceivedId()
    {
        return bankSettlementReceivedId;
    }

    public void setBankSettlementReceivedId(String bankSettlementReceivedId)
    {
        this.bankSettlementReceivedId = bankSettlementReceivedId;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public String getPgTypeId()
    {
        return pgTypeId;
    }

    public void setPgTypeId(String pgTypeId)
    {
        this.pgTypeId = pgTypeId;
    }

    public String getMerchantId()
    {
        return merchantId;
    }

    public void setMerchantId(String merchantId)
    {
        this.merchantId = merchantId;
    }

    public String getSettlementDate()
    {
        return settlementDate;
    }

    public void setSettlementDate(String settlementDate)
    {
        this.settlementDate = settlementDate;
    }

    public String getExpected_startDate()
    {
        return expected_startDate;
    }

    public void setExpected_startDate(String expected_startDate)
    {
        this.expected_startDate = expected_startDate;
    }

    public String getExpected_endDate()
    {
        return expected_endDate;
    }

    public void setExpected_endDate(String expected_endDate)
    {
        this.expected_endDate = expected_endDate;
    }

    public String getActual_startDate()
    {
        return actual_startDate;
    }

    public void setActual_startDate(String actual_startDate)
    {
        this.actual_startDate = actual_startDate;
    }

    public String getActual_endDate()
    {
        return actual_endDate;
    }

    public void setActual_endDate(String actual_endDate)
    {
        this.actual_endDate = actual_endDate;
    }

    public String getBank_settlementId()
    {
        return bank_settlementId;
    }

    public void setBank_settlementId(String bank_settlementId)
    {
        this.bank_settlementId = bank_settlementId;
    }

    public String getSettlementCronExecuted()
    {
        return isSettlementCronExecuted;
    }

    public void setSettlementCronExecuted(String settlementCronExecuted)
    {
        isSettlementCronExecuted = settlementCronExecuted;
    }

    public String getPayoutCronExecuted()
    {
        return isPayoutCronExecuted;
    }

    public void setPayoutCronExecuted(String payoutCronExecuted)
    {
        isPayoutCronExecuted = payoutCronExecuted;
    }
}
