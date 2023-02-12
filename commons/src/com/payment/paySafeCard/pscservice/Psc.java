/**
 * Psc.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package com.payment.paySafeCard.pscservice;

public interface Psc extends java.rmi.Remote {
    public com.payment.paySafeCard.pscservice.CreateDispositionReturn createDisposition(java.lang.String username, java.lang.String password, java.lang.String mtid, java.lang.String[] subId, double amount, java.lang.String currency, java.lang.String okUrl, java.lang.String nokUrl, java.lang.String merchantclientid, java.lang.String pnUrl, java.lang.String clientIp, com.payment.paySafeCard.pscservice.DispositionRestriction[] dispositionRestrictions, com.payment.paySafeCard.pscservice.MerchantClientData merchantClientData, java.lang.String shopId, java.lang.String shopLabel, java.lang.String[] customerDetail, java.lang.Boolean subscription) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.AssignCardToDispositionReturn assignCardToDisposition(java.lang.String username, java.lang.String password, java.lang.String mtid, java.lang.String[] subId, double amount, java.lang.String currency, java.lang.String pin, java.lang.String threatmetrixIdentifier) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.AssignCardsToDispositionReturn assignCardsToDisposition(java.lang.String username, java.lang.String password, java.lang.String mtid, java.lang.String[] subId, double amount, java.lang.String currency, java.lang.String locale, int acceptingTerms, com.payment.paySafeCard.pscservice.AssignCardsInformation[] cards, java.lang.String threatmetrixIdentifier) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.ModifyDispositionValueReturn modifyDispositionValue(java.lang.String username, java.lang.String password, java.lang.String mtid, java.lang.String[] subId, double amount, java.lang.String currency) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.GetSerialNumbersReturn getSerialNumbers(java.lang.String username, java.lang.String password, java.lang.String mtid, java.lang.String[] subId, java.lang.String currency, java.lang.String[] customerDetail) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.GetDispositionRawStateReturn getDispositionRawState(java.lang.String username, java.lang.String password, java.lang.String mtid, java.lang.String[] subId, java.lang.String currency) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.ExecuteDebitReturn executeDebit(java.lang.String username, java.lang.String password, java.lang.String mtid, java.lang.String[] subId, double amount, java.lang.String currency, int close, java.lang.String partialDebitId) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.ExecuteDirectDebitReturn executeDirectDebit(java.lang.String username, java.lang.String password, java.lang.String mtid, java.lang.String[] subId, double amount, java.lang.String currency, java.lang.String merchantclientid, java.lang.String pnUrl, com.payment.paySafeCard.pscservice.DispositionRestriction[] dispositionRestrictions, java.lang.String shopId, java.lang.String shopLabel, java.lang.String[] customerDetail, java.lang.Boolean validationOnly, com.payment.paySafeCard.pscservice.CustomerIdType customerIdType, java.lang.String customerId) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.GetCardInfoReturn getCardInfo(java.lang.String username, java.lang.String password, java.lang.String id, java.lang.String subId, java.lang.String pin) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.GetCardInfoActDateReturn getCardInfoActDate(java.lang.String username, java.lang.String password, java.lang.String id, java.lang.String subId, java.lang.String pin) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.GetBalanceReturn getBalance(java.lang.String username, java.lang.String password, java.lang.String id, java.lang.String subId, java.lang.String pin, java.lang.String dispositionCurrency) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.GetConvertedDispositionAmountReturn getConvertedDispositionAmount(java.lang.String username, java.lang.String password, java.lang.String mtid, java.lang.String[] subId, double cardAmount, java.lang.String cardCurrency, java.lang.String dispositionCurrency) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.ActivateCardReturn activateCard(java.lang.String username, java.lang.String password, java.lang.String id, java.lang.String subId, java.lang.String serial) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.CancelCardReturn cancelCard(java.lang.String username, java.lang.String password, java.lang.String id, java.lang.String subId, java.lang.String serial) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.DeactivateCardReturn deactivateCard(java.lang.String username, java.lang.String password, java.lang.String id, java.lang.String subId, java.lang.String serial) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.ExecutePaymentReturn executePayment(java.lang.String username, java.lang.String password, java.lang.String id, java.lang.String subId, java.lang.String pin, double amount, java.lang.String currency, java.lang.String merchantclientid, java.lang.String pnUrl, java.lang.String clientIp, java.lang.String threatmetrixIdentifier) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.ExecutePaymentActDateReturn executePaymentActDate(java.lang.String username, java.lang.String password, java.lang.String id, java.lang.String subId, java.lang.String pin, double amount, java.lang.String currency, java.lang.String merchantclientid, java.lang.String pnUrl, java.lang.String clientIp, java.lang.String threatmetrixIdentifier) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.ExecutePaymentACKReturn executePaymentACK(java.lang.String username, java.lang.String password, java.lang.String id, java.lang.String subId) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.CancelPaymentReturn cancelPayment(java.lang.String username, java.lang.String password, java.lang.String id, java.lang.String subId) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.GetMidReturn getMid(java.lang.String username, java.lang.String password, java.lang.String currency) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.OpenLoopGiftcardActivationReturn openLoopGiftcardActivation(java.lang.String username, java.lang.String password, int messageId, java.lang.String accountNumber, java.lang.String subId) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.OpenLoopGiftcardReversalReturn openLoopGiftcardReversal(java.lang.String username, java.lang.String password, int messageId, java.lang.String accountNumber, java.lang.String subId) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.LoadWalletReturn loadWallet(java.lang.String username, java.lang.String password, java.lang.String dtid, java.lang.String customerIdentificationNumber, com.payment.paySafeCard.pscservice.CustomerIdentificationType customerIdentificationType, double amount, java.lang.String product, java.lang.String terminalId) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.CancelLoadWalletReturn cancelLoadWallet(java.lang.String username, java.lang.String password, java.lang.String dtid, java.lang.String terminalId) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.ExecutePayoutReturn executePayout(java.lang.String username, java.lang.String password, java.lang.String merchantPayoutTransactionId, double amount, java.lang.String currency, java.lang.Long customerId, java.lang.String emailId, java.lang.String reportingCriteria, java.lang.String shopId, java.lang.String shopLabel, java.lang.String merchantclientid, java.lang.String clientIp, java.lang.String utcOffset) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.RefundRequestReturnType refund(java.lang.String username, java.lang.String password, java.lang.String rtid, java.lang.String mtid, double amount, java.lang.String currency, com.payment.paySafeCard.pscservice.CustomerIdType customerIdType, java.lang.String customerId, java.lang.String merchantClientId, java.lang.String clientIp, java.lang.Boolean validationOnly, java.lang.String shopId, java.lang.String shopLabel, java.lang.String utcOffset, java.lang.String subId, java.lang.String comment) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.PayoutReturn payout(java.lang.String username, java.lang.String password, java.lang.String ptid, double amount, java.lang.String currency, com.payment.paySafeCard.pscservice.CustomerIdType customerIdType, java.lang.String customerId, java.lang.String merchantClientId, java.lang.String clientIp, java.lang.Boolean validationOnly, java.lang.String shopId, java.lang.String shopLabel, java.lang.String utcOffset, java.lang.String subId, java.lang.String comment, com.payment.paySafeCard.pscservice.CustomerDetailsBasic customerDetailsBasic) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.GetPayoutStateReturn getPayoutState(java.lang.String username, java.lang.String password) throws java.rmi.RemoteException;
    public com.payment.paySafeCard.pscservice.PrecheckPayoutReturn precheckPayout(java.lang.String username, java.lang.String password, java.lang.String merchantPayoutTransactionId, double amount, java.lang.String currency, java.lang.Long customerId, java.lang.String emailId, java.lang.String reportingCriteria, java.lang.String shopId, java.lang.String shopLabel, java.lang.String merchantclientid, java.lang.String clientIp, java.lang.String utcOffset) throws java.rmi.RemoteException;
}
