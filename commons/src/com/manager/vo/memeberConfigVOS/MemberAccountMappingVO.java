package com.manager.vo.memeberConfigVOS;

/**
 * Created with IntelliJ IDEA.
 * User: Administrator
 * Date: 12/11/14
 * Time: 3:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class MemberAccountMappingVO
{
    //member details
    String memberid ;
    String accountid ;
    String paymodeid;
    String cardtypeid;
    String terminalid;

    //check
    String isActive;
    String priority;
    String isTest;
    String riskRuleActivation;
    String settlementCurrency;
    String isCardWhitelisted;
    String isEmailWhitelisted;
    String isTokenizationAllowed;
    String addressDetails;
    String addressValidation;
    String binRouting;
    String emiSupport;


    //charges in int
    int taxper;
    int reversalcharge;
    int withdrawalcharge;
    int chargebackcharge;

    int fraudVerificationCharge;
    int annualCharge;
    int setupCharge;

    int monthlyGatewayCharge;
    int monthlyAccountMntCharge;
    int reportCharge;
    int fraudulentCharge;
    int autoRepresentationCharge;

    //charges in double
    double interchangePlusCharge;
    double daily_card_amount_limit;
    double weekly_card_amount_limit;
    double monthly_card_amount_limit;
    double daily_amount_limit;
    double monthly_amount_limit;
    double daily_card_limit;
    double weekly_card_limit;
    double monthly_card_limit;
    double chargePercentage;
    double fixApprovalCharge;
    double fixDeclinedCharge;
    double min_transaction_amount;
    double max_transaction_amount;
    double weekly_amount_limit;
    double daily_avg_ticket;
    double weekly_avg_ticket;
    double monthly_avg_ticket;
    double min_payout_amount;

    double reservePercentage;
    double fxClearanceChargePercentage;

    public String getMemberid()
    {
        return memberid;
    }

    public void setMemberid(String memberid)
    {
        this.memberid = memberid;
    }

    public String getAccountid()
    {
        return accountid;
    }

    public void setAccountid(String accountid)
    {
        this.accountid = accountid;
    }

    public String getPaymodeid()
    {
        return paymodeid;
    }

    public void setPaymodeid(String paymodeid)
    {
        this.paymodeid = paymodeid;
    }

    public String getCardtypeid()
    {
        return cardtypeid;
    }

    public void setCardtypeid(String cardtypeid)
    {
        this.cardtypeid = cardtypeid;
    }

    public String getTerminalid()
    {
        return terminalid;
    }

    public void setTerminalid(String terminalid)
    {
        this.terminalid = terminalid;
    }

    public String getActive()
    {
        return isActive;
    }

    public void setActive(String active)
    {
        isActive = active;
    }

    public String getPriority()
    {
        return priority;
    }

    public void setPriority(String priority)
    {
        this.priority = priority;
    }

    public String getTest()
    {
        return isTest;
    }

    public void setTest(String test)
    {
        isTest = test;
    }

    public int getTaxper()
    {
        return taxper;
    }

    public void setTaxper(int taxper)
    {
        this.taxper = taxper;
    }

    public int getReversalcharge()
    {
        return reversalcharge;
    }

    public void setReversalcharge(int reversalcharge)
    {
        this.reversalcharge = reversalcharge;
    }

    public int getWithdrawalcharge()
    {
        return withdrawalcharge;
    }

    public void setWithdrawalcharge(int withdrawalcharge)
    {
        this.withdrawalcharge = withdrawalcharge;
    }

    public int getChargebackcharge()
    {
        return chargebackcharge;
    }

    public void setChargebackcharge(int chargebackcharge)
    {
        this.chargebackcharge = chargebackcharge;
    }

    public double getReservePercentage()
    {
        return reservePercentage;
    }

    public void setReservePercentage(double reservePercentage)
    {
        this.reservePercentage = reservePercentage;
    }

    public int getFraudVerificationCharge()
    {
        return fraudVerificationCharge;
    }

    public void setFraudVerificationCharge(int fraudVerificationCharge)
    {
        this.fraudVerificationCharge = fraudVerificationCharge;
    }

    public int getAnnualCharge()
    {
        return annualCharge;
    }

    public void setAnnualCharge(int annualCharge)
    {
        this.annualCharge = annualCharge;
    }

    public int getSetupCharge()
    {
        return setupCharge;
    }

    public void setSetupCharge(int setupCharge)
    {
        this.setupCharge = setupCharge;
    }

    public double getFxClearanceChargePercentage()
    {
        return fxClearanceChargePercentage;
    }

    public void setFxClearanceChargePercentage(double fxClearanceChargePercentage)
    {
        this.fxClearanceChargePercentage = fxClearanceChargePercentage;
    }

    public int getMonthlyGatewayCharge()
    {
        return monthlyGatewayCharge;
    }

    public void setMonthlyGatewayCharge(int monthlyGatewayCharge)
    {
        this.monthlyGatewayCharge = monthlyGatewayCharge;
    }

    public int getMonthlyAccountMntCharge()
    {
        return monthlyAccountMntCharge;
    }

    public void setMonthlyAccountMntCharge(int monthlyAccountMntCharge)
    {
        this.monthlyAccountMntCharge = monthlyAccountMntCharge;
    }

    public int getReportCharge()
    {
        return reportCharge;
    }

    public void setReportCharge(int reportCharge)
    {
        this.reportCharge = reportCharge;
    }

    public int getFraudulentCharge()
    {
        return fraudulentCharge;
    }

    public void setFraudulentCharge(int fraudulentCharge)
    {
        this.fraudulentCharge = fraudulentCharge;
    }

    public int getAutoRepresentationCharge()
    {
        return autoRepresentationCharge;
    }

    public void setAutoRepresentationCharge(int autoRepresentationCharge)
    {
        this.autoRepresentationCharge = autoRepresentationCharge;
    }

    public double getInterchangePlusCharge()
    {
        return interchangePlusCharge;
    }

    public void setInterchangePlusCharge(double interchangePlusCharge)
    {
        this.interchangePlusCharge = interchangePlusCharge;
    }

    public double getDaily_card_amount_limit()
    {
        return daily_card_amount_limit;
    }

    public void setDaily_card_amount_limit(double daily_card_amount_limit)
    {
        this.daily_card_amount_limit = daily_card_amount_limit;
    }

    public double getWeekly_card_amount_limit()
    {
        return weekly_card_amount_limit;
    }

    public void setWeekly_card_amount_limit(double weekly_card_amount_limit)
    {
        this.weekly_card_amount_limit = weekly_card_amount_limit;
    }

    public double getMonthly_card_amount_limit()
    {
        return monthly_card_amount_limit;
    }

    public void setMonthly_card_amount_limit(double monthly_card_amount_limit)
    {
        this.monthly_card_amount_limit = monthly_card_amount_limit;
    }

    public double getDaily_amount_limit()
    {
        return daily_amount_limit;
    }

    public void setDaily_amount_limit(double daily_amount_limit)
    {
        this.daily_amount_limit = daily_amount_limit;
    }

    public double getMonthly_amount_limit()
    {
        return monthly_amount_limit;
    }

    public void setMonthly_amount_limit(double monthly_amount_limit)
    {
        this.monthly_amount_limit = monthly_amount_limit;
    }

    public double getDaily_card_limit()
    {
        return daily_card_limit;
    }

    public void setDaily_card_limit(double daily_card_limit)
    {
        this.daily_card_limit = daily_card_limit;
    }

    public double getWeekly_card_limit()
    {
        return weekly_card_limit;
    }

    public void setWeekly_card_limit(double weekly_card_limit)
    {
        this.weekly_card_limit = weekly_card_limit;
    }

    public double getMonthly_card_limit()
    {
        return monthly_card_limit;
    }

    public void setMonthly_card_limit(double monthly_card_limit)
    {
        this.monthly_card_limit = monthly_card_limit;
    }

    public double getChargePercentage()
    {
        return chargePercentage;
    }

    public void setChargePercentage(double chargePercentage)
    {
        this.chargePercentage = chargePercentage;
    }

    public double getFixApprovalCharge()
    {
        return fixApprovalCharge;
    }

    public void setFixApprovalCharge(double fixApprovalCharge)
    {
        this.fixApprovalCharge = fixApprovalCharge;
    }

    public double getFixDeclinedCharge()
    {
        return fixDeclinedCharge;
    }

    public void setFixDeclinedCharge(double fixDeclinedCharge)
    {
        this.fixDeclinedCharge = fixDeclinedCharge;
    }

    public double getMin_transaction_amount()
    {
        return min_transaction_amount;
    }

    public void setMin_transaction_amount(double min_transaction_amount)
    {
        this.min_transaction_amount = min_transaction_amount;
    }

    public double getMax_transaction_amount()
    {
        return max_transaction_amount;
    }

    public void setMax_transaction_amount(double max_transaction_amount)
    {
        this.max_transaction_amount = max_transaction_amount;
    }

    public double getWeekly_amount_limit()
    {
        return weekly_amount_limit;
    }

    public void setWeekly_amount_limit(double weekly_amount_limit)
    {
        this.weekly_amount_limit = weekly_amount_limit;
    }

    public String getIsActive()
    {
        return isActive;
    }

    public void setIsActive(String isActive)
    {
        this.isActive = isActive;
    }

    public String getIsTest()
    {
        return isTest;
    }

    public void setIsTest(String isTest)
    {
        this.isTest = isTest;
    }

    public double getDaily_avg_ticket()
    {
        return daily_avg_ticket;
    }

    public void setDaily_avg_ticket(double daily_avg_ticket)
    {
        this.daily_avg_ticket = daily_avg_ticket;
    }

    public double getWeekly_avg_ticket()
    {
        return weekly_avg_ticket;
    }

    public void setWeekly_avg_ticket(double weekly_avg_ticket)
    {
        this.weekly_avg_ticket = weekly_avg_ticket;
    }

    public double getMonthly_avg_ticket()
    {
        return monthly_avg_ticket;
    }

    public void setMonthly_avg_ticket(double monthly_avg_ticket)
    {
        this.monthly_avg_ticket = monthly_avg_ticket;
    }

    public String getRiskRuleActivation()
    {
        return riskRuleActivation;
    }

    public void setRiskRuleActivation(String riskRuleActivation)
    {
        this.riskRuleActivation = riskRuleActivation;
    }

    public String getSettlementCurrency()
    {
        return settlementCurrency;
    }

    public void setSettlementCurrency(String settlementCurrency)
    {
        this.settlementCurrency = settlementCurrency;
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

    public double getMin_payout_amount()
    {
        return min_payout_amount;
    }

    public void setMin_payout_amount(double min_payout_amount)
    {
        this.min_payout_amount = min_payout_amount;
    }

    public String getIsTokenizationAllowed()
    {
        return isTokenizationAllowed;
    }

    public void setIsTokenizationAllowed(String isTokenizationAllowed)
    {
        this.isTokenizationAllowed = isTokenizationAllowed;
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

    public String getBinRouting()
    {
        return binRouting;
    }

    public void setBinRouting(String binRouting)
    {
        this.binRouting = binRouting;
    }

    public String getEmiSupport()
    {
        return emiSupport;
    }

    public void setEmiSupport(String emiSupport)
    {
        this.emiSupport = emiSupport;
    }
}
