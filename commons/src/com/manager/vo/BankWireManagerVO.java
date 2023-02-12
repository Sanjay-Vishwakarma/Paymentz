package com.manager.vo;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 10/9/14
 * Time: 6:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class BankWireManagerVO
{
    String bankwiremanagerId ;
    String  settleddate ;
    String  pgtypeId ;
    String accountId ;
    String currency;
    String mid;
    String bank_start_date ;
    String  bank_end_date ;
    String  server_start_date;
    String  server_end_date ;
    String processing_amount ;
    String  grossAmount ;
    String  netfinal_amount ;
    String  unpaid_amount ;
    String  isrollingreservereleasewire ;
    String  rollingreservereleasedateupto ;
    String  declinedcoveredupto ;
    String  chargebackcoveredupto;
    String  reversedCoveredUpto ;
    String  banksettlement_report_file;
    String   banksettlement_transaction_file ;
    String   isSettlementCronExceuted;
    String  isPayoutCronExcuted ;
    String  ispaid;
    String isPartnerCommCronExecuted;
    String isAgentCommCronExecuted;
    String settlementCycleId;

    String declinedcoveredStartdate;
    String chargebackcoveredStartdate;
    String reversedCoveredStartdate;
    String rollingreservereleaseStartdate;

    String declinedcoveredtimeStarttime;
    String chargebackcoveredtimeStarttime;
    String reversedcoveredtimeStarttime;
    String rollingreservereleaseStarttime;

    //temporary block kept only for the split from the db and datepicker ,waiting for datepicker as well as timepicker to work
    String bank_start_timestamp;
    String bank_end_timestamp;
    String server_start_timestamp;
    String server_end_timestamp;
    String settled_timestamp;

    String rollingreservetime;
    String declinedcoveredtime;
    String chargebackcoveredtime;
    String reversedcoveredtime;
    String parent_bankwireid;
    //Access mutator

    public String getBanksettlement_transaction_file()
    {
        return banksettlement_transaction_file;
    }

    public void setBanksettlement_transaction_file(String banksettlement_transaction_file)
    {
        this.banksettlement_transaction_file = banksettlement_transaction_file;
    }

    public String getBankwiremanagerId()
    {
        return bankwiremanagerId;
    }

    public void setBankwiremanagerId(String bankwiremanagerId)
    {
        this.bankwiremanagerId = bankwiremanagerId;
    }

    public String getSettleddate()
    {
        return settleddate;
    }

    public void setSettleddate(String settleddate)
    {
        this.settleddate = settleddate;
    }

    public String getPgtypeId()
    {
        return pgtypeId;
    }

    public void setPgtypeId(String pgtypeId)
    {
        this.pgtypeId = pgtypeId;
    }

    public String getAccountId()
    {
        return accountId;
    }

    public void setAccountId(String accountId)
    {
        this.accountId = accountId;
    }

    public String getMid()
    {
        return mid;
    }

    public void setMid(String mid)
    {
        this.mid = mid;
    }

    public String getBank_start_date()
    {
        return bank_start_date;
    }

    public void setBank_start_date(String bank_start_date)
    {
        this.bank_start_date = bank_start_date;
    }

    public String getBank_end_date()
    {
        return bank_end_date;
    }

    public void setBank_end_date(String bank_end_date)
    {
        this.bank_end_date = bank_end_date;
    }

    public String getServer_start_date()
    {
        return server_start_date;
    }

    public void setServer_start_date(String server_start_date)
    {
        this.server_start_date = server_start_date;
    }

    public String getServer_end_date()
    {
        return server_end_date;
    }

    public void setServer_end_date(String server_end_date)
    {
        this.server_end_date = server_end_date;
    }

    public String getProcessing_amount()
    {
        return processing_amount;
    }

    public void setProcessing_amount(String processing_amount)
    {
        this.processing_amount = processing_amount;
    }

    public String getGrossAmount()
    {
        return grossAmount;
    }

    public void setGrossAmount(String grossAmount)
    {
        this.grossAmount = grossAmount;
    }

    public String getNetfinal_amount()
    {
        return netfinal_amount;
    }

    public void setNetfinal_amount(String netfinal_amount)
    {
        this.netfinal_amount = netfinal_amount;
    }

    public String getUnpaid_amount()
    {
        return unpaid_amount;
    }

    public void setUnpaid_amount(String unpaid_amount)
    {
        this.unpaid_amount = unpaid_amount;
    }

    public String getIsrollingreservereleasewire()
    {
        return isrollingreservereleasewire;
    }

    public void setIsrollingreservereleasewire(String isrollingreservereleasewire)
    {
        this.isrollingreservereleasewire = isrollingreservereleasewire;
    }

    public String getRollingreservereleasedateupto()
    {
        return rollingreservereleasedateupto;
    }

    public void setRollingreservereleasedateupto(String rollingreservereleasedateupto)
    {
        this.rollingreservereleasedateupto = rollingreservereleasedateupto;
    }

    public String getDeclinedcoveredupto()
    {
        return declinedcoveredupto;
    }

    public void setDeclinedcoveredupto(String declinedcoveredupto)
    {
        this.declinedcoveredupto = declinedcoveredupto;
    }

    public String getChargebackcoveredupto()
    {
        return chargebackcoveredupto;
    }

    public void setChargebackcoveredupto(String chargebackcoveredupto)
    {
        this.chargebackcoveredupto = chargebackcoveredupto;
    }

    public String getReversedCoveredUpto()
    {
        return reversedCoveredUpto;
    }

    public void setReversedCoveredUpto(String reversedCoveredUpto)
    {
        this.reversedCoveredUpto = reversedCoveredUpto;
    }

    public String getBanksettlement_report_file()
    {
        return banksettlement_report_file;
    }

    public void setBanksettlement_report_file(String banksettlement_report_file)
    {
        this.banksettlement_report_file = banksettlement_report_file;
    }

    public String getSettlementCronExceuted()
    {
        return isSettlementCronExceuted;
    }

    public void setSettlementCronExceuted(String settlementCronExceuted)
    {
        isSettlementCronExceuted = settlementCronExceuted;
    }

    public String getPayoutCronExcuted()
    {
        return isPayoutCronExcuted;
    }

    public void setPayoutCronExcuted(String payoutCronExcuted)
    {
        isPayoutCronExcuted = payoutCronExcuted;
    }

    public String getIspaid()
    {
        return ispaid;
    }

    public void setIspaid(String ispaid)
    {
        this.ispaid = ispaid;
    }
    //temporary variables should bve removed after the datetimepicker added
    public String getBank_start_timestamp()
    {
        return bank_start_timestamp;
    }

    public void setBank_start_timestamp(String bank_start_timestamp)
    {
        this.bank_start_timestamp = bank_start_timestamp;
    }

    public String getBank_end_timestamp()
    {
        return bank_end_timestamp;
    }

    public void setBank_end_timestamp(String bank_end_timestamp)
    {
        this.bank_end_timestamp = bank_end_timestamp;
    }

    public String getServer_start_timestamp()
    {
        return server_start_timestamp;
    }

    public void setServer_start_timestamp(String server_start_timestamp)
    {
        this.server_start_timestamp = server_start_timestamp;
    }

    public String getServer_end_timestamp()
    {
        return server_end_timestamp;
    }

    public void setServer_end_timestamp(String server_end_timestamp)
    {
        this.server_end_timestamp = server_end_timestamp;
    }

    public String getSettled_timestamp()
    {
        return settled_timestamp;
    }

    public void setSettled_timestamp(String settled_timestamp)
    {
        this.settled_timestamp = settled_timestamp;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setCurrency(String currency)
    {
        this.currency = currency;
    }

    public String getRollingreservetime()
    {
        return rollingreservetime;
    }

    public void setRollingreservetime(String rollingreservetime)
    {
        this.rollingreservetime = rollingreservetime;
    }

    public String getDeclinedcoveredtime()
    {
        return declinedcoveredtime;
    }

    public void setDeclinedcoveredtime(String declinedcoveredtime)
    {
        this.declinedcoveredtime = declinedcoveredtime;
    }

    public String getChargebackcoveredtime()
    {
        return chargebackcoveredtime;
    }

    public void setChargebackcoveredtime(String chargebackcoveredtime)
    {
        this.chargebackcoveredtime = chargebackcoveredtime;
    }

    public String getReversedcoveredtime()
    {
        return reversedcoveredtime;
    }

    public void setReversedcoveredtime(String reversedcoveredtime)
    {
        this.reversedcoveredtime = reversedcoveredtime;
    }

    public String getIsSettlementCronExceuted()
    {
        return isSettlementCronExceuted;
    }

    public void setIsSettlementCronExceuted(String isSettlementCronExceuted)
    {
        this.isSettlementCronExceuted = isSettlementCronExceuted;
    }

    public String getIsPartnerCommCronExecuted()
    {
        return isPartnerCommCronExecuted;
    }

    public void setIsPartnerCommCronExecuted(String isPartnerCommCronExecuted)
    {
        this.isPartnerCommCronExecuted = isPartnerCommCronExecuted;
    }

    public String getIsPayoutCronExcuted()
    {
        return isPayoutCronExcuted;
    }

    public void setIsPayoutCronExcuted(String isPayoutCronExcuted)
    {
        this.isPayoutCronExcuted = isPayoutCronExcuted;
    }

    public String getIsAgentCommCronExecuted()
    {
        return isAgentCommCronExecuted;
    }

    public void setIsAgentCommCronExecuted(String isAgentCommCronExecuted)
    {
        this.isAgentCommCronExecuted = isAgentCommCronExecuted;
    }

    public String getSettlementCycleId()
    {
        return settlementCycleId;
    }

    public void setSettlementCycleId(String settlementCycleId)
    {
        this.settlementCycleId = settlementCycleId;
    }

    public String getDeclinedcoveredStartdate()
    {
        return declinedcoveredStartdate;
    }

    public void setDeclinedcoveredStartdate(String declinedcoveredStartdate)
    {
        this.declinedcoveredStartdate = declinedcoveredStartdate;
    }

    public String getChargebackcoveredStartdate()
    {
        return chargebackcoveredStartdate;
    }

    public void setChargebackcoveredStartdate(String chargebackcoveredStartdate)
    {
        this.chargebackcoveredStartdate = chargebackcoveredStartdate;
    }

    public String getReversedCoveredStartdate()
    {
        return reversedCoveredStartdate;
    }

    public void setReversedCoveredStartdate(String reversedCoveredStartdate)
    {
        this.reversedCoveredStartdate = reversedCoveredStartdate;
    }

    public String getDeclinedcoveredtimeStarttime()
    {
        return declinedcoveredtimeStarttime;
    }

    public void setDeclinedcoveredtimeStarttime(String declinedcoveredtimeStarttime)
    {
        this.declinedcoveredtimeStarttime = declinedcoveredtimeStarttime;
    }

    public String getChargebackcoveredtimeStarttime()
    {
        return chargebackcoveredtimeStarttime;
    }

    public void setChargebackcoveredtimeStarttime(String chargebackcoveredtimeStarttime)
    {
        this.chargebackcoveredtimeStarttime = chargebackcoveredtimeStarttime;
    }

    public String getReversedcoveredtimeStarttime()
    {
        return reversedcoveredtimeStarttime;
    }

    public void setReversedcoveredtimeStarttime(String reversedcoveredtimeStarttime)
    {
        this.reversedcoveredtimeStarttime = reversedcoveredtimeStarttime;
    }

    public String getRollingreservereleaseStartdate()
    {
        return rollingreservereleaseStartdate;
    }

    public void setRollingreservereleaseStartdate(String rollingreservereleaseStartdate)
    {
        this.rollingreservereleaseStartdate = rollingreservereleaseStartdate;
    }

    public String getRollingreservereleaseStarttime()
    {
        return rollingreservereleaseStarttime;
    }

    public void setRollingreservereleaseStarttime(String rollingreservereleaseStarttime)
    {
        this.rollingreservereleaseStarttime = rollingreservereleaseStarttime;
    }

    public String getParent_bankwireid()
    {
        return parent_bankwireid;
    }

    public void setParent_bankwireid(String parent_bankwireid)
    {
        this.parent_bankwireid = parent_bankwireid;
    }
}
