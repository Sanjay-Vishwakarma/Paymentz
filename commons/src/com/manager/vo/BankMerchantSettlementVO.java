package com.manager.vo;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 9/10/14
 * Time: 3:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class BankMerchantSettlementVO
{
    String bankMerchantSettlementId;
    String accountId;
    String memberId;
    String bankReceivedSettlementId;//cycleId
    String pgTypeId;

    String isPaid;

    public String getBankMerchantSettlementId()
    {
        return bankMerchantSettlementId;
    }

    public void setBankMerchantSettlementId(String bankMerchantSettlementId)
    {
        this.bankMerchantSettlementId = bankMerchantSettlementId;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public String getPgTypeId()
    {
        return pgTypeId;
    }

    public void setPgTypeId(String pgTypeId)
    {
        this.pgTypeId = pgTypeId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public String getMemberId()
    {
        return memberId;
    }

    public void setMemberId(String memberId)
    {
        this.memberId = memberId;
    }

    public String getBankReceivedSettlementId()
    {
        return bankReceivedSettlementId;
    }

    public void setBankReceivedSettlementId(String bankReceivedSettlementId)
    {
        this.bankReceivedSettlementId = bankReceivedSettlementId;
    }

    public String getPaid()
    {
        return isPaid;
    }

    public void setPaid(String paid)
    {
        isPaid = paid;
    }
}
