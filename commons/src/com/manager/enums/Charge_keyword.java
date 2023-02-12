package com.manager.enums;

/**
 * Created by IntelliJ IDEA.
 * User: Admin
 * Date: 26/7/14
 * Time: 8:13 PM
 * To change this template use File | Settings | File Templates.
 */
public enum Charge_keyword
{
    AuthFailed("AuthFailed"),
    AuthStarted("AuthStarted"),
    CaptureSuccess("CaptureSuccess"),
    Settled("Settled"),
    Chargeback("Chargeback"),
    Reversed("Reversed"),
    Total("Total"),
    Wire("Wire"),
    Statement("Statement"),
    Setup("Setup"),
    TotalReserveGenerated("TotalReserveGenerated"),
    TotalReserveRefunded("TotalReserveRefunded"),
    GrossBalanceAmount("GrossBalanceAmount"),
    CalculatedReserveRefund("CalculatedReserveRefund"),
    VerifyOrder("VerifyOrder"),
    RefundAlert("RefundAlert"),
    RetrivalRequest("RetrivalRequest"),
    ServiceTax("ServiceTax"),
    NetProfit("NetProfit"),
    BankIntegration("BankIntegration"),
    SSLCertificate("SSLCertificate"),
    FraudulentTransaction("FraudulentTransaction"),
    NetFinalAmount("NetFinalAmount"),
    FraudAlert("FraudAlert"),
    Payout("Payout"),
    DomesticTotal("DomesticTotal"),
    InternationalTotal("InternationalTotal"),
    CaseFiling("CaseFiling"),
    TotalFees("TotalFees"),
    OtherCharges("OtherCharges");

    private String keyword;

    Charge_keyword(String keyword)
    {
     this.keyword=keyword;
    }

    public String toString()
    {
        return keyword;
    }
}
