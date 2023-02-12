package com.manager.vo.merchantmonitoring.enums;

/**
 * Created by admin on 5/11/16.
 */
public enum MonitoringKeyword
{
    AuthFailed("AuthFailed"),
    CaptureSuccess("CaptureSuccess"),
    Settled("Settled"),
    Chargeback("Chargeback"),
    Reversed("Reversed"),
    AuthSuccessful("AuthSuccessful"),
    Total("Total"),
    StuckTransaction("StuckTransaction"),
    FirstSubmission("FirstSubmission"),
    InactivityPeriod("InactivityPeriod"),
    SameAmountWithRound("SameAmountWithRound"),
    HighAmountOrder("HighAmountOrder"),
    SameAmountWithoutRound("SameAmountWithoutRound"),
    SameCardSameAmount("SameCardSameAmount"),
    SameCardSameAmountConsecutive("SameCardSameAmountConsecutive"),
    SameCardConsecutive("SameCardConsecutive"),
    MatureRefund("MatureRefund"),
    MatureChargeback("MatureChargeback"),
    BlockedCountry("BlockedCountry"),
    BlockedIP("BlockedIP"),
    BlockedEmail("BlockedEmail"),
    BlockedName("BlockedName"),
    BlockedCard("BlockedCard"),
    ForeignSales("ForeignSales"),
    ;

    private String monitoringKeyword;

    MonitoringKeyword(String monitoringKeyword)
    {
        this.monitoringKeyword = monitoringKeyword;
    }

    public String toString()
    {
        return monitoringKeyword;
    }
}
